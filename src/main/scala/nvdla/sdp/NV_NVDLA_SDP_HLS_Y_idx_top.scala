// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_cvt_top(implicit conf: sdpConfiguration) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val cfg_lut_hybrid_priority = Input(Bool())
//         val cfg_lut_le_function = Input(Bool())
//         val cfg_lut_le_index_offset = Input(UInt(8.W))
//         val cfg_lut_le_index_select = Input(UInt(8.W))
//         val cfg_lut_le_start = Input(UInt(32.W))
//         val cfg_lut_lo_index_select = Input(UInt(8.W))
//         val cfg_lut_lo_start = Input(UInt(32.W))

//         val chn_lut_in_pvld = Input(Bool())
//         val chn_lut_in_prdy = Output(Bool())
//         val chn_lut_in_pd = Input(UInt(conf.EW_CORE_OUT_DW.W))

//         val chn_lut_out_pvld = Output(Bool())
//         val chn_lut_out_prdy = Input(Bool())
//         val chn_lut_out_pd = Output(UInt(conf.EW_IDX_OUT_DW.W))
        
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
//     val cvt_data_in_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                             map { i => io.cvt_data_in(16*i+15, 16*i)})
//     val cvt_data_out_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
//     val cvt_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val cvt_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))

//     io.cvt_data_out := cvt_data_out_wire.asUInt

//     val y_int_cvt = Array.fill(conf.NVDLA_SDP_EW_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_Y_int_idx)}

//     for(i <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){

//         y_int_cvt(i).io.nvdla_core_clk := io.nvdla_core_clk
//         y_int_cvt(i).io.cfg_cvt_bypass := io.cfg_cvt_bypass
//         y_int_cvt(i).io.cfg_cvt_offset := io.cfg_cvt_offset
//         y_int_cvt(i).io.cfg_cvt_scale := io.cfg_cvt_scale
//         y_int_cvt(i).io.cfg_cvt_truncate := io.cfg_cvt_truncate
//         y_int_cvt(i).io.cvt_data_in := cvt_data_in_wire(i)
//         y_int_cvt(i).io.cvt_in_pvld := io.cvt_in_pvld
//         y_int_cvt(i).io.cvt_out_prdy := io.cvt_out_prdy
//         cvt_data_out_wire(i) := y_int_cvt(i).io.cvt_data_out
//         cvt_in_prdy_wire(i) := y_int_cvt(i).io.cvt_in_prdy
//         cvt_out_pvld_wire(i) := y_int_cvt(i).io.cvt_out_pvld

//     }

//     io.cvt_in_prdy := cvt_in_prdy_wire(0)
//     io.cvt_out_pvld := cvt_out_pvld_wire(0)


// }}


// object NV_NVDLA_SDP_HLS_Y_cvt_topDriver extends App {
//   implicit val conf: sdpConfiguration = new sdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_cvt_top)
// }