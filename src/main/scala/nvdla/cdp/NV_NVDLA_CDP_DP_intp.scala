// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_intp(implicit val conf: cdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         val intp2mul_pvld = Output(Bool())
//         val intp2mul_prdy = Input(Bool())
//         val intp2mul_pd = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))

//         val lut2intp_pvld = Input(Bool())
//         val lut2intp_prdy = Output(Bool())
//         val lut2intp_X_data_0 = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//         val lut2intp_X_data_0_17b = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
//         val lut2intp_X_data_1 = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//         val lut2intp_X_info = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(20.W)))
//         val lut2intp_X_sel = Input(UInt(conf.NVDLA_CDP_THROUGHPUT.W))
//         val lut2intp_Y_sel = Input(UInt(conf.NVDLA_CDP_THROUGHPUT.W))

//         val sync2itp_pvld = Input(Bool())
//         val sync2itp_prdy = Output(Bool())
//         val sync2itp_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE*2+3)).W))
        
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

//         val dp2reg_done = Input(Bool())
//     })

// withClock(io.nvdla_core_clk){
//     ///////////////////////////////////////////
//     val x_exp = RegInit(false.B)
//     val sqsum_bypass_enable = RegInit(false.B)
//     val reg2dp_lut_le_slope_uflow_shift_sync = RegInit(0.U(5.W))
//     val reg2dp_lut_le_slope_oflow_shift_sync = RegInit(0.U(5.W))
//     val reg2dp_lut_lo_slope_uflow_shift_sync = RegInit(0.U(5.W))
//     val reg2dp_lut_lo_slope_oflow_shift_sync = RegInit(0.U(5.W))
//     val reg2dp_lut_le_slope_uflow_scale_sync = RegInit(0.U(16.W))
//     val reg2dp_lut_le_slope_oflow_scale_sync = RegInit(0.U(16.W))
//     val reg2dp_lut_lo_slope_uflow_scale_sync = RegInit(0.U(16.W))
//     val reg2dp_lut_lo_slope_oflow_scale_sync = RegInit(0.U(16.W))

//     x_exp := (io.reg2dp_lut_le_function === 0.U)
//     sqsum_bypass_enable := (io.reg2dp_sqsum_bypass === 1.U)
//     reg2dp_lut_le_slope_uflow_shift_sync := io.reg2dp_lut_le_slope_uflow_shift
//     reg2dp_lut_le_slope_oflow_shift_sync := io.reg2dp_lut_le_slope_oflow_shift
//     reg2dp_lut_lo_slope_uflow_shift_sync := io.reg2dp_lut_lo_slope_uflow_shift
//     reg2dp_lut_lo_slope_oflow_shift_sync := io.reg2dp_lut_lo_slope_oflow_shift
//     reg2dp_lut_le_slope_uflow_scale_sync := io.reg2dp_lut_le_slope_uflow_scale
//     reg2dp_lut_le_slope_oflow_scale_sync := io.reg2dp_lut_le_slope_oflow_scale
//     reg2dp_lut_lo_slope_uflow_scale_sync := io.reg2dp_lut_lo_slope_uflow_scale
//     reg2dp_lut_lo_slope_oflow_scale_sync := io.reg2dp_lut_lo_slope_oflow_scale
//     ///////////////////////////////////////////
//     val le_slope_uflow_scale = Cat(reg2dp_lut_le_slope_uflow_scale_sync(15), reg2dp_lut_le_slope_uflow_scale_sync(15,0))
//     val le_slope_oflow_scale = Cat(reg2dp_lut_le_slope_oflow_scale_sync(15), reg2dp_lut_le_slope_oflow_scale_sync(15,0))
//     val lo_slope_uflow_scale = Cat(reg2dp_lut_lo_slope_uflow_scale_sync(15), reg2dp_lut_lo_slope_uflow_scale_sync(15,0))
//     val lo_slope_oflow_scale = Cat(reg2dp_lut_lo_slope_oflow_scale_sync(15), reg2dp_lut_lo_slope_oflow_scale_sync(15,0))

//     ///////////////////////////////////////////
//     //lut2intp pipe sync for timing

