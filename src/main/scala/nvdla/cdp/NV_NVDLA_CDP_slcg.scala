package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_slcg extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val slcg_en_src = Input(Bool())
        val tmc2slcg_disable_clock_gating = Input(Bool())
        val nvdla_core_gated_clk = Output(Clock())
    })

    withClock(io.nvdla_core_clk){
        val enable = io.slcg_en_src

        val nvdla_core_clk_slcg_0_en = enable | 
                        (io.dla_clk_ovr_on_sync.asUInt.toBool | 
                        ((io.tmc2slcg_disable_clock_gating | 
                        io.global_clk_ovr_on_sync.asUInt.toBool)))

        val nvdla_core_clk_slcg_0 = Module(new NV_CLK_gate_power)
        nvdla_core_clk_slcg_0.io.clk := io.nvdla_core_clk
        nvdla_core_clk_slcg_0.io.clk_en := nvdla_core_clk_slcg_0_en
        io.nvdla_core_gated_clk := nvdla_core_clk_slcg_0.io.clk_gated

    }
    
}


object NV_NVDLA_CDP_slcgDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_slcg())
}

