module NV_NVDLA_csb_master_golden_wrapper (
   nvdla_core_clk
  ,nvdla_core_rstn
  ,pwrbus_ram_pd
  ,csb2nvdla_valid
  ,csb2nvdla_ready 
  ,csb2nvdla_addr
  ,csb2nvdla_wdat
  ,csb2nvdla_write
  ,csb2nvdla_nposted
  ,nvdla2csb_valid 
  ,nvdla2csb_data 
  ,nvdla2csb_wr_complete 
  ,csb2cfgrom_req_pvld 
  ,csb2cfgrom_req_prdy
  ,csb2cfgrom_req_pd 
  ,cfgrom2csb_resp_valid
  ,cfgrom2csb_resp_pd
  ,csb2glb_req_pvld 
  ,csb2glb_req_prdy
  ,csb2glb_req_pd 
  ,glb2csb_resp_valid
  ,glb2csb_resp_pd
  ,csb2mcif_req_pvld 
  ,csb2mcif_req_prdy
  ,csb2mcif_req_pd 
  ,mcif2csb_resp_valid
  ,mcif2csb_resp_pd
  ,csb2cdma_req_pvld 
  ,csb2cdma_req_prdy
  ,csb2cdma_req_pd 
  ,cdma2csb_resp_valid
  ,cdma2csb_resp_pd
  ,csb2csc_req_pvld 
  ,csb2csc_req_prdy
  ,csb2csc_req_pd 
  ,csc2csb_resp_valid
  ,csc2csb_resp_pd
  ,csb2cmac_a_req_pvld 
  ,csb2cmac_a_req_prdy
  ,csb2cmac_a_req_pd 
  ,cmac_a2csb_resp_valid
  ,cmac_a2csb_resp_pd
  ,csb2cmac_b_req_pvld 
  ,csb2cmac_b_req_prdy
  ,csb2cmac_b_req_pd 
  ,cmac_b2csb_resp_valid
  ,cmac_b2csb_resp_pd
  ,csb2cacc_req_pvld 
  ,csb2cacc_req_prdy
  ,csb2cacc_req_pd 
  ,cacc2csb_resp_valid
  ,cacc2csb_resp_pd
  ,csb2sdp_rdma_req_pvld 
  ,csb2sdp_rdma_req_prdy
  ,csb2sdp_rdma_req_pd 
  ,sdp_rdma2csb_resp_valid
  ,sdp_rdma2csb_resp_pd
  ,csb2sdp_req_pvld 
  ,csb2sdp_req_prdy
  ,csb2sdp_req_pd 
  ,sdp2csb_resp_valid
  ,sdp2csb_resp_pd
  ,csb2pdp_rdma_req_pvld 
  ,csb2pdp_rdma_req_prdy
  ,csb2pdp_rdma_req_pd 
  ,pdp_rdma2csb_resp_valid
  ,pdp_rdma2csb_resp_pd
  ,csb2pdp_req_pvld 
  ,csb2pdp_req_prdy
  ,csb2pdp_req_pd 
  ,pdp2csb_resp_valid
  ,pdp2csb_resp_pd
  ,csb2cdp_rdma_req_pvld 
  ,csb2cdp_rdma_req_prdy
  ,csb2cdp_rdma_req_pd 
  ,cdp_rdma2csb_resp_valid
  ,cdp_rdma2csb_resp_pd
  ,csb2cdp_req_pvld 
  ,csb2cdp_req_prdy
  ,csb2cdp_req_pd 
  ,cdp2csb_resp_valid
  ,cdp2csb_resp_pd
  );
