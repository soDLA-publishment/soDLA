module NV_NVDLA_DMAIF_rdrsp_wrapper (
   nvdla_core_clk
  ,nvdla_core_rstn
  ,mcif_rd_rsp_pd
  ,mcif_rd_rsp_valid
  ,mcif_rd_rsp_ready
  ,dmaif_rd_rsp_pd
  ,dmaif_rd_rsp_pvld
  ,dmaif_rd_rsp_prdy
);

input nvdla_core_clk;
input nvdla_core_rstn;
input [65-1:0] mcif_rd_rsp_pd; 
output [65-1:0] dmaif_rd_rsp_pd; 
input mcif_rd_rsp_valid;
output mcif_rd_rsp_ready;
output dmaif_rd_rsp_pvld;
input dmaif_rd_rsp_prdy;


NV_NVDLA_DMAIF_rdrsp (
.io_nvdla_core_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_mcif_rd_rsp_pd_ready (mcif_rd_rsp_ready)
,.io_mcif_rd_rsp_pd_valid (mcif_rd_rsp_valid)
,.io_mcif_rd_rsp_pd_bits (mcif_rd_rsp_pd)
,.io_dmaif_rd_rsp_pd_ready (dmaif_rd_rsp_prdy)
,.io_dmaif_rd_rsp_pd_valid (dmaif_rd_rsp_pvld)
,.io_dmaif_rd_rsp_pd_bits (dmaif_rd_rsp_pd)
);


endmodule
