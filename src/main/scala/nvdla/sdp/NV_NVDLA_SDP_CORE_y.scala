// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_CORE_y(implicit val conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {

//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(Bool())
//         //alu_in
//         val ew_alu_in_data = Flipped(DecoupledIO(UInt(conf.EW_OP_DW.W)))
//         // data_in
//         val ew_data_in_pd = Flipped(DecoupledIO(UInt(conf.EW_IN_DW.W)))
//         // mul_in
//         val ew_mul_in_data = Flipped(DecoupledIO(UInt(conf.EW_OP_DW.W)))
//         // data_out
//         val ew_data_out_pd = DecoupledIO(UInt(conf.EW_OUT_DW.W))
//         // reg2dp
//         val reg2dp_ew_alu_algo = Input(UInt(2.W))
//         val reg2dp_ew_alu_bypass = Input(Bool())
//         val reg2dp_ew_alu_cvt_bypass = Input(Bool())
//         val reg2dp_ew_alu_cvt_offset = Input(UInt(32.W))
//         val reg2dp_ew_alu_cvt_scale = Input(UInt(16.W))
//         val reg2dp_ew_alu_cvt_truncate = Input(UInt(6.W))
//         val reg2dp_ew_alu_operand = Input(UInt(32.W))
//         val reg2dp_ew_alu_src = Input(Bool())
//         val reg2dp_ew_lut_bypass = Input(Bool())
//         val reg2dp_ew_mul_bypass = Input(Bool())
//         val reg2dp_ew_mul_cvt_bypass = Input(Bool())
//         val reg2dp_ew_mul_cvt_offset = Input(UInt(32.W))
//         val reg2dp_ew_mul_cvt_scale = Input(UInt(16.W))
//         val reg2dp_ew_mul_cvt_truncate = Input(UInt(6.W))
//         val reg2dp_ew_mul_operand = Input(UInt(32.W))
//         val reg2dp_ew_mul_prelu = Input(Bool())
//         val reg2dp_ew_mul_src = Input(Bool())
//         val reg2dp_ew_truncate = Input(UInt(10.W))

//         val reg2dp_nan_to_zero = Input(Bool())
//         val reg2dp_perf_lut_en = Input(Bool())
//         val reg2dp_proc_precision = Input(UInt(2.W))

//         val op_en_load = Input(Bool())
//     })

//     val sdp_lut_io = if(conf.NVDLA_SDP_LUT_ENABLE) Some(IO(new Bundle{

//         val reg2dp_lut_hybrid_priority = Input(Bool())
//         val reg2dp_lut_int_access_type = Input(Bool())
//         val reg2dp_lut_int_addr = Input(UInt(10.W))
//         val reg2dp_lut_int_data = Input(UInt(16.W))
//         val reg2dp_lut_int_data_wr = Input(Bool())
//         val reg2dp_lut_int_table_id = Input(Bool())
//         val reg2dp_lut_le_end = Input(UInt(32.W))
//         val reg2dp_lut_le_function = Input(Bool())
//         val reg2dp_lut_le_index_offset = Input(UInt(8.W))
//         val reg2dp_lut_le_index_select = Input(UInt(8.W))
//         val reg2dp_lut_le_slope_oflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_le_slope_oflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_le_slope_uflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_le_slope_uflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_le_start = Input(UInt(32.W))
//         val reg2dp_lut_lo_end = Input(UInt(32.W))
//         val reg2dp_lut_lo_index_select = Input(UInt(8.W))
//         val reg2dp_lut_lo_slope_oflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_lo_slope_oflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_lo_slope_uflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_lo_slope_uflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_lo_start = Input(UInt(32.W))
//         val reg2dp_lut_oflow_priority = Input(Bool())
//         val reg2dp_lut_uflow_priority = Input(Bool())
//         val dp2reg_lut_hybrid = Output(UInt(32.W))
//         val dp2reg_lut_int_data = Output(UInt(16.W))
//         val dp2reg_lut_le_hit = Output(UInt(32.W))
//         val dp2reg_lut_lo_hit = Output(UInt(32.W))
//         val dp2reg_lut_oflow = Output(UInt(32.W))
//         val dp2reg_lut_uflow = Output(UInt(32.W))

