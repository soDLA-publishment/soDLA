package slibs

import chisel3._

class OR2D1 extends Module {
  val io = IO(new Bundle {
    val A1  = Input(Bool())
    val A2 = Input(Bool())
    val Z = Output(Bool())
  })
  io.Z := io.A1 || io.A2
}
