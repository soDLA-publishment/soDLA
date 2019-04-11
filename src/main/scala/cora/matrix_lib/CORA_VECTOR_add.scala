// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._

// //this module is to mac tr and stat

// class CORA_MATRIX_MUL_v2v_for_verify(implicit val conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //input
//         val roundingMode = Input(UInt(3.W))
//         val detectTininess = Input(UInt(1.W))

//         val a_actv_data = Input(Vec(4, conf.KF_TYPE(conf.KF_BPE.W)))
//         val a_actv_pvld = Input(Vec(4, Bool()))

//         val b_actv_data = Input(Vec(4, conf.KF_TYPE(conf.KF_BPE.W)))
//         val b_actv_pvld = Input(Vec(4, Bool()))

//         //output
//         val y_out_data = Output(Vec(4, conf.KF_TYPE(conf.KF_BPE.W)))
//         val y_out_pvld = Output(Vec(4, Bool()))
       
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

//     //==========================================================
//     // default mac cells
//     //==========================================================
    
//     //no setup config in sint

//     //
//     val sum_out = VecInit((0 to 3) map { i => (io.a_actv_data(i) + io.b_actv_data(i))}

//     //add retiming
//     val pp_pvld_d0 = io.a_actv_pvld(0)&io.b_actv_pvld(0)
//                      io.a_actv_pvld(1)&io.b_actv_pvld(1)&
//                      io.a_actv_pvld(2)&io.b_actv_pvld(2)&
//                      io.a_actv_pvld(3)&io.b_actv_pvld(3)
      
//     io.y_out_data := ShiftRegister(sum_out, conf.MATRIX_V2V_RETIMING, pp_pvld_d0)
//     io.y_out_pvld := ShiftRegister(pp_pvld_d0, conf.MATRIX_V2V_RETIMING, pp_pvld_d0)


// }