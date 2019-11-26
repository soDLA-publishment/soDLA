package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_nvdla(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val dla_core_clk = Input(Clock())
        val dla_csb_clk = Input(Clock())
        val global_clk_ovr_on = Input(Clock())
        val tmc2slcg_disable_clock_gating = Input(Bool())
        val dla_reset_rstn = Input(Bool())
        val direct_reset_ = Input(Bool())
        val test_mode = Input(Bool())
        //csb2nvdla
        val csb2nvdla = Flipped(DecoupledIO(new csb2nvdla_if))
        val nvdla2csb = ValidIO(new nvdla2csb_if)
        val nvdla2csb_wr_complete = Output(Bool())
        ///////////////
        //axi
        //2dbb
        val nvdla_core2dbb_aw = DecoupledIO(new nocif_axi_wr_address_if)
        val nvdla_core2dbb_w = DecoupledIO(new nocif_axi_wr_data_if)
        val nvdla_core2dbb_b = Flipped(DecoupledIO(new nocif_axi_wr_response_if))
        val nvdla_core2dbb_ar = Flipped(DecoupledIO(new nocif_axi_rd_address_if))
        val nvdla_core2dbb_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if))
        //2cvsram
        val nvdla_core2cvsram_aw = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_address_if)) else None
        val nvdla_core2cvsram_w = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_data_if)) else None
        val nvdla_core2cvsram_b = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_wr_response_if))) else None
        val nvdla_core2cvsram_ar = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_address_if))) else None
        val nvdla_core2cvsram_r = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_data_if))) else None
        //pwr_ram_pd
        val nvdla_pwrbus_ram_c_pd = Input(UInt(32.W))
        val nvdla_pwrbus_ram_ma_pd = Input(UInt(32.W))
        val nvdla_pwrbus_ram_mb_pd = Input(UInt(32.W))
        val nvdla_pwrbus_ram_p_pd = Input(UInt(32.W))
        val nvdla_pwrbus_ram_o_pd = Input(UInt(32.W))
        val nvdla_pwrbus_ram_a_pd = Input(UInt(32.W))
        val dla_intr = Output(Bool())
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

    val u_partition_o = Module(new NV_NVDLA_partition_o)
    val u_partition_c = Module(new NV_NVDLA_partition_c)
    val u_partition_ma = Module(new NV_NVDLA_partition_m)
    val u_partition_mb = Module(new NV_NVDLA_partition_m)
    val u_partition_a = Module(new NV_NVDLA_partition_a)
    val u_partition_p = Module(new NV_NVDLA_partition_p)
    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition O                                                 //
    ////////////////////////////////////////////////////////////////////////  
    u_partition_o.io.test_mode := io.test_mode
    u_partition_o.io.direct_reset_ := io.direct_reset_
    u_partition_o.io.global_clk_ovr_on := io.global_clk_ovr_on
    u_partition_o.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
    u_partition_o.io.nvdla_core_clk := io.dla_core_clk
    u_partition_o.io.nvdla_falcon_clk := io.dla_csb_clk
    val nvdla_core_rstn = u_partition_o.io.nvdla_core_rstn
    val nvdla_clk_ovr_on = u_partition_o.io.nvdla_clk_ovr_on
    u_partition_o.io.dla_reset_rstn := io.dla_reset_rstn
    //cdma
    u_partition_o.io.csb2cdma <> u_partition_c.io.csb2cdma
    u_partition_o.io.cdma_dat2glb_done_intr_pd := u_partition_c.io.cdma_dat2glb_done_intr_pd
    u_partition_o.io.cdma_wt2glb_done_intr_pd := u_partition_c.io.cdma_wt2glb_done_intr_pd
    //cmac
    u_partition_o.io.csb2cmac_a <> u_partition_ma.io.csb2cmac_a
    u_partition_o.io.csb2cmac_b <> u_partition_mb.io.csb2cmac_a
    //cacc
    u_partition_o.io.csb2cacc <> u_partition_a.io.csb2cacc
    u_partition_o.io.cacc2glb_done_intr_pd := u_partition_a.io.cacc2glb_done_intr_pd
    //csc
    u_partition_o.io.csb2csc <> u_partition_c.io.csb2csc
    //apb
    u_partition_o.io.csb2nvdla <> io.csb2nvdla
    io.nvdla2csb <> u_partition_o.io.nvdla2csb
    io.nvdla2csb_wr_complete := u_partition_o.io.nvdla2csb_wr_complete
    //sdp
    u_partition_o.io.csb2sdp_rdma <> u_partition_p.io.csb2sdp_rdma
    u_partition_o.io.csb2sdp <> u_partition_p.io.csb2sdp
    u_partition_o.io.sdp2glb_done_intr_pd := u_partition_p.io.sdp2glb_done_intr_pd

    u_partition_o.io.sdp2pdp_pd <> u_partition_p.io.sdp2pdp_pd
    //client2mcif 
    u_partition_o.io.cdma_dat2mcif_rd_req_pd <> u_partition_c.io.cdma_dat2mcif_rd_req_pd
    u_partition_c.io.mcif2cdma_dat_rd_rsp_pd <> u_partition_o.io.mcif2cdma_dat_rd_rsp_pd

    u_partition_o.io.cdma_wt2mcif_rd_req_pd <> u_partition_c.io.cdma_wt2mcif_rd_req_pd
    u_partition_c.io.mcif2cdma_wt_rd_rsp_pd <> u_partition_o.io.mcif2cdma_wt_rd_rsp_pd

    u_partition_o.io.sdp2mcif_rd_req_pd <> u_partition_p.io.sdp2mcif_rd_req_pd
    u_partition_p.io.mcif2sdp_rd_rsp_pd <> u_partition_o.io.mcif2sdp_rd_rsp_pd
    u_partition_o.io.sdp2mcif_rd_cdt_lat_fifo_pop := u_partition_p.io.sdp2mcif_rd_cdt_lat_fifo_pop
    u_partition_o.io.sdp2mcif_wr_req_pd <> u_partition_p.io.sdp2mcif_wr_req_pd
    u_partition_o.io.mcif2sdp_wr_rsp_complete := u_partition_p.io.mcif2sdp_wr_rsp_complete

    if(conf.NVDLA_SDP_BS_ENABLE){
        u_partition_o.io.sdp_b2mcif_rd_req_pd.get <> u_partition_p.io.sdp_b2mcif_rd_req_pd.get
        u_partition_p.io.mcif2sdp_b_rd_rsp_pd.get <> u_partition_o.io.mcif2sdp_b_rd_rsp_pd.get
        u_partition_o.io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get := u_partition_p.io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get
    }
    if(conf.NVDLA_SDP_EW_ENABLE){
        u_partition_o.io.sdp_e2mcif_rd_req_pd.get <> u_partition_p.io.sdp_e2mcif_rd_req_pd.get
        u_partition_p.io.mcif2sdp_e_rd_rsp_pd.get <> u_partition_o.io.mcif2sdp_e_rd_rsp_pd.get
        u_partition_o.io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get := u_partition_p.io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get
    }
    if(conf.NVDLA_SDP_BN_ENABLE){
        u_partition_o.io.sdp_n2mcif_rd_req_pd.get <> u_partition_p.io.sdp_n2mcif_rd_req_pd.get
        u_partition_p.io.mcif2sdp_n_rd_rsp_pd.get <> u_partition_o.io.mcif2sdp_n_rd_rsp_pd.get
        u_partition_o.io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get := u_partition_p.io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get
    }

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_partition_o.io.cdma_dat2cvif_rd_req_pd.get <> u_partition_c.io.cdma_dat2cvif_rd_req_pd.get
        u_partition_c.io.cvif2cdma_dat_rd_rsp_pd.get <> u_partition_o.io.cvif2cdma_dat_rd_rsp_pd.get

        u_partition_o.io.cdma_wt2cvif_rd_req_pd.get <> u_partition_c.io.cdma_wt2cvif_rd_req_pd.get
        u_partition_c.io.cvif2cdma_wt_rd_rsp_pd.get <> u_partition_o.io.cvif2cdma_wt_rd_rsp_pd.get

        u_partition_o.io.sdp2cvif_rd_req_pd.get <> u_partition_p.io.sdp2cvif_rd_req_pd.get
        u_partition_p.io.cvif2sdp_rd_rsp_pd.get <> u_partition_o.io.cvif2sdp_rd_rsp_pd.get
        u_partition_o.io.sdp2cvif_rd_cdt_lat_fifo_pop.get := u_partition_p.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
        u_partition_o.io.sdp2cvif_wr_req_pd.get <> u_partition_p.io.sdp2cvif_wr_req_pd.get
        u_partition_o.io.cvif2sdp_wr_rsp_complete.get := u_partition_p.io.cvif2sdp_wr_rsp_complete.get

        if(conf.NVDLA_SDP_BS_ENABLE){
            u_partition_o.io.sdp_b2cvif_rd_req_pd.get <> u_partition_p.io.sdp_b2cvif_rd_req_pd.get
            u_partition_p.io.cvif2sdp_b_rd_rsp_pd.get <> u_partition_o.io.cvif2sdp_b_rd_rsp_pd.get
            u_partition_o.io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get := u_partition_p.io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get
        }
        if(conf.NVDLA_SDP_EW_ENABLE){
            u_partition_o.io.sdp_e2cvif_rd_req_pd.get <> u_partition_p.io.sdp_e2cvif_rd_req_pd.get
            u_partition_p.io.cvif2sdp_e_rd_rsp_pd.get <> u_partition_o.io.cvif2sdp_e_rd_rsp_pd.get
            u_partition_o.io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get := u_partition_p.io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get
        }
        if(conf.NVDLA_SDP_BN_ENABLE){
            u_partition_o.io.sdp_n2cvif_rd_req_pd.get <> u_partition_p.io.sdp_n2cvif_rd_req_pd.get
            u_partition_p.io.cvif2sdp_n_rd_rsp_pd.get <> u_partition_o.io.cvif2sdp_n_rd_rsp_pd.get
            u_partition_o.io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get := u_partition_p.io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get
        }
        
    }

    io.nvdla_core2dbb_ar <> u_partition_o.io.mcif2noc_axi_ar
    io.nvdla_core2dbb_aw <> u_partition_o.io.mcif2noc_axi_aw 
    io.nvdla_core2dbb_w <> u_partition_o.io.mcif2noc_axi_w 
    u_partition_o.io.noc2mcif_axi_b <> io.nvdla_core2dbb_b
    u_partition_o.io.noc2mcif_axi_r <> io.nvdla_core2dbb_r

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.nvdla_core2cvsram_ar.get <> u_partition_o.io.cvif2noc_axi_ar.get
        io.nvdla_core2cvsram_aw.get <> u_partition_o.io.cvif2noc_axi_aw.get 
        io.nvdla_core2cvsram_w.get <> u_partition_o.io.cvif2noc_axi_w.get 
        u_partition_o.io.noc2cvif_axi_b.get <> io.nvdla_core2cvsram_b.get
        u_partition_o.io.noc2cvif_axi_r.get <> io.nvdla_core2cvsram_r.get
    }
    io.dla_intr := u_partition_o.io.core_intr
    u_partition_o.io.pwrbus_ram_pd := io.nvdla_pwrbus_ram_o_pd

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition C                                                 //
    ////////////////////////////////////////////////////////////////////////
    u_partition_c.io.test_mode := io.test_mode
    u_partition_c.io.direct_reset_ := io.direct_reset_
    u_partition_c.io.global_clk_ovr_on := io.global_clk_ovr_on
    u_partition_c.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
    u_partition_c.io.nvdla_core_clk := io.dla_core_clk
    u_partition_c.io.dla_reset_rstn := nvdla_core_rstn
    u_partition_c.io.nvdla_clk_ovr_on := nvdla_clk_ovr_on

    u_partition_c.io.accu2sc_credit_size <> u_partition_a.io.accu2sc_credit_size
    u_partition_c.io.pwrbus_ram_pd := io.nvdla_pwrbus_ram_c_pd

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition MA                                                //
    ////////////////////////////////////////////////////////////////////////

    u_partition_ma.io.test_mode := io.test_mode
    u_partition_ma.io.direct_reset_ := io.direct_reset_
    u_partition_ma.io.global_clk_ovr_on := io.global_clk_ovr_on
    u_partition_ma.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
    u_partition_ma.io.nvdla_core_clk := io.dla_core_clk
    u_partition_ma.io.dla_reset_rstn := nvdla_core_rstn
    u_partition_ma.io.nvdla_clk_ovr_on := nvdla_clk_ovr_on

    u_partition_ma.io.sc2mac_dat <> u_partition_c.io.sc2mac_dat_a
    u_partition_ma.io.sc2mac_wt <> u_partition_c.io.sc2mac_wt_a  



    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition MB                                                //
    ////////////////////////////////////////////////////////////////////////

    u_partition_mb.io.test_mode := io.test_mode
    u_partition_mb.io.direct_reset_ := io.direct_reset_
    u_partition_mb.io.global_clk_ovr_on := io.global_clk_ovr_on
    u_partition_mb.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
    u_partition_mb.io.nvdla_core_clk := io.dla_core_clk
    u_partition_mb.io.dla_reset_rstn := nvdla_core_rstn
    u_partition_mb.io.nvdla_clk_ovr_on := nvdla_clk_ovr_on

    u_partition_mb.io.sc2mac_dat <> u_partition_c.io.sc2mac_dat_b
    u_partition_mb.io.sc2mac_wt <> u_partition_c.io.sc2mac_wt_b 


    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition A                                                 //
    ////////////////////////////////////////////////////////////////////////
    u_partition_a.io.test_mode := io.test_mode
    u_partition_a.io.direct_reset_ := io.direct_reset_
    u_partition_a.io.global_clk_ovr_on := io.global_clk_ovr_on
    u_partition_a.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
    u_partition_a.io.nvdla_core_clk := io.dla_core_clk
    u_partition_a.io.dla_reset_rstn := nvdla_core_rstn
    u_partition_a.io.nvdla_clk_ovr_on := nvdla_clk_ovr_on
    //mac
    u_partition_a.io.mac_a2accu <> u_partition_ma.io.mac2accu
    u_partition_a.io.mac_b2accu <> u_partition_mb.io.mac2accu

    u_partition_a.io.pwrbus_ram_pd := io.nvdla_pwrbus_ram_a_pd
    

    ////////////////////////////////////////////////////////////////////////
    //  NVDLA Partition P                                                 //
    ////////////////////////////////////////////////////////////////////////
    u_partition_p.io.test_mode := io.test_mode
    u_partition_p.io.direct_reset_ := io.direct_reset_
    u_partition_p.io.global_clk_ovr_on := io.global_clk_ovr_on
    u_partition_p.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
    u_partition_p.io.nvdla_core_clk := io.dla_core_clk
    u_partition_p.io.dla_reset_rstn := nvdla_core_rstn
    u_partition_p.io.nvdla_clk_ovr_on := nvdla_clk_ovr_on

    //cacc2sdp
    u_partition_p.io.cacc2sdp_pd <> u_partition_a.io.cacc2sdp_pd

    u_partition_p.io.pwrbus_ram_pd := io.nvdla_pwrbus_ram_p_pd

}


object NV_NVDLADriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_nvdla())
}
