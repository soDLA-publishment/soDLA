// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_partition_p(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val dla_core_clk = Input(Bool())
//         val dla_csb_clk = Input(Bool())
//         val global_clk_ovr_on = Input(Clock())
//         val tmc2slcg_disable_clock_gating = Input(Bool())
//         val dla_reset_rstn = Input(Bool())
//         val direct_reset_ = Input(Bool())
//         val test_mode = Input(Bool())
//         //csb2nvdla
//         val csb2nvdla = Flipped(DecoupledIO(new csb2nvdla_if))
//         val nvdla2csb = ValidIO(new nvdla2csb_if)
//         val nvdla2csb_wr_complete = Output(Bool())
//         ///////////////
//         //axi
//         //2dbb
//         val nvdla_core2dbb_aw = DecoupledIO(new nocif_axi_wr_address_if)
//         val nvdla_core2dbb_w = DecoupledIO(new nocif_axi_wr_data_if)
//         val nvdla_core2dbb_b = Flipped(DecoupledIO(new nocif_axi_wr_response_if))
//         val nvdla_core2dbb_ar = Flipped(DecoupledIO(new nocif_axi_rd_address_if))
//         val nvdla_core2dbb_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if)
//         //2cvsram
//         val nvdla_core2cvsram_aw = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_address_if)) else None
//         val nvdla_core2cvsram_w = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_data_if)) else None
//         val nvdla_core2cvsram_b = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_wr_response_if))) else None
//         val nvdla_core2cvsram_ar = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_address_if))) else None
//         val nvdla_core2cvsram_r = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_data_if)) else None
//         //pwr_ram_pd
//         val nvdla_pwrbus_ram_c_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_ma_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_mb_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_p_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_o_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_a_pd = Input(UInt(32.W))
//         val dla_intr = Output(Bool())
//     }
// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 

// if(NVDLA_SECONDARY_MEMIF_ENABLE){
//     io.nvdla_core2cvsram_aw.valid := false.B
//     io.nvdla_core2cvsram_w.valid := false.B
//     io.nvdla_core2cvsram_w.bits.last := false.B
//     io.nvdla_core2cvsram_b.ready := true.B
//     io.nvdla_core2cvsram_r.ready := true.B
// }

// ////////////////////////////////////////////////////////////////////////
// //  NVDLA Partition O                                                 //
// ////////////////////////////////////////////////////////////////////////
//     val u_partition_o = Module(new NV_NVDLA_partition_o)
//     u_partition_o.io.test_mode := io.test_mode
//         val direct_reset_ = Input(Bool())

//         val global_clk_ovr_on = Input(Clock())
//         val tmc2slcg_disable_clock_gating = Input(Bool())

//         val nvdla_core_clk = Input(Clock())
//         val nvdla_falcon_clk = Input(Clock())
//         val nvdla_core_rstn = Output(Bool())
//         val nvdla_clk_ovr_on = Output(Clock())

//         val dla_reset_rstn = Input(Bool())

//         //cdma
//         val csb2cdma = Flipped(new csb2dp_if)
//         val cdma_dat2glb_done_intr_pd = Input(UInt(2.W))
//         val cdma_wt2glb_done_intr_pd = Input(UInt(2.W))
//         //cmac
//         val csb2cmac_a = Flipped(new csb2dp_if)
//         val csb2cmac_b = Flipped(new csb2dp_if)
//         //cacc
//         val csb2cacc = Flipped(new csb2dp_if)
//         val cacc2glb_done_intr_pd = Input(UInt(2.W))
//         //csc
//         val csb2csc = Flipped(new csb2dp_if)
//         //apb
//         val csb2nvdla = Flipped(DecoupledIO(new csb2nvdla_if))
//         val nvdla2csb = ValidIO(new nvdla2csb_if)
//         val nvdla2csb_wr_complete = Output(Bool())
//         //sdp
//         val csb2sdp_rdma = Flipped(new csb2dp_if)
//         val csb2sdp = Flipped(new csb2dp_if)
//         val sdp2glb_done_intr_pd = Input(UInt(2.W))

//         val sdp2pdp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_SDP_MAX_THROUGHPUT*conf.NVDLA_BPE).W)))

//         //client2mcif 
//         val cdma_dat2mcif_rd_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))
//         val mcif2cdma_dat_rd_rsp_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))

//         val cdma_wt2mcif_rd_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))
//         val mcif2cdma_wt_rd_rsp_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))

//         val sdp2mcif_rd_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))
//         val mcif2sdp_rd_rsp_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))
//         val sdp2mcif_rd_cdt_lat_fifo_pop = Input(Bool())
//         val sdp2mcif_wr_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W)))
//         val mcif2sdp_wr_rsp_complete = Output(Bool())

//         val sdp_b2mcif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
//         val mcif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
//         val sdp_b2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None

//         val sdp_e2mcif_rd_req_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
//         val mcif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
//         val sdp_e2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None   

//         val sdp_n2mcif_rd_req_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
//         val mcif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
//         val sdp_n2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None

//         //client2cvif
//         val cdma_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
//         val cvif2cdma_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

//         val cdma_wt2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
//         val cvif2cdma_wt_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

//         val sdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
//         val cvif2sdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
//         val sdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//         val sdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W)))) else None
//         val cvif2sdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

//         val sdp_b2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
//         val cvif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
//         val sdp_b2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None

//         val sdp_e2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
//         val cvif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
//         val sdp_e2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None   

//         val sdp_n2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
//         val cvif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
//         val sdp_n2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None


//         val mcif2noc_axi_ar = DecoupledIO(new nocif_axi_rd_address_if)
//         val mcif2noc_axi_aw = DecoupledIO(new nocif_axi_wr_address_if)
//         val mcif2noc_axi_w = DecoupledIO(new nocif_axi_wr_data_if)
//         val noc2mcif_axi_b = Flipped(DecoupledIO(new nocif_axi_wr_response_if))
//         val noc2mcif_axi_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if))

//         val cvif2noc_axi_ar = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_rd_address_if)) else None
//         val cvif2noc_axi_aw = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_address_if)) else None
//         val cvif2noc_axi_w = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_data_if)) else None
//         val noc2cvif_axi_b = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_wr_response_if))) else None
//         val noc2cvif_axi_r = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_data_if))) else None

//         val core_intr = Output(Bool())
//         val pwrbus_ram_pd = Input(UInt(32.W))

// ////////////////////////////////////////////////////////////////////////
// //  NVDLA Partition C                                                 //
// ////////////////////////////////////////////////////////////////////////

// }


// object NV_NVDLADriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA())
// }
