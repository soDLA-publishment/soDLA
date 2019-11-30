// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._
// import chisel3.iotesters.Driver

// //this module is to prepare noise q


// class CORA_PRED_PRECOMPUTE_dt(implicit val conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //input
//         val timestamp = Input(UInt(64.W))  //in milisecond
//         val timestamp_pvld = Input(Bool())
//         val timestamp_prdy = Output(Bool())

//         //output
//         val deltatimestamp_out_data = Output(UInt(conf.KF_BPE.W))
//         val deltatimestamp_out_pvld = Output(Bool())
//         val deltatimestamp_out_prdy = Input(Bool())

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
//     val last_timestamp = Reg(UInt(64.W))
//     val current_timestamp = Reg(UInt(64.W))

//     val update_time_start = io.timestamp_pvld & io.deltatimstamp_out_prdy 
//     val current_timestamp_w = (io.timestamp/1000000.U) //millisecond to second 
    
//     update_time_start_d1 := update_time_start
//     when(update_time_start){
//         current_timestamp := current_timestamp_w
//         last_timestamp := current_timestamp
//     }

//     //The second stage, get the delta_t
//     val delta_timestamp = Reg(UInt(64.W))
//     when(update_time_start_d1){
//         delta_timestamp := current_timestamp - last_timestamp
//     }

//     //connect output
//     io.timestamp_prdy := io.deltatimestamp_out_prdy
//     io.deltatimestamp_out_pvld := update_time_start_d1
//     io.deltatimestamp_out_data := delta_timestamp

// }




// object CORA_PRED_PRECOMPUTE_dtDriver extends App {
//   implicit val conf: matrixConfiguration = new matrixConfiguration
//   chisel3.Driver.execute(args, () => new CORA_PRED_PRECOMPUTE_dt)
// }