//
// NV_NVDLA_csb_master_ports.v
//
input nvdla_core_clk;
input nvdla_core_rstn;
input [31:0] pwrbus_ram_pd;
input csb2nvdla_valid; /* data valid */
output csb2nvdla_ready; /* data return handshake */
input [15:0] csb2nvdla_addr;
input [31:0] csb2nvdla_wdat;
input csb2nvdla_write;
input csb2nvdla_nposted;
output nvdla2csb_valid; /* data valid */
output [31:0] nvdla2csb_data;
output nvdla2csb_wr_complete;
output csb2cfgrom_req_pvld; /* data valid */
input csb2cfgrom_req_prdy; /* data return handshake */
output [62:0] csb2cfgrom_req_pd;
input cfgrom2csb_resp_valid; /* data valid */
input [33:0] cfgrom2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2glb_req_pvld; /* data valid */
input csb2glb_req_prdy; /* data return handshake */
output [62:0] csb2glb_req_pd;
input glb2csb_resp_valid; /* data valid */
input [33:0] glb2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2mcif_req_pvld; /* data valid */
input csb2mcif_req_prdy; /* data return handshake */
output [62:0] csb2mcif_req_pd;
input mcif2csb_resp_valid; /* data valid */
input [33:0] mcif2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2cdma_req_pvld; /* data valid */
input csb2cdma_req_prdy; /* data return handshake */
output [62:0] csb2cdma_req_pd;
input cdma2csb_resp_valid; /* data valid */
input [33:0] cdma2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2csc_req_pvld; /* data valid */
input csb2csc_req_prdy; /* data return handshake */
output [62:0] csb2csc_req_pd;
input csc2csb_resp_valid; /* data valid */
input [33:0] csc2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2cmac_a_req_pvld; /* data valid */
input csb2cmac_a_req_prdy; /* data return handshake */
output [62:0] csb2cmac_a_req_pd;
input cmac_a2csb_resp_valid; /* data valid */
input [33:0] cmac_a2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2cmac_b_req_pvld; /* data valid */
input csb2cmac_b_req_prdy; /* data return handshake */
output [62:0] csb2cmac_b_req_pd;
input cmac_b2csb_resp_valid; /* data valid */
input [33:0] cmac_b2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2cacc_req_pvld; /* data valid */
input csb2cacc_req_prdy; /* data return handshake */
output [62:0] csb2cacc_req_pd;
input cacc2csb_resp_valid; /* data valid */
input [33:0] cacc2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2sdp_rdma_req_pvld; /* data valid */
input csb2sdp_rdma_req_prdy; /* data return handshake */
output [62:0] csb2sdp_rdma_req_pd;
input sdp_rdma2csb_resp_valid; /* data valid */
input [33:0] sdp_rdma2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2sdp_req_pvld; /* data valid */
input csb2sdp_req_prdy; /* data return handshake */
output [62:0] csb2sdp_req_pd;
input sdp2csb_resp_valid; /* data valid */
input [33:0] sdp2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2pdp_rdma_req_pvld; /* data valid */
input csb2pdp_rdma_req_prdy; /* data return handshake */
output [62:0] csb2pdp_rdma_req_pd;
input pdp_rdma2csb_resp_valid; /* data valid */
input [33:0] pdp_rdma2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2pdp_req_pvld; /* data valid */
input csb2pdp_req_prdy; /* data return handshake */
output [62:0] csb2pdp_req_pd;
input pdp2csb_resp_valid; /* data valid */
input [33:0] pdp2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2cdp_rdma_req_pvld; /* data valid */
input csb2cdp_rdma_req_prdy; /* data return handshake */
output [62:0] csb2cdp_rdma_req_pd;
input cdp_rdma2csb_resp_valid; /* data valid */
input [33:0] cdp_rdma2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */
output csb2cdp_req_pvld; /* data valid */
input csb2cdp_req_prdy; /* data return handshake */
output [62:0] csb2cdp_req_pd;
input cdp2csb_resp_valid; /* data valid */
input [33:0] cdp2csb_resp_pd; /* pkt_id_width=1 pkt_widths=33,33  */

