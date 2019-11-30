// package cora

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._
// import chisel3.iotesters.Driver

// //this module is to prepare (dt_4/4, dt_3/2, dt_2)


// class CORA_PRED_PRECOMPUTE_q(implicit val conf: matrixConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //reg2dp
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_noise_ax = Input(UInt(conf.KF_BPE.W)) 
//         val reg2dp_noise_ay = Input(UInt(conf.KF_BPE.W)) 
//         val reg2dp_roundingMode = Input(UInt(3.W))
//         val reg2dp_detectTininess = Input(Bool())

//         //status controller
//         val status_fsm_switch = Input(Bool())
//         val status_is_initialized = Input(Bool())

//         //input
//         val timestamp = Input(UInt(64.W))  //request time
//         val timestamp_pvld = Input(Bool())  
//         val timestamp_prdy = Output(Bool()) 

//         //output
//         val noise_q_data = Output(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))   //(dt_4/4, dt_3/2, dt_2)
//         val noise_q_pvld = Output(Bool())
//         val noise_q_prdy = Input(Bool())

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
//     ////////////////////////////////////////////////////////////////////////
//     // Prediction Step Precompute Q FSM                                   //
//     ////////////////////////////////////////////////////////////////////////
//     val pq_en = io.reg2dp_op_en
//     val need_pending = Wire(Bool())
//     val pq_done = Wire(Bool())
//     val pending_req_end = Wire(Bool())

//     val sIdle :: sPend :: sBusy :: sDone :: Nil = Enum(4)
//     val cur_state = RegInit(sIdle)
//     val nxt_state = WireInit(sIdle)

//     switch (cur_state) {
//         is (sIdle) {
//             when(pq_en){
//                 when(need_pending){ nxt_state := sPend }
//                 .otherwise{ nxt_state := sBusy }
//         }
//         is (sPend) {
//             when(pending_req_end){ nxt_state := sBusy }
//         }
//         is (sBusy) {
//             when (pq_done) { nxt_state := sDone }
//         }
//         is (sDone) {
//             when (io.status_fsm_switch) { nxt_state := sIdle }
//         }
//     }
//     cur_state := nxt_state
//     ////////////////////////////////////////////////////////////////////////
//     //  FSM input signals                                                 //
//     ////////////////////////////////////////////////////////////////////////
//     val is_running = Wire(Bool())

//     need_pending := ~io.noise_q_prdy & pq_done

//     pending_req_end := pq_done & 

//     ////////////////////////////////////////////////////////////////////////
//     //  FSM output signals                                                //
//     ////////////////////////////////////////////////////////////////////////  
//     val timestamp_prdy_out = RegInit(false.B)
//     val is_idle = Wire(Bool())
//     val pq_st = pq_en & is_idle
//     is_idle := (cur_state === sIdle)
//     val is_pending = (cur_state === sPend)
//     is_running := (cur_state === sBusy)
//     val is_done = (cur_state === sDone)
//     val is_nxt_running = (nxt_state === sBusy)
//     val is_first_running = ~is_running & is_nxt_running
//     val pq2status_state_w = nxt_state

//     io.pq2status_state := RegNext(pq2status_state_w, false.B)

//     when(io.reg2dp_op_en){
//         when(is_idle){
//             timestamp_prdy_out := true.B
//         }
//         .otherwise{
//             timestamp_prdy_out := false.B
//         } 
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //  registers to keep last q status                                    //
//     ////////////////////////////////////////////////////////////////////////
//     val pending_end = RegInit(false.B)
//     pending_end := io.noise_q_prdy

//     when()

//     ////////////////////////////////////////////////////////////////////////
//     //  SLCG control signal                                               //
//     ////////////////////////////////////////////////////////////////////////
//     val slcg_int2fp_en_w =
//     val slcg_dt_en_w = 


