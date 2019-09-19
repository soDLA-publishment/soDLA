// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_partition_p(implicit val conf: nvdlaConfiguration) extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val test_mode = Input(Bool())
//         val direct_reset_ = Input(Bool())

//         val global_clk_ovr_on = Input(Clock())
//         val tmc2slcg_disable_clock_gating = Input(Bool())

//         val nvdla_core_clk = Input(Clock())
//         val nvdla_clk_ovr_on = Input(Clock())

//         val dla_reset_rstn = Input(Bool())
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         //cacc2sdp
//         val cacc2sdp_valid = Input(Bool())  /* data valid */
//         val cacc2sdp_ready = Output(Bool())  /* data return handshake */
//         val cacc2sdp_pd = Input(UInt((conf.NVDLA_SDP_MAX_THROUGHPUT*32+2).W)) 
//         //csb2sdp_rdma
//         val csb2sdp_rdma_req_pvld = Input(Bool())   /* data valid */
//         val csb2sdp_rdma_req_prdy = Output(Bool())  /* data return handshake */
//         val csb2sdp_rdma_req_pd = Input(UInt(63.W))
//         //csb2sdp
//         val csb2sdp_req_pvld = Input(Bool())    /* data valid */
//         val csb2sdp_req_prdy = Output(Bool())   /* data return handshake */
//         val csb2sdp_req_pd = Input(UInt(63.W))

//         val sdp2csb_resp_valid = Output(Bool()) /* data valid */
//         val sdp2csb_resp_pd = Output(UInt(34.W))    /* pkt_id_width=1 pkt_widths=33,33  */
        
//         val sdp_b2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val sdp_b2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None /* data valid */
//         val sdp_b2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None /* data return handshake */
//         val sdp_b2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))) else None
//         val cvif2sdp_b_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None /* data valid */
//         val cvif2sdp_b_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None /* data return handshake */
//         val cvif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT).W))) else None

//         val sdp_n2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//         val sdp_n2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None /* data valid */
//         val sdp_n2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None /* data return handshake */
//         val sdp_n2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))) else None
//         val cvif2sdp_n_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None /* data valid */
//         val cvif2sdp_n_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None /* data return handshake */
//         val cvif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT).W))) else None

//         val sdp_e2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//         val sdp_e2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None /* data valid */
//         val sdp_e2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None /* data return handshake */
//         val sdp_e2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))) else None
//         val cvif2sdp_e_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None /* data valid */
//         val cvif2sdp_e_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None /* data return handshake */
//         val cvif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT).W))) else None

//         val sdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//         val sdp2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None /* data valid */
//         val sdp2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None /* data return handshake */
//         val sdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))) else None
//         val cvif2sdp_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None /* data valid */
//         val cvif2sdp_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None /* data return handshake */
//         val cvif2sdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT).W))) else None    

//         val sdp2cvif_wr_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None    /* data valid */
//         val sdp2cvif_wr_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None     /* data return handshake */
//         val sdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT+1).W))) else None    /* pkt_id_width=1 pkt_widths=78,514  */
//         val cvif2sdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None

//         val sdp2glb_done_intr_pd = Output(UInt(2.W))

//         val sdp_b2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val sdp_b2mcif_rd_req_valid = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None /* data valid */
//         val sdp_b2mcif_rd_req_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None /* data return handshake */
//         val sdp_b2mcif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))) else None
//         val mcif2sdp_b_rd_rsp_valid = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None /* data valid */
//         val mcif2sdp_b_rd_rsp_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None /* data return handshake */
//         val mcif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT).W))) else None

//         val sdp_n2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//         val sdp_n2mcif_rd_req_valid = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None /* data valid */
//         val sdp_n2mcif_rd_req_ready = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None /* data return handshake */
//         val sdp_n2mcif_rd_req_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))) else None
//         val mcif2sdp_n_rd_rsp_valid = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None /* data valid */
//         val mcif2sdp_n_rd_rsp_ready = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None /* data return handshake */
//         val mcif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT).W))) else None

