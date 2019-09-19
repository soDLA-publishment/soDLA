package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_partition_c(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val dla_reset_rstn = Input(Bool())
        val test_mode = Input(Bool())
        val direct_reset_ = Input(Bool())
        val nvdla_clk_ovr_on = Input(Clock())
        val global_clk_ovr_on = Input(Clock())
        val tmc2slcg_disable_clock_gating = Input(Bool())

        //csb
        val csb2cdma = new csb2dp_if 
        val csb2csc = new csb2dp_if

        //accu2sc
        val accu2sc_credit_size = Flipped(ValidIO(UInt(3.W)))

        //2glb
        val cdma_dat2glb_done_intr_pd = Output(UInt(2.W))
        val cdma_wt2glb_done_intr_pd = Output(UInt(2.W))

        //mcif
        val cdma_dat2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2cdma_dat_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))

        val cdma_wt2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2cdma_wt_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))

        //cvif
        val cdma_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2cdma_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))) else None

        val cdma_wt2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2cdma_wt_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))) else None
        
        //mac_dat & wt
        val sc2mac_dat_a = ValidIO(new csc2cmac_data_if)    
        val sc2mac_dat_b = ValidIO(new csc2cmac_data_if)    

        //mac_wt
        val sc2mac_wt_a = ValidIO(new csc2cmac_wt_if)    
        val sc2mac_wt_b = ValidIO(new csc2cmac_wt_if)    

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
    //  NVDLA Partition C:    Reset Sync                                  //
    ////////////////////////////////////////////////////////////////////////
    val u_partition_c_reset = Module(new NV_NVDLA_reset)
    u_partition_c_reset.io.nvdla_clk  := io.nvdla_core_clk
    u_partition_c_reset.io.dla_reset_rstn := io.dla_reset_rstn
    u_partition_c_reset.io.direct_reset_ := io.direct_reset_
    u_partition_c_reset.io.test_mode := io.test_mode
    val nvdla_core_rstn = u_partition_c_reset.io.synced_rstn

    ////////////////////////////////////////////////////////////////////////
    // SLCG override
    ////////////////////////////////////////////////////////////////////////
    val u_csc_dla_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d)
    u_csc_dla_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
    u_csc_dla_clk_ovr_on_sync.io.sync_i := io.nvdla_clk_ovr_on
    val csc_dla_clk_ovr_on_sync = u_csc_dla_clk_ovr_on_sync.io.sync_o 

    val u_cdma_dla_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d)
    u_cdma_dla_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
    u_cdma_dla_clk_ovr_on_sync.io.sync_i := io.nvdla_clk_ovr_on
    val cdma_dla_clk_ovr_on_sync = u_cdma_dla_clk_ovr_on_sync.io.sync_o 

    val u_global_csc_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d_s)
    u_global_csc_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
    u_global_csc_clk_ovr_on_sync.io.prst := nvdla_core_rstn
    u_global_csc_clk_ovr_on_sync.io.sync_i := io.global_clk_ovr_on
    val csc_global_clk_ovr_on_sync = u_global_csc_clk_ovr_on_sync.io.sync_o 

    val u_global_cdma_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d_s)
    u_global_cdma_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
    u_global_cdma_clk_ovr_on_sync.io.prst := nvdla_core_rstn
    u_global_cdma_clk_ovr_on_sync.io.sync_i := io.global_clk_ovr_on
    val cdma_global_clk_ovr_on_sync = u_global_cdma_clk_ovr_on_sync.io.sync_o 

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition C:    Convolution DMA                             //
    ////////////////////////////////////////////////////////////////////////
    val u_NV_NVDLA_cdma = Module(new NV_NVDLA_cdma)

    u_NV_NVDLA_cdma.io.nvdla_clock.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_cdma.io.nvdla_core_rstn := nvdla_core_rstn
    u_NV_NVDLA_cdma.io.nvdla_clock.dla_clk_ovr_on_sync := cdma_dla_clk_ovr_on_sync
    u_NV_NVDLA_cdma.io.nvdla_clock.global_clk_ovr_on_sync := cdma_global_clk_ovr_on_sync
    u_NV_NVDLA_cdma.io.nvdla_clock.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating     

    //csb
    io.csb2cdma <> u_NV_NVDLA_cdma.io.csb2cdma

    //glb
    io.cdma_dat2glb_done_intr_pd := u_NV_NVDLA_cdma.io.cdma_dat2glb_done_intr_pd
    io.cdma_wt2glb_done_intr_pd := u_NV_NVDLA_cdma.io.cdma_wt2glb_done_intr_pd

    //mcif
    io.cdma_dat2mcif_rd_req_pd <> u_NV_NVDLA_cdma.io.cdma_dat2mcif_rd_req_pd
    u_NV_NVDLA_cdma.io.mcif2cdma_dat_rd_rsp_pd <> io.mcif2cdma_dat_rd_rsp_pd

    io.cdma_wt2mcif_rd_req_pd <> u_NV_NVDLA_cdma.io.cdma_wt2mcif_rd_req_pd
    u_NV_NVDLA_cdma.io.mcif2cdma_wt_rd_rsp_pd <> io.mcif2cdma_wt_rd_rsp_pd

    //cvif
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){ 
        io.cdma_dat2cvif_rd_req_pd.get <> u_NV_NVDLA_cdma.io.cdma_dat2cvif_rd_req_pd.get
        u_NV_NVDLA_cdma.io.cvif2cdma_dat_rd_rsp_pd.get <> io.cvif2cdma_dat_rd_rsp_pd.get

        io.cdma_wt2cvif_rd_req_pd.get <> u_NV_NVDLA_cdma.io.cdma_wt2cvif_rd_req_pd.get
        u_NV_NVDLA_cdma.io.cvif2cdma_wt_rd_rsp_pd.get <> io.cvif2cdma_wt_rd_rsp_pd.get
    }



    val cdma2sc_dat_pending_ack = u_NV_NVDLA_cdma.io.cdma2sc_dat_pending_ack
    val cdma2sc_wt_pending_ack = u_NV_NVDLA_cdma.io.cdma2sc_wt_pending_ack

    //pwrbus
    u_NV_NVDLA_cdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition C:    Convolution Buffer                         //
    ////////////////////////////////////////////////////////////////////////
    val u_NV_NVDLA_cbuf = Module(new NV_NVDLA_cbuf)

    u_NV_NVDLA_cbuf.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_cbuf.io.nvdla_core_rstn := nvdla_core_rstn
    u_NV_NVDLA_cbuf.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    if(conf.NVDLA_CC_ATOMC_DIV_ATOMK == 1){
        u_NV_NVDLA_cbuf.io.cdma2buf_wr.sel(0) := Fill(conf.CBUF_WR_BANK_SEL_WIDTH, true.B)
        u_NV_NVDLA_cbuf.io.cdma2buf_wr.sel(1) := Fill(conf.CBUF_WR_BANK_SEL_WIDTH, true.B)
    }

    if(conf.NVDLA_CC_ATOMC_DIV_ATOMK == 2){
        u_NV_NVDLA_cbuf.io.cdma2buf_wr.sel(0) := u_NV_NVDLA_cdma.io.cdma2buf_dat_wr_sel.get
        u_NV_NVDLA_cbuf.io.cdma2buf_wr.sel(1) := u_NV_NVDLA_cdma.io.cdma2buf_wt_wr_sel.get
    }

    if(conf.NVDLA_CC_ATOMC_DIV_ATOMK == 4){
        u_NV_NVDLA_cbuf.io.cdma2buf_wr.sel(0) := u_NV_NVDLA_cdma.io.cdma2buf_dat_wr_sel.get
        u_NV_NVDLA_cbuf.io.cdma2buf_wr.sel(1) := u_NV_NVDLA_cdma.io.cdma2buf_wt_wr_sel.get
    }


    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition C:    Convolution Sequence Controller             //
    ////////////////////////////////////////////////////////////////////////

    val u_NV_NVDLA_csc = Module(new NV_NVDLA_csc)
    //clock
    u_NV_NVDLA_csc.io.nvdla_clock.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_csc.io.nvdla_core_rstn := nvdla_core_rstn
    u_NV_NVDLA_csc.io.nvdla_clock.dla_clk_ovr_on_sync := csc_dla_clk_ovr_on_sync
    u_NV_NVDLA_csc.io.nvdla_clock.global_clk_ovr_on_sync := csc_global_clk_ovr_on_sync   
    u_NV_NVDLA_csc.io.nvdla_clock.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating


    u_NV_NVDLA_csc.io.accu2sc_credit_size <> io.accu2sc_credit_size

    //csb
    io.csb2csc <> u_NV_NVDLA_csc.io.csb2csc

    //cdma
    u_NV_NVDLA_csc.io.cdma2sc_dat_updt <> u_NV_NVDLA_cdma.io.cdma2sc_dat_updt
    u_NV_NVDLA_cdma.io.sc2cdma_dat_updt <> u_NV_NVDLA_csc.io.sc2cdma_dat_updt
    u_NV_NVDLA_cdma.io.sc2cdma_dat_pending_req := u_NV_NVDLA_csc.io.sc2cdma_dat_pending_req
    u_NV_NVDLA_cdma.io.sc2cdma_wt_pending_req := u_NV_NVDLA_csc.io.sc2cdma_wt_pending_req
    u_NV_NVDLA_csc.io.cdma2sc_dat_pending_ack := u_NV_NVDLA_cdma.io.cdma2sc_dat_pending_ack
    u_NV_NVDLA_csc.io.cdma2sc_wt_pending_ack := u_NV_NVDLA_cdma.io.cdma2sc_wt_pending_ack

    //cbuf

    u_NV_NVDLA_cbuf.io.cdma2buf_wr.en(0) := u_NV_NVDLA_cdma.io.cdma2buf_dat_wr.addr.valid
    u_NV_NVDLA_cbuf.io.cdma2buf_wr.addr(0) := u_NV_NVDLA_cdma.io.cdma2buf_dat_wr.addr.bits(conf.CBUF_ADDR_WIDTH-1, 0)
    u_NV_NVDLA_cbuf.io.cdma2buf_wr.data(0) := u_NV_NVDLA_cdma.io.cdma2buf_dat_wr.data

    u_NV_NVDLA_cbuf.io.cdma2buf_wr.en(1) := u_NV_NVDLA_cdma.io.cdma2buf_wt_wr.addr.valid
    u_NV_NVDLA_cbuf.io.cdma2buf_wr.addr(1) := u_NV_NVDLA_cdma.io.cdma2buf_wt_wr.addr.bits(conf.CBUF_ADDR_WIDTH-1, 0)
    u_NV_NVDLA_cbuf.io.cdma2buf_wr.data(1) := u_NV_NVDLA_cdma.io.cdma2buf_wt_wr.data


    u_NV_NVDLA_cbuf.io.sc2buf_dat_rd <> u_NV_NVDLA_csc.io.sc2buf_dat_rd  
    u_NV_NVDLA_cbuf.io.sc2buf_wt_rd <> u_NV_NVDLA_csc.io.sc2buf_wt_rd          


    //mac_dat & wt

    io.sc2mac_dat_a <> u_NV_NVDLA_csc.io.sc2mac_dat_a
    io.sc2mac_dat_b <> u_NV_NVDLA_csc.io.sc2mac_dat_b

    io.sc2mac_wt_a <> u_NV_NVDLA_csc.io.sc2mac_wt_a
    io.sc2mac_wt_b <> u_NV_NVDLA_csc.io.sc2mac_wt_b

    u_NV_NVDLA_csc.io.cdma2sc_wt_updt <> u_NV_NVDLA_cdma.io.cdma2sc_wt_updt
    u_NV_NVDLA_csc.io.cdma2sc_wmb_entries := 0.U
    u_NV_NVDLA_cdma.io.sc2cdma_wt_updt <> u_NV_NVDLA_csc.io.sc2cdma_wt_updt      

    u_NV_NVDLA_csc.io.pwrbus_ram_pd := io.pwrbus_ram_pd

}


object NV_NVDLA_partition_cDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_partition_c())
}
