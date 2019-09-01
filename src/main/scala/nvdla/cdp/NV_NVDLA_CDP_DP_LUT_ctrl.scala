// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_LUT_ctrl(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val sum2itp_pvld = Input(Bool())
//         val sum2itp_prdy = Output(Bool())
//         val sum2itp_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT * (conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)).W))

//         val sum2sync_pvld = Output(Bool())
//         val sum2sync_prdy = Input(Bool())
//         val sum2sync_pd = Output(UInt((conf.NVDLA_CDP_THROUGHPUT * (conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)).W))

//         val dp2lut_pvld = Output(Bool())
//         val dp2lut_prdy = Input(Bool())
//         val dp2lut_X_entry = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(10.W)))
//         val dp2lut_Xinfo = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(18.W)))
//         val dp2lut_Y_entry = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(10.W)))
//         val dp2lut_Yinfo = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(18.W)))

//         val reg2dp_sqsum_bypass = Input(Bool())
//         val reg2dp_lut_le_function = Input(Bool())
//         val reg2dp_lut_le_index_offset = Input(UInt(8.W))
//         val reg2dp_lut_le_index_select = Input(UInt(8.W))
//         val reg2dp_lut_le_start_high = Input(UInt(6.W))
//         val reg2dp_lut_le_start_low = Input(UInt(32.W))
//         val reg2dp_lut_lo_index_select = Input(UInt(8.W))
//         val reg2dp_lut_lo_start_high = Input(UInt(6.W))
//         val reg2dp_lut_lo_start_low = Input(UInt(32.W))

//     })

// withClock(io.nvdla_core_clk){

//     ////////////////////////////////////////////////////////////////////////////////////////
//     val sum2itp_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     io.sum2itp_prdy := (sum2itp_rdy.asUInt.andR) & io.sum2sync_prdy

// //////////////////////////////////////////////////////////////////////
// //from intp_ctrl input port to sync fifo for interpolation
//     io.sum2sync_pvld := io.sum2itp_pvld & (sum2itp_rdy.asUInt.andR)
//     io.sum2sync_pd := io.sum2itp_pd
// ///////////////////////////////////////////
// // NVDLA_CDP_THROUGHPUT = 8
//     val sum2itp_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT - 1)){
//         for(j <- 0 to (conf.NVDLA_CDP_THROUGHPUT - 1)){
//             sum2itp_vld(i) := io.sum2itp_pvld & io.sum2sync_prdy & 
//                         (if(j != i) sum2itp_rdy(j) else true.B) 
//         }
//     }

//     // val SQSUMO = conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4
//     val sum2itp_a_pd = VecInit(
//         (0 to conf.NVDLA_CDP_THROUGHPUT-1) map {
//             i => io.sum2itp_pd((conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)*i+(conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)-1, (conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)*i)
//             })

//     val dp2lut_X_info = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(18.W)))
//     val dp2lut_X_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(10.W)))
//     val dp2lut_Y_info = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(18.W)))
//     val dp2lut_Y_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(10.W)))
//     val dp2lut_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     val dp2lut_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))

//     val u_LUT_CTRL_unit = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new NV_NVDLA_CDP_DP_LUT_CTRL_unit)}
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         u_LUT_CTRL_unit(i).io.nvdla_core_clk := io.nvdla_core_clk
//         u_LUT_CTRL_unit(i).io.sum2itp_pd := sum2itp_a_pd(i)
//         u_LUT_CTRL_unit(i).io.sum2itp_pvld := sum2itp_vld(i)
//         sum2itp_rdy(i) := u_LUT_CTRL_unit(i).io.sum2itp_prdy
//         u_LUT_CTRL_unit(i).io.reg2dp_lut_le_function := io.reg2dp_lut_le_function
//         u_LUT_CTRL_unit(i).io.reg2dp_lut_le_index_offset := io.reg2dp_lut_le_index_offset
//         u_LUT_CTRL_unit(i).io.reg2dp_lut_le_index_select := io.reg2dp_lut_le_index_select
//         u_LUT_CTRL_unit(i).io.reg2dp_lut_le_start_high := io.reg2dp_lut_le_start_high
//         u_LUT_CTRL_unit(i).io.reg2dp_lut_le_start_low := io.reg2dp_lut_le_start_low
//         u_LUT_CTRL_unit(i).io.reg2dp_lut_lo_index_select := io.reg2dp_lut_lo_index_select
//         u_LUT_CTRL_unit(i).io.reg2dp_lut_lo_start_high := io.reg2dp_lut_lo_start_high
//         u_LUT_CTRL_unit(i).io.reg2dp_lut_lo_start_low := io.reg2dp_lut_lo_start_low
//         u_LUT_CTRL_unit(i).io.reg2dp_sqsum_bypass := io.reg2dp_sqsum_bypass
//         dp2lut_X_info(i) := u_LUT_CTRL_unit(i).io.dp2lut_X_info
//         dp2lut_X_pd(i) := u_LUT_CTRL_unit(i).io.dp2lut_X_pd
//         dp2lut_Y_info(i) := u_LUT_CTRL_unit(i).io.dp2lut_Y_info
//         dp2lut_Y_pd(i) := u_LUT_CTRL_unit(i).io.dp2lut_Y_pd
//         dp2lut_vld(i) := u_LUT_CTRL_unit(i).io.dp2lut_pvld
//         u_LUT_CTRL_unit(i).io.dp2lut_prdy := dp2lut_rdy(i)

//     }

//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT - 1)){
//         io.dp2lut_X_entry(i) := dp2lut_X_pd(i)
//         io.dp2lut_Y_entry(i) := dp2lut_Y_pd(i)
//         io.dp2lut_Xinfo(i) := dp2lut_X_info(i)
//         io.dp2lut_Yinfo(i) := dp2lut_Y_info(i)
//     }

//     io.dp2lut_pvld := dp2lut_vld.asUInt.andR

// // NVDLA_CDP_THROUGHPUT = 8
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT - 1)){
//         for(j <- 0 to (conf.NVDLA_CDP_THROUGHPUT - 1)){
//             dp2lut_rdy(i) := io.dp2lut_prdy & 
//                         (if(j != i) dp2lut_vld(j) else true.B) 
//         }
//     }
// }}


// object NV_NVDLA_CDP_DP_LUT_ctrlDriver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_LUT_ctrl())
// }
