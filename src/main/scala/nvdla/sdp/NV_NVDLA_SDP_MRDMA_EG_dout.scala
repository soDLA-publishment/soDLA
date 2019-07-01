package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_MRDMA_EG_dout(implicit val conf: sdpConfiguration) extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val op_load = Input(Bool())
        val eg_done = Output(Bool())

        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_width = Input(UInt(13.W))
        val reg2dp_in_precision = Input(UInt(2.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_perf_nan_inf_count_en = Input(Bool())
        val dp2reg_status_inf_input_num = Output(UInt(32.W))
        val dp2reg_status_nan_input_num = Output(UInt(32.W))

        val sdp_mrdma2cmux_valid = Output(Bool())
        val sdp_mrdma2cmux_ready = Input(Bool())
        val sdp_mrdma2cmux_pd = Output(UInt((conf.DP_DIN_DW + 2).W))

        val cmd2dat_dma_pvld = Input(Bool())
        val cmd2dat_dma_prdy = Output(Bool())
        val cmd2dat_dma_pd = Input(UInt(15.W))

        val pfifo0_rd_pvld = Input(Bool())
        val pfifo0_rd_prdy = Output(Bool())
        val pfifo0_rd_pd = Input(UInt(conf.AM_DW.W))

        val pfifo1_rd_pvld = Input(Bool())
        val pfifo1_rd_prdy = Output(Bool())
        val pfifo1_rd_pd = Input(UInt(conf.AM_DW.W))

        val pfifo2_rd_pvld = Input(Bool())
        val pfifo2_rd_prdy = Output(Bool())
        val pfifo2_rd_pd = Input(UInt(conf.AM_DW.W))

        val pfifo3_rd_pvld = Input(Bool())
        val pfifo3_rd_prdy = Output(Bool())
        val pfifo3_rd_pd = Input(UInt(conf.AM_DW.W))
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
// CFG
//==============

    val cfg_di_int8 = io.reg2dp_in_precision == false.B
    val cfg_di_int16 = io.reg2dp_in_precision == true.B
    val cfg_di_fp16 = io.reg2dp_in_precision == 2.U
    val cfg_di_16 = cfg_di_int16 | cfg_di_fp16
    val cfg_do_int8 = io.reg2dp_proc_precision == true.B
// #ifdef NVDLA_SDP_DATA_TYPE_INT16TO8
    val cfg_mode_1x1_pack = (io.reg2dp_width == false.B) & (io.reg2dp_height == false.B)
    val cfg_perf_nan_inf_count_en = io.reg2dp_perf_nan_inf_count_en

//pop command dat fifo //
    val dat_accept = Wire(Bool())
    val is_last_beat = Wire(Bool())
    val fifo_vld = Wire(Bool())
    val dat_rdy = Wire(Bool())

    io.cmd2dat_dma_prdy := dat_accept & is_last_beat & fifo_vld & dat_rdy

    val cmd2dat_dma_size = io.cmd2dat_dma_pd(13, 0)
    val cmd2dat_dma_cube_end  = io.cmd2dat_dma_pd(14)

    val size_of_beat = (Fill(14, io.cmd2dat_dma_pvld)) & cmd2dat_dma_size
    val cmd_cube_end = io.cmd2dat_dma_pvld & cmd2dat_dma_cube_end

    val beat_cnt = RegInit(0.U(14.W))

    when(dat_accept){
        when(is_last_beat){
            beat_cnt := 0.U
        }.otherwise{
            beat_cnt := beat_cnt + 1.U
        }
    }
    is_last_beat := (beat_cnt === size_of_beat)

    // #ifdef NVDLA_SDP_DATA_TYPE_INT16TO8
    // #else

    val pfifo0_sel = beat_cnt(1, 0) === 0.U
    val pfifo1_sel = beat_cnt(1, 0) === 1.U
    val pfifo2_sel = beat_cnt(1, 0) === 2.U
    val pfifo3_sel = beat_cnt(1, 0) === 3.U

    val pfifo_vld = (io.pfifo3_rd_pvld & pfifo3_sel) | 
                    (io.pfifo2_rd_pvld & pfifo2_sel) | 
                    (io.pfifo1_rd_pvld & pfifo1_sel) | 
                    (io.pfifo0_rd_pvld & pfifo0_sel)

    // #ifdef NVDLA_SDP_DATA_TYPE_INT16TO8
    // #else

    fifo_vld := pfifo_vld

    val dat_vld = fifo_vld

    io.pfifo0_rd_prdy := dat_rdy & pfifo0_sel
    io.pfifo1_rd_prdy := dat_rdy & pfifo1_sel
    io.pfifo2_rd_prdy := dat_rdy & pfifo2_sel
    io.pfifo3_rd_prdy := dat_rdy & pfifo3_sel

    val pfifo0_rd_data = (Fill(conf.AM_DW, pfifo0_sel)) & io.pfifo0_rd_pd
    val pfifo1_rd_data = (Fill(conf.AM_DW, pfifo1_sel)) & io.pfifo1_rd_pd
    val pfifo2_rd_data = (Fill(conf.AM_DW, pfifo2_sel)) & io.pfifo2_rd_pd
    val pfifo3_rd_data = (Fill(conf.AM_DW, pfifo3_sel)) & io.pfifo3_rd_pd

    // #ifdef NVDLA_FEATURE_DATA_TYPE_FP16

//=====PERF COUNT BEG=============
    // #ifdef NVDLA_FEATURE_DATA_TYPE_FP16
    // #else
    io.dp2reg_status_inf_input_num := 0.U
    io.dp2reg_status_nan_input_num := 0.U

    // #ifdef NVDLA_SDP_DATA_TYPE_INT16TO8

    // #ifdef NVDLA_SDP_DATA_TYPE_INT16TO8
    // #else
    val pfifo_data_r = Reg(UInt(conf.DP_DIN_DW.W))

    val pfifo_data0_16 = Wire(UInt(conf.DP_DIN_DW.W))
    val pfifo_data1_16 = Wire(UInt(conf.DP_DIN_DW.W))
    val pfifo_data2_16 = Wire(UInt(conf.DP_DIN_DW.W))
    val pfifo_data3_16 = Wire(UInt(conf.DP_DIN_DW.W))
    val pfifo_data0_8 = Wire(UInt(conf.DP_DIN_DW.W))
    val pfifo_data1_8 = Wire(UInt(conf.DP_DIN_DW.W))
    val pfifo_data2_8 = Wire(UInt(conf.DP_DIN_DW.W))
    val pfifo_data3_8 = Wire(UInt(conf.DP_DIN_DW.W))
    pfifo_data_r := MuxCase(
        Fill(conf.DP_DIN_DW, false.B),
        Array(
            (pfifo0_sel === true.B) -> Mux(cfg_di_16.asBool, pfifo_data0_16, pfifo_data0_8),
            (pfifo1_sel === true.B) -> Mux(cfg_di_16.asBool, pfifo_data1_16, pfifo_data1_8),
            (pfifo2_sel === true.B) -> Mux(cfg_di_16.asBool, pfifo_data2_16, pfifo_data2_8),
            (pfifo3_sel === true.B) -> Mux(cfg_di_16.asBool, pfifo_data3_16, pfifo_data3_8)
        )
    )
    val dat_data = pfifo_data_r

    dat_accept := dat_vld & dat_rdy
    val dat_layer_end = cmd_cube_end & is_last_beat
    val dat_batch_end = dat_layer_end

    val dat_pd = Cat(dat_layer_end, dat_batch_end, dat_data)

    val pipe_p1 = Module{new NV_NVDLA_BC_pipe(conf.DP_DIN_DW+2)}
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := dat_vld
    dat_rdy := pipe_p1.io.ro
    pipe_p1.io.di := dat_pd
    io.sdp_mrdma2cmux_valid := pipe_p1.io.vo
    pipe_p1.io.ri := io.sdp_mrdma2cmux_ready
    io.sdp_mrdma2cmux_pd := pipe_p1.io.dout

    val sdp_mrdma2cmux_layer_end = io.sdp_mrdma2cmux_pd(conf.DP_DIN_DW+1)
    io.eg_done = RegInit(false.B)
    io.eg_done := sdp_mrdma2cmux_layer_end & io.sdp_mrdma2cmux_valid & io.sdp_mrdma2cmux_ready

}
}

object NV_NVDLA_SDP_MRDMA_EG_doutDriver extends App {
    implicit val conf: sdpConfiguration = new sdpConfiguration
    chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_MRDMA_EG_dout())
}