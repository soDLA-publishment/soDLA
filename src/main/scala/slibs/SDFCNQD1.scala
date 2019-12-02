package nvdla

import chisel3._


class SDFCNQD1 extends RawModule {
  val io = IO(new Bundle {
    val SI = Input(Bool())
    val D = Input(Bool())
    val SE = Input(Bool())
    val CP = Input(Clock())
    val CDN = Input(Bool())
    val Q = Output(Bool())

  })


  withClockAndReset (io.CP, !io.CDN) {
    // In this withClock scope, all synchronous elements are clocked against io.clockB.
    // This register is clocked against io.clockB, but uses implict reset from the parent context.
    val sel = Mux(io.SE, io.SI,  io.D)
    val Q_out = RegInit(false.B)
    Q_out := sel
    io.Q := Q_out
    
  }

}



