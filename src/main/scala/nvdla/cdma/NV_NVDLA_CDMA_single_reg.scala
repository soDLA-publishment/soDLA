package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDMA_single_reg extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg = new reg_control_if

        // Writable register flop/trigger outputs
        val producer = Output(Bool())
        val arb_weight = Output(UInt(4.W))
        val arb_wmb = Output(UInt(4.W))

        // Read-only register inputs
        val flush_done = Input(Bool())
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
// ///// Address decode
    val nvdla_cdma_s_arbiter_0_wren = (io.reg.offset === "h8".asUInt(32.W))&io.reg.wr_en
    val nvdla_cdma_s_cbuf_flush_status_0_wren = (io.reg.offset === "hc".asUInt(32.W))&io.reg.wr_en
    val nvdla_cdma_s_pointer_0_wren = (io.reg.offset === "h4".asUInt(32.W))&io.reg.wr_en
    val nvdla_cdma_s_status_0_wren = (io.reg.offset === "h0".asUInt(32.W))&io.reg.wr_en

// ///// Output mux  
    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(  
    //nvdla_cdma_s_arbiter_0_out    
    "h8".asUInt(32.W)  -> Cat("b0".asUInt(12.W), io.arb_wmb, "b0".asUInt(15.W), io.producer),
    //nvdla_cdma_s_cbuf_flush_status_0_out
    "hc".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.flush_done),
    //nvdla_cdma_s_pointer_0_out
    "h4".asUInt(32.W)  -> Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer),
    //nvdla_cdma_s_status_0_out
    "h0".asUInt(32.W)  -> Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)
    ))

// ///// Register flop declarations    
    io.arb_weight := RegEnable(io.reg.wr_data(3, 0), "b1111".asUInt(4.W), nvdla_cdma_s_arbiter_0_wren)
    io.arb_wmb := RegEnable(io.reg.wr_data(19, 16), "b0011".asUInt(4.W), nvdla_cdma_s_arbiter_0_wren)
    io.producer := RegEnable(io.reg.wr_data(0), false.B, nvdla_cdma_s_pointer_0_wren)
    
}

