module NV_NVDLA_MCIF_WRITE_IG_cvt_wrapper (
   nvdla_core_clk //|< i
  ,nvdla_core_rstn //|< i
  ,reg2dp_wr_os_cnt //|< i
  ,cq_wr_pd //|> o
  ,cq_wr_pvld //|> o
  ,cq_wr_prdy //|< i
  ,cq_wr_thread_id //|> o
  ,eg2ig_axi_len //|< i
  ,eg2ig_axi_vld //|< i
  ,spt2cvt_cmd_pd //|< i
  ,spt2cvt_cmd_valid //|< i
  ,spt2cvt_cmd_ready //|> o
  ,spt2cvt_dat_pd //|< i
  ,spt2cvt_dat_valid //|< i
  ,spt2cvt_dat_ready //|> o
  ,mcif2noc_axi_aw_awaddr //|> o
  ,mcif2noc_axi_aw_awid //|> o
  ,mcif2noc_axi_aw_awlen //|> o
  ,mcif2noc_axi_aw_awvalid //|> o
  ,mcif2noc_axi_aw_awready //|< i
  ,mcif2noc_axi_w_wdata //|> o
  ,mcif2noc_axi_w_wlast //|> o
  ,mcif2noc_axi_w_wstrb //|> o
  ,mcif2noc_axi_w_wvalid //|> o
  ,mcif2noc_axi_w_wready //|< i
  );
input nvdla_core_clk;
input nvdla_core_rstn;
input spt2cvt_cmd_valid;
output spt2cvt_cmd_ready;
input [32 +13 -1:0] spt2cvt_cmd_pd;
input spt2cvt_dat_valid;
output spt2cvt_dat_ready;
input [64 +1 -1:0] spt2cvt_dat_pd;
output cq_wr_pvld;
input cq_wr_prdy;
output [2:0] cq_wr_pd;
output [2:0] cq_wr_thread_id;
output mcif2noc_axi_aw_awvalid;
input mcif2noc_axi_aw_awready;
output [32 -1:0] mcif2noc_axi_aw_awaddr;
output [7:0] mcif2noc_axi_aw_awid;
output [3:0] mcif2noc_axi_aw_awlen;
output mcif2noc_axi_w_wvalid;
input mcif2noc_axi_w_wready;
output [64 -1:0] mcif2noc_axi_w_wdata;
output [8 -1:0] mcif2noc_axi_w_wstrb;
output mcif2noc_axi_w_wlast;
input [1:0] eg2ig_axi_len;
input eg2ig_axi_vld;
input [7:0] reg2dp_wr_os_cnt;

NV_NVDLA_MCIF_WRITE_IG_cvt (
.reset (~nvdla_core_rstn)
,.io_nvdla_core_clk  (nvdla_core_clk)
,.io_spt2cvt_cmd_pd_ready  (spt2cvt_cmd_ready)
,.io_spt2cvt_cmd_pd_valid  (spt2cvt_cmd_valid)
,.io_spt2cvt_cmd_pd_bits   (spt2cvt_cmd_pd)
,.io_spt2cvt_dat_pd_ready  (spt2cvt_dat_ready)
,.io_spt2cvt_dat_pd_valid  (spt2cvt_dat_valid)
,.io_spt2cvt_dat_pd_bits   (spt2cvt_dat_pd)
,.io_cq_wr_pd_ready        (cq_wr_prdy)
,.io_cq_wr_pd_valid        (cq_wr_pvld)
,.io_cq_wr_pd_bits         (cq_wr_pd)
,.io_cq_wr_thread_id       (cq_wr_thread_id)
,.io_mcif2noc_axi_aw_ready  (mcif2noc_axi_aw_awready)
,.io_mcif2noc_axi_aw_valid  (mcif2noc_axi_aw_awvalid)
,.io_mcif2noc_axi_aw_bits_id  (mcif2noc_axi_aw_awid)
,.io_mcif2noc_axi_aw_bits_len (mcif2noc_axi_aw_awlen)
,.io_mcif2noc_axi_aw_bits_addr(mcif2noc_axi_aw_awaddr)
,.io_mcif2noc_axi_w_ready  (mcif2noc_axi_w_wready)
,.io_mcif2noc_axi_w_valid  (mcif2noc_axi_w_wvalid)
,.io_mcif2noc_axi_w_bits_data  (mcif2noc_axi_w_wdata)
,.io_mcif2noc_axi_w_bits_strb  (mcif2noc_axi_w_wstrb)
,.io_mcif2noc_axi_w_bits_last  (mcif2noc_axi_w_wlast)
,.io_eg2ig_axi_len_valid  (eg2ig_axi_vld)
,.io_eg2ig_axi_len_bits   (eg2ig_axi_len)
,.io_reg2dp_wr_os_cnt    (reg2dp_wr_os_cnt)
);


endmodule