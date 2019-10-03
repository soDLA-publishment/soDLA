package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class sdp_x_int_alu_cfg_if extends Bundle{
    val algo = Output(UInt(2.W))
    val bypass = Output(Bool())
    val op = Output(UInt(16.W))
    val shift_value = Output(UInt(6.W))
    val src = Output(Bool())
}

class NV_NVDLA_SDP_HLS_X_int_alu extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val chn_alu_op = Flipped(DecoupledIO(UInt(16.W)))
        val alu_data_in = Flipped(DecoupledIO(UInt(32.W)))
        val alu_data_out = DecoupledIO(UInt(33.W))

        val cfg_alu = Flipped(new sdp_x_int_alu_cfg_if)
               
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

    val alu_sync_prdy = Wire(Bool())
    val x_alu_sync2data = Module{new NV_NVDLA_HLS_sync2data(16, 32)}
    x_alu_sync2data.io.chn1_en := io.cfg_alu.src & !io.cfg_alu.bypass
    x_alu_sync2data.io.chn2_en := !io.cfg_alu.bypass
    x_alu_sync2data.io.chn1_in <> io.chn_alu_op
    x_alu_sync2data.io.chn2_in.valid := io.alu_data_in.valid
    val alu_in_srdy = x_alu_sync2data.io.chn2_in.ready
    x_alu_sync2data.io.chn2_in.bits := io.alu_data_in.bits
    val alu_sync_pvld = x_alu_sync2data.io.chn_out.valid   
    x_alu_sync2data.io.chn_out.ready := alu_sync_prdy
    val alu_op_sync = x_alu_sync2data.io.chn_out.bits.data1
    val alu_data_sync = x_alu_sync2data.io.chn_out.bits.data2

    val alu_op_in = Mux(io.cfg_alu.src, alu_op_sync, io.cfg_alu.op)

    val x_alu_shiftleft_su = Module{new NV_NVDLA_HLS_shiftleftsu(16, 32, 6)}
    x_alu_shiftleft_su.io.data_in := alu_op_in
    x_alu_shiftleft_su.io.shift_num := io.cfg_alu.shift_value
    val alu_op_shift = x_alu_shiftleft_su.io.data_out

    val alu_shift_prdy = Wire(Bool())
    val pipe_p1_data_in = Cat(alu_op_shift, alu_data_sync)
    val pipe_p1 = Module{new NV_NVDLA_BC_pipe(64)}
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := alu_sync_pvld
    alu_sync_prdy := pipe_p1.io.ro
    pipe_p1.io.di := pipe_p1_data_in
    val alu_shift_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := alu_shift_prdy
    val pipe_p1_data_out = pipe_p1.io.dout

    val alu_data_reg = pipe_p1_data_out(31, 0)
    val operand_shift = pipe_p1_data_out(63, 32)

    val operand_ext = Cat(operand_shift(31), operand_shift)
    val alu_data_ext = Cat(alu_data_reg(31), alu_data_reg)
    val alu_sum = (alu_data_ext.asSInt + operand_ext.asSInt).asUInt

    val alu_dout = Wire(UInt(33.W))
    when(io.cfg_alu.algo === 0.U){
        alu_dout := Mux(alu_data_ext.asSInt > operand_ext.asSInt, alu_data_ext, operand_ext)
    }
    .elsewhen(io.cfg_alu.algo === 1.U){
        alu_dout := Mux(alu_data_ext.asSInt < operand_ext.asSInt, alu_data_ext, operand_ext)
    }
    .otherwise{
        alu_dout := alu_sum
    }

    val alu_final_prdy = Wire(Bool())
    val pipe_p2 = Module{new NV_NVDLA_BC_pipe(32)}
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := alu_shift_pvld
    alu_shift_prdy := pipe_p2.io.ro
    pipe_p2.io.di := alu_dout
    val alu_final_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := alu_final_prdy
    val alu_data_final = pipe_p2.io.dout

    io.alu_data_in.ready := Mux(io.cfg_alu.bypass, io.alu_data_out.ready, alu_in_srdy)
    alu_final_prdy := Mux(io.cfg_alu.bypass, true.B, io.alu_data_out.ready)
    io.alu_data_out.valid := Mux(io.cfg_alu.bypass, io.alu_data_in.ready, alu_final_pvld)
    io.alu_data_out.bits := Mux(io.cfg_alu.bypass, Cat(io.alu_data_in.bits(31), io.alu_data_in.bits), alu_data_final)


}} 

object NV_NVDLA_SDP_HLS_X_int_aluDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_X_int_alu)
}
