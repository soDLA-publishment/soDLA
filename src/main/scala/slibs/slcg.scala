package nvdla

import chisel3._
import chisel3.experimental._

class NV_NVDLA_slcg(input_num: Int, io_slcg_disable: Boolean) extends Module {
   val io = IO(new Bundle {
        //in clock
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val nvdla_core_clk = Input(Clock())

        //enable
        val slcg_disable = if(io_slcg_disable) Some(Input(Bool())) else None  
        val slcg_en = Input(Vec(input_num, Bool()))
        val tmc2slcg_disable_clock_gating = Input(Bool())

        //out clock
        val nvdla_core_gated_clk = Output(Clock())

    })

withClock(io.nvdla_core_clk){  

    val slcg_enable = if(io_slcg_disable) Some(RegInit(false.B)) else None
    val cfg_clk_en = Wire(Bool())
    if(io_slcg_disable){
        slcg_enable.get := !io.slcg_disable.get
        cfg_clk_en := io.slcg_en.asUInt.andR & slcg_enable.get
    }
    else{
        cfg_clk_en := io.slcg_en.asUInt.andR
    }
    
    val nvdla_core_clk_slcg_en = cfg_clk_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
                                   (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)

    val nvdla_core_clk_slcg = Module(new NV_CLK_gate_power)
    nvdla_core_clk_slcg.io.clk := io.nvdla_core_clk
    nvdla_core_clk_slcg.io.clk_en := nvdla_core_clk_slcg_en
    io.nvdla_core_gated_clk := nvdla_core_clk_slcg.io.clk_gated
}}


