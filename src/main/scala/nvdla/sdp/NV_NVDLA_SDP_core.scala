// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_core(implicit val conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {

//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         val sdp_brdma2dp_mul_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt((conf.AM_DW2+1).W)))) else None
//         val sdp_brdma2dp_alu_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt((conf.AM_DW2+1).W)))) else None

//         val sdp_nrdma2dp_mul_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt((conf.AM_DW2+1).W)))) else None
//         val sdp_nrdma2dp_alu_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt((conf.AM_DW2+1).W)))) else None

//         val sdp_erdma2dp_mul_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt((conf.AM_DW2+1).W)))) else None
//         val sdp_erdma2dp_alu_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt((conf.AM_DW2+1).W)))) else None

//         val sdp_dp2wdma_pd = DecoupledIO(UInt(conf.DP_DOUT_DW.W))
//         val sdp2pdp_pd = DecoupledIO(UInt(conf.DP_OUT_DW.W))

//         val cacc2sdp_pd = Flipped(DecoupledIO(UInt((conf.DP_IN_DW+2).W)))
//         val sdp_mrdma2cmux_pd =  Flipped(DecoupledIO(UInt((conf.DP_IN_DW+2).W)))

//         val reg2dp_bcore_slcg_op_en = Input(Bool())
//         val reg2dp_flying_mode = Input(Bool())

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
//     //===========================================
//     // CFG
//     //===========================================
//     val cfg_bs_en = RegInit(false.B)
//     val cfg_bn_en = RegInit(false.B)
//     val cfg_ew_en = RegInit(false.B)
//     val cfg_mode_eql = RegInit(false.B)

//     if(conf.NVDLA_SDP_BS_ENABLE){
//         cfg_bs_en := io.reg2dp_bs_bypass.get === 0.U
//     }
//     else{
//         cfg_bs_en := false.B
//     }

//     if(conf.NVDLA_SDP_BN_ENABLE){
//         cfg_bn_en := io.reg2dp_bn_bypass.get === 0.U
//     }
//     else{
//         cfg_bn_en := false.B
//     }

//     if(conf.NVDLA_SDP_EW_ENABLE){
//         cfg_ew_en := io.reg2dp_ew_bypass.get === 0.U
//         cfg_mode_eql := (io.reg2dp_ew_alu_algo.get === 3.U) && 
//                         (io.reg2dp_ew_alu_bypass.get === 0.U) && 
//                         (io.reg2dp_ew_bypass.get === 0.U)
//     }
//     else{
//         cfg_bs_en := false.B
//         cfg_mode_eql := false.B
//     }    

//     val cfg_bs_alu_operand = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(0.U(16.W))) else None
//     val cfg_bs_mul_operand = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(0.U(16.W))) else None
//     val cfg_bs_alu_bypass = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bs_alu_algo = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(0.U(2.W))) else None
//     val cfg_bs_alu_src = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bs_alu_shift_value = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(0.U(6.W))) else None
//     val cfg_bs_mul_bypass = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bs_mul_prelu = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bs_mul_src = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bs_mul_shift_value = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(0.U(8.W))) else None
//     val cfg_bs_relu_bypass = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(false.B)) else None    

//     val cfg_bn_alu_operand = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(0.U(16.W))) else None
//     val cfg_bn_mul_operand = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(0.U(16.W))) else None
//     val cfg_bn_alu_bypass = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bn_alu_algo = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(0.U(2.W))) else None
//     val cfg_bn_alu_src = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bn_alu_shift_value = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(0.U(6.W))) else None
//     val cfg_bn_mul_bypass = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bn_mul_prelu = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bn_mul_src = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_bn_mul_shift_value = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(0.U(8.W))) else None
//     val cfg_bn_relu_bypass = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(false.B)) else None    

//     val cfg_cvt_offset = RegInit(0.U(32.W))
//     val cfg_cvt_scale = RegInit(0.U(16.W))
//     val cfg_cvt_shift = RegInit(0.U(6.W))
//     val cfg_proc_precision = RegInit(0.U(2.W))
//     val cfg_out_precision = RegInit(0.U(2.W))
//     val cfg_nan_to_zero = RegInit(false.B)

//     val op_en_load = Wire(Bool())
//     when(op_en_load){
//         if(conf.NVDLA_SDP_BS_ENABLE){
//         cfg_bs_alu_operand.get      := io.reg2dp_bs_alu_operand.get      
//         cfg_bs_mul_operand.get      := io.reg2dp_bs_mul_operand.get      
//         cfg_bs_alu_bypass.get       := io.reg2dp_bs_alu_bypass.get       
//         cfg_bs_alu_algo.get         := io.reg2dp_bs_alu_algo.get         
//         cfg_bs_alu_src.get          := io.reg2dp_bs_alu_src.get          
//         cfg_bs_alu_shift_value.get  := io.reg2dp_bs_alu_shift_value.get  
//         cfg_bs_mul_bypass.get       := io.reg2dp_bs_mul_bypass.get       
//         cfg_bs_mul_prelu.get        := io.reg2dp_bs_mul_prelu.get        
//         cfg_bs_mul_src.get          := io.reg2dp_bs_mul_src.get          
//         cfg_bs_mul_shift_value.get  := io.reg2dp_bs_mul_shift_value.get  
//         cfg_bs_relu_bypass.get      := io.reg2dp_bs_relu_bypass.get                  
//         }
//         if(conf.NVDLA_SDP_BN_ENABLE){
//         cfg_bn_alu_operand.get      := io.reg2dp_bn_alu_operand.get      
//         cfg_bn_mul_operand.get      := io.reg2dp_bn_mul_operand.get      
//         cfg_bn_alu_bypass.get       := io.reg2dp_bn_alu_bypass.get       
//         cfg_bn_alu_algo.get         := io.reg2dp_bn_alu_algo.get         
//         cfg_bn_alu_src.get          := io.reg2dp_bn_alu_src.get          
//         cfg_bn_alu_shift_value.get  := io.reg2dp_bn_alu_shift_value.get  
//         cfg_bn_mul_bypass.get       := io.reg2dp_bn_mul_bypass.get       
//         cfg_bn_mul_prelu.get        := io.reg2dp_bn_mul_prelu.get        
//         cfg_bn_mul_src.get          := io.reg2dp_bn_mul_src.get          
//         cfg_bn_mul_shift_value.get  := io.reg2dp_bn_mul_shift_value.get  
//         cfg_bn_relu_bypass.get      := io.reg2dp_bn_relu_bypass.get      
//         }
//         cfg_cvt_offset := io.reg2dp_cvt_offset
//         cfg_cvt_scale := io.reg2dp_cvt_scale
//         cfg_cvt_shift := io.reg2dp_cvt_shift
//         cfg_proc_precision := io.reg2dp_proc_precision
//         cfg_out_precision := io.reg2dp_out_precision
//         cfg_nan_to_zero := io.reg2dp_nan_to_zero        
//     }