//         val sdp_e2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//         val sdp_e2mcif_rd_req_valid = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None /* data valid */
//         val sdp_e2mcif_rd_req_ready = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None /* data return handshake */
//         val sdp_e2mcif_rd_req_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))) else None
//         val mcif2sdp_e_rd_rsp_valid = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None /* data valid */
//         val mcif2sdp_e_rd_rsp_ready = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None /* data return handshake */
//         val mcif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT).W))) else None

//         val sdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())
//         val sdp2mcif_rd_req_valid = Output(Bool()) /* data valid */
//         val sdp2mcif_rd_req_ready = Input(Bool()) /* data return handshake */
//         val sdp2mcif_rd_req_pd = Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))
//         val mcif2sdp_rd_rsp_valid = Input(Bool()) /* data valid */
//         val mcif2sdp_rd_rsp_ready = Output(Bool()) /* data return handshake */
//         val mcif2sdp_rd_rsp_pd = Input(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT).W))  

//         val sdp2mcif_wr_req_valid = Output(Bool())    /* data valid */
//         val sdp2mcif_wr_req_ready = Input(Bool())     /* data return handshake */
//         val sdp2mcif_wr_req_pd = Output(UInt((conf.NVDLA_DMAIF_BW+conf.NVDLA_MEM_MASK_BIT+1).W))    /* pkt_id_width=1 pkt_widths=78,514  */
//         val mcif2sdp_wr_rsp_complete = Input(Bool())

//         val sdp2pdp_valid = Output(Bool())
//         val sdp2pdp_ready = Input(Bool())
//         val sdp2pdp_pd = Output(UInt((conf.NVDLA_SDP_MAX_THROUGHPUT*conf.NVDLA_BPE).W))

//         val sdp_rdma2csb_resp_valid = Output(Bool())
//         val sdp_rdma2csb_resp_pd = Output(UInt(34.W))
//     })
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

// ////////////////////////////////////////////////////////////////////////
// //  NVDLA Partition M:    Reset Syncer                                //
// ////////////////////////////////////////////////////////////////////////
//     val u_partition_p_reset = Module(new NV_NVDLA_reset)
//     u_partition_p_reset.io.nvdla_clk  := io.nvdla_core_clk
//     u_partition_p_reset.io.dla_reset_rstn := io.dla_reset_rstn
//     u_partition_p_reset.io.direct_reset_ := io.direct_reset_
//     u_partition_p_reset.io.test_mode := io.test_mode
//     val nvdla_core_rstn = u_partition_p_reset.io.synced_rstn
//     ////////////////////////////////////////////////////////////////////////
//     // SLCG override
//     ////////////////////////////////////////////////////////////////////////
//     val u_dla_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d)
//     u_dla_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
//     u_dla_clk_ovr_on_sync.io.sync_i := io.nvdla_clk_ovr_on
//     val dla_clk_ovr_on_sync = u_dla_clk_ovr_on_sync.io.sync_o 

//     val u_global_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d_s)
//     u_global_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
//     u_global_clk_ovr_on_sync.io.prst := nvdla_core_rstn
//     u_global_clk_ovr_on_sync.io.sync_i := io.global_clk_ovr_on
//     val global_clk_ovr_on_sync = u_global_clk_ovr_on_sync.io.sync_o 

//     ////////////////////////////////////////////////////////////////////////
//     //  NVDLA Partition P:    Single Data Processor                       //
//     ////////////////////////////////////////////////////////////////////////
//     val u_NV_NVDLA_sdp = Module(new NV_NVDLA_sdp)
//     u_NV_NVDLA_sdp.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_sdp.io.nvdla_core_rstn := nvdla_core_rstn
//     u_NV_NVDLA_sdp.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     u_NV_NVDLA_sdp.io.cacc2sdp_valid := io.cacc2sdp_valid
//     io.cacc2sdp_ready := u_NV_NVDLA_sdp.io.cacc2sdp_ready
//     u_NV_NVDLA_sdp.io.cacc2sdp_pd := io.cacc2sdp_pd

