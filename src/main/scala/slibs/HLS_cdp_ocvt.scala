package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class HLS_cdp_ocvt extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())
        val cfg_alu_in_rsc_z = Input(UInt(25.W))
        val cfg_mul_in_rsc_z = Input(UInt(16.W))
        val cfg_truncate_rsc_z = Input(UInt(6.W))
        val chn_data_in_rsc_vz = Input(Bool())
        val chn_data_in_rsc_z = Input(UInt(25.W))
        val chn_data_out_rsc_vz = Input(Bool())
        val chn_data_in_rsc_lz = Output(Bool())
        val chn_data_out_rsc_lz = Output(Bool())
        val chn_data_out_rsc_z = Output(UInt(8.W))

    })

withClock(io.nvdla_core_clk){


// synoff nets

// monitor nets

// debug nets

// tie high nets

// tie low nets

// no connect nets

// not all bits used nets

// todo nets


    val chn_in_pvld = io.chn_data_in_rsc_vz
    val chn_out_prdy = io.chn_data_out_rsc_vz
    val chn_data_in = io.chn_data_in_rsc_z
    val cfg_alu_in = io.cfg_alu_in_rsc_z
    val cfg_mul_in = io.cfg_mul_in_rsc_z
    val cfg_truncate = io.cfg_truncate_rsc_z

    val chn_in_prdy = Wire(Bool())
    val chn_out_pvld = Wire(Bool())
    val chn_data_out = Wire(UInt(9.W))
    io.chn_data_in_rsc_lz := chn_in_prdy
    io.chn_data_out_rsc_lz := chn_out_pvld
    io.chn_data_out_rsc_z := chn_data_out

    val cat_chn_cfg_in = Cat(chn_data_in, cfg_alu_in)
    val sub_in_prdy = Wire(Bool())

    val pipe_p1 = Module(new NV_NVDLA_BC_pipe(50))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := chn_in_pvld
    chn_in_prdy := pipe_p1.io.ro
    pipe_p1.io.di := cat_chn_cfg_in
    val sub_in_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := sub_in_prdy
    val cat_chn_cfg_reg = pipe_p1.io.dout

    val chn_data_reg = cat_chn_cfg_reg(49, 25)
    val cfg_alu_reg = cat_chn_cfg_reg(24, 0)


    //cvt
    val chn_data_ext = Cat(chn_data_reg(24), chn_data_reg)
    val cfg_alu_ext = Cat(cfg_alu_reg(24), cfg_alu_reg)

    //sub
    val chn_data_sub_cfg_alu = (chn_data_ext.asSInt -& cfg_alu_ext.asSInt).asUInt
    val mon_sub_c = chn_data_sub_cfg_alu(26)
    val sub_dout = chn_data_sub_cfg_alu(25,0)

    val sub_out_prdy = Wire(Bool())

    val pipe_p2 = Module(new NV_NVDLA_BC_pipe(26))
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := sub_in_pvld
    sub_in_prdy := pipe_p2.io.ro
    pipe_p2.io.di := sub_dout
    val sub_out_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := sub_out_prdy
    val sub_data_out = pipe_p2.io.dout

    //mul
    val mul_dout = (sub_data_out.asSInt * cfg_mul_in.asSInt).asUInt

    val mul_out_prdy = Wire(Bool())
    
    val pipe_p3 = Module(new NV_NVDLA_BC_pipe(42))
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := sub_out_pvld
    sub_out_prdy := pipe_p3.io.ro
    pipe_p3.io.di := mul_dout
    val mul_out_pvld = pipe_p3.io.vo
    pipe_p3.io.ri := mul_out_prdy
    val mul_data_out = pipe_p3.io.dout

    //truncate

    val shiftright_su = Module(new NV_NVDLA_HLS_shiftrightsu(16+26, 8, 6))
    shiftright_su.io.data_in := mul_data_out
    shiftright_su.io.shift_num := cfg_truncate
    val tru_dout = shiftright_su.io.data_out

    val pipe_p4 = Module(new NV_NVDLA_BC_pipe(8))
    pipe_p4.io.clk := io.nvdla_core_clk
    pipe_p4.io.vi := mul_out_pvld
    mul_out_prdy := pipe_p4.io.ro
    pipe_p4.io.di := tru_dout
    chn_out_pvld := pipe_p4.io.vo
    pipe_p4.io.ri := chn_out_prdy
    chn_data_out := pipe_p4.io.dout

}}

object HLS_cdp_ocvtDriver extends App {
  chisel3.Driver.execute(args, () => new HLS_cdp_ocvt())
}




    



  






  



