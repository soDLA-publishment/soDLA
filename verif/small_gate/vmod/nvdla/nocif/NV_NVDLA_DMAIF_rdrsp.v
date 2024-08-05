module NV_NVDLA_IS_pipe( // @[:@3.2]
  input         reset, // @[:@5.4]
  input         io_clk, // @[:@6.4]
  output [64:0] io_dout, // @[:@6.4]
  output        io_vo, // @[:@6.4]
  input         io_ri, // @[:@6.4]
  input  [64:0] io_di, // @[:@6.4]
  input         io_vi, // @[:@6.4]
  output        io_ro // @[:@6.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@8.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@9.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@10.4]
  reg [31:0] _RAND_2;
  reg [64:0] skid_flop_di; // @[IS_pipe.scala 57:31:@11.4]
  reg [95:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@12.4]
  reg [31:0] _RAND_4;
  reg [64:0] pipe_skid_di; // @[IS_pipe.scala 59:31:@13.4]
  reg [95:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@22.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@27.4]
  wire [64:0] _GEN_1; // @[IS_pipe.scala 76:29:@28.4]
  wire [64:0] skid_di; // @[IS_pipe.scala 79:19:@31.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@33.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@34.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@36.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@39.4]
  wire [64:0] _GEN_3; // @[IS_pipe.scala 87:29:@40.4]
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
  _RAND_3 = {3{`RANDOM}};
  skid_flop_di = _RAND_3[64:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {3{`RANDOM}};
  pipe_skid_di = _RAND_5[64:0];
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
      skid_flop_di <= 65'h0;
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
      pipe_skid_di <= 65'h0;
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
module NV_NVDLA_DMAIF_rdrsp( // @[:@97.2]
  input         clock, // @[:@98.4]
  input         reset, // @[:@99.4]
  input         io_nvdla_core_clk, // @[:@100.4]
  output        io_mcif_rd_rsp_pd_ready, // @[:@100.4]
  input         io_mcif_rd_rsp_pd_valid, // @[:@100.4]
  input  [64:0] io_mcif_rd_rsp_pd_bits, // @[:@100.4]
  input         io_dmaif_rd_rsp_pd_ready, // @[:@100.4]
  output        io_dmaif_rd_rsp_pd_valid, // @[:@100.4]
  output [64:0] io_dmaif_rd_rsp_pd_bits // @[:@100.4]
);
  wire  is_pipe0_reset; // @[NV_NVDLA_DMAIF_rdrsp.scala 50:26:@105.4]
  wire  is_pipe0_io_clk; // @[NV_NVDLA_DMAIF_rdrsp.scala 50:26:@105.4]
  wire [64:0] is_pipe0_io_dout; // @[NV_NVDLA_DMAIF_rdrsp.scala 50:26:@105.4]
  wire  is_pipe0_io_vo; // @[NV_NVDLA_DMAIF_rdrsp.scala 50:26:@105.4]
  wire  is_pipe0_io_ri; // @[NV_NVDLA_DMAIF_rdrsp.scala 50:26:@105.4]
  wire [64:0] is_pipe0_io_di; // @[NV_NVDLA_DMAIF_rdrsp.scala 50:26:@105.4]
  wire  is_pipe0_io_vi; // @[NV_NVDLA_DMAIF_rdrsp.scala 50:26:@105.4]
  wire  is_pipe0_io_ro; // @[NV_NVDLA_DMAIF_rdrsp.scala 50:26:@105.4]
  wire  is_pipe2_reset; // @[NV_NVDLA_DMAIF_rdrsp.scala 86:26:@118.4]
  wire  is_pipe2_io_clk; // @[NV_NVDLA_DMAIF_rdrsp.scala 86:26:@118.4]
  wire [64:0] is_pipe2_io_dout; // @[NV_NVDLA_DMAIF_rdrsp.scala 86:26:@118.4]
  wire  is_pipe2_io_vo; // @[NV_NVDLA_DMAIF_rdrsp.scala 86:26:@118.4]
  wire  is_pipe2_io_ri; // @[NV_NVDLA_DMAIF_rdrsp.scala 86:26:@118.4]
  wire [64:0] is_pipe2_io_di; // @[NV_NVDLA_DMAIF_rdrsp.scala 86:26:@118.4]
  wire  is_pipe2_io_vi; // @[NV_NVDLA_DMAIF_rdrsp.scala 86:26:@118.4]
  wire  is_pipe2_io_ro; // @[NV_NVDLA_DMAIF_rdrsp.scala 86:26:@118.4]
  wire  _T_28; // @[Bitwise.scala 72:15:@114.4]
  wire [64:0] _T_31; // @[Bitwise.scala 72:12:@115.4]
  NV_NVDLA_IS_pipe is_pipe0 ( // @[NV_NVDLA_DMAIF_rdrsp.scala 50:26:@105.4]
    .reset(is_pipe0_reset),
    .io_clk(is_pipe0_io_clk),
    .io_dout(is_pipe0_io_dout),
    .io_vo(is_pipe0_io_vo),
    .io_ri(is_pipe0_io_ri),
    .io_di(is_pipe0_io_di),
    .io_vi(is_pipe0_io_vi),
    .io_ro(is_pipe0_io_ro)
  );
  NV_NVDLA_IS_pipe is_pipe2 ( // @[NV_NVDLA_DMAIF_rdrsp.scala 86:26:@118.4]
    .reset(is_pipe2_reset),
    .io_clk(is_pipe2_io_clk),
    .io_dout(is_pipe2_io_dout),
    .io_vo(is_pipe2_io_vo),
    .io_ri(is_pipe2_io_ri),
    .io_di(is_pipe2_io_di),
    .io_vi(is_pipe2_io_vi),
    .io_ro(is_pipe2_io_ro)
  );
  assign _T_28 = is_pipe0_io_vo; // @[Bitwise.scala 72:15:@114.4]
  assign _T_31 = _T_28 ? 65'h1ffffffffffffffff : 65'h0; // @[Bitwise.scala 72:12:@115.4]
  assign io_mcif_rd_rsp_pd_ready = is_pipe0_io_ro; // @[NV_NVDLA_DMAIF_rdrsp.scala 53:29:@110.4]
  assign io_dmaif_rd_rsp_pd_valid = is_pipe2_io_vo; // @[NV_NVDLA_DMAIF_rdrsp.scala 91:30:@125.4]
  assign io_dmaif_rd_rsp_pd_bits = is_pipe2_io_dout; // @[NV_NVDLA_DMAIF_rdrsp.scala 93:28:@127.4]
  assign is_pipe0_reset = reset; // @[:@107.4]
  assign is_pipe0_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_DMAIF_rdrsp.scala 51:21:@108.4]
  assign is_pipe0_io_ri = is_pipe2_io_ro; // @[NV_NVDLA_DMAIF_rdrsp.scala 56:20:@112.4]
  assign is_pipe0_io_di = io_mcif_rd_rsp_pd_bits; // @[NV_NVDLA_DMAIF_rdrsp.scala 54:20:@111.4]
  assign is_pipe0_io_vi = io_mcif_rd_rsp_pd_valid; // @[NV_NVDLA_DMAIF_rdrsp.scala 52:20:@109.4]
  assign is_pipe2_reset = reset; // @[:@120.4]
  assign is_pipe2_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_DMAIF_rdrsp.scala 87:21:@121.4]
  assign is_pipe2_io_ri = io_dmaif_rd_rsp_pd_ready; // @[NV_NVDLA_DMAIF_rdrsp.scala 92:20:@126.4]
  assign is_pipe2_io_di = _T_31 & is_pipe0_io_dout; // @[NV_NVDLA_DMAIF_rdrsp.scala 90:20:@124.4]
  assign is_pipe2_io_vi = is_pipe0_io_vo; // @[NV_NVDLA_DMAIF_rdrsp.scala 88:20:@122.4]
endmodule
