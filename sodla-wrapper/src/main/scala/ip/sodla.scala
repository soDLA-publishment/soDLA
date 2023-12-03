// See LICENSE for license details.
package np.blocks.ip.dla

import sys.process._

import chisel3._
import chisel3.util._

//scalastyle:off
//turn off linter: blackbox name must match verilog module
class sodla(configName: String, hasSecondAXI: Boolean, dataWidthAXI: Int)
  extends BlackBox with HasBlackBoxPath
{
  val io = IO(new Bundle {

    val io_core_clk = Input(Clock())
    val io_rstn = Input(Bool())
    val io_csb_rstn = Input(Bool())

    val io_dla_intr = Output(Bool())
    // dbb AXI
    val io_nvdla_core2dbb_aw_valid = Output(Bool())
    val io_nvdla_core2dbb_aw_ready = Input(Bool())
    val io_nvdla_core2dbb_aw_bits_id = Output(UInt((8).W))
    val io_nvdla_core2dbb_aw_bits_len = Output(UInt((4).W))
    val io_nvdla_core2dbb_aw_size = Output(UInt((3).W))
    val io_nvdla_core2dbb_aw_bits_addr = Output(UInt((64).W))

    val io_nvdla_core2dbb_w_valid = Output(Bool())
    val io_nvdla_core2dbb_w_ready = Input(Bool())
    val io_nvdla_core2dbb_w_bits_data = Output(UInt((dataWidthAXI).W))
    val io_nvdla_core2dbb_w_bits_strb = Output(UInt((dataWidthAXI/8).W))
    val io_nvdla_core2dbb_w_bits_last = Output(Bool())

    val io_nvdla_core2dbb_ar_valid = Output(Bool())
    val io_nvdla_core2dbb_ar_ready = Input(Bool())
    val io_nvdla_core2dbb_ar_bits_id = Output(UInt((8).W))
    val io_nvdla_core2dbb_ar_bits_len = Output(UInt((4).W))
    val io_nvdla_core2dbb_ar_size = Output(UInt((3).W))
    val io_nvdla_core2dbb_ar_bits_addr = Output(UInt((64).W))

    val io_nvdla_core2dbb_b_valid = Input(Bool())
    val io_nvdla_core2dbb_b_ready = Output(Bool())
    val io_nvdla_core2dbb_b_bits_id = Input(UInt((8).W))

    val io_nvdla_core2dbb_r_valid = Input(Bool())
    val io_nvdla_core2dbb_r_ready = Output(Bool())
    val io_nvdla_core2dbb_r_bits_id = Input(UInt((8).W))
    val io_nvdla_core2dbb_r_bits_last = Input(Bool())
    val io_nvdla_core2dbb_r_bits_data = Input(UInt((dataWidthAXI).W))
    // cvsram AXI
    // cfg APB
    val io_psel = Input(Bool())
    val io_penable = Input(Bool())
    val io_pwrite = Input(Bool())
    val io_paddr = Input(UInt((32).W))
    val io_pwdata = Input(UInt((32).W))
    val io_prdata = Output(UInt((32).W))
    val io_pready = Output(Bool())
  })

  val chipyardDir = System.getProperty("user.dir")
  val nvdlaVsrcDir = s"$chipyardDir/generators/sodla-wrapper/src/main/resources"

  val makeStr = s"make -C $chipyardDir/generators/soDLA default SODLA_TYPE=${configName}"
  require (makeStr.! == 0, "Failed to run pre-processing step")

  addPath(s"$nvdlaVsrcDir/SO_${configName}.v")
}
