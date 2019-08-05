package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_eg(implicit conf: cscConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())

        //RDMA_NAME mcif2rd

        //pwrbus
        val pwrbus_ram_pd = Input(UInt(32.W))
        
        val eg2ig_axi_vld = Output(Bool())
        
        //if(NVDLA_PRIMARY_MEMIF_WIDTH > NVDLA_MEMORY_ATOMIC_WIDTH)
        //cq_rd
        val cq_rd_pvld = Input(Vec(conf.RDMA_NUM, Bool()))
        val cq_rd_prdy = Output(Vec(conf.RDMA_NUM, Bool()))
        val cq_rd_pd = Input(Vec(conf.RDMA_NUM, UInt(7.W))

        //RDMA_NAME mcif2rd_rsp

        //noc2mcif
        val noc2mcif_axi_r_rvalid = Input(Bool())
        val noc2mcif_axi_r_rready = Output(Bool())
        val noc2mcif_axi_r_rid = Input(UInt(8.W))
        val noc2mcif_axi_r_rlast = Input(Bool())
        val noc2mcif_axi_r_rdata = Input(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))
        })
    withClock(io.nvdla_core_clk){
}}