//     ////////////////////////////////////////////////////////////////////////
//     //  registers to calculate local values                               //
//     ////////////////////////////////////////////////////////////////////////
//     when(~is_running){
//         is_blocking := false.B
//     }
//     .otherwise{
//         is_blocking := ~is_blocking
//     }
//     // rd Channel: Response
//     when(timestamp_pvld)





//     ////////////////////////////////////////////////////////////////////////
//     //  generate controls for input data                                  //
//     ////////////////////////////////////////////////////////////////////////
//     //start signal
//     val dt_start = RegInit(false)
//     val dt_start = Reg(Vec(3, UInt(conf.KF_BPE.W)))
//     val is_running = Wire(Bool())

//     dt_pvld_d1 := io.dt_pvld & ~is_running

//     when(io.dt_pvld & ~is_running){
//         is_idle := 
//     }
    
//     //counter
//     val dt2_cnt = RegInit(0.U)
//     val dt2_cnt_w = Mux(dt2_st,  0.U, 
//                     Mux(dt2_end, 0.U,
//                      dt2_cnt + 1.U))
//     dt2_end := (dt2_cnt === ((conf.CORA_MAC_LATENCY)*2).U)
//     dt2_cnt := dt2_cnt_w 

//     //one pipe to send config
//     val roundingMode_d1 = Reg(UInt(2.W))
//     val detectTininess_d1 = Reg(Bool())

//     when(io.dt_pvld){
//         roundingMode_d1 := io.roundingMode
//         detectTininess_d1 := io.detectTininess
//     }

//     //In unit system, timestamp is millisecond. So in the first stage, generate timestamp into the unit of second.
//     val current_timestamp = Reg(UInt(32.W))
//     val update_time_start_d1 = RegInit(false.B)
//     val update_time_start = io.timestamp_pvld & io.deltatimstamp_out_prdy 
    
//     update_time_start_d1 := update_time_start
//     when(is_running){
//         current_timestamp := io.timestamp 
//         last_timestamp := current_timestamp
//     }

//     //connect output

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


//     //MulAddRecFNPipe from https://github.com/freechipsproject/rocket-chip/blob/master/src/main/scala/tile/FPU.scala
//     //two operations per time
//     val u_dt2 = Array(2).fill(Module(new MulAddRecFNPipe()))
    
//     //setup config
//     for (i <- 0 to 1){
//         u_dt(i).io.roundingMode := io.roundingMode
//         u_dt(i).io.op := 0.U
//         u_dt(i).io.detectTininess := io.detectTininess
//     }

//     //derive dt2
//     u_dt.io.validin := cnt == 0.U
//     u_dt.io.a := delta_timestamp_rec_w
//     u_dt.io.b := 
//     u_dt.io.c := "b0".asUInt(conf.KF_BPE.W)

//     val dt_div_2_w = u_dt_b.io.out
//     val dt_div_2_pvld_w = u_dt_b.io.validout

//     //The second stage, get the delta_t
//     val update_time_start_d2  = RegInit(false.B)
//     val delta_timestamp = Reg(UInt(32.W))

//     update_time_start_d2 := update_time_start_d1
//     when(update_time_start_d1){
//         delta_timestamp := current_timestamp - last_timestamp
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //  registers to keep last layer status                               //
//     ////////////////////////////////////////////////////////////////////////

//     //The third stage, get the delta_t^2
//     val update_time_start_d3 = RegInit(false.B)
//     when

//     //connect output
//     io.timestamp_prdy := io.deltatimestamp_out_prdy
//     io.deltatimestamp_out_pvld := update_time_start_d1
//     io.deltatimestamp_out_data := delta_timestamp

//     ////////////////////////////////////////////////////////////////////////
//     //  generate address for input feature data                           //
//     ////////////////////////////////////////////////////////////////////////

// }




// object CORA_PRED_PRECOMPUTE_dtDriver extends App {
//   implicit val conf: matrixConfiguration = new matrixConfiguration
//   chisel3.Driver.execute(args, () => new CORA_PRED_PRECOMPUTE_dt)
// }