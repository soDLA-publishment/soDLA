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

    io.chn_in_prdy := Reg(Bool())
    val p1_pipe_data = Reg(UInt(9.W))
    val p1_pipe_ready = Wire(Bool())//actually wire
    val p1_pipe_ready_bc = Wire(Bool())//actually wire
    val p1_pipe_valid = Reg(Bool())
    val p1_skid_catch = Wire(Bool())    //actually wire
    val p1_skid_data = Reg(UInt(9.W))
    val p1_skid_pipe_data = Wire(UInt(9.W))    //actually wire
    val p1_skid_pipe_ready = Wire(Bool()) //actually wire
    val p1_skid_pipe_valid = Wire(Bool())    //actually wire
    val p1_skid_ready = Wire(Bool())    //actually wire
    val p1_skid_ready_flop = Reg(Bool())
    val p1_skid_valid = Reg(Bool())
    io.sub_data_out = Wire(UInt(9.W))//actually wire
    io.sub_out_pvld = Wire(Bool())//actually wire

    //## pipe (1) skid buffer
    p1_skid_catch := io.chn_in_pvld && p1_skid_ready_flop && !p1_skid_pipe_ready
    p1_skid_ready := Mux(p1_skid_valid, p1_skid_pipe_ready, !p1_skid_catch)


    withClock((io.nvdla_core_clk)|(!io.nvdla_core_rstn.asClock)) {
        when(!io.nvdla_core_rstn){
            p1_skid_valid:=false.B
            p1_skid_ready_flop:=true.B
            io.chn_in_prdy:= true.B
        }
        .otherwise{
            p1_skid_valid:= Mux(p1_skid_valid, p1_skid_pipe_ready, !p1_skid_catch)
            p1_skid_ready_flop:=p1_skid_ready 
            io.chn_in_prdy:=p1_skid_ready
        }
    } 

    withClock(io.nvdla_core_clk) {
        p1_skid_data := Mux(p1_skid_catch, io.sub_dout, p1_skid_data)
    } 

    p1_skid_pipe_valid := Mux(p1_skid_ready_flop,  io.chn_in_pvld, p1_skid_valid)
    p1_skid_pipe_data := Mux(p1_skid_ready_flop,  io.sub_dout, p1_skid_data)

    //## pipe (1) valid-ready-bubble-collapse
    p1_pipe_ready_bc:= p1_pipe_ready||!p1_pipe_valid

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
        p1_pipe_valid := Mux(p1_pipe_ready_bc,  p1_skid_pipe_valid, true.B)
    }

    withClock(io.nvdla_core_clk) {
        p1_pipe_data := Mux(p1_pipe_ready_bc&&p1_skid_pipe_valid,  p1_skid_pipe_data, p1_pipe_data)
    }
    
    p1_skid_pipe_ready:= p1_pipe_ready_bc
    //## pipe (1) output
    io.sub_out_pvld := p1_pipe_valid
    p1_pipe_ready := io.sub_out_prdy
    io.sub_data_out:= p1_pipe_data

}

class HLS_cdp_ICVT_pipe_p2 extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //
        val mul_dout = Input(UInt(25.W))
        val mul_out_prdy = Input(Bool())
        val sub_out_pvld = Input(Bool())
        val mul_data_out = Input(UInt(25.W))
        val mul_out_pvld = Output(Bool())
        val sub_out_prdy = Output(Bool())
    })

    io.mul_data_out = Reg(UInt(25.W))
    io.mul_out_pvld = Reg(Bool())
    io.sub_out_prdy = Reg(Bool())

    val p2_pipe_data = Reg(UInt(25.W))
    val p2_pipe_ready = Wire(Bool())//actually wire
    val p2_pipe_ready_bc = Wire(Bool())//actually wire
    val p2_pipe_valid = Reg(Bool())
    val p2_skid_catch = Wire(Bool())    //actually wire
    val p2_skid_data = Reg(UInt(25.W))
    val p2_skid_pipe_data = Wire(UInt(25.W))    //actually wire
    val p2_skid_pipe_ready = Wire(Bool()) //actually wire
    val p2_skid_pipe_valid = Wire(Bool())    //actually wire
    val p2_skid_ready = Wire(Bool())    //actually wire
    val p2_skid_ready_flop = Reg(Bool())
    val p2_skid_valid = Reg(Bool())
    

    //## pipe (2) skid buffer
    p2_skid_catch := io.sub_out_pvld && p2_skid_ready_flop && !p2_skid_pipe_ready
    p2_skid_ready := Mux(p2_skid_valid, p2_skid_pipe_ready, !p2_skid_catch)


    withClock((io.nvdla_core_clk)|(!io.nvdla_core_rstn.asClock)) {
        when(!io.nvdla_core_rstn){
            p2_skid_valid:=false.B
            p2_skid_ready_flop:=true.B
            io.sub_out_prdy:= true.B
        }
        .otherwise{
            p2_skid_valid:= Mux(p2_skid_valid, !p2_skid_pipe_ready, p2_skid_catch)
            p2_skid_ready_flop:=p2_skid_ready 
            io.sub_out_prdy:=p2_skid_ready
        }
    } 

    withClock(io.nvdla_core_clk) {
        p2_skid_data := Mux(p2_skid_catch, io.mul_dout, p2_skid_data)
    } 

    p2_skid_pipe_valid := Mux(p2_skid_ready_flop,  io.sub_out_pvld, p2_skid_valid)
    p2_skid_pipe_data := Mux(p2_skid_ready_flop,  io.mul_dout, p2_skid_data)

    //## pipe (1) valid-ready-bubble-collapse
    p2_pipe_ready_bc:= p2_pipe_ready||!p2_pipe_valid

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
        p2_pipe_valid := Mux(p2_pipe_ready_bc,  p2_skid_pipe_valid, true.B)
    }

    withClock(io.nvdla_core_clk) {
        p2_pipe_data := Mux(p2_pipe_ready_bc&&p2_skid_pipe_valid,  p2_skid_pipe_data, p2_pipe_data)
    }
    
    p2_skid_pipe_ready:= p2_pipe_ready_bc
    //## pipe (1) output
    io.mul_out_pvld := p2_pipe_valid
    p2_pipe_ready := io.mul_out_prdy
    io.mul_data_out:= p2_pipe_data

}