//     })) else None  
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

//     val cfg_proc_precision = RegInit("b0".asUInt(2.W))
//     val cfg_nan_to_zero = RegInit(false.B)
//     val cfg_ew_alu_operand = RegInit("b0".asUInt(32.W))
//     val cfg_ew_alu_bypass = RegInit(false.B)
//     val cfg_ew_alu_algo = RegInit("b0".asUInt(2.W))
//     val cfg_ew_alu_src = RegInit(false.B)
//     val cfg_ew_alu_cvt_bypass = RegInit(false.B)
//     val cfg_ew_alu_cvt_offset = RegInit("b0".asUInt(32.W))
//     val cfg_ew_alu_cvt_scale = RegInit("b0".asUInt(16.W))
//     val cfg_ew_alu_cvt_truncate = RegInit("b0".asUInt(6.W))
//     val cfg_ew_mul_operand = RegInit("b0".asUInt(32.W))
//     val cfg_ew_mul_bypass = RegInit(false.B)
//     val cfg_ew_mul_src = RegInit(false.B)
//     val cfg_ew_mul_cvt_bypass = RegInit(false.B)
//     val cfg_ew_mul_cvt_offset = RegInit("b0".asUInt(32.W))
//     val cfg_ew_mul_cvt_scale = RegInit("b0".asUInt(16.W))
//     val cfg_ew_mul_cvt_truncate = RegInit("b0".asUInt(6.W))
//     val cfg_ew_truncate = RegInit("b0".asUInt(10.W))
//     val cfg_ew_mul_prelu = RegInit(false.B)
//     val cfg_ew_lut_bypass = if(conf.NVDLA_SDP_LUT_ENABLE) RegInit(false.B) else RegInit(true.B)
//     val cfg_lut_le_start = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(32.W))) else None
//     val cfg_lut_le_end = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(32.W))) else None
//     val cfg_lut_lo_start = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(32.W))) else None
//     val cfg_lut_lo_end = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(32.W))) else None
//     val cfg_lut_le_index_offset = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(8.W))) else None
//     val cfg_lut_le_index_select = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(8.W))) else None
//     val cfg_lut_lo_index_select = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(8.W))) else None
//     val cfg_lut_le_function = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_lut_uflow_priority = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_lut_oflow_priority = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_lut_hybrid_priority = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit(false.B)) else None
//     val cfg_lut_le_slope_oflow_scale = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(16.W))) else None
//     val cfg_lut_le_slope_oflow_shift = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(5.W))) else None
//     val cfg_lut_le_slope_uflow_scale = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(16.W))) else None
//     val cfg_lut_le_slope_uflow_shift = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(5.W))) else None
//     val cfg_lut_lo_slope_oflow_scale = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(16.W))) else None
//     val cfg_lut_lo_slope_oflow_shift = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(5.W))) else None
//     val cfg_lut_lo_slope_uflow_scale = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(16.W))) else None
//     val cfg_lut_lo_slope_uflow_shift = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(5.W))) else None

