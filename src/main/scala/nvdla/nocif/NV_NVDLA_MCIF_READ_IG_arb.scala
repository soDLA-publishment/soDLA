 package nvdla

 import chisel3._
 import chisel3.experimental._
 import chisel3.util._


 class NV_NVDLA_MCIF_READ_IG_arb(implicit conf:nocifConfiguration) extends Module {
     val io = IO(new Bundle {
         //general clock
         val nvdla_core_clk = Input(Clock())
         val nvdla_core_rstn = Input(Bool())

         // bpt2arb
         val bpt2arb_req_valid = Input(Vec(conf.RDMA_NUM, Bool()))
         val bpt2arb_req_ready = Output(Vec(conf.RDMA_NUM, Bool()))
         val bpt2arb_req_pd = Input(Vec(conf.RDMA_NUM, UInt(75.W)))

         // arb2spt
         val arb2spt_req_valid = Output(Bool())
         val arb2spt_req_ready = Input(Bool())
         val arb2spt_req_pd = Output(UInt(75.W))

         //
        val reg2dp_rd_weight_bdma = Input(UInt(8.W))
        val reg2dp_rd_weight_cdma_dat = Input(UInt(8.W))
        val reg2dp_rd_weight_cdma_wt = Input(UInt(8.W))
        val reg2dp_rd_weight_cdp = Input(UInt(8.W))
        val reg2dp_rd_weight_pdp = Input(UInt(8.W))
        val reg2dp_rd_weight_rbk = Input(UInt(8.W))
        val reg2dp_rd_weight_sdp = Input(UInt(8.W))
        val reg2dp_rd_weight_sdp_b = Input(UInt(8.W))
        val reg2dp_rd_weight_sdp_e = Input(UInt(8.W))
        val reg2dp_rd_weight_sdp_n = Input(UInt(8.W))
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