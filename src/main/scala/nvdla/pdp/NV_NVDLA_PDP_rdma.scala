package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_PDP_rdma(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_clock = Flipped(new nvdla_clock_if)
        val pwrbus_ram_pd = Input(UInt(32.W))

        //csb2pdp_rdma
        val csb2pdp_rdma = new csb2dp_if

        //request_response
        val pdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_REQ.W))
        val mcif2pdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_RSP.W)))
        val pdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

        val pdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_REQ.W))) else None
        val cvif2pdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_RSP.W)))) else None
        val pdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

        val pdp_rdma2dp_pd = DecoupledIO(UInt((conf.PDPBW+14).W))

        val rdma2wdma_done = Output(Bool())

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
    val u_reg = Module(new NV_NVDLA_PDP_RDMA_reg)
    val u_ig = Module(new NV_NVDLA_PDP_RDMA_ig)
    val u_cq = Module(new NV_NVDLA_fifo(
                        depth = 256, 
                        width = 18,
                        ram_type = 0,
                        distant_wr_req = false))
    val u_eg = Module(new NV_NVDLA_PDP_RDMA_eg)
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
    u_reg.io.csb2pdp_rdma <> io.csb2pdp_rdma

    u_reg.io.dp2reg_d0_perf_read_stall := u_ig.io.dp2reg_d0_perf_read_stall
    u_reg.io.dp2reg_d1_perf_read_stall := u_ig.io.dp2reg_d1_perf_read_stall
    u_reg.io.dp2reg_done := u_eg.io.dp2reg_done

    val field = u_reg.io.reg2dp_field

    //=======================================
    // Ingress: send read request to external mem
    //---------------------------------------
    u_ig.io.nvdla_core_clk := nvdla_op_gated_clk

    io.pdp2mcif_rd_req_pd <> u_ig.io.pdp2mcif_rd_req_pd
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.pdp2cvif_rd_req_pd.get <> u_ig.io.pdp2cvif_rd_req_pd.get
    }
    u_ig.io.reg2dp_cube_in_channel := field.cube_in_channel
    u_ig.io.reg2dp_cube_in_height := field.cube_in_height
    u_ig.io.reg2dp_cube_in_width := field.cube_in_width
    u_ig.io.reg2dp_dma_en := field.dma_en
    u_ig.io.reg2dp_kernel_stride_width := field.kernel_stride_width
    u_ig.io.reg2dp_kernel_width := field.kernel_width
    u_ig.io.reg2dp_partial_width_in_first := field.partial_width_in_first
    u_ig.io.reg2dp_partial_width_in_last := field.partial_width_in_last
    u_ig.io.reg2dp_partial_width_in_mid := field.partial_width_in_mid
    u_ig.io.reg2dp_split_num := field.split_num
    u_ig.io.reg2dp_src_base_addr_high := field.src_base_addr_high
    u_ig.io.reg2dp_src_base_addr_low := field.src_base_addr_low 
    u_ig.io.reg2dp_src_line_stride := field.src_line_stride
    u_ig.io.reg2dp_src_ram_type := field.src_ram_type
    u_ig.io.reg2dp_src_surface_stride := field.src_surface_stride
    u_ig.io.eg2ig_done := u_eg.io.eg2ig_done
    u_ig.io.reg2dp_op_en := u_reg.io.reg2dp_op_en

     //=======================================
    // Context Queue: trace outstanding req, and pass info from Ig to Eg
    //---------------------------------------
    u_cq.io.clk := nvdla_op_gated_clk
    u_cq.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_cq.io.wr_pvld := u_ig.io.ig2cq_pd.valid
    u_ig.io.ig2cq_pd.ready := u_cq.io.wr_prdy
    u_cq.io.wr_pd := u_ig.io.ig2cq_pd.bits

     //=======================================
    // Egress: get return data from external mem
    //---------------------------------------
    u_eg.io.nvdla_core_clk := nvdla_op_gated_clk
    u_eg.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_eg.io.mcif2pdp_rd_rsp_pd <> io.mcif2pdp_rd_rsp_pd
    io.pdp2mcif_rd_cdt_lat_fifo_pop := u_eg.io.pdp2mcif_rd_cdt_lat_fifo_pop

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_eg.io.cvif2pdp_rd_rsp_pd.get <> io.cvif2pdp_rd_rsp_pd.get
        io.pdp2cvif_rd_cdt_lat_fifo_pop.get := u_eg.io.pdp2cvif_rd_cdt_lat_fifo_pop.get
    }
       
    io.pdp_rdma2dp_pd <> u_eg.io.pdp_rdma2dp_pd

    u_eg.io.cq2eg_pd.valid := u_cq.io.rd_pvld
    u_cq.io.rd_prdy := u_eg.io.cq2eg_pd.ready
    u_eg.io.cq2eg_pd.bits := u_cq.io.rd_pd

    u_eg.io.reg2dp_src_ram_type := field.src_ram_type
    io.rdma2wdma_done := u_eg.io.rdma2wdma_done

 
}}

