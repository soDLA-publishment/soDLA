package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class sdp_y_int_mul_cfg_if extends Bundle{
    val bypass = Output(Bool())
    val op = Output(UInt(32.W))
    val prelu = Output(Bool())
    val truncate = Output(UInt(10.W))
    val src = Output(Bool())
}

class NV_NVDLA_SDP_HLS_Y_int_mul extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val chn_mul_in = Flipped(DecoupledIO(UInt(32.W)))
        val chn_mul_op = Flipped(DecoupledIO(UInt(32.W)))
        val mul_data_out = DecoupledIO(UInt(32.W))

        val cfg_mul = Flipped(new sdp_y_int_mul_cfg_if)

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
    val y_mul_sync2data = Module{new NV_NVDLA_HLS_sync2data(32, 32)}
    y_mul_sync2data.io.chn1_en := !io.cfg_mul.bypass & io.cfg_mul.src
    y_mul_sync2data.io.chn2_en := !io.cfg_mul.bypass
    y_mul_sync2data.io.chn1_in <> io.chn_mul_op
    y_mul_sync2data.io.chn2_in.valid := io.chn_mul_in.valid
    val chn_in_srdy = y_mul_sync2data.io.chn2_in.ready
    y_mul_sync2data.io.chn2_in.bits := io.chn_mul_in.bits

    val mul_sync_pvld = y_mul_sync2data.io.chn_out.valid   
    y_mul_sync2data.io.chn_out.ready := mul_sync_prdy        
    val mul_op_sync = y_mul_sync2data.io.chn_out.bits.data1
    val mul_data_sync = y_mul_sync2data.io.chn_out.bits.data2

    val mul_data_in = mul_data_sync
    val mul_op_in = Mux(io.cfg_mul.src === 0.U, io.cfg_mul.op, mul_op_sync)

    val x_mul_prelu = Module{new NV_NVDLA_HLS_prelu(32, 32+32, 32)}
    x_mul_prelu.io.cfg_prelu_en := io.cfg_mul.prelu
    x_mul_prelu.io.data_in := mul_data_in
    x_mul_prelu.io.op_in := mul_op_in
    val mul_prelu_dout = x_mul_prelu.io.data_out
    
    val mul_prelu_prdy = Wire(Bool())
    val pipe_p1_data_in = Cat(mul_data_in, mul_prelu_dout)
    val pipe_p1 = Module{new NV_NVDLA_BC_pipe(96)}
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := mul_sync_pvld
    mul_sync_prdy := pipe_p1.io.ro
    pipe_p1.io.di := pipe_p1_data_in
    val mul_prelu_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := mul_prelu_prdy
    val pipe_p1_data_out = pipe_p1.io.dout
    val mul_prelu_out = pipe_p1_data_out(63, 0)
    val mul_data_reg = pipe_p1_data_out(95, 64)

    val y_mul_shiftright_su = Module{new NV_NVDLA_HLS_shiftrightsu(64, 32, 10)}
    y_mul_shiftright_su.io.data_in := mul_prelu_out
    y_mul_shiftright_su.io.shift_num := io.cfg_mul.truncate
    val mul_truncate_out = y_mul_shiftright_su.io.data_out

    //signed 
    //unsigned 
    val mul_dout = Wire(UInt(32.W))
    when(io.cfg_mul.prelu & !mul_data_reg(31)){
        mul_dout := mul_data_reg
    }
    .otherwise{
        mul_dout := mul_truncate_out
    }

    val mul_final_prdy = Wire(Bool())
    val pipe_p2 = Module{new NV_NVDLA_BC_pipe(32)}
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := mul_prelu_pvld
    mul_prelu_prdy := pipe_p2.io.ro
    pipe_p2.io.di := mul_dout
    val mul_final_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := mul_final_prdy
    val mul_data_final = pipe_p2.io.dout
    
    io.chn_mul_in.ready := Mux(io.cfg_mul.bypass, io.mul_data_out.ready, chn_in_srdy)
    mul_final_prdy := Mux(io.cfg_mul.bypass, true.B, io.mul_data_out.ready)
    io.mul_data_out.valid := Mux(io.cfg_mul.bypass, io.chn_mul_in.valid, mul_final_pvld)
    io.mul_data_out.bits := Mux(io.cfg_mul.bypass, io.chn_mul_in.bits, mul_data_final)
}}


object NV_NVDLA_SDP_HLS_Y_int_mulDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_mul)
}
