package np.devices.ip.sodla

import sys.process._

import chisel3._
import chisel3.util._

//scalastyle:off
//turn off linter: blackbox name must match verilog module
class sodla(configName: String, hasSecondAXI: Boolean, dataWidthAXI: Int)
  extends BlackBox with HasBlackBoxResource
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
    val io_nvdla_core2dbb_aw_bits_addr = Output(UInt((32).W))
    val io_nvdla_core2dbb_aw_size = Output(UInt((3).W))

    val io_nvdla_core2dbb_w_valid = Output(Bool())
    val io_nvdla_core2dbb_w_ready = Input(Bool())
    val io_nvdla_core2dbb_w_bits_data = Output(UInt((dataWidthAXI).W))
    val io_nvdla_core2dbb_w_bits_strb = Output(UInt((dataWidthAXI/8).W))
    val io_nvdla_core2dbb_w_bits_last = Output(Bool())

    val io_nvdla_core2dbb_ar_valid = Output(Bool())
    val io_nvdla_core2dbb_ar_ready = Input(Bool())
    val io_nvdla_core2dbb_ar_bits_id = Output(UInt((8).W))
    val io_nvdla_core2dbb_ar_bits_len = Output(UInt((4).W))
    val io_nvdla_core2dbb_ar_bits_addr = Output(UInt((32).W))
    val io_nvdla_core2dbb_ar_size = Output(UInt((3).W))

    val io_nvdla_core2dbb_b_valid = Input(Bool())
    val io_nvdla_core2dbb_b_ready = Output(Bool())
    val io_nvdla_core2dbb_b_bits_id = Input(UInt((8).W))

    val io_nvdla_core2dbb_r_valid = Input(Bool())
    val io_nvdla_core2dbb_r_ready = Output(Bool())
    val io_nvdla_core2dbb_r_bits_id = Input(UInt((8).W))
    val io_nvdla_core2dbb_r_bits_last = Input(Bool())
    val io_nvdla_core2dbb_r_bits_data = Input(UInt((dataWidthAXI).W))

    // cvsram AXI
    // val nvdla_core2cvsram = if (hasSecondAXI) Some(new Bundle {
    //   val aw_awvalid = Output(Bool())
    //   val aw_awready = Input(Bool())
    //   val aw_awid = Output(UInt((8).W))
    //   val aw_awlen = Output(UInt((4).W))
    //   val aw_awsize = Output(UInt((3).W))
    //   val aw_awaddr = Output(UInt((64).W))

    //   val w_wvalid = Output(Bool())
    //   val w_wready = Input(Bool())
    //   val w_wdata = Output(UInt((dataWidthAXI).W))
    //   val w_wstrb = Output(UInt((dataWidthAXI/8).W))
    //   val w_wlast = Output(Bool())

    //   val ar_arvalid = Output(Bool())
    //   val ar_arready = Input(Bool())
    //   val ar_arid = Output(UInt((8).W))
    //   val ar_arlen = Output(UInt((4).W))
    //   val ar_arsize = Output(UInt((3).W))
    //   val ar_araddr = Output(UInt((64).W))

    //   val b_bvalid = Input(Bool())
    //   val b_bready = Output(Bool())
    //   val b_bid = Input(UInt((8).W))

    //   val r_rvalid = Input(Bool())
    //   val r_rready = Output(Bool())
    //   val r_rid = Input(UInt((8).W))
    //   val r_rlast = Input(Bool())
    //   val r_rdata = Input(UInt((dataWidthAXI).W))
    // }) else None
    // cfg APB
    val io_psel = Input(Bool())
    val io_penable = Input(Bool())
    val io_pwrite = Input(Bool())
    val io_paddr = Input(UInt((32).W))
    val io_pwdata = Input(UInt((32).W))
    val io_prdata = Output(UInt((32).W))
    val io_pready = Output(Bool())
  })

  val makeStr = s"make -C generators/soDLA default SODLA_TYPE=${configName}"
  // val preproc = if (synthRAMs) makeStr + " NVDLA_RAMS=synth" else makeStr
  require (makeStr.! == 0, "Failed to run pre-processing step")

  addResource(s"/SO_${configName}.v")
}
