// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_intp(implicit val conf: cdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val dp2reg_done = Input(Bool())
//         val intp2mul_prdy = Input(Bool())
//         val lut2intp_X_data_0 = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//         val lut2intp_X_data_0_17b = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
//         val lut2intp_X_data_1 = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//         val lut2intp_X_info = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(20.W)))
//         val lut2intp_X_sel = Input(UInt(conf.NVDLA_CDP_THROUGHPUT.W))
//         val lut2intp_Y_sel = Input(UInt(conf.NVDLA_CDP_THROUGHPUT.W))
//         val lut2intp_pvld = Input(Bool())
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         val reg2dp_lut_le_end_high = Input(UInt(6.W))
//         val reg2dp_lut_le_end_low = Input(UInt(32.W))
//         val reg2dp_lut_le_function = Input(Bool())
//         val reg2dp_lut_le_index_offset = Input(UInt(8.W))
//         val reg2dp_lut_le_slope_oflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_le_slope_oflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_le_slope_uflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_le_slope_uflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_le_start_high = Input(UInt(6.W))
//         val reg2dp_lut_le_start_low = Input(UInt(32.W))
//         val reg2dp_lut_lo_end_high = Input(UInt(6.W))
//         val reg2dp_lut_lo_end_low = Input(UInt(32.W))
//         val reg2dp_lut_lo_slope_oflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_lo_slope_oflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_lo_slope_uflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_lo_slope_uflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_lo_start_high = Input(UInt(6.W))
//         val reg2dp_lut_lo_start_low = Input(UInt(32.W))
//         val reg2dp_sqsum_bypass = Input(Bool())
//         val sync2itp_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE*2+3)).W))
//         val sync2itp_pvld = Input(Bool())
//         val dp2reg_d0_perf_lut_hybrid = Output(UInt(32.W))
//         val dp2reg_d0_perf_lut_le_hit = Output(UInt(32.W))
//         val dp2reg_d0_perf_lut_lo_hit = Output(UInt(32.W))
//         val dp2reg_d0_perf_lut_oflow = Output(UInt(32.W))
//         val dp2reg_d0_perf_lut_uflow = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_hybrid = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_le_hit = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_lo_hit = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_oflow = Output(UInt(32.W))
//         val dp2reg_d1_perf_lut_uflow = Output(UInt(32.W))
//         val intp2mul_pd = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
//         val intp2mul_pvld = Output(Bool())
//         val lut2intp_prdy = Output(Bool())
//         val sync2itp_prdy = Output(Bool())


//     })

// withClock(io.nvdla_core_clk){

//     val x_exp = RegInit(false.B)
//     x_exp := (io.reg2dp_lut_le_function === 0.U)

//     val sqsum_bypass_enable = RegInit(false.B)
//     sqsum_bypass_enable := (io.reg2dp_sqsum_bypass === 1.U)

//     val reg2dp_lut_le_slope_uflow_shift_sync = RegInit(0.U(5.W))
//     reg2dp_lut_le_slope_uflow_shift_sync := io.reg2dp_lut_le_slope_uflow_shift

//     val reg2dp_lut_le_slope_oflow_shift_sync = RegInit(0.U(5.W))
//     reg2dp_lut_le_slope_oflow_shift_sync := io.reg2dp_lut_le_slope_oflow_shift

//     val reg2dp_lut_lo_slope_uflow_shift_sync = RegInit(0.U(5.W))
//     reg2dp_lut_lo_slope_uflow_shift_sync := io.reg2dp_lut_lo_slope_uflow_shift

//     val reg2dp_lut_lo_slope_oflow_shift_sync = RegInit(0.U(5.W))
//     reg2dp_lut_lo_slope_oflow_shift_sync := io.reg2dp_lut_lo_slope_oflow_shift

//     val reg2dp_lut_le_slope_uflow_scale_sync = RegInit(0.U(16.W))
//     reg2dp_lut_le_slope_uflow_scale_sync := io.reg2dp_lut_le_slope_uflow_scale

