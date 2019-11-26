package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_partition_m(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val test_mode = Input(Bool())
        val direct_reset_ = Input(Bool())

        val global_clk_ovr_on = Input(Clock())
        val tmc2slcg_disable_clock_gating = Input(Bool())

        val nvdla_core_clk = Input(Clock())
        val nvdla_clk_ovr_on = Input(Clock())

        val dla_reset_rstn = Input(Bool())

        //csb
        val csb2cmac_a = new csb2dp_if 

        //sc2mac_dat&wt
        val sc2mac_dat = Flipped(ValidIO(new csc2cmac_data_if))  
        val sc2mac_wt = Flipped(ValidIO(new csc2cmac_wt_if))   

        //mac2accu
        val mac2accu = ValidIO(new cmac2cacc_if) 
           
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
    val u_partition_m_reset = Module(new NV_NVDLA_reset)
    u_partition_m_reset.io.nvdla_clk  := io.nvdla_core_clk
    u_partition_m_reset.io.dla_reset_rstn := io.dla_reset_rstn
    u_partition_m_reset.io.direct_reset_ := io.direct_reset_
    u_partition_m_reset.io.test_mode := io.test_mode
    val nvdla_core_rstn = u_partition_m_reset.io.synced_rstn
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
//  NVDLA Partition M:    Convolution MAC Array                       //
////////////////////////////////////////////////////////////////////////
    val u_NV_NVDLA_cmac = Module(new NV_NVDLA_cmac)
    u_NV_NVDLA_cmac.io.nvdla_clock.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_cmac.io.nvdla_core_rstn := nvdla_core_rstn
    u_NV_NVDLA_cmac.io.nvdla_clock.dla_clk_ovr_on_sync := dla_clk_ovr_on_sync
    u_NV_NVDLA_cmac.io.nvdla_clock.global_clk_ovr_on_sync := global_clk_ovr_on_sync 
    u_NV_NVDLA_cmac.io.nvdla_clock.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating

    //csb
    io.csb2cmac_a <> u_NV_NVDLA_cmac.io.csb2cmac_a
    
    //csc->cmac->cacc
    u_NV_NVDLA_cmac.io.sc2mac_dat <> io.sc2mac_dat
    u_NV_NVDLA_cmac.io.sc2mac_wt <> io.sc2mac_wt
    io.mac2accu <> u_NV_NVDLA_cmac.io.mac2accu

}


object NV_NVDLA_partition_mDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_partition_m())
}