//     //===========================================
//     // SLCG Gate
//     //===========================================
//     val bcore_slcg_en = cfg_bs_en & io.reg2dp_bcore_slcg_op_en
//     val ncore_slcg_en = cfg_bn_en & io.reg2dp_ncore_slcg_op_en
//     val ecore_slcg_en = Wire(Bool())
//     if(conf.NVDLA_SDP_EW_ENABLE){
//         if(conf.NVDLA_SDP_LUT_ENABLE){
//             ecore_slcg_en := (cfg_ew_en & io.reg2dp_ecore_slcg_op_en) | io.reg2dp_lut_slcg_en.get
//         }
//         else{
//             ecore_slcg_en := cfg_ew_en & io.reg2dp_ecore_slcg_op_en
//         }
//     }
//     else{
//         ecore_slcg_en := cfg_ew_en & io.reg2dp_ecore_slcg_op_en
//     }

//     val u_gate = Array(3){Module(new NV_NVDLA_slcg(1, false))}
//     for(i <- 0 to 2){
//         u_gate(i).io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync
//         u_gate(i).io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//         u_gate(i).io.nvdla_core_clk := io.nvdla_core_clk
//         u_gate(i).io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//     }
//     u_gate(0).io.slcg_en(0) := bcore_slcg_en
//     u_gate(1).io.slcg_en(0) := ecore_slcg_en
//     u_gate(2).io.slcg_en(0) := ncore_slcg_en
//     val nvdla_gated_bcore_clk = u_gate(0).io.nvdla_core_gated_clk
//     val nvdla_gated_ecore_clk = u_gate(1).io.nvdla_core_gated_clk
//     val nvdla_gated_ncore_clk = u_gate(2).io.nvdla_core_gated_clk

//     //===========================================================================
//     //  DATA PATH LOGIC 
//     //  RDMA data 
//     //===========================================================================
//     //covert mrdma data from atomic_m to NVDLA_SDP_MAX_THROUGHPUT
//     val sdp_mrdma_data_in_ready = Wire(Bool())
//     val u_dpin_pack = Module(new NV_NVDLA_SDP_RDMA_pack(conf.DP_DIN_DW, conf.DP_IN_DW, 2))
//     u_dpin_pack.io.nvdla_core_clk := io.nvdla_core_clk
//     u_dpin_pack.io.cfg_dp_8 := !(io.reg2dp_proc_precision.asUInt.orR)
//     u_dpin_pack.io.inp_pvld := io.sdp_mrdma2cmux_valid
//     io.sdp_mrdma2cmux_ready := u_dpin_pack.io.inp_prdy
//     u_dpin_pack.io.inp_data := io.sdp_mrdma2cmux_pd
//     val sdp_mrdma_data_in_valid = u_dpin_pack.io.out_pvld
//     u_dpin_pack.io.out_prdy := sdp_mrdma_data_in_ready
//     val sdp_mrdma_data_in_pd = u_dpin_pack.io.out_data

//     // #ifdef NVDLA_SDP_BS_ENABLE
//     //covert atomic_m to NVDLA_SDP_BS_THROUGHPUT
//     val bs_mul_in_pvld = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val bs_mul_in_prdy = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val bs_mul_in_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(UInt((conf.BS_OP_DW+1).W))) else None

//     val bs_mul_in_data = if(conf.NVDLA_SDP_BS_ENABLE) Some(bs_mul_in_pd.get(conf.BS_OP_DW-1,0)) else None
//     val bs_mul_in_layer_end = if(conf.NVDLA_SDP_BS_ENABLE) Some(bs_mul_in_pd.get(conf.BS_OP_DW)) else None  

//     val bs_alu_in_pvld = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val bs_alu_in_prdy = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val bs_alu_in_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(UInt((conf.BS_OP_DW+1).W))) else None

//     val bs_alu_in_data = if(conf.NVDLA_SDP_BS_ENABLE) Some(bs_alu_in_pd.get(conf.BS_OP_DW-1,0)) else None
//     val bs_alu_in_layer_end = if(conf.NVDLA_SDP_BS_ENABLE) Some(bs_alu_in_pd.get(conf.BS_OP_DW)) else None

//     val u_bs_mul_pack = if(conf.NVDLA_SDP_BS_ENABLE) Some(Module(new NV_NVDLA_SDP_RDMA_pack(conf.AM_DW2, conf.BS_OP_DW, 1))) else None
//     val u_bs_alu_pack = if(conf.NVDLA_SDP_BS_ENABLE) Some(Module(new NV_NVDLA_SDP_RDMA_pack(conf.AM_DW2, conf.BS_OP_DW, 1))) else None
//     if(conf.NVDLA_SDP_BS_ENABLE){
//         u_bs_mul_pack.get.io.nvdla_core_clk := io.nvdla_core_clk
//         u_bs_mul_pack.get.io.cfg_dp_8 := !(io.reg2dp_proc_precision.asUInt.orR)
//         u_bs_mul_pack.get.io.inp_pvld := io.sdp_brdma2dp_mul_valid.get
//         io.sdp_brdma2dp_mul_ready.get := u_bs_mul_pack.get.io.inp_prdy
//         u_bs_mul_pack.get.io.inp_data := io.sdp_brdma2dp_mul_pd.get
//         bs_mul_in_pvld.get := u_bs_mul_pack.get.io.out_pvld
//         u_bs_mul_pack.get.io.out_prdy := bs_mul_in_prdy.get
//         bs_mul_in_pd.get := u_bs_mul_pack.get.io.out_data

//         u_bs_alu_pack.get.io.nvdla_core_clk := io.nvdla_core_clk
//         u_bs_alu_pack.get.io.cfg_dp_8 := !(io.reg2dp_proc_precision.asUInt.orR)
//         u_bs_alu_pack.get.io.inp_pvld := io.sdp_brdma2dp_alu_valid.get
//         io.sdp_brdma2dp_alu_ready.get := u_bs_mul_pack.get.io.inp_prdy
//         u_bs_alu_pack.get.io.inp_data := io.sdp_brdma2dp_alu_pd.get
//         bs_alu_in_pvld.get := u_bs_alu_pack.get.io.out_pvld
//         u_bs_alu_pack.get.io.out_prdy := bs_alu_in_prdy.get
//         bs_alu_in_pd.get := u_bs_alu_pack.get.io.out_data
//     }

//     // #ifdef NVDLA_SDP_BN_ENABLE
//     //covert atomic_m to NVDLA_SDP_BN_THROUGHPUT
//     val bn_mul_in_pvld = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val bn_mul_in_prdy = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val bn_mul_in_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(UInt((conf.BN_OP_DW+1).W))) else None

//     val bn_mul_in_data = if(conf.NVDLA_SDP_BN_ENABLE) Some(bn_mul_in_pd.get(conf.BN_OP_DW-1,0)) else None
//     val bn_mul_in_layer_end = if(conf.NVDLA_SDP_BN_ENABLE) Some(bn_mul_in_pd.get(conf.BN_OP_DW)) else None    

//     val bn_alu_in_pvld = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val bn_alu_in_prdy = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val bn_alu_in_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(UInt((conf.BN_OP_DW+1).W))) else None

//     val bn_alu_in_data = if(conf.NVDLA_SDP_BN_ENABLE) Some(bn_alu_in_pd.get(conf.BN_OP_DW-1,0)) else None
//     val bn_alu_in_layer_end = if(conf.NVDLA_SDP_BN_ENABLE) Some(bn_alu_in_pd.get(conf.BN_OP_DW)) else None

