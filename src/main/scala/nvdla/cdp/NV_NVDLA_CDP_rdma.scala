package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_rdma(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_clock = Flipped(new nvdla_clock_if)
        val pwrbus_ram_pd = Input(UInt(32.W))

        //csb2cdp_rdma
        val csb2cdp_rdma = new csb2dp_if

        //request_response
        val cdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
        val mcif2cdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))
        val cdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

        val cdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val cvif2cdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None
        val cdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

        val cdp_rdma2dp_pd = DecoupledIO(UInt((conf.CDPBW+25).W))
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
    //=======================================
    val u_slcg = Module(new NV_NVDLA_slcg(1, false))
    val u_reg = Module(new NV_NVDLA_CDP_RDMA_reg)
    val u_ig = Module(new NV_NVDLA_CDP_RDMA_ig)
    val u_cq = Module(new NV_NVDLA_fifo(
                        depth = 256, 
                        width = 7,
                        ram_type = 0,
                        distant_wr_req = false))
    val u_eg = Module(new NV_NVDLA_CDP_RDMA_eg)
    //=======================================
    //        SLCG gen unit
    //---------------------------------------
    u_slcg.io.nvdla_clock <> io.nvdla_clock
    u_slcg.io.slcg_en(0) := u_reg.io.slcg_op_en
    val nvdla_op_gated_clk = u_slcg.io.nvdla_core_gated_clk 
    //========================================
    //CFG: Configure Registers
    //----------------------------------------
    u_reg.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk
    u_reg.io.csb2cdp_rdma <> io.csb2cdp_rdma

    u_reg.io.dp2reg_d0_perf_read_stall := u_ig.io.dp2reg_d0_perf_read_stall
    u_reg.io.dp2reg_d1_perf_read_stall := u_ig.io.dp2reg_d1_perf_read_stall
    u_reg.io.dp2reg_done := u_eg.io.dp2reg_done

    val field = u_reg.io.reg2dp_field
    //=======================================
    // Ingress: send read request to external mem
    //---------------------------------------
    u_ig.io.nvdla_core_clk := nvdla_op_gated_clk

    io.cdp2mcif_rd_req_pd <> u_ig.io.cdp2mcif_rd_req_pd
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdp2cvif_rd_req_pd.get <> u_ig.io.cdp2cvif_rd_req_pd.get
    }

    u_ig.io.reg2dp_input_data := field.input_data
    u_ig.io.reg2dp_channel := field.channel
    u_ig.io.reg2dp_height := field.height
    u_ig.io.reg2dp_width := field.cdp_width
    u_ig.io.reg2dp_dma_en := field.dma_en
    u_ig.io.reg2dp_op_en := u_reg.io.reg2dp_op_en
    u_ig.io.reg2dp_src_base_addr_high := field.src_base_addr_high
    u_ig.io.reg2dp_src_base_addr_low := field.src_base_addr_low
    u_ig.io.reg2dp_src_line_stride := field.src_line_stride
    u_ig.io.reg2dp_src_ram_type := field.src_ram_type
    u_ig.io.reg2dp_src_surface_stride := field.src_surface_stride

    u_ig.io.eg2ig_done := u_eg.io.eg2ig_done
    //=======================================
    // Context Queue: trace outstanding req, and pass info from Ig to Eg
    //---------------------------------------
    u_cq.io.clk := nvdla_op_gated_clk
    u_cq.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_cq.io.wr_pvld := u_ig.io.cq_wr_pd.valid
    u_ig.io.cq_wr_pd.ready := u_cq.io.wr_prdy
    u_cq.io.wr_pd := u_ig.io.cq_wr_pd.bits
     //=======================================
    // Egress: get return data from external mem
    //---------------------------------------
    u_eg.io.nvdla_core_clk := nvdla_op_gated_clk
    u_eg.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_eg.io.mcif2cdp_rd_rsp_pd <> io.mcif2cdp_rd_rsp_pd
    io.cdp2mcif_rd_cdt_lat_fifo_pop := u_eg.io.cdp2mcif_rd_cdt_lat_fifo_pop

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_eg.io.cvif2cdp_rd_rsp_pd.get <> io.cvif2cdp_rd_rsp_pd.get
        io.cdp2cvif_rd_cdt_lat_fifo_pop.get := u_eg.io.cdp2cvif_rd_cdt_lat_fifo_pop.get
    }
       
    io.cdp_rdma2dp_pd <> u_eg.io.cdp_rdma2dp_pd

    u_eg.io.cq_rd_pd.valid := u_cq.io.rd_pvld
    u_cq.io.rd_prdy := u_eg.io.cq_rd_pd.ready
    u_eg.io.cq_rd_pd.bits := u_cq.io.rd_pd

    u_eg.io.reg2dp_input_data := field.input_data
    u_eg.io.reg2dp_channel := field.channel
    u_eg.io.reg2dp_src_ram_type := field.src_ram_type

 
}}



object NV_NVDLA_CDP_rdmaDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_rdma())
}