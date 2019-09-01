// package nvdla

// import chisel3._
// import chisel3.util._
// import chisel3.experimental._

// class NV_NVDLA_SDP_rdma(implicit conf: nvdlaConfig) extends Module {

// val io = IO(new Bundle {
//     //in clock
//     val nvdla_core_clk = Input(Clock())
//     val pwrbus_ram_pd = Input(UInt(32.W))

//     //csb2sdp
//     val csb2sdp_rdma_req_pvld = Input(Bool())
//     val csb2sdp_rdma_req_prdy = Output(Bool())
//     val csb2sdp_rdma_req_pd = Input(UInt(63.W))
//     val sdp_rdma2csb_resp_valid = Output(Bool())
//     val sdp_rdma2csb_resp_pd = Output(UInt(34.W))

//     //sdp_bcvif
//     val sdp_b2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//     val sdp_b2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//     val sdp_b2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//     val sdp_b2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_b_rd_rsp_valid  = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//     val cvif2sdp_b_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

//     //sdp_e2cvif
//     val sdp_e2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//     val sdp_e2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//     val sdp_e2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//     val sdp_e2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_e_rd_rsp_valid  = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//     val cvif2sdp_e_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
    
//     //sdp_n2cvif
//     val sdp_n2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//     val sdp_n2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//     val sdp_n2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//     val sdp_n2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_n_rd_rsp_valid  = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//     val cvif2sdp_n_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

//     //sdp2cvif
//     val sdp2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//     val sdp2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//     val sdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//     val sdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_rd_rsp_valid  = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//     val cvif2sdp_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

//     //sdp_b2mcif
//     val sdp_b2mcif_rd_req_valid = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//     val sdp_b2mcif_rd_req_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//     val sdp_b2mcif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//     val sdp_b2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//     val mcif2sdp_b_rd_rsp_valid  = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//     val mcif2sdp_b_rd_rsp_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//     val mcif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

//     val sdp_brdma2dp_alu_valid = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//     val sdp_brdma2dp_alu_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//     val sdp_brdma2dp_alu_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt((conf.AM_DW2+1).W))) else None

//     val sdp_brdma2dp_mul_valid = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//     val sdp_brdma2dp_mul_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//     val sdp_brdma2dp_mul_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt((conf.AM_DW2+1).W))) else None

//     //sdp_e2mcif
//     val sdp_e2mcif_rd_req_valid = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//     val sdp_e2mcif_rd_req_ready = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//     val sdp_e2mcif_rd_req_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//     val sdp_e2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//     val mcif2sdp_e_rd_rsp_valid  = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//     val mcif2sdp_e_rd_rsp_ready = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//     val mcif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

//     val sdp_erdma2dp_alu_valid = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//     val sdp_erdma2dp_alu_ready = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//     val sdp_erdma2dp_alu_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(UInt((conf.AM_DW2+1).W))) else None

//     val sdp_erdma2dp_mul_valid = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//     val sdp_erdma2dp_mul_ready = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//     val sdp_erdma2dp_mul_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(UInt((conf.AM_DW2+1).W))) else None

//     //sdp_n2mcif
//     val sdp_n2mcif_rd_req_valid = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//     val sdp_n2mcif_rd_req_ready = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//     val sdp_n2mcif_rd_req_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
//     val sdp_n2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//     val mcif2sdp_n_rd_rsp_valid  = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//     val mcif2sdp_n_rd_rsp_ready = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//     val mcif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

//     val sdp_nrdma2dp_alu_valid = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//     val sdp_nrdma2dp_alu_ready = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//     val sdp_nrdma2dp_alu_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(UInt((conf.AM_DW2+1).W))) else None

//     val sdp_nrdma2dp_mul_valid = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//     val sdp_nrdma2dp_mul_ready = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//     val sdp_nrdma2dp_mul_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(UInt((conf.AM_DW2+1).W))) else None
 
//     val sdp2mcif_rd_req_valid = Output(Bool())
//     val sdp2mcif_rd_req_ready = Input(Bool())
//     val sdp2mcif_rd_req_pd = Output(UInt(conf.NVDLA_DMA_RD_REQ.W))
//     val sdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())
//     val mcif2sdp_rd_rsp_valid = Input(Bool())
//     val mcif2sdp_rd_rsp_ready = Output(Bool())  
//     val mcif2sdp_rd_rsp_pd = Input(UInt(conf.NVDLA_DMA_RD_RSP.W))

//     val sdp_mrdma2cmux_valid = Output(Bool())
//     val sdp_mrdma2cmux_ready = Input(Bool())
//     val sdp_mrdma2cmux_pd = Output(UInt((conf.DP_DIN_DW+2).W))

