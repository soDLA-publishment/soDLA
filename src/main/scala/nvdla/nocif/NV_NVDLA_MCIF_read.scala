// package nvdla
// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_MCIF_read(implicit conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle{
//         //general clock
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         //client2mcif
//         val client2mcif_rd_cdt_lat_fifo_pop = Input(Vec(conf.RDMA_NUM, Bool()))
//         val client2mcif_rd_req_pd = Flipped(Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))))
//         val mcif2client_rd_rsp_pd = Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))

//         //noc2mcif
//         //mcif2noc
//         val noc2mcif_axi_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if))
//         val mcif2noc_axi_ar = DecoupledIO(new nocif_axi_rd_address_if)

//         val reg2dp_rd_os_cnt    = Input(UInt(8.W))
//         val reg2dp_rd_weight_client = Input(Vec(conf.RDMA_NUM, UInt(8.W)))
//     })

//     val eg2ig_axi_vld = Wire(Bool())

//     val u_ig = Module(new NV_NVDLA_MCIF_READ_ig)
//     u_ig.io.nvdla_core_clk      := io.nvdla_core_clk
//     u_ig.io.nvdla_core_rstn     := io.nvdla_core_rstn
//     u_ig.io.pwrbus_ram_pd       := io.pwrbus_ram_pd
//     u_ig.io.reg2dp_rd_os_cnt    := io.reg2dp_rd_os_cnt

//     u_ig.io.rdma2mcif_rd_cdt_lat_fifo_pop  := io.rdma2mcif_rd_cdt_lat_fifo_pop
//     u_ig.io.rdma2mcif_rd_req_valid         := io.rdma2mcif_rd_req_valid
//     io.rdma2mcif_rd_req_ready              :=  u_ig.io.rdma2mcif_rd_req_ready
//     u_ig.io.rdma2mcif_rd_req_pd            := io.rdma2mcif_rd_req_pd

//     // regs
//     u_ig.io.reg2dp_rw_weight   := io.reg2dp_rw_weight

//     u_ig.io.eg2ig_axi_vld          := eg2ig_axi_vld
//     io.mcif2noc_axi_ar_arvalid     := u_ig.io.mcif2noc_axi_ar_arvalid
//     u_ig.io.mcif2noc_axi_ar_arready:= io.mcif2noc_axi_ar_arready
//     io.mcif2noc_axi_ar_arid        := u_ig.io.mcif2noc_axi_ar_arid
//     io.mcif2noc_axi_ar_arlen       := u_ig.io.mcif2noc_axi_ar_arlen
//     io.mcif2noc_axi_ar_araddr      := u_ig.io.mcif2noc_axi_ar_araddr


//     val u_eg = Module(new NV_NVDLA_MCIF_READ_eg)
//     u_eg.io.nvdla_core_clk      := io.nvdla_core_clk
//     u_eg.io.nvdla_core_rstn     := io.nvdla_core_rstn
//     u_eg.io.pwrbus_ram_pd       := io.pwrbus_ram_pd
//     eg2ig_axi_vld               := u_eg.io.eg2ig_axi_vld

//     io.mcif2rdma_rd_rsp_valid       :=  u_eg.io.mcif2rdma_rd_rsp_valid
//     u_eg.io.mcif2rdma_rd_rsp_ready  := io.mcif2rdma_rd_rsp_ready
//     io.mcif2rdma_rd_rsp_pd          :=  u_eg.io.mcif2rdma_rd_rsp_pd

//     u_eg.io.noc2mcif_axi_r_rvalid    := io.noc2mcif_axi_r_rvalid
//     io.noc2mcif_axi_r_rready         := u_eg.io.noc2mcif_axi_r_rready
//     u_eg.io.noc2mcif_axi_r_rid       := io.noc2mcif_axi_r_rid
//     u_eg.io.noc2mcif_axi_r_rlast     := io.noc2mcif_axi_r_rlast
//     u_eg.io.noc2mcif_axi_r_rdata     := io.noc2mcif_axi_r_rdata
// }


// object NV_NVDLA_MCIF_readDriver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_read())
// }