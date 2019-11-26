package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_partition_o(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val test_mode = Input(Bool())
        val direct_reset_ = Input(Bool())

        val global_clk_ovr_on = Input(Clock())
        val tmc2slcg_disable_clock_gating = Input(Bool())

        val nvdla_core_clk = Input(Clock())
        val nvdla_falcon_clk = Input(Clock())
        val nvdla_core_rstn = Output(Bool())
        val nvdla_clk_ovr_on = Output(Clock())

        val dla_reset_rstn = Input(Bool())

        //cdma
        val csb2cdma = Flipped(new csb2dp_if)
        val cdma_dat2glb_done_intr_pd = Input(UInt(2.W))
        val cdma_wt2glb_done_intr_pd = Input(UInt(2.W))
        //cmac
        val csb2cmac_a = Flipped(new csb2dp_if)
        val csb2cmac_b = Flipped(new csb2dp_if)
        //cacc
        val csb2cacc = Flipped(new csb2dp_if)
        val cacc2glb_done_intr_pd = Input(UInt(2.W))
        //csc
        val csb2csc = Flipped(new csb2dp_if)
        //apb
        val csb2nvdla = Flipped(DecoupledIO(new csb2nvdla_if))
        val nvdla2csb = ValidIO(new nvdla2csb_if)
        val nvdla2csb_wr_complete = Output(Bool())
        //sdp
        val csb2sdp_rdma = Flipped(new csb2dp_if)
        val csb2sdp = Flipped(new csb2dp_if)
        val sdp2glb_done_intr_pd = Input(UInt(2.W))

        val sdp2pdp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_SDP_MAX_THROUGHPUT*conf.NVDLA_BPE).W)))

        //client2mcif 
        val cdma_dat2mcif_rd_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))
        val mcif2cdma_dat_rd_rsp_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))

        val cdma_wt2mcif_rd_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))
        val mcif2cdma_wt_rd_rsp_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))

        val sdp2mcif_rd_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))
        val mcif2sdp_rd_rsp_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))
        val sdp2mcif_rd_cdt_lat_fifo_pop = Input(Bool())
        val sdp2mcif_wr_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W)))
        val mcif2sdp_wr_rsp_complete = Output(Bool())

        val sdp_b2mcif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
        val mcif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
        val sdp_b2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None

        val sdp_e2mcif_rd_req_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
        val mcif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
        val sdp_e2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None   

        val sdp_n2mcif_rd_req_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
        val mcif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
        val sdp_n2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None

        //client2cvif
        val cdma_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
        val cvif2cdma_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        val cdma_wt2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
        val cvif2cdma_wt_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        val sdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
        val cvif2sdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
        val sdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val sdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W)))) else None
        val cvif2sdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

        val sdp_b2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
        val cvif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
        val sdp_b2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None

        val sdp_e2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
        val cvif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
        val sdp_e2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None   

        val sdp_n2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))) else None
        val cvif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None
        val sdp_n2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE & conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None


        val mcif2noc_axi_ar = DecoupledIO(new nocif_axi_rd_address_if)
        val mcif2noc_axi_aw = DecoupledIO(new nocif_axi_wr_address_if)
        val mcif2noc_axi_w = DecoupledIO(new nocif_axi_wr_data_if)
        val noc2mcif_axi_b = Flipped(DecoupledIO(new nocif_axi_wr_response_if))
        val noc2mcif_axi_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if))

        val cvif2noc_axi_ar = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_rd_address_if)) else None
        val cvif2noc_axi_aw = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_address_if)) else None
        val cvif2noc_axi_w = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_data_if)) else None
        val noc2cvif_axi_b = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_wr_response_if))) else None
        val noc2cvif_axi_r = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_data_if))) else None

        val core_intr = Output(Bool())
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

    io.nvdla_clk_ovr_on := (false.B).asClock
    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O:    Reset Syncer for nvdla_core_clk             //
    ////////////////////////////////////////////////////////////////////////

    val u_sync_core_reset = Module(new NV_NVDLA_core_reset)
    u_sync_core_reset.io.nvdla_clk := io.nvdla_core_clk
    u_sync_core_reset.io.dla_reset_rstn  := io.dla_reset_rstn
    u_sync_core_reset.io.direct_reset_ := io.direct_reset_
    u_sync_core_reset.io.test_mode := io.test_mode
    u_sync_core_reset.io.core_reset_rstn := true.B
    io.nvdla_core_rstn := u_sync_core_reset.io.synced_rstn

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O:    Reset Syncer for nvdla_falcon_clk           //
    ////////////////////////////////////////////////////////////////////////
    val u_sync_falcon_reset = Module(new NV_NVDLA_reset)
    u_sync_falcon_reset.io.nvdla_clk  := io.nvdla_falcon_clk
    u_sync_falcon_reset.io.dla_reset_rstn := io.nvdla_core_rstn
    u_sync_falcon_reset.io.direct_reset_ := io.direct_reset_
    u_sync_falcon_reset.io.test_mode := io.test_mode
    val nvdla_falcon_rstn = u_sync_falcon_reset.io.synced_rstn

    ////////////////////////////////////////////////////////////////////////
    // SLCG override
    ////////////////////////////////////////////////////////////////////////
    val u_dla_clk_ovr_on_core_sync = Module(new NV_NVDLA_sync3d)
    u_dla_clk_ovr_on_core_sync.io.clk := io.nvdla_core_clk
    u_dla_clk_ovr_on_core_sync.io.sync_i := io.nvdla_clk_ovr_on
    val dla_clk_ovr_on_sync = u_dla_clk_ovr_on_core_sync.io.sync_o 

    val u_global_clk_ovr_on_core_sync = Module(new NV_NVDLA_sync3d_s)
    u_global_clk_ovr_on_core_sync.io.clk := io.nvdla_core_clk
    u_global_clk_ovr_on_core_sync.io.prst := io.nvdla_core_rstn
    u_global_clk_ovr_on_core_sync.io.sync_i := io.global_clk_ovr_on
    val global_clk_ovr_on_sync = u_global_clk_ovr_on_core_sync.io.sync_o 

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O:    CFGROM                                      //
    ////////////////////////////////////////////////////////////////////////
    val u_NV_NVDLA_cfgrom = Module(new NV_NVDLA_cfgrom)
    u_NV_NVDLA_cfgrom.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_cfgrom.io.nvdla_core_rstn := io.nvdla_core_rstn

    val u_NV_NVDLA_csb_master = Module(new NV_NVDLA_csb_master)
    val u_NV_NVDLA_mcif = Module(new NV_NVDLA_mcif)
    val u_NV_NVDLA_cvif = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Module(new NV_NVDLA_mcif)) else None
    val u_NV_NVDLA_cdp = if(conf.NVDLA_CDP_ENABLE) Some(Module(new NV_NVDLA_cdp)) else None
    val u_NV_NVDLA_pdp = if(conf.NVDLA_PDP_ENABLE) Some(Module(new NV_NVDLA_pdp)) else None
    val u_NV_NVDLA_glb = Module(new NV_NVDLA_glb)
    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O:    CSB master                                  //
    ////////////////////////////////////////////////////////////////////////
    //general clock
    u_NV_NVDLA_csb_master.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_csb_master.io.nvdla_falcon_clk := io.nvdla_falcon_clk
    u_NV_NVDLA_csb_master.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_NV_NVDLA_csb_master.io.nvdla_falcon_rstn := nvdla_falcon_rstn
    u_NV_NVDLA_csb_master.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    //csb2nvdla
    u_NV_NVDLA_csb_master.io.csb2nvdla <> io.csb2nvdla
    io.nvdla2csb <> u_NV_NVDLA_csb_master.io.nvdla2csb
    io.nvdla2csb_wr_complete := u_NV_NVDLA_csb_master.io.nvdla2csb_wr_complete
    //cfgrom
    u_NV_NVDLA_csb_master.io.csb2cfgrom <> u_NV_NVDLA_cfgrom.io.csb2cfgrom
    //csb2glb
    u_NV_NVDLA_csb_master.io.csb2glb <> u_NV_NVDLA_glb.io.csb2glb
    //mcif
    u_NV_NVDLA_csb_master.io.csb2mcif <> u_NV_NVDLA_mcif.io.csb2mcif
    //memif
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_NV_NVDLA_csb_master.io.csb2cvif.get <> u_NV_NVDLA_cvif.get.io.csb2mcif
    }
    //cdma
    u_NV_NVDLA_csb_master.io.csb2cdma <> io.csb2cdma
    //csc
    u_NV_NVDLA_csb_master.io.csb2csc <> io.csb2csc
    //cmac
    u_NV_NVDLA_csb_master.io.csb2cmac_a <> io.csb2cmac_a
    u_NV_NVDLA_csb_master.io.csb2cmac_b <> io.csb2cmac_b
    //cacc
    u_NV_NVDLA_csb_master.io.csb2cacc <> io.csb2cacc
    //sdp
    u_NV_NVDLA_csb_master.io.csb2sdp_rdma <> io.csb2sdp_rdma
    u_NV_NVDLA_csb_master.io.csb2sdp <> io.csb2sdp
    //pdp
    if(conf.NVDLA_PDP_ENABLE){
        u_NV_NVDLA_csb_master.io.csb2pdp_rdma.get <> u_NV_NVDLA_pdp.get.io.csb2pdp_rdma
        u_NV_NVDLA_csb_master.io.csb2pdp.get <> u_NV_NVDLA_pdp.get.io.csb2pdp
    }
    //cdp
    if(conf.NVDLA_CDP_ENABLE){
        u_NV_NVDLA_csb_master.io.csb2cdp_rdma.get <> u_NV_NVDLA_cdp.get.io.csb2cdp_rdma
        u_NV_NVDLA_csb_master.io.csb2cdp.get <> u_NV_NVDLA_cdp.get.io.csb2cdp
    }

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O:    AXI Interface to MC                         //
    ////////////////////////////////////////////////////////////////////////
    u_NV_NVDLA_mcif.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_mcif.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_NV_NVDLA_mcif.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    //client2mcif
    for(i <- 0 to conf.RDMA_NUM-1){
        if(conf.arr_tieoff_axid(i) == 8){
            u_NV_NVDLA_mcif.io.client2mcif_rd_cdt_lat_fifo_pop(i) := 0.U
            u_NV_NVDLA_mcif.io.client2mcif_rd_req_pd(i) <> io.cdma_dat2mcif_rd_req_pd
            io.mcif2cdma_dat_rd_rsp_pd <> u_NV_NVDLA_mcif.io.mcif2client_rd_rsp_pd(i)      
        }
        if(conf.arr_tieoff_axid(i) == 9){
            u_NV_NVDLA_mcif.io.client2mcif_rd_cdt_lat_fifo_pop(i) := 0.U
            u_NV_NVDLA_mcif.io.client2mcif_rd_req_pd(i) <> io.cdma_wt2mcif_rd_req_pd
            io.mcif2cdma_wt_rd_rsp_pd <> u_NV_NVDLA_mcif.io.mcif2client_rd_rsp_pd(i)      
        }
        if(conf.arr_tieoff_axid(i) == 1){
            u_NV_NVDLA_mcif.io.client2mcif_rd_cdt_lat_fifo_pop(i) := io.sdp2mcif_rd_cdt_lat_fifo_pop
            u_NV_NVDLA_mcif.io.client2mcif_rd_req_pd(i) <> io.sdp2mcif_rd_req_pd
            io.mcif2sdp_rd_rsp_pd <> u_NV_NVDLA_mcif.io.mcif2client_rd_rsp_pd(i)
        }
        if(conf.arr_tieoff_axid(i) == 3){
            u_NV_NVDLA_mcif.io.client2mcif_rd_cdt_lat_fifo_pop(i) := u_NV_NVDLA_cdp.get.io.cdp2mcif_rd_cdt_lat_fifo_pop
            u_NV_NVDLA_mcif.io.client2mcif_rd_req_pd(i) <> u_NV_NVDLA_cdp.get.io.cdp2mcif_rd_req_pd
            u_NV_NVDLA_cdp.get.io.mcif2cdp_rd_rsp_pd <> u_NV_NVDLA_mcif.io.mcif2client_rd_rsp_pd(i)
        }
        if(conf.arr_tieoff_axid(i) == 2){
            u_NV_NVDLA_mcif.io.client2mcif_rd_cdt_lat_fifo_pop(i) := u_NV_NVDLA_pdp.get.io.pdp2mcif_rd_cdt_lat_fifo_pop
            u_NV_NVDLA_mcif.io.client2mcif_rd_req_pd(i) <> u_NV_NVDLA_pdp.get.io.pdp2mcif_rd_req_pd
            u_NV_NVDLA_pdp.get.io.mcif2pdp_rd_rsp_pd <> u_NV_NVDLA_mcif.io.mcif2client_rd_rsp_pd(i)
        }
        if(conf.arr_tieoff_axid(i) == 5){
            u_NV_NVDLA_mcif.io.client2mcif_rd_cdt_lat_fifo_pop(i) := io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get
            u_NV_NVDLA_mcif.io.client2mcif_rd_req_pd(i) <> io.sdp_b2mcif_rd_req_pd.get
            io.mcif2sdp_b_rd_rsp_pd.get <> u_NV_NVDLA_mcif.io.mcif2client_rd_rsp_pd(i)
        }
        if(conf.arr_tieoff_axid(i) == 7){
            u_NV_NVDLA_mcif.io.client2mcif_rd_cdt_lat_fifo_pop(i) := io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get
            u_NV_NVDLA_mcif.io.client2mcif_rd_req_pd(i) <> io.sdp_e2mcif_rd_req_pd.get
            io.mcif2sdp_e_rd_rsp_pd.get <> u_NV_NVDLA_mcif.io.mcif2client_rd_rsp_pd(i)   
        }
        if(conf.arr_tieoff_axid(i) == 6){
            u_NV_NVDLA_mcif.io.client2mcif_rd_cdt_lat_fifo_pop(i) := io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get
            u_NV_NVDLA_mcif.io.client2mcif_rd_req_pd(i) <> io.sdp_n2mcif_rd_req_pd.get
            io.mcif2sdp_n_rd_rsp_pd.get <> u_NV_NVDLA_mcif.io.mcif2client_rd_rsp_pd(i)  
        }
    }

    for(i <- 0 to conf.WDMA_NUM-1){
        if(conf.arr_tieoff_axid(i) == 1){
            u_NV_NVDLA_mcif.io.client2mcif_wr_req_pd(i) <> io.sdp2mcif_wr_req_pd
            io.mcif2sdp_wr_rsp_complete := u_NV_NVDLA_mcif.io.mcif2client_wr_rsp_complete(i)  
        }
        if(conf.arr_tieoff_axid(i) == 3){
            u_NV_NVDLA_mcif.io.client2mcif_wr_req_pd(i) <> u_NV_NVDLA_cdp.get.io.cdp2mcif_wr_req_pd
            u_NV_NVDLA_cdp.get.io.mcif2cdp_wr_rsp_complete := u_NV_NVDLA_mcif.io.mcif2client_wr_rsp_complete(i)          
        }
        if(conf.arr_tieoff_axid(i) == 2){
            u_NV_NVDLA_mcif.io.client2mcif_wr_req_pd(i) <> u_NV_NVDLA_pdp.get.io.pdp2mcif_wr_req_pd
            u_NV_NVDLA_pdp.get.io.mcif2pdp_wr_rsp_complete := u_NV_NVDLA_mcif.io.mcif2client_wr_rsp_complete(i)          
        }
    }


    io.mcif2noc_axi_ar <> u_NV_NVDLA_mcif.io.mcif2noc_axi_ar
    io.mcif2noc_axi_aw <> u_NV_NVDLA_mcif.io.mcif2noc_axi_aw
    io.mcif2noc_axi_w <> u_NV_NVDLA_mcif.io.mcif2noc_axi_w
    u_NV_NVDLA_mcif.io.noc2mcif_axi_b <> io.noc2mcif_axi_b
    u_NV_NVDLA_mcif.io.noc2mcif_axi_r <> io.noc2mcif_axi_r

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O:    AXI Interface to CVSRAM                     //
    ////////////////////////////////////////////////////////////////////////
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_NV_NVDLA_cvif.get.io.nvdla_core_clk := io.nvdla_core_clk
        u_NV_NVDLA_cvif.get.io.nvdla_core_rstn := io.nvdla_core_rstn
        u_NV_NVDLA_cvif.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd

        //client2cvif
        for(i <- 0 to conf.RDMA_NUM-1){
            if(conf.arr_tieoff_axid(i) == 8){
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_cdt_lat_fifo_pop(i) := 0.U
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_req_pd(i) <> io.cdma_dat2cvif_rd_req_pd.get
                io.cvif2cdma_dat_rd_rsp_pd.get <> u_NV_NVDLA_cvif.get.io.mcif2client_rd_rsp_pd(i)      
            }
            if(conf.arr_tieoff_axid(i) == 9){
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_cdt_lat_fifo_pop(i) := 0.U
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_req_pd(i) <> io.cdma_wt2cvif_rd_req_pd.get
                io.cvif2cdma_wt_rd_rsp_pd.get <> u_NV_NVDLA_cvif.get.io.mcif2client_rd_rsp_pd(i)      
            }
            if(conf.arr_tieoff_axid(i) == 1){
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_cdt_lat_fifo_pop(i) := io.sdp2cvif_rd_cdt_lat_fifo_pop.get
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_req_pd(i) <> io.sdp2cvif_rd_req_pd.get
                io.cvif2sdp_rd_rsp_pd.get <> u_NV_NVDLA_cvif.get.io.mcif2client_rd_rsp_pd(i)
            }
            if(conf.arr_tieoff_axid(i) == 3){
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_cdt_lat_fifo_pop(i) := u_NV_NVDLA_cdp.get.io.cdp2cvif_rd_cdt_lat_fifo_pop.get
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_req_pd(i) <> u_NV_NVDLA_cdp.get.io.cdp2cvif_rd_req_pd.get
                u_NV_NVDLA_cdp.get.io.cvif2cdp_rd_rsp_pd.get <> u_NV_NVDLA_cvif.get.io.mcif2client_rd_rsp_pd(i)
            }
            if(conf.arr_tieoff_axid(i) == 2){
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_cdt_lat_fifo_pop(i) := u_NV_NVDLA_pdp.get.io.pdp2cvif_rd_cdt_lat_fifo_pop.get
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_req_pd(i) <> u_NV_NVDLA_pdp.get.io.pdp2cvif_rd_req_pd.get
                u_NV_NVDLA_pdp.get.io.cvif2pdp_rd_rsp_pd.get <> u_NV_NVDLA_cvif.get.io.mcif2client_rd_rsp_pd(i)
            }
            if(conf.arr_tieoff_axid(i) == 5){
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_cdt_lat_fifo_pop(i) := io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_req_pd(i) <> io.sdp_b2cvif_rd_req_pd.get
                io.cvif2sdp_b_rd_rsp_pd.get <> u_NV_NVDLA_cvif.get.io.mcif2client_rd_rsp_pd(i)
            }
            if(conf.arr_tieoff_axid(i) == 7){
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_cdt_lat_fifo_pop(i) := io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_req_pd(i) <> io.sdp_e2cvif_rd_req_pd.get
                io.cvif2sdp_e_rd_rsp_pd.get <> u_NV_NVDLA_cvif.get.io.mcif2client_rd_rsp_pd(i)   
            }
            if(conf.arr_tieoff_axid(i) == 6){
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_cdt_lat_fifo_pop(i) := io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get
                u_NV_NVDLA_cvif.get.io.client2mcif_rd_req_pd(i) <> io.sdp_n2cvif_rd_req_pd.get
                io.cvif2sdp_n_rd_rsp_pd.get <> u_NV_NVDLA_cvif.get.io.mcif2client_rd_rsp_pd(i)  
            }
        }

        for(i <- 0 to conf.WDMA_NUM-1){
            if(conf.arr_tieoff_axid(i) == 1){
                u_NV_NVDLA_cvif.get.io.client2mcif_wr_req_pd(i) <> io.sdp2cvif_wr_req_pd.get
                io.cvif2sdp_wr_rsp_complete.get := u_NV_NVDLA_cvif.get.io.mcif2client_wr_rsp_complete(i)  
            }
            if(conf.arr_tieoff_axid(i) == 3){
                u_NV_NVDLA_cvif.get.io.client2mcif_wr_req_pd(i) <> u_NV_NVDLA_cdp.get.io.cdp2cvif_wr_req_pd.get
                u_NV_NVDLA_cdp.get.io.cvif2cdp_wr_rsp_complete.get := u_NV_NVDLA_cvif.get.io.mcif2client_wr_rsp_complete(i)          
            }
            if(conf.arr_tieoff_axid(i) == 2){
                u_NV_NVDLA_cvif.get.io.client2mcif_wr_req_pd(i) <> u_NV_NVDLA_pdp.get.io.pdp2cvif_wr_req_pd.get
                u_NV_NVDLA_pdp.get.io.cvif2pdp_wr_rsp_complete.get := u_NV_NVDLA_cvif.get.io.mcif2client_wr_rsp_complete(i)          
            }
        }

        io.cvif2noc_axi_ar.get <> u_NV_NVDLA_cvif.get.io.mcif2noc_axi_ar
        io.cvif2noc_axi_aw.get <> u_NV_NVDLA_cvif.get.io.mcif2noc_axi_aw
        io.cvif2noc_axi_w.get <> u_NV_NVDLA_cvif.get.io.mcif2noc_axi_w
        u_NV_NVDLA_cvif.get.io.noc2mcif_axi_b <> io.noc2cvif_axi_b.get
        u_NV_NVDLA_cvif.get.io.noc2mcif_axi_r <> io.noc2cvif_axi_r.get
    }

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O:    Cross-Channel Data Processor                //
    ////////////////////////////////////////////////////////////////////////
    if(conf.NVDLA_CDP_ENABLE){
        u_NV_NVDLA_cdp.get.io.nvdla_clock.nvdla_core_clk := io.nvdla_core_clk
        u_NV_NVDLA_cdp.get.io.nvdla_clock.dla_clk_ovr_on_sync := dla_clk_ovr_on_sync
        u_NV_NVDLA_cdp.get.io.nvdla_clock.global_clk_ovr_on_sync := global_clk_ovr_on_sync
        u_NV_NVDLA_cdp.get.io.nvdla_clock.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
        u_NV_NVDLA_cdp.get.io.nvdla_core_rstn := io.nvdla_core_rstn
        u_NV_NVDLA_cdp.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    }

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O:    Planar Data Processor                       //
    ////////////////////////////////////////////////////////////////////////
    if(conf.NVDLA_PDP_ENABLE){
        u_NV_NVDLA_pdp.get.io.nvdla_clock.nvdla_core_clk := io.nvdla_core_clk
        u_NV_NVDLA_pdp.get.io.nvdla_clock.dla_clk_ovr_on_sync := dla_clk_ovr_on_sync
        u_NV_NVDLA_pdp.get.io.nvdla_clock.global_clk_ovr_on_sync := global_clk_ovr_on_sync
        u_NV_NVDLA_pdp.get.io.nvdla_clock.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
        u_NV_NVDLA_pdp.get.io.nvdla_core_rstn := io.nvdla_core_rstn
        u_NV_NVDLA_pdp.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
        u_NV_NVDLA_pdp.get.io.sdp2pdp_pd <> io.sdp2pdp_pd
    }
    else{
        io.sdp2pdp_pd.ready := true.B
    }

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O:    Global Unit                                 //
    ////////////////////////////////////////////////////////////////////////
    //clock
    u_NV_NVDLA_glb.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_glb.io.nvdla_falcon_clk := io.nvdla_falcon_clk

    u_NV_NVDLA_glb.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_NV_NVDLA_glb.io.nvdla_falcon_rstn := nvdla_falcon_rstn

    io.core_intr := u_NV_NVDLA_glb.io.core_intr

    if(conf.NVDLA_CDP_ENABLE){
        u_NV_NVDLA_glb.io.cdp2glb_done_intr_pd.get := u_NV_NVDLA_cdp.get.io.cdp2glb_done_intr_pd
    }
    if(conf.NVDLA_PDP_ENABLE){
        u_NV_NVDLA_glb.io.pdp2glb_done_intr_pd.get := u_NV_NVDLA_pdp.get.io.pdp2glb_done_intr_pd
    }
    u_NV_NVDLA_glb.io.cacc2glb_done_intr_pd := io.cacc2glb_done_intr_pd
    u_NV_NVDLA_glb.io.cdma_dat2glb_done_intr_pd := io.cdma_dat2glb_done_intr_pd
    u_NV_NVDLA_glb.io.cdma_wt2glb_done_intr_pd := io.cdma_wt2glb_done_intr_pd
    u_NV_NVDLA_glb.io.sdp2glb_done_intr_pd := io.sdp2glb_done_intr_pd



}


object NV_NVDLA_partition_oDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_partition_o())
}
