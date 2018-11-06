package slibs

import chisel3._


class p_SSYNC3DO_C_PPP extends Module {
    val io = IO(new Bundle {
        val clk= Input(Clock())
        val d = Input(Bool())
        val clr_ = Input(Clock())
        val q = Output(Bool())
    })

    io.q := Reg(Bool())
    val d1 = Reg(Bool())
    val d0 = Reg(Bool())

    withClockAndReset(io.clk, !clr_) {
        d0:=d
        d1:=d0
        io.q:=d1
    
  }
}