//     val u_bn_mul_pack = if(conf.NVDLA_SDP_BN_ENABLE) Some(Module(new NV_NVDLA_SDP_RDMA_pack(conf.AM_DW2, conf.BN_OP_DW, 1))) else None
//     val u_bn_alu_pack = if(conf.NVDLA_SDP_BN_ENABLE) Some(Module(new NV_NVDLA_SDP_RDMA_pack(conf.AM_DW2, conf.BN_OP_DW, 1))) else None
//     if(conf.NVDLA_SDP_BN_ENABLE){
//         u_bn_mul_pack.get.io.nvdla_core_clk := io.nvdla_core_clk
//         u_bn_mul_pack.get.io.cfg_dp_8 := !(io.reg2dp_proc_precision.asUInt.orR)
//         u_bn_mul_pack.get.io.inp_pvld := io.sdp_nrdma2dp_mul_valid.get
//         io.sdp_nrdma2dp_mul_ready.get := u_bn_mul_pack.get.io.inp_prdy
//         u_bn_mul_pack.get.io.inp_data := io.sdp_nrdma2dp_mul_pd.get
//         bn_mul_in_pvld.get := u_bn_mul_pack.get.io.out_pvld
//         u_bn_mul_pack.get.io.out_prdy := bn_mul_in_prdy.get
//         bn_mul_in_pd.get := u_bn_mul_pack.get.io.out_data

//         u_bn_alu_pack.get.io.nvdla_core_clk := io.nvdla_core_clk
//         u_bn_alu_pack.get.io.cfg_dp_8 := !(io.reg2dp_proc_precision.asUInt.orR)
//         u_bn_alu_pack.get.io.inp_pvld := io.sdp_nrdma2dp_alu_valid.get
//         io.sdp_nrdma2dp_alu_ready.get := u_bn_mul_pack.get.io.inp_prdy
//         u_bn_alu_pack.get.io.inp_data := io.sdp_nrdma2dp_alu_pd.get
//         bn_alu_in_pvld.get := u_bn_alu_pack.get.io.out_pvld
//         u_bn_alu_pack.get.io.out_prdy := bn_alu_in_prdy.get
//         bn_alu_in_pd.get := u_bn_alu_pack.get.io.out_data
//     }

//     // #ifdef NVDLA_SDP_EW_ENABLE
//     //=====EW=======
//     //covert atomic_m to NVDLA_SDP_EW_THROUGHPUT
//     val ew_mul_in_pvld = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val ew_mul_in_prdy = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val ew_mul_in_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt((conf.EW_OP_DW+1).W))) else None

//     val ew_mul_in_data = if(conf.NVDLA_SDP_EW_ENABLE) Some(ew_mul_in_pd.get(conf.EW_OP_DW-1,0)) else None
//     val ew_mul_in_layer_end = if(conf.NVDLA_SDP_EW_ENABLE) Some(ew_mul_in_pd.get(conf.EW_OP_DW)) else None    

//     val ew_alu_in_pvld = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val ew_alu_in_prdy = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val ew_alu_in_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt((conf.EW_OP_DW+1).W))) else None

//     val ew_alu_in_data = if(conf.NVDLA_SDP_EW_ENABLE) Some(ew_alu_in_pd.get(conf.EW_OP_DW-1,0)) else None
//     val ew_alu_in_layer_end = if(conf.NVDLA_SDP_EW_ENABLE) Some(ew_alu_in_pd.get(conf.EW_OP_DW)) else None

//     val u_ew_mul_pack = if(conf.NVDLA_SDP_EW_ENABLE) Some(Module(new NV_NVDLA_SDP_RDMA_pack(conf.AM_DW2, conf.EW_OP_DW, 1))) else None
//     val u_ew_alu_pack = if(conf.NVDLA_SDP_EW_ENABLE) Some(Module(new NV_NVDLA_SDP_RDMA_pack(conf.AM_DW2, conf.EW_OP_DW, 1))) else None
//     if(conf.NVDLA_SDP_EW_ENABLE){
//         u_ew_mul_pack.get.io.nvdla_core_clk := io.nvdla_core_clk
//         u_ew_mul_pack.get.io.cfg_dp_8 := !(io.reg2dp_proc_precision.asUInt.orR)
//         u_ew_mul_pack.get.io.inp_pvld := io.sdp_erdma2dp_mul_valid.get
//         io.sdp_erdma2dp_mul_ready.get := u_ew_mul_pack.get.io.inp_prdy
//         u_ew_mul_pack.get.io.inp_data := io.sdp_erdma2dp_mul_pd.get
//         ew_mul_in_pvld.get := u_ew_mul_pack.get.io.out_pvld
//         u_ew_mul_pack.get.io.out_prdy := ew_mul_in_prdy.get
//         ew_mul_in_pd.get := u_ew_mul_pack.get.io.out_data

//         u_ew_alu_pack.get.io.nvdla_core_clk := io.nvdla_core_clk
//         u_ew_alu_pack.get.io.cfg_dp_8 := !(io.reg2dp_proc_precision.asUInt.orR)
//         u_ew_alu_pack.get.io.inp_pvld := io.sdp_erdma2dp_alu_valid.get
//         io.sdp_erdma2dp_alu_ready.get := u_ew_mul_pack.get.io.inp_prdy
//         u_ew_alu_pack.get.io.inp_data := io.sdp_erdma2dp_alu_pd.get
//         ew_alu_in_pvld.get := u_ew_alu_pack.get.io.out_pvld
//         u_ew_alu_pack.get.io.out_prdy := ew_alu_in_prdy.get
//         ew_alu_in_pd.get := u_ew_alu_pack.get.io.out_data
//     }

//     val wait_for_op_en = RegInit(true.B)
//     when(io.dp2reg_done){
//         wait_for_op_en := true.B
//     }.elsewhen(io.reg2dp_op_en){
//         wait_for_op_en := false.B
//     }

//     op_en_load := wait_for_op_en & io.reg2dp_op_en

//     // #ifdef NVDLA_SDP_BS_ENABLE
//     val bs_alu_in_en = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(false.B)) else None
//     val bs_alu_in_rdy = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val bs_alu_in_vld = if(conf.NVDLA_SDP_BS_ENABLE) Some(bs_alu_in_en.get & bs_alu_in_pvld.get) else None
//     val bs_mul_in_en = if(conf.NVDLA_SDP_BS_ENABLE) Some(RegInit(false.B)) else None
//     val bs_mul_in_rdy = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val bs_mul_in_vld = if(conf.NVDLA_SDP_BS_ENABLE) Some(bs_mul_in_en.get & bs_mul_in_pvld.get) else None

