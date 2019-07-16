// package nvdla

// import chisel3._
// import chisel3.util._
// import chisel3.experimental._

// class NV_NVDLA_SDP_rdma(implicit conf: sdpConfiguration) extends Module {

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
//     u_mrdma.io.reg2dp_batch_number := io.reg2dp_batch_number
//     u_mrdma.io.reg2dp_channel := io.reg2dp_channel
//     u_mrdma.io.reg2dp_height := io.reg2dp_height
//     u_mrdma.io.reg2dp_width := io.reg2dp_width
//     u_mrdma.io.reg2dp_in_precision := io.reg2dp_in_precision
//     u_mrdma.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_mrdma.io.reg2dp_src_ram_type := io.reg2dp_src_ram_type
//     u_mrdma.io.reg2dp_src_base_addr_high := io.reg2dp_src_base_addr_high
//     u_mrdma.io.reg2dp_src_base_addr_low := io.reg2dp_src_base_addr_low
//     u_mrdma.io.reg2dp_src_line_stride := io.reg2dp_src_line_stride
//     u_mrdma.io.reg2dp_src_surface_stride := io.reg2dp_src_surface_stride
//     u_mrdma.io.reg2dp_perf_dma_en := io.reg2dp_perf_dma_en
//     u_mrdma.io.reg2dp_perf_nan_inf_count_en := io.reg2dp_perf_nan_inf_count_en
//     val mrdma_done = u_mrdma.io.dp2reg_done 
//     val dp2reg_mrdma_stall = u_mrdma.io.dp2reg_mrdma_stall
//     val dp2reg_status_inf_input_num = u_mrdma.io.dp2reg_status_inf_input_num 
//     val dp2reg_status_nan_input_num = u_mrdma.io.dp2reg_status_nan_input_num

//     val brdma_slcg_op_en = Wire(Bool())
//     val brdma_disable = Wire(Bool())
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

//         }
//     }




 

// }}

// object NV_NVDLA_SDP_rdmaDriver extends App {
//   implicit val conf: sdpConfiguration = new sdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_rdma())
// }



