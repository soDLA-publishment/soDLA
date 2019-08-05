package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_MCIF_WRITE_IG_arb extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))
        
        //arb2spt
        val arb2spt_cmd_valid = Output(Bool())
        val arb2spt_cmd_ready = Input(Bool())
        val arb2spt_cmd_pd = Output(UInt(conf.NVDLA_DMA_WR_IG_PW.W))
        val arb2spt_dat_valid = OutputR(Bool())
        val arb2spt_dat_ready = Input(Bool())
        val arb2spt_dat_pd = Output(UInt((conf.NVDLA_DMA_WR_REQ-1).W))

        //bpt2arb
        val bpt2arb_cmd_valid = Input(Vec(conf.WDMA_NUM, Bool()))
        val bpt2arb_cmd_ready = Output(Vec(conf.WDMA_NUM, Bool()))
        val bpt2arb_cmd_pd = Input(Vec(conf.WDMA_NUM, UInt(conf.NVDLA_DMA_WR_IG_PW.W)))
        val bpt2arb_dat_valid = Input(Vec(conf.WDMA_NUM, Bool()))
        val bpt2arb_dat_ready = Output(Vec(conf.WDMA_NUM, Bool()))
        val bpt2arb_dat_pd = Input(Vec(conf.WDMA_NUM, UInt((conf.NVDLA_DMA_WR_REQ-1).W)))
        
        val reg2dp_wr_weight = Input(Vec(conf.WDMA_NUM, UInt(8.W)))

        })
    withClock(io.nvdla_core_clk){
}}   
