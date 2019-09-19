// package nvdla

// import chisel3._
// import chisel3.util._
// import chisel3.experimental._


// class NV_NVDLA_SDP_CORE_Y_lut(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//     //general clock
//     val nvdla_core_clk = Input(Clock())

//     //lut2inp interface  
//     val lut2inp_pd = DecoupleIO(UInt(conf.EW_LUT_OUT_DW.W))

//     //idx2lut interface  
//     val idx2lut_pd = Flipped(DecoupleIO(UInt(conf.EW_LUT_OUT_DW.W)))
  
//     //reg2dp interface
//     val reg2dp_lut_int_access_type = Input(Bool())
//     val reg2dp_lut_int_addr = Input(UInt(10.W))
//     val reg2dp_lut_int_data = Input(UInt(16.W))
//     val reg2dp_lut_int_data_wr = Input(Bool())
//     val reg2dp_lut_int_table_id = Input(Bool())
//     val reg2dp_lut_le_end = Input(UInt(32.W))
//     val reg2dp_lut_le_function = Input(Bool())
//     val reg2dp_lut_le_index_offset = Input(UInt(8.W))
//     val reg2dp_lut_le_slope_oflow_scale = Input(UInt(16.W))
//     val reg2dp_lut_le_slope_oflow_shift = Input(UInt(5.W))
//     val reg2dp_lut_le_slope_uflow_scale = Input(UInt(16.W))
//     val reg2dp_lut_le_slope_uflow_shift = Input(UInt(5.W))
//     val reg2dp_lut_le_start = Input(UInt(32.W))
//     val reg2dp_lut_lo_end = Input(UInt(32.W))
//     val reg2dp_lut_lo_slope_oflow_scale = Input(UInt(16.W))
//     val reg2dp_lut_lo_slope_oflow_shift = Input(UInt(5.W))
//     val reg2dp_lut_lo_slope_uflow_scale = Input(UInt(16.W))
//     val reg2dp_lut_lo_slope_uflow_shift = Input(UInt(5.W))
//     val reg2dp_lut_lo_start = Input(UInt(32.W))
//     val reg2dp_perf_lut_en = Input(Bool())
//     val reg2dp_proc_precision = Input(UInt(2.W))

//     val dp2reg_lut_hybrid = Output(UInt(32.W))
//     val dp2reg_lut_int_data = Output(UInt(16.W))
//     val dp2reg_lut_le_hit = Output(UInt(32.W))
//     val dp2reg_lut_lo_hit = Output(UInt(32.W))
//     val dp2reg_lut_oflow = Output(UInt(32.W))
//     val dp2reg_lut_uflow = Output(UInt(32.W))

//     val pwrbus_ram_pd = Input(UInt(32.W))
//     val op_en_load = Input(Bool())
//   })

// withClock(io.nvdla_core_clk){
// //==============
// // Reg Configure
// //==============
// // get the width of all regs
// //=======================================

// //===========================================
// // LUT Programing
// //===========================================
// val lut_addr = io.reg2dp_lut_int_addr(9, 0)
// val lut_data = io.reg2dp_lut_int_data(15, 0)
// val lut_table_id = io.reg2dp_lut_int_table_id
// val lut_access_type = io.reg2dp_lut_int_access_type

// val lut_pd = Cat(lut_access_type,lut_table_id,lut_data,lut_addr)
// val pro2lut_valid = io.reg2dp_lut_int_data_wr
// val pro2lut_pd   = lut_pd

// val pro_in_addr = pro2lut_pd(9, 0)
// val pro_in_data = pro2lut_pd(25, 10)
// val pro_in_table_id = pro2lut_pd(26)
// val pro_in_wr = pro2lut_pd(27)
// val pro_in_wr_en = pro2lut_valid & (pro_in_wr === true.B )

// val pro_in_select_le = pro_in_table_id === false.B
// val pro_in_select_lo = pro_in_table_id === true.B


// val reg_le = Seq.fill(conf.LUT_TABLE_LE_DEPTH)(RegInit("b0".asUInt(16.W)))     
// val reg_lo = Seq.fill(conf.LUT_TABLE_LO_DEPTH)(RegInit("b0".asUInt(16.W)))
// //===========================================
// // READ LUT
// val le_lut_data = MuxLookup(pro_in_addr, "b0".asUInt(16.W),
//                   (0 to conf.LUT_TABLE_LE_DEPTH-1) map { i => i.U -> reg_le(i) })

// val lo_lut_data = MuxLookup(pro_in_addr, "b0".asUInt(16.W),
//                   (0 to conf.LUT_TABLE_LO_DEPTH-1) map { i => i.U -> reg_lo(i) })

// io.dp2reg_lut_int_data := Mux(pro_in_select_le, le_lut_data, lo_lut_data)
// //=======================================
// // WRITE LUT
// //=======================================
// val le_wr_en = Wire(Vec(conf.LUT_TABLE_LE_DEPTH, Bool()))
// for(i <- 0 to conf.LUT_TABLE_LE_DEPTH -1){
//     le_wr_en(i) := pro_in_wr_en & pro_in_select_le & (pro_in_addr === i.U)
//     when(le_wr_en(i)){
//         reg_le(i) := pro_in_data
//     }
// }


