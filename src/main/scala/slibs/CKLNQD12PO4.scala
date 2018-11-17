package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class CKLNQD12PO4 extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val CP = Input(Clock())
    val TE = Input(Bool())
    val E = Input(Bool())
    val Q = Output(Clock())
  })


  withClock((!io.CP.asUInt()).asClock){
    val qd = RegNext(io.TE|io.E)
    io.Q := io.CP.asUInt() & qd   
  }
}

