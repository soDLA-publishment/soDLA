package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_HLS_lut_expn extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val cfg_lut_offset = Input(UInt(8.W))
        val cfg_lut_start = Input(UInt(32.W))
        val idx_data_in = Input(UInt(32.W))
        val idx_in_pvld = Input(Bool())
        val idx_out_prdy = Input(Bool())

        val idx_in_prdy = Output(Bool())
        val idx_out_pvld = Output(Bool())
        val lut_frac_out = Output(UInt(35.W))
        val lut_index_out = Output(UInt(9.W))
        val lut_oflow_out = Output(Bool())
        val lut_uflow_out = Output(Bool())
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