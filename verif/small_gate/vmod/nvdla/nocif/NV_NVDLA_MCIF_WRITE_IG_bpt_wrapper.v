module NV_NVDLA_MCIF_WRITE_IG_bpt_wrapper (
   nvdla_core_clk //|< i
  ,nvdla_core_rstn //|< i
  ,pwrbus_ram_pd //|< i
  ,dma2bpt_req_valid //|< i
  ,dma2bpt_req_ready //|> o
  ,dma2bpt_req_pd //|< i
  ,bpt2arb_cmd_valid //|> o
  ,bpt2arb_cmd_ready //|< i
  ,bpt2arb_cmd_pd //|> o
  ,bpt2arb_dat_valid //|> o
  ,bpt2arb_dat_ready //|< i
  ,bpt2arb_dat_pd //|> o
  ,axid //|< i
);
input nvdla_core_clk;
input nvdla_core_rstn;
input [31:0] pwrbus_ram_pd;
input [3:0] axid;
input dma2bpt_req_valid;
output dma2bpt_req_ready;
input [66 -1:0] dma2bpt_req_pd;
output bpt2arb_cmd_valid;
input bpt2arb_cmd_ready;
output [32 +13 -1:0] bpt2arb_cmd_pd;
output bpt2arb_dat_valid;
input bpt2arb_dat_ready;
output [66 -2:0] bpt2arb_dat_pd;

NV_NVDLA_MCIF_WRITE_IG_bpt (
.io_nvdla_core_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_axid (axid)
,.io_dma2bpt_req_pd_ready (dma2bpt_req_ready)
,.io_dma2bpt_req_pd_valid (dma2bpt_req_valid)
,.io_dma2bpt_req_pd_bits (dma2bpt_req_pd)
,.io_bpt2arb_cmd_pd_ready (bpt2arb_cmd_ready)
,.io_bpt2arb_cmd_pd_valid (bpt2arb_cmd_valid)
,.io_bpt2arb_cmd_pd_bits (bpt2arb_cmd_pd)
,.io_bpt2arb_dat_pd_ready (bpt2arb_dat_ready)
,.io_bpt2arb_dat_pd_valid (bpt2arb_dat_valid)
,.io_bpt2arb_dat_pd_bits (bpt2arb_dat_pd)
);


endmodule