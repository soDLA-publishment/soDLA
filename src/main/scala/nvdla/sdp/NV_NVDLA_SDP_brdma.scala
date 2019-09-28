// package nvdla

// import chisel3._
// import chisel3.util._
// import chisel3.experimental._

// class NV_NVDLA_SDP_brdma(implicit conf: nvdlaConfig) extends Module {

// val io = IO(new Bundle {
//     //in clock
//     val nvdla_clock = Flipped(new nvdla_clock_if)
//     val pwrbus_ram_pd = Input(UInt(32.W))
//     val brdma_slcg_op_en = Input(Bool())
//     val brdma_disable = Input(Bool())

//     //mcif
//     val sdp_b2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
//     val mcif2sdp_b_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))
//     val sdp_b2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

//     //cvif
//     val sdp_b2cvif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
//     val cvif2sdp_b_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))
//     val sdp_b2cvif_rd_cdt_lat_fifo_pop = Output(Bool())

//     //sdp_brdma2dp
//     val sdp_brdma2dp_alu_pd = DecoupledIO(UInt((conf.AM_DW2+1).W))
//     val sdp_brdma2dp_mul_pd = DecoupledIO(UInt((conf.AM_DW2+1).W))

//     val reg2dp_brdma_data_mode = Input(Bool())  
//     val reg2dp_brdma_data_size = Input(Bool())  
//     val reg2dp_brdma_data_use = Input(UInt(2.W))  
//     val reg2dp_brdma_ram_type = Input(Bool())  
//     val reg2dp_bs_base_addr_high = Input(UInt(32.W))  
//     val reg2dp_bs_base_addr_low = Input(UInt((32-conf.AM_AW).W))  
//     val reg2dp_bs_line_stride = Input(UInt((32-conf.AM_AW).W))   
//     val reg2dp_bs_surface_stride = Input(UInt((32-conf.AM_AW).W))
//     val reg2dp_batch_number = Input(UInt(5.W))  
//     val reg2dp_channel = Input(UInt(13.W))  
//     val reg2dp_height = Input(UInt(13.W))  
//     val reg2dp_op_en = Input(Bool())  
//     val reg2dp_out_precision = Input(UInt(2.W)) 
//     val reg2dp_perf_dma_en = Input(Bool())  
//     val reg2dp_proc_precision = Input(UInt(2.W))  
//     val reg2dp_width = Input(UInt(13.W))  
//     val reg2dp_winograd = Input(Bool())  
//     val dp2reg_brdma_stall = Output(UInt(32.W))   
//     val dp2reg_done = Output(Bool())  

// })
//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │
//     //       │                 │
//     //       └───┐         ┌───┘
//     //           │         │
//     //           │         │
//     //           │         │
//     //           │         └──────────────┐
//     //           │                        │
//     //           │                        ├─┐
//     //           │                        ┌─┘    
//     //           │                        │
//     //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //             │ ─┤ ─┤       │ ─┤ ─┤         
//     //             └──┴──┘       └──┴──┘ 
// withClock(io.nvdla_core_clk){
//     // Layer Switch
//     val layer_process = RegInit(false.B)
//     val eg_done = Wire(Bool())
//     val op_load = io.reg2dp_op_en & !layer_process
//     when(op_load){
//         layer_process := true.B
//     }
//     .elsewhen(eg_done){
//         layer_process := false.B
//     }

//     //=======================================
//     val u_gate = Module(new NV_NVDLA_slcg(1, true))
//     u_gate.io.nvdla_clk := io.nvdla_clk
//     u_gate.io.slcg_disable.get := io.brdma_disable
//     u_gate.io.slcg_en(0) := io.brdma_slcg_op_en
//     val nvdla_gated_clk = u_gate.io.nvdla_gated_clk 

//     val ig2cq_prdy = Wire(Bool())
//     val dma_rd_req_rdy = Wire(Bool())

//     val u_ig = Module(new NV_NVDLA_SDP_RDMA_ig)
//     u_ig.io.nvdla_core_clk := nvdla_gated_clk
//     u_ig.io.op_load := op_load
//     u_ig.io.reg2dp_op_en := io.reg2dp_op_en
//     u_ig.io.reg2dp_perf_dma_en := io.reg2dp_perf_dma_en
//     u_ig.io.reg2dp_winograd := io.reg2dp_winograd
//     u_ig.io.reg2dp_channel := io.reg2dp_channel
//     u_ig.io.reg2dp_height := io.reg2dp_height
//     u_ig.io.reg2dp_width := io.reg2dp_width
//     u_ig.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_ig.io.reg2dp_rdma_data_mode := io.reg2dp_brdma_data_mode
//     u_ig.io.reg2dp_rdma_data_size := io.reg2dp_brdma_data_size
//     u_ig.io.reg2dp_rdma_data_use := io.reg2dp_brdma_data_use
//     u_ig.io.reg2dp_base_addr_high := io.reg2dp_bs_base_addr_high
//     u_ig.io.reg2dp_base_addr_low := io.reg2dp_bs_base_addr_low
//     u_ig.io.reg2dp_line_stride := io.reg2dp_bs_line_stride
//     u_ig.io.reg2dp_surface_stride := io.reg2dp_bs_surface_stride
//     io.dp2reg_brdma_stall := u_ig.io.dp2reg_rdma_stall