//     val dla_clk_ovr_on_sync = Input(Clock())
//     val global_clk_ovr_on_sync = Input(Clock())
//     val tmc2slcg_disable_clock_gating = Input(Bool())
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
//     val reg2dp_batch_number = Wire(UInt(5.W))
//     val reg2dp_op_en = Wire(Bool())
//     val reg2dp_flying_mode = Wire(Bool())
//     val reg2dp_src_ram_type = Wire(Bool())
//     val reg2dp_in_precision = Wire(UInt(2.W))
//     val reg2dp_out_precision = Wire(UInt(2.W))
//     val reg2dp_perf_dma_en = Wire(Bool())
//     val reg2dp_perf_nan_inf_count_en = Wire(Bool())
//     val reg2dp_proc_precision = Wire(UInt(2.W))
//     val reg2dp_src_base_addr_high = Wire(UInt(32.W))
//     val reg2dp_src_base_addr_low = Wire(UInt(32.W))
//     val reg2dp_src_line_stride = Wire(UInt(32.W))
//     val reg2dp_src_surface_stride = Wire(UInt(32.W))
//     val reg2dp_width = Wire(UInt(13.W))
//     val reg2dp_height = Wire(UInt(13.W))
//     val reg2dp_channel = Wire(UInt(13.W))
//     val reg2dp_winograd = Wire(Bool())
//     val slcg_op_en = Wire(UInt(4.W))

//     //=======================================
//     // M-RDMA
//     val mrdma_op_en = Wire(Bool())
//     val mrdma_slcg_op_en = Wire(Bool())
//     val mrdma_disable = Wire(Bool())
//     val u_mrdma = Module(new NV_NVDLA_SDP_mrdma)
//     u_mrdma.io.nvdla_core_clk := io.nvdla_core_clk
//     u_mrdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_mrdma.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync
//     u_mrdma.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//     u_mrdma.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//     u_mrdma.io.mrdma_slcg_op_en := mrdma_slcg_op_en
//     u_mrdma.io.mrdma_disable := mrdma_disable
//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         u_mrdma.io.cvif2sdp_rd_rsp_valid.get := io.cvif2sdp_rd_rsp_valid.get
//         io.cvif2sdp_rd_rsp_ready.get := u_mrdma.io.cvif2sdp_rd_rsp_ready.get
//         u_mrdma.io.cvif2sdp_rd_rsp_pd.get := io.cvif2sdp_rd_rsp_pd.get
//         io.sdp2cvif_rd_cdt_lat_fifo_pop.get := u_mrdma.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
//         io.sdp2cvif_rd_req_valid.get := u_mrdma.io.sdp2cvif_rd_req_valid.get
//         u_mrdma.io.sdp2cvif_rd_req_ready.get := io.sdp2cvif_rd_req_ready.get
//         io.sdp2cvif_rd_req_pd.get := u_mrdma.io.sdp2cvif_rd_req_pd.get
//     }
//     io.sdp2mcif_rd_cdt_lat_fifo_pop := u_mrdma.io.sdp2mcif_rd_cdt_lat_fifo_pop
//     io.sdp2mcif_rd_req_valid := u_mrdma.io.sdp2mcif_rd_req_valid
//     u_mrdma.io.sdp2mcif_rd_req_ready := io.sdp2mcif_rd_req_ready
//     io.sdp2mcif_rd_req_pd := u_mrdma.io.sdp2mcif_rd_req_pd
//     u_mrdma.io.mcif2sdp_rd_rsp_valid := io.mcif2sdp_rd_rsp_valid
//     io.mcif2sdp_rd_rsp_ready := u_mrdma.io.mcif2sdp_rd_rsp_ready
//     u_mrdma.io.mcif2sdp_rd_rsp_pd := io.mcif2sdp_rd_rsp_pd
//     io.sdp_mrdma2cmux_valid := u_mrdma.io.sdp_mrdma2cmux_valid
//     u_mrdma.io.sdp_mrdma2cmux_ready := io.sdp_mrdma2cmux_ready
//     io.sdp_mrdma2cmux_pd := u_mrdma.io.sdp_mrdma2cmux_pd
//     u_mrdma.io.reg2dp_op_en := mrdma_op_en
//     u_mrdma.io.reg2dp_batch_number := reg2dp_batch_number
//     u_mrdma.io.reg2dp_channel := reg2dp_channel
//     u_mrdma.io.reg2dp_height := reg2dp_height
//     u_mrdma.io.reg2dp_width := reg2dp_width
//     u_mrdma.io.reg2dp_in_precision := reg2dp_in_precision
//     u_mrdma.io.reg2dp_proc_precision := reg2dp_proc_precision
//     u_mrdma.io.reg2dp_src_ram_type := reg2dp_src_ram_type
//     u_mrdma.io.reg2dp_src_base_addr_high := reg2dp_src_base_addr_high
//     u_mrdma.io.reg2dp_src_base_addr_low := reg2dp_src_base_addr_low
//     u_mrdma.io.reg2dp_src_line_stride := reg2dp_src_line_stride
//     u_mrdma.io.reg2dp_src_surface_stride := reg2dp_src_surface_stride
//     u_mrdma.io.reg2dp_perf_dma_en := reg2dp_perf_dma_en
//     u_mrdma.io.reg2dp_perf_nan_inf_count_en := reg2dp_perf_nan_inf_count_en
//     val mrdma_done = u_mrdma.io.dp2reg_done 
//     val dp2reg_mrdma_stall = u_mrdma.io.dp2reg_mrdma_stall
//     val dp2reg_status_inf_input_num = u_mrdma.io.dp2reg_status_inf_input_num 
//     val dp2reg_status_nan_input_num = u_mrdma.io.dp2reg_status_nan_input_num