//     when(io.op_en_load){
//         cfg_proc_precision := io.reg2dp_proc_precision
//         cfg_nan_to_zero := io.reg2dp_nan_to_zero
//         cfg_ew_alu_operand := io.reg2dp_ew_alu_operand
//         cfg_ew_alu_bypass := io.reg2dp_ew_alu_bypass
//         cfg_ew_alu_algo := io.reg2dp_ew_alu_algo
//         cfg_ew_alu_src := io.reg2dp_ew_alu_src
//         cfg_ew_alu_cvt_bypass := io.reg2dp_ew_alu_cvt_bypass
//         cfg_ew_alu_cvt_offset := io.reg2dp_ew_alu_cvt_offset
//         cfg_ew_alu_cvt_scale := io.reg2dp_ew_alu_cvt_scale
//         cfg_ew_alu_cvt_truncate := io.reg2dp_ew_alu_cvt_truncate
//         cfg_ew_mul_operand := io.reg2dp_ew_mul_operand  
//         cfg_ew_mul_bypass := io.reg2dp_ew_mul_bypass      
//         cfg_ew_mul_src := io.reg2dp_ew_mul_src         
//         cfg_ew_mul_cvt_bypass := io.reg2dp_ew_mul_cvt_bypass  
//         cfg_ew_mul_cvt_offset := io.reg2dp_ew_mul_cvt_offset  
//         cfg_ew_mul_cvt_scale  := io.reg2dp_ew_mul_cvt_scale   
//         cfg_ew_mul_cvt_truncate := io.reg2dp_ew_mul_cvt_truncate
//         cfg_ew_truncate := io.reg2dp_ew_truncate        
//         cfg_ew_mul_prelu := io.reg2dp_ew_mul_prelu  

//         if(conf.NVDLA_SDP_LUT_ENABLE){   
//             cfg_ew_lut_bypass := io.reg2dp_ew_lut_bypass     
//             cfg_lut_le_start.get := sdp_lut_io.get.reg2dp_lut_le_start         
//             cfg_lut_le_end.get := sdp_lut_io.get.reg2dp_lut_le_end
//             cfg_lut_lo_start.get := sdp_lut_io.get.reg2dp_lut_lo_start
//             cfg_lut_lo_end.get := sdp_lut_io.get.reg2dp_lut_lo_end  
//             cfg_lut_le_index_offset.get := sdp_lut_io.get.reg2dp_lut_le_index_offset
//             cfg_lut_le_index_select.get := sdp_lut_io.get.reg2dp_lut_le_index_select
//             cfg_lut_lo_index_select.get := sdp_lut_io.get.reg2dp_lut_lo_index_select
//             cfg_lut_le_function.get := sdp_lut_io.get.reg2dp_lut_le_function
//             cfg_lut_uflow_priority.get := sdp_lut_io.get.reg2dp_lut_uflow_priority
//             cfg_lut_oflow_priority.get := sdp_lut_io.get.reg2dp_lut_oflow_priority
//             cfg_lut_hybrid_priority.get := sdp_lut_io.get.reg2dp_lut_hybrid_priority

//             cfg_lut_le_slope_oflow_scale.get := sdp_lut_io.get.reg2dp_lut_le_slope_oflow_scale
//             cfg_lut_le_slope_oflow_shift.get := sdp_lut_io.get.reg2dp_lut_le_slope_oflow_shift
//             cfg_lut_le_slope_uflow_scale.get := sdp_lut_io.get.reg2dp_lut_le_slope_uflow_scale
//             cfg_lut_le_slope_uflow_shift.get := sdp_lut_io.get.reg2dp_lut_le_slope_uflow_shift

//             cfg_lut_lo_slope_oflow_scale.get := sdp_lut_io.get.reg2dp_lut_lo_slope_oflow_scale
//             cfg_lut_lo_slope_oflow_shift.get := sdp_lut_io.get.reg2dp_lut_lo_slope_oflow_shift
//             cfg_lut_lo_slope_uflow_scale.get := sdp_lut_io.get.reg2dp_lut_lo_slope_uflow_scale
//             cfg_lut_lo_slope_uflow_shift.get := sdp_lut_io.get.reg2dp_lut_lo_slope_uflow_shift
//         }  
//         else{
//             cfg_ew_lut_bypass := true.B
//         }  
//     }
//     //===========================================
//     // y input pipe
//     //===========================================