//     val u_cq = Module(new NV_NVDLA_fifo(depth = conf.NVDLA_VMOD_SDP_BRDMA_LATENCY_FIFO_DEPTH, width = 16, distant_wr_req = false, ram_type = 2))
//     u_cq.io.clk := nvdla_gated_clk
//     u_cq.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     u_cq.io.wr_pvld := u_ig.io.ig2cq_pd.valid
//     u_ig.io.ig2cq_pd.ready := u_cq.io.wr_prdy
//     u_cq.io.wr_pd := u_ig.io.ig2cq_pd.bits


//     val u_eg = Module(new NV_NVDLA_SDP_RDMA_eg)
//     u_eg.io.nvdla_core_clk := nvdla_gated_clk
//     u_eg.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_eg.io.op_load := op_load 
//     eg_done := u_eg.io.eg_done
    
//     u_eg.io.cq2eg_pd.valid := u_cq.io.cq2eg_pvld
//     u_cq.io.cq2eg_prdy := u_eg.io.cq2eg_pd.ready
//     u_eg.io.cq2eg_pd.bits := u_cq.io.cq2eg_pd

//     io.sdp_brdma2dp_alu_pd <> u_eg.io.sdp_rdma2dp_alu_pd
//     io.sdp_brdma2dp_mul_pd <> u_eg.io.sdp_rdma2dp_mul_pd

//     u_eg.io.reg2dp_batch_number := io.reg2dp_batch_number
//     u_eg.io.reg2dp_channel := io.reg2dp_channel
//     u_eg.io.reg2dp_height := io.reg2dp_height
//     u_eg.io.reg2dp_width := io.reg2dp_width
//     u_eg.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_eg.io.reg2dp_out_precision := io.reg2dp_out_precision
//     u_eg.io.reg2dp_rdma_data_mode := io.reg2dp_brdma_data_mode
//     u_eg.io.reg2dp_rdma_data_size := io.reg2dp_brdma_data_size
//     u_eg.io.reg2dp_rdma_data_use := io.reg2dp_brdma_data_use


//     val u_lat_fifo = Module(new NV_NVDLA_fifo(depth = conf.NVDLA_VMOD_SDP_BRDMA_LATENCY_FIFO_DEPTH, width = conf.NVDLA_DMA_RD_RSP, distant_wr_req = false, ram_type = 2))
//     u_lat_fifo.io.clk := nvdla_gated_clk
//     u_lat_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_eg.io.lat_fifo_rd_pd.valid := u_lat_fifo.io.rd_pvld
//     u_lat_fifo.io.rd_prdy := u_eg.io.lat_fifo_rd_pd.ready
//     u_eg.io.lat_fifo_rd_pd.bits := u_lat_fifo.io.rd_pd

//     val u_NV_NVDLA_SDP_RDMA_dmaif = Module(new NV_NVDLA_SDP_RDMA_dmaif)
//     u_NV_NVDLA_SDP_RDMA_dmaif.io.nvdla_core_clk := nvdla_gated_clk
//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         io.sdp_b2cvif_rd_req_pd.get <> u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_req_pd.get
//         io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
//         u_NV_NVDLA_SDP_RDMA_dmaif.io.cvif2sdp_rd_rsp_pd.get <> io.cvif2sdp_b_rd_rsp_pd.get
//     }
//     io.sdp_b2mcif_rd_req_pd <> u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2mcif_rd_req_pd
//     io.sdp_b2mcif_rd_cdt_lat_fifo_pop := u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2mcif_rd_cdt_lat_fifo_pop
//     u_NV_NVDLA_SDP_RDMA_dmaif.io.mcif2sdp_rd_rsp_pd <> io.mcif2sdp_b_rd_rsp_pd

//     u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_req_ram_type := io.reg2dp_brdma_ram_type
//     u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_req_pd <> u_ig.io.dma_rd_req_pd

//     u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_ram_type := io.reg2dp_brdma_ram_type
//     u_lat_fifo.io.wr_pvld := u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_pd.valid
//     u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_pd.ready := u_lat_fifo.io.wr_prdy
//     u_lat_fifo.io.wr_pd := u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_pd.bits
//     u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_cdt_lat_fifo_pop := u_eg.io.dma_rd_cdt_lat_fifo_pop

// }}

// object NV_NVDLA_SDP_brdmaDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_brdma())
// }



