package nvdla

import chisel3._
import chisel3.experimental._

class NV_NVDLA_SDP_CORE_gate extends Module {
   val io = IO(new Bundle {
        //in clock
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val nvdla_core_clk = Input(Clock())

        //enable
        val bcore_slcg_en= Input(Bool())
        val ecore_slcg_en = Input(Bool())
        val ncore_slcg_en = Input(Bool())
        val tmc2slcg_disable_clock_gating = Input(Bool())

        //out clock
        val nvdla_gated_bcore_clk = Output(Clock())
        val nvdla_gated_ecore_clk = Output(Clock())
        val nvdla_gated_ncore_clk = Output(Clock())

    })
    val nvdla_core_clk_slcg_0_en = io.bcore_slcg_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
                                   (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)
    val nvdla_core_clk_slcg_1_en = io.ecore_slcg_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
                                   (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)
    val nvdla_core_clk_slcg_2_en = io.ncore_slcg_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
                                   (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)                                      

    io.nvdla_gated_bcore_clk := (io.nvdla_core_clk.asUInt.toBool & nvdla_core_clk_slcg_0_en).asClock()
    io.nvdla_gated_ecore_clk := (io.nvdla_core_clk.asUInt.toBool & nvdla_core_clk_slcg_1_en).asClock()
    io.nvdla_gated_ncore_clk := (io.nvdla_core_clk.asUInt.toBool & nvdla_core_clk_slcg_2_en).asClock()


}

