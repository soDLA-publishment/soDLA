module NV_NVDLA_MCIF_csb_wrapper (
   nvdla_core_clk //|< i
  ,nvdla_core_rstn //|< i
  ,csb2mcif_req_pd //|< i
  ,csb2mcif_req_pvld //|< i
  ,dp2reg_idle //|< i
  ,csb2mcif_req_prdy //|> o
  ,mcif2csb_resp_pd //|> o
  ,mcif2csb_resp_valid //|> o
  ,reg2dp_rd_os_cnt //|> o
  ,reg2dp_rd_weight_bdma //|> o
  ,reg2dp_rd_weight_cdma_dat //|> o
  ,reg2dp_rd_weight_cdma_wt //|> o
  ,reg2dp_rd_weight_cdp //|> o
  ,reg2dp_rd_weight_pdp //|> o
  ,reg2dp_rd_weight_rbk //|> o
  ,reg2dp_rd_weight_rsv_0 //|> o
  ,reg2dp_rd_weight_rsv_1 //|> o
  ,reg2dp_rd_weight_sdp //|> o
  ,reg2dp_rd_weight_sdp_b //|> o
  ,reg2dp_rd_weight_sdp_e //|> o
  ,reg2dp_rd_weight_sdp_n //|> o
  ,reg2dp_wr_os_cnt //|> o
  ,reg2dp_wr_weight_bdma //|> o
  ,reg2dp_wr_weight_cdp //|> o
  ,reg2dp_wr_weight_pdp //|> o
  ,reg2dp_wr_weight_rbk //|> o
  ,reg2dp_wr_weight_rsv_0 //|> o
  ,reg2dp_wr_weight_rsv_1 //|> o
  ,reg2dp_wr_weight_rsv_2 //|> o
  ,reg2dp_wr_weight_sdp //|> o
  );
//

input nvdla_core_clk;
input nvdla_core_rstn;
input csb2mcif_req_pvld; /* data valid */
output csb2mcif_req_prdy; /* data return handshake */
input [62:0] csb2mcif_req_pd;
output mcif2csb_resp_valid; /* data valid */
output [33:0] mcif2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
input dp2reg_idle;
output [7:0] reg2dp_rd_os_cnt;
output [7:0] reg2dp_rd_weight_bdma;
output [7:0] reg2dp_rd_weight_cdma_dat;
output [7:0] reg2dp_rd_weight_cdma_wt;
output [7:0] reg2dp_rd_weight_cdp;
output [7:0] reg2dp_rd_weight_pdp;
output [7:0] reg2dp_rd_weight_rbk;
output [7:0] reg2dp_rd_weight_rsv_0;
output [7:0] reg2dp_rd_weight_rsv_1;
output [7:0] reg2dp_rd_weight_sdp;
output [7:0] reg2dp_rd_weight_sdp_b;
output [7:0] reg2dp_rd_weight_sdp_e;
output [7:0] reg2dp_rd_weight_sdp_n;
output [7:0] reg2dp_wr_os_cnt;
output [7:0] reg2dp_wr_weight_bdma;
output [7:0] reg2dp_wr_weight_cdp;
output [7:0] reg2dp_wr_weight_pdp;
output [7:0] reg2dp_wr_weight_rbk;
output [7:0] reg2dp_wr_weight_rsv_0;
output [7:0] reg2dp_wr_weight_rsv_1;
output [7:0] reg2dp_wr_weight_rsv_2;
output [7:0] reg2dp_wr_weight_sdp;

NV_NVDLA_MCIF_csb (
.io_nvdla_core_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_csb2mcif_req_ready (csb2mcif_req_prdy)
,.io_csb2mcif_req_valid (csb2mcif_req_pvld)
,.io_csb2mcif_req_bits  (csb2mcif_req_pd)
,.io_csb2mcif_resp_valid (mcif2csb_resp_valid)
,.io_csb2mcif_resp_bits (mcif2csb_resp_pd)
,.io_reg2dp_field_rd_os_cnt (reg2dp_rd_os_cnt)
,.io_reg2dp_field_rd_weight_rsv_0 (reg2dp_rd_weight_rsv_0)
,.io_reg2dp_field_rd_weight_rsv_1 (reg2dp_rd_weight_rsv_1)
,.io_reg2dp_field_rd_weight_client_0 (reg2dp_rd_weight_bdma)
,.io_reg2dp_field_rd_weight_client_1 (reg2dp_rd_weight_sdp)
,.io_reg2dp_field_rd_weight_client_2 (reg2dp_rd_weight_pdp)
,.io_reg2dp_field_rd_weight_client_3 (reg2dp_rd_weight_cdp)
,.io_reg2dp_field_rd_weight_client_4 (reg2dp_rd_weight_rbk)
,.io_reg2dp_field_rd_weight_client_5 (reg2dp_rd_weight_sdp_b)
,.io_reg2dp_field_rd_weight_client_6 (reg2dp_rd_weight_sdp_n)
,.io_reg2dp_field_rd_weight_client_7 (reg2dp_rd_weight_sdp_e)
,.io_reg2dp_field_rd_weight_client_8 (reg2dp_rd_weight_cdma_dat)
,.io_reg2dp_field_rd_weight_client_9 (reg2dp_rd_weight_cdma_wt)
,.io_reg2dp_field_wr_os_cnt (reg2dp_wr_os_cnt)
,.io_reg2dp_field_wr_weight_rsv_0 (reg2dp_wr_weight_rsv_0)
,.io_reg2dp_field_wr_weight_rsv_1 (reg2dp_wr_weight_rsv_1)
,.io_reg2dp_field_wr_weight_rsv_2 (reg2dp_wr_weight_rsv_2)
,.io_reg2dp_field_wr_weight_client_0 (reg2dp_wr_weight_bdma)
,.io_reg2dp_field_wr_weight_client_1 (reg2dp_wr_weight_sdp)
,.io_reg2dp_field_wr_weight_client_2 (reg2dp_wr_weight_pdp)
,.io_reg2dp_field_wr_weight_client_3 (reg2dp_wr_weight_cdp)
,.io_reg2dp_field_wr_weight_client_4 (reg2dp_wr_weight_rbk)
,.io_dp2reg_idle (dp2reg_idle)
);




endmodule