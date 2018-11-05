package slibs

import chisel3._

class HLS_cdp_icvt extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //
        val cfg_alu_in_rsc_z = Input(UInt(8.W))
        val cfg_mul_in_rsc_z = Input(UInt(16.W))
        val cfg_truncate_rsc_z = Input(UInt(4.W))
        val chn_data_in_rsc_vz = Input(UInt(1.W))
        val chn_data_in_rsc_z = Input(UInt(8.W))
        val chn_data_out_rsc_vz = Input(UInt(1.W))
        val chn_data_in_rsc_lz = Output(UInt(1.W))
        val chn_data_out_rsc_lz = Output(UInt(1.W))
        val chn_data_out_rsc_z = Output(UInt(9.W))

    })

    val cfg_alu_ext = Wire(UInt(9.W))
    val cfg_alu_in = Wire(UInt(8.W))
    val cfg_mul_in = Wire(UInt(16.W))
    val cfg_truncate = Wire(UInt(5.W))
    val chn_data_ext = Wire(UInt(9.W))
    val chn_data_in = Wire(UInt(8.W))
    val chn_data_out = Wire(UInt(9.W))
    val chn_in_prdy = Wire(Bool())
    val chn_in_pvld = Wire(Bool())
    val chn_out_prdy = Wire(Bool())
    val chn_out_pvld = Wire(Bool())
    val mon_sub_c = Wire(UInt(1.W))
    val mul_data_out = Wire(UInt(25.W))
    val mul_dout = Wire(UInt(25.W))
    val mul_out_prdy = Wire(Bool())
    val mul_out_pvld = Wire(Bool())
    val sub_data_out =  Wire(UInt(9.W))
    val sub_dout = Wire(UInt(9.W))
    val sub_out_prdy = Wire(Bool())
    val sub_out_pvld = Wire(Bool())
    val tru_dout =  Wire(UInt(9.W)) 

    chn_in_pvld := io.chn_data_in_rsc_vz.asBool
    chn_out_prdy := io.chn_data_out_rsc_vz.asBool
    chn_data_in := io.chn_data_in_rsc_z
    cfg_alu_in := io.cfg_alu_in_rsc_z
    cfg_mul_in := io.cfg_mul_in_rsc_z
    cfg_truncate := io.cfg_truncate_rsc_z

    io.chn_data_in_rsc_lz := chn_in_prdy.asUInt
    io.chn_data_out_rsc_lz := chn_out_pvld.asUInt
    io.chn_data_out_rsc_z := chn_data_out

    //cvt
    chn_data_ext := Cat(chn_data_in(7), chn_data_in)
    cfg_alu_ext := Cat(cfg_alu_in(7), cfg_alu_in)

    //sub
    val chn_data_sub_cfg_alu = chn_data_ext.asSInt - chn_data_ext.asSInt
    mon_sub_c := chn_data_sub_cfg_alu(9)
    sub_dout := chn_data_sub_cfg_alu(8,0)

    val pipe_p1 = Module(new HLS_cdp_ICVT_pipe_p1())
    io.nvdla_core_clk := pipe_p1.io.nvdla_core_clk
    io.nvdla_core_rstn := pipe_p1.io.nvdla_core_rstn
    chn_in_pvld := pipe_p1.io.chn_in_pvld
    sub_dout := pipe_p1.io.sub_dout
    sub_out_prdy := pipe_p1.io.sub_out_prdy 
    chn_in_prdy := pipe_p1.io.chn_in_prdy
    sub_data_out := pipe_p1.io.sub_data_out
    sub_out_pvld := pipe_p1.io.sub_out_pvld

    //mul
    val mul_dout = sub_data_out.asSInt*cfg_mul_in.asSInt

    val pipe_p2 = Module(new HLS_cdp_ICVT_pipe_p2())
    io.nvdla_core_clk := pipe_p2.io.nvdla_core_clk
    io.nvdla_core_rstn := pipe_p2.io.nvdla_core_rstn
    mul_dout:= pipe_p2.io.mul_dout
    mul_out_prdy := pipe_p2.io.mul_out_prdy 
    sub_out_prdy := pipe_p2.io.sub_out_prdy 
    mul_data_out  := pipe_p2.io.mul_data_out 
    mul_out_pvld := pipe_p2.io.mul_out_pvld
    sub_out_prdy := pipe_p2.io.sub_out_prdy

    //truncate

    val shiftright_su = Module(new NV_NVDLA_HLS_shiftrightsu(16+9, 9, 5))
    mul_data_out := shiftright_su.io.data_in
    cfg_truncate := shiftright_su.io.shift_num 
    tru_dout := shiftright_su.io.data_out
  

    val pipe_p3 = Module(new HLS_cdp_ICVT_pipe_p3())
    io.nvdla_core_clk := pipe_p3.io.nvdla_core_clk
    io.nvdla_core_rstn := pipe_p3.io.nvdla_core_rstn
    chn_out_prdy:= pipe_p3.io.chn_out_prdy
    mul_out_prdy := pipe_p3.io.mul_out_prdy 
    tru_dout:= pipe_p3.io.tru_dout
    chn_data_out  := pipe_p3.io.chn_data_out
    chn_out_pvld := pipe_p3.io.chn_out_pvld
    mul_out_prdy := pipe_p3.io.mul_out_prdy


}

class HLS_cdp_ICVT_pipe_p1 extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //
        val chn_in_pvld = Input(Bool())
        val sub_dout = Input(UInt(9.W))
        val sub_out_prdy = Input(Bool())
        val chn_in_prdy = Output(Bool())
        val sub_data_out = Output(UInt(9.W))
        val sub_out_pvld = Output(Bool())

    })

    



  






  



