// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_int_idx extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val lut_in_pvld = Input(Bool())
//         val lut_in_prdy = Output(Bool())
//         val lut_data_in = Input(UInt(32.W))

//         val lut_out_pvld = Output(Bool())
//         val lut_out_prdy = Input(Bool())
//         val lut_out_frac = Output(UInt(35.W))
//         val lut_out_le_hit = Output(Bool())
//         val lut_out_lo_hit = Output(Bool())
//         val lut_out_oflow = Output(Bool())
//         val lut_out_ram_addr = Output(UInt(9.W))
//         val lut_out_ram_sel = Output(Bool())
//         val lut_out_uflow = Output(Bool())
//         val lut_out_x = Output(UInt(32.W))

//         val cfg_lut_hybrid_priority = Input(Bool())
//         val cfg_lut_le_function = Input(Bool())
//         val cfg_lut_le_index_offset = Input(UInt(8.W))
//         val cfg_lut_le_index_select = Input(UInt(8.W))
//         val cfg_lut_le_start = Input(UInt(32.W))
//         val cfg_lut_lo_index_select = Input(UInt(8.W))
//         val cfg_lut_lo_start = Input(UInt(32.W))
//         val cfg_lut_oflow_priority = Input(Bool())
//         val cfg_lut_uflow_priority = Input(Bool())
        
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

//     val lut_x_in_pvld = Wire(Bool())
//     val lut_pipe_prdy = Wire(Bool())
//     //The same three stage pipe with lut_expn and lut_line
//     val pipe_p1 = Module(new NV_NVDLA_BC_pipe(32))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := lut_x_in_pvld
//     val lut_x_in_prdy = pipe_p1.io.ro
//     pipe_p1.io.di := io.lut_data_in
//     val lut_pipe_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := lut_pipe_prdy
//     val lut_pipe_x = pipe_p1.io.dout

//     val lut_pipe2_prdy = Wire(Bool())
//     val pipe_p2 = Module(new NV_NVDLA_BC_pipe(32))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := lut_pipe_pvld
//     val lut_pipe_prdy = pipe_p1.io.ro
//     pipe_p1.io.di := lut_pipe_x
//     val lut_pipe2_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := lut_pipe2_prdy
//     val lut_pipe2_x = pipe_p1.io.dout

//     val lut_x_out_prdy = Wire(Bool())
//     val pipe_p3 = Module(new NV_NVDLA_BC_pipe(32))
//     pipe_p3.io.clk := io.nvdla_core_clk
//     pipe_p3.io.vi := lut_pipe2_pvld
//     val lut_pipe2_prdy = pipe_p3.io.ro
//     pipe_p3.io.di := lut_pipe2_x
//     val lut_x_out_pvld = pipe_p3.io.vo
//     pipe_p3.io.ri := lut_x_out_prdy
//     val lut_final_x = pipe_p3.io.dout

//     val le_expn_cfg_offset = Wire(UInt(8.W))
//     val le_expn_cfg_start = Wire(UInt(32.W))
//     val le_expn_data_in = Wire(UInt(32.W))
//     val le_expn_in_pvld = Wire(Bool())
//     val le_expn_out_prdy = Wire(Bool())
//     val lut_le_expn = Module(new NV_NVDLA_SDP_HLS_lut_expn(65))
//     lut_le_expn.io.nvdla_core_clk := io.nvdla_core_clk
//     lut_le_expn.io.cfg_lut_offset := le_expn_cfg_offset
//     lut_le_expn.io.cfg_lut_start := le_expn_cfg_start
//     lut_le_expn.io.idx_data_in := le_expn_data_in
//     lut_le_expn.io.idx_out_prdy := le_expn_out_prdy
//     val le_expn_in_prdy = lut_le_expn.io.idx_in_prdy
//     val le_expn_out_pvld = lut_le_expn.io.idx_out_pvld
//     val le_expn_frac = lut_le_expn.io.lut_frac_out
//     val le_expn_index = lut_le_expn.io.lut_index_out
//     val le_expn_oflow = lut_le_expn.io.lut_oflow_out
//     val le_expn_uflow = lut_le_expn.io.lut_uflow_out

