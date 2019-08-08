package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_MCIF_WRITE_eg extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        //cq_rd
        val cq_rd_pvld = Input(Vec(conf.WDMA_MAX_NUM, Bool()))
        val cq_rd_prdy = Output(Vec(conf.WDMA_MAX_NUM, Bool()))
        val cq_rd_pd = Input(Vec(conf.WDMA_MAX_NUM, UInt(3.W)))

        //eq2ig
        val eq2ig_axi_len = Output(UInt(2.W))
        val eq2ig_axi_vld = Output(Bool())

        //noc2mcif
        val noc2mcif_axi_b_bvalid = Input(Bool())
        val noc2mcif_axi_b_bready = Output(Bool())
        val noc2mcif_axi_b_bid = Input(UInt(8.W))
        })
    withClock(io.nvdla_core_clk){
}}    