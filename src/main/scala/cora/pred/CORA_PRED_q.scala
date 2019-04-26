// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._
// import chisel3.iotesters.Driver

// //this module is to predict q

// class CORA_PRED_Q(implicit val conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //input
//         val reg2dp_roundingMode = Input(UInt(3.W))
//         val reg2dp_detectTininess = Input(Bool())
//         val reg2dp_noise_ax2 = Input(UInt(conf.KF_BPE.W))
//         val reg2dp_noise_ay2 = Input(UInt(conf.KF_BPE.W))
        
//         val dt_actv_data = Input(UInt(conf.KF_BPE.W))
//         val dt_actv_pvld = Input(Bool())

//         //output
//         val g_out_data = Output(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
//         val g_out_pvld = Output(Bool())

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

//     //use one mult pipe to prepare the matrix
//     //clock counter
//     val pred_q_st = io.dt_actv_pvld
//     val pre_p_done = Wire(Bool())

//     val clk_cnt = RegInit(0.U)
//     clk_cnt := Mux(pre_p_st, 0.U,
//                Mux(pre_p_done, 0.U,
//                clk_cnt + 1.U))
    
//     pre_p_done := ( clk_cnt === (2*conf.V2V_MAC_LATENCY + conf.HARDFLOAT_MAC_LATENCY).U)
    
//     //setup pipelines
//     val first_stage = (clk_cnt >= 0.U) & (clk_cnt <= ((2*conf.V2V_MAC_LATENCY-1).U))
//     val second_stage = (clk_cnt >= ((2*conf.V2V_MAC_LATENCY).U)) & (clk_cnt <= (2*conf.V2V_MAC_LATENCY + conf.HARDFLOAT_MAC_LATENCY-1).U)

//     val dout_first_stage = Reg(Vec(4, Vec(4, UInt((conf.KF_BPE).W))))
//     val dout_pvld_first_stage = RegInit(false.B)

//     //set up modules
//     val u_m2m2m = Module(new CORA_MATRIX_MUL_m2m2m)
//     val u_madd = Module(new CORA_MATRIX_ADD_m2m)
//     //setup config
//     u_m2m2m.io.reg2dp_roundingMode := io.reg2dp_roundingMode
//     u_m2m2m.io.reg2dp_detectTininess := io.reg2dp_detectTininess   

//     u_madd.io.reg2dp_roundingMode := io.reg2dp_roundingMode
//     u_madd.io.reg2dp_detectTininess := io.reg2dp_detectTininess  







// }

// //     