//     val lut2intp_pd_data = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
//     map {i => Cat(io.lut2intp_X_data_0(i), io.lut2intp_X_data_0_17b(i), io.lut2intp_X_data_1(i))}).asUInt
//     val lut2intp_pd_info = io.lut2intp_X_info.asUInt
//     val lut2intp_pd = Cat(lut2intp_pd_data, lut2intp_pd_info, io.lut2intp_X_sel, io.lut2intp_Y_sel)

//     val lut2intp_ready = Wire(Bool())
//     val pipe = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_CDP_THROUGHPUT*103))
//     pipe.io.clk := io.nvdla_core_clk
//     pipe.io.vi := io.lut2intp_pvld
//     io.lut2intp_prdy := pipe.io.ro
//     pipe.io.di := lut2intp_pd
//     val lut2intp_valid = pipe.io.vo
//     pipe.io.ri := lut2intp_ready
//     val lut2intp_data = pipe.io.dout

//     val lut2ip_X_data_0 = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//     val lut2ip_X_data_0_17b = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
//     val lut2ip_X_data_1 = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//     val lut2ip_X_info = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(20.W)))

//     val lut2ip_X_info = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
//     map {i => Cat(io.lut2intp_X_data_0(i), io.lut2intp_X_data_0_17b(i), io.lut2intp_X_data_1(i))})
//     val lut2ip_X_sel = lut2intp_data(2*conf.NVDLA_CDP_THROUGHPUT-1, conf.NVDLA_CDP_THROUGHPUT)
//     val lut2ip_Y_sel = lut2intp_data(conf.NVDLA_CDP_THROUGHPUT-1, 0)

//     val lut2ip_pd_info = Cat(io.lut2ip_X_info)

//     val unpacked = lut2intp_data.asTypeOf(new MyBundle)
//     unpacked.lut2ip_pd_data
//     unpacked.lut2ip_pd_info
//     unpacked.lut2ip_X_sel.asUInt
//     unpacked.lut2ip_Y_sel.asUInt


    
//     ///////////////////////////////////////////
//     //lock
//     //from lut2int and sync2itp to intp_in
//     val intp_in_prdy = Wire(Bool())
//     lut2intp_ready := intp_in_prdy & io.sync2itp_pvld
//     io.sync2itp_prdy := intp_in_prdy & lut2intp_valid
//     val intp_in_pvld = io.sync2itp_pvld & lut2intp_valid
// ///////////////////////////////////////////
//     val xinterp_in_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
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

//     val lut_le_min_int = Wire(UInt(39.W))
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
//         (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_X_info(i)(16)}
//     )

//     val x_oflow = VecInit(
//         (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_X_info(i)(17)}
//     )

//     val y_uflow = VecInit(
//         (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_X_info(i)(18)}
//     )

//     val y_oflow = VecInit(
//         (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_X_info(i)(19)}
//     )

//     val xinterp_in0_pd = Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(39.W)))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(lut2ip_X_sel(i)){
//             when(x_uflow(i)){
//                 xinterp_in0_pd(i) := lut_le_min
//             }.elsewhen(x_oflow(i)){
//                 xinterp_in0_pd(i) := Cat(lut_le_max(37), lut_le_max(37,0))
//             }.otherwise{
//                 xinterp_in0_pd(i) := Cat(Fill(7, lut2ip_X_data_0(i)(31)), lut2ip_X_data_0(i))
//             }
//         }.elsewhen(lut2ip_Y_sel(i)){
//             when(y_uflow(i)){
//                 xinterp_in0_pd(i) := Cat(lut_lo_min(37), lut_lo_min)
//             }.elsewhen(y_oflow(i)){
//                 xinterp_in0_pd(i) := Cat(lut_lo_max(37), lut_lo_max)
//             }.otherwise{
//                 xinterp_in0_pd(i) := Cat(Fill(7, lut2ip_X_data_0(i)(31)), lut2ip_X_data_0(i))
//             }
//         }.otherwise{
//             xinterp_in0_pd(i) := 0.U
//         }
//     }