//     if(conf.NVDLA_SDP_BS_ENABLE){
//         when(io.dp2reg_done){
//             bs_alu_in_en.get := false.B
//             bs_mul_in_en.get := false.B
//         }
//         .elsewhen(op_en_load){
//             bs_alu_in_en.get := cfg_bs_en && (!io.reg2dp_bs_alu_bypass.get) && (io.reg2dp_bs_alu_src.get===1.U)
//             bs_mul_in_en.get := cfg_bs_en && (!io.reg2dp_bs_mul_bypass.get) && (io.reg2dp_bs_mul_src.get===1.U)
//         }
//         .elsewhen(bs_alu_in_layer_end.get && bs_alu_in_pvld.get && bs_alu_in_prdy.get){
//             bs_alu_in_en.get := false.B
//         }
//         .elsewhen(bs_mul_in_layer_end.get && bs_mul_in_pvld.get && bs_mul_in_prdy.get){
//             bs_mul_in_en.get := false.B
//         }
//         bs_alu_in_prdy.get := bs_alu_in_en.get & bs_alu_in_rdy.get
//         bs_mul_in_prdy.get := bs_mul_in_en.get & bs_mul_in_rdy.get
//     }
    
//     // #ifdef NVDLA_SDP_BN_ENABLE
//     val bn_alu_in_en = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(false.B)) else None
//     val bn_alu_in_rdy = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val bn_alu_in_vld = if(conf.NVDLA_SDP_BN_ENABLE) Some(bn_alu_in_en.get & bn_alu_in_pvld.get) else None
//     val bn_mul_in_en = if(conf.NVDLA_SDP_BN_ENABLE) Some(RegInit(false.B)) else None
//     val bn_mul_in_rdy = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None
//     val bn_mul_in_vld = if(conf.NVDLA_SDP_BN_ENABLE) Some(bn_mul_in_en.get & bn_mul_in_pvld.get) else None

//     if(conf.NVDLA_SDP_BN_ENABLE){
//         when(io.dp2reg_done){
//             bn_alu_in_en.get := false.B
//             bn_mul_in_en.get := false.B
//         }
//         .elsewhen(op_en_load){
//             bn_alu_in_en.get := cfg_bn_en && (!io.reg2dp_bn_alu_bypass.get) && (io.reg2dp_bn_alu_src.get===1.U)
//             bn_mul_in_en.get := cfg_bn_en && (!io.reg2dp_bn_mul_bypass.get) && (io.reg2dp_bn_mul_src.get===1.U)
//         }
//         .elsewhen(bn_alu_in_layer_end.get && bn_alu_in_pvld.get && bn_alu_in_prdy.get){
//             bn_alu_in_en.get := false.B
//         }
//         .elsewhen(bn_mul_in_layer_end.get && bn_mul_in_pvld.get && bn_mul_in_prdy.get){
//             bn_mul_in_en.get := false.B
//         }

//         bn_alu_in_prdy.get := bn_alu_in_en.get & bn_alu_in_rdy.get
//         bn_mul_in_prdy.get := bn_mul_in_en.get & bn_mul_in_rdy.get
//     }

//     // #ifdef NVDLA_SDP_EW_ENABLE
//     val ew_alu_in_en = if(conf.NVDLA_SDP_EW_ENABLE) Some(RegInit(false.B)) else None
//     val ew_alu_in_rdy = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val ew_alu_in_vld = if(conf.NVDLA_SDP_EW_ENABLE) Some(ew_alu_in_en.get & ew_alu_in_pvld.get) else None
//     val ew_mul_in_en = if(conf.NVDLA_SDP_EW_ENABLE) Some(RegInit(false.B)) else None
//     val ew_mul_in_rdy = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val ew_mul_in_vld = if(conf.NVDLA_SDP_EW_ENABLE) Some(ew_mul_in_en.get & ew_mul_in_pvld.get) else None

//     if(conf.NVDLA_SDP_EW_ENABLE){
//         when(io.dp2reg_done){
//             ew_alu_in_en.get := false.B
//             ew_mul_in_en.get := false.B
//         }
//         .elsewhen(op_en_load){
//             ew_alu_in_en.get := cfg_ew_en && (!io.reg2dp_ew_alu_bypass.get) && (io.reg2dp_ew_alu_src.get===1.U)
//             ew_mul_in_en.get := cfg_ew_en && (!io.reg2dp_ew_mul_bypass.get) && (io.reg2dp_ew_mul_src.get===1.U)
//         }
//         .elsewhen(ew_alu_in_layer_end.get && ew_alu_in_pvld.get && ew_alu_in_prdy.get){
//             ew_alu_in_en.get := false.B
//         }
//         .elsewhen(ew_mul_in_layer_end.get && ew_mul_in_pvld.get && ew_mul_in_prdy.get){
//             ew_mul_in_en.get := false.B
//         }
//         ew_alu_in_prdy.get := ew_alu_in_en.get & ew_alu_in_rdy.get
//         ew_mul_in_prdy.get := ew_mul_in_en.get & ew_mul_in_rdy.get
//     }

//     //===========================================
//     // CORE
//     //===========================================
//     // data from MUX ? CC : MEM
//     val sdp_cmux2dp_ready = Wire(Bool())
//     val u_NV_NVDLA_SDP_cmux = Module(new NV_NVDLA_SDP_cmux)
//     u_NV_NVDLA_SDP_cmux.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_SDP_cmux.io.cacc2sdp_valid := io.cacc2sdp_valid
//     io.cacc2sdp_ready := u_NV_NVDLA_SDP_cmux.io.cacc2sdp_ready
//     u_NV_NVDLA_SDP_cmux.io.cacc2sdp_pd := io.cacc2sdp_pd
//     u_NV_NVDLA_SDP_cmux.io.sdp_mrdma2cmux_valid := sdp_mrdma_data_in_valid
//     sdp_mrdma_data_in_ready := u_NV_NVDLA_SDP_cmux.io.sdp_mrdma2cmux_ready
//     u_NV_NVDLA_SDP_cmux.io.sdp_mrdma2cmux_pd := sdp_mrdma_data_in_pd
//     val sdp_cmux2dp_valid = u_NV_NVDLA_SDP_cmux.io.sdp_cmux2dp_valid
//     u_NV_NVDLA_SDP_cmux.io.sdp_cmux2dp_ready := sdp_cmux2dp_ready
//     val sdp_cmux2dp_pd = u_NV_NVDLA_SDP_cmux.io.sdp_cmux2dp_pd
//     u_NV_NVDLA_SDP_cmux.io.reg2dp_flying_mode := io.reg2dp_flying_mode
//     u_NV_NVDLA_SDP_cmux.io.reg2dp_nan_to_zero := io.reg2dp_nan_to_zero
//     u_NV_NVDLA_SDP_cmux.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_NV_NVDLA_SDP_cmux.io.op_en_load := op_en_load

//     val sdp_cmux2dp_data = sdp_cmux2dp_pd

//     // MUX to bypass CORE_x0
//     val flop_bn_data_out_prdy = Wire(Bool())
//     val bn_data_in_prdy = Wire(Bool())

//     val bs_data_in_prdy = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val bs_data_in_pvld = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
//     val bs_data_in_pd = if(conf.NVDLA_SDP_BS_ENABLE) Wire(UInt(conf.BS_IN_DW.W)) else "b0".asUInt(conf.BS_IN_DW.W)
//     val flop_bs_data_out_pvld = if(conf.NVDLA_SDP_BS_ENABLE) Wire(Bool()) else false.B
//     val flop_bs_data_out_prdy = if(conf.NVDLA_SDP_BN_ENABLE) Mux(cfg_bn_en, bn_data_in_prdy, flop_bn_data_out_prdy) else flop_bn_data_out_prdy
//     val flop_bs_data_out_pd = if(conf.NVDLA_SDP_BS_ENABLE) Wire(UInt(conf.BS_DOUT_DW.W)) else "b0".asUInt(conf.BS_DOUT_DW.W)

