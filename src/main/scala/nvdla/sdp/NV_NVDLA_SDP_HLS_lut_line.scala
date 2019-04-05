// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_lut_line extends Module {

//    val LUT_DEPTH = 256

//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val cfg_lut_sel = Input(UInt(8.W))
//         val cfg_lut_start = Input(UInt(32.W))
//         val idx_data_in = Input(UInt(32.W))
//         val idx_in_pvld = Input(Bool())
//         val idx_out_prdy = Input(Bool())
    
//         val idx_in_prdy = Output(Bool())
//         val idx_out_pvld = Output(Bool())
//         val lut_frac_out = Output(UInt(34.W))
//         val lut_index_out = Output(UInt(9.W))
//         val lut_oflow_out = Output(Bool())
//         val ut_uflow_out = Output(Bool())
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
//     val mux_prdy = Wire(Bool())
//     val pipe_p1 = Module(new NV_NVDLA_SDP_HLS_LUT_LINE_pipe_p1)
//     pipe_p1.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p1.io.cfg_lut_sel := io.cfg_lut_sel
//     pipe_p1.io.cfg_lut_start := io.cfg_lut_start
//     pipe_p1.io.idx_data_in := io.idx_data_in
//     pipe_p1.io.idx_in_pvld := io.idx_in_pvld
//     pipe_p1.io.mux_prdy := mux_prdy
//     val cfg_lut_sel_reg = pipe_p1.io.cfg_lut_sel_reg
//     val cfg_lut_start_reg = pipe_p1.io.cfg_lut_start_reg
//     val idx_data_reg = pipe_p1.io.idx_data_reg
//     val idx_in_prdy = pipe_p1.io.idx_in_prdy
//     val mux_pvld = pipe_p1.io.mux_pvld

//     val lut_uflow_in = (idx_data_reg.asSInt <= cfg_lut_start_reg.asSInt)
//     val lut_index_sub_tmp = idx_data_reg.asSInt - cfg_lut_start_reg.asSInt
//     //unsigned int
//     val lut_index_sub = Mux(lut_uflow_in, 0.asSInt, lut_index_sub_tmp).asUInt

//     val sub_prdy = Wire(Bool())
//     val pipe_p2 = Module(new NV_NVDLA_SDP_HLS_LUT_LINE_pipe_p2)
//     pipe_p2.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p2.io.lut_index_sub := lut_index_sub
//     pipe_p2.io.lut_uflow_in := lut_uflow_in
//     pipe_p2.io.mux_pvld := mux_pvld
//     pipe_p2.io.sub_prdy := sub_prdy
//     val lut_index_sub_reg = pipe_p2.io.lut_index_sub_reg
//     val lut_uflow = pipe_p2.io.lut_uflow
//     mux_prdy := pipe_p2.io.mux_prdy
//     val sub_pvld = pipe_p2.io.sub_pvld

//     //saturation and truncate, but no rounding










// }