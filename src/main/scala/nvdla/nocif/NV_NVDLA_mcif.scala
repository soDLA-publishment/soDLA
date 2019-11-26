package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_mcif(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val csb2mcif = new csb2dp_if

        val mcif2noc_axi_ar = DecoupledIO(new nocif_axi_rd_address_if)
        val mcif2noc_axi_aw = DecoupledIO(new nocif_axi_wr_address_if)
        val mcif2noc_axi_w = DecoupledIO(new nocif_axi_wr_data_if)
        val noc2mcif_axi_b = Flipped(DecoupledIO(new nocif_axi_wr_response_if))
        val noc2mcif_axi_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if))

        //client2mcif
        val client2mcif_rd_cdt_lat_fifo_pop = Input(Vec(conf.RDMA_NUM, Bool()))
        val client2mcif_rd_req_pd = Flipped(Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))))
        val mcif2client_rd_rsp_pd = Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))

        val client2mcif_wr_req_pd = Flipped(Vec(conf.WDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))))
        val mcif2client_wr_rsp_complete = Output(Vec(conf.WDMA_NUM, Bool()))
    })
//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │          
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘
withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
    val u_csb = Module(new NV_NVDLA_MCIF_csb)
    val u_read = Module(new NV_NVDLA_MCIF_read)
    val u_write = Module(new NV_NVDLA_MCIF_write)
    //===========================================
    // NV_NVDLA_MCIF_csb
    //===========================================
    u_csb.io.nvdla_core_clk := io.nvdla_core_clk
    //csb2mcif
    io.csb2mcif <> u_csb.io.csb2mcif
    //reg2dp
    u_csb.io.dp2reg_idle := true.B
    val field = u_csb.io.reg2dp_field
    
    //===========================================
    // NV_NVDLA_MCIF_read
    //===========================================
    u_read.io.nvdla_core_clk := io.nvdla_core_clk
    u_read.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    //client2mcif
    u_read.io.client2mcif_rd_cdt_lat_fifo_pop := io.client2mcif_rd_cdt_lat_fifo_pop
    u_read.io.client2mcif_rd_req_pd <> io.client2mcif_rd_req_pd
    io.mcif2client_rd_rsp_pd <> u_read.io.mcif2client_rd_rsp_pd
    //noc2mcif
    //mcif2noc
    u_read.io.noc2mcif_axi_r <> io.noc2mcif_axi_r
    io.mcif2noc_axi_ar <> u_read.io.mcif2noc_axi_ar
    //config
    u_read.io.reg2dp_rd_os_cnt := field.rd_os_cnt
    for(i <- 0 to conf.RDMA_NUM-1){
        u_read.io.reg2dp_rd_weight(i) := field.rd_weight_client(conf.arr_tieoff_axid(i))
    }

    //===========================================
    // NV_NVDLA_MCIF_write
    //===========================================
    u_write.io.nvdla_core_clk := io.nvdla_core_clk
    u_write.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_write.io.client2mcif_wr_req_pd <> io.client2mcif_wr_req_pd
    io.mcif2client_wr_rsp_complete := u_write.io.mcif2client_wr_rsp_complete

    //noc2mcif
    u_write.io.noc2mcif_axi_b <> io.noc2mcif_axi_b
    io.mcif2noc_axi_aw <> u_write.io.mcif2noc_axi_aw
    io.mcif2noc_axi_w <> u_write.io.mcif2noc_axi_w
    u_write.io.reg2dp_wr_os_cnt := field.wr_os_cnt
    for(i <- 0 to conf.WDMA_NUM-1){
        u_write.io.reg2dp_wr_weight(i) := field.wr_weight_client(conf.awr_tieoff_axid(i))
    }
        
}}

object NV_NVDLA_mcifDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_mcif())
}
