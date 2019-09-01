// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_sdp(implicit val conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {

//         val nvdla_core_clk = Input(Clock())
//         val nvdla_core_rstn = Input(Bool())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         val cacc2sdp_pd = Flipped(DecoupledIO(UInt((conf.DP_IN_DW+2).W)))
//         val sdp2pdp_pd = DecoupledIO(UInt(conf.DP_OUT_DW.W))
//         val sdp2glb_done_intr_pd = Output(UInt(2.W))

//         val csb2sdp_rdma = new csb2dp_if
//         val csb2sdp = new csb2dp_if

//         val sdp_b2mcif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//         val sdp_b2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val mcif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None 

//         val sdp_b2cvif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//         val sdp_b2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val cvif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None 

//         val sdp_n2mcif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//         val sdp_n2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val mcif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None 

//         val sdp_n2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val sdp_n2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val sdp_n2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//         val sdp_n2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val cvif2sdp_n_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val cvif2sdp_n_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val cvif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None 

//         val sdp_e2mcif_rd_req_valid = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val sdp_e2mcif_rd_req_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val sdp_e2mcif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//         val sdp_e2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val mcif2sdp_e_rd_rsp_valid = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val mcif2sdp_e_rd_rsp_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val mcif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None 

//         val sdp_e2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val sdp_e2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val sdp_e2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//         val sdp_e2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val cvif2sdp_e_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val cvif2sdp_e_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val cvif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None 

//         val sdp2mcif_rd_req_valid = Output(Bool())
//         val sdp2mcif_rd_req_ready = Input(Bool())
//         val sdp2mcif_rd_req_pd = Output(UInt(conf.NVDLA_DMA_RD_REQ.W))
//         val sdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())
//         val mcif2sdp_rd_rsp_valid = Input(Bool())
//         val mcif2sdp_rd_rsp_ready = Output(Bool())
//         val mcif2sdp_rd_rsp_pd = Input(UInt(conf.NVDLA_DMA_RD_RSP.W))

//         val sdp2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//         val sdp2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//         val sdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//         val sdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//         val cvif2sdp_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//         val cvif2sdp_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//         val cvif2sdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None 

//         val sdp2mcif_wr_req_valid = Output(Bool())
//         val sdp2mcif_wr_req_ready = Input(Bool())
//         val sdp2mcif_wr_req_pd = Output(UInt(conf.NVDLA_DMA_WR_REQ.W))
//         val mcif2sdp_wr_rsp_complete = Input(Bool())

//         val sdp2cvif_wr_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//         val sdp2cvif_wr_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//         val sdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_WR_REQ.W))) else None
//         val cvif2sdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None

//     })
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
// withReset(!io.nvdla_core_rstn){  
//     //=======================================
//     //DMA
//     //--------------------------------------- 
//     val u_rdma = Module(new NV_NVDLA_SDP_rdma)
//     val u_wdma = Module(new NV_NVDLA_SDP_wdma)
//     //========================================
//     //SDP core instance
//     //----------------------------------------
//     val u_core = Module(new NV_NVDLA_SDP_core)
//     //=======================================
//     //CONFIG instance
//     //rdma has seperate config register, while wdma share with core
//     //---------------------------------------
//     val u_reg = Module(new NV_NVDLA_SDP_reg)
//     //glb
//     u_rdma.io.nvdla_core_clk := io.nvdla_core_clk
//     u_rdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_rdma.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync
//     u_rdma.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//     u_rdma.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//     u_wdma.io.nvdla_core_clk := io.nvdla_core_clk
//     u_wdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_wdma.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
//     u_wdma.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//     u_wdma.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//     u_core.io.nvdla_core_clk := io.nvdla_core_clk
//     u_core.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_core.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync
//     u_core.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//     u_core.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//     u_reg.io.nvdla_core_clk := io.nvdla_core_clk
    
