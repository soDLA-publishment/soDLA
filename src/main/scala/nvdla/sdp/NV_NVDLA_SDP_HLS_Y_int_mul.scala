// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_int_mul extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val cfg_mul_bypass = Input(Bool())
//         val cfg_mul_op = Input(UInt(32.W))
//         val cfg_mul_prelu = Input(Bool())
//         val cfg_mul_src = Input(Bool())
//         val cfg_mul_truncate = Input(UInt(10.W))

//         val chn_in_pvld = Input(Bool())
//         val chn_in_prdy = Output(Bool())
//         val chn_mul_in = Input(UInt(32.W))

//         val chn_mul_op_pvld = Input(Bool())
//         val chn_mul_op_prdy = Output(Bool())
//         val chn_mul_op = Input(UInt(32.W))

//         val mul_out_pvld = Output(Bool())
//         val mul_out_prdy = Input(Bool())
//         val mul_data_out = Output(UInt(32.W))

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

//     val chn_in_srdy = Wire(Bool())
//     val mul_sync_prdy = Wire(Bool())
//     val y_mul_sync2data = Module{new NV_NVDLA_SDP_HLS_sync2data(32, 32)}
//     y_mul_sync2data.io.chn1_en := !io.cfg_mul_bypass & io.cfg_mul_src
//     y_mul_sync2data.io.chn2_en := !io.cfg_mul_bypass
//     y_mul_sync2data.io.chn1_in_pvld := io.chn_mul_op_pvld
//     io.chn_mul_op_prdy := y_mul_sync2data.io.chn1_in_prdy
//     y_mul_sync2data.io.chn2_in_pvld := io.chn_in_pvld
//     chn_in_srdy := y_mul_sync2data.io.chn2_in_prdy
//     val mul_sync_pvld = y_mul_sync2data.io.chn_out_pvld    
//     y_mul_sync2data.io.chn_out_prdy := mul_sync_prdy        
//     y_mul_sync2data.io.data1_in := io.chn_mul_op 
//     y_mul_sync2data.io.data2_in := io.chn_mul_in
//     val mul_op_sync = y_mul_sync2data.io.data1_out
//     val mul_data_sync = y_mul_sync2data.io.data2_out

//     val mul_data_in = mul_data_sync
//     val mul_op_in = Mux(io.cfg_mul_src === 0.U, io.cfg_mul_op, mul_op_sync)

//     val x_mul_prelu = Module{new NV_NVDLA_SDP_HLS_prelu}
//     x_mul_prelu.io.cfg_prelu_en := io.cfg_mul_prelu
//     x_mul_prelu.io.data_in := mul_data_in
//     x_mul_prelu.io.op_in := mul_op_in
//     val mul_prelu_dout = x_mul_prelu.io.data_out
    
//     val mul_prelu_prdy = Wire(Bool())
//     val pipe_p1_data_in = Cat(mul_data_in, mul_prelu_dout)
//     val pipe_p1 = Module{new NV_NVDLA_BC_pipe(96)}
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := mul_sync_pvld
//     mul_sync_prdy := pipe_p1.io.ro
//     pipe_p1.io.di := pipe_p1_data_in
//     val mul_prelu_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := mul_prelu_prdy
//     val pipe_p1_data_out = pipe_p1.io.dout
//     val mul_prelu_out = pipe_p1_data_out(63, 0)
//     val mul_data_reg = pipe_p1_data_out(95, 64)

//     val y_mul_shiftright_su = Module{new NV_NVDLA_HLS_shiftrightsu(64, 32, 10)}
//     y_mul_shiftright_su.io.data_in := mul_prelu_out
//     y_mul_shiftright_su.io.shift_num := io.cfg_mul_truncate
//     val mul_truncate_out = y_mul_shiftright_su.io.data_out

//     //signed 
//     //unsigned 
//     val mul_dout = Wire(UInt(32.W))
//     when(io.cfg_mul_prelu & !mul_data_reg(31)){
//         mul_dout := mul_data_reg
//     }
//     .otherwise{
//         mul_dout := mul_truncate_out
//     }

//     val mul_final_prdy = Wire(Bool())
//     val pipe_p2 = Module{new NV_NVDLA_BC_pipe(32)}
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := mul_prelu_pvld
//     mul_prelu_prdy := pipe_p2.io.ro
//     pipe_p2.io.di := mul_dout
//     val mul_final_pvld = pipe_p2.io.vo
//     pipe_p2.io.ri := mul_final_prdy
//     val mul_data_final = pipe_p2.io.dout
    
//     io.chn_in_prdy := Mux(io.cfg_mul_bypass, io.mul_out_prdy, chn_in_srdy)
//     mul_final_prdy := Mux(io.cfg_mul_bypass, true.B, io.mul_out_prdy)
//     io.mul_out_pvld := Mux(io.cfg_mul_bypass, io.chn_in_pvld, mul_final_pvld)
//     io.mul_data_out := Mux(io.cfg_mul_bypass, io.chn_mul_in, mul_data_final)
// }}


// object NV_NVDLA_SDP_HLS_Y_int_mulDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_mul)
// }