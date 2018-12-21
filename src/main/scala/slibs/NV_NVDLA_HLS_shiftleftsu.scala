package nvdla

import chisel3._
import chisel3.experimental._


class BlackBoxRealAdd extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle() {
    val in1 = Input(UInt(64.W))
    val in2 = Input(UInt(64.W))
    val out = Output(UInt(64.W))
  })
  setResource("/real_math.v")
}