//     val brdma_slcg_op_en = Wire(Bool())
//     val brdma_disable = Wire(Bool())
//     val brdma_done = Wire(Bool())
//     val brdma_op_en = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_brdma_data_mode = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_brdma_data_size = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_brdma_data_use = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(UInt(2.W))) else None
//     val reg2dp_brdma_disable = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_brdma_ram_type = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_bs_base_addr_high = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_bs_base_addr_low = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_bs_batch_stride = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_bs_line_stride = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_bs_surface_stride = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(UInt(32.W))) else None
//     val dp2reg_brdma_stall = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(UInt(32.W))) else None
//     val u_brdma = if(conf.NVDLA_SDP_BS_ENABLE) Some(Module(new NV_NVDLA_SDP_brdma)) else None
//     if(conf.NVDLA_SDP_BS_ENABLE){
//         u_brdma.get.io.nvdla_core_clk := io.nvdla_core_clk 
//         u_brdma.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//         u_brdma.get.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
//         u_brdma.get.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//         u_brdma.get.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//         u_brdma.get.io.brdma_slcg_op_en := brdma_slcg_op_en
//         u_brdma.get.io.brdma_disable := brdma_disable
//         if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//             u_brdma.get.io.cvif2sdp_b_rd_rsp_valid.get := io.cvif2sdp_b_rd_rsp_valid.get
//             io.cvif2sdp_b_rd_rsp_ready.get := u_brdma.get.io.cvif2sdp_b_rd_rsp_ready.get
//             u_brdma.get.io.cvif2sdp_b_rd_rsp_pd.get := io.cvif2sdp_b_rd_rsp_pd.get
//             io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get := u_brdma.get.io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get
//             io.sdp_b2cvif_rd_req_valid.get := u_brdma.get.io.sdp_b2cvif_rd_req_valid.get
//             u_brdma.get.io.sdp_b2cvif_rd_req_ready.get := io.sdp_b2cvif_rd_req_ready.get
//             io.sdp_b2cvif_rd_req_pd.get := u_brdma.get.io.sdp_b2cvif_rd_req_pd.get
//         }
//         io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get := u_brdma.get.io.sdp_b2mcif_rd_cdt_lat_fifo_pop
//         io.sdp_b2mcif_rd_req_valid.get := u_brdma.get.io.sdp_b2mcif_rd_req_valid
//         u_brdma.get.io.sdp_b2mcif_rd_req_ready := io.sdp_b2mcif_rd_req_ready.get
//         io.sdp_b2mcif_rd_req_pd.get := u_brdma.get.io.sdp_b2mcif_rd_req_pd
//         u_brdma.get.io.mcif2sdp_b_rd_rsp_valid := io.mcif2sdp_b_rd_rsp_valid.get
//         io.mcif2sdp_b_rd_rsp_ready.get := u_brdma.get.io.mcif2sdp_b_rd_rsp_ready
//         u_brdma.get.io.mcif2sdp_b_rd_rsp_pd := io.mcif2sdp_b_rd_rsp_pd.get
//         io.sdp_brdma2dp_alu_valid.get := u_brdma.get.io.sdp_brdma2dp_alu_valid 
//         u_brdma.get.io.sdp_brdma2dp_alu_ready := io.sdp_brdma2dp_alu_ready.get
//         io.sdp_brdma2dp_alu_pd.get := u_brdma.get.io.sdp_brdma2dp_alu_pd
//         io.sdp_brdma2dp_mul_valid.get := u_brdma.get.io.sdp_brdma2dp_mul_valid
//         u_brdma.get.io.sdp_brdma2dp_mul_ready := io.sdp_brdma2dp_mul_ready.get
//         io.sdp_brdma2dp_mul_pd.get := u_brdma.get.io.sdp_brdma2dp_mul_pd
//         u_brdma.get.io.reg2dp_op_en := brdma_op_en.get
//         u_brdma.get.io.reg2dp_batch_number := reg2dp_batch_number
//         u_brdma.get.io.reg2dp_winograd := reg2dp_winograd
//         u_brdma.get.io.reg2dp_channel := reg2dp_channel
//         u_brdma.get.io.reg2dp_height := reg2dp_height
//         u_brdma.get.io.reg2dp_width := reg2dp_width
//         u_brdma.get.io.reg2dp_brdma_data_mode := reg2dp_brdma_data_mode.get
//         u_brdma.get.io.reg2dp_brdma_data_size := reg2dp_brdma_data_size.get
//         u_brdma.get.io.reg2dp_brdma_data_use := reg2dp_brdma_data_use.get
//         u_brdma.get.io.reg2dp_brdma_ram_type := reg2dp_brdma_ram_type.get
//         u_brdma.get.io.reg2dp_bs_base_addr_high := reg2dp_bs_base_addr_high.get
//         u_brdma.get.io.reg2dp_bs_base_addr_low := reg2dp_bs_base_addr_low.get(31, conf.AM_AW)
//         u_brdma.get.io.reg2dp_bs_line_stride := reg2dp_bs_line_stride.get(31, conf.AM_AW)
//         u_brdma.get.io.reg2dp_bs_surface_stride := reg2dp_bs_surface_stride.get(31, conf.AM_AW)
//         u_brdma.get.io.reg2dp_out_precision := reg2dp_out_precision
//         u_brdma.get.io.reg2dp_proc_precision := reg2dp_proc_precision
//         u_brdma.get.io.reg2dp_perf_dma_en := reg2dp_perf_dma_en
//         dp2reg_brdma_stall.get := u_brdma.get.io.dp2reg_brdma_stall
//         brdma_done := u_brdma.get.io.dp2reg_done
//     }

