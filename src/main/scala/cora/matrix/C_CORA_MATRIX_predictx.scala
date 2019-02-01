package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._

//this module is to mac tr and stat



class C_CORA_MATRIX_predictx_fp(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val rounding_mode = Input(UInt(3.W))
        val op = Input(UInt(2.W))
        val detectTininess = Input(UInt(1.W))

        val stat_actv_data = Input(Vec(conf.KF_STAT, UInt(conf.KF_BPE.W)))
        val stat_actv_pvld = Input(Vec(conf.KF_STAT, Bool()))

        val tr_actv_data = Input(Vec(conf.KF_STAT, UInt(conf.KF_BPE.W)))
        val tr_actv_pvld = Input(Vec(conf.KF_STAT, Bool()))

        //output
        val stat_out_data = Output(UInt(conf.KF_BPE.W))
        val stat_out_pvld = Output(Bool())
        val stat_out_exceptionFlags = Output(UInt(5.W))  
       
    })

//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │                            need 8 pipes to finish
//       │                 │
//       └───┐         ┌───┘                            0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |
//           │         │                                0 ------>      
//           │         │                                a0------>| s0 ------>|
//           │         │                                b0------>|    
//           │         └──────────────┐                            a1 ------>| s1 ------>|  
//           │                        │                            b1 ------>|      
//           │                        ├─┐                                      a2 ------>| s2 ------>|       
//           │                        ┌─┘                                      b2 ------>|
//           │                        │                                                    a3 ------>| s3
//           └─┐  ┐  ┌───────┬──┐  ┌──┘                                                    b3 ------>|
//             │ ─┤ ─┤       │ ─┤ ─┤            
//             └──┴──┘       └──┴──┘ 


    //==========================================================
    // MulAddRecFNPipe CELLs
    //==========================================================

    val umac = Array.fill(conf.KF_STAT){Module(new MulAddRecFNPipe)}
    val sum = Wire("b0".asUInt(conf.KF_BPE.W)) +: 
              Seq.fill(conf.KF_STAT)(Wire(UInt(conf.KF_BPE.W)))
    val valid = Wire(Vec(conf.KF_STAT+1, Bool()))
    val exception = Wire(Vec(conf.KF_STAT, UInt(5.W)))
    

    //setup config
    for (i <- 0 until conf.KF_STAT-1){
        umac(i).io.roundingMode := io.rounding_mode
        umac(i).io.op := io.op
        umac(i).io.detectTininess := io.detectTininess       
    }

    //set up first stage
    valid(0) := io.stat_actv_pvld(0) & io.tr_actv_pvld(0)

    for (i <- 0 until conf.KF_STAT-1){

        umac(i).io.validin := valid(i)
        umac(i).io.a := io.stat_actv_data(i)
        umac(i).io.b := io.tr_actv_data(i)    
        umac(i).io.c := sum(i)
        sum(i+1) := umac(i).io.out
        valid(i+1) := umac(i).io.validout*io.stat_actv_pvld(i) & io.tr_actv_pvld(i)
        exception(i) := umac(i).io.exceptionFlags
    }

    io.stat_out_data := sum(conf.KF_STAT)
    io.stat_out_pvld := valid(conf.KF_STAT)
    io.stat_out_exceptionFlags := exception.reduce(_|_)

}