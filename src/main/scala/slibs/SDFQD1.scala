package slibs

import chisel3._


class SDFQD1 extends Module {
  val io = IO(new Bundle {
    val SI = Input(Bool())
    val D = Input(Bool())
    val SE = Input(Bool())
    val CP = Input(Clock())
    val Q = Output(Bool())

  })

    val sel = Mux(SE, I1,  D)

  withClock (io.CP) {
    // In this withClock scope, all synchronous elements are clocked against io.clockB.

    // This register is clocked against io.clockB, but uses implict reset from the parent context.

    io.Q := RegNext(sel)
    
  }

}