package nvdla
import chisel3._
import chisel3.util._

class NV_NVDLA_MCIF_READ_ig (implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))
        
        //client2mcif
        val client2mcif_rd_cdt_lat_fifo_pop = Input(Vec(conf.RDMA_NUM, Bool()))
        val client2mcif_rd_req_pd = Flipped(Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))))

        //mcif2noc
        val eg2ig_axi_vld = Input(Bool())
        val mcif2noc_axi_ar = DecoupledIO(new nocif_axi_rd_address_if)

        //config
        val reg2dp_rd_weight = Input(Vec(conf.RDMA_NUM, UInt(8.W)))
        val reg2dp_rd_os_cnt = Input(UInt(8.W))
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
    //---------------------read_bpt inst--------------------------------//
    val u_bpt = Array.fill(conf.RDMA_NUM){Module(new NV_NVDLA_MCIF_READ_IG_bpt)}
    for(i <- 0 until conf.RDMA_NUM){
        u_bpt(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_bpt(i).io.dma2bpt_cdt_lat_fifo_pop := io.client2mcif_rd_cdt_lat_fifo_pop(i)
        u_bpt(i).io.dma2bpt_req_pd <> io.client2mcif_rd_req_pd(i)

        u_bpt(i).io.tieoff_axid := conf.arr_tieoff_axid(i).asUInt(4.W)
        u_bpt(i).io.tieoff_lat_fifo_depth := conf.arr_tieoff_lat_fifo_depth(i).asUInt(9.W)
    }

    //arb
    val u_arb = Module(new NV_NVDLA_MCIF_READ_IG_arb)
    u_arb.io.nvdla_core_clk := io.nvdla_core_clk
    for(i <- 0 to conf.RDMA_NUM-1){
        u_arb.io.bpt2arb_req_pd(i) <> u_bpt(i).io.bpt2arb_req_pd
    }
    u_arb.io.reg2dp_rd_weight := io.reg2dp_rd_weight

    val u_cvt = Module(new NV_NVDLA_XXIF_READ_IG_cvt(cq_enabled = false))
    u_cvt.io.nvdla_core_clk := io.nvdla_core_clk
    u_cvt.io.eg2ig_axi_vld := io.eg2ig_axi_vld
    u_cvt.io.spt2cvt_req_pd <> u_arb.io.arb2spt_req_pd
    io.mcif2noc_axi_ar <> u_cvt.io.mcif2noc_axi_ar
    u_cvt.io.reg2dp_rd_os_cnt := io.reg2dp_rd_os_cnt
}}

object NV_NVDLA_MCIF_READ_igDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_READ_ig())
}
