package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._
import hardfloat._
import chisel3.iotesters.Driver

//A*B*C

class CORA_MATRIX_MUL_m2m2m(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val reg2dp_roundingMode = Input(UInt(3.W))
        val reg2dp_detectTininess = Input(Bool())

        val tr_a_actv_data = Input(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
        val tr_a_actv_pvld = Input(Bool())

        val tr_b_actv_data = Input(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
        val tr_b_actv_pvld = Input(Bool())

        val tr_c_actv_data = Input(Vec(4, Vec(4, UInt(conf.KF_BPE.W))))
        val tr_c_actv_pvld = Input(Bool())

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


    //Hardware Reuse
    //clock counter
    val m2m2m_st = io.tr_a_actv_pvld & io.tr_b_actv_pvld & io.tr_c_actv_pvld
    val m2m2m_done = Wire(Bool())

    val clk_cnt = RegInit(0.U)
    clk_cnt := Mux(m2m2m_st, 0.U,
               Mux(m2m2m_done, 0.U,
               clk_cnt + 1.U))
    
    m2m2m_done := ( clk_cnt === (2*conf.V2V_MAC_LATENCY).U)
    
    //instantiate m2m2m cells
    val first_stage = (clk_cnt >= 0.U) & (clk_cnt <= (conf.V2V_MAC_LATENCY-1).U)
    val second_stage = (clk_cnt >= (conf.V2V_MAC_LATENCY).U) & (clk_cnt <= (2*conf.V2V_MAC_LATENCY-1).U)

    val dout_first_stage = Reg(Vec(4, Vec(4, UInt((conf.KF_BPE).W))))
    val dout_pvld_first_stage = RegInit(false.B)

    val um2m = Module(new CORA_MATRIX_MUL_m2m)
    //setup config
    um2m.io.reg2dp_roundingMode := io.reg2dp_roundingMode
    um2m.io.reg2dp_detectTininess := io.reg2dp_detectTininess   

    when(first_stage){
        //set up first stage
        //mac zero
        um2m.io.tr_a_actv_data := io.tr_a_actv_data
        um2m.io.tr_a_actv_pvld := m2m2m_st

        um2m.io.tr_b_actv_data := io.tr_b_actv_data
        um2m.io.tr_b_actv_pvld := m2m2m_st
    }
    .elsewhen(second_stage){
        //result from first stage
        dout_pvld_first_stage := um2m.io.tr_out_pvld
        dout_first_stage := um2m.io.tr_out_data

        um2m.io.tr_a_actv_data := dout_first_stage
        um2m.io.tr_a_actv_pvld := dout_pvld_first_stage

        um2m.io.tr_b_actv_data := ShiftRegister(io.tr_c_actv_data, conf.V2V_MAC_LATENCY)
        um2m.io.tr_b_actv_pvld := dout_pvld_first_stage
    }
    .otherwise{
        um2m.io.tr_a_actv_data := VecInit(Seq.fill(4)(VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))))
        um2m.io.tr_a_actv_pvld := false.B

        um2m.io.tr_b_actv_data := VecInit(Seq.fill(4)(VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))))
        um2m.io.tr_b_actv_pvld := false.B
    }

    when(io.tr_out_pvld){
        io.tr_out_data := um2m.io.tr_out_data
    }
    .otherwise{
        io.tr_out_data := VecInit(Seq.fill(4)(VecInit(Seq.fill(4)("b0".asUInt((conf.KF_BPE).W)))))
    }
    io.tr_out_pvld := ShiftRegister(m2m2m_st, 2*conf.V2V_MAC_LATENCY, m2m2m_st) &
                        ShiftRegister(dout_pvld_first_stage, conf.V2V_MAC_LATENCY, dout_pvld_first_stage) &
                        um2m.io.tr_out_pvld

}

object CORA_MATRIX_MUL_m2m2mDriver extends App {
  implicit val conf: matrixConfiguration = new matrixConfiguration
  chisel3.Driver.execute(args, () => new CORA_MATRIX_MUL_m2m2m)
}
