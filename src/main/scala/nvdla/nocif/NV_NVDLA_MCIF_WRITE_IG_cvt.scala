package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_WRITE_IG_cvt(conf:xxifConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //spt2cvt
        val spt2cvt_cmd_valid = Input(Bool())
        val spt2cvt_cmd_ready = Output(Bool())
        val spt2cvt_cmd_pd = Input(UInt(77.W))

        val spt2cvt_dat_valid = Input(Bool())
        val spt2cvt_dat_ready = Output(Bool())
        val spt2cvt_dat_pd = Input(UInt(514.W))

        //cq_wr
        val cq_wr_pvld = Output(Bool())
        val cq_wr_prdy = Input(Bool())
        val cq_wr_pd = Output(UInt(3.W))
        val cq_wr_thread_id = Output(UInt(3.W))

        //mcif2noc
        val mcif2noc_axi_aw_awvalid = Output(Bool())
        val mcif2noc_axi_aw_awready = Input(Bool())
        val mcif2noc_axi_aw_awaddr = Output(UInt(64.W))
        val mcif2noc_axi_aw_awid = Output(UInt(8.W))
        val mcif2noc_axi_aw_awlen = Output(UInt(4.W))

        val mcif2noc_axi_w_wvalid = Output(Bool())
        val mcif2noc_axi_w_wready = Input(Bool())
        val mcif2noc_axi_w_wdata = Output(UInt(512.W))
        val mcif2noc_axi_w_wstrb = Output(UInt(64.W))
        val mcif2noc_axi_w_wlast = Output(Bool())

        //eg2ig
        val eg2ig_axi_len = Input(UInt(2.W))
        val eg2ig_axi_vld = Input(Bool())
        
        val reg2dp_wr_os_cnt = Input(UInt(8.W))
        })

    withClock(io.nvdla_core_clk){
    }
}