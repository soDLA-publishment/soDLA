package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_SDP_nrdma(implicit conf: nvdlaConfig) extends Module {

val io = IO(new Bundle {
    //in clock
    val nvdla_clock = Flipped(new nvdla_clock_if)
    val nrdma_slcg_op_en = Input(Bool())
    val nrdma_disable = Input(Bool())
    val pwrbus_ram_pd = Input(UInt(32.W))

    //cvif
    val sdp_n2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
    val cvif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None
    val sdp_n2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

    //mcif
    val sdp_n2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
    val mcif2sdp_n_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))
    val sdp_n2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

    val sdp_nrdma2dp_alu_pd = DecoupledIO(UInt((conf.AM_DW2+1).W))
    val sdp_nrdma2dp_mul_pd = DecoupledIO(UInt((conf.AM_DW2+1).W))

    val reg2dp_nrdma_data_mode = Input(Bool())  
    val reg2dp_nrdma_data_size = Input(Bool())  
    val reg2dp_nrdma_data_use = Input(UInt(2.W))  
    val reg2dp_nrdma_ram_type = Input(Bool())  
    val reg2dp_bn_base_addr_high = Input(UInt(32.W))  
    val reg2dp_bn_base_addr_low = Input(UInt((32-conf.AM_AW).W))  
    val reg2dp_bn_line_stride = Input(UInt((32-conf.AM_AW).W))   
    val reg2dp_bn_surface_stride = Input(UInt((32-conf.AM_AW).W))
    val reg2dp_batch_number = Input(UInt(5.W))  
    val reg2dp_channel = Input(UInt(13.W))  
    val reg2dp_height = Input(UInt(13.W))  
    val reg2dp_op_en = Input(Bool())  
    val reg2dp_out_precision = Input(UInt(2.W)) 
    val reg2dp_perf_dma_en = Input(Bool())  
    val reg2dp_proc_precision = Input(UInt(2.W))  
    val reg2dp_width = Input(UInt(13.W))  
    val reg2dp_winograd = Input(Bool())  
    val dp2reg_nrdma_stall = Output(UInt(32.W))   
    val dp2reg_done = Output(Bool())  
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
withClock(io.nvdla_clock.nvdla_core_clk){
    // Layer Switch
    val layer_process = RegInit(false.B)
    val eg_done = Wire(Bool())
    val op_load = io.reg2dp_op_en & !layer_process
    when(op_load){
        layer_process := true.B
    }
    .elsewhen(eg_done){
        layer_process := false.B
    }

    io.dp2reg_done := eg_done

    //=======================================
    val u_gate = Module(new NV_NVDLA_slcg(1, true))
    u_gate.io.nvdla_clock <> io.nvdla_clock
    u_gate.io.slcg_disable.get := io.nrdma_disable
    u_gate.io.slcg_en(0) := io.nrdma_slcg_op_en
    val nvdla_gated_clk = u_gate.io.nvdla_core_gated_clk 

     //=======================================
    // Ingress: send read request to external mem
    //---------------------------------------
    val u_ig = Module(new NV_NVDLA_SDP_RDMA_ig)
    u_ig.io.nvdla_core_clk := nvdla_gated_clk
    u_ig.io.op_load := op_load

    u_ig.io.reg2dp_op_en := io.reg2dp_op_en
    u_ig.io.reg2dp_perf_dma_en := io.reg2dp_perf_dma_en
    u_ig.io.reg2dp_winograd := io.reg2dp_winograd
    u_ig.io.reg2dp_channel := io.reg2dp_channel
    u_ig.io.reg2dp_height := io.reg2dp_height
    u_ig.io.reg2dp_width := io.reg2dp_width
    u_ig.io.reg2dp_proc_precision := io.reg2dp_proc_precision
    u_ig.io.reg2dp_rdma_data_mode := io.reg2dp_nrdma_data_mode
    u_ig.io.reg2dp_rdma_data_size := io.reg2dp_nrdma_data_size
    u_ig.io.reg2dp_rdma_data_use := io.reg2dp_nrdma_data_use
    u_ig.io.reg2dp_base_addr_high := io.reg2dp_bn_base_addr_high
    u_ig.io.reg2dp_base_addr_low := io.reg2dp_bn_base_addr_low
    u_ig.io.reg2dp_line_stride := io.reg2dp_bn_line_stride
    u_ig.io.reg2dp_surface_stride := io.reg2dp_bn_surface_stride
    io.dp2reg_nrdma_stall := u_ig.io.dp2reg_rdma_stall


    //=======================================
    // Context Queue: trace outstanding req, and pass info from Ig to Eg
    //---------------------------------------
    val u_cq = Module(new NV_NVDLA_fifo(depth = conf.NVDLA_VMOD_SDP_BRDMA_LATENCY_FIFO_DEPTH, width = 16, distant_wr_req = false, ram_type = 2))
    u_cq.io.clk := nvdla_gated_clk
    u_cq.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_cq.io.wr_pvld := u_ig.io.ig2cq_pd.valid
    u_ig.io.ig2cq_pd.ready  := u_cq.io.wr_prdy
    u_cq.io.wr_pd := u_ig.io.ig2cq_pd.bits

    //=======================================
    // Egress: get return data from external mem
    //---------------------------------------
    val u_eg = Module(new NV_NVDLA_SDP_RDMA_eg)
    u_eg.io.nvdla_core_clk := nvdla_gated_clk
    u_eg.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_eg.io.op_load := op_load 
    eg_done := u_eg.io.eg_done

    u_eg.io.cq2eg_pd.valid := u_cq.io.rd_pvld
    u_cq.io.rd_prdy := u_eg.io.cq2eg_pd.ready
    u_eg.io.cq2eg_pd.bits := u_cq.io.rd_pd
    
    io.sdp_nrdma2dp_alu_pd <> u_eg.io.sdp_rdma2dp_alu_pd
    io.sdp_nrdma2dp_mul_pd <> u_eg.io.sdp_rdma2dp_mul_pd
    u_eg.io.reg2dp_batch_number := io.reg2dp_batch_number
    u_eg.io.reg2dp_channel := io.reg2dp_channel
    u_eg.io.reg2dp_height := io.reg2dp_height
    u_eg.io.reg2dp_width := io.reg2dp_width
    u_eg.io.reg2dp_proc_precision := io.reg2dp_proc_precision
    u_eg.io.reg2dp_out_precision := io.reg2dp_out_precision
    u_eg.io.reg2dp_rdma_data_mode := io.reg2dp_nrdma_data_mode
    u_eg.io.reg2dp_rdma_data_size := io.reg2dp_nrdma_data_size
    u_eg.io.reg2dp_rdma_data_use := io.reg2dp_nrdma_data_use

    val u_lat_fifo = Module(new NV_NVDLA_fifo(depth = conf.NVDLA_VMOD_SDP_NRDMA_LATENCY_FIFO_DEPTH, width = conf.NVDLA_DMA_RD_RSP, distant_wr_req = false, ram_type = 2))
    u_lat_fifo.io.clk := nvdla_gated_clk
    u_lat_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    
    u_eg.io.lat_fifo_rd_pd.valid := u_lat_fifo.io.rd_pvld
    u_lat_fifo.io.rd_prdy := u_eg.io.lat_fifo_rd_pd.ready
    u_eg.io.lat_fifo_rd_pd.bits := u_lat_fifo.io.rd_pd

    val u_NV_NVDLA_SDP_RDMA_dmaif = Module(new NV_NVDLA_SDP_RDMA_dmaif)
    u_NV_NVDLA_SDP_RDMA_dmaif.io.nvdla_core_clk := nvdla_gated_clk
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.sdp_n2cvif_rd_req_pd.get <> u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_req_pd.get
        io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
        u_NV_NVDLA_SDP_RDMA_dmaif.io.cvif2sdp_rd_rsp_pd.get <> io.cvif2sdp_n_rd_rsp_pd.get
    }

    io.sdp_n2mcif_rd_req_pd <> u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2mcif_rd_req_pd
    io.sdp_n2mcif_rd_cdt_lat_fifo_pop := u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2mcif_rd_cdt_lat_fifo_pop
    u_NV_NVDLA_SDP_RDMA_dmaif.io.mcif2sdp_rd_rsp_pd <> io.mcif2sdp_n_rd_rsp_pd

    u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_req_ram_type := io.reg2dp_nrdma_ram_type
    u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_req_pd <> u_ig.io.dma_rd_req_pd 

    u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_ram_type := io.reg2dp_nrdma_ram_type
    u_lat_fifo.io.wr_pvld := u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_pd.valid
    u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_pd.ready := u_lat_fifo.io.wr_prdy
    u_lat_fifo.io.wr_pd := u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_pd.bits
    u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_cdt_lat_fifo_pop := u_eg.io.dma_rd_cdt_lat_fifo_pop

}}




