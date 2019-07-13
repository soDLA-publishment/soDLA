package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

class NV_NVDLA_SDP_RDMA_gate extends Module {
   val io = IO(new Bundle {
        //in clock
        val nvdla_core_clk = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val dla_clk_ovr_on_sync = Input(Clock())

        //enable
        val rdma_disable = Input(Bool())
        val rdma_slcg_op_en = Input(Bool())
        val tmc2slcg_disable_clock_gating = Input(Bool())

        //out clock
        val nvdla_gated_clk = Output(Clock())

    })

    val rdma_enable = RegInit(false.B)
    rdma_enable := !io.rdma_disable

    val cfg_clk_en = io.rdma_slcg_op_en & rdma_enable


    val nvdla_core_clk_slcg_0_en = cfg_clk_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
                                   (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)                                     

    val nvdla_core_clk_slcg_0 = Module(new NV_CLK_gate_power)
    nvdla_core_clk_slcg_0.io.clk := io.nvdla_core_clk
    nvdla_core_clk_slcg_0.io.clk_en := nvdla_core_clk_slcg_0_en
    io.nvdla_gated_clk := nvdla_core_clk_slcg_0.io.clk_gated

}

object NV_NVDLA_SDP_RDMA_gateDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_gate())
}