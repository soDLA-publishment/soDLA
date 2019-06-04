// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_int_cvt extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val cfg_cvt_bypass = Input(Bool())
//         val cfg_cvt_offset = Input(UInt(32.W))
//         val cfg_cvt_scale = Input(UInt(16.W))
//         val cfg_cvt_truncate = Input(UInt(6.W))

//         val chn_in_pvld = Input(Bool())
//         val cvt_data_in = Input(UInt(16.W))
//         val chn_in_prdy = Output(Bool())

//         val cvt_out_pvld = Output(Bool())
//         val cvt_data_out = Output(UInt(32.W))
//         val cvt_out_prdy = Input(Bool())
        
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

//     //sub
//     val cfg_scale = Mux(io.cfg_cvt_bypass, "b0".asUInt(16.W), io.cfg_cvt_scale)
//     val cfg_truncate = Mux(io.cfg_cvt_bypass, "b0".asUInt(6.W), io.cfg_cvt_truncate)

//     val cfg_offset_ext

//     val y_mul_sync2data = Module{new NV_NVDLA_SDP_HLS_sync2data(32, 32)}
//     y_mul_sync2data.io.chn1_en := !io.cfg_mul_bypass & !io.cfg_mul_src
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

//     val mul_final_prdy = Wire(Bool())
//     val mul_prelu_prdy = Wire(Bool())

//     val pipe_p1 = Module{new NV_NVDLA_SDP_HLS_Y_INT_MUL_pipe_p1}
//     pipe_p1.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p1.io.mul_data_in := mul_data_in
//     pipe_p1.io.mul_prelu_dout := mul_prelu_dout
//     pipe_p1.io.mul_prelu_prdy := mul_prelu_prdy
//     pipe_p1.io.mul_sync_pvld := mul_sync_pvld
//     val mul_data_reg = pipe_p1.io.mul_data_reg
//     val mul_prelu_out = pipe_p1.io.mul_prelu_out
//     val mul_prelu_pvld = pipe_p1.io.mul_prelu_pvld
//     mul_sync_prdy := pipe_p1.io.mul_sync_prdy

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

//     val pipe_p2 = Module{new NV_NVDLA_SDP_HLS_Y_INT_MUL_pipe_p2}
//     pipe_p2.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p2.io.mul_dout := mul_dout
//     pipe_p2.io.mul_final_prdy := mul_final_prdy
//     pipe_p2.io.mul_prelu_pvld := mul_prelu_pvld
//     val mul_data_final = pipe_p2.io.mul_data_final
//     val mul_final_pvld = pipe_p2.io.mul_final_pvld
//     mul_prelu_prdy := pipe_p2.io.mul_prelu_prdy

//     io.chn_in_prdy := Mux(io.cfg_mul_bypass, io.mul_out_prdy, chn_in_srdy)
//     mul_final_prdy := Mux(io.cfg_mul_bypass, true.B, io.mul_out_prdy)
//     io.mul_out_pvld := Mux(io.cfg_mul_bypass, io.chn_in_pvld, mul_final_pvld)
//     io.mul_data_out := Mux(io.cfg_mul_bypass, io.chn_mul_in, mul_data_final)
// }}


// class NV_NVDLA_SDP_HLS_Y_INT_MUL_pipe_p1 extends Module {
//    val io = IO(new Bundle {

//         val nvdla_core_clk = Input(Clock())

//         val mul_sync_pvld = Input(Bool())
//         val mul_sync_prdy = Output(Bool())
//         val mul_data_in = Input(UInt(32.W))
//         val mul_prelu_dout = Input(UInt(64.W))
        
//         val mul_prelu_pvld = Output(Bool())
//         val mul_prelu_prdy = Input(Bool())
//         val mul_data_reg = Output(UInt(32.W))
//         val mul_prelu_out = Output(UInt(64.W))
//     })

//   val bc_pipe_0 = Module(new NV_NVDLA_BC_pipe(32))
//   bc_pipe_0.io.clk := io.nvdla_core_clk
//   bc_pipe_0.io.vi := io.mul_sync_pvld
//   io.mul_sync_prdy := bc_pipe_0.io.ro
//   bc_pipe_0.io.di := io.mul_data_in
//   io.mul_prelu_pvld := bc_pipe_0.io.vo
//   bc_pipe_0.io.ri := io.mul_prelu_prdy
//   io.mul_data_reg := bc_pipe_0.io.dout

//   val bc_pipe_1 = Module(new NV_NVDLA_BC_pipe(64))
//   bc_pipe_1.io.clk := io.nvdla_core_clk
//   bc_pipe_1.io.vi := io.mul_sync_pvld
//   io.mul_sync_prdy := bc_pipe_1.io.ro
//   bc_pipe_1.io.di := io.mul_prelu_dout
//   io.mul_prelu_pvld := bc_pipe_1.io.vo
//   bc_pipe_1.io.ri := io.mul_prelu_prdy
//   io.mul_prelu_out := bc_pipe_1.io.dout

// }

// class NV_NVDLA_SDP_HLS_Y_INT_MUL_pipe_p2 extends Module {
//    val io = IO(new Bundle {

//         val nvdla_core_clk = Input(Clock())

//         val mul_prelu_pvld = Input(Bool())
//         val mul_prelu_prdy = Output(Bool())
//         val mul_dout = Input(UInt(32.W))
        
//         val mul_final_pvld = Output(Bool())
//         val mul_final_prdy = Input(Bool())
//         val mul_data_final = Output(UInt(32.W))
//     })

//   val bc_pipe = Module(new NV_NVDLA_BC_pipe(32))

//   bc_pipe.io.clk := io.nvdla_core_clk

//   bc_pipe.io.vi := io.mul_prelu_pvld
//   io.mul_prelu_prdy := bc_pipe.io.ro
//   bc_pipe.io.di := io.mul_dout

//   io.mul_final_pvld := bc_pipe.io.vo
//   bc_pipe.io.ri := io.mul_final_prdy
//   io.mul_data_final := bc_pipe.io.dout

// }

// object NV_NVDLA_SDP_HLS_Y_int_mulDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_mul)
// }