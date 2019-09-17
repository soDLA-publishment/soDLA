// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_dp(implicit val conf: cdpConfiguration) extends Module {
//     val io = IO(new Bundle {

//         val nvdla_core_clk = Input(Clock())
//         val cdp_rdma2dp_valid = Input(Bool())
//         val cdp_rdma2dp_ready = Output(Bool())
//         val cdp_rdma2dp_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE+25).W))
//         val cdp_dp2wdma_valid = Output(Bool())
//         val cdp_dp2wdma_ready = Input(Bool())
//         val cdp_dp2wdma_pd = Output(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE+17).W))
//         val nvdla_core_clk_orig = Input(Clock())

//         val pwrbus_ram_pd = Input(UInt(32.W))
//         val dp2reg_done = Input(Bool())
//         val reg2dp_datin_offset = Input(UInt(16.W))
//         val reg2dp_datin_scale = Input(UInt(16.W))
//         val reg2dp_datin_shifter = Input(UInt(5.W))
//         val reg2dp_datout_offset = Input(UInt(32.W))
//         val reg2dp_datout_scale = Input(UInt(16.W))
//         val reg2dp_datout_shifter = Input(UInt(6.W))
//         val reg2dp_lut_access_type = Input(Bool())
//         val reg2dp_lut_addr = Input(UInt(10.W))
//         val reg2dp_lut_data = Input(UInt(16.W))
//         val reg2dp_lut_data_trigger = Input(Bool())
//         val reg2dp_lut_hybrid_priority = Input(Bool())
//         val reg2dp_lut_le_end_high = Input(UInt(6.W))
//         val reg2dp_lut_le_end_low = Input(UInt(32.W))
//         val reg2dp_lut_le_function = Input(Bool())
//         val reg2dp_lut_le_index_offset = Input(UInt(8.W))
//         val reg2dp_lut_le_index_select = Input(UInt(8.W))
//         val reg2dp_lut_le_slope_oflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_le_slope_oflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_le_slope_uflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_le_slope_uflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_le_start_high = Input(UInt(6.W))
//         val reg2dp_lut_le_start_low = Input(UInt(32.W))
//         val reg2dp_lut_lo_end_high = Input(UInt(6.W))
//         val reg2dp_lut_lo_end_low = Input(UInt(32.W))
//         val reg2dp_lut_lo_index_select = Input(UInt(8.W))
//         val reg2dp_lut_lo_slope_oflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_lo_slope_oflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_lo_slope_uflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_lo_slope_uflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_lo_start_high = Input(UInt(6.W))
//         val reg2dp_lut_lo_start_low = Input(UInt(32.W))
//         val reg2dp_lut_oflow_priority = Input(Bool())
//         val reg2dp_lut_table_id = Input(Bool())
//         val reg2dp_lut_uflow_priority = Input(Bool())
//         val reg2dp_mul_bypass = Input(Bool())
//         val reg2dp_normalz_len = Input(UInt(2.W))
//         val reg2dp_sqsum_bypass = Input(Bool())
//         val dp2reg_d0_out_saturation = Output(UInt(32.W))
//         val dp2reg_d0_perf_lut_hybrid = Output(UInt(32.W))
//         val dp2reg_d0_perf_lut_le_hit = Output(UInt(32.W))
//         val dp2reg_d0_perf_lut_lo_hit = Output(UInt(32.W))
//         val dp2reg_d0_perf_lut_oflow = Output(UInt(32.W))
//         val dp2reg_d0_perf_lut_uflow = Output(UInt(32.W))
//         val dp2reg_d1_out_saturation = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_hybrid = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_le_hit = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_lo_hit = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_oflow = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_uflow = Output(UInt(32.W))
//         val dp2reg_lut_data = Output(UInt(16.W))

//     })

// withClock(io.nvdla_core_clk){

//     io.dp2reg_d0_out_saturation := 0.U
//     io.dp2reg_d1_out_saturation := 0.U

//     val sqsum_bypass_en = RegInit(false.B)
//     sqsum_bypass_en := (io.reg2dp_sqsum_bypass === true.B)

