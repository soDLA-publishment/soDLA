package nvdla

import chisel3._
import chisel3.experimental._


class SDFSNQD1 extends Module {
  val io = IO(new Bundle {
    val SI = Input(Bool())
    val D = Input(Bool())
    val SE = Input(Bool())
    val CP = Input(Clock())
    val SDN = Input(Bool())
    val Q = Output(Bool())

  })

  withClockAndReset (io.CP, !io.SDN) {
    // In this withClock scope, all synchronous elements are clocked against io.clockB.
    // This register is clocked against io.clockB, but uses implict reset from the parent context.
    val sel = Mux(io.SE, io.SI, io.D)
    io.Q := RegInit(true.B)
    io.Q := sel
    
  }
    

}