//     val nrdma_slcg_op_en = Wire(Bool())
//     val nrdma_disable = Wire(Bool())
//     val nrdma_done = Wire(Bool())
//     val nrdma_op_en = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_nrdma_data_mode = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_nrdma_data_size = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_nrdma_data_use = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(UInt(2.W))) else None
//     val reg2dp_nrdma_disable = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_nrdma_ram_type = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_bn_base_addr_high = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_bn_base_addr_low = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_bn_batch_stride = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_bn_line_stride = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_bn_surface_stride = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(UInt(32.W))) else None
//     val dp2reg_nrdma_stall = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(UInt(32.W))) else None
//     val u_nrdma = if(conf.NVDLA_SDP_BN_ENABLE) Some(Module(new NV_NVDLA_SDP_nrdma)) else None
//     if(conf.NVDLA_SDP_BN_ENABLE){
//         u_nrdma.get.io.nvdla_core_clk := io.nvdla_core_clk 
//         u_nrdma.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//         u_nrdma.get.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
//         u_nrdma.get.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//         u_nrdma.get.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//         u_nrdma.get.io.nrdma_slcg_op_en := nrdma_slcg_op_en
//         u_nrdma.get.io.nrdma_disable := nrdma_disable
//         if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//             u_nrdma.get.io.cvif2sdp_n_rd_rsp_valid.get := io.cvif2sdp_n_rd_rsp_valid.get
//             io.cvif2sdp_n_rd_rsp_ready.get := u_nrdma.get.io.cvif2sdp_n_rd_rsp_ready.get
//             u_nrdma.get.io.cvif2sdp_n_rd_rsp_pd.get := io.cvif2sdp_n_rd_rsp_pd.get
//             io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get := u_nrdma.get.io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get
//             io.sdp_n2cvif_rd_req_valid.get := u_nrdma.get.io.sdp_n2cvif_rd_req_valid.get
//             u_nrdma.get.io.sdp_n2cvif_rd_req_ready.get := io.sdp_n2cvif_rd_req_ready.get
//             io.sdp_n2cvif_rd_req_pd.get := u_nrdma.get.io.sdp_n2cvif_rd_req_pd.get
//         }
//         io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get := u_nrdma.get.io.sdp_n2mcif_rd_cdt_lat_fifo_pop
//         io.sdp_n2mcif_rd_req_valid.get := u_nrdma.get.io.sdp_n2mcif_rd_req_valid
//         u_nrdma.get.io.sdp_n2mcif_rd_req_ready := io.sdp_n2mcif_rd_req_ready.get
//         io.sdp_n2mcif_rd_req_pd.get := u_nrdma.get.io.sdp_n2mcif_rd_req_pd 
//         u_nrdma.get.io.mcif2sdp_n_rd_rsp_valid := io.mcif2sdp_n_rd_rsp_valid.get
//         io.mcif2sdp_n_rd_rsp_ready.get := u_nrdma.get.io.mcif2sdp_n_rd_rsp_ready
//         u_nrdma.get.io.mcif2sdp_n_rd_rsp_pd := io.mcif2sdp_n_rd_rsp_pd.get
//         io.sdp_nrdma2dp_alu_valid.get := u_nrdma.get.io.sdp_nrdma2dp_alu_valid
//         u_nrdma.get.io.sdp_nrdma2dp_alu_ready := io.sdp_nrdma2dp_alu_ready.get
//         io.sdp_nrdma2dp_alu_pd.get := u_nrdma.get.io.sdp_nrdma2dp_alu_pd
//         io.sdp_nrdma2dp_mul_valid.get := u_nrdma.get.io.sdp_nrdma2dp_mul_valid
//         u_nrdma.get.io.sdp_nrdma2dp_mul_ready := io.sdp_nrdma2dp_mul_ready.get
//         io.sdp_nrdma2dp_mul_pd.get := u_nrdma.get.io.sdp_nrdma2dp_mul_pd
//         u_nrdma.get.io.reg2dp_op_en := nrdma_op_en.get
//         u_nrdma.get.io.reg2dp_batch_number := reg2dp_batch_number
//         u_nrdma.get.io.reg2dp_winograd := reg2dp_winograd
//         u_nrdma.get.io.reg2dp_channel := reg2dp_channel
//         u_nrdma.get.io.reg2dp_height := reg2dp_height
//         u_nrdma.get.io.reg2dp_width := reg2dp_width
//         u_nrdma.get.io.reg2dp_nrdma_data_mode := reg2dp_nrdma_data_mode.get
//         u_nrdma.get.io.reg2dp_nrdma_data_size := reg2dp_nrdma_data_size.get
//         u_nrdma.get.io.reg2dp_nrdma_data_use := reg2dp_nrdma_data_use.get
//         u_nrdma.get.io.reg2dp_nrdma_ram_type := reg2dp_nrdma_ram_type.get
//         u_nrdma.get.io.reg2dp_bn_base_addr_high := reg2dp_bn_base_addr_high.get
//         u_nrdma.get.io.reg2dp_bn_base_addr_low := reg2dp_bn_base_addr_low.get(31, conf.AM_AW)
//         u_nrdma.get.io.reg2dp_bn_line_stride := reg2dp_bn_line_stride.get(31, conf.AM_AW)
//         u_nrdma.get.io.reg2dp_bn_surface_stride := reg2dp_bn_surface_stride.get(31, conf.AM_AW)
//         u_nrdma.get.io.reg2dp_out_precision := reg2dp_out_precision
//         u_nrdma.get.io.reg2dp_proc_precision := reg2dp_proc_precision
//         u_nrdma.get.io.reg2dp_perf_dma_en := reg2dp_perf_dma_en
//         dp2reg_nrdma_stall.get := u_nrdma.get.io.dp2reg_nrdma_stall
//         nrdma_done := u_nrdma.get.io.dp2reg_done
//     }
        
