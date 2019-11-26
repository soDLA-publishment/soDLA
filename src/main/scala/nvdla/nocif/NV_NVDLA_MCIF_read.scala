package nvdla
import chisel3._
import chisel3.util._

class NV_NVDLA_MCIF_read(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        //client2mcif
        val client2mcif_rd_cdt_lat_fifo_pop = Input(Vec(conf.RDMA_NUM, Bool()))
        val client2mcif_rd_req_pd = Flipped(Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))))
        val mcif2client_rd_rsp_pd = Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))

        //noc2mcif
        //mcif2noc
        val noc2mcif_axi_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if))
        val mcif2noc_axi_ar = DecoupledIO(new nocif_axi_rd_address_if)

        val reg2dp_rd_os_cnt = Input(UInt(8.W))
        val reg2dp_rd_weight = Input(Vec(conf.RDMA_NUM, UInt(8.W)))
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
withClock(io.nvdla_core_clk){
    val u_ig = Module(new NV_NVDLA_MCIF_READ_ig)
    val u_eg = Module(new NV_NVDLA_MCIF_READ_eg)
    //===========================================
    // NV_NVDLA_MCIF_READ_ig
    //===========================================
    u_ig.io.nvdla_core_clk := io.nvdla_core_clk
    u_ig.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    
    u_ig.io.client2mcif_rd_cdt_lat_fifo_pop := io.client2mcif_rd_cdt_lat_fifo_pop
    u_ig.io.client2mcif_rd_req_pd <> io.client2mcif_rd_req_pd

    u_ig.io.eg2ig_axi_vld := u_eg.io.eg2ig_axi_vld
    io.mcif2noc_axi_ar <> u_ig.io.mcif2noc_axi_ar

    u_ig.io.reg2dp_rd_weight := io.reg2dp_rd_weight
    u_ig.io.reg2dp_rd_os_cnt := io.reg2dp_rd_os_cnt
    //===========================================
    // NV_NVDLA_MCIF_READ_eg
    //===========================================
    u_eg.io.nvdla_core_clk := io.nvdla_core_clk
    u_eg.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    io.mcif2client_rd_rsp_pd <> u_eg.io.mcif2client_rd_rsp_pd
    u_eg.io.noc2mcif_axi_r <> io.noc2mcif_axi_r

}}

