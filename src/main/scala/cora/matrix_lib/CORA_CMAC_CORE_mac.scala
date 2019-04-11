package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

//this module is to mac stat and transition

class CORA_CMAC_CORE_mac(implicit conf: matrixConfiguration) extends Module {
    val io = IO(new Bundle {
        //reg2dp
        val reg2dp_roundingMode = Input(UInt(3.W))
        val reg2dp_detectTininess = Input(Bool())

        //tr and stat
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
    // MulAddRecFNPipe CELLs
    //==========================================================
    //Hardware Reuse
    //clock counter
    val mac_st = io.stat_actv_pvld & io.tr_actv_pvld
    val mac_done = Wire(Bool())

    val clk_cnt = RegInit(0.U)
    clk_cnt := Mux(mac_st, 0.U,
               Mux(mac_done, 0.U,
               clk_cnt + 1.U))
    
    mac_done := ( clk_cnt === (conf.V2V_MAC_LATENCY).U)
    
    //instantiate MulAddRecFNPipe cells
    val first_stage = (clk_cnt >= 0.U) | (clk_cnt <= (conf.HARDFLOAT_MAC_LATENCY - 1).U)
    val second_stage = (clk_cnt >= (conf.HARDFLOAT_MAC_LATENCY).U) | (clk_cnt <= (2*conf.HARDFLOAT_MAC_LATENCY - 1).U)
    val third_stage = (clk_cnt >= (2*conf.HARDFLOAT_MAC_LATENCY).U) | (clk_cnt <= (3*conf.HARDFLOAT_MAC_LATENCY - 1).U)

    val dout_first_stage = Reg(Vec(2, UInt(conf.KF_BPE.W)))
    val dout_pvld_first_stage = Seq.fill(2)(RegInit(false.B))

    val dout_second_stage = Reg(Vec(2, UInt(conf.KF_BPE.W)))
    val dout_pvld_second_stage = Seq.fill(2)(RegInit(false.B))

    val umac = Array.fill(2)(Module(new MulAddRecFNPipe())) 
    //setup config
    for (i <- 0 to 1){
        umac(i).io.roundingMode := io.reg2dp_roundingMode
        umac(i).io.op := 0.U
        umac(i).io.detectTininess := io.reg2dp_detectTininess   
    }

    when(first_stage){
        //set up first stage
        //mac zero
        umac(0).io.validin := mac_st
        umac(0).io.a := io.stat_actv_data(0)
        umac(0).io.b := io.tr_actv_data(0) 
        umac(0).io.c := "b0".asUInt(conf.KF_BPE.W)
        //mac one
        umac(1).io.validin := mac_st
        umac(1).io.a := io.stat_actv_data(2)
        umac(1).io.b := io.tr_actv_data(2)   
        umac(1).io.c := "b0".asUInt(conf.KF_BPE.W)  
    }
    .elsewhen(second_stage){
        //result from first stage
        dout_pvld_first_stage(0) := umac(0).io.validout
        dout_first_stage(0) := umac(0).io.out 

        dout_pvld_first_stage(1) := umac(1).io.validout
        dout_first_stage(1) := umac(1).io.out

        //set up second stage
        //mac zero
        umac(0).io.validin := dout_pvld_first_stage(0) & dout_pvld_first_stage(1)
        umac(0).io.a := ShiftRegister(io.stat_actv_data(1), conf.HARDFLOAT_MAC_LATENCY)
        umac(0).io.b := ShiftRegister(io.tr_actv_data(1), conf.HARDFLOAT_MAC_LATENCY) 
        umac(0).io.c := dout_first_stage(0)
        
        //mac one
        umac(1).io.validin := dout_pvld_first_stage(0) & dout_pvld_first_stage(1)
        umac(1).io.a := ShiftRegister(io.stat_actv_data(3), conf.HARDFLOAT_MAC_LATENCY)
        umac(1).io.b := ShiftRegister(io.tr_actv_data(3), conf.HARDFLOAT_MAC_LATENCY)    
        umac(1).io.c := dout_first_stage(1) 

    }
    .elsewhen(third_stage){
        //result from second stage
        dout_pvld_second_stage(0) := umac(0).io.validout
        dout_second_stage(0) := umac(0).io.out
        
        dout_pvld_second_stage(1) := umac(1).io.validout
        dout_second_stage(1) := umac(1).io.out

        //set up third stage
        //mac zero
        umac(0).io.validin := dout_pvld_second_stage(0) & dout_pvld_second_stage(1)
        umac(0).io.a := "b0_100000000_000000000000000000000000".asUInt(conf.KF_BPE.W)
        umac(0).io.b := dout_second_stage(0) 
        umac(0).io.c := dout_second_stage(1)


        //mac one
        umac(1).io.validin := false.B
        umac(1).io.a := "b0".asUInt(conf.KF_BPE.W)
        umac(1).io.b := "b0".asUInt(conf.KF_BPE.W)    
        umac(1).io.c := "b0".asUInt(conf.KF_BPE.W) 
    
    }
    .otherwise{
        //mac zero
        umac(0).io.validin := false.B
        umac(0).io.a := "b0".asUInt(conf.KF_BPE.W)
        umac(0).io.b := "b0".asUInt(conf.KF_BPE.W)    
        umac(0).io.c := "b0".asUInt(conf.KF_BPE.W) 

        //mac one
        umac(1).io.validin := false.B
        umac(1).io.a := "b0".asUInt(conf.KF_BPE.W)
        umac(1).io.b := "b0".asUInt(conf.KF_BPE.W)    
        umac(1).io.c := "b0".asUInt(conf.KF_BPE.W) 

    }

    io.mac_out_data := Fill(conf.KF_BPE, io.mac_out_pvld) & umac(0).io.out
    io.mac_out_pvld := ShiftRegister(mac_st, (conf.HARDFLOAT_MAC_LATENCY*3), mac_st) &
                       ShiftRegister(dout_pvld_first_stage.reduce(_&_), (conf.HARDFLOAT_MAC_LATENCY*2), dout_pvld_first_stage.reduce(_&_)) &
                       ShiftRegister(dout_pvld_second_stage.reduce(_&_), (conf.HARDFLOAT_MAC_LATENCY), dout_pvld_second_stage.reduce(_&_)) &
                       umac(0).io.validout  


}


object CORA_CMAC_CORE_macDriver extends App {
  implicit val conf: matrixConfiguration = new matrixConfiguration
  chisel3.Driver.execute(args, () => new CORA_CMAC_CORE_mac)
}
