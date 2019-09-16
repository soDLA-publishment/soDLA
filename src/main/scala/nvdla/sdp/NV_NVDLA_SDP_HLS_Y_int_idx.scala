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
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := lut_pipe_pvld
//     lut_pipe_prdy := pipe_p2.io.ro
//     pipe_p2.io.di := lut_pipe_x
//     val lut_pipe2_pvld = pipe_p2.io.vo
//     pipe_p2.io.ri := lut_pipe2_prdy
//     val lut_pipe2_x = pipe_p2.io.dout

//     val lut_x_out_prdy = Wire(Bool())
//     val pipe_p3 = Module(new NV_NVDLA_BC_pipe(32))
//     pipe_p3.io.clk := io.nvdla_core_clk
//     pipe_p3.io.vi := lut_pipe2_pvld
//     lut_pipe2_prdy := pipe_p3.io.ro
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
//     lut_le_expn.io.idx_in_pvld := le_expn_in_pvld
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
//     io.lut_in_prdy := Mux(io.cfg_lut_le_function === 0.U, le_expn_in_prdy, le_line_in_prdy) & lo_line_in_prdy & lut_x_in_prdy

//     //sync lut_x_out, le_out,lo_out
//     val lut_final_prdy = Wire(Bool())
//     le_expn_out_prdy := (io.cfg_lut_le_function === 0.U) & lut_final_prdy & lo_line_out_pvld & lut_x_out_pvld;
//     le_line_out_prdy := (io.cfg_lut_le_function =/= 0.U) & lut_final_prdy & lo_line_out_pvld & lut_x_out_pvld;
//     lo_line_out_prdy := Mux(io.cfg_lut_le_function === 0.U, le_expn_out_pvld, le_line_out_pvld) & lut_final_prdy & lut_x_out_pvld;
//     lut_x_out_prdy := Mux(io.cfg_lut_le_function === 0.U, le_expn_out_pvld, le_line_out_pvld) & lut_final_prdy & lo_line_out_pvld;
//     val lut_final_pvld = Mux(io.cfg_lut_le_function === 0.U, le_expn_out_pvld, le_line_out_pvld) & lo_line_out_pvld & lut_x_out_pvld;

//     le_expn_data_in := Mux(io.cfg_lut_le_function === 0.U, io.lut_data_in, "b0".asUInt(32.W))
//     le_expn_cfg_start := Mux(io.cfg_lut_le_function === 0.U, io.cfg_lut_le_start, "b0".asUInt(32.W))
//     le_expn_cfg_offset := Mux(io.cfg_lut_le_function === 0.U, io. cfg_lut_le_index_offset, "b0".asUInt(8.W))

//     le_line_data_in := Mux(io.cfg_lut_le_function =/= 0.U, io.lut_data_in, "b0".asUInt(32.W))
//     le_line_cfg_start := Mux(io.cfg_lut_le_function =/= 0.U, io.cfg_lut_le_start, "b0".asUInt(32.W))
//     le_line_cfg_sel := Mux(io.cfg_lut_le_function =/= 0.U, io.cfg_lut_le_index_select, "b0".asUInt(8.W))

//     val le_oflow = Mux(io.cfg_lut_le_function === 0.U, le_expn_oflow, le_line_oflow)
//     val le_uflow = Mux(io.cfg_lut_le_function === 0.U, le_expn_uflow, le_line_uflow)

//     val le_index = Mux(io.cfg_lut_le_function === 0.U, le_expn_index, le_line_index)
//     val le_frac = Mux(io.cfg_lut_le_function === 0.U, le_expn_frac, le_line_frac)

//     val lo_oflow = lo_line_oflow
//     val lo_uflow = lo_line_uflow
//     val lo_index = lo_line_index
//     val lo_frac = lo_line_frac

//     //hit miss
//     val le_miss = (le_uflow | le_oflow)
//     val le_hit = !le_miss
//     val lo_miss = (lo_uflow | lo_oflow)
//     val lo_hit = !lo_miss

//     val lut_final_uflow = Wire(Bool())
//     val lut_final_oflow = Wire(Bool())
//     val lut_final_ram_sel = Wire(Bool())
//     val lut_final_ram_addr = Wire(UInt(9.W))
//     val lut_final_frac = Wire(UInt(35.W))

//     when(le_uflow & lo_uflow){
//         lut_final_uflow := Mux(io.cfg_lut_uflow_priority, lo_uflow, le_uflow)
//         lut_final_oflow := false.B 
//         lut_final_ram_sel := Mux(io.cfg_lut_uflow_priority, true.B, false.B)
//         lut_final_ram_addr := Mux(io.cfg_lut_uflow_priority, lo_index, le_index)
//         lut_final_frac := Mux(io.cfg_lut_uflow_priority, lo_frac, le_frac)
//     }
//     .elsewhen(le_oflow & lo_oflow){
//         lut_final_uflow := false.B
//         lut_final_oflow := Mux(io.cfg_lut_oflow_priority, lo_oflow, le_oflow)
//         lut_final_ram_sel := Mux(io.cfg_lut_oflow_priority, true.B, false.B)
//         lut_final_ram_addr := Mux(io.cfg_lut_uflow_priority, lo_index, le_index)
//         lut_final_frac := Mux(io.cfg_lut_oflow_priority, lo_frac, le_frac)
//     }
//     .elsewhen(le_hit & lo_hit){
//         lut_final_uflow := false.B
//         lut_final_oflow := false.B
//         lut_final_ram_sel := Mux(io.cfg_lut_hybrid_priority, true.B, false.B)
//         lut_final_ram_addr := Mux(io.cfg_lut_uflow_priority, lo_index, le_index)
//         lut_final_frac := Mux(io.cfg_lut_uflow_priority, lo_frac, le_frac)
//     }
//     .elsewhen(le_hit){
//         lut_final_uflow := false.B
//         lut_final_oflow := false.B
//         lut_final_ram_sel := false.B
//         lut_final_frac := le_frac
//         lut_final_ram_addr := le_index
//     }
//     .otherwise{//lo_hit
//         lut_final_uflow := false.B
//         lut_final_oflow := false.B
//         lut_final_ram_sel := true.B
//         lut_final_frac := lo_frac
//         lut_final_ram_addr := lo_index
//     }

//     val lut_out_pd = Wire(UInt(81.W))
//     val lut_final_pd = Cat(lo_hit, le_hit,lut_final_oflow, lut_final_uflow, lut_final_frac, lut_final_ram_addr, lut_final_ram_sel, lut_final_x)
//     io.lut_out_x := lut_out_pd(31, 0)
//     io.lut_out_ram_sel := lut_out_pd(32)
//     io.lut_out_ram_addr := lut_out_pd(41, 33)
//     io.lut_out_frac := lut_out_pd(76, 42)
//     io.lut_out_uflow := lut_out_pd(77)
//     io.lut_out_oflow := lut_out_pd(78)
//     io.lut_out_le_hit := lut_out_pd(79)
//     io.lut_out_lo_hit := lut_out_pd(80)

//     val pipe_p4 = Module{new NV_NVDLA_BC_pipe(81)}
//     pipe_p4.io.clk := io.nvdla_core_clk
//     pipe_p4.io.vi := lut_final_pvld
//     lut_final_prdy := pipe_p4.io.ro
//     pipe_p4.io.di := lut_final_pd
//     io.lut_out_pvld := pipe_p4.io.vo
//     pipe_p4.io.ri := io.lut_out_prdy
//     lut_out_pd := pipe_p4.io.dout



// }}


// object NV_NVDLA_SDP_HLS_Y_int_idxDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_idx)
// }