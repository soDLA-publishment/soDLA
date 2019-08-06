
// package nvdla

// import chisel3._
// import chisel3.util._
// import chisel3.experimental._


// class NV_NVDLA_SDP_RDMA_dmaif(implicit conf: sdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//     //general clock
//     val nvdla_core_clk = Input(Clock())

//     //mcif
//     val sdp2mcif_rd_req_valid = Output(Bool())
//     val sdp2mcif_rd_req_ready = Input(Bool())
//     val sdp2mcif_rd_req_pd = Output(UInt(conf.NVDLA_DMA_RD_REQ.W))
//     val sdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())
//     val mcif2sdp_rd_rsp_valid  = Input(Bool())
//     val mcif2sdp_rd_rsp_ready = Output(Bool())
//     val mcif2sdp_rd_rsp_pd = Input(UInt(conf.NVDLA_DMA_RD_RSP.W))

//     val dma_rd_req_ram_type = Input(Bool())
//     val dma_rd_req_vld = Input(Bool())
//     val dma_rd_req_rdy = Output(Bool())
//     val dma_rd_req_pd = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))

//     val dma_rd_rsp_ram_type = Input(Bool())
//     val dma_rd_rsp_vld = Output(Bool())
//     val dma_rd_rsp_rdy = Input(Bool())
//     val dma_rd_rsp_pd = Output(UInt(conf.NVDLA_DMA_RD_RSP.W))
//     val dma_rd_cdt_lat_fifo_pop = Input(Bool())

//   })

//   val cvio = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(IO(new Bundle{ 
//     //cvif
//     val sdp2cvif_rd_req_valid = Output(Bool())
//     val sdp2cvif_rd_req_ready = Input(Bool())
//     val sdp2cvif_rd_req_pd = Output(UInt(conf.NVDLA_DMA_RD_REQ.W))
//     val sdp2cvif_rd_cdt_lat_fifo_pop = Output(Bool())
//     val cvif2sdp_rd_rsp_valid  = Input(Bool())
//     val cvif2sdp_rd_rsp_ready = Output(Bool())
//     val cvif2sdp_rd_rsp_pd = Input(UInt(conf.NVDLA_DMA_RD_RSP.W))
//   })

// withClock(io.nvdla_core_clk){
// //==============
// // DMA Interface
// //==============
//     // rd Channel: Request
//     val nv_NVDLA_SDP_RDMA_rdreq = Module{new NV_NVDLA_DMAIF_rdreq}

//     nv_NVDLA_SDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
//     nv_NVDLA_SDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.dma_rd_req_ram_type

//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         nv_NVDLA_SDP_RDMA_rdreq.io.cvif_rd_req_ready.get := cvio.sdp2cvif_rd_req_ready
//         io.sdp2cvif_rd_req_pd.get := nv_NVDLA_SDP_RDMA_rdreq.io.cvif_rd_req_pd.get
//         io.sdp2cvif_rd_req_valid.get := nv_NVDLA_SDP_RDMA_rdreq.io.cvif_rd_req_valid.get
//     }

//     io.sdp2mcif_rd_req_pd:= nv_NVDLA_SDP_RDMA_rdreq.io.mcif_rd_req_pd
//     io.sdp2mcif_rd_req_valid := nv_NVDLA_SDP_RDMA_rdreq.io.mcif_rd_req_valid
//     nv_NVDLA_SDP_RDMA_rdreq.io.mcif_rd_req_ready := io.sdp2mcif_rd_req_ready

//     nv_NVDLA_SDP_RDMA_rdreq.io.dmaif_rd_req_pd := io.dma_rd_req_pd
//     nv_NVDLA_SDP_RDMA_rdreq.io.dmaif_rd_req_vld := io.dma_rd_req_vld
//     io.dma_rd_req_rdy := nv_NVDLA_SDP_RDMA_rdreq.io.dmaif_rd_req_rdy


//     // rd Channel: Response
//     val nv_NVDLA_SDP_RDMA_rdrsp = Module{new NV_NVDLA_DMAIF_rdrsp}

//     nv_NVDLA_SDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk

//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         nv_NVDLA_SDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get := io.cvif2sdp_rd_rsp_pd.get
//         nv_NVDLA_SDP_RDMA_rdrsp.io.cvif_rd_rsp_valid.get := io.cvif2sdp_rd_rsp_valid.get
//         io.cvif2sdp_rd_rsp_ready.get := nv_NVDLA_SDP_RDMA_rdrsp.io.cvif_rd_rsp_ready.get
//     }

//     nv_NVDLA_SDP_RDMA_rdrsp.io.mcif_rd_rsp_pd := io.mcif2sdp_rd_rsp_pd
//     nv_NVDLA_SDP_RDMA_rdrsp.io.mcif_rd_rsp_valid := io.mcif2sdp_rd_rsp_valid
//     io.mcif2sdp_rd_rsp_ready := nv_NVDLA_SDP_RDMA_rdrsp.io.mcif_rd_rsp_ready

//     io.dma_rd_rsp_pd := nv_NVDLA_SDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd
//     io.dma_rd_rsp_vld := nv_NVDLA_SDP_RDMA_rdrsp.io.dmaif_rd_rsp_pvld
//     nv_NVDLA_SDP_RDMA_rdrsp.io.dmaif_rd_rsp_prdy := io.dma_rd_rsp_rdy

//     io.sdp2mcif_rd_cdt_lat_fifo_pop := RegNext(io.dma_rd_cdt_lat_fifo_pop & (io.dma_rd_rsp_ram_type === true.B), false.B)
//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         io.sdp2cvif_rd_cdt_lat_fifo_pop.get := RegNext(io.dma_rd_cdt_lat_fifo_pop & (io.dma_rd_rsp_ram_type === false.B), false.B)
//     }

// }}

// object NV_NVDLA_SDP_RDMA_dmaifDriver extends App {
//   implicit val conf: sdpConfiguration = new sdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_dmaif())
// }



