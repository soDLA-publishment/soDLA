package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_read(implicit conf: nocifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk      = Input(Clock())
        val nvdla_core_rstn     = Input(Bool())
        val pwrbus_ram_pd       = Input(UInt(32.W))
        val reg2dp_rd_os_cnt    = Input(UInt(8.W))

        val reg2dp_rd_weight_cdma_dat   = Input(UInt(8.W))
        val reg2dp_rd_weight_cdma_wt    = Input(UInt(8.W))
        val reg2dp_rd_weight_sdp        = Input(UInt(8.W))
        val reg2dp_rd_weight_sdp_b      = if(conf.NVDLA_SDP_BS_ENABLE)  Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_sdp_n      = if(conf.NVDLA_SDP_BN_ENABLE)  Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_sdp_e      = if(conf.NVDLA_SDP_EW_ENABLE)  Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_pdp        = if(conf.NVDLA_PDP_ENABLE)     Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_cdp        = if(conf.NVDLA_CDP_ENABLE)     Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_rbk        = if(conf.NVDLA_RUBIK_ENABLE)   Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_bdma       = if(conf.NVDLA_BDMA_ENABLE)    Some(Input(UInt(8.W))) else None

        // cdma_dat
        val cdma_dat2mcif_rd_cdt_lat_fifo_pop   = Input(Bool())
        val cdma_dat2mcif_rd_req_valid          = Input(Bool())
        val cdma_dat2mcif_rd_req_ready          = Output(Bool())
        val cdma_dat2mcif_rd_req_pd             = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))
        val mcif2cdma_dat_rd_rsp_valid          = Output(Bool())
        val mcif2cdma_dat_rd_rsp_ready          = Input(Bool())
        val mcif2cdma_dat_rd_rsp_pd             = Output(UInt(conf.NVDLA_DMA_RD_RSP.W))

        // cdma_wt
        val cdma_wt2mcif_rd_cdt_lat_fifo_pop   = Input(Bool())
        val cdma_wt2mcif_rd_req_valid          = Input(Bool())
        val cdma_wt2mcif_rd_req_ready          = Output(Bool())
        val cdma_wt2mcif_rd_req_pd             = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))
        val mcif2cdma_wt_rd_rsp_valid          = Output(Bool())
        val mcif2cdma_wt_rd_rsp_ready          = Input(Bool())
        val mcif2cdma_wt_rd_rsp_pd             = Output(UInt(conf.NVDLA_DMA_RD_RSP.W))

        // sdp
        val sdp2mcif_rd_cdt_lat_fifo_pop   = Input(Bool())
        val sdp2mcif_rd_req_valid          = Input(Bool())
        val sdp2mcif_rd_req_ready          = Output(Bool())
        val sdp2mcif_rd_req_pd             = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))
        val mcif2sdp_rd_rsp_valid          = Output(Bool())
        val mcif2sdp_rd_rsp_ready          = Input(Bool())
        val mcif2sdp_rd_rsp_pd             = Output(UInt(conf.NVDLA_DMA_RD_RSP.W))

        // sdp_b
        val sdp_b2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
        val sdp_b2mcif_rd_req_valid          = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
        val sdp_b2mcif_rd_req_ready          = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
        val sdp_b2mcif_rd_req_pd             = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val mcif2sdp_b_rd_rsp_valid          = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
        val mcif2sdp_b_rd_rsp_ready          = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
        val mcif2sdp_b_rd_rsp_pd             = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // sdp_n
        val sdp_n2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
        val sdp_n2mcif_rd_req_valid          = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
        val sdp_n2mcif_rd_req_ready          = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
        val sdp_n2mcif_rd_req_pd             = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val mcif2sdp_n_rd_rsp_valid          = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
        val mcif2sdp_n_rd_rsp_ready          = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
        val mcif2sdp_n_rd_rsp_pd             = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // sdp_e
        val sdp_e2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
        val sdp_e2mcif_rd_req_valid          = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
        val sdp_e2mcif_rd_req_ready          = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
        val sdp_e2mcif_rd_req_pd             = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val mcif2sdp_e_rd_rsp_valid          = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
        val mcif2sdp_e_rd_rsp_ready          = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
        val mcif2sdp_e_rd_rsp_pd             = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // pdp
        val pdp2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None
        val pdp2mcif_rd_req_valid          = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None
        val pdp2mcif_rd_req_ready          = if(conf.NVDLA_PDP_ENABLE) Some(Output(Bool())) else None
        val pdp2mcif_rd_req_pd             = if(conf.NVDLA_PDP_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val mcif2pdp_rd_rsp_valid          = if(conf.NVDLA_PDP_ENABLE) Some(Output(Bool())) else None
        val mcif2pdp_rd_rsp_ready          = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None
        val mcif2pdp_rd_rsp_pd             = if(conf.NVDLA_PDP_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // cdp
        val cdp2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None
        val cdp2mcif_rd_req_valid          = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None
        val cdp2mcif_rd_req_ready          = if(conf.NVDLA_CDP_ENABLE) Some(Output(Bool())) else None
        val cdp2mcif_rd_req_pd             = if(conf.NVDLA_CDP_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val mcif2cdp_rd_rsp_valid          = if(conf.NVDLA_CDP_ENABLE) Some(Output(Bool())) else None
        val mcif2cdp_rd_rsp_ready          = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None
        val mcif2cdp_rd_rsp_pd             = if(conf.NVDLA_CDP_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // rubik
        val rubik2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None
        val rubik2mcif_rd_req_valid          = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None
        val rubik2mcif_rd_req_ready          = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(Bool())) else None
        val rubik2mcif_rd_req_pd             = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val mcif2rubik_rd_rsp_valid          = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(Bool())) else None
        val mcif2rubik_rd_rsp_ready          = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None
        val mcif2rubik_rd_rsp_pd             = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // bdma
        val bdma2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None
        val bdma2mcif_rd_req_valid          = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None
        val bdma2mcif_rd_req_ready          = if(conf.NVDLA_BDMA_ENABLE) Some(Output(Bool())) else None
        val bdma2mcif_rd_req_pd             = if(conf.NVDLA_BDMA_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val mcif2bdma_rd_rsp_valid          = if(conf.NVDLA_BDMA_ENABLE) Some(Output(Bool())) else None
        val mcif2bdma_rd_rsp_ready          = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None
        val mcif2bdma_rd_rsp_pd             = if(conf.NVDLA_BDMA_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None


        //noc2mcif
        val noc2mcif_axi_r_rvalid = Input(Bool())
        val noc2mcif_axi_r_rready = Output(Bool())
        val noc2mcif_axi_r_rid = Input(UInt(8.W))
        val noc2mcif_axi_r_rlast = Input(Bool())
        val noc2mcif_axi_r_rdata = Input(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))

        //mcif2noc
        val mcif2noc_axi_ar_arvalid = Output(Bool())
        val mcif2noc_axi_ar_arready = Input(Bool())
        val mcif2noc_axi_ar_arid = Output(UInt(8.W))
        val mcif2noc_axi_ar_arlen = Output(UInt(4.W))
        val mcif2noc_axi_ar_araddr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    })

    withClock(io.nvdla_core_clk){
    }
}