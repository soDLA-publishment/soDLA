package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_HLS_X_int_alu extends Module {
   val LUT_DEPTH = 256
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val alu_data_in = Input(UInt(32.W))
        val alu_in_pvld = Input(Bool())
        val alu_op_pvld = Input(Bool())
        val alu_out_prdy = Input(Bool())
        val cfg_alu_algo = Input(UInt(2.W))
        val cfg_alu_bypass = Input(Bool())
        val cfg_alu_op = Input(UInt(16.W))
        val cfg_alu_shift_value = Input(UInt(6.W))
        val cfg_alu_src = Input(Bool())
        val chn_alu_op = Input(UInt(16.W))
        val nvdla_core_clk = Input(Bool())
        val nvdla_core_rstn = Input(Bool())
        val alu_data_out = Output(UInt(33.W))
        val alu_in_prdy = Output(Bool())
        val alu_op_prdy = Output(Bool())
        val alu_out_pvld = Output(Bool())
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
withClock(io.nv)
}