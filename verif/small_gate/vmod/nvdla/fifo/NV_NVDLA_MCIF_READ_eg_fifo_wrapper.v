module  NV_NVDLA_MCIF_READ_eg_fifo_wrapper(
      nvdla_core_clk
    , nvdla_core_rstn
    , rq_wr_prdy
    , rq_wr_pvld
    , rq_wr_pd
    , rq_rd_prdy
    , rq_rd_pvld
    , rq_rd_pd
    , pwrbus_ram_pd
    );
// spyglass disable_block W401 -- clock is not input to module
input nvdla_core_clk;
input nvdla_core_rstn;
output rq_wr_prdy;
input rq_wr_pvld;
input [63:0] rq_wr_pd;
input rq_rd_prdy;
output rq_rd_pvld;
output [63:0] rq_rd_pd;
input [31:0] pwrbus_ram_pd;


NV_NVDLA_MCIF_READ_eg_fifo (
.io_clk (nvdla_core_clk)
,.reset (~nvdla_core_rstn)
,.io_wr_pvld (rq_wr_pvld)
,.io_wr_prdy (rq_wr_prdy) 
,.io_wr_pd (rq_wr_pd)
,.io_rd_pvld (rq_rd_pvld) 
,.io_rd_prdy (rq_rd_prdy) 
,.io_rd_pd (rq_rd_pd) 
,.io_pwrbus_ram_pd (pwrbus_ram_pd)
);

endmodule

