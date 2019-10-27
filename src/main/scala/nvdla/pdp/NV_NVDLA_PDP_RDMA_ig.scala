package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//NV_NVDLA_PDP_RDMA_ig.v

class NV_NVDLA_PDP_RDMA_ig(implicit conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val pdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_REQ.W))
        val pdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_REQ.W))) else None
        val ig2cq_pd = DecoupledIO(UInt(18.W))

        val reg2dp_cube_in_channel = Input(UInt(13.W))
        val reg2dp_cube_in_height = Input(UInt(13.W))
        val reg2dp_cube_in_width = Input(UInt(13.W))
        val reg2dp_dma_en = Input(Bool())
        val reg2dp_kernel_stride_width = Input(UInt(4.W))
        val reg2dp_kernel_width = Input(UInt(4.W))
        val reg2dp_op_en = Input(Bool())
        val reg2dp_partial_width_in_first = Input(UInt(10.W))
        val reg2dp_partial_width_in_last = Input(UInt(10.W))
        val reg2dp_partial_width_in_mid = Input(UInt(10.W))
        val reg2dp_split_num = Input(UInt(8.W))
        val reg2dp_src_base_addr_high = Input(UInt(32.W))
        val reg2dp_src_base_addr_low = Input(UInt(32.W))
        val reg2dp_src_line_stride = Input(UInt(32.W))
        val reg2dp_src_ram_type = Input(Bool())
        val reg2dp_src_surface_stride = Input(UInt(32.W))
        val dp2reg_d0_perf_read_stall = Output(UInt(32.W))
        val dp2reg_d1_perf_read_stall = Output(UInt(32.W))
        val eg2ig_done = Input(Bool())

    })
    //     
    //          ┌─┐       ┌─┐
    //       ┌──┘ ┴───────┘ ┴──┐
    //       │                 │
    //       │       ───       │          
    //       │  ─┬┘       └┬─  │
    //       │                 │
    //       │       ─┴─       │
    //       │                 │
    //       └───┐         ┌───┘
    //           │         │
    //           │         │
    //           │         │
    //           │         └──────────────┐
    //           │                        │
    //           │                        ├─┐
    //           │                        ┌─┘    
    //           │                        │
    //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
    //             │ ─┤ ─┤       │ ─┤ ─┤         
    //             └──┴──┘       └──┴──┘ 