//     io.sdp2glb_done_intr_pd := u_wdma.io.sdp2glb_done_intr_pd 
//     //mcif
//     io.sdp2mcif_rd_req_valid := u_rdma.io.sdp2mcif_rd_req_valid
//     u_rdma.io.sdp2mcif_rd_req_ready := io.sdp2mcif_rd_req_ready
//     io.sdp2mcif_rd_req_pd := u_rdma.io.sdp2mcif_rd_req_pd
//     io.sdp2mcif_rd_cdt_lat_fifo_pop := u_rdma.io.sdp2mcif_rd_cdt_lat_fifo_pop
//     u_rdma.io.mcif2sdp_rd_rsp_valid := io.mcif2sdp_rd_rsp_valid
//     io.mcif2sdp_rd_rsp_ready := u_rdma.io.mcif2sdp_rd_rsp_ready
//     u_rdma.io.mcif2sdp_rd_rsp_pd := io.mcif2sdp_rd_rsp_pd

//     io.sdp2mcif_wr_req_valid := u_wdma.io.sdp2mcif_wr_req_valid
//     u_wdma.io.sdp2mcif_wr_req_ready := io.sdp2mcif_wr_req_ready
//     io.sdp2mcif_wr_req_pd := u_wdma.io.sdp2mcif_wr_req_pd
//     u_wdma.io.mcif2sdp_wr_rsp_complete := io.mcif2sdp_wr_rsp_complete

//     u_core.io.sdp_mrdma2cmux_valid := u_rdma.io.sdp_mrdma2cmux_valid
//     u_rdma.io.sdp_mrdma2cmux_ready := u_core.io.sdp_mrdma2cmux_ready
//     u_core.io.sdp_mrdma2cmux_pd := u_rdma.io.sdp_mrdma2cmux_pd

//     //csb2sdp
//     u_rdma.io.csb2sdp_rdma_req_pvld := io.csb2sdp_rdma_req_pvld
//     io.csb2sdp_rdma_req_prdy := u_rdma.io.csb2sdp_rdma_req_prdy
//     u_rdma.io.csb2sdp_rdma_req_pd := io.csb2sdp_rdma_req_pd
//     io.sdp_rdma2csb_resp_valid := u_rdma.io.sdp_rdma2csb_resp_valid
//     io.sdp_rdma2csb_resp_pd := u_rdma.io.sdp_rdma2csb_resp_pd

//     u_reg.io.csb2sdp_req_pvld := io.csb2sdp_req_pvld
//     io.csb2sdp_req_prdy := u_reg.io.csb2sdp_req_prdy
//     u_reg.io.csb2sdp_req_pd := io.csb2sdp_req_pd
//     io.sdp2csb_resp_valid := u_reg.io.sdp2csb_resp_valid
//     io.sdp2csb_resp_pd := u_reg.io.sdp2csb_resp_pd

//     //sdp2cacc
//     u_core.io.cacc2sdp_valid := io.cacc2sdp_valid
//     io.cacc2sdp_ready := u_core.io.cacc2sdp_ready
//     u_core.io.cacc2sdp_pd := io.cacc2sdp_pd

//     //sdp_dp2wdma
//     u_wdma.io.sdp_dp2wdma_valid := u_core.io.sdp_dp2wdma_valid
//     u_core.io.sdp_dp2wdma_ready := u_wdma.io.sdp_dp2wdma_ready
//     u_wdma.io.sdp_dp2wdma_pd := u_core.io.sdp_dp2wdma_pd

//     //sdp2pdp
//     io.sdp2pdp_valid := u_core.io.sdp2pdp_valid
//     u_core.io.sdp2pdp_ready := io.sdp2pdp_ready
//     io.sdp2pdp_pd := u_core.io.sdp2pdp_pd
//     u_core.io.reg2dp_ncore_slcg_op_en := u_reg.io.reg2dp_ncore_slcg_op_en
//     u_core.io.reg2dp_bcore_slcg_op_en := u_reg.io.reg2dp_bcore_slcg_op_en
//     u_core.io.reg2dp_ecore_slcg_op_en := u_reg.io.reg2dp_ecore_slcg_op_en




