package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CMAC_REG_dual extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))
        val reg_wr_en = Input(Bool())

        // Writable register flop/trigger outputs
        val conv_mode = Output(UInt(1.W))
        val proc_precision = Output(UInt(2.W))
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

    val nvdla_cmac_a_d_misc_cfg_0_wren = (io.reg_offset === "hc".asUInt(32.W))&io.reg_wr_en
    val nvdla_cmac_a_d_op_enable_0_wren = (io.reg_offset === "h8".asUInt(32.W))&io.reg_wr_en
    
    val nvdla_cmac_a_d_misc_cfg_0_out = Cat("b0".asUInt(18.W), io.proc_precision, "b0".asUInt(11.W), io.conv_mode)
    val nvdla_cmac_a_d_op_enable_0_out =  Cat("b0".asUInt(31.W), io.op_en)

    io.op_en_trigger := nvdla_cmac_a_d_op_enable_0_wren

    // Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
    "hc".asUInt(32.W)  -> nvdla_cmac_a_d_misc_cfg_0_out,
    "h8".asUInt(32.W)  -> nvdla_cmac_a_d_op_enable_0_out
    ))

    // Register flop declarations

    val conv_mode_out = RegInit(false.B)
    val proc_precision_out = RegInit("b01".U)

    when(nvdla_cmac_a_d_misc_cfg_0_wren){
        conv_mode_out:= io.reg_wr_data(0)
    }
    when(nvdla_cmac_a_d_op_enable_0_wren){
        proc_precision_out:= io.reg_wr_data(13, 12)
    }
        
    io.conv_mode := conv_mode_out
    io.proc_precision := proc_precision_out

}}