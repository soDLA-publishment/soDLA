package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class p_SSYNC3DO_C_PPP extends Module {
    val io = IO(new Bundle {
        val clk= Input(Clock())
        val d = Input(Bool())
        val q = Output(Bool())
    })
withClock(io.clk){

    val q_out = RegInit(false.B)
    val d1_out = RegInit(false.B)
    val d0_out = RegInit(false.B)
    
    q_out := d1_out
    d1_out := d0_out
    d0_out := io.q

    io.q := q_out

  }
}


