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
module NV_NVDLA_BC_OS_pipe( // @[:@1620.2]
  input         reset, // @[:@1622.4]
  input         io_clk, // @[:@1623.4]
  input         io_vi, // @[:@1623.4]
  output        io_ro, // @[:@1623.4]
  input  [42:0] io_di, // @[:@1623.4]
  output        io_vo, // @[:@1623.4]
  input         io_ri, // @[:@1623.4]
  output [42:0] io_dout // @[:@1623.4]
);
  reg  pipe_valid; // @[BC_OS_pipe.scala 57:29:@1626.4]
  reg [31:0] _RAND_0;
  reg [42:0] pipe_data; // @[BC_OS_pipe.scala 58:50:@1627.4]
  reg [63:0] _RAND_1;
  reg  pipe_ready; // @[BC_OS_pipe.scala 59:29:@1628.4]
  reg [31:0] _RAND_2;
  wire  _T_26; // @[BC_OS_pipe.scala 61:36:@1629.4]
  wire  pipe_ready_bc; // @[BC_OS_pipe.scala 61:33:@1630.4]
  wire  _T_29; // @[BC_OS_pipe.scala 62:22:@1632.4]
  wire  _T_30; // @[BC_OS_pipe.scala 63:35:@1634.4]
  wire [42:0] _T_31; // @[BC_OS_pipe.scala 63:21:@1635.4]
  reg  skid_valid; // @[BC_OS_pipe.scala 67:29:@1638.4]
  reg [31:0] _RAND_3;
  reg  skid_ready_flop; // @[BC_OS_pipe.scala 68:34:@1639.4]
  reg [31:0] _RAND_4;
  reg [42:0] skid_data; // @[BC_OS_pipe.scala 69:50:@1640.4]
  reg [63:0] _RAND_5;
  wire  _T_43; // @[BC_OS_pipe.scala 79:30:@1646.4]
  wire  pipe_skid_ready; // @[BC_OS_pipe.scala 73:31:@1644.4 BC_OS_pipe.scala 92:21:@1665.4]
  wire  _T_44; // @[BC_OS_pipe.scala 79:52:@1647.4]
  wire  skid_catch; // @[BC_OS_pipe.scala 79:49:@1648.4]
  wire  _T_46; // @[BC_OS_pipe.scala 80:52:@1650.4]
  wire  skid_ready; // @[BC_OS_pipe.scala 80:22:@1651.4]
  wire  _T_49; // @[BC_OS_pipe.scala 81:22:@1654.4]
  wire [42:0] _T_50; // @[BC_OS_pipe.scala 85:21:@1658.4]
  wire  _T_51; // @[BC_OS_pipe.scala 87:27:@1660.4]
  wire [42:0] _T_52; // @[BC_OS_pipe.scala 88:26:@1662.4]
  wire  pipe_skid_valid; // @[BC_OS_pipe.scala 72:31:@1643.4 BC_OS_pipe.scala 87:21:@1661.4]
  wire [42:0] pipe_skid_data; // @[BC_OS_pipe.scala 74:30:@1645.4 BC_OS_pipe.scala 88:20:@1663.4]
  assign _T_26 = ~ pipe_valid; // @[BC_OS_pipe.scala 61:36:@1629.4]
  assign pipe_ready_bc = pipe_ready | _T_26; // @[BC_OS_pipe.scala 61:33:@1630.4]
  assign _T_29 = pipe_ready_bc ? io_vi : 1'h1; // @[BC_OS_pipe.scala 62:22:@1632.4]
  assign _T_30 = pipe_ready_bc & io_vi; // @[BC_OS_pipe.scala 63:35:@1634.4]
  assign _T_31 = _T_30 ? io_di : pipe_data; // @[BC_OS_pipe.scala 63:21:@1635.4]
  assign _T_43 = pipe_valid & skid_ready_flop; // @[BC_OS_pipe.scala 79:30:@1646.4]
  assign pipe_skid_ready = io_ri; // @[BC_OS_pipe.scala 73:31:@1644.4 BC_OS_pipe.scala 92:21:@1665.4]
  assign _T_44 = ~ pipe_skid_ready; // @[BC_OS_pipe.scala 79:52:@1647.4]
  assign skid_catch = _T_43 & _T_44; // @[BC_OS_pipe.scala 79:49:@1648.4]
  assign _T_46 = ~ skid_catch; // @[BC_OS_pipe.scala 80:52:@1650.4]
  assign skid_ready = skid_valid ? pipe_skid_ready : _T_46; // @[BC_OS_pipe.scala 80:22:@1651.4]
  assign _T_49 = skid_valid ? _T_44 : skid_catch; // @[BC_OS_pipe.scala 81:22:@1654.4]
  assign _T_50 = skid_catch ? pipe_data : skid_data; // @[BC_OS_pipe.scala 85:21:@1658.4]
  assign _T_51 = skid_ready_flop ? pipe_valid : skid_valid; // @[BC_OS_pipe.scala 87:27:@1660.4]
  assign _T_52 = skid_ready_flop ? pipe_data : skid_data; // @[BC_OS_pipe.scala 88:26:@1662.4]
  assign pipe_skid_valid = _T_51; // @[BC_OS_pipe.scala 72:31:@1643.4 BC_OS_pipe.scala 87:21:@1661.4]
  assign pipe_skid_data = _T_52; // @[BC_OS_pipe.scala 74:30:@1645.4 BC_OS_pipe.scala 88:20:@1663.4]
  assign io_ro = pipe_ready | _T_26; // @[BC_OS_pipe.scala 64:11:@1637.4]
  assign io_vo = pipe_skid_valid; // @[BC_OS_pipe.scala 91:11:@1664.4]
  assign io_dout = pipe_skid_data; // @[BC_OS_pipe.scala 93:13:@1666.4]
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
  pipe_data = _RAND_1[42:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  pipe_ready = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  skid_valid = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  skid_ready_flop = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {2{`RANDOM}};
  skid_data = _RAND_5[42:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      pipe_valid <= 1'h0;
    end else begin
      if (pipe_ready_bc) begin
        pipe_valid <= io_vi;
      end else begin
        pipe_valid <= 1'h1;
      end
    end
    if (reset) begin
      pipe_data <= 43'h0;
    end else begin
      if (_T_30) begin
        pipe_data <= io_di;
      end
    end
    if (reset) begin
      pipe_ready <= 1'h1;
    end else begin
      if (skid_valid) begin
        pipe_ready <= pipe_skid_ready;
      end else begin
        pipe_ready <= _T_46;
      end
    end
    if (reset) begin
      skid_valid <= 1'h0;
    end else begin
      if (skid_valid) begin
        skid_valid <= _T_44;
      end else begin
        skid_valid <= skid_catch;
      end
    end
    if (reset) begin
      skid_ready_flop <= 1'h1;
    end else begin
      if (skid_valid) begin
        skid_ready_flop <= pipe_skid_ready;
      end else begin
        skid_ready_flop <= _T_46;
      end
    end
    if (reset) begin
      skid_data <= 43'h0;
    end else begin
      if (skid_catch) begin
        skid_data <= pipe_data;
      end
    end
  end
endmodule
module NV_NVDLA_arb( // @[:@1956.2]
  input        reset, // @[:@1958.4]
  input        io_clk, // @[:@1959.4]
  input        io_req_0, // @[:@1959.4]
  input        io_req_1, // @[:@1959.4]
  input        io_req_2, // @[:@1959.4]
  input        io_req_3, // @[:@1959.4]
  input        io_req_4, // @[:@1959.4]
  input        io_req_5, // @[:@1959.4]
  input        io_req_6, // @[:@1959.4]
  input  [7:0] io_wt_0, // @[:@1959.4]
  input  [7:0] io_wt_1, // @[:@1959.4]
  input  [7:0] io_wt_2, // @[:@1959.4]
  input  [7:0] io_wt_3, // @[:@1959.4]
  input  [7:0] io_wt_4, // @[:@1959.4]
  input  [7:0] io_wt_5, // @[:@1959.4]
  input  [7:0] io_wt_6, // @[:@1959.4]
  input        io_gnt_busy, // @[:@1959.4]
  output       io_gnt_0, // @[:@1959.4]
  output       io_gnt_1, // @[:@1959.4]
  output       io_gnt_2, // @[:@1959.4]
  output       io_gnt_3, // @[:@1959.4]
  output       io_gnt_4, // @[:@1959.4]
  output       io_gnt_5, // @[:@1959.4]
  output       io_gnt_6, // @[:@1959.4]
  output       io_gnt_7, // @[:@1959.4]
  output       io_gnt_8, // @[:@1959.4]
  output       io_gnt_9 // @[:@1959.4]
);
  wire  _T_100; // @[NV_NVDLA_arb.scala 41:68:@1961.4]
  wire  _T_101; // @[NV_NVDLA_arb.scala 41:56:@1962.4]
  wire  _T_103; // @[NV_NVDLA_arb.scala 41:68:@1963.4]
  wire  _T_104; // @[NV_NVDLA_arb.scala 41:56:@1964.4]
  wire  _T_106; // @[NV_NVDLA_arb.scala 41:68:@1965.4]
  wire  _T_107; // @[NV_NVDLA_arb.scala 41:56:@1966.4]
  wire  _T_109; // @[NV_NVDLA_arb.scala 41:68:@1967.4]
  wire  _T_110; // @[NV_NVDLA_arb.scala 41:56:@1968.4]
  wire  _T_112; // @[NV_NVDLA_arb.scala 41:68:@1969.4]
  wire  _T_113; // @[NV_NVDLA_arb.scala 41:56:@1970.4]
  wire  _T_115; // @[NV_NVDLA_arb.scala 41:68:@1971.4]
  wire  _T_116; // @[NV_NVDLA_arb.scala 41:56:@1972.4]
  wire  _T_118; // @[NV_NVDLA_arb.scala 41:68:@1973.4]
  wire  _T_119; // @[NV_NVDLA_arb.scala 41:56:@1974.4]
  wire [9:0] _T_153; // @[NV_NVDLA_arb.scala 41:75:@2000.4]
  wire [8:0] _T_171; // @[NV_NVDLA_arb.scala 45:36:@2002.4]
  wire [8:0] _T_172; // @[NV_NVDLA_arb.scala 45:36:@2003.4]
  wire [7:0] _T_173; // @[NV_NVDLA_arb.scala 45:36:@2004.4]
  wire [8:0] _T_175; // @[NV_NVDLA_arb.scala 45:36:@2006.4]
  wire [8:0] _T_176; // @[NV_NVDLA_arb.scala 45:36:@2007.4]
  wire [7:0] _T_177; // @[NV_NVDLA_arb.scala 45:36:@2008.4]
  wire [8:0] _T_179; // @[NV_NVDLA_arb.scala 45:36:@2010.4]
  wire [8:0] _T_180; // @[NV_NVDLA_arb.scala 45:36:@2011.4]
  wire [7:0] _T_181; // @[NV_NVDLA_arb.scala 45:36:@2012.4]
  wire [8:0] _T_183; // @[NV_NVDLA_arb.scala 45:36:@2014.4]
  wire [8:0] _T_184; // @[NV_NVDLA_arb.scala 45:36:@2015.4]
  wire [7:0] _T_185; // @[NV_NVDLA_arb.scala 45:36:@2016.4]
  wire [8:0] _T_187; // @[NV_NVDLA_arb.scala 45:36:@2018.4]
  wire [8:0] _T_188; // @[NV_NVDLA_arb.scala 45:36:@2019.4]
  wire [7:0] _T_189; // @[NV_NVDLA_arb.scala 45:36:@2020.4]
  wire [8:0] _T_191; // @[NV_NVDLA_arb.scala 45:36:@2022.4]
  wire [8:0] _T_192; // @[NV_NVDLA_arb.scala 45:36:@2023.4]
  wire [7:0] _T_193; // @[NV_NVDLA_arb.scala 45:36:@2024.4]
  wire [8:0] _T_195; // @[NV_NVDLA_arb.scala 45:36:@2026.4]
  wire [8:0] _T_196; // @[NV_NVDLA_arb.scala 45:36:@2027.4]
  wire [7:0] _T_197; // @[NV_NVDLA_arb.scala 45:36:@2028.4]
  wire [8:0] _T_199; // @[NV_NVDLA_arb.scala 45:36:@2030.4]
  wire [8:0] _T_200; // @[NV_NVDLA_arb.scala 45:36:@2031.4]
  wire [7:0] _T_201; // @[NV_NVDLA_arb.scala 45:36:@2032.4]
  reg [9:0] _T_212; // @[NV_NVDLA_arb.scala 49:26:@2042.4]
  reg [31:0] _RAND_0;
  reg [7:0] _T_215; // @[NV_NVDLA_arb.scala 50:26:@2043.4]
  reg [31:0] _RAND_1;
  wire  _T_219; // @[NV_NVDLA_arb.scala 54:14:@2046.4]
  wire  _T_221; // @[NV_NVDLA_arb.scala 54:37:@2047.4]
  wire  _T_222; // @[NV_NVDLA_arb.scala 54:31:@2048.4]
  wire [9:0] _T_231; // @[NV_NVDLA_arb.scala 55:31:@2058.6]
  wire [9:0] _GEN_0; // @[NV_NVDLA_arb.scala 54:45:@2049.4]
  wire  _T_236; // @[NV_NVDLA_arb.scala 68:18:@2064.4]
  wire [9:0] _T_237; // @[NV_NVDLA_arb.scala 68:35:@2065.4]
  wire  _T_239; // @[NV_NVDLA_arb.scala 68:46:@2066.4]
  wire  _T_240; // @[NV_NVDLA_arb.scala 68:28:@2067.4]
  wire  _T_241; // @[NV_NVDLA_arb.scala 68:26:@2068.4]
  wire [25:0] _T_914; // @[NV_NVDLA_arb.scala 70:52:@2640.6]
  wire [9:0] _T_915; // @[NV_NVDLA_arb.scala 70:59:@2641.6]
  wire  _T_916; // @[NV_NVDLA_arb.scala 70:26:@2642.6]
  wire  _T_959; // @[NV_NVDLA_arb.scala 72:84:@2675.8]
  wire  _T_960; // @[NV_NVDLA_arb.scala 72:84:@2676.8]
  wire  _T_961; // @[NV_NVDLA_arb.scala 72:84:@2677.8]
  wire  _T_962; // @[NV_NVDLA_arb.scala 72:84:@2678.8]
  wire  _T_963; // @[NV_NVDLA_arb.scala 72:84:@2679.8]
  wire  _T_964; // @[NV_NVDLA_arb.scala 72:84:@2680.8]
  wire  _T_965; // @[NV_NVDLA_arb.scala 72:84:@2681.8]
  wire  _T_966; // @[NV_NVDLA_arb.scala 72:84:@2682.8]
  wire  _T_967; // @[NV_NVDLA_arb.scala 72:84:@2683.8]
  wire  _T_968; // @[NV_NVDLA_arb.scala 72:84:@2684.8]
  wire [7:0] _T_969; // @[Mux.scala 61:16:@2685.8]
  wire [7:0] _T_970; // @[Mux.scala 61:16:@2686.8]
  wire [7:0] _T_971; // @[Mux.scala 61:16:@2687.8]
  wire [7:0] _T_972; // @[Mux.scala 61:16:@2688.8]
  wire [7:0] _T_973; // @[Mux.scala 61:16:@2689.8]
  wire [7:0] _T_974; // @[Mux.scala 61:16:@2690.8]
  wire [7:0] _T_975; // @[Mux.scala 61:16:@2691.8]
  wire [7:0] _T_976; // @[Mux.scala 61:16:@2692.8]
  wire [7:0] _T_977; // @[Mux.scala 61:16:@2693.8]
  wire [7:0] _T_978; // @[Mux.scala 61:16:@2694.8]
  wire [25:0] _T_847; // @[NV_NVDLA_arb.scala 70:52:@2583.6]
  wire [9:0] _T_848; // @[NV_NVDLA_arb.scala 70:59:@2584.6]
  wire  _T_849; // @[NV_NVDLA_arb.scala 70:26:@2585.6]
  wire [7:0] _T_902; // @[Mux.scala 61:16:@2628.8]
  wire [7:0] _T_903; // @[Mux.scala 61:16:@2629.8]
  wire [7:0] _T_904; // @[Mux.scala 61:16:@2630.8]
  wire [7:0] _T_905; // @[Mux.scala 61:16:@2631.8]
  wire [7:0] _T_906; // @[Mux.scala 61:16:@2632.8]
  wire [7:0] _T_907; // @[Mux.scala 61:16:@2633.8]
  wire [7:0] _T_908; // @[Mux.scala 61:16:@2634.8]
  wire [7:0] _T_909; // @[Mux.scala 61:16:@2635.8]
  wire [7:0] _T_910; // @[Mux.scala 61:16:@2636.8]
  wire [7:0] _T_911; // @[Mux.scala 61:16:@2637.8]
  wire [25:0] _T_780; // @[NV_NVDLA_arb.scala 70:52:@2526.6]
  wire [9:0] _T_781; // @[NV_NVDLA_arb.scala 70:59:@2527.6]
  wire  _T_782; // @[NV_NVDLA_arb.scala 70:26:@2528.6]
  wire [7:0] _T_835; // @[Mux.scala 61:16:@2571.8]
  wire [7:0] _T_836; // @[Mux.scala 61:16:@2572.8]
  wire [7:0] _T_837; // @[Mux.scala 61:16:@2573.8]
  wire [7:0] _T_838; // @[Mux.scala 61:16:@2574.8]
  wire [7:0] _T_839; // @[Mux.scala 61:16:@2575.8]
  wire [7:0] _T_840; // @[Mux.scala 61:16:@2576.8]
  wire [7:0] _T_841; // @[Mux.scala 61:16:@2577.8]
  wire [7:0] _T_842; // @[Mux.scala 61:16:@2578.8]
  wire [7:0] _T_843; // @[Mux.scala 61:16:@2579.8]
  wire [7:0] _T_844; // @[Mux.scala 61:16:@2580.8]
  wire [17:0] _T_713; // @[NV_NVDLA_arb.scala 70:52:@2469.6]
  wire [9:0] _T_714; // @[NV_NVDLA_arb.scala 70:59:@2470.6]
  wire  _T_715; // @[NV_NVDLA_arb.scala 70:26:@2471.6]
  wire [7:0] _T_768; // @[Mux.scala 61:16:@2514.8]
  wire [7:0] _T_769; // @[Mux.scala 61:16:@2515.8]
  wire [7:0] _T_770; // @[Mux.scala 61:16:@2516.8]
  wire [7:0] _T_771; // @[Mux.scala 61:16:@2517.8]
  wire [7:0] _T_772; // @[Mux.scala 61:16:@2518.8]
  wire [7:0] _T_773; // @[Mux.scala 61:16:@2519.8]
  wire [7:0] _T_774; // @[Mux.scala 61:16:@2520.8]
  wire [7:0] _T_775; // @[Mux.scala 61:16:@2521.8]
  wire [7:0] _T_776; // @[Mux.scala 61:16:@2522.8]
  wire [7:0] _T_777; // @[Mux.scala 61:16:@2523.8]
  wire [17:0] _T_646; // @[NV_NVDLA_arb.scala 70:52:@2412.6]
  wire [9:0] _T_647; // @[NV_NVDLA_arb.scala 70:59:@2413.6]
  wire  _T_648; // @[NV_NVDLA_arb.scala 70:26:@2414.6]
  wire [7:0] _T_701; // @[Mux.scala 61:16:@2457.8]
  wire [7:0] _T_702; // @[Mux.scala 61:16:@2458.8]
  wire [7:0] _T_703; // @[Mux.scala 61:16:@2459.8]
  wire [7:0] _T_704; // @[Mux.scala 61:16:@2460.8]
  wire [7:0] _T_705; // @[Mux.scala 61:16:@2461.8]
  wire [7:0] _T_706; // @[Mux.scala 61:16:@2462.8]
  wire [7:0] _T_707; // @[Mux.scala 61:16:@2463.8]
  wire [7:0] _T_708; // @[Mux.scala 61:16:@2464.8]
  wire [7:0] _T_709; // @[Mux.scala 61:16:@2465.8]
  wire [7:0] _T_710; // @[Mux.scala 61:16:@2466.8]
  wire [17:0] _T_579; // @[NV_NVDLA_arb.scala 70:52:@2355.6]
  wire [9:0] _T_580; // @[NV_NVDLA_arb.scala 70:59:@2356.6]
  wire  _T_581; // @[NV_NVDLA_arb.scala 70:26:@2357.6]
  wire [7:0] _T_634; // @[Mux.scala 61:16:@2400.8]
  wire [7:0] _T_635; // @[Mux.scala 61:16:@2401.8]
  wire [7:0] _T_636; // @[Mux.scala 61:16:@2402.8]
  wire [7:0] _T_637; // @[Mux.scala 61:16:@2403.8]
  wire [7:0] _T_638; // @[Mux.scala 61:16:@2404.8]
  wire [7:0] _T_639; // @[Mux.scala 61:16:@2405.8]
  wire [7:0] _T_640; // @[Mux.scala 61:16:@2406.8]
  wire [7:0] _T_641; // @[Mux.scala 61:16:@2407.8]
  wire [7:0] _T_642; // @[Mux.scala 61:16:@2408.8]
  wire [7:0] _T_643; // @[Mux.scala 61:16:@2409.8]
  wire [17:0] _T_512; // @[NV_NVDLA_arb.scala 70:52:@2298.6]
  wire [9:0] _T_513; // @[NV_NVDLA_arb.scala 70:59:@2299.6]
  wire  _T_514; // @[NV_NVDLA_arb.scala 70:26:@2300.6]
  wire [7:0] _T_567; // @[Mux.scala 61:16:@2343.8]
  wire [7:0] _T_568; // @[Mux.scala 61:16:@2344.8]
  wire [7:0] _T_569; // @[Mux.scala 61:16:@2345.8]
  wire [7:0] _T_570; // @[Mux.scala 61:16:@2346.8]
  wire [7:0] _T_571; // @[Mux.scala 61:16:@2347.8]
  wire [7:0] _T_572; // @[Mux.scala 61:16:@2348.8]
  wire [7:0] _T_573; // @[Mux.scala 61:16:@2349.8]
  wire [7:0] _T_574; // @[Mux.scala 61:16:@2350.8]
  wire [7:0] _T_575; // @[Mux.scala 61:16:@2351.8]
  wire [7:0] _T_576; // @[Mux.scala 61:16:@2352.8]
  wire [13:0] _T_445; // @[NV_NVDLA_arb.scala 70:52:@2241.6]
  wire [9:0] _T_446; // @[NV_NVDLA_arb.scala 70:59:@2242.6]
  wire  _T_447; // @[NV_NVDLA_arb.scala 70:26:@2243.6]
  wire [7:0] _T_500; // @[Mux.scala 61:16:@2286.8]
  wire [7:0] _T_501; // @[Mux.scala 61:16:@2287.8]
  wire [7:0] _T_502; // @[Mux.scala 61:16:@2288.8]
  wire [7:0] _T_503; // @[Mux.scala 61:16:@2289.8]
  wire [7:0] _T_504; // @[Mux.scala 61:16:@2290.8]
  wire [7:0] _T_505; // @[Mux.scala 61:16:@2291.8]
  wire [7:0] _T_506; // @[Mux.scala 61:16:@2292.8]
  wire [7:0] _T_507; // @[Mux.scala 61:16:@2293.8]
  wire [7:0] _T_508; // @[Mux.scala 61:16:@2294.8]
  wire [7:0] _T_509; // @[Mux.scala 61:16:@2295.8]
  wire [13:0] _T_378; // @[NV_NVDLA_arb.scala 70:52:@2184.6]
  wire [9:0] _T_379; // @[NV_NVDLA_arb.scala 70:59:@2185.6]
  wire  _T_380; // @[NV_NVDLA_arb.scala 70:26:@2186.6]
  wire [7:0] _T_433; // @[Mux.scala 61:16:@2229.8]
  wire [7:0] _T_434; // @[Mux.scala 61:16:@2230.8]
  wire [7:0] _T_435; // @[Mux.scala 61:16:@2231.8]
  wire [7:0] _T_436; // @[Mux.scala 61:16:@2232.8]
  wire [7:0] _T_437; // @[Mux.scala 61:16:@2233.8]
  wire [7:0] _T_438; // @[Mux.scala 61:16:@2234.8]
  wire [7:0] _T_439; // @[Mux.scala 61:16:@2235.8]
  wire [7:0] _T_440; // @[Mux.scala 61:16:@2236.8]
  wire [7:0] _T_441; // @[Mux.scala 61:16:@2237.8]
  wire [7:0] _T_442; // @[Mux.scala 61:16:@2238.8]
  wire [11:0] _T_311; // @[NV_NVDLA_arb.scala 70:52:@2127.6]
  wire [9:0] _T_312; // @[NV_NVDLA_arb.scala 70:59:@2128.6]
  wire  _T_313; // @[NV_NVDLA_arb.scala 70:26:@2129.6]
  wire [7:0] _T_366; // @[Mux.scala 61:16:@2172.8]
  wire [7:0] _T_367; // @[Mux.scala 61:16:@2173.8]
  wire [7:0] _T_368; // @[Mux.scala 61:16:@2174.8]
  wire [7:0] _T_369; // @[Mux.scala 61:16:@2175.8]
  wire [7:0] _T_370; // @[Mux.scala 61:16:@2176.8]
  wire [7:0] _T_371; // @[Mux.scala 61:16:@2177.8]
  wire [7:0] _T_372; // @[Mux.scala 61:16:@2178.8]
  wire [7:0] _T_373; // @[Mux.scala 61:16:@2179.8]
  wire [7:0] _T_374; // @[Mux.scala 61:16:@2180.8]
  wire [7:0] _T_375; // @[Mux.scala 61:16:@2181.8]
  wire [11:0] _T_244; // @[NV_NVDLA_arb.scala 70:52:@2070.6]
  wire [9:0] _T_245; // @[NV_NVDLA_arb.scala 70:59:@2071.6]
  wire  _T_246; // @[NV_NVDLA_arb.scala 70:26:@2072.6]
  wire [7:0] _GEN_3; // @[NV_NVDLA_arb.scala 70:66:@2073.6]
  wire [7:0] _GEN_5; // @[NV_NVDLA_arb.scala 70:66:@2130.6]
  wire [7:0] _GEN_7; // @[NV_NVDLA_arb.scala 70:66:@2187.6]
  wire [7:0] _GEN_9; // @[NV_NVDLA_arb.scala 70:66:@2244.6]
  wire [7:0] _GEN_11; // @[NV_NVDLA_arb.scala 70:66:@2301.6]
  wire [7:0] _GEN_13; // @[NV_NVDLA_arb.scala 70:66:@2358.6]
  wire [7:0] _GEN_15; // @[NV_NVDLA_arb.scala 70:66:@2415.6]
  wire [7:0] _GEN_17; // @[NV_NVDLA_arb.scala 70:66:@2472.6]
  wire [7:0] _GEN_19; // @[NV_NVDLA_arb.scala 70:66:@2529.6]
  wire [7:0] _GEN_21; // @[NV_NVDLA_arb.scala 70:66:@2586.6]
  wire [7:0] _GEN_23; // @[NV_NVDLA_arb.scala 70:66:@2643.6]
  wire [8:0] _T_980; // @[NV_NVDLA_arb.scala 78:32:@2700.6]
  wire [8:0] _T_981; // @[NV_NVDLA_arb.scala 78:32:@2701.6]
  wire [7:0] _T_982; // @[NV_NVDLA_arb.scala 78:32:@2702.6]
  wire [7:0] _GEN_25; // @[NV_NVDLA_arb.scala 68:52:@2069.4]
  wire [7:0] _GEN_1; // @[NV_NVDLA_arb.scala 54:45:@2049.4]
  wire [9:0] _T_278; // @[Mux.scala 61:16:@2094.8]
  wire [9:0] _T_279; // @[Mux.scala 61:16:@2095.8]
  wire [9:0] _T_280; // @[Mux.scala 61:16:@2096.8]
  wire [9:0] _T_281; // @[Mux.scala 61:16:@2097.8]
  wire [9:0] _T_282; // @[Mux.scala 61:16:@2098.8]
  wire [9:0] _T_283; // @[Mux.scala 61:16:@2099.8]
  wire [9:0] _T_284; // @[Mux.scala 61:16:@2100.8]
  wire [9:0] _T_285; // @[Mux.scala 61:16:@2101.8]
  wire [9:0] _T_286; // @[Mux.scala 61:16:@2102.8]
  wire [9:0] _T_287; // @[Mux.scala 61:16:@2103.8]
  wire [9:0] _GEN_2; // @[NV_NVDLA_arb.scala 70:66:@2073.6]
  wire [9:0] _T_345; // @[Mux.scala 61:16:@2151.8]
  wire [9:0] _T_346; // @[Mux.scala 61:16:@2152.8]
  wire [9:0] _T_347; // @[Mux.scala 61:16:@2153.8]
  wire [9:0] _T_348; // @[Mux.scala 61:16:@2154.8]
  wire [9:0] _T_349; // @[Mux.scala 61:16:@2155.8]
  wire [9:0] _T_350; // @[Mux.scala 61:16:@2156.8]
  wire [9:0] _T_351; // @[Mux.scala 61:16:@2157.8]
  wire [9:0] _T_352; // @[Mux.scala 61:16:@2158.8]
  wire [9:0] _T_353; // @[Mux.scala 61:16:@2159.8]
  wire [9:0] _T_354; // @[Mux.scala 61:16:@2160.8]
  wire [9:0] _GEN_4; // @[NV_NVDLA_arb.scala 70:66:@2130.6]
  wire [9:0] _T_412; // @[Mux.scala 61:16:@2208.8]
  wire [9:0] _T_413; // @[Mux.scala 61:16:@2209.8]
  wire [9:0] _T_414; // @[Mux.scala 61:16:@2210.8]
  wire [9:0] _T_415; // @[Mux.scala 61:16:@2211.8]
  wire [9:0] _T_416; // @[Mux.scala 61:16:@2212.8]
  wire [9:0] _T_417; // @[Mux.scala 61:16:@2213.8]
  wire [9:0] _T_418; // @[Mux.scala 61:16:@2214.8]
  wire [9:0] _T_419; // @[Mux.scala 61:16:@2215.8]
  wire [9:0] _T_420; // @[Mux.scala 61:16:@2216.8]
  wire [9:0] _T_421; // @[Mux.scala 61:16:@2217.8]
  wire [9:0] _GEN_6; // @[NV_NVDLA_arb.scala 70:66:@2187.6]
  wire [9:0] _T_479; // @[Mux.scala 61:16:@2265.8]
  wire [9:0] _T_480; // @[Mux.scala 61:16:@2266.8]
  wire [9:0] _T_481; // @[Mux.scala 61:16:@2267.8]
  wire [9:0] _T_482; // @[Mux.scala 61:16:@2268.8]
  wire [9:0] _T_483; // @[Mux.scala 61:16:@2269.8]
  wire [9:0] _T_484; // @[Mux.scala 61:16:@2270.8]
  wire [9:0] _T_485; // @[Mux.scala 61:16:@2271.8]
  wire [9:0] _T_486; // @[Mux.scala 61:16:@2272.8]
  wire [9:0] _T_487; // @[Mux.scala 61:16:@2273.8]
  wire [9:0] _T_488; // @[Mux.scala 61:16:@2274.8]
  wire [9:0] _GEN_8; // @[NV_NVDLA_arb.scala 70:66:@2244.6]
  wire [9:0] _T_546; // @[Mux.scala 61:16:@2322.8]
  wire [9:0] _T_547; // @[Mux.scala 61:16:@2323.8]
  wire [9:0] _T_548; // @[Mux.scala 61:16:@2324.8]
  wire [9:0] _T_549; // @[Mux.scala 61:16:@2325.8]
  wire [9:0] _T_550; // @[Mux.scala 61:16:@2326.8]
  wire [9:0] _T_551; // @[Mux.scala 61:16:@2327.8]
  wire [9:0] _T_552; // @[Mux.scala 61:16:@2328.8]
  wire [9:0] _T_553; // @[Mux.scala 61:16:@2329.8]
  wire [9:0] _T_554; // @[Mux.scala 61:16:@2330.8]
  wire [9:0] _T_555; // @[Mux.scala 61:16:@2331.8]
  wire [9:0] _GEN_10; // @[NV_NVDLA_arb.scala 70:66:@2301.6]
  wire [9:0] _T_613; // @[Mux.scala 61:16:@2379.8]
  wire [9:0] _T_614; // @[Mux.scala 61:16:@2380.8]
  wire [9:0] _T_615; // @[Mux.scala 61:16:@2381.8]
  wire [9:0] _T_616; // @[Mux.scala 61:16:@2382.8]
  wire [9:0] _T_617; // @[Mux.scala 61:16:@2383.8]
  wire [9:0] _T_618; // @[Mux.scala 61:16:@2384.8]
  wire [9:0] _T_619; // @[Mux.scala 61:16:@2385.8]
  wire [9:0] _T_620; // @[Mux.scala 61:16:@2386.8]
  wire [9:0] _T_621; // @[Mux.scala 61:16:@2387.8]
  wire [9:0] _T_622; // @[Mux.scala 61:16:@2388.8]
  wire [9:0] _GEN_12; // @[NV_NVDLA_arb.scala 70:66:@2358.6]
  wire [9:0] _T_680; // @[Mux.scala 61:16:@2436.8]
  wire [9:0] _T_681; // @[Mux.scala 61:16:@2437.8]
  wire [9:0] _T_682; // @[Mux.scala 61:16:@2438.8]
  wire [9:0] _T_683; // @[Mux.scala 61:16:@2439.8]
  wire [9:0] _T_684; // @[Mux.scala 61:16:@2440.8]
  wire [9:0] _T_685; // @[Mux.scala 61:16:@2441.8]
  wire [9:0] _T_686; // @[Mux.scala 61:16:@2442.8]
  wire [9:0] _T_687; // @[Mux.scala 61:16:@2443.8]
  wire [9:0] _T_688; // @[Mux.scala 61:16:@2444.8]
  wire [9:0] _T_689; // @[Mux.scala 61:16:@2445.8]
  wire [9:0] _GEN_14; // @[NV_NVDLA_arb.scala 70:66:@2415.6]
  wire [9:0] _T_747; // @[Mux.scala 61:16:@2493.8]
  wire [9:0] _T_748; // @[Mux.scala 61:16:@2494.8]
  wire [9:0] _T_749; // @[Mux.scala 61:16:@2495.8]
  wire [9:0] _T_750; // @[Mux.scala 61:16:@2496.8]
  wire [9:0] _T_751; // @[Mux.scala 61:16:@2497.8]
  wire [9:0] _T_752; // @[Mux.scala 61:16:@2498.8]
  wire [9:0] _T_753; // @[Mux.scala 61:16:@2499.8]
  wire [9:0] _T_754; // @[Mux.scala 61:16:@2500.8]
  wire [9:0] _T_755; // @[Mux.scala 61:16:@2501.8]
  wire [9:0] _T_756; // @[Mux.scala 61:16:@2502.8]
  wire [9:0] _GEN_16; // @[NV_NVDLA_arb.scala 70:66:@2472.6]
  wire [9:0] _T_814; // @[Mux.scala 61:16:@2550.8]
  wire [9:0] _T_815; // @[Mux.scala 61:16:@2551.8]
  wire [9:0] _T_816; // @[Mux.scala 61:16:@2552.8]
  wire [9:0] _T_817; // @[Mux.scala 61:16:@2553.8]
  wire [9:0] _T_818; // @[Mux.scala 61:16:@2554.8]
  wire [9:0] _T_819; // @[Mux.scala 61:16:@2555.8]
  wire [9:0] _T_820; // @[Mux.scala 61:16:@2556.8]
  wire [9:0] _T_821; // @[Mux.scala 61:16:@2557.8]
  wire [9:0] _T_822; // @[Mux.scala 61:16:@2558.8]
  wire [9:0] _T_823; // @[Mux.scala 61:16:@2559.8]
  wire [9:0] _GEN_18; // @[NV_NVDLA_arb.scala 70:66:@2529.6]
  wire [9:0] _T_881; // @[Mux.scala 61:16:@2607.8]
  wire [9:0] _T_882; // @[Mux.scala 61:16:@2608.8]
  wire [9:0] _T_883; // @[Mux.scala 61:16:@2609.8]
  wire [9:0] _T_884; // @[Mux.scala 61:16:@2610.8]
  wire [9:0] _T_885; // @[Mux.scala 61:16:@2611.8]
  wire [9:0] _T_886; // @[Mux.scala 61:16:@2612.8]
  wire [9:0] _T_887; // @[Mux.scala 61:16:@2613.8]
  wire [9:0] _T_888; // @[Mux.scala 61:16:@2614.8]
  wire [9:0] _T_889; // @[Mux.scala 61:16:@2615.8]
  wire [9:0] _T_890; // @[Mux.scala 61:16:@2616.8]
  wire [9:0] _GEN_20; // @[NV_NVDLA_arb.scala 70:66:@2586.6]
  wire [9:0] _GEN_22; // @[NV_NVDLA_arb.scala 70:66:@2643.6]
  wire [9:0] _GEN_24; // @[NV_NVDLA_arb.scala 68:52:@2069.4]
  wire  _T_984; // @[NV_NVDLA_arb.scala 84:52:@2706.4]
  wire  _T_987; // @[NV_NVDLA_arb.scala 84:52:@2710.4]
  wire  _T_990; // @[NV_NVDLA_arb.scala 84:52:@2714.4]
  wire  _T_993; // @[NV_NVDLA_arb.scala 84:52:@2718.4]
  wire  _T_996; // @[NV_NVDLA_arb.scala 84:52:@2722.4]
  wire  _T_999; // @[NV_NVDLA_arb.scala 84:52:@2726.4]
  wire  _T_1002; // @[NV_NVDLA_arb.scala 84:52:@2730.4]
  wire  _T_1005; // @[NV_NVDLA_arb.scala 84:52:@2734.4]
  wire  _T_1008; // @[NV_NVDLA_arb.scala 84:52:@2738.4]
  wire  _T_1011; // @[NV_NVDLA_arb.scala 84:52:@2742.4]
  assign _T_100 = io_wt_0 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@1961.4]
  assign _T_101 = io_req_0 & _T_100; // @[NV_NVDLA_arb.scala 41:56:@1962.4]
  assign _T_103 = io_wt_1 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@1963.4]
  assign _T_104 = io_req_1 & _T_103; // @[NV_NVDLA_arb.scala 41:56:@1964.4]
  assign _T_106 = io_wt_2 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@1965.4]
  assign _T_107 = io_req_2 & _T_106; // @[NV_NVDLA_arb.scala 41:56:@1966.4]
  assign _T_109 = io_wt_3 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@1967.4]
  assign _T_110 = io_req_3 & _T_109; // @[NV_NVDLA_arb.scala 41:56:@1968.4]
  assign _T_112 = io_wt_4 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@1969.4]
  assign _T_113 = io_req_4 & _T_112; // @[NV_NVDLA_arb.scala 41:56:@1970.4]
  assign _T_115 = io_wt_5 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@1971.4]
  assign _T_116 = io_req_5 & _T_115; // @[NV_NVDLA_arb.scala 41:56:@1972.4]
  assign _T_118 = io_wt_6 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@1973.4]
  assign _T_119 = io_req_6 & _T_118; // @[NV_NVDLA_arb.scala 41:56:@1974.4]
  assign _T_153 = {3'h0,_T_119,_T_116,_T_113,_T_110,_T_107,_T_104,_T_101}; // @[NV_NVDLA_arb.scala 41:75:@2000.4]
  assign _T_171 = io_wt_0 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@2002.4]
  assign _T_172 = $unsigned(_T_171); // @[NV_NVDLA_arb.scala 45:36:@2003.4]
  assign _T_173 = _T_172[7:0]; // @[NV_NVDLA_arb.scala 45:36:@2004.4]
  assign _T_175 = io_wt_1 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@2006.4]
  assign _T_176 = $unsigned(_T_175); // @[NV_NVDLA_arb.scala 45:36:@2007.4]
  assign _T_177 = _T_176[7:0]; // @[NV_NVDLA_arb.scala 45:36:@2008.4]
  assign _T_179 = io_wt_2 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@2010.4]
  assign _T_180 = $unsigned(_T_179); // @[NV_NVDLA_arb.scala 45:36:@2011.4]
  assign _T_181 = _T_180[7:0]; // @[NV_NVDLA_arb.scala 45:36:@2012.4]
  assign _T_183 = io_wt_3 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@2014.4]
  assign _T_184 = $unsigned(_T_183); // @[NV_NVDLA_arb.scala 45:36:@2015.4]
  assign _T_185 = _T_184[7:0]; // @[NV_NVDLA_arb.scala 45:36:@2016.4]
  assign _T_187 = io_wt_4 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@2018.4]
  assign _T_188 = $unsigned(_T_187); // @[NV_NVDLA_arb.scala 45:36:@2019.4]
  assign _T_189 = _T_188[7:0]; // @[NV_NVDLA_arb.scala 45:36:@2020.4]
  assign _T_191 = io_wt_5 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@2022.4]
  assign _T_192 = $unsigned(_T_191); // @[NV_NVDLA_arb.scala 45:36:@2023.4]
  assign _T_193 = _T_192[7:0]; // @[NV_NVDLA_arb.scala 45:36:@2024.4]
  assign _T_195 = io_wt_6 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@2026.4]
  assign _T_196 = $unsigned(_T_195); // @[NV_NVDLA_arb.scala 45:36:@2027.4]
  assign _T_197 = _T_196[7:0]; // @[NV_NVDLA_arb.scala 45:36:@2028.4]
  assign _T_199 = 8'h0 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@2030.4]
  assign _T_200 = $unsigned(_T_199); // @[NV_NVDLA_arb.scala 45:36:@2031.4]
  assign _T_201 = _T_200[7:0]; // @[NV_NVDLA_arb.scala 45:36:@2032.4]
  assign _T_219 = ~ io_gnt_busy; // @[NV_NVDLA_arb.scala 54:14:@2046.4]
  assign _T_221 = _T_153 != 10'h0; // @[NV_NVDLA_arb.scala 54:37:@2047.4]
  assign _T_222 = _T_219 & _T_221; // @[NV_NVDLA_arb.scala 54:31:@2048.4]
  assign _T_231 = {io_gnt_9,io_gnt_8,io_gnt_7,io_gnt_6,io_gnt_5,io_gnt_4,io_gnt_3,io_gnt_2,io_gnt_1,io_gnt_0}; // @[NV_NVDLA_arb.scala 55:31:@2058.6]
  assign _GEN_0 = _T_222 ? _T_231 : _T_212; // @[NV_NVDLA_arb.scala 54:45:@2049.4]
  assign _T_236 = _T_215 == 8'h0; // @[NV_NVDLA_arb.scala 68:18:@2064.4]
  assign _T_237 = _T_153 & _T_212; // @[NV_NVDLA_arb.scala 68:35:@2065.4]
  assign _T_239 = _T_237 != 10'h0; // @[NV_NVDLA_arb.scala 68:46:@2066.4]
  assign _T_240 = ~ _T_239; // @[NV_NVDLA_arb.scala 68:28:@2067.4]
  assign _T_241 = _T_236 | _T_240; // @[NV_NVDLA_arb.scala 68:26:@2068.4]
  assign _T_914 = 26'h1 << 4'ha; // @[NV_NVDLA_arb.scala 70:52:@2640.6]
  assign _T_915 = _T_914[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2641.6]
  assign _T_916 = _T_212 == _T_915; // @[NV_NVDLA_arb.scala 70:26:@2642.6]
  assign _T_959 = _T_153[0]; // @[NV_NVDLA_arb.scala 72:84:@2675.8]
  assign _T_960 = _T_153[1]; // @[NV_NVDLA_arb.scala 72:84:@2676.8]
  assign _T_961 = _T_153[2]; // @[NV_NVDLA_arb.scala 72:84:@2677.8]
  assign _T_962 = _T_153[3]; // @[NV_NVDLA_arb.scala 72:84:@2678.8]
  assign _T_963 = _T_153[4]; // @[NV_NVDLA_arb.scala 72:84:@2679.8]
  assign _T_964 = _T_153[5]; // @[NV_NVDLA_arb.scala 72:84:@2680.8]
  assign _T_965 = _T_153[6]; // @[NV_NVDLA_arb.scala 72:84:@2681.8]
  assign _T_966 = _T_153[7]; // @[NV_NVDLA_arb.scala 72:84:@2682.8]
  assign _T_967 = _T_153[8]; // @[NV_NVDLA_arb.scala 72:84:@2683.8]
  assign _T_968 = _T_153[9]; // @[NV_NVDLA_arb.scala 72:84:@2684.8]
  assign _T_969 = _T_968 ? _T_201 : 8'h0; // @[Mux.scala 61:16:@2685.8]
  assign _T_970 = _T_967 ? _T_201 : _T_969; // @[Mux.scala 61:16:@2686.8]
  assign _T_971 = _T_966 ? _T_201 : _T_970; // @[Mux.scala 61:16:@2687.8]
  assign _T_972 = _T_965 ? _T_197 : _T_971; // @[Mux.scala 61:16:@2688.8]
  assign _T_973 = _T_964 ? _T_193 : _T_972; // @[Mux.scala 61:16:@2689.8]
  assign _T_974 = _T_963 ? _T_189 : _T_973; // @[Mux.scala 61:16:@2690.8]
  assign _T_975 = _T_962 ? _T_185 : _T_974; // @[Mux.scala 61:16:@2691.8]
  assign _T_976 = _T_961 ? _T_181 : _T_975; // @[Mux.scala 61:16:@2692.8]
  assign _T_977 = _T_960 ? _T_177 : _T_976; // @[Mux.scala 61:16:@2693.8]
  assign _T_978 = _T_959 ? _T_173 : _T_977; // @[Mux.scala 61:16:@2694.8]
  assign _T_847 = 26'h1 << 4'h9; // @[NV_NVDLA_arb.scala 70:52:@2583.6]
  assign _T_848 = _T_847[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2584.6]
  assign _T_849 = _T_212 == _T_848; // @[NV_NVDLA_arb.scala 70:26:@2585.6]
  assign _T_902 = _T_967 ? _T_201 : 8'h0; // @[Mux.scala 61:16:@2628.8]
  assign _T_903 = _T_966 ? _T_201 : _T_902; // @[Mux.scala 61:16:@2629.8]
  assign _T_904 = _T_965 ? _T_197 : _T_903; // @[Mux.scala 61:16:@2630.8]
  assign _T_905 = _T_964 ? _T_193 : _T_904; // @[Mux.scala 61:16:@2631.8]
  assign _T_906 = _T_963 ? _T_189 : _T_905; // @[Mux.scala 61:16:@2632.8]
  assign _T_907 = _T_962 ? _T_185 : _T_906; // @[Mux.scala 61:16:@2633.8]
  assign _T_908 = _T_961 ? _T_181 : _T_907; // @[Mux.scala 61:16:@2634.8]
  assign _T_909 = _T_960 ? _T_177 : _T_908; // @[Mux.scala 61:16:@2635.8]
  assign _T_910 = _T_959 ? _T_173 : _T_909; // @[Mux.scala 61:16:@2636.8]
  assign _T_911 = _T_968 ? _T_201 : _T_910; // @[Mux.scala 61:16:@2637.8]
  assign _T_780 = 26'h1 << 4'h8; // @[NV_NVDLA_arb.scala 70:52:@2526.6]
  assign _T_781 = _T_780[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2527.6]
  assign _T_782 = _T_212 == _T_781; // @[NV_NVDLA_arb.scala 70:26:@2528.6]
  assign _T_835 = _T_966 ? _T_201 : 8'h0; // @[Mux.scala 61:16:@2571.8]
  assign _T_836 = _T_965 ? _T_197 : _T_835; // @[Mux.scala 61:16:@2572.8]
  assign _T_837 = _T_964 ? _T_193 : _T_836; // @[Mux.scala 61:16:@2573.8]
  assign _T_838 = _T_963 ? _T_189 : _T_837; // @[Mux.scala 61:16:@2574.8]
  assign _T_839 = _T_962 ? _T_185 : _T_838; // @[Mux.scala 61:16:@2575.8]
  assign _T_840 = _T_961 ? _T_181 : _T_839; // @[Mux.scala 61:16:@2576.8]
  assign _T_841 = _T_960 ? _T_177 : _T_840; // @[Mux.scala 61:16:@2577.8]
  assign _T_842 = _T_959 ? _T_173 : _T_841; // @[Mux.scala 61:16:@2578.8]
  assign _T_843 = _T_968 ? _T_201 : _T_842; // @[Mux.scala 61:16:@2579.8]
  assign _T_844 = _T_967 ? _T_201 : _T_843; // @[Mux.scala 61:16:@2580.8]
  assign _T_713 = 18'h1 << 3'h7; // @[NV_NVDLA_arb.scala 70:52:@2469.6]
  assign _T_714 = _T_713[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2470.6]
  assign _T_715 = _T_212 == _T_714; // @[NV_NVDLA_arb.scala 70:26:@2471.6]
  assign _T_768 = _T_965 ? _T_197 : 8'h0; // @[Mux.scala 61:16:@2514.8]
  assign _T_769 = _T_964 ? _T_193 : _T_768; // @[Mux.scala 61:16:@2515.8]
  assign _T_770 = _T_963 ? _T_189 : _T_769; // @[Mux.scala 61:16:@2516.8]
  assign _T_771 = _T_962 ? _T_185 : _T_770; // @[Mux.scala 61:16:@2517.8]
  assign _T_772 = _T_961 ? _T_181 : _T_771; // @[Mux.scala 61:16:@2518.8]
  assign _T_773 = _T_960 ? _T_177 : _T_772; // @[Mux.scala 61:16:@2519.8]
  assign _T_774 = _T_959 ? _T_173 : _T_773; // @[Mux.scala 61:16:@2520.8]
  assign _T_775 = _T_968 ? _T_201 : _T_774; // @[Mux.scala 61:16:@2521.8]
  assign _T_776 = _T_967 ? _T_201 : _T_775; // @[Mux.scala 61:16:@2522.8]
  assign _T_777 = _T_966 ? _T_201 : _T_776; // @[Mux.scala 61:16:@2523.8]
  assign _T_646 = 18'h1 << 3'h6; // @[NV_NVDLA_arb.scala 70:52:@2412.6]
  assign _T_647 = _T_646[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2413.6]
  assign _T_648 = _T_212 == _T_647; // @[NV_NVDLA_arb.scala 70:26:@2414.6]
  assign _T_701 = _T_964 ? _T_193 : 8'h0; // @[Mux.scala 61:16:@2457.8]
  assign _T_702 = _T_963 ? _T_189 : _T_701; // @[Mux.scala 61:16:@2458.8]
  assign _T_703 = _T_962 ? _T_185 : _T_702; // @[Mux.scala 61:16:@2459.8]
  assign _T_704 = _T_961 ? _T_181 : _T_703; // @[Mux.scala 61:16:@2460.8]
  assign _T_705 = _T_960 ? _T_177 : _T_704; // @[Mux.scala 61:16:@2461.8]
  assign _T_706 = _T_959 ? _T_173 : _T_705; // @[Mux.scala 61:16:@2462.8]
  assign _T_707 = _T_968 ? _T_201 : _T_706; // @[Mux.scala 61:16:@2463.8]
  assign _T_708 = _T_967 ? _T_201 : _T_707; // @[Mux.scala 61:16:@2464.8]
  assign _T_709 = _T_966 ? _T_201 : _T_708; // @[Mux.scala 61:16:@2465.8]
  assign _T_710 = _T_965 ? _T_197 : _T_709; // @[Mux.scala 61:16:@2466.8]
  assign _T_579 = 18'h1 << 3'h5; // @[NV_NVDLA_arb.scala 70:52:@2355.6]
  assign _T_580 = _T_579[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2356.6]
  assign _T_581 = _T_212 == _T_580; // @[NV_NVDLA_arb.scala 70:26:@2357.6]
  assign _T_634 = _T_963 ? _T_189 : 8'h0; // @[Mux.scala 61:16:@2400.8]
  assign _T_635 = _T_962 ? _T_185 : _T_634; // @[Mux.scala 61:16:@2401.8]
  assign _T_636 = _T_961 ? _T_181 : _T_635; // @[Mux.scala 61:16:@2402.8]
  assign _T_637 = _T_960 ? _T_177 : _T_636; // @[Mux.scala 61:16:@2403.8]
  assign _T_638 = _T_959 ? _T_173 : _T_637; // @[Mux.scala 61:16:@2404.8]
  assign _T_639 = _T_968 ? _T_201 : _T_638; // @[Mux.scala 61:16:@2405.8]
  assign _T_640 = _T_967 ? _T_201 : _T_639; // @[Mux.scala 61:16:@2406.8]
  assign _T_641 = _T_966 ? _T_201 : _T_640; // @[Mux.scala 61:16:@2407.8]
  assign _T_642 = _T_965 ? _T_197 : _T_641; // @[Mux.scala 61:16:@2408.8]
  assign _T_643 = _T_964 ? _T_193 : _T_642; // @[Mux.scala 61:16:@2409.8]
  assign _T_512 = 18'h1 << 3'h4; // @[NV_NVDLA_arb.scala 70:52:@2298.6]
  assign _T_513 = _T_512[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2299.6]
  assign _T_514 = _T_212 == _T_513; // @[NV_NVDLA_arb.scala 70:26:@2300.6]
  assign _T_567 = _T_962 ? _T_185 : 8'h0; // @[Mux.scala 61:16:@2343.8]
  assign _T_568 = _T_961 ? _T_181 : _T_567; // @[Mux.scala 61:16:@2344.8]
  assign _T_569 = _T_960 ? _T_177 : _T_568; // @[Mux.scala 61:16:@2345.8]
  assign _T_570 = _T_959 ? _T_173 : _T_569; // @[Mux.scala 61:16:@2346.8]
  assign _T_571 = _T_968 ? _T_201 : _T_570; // @[Mux.scala 61:16:@2347.8]
  assign _T_572 = _T_967 ? _T_201 : _T_571; // @[Mux.scala 61:16:@2348.8]
  assign _T_573 = _T_966 ? _T_201 : _T_572; // @[Mux.scala 61:16:@2349.8]
  assign _T_574 = _T_965 ? _T_197 : _T_573; // @[Mux.scala 61:16:@2350.8]
  assign _T_575 = _T_964 ? _T_193 : _T_574; // @[Mux.scala 61:16:@2351.8]
  assign _T_576 = _T_963 ? _T_189 : _T_575; // @[Mux.scala 61:16:@2352.8]
  assign _T_445 = 14'h1 << 2'h3; // @[NV_NVDLA_arb.scala 70:52:@2241.6]
  assign _T_446 = _T_445[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2242.6]
  assign _T_447 = _T_212 == _T_446; // @[NV_NVDLA_arb.scala 70:26:@2243.6]
  assign _T_500 = _T_961 ? _T_181 : 8'h0; // @[Mux.scala 61:16:@2286.8]
  assign _T_501 = _T_960 ? _T_177 : _T_500; // @[Mux.scala 61:16:@2287.8]
  assign _T_502 = _T_959 ? _T_173 : _T_501; // @[Mux.scala 61:16:@2288.8]
  assign _T_503 = _T_968 ? _T_201 : _T_502; // @[Mux.scala 61:16:@2289.8]
  assign _T_504 = _T_967 ? _T_201 : _T_503; // @[Mux.scala 61:16:@2290.8]
  assign _T_505 = _T_966 ? _T_201 : _T_504; // @[Mux.scala 61:16:@2291.8]
  assign _T_506 = _T_965 ? _T_197 : _T_505; // @[Mux.scala 61:16:@2292.8]
  assign _T_507 = _T_964 ? _T_193 : _T_506; // @[Mux.scala 61:16:@2293.8]
  assign _T_508 = _T_963 ? _T_189 : _T_507; // @[Mux.scala 61:16:@2294.8]
  assign _T_509 = _T_962 ? _T_185 : _T_508; // @[Mux.scala 61:16:@2295.8]
  assign _T_378 = 14'h1 << 2'h2; // @[NV_NVDLA_arb.scala 70:52:@2184.6]
  assign _T_379 = _T_378[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2185.6]
  assign _T_380 = _T_212 == _T_379; // @[NV_NVDLA_arb.scala 70:26:@2186.6]
  assign _T_433 = _T_960 ? _T_177 : 8'h0; // @[Mux.scala 61:16:@2229.8]
  assign _T_434 = _T_959 ? _T_173 : _T_433; // @[Mux.scala 61:16:@2230.8]
  assign _T_435 = _T_968 ? _T_201 : _T_434; // @[Mux.scala 61:16:@2231.8]
  assign _T_436 = _T_967 ? _T_201 : _T_435; // @[Mux.scala 61:16:@2232.8]
  assign _T_437 = _T_966 ? _T_201 : _T_436; // @[Mux.scala 61:16:@2233.8]
  assign _T_438 = _T_965 ? _T_197 : _T_437; // @[Mux.scala 61:16:@2234.8]
  assign _T_439 = _T_964 ? _T_193 : _T_438; // @[Mux.scala 61:16:@2235.8]
  assign _T_440 = _T_963 ? _T_189 : _T_439; // @[Mux.scala 61:16:@2236.8]
  assign _T_441 = _T_962 ? _T_185 : _T_440; // @[Mux.scala 61:16:@2237.8]
  assign _T_442 = _T_961 ? _T_181 : _T_441; // @[Mux.scala 61:16:@2238.8]
  assign _T_311 = 12'h1 << 1'h1; // @[NV_NVDLA_arb.scala 70:52:@2127.6]
  assign _T_312 = _T_311[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2128.6]
  assign _T_313 = _T_212 == _T_312; // @[NV_NVDLA_arb.scala 70:26:@2129.6]
  assign _T_366 = _T_959 ? _T_173 : 8'h0; // @[Mux.scala 61:16:@2172.8]
  assign _T_367 = _T_968 ? _T_201 : _T_366; // @[Mux.scala 61:16:@2173.8]
  assign _T_368 = _T_967 ? _T_201 : _T_367; // @[Mux.scala 61:16:@2174.8]
  assign _T_369 = _T_966 ? _T_201 : _T_368; // @[Mux.scala 61:16:@2175.8]
  assign _T_370 = _T_965 ? _T_197 : _T_369; // @[Mux.scala 61:16:@2176.8]
  assign _T_371 = _T_964 ? _T_193 : _T_370; // @[Mux.scala 61:16:@2177.8]
  assign _T_372 = _T_963 ? _T_189 : _T_371; // @[Mux.scala 61:16:@2178.8]
  assign _T_373 = _T_962 ? _T_185 : _T_372; // @[Mux.scala 61:16:@2179.8]
  assign _T_374 = _T_961 ? _T_181 : _T_373; // @[Mux.scala 61:16:@2180.8]
  assign _T_375 = _T_960 ? _T_177 : _T_374; // @[Mux.scala 61:16:@2181.8]
  assign _T_244 = 12'h1 << 1'h0; // @[NV_NVDLA_arb.scala 70:52:@2070.6]
  assign _T_245 = _T_244[10:1]; // @[NV_NVDLA_arb.scala 70:59:@2071.6]
  assign _T_246 = _T_212 == _T_245; // @[NV_NVDLA_arb.scala 70:26:@2072.6]
  assign _GEN_3 = _T_246 ? _T_978 : 8'h0; // @[NV_NVDLA_arb.scala 70:66:@2073.6]
  assign _GEN_5 = _T_313 ? _T_375 : _GEN_3; // @[NV_NVDLA_arb.scala 70:66:@2130.6]
  assign _GEN_7 = _T_380 ? _T_442 : _GEN_5; // @[NV_NVDLA_arb.scala 70:66:@2187.6]
  assign _GEN_9 = _T_447 ? _T_509 : _GEN_7; // @[NV_NVDLA_arb.scala 70:66:@2244.6]
  assign _GEN_11 = _T_514 ? _T_576 : _GEN_9; // @[NV_NVDLA_arb.scala 70:66:@2301.6]
  assign _GEN_13 = _T_581 ? _T_643 : _GEN_11; // @[NV_NVDLA_arb.scala 70:66:@2358.6]
  assign _GEN_15 = _T_648 ? _T_710 : _GEN_13; // @[NV_NVDLA_arb.scala 70:66:@2415.6]
  assign _GEN_17 = _T_715 ? _T_777 : _GEN_15; // @[NV_NVDLA_arb.scala 70:66:@2472.6]
  assign _GEN_19 = _T_782 ? _T_844 : _GEN_17; // @[NV_NVDLA_arb.scala 70:66:@2529.6]
  assign _GEN_21 = _T_849 ? _T_911 : _GEN_19; // @[NV_NVDLA_arb.scala 70:66:@2586.6]
  assign _GEN_23 = _T_916 ? _T_978 : _GEN_21; // @[NV_NVDLA_arb.scala 70:66:@2643.6]
  assign _T_980 = _T_215 - 8'h1; // @[NV_NVDLA_arb.scala 78:32:@2700.6]
  assign _T_981 = $unsigned(_T_980); // @[NV_NVDLA_arb.scala 78:32:@2701.6]
  assign _T_982 = _T_981[7:0]; // @[NV_NVDLA_arb.scala 78:32:@2702.6]
  assign _GEN_25 = _T_241 ? _GEN_23 : _T_982; // @[NV_NVDLA_arb.scala 68:52:@2069.4]
  assign _GEN_1 = _T_222 ? _GEN_25 : _T_215; // @[NV_NVDLA_arb.scala 54:45:@2049.4]
  assign _T_278 = _T_968 ? 10'h200 : 10'h0; // @[Mux.scala 61:16:@2094.8]
  assign _T_279 = _T_967 ? 10'h100 : _T_278; // @[Mux.scala 61:16:@2095.8]
  assign _T_280 = _T_966 ? 10'h80 : _T_279; // @[Mux.scala 61:16:@2096.8]
  assign _T_281 = _T_965 ? 10'h40 : _T_280; // @[Mux.scala 61:16:@2097.8]
  assign _T_282 = _T_964 ? 10'h20 : _T_281; // @[Mux.scala 61:16:@2098.8]
  assign _T_283 = _T_963 ? 10'h10 : _T_282; // @[Mux.scala 61:16:@2099.8]
  assign _T_284 = _T_962 ? 10'h8 : _T_283; // @[Mux.scala 61:16:@2100.8]
  assign _T_285 = _T_961 ? 10'h4 : _T_284; // @[Mux.scala 61:16:@2101.8]
  assign _T_286 = _T_960 ? 10'h2 : _T_285; // @[Mux.scala 61:16:@2102.8]
  assign _T_287 = _T_959 ? 10'h1 : _T_286; // @[Mux.scala 61:16:@2103.8]
  assign _GEN_2 = _T_246 ? _T_287 : 10'h0; // @[NV_NVDLA_arb.scala 70:66:@2073.6]
  assign _T_345 = _T_959 ? 10'h1 : 10'h0; // @[Mux.scala 61:16:@2151.8]
  assign _T_346 = _T_968 ? 10'h200 : _T_345; // @[Mux.scala 61:16:@2152.8]
  assign _T_347 = _T_967 ? 10'h100 : _T_346; // @[Mux.scala 61:16:@2153.8]
  assign _T_348 = _T_966 ? 10'h80 : _T_347; // @[Mux.scala 61:16:@2154.8]
  assign _T_349 = _T_965 ? 10'h40 : _T_348; // @[Mux.scala 61:16:@2155.8]
  assign _T_350 = _T_964 ? 10'h20 : _T_349; // @[Mux.scala 61:16:@2156.8]
  assign _T_351 = _T_963 ? 10'h10 : _T_350; // @[Mux.scala 61:16:@2157.8]
  assign _T_352 = _T_962 ? 10'h8 : _T_351; // @[Mux.scala 61:16:@2158.8]
  assign _T_353 = _T_961 ? 10'h4 : _T_352; // @[Mux.scala 61:16:@2159.8]
  assign _T_354 = _T_960 ? 10'h2 : _T_353; // @[Mux.scala 61:16:@2160.8]
  assign _GEN_4 = _T_313 ? _T_354 : _GEN_2; // @[NV_NVDLA_arb.scala 70:66:@2130.6]
  assign _T_412 = _T_960 ? 10'h2 : 10'h0; // @[Mux.scala 61:16:@2208.8]
  assign _T_413 = _T_959 ? 10'h1 : _T_412; // @[Mux.scala 61:16:@2209.8]
  assign _T_414 = _T_968 ? 10'h200 : _T_413; // @[Mux.scala 61:16:@2210.8]
  assign _T_415 = _T_967 ? 10'h100 : _T_414; // @[Mux.scala 61:16:@2211.8]
  assign _T_416 = _T_966 ? 10'h80 : _T_415; // @[Mux.scala 61:16:@2212.8]
  assign _T_417 = _T_965 ? 10'h40 : _T_416; // @[Mux.scala 61:16:@2213.8]
  assign _T_418 = _T_964 ? 10'h20 : _T_417; // @[Mux.scala 61:16:@2214.8]
  assign _T_419 = _T_963 ? 10'h10 : _T_418; // @[Mux.scala 61:16:@2215.8]
  assign _T_420 = _T_962 ? 10'h8 : _T_419; // @[Mux.scala 61:16:@2216.8]
  assign _T_421 = _T_961 ? 10'h4 : _T_420; // @[Mux.scala 61:16:@2217.8]
  assign _GEN_6 = _T_380 ? _T_421 : _GEN_4; // @[NV_NVDLA_arb.scala 70:66:@2187.6]
  assign _T_479 = _T_961 ? 10'h4 : 10'h0; // @[Mux.scala 61:16:@2265.8]
  assign _T_480 = _T_960 ? 10'h2 : _T_479; // @[Mux.scala 61:16:@2266.8]
  assign _T_481 = _T_959 ? 10'h1 : _T_480; // @[Mux.scala 61:16:@2267.8]
  assign _T_482 = _T_968 ? 10'h200 : _T_481; // @[Mux.scala 61:16:@2268.8]
  assign _T_483 = _T_967 ? 10'h100 : _T_482; // @[Mux.scala 61:16:@2269.8]
  assign _T_484 = _T_966 ? 10'h80 : _T_483; // @[Mux.scala 61:16:@2270.8]
  assign _T_485 = _T_965 ? 10'h40 : _T_484; // @[Mux.scala 61:16:@2271.8]
  assign _T_486 = _T_964 ? 10'h20 : _T_485; // @[Mux.scala 61:16:@2272.8]
  assign _T_487 = _T_963 ? 10'h10 : _T_486; // @[Mux.scala 61:16:@2273.8]
  assign _T_488 = _T_962 ? 10'h8 : _T_487; // @[Mux.scala 61:16:@2274.8]
  assign _GEN_8 = _T_447 ? _T_488 : _GEN_6; // @[NV_NVDLA_arb.scala 70:66:@2244.6]
  assign _T_546 = _T_962 ? 10'h8 : 10'h0; // @[Mux.scala 61:16:@2322.8]
  assign _T_547 = _T_961 ? 10'h4 : _T_546; // @[Mux.scala 61:16:@2323.8]
  assign _T_548 = _T_960 ? 10'h2 : _T_547; // @[Mux.scala 61:16:@2324.8]
  assign _T_549 = _T_959 ? 10'h1 : _T_548; // @[Mux.scala 61:16:@2325.8]
  assign _T_550 = _T_968 ? 10'h200 : _T_549; // @[Mux.scala 61:16:@2326.8]
  assign _T_551 = _T_967 ? 10'h100 : _T_550; // @[Mux.scala 61:16:@2327.8]
  assign _T_552 = _T_966 ? 10'h80 : _T_551; // @[Mux.scala 61:16:@2328.8]
  assign _T_553 = _T_965 ? 10'h40 : _T_552; // @[Mux.scala 61:16:@2329.8]
  assign _T_554 = _T_964 ? 10'h20 : _T_553; // @[Mux.scala 61:16:@2330.8]
  assign _T_555 = _T_963 ? 10'h10 : _T_554; // @[Mux.scala 61:16:@2331.8]
  assign _GEN_10 = _T_514 ? _T_555 : _GEN_8; // @[NV_NVDLA_arb.scala 70:66:@2301.6]
  assign _T_613 = _T_963 ? 10'h10 : 10'h0; // @[Mux.scala 61:16:@2379.8]
  assign _T_614 = _T_962 ? 10'h8 : _T_613; // @[Mux.scala 61:16:@2380.8]
  assign _T_615 = _T_961 ? 10'h4 : _T_614; // @[Mux.scala 61:16:@2381.8]
  assign _T_616 = _T_960 ? 10'h2 : _T_615; // @[Mux.scala 61:16:@2382.8]
  assign _T_617 = _T_959 ? 10'h1 : _T_616; // @[Mux.scala 61:16:@2383.8]
  assign _T_618 = _T_968 ? 10'h200 : _T_617; // @[Mux.scala 61:16:@2384.8]
  assign _T_619 = _T_967 ? 10'h100 : _T_618; // @[Mux.scala 61:16:@2385.8]
  assign _T_620 = _T_966 ? 10'h80 : _T_619; // @[Mux.scala 61:16:@2386.8]
  assign _T_621 = _T_965 ? 10'h40 : _T_620; // @[Mux.scala 61:16:@2387.8]
  assign _T_622 = _T_964 ? 10'h20 : _T_621; // @[Mux.scala 61:16:@2388.8]
  assign _GEN_12 = _T_581 ? _T_622 : _GEN_10; // @[NV_NVDLA_arb.scala 70:66:@2358.6]
  assign _T_680 = _T_964 ? 10'h20 : 10'h0; // @[Mux.scala 61:16:@2436.8]
  assign _T_681 = _T_963 ? 10'h10 : _T_680; // @[Mux.scala 61:16:@2437.8]
  assign _T_682 = _T_962 ? 10'h8 : _T_681; // @[Mux.scala 61:16:@2438.8]
  assign _T_683 = _T_961 ? 10'h4 : _T_682; // @[Mux.scala 61:16:@2439.8]
  assign _T_684 = _T_960 ? 10'h2 : _T_683; // @[Mux.scala 61:16:@2440.8]
  assign _T_685 = _T_959 ? 10'h1 : _T_684; // @[Mux.scala 61:16:@2441.8]
  assign _T_686 = _T_968 ? 10'h200 : _T_685; // @[Mux.scala 61:16:@2442.8]
  assign _T_687 = _T_967 ? 10'h100 : _T_686; // @[Mux.scala 61:16:@2443.8]
  assign _T_688 = _T_966 ? 10'h80 : _T_687; // @[Mux.scala 61:16:@2444.8]
  assign _T_689 = _T_965 ? 10'h40 : _T_688; // @[Mux.scala 61:16:@2445.8]
  assign _GEN_14 = _T_648 ? _T_689 : _GEN_12; // @[NV_NVDLA_arb.scala 70:66:@2415.6]
  assign _T_747 = _T_965 ? 10'h40 : 10'h0; // @[Mux.scala 61:16:@2493.8]
  assign _T_748 = _T_964 ? 10'h20 : _T_747; // @[Mux.scala 61:16:@2494.8]
  assign _T_749 = _T_963 ? 10'h10 : _T_748; // @[Mux.scala 61:16:@2495.8]
  assign _T_750 = _T_962 ? 10'h8 : _T_749; // @[Mux.scala 61:16:@2496.8]
  assign _T_751 = _T_961 ? 10'h4 : _T_750; // @[Mux.scala 61:16:@2497.8]
  assign _T_752 = _T_960 ? 10'h2 : _T_751; // @[Mux.scala 61:16:@2498.8]
  assign _T_753 = _T_959 ? 10'h1 : _T_752; // @[Mux.scala 61:16:@2499.8]
  assign _T_754 = _T_968 ? 10'h200 : _T_753; // @[Mux.scala 61:16:@2500.8]
  assign _T_755 = _T_967 ? 10'h100 : _T_754; // @[Mux.scala 61:16:@2501.8]
  assign _T_756 = _T_966 ? 10'h80 : _T_755; // @[Mux.scala 61:16:@2502.8]
  assign _GEN_16 = _T_715 ? _T_756 : _GEN_14; // @[NV_NVDLA_arb.scala 70:66:@2472.6]
  assign _T_814 = _T_966 ? 10'h80 : 10'h0; // @[Mux.scala 61:16:@2550.8]
  assign _T_815 = _T_965 ? 10'h40 : _T_814; // @[Mux.scala 61:16:@2551.8]
  assign _T_816 = _T_964 ? 10'h20 : _T_815; // @[Mux.scala 61:16:@2552.8]
  assign _T_817 = _T_963 ? 10'h10 : _T_816; // @[Mux.scala 61:16:@2553.8]
  assign _T_818 = _T_962 ? 10'h8 : _T_817; // @[Mux.scala 61:16:@2554.8]
  assign _T_819 = _T_961 ? 10'h4 : _T_818; // @[Mux.scala 61:16:@2555.8]
  assign _T_820 = _T_960 ? 10'h2 : _T_819; // @[Mux.scala 61:16:@2556.8]
  assign _T_821 = _T_959 ? 10'h1 : _T_820; // @[Mux.scala 61:16:@2557.8]
  assign _T_822 = _T_968 ? 10'h200 : _T_821; // @[Mux.scala 61:16:@2558.8]
  assign _T_823 = _T_967 ? 10'h100 : _T_822; // @[Mux.scala 61:16:@2559.8]
  assign _GEN_18 = _T_782 ? _T_823 : _GEN_16; // @[NV_NVDLA_arb.scala 70:66:@2529.6]
  assign _T_881 = _T_967 ? 10'h100 : 10'h0; // @[Mux.scala 61:16:@2607.8]
  assign _T_882 = _T_966 ? 10'h80 : _T_881; // @[Mux.scala 61:16:@2608.8]
  assign _T_883 = _T_965 ? 10'h40 : _T_882; // @[Mux.scala 61:16:@2609.8]
  assign _T_884 = _T_964 ? 10'h20 : _T_883; // @[Mux.scala 61:16:@2610.8]
  assign _T_885 = _T_963 ? 10'h10 : _T_884; // @[Mux.scala 61:16:@2611.8]
  assign _T_886 = _T_962 ? 10'h8 : _T_885; // @[Mux.scala 61:16:@2612.8]
  assign _T_887 = _T_961 ? 10'h4 : _T_886; // @[Mux.scala 61:16:@2613.8]
  assign _T_888 = _T_960 ? 10'h2 : _T_887; // @[Mux.scala 61:16:@2614.8]
  assign _T_889 = _T_959 ? 10'h1 : _T_888; // @[Mux.scala 61:16:@2615.8]
  assign _T_890 = _T_968 ? 10'h200 : _T_889; // @[Mux.scala 61:16:@2616.8]
  assign _GEN_20 = _T_849 ? _T_890 : _GEN_18; // @[NV_NVDLA_arb.scala 70:66:@2586.6]
  assign _GEN_22 = _T_916 ? _T_287 : _GEN_20; // @[NV_NVDLA_arb.scala 70:66:@2643.6]
  assign _GEN_24 = _T_241 ? _GEN_22 : _T_212; // @[NV_NVDLA_arb.scala 68:52:@2069.4]
  assign _T_984 = _GEN_24[0]; // @[NV_NVDLA_arb.scala 84:52:@2706.4]
  assign _T_987 = _GEN_24[1]; // @[NV_NVDLA_arb.scala 84:52:@2710.4]
  assign _T_990 = _GEN_24[2]; // @[NV_NVDLA_arb.scala 84:52:@2714.4]
  assign _T_993 = _GEN_24[3]; // @[NV_NVDLA_arb.scala 84:52:@2718.4]
  assign _T_996 = _GEN_24[4]; // @[NV_NVDLA_arb.scala 84:52:@2722.4]
  assign _T_999 = _GEN_24[5]; // @[NV_NVDLA_arb.scala 84:52:@2726.4]
  assign _T_1002 = _GEN_24[6]; // @[NV_NVDLA_arb.scala 84:52:@2730.4]
  assign _T_1005 = _GEN_24[7]; // @[NV_NVDLA_arb.scala 84:52:@2734.4]
  assign _T_1008 = _GEN_24[8]; // @[NV_NVDLA_arb.scala 84:52:@2738.4]
  assign _T_1011 = _GEN_24[9]; // @[NV_NVDLA_arb.scala 84:52:@2742.4]
  assign io_gnt_0 = _T_219 & _T_984; // @[NV_NVDLA_arb.scala 84:23:@2708.4]
  assign io_gnt_1 = _T_219 & _T_987; // @[NV_NVDLA_arb.scala 84:23:@2712.4]
  assign io_gnt_2 = _T_219 & _T_990; // @[NV_NVDLA_arb.scala 84:23:@2716.4]
  assign io_gnt_3 = _T_219 & _T_993; // @[NV_NVDLA_arb.scala 84:23:@2720.4]
  assign io_gnt_4 = _T_219 & _T_996; // @[NV_NVDLA_arb.scala 84:23:@2724.4]
  assign io_gnt_5 = _T_219 & _T_999; // @[NV_NVDLA_arb.scala 84:23:@2728.4]
  assign io_gnt_6 = _T_219 & _T_1002; // @[NV_NVDLA_arb.scala 84:23:@2732.4]
  assign io_gnt_7 = _T_219 & _T_1005; // @[NV_NVDLA_arb.scala 84:23:@2736.4]
  assign io_gnt_8 = _T_219 & _T_1008; // @[NV_NVDLA_arb.scala 84:23:@2740.4]
  assign io_gnt_9 = _T_219 & _T_1011; // @[NV_NVDLA_arb.scala 84:23:@2744.4]
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
  _T_212 = _RAND_0[9:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_215 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      _T_212 <= 10'h0;
    end else begin
      if (_T_222) begin
        _T_212 <= _T_231;
      end
    end
    if (reset) begin
      _T_215 <= 8'h0;
    end else begin
      if (_T_222) begin
        if (_T_241) begin
          if (_T_916) begin
            if (_T_959) begin
              _T_215 <= _T_173;
            end else begin
              if (_T_960) begin
                _T_215 <= _T_177;
              end else begin
                if (_T_961) begin
                  _T_215 <= _T_181;
                end else begin
                  if (_T_962) begin
                    _T_215 <= _T_185;
                  end else begin
                    if (_T_963) begin
                      _T_215 <= _T_189;
                    end else begin
                      if (_T_964) begin
                        _T_215 <= _T_193;
                      end else begin
                        if (_T_965) begin
                          _T_215 <= _T_197;
                        end else begin
                          if (_T_966) begin
                            _T_215 <= _T_201;
                          end else begin
                            if (_T_967) begin
                              _T_215 <= _T_201;
                            end else begin
                              if (_T_968) begin
                                _T_215 <= _T_201;
                              end else begin
                                _T_215 <= 8'h0;
                              end
                            end
                          end
                        end
                      end
                    end
                  end
                end
              end
            end
          end else begin
            if (_T_849) begin
              if (_T_968) begin
                _T_215 <= _T_201;
              end else begin
                if (_T_959) begin
                  _T_215 <= _T_173;
                end else begin
                  if (_T_960) begin
                    _T_215 <= _T_177;
                  end else begin
                    if (_T_961) begin
                      _T_215 <= _T_181;
                    end else begin
                      if (_T_962) begin
                        _T_215 <= _T_185;
                      end else begin
                        if (_T_963) begin
                          _T_215 <= _T_189;
                        end else begin
                          if (_T_964) begin
                            _T_215 <= _T_193;
                          end else begin
                            if (_T_965) begin
                              _T_215 <= _T_197;
                            end else begin
                              if (_T_966) begin
                                _T_215 <= _T_201;
                              end else begin
                                if (_T_967) begin
                                  _T_215 <= _T_201;
                                end else begin
                                  _T_215 <= 8'h0;
                                end
                              end
                            end
                          end
                        end
                      end
                    end
                  end
                end
              end
            end else begin
              if (_T_782) begin
                if (_T_967) begin
                  _T_215 <= _T_201;
                end else begin
                  if (_T_968) begin
                    _T_215 <= _T_201;
                  end else begin
                    if (_T_959) begin
                      _T_215 <= _T_173;
                    end else begin
                      if (_T_960) begin
                        _T_215 <= _T_177;
                      end else begin
                        if (_T_961) begin
                          _T_215 <= _T_181;
                        end else begin
                          if (_T_962) begin
                            _T_215 <= _T_185;
                          end else begin
                            if (_T_963) begin
                              _T_215 <= _T_189;
                            end else begin
                              if (_T_964) begin
                                _T_215 <= _T_193;
                              end else begin
                                if (_T_965) begin
                                  _T_215 <= _T_197;
                                end else begin
                                  if (_T_966) begin
                                    _T_215 <= _T_201;
                                  end else begin
                                    _T_215 <= 8'h0;
                                  end
                                end
                              end
                            end
                          end
                        end
                      end
                    end
                  end
                end
              end else begin
                if (_T_715) begin
                  if (_T_966) begin
                    _T_215 <= _T_201;
                  end else begin
                    if (_T_967) begin
                      _T_215 <= _T_201;
                    end else begin
                      if (_T_968) begin
                        _T_215 <= _T_201;
                      end else begin
                        if (_T_959) begin
                          _T_215 <= _T_173;
                        end else begin
                          if (_T_960) begin
                            _T_215 <= _T_177;
                          end else begin
                            if (_T_961) begin
                              _T_215 <= _T_181;
                            end else begin
                              if (_T_962) begin
                                _T_215 <= _T_185;
                              end else begin
                                if (_T_963) begin
                                  _T_215 <= _T_189;
                                end else begin
                                  if (_T_964) begin
                                    _T_215 <= _T_193;
                                  end else begin
                                    if (_T_965) begin
                                      _T_215 <= _T_197;
                                    end else begin
                                      _T_215 <= 8'h0;
                                    end
                                  end
                                end
                              end
                            end
                          end
                        end
                      end
                    end
                  end
                end else begin
                  if (_T_648) begin
                    if (_T_965) begin
                      _T_215 <= _T_197;
                    end else begin
                      if (_T_966) begin
                        _T_215 <= _T_201;
                      end else begin
                        if (_T_967) begin
                          _T_215 <= _T_201;
                        end else begin
                          if (_T_968) begin
                            _T_215 <= _T_201;
                          end else begin
                            if (_T_959) begin
                              _T_215 <= _T_173;
                            end else begin
                              if (_T_960) begin
                                _T_215 <= _T_177;
                              end else begin
                                if (_T_961) begin
                                  _T_215 <= _T_181;
                                end else begin
                                  if (_T_962) begin
                                    _T_215 <= _T_185;
                                  end else begin
                                    if (_T_963) begin
                                      _T_215 <= _T_189;
                                    end else begin
                                      if (_T_964) begin
                                        _T_215 <= _T_193;
                                      end else begin
                                        _T_215 <= 8'h0;
                                      end
                                    end
                                  end
                                end
                              end
                            end
                          end
                        end
                      end
                    end
                  end else begin
                    if (_T_581) begin
                      if (_T_964) begin
                        _T_215 <= _T_193;
                      end else begin
                        if (_T_965) begin
                          _T_215 <= _T_197;
                        end else begin
                          if (_T_966) begin
                            _T_215 <= _T_201;
                          end else begin
                            if (_T_967) begin
                              _T_215 <= _T_201;
                            end else begin
                              if (_T_968) begin
                                _T_215 <= _T_201;
                              end else begin
                                if (_T_959) begin
                                  _T_215 <= _T_173;
                                end else begin
                                  if (_T_960) begin
                                    _T_215 <= _T_177;
                                  end else begin
                                    if (_T_961) begin
                                      _T_215 <= _T_181;
                                    end else begin
                                      if (_T_962) begin
                                        _T_215 <= _T_185;
                                      end else begin
                                        if (_T_963) begin
                                          _T_215 <= _T_189;
                                        end else begin
                                          _T_215 <= 8'h0;
                                        end
                                      end
                                    end
                                  end
                                end
                              end
                            end
                          end
                        end
                      end
                    end else begin
                      if (_T_514) begin
                        if (_T_963) begin
                          _T_215 <= _T_189;
                        end else begin
                          if (_T_964) begin
                            _T_215 <= _T_193;
                          end else begin
                            if (_T_965) begin
                              _T_215 <= _T_197;
                            end else begin
                              if (_T_966) begin
                                _T_215 <= _T_201;
                              end else begin
                                if (_T_967) begin
                                  _T_215 <= _T_201;
                                end else begin
                                  if (_T_968) begin
                                    _T_215 <= _T_201;
                                  end else begin
                                    if (_T_959) begin
                                      _T_215 <= _T_173;
                                    end else begin
                                      if (_T_960) begin
                                        _T_215 <= _T_177;
                                      end else begin
                                        if (_T_961) begin
                                          _T_215 <= _T_181;
                                        end else begin
                                          if (_T_962) begin
                                            _T_215 <= _T_185;
                                          end else begin
                                            _T_215 <= 8'h0;
                                          end
                                        end
                                      end
                                    end
                                  end
                                end
                              end
                            end
                          end
                        end
                      end else begin
                        if (_T_447) begin
                          if (_T_962) begin
                            _T_215 <= _T_185;
                          end else begin
                            if (_T_963) begin
                              _T_215 <= _T_189;
                            end else begin
                              if (_T_964) begin
                                _T_215 <= _T_193;
                              end else begin
                                if (_T_965) begin
                                  _T_215 <= _T_197;
                                end else begin
                                  if (_T_966) begin
                                    _T_215 <= _T_201;
                                  end else begin
                                    if (_T_967) begin
                                      _T_215 <= _T_201;
                                    end else begin
                                      if (_T_968) begin
                                        _T_215 <= _T_201;
                                      end else begin
                                        if (_T_959) begin
                                          _T_215 <= _T_173;
                                        end else begin
                                          if (_T_960) begin
                                            _T_215 <= _T_177;
                                          end else begin
                                            if (_T_961) begin
                                              _T_215 <= _T_181;
                                            end else begin
                                              _T_215 <= 8'h0;
                                            end
                                          end
                                        end
                                      end
                                    end
                                  end
                                end
                              end
                            end
                          end
                        end else begin
                          if (_T_380) begin
                            if (_T_961) begin
                              _T_215 <= _T_181;
                            end else begin
                              if (_T_962) begin
                                _T_215 <= _T_185;
                              end else begin
                                if (_T_963) begin
                                  _T_215 <= _T_189;
                                end else begin
                                  if (_T_964) begin
                                    _T_215 <= _T_193;
                                  end else begin
                                    if (_T_965) begin
                                      _T_215 <= _T_197;
                                    end else begin
                                      if (_T_966) begin
                                        _T_215 <= _T_201;
                                      end else begin
                                        if (_T_967) begin
                                          _T_215 <= _T_201;
                                        end else begin
                                          if (_T_968) begin
                                            _T_215 <= _T_201;
                                          end else begin
                                            if (_T_959) begin
                                              _T_215 <= _T_173;
                                            end else begin
                                              if (_T_960) begin
                                                _T_215 <= _T_177;
                                              end else begin
                                                _T_215 <= 8'h0;
                                              end
                                            end
                                          end
                                        end
                                      end
                                    end
                                  end
                                end
                              end
                            end
                          end else begin
                            if (_T_313) begin
                              if (_T_960) begin
                                _T_215 <= _T_177;
                              end else begin
                                if (_T_961) begin
                                  _T_215 <= _T_181;
                                end else begin
                                  if (_T_962) begin
                                    _T_215 <= _T_185;
                                  end else begin
                                    if (_T_963) begin
                                      _T_215 <= _T_189;
                                    end else begin
                                      if (_T_964) begin
                                        _T_215 <= _T_193;
                                      end else begin
                                        if (_T_965) begin
                                          _T_215 <= _T_197;
                                        end else begin
                                          if (_T_966) begin
                                            _T_215 <= _T_201;
                                          end else begin
                                            if (_T_967) begin
                                              _T_215 <= _T_201;
                                            end else begin
                                              if (_T_968) begin
                                                _T_215 <= _T_201;
                                              end else begin
                                                if (_T_959) begin
                                                  _T_215 <= _T_173;
                                                end else begin
                                                  _T_215 <= 8'h0;
                                                end
                                              end
                                            end
                                          end
                                        end
                                      end
                                    end
                                  end
                                end
                              end
                            end else begin
                              if (_T_246) begin
                                if (_T_959) begin
                                  _T_215 <= _T_173;
                                end else begin
                                  if (_T_960) begin
                                    _T_215 <= _T_177;
                                  end else begin
                                    if (_T_961) begin
                                      _T_215 <= _T_181;
                                    end else begin
                                      if (_T_962) begin
                                        _T_215 <= _T_185;
                                      end else begin
                                        if (_T_963) begin
                                          _T_215 <= _T_189;
                                        end else begin
                                          if (_T_964) begin
                                            _T_215 <= _T_193;
                                          end else begin
                                            if (_T_965) begin
                                              _T_215 <= _T_197;
                                            end else begin
                                              if (_T_966) begin
                                                _T_215 <= _T_201;
                                              end else begin
                                                if (_T_967) begin
                                                  _T_215 <= _T_201;
                                                end else begin
                                                  if (_T_968) begin
                                                    _T_215 <= _T_201;
                                                  end else begin
                                                    _T_215 <= 8'h0;
                                                  end
                                                end
                                              end
                                            end
                                          end
                                        end
                                      end
                                    end
                                  end
                                end
                              end else begin
                                _T_215 <= 8'h0;
                              end
                            end
                          end
                        end
                      end
                    end
                  end
                end
              end
            end
          end
        end else begin
          _T_215 <= _T_982;
        end
      end
    end
  end
endmodule
module NV_NVDLA_IS_pipe_14( // @[:@2746.2]
  input         reset, // @[:@2748.4]
  input         io_clk, // @[:@2749.4]
  output [42:0] io_dout, // @[:@2749.4]
  output        io_vo, // @[:@2749.4]
  input         io_ri, // @[:@2749.4]
  input  [42:0] io_di, // @[:@2749.4]
  input         io_vi, // @[:@2749.4]
  output        io_ro // @[:@2749.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@2751.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@2752.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@2753.4]
  reg [31:0] _RAND_2;
  reg [42:0] skid_flop_di; // @[IS_pipe.scala 57:53:@2754.4]
  reg [63:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@2755.4]
  reg [31:0] _RAND_4;
  reg [42:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@2756.4]
  reg [63:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@2765.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@2770.4]
  wire [42:0] _GEN_1; // @[IS_pipe.scala 76:29:@2771.4]
  wire [42:0] skid_di; // @[IS_pipe.scala 79:19:@2774.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@2776.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@2777.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@2779.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@2782.4]
  wire [42:0] _GEN_3; // @[IS_pipe.scala 87:29:@2783.4]
  assign _GEN_0 = skid_flop_ro ? io_vi : skid_flop_vi; // @[IS_pipe.scala 71:23:@2765.4]
  assign _T_38 = skid_flop_ro & io_vi; // @[IS_pipe.scala 76:22:@2770.4]
  assign _GEN_1 = _T_38 ? io_di : skid_flop_di; // @[IS_pipe.scala 76:29:@2771.4]
  assign skid_di = skid_flop_ro ? io_di : skid_flop_di; // @[IS_pipe.scala 79:19:@2774.4]
  assign _T_40 = ~ pipe_skid_vi; // @[IS_pipe.scala 81:32:@2776.4]
  assign skid_ro = io_ri | _T_40; // @[IS_pipe.scala 81:29:@2777.4]
  assign _GEN_2 = skid_ro ? _GEN_0 : pipe_skid_vi; // @[IS_pipe.scala 83:18:@2779.4]
  assign _T_42 = skid_ro & _GEN_0; // @[IS_pipe.scala 87:18:@2782.4]
  assign _GEN_3 = _T_42 ? skid_di : pipe_skid_di; // @[IS_pipe.scala 87:29:@2783.4]
  assign io_dout = pipe_skid_di; // @[IS_pipe.scala 98:13:@2791.4]
  assign io_vo = pipe_skid_vi; // @[IS_pipe.scala 97:11:@2790.4]
  assign io_ro = ro_out; // @[IS_pipe.scala 96:11:@2789.4]
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
  skid_flop_di = _RAND_3[42:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {2{`RANDOM}};
  pipe_skid_di = _RAND_5[42:0];
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
      skid_flop_di <= 43'h0;
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
      pipe_skid_di <= 43'h0;
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
module NV_NVDLA_MCIF_READ_IG_arb( // @[:@2793.2]
  input         reset, // @[:@2795.4]
  input         io_nvdla_core_clk, // @[:@2796.4]
  output        io_bpt2arb_req_pd_0_ready, // @[:@2796.4]
  input         io_bpt2arb_req_pd_0_valid, // @[:@2796.4]
  input  [42:0] io_bpt2arb_req_pd_0_bits, // @[:@2796.4]
  output        io_bpt2arb_req_pd_1_ready, // @[:@2796.4]
  input         io_bpt2arb_req_pd_1_valid, // @[:@2796.4]
  input  [42:0] io_bpt2arb_req_pd_1_bits, // @[:@2796.4]
  output        io_bpt2arb_req_pd_2_ready, // @[:@2796.4]
  input         io_bpt2arb_req_pd_2_valid, // @[:@2796.4]
  input  [42:0] io_bpt2arb_req_pd_2_bits, // @[:@2796.4]
  output        io_bpt2arb_req_pd_3_ready, // @[:@2796.4]
  input         io_bpt2arb_req_pd_3_valid, // @[:@2796.4]
  input  [42:0] io_bpt2arb_req_pd_3_bits, // @[:@2796.4]
  output        io_bpt2arb_req_pd_4_ready, // @[:@2796.4]
  input         io_bpt2arb_req_pd_4_valid, // @[:@2796.4]
  input  [42:0] io_bpt2arb_req_pd_4_bits, // @[:@2796.4]
  output        io_bpt2arb_req_pd_5_ready, // @[:@2796.4]
  input         io_bpt2arb_req_pd_5_valid, // @[:@2796.4]
  input  [42:0] io_bpt2arb_req_pd_5_bits, // @[:@2796.4]
  output        io_bpt2arb_req_pd_6_ready, // @[:@2796.4]
  input         io_bpt2arb_req_pd_6_valid, // @[:@2796.4]
  input  [42:0] io_bpt2arb_req_pd_6_bits, // @[:@2796.4]
  input         io_arb2spt_req_pd_ready, // @[:@2796.4]
  output        io_arb2spt_req_pd_valid, // @[:@2796.4]
  output [42:0] io_arb2spt_req_pd_bits, // @[:@2796.4]
  input  [7:0]  io_reg2dp_rd_weight_0, // @[:@2796.4]
  input  [7:0]  io_reg2dp_rd_weight_1, // @[:@2796.4]
  input  [7:0]  io_reg2dp_rd_weight_2, // @[:@2796.4]
  input  [7:0]  io_reg2dp_rd_weight_3, // @[:@2796.4]
  input  [7:0]  io_reg2dp_rd_weight_4, // @[:@2796.4]
  input  [7:0]  io_reg2dp_rd_weight_5, // @[:@2796.4]
  input  [7:0]  io_reg2dp_rd_weight_6 // @[:@2796.4]
);
  wire  NV_NVDLA_BC_OS_pipe_reset; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2801.4]
  wire  NV_NVDLA_BC_OS_pipe_io_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2801.4]
  wire  NV_NVDLA_BC_OS_pipe_io_vi; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2801.4]
  wire  NV_NVDLA_BC_OS_pipe_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2801.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_io_di; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2801.4]
  wire  NV_NVDLA_BC_OS_pipe_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2801.4]
  wire  NV_NVDLA_BC_OS_pipe_io_ri; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2801.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2801.4]
  wire  NV_NVDLA_BC_OS_pipe_1_reset; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2804.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2804.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_vi; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2804.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2804.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_1_io_di; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2804.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2804.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_ri; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2804.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_1_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2804.4]
  wire  NV_NVDLA_BC_OS_pipe_2_reset; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2807.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2807.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_vi; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2807.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2807.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_2_io_di; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2807.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2807.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_ri; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2807.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_2_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2807.4]
  wire  NV_NVDLA_BC_OS_pipe_3_reset; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2810.4]
  wire  NV_NVDLA_BC_OS_pipe_3_io_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2810.4]
  wire  NV_NVDLA_BC_OS_pipe_3_io_vi; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2810.4]
  wire  NV_NVDLA_BC_OS_pipe_3_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2810.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_3_io_di; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2810.4]
  wire  NV_NVDLA_BC_OS_pipe_3_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2810.4]
  wire  NV_NVDLA_BC_OS_pipe_3_io_ri; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2810.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_3_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2810.4]
  wire  NV_NVDLA_BC_OS_pipe_4_reset; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2813.4]
  wire  NV_NVDLA_BC_OS_pipe_4_io_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2813.4]
  wire  NV_NVDLA_BC_OS_pipe_4_io_vi; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2813.4]
  wire  NV_NVDLA_BC_OS_pipe_4_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2813.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_4_io_di; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2813.4]
  wire  NV_NVDLA_BC_OS_pipe_4_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2813.4]
  wire  NV_NVDLA_BC_OS_pipe_4_io_ri; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2813.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_4_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2813.4]
  wire  NV_NVDLA_BC_OS_pipe_5_reset; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2816.4]
  wire  NV_NVDLA_BC_OS_pipe_5_io_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2816.4]
  wire  NV_NVDLA_BC_OS_pipe_5_io_vi; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2816.4]
  wire  NV_NVDLA_BC_OS_pipe_5_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2816.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_5_io_di; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2816.4]
  wire  NV_NVDLA_BC_OS_pipe_5_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2816.4]
  wire  NV_NVDLA_BC_OS_pipe_5_io_ri; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2816.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_5_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2816.4]
  wire  NV_NVDLA_BC_OS_pipe_6_reset; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2819.4]
  wire  NV_NVDLA_BC_OS_pipe_6_io_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2819.4]
  wire  NV_NVDLA_BC_OS_pipe_6_io_vi; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2819.4]
  wire  NV_NVDLA_BC_OS_pipe_6_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2819.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_6_io_di; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2819.4]
  wire  NV_NVDLA_BC_OS_pipe_6_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2819.4]
  wire  NV_NVDLA_BC_OS_pipe_6_io_ri; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2819.4]
  wire [42:0] NV_NVDLA_BC_OS_pipe_6_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2819.4]
  wire  u_read_ig_arb_reset; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_req_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_req_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_req_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_req_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_req_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_req_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_req_6; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire [7:0] u_read_ig_arb_io_wt_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire [7:0] u_read_ig_arb_io_wt_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire [7:0] u_read_ig_arb_io_wt_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire [7:0] u_read_ig_arb_io_wt_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire [7:0] u_read_ig_arb_io_wt_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire [7:0] u_read_ig_arb_io_wt_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire [7:0] u_read_ig_arb_io_wt_6; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_busy; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_6; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_7; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_8; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  u_read_ig_arb_io_gnt_9; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
  wire  pipe_out_reset; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 92:26:@2945.4]
  wire  pipe_out_io_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 92:26:@2945.4]
  wire [42:0] pipe_out_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 92:26:@2945.4]
  wire  pipe_out_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 92:26:@2945.4]
  wire  pipe_out_io_ri; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 92:26:@2945.4]
  wire [42:0] pipe_out_io_di; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 92:26:@2945.4]
  wire  pipe_out_io_vi; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 92:26:@2945.4]
  wire  pipe_out_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 92:26:@2945.4]
  wire  src_gnt_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2880.4]
  wire [42:0] arb_src_pd_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2828.4]
  wire [42:0] _GEN_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2914.4]
  wire  src_gnt_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2883.4]
  wire [42:0] arb_src_pd_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2835.4]
  wire [42:0] _GEN_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2917.4]
  wire  src_gnt_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2886.4]
  wire [42:0] arb_src_pd_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2842.4]
  wire [42:0] _GEN_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2920.4]
  wire  src_gnt_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2889.4]
  wire [42:0] arb_src_pd_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2849.4]
  wire [42:0] _GEN_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2923.4]
  wire  src_gnt_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2892.4]
  wire [42:0] arb_src_pd_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2856.4]
  wire [42:0] _GEN_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2926.4]
  wire  src_gnt_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2895.4]
  wire [42:0] arb_src_pd_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2863.4]
  wire [42:0] _GEN_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2929.4]
  wire  src_gnt_6; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2898.4]
  wire [42:0] arb_src_pd_6; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2870.4]
  wire [6:0] _T_225; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 87:31:@2940.4]
  wire  arb_out_rdy; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 88:27:@2942.4 NV_NVDLA_MCIF_READ_IG_arb.scala 95:17:@2950.4]
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe ( // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2801.4]
    .reset(NV_NVDLA_BC_OS_pipe_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_io_dout)
  );
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe_1 ( // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2804.4]
    .reset(NV_NVDLA_BC_OS_pipe_1_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_1_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_1_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_1_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_1_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_1_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_1_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_1_io_dout)
  );
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe_2 ( // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2807.4]
    .reset(NV_NVDLA_BC_OS_pipe_2_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_2_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_2_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_2_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_2_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_2_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_2_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_2_io_dout)
  );
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe_3 ( // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2810.4]
    .reset(NV_NVDLA_BC_OS_pipe_3_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_3_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_3_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_3_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_3_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_3_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_3_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_3_io_dout)
  );
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe_4 ( // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2813.4]
    .reset(NV_NVDLA_BC_OS_pipe_4_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_4_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_4_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_4_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_4_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_4_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_4_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_4_io_dout)
  );
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe_5 ( // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2816.4]
    .reset(NV_NVDLA_BC_OS_pipe_5_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_5_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_5_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_5_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_5_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_5_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_5_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_5_io_dout)
  );
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe_6 ( // @[NV_NVDLA_MCIF_READ_IG_arb.scala 48:50:@2819.4]
    .reset(NV_NVDLA_BC_OS_pipe_6_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_6_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_6_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_6_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_6_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_6_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_6_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_6_io_dout)
  );
  NV_NVDLA_arb u_read_ig_arb ( // @[NV_NVDLA_MCIF_READ_IG_arb.scala 64:31:@2873.4]
    .reset(u_read_ig_arb_reset),
    .io_clk(u_read_ig_arb_io_clk),
    .io_req_0(u_read_ig_arb_io_req_0),
    .io_req_1(u_read_ig_arb_io_req_1),
    .io_req_2(u_read_ig_arb_io_req_2),
    .io_req_3(u_read_ig_arb_io_req_3),
    .io_req_4(u_read_ig_arb_io_req_4),
    .io_req_5(u_read_ig_arb_io_req_5),
    .io_req_6(u_read_ig_arb_io_req_6),
    .io_wt_0(u_read_ig_arb_io_wt_0),
    .io_wt_1(u_read_ig_arb_io_wt_1),
    .io_wt_2(u_read_ig_arb_io_wt_2),
    .io_wt_3(u_read_ig_arb_io_wt_3),
    .io_wt_4(u_read_ig_arb_io_wt_4),
    .io_wt_5(u_read_ig_arb_io_wt_5),
    .io_wt_6(u_read_ig_arb_io_wt_6),
    .io_gnt_busy(u_read_ig_arb_io_gnt_busy),
    .io_gnt_0(u_read_ig_arb_io_gnt_0),
    .io_gnt_1(u_read_ig_arb_io_gnt_1),
    .io_gnt_2(u_read_ig_arb_io_gnt_2),
    .io_gnt_3(u_read_ig_arb_io_gnt_3),
    .io_gnt_4(u_read_ig_arb_io_gnt_4),
    .io_gnt_5(u_read_ig_arb_io_gnt_5),
    .io_gnt_6(u_read_ig_arb_io_gnt_6),
    .io_gnt_7(u_read_ig_arb_io_gnt_7),
    .io_gnt_8(u_read_ig_arb_io_gnt_8),
    .io_gnt_9(u_read_ig_arb_io_gnt_9)
  );
  NV_NVDLA_IS_pipe_14 pipe_out ( // @[NV_NVDLA_MCIF_READ_IG_arb.scala 92:26:@2945.4]
    .reset(pipe_out_reset),
    .io_clk(pipe_out_io_clk),
    .io_dout(pipe_out_io_dout),
    .io_vo(pipe_out_io_vo),
    .io_ri(pipe_out_io_ri),
    .io_di(pipe_out_io_di),
    .io_vi(pipe_out_io_vi),
    .io_ro(pipe_out_io_ro)
  );
  assign src_gnt_0 = u_read_ig_arb_io_gnt_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2880.4]
  assign arb_src_pd_0 = NV_NVDLA_BC_OS_pipe_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2828.4]
  assign _GEN_0 = src_gnt_0 ? arb_src_pd_0 : 43'h0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2914.4]
  assign src_gnt_1 = u_read_ig_arb_io_gnt_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2883.4]
  assign arb_src_pd_1 = NV_NVDLA_BC_OS_pipe_1_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2835.4]
  assign _GEN_1 = src_gnt_1 ? arb_src_pd_1 : _GEN_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2917.4]
  assign src_gnt_2 = u_read_ig_arb_io_gnt_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2886.4]
  assign arb_src_pd_2 = NV_NVDLA_BC_OS_pipe_2_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2842.4]
  assign _GEN_2 = src_gnt_2 ? arb_src_pd_2 : _GEN_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2920.4]
  assign src_gnt_3 = u_read_ig_arb_io_gnt_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2889.4]
  assign arb_src_pd_3 = NV_NVDLA_BC_OS_pipe_3_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2849.4]
  assign _GEN_3 = src_gnt_3 ? arb_src_pd_3 : _GEN_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2923.4]
  assign src_gnt_4 = u_read_ig_arb_io_gnt_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2892.4]
  assign arb_src_pd_4 = NV_NVDLA_BC_OS_pipe_4_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2856.4]
  assign _GEN_4 = src_gnt_4 ? arb_src_pd_4 : _GEN_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2926.4]
  assign src_gnt_5 = u_read_ig_arb_io_gnt_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2895.4]
  assign arb_src_pd_5 = NV_NVDLA_BC_OS_pipe_5_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2863.4]
  assign _GEN_5 = src_gnt_5 ? arb_src_pd_5 : _GEN_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 82:25:@2929.4]
  assign src_gnt_6 = u_read_ig_arb_io_gnt_6; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 63:23:@2872.4 NV_NVDLA_MCIF_READ_IG_arb.scala 70:20:@2898.4]
  assign arb_src_pd_6 = NV_NVDLA_BC_OS_pipe_6_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 44:26:@2798.4 NV_NVDLA_MCIF_READ_IG_arb.scala 58:23:@2870.4]
  assign _T_225 = {src_gnt_6,src_gnt_5,src_gnt_4,src_gnt_3,src_gnt_2,src_gnt_1,src_gnt_0}; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 87:31:@2940.4]
  assign arb_out_rdy = pipe_out_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 88:27:@2942.4 NV_NVDLA_MCIF_READ_IG_arb.scala 95:17:@2950.4]
  assign io_bpt2arb_req_pd_0_ready = NV_NVDLA_BC_OS_pipe_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 53:36:@2824.4]
  assign io_bpt2arb_req_pd_1_ready = NV_NVDLA_BC_OS_pipe_1_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 53:36:@2831.4]
  assign io_bpt2arb_req_pd_2_ready = NV_NVDLA_BC_OS_pipe_2_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 53:36:@2838.4]
  assign io_bpt2arb_req_pd_3_ready = NV_NVDLA_BC_OS_pipe_3_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 53:36:@2845.4]
  assign io_bpt2arb_req_pd_4_ready = NV_NVDLA_BC_OS_pipe_4_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 53:36:@2852.4]
  assign io_bpt2arb_req_pd_5_ready = NV_NVDLA_BC_OS_pipe_5_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 53:36:@2859.4]
  assign io_bpt2arb_req_pd_6_ready = NV_NVDLA_BC_OS_pipe_6_io_ro; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 53:36:@2866.4]
  assign io_arb2spt_req_pd_valid = pipe_out_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 97:29:@2952.4]
  assign io_arb2spt_req_pd_bits = pipe_out_io_dout; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 99:28:@2954.4]
  assign NV_NVDLA_BC_OS_pipe_reset = reset; // @[:@2803.4]
  assign NV_NVDLA_BC_OS_pipe_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 50:26:@2822.4]
  assign NV_NVDLA_BC_OS_pipe_io_vi = io_bpt2arb_req_pd_0_valid; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 52:25:@2823.4]
  assign NV_NVDLA_BC_OS_pipe_io_di = io_bpt2arb_req_pd_0_bits; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 54:25:@2825.4]
  assign NV_NVDLA_BC_OS_pipe_io_ri = u_read_ig_arb_io_gnt_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 57:25:@2827.4]
  assign NV_NVDLA_BC_OS_pipe_1_reset = reset; // @[:@2806.4]
  assign NV_NVDLA_BC_OS_pipe_1_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 50:26:@2829.4]
  assign NV_NVDLA_BC_OS_pipe_1_io_vi = io_bpt2arb_req_pd_1_valid; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 52:25:@2830.4]
  assign NV_NVDLA_BC_OS_pipe_1_io_di = io_bpt2arb_req_pd_1_bits; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 54:25:@2832.4]
  assign NV_NVDLA_BC_OS_pipe_1_io_ri = u_read_ig_arb_io_gnt_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 57:25:@2834.4]
  assign NV_NVDLA_BC_OS_pipe_2_reset = reset; // @[:@2809.4]
  assign NV_NVDLA_BC_OS_pipe_2_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 50:26:@2836.4]
  assign NV_NVDLA_BC_OS_pipe_2_io_vi = io_bpt2arb_req_pd_2_valid; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 52:25:@2837.4]
  assign NV_NVDLA_BC_OS_pipe_2_io_di = io_bpt2arb_req_pd_2_bits; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 54:25:@2839.4]
  assign NV_NVDLA_BC_OS_pipe_2_io_ri = u_read_ig_arb_io_gnt_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 57:25:@2841.4]
  assign NV_NVDLA_BC_OS_pipe_3_reset = reset; // @[:@2812.4]
  assign NV_NVDLA_BC_OS_pipe_3_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 50:26:@2843.4]
  assign NV_NVDLA_BC_OS_pipe_3_io_vi = io_bpt2arb_req_pd_3_valid; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 52:25:@2844.4]
  assign NV_NVDLA_BC_OS_pipe_3_io_di = io_bpt2arb_req_pd_3_bits; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 54:25:@2846.4]
  assign NV_NVDLA_BC_OS_pipe_3_io_ri = u_read_ig_arb_io_gnt_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 57:25:@2848.4]
  assign NV_NVDLA_BC_OS_pipe_4_reset = reset; // @[:@2815.4]
  assign NV_NVDLA_BC_OS_pipe_4_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 50:26:@2850.4]
  assign NV_NVDLA_BC_OS_pipe_4_io_vi = io_bpt2arb_req_pd_4_valid; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 52:25:@2851.4]
  assign NV_NVDLA_BC_OS_pipe_4_io_di = io_bpt2arb_req_pd_4_bits; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 54:25:@2853.4]
  assign NV_NVDLA_BC_OS_pipe_4_io_ri = u_read_ig_arb_io_gnt_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 57:25:@2855.4]
  assign NV_NVDLA_BC_OS_pipe_5_reset = reset; // @[:@2818.4]
  assign NV_NVDLA_BC_OS_pipe_5_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 50:26:@2857.4]
  assign NV_NVDLA_BC_OS_pipe_5_io_vi = io_bpt2arb_req_pd_5_valid; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 52:25:@2858.4]
  assign NV_NVDLA_BC_OS_pipe_5_io_di = io_bpt2arb_req_pd_5_bits; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 54:25:@2860.4]
  assign NV_NVDLA_BC_OS_pipe_5_io_ri = u_read_ig_arb_io_gnt_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 57:25:@2862.4]
  assign NV_NVDLA_BC_OS_pipe_6_reset = reset; // @[:@2821.4]
  assign NV_NVDLA_BC_OS_pipe_6_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 50:26:@2864.4]
  assign NV_NVDLA_BC_OS_pipe_6_io_vi = io_bpt2arb_req_pd_6_valid; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 52:25:@2865.4]
  assign NV_NVDLA_BC_OS_pipe_6_io_di = io_bpt2arb_req_pd_6_bits; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 54:25:@2867.4]
  assign NV_NVDLA_BC_OS_pipe_6_io_ri = u_read_ig_arb_io_gnt_6; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 57:25:@2869.4]
  assign u_read_ig_arb_reset = reset; // @[:@2875.4]
  assign u_read_ig_arb_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 65:26:@2876.4]
  assign u_read_ig_arb_io_req_0 = NV_NVDLA_BC_OS_pipe_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 68:33:@2878.4]
  assign u_read_ig_arb_io_req_1 = NV_NVDLA_BC_OS_pipe_1_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 68:33:@2881.4]
  assign u_read_ig_arb_io_req_2 = NV_NVDLA_BC_OS_pipe_2_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 68:33:@2884.4]
  assign u_read_ig_arb_io_req_3 = NV_NVDLA_BC_OS_pipe_3_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 68:33:@2887.4]
  assign u_read_ig_arb_io_req_4 = NV_NVDLA_BC_OS_pipe_4_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 68:33:@2890.4]
  assign u_read_ig_arb_io_req_5 = NV_NVDLA_BC_OS_pipe_5_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 68:33:@2893.4]
  assign u_read_ig_arb_io_req_6 = NV_NVDLA_BC_OS_pipe_6_io_vo; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 68:33:@2896.4]
  assign u_read_ig_arb_io_wt_0 = io_reg2dp_rd_weight_0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 69:32:@2879.4]
  assign u_read_ig_arb_io_wt_1 = io_reg2dp_rd_weight_1; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 69:32:@2882.4]
  assign u_read_ig_arb_io_wt_2 = io_reg2dp_rd_weight_2; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 69:32:@2885.4]
  assign u_read_ig_arb_io_wt_3 = io_reg2dp_rd_weight_3; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 69:32:@2888.4]
  assign u_read_ig_arb_io_wt_4 = io_reg2dp_rd_weight_4; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 69:32:@2891.4]
  assign u_read_ig_arb_io_wt_5 = io_reg2dp_rd_weight_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 69:32:@2894.4]
  assign u_read_ig_arb_io_wt_6 = io_reg2dp_rd_weight_6; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 69:32:@2897.4]
  assign u_read_ig_arb_io_gnt_busy = ~ arb_out_rdy; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 66:35:@2877.4]
  assign pipe_out_reset = reset; // @[:@2947.4]
  assign pipe_out_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 93:21:@2948.4]
  assign pipe_out_io_ri = io_arb2spt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 98:20:@2953.4]
  assign pipe_out_io_di = src_gnt_6 ? arb_src_pd_6 : _GEN_5; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 96:20:@2951.4]
  assign pipe_out_io_vi = _T_225 != 7'h0; // @[NV_NVDLA_MCIF_READ_IG_arb.scala 94:20:@2949.4]
