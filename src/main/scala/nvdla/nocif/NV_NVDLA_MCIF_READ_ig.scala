package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_ig (implicit conf: cscConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        
        val pwrbus_ram_pd = Input(UInt(32.W))
        val reg2dp_rd_os_cnt = Input(UInt(8.W))

        //RDMA_NAME

        //mcif2noc
        val mcif2noc_axi_ar_arvalid = Output(Bool())
        val mcif2noc_axi_ar_arready = Input(Bool())
        val mcif2noc_axi_ar_arid = Output(UInt(8.W))
        val mcif2noc_axi_ar_arlen = Output(UInt(4.W))
        val mcif2noc_axi_ar_araddr = Output(UInt(NVDLA_MEM_ADDRESS_WIDTH.W))

        val eg2ig_axi_vld = Input(Bool())
        //if(UVDLA_PRIMARY_MEMIF_WIDTH > NVDLA_MEMORY_ATOMIC_WIDTH)
        //cq_wr
        val cq_wr_pvld = Output(Bool())
        val cq_wr_prdy = Input(Bool())
        val cq_wr_pd = Output(UInt(7.W))
        val cq_wr_thread_id = Output(UInt(4.W))
       
        })
    withClock(io.nvdla_core_clk){
}}