// val lo_wr_en = Wire(Vec(conf.LUT_TABLE_LO_DEPTH, Bool()))
// for(i <- 0 to conf.LUT_TABLE_LO_DEPTH -1){
//     lo_wr_en(i) := pro_in_wr_en & pro_in_select_lo & (pro_in_addr === i.U)
//     when(lo_wr_en(i)){
//         reg_lo(i) := pro_in_data
//     }
// }


// val lut_in_prdy = Wire(Bool())
// val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.EW_IDX_OUT_DW))
// pipe_p1.io.clk := io.nvdla_core_clk
// pipe_p1.io.ri := lut_in_prdy
// pipe_p1.io.vi := io.idx2lut_pvld
// pipe_p1.io.di := io.idx2lut_pd
// io.idx2lut_prdy := pipe_p1.io.ro
// val lut_in_pvld = pipe_p1.io.vo
// val lut_in_pd = pipe_p1.io.dout

// val bx = conf.NVDLA_SDP_EW_THROUGHPUT*35
// val bof = conf.NVDLA_SDP_EW_THROUGHPUT*(35 + 32)
// val buf = conf.NVDLA_SDP_EW_THROUGHPUT*(35 + 32 + 1)
// val bsl = conf.NVDLA_SDP_EW_THROUGHPUT*(35 + 32 + 2)
// val ba = conf.NVDLA_SDP_EW_THROUGHPUT*(35 + 32 + 3)
// val beh = conf.NVDLA_SDP_EW_THROUGHPUT*(35 + 32 + 12)
// val boh = conf.NVDLA_SDP_EW_THROUGHPUT*(35 + 32 + 13)

// val lut_in_fraction = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_pd(35*i+34, 35*i)}) 
// val lut_in_x = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_pd(32*i+31+bx, 32*i+bx)})
// val lut_in_oflow = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_pd(i+bof).asUInt})
// val lut_in_uflow = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_pd(i+buf).asUInt})
// val lut_in_sel = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_pd(i+bsl).asUInt})
// val lut_in_addr = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_pd(9*i+8+ba, 9*i+ba)})
// val lut_in_le_hit = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_pd(i+beh).asUInt})
// val lut_in_lo_hit = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_pd(i+boh).asUInt})

// //=======================================
// // PERF STATISTIC
// // OFLOW
// //=======================================
// val lut_oflow_sum = lut_in_oflow.reduce(_+&_)
// val lut_oflow_cnt = Wire(UInt(32.W))

// val perf_lut_oflow_add = Mux(lut_oflow_cnt.andR, "b0".asUInt(5.W), lut_oflow_sum)
// val perf_lut_oflow_sub = false.B
// io.dp2reg_lut_oflow := lut_oflow_cnt

// val perf_lut_oflow_adv = perf_lut_oflow_add =/= perf_lut_oflow_sub

// // cnt logic
// val perf_lut_oflow_cnt_cur = RegInit("b0".asUInt(32.W))
// val perf_lut_oflow_cnt_ext = Wire(UInt(34.W))
// val perf_lut_oflow_cnt_mod = Wire(UInt(34.W))
// val perf_lut_oflow_cnt_new = Wire(UInt(34.W))
// val perf_lut_oflow_cnt_nxt = Wire(UInt(34.W))

// perf_lut_oflow_cnt_ext := perf_lut_oflow_cnt_cur
// perf_lut_oflow_cnt_mod := perf_lut_oflow_cnt_cur +& perf_lut_oflow_add -& perf_lut_oflow_sub
// perf_lut_oflow_cnt_new := Mux(perf_lut_oflow_adv, perf_lut_oflow_cnt_mod, perf_lut_oflow_cnt_ext)
// perf_lut_oflow_cnt_nxt := Mux(io.op_en_load, "b0".asUInt(34.W), perf_lut_oflow_cnt_new)

// when(io.reg2dp_perf_lut_en){
//     perf_lut_oflow_cnt_cur := perf_lut_oflow_cnt_nxt(31, 0)
// }

// lut_oflow_cnt := perf_lut_oflow_cnt_cur

// //=======================================
// // PERF STATISTIC
// // UFLOW
// //=======================================
// val lut_uflow_sum = lut_in_uflow.reduce(_+&_)
// val lut_uflow_cnt = Wire(UInt(32.W))

// val perf_lut_uflow_add = Mux(lut_uflow_cnt.andR, "b0".asUInt(5.W), lut_uflow_sum)
// val perf_lut_uflow_sub = false.B
// io.dp2reg_lut_uflow := lut_uflow_cnt

// val perf_lut_uflow_adv = perf_lut_uflow_add =/= perf_lut_uflow_sub

