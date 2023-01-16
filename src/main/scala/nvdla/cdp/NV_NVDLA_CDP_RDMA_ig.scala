package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_CDP_RDMA_ig(implicit conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val cdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
        val cdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))
                                 else None
        val cq_wr_pd = DecoupledIO(UInt(7.W))

        val reg2dp_channel = Input(UInt(13.W))
        val reg2dp_dma_en = Input(Bool())
        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_input_data = Input(UInt(2.W))
        val reg2dp_op_en = Input(Bool())
        val reg2dp_src_base_addr_high = Input(UInt(32.W))
        val reg2dp_src_base_addr_low = Input(UInt(32.W))
        val reg2dp_src_line_stride = Input(UInt(32.W))
        val reg2dp_src_ram_type = Input(Bool())
        val reg2dp_src_surface_stride = Input(UInt(32.W))
        val reg2dp_width = Input(UInt(13.W))
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

    ////////////////////////////////////////////////////////////////////////////////////
    //==============
    // Work Processing
    //==============
    // one bubble between operation on two layers to let ARREG to switch to the next configration group
    val cmd_accept = Wire(Bool())
    val is_cube_end = Wire(Bool())
    val tran_vld = RegInit(false.B)
    val after_op_done = RegInit(false.B)

    val op_load = io.reg2dp_op_en & ~tran_vld
    val op_done = cmd_accept & is_cube_end

    when(op_done){
        tran_vld := false.B
    }.elsewhen(after_op_done){
        tran_vld := false.B
    }
    .elsewhen(op_load){
        tran_vld := true.B
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
    val reg2dp_src_base_addr =  Cat(io.reg2dp_src_base_addr_high, io.reg2dp_src_base_addr_low)
    val reg2dp_width_use = io.reg2dp_width +& 1.U
    //==============

    //==============
    // WIDTH Direction
    // calculate how many atomic_m x8 blocks in width direction, also get the first and last block, which may be less than 8
    //==============
    val number_of_total_trans_in_width = reg2dp_width_use(13, 3) + reg2dp_width_use(2, 0).orR

    //==============
    // Positioning
    //==============
    val width_count = RegInit("b0".asUInt(11.W))
    val is_last_c = Wire(Bool())
    val is_last_w = Wire(Bool())
    val is_last_h = Wire(Bool())

    val is_first_w = (width_count === 0.U)
    val is_chn_end = is_last_c
    val is_slice_end = is_last_w & is_last_c
    is_cube_end := is_last_w & is_last_h & is_last_c
    
    //==============
    // CHANNEL Count: with inital value of total number in C direction, and will count-- when moving in chn direction
    //==============
    val channel_count = RegInit("b0".asUInt((13-conf.ATMMBW).W))

    when(cmd_accept){
        when(is_last_c){
            channel_count := 0.U
        }.otherwise{
            channel_count := channel_count + 1.U
        }
    }

    is_last_c := (channel_count === io.reg2dp_channel(12, conf.ATMMBW))

    //==============
    // WID Count: with inital value of total number in W direction, and will count-- when moving in wid direction
    //==============
    when(cmd_accept){
        when(is_slice_end){
            width_count := 0.U
        }.elsewhen(is_chn_end){
            width_count := width_count + 1.U
        }
    }

    is_last_w := (width_count === (number_of_total_trans_in_width -& 1.U))

    //==============
    // HEIGHT Count: move to next line after one wx1xc plane done
    //==============
    val height_count = RegInit(0.U(13.W))

    when(cmd_accept){
        when(is_cube_end){
            height_count := 0.U
        }.elsewhen(is_slice_end){
            height_count := height_count + 1.U
        }
    }

    is_last_h := (height_count === io.reg2dp_height)

    //==========================================
    // DMA: addr | size 
    //==========================================
    val reg2dp_base_addr = reg2dp_src_base_addr
    val reg2dp_line_stride = io.reg2dp_src_line_stride
    val reg2dp_surf_stride = io.reg2dp_src_surface_stride

    //==============
    // DMA Req : ADDR : Prepration
    // DMA Req: go through the CUBE: W8->C->H
    //==============
    // Width: need be updated when move to next line 
    // Trigger Condition: (is_last_c & is_last_w)
    val base_addr_w = RegInit("b0".asUInt(64.W))

    when(op_load){
        base_addr_w := reg2dp_base_addr
    }
    .elsewhen(cmd_accept){
        when(is_last_c && is_last_w){
            base_addr_w := base_addr_w + reg2dp_line_stride
        }
    }

    // base_Chn: need be updated when move to next w.group
    // Trigger Condition: (is_last_c)
    // 1, jump to next line when is_last_w
    // 2, jump to next w.group when !is_last_w
    val base_addr_c = RegInit("b0".asUInt(64.W))
    val width_size = Wire(UInt(3.W))
    val width_size_use = width_size +& 1.U

    when(op_load){
        base_addr_c := reg2dp_base_addr
    }.elsewhen(cmd_accept){
        when(is_last_c){
            when(is_last_w){
                base_addr_c := base_addr_w + reg2dp_line_stride
            }
            .otherwise{
                base_addr_c := base_addr_c + Cat(width_size_use, "b0".asUInt(conf.ATMMBW.W))
            }
        }
    }

    //==========================================
    // DMA Req : ADDR : Generation
    //==========================================

    val dma_req_addr = RegInit("b0".asUInt(64.W))
    when(op_load){
       dma_req_addr := reg2dp_base_addr
    }
    .elsewhen(cmd_accept){
        when(is_last_c){
            when(is_last_w){
                dma_req_addr := base_addr_w + reg2dp_line_stride
            }
            .otherwise{
                dma_req_addr := base_addr_c + Cat(width_size_use, "b0".asUInt(conf.ATMMBW.W))
            }
        }
        .otherwise{
            dma_req_addr := dma_req_addr + reg2dp_surf_stride
        }
    }

    
    ////==============
    // DMA Req : SIZE : Prepration
    //==============
    // if there is only trans in total width, this one will be counted into the first trans, so is_first_w should take prior to is_last_w
    val size_of_32x1_in_first_block_in_width = Wire(UInt(3.W))

    when(number_of_total_trans_in_width === 1.U){
        size_of_32x1_in_first_block_in_width := io.reg2dp_width
    }
    .otherwise{
        size_of_32x1_in_first_block_in_width := "b11".asUInt(2.W)
    }

    // when there is no L trans, still need calc the size for last trans which belongs to middle trans
    // end_addr: 0 1 2 3 4 5 6 7
    // size : 0 1 2 3 4 5 6 7
    val size_of_32x1_in_last_block_in_width = io.reg2dp_width

    //==========================================
    // DMA Req : SIZE : Generation
    //==========================================
    val req_size = Wire(UInt(3.W))

    when(is_first_w){
        req_size := size_of_32x1_in_first_block_in_width
    }
    .elsewhen(is_last_w){
        req_size := size_of_32x1_in_last_block_in_width
    }
    .otherwise{
        req_size := "d7".asUInt(3.W)
    }

    width_size := req_size  // 1~8
    val dma_req_size = Cat("b0".asUInt(12.W), req_size)

    //==============
    // Context Qeueu : Beats
    //==============
    val dma_rd_req_rdy = Wire(Bool())

    val ig2eg_width = dma_req_size
    val ig2eg_align = false.B
    val ig2eg_last_w = is_last_w;
    val ig2eg_last_h = is_last_h;
    val ig2eg_last_c = is_last_c;

    io.cq_wr_pd.bits := Cat(ig2eg_last_c, ig2eg_last_h, ig2eg_last_w, ig2eg_align, ig2eg_width(2, 0))
    io.cq_wr_pd.valid := tran_vld & dma_rd_req_rdy;

    //==============
    // DMA Req : PIPE
    //==============
    // VALID: clamp when when cq is not ready
    val dma_rd_req_vld = tran_vld & io.cq_wr_pd.ready
    // PayLoad
    val dma_rd_req_pd = Cat(dma_req_size, dma_req_addr(conf.NVDLA_CDP_MEM_ADDR_BW-1, 0))
    // Accept
    cmd_accept := dma_rd_req_vld & dma_rd_req_rdy

    //==============
    // reading stall counter before DMA_if
    //==============
    val cnt_clr = is_cube_end & cmd_accept

    val u_histo_rdma = Module{new NV_COUNTER_STAGE_histogram(32)}
    u_histo_rdma.io.clk := io.nvdla_core_clk
    u_histo_rdma.io.rd_stall_inc := true.B
    u_histo_rdma.io.rd_stall_dec := false.B
    u_histo_rdma.io.rd_stall_clr := cnt_clr
    u_histo_rdma.io.rd_stall_cen := (io.reg2dp_dma_en === 1.U) & (dma_rd_req_vld & (~dma_rd_req_rdy));
    val cdp_rd_stall_count = u_histo_rdma.io.cnt_cur

    val layer_flag = RegInit(false.B)
    when(cnt_clr){
        layer_flag := ~layer_flag
    }

    val dp2reg_d0_perf_read_stall_out = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_perf_read_stall_out = RegInit("b0".asUInt(32.W))

    when(cnt_clr & (~layer_flag)){
        dp2reg_d0_perf_read_stall_out := cdp_rd_stall_count
    }

    when(cnt_clr & layer_flag){
        dp2reg_d1_perf_read_stall_out := cdp_rd_stall_count
    }

    io.dp2reg_d0_perf_read_stall := dp2reg_d0_perf_read_stall_out
    io.dp2reg_d1_perf_read_stall := dp2reg_d1_perf_read_stall_out

    //==============
    // DMA Interface
    //==============
    val u_NV_NVDLA_CDP_RDMA_rdreq = Module(new NV_NVDLA_DMAIF_rdreq(conf.NVDLA_DMA_RD_REQ))
    u_NV_NVDLA_CDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
    io.cdp2cvif_rd_req_pd.get <> u_NV_NVDLA_CDP_RDMA_rdreq.io.cvif_rd_req_pd.get
    }
    io.cdp2mcif_rd_req_pd <> u_NV_NVDLA_CDP_RDMA_rdreq.io.mcif_rd_req_pd

    u_NV_NVDLA_CDP_RDMA_rdreq.io.dmaif_rd_req_pd.valid := dma_rd_req_vld
    dma_rd_req_rdy := u_NV_NVDLA_CDP_RDMA_rdreq.io.dmaif_rd_req_pd.ready
    u_NV_NVDLA_CDP_RDMA_rdreq.io.dmaif_rd_req_pd.bits := dma_rd_req_pd

    u_NV_NVDLA_CDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.reg2dp_src_ram_type

}}


object NV_NVDLA_CDP_RDMA_igDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_RDMA_ig)
}




