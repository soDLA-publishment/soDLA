package nvdla

import chisel3._


class NV_BLKBOX_SINK extends Module {
  val io = IO(new Bundle{
      val A = Input(Bool())
  })

}
