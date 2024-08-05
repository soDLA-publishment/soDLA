module NV_NVDLA_IS_pipe( // @[:@3.2]
  input         reset, // @[:@5.4]
  input         io_clk, // @[:@6.4]
  output [65:0] io_dout, // @[:@6.4]
  output        io_vo, // @[:@6.4]
  input         io_ri, // @[:@6.4]
  input  [65:0] io_di, // @[:@6.4]
  input         io_vi, // @[:@6.4]
  output        io_ro // @[:@6.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@8.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@9.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@10.4]
  reg [31:0] _RAND_2;
  reg [65:0] skid_flop_di; // @[IS_pipe.scala 57:53:@11.4]
  reg [95:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@12.4]
  reg [31:0] _RAND_4;
  reg [65:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@13.4]
  reg [95:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@22.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@27.4]
  wire [65:0] _GEN_1; // @[IS_pipe.scala 76:29:@28.4]
  wire [65:0] skid_di; // @[IS_pipe.scala 79:19:@31.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@33.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@34.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@36.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@39.4]
  wire [65:0] _GEN_3; // @[IS_pipe.scala 87:29:@40.4]
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
  skid_flop_di = _RAND_3[65:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {3{`RANDOM}};
  pipe_skid_di = _RAND_5[65:0];
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
      skid_flop_di <= 66'h0;
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
      pipe_skid_di <= 66'h0;
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
module NV_NVDLA_BC_pipe( // @[:@97.2]
  input         reset, // @[:@99.4]
  input         io_clk, // @[:@100.4]
  input         io_vi, // @[:@100.4]
  output        io_ro, // @[:@100.4]
  input  [45:0] io_di, // @[:@100.4]
  output        io_vo, // @[:@100.4]
  input         io_ri, // @[:@100.4]
  output [45:0] io_dout // @[:@100.4]
);
  reg  pipe_valid; // @[BC_pipe.scala 50:29:@102.4]
  reg [31:0] _RAND_0;
  reg [45:0] pipe_data; // @[BC_pipe.scala 51:24:@103.4]
  reg [63:0] _RAND_1;
  wire  _T_24; // @[BC_pipe.scala 54:22:@105.4]
  wire  _T_25; // @[BC_pipe.scala 55:28:@107.4]
  wire  _T_27; // @[BC_pipe.scala 56:28:@110.4]
  assign _T_24 = io_ro ? io_vi : 1'h1; // @[BC_pipe.scala 54:22:@105.4]
  assign _T_25 = ~ pipe_valid; // @[BC_pipe.scala 55:28:@107.4]
  assign _T_27 = io_ro & io_vi; // @[BC_pipe.scala 56:28:@110.4]
  assign io_ro = io_ri | _T_25; // @[BC_pipe.scala 55:11:@109.4]
  assign io_vo = pipe_valid; // @[BC_pipe.scala 60:11:@114.4]
  assign io_dout = pipe_data; // @[BC_pipe.scala 61:13:@115.4]
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
  pipe_valid = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {2{`RANDOM}};
  pipe_data = _RAND_1[45:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      pipe_valid <= 1'h0;
    end else begin
      if (io_ro) begin
        pipe_valid <= io_vi;
      end else begin
        pipe_valid <= 1'h1;
      end
    end
    if (_T_27) begin
      pipe_data <= io_di;
    end
  end
endmodule
module NV_NVDLA_IS_pipe_2( // @[:@117.2]
  input         reset, // @[:@119.4]
  input         io_clk, // @[:@120.4]
  output [63:0] io_dout, // @[:@120.4]
  output        io_vo, // @[:@120.4]
  input         io_ri, // @[:@120.4]
  input  [63:0] io_di, // @[:@120.4]
  input         io_vi, // @[:@120.4]
  output        io_ro // @[:@120.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@122.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@123.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@124.4]
  reg [31:0] _RAND_2;
  reg [63:0] skid_flop_di; // @[IS_pipe.scala 57:53:@125.4]
  reg [63:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@126.4]
  reg [31:0] _RAND_4;
  reg [63:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@127.4]
  reg [63:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@136.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@141.4]
  wire [63:0] _GEN_1; // @[IS_pipe.scala 76:29:@142.4]
  wire [63:0] skid_di; // @[IS_pipe.scala 79:19:@145.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@147.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@148.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@150.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@153.4]
  wire [63:0] _GEN_3; // @[IS_pipe.scala 87:29:@154.4]
  assign _GEN_0 = skid_flop_ro ? io_vi : skid_flop_vi; // @[IS_pipe.scala 71:23:@136.4]
  assign _T_38 = skid_flop_ro & io_vi; // @[IS_pipe.scala 76:22:@141.4]
  assign _GEN_1 = _T_38 ? io_di : skid_flop_di; // @[IS_pipe.scala 76:29:@142.4]
  assign skid_di = skid_flop_ro ? io_di : skid_flop_di; // @[IS_pipe.scala 79:19:@145.4]
  assign _T_40 = ~ pipe_skid_vi; // @[IS_pipe.scala 81:32:@147.4]
  assign skid_ro = io_ri | _T_40; // @[IS_pipe.scala 81:29:@148.4]
  assign _GEN_2 = skid_ro ? _GEN_0 : pipe_skid_vi; // @[IS_pipe.scala 83:18:@150.4]
  assign _T_42 = skid_ro & _GEN_0; // @[IS_pipe.scala 87:18:@153.4]
  assign _GEN_3 = _T_42 ? skid_di : pipe_skid_di; // @[IS_pipe.scala 87:29:@154.4]
  assign io_dout = pipe_skid_di; // @[IS_pipe.scala 98:13:@162.4]
  assign io_vo = pipe_skid_vi; // @[IS_pipe.scala 97:11:@161.4]
  assign io_ro = ro_out; // @[IS_pipe.scala 96:11:@160.4]
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
  _RAND_3 = {2{`RANDOM}};
  skid_flop_di = _RAND_3[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {2{`RANDOM}};
  pipe_skid_di = _RAND_5[63:0];
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
      skid_flop_di <= 64'h0;
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
      pipe_skid_di <= 64'h0;
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
module NV_NVDLA_MCIF_WRITE_IG_bpt( // @[:@164.2]
  input         clock, // @[:@165.4]
  input         reset, // @[:@166.4]
  input         io_nvdla_core_clk, // @[:@167.4]
  input  [31:0] io_pwrbus_ram_pd, // @[:@167.4]
  input  [3:0]  io_axid, // @[:@167.4]
  output        io_dma2bpt_req_pd_ready, // @[:@167.4]
  input         io_dma2bpt_req_pd_valid, // @[:@167.4]
  input  [65:0] io_dma2bpt_req_pd_bits, // @[:@167.4]
  input         io_bpt2arb_cmd_pd_ready, // @[:@167.4]
  output        io_bpt2arb_cmd_pd_valid, // @[:@167.4]
  output [44:0] io_bpt2arb_cmd_pd_bits, // @[:@167.4]
  input         io_bpt2arb_dat_pd_ready, // @[:@167.4]
  output        io_bpt2arb_dat_pd_valid, // @[:@167.4]
  output [64:0] io_bpt2arb_dat_pd_bits // @[:@167.4]
);
  wire  pipe_p1_reset; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 47:25:@170.4]
  wire  pipe_p1_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 47:25:@170.4]
  wire [65:0] pipe_p1_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 47:25:@170.4]
  wire  pipe_p1_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 47:25:@170.4]
  wire  pipe_p1_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 47:25:@170.4]
  wire [65:0] pipe_p1_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 47:25:@170.4]
  wire  pipe_p1_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 47:25:@170.4]
  wire  pipe_p1_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 47:25:@170.4]
  wire  pipe_p2_reset; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 53:25:@177.4]
  wire  pipe_p2_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 53:25:@177.4]
  wire [65:0] pipe_p2_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 53:25:@177.4]
  wire  pipe_p2_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 53:25:@177.4]
  wire  pipe_p2_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 53:25:@177.4]
  wire [65:0] pipe_p2_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 53:25:@177.4]
  wire  pipe_p2_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 53:25:@177.4]
  wire  pipe_p2_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 53:25:@177.4]
  wire  pipe_p3_reset; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 76:25:@201.4]
  wire  pipe_p3_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 76:25:@201.4]
  wire  pipe_p3_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 76:25:@201.4]
  wire  pipe_p3_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 76:25:@201.4]
  wire [45:0] pipe_p3_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 76:25:@201.4]
  wire  pipe_p3_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 76:25:@201.4]
  wire  pipe_p3_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 76:25:@201.4]
  wire [45:0] pipe_p3_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 76:25:@201.4]
  wire  u_dfifo_reset; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 96:25:@222.4]
  wire  u_dfifo_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 96:25:@222.4]
  wire [63:0] u_dfifo_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 96:25:@222.4]
  wire  u_dfifo_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 96:25:@222.4]
  wire  u_dfifo_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 96:25:@222.4]
  wire [63:0] u_dfifo_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 96:25:@222.4]
  wire  u_dfifo_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 96:25:@222.4]
  wire  u_dfifo_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 96:25:@222.4]
  wire  _T_36; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 64:47:@185.4]
  wire  _T_38; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 64:73:@186.4]
  wire  ipipe_cmd_vld; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 64:35:@187.4]
  wire  dfifo_wr_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 65:35:@190.4]
  wire  ipipe_cmd_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 70:29:@193.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 79:19:@206.4]
  wire  _T_44; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 72:33:@195.4]
  wire  dfifo_wr_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 71:29:@194.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 99:19:@227.4]
  wire  _T_45; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 72:68:@196.4]
  reg [12:0] req_count; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 223:28:@287.4]
  reg [31:0] _RAND_0;
  wire  _T_53; // @[Bitwise.scala 72:15:@215.4]
  wire [45:0] _T_56; // @[Bitwise.scala 72:12:@216.4]
  wire [45:0] in_cmd_vld_pd; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 89:65:@217.4]
  wire [12:0] in_cmd_size; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 92:36:@219.4]
  wire  is_ltran; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 236:28:@305.4]
  reg [1:0] beat_count; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 159:29:@255.4]
  reg [31:0] _RAND_1;
  wire [2:0] out_size; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 138:24:@241.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 190:18:@270.4]
  wire [2:0] _GEN_10; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 169:33:@266.4]
  wire  is_last_beat; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 169:33:@266.4]
  wire  _T_51; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 88:28:@212.4]
  wire  bpt2arb_dat_accept; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 261:51:@325.4]
  wire [31:0] in_cmd_addr; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 91:36:@218.4]
  wire  in_cmd_require_ack; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 93:44:@220.4]
  wire [13:0] _T_64; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 132:34:@235.4]
  wire [13:0] _T_65; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 132:34:@236.4]
  reg  dat_en; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 135:25:@238.4]
  reg [31:0] _RAND_2;
  reg  cmd_en; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 145:25:@244.4]
  reg [31:0] _RAND_3;
  wire  _T_75; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 150:34:@250.6]
  wire  _GEN_0; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 150:49:@251.6]
  wire  _GEN_1; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 150:49:@251.6]
  wire  bpt2arb_cmd_accept; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 260:51:@323.4]
  wire  _GEN_2; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 146:29:@245.4]
  wire  _GEN_3; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 146:29:@245.4]
  wire [2:0] _T_82; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 165:38:@261.8]
  wire [1:0] _T_83; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 165:38:@262.8]
  wire [1:0] _GEN_4; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 161:27:@257.6]
  wire [1:0] _GEN_5; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 160:29:@256.4]
  reg [31:0] out_addr; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 196:49:@271.4]
  reg [31:0] _RAND_4;
  wire [2:0] ftran_size; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 108:26:@230.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 130:20:@233.4]
  wire [3:0] _T_91; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:53:@274.8]
  wire [6:0] _GEN_11; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:61:@275.8]
  wire [6:0] _T_93; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:61:@275.8]
  wire [31:0] _GEN_12; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:37:@276.8]
  wire [32:0] _T_94; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:37:@276.8]
  wire [3:0] _T_97; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 202:67:@280.8]
  wire [31:0] _GEN_13; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 202:34:@281.8]
  wire [32:0] _T_98; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 202:34:@281.8]
  wire  is_ftran; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 234:28:@299.4]
  wire [32:0] _GEN_6; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 198:23:@273.6]
  wire [32:0] _GEN_7; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 197:29:@272.4]
  wire [13:0] _T_105; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 229:36:@294.8]
  wire [12:0] _T_106; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 229:36:@295.8]
  wire [12:0] _GEN_8; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 225:23:@290.6]
  wire [12:0] _GEN_9; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 224:44:@289.4]
  wire [31:0] out_cmd_addr; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 239:27:@308.4]
  wire  out_cmd_require_ack; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 249:50:@309.4]
  wire [39:0] _T_116; // @[Cat.scala 30:58:@312.4]
  wire [4:0] _T_120; // @[Cat.scala 30:58:@316.4]
  wire [2:0] ltran_size; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 109:26:@231.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 131:20:@234.4]
  wire [12:0] mtran_num; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 110:25:@232.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 132:19:@237.4]
  NV_NVDLA_IS_pipe pipe_p1 ( // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 47:25:@170.4]
    .reset(pipe_p1_reset),
    .io_clk(pipe_p1_io_clk),
    .io_dout(pipe_p1_io_dout),
    .io_vo(pipe_p1_io_vo),
    .io_ri(pipe_p1_io_ri),
    .io_di(pipe_p1_io_di),
    .io_vi(pipe_p1_io_vi),
    .io_ro(pipe_p1_io_ro)
  );
  NV_NVDLA_IS_pipe pipe_p2 ( // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 53:25:@177.4]
    .reset(pipe_p2_reset),
    .io_clk(pipe_p2_io_clk),
    .io_dout(pipe_p2_io_dout),
    .io_vo(pipe_p2_io_vo),
    .io_ri(pipe_p2_io_ri),
    .io_di(pipe_p2_io_di),
    .io_vi(pipe_p2_io_vi),
    .io_ro(pipe_p2_io_ro)
  );
  NV_NVDLA_BC_pipe pipe_p3 ( // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 76:25:@201.4]
    .reset(pipe_p3_reset),
    .io_clk(pipe_p3_io_clk),
    .io_vi(pipe_p3_io_vi),
    .io_ro(pipe_p3_io_ro),
    .io_di(pipe_p3_io_di),
    .io_vo(pipe_p3_io_vo),
    .io_ri(pipe_p3_io_ri),
    .io_dout(pipe_p3_io_dout)
  );
  NV_NVDLA_IS_pipe_2 u_dfifo ( // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 96:25:@222.4]
    .reset(u_dfifo_reset),
    .io_clk(u_dfifo_io_clk),
    .io_dout(u_dfifo_io_dout),
    .io_vo(u_dfifo_io_vo),
    .io_ri(u_dfifo_io_ri),
    .io_di(u_dfifo_io_di),
    .io_vi(u_dfifo_io_vi),
    .io_ro(u_dfifo_io_ro)
  );
  assign _T_36 = pipe_p2_io_dout[65]; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 64:47:@185.4]
  assign _T_38 = _T_36 == 1'h0; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 64:73:@186.4]
  assign ipipe_cmd_vld = pipe_p2_io_vo & _T_38; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 64:35:@187.4]
  assign dfifo_wr_pvld = pipe_p2_io_vo & _T_36; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 65:35:@190.4]
  assign ipipe_cmd_rdy = pipe_p3_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 70:29:@193.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 79:19:@206.4]
  assign _T_44 = ipipe_cmd_vld & ipipe_cmd_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 72:33:@195.4]
  assign dfifo_wr_prdy = u_dfifo_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 71:29:@194.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 99:19:@227.4]
  assign _T_45 = dfifo_wr_pvld & dfifo_wr_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 72:68:@196.4]
  assign _T_53 = pipe_p3_io_vo; // @[Bitwise.scala 72:15:@215.4]
  assign _T_56 = _T_53 ? 46'h3fffffffffff : 46'h0; // @[Bitwise.scala 72:12:@216.4]
  assign in_cmd_vld_pd = _T_56 & pipe_p3_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 89:65:@217.4]
  assign in_cmd_size = in_cmd_vld_pd[44:32]; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 92:36:@219.4]
  assign is_ltran = req_count == in_cmd_size; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 236:28:@305.4]
  assign out_size = 3'h0; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 138:24:@241.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 190:18:@270.4]
  assign _GEN_10 = {{1'd0}, beat_count}; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 169:33:@266.4]
  assign is_last_beat = _GEN_10 == 3'h0; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 169:33:@266.4]
  assign _T_51 = is_ltran & is_last_beat; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 88:28:@212.4]
  assign bpt2arb_dat_accept = io_bpt2arb_dat_pd_valid & io_bpt2arb_dat_pd_ready; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 261:51:@325.4]
  assign in_cmd_addr = in_cmd_vld_pd[31:0]; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 91:36:@218.4]
  assign in_cmd_require_ack = in_cmd_vld_pd[45]; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 93:44:@220.4]
  assign _T_64 = in_cmd_size - 13'h1; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 132:34:@235.4]
  assign _T_65 = $unsigned(_T_64); // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 132:34:@236.4]
  assign _T_75 = bpt2arb_dat_accept & is_last_beat; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 150:34:@250.6]
  assign _GEN_0 = _T_75 ? 1'h1 : cmd_en; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 150:49:@251.6]
  assign _GEN_1 = _T_75 ? 1'h0 : dat_en; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 150:49:@251.6]
  assign bpt2arb_cmd_accept = io_bpt2arb_cmd_pd_valid & io_bpt2arb_cmd_pd_ready; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 260:51:@323.4]
  assign _GEN_2 = bpt2arb_cmd_accept ? 1'h0 : _GEN_0; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 146:29:@245.4]
  assign _GEN_3 = bpt2arb_cmd_accept ? 1'h1 : _GEN_1; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 146:29:@245.4]
  assign _T_82 = beat_count + 2'h1; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 165:38:@261.8]
  assign _T_83 = beat_count + 2'h1; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 165:38:@262.8]
  assign _GEN_4 = is_last_beat ? 2'h0 : _T_83; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 161:27:@257.6]
  assign _GEN_5 = bpt2arb_dat_accept ? _GEN_4 : beat_count; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 160:29:@256.4]
  assign ftran_size = 3'h0; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 108:26:@230.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 130:20:@233.4]
  assign _T_91 = out_size + 3'h1; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:53:@274.8]
  assign _GEN_11 = {{3'd0}, _T_91}; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:61:@275.8]
  assign _T_93 = _GEN_11 << 2'h3; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:61:@275.8]
  assign _GEN_12 = {{25'd0}, _T_93}; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:37:@276.8]
  assign _T_94 = in_cmd_addr + _GEN_12; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 199:37:@276.8]
  assign _T_97 = 4'h1 << 2'h3; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 202:67:@280.8]
  assign _GEN_13 = {{28'd0}, _T_97}; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 202:34:@281.8]
  assign _T_98 = out_addr + _GEN_13; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 202:34:@281.8]
  assign is_ftran = req_count == 13'h0; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 234:28:@299.4]
  assign _GEN_6 = is_ftran ? _T_94 : _T_98; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 198:23:@273.6]
  assign _GEN_7 = bpt2arb_cmd_accept ? _GEN_6 : {{1'd0}, out_addr}; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 197:29:@272.4]
  assign _T_105 = req_count + 13'h1; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 229:36:@294.8]
  assign _T_106 = req_count + 13'h1; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 229:36:@295.8]
  assign _GEN_8 = is_ltran ? 13'h0 : _T_106; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 225:23:@290.6]
  assign _GEN_9 = _T_75 ? _GEN_8 : req_count; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 224:44:@289.4]
  assign out_cmd_addr = is_ftran ? in_cmd_addr : out_addr; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 239:27:@308.4]
  assign out_cmd_require_ack = in_cmd_require_ack & is_ltran; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 249:50:@309.4]
  assign _T_116 = {3'h0,out_cmd_addr,out_cmd_require_ack,io_axid}; // @[Cat.scala 30:58:@312.4]
  assign _T_120 = {is_ftran,is_ltran,1'h0,2'h0}; // @[Cat.scala 30:58:@316.4]
  assign ltran_size = 3'h0; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 109:26:@231.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 131:20:@234.4]
  assign mtran_num = _T_65[12:0]; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 110:25:@232.4 NV_NVDLA_MCIF_WRITE_IG_bpt.scala 132:19:@237.4]
  assign io_dma2bpt_req_pd_ready = pipe_p1_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 50:29:@175.4]
  assign io_bpt2arb_cmd_pd_valid = cmd_en & pipe_p3_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 257:29:@321.4]
  assign io_bpt2arb_cmd_pd_bits = {_T_120,_T_116}; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 251:28:@318.4]
  assign io_bpt2arb_dat_pd_valid = dat_en & u_dfifo_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 258:29:@322.4]
  assign io_bpt2arb_dat_pd_bits = {u_dfifo_io_vo,u_dfifo_io_dout}; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 255:28:@320.4]
  assign pipe_p1_reset = reset; // @[:@172.4]
  assign pipe_p1_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 48:20:@173.4]
  assign pipe_p1_io_ri = pipe_p2_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 57:19:@182.4]
  assign pipe_p1_io_di = io_dma2bpt_req_pd_bits; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 51:19:@176.4]
  assign pipe_p1_io_vi = io_dma2bpt_req_pd_valid; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 49:19:@174.4]
  assign pipe_p2_reset = reset; // @[:@179.4]
  assign pipe_p2_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 54:20:@180.4]
  assign pipe_p2_io_ri = _T_44 | _T_45; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 61:19:@184.4]
  assign pipe_p2_io_di = pipe_p1_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 58:19:@183.4]
  assign pipe_p2_io_vi = pipe_p1_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 56:19:@181.4]
  assign pipe_p3_reset = reset; // @[:@203.4]
  assign pipe_p3_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 77:20:@204.4]
  assign pipe_p3_io_vi = pipe_p2_io_vo & _T_38; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 78:19:@205.4]
  assign pipe_p3_io_di = pipe_p2_io_dout[45:0]; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 80:19:@207.4]
  assign pipe_p3_io_ri = _T_51 & bpt2arb_dat_accept; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 82:19:@208.4]
  assign u_dfifo_reset = reset; // @[:@224.4]
  assign u_dfifo_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 97:20:@225.4]
  assign u_dfifo_io_ri = dat_en & io_bpt2arb_dat_pd_ready; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 102:19:@229.4]
  assign u_dfifo_io_di = pipe_p2_io_dout[63:0]; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 100:19:@228.4]
  assign u_dfifo_io_vi = pipe_p2_io_vo & _T_36; // @[NV_NVDLA_MCIF_WRITE_IG_bpt.scala 98:19:@226.4]
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
  req_count = _RAND_0[12:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  beat_count = _RAND_1[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  dat_en = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  cmd_en = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  out_addr = _RAND_4[31:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      req_count <= 13'h0;
    end else begin
      if (_T_75) begin
        if (is_ltran) begin
          req_count <= 13'h0;
        end else begin
          req_count <= _T_106;
        end
      end
    end
    if (reset) begin
      beat_count <= 2'h0;
    end else begin
      if (bpt2arb_dat_accept) begin
        if (is_last_beat) begin
          beat_count <= 2'h0;
        end else begin
          beat_count <= _T_83;
        end
      end
    end
    if (reset) begin
      dat_en <= 1'h0;
    end else begin
      if (bpt2arb_cmd_accept) begin
        dat_en <= 1'h1;
      end else begin
        if (_T_75) begin
          dat_en <= 1'h0;
        end
      end
    end
    if (reset) begin
      cmd_en <= 1'h1;
    end else begin
      if (bpt2arb_cmd_accept) begin
        cmd_en <= 1'h0;
      end else begin
        if (_T_75) begin
          cmd_en <= 1'h1;
        end
      end
    end
    if (reset) begin
      out_addr <= 32'h0;
    end else begin
      out_addr <= _GEN_7[31:0];
    end
  end
endmodule
