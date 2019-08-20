package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_eg(implicit conf: nocifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))
        val eg2ig_axi_vld = Output(Bool())

        // cdma_dat
        val mcif2cdma_dat_rd_rsp_valid          = Output(Bool())
        val mcif2cdma_dat_rd_rsp_ready          = Input(Bool())
        val mcif2cdma_dat_rd_rsp_pd             = Output(UInt(conf.NVDLA_DMA_RD_RSP.W))

        // cdma_wt
        val mcif2cdma_wt_rd_rsp_valid          = Output(Bool())
        val mcif2cdma_wt_rd_rsp_ready          = Input(Bool())
        val mcif2cdma_wt_rd_rsp_pd             = Output(UInt(conf.NVDLA_DMA_RD_RSP.W))

        // sdp
        val mcif2sdp_rd_rsp_valid          = Output(Bool())
        val mcif2sdp_rd_rsp_ready          = Input(Bool())
        val mcif2sdp_rd_rsp_pd             = Output(UInt(conf.NVDLA_DMA_RD_RSP.W))

        // sdp_b
        val mcif2sdp_b_rd_rsp_valid          = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
        val mcif2sdp_b_rd_rsp_ready          = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
        val mcif2sdp_b_rd_rsp_pd             = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // sdp_n
        val mcif2sdp_n_rd_rsp_valid          = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
        val mcif2sdp_n_rd_rsp_ready          = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
        val mcif2sdp_n_rd_rsp_pd             = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // sdp_e
        val mcif2sdp_e_rd_rsp_valid          = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
        val mcif2sdp_e_rd_rsp_ready          = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
        val mcif2sdp_e_rd_rsp_pd             = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // pdp
        val mcif2pdp_rd_rsp_valid          = if(conf.NVDLA_PDP_ENABLE) Some(Output(Bool())) else None
        val mcif2pdp_rd_rsp_ready          = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None
        val mcif2pdp_rd_rsp_pd             = if(conf.NVDLA_PDP_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // cdp
        val mcif2cdp_rd_rsp_valid          = if(conf.NVDLA_CDP_ENABLE) Some(Output(Bool())) else None
        val mcif2cdp_rd_rsp_ready          = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None
        val mcif2cdp_rd_rsp_pd             = if(conf.NVDLA_CDP_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // rubik
        val mcif2rubik_rd_rsp_valid          = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(Bool())) else None
        val mcif2rubik_rd_rsp_ready          = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None
        val mcif2rubik_rd_rsp_pd             = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None

        // bdma
        val mcif2bdma_rd_rsp_valid          = if(conf.NVDLA_BDMA_ENABLE) Some(Output(Bool())) else None
        val mcif2bdma_rd_rsp_ready          = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None
        val mcif2bdma_rd_rsp_pd             = if(conf.NVDLA_BDMA_ENABLE) Some(Output(UInt(conf.NVDLA_DMA_RD_RSP.W))) else None


        //noc2mcif
        val noc2mcif_axi_r_rvalid = Input(Bool())
        val noc2mcif_axi_r_rready = Output(Bool())
        val noc2mcif_axi_r_rid = Input(UInt(8.W))
        val noc2mcif_axi_r_rlast = Input(Bool())
        val noc2mcif_axi_r_rdata = Input(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))
    })

    withClock(io.nvdla_core_clk){
    }
}



