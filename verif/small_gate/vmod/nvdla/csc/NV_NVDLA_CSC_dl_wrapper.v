module NV_NVDLA_CSC_dl_wrapper (
   nvdla_core_clk //|< i
  ,nvdla_core_rstn //|< i
  ,sg2dl_pvld //|< i
  ,sg2dl_pd //|< i
  ,sc_state //|< i
  ,sg2dl_reuse_rls //|< i
  ,sc2cdma_dat_pending_req //|< i
  ,cdma2sc_dat_updt //|< i
  ,cdma2sc_dat_entries //|< i
  ,cdma2sc_dat_slices //|< i
  ,sc2cdma_dat_updt //|> o
  ,sc2cdma_dat_entries //|> o
  ,sc2cdma_dat_slices //|> o
  ,sc2buf_dat_rd_en //|> o
  ,sc2buf_dat_rd_addr //|> o
  ,sc2buf_dat_rd_valid //|< i
  ,sc2buf_dat_rd_data //|< i
  ,sc2buf_dat_rd_shift //|> o
  ,sc2buf_dat_rd_next1_en //|> o
  ,sc2buf_dat_rd_next1_addr //|> o
  ,sc2mac_dat_a_pvld //|> o
  ,sc2mac_dat_a_mask //|> o
  ,sc2mac_dat_a_data0 //|> o 
  ,sc2mac_dat_a_data1 //|> o 
  ,sc2mac_dat_a_data2 //|> o 
  ,sc2mac_dat_a_data3 //|> o 
  ,sc2mac_dat_a_data4 //|> o 
  ,sc2mac_dat_a_data5 //|> o 
  ,sc2mac_dat_a_data6 //|> o 
  ,sc2mac_dat_a_data7 //|> o 
  ,sc2mac_dat_a_pd //|> o
  ,sc2mac_dat_b_pvld //|> o
  ,sc2mac_dat_b_mask //|> 
  ,sc2mac_dat_b_data0 //|> o 
  ,sc2mac_dat_b_data1 //|> o 
  ,sc2mac_dat_b_data2 //|> o 
  ,sc2mac_dat_b_data3 //|> o 
  ,sc2mac_dat_b_data4 //|> o 
  ,sc2mac_dat_b_data5 //|> o 
  ,sc2mac_dat_b_data6 //|> o 
  ,sc2mac_dat_b_data7 //|> o 
  ,sc2mac_dat_b_pd //|> o
  ,reg2dp_op_en //|< i
  ,reg2dp_conv_mode //|< i
  ,reg2dp_batches //|< i
  ,reg2dp_proc_precision //|< i
  ,reg2dp_datain_format //|< i
  ,reg2dp_skip_data_rls //|< i
  ,reg2dp_datain_channel_ext //|< i
  ,reg2dp_datain_height_ext //|< i
  ,reg2dp_datain_width_ext //|< i
  ,reg2dp_y_extension //|< i
  ,reg2dp_weight_channel_ext //|< i
  ,reg2dp_entries //|< i
  ,reg2dp_dataout_width //|< i
  ,reg2dp_rls_slices //|< i
  ,reg2dp_conv_x_stride_ext //|< i
  ,reg2dp_conv_y_stride_ext //|< i
  ,reg2dp_x_dilation_ext //|< i
  ,reg2dp_y_dilation_ext //|< i
  ,reg2dp_pad_left //|< i
  ,reg2dp_pad_top //|< i
  ,reg2dp_pad_value //|< i
  ,reg2dp_data_bank //|< i
  ,reg2dp_pra_truncate //|< i
  ,slcg_wg_en //|> o
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
.io_nvdla_core_clk(nvdla_core_clk)
,.io_nvdla_core_ng_clk(nvdla_core_clk)
,.reset(~nvdla_core_rstn)
,.io_sc_state(sc_state)
,.io_sg2dl_pd_valid(sg2dl_pvld)
,.io_sg2dl_pd_bits(sg2dl_pd)
,.io_sg2dl_reuse_rls(sg2dl_reuse_rls)
,.io_cdma2sc_dat_updt_valid(cdma2sc_dat_updt)
,.io_cdma2sc_dat_updt_bits_entries(cdma2sc_dat_entries)
,.io_cdma2sc_dat_updt_bits_slices(cdma2sc_dat_slices)
,.io_sc2cdma_dat_pending_req(sc2cdma_dat_pending_req)
,.io_sc2cdma_dat_updt_valid(sc2cdma_dat_updt)
,.io_sc2cdma_dat_updt_bits_entries(sc2cdma_dat_entries)
,.io_sc2cdma_dat_updt_bits_slices(sc2cdma_dat_slices)
,.io_sc2buf_dat_rd_addr_valid(sc2buf_dat_rd_en)
,.io_sc2buf_dat_rd_addr_bits(sc2buf_dat_rd_addr)
,.io_sc2buf_dat_rd_data_valid(sc2buf_dat_rd_valid)
,.io_sc2buf_dat_rd_data_bits(sc2buf_dat_rd_data)
,.io_sc2mac_dat_a_valid(sc2mac_dat_a_pvld)
,.io_sc2mac_dat_a_bits_mask_0(sc2mac_dat_a_mask[0])
,.io_sc2mac_dat_a_bits_mask_1(sc2mac_dat_a_mask[1])
,.io_sc2mac_dat_a_bits_mask_2(sc2mac_dat_a_mask[2])
,.io_sc2mac_dat_a_bits_mask_3(sc2mac_dat_a_mask[3])
,.io_sc2mac_dat_a_bits_mask_4(sc2mac_dat_a_mask[4])
,.io_sc2mac_dat_a_bits_mask_5(sc2mac_dat_a_mask[5])
,.io_sc2mac_dat_a_bits_mask_6(sc2mac_dat_a_mask[6])
,.io_sc2mac_dat_a_bits_mask_7(sc2mac_dat_a_mask[7])
,.io_sc2mac_dat_a_bits_data_0(sc2mac_dat_a_data0)
,.io_sc2mac_dat_a_bits_data_1(sc2mac_dat_a_data1)
,.io_sc2mac_dat_a_bits_data_2(sc2mac_dat_a_data2)
,.io_sc2mac_dat_a_bits_data_3(sc2mac_dat_a_data3)
,.io_sc2mac_dat_a_bits_data_4(sc2mac_dat_a_data4)
,.io_sc2mac_dat_a_bits_data_5(sc2mac_dat_a_data5)
,.io_sc2mac_dat_a_bits_data_6(sc2mac_dat_a_data6)
,.io_sc2mac_dat_a_bits_data_7(sc2mac_dat_a_data7)
,.io_sc2mac_dat_a_bits_pd(sc2mac_dat_a_pd)
,.io_sc2mac_dat_b_valid(sc2mac_dat_b_pvld)
,.io_sc2mac_dat_b_bits_mask_0(sc2mac_dat_b_mask[0])
,.io_sc2mac_dat_b_bits_mask_1(sc2mac_dat_b_mask[1])
,.io_sc2mac_dat_b_bits_mask_2(sc2mac_dat_b_mask[2])
,.io_sc2mac_dat_b_bits_mask_3(sc2mac_dat_b_mask[3])
,.io_sc2mac_dat_b_bits_mask_4(sc2mac_dat_b_mask[4])
,.io_sc2mac_dat_b_bits_mask_5(sc2mac_dat_b_mask[5])
,.io_sc2mac_dat_b_bits_mask_6(sc2mac_dat_b_mask[6])
,.io_sc2mac_dat_b_bits_mask_7(sc2mac_dat_b_mask[7])
,.io_sc2mac_dat_b_bits_data_0(sc2mac_dat_b_data0)
,.io_sc2mac_dat_b_bits_data_1(sc2mac_dat_b_data1)
,.io_sc2mac_dat_b_bits_data_2(sc2mac_dat_b_data2)
,.io_sc2mac_dat_b_bits_data_3(sc2mac_dat_b_data3)
,.io_sc2mac_dat_b_bits_data_4(sc2mac_dat_b_data4)
,.io_sc2mac_dat_b_bits_data_5(sc2mac_dat_b_data5)
,.io_sc2mac_dat_b_bits_data_6(sc2mac_dat_b_data6)
,.io_sc2mac_dat_b_bits_data_7(sc2mac_dat_b_data7)
,.io_sc2mac_dat_b_bits_pd(sc2mac_dat_b_pd)
,.io_reg2dp_op_en(reg2dp_op_en)
,.io_reg2dp_conv_mode(reg2dp_conv_mode)
,.io_reg2dp_batches(reg2dp_batches)
,.io_reg2dp_proc_precision(reg2dp_proc_precision)
,.io_reg2dp_datain_format(reg2dp_datain_format)
,.io_reg2dp_skip_data_rls(reg2dp_skip_data_rls)
,.io_reg2dp_datain_channel_ext(reg2dp_datain_channel_ext)
,.io_reg2dp_datain_height_ext(reg2dp_datain_height_ext)
,.io_reg2dp_datain_width_ext(reg2dp_datain_width_ext)
,.io_reg2dp_y_extension(reg2dp_y_extension)
,.io_reg2dp_weight_channel_ext(reg2dp_weight_channel_ext)
,.io_reg2dp_entries(reg2dp_entries)
,.io_reg2dp_dataout_width(reg2dp_dataout_width)
,.io_reg2dp_rls_slices(reg2dp_rls_slices)
,.io_reg2dp_conv_x_stride_ext(reg2dp_conv_x_stride_ext)
,.io_reg2dp_conv_y_stride_ext(reg2dp_conv_y_stride_ext)
,.io_reg2dp_x_dilation_ext(reg2dp_x_dilation_ext)
,.io_reg2dp_y_dilation_ext(reg2dp_y_dilation_ext)
,.io_reg2dp_pad_left(reg2dp_pad_left)
,.io_reg2dp_pad_top(reg2dp_pad_top)
,.io_reg2dp_pad_value(reg2dp_pad_value)
,.io_reg2dp_data_bank(reg2dp_data_bank)
,.io_reg2dp_pra_truncate(reg2dp_pra_truncate)
,.io_slcg_wg_en(slcg_wg_en)
);

endmodule