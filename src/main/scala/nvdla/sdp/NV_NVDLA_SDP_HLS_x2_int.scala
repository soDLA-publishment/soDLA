package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_HLS_x2_int extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val cfg_alu_algo = Input(UInt(2.W))
        val cfg_alu_bypass = Input(Bool())
        val cfg_alu_op = Input(UInt(16.W))
        val cfg_alu_shift_value = Input(UInt(6.W))
        val cfg_alu_src = Input(Bool())
        val cfg_mul_bypass = Input(Bool())
        val cfg_mul_op = Input(UInt(16.W))
        val cfg_mul_prelu = Input(Bool())
        val cfg_mul_shift_value = Input(UInt(6.W))
        val cfg_mul_src = Input(Bool())
        val cfg_relu_bypass = Input(Bool())
modify yourself
        val chn_alu_op_pvld = Input(Bool())
modify yourself
        val chn_in_pvld = Input(Bool())
modify yourself
        val chn_mul_op_pvld = Input(Bool())
        val chn_out_prdy = Input(Bool())
        val chn_alu_op_prdy = Output(Bool())
modify yourself
        val chn_in_prdy = Output(Bool())
        val chn_mul_op_prdy = Output(Bool())
        val chn_out_pvld = Output(Bool())
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
withClock(io.nvdla_core_clk)
}