NV_NVDLA_csb_master u_csb(
.nvdla_core_clk (nvdla_core_clk)
,.nvdla_core_rstn (nvdla_core_rstn)
,.nvdla_falcon_clk (nvdla_core_clk)
,.nvdla_falcon_rstn (nvdla_core_rstn)
,.pwrbus_ram_pd (pwrbus_ram_pd)
,.csb2nvdla_valid (csb2nvdla_valid)
,.csb2nvdla_ready (csb2nvdla_ready)
,.csb2nvdla_addr (csb2nvdla_addr)
,.csb2nvdla_wdat (csb2nvdla_wdat)
,.csb2nvdla_write (csb2nvdla_write)
,.csb2nvdla_nposted (csb2nvdla_nposted)
,.nvdla2csb_valid (nvdla2csb_valid)
,.nvdla2csb_data (nvdla2csb_data)
,.nvdla2csb_wr_complete (nvdla2csb_wr_complete) 
,.csb2cfgrom_req_pvld (csb2cfgrom_req_pvld)
,.csb2cfgrom_req_prdy (1'b1)
,.csb2cfgrom_req_pd (csb2cfgrom_req_pd)
,.cfgrom2csb_resp_valid (cfgrom2csb_resp_valid)
,.cfgrom2csb_resp_pd (cfgrom2csb_resp_pd)
,.csb2glb_req_pvld (csb2glb_req_pvld)
,.csb2glb_req_prdy (1'b1)
,.csb2glb_req_pd (csb2glb_req_pd)
,.glb2csb_resp_valid (glb2csb_resp_valid)
,.glb2csb_resp_pd (glb2csb_resp_pd)
,.csb2mcif_req_pvld (csb2mcif_req_pvld)
,.csb2mcif_req_prdy (1'b1)
,.csb2mcif_req_pd (csb2mcif_req_pd)
,.mcif2csb_resp_valid (mcif2csb_resp_valid)
,.mcif2csb_resp_pd (mcif2csb_resp_pd)
,.csb2cdma_req_pvld (csb2cdma_req_pvld)
,.csb2cdma_req_prdy (1'b1)
,.csb2cdma_req_pd (csb2cdma_req_pd)
,.cdma2csb_resp_valid (cdma2csb_resp_valid)
,.cdma2csb_resp_pd (cdma2csb_resp_pd)
,.csb2csc_req_pvld (csb2csc_req_pvld)
,.csb2csc_req_prdy (1'b1)
,.csb2csc_req_pd (csb2csc_req_pd)
,.csc2csb_resp_valid (csc2csb_resp_valid)
,.csc2csb_resp_pd (csc2csb_resp_pd)
,.csb2cmac_a_req_pvld (csb2cmac_a_req_pvld)
,.csb2cmac_a_req_prdy (1'b1)
,.csb2cmac_a_req_pd (csb2cmac_a_req_pd)
,.cmac_a2csb_resp_valid (cmac_a2csb_resp_valid)
,.cmac_a2csb_resp_pd (cmac_a2csb_resp_pd)
,.csb2cmac_b_req_pvld (csb2cmac_b_req_pvld)
,.csb2cmac_b_req_prdy (1'b1)
,.csb2cmac_b_req_pd (csb2cmac_b_req_pd)
,.cmac_b2csb_resp_valid (cmac_b2csb_resp_valid)
,.cmac_b2csb_resp_pd (cmac_b2csb_resp_pd)
,.csb2cacc_req_pvld (csb2cacc_req_pvld)
,.csb2cacc_req_prdy (1'b1)
,.csb2cacc_req_pd (csb2cacc_req_pd)
,.cacc2csb_resp_valid (cacc2csb_resp_valid)
,.cacc2csb_resp_pd (cacc2csb_resp_pd)
,.csb2sdp_rdma_req_pvld (csb2sdp_rdma_req_pvld)
,.csb2sdp_rdma_req_prdy (1'b1)
,.csb2sdp_rdma_req_pd (csb2sdp_rdma_req_pd)
,.sdp_rdma2csb_resp_valid (sdp_rdma2csb_resp_valid)
,.sdp_rdma2csb_resp_pd (sdp_rdma2csb_resp_pd)
,.csb2sdp_req_pvld (csb2sdp_req_pvld)
,.csb2sdp_req_prdy (1'b1)
,.csb2sdp_req_pd (csb2sdp_req_pd)
,.sdp2csb_resp_valid (sdp2csb_resp_valid)
,.sdp2csb_resp_pd (sdp2csb_resp_pd)
,.csb2pdp_rdma_req_pvld (csb2pdp_rdma_req_pvld) 
,.csb2pdp_rdma_req_prdy (1'b1)
,.csb2pdp_rdma_req_pd (csb2pdp_rdma_req_pd)
,.pdp_rdma2csb_resp_valid (pdp_rdma2csb_resp_valid)
,.pdp_rdma2csb_resp_pd (pdp_rdma2csb_resp_pd)
,.csb2pdp_req_pvld (csb2pdp_req_pvld)
,.csb2pdp_req_prdy (1'b1)
,.csb2pdp_req_pd (csb2pdp_req_pd)
,.pdp2csb_resp_valid (pdp2csb_resp_valid)
,.pdp2csb_resp_pd (pdp2csb_resp_pd)
,.csb2cdp_rdma_req_pvld (csb2cdp_rdma_req_pvld) 
,.csb2cdp_rdma_req_prdy (1'b1)
,.csb2cdp_rdma_req_pd (csb2cdp_rdma_req_pd)
,.cdp_rdma2csb_resp_valid (cdp_rdma2csb_resp_valid)
,.cdp_rdma2csb_resp_pd (cdp_rdma2csb_resp_pd)
,.csb2cdp_req_pvld (csb2cdp_req_pvld)
,.csb2cdp_req_prdy (1'b1)
,.csb2cdp_req_pd (csb2cdp_req_pd)
,.cdp2csb_resp_valid (cdp2csb_resp_valid)
,.cdp2csb_resp_pd (cdp2csb_resp_pd)
);

endmodule // NV_NVDLA_glb