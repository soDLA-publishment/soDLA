package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.iotesters.Driver

class NV_NVDLA_SDP_cmux(implicit val conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        //in clock
        val nvdla_core_clk = Input(Clock())

        //cacc2sdp
        val cacc2sdp_pd = Flipped(DecoupledIO(UInt((conf.DP_IN_DW+2).W)))
        //sdp_mrdma2cmux
        val sdp_mrdma2cmux_pd = Flipped(DecoupledIO(UInt((conf.DP_IN_DW+2).W)))
        //sdp_cmux2dp
        val sdp_cmux2dp_pd = DecoupledIO(UInt((conf.DP_IN_DW).W))

        //reg2dp
        val reg2dp_flying_mode = Input(Bool())
        val reg2dp_nan_to_zero = Input(Bool())
        val reg2dp_proc_precision = Input(UInt(2.W))
        val op_en_load = Input(Bool())
    }) 

withClock(io.nvdla_core_clk){

    //=======================
    // CFG
    val cfg_flying_mode_on = RegInit(false.B)
    val cfg_proc_precision = RegInit(false.B)
    val cfg_nan_to_zero = RegInit(false.B)

    cfg_flying_mode_on := io.reg2dp_flying_mode === 1.U
    cfg_proc_precision := io.reg2dp_proc_precision === 2.U
    cfg_nan_to_zero := io.reg2dp_nan_to_zero === 1.U

    val cfg_nan_to_zero_en = cfg_nan_to_zero & cfg_proc_precision

    val cmux_in_en = RegInit(false.B)
    val cacc_rdy = Wire(Bool())
    val cmux2dp_prdy = Wire(Bool())
    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.DP_IN_DW+2))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.cacc2sdp_pd.valid
    io.cacc2sdp_pd.ready := pipe_p1.io.ro
    pipe_p1.io.di := io.cacc2sdp_pd.bits
    val cacc_vld = pipe_p1.io.vo
    pipe_p1.io.ri := cacc_rdy
    val cacc_pd = pipe_p1.io.dout

    val cmux2dp_pvld = cmux_in_en & Mux(cfg_flying_mode_on, cacc_vld, io.sdp_mrdma2cmux_pd.valid)
    cacc_rdy := cmux_in_en & cfg_flying_mode_on & cmux2dp_prdy
    io.sdp_mrdma2cmux_pd.ready := cmux_in_en & (!cfg_flying_mode_on) & cmux2dp_prdy

    //===========================================
    // Layer Switch
    //===========================================
    val cmux_pd = Mux(cfg_flying_mode_on, cacc_pd, io.sdp_mrdma2cmux_pd.bits)

    val cmux_pd_batch_end = cmux_pd(conf.DP_IN_DW)
    val cmux_pd_layer_end = cmux_pd(conf.DP_IN_DW+1)
    val cmux_pd_flush_batch_end_NC = cmux_pd_batch_end

    val cmux2dp_pd = cmux_pd(conf.DP_IN_DW-1, 0)

    val pipe_p2 = Module(new NV_NVDLA_IS_pipe(conf.DP_IN_DW))
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := cmux2dp_pvld
    cmux2dp_prdy := pipe_p2.io.ro
    pipe_p2.io.di := cmux2dp_pd
    io.sdp_cmux2dp_pd.valid := pipe_p2.io.vo
    pipe_p2.io.ri := io.sdp_cmux2dp_pd.ready
    io.sdp_cmux2dp_pd.bits := pipe_p2.io.dout

}}


object NV_NVDLA_SDP_cmuxDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_cmux())
}