// // cnt logic
// val perf_lut_uflow_cnt_cur = RegInit("b0".asUInt(32.W))
// val perf_lut_uflow_cnt_ext = Wire(UInt(34.W))
// val perf_lut_uflow_cnt_mod = Wire(UInt(34.W))
// val perf_lut_uflow_cnt_new = Wire(UInt(34.W))
// val perf_lut_uflow_cnt_nxt = Wire(UInt(34.W))

// perf_lut_uflow_cnt_ext := perf_lut_uflow_cnt_cur
// perf_lut_uflow_cnt_mod := perf_lut_uflow_cnt_cur +& perf_lut_uflow_add -& perf_lut_uflow_sub
// perf_lut_uflow_cnt_new := Mux(perf_lut_uflow_adv, perf_lut_uflow_cnt_mod, perf_lut_uflow_cnt_ext)
// perf_lut_uflow_cnt_nxt := Mux(io.op_en_load, "b0".asUInt(34.W), perf_lut_uflow_cnt_new)

// when(io.reg2dp_perf_lut_en){
//     perf_lut_uflow_cnt_cur := perf_lut_uflow_cnt_nxt(31, 0)
// }

// lut_uflow_cnt := perf_lut_uflow_cnt_cur

// //=======================================
// // PERF STATISTIC
// // HYBRID
// //=======================================
// val lut_in_hybrid = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => Cat(!(lut_in_oflow(i)|lut_in_uflow(i)))})
// val lut_hybrid_sum = lut_in_hybrid.reduce(_+&_)
// val lut_hybrid_cnt = Wire(UInt(32.W))

// val perf_lut_hybrid_add = Mux(lut_hybrid_cnt.andR, "b0".asUInt(5.W), lut_hybrid_sum)
// val perf_lut_hybrid_sub = false.B
// io.dp2reg_lut_hybrid := lut_hybrid_cnt

// val perf_lut_hybrid_adv = perf_lut_hybrid_add =/= perf_lut_hybrid_sub

// // cnt logic
// val perf_lut_hybrid_cnt_cur = RegInit("b0".asUInt(32.W))
// val perf_lut_hybrid_cnt_ext = Wire(UInt(34.W))
// val perf_lut_hybrid_cnt_mod = Wire(UInt(34.W))
// val perf_lut_hybrid_cnt_new = Wire(UInt(34.W))
// val perf_lut_hybrid_cnt_nxt = Wire(UInt(34.W))

// perf_lut_hybrid_cnt_ext := perf_lut_hybrid_cnt_cur
// perf_lut_hybrid_cnt_mod := perf_lut_hybrid_cnt_cur +& perf_lut_hybrid_add -& perf_lut_hybrid_sub
// perf_lut_hybrid_cnt_new := Mux(perf_lut_hybrid_adv, perf_lut_hybrid_cnt_mod, perf_lut_hybrid_cnt_ext)
// perf_lut_hybrid_cnt_nxt := Mux(io.op_en_load, "b0".asUInt(34.W), perf_lut_hybrid_cnt_new)

// when(io.reg2dp_perf_lut_en){
//     perf_lut_hybrid_cnt_cur := perf_lut_hybrid_cnt_nxt(31, 0)
// }

// lut_hybrid_cnt := perf_lut_hybrid_cnt_cur

// //=======================================
// // PERF STATISTIC
// // LE_HIT
// //=======================================
// val lut_le_hit_sum = lut_in_le_hit.reduce(_+&_)
// val lut_le_hit_cnt = Wire(UInt(32.W))

// val perf_lut_le_hit_add = Mux(lut_le_hit_cnt.andR, "b0".asUInt(5.W), lut_le_hit_sum)
// val perf_lut_le_hit_sub = false.B
// io.dp2reg_lut_le_hit := lut_le_hit_cnt

// val perf_lut_le_hit_adv = perf_lut_le_hit_add =/= perf_lut_le_hit_sub

// // cnt logic
// val perf_lut_le_hit_cnt_cur = RegInit("b0".asUInt(32.W))
// val perf_lut_le_hit_cnt_ext = Wire(UInt(34.W))
// val perf_lut_le_hit_cnt_mod = Wire(UInt(34.W))
// val perf_lut_le_hit_cnt_new = Wire(UInt(34.W))
// val perf_lut_le_hit_cnt_nxt = Wire(UInt(34.W))

// perf_lut_le_hit_cnt_ext := perf_lut_le_hit_cnt_cur
// perf_lut_le_hit_cnt_mod := perf_lut_le_hit_cnt_cur +& perf_lut_le_hit_add -& perf_lut_le_hit_sub
// perf_lut_le_hit_cnt_new := Mux(perf_lut_le_hit_adv, perf_lut_le_hit_cnt_mod, perf_lut_le_hit_cnt_ext)
// perf_lut_le_hit_cnt_nxt := Mux(io.op_en_load, "b0".asUInt(34.W), perf_lut_le_hit_cnt_new)

// when(io.reg2dp_perf_lut_en){
//     perf_lut_le_hit_cnt_cur := perf_lut_le_hit_cnt_nxt(31, 0)
// }

