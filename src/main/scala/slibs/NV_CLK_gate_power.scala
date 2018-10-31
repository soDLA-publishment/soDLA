package slibs

import chisel3._


class NV_CLK_gate_power(BYPASS_POWER_CG:Boolean = false)  extends Module {
   val io = IO(new Bundle {
        //nvdla core clock
        val clk = Input(Clock())
        val reset_ = Input(Bool())

        //control signal
        val clk_en = Input(Bool())
        val clk_gated = Output(Bool()

    })

    if(BYPASS_POWER_CG){
        clk_gated = io.clk
    }
    else{
        val p_clkgate = Module(new CKLNQD12())
        p_clkgate.io.TE := false.B
        io.clk := p_clkgate.io.CP
        io.clk_en := p_clkgate.io.E
        io.clk_gated := p_clkgate.io.Q
    } 

}

