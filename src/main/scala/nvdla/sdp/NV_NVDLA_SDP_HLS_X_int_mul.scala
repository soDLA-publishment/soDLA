package nvdla

import chisel3._
import chisel3.util._

class sdp_x_int_mul_cfg_if extends Bundle{
    val bypass = Output(Bool())
    val op = Output(UInt(16.W))
    val prelu = Output(Bool())
    val src = Output(Bool())
    val shift_value = Output(UInt(6.W))
}

class NV_NVDLA_SDP_HLS_X_int_mul extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val alu_data_out = Flipped(DecoupledIO(UInt(33.W)))
        val chn_mul_op = Flipped(DecoupledIO(UInt(16.W)))
        val mul_data_out = DecoupledIO(UInt(49.W))

        val cfg_mul = Flipped(new sdp_x_int_mul_cfg_if)

        val bypass_trt_out = Output(Bool())

    })
    //     
    //          ┌─┐       ┌─┐
    //       ┌──┘ ┴───────┘ ┴──┐
    //       │                 │
    //       │       ───       │          
    //       │  ─┬┘       └┬─  │
    //       │                 │
    //       │       ─┴─       │
    //       │                 │
    //       └───┐         ┌───┘
    //           │         │
    //           │         │
    //           │         │
    //           │         └──────────────┐
    //           │                        │
    //           │                        ├─┐
    //           │                        ┌─┘    
    //           │                        │
    //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
    //             │ ─┤ ─┤       │ ─┤ ─┤         
    //             └──┴──┘       └──┴──┘ 
withClock(io.nvdla_core_clk){

    val mul_sync_prdy = Wire(Bool())
    val x_mul_sync2data = Module{new NV_NVDLA_HLS_sync2data(16, 33)}
    x_mul_sync2data.io.chn1_en := io.cfg_mul.src & !io.cfg_mul.bypass
    x_mul_sync2data.io.chn1_in <> io.chn_mul_op
    x_mul_sync2data.io.chn2_en := !io.cfg_mul.bypass
    x_mul_sync2data.io.chn2_in.valid := io.alu_data_out.valid
    val alu_data_out_srdy = x_mul_sync2data.io.chn2_in.ready
    x_mul_sync2data.io.chn2_in.bits := io.alu_data_out.bits
    val mul_sync_pvld = x_mul_sync2data.io.chn_out.valid   
    x_mul_sync2data.io.chn_out.ready := mul_sync_prdy        
    val mul_op_sync = x_mul_sync2data.io.chn_out.bits.data1
    val mul_data_sync = x_mul_sync2data.io.chn_out.bits.data2

    val bypass_trt = io.cfg_mul.prelu & !mul_data_sync(32)

    val mul_op_in = Mux(io.cfg_mul.src === 0.U, io.cfg_mul.op, mul_op_sync)
    val mul_data_in = mul_data_sync

    val x_mul_prelu = Module{new NV_NVDLA_HLS_prelu(33, 49, 16)}
    x_mul_prelu.io.cfg_prelu_en := io.cfg_mul.prelu
    x_mul_prelu.io.data_in := mul_data_in
    x_mul_prelu.io.op_in := mul_op_in
    val mul_prelu_out = x_mul_prelu.io.data_out

    val mul_final_prdy = Wire(Bool())
    val pipe_p1_data_in = Cat(mul_prelu_out, bypass_trt)
    val pipe_p1 = Module{new NV_NVDLA_BC_pipe(50)}
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := mul_sync_pvld
    mul_sync_prdy := pipe_p1.io.ro
    pipe_p1.io.di := pipe_p1_data_in
    val mul_final_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := mul_final_prdy
    val pipe_p1_data_out = pipe_p1.io.dout
    val bypass_trt_reg = pipe_p1_data_out(0)
    val mul_data_final = pipe_p1_data_out(49, 1)


    io.alu_data_out.ready := Mux(io.cfg_mul.bypass, io.mul_data_out.ready, alu_data_out_srdy)
    mul_final_prdy := Mux(io.cfg_mul.bypass, true.B, io.mul_data_out.ready)
    io.mul_data_out.valid := Mux(io.cfg_mul.bypass, io.alu_data_out.valid, mul_final_pvld)
    io.bypass_trt_out := Mux(io.cfg_mul.bypass, false.B, bypass_trt_reg)
    io.mul_data_out.bits := Mux(io.cfg_mul.bypass, Cat(Fill(16, io.alu_data_out.bits(32)), io.alu_data_out.bits), mul_data_final)

}}

