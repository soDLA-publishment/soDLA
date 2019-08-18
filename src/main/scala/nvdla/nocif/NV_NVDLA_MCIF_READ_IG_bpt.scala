package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_IG_bpt(implicit conf: nocifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //dma2bpt
        val dma2bpt_req_valid = Input(Bool())
        val dma2bpt_req_ready = Output(Bool())
        val dma2bpt_req_pd = Input(UInt(79.W))
        val dma2bpt_cdt_lat_fifo_pop = Input(Bool())

        //bpt2arb
        val bpt2arb_req_valid = Output(Bool())
        val bpt2arb_req_ready = Input(Bool())
        val bpt2arb_req_pd = Output(UInt(75.W))

        val tieoff_axid = Input(UInt(4.W))
        val tieoff_lat_fifo_depth = Input(UInt(8.W))
    })

    withClock(io.nvdla_core_clk){
    }
}
