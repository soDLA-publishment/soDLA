package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._
import hardfloat._
import chisel3.iotesters.Driver

//B*A

class CORA_MATRIX_MUL_m2m(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val reg2dp_roundingMode = Input(UInt(3.W))
        val reg2dp_detectTininess = Input(UInt(1.W))

        val m2m_st = Input(Bool())
        val m2m_done = Output(Bool())

        val tr_a_actv_data = Input(Vec(4, Vec(4, conf.KF_TYPE(conf.KF_BPE.W))))
        val tr_a_actv_pvld = Input(Bool())

        val tr_b_actv_data = Input(Vec(4, Vec(4, conf.KF_TYPE(conf.KF_BPE.W))))
        val tr_b_actv_pvld = Input(Bool())

        //output
        val tr_out_data = Output(Vec(4, Vec(4, conf.KF_TYPE(conf.KF_BPE.W))))
        val tr_out_pvld = Output(Bool())
       
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
//           |                        |                     
//           │                        ├─┐           
//           │                        ┌─┘             
//           │                        │                            
//           └─┐  ┐  ┌───────┬──┐  ┌──┘                            
//             │ ─┤ ─┤       │ ─┤ ─┤            
//             └──┴──┘       └──┴──┘ 


    //==========================================================
    // v2m cells
    //==========================================================

    val u_v2m = Array.fill(4)(Module(new CORA_MATRIX_MUL_v2m()))
    
    //setup input
    for (i <- 0 to 3){
        u_v2m(i).io.reg2dp_roundingMode := io.reg2dp_roundingMode
        u_v2m(i).io.reg2dp_detectTininess := io.reg2dp_detectTininess   
        u_v2m(i).io.v2m_st := io.m2m_st
        u_v2m(i).io.stat_actv_data := io.tr_a_actv_data(i)
        u_v2m(i).io.stat_actv_pvld := io.tr_a_actv_pvld
        u_v2m(i).io.tr_actv_data := io.tr_b_actv_data
        u_v2m(i).io.tr_actv_pvld := io.tr_b_actv_pvld

        when(io.tr_out_pvld){
            io.tr_out_data(i) := u_v2m(i).io.stat_out_data
        }
        .otherwise{
            io.tr_out_data(i) := VecInit(Seq.fill(4)("b0".asUInt(conf.KF_BPE.W)))
        }
    }

    io.tr_out_pvld := u_v2m(0).io.stat_out_pvld &
                      u_v2m(1).io.stat_out_pvld &
                      u_v2m(2).io.stat_out_pvld &
                      u_v2m(3).io.stat_out_pvld

    io.m2m_done := u_v2m(0).io.v2m_done
}

object CORA_MATRIX_MUL_m2mDriver extends App {
  implicit val conf: matrixConfiguration = new matrixConfiguration
  chisel3.Driver.execute(args, () => new CORA_MATRIX_MUL_m2m)
}
