module NV_NVDLA_MCIF_READ_eg_wrapper (
   nvdla_core_clk //|< i
  ,nvdla_core_rstn //|< i
  ,pwrbus_ram_pd //|< i
  ,eg2ig_axi_vld //|> o
  ,mcif2cdma_dat_rd_rsp_pd    
  ,mcif2cdma_dat_rd_rsp_valid 
  ,mcif2cdma_dat_rd_rsp_ready 
  ,mcif2cdma_wt_rd_rsp_pd    
  ,mcif2cdma_wt_rd_rsp_valid 
  ,mcif2cdma_wt_rd_rsp_ready 
  ,mcif2sdp_rd_rsp_pd    
  ,mcif2sdp_rd_rsp_valid 
  ,mcif2sdp_rd_rsp_ready 
  ,mcif2sdp_b_rd_rsp_pd    
  ,mcif2sdp_b_rd_rsp_valid 
  ,mcif2sdp_b_rd_rsp_ready 
  ,mcif2sdp_n_rd_rsp_pd    
  ,mcif2sdp_n_rd_rsp_valid 
  ,mcif2sdp_n_rd_rsp_ready 
  ,mcif2pdp_rd_rsp_pd    
  ,mcif2pdp_rd_rsp_valid 
  ,mcif2pdp_rd_rsp_ready 
  ,mcif2cdp_rd_rsp_pd    
  ,mcif2cdp_rd_rsp_valid 
  ,mcif2cdp_rd_rsp_ready 

//| eperl: generated_end (DO NOT EDIT ABOVE)
  ,noc2mcif_axi_r_rdata //|< i
  ,noc2mcif_axi_r_rid //|< i
  ,noc2mcif_axi_r_rlast //|< i
  ,noc2mcif_axi_r_rvalid //|< i
  ,noc2mcif_axi_r_rready //|> o
  );
input nvdla_core_clk;
input nvdla_core_rstn;
input [31:0] pwrbus_ram_pd;
output eg2ig_axi_vld;

output mcif2cdma_dat_rd_rsp_valid;
input mcif2cdma_dat_rd_rsp_ready;
output [65 -1:0] mcif2cdma_dat_rd_rsp_pd;

output mcif2cdma_wt_rd_rsp_valid;
input mcif2cdma_wt_rd_rsp_ready;
output [65 -1:0] mcif2cdma_wt_rd_rsp_pd;

output mcif2sdp_rd_rsp_valid;
input mcif2sdp_rd_rsp_ready;
output [65 -1:0] mcif2sdp_rd_rsp_pd;

output mcif2sdp_b_rd_rsp_valid;
input mcif2sdp_b_rd_rsp_ready;
output [65 -1:0] mcif2sdp_b_rd_rsp_pd;

output mcif2sdp_n_rd_rsp_valid;
input mcif2sdp_n_rd_rsp_ready;
output [65 -1:0] mcif2sdp_n_rd_rsp_pd;

output mcif2pdp_rd_rsp_valid;
input mcif2pdp_rd_rsp_ready;
output [65 -1:0] mcif2pdp_rd_rsp_pd;

output mcif2cdp_rd_rsp_valid;
input mcif2cdp_rd_rsp_ready;
output [65 -1:0] mcif2cdp_rd_rsp_pd;

//| eperl: generated_end (DO NOT EDIT ABOVE)
input noc2mcif_axi_r_rvalid;
output noc2mcif_axi_r_rready;
input [7:0] noc2mcif_axi_r_rid;
input noc2mcif_axi_r_rlast;
input [64 -1:0] noc2mcif_axi_r_rdata;

NV_NVDLA_MCIF_READ_eg (
.io_nvdla_core_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_mcif2client_rd_rsp_pd_0_ready (mcif2cdma_dat_rd_rsp_ready)
,.io_mcif2client_rd_rsp_pd_0_valid (mcif2cdma_dat_rd_rsp_valid)
,.io_mcif2client_rd_rsp_pd_0_bits (mcif2cdma_dat_rd_rsp_pd)
,.io_mcif2client_rd_rsp_pd_1_ready (mcif2cdma_wt_rd_rsp_ready)
,.io_mcif2client_rd_rsp_pd_1_valid (mcif2cdma_wt_rd_rsp_valid)
,.io_mcif2client_rd_rsp_pd_1_bits (mcif2cdma_wt_rd_rsp_pd)
,.io_mcif2client_rd_rsp_pd_2_ready (mcif2sdp_rd_rsp_ready)
,.io_mcif2client_rd_rsp_pd_2_valid (mcif2sdp_rd_rsp_valid)
,.io_mcif2client_rd_rsp_pd_2_bits (mcif2sdp_rd_rsp_pd)
,.io_mcif2client_rd_rsp_pd_3_ready (mcif2sdp_p_rd_rsp_ready)
,.io_mcif2client_rd_rsp_pd_3_valid (mcif2sdp_p_rd_rsp_valid)
,.io_mcif2client_rd_rsp_pd_3_bits (mcif2sdp_p_rd_rsp_pd)
,.io_mcif2client_rd_rsp_pd_4_ready (mcif2sdp_n_rd_rsp_ready)
,.io_mcif2client_rd_rsp_pd_4_valid (mcif2sdp_n_rd_rsp_valid)
,.io_mcif2client_rd_rsp_pd_4_bits (mcif2sdp_n_rd_rsp_pd)
,.io_mcif2client_rd_rsp_pd_5_ready (mcif2pdp_rd_rsp_ready)
,.io_mcif2client_rd_rsp_pd_5_valid (mcif2pdp_rd_rsp_valid)
,.io_mcif2client_rd_rsp_pd_5_bits (mcif2pdp_rd_rsp_pd)
,.io_mcif2client_rd_rsp_pd_6_ready (mcif2cdp_rd_rsp_ready)
,.io_mcif2client_rd_rsp_pd_6_valid (mcif2cdp_rd_rsp_valid)
,.io_mcif2client_rd_rsp_pd_6_bits (mcif2cdp_rd_rsp_pd)
,.io_noc2mcif_axi_r_ready (noc2mcif_axi_r_rready)
,.io_noc2mcif_axi_r_valid (noc2mcif_axi_r_rvalid)
,.io_noc2mcif_axi_r_bits_id (noc2mcif_axi_r_rid)
,.io_noc2mcif_axi_r_bits_last (noc2mcif_axi_r_rlast)
,.io_noc2mcif_axi_r_bits_data (noc2mcif_axi_r_rdata)
,.io_eg2ig_axi_vld (eg2ig_axi_vld)
);


endmodule