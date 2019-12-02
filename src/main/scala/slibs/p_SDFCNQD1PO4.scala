package nvdla

import chisel3._
import chisel3.util._

class p_SDFCNQD1PO4 extends RawModule {
  val io = IO(new Bundle {
    val D = Input(Bool())
    val CP = Input(Clock())
    val CDN = Input(Bool())
    val Q = Output(Bool())
  })

withClockAndReset (io.CP, !io.CDN) {
    // In this withClock scope, all synchronous elements are clocked against io.clockB.

    // This register is clocked against io.clockB, but uses implict reset from the parent context.
    val Q_out = RegInit(false.B)
    Q_out := io.D
    io.Q := Q_out
    
  }
}



