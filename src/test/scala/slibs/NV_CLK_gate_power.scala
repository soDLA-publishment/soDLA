package slibs

import chisel3._



class NV_CLK_gate_power(implicit val conf: cdpConfiguration)  extends Module {
   val io = IO(new Bundle {
        //nvdla core clock
        val clk = Input(Clock())
        val reset_ = Input(Bool())

        //control signal
        val clk_en = Input(Bool())
        val clk_gated = Output(Bool()

    })

    if(conf.BYPASS_POWER_CG){
        clk_gated = io.clk
    }
    else{
        val p_clkgate = Module(new CKLNQD12())
        p_clkgate.io.TE := false
        p_clkgate.io.CP := io.clk
        p_clkgate.io.E := io.clk_en
        p_clkgate.io.Q := io.clk_gated
    } 

}

