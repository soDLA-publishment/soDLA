// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //this module is to mac tr and stat

// class CORA_MATRIX_predictP(implicit conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //input
//         val f_actv_data = Input(Vec(4, Vec(4, conf.KF_TYPE(conf.KF_BPE.W))))
//         val f_actv_nz = Input(Vec(4, Vec(4, Bool())))
//         val f_actv_pvld = Input(Vec(4, Vec(4, Bool())))

//         val p_actv_data = Input(Vec(4, Vec(4, conf.KF_TYPE(conf.KF_BPE.W))))
//         val p_actv_nz = Input(Vec(4, Vec(4, Bool())))
//         val p_actv_pvld = Input(Vec(4, Vec(4, Bool())))

//         //output
//         val p_out_data = Input(Vec(4, Vec(4, conf.KF_TYPE(conf.KF_BPE.W))))
//         val p_out_nz = Input(Vec(4, Vec(4, Bool())))
//         val p_out_pvld = Input(Vec(4, Vec(4, Bool()))) 
       
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
//     //==========================================================
//     // calculate P*Ftranspose:  multiply        
//     //==========================================================
                
//     val mout = VecInit(Seq.fill(4)(VecInit(Seq.fill(4)(conf.KF_TYPE(0, (2*4).W)))))
//     val nz = VecInit(Seq.fill(4)(false.B))

//     for(i <- 0 to 4-1){
//         for(j <- 0 to 4-1){
//             when(io.tr_actv_pvld(i)(j)&io.tr_actv_nz(i)(j)&io.stat_actv_pvld(i)(j)&io.stat_actv_nz(j)){                       
//                 mout(i)(j) := io.tr_actv_data(i)(j)*io.stat_actv_data(j)
//                 nz(i) := true.B
//             }
//             .otherwise{
//                 mout(i)(j) := conf.KF_TYPE(0, conf.KF_RESULT_WIDTH)
//                 nz(i) := false.B
//             }
//     }} 

//     //==========================================================
//     // accumulate        
//     //==========================================================
//     val sum_out = VecInit(Seq.fill(4)(conf.KF_TYPE(0, conf.KF_RESULT_WIDTH)))
//     val pvld_out = VecInit(Seq.fill(4)(false.B))

//     for(i <- 0 to 4-1){
//         sum_out(i) := mout(i).reduce(_+&_)
//         pvld_out(i) := io.tr_actv_pvld(i)(0)*io.stat_actv_pvld(0)
        
//     }
    
//     //add retiming
  
//     for(i <- 0 to 4-1){
//         io.stat_out_data(i) := ShiftRegister(sum_out(i), conf.KF_OUT_RETIMING, pvld_out(i))
//         io.stat_out_nz(i) := ShiftRegister(nz(i), conf.KF_OUT_RETIMING, pvld_out(i)) 
//         io.stat_out_pvld(i) := ShiftRegister(pvld_out(i), conf.KF_OUT_RETIMING, pvld_out(i))
//     }

// }