// //===== convertor_in Instance========
//     val sum2itp_prdy = Wire(Bool())
//     val bufin_prdy = Wire(Bool())
//     val cvt2buf_prdy = Mux(sqsum_bypass_en, sum2itp_prdy, bufin_prdy)

//     val cvt2sync_prdy = Wire(Bool())

//     val u_NV_NVDLA_CDP_DP_cvtin = Module(new NV_NVDLA_CDP_DP_cvtin)
//     u_NV_NVDLA_CDP_DP_cvtin.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_CDP_DP_cvtin.io.cdp_rdma2dp_valid := io.cdp_rdma2dp_valid
//     io.cdp_rdma2dp_ready := u_NV_NVDLA_CDP_DP_cvtin.io.cdp_rdma2dp_ready
//     u_NV_NVDLA_CDP_DP_cvtin.io.cdp_rdma2dp_pd := io.cdp_rdma2dp_pd
//     u_NV_NVDLA_CDP_DP_cvtin.io.reg2dp_datin_offset := io.reg2dp_datin_offset
//     u_NV_NVDLA_CDP_DP_cvtin.io.reg2dp_datin_scale := io.reg2dp_datin_scale
//     u_NV_NVDLA_CDP_DP_cvtin.io.reg2dp_datin_shifter := io.reg2dp_datin_shifter
//     val cvt2buf_pd = u_NV_NVDLA_CDP_DP_cvtin.io.cvt2buf_pd
//     val cvt2buf_pvld = u_NV_NVDLA_CDP_DP_cvtin.io.cvt2buf_pvld
//     u_NV_NVDLA_CDP_DP_cvtin.io.cvt2buf_prdy := cvt2buf_prdy
//     val cvt2sync_pd = u_NV_NVDLA_CDP_DP_cvtin.io.cvt2sync_pd
//     val cvt2sync_pvld = u_NV_NVDLA_CDP_DP_cvtin.io.cvt2sync_pvld
//     u_NV_NVDLA_CDP_DP_cvtin.io.cvt2sync_prdy := cvt2sync_prdy

// //===== sync fifo Instance========
//     val sum2sync_pd = Wire(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE*2+3).W))
//     val sum2sync_pvld = Wire(Bool())
//     val sum2sync_prdy = Wire(Bool())
//     val sync2itp_pd = Wire(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE*2+3).W))
//     val sync2itp_pvld = Wire(Bool())
//     val sync2itp_prdy = Wire(Bool())
//     val sync2mul_pd = Wire(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE).W))
//     val sync2mul_pvld = Wire(Bool())
//     val sync2mul_prdy = Wire(Bool())
//     val sync2ocvt_pd = Wire(UInt(17.W))
//     val sync2ocvt_pvld = Wire(Bool())
//     val sync2ocvt_prdy = Wire(Bool())

    
// // NV_NVDLA_CDP_DP_syncfifo u_NV_NVDLA_CDP_DP_syncfifo (
// //    .nvdla_core_clk                  (nvdla_core_clk)             
// //   ,.nvdla_core_rstn                 (nvdla_core_rstn)            
// //   ,.cvt2sync_pd                     (cvt2sync_pd)          
// //   ,.cvt2sync_pvld                   (cvt2sync_pvld)              
// //   ,.pwrbus_ram_pd                   (pwrbus_ram_pd[31:0])        
// //   ,.sum2sync_pd                     (sum2sync_pd)         
// //   ,.sum2sync_pvld                   (sum2sync_pvld)              
// //   ,.sync2itp_prdy                   (sync2itp_prdy)              
// //   ,.sync2mul_prdy                   (sync2mul_prdy)              
// //   ,.sync2ocvt_prdy                  (sync2ocvt_prdy)             
// //   ,.cvt2sync_prdy                   (cvt2sync_prdy)              
// //   ,.sum2sync_prdy                   (sum2sync_prdy)              
// //   ,.sync2itp_pd                     (sync2itp_pd)         
// //   ,.sync2itp_pvld                   (sync2itp_pvld)              
// //   ,.sync2mul_pd                     (sync2mul_pd)          
// //   ,.sync2mul_pvld                   (sync2mul_pvld)              
// //   ,.sync2ocvt_pd                    (sync2ocvt_pd[16:0])         
// //   ,.sync2ocvt_pvld                  (sync2ocvt_pvld)             
// //   );

