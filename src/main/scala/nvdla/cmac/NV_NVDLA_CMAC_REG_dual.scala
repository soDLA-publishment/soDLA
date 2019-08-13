package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CMAC_REG_dual extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg_control = new reg_control_if

        // Writable register flop/trigger outputs
        val reg_flop = new cmac_reg_flop_outputs
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
    withClock(io.nvdla_core_clk){

    // Address decode

    val nvdla_cmac_a_d_misc_cfg_0_wren = (io.reg_control.offset === "hc".asUInt(32.W))&io.reg_control.wr_en
    val nvdla_cmac_a_d_op_enable_0_wren = (io.reg_control.offset === "h8".asUInt(32.W))&io.reg_control.wr_en
    
    io.op_en_trigger := nvdla_cmac_a_d_op_enable_0_wren

    // Output mux

    io.reg_control.rd_data := MuxLookup(io.reg_control.offset, "b0".asUInt(32.W), 
    Seq( 
    //nvdla_cmac_a_d_misc_cfg_0_out         
    "hc".asUInt(32.W)  -> Cat("b0".asUInt(18.W), io.reg_flop.proc_precision, "b0".asUInt(11.W), io.reg_flop.conv_mode),
    //nvdla_cmac_a_d_op_enable_0_out
    "h8".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.op_en)
    ))

    // Register flop declarations
        
    io.reg_flop.conv_mode := RegEnable(io.reg_control.wr_data(0), false.B, nvdla_cmac_a_d_misc_cfg_0_wren)
    io.reg_flop.proc_precision := RegEnable(io.reg_control.wr_data(13, 12), "b01".U, nvdla_cmac_a_d_op_enable_0_wren)

}}