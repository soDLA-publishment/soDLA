package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._
import hardfloat._
import chisel3.iotesters.Driver

//this module is to get the result of stat multiply transition matrix

class CORA_MATRIX_MUL_v2m(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val reg2dp_roundingMode = Input(UInt(3.W))
        val reg2dp_detectTininess = Input(Bool())

        val stat_actv_data = Input(Vec(4, conf.KF_TYPE(conf.KF_BPE.W)))
        val stat_actv_pvld = Input(Bool())

        val tr_actv_data = Input(Vec(4, Vec(4, conf.KF_TYPE(conf.KF_BPE.W))))
        val tr_actv_pvld = Input(Bool())

        //output
        val stat_out_data = Output(Vec(4, conf.KF_TYPE(conf.KF_BPE.W)))
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
//                                                        
//           │                        ├─┐                   
//           │                        ┌─┘             
//           │                        │                            
//           └─┐  ┐  ┌───────┬──┐  ┌──┘                            
//             │ ─┤ ─┤       │ ─┤ ─┤            
//             └──┴──┘       └──┴──┘ 


    //==========================================================
    // vector mac cells
    //==========================================================

    val u_tr = Module(new CORA_MATRIX_transpose)
    u_tr.io.tr_actv_data := io.tr_actv_data
    u_tr.io.tr_actv_pvld := io.tr_actv_pvld

    val transpose_out_data = u_tr.io.transpose_out_data
    val transpose_out_pvld = u_tr.io.transpose_out_pvld

    val u_v2v = Array.fill(4)(Module(new CORA_CMAC_CORE_mac()))
                  
    //setup config
    for (i <- 0 to 3){

        u_v2v(i).io.reg2dp_roundingMode := io.reg2dp_roundingMode
        u_v2v(i).io.reg2dp_detectTininess := io.reg2dp_detectTininess   
        u_v2v(i).io.stat_actv_data := io.stat_actv_data
        u_v2v(i).io.stat_actv_pvld := io.stat_actv_pvld
        u_v2v(i).io.tr_actv_data := transpose_out_data(i)
        u_v2v(i).io.tr_actv_pvld := transpose_out_pvld

        io.stat_out_data(i) := u_v2v(i).io.mac_out_data
    }

    io.stat_out_pvld := u_v2v(0).io.mac_out_pvld &
                        u_v2v(1).io.mac_out_pvld &
                        u_v2v(2).io.mac_out_pvld &
                        u_v2v(3).io.mac_out_pvld

}


object CORA_MATRIX_MUL_v2mDriver extends App {
  implicit val conf: matrixConfiguration = new matrixConfiguration
  chisel3.Driver.execute(args, () => new CORA_MATRIX_MUL_v2m)
}
