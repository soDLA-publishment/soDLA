package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

@chiselName
class NV_NVDLA_SDP_mrdma(implicit conf: nvdlaConfig) extends Module {

val io = IO(new Bundle {
    //in clock
    val nvdla_clock = Flipped(new nvdla_clock_if)
    val mrdma_slcg_op_en = Input(Bool())
    val mrdma_disable = Input(Bool())
    val pwrbus_ram_pd = Input(UInt(32.W))

    //cvif
    val sdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
    val cvif2sdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None
    val sdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

    //mcif
    val sdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
    val mcif2sdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))
    val sdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

    val sdp_mrdma2cmux_pd = DecoupledIO(UInt((conf.DP_DIN_DW+2).W))

    val reg2dp_op_en = Input(Bool())  
    val reg2dp_batch_number = Input(UInt(5.W))  
    val reg2dp_channel = Input(UInt(13.W))  
    val reg2dp_height = Input(UInt(13.W))  
    val reg2dp_width = Input(UInt(13.W))  
    val reg2dp_in_precision = Input(UInt(2.W))  
    val reg2dp_proc_precision = Input(UInt(2.W))  
    val reg2dp_src_ram_type = Input(Bool())  
    val reg2dp_src_base_addr_high = Input(UInt(32.W))  
    val reg2dp_src_base_addr_low = Input(UInt((32-conf.AM_AW).W))  
    val reg2dp_src_line_stride = Input(UInt((32-conf.AM_AW).W))  
    val reg2dp_src_surface_stride = Input(UInt((32-conf.AM_AW).W))  
    val reg2dp_perf_dma_en = Input(Bool())  
    val reg2dp_perf_nan_inf_count_en = Input(Bool())  
    val dp2reg_done = Output(Bool())
    val dp2reg_mrdma_stall = Output(UInt(32.W))
    val dp2reg_status_inf_input_num = Output(UInt(32.W))
    val dp2reg_status_nan_input_num= Output(UInt(32.W))




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
    //==============
    // Work Processing
    //==============
    val layer_process = RegInit(false.B)
    val eg_done = Wire(Bool())
    val op_load = io.reg2dp_op_en & ~layer_process
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
    u_gate.io.slcg_disable.get := io.mrdma_disable
    u_gate.io.slcg_en(0) := io.mrdma_slcg_op_en
    val nvdla_gated_clk = u_gate.io.nvdla_core_gated_clk


     //=======================================
    // Ingress: send read request to external mem
    //---------------------------------------
    val u_ig = Module(new NV_NVDLA_SDP_MRDMA_ig)
    u_ig.io.nvdla_core_clk := nvdla_gated_clk
    u_ig.io.op_load := op_load

    u_ig.io.reg2dp_batch_number  := io.reg2dp_batch_number
    u_ig.io.reg2dp_channel := io.reg2dp_channel
    u_ig.io.reg2dp_height := io.reg2dp_height
    u_ig.io.reg2dp_width := io.reg2dp_width
    u_ig.io.reg2dp_in_precision := io.reg2dp_in_precision
    u_ig.io.reg2dp_proc_precision := io.reg2dp_proc_precision
    u_ig.io.reg2dp_src_ram_type := io.reg2dp_src_ram_type
    u_ig.io.reg2dp_src_base_addr_high := io.reg2dp_src_base_addr_high
    u_ig.io.reg2dp_src_base_addr_low := io.reg2dp_src_base_addr_low
    u_ig.io.reg2dp_src_line_stride := io.reg2dp_src_line_stride
    u_ig.io.reg2dp_src_surface_stride := io.reg2dp_src_surface_stride
    u_ig.io.reg2dp_perf_dma_en := io.reg2dp_perf_dma_en
    io.dp2reg_mrdma_stall := u_ig.io.dp2reg_mrdma_stall 

    //=======================================
    // Context Queue: trace outstanding req, and pass info from Ig to Eg
    //---------------------------------------
    val cq_depth = if(conf.NVDLA_VMOD_SDP_MRDMA_LATENCY_FIFO_DEPTH < 16) 16 else conf.NVDLA_VMOD_SDP_MRDMA_LATENCY_FIFO_DEPTH
    val cq_width = 14
    val u_cq = Module(new NV_NVDLA_fifo(depth = cq_depth, width = cq_width, distant_wr_req = false, ram_type = 2))
    u_cq.io.clk := nvdla_gated_clk
    u_cq.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_cq.io.wr_pvld := u_ig.io.ig2cq_pd.valid
    u_ig.io.ig2cq_pd.ready  := u_cq.io.wr_prdy
    u_cq.io.wr_pd := u_ig.io.ig2cq_pd.bits

    //=======================================
    // Egress: get return data from external mem
    //---------------------------------------

    val u_eg = Module(new NV_NVDLA_SDP_MRDMA_eg)
    u_eg.io.nvdla_core_clk := nvdla_gated_clk
    u_eg.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_eg.io.op_load := op_load 
    eg_done := u_eg.io.eg_done

    u_eg.io.cq2eg_pd.valid := u_cq.io.rd_pvld
    u_cq.io.rd_prdy := u_eg.io.cq2eg_pd.ready
    u_eg.io.cq2eg_pd.bits := u_cq.io.rd_pd

    io.sdp_mrdma2cmux_pd <> u_eg.io.sdp_mrdma2cmux_pd
    u_eg.io.reg2dp_height := io.reg2dp_height
    u_eg.io.reg2dp_width := io.reg2dp_width
    u_eg.io.reg2dp_height := io.reg2dp_height
    u_eg.io.reg2dp_in_precision := io.reg2dp_in_precision
    u_eg.io.reg2dp_proc_precision := io.reg2dp_proc_precision
    u_eg.io.reg2dp_src_ram_type := io.reg2dp_src_ram_type
    u_eg.io.reg2dp_perf_nan_inf_count_en := io.reg2dp_perf_nan_inf_count_en
    io.dp2reg_status_inf_input_num := u_eg.io.dp2reg_status_inf_input_num
    io.dp2reg_status_nan_input_num := u_eg.io.dp2reg_status_nan_input_num

    val u_NV_NVDLA_SDP_RDMA_dmaif = Module(new NV_NVDLA_SDP_RDMA_dmaif)
    u_NV_NVDLA_SDP_RDMA_dmaif.io.nvdla_core_clk := nvdla_gated_clk
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){

        io.sdp2cvif_rd_req_pd.get <> u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_req_pd.get
        io.sdp2cvif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
        u_NV_NVDLA_SDP_RDMA_dmaif.io.cvif2sdp_rd_rsp_pd.get <> io.cvif2sdp_rd_rsp_pd.get
    }

    io.sdp2mcif_rd_req_pd <> u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2mcif_rd_req_pd
    io.sdp2mcif_rd_cdt_lat_fifo_pop := u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2mcif_rd_cdt_lat_fifo_pop
    u_NV_NVDLA_SDP_RDMA_dmaif.io.mcif2sdp_rd_rsp_pd <> io.mcif2sdp_rd_rsp_pd

    u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_req_pd <> u_ig.io.dma_rd_req_pd
    u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_req_ram_type := u_ig.io.dma_rd_req_ram_type

    u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_ram_type := u_eg.io.dma_rd_rsp_ram_type
    u_eg.io.dma_rd_rsp_pd <> u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_rsp_pd
    u_NV_NVDLA_SDP_RDMA_dmaif.io.dma_rd_cdt_lat_fifo_pop := u_eg.io.dma_rd_cdt_lat_fifo_pop

}}

object NV_NVDLA_SDP_mrdmaDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_mrdma())
}



