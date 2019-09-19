// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_X_int_mul extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val alu_data_out = Flipped(DecoupledIO(UInt(33.W)))
//         val chn_mul_op = Flipped(DecoupledIO(UInt(16.W)))
//         val mul_data_out = DecoupledIO(UInt(49.W))

//         val cfg_mul_bypass = Input(Bool())
//         val cfg_mul_op = Input(UInt(16.W))
//         val cfg_mul_prelu = Input(Bool())
//         val cfg_mul_src = Input(Bool())

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
//     x_mul_sync2data.io.data1_in := io.chn_mul_op
//     x_mul_sync2data.io.data2_in := io.alu_data_out
//     val mul_op_sync = x_mul_sync2data.io.data1_out
//     val mul_data_sync = x_mul_sync2data.io.data2_out

//     val bypass_trt = io.cfg_mul_prelu & !mul_data_sync(32)

//     val mul_op_in = Mux(io.cfg_mul_src === 0.U, io.cfg_mul_op, mul_op_sync)
//     val mul_data_in = mul_data_sync

//     val x_mul_prelu = Module{new NV_NVDLA_SDP_HLS_prelu(33, 49, 16)}
//     x_mul_prelu.io.cfg_prelu_en := io.cfg_mul_prelu
//     x_mul_prelu.io.data_in := mul_data_in
//     x_mul_prelu.io.op_in := mul_op_in
//     val mul_prelu_out = x_mul_prelu.io.data_out

//     val mul_final_prdy = Wire(Bool())
//     val pipe_p1_data_in = Cat(mul_prelu_out, bypass_trt)
//     val pipe_p1 = Module{new NV_NVDLA_BC_pipe(50)}
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := mul_sync_pvld
//     mul_sync_prdy := pipe_p1.io.ro
//     pipe_p1.io.di := pipe_p1_data_in
//     val mul_final_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := mul_final_prdy
//     val pipe_p1_data_out = pipe_p1.io.dout
//     val bypass_trt_reg = pipe_p1_data_out(0)
//     val mul_data_final = pipe_p1_data_out(49, 1)


//     io.alu_out_prdy := Mux(io.cfg_mul_bypass, io.mul_out_prdy, alu_out_srdy)
//     mul_final_prdy := Mux(io.cfg_mul_bypass, true.B, io.mul_out_prdy)
//     io.mul_out_pvld := Mux(io.cfg_mul_bypass, io.alu_out_pvld, mul_final_pvld)
//     io.bypass_trt_out := Mux(io.cfg_mul_bypass, false.B, bypass_trt_reg)
//     io.mul_data_out := Mux(io.cfg_mul_bypass, Cat(Fill(16, io.alu_data_out(32)), io.alu_data_out), mul_data_final)

// }}

// object NV_NVDLA_SDP_HLS_X_int_mulDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_X_int_mul)
// }