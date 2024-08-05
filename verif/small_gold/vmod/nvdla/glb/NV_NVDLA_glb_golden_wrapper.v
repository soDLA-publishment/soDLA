module NV_NVDLA_glb_golden_wrapper (
   cacc2glb_done_intr_pd 
  ,cdma_dat2glb_done_intr_pd 
  ,cdma_wt2glb_done_intr_pd 
  ,cdp2glb_done_intr_pd 
  ,csb2glb_req_pd 
  ,csb2glb_req_pvld 
  ,nvdla_core_clk 
  ,nvdla_core_rstn 
  ,pdp2glb_done_intr_pd 
  ,sdp2glb_done_intr_pd 
  ,core_intr 
  ,csb2glb_req_prdy 
  ,glb2csb_resp_pd 
  ,glb2csb_resp_valid 
  );
//
// NV_NVDLA_glb_io.v
//
input csb2glb_req_pvld; /* data valid */
output csb2glb_req_prdy; /* data return handshake */
input [62:0] csb2glb_req_pd;
output glb2csb_resp_valid; /* data valid */
output [33:0] glb2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output core_intr;
input [1:0] sdp2glb_done_intr_pd;
input [1:0] cdp2glb_done_intr_pd;
input [1:0] pdp2glb_done_intr_pd;
input [1:0] cdma_wt2glb_done_intr_pd;
input [1:0] cdma_dat2glb_done_intr_pd;
input [1:0] cacc2glb_done_intr_pd;
input nvdla_core_clk;
input nvdla_core_rstn;


NV_NVDLA_glb u_glb(
.cacc2glb_done_intr_pd (cacc2glb_done_intr_pd)
,.cdma_dat2glb_done_intr_pd (cdma_dat2glb_done_intr_pd)
,.cdma_wt2glb_done_intr_pd (cdma_wt2glb_done_intr_pd)
,.cdp2glb_done_intr_pd (cdp2glb_done_intr_pd)
,.csb2glb_req_pd (csb2glb_req_pd)
,.csb2glb_req_pvld (csb2glb_req_pvld)
,.nvdla_core_clk (nvdla_core_clk)
,.nvdla_core_rstn (nvdla_core_rstn)
,.nvdla_falcon_clk (nvdla_core_clk)
,.nvdla_falcon_rstn (nvdla_core_rstn)
,.pdp2glb_done_intr_pd (pdp2glb_done_intr_pd)
,.sdp2glb_done_intr_pd (sdp2glb_done_intr_pd)
,.core_intr (core_intr)
,.csb2glb_req_prdy (csb2glb_req_prdy)
,.glb2csb_resp_pd (glb2csb_resp_pd)
,.glb2csb_resp_valid (glb2csb_resp_valid)
);

endmodule // NV_NVDLA_glb
