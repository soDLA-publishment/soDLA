package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_core_reset extends Module {
    val io = IO(new Bundle {
        val dla_reset_rstn = Input(Bool())
        val direct_reset_ = Input(Bool())
        val test_mode = Input(Bool())
        val synced_rstn = Output(Bool())
        val core_reset_rstn = Input(Bool()) 
        val nvdla_clk = Input(Clock())
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
    val sync_reset_synced_dla_rstn = Module(new sync_reset)
    sync_reset_synced_dla_rstn.io.clk := io.nvdla_clk
    sync_reset_synced_dla_rstn.io.inreset_ := io.dla_reset_rstn
    sync_reset_synced_dla_rstn.io.direct_reset_ := io.direct_reset_
    sync_reset_synced_dla_rstn.io.test_mode := io.test_mode
    val synced_dla_rstn = sync_reset_synced_dla_rstn.io.outreset_ 

    val sync_reset_synced_core_rstn = Module(new sync_reset)
    sync_reset_synced_core_rstn.io.clk := io.nvdla_clk
    sync_reset_synced_core_rstn.io.inreset_ := io.core_reset_rstn
    sync_reset_synced_core_rstn.io.direct_reset_ := io.direct_reset_
    sync_reset_synced_core_rstn.io.test_mode := io.test_mode
    val synced_core_rstn = sync_reset_synced_core_rstn.io.outreset_  

    val combined_rstn = withClockAndReset(io.nvdla_clk, !synced_dla_rstn){
                        RegInit(synced_dla_rstn & synced_core_rstn, false.B)
                        }

    val sync_reset_synced_rstn = Module(new sync_reset)
    sync_reset_synced_rstn.io.clk := io.nvdla_clk
    sync_reset_synced_rstn.io.inreset_ := combined_rstn
    sync_reset_synced_rstn.io.direct_reset_ := io.direct_reset_
    sync_reset_synced_rstn.io.test_mode := io.test_mode
    io.synced_rstn := sync_reset_synced_rstn.io.outreset_    
    
}