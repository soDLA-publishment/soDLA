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

//         val cvt_in_pvld = Input(Bool())
//         val cvt_in_prdy = Output(Bool())
//         val cvt_data_in = Input(UInt(16.W))

//         val cvt_out_pvld = Output(Bool())
//         val cvt_out_prdy = Input(Bool())
//         val cvt_data_out = Output(UInt(32.W))
        
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
//     val cfg_scale = Mux(io.cfg_cvt_bypass, 0.U, io.cfg_cvt_scale)
//     val cfg_truncate = Mux(io.cfg_cvt_bypass, 0.U, io.cfg_cvt_truncate)

//     val cfg_offset_ext = Mux(io.cfg_cvt_bypass, 0.U, Cat(io.cfg_cvt_offset(31), io.cfg_cvt_offset))
//     val cvt_data_ext = Mux(io.cfg_cvt_bypass, 0.U, Cat(Fill(17, io.cvt_data_in(15)), io.cvt_data_in))

//     val sub_in_pvld = Wire(Bool())
//     val sub_out_prdy = Wire(Bool())
//     val sub_dout = (cvt_data_ext.asSInt - cfg_offset_ext.asSInt).asUInt

//     val pipe_p1 = Module(new NV_NVDLA_BC_pipe(33))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := sub_in_pvld
//     val sub_in_prdy = pipe_p1.io.ro
//     pipe_p1.io.di := sub_dout
//     val sub_out_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := sub_out_prdy
//     val sub_data_out = pipe_p1.io.dout

//     val mul_out_prdy = Wire(Bool())
//     //mul 
//     val mul_dout = (sub_data_out.asSInt*cfg_scale.asSInt).asUInt
    
//     val pipe_p2 = Module{new NV_NVDLA_BC_pipe(49)}
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := sub_out_pvld
//     sub_out_prdy := pipe_p2.io.ro
//     pipe_p2.io.di := mul_dout
//     val mul_out_pvld = pipe_p2.io.vo
//     pipe_p2.io.ri := mul_out_prdy
//     val mul_data_out = pipe_p2.io.dout


//     //truncate
//     val y_mul_shiftright_su = Module{new NV_NVDLA_HLS_shiftrightsu(33 + 16, 32, 6)}
//     y_mul_shiftright_su.io.data_in := mul_data_out
//     y_mul_shiftright_su.io.shift_num := cfg_truncate
//     val tru_dout = y_mul_shiftright_su.io.data_out

//     //signed 
//     //unsigned 
//     val final_out_prdy = Wire(Bool())
//     sub_in_pvld := Mux(io.cfg_cvt_bypass, false.B, io.cvt_in_pvld)
//     io.cvt_in_prdy := Mux(io.cfg_cvt_bypass, final_out_prdy, sub_in_prdy) 
//     mul_out_prdy := Mux(io.cfg_cvt_bypass, true.B, final_out_prdy)
//     val final_out_pvld = Mux(io.cfg_cvt_bypass, io.cvt_in_pvld, mul_out_pvld)
//     val cvt_dout = Mux(io.cfg_cvt_bypass, Cat(Fill(16, io.cvt_data_in(15)), io.cvt_data_in), tru_dout)   

//     val pipe_p3 = Module{new NV_NVDLA_BC_pipe(32)}
//     pipe_p3.io.clk := io.nvdla_core_clk
//     pipe_p3.io.vi := final_out_pvld
//     final_out_prdy := pipe_p3.io.ro
//     pipe_p3.io.di := cvt_dout
//     io.cvt_out_pvld := pipe_p3.io.vo
//     pipe_p3.io.ri := io.cvt_out_prdy
//     io.cvt_data_out := pipe_p3.io.dout
// }}


// object NV_NVDLA_SDP_HLS_Y_int_cvtDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_cvt)
// }