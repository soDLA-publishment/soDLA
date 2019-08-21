package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_eg(implicit conf: nocifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))
        val eg2ig_axi_vld = Output(Bool())

        val mcif2rdma_rd_rsp_valid          = Output(Vec(conf.RDMA_NUM, Bool()))
        val mcif2rdma_rd_rsp_ready          = Input(Vec(conf.RDMA_NUM, Bool()))
        val mcif2rdma_rd_rsp_pd             = Output(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_RSP.W)))

        //noc2mcif
        val noc2mcif_axi_r_rvalid = Input(Bool())
        val noc2mcif_axi_r_rready = Output(Bool())
        val noc2mcif_axi_r_rid = Input(UInt(8.W))
        val noc2mcif_axi_r_rlast = Input(Bool())
        val noc2mcif_axi_r_rdata = Input(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))
    })

    withClock(io.nvdla_core_clk){
    }
}



