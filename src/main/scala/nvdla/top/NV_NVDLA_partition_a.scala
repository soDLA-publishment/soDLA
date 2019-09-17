package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_partition_a(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val test_mode = Input(Bool())
        val global_clk_ovr_on = Input(Clock())
        val nvdla_clk_ovr_on = Input(Clock())
        val nvdla_core_clk = Input(Clock())
        val tmc2slcg_disable_clock_gating = Input(Bool())
        val direct_reset_ = Input(Bool())
        val dla_reset_rstn = Input(Bool())

        //csc
        val accu2sc_credit_size = ValidIO((UInt(3.W)))
        //csb2cacc
        val csb2cacc = new csb2dp_if 
        //glb
        val cacc2glb_done_intr_pd = Output(UInt(2.W))
        //mac
        val mac_a2accu = Flipped(ValidIO(new cmac2cacc_if))    /* data valid */
        val mac_b2accu = Flipped(ValidIO(new cmac2cacc_if))    /* data valid */
        //sdp
        val cacc2sdp = DecoupledIO(new cacc2sdp_if)    /* data valid */

        val pwrbus_ram_pd = Input(UInt(32.W))

           
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

////////////////////////////////////////////////////////////////////////
//  NVDLA Partition M:    Reset Syncer                                //
////////////////////////////////////////////////////////////////////////
    val u_partition_a_reset = Module(new NV_NVDLA_reset)
    u_partition_a_reset.io.nvdla_clk  := io.nvdla_core_clk
    u_partition_a_reset.io.dla_reset_rstn := io.dla_reset_rstn
    u_partition_a_reset.io.direct_reset_ := io.direct_reset_
    u_partition_a_reset.io.test_mode := io.test_mode
    val nvdla_core_rstn = u_partition_a_reset.io.synced_rstn
////////////////////////////////////////////////////////////////////////
// SLCG override
////////////////////////////////////////////////////////////////////////
    val u_dla_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d)
    u_dla_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
    u_dla_clk_ovr_on_sync.io.sync_i := io.nvdla_clk_ovr_on
    val dla_clk_ovr_on_sync = u_dla_clk_ovr_on_sync.io.sync_o 

    val u_global_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d_s)
    u_global_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
    u_global_clk_ovr_on_sync.io.prst := nvdla_core_rstn
    u_global_clk_ovr_on_sync.io.sync_i := io.global_clk_ovr_on
    val global_clk_ovr_on_sync = u_global_clk_ovr_on_sync.io.sync_o 

////////////////////////////////////////////////////////////////////////
//  NVDLA Partition A:     Convolution Accumulator                    //
////////////////////////////////////////////////////////////////////////
//stepheng, modify for cacc verification
    val u_NV_NVDLA_cacc = Module(new NV_NVDLA_cacc)
    u_NV_NVDLA_cacc.io.nvdla_clock.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_cacc.io.nvdla_core_rstn := nvdla_core_rstn
    u_NV_NVDLA_cacc.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    io.cacc2glb_done_intr_pd := u_NV_NVDLA_cacc.io.cacc2glb_done_intr_pd

    u_NV_NVDLA_cacc.io.mac_a2accu <> io.mac_a2accu  
    u_NV_NVDLA_cacc.io.mac_b2accu <> io.mac_b2accu 

    io.cacc2sdp <> u_NV_NVDLA_cacc.io.cacc2sdp

    io.csb2cacc <> u_NV_NVDLA_cacc.io.csb2cacc
    io.accu2sc_credit_size <> u_NV_NVDLA_cacc.io.accu2sc_credit_size
    
    //Port for SLCG
    u_NV_NVDLA_cacc.io.nvdla_clock.dla_clk_ovr_on_sync := dla_clk_ovr_on_sync
    u_NV_NVDLA_cacc.io.nvdla_clock.global_clk_ovr_on_sync := global_clk_ovr_on_sync
    u_NV_NVDLA_cacc.io.nvdla_clock.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating


}


object NV_NVDLA_partition_aDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_partition_a())
}
