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

//     val inp_ob_in = inp_bias_mux.asSInt +& inp_offset_mux.asSInt
//     val inp_xsub = inp_bias_mux.asSInt + inp_ob_in
//     val flow_pd = Cat(inp_y0_mux, io.inp_shift_in, io.inp_scale_in, inp_xsub)

//     val xsub_prdy = Wire(Bool())
//     val pipe_p1 = Module{new NV_NVDLA_SDP_HLS_Y_INT_INP_pipe_p1}
//     pipe_p1.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p1.io.flow_pd := flow_pd
//     pipe_p1.io.inp_in_pvld := io.inp_in_pvld
//     val inp_in_frdy = pipe_p1.io.inp_in_frdy
//     val flow_pd_reg = pipe_p1.io.flow_pd_reg
//     val xsub_pvld = pipe_p1.io.xsub_pvld
//     pipe_p1.io.xsub_prdy := xsub_prdy

//     val inp_y0_reg = flow_pd_reg(70, 55)
//     val inp_shift_reg = flow_pd_reg(54, 50)
//     val inp_scale_reg = flow_pd_reg(49, 34)
//     val inp_xsub_reg = flow_pd_reg(33, 0)

//     val inp_mul_scale = inp_xsub_reg.asSInt * inp_scale_reg.asSInt //morework
//     val flow_pd2 = Cat(inp_y0_reg, inp_shift_reg, inp_mul_scale)

//     val mul_scale_prdy = Wire(Bool())
//     val pipe_p2 = Module{new NV_NVDLA_SDP_HLS_Y_INT_INP_pipe_p2}
//     pipe_p2.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p2.io.flow_pd2 := flow_pd2
//     pipe_p2.io.mul_scale_prdy := mul_scale_prdy
//     pipe_p2.io.xsub_pvld = xsub_pvld
//     val flow_pd2_reg = pipe_p2.io.flow_pd2_reg
//     val mul_scale_pvld = pipe_p2.io.mul_scale_pvld
//     val xsub_prdy = pipe_p2.io.xsub_prdy

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
//     val pipe_p3 = Module{new NV_NVDLA_SDP_HLS_Y_INT_INP_pipe_p3}
//     pipe_p3.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p3.io.inp_y0_sum := inp_y0_sum
//     pipe_p3.io.mul_scale_pvld := mul_scale_pvld
//     val mul_scale_prdy = pipe_p3.io.mul_scale_prdy
//     val inp_fout_pvld = pipe_p3.io.inp_fout_pvld
//     pipe_p3.io.inp_out_prdy := inp_fout_prdy
//     val inp_y0_sum_reg = pipe_p3.io.inp_y0_sum_reg

//     val intp_flow_saturate = Module{new NV_NVDLA_HLS_saturate(IN_WIDTH = 32+16+2, OUT_WIDTH = 32)}
//     intp_flow_saturate.io.data_in := inp_y0_sum_reg
//     val inp_flow_dout = intp_flow_saturate.io.data_out

//     //hit interpolation
//     val frac_in = Mux(io.inp_flow_in, 0.U, io.inp_frac_in)  //unsigned  
//     val frac_remain = 1.U << 35.U -& frac_in //unsigned 
//     val mul0 = (inp_y0_in.asSInt * Cat(false.B, frac_remain).asSInt).asUInt

//     val inp_in_mvld = Wire(Bool())
//     val mul0_prdy = Wire(Bool())
//     val pipe_p4 = Module{new NV_NVDLA_SDP_HLS_Y_INT_INP_pipe_p4}
//     pipe_p4.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p4.io.inp_in_pvld := inp_in_mvld
//     val inp_in_prdy0 = pipe_p4.io.inp_in_prdy0
//     pipe_p4.io.mul0 := mul0
//     val mul0_pvld = pipe_p4.io.mul0_pvld
//     val mul0_reg = pipe_p4.io.mul0_reg

//     val mul1 = (inp_y1_in.asSInt * Cat(Fill(2, false.B), frac_in).asSInt).asUInt
//     val mul1_prdy = Wire(Bool())
//     val pipe_p5 = Module{new NV_NVDLA_SDP_HLS_Y_INT_INP_pipe_p5}
//     pipe_p5.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p5.io.inp_in_pvld := inp_in_mvld
//     val inp_in_prdy1 = pipe_p5.io.inp_in_prdy1
//     pipe_p5.io.mul1 := mul1
//     pipe_p5.io.mul1_prdy := mul1_prdy
//     val mul1_pvld = pipe_p5.io.mul1_pvld
//     val mul1_reg = pipe_p5.io.mul1_reg

//     val intp_sum = (mul0_reg.asSInt + mul1_reg.asSInt).asUInt
//     val sum_in_prdy = Wire(Bool())
//     mul0_prdy := sum_in_prdy
//     mul1_prdy := sum_in_prdy
//     val sum_in_pvld = mul0_pvld & mul1_pvld

//     val sum_out_prdy = Wire(Bool())
//     val pipe_p6 = Module{new NV_NVDLA_SDP_HLS_Y_INT_INP_pipe_p6}
//     pipe_p6.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p6.io.intp_sum := intp_sum
//     pipe_p6.io.sum_in_pvld := sum_in_pvld
//     pipe_p6.io.sum_out_prdy := sum_out_prdy
//     val intp_sum_reg = pipe_p6.io.intp_sum_reg
//     sum_in_prdy := pipe_p6.io.sum_in_prdy
//     val sum_out_pvld = pipe_p6.io.sum_out_pvld

//     val inp_shiftright_su = Module{new NV_NVDLA_HLS_shiftrightsu(IN_WIDTH = 35+16+2, OUT_WIDTH = 32, SHIFT_WIDTH = 6)}
//     inp_shiftright_su.io.data_in := intp_sum_reg
//     inp_shiftright_su.io.shift_num := "d35".asUInt(6.W)
//     val intp_sum_tru = inp_shiftright_su.io.data_out
//     //signed 
//     //unsigned 

//     val inp_mout_prdy = Wire(Bool())
//     val pipe_p7 = Module{new NV_NVDLA_SDP_HLS_Y_INT_INP_pipe_p7}
//     pipe_p7.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p7.io.intp_sum_tru := intp_sum_tru
//     pipe_p7.io.sum_out_pvld := sum_out_pvld
//     sum_out_prdy := pipe_p7.io.sum_out_prdy
//     val inp_mout_pvld = pipe_p7.io.inp_mout_pvld
//     pipe_p7.io.inp_out_prdy := inp_mout_prdy
//     val inp_nrm_dout = pipe_p7.io.inp_nrm_dout

//     val inp_flow_prdy = Wire(Bool())
//     val inp_in_fvld =  io.inp_flow_in & inp_flow_prdy & io.inp_in_pvld;
//     inp_in_mvld := !io.inp_flow_in & inp_flow_prdy & io.inp_in_pvld;
//     val inp_flow_pvld = Mux(io.inp_flow_in, inp_in_frdy, inp_in_prdy0 & inp_in_prdy1) & io.inp_in_pvld
//     val inp_in_prdy = Mux()

    




















// }}

