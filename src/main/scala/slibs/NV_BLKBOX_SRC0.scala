package nvdla

import chisel3._


class NV_BLKBOX_SRC0 extends Module {
  val io = IO(new Bundle{
      val Y = Output(Bool())
  })

  io.Y := false.B
}
