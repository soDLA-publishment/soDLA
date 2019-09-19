// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_int_inp extends Module {
//    val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         //in
//         val inp_in_pvld = Input(Bool())
//         val inp_in_prdy = Output(Bool())
//         val inp_bias_in = Input(UInt(32.W))
//         val inp_flow_in = Input(Bool())
//         val inp_frac_in = Input(UInt(35.W))
//         val inp_offset_in = Input(UInt(32.W))
//         val inp_scale_in = Input(UInt(16.W))
//         val inp_shift_in = Input(UInt(5.W))
//         val inp_x_in = Input(UInt(32.W))
//         val inp_y0_in = Input(UInt(16.W))
//         val inp_y1_in = Input(UInt(16.W))
//         //out
//         val inp_out_pvld = Output(Bool())
//         val inp_out_prdy = Input(Bool())
//         val inp_data_out = Output(UInt(32.W))

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

//     //overflow and unflow  interpolation
//     val inp_x_ext = Mux(io.inp_flow_in, Cat(Fill(2, io.inp_x_in(31)), io.inp_x_in), Fill(32+2, false.B))
//     val inp_offset_mux = Mux(io.inp_flow_in, Cat(io.inp_x_in(31), io.inp_x_in), Fill(32+1, false.B))
//     val inp_bias_mux = Mux(io.inp_flow_in, Cat(false.B, io.inp_x_in), Fill(32+1, false.B))
//     val inp_y0_mux = Mux(io.inp_flow_in, io.inp_y0_in, Fill(16, false.B))

//     val inp_ob_in = (inp_bias_mux.asSInt +& inp_offset_mux.asSInt).asUInt
//     val inp_xsub = (inp_bias_mux.asSInt + inp_ob_in.asSInt).asUInt
//     val flow_pd = Cat(inp_y0_mux, io.inp_shift_in, io.inp_scale_in, inp_xsub)

//     val xsub_prdy = Wire(Bool())
//     val pipe_p1 = Module{new NV_NVDLA_BC_pipe(71)}
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := io.inp_in_pvld
//     val inp_in_frdy = pipe_p1.io.ro
//     pipe_p1.io.di := flow_pd
//     val xsub_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := xsub_prdy
//     val flow_pd_reg = pipe_p1.io.dout
    
//     val inp_y0_reg = flow_pd_reg(70, 55)
//     val inp_shift_reg = flow_pd_reg(54, 50)
//     val inp_scale_reg = flow_pd_reg(49, 34)
//     val inp_xsub_reg = flow_pd_reg(33, 0)

//     val inp_mul_scale = (inp_xsub_reg.asSInt * inp_scale_reg.asSInt).asUInt //morework
//     val flow_pd2 = Cat(inp_y0_reg, inp_shift_reg, inp_mul_scale)

//     val mul_scale_prdy = Wire(Bool())
//     val pipe_p2 = Module{new NV_NVDLA_BC_pipe(71)}
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := xsub_pvld
//     xsub_prdy := pipe_p2.io.ro
//     pipe_p2.io.di := flow_pd2
//     val mul_scale_pvld = pipe_p2.io.vo
//     pipe_p2.io.ri := mul_scale_prdy
//     val flow_pd2_reg = pipe_p2.io.dout
    
//     val inp_y0_reg2 = flow_pd2_reg(70, 55)
//     val inp_shift_reg2 = flow_pd2_reg(54, 50)
//     val inp_mul_scale_reg = flow_pd2_reg(49, 0)

//     val intp_flow_shiftright_ss = Module{new NV_NVDLA_HLS_shiftrightss(IN_WIDTH = 32+16+2, OUT_WIDTH = 32, SHIFT_WIDTH = 5)}
//     intp_flow_shiftright_ss.io.data_in := inp_mul_scale_reg
//     intp_flow_shiftright_ss.io.shift_num := inp_shift_reg2
//     val inp_mul_tru = intp_flow_shiftright_ss.io.data_out

