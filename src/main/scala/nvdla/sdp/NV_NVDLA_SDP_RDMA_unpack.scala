
// package nvdla

// import chisel3._
// import chisel3.util._
// import chisel3.experimental._


// class (implicit conf: sdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//     //general clock

//   })


// withClock(io.nvdla_core_clk){
// //==============
// // DMA Interface
// //==============
//     // rd Channel: Request
//     val nv_NVDLA_PDP_RDMA_rdreq = Module{new NV_NVDLA_DMAIF_rdreq}

//     nv_NVDLA_PDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
//     nv_NVDLA_PDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.dma_rd_req_ram_type

//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         nv_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_ready.get := io.sdp2cvif_rd_req_ready.get
//         io.sdp2cvif_rd_req_pd.get := nv_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_pd.get
//         io.sdp2cvif_rd_req_valid.get := nv_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_valid.get
//     }

//     io.sdp2mcif_rd_req_pd:= nv_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_pd
//     io.sdp2mcif_rd_req_valid := nv_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_valid
//     nv_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_ready := io.sdp2mcif_rd_req_ready

//     nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd := io.dma_rd_req_pd
//     nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_vld := io.dma_rd_req_vld
//     io.dma_rd_req_rdy := nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_rdy


//     // rd Channel: Response
//     val nv_NVDLA_PDP_RDMA_rdrsp = Module{new NV_NVDLA_DMAIF_rdrsp}

//     nv_NVDLA_PDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk

//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get := io.cvif2sdp_rd_rsp_pd.get
//         nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_valid.get := io.cvif2sdp_rd_rsp_valid.get
//         io.cvif2sdp_rd_rsp_ready.get := nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_ready.get
//     }

//     nv_NVDLA_PDP_RDMA_rdrsp.io.mcif_rd_rsp_pd := io.mcif2sdp_rd_rsp_pd
//     nv_NVDLA_PDP_RDMA_rdrsp.io.mcif_rd_rsp_valid := io.mcif2sdp_rd_rsp_valid
//     io.mcif2cdma_wt_rd_rsp_ready := nv_NVDLA_PDP_RDMA_rdrsp.io.mcif2sdp_rd_rsp_ready

//     io.dma_rd_rsp_pd := nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd
//     io.dma_rd_rsp_vld := nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pvld
//     nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_prdy := io.dma_rd_rsp_rdy

//     io.sdp2mcif_rd_cdt_lat_fifo_pop := RegNext(io.dma_rd_cdt_lat_fifo_pop & (io.dma_rd_rsp_ram_type === true.B), false.B)
//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         io.sdp2cvif_rd_cdt_lat_fifo_pop.get := RegNext(io.dma_rd_cdt_lat_fifo_pop & (io.dma_rd_rsp_ram_type === false.B), false.B)
//     }

// }}




