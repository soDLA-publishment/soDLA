package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_MCIF_WRITE_ig (implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        val pwrbus_ram_pd = Input(UInt(32.W))
        val reg3dp_wr_os_cnt = Input(UInt(8.W))
        val eg2ig_axi_len = Input(UInt(2.W))
        val eg2ig_axi_vld = Input(Bool())

        //cq_wr
        val cq_wr_pvld = Output(Bool())
        val cq_wr_prdy = Input(Bool())
        val cq_wr_thread_id = Output(UInt(3.W))
        val cq_wr_pd = Output(UInt(3.W))

        //mcif2noc
        val mcif2noc_axi_aw_awvalid = Output(Bool())
        val mcif2noc_axi_aw_awready = Input(Bool())
        val mcif2noc_axi_aw_awid = Output(UInt(8.W))
        val mcif2noc_axi_aw_awlen = Output(UInt(4.W))
        val mcif2noc_axi_aw_awaddr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
        
        val mcif2noc_axi_w_wvalid = Output(Bool())
        val mcif2noc_axi_w_wready = Input(Bool())
        val mcif2noc_axi_w_wdata = Output(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))
        val mcif2noc_axi_w_wstrb = Output(UInt(conf.NVDLA_PRIMARY_MEMIF_STRB.W))
        val mcif2noc_axi_w_wlast = Output(Bool())

        //WDMA_NAME 
        })
    withClock(io.nvdla_core_clk){
}}   