package nvdla

import chisel3._
import chisel3.experimental._


class LNQD1PO4 extends Module {
  val io = IO(new Bundle {
    val D = Input(Bool())
    val EN = Input(Clock())
    val Q = Output(Bool())
  })


  withClock((!io.EN.asUInt()).asClock) {
    // In this withClock scope, all synchronous elements are clocked against io.clockB.

    // This register is clocked against io.clockB, but uses implict reset from the parent context.
    io.Q := RegNext(io.D)
  }

}

