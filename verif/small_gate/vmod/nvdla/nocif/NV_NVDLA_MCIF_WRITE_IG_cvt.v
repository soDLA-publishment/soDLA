module NV_NVDLA_BC_pipe( // @[:@3.2]
  input         reset, // @[:@5.4]
  input         io_clk, // @[:@6.4]
  input         io_vi, // @[:@6.4]
  output        io_ro, // @[:@6.4]
  input  [44:0] io_di, // @[:@6.4]
  output        io_vo, // @[:@6.4]
  input         io_ri, // @[:@6.4]
  output [44:0] io_dout // @[:@6.4]
);
  reg  pipe_valid; // @[BC_pipe.scala 50:29:@8.4]
  reg [31:0] _RAND_0;
  reg [44:0] pipe_data; // @[BC_pipe.scala 51:50:@9.4]
  reg [63:0] _RAND_1;
  wire  _T_25; // @[BC_pipe.scala 54:22:@11.4]
  wire  _T_26; // @[BC_pipe.scala 55:28:@13.4]
  wire  _T_28; // @[BC_pipe.scala 56:28:@16.4]
  wire [44:0] _T_29; // @[BC_pipe.scala 56:21:@17.4]
  assign _T_25 = io_ro ? io_vi : 1'h1; // @[BC_pipe.scala 54:22:@11.4]
  assign _T_26 = ~ pipe_valid; // @[BC_pipe.scala 55:28:@13.4]
  assign _T_28 = io_ro & io_vi; // @[BC_pipe.scala 56:28:@16.4]
  assign _T_29 = _T_28 ? io_di : pipe_data; // @[BC_pipe.scala 56:21:@17.4]
  assign io_ro = io_ri | _T_26; // @[BC_pipe.scala 55:11:@15.4]
  assign io_vo = pipe_valid; // @[BC_pipe.scala 60:11:@20.4]
  assign io_dout = pipe_data; // @[BC_pipe.scala 61:13:@21.4]
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
  pipe_data = _RAND_1[44:0];
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
    if (reset) begin
      pipe_data <= 45'h0;
    end else begin
      if (_T_28) begin
        pipe_data <= io_di;
      end
    end
  end
