package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


//for winograd

class NV_NVDLA_CSC_pra_cell(implicit val conf: cscConfiguration) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        // cfg
        val cfg_precision = Input(UInt(2.W))
        val cfg_truncate_rsc_z = Input(UInt(2.W))

        //chn vz&z
        val chn_data_in_rsc_vz = Input(Bool())
        val chn_data_in_rsc_z = Input(Vec(16, SInt(conf.CSC_BPE.W)))
        val chn_data_out_rsc_vz = Input(Bool())

        //chn lz
        val chn_data_in_rsc_lz = Output(Bool())
        val chn_data_out_rsc_lz = Output(Bool())
        val chn_data_out_rsc_z = Output(Vec(16, SInt(conf.CSC_BPE.W)))

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
    val chn_data_out  = Wire(Vec(16, SInt(conf.CSC_BPE.W)))
    val chn_in_prdy = Wire(Bool())
    val chn_out_pvld = Wire(Bool())

    val chn_in_pvld = io.chn_data_in_rsc_vz
    val chn_out_prdy = io.chn_data_out_rsc_vz
    val cfg_truncate = io.cfg_truncate_rsc_z
    val chn_data_in = io.chn_data_in_rsc_z

    io.chn_data_out_rsc_z := chn_data_out
    io.chn_data_in_rsc_lz := chn_in_prdy
    io.chn_data_out_rsc_lz := chn_out_pvld

    val din_prdy = Wire(Bool())

    val pipe_p1 = Module(new NV_NVDLA_CSC_PRA_CELL_pipe_p1)
    pipe_p1.io.nvdla_core_clk := io.nvdla_core_clk
    pipe_p1.io.chn_data_in := chn_data_in
    pipe_p1.io.chn_in_pvld := chn_in_pvld
    pipe_p1.io.din_prdy := din_prdy
    val chn_data_reg = pipe_p1.io.chn_data_reg 
    chn_in_prdy := pipe_p1.io.chn_in_prdy
    val din_pvld = pipe_p1.io.din_pvld

    val mdout = Wire(Vec(16, SInt(17.W)))
    val mdata_out = Wire(Vec(16, SInt(17.W))) 
    val tdout = Wire(Vec(16, SInt(18.W)))

    mdout(0) := chn_data_reg(0) -& chn_data_reg(8)
    mdout(1) := chn_data_reg(1) -& chn_data_reg(9)
    mdout(2) := chn_data_reg(2) -& chn_data_reg(10)
    mdout(3) := chn_data_reg(3) -& chn_data_reg(11)

    mdout(4) := chn_data_reg(4) +& chn_data_reg(8)
    mdout(5) := chn_data_reg(5) +& chn_data_reg(9)
    mdout(6) := chn_data_reg(6) +& chn_data_reg(10)
    mdout(7) := chn_data_reg(7) +& chn_data_reg(11)

    mdout(8) := chn_data_reg(8) -& chn_data_reg(4)
    mdout(9) := chn_data_reg(9) -& chn_data_reg(5)
    mdout(10) := chn_data_reg(10) -& chn_data_reg(6)
    mdout(11) := chn_data_reg(11) -& chn_data_reg(7)

    mdout(12) := chn_data_reg(4) -& chn_data_reg(12)
    mdout(13) := chn_data_reg(5) -& chn_data_reg(13)
    mdout(14) := chn_data_reg(6) -& chn_data_reg(14)
    mdout(15) := chn_data_reg(7) -& chn_data_reg(15)

    tdout(0) := mdata_out(0) -& mdata_out(8)
    tdout(1) := mdata_out(1) -& mdata_out(9)
    tdout(2) := mdata_out(2) -& mdata_out(10)
    tdout(3) := mdata_out(3) -& mdata_out(11)

    tdout(4) := mdata_out(4) +& mdata_out(8)
    tdout(5) := mdata_out(5) +& mdata_out(9)
    tdout(6) := mdata_out(6) +& mdata_out(10)
    tdout(7) := mdata_out(7) +& mdata_out(11)

    tdout(8) := mdata_out(8) -& mdata_out(4)
    tdout(9) := mdata_out(9) -& mdata_out(5)
    tdout(10) := mdata_out(10) -& mdata_out(6)
    tdout(11) := mdata_out(11) -& mdata_out(7)

    tdout(12) := mdata_out(4) -& mdata_out(12)
    tdout(13) := mdata_out(5) -& mdata_out(13)
    tdout(14) := mdata_out(6) -& mdata_out(14)
    tdout(15) := mdata_out(7) -& mdata_out(15)

    //row
    
    val mout_prdy = Wire(Bool())

    val pipe_p2 = Module(new NV_NVDLA_CSC_PRA_CELL_pipe_p2)
    pipe_p2.io.nvdla_core_clk := io.nvdla_core_clk
    pipe_p2.io.mdout := mdout
    pipe_p2.io.din_pvld := din_pvld
    pipe_p2.io.mout_prdy := mout_prdy
    din_prdy := pipe_p2.io.din_prdy 
    mdata_out := pipe_p2.io.mdata_out
    val mout_pvld = pipe_p2.io.mout_pvld

    //col
    val tout_prdy = Wire(Bool())

    val pipe_p3 = Module(new NV_NVDLA_CSC_PRA_CELL_pipe_p3)
    pipe_p3.io.nvdla_core_clk := io.nvdla_core_clk
    pipe_p3.io.tdout := tdout
    pipe_p3.io.mout_pvld := mout_pvld
    pipe_p3.io.tout_prdy := tout_prdy
    mout_prdy := pipe_p3.io.mout_prdy 
    val tdata_out = pipe_p3.io.tdata_out
    val tout_pvld = pipe_p3.io.tout_pvld

    val int16_shiftright_su = Array.fill(16){Module(new NV_NVDLA_HLS_shiftrightsu(18, 16, 2))}
    val int8_shiftright_su = Array.fill(16){Module(new NV_NVDLA_HLS_shiftrightsu(18, 8, 2))}

    val tru_dout_int16 = Wire(Vec(16, SInt(16.W)))
    val tru_dout_int8 = Wire(Vec(16, SInt(8.W)))

    for(i <- 0 to 15){
        int16_shiftright_su(i).io.data_in := tdata_out(i).asUInt
        int16_shiftright_su(i).io.shift_num := cfg_truncate
        tru_dout_int16(i) := int16_shiftright_su(i).io.data_out.asSInt
    }
    for(i <- 0 to 15){
        int8_shiftright_su(i).io.data_in := tdata_out(i).asUInt
        int8_shiftright_su(i).io.shift_num := cfg_truncate
        tru_dout_int8(i) := int8_shiftright_su(i).io.data_out.asSInt
    }
  
    val final_out_prdy = Wire(Bool())
    val final_out_pvld = Wire(Bool())
    val pipe_p4 = Module(new NV_NVDLA_CSC_PRA_CELL_pipe_p4)
    pipe_p4.io.nvdla_core_clk := io.nvdla_core_clk
    pipe_p4.io.tru_dout_int16 := tru_dout_int16
    pipe_p4.io.tru_dout_int8_ext := tru_dout_int8
    pipe_p4.io.tout_pvld := tout_pvld
    pipe_p4.io.final_out_prdy := final_out_prdy
    tout_prdy := pipe_p4.io.tout_prdy 
    final_out_pvld := pipe_p4.io.final_out_pvld
    val tru_data_out_int16 = pipe_p4.io.tru_data_out_int16
    val tru_data_out_int8 = pipe_p4.io.tru_data_out_int8

    val chn_dout = Mux(io.cfg_precision===1.U, tru_data_out_int16, tru_data_out_int8)

    val pipe_p5 = Module(new NV_NVDLA_CSC_PRA_CELL_pipe_p5)
    pipe_p5.io.nvdla_core_clk := io.nvdla_core_clk
    pipe_p5.io.chn_dout := chn_dout
    pipe_p5.io.final_out_pvld := final_out_pvld
    pipe_p5.io.chn_out_prdy := chn_out_prdy
    final_out_prdy := pipe_p5.io.final_out_prdy 
    chn_data_out := pipe_p5.io.chn_data_out
    chn_out_pvld := pipe_p5.io.chn_out_pvld



    
}

