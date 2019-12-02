package nvdla

import chisel3._
import chisel3.util._

class CKLNQD12 extends Module {
  val io = IO(new Bundle {
    val CP = Input(Clock())
    val TE = Input(Bool())
    val E = Input(Bool())
    val Q = Output(Clock())
  })


  withClock((!io.CP.asUInt.asBool).asClock) {
    val qd = RegNext(io.TE|io.E)
    io.Q := (io.CP.asUInt.asBool & qd).asClock 
  }
}

