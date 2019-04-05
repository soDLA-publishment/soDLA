// package nvdla

// import chisel3._
// import chisel3.experimental._

// class NV_NVDLA_SDP_rdma extends Module {

// val io = IO(new Bundle {
//     //in clock
//     val nvdla_core_clk = Input(Clock())
//     val pwrbus_ram_pd = Input(UInt(32.W))

//     //cvif
//     val sdp_b2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//     val sdp_b2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//     val sdp_b2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
//     val cvif2sdp_b_rd_rsp_valid  = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//     val cvif2sdp_b_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None
//     val sdp_b2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

//     //mcif
//     val sdp_b2mcif_rd_req_valid = Output(Bool())
//     val sdp_b2mcif_rd_req_ready = Input(Bool())
//     val sdp_b2mcif_rd_req_pd = Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
//     val mcif2sdp_b_rd_rsp_valid  = Input(Bool())
//     val mcif2sdp_b_rd_rsp_ready = Output(Bool())
//     val mcif2sdp_b_rd_rsp_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
//     val sdp_b2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

//     val sdp_brdma2dp_alu_valid = Output(Bool()) 
//     val sdp_brdma2dp_alu_ready = Input(Bool())  
//     val sdp_brdma2dp_alu_pd = Output(UInt(conf.AM_DW2.W))

//     val sdp_brdma2dp_mul_valid = Output(Bool()) 
//     val sdp_brdma2dp_mul_ready = Input(Bool())  
//     val sdp_brdma2dp_mul_pd = Output(UInt(conf.AM_DW2.W))

//     val reg2dp_brdma_data_mode = Input(Bool())  
//     val reg2dp_brdma_data_size = Input(Bool())  
//     val reg2dp_brdma_data_use = Input(UInt(2.W))  
//     val reg2dp_brdma_ram_type = Input(Bool())  
//     val reg2dp_bs_base_addr_high = Input(UInt(32.W))  
//     val reg2dp_bs_base_addr_low = Input(UInt(32-conf.AM_DW.W))  
//     val reg2dp_bs_line_stride = Input(UInt(32-conf.AM_DW.W))   
//     val reg2dp_bs_surface_stride = Input(UInt(32-conf.AM_DW.W))  
//     val reg2dp_batch_number = Input(UInt(5.W))  
//     val reg2dp_channel = Input(UInt(13.W))  
//     val reg2dp_height = Input(UInt(13.W))  
//     val reg2dp_op_en = Input(Bool())  
//     val reg2dp_out_precision = Input(UInt(2.W)))  
//     val reg2dp_perf_dma_en = Input(Bool())  
//     val eg2dp_proc_precision = Input(UInt(2.W)))  
//     val reg2dp_width = Input(UInt(13.W))  
//     val reg2dp_winograd = Input(Bool())  
//     val dp2reg_brdma_stall = Output(UInt(32.W))   
//     val dp2reg_done = Output(Bool())  


//     val dla_clk_ovr_on_sync = Input(Clock())
//     val global_clk_ovr_on_sync = Input(Clock())
//     val tmc2slcg_disable_clock_gating = Input(Clock())
//     val brdma_slcg_op_en = Input(Bool())
//     val brdma_disable = Input(Bool())

// })

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

//     io.dp2reg_done := eg_done

//     //=======================================
//     val u_gate = Module(new NV_NVDLA_SDP_BRDMA_gate)




// }

