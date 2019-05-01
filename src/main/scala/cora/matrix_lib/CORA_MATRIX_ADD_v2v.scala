// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._
// import chisel3.iotesters.Driver

// //A+B
// //need retiming
// class CORA_MATRIX_ADD_v2v(implicit val conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //input
//         val reg2dp_roundingMode = Input(UInt(3.W))
//         val reg2dp_detectTininess = Input(Bool())

//         val v2v_st = Input(Bool())
//         val v2v_done = Output(Bool())

//         val stat_a_actv_data = Input(Vec(4, UInt(conf.KF_BPE.W)))
//         val stat_a_actv_pvld = Input(Bool())

//         val stat_b_actv_data = Input(Vec(4, UInt(conf.KF_BPE.W)))
//         val stat_b_actv_pvld = Input(Bool())

//         //output
//         val stat_out_data = Output(Vec(4, UInt(conf.KF_BPE.W)))
//         val stat_out_pvld = Output(Bool())
       
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
// //           |                        |                     
// //           │                        ├─┐           
// //           │                        ┌─┘             
// //           │                        │                            
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘                            
// //             │ ─┤ ─┤       │ ─┤ ─┤            
// //             └──┴──┘       └──┴──┘ 


//     val v_add_st = io.stat_a_actv_pvld & io.stat_b_actv_pvld

//     //instance adders
//     val u_v_add = Array.fill(4){Module(new MulAddRecFNPipe())}
     
//     val out_valid = Wire(Vec(4, Bool()))
//     for(i<- 0 to 3){    
//         u_v_add(i).io.roundingMode := io.reg2dp_roundingMode
//         u_v_add(i).io.op := 0.U
//         u_v_add(i).io.detectTininess := io.reg2dp_detectTininess
//         u_v_add(i).io.validin := v_add_st
//         u_v_add(i).io.a := "b0_100000000_000000000000000000000000".asUInt(conf.KF_BPE.W) 
//         u_v_add(i).io.b := io.stat_a_actv_data(i)
//         u_v_add(i).io.c := io.stat_b_actv_data(i)
//         io.stat_out_data(i) := u_v_add(i).io.out
//         out_valid(i) := u_v_add(i).io.validout    
//     }

//     io.stat_out_pvld := out_valid.asUInt.andR

// }

// object CORA_MATRIX_ADD_v2vDriver extends App {
//   implicit val conf: matrixConfiguration = new matrixConfiguration
//   chisel3.Driver.execute(args, () => new CORA_MATRIX_ADD_v2v)
// }