//     u_NV_NVDLA_sdp.io.csb2sdp_rdma_req_pvld := io.csb2sdp_rdma_req_pvld
//     io.csb2sdp_rdma_req_prdy := u_NV_NVDLA_sdp.io.csb2sdp_rdma_req_prdy
//     u_NV_NVDLA_sdp.io.csb2sdp_rdma_req_pd := io.csb2sdp_rdma_req_pd

//     u_NV_NVDLA_sdp.io.csb2sdp_req_pvld := io.csb2sdp_req_pvld 
//     io.csb2sdp_req_prdy := u_NV_NVDLA_sdp.io.csb2sdp_req_prdy
//     u_NV_NVDLA_sdp.io.csb2sdp_req_pd := io.csb2sdp_req_pd 

//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         if(conf.NVDLA_SDP_BS_ENABLE){
//             io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_sdp.io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get
//             io.sdp_b2cvif_rd_req_valid.get := u_NV_NVDLA_sdp.io.sdp_b2cvif_rd_req_valid.get
//             u_NV_NVDLA_sdp.io.sdp_b2cvif_rd_req_ready.get := io.sdp_b2cvif_rd_req_ready.get
//             io.sdp_b2cvif_rd_req_pd.get := u_NV_NVDLA_sdp.io.sdp_b2cvif_rd_req_pd.get
//             u_NV_NVDLA_sdp.io.cvif2sdp_b_rd_rsp_valid.get := io.cvif2sdp_b_rd_rsp_valid.get
//             io.cvif2sdp_b_rd_rsp_ready.get := u_NV_NVDLA_sdp.io.cvif2sdp_b_rd_rsp_ready.get
//             u_NV_NVDLA_sdp.io.cvif2sdp_b_rd_rsp_pd.get := io.cvif2sdp_b_rd_rsp_pd.get
//         }
//         if(conf.NVDLA_SDP_EW_ENABLE){
//             io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_sdp.io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get
//             io.sdp_e2cvif_rd_req_valid.get := u_NV_NVDLA_sdp.io.sdp_e2cvif_rd_req_valid.get
//             u_NV_NVDLA_sdp.io.sdp_e2cvif_rd_req_ready.get := io.sdp_e2cvif_rd_req_ready.get
//             io.sdp_e2cvif_rd_req_pd.get := u_NV_NVDLA_sdp.io.sdp_e2cvif_rd_req_pd.get
//             u_NV_NVDLA_sdp.io.cvif2sdp_e_rd_rsp_valid.get := io.cvif2sdp_e_rd_rsp_valid.get
//             io.cvif2sdp_e_rd_rsp_ready.get := u_NV_NVDLA_sdp.io.cvif2sdp_e_rd_rsp_ready.get
//             u_NV_NVDLA_sdp.io.cvif2sdp_e_rd_rsp_pd.get := io.cvif2sdp_e_rd_rsp_pd.get           
//         }
//         if(conf.NVDLA_SDP_BN_ENABLE){
//             io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_sdp.io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get
//             io.sdp_n2cvif_rd_req_valid.get := u_NV_NVDLA_sdp.io.sdp_n2cvif_rd_req_valid.get
//             u_NV_NVDLA_sdp.io.sdp_n2cvif_rd_req_ready.get := io.sdp_n2cvif_rd_req_ready.get
//             io.sdp_n2cvif_rd_req_pd.get := u_NV_NVDLA_sdp.io.sdp_n2cvif_rd_req_pd.get
//             u_NV_NVDLA_sdp.io.cvif2sdp_n_rd_rsp_valid.get := io.cvif2sdp_n_rd_rsp_valid.get
//             io.cvif2sdp_n_rd_rsp_ready.get := u_NV_NVDLA_sdp.io.cvif2sdp_n_rd_rsp_ready.get
//             u_NV_NVDLA_sdp.io.cvif2sdp_n_rd_rsp_pd.get := io.cvif2sdp_n_rd_rsp_pd.get           
//         }
//         io.sdp2cvif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_sdp.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
//         io.sdp2cvif_rd_req_valid.get := u_NV_NVDLA_sdp.io.sdp2cvif_rd_req_valid.get
//         u_NV_NVDLA_sdp.io.sdp2cvif_rd_req_ready.get := io.sdp2cvif_rd_req_ready.get
//         io.sdp2cvif_rd_req_pd.get := u_NV_NVDLA_sdp.io.sdp2cvif_rd_req_pd.get
//         u_NV_NVDLA_sdp.io.cvif2sdp_rd_rsp_valid.get := io.cvif2sdp_rd_rsp_valid.get
//         io.cvif2sdp_rd_rsp_ready.get := u_NV_NVDLA_sdp.io.cvif2sdp_rd_rsp_ready.get
//         u_NV_NVDLA_sdp.io.cvif2sdp_rd_rsp_pd.get := io.cvif2sdp_rd_rsp_pd.get

