
package nvdla

import chisel3._
import chisel3.util._


class NV_NVDLA_SDP_RDMA_dmaif(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
    //general clock
    val nvdla_core_clk = Input(Clock())

    //mcif
    val sdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
    val sdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())
    val mcif2sdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))
    
    //dma_rd
    val dma_rd_req_ram_type = Input(Bool())
    val dma_rd_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))

    val dma_rd_rsp_ram_type = Input(Bool())
    val dma_rd_rsp_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))
    val dma_rd_cdt_lat_fifo_pop = Input(Bool())

    val sdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))
                          else None
    val sdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool()))
                                       else None
    val cvif2sdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))))
                             else None

  })


withClock(io.nvdla_core_clk){
//==============
// DMA Interface
//==============
    // rd Channel: Request
    val nv_NVDLA_SDP_RDMA_rdreq = Module{new NV_NVDLA_DMAIF_rdreq(conf.NVDLA_DMA_RD_REQ)}
    nv_NVDLA_SDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.sdp2cvif_rd_req_pd.get <> nv_NVDLA_SDP_RDMA_rdreq.io.cvif_rd_req_pd.get
    }
    io.sdp2mcif_rd_req_pd <> nv_NVDLA_SDP_RDMA_rdreq.io.mcif_rd_req_pd
    nv_NVDLA_SDP_RDMA_rdreq.io.dmaif_rd_req_pd <> io.dma_rd_req_pd
    nv_NVDLA_SDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.dma_rd_req_ram_type

    // rd Channel: Response
    val nv_NVDLA_SDP_RDMA_rdrsp = Module{new NV_NVDLA_DMAIF_rdrsp(conf.NVDLA_DMA_RD_RSP)}
    nv_NVDLA_SDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        nv_NVDLA_SDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get <> io.cvif2sdp_rd_rsp_pd.get
    }
    nv_NVDLA_SDP_RDMA_rdrsp.io.mcif_rd_rsp_pd <> io.mcif2sdp_rd_rsp_pd

    io.dma_rd_rsp_pd <> nv_NVDLA_SDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd

    io.sdp2mcif_rd_cdt_lat_fifo_pop := RegNext(io.dma_rd_cdt_lat_fifo_pop & (io.dma_rd_rsp_ram_type === true.B), false.B)
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.sdp2cvif_rd_cdt_lat_fifo_pop.get := RegNext(io.dma_rd_cdt_lat_fifo_pop & (io.dma_rd_rsp_ram_type === false.B), false.B)
    }

}}