class NV_NVDLA_CSC_PRA_CELL_pipe_p1 extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //input 
        val chn_data_in = Input(Vec(16, SInt(16.W)))
        val chn_in_pvld = Input(Bool())
        val din_prdy = Input(Bool())

        //output
        val chn_data_reg = Output(Vec(16, SInt(16.W)))
        val chn_in_prdy = Output(Bool())
        val din_pvld = Output(Bool())

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

    val p1_skid_valid = RegInit(false.B)
    val p1_skid_ready_flop = RegInit(true.B)
    val chn_in_prdy_out = RegInit(true.B)
    val p1_skid_pipe_ready = Wire(Bool())
    val p1_skid_data = RegInit(VecInit(Seq.fill(16)(0.asSInt(16.W))))

    val p1_skid_catch = io.chn_in_pvld && p1_skid_ready_flop && !p1_skid_pipe_ready
    val p1_skid_ready = Mux(p1_skid_valid, p1_skid_pipe_ready, !p1_skid_catch)

    p1_skid_valid := Mux(p1_skid_valid, !p1_skid_pipe_ready, p1_skid_catch)
    p1_skid_ready_flop := p1_skid_ready
    chn_in_prdy_out := p1_skid_ready
    p1_skid_data := Mux(p1_skid_catch, io.chn_data_in, p1_skid_data)

    io.chn_in_prdy := chn_in_prdy_out

    val p1_skid_pipe_valid = Mux(p1_skid_ready_flop, io.chn_in_pvld, p1_skid_valid)
    val p1_skid_pipe_data = Mux(p1_skid_ready_flop, io.chn_data_in, p1_skid_data)
    //## pipe (1) valid-ready-bubble-collapse
    val p1_pipe_ready = Wire(Bool())
    val p1_pipe_valid = RegInit(false.B)
    val p1_pipe_ready_bc = p1_pipe_ready || !p1_pipe_valid
    val p1_pipe_data = RegInit(VecInit(Seq.fill(16)(0.asSInt(16.W))))

    p1_pipe_valid := Mux(p1_pipe_ready_bc,  p1_skid_pipe_valid, true.B)
    p1_pipe_data := Mux(p1_pipe_ready_bc && p1_skid_pipe_valid,  p1_skid_pipe_data, p1_pipe_data)

    p1_skid_pipe_ready := p1_pipe_ready_bc
    //## pipe (1) output
    io.din_pvld := p1_pipe_valid
    p1_pipe_ready := io.din_prdy
    io.chn_data_reg := p1_pipe_data
    
}}