//     if(conf.NVDLA_SDP_BS_ENABLE){
//         //sdp_b2mcif
//         io.sdp_b2mcif_rd_req_valid.get := u_rdma.io.sdp_b2mcif_rd_req_valid.get
//         u_rdma.io.sdp_b2mcif_rd_req_ready.get := io.sdp_b2mcif_rd_req_ready.get
//         io.sdp_b2mcif_rd_req_pd.get := u_rdma.io.sdp_b2mcif_rd_req_pd.get
//         io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get
//         u_rdma.io.mcif2sdp_b_rd_rsp_valid.get := io.mcif2sdp_b_rd_rsp_valid.get
//         io.mcif2sdp_b_rd_rsp_ready.get := u_rdma.io.mcif2sdp_b_rd_rsp_ready.get
//         u_rdma.io.mcif2sdp_b_rd_rsp_pd.get := io.mcif2sdp_b_rd_rsp_pd.get
//         //sdp_brdma2dp_alu
//         u_core.io.sdp_brdma2dp_alu_valid.get := u_rdma.io.sdp_brdma2dp_alu_valid.get
//         u_rdma.io.sdp_brdma2dp_alu_ready.get := u_core.io.sdp_brdma2dp_alu_ready.get
//         u_core.io.sdp_brdma2dp_alu_pd.get := u_rdma.io.sdp_brdma2dp_alu_pd.get
//         //sdp_brdma2dp_mul
//         u_core.io.sdp_brdma2dp_mul_valid.get := u_rdma.io.sdp_brdma2dp_mul_valid.get
//         u_rdma.io.sdp_brdma2dp_mul_ready.get := u_core.io.sdp_brdma2dp_mul_ready.get
//         u_core.io.sdp_brdma2dp_mul_pd.get := u_rdma.io.sdp_brdma2dp_mul_pd.get
//         //reg2dp_bs
//         u_core.io.reg2dp_bs_alu_algo.get := u_reg.io.reg2dp_bs_alu_algo
//         u_core.io.reg2dp_bs_alu_bypass.get := u_reg.io.reg2dp_bs_alu_bypass
//         u_core.io.reg2dp_bs_alu_operand.get := u_reg.io.reg2dp_bs_alu_operand
//         u_core.io.reg2dp_bs_alu_shift_value.get := u_reg.io.reg2dp_bs_alu_shift_value
//         u_core.io.reg2dp_bs_alu_src.get := u_reg.io.reg2dp_bs_alu_src
//         u_core.io.reg2dp_bs_bypass.get := u_reg.io.reg2dp_bs_bypass
//         u_core.io.reg2dp_bs_mul_bypass.get := u_reg.io.reg2dp_bs_mul_bypass
//         u_core.io.reg2dp_bs_mul_operand.get := u_reg.io.reg2dp_bs_mul_operand
//         u_core.io.reg2dp_bs_mul_prelu.get := u_reg.io.reg2dp_bs_mul_prelu
//         u_core.io.reg2dp_bs_mul_shift_value.get := u_reg.io.reg2dp_bs_mul_shift_value
//         u_core.io.reg2dp_bs_mul_src.get := u_reg.io.reg2dp_bs_mul_src
//         u_core.io.reg2dp_bs_relu_bypass.get := u_reg.io.reg2dp_bs_relu_bypass

