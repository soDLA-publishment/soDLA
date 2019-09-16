// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_X_int_relu extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val trt_out_pvld = Input(Bool())
//         val trt_out_prdy = Output(Bool())
//         val trt_data_out = Input(UInt(32.W))

//         val relu_out_pvld = Output(Bool())
//         val relu_out_prdy = Input(Bool())
//         val relu_data_out = Output(UInt(32.W))

//         val cfg_relu_bypass = Input(Bool())

               
//     })
//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │
//     //       │                 │
//     //       └───┐         ┌───┘
//     //           │         │
//     //           │         │
//     //           │         │
//     //           │         └──────────────┐
//     //           │                        │
//     //           │                        ├─┐
//     //           │                        ┌─┘    
//     //           │                        │
//     //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //             │ ─┤ ─┤       │ ─┤ ─┤         
//     //             └──┴──┘       └──┴──┘ 
// withClock(io.nvdla_core_clk){

//     val u_x_relu = Module(new NV_NVDLA_SDP_HLS_relu(32))
//     u_x_relu.io.data_in := io.trt_data_out
//     val relu_out = u_x_relu.io.data_out

//     val relu_dout = Mux(io.cfg_relu_bypass, io.trt_data_out, relu_out)

//     val pipe_p1 = Module(new NV_NVDLA_BC_pipe(1))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := io.trt_out_pvld
//     io.trt_out_prdy := pipe_p1.io.ro
//     pipe_p1.io.di := relu_dout
//     io.relu_out_pvld := pipe_p1.io.vo
//     pipe_p1.io.ri := io.relu_out_prdy
//     io.relu_data_out := pipe_p1.io.dout

// }}


// object NV_NVDLA_SDP_HLS_X_int_reluDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_X_int_relu)
// }