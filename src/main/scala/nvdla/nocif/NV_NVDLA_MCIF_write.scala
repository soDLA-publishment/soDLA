package nvdla

import chisel3._
import chisel3.util._


class NV_NVDLA_MCIF_write(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))


        val client2mcif_wr_req_pd = Flipped(Vec(conf.WDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))))
        val mcif2client_wr_rsp_complete = Output(Vec(conf.WDMA_NUM, Bool()))

        //noc2mcif
        val noc2mcif_axi_b = Flipped(Decoupled(new nocif_axi_wr_response_if))

        val mcif2noc_axi_aw = DecoupledIO(new nocif_axi_wr_address_if)
        val mcif2noc_axi_w = DecoupledIO(new nocif_axi_wr_data_if)

        val reg2dp_wr_os_cnt = Input(UInt(8.W))
        val reg2dp_wr_weight = Input(Vec(conf.WDMA_NUM, UInt(8.W)))
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
    val u_ig = Module(new NV_NVDLA_MCIF_WRITE_ig)
    val u_cq = Module(new NV_NVDLA_XXIF_WRITE_cq(vec_num = conf.MCIF_WRITE_CQ_VEC_NUM, width = conf.MCIF_WRITE_CQ_WIDTH))
    val u_eg = Module(new NV_NVDLA_MCIF_WRITE_eg)
    //===========================================
    // NV_NVDLA_MCIF_WRITE_ig
    //===========================================
    u_ig.io.nvdla_core_clk := io.nvdla_core_clk
    u_ig.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_ig.io.eg2ig_axi_len <> u_eg.io.eg2ig_axi_len
    u_ig.io.client2mcif_wr_req_pd <> io.client2mcif_wr_req_pd
    io.mcif2noc_axi_aw <> u_ig.io.mcif2noc_axi_aw
    io.mcif2noc_axi_w <> u_ig.io.mcif2noc_axi_w
    u_ig.io.reg2dp_wr_os_cnt := io.reg2dp_wr_os_cnt
    u_ig.io.reg2dp_wr_weight := io.reg2dp_wr_weight
    
    //===========================================
    // NV_NVDLA_MCIF_WRITE_cq
    //===========================================
    u_cq.io.clk := io.nvdla_core_clk
    u_cq.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_cq.io.cq_wr_pd <> u_ig.io.cq_wr_pd
    u_cq.io.cq_wr_thread_id := u_ig.io.cq_wr_thread_id

    //===========================================
    // NV_NVDLA_MCIF_WRITE_eg
    //===========================================
    u_eg.io.nvdla_core_clk := io.nvdla_core_clk
    io.mcif2client_wr_rsp_complete <> u_eg.io.mcif2client_wr_rsp_complete
    u_eg.io.cq_rd_pd <> u_cq.io.cq_rd_pd
    u_eg.io.noc2mcif_axi_b <> io.noc2mcif_axi_b

        
}}

object NV_NVDLA_MCIF_writeDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_write())
}