//         io.sdp2cvif_wr_req_valid.get := u_NV_NVDLA_sdp.io.sdp2cvif_wr_req_valid.get
//         u_NV_NVDLA_sdp.io.sdp2cvif_wr_req_ready.get := io.sdp2cvif_wr_req_ready.get
//         io.sdp2cvif_wr_req_pd.get := u_NV_NVDLA_sdp.io.sdp2cvif_wr_req_pd.get
//         u_NV_NVDLA_sdp.io.cvif2sdp_wr_rsp_complete.get := io.cvif2sdp_wr_rsp_complete.get

//     }
//     if(conf.NVDLA_SDP_BS_ENABLE){
//         io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_sdp.io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get
//         io.sdp_b2mcif_rd_req_valid.get := u_NV_NVDLA_sdp.io.sdp_b2mcif_rd_req_valid.get
//         u_NV_NVDLA_sdp.io.sdp_b2mcif_rd_req_ready.get := io.sdp_b2mcif_rd_req_ready.get
//         io.sdp_b2mcif_rd_req_pd.get := u_NV_NVDLA_sdp.io.sdp_b2mcif_rd_req_pd.get
//         u_NV_NVDLA_sdp.io.mcif2sdp_b_rd_rsp_valid.get := io.mcif2sdp_b_rd_rsp_valid.get
//         io.mcif2sdp_b_rd_rsp_ready.get := u_NV_NVDLA_sdp.io.mcif2sdp_b_rd_rsp_ready.get
//         u_NV_NVDLA_sdp.io.mcif2sdp_b_rd_rsp_pd.get := io.mcif2sdp_b_rd_rsp_pd.get
//     }
//     if(conf.NVDLA_SDP_EW_ENABLE){
//         io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_sdp.io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get
//         io.sdp_e2mcif_rd_req_valid.get := u_NV_NVDLA_sdp.io.sdp_e2mcif_rd_req_valid.get
//         u_NV_NVDLA_sdp.io.sdp_e2mcif_rd_req_ready.get := io.sdp_e2mcif_rd_req_ready.get
//         io.sdp_e2mcif_rd_req_pd.get := u_NV_NVDLA_sdp.io.sdp_e2mcif_rd_req_pd.get
//         u_NV_NVDLA_sdp.io.mcif2sdp_e_rd_rsp_valid.get := io.mcif2sdp_e_rd_rsp_valid.get
//         io.mcif2sdp_e_rd_rsp_ready.get := u_NV_NVDLA_sdp.io.mcif2sdp_e_rd_rsp_ready.get
//         u_NV_NVDLA_sdp.io.mcif2sdp_e_rd_rsp_pd.get := io.mcif2sdp_e_rd_rsp_pd.get           
//     }
//     if(conf.NVDLA_SDP_BN_ENABLE){
//         io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_sdp.io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get
//         io.sdp_n2mcif_rd_req_valid.get := u_NV_NVDLA_sdp.io.sdp_n2mcif_rd_req_valid.get
//         u_NV_NVDLA_sdp.io.sdp_n2mcif_rd_req_ready.get := io.sdp_n2mcif_rd_req_ready.get
//         io.sdp_n2mcif_rd_req_pd.get := u_NV_NVDLA_sdp.io.sdp_n2mcif_rd_req_pd.get
//         u_NV_NVDLA_sdp.io.mcif2sdp_n_rd_rsp_valid.get := io.mcif2sdp_n_rd_rsp_valid.get
//         io.mcif2sdp_n_rd_rsp_ready.get := u_NV_NVDLA_sdp.io.mcif2sdp_n_rd_rsp_ready.get
//         u_NV_NVDLA_sdp.io.mcif2sdp_n_rd_rsp_pd.get := io.mcif2sdp_n_rd_rsp_pd.get           
//     }
//     io.sdp2mcif_rd_cdt_lat_fifo_pop := u_NV_NVDLA_sdp.io.sdp2mcif_rd_cdt_lat_fifo_pop
//     io.sdp2mcif_rd_req_valid := u_NV_NVDLA_sdp.io.sdp2mcif_rd_req_valid
//     u_NV_NVDLA_sdp.io.sdp2mcif_rd_req_ready := io.sdp2mcif_rd_req_ready
//     io.sdp2mcif_rd_req_pd := u_NV_NVDLA_sdp.io.sdp2mcif_rd_req_pd
//     u_NV_NVDLA_sdp.io.mcif2sdp_rd_rsp_valid := io.mcif2sdp_rd_rsp_valid
//     io.mcif2sdp_rd_rsp_ready := u_NV_NVDLA_sdp.io.mcif2sdp_rd_rsp_ready
//     u_NV_NVDLA_sdp.io.mcif2sdp_rd_rsp_pd := io.mcif2sdp_rd_rsp_pd