//     //=================================================
//     val alu_cvt_out_prdy = Wire(Bool())
//     val u_alu_cvt = Module(new NV_NVDLA_SDP_HLS_Y_cvt_top)
//     u_alu_cvt.io.nvdla_core_clk := io.nvdla_core_clk
//     u_alu_cvt.io.cfg_cvt_bypass := cfg_ew_alu_cvt_bypass
//     u_alu_cvt.io.cfg_cvt_offset := cfg_ew_alu_cvt_offset
//     u_alu_cvt.io.cfg_cvt_scale := cfg_ew_alu_cvt_scale
//     u_alu_cvt.io.cfg_cvt_truncate := cfg_ew_alu_cvt_truncate
//     u_alu_cvt.io.cvt_data_in := io.ew_alu_in_data
//     u_alu_cvt.io.cvt_in_pvld := io.ew_alu_in_vld
//     io.ew_alu_in_rdy := u_alu_cvt.io.cvt_out_prdy
//     u_alu_cvt.io.cvt_out_prdy := alu_cvt_out_prdy
//     val alu_cvt_out_pd = u_alu_cvt.io.cvt_data_out
//     val alu_cvt_out_pvld = u_alu_cvt.io.cvt_out_pvld

//     val mul_cvt_out_prdy = Wire(Bool())
//     val u_mul_cvt = Module(new NV_NVDLA_SDP_HLS_Y_cvt_top)
//     u_mul_cvt.io.nvdla_core_clk := io.nvdla_core_clk
//     u_mul_cvt.io.cfg_cvt_bypass := cfg_ew_mul_cvt_bypass
//     u_mul_cvt.io.cfg_cvt_offset := cfg_ew_mul_cvt_offset
//     u_mul_cvt.io.cfg_cvt_scale := cfg_ew_mul_cvt_scale
//     u_mul_cvt.io.cfg_cvt_truncate := cfg_ew_mul_cvt_truncate
//     u_mul_cvt.io.cvt_data_in := io.ew_mul_in_data
//     u_mul_cvt.io.cvt_in_pvld := io.ew_mul_in_vld
//     io.ew_mul_in_rdy := u_mul_cvt.io.cvt_out_prdy
//     u_mul_cvt.io.cvt_out_prdy := mul_cvt_out_prdy
//     val mul_cvt_out_pd = u_mul_cvt.io.cvt_data_out
//     val mul_cvt_out_pvld = u_mul_cvt.io.cvt_out_pvld

//     val core_out_prdy = Wire(Bool())
//     val u_core = Module(new NV_NVDLA_SDP_HLS_Y_int_core)
//     u_core.io.nvdla_core_clk := io.nvdla_core_clk
//     u_core.io.cfg_alu_algo := cfg_ew_alu_algo
//     u_core.io.cfg_alu_bypass := cfg_ew_alu_bypass
//     u_core.io.cfg_alu_op := cfg_ew_alu_operand
//     u_core.io.cfg_alu_src := cfg_ew_alu_src
//     u_core.io.cfg_mul_bypass := cfg_ew_mul_bypass
//     u_core.io.cfg_mul_op := cfg_ew_mul_operand
//     u_core.io.cfg_mul_prelu := cfg_ew_mul_prelu
//     u_core.io.cfg_mul_src := cfg_ew_mul_src
//     u_core.io.cfg_mul_truncate := cfg_ew_truncate
//     u_core.io.chn_alu_op := alu_cvt_out_pd
//     u_core.io.chn_alu_op_pvld := alu_cvt_out_pvld
//     u_core.io.chn_data_in := io.ew_data_in_pd
//     u_core.io.chn_in_pvld := io.ew_data_in_pvld
//     io.ew_data_in_prdy := u_core.io.chn_in_prdy
//     u_core.io.chn_mul_op := mul_cvt_out_pd
//     u_core.io.chn_mul_op_pvld := mul_cvt_out_pvld
//     alu_cvt_out_prdy := u_core.io.chn_alu_op_prdy
//     mul_cvt_out_prdy := u_core.io.chn_mul_op_prdy
//     val core_out_pd = u_core.io.chn_data_out
//     val core_out_pvld = u_core.io.chn_out_pvld
//     u_core.io.chn_out_prdy := core_out_prdy

//     val idx2lut_pvld = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(Bool())) else None
//     val idx2lut_prdy = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(Bool())) else None
//     val idx2lut_pd = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(UInt(conf.EW_IDX_OUT_DW.W))) else None