endmodule
module NV_COUNTER_STAGE_os( // @[:@2956.2]
  input        reset, // @[:@2958.4]
  input        io_clk, // @[:@2959.4]
  input  [2:0] io_os_cnt_add, // @[:@2959.4]
  input  [2:0] io_os_cnt_sub, // @[:@2959.4]
  input        io_os_cnt_cen, // @[:@2959.4]
  output [8:0] io_os_cnt_cur // @[:@2959.4]
);
  wire  _T_15; // @[Perf_Counter.scala 244:32:@2961.4]
  reg [8:0] _T_18; // @[Perf_Counter.scala 247:33:@2962.4]
  reg [31:0] _RAND_0;
  wire [8:0] _GEN_1; // @[Perf_Counter.scala 254:34:@2968.4]
  wire [9:0] _T_27; // @[Perf_Counter.scala 254:34:@2968.4]
  wire [9:0] _GEN_2; // @[Perf_Counter.scala 254:51:@2969.4]
  wire [10:0] _T_28; // @[Perf_Counter.scala 254:51:@2969.4]
  wire [10:0] _T_29; // @[Perf_Counter.scala 254:51:@2970.4]
  wire [10:0] _T_20; // @[Perf_Counter.scala 248:26:@2963.4 Perf_Counter.scala 253:16:@2967.4]
  wire [10:0] _T_30; // @[Perf_Counter.scala 255:22:@2972.4]
  wire [10:0] _GEN_0; // @[Perf_Counter.scala 259:24:@2975.4]
  assign _T_15 = io_os_cnt_add != io_os_cnt_sub; // @[Perf_Counter.scala 244:32:@2961.4]
  assign _GEN_1 = {{6'd0}, io_os_cnt_add}; // @[Perf_Counter.scala 254:34:@2968.4]
  assign _T_27 = _T_18 + _GEN_1; // @[Perf_Counter.scala 254:34:@2968.4]
  assign _GEN_2 = {{7'd0}, io_os_cnt_sub}; // @[Perf_Counter.scala 254:51:@2969.4]
  assign _T_28 = _T_27 - _GEN_2; // @[Perf_Counter.scala 254:51:@2969.4]
  assign _T_29 = $unsigned(_T_28); // @[Perf_Counter.scala 254:51:@2970.4]
  assign _T_20 = {{2'd0}, _T_18}; // @[Perf_Counter.scala 248:26:@2963.4 Perf_Counter.scala 253:16:@2967.4]
  assign _T_30 = _T_15 ? _T_29 : _T_20; // @[Perf_Counter.scala 255:22:@2972.4]
  assign _GEN_0 = io_os_cnt_cen ? _T_30 : {{2'd0}, _T_18}; // @[Perf_Counter.scala 259:24:@2975.4]
  assign io_os_cnt_cur = _T_18; // @[Perf_Counter.scala 263:19:@2978.4]
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
module NV_NVDLA_IS_pipe_15( // @[:@2980.2]
  input         reset, // @[:@2982.4]
  input         io_clk, // @[:@2983.4]
  output [37:0] io_dout, // @[:@2983.4]
  output        io_vo, // @[:@2983.4]
  input         io_ri, // @[:@2983.4]
  input  [37:0] io_di, // @[:@2983.4]
  input         io_vi, // @[:@2983.4]
  output        io_ro // @[:@2983.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@2985.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@2986.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@2987.4]
  reg [31:0] _RAND_2;
  reg [37:0] skid_flop_di; // @[IS_pipe.scala 57:53:@2988.4]
  reg [63:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@2989.4]
  reg [31:0] _RAND_4;
  reg [37:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@2990.4]
  reg [63:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@2999.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@3004.4]
  wire [37:0] _GEN_1; // @[IS_pipe.scala 76:29:@3005.4]
  wire [37:0] skid_di; // @[IS_pipe.scala 79:19:@3008.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@3010.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@3011.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@3013.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@3016.4]
  wire [37:0] _GEN_3; // @[IS_pipe.scala 87:29:@3017.4]
  assign _GEN_0 = skid_flop_ro ? io_vi : skid_flop_vi; // @[IS_pipe.scala 71:23:@2999.4]
  assign _T_38 = skid_flop_ro & io_vi; // @[IS_pipe.scala 76:22:@3004.4]
  assign _GEN_1 = _T_38 ? io_di : skid_flop_di; // @[IS_pipe.scala 76:29:@3005.4]
  assign skid_di = skid_flop_ro ? io_di : skid_flop_di; // @[IS_pipe.scala 79:19:@3008.4]
  assign _T_40 = ~ pipe_skid_vi; // @[IS_pipe.scala 81:32:@3010.4]
  assign skid_ro = io_ri | _T_40; // @[IS_pipe.scala 81:29:@3011.4]
  assign _GEN_2 = skid_ro ? _GEN_0 : pipe_skid_vi; // @[IS_pipe.scala 83:18:@3013.4]
  assign _T_42 = skid_ro & _GEN_0; // @[IS_pipe.scala 87:18:@3016.4]
  assign _GEN_3 = _T_42 ? skid_di : pipe_skid_di; // @[IS_pipe.scala 87:29:@3017.4]
  assign io_dout = pipe_skid_di; // @[IS_pipe.scala 98:13:@3025.4]
  assign io_vo = pipe_skid_vi; // @[IS_pipe.scala 97:11:@3024.4]
  assign io_ro = ro_out; // @[IS_pipe.scala 96:11:@3023.4]
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
module NV_NVDLA_XXIF_READ_IG_cvt( // @[:@3027.2]
  input         reset, // @[:@3029.4]
  input         io_nvdla_core_clk, // @[:@3030.4]
  input  [7:0]  io_reg2dp_rd_os_cnt, // @[:@3030.4]
  input         io_eg2ig_axi_vld, // @[:@3030.4]
  output        io_spt2cvt_req_pd_ready, // @[:@3030.4]
  input         io_spt2cvt_req_pd_valid, // @[:@3030.4]
  input  [42:0] io_spt2cvt_req_pd_bits, // @[:@3030.4]
  input         io_mcif2noc_axi_ar_ready, // @[:@3030.4]
  output        io_mcif2noc_axi_ar_valid, // @[:@3030.4]
  output [7:0]  io_mcif2noc_axi_ar_bits_id, // @[:@3030.4]
  output [3:0]  io_mcif2noc_axi_ar_bits_len, // @[:@3030.4]
  output [63:0] io_mcif2noc_axi_ar_bits_addr // @[:@3030.4]
);
  wire  perf_os_reset; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@3072.4]
  wire  perf_os_io_clk; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@3072.4]
  wire [2:0] perf_os_io_os_cnt_add; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@3072.4]
  wire [2:0] perf_os_io_os_cnt_sub; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@3072.4]
  wire  perf_os_io_os_cnt_cen; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@3072.4]
  wire [8:0] perf_os_io_os_cnt_cur; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@3072.4]
  wire  pipe_p1_reset; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@3082.4]
  wire  pipe_p1_io_clk; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@3082.4]
  wire [37:0] pipe_p1_io_dout; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@3082.4]
  wire  pipe_p1_io_vo; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@3082.4]
  wire  pipe_p1_io_ri; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@3082.4]
  wire [37:0] pipe_p1_io_di; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@3082.4]
  wire  pipe_p1_io_vi; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@3082.4]
  wire  pipe_p1_io_ro; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@3082.4]
  wire [3:0] cmd_axid; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 36:42:@3034.4]
  wire [31:0] cmd_addr; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 37:42:@3035.4]
  wire [2:0] cmd_size; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 38:42:@3036.4]
  wire [8:0] os_cnt; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 72:22:@3054.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 94:12:@3079.4]
  wire [1:0] axi_len; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 46:23:@3043.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 66:17:@3051.4]
  wire [2:0] _T_54; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 73:47:@3055.4]
  wire [2:0] os_inp_add_nxt; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 73:29:@3056.4]
  wire [8:0] _GEN_0; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:29:@3060.4]
  wire [9:0] _T_60; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:29:@3060.4]
  reg  eg2ig_axi_vld_d; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 74:34:@3057.4]
  reg [31:0] _RAND_0;
  wire [9:0] _GEN_1; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@3061.4]
  wire [10:0] _T_61; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@3061.4]
  wire [10:0] os_inp_nxt; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@3062.4]
  wire [8:0] rd_os_cnt_ext; // @[Cat.scala 30:58:@3068.4]
  wire [9:0] _T_69; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:48:@3069.4]
  wire [10:0] _GEN_2; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:31:@3070.4]
  wire  os_cnt_full; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:31:@3070.4]
  wire  _T_46; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 64:34:@3045.4]
  wire  axi_cmd_vld; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 64:32:@3046.4]
  wire  axi_cmd_rdy; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 45:27:@3042.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 101:17:@3087.4]
  wire [28:0] _T_50; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 70:32:@3052.4]
  wire  os_cnt_add_en; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 80:37:@3063.4]
  wire [35:0] _T_71; // @[Cat.scala 30:58:@3080.4]
  wire [3:0] opipe_axi_axid; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 107:41:@3091.4]
  wire [31:0] opipe_axi_addr; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 108:41:@3092.4]
  wire [1:0] opipe_axi_len; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 109:41:@3093.4]
  NV_COUNTER_STAGE_os perf_os ( // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@3072.4]
    .reset(perf_os_reset),
    .io_clk(perf_os_io_clk),
    .io_os_cnt_add(perf_os_io_os_cnt_add),
    .io_os_cnt_sub(perf_os_io_os_cnt_sub),
    .io_os_cnt_cen(perf_os_io_os_cnt_cen),
    .io_os_cnt_cur(perf_os_io_os_cnt_cur)
  );
  NV_NVDLA_IS_pipe_15 pipe_p1 ( // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@3082.4]
    .reset(pipe_p1_reset),
    .io_clk(pipe_p1_io_clk),
    .io_dout(pipe_p1_io_dout),
    .io_vo(pipe_p1_io_vo),
    .io_ri(pipe_p1_io_ri),
    .io_di(pipe_p1_io_di),
    .io_vi(pipe_p1_io_vi),
    .io_ro(pipe_p1_io_ro)
  );
  assign cmd_axid = io_spt2cvt_req_pd_bits[3:0]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 36:42:@3034.4]
  assign cmd_addr = io_spt2cvt_req_pd_bits[35:4]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 37:42:@3035.4]
  assign cmd_size = io_spt2cvt_req_pd_bits[38:36]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 38:42:@3036.4]
  assign os_cnt = perf_os_io_os_cnt_cur; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 72:22:@3054.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 94:12:@3079.4]
  assign axi_len = cmd_size[1:0]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 46:23:@3043.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 66:17:@3051.4]
  assign _T_54 = axi_len + 2'h1; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 73:47:@3055.4]
  assign os_inp_add_nxt = io_spt2cvt_req_pd_valid ? _T_54 : 3'h0; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 73:29:@3056.4]
  assign _GEN_0 = {{6'd0}, os_inp_add_nxt}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:29:@3060.4]
  assign _T_60 = os_cnt + _GEN_0; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:29:@3060.4]
  assign _GEN_1 = {{9'd0}, eg2ig_axi_vld_d}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@3061.4]
  assign _T_61 = _T_60 - _GEN_1; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@3061.4]
  assign os_inp_nxt = $unsigned(_T_61); // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@3062.4]
  assign rd_os_cnt_ext = {1'h0,io_reg2dp_rd_os_cnt}; // @[Cat.scala 30:58:@3068.4]
  assign _T_69 = rd_os_cnt_ext + 9'h1; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:48:@3069.4]
  assign _GEN_2 = {{1'd0}, _T_69}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:31:@3070.4]
  assign os_cnt_full = os_inp_nxt > _GEN_2; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:31:@3070.4]
  assign _T_46 = ~ os_cnt_full; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 64:34:@3045.4]
  assign axi_cmd_vld = io_spt2cvt_req_pd_valid & _T_46; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 64:32:@3046.4]
  assign axi_cmd_rdy = pipe_p1_io_ro; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 45:27:@3042.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 101:17:@3087.4]
  assign _T_50 = cmd_addr[31:3]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 70:32:@3052.4]
  assign os_cnt_add_en = axi_cmd_vld & axi_cmd_rdy; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 80:37:@3063.4]
  assign _T_71 = {cmd_axid,_T_50,3'h0}; // @[Cat.scala 30:58:@3080.4]
  assign opipe_axi_axid = pipe_p1_io_dout[37:34]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 107:41:@3091.4]
  assign opipe_axi_addr = pipe_p1_io_dout[33:2]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 108:41:@3092.4]
  assign opipe_axi_len = pipe_p1_io_dout[1:0]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 109:41:@3093.4]
  assign io_spt2cvt_req_pd_ready = axi_cmd_rdy & _T_46; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 34:29:@3033.4]
  assign io_mcif2noc_axi_ar_valid = pipe_p1_io_vo; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 104:30:@3089.4]
  assign io_mcif2noc_axi_ar_bits_id = {4'h0,opipe_axi_axid}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 111:35:@3095.4]
  assign io_mcif2noc_axi_ar_bits_len = {2'h0,opipe_axi_len}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 113:35:@3099.4]
  assign io_mcif2noc_axi_ar_bits_addr = {32'h0,opipe_axi_addr}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 112:35:@3097.4]
  assign perf_os_reset = reset; // @[:@3074.4]
  assign perf_os_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 90:20:@3075.4]
  assign perf_os_io_os_cnt_add = os_cnt_add_en ? _T_54 : 3'h0; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 91:27:@3076.4]
  assign perf_os_io_os_cnt_sub = {{2'd0}, eg2ig_axi_vld_d}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 92:27:@3077.4]
  assign perf_os_io_os_cnt_cen = os_cnt_add_en | eg2ig_axi_vld_d; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 93:27:@3078.4]
  assign pipe_p1_reset = reset; // @[:@3084.4]
  assign pipe_p1_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 99:20:@3085.4]
  assign pipe_p1_io_ri = io_mcif2noc_axi_ar_ready; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 105:19:@3090.4]
  assign pipe_p1_io_di = {_T_71,axi_len}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 102:19:@3088.4]
  assign pipe_p1_io_vi = io_spt2cvt_req_pd_valid & _T_46; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 100:19:@3086.4]
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
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      eg2ig_axi_vld_d <= 1'h0;
    end else begin
      eg2ig_axi_vld_d <= io_eg2ig_axi_vld;
    end
  end
