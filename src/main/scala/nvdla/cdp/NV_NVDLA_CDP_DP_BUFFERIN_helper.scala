package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_DP_BUFFERIN_unpack(implicit val conf: nvdlaConfig) extends Module{
    val io = IO(new Bundle {
        val nvdla_cdp_rdma2dp_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W))

        val dp_data = Output(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE).W))
        val is_pos_w = Output(UInt(4.W))
        val is_width = Output(UInt(4.W))
        val is_pos_c = Output(UInt(5.W))
        val is_b_sync = Output(Bool())
        val is_last_w = Output(Bool())
        val is_last_h = Output(Bool())
        val is_last_c = Output(Bool())
    })  

    io.dp_data := io.nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE-1, 0)
    val dp_pos_w = io.nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+3, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE)
    val dp_width = io.nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+7, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+4)
    val dp_pos_c = io.nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+12, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+8)
    val dp_b_sync = io.nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+13)
    val dp_last_w = io.nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+14)
    val dp_last_h = io.nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+15)
    val dp_last_c = io.nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+16)

    io.is_pos_w := dp_pos_w
    io.is_width := dp_width - 1.U
    io.is_pos_c := dp_pos_c
    io.is_b_sync := dp_b_sync
    io.is_last_w := dp_last_w
    io.is_last_h := dp_last_h
    io.is_last_c := dp_last_c
}


class NV_NVDLA_CDP_DP_BUFFERIN_two_cycle_delay(implicit val conf: nvdlaConfig) extends Module{
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())

        val load_din = Input(Bool())
        val stat_cur = Input(UInt(3.W))
        val first_c = Input(UInt(3.W))
        val normal_c = Input(UInt(3.W))
        val second_c = Input(UInt(3.W))
        val cube_end = Input(UInt(3.W))
        val rdma2dp_ready_normal = Input(Bool())
        val l2m_1stC_vld = Input(Bool())
        val data_shift_load_all = Input(Bool())

        val pos_w_align = Input(UInt(4.W))
        val width_align = Input(UInt(4.W))
        val pos_c_align = Input(UInt(5.W))
        val b_sync_align = Input(Bool())

        val last_w_align = Input(Bool())
        val last_h_align = Input(Bool())
        val last_c_align = Input(Bool())

        val more2less = Input(Bool())
        val less2more = Input(Bool())
        val hold_here = Input(Bool())

        val buffer_pos_w = Output(UInt(4.W))
        val buffer_width = Output(UInt(4.W))
        val buffer_pos_c = Output(UInt(5.W))
        val buffer_b_sync = Output(Bool())
        val buffer_last_w = Output(Bool())
        val buffer_last_h = Output(Bool())
        val buffer_last_c = Output(Bool())
    })  
withClock(io.nvdla_core_clk){

    val pos_w_dly1_reg = RegInit(0.U(4.W))
    val width_dly1_reg = RegInit(0.U(4.W))
    val pos_c_dly1_reg = RegInit(0.U(5.W))
    val b_sync_dly1_reg = RegInit(false.B)
    val last_w_dly1_reg = RegInit(false.B)
    val last_h_dly1_reg = RegInit(false.B)
    val last_c_dly1_reg = RegInit(false.B)

    when((((io.stat_cur===io.normal_c)||(io.stat_cur===io.second_c)) & io.load_din)
      || ((io.stat_cur===io.cube_end) & io.rdma2dp_ready_normal)){
        pos_w_dly1_reg  :=  io.pos_w_align
        width_dly1_reg  :=  io.width_align
        pos_c_dly1_reg  :=  io.pos_c_align
        b_sync_dly1_reg :=  io.b_sync_align
        last_w_dly1_reg :=  io.last_w_align
        last_h_dly1_reg :=  io.last_h_align
        last_c_dly1_reg :=  io.last_c_align
      }.elsewhen(io.stat_cur===io.first_c){
          when(io.more2less & io.rdma2dp_ready_normal){
              when(io.hold_here){
                pos_w_dly1_reg  :=  io.pos_w_align
                width_dly1_reg  :=  io.width_align
                pos_c_dly1_reg  :=  io.pos_c_align
                b_sync_dly1_reg :=  io.b_sync_align
                last_w_dly1_reg :=  io.last_w_align
                last_h_dly1_reg :=  io.last_h_align
                last_c_dly1_reg :=  io.last_c_align
              }.elsewhen(io.load_din){
                pos_w_dly1_reg  :=  io.pos_w_align
                width_dly1_reg  :=  io.width_align
                pos_c_dly1_reg  :=  io.pos_c_align
                b_sync_dly1_reg :=  io.b_sync_align
                last_w_dly1_reg :=  io.last_w_align
                last_h_dly1_reg :=  io.last_h_align
                last_c_dly1_reg :=  io.last_c_align
              }
          }.elsewhen(io.less2more){
              when(io.l2m_1stC_vld & io.load_din){
                pos_w_dly1_reg  :=  io.pos_w_align
                width_dly1_reg  :=  io.width_align
                pos_c_dly1_reg  :=  io.pos_c_align
                b_sync_dly1_reg :=  io.b_sync_align
                last_w_dly1_reg :=  io.last_w_align
                last_h_dly1_reg :=  io.last_h_align
                last_c_dly1_reg :=  io.last_c_align
              }.elsewhen(io.load_din){
                pos_w_dly1_reg  :=  io.pos_w_align
                width_dly1_reg  :=  io.width_align
                pos_c_dly1_reg  :=  io.pos_c_align
                b_sync_dly1_reg :=  io.b_sync_align
                last_w_dly1_reg :=  io.last_w_align
                last_h_dly1_reg :=  io.last_h_align
                last_c_dly1_reg :=  io.last_c_align
              }
          }
      }

    val buffer_pos_w_reg = RegInit(0.U(4.W))
    val buffer_width_reg = RegInit(0.U(4.W))
    val buffer_pos_c_reg = RegInit(0.U(5.W))
    val buffer_b_sync_reg = RegInit(false.B)
    val buffer_last_w_reg = RegInit(false.B)
    val buffer_last_h_reg = RegInit(false.B)
    val buffer_last_c_reg = RegInit(false.B)

    when(io.data_shift_load_all){
        buffer_pos_w_reg := pos_w_dly1_reg
        buffer_width_reg := width_dly1_reg
        buffer_pos_c_reg := pos_c_dly1_reg
        buffer_b_sync_reg := b_sync_dly1_reg
        buffer_last_w_reg := last_w_dly1_reg
        buffer_last_h_reg := last_h_dly1_reg
        buffer_last_c_reg := last_c_dly1_reg
    }

    io.buffer_pos_w := buffer_pos_w_reg 
    io.buffer_width := buffer_width_reg 
    io.buffer_pos_c := buffer_pos_c_reg 
    io.buffer_b_sync := buffer_b_sync_reg 
    io.buffer_last_w := buffer_last_w_reg 
    io.buffer_last_h := buffer_last_h_reg
    io.buffer_last_c := buffer_last_c_reg 
}}