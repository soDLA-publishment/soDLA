package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_ig (implicit conf: nocifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))
        val reg2dp_rd_os_cnt = Input(UInt(8.W))


        // cdma_dat
        val cdma_dat2mcif_rd_cdt_lat_fifo_pop   = Input(Bool())
        val cdma_dat2mcif_rd_req_valid          = Input(Bool())
        val cdma_dat2mcif_rd_req_ready          = Output(Bool())
        val cdma_dat2mcif_rd_req_pd             = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))

        // cdma_wt
        val cdma_wt2mcif_rd_cdt_lat_fifo_pop   = Input(Bool())
        val cdma_wt2mcif_rd_req_valid          = Input(Bool())
        val cdma_wt2mcif_rd_req_ready          = Output(Bool())
        val cdma_wt2mcif_rd_req_pd             = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))

        // sdp
        val sdp2mcif_rd_cdt_lat_fifo_pop   = Input(Bool())
        val sdp2mcif_rd_req_valid          = Input(Bool())
        val sdp2mcif_rd_req_ready          = Output(Bool())
        val sdp2mcif_rd_req_pd             = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))

        // sdp_b
        val sdp_b2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
        val sdp_b2mcif_rd_req_valid          = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(Bool())) else None
        val sdp_b2mcif_rd_req_ready          = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
        val sdp_b2mcif_rd_req_pd             = if(conf.NVDLA_SDP_BS_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None

        // sdp_n
        val sdp_n2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
        val sdp_n2mcif_rd_req_valid          = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(Bool())) else None
        val sdp_n2mcif_rd_req_ready          = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
        val sdp_n2mcif_rd_req_pd             = if(conf.NVDLA_SDP_BN_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None

        // sdp_e
        val sdp_e2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
        val sdp_e2mcif_rd_req_valid          = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(Bool())) else None
        val sdp_e2mcif_rd_req_ready          = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
        val sdp_e2mcif_rd_req_pd             = if(conf.NVDLA_SDP_EW_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None

        // pdp
        val pdp2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None
        val pdp2mcif_rd_req_valid          = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None
        val pdp2mcif_rd_req_ready          = if(conf.NVDLA_PDP_ENABLE) Some(Output(Bool())) else None
        val pdp2mcif_rd_req_pd             = if(conf.NVDLA_PDP_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None

        // cdp
        val cdp2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None
        val cdp2mcif_rd_req_valid          = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None
        val cdp2mcif_rd_req_ready          = if(conf.NVDLA_CDP_ENABLE) Some(Output(Bool())) else None
        val cdp2mcif_rd_req_pd             = if(conf.NVDLA_CDP_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None

        // rubik
        val rubik2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None
        val rubik2mcif_rd_req_valid          = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None
        val rubik2mcif_rd_req_ready          = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(Bool())) else None
        val rubik2mcif_rd_req_pd             = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None

        // bdma
        val bdma2mcif_rd_cdt_lat_fifo_pop   = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None
        val bdma2mcif_rd_req_valid          = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None
        val bdma2mcif_rd_req_ready          = if(conf.NVDLA_BDMA_ENABLE) Some(Output(Bool())) else None
        val bdma2mcif_rd_req_pd             = if(conf.NVDLA_BDMA_ENABLE) Some(Input(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None


        val cq_wr_pvld = if(conf.NVDLA_PRIMARY_MEMIF_WIDTH > conf.NVDLA_MEMORY_ATOMIC_WIDTH) Some(Output(Bool())) else None
        val cq_wr_prdy = if(conf.NVDLA_PRIMARY_MEMIF_WIDTH > conf.NVDLA_MEMORY_ATOMIC_WIDTH) Some(Input(Bool())) else None
        val cq_wr_thread_id = if(conf.NVDLA_PRIMARY_MEMIF_WIDTH > conf.NVDLA_MEMORY_ATOMIC_WIDTH) Some(Output(UInt(4.W))) else None
        val cq_wr_pd = if(conf.NVDLA_PRIMARY_MEMIF_WIDTH > conf.NVDLA_MEMORY_ATOMIC_WIDTH) Some(Output(UInt(7.W))) else None

        //mcif2noc
        val eg2ig_axi_vld = Input(Bool())
        val mcif2noc_axi_ar_arvalid = Output(Bool())
        val mcif2noc_axi_ar_arready = Input(Bool())
        val mcif2noc_axi_ar_arid = Output(UInt(8.W))
        val mcif2noc_axi_ar_arlen = Output(UInt(4.W))
        val mcif2noc_axi_ar_araddr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))

    })

    withClock(io.nvdla_core_clk){
    }
}
