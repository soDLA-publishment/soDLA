package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._

//this module is to mac tr and stat

class C_CORA_MATRIX_mac(implicit conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val stat_actv_data = Input(Vec(conf.KF_STATE, conf.KF_TYPE(conf.KF_BPE.W)))
        val stat_actv_nz = Input(Vec(conf.KF_STATE, Bool()))
        val stat_actv_pvld = Input(Vec(conf.KF_STATE, Bool()))

        val tr_actv_data = Input(Vec(conf.KF_STATE, conf.KF_TYPE(conf.KF_BPE.W)))
        val tr_actv_nz = Input(Vec(conf.KF_STATE, Bool()))
        val tr_actv_pvld = Input(Vec(conf.KF_STATE, Bool()))

        //output
        val stat_out_data = Output(conf.KF_TYPE(conf.KF_RESULT_WIDTH.W))
        val stat_out_pvld = Output(Bool())         
    })

//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 
                
    val mout = VecInit(Seq.fill(conf.KF_STATE)(conf.KF_TYPE(0, (2*conf.KF_STATE).W)))

    for(i <- 0 to conf.KF_STATE-1){
        when(io.tr_actv_pvld(i)&io.tr_actv_nz(i)&io.stat_actv_pvld(i)&io.stat_actv_nz(i)){                       
             mout(i) := io.tr_actv_data(i)*io.stat_actv_data(i)
        }
        .otherwise{
             mout(i) := conf.KF_STATE(0, conf.KF_RESULT_WIDTH)
        }
    }  

    val sum_out = mout.reduce(_+&_)
    
    //add retiming
    val pp_pvld_d0 = io.tr_actv_pvld(0)&io.stat_actv_pvld(0)

    io.stat_out_data := ShiftRegister(sum_out, conf.KF_OUT_RETIMING, pp_pvld_d0)
    io.stat_out_pvld := ShiftRegister(pp_pvld_d0, conf.KF_OUT_RETIMING, pp_pvld_d0)


}