// package nvdla

// import chisel3._
// import chisel3.util._
// import chisel3.experimental._


// class NV_NVDLA_SDP_CORE_Y_lut extends Module {
//     val io = IO(new Bundle {
//     //general clock
//     val nvdla_core_clk = Input(Clock())

//     //lut2inp interface  
//     val lut2inp_pvld = Output(Bool())   /* data valid */
//     val lut2inp_prdy = Input(Bool())     /* data return handshake */
//     val lut2inp_pd = Output(UInt(conf.EW_LUT_OUT_DW.W))

//     //idx2lut interface  
//     val idx2lut_pvld = Input(Bool())   /* data valid */
//     val idx2lut_prdy = Output(Bool())     /* data return handshake */
//     val idx2lut_pd = Input(UInt(conf.EW_LUT_OUT_DW.W))
  
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
//     val reg2dp_lut_le_slope_uflow_shift = Input((UInt(5.W))
//     val reg2dp_lut_le_start = Input(UInt(32.W))
//     val reg2dp_lut_lo_end = Input(UInt(32.W))
//     val reg2dp_lut_lo_slope_oflow_scale = Input(UInt(16.W))
//     val reg2dp_lut_lo_slope_oflow_shift = Input(UInt(5.W))
//     val reg2dp_lut_lo_slope_uflow_scale = Input(UInt(16.W))
//     val reg2dp_lut_lo_slope_uflow_shift = Input((UInt(5.W))
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
//     val op_en_loa = Input(Bool())
//   })

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


// val reg_le = Seq.fill(conf.LUT_TABLE_LE_DEPTH)(RegInit("b0".asUInt(16.W))) +: 
//              Seq.fill(conf.LUT_TABLE_LO_DEPTH - conf.LUT_TABLE_LE_DEPTH)(RegInit("b0".UInt(16.W)))

// val reg_lo = Seq.fill(conf.LUT_TABLE_LO_DEPTH)(RegInit("b0".asUInt(16.W)))


// // READ LUT
// val le_lut_data = MuxLookup(pro_in_addr, "b0".asUInt(16.W),
//                   (0 to conf.LUT_TABLE_LE_DEPTH-1) map { i => i.U -> reg_le(i) })

// val lo_lut_data = MuxLookup(pro_in_addr, "b0".asUInt(16.W),
//                   (0 to conf.LUT_TABLE_LO_DEPTH-1) map { i => i.U -> reg_lo(i) })


// //=======================================
// // WRITE LUT

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




// }}















  














  

