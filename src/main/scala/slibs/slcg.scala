package nvdla

import chisel3._
import chisel3.experimental._

class NV_NVDLA_slcg extends Module {
   val io = IO(new Bundle {
        //in clock
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val nvdla_core_clk = Input(Clock())

        //enable
        val slcg_en_src_0 = Input(Bool())
        val slcg_en_src_1 = Input(Bool())
        val tmc2slcg_disable_clock_gating = Input(Bool())

        //out clock
        val nvdla_core_gated_clk = Output(Clock())

    })
    val enable = io.slcg_en_src_0 & io.slcg_en_src_1 
    val nvdla_core_clk_slcg_0_en = enable | io.dla_clk_ovr_on_sync.asUInt.toBool |
                                   (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)

    val nvdla_core_clk_slcg_0 = Module(new NV_CLK_gate_power)
    nvdla_core_clk_slcg_0.io.clk := io.nvdla_core_clk
    nvdla_core_clk_slcg_0.io.clk_en := nvdla_core_clk_slcg_0_en
    io.nvdla_core_gated_clk := nvdla_core_clk_slcg_0.io.clk_gated
}