//     }
//     if(conf.NVDLA_SDP_BN_ENABLE){
//         //sdp_n2mcif
//         io.sdp_n2mcif_rd_req_valid.get := u_rdma.io.sdp_n2mcif_rd_req_valid.get
//         u_rdma.io.sdp_n2mcif_rd_req_ready.get := io.sdp_n2mcif_rd_req_ready.get
//         io.sdp_n2mcif_rd_req_pd.get := u_rdma.io.sdp_n2mcif_rd_req_pd.get
//         io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get
//         u_rdma.io.mcif2sdp_n_rd_rsp_valid.get := io.mcif2sdp_n_rd_rsp_valid.get
//         io.mcif2sdp_n_rd_rsp_ready.get := u_rdma.io.mcif2sdp_n_rd_rsp_ready.get
//         u_rdma.io.mcif2sdp_n_rd_rsp_pd.get := io.mcif2sdp_n_rd_rsp_pd.get
//         //sdp_nrdma2dp_alu
//         u_core.io.sdp_nrdma2dp_alu_valid.get := u_rdma.io.sdp_nrdma2dp_alu_valid.get
//         u_rdma.io.sdp_nrdma2dp_alu_ready.get := u_core.io.sdp_nrdma2dp_alu_ready.get
//         u_core.io.sdp_nrdma2dp_alu_pd.get := u_rdma.io.sdp_nrdma2dp_alu_pd.get
//         //sdp_nrdma2dp_mul
//         u_core.io.sdp_nrdma2dp_mul_valid.get := u_rdma.io.sdp_nrdma2dp_mul_valid.get
//         u_rdma.io.sdp_nrdma2dp_mul_ready.get := u_core.io.sdp_nrdma2dp_mul_ready.get
//         u_core.io.sdp_nrdma2dp_mul_pd.get := u_rdma.io.sdp_nrdma2dp_mul_pd.get
//         //reg2dp_bn
//         u_core.io.reg2dp_bn_alu_algo.get := u_reg.io.reg2dp_bn_alu_algo
//         u_core.io.reg2dp_bn_alu_bypass.get := u_reg.io.reg2dp_bn_alu_bypass
//         u_core.io.reg2dp_bn_alu_operand.get := u_reg.io.reg2dp_bn_alu_operand
//         u_core.io.reg2dp_bn_alu_shift_value.get := u_reg.io.reg2dp_bn_alu_shift_value
//         u_core.io.reg2dp_bn_alu_src.get := u_reg.io.reg2dp_bn_alu_src
//         u_core.io.reg2dp_bn_bypass.get := u_reg.io.reg2dp_bn_bypass
//         u_core.io.reg2dp_bn_mul_bypass.get := u_reg.io.reg2dp_bn_mul_bypass
//         u_core.io.reg2dp_bn_mul_operand.get := u_reg.io.reg2dp_bn_mul_operand
//         u_core.io.reg2dp_bn_mul_prelu.get := u_reg.io.reg2dp_bn_mul_prelu
//         u_core.io.reg2dp_bn_mul_shift_value.get := u_reg.io.reg2dp_bn_mul_shift_value
//         u_core.io.reg2dp_bn_mul_src.get := u_reg.io.reg2dp_bn_mul_src
//         u_core.io.reg2dp_bn_relu_bypass.get := u_reg.io.reg2dp_bn_relu_bypass

//     }
//     if(conf.NVDLA_SDP_EW_ENABLE){
//         //sdp_e2mcif
//         io.sdp_e2mcif_rd_req_valid.get := u_rdma.io.sdp_e2mcif_rd_req_valid.get
//         u_rdma.io.sdp_e2mcif_rd_req_ready.get := io.sdp_e2mcif_rd_req_ready.get
//         io.sdp_e2mcif_rd_req_pd.get := u_rdma.io.sdp_e2mcif_rd_req_pd.get
//         io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get
//         u_rdma.io.mcif2sdp_e_rd_rsp_valid.get := io.mcif2sdp_e_rd_rsp_valid.get
//         io.mcif2sdp_e_rd_rsp_ready.get := u_rdma.io.mcif2sdp_e_rd_rsp_ready.get
//         u_rdma.io.mcif2sdp_e_rd_rsp_pd.get := io.mcif2sdp_e_rd_rsp_pd.get
//         //sdp_erdma2dp_alu
//         u_core.io.sdp_erdma2dp_alu_valid.get := u_rdma.io.sdp_erdma2dp_alu_valid.get
//         u_rdma.io.sdp_erdma2dp_alu_ready.get := u_core.io.sdp_erdma2dp_alu_ready.get
//         u_core.io.sdp_erdma2dp_alu_pd.get := u_rdma.io.sdp_erdma2dp_alu_pd.get
//         //sdp_erdma2dp_mul
//         u_core.io.sdp_erdma2dp_mul_valid.get := u_rdma.io.sdp_erdma2dp_mul_valid.get
//         u_rdma.io.sdp_erdma2dp_mul_ready.get := u_core.io.sdp_erdma2dp_mul_ready.get
//         u_core.io.sdp_erdma2dp_mul_pd.get := u_rdma.io.sdp_erdma2dp_mul_pd.get