endmodule
module NV_NVDLA_MCIF_READ_ig( // @[:@3101.2]
  input         clock, // @[:@3102.4]
  input         reset, // @[:@3103.4]
  input         io_nvdla_core_clk, // @[:@3104.4]
  input  [31:0] io_pwrbus_ram_pd, // @[:@3104.4]
  input         io_client2mcif_rd_cdt_lat_fifo_pop_0, // @[:@3104.4]
  input         io_client2mcif_rd_cdt_lat_fifo_pop_1, // @[:@3104.4]
  input         io_client2mcif_rd_cdt_lat_fifo_pop_2, // @[:@3104.4]
  input         io_client2mcif_rd_cdt_lat_fifo_pop_3, // @[:@3104.4]
  input         io_client2mcif_rd_cdt_lat_fifo_pop_4, // @[:@3104.4]
  input         io_client2mcif_rd_cdt_lat_fifo_pop_5, // @[:@3104.4]
  input         io_client2mcif_rd_cdt_lat_fifo_pop_6, // @[:@3104.4]
  output        io_client2mcif_rd_req_pd_0_ready, // @[:@3104.4]
  input         io_client2mcif_rd_req_pd_0_valid, // @[:@3104.4]
  input  [46:0] io_client2mcif_rd_req_pd_0_bits, // @[:@3104.4]
  output        io_client2mcif_rd_req_pd_1_ready, // @[:@3104.4]
  input         io_client2mcif_rd_req_pd_1_valid, // @[:@3104.4]
  input  [46:0] io_client2mcif_rd_req_pd_1_bits, // @[:@3104.4]
  output        io_client2mcif_rd_req_pd_2_ready, // @[:@3104.4]
  input         io_client2mcif_rd_req_pd_2_valid, // @[:@3104.4]
  input  [46:0] io_client2mcif_rd_req_pd_2_bits, // @[:@3104.4]
  output        io_client2mcif_rd_req_pd_3_ready, // @[:@3104.4]
  input         io_client2mcif_rd_req_pd_3_valid, // @[:@3104.4]
  input  [46:0] io_client2mcif_rd_req_pd_3_bits, // @[:@3104.4]
  output        io_client2mcif_rd_req_pd_4_ready, // @[:@3104.4]
  input         io_client2mcif_rd_req_pd_4_valid, // @[:@3104.4]
  input  [46:0] io_client2mcif_rd_req_pd_4_bits, // @[:@3104.4]
  output        io_client2mcif_rd_req_pd_5_ready, // @[:@3104.4]
  input         io_client2mcif_rd_req_pd_5_valid, // @[:@3104.4]
  input  [46:0] io_client2mcif_rd_req_pd_5_bits, // @[:@3104.4]
  output        io_client2mcif_rd_req_pd_6_ready, // @[:@3104.4]
  input         io_client2mcif_rd_req_pd_6_valid, // @[:@3104.4]
  input  [46:0] io_client2mcif_rd_req_pd_6_bits, // @[:@3104.4]
  input         io_eg2ig_axi_vld, // @[:@3104.4]
  input         io_mcif2noc_axi_ar_ready, // @[:@3104.4]
  output        io_mcif2noc_axi_ar_valid, // @[:@3104.4]
  output [7:0]  io_mcif2noc_axi_ar_bits_id, // @[:@3104.4]
  output [3:0]  io_mcif2noc_axi_ar_bits_len, // @[:@3104.4]
  output [63:0] io_mcif2noc_axi_ar_bits_addr, // @[:@3104.4]
  input  [7:0]  io_reg2dp_rd_weight_0, // @[:@3104.4]
  input  [7:0]  io_reg2dp_rd_weight_1, // @[:@3104.4]
  input  [7:0]  io_reg2dp_rd_weight_2, // @[:@3104.4]
  input  [7:0]  io_reg2dp_rd_weight_3, // @[:@3104.4]
  input  [7:0]  io_reg2dp_rd_weight_4, // @[:@3104.4]
  input  [7:0]  io_reg2dp_rd_weight_5, // @[:@3104.4]
  input  [7:0]  io_reg2dp_rd_weight_6, // @[:@3104.4]
  input  [7:0]  io_reg2dp_rd_os_cnt // @[:@3104.4]
);
  wire  NV_NVDLA_MCIF_READ_IG_bpt_reset; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire [46:0] NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_cdt_lat_fifo_pop; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_io_bpt2arb_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire [42:0] NV_NVDLA_MCIF_READ_IG_bpt_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire [3:0] NV_NVDLA_MCIF_READ_IG_bpt_io_tieoff_axid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire [8:0] NV_NVDLA_MCIF_READ_IG_bpt_io_tieoff_lat_fifo_depth; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_1_reset; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_1_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire [46:0] NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_cdt_lat_fifo_pop; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_1_io_bpt2arb_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_1_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire [42:0] NV_NVDLA_MCIF_READ_IG_bpt_1_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire [3:0] NV_NVDLA_MCIF_READ_IG_bpt_1_io_tieoff_axid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire [8:0] NV_NVDLA_MCIF_READ_IG_bpt_1_io_tieoff_lat_fifo_depth; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_2_reset; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_2_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire [46:0] NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_cdt_lat_fifo_pop; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_2_io_bpt2arb_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_2_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire [42:0] NV_NVDLA_MCIF_READ_IG_bpt_2_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire [3:0] NV_NVDLA_MCIF_READ_IG_bpt_2_io_tieoff_axid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire [8:0] NV_NVDLA_MCIF_READ_IG_bpt_2_io_tieoff_lat_fifo_depth; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_3_reset; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_3_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire [46:0] NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_cdt_lat_fifo_pop; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_3_io_bpt2arb_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_3_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire [42:0] NV_NVDLA_MCIF_READ_IG_bpt_3_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire [3:0] NV_NVDLA_MCIF_READ_IG_bpt_3_io_tieoff_axid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire [8:0] NV_NVDLA_MCIF_READ_IG_bpt_3_io_tieoff_lat_fifo_depth; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_4_reset; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_4_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire [46:0] NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_cdt_lat_fifo_pop; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_4_io_bpt2arb_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_4_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire [42:0] NV_NVDLA_MCIF_READ_IG_bpt_4_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire [3:0] NV_NVDLA_MCIF_READ_IG_bpt_4_io_tieoff_axid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire [8:0] NV_NVDLA_MCIF_READ_IG_bpt_4_io_tieoff_lat_fifo_depth; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_5_reset; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_5_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire [46:0] NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_cdt_lat_fifo_pop; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_5_io_bpt2arb_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_5_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire [42:0] NV_NVDLA_MCIF_READ_IG_bpt_5_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire [3:0] NV_NVDLA_MCIF_READ_IG_bpt_5_io_tieoff_axid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire [8:0] NV_NVDLA_MCIF_READ_IG_bpt_5_io_tieoff_lat_fifo_depth; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_6_reset; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_6_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire [46:0] NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_cdt_lat_fifo_pop; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_6_io_bpt2arb_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire  NV_NVDLA_MCIF_READ_IG_bpt_6_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire [42:0] NV_NVDLA_MCIF_READ_IG_bpt_6_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire [3:0] NV_NVDLA_MCIF_READ_IG_bpt_6_io_tieoff_axid; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire [8:0] NV_NVDLA_MCIF_READ_IG_bpt_6_io_tieoff_lat_fifo_depth; // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
  wire  u_arb_reset; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_0_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_0_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [42:0] u_arb_io_bpt2arb_req_pd_0_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_1_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_1_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [42:0] u_arb_io_bpt2arb_req_pd_1_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_2_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_2_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [42:0] u_arb_io_bpt2arb_req_pd_2_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_3_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_3_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [42:0] u_arb_io_bpt2arb_req_pd_3_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_4_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_4_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [42:0] u_arb_io_bpt2arb_req_pd_4_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_5_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_5_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [42:0] u_arb_io_bpt2arb_req_pd_5_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_6_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_bpt2arb_req_pd_6_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [42:0] u_arb_io_bpt2arb_req_pd_6_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_arb2spt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_arb_io_arb2spt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [42:0] u_arb_io_arb2spt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [7:0] u_arb_io_reg2dp_rd_weight_0; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [7:0] u_arb_io_reg2dp_rd_weight_1; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [7:0] u_arb_io_reg2dp_rd_weight_2; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [7:0] u_arb_io_reg2dp_rd_weight_3; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [7:0] u_arb_io_reg2dp_rd_weight_4; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [7:0] u_arb_io_reg2dp_rd_weight_5; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire [7:0] u_arb_io_reg2dp_rd_weight_6; // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
  wire  u_cvt_reset; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire  u_cvt_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire [7:0] u_cvt_io_reg2dp_rd_os_cnt; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire  u_cvt_io_eg2ig_axi_vld; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire  u_cvt_io_spt2cvt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire  u_cvt_io_spt2cvt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire [42:0] u_cvt_io_spt2cvt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire  u_cvt_io_mcif2noc_axi_ar_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire  u_cvt_io_mcif2noc_axi_ar_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire [7:0] u_cvt_io_mcif2noc_axi_ar_bits_id; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire [3:0] u_cvt_io_mcif2noc_axi_ar_bits_len; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  wire [63:0] u_cvt_io_mcif2noc_axi_ar_bits_addr; // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
  NV_NVDLA_MCIF_READ_IG_bpt NV_NVDLA_MCIF_READ_IG_bpt ( // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3106.4]
    .reset(NV_NVDLA_MCIF_READ_IG_bpt_reset),
    .io_nvdla_core_clk(NV_NVDLA_MCIF_READ_IG_bpt_io_nvdla_core_clk),
    .io_dma2bpt_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_req_pd_ready),
    .io_dma2bpt_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_req_pd_valid),
    .io_dma2bpt_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_req_pd_bits),
    .io_dma2bpt_cdt_lat_fifo_pop(NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_cdt_lat_fifo_pop),
    .io_bpt2arb_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_io_bpt2arb_req_pd_ready),
    .io_bpt2arb_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_io_bpt2arb_req_pd_valid),
    .io_bpt2arb_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_io_bpt2arb_req_pd_bits),
    .io_tieoff_axid(NV_NVDLA_MCIF_READ_IG_bpt_io_tieoff_axid),
    .io_tieoff_lat_fifo_depth(NV_NVDLA_MCIF_READ_IG_bpt_io_tieoff_lat_fifo_depth)
  );
  NV_NVDLA_MCIF_READ_IG_bpt NV_NVDLA_MCIF_READ_IG_bpt_1 ( // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3109.4]
    .reset(NV_NVDLA_MCIF_READ_IG_bpt_1_reset),
    .io_nvdla_core_clk(NV_NVDLA_MCIF_READ_IG_bpt_1_io_nvdla_core_clk),
    .io_dma2bpt_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_req_pd_ready),
    .io_dma2bpt_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_req_pd_valid),
    .io_dma2bpt_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_req_pd_bits),
    .io_dma2bpt_cdt_lat_fifo_pop(NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_cdt_lat_fifo_pop),
    .io_bpt2arb_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_1_io_bpt2arb_req_pd_ready),
    .io_bpt2arb_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_1_io_bpt2arb_req_pd_valid),
    .io_bpt2arb_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_1_io_bpt2arb_req_pd_bits),
    .io_tieoff_axid(NV_NVDLA_MCIF_READ_IG_bpt_1_io_tieoff_axid),
    .io_tieoff_lat_fifo_depth(NV_NVDLA_MCIF_READ_IG_bpt_1_io_tieoff_lat_fifo_depth)
  );
  NV_NVDLA_MCIF_READ_IG_bpt NV_NVDLA_MCIF_READ_IG_bpt_2 ( // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3112.4]
    .reset(NV_NVDLA_MCIF_READ_IG_bpt_2_reset),
    .io_nvdla_core_clk(NV_NVDLA_MCIF_READ_IG_bpt_2_io_nvdla_core_clk),
    .io_dma2bpt_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_req_pd_ready),
    .io_dma2bpt_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_req_pd_valid),
    .io_dma2bpt_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_req_pd_bits),
    .io_dma2bpt_cdt_lat_fifo_pop(NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_cdt_lat_fifo_pop),
    .io_bpt2arb_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_2_io_bpt2arb_req_pd_ready),
    .io_bpt2arb_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_2_io_bpt2arb_req_pd_valid),
    .io_bpt2arb_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_2_io_bpt2arb_req_pd_bits),
    .io_tieoff_axid(NV_NVDLA_MCIF_READ_IG_bpt_2_io_tieoff_axid),
    .io_tieoff_lat_fifo_depth(NV_NVDLA_MCIF_READ_IG_bpt_2_io_tieoff_lat_fifo_depth)
  );
  NV_NVDLA_MCIF_READ_IG_bpt NV_NVDLA_MCIF_READ_IG_bpt_3 ( // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3115.4]
    .reset(NV_NVDLA_MCIF_READ_IG_bpt_3_reset),
    .io_nvdla_core_clk(NV_NVDLA_MCIF_READ_IG_bpt_3_io_nvdla_core_clk),
    .io_dma2bpt_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_req_pd_ready),
    .io_dma2bpt_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_req_pd_valid),
    .io_dma2bpt_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_req_pd_bits),
    .io_dma2bpt_cdt_lat_fifo_pop(NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_cdt_lat_fifo_pop),
    .io_bpt2arb_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_3_io_bpt2arb_req_pd_ready),
    .io_bpt2arb_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_3_io_bpt2arb_req_pd_valid),
    .io_bpt2arb_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_3_io_bpt2arb_req_pd_bits),
    .io_tieoff_axid(NV_NVDLA_MCIF_READ_IG_bpt_3_io_tieoff_axid),
    .io_tieoff_lat_fifo_depth(NV_NVDLA_MCIF_READ_IG_bpt_3_io_tieoff_lat_fifo_depth)
  );
  NV_NVDLA_MCIF_READ_IG_bpt NV_NVDLA_MCIF_READ_IG_bpt_4 ( // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3118.4]
    .reset(NV_NVDLA_MCIF_READ_IG_bpt_4_reset),
    .io_nvdla_core_clk(NV_NVDLA_MCIF_READ_IG_bpt_4_io_nvdla_core_clk),
    .io_dma2bpt_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_req_pd_ready),
    .io_dma2bpt_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_req_pd_valid),
    .io_dma2bpt_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_req_pd_bits),
    .io_dma2bpt_cdt_lat_fifo_pop(NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_cdt_lat_fifo_pop),
    .io_bpt2arb_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_4_io_bpt2arb_req_pd_ready),
    .io_bpt2arb_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_4_io_bpt2arb_req_pd_valid),
    .io_bpt2arb_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_4_io_bpt2arb_req_pd_bits),
    .io_tieoff_axid(NV_NVDLA_MCIF_READ_IG_bpt_4_io_tieoff_axid),
    .io_tieoff_lat_fifo_depth(NV_NVDLA_MCIF_READ_IG_bpt_4_io_tieoff_lat_fifo_depth)
  );
  NV_NVDLA_MCIF_READ_IG_bpt NV_NVDLA_MCIF_READ_IG_bpt_5 ( // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3121.4]
    .reset(NV_NVDLA_MCIF_READ_IG_bpt_5_reset),
    .io_nvdla_core_clk(NV_NVDLA_MCIF_READ_IG_bpt_5_io_nvdla_core_clk),
    .io_dma2bpt_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_req_pd_ready),
    .io_dma2bpt_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_req_pd_valid),
    .io_dma2bpt_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_req_pd_bits),
    .io_dma2bpt_cdt_lat_fifo_pop(NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_cdt_lat_fifo_pop),
    .io_bpt2arb_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_5_io_bpt2arb_req_pd_ready),
    .io_bpt2arb_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_5_io_bpt2arb_req_pd_valid),
    .io_bpt2arb_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_5_io_bpt2arb_req_pd_bits),
    .io_tieoff_axid(NV_NVDLA_MCIF_READ_IG_bpt_5_io_tieoff_axid),
    .io_tieoff_lat_fifo_depth(NV_NVDLA_MCIF_READ_IG_bpt_5_io_tieoff_lat_fifo_depth)
  );
  NV_NVDLA_MCIF_READ_IG_bpt NV_NVDLA_MCIF_READ_IG_bpt_6 ( // @[NV_NVDLA_MCIF_READ_ig.scala 48:49:@3124.4]
    .reset(NV_NVDLA_MCIF_READ_IG_bpt_6_reset),
    .io_nvdla_core_clk(NV_NVDLA_MCIF_READ_IG_bpt_6_io_nvdla_core_clk),
    .io_dma2bpt_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_req_pd_ready),
    .io_dma2bpt_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_req_pd_valid),
    .io_dma2bpt_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_req_pd_bits),
    .io_dma2bpt_cdt_lat_fifo_pop(NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_cdt_lat_fifo_pop),
    .io_bpt2arb_req_pd_ready(NV_NVDLA_MCIF_READ_IG_bpt_6_io_bpt2arb_req_pd_ready),
    .io_bpt2arb_req_pd_valid(NV_NVDLA_MCIF_READ_IG_bpt_6_io_bpt2arb_req_pd_valid),
    .io_bpt2arb_req_pd_bits(NV_NVDLA_MCIF_READ_IG_bpt_6_io_bpt2arb_req_pd_bits),
    .io_tieoff_axid(NV_NVDLA_MCIF_READ_IG_bpt_6_io_tieoff_axid),
    .io_tieoff_lat_fifo_depth(NV_NVDLA_MCIF_READ_IG_bpt_6_io_tieoff_lat_fifo_depth)
  );
  NV_NVDLA_MCIF_READ_IG_arb u_arb ( // @[NV_NVDLA_MCIF_READ_ig.scala 60:23:@3176.4]
    .reset(u_arb_reset),
    .io_nvdla_core_clk(u_arb_io_nvdla_core_clk),
    .io_bpt2arb_req_pd_0_ready(u_arb_io_bpt2arb_req_pd_0_ready),
    .io_bpt2arb_req_pd_0_valid(u_arb_io_bpt2arb_req_pd_0_valid),
    .io_bpt2arb_req_pd_0_bits(u_arb_io_bpt2arb_req_pd_0_bits),
    .io_bpt2arb_req_pd_1_ready(u_arb_io_bpt2arb_req_pd_1_ready),
    .io_bpt2arb_req_pd_1_valid(u_arb_io_bpt2arb_req_pd_1_valid),
    .io_bpt2arb_req_pd_1_bits(u_arb_io_bpt2arb_req_pd_1_bits),
    .io_bpt2arb_req_pd_2_ready(u_arb_io_bpt2arb_req_pd_2_ready),
    .io_bpt2arb_req_pd_2_valid(u_arb_io_bpt2arb_req_pd_2_valid),
    .io_bpt2arb_req_pd_2_bits(u_arb_io_bpt2arb_req_pd_2_bits),
    .io_bpt2arb_req_pd_3_ready(u_arb_io_bpt2arb_req_pd_3_ready),
    .io_bpt2arb_req_pd_3_valid(u_arb_io_bpt2arb_req_pd_3_valid),
    .io_bpt2arb_req_pd_3_bits(u_arb_io_bpt2arb_req_pd_3_bits),
    .io_bpt2arb_req_pd_4_ready(u_arb_io_bpt2arb_req_pd_4_ready),
    .io_bpt2arb_req_pd_4_valid(u_arb_io_bpt2arb_req_pd_4_valid),
    .io_bpt2arb_req_pd_4_bits(u_arb_io_bpt2arb_req_pd_4_bits),
    .io_bpt2arb_req_pd_5_ready(u_arb_io_bpt2arb_req_pd_5_ready),
    .io_bpt2arb_req_pd_5_valid(u_arb_io_bpt2arb_req_pd_5_valid),
    .io_bpt2arb_req_pd_5_bits(u_arb_io_bpt2arb_req_pd_5_bits),
    .io_bpt2arb_req_pd_6_ready(u_arb_io_bpt2arb_req_pd_6_ready),
    .io_bpt2arb_req_pd_6_valid(u_arb_io_bpt2arb_req_pd_6_valid),
    .io_bpt2arb_req_pd_6_bits(u_arb_io_bpt2arb_req_pd_6_bits),
    .io_arb2spt_req_pd_ready(u_arb_io_arb2spt_req_pd_ready),
    .io_arb2spt_req_pd_valid(u_arb_io_arb2spt_req_pd_valid),
    .io_arb2spt_req_pd_bits(u_arb_io_arb2spt_req_pd_bits),
    .io_reg2dp_rd_weight_0(u_arb_io_reg2dp_rd_weight_0),
    .io_reg2dp_rd_weight_1(u_arb_io_reg2dp_rd_weight_1),
    .io_reg2dp_rd_weight_2(u_arb_io_reg2dp_rd_weight_2),
    .io_reg2dp_rd_weight_3(u_arb_io_reg2dp_rd_weight_3),
    .io_reg2dp_rd_weight_4(u_arb_io_reg2dp_rd_weight_4),
    .io_reg2dp_rd_weight_5(u_arb_io_reg2dp_rd_weight_5),
    .io_reg2dp_rd_weight_6(u_arb_io_reg2dp_rd_weight_6)
  );
  NV_NVDLA_XXIF_READ_IG_cvt u_cvt ( // @[NV_NVDLA_MCIF_READ_ig.scala 67:23:@3208.4]
    .reset(u_cvt_reset),
    .io_nvdla_core_clk(u_cvt_io_nvdla_core_clk),
    .io_reg2dp_rd_os_cnt(u_cvt_io_reg2dp_rd_os_cnt),
    .io_eg2ig_axi_vld(u_cvt_io_eg2ig_axi_vld),
    .io_spt2cvt_req_pd_ready(u_cvt_io_spt2cvt_req_pd_ready),
    .io_spt2cvt_req_pd_valid(u_cvt_io_spt2cvt_req_pd_valid),
    .io_spt2cvt_req_pd_bits(u_cvt_io_spt2cvt_req_pd_bits),
    .io_mcif2noc_axi_ar_ready(u_cvt_io_mcif2noc_axi_ar_ready),
    .io_mcif2noc_axi_ar_valid(u_cvt_io_mcif2noc_axi_ar_valid),
    .io_mcif2noc_axi_ar_bits_id(u_cvt_io_mcif2noc_axi_ar_bits_id),
    .io_mcif2noc_axi_ar_bits_len(u_cvt_io_mcif2noc_axi_ar_bits_len),
    .io_mcif2noc_axi_ar_bits_addr(u_cvt_io_mcif2noc_axi_ar_bits_addr)
  );
  assign io_client2mcif_rd_req_pd_0_ready = NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3131.4]
  assign io_client2mcif_rd_req_pd_1_ready = NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3138.4]
  assign io_client2mcif_rd_req_pd_2_ready = NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3145.4]
  assign io_client2mcif_rd_req_pd_3_ready = NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3152.4]
  assign io_client2mcif_rd_req_pd_4_ready = NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3159.4]
  assign io_client2mcif_rd_req_pd_5_ready = NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3166.4]
  assign io_client2mcif_rd_req_pd_6_ready = NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3173.4]
  assign io_mcif2noc_axi_ar_valid = u_cvt_io_mcif2noc_axi_ar_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 71:24:@3219.4]
  assign io_mcif2noc_axi_ar_bits_id = u_cvt_io_mcif2noc_axi_ar_bits_id; // @[NV_NVDLA_MCIF_READ_ig.scala 71:24:@3218.4]
  assign io_mcif2noc_axi_ar_bits_len = u_cvt_io_mcif2noc_axi_ar_bits_len; // @[NV_NVDLA_MCIF_READ_ig.scala 71:24:@3217.4]
  assign io_mcif2noc_axi_ar_bits_addr = u_cvt_io_mcif2noc_axi_ar_bits_addr; // @[NV_NVDLA_MCIF_READ_ig.scala 71:24:@3216.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_reset = reset; // @[:@3108.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 50:36:@3127.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_req_pd_valid = io_client2mcif_rd_req_pd_0_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3130.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_req_pd_bits = io_client2mcif_rd_req_pd_0_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3129.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_io_dma2bpt_cdt_lat_fifo_pop = io_client2mcif_rd_cdt_lat_fifo_pop_0; // @[NV_NVDLA_MCIF_READ_ig.scala 52:46:@3128.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_io_bpt2arb_req_pd_ready = u_arb_io_bpt2arb_req_pd_0_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3182.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_io_tieoff_axid = 4'h8; // @[NV_NVDLA_MCIF_READ_ig.scala 55:33:@3132.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_io_tieoff_lat_fifo_depth = 9'h0; // @[NV_NVDLA_MCIF_READ_ig.scala 56:43:@3133.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_1_reset = reset; // @[:@3111.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_1_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 50:36:@3134.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_req_pd_valid = io_client2mcif_rd_req_pd_1_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3137.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_req_pd_bits = io_client2mcif_rd_req_pd_1_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3136.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_1_io_dma2bpt_cdt_lat_fifo_pop = io_client2mcif_rd_cdt_lat_fifo_pop_1; // @[NV_NVDLA_MCIF_READ_ig.scala 52:46:@3135.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_1_io_bpt2arb_req_pd_ready = u_arb_io_bpt2arb_req_pd_1_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3185.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_1_io_tieoff_axid = 4'h9; // @[NV_NVDLA_MCIF_READ_ig.scala 55:33:@3139.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_1_io_tieoff_lat_fifo_depth = 9'h0; // @[NV_NVDLA_MCIF_READ_ig.scala 56:43:@3140.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_2_reset = reset; // @[:@3114.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_2_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 50:36:@3141.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_req_pd_valid = io_client2mcif_rd_req_pd_2_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3144.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_req_pd_bits = io_client2mcif_rd_req_pd_2_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3143.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_2_io_dma2bpt_cdt_lat_fifo_pop = io_client2mcif_rd_cdt_lat_fifo_pop_2; // @[NV_NVDLA_MCIF_READ_ig.scala 52:46:@3142.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_2_io_bpt2arb_req_pd_ready = u_arb_io_bpt2arb_req_pd_2_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3188.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_2_io_tieoff_axid = 4'h1; // @[NV_NVDLA_MCIF_READ_ig.scala 55:33:@3146.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_2_io_tieoff_lat_fifo_depth = 9'h8; // @[NV_NVDLA_MCIF_READ_ig.scala 56:43:@3147.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_3_reset = reset; // @[:@3117.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_3_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 50:36:@3148.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_req_pd_valid = io_client2mcif_rd_req_pd_3_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3151.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_req_pd_bits = io_client2mcif_rd_req_pd_3_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3150.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_3_io_dma2bpt_cdt_lat_fifo_pop = io_client2mcif_rd_cdt_lat_fifo_pop_3; // @[NV_NVDLA_MCIF_READ_ig.scala 52:46:@3149.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_3_io_bpt2arb_req_pd_ready = u_arb_io_bpt2arb_req_pd_3_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3191.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_3_io_tieoff_axid = 4'h5; // @[NV_NVDLA_MCIF_READ_ig.scala 55:33:@3153.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_3_io_tieoff_lat_fifo_depth = 9'h10; // @[NV_NVDLA_MCIF_READ_ig.scala 56:43:@3154.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_4_reset = reset; // @[:@3120.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_4_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 50:36:@3155.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_req_pd_valid = io_client2mcif_rd_req_pd_4_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3158.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_req_pd_bits = io_client2mcif_rd_req_pd_4_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3157.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_4_io_dma2bpt_cdt_lat_fifo_pop = io_client2mcif_rd_cdt_lat_fifo_pop_4; // @[NV_NVDLA_MCIF_READ_ig.scala 52:46:@3156.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_4_io_bpt2arb_req_pd_ready = u_arb_io_bpt2arb_req_pd_4_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3194.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_4_io_tieoff_axid = 4'h6; // @[NV_NVDLA_MCIF_READ_ig.scala 55:33:@3160.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_4_io_tieoff_lat_fifo_depth = 9'h10; // @[NV_NVDLA_MCIF_READ_ig.scala 56:43:@3161.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_5_reset = reset; // @[:@3123.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_5_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 50:36:@3162.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_req_pd_valid = io_client2mcif_rd_req_pd_5_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3165.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_req_pd_bits = io_client2mcif_rd_req_pd_5_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3164.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_5_io_dma2bpt_cdt_lat_fifo_pop = io_client2mcif_rd_cdt_lat_fifo_pop_5; // @[NV_NVDLA_MCIF_READ_ig.scala 52:46:@3163.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_5_io_bpt2arb_req_pd_ready = u_arb_io_bpt2arb_req_pd_5_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3197.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_5_io_tieoff_axid = 4'h2; // @[NV_NVDLA_MCIF_READ_ig.scala 55:33:@3167.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_5_io_tieoff_lat_fifo_depth = 9'h8; // @[NV_NVDLA_MCIF_READ_ig.scala 56:43:@3168.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_6_reset = reset; // @[:@3126.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_6_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 50:36:@3169.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_req_pd_valid = io_client2mcif_rd_req_pd_6_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3172.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_req_pd_bits = io_client2mcif_rd_req_pd_6_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 53:36:@3171.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_6_io_dma2bpt_cdt_lat_fifo_pop = io_client2mcif_rd_cdt_lat_fifo_pop_6; // @[NV_NVDLA_MCIF_READ_ig.scala 52:46:@3170.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_6_io_bpt2arb_req_pd_ready = u_arb_io_bpt2arb_req_pd_6_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3200.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_6_io_tieoff_axid = 4'h3; // @[NV_NVDLA_MCIF_READ_ig.scala 55:33:@3174.4]
  assign NV_NVDLA_MCIF_READ_IG_bpt_6_io_tieoff_lat_fifo_depth = 9'h8; // @[NV_NVDLA_MCIF_READ_ig.scala 56:43:@3175.4]
  assign u_arb_reset = reset; // @[:@3178.4]
  assign u_arb_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 61:29:@3179.4]
  assign u_arb_io_bpt2arb_req_pd_0_valid = NV_NVDLA_MCIF_READ_IG_bpt_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3181.4]
  assign u_arb_io_bpt2arb_req_pd_0_bits = NV_NVDLA_MCIF_READ_IG_bpt_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3180.4]
  assign u_arb_io_bpt2arb_req_pd_1_valid = NV_NVDLA_MCIF_READ_IG_bpt_1_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3184.4]
  assign u_arb_io_bpt2arb_req_pd_1_bits = NV_NVDLA_MCIF_READ_IG_bpt_1_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3183.4]
  assign u_arb_io_bpt2arb_req_pd_2_valid = NV_NVDLA_MCIF_READ_IG_bpt_2_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3187.4]
  assign u_arb_io_bpt2arb_req_pd_2_bits = NV_NVDLA_MCIF_READ_IG_bpt_2_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3186.4]
  assign u_arb_io_bpt2arb_req_pd_3_valid = NV_NVDLA_MCIF_READ_IG_bpt_3_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3190.4]
  assign u_arb_io_bpt2arb_req_pd_3_bits = NV_NVDLA_MCIF_READ_IG_bpt_3_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3189.4]
  assign u_arb_io_bpt2arb_req_pd_4_valid = NV_NVDLA_MCIF_READ_IG_bpt_4_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3193.4]
  assign u_arb_io_bpt2arb_req_pd_4_bits = NV_NVDLA_MCIF_READ_IG_bpt_4_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3192.4]
  assign u_arb_io_bpt2arb_req_pd_5_valid = NV_NVDLA_MCIF_READ_IG_bpt_5_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3196.4]
  assign u_arb_io_bpt2arb_req_pd_5_bits = NV_NVDLA_MCIF_READ_IG_bpt_5_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3195.4]
  assign u_arb_io_bpt2arb_req_pd_6_valid = NV_NVDLA_MCIF_READ_IG_bpt_6_io_bpt2arb_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3199.4]
  assign u_arb_io_bpt2arb_req_pd_6_bits = NV_NVDLA_MCIF_READ_IG_bpt_6_io_bpt2arb_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 63:36:@3198.4]
  assign u_arb_io_arb2spt_req_pd_ready = u_cvt_io_spt2cvt_req_pd_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 70:29:@3215.4]
  assign u_arb_io_reg2dp_rd_weight_0 = io_reg2dp_rd_weight_0; // @[NV_NVDLA_MCIF_READ_ig.scala 65:31:@3201.4]
  assign u_arb_io_reg2dp_rd_weight_1 = io_reg2dp_rd_weight_1; // @[NV_NVDLA_MCIF_READ_ig.scala 65:31:@3202.4]
  assign u_arb_io_reg2dp_rd_weight_2 = io_reg2dp_rd_weight_2; // @[NV_NVDLA_MCIF_READ_ig.scala 65:31:@3203.4]
  assign u_arb_io_reg2dp_rd_weight_3 = io_reg2dp_rd_weight_3; // @[NV_NVDLA_MCIF_READ_ig.scala 65:31:@3204.4]
  assign u_arb_io_reg2dp_rd_weight_4 = io_reg2dp_rd_weight_4; // @[NV_NVDLA_MCIF_READ_ig.scala 65:31:@3205.4]
  assign u_arb_io_reg2dp_rd_weight_5 = io_reg2dp_rd_weight_5; // @[NV_NVDLA_MCIF_READ_ig.scala 65:31:@3206.4]
  assign u_arb_io_reg2dp_rd_weight_6 = io_reg2dp_rd_weight_6; // @[NV_NVDLA_MCIF_READ_ig.scala 65:31:@3207.4]
  assign u_cvt_reset = reset; // @[:@3210.4]
  assign u_cvt_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_ig.scala 68:29:@3211.4]
  assign u_cvt_io_reg2dp_rd_os_cnt = io_reg2dp_rd_os_cnt; // @[NV_NVDLA_MCIF_READ_ig.scala 72:31:@3221.4]
  assign u_cvt_io_eg2ig_axi_vld = io_eg2ig_axi_vld; // @[NV_NVDLA_MCIF_READ_ig.scala 69:28:@3212.4]
  assign u_cvt_io_spt2cvt_req_pd_valid = u_arb_io_arb2spt_req_pd_valid; // @[NV_NVDLA_MCIF_READ_ig.scala 70:29:@3214.4]
  assign u_cvt_io_spt2cvt_req_pd_bits = u_arb_io_arb2spt_req_pd_bits; // @[NV_NVDLA_MCIF_READ_ig.scala 70:29:@3213.4]
  assign u_cvt_io_mcif2noc_axi_ar_ready = io_mcif2noc_axi_ar_ready; // @[NV_NVDLA_MCIF_READ_ig.scala 71:24:@3220.4]
endmodule
