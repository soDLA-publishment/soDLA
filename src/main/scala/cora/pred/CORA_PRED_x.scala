// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._
// import chisel3.iotesters.Driver

// //this module is to predict state


// class CORA_PRED_x(implicit val conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //input
//         val reg2dp_roundingMode = Input(UInt(3.W))
//         val reg2dp_detectTininess = Input(Bool())

//         val stat_x_actv_data = Input(Vec(4, UInt(conf.KF_BPE.W)))
//         val stat_x_actv_pvld = Input(Bool())

//         val mat_f_actv_data = Input(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
//         val mat_f_actv_pvld = Input(Bool())

//         val stat_u_actv_data = Input(Vec(4, UInt(conf.KF_BPE.W)))
//         val stat_u_actv_pvld = Input(Bool())

//         //output
//         val stat_x_data = Output(Vec(4, UInt(conf.KF_BPE.W)))
//         val stat_x_pvld = Output(Bool())

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
// //                                                      
// //           │                        ├─┐                  
// //           │                        ┌─┘              
// //           │                        │                            
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘                            
// //             │ ─┤ ─┤       │ ─┤ ─┤            
// //             └──┴──┘       └──┴──┘ 

//     //Hardware Reuse
//     //clock counter
//     val pre_x_st = io.stat_x_actv_pvld & io.mat_f_actv_pvld & io.stat_u_actv_pvld 

//     val pre_x_done = Wire(Bool())

//     val clk_cnt = RegInit(0.U)
//     clk_cnt := Mux(pre_x_st, 0.U,
//                Mux(pre_x_done, 0.U,
//                clk_cnt + 1.U))
    
//     pre_x_done := ( clk_cnt === (conf.V2V_MAC_LATENCY + conf.HARDFLOAT_MAC_LATENCY).U)
    
//     //instantiate m2m2m cells
//     val first_stage = (clk_cnt >= 0.U) & (clk_cnt <= ((conf.V2V_MAC_LATENCY-1).U))
//     val second_stage = (clk_cnt >= ((conf.V2V_MAC_LATENCY).U)) & (clk_cnt <= (conf.V2V_MAC_LATENCY + conf.HARDFLOAT_MAC_LATENCY-1).U)

//     val dout_first_stage = Reg(Vec(4, UInt((conf.KF_BPE).W)))
//     val dout_pvld_first_stage = RegInit(false.B)

//     val u_v2m = Module(new CORA_MATRIX_MUL_v2m)
//     val u_vadd = Module(new CORA_MATRIX_ADD_v2v)
//     //setup config
//     u_v2m.io.reg2dp_roundingMode := io.reg2dp_roundingMode
//     u_v2m.io.reg2dp_detectTininess := io.reg2dp_detectTininess   

//     u_vadd.io.reg2dp_roundingMode := io.reg2dp_roundingMode
//     u_vadd.io.reg2dp_detectTininess := io.reg2dp_detectTininess

//     when(first_stage){
//         //set up first stage
//         //v2m 
//         u_v2m.io.stat_actv_data := io.stat_x_actv_data
//         u_v2m.io.stat_actv_pvld := pre_x_st

//         u_v2m.io.tr_actv_data := io.mat_f_actv_data
//         u_v2m.io.tr_actv_pvld := pre_x_st

//         //v2v add
//         u_vadd.io.stat_a_actv_data := VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))
//         u_vadd.io.stat_a_actv_pvld := false.B

//         u_vadd.io.stat_b_actv_data := VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))
//         u_vadd.io.stat_b_actv_pvld := false.B
        
//     }
//     .elsewhen(second_stage){
//         //result from first stage
//         dout_first_stage := u_v2m.io.stat_out_data
//         dout_pvld_first_stage := u_v2m.io.stat_out_pvld

//         //v2m 
//         u_v2m.io.stat_actv_data := VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))
//         u_v2m.io.stat_actv_pvld := false.B

//         u_v2m.io.tr_actv_data := VecInit(Seq.fill(4)(VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))))
//         u_v2m.io.tr_actv_pvld := false.B

//         //v2v add
//         u_vadd.io.stat_a_actv_data := dout_first_stage
//         u_vadd.io.stat_a_actv_pvld := dout_pvld_first_stage

//         u_vadd.io.stat_b_actv_data := ShiftRegister(io.stat_u_actv_data, conf.V2V_MAC_LATENCY)
//         u_vadd.io.stat_b_actv_pvld := dout_pvld_first_stage
//     }
//     .otherwise{
//         //v2m zero
//         u_v2m.io.stat_actv_data := VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))
//         u_v2m.io.stat_actv_pvld := false.B

//         u_v2m.io.tr_actv_data := VecInit(Seq.fill(4)(VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))))
//         u_v2m.io.tr_actv_pvld := false.B

//         //v2v add
//         u_vadd.io.stat_a_actv_data := VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))
//         u_vadd.io.stat_a_actv_pvld := false.B

//         u_vadd.io.stat_b_actv_data := VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))
//         u_vadd.io.stat_b_actv_pvld := false.B
//     }


//     when(io.stat_x_pvld){
//         io.stat_x_data := u_vadd.io.stat_out_data
//     }
//     .otherwise{
//         io.stat_x_data := VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))
//     }
//     io.stat_x_pvld := ShiftRegister(pre_x_st, (conf.V2V_MAC_LATENCY + conf.HARDFLOAT_MAC_LATENCY), pre_x_st) &
//                       ShiftRegister(dout_pvld_first_stage, conf.HARDFLOAT_MAC_LATENCY, dout_pvld_first_stage) &
//                       u_vadd.io.stat_out_pvld

// }




// object CORA_PRED_xDriver extends App {
//   implicit val conf: matrixConfiguration = new matrixConfiguration
//   chisel3.Driver.execute(args, () => new CORA_PRED_x)
// }