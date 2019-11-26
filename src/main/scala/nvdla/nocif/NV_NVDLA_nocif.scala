package nvdla

import chisel3._
import chisel3.util._


class NV_NVDLA_nocif(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        val pwrbus_ram_pd = Input(UInt(32.W))

        val csb2mcif = new csb2dp_if
        val csb2cvif = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(new csb2dp_if) else None

        val mcif2noc_axi_ar = DecoupledIO(new nocif_axi_rd_address_if)
        val mcif2noc_axi_aw = DecoupledIO(new nocif_axi_wr_address_if)
        val mcif2noc_axi_w = DecoupledIO(new nocif_axi_wr_data_if)
        val noc2mcif_axi_b = Flipped(DecoupledIO(new nocif_axi_wr_response_if))
        val noc2mcif_axi_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if))

        val cvif2noc_axi_ar = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_rd_address_if)) else None
        val cvif2noc_axi_aw = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_address_if)) else None
        val cvif2noc_axi_w = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_data_if)) else None
        val noc2cvif_axi_b = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_wr_response_if))) else None
        val noc2cvif_axi_r = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_data_if))) else None

        //client2mcif
        val client2mcif_rd_cdt_lat_fifo_pop = Input(Vec(conf.RDMA_NUM, Bool()))
        val client2mcif_rd_req_pd = Flipped(Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))))
        val mcif2client_rd_rsp_pd = Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))

        val client2mcif_wr_req_pd = Flipped(Vec(conf.WDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))))
        val mcif2client_wr_rsp_complete = Output(Vec(conf.WDMA_NUM, Bool()))

        //client2cvif
        val client2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Vec(conf.RDMA_NUM, Bool()))) else None
        val client2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))))) else None
        val cvif2client_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None

        val client2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(Vec(conf.WDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))))) else None
        val cvif2client_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Vec(conf.WDMA_NUM, Bool()))) else None
    
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
withReset(!io.nvdla_core_rstn){

    val u_mcif = Module(new NV_NVDLA_mcif)
    u_mcif.io.nvdla_core_clk := io.nvdla_core_clk
    u_mcif.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    io.csb2mcif <> u_mcif.io.csb2mcif

    io.mcif2noc_axi_ar <> u_mcif.io.mcif2noc_axi_ar
    io.mcif2noc_axi_aw <> u_mcif.io.mcif2noc_axi_aw
    io.mcif2noc_axi_w <> u_mcif.io.mcif2noc_axi_w
    u_mcif.io.noc2mcif_axi_b <> io.noc2mcif_axi_b
    u_mcif.io.noc2mcif_axi_r <> io.noc2mcif_axi_r

    u_mcif.io.client2mcif_rd_cdt_lat_fifo_pop := io.client2mcif_rd_cdt_lat_fifo_pop
    u_mcif.io.client2mcif_rd_req_pd <> io.client2mcif_rd_req_pd
    io.mcif2client_rd_rsp_pd <> u_mcif.io.mcif2client_rd_rsp_pd

    u_mcif.io.client2mcif_wr_req_pd <> io.client2mcif_wr_req_pd
    io.mcif2client_wr_rsp_complete := u_mcif.io.mcif2client_wr_rsp_complete

    val u_cvif = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Module(new NV_NVDLA_mcif)) else None
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_cvif.get.io.nvdla_core_clk := io.nvdla_core_clk
        u_cvif.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
        io.csb2cvif.get <> u_cvif.get.io.csb2mcif

        io.cvif2noc_axi_ar.get <> u_cvif.get.io.mcif2noc_axi_ar
        io.cvif2noc_axi_aw.get <> u_cvif.get.io.mcif2noc_axi_aw
        io.cvif2noc_axi_w.get <> u_cvif.get.io.mcif2noc_axi_w
        u_cvif.get.io.noc2mcif_axi_b <> io.noc2cvif_axi_b.get
        u_cvif.get.io.noc2mcif_axi_r <> io.noc2cvif_axi_r.get

        u_cvif.get.io.client2mcif_rd_cdt_lat_fifo_pop := io.client2cvif_rd_cdt_lat_fifo_pop.get
        u_cvif.get.io.client2mcif_rd_req_pd <> io.client2cvif_rd_req_pd.get
        io.cvif2client_rd_rsp_pd.get <> u_cvif.get.io.mcif2client_rd_rsp_pd

        u_cvif.get.io.client2mcif_wr_req_pd <> io.client2cvif_wr_req_pd.get
        io.cvif2client_wr_rsp_complete.get := u_cvif.get.io.mcif2client_wr_rsp_complete 
    }

        
}}

object NV_NVDLA_nocifDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_nocif())
}
