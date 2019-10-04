// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._
// import chisel3.iotesters.Driver

// //this module is to prepare noise q


// class CORA_PRED_UPDATE_q(implicit val conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //input
//         val roundingMode = Input(UInt(3.W))
//         val detectTininess = Input(UInt(1.W))

//         val noise_ax = Input(UInt(conf.KF_BPE.W))
//         val noise_ay = Input(UInt(conf.KF_BPE.W))
//         val noise_a_pvld = Input(Bool())
//         val noise_a_prdy = Output(Bool())

//         val timestamp = Input(UInt(64.W))  //in milisecond
//         val timestamp_pvld = Input(Bool())
//         val timestamp_prdy = Output(Bool())

//         //output
//         val q_out_data = Output(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
//         val q_out_pvld = Output(Bool())
//         val q_in_prdy = Input(Bool())

//     })

// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │                            need 10 pipes to finish
// //       │                 │
// //       └───┐         ┌───┘                            0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  | 9  |
// //           │         │                                1/2----->| dt_div2-->|
// //           │         │                   group_a      dt------>| dt_div2-->| dt2_div4->|
// //           │         │                                         |                                     -> noise >| q
// //           │         └──────────────┐                                              dt2 | dt4_div4->|
// //           │                        │    group_b      dt------>|           | dt3_div2->|
// //                                                      dt------>| dt2-------|
// //           │                        ├─┐                  
// //           │                        ┌─┘              
// //           │                        │                            
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘                            
// //             │ ─┤ ─┤       │ ─┤ ─┤            
// //             └──┴──┘       └──┴──┘ 

//     val exceptions_clear = RegInit(true.B)
//     val update_q_start = RegInit(false.B)
//     val last_timestamp = Reg(UInt(64.W))
//     val current_timestamp = Reg(UInt(64.W))
//     val delta_timestamp = Reg(UInt(64.W))
//     val roundingMode_d1 = Reg(UInt(3.W))
//     val detectTininess_d1 = Reg(Bool())
//     val noise_ax_d1 = Reg(UInt(conf.KF_BPE.W))
//     val noise_ay_d1 = Reg(UInt(conf.KF_BPE.W))
    
//     val update_q_start_w = io.noise_a_pvld & io.timestamp_pvld & io.q_in_prdy & exceptions_clear
//     val delta_timestamp_w = io.timestamp - current_timestamp

//     update_q_start := update_q_start_w
//     when(update_q_start_w){
//         current_timestamp := io.timestamp
//         last_timestamp := current_timestamp
//         delta_timestamp := delta_timestamp_w
//         roundingMode_d1 := io.roundingMode
//         detectTininess_d1 := io.detectTininess    
//     }
        
//     val delta_timestamp_rec_valid_d1 = RegInit(false.B)
//     val delta_timestamp_rec_d1 = Reg(UInt(conf.KF_BPE.W))
//     val delta_timestamp_expectionFlags_d1 = Reg(UInt(5.W))

//     val uINToRecFN = Module(new INToRecFN(64, 8, 24))

//     //setup config
//     uINToRecFN.io.roundingMode := io.roundingMode
//     uINToRecFN.io.detectTininess := io.detectTininess  
//     uINToRecFN.io.signedIn := false.B
//     uINToRecFN.io.in := delta_timestamp_w
    
//     val delta_timestamp_rec_valid = uINToRecFN.io.validout
//     val delta_timestamp_rec = uINToRecFN.io.recOut
//     val delta_timestamp_expectionFlags = uINToRecFN.io.exceptionFlags

//     delta_timestamp_rec_valid_d1 := delta_timestamp_rec_valid
//     when(delta_timestamp_rec_valid){  
//         delta_timestamp_rec_d1 := delta_timestamp_rec
//         delta_timestamp_expectionFlag_d1 := delta_timestamp_expectionFlags
//     }

//     //==========================================================
//     // derive dt/2, dt/4, 
//     //==========================================================

//     val u_dt_b = Module(new MulAddRecFNPipe())

//     //setup config
//     u_dt_b.io.roundingMode := roundingMode_d1
//     u_dt_b.io.op := 0.U
//     u_dt_b.io.detectTininess := detectTininess_d1   
    
//     //derive dt/2
//     u_dt_b.io.validin := delta_timestamp_rec_valid_w
//     u_dt_b.io.a := delta_timestamp_rec_w
//     u_dt_b.io.b := Mux("b0_0100000000_000000000000000000000000".asUInt(conf.KF_BPE.W)
//     u_dt_b.io.c := "b0".asUInt(conf.KF_BPE.W)

//     val dt_div_2_w = u_dt_b.io.out
//     val dt_div_2_pvld_w = u_dt_b.io.validout

//     val dt_div_2 = Reg(UInt(conf.KF_BPE.W))
//     val dt_div_2_pvld = RegInit(false.B)

//     when(delta_timestamp_rec_valid){
//         dt_div_2 := dt_div_2_w
//         dt_div_2_pvld := dt_div_2_pvld_w
//     }
    
//     //derive dt^2/4
//     u_dt_b(1).io.validin := dt_div_2_pvld
//     u_dt_b(1).io.a := dt_div_2
//     u_dt_b(1).io.b := dt_div_2
//     u_dt_b(1).io.c := "b0".asUInt(conf.KF_BPE.W)

//     val dt2_div_4 = u_dt_b(1).io.out
//     val dt2_div_4_pvld = u_dt_b(1).io.validout

//     //==========================================================
//     // derive  dt^2, dt^3/2, dt^4/4
//     //==========================================================

//     val u_dt_a = Array.fill(3)(Module(new MulAddRecFNPipe()))
    
//     //setup config
//     for (i <- 0 to 2){
//         u_dt_a(i).io.roundingMode := io.roundingMode
//         u_dt_a(i).io.op := 0.U
//         u_dt_a(i).io.detectTininess := io.detectTininess   
//     }

//     //derive dt^2
//     u_dt_a(0).io.validin := io.delta_timestamp_pvld
//     u_dt_a(0).io.a := io.delta_timestamp
//     u_dt_a(0).io.b := io.delta_timestamp
//     u_dt_a(0).io.c := "b0".asUInt(conf.KF_BPE.W)

//     val dt2 = u_dt_a(0).io.out
//     val dt2_pvld = u_dt_a(0).io.validout
    

//     //derive dt^3/2
//     u_dt_a(1).io.validin := dt2_pvld & dt_div_2_pvld
//     u_dt_a(1).io.a := dt2
//     u_dt_a(1).io.b := dt_div_2
//     u_dt_a(1).io.c := "b0".asUInt(conf.KF_BPE.W)

//     val dt3_div_2 = u_dt_a(1).io.out
//     val dt3_div_2_pvld = u_dt_a(1).io.validout

//     //derive dt^4
//     u_dt_a(2).io.validin := dt2_pvld & dt2_div_4_pvld
//     u_dt_a(2).io.a := dt2
//     u_dt_a(2).io.b := dt2_div_4
//     u_dt_a(2).io.c := "b0".asUInt(conf.KF_BPE.W)  

//     val dt4_div_4 = u_dt_a(2).io.out
//     val dt4_div_4_pvld = u_dt_a(2).io.validout

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