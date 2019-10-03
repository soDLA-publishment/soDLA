package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class sdp_c_int_out_if extends Bundle{
    val data = Output(UInt(16.W))
    val sat = Output(Bool())
}

class sdp_c_int_cfg_if extends Bundle{
    val mode_eql = Output(Bool())
    val out_precision = Output(UInt(2.W))
    val offset = Output(UInt(32.W))
    val scale = Output(UInt(16.W))
    val truncate = Output(UInt(6.W))
}


class NV_NVDLA_SDP_HLS_C_int extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val cvt_in = Flipped(DecoupledIO(UInt(32.W)))
        val cvt_out = DecoupledIO(new sdp_c_int_out_if)

        val cfg = Flipped(new sdp_c_int_cfg_if)

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

    val cvt_data_mux = Mux(io.cfg.mode_eql, 0.U, io.cvt_in.bits)
    val cfg_offset_mux = Mux(io.cfg.mode_eql, 0.U, io.cfg.offset)
    val cfg_scale_mux = Mux(io.cfg.mode_eql, 0.U, io.cfg.scale)

    //sub
    val sub_dout = (cvt_data_mux.asSInt -& cfg_offset_mux.asSInt).asUInt
    val sub_in_pvld = Wire(Bool())
    val sub_out_prdy = Wire(Bool())
    val pipe_p1 = Module{new NV_NVDLA_BC_pipe(33)}
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := sub_in_pvld
    val sub_in_prdy = pipe_p1.io.ro
    pipe_p1.io.di := sub_dout
    val sub_out_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := sub_out_prdy
    val sub_data_out = pipe_p1.io.dout

    //mul 
    val mul_dout = (sub_data_out.asSInt * cfg_scale_mux.asSInt).asUInt
    val mul_out_prdy = Wire(Bool())
    val pipe_p2 = Module{new NV_NVDLA_BC_pipe(49)}
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := sub_out_pvld
    sub_out_prdy := pipe_p2.io.ro
    pipe_p2.io.di := mul_dout
    val mul_out_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := mul_out_prdy
    val mul_data_out = pipe_p2.io.dout

    //truncate
    val c_shiftrightsat_su = Module{new NV_NVDLA_HLS_shiftrightsatsu(49, 17, 6)}
    c_shiftrightsat_su.io.data_in := mul_data_out
    c_shiftrightsat_su.io.shift_num := io.cfg.truncate
    val tru_dout = c_shiftrightsat_su.io.data_out
    val sat_dout = c_shiftrightsat_su.io.sat_out

    //signed 
    //unsigned
    val pipe_p3_data_in = Cat(sat_dout, tru_dout)
    val tru_out_prdy = Wire(Bool())
    val pipe_p3 = Module{new NV_NVDLA_BC_pipe(18)}
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := mul_out_pvld
    mul_out_prdy := pipe_p3.io.ro
    pipe_p3.io.di := pipe_p3_data_in
    val tru_out_pvld = pipe_p3.io.vo
    pipe_p3.io.ri := tru_out_prdy
    val pipe_p3_data_out = pipe_p3.io.dout

    val tru_out = pipe_p3_data_out(16, 0)
    val sat_out = pipe_p3_data_out(17)

    val c_saturate_int16 = Module{new NV_NVDLA_HLS_saturate(17, 16)}
    c_saturate_int16.io.data_in := tru_out
    val dout_int16_sat = c_saturate_int16.io.data_out

    val c_saturate_int8 = Module{new NV_NVDLA_HLS_saturate(17, 8)}
    c_saturate_int8.io.data_in := tru_out
    val dout_int8_sat = c_saturate_int8.io.data_out

    val final_out_prdy = Wire(Bool())
    sub_in_pvld := Mux(io.cfg.mode_eql, false.B, io.cvt_in.valid)
    io.cvt_in.ready := Mux(io.cfg.mode_eql, final_out_prdy, sub_in_prdy)
    tru_out_prdy := Mux(io.cfg.mode_eql, true.B, final_out_prdy)
    val final_out_pvld = Mux(io.cfg.mode_eql, io.cvt_in.valid, tru_out_pvld)

    val cvt_dout = Mux(io.cfg.mode_eql, io.cvt_in.bits,
                   Mux(io.cfg.out_precision === 1.U, dout_int16_sat, 
                   Cat(Fill(8, dout_int8_sat(7)), dout_int8_sat)))
    val cvt_sat = Mux(io.cfg.mode_eql, false.B, sat_out)

    val pipe_p4_data_in = Cat(cvt_sat, cvt_dout)
    val pipe_p4 = Module{new NV_NVDLA_BC_pipe(17)}
    pipe_p4.io.clk := io.nvdla_core_clk
    pipe_p4.io.vi := final_out_pvld
    final_out_prdy := pipe_p4.io.ro
    pipe_p4.io.di := pipe_p4_data_in
    io.cvt_out.valid := pipe_p4.io.vo
    pipe_p4.io.ri := io.cvt_out.ready
    val pipe_p4_data_out = pipe_p4.io.dout

    io.cvt_out.bits.data := pipe_p4_data_out(15, 0)
    io.cvt_out.bits.sat := pipe_p4_data_out(16)

}}



object NV_NVDLA_SDP_HLS_C_intDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_C_int)
}