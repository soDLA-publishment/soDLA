package nvdla

import chisel3._
import chisel3.experimental._


class NV_BLKBOX_BUFFER extends Module {
  val io = IO(new Bundle{
      val A = Input(Bool())
      val Y = Output(Bool())
  })
  io.Y := io.A

}
