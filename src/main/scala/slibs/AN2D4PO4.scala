package nvdla

import chisel3._
import chisel3.experimental._

class AN2D4PO4 extends RawModule {
  val io = IO(new Bundle{
    val A1  = Input(Bool())
    val A2 = Input(Bool())
    val Z = Output(Bool())
  })

  io.Z := io.A1 & io.A2
}

object AN2D4PO4Driver extends App {
  chisel3.Driver.execute(args, () => new AN2D4PO4)
}

