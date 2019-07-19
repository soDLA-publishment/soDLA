// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_sdp(implicit val conf: sdpConfiguration) extends Module {
//    val io = IO(new Bundle {

//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         val cacc2sdp_valid = Input(Bool())
//         val cacc2sdp_ready = Output(Bool())
//         val cacc2sdp_pd = Input(UInt((conf.DP_IN_DW+2).W))

//         val sdp2pdp_valid = Output(Bool())
//         val sdp2pdp_ready = Input(Bool())
//         val sdp2pdp_pd = Output(UInt(conf.DP_OUT_DW.W))

//         val sdp2glb_done_intr_pd = Output(UInt(2.W))

//         val sdp_brdma2dp_mul_valid = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val sdp_brdma2dp_mul_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val sdp_brdma2dp_mul_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt((conf.AM_DW2+1).W))) else None

//         val sdp_brdma2dp_alu_valid = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val sdp_brdma2dp_alu_ready = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
//         val sdp_brdma2dp_alu_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt((conf.AM_DW2+1).W))) else None

//         val sdp_nrdma2dp_mul_valid = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//         val sdp_nrdma2dp_mul_ready = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//         val sdp_nrdma2dp_mul_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt((conf.AM_DW2+1).W))) else None

//         val sdp_nrdma2dp_alu_valid = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//         val sdp_nrdma2dp_alu_ready = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
//         val sdp_nrdma2dp_alu_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt((conf.AM_DW2+1).W))) else None

//         val sdp_erdma2dp_mul_valid = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//         val sdp_erdma2dp_mul_ready = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//         val sdp_erdma2dp_mul_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt((conf.AM_DW2+1).W))) else None

//         val sdp_erdma2dp_alu_valid = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//         val sdp_erdma2dp_alu_ready = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
//         val sdp_erdma2dp_alu_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt((conf.AM_DW2+1).W))) else None

//         val sdp_dp2wdma_valid = Output(Bool())
//         val sdp_dp2wdma_ready = Input(Bool())
//         val sdp_dp2wdma_pd = Output(UInt(conf.DP_DOUT_DW.W))

//         val sdp2pdp_valid = Output(Bool())
//         val sdp2pdp_ready = Input(Bool())
//         val sdp2pdp_pd = Output(UInt(conf.DP_OUT_DW.W))

//         val cacc2sdp_valid = Input(Bool())
//         val cacc2sdp_ready = Output(Bool())
//         val cacc2sdp_pd = Input(UInt((conf.DP_IN_DW+2).W))

//         val sdp_mrdma2cmux_valid = Input(Bool())
//         val sdp_mrdma2cmux_ready = Output(Bool())
//         val sdp_mrdma2cmux_pd = Input(UInt((conf.DP_DIN_DW+2).W))

//         val reg2dp_bcore_slcg_op_en = Input(Bool())
//         val reg2dp_flying_mode = Input(Bool())

//         val cacc2sdp_valid = Input(Bool())
//         val cacc2sdp_ready = Output(Bool())
//         val cacc2sdp_pd = Input(UInt((conf.DP_DIN_DW+2).W))
//         val sdp2pdp_valid = Output(Bool())
//         val sdp2pdp_ready = Input(Bool())
//         val sdp2pdp_pd = Output

//         //ifdef NVDLA_SDP_BN_ENABLE
//         val reg2dp_bn_alu_algo = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt(2.W))) else None
//         val reg2dp_bn_alu_bypass = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bn_alu_operand = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt(16.W))) else None
//         val reg2dp_bn_alu_shift_value = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt(6.W))) else None
//         val reg2dp_bn_alu_src = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bn_bypass = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bn_mul_bypass = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bn_mul_operand = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt(16.W))) else None
//         val reg2dp_bn_mul_prelu = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bn_mul_shift_value = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt(8.W))) else None
//         val reg2dp_bn_mul_src = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bn_relu_bypass = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None

//         //ifdef NVDLA_SDP_BS_ENABLE
//         val reg2dp_bs_alu_algo = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(2.W))) else None
//         val reg2dp_bs_alu_bypass = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bs_alu_operand = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(16.W))) else None
//         val reg2dp_bs_alu_shift_value = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(6.W))) else None
//         val reg2dp_bs_alu_src = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bs_bypass = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bs_mul_bypass = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bs_mul_operand = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(16.W))) else None
//         val reg2dp_bs_mul_prelu = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bs_mul_shift_value = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(8.W))) else None
//         val reg2dp_bs_mul_src = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_bs_relu_bypass = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None

//         val reg2dp_cvt_offset = Input(UInt(32.W))
//         val reg2dp_cvt_scale = Input(UInt(16.W))
//         val reg2dp_cvt_shift = Input(UInt(6.W))
//         val reg2dp_ecore_slcg_op_en = Input(Bool())

