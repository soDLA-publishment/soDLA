module NV_NVDLA_CSC_sg_golden_wrapper (
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
  ,dp2reg_done 
  ,sc2cdma_dat_pending_req 
  ,sc2cdma_wt_pending_req 
  ,sc_state 
  ,sg2dl_pd 
  ,sg2dl_pvld 
  ,sg2dl_reuse_rls 
  ,sg2wl_pd 
  ,sg2wl_pvld 
  ,sg2wl_reuse_rls 
  );
input nvdla_core_clk; /* done_dp2reg, dat_up_cdma2sc, wt_up_cdma2sc, sg2dl_pkg, sg2wl_pkg, accu2sc_credit, sc_state, sc2cdma_dat_pending, sc2cdma_wt_pending, cdma2sc_dat_pending, cdma2sc_wt_pending, sg2dl_reuse, sg2wl_reuse */
input nvdla_core_rstn; /* done_dp2reg, dat_up_cdma2sc, wt_up_cdma2sc, sg2dl_pkg, sg2wl_pkg, accu2sc_credit, sc_state, sc2cdma_dat_pending, sc2cdma_wt_pending, cdma2sc_dat_pending, cdma2sc_wt_pending, sg2dl_reuse, sg2wl_reuse */
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
.nvdla_core_clk(nvdla_core_clk) 
,.nvdla_core_ng_clk(nvdla_core_clk)
,.nvdla_core_rstn(nvdla_core_rstn)
,.accu2sc_credit_size(accu2sc_credit_size)
,.accu2sc_credit_vld(accu2sc_credit_vld)
,.cdma2sc_dat_entries(cdma2sc_dat_entries)
,.cdma2sc_dat_pending_ack(cdma2sc_dat_pending_ack)
,.cdma2sc_dat_slices(cdma2sc_dat_slices)
,.cdma2sc_dat_updt(cdma2sc_dat_updt)
,.cdma2sc_wmb_entries(cdma2sc_wmb_entries)
,.cdma2sc_wt_entries(cdma2sc_wt_entries)
,.cdma2sc_wt_kernels(cdma2sc_wt_kernels)
,.cdma2sc_wt_pending_ack(cdma2sc_wt_pending_ack)
,.cdma2sc_wt_updt(cdma2sc_wt_updt)
,.pwrbus_ram_pd(pwrbus_ram_pd)
,.reg2dp_atomics(reg2dp_atomics)
,.reg2dp_batches(reg2dp_batches)
,.reg2dp_conv_mode(reg2dp_conv_mode)
,.reg2dp_data_bank(reg2dp_data_bank)
,.reg2dp_data_reuse(reg2dp_data_reuse)
,.reg2dp_datain_format(reg2dp_datain_format)
,.reg2dp_datain_height_ext(reg2dp_datain_height_ext)
,.reg2dp_dataout_height(reg2dp_dataout_height)
,.reg2dp_dataout_width(reg2dp_dataout_width)
,.reg2dp_op_en(reg2dp_op_en)
,.reg2dp_proc_precision(reg2dp_proc_precision)
,.reg2dp_rls_slices(reg2dp_rls_slices)
,.reg2dp_skip_data_rls(reg2dp_skip_data_rls)
,.reg2dp_skip_weight_rls(reg2dp_skip_weight_rls)
,.reg2dp_weight_bank(reg2dp_weight_bank)
,.reg2dp_weight_channel_ext(reg2dp_weight_channel_ext)
,.reg2dp_weight_height_ext(reg2dp_weight_height_ext)
,.reg2dp_weight_kernel(reg2dp_weight_kernel)
,.reg2dp_weight_reuse(reg2dp_weight_reuse)
,.reg2dp_weight_width_ext(reg2dp_weight_width_ext)
,.reg2dp_y_extension(reg2dp_y_extension)
,.dp2reg_done(dp2reg_done)
,.sc2cdma_dat_pending_req(sc2cdma_dat_pending_req)
,.sc2cdma_wt_pending_req(sc2cdma_wt_pending_req)
,.sc_state(sc_state)
,.sg2dl_pd(sg2dl_pd)
,.sg2dl_pvld(sg2dl_pvld)
,.sg2dl_reuse_rls(sg2dl_reuse_rls)
,.sg2wl_pd(sg2wl_pd)
,.sg2wl_pvld(sg2wl_pvld)
,.sg2wl_reuse_rls(sg2wl_reuse_rls)
);

endmodule