endmodule
module NV_NVDLA_BC_pipe_1( // @[:@23.2]
  input         reset, // @[:@25.4]
  input         io_clk, // @[:@26.4]
  input         io_vi, // @[:@26.4]
  output        io_ro, // @[:@26.4]
  input  [64:0] io_di, // @[:@26.4]
  output        io_vo, // @[:@26.4]
  input         io_ri, // @[:@26.4]
  output [64:0] io_dout // @[:@26.4]
);
  reg  pipe_valid; // @[BC_pipe.scala 50:29:@28.4]
  reg [31:0] _RAND_0;
  reg [64:0] pipe_data; // @[BC_pipe.scala 51:50:@29.4]
  reg [95:0] _RAND_1;
  wire  _T_25; // @[BC_pipe.scala 54:22:@31.4]
  wire  _T_26; // @[BC_pipe.scala 55:28:@33.4]
  wire  _T_28; // @[BC_pipe.scala 56:28:@36.4]
  wire [64:0] _T_29; // @[BC_pipe.scala 56:21:@37.4]
  assign _T_25 = io_ro ? io_vi : 1'h1; // @[BC_pipe.scala 54:22:@31.4]
  assign _T_26 = ~ pipe_valid; // @[BC_pipe.scala 55:28:@33.4]
  assign _T_28 = io_ro & io_vi; // @[BC_pipe.scala 56:28:@36.4]
  assign _T_29 = _T_28 ? io_di : pipe_data; // @[BC_pipe.scala 56:21:@37.4]
  assign io_ro = io_ri | _T_26; // @[BC_pipe.scala 55:11:@35.4]
  assign io_vo = pipe_valid; // @[BC_pipe.scala 60:11:@40.4]
  assign io_dout = pipe_data; // @[BC_pipe.scala 61:13:@41.4]
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
  _RAND_1 = {3{`RANDOM}};
  pipe_data = _RAND_1[64:0];
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
    if (reset) begin
      pipe_data <= 65'h0;
    end else begin
      if (_T_28) begin
        pipe_data <= io_di;
      end
    end
  end
endmodule
module NV_COUNTER_STAGE_os( // @[:@43.2]
  input        reset, // @[:@45.4]
  input        io_clk, // @[:@46.4]
  input  [2:0] io_os_cnt_add, // @[:@46.4]
  input  [2:0] io_os_cnt_sub, // @[:@46.4]
  input        io_os_cnt_cen, // @[:@46.4]
  output [8:0] io_os_cnt_cur // @[:@46.4]
);
  wire  _T_15; // @[Perf_Counter.scala 244:32:@48.4]
  reg [8:0] _T_18; // @[Perf_Counter.scala 247:33:@49.4]
  reg [31:0] _RAND_0;
  wire [8:0] _GEN_1; // @[Perf_Counter.scala 254:34:@55.4]
  wire [9:0] _T_27; // @[Perf_Counter.scala 254:34:@55.4]
  wire [9:0] _GEN_2; // @[Perf_Counter.scala 254:51:@56.4]
  wire [10:0] _T_28; // @[Perf_Counter.scala 254:51:@56.4]
  wire [10:0] _T_29; // @[Perf_Counter.scala 254:51:@57.4]
  wire [10:0] _T_20; // @[Perf_Counter.scala 248:26:@50.4 Perf_Counter.scala 253:16:@54.4]
  wire [10:0] _T_30; // @[Perf_Counter.scala 255:22:@59.4]
  wire [10:0] _GEN_0; // @[Perf_Counter.scala 259:24:@62.4]
  assign _T_15 = io_os_cnt_add != io_os_cnt_sub; // @[Perf_Counter.scala 244:32:@48.4]
  assign _GEN_1 = {{6'd0}, io_os_cnt_add}; // @[Perf_Counter.scala 254:34:@55.4]
  assign _T_27 = _T_18 + _GEN_1; // @[Perf_Counter.scala 254:34:@55.4]
  assign _GEN_2 = {{7'd0}, io_os_cnt_sub}; // @[Perf_Counter.scala 254:51:@56.4]
  assign _T_28 = _T_27 - _GEN_2; // @[Perf_Counter.scala 254:51:@56.4]
  assign _T_29 = $unsigned(_T_28); // @[Perf_Counter.scala 254:51:@57.4]
  assign _T_20 = {{2'd0}, _T_18}; // @[Perf_Counter.scala 248:26:@50.4 Perf_Counter.scala 253:16:@54.4]
  assign _T_30 = _T_15 ? _T_29 : _T_20; // @[Perf_Counter.scala 255:22:@59.4]
  assign _GEN_0 = io_os_cnt_cen ? _T_30 : {{2'd0}, _T_18}; // @[Perf_Counter.scala 259:24:@62.4]
  assign io_os_cnt_cur = _T_18; // @[Perf_Counter.scala 263:19:@65.4]
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
  _T_18 = _RAND_0[8:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      _T_18 <= 9'h0;
    end else begin
      _T_18 <= _GEN_0[8:0];
    end
  end
endmodule
module NV_NVDLA_IS_pipe( // @[:@67.2]
  input         reset, // @[:@69.4]
  input         io_clk, // @[:@70.4]
  output [37:0] io_dout, // @[:@70.4]
  output        io_vo, // @[:@70.4]
  input         io_ri, // @[:@70.4]
  input  [37:0] io_di, // @[:@70.4]
  input         io_vi, // @[:@70.4]
  output        io_ro // @[:@70.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@72.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@73.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@74.4]
  reg [31:0] _RAND_2;
  reg [37:0] skid_flop_di; // @[IS_pipe.scala 57:53:@75.4]
  reg [63:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@76.4]
  reg [31:0] _RAND_4;
  reg [37:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@77.4]
  reg [63:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@86.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@91.4]
  wire [37:0] _GEN_1; // @[IS_pipe.scala 76:29:@92.4]
  wire [37:0] skid_di; // @[IS_pipe.scala 79:19:@95.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@97.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@98.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@100.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@103.4]
  wire [37:0] _GEN_3; // @[IS_pipe.scala 87:29:@104.4]
  assign _GEN_0 = skid_flop_ro ? io_vi : skid_flop_vi; // @[IS_pipe.scala 71:23:@86.4]
  assign _T_38 = skid_flop_ro & io_vi; // @[IS_pipe.scala 76:22:@91.4]
  assign _GEN_1 = _T_38 ? io_di : skid_flop_di; // @[IS_pipe.scala 76:29:@92.4]
  assign skid_di = skid_flop_ro ? io_di : skid_flop_di; // @[IS_pipe.scala 79:19:@95.4]
  assign _T_40 = ~ pipe_skid_vi; // @[IS_pipe.scala 81:32:@97.4]
  assign skid_ro = io_ri | _T_40; // @[IS_pipe.scala 81:29:@98.4]
  assign _GEN_2 = skid_ro ? _GEN_0 : pipe_skid_vi; // @[IS_pipe.scala 83:18:@100.4]
  assign _T_42 = skid_ro & _GEN_0; // @[IS_pipe.scala 87:18:@103.4]
  assign _GEN_3 = _T_42 ? skid_di : pipe_skid_di; // @[IS_pipe.scala 87:29:@104.4]
  assign io_dout = pipe_skid_di; // @[IS_pipe.scala 98:13:@112.4]
  assign io_vo = pipe_skid_vi; // @[IS_pipe.scala 97:11:@111.4]
  assign io_ro = ro_out; // @[IS_pipe.scala 96:11:@110.4]
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
  skid_flop_di = _RAND_3[37:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {2{`RANDOM}};
  pipe_skid_di = _RAND_5[37:0];
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
      skid_flop_di <= 38'h0;
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
      pipe_skid_di <= 38'h0;
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
module NV_NVDLA_IS_pipe_1( // @[:@114.2]
  input         reset, // @[:@116.4]
  input         io_clk, // @[:@117.4]
  output [72:0] io_dout, // @[:@117.4]
  output        io_vo, // @[:@117.4]
  input         io_ri, // @[:@117.4]
  input  [72:0] io_di, // @[:@117.4]
  input         io_vi, // @[:@117.4]
  output        io_ro // @[:@117.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@119.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@120.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@121.4]
  reg [31:0] _RAND_2;
  reg [72:0] skid_flop_di; // @[IS_pipe.scala 57:53:@122.4]
  reg [95:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@123.4]
  reg [31:0] _RAND_4;
  reg [72:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@124.4]
  reg [95:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@133.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@138.4]
  wire [72:0] _GEN_1; // @[IS_pipe.scala 76:29:@139.4]
  wire [72:0] skid_di; // @[IS_pipe.scala 79:19:@142.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@144.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@145.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@147.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@150.4]
  wire [72:0] _GEN_3; // @[IS_pipe.scala 87:29:@151.4]
  assign _GEN_0 = skid_flop_ro ? io_vi : skid_flop_vi; // @[IS_pipe.scala 71:23:@133.4]
  assign _T_38 = skid_flop_ro & io_vi; // @[IS_pipe.scala 76:22:@138.4]
  assign _GEN_1 = _T_38 ? io_di : skid_flop_di; // @[IS_pipe.scala 76:29:@139.4]
  assign skid_di = skid_flop_ro ? io_di : skid_flop_di; // @[IS_pipe.scala 79:19:@142.4]
  assign _T_40 = ~ pipe_skid_vi; // @[IS_pipe.scala 81:32:@144.4]
  assign skid_ro = io_ri | _T_40; // @[IS_pipe.scala 81:29:@145.4]
  assign _GEN_2 = skid_ro ? _GEN_0 : pipe_skid_vi; // @[IS_pipe.scala 83:18:@147.4]
  assign _T_42 = skid_ro & _GEN_0; // @[IS_pipe.scala 87:18:@150.4]
  assign _GEN_3 = _T_42 ? skid_di : pipe_skid_di; // @[IS_pipe.scala 87:29:@151.4]
  assign io_dout = pipe_skid_di; // @[IS_pipe.scala 98:13:@159.4]
  assign io_vo = pipe_skid_vi; // @[IS_pipe.scala 97:11:@158.4]
  assign io_ro = ro_out; // @[IS_pipe.scala 96:11:@157.4]
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
  skid_flop_di = _RAND_3[72:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {3{`RANDOM}};
  pipe_skid_di = _RAND_5[72:0];
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
      skid_flop_di <= 73'h0;
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
      pipe_skid_di <= 73'h0;
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
module NV_NVDLA_MCIF_WRITE_IG_cvt( // @[:@161.2]
  input         clock, // @[:@162.4]
  input         reset, // @[:@163.4]
  input         io_nvdla_core_clk, // @[:@164.4]
  output        io_spt2cvt_cmd_pd_ready, // @[:@164.4]
  input         io_spt2cvt_cmd_pd_valid, // @[:@164.4]
  input  [44:0] io_spt2cvt_cmd_pd_bits, // @[:@164.4]
  output        io_spt2cvt_dat_pd_ready, // @[:@164.4]
  input         io_spt2cvt_dat_pd_valid, // @[:@164.4]
  input  [64:0] io_spt2cvt_dat_pd_bits, // @[:@164.4]
  input         io_cq_wr_pd_ready, // @[:@164.4]
  output        io_cq_wr_pd_valid, // @[:@164.4]
  output [2:0]  io_cq_wr_pd_bits, // @[:@164.4]
  output [4:0]  io_cq_wr_thread_id, // @[:@164.4]
  input         io_mcif2noc_axi_aw_ready, // @[:@164.4]
  output        io_mcif2noc_axi_aw_valid, // @[:@164.4]
  output [7:0]  io_mcif2noc_axi_aw_bits_id, // @[:@164.4]
  output [3:0]  io_mcif2noc_axi_aw_bits_len, // @[:@164.4]
  output [63:0] io_mcif2noc_axi_aw_bits_addr, // @[:@164.4]
  input         io_mcif2noc_axi_w_ready, // @[:@164.4]
  output        io_mcif2noc_axi_w_valid, // @[:@164.4]
  output [63:0] io_mcif2noc_axi_w_bits_data, // @[:@164.4]
  output [7:0]  io_mcif2noc_axi_w_bits_strb, // @[:@164.4]
  output        io_mcif2noc_axi_w_bits_last, // @[:@164.4]
  input         io_eg2ig_axi_len_valid, // @[:@164.4]
  input  [1:0]  io_eg2ig_axi_len_bits, // @[:@164.4]
  input  [7:0]  io_reg2dp_wr_os_cnt // @[:@164.4]
);
  wire  pipe_p1_reset; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 54:25:@167.4]
  wire  pipe_p1_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 54:25:@167.4]
  wire  pipe_p1_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 54:25:@167.4]
  wire  pipe_p1_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 54:25:@167.4]
  wire [44:0] pipe_p1_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 54:25:@167.4]
  wire  pipe_p1_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 54:25:@167.4]
  wire  pipe_p1_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 54:25:@167.4]
  wire [44:0] pipe_p1_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 54:25:@167.4]
  wire  pipe_p2_reset; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 64:25:@176.4]
  wire  pipe_p2_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 64:25:@176.4]
  wire  pipe_p2_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 64:25:@176.4]
  wire  pipe_p2_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 64:25:@176.4]
  wire [64:0] pipe_p2_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 64:25:@176.4]
  wire  pipe_p2_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 64:25:@176.4]
  wire  pipe_p2_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 64:25:@176.4]
  wire [64:0] pipe_p2_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 64:25:@176.4]
  wire  perf_os_reset; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 149:25:@266.4]
  wire  perf_os_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 149:25:@266.4]
  wire [2:0] perf_os_io_os_cnt_add; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 149:25:@266.4]
  wire [2:0] perf_os_io_os_cnt_sub; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 149:25:@266.4]
  wire  perf_os_io_os_cnt_cen; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 149:25:@266.4]
  wire [8:0] perf_os_io_os_cnt_cur; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 149:25:@266.4]
  wire  pipe_p3_reset; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 161:25:@278.4]
  wire  pipe_p3_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 161:25:@278.4]
  wire [37:0] pipe_p3_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 161:25:@278.4]
  wire  pipe_p3_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 161:25:@278.4]
  wire  pipe_p3_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 161:25:@278.4]
  wire [37:0] pipe_p3_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 161:25:@278.4]
  wire  pipe_p3_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 161:25:@278.4]
  wire  pipe_p3_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 161:25:@278.4]
  wire  pipe_p4_reset; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 174:25:@293.4]
  wire  pipe_p4_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 174:25:@293.4]
  wire [72:0] pipe_p4_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 174:25:@293.4]
  wire  pipe_p4_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 174:25:@293.4]
  wire  pipe_p4_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 174:25:@293.4]
  wire [72:0] pipe_p4_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 174:25:@293.4]
  wire  pipe_p4_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 174:25:@293.4]
  wire  pipe_p4_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 174:25:@293.4]
  wire [8:0] os_cnt; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 124:22:@240.4 NV_NVDLA_MCIF_WRITE_IG_cvt.scala 154:12:@273.4]
  wire  _T_98; // @[Bitwise.scala 72:15:@198.4]
  wire [44:0] _T_101; // @[Bitwise.scala 72:12:@199.4]
  wire [44:0] cmd_vld_pd; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 85:61:@200.4]
  wire [2:0] cmd_size; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 90:30:@204.4]
  wire [1:0] axi_len; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 101:27:@212.4]
  wire [2:0] _T_127; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 125:47:@241.4]
  wire [2:0] os_inp_add_nxt; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 125:29:@242.4]
  wire [8:0] _GEN_3; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:29:@251.4]
  wire [9:0] _T_136; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:29:@251.4]
  reg  eg2ig_axi_vld_d; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 126:34:@243.4]
  reg [31:0] _RAND_0;
  reg [1:0] eg2ig_axi_len_d; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 127:34:@244.4]
  reg [31:0] _RAND_1;
  wire [2:0] _T_134; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 134:63:@249.4]
  wire [2:0] os_inp_sub_nxt; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 134:29:@250.4]
  wire [9:0] _GEN_4; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:47:@252.4]
  wire [10:0] _T_137; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:47:@252.4]
  wire [10:0] os_inp_nxt; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:47:@253.4]
  wire [8:0] wr_os_cnt_ext; // @[Cat.scala 30:58:@262.4]
  wire [9:0] _T_148; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 147:48:@263.4]
  wire [10:0] _GEN_5; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 147:31:@264.4]
  wire  os_cnt_full; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 147:31:@264.4]
  wire  _T_88; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 74:32:@185.4]
  wire  os_cmd_vld; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 74:30:@186.4]
  wire  axi_cmd_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 139:27:@255.4 NV_NVDLA_MCIF_WRITE_IG_cvt.scala 164:17:@283.4]
  wire  axi_dat_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 77:27:@188.4 NV_NVDLA_MCIF_WRITE_IG_cvt.scala 177:17:@298.4]
  wire  axi_both_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 203:37:@321.4]
  wire  all_downs_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 204:40:@322.4]
  wire  _T_92; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 80:46:@190.4]
  reg [1:0] beat_count; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 105:29:@215.4]
  reg [31:0] _RAND_2;
  wire  is_first_beat; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 113:34:@230.4]
  wire  _T_94; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 82:30:@193.4]
  wire  _T_95; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 82:40:@194.4]
  wire [3:0] cmd_axid; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 87:30:@201.4]
  wire  cmd_require_ack; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 88:37:@202.4]
  wire [31:0] cmd_addr; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 89:30:@203.4]
  wire  cmd_ltran; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 94:31:@208.4]
  wire [63:0] dat_data; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 98:26:@210.4]
  wire  dat_mask; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 99:26:@211.4]
  wire  _T_102; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 103:43:@213.4]
  wire  is_first_cmd_dat_vld; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 103:53:@214.4]
  wire  _T_105; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 106:31:@216.4]
  wire  _T_107; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 109:27:@221.6]
  wire  _T_108; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 109:36:@222.6]
  wire  _T_109; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 109:46:@223.6]
  wire [2:0] _T_111; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 110:34:@225.8]
  wire [2:0] _T_112; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 110:34:@226.8]
  wire [1:0] _T_113; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 110:34:@227.8]
  wire [1:0] _GEN_0; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 109:60:@224.6]
  wire [1:0] _GEN_1; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 106:47:@217.4]
  wire  is_single_beat; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 114:35:@232.4]
  wire  _T_118; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 115:36:@233.4]
  wire  _T_121; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 115:67:@235.4]
  wire  is_last_beat; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 115:44:@236.4]
  wire [7:0] axi_strb; // @[Bitwise.scala 72:12:@239.4]
  wire [1:0] _GEN_2; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 130:33:@246.4]
  wire  _T_150; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 159:41:@274.4]
  wire  axi_cmd_vld; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 159:61:@275.4]
  wire  os_cnt_add_en; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 140:37:@256.4]
  wire  _T_153; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 172:34:@287.4]
  wire  _T_154; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 172:64:@288.4]
  wire  _T_155; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 172:84:@289.4]
  wire  _T_156; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 172:49:@290.4]
  wire [35:0] _T_158; // @[Cat.scala 30:58:@302.4]
  wire [1:0] opipe_axi_len; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 184:34:@305.4]
  wire [31:0] opipe_axi_addr; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 185:35:@306.4]
  wire [3:0] opipe_axi_axid; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 186:35:@307.4]
  wire [71:0] _T_160; // @[Cat.scala 30:58:@308.4]
  wire  _T_165; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 211:47:@324.4]
  wire  cq_wr_require_ack; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 212:39:@328.4]
  NV_NVDLA_BC_pipe pipe_p1 ( // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 54:25:@167.4]
    .reset(pipe_p1_reset),
    .io_clk(pipe_p1_io_clk),
    .io_vi(pipe_p1_io_vi),
    .io_ro(pipe_p1_io_ro),
    .io_di(pipe_p1_io_di),
    .io_vo(pipe_p1_io_vo),
    .io_ri(pipe_p1_io_ri),
    .io_dout(pipe_p1_io_dout)
  );
  NV_NVDLA_BC_pipe_1 pipe_p2 ( // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 64:25:@176.4]
    .reset(pipe_p2_reset),
    .io_clk(pipe_p2_io_clk),
    .io_vi(pipe_p2_io_vi),
    .io_ro(pipe_p2_io_ro),
    .io_di(pipe_p2_io_di),
    .io_vo(pipe_p2_io_vo),
    .io_ri(pipe_p2_io_ri),
    .io_dout(pipe_p2_io_dout)
  );
  NV_COUNTER_STAGE_os perf_os ( // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 149:25:@266.4]
    .reset(perf_os_reset),
    .io_clk(perf_os_io_clk),
    .io_os_cnt_add(perf_os_io_os_cnt_add),
    .io_os_cnt_sub(perf_os_io_os_cnt_sub),
    .io_os_cnt_cen(perf_os_io_os_cnt_cen),
    .io_os_cnt_cur(perf_os_io_os_cnt_cur)
  );
  NV_NVDLA_IS_pipe pipe_p3 ( // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 161:25:@278.4]
    .reset(pipe_p3_reset),
    .io_clk(pipe_p3_io_clk),
    .io_dout(pipe_p3_io_dout),
    .io_vo(pipe_p3_io_vo),
    .io_ri(pipe_p3_io_ri),
    .io_di(pipe_p3_io_di),
    .io_vi(pipe_p3_io_vi),
    .io_ro(pipe_p3_io_ro)
  );
  NV_NVDLA_IS_pipe_1 pipe_p4 ( // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 174:25:@293.4]
    .reset(pipe_p4_reset),
    .io_clk(pipe_p4_io_clk),
    .io_dout(pipe_p4_io_dout),
    .io_vo(pipe_p4_io_vo),
    .io_ri(pipe_p4_io_ri),
    .io_di(pipe_p4_io_di),
    .io_vi(pipe_p4_io_vi),
    .io_ro(pipe_p4_io_ro)
  );
  assign os_cnt = perf_os_io_os_cnt_cur; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 124:22:@240.4 NV_NVDLA_MCIF_WRITE_IG_cvt.scala 154:12:@273.4]
  assign _T_98 = pipe_p1_io_vo; // @[Bitwise.scala 72:15:@198.4]
  assign _T_101 = _T_98 ? 45'h1fffffffffff : 45'h0; // @[Bitwise.scala 72:12:@199.4]
  assign cmd_vld_pd = _T_101 & pipe_p1_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 85:61:@200.4]
  assign cmd_size = cmd_vld_pd[39:37]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 90:30:@204.4]
  assign axi_len = cmd_size[1:0]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 101:27:@212.4]
  assign _T_127 = axi_len + 2'h1; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 125:47:@241.4]
  assign os_inp_add_nxt = pipe_p1_io_vo ? _T_127 : 3'h0; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 125:29:@242.4]
  assign _GEN_3 = {{6'd0}, os_inp_add_nxt}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:29:@251.4]
  assign _T_136 = os_cnt + _GEN_3; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:29:@251.4]
  assign _T_134 = eg2ig_axi_len_d + 2'h1; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 134:63:@249.4]
  assign os_inp_sub_nxt = eg2ig_axi_vld_d ? _T_134 : 3'h0; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 134:29:@250.4]
  assign _GEN_4 = {{7'd0}, os_inp_sub_nxt}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:47:@252.4]
  assign _T_137 = _T_136 - _GEN_4; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:47:@252.4]
  assign os_inp_nxt = $unsigned(_T_137); // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 135:47:@253.4]
  assign wr_os_cnt_ext = {1'h0,io_reg2dp_wr_os_cnt}; // @[Cat.scala 30:58:@262.4]
  assign _T_148 = wr_os_cnt_ext + 9'h1; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 147:48:@263.4]
  assign _GEN_5 = {{1'd0}, _T_148}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 147:31:@264.4]
  assign os_cnt_full = os_inp_nxt > _GEN_5; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 147:31:@264.4]
  assign _T_88 = ~ os_cnt_full; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 74:32:@185.4]
  assign os_cmd_vld = pipe_p1_io_vo & _T_88; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 74:30:@186.4]
  assign axi_cmd_rdy = pipe_p3_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 139:27:@255.4 NV_NVDLA_MCIF_WRITE_IG_cvt.scala 164:17:@283.4]
  assign axi_dat_rdy = pipe_p4_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 77:27:@188.4 NV_NVDLA_MCIF_WRITE_IG_cvt.scala 177:17:@298.4]
  assign axi_both_rdy = axi_cmd_rdy & axi_dat_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 203:37:@321.4]
  assign all_downs_rdy = io_cq_wr_pd_ready & axi_both_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 204:40:@322.4]
  assign _T_92 = os_cmd_vld & all_downs_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 80:46:@190.4]
  assign is_first_beat = beat_count == 2'h0; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 113:34:@230.4]
  assign _T_94 = is_first_beat & pipe_p2_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 82:30:@193.4]
  assign _T_95 = _T_94 & all_downs_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 82:40:@194.4]
  assign cmd_axid = cmd_vld_pd[3:0]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 87:30:@201.4]
  assign cmd_require_ack = cmd_vld_pd[4]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 88:37:@202.4]
  assign cmd_addr = cmd_vld_pd[36:5]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 89:30:@203.4]
  assign cmd_ltran = cmd_vld_pd[43]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 94:31:@208.4]
  assign dat_data = pipe_p2_io_dout[63:0]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 98:26:@210.4]
  assign dat_mask = pipe_p2_io_dout[64]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 99:26:@211.4]
  assign _T_102 = os_cmd_vld & pipe_p2_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 103:43:@213.4]
  assign is_first_cmd_dat_vld = _T_102 & is_first_beat; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 103:53:@214.4]
  assign _T_105 = is_first_cmd_dat_vld & all_downs_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 106:31:@216.4]
  assign _T_107 = beat_count != 2'h0; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 109:27:@221.6]
  assign _T_108 = _T_107 & pipe_p2_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 109:36:@222.6]
  assign _T_109 = _T_108 & axi_dat_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 109:46:@223.6]
  assign _T_111 = beat_count - 2'h1; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 110:34:@225.8]
  assign _T_112 = $unsigned(_T_111); // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 110:34:@226.8]
  assign _T_113 = _T_112[1:0]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 110:34:@227.8]
  assign _GEN_0 = _T_109 ? _T_113 : beat_count; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 109:60:@224.6]
  assign _GEN_1 = _T_105 ? axi_len : _GEN_0; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 106:47:@217.4]
  assign is_single_beat = axi_len == 2'h0; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 114:35:@232.4]
  assign _T_118 = beat_count == 2'h1; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 115:36:@233.4]
  assign _T_121 = is_first_beat & is_single_beat; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 115:67:@235.4]
  assign is_last_beat = _T_118 | _T_121; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 115:44:@236.4]
  assign axi_strb = dat_mask ? 8'hff : 8'h0; // @[Bitwise.scala 72:12:@239.4]
  assign _GEN_2 = io_eg2ig_axi_len_valid ? io_eg2ig_axi_len_bits : eg2ig_axi_len_d; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 130:33:@246.4]
  assign _T_150 = is_first_cmd_dat_vld & io_cq_wr_pd_ready; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 159:41:@274.4]
  assign axi_cmd_vld = _T_150 & axi_dat_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 159:61:@275.4]
  assign os_cnt_add_en = axi_cmd_vld & axi_cmd_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 140:37:@256.4]
  assign _T_153 = ~ is_first_beat; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 172:34:@287.4]
  assign _T_154 = os_cmd_vld & io_cq_wr_pd_ready; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 172:64:@288.4]
  assign _T_155 = _T_154 & axi_cmd_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 172:84:@289.4]
  assign _T_156 = _T_153 | _T_155; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 172:49:@290.4]
  assign _T_158 = {cmd_axid,cmd_addr}; // @[Cat.scala 30:58:@302.4]
  assign opipe_axi_len = pipe_p3_io_dout[1:0]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 184:34:@305.4]
  assign opipe_axi_addr = pipe_p3_io_dout[33:2]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 185:35:@306.4]
  assign opipe_axi_axid = pipe_p3_io_dout[37:34]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 186:35:@307.4]
  assign _T_160 = {dat_data,axi_strb}; // @[Cat.scala 30:58:@308.4]
  assign _T_165 = is_first_cmd_dat_vld & axi_both_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 211:47:@324.4]
  assign cq_wr_require_ack = cmd_ltran & cmd_require_ack; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 212:39:@328.4]
  assign io_spt2cvt_cmd_pd_ready = pipe_p1_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 57:29:@172.4]
  assign io_spt2cvt_dat_pd_ready = pipe_p2_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 67:29:@181.4]
  assign io_cq_wr_pd_valid = _T_165 & _T_88; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 211:23:@327.4]
  assign io_cq_wr_pd_bits = {axi_len,cq_wr_require_ack}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 216:22:@330.4]
  assign io_cq_wr_thread_id = {{1'd0}, cmd_axid}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 217:24:@331.4]
  assign io_mcif2noc_axi_aw_valid = pipe_p3_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 166:30:@285.4]
  assign io_mcif2noc_axi_aw_bits_id = {{4'd0}, opipe_axi_axid}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 193:32:@314.4]
  assign io_mcif2noc_axi_aw_bits_len = {{2'd0}, opipe_axi_len}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 195:33:@317.4]
  assign io_mcif2noc_axi_aw_bits_addr = {32'h0,opipe_axi_addr}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 194:34:@316.4]
  assign io_mcif2noc_axi_w_valid = pipe_p4_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 179:29:@300.4]
  assign io_mcif2noc_axi_w_bits_data = pipe_p4_io_dout[72:9]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 197:33:@319.4]
  assign io_mcif2noc_axi_w_bits_strb = pipe_p4_io_dout[8:1]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 198:33:@320.4]
  assign io_mcif2noc_axi_w_bits_last = pipe_p4_io_dout[0]; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 196:33:@318.4]
  assign pipe_p1_reset = reset; // @[:@169.4]
  assign pipe_p1_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 55:20:@170.4]
  assign pipe_p1_io_vi = io_spt2cvt_cmd_pd_valid; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 56:19:@171.4]
  assign pipe_p1_io_di = io_spt2cvt_cmd_pd_bits; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 58:19:@173.4]
  assign pipe_p1_io_ri = _T_95 & _T_88; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 60:19:@174.4]
  assign pipe_p2_reset = reset; // @[:@178.4]
  assign pipe_p2_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 65:20:@179.4]
  assign pipe_p2_io_vi = io_spt2cvt_dat_pd_valid; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 66:19:@180.4]
  assign pipe_p2_io_di = io_spt2cvt_dat_pd_bits; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 68:19:@182.4]
  assign pipe_p2_io_ri = is_first_beat ? _T_92 : axi_dat_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 70:19:@183.4]
  assign perf_os_reset = reset; // @[:@268.4]
  assign perf_os_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 150:20:@269.4]
  assign perf_os_io_os_cnt_add = os_cnt_add_en ? _T_127 : 3'h0; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 151:27:@270.4]
  assign perf_os_io_os_cnt_sub = eg2ig_axi_vld_d ? _T_134 : 3'h0; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 152:27:@271.4]
  assign perf_os_io_os_cnt_cen = os_cnt_add_en | eg2ig_axi_vld_d; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 153:27:@272.4]
  assign pipe_p3_reset = reset; // @[:@280.4]
  assign pipe_p3_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 162:20:@281.4]
  assign pipe_p3_io_ri = io_mcif2noc_axi_aw_ready; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 167:19:@286.4]
  assign pipe_p3_io_di = {_T_158,axi_len}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 165:19:@284.4]
  assign pipe_p3_io_vi = _T_150 & axi_dat_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 163:19:@282.4]
  assign pipe_p4_reset = reset; // @[:@295.4]
  assign pipe_p4_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 175:20:@296.4]
  assign pipe_p4_io_ri = io_mcif2noc_axi_w_ready; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 180:19:@301.4]
  assign pipe_p4_io_di = {_T_160,is_last_beat}; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 178:19:@299.4]
  assign pipe_p4_io_vi = pipe_p2_io_vo & _T_156; // @[NV_NVDLA_MCIF_WRITE_IG_cvt.scala 176:19:@297.4]
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
  eg2ig_axi_vld_d = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  eg2ig_axi_len_d = _RAND_1[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  beat_count = _RAND_2[1:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      eg2ig_axi_vld_d <= 1'h0;
    end else begin
      eg2ig_axi_vld_d <= io_eg2ig_axi_len_valid;
    end
    if (reset) begin
      eg2ig_axi_len_d <= 2'h0;
    end else begin
      if (io_eg2ig_axi_len_valid) begin
        eg2ig_axi_len_d <= io_eg2ig_axi_len_bits;
      end
    end
    if (reset) begin
      beat_count <= 2'h0;
    end else begin
      if (_T_105) begin
        beat_count <= axi_len;
      end else begin
        if (_T_109) begin
          beat_count <= _T_113;
        end
      end
    end
  end
endmodule
