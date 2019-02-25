// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_cfgrom_rom extends Module {
//     val io = IO(new Bundle {
//         general clock
//         val nvdla_core_clk = Input(Clock())     

//         val reg_rd_data = Output(UInt(32.W))
//         val reg_offset = Input(UInt(12.W))
//         val reg_wr_data = Input(UInt(32.W))
//         val reg_wr_en = Input(Bool())
//     }) 
//     withClock(io.nvdla_core_clk){

//     io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
//     Seq( 
//      (NVDLA_CFGROM_CFGROM_HW_VERSION_0 & "h00000fff".asUInt(32.W)) -> "h10001".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_GLB_DESC_0 & "h00000fff".asUInt(32.W)) -> "h1".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CIF_DESC_0 & "h00000fff".asUInt(32.W)) -> "h180002".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CIF_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CIF_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CIF_BASE_WIDTH_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CIF_BASE_LATENCY_0 & "h00000fff".asUInt(32.W)) -> "h32 ".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CIF_BASE_BURST_LENGTH_MAX_0 & "h00000fff".asUInt(32.W)) -> "h80".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CIF_BASE_MEM_ADDR_WIDTH_0 & "h00000fff".asUInt(32.W)) -> "h400".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_DESC_0 & "h00000fff".asUInt(32.W)) -> "h340003".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_BASE_FEATURE_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_BASE_WEIGHT_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_BASE_ATOMIC_C_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_BASE_ATOMIC_K_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_BASE_ATOMIC_M_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_BASE_CBUF_BANK_NUM_0 & "h00000fff".asUInt(32.W)) -> "h20".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_BASE_CBUF_BANK_WIDTH_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_BASE_CBUF_BANK_DEPTH_0 & "h00000fff".asUInt(32.W)) -> "h200".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_MULTI_BATCH_MAX_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_IMAGE_IN_FORMATS_PACKED_0 & "h00000fff".asUInt(32.W)) -> "hcfff001".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CDMA_IMAGE_IN_FORMATS_SEMI_0 & "h00000fff".asUInt(32.W)) -> "h3".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CBUF_DESC_0 & "h00000fff".asUInt(32.W)) -> "h180004".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CBUF_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CBUF_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CBUF_BASE_CBUF_BANK_NUM_0 & "h00000fff".asUInt(32.W)) -> "h20".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CBUF_BASE_CBUF_BANK_WIDTH_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W), 
//      (NVDLA_CFGROM_CFGROM_CBUF_BASE_CBUF_BANK_DEPTH_0 & "h00000fff".asUInt(32.W)) -> "h200".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CBUF_BASE_CDMA_ID_0 & "h00000fff".asUInt(32.W)) -> "h3".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_DESC_0 & "h00000fff".asUInt(32.W)) -> "h300005".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_BASE_FEATURE_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_BASE_WEIGHT_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_BASE_ATOMIC_C_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_BASE_ATOMIC_K_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_BASE_ATOMIC_M_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_BASE_CBUF_BANK_NUM_0 & "h00000fff".asUInt(32.W)) -> "h20".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_BASE_CBUF_BANK_WIDTH_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_BASE_CBUF_BANK_DEPTH_0 & "h00000fff".asUInt(32.W)) -> "h200".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_BASE_CDMA_ID_0 & "h00000fff".asUInt(32.W)) -> "h3".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CSC_MULTI_BATCH_MAX_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_A_DESC_0 & "h00000fff".asUInt(32.W)) -> "h1c0006".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_A_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_A_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_A_BASE_FEATURE_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_A_BASE_WEIGHT_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_A_BASE_ATOMIC_C_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_A_BASE_ATOMIC_K_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_A_BASE_CDMA_ID_0 & "h00000fff".asUInt(32.W)) -> "h3".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_B_DESC_0 & "h00000fff".asUInt(32.W)) -> "h1c0006".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_B_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_B_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_B_BASE_FEATURE_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_B_BASE_WEIGHT_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_B_BASE_ATOMIC_C_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_B_BASE_ATOMIC_K_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CMAC_B_BASE_CDMA_ID_0 & "h00000fff".asUInt(32.W)) -> "h3".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CACC_DESC_0 & "h00000fff".asUInt(32.W)) -> "h200007".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CACC_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CACC_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CACC_BASE_FEATURE_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CACC_BASE_WEIGHT_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CACC_BASE_ATOMIC_C_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CACC_BASE_ATOMIC_K_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CACC_BASE_CDMA_ID_0 & "h00000fff".asUInt(32.W)) -> "h3".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CACC_MULTI_BATCH_MAX_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_RDMA_DESC_0 & "h00000fff".asUInt(32.W)) -> "he0008".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_RDMA_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_RDMA_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_RDMA_BASE_ATOMIC_M_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_RDMA_BASE_SDP_ID_0 & "h00000fff".asUInt(32.W)) -> "h9".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_DESC_0 & "h00000fff".asUInt(32.W)) -> "h200009".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h18".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_BASE_FEATURE_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_BASE_WEIGHT_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_BASE_CDMA_ID_0 & "h00000fff".asUInt(32.W)) -> "h3".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_MULTI_BATCH_MAX_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_BS_THROUGHPUT_0 & "h00000fff".asUInt(32.W)) -> "h1".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_BN_THROUGHPUT_0 & "h00000fff".asUInt(32.W)) -> "h1".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_SDP_EW_THROUGHPUT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_RDMA_DESC_0 & "h00000fff".asUInt(32.W)) -> "he000a".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_RDMA_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_RDMA_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_RDMA_BASE_ATOMIC_M_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_RDMA_BASE_PDP_ID_0 & "h00000fff".asUInt(32.W)) -> "hb".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_DESC_0 & "h00000fff".asUInt(32.W)) -> "h10000b".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_BASE_FEATURE_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_PDP_BASE_THROUGHPUT_0 & "h00000fff".asUInt(32.W)) -> "h1".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_RDMA_DESC_0 & "h00000fff".asUInt(32.W)) -> "he000c".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_RDMA_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_RDMA_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_RDMA_BASE_ATOMIC_M_0 & "h00000fff".asUInt(32.W)) -> "h8".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_RDMA_BASE_CDP_ID_0 & "h00000fff".asUInt(32.W)) -> "hd".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_DESC_0 & "h00000fff".asUInt(32.W)) -> "h10000d".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_CAP_INCOMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_CAP_COMPAT_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_BASE_FEATURE_TYPES_0 & "h00000fff".asUInt(32.W)) -> "h10".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_CDP_BASE_THROUGHPUT_0 & "h00000fff".asUInt(32.W)) -> "h1".asUInt(32.W),
//      (NVDLA_CFGROM_CFGROM_END_OF_LIST_0 & "h00000fff".asUInt(32.W)) -> "h0".asUInt(32.W)     
//     ))




    
//   }}