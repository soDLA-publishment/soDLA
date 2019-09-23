package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_HLS_X_int_trt extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())
        //in
        val mul_data_out = Flipped(DecoupledIO(UInt(49.W)))
        //out
        val trt_data_out = DecoupledIO(UInt(32.W))
        //config
        val cfg_mul_shift_value = Input(UInt(6.W))
        val bypass_trt_in = Input(Bool())
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

    val x_trt_shiftright_su = Module(new NV_NVDLA_HLS_shiftrightsu(49, 32, 6))
    x_trt_shiftright_su.io.data_in := Mux(io.bypass_trt_in, 0.U, io.mul_data_out.bits)
    x_trt_shiftright_su.io.shift_num := io.cfg_mul_shift_value
    val trt_data_final = x_trt_shiftright_su.io.data_out

    val trt_dout = Wire(UInt(32.W))
    when(io.bypass_trt_in){
        trt_dout := io.mul_data_out.bits //morework
    }
    .otherwise{
        trt_dout := trt_data_final
    }

    val pipe_p1 = Module(new NV_NVDLA_BC_pipe(32))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.mul_data_out.valid
    io.mul_data_out.ready := pipe_p1.io.ro
    pipe_p1.io.di := trt_dout
    io.trt_data_out.valid := pipe_p1.io.vo
    pipe_p1.io.ri := io.trt_data_out.ready
    io.trt_data_out.bits := pipe_p1.io.dout

}}

object NV_NVDLA_SDP_HLS_X_int_trtDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_X_int_trt)
}