//     val erdma_slcg_op_en = Wire(Bool())
//     val erdma_disable = Wire(Bool())
//     val erdma_done = Wire(Bool())
//     val erdma_op_en = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_erdma_data_mode = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_erdma_data_size = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_erdma_data_use = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt(2.W))) else None
//     val reg2dp_erdma_disable = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_erdma_ram_type = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val reg2dp_ew_base_addr_high = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_ew_base_addr_low = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_ew_batch_stride = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_ew_line_stride = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt(32.W))) else None
//     val reg2dp_ew_surface_stride = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt(32.W))) else None
//     val dp2reg_erdma_stall = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt(32.W))) else None
//     val u_erdma = if(conf.NVDLA_SDP_EW_ENABLE) Some(Module(new NV_NVDLA_SDP_erdma)) else None
//     if(conf.NVDLA_SDP_EW_ENABLE){
//         u_erdma.get.io.nvdla_core_clk := io.nvdla_core_clk 
//         u_erdma.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//         u_erdma.get.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
//         u_erdma.get.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//         u_erdma.get.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//         u_erdma.get.io.erdma_slcg_op_en := erdma_slcg_op_en
//         u_erdma.get.io.erdma_disable := erdma_disable
//         if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//             u_erdma.get.io.cvif2sdp_e_rd_rsp_valid.get := io.cvif2sdp_e_rd_rsp_valid.get
//             io.cvif2sdp_e_rd_rsp_ready.get := u_erdma.get.io.cvif2sdp_e_rd_rsp_ready.get
//             u_erdma.get.io.cvif2sdp_e_rd_rsp_pd.get := io.cvif2sdp_e_rd_rsp_pd.get
//             io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get := u_erdma.get.io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get
//             io.sdp_e2cvif_rd_req_valid.get := u_erdma.get.io.sdp_e2cvif_rd_req_valid.get
//             u_erdma.get.io.sdp_e2cvif_rd_req_ready.get := io.sdp_e2cvif_rd_req_ready.get
//             io.sdp_e2cvif_rd_req_pd.get := u_erdma.get.io.sdp_e2cvif_rd_req_pd.get
//         }
//         io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get := u_erdma.get.io.sdp_e2mcif_rd_cdt_lat_fifo_pop
//         io.sdp_e2mcif_rd_req_valid.get := u_erdma.get.io.sdp_e2mcif_rd_req_valid
//         u_erdma.get.io.sdp_e2mcif_rd_req_ready := io.sdp_e2mcif_rd_req_ready.get
//         io.sdp_e2mcif_rd_req_pd.get := u_erdma.get.io.sdp_e2mcif_rd_req_pd 
//         u_erdma.get.io.mcif2sdp_e_rd_rsp_valid := io.mcif2sdp_e_rd_rsp_valid.get
//         io.mcif2sdp_e_rd_rsp_ready.get := u_erdma.get.io.mcif2sdp_e_rd_rsp_ready
//         u_erdma.get.io.mcif2sdp_e_rd_rsp_pd := io.mcif2sdp_e_rd_rsp_pd.get
//         io.sdp_erdma2dp_alu_valid.get := u_erdma.get.io.sdp_erdma2dp_alu_valid
//         u_erdma.get.io.sdp_erdma2dp_alu_ready := io.sdp_erdma2dp_alu_ready.get
//         io.sdp_erdma2dp_alu_pd.get := u_erdma.get.io.sdp_erdma2dp_alu_pd
//         io.sdp_erdma2dp_mul_valid.get := u_erdma.get.io.sdp_erdma2dp_mul_valid
//         u_erdma.get.io.sdp_erdma2dp_mul_ready := io.sdp_erdma2dp_mul_ready.get
//         io.sdp_erdma2dp_mul_pd.get := u_erdma.get.io.sdp_erdma2dp_mul_pd
//         u_erdma.get.io.reg2dp_op_en := erdma_op_en.get
//         u_erdma.get.io.reg2dp_batch_number := reg2dp_batch_number
//         u_erdma.get.io.reg2dp_winograd := reg2dp_winograd
//         u_erdma.get.io.reg2dp_channel := reg2dp_channel
//         u_erdma.get.io.reg2dp_height := reg2dp_height
//         u_erdma.get.io.reg2dp_width := reg2dp_width
//         u_erdma.get.io.reg2dp_erdma_data_mode := reg2dp_erdma_data_mode.get
//         u_erdma.get.io.reg2dp_erdma_data_size := reg2dp_erdma_data_size.get
//         u_erdma.get.io.reg2dp_erdma_data_use := reg2dp_erdma_data_use.get
//         u_erdma.get.io.reg2dp_erdma_ram_type := reg2dp_erdma_ram_type.get
//         u_erdma.get.io.reg2dp_ew_base_addr_high := reg2dp_ew_base_addr_high.get
//         u_erdma.get.io.reg2dp_ew_base_addr_low := reg2dp_ew_base_addr_low.get(31, conf.AM_AW)
//         u_erdma.get.io.reg2dp_ew_line_stride := reg2dp_ew_line_stride.get(31, conf.AM_AW)
//         u_erdma.get.io.reg2dp_ew_surface_stride := reg2dp_ew_surface_stride.get(31, conf.AM_AW)
//         u_erdma.get.io.reg2dp_out_precision := reg2dp_out_precision
//         u_erdma.get.io.reg2dp_proc_precision := reg2dp_proc_precision
//         u_erdma.get.io.reg2dp_perf_dma_en := reg2dp_perf_dma_en
//         dp2reg_erdma_stall.get := u_erdma.get.io.dp2reg_erdma_stall
//         erdma_done := u_erdma.get.io.dp2reg_done
//     }
    
