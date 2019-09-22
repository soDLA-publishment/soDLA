package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_RDMA_ig(implicit val conf: cdpConfiguration) extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())
        val reg2dp_channel = Input(UInt(5.W))
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

        val cdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))

        val cdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None

        val cq_wr_pd = DecoupledIO(UInt(7.W))
    })

withClock(io.nvdla_core_clk){
////////////////////////////////////////////////////////////////////////////////////
//==============
// Work Processing
//==============
// one bubble between operation on two layers to let ARREG to switch to the next configration group
    val tran_vld = RegInit(false.B)
    val cmd_accept = Wire(Bool())
    val is_cube_end = Wire(Bool())

    val op_load = io.reg2dp_op_en & !tran_vld
    val op_done = cmd_accept & is_cube_end

    val after_op_done = RegInit(false.B)
    when(op_done){
        tran_vld := false.B
    }.elsewhen(after_op_done){
        tran_vld := false.B
    }.elsewhen(op_load){
        tran_vld := true.B
    }

    when(op_done){
        after_op_done := true.B
    }.elsewhen(io.eg2ig_done){
        after_op_done := false.B
    }

//==============
// Address catenate and offset calc
//==============
    val reg2dp_src_base_addr = Cat(io.reg2dp_src_base_addr_high,io.reg2dp_src_base_addr_low)
    val reg2dp_width_use = io.reg2dp_width +& true.B
//==============

//==============
// WIDTH Direction
// calculate how many atomic_m x8 blocks in width direction, also get the first and last block, which may be less than 8
//==============

    val number_of_total_trans_in_width = reg2dp_width_use(13,3) +& Cat(Fill(10, false.B), reg2dp_width_use(2,0).asUInt.orR)

//==============
// Positioning
//==============
    val width_count = RegInit(0.U(11.W))
    val is_first_w = (width_count === 0.U)

    val is_last_c = Wire(Bool())
    val is_last_w = Wire(Bool())
    val is_last_h = Wire(Bool())

    val is_chn_end = is_last_c
    val is_slice_end = is_last_w & is_last_c
    is_cube_end := is_last_w & is_last_h & is_last_c
//==============
// CHANNEL Count: with inital value of total number in C direction, and will count-- when moving in chn direction
//==============

    val channel_count = RegInit(0.U((13-log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)).W))
    when(cmd_accept){
        when(is_last_c){
            channel_count := 0.U
        }.otherwise{
            channel_count := channel_count + true.B
        }
    }

    is_last_c := (channel_count === io.reg2dp_channel(12, log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)))

    //  val is_last_c = (channel_count==number_of_block_in_channel-1);

//==============
// WID Count: with inital value of total number in W direction, and will count-- when moving in wid direction
//==============

    when(cmd_accept){
        when(is_slice_end){
            width_count := 0.U
        }.elsewhen(is_chn_end){
            width_count := width_count + true.B
        }
    }

    is_last_w := (width_count === (number_of_total_trans_in_width - 1.U))

//==============
// HEIGHT Count: move to next line after one wx1xc plane done
//==============

    val height_count = RegInit(0.U(13.W))
    when(cmd_accept){
        when(is_cube_end){
            height_count := 0.U
        }.elsewhen(is_slice_end){
            height_count := height_count + true.B
        }
    }

    is_last_h := (height_count === io.reg2dp_height)

//==========================================
// DMA: addr | size 
//==========================================

    val reg2dp_base_addr   = reg2dp_src_base_addr
    val reg2dp_line_stride = io.reg2dp_src_line_stride
    val reg2dp_surf_stride = io.reg2dp_src_surface_stride
//==============
// DMA Req : ADDR : Prepration
// DMA Req: go through the CUBE: W8->C->H
//==============
// Width: need be updated when move to next line 
// Trigger Condition: (is_last_c & is_last_w)

    val base_addr_w = RegInit(0.U(64.W))
    when(op_load){
        base_addr_w := reg2dp_base_addr
    }.elsewhen(cmd_accept){
        when(is_last_c & is_last_w){
            base_addr_w := base_addr_w +& reg2dp_line_stride
        }
    }

