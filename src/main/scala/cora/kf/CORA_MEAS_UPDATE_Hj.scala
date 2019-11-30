// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._
// import chisel3.iotesters.Driver

// //this module is to prepare Jacobian

// class CORA_PRED_UPDATE_q(implicit val conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //input
//         val roundingMode = Input(UInt(3.W))
//         val detectTininess = Input(UInt(1.W))

//         val px = Input(UInt(conf.KF_BPE.W))
//         val py = Input(UInt(conf.KF_BPE.W))
//         val vx = Input(UInt(conf.KF_BPE.W))
//         val vy = Input(UInt(conf.KF_BPE.W))
        
//         val stat_pvld = Input(Bool())

//         //output
//         val q_out_data = Output(Vec(4, Vec(3, UInt(conf.KF_BPE.W))))
//         val q_out_pvld = Output(Bool())
     
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
// //       └───┐         ┌───┘                            0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  | 9  |
// //           │         │                               
// //           │         │                   
// //           │         │                                        
// //           │         └──────────────┐                                         
// //           │                        │   
// //                                                     
// //           │                        ├─┐                  
// //           │                        ┌─┘              
// //           │                        │                            
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘                            
// //             │ ─┤ ─┤       │ ─┤ ─┤            
// //             └──┴──┘       └──┴──┘ 
//     //==========================================================
//     // prepare px^2, py^2, px^2 + py^2, vxpy - vxpx, 
//     //==========================================================


//     //first stage
//     val u_p2 = Array.fill(4)(Module(new MulAddRecFNPipe()))

//     //setup config
//     for (i <- 0 to 3){
//         u_p2(i).io.roundingMode := io.roundingMode
//         u_p2(i).io.op := 0.U
//         u_p2(i).io.detectTininess := io.detectTininess   
//     }

//     //derive px2
//     u_p2(0).io.validin := io.stat_pvld
//     u_p2(0).io.a := io.px
//     u_p2(0).io.b := io.px
//     u_p2(0).io.c := "b0".asUInt(conf.KF_BPE.W)

//     val px2 = u_p2(0).io.out
//     val px2_pvld = u_p2(0).io.validout

//     //derive vxpy
//     u_p2(2).io.validin := io.stat_pvld
//     u_p2(2).io.a := io.vx
//     u_p2(2).io.b := io.py
//     u_p2(2).io.c := "b0".asUInt(conf.KF_BPE.W)

//     val vxpy = u_p2(2).io.out
//     val vxpy_pvld = u_p2(2).io.validout
    
//     //second stage

//     //derive py2 + px2
//     u_p2(1).io.validin := io.stat_pvld
//     u_p2(1).io.a := io.py
//     u_p2(1).io.b := io.py
//     u_p2(1).io.c := px2

//     val px2_add_py2 = u_p2(1).io.out
//     val px2_add_py2_pvld = u_p2(1).io.validout

//     //derive vxpy - vypx
//     u_p2(3).io.validin := vxpy_pvld 
//     u_p2(3).io.a := io.vy
//     u_p2(3).io.b := Cat(~io.px(32), io.px(31, 0))
//     u_p2(3).io.c := vxpy

//     val vxpy_minus_vypx = u_p2(3).io.out
//     val vxpy_minus_vypx_pvld = u_p2(3).io.validout

//     //third stage

//     //

//     //==========================================================
//     // prepare py(vxpy - vxpx), px(vypx - vxpy)
//     //==========================================================

//     val u_p3 = Array.fill(4)(Module(new MulAddRecFNPipe()))








//     //==========================================================
//     // get (px^2+py^2)^(-1/2), (px^2+py^2)^(-3/2)
//     //==========================================================
//     // get (px^2+py^2)^(-1/2)
//     val sq = Module (new DivSqrtRecFN_small(8, 24, 0))
    
//     //setup config
//     sq.io.roundingMode := io.roundingMode
//     sq.io.detectTininess := io.detectTininess   
//     sq.io.sqrtOp := true.B
//     sq.io.inValid := px2_add_py2_pvld

//     //derive (px^2+py^2)^(1/2)
//     sq.io.a := px2_add_py2
//     val px2_add_py2_sqrt = sq.io.out
//     val px2_add_py2_sqrt_pvld = sq.io.validout

//     val sq_3to2 = Module (new DivSqrtRecFN_small(8, 24, 0))
    
//     //setup config
//     sq.io.roundingMode := io.roundingMode
//     sq.io.detectTininess := io.detectTininess   
//     sq.io.sqrtOp := true.B
//     sq.io.inValid := px2_add_py2_pvld

//     // get (px^2+py^2)^(-3/2)

//     //derive (px^2+py^2)^(1/2)
//     sq.io.a := px2_add_py2
//     val px2_add_py2_sqrt = sq.io.out
//     val px2_add_py2_sqrt_pvld = sq.io.validout
    
//     //derive (px^2+py^2)^(-1/2)
//     val div = Module (new DivSqrtRecFN_small(8, 24, 0))
    
//     //setup config
//     div.io.roundingMode := io.roundingMode
//     div.io.detectTininess := io.detectTininess   
//     div.io.sqrtOp := false.B
//     div.io.inValid := px2_add_py2_sqrt_pvld

