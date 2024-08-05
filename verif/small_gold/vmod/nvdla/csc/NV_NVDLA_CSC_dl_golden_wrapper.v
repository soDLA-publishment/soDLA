module NV_NVDLA_CSC_dl_golden_wrapper (
   nvdla_core_clk 
  ,nvdla_core_rstn 
  ,sg2dl_pvld 
  ,sg2dl_pd 
  ,sc_state 
  ,sg2dl_reuse_rls 
  ,sc2cdma_dat_pending_req 
  ,cdma2sc_dat_updt 
  ,cdma2sc_dat_entries 
  ,cdma2sc_dat_slices 
  ,sc2cdma_dat_updt
  ,sc2cdma_dat_entries
  ,sc2cdma_dat_slices
  ,sc2buf_dat_rd_en
  ,sc2buf_dat_rd_addr
  ,sc2buf_dat_rd_valid 
  ,sc2buf_dat_rd_data 
  ,sc2buf_dat_rd_shift
  ,sc2buf_dat_rd_next1_en
  ,sc2buf_dat_rd_next1_addr
  ,sc2mac_dat_a_pvld
  ,sc2mac_dat_a_mask
  ,sc2mac_dat_a_data0 
  ,sc2mac_dat_a_data1 
  ,sc2mac_dat_a_data2 
  ,sc2mac_dat_a_data3 
  ,sc2mac_dat_a_data4 
  ,sc2mac_dat_a_data5 
  ,sc2mac_dat_a_data6 
  ,sc2mac_dat_a_data7 
  ,sc2mac_dat_a_pd
  ,sc2mac_dat_b_pvld
  ,sc2mac_dat_b_mask //|> 
  ,sc2mac_dat_b_data0 
  ,sc2mac_dat_b_data1 
  ,sc2mac_dat_b_data2 
  ,sc2mac_dat_b_data3 
  ,sc2mac_dat_b_data4 
  ,sc2mac_dat_b_data5 
  ,sc2mac_dat_b_data6 
  ,sc2mac_dat_b_data7 
  ,sc2mac_dat_b_pd
  ,reg2dp_op_en 
  ,reg2dp_conv_mode 
  ,reg2dp_batches 
  ,reg2dp_proc_precision 
  ,reg2dp_datain_format 
  ,reg2dp_skip_data_rls 
  ,reg2dp_datain_channel_ext 
  ,reg2dp_datain_height_ext 
  ,reg2dp_datain_width_ext 
  ,reg2dp_y_extension 
  ,reg2dp_weight_channel_ext 
  ,reg2dp_entries 
  ,reg2dp_dataout_width 
  ,reg2dp_rls_slices 
  ,reg2dp_conv_x_stride_ext 
  ,reg2dp_conv_y_stride_ext 
  ,reg2dp_x_dilation_ext 
  ,reg2dp_y_dilation_ext 
  ,reg2dp_pad_left 
  ,reg2dp_pad_top 
  ,reg2dp_pad_value 
  ,reg2dp_data_bank 
  ,reg2dp_pra_truncate 
  ,slcg_wg_en
  );
  input nvdla_core_clk;
  input nvdla_core_rstn;
  input sg2dl_pvld; /* data valid */
  input [30:0] sg2dl_pd;
  input [1:0] sc_state;
  input sg2dl_reuse_rls;
  input sc2cdma_dat_pending_req;
  input cdma2sc_dat_updt; /* data valid */
  input [15 -1:0] cdma2sc_dat_entries;
  input [13:0] cdma2sc_dat_slices;
  output sc2cdma_dat_updt; /* data valid */
  output [15 -1:0] sc2cdma_dat_entries;
  output [13:0] sc2cdma_dat_slices;
  output sc2buf_dat_rd_en; /* data valid */
  output [14 -1:0] sc2buf_dat_rd_addr;
  input sc2buf_dat_rd_valid; /* data valid */
  input [64 -1:0] sc2buf_dat_rd_data;
  output [7 -1:0] sc2buf_dat_rd_shift;
  output sc2buf_dat_rd_next1_en;
  output [14 -1:0] sc2buf_dat_rd_next1_addr;
  output sc2mac_dat_a_pvld; /* data valid */
  output [8 -1:0] sc2mac_dat_a_mask;
  output [8 -1:0] sc2mac_dat_a_data0; 
  output [8 -1:0] sc2mac_dat_a_data1; 
  output [8 -1:0] sc2mac_dat_a_data2; 
  output [8 -1:0] sc2mac_dat_a_data3; 
  output [8 -1:0] sc2mac_dat_a_data4; 
  output [8 -1:0] sc2mac_dat_a_data5; 
  output [8 -1:0] sc2mac_dat_a_data6; 
  output [8 -1:0] sc2mac_dat_a_data7; 
  output [8:0] sc2mac_dat_a_pd;
  output sc2mac_dat_b_pvld; /* data valid */
  output [8 -1:0] sc2mac_dat_b_mask;
  output [8 -1:0] sc2mac_dat_b_data0; 
  output [8 -1:0] sc2mac_dat_b_data1; 
  output [8 -1:0] sc2mac_dat_b_data2; 
  output [8 -1:0] sc2mac_dat_b_data3; 
  output [8 -1:0] sc2mac_dat_b_data4; 
  output [8 -1:0] sc2mac_dat_b_data5; 
  output [8 -1:0] sc2mac_dat_b_data6; 
  output [8 -1:0] sc2mac_dat_b_data7; 
  output [8:0] sc2mac_dat_b_pd;
  input [0:0] reg2dp_op_en;
  input [0:0] reg2dp_conv_mode;
  input [4:0] reg2dp_batches;
  input [1:0] reg2dp_proc_precision;
  input [0:0] reg2dp_datain_format;
  input [0:0] reg2dp_skip_data_rls;
  input [12:0] reg2dp_datain_channel_ext;
  input [12:0] reg2dp_datain_height_ext;
  input [12:0] reg2dp_datain_width_ext;
  input [1:0] reg2dp_y_extension;
  input [12:0] reg2dp_weight_channel_ext;
  input [13:0] reg2dp_entries;
  input [12:0] reg2dp_dataout_width;
  input [11:0] reg2dp_rls_slices;
  input [2:0] reg2dp_conv_x_stride_ext;
  input [2:0] reg2dp_conv_y_stride_ext;
  input [4:0] reg2dp_x_dilation_ext;
  input [4:0] reg2dp_y_dilation_ext;
  input [4:0] reg2dp_pad_left;
  input [4:0] reg2dp_pad_top;
  input [15:0] reg2dp_pad_value;
  input [4:0] reg2dp_data_bank;
  input [1:0] reg2dp_pra_truncate;
  output slcg_wg_en;


