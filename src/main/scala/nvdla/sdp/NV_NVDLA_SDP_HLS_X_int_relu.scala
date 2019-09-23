package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_HLS_X_int_relu extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val trt_data_out = Flipped(DecoupledIO(UInt(32.W)))

        val relu_data_out = DecoupledIO(UInt(32.W))

        val cfg_relu_bypass = Input(Bool())

               
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

    val u_x_relu = Module(new NV_NVDLA_HLS_relu(32))
    u_x_relu.io.data_in := io.trt_data_out.bits
    val relu_out = u_x_relu.io.data_out

    val relu_dout = Mux(io.cfg_relu_bypass, io.trt_data_out.bits, relu_out)

    val pipe_p1 = Module(new NV_NVDLA_BC_pipe(32))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.trt_data_out.valid
    io.trt_data_out.ready := pipe_p1.io.ro
    pipe_p1.io.di := relu_dout
    io.relu_data_out.valid := pipe_p1.io.vo
    pipe_p1.io.ri := io.relu_data_out.ready
    io.relu_data_out.bits := pipe_p1.io.dout

}}


object NV_NVDLA_SDP_HLS_X_int_reluDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_X_int_relu)
}