//     val u_bs_dppack = if(conf.NVDLA_SDP_BS_ENABLE) Some(Module(new NV_NVDLA_SDP_CORE_pack(conf.DP_IN_DW, conf.BS_IN_DW))) else None
//     val u_bs = if(conf.NVDLA_SDP_BS_ENABLE) Some(Module(new NV_NVDLA_SDP_HLS_x1_int)) else None
//     val u_bs_dpunpack = if(conf.NVDLA_SDP_BS_ENABLE) Some(Module(new NV_NVDLA_SDP_CORE_unpack(conf.BS_OUT_DW, conf.BS_DOUT_DW))) else None

//     if(conf.NVDLA_SDP_BS_ENABLE){
//         sdp_cmux2dp_ready := Mux(cfg_bs_en, bs_data_in_prdy.get, flop_bs_data_out_prdy)
//     }
//     else{
//         sdp_cmux2dp_ready := flop_bs_data_out_prdy
//     }

//     if(conf.NVDLA_SDP_BS_ENABLE){
//     bs_data_in_pvld.get := cfg_bs_en & sdp_cmux2dp_valid
//     bs_data_in_pd := sdp_cmux2dp_data

//     //covert NVDLA_SDP_MAX_THROUGHPUT to NVDLA_SDP_BS_THROUGHPUT
//     u_bs_dppack.get.io.nvdla_core_clk := nvdla_gated_bcore_clk
//     u_bs_dppack.get.io.inp_pvld := bs_data_in_pvld.get
//     bs_data_in_prdy.get := u_bs_dppack.get.io.inp_prdy
//     u_bs_dppack.get.io.inp_data := bs_data_in_pd
//     u_bs.get.io.chn_in_pvld := u_bs_dppack.get.io.out_pvld
//     u_bs_dppack.get.io.out_prdy := u_bs.get.io.chn_in_prdy
//     u_bs.get.io.chn_data_in  := u_bs_dppack.get.io.out_data

//     u_bs.get.io.nvdla_core_clk := nvdla_gated_bcore_clk
//     u_bs.get.io.chn_alu_op_pvld := bs_alu_in_vld.get
//     bs_alu_in_rdy.get := u_bs.get.io.chn_alu_op_prdy
//     u_bs.get.io.chn_alu_op := bs_alu_in_data.get
//     u_bs.get.io.chn_mul_op_pvld := bs_mul_in_vld.get
//     bs_mul_in_rdy.get := u_bs.get.io.chn_mul_op_prdy
//     u_bs.get.io.chn_mul_op := bs_mul_in_data.get
//     u_bs_dpunpack.get.io.inp_pvld := u_bs.get.io.chn_out_pvld
//     u_bs.get.io.chn_out_prdy := u_bs_dpunpack.get.io.inp_prdy
//     u_bs_dpunpack.get.io.inp_data := u_bs.get.io.chn_data_out
//     u_bs.get.io.cfg_alu_algo := cfg_bs_alu_algo.get
//     u_bs.get.io.cfg_alu_bypass := cfg_bs_alu_bypass.get
//     u_bs.get.io.cfg_alu_op := cfg_bs_alu_operand.get
//     u_bs.get.io.cfg_alu_shift_value := cfg_bs_alu_shift_value.get
//     u_bs.get.io.cfg_alu_src := cfg_bs_alu_src.get
//     u_bs.get.io.cfg_mul_bypass := cfg_bs_mul_bypass.get
//     u_bs.get.io.cfg_mul_op := cfg_bs_mul_operand.get
//     u_bs.get.io.cfg_mul_prelu := cfg_bs_mul_prelu.get
//     u_bs.get.io.cfg_mul_shift_value := cfg_bs_mul_shift_value.get
//     u_bs.get.io.cfg_mul_src := cfg_bs_mul_src.get
//     u_bs.get.io.cfg_relu_bypass := cfg_bs_relu_bypass.get

//     //covert NVDLA_SDP_BS_THROUGHPUT to NVDLA_SDP_MAX_THROUGHPUT
//     u_bs_dpunpack.get.io.nvdla_core_clk := nvdla_gated_bcore_clk
//     flop_bs_data_out_pvld := u_bs_dpunpack.get.io.out_pvld
//     u_bs_dpunpack.get.io.out_prdy := flop_bs_data_out_prdy
//     flop_bs_data_out_pd := u_bs_dpunpack.get.io.out_data
//     }

//     //===========================================
//     // MUX between BS and BN
//     //===========================================
//     val bs2bn_data_pvld = Mux(cfg_bs_en, flop_bs_data_out_pvld, sdp_cmux2dp_valid)
//     val bn_data_in_pd = Mux(cfg_bs_en, flop_bs_data_out_pd, bs_data_in_pd)
//     val bn_data_in_pvld = if(conf.NVDLA_SDP_BN_ENABLE) Some(Wire(Bool())) else None

//     //covert NVDLA_SDP_MAX_THROUGHPUT to NVDLA_SDP_BN_THROUGHPUT

//     val flop_bn_data_out_pvld = if(conf.NVDLA_SDP_BN_ENABLE) Wire(Bool()) else false.B
//     val flop_bn_data_out_pd = if(conf.NVDLA_SDP_BN_ENABLE) Wire(UInt(conf.BN_DOUT_DW.W)) else "b0".asUInt(conf.BS_DOUT_DW.W)

//     val u_bn_dppack = if(conf.NVDLA_SDP_BN_ENABLE) Some(Module(new NV_NVDLA_SDP_CORE_pack(conf.BN_DIN_DW, conf.BN_IN_DW))) else None
//     val u_bn = if(conf.NVDLA_SDP_BN_ENABLE) Some(Module(new NV_NVDLA_SDP_HLS_x2_int)) else None
//     val u_bn_dpunpack = if(conf.NVDLA_SDP_BN_ENABLE) Some(Module(new NV_NVDLA_SDP_CORE_unpack(conf.BN_OUT_DW, conf.BN_DOUT_DW))) else None
//     if(conf.NVDLA_SDP_BN_ENABLE){
//     bn_data_in_pvld.get := cfg_bn_en & bs2bn_data_pvld

//     u_bn_dppack.get.io.nvdla_core_clk := nvdla_gated_ncore_clk
//     u_bn_dppack.get.io.inp_pvld := bn_data_in_pvld.get
//     bn_data_in_prdy := u_bn_dppack.get.io.inp_prdy
//     u_bn_dppack.get.io.inp_data := bn_data_in_pd
//     u_bn.get.io.chn_in_pvld := u_bn_dppack.get.io.out_pvld
//     u_bn_dppack.get.io.out_prdy := u_bn.get.io.chn_in_prdy
//     u_bn.get.io.chn_data_in := u_bn_dppack.get.io.out_data

