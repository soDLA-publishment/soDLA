package nvdla

import chisel3._
import chisel3.experimental._

class NV_NVDLA_MCIF_READ_cq(implicit conf: nocifConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())
        //cq_wr
        val cq_wr_prdy = Output(Bool())
        val cq_wr_pvld = Input(Bool())
        val cq_wr_thread_id = Input(UInt(4.W))
        val cq_wr_pause = if(conf.FV_RAND_WR_PAUSE) Some(Input(Bool())) else None
        val cq_wr_pd = Input(UInt(7.W))

        val cq_rd_prdy = Input(Vec(conf.RDMA_NUM, Bool()))
        val cq_rd_pvld = Output(Vec(conf.RDMA_NUM, Bool()))
        val cq_rd_pd = Output(Vec(conf.RDMA_NUM, UInt(7.W)))

        val pwrbus_ram_pd = Input(UInt(32.W))
    })

    withClock(io.nvdla_core_clk){
    }
}





