package nvdla

import chisel3._
import chisel3.experimental._


class oneHotClk_async_write_clock extends Module {
  val io = IO(new Bundle{
      val enable_w = Output(Bool())
  })

    val UJ_dft_xclamp_ctrl_asyncfifo_onehotclk_write = Module(new NV_BLKBOX_SRC0)
    val UJ_dft_xclamp_scan_asyncfifo_onehotclk_write = Module(new NV_BLKBOX_SRC0)

    val one_hot_enable = UJ_dft_xclamp_ctrl_asyncfifo_onehotclk_write.io.Y
    val tp = UJ_dft_xclamp_ctrl_asyncfifo_onehotclk_write.io.Y

    io.enable_w := ((!one_hot_enable) || tp )

}