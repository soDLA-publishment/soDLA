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









// }}


// object NV_NVDLA_SDP_HLS_Y_int_idxDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_idx)
// }