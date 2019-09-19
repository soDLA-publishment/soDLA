// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_cvtout(implicit val conf: cdpConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //clock
//         val nvdla_core_clk = Input(Clock())

//         val cvtout_pd = Output(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE+17).W))

//         val mul2ocvt_pvld = Input(Bool())
//         val mul2ocvt_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE+16)).W))
//         val mul2ocvt_prdy = Output(Bool())

//         val reg2dp_datout_offset = Input(UInt(32.W))
//         val reg2dp_datout_scale = Input(UInt(16.W))
//         val reg2dp_datout_shifter = Input(UInt(6.W))

//         val sync2ocvt_pd = Input(UInt(17.W))
//         val sync2ocvt_pvld = Input(Bool())
//         val sync2ocvt_prdy = Output(Bool())
//     })

//     /////////////////////////////////////////////////////////////
// withClock(io.nvdla_core_clk){

// //----------------------------------------
// //interlock between data and info

// /////////////////////////////
//     val cdp_cvtout_input_rdy = Wire(Bool())
//     val data_info_in_rdy = Wire(Bool())
//     val cdp_cvtout_in_ready = cdp_cvtout_input_rdy & data_info_in_rdy

//     val cdp_cvtout_in_valid = io.sync2ocvt_pvld & io.mul2ocvt_pvld
//     io.mul2ocvt_prdy := cdp_cvtout_in_ready & io.sync2ocvt_pvld
//     io.sync2ocvt_prdy := cdp_cvtout_in_ready & io.mul2ocvt_pvld

// //===============================================
// //pipeline delay for data info to sync with data path
// //-----------------------------------------------
// //data info valid in
//     val data_info_in_vld = cdp_cvtout_in_valid & cdp_cvtout_input_rdy
// //data info data in
//     val data_info_in_pd = io.sync2ocvt_pd

//     val data_info_in_vld_d0 = data_info_in_vld
//     val data_info_in_pd_d0 = data_info_in_pd

//     val data_info_in_rdy_d1_f = Wire(Bool())
//     val pipe_p1 = Module(new NV_NVDLA_IS_pipe(17))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := data_info_in_vld_d0
//     val data_info_in_rdy_d0 = pipe_p1.io.ro
//     pipe_p1.io.di := data_info_in_pd_d0
//     val data_info_in_vld_d1 = pipe_p1.io.vo
//     pipe_p1.io.ri := data_info_in_rdy_d1_f
//     val data_info_in_pd_d1 = pipe_p1.io.dout

//     val data_info_in_rdy_d2_f = Wire(Bool())
//     val pipe_p2 = Module(new NV_NVDLA_IS_pipe(17))
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := data_info_in_vld_d1
//     val data_info_in_rdy_d1 = pipe_p2.io.ro
//     pipe_p2.io.di := data_info_in_pd_d1
//     val data_info_in_vld_d2 = pipe_p2.io.vo
//     pipe_p2.io.ri := data_info_in_rdy_d2_f
//     val data_info_in_pd_d2 = pipe_p2.io.dout

//     val data_info_in_rdy_d3_f = Wire(Bool())
//     val pipe_p3 = Module(new NV_NVDLA_IS_pipe(17))
//     pipe_p3.io.clk := io.nvdla_core_clk
//     pipe_p3.io.vi := data_info_in_vld_d2
//     val data_info_in_rdy_d2 = pipe_p3.io.ro
//     pipe_p3.io.di := data_info_in_pd_d2
//     val data_info_in_vld_d3 = pipe_p3.io.vo
//     pipe_p3.io.ri := data_info_in_rdy_d3_f
//     val data_info_in_pd_d3 = pipe_p3.io.dout

//     val data_info_in_rdy_d4 = Wire(Bool())
//     val pipe_p4 = Module(new NV_NVDLA_IS_pipe(17))
//     pipe_p4.io.clk := io.nvdla_core_clk
//     pipe_p4.io.vi := data_info_in_vld_d3
//     val data_info_in_rdy_d3 = pipe_p4.io.ro
//     pipe_p4.io.di := data_info_in_pd_d3
//     val data_info_in_vld_d4 = pipe_p4.io.vo
//     pipe_p4.io.ri := data_info_in_rdy_d4
//     val data_info_in_pd_d4 = pipe_p4.io.dout

//     data_info_in_rdy := data_info_in_rdy_d0
//     data_info_in_rdy_d1_f := data_info_in_rdy_d1
//     data_info_in_rdy_d2_f := data_info_in_rdy_d2
//     data_info_in_rdy_d3_f := data_info_in_rdy_d3

//     val data_info_out_vld = data_info_in_vld_d4
//     val data_info_out_rdy = Wire(Bool())
//     data_info_in_rdy_d4 := data_info_out_rdy
//     val data_info_out_pd = data_info_in_pd_d4

