// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_X_int_alu extends Module {
//    val LUT_DEPTH = 256
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val cfg_mul_shift_value = Input(Bool())
//         val bypass_trt_in = Input(Bool())
//         val mul_data_out = Input(UInt(49.W))
//         val mul_out_pvld = Input(Bool())
//         val mul_out_prdy = Output(Bool())
//         val trt_data_out = Output(Bool())
//         val trt_out_pvld = Output(Bool())
//         val trt_out_prdy = Input(Bool())
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
// withClock(io.nv)
// }