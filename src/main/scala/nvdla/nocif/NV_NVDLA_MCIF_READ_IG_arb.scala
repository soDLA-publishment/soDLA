 package nvdla

 import chisel3._
 import chisel3.experimental._
 import chisel3.util._


 class NV_NVDLA_MCIF_READ_IG_arb(implicit conf:nocifConfiguration) extends Module {
     val io = IO(new Bundle {
         //general clock
         val nvdla_core_clk = Input(Clock())
         val nvdla_core_rstn = Input(Bool())

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


        // bpt2arb
        val bpt2arb_req_valid   = Input(Vec(conf.RDMA_NUM, Bool()))
        val bpt2arb_req_ready  = Output(Vec(conf.RDMA_NUM, Bool()))
        val bpt2arb_req_pd     = Input(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_IG_PW.W)))


        // arb2spt
        val arb2spt_req_valid = Output(Bool())
        val arb2spt_req_ready = Input(Bool())
        val arb2spt_req_pd = Output(UInt(conf.NVDLA_DMA_RD_IG_PW.W))
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
     }
 }