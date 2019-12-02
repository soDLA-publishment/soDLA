package nvdla

import chisel3._
import chisel3.util._

class HLS_cdp_icvt extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val chn_data_in_rsc_z = Flipped(DecoupledIO(UInt(8.W)))
        val chn_data_out_rsc_z = DecoupledIO(UInt(9.W))

        val cfg_alu_in_rsc_z = Input(UInt(8.W))
        val cfg_mul_in_rsc_z = Input(UInt(16.W))
        val cfg_truncate_rsc_z = Input(UInt(5.W))

    })

withClock(io.nvdla_core_clk){

    //cvt
    val chn_data_ext = Cat(io.chn_data_in_rsc_z.bits(7), io.chn_data_in_rsc_z.bits)
    val cfg_alu_ext = Cat(io.cfg_alu_in_rsc_z(7), io.cfg_alu_in_rsc_z)

    //sub
    val chn_data_sub_cfg_alu = (chn_data_ext.asSInt -& cfg_alu_ext.asSInt).asUInt
    val mon_sub_c = chn_data_sub_cfg_alu(9)
    val sub_dout = chn_data_sub_cfg_alu(8,0)

    val sub_out_prdy = Wire(Bool())

    val pipe_p1 = Module(new NV_NVDLA_BC_pipe(9))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.chn_data_in_rsc_z.valid
    io.chn_data_in_rsc_z.ready := pipe_p1.io.ro
    pipe_p1.io.di := sub_dout
    val sub_out_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := sub_out_prdy
    val sub_data_out = pipe_p1.io.dout

    //mul
    val mul_dout = (sub_data_out.asSInt * io.cfg_mul_in_rsc_z.asSInt).asUInt

    val mul_out_prdy = Wire(Bool())
    
    val pipe_p2 = Module(new NV_NVDLA_BC_pipe(25))
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := sub_out_pvld
    sub_out_prdy := pipe_p2.io.ro
    pipe_p2.io.di := mul_dout
    val mul_out_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := mul_out_prdy
    val mul_data_out = pipe_p2.io.dout

    //truncate

    val shiftright_su = Module(new NV_NVDLA_HLS_shiftrightsu(16+9, 9, 5))
    shiftright_su.io.data_in := mul_data_out
    shiftright_su.io.shift_num := io.cfg_truncate_rsc_z
    val tru_dout = shiftright_su.io.data_out

    val pipe_p3 = Module(new NV_NVDLA_BC_pipe(9))
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := mul_out_pvld
    mul_out_prdy := pipe_p3.io.ro
    pipe_p3.io.di := tru_dout
    io.chn_data_out_rsc_z.valid := pipe_p3.io.vo
    pipe_p3.io.ri := io.chn_data_out_rsc_z.ready
    io.chn_data_out_rsc_z.bits := pipe_p3.io.dout

}}




    



  






  



