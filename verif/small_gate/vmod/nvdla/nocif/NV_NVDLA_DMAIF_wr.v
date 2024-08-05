module NV_NVDLA_IS_pipe( // @[:@3.2]
  input         reset, // @[:@5.4]
  input         io_clk, // @[:@6.4]
  output [66:0] io_dout, // @[:@6.4]
  output        io_vo, // @[:@6.4]
  input         io_ri, // @[:@6.4]
  input  [66:0] io_di, // @[:@6.4]
  input         io_vi, // @[:@6.4]
  output        io_ro // @[:@6.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@8.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@9.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@10.4]
  reg [31:0] _RAND_2;
  reg [66:0] skid_flop_di; // @[IS_pipe.scala 57:53:@11.4]
  reg [95:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@12.4]
  reg [31:0] _RAND_4;
  reg [66:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@13.4]
  reg [95:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@22.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@27.4]
  wire [66:0] _GEN_1; // @[IS_pipe.scala 76:29:@28.4]
  wire [66:0] skid_di; // @[IS_pipe.scala 79:19:@31.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@33.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@34.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@36.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@39.4]
  wire [66:0] _GEN_3; // @[IS_pipe.scala 87:29:@40.4]
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
  skid_flop_di = _RAND_3[66:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {3{`RANDOM}};
  pipe_skid_di = _RAND_5[66:0];
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
      skid_flop_di <= 67'h0;
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
      pipe_skid_di <= 67'h0;
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
module NV_NVDLA_DMAIF_wr( // @[:@50.2]
  input         clock, // @[:@51.4]
  input         reset, // @[:@52.4]
  input         io_nvdla_core_clk, // @[:@53.4]
  input         io_mcif_wr_req_pd_ready, // @[:@53.4]
  output        io_mcif_wr_req_pd_valid, // @[:@53.4]
  output [65:0] io_mcif_wr_req_pd_bits, // @[:@53.4]
  input         io_mcif_wr_rsp_complete, // @[:@53.4]
  output        io_dmaif_wr_req_pd_ready, // @[:@53.4]
  input         io_dmaif_wr_req_pd_valid, // @[:@53.4]
  input  [65:0] io_dmaif_wr_req_pd_bits, // @[:@53.4]
  output        io_dmaif_wr_rsp_complete, // @[:@53.4]
  input         io_reg2dp_dst_ram_type // @[:@53.4]
);
  wire  is_pipe1_reset; // @[NV_NVDLA_DMAIF_wr.scala 72:26:@65.4]
  wire  is_pipe1_io_clk; // @[NV_NVDLA_DMAIF_wr.scala 72:26:@65.4]
  wire [66:0] is_pipe1_io_dout; // @[NV_NVDLA_DMAIF_wr.scala 72:26:@65.4]
  wire  is_pipe1_io_vo; // @[NV_NVDLA_DMAIF_wr.scala 72:26:@65.4]
  wire  is_pipe1_io_ri; // @[NV_NVDLA_DMAIF_wr.scala 72:26:@65.4]
  wire [66:0] is_pipe1_io_di; // @[NV_NVDLA_DMAIF_wr.scala 72:26:@65.4]
  wire  is_pipe1_io_vi; // @[NV_NVDLA_DMAIF_wr.scala 72:26:@65.4]
  wire  is_pipe1_io_ro; // @[NV_NVDLA_DMAIF_wr.scala 72:26:@65.4]
  wire  mc_dma_wr_req_rdy; // @[NV_NVDLA_DMAIF_wr.scala 68:33:@60.4 NV_NVDLA_DMAIF_wr.scala 75:23:@70.4]
  wire  mc_wr_req_rdyi; // @[NV_NVDLA_DMAIF_wr.scala 69:41:@62.4]
  wire  _T_39; // @[NV_NVDLA_DMAIF_wr.scala 95:52:@75.4]
  wire  _T_41; // @[NV_NVDLA_DMAIF_wr.scala 95:62:@76.4]
  wire  _T_42; // @[NV_NVDLA_DMAIF_wr.scala 95:97:@77.4]
  wire  require_ack; // @[NV_NVDLA_DMAIF_wr.scala 95:71:@79.4]
  wire  _T_45; // @[NV_NVDLA_DMAIF_wr.scala 96:48:@80.4]
  wire  ack_raw_vld; // @[NV_NVDLA_DMAIF_wr.scala 96:62:@81.4]
  reg  ack_bot_vld; // @[NV_NVDLA_DMAIF_wr.scala 100:30:@83.4]
  reg [31:0] _RAND_0;
  reg  ack_bot_id; // @[NV_NVDLA_DMAIF_wr.scala 101:29:@84.4]
  reg [31:0] _RAND_1;
  wire  _T_51; // @[NV_NVDLA_DMAIF_wr.scala 102:38:@85.4]
  reg  ack_top_id; // @[NV_NVDLA_DMAIF_wr.scala 111:29:@95.4]
  reg [31:0] _RAND_2;
  reg  mc_dma_wr_rsp_complete; // @[NV_NVDLA_DMAIF_wr.scala 123:41:@109.4]
  reg [31:0] _RAND_3;
  reg  mc_pending; // @[NV_NVDLA_DMAIF_wr.scala 135:29:@114.4]
  reg [31:0] _RAND_4;
  wire  _T_76; // @[NV_NVDLA_DMAIF_wr.scala 146:71:@129.4]
  wire  mc_releasing; // @[NV_NVDLA_DMAIF_wr.scala 146:45:@130.4]
  reg  ack_top_vld; // @[NV_NVDLA_DMAIF_wr.scala 110:30:@94.4]
  reg [31:0] _RAND_5;
  wire  _T_58; // @[NV_NVDLA_DMAIF_wr.scala 113:35:@97.4]
  wire  ack_bot_rdy; // @[NV_NVDLA_DMAIF_wr.scala 113:32:@98.4]
  wire  ack_raw_rdy; // @[NV_NVDLA_DMAIF_wr.scala 102:35:@86.4]
  wire  _T_52; // @[NV_NVDLA_DMAIF_wr.scala 103:22:@87.4]
  wire  _GEN_0; // @[NV_NVDLA_DMAIF_wr.scala 103:36:@88.4]
  wire  _GEN_1; // @[NV_NVDLA_DMAIF_wr.scala 106:22:@91.4]
  wire  _T_60; // @[NV_NVDLA_DMAIF_wr.scala 114:22:@100.4]
  wire  _GEN_2; // @[NV_NVDLA_DMAIF_wr.scala 114:36:@101.4]
  wire  _GEN_3; // @[NV_NVDLA_DMAIF_wr.scala 117:22:@104.4]
  reg  dmaif_wr_rsp_complete_out; // @[NV_NVDLA_DMAIF_wr.scala 131:44:@111.4]
  reg [31:0] _RAND_6;
  wire  _T_69; // @[NV_NVDLA_DMAIF_wr.scala 136:21:@115.4]
  wire  _GEN_4; // @[NV_NVDLA_DMAIF_wr.scala 137:37:@117.6]
  wire  _GEN_5; // @[NV_NVDLA_DMAIF_wr.scala 142:25:@124.8]
  wire  _GEN_6; // @[NV_NVDLA_DMAIF_wr.scala 141:34:@123.6]
  wire  _GEN_7; // @[NV_NVDLA_DMAIF_wr.scala 136:29:@116.4]
  NV_NVDLA_IS_pipe is_pipe1 ( // @[NV_NVDLA_DMAIF_wr.scala 72:26:@65.4]
    .reset(is_pipe1_reset),
    .io_clk(is_pipe1_io_clk),
    .io_dout(is_pipe1_io_dout),
    .io_vo(is_pipe1_io_vo),
    .io_ri(is_pipe1_io_ri),
    .io_di(is_pipe1_io_di),
    .io_vi(is_pipe1_io_vi),
    .io_ro(is_pipe1_io_ro)
  );
  assign mc_dma_wr_req_rdy = is_pipe1_io_ro; // @[NV_NVDLA_DMAIF_wr.scala 68:33:@60.4 NV_NVDLA_DMAIF_wr.scala 75:23:@70.4]
  assign mc_wr_req_rdyi = mc_dma_wr_req_rdy & io_reg2dp_dst_ram_type; // @[NV_NVDLA_DMAIF_wr.scala 69:41:@62.4]
  assign _T_39 = io_dmaif_wr_req_pd_bits[65]; // @[NV_NVDLA_DMAIF_wr.scala 95:52:@75.4]
  assign _T_41 = _T_39 == 1'h0; // @[NV_NVDLA_DMAIF_wr.scala 95:62:@76.4]
  assign _T_42 = io_dmaif_wr_req_pd_bits[45]; // @[NV_NVDLA_DMAIF_wr.scala 95:97:@77.4]
  assign require_ack = _T_41 & _T_42; // @[NV_NVDLA_DMAIF_wr.scala 95:71:@79.4]
  assign _T_45 = io_dmaif_wr_req_pd_valid & mc_wr_req_rdyi; // @[NV_NVDLA_DMAIF_wr.scala 96:48:@80.4]
  assign ack_raw_vld = _T_45 & require_ack; // @[NV_NVDLA_DMAIF_wr.scala 96:62:@81.4]
  assign _T_51 = ~ ack_bot_vld; // @[NV_NVDLA_DMAIF_wr.scala 102:38:@85.4]
  assign _T_76 = mc_dma_wr_rsp_complete | mc_pending; // @[NV_NVDLA_DMAIF_wr.scala 146:71:@129.4]
  assign mc_releasing = ack_top_id & _T_76; // @[NV_NVDLA_DMAIF_wr.scala 146:45:@130.4]
  assign _T_58 = ~ ack_top_vld; // @[NV_NVDLA_DMAIF_wr.scala 113:35:@97.4]
  assign ack_bot_rdy = mc_releasing | _T_58; // @[NV_NVDLA_DMAIF_wr.scala 113:32:@98.4]
  assign ack_raw_rdy = ack_bot_rdy | _T_51; // @[NV_NVDLA_DMAIF_wr.scala 102:35:@86.4]
  assign _T_52 = ack_raw_vld & ack_raw_rdy; // @[NV_NVDLA_DMAIF_wr.scala 103:22:@87.4]
  assign _GEN_0 = _T_52 ? io_reg2dp_dst_ram_type : ack_bot_id; // @[NV_NVDLA_DMAIF_wr.scala 103:36:@88.4]
  assign _GEN_1 = ack_raw_rdy ? ack_raw_vld : ack_bot_vld; // @[NV_NVDLA_DMAIF_wr.scala 106:22:@91.4]
  assign _T_60 = ack_bot_vld & ack_bot_rdy; // @[NV_NVDLA_DMAIF_wr.scala 114:22:@100.4]
  assign _GEN_2 = _T_60 ? ack_bot_id : ack_top_id; // @[NV_NVDLA_DMAIF_wr.scala 114:36:@101.4]
  assign _GEN_3 = ack_bot_rdy ? ack_bot_vld : ack_top_vld; // @[NV_NVDLA_DMAIF_wr.scala 117:22:@104.4]
  assign _T_69 = ack_top_id == 1'h0; // @[NV_NVDLA_DMAIF_wr.scala 136:21:@115.4]
  assign _GEN_4 = mc_dma_wr_rsp_complete ? 1'h1 : mc_pending; // @[NV_NVDLA_DMAIF_wr.scala 137:37:@117.6]
  assign _GEN_5 = mc_pending ? 1'h0 : mc_pending; // @[NV_NVDLA_DMAIF_wr.scala 142:25:@124.8]
  assign _GEN_6 = ack_top_id ? _GEN_5 : mc_pending; // @[NV_NVDLA_DMAIF_wr.scala 141:34:@123.6]
  assign _GEN_7 = _T_69 ? _GEN_4 : _GEN_6; // @[NV_NVDLA_DMAIF_wr.scala 136:29:@116.4]
  assign io_mcif_wr_req_pd_valid = is_pipe1_io_vo; // @[NV_NVDLA_DMAIF_wr.scala 77:29:@72.4]
  assign io_mcif_wr_req_pd_bits = is_pipe1_io_dout[65:0]; // @[NV_NVDLA_DMAIF_wr.scala 79:28:@74.4]
  assign io_dmaif_wr_req_pd_ready = mc_dma_wr_req_rdy & io_reg2dp_dst_ram_type; // @[NV_NVDLA_DMAIF_wr.scala 70:30:@64.4]
  assign io_dmaif_wr_rsp_complete = dmaif_wr_rsp_complete_out; // @[NV_NVDLA_DMAIF_wr.scala 133:30:@113.4]
  assign is_pipe1_reset = reset; // @[:@67.4]
  assign is_pipe1_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_DMAIF_wr.scala 73:21:@68.4]
  assign is_pipe1_io_ri = io_mcif_wr_req_pd_ready; // @[NV_NVDLA_DMAIF_wr.scala 78:20:@73.4]
  assign is_pipe1_io_di = {{1'd0}, io_dmaif_wr_req_pd_bits}; // @[NV_NVDLA_DMAIF_wr.scala 76:20:@71.4]
  assign is_pipe1_io_vi = io_dmaif_wr_req_pd_valid & io_reg2dp_dst_ram_type; // @[NV_NVDLA_DMAIF_wr.scala 74:20:@69.4]
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
  ack_bot_vld = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  ack_bot_id = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ack_top_id = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  mc_dma_wr_rsp_complete = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  mc_pending = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  ack_top_vld = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  dmaif_wr_rsp_complete_out = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      ack_bot_vld <= 1'h0;
    end else begin
      if (ack_raw_rdy) begin
        ack_bot_vld <= ack_raw_vld;
      end
    end
    if (reset) begin
      ack_bot_id <= 1'h0;
    end else begin
      if (_T_52) begin
        ack_bot_id <= io_reg2dp_dst_ram_type;
      end
    end
    if (reset) begin
      ack_top_id <= 1'h0;
    end else begin
      if (_T_60) begin
        ack_top_id <= ack_bot_id;
      end
    end
    if (reset) begin
      mc_dma_wr_rsp_complete <= 1'h0;
    end else begin
      mc_dma_wr_rsp_complete <= io_mcif_wr_rsp_complete;
    end
    if (reset) begin
      mc_pending <= 1'h0;
    end else begin
      if (_T_69) begin
        if (mc_dma_wr_rsp_complete) begin
          mc_pending <= 1'h1;
        end
      end else begin
        if (ack_top_id) begin
          if (mc_pending) begin
            mc_pending <= 1'h0;
          end
        end
      end
    end
    if (reset) begin
      ack_top_vld <= 1'h0;
    end else begin
      if (ack_bot_rdy) begin
        ack_top_vld <= ack_bot_vld;
      end
    end
    if (reset) begin
      dmaif_wr_rsp_complete_out <= 1'h0;
    end else begin
      dmaif_wr_rsp_complete_out <= mc_releasing;
    end
  end
endmodule
