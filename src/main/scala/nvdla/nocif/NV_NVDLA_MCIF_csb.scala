package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_MCIF_WRITE_iq extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())

        //csb2mcif
        val csb2mcif = new csb2dp_if

        //reg2dp
        val dp2reg_idle = Input(Bool())
        val reg2dp_field = new mcif_reg_flop_outputs
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

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // Instantiate register                                               //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    val csb_logic = Module(new NV_NVDLA_CSB_LOGIC)
    val csb_reg = Module(new NV_NVDLA_MCIF_CSB_reg)
    csb_logic.io.clk := io.nvdla_core_clk
    csb_reg.io.nvdla_core_clk := io.nvdla_core_clk
    csb_logic.io.csb2dp <> io.csb2mcif
    csb_logic.io.reg <> csb_reg.io.reg
    csb_reg.io.idle := io.dp2reg_idle
    io.reg2dp_field <> csb_reg.io.field

}}