//     //signed
//     //signed

//     val inp_y0_sum = (inp_y0_reg2.asSInt +& inp_mul_tru.asSInt).asUInt

//     val inp_fout_prdy = Wire(Bool())
//     val pipe_p3 = Module{new NV_NVDLA_BC_pipe(33)}
//     pipe_p3.io.clk := io.nvdla_core_clk
//     pipe_p3.io.vi := mul_scale_pvld
//     mul_scale_prdy := pipe_p3.io.ro
//     pipe_p3.io.di := inp_y0_sum
//     val inp_fout_pvld = pipe_p3.io.vo
//     pipe_p3.io.ri := inp_fout_prdy
//     val inp_y0_sum_reg = pipe_p3.io.dout

//     val intp_flow_saturate = Module{new NV_NVDLA_HLS_saturate(IN_WIDTH = 32+1, OUT_WIDTH = 32)}
//     intp_flow_saturate.io.data_in := inp_y0_sum_reg
//     val inp_flow_dout = intp_flow_saturate.io.data_out

//     //hit interpolation
//     val frac_in = Mux(io.inp_flow_in, 0.U, io.inp_frac_in)  //unsigned  
//     val frac_remain = (1.U << 35.U) -& frac_in //unsigned 
//     val mul0 = (io.inp_y0_in.asSInt * Cat(false.B, frac_remain).asSInt).asUInt

//     val inp_in_mvld = Wire(Bool())
//     val mul0_prdy = Wire(Bool())
//     val pipe_p4 = Module{new NV_NVDLA_BC_pipe(53)}
//     pipe_p4.io.clk := io.nvdla_core_clk
//     pipe_p4.io.vi := inp_in_mvld
//     val inp_in_prdy0 = pipe_p4.io.ro
//     pipe_p4.io.di := mul0
//     val mul0_pvld = pipe_p4.io.vo
//     pipe_p4.io.ri := mul0_prdy
//     val mul0_reg = pipe_p4.io.dout

//     val mul1 = (io.inp_y1_in.asSInt * Cat(Fill(2, false.B), frac_in).asSInt).asUInt
//     val mul1_prdy = Wire(Bool())
//     val pipe_p5 = Module{new NV_NVDLA_BC_pipe(53)}
//     pipe_p5.io.clk := io.nvdla_core_clk
//     pipe_p5.io.vi := inp_in_mvld
//     val inp_in_prdy1 = pipe_p5.io.vo
//     pipe_p5.io.di := mul1
//     val mul1_pvld = pipe_p5.io.vo
//     pipe_p5.io.ri := mul1_prdy
//     val mul1_reg = pipe_p5.io.dout

//     val intp_sum = (mul0_reg.asSInt + mul1_reg.asSInt).asUInt
//     val sum_in_prdy = Wire(Bool())
//     mul0_prdy := sum_in_prdy
//     mul1_prdy := sum_in_prdy
//     val sum_in_pvld = mul0_pvld & mul1_pvld

//     val sum_out_prdy = Wire(Bool())
//     val pipe_p6 = Module{new NV_NVDLA_BC_pipe(53)}
//     pipe_p6.io.clk := io.nvdla_core_clk
//     pipe_p6.io.vi := sum_in_pvld
//     sum_in_prdy := pipe_p6.io.ro
//     pipe_p6.io.di := intp_sum
//     val sum_out_pvld = pipe_p6.io.vo
//     pipe_p6.io.ri := sum_out_prdy
//     val intp_sum_reg = pipe_p6.io.dout
    
//     val inp_shiftright_su = Module{new NV_NVDLA_HLS_shiftrightsu(IN_WIDTH = 35+16+2, OUT_WIDTH = 32, SHIFT_WIDTH = 6)}
//     inp_shiftright_su.io.data_in := intp_sum_reg
//     inp_shiftright_su.io.shift_num := "d35".asUInt(6.W)
//     val intp_sum_tru = inp_shiftright_su.io.data_out
//     //signed 
//     //unsigned 

