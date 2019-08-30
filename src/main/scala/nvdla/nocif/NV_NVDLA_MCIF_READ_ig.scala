package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_ig (implicit conf: xxifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))
        val reg2dp_rd_os_cnt = Input(UInt(8.W))

        val reg2dp_rw_weight = Input(Vec(conf.RDMA_NUM, UInt(8.W)))

        val rdma2mcif_rd_cdt_lat_fifo_pop   = Input(Vec(conf.RDMA_NUM, Bool()))
        val rdma2mcif_rd_req_valid          = Input(Vec(conf.RDMA_NUM, Bool()))
        val rdma2mcif_rd_req_ready          = Output(Vec(conf.RDMA_NUM, Bool()))
        val rdma2mcif_rd_req_pd             = Input(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_REQ.W)))

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


    val  bpt2arb_req_pd = Wire(Vec(conf.RDMA_NUM, UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+10).W)))
    val  bpt2arb_req_ready = Wire(Vec(conf.RDMA_NUM, Bool()))
    val  bpt2arb_req_valid = Wire(Vec(conf.RDMA_NUM, Bool()))

    val u_bpt = Array.fill(conf.RDMA_NUM){ Module(new NV_NVDLA_MCIF_READ_IG_bpt)}
    for(i <- 0 until conf.RDMA_NUM){
        u_bpt(i).io.nvdla_core_clk := io.nvdla_core_clk
//        u_bpt(i).io.nvdla_core_rstn := io.nvdla_core_rstn

        u_bpt(i).io.dma2bpt_cdt_lat_fifo_pop := io.rdma2mcif_rd_cdt_lat_fifo_pop(i)
        u_bpt(i).io.dma2bpt_req_pd := io.rdma2mcif_rd_req_pd(i)
        io.rdma2mcif_rd_req_ready(i) := u_bpt(i).io.dma2bpt_req_ready
        u_bpt(i).io.dma2bpt_req_valid := io.rdma2mcif_rd_req_valid(i)

        bpt2arb_req_pd(i) := u_bpt(i).io.bpt2arb_req_pd
        bpt2arb_req_valid(i) := u_bpt(i).io.bpt2arb_req_valid
        u_bpt(i).io.bpt2arb_req_ready := bpt2arb_req_ready(i)
    }


    var j = 0
    // cdma_dat
    u_bpt(j).io.tieoff_axid := 8.U(4.W)
    u_bpt(j).io.tieoff_lat_fifo_depth := 0.U(9.W)
    j += 1;

    // cdma_wt
    u_bpt(j).io.tieoff_axid := 9.U(4.W)
    u_bpt(j).io.tieoff_lat_fifo_depth := 0.U(9.W)
    j += 1;

    // sdp
    u_bpt(j).io.tieoff_axid := 1.U(4.W)
    u_bpt(j).io.tieoff_lat_fifo_depth := 256.U(9.W)
    j += 1;

    if(conf.NVDLA_SDP_BS_ENABLE) {
        u_bpt(j).io.tieoff_axid := 5.U(4.W)
        u_bpt(j).io.tieoff_lat_fifo_depth := 256.U(9.W)
        j += 1;
    }

    if(conf.NVDLA_SDP_BN_ENABLE) {
        u_bpt(j).io.tieoff_axid := 6.U(4.W)
        u_bpt(j).io.tieoff_lat_fifo_depth := 256.U(9.W)
        j += 1;
    }

    if(conf.NVDLA_SDP_EW_ENABLE) {
        u_bpt(j).io.tieoff_axid := 7.U(4.W)
        u_bpt(j).io.tieoff_lat_fifo_depth := 256.U(9.W)
        j += 1;
    }

    if(conf.NVDLA_PDP_ENABLE) {
        u_bpt(j).io.tieoff_axid := 2.U(4.W)
        u_bpt(j).io.tieoff_lat_fifo_depth := 256.U(9.W)
        j += 1;
    }

    if(conf.NVDLA_CDP_ENABLE) {
        u_bpt(j).io.tieoff_axid := 2.U(4.W)
        u_bpt(j).io.tieoff_lat_fifo_depth := 256.U(9.W)
        j += 1;
    }

    val u_arb = Module(new NV_NVDLA_MCIF_READ_IG_arb)
    u_arb.io.nvdla_core_clk := io.nvdla_core_clk
    u_arb.io.nvdla_core_rstn := io.nvdla_core_rstn

    u_arb.io.reg2dp_rw_weight := io.reg2dp_rw_weight


    u_arb.io.bpt2arb_req_valid := bpt2arb_req_pd
    bpt2arb_req_ready := u_arb.io.bpt2arb_req_ready
    u_arb.io.bpt2arb_req_pd := bpt2arb_req_pd


    val arb2spt_req_valid = Wire(Bool())
    val arb2spt_req_ready = Wire(Bool())
    val arb2spt_req_pd = Wire(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+10).W))

    arb2spt_req_valid := u_arb.io.arb2spt_req_valid
    u_arb.io.arb2spt_req_ready := arb2spt_req_ready
    arb2spt_req_pd := u_arb.io.arb2spt_req_pd

    val u_cvt = Module(new NV_NVDLA_MCIF_READ_IG_cvt)
    u_cvt.io.nvdla_core_clk := io.nvdla_core_clk
    u_cvt.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_cvt.io.reg2dp_rd_os_cnt := io.reg2dp_rd_os_cnt
    u_cvt.io.eg2ig_axi_vld := io.eg2ig_axi_vld

    u_cvt.io.spt2cvt_req_valid := arb2spt_req_valid
    arb2spt_req_ready := u_cvt.io.spt2cvt_req_ready
    u_cvt.io.spt2cvt_req_pd := arb2spt_req_pd
}
