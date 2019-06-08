// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //Implementation overview of ping-pong register file.

// class fp16_4add extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())      

//         //input a, input b
//         val fp16_add_in_a = Input(UInt(68.W))
//         val fp16_add_in_b = Input(UInt(68.W))
//         val fp16_add_in_pvld = Input(Bool())
//         val fp16_add_in_prdy = Output(Bool())

//         //output
//         val fp16_add_out_dp = Output(UInt(68.W))
//         val fp16_add_out_pvld = Output(Bool())
//         val fp16_add_out_prdy = Input(Bool())
//     })
// //                             
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │              
// //       │       ───       │             
// //       │  ─┬┘       └┬─  │             
// //       │                 │                  
// //       │       ─┴─       │             
// //       │                 │                   
// //       └───┐         ┌───┘             
// //           │         │                  
// //           │         │                  
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 

// withClock(io.nvdla_core_clk){

// /////////////////////////////////////////////
//     val fp16_add_in_a_rdy = Wire(Vec(4, Bool()))
//     val fp16_add_in_b_rdy = Wire(Vec(4, Bool()))
//     val fp16_add_in_a_vld = Wire(Vec(4, Bool()))
//     val fp16_add_in_b_vld = Wire(Vec(4, Bool()))

//     val fp16_add_in_prdy = (fp16_add_in_a_rdy.asUInt.andR) & (fp16_add_in_b_rdy.asUInt.andR)
//     fp16_add_in_a_vld(0) := io.fp16_add_in_pvld & 
//                             fp16_add_in_b_rdy(0)&
//                             fp16_add_in_a_rdy(1)&fp16_add_in_b_rdy(1)&
//                             fp16_add_in_a_rdy(2)&fp16_add_in_b_rdy(2)&
//                             fp16_add_in_a_rdy(3)&fp16_add_in_b_rdy(3);
//     fp16_add_in_a_vld(1) := io.fp16_add_in_pvld & 
//                             fp16_add_in_a_rdy(0)&fp16_add_in_b_rdy(0)&
//                             fp16_add_in_b_rdy(1)&
//                             fp16_add_in_a_rdy(2)&fp16_add_in_b_rdy(2)&
//                             fp16_add_in_a_rdy(3)&fp16_add_in_b_rdy(3);
//     fp16_add_in_a_vld(2) := io.fp16_add_in_pvld & 
//                             fp16_add_in_a_rdy(0)&fp16_add_in_b_rdy(0)&
//                             fp16_add_in_a_rdy(1)&fp16_add_in_b_rdy(1)&
//                             fp16_add_in_b_rdy(2)&
//                             fp16_add_in_a_rdy(3)&fp16_add_in_b_rdy(3);
//     fp16_add_in_a_vld(3) := io.fp16_add_in_pvld & 
//                             fp16_add_in_a_rdy(0)&fp16_add_in_b_rdy(0)&
//                             fp16_add_in_a_rdy(1)&fp16_add_in_b_rdy(1)&
//                             fp16_add_in_a_rdy(2)&fp16_add_in_b_rdy(2)&
//                             fp16_add_in_b_rdy(3);

//     fp16_add_in_b_vld(0) := io.fp16_add_in_pvld & 
//                             fp16_add_in_a_rdy(0)&
//                             fp16_add_in_a_rdy(1)&fp16_add_in_b_rdy(1)&
//                             fp16_add_in_a_rdy(2)&fp16_add_in_b_rdy(2)&
//                             fp16_add_in_a_rdy(3)&fp16_add_in_b_rdy(3);
//     fp16_add_in_b_vld(1) := io.fp16_add_in_pvld & 
//                             fp16_add_in_a_rdy(0)&fp16_add_in_b_rdy(0)&
//                             fp16_add_in_a_rdy(1)&
//                             fp16_add_in_a_rdy(2)&fp16_add_in_b_rdy(2)&
//                             fp16_add_in_a_rdy(3)&fp16_add_in_b_rdy(3);
//     fp16_add_in_b_vld(2) := io.fp16_add_in_pvld & 
//                             fp16_add_in_a_rdy(0)&fp16_add_in_b_rdy(0)&
//                             fp16_add_in_a_rdy(1)&fp16_add_in_b_rdy(1)&
//                             fp16_add_in_a_rdy(2)&
//                             fp16_add_in_a_rdy(3)&fp16_add_in_b_rdy(3);
//     fp16_add_in_b_vld(3) := io.fp16_add_in_pvld & 
//                             fp16_add_in_a_rdy(0)&fp16_add_in_b_rdy(0)&
//                             fp16_add_in_a_rdy(1)&fp16_add_in_b_rdy(1)&
//                             fp16_add_in_a_rdy(2)&fp16_add_in_b_rdy(2)&
//                             fp16_add_in_a_rdy(3)





//     val u_HLS_fp17_pooling_add_0 = Module(new HLS_fp17_add)
//     u_HLS_fp17_pooling_add_0.io.nvdla_core_clk := io.nvdla_core_clk
//     u_HLS_fp17_pooling_add_0.io.chn_a_rsc_z := io.fp16_add_in_a(16, 0)
//     u_HLS_fp17_pooling_add_0.io.chn_a_rsc_vz := fp16_add_in_a_vld(0)


// }}


// object fp16_4add extends App {
//   chisel3.Driver.execute(args, () => new fp16_4add())
// }