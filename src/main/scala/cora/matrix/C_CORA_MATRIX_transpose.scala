package cora


import chisel3._
import chisel3.experimental._
import chisel3.util._
import hardfloat._

//this module is to mac tr and stat

class C_CORA_MATRIX_transpose(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val tr_actv_data = Input(Vec(conf.KF_STAT, Vec(conf.KF_STAT, UInt(conf.KF_BPE.W))))
        val tr_actv_pvld = Input(Vec(conf.KF_STAT, Vec(conf.KF_STAT, Bool())))

        //output
        val transpose_out_data = Output(Vec(conf.KF_STAT, Vec(conf.KF_STAT, UInt(conf.KF_BPE.W))))
        val transpose_out_pvld = Output(Vec(conf.KF_STAT, Vec(conf.KF_STAT, Bool())))      
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

    
    for (i <- 0 to conf.KF_STAT-1){
        for (j <- 0 to conf.KF_STAT-1){
            io.transpose_out_data(i)(j) := io.tr_actv_data(j)(i)
            io.transpose_out_pvld(i)(j) := io.tr_actv_pvld(j)(i)
        }
    }

}