//         //ifdef NVDLA_SDP_EW_ENABLE
//         val reg2dp_ew_alu_algo = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(2.W))) else None
//         val reg2dp_ew_alu_bypass = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_ew_alu_cvt_bypass = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None        
//         val reg2dp_ew_alu_cvt_offset = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(32.W))) else None
//         val reg2dp_ew_alu_cvt_scale = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(16.W))) else None        
//         val reg2dp_ew_alu_cvt_truncate = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(6.W))) else None
//         val reg2dp_ew_alu_operand = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(32.W))) else None        
//         val reg2dp_ew_alu_src = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_ew_bypass = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_ew_lut_bypass = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None        
//         val reg2dp_ew_mul_bypass = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_ew_mul_cvt_bypass = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_ew_mul_cvt_offset = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(32.W))) else None
//         val reg2dp_ew_mul_cvt_scale = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(16.W))) else None
//         val reg2dp_ew_mul_cvt_truncate = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(6.W))) else None
//         val reg2dp_ew_mul_operand = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(32.W))) else None
//         val reg2dp_ew_mul_prelu = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_ew_mul_src = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_ew_truncate = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(10.W))) else None

//         val reg2dp_lut_slcg_en = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_lut_hybrid_priority = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(Bool())) else None
//         val reg2dp_lut_int_access_type = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(Bool())) else None   
//         val reg2dp_lut_int_addr = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(10.W))) else None        
//         val reg2dp_lut_int_data = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(16.W))) else None        
//         val reg2dp_lut_int_data_wr = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(Bool())) else None   
//         val reg2dp_lut_int_table_id = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(Bool())) else None   
//         val reg2dp_lut_le_end = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(32.W))) else None        
//         val reg2dp_lut_le_function = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(Bool())) else None   
//         val reg2dp_lut_le_index_offset = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(8.W))) else None        
//         val reg2dp_lut_le_index_select = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(8.W))) else None        
//         val reg2dp_lut_le_slope_oflow_scale = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(16.W))) else None        
//         val reg2dp_lut_le_slope_oflow_shift = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(5.W))) else None        
//         val reg2dp_lut_le_slope_uflow_scale = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(16.W))) else None        
//         val reg2dp_lut_le_slope_uflow_shift = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(5.W))) else None        
//         val reg2dp_lut_le_start = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(32.W))) else None        
//         val reg2dp_lut_lo_end = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(32.W))) else None        
//         val reg2dp_lut_lo_index_select = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(8.W))) else None  
//         val reg2dp_lut_lo_slope_oflow_scale = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(16.W))) else None        
//         val reg2dp_lut_lo_slope_oflow_shift = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(5.W))) else None        
//         val reg2dp_lut_lo_slope_uflow_scale = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(16.W))) else None        
//         val reg2dp_lut_lo_slope_uflow_shift = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(5.W))) else None        
//         val reg2dp_lut_lo_start = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(UInt(32.W))) else None     
//         val reg2dp_lut_oflow_priority = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(Bool())) else None   
//         val reg2dp_lut_uflow_priority = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Input(Bool())) else None  
//         val dp2reg_lut_hybrid = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Output(UInt(32.W))) else None     
//         val dp2reg_lut_int_data = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Output(UInt(16.W))) else None     
//         val dp2reg_lut_le_hit = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Output(UInt(32.W))) else None     
//         val dp2reg_lut_lo_hit = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Output(UInt(32.W))) else None     
//         val dp2reg_lut_oflow = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Output(UInt(32.W))) else None     
//         val dp2reg_lut_uflow = if(conf.NVDLA_SDP_EW_ENABLE&conf.NVDLA_SDP_LUT_ENABLE) Some(Output(UInt(32.W))) else None      

//         val reg2dp_nan_to_zero = Input(Bool())
//         val reg2dp_ncore_slcg_op_en = Input(Bool())
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_out_precision = Input(UInt(2.W))
//         val reg2dp_output_dst = Input(Bool())
//         val reg2dp_perf_lut_en = Input(Bool())
//         val reg2dp_perf_sat_en = Input(Bool())
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val dp2reg_done = Input(Bool())
//         val dp2reg_out_saturation = Output(UInt(32.W))

//         val dla_clk_ovr_on_sync = Input(Clock())
//         val global_clk_ovr_on_sync = Input(Clock())
//         val tmc2slcg_disable_clock_gating = Input(Bool())

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
// withClock(io.nvdla_core_clk){   

// }}


// object NV_NVDLA_SDP_coreDriver extends App {
//   implicit val conf: sdpConfiguration = new sdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_core)
// }





