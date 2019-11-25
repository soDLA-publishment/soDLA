package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_MCIF_WRITE_ig(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val pwrbus_ram_pd = Input(UInt(32.W))
        
        val eg2ig_axi_len = Flipped(ValidIO(UInt(2.W)))

        //cq_wr
        val cq_wr_pd = DecoupledIO(UInt(conf.MCIF_WRITE_CQ_WIDTH.W))
        val cq_wr_thread_id = Output(UInt(conf.MCIF_WRITE_CQ_VEC_NUM.W))

        //mcif2noc
        val mcif2noc_axi_aw = DecoupledIO(new nocif_axi_wr_address_if)
        val mcif2noc_axi_w = DecoupledIO(new nocif_axi_wr_data_if)

        //client2mcif
        val client2mcif_wr_req_pd = Flipped(Vec(conf.WDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))))

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
    val u_bpt = Array.fill(conf.WDMA_NUM){Module(new NV_NVDLA_MCIF_WRITE_IG_bpt)}
    val u_arb = Module(new NV_NVDLA_MCIF_WRITE_IG_arb)
    val u_cvt = Module(new NV_NVDLA_MCIF_WRITE_IG_cvt)
    //===========================================
    // NV_NVDLA_MCIF_WRITE_IG_bpt
    //===========================================
    for(i <- 0 to conf.WDMA_NUM-1){
        u_bpt(i).io.nvdla_core_clk := io.nvdla_core_clk 
        u_bpt(i).io.pwrbus_ram_pd := io.pwrbus_ram_pd 
        u_bpt(i).io.dma2bpt_req_pd <> io.client2mcif_wr_req_pd(i)
        u_arb.io.bpt2arb_cmd_pd(i) <> u_bpt(i).io.bpt2arb_cmd_pd
        u_arb.io.bpt2arb_dat_pd(i) <> u_bpt(i).io.bpt2arb_dat_pd
        u_bpt(i).io.axid := conf.awr_tieoff_axid(i).U
    }

    //===========================================
    // NV_NVDLA_MCIF_WRITE_IG_arb
    //===========================================
    u_arb.io.nvdla_core_clk := io.nvdla_core_clk
    u_arb.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_arb.io.reg2dp_wr_weight := io.reg2dp_wr_weight

    //===========================================
    // NV_NVDLA_MCIF_WRITE_IG_arb
    //===========================================
    u_cvt.io.nvdla_core_clk := io.nvdla_core_clk
    u_cvt.io.spt2cvt_cmd_pd <> u_arb.io.arb2spt_cmd_pd
    u_cvt.io.spt2cvt_dat_pd <> u_arb.io.arb2spt_dat_pd
    io.cq_wr_pd <> u_cvt.io.cq_wr_pd
    io.cq_wr_thread_id := u_cvt.io.cq_wr_thread_id
    io.mcif2noc_axi_aw <> u_cvt.io.mcif2noc_axi_aw
    io.mcif2noc_axi_w <> u_cvt.io.mcif2noc_axi_w
    u_cvt.io.eg2ig_axi_len <> io.eg2ig_axi_len
    u_cvt.io.reg2dp_wr_os_cnt := io.reg2dp_wr_os_cnt


}}   


object NV_NVDLA_MCIF_WRITE_igDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_WRITE_ig)
}