NV_NVDLA_CSC_dl u_csc_dl(
.nvdla_core_clk(nvdla_core_clk)
,.nvdla_core_rstn(nvdla_core_rstn)
,.sg2dl_pvld(sg2dl_pvld)
,.sg2dl_pd(sg2dl_pd)
,.sc_state(sc_state)
,.sg2dl_reuse_rls(sg2dl_reuse_rls)
,.sc2cdma_dat_pending_req(sc2cdma_dat_pending_req)
,.cdma2sc_dat_updt(cdma2sc_dat_updt)
,.cdma2sc_dat_entries(cdma2sc_dat_entries)
,.cdma2sc_dat_slices(cdma2sc_dat_slices)
,.sc2cdma_dat_updt(sc2cdma_dat_updt)
,.sc2cdma_dat_entries(sc2cdma_dat_entries)
,.sc2cdma_dat_slices(sc2cdma_dat_slices)
,.sc2buf_dat_rd_en(sc2buf_dat_rd_en)
,.sc2buf_dat_rd_addr(sc2buf_dat_rd_addr)
,.sc2buf_dat_rd_valid(sc2buf_dat_rd_valid)
,.sc2buf_dat_rd_data(sc2buf_dat_rd_data)
,.sc2buf_dat_rd_shift(sc2buf_dat_rd_shift)
,.sc2buf_dat_rd_next1_en(sc2buf_dat_rd_next1_en)
,.sc2buf_dat_rd_next1_addr(sc2buf_dat_rd_next1_addr)
,.sc2mac_dat_a_pvld(sc2mac_dat_a_pvld)
,.sc2mac_dat_a_mask(sc2mac_dat_a_mask)
,.sc2mac_dat_a_data0(sc2mac_dat_a_data0)
,.sc2mac_dat_a_data1(sc2mac_dat_a_data1)
,.sc2mac_dat_a_data2(sc2mac_dat_a_data2)
,.sc2mac_dat_a_data3(sc2mac_dat_a_data3)
,.sc2mac_dat_a_data4(sc2mac_dat_a_data4)
,.sc2mac_dat_a_data5(sc2mac_dat_a_data5)
,.sc2mac_dat_a_data6(sc2mac_dat_a_data6)
,.sc2mac_dat_a_data7(sc2mac_dat_a_data7)
,.sc2mac_dat_a_pd(sc2mac_dat_a_pd)
,.sc2mac_dat_b_pvld(sc2mac_dat_b_pvld)
,.sc2mac_dat_b_mask(sc2mac_dat_b_mask)
,.sc2mac_dat_b_data0(sc2mac_dat_b_data0)
,.sc2mac_dat_b_data1(sc2mac_dat_b_data1)
,.sc2mac_dat_b_data2(sc2mac_dat_b_data2)
,.sc2mac_dat_b_data3(sc2mac_dat_b_data3)
,.sc2mac_dat_b_data4(sc2mac_dat_b_data4)
,.sc2mac_dat_b_data5(sc2mac_dat_b_data5)
,.sc2mac_dat_b_data6(sc2mac_dat_b_data6)
,.sc2mac_dat_b_data7(sc2mac_dat_b_data7)
,.sc2mac_dat_b_pd(sc2mac_dat_b_pd)
,.nvdla_core_ng_clk(nvdla_core_clk)
,.nvdla_wg_clk(nvdla_core_clk)
,.reg2dp_op_en(reg2dp_op_en) 
,.reg2dp_conv_mode(reg2dp_conv_mode)
,.reg2dp_batches(reg2dp_batches)
,.reg2dp_proc_precision(reg2dp_proc_precision)
,.reg2dp_datain_format(reg2dp_datain_format)
,.reg2dp_skip_data_rls(reg2dp_skip_data_rls)
,.reg2dp_datain_channel_ext(reg2dp_datain_channel_ext)
,.reg2dp_datain_height_ext(reg2dp_datain_height_ext)
,.reg2dp_datain_width_ext(reg2dp_datain_width_ext)
,.reg2dp_y_extension(reg2dp_y_extension)
,.reg2dp_weight_channel_ext(reg2dp_weight_channel_ext) 
,.reg2dp_entries(reg2dp_entries)
,.reg2dp_dataout_width(reg2dp_dataout_width) 
,.reg2dp_rls_slices(reg2dp_rls_slices)
,.reg2dp_conv_x_stride_ext(reg2dp_conv_x_stride_ext) 
,.reg2dp_conv_y_stride_ext(reg2dp_conv_y_stride_ext)
,.reg2dp_x_dilation_ext(reg2dp_x_dilation_ext)
,.reg2dp_y_dilation_ext(reg2dp_y_dilation_ext)
,.reg2dp_pad_left(reg2dp_pad_left) 
,.reg2dp_pad_top(reg2dp_pad_top)
,.reg2dp_pad_value(reg2dp_pad_value) 
,.reg2dp_data_bank(reg2dp_data_bank)
,.reg2dp_pra_truncate(reg2dp_pra_truncate) 
,.slcg_wg_en(slcg_wg_en)
);

endmodule