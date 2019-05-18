// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_X_int_mul extends Module {
//    val LUT_DEPTH = 256
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val alu_data_out = Input(UInt(33.W))
//         val alu_out_pvld = Input(Bool())

//         val cfg_mul_bypass = Input(Bool())
//         val cfg_mul_op = Input(UInt(16.W))
//         val cfg_mul_prelu = Input(Bool())
//         val cfg_mul_src = Input(Bool())
//         val chn_mul_op = Input(UInt(16.W))

//         val mul_op_pvld = Input(Bool())
//         val mul_op_prdy = Output(Bool())
//         val mul_out_pvld = Output(Bool())
//         val mul_out_prdy = Input(Bool())
//         val mul_data_out = Output(UInt(49.W))
        
//         val alu_out_prdy = Output(Bool())
//         val bypass_trt_out = Output(Bool())

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

//     val mul_sync_prdy = Wire(Bool())
//     val x_mul_sync2data = Module{new NV_NVDLA_SDP_HLS_sync2data(16, 33)}
//     x_mul_sync2data.io.chn1_en := io.cfg_mul_src & !io.cfg_mul_bypass
//     x_mul_sync2data.io.chn2_en := !io.cfg_mul_bypass
//     x_mul_sync2data.io.chn1_in_pvld := io.mul_op_pvld
//     io.mul_op_prdy := x_mul_sync2data.io.chn1_in_prdy
//     x_mul_sync2data.io.chn2_in_pvld := io.alu_out_pvld
//     val alu_out_srdy = x_mul_sync2data.io.chn2_in_prdy
//     val mul_sync_pvld = x_mul_sync2data.io.chn_out_pvld    
//     x_mul_sync2data.io.chn_out_prdy := mul_sync_prdy        
//     io.chn_mul_op := x_mul_sync2data.io.data1_in 
//     io.alu_data_out :=  x_mul_sync2data.io.data2_in
//     val mul_op_sync = x_mul_sync2data.io.data1_out
//     val mul_data_sync = x_mul_sync2data.io.data2_out

//     val bypass_trt = io.cfg_mul_prelu & !mul_data_sync(32)

//     val mul_op_in = Mux(io.cfg_mul_src === 0.U, io.cfg_mul_op, mul_op_sync)
//     val mul_data_in = io.mul_data_sync

//     val x_mul_prelu = Module{new NV_NVDLA_SDP_HLS_prelu(33, 49, 16)}
//     x_mul_prelu.io.cfg_prelu_en := io.cfg_mul_prelu
//     x_mul_prelu.io.data_in := mul_data_in
//     x_mul_prelu.io.op_in := mul_op_in
//     val mul_prelu_out = x_mul_prelu.io.data_out

//     val mul_final_prdy = Wire(Bool())
//     val pipe_p1 = Module{new NV_NVDLA_SDP_HLS_X_INT_MUL_pipe_p1}
//     pipe_p1.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p1.io.bypass_trt := bypass_trt
//     pipe_p1.io.mul_final_prdy := mul_final_prdy




// }