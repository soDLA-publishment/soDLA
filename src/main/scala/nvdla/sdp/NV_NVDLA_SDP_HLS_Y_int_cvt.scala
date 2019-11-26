package nvdla

import chisel3._
import chisel3.util._

class sdp_y_int_cvt_cfg_if extends Bundle{
    val bypass = Output(Bool())
    val offset = Output(UInt(32.W))
    val scale = Output(UInt(16.W))
    val truncate = Output(UInt(6.W))
}

class NV_NVDLA_SDP_HLS_Y_int_cvt extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val cvt_data_in = Flipped(DecoupledIO(UInt(16.W)))
        val cvt_data_out = DecoupledIO(UInt(32.W))
    
        val cfg_cvt = Flipped(new sdp_y_int_cvt_cfg_if)
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

    //sub
    val cfg_scale = Mux(io.cfg_cvt.bypass, 0.U, io.cfg_cvt.scale)
    val cfg_truncate = Mux(io.cfg_cvt.bypass, 0.U, io.cfg_cvt.truncate)

    val cfg_offset_ext = Mux(io.cfg_cvt.bypass, 0.U, Cat(io.cfg_cvt.offset(31), io.cfg_cvt.offset))
    val cvt_data_ext = Mux(io.cfg_cvt.bypass, 0.U, Cat(Fill(17, io.cvt_data_in.bits(15)), io.cvt_data_in.bits))

    val sub_in_pvld = Wire(Bool())
    val sub_out_prdy = Wire(Bool())
    val sub_dout = (cvt_data_ext.asSInt - cfg_offset_ext.asSInt).asUInt

    val pipe_p1 = Module(new NV_NVDLA_BC_pipe(33))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := sub_in_pvld
    val sub_in_prdy = pipe_p1.io.ro
    pipe_p1.io.di := sub_dout
    val sub_out_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := sub_out_prdy
    val sub_data_out = pipe_p1.io.dout

    val mul_out_prdy = Wire(Bool())
    //mul 
    val mul_dout = (sub_data_out.asSInt*cfg_scale.asSInt).asUInt
    
    val pipe_p2 = Module{new NV_NVDLA_BC_pipe(49)}
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := sub_out_pvld
    sub_out_prdy := pipe_p2.io.ro
    pipe_p2.io.di := mul_dout
    val mul_out_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := mul_out_prdy
    val mul_data_out = pipe_p2.io.dout


    //truncate
    val y_mul_shiftright_su = Module{new NV_NVDLA_HLS_shiftrightsu(33 + 16, 32, 6)}
    y_mul_shiftright_su.io.data_in := mul_data_out
    y_mul_shiftright_su.io.shift_num := cfg_truncate
    val tru_dout = y_mul_shiftright_su.io.data_out

    //signed 
    //unsigned 
    val final_out_prdy = Wire(Bool())
    sub_in_pvld := Mux(io.cfg_cvt.bypass, false.B, io.cvt_data_in.valid)
    io.cvt_data_in.ready := Mux(io.cfg_cvt.bypass, final_out_prdy, sub_in_prdy) 
    mul_out_prdy := Mux(io.cfg_cvt.bypass, true.B, final_out_prdy)
    val final_out_pvld = Mux(io.cfg_cvt.bypass, io.cvt_data_in.valid, mul_out_pvld)
    val cvt_dout = Mux(io.cfg_cvt.bypass, Cat(Fill(16, io.cvt_data_in.bits(15)), io.cvt_data_in.bits), tru_dout)   

    val pipe_p3 = Module{new NV_NVDLA_BC_pipe(32)}
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := final_out_pvld
    final_out_prdy := pipe_p3.io.ro
    pipe_p3.io.di := cvt_dout
    io.cvt_data_out.valid := pipe_p3.io.vo
    pipe_p3.io.ri := io.cvt_data_out.ready
    io.cvt_data_out.bits := pipe_p3.io.dout
}}