withClock(io.nvdla_core_clk){

    //==============
    // Work Processing
    //==============
    // one bubble between operation on two layers to let ARREG to switch to the next configration group
    val cmd_accept = Wire(Bool())
    val is_cube_end = Wire(Bool())
    val op_process = RegInit(false.B)
    val after_op_done = RegInit(false.B)

    val op_load = io.reg2dp_op_en & !op_process
    val op_done = cmd_accept & is_cube_end

    when(op_done){
        op_process := false.B
    }.elsewhen(after_op_done){
        op_process := false.B
    }.elsewhen(op_load){
        op_process := true.B
    }

    when(op_done){
        after_op_done := true.B
    }
    .elsewhen(io.eg2ig_done){
        after_op_done := false.B
    }

    //==============
    // Address catenate and offset calc
    //==============
    val reg2dp_src_base_addr = Cat(io.reg2dp_src_base_addr_high, io.reg2dp_src_base_addr_low)

    //==============
    // CFG:
    //==============
    val cfg_width = io.reg2dp_cube_in_width +& 1.U

    val cfg_fspt_width = io.reg2dp_partial_width_in_first
    val cfg_mspt_width = io.reg2dp_partial_width_in_mid
    val cfg_lspt_width = io.reg2dp_partial_width_in_last
    val cfg_fspt_width_use = io.reg2dp_partial_width_in_first +& 1.U
    val cfg_mspt_width_use = io.reg2dp_partial_width_in_mid +& 1.U
    val cfg_lspt_width_use = io.reg2dp_partial_width_in_last +& 1.U
    val cfg_mode_split = (io.reg2dp_split_num =/= 0.U) 
    val cfg_split_num = io.reg2dp_split_num +& 1.U

    //==============
    // CHANNEL Direction
    // calculate how many 32x8 blocks in channel direction
    //==============
    val number_of_block_in_c = io.reg2dp_cube_in_channel(12, log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE))

    //==============
    // WIDTH calculation
    // Always has FTRAN with size 0~7
    // then will LTRAN with size 0~7
    // then will have MTEAN with fixed size 7
    //==============
    val is_fspt = Wire(Bool())
    val is_lspt = Wire(Bool())
    val width_stride = Wire(UInt(14.W))
    when(cfg_mode_split){
        when(is_fspt){
            width_stride := cfg_fspt_width_use
        }.elsewhen(is_lspt){
            width_stride := cfg_lspt_width_use
        }.otherwise{
            width_stride := cfg_mspt_width_use
        }
    }
    .otherwise{
        width_stride := cfg_width
    }

    //==============
    // ENDing of line/surf/split/cube
    //==============
    val is_last_h = Wire(Bool())
    val is_last_c = Wire(Bool())
    val is_line_end = true.B    //is_last_w;
    val is_surf_end = is_line_end & is_last_h
    val is_split_end = is_surf_end & is_last_c
    is_cube_end := Mux(cfg_mode_split, is_split_end & is_lspt, is_split_end)

    //==============
    // WGROUP Count: width group: number of window after split-w. equal to 1 in non-split-w mode
    //==============
    val count_wg = RegInit("b0".asUInt(10.W))
    val wg_num = Wire(UInt(9.W))
    when(cmd_accept & is_split_end & cfg_mode_split){
        when(count_wg === (wg_num -& 1.U)){
            count_wg := 0.U
        }
        .otherwise{
            count_wg := count_wg + 1.U
        }
    }

    wg_num := Mux(cfg_mode_split, cfg_split_num, 1.U)
    is_fspt := cfg_mode_split & (count_wg === 0.U)
    is_lspt := cfg_mode_split & (count_wg === (wg_num -& 1.U))

    //==============
    // CHANNEL Count: with inital value of total number in C direction, and will count-- when moving in chn direction
    //==============
    val count_c = RegInit("b0".asUInt((13-conf.ATMMBW).W))

    when(cmd_accept){
        when(is_split_end){
            count_c := 0.U
        }
        .elsewhen(is_surf_end){
            count_c := count_c + 1.U
        }
    }

    is_last_c := (count_c === number_of_block_in_c)

    //==============
    // HEIGHT Count: move to next line after one line is done
    //==============
    val count_h = RegInit("b0".asUInt(13.W))

    when(op_load){
        count_h := 0.U
    }
    .elsewhen(cmd_accept){
        when(is_surf_end){
            count_h := 0.U
        }
        .elsewhen(is_line_end){
            count_h := count_h + 1.U
        }
    }
    
    is_last_h := (count_h === io.reg2dp_cube_in_height);

    //==========================================
    // DMA Req : ADDR
    //==========================================
    val reg2dp_base_addr = reg2dp_src_base_addr
    val reg2dp_line_stride = io.reg2dp_src_line_stride
    val reg2dp_surf_stride = io.reg2dp_src_surface_stride
    val reg2dp_esurf_stride = io.reg2dp_src_surface_stride

    //==============
    // DMA Req : ADDR : Prepration
    // DMA Req: go through the CUBE: W8->C->H
    //==============
    // ELEMENT
    val base_addr_width = RegInit("b0".asUInt(64.W))
    val base_addr_esurf = RegInit("b0".asUInt(64.W))
    val base_addr_line = RegInit("b0".asUInt(64.W))
    val base_addr_split = RegInit("b0".asUInt(64.W))
    val overlap = Wire(UInt(4.W))
    when(op_load){
        base_addr_width := reg2dp_base_addr
    }
    .elsewhen(cmd_accept){
        when(is_split_end & (~is_cube_end)){
            when(is_fspt){
                when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                    base_addr_width := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W)) +& Cat(overlap, "b0".asUInt((conf.ATMMBW).W)) 
                }
                .otherwise{
                    base_addr_width := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W)) -& Cat(overlap, "b0".asUInt((conf.ATMMBW).W)) 
                }
            }
            .otherwise{
                when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                    base_addr_width := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W))
                }
                .otherwise{
                    base_addr_width := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W))
                }
            }
        }
        .elsewhen(is_surf_end){
            base_addr_width := base_addr_esurf +& reg2dp_esurf_stride
        }
        .elsewhen(is_line_end){
            base_addr_width := base_addr_line +& reg2dp_line_stride
        }
    }

    // LINE
    when(op_load){
        base_addr_line := reg2dp_base_addr
    }
    .elsewhen(cmd_accept){
        when(is_split_end & (~is_cube_end)){
            when(is_fspt){
                when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                    base_addr_line := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W)) +& Cat(overlap, "b0".asUInt((conf.ATMMBW).W)) 
                }
                .otherwise{
                    base_addr_line := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W)) -& Cat(overlap, "b0".asUInt((conf.ATMMBW).W)) 
                }
            }
            .otherwise{
                when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                    base_addr_line := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W))
                }
                .otherwise{
                    base_addr_line := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W))
                }
            }
        }
        .elsewhen(is_surf_end){
            base_addr_line := base_addr_esurf +& reg2dp_esurf_stride
        }
        .elsewhen(is_line_end){
            base_addr_line := base_addr_line +& reg2dp_line_stride
        }
    }

    // SURF
    when(op_load){
        base_addr_esurf := reg2dp_base_addr
    }
    .elsewhen(cmd_accept){
        when(is_split_end & (~is_cube_end)){
            when(is_fspt){
                when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                    base_addr_esurf := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W)) +& Cat(overlap, "b0".asUInt((conf.ATMMBW).W)) 
                }
                .otherwise{
                    base_addr_esurf := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W)) -& Cat(overlap, "b0".asUInt((conf.ATMMBW).W)) 
                }
            }
            .otherwise{
                when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                    base_addr_esurf := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W))
                }
                .otherwise{
                    base_addr_esurf := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W))
                }
            }
        }
        .elsewhen(is_surf_end){
            base_addr_esurf := base_addr_esurf +& reg2dp_esurf_stride
        }
    }

    // SPLIT
    when(op_load){
        base_addr_split := reg2dp_base_addr
    }
    .elsewhen(cmd_accept){
        when(is_split_end & (~is_cube_end)){
            when(is_fspt){
                when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                    base_addr_split := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W)) +& Cat(overlap, "b0".asUInt((conf.ATMMBW).W)) 
                }
                .otherwise{
                    base_addr_split := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W)) -& Cat(overlap, "b0".asUInt((conf.ATMMBW).W)) 
                }
            }
            .otherwise{
                when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                    base_addr_split := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W))
                }
                .otherwise{
                    base_addr_split := base_addr_split +& Cat(width_stride, "b0".asUInt((conf.ATMMBW).W))
                }
            }
        }
    }

    val dma_req_addr = base_addr_width

    //==============
    // DMA Req : SIZE : Generation
    //==============
    val req_size = Wire(UInt(13.W))
    overlap := Mux(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width, io.reg2dp_kernel_stride_width - io.reg2dp_kernel_width, io.reg2dp_kernel_width - io.reg2dp_kernel_stride_width)
    when(cfg_mode_split){
        when(is_fspt){
            req_size := cfg_fspt_width
        }
        .elsewhen(is_lspt){
            when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                req_size := cfg_lspt_width -& overlap
            }.otherwise{
                req_size := cfg_lspt_width +& overlap
            }
        }
        .otherwise{
            when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
                req_size := cfg_mspt_width -& overlap
            }.otherwise{
                req_size := cfg_mspt_width +& overlap
            }
        }
    }
    .otherwise{
        req_size := io.reg2dp_cube_in_width
    }

    val dma_req_size = Cat("b0".asUInt(2.W), req_size)

    //==============
    // Context Qeueu : Beats
    //==============
    //{s,e}-> 11 10 01 00
    //     --------------
    //size | 
    // 0:  |  x  0  0  x
    // 1:  |  1  x  x  0
    // 2:  |  x  1  1  x
    // 3:  |  2  x  x  1
    // 4:  |  x  2  2  x
    // 5:  |  3  x  x  2
    // 6:  |  x  3  3  x
    // 7:  |  4  x  x  3

    // 64.size = ((32.size>>1) + &mask)
    // 64.cnt = 64.size + 1

    val ig2eg_size = dma_req_size
    val ig2eg_align = false.B   // can be elimnated after mcif update for re-alignment
    val ig2eg_line_end  = is_line_end;
    val ig2eg_surf_end  = is_surf_end;
    val ig2eg_split_end = is_split_end;
    val ig2eg_cube_end  = is_cube_end;

    // PKT_PACK_WIRE( pdp_rdma_ig2eg ,  ig2eg_ ,  ig2cq_pd )
    val dma_rd_req_rdy = Wire(Bool())
    io.ig2cq_pd.bits := Cat(ig2eg_cube_end, ig2eg_split_end, ig2eg_surf_end, ig2eg_line_end, ig2eg_align, ig2eg_size(12, 0))
    io.ig2cq_pd.valid := op_process & dma_rd_req_rdy

    //==============
    // DMA Req : PIPE
    //==============
    // VALID: clamp when when cq is not ready
    val dma_rd_req_vld = op_process & io.ig2cq_pd.ready

    // PayLoad
    val dma_rd_req_pd = Cat(dma_req_addr,  dma_req_size)
    val dma_rd_req_ram_type = io.reg2dp_src_ram_type;

    // Accept
    cmd_accept := dma_rd_req_vld & dma_rd_req_rdy;

    //==============
    // reading stall counter before DMA_if
    //==============
    val cnt_inc = true.B
    val pdp_rd_stall_count_dec = false.B
    val cnt_clr = cmd_accept & is_cube_end;
    val cnt_cen = (io.reg2dp_dma_en === true.B ) & (dma_rd_req_vld & (~dma_rd_req_rdy))

    val u_histo_rdma = Module{new NV_COUNTER_STAGE_histogram(32)}
    u_histo_rdma.io.clk := io.nvdla_core_clk
    u_histo_rdma.io.rd_stall_inc := cnt_inc
    u_histo_rdma.io.rd_stall_dec := pdp_rd_stall_count_dec
    u_histo_rdma.io.rd_stall_clr := cnt_clr
    u_histo_rdma.io.rd_stall_cen := cnt_cen
    val pdp_rd_stall_count = u_histo_rdma.io.cnt_cur

    val layer_flag = RegInit(false.B)
    when(cnt_clr){
        layer_flag := ~layer_flag
    }

    val dp2reg_d0_perf_read_stall_out = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_perf_read_stall_out = RegInit("b0".asUInt(32.W))

    when(cnt_clr & (~layer_flag)){
        dp2reg_d0_perf_read_stall_out := pdp_rd_stall_count
    }

    when(cnt_clr & layer_flag){
        dp2reg_d1_perf_read_stall_out := pdp_rd_stall_count
    }

    io.dp2reg_d0_perf_read_stall := dp2reg_d0_perf_read_stall_out
    io.dp2reg_d1_perf_read_stall := dp2reg_d1_perf_read_stall_out

    //==============
    // DMA Interface
    //==============
    val u_NV_NVDLA_PDP_RDMA_rdreq = Module(new NV_NVDLA_DMAIF_rdreq(conf.NVDLA_DMA_RD_REQ))
    u_NV_NVDLA_PDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
    io.pdp2cvif_rd_req_pd.get <> u_NV_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_pd.get
    }
    io.pdp2mcif_rd_req_pd <> u_NV_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_pd

    u_NV_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.valid := dma_rd_req_vld
    dma_rd_req_rdy := u_NV_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.ready
    u_NV_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.bits := dma_rd_req_pd

    u_NV_NVDLA_PDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.reg2dp_src_ram_type

}}


object NV_NVDLA_PDP_RDMA_igDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_ig)
}




