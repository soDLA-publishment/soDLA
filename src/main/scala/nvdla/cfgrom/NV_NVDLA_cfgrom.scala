package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_cfgrom extends Module {

    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())     

        //csb2cfgrom
        val csb2cfgrom = new csb2dp_if
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
                
withClock(io.nvdla_core_clk) { 
    
////////////////////////////////////////////////////////////////////////
    val reg_offset = Wire(UInt(24.W))
    val reg_wr_data = Wire(UInt(32.W))
    val reg_wr_en = Wire(Bool())

    val u_NV_NVDLA_CFGROM_rom = Module(new NV_NVDLA_CFGROM_rom)

    u_NV_NVDLA_CFGROM_rom.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CFGROM_rom.io.reg.offset := reg_offset(11, 0)
    u_NV_NVDLA_CFGROM_rom.io.reg.wr_data := reg_wr_data
    u_NV_NVDLA_CFGROM_rom.io.reg.wr_en := reg_wr_en
    val reg_rd_data = u_NV_NVDLA_CFGROM_rom.io.reg.rd_data 

////////////////////////////////////////////////////////////////////////
//                                                                    //
// GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
//                                                                    //
////////////////////////////////////////////////////////////////////////
    val csb_logic = Module(new NV_NVDLA_CSB_LOGIC)
    csb_logic.io.clk := io.nvdla_core_clk
    csb_logic.io.csb2dp <> io.csb2cfgrom
    reg_offset := csb_logic.io.reg.offset
    reg_wr_en := csb_logic.io.reg.wr_en
    reg_wr_data := csb_logic.io.reg.wr_data
    csb_logic.io.reg.rd_data := reg_rd_data 

}}

object NV_NVDLA_cfgromDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_cfgrom())
}
