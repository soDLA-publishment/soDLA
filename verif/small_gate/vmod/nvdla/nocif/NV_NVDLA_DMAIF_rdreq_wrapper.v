module NV_NVDLA_DMAIF_rdreq_wrapper (
   nvdla_core_clk
  ,nvdla_core_rstn
  ,reg2dp_src_ram_type
  ,mcif_rd_req_pd
  ,mcif_rd_req_valid
  ,mcif_rd_req_ready
  ,dmaif_rd_req_pd
  ,dmaif_rd_req_vld
  ,dmaif_rd_req_rdy
);

input nvdla_core_clk;
input nvdla_core_rstn;
input reg2dp_src_ram_type;
output [32 +14:0] mcif_rd_req_pd;
output mcif_rd_req_valid;
input mcif_rd_req_ready;
input [32 +14:0] dmaif_rd_req_pd;
input dmaif_rd_req_vld;
output dmaif_rd_req_rdy;


NV_NVDLA_DMAIF_rdreq (
.io_nvdla_core_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_reg2dp_src_ram_type (reg2dp_src_ram_type)
,.io_dmaif_rd_req_pd_ready (dmaif_rd_req_rdy)
,.io_dmaif_rd_req_pd_valid (dmaif_rd_req_vld)
,.io_dmaif_rd_req_pd_bits (dmaif_rd_req_pd)
,.io_mcif_rd_req_pd_ready (mcif_rd_req_ready)
,.io_mcif_rd_req_pd_valid (mcif_rd_req_valid)
,.io_mcif_rd_req_pd_bits (mcif_rd_req_pd)
);


endmodule
