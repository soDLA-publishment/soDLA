package nvdla

import chisel3._
import chisel3.experimental._


class NV_BLKBOX_SRC0 extends Module {
  val io = IO(new Bundle{
      val Y = Output(Bool())
  })

  io.Y := false.B
}
