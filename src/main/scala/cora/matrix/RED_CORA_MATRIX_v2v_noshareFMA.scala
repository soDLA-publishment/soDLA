package cora


import chisel3._
import chisel3.experimental._
import chisel3.util._
import hardfloat._

//this module is to mac tr and stat

class RED_CORA_MATRIX_v2v_fp_noshareFMA(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
        
        val roundingMode = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))

        val stat_actv_data = Input(Vec(conf.KF_STAT, UInt(conf.KF_BPE.W)))
        val stat_actv_nz = Input(Vec(conf.KF_STAT, Bool()))
        val stat_actv_pvld = Input(Vec(conf.KF_STAT, Bool()))

        val tr_actv_data = Input(Vec(conf.KF_STAT, UInt(conf.KF_BPE.W)))
        val tr_actv_nz = Input(Vec(conf.KF_STAT, Bool()))
        val tr_actv_pvld = Input(Vec(conf.KF_STAT, Bool()))

        //output
        val stat_out_data = Output(UInt(conf.KF_BPE.W))
        val stat_out_pvld = Output(Bool())
       
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

withClock(io.nvdla_core_clk){
    //==========================================================
    // MulAddRecFNPipe CELLs
    //==========================================================

    val umac = Array.fill(5)(Module(new MulAddRecFNPipe()))
    
    //setup config
    for (i <- 0 to 4){
        umac(i).io.roundingMode := io.roundingMode
        umac(i).io.op := 0.U
        umac(i).io.detectTininess := io.detectTininess   
    }

    //set up first stage
    umac(0).io.validin := io.stat_actv_pvld(0) & io.tr_actv_pvld(0)
    umac(0).io.a := io.stat_actv_data(0)
    umac(0).io.b := io.tr_actv_data(0) 
    umac(0).io.c := "b0".asUInt(conf.KF_BPE.W)

    umac(2).io.validin := io.stat_actv_pvld(2) & io.tr_actv_pvld(2)
    umac(2).io.a := io.stat_actv_data(2)
    umac(2).io.b := io.tr_actv_data(2)   
    umac(2).io.c := "b0".asUInt(conf.KF_BPE.W)  

    //set up second stage

    umac(1).io.validin := io.stat_actv_pvld(1) & io.tr_actv_pvld(1) & umac(0).io.validout
    umac(1).io.a := io.stat_actv_data(1)
    umac(1).io.b := io.tr_actv_data(1)   
    umac(1).io.c := umac(0).io.out

    umac(3).io.validin := io.stat_actv_pvld(3) & io.tr_actv_pvld(3) & umac(2).io.validout
    umac(3).io.a := io.stat_actv_data(3)
    umac(3).io.b := io.tr_actv_data(3)     
    umac(3).io.c := umac(2).io.out  

    //third stage

    umac(4).io.validin := umac(1).io.validout & umac(3).io.validout
    umac(4).io.a := "b0_100000000_000000000000000000000000".asUInt(conf.KF_BPE.W)
    umac(4).io.b := umac(1).io.out
    umac(4).io.c := umac(3).io.out 
    
    io.stat_out_data := umac(4).io.out
    io.stat_out_pvld := umac(4).io.validout

}}