package nvdla

import chisel3._
import chisel3.experimental._


class p_SSYNC3DO_C_PPP extends Module {
    val io = IO(new Bundle {
        val clk= Input(Clock())
        val d = Input(Bool())
        val clr_ = Input(Bool())
        val q = Output(Bool())
    })
    withClockAndReset(io.clk, !io.clr_){

    io.q := Reg(false.B)
    val d1 = Reg(false.B)
    val d0 = Reg(false.B)
    
    d0:=io.d
    d1:=d0
    io.q:=d1
    
  }
}