//     val inp_mout_prdy = Wire(Bool())
//     val pipe_p7 = Module{new NV_NVDLA_BC_pipe(32)}
//     pipe_p7.io.clk := io.nvdla_core_clk
//     pipe_p7.io.vi := sum_out_pvld
//     sum_out_prdy := pipe_p7.io.ro
//     pipe_p7.io.di := intp_sum_tru
//     val inp_mout_pvld = pipe_p7.io.vo
//     pipe_p7.io.ri := inp_mout_prdy
//     val inp_nrm_dout = pipe_p7.io.dout

//     val inp_flow_prdy = Wire(Bool())
//     val inp_in_fvld =  io.inp_flow_in & inp_flow_prdy & io.inp_in_pvld;
//     inp_in_mvld := !io.inp_flow_in & inp_flow_prdy & io.inp_in_pvld;
//     val inp_flow_pvld = Mux(io.inp_flow_in, inp_in_frdy, inp_in_prdy0 & inp_in_prdy1) & io.inp_in_pvld
//     io.inp_in_prdy := Mux(io.inp_flow_in, inp_in_frdy, inp_in_prdy0 & inp_in_prdy1) & inp_flow_prdy

//     val flow_in_pipe3 = Wire(Bool())
//     val flow_pipe3_pvld = Wire(Bool())
//     inp_fout_prdy :=  flow_in_pipe3 & flow_pipe3_pvld & io.inp_out_prdy;
//     inp_mout_prdy := !flow_in_pipe3 & flow_pipe3_pvld & io.inp_out_prdy;
//     val flow_pipe3_prdy = Mux(flow_in_pipe3, inp_fout_pvld, inp_mout_pvld) & io.inp_out_prdy;

//     io.inp_out_pvld := Mux(flow_in_pipe3, inp_fout_pvld, inp_mout_pvld) & flow_pipe3_pvld
//     io.inp_data_out := Mux(flow_in_pipe3, inp_flow_dout, inp_nrm_dout)

//     val flow_pipe1_prdy = Wire(Bool())
//     val pipe_p8 = Module{new NV_NVDLA_BC_pipe(1)}
//     pipe_p8.io.clk := io.nvdla_core_clk
//     pipe_p8.io.vi := inp_flow_pvld
//     inp_flow_prdy := pipe_p8.io.ro
//     pipe_p8.io.di := io.inp_flow_in
//     val flow_pipe1_pvld = pipe_p8.io.vo
//     pipe_p8.io.ri := flow_pipe1_prdy
//     val flow_in_pipe1 = pipe_p8.io.dout

//     val flow_pipe2_prdy = Wire(Bool())
//     val pipe_p9 = Module{new NV_NVDLA_BC_pipe(1)}
//     pipe_p9.io.clk := io.nvdla_core_clk
//     pipe_p9.io.vi := flow_pipe1_pvld
//     flow_pipe1_prdy := pipe_p9.io.ro
//     pipe_p9.io.di := flow_in_pipe1
//     val flow_pipe2_pvld = pipe_p9.io.vo
//     pipe_p9.io.ri := flow_pipe2_prdy
//     val flow_in_pipe2 = pipe_p9.io.dout

//     val pipe_p10 = Module{new NV_NVDLA_BC_pipe(1)}
//     pipe_p10.io.clk := io.nvdla_core_clk
//     pipe_p10.io.vi := flow_pipe2_pvld
//     flow_pipe2_prdy := pipe_p10.io.ro
//     pipe_p10.io.di := flow_in_pipe2
//     flow_pipe3_pvld := pipe_p10.io.vo
//     pipe_p10.io.ri := flow_pipe3_prdy
//     flow_in_pipe3 := pipe_p10.io.dout

// }}


// object NV_NVDLA_SDP_HLS_Y_int_inpDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_inp)
// }
