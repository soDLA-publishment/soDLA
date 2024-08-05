module NV_COUNTER_STAGE_os( // @[:@3.2]
  input        reset, // @[:@5.4]
  input        io_clk, // @[:@6.4]
  input  [2:0] io_os_cnt_add, // @[:@6.4]
  input  [2:0] io_os_cnt_sub, // @[:@6.4]
  input        io_os_cnt_cen, // @[:@6.4]
  output [8:0] io_os_cnt_cur // @[:@6.4]
);
  wire  _T_15; // @[Perf_Counter.scala 244:32:@8.4]
  reg [8:0] _T_18; // @[Perf_Counter.scala 247:33:@9.4]
  reg [31:0] _RAND_0;
  wire [8:0] _GEN_1; // @[Perf_Counter.scala 254:34:@15.4]
  wire [9:0] _T_27; // @[Perf_Counter.scala 254:34:@15.4]
  wire [9:0] _GEN_2; // @[Perf_Counter.scala 254:51:@16.4]
  wire [10:0] _T_28; // @[Perf_Counter.scala 254:51:@16.4]
  wire [10:0] _T_29; // @[Perf_Counter.scala 254:51:@17.4]
  wire [10:0] _T_20; // @[Perf_Counter.scala 248:26:@10.4 Perf_Counter.scala 253:16:@14.4]
  wire [10:0] _T_30; // @[Perf_Counter.scala 255:22:@19.4]
  wire [10:0] _GEN_0; // @[Perf_Counter.scala 259:24:@22.4]
  assign _T_15 = io_os_cnt_add != io_os_cnt_sub; // @[Perf_Counter.scala 244:32:@8.4]
  assign _GEN_1 = {{6'd0}, io_os_cnt_add}; // @[Perf_Counter.scala 254:34:@15.4]
  assign _T_27 = _T_18 + _GEN_1; // @[Perf_Counter.scala 254:34:@15.4]
  assign _GEN_2 = {{7'd0}, io_os_cnt_sub}; // @[Perf_Counter.scala 254:51:@16.4]
  assign _T_28 = _T_27 - _GEN_2; // @[Perf_Counter.scala 254:51:@16.4]
  assign _T_29 = $unsigned(_T_28); // @[Perf_Counter.scala 254:51:@17.4]
  assign _T_20 = {{2'd0}, _T_18}; // @[Perf_Counter.scala 248:26:@10.4 Perf_Counter.scala 253:16:@14.4]
  assign _T_30 = _T_15 ? _T_29 : _T_20; // @[Perf_Counter.scala 255:22:@19.4]
  assign _GEN_0 = io_os_cnt_cen ? _T_30 : {{2'd0}, _T_18}; // @[Perf_Counter.scala 259:24:@22.4]
  assign io_os_cnt_cur = _T_18; // @[Perf_Counter.scala 263:19:@25.4]
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
module NV_NVDLA_IS_pipe( // @[:@27.2]
  input         reset, // @[:@29.4]
  input         io_clk, // @[:@30.4]
  output [37:0] io_dout, // @[:@30.4]
  output        io_vo, // @[:@30.4]
  input         io_ri, // @[:@30.4]
  input  [37:0] io_di, // @[:@30.4]
  input         io_vi, // @[:@30.4]
  output        io_ro // @[:@30.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@32.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@33.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@34.4]
  reg [31:0] _RAND_2;
  reg [37:0] skid_flop_di; // @[IS_pipe.scala 57:53:@35.4]
  reg [63:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@36.4]
  reg [31:0] _RAND_4;
  reg [37:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@37.4]
  reg [63:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@46.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@51.4]
  wire [37:0] _GEN_1; // @[IS_pipe.scala 76:29:@52.4]
  wire [37:0] skid_di; // @[IS_pipe.scala 79:19:@55.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@57.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@58.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@60.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@63.4]
  wire [37:0] _GEN_3; // @[IS_pipe.scala 87:29:@64.4]
  assign _GEN_0 = skid_flop_ro ? io_vi : skid_flop_vi; // @[IS_pipe.scala 71:23:@46.4]
  assign _T_38 = skid_flop_ro & io_vi; // @[IS_pipe.scala 76:22:@51.4]
  assign _GEN_1 = _T_38 ? io_di : skid_flop_di; // @[IS_pipe.scala 76:29:@52.4]
  assign skid_di = skid_flop_ro ? io_di : skid_flop_di; // @[IS_pipe.scala 79:19:@55.4]
  assign _T_40 = ~ pipe_skid_vi; // @[IS_pipe.scala 81:32:@57.4]
  assign skid_ro = io_ri | _T_40; // @[IS_pipe.scala 81:29:@58.4]
  assign _GEN_2 = skid_ro ? _GEN_0 : pipe_skid_vi; // @[IS_pipe.scala 83:18:@60.4]
  assign _T_42 = skid_ro & _GEN_0; // @[IS_pipe.scala 87:18:@63.4]
  assign _GEN_3 = _T_42 ? skid_di : pipe_skid_di; // @[IS_pipe.scala 87:29:@64.4]
  assign io_dout = pipe_skid_di; // @[IS_pipe.scala 98:13:@72.4]
  assign io_vo = pipe_skid_vi; // @[IS_pipe.scala 97:11:@71.4]
  assign io_ro = ro_out; // @[IS_pipe.scala 96:11:@70.4]
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
module NV_NVDLA_XXIF_READ_IG_cvt( // @[:@74.2]
  input         clock, // @[:@75.4]
  input         reset, // @[:@76.4]
  input         io_nvdla_core_clk, // @[:@77.4]
  input  [7:0]  io_reg2dp_rd_os_cnt, // @[:@77.4]
  input         io_eg2ig_axi_vld, // @[:@77.4]
  output        io_spt2cvt_req_pd_ready, // @[:@77.4]
  input         io_spt2cvt_req_pd_valid, // @[:@77.4]
  input  [42:0] io_spt2cvt_req_pd_bits, // @[:@77.4]
  input         io_mcif2noc_axi_ar_ready, // @[:@77.4]
  output        io_mcif2noc_axi_ar_valid, // @[:@77.4]
  output [7:0]  io_mcif2noc_axi_ar_bits_id, // @[:@77.4]
  output [3:0]  io_mcif2noc_axi_ar_bits_len, // @[:@77.4]
  output [63:0] io_mcif2noc_axi_ar_bits_addr // @[:@77.4]
);
  wire  perf_os_reset; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@119.4]
  wire  perf_os_io_clk; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@119.4]
  wire [2:0] perf_os_io_os_cnt_add; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@119.4]
  wire [2:0] perf_os_io_os_cnt_sub; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@119.4]
  wire  perf_os_io_os_cnt_cen; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@119.4]
  wire [8:0] perf_os_io_os_cnt_cur; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@119.4]
  wire  pipe_p1_reset; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@129.4]
  wire  pipe_p1_io_clk; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@129.4]
  wire [37:0] pipe_p1_io_dout; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@129.4]
  wire  pipe_p1_io_vo; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@129.4]
  wire  pipe_p1_io_ri; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@129.4]
  wire [37:0] pipe_p1_io_di; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@129.4]
  wire  pipe_p1_io_vi; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@129.4]
  wire  pipe_p1_io_ro; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@129.4]
  wire [3:0] cmd_axid; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 36:42:@81.4]
  wire [31:0] cmd_addr; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 37:42:@82.4]
  wire [2:0] cmd_size; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 38:42:@83.4]
  wire [8:0] os_cnt; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 72:22:@101.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 94:12:@126.4]
  wire [1:0] axi_len; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 46:23:@90.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 66:17:@98.4]
  wire [2:0] _T_54; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 73:47:@102.4]
  wire [2:0] os_inp_add_nxt; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 73:29:@103.4]
  wire [8:0] _GEN_0; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:29:@107.4]
  wire [9:0] _T_60; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:29:@107.4]
  reg  eg2ig_axi_vld_d; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 74:34:@104.4]
  reg [31:0] _RAND_0;
  wire [9:0] _GEN_1; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@108.4]
  wire [10:0] _T_61; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@108.4]
  wire [10:0] os_inp_nxt; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@109.4]
  wire [8:0] rd_os_cnt_ext; // @[Cat.scala 30:58:@115.4]
  wire [9:0] _T_69; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:48:@116.4]
  wire [10:0] _GEN_2; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:31:@117.4]
  wire  os_cnt_full; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:31:@117.4]
  wire  _T_46; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 64:34:@92.4]
  wire  axi_cmd_vld; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 64:32:@93.4]
  wire  axi_cmd_rdy; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 45:27:@89.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 101:17:@134.4]
  wire [28:0] _T_50; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 70:32:@99.4]
  wire  os_cnt_add_en; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 80:37:@110.4]
  wire [35:0] _T_71; // @[Cat.scala 30:58:@127.4]
  wire [3:0] opipe_axi_axid; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 107:41:@138.4]
  wire [31:0] opipe_axi_addr; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 108:41:@139.4]
  wire [1:0] opipe_axi_len; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 109:41:@140.4]
  NV_COUNTER_STAGE_os perf_os ( // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 89:25:@119.4]
    .reset(perf_os_reset),
    .io_clk(perf_os_io_clk),
    .io_os_cnt_add(perf_os_io_os_cnt_add),
    .io_os_cnt_sub(perf_os_io_os_cnt_sub),
    .io_os_cnt_cen(perf_os_io_os_cnt_cen),
    .io_os_cnt_cur(perf_os_io_os_cnt_cur)
  );
  NV_NVDLA_IS_pipe pipe_p1 ( // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 98:25:@129.4]
    .reset(pipe_p1_reset),
    .io_clk(pipe_p1_io_clk),
    .io_dout(pipe_p1_io_dout),
    .io_vo(pipe_p1_io_vo),
    .io_ri(pipe_p1_io_ri),
    .io_di(pipe_p1_io_di),
    .io_vi(pipe_p1_io_vi),
    .io_ro(pipe_p1_io_ro)
  );
  assign cmd_axid = io_spt2cvt_req_pd_bits[3:0]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 36:42:@81.4]
  assign cmd_addr = io_spt2cvt_req_pd_bits[35:4]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 37:42:@82.4]
  assign cmd_size = io_spt2cvt_req_pd_bits[38:36]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 38:42:@83.4]
  assign os_cnt = perf_os_io_os_cnt_cur; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 72:22:@101.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 94:12:@126.4]
  assign axi_len = cmd_size[1:0]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 46:23:@90.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 66:17:@98.4]
  assign _T_54 = axi_len + 2'h1; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 73:47:@102.4]
  assign os_inp_add_nxt = io_spt2cvt_req_pd_valid ? _T_54 : 3'h0; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 73:29:@103.4]
  assign _GEN_0 = {{6'd0}, os_inp_add_nxt}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:29:@107.4]
  assign _T_60 = os_cnt + _GEN_0; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:29:@107.4]
  assign _GEN_1 = {{9'd0}, eg2ig_axi_vld_d}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@108.4]
  assign _T_61 = _T_60 - _GEN_1; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@108.4]
  assign os_inp_nxt = $unsigned(_T_61); // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 77:47:@109.4]
  assign rd_os_cnt_ext = {1'h0,io_reg2dp_rd_os_cnt}; // @[Cat.scala 30:58:@115.4]
  assign _T_69 = rd_os_cnt_ext + 9'h1; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:48:@116.4]
  assign _GEN_2 = {{1'd0}, _T_69}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:31:@117.4]
  assign os_cnt_full = os_inp_nxt > _GEN_2; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 87:31:@117.4]
  assign _T_46 = ~ os_cnt_full; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 64:34:@92.4]
  assign axi_cmd_vld = io_spt2cvt_req_pd_valid & _T_46; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 64:32:@93.4]
  assign axi_cmd_rdy = pipe_p1_io_ro; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 45:27:@89.4 NV_NVDLA_XXIF_READ_IG_cvt.scala 101:17:@134.4]
  assign _T_50 = cmd_addr[31:3]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 70:32:@99.4]
  assign os_cnt_add_en = axi_cmd_vld & axi_cmd_rdy; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 80:37:@110.4]
  assign _T_71 = {cmd_axid,_T_50,3'h0}; // @[Cat.scala 30:58:@127.4]
  assign opipe_axi_axid = pipe_p1_io_dout[37:34]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 107:41:@138.4]
  assign opipe_axi_addr = pipe_p1_io_dout[33:2]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 108:41:@139.4]
  assign opipe_axi_len = pipe_p1_io_dout[1:0]; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 109:41:@140.4]
  assign io_spt2cvt_req_pd_ready = axi_cmd_rdy & _T_46; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 34:29:@80.4]
  assign io_mcif2noc_axi_ar_valid = pipe_p1_io_vo; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 104:30:@136.4]
  assign io_mcif2noc_axi_ar_bits_id = {4'h0,opipe_axi_axid}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 111:35:@142.4]
  assign io_mcif2noc_axi_ar_bits_len = {2'h0,opipe_axi_len}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 113:35:@146.4]
  assign io_mcif2noc_axi_ar_bits_addr = {32'h0,opipe_axi_addr}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 112:35:@144.4]
  assign perf_os_reset = reset; // @[:@121.4]
  assign perf_os_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 90:20:@122.4]
  assign perf_os_io_os_cnt_add = os_cnt_add_en ? _T_54 : 3'h0; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 91:27:@123.4]
  assign perf_os_io_os_cnt_sub = {{2'd0}, eg2ig_axi_vld_d}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 92:27:@124.4]
  assign perf_os_io_os_cnt_cen = os_cnt_add_en | eg2ig_axi_vld_d; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 93:27:@125.4]
  assign pipe_p1_reset = reset; // @[:@131.4]
  assign pipe_p1_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 99:20:@132.4]
  assign pipe_p1_io_ri = io_mcif2noc_axi_ar_ready; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 105:19:@137.4]
  assign pipe_p1_io_di = {_T_71,axi_len}; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 102:19:@135.4]
  assign pipe_p1_io_vi = io_spt2cvt_req_pd_valid & _T_46; // @[NV_NVDLA_XXIF_READ_IG_cvt.scala 100:19:@133.4]
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