//     val reg2dp_lut_le_slope_oflow_scale_sync = RegInit(0.U(16.W))
//     reg2dp_lut_le_slope_oflow_scale_sync := io.reg2dp_lut_le_slope_oflow_scale

//     val reg2dp_lut_lo_slope_uflow_scale_sync = RegInit(0.U(16.W))
//     reg2dp_lut_lo_slope_uflow_scale_sync := io.reg2dp_lut_lo_slope_uflow_scale

//     val reg2dp_lut_lo_slope_oflow_scale_sync = RegInit(0.U(16.W))
//     reg2dp_lut_lo_slope_oflow_scale_sync := io.reg2dp_lut_lo_slope_oflow_scale

//     val le_slope_uflow_scale = Cat(reg2dp_lut_le_slope_uflow_scale_sync(15), reg2dp_lut_le_slope_uflow_scale_sync(15,0))
//     val le_slope_oflow_scale = Cat(reg2dp_lut_le_slope_oflow_scale_sync(15), reg2dp_lut_le_slope_oflow_scale_sync(15,0))
//     val lo_slope_uflow_scale = Cat(reg2dp_lut_lo_slope_uflow_scale_sync(15), reg2dp_lut_lo_slope_uflow_scale_sync(15,0))
//     val lo_slope_oflow_scale = Cat(reg2dp_lut_lo_slope_oflow_scale_sync(15), reg2dp_lut_lo_slope_oflow_scale_sync(15,0))

//     val lut2intp_pd_data = VecInit(
//         (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => Cat(
//                                         io.lut2intp_X_data_0(i), 
//                                         io.lut2intp_X_data_0_17b(i), 
//                                         io.lut2intp_X_data_1(i))})

//     val lut2intp_pd_info = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => Cat(io.lut2intp_X_info(i))})
//     val lut2intp_pd = Cat(
//                         lut2intp_pd_data,
//                         lut2intp_pd_info,
//                         io.lut2intp_X_sel, 
//                         io.lut2intp_Y_sel
//                         )

//     val lut2intp_ready = Wire(Bool())
//     val pipe = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_CDP_THROUGHPUT*103))
//     pipe.io.clk := io.nvdla_core_clk
//     pipe.io.vi := io.lut2intp_pvld
//     io.lut2intp_prdy := pipe.io.ro
//     pipe.io.di := lut2intp_pd
//     val lut2intp_valid = pipe.io.vo
//     pipe.io.ri := lut2intp_ready
//     val lut2intp_data = pipe.io.dout

// // assign {
// // //: my $k = NVDLA_CDP_THROUGHPUT;
// // //: foreach my $m  (0..$k-1) {
// // //:     print qq(
// // //:         lut2ip_X_data_${m}0[31:0],lut2ip_X_data_${m}0_17b[16:0],lut2ip_X_data_${m}1[31:0],
// // //:     );
// // //: }
// // //: my $k = NVDLA_CDP_THROUGHPUT;
// // //: foreach my $m  (0..$k-1) {
// // //:     print qq(
// // //:         lut2ip_X_info_${m}[19:0],
// // //:     );
// // //: }
// //         lut2ip_X_sel,
// //         lut2ip_Y_sel} = lut2intp_data;                    
    
// ///////////////////////////////////////////
// //lock
// //from lut2int and sync2itp to intp_in
//     val intp_in_prdy = Wire(Bool())
//     lut2intp_ready := intp_in_prdy & io.sync2itp_pvld
//     io.sync2itp_prdy := intp_in_prdy & lut2intp_valid
//     val intp_in_pvld = io.sync2itp_pvld & lut2intp_valid
// ///////////////////////////////////////////
//     val xinterp_in_rdy = Wire(UInt(conf.NVDLA_CDP_THROUGHPUT.W))
//     val info_in_rdy = Wire(Bool())
//     intp_in_prdy := (xinterp_in_rdy.asUInt.andR) & info_in_rdy

