module NV_NVDLA_DMAIF_wr_wrapper (
   nvdla_core_clk
  ,nvdla_core_rstn
  ,reg2dp_dst_ram_type
  ,mcif_wr_req_pd
  ,mcif_wr_req_valid
  ,mcif_wr_req_ready
  ,mcif_wr_rsp_complete
  ,dmaif_wr_req_pd
  ,dmaif_wr_req_pvld
  ,dmaif_wr_req_prdy
  ,dmaif_wr_rsp_complete
);

input nvdla_core_clk;
input nvdla_core_rstn;
input reg2dp_dst_ram_type;
output [65:0] mcif_wr_req_pd; 

output mcif_wr_req_valid;
input mcif_wr_req_ready;
input mcif_wr_rsp_complete;
input [65:0] dmaif_wr_req_pd; 

input dmaif_wr_req_pvld;
output dmaif_wr_req_prdy;
output dmaif_wr_rsp_complete;


NV_NVDLA_DMAIF_wr (
.io_nvdla_core_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_mcif_wr_req_pd_ready (mcif_wr_req_ready)
,.io_mcif_wr_req_pd_valid (mcif_wr_req_valid)
,.io_mcif_wr_req_pd_bits (mcif_wr_req_pd)
,.io_mcif_wr_rsp_complete (mcif_wr_rsp_complete)
,.io_dmaif_wr_req_pd_ready (dmaif_wr_req_prdy)
,.io_dmaif_wr_req_pd_valid (dmaif_wr_req_pvld)
,.io_dmaif_wr_req_pd_bits (dmaif_wr_req_pd)
,.io_dmaif_wr_rsp_complete (dmaif_wr_rsp_complete)
,.io_reg2dp_dst_ram_type (reg2dp_dst_ram_type)
);

endmodule
