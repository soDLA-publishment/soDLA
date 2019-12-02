package nvdla

import chisel3._
import chisel3.util._


class p_SSYNC2DO_C_PP extends Module {
    val io = IO(new Bundle {
        val clk= Input(Clock())
        val d = Input(Bool())
        val q = Output(Bool())
        val clr_ = Input(Bool())
    })
withClockAndReset(io.clk, !io.clr_){

    val q_out = RegInit(false.B)
    val d0_out = RegInit(false.B)
    
    q_out := d0_out
    d0_out := io.d

    io.q := q_out
  
}}


