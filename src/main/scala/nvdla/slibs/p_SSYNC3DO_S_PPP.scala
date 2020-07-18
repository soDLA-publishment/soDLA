package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class p_SSYNC3DO_S_PPP extends Module {
    val io = IO(new Bundle {
        val clk= Input(Clock())
        val d = Input(Bool())
        val q = Output(Bool())
        val set_ = Input(Bool())
    })
withClockAndReset(io.clk, !io.set_){

    val q_out = RegInit(true.B)
    val d1_out = RegInit(true.B)
    val d0_out = RegInit(true.B)
    
    q_out := d1_out
    d1_out := d0_out
    d0_out := io.d

    io.q := q_out

  }
}