// lut_le_hit_cnt := perf_lut_le_hit_cnt_cur

// //=======================================
// // PERF STATISTIC
// // LO_HIT
// //=======================================
// val lut_lo_hit_sum = lut_in_lo_hit.reduce(_+&_)
// val lut_lo_hit_cnt = Wire(UInt(32.W))

// val perf_lut_lo_hit_add = Mux(lut_lo_hit_cnt.andR, "b0".asUInt(5.W), lut_lo_hit_sum)
// val perf_lut_lo_hit_sub = false.B
// io.dp2reg_lut_lo_hit := lut_lo_hit_cnt

// val perf_lut_lo_hit_adv = perf_lut_lo_hit_add =/= perf_lut_lo_hit_sub

// // cnt logic
// val perf_lut_lo_hit_cnt_cur = RegInit("b0".asUInt(32.W))
// val perf_lut_lo_hit_cnt_ext = Wire(UInt(34.W))
// val perf_lut_lo_hit_cnt_mod = Wire(UInt(34.W))
// val perf_lut_lo_hit_cnt_new = Wire(UInt(34.W))
// val perf_lut_lo_hit_cnt_nxt = Wire(UInt(34.W))

// perf_lut_lo_hit_cnt_ext := perf_lut_lo_hit_cnt_cur
// perf_lut_lo_hit_cnt_mod := perf_lut_lo_hit_cnt_cur +& perf_lut_lo_hit_add -& perf_lut_lo_hit_sub
// perf_lut_lo_hit_cnt_new := Mux(perf_lut_lo_hit_adv, perf_lut_lo_hit_cnt_mod, perf_lut_lo_hit_cnt_ext)
// perf_lut_lo_hit_cnt_nxt := Mux(io.op_en_load, "b0".asUInt(34.W), perf_lut_lo_hit_cnt_new)

// when(io.reg2dp_perf_lut_en){
//     perf_lut_lo_hit_cnt_cur := perf_lut_lo_hit_cnt_nxt(31, 0)
// }

// lut_lo_hit_cnt := perf_lut_lo_hit_cnt_cur

// //=======================================
// // rd addr mux 
// //=======================================
// val lut_in_addr_0 = lut_in_addr
// val lut_in_addr_1 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_addr(i)+1.U})

// val le_data0 = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(16.W)))
// val le_data1 = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(16.W)))

// val lo_data0 = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(16.W)))
// val lo_data1 = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(16.W)))

// val dat_in_y0 = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(16.W)))
// val dat_in_y1 = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(16.W)))

// for(j <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){
//     le_data0(j) := MuxLookup(lut_in_addr_0(j), "b0".asUInt(16.W),
//                    (0 to conf.LUT_TABLE_LE_DEPTH-1) map { i => i.U -> reg_le(i) })

//     le_data1(j) := MuxLookup(lut_in_addr_1(j), "b0".asUInt(16.W),
//                    (0 to conf.LUT_TABLE_LE_DEPTH-1) map { i => i.U -> reg_le(i) })

//     lo_data0(j) := MuxLookup(lut_in_addr_0(j), "b0".asUInt(16.W),
//                    (0 to conf.LUT_TABLE_MAX_DEPTH-1) map { i => i.U -> reg_lo(i) })

//     lo_data1(j) := MuxLookup(lut_in_addr_1(j), "b0".asUInt(16.W),
//                    (0 to conf.LUT_TABLE_MAX_DEPTH-1) map { i => i.U -> reg_lo(i) })   

//     dat_in_y0(j) := Mux(lut_in_sel(j) === false.B, le_data0(j), lo_data0(j))
//     dat_in_y1(j) := Mux(lut_in_sel(j) === false.B, le_data1(j), lo_data1(j))
// }

// //=======================================
// // dat fifo wr
// //=======================================
// val rd_lut_en = lut_in_pvld & lut_in_prdy
// val dat_fifo_wr_pvld = rd_lut_en

// // PKT_PACK_WIRE( sdp_y_lut_dat ,  dat_in_ ,  dat_fifo_wr_pd )

// val dat_fifo_wr_pd_0 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => dat_in_y0(i)}).asUInt
// val dat_fifo_wr_pd_1 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => dat_in_y1(i)}).asUInt
// val dat_fifo_wr_pd = Cat(dat_fifo_wr_pd_1, dat_fifo_wr_pd_0)

// val out_y0 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => dat_fifo_wr_pd_0(16*i+15, 16*i)})
// val out_y1 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => dat_fifo_wr_pd_1(16*i+15, 16*i)})