//     //=======================================
//     // Configuration Register File
//     mrdma_slcg_op_en := slcg_op_en(0)
//     brdma_slcg_op_en := slcg_op_en(1)
//     nrdma_slcg_op_en := slcg_op_en(2)
//     erdma_slcg_op_en := slcg_op_en(3)

//     val dp2reg_done = Wire(Bool())
//     val mrdma_done_pending = RegInit(false.B)
//     when(dp2reg_done){
//         mrdma_done_pending := false.B
//     }
//     .elsewhen(mrdma_done){
//         mrdma_done_pending := true.B
//     }
//     mrdma_op_en := reg2dp_op_en & ~mrdma_done_pending & ~mrdma_disable

//     val brdma_done_pending = if(conf.NVDLA_SDP_BS_ENABLE) RegInit(false.B) else false.B
//     if(conf.NVDLA_SDP_BS_ENABLE){
//         when(dp2reg_done){
//             brdma_done_pending := false.B
//         }
//         .elsewhen(brdma_done){
//             brdma_done_pending := true.B
//         }
//         brdma_op_en.get := reg2dp_op_en & ~brdma_done_pending & ~brdma_disable
//     }
//     else{
//         brdma_done := false.B
//     }

//     val nrdma_done_pending = if(conf.NVDLA_SDP_BN_ENABLE) RegInit(false.B) else false.B
//     if(conf.NVDLA_SDP_BN_ENABLE){
//         when(dp2reg_done){
//             nrdma_done_pending := false.B
//         }
//         .elsewhen(nrdma_done){
//             nrdma_done_pending := true.B
//         }
//         nrdma_op_en.get := reg2dp_op_en & ~nrdma_done_pending & ~nrdma_disable
//     }
//     else{
//         nrdma_done := false.B
//     }