//         u_wdma.io.reg2dp_ew_alu_algo := u_reg.io.reg2dp_ew_alu_algo
//         u_wdma.io.reg2dp_ew_alu_bypass := u_reg.io.reg2dp_ew_alu_bypass
//         u_wdma.io.reg2dp_ew_bypass := u_reg.io.reg2dp_ew_bypass

//         u_core.io.reg2dp_ew_alu_algo.get := u_reg.io.reg2dp_ew_alu_algo
//         u_core.io.reg2dp_ew_alu_bypass.get := u_reg.io.reg2dp_ew_alu_bypass
//         u_core.io.reg2dp_ew_alu_cvt_bypass.get := u_reg.io.reg2dp_ew_alu_cvt_bypass
//         u_core.io.reg2dp_ew_alu_cvt_offset.get := u_reg.io.reg2dp_ew_alu_cvt_offset
//         u_core.io.reg2dp_ew_alu_cvt_scale.get := u_reg.io.reg2dp_ew_alu_cvt_scale
//         u_core.io.reg2dp_ew_alu_cvt_truncate.get := u_reg.io.reg2dp_ew_alu_cvt_truncate
//         u_core.io.reg2dp_ew_alu_operand.get := u_reg.io.reg2dp_ew_alu_operand
//         u_core.io.reg2dp_ew_alu_src.get := u_reg.io.reg2dp_ew_alu_src
//         u_core.io.reg2dp_ew_bypass.get := u_reg.io.reg2dp_ew_bypass
//         u_core.io.reg2dp_ew_lut_bypass.get := u_reg.io.reg2dp_ew_lut_bypass
//         u_core.io.reg2dp_ew_mul_bypass.get := u_reg.io.reg2dp_ew_mul_bypass
//         u_core.io.reg2dp_ew_mul_cvt_bypass.get := u_reg.io.reg2dp_ew_mul_cvt_bypass
//         u_core.io.reg2dp_ew_mul_cvt_offset.get := u_reg.io.reg2dp_ew_mul_cvt_offset
//         u_core.io.reg2dp_ew_mul_cvt_scale.get := u_reg.io.reg2dp_ew_mul_cvt_scale
//         u_core.io.reg2dp_ew_mul_cvt_truncate.get := u_reg.io.reg2dp_ew_mul_cvt_truncate
//         u_core.io.reg2dp_ew_mul_operand.get := u_reg.io.reg2dp_ew_mul_operand
//         u_core.io.reg2dp_ew_mul_prelu.get := u_reg.io.reg2dp_ew_mul_prelu
//         u_core.io.reg2dp_ew_mul_src.get := u_reg.io.reg2dp_ew_mul_src
//         u_core.io.reg2dp_ew_truncate.get := u_reg.io.reg2dp_ew_truncate