// val dat_fifo_rd_prdy = Wire(Bool())
// val lut_out_prdy = Wire(Bool())
// val u_dat = Module(new NV_NVDLA_SDP_CORE_Y_LUT_dat(128))
// u_dat.io.nvdla_core_clk := io.nvdla_core_clk
// u_dat.io.dat_fifo_wr_pvld := dat_fifo_wr_pvld
// u_dat.io.dat_fifo_wr_pd := dat_fifo_wr_pd(32*conf.NVDLA_SDP_EW_THROUGHPUT-1, 0)
// u_dat.io.dat_fifo_rd_prdy := dat_fifo_rd_prdy
// val dat_fifo_rd_pvld = u_dat.io.dat_fifo_rd_pvld
// val dat_fifo_rd_pd = u_dat.io.dat_fifo_rd_pd
// u_dat.io.pwrbus_ram_pd := io.pwrbus_ram_pd

// // dat fifo rd
// dat_fifo_rd_prdy := lut_out_prdy;

// //============
// // cmd fifo wr:

// val cmd_fifo_wr_pd_0 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_fraction(i)}).asUInt
// val cmd_fifo_wr_pd_1 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => lut_in_x(i)}).asUInt
// val cmd_fifo_wr_pd_2 = lut_in_oflow.asUInt
// val cmd_fifo_wr_pd_3 = lut_in_uflow.asUInt
// val cmd_fifo_wr_pd_4 = lut_in_sel.asUInt
// val cmd_fifo_wr_pd = Cat(cmd_fifo_wr_pd_4, cmd_fifo_wr_pd_3, cmd_fifo_wr_pd_2, cmd_fifo_wr_pd_1, cmd_fifo_wr_pd_0)

// val out_fraction = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(35.W)))
// val out_x = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
// val out_oflow = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
// val out_uflow = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
// val out_sel = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))

// val cmd_fifo_rd_pd = Wire(UInt((70*conf.NVDLA_SDP_EW_THROUGHPUT).W))
// for(i <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){
//     out_fraction(i) := cmd_fifo_rd_pd(35*i+34, 35*i)
//     out_x(i) := cmd_fifo_rd_pd(32*i+31+conf.NVDLA_SDP_EW_THROUGHPUT*35, 32*i+conf.NVDLA_SDP_EW_THROUGHPUT*35)
//     out_oflow(i) := cmd_fifo_rd_pd(i+conf.NVDLA_SDP_EW_THROUGHPUT*(35+32))
//     out_uflow(i) := cmd_fifo_rd_pd(i+conf.NVDLA_SDP_EW_THROUGHPUT*(35+32+1))
//     out_sel(i) := cmd_fifo_rd_pd(i+conf.NVDLA_SDP_EW_THROUGHPUT*(35+32+2))
// }

// val cmd_fifo_wr_prdy = Wire(Bool())
// val cmd_fifo_rd_prdy = Wire(Bool())
// val cmd_fifo_wr_pvld = lut_in_pvld
// lut_in_prdy := cmd_fifo_wr_prdy

// // cmd fifo inst:

// val u_cmd = Module{new NV_NVDLA_SDP_CORE_Y_LUT_cmd(70*conf.NVDLA_SDP_EW_THROUGHPUT)}
// u_cmd.io.nvdla_core_clk := io.nvdla_core_clk
// cmd_fifo_wr_prdy := u_cmd.io.cmd_fifo_wr_prdy
// u_cmd.io.cmd_fifo_wr_pvld := cmd_fifo_wr_pvld
// u_cmd.io.cmd_fifo_wr_pd := cmd_fifo_wr_pd
// u_cmd.io.cmd_fifo_rd_prdy := cmd_fifo_rd_prdy
// val cmd_fifo_rd_pvld = u_cmd.io.cmd_fifo_rd_pvld
// cmd_fifo_rd_pd := u_cmd.io.cmd_fifo_rd_pd
// u_cmd.io.pwrbus_ram_pd := io.pwrbus_ram_pd

// // cmd fifo rd:

// cmd_fifo_rd_prdy := lut_out_prdy & dat_fifo_rd_pvld;

// //=======================================
// // output mux when oflow/uflow

// val out_flow =  VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_uflow(i)|out_oflow(i)})

// val out_scale = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(16.W)))
// val out_shift = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(5.W)))
// val out_offset = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
// val out_bias = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))

// for (i <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){
//     when(out_uflow(i)){
//         when(!out_sel(i)){
//             out_scale(i) := io.reg2dp_lut_le_slope_uflow_scale
//             out_shift(i) := io.reg2dp_lut_le_slope_uflow_shift
//             out_offset(i) := io.reg2dp_lut_le_start
//             when(!io.reg2dp_lut_le_function){
//                 out_bias(i) := Mux(io.reg2dp_lut_le_index_offset(7), 0.U, 1.U << io.reg2dp_lut_le_index_offset)
//             }
//             .otherwise{
//                 out_bias(i) := 0.U
//             }
//         }
//         .otherwise{
//             out_scale(i) := io.reg2dp_lut_lo_slope_uflow_scale
//             out_shift(i) := io.reg2dp_lut_lo_slope_uflow_shift
//             out_offset(i) := io.reg2dp_lut_lo_start
//             out_bias(i) := 0.U
//         }
//     }
//     .elsewhen(out_oflow(i)){
//         when(!out_sel(i)){
//             out_scale(i) := io.reg2dp_lut_le_slope_oflow_scale
//             out_shift(i) := io.reg2dp_lut_le_slope_oflow_shift
//             out_offset(i) := io.reg2dp_lut_le_end
//             out_bias(i) := 0.U
//         }
//         .otherwise{
//             out_scale(i) := io.reg2dp_lut_lo_slope_oflow_scale
//             out_shift(i) := io.reg2dp_lut_lo_slope_oflow_shift
//             out_offset(i) := io.reg2dp_lut_lo_end
//             out_bias(i) := 0.U           
//         }
//     }
//     .otherwise{
//         out_scale(i) := 0.U
//         out_shift(i) := 0.U
//         out_offset(i) := 0.U
//         out_bias(i) := 0.U            
//     }
// }

