package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

@chiselName
class NV_NVDLA_BASIC_REG_single extends Module {
    val io = IO(new Bundle {
      // clk
      val nvdla_core_clk = Input(Clock())

      // Register control interface
      val reg = new reg_control_if

      // Writable register flop/trigger outputs
      val producer = Output(Bool())

      // Read-only register input
      val consumer = Input(Bool())
      val status_0 = Input(UInt(2.W))
      val status_1 = Input(UInt(2.W))
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
withClock(io.nvdla_core_clk){
    // Address decode
    val s_pointer_0_wren = (io.reg.offset === "h4".asUInt(32.W)) & io.reg.wr_en
    val s_status_0_wren = (io.reg.offset === "h0".asUInt(32.W)) & io.reg.wr_en

    // Output mux  
    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W),
      Seq(
        //s_pointer_0_out
        "h4".asUInt(32.W) -> Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer),
        //s_status_0_out
        "h0".asUInt(32.W) -> Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)
      ))

    // Register flop declarations
    val reg_pointer = RegEnable(io.reg.wr_data(0), false.B, s_pointer_0_wren)
    io.producer := reg_pointer
}}


object NV_NVDLA_BASIC_REG_singleDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_BASIC_REG_single)
}
