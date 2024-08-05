module NV_NVDLA_MCIF_READ_IG_cvt_wrapper (
   nvdla_core_clk //|< i
  ,nvdla_core_rstn //|< i
  ,reg2dp_rd_os_cnt //|< i
  ,eg2ig_axi_vld //|< i
  ,spt2cvt_req_pd //|< i
  ,spt2cvt_req_valid //|< i
  ,spt2cvt_req_ready //|> o
  ,mcif2noc_axi_ar_araddr //|> o
  ,mcif2noc_axi_ar_arid //|> o
  ,mcif2noc_axi_ar_arlen //|> o
  ,mcif2noc_axi_ar_arvalid //|> o
  ,mcif2noc_axi_ar_arready //|< i
  );
//
// NV_NVDLA_MCIF_READ_IG_cvt_ports.v
//
input nvdla_core_clk;
input nvdla_core_rstn;
input [7:0] reg2dp_rd_os_cnt;
output mcif2noc_axi_ar_arvalid;
input mcif2noc_axi_ar_arready;
output [32 -1:0] mcif2noc_axi_ar_araddr;
output [7:0] mcif2noc_axi_ar_arid;
output [3:0] mcif2noc_axi_ar_arlen;
input spt2cvt_req_valid;
output spt2cvt_req_ready;
input [32 +10:0] spt2cvt_req_pd;
input eg2ig_axi_vld;


NV_NVDLA_XXIF_READ_IG_cvt (
.io_nvdla_core_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_reg2dp_rd_os_cnt (reg2dp_rd_os_cnt)
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