//     val xinterp_in1_pd = Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(38.W)))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(lut2ip_X_sel(i)){
//             when(x_uflow(i) | x_oflow(i)){
//                 xinterp_in1_pd(i) := hit_in1_pd(i)
//             }.otherwise{
//                 xinterp_in1_pd(i) := Cat(Fill(6, lut2ip_X_data_1(i)(31)), lut2ip_X_data_1(i))
//             }
//         }.elsewhen(lut2ip_Y_sel(i)){
//             when(y_uflow(i) | y_oflow(i)){
//                 xinterp_in1_pd(i) := hit_in1_pd(i)
//             }.otherwise{
//                 xinterp_in1_pd(i) := Cat(Fill(6, lut2ip_X_data_1(i)(31)), lut2ip_X_data_1(i))
//             }
//         }.otherwise{
//             xinterp_in1_pd(i) := 0.U
//         }
//     }

//     val xinterp_in_pd = Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(lut2ip_X_sel(i) | lut2ip_Y_sel(i)){
//             xinterp_in_pd(i) := lut2ip_X_data_0_17b(i)
//         }.otherwise{
//             xinterp_in_pd(i) := 0.U
//         }
//     }

//      val xinterp_in_scale = Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(lut2ip_X_sel(i)){
//             when(x_uflow(i)){
//                 xinterp_in_scale(i) := le_slope_uflow_scale
//             }.elsewhen(x_oflow(i)){
//                 xinterp_in_scale(i) := le_slope_oflow_scale
//             }.otherwise{
//                 xinterp_in_scale(i) := Cat(false.B, lut2ip_X_info(i)(15,0))
//             }
//         }.elsewhen(lut2ip_Y_sel(i)){
//             when(y_uflow(i)){
//                 xinterp_in_scale(i) := lo_slope_uflow_scale
//             }.elsewhen(y_oflow(i)){
//                 xinterp_in_scale(i) := lo_slope_oflow_scale
//             }.otherwise{
//                 xinterp_in_scale(i) := Cat(false.B, lut2ip_X_info(i)(15,0))
//             }
//         }.otherwise{
//             xinterp_in_scale(i) := 0.U
//         }
//     }

//      val xinterp_in_shift = Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(6.W)))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(lut2ip_X_sel(i)){
//             when(x_uflow(i)){
//                 xinterp_in_shift(i) := Cat(reg2dp_lut_le_slope_uflow_shift_sync(4), reg2dp_lut_le_slope_uflow_shift_sync)
//             }.elsewhen(x_oflow(i)){
//                 xinterp_in_shift(i) := Cat(reg2dp_lut_le_slope_oflow_shift_sync(4), reg2dp_lut_le_slope_oflow_shift_sync)
//             }.otherwise{
//                 xinterp_in_shift(i) := Cat(false.B, 16.U(5.W))
//             }
//         }.elsewhen(lut2ip_Y_sel(i)){
//             when(y_uflow(i)){
//                 xinterp_in_shift(i) := Cat(reg2dp_lut_lo_slope_uflow_shift_sync(4), reg2dp_lut_lo_slope_uflow_shift_sync)
//             }.elsewhen(y_oflow(i)){
//                 xinterp_in_shift(i) := Cat(reg2dp_lut_lo_slope_oflow_shift_sync(4), reg2dp_lut_lo_slope_oflow_shift_sync)
//             }.otherwise{
//                 xinterp_in_shift(i) := Cat(false.B, 16.U(5.W))
//             }
//         }.otherwise{
//             xinterp_in_shift(i) := 0.U
//         } 
//     }  

//     val xinterp_in_vld = Cat(
//         VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map 
//             {i => intp_in_pvld & info_in_rdy & xinterp_in_rdy(i)})
//         )

//     val xinterp_out_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     val xinterp_out_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
//     val xinterp_out_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))