class NV_NVDLA_CSC_PRA_CELL_pipe_p2 extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //input 
        val mdout = Input(Vec(16, SInt(17.W)))
        val din_pvld = Input(Bool())
        val mout_prdy = Input(Bool())

        //output
        val mdata_out = Output(Vec(16, SInt(17.W)))
        val din_prdy = Output(Bool())
        val mout_pvld = Output(Bool())

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

    //## pipe (2) skid buffer
    val p2_skid_valid = RegInit(false.B)
    val p2_skid_ready_flop = RegInit(true.B)
    val din_prdy_out = RegInit(true.B)
    val p2_skid_pipe_ready = Wire(Bool())
    val p2_skid_data = RegInit(VecInit(Seq.fill(16)(0.asSInt(17.W))))

    val p2_skid_catch = io.din_pvld && p2_skid_ready_flop && !p2_skid_pipe_ready
    val p2_skid_ready = Mux(p2_skid_valid, p2_skid_pipe_ready, !p2_skid_catch)

    p2_skid_valid := Mux(p2_skid_valid, !p2_skid_pipe_ready, p2_skid_catch)
    p2_skid_ready_flop := p2_skid_ready
    din_prdy_out := p2_skid_ready
    p2_skid_data := Mux(p2_skid_catch, io.mdout, p2_skid_data)

    io.din_prdy := din_prdy_out

    val p2_skid_pipe_valid = Mux(p2_skid_ready_flop, io.din_pvld, p2_skid_valid)
    val p2_skid_pipe_data = Mux(p2_skid_ready_flop, io.mdout, p2_skid_data)
    //## pipe (2) valid-ready-bubble-collapse
    val p2_pipe_ready = Wire(Bool())
    val p2_pipe_valid = RegInit(false.B)
    val p2_pipe_ready_bc = p2_pipe_ready || !p2_pipe_valid
    val p2_pipe_data = RegInit(VecInit(Seq.fill(16)(0.asSInt(17.W))))

    p2_pipe_valid := Mux(p2_pipe_ready_bc,  p2_skid_pipe_valid, true.B)
    p2_pipe_data := Mux(p2_pipe_ready_bc && p2_skid_pipe_valid,  p2_skid_pipe_data, p2_pipe_data)

    p2_skid_pipe_ready := p2_pipe_ready_bc
    //## pipe (1) output
    io.mout_pvld := p2_pipe_valid
    p2_pipe_ready := io.mout_prdy
    io.mdata_out := p2_pipe_data
    
}}


