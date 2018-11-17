package nvdla

import chisel3._
import chisel3.experimental._

class MUX2HDD2 extends Module {
  val io = IO(new Bundle {
    val I0 = Input(Bool())
    val I1 = Input(Bool())
    val S = Input(Bool())
    val Z = Output(Bool())
  })
  io.Z := Mux(io.S, io.I1, io.I0)
}