//     val idx_in_pvld = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Mux(cfg_ew_lut_bypass, false.B, core_out_pvld)) else None
//     val idx_in_prdy = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(Bool())) else None
//     val idx_in_pd = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Fill(conf.EW_CORE_OUT_DW, idx_in_pvld.get)&core_out_pd) else None

//     val inp_out_pvld = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(Bool())) else None
//     val inp_out_prdy = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(Bool())) else None
//     val inp_out_pd = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(UInt(conf.EW_INP_OUT_DW.W))) else None
    
//     val lut2inp_pvld = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(Bool())) else None
//     val lut2inp_prdy = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(Bool())) else None
//     val lut2inp_pd = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(UInt(conf.EW_LUT_OUT_DW.W))) else None

//     if(conf.NVDLA_SDP_LUT_ENABLE){
//         core_out_prdy := Mux(cfg_ew_lut_bypass, io.ew_data_out_prdy, idx_in_prdy.get) 
//     }
//     else{
//         core_out_prdy := io.ew_data_out_prdy
//     }

//     val u_idx = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Module(new NV_NVDLA_SDP_HLS_Y_idx_top)) else None
//     val u_lut = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Module(new NV_NVDLA_SDP_CORE_Y_lut)) else None
//     val u_inp = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Module(new NV_NVDLA_SDP_HLS_Y_inp_top)) else None

//     if(conf.NVDLA_SDP_LUT_ENABLE){
//         //u_idx
//         u_idx.get.io.nvdla_core_clk := io.nvdla_core_clk
//         u_idx.get.io.cfg_lut_hybrid_priority := cfg_lut_hybrid_priority.get
//         u_idx.get.io.cfg_lut_le_function := cfg_lut_le_function.get
//         u_idx.get.io.cfg_lut_le_index_offset := cfg_lut_le_index_offset.get
//         u_idx.get.io.cfg_lut_le_index_select := cfg_lut_le_index_select.get
//         u_idx.get.io.cfg_lut_le_start := cfg_lut_le_start.get
//         u_idx.get.io.cfg_lut_lo_index_select := cfg_lut_lo_index_select.get
//         u_idx.get.io.cfg_lut_lo_start := cfg_lut_lo_start.get
//         u_idx.get.io.cfg_lut_oflow_priority := cfg_lut_oflow_priority.get
//         u_idx.get.io.cfg_lut_uflow_priority := cfg_lut_uflow_priority.get
//         u_idx.get.io.chn_lut_in_pd := idx_in_pd.get
//         u_idx.get.io.chn_lut_in_pvld := idx_in_pvld.get
//         u_idx.get.io.chn_lut_out_prdy := idx2lut_prdy.get
//         idx_in_prdy.get := u_idx.get.io.chn_lut_in_prdy 
//         idx2lut_pd.get := u_idx.get.io.chn_lut_out_pd
//         idx2lut_pvld.get := u_idx.get.io.chn_lut_out_pvld

