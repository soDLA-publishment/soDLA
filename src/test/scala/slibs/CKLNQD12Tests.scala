package slibs

import chisel3._

class CKLNQD12 extends Module {
  val io = IO(new Bundle {
    val CP = Input(Clock())
    val TE = Input(Bool())
    val E = Input(Bool())
    val Q = Output(Bool())
  })


  withClock (!io.CP) {

    val qd = RegNext(io.TE || io.E)
  }

  io.Q := qd & io.CP
}
