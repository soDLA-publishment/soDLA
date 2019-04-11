package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._
import hardfloat._
import chisel3.iotesters.Driver

//A+B
//need retiming
class CORA_MATRIX_ADD_m2m(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val reg2dp_roundingMode = Input(UInt(3.W))
        val reg2dp_detectTininess = Input(Bool())

        val tr_a_actv_data = Input(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
        val tr_a_actv_pvld = Input(Bool())

        val tr_b_actv_data = Input(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
        val tr_b_actv_pvld = Input(Bool())

        //output
        val tr_out_data = Output(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
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


    val mat_add_st = io.tr_a_actv_pvld & io.tr_b_actv_pvld

    //instance adders
    val u_mat_add = Array.fill(4){Array.fill(4){Module(new MulAddRecFNPipe())}}
     
    val out_valid = Wire(Vec(4, Vec(4, Bool())))
    for(i<- 0 to 3){
        for(j<- 0 to 3){
            u_mat_add(i)(j).io.roundingMode := io.reg2dp_roundingMode
            u_mat_add(i)(j).io.op := 0.U
            u_mat_add(i)(j).io.detectTininess := io.reg2dp_detectTininess
            u_mat_add(i)(j).io.validin := mat_add_st
            u_mat_add(i)(j).io.a := "b0_100000000_000000000000000000000000".asUInt(conf.KF_BPE.W) 
            u_mat_add(i)(j).io.b := io.tr_a_actv_data(i)(j)
            u_mat_add(i)(j).io.c := io.tr_b_actv_data(i)(j)
            io.tr_out_data(i)(j) := u_mat_add(i)(j).io.out
            out_valid(i)(j) := u_mat_add(i)(j).io.validout

        }
    }

    io.tr_out_pvld := out_valid.asUInt.andR

}

object CORA_MATRIX_ADD_m2mDriver extends App {
  implicit val conf: matrixConfiguration = new matrixConfiguration
  chisel3.Driver.execute(args, () => new CORA_MATRIX_ADD_m2m)
}