//     io.sdp2mcif_wr_req_valid := u_NV_NVDLA_sdp.io.sdp2mcif_wr_req_valid
//     u_NV_NVDLA_sdp.io.sdp2mcif_wr_req_ready := io.sdp2mcif_wr_req_ready
//     io.sdp2mcif_wr_req_pd := u_NV_NVDLA_sdp.io.sdp2mcif_wr_req_pd
//     u_NV_NVDLA_sdp.io.mcif2sdp_wr_rsp_complete := io.mcif2sdp_wr_rsp_complete

//     io.sdp2csb_resp_valid := u_NV_NVDLA_sdp.io.sdp2csb_resp_valid
//     io.sdp2csb_resp_pd := u_NV_NVDLA_sdp.io.sdp2csb_resp_pd
//     io.sdp2glb_done_intr_pd := u_NV_NVDLA_sdp.io.sdp2glb_done_intr_pd

//     io.sdp2pdp_valid := u_NV_NVDLA_sdp.io.sdp2pdp_valid
//     u_NV_NVDLA_sdp.io.sdp2pdp_ready := io.sdp2pdp_ready
//     io.sdp2pdp_pd := u_NV_NVDLA_sdp.io.sdp2pdp_pd

//     io.sdp_rdma2csb_resp_valid  := u_NV_NVDLA_sdp.io.sdp_rdma2csb_resp_valid 
//     io.sdp_rdma2csb_resp_pd := u_NV_NVDLA_sdp.io.sdp_rdma2csb_resp_pd

//     u_NV_NVDLA_sdp.io.dla_clk_ovr_on_sync := dla_clk_ovr_on_sync
//     u_NV_NVDLA_sdp.io.global_clk_ovr_on_sync := global_clk_ovr_on_sync 
//     u_NV_NVDLA_sdp.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
// }


// object NV_NVDLA_partition_pDriver extends App {
//   implicit val conf: nvdlaConfiguration = new nvdlaConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_partition_p())
// }
