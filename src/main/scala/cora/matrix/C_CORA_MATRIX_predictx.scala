package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._

//this module is to mac tr and stat

class C_CORA_MATRIX_predictx_int(implicit conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val stat_actv_data = Input(Vec(conf.KF_STAT, conf.KF_TYPE(conf.KF_BPE.W)))
        val stat_actv_nz = Input(Vec(conf.KF_STAT, Bool()))
        val stat_actv_pvld = Input(Vec(conf.KF_STAT, Bool()))

        val tr_actv_data = Input(Vec(conf.KF_STAT, Vec(conf.KF_STAT, conf.KF_TYPE(conf.KF_BPE.W))))
        val tr_actv_nz = Input(Vec(conf.KF_STAT, Vec(conf.KF_STAT, Bool())))
        val tr_actv_pvld = Input(Vec(conf.KF_STAT, Vec(conf.KF_STAT, Bool())))

        //output
        val stat_out_data = Output(Vec(conf.KF_STAT, conf.KF_TYPE(conf.KF_RESULT_WIDTH.W)))
        val stat_out_nz = Output(Vec(conf.KF_STAT, Bool()))
        val stat_out_pvld = Output(Vec(conf.KF_STAT, Bool()))   
       
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
    //==========================================================
    // multiply        
    //==========================================================
                
    val mout = VecInit(Seq.fill(conf.KF_STAT)(VecInit(Seq.fill(conf.KF_STAT)(conf.KF_TYPE(0, (2*conf.KF_BPE).W)))))
    val nz = VecInit(Seq.fill(conf.KF_STAT)(false.B))

    for(i <- 0 to conf.KF_STAT-1){
        for(j <- 0 to conf.KF_STAT-1){
            when(io.tr_actv_pvld(i)(j)&io.tr_actv_nz(i)(j)&io.stat_actv_pvld(j)&io.stat_actv_nz(j)){                       
                mout(i)(j) := io.tr_actv_data(i)(j)*io.stat_actv_data(j)
                nz(i) := true.B
            }
            .otherwise{
                mout(i)(j) := conf.KF_TYPE(0, conf.KF_RESULT_WIDTH)
                nz(i) := false.B
            }
    }} 

    //==========================================================
    // accumulate        
    //==========================================================
    val sum_out = VecInit(Seq.fill(conf.KF_STAT)(conf.KF_TYPE(0, conf.KF_RESULT_WIDTH)))
    val pvld_out = VecInit(Seq.fill(conf.KF_STAT)(false.B))

    for(i <- 0 to conf.KF_STAT-1){
        sum_out(i) := mout(i).reduce(_+_)
        pvld_out(i) := io.tr_actv_pvld(i)(0)*io.stat_actv_pvld(0)
        
    }
    
    //add retiming
  
    for(i <- 0 to conf.KF_STAT-1){
        io.stat_out_data(i) := ShiftRegister(sum_out(i), conf.KF_OUT_RETIMING, pvld_out(i))
        io.stat_out_nz(i) := ShiftRegister(nz(i), conf.KF_OUT_RETIMING, pvld_out(i)) 
        io.stat_out_pvld(i) := ShiftRegister(pvld_out(i), conf.KF_OUT_RETIMING, pvld_out(i))
    }

}