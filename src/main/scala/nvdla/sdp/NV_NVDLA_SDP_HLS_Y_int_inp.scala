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
//     val pipe_p3 = Module{new NV_NVDLA_SDP_HLS_Y_INT_INP_pipe_p3}














// }}

