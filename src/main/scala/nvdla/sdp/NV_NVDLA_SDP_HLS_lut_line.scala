package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_HLS_lut_line(LUT_DEPTH:Int = 256) extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val idx_in = Flipped(DecoupledIO(UInt(32.W)))
        val idx_out = DecoupledIO(new lut_out_if)

        val cfg_lut_sel = Input(UInt(8.W))
        val cfg_lut_start = Input(UInt(32.W))
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
    val mux_prdy = Wire(Bool())
    val pipe_p1_data_in = Cat(io.cfg_lut_sel, io.cfg_lut_start, io.idx_in.bits)
    val pipe_p1 = Module(new NV_NVDLA_BC_pipe(72))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.idx_in.valid
    io.idx_in.ready := pipe_p1.io.ro
    pipe_p1.io.di := pipe_p1_data_in
    val mux_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := mux_prdy
    val pipe_p1_data_out = pipe_p1.io.dout

    val idx_data_reg = pipe_p1_data_out(31, 0)
    val cfg_lut_start_reg = pipe_p1_data_out(63, 32)
    val cfg_lut_sel_reg = pipe_p1_data_out(71, 64)
    val lut_uflow_in = (idx_data_reg.asSInt <= cfg_lut_start_reg.asSInt)
    val lut_index_sub_tmp = (idx_data_reg.asSInt - cfg_lut_start_reg.asSInt).asUInt

    //unsigned int
    val lut_index_sub = Mux(lut_uflow_in, "b0".asUInt(32.W), lut_index_sub_tmp)
    val sub_prdy = Wire(Bool())
    val pipe_p2_data_in = Cat(lut_uflow_in, lut_index_sub)
    val pipe_p2 = Module(new NV_NVDLA_BC_pipe(33))
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := mux_pvld
    mux_prdy := pipe_p2.io.ro
    pipe_p2.io.di := pipe_p2_data_in
    val sub_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := sub_prdy
    val pipe_p2_data_out = pipe_p2.io.dout

    val lut_index_sub_reg = pipe_p2_data_out(31, 0)
    val lut_uflow = pipe_p2_data_out(32)

    //saturation and truncate, but no rounding
    val lut_index_shiftright_usz = Module(new NV_NVDLA_HLS_shiftrightusz(32, 9, 35, 8))
    lut_index_shiftright_usz.io.data_in := lut_index_sub_reg
    lut_index_shiftright_usz.io.shift_num := cfg_lut_sel_reg
    val lut_index_shift = lut_index_shiftright_usz.io.data_out
    val lut_frac_shift = lut_index_shiftright_usz.io.frac_out

    val lut_oflow = (lut_index_shift >= (LUT_DEPTH -1).U)

    //index integar
    val lut_index_final = Wire(UInt(9.W))
    when(lut_oflow){
        lut_index_final := (LUT_DEPTH - 1).U
    }
    .otherwise{
        lut_index_final := lut_index_shift
    }
    val lut_frac_final = lut_frac_shift

    val pipe_p3_data_in = Cat(lut_uflow, lut_oflow, lut_index_final)
    val pipe_p3 = Module(new NV_NVDLA_BC_pipe(46))
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := sub_pvld
    sub_prdy := pipe_p3.io.ro
    pipe_p3.io.di := pipe_p3_data_in
    io.idx_out.valid := pipe_p3.io.vo
    pipe_p3.io.ri := io.idx_out.ready
    val pipe_p3_data_out = pipe_p3.io.dout

    io.idx_out.bits.frac := pipe_p3_data_out(34, 0)
    io.idx_out.bits.index := pipe_p3_data_out(43, 35)
    io.idx_out.bits.oflow := pipe_p3_data_out(44)
    io.idx_out.bits.uflow := pipe_p3_data_out(45)
}}


object NV_NVDLA_SDP_HLS_lut_lineDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_lut_line())
}