//         if(conf.NVDLA_SDP_LUT_ENABLE){
//             u_core.io.reg2dp_lut_hybrid_priority.get := u_reg.io.reg2dp_lut_hybrid_priority
//             u_core.io.reg2dp_lut_int_access_type.get := u_reg.io.reg2dp_lut_int_access_type
//             u_core.io.reg2dp_lut_int_addr.get := u_reg.io.reg2dp_lut_int_addr
//             u_core.io.reg2dp_lut_int_data.get := u_reg.io.reg2dp_lut_int_data
//             u_core.io.reg2dp_lut_int_data_wr.get := u_reg.io.reg2dp_lut_int_data_wr
//             u_core.io.reg2dp_lut_int_table_id.get := u_reg.io.reg2dp_lut_int_table_id
//             u_core.io.reg2dp_lut_le_end.get := u_reg.io.reg2dp_lut_le_end
//             u_core.io.reg2dp_lut_le_function.get := u_reg.io.reg2dp_lut_le_function
//             u_core.io.reg2dp_lut_le_index_offset.get := u_reg.io.reg2dp_lut_le_index_offset
//             u_core.io.reg2dp_lut_le_index_select.get := u_reg.io.reg2dp_lut_le_index_select
//             u_core.io.reg2dp_lut_le_slope_oflow_scale.get := u_reg.io.reg2dp_lut_le_slope_oflow_scale
//             u_core.io.reg2dp_lut_le_slope_oflow_shift.get := u_reg.io.reg2dp_lut_le_slope_oflow_shift
//             u_core.io.reg2dp_lut_le_slope_uflow_scale.get := u_reg.io.reg2dp_lut_le_slope_uflow_scale
//             u_core.io.reg2dp_lut_le_slope_uflow_shift.get := u_reg.io.reg2dp_lut_le_slope_uflow_shift
//             u_core.io.reg2dp_lut_le_start.get := u_reg.io.reg2dp_lut_le_start
//             u_core.io.reg2dp_lut_lo_end.get := u_reg.io.reg2dp_lut_lo_end
//             u_core.io.reg2dp_lut_lo_index_select.get := u_reg.io.reg2dp_lut_lo_index_select
//             u_core.io.reg2dp_lut_lo_slope_oflow_scale.get := u_reg.io.reg2dp_lut_lo_slope_oflow_scale
//             u_core.io.reg2dp_lut_lo_slope_oflow_shift.get := u_reg.io.reg2dp_lut_lo_slope_oflow_shift
//             u_core.io.reg2dp_lut_lo_slope_uflow_scale.get := u_reg.io.reg2dp_lut_lo_slope_uflow_scale
//             u_core.io.reg2dp_lut_lo_slope_uflow_shift.get := u_reg.io.reg2dp_lut_lo_slope_uflow_shift
//             u_core.io.reg2dp_lut_lo_start.get := u_reg.io.reg2dp_lut_lo_start
//             u_core.io.reg2dp_lut_oflow_priority.get := u_reg.io.reg2dp_lut_oflow_priority
//             u_core.io.reg2dp_lut_slcg_en.get := u_reg.io.reg2dp_lut_slcg_en
//             u_core.io.reg2dp_lut_uflow_priority.get := u_reg.io.reg2dp_lut_uflow_priority
//             u_reg.io.dp2reg_lut_hybrid := u_core.io.dp2reg_lut_hybrid.get 
//             u_reg.io.dp2reg_lut_int_data := u_core.io.dp2reg_lut_int_data.get 
//             u_reg.io.dp2reg_lut_le_hit := u_core.io.dp2reg_lut_le_hit.get 
//             u_reg.io.dp2reg_lut_lo_hit := u_core.io.dp2reg_lut_lo_hit.get 
//             u_reg.io.dp2reg_lut_oflow := u_core.io.dp2reg_lut_oflow.get 
//             u_reg.io.dp2reg_lut_uflow := u_core.io.dp2reg_lut_uflow.get 
//         }
//     }
//     else{
//         u_wdma.io.reg2dp_ew_alu_algo := "b0".asUInt(2.W)
//         u_wdma.io.reg2dp_ew_alu_bypass := "b1".asUInt(1.W)
//         u_wdma.io.reg2dp_ew_bypass := "b1".asUInt(1.W)