class NV_NVDLA_CSC_PRA_CELL_pipe_p3 extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //input 
        val tdout = Input(Vec(16, SInt(18.W)))
        val mout_pvld = Input(Bool())
        val tout_prdy = Input(Bool())

        //output
        val tdata_out = Output(Vec(16, SInt(18.W)))
        val mout_prdy = Output(Bool())
        val tout_pvld = Output(Bool())

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

    //## pipe (2) skid buffer
    val p3_skid_valid = RegInit(false.B)
    val p3_skid_ready_flop = RegInit(true.B)
    val mout_prdy_out = RegInit(true.B)
    val p3_skid_pipe_ready = Wire(Bool())
    val p3_skid_data = RegInit(VecInit(Seq.fill(16)(0.asSInt(18.W))))

    val p3_skid_catch = io.mout_pvld && p3_skid_ready_flop && !p3_skid_pipe_ready
    val p3_skid_ready = Mux(p3_skid_valid, p3_skid_pipe_ready, !p3_skid_catch)

    p3_skid_valid := Mux(p3_skid_valid, !p3_skid_pipe_ready, p3_skid_catch)
    p3_skid_ready_flop := p3_skid_ready
    mout_prdy_out := p3_skid_ready
    p3_skid_data := Mux(p3_skid_catch, io.tdout, p3_skid_data)

    io.mout_prdy := mout_prdy_out

    val p3_skid_pipe_valid = Mux(p3_skid_ready_flop, io.mout_pvld, p3_skid_valid)
    val p3_skid_pipe_data = Mux(p3_skid_ready_flop, io.tdout, p3_skid_data)
    //## pipe (2) valid-ready-bubble-collapse
    val p3_pipe_ready = Wire(Bool())
    val p3_pipe_valid = RegInit(false.B)
    val p3_pipe_ready_bc = p3_pipe_ready || !p3_pipe_valid
    val p3_pipe_data = RegInit(VecInit(Seq.fill(16)(0.asSInt(18.W))))

    p3_pipe_valid := Mux(p3_pipe_ready_bc,  p3_skid_pipe_valid, true.B)
    p3_pipe_data := Mux(p3_pipe_ready_bc && p3_skid_pipe_valid,  p3_skid_pipe_data, p3_pipe_data)

    p3_skid_pipe_ready := p3_pipe_ready_bc
    //## pipe (1) output
    io.tout_pvld := p3_pipe_valid
    p3_pipe_ready := io.tout_prdy
    io.tdata_out := p3_pipe_data
    
}}

class NV_NVDLA_CSC_PRA_CELL_pipe_p4 extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //input 
        val tru_dout_int16 = Input(Vec(16, SInt(16.W)))
        val tru_dout_int8_ext = Input(Vec(16, SInt(16.W))) 
        val final_out_prdy = Input(Bool())
        val tout_pvld = Input(Bool())

        //output
        val tru_data_out_int16 = Output(Vec(16, SInt(16.W)))
        val tru_data_out_int8 = Output(Vec(16, SInt(16.W)))
        val final_out_pvld = Output(Bool())
        val tout_prdy = Output(Bool())

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

    //## pipe (2) skid buffer
    val p4_skid_valid = RegInit(false.B)
    val p4_skid_ready_flop = RegInit(true.B)
    val tout_prdy_out = RegInit(true.B)
    val p4_skid_pipe_ready = Wire(Bool())
    val p4_skid_data_0 = RegInit(VecInit(Seq.fill(16)(0.asSInt(16.W))))
    val p4_skid_data_1 = RegInit(VecInit(Seq.fill(16)(0.asSInt(16.W))))

    val p4_skid_catch = io.tout_pvld && p4_skid_ready_flop && !p4_skid_pipe_ready
    val p4_skid_ready = Mux(p4_skid_valid, p4_skid_pipe_ready, !p4_skid_catch)

    p4_skid_valid := Mux(p4_skid_valid, !p4_skid_pipe_ready, p4_skid_catch)
    p4_skid_ready_flop := p4_skid_ready
    tout_prdy_out := p4_skid_ready
    p4_skid_data_0 := Mux(p4_skid_catch, io.tru_dout_int16, p4_skid_data_0)
    p4_skid_data_1 := Mux(p4_skid_catch, io.tru_dout_int8_ext, p4_skid_data_1)

    io.tout_prdy := tout_prdy_out

    val p4_skid_pipe_valid = Mux(p4_skid_ready_flop, io.tout_pvld, p4_skid_valid)
    val p4_skid_pipe_data_0 = Mux(p4_skid_ready_flop, io.tru_dout_int16, p4_skid_data_0)
    val p4_skid_pipe_data_1 = Mux(p4_skid_ready_flop, io.tru_dout_int8_ext, p4_skid_data_1)

    //## pipe (4) valid-ready-bubble-collapse
    val p4_pipe_ready = Wire(Bool())
    val p4_pipe_valid = RegInit(false.B)
    val p4_pipe_ready_bc = p4_pipe_ready || !p4_pipe_valid
    val p4_pipe_data_0 = RegInit(VecInit(Seq.fill(16)(0.asSInt(16.W))))
    val p4_pipe_data_1 = RegInit(VecInit(Seq.fill(16)(0.asSInt(16.W))))

    p4_pipe_valid := Mux(p4_pipe_ready_bc,  p4_skid_pipe_valid, true.B)
    p4_pipe_data_0 := Mux(p4_pipe_ready_bc && p4_skid_pipe_valid,  p4_skid_pipe_data_0, p4_pipe_data_0)
    p4_pipe_data_1 := Mux(p4_pipe_ready_bc && p4_skid_pipe_valid,  p4_skid_pipe_data_1, p4_pipe_data_1)

    p4_skid_pipe_ready := p4_pipe_ready_bc
    //## pipe (1) output
    io.final_out_pvld := p4_pipe_valid
    p4_pipe_ready := io.final_out_prdy
    io.tru_data_out_int16 := p4_pipe_data_0
    io.tru_data_out_int8 := p4_pipe_data_1

}}