// //===== Buffer_in Instance========
//     val bufin_pd   = Mux(sqsum_bypass_en, 0.U, cvt2buf_pd)
//     val bufin_pvld = Mux(sqsum_bypass_en, false.B, cvt2buf_pvld)

//     val normalz_buf_data_prdy = Wire(Bool())
//     val u_NV_NVDLA_CDP_DP_bufferin = Module(new NV_NVDLA_CDP_DP_bufferin)
//     u_NV_NVDLA_CDP_DP_bufferin.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_CDP_DP_bufferin.io.cdp_rdma2dp_valid := bufin_pvld
//     bufin_prdy := u_NV_NVDLA_CDP_DP_bufferin.io.cdp_rdma2dp_ready
//     u_NV_NVDLA_CDP_DP_bufferin.io.cdp_rdma2dp_pd := bufin_pd
//     val normalz_buf_data_pvld = u_NV_NVDLA_CDP_DP_bufferin.io.normalz_buf_data_pvld
//     u_NV_NVDLA_CDP_DP_bufferin.io.normalz_buf_data_prdy := normalz_buf_data_prdy
//     val normalz_buf_data = u_NV_NVDLA_CDP_DP_bufferin.io.normalz_buf_data

// //: } elsif(NVDLA_CDP_THROUGHPUT < 4) {
// //...

// //===== sigma squre Instance========
//     val u_NV_NVDLA_CDP_DP_sum = Module(new NV_NVDLA_CDP_DP_sum)
//     u_NV_NVDLA_CDP_DP_sum.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_CDP_DP_sum.io.normalz_buf_data := normalz_buf_data
//     u_NV_NVDLA_CDP_DP_sum.io.normalz_buf_data_pvld := normalz_buf_data_pvld
//     normalz_buf_data_prdy := u_NV_NVDLA_CDP_DP_sum.io.normalz_buf_data_prdy
//     val sum2itp_pd = u_NV_NVDLA_CDP_DP_sum.io.sum2itp_pd
//     u_NV_NVDLA_CDP_DP_sum.io.sum2itp_prdy := sum2itp_prdy
//     val sum2itp_pvld = u_NV_NVDLA_CDP_DP_sum.io.sum2itp_pvld
//     u_NV_NVDLA_CDP_DP_sum.io.reg2dp_normalz_len := io.reg2dp_normalz_len

// //===== LUT controller Instance========
//     val cvtin_out_int8 = VecInit(
//         (0 until conf.NVDLA_CDP_THROUGHPUT) map
//         {i => cvt2buf_pd(i*conf.NVDLA_CDP_ICVTO_BWPE+conf.NVDLA_CDP_ICVTO_BWPE-1, i*conf.NVDLA_CDP_ICVTO_BWPE)}
//     )

//     val cvtin_out_int8_ext_elem = VecInit(
//         (0 until conf.NVDLA_CDP_THROUGHPUT) map 
//         {i => Cat(
//             Fill((conf.NVDLA_CDP_ICVTO_BWPE+3), cvtin_out_int8(i)(conf.NVDLA_CDP_ICVTO_BWPE-1)), 
//             cvtin_out_int8(i)
//             )}
//     )

//     val cvtin_out_int8_ext = cvtin_out_int8_ext_elem.asUInt

//     val lutctrl_in_pd = Cat(sqsum_bypass_en, cvtin_out_int8_ext, sum2itp_pd)
//     val lutctrl_in_pvld = Cat(sqsum_bypass_en, cvt2buf_pvld, sum2itp_pvld)

//     val dp2lut_prdy = Wire(Bool())
//     val dp2lut_X_entry = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(10.W)))
//     val dp2lut_Xinfo = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(18.W)))
//     val dp2lut_Y_entry = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(10.W)))
//     val dp2lut_Yinfo = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(18.W)))

