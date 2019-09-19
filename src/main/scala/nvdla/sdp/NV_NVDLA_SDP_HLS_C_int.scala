// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_C_int extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val cvt_in = Flipped(DecoupledIO(UInt(32.W)))

//         val cvt_out = DecoupledIO(UInt(17.W))

//         val cfg_mode_eql = Input(Bool())
//         val cfg_offset = Input(UInt(32.W))
//         val cfg_out_precision = Input(UInt(2.W))
//         val cfg_scale = Input(UInt(16.W))
//         val cfg_truncate = Input(UInt(6.W))

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

//     val cvt_data_mux = Mux(io.cfg_mode_eql, 0.U, io.cvt_in.bits)
//     val cfg_offset_mux = Mux(io.cfg_mode_eql, 0.U, io.cfg_offset)
//     val cfg_scale_mux = Mux(io.cfg_mode_eql, 0.U, io.cfg_scale)

//     //sub
//     val sub_dout = (cvt_data_mux.asSInt -& cfg_offset_mux.asSInt).asUInt
//     val sub_in_pvld = Wire(Bool())
//     val sub_out_prdy = Wire(Bool())
//     val pipe_p1 = Module{new NV_NVDLA_BC_pipe(33)}
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := sub_in_pvld
//     val sub_in_prdy = pipe_p1.io.ro
//     pipe_p1.io.di := sub_dout
//     val sub_out_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := sub_out_prdy
//     val sub_data_out = pipe_p1.io.dout

//     //mul 
//     val mul_dout = (sub_data_out.asSInt * cfg_scale_mux.asSInt).asUInt
//     val mul_out_prdy = Wire(Bool())
//     val pipe_p2 = Module{new NV_NVDLA_BC_pipe(49)}
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := sub_out_pvld
//     sub_out_prdy := pipe_p2.io.ro
//     pipe_p2.io.di := mul_dout
//     val mul_out_pvld = pipe_p2.io.vo
//     pipe_p2.io.ri := mul_out_prdy
//     val mul_data_out = pipe_p2.io.dout

//     //truncate
//     val c_shiftrightsat_su = Module{new NV_NVDLA_HLS_shiftrightsatsu(49, 17, 6)}
//     c_shiftrightsat_su.io.data_in := mul_data_out
//     c_shiftrightsat_su.io.shift_num := io.cfg_truncate
//     val tru_dout = c_shiftrightsat_su.io.data_out
//     val sat_dout = c_shiftrightsat_su.io.sat_out

//     //signed 
//     //unsigned
//     val pipe_p3_data_in = Cat(sat_dout, tru_dout)
//     val tru_out_prdy = Wire(Bool())
//     val pipe_p3 = Module{new NV_NVDLA_BC_pipe(18)}
//     pipe_p3.io.clk := io.nvdla_core_clk
//     pipe_p3.io.vi := mul_out_pvld
//     mul_out_prdy := pipe_p3.io.ro
//     pipe_p3.io.di := pipe_p3_data_in
//     val tru_out_pvld = pipe_p3.io.vo
//     pipe_p3.io.ri := tru_out_prdy
//     val pipe_p3_data_out = pipe_p3.io.dout

//     val tru_out = pipe_p3_data_out(16, 0)
//     val sat_out = pipe_p3_data_out(17)

//     val c_saturate_int16 = Module{new NV_NVDLA_HLS_saturate(17, 16)}
//     c_saturate_int16.io.data_in := tru_out
//     val dout_int16_sat = c_saturate_int16.io.data_out

//     val c_saturate_int8 = Module{new NV_NVDLA_HLS_saturate(17, 8)}
//     c_saturate_int8.io.data_in := tru_out
//     val dout_int8_sat = c_saturate_int8.io.data_out

//     val final_out_prdy = Wire(Bool())
//     sub_in_pvld := Mux(io.cfg_mode_eql, false.B, io.cvt_in_pvld)
//     io.cvt_in_prdy := Mux(io.cfg_mode_eql, final_out_prdy, sub_in_prdy)
//     tru_out_prdy := Mux(io.cfg_mode_eql, true.B, final_out_prdy)
//     val final_out_pvld = Mux(io.cfg_mode_eql, io.cvt_in_pvld, tru_out_pvld)

//     val cvt_dout = Mux(io.cfg_mode_eql, io.cvt_data_in,
//                    Mux(io.cfg_out_precision === 1.U, dout_int16_sat, 
//                    Cat(Fill(8, dout_int8_sat(7)), dout_int8_sat)))
//     val cvt_sat = Mux(io.cfg_mode_eql, false.B, sat_out)

//     val pipe_p4_data_in = Cat(cvt_sat, cvt_dout)
//     val pipe_p4 = Module{new NV_NVDLA_BC_pipe(17)}
//     pipe_p4.io.clk := io.nvdla_core_clk
//     pipe_p4.io.vi := final_out_pvld
//     final_out_prdy := pipe_p4.io.ro
//     pipe_p4.io.di := pipe_p4_data_in
//     io.cvt_out_pvld := pipe_p4.io.vo
//     pipe_p4.io.ri := io.cvt_out_prdy
//     val pipe_p4_data_out = pipe_p4.io.dout

//     io.cvt_data_out := pipe_p4_data_out(15, 0)
//     io.cvt_sat_out := pipe_p4_data_out(16)


// }}



// object NV_NVDLA_SDP_HLS_C_intDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_C_int)
// }