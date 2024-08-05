module NV_NVDLA_CSC_sg_wrapper (
   nvdla_core_clk 
  ,nvdla_core_rstn 
  ,accu2sc_credit_size 
  ,accu2sc_credit_vld 
  ,cdma2sc_dat_entries  
  ,cdma2sc_dat_pending_ack 
  ,cdma2sc_dat_slices 
  ,cdma2sc_dat_updt 
  ,cdma2sc_wmb_entries  
  ,cdma2sc_wt_entries  
  ,cdma2sc_wt_kernels 
  ,cdma2sc_wt_pending_ack 
  ,cdma2sc_wt_updt 
  ,pwrbus_ram_pd 
  ,reg2dp_atomics 
  ,reg2dp_batches 
  ,reg2dp_conv_mode 
  ,reg2dp_data_bank 
  ,reg2dp_data_reuse 
  ,reg2dp_datain_format 
  ,reg2dp_datain_height_ext 
  ,reg2dp_dataout_height 
  ,reg2dp_dataout_width 
  ,reg2dp_op_en 
  ,reg2dp_proc_precision 
  ,reg2dp_rls_slices 
  ,reg2dp_skip_data_rls 
  ,reg2dp_skip_weight_rls 
  ,reg2dp_weight_bank 
  ,reg2dp_weight_channel_ext 
  ,reg2dp_weight_height_ext 
  ,reg2dp_weight_kernel 
  ,reg2dp_weight_reuse 
  ,reg2dp_weight_width_ext 
  ,reg2dp_y_extension 
  ,dp2reg_done //|> o
  ,sc2cdma_dat_pending_req //|> o
  ,sc2cdma_wt_pending_req //|> o
  ,sc_state //|> o
  ,sg2dl_pd //|> o
  ,sg2dl_pvld //|> o
  ,sg2dl_reuse_rls //|> o
  ,sg2wl_pd //|> o
  ,sg2wl_pvld //|> o
  ,sg2wl_reuse_rls //|> o
  );
input nvdla_core_clk;
input nvdla_core_rstn; 
input [31:0] pwrbus_ram_pd;
output dp2reg_done;
input cdma2sc_dat_updt; /* data valid */
input [15 -1:0] cdma2sc_dat_entries;
input [13:0] cdma2sc_dat_slices;
input cdma2sc_wt_updt; /* data valid */
input [13:0] cdma2sc_wt_kernels;
input [15 -1:0] cdma2sc_wt_entries;
input [8:0] cdma2sc_wmb_entries;
output sg2dl_pvld; /* data valid */
output [30:0] sg2dl_pd;
output sg2wl_pvld; /* data valid */
output [17:0] sg2wl_pd;
input accu2sc_credit_vld; /* data valid */
input [2:0] accu2sc_credit_size;
output [1:0] sc_state;
output sc2cdma_dat_pending_req; //send sg pending to cdma
output sc2cdma_wt_pending_req;
input cdma2sc_dat_pending_ack; //cdma tould sg to clr pending
input cdma2sc_wt_pending_ack;
output sg2dl_reuse_rls;
output sg2wl_reuse_rls;
input [0:0] reg2dp_op_en;
input [0:0] reg2dp_conv_mode;
input [1:0] reg2dp_proc_precision;
input [0:0] reg2dp_data_reuse;
input [0:0] reg2dp_skip_data_rls;
input [0:0] reg2dp_weight_reuse;
input [0:0] reg2dp_skip_weight_rls;
input [4:0] reg2dp_batches;
input [0:0] reg2dp_datain_format;
input [12:0] reg2dp_datain_height_ext;
input [1:0] reg2dp_y_extension;
input [4:0] reg2dp_weight_width_ext;
input [4:0] reg2dp_weight_height_ext;
input [12:0] reg2dp_weight_channel_ext;
input [12:0] reg2dp_weight_kernel;
input [12:0] reg2dp_dataout_width;
input [12:0] reg2dp_dataout_height;
input [4:0] reg2dp_data_bank;
input [4:0] reg2dp_weight_bank;
input [20:0] reg2dp_atomics;
input [11:0] reg2dp_rls_slices;


NV_NVDLA_CSC_sg u_csc_sg(
.reset(~nvdla_core_rstn)
,.io_nvdla_core_clk(nvdla_core_clk)
,.io_nvdla_core_ng_clk(nvdla_core_clk)
,.io_cdma2sc_dat_updt_valid(cdma2sc_dat_updt)
,.io_cdma2sc_dat_updt_bits_entries(cdma2sc_dat_entries)
,.io_cdma2sc_dat_updt_bits_slices(cdma2sc_dat_slices)
,.io_sc2cdma_dat_pending_req(sc2cdma_dat_pending_req)
,.io_cdma2sc_dat_pending_ack(cdma2sc_dat_pending_ack)
,.io_cdma2sc_wt_updt_valid(cdma2sc_wt_updt)
,.io_cdma2sc_wt_updt_bits_entries(cdma2sc_wt_entries)
,.io_cdma2sc_wt_updt_bits_kernels(cdma2sc_wt_kernels)
,.io_sc2cdma_wt_pending_req(sc2cdma_wt_pending_req)
,.io_cdma2sc_wt_pending_ack(cdma2sc_wt_pending_ack)
,.io_sc_state(sc_state)
,.io_sg2dl_pd_valid(sg2dl_pvld)
,.io_sg2dl_pd_bits(sg2dl_pd)
,.io_sg2dl_reuse_rls(sg2dl_reuse_rls)
,.io_sg2wl_pd_valid(sg2wl_pvld)
,.io_sg2wl_pd_bits(sg2wl_pd)
,.io_sg2wl_reuse_rls(sg2wl_reuse_rls)
,.io_accu2sc_credit_size_valid(accu2sc_credit_vld)
,.io_accu2sc_credit_size_bits(accu2sc_credit_size)
,.io_reg2dp_op_en(reg2dp_op_en)
,.io_reg2dp_conv_mode(reg2dp_conv_mode)
,.io_reg2dp_proc_precision(reg2dp_proc_precision)
,.io_reg2dp_data_reuse(reg2dp_data_reuse)
,.io_reg2dp_skip_data_rls(reg2dp_skip_data_rls)
,.io_reg2dp_weight_reuse(reg2dp_weight_reuse)
,.io_reg2dp_skip_weight_rls(reg2dp_skip_weight_rls)
,.io_reg2dp_batches(reg2dp_batches)
,.io_reg2dp_datain_format(reg2dp_datain_format)
,.io_reg2dp_datain_height_ext(reg2dp_datain_height_ext)
,.io_reg2dp_y_extension(reg2dp_y_extension)
,.io_reg2dp_weight_width_ext(reg2dp_weight_width_ext)
,.io_reg2dp_weight_height_ext(reg2dp_weight_height_ext)
,.io_reg2dp_weight_channel_ext(reg2dp_weight_channel_ext)
,.io_reg2dp_weight_kernel(reg2dp_weight_kernel)
,.io_reg2dp_dataout_width(reg2dp_dataout_width)
,.io_reg2dp_dataout_height(reg2dp_dataout_height)
,.io_reg2dp_data_bank(reg2dp_data_bank)
,.io_reg2dp_weight_bank(reg2dp_weight_bank)
,.io_reg2dp_atomics(reg2dp_atomics)
,.io_reg2dp_rls_slices(reg2dp_rls_slices)
,.io_dp2reg_done(dp2reg_done)
,.io_pwrbus_ram_pd(pwrbus_ram_pd)
);

endmodule