//     val u_NV_NVDLA_CDP_DP_LUT_ctrl = Module(new NV_NVDLA_CDP_DP_LUT_ctrl)
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2itp_pvld := lutctrl_in_pvld
//     sum2itp_prdy := u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2itp_prdy
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2itp_pd := lutctrl_in_pd  
//     sum2sync_pvld := u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2sync_pvld
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2sync_prdy := sum2sync_prdy
//     sum2sync_pd := u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2sync_pd
//     val dp2lut_pvld = u_NV_NVDLA_CDP_DP_LUT_ctrl.io.dp2lut_pvld
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.dp2lut_prdy := dp2lut_prdy
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_sqsum_bypass := io.reg2dp_sqsum_bypass
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut_le_function := io.reg2dp_lut_le_function
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut_le_index_offset := io.reg2dp_lut_le_index_offset
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut_le_index_select := io.reg2dp_lut_le_index_select
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut_le_start_high := io.reg2dp_lut_le_start_high
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut_le_start_low := io.reg2dp_lut_le_start_low
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut_lo_index_select := io.reg2dp_lut_lo_index_select
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut_lo_start_high := io.reg2dp_lut_lo_start_high
//     u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut_lo_start_low := io.reg2dp_lut_lo_start_low
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         dp2lut_X_entry(i) := u_NV_NVDLA_CDP_DP_LUT_ctrl.io.dp2lut_X_entry(i)
//         dp2lut_Xinfo(i) := u_NV_NVDLA_CDP_DP_LUT_ctrl.io.dp2lut_Xinfo(i)
//         dp2lut_Y_entry(i) := u_NV_NVDLA_CDP_DP_LUT_ctrl.io.dp2lut_Y_entry(i)
//         dp2lut_Yinfo(i) := u_NV_NVDLA_CDP_DP_LUT_ctrl.io.dp2lut_Yinfo(i)
//     }

// //===== LUT Instance========
//     val lut2intp_prdy = Wire(Bool())
//     val lut2intp_X_data_0 = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//     val lut2intp_X_data_0_17b = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
//     val lut2intp_X_data_1 = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//     val lut2intp_X_info = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(20.W)))

//     val u_NV_NVDLA_CDP_DP_lut = Module(new NV_NVDLA_CDP_DP_lut)
//     u_NV_NVDLA_CDP_DP_lut.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_CDP_DP_lut.io.nvdla_core_clk_orig := io.nvdla_core_clk_orig
//     u_NV_NVDLA_CDP_DP_lut.io.dp2lut_pvld := dp2lut_pvld
//     u_NV_NVDLA_CDP_DP_lut.io.lut2intp_prdy := lut2intp_prdy
//     u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut_access_type := io.reg2dp_lut_access_type
//     u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut_addr := io.reg2dp_lut_addr
//     u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut_data := io.reg2dp_lut_data
//     u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut_data_trigger := io.reg2dp_lut_data_trigger
//     u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut_hybrid_priority := io.reg2dp_lut_hybrid_priority
//     u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut_oflow_priority := io.reg2dp_lut_oflow_priority
//     u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut_table_id := io.reg2dp_lut_table_id
//     u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut_uflow_priority := io.reg2dp_lut_uflow_priority
//     dp2lut_prdy := u_NV_NVDLA_CDP_DP_lut.io.dp2lut_prdy
//     io.dp2reg_lut_data := u_NV_NVDLA_CDP_DP_lut.io.dp2reg_lut_data
//     val lut2intp_X_sel = u_NV_NVDLA_CDP_DP_lut.io.lut2intp_X_sel
//     val lut2intp_Y_sel = u_NV_NVDLA_CDP_DP_lut.io.lut2intp_Y_sel
//     val lut2intp_pvld = u_NV_NVDLA_CDP_DP_lut.io.lut2intp_pvld
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         u_NV_NVDLA_CDP_DP_lut.io.dp2lut_X_entry(i) := dp2lut_X_entry(i)
//         u_NV_NVDLA_CDP_DP_lut.io.dp2lut_Xinfo(i) := dp2lut_Xinfo(i)
//         u_NV_NVDLA_CDP_DP_lut.io.dp2lut_Y_entry(i) := dp2lut_Y_entry(i)
//         u_NV_NVDLA_CDP_DP_lut.io.dp2lut_Yinfo(i) := dp2lut_Yinfo(i)
//         lut2intp_X_data_0(i) := u_NV_NVDLA_CDP_DP_lut.io.lut2intp_X_data_0(i)
//         lut2intp_X_data_0_17b(i) := u_NV_NVDLA_CDP_DP_lut.io.lut2intp_X_data_0_17b(i)
//         lut2intp_X_data_1(i) := u_NV_NVDLA_CDP_DP_lut.io.lut2intp_X_data_1(i)
//         lut2intp_X_info(i) := u_NV_NVDLA_CDP_DP_lut.io.lut2intp_X_info(i)
//     }

