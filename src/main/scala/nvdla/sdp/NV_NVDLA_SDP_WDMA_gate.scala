package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.Driver

class NV_NVDLA_SDP_WDMA_gate extends Module {
   val io = IO(new Bundle {
        //in clock
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val nvdla_core_clk = Input(Clock())

        //enable
        val reg2dp_wdma_slcg_op_en = Input(Bool())
        val tmc2slcg_disable_clock_gating = Input(Bool())

        //out clock
        val nvdla_gated_clk = Output(Clock())

    })
    val nvdla_core_clk_slcg_0_en = io.reg2dp_wdma_slcg_op_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
                                   (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)

    val nvdla_core_clk_slcg_0 = Module(new NV_CLK_gate_power)
    nvdla_core_clk_slcg_0.io.clk := io.nvdla_core_clk
    nvdla_core_clk_slcg_0.io.clk_en := nvdla_core_clk_slcg_0_en
    io.nvdla_gated_clk := nvdla_core_clk_slcg_0.io.clk_gated 
            
}


object NV_NVDLA_SDP_WDMA_gateDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_WDMA_gate())
}