//     u_bn.get.io.nvdla_core_clk := nvdla_gated_ncore_clk
//     u_bn.get.io.chn_alu_op_pvld := bn_alu_in_vld.get
//     bn_alu_in_rdy.get := u_bn.get.io.chn_alu_op_prdy
//     u_bn.get.io.chn_alu_op := bn_alu_in_data.get
//     u_bn.get.io.chn_mul_op_pvld := bn_mul_in_vld.get
//     bn_mul_in_rdy.get := u_bn.get.io.chn_mul_op_prdy
//     u_bn.get.io.chn_mul_op := bn_mul_in_data.get
//     u_bn_dpunpack.get.io.inp_pvld := u_bn.get.io.chn_out_pvld
//     u_bn.get.io.chn_out_prdy := u_bn_dpunpack.get.io.inp_prdy
//     u_bn_dpunpack.get.io.inp_data := u_bn.get.io.chn_data_out
//     u_bn.get.io.cfg_alu_algo := cfg_bn_alu_algo.get
//     u_bn.get.io.cfg_alu_bypass := cfg_bn_alu_bypass.get
//     u_bn.get.io.cfg_alu_op := cfg_bn_alu_operand.get
//     u_bn.get.io.cfg_alu_shift_value := cfg_bn_alu_shift_value.get
//     u_bn.get.io.cfg_alu_src := cfg_bn_alu_src.get
//     u_bn.get.io.cfg_mul_bypass := cfg_bn_mul_bypass.get
//     u_bn.get.io.cfg_mul_op := cfg_bn_mul_operand.get
//     u_bn.get.io.cfg_mul_prelu := cfg_bn_mul_prelu.get
//     u_bn.get.io.cfg_mul_shift_value := cfg_bn_mul_shift_value.get
//     u_bn.get.io.cfg_mul_src := cfg_bn_mul_src.get
//     u_bn.get.io.cfg_relu_bypass := cfg_bn_relu_bypass.get 
    
//     //covert NVDLA_SDP_BN_THROUGHPUT to NVDLA_SDP_MAX_THROUGHPUT

//     u_bn_dpunpack.get.io.nvdla_core_clk := nvdla_gated_ncore_clk
//     flop_bn_data_out_pvld := u_bn_dpunpack.get.io.out_pvld
//     u_bn_dpunpack.get.io.out_prdy := flop_bn_data_out_prdy
//     flop_bn_data_out_pd := u_bn_dpunpack.get.io.out_data
//     }

//     //===========================================
//     // MUX between BN and EW
//     //===========================================
//     val flop_ew_data_out_prdy = Wire(Bool())
//     val ew_data_in_prdy = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     if(conf.NVDLA_SDP_EW_ENABLE){
//         flop_bn_data_out_prdy := Mux(cfg_ew_en, ew_data_in_prdy.get, flop_ew_data_out_prdy)
//     }
//     else{
//         flop_bn_data_out_prdy := flop_ew_data_out_prdy
//     }

//     val bn2ew_data_pvld = Mux(cfg_bn_en, flop_bn_data_out_pvld, bs2bn_data_pvld)
//     val ew_data_in_pd = Mux(cfg_bn_en, flop_bn_data_out_pd, bn_data_in_pd)
    
//     val ew_data_in_pvld = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     if(conf.NVDLA_SDP_EW_ENABLE){
//         ew_data_in_pvld.get := cfg_ew_en & bn2ew_data_pvld
//     }

//     //===========================================
//     // CORE: y
//     //===========================================
//     val flop_ew_data_out_pvld = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val flop_ew_data_out_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(UInt(conf.EW_DOUT_DW.W))) else None
//     //covert NVDLA_SDP_MAX_THROUGHPUT to NVDLA_SDP_EW_THROUGHPUT
//     val u_ew_dppack = if(conf.NVDLA_SDP_EW_ENABLE) Some(Module(new NV_NVDLA_SDP_CORE_pack(conf.EW_DIN_DW, conf.EW_IN_DW))) else None
//     val u_ew = if(conf.NVDLA_SDP_EW_ENABLE) Some(Module(new NV_NVDLA_SDP_CORE_y)) else None
//     val u_ew_dpunpack = if(conf.NVDLA_SDP_EW_ENABLE) Some(Module(new NV_NVDLA_SDP_CORE_unpack(conf.EW_OUT_DW, conf.EW_DOUT_DW))) else None
//     if(conf.NVDLA_SDP_EW_ENABLE){
        
//     u_ew_dppack.get.io.nvdla_core_clk := nvdla_gated_ecore_clk
//     u_ew_dppack.get.io.inp_pvld := ew_data_in_pvld.get
//     ew_data_in_prdy.get := u_ew_dppack.get.io.inp_prdy
//     u_ew_dppack.get.io.inp_data := ew_data_in_pd
//     u_ew.get.io.ew_data_in_pvld := u_ew_dppack.get.io.out_pvld
//     u_ew_dppack.get.io.out_prdy := u_ew.get.io.ew_data_in_prdy
//     u_ew.get.io.ew_data_in_pd := u_ew_dppack.get.io.out_data 

//     u_ew.get.io.nvdla_core_clk := nvdla_gated_ecore_clk
//     u_ew.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_ew.get.io.ew_alu_in_vld := ew_alu_in_vld.get
//     ew_alu_in_rdy.get := u_ew.get.io.ew_alu_in_rdy
//     u_ew.get.io.ew_alu_in_data := ew_alu_in_data.get
//     u_ew.get.io.ew_mul_in_vld := ew_mul_in_vld.get
//     ew_mul_in_rdy.get := u_ew.get.io.ew_mul_in_rdy
//     u_ew.get.io.ew_mul_in_data := ew_mul_in_data.get
//     u_ew_dpunpack.get.io.inp_pvld := u_ew.get.io.ew_data_out_pvld
//     u_ew.get.io.ew_data_out_prdy := u_ew_dpunpack.get.io.inp_prdy
//     u_ew_dpunpack.get.io.inp_data := u_ew.get.io.ew_data_out_pd

//     u_ew.get.io.reg2dp_ew_alu_algo := io.reg2dp_ew_alu_algo.get
//     u_ew.get.io.reg2dp_ew_alu_bypass := io.reg2dp_ew_alu_bypass.get
//     u_ew.get.io.reg2dp_ew_alu_cvt_bypass := io.reg2dp_ew_alu_cvt_bypass.get
//     u_ew.get.io.reg2dp_ew_alu_cvt_offset := io.reg2dp_ew_alu_cvt_offset.get
//     u_ew.get.io.reg2dp_ew_alu_cvt_scale := io.reg2dp_ew_alu_cvt_scale.get
//     u_ew.get.io.reg2dp_ew_alu_cvt_truncate := io.reg2dp_ew_alu_cvt_truncate.get
//     u_ew.get.io.reg2dp_ew_alu_operand := io.reg2dp_ew_alu_operand.get
//     u_ew.get.io.reg2dp_ew_alu_src := io.reg2dp_ew_alu_src.get
//     u_ew.get.io.reg2dp_ew_lut_bypass := io.reg2dp_ew_lut_bypass.get
//     u_ew.get.io.reg2dp_ew_mul_bypass := io.reg2dp_ew_mul_bypass.get
//     u_ew.get.io.reg2dp_ew_mul_cvt_bypass := io.reg2dp_ew_mul_cvt_bypass.get
//     u_ew.get.io.reg2dp_ew_mul_cvt_offset := io.reg2dp_ew_mul_cvt_offset.get
//     u_ew.get.io.reg2dp_ew_mul_cvt_scale := io.reg2dp_ew_mul_cvt_scale.get
//     u_ew.get.io.reg2dp_ew_mul_cvt_truncate := io.reg2dp_ew_mul_cvt_truncate.get
//     u_ew.get.io.reg2dp_ew_mul_operand := io.reg2dp_ew_mul_operand.get
//     u_ew.get.io.reg2dp_ew_mul_prelu := io.reg2dp_ew_mul_prelu.get
//     u_ew.get.io.reg2dp_ew_mul_src := io.reg2dp_ew_mul_src.get
//     u_ew.get.io.reg2dp_ew_truncate := io.reg2dp_ew_truncate.get