// //=======================================
// // output pipe

// val lut_out_pvld = dat_fifo_rd_pvld

// val lut_out_pd_0 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_x(i)}).asUInt
// val lut_out_pd_1 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_fraction(i)}).asUInt
// val lut_out_pd_2 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_y0(i)}).asUInt
// val lut_out_pd_3 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_y1(i)}).asUInt
// val lut_out_pd_4 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_scale(i)}).asUInt
// val lut_out_pd_5 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_shift(i)}).asUInt
// val lut_out_pd_6 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_offset(i)}).asUInt
// val lut_out_pd_7 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_bias(i)}).asUInt
// val lut_out_pd_8 = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map { i => out_flow(i)}).asUInt

// val lut_out_pd = Cat(lut_out_pd_8, lut_out_pd_7, lut_out_pd_6, lut_out_pd_5, lut_out_pd_4,
//                      lut_out_pd_3, lut_out_pd_2, lut_out_pd_1, lut_out_pd_0)

// val pipe_p2 = Module(new NV_NVDLA_IS_pipe(conf.EW_LUT_OUT_DW))
// pipe_p2.io.clk := io.nvdla_core_clk
// pipe_p2.io.vi := lut_out_pvld
// lut_out_prdy := pipe_p2.io.ro
// pipe_p2.io.di := lut_out_pd
// io.lut2inp_pvld := pipe_p2.io.vo
// pipe_p2.io.ri := io.lut2inp_prdy
// io.lut2inp_pd := pipe_p2.io.dout
// }}




// class NV_NVDLA_SDP_CORE_Y_LUT_dat(width:Int) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val dat_fifo_wr_pvld = Input(Bool())
//         val dat_fifo_wr_pd = Input(UInt(width.W))
//         val dat_fifo_rd_prdy = Input(Bool())
//         val dat_fifo_rd_pvld = Output(Bool())
//         val dat_fifo_rd_pd = Output(UInt(width.W))

//         val pwrbus_ram_pd = Input(UInt(32.W))
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
//     withClock(io.nvdla_core_clk){
//     // Master Clock Gating (SLCG)
//     //
//     // We gate the clock(s) when idle or stalled.
//     // This allows us to turn off numerous miscellaneous flops
//     // that don't get gated during synthesis for one reason or another.
//     //
//     // We gate write side and read side separately. 
//     // If the fifo is synchronous, we also gate the ram separately, but if
//     // -master_clk_gated_unified or -status_reg/-status_logic_reg is specified, 
//     // then we use one clk gate for write, ram, and read.
//     //
//     val nvdla_core_clk_mgated_enable = Wire(Bool())
//     val nvdla_core_clk_mgate = Module(new NV_CLK_gate_power)
//     nvdla_core_clk_mgate.io.clk := io.nvdla_core_clk
//     nvdla_core_clk_mgate.io.clk_en := nvdla_core_clk_mgated_enable
//     val nvdla_core_clk_mgated = nvdla_core_clk_mgate.io.clk_gated

//     ////////////////////////////////////////////////////////////////////////
//     // WRITE SIDE                                                        //
//     ////////////////////////////////////////////////////////////////////////
//     val wr_reserving = io.dat_fifo_wr_pvld

//     val wr_popping = Wire(Bool())      // fwd: write side sees pop?
//     val dat_fifo_wr_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))} // write-side count

//     val wr_count_next_wr_popping = Mux(wr_reserving, dat_fifo_wr_count, dat_fifo_wr_count-1.U)
//     val wr_count_next_no_wr_popping = Mux(wr_reserving, dat_fifo_wr_count+1.U, dat_fifo_wr_count)
//     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

//     when(wr_reserving ^ wr_popping){
//         dat_fifo_wr_count := wr_count_next
//     }

//     val wr_pushing = wr_reserving  // data pushed same cycle as wr_pvld

//     //
//     // RAM
//     //  

//     val dat_fifo_wr_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(1.W))}   // current write address
//     when(wr_pushing){
//         dat_fifo_wr_adr := dat_fifo_wr_adr + 1.U
//     }
//     val rd_popping = Wire(Bool())

//     val dat_fifo_rd_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(1.W))}   // read address this cycle
//     val ram_we = wr_pushing && (dat_fifo_wr_count > 0.U || !rd_popping);   // note: write occurs next cycle
//     val dat_fifo_rd_pd_p = Wire(UInt(32.W))