class NV_NVDLA_CSC_PRA_CELL_pipe_p5 extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //input 
        val chn_dout = Input(Vec(16, SInt(16.W)))
        val final_out_pvld = Input(Bool())
        val chn_out_prdy = Input(Bool())

        //output
        val chn_data_out = Output(Vec(16, SInt(16.W)))
        val final_out_prdy = Output(Bool())
        val chn_out_pvld = Output(Bool())

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

    //## pipe (2) skid buffer
    val p5_skid_valid = RegInit(false.B)
    val p5_skid_ready_flop = RegInit(true.B)
    val final_out_prdy_out = RegInit(true.B)
    val p5_skid_pipe_ready = Wire(Bool())
    val p5_skid_data = RegInit(VecInit(Seq.fill(16)(0.asSInt(16.W))))

    val p5_skid_catch = io.final_out_pvld && p5_skid_ready_flop && !p5_skid_pipe_ready
    val p5_skid_ready = Mux(p5_skid_valid, p5_skid_pipe_ready, !p5_skid_catch)

    p5_skid_valid := Mux(p5_skid_valid, !p5_skid_pipe_ready, p5_skid_catch)
    p5_skid_ready_flop := p5_skid_ready
    final_out_prdy_out := p5_skid_ready
    p5_skid_data := Mux(p5_skid_catch, io.chn_dout, p5_skid_data)

    io.final_out_prdy := final_out_prdy_out

    val p5_skid_pipe_valid = Mux(p5_skid_ready_flop, io.final_out_pvld, p5_skid_valid)
    val p5_skid_pipe_data = Mux(p5_skid_ready_flop, io.chn_dout, p5_skid_data)
    //## pipe (2) valid-ready-bubble-collapse
    val p5_pipe_ready = Wire(Bool())
    val p5_pipe_valid = RegInit(false.B)
    val p5_pipe_ready_bc = p5_pipe_ready || !p5_pipe_valid
    val p5_pipe_data = RegInit(VecInit(Seq.fill(16)(0.asSInt(16.W))))

    p5_pipe_valid := Mux(p5_pipe_ready_bc,  p5_skid_pipe_valid, true.B)
    p5_pipe_data := Mux(p5_pipe_ready_bc && p5_skid_pipe_valid,  p5_skid_pipe_data, p5_pipe_data)

    p5_skid_pipe_ready := p5_pipe_ready_bc
    //## pipe (1) output
    io.chn_out_pvld := p5_pipe_valid
    p5_pipe_ready := io.chn_out_prdy
    io.chn_data_out := p5_pipe_data
    
}}




object NV_NVDLA_CSC_pra_cellDriver extends App {
  implicit val conf: cscConfiguration = new cscConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_CSC_pra_cell())
}