//     val SQBW = conf.NVDLA_CDP_ICVTO_BWPE * 2 + 3
//     val hit_in1_pd = VecInit(
//         (0 to conf.NVDLA_CDP_THROUGHPUT-1) map {
//             i => Mux(
//                 sqsum_bypass_enable, 
//                 Cat(Fill((38 - SQBW), io.sync2itp_pd(SQBW * (i + 1) - 1)), io.sync2itp_pd((SQBW * (i + 1) - 1), (SQBW * i))),
//                 Cat(Fill(17, false.B), io.sync2itp_pd((SQBW * (i + 1) - 1), (SQBW * i)))
//             )
//             }
//     )

// /////////////////////////////////////////////////
// //start/end prepare for out of range interpolation
// /////////////////////////////////////////////////

//     val lut_le_end = Cat(io.reg2dp_lut_le_end_high, io.reg2dp_lut_le_end_low)
//     val lut_le_start = Cat(io.reg2dp_lut_le_start_high, io.reg2dp_lut_le_start_low)
//     val lut_lo_end = Cat(io.reg2dp_lut_lo_end_high, io.reg2dp_lut_lo_end_low)
//     val lut_lo_start = Cat(io.reg2dp_lut_lo_start_high, io.reg2dp_lut_lo_start_low)

//     val le_offset_use = io.reg2dp_lut_le_index_offset(6,0)
//     val le_offset_exp = Mux(io.reg2dp_lut_le_index_offset(7), 0.U, (true.B << le_offset_use))

// // assign {mon_lut_le_min_int[90:0],lut_le_min_int[38:0]} = X_exp ? ($signed({{91{lut_le_start[37]}}, lut_le_start[37:0]}) + $signed({1'b0,le_offset_exp})) : {{92{lut_le_start[37]}}, lut_le_start[37:0]};

//     val lut_le_max = RegInit(0.U(38.W))
//     lut_le_max := lut_le_end

//     val lut_le_min = RegInit(0.U(39.W))
//     lut_le_min := lut_le_min_int

//     val lut_lo_max = RegInit(0.U(38.W))
//     lut_lo_max := lut_lo_end

//     val lut_lo_min = RegInit(0.U(38.W))
//     lut_lo_min := lut_lo_start

// /////////////////////////////////////////////////

//     val x_uflow = VecInit(
//         (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_x_info(i)(16)}
//     )

//     val x_oflow = VecInit(
//         (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_x_info(i)(17)}
//     )

//     val y_uflow = VecInit(
//         (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_x_info(i)(18)}
//     )

//     val y_oflow = VecInit(
//         (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_x_info(i)(19)}
//     )

//     val xinterp_in0_pd = Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(39.W)))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(lut2ip_x_sel(i)){
//             when(x_uflow(i)){
//                 xinterp_in0_pd(i) := lut_le_min
//             }.elsewhen(x_oflow(i)){
//                 xinterp_in0_pd(i) := Cat(lut_le_max(37), lut_le_max(37,0))
//             }.otherwise{
//                 xinterp_in0_pd(i) := Cat(Fill(7, lut2intp_X_data_0(i)(31)), lut2intp_X_data_0(i)(31,0))
//             }
//         }.elsewhen(lut2ip_y_sel(i)){
//             when(y_uflow){
//                 xinterp_in0_pd(i) := Cat(lut_lo_min(37), lut_lo_min(37,0))
//             }.elsewhen(y_oflow(i)){
//                 xinterp_in0_pd(i) := Cat(lut_lo_max(37), lut_lo_max(37,0))
//             }.otherwise{
//                 xinterp_in0_pd(i) := Cat(Fill(7, lut2intp_X_data_0(i)(31)), lut2intp_X_data_0(i)(31,0))
//             }
//         }.otherwise{
//             xinterp_in0_pd(i) := 0.U
//         }
//     }



// }}


// object NV_NVDLA_CDP_DP_INTP_unitDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_INTP_unit())
// }