//         u_reg.io.dp2reg_lut_hybrid := 0.U
//         u_reg.io.dp2reg_lut_int_data := 0.U
//         u_reg.io.dp2reg_lut_le_hit := 0.U
//         u_reg.io.dp2reg_lut_lo_hit := 0.U
//         u_reg.io.dp2reg_lut_oflow := 0.U
//         u_reg.io.dp2reg_lut_uflow := 0.U
//     }
//     u_wdma.io.reg2dp_op_en := u_reg.io.reg2dp_op_en
//     u_wdma.io.reg2dp_wdma_slcg_op_en := u_reg.io.reg2dp_wdma_slcg_op_en
//     u_wdma.io.reg2dp_output_dst := u_reg.io.reg2dp_output_dst
//     u_wdma.io.reg2dp_batch_number := u_reg.io.reg2dp_batch_number
//     u_wdma.io.reg2dp_winograd := u_reg.io.reg2dp_winograd
//     u_wdma.io.reg2dp_channel := u_reg.io.reg2dp_channel
//     u_wdma.io.reg2dp_height := u_reg.io.reg2dp_height
//     u_wdma.io.reg2dp_width := u_reg.io.reg2dp_width
//     u_wdma.io.reg2dp_proc_precision := u_reg.io.reg2dp_proc_precision
//     u_wdma.io.reg2dp_out_precision := u_reg.io.reg2dp_out_precision
//     u_wdma.io.reg2dp_dst_ram_type := u_reg.io.reg2dp_dst_ram_type
//     u_wdma.io.reg2dp_dst_base_addr_high := u_reg.io.reg2dp_dst_base_addr_high 
//     u_wdma.io.reg2dp_dst_base_addr_low := u_reg.io.reg2dp_dst_base_addr_low
//     u_wdma.io.reg2dp_dst_batch_stride  := u_reg.io.reg2dp_dst_batch_stride 
//     u_wdma.io.reg2dp_dst_line_stride := u_reg.io.reg2dp_dst_line_stride
//     u_wdma.io.reg2dp_dst_surface_stride := u_reg.io.reg2dp_dst_surface_stride
//     u_wdma.io.reg2dp_interrupt_ptr := u_reg.io.reg2dp_interrupt_ptr 
//     u_wdma.io.reg2dp_perf_dma_en := u_reg.io.reg2dp_perf_dma_en
//     u_reg.io.dp2reg_done := u_wdma.io.dp2reg_done
//     u_reg.io.dp2reg_status_nan_output_num := u_wdma.io.dp2reg_status_nan_output_num
//     u_reg.io.dp2reg_status_unequal := u_wdma.io.dp2reg_status_unequal
//     u_reg.io.dp2reg_wdma_stall := u_wdma.io.dp2reg_wdma_stall

//     u_core.io.reg2dp_cvt_offset := u_reg.io.reg2dp_cvt_offset
//     u_core.io.reg2dp_cvt_scale := u_reg.io.reg2dp_cvt_scale
//     u_core.io.reg2dp_cvt_shift := u_reg.io.reg2dp_cvt_shift
//     u_core.io.reg2dp_op_en := u_reg.io.reg2dp_op_en
//     u_core.io.reg2dp_flying_mode := u_reg.io.reg2dp_flying_mode
//     u_core.io.reg2dp_output_dst := u_reg.io.reg2dp_output_dst
//     u_core.io.reg2dp_nan_to_zero := u_reg.io.reg2dp_nan_to_zero
//     u_core.io.reg2dp_proc_precision := u_reg.io.reg2dp_proc_precision
//     u_core.io.reg2dp_out_precision := u_reg.io.reg2dp_out_precision
//     u_core.io.reg2dp_perf_lut_en := u_reg.io.reg2dp_perf_lut_en
//     u_core.io.reg2dp_perf_sat_en := u_reg.io.reg2dp_perf_sat_en
//     u_core.io.dp2reg_done := u_wdma.io.dp2reg_done
//     u_reg.io.dp2reg_out_saturation := u_core.io.dp2reg_out_saturation

//     u_reg.io.dp2reg_done := u_wdma.io.dp2reg_done     
//     u_reg.io.dp2reg_status_inf_input_num := 0.U
//     u_reg.io.dp2reg_status_nan_input_num := 0.U
//     u_reg.io.dp2reg_status_nan_output_num := u_wdma.io.dp2reg_status_nan_output_num
//     u_reg.io.dp2reg_status_unequal := u_wdma.io.dp2reg_status_unequal
//     u_reg.io.dp2reg_wdma_stall := u_wdma.io.dp2reg_wdma_stall


