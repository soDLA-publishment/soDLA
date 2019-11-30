package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._
import hardfloat._

//this module is to mac tr and stat

class CORA_MATRIX_transpose(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val tr_actv_data = Input(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
        val tr_actv_pvld = Input(Bool())

        //output
        val transpose_out_data = Output(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
        val transpose_out_pvld = Output(Bool())      
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

    for (i <- 0 to 3){
        for (j <- 0 to 3){
            io.transpose_out_data(i)(j) := io.tr_actv_data(j)(i)
        }
    }

    io.transpose_out_pvld := io.tr_actv_pvld

}