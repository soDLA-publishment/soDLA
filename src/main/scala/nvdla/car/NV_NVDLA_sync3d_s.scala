package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_sync3d_s extends Module {
    val io = IO(new Bundle {
        val clk = Input(Clock())
        val prst = Input(Bool())
        val sync_i = Input(Clock())
        val sync_o = Output(Clock())

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

val sync_ibus_preDFTxclamp = io.sync_i.asUInt.toBool

val UJ_dft_xclamp_ctrl_hold_sync_i = Module(new NV_BLKBOX_SRC0)
val dft_xclamp_hold_mux_s_sync_i = UJ_dft_xclamp_ctrl_hold_sync_i.io.Y

val UJ_dft_xclamp_scan_hold_sync_i = Module(new NV_BLKBOX_SRC0)
val dft_xclamp_hold_mux_i1_sync_i = UJ_dft_xclamp_scan_hold_sync_i.io.Y

val UJ_FP_MUX_sync_i_dft_xclamp_before_sync = Module(new MUX2HDD2)
UJ_FP_MUX_sync_i_dft_xclamp_before_sync.io.S := dft_xclamp_hold_mux_s_sync_i
UJ_FP_MUX_sync_i_dft_xclamp_before_sync.io.I0 := sync_ibus_preDFTxclamp
UJ_FP_MUX_sync_i_dft_xclamp_before_sync.io.I1 := dft_xclamp_hold_mux_i1_sync_i
val sync_ibus = UJ_FP_MUX_sync_i_dft_xclamp_before_sync.io.Z

// random bus
val sync_rbus = sync_ibus

// buffer bus
val sync_bbus = sync_rbus

val sync_0 = Module(new sync3d_s_ppp)
sync_0.io.clk := io.clk
sync_0.io.set_ := io.prst
sync_0.io.d := sync_bbus
val sync_sbus = sync_0.io.q

io.sync_o := sync_sbus.asClock

}