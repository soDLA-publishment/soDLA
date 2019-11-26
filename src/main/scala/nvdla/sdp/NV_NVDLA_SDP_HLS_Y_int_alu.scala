package nvdla

import chisel3._
import chisel3.util._

class sdp_y_int_alu_cfg_if extends Bundle{
    val algo = Output(UInt(2.W))
    val bypass = Output(Bool())
    val op = Output(UInt(32.W))
    val src = Output(Bool())
}


class NV_NVDLA_SDP_HLS_Y_int_alu extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val alu_data_in = Flipped(DecoupledIO(UInt(32.W)))
        val chn_alu_op = Flipped(DecoupledIO(UInt(32.W)))
        val alu_data_out = DecoupledIO(UInt(32.W))

        val cfg_alu = Flipped(new sdp_y_int_alu_cfg_if)
        
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
    val y_alu_sync2data = Module{new NV_NVDLA_HLS_sync2data(32, 32)}
    y_alu_sync2data.io.chn1_en := io.cfg_alu.src & !io.cfg_alu.bypass
    y_alu_sync2data.io.chn2_en := !io.cfg_alu.bypass
    y_alu_sync2data.io.chn1_in <> io.chn_alu_op
    y_alu_sync2data.io.chn2_in.valid := io.alu_data_in.valid
    val alu_in_srdy = y_alu_sync2data.io.chn2_in.ready
    y_alu_sync2data.io.chn2_in.bits := io.alu_data_in.bits

    val alu_sync_pvld = y_alu_sync2data.io.chn_out.valid
    y_alu_sync2data.io.chn_out.ready := alu_sync_prdy
    val alu_op_sync = y_alu_sync2data.io.chn_out.bits.data1
    val alu_data_sync = y_alu_sync2data.io.chn_out.bits.data2

    val alu_op_mux = Mux(io.cfg_alu.src, alu_op_sync, io.cfg_alu.op)

    //pack {alu_data_sync[31:0],alu_op_mux[31:0]}
    val alu_data_in_p1 = Cat(alu_data_sync, alu_op_mux)
    val alu_mux_prdy = Wire(Bool())
    val pipe_p1 = Module{new NV_NVDLA_BC_pipe(64)}
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := alu_sync_pvld
    alu_sync_prdy := pipe_p1.io.ro
    pipe_p1.io.di := alu_data_in_p1
    val alu_mux_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := alu_mux_prdy
    val alu_data_out_p1 = pipe_p1.io.dout

    val alu_data_reg = alu_data_out_p1(63, 32)
    val alu_op_reg = alu_data_out_p1(31, 0)

    val alu_op_ext = Cat(alu_op_reg(31), alu_op_reg)
    val alu_data_ext = Cat(alu_data_reg(31), alu_data_reg)
    val alu_sum = (alu_op_ext.asSInt + alu_op_ext.asSInt).asUInt

    val alu_dout = Wire(UInt(33.W))
    when(io.cfg_alu.algo === 0.U){
        alu_dout := Mux(alu_data_ext.asSInt > alu_op_ext.asSInt, alu_data_ext, alu_op_ext)
    }
    .elsewhen(io.cfg_alu.algo === 1.U){
        alu_dout := Mux(alu_data_ext.asSInt < alu_op_ext.asSInt, alu_data_ext, alu_op_ext)
    }
    .elsewhen(io.cfg_alu.algo === 3.U){
        alu_dout := Mux(alu_data_ext === alu_op_ext, 0.U, 1.U)
    }
    .otherwise{
        alu_dout := alu_sum
    }

    val y_alu_saturate = Module{new NV_NVDLA_HLS_saturate(32+1, 32)}
    y_alu_saturate.io.data_in := alu_dout
    val alu_sat = y_alu_saturate.io.data_out

    val alu_final_prdy = Wire(Bool())
    val pipe_p2 = Module{new NV_NVDLA_BC_pipe(32)}
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := alu_mux_pvld
    alu_mux_prdy := pipe_p2.io.ro
    pipe_p2.io.di := alu_sat
    val alu_final_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := alu_final_prdy
    val alu_data_final = pipe_p2.io.dout

    io.alu_data_in.ready := Mux(io.cfg_alu.bypass, io.alu_data_out.ready, alu_in_srdy)
    alu_final_prdy := Mux(io.cfg_alu.bypass, true.B, io.alu_data_out.ready)
    io.alu_data_out.valid := Mux(io.cfg_alu.bypass, io.alu_data_in.valid, alu_final_pvld)
    io.alu_data_out.bits := Mux(io.cfg_alu.bypass, io.alu_data_in.bits, alu_data_final)


}}


object NV_NVDLA_SDP_HLS_Y_int_aluDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_alu)
}