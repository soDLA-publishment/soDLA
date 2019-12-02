package nvdla

import chisel3._
import chisel3.util._

class HLS_cdp_ocvt extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val chn_data_in_rsc_z = Flipped(DecoupledIO(UInt(25.W)))
        val chn_data_out_rsc_z = DecoupledIO(UInt(8.W))
    
        val cfg_alu_in_rsc_z = Input(UInt(25.W))
        val cfg_mul_in_rsc_z = Input(UInt(16.W))
        val cfg_truncate_rsc_z = Input(UInt(6.W))
    })

withClock(io.nvdla_core_clk){

    val cat_chn_cfg_in = Cat(io.chn_data_in_rsc_z.bits, io.cfg_alu_in_rsc_z)
    val sub_in_prdy = Wire(Bool())

    val pipe_p1 = Module(new NV_NVDLA_BC_pipe(50))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.chn_data_in_rsc_z.valid
    io.chn_data_in_rsc_z.ready := pipe_p1.io.ro
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
    val mul_dout = (sub_data_out.asSInt * io.cfg_mul_in_rsc_z.asSInt).asUInt

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
    shiftright_su.io.shift_num := io.cfg_truncate_rsc_z
    val tru_dout = shiftright_su.io.data_out

    val pipe_p4 = Module(new NV_NVDLA_BC_pipe(8))
    pipe_p4.io.clk := io.nvdla_core_clk
    pipe_p4.io.vi := mul_out_pvld
    mul_out_prdy := pipe_p4.io.ro
    pipe_p4.io.di := tru_dout
    io.chn_data_out_rsc_z.valid := pipe_p4.io.vo
    pipe_p4.io.ri := io.chn_data_out_rsc_z.ready
    io.chn_data_out_rsc_z.bits := pipe_p4.io.dout

}}

object HLS_cdp_ocvtDriver extends App {
  chisel3.Driver.execute(args, () => new HLS_cdp_ocvt())
}




    



  






  



