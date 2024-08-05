module NV_NVDLA_IS_pipe( // @[:@3.2]
  input         reset, // @[:@5.4]
  input         io_clk, // @[:@6.4]
  output [46:0] io_dout, // @[:@6.4]
  output        io_vo, // @[:@6.4]
  input         io_ri, // @[:@6.4]
  input  [46:0] io_di, // @[:@6.4]
  input         io_vi, // @[:@6.4]
  output        io_ro // @[:@6.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@8.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@9.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@10.4]
  reg [31:0] _RAND_2;
  reg [46:0] skid_flop_di; // @[IS_pipe.scala 57:53:@11.4]
  reg [63:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@12.4]
  reg [31:0] _RAND_4;
  reg [46:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@13.4]
  reg [63:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@22.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@27.4]
  wire [46:0] _GEN_1; // @[IS_pipe.scala 76:29:@28.4]
  wire [46:0] skid_di; // @[IS_pipe.scala 79:19:@31.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@33.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@34.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@36.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@39.4]
  wire [46:0] _GEN_3; // @[IS_pipe.scala 87:29:@40.4]
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
  _RAND_3 = {2{`RANDOM}};
  skid_flop_di = _RAND_3[46:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {2{`RANDOM}};
  pipe_skid_di = _RAND_5[46:0];
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
      skid_flop_di <= 47'h0;
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
      pipe_skid_di <= 47'h0;
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
module NV_COUNTER_STAGE_lat( // @[:@97.2]
  input        reset, // @[:@99.4]
  input        io_clk, // @[:@100.4]
  input  [2:0] io_lat_cnt_inc, // @[:@100.4]
  input        io_lat_cnt_dec, // @[:@100.4]
  output [8:0] io_lat_cnt_cur // @[:@100.4]
);
  wire [2:0] _GEN_0; // @[Perf_Counter.scala 303:34:@102.4]
  wire  _T_13; // @[Perf_Counter.scala 303:34:@102.4]
  reg [8:0] _T_16; // @[Perf_Counter.scala 306:34:@103.4]
  reg [31:0] _RAND_0;
  wire [8:0] _GEN_1; // @[Perf_Counter.scala 313:36:@109.4]
  wire [9:0] _T_25; // @[Perf_Counter.scala 313:36:@109.4]
  wire [9:0] _GEN_2; // @[Perf_Counter.scala 313:54:@110.4]
  wire [10:0] _T_26; // @[Perf_Counter.scala 313:54:@110.4]
  wire [10:0] _T_27; // @[Perf_Counter.scala 313:54:@111.4]
  wire [10:0] _T_18; // @[Perf_Counter.scala 307:27:@104.4 Perf_Counter.scala 312:17:@108.4]
  wire [10:0] _T_28; // @[Perf_Counter.scala 314:23:@113.4]
  assign _GEN_0 = {{2'd0}, io_lat_cnt_dec}; // @[Perf_Counter.scala 303:34:@102.4]
  assign _T_13 = io_lat_cnt_inc != _GEN_0; // @[Perf_Counter.scala 303:34:@102.4]
  assign _GEN_1 = {{6'd0}, io_lat_cnt_inc}; // @[Perf_Counter.scala 313:36:@109.4]
  assign _T_25 = _T_16 + _GEN_1; // @[Perf_Counter.scala 313:36:@109.4]
  assign _GEN_2 = {{9'd0}, io_lat_cnt_dec}; // @[Perf_Counter.scala 313:54:@110.4]
  assign _T_26 = _T_25 - _GEN_2; // @[Perf_Counter.scala 313:54:@110.4]
  assign _T_27 = $unsigned(_T_26); // @[Perf_Counter.scala 313:54:@111.4]
  assign _T_18 = {{2'd0}, _T_16}; // @[Perf_Counter.scala 307:27:@104.4 Perf_Counter.scala 312:17:@108.4]
  assign _T_28 = _T_13 ? _T_27 : _T_18; // @[Perf_Counter.scala 314:23:@113.4]
  assign io_lat_cnt_cur = _T_16; // @[Perf_Counter.scala 320:20:@117.4]
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
  _T_16 = _RAND_0[8:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      _T_16 <= 9'h0;
    end else begin
      _T_16 <= _T_28[8:0];
    end
  end
endmodule
module NV_NVDLA_MCIF_READ_IG_bpt( // @[:@119.2]
  input         clock, // @[:@120.4]
  input         reset, // @[:@121.4]
  input         io_nvdla_core_clk, // @[:@122.4]
  output        io_dma2bpt_req_pd_ready, // @[:@122.4]
  input         io_dma2bpt_req_pd_valid, // @[:@122.4]
  input  [46:0] io_dma2bpt_req_pd_bits, // @[:@122.4]
  input         io_dma2bpt_cdt_lat_fifo_pop, // @[:@122.4]
  input         io_bpt2arb_req_pd_ready, // @[:@122.4]
  output        io_bpt2arb_req_pd_valid, // @[:@122.4]
  output [42:0] io_bpt2arb_req_pd_bits, // @[:@122.4]
  input  [3:0]  io_tieoff_axid, // @[:@122.4]
  input  [8:0]  io_tieoff_lat_fifo_depth // @[:@122.4]
);
  wire  pipe_p1_reset; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 46:25:@124.4]
  wire  pipe_p1_io_clk; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 46:25:@124.4]
  wire [46:0] pipe_p1_io_dout; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 46:25:@124.4]
  wire  pipe_p1_io_vo; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 46:25:@124.4]
  wire  pipe_p1_io_ri; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 46:25:@124.4]
  wire [46:0] pipe_p1_io_di; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 46:25:@124.4]
  wire  pipe_p1_io_vi; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 46:25:@124.4]
  wire  pipe_p1_io_ro; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 46:25:@124.4]
  wire  pipe_p2_reset; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 58:25:@132.4]
  wire  pipe_p2_io_clk; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 58:25:@132.4]
  wire [46:0] pipe_p2_io_dout; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 58:25:@132.4]
  wire  pipe_p2_io_vo; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 58:25:@132.4]
  wire  pipe_p2_io_ri; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 58:25:@132.4]
  wire [46:0] pipe_p2_io_di; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 58:25:@132.4]
  wire  pipe_p2_io_vi; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 58:25:@132.4]
  wire  pipe_p2_io_ro; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 58:25:@132.4]
  wire  perf_lat_reset; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 129:26:@167.4]
  wire  perf_lat_io_clk; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 129:26:@167.4]
  wire [2:0] perf_lat_io_lat_cnt_inc; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 129:26:@167.4]
  wire  perf_lat_io_lat_cnt_dec; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 129:26:@167.4]
  wire [8:0] perf_lat_io_lat_cnt_cur; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 129:26:@167.4]
  wire  lat_fifo_stall_enable; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 123:59:@161.4]
  wire  _T_59; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 136:23:@176.4]
  wire [9:0] _T_57; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 135:55:@173.4]
  wire [9:0] _T_58; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 135:55:@174.4]
  wire [8:0] lat_fifo_free_slot; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 135:55:@175.4]
  wire  _T_60; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 136:63:@177.4]
  wire  req_enable; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 136:47:@178.4]
  wire  req_rdy; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 228:27:@220.4]
  reg [14:0] count_req; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 206:28:@200.4]
  reg [31:0] _RAND_0;
  wire  _T_35; // @[Bitwise.scala 72:15:@144.4]
  wire [46:0] _T_38; // @[Bitwise.scala 72:12:@145.4]
  wire [46:0] in_vld_pd; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 72:57:@146.4]
  wire [14:0] in_size; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 76:28:@148.4]
  wire  is_ltran; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 218:28:@217.4]
  wire [31:0] in_addr; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 75:28:@147.4]
  reg  lat_count_dec; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 124:32:@162.4]
  reg [31:0] _RAND_1;
  wire  bpt2arb_accept; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 232:47:@224.4]
  wire  _T_55; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 127:44:@165.4]
  reg [31:0] out_addr; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 175:49:@185.4]
  reg [31:0] _RAND_2;
  wire [3:0] _T_71; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 182:45:@188.8]
  wire [31:0] _GEN_4; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 182:37:@189.8]
  wire [32:0] _T_72; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 182:37:@189.8]
  wire [32:0] _T_76; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 186:34:@194.8]
  wire  is_ftran; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 216:28:@211.4]
  wire [32:0] _GEN_0; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 177:23:@187.6]
  wire [32:0] _GEN_1; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 176:25:@186.4]
  wire [15:0] _T_82; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 212:36:@206.8]
  wire [14:0] _T_83; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 212:36:@207.8]
  wire [14:0] _GEN_2; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 208:23:@202.6]
  wire [14:0] _GEN_3; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 207:25:@201.4]
  wire [31:0] bpt2arb_addr; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 220:27:@219.4]
  wire [38:0] _T_94; // @[Cat.scala 30:58:@227.4]
  wire [3:0] _T_97; // @[Cat.scala 30:58:@230.4]
  NV_NVDLA_IS_pipe pipe_p1 ( // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 46:25:@124.4]
    .reset(pipe_p1_reset),
    .io_clk(pipe_p1_io_clk),
    .io_dout(pipe_p1_io_dout),
    .io_vo(pipe_p1_io_vo),
    .io_ri(pipe_p1_io_ri),
    .io_di(pipe_p1_io_di),
    .io_vi(pipe_p1_io_vi),
    .io_ro(pipe_p1_io_ro)
  );
  NV_NVDLA_IS_pipe pipe_p2 ( // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 58:25:@132.4]
    .reset(pipe_p2_reset),
    .io_clk(pipe_p2_io_clk),
    .io_dout(pipe_p2_io_dout),
    .io_vo(pipe_p2_io_vo),
    .io_ri(pipe_p2_io_ri),
    .io_di(pipe_p2_io_di),
    .io_vi(pipe_p2_io_vi),
    .io_ro(pipe_p2_io_ro)
  );
  NV_COUNTER_STAGE_lat perf_lat ( // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 129:26:@167.4]
    .reset(perf_lat_reset),
    .io_clk(perf_lat_io_clk),
    .io_lat_cnt_inc(perf_lat_io_lat_cnt_inc),
    .io_lat_cnt_dec(perf_lat_io_lat_cnt_dec),
    .io_lat_cnt_cur(perf_lat_io_lat_cnt_cur)
  );
  assign lat_fifo_stall_enable = io_tieoff_lat_fifo_depth != 9'h0; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 123:59:@161.4]
  assign _T_59 = ~ lat_fifo_stall_enable; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 136:23:@176.4]
  assign _T_57 = io_tieoff_lat_fifo_depth - perf_lat_io_lat_cnt_cur; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 135:55:@173.4]
  assign _T_58 = $unsigned(_T_57); // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 135:55:@174.4]
  assign lat_fifo_free_slot = _T_58[8:0]; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 135:55:@175.4]
  assign _T_60 = 9'h1 <= lat_fifo_free_slot; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 136:63:@177.4]
  assign req_enable = _T_59 | _T_60; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 136:47:@178.4]
  assign req_rdy = req_enable & io_bpt2arb_req_pd_ready; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 228:27:@220.4]
  assign _T_35 = pipe_p2_io_vo; // @[Bitwise.scala 72:15:@144.4]
  assign _T_38 = _T_35 ? 47'h7fffffffffff : 47'h0; // @[Bitwise.scala 72:12:@145.4]
  assign in_vld_pd = _T_38 & pipe_p2_io_dout; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 72:57:@146.4]
  assign in_size = in_vld_pd[46:32]; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 76:28:@148.4]
  assign is_ltran = count_req == in_size; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 218:28:@217.4]
  assign in_addr = in_vld_pd[31:0]; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 75:28:@147.4]
  assign bpt2arb_accept = io_bpt2arb_req_pd_valid & req_rdy; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 232:47:@224.4]
  assign _T_55 = bpt2arb_accept & lat_fifo_stall_enable; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 127:44:@165.4]
  assign _T_71 = 4'h1 << 2'h3; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 182:45:@188.8]
  assign _GEN_4 = {{28'd0}, _T_71}; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 182:37:@189.8]
  assign _T_72 = in_addr + _GEN_4; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 182:37:@189.8]
  assign _T_76 = out_addr + _GEN_4; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 186:34:@194.8]
  assign is_ftran = count_req == 15'h0; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 216:28:@211.4]
  assign _GEN_0 = is_ftran ? _T_72 : _T_76; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 177:23:@187.6]
  assign _GEN_1 = bpt2arb_accept ? _GEN_0 : {{1'd0}, out_addr}; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 176:25:@186.4]
  assign _T_82 = count_req + 15'h1; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 212:36:@206.8]
  assign _T_83 = count_req + 15'h1; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 212:36:@207.8]
  assign _GEN_2 = is_ltran ? 15'h0 : _T_83; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 208:23:@202.6]
  assign _GEN_3 = bpt2arb_accept ? _GEN_2 : count_req; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 207:25:@201.4]
  assign bpt2arb_addr = is_ftran ? in_addr : out_addr; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 220:27:@219.4]
  assign _T_94 = {3'h0,bpt2arb_addr,io_tieoff_axid}; // @[Cat.scala 30:58:@227.4]
  assign _T_97 = {is_ftran,is_ltran,2'h0}; // @[Cat.scala 30:58:@230.4]
  assign io_dma2bpt_req_pd_ready = pipe_p1_io_ro; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 50:29:@129.4]
  assign io_bpt2arb_req_pd_valid = req_enable & pipe_p2_io_vo; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 231:29:@223.4]
  assign io_bpt2arb_req_pd_bits = {_T_97,_T_94}; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 233:28:@232.4]
  assign pipe_p1_reset = reset; // @[:@126.4]
  assign pipe_p1_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 47:21:@127.4]
  assign pipe_p1_io_ri = pipe_p2_io_ro; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 62:19:@137.4]
  assign pipe_p1_io_di = io_dma2bpt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 51:19:@130.4]
  assign pipe_p1_io_vi = io_dma2bpt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 49:19:@128.4]
  assign pipe_p2_reset = reset; // @[:@134.4]
  assign pipe_p2_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 59:21:@135.4]
  assign pipe_p2_io_ri = req_rdy & is_ltran; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 66:19:@139.4]
  assign pipe_p2_io_di = pipe_p1_io_dout; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 63:19:@138.4]
  assign pipe_p2_io_vi = pipe_p1_io_vo; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 61:19:@136.4]
  assign perf_lat_reset = reset; // @[:@169.4]
  assign perf_lat_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 130:21:@170.4]
  assign perf_lat_io_lat_cnt_inc = _T_55 ? 3'h1 : 3'h0; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 131:29:@171.4]
  assign perf_lat_io_lat_cnt_dec = lat_count_dec; // @[NV_NVDLA_MCIF_READ_IG_bpt.scala 132:29:@172.4]
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
  count_req = _RAND_0[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  lat_count_dec = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  out_addr = _RAND_2[31:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      count_req <= 15'h0;
    end else begin
      if (bpt2arb_accept) begin
        if (is_ltran) begin
          count_req <= 15'h0;
        end else begin
          count_req <= _T_83;
        end
      end
    end
    if (reset) begin
      lat_count_dec <= 1'h0;
    end else begin
      lat_count_dec <= io_dma2bpt_cdt_lat_fifo_pop;
    end
    if (reset) begin
      out_addr <= 32'h0;
    end else begin
      out_addr <= _GEN_1[31:0];
    end
  end
endmodule
