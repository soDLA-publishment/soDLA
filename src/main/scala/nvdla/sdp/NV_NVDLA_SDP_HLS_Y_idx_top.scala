// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_idx_top(implicit conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val cfg_lut_hybrid_priority = Input(Bool())
//         val cfg_lut_le_function = Input(Bool())
//         val cfg_lut_le_index_offset = Input(UInt(8.W))
//         val cfg_lut_le_index_select = Input(UInt(8.W))
//         val cfg_lut_le_start = Input(UInt(32.W))
//         val cfg_lut_lo_index_select = Input(UInt(8.W))
//         val cfg_lut_lo_start = Input(UInt(32.W))
//         val cfg_lut_oflow_priority = Input(Bool())
//         val cfg_lut_uflow_priority = Input(Bool())

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
//     val chn_lut_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val chn_lut_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val lut_data_in_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                                map { i => io.chn_lut_in_pd(32*i+31, 32*i)})
//     val lut_out_addr_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(9.W)))
//     val lut_out_fraction_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(35.W)))
//     val lut_out_le_hit_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val lut_out_lo_hit_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val lut_out_oflow_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val lut_out_sel_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val lut_out_uflow_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val lut_out_x_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))

//     val y_int_idx = Array.fill(conf.NVDLA_SDP_EW_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_Y_int_idx)}

//     for(i <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){

//         y_int_idx(i).io.nvdla_core_clk := io.nvdla_core_clk
//         y_int_idx(i).io.cfg_lut_hybrid_priority := io.cfg_lut_hybrid_priority
//         y_int_idx(i).io.cfg_lut_le_function := io.cfg_lut_le_function
//         y_int_idx(i).io.cfg_lut_le_index_offset := io.cfg_lut_le_index_offset
//         y_int_idx(i).io.cfg_lut_le_index_select := io.cfg_lut_le_index_select
//         y_int_idx(i).io.cfg_lut_le_start := io.cfg_lut_le_start
//         y_int_idx(i).io.cfg_lut_lo_index_select := io.cfg_lut_lo_index_select
//         y_int_idx(i).io.cfg_lut_lo_start := io.cfg_lut_lo_start
//         y_int_idx(i).io.cfg_lut_oflow_priority := io.cfg_lut_oflow_priority
//         y_int_idx(i).io.cfg_lut_uflow_priority := io.cfg_lut_uflow_priority
//         y_int_idx(i).io.lut_data_in := lut_data_in_wire(i)
//         y_int_idx(i).io.lut_in_pvld := io.chn_lut_in_pvld
//         y_int_idx(i).io.lut_out_prdy := io.chn_lut_out_prdy
//         chn_lut_in_prdy_wire(i) := y_int_idx(i).io.lut_in_prdy
//         lut_out_fraction_wire(i) := y_int_idx(i).io.lut_out_frac 
//         lut_out_le_hit_wire(i) := y_int_idx(i).io.lut_out_le_hit
//         lut_out_lo_hit_wire(i) := y_int_idx(i).io.lut_out_lo_hit
//         lut_out_oflow_wire(i) := y_int_idx(i).io.lut_out_oflow
//         chn_lut_out_pvld_wire(i) := y_int_idx(i).io.lut_out_pvld 
//         lut_out_addr_wire(i) := y_int_idx(i).io.lut_out_ram_addr 
//         lut_out_sel_wire(i) := y_int_idx(i).io.lut_out_ram_sel 
//         lut_out_uflow_wire(i) := y_int_idx(i).io.lut_out_uflow 
//         lut_out_x_wire(i) := y_int_idx(i).io.lut_out_x
//     }

//     io.chn_lut_out_pd := Cat(lut_out_lo_hit_wire.asUInt, lut_out_le_hit_wire.asUInt, lut_out_addr_wire.asUInt,
//                             lut_out_sel_wire.asUInt, lut_out_uflow_wire.asUInt, lut_out_oflow_wire.asUInt,
//                             lut_out_x_wire.asUInt, lut_out_fraction_wire.asUInt)

//     io.chn_lut_in_prdy := chn_lut_in_prdy_wire(0)
//     io.chn_lut_out_pvld := chn_lut_out_pvld_wire(0)

// }}


// object NV_NVDLA_SDP_HLS_Y_idx_topDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_idx_top)
// }