//         //u_lut
//         u_lut.get.io.nvdla_core_clk := io.nvdla_core_clk
//         lut2inp_pvld.get := u_lut.get.io.lut2inp_pvld
//         u_lut.get.io.lut2inp_prdy := lut2inp_prdy.get
//         lut2inp_pd.get := u_lut.get.io.lut2inp_pd
//         u_lut.get.io.idx2lut_pvld := idx2lut_pvld.get
//         idx2lut_prdy.get := u_lut.get.io.idx2lut_prdy
//         u_lut.get.io.idx2lut_pd := idx2lut_pd.get
//         u_lut.get.io.reg2dp_lut_int_access_type := io.reg2dp_lut_int_access_type.get
//         u_lut.get.io.reg2dp_lut_int_addr := io.reg2dp_lut_int_addr.get
//         u_lut.get.io.reg2dp_lut_int_data := io.reg2dp_lut_int_data.get
//         u_lut.get.io.reg2dp_lut_int_data_wr := io.reg2dp_lut_int_data_wr.get
//         u_lut.get.io.reg2dp_lut_int_table_id := io.reg2dp_lut_int_table_id.get
//         u_lut.get.io.reg2dp_lut_le_end := io.reg2dp_lut_le_end.get
//         u_lut.get.io.reg2dp_lut_le_function := io.reg2dp_lut_le_function.get
//         u_lut.get.io.reg2dp_lut_le_index_offset := io.reg2dp_lut_le_index_offset.get
//         u_lut.get.io.reg2dp_lut_le_slope_oflow_scale := cfg_lut_le_slope_oflow_scale.get
//         u_lut.get.io.reg2dp_lut_le_slope_oflow_shift := cfg_lut_le_slope_oflow_shift.get
//         u_lut.get.io.reg2dp_lut_le_slope_uflow_scale := cfg_lut_le_slope_uflow_scale.get
//         u_lut.get.io.reg2dp_lut_le_slope_uflow_shift := cfg_lut_le_slope_uflow_shift.get
//         u_lut.get.io.reg2dp_lut_le_start := cfg_lut_le_start.get
//         u_lut.get.io.reg2dp_lut_lo_end := cfg_lut_lo_end.get
//         u_lut.get.io.reg2dp_lut_lo_slope_oflow_scale := cfg_lut_lo_slope_oflow_scale.get
//         u_lut.get.io.reg2dp_lut_lo_slope_oflow_shift := cfg_lut_lo_slope_oflow_shift.get
//         u_lut.get.io.reg2dp_lut_lo_slope_uflow_scale := cfg_lut_lo_slope_uflow_scale.get
//         u_lut.get.io.reg2dp_lut_lo_slope_uflow_shift := cfg_lut_lo_slope_uflow_shift.get
//         u_lut.get.io.reg2dp_lut_lo_start := cfg_lut_lo_start.get
//         u_lut.get.io.reg2dp_perf_lut_en := io.reg2dp_perf_lut_en
//         u_lut.get.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//         io.dp2reg_lut_hybrid.get := u_lut.get.io.dp2reg_lut_hybrid
//         io.dp2reg_lut_int_data.get := u_lut.get.io.dp2reg_lut_int_data
//         io.dp2reg_lut_le_hit.get := u_lut.get.io.dp2reg_lut_le_hit
//         io.dp2reg_lut_lo_hit.get := u_lut.get.io.dp2reg_lut_lo_hit
//         io.dp2reg_lut_oflow.get := u_lut.get.io.dp2reg_lut_oflow
//         io.dp2reg_lut_uflow.get := u_lut.get.io.dp2reg_lut_uflow
//         u_lut.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//         u_lut.get.io.op_en_load := io.op_en_load

//         //u_inp
//         u_inp.get.io.nvdla_core_clk := io.nvdla_core_clk
//         u_inp.get.io.chn_inp_in_pvld := lut2inp_pvld.get
//         lut2inp_prdy.get := u_inp.get.io.chn_inp_in_prdy
//         u_inp.get.io.chn_inp_in_pd := lut2inp_pd.get
//         inp_out_pvld.get := u_inp.get.io.chn_inp_out_pvld
//         u_inp.get.io.chn_inp_out_prdy := inp_out_prdy.get
//         inp_out_pd.get := u_inp.get.io.chn_inp_out_pd

//         io.ew_data_out_pvld := Mux(cfg_ew_lut_bypass, core_out_pvld, inp_out_pvld.get)
//         io.ew_data_out_pd := Mux(cfg_ew_lut_bypass, core_out_pd, inp_out_pd.get)
//         inp_out_prdy.get := Mux(cfg_ew_lut_bypass, false.B, io.ew_data_out_prdy)
        
//     }
//     else{
//         io.ew_data_out_pvld := core_out_pvld
//         io.ew_data_out_pd := core_out_pd
//     }


// }}


// object NV_NVDLA_SDP_CORE_yDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_CORE_y)
// }


