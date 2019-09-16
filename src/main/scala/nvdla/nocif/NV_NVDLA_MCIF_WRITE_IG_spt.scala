package nvdla

import chisel3._

class NV_NVDLA_MCIF_WRITE_IG_spt(conf:xxifConfiguration) extends Module {
  val io = IO(new Bundle {
    //general clock
    val nvdla_core_clk = Input(Clock())
    val nvdla_core_rstn = Input(Bool())

    val arb2spt_cmd_valid = Input(Bool())
    val arb2spt_cmd_ready = Output(Bool())
    val arb2spt_cmd_pd  = Input(UInt(77.W))

    val arb2spt_dat_valid = Input(Bool())  /* data valid */
    val arb2spt_dat_ready = Output(Bool())  /* data return handshake */
    val arb2spt_dat_pd = Input(UInt(514.W))

    val spt2cvt_cmd_valid = Output(Bool())  /* data valid */
    val spt2cvt_cmd_ready = Input(Bool())  /* data return handshake */
    val spt2cvt_cmd_pd = Output(UInt(77.W))

    val spt2cvt_dat_valid = Output(Bool())  /* data valid */
    val spt2cvt_dat_ready = Input(Bool())  /* data return handshake */
    val spt2cvt_dat_pd = Input(UInt(514.W))

    val pwrbus_ram_pd = Input(UInt(32.W))
  })



}