//     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
//     // Fifogen handles this by ignoring the data on the ram data out for that cycle.

//     val ram = Module(new nv_flopram(2, 32))
//     ram.io.clk := nvdla_core_clk_mgated
//     ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ram.io.di := io.dat_fifo_wr_pd
//     ram.io.we := ram_we
//     ram.io.wa := dat_fifo_wr_adr
//     ram.io.ra := Mux(dat_fifo_wr_count === 0.U, "d2".asUInt(2.W), dat_fifo_rd_adr)
//     dat_fifo_rd_pd_p := ram.io.dout

//     val rd_adr_next_popping = dat_fifo_rd_adr + 1.U
//     when(rd_popping){
//         dat_fifo_rd_adr := rd_adr_next_popping
//     }
    
//     //
//     // SYNCHRONOUS BOUNDARY
//     //
//     wr_popping := rd_popping    
//     val rd_pushing = withClock(nvdla_core_clk_mgated){RegNext(wr_pushing, false.B)} 

//     //
//     // READ SIDE
//     //
//     val dat_fifo_rd_pvld_p = Wire(Bool())  // data out of fifo is valid
//     val dat_fifo_rd_pvld_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)} // internal copy of rd_req
//     io.dat_fifo_rd_pvld := dat_fifo_rd_pvld_int
//     rd_popping := dat_fifo_rd_pvld_p && !(dat_fifo_rd_pvld_int && !io.dat_fifo_rd_prdy)

//     val dat_fifo_rd_count_p = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))} //read-side fifo count
//     val rd_count_p_next_rd_popping = Mux(rd_pushing, dat_fifo_rd_count_p, dat_fifo_rd_count_p-1.U)
//     val rd_count_p_next_no_rd_popping = Mux(rd_pushing, dat_fifo_rd_count_p + 1.U, dat_fifo_rd_count_p)
//     val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)

//     dat_fifo_rd_pvld_p := (dat_fifo_rd_count_p =/= 0.U) || rd_pushing
  
//     when(rd_pushing || rd_popping){
//         dat_fifo_rd_count_p := rd_count_p_next
//     }

//     val dat_fifo_rd_pd_out = withClock(nvdla_core_clk_mgated){Reg(UInt(width.W))}    // output data register
//     val rd_req_next = (dat_fifo_rd_pvld_p || (dat_fifo_rd_pvld_int && !io.dat_fifo_rd_prdy))

//     dat_fifo_rd_pvld_int := rd_req_next
//     when(rd_popping){
//         dat_fifo_rd_pd_out := dat_fifo_rd_pd_p
//     }

//     io.dat_fifo_rd_pd := dat_fifo_rd_pd_out
//     nvdla_core_clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
//                                     io.dat_fifo_wr_pvld) || (rd_pushing || rd_popping || 
//                                     (dat_fifo_rd_pvld_int && io.dat_fifo_rd_prdy)) || (wr_pushing))
// }}



// class NV_NVDLA_SDP_CORE_Y_LUT_cmd(width:Int) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val cmd_fifo_wr_prdy = Output(Bool())
//         val cmd_fifo_wr_pvld = Input(Bool())
//         val cmd_fifo_wr_pd = Input(UInt(width.W))
//         val cmd_fifo_rd_prdy = Input(Bool())
//         val cmd_fifo_rd_pvld = Output(Bool())
//         val cmd_fifo_rd_pd = Output(UInt(width.W))

//         val pwrbus_ram_pd = Input(UInt(32.W))
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
//     withClock(io.nvdla_core_clk){
//    // Master Clock Gating (SLCG)
//     //
//     // We gate the clock(s) when idle or stalled.
//     // This allows us to turn off numerous miscellaneous flops
//     // that don't get gated during synthesis for one reason or another.
//     //
//     // We gate write side and read side separately. 
//     // If the fifo is synchronous, we also gate the ram separately, but if
//     // -master_clk_gated_unified or -status_reg/-status_logic_reg is specified, 
//     // then we use one clk gate for write, ram, and read.
//     //
//     val nvdla_core_clk_mgated_enable = Wire(Bool())
//     val nvdla_core_clk_mgate = Module(new NV_CLK_gate_power)
//     nvdla_core_clk_mgate.io.clk := io.nvdla_core_clk
//     nvdla_core_clk_mgate.io.clk_en := nvdla_core_clk_mgated_enable
//     val nvdla_core_clk_mgated = nvdla_core_clk_mgate.io.clk_gated

//     ////////////////////////////////////////////////////////////////////////
//     // WRITE SIDE                                                        //
//     ////////////////////////////////////////////////////////////////////////
//     val wr_reserving = Wire(Bool())
//     val cmd_fifo_wr_busy_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // copy for internal use
//     io.cmd_fifo_wr_prdy := !cmd_fifo_wr_busy_int   // reserving write space?
//     wr_reserving := io.cmd_fifo_wr_pvld && !cmd_fifo_wr_busy_int

