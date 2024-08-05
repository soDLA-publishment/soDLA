module NV_NVDLA_MCIF_READ_IG_bpt_wrapper (
   nvdla_core_clk //|< i
  ,nvdla_core_rstn //|< i
  ,dma2bpt_cdt_lat_fifo_pop //|< i
  ,dma2bpt_req_pd //|< i
  ,dma2bpt_req_valid //|< i
  ,dma2bpt_req_ready //|> o
  ,bpt2arb_req_pd //|> o
  ,bpt2arb_req_valid //|> o
  ,bpt2arb_req_ready //|< i
  ,tieoff_axid //|< i
  ,tieoff_lat_fifo_depth //|< i
);
input nvdla_core_clk;
input nvdla_core_rstn;
input dma2bpt_req_valid;
output dma2bpt_req_ready;
input [47 -1:0] dma2bpt_req_pd;
input dma2bpt_cdt_lat_fifo_pop;
output bpt2arb_req_valid;
input bpt2arb_req_ready;
output [32 +11 -1:0] bpt2arb_req_pd;
input [3:0] tieoff_axid;
input [8:0] tieoff_lat_fifo_depth;

NV_NVDLA_MCIF_READ_IG_bpt (
.reset (~nvdla_core_rstn)
,.io_nvdla_core_clk (nvdla_core_clk)
,.io_dma2bpt_req_pd_ready (dma2bpt_req_ready)
,.io_dma2bpt_req_pd_valid (dma2bpt_req_valid)
,.io_dma2bpt_req_pd_bits (dma2bpt_req_pd)
,.io_dma2bpt_cdt_lat_fifo_pop (dma2bpt_cdt_lat_fifo_pop)
,.io_bpt2arb_req_pd_ready (bpt2arb_req_ready)
,.io_bpt2arb_req_pd_valid (bpt2arb_req_valid)
,.io_bpt2arb_req_pd_bits  (bpt2arb_req_pd)
,.io_tieoff_axid  (tieoff_axid)
,.io_tieoff_lat_fifo_depth (tieoff_lat_fifo_depth)
);


endmodule