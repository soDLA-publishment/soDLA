// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_prelu extends Module {
//    val IN_WIDTH = 32
//    val OP_WIDTH  = 32
//    val OUT_WIDTH = 64
//    val io = IO(new Bundle {
//         val cfg_prelu_en = Input(Bool())
//         val data_in = Input(SInt(IN_WIDTH.W))
//         val op_in = Input(SInt(OP_WIDTH.W))
//         val data_out =  Output(SInt(OUT_WIDTH.W))
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

//     val data_in_sign = io.data_in(IN_WIDTH-1)

//     when(io.cfg_prelu_en & !data_in_sign){
//         io.data_out := io.data_in
//     }
//     .otherwise{
//         io.data_out := io.data_in * io.op_in
//     }

// }