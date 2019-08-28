package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.experimental.chiselName

@chiselName
class NV_NVDLA_CMAC_REG_dual(useRealClock: Boolean = false) extends Module {
  val io = IO(new Bundle {
    // clk
    val nvdla_core_clk = Input(Clock())

    // Register control interface
    val reg = new reg_control_if

    // Writable register flop/trigger outputs
    val field = new cmac_reg_dual_flop_outputs
    val op_en_trigger = Output(Bool())

    // Read-only register input
    val op_en = Input(Bool())
  })
  //
  //          ┌─┐       ┌─┐
  //       ┌──┘ ┴───────┘ ┴──┐
  //       │                 │
  //       │       ───       │
  //       │  ─┬┘       └┬─  │
  //       │                 │
  //       │       ─┴─       │
  //       │                 │
  //       └───┐         ┌───┘
  //           │         │
  //           │         │
  //           │         │
  //           │         └──────────────┐
  //           │                        │
  //           │                        ├─┐
  //           │                        ┌─┘
  //           │                        │
  //           └─┐  ┐  ┌───────┬──┐  ┌──┘
  //             │ ─┤ ─┤       │ ─┤ ─┤
  //             └──┴──┘       └──┴──┘

  val internal_clock = if (useRealClock) io.nvdla_core_clk else clock

  class dualImp {

    // Address decode
    val nvdla_cmac_a_d_misc_cfg_0_wren = (io.reg.offset === "hc".asUInt(32.W)) & io.reg.wr_en
    val nvdla_cmac_a_d_op_enable_0_wren = (io.reg.offset === "h8".asUInt(32.W)) & io.reg.wr_en

    io.op_en_trigger := nvdla_cmac_a_d_op_enable_0_wren

    // Output mux

    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W),
      Seq(
        //nvdla_cmac_a_d_misc_cfg_0_out
        "hc".asUInt(32.W) -> Cat("b0".asUInt(18.W), io.field.proc_precision, "b0".asUInt(11.W), io.field.conv_mode),
        //nvdla_cmac_a_d_op_enable_0_out
        "h8".asUInt(32.W) -> Cat("b0".asUInt(31.W), io.op_en)
      ))

    // Register flop declarations

    io.field.conv_mode := RegEnable(io.reg.wr_data(0), false.B, nvdla_cmac_a_d_misc_cfg_0_wren)

    //TODO: need to check logic!!   in nvdla generator code there is nvdla_cmac_a_d_misc_cfg_0_wren instead of nvdla_cmac_a_d_op_enable_0_wren
    // and there must use  <"b01".asUInt(2.W)> instead of <"b01">, otherwise chisel will optimizate proc_precision to 1bit!
    io.field.proc_precision := RegEnable(io.reg.wr_data(13, 12), "b01".asUInt(2.W), nvdla_cmac_a_d_misc_cfg_0_wren)
  }

  val dual = withClock(internal_clock){new dualImp}
}