package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_CDMA_CVT_cell(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())
        //cfg
        val cfg_mul_in_rsc = Input(UInt(16.W))
        val cfg_out_precision = Input(UInt(2.W))
        val cfg_truncate = Input(UInt(6.W))
        //cvt
        val chn_alu_in_rsc = Flipped(DecoupledIO(UInt(16.W)))
        val chn_data_in_rsc = Flipped(DecoupledIO(UInt(17.W)))
        val chn_data_out_rsc = DecoupledIO(UInt(16.W))

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
    val chn_sync_prdy = Wire(Bool())

    io.chn_data_in_rsc.ready := chn_sync_prdy & io.chn_alu_in_rsc.valid
    io.chn_alu_in_rsc.ready := chn_sync_prdy & io.chn_data_in_rsc.valid

    val chn_data_ext = Wire(SInt(18.W))
    chn_data_ext := io.chn_data_in_rsc.bits.asSInt
    val chn_alu_ext = Wire(SInt(18.W))
    chn_alu_ext := io.chn_alu_in_rsc.bits.asSInt

    //sub
    val sub_out_prdy = Wire(Bool())
    val pipe_p1 = Module(new NV_NVDLA_BC_pipe(18))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.chn_alu_in_rsc.valid & io.chn_data_in_rsc.valid
    chn_sync_prdy := pipe_p1.io.ro
    pipe_p1.io.di := (chn_data_ext - chn_alu_ext).asUInt
    val sub_out_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := sub_out_prdy
    val sub_data_out = pipe_p1.io.dout
    
    //mul 
    val mul_out_prdy = Wire(Bool())
    val pipe_p2 = Module(new NV_NVDLA_BC_pipe(34))
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := sub_out_pvld
    sub_out_prdy := pipe_p2.io.ro
    pipe_p2.io.di := (sub_data_out.asSInt * (io.cfg_mul_in_rsc.asSInt)).asUInt
    val mul_out_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := mul_out_prdy
    val mul_data_out = pipe_p2.io.dout

    //truncate
    val u_shiftright_su = Module(new NV_NVDLA_HLS_shiftrightsu(34, 17, 6))
    u_shiftright_su.io.data_in := mul_data_out
    u_shiftright_su.io.shift_num := io.cfg_truncate
    val tru_dout = u_shiftright_su.io.data_out

    val u_saturate_int16 = Module(new NV_NVDLA_HLS_saturate(17, 16)) 
    u_saturate_int16.io.data_in := tru_dout
    val dout_int16_sat = u_saturate_int16.io.data_out 

    val u_saturate_int8 = Module(new NV_NVDLA_HLS_saturate(17, 8)) 
    u_saturate_int8.io.data_in := tru_dout
    val dout_int8_sat = u_saturate_int8.io.data_out

    val pipe_p3 = Module(new NV_NVDLA_BC_pipe(16))
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := mul_out_pvld
    mul_out_prdy := pipe_p3.io.ro 
    pipe_p3.io.di :=  Mux(io.cfg_out_precision === 1.U, dout_int16_sat, dout_int8_sat)
    io.chn_data_out_rsc.valid := pipe_p3.io.vo
    pipe_p3.io.ri := io.chn_data_out_rsc.ready
    io.chn_data_out_rsc.bits  := pipe_p3.io.dout
     

}}


object NV_NVDLA_CDMA_CVT_cellDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_CVT_cell())
}
