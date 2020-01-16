package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.Driver

class NV_CLK_gate_power(implicit conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        //nvdla core clock
        val clk = Input(Clock())

        //control signal
        val clk_en = Input(Bool())
        val clk_gated = Output(Clock())

    })

    if(conf.useFPGA){
        io.clk_gated := io.clk
    }
    else{
        val p_clkgate = Module(new CKLNQD12())
        p_clkgate.io.TE := false.B
        p_clkgate.io.CP := io.clk
        p_clkgate.io.E := io.clk_en
        io.clk_gated := p_clkgate.io.Q
    
    }
 
}