// //===============================================
// //convertor process
// //-----------------------------------------------
// //cvtout valid input
//     val cdp_cvtout_input_vld = cdp_cvtout_in_valid & data_info_in_rdy
// //cvtout ready input
//     val cdp_cvtout_input_rdys = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     cdp_cvtout_input_rdy := cdp_cvtout_input_rdys.asUInt.andR

// //cvt sub-unit valid in
//     val cdp_cvtout_input_vlds = VecInit(
//         (0 until conf.NVDLA_CDP_THROUGHPUT) map {
//             i => cdp_cvtout_input_vld & cdp_cvtout_input_rdys.asUInt.andR
//         })

// //cvt sub-unit data in
//     val cdp_cvtout_input_pd = VecInit(
//         (0 until conf.NVDLA_CDP_THROUGHPUT) map {
//             i => io.mul2ocvt_pd(i*(conf.NVDLA_CDP_ICVTO_BWPE+16)+conf.NVDLA_CDP_ICVTO_BWPE+16-1, i*(conf.NVDLA_CDP_ICVTO_BWPE+16))
//         })

//     val reg2dp_datout_offset_use = RegInit(0.U(32.W))
//     reg2dp_datout_offset_use := io.reg2dp_datout_offset

//     val reg2dp_datout_scale_use = RegInit(0.U(16.W))
//     reg2dp_datout_scale_use := io.reg2dp_datout_scale

//     val reg2dp_datout_shifter_use = RegInit(0.U(6.W))
//     reg2dp_datout_shifter_use := io.reg2dp_datout_shifter

//     val cdp_cvtout_output_rdys = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     val cdp_cvtout_output_vlds = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     val cdp_cvtout_output_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt((conf.NVDLA_CDP_ICVTO_BWPE+16).W)))

//     val u_HLS_cdp_ocvt = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new HLS_cdp_ocvt)}
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         u_HLS_cdp_ocvt(i).io.nvdla_core_clk := io.nvdla_core_clk
//         u_HLS_cdp_ocvt(i).io.cfg_alu_in_rsc_z := reg2dp_datout_offset_use(conf.NVDLA_CDP_ICVTO_BWPE+16-1, 0)
//         u_HLS_cdp_ocvt(i).io.cfg_mul_in_rsc_z := reg2dp_datout_scale_use
//         u_HLS_cdp_ocvt(i).io.cfg_truncate_rsc_z := reg2dp_datout_shifter_use
//         u_HLS_cdp_ocvt(i).io.chn_data_in_rsc_vz := cdp_cvtout_input_vlds(i)
//         u_HLS_cdp_ocvt(i).io.chn_data_in_rsc_z := cdp_cvtout_input_pd(i)
//         u_HLS_cdp_ocvt(i).io.chn_data_out_rsc_vz := cdp_cvtout_output_rdys(i)
//         cdp_cvtout_input_rdys(i) := u_HLS_cdp_ocvt(i).io.chn_data_in_rsc_lz
//         cdp_cvtout_output_vlds(i) := u_HLS_cdp_ocvt(i).io.chn_data_out_rsc_lz
//         cdp_cvtout_output_pd(i) := u_HLS_cdp_ocvt(i).io.chn_data_out_rsc_z
//     }

// //sub-unit output ready
//     val cdp_cvtout_output_rdy = Wire(Bool())
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         cdp_cvtout_output_rdys(i) := cdp_cvtout_output_rdy & cdp_cvtout_output_vlds.asUInt.andR
//     }

// //output valid
//     val cdp_cvtout_output_vld = cdp_cvtout_output_vlds.asUInt.andR
// //output ready
//     cdp_cvtout_output_rdy := io.cvtout_prdy & data_info_out_vld
// //output data
//     val cdp_cvtout_output_pd_all = 
//         Cat(cdp_cvtout_output_pd(7), cdp_cvtout_output_pd(6), 
//             cdp_cvtout_output_pd(5), cdp_cvtout_output_pd(4), 
//             cdp_cvtout_output_pd(3), cdp_cvtout_output_pd(2), 
//             cdp_cvtout_output_pd(1), cdp_cvtout_output_pd(0))   

// //===============================================
// //data info output
// //-----------------------------------------------
// //data info output ready
//     data_info_out_rdy := io.cvtout_prdy & cdp_cvtout_output_vld

// //===============================================
// //convertor output
// //-----------------------------------------------
//     io.cvtout_pvld := cdp_cvtout_output_vld & data_info_out_vld
//     io.cvtout_pd   := Cat(data_info_out_pd, cdp_cvtout_output_pd_all)

// }
// }
      
// object NV_NVDLA_CDP_DP_cvtoutDriver extends App {
//     implicit val conf: cdpConfiguration = new cdpConfiguration
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_cvtout())
// }
