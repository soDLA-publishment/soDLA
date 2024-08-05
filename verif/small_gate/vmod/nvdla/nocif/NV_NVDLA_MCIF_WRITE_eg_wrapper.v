module NV_NVDLA_MCIF_WRITE_eg_wrapper (
   nvdla_core_clk
  ,nvdla_core_rstn
  ,cq_rd0_pvld 
  ,cq_rd0_pd   
  ,cq_rd0_prdy 
  ,cq_rd1_pvld 
  ,cq_rd1_pd   
  ,cq_rd1_prdy 
  ,cq_rd2_pvld 
  ,cq_rd2_pd   
  ,cq_rd2_prdy 
  ,cq_rd3_pvld 
  ,cq_rd3_pd   
  ,cq_rd3_prdy 
  ,cq_rd4_pvld 
  ,cq_rd4_pd   
  ,cq_rd4_prdy 
  ,mcif2sdp_wr_rsp_complete
  ,mcif2pdp_wr_rsp_complete
  ,mcif2cdp_wr_rsp_complete
  ,eg2ig_axi_len
  ,eg2ig_axi_vld
  ,noc2mcif_axi_b_bid
  ,noc2mcif_axi_b_bvalid
  ,noc2mcif_axi_b_bready
);
input nvdla_core_clk;
input nvdla_core_rstn;
input cq_rd0_pvld;
output cq_rd0_prdy;
input [2:0] cq_rd0_pd;

input cq_rd1_pvld;
output cq_rd1_prdy;
input [2:0] cq_rd1_pd;

input cq_rd2_pvld;
output cq_rd2_prdy;
input [2:0] cq_rd2_pd;

input cq_rd3_pvld;
output cq_rd3_prdy;
input [2:0] cq_rd3_pd;

input cq_rd4_pvld;
output cq_rd4_prdy;
input [2:0] cq_rd4_pd;

//| eperl: generated_end (DO NOT EDIT ABOVE)
output [1:0] eg2ig_axi_len;
output eg2ig_axi_vld;
input noc2mcif_axi_b_bvalid;
output noc2mcif_axi_b_bready;
input [7:0] noc2mcif_axi_b_bid;

output mcif2sdp_wr_rsp_complete;
output mcif2pdp_wr_rsp_complete;
output mcif2cdp_wr_rsp_complete;


NV_NVDLA_MCIF_WRITE_eg (
.io_nvdla_core_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_cq_rd_pd_0_ready (cq_rd0_prdy)
,.io_cq_rd_pd_0_valid (cq_rd0_pvld)
,.io_cq_rd_pd_0_bits (cq_rd0_pd)
,.io_cq_rd_pd_1_ready (cq_rd1_prdy)
,.io_cq_rd_pd_1_valid (cq_rd1_pvld)
,.io_cq_rd_pd_1_bits (cq_rd1_pd)
,.io_cq_rd_pd_2_ready (cq_rd2_prdy)
,.io_cq_rd_pd_2_valid (cq_rd2_pvld)
,.io_cq_rd_pd_2_bits (cq_rd2_pd)
,.io_cq_rd_pd_3_ready (cq_rd3_prdy)
,.io_cq_rd_pd_3_valid (cq_rd3_pvld)
,.io_cq_rd_pd_3_bits (cq_rd3_pd)
,.io_cq_rd_pd_4_ready (cq_rd4_prdy)
,.io_cq_rd_pd_4_valid (cq_rd4_pvld)
,.io_cq_rd_pd_4_bits (cq_rd4_pd)
,.io_noc2mcif_axi_b_ready (noc2mcif_axi_b_bready)
,.io_noc2mcif_axi_b_valid (noc2mcif_axi_b_bvalid)
,.io_noc2mcif_axi_b_bits_id (noc2mcif_axi_b_bid)
,.io_eg2ig_axi_len_valid (eg2ig_axi_vld)
,.io_eg2ig_axi_len_bits (eg2ig_axi_len)
,.io_mcif2client_wr_rsp_complete_0(mcif2sdp_wr_rsp_complete)
,.io_mcif2client_wr_rsp_complete_1(mcif2pdp_wr_rsp_complete)
,.io_mcif2client_wr_rsp_complete_2(mcif2cdp_wr_rsp_complete)
);


endmodule