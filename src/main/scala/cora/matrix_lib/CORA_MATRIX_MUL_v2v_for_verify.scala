package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._
import hardfloat._

//this module is to mac tr and stat

class CORA_MATRIX_MUL_v2v_for_verify(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val reg2dp_roundingMode = Input(UInt(3.W))
        val reg2dp_detectTininess = Input(Bool())

        val stat_actv_data = Input(Vec(4, UInt(conf.KF_BPE.W)))
        val stat_actv_pvld = Input(Bool())

        val tr_actv_data = Input(Vec(4, UInt(conf.KF_BPE.W)))
        val tr_actv_pvld = Input(Bool())

        //output
        val mac_out_data = Output(UInt(conf.KF_BPE.W))
        val mac_out_pvld = Output(Bool())
       
    })

//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │                            need 6 pipes to finish
//       │                 │
//       └───┐         ┌───┘                            0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |
//           │         │                                0 ------>      
//           │         │                                a0------>| s0 ------>|
//           │         │                                b0------>|    
//           │         └──────────────┐                            a1 ------>| s01 ------>|
//           │                        │                            b1 ------>| 
//                                                     0 ------->|                         result
//           │                        ├─┐              a2 ------>| s2 ------>|       
//           │                        ┌─┘              b2 ------>|
//           │                        │                            a3 ------>| s23 ------>|
//           └─┐  ┐  ┌───────┬──┐  ┌──┘                            b3 ------>|
//             │ ─┤ ─┤       │ ─┤ ─┤            
//             └──┴──┘       └──┴──┘ 

    //==========================================================
    // default mac cells
    //==========================================================
    
    val pp_pvld_d0 = io.stat_actv_pvld & io.tr_actv_pvld
    //
    val mout = VecInit(Seq.fill(4)(0.asUInt(conf.KF_BPE.W)))

    for(i <- 0 to 3){                     
        mout(i) := io.stat_actv_data(i)*io.tr_actv_data(i)
    }  

    val sum_out = mout.reduce(_+_).asUInt & Fill(conf.V2V_MAC_LATENCY, pp_pvld_d0) 

    //add retiming as delay
 
    io.mac_out_data := ShiftRegister(sum_out, conf.V2V_MAC_LATENCY, pp_pvld_d0)
    io.mac_out_pvld := ShiftRegister(pp_pvld_d0, conf.V2V_MAC_LATENCY, pp_pvld_d0)


}