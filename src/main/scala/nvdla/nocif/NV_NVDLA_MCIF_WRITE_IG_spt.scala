package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_WRITE_IG_spt(conf:nvdlaConfig) extends Module {
  val io = IO(new Bundle {
    //general clock
    val nvdla_core_clk = Input(Clock())
    val nvdla_core_rstn = Input(Bool())

    val arb2spt_cmd_pd  = Flipped(DecoupledIO(UInt(77.W)))

    val arb2spt_dat_pd = Flipped(DecoupledIO(UInt(514.W)))

    val spt2cvt_cmd_pd = DecoupledIO(UInt(77.W))

    val spt2cvt_dat_pd = DecoupledIO(UInt(514.W))

    val pwrbus_ram_pd = Input(UInt(32.W))
  })



}
