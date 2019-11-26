package nvdla

import chisel3._
import chisel3.util._


class p_SSYNC3DO extends Module {
    val io = IO(new Bundle {
        val clk= Input(Clock())
        val d = Input(Bool())
        val q = Output(Bool())
    })
withClock(io.clk){

    val q_out = Reg(Bool())
    val d1_out = Reg(Bool())
    val d0_out = Reg(Bool())
    
    q_out := d1_out
    d1_out := d0_out
    d0_out := io.d

    io.q := q_out

  }
}