//     val erdma_done_pending = if(conf.NVDLA_SDP_EW_ENABLE) RegInit(false.B) else false.B
//     if(conf.NVDLA_SDP_EW_ENABLE){
//         when(dp2reg_done){
//             erdma_done_pending := false.B
//         }
//         .elsewhen(erdma_done){
//             erdma_done_pending := true.B
//         }
//         erdma_op_en.get := reg2dp_op_en & ~erdma_done_pending & ~erdma_disable
//     }
//     else{
//         erdma_done := false.B
//     }

//     dp2reg_done := reg2dp_op_en & ((mrdma_done_pending || mrdma_done || mrdma_disable)&
//                                   (brdma_done_pending || brdma_done || brdma_disable)&
//                                   (nrdma_done_pending || nrdma_done || nrdma_disable)&
//                                   (erdma_done_pending || erdma_done || erdma_disable));
//     mrdma_disable := reg2dp_flying_mode === 1.U
//     if(conf.NVDLA_SDP_BS_ENABLE){
//         brdma_disable := reg2dp_brdma_disable.get === 1.U
//         nrdma_disable := reg2dp_brdma_disable.get === 1.U
//         erdma_disable := reg2dp_erdma_disable.get === 1.U
//     }
//     else{
//         brdma_disable := true.B
//         nrdma_disable := true.B
//         erdma_disable := true.B
//     }

