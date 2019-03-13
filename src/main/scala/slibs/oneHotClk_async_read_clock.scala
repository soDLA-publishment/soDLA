package nvdla

import chisel3._
import chisel3.experimental._


class oneHotClk_async_read_clock extends Module {
  val io = IO(new Bundle{
      val enable_r = Output(Bool())
  })

    val UJ_dft_xclamp_ctrl_asyncfifo_onehotclk_read = Module(new NV_BLKBOX_SRC0)
    val UJ_dft_xclamp_scan_asyncfifo_onehotclk_read = Module(new NV_BLKBOX_SRC0)

    val one_hot_enable = UJ_dft_xclamp_ctrl_asyncfifo_onehotclk_read.io.Y
    val tp = UJ_dft_xclamp_scan_asyncfifo_onehotclk_read.io.Y

    io.enable_r := (!one_hot_enable) || (!tp) 

}