//     val u_interp_X = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new NV_NVDLA_CDP_DP_INTP_unit)}
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         u_interp_X(i).io.nvdla_core_clk := io.nvdla_core_clk
//         u_interp_X(i).io.interp_in0_pd := xinterp_in0_pd(i)
//         u_interp_X(i).io.interp_in1_pd := xinterp_in1_pd(i)
//         u_interp_X(i).io.interp_in_pd := xinterp_in_pd(i)
//         u_interp_X(i).io.interp_in_scale := xinterp_in_scale(i)
//         u_interp_X(i).io.interp_in_shift := xinterp_in_shift(i)
//         u_interp_X(i).io.interp_in_vld := xinterp_in_vld(i)
//         u_interp_X(i).io.interp_out_rdy := xinterp_out_rdy(i)
//         xinterp_in_rdy(i) := u_interp_X(i).io.interp_in_rdy
//         xinterp_out_pd(i) := u_interp_X(i).io.interp_out_pd
//         xinterp_out_vld(i) := u_interp_X(i).io.interp_out_vld
//     }

//     val intp_prdy = Wire(Bool())
//     val info_o_vld = Wire(Bool())
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         xinterp_out_rdy(i) := intp_prdy & info_o_vld & xinterp_out_vld(i)
//     }

//     val info_o_rdy = intp_prdy & (xinterp_out_vld.asUInt.andR)
    
//     ///////////////////////////////////////////////
//     //process for normal uflow/oflow info
//     val info_in_vld = intp_in_pvld & (xinterp_in_rdy.asUInt.andR);


// //      NVDLA_CDP_THROUGHPUT = 8

//     val info_Xin_pd = Cat(
//         lut2ip_X_info(7)(17,16),
//         lut2ip_X_info(6)(17,16),
//         lut2ip_X_info(5)(17,16),
//         lut2ip_X_info(4)(17,16),
//         lut2ip_X_info(3)(17,16),
//         lut2ip_X_info(2)(17,16),
//         lut2ip_X_info(1)(17,16),
//         lut2ip_X_info(0)(17,16)
//     )

//     val info_Yin_pd = Cat(
//         lut2ip_Y_info(7)(19,18),
//         lut2ip_Y_info(6)(19,18),
//         lut2ip_Y_info(5)(19,18),
//         lut2ip_Y_info(4)(19,18),
//         lut2ip_Y_info(3)(19,18),
//         lut2ip_Y_info(2)(19,18),
//         lut2ip_Y_info(1)(19,18),
//         lut2ip_Y_info(0)(19,18)
//     )

//     val dat_info_in = Cat(info_Xin_pd, info_Yin_pd)
//     val info_in_pd = dat_info_in

//     //////////////////////////
//     //// THERE IS A FIFO 
//     //////////////////////////

//     val x_info = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map 
//                             {i => info_o_pd(i*2+1,i*2)})
//     val y_info = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map 
//                             {i => info_o_pd((conf.NVDLA_CDP_THROUGHPUT*2+i*2+1),(conf.NVDLA_CDP_THROUGHPUT*2+i*2))})

// ////////////////////////////////////////////////
//     val intp_pvld = info_o_vld & (xinterp_out_vld.asUInt.andR)
//     val intp_pvld_d = RegInit(false.B)
//     val intp_prdy_d = Wire(Bool())
//     intp_prdy = ~intp_pvld_d | intp_prdy_d
// ////////

//     when(intp_pvld){
//         intp_pvld_d := true.B
//     }.elsewhen(intp_prdy_d){
//         intp_pvld_d := false.B
//     }

//     val ip2mul_pvld = intp_pvld_d

//     val ip2mul_pd = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(17.W))))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(intp_pvld & intp_prdy){
//             ip2mul_pd(i) := xinterp_out_pd(i)
//         }
//     }

// ////////////////////////////////////////////////
// //LUT perf counters
// ////////////////////////////////////////////////

//     val layer_done = io.dp2reg_done