//     val u_reg = Module(new NV_NVDLA_SDP_RDMA_reg)
//     u_reg.io.nvdla_core_clk := io.nvdla_core_clk
//     u_reg.io.csb2sdp_rdma_req_pvld := io.csb2sdp_rdma_req_pvld 
//     io.csb2sdp_rdma_req_prdy := u_reg.io.csb2sdp_rdma_req_prdy
//     u_reg.io.csb2sdp_rdma_req_pd := io.csb2sdp_rdma_req_pd
//     io.sdp_rdma2csb_resp_valid := u_reg.io.sdp_rdma2csb_resp_valid
//     io.sdp_rdma2csb_resp_pd := u_reg.io.sdp_rdma2csb_resp_pd
//     slcg_op_en := u_reg.io.slcg_op_en
//     u_reg.io.dp2reg_done := dp2reg_done
//     u_reg.io.dp2reg_mrdma_stall := dp2reg_mrdma_stall
//     u_reg.io.dp2reg_status_inf_input_num := dp2reg_status_inf_input_num
//     u_reg.io.dp2reg_status_nan_input_num := dp2reg_status_nan_input_num
//     if(conf.NVDLA_SDP_BN_ENABLE){
//         reg2dp_nrdma_data_mode.get := u_reg.io.reg2dp_nrdma_data_mode
//         reg2dp_nrdma_data_size.get := u_reg.io.reg2dp_nrdma_data_size
//         reg2dp_nrdma_data_use.get := u_reg.io.reg2dp_nrdma_data_use
//         reg2dp_nrdma_disable.get := u_reg.io.reg2dp_nrdma_disable
//         reg2dp_nrdma_ram_type.get := u_reg.io.reg2dp_nrdma_ram_type
//         reg2dp_bn_base_addr_high.get := u_reg.io.reg2dp_bn_base_addr_high
//         reg2dp_bn_base_addr_low.get := u_reg.io.reg2dp_bn_base_addr_low
//         reg2dp_bn_batch_stride.get := u_reg.io.reg2dp_bn_batch_stride
//         reg2dp_bn_line_stride.get := u_reg.io.reg2dp_bn_line_stride
//         reg2dp_bn_surface_stride.get := u_reg.io.reg2dp_bn_surface_stride
//         u_reg.io.dp2reg_nrdma_stall := dp2reg_nrdma_stall.get
//     }
//     u_reg.io.dp2reg_nrdma_stall := 0.U
//    if(conf.NVDLA_SDP_BS_ENABLE){
//         reg2dp_brdma_data_mode.get := u_reg.io.reg2dp_brdma_data_mode
//         reg2dp_brdma_data_size.get := u_reg.io.reg2dp_brdma_data_size
//         reg2dp_brdma_data_use.get := u_reg.io.reg2dp_brdma_data_use
//         reg2dp_brdma_disable.get := u_reg.io.reg2dp_brdma_disable
//         reg2dp_brdma_ram_type.get := u_reg.io.reg2dp_brdma_ram_type
//         reg2dp_bs_base_addr_high.get := u_reg.io.reg2dp_bs_base_addr_high
//         reg2dp_bs_base_addr_low.get := u_reg.io.reg2dp_bs_base_addr_low
//         reg2dp_bs_batch_stride.get := u_reg.io.reg2dp_bs_batch_stride
//         reg2dp_bs_line_stride.get := u_reg.io.reg2dp_bs_line_stride
//         reg2dp_bs_surface_stride.get := u_reg.io.reg2dp_bs_surface_stride
//         u_reg.io.dp2reg_brdma_stall := dp2reg_brdma_stall.get
//     }
//     u_reg.io.dp2reg_brdma_stall := 0.U
//    if(conf.NVDLA_SDP_EW_ENABLE){
//         reg2dp_erdma_data_mode.get := u_reg.io.reg2dp_erdma_data_mode
//         reg2dp_erdma_data_size.get := u_reg.io.reg2dp_erdma_data_size
//         reg2dp_erdma_data_use.get := u_reg.io.reg2dp_erdma_data_use
//         reg2dp_erdma_disable.get := u_reg.io.reg2dp_erdma_disable
//         reg2dp_erdma_ram_type.get := u_reg.io.reg2dp_erdma_ram_type
//         reg2dp_ew_base_addr_high.get := u_reg.io.reg2dp_ew_base_addr_high
//         reg2dp_ew_base_addr_low.get := u_reg.io.reg2dp_ew_base_addr_low
//         reg2dp_ew_batch_stride.get := u_reg.io.reg2dp_ew_batch_stride
//         reg2dp_ew_line_stride.get := u_reg.io.reg2dp_ew_line_stride
//         reg2dp_ew_surface_stride.get := u_reg.io.reg2dp_ew_surface_stride
//         u_reg.io.dp2reg_erdma_stall := dp2reg_erdma_stall.get
//     }
//     u_reg.io.dp2reg_erdma_stall := 0.U
//     reg2dp_op_en := u_reg.io.reg2dp_op_en
//     reg2dp_batch_number := u_reg.io.reg2dp_batch_number
//     reg2dp_winograd := u_reg.io.reg2dp_winograd
//     reg2dp_flying_mode := u_reg.io.reg2dp_flying_mode
//     reg2dp_channel := u_reg.io.reg2dp_channel
//     reg2dp_height := u_reg.io.reg2dp_height
//     reg2dp_width := u_reg.io.reg2dp_width
//     reg2dp_in_precision := u_reg.io.reg2dp_in_precision
//     reg2dp_out_precision := u_reg.io.reg2dp_out_precision
//     reg2dp_proc_precision := u_reg.io.reg2dp_proc_precision
//     reg2dp_src_ram_type := u_reg.io.reg2dp_src_ram_type
//     reg2dp_src_base_addr_high := u_reg.io.reg2dp_src_base_addr_high
//     reg2dp_src_base_addr_low := u_reg.io.reg2dp_src_base_addr_low
//     reg2dp_src_line_stride := u_reg.io.reg2dp_src_line_stride
//     reg2dp_src_surface_stride := u_reg.io.reg2dp_src_surface_stride
//     reg2dp_perf_dma_en := u_reg.io.reg2dp_perf_dma_en
//     reg2dp_perf_nan_inf_count_en := u_reg.io.reg2dp_perf_nan_inf_count_en

    
// }}

// object NV_NVDLA_SDP_rdmaDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_rdma())
// }