// //===== interpolator Instance========
//     val intp2mul_prdy = Wire(Bool())
//     val intp2mul_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))


//     val u_NV_NVDLA_CDP_DP_intp = Module(new NV_NVDLA_CDP_DP_intp)
//     u_NV_NVDLA_CDP_DP_intp.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_CDP_DP_intp.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     val intp2mul_pvld = u_NV_NVDLA_CDP_DP_intp.io.intp2mul_pvld
//     u_NV_NVDLA_CDP_DP_intp.io.intp2mul_prdy := intp2mul_prdy
//     u_NV_NVDLA_CDP_DP_intp.io.lut2intp_pvld := lut2intp_pvld
//     lut2intp_prdy := u_NV_NVDLA_CDP_DP_intp.io.lut2intp_prdy
//     u_NV_NVDLA_CDP_DP_intp.io.sync2itp_pvld := sync2itp_pvld
//     sync2itp_prdy := u_NV_NVDLA_CDP_DP_intp.io.sync2itp_prdy
//     u_NV_NVDLA_CDP_DP_intp.io.sync2itp_pd := sync2itp_pd
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_end_high := io.reg2dp_lut_le_end_high
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_end_low := io.reg2dp_lut_le_end_low
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_function := io.reg2dp_lut_le_function
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_index_offset := io.reg2dp_lut_le_index_offset
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_slope_oflow_scale := io.reg2dp_lut_le_slope_oflow_scale
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_slope_oflow_shift := io.reg2dp_lut_le_slope_oflow_shift
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_slope_uflow_scale := io.reg2dp_lut_le_slope_uflow_scale
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_slope_uflow_shift := io.reg2dp_lut_le_slope_uflow_shift
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_start_high := io.reg2dp_lut_le_start_high
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_le_start_low := io.reg2dp_lut_le_start_low
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_lo_end_high := io.reg2dp_lut_lo_end_high
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_lo_end_low := io.reg2dp_lut_lo_end_low
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_lo_slope_oflow_scale := io.reg2dp_lut_lo_slope_oflow_scale
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_lo_slope_oflow_shift := io.reg2dp_lut_lo_slope_oflow_shift
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_lo_slope_uflow_scale := io.reg2dp_lut_lo_slope_uflow_scale
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_lo_slope_uflow_shift := io.reg2dp_lut_lo_slope_uflow_shift
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_lo_start_high := io.reg2dp_lut_lo_start_high
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut_lo_start_low := io.reg2dp_lut_lo_start_low
//     u_NV_NVDLA_CDP_DP_intp.io.reg2dp_sqsum_bypass := io.reg2dp_sqsum_bypass
//     io.dp2reg_d0_perf_lut_hybrid := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d0_perf_lut_hybrid
//     io.dp2reg_d0_perf_lut_le_hit := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d0_perf_lut_le_hit
//     io.dp2reg_d0_perf_lut_lo_hit := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d0_perf_lut_lo_hit
//     io.dp2reg_d0_perf_lut_oflow := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d0_perf_lut_oflow
//     io.dp2reg_d0_perf_lut_uflow := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d0_perf_lut_uflow
//     io.dp2reg_d1_perf_lut_hybrid := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d1_perf_lut_hybrid
//     io.dp2reg_d1_perf_lut_le_hit := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d1_perf_lut_le_hit
//     io.dp2reg_d1_perf_lut_lo_hit := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d1_perf_lut_lo_hit
//     io.dp2reg_d1_perf_lut_oflow := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d1_perf_lut_oflow
//     io.dp2reg_d1_perf_lut_uflow := u_NV_NVDLA_CDP_DP_intp.io.dp2reg_d1_perf_lut_uflow
//     u_NV_NVDLA_CDP_DP_intp.io.dp2reg_done := io.dp2reg_done
//     u_NV_NVDLA_CDP_DP_intp.io.lut2intp_X_sel := lut2intp_X_sel
//     u_NV_NVDLA_CDP_DP_intp.io.lut2intp_Y_sel := lut2intp_Y_sel
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         intp2mul_pd(i) := u_NV_NVDLA_CDP_DP_intp.io.intp2mul_pd(i)
//         u_NV_NVDLA_CDP_DP_intp.io.lut2intp_X_data_0(i) := lut2intp_X_data_0(i)
//         u_NV_NVDLA_CDP_DP_intp.io.lut2intp_X_data_0_17b(i) := lut2intp_X_data_0_17b(i)
//         u_NV_NVDLA_CDP_DP_intp.io.lut2intp_X_data_1(i) := lut2intp_X_data_1(i)
//         u_NV_NVDLA_CDP_DP_intp.io.lut2intp_X_info(i) := lut2intp_X_info(i)

