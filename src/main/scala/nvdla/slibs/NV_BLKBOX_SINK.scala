package nvdla

import chisel3._
import chisel3.experimental._


class NV_BLKBOX_SINK extends Module {
  val io = IO(new Bundle{
      val A = Input(Bool())
  })

}