class HLS_cdp_ICVT_pipe_p3 extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //
        val chn_out_prdy = Input(Bool())
        val mul_out_pvld = Input(Bool())
        val tru_dout = Input(UInt(9.W))
        val chn_data_out = Output(UInt(9.W))
        val chn_out_pvld = Output(Bool())
        val mul_out_prdy  = Output(Bool())

    })

    io.chn_data_out := Reg(UInt(9.W))
    io.chn_out_pvld := Reg(Bool())
    io.mul_out_prdy := Reg(Bool())

    val p3_pipe_data = Reg(UInt(9.W))
    val p3_pipe_ready = Wire(Bool())//actually wire
    val p3_pipe_ready_bc = Wire(Bool())//actually wire
    val p3_pipe_valid = Reg(Bool())
    val p3_skid_catch = Wire(Bool())    //actually wire
    val p3_skid_data = Reg(UInt(9.W))
    val p3_skid_pipe_data = Wire(UInt(9.W))    //actually wire
    val p3_skid_pipe_ready = Wire(Bool()) //actually wire
    val p3_skid_pipe_valid = Wire(Bool())    //actually wire
    val p3_skid_ready = Wire(Bool())    //actually wire
    val p3_skid_ready_flop = Reg(Bool())
    val p3_skid_valid = Reg(Bool())

    //## pipe (3) skid buffer
    p3_skid_catch := io.mul_out_pvld && p3_skid_ready_flop && !p3_skid_pipe_ready
    p3_skid_ready := Mux(p3_skid_valid, p3_skid_pipe_ready, !p3_skid_catch)


    withClock((io.nvdla_core_clk)|(!io.nvdla_core_rstn.asClock)) {
        when(!io.nvdla_core_rstn){
            p3_skid_valid:=false.B
            p3_skid_ready_flop:=true.B
            io.mul_out_prdy:= true.B
        }
        .otherwise{
            p3_skid_valid:= Mux(p3_skid_valid, !p3_skid_pipe_ready, p3_skid_catch)
            p3_skid_ready_flop:=p3_skid_ready 
            io.mul_out_prdy:=p3_skid_ready
        }
    } 

    withClock(io.nvdla_core_clk) {
        p3_skid_data := Mux(p3_skid_catch, io.tru_dout, p3_skid_data)
    } 

    p3_skid_pipe_valid := Mux(p3_skid_ready_flop,  io.mul_out_pvld, p3_skid_valid)
    p3_skid_pipe_data := Mux(p3_skid_ready_flop,  io.tru_dout, p3_skid_data)

    //## pipe (1) valid-ready-bubble-collapse
    p3_pipe_ready_bc:= p3_pipe_ready||!p3_pipe_valid

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
        p3_pipe_valid := Mux(p3_pipe_ready_bc,  p3_skid_pipe_valid, true.B)
    }

    withClock(io.nvdla_core_clk) {
        p3_pipe_data := Mux(p3_pipe_ready_bc&&p3_skid_pipe_valid,  p3_skid_pipe_data, p3_pipe_data)
    }
    
    p3_skid_pipe_ready:= p3_pipe_ready_bc
    //## pipe (1) output
    io.chn_out_pvld := p3_pipe_valid
    p3_pipe_ready := io.chn_out_pvld
    io.chn_data_out:= p3_pipe_data

}












        











    



  






  