//     //cvif
//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         //sdp2cvif
//         io.sdp2cvif_rd_req_valid.get := u_rdma.io.sdp2cvif_rd_req_valid.get
//         u_rdma.io.sdp2cvif_rd_req_ready.get := io.sdp2cvif_rd_req_ready.get
//         io.sdp2cvif_rd_req_pd.get := u_rdma.io.sdp2cvif_rd_req_pd.get
//         io.sdp2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
//         u_rdma.io.cvif2sdp_rd_rsp_valid.get := io.cvif2sdp_rd_rsp_valid.get
//         io.cvif2sdp_rd_rsp_ready.get := u_rdma.io.cvif2sdp_rd_rsp_ready.get
//         u_rdma.io.cvif2sdp_rd_rsp_pd.get := io.cvif2sdp_rd_rsp_pd.get

//         io.sdp2cvif_wr_req_valid.get := u_wdma.io.sdp2cvif_wr_req_valid.get
//         u_wdma.io.sdp2cvif_wr_req_ready.get := io.sdp2cvif_wr_req_ready.get
//         io.sdp2cvif_wr_req_pd.get := u_wdma.io.sdp2cvif_wr_req_pd.get
//         u_wdma.io.cvif2sdp_wr_rsp_complete.get := io.cvif2sdp_wr_rsp_complete.get
        
//         if(conf.NVDLA_SDP_BS_ENABLE){
//             //sdp_bcvif
//             io.sdp_b2cvif_rd_req_valid.get := u_rdma.io.sdp_b2cvif_rd_req_valid.get
//             u_rdma.io.sdp_b2cvif_rd_req_ready.get := io.sdp_b2cvif_rd_req_ready.get
//             io.sdp_b2cvif_rd_req_pd.get := u_rdma.io.sdp_b2cvif_rd_req_pd.get
//             io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get
//             u_rdma.io.cvif2sdp_b_rd_rsp_valid.get := io.cvif2sdp_b_rd_rsp_valid.get
//             io.cvif2sdp_b_rd_rsp_ready.get := u_rdma.io.cvif2sdp_b_rd_rsp_ready.get
//             u_rdma.io.cvif2sdp_b_rd_rsp_pd.get := io.cvif2sdp_b_rd_rsp_pd.get
//         }
//         if(conf.NVDLA_SDP_BN_ENABLE){
//             //sdp_n2cvif
//             io.sdp_n2cvif_rd_req_valid.get := u_rdma.io.sdp_n2cvif_rd_req_valid.get
//             u_rdma.io.sdp_n2cvif_rd_req_ready.get := io.sdp_n2cvif_rd_req_ready.get
//             io.sdp_n2cvif_rd_req_pd.get := u_rdma.io.sdp_n2cvif_rd_req_pd.get
//             io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get
//             u_rdma.io.cvif2sdp_n_rd_rsp_valid.get := io.cvif2sdp_n_rd_rsp_valid.get
//             io.cvif2sdp_n_rd_rsp_ready.get := u_rdma.io.cvif2sdp_n_rd_rsp_ready.get
//             u_rdma.io.cvif2sdp_n_rd_rsp_pd.get := io.cvif2sdp_n_rd_rsp_pd.get
//         }
//         if(conf.NVDLA_SDP_EW_ENABLE){
//             //sdp_e2cvif
//             io.sdp_e2cvif_rd_req_valid.get := u_rdma.io.sdp_e2cvif_rd_req_valid.get
//             u_rdma.io.sdp_e2cvif_rd_req_ready.get := io.sdp_e2cvif_rd_req_ready.get
//             io.sdp_e2cvif_rd_req_pd.get := u_rdma.io.sdp_e2cvif_rd_req_pd.get
//             io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get
//             u_rdma.io.cvif2sdp_e_rd_rsp_valid.get := io.cvif2sdp_e_rd_rsp_valid.get
//             io.cvif2sdp_e_rd_rsp_ready.get := u_rdma.io.cvif2sdp_e_rd_rsp_ready.get
//             u_rdma.io.cvif2sdp_e_rd_rsp_pd.get := io.cvif2sdp_e_rd_rsp_pd.get
//         }
//     }
    
// }}


// object NV_NVDLA_sdpDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_sdp)
// }





