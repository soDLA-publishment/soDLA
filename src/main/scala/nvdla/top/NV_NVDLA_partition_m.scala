package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_partition_m(implicit val conf: cdmaConfiguration) extends Module {
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
        val csb2cmac_a_req_pvld = Input(Bool())  /* data valid */
        val csb2cmac_a_req_prdy = Output(Bool())  /* data return handshake */
        val csb2cmac_a_req_pd = Input(UInt(63.W)) 
        val cmac_a2csb_resp_valid = Output(Bool())
        val cmac_a2csb_resp_pd = Output(UInt(34.W))

        //sc2mac_wt
        val sc2mac_wt_pvld = Input(Bool())      /* data valid */
        val sc2mac_wt_mask = Input(Vec(conf.CMAC_ATOMC, Bool()))
        val sc2mac_wt_data = Input(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
        val sc2mac_wt_sel = Input(Vec(conf.CMAC_ATOMK_HALF, Bool()))

        val sc2mac_dat_pvld = Input(Bool())      /* data valid */
        val sc2mac_dat_mask = Input(Vec(conf.CMAC_ATOMC, Bool()))
        val sc2mac_dat_data = Input(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
        val sc2mac_dat_pd = Input(UInt(9.W))

        val mac2accu_pvld = Output(Bool())  /* data valid */ 
        val mac2accu_mask = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))  
        val mac2accu_mode = Output(Bool())
        val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)))
        val mac2accu_pd = Output(UInt(9.W))
           
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
    u_NV_NVDLA_cmac.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_cmac.io.nvdla_core_rstn := nvdla_core_rstn

    io.cmac_a2csb_resp_valid := u_NV_NVDLA_cmac.io.cmac_a2csb_resp_valid
    io.cmac_a2csb_resp_pd := u_NV_NVDLA_cmac.io.cmac_a2csb_resp_pd

    u_NV_NVDLA_cmac.io.csb2cmac_a_req_pvld := io.csb2cmac_a_req_pvld
    io.csb2cmac_a_req_prdy := u_NV_NVDLA_cmac.io.csb2cmac_a_req_prdy
    u_NV_NVDLA_cmac.io.csb2cmac_a_req_pd := io.csb2cmac_a_req_pd

    io.mac2accu_pvld := u_NV_NVDLA_cmac.io.mac2accu_pvld
    io.mac2accu_mask := u_NV_NVDLA_cmac.io.mac2accu_mask
    io.mac2accu_mode := u_NV_NVDLA_cmac.io.mac2accu_mode
    io.mac2accu_data := u_NV_NVDLA_cmac.io.mac2accu_data
    io.mac2accu_pd := u_NV_NVDLA_cmac.io.mac2accu_pd

    u_NV_NVDLA_cmac.io.sc2mac_dat_pvld := io.sc2mac_dat_pvld
    u_NV_NVDLA_cmac.io.sc2mac_dat_mask := io.sc2mac_dat_mask
    u_NV_NVDLA_cmac.io.sc2mac_dat_data := io.sc2mac_dat_data
    u_NV_NVDLA_cmac.io.sc2mac_dat_pd := io.sc2mac_dat_pd 

    u_NV_NVDLA_cmac.io.sc2mac_wt_pvld := io.sc2mac_wt_pvld
    u_NV_NVDLA_cmac.io.sc2mac_wt_mask := io.sc2mac_wt_mask
    u_NV_NVDLA_cmac.io.sc2mac_wt_data := io.sc2mac_wt_data
    u_NV_NVDLA_cmac.io.sc2mac_wt_sel := io.sc2mac_wt_sel

    u_NV_NVDLA_cmac.io.dla_clk_ovr_on_sync := dla_clk_ovr_on_sync
    u_NV_NVDLA_cmac.io.global_clk_ovr_on_sync := global_clk_ovr_on_sync 
    u_NV_NVDLA_cmac.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating

}


object NV_NVDLA_partition_mDriver extends App {
  implicit val conf: cdmaConfiguration = new cdmaConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_partition_m())
}
