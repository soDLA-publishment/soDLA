package nvdla

import chisel3._


class LNQD1PO4 extends Module {
  val io = IO(new Bundle {
    val D = Input(Clock())
    val EN = Input(Bool())
    val Q = Output(Bool())
  })


  withClock (!io.EN) {
    // In this withClock scope, all synchronous elements are clocked against io.clockB.

    // This register is clocked against io.clockB, but uses implict reset from the parent context.
    io.Q := RegNext(io.D)
  }

}