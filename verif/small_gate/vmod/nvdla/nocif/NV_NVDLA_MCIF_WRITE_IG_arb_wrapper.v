// module NV_NVDLA_MCIF_WRITE_IG_arb_wrapper (
//    nvdla_core_clk //|< i
//   ,nvdla_core_rstn //|< i
//   ,pwrbus_ram_pd
//   ,arb2spt_cmd_pd //|> o
//   ,arb2spt_cmd_valid //|> o
//   ,arb2spt_cmd_ready //|< i
//   ,arb2spt_dat_pd //|> o
//   ,arb2spt_dat_valid //|> o
//   ,arb2spt_dat_ready //|< i
//   ,bpt2arb_cmd0_pd    
//   ,bpt2arb_cmd0_valid 
//   ,bpt2arb_cmd0_ready 
//   ,bpt2arb_dat0_pd    
//   ,bpt2arb_dat0_valid 
//   ,bpt2arb_dat0_ready 
//   ,reg2dp_wr_weight0  
//   ,bpt2arb_cmd1_pd    
//   ,bpt2arb_cmd1_valid 
//   ,bpt2arb_cmd1_ready 
//   ,bpt2arb_dat1_pd    
//   ,bpt2arb_dat1_valid 
//   ,bpt2arb_dat1_ready 
//   ,reg2dp_wr_weight1  
//   ,bpt2arb_cmd2_pd    
//   ,bpt2arb_cmd2_valid 
//   ,bpt2arb_cmd2_ready 
//   ,bpt2arb_dat2_pd    
//   ,bpt2arb_dat2_valid 
//   ,bpt2arb_dat2_ready 
//   ,reg2dp_wr_weight2  

// //| eperl: generated_end (DO NOT EDIT ABOVE)
// );

// input nvdla_core_clk;
// input nvdla_core_rstn;
// input [31:0] pwrbus_ram_pd;
// output arb2spt_cmd_valid;
// input arb2spt_cmd_ready;
// output [32 +13 -1:0] arb2spt_cmd_pd;
// output arb2spt_dat_valid;
// input arb2spt_dat_ready;
// output [66 -2:0] arb2spt_dat_pd;

// input bpt2arb_cmd0_valid;
// output bpt2arb_cmd0_ready;
// input [32 +13 -1:0] bpt2arb_cmd0_pd;
// input bpt2arb_dat0_valid;
// output bpt2arb_dat0_ready;
// input [66 -2:0] bpt2arb_dat0_pd;
// input [7:0] reg2dp_wr_weight0;

// input bpt2arb_cmd1_valid;
// output bpt2arb_cmd1_ready;
// input [32 +13 -1:0] bpt2arb_cmd1_pd;
// input bpt2arb_dat1_valid;
// output bpt2arb_dat1_ready;
// input [66 -2:0] bpt2arb_dat1_pd;
// input [7:0] reg2dp_wr_weight1;

// input bpt2arb_cmd2_valid;
// output bpt2arb_cmd2_ready;
// input [32 +13 -1:0] bpt2arb_cmd2_pd;
// input bpt2arb_dat2_valid;
// output bpt2arb_dat2_ready;
// input [66 -2:0] bpt2arb_dat2_pd;
// input [7:0] reg2dp_wr_weight2;

// NV_NVDLA_MCIF_WRITE_IG_arb (
// .reset (~nvdla_core_rstn)
// ,.io_nvdla_core_clk  (nvdla_core_clk)
// ,.io_spt2cvt_cmd_pd_ready  (spt2cvt_cmd_ready)
// ,.io_spt2cvt_cmd_pd_valid  (spt2cvt_cmd_valid)
// ,.io_spt2cvt_cmd_pd_bits   (spt2cvt_cmd_pd)
// ,.io_spt2cvt_dat_pd_ready  (spt2cvt_dat_ready)
// ,.io_spt2cvt_dat_pd_valid  (spt2cvt_dat_valid)
// ,.io_spt2cvt_dat_pd_bits   (spt2cvt_dat_pd)
// ,.io_cq_wr_pd_ready        (cq_wr_prdy)
// ,.io_cq_wr_pd_valid        (cq_wr_pvld)
// ,.io_cq_wr_pd_bits         (cq_wr_pd)
// ,.io_cq_wr_thread_id       (cq_wr_thread_id)
// ,.io_mcif2noc_axi_aw_ready  (mcif2noc_axi_aw_awready)
// ,.io_mcif2noc_axi_aw_valid  (mcif2noc_axi_aw_awvalid)
// ,.io_mcif2noc_axi_aw_bits_id  (mcif2noc_axi_aw_awid)
// ,.io_mcif2noc_axi_aw_bits_len (mcif2noc_axi_aw_awlen)
// ,.io_mcif2noc_axi_aw_bits_addr(mcif2noc_axi_aw_awaddr)
// ,.io_mcif2noc_axi_w_ready  (mcif2noc_axi_w_wready)
// ,.io_mcif2noc_axi_w_valid  (mcif2noc_axi_w_wvalid)
// ,.io_mcif2noc_axi_w_bits_data  (mcif2noc_axi_w_wdata)
// ,.io_mcif2noc_axi_w_bits_strb  (mcif2noc_axi_w_wstrb)
// ,.io_mcif2noc_axi_w_bits_last  (mcif2noc_axi_w_wlast)
// ,.io_eg2ig_axi_len_valid  (eg2ig_axi_vld)
// ,.io_eg2ig_axi_len_bits   (eg2ig_axi_len)
// ,.io_reg2dp_wr_os_cnt    (reg2dp_wr_os_cnt)
// );


// endmodule