//     val wr_popping = Wire(Bool())      // fwd: write side sees pop?
//     val cmd_fifo_wr_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))} // write-side count
//     val wr_count_next_wr_popping = Mux(wr_reserving, cmd_fifo_wr_count, cmd_fifo_wr_count-1.U)
//     val wr_count_next_no_wr_popping = Mux(wr_reserving, cmd_fifo_wr_count+1.U, cmd_fifo_wr_count)
//     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

//     val wr_count_next_no_wr_popping_is_2 = (wr_count_next_no_wr_popping === 2.U)
//     val wr_count_next_is_2 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_2)
//     val wr_limit_muxed = Wire(UInt(2.W))    // muxed with simulation/emulation overrides
//     val wr_limit_reg = wr_limit_muxed
//     val cmd_fifo_wr_busy_next = wr_count_next_is_2 ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

//     cmd_fifo_wr_busy_int := cmd_fifo_wr_busy_next
//     when(wr_reserving ^ wr_popping){
//         cmd_fifo_wr_count := wr_count_next
//     }

//     val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

//     //
//     // RAM
//     //  

//     val cmd_fifo_wr_adr = withClock(nvdla_core_clk_mgated){RegInit(false.B)}   // current write address
//     cmd_fifo_wr_adr := cmd_fifo_wr_adr + 1.U

//     val rd_popping = Wire(Bool())  // read side doing pop this cycle?
//     val cmd_fifo_rd_adr = withClock(nvdla_core_clk_mgated){RegInit(false.B)}   // current read address
//     val ram_we = wr_pushing && (cmd_fifo_wr_count > 0.U || !rd_popping);   // note: write occurs next cycle

//     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
//     // Fifogen handles this by ignoring the data on the ram data out for that cycle.

//     val ram = Module(new nv_flopram(2, width))
//     ram.io.clk := nvdla_core_clk_mgated
//     ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ram.io.di := io.cmd_fifo_wr_pd
//     ram.io.we := ram_we
//     ram.io.wa := cmd_fifo_wr_adr
//     ram.io.ra := Mux(cmd_fifo_wr_count =/= 0.U, "d2".asUInt(2.W), cmd_fifo_rd_adr)
//     val cmd_fifo_rd_pd_p = ram.io.dout
    
//     // next    read address
//     when(rd_popping){
//         cmd_fifo_rd_adr := cmd_fifo_rd_adr + 1.U
//     }

//     //
//     // SYNCHRONOUS BOUNDARY
//     //
//     wr_popping := rd_popping    
//     val rd_pushing = withClock(nvdla_core_clk_mgated){RegNext(wr_pushing, false.B)} 

//     //
//     // READ SIDE
//     //
//     val cmd_fifo_rd_pvld_p = Wire(Bool())  // data out of fifo is valid
//     val cmd_fifo_rd_pvld_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)} // internal copy of rd_req
//     io.cmd_fifo_rd_pvld := cmd_fifo_rd_pvld_int
//     rd_popping := cmd_fifo_rd_pvld_p && !(cmd_fifo_rd_pvld_int && !io.cmd_fifo_rd_prdy)

//     val cmd_fifo_rd_count_p = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))} //read-side fifo count
//     val rd_count_p_next_rd_popping = Mux(rd_pushing, cmd_fifo_rd_count_p, cmd_fifo_rd_count_p-1.U)
//     val rd_count_p_next_no_rd_popping = Mux(rd_pushing, cmd_fifo_rd_count_p + 1.U, cmd_fifo_rd_count_p)
//     val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)

//     cmd_fifo_rd_pvld_p := (cmd_fifo_rd_count_p =/= 0.U) || rd_pushing

//     when(rd_pushing || rd_popping){
//         cmd_fifo_rd_count_p := rd_count_p_next
//     }
    
//     val cmd_fifo_rd_pd_out = withClock(nvdla_core_clk_mgated){Reg(UInt(width.W))}    // output data register
//     val rd_req_next = (cmd_fifo_rd_pvld_p || (cmd_fifo_rd_pvld_int && !io.cmd_fifo_rd_prdy))

//     cmd_fifo_rd_pvld_int := rd_req_next
//     when(rd_popping){
//         cmd_fifo_rd_pd_out := cmd_fifo_rd_pd_p
//     }

//     io.cmd_fifo_rd_pd := cmd_fifo_rd_pd_out
//     nvdla_core_clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
//                          (io.cmd_fifo_wr_pvld && !cmd_fifo_wr_busy_int) || 
//                          (cmd_fifo_wr_busy_int =/= cmd_fifo_wr_busy_next)) || 
//                          (rd_pushing || rd_popping || (cmd_fifo_rd_pvld_int && io.cmd_fifo_rd_prdy)) || 
//                          (wr_pushing))

//     wr_limit_muxed := "d0".asUInt(2.W)
// }}







// object NV_NVDLA_SDP_CORE_Y_lutDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_CORE_Y_lut)
// }



















  














  