//     }

// //===== DP multiple Instance========

//     val mul2ocvt_prdy = Wire(Bool())

//     val u_NV_NVDLA_CDP_DP_mul = Module(new NV_NVDLA_CDP_DP_mul)
//     u_NV_NVDLA_CDP_DP_mul.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_CDP_DP_mul.io.intp2mul_pvld := intp2mul_pvld
//     intp2mul_prdy := u_NV_NVDLA_CDP_DP_mul.io.intp2mul_prdy
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         u_NV_NVDLA_CDP_DP_mul.io.intp2mul_pd(i) := intp2mul_pd(i)
//     }
//     u_NV_NVDLA_CDP_DP_mul.io.sync2mul_pvld := sync2mul_pvld
//     sync2mul_prdy := u_NV_NVDLA_CDP_DP_mul.io.sync2mul_prdy
//     u_NV_NVDLA_CDP_DP_mul.io.sync2mul_pd := sync2mul_pd
//     val mul2ocvt_pvld = u_NV_NVDLA_CDP_DP_mul.io.mul2ocvt_pvld
//     u_NV_NVDLA_CDP_DP_mul.io.mul2ocvt_prdy := mul2ocvt_prdy
//     val mul2ocvt_pd = u_NV_NVDLA_CDP_DP_mul.io.mul2ocvt_pd
//     u_NV_NVDLA_CDP_DP_mul.io.reg2dp_mul_bypass := io.reg2dp_mul_bypass

// //===== convertor_out Instance========
//     val cvtout_prdy = Wire(Bool())

//     val u_NV_NVDLA_CDP_DP_cvtout = Module(new NV_NVDLA_CDP_DP_cvtout)
//     u_NV_NVDLA_CDP_DP_cvtout.io.nvdla_core_clk := io.nvdla_core_clk
//     val cvtout_pd = u_NV_NVDLA_CDP_DP_cvtout.io.cvtout_pd
//     val cvtout_pvld = u_NV_NVDLA_CDP_DP_cvtout.io.cvtout_pvld
//     u_NV_NVDLA_CDP_DP_cvtout.io.mul2ocvt_pvld := mul2ocvt_pvld
//     u_NV_NVDLA_CDP_DP_cvtout.io.mul2ocvt_pd := mul2ocvt_pd
//     mul2ocvt_prdy := u_NV_NVDLA_CDP_DP_cvtout.io.mul2ocvt_prdy
//     u_NV_NVDLA_CDP_DP_cvtout.io.reg2dp_datout_offset := io.reg2dp_datout_offset
//     u_NV_NVDLA_CDP_DP_cvtout.io.reg2dp_datout_scale := io.reg2dp_datout_scale
//     u_NV_NVDLA_CDP_DP_cvtout.io.reg2dp_datout_shifter := io.reg2dp_datout_shifter
//     u_NV_NVDLA_CDP_DP_cvtout.io.sync2ocvt_pd := sync2ocvt_pd
//     u_NV_NVDLA_CDP_DP_cvtout.io.sync2ocvt_pvld := sync2ocvt_pvld
//     sync2ocvt_prdy := u_NV_NVDLA_CDP_DP_cvtout.io.sync2ocvt_prdy

// ////==============
// ////OBS signals
// ////==============


// }}


// object NV_NVDLA_CDP_dpDriver extends App {
//     implicit val conf: cdpConfiguration = new cdpConfiguration
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_dp())
// }
