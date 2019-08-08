package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_IG_bpt(implicit conf: cscConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())

        //dma2bpt
        val dma2bpt_req_valid = Input(Bool())
        val dma2bpt_req_ready = Output(Bool())
        val dma2bpt_req_pd = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))
        val dma2bpt_cdt_lat_fifo_pop = Input(Bool())

        //bpt2arb
        val bpt2arb_req_valid = Output(Bool())
        val bpt2arb_req_ready = Input(Bool())
        val bpt2arb_req_pd = Output(UInt(conf.NVDLA_DMA_RD_IG_PW.W))
        val tieoff_axid = Input(UInt(4.W))
        val tieoff_lat_fifo_depth = Input(UInt(9.W))

    })
    withClock(io.nvdla_core_clk){
}}