//     val both_hybrid_flag = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
//     val both_of_flag = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
//     val both_uf_flag = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
//     val only_le_hit = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
//     val only_lo_hit = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(intp_pvld & intp_prdy){
//             both_hybrid_flag(i) :=  (Cat(x_info(i),y_info(i)) === "b0000".asUInt(4.W)) | 
//                                     (Cat(x_info(i),y_info(i)) === "b0110".asUInt(4.W)) | 
//                                     (Cat(x_info(i),y_info(i)) === "b1001".asUInt(4.W))
//             both_of_flag(i) := (Cat(x_info(i),y_info(i)) === "b1010".asUInt(4.W))
//             both_uf_flag(i) := (Cat(x_info(i),y_info(i)) === "b0101".asUInt(4.W))
//             only_le_hit(i)  :=  (Cat(x_info(i),y_info(i)) === "b0001".asUInt(4.W)) | 
//                                 (Cat(x_info(i),y_info(i)) === "b0010".asUInt(4.W))
//             only_lo_hit(i)  :=  (Cat(x_info(i),y_info(i)) === "b0100".asUInt(4.W)) | 
//                                 (Cat(x_info(i),y_info(i)) === "b1000".asUInt(4.W))
//         }
//     }

// // function [3:0] fun_bit_sum_8;
// //   input [7:0] idata;
// //   reg [3:0] ocnt;
// //   begin
// //     ocnt =
// //         (( idata[0]  
// //       +  idata[1]  
// //       +  idata[2] ) 
// //       + ( idata[3]  
// //       +  idata[4]  
// //       +  idata[5] )) 
// //       + ( idata[6]  
// //       +  idata[7] ) ;
// //     fun_bit_sum_8 = ocnt;
// //   end
// // endfunction

// //: my $tp = NVDLA_CDP_THROUGHPUT;
// //: if($tp ==8) {
// //: print qq(
// //:     assign both_hybrid_ele = fun_bit_sum_8({both_hybrid_flag});
// //:     assign both_of_ele     = fun_bit_sum_8({both_of_flag});
// //:     assign both_uf_ele     = fun_bit_sum_8({both_uf_flag});
// //:     assign only_le_hit_ele = fun_bit_sum_8({only_le_hit});
// //:     assign only_lo_hit_ele = fun_bit_sum_8({only_lo_hit});
// //: );
// //: } else {
// //: print qq(
// //:     assign both_hybrid_ele = fun_bit_sum_8({{(8-${tp}){1'b0}},both_hybrid_flag});
// //:     assign both_of_ele     = fun_bit_sum_8({{(8-${tp}){1'b0}},both_of_flag});
// //:     assign both_uf_ele     = fun_bit_sum_8({{(8-${tp}){1'b0}},both_uf_flag});
// //:     assign only_le_hit_ele = fun_bit_sum_8({{(8-${tp}){1'b0}},only_le_hit});
// //:     assign only_lo_hit_ele = fun_bit_sum_8({{(8-${tp}){1'b0}},only_lo_hit});
// //: );
// //: }

// //assign both_hybrid_ele = fun_bit_sum_8({{(8-NVDLA_CDP_THROUGHPUT){1'b0}},both_hybrid_flag});
// //assign both_of_ele     = fun_bit_sum_8({{(8-NVDLA_CDP_THROUGHPUT){1'b0}},both_of_flag});
// //assign both_uf_ele     = fun_bit_sum_8({{(8-NVDLA_CDP_THROUGHPUT){1'b0}},both_uf_flag});
// //assign only_le_hit_ele = fun_bit_sum_8({{(8-NVDLA_CDP_THROUGHPUT){1'b0}},only_le_hit});
// //assign only_lo_hit_ele = fun_bit_sum_8({{(8-NVDLA_CDP_THROUGHPUT){1'b0}},only_lo_hit});

//     val both_hybrid_counter = RegInit(0.U(32.W))
//     val both_of_counter = RegInit(0.U(32.W))
//     val both_uf_counter = RegInit(0.U(32.W))
//     val only_le_hit_counter = RegInit(0.U(32.W))
//     val only_lo_hit_counter = RegInit(0.U(32.W))
//     when(layer_done){
//         both_hybrid_counter := 0.U
//         both_of_counter := 0.U
//         both_uf_counter := 0.U
//         only_le_hit_counter := 0.U
//         only_lo_hit_counter := 0.U
//     }.elsewhen(intp_pvld_d & intp_prdy_d){

//     }


// }}


// object NV_NVDLA_CDP_DP_intpDriver extends App {
//     implicit val conf: cdpConfiguration = new cdpConfiguration
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_intp())
// }