//     if(conf.NVDLA_SDP_LUT_ENABLE){
//         u_ew.get.io.reg2dp_lut_hybrid_priority.get := io.reg2dp_lut_hybrid_priority.get
//         u_ew.get.io.reg2dp_lut_int_access_type.get := io.reg2dp_lut_int_access_type.get
//         u_ew.get.io.reg2dp_lut_int_addr.get := io.reg2dp_lut_int_addr.get
//         u_ew.get.io.reg2dp_lut_int_data.get := io.reg2dp_lut_int_data.get
//         u_ew.get.io.reg2dp_lut_int_data_wr.get := io.reg2dp_lut_int_data_wr.get
//         u_ew.get.io.reg2dp_lut_int_table_id.get := io.reg2dp_lut_int_table_id.get
//         u_ew.get.io.reg2dp_lut_le_end.get := io.reg2dp_lut_le_end.get
//         u_ew.get.io.reg2dp_lut_le_function.get := io.reg2dp_lut_le_function.get
//         u_ew.get.io.reg2dp_lut_le_index_offset.get := io.reg2dp_lut_le_index_offset.get
//         u_ew.get.io.reg2dp_lut_le_index_select.get := io.reg2dp_lut_le_index_select.get
//         u_ew.get.io.reg2dp_lut_le_slope_oflow_scale.get := io.reg2dp_lut_le_slope_oflow_scale.get
//         u_ew.get.io.reg2dp_lut_le_slope_oflow_shift.get := io.reg2dp_lut_le_slope_oflow_shift.get
//         u_ew.get.io.reg2dp_lut_le_slope_uflow_scale.get := io.reg2dp_lut_le_slope_uflow_scale.get
//         u_ew.get.io.reg2dp_lut_le_slope_uflow_shift.get := io.reg2dp_lut_le_slope_uflow_shift.get
//         u_ew.get.io.reg2dp_lut_le_start.get := io.reg2dp_lut_le_start.get
//         u_ew.get.io.reg2dp_lut_lo_end.get := io.reg2dp_lut_lo_end.get
//         u_ew.get.io.reg2dp_lut_lo_index_select.get := io.reg2dp_lut_lo_index_select.get
//         u_ew.get.io.reg2dp_lut_lo_slope_oflow_scale.get := io.reg2dp_lut_lo_slope_oflow_scale.get
//         u_ew.get.io.reg2dp_lut_lo_slope_oflow_shift.get := io.reg2dp_lut_lo_slope_oflow_shift.get
//         u_ew.get.io.reg2dp_lut_lo_slope_uflow_scale.get := io.reg2dp_lut_lo_slope_uflow_scale.get
//         u_ew.get.io.reg2dp_lut_lo_slope_uflow_shift.get := io.reg2dp_lut_lo_slope_uflow_shift.get
//         u_ew.get.io.reg2dp_lut_lo_start.get := io.reg2dp_lut_lo_start.get
//         u_ew.get.io.reg2dp_lut_oflow_priority.get := io.reg2dp_lut_oflow_priority.get
//         u_ew.get.io.reg2dp_lut_uflow_priority.get := io.reg2dp_lut_uflow_priority.get
//         io.dp2reg_lut_hybrid.get := u_ew.get.io.dp2reg_lut_hybrid.get
//         io.dp2reg_lut_int_data.get := u_ew.get.io.dp2reg_lut_int_data.get
//         io.dp2reg_lut_le_hit.get := u_ew.get.io.dp2reg_lut_le_hit.get
//         io.dp2reg_lut_lo_hit.get := u_ew.get.io.dp2reg_lut_lo_hit.get
//         io.dp2reg_lut_oflow.get := u_ew.get.io.dp2reg_lut_oflow.get
//         io.dp2reg_lut_uflow.get := u_ew.get.io.dp2reg_lut_uflow.get
//     }
//     u_ew.get.io.reg2dp_nan_to_zero := io.reg2dp_nan_to_zero
//     u_ew.get.io.reg2dp_perf_lut_en := io.reg2dp_perf_lut_en
//     u_ew.get.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_ew.get.io.op_en_load := op_en_load
    

//     //covert NVDLA_SDP_EW_THROUGHPUT to NVDLA_SDP_MAX_THROUGHPUT
//     u_ew_dpunpack.get.io.nvdla_core_clk := nvdla_gated_ecore_clk
//     flop_ew_data_out_pvld.get := u_ew_dpunpack.get.io.out_pvld
//     u_ew_dpunpack.get.io.out_prdy := flop_ew_data_out_prdy
//     flop_ew_data_out_pd.get := u_ew_dpunpack.get.io.out_data
//     }

//     //===========================================
//     // MUX between EW and CVT
//     //===========================================
//     val ew2cvt_data_pvld = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
//     val cvt_data_in_prdy = Wire(Bool())
//     val cvt_data_in_pvld = Wire(Bool())
//     val cvt_data_in_pd = Wire(UInt(conf.CV_IN_DW.W))

//     if(conf.NVDLA_SDP_EW_ENABLE){
//         flop_ew_data_out_prdy := cvt_data_in_prdy
//         ew2cvt_data_pvld.get := Mux(cfg_ew_en, flop_ew_data_out_pvld.get, bn2ew_data_pvld)
//         cvt_data_in_pvld := ew2cvt_data_pvld.get
//         cvt_data_in_pd := Mux(cfg_ew_en, flop_ew_data_out_pd.get, ew_data_in_pd)
//     }
//     else{
//         flop_ew_data_out_prdy := cvt_data_in_prdy
//         cvt_data_in_pvld := bn2ew_data_pvld
//         cvt_data_in_pd   := ew_data_in_pd
//     }

//     val cvt_data_out_prdy = Wire(Bool())
//     val u_c = Module(new NV_NVDLA_SDP_HLS_c)
//     u_c.io.nvdla_core_clk := io.nvdla_core_clk
//     u_c.io.cvt_in_pvld := cvt_data_in_pvld
//     cvt_data_in_prdy := u_c.io.cvt_in_prdy
//     u_c.io.cvt_pd_in := cvt_data_in_pd
//     val cvt_data_out_pvld = u_c.io.cvt_out_pvld
//     u_c.io.cvt_out_prdy := cvt_data_out_prdy
//     val cvt_data_out_pd = u_c.io.cvt_pd_out
//     u_c.io.cfg_mode_eql := cfg_mode_eql
//     u_c.io.cfg_offset := cfg_cvt_offset
//     u_c.io.cfg_out_precision := cfg_out_precision
//     u_c.io.cfg_scale := cfg_cvt_scale
//     u_c.io.cfg_truncate := cfg_cvt_shift

