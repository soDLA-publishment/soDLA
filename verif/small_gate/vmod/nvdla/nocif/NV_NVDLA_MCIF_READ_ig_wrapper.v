module NV_NVDLA_MCIF_READ_ig (
   nvdla_core_clk //|< i
  ,nvdla_core_rstn //|< i
  ,pwrbus_ram_pd
  ,reg2dp_rd_os_cnt
  ,reg2dp_rd_weight_cdma_dat
  ,reg2dp_rd_weight_cdma_wt
  ,reg2dp_rd_weight_sdp
  ,reg2dp_rd_weight_sdp_b
  ,reg2dp_rd_weight_sdp_n
  ,reg2dp_rd_weight_pdp
  ,reg2dp_rd_weight_cdp
  ,cdma_dat2mcif_rd_cdt_lat_fifo_pop
  ,cdma_dat2mcif_rd_req_valid
  ,cdma_dat2mcif_rd_req_ready
  ,cdma_dat2mcif_rd_req_pd
  ,cdma_wt2mcif_rd_cdt_lat_fifo_pop
  ,cdma_wt2mcif_rd_req_valid
  ,cdma_wt2mcif_rd_req_ready
  ,cdma_wt2mcif_rd_req_pd
  ,sdp2mcif_rd_cdt_lat_fifo_pop
  ,sdp2mcif_rd_req_valid
  ,sdp2mcif_rd_req_ready
  ,sdp2mcif_rd_req_pd
  ,sdp_b2mcif_rd_cdt_lat_fifo_pop
  ,sdp_b2mcif_rd_req_valid
  ,sdp_b2mcif_rd_req_ready
  ,sdp_b2mcif_rd_req_pd
  ,sdp_n2mcif_rd_cdt_lat_fifo_pop
  ,sdp_n2mcif_rd_req_valid
  ,sdp_n2mcif_rd_req_ready
  ,sdp_n2mcif_rd_req_pd
  ,pdp2mcif_rd_cdt_lat_fifo_pop
  ,pdp2mcif_rd_req_valid
  ,pdp2mcif_rd_req_ready
  ,pdp2mcif_rd_req_pd
  ,cdp2mcif_rd_cdt_lat_fifo_pop
  ,cdp2mcif_rd_req_valid
  ,cdp2mcif_rd_req_ready
  ,cdp2mcif_rd_req_pd
  ,eg2ig_axi_vld
  ,mcif2noc_axi_ar_araddr //|> o
  ,mcif2noc_axi_ar_arready //|< i
  ,mcif2noc_axi_ar_arid //|> o
  ,mcif2noc_axi_ar_arlen //|> o
  ,mcif2noc_axi_ar_arvalid //|> o
);
input nvdla_core_clk;
input nvdla_core_rstn;
input [31:0] pwrbus_ram_pd;
input [7:0] reg2dp_rd_os_cnt;
input  [7:0] reg2dp_rd_weight_cdma_dat;
input  [7:0] reg2dp_rd_weight_cdma_wt;
input  [7:0] reg2dp_rd_weight_sdp;
input  [7:0] reg2dp_rd_weight_sdp_b;
input  [7:0] reg2dp_rd_weight_sdp_n;
input  [7:0] reg2dp_rd_weight_pdp;
input  [7:0] reg2dp_rd_weight_cdp;
input  cdma_dat2mcif_rd_cdt_lat_fifo_pop;
input  cdma_dat2mcif_rd_req_valid;
output cdma_dat2mcif_rd_req_ready;
input [47 -1:0] cdma_dat2mcif_rd_req_pd;
input  cdma_wt2mcif_rd_cdt_lat_fifo_pop;
input  cdma_wt2mcif_rd_req_valid;
output cdma_wt2mcif_rd_req_ready;
input [47 -1:0] cdma_wt2mcif_rd_req_pd;
input  sdp2mcif_rd_cdt_lat_fifo_pop;
input  sdp2mcif_rd_req_valid;
output sdp2mcif_rd_req_ready;
input [47 -1:0] sdp2mcif_rd_req_pd;
input  sdp_b2mcif_rd_cdt_lat_fifo_pop;
input  sdp_b2mcif_rd_req_valid;
output sdp_b2mcif_rd_req_ready;
input [47 -1:0] sdp_b2mcif_rd_req_pd;
input  sdp_n2mcif_rd_cdt_lat_fifo_pop;
input  sdp_n2mcif_rd_req_valid;
output sdp_n2mcif_rd_req_ready;
input [47 -1:0] sdp_n2mcif_rd_req_pd;
input  pdp2mcif_rd_cdt_lat_fifo_pop;
input  pdp2mcif_rd_req_valid;
output pdp2mcif_rd_req_ready;
input [47 -1:0] pdp2mcif_rd_req_pd;
input  cdp2mcif_rd_cdt_lat_fifo_pop;
input  cdp2mcif_rd_req_valid;
output cdp2mcif_rd_req_ready;
input [47 -1:0] cdp2mcif_rd_req_pd;
output mcif2noc_axi_ar_arvalid;
input mcif2noc_axi_ar_arready;
output [7:0] mcif2noc_axi_ar_arid;
output [3:0] mcif2noc_axi_ar_arlen;
output [32 -1:0] mcif2noc_axi_ar_araddr;
input eg2ig_axi_vld;


NV_NVDLA_MCIF_READ_ig (
.io_nvdla_core_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_client2mcif_rd_cdt_lat_fifo_pop_0 (reg2dp_rd_os_cnt)
,.io_eg2ig_axi_vld (eg2ig_axi_vld)
,.io_spt2cvt_req_pd_ready (spt2cvt_req_ready)
,.io_spt2cvt_req_pd_valid (spt2cvt_req_valid)
,.io_spt2cvt_req_pd_bits  (spt2cvt_req_pd)
,.io_mcif2noc_axi_ar_ready  (mcif2noc_axi_ar_arready)
,.io_mcif2noc_axi_ar_valid  (mcif2noc_axi_ar_arvalid)
,.io_mcif2noc_axi_ar_bits_id  (mcif2noc_axi_ar_arid)
,.io_mcif2noc_axi_ar_bits_len  (mcif2noc_axi_ar_arlen)
,.io_mcif2noc_axi_ar_bits_addr  (mcif2noc_axi_ar_araddr)
);


endmodule