// base_Chn: need be updated when move to next w.group 
// Trigger Condition: (is_last_c)
//  1, jump to next line when is_last_w
//  2, jump to next w.group when !is_last_w
    val width_size = Wire(UInt(3.W))
    val width_size_use = width_size +& 1.U
    val base_addr_c = RegInit(0.U(64.W))
    when(op_load){
        base_addr_c := reg2dp_base_addr
    }.elsewhen(cmd_accept){
        when(is_last_c){
            when(is_last_w){
                base_addr_c := base_addr_w +& reg2dp_line_stride
            }.otherwise{
                base_addr_c := base_addr_c +& Cat(width_size_use, Fill(log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE), false.B))
            }
        }
    }

//==============
// DMA Req : ADDR : Generation
//==============
    val dma_req_addr = RegInit(0.U(64.W))
    when(op_load){
        dma_req_addr := reg2dp_base_addr
    }.elsewhen(cmd_accept){
        when(is_last_c){
            when(is_last_w){
                dma_req_addr := base_addr_w +& reg2dp_line_stride
            }.otherwise{
                dma_req_addr := base_addr_c +& Cat(width_size_use, Fill(log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE), false.B))
            }
        }.otherwise{
            dma_req_addr := dma_req_addr +& reg2dp_surf_stride
        }
    }

////==============
// DMA Req : SIZE : Prepration
//==============
// if there is only trans in total width, this one will be counted into the first trans, so is_first_w should take prior to is_last_w

    val size_of_32x1_in_first_block_in_width = Reg(UInt(3.W))
    when(number_of_total_trans_in_width===1.U){
        size_of_32x1_in_first_block_in_width := io.reg2dp_width(2, 0)
    }.otherwise{
        size_of_32x1_in_first_block_in_width := 7.U
    }

// when there is no L trans, still need calc the size for last trans which belongs to middle trans
// end_addr: 0 1 2 3 4 5 6 7 
// size    : 0 1 2 3 4 5 6 7 
    val size_of_32x1_in_last_block_in_width = io.reg2dp_width(2, 0)
//==============
// DMA Req : SIZE : Generation
//==============
    val req_size = Reg(UInt(3.W))
    when(is_first_w){
        req_size := size_of_32x1_in_first_block_in_width
    }.elsewhen(is_last_w){
        req_size := size_of_32x1_in_last_block_in_width
    }.otherwise{
        req_size := 7.U
    }

    width_size := req_size // 1~8
    val dma_req_size = Cat(Fill(12, false.B), req_size)

//==============
// Context Qeueu : Beats
//==============

//  val dma_req_align = (dma_req_addr[5]==0);
    val ig2eg_width = dma_req_size
    val ig2eg_align = false.B//dma_req_align;
    val ig2eg_last_w  = is_last_w
    val ig2eg_last_h  = is_last_h
    val ig2eg_last_c  = is_last_c

    val cq_rd_pd_cat = Cat(ig2eg_last_c, ig2eg_last_h, ig2eg_last_w, ig2eg_align, ig2eg_width(2,0))
    io.cq_wr_pd.bits := cq_rd_pd_cat
    val dma_rd_req_rdy = Wire(Bool())
    io.cq_wr_pd.valid := tran_vld & dma_rd_req_rdy

//==============
// DMA Req : PIPE
//==============
// VALID: clamp when when cq is not ready
    val dma_rd_req_vld = tran_vld & io.cq_wr_pd.ready

// PayLoad
    val dma_rd_req_pd = Cat(dma_req_size, dma_req_addr(conf.NVDLA_CDP_MEM_ADDR_BW-1, 0))

    val dma_rd_req_ram_type = io.reg2dp_src_ram_type

// Accept
    cmd_accept := dma_rd_req_vld & dma_rd_req_rdy

//==============
// reading stall counter before DMA_if
//==============
    val cnt_inc = true.B
    val cnt_clr = is_cube_end & cmd_accept
    val cnt_cen = (io.reg2dp_dma_en === 1.U ) & (dma_rd_req_vld & (~dma_rd_req_rdy))

    val cdp_rd_stall_count_dec = false.B

    // stl adv logic

    val stl_adv = cnt_inc ^ cdp_rd_stall_count_dec

    // stl cnt logic
    val stl_cnt_cur = RegInit(0.U(32.W))
    val stl_cnt_ext = Cat(false.B, false.B, stl_cnt_cur)
    val stl_cnt_inc = stl_cnt_cur +& true.B // spyglass disable W164b
    val stl_cnt_dec = stl_cnt_cur -& true.B // spyglass disable W164b
    val stl_cnt_mod = Mux(
            (cnt_inc && !cdp_rd_stall_count_dec), 
            stl_cnt_inc, 
            Mux(
                (!cnt_inc && cdp_rd_stall_count_dec), 
                stl_cnt_dec, 
                stl_cnt_ext
                ))
    val stl_cnt_new = Mux(stl_adv, stl_cnt_mod, stl_cnt_ext)
    val stl_cnt_nxt = Mux(cnt_clr, 0.U, stl_cnt_new)

    // stl flops

    when(cnt_cen){
        stl_cnt_cur := stl_cnt_nxt(31, 0)
    }

    // stl output logic

    val cdp_rd_stall_count = stl_cnt_cur

    val layer_flag = RegInit(false.B)
    when(cnt_clr===true.B){
        layer_flag := ~layer_flag
    }

    val dp2reg_d0_perf_read_stall_reg = RegInit(0.U(32.W))
    when((cnt_clr & (~layer_flag)) === true.B){
        dp2reg_d0_perf_read_stall_reg := cdp_rd_stall_count
    }
    io.dp2reg_d0_perf_read_stall := dp2reg_d0_perf_read_stall_reg

    val dp2reg_d1_perf_read_stall_reg = RegInit(0.U(32.W))
    when((cnt_clr & (~layer_flag)) === true.B){
        dp2reg_d1_perf_read_stall_reg := cdp_rd_stall_count
    }
    io.dp2reg_d1_perf_read_stall := dp2reg_d1_perf_read_stall_reg

//==============
// DMA Interface
//==============

    val nv_NVDLA_CDP_RDMA_rdreq = Module{new NV_NVDLA_DMAIF_rdreq(conf.NVDLA_DMA_RD_REQ)}
    nv_NVDLA_CDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
    nv_NVDLA_CDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.reg2dp_src_ram_type
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdp2cvif_rd_req_pd.get <> nv_NVDLA_CDP_RDMA_rdreq.io.cvif_rd_req_pd.get
    }
    io.cdp2mcif_rd_req_pd <> nv_NVDLA_CDP_RDMA_rdreq.io.mcif_rd_req_pd
    nv_NVDLA_CDP_RDMA_rdreq.io.dmaif_rd_req_pd.bits := dma_rd_req_pd
    nv_NVDLA_CDP_RDMA_rdreq.io.dmaif_rd_req_pd.valid := dma_rd_req_vld
    dma_rd_req_rdy := nv_NVDLA_CDP_RDMA_rdreq.io.dmaif_rd_req_pd.ready

////==============
////OBS signals
////==============
//  val obs_bus_cdp_rdma_proc_en = tran_vld;

//==============
//function point
//==============
// ...

//two continuous layers
    val mon_op_en_dly = RegInit(false.B)
    mon_op_en_dly := io.reg2dp_op_en

    val mon_op_en_pos = io.reg2dp_op_en & (~mon_op_en_dly)
    val mon_op_en_neg = (~io.reg2dp_op_en) & mon_op_en_dly

    val mon_layer_end_flg = RegInit(false.B)
    when(mon_op_en_neg){
        mon_layer_end_flg := true.B
    }.elsewhen(mon_op_en_pos){
        mon_layer_end_flg := false.B
    }

    val mon_gap_between_layers = RegInit(0.U(32.W))
    when(mon_layer_end_flg){
        mon_gap_between_layers := mon_gap_between_layers + true.B
    }.otherwise{
        mon_gap_between_layers := 0.U
    }

    //3 cycles means continuous layer

//==============
// Context Queue Interface
//==============

}}


object NV_NVDLA_CDP_RDMA_igDriver extends App {
    implicit val conf: cdpConfiguration = new cdpConfiguration
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_RDMA_ig())
}
