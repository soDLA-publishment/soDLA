package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_CDMA_CVT_cell(implicit conf: cdmaConfiguration) extends Module {

    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())

        val cfg_mul_in_rsc_z = Input(UInt(16.W))
        val cfg_in_precision = Input(UInt(2.W))
        val cfg_out_precision = Input(UInt(2.W))
        val cfg_truncate = Input(UInt(6.W))

        val chn_alu_in_rsc_vz = Input(Bool())
        val chn_alu_in_rsc_z = Input(UInt(16.W))
        val chn_alu_in_rsc_lz = Output(Bool())

        val chn_data_in_rsc_vz = Input(Bool())
        val chn_data_in_rsc_z = Input(UInt(17.W))
        val chn_data_in_rsc_lz = Output(Bool())

        val chn_data_out_rsc_vz = Input(Bool())
        val chn_data_out_rsc_z = Output(UInt(16.W))
        val chn_data_out_rsc_lz = Output(Bool())

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

    val chn_in_prdy = Wire(Bool())
    val chn_alu_prdy = Wire(Bool())
    val chn_out_pvld = Wire(Bool())
    val chn_sync_pvld = Wire(Bool())
    val chn_sync_prdy = Wire(Bool())
    val chn_data_out = Wire(SInt(16.W))

    val chn_in_pvld  = io.chn_data_in_rsc_vz
    val chn_alu_pvld = io.chn_alu_in_rsc_vz
    val chn_data_in = io.chn_data_in_rsc_z
    val chn_alu_in = io.chn_alu_in_rsc_z
    val cfg_mul_in = io.cfg_mul_in_rsc_z
    val chn_out_prdy = io.chn_data_out_rsc_vz

    io.chn_data_in_rsc_lz := chn_in_prdy
    io.chn_alu_in_rsc_lz := chn_alu_prdy
    io.chn_data_out_rsc_lz := chn_out_pvld
    io.chn_data_out_rsc_z := chn_data_out

    chn_sync_pvld := chn_alu_pvld  & chn_in_pvld
    chn_alu_prdy := chn_sync_prdy & chn_in_pvld
    chn_in_prdy := chn_sync_prdy & chn_alu_pvld

    val chn_data_ext = Wire(SInt(18.W))
    chn_data_ext := chn_data_in
    val chn_alu_ext = Wire(SInt(18.W))
    chn_alu_ext := chn_alu_in

    //sub
    val sub_out_prdy = Wire(Bool())
    val sub_dout = (chn_data_ext - chn_alu_ext).asUInt

    val pipe_p1 = Module(NV_NVDLA_BC_pipe(18))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := chn_sync_pvld
    chn_sync_prdy := pipe_p1.io.ro
    pipe_p1.io.di := sub_dout
    val sub_out_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := sub_out_prdy
    val sub_data_out = pipe_p1.io.dout
    
    //mul 
    val mul_out_prdy = Wire(Bool())
    val mul_dout = (sub_data_out.asSInt * cfg_mul_in.asSInt).asUInt

    val pipe_p2 = Module(NV_NVDLA_BC_pipe(34))
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := sub_out_pvld
    sub_out_prdy := pipe_p2.io.ro
    pipe_p2.io.di := mul_dout
    val mul_out_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := mul_out_prdy
    val mul_data_out = pipe_p2.io.dout
    
    //truncate
    val u_shiftright_su = Module(new NV_NVDLA_HLS_shiftrightsu(34, 17, 6))
    u_shiftright_su.io.data_in := mul_data_out
    u_shiftright_su.io.shift_num := io.cfg_truncate
    val tru_dout = u_shiftright_su.io.data_out
    
    //unsigned
    val tru_out_prdy = Wire(Bool())
    val tru_data_out = tru_dout
    val tru_out_pvld = mul_out_pvld
    mul_out_prdy := tru_out_prdy

    val u_saturate_int16 = Module(new NV_NVDLA_HLS_saturate(17, 16)) 
    u_saturate_int16.io.data_in := tru_data_out
    val dout_int16_sat = u_saturate_int16.io.data_out 

    val u_saturate_int8 = Module(new NV_NVDLA_HLS_saturate(17, 8)) 
    u_saturate_int8.io.data_in := tru_data_out
    val dout_int8_sat = u_saturate_int8.io.data_out

    val chn_dout = Mux(io.cfg_out_precision === 1.U, dout_int16_sat, dout_int8_sat)

    val pipe_p3 = Module(new NV_NVDLA_BC_pipe(16))
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := tru_out_pvld
    tru_out_prdy := pipe_p3.io.ro 
    pipe_p3.io.di := chn_dout
    chn_out_pvld := pipe_p3.io.vo
    pipe_p3.io.ri := chn_out_prdy
    chn_data_out  := pipe_p3.io.dout
     

}}


object NV_NVDLA_CDMA_CVT_cellDriver extends App {
  implicit val conf: cdmaConfiguration = new cdmaConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_CVT_cell())
}