//     val le_line_cfg_sel = Wire(UInt(8.W))
//     val le_line_cfg_start = Wire(UInt(32.W))
//     val le_line_data_in = Wire(UInt(32.W))
//     val le_line_in_pvld = Wire(Bool())
//     val le_line_out_prdy = Wire(Bool())
//     val lut_le_line = Module(new NV_NVDLA_SDP_HLS_lut_line(65))
//     lut_le_line.io.nvdla_core_clk := io.nvdla_core_clk
//     lut_le_line.io.cfg_lut_sel := le_line_cfg_sel
//     lut_le_line.io.cfg_lut_start := le_line_cfg_start
//     lut_le_line.io.idx_data_in := le_line_data_in
//     lut_le_line.io.idx_in_pvld := le_line_in_pvld
//     lut_le_line.io.idx_out_prdy := le_line_out_prdy
//     val le_line_in_prdy = lut_le_line.io.idx_in_prdy
//     val le_line_out_pvld = lut_le_line.io.idx_out_pvld
//     val le_line_frac = lut_le_line.io.lut_frac_out
//     val le_line_index = lut_le_line.io.lut_index_out
//     val le_line_oflow = lut_le_line.io.lut_oflow_out
//     val le_line_uflow = lut_le_line.io.lut_uflow_out

//     val lo_line_in_pvld = Wire(Bool())
//     val lo_line_out_prdy = Wire(Bool())
//     val lut_lo_line = Module(new NV_NVDLA_SDP_HLS_lut_line(257))
//     lut_lo_line.io.nvdla_core_clk := io.nvdla_core_clk
//     lut_lo_line.io.cfg_lut_sel := io.cfg_lut_lo_index_select
//     lut_lo_line.io.cfg_lut_start := io.cfg_lut_lo_start
//     lut_lo_line.io.idx_data_in := io.lut_data_in
//     lut_lo_line.io.idx_in_pvld := lo_line_in_pvld
//     lut_lo_line.io.idx_out_prdy := lo_line_out_prdy
//     val lo_line_in_prdy = lut_lo_line.io.idx_in_prdy
//     val lo_line_out_pvld = lut_lo_line.io.idx_out_pvld
//     val lo_line_frac = lut_lo_line.io.lut_frac_out
//     val lo_line_index = lut_lo_line.io.lut_index_out
//     val lo_line_oflow = lut_lo_line.io.lut_oflow_out
//     val lo_line_uflow = lut_lo_line.io.lut_uflow_out

//     //sync lut_x_in, le_in,lo_in 
//     le_expn_in_pvld := (io.cfg_lut_le_function === 0.U) & io.lut_in_pvld & lo_line_in_prdy & lut_x_in_prdy
//     le_line_in_pvld := (io.cfg_lut_le_function =/= 0.U) & io.lut_in_pvld & lo_line_in_prdy & lut_x_in_prdy
//     lo_line_in_pvld := Mux(io.cfg_lut_le_function === 0.U, le_expn_in_prdy, le_line_in_prdy) & io.lut_in_pvld & lut_x_in_prdy
//     lut_x_in_pvld := Mux(io.cfg_lut_le_function === 0.U, le_expn_in_prdy, le_line_in_prdy) & io.lut_in_pvld & lo_line_in_prdy
//     lut_in_prdy := Mux(io.cfg_lut_le_function === 0.U, le_expn_in_prdy, le_line_in_prdy) & lo_line_in_prdy & lut_x_in_prdy

//     //sync lut_x_out, le_out,lo_out
//     val lut_final_prdy = Wire(Bool())
//     le_expn_out_prdy := (io.cfg_lut_le_function === 0.U) & lut_final_prdy & lo_line_out_pvld & lut_x_out_pvld;
//     le_line_out_prdy := (io.cfg_lut_le_function =/= 0.U) & lut_final_prdy & lo_line_out_pvld & lut_x_out_pvld;
//     lo_line_out_prdy := Mux(io.cfg_lut_le_function === 0.U， le_expn_out_pvld, le_line_out_pvld) & lut_final_prdy & lut_x_out_pvld;
//     lut_x_out_prdy := Mux(io.cfg_lut_le_function === 0.U， le_expn_out_pvld, le_line_out_pvld) & lut_final_prdy & lo_line_out_pvld;
//     lut_final_pvld := Mux(io.cfg_lut_le_function === 0.U， le_expn_out_pvld, le_line_out_pvld) & lo_line_out_pvld & lut_x_out_pvld;

//     le_expn_data_in := Mux(io.cfg_lut_le_function === 0.U, io.lut_data_in)









// }}


// object NV_NVDLA_SDP_HLS_Y_int_idxDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_idx)
// }