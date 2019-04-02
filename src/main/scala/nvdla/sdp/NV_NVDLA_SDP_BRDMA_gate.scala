package nvdla

import chisel3._
import chisel3.experimental._

class NV_NVDLA_SDP_BRDMA_gate extends Module {
   val io = IO(new Bundle {
        //in clock
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val nvdla_core_clk = Input(Clock())

        //enable
        val brdma_disable = Input(Bool())
        val brdma_slcg_op_en = Input(Bool())
        val tmc2slcg_disable_clock_gating = Input(Bool())

        //out clock
        val nvdla_gated_clk = Output(Clock())

    })

    val brdma_enable = RegInit(false.B)
    brdma_enable := !io.brdma_disable

    val cfg_clk_en = io.brdma_slcg_op_en & brdma_enable


    val nvdla_core_clk_slcg_0_en = cfg_clk_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
                                   (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)                                     

    io.nvdla_gated_clk := (io.nvdla_core_clk.asUInt.toBool & nvdla_core_clk_slcg_0_en).asClock()

}