//     val cvt_data_out_data = cvt_data_out_pd(conf.CV_OUT_DW-1,0)
//     val cvt_data_out_sat = cvt_data_out_pd(conf.CV_OUT_DW+conf.NVDLA_SDP_MAX_THROUGHPUT-1, conf.CV_OUT_DW)

//     // to (PDP | WDMA)
//     val cfg_mode_pdp = io.reg2dp_output_dst === 1.U
//     val core2wdma_rdy = Wire(Bool())
//     val core2pdp_rdy = Wire(Bool())
//     cvt_data_out_prdy := core2wdma_rdy & ((!cfg_mode_pdp) || core2pdp_rdy)
    
//     val core2wdma_vld  = cvt_data_out_pvld & ((!cfg_mode_pdp) || core2pdp_rdy)
//     val core2pdp_vld = cfg_mode_pdp & cvt_data_out_pvld & core2wdma_rdy

//     val core2wdma_pd = Mux(cfg_mode_pdp, Fill(conf.DP_OUT_DW, false.B), cvt_data_out_data)
//     val core2pdp_pd = Mux(cfg_mode_pdp, cvt_data_out_data(conf.DP_OUT_DW-1,0), Fill(conf.DP_OUT_DW, false.B))

//     //covert NVDLA_SDP_MAX_THROUGHPUT to atomic_m
//     //only int8 or int16. If support both, use NV_NVDLA_SDP_WDMA_unpack
//     val u_dpout_unpack = Module(new NV_NVDLA_SDP_CORE_unpack(conf.DP_OUT_DW, conf.DP_DOUT_DW))
//     u_dpout_unpack.io.nvdla_core_clk := io.nvdla_core_clk
//     u_dpout_unpack.io.inp_pvld := core2wdma_vld
//     core2wdma_rdy := u_dpout_unpack.io.inp_prdy
//     u_dpout_unpack.io.inp_data := core2wdma_pd
//     io.sdp_dp2wdma_valid := u_dpout_unpack.io.out_pvld
//     u_dpout_unpack.io.out_prdy := io.sdp_dp2wdma_ready
//     io.sdp_dp2wdma_pd := u_dpout_unpack.io.out_data

//     //pdp THROUGHPUT is NVDLA_SDP_MAX_THROUGHPUT
//     val pipe_p11 = Module{new NV_NVDLA_IS_pipe(conf.DP_OUT_DW)}
//     pipe_p11.io.clk := io.nvdla_core_clk
//     pipe_p11.io.vi := core2pdp_vld
//     core2pdp_rdy := pipe_p11.io.ro
//     pipe_p11.io.di := core2pdp_pd
//     io.sdp2pdp_valid := pipe_p11.io.vo
//     pipe_p11.io.ri := io.sdp2pdp_ready
//     io.sdp2pdp_pd := pipe_p11.io.dout

//     //===========================================
//     // PERF STATISTIC: SATURATION 

//     val saturation_bits = RegInit(0.U(conf.NVDLA_SDP_MAX_THROUGHPUT.W))
//     when(cvt_data_out_pvld & cvt_data_out_prdy){
//         saturation_bits := cvt_data_out_sat
//     }.otherwise{
//         saturation_bits := 0.U
//     }

//     val idata = Wire(UInt(16.W))
//     idata := saturation_bits
//     val cvt_saturation_add = idata(0) +& idata(1) +& idata(2) +& idata(3) +& idata(4) +& idata(5) +& 
//                              idata(6) +& idata(7) +& idata(8) +& idata(9) +& idata(10) +& idata(11) +&
//                              idata(12) +& idata(13) +& idata(14) +& idata(15)
//     val cvt_saturation_sub = false.B
//     val cvt_saturation_clr = op_en_load
//     val cvt_saturation_cen = io.reg2dp_perf_sat_en

//     val cvt_sat_add_ext = cvt_saturation_add
//     val cvt_sat_sub_ext = Cat(Fill(4, false.B), cvt_saturation_sub)
//     val cvt_sat_inc = cvt_sat_add_ext > cvt_sat_sub_ext
//     val cvt_sat_dec = cvt_sat_add_ext < cvt_sat_sub_ext
//     val cvt_sat_mod_ext = Mux(cvt_sat_inc, (cvt_sat_add_ext -& cvt_sat_sub_ext) , (cvt_sat_sub_ext -& cvt_sat_add_ext))(4, 0)

//     val cvt_saturation_cnt = Reg(UInt(32.W))
//     val cvt_sat_sub_guard = cvt_saturation_cnt(31,1).orR === false.B
//     val cvt_sat_sub_act = cvt_saturation_cnt(0)
//     val cvt_sat_sub_act_ext = Cat(Fill(4, false.B), cvt_sat_sub_act)
//     val cvt_sat_sub_flow = cvt_sat_dec & cvt_sat_sub_guard & (cvt_sat_sub_act_ext < cvt_sat_mod_ext)

//     val cvt_sat_add_guard = cvt_saturation_cnt(31,5).andR === true.B
//     val cvt_sat_add_act = cvt_saturation_cnt(4,0)
//     val cvt_sat_add_act_ext = cvt_sat_add_act
//     val cvt_sat_add_flow = cvt_sat_inc & cvt_sat_add_guard & ((cvt_sat_add_act_ext + cvt_sat_mod_ext) > 31.U )

//     val i_add = Mux(cvt_sat_add_flow, (31.U -& cvt_sat_add_act), Mux(cvt_sat_sub_flow, 0.U, cvt_saturation_add))
//     val i_sub = Mux(cvt_sat_sub_flow, cvt_sat_sub_act, Mux(cvt_sat_add_flow, 0.U, cvt_saturation_sub))

//     val cvt_sat_cvt_sat_adv = i_add =/= Cat(Fill(4,false.B), i_sub(0))

//     val cvt_sat_cvt_sat_cnt_cur = RegInit(0.U(32.W))
//     val cvt_sat_cvt_sat_cnt_ext = Cat(false.B, false.B, cvt_sat_cvt_sat_cnt_cur)
//     val cvt_sat_cvt_sat_cnt_mod = Reg(UInt(34.W))
//     cvt_sat_cvt_sat_cnt_mod := cvt_sat_cvt_sat_cnt_cur +& i_add(4, 0) -& i_sub(0)
//     val cvt_sat_cvt_sat_cnt_new = Mux(cvt_sat_cvt_sat_adv, cvt_sat_cvt_sat_cnt_mod, cvt_sat_cvt_sat_cnt_ext)
//     val cvt_sat_cvt_sat_cnt_nxt = Mux(cvt_saturation_clr, 0.U, cvt_sat_cvt_sat_cnt_new)

//     when(cvt_saturation_cen){
//         cvt_sat_cvt_sat_cnt_cur := cvt_sat_cvt_sat_cnt_nxt(31,0)
//     }
//     cvt_saturation_cnt := cvt_sat_cvt_sat_cnt_cur
//     io.dp2reg_out_saturation := cvt_saturation_cnt

// }}


// object NV_NVDLA_SDP_coreDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_core)
// }





