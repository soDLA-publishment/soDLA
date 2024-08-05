module NV_NVDLA_IS_pipe( // @[:@3.2]
  input        reset, // @[:@5.4]
  input        io_clk, // @[:@6.4]
  output [2:0] io_dout, // @[:@6.4]
  output       io_vo, // @[:@6.4]
  input        io_ri, // @[:@6.4]
  input  [2:0] io_di, // @[:@6.4]
  input        io_vi, // @[:@6.4]
  output       io_ro // @[:@6.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@8.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@9.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@10.4]
  reg [31:0] _RAND_2;
  reg [2:0] skid_flop_di; // @[IS_pipe.scala 57:53:@11.4]
  reg [31:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@12.4]
  reg [31:0] _RAND_4;
  reg [2:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@13.4]
  reg [31:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@22.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@27.4]
  wire [2:0] _GEN_1; // @[IS_pipe.scala 76:29:@28.4]
  wire [2:0] skid_di; // @[IS_pipe.scala 79:19:@31.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@33.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@34.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@36.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@39.4]
  wire [2:0] _GEN_3; // @[IS_pipe.scala 87:29:@40.4]
  assign _GEN_0 = skid_flop_ro ? io_vi : skid_flop_vi; // @[IS_pipe.scala 71:23:@22.4]
  assign _T_38 = skid_flop_ro & io_vi; // @[IS_pipe.scala 76:22:@27.4]
  assign _GEN_1 = _T_38 ? io_di : skid_flop_di; // @[IS_pipe.scala 76:29:@28.4]
  assign skid_di = skid_flop_ro ? io_di : skid_flop_di; // @[IS_pipe.scala 79:19:@31.4]
  assign _T_40 = ~ pipe_skid_vi; // @[IS_pipe.scala 81:32:@33.4]
  assign skid_ro = io_ri | _T_40; // @[IS_pipe.scala 81:29:@34.4]
  assign _GEN_2 = skid_ro ? _GEN_0 : pipe_skid_vi; // @[IS_pipe.scala 83:18:@36.4]
  assign _T_42 = skid_ro & _GEN_0; // @[IS_pipe.scala 87:18:@39.4]
  assign _GEN_3 = _T_42 ? skid_di : pipe_skid_di; // @[IS_pipe.scala 87:29:@40.4]
  assign io_dout = pipe_skid_di; // @[IS_pipe.scala 98:13:@48.4]
  assign io_vo = pipe_skid_vi; // @[IS_pipe.scala 97:11:@47.4]
  assign io_ro = ro_out; // @[IS_pipe.scala 96:11:@46.4]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  ro_out = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  skid_flop_ro = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  skid_flop_vi = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  skid_flop_di = _RAND_3[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  pipe_skid_di = _RAND_5[2:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      ro_out <= 1'h1;
    end else begin
      ro_out <= skid_ro;
    end
    if (reset) begin
      skid_flop_ro <= 1'h1;
    end else begin
      skid_flop_ro <= skid_ro;
    end
    if (reset) begin
      skid_flop_vi <= 1'h0;
    end else begin
      if (skid_flop_ro) begin
        skid_flop_vi <= io_vi;
      end
    end
    if (reset) begin
      skid_flop_di <= 3'h0;
    end else begin
      if (_T_38) begin
        skid_flop_di <= io_di;
      end
    end
    if (reset) begin
      pipe_skid_vi <= 1'h0;
    end else begin
      if (skid_ro) begin
        if (skid_flop_ro) begin
          pipe_skid_vi <= io_vi;
        end else begin
          pipe_skid_vi <= skid_flop_vi;
        end
      end
    end
    if (reset) begin
      pipe_skid_di <= 3'h0;
    end else begin
      if (_T_42) begin
        if (skid_flop_ro) begin
          pipe_skid_di <= io_di;
        end else begin
          pipe_skid_di <= skid_flop_di;
        end
      end
    end
  end
endmodule
module NV_NVDLA_MCIF_WRITE_eg( // @[:@50.2]
  input        clock, // @[:@51.4]
  input        reset, // @[:@52.4]
  input        io_nvdla_core_clk, // @[:@53.4]
  output       io_mcif2client_wr_rsp_complete_0, // @[:@53.4]
  output       io_mcif2client_wr_rsp_complete_1, // @[:@53.4]
  output       io_mcif2client_wr_rsp_complete_2, // @[:@53.4]
  output       io_cq_rd_pd_0_ready, // @[:@53.4]
  input        io_cq_rd_pd_0_valid, // @[:@53.4]
  input  [2:0] io_cq_rd_pd_0_bits, // @[:@53.4]
  output       io_cq_rd_pd_1_ready, // @[:@53.4]
  input        io_cq_rd_pd_1_valid, // @[:@53.4]
  input  [2:0] io_cq_rd_pd_1_bits, // @[:@53.4]
  output       io_cq_rd_pd_2_ready, // @[:@53.4]
  input        io_cq_rd_pd_2_valid, // @[:@53.4]
  input  [2:0] io_cq_rd_pd_2_bits, // @[:@53.4]
  output       io_cq_rd_pd_3_ready, // @[:@53.4]
  input        io_cq_rd_pd_3_valid, // @[:@53.4]
  input  [2:0] io_cq_rd_pd_3_bits, // @[:@53.4]
  output       io_cq_rd_pd_4_ready, // @[:@53.4]
  input        io_cq_rd_pd_4_valid, // @[:@53.4]
  input  [2:0] io_cq_rd_pd_4_bits, // @[:@53.4]
  output       io_noc2mcif_axi_b_ready, // @[:@53.4]
  input        io_noc2mcif_axi_b_valid, // @[:@53.4]
  input  [7:0] io_noc2mcif_axi_b_bits_id, // @[:@53.4]
  output       io_eg2ig_axi_len_valid, // @[:@53.4]
  output [1:0] io_eg2ig_axi_len_bits // @[:@53.4]
);
  wire  u_pipe_reset; // @[NV_NVDLA_MCIF_WRITE_eg.scala 45:24:@56.4]
  wire  u_pipe_io_clk; // @[NV_NVDLA_MCIF_WRITE_eg.scala 45:24:@56.4]
  wire [2:0] u_pipe_io_dout; // @[NV_NVDLA_MCIF_WRITE_eg.scala 45:24:@56.4]
  wire  u_pipe_io_vo; // @[NV_NVDLA_MCIF_WRITE_eg.scala 45:24:@56.4]
  wire  u_pipe_io_ri; // @[NV_NVDLA_MCIF_WRITE_eg.scala 45:24:@56.4]
  wire [2:0] u_pipe_io_di; // @[NV_NVDLA_MCIF_WRITE_eg.scala 45:24:@56.4]
  wire  u_pipe_io_vi; // @[NV_NVDLA_MCIF_WRITE_eg.scala 45:24:@56.4]
  wire  u_pipe_io_ro; // @[NV_NVDLA_MCIF_WRITE_eg.scala 45:24:@56.4]
  wire  _T_145; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@64.4]
  wire  iflop_axi_rdy_vec_0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@65.4]
  wire  _T_148; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@66.4]
  wire  iflop_axi_rdy_vec_1; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@67.4]
  wire  _T_151; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@68.4]
  wire  iflop_axi_rdy_vec_2; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@69.4]
  wire  _T_154; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@70.4]
  wire  iflop_axi_rdy_vec_3; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@71.4]
  wire  _T_157; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@72.4]
  wire  iflop_axi_rdy_vec_4; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@73.4]
  wire  iflop_axi_vld_vec_0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@81.4]
  wire  iflop_axi_vld_vec_1; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@83.4]
  wire  iflop_axi_vld_vec_2; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@85.4]
  wire  iflop_axi_vld_vec_3; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@87.4]
  wire  iflop_axi_vld_vec_4; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@89.4]
  wire [1:0] cq_rd_len_vec_0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@96.4]
  wire [1:0] cq_rd_len_vec_1; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@97.4]
  wire [1:0] cq_rd_len_vec_2; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@98.4]
  wire [1:0] cq_rd_len_vec_3; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@99.4]
  wire [1:0] cq_rd_len_vec_4; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@100.4]
  wire [4:0] _T_212; // @[NV_NVDLA_MCIF_WRITE_eg.scala 63:40:@115.4]
  wire  iflop_axi_rdy; // @[NV_NVDLA_MCIF_WRITE_eg.scala 63:47:@116.4]
  wire [1:0] _GEN_0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 69:35:@122.4]
  wire [1:0] _GEN_1; // @[NV_NVDLA_MCIF_WRITE_eg.scala 69:35:@125.4]
  wire [1:0] _GEN_2; // @[NV_NVDLA_MCIF_WRITE_eg.scala 69:35:@128.4]
  wire [1:0] _GEN_3; // @[NV_NVDLA_MCIF_WRITE_eg.scala 69:35:@131.4]
  wire  client_cq_rd_ack_0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 84:91:@153.4]
  wire  _T_266; // @[NV_NVDLA_MCIF_WRITE_eg.scala 84:91:@154.4]
  wire  _T_268; // @[NV_NVDLA_MCIF_WRITE_eg.scala 84:91:@155.4]
  wire  _T_284; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:74:@171.4]
  wire  _T_285; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:94:@172.4]
  reg  _T_288; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:53:@173.4]
  reg [31:0] _RAND_0;
  wire  _T_331; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:74:@206.4]
  wire  _T_332; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:94:@207.4]
  reg  _T_335; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:53:@208.4]
  reg [31:0] _RAND_1;
  wire  _T_378; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:74:@241.4]
  wire  _T_379; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:94:@242.4]
  reg  _T_382; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:53:@243.4]
  reg [31:0] _RAND_2;
  NV_NVDLA_IS_pipe u_pipe ( // @[NV_NVDLA_MCIF_WRITE_eg.scala 45:24:@56.4]
    .reset(u_pipe_reset),
    .io_clk(u_pipe_io_clk),
    .io_dout(u_pipe_io_dout),
    .io_vo(u_pipe_io_vo),
    .io_ri(u_pipe_io_ri),
    .io_di(u_pipe_io_di),
    .io_vi(u_pipe_io_vi),
    .io_ro(u_pipe_io_ro)
  );
  assign _T_145 = u_pipe_io_dout == 3'h0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@64.4]
  assign iflop_axi_rdy_vec_0 = io_cq_rd_pd_0_valid & _T_145; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@65.4]
  assign _T_148 = u_pipe_io_dout == 3'h1; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@66.4]
  assign iflop_axi_rdy_vec_1 = io_cq_rd_pd_1_valid & _T_148; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@67.4]
  assign _T_151 = u_pipe_io_dout == 3'h2; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@68.4]
  assign iflop_axi_rdy_vec_2 = io_cq_rd_pd_2_valid & _T_151; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@69.4]
  assign _T_154 = u_pipe_io_dout == 3'h3; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@70.4]
  assign iflop_axi_rdy_vec_3 = io_cq_rd_pd_3_valid & _T_154; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@71.4]
  assign _T_157 = u_pipe_io_dout == 3'h4; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:112:@72.4]
  assign iflop_axi_rdy_vec_4 = io_cq_rd_pd_4_valid & _T_157; // @[NV_NVDLA_MCIF_WRITE_eg.scala 56:94:@73.4]
  assign iflop_axi_vld_vec_0 = u_pipe_io_vo & _T_145; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@81.4]
  assign iflop_axi_vld_vec_1 = u_pipe_io_vo & _T_148; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@83.4]
  assign iflop_axi_vld_vec_2 = u_pipe_io_vo & _T_151; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@85.4]
  assign iflop_axi_vld_vec_3 = u_pipe_io_vo & _T_154; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@87.4]
  assign iflop_axi_vld_vec_4 = u_pipe_io_vo & _T_157; // @[NV_NVDLA_MCIF_WRITE_eg.scala 57:87:@89.4]
  assign cq_rd_len_vec_0 = io_cq_rd_pd_0_bits[2:1]; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@96.4]
  assign cq_rd_len_vec_1 = io_cq_rd_pd_1_bits[2:1]; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@97.4]
  assign cq_rd_len_vec_2 = io_cq_rd_pd_2_bits[2:1]; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@98.4]
  assign cq_rd_len_vec_3 = io_cq_rd_pd_3_bits[2:1]; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@99.4]
  assign cq_rd_len_vec_4 = io_cq_rd_pd_4_bits[2:1]; // @[NV_NVDLA_MCIF_WRITE_eg.scala 58:88:@100.4]
  assign _T_212 = {iflop_axi_rdy_vec_4,iflop_axi_rdy_vec_3,iflop_axi_rdy_vec_2,iflop_axi_rdy_vec_1,iflop_axi_rdy_vec_0}; // @[NV_NVDLA_MCIF_WRITE_eg.scala 63:40:@115.4]
  assign iflop_axi_rdy = _T_212 != 5'h0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 63:47:@116.4]
  assign _GEN_0 = iflop_axi_vld_vec_0 ? cq_rd_len_vec_0 : 2'h0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 69:35:@122.4]
  assign _GEN_1 = iflop_axi_vld_vec_1 ? cq_rd_len_vec_1 : _GEN_0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 69:35:@125.4]
  assign _GEN_2 = iflop_axi_vld_vec_2 ? cq_rd_len_vec_2 : _GEN_1; // @[NV_NVDLA_MCIF_WRITE_eg.scala 69:35:@128.4]
  assign _GEN_3 = iflop_axi_vld_vec_3 ? cq_rd_len_vec_3 : _GEN_2; // @[NV_NVDLA_MCIF_WRITE_eg.scala 69:35:@131.4]
  assign client_cq_rd_ack_0 = io_cq_rd_pd_1_bits[0]; // @[NV_NVDLA_MCIF_WRITE_eg.scala 84:91:@153.4]
  assign _T_266 = io_cq_rd_pd_2_bits[0]; // @[NV_NVDLA_MCIF_WRITE_eg.scala 84:91:@154.4]
  assign _T_268 = io_cq_rd_pd_3_bits[0]; // @[NV_NVDLA_MCIF_WRITE_eg.scala 84:91:@155.4]
  assign _T_284 = io_cq_rd_pd_1_valid & client_cq_rd_ack_0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:74:@171.4]
  assign _T_285 = _T_284 & iflop_axi_vld_vec_1; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:94:@172.4]
  assign _T_331 = io_cq_rd_pd_2_valid & _T_266; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:74:@206.4]
  assign _T_332 = _T_331 & iflop_axi_vld_vec_2; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:94:@207.4]
  assign _T_378 = io_cq_rd_pd_3_valid & _T_268; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:74:@241.4]
  assign _T_379 = _T_378 & iflop_axi_vld_vec_3; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:94:@242.4]
  assign io_mcif2client_wr_rsp_complete_0 = _T_288; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:43:@175.4]
  assign io_mcif2client_wr_rsp_complete_1 = _T_335; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:43:@210.4]
  assign io_mcif2client_wr_rsp_complete_2 = _T_382; // @[NV_NVDLA_MCIF_WRITE_eg.scala 86:43:@245.4]
  assign io_cq_rd_pd_0_ready = u_pipe_io_vo & _T_145; // @[NV_NVDLA_MCIF_WRITE_eg.scala 60:30:@107.4]
  assign io_cq_rd_pd_1_ready = u_pipe_io_vo & _T_148; // @[NV_NVDLA_MCIF_WRITE_eg.scala 60:30:@108.4]
  assign io_cq_rd_pd_2_ready = u_pipe_io_vo & _T_151; // @[NV_NVDLA_MCIF_WRITE_eg.scala 60:30:@109.4]
  assign io_cq_rd_pd_3_ready = u_pipe_io_vo & _T_154; // @[NV_NVDLA_MCIF_WRITE_eg.scala 60:30:@110.4]
  assign io_cq_rd_pd_4_ready = u_pipe_io_vo & _T_157; // @[NV_NVDLA_MCIF_WRITE_eg.scala 60:30:@111.4]
  assign io_noc2mcif_axi_b_ready = u_pipe_io_ro; // @[NV_NVDLA_MCIF_WRITE_eg.scala 49:29:@61.4]
  assign io_eg2ig_axi_len_valid = u_pipe_io_vo & iflop_axi_rdy; // @[NV_NVDLA_MCIF_WRITE_eg.scala 65:28:@119.4]
  assign io_eg2ig_axi_len_bits = iflop_axi_vld_vec_4 ? cq_rd_len_vec_4 : _GEN_3; // @[NV_NVDLA_MCIF_WRITE_eg.scala 74:27:@137.4]
  assign u_pipe_reset = reset; // @[:@58.4]
  assign u_pipe_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_eg.scala 46:19:@59.4]
  assign u_pipe_io_ri = _T_212 != 5'h0; // @[NV_NVDLA_MCIF_WRITE_eg.scala 53:18:@63.4]
  assign u_pipe_io_di = io_noc2mcif_axi_b_bits_id[2:0]; // @[NV_NVDLA_MCIF_WRITE_eg.scala 50:18:@62.4]
  assign u_pipe_io_vi = io_noc2mcif_axi_b_valid; // @[NV_NVDLA_MCIF_WRITE_eg.scala 48:18:@60.4]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  _T_288 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_335 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_382 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_288 <= 1'h0;
    end else begin
      _T_288 <= _T_285;
    end
    if (reset) begin
      _T_335 <= 1'h0;
    end else begin
      _T_335 <= _T_332;
    end
    if (reset) begin
      _T_382 <= 1'h0;
    end else begin
      _T_382 <= _T_379;
    end
  end
endmodule
