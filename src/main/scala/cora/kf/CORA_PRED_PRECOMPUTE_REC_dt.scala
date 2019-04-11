// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._
// import chisel3.iotesters.Driver

// //


// class CORA_PRED_PRECOMPUTE_REC_dt(implicit val conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //input
//         val timestamp = Input(UInt(64.W))  //long long uint, unit is us  
//         val timestamp_pvld = Input(Bool())
//         val timestamp_prdy = Output(Bool())

//         //output
//         val timestamp_rec = Output(UInt(33.W))  //rec32, unit is s
//         val timestamp_rec_pvld = Output(Bool())
//         val timestamp_rec_prdy = Input(Bool())

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
//     //In unit system, timestamp is millisecond. So in the first stage, generate timestamp into the unit of second.
//     val current_timestamp = Reg(UInt(32.W))
//     val update_time_start_d1 = RegInit(false.B)
//     val update_time_start = io.timestamp_pvld & io.deltatimstamp_out_prdy 
    
//     update_time_start_d1 := update_time_start
//     when(update_time_start){
//         current_timestamp := io.timestamp 
//         last_timestamp := current_timestamp
//     }

//     //connect output
//     io.timestamp_prdy := io.deltatimestamp_out_prdy
//     io.deltatimestamp_out_pvld := update_time_start_d1
//     io.deltatimestamp_out_data := delta_timestamp

//     uNToRecFN = Module(new INToRecFN(64, 8, 24))
//     uNToRecFN.io.signedIn := Bool(false.B)
//     uNToRecFN.io.in := io.timestamp
//     uNToRecFN.io.roundingMode   := io.roundingMode
//     uNToRecFN.io.detectTininess := io.detectTininess

//     val timestamp_rec_out = uNToRecFN.io.out
//     val timestamp_rec_out_exceptionFlags = uNToRecFN.io.exceptionFlags

//     val timestamp_rec_out_pvld = (~(timestamp_rec_out_exceptionFlags.andR)) & 
//                                  io.timestamp_pvld & 
//                                  io.deltatimstamp_out_prdy 

//     io.timestamp_rec_pvld := ShiftRegister(io.timestamp_pvld, conf.REC_DT_RETIMING, io.timestamp_pvld)
//     io.timestamp_rec := ShiftRegister(io.timestamp_pvld, conf.REC_DT_RETIMING, io.timestamp_pvld)
//     io.timestamp_rec

// }




// object CORA_PRED_PRECOMPUTE_REC_dtDriver extends App {
//   implicit val conf: matrixConfiguration = new matrixConfiguration
//   chisel3.Driver.execute(args, () => new CORA_PRED_PRECOMPUTE_REC_dt)
// }