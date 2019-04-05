// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_X_int_alu extends Module {
//    val LUT_DEPTH = 256
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val alu_data_out = Input(UInt(33.W))
//         val alu_out_pvld = Input(Bool())
//         val cfg_mul_bypass = Input(Bool())
//         val cfg_mul_op = Input(UInt(16.W))
//         val cfg_mul_prelu = Input(Bool())
//         val cfg_mul_src = Input(Bool())
//         val chn_mul_op = Input(UInt(16.W))
//         val mul_op_pvld = Input(Bool())
//         val mul_out_prdy = Input(Bool())
//         val nvdla_core_clk = Input(Bool())
//         val nvdla_core_rstn = Input(Bool())
//         val alu_out_prdy = Output(Bool())
//         val bypass_trt_out = Output(Bool())
//         val mul_data_out = Output(UInt(49.W))
//         val mul_op_prdy = Output(Bool())
//         val mul_out_pvld = Output(Bool())
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