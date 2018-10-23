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
    // In this withClock scope, all synchronous elements are clocked against io.clockB.

    // This register is clocked against io.clockB, but uses implict reset from the parent context.
    val qd = RegNext(io.TE || io.E)
  }

  io.Q := qd & io.CP
}