//     //derive 1/(px^2+py^2)^(1/2)
//     div.io.a := "b0_100000000_000000000000000000000000".asUInt(conf.KF_BPE.W)
//     div.io.b := px2_add_py2_sqrt
//     val one_over_px2_add_py2_sqrt = div.io.out
//     val one_over_px2_add_py2_sqrt_pvld = div.io.validout


//     //==========================================================
//     // get q matric
//     //==========================================================
//     val u_noise_a = Array.fill(8)(Module(new MulAddRecFNPipe()))

//     //setup config
//     for (i <- 0 to 7){
//         u_noise_a(i).io.roundingMode := io.roundingMode
//         u_noise_a(i).io.op := 0.U
//         u_noise_a(i).io.detectTininess := io.detectTininess   
//     }

//     //derive q11
//     u_noise_a(0).io.validin := dt4_div_4_pvld & io.noise_a_pvld
//     u_noise_a(0).io.a := dt4_div_4
//     u_noise_a(0).io.b := io.noise_ax
//     u_noise_a(0).io.c := "b0".asUInt(conf.KF_BPE.W)  

//     val q11 = u_noise_a(0).io.out
//     val q11_pvld = u_noise_a(0).io.validout

//     //derive q13
//     u_noise_a(1).io.validin := dt3_div_2_pvld & io.noise_a_pvld
//     u_noise_a(1).io.a := dt3_div_2
//     u_noise_a(1).io.b := io.noise_ax
//     u_noise_a(1).io.c := "b0".asUInt(conf.KF_BPE.W)  

//     val q13 = u_noise_a(1).io.out
//     val q13_pvld = u_noise_a(1).io.validout

//     //derive q22
//     u_noise_a(2).io.validin := dt4_div_4_pvld & io.noise_a_pvld
//     u_noise_a(2).io.a := dt4_div_4
//     u_noise_a(2).io.b := io.noise_ay
//     u_noise_a(2).io.c := "b0".asUInt(conf.KF_BPE.W)  

//     val q22 = u_noise_a(2).io.out
//     val q22_pvld = u_noise_a(2).io.validout

//     //derive q24
//     u_noise_a(3).io.validin := dt3_div_2_pvld & io.noise_a_pvld
//     u_noise_a(3).io.a := dt3_div_2
//     u_noise_a(3).io.b := io.noise_ay
//     u_noise_a(3).io.c := "b0".asUInt(conf.KF_BPE.W)  

//     val q24 = u_noise_a(3).io.out
//     val q24_pvld = u_noise_a(3).io.validout

//     //derive q31
//     u_noise_a(4).io.validin := dt3_div_2_pvld & io.noise_a_pvld
//     u_noise_a(4).io.a := dt3_div_2
//     u_noise_a(4).io.b := io.noise_ax
//     u_noise_a(4).io.c := "b0".asUInt(conf.KF_BPE.W)  

//     val q31 = u_noise_a(4).io.out
//     val q31_pvld = u_noise_a(4).io.validout

//     //derive q33
//     u_noise_a(5).io.validin := dt2_pvld & io.noise_a_pvld
//     u_noise_a(5).io.a := dt2
//     u_noise_a(5).io.b := io.noise_ax
//     u_noise_a(5).io.c := "b0".asUInt(conf.KF_BPE.W)  

//     val q33 = u_noise_a(5).io.out
//     val q33_pvld = u_noise_a(5).io.validout

//     //derive q42
//     u_noise_a(6).io.validin := dt3_div_2_pvld & io.noise_a_pvld
//     u_noise_a(6).io.a := dt3_div_2
//     u_noise_a(6).io.b := io.noise_ay
//     u_noise_a(6).io.c := "b0".asUInt(conf.KF_BPE.W)  

//     val q42 = u_noise_a(6).io.out
//     val q42_pvld = u_noise_a(6).io.validout

//     //derive q44
//     u_noise_a(7).io.validin := dt2_pvld & io.noise_a_pvld
//     u_noise_a(7).io.a := dt2
//     u_noise_a(7).io.b := io.noise_ay
//     u_noise_a(7).io.c := "b0".asUInt(conf.KF_BPE.W)  

//     val q44 = u_noise_a(7).io.out
//     val q44_pvld = u_noise_a(7).io.validout


//     io.q_out_data := VecInit(
//                      VecInit(q11, "b0".asUInt(conf.KF_BPE.W), q13, "b0".asUInt(conf.KF_BPE.W)),
//                      VecInit("b0".asUInt(conf.KF_BPE.W), q22, "b0".asUInt(conf.KF_BPE.W), q24),
//                      VecInit(q31, "b0".asUInt(conf.KF_BPE.W), q33, "b0".asUInt(conf.KF_BPE.W)),
//                      VecInit("b0".asUInt(conf.KF_BPE.W), q42, "b0".asUInt(conf.KF_BPE.W), q44)
//                     )
//     io.q_out_pvld := q11_pvld & q13_pvld & q22_pvld & q24_pvld &
//                      q31_pvld & q33_pvld & q42_pvld & q44_pvld 
// }


// object CORA_PRED_UPDATE_qDriver extends App {
//   implicit val conf: matrixConfiguration = new matrixConfiguration
//   chisel3.Driver.execute(args, () => new CORA_PRED_UPDATE_q)
// }