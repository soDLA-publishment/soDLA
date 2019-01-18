package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class p_STRICTSYNC3DOTM_C_PPP extends Module {
  val io = IO(new Bundle {
    val SRC_D_NEXT = Input(Bool())
    val SRC_CLK = Input(Clock())
    val SRC_CLRN = Input(Bool())
    val DST_CLK = Input(Clock())
    val DST_CLRN = Input(Bool())

    val SRC_D = Output(Bool())
    val DST_Q = Output(Bool())

    val ATPG_CTL = Input(Bool())
    val TEST_MODE = Input(Bool())


  })

  withClockAndReset(io.SRC_CLK, !io.SRC_CLRN) {

  io.SRC_D := RegNext(io.SRC_D_NEXT, false.B)

  }

  withClockAndReset(io.DST_CLK, !io.DST_CLRN) {

  io.DST_Q := RegNext(io.SRC_D_NEXT, false.B)

  }


  val sync3d = Module(new p_SSYNC3DO_C_PPP)

  io.DST_CLK := sync3d.io.clk
  io.DST_CLRN:=sync3d.io.clr_ 
  io.DST_Q:=sync3d.io.q 

}
