package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_MCIF_WRITE_IG_arb (implicit conf:nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //bpt2arb
        val bpt2arb_cmd_valid = Input(Vec(conf.WDMA_NUM, Bool()))
        val bpt2arb_cmd_ready = Output(Vec(conf.WDMA_NUM, Bool()))
        val bpt2arb_cmd_pd = Input(Vec(conf.WDMA_NUM, UInt(77.W)))

        val bpt2arb_dat_valid = Input(Vec(conf.WDMA_NUM, Bool()))
        val bpt2arb_dat_ready = Output(Vec(conf.WDMA_NUM, Bool()))
        val bpt2arb_dat_pd = Input(Vec(conf.WDMA_NUM, UInt(514.W)))

        //arb2spt
        val arb2spt_cmd_valid = Output(Bool())
        val arb2spt_cmd_ready = Input(Bool())
        val arb2spt_cmd_pd = Output(UInt(77.W))

        val arb2spt_dat_valid = Output(Bool())
        val arb2spt_dat_ready = Input(Bool())
        val arb2spt_dat_pd = Output(UInt(514.W))


        val pwrbus_ram_pd = Input(UInt(32.W))

        val reg2dp_wr_weight_bdma = Input(UInt(8.W))
        val reg2dp_wr_weight_cdp = Input(UInt(8.W))
        val reg2dp_wr_weight_pdp = Input(UInt(8.W))
        val reg2dp_wr_weight_rbk = Input(UInt(8.W))
        val reg2dp_wr_weight_sdp = Input(UInt(8.W))
        })

    withClock(io.nvdla_core_clk){
    }
}
