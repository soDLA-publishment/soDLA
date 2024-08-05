module NV_NVDLA_IS_pipe( // @[:@3.2]
  input         reset, // @[:@5.4]
  input         io_clk, // @[:@6.4]
  output [67:0] io_dout, // @[:@6.4]
  output        io_vo, // @[:@6.4]
  input         io_ri, // @[:@6.4]
  input  [67:0] io_di, // @[:@6.4]
  input         io_vi, // @[:@6.4]
  output        io_ro // @[:@6.4]
);
  reg  ro_out; // @[IS_pipe.scala 54:25:@8.4]
  reg [31:0] _RAND_0;
  reg  skid_flop_ro; // @[IS_pipe.scala 55:31:@9.4]
  reg [31:0] _RAND_1;
  reg  skid_flop_vi; // @[IS_pipe.scala 56:31:@10.4]
  reg [31:0] _RAND_2;
  reg [67:0] skid_flop_di; // @[IS_pipe.scala 57:53:@11.4]
  reg [95:0] _RAND_3;
  reg  pipe_skid_vi; // @[IS_pipe.scala 58:31:@12.4]
  reg [31:0] _RAND_4;
  reg [67:0] pipe_skid_di; // @[IS_pipe.scala 59:53:@13.4]
  reg [95:0] _RAND_5;
  wire  _GEN_0; // @[IS_pipe.scala 71:23:@22.4]
  wire  _T_38; // @[IS_pipe.scala 76:22:@27.4]
  wire [67:0] _GEN_1; // @[IS_pipe.scala 76:29:@28.4]
  wire [67:0] skid_di; // @[IS_pipe.scala 79:19:@31.4]
  wire  _T_40; // @[IS_pipe.scala 81:32:@33.4]
  wire  skid_ro; // @[IS_pipe.scala 81:29:@34.4]
  wire  _GEN_2; // @[IS_pipe.scala 83:18:@36.4]
  wire  _T_42; // @[IS_pipe.scala 87:18:@39.4]
  wire [67:0] _GEN_3; // @[IS_pipe.scala 87:29:@40.4]
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
  skid_flop_di = _RAND_3[67:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  pipe_skid_vi = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {3{`RANDOM}};
  pipe_skid_di = _RAND_5[67:0];
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
      skid_flop_di <= 68'h0;
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
      pipe_skid_di <= 68'h0;
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
module nv_flopram( // @[:@57.2]
  input         reset, // @[:@59.4]
  input         io_clk, // @[:@60.4]
  input  [63:0] io_di, // @[:@60.4]
  input         io_we, // @[:@60.4]
  input  [1:0]  io_wa, // @[:@60.4]
  input  [2:0]  io_ra, // @[:@60.4]
  output [63:0] io_dout // @[:@60.4]
);
  reg [63:0] _T_22; // @[nv_flopram.scala 68:61:@64.4]
  reg [63:0] _RAND_0;
  reg [63:0] _T_25; // @[nv_flopram.scala 68:61:@65.4]
  reg [63:0] _RAND_1;
  reg [63:0] _T_28; // @[nv_flopram.scala 68:61:@66.4]
  reg [63:0] _RAND_2;
  reg [63:0] _T_31; // @[nv_flopram.scala 68:61:@67.4]
  reg [63:0] _RAND_3;
  wire  _T_35; // @[nv_flopram.scala 73:32:@70.6]
  wire [63:0] _GEN_0; // @[nv_flopram.scala 73:40:@71.6]
  wire  _T_37; // @[nv_flopram.scala 73:32:@74.6]
  wire [63:0] _GEN_1; // @[nv_flopram.scala 73:40:@75.6]
  wire  _T_39; // @[nv_flopram.scala 73:32:@78.6]
  wire [63:0] _GEN_2; // @[nv_flopram.scala 73:40:@79.6]
  wire  _T_41; // @[nv_flopram.scala 73:32:@82.6]
  wire [63:0] _GEN_3; // @[nv_flopram.scala 73:40:@83.6]
  wire [63:0] _GEN_4; // @[nv_flopram.scala 70:16:@69.4]
  wire [63:0] _GEN_5; // @[nv_flopram.scala 70:16:@69.4]
  wire [63:0] _GEN_6; // @[nv_flopram.scala 70:16:@69.4]
  wire [63:0] _GEN_7; // @[nv_flopram.scala 70:16:@69.4]
  wire  _T_48; // @[Mux.scala 46:19:@88.4]
  wire [63:0] _T_49; // @[Mux.scala 46:16:@89.4]
  wire  _T_50; // @[Mux.scala 46:19:@90.4]
  wire [63:0] _T_51; // @[Mux.scala 46:16:@91.4]
  wire  _T_52; // @[Mux.scala 46:19:@92.4]
  wire [63:0] _T_53; // @[Mux.scala 46:16:@93.4]
  wire  _T_54; // @[Mux.scala 46:19:@94.4]
  wire [63:0] _T_55; // @[Mux.scala 46:16:@95.4]
  wire  _T_56; // @[Mux.scala 46:19:@96.4]
  assign _T_35 = io_wa == 2'h0; // @[nv_flopram.scala 73:32:@70.6]
  assign _GEN_0 = _T_35 ? io_di : _T_22; // @[nv_flopram.scala 73:40:@71.6]
  assign _T_37 = io_wa == 2'h1; // @[nv_flopram.scala 73:32:@74.6]
  assign _GEN_1 = _T_37 ? io_di : _T_25; // @[nv_flopram.scala 73:40:@75.6]
  assign _T_39 = io_wa == 2'h2; // @[nv_flopram.scala 73:32:@78.6]
  assign _GEN_2 = _T_39 ? io_di : _T_28; // @[nv_flopram.scala 73:40:@79.6]
  assign _T_41 = io_wa == 2'h3; // @[nv_flopram.scala 73:32:@82.6]
  assign _GEN_3 = _T_41 ? io_di : _T_31; // @[nv_flopram.scala 73:40:@83.6]
  assign _GEN_4 = io_we ? _GEN_0 : _T_22; // @[nv_flopram.scala 70:16:@69.4]
  assign _GEN_5 = io_we ? _GEN_1 : _T_25; // @[nv_flopram.scala 70:16:@69.4]
  assign _GEN_6 = io_we ? _GEN_2 : _T_28; // @[nv_flopram.scala 70:16:@69.4]
  assign _GEN_7 = io_we ? _GEN_3 : _T_31; // @[nv_flopram.scala 70:16:@69.4]
  assign _T_48 = 3'h4 == io_ra; // @[Mux.scala 46:19:@88.4]
  assign _T_49 = _T_48 ? io_di : 64'h0; // @[Mux.scala 46:16:@89.4]
  assign _T_50 = 3'h3 == io_ra; // @[Mux.scala 46:19:@90.4]
  assign _T_51 = _T_50 ? _T_31 : _T_49; // @[Mux.scala 46:16:@91.4]
  assign _T_52 = 3'h2 == io_ra; // @[Mux.scala 46:19:@92.4]
  assign _T_53 = _T_52 ? _T_28 : _T_51; // @[Mux.scala 46:16:@93.4]
  assign _T_54 = 3'h1 == io_ra; // @[Mux.scala 46:19:@94.4]
  assign _T_55 = _T_54 ? _T_25 : _T_53; // @[Mux.scala 46:16:@95.4]
  assign _T_56 = 3'h0 == io_ra; // @[Mux.scala 46:19:@96.4]
  assign io_dout = _T_56 ? _T_22 : _T_55; // @[nv_flopram.scala 83:13:@98.4]
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
  _RAND_0 = {2{`RANDOM}};
  _T_22 = _RAND_0[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {2{`RANDOM}};
  _T_25 = _RAND_1[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_28 = _RAND_2[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {2{`RANDOM}};
  _T_31 = _RAND_3[63:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      _T_22 <= 64'h0;
    end else begin
      if (io_we) begin
        if (_T_35) begin
          _T_22 <= io_di;
        end
      end
    end
    if (reset) begin
      _T_25 <= 64'h0;
    end else begin
      if (io_we) begin
        if (_T_37) begin
          _T_25 <= io_di;
        end
      end
    end
    if (reset) begin
      _T_28 <= 64'h0;
    end else begin
      if (io_we) begin
        if (_T_39) begin
          _T_28 <= io_di;
        end
      end
    end
    if (reset) begin
      _T_31 <= 64'h0;
    end else begin
      if (io_we) begin
        if (_T_41) begin
          _T_31 <= io_di;
        end
      end
    end
  end
endmodule
module NV_NVDLA_fifo_new( // @[:@100.2]
  input         clock, // @[:@101.4]
  input         reset, // @[:@102.4]
  input         io_wr_pvld, // @[:@103.4]
  output        io_wr_prdy, // @[:@103.4]
  input  [63:0] io_wr_pd, // @[:@103.4]
  output        io_rd_pvld, // @[:@103.4]
  input         io_rd_prdy, // @[:@103.4]
  output [63:0] io_rd_pd // @[:@103.4]
);
  wire  ram_reset; // @[FIFO_new.scala 254:29:@156.4]
  wire  ram_io_clk; // @[FIFO_new.scala 254:29:@156.4]
  wire [63:0] ram_io_di; // @[FIFO_new.scala 254:29:@156.4]
  wire  ram_io_we; // @[FIFO_new.scala 254:29:@156.4]
  wire [1:0] ram_io_wa; // @[FIFO_new.scala 254:29:@156.4]
  wire [2:0] ram_io_ra; // @[FIFO_new.scala 254:29:@156.4]
  wire [63:0] ram_io_dout; // @[FIFO_new.scala 254:29:@156.4]
  reg  wr_busy_int; // @[FIFO_new.scala 162:56:@112.4]
  reg [31:0] _RAND_0;
  wire  _T_26; // @[FIFO_new.scala 192:23:@114.4]
  wire  wr_reserving; // @[FIFO_new.scala 193:36:@117.4]
  reg [2:0] wr_count; // @[FIFO_new.scala 196:53:@120.4]
  reg [31:0] _RAND_1;
  wire [3:0] _T_33; // @[FIFO_new.scala 200:76:@121.4]
  wire [3:0] _T_34; // @[FIFO_new.scala 200:76:@122.4]
  wire [2:0] _T_35; // @[FIFO_new.scala 200:76:@123.4]
  wire [2:0] wr_count_next_wr_popping; // @[FIFO_new.scala 200:43:@124.4]
  wire [3:0] _T_37; // @[FIFO_new.scala 201:69:@125.4]
  wire [2:0] _T_38; // @[FIFO_new.scala 201:69:@126.4]
  wire [2:0] wr_count_next_no_wr_popping; // @[FIFO_new.scala 201:46:@127.4]
  reg  _T_63; // @[FIFO_new.scala 345:108:@168.4]
  reg [31:0] _RAND_2;
  wire  rd_popping; // @[FIFO_new.scala 374:47:@182.4]
  wire [2:0] wr_count_next; // @[FIFO_new.scala 202:32:@128.4]
  wire  wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 204:80:@129.4]
  wire  wr_count_next_is_full; // @[FIFO_new.scala 205:40:@130.4]
  wire  _T_47; // @[FIFO_new.scala 212:27:@138.4]
  wire [2:0] _GEN_0; // @[FIFO_new.scala 212:40:@139.4]
  reg [1:0] _T_50; // @[FIFO_new.scala 224:68:@142.4]
  reg [31:0] _RAND_3;
  wire [2:0] _T_52; // @[FIFO_new.scala 227:47:@143.4]
  wire [1:0] wr_adr_next; // @[FIFO_new.scala 227:47:@144.4]
  wire [1:0] _GEN_1; // @[FIFO_new.scala 228:29:@145.4]
  reg [1:0] rd_adr; // @[FIFO_new.scala 233:63:@149.4]
  reg [31:0] _RAND_4;
  wire [2:0] _T_57; // @[FIFO_new.scala 235:42:@150.4]
  wire [1:0] rd_adr_next_popping; // @[FIFO_new.scala 235:42:@151.4]
  wire [1:0] _GEN_2; // @[FIFO_new.scala 244:33:@152.4]
  reg  rd_pvld_p; // @[FIFO_new.scala 343:137:@167.4]
  reg [31:0] _RAND_5;
  reg [2:0] rd_count; // @[FIFO_new.scala 347:53:@169.4]
  reg [31:0] _RAND_6;
  wire [3:0] _T_67; // @[FIFO_new.scala 349:74:@170.4]
  wire [3:0] _T_68; // @[FIFO_new.scala 349:74:@171.4]
  wire [2:0] _T_69; // @[FIFO_new.scala 349:74:@172.4]
  wire [2:0] rd_count_next_rd_popping; // @[FIFO_new.scala 349:43:@173.4]
  wire [3:0] _T_71; // @[FIFO_new.scala 350:68:@174.4]
  wire [2:0] _T_72; // @[FIFO_new.scala 350:68:@175.4]
  wire [2:0] rd_count_next_no_rd_popping; // @[FIFO_new.scala 350:46:@176.4]
  wire [2:0] rd_count_next; // @[FIFO_new.scala 351:32:@177.4]
  wire  _T_73; // @[FIFO_new.scala 353:25:@178.4]
  wire [2:0] _GEN_3; // @[FIFO_new.scala 353:39:@179.4]
  wire  rd_count_p_next_rd_popping_not_0; // @[FIFO_new.scala 376:81:@184.4]
  wire  rd_count_p_next_no_rd_popping_not_0; // @[FIFO_new.scala 377:87:@185.4]
  wire  rd_count_p_next_not_0; // @[FIFO_new.scala 378:48:@186.4]
  wire  _GEN_4; // @[FIFO_new.scala 380:47:@188.4]
  wire  _GEN_5; // @[FIFO_new.scala 380:47:@188.4]
  nv_flopram ram ( // @[FIFO_new.scala 254:29:@156.4]
    .reset(ram_reset),
    .io_clk(ram_io_clk),
    .io_di(ram_io_di),
    .io_we(ram_io_we),
    .io_wa(ram_io_wa),
    .io_ra(ram_io_ra),
    .io_dout(ram_io_dout)
  );
  assign _T_26 = ~ wr_busy_int; // @[FIFO_new.scala 192:23:@114.4]
  assign wr_reserving = io_wr_pvld & _T_26; // @[FIFO_new.scala 193:36:@117.4]
  assign _T_33 = wr_count - 3'h1; // @[FIFO_new.scala 200:76:@121.4]
  assign _T_34 = $unsigned(_T_33); // @[FIFO_new.scala 200:76:@122.4]
  assign _T_35 = _T_34[2:0]; // @[FIFO_new.scala 200:76:@123.4]
  assign wr_count_next_wr_popping = wr_reserving ? wr_count : _T_35; // @[FIFO_new.scala 200:43:@124.4]
  assign _T_37 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@125.4]
  assign _T_38 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@126.4]
  assign wr_count_next_no_wr_popping = wr_reserving ? _T_38 : wr_count; // @[FIFO_new.scala 201:46:@127.4]
  assign rd_popping = _T_63 & io_rd_prdy; // @[FIFO_new.scala 374:47:@182.4]
  assign wr_count_next = rd_popping ? wr_count_next_wr_popping : wr_count_next_no_wr_popping; // @[FIFO_new.scala 202:32:@128.4]
  assign wr_count_next_no_wr_popping_is_full = wr_count_next_no_wr_popping == 3'h4; // @[FIFO_new.scala 204:80:@129.4]
  assign wr_count_next_is_full = rd_popping ? 1'h0 : wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 205:40:@130.4]
  assign _T_47 = wr_reserving ^ rd_popping; // @[FIFO_new.scala 212:27:@138.4]
  assign _GEN_0 = _T_47 ? wr_count_next : wr_count; // @[FIFO_new.scala 212:40:@139.4]
  assign _T_52 = _T_50 + 2'h1; // @[FIFO_new.scala 227:47:@143.4]
  assign wr_adr_next = _T_50 + 2'h1; // @[FIFO_new.scala 227:47:@144.4]
  assign _GEN_1 = wr_reserving ? wr_adr_next : _T_50; // @[FIFO_new.scala 228:29:@145.4]
  assign _T_57 = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@150.4]
  assign rd_adr_next_popping = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@151.4]
  assign _GEN_2 = rd_popping ? rd_adr_next_popping : rd_adr; // @[FIFO_new.scala 244:33:@152.4]
  assign _T_67 = rd_count - 3'h1; // @[FIFO_new.scala 349:74:@170.4]
  assign _T_68 = $unsigned(_T_67); // @[FIFO_new.scala 349:74:@171.4]
  assign _T_69 = _T_68[2:0]; // @[FIFO_new.scala 349:74:@172.4]
  assign rd_count_next_rd_popping = wr_reserving ? rd_count : _T_69; // @[FIFO_new.scala 349:43:@173.4]
  assign _T_71 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@174.4]
  assign _T_72 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@175.4]
  assign rd_count_next_no_rd_popping = wr_reserving ? _T_72 : rd_count; // @[FIFO_new.scala 350:46:@176.4]
  assign rd_count_next = rd_popping ? rd_count_next_rd_popping : rd_count_next_no_rd_popping; // @[FIFO_new.scala 351:32:@177.4]
  assign _T_73 = wr_reserving | rd_popping; // @[FIFO_new.scala 353:25:@178.4]
  assign _GEN_3 = _T_73 ? rd_count_next : rd_count; // @[FIFO_new.scala 353:39:@179.4]
  assign rd_count_p_next_rd_popping_not_0 = rd_count_next_rd_popping != 3'h0; // @[FIFO_new.scala 376:81:@184.4]
  assign rd_count_p_next_no_rd_popping_not_0 = rd_count_next_no_rd_popping != 3'h0; // @[FIFO_new.scala 377:87:@185.4]
  assign rd_count_p_next_not_0 = rd_popping ? rd_count_p_next_rd_popping_not_0 : rd_count_p_next_no_rd_popping_not_0; // @[FIFO_new.scala 378:48:@186.4]
  assign _GEN_4 = _T_73 ? rd_count_p_next_not_0 : rd_pvld_p; // @[FIFO_new.scala 380:47:@188.4]
  assign _GEN_5 = _T_73 ? rd_count_p_next_not_0 : _T_63; // @[FIFO_new.scala 380:47:@188.4]
  assign io_wr_prdy = ~ wr_busy_int; // @[FIFO_new.scala 192:20:@115.4]
  assign io_rd_pvld = rd_pvld_p; // @[FIFO_new.scala 386:28:@193.4]
  assign io_rd_pd = ram_io_dout; // @[FIFO_new.scala 385:26:@192.4]
  assign ram_reset = reset; // @[:@158.4]
  assign ram_io_clk = clock; // @[FIFO_new.scala 255:24:@159.4]
  assign ram_io_di = io_wr_pd; // @[FIFO_new.scala 261:23:@163.4]
  assign ram_io_we = io_wr_pvld & _T_26; // @[FIFO_new.scala 260:23:@162.4]
  assign ram_io_wa = _T_50; // @[FIFO_new.scala 258:31:@161.4]
  assign ram_io_ra = {{1'd0}, rd_adr}; // @[FIFO_new.scala 266:27:@164.4]
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
  wr_busy_int = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  wr_count = _RAND_1[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_63 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_50 = _RAND_3[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  rd_adr = _RAND_4[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  rd_pvld_p = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  rd_count = _RAND_6[2:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      wr_busy_int <= 1'h0;
    end else begin
      if (rd_popping) begin
        wr_busy_int <= 1'h0;
      end else begin
        wr_busy_int <= wr_count_next_no_wr_popping_is_full;
      end
    end
    if (reset) begin
      wr_count <= 3'h0;
    end else begin
      if (_T_47) begin
        if (rd_popping) begin
          if (!(wr_reserving)) begin
            wr_count <= _T_35;
          end
        end else begin
          if (wr_reserving) begin
            wr_count <= _T_38;
          end
        end
      end
    end
    if (reset) begin
      _T_63 <= 1'h0;
    end else begin
      if (_T_73) begin
        if (rd_popping) begin
          _T_63 <= rd_count_p_next_rd_popping_not_0;
        end else begin
          _T_63 <= rd_count_p_next_no_rd_popping_not_0;
        end
      end
    end
    if (reset) begin
      _T_50 <= 2'h0;
    end else begin
      if (wr_reserving) begin
        _T_50 <= wr_adr_next;
      end
    end
    if (reset) begin
      rd_adr <= 2'h0;
    end else begin
      if (rd_popping) begin
        rd_adr <= rd_adr_next_popping;
      end
    end
    if (reset) begin
      rd_pvld_p <= 1'h0;
    end else begin
      if (_T_73) begin
        if (rd_popping) begin
          rd_pvld_p <= rd_count_p_next_rd_popping_not_0;
        end else begin
          rd_pvld_p <= rd_count_p_next_no_rd_popping_not_0;
        end
      end
    end
    if (reset) begin
      rd_count <= 3'h0;
    end else begin
      if (_T_73) begin
        if (rd_popping) begin
          if (!(wr_reserving)) begin
            rd_count <= _T_69;
          end
        end else begin
          if (wr_reserving) begin
            rd_count <= _T_72;
          end
        end
      end
    end
  end
endmodule
module NV_NVDLA_BC_pipe( // @[:@1163.2]
  input         reset, // @[:@1165.4]
  input         io_clk, // @[:@1166.4]
  input         io_vi, // @[:@1166.4]
  output        io_ro, // @[:@1166.4]
  input  [64:0] io_di, // @[:@1166.4]
  output        io_vo, // @[:@1166.4]
  input         io_ri, // @[:@1166.4]
  output [64:0] io_dout // @[:@1166.4]
);
  reg  pipe_valid; // @[BC_pipe.scala 50:29:@1168.4]
  reg [31:0] _RAND_0;
  reg [64:0] pipe_data; // @[BC_pipe.scala 51:50:@1169.4]
  reg [95:0] _RAND_1;
  wire  _T_25; // @[BC_pipe.scala 54:22:@1171.4]
  wire  _T_26; // @[BC_pipe.scala 55:28:@1173.4]
  wire  _T_28; // @[BC_pipe.scala 56:28:@1176.4]
  wire [64:0] _T_29; // @[BC_pipe.scala 56:21:@1177.4]
  assign _T_25 = io_ro ? io_vi : 1'h1; // @[BC_pipe.scala 54:22:@1171.4]
  assign _T_26 = ~ pipe_valid; // @[BC_pipe.scala 55:28:@1173.4]
  assign _T_28 = io_ro & io_vi; // @[BC_pipe.scala 56:28:@1176.4]
  assign _T_29 = _T_28 ? io_di : pipe_data; // @[BC_pipe.scala 56:21:@1177.4]
  assign io_ro = io_ri | _T_26; // @[BC_pipe.scala 55:11:@1175.4]
  assign io_vo = pipe_valid; // @[BC_pipe.scala 60:11:@1180.4]
  assign io_dout = pipe_data; // @[BC_pipe.scala 61:13:@1181.4]
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
module NV_NVDLA_MCIF_READ_eg( // @[:@1303.2]
  input         clock, // @[:@1304.4]
  input         reset, // @[:@1305.4]
  input         io_nvdla_core_clk, // @[:@1306.4]
  input  [31:0] io_pwrbus_ram_pd, // @[:@1306.4]
  output        io_eg2ig_axi_vld, // @[:@1306.4]
  input         io_mcif2client_rd_rsp_pd_0_ready, // @[:@1306.4]
  output        io_mcif2client_rd_rsp_pd_0_valid, // @[:@1306.4]
  output [64:0] io_mcif2client_rd_rsp_pd_0_bits, // @[:@1306.4]
  input         io_mcif2client_rd_rsp_pd_1_ready, // @[:@1306.4]
  output        io_mcif2client_rd_rsp_pd_1_valid, // @[:@1306.4]
  output [64:0] io_mcif2client_rd_rsp_pd_1_bits, // @[:@1306.4]
  input         io_mcif2client_rd_rsp_pd_2_ready, // @[:@1306.4]
  output        io_mcif2client_rd_rsp_pd_2_valid, // @[:@1306.4]
  output [64:0] io_mcif2client_rd_rsp_pd_2_bits, // @[:@1306.4]
  input         io_mcif2client_rd_rsp_pd_3_ready, // @[:@1306.4]
  output        io_mcif2client_rd_rsp_pd_3_valid, // @[:@1306.4]
  output [64:0] io_mcif2client_rd_rsp_pd_3_bits, // @[:@1306.4]
  input         io_mcif2client_rd_rsp_pd_4_ready, // @[:@1306.4]
  output        io_mcif2client_rd_rsp_pd_4_valid, // @[:@1306.4]
  output [64:0] io_mcif2client_rd_rsp_pd_4_bits, // @[:@1306.4]
  input         io_mcif2client_rd_rsp_pd_5_ready, // @[:@1306.4]
  output        io_mcif2client_rd_rsp_pd_5_valid, // @[:@1306.4]
  output [64:0] io_mcif2client_rd_rsp_pd_5_bits, // @[:@1306.4]
  input         io_mcif2client_rd_rsp_pd_6_ready, // @[:@1306.4]
  output        io_mcif2client_rd_rsp_pd_6_valid, // @[:@1306.4]
  output [64:0] io_mcif2client_rd_rsp_pd_6_bits, // @[:@1306.4]
  output        io_noc2mcif_axi_r_ready, // @[:@1306.4]
  input         io_noc2mcif_axi_r_valid, // @[:@1306.4]
  input  [7:0]  io_noc2mcif_axi_r_bits_id, // @[:@1306.4]
  input         io_noc2mcif_axi_r_bits_last, // @[:@1306.4]
  input  [63:0] io_noc2mcif_axi_r_bits_data // @[:@1306.4]
);
  wire  pipe_pr_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 25:25:@1309.4]
  wire  pipe_pr_io_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 25:25:@1309.4]
  wire [67:0] pipe_pr_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 25:25:@1309.4]
  wire  pipe_pr_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 25:25:@1309.4]
  wire  pipe_pr_io_ri; // @[NV_NVDLA_MCIF_READ_eg.scala 25:25:@1309.4]
  wire [67:0] pipe_pr_io_di; // @[NV_NVDLA_MCIF_READ_eg.scala 25:25:@1309.4]
  wire  pipe_pr_io_vi; // @[NV_NVDLA_MCIF_READ_eg.scala 25:25:@1309.4]
  wire  pipe_pr_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 25:25:@1309.4]
  wire  NV_NVDLA_fifo_new_clock; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1360.4]
  wire  NV_NVDLA_fifo_new_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1360.4]
  wire  NV_NVDLA_fifo_new_io_wr_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1360.4]
  wire  NV_NVDLA_fifo_new_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1360.4]
  wire [63:0] NV_NVDLA_fifo_new_io_wr_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1360.4]
  wire  NV_NVDLA_fifo_new_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1360.4]
  wire  NV_NVDLA_fifo_new_io_rd_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1360.4]
  wire [63:0] NV_NVDLA_fifo_new_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1360.4]
  wire  NV_NVDLA_fifo_new_1_clock; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1363.4]
  wire  NV_NVDLA_fifo_new_1_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1363.4]
  wire  NV_NVDLA_fifo_new_1_io_wr_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1363.4]
  wire  NV_NVDLA_fifo_new_1_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1363.4]
  wire [63:0] NV_NVDLA_fifo_new_1_io_wr_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1363.4]
  wire  NV_NVDLA_fifo_new_1_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1363.4]
  wire  NV_NVDLA_fifo_new_1_io_rd_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1363.4]
  wire [63:0] NV_NVDLA_fifo_new_1_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1363.4]
  wire  NV_NVDLA_fifo_new_2_clock; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1366.4]
  wire  NV_NVDLA_fifo_new_2_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1366.4]
  wire  NV_NVDLA_fifo_new_2_io_wr_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1366.4]
  wire  NV_NVDLA_fifo_new_2_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1366.4]
  wire [63:0] NV_NVDLA_fifo_new_2_io_wr_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1366.4]
  wire  NV_NVDLA_fifo_new_2_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1366.4]
  wire  NV_NVDLA_fifo_new_2_io_rd_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1366.4]
  wire [63:0] NV_NVDLA_fifo_new_2_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1366.4]
  wire  NV_NVDLA_fifo_new_3_clock; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1369.4]
  wire  NV_NVDLA_fifo_new_3_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1369.4]
  wire  NV_NVDLA_fifo_new_3_io_wr_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1369.4]
  wire  NV_NVDLA_fifo_new_3_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1369.4]
  wire [63:0] NV_NVDLA_fifo_new_3_io_wr_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1369.4]
  wire  NV_NVDLA_fifo_new_3_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1369.4]
  wire  NV_NVDLA_fifo_new_3_io_rd_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1369.4]
  wire [63:0] NV_NVDLA_fifo_new_3_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1369.4]
  wire  NV_NVDLA_fifo_new_4_clock; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1372.4]
  wire  NV_NVDLA_fifo_new_4_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1372.4]
  wire  NV_NVDLA_fifo_new_4_io_wr_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1372.4]
  wire  NV_NVDLA_fifo_new_4_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1372.4]
  wire [63:0] NV_NVDLA_fifo_new_4_io_wr_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1372.4]
  wire  NV_NVDLA_fifo_new_4_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1372.4]
  wire  NV_NVDLA_fifo_new_4_io_rd_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1372.4]
  wire [63:0] NV_NVDLA_fifo_new_4_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1372.4]
  wire  NV_NVDLA_fifo_new_5_clock; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1375.4]
  wire  NV_NVDLA_fifo_new_5_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1375.4]
  wire  NV_NVDLA_fifo_new_5_io_wr_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1375.4]
  wire  NV_NVDLA_fifo_new_5_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1375.4]
  wire [63:0] NV_NVDLA_fifo_new_5_io_wr_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1375.4]
  wire  NV_NVDLA_fifo_new_5_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1375.4]
  wire  NV_NVDLA_fifo_new_5_io_rd_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1375.4]
  wire [63:0] NV_NVDLA_fifo_new_5_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1375.4]
  wire  NV_NVDLA_fifo_new_6_clock; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1378.4]
  wire  NV_NVDLA_fifo_new_6_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1378.4]
  wire  NV_NVDLA_fifo_new_6_io_wr_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1378.4]
  wire  NV_NVDLA_fifo_new_6_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1378.4]
  wire [63:0] NV_NVDLA_fifo_new_6_io_wr_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1378.4]
  wire  NV_NVDLA_fifo_new_6_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1378.4]
  wire  NV_NVDLA_fifo_new_6_io_rd_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1378.4]
  wire [63:0] NV_NVDLA_fifo_new_6_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1378.4]
  wire  NV_NVDLA_BC_pipe_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1440.4]
  wire  NV_NVDLA_BC_pipe_io_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1440.4]
  wire  NV_NVDLA_BC_pipe_io_vi; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1440.4]
  wire  NV_NVDLA_BC_pipe_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1440.4]
  wire [64:0] NV_NVDLA_BC_pipe_io_di; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1440.4]
  wire  NV_NVDLA_BC_pipe_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1440.4]
  wire  NV_NVDLA_BC_pipe_io_ri; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1440.4]
  wire [64:0] NV_NVDLA_BC_pipe_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1440.4]
  wire  NV_NVDLA_BC_pipe_1_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1443.4]
  wire  NV_NVDLA_BC_pipe_1_io_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1443.4]
  wire  NV_NVDLA_BC_pipe_1_io_vi; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1443.4]
  wire  NV_NVDLA_BC_pipe_1_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1443.4]
  wire [64:0] NV_NVDLA_BC_pipe_1_io_di; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1443.4]
  wire  NV_NVDLA_BC_pipe_1_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1443.4]
  wire  NV_NVDLA_BC_pipe_1_io_ri; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1443.4]
  wire [64:0] NV_NVDLA_BC_pipe_1_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1443.4]
  wire  NV_NVDLA_BC_pipe_2_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1446.4]
  wire  NV_NVDLA_BC_pipe_2_io_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1446.4]
  wire  NV_NVDLA_BC_pipe_2_io_vi; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1446.4]
  wire  NV_NVDLA_BC_pipe_2_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1446.4]
  wire [64:0] NV_NVDLA_BC_pipe_2_io_di; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1446.4]
  wire  NV_NVDLA_BC_pipe_2_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1446.4]
  wire  NV_NVDLA_BC_pipe_2_io_ri; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1446.4]
  wire [64:0] NV_NVDLA_BC_pipe_2_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1446.4]
  wire  NV_NVDLA_BC_pipe_3_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1449.4]
  wire  NV_NVDLA_BC_pipe_3_io_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1449.4]
  wire  NV_NVDLA_BC_pipe_3_io_vi; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1449.4]
  wire  NV_NVDLA_BC_pipe_3_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1449.4]
  wire [64:0] NV_NVDLA_BC_pipe_3_io_di; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1449.4]
  wire  NV_NVDLA_BC_pipe_3_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1449.4]
  wire  NV_NVDLA_BC_pipe_3_io_ri; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1449.4]
  wire [64:0] NV_NVDLA_BC_pipe_3_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1449.4]
  wire  NV_NVDLA_BC_pipe_4_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1452.4]
  wire  NV_NVDLA_BC_pipe_4_io_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1452.4]
  wire  NV_NVDLA_BC_pipe_4_io_vi; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1452.4]
  wire  NV_NVDLA_BC_pipe_4_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1452.4]
  wire [64:0] NV_NVDLA_BC_pipe_4_io_di; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1452.4]
  wire  NV_NVDLA_BC_pipe_4_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1452.4]
  wire  NV_NVDLA_BC_pipe_4_io_ri; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1452.4]
  wire [64:0] NV_NVDLA_BC_pipe_4_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1452.4]
  wire  NV_NVDLA_BC_pipe_5_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1455.4]
  wire  NV_NVDLA_BC_pipe_5_io_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1455.4]
  wire  NV_NVDLA_BC_pipe_5_io_vi; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1455.4]
  wire  NV_NVDLA_BC_pipe_5_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1455.4]
  wire [64:0] NV_NVDLA_BC_pipe_5_io_di; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1455.4]
  wire  NV_NVDLA_BC_pipe_5_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1455.4]
  wire  NV_NVDLA_BC_pipe_5_io_ri; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1455.4]
  wire [64:0] NV_NVDLA_BC_pipe_5_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1455.4]
  wire  NV_NVDLA_BC_pipe_6_reset; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1458.4]
  wire  NV_NVDLA_BC_pipe_6_io_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1458.4]
  wire  NV_NVDLA_BC_pipe_6_io_vi; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1458.4]
  wire  NV_NVDLA_BC_pipe_6_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1458.4]
  wire [64:0] NV_NVDLA_BC_pipe_6_io_di; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1458.4]
  wire  NV_NVDLA_BC_pipe_6_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1458.4]
  wire  NV_NVDLA_BC_pipe_6_io_ri; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1458.4]
  wire [64:0] NV_NVDLA_BC_pipe_6_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1458.4]
  wire [3:0] _T_84; // @[NV_NVDLA_MCIF_READ_eg.scala 29:51:@1315.4]
  wire  rq_wr_prdy_0; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1387.4]
  wire [3:0] ipipe_axi_axid; // @[NV_NVDLA_MCIF_READ_eg.scala 34:41:@1321.4]
  wire  _T_125; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1339.4]
  wire  rq_wr_pvld_0; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1340.4]
  wire  _T_111; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1325.4]
  wire  rq_wr_prdy_1; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1395.4]
  wire  _T_128; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1342.4]
  wire  rq_wr_pvld_1; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1343.4]
  wire  _T_112; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1326.4]
  wire  _T_118; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1332.4]
  wire  rq_wr_prdy_2; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1403.4]
  wire  _T_131; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1345.4]
  wire  rq_wr_pvld_2; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1346.4]
  wire  _T_113; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1327.4]
  wire  _T_119; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1333.4]
  wire  rq_wr_prdy_3; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1411.4]
  wire  _T_134; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1348.4]
  wire  rq_wr_pvld_3; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1349.4]
  wire  _T_114; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1328.4]
  wire  _T_120; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1334.4]
  wire  rq_wr_prdy_4; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1419.4]
  wire  _T_137; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1351.4]
  wire  rq_wr_pvld_4; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1352.4]
  wire  _T_115; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1329.4]
  wire  _T_121; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1335.4]
  wire  rq_wr_prdy_5; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1427.4]
  wire  _T_140; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1354.4]
  wire  rq_wr_pvld_5; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1355.4]
  wire  _T_116; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1330.4]
  wire  _T_122; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1336.4]
  wire  rq_wr_prdy_6; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1435.4]
  wire  _T_143; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1357.4]
  wire  rq_wr_pvld_6; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1358.4]
  wire  _T_117; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1331.4]
  wire  ipipe_axi_rdy; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1337.4]
  wire  rq_rd_pvld_0; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1389.4]
  wire [63:0] rq_rd_pd_0; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1391.4]
  wire  rq_rd_pvld_1; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1397.4]
  wire [63:0] rq_rd_pd_1; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1399.4]
  wire  rq_rd_pvld_2; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1405.4]
  wire [63:0] rq_rd_pd_2; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1407.4]
  wire  rq_rd_pvld_3; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1413.4]
  wire [63:0] rq_rd_pd_3; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1415.4]
  wire  rq_rd_pvld_4; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1421.4]
  wire [63:0] rq_rd_pd_4; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1423.4]
  wire  rq_rd_pvld_5; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1429.4]
  wire [63:0] rq_rd_pd_5; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1431.4]
  wire  rq_rd_pvld_6; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1437.4]
  wire [63:0] rq_rd_pd_6; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1439.4]
  NV_NVDLA_IS_pipe pipe_pr ( // @[NV_NVDLA_MCIF_READ_eg.scala 25:25:@1309.4]
    .reset(pipe_pr_reset),
    .io_clk(pipe_pr_io_clk),
    .io_dout(pipe_pr_io_dout),
    .io_vo(pipe_pr_io_vo),
    .io_ri(pipe_pr_io_ri),
    .io_di(pipe_pr_io_di),
    .io_vi(pipe_pr_io_vi),
    .io_ro(pipe_pr_io_ro)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new ( // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1360.4]
    .clock(NV_NVDLA_fifo_new_clock),
    .reset(NV_NVDLA_fifo_new_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_io_wr_pd),
    .io_rd_pvld(NV_NVDLA_fifo_new_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_io_rd_pd)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new_1 ( // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1363.4]
    .clock(NV_NVDLA_fifo_new_1_clock),
    .reset(NV_NVDLA_fifo_new_1_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_1_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_1_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_1_io_wr_pd),
    .io_rd_pvld(NV_NVDLA_fifo_new_1_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_1_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_1_io_rd_pd)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new_2 ( // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1366.4]
    .clock(NV_NVDLA_fifo_new_2_clock),
    .reset(NV_NVDLA_fifo_new_2_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_2_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_2_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_2_io_wr_pd),
    .io_rd_pvld(NV_NVDLA_fifo_new_2_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_2_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_2_io_rd_pd)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new_3 ( // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1369.4]
    .clock(NV_NVDLA_fifo_new_3_clock),
    .reset(NV_NVDLA_fifo_new_3_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_3_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_3_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_3_io_wr_pd),
    .io_rd_pvld(NV_NVDLA_fifo_new_3_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_3_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_3_io_rd_pd)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new_4 ( // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1372.4]
    .clock(NV_NVDLA_fifo_new_4_clock),
    .reset(NV_NVDLA_fifo_new_4_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_4_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_4_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_4_io_wr_pd),
    .io_rd_pvld(NV_NVDLA_fifo_new_4_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_4_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_4_io_rd_pd)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new_5 ( // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1375.4]
    .clock(NV_NVDLA_fifo_new_5_clock),
    .reset(NV_NVDLA_fifo_new_5_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_5_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_5_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_5_io_wr_pd),
    .io_rd_pvld(NV_NVDLA_fifo_new_5_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_5_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_5_io_rd_pd)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new_6 ( // @[NV_NVDLA_MCIF_READ_eg.scala 45:52:@1378.4]
    .clock(NV_NVDLA_fifo_new_6_clock),
    .reset(NV_NVDLA_fifo_new_6_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_6_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_6_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_6_io_wr_pd),
    .io_rd_pvld(NV_NVDLA_fifo_new_6_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_6_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_6_io_rd_pd)
  );
  NV_NVDLA_BC_pipe NV_NVDLA_BC_pipe ( // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1440.4]
    .reset(NV_NVDLA_BC_pipe_reset),
    .io_clk(NV_NVDLA_BC_pipe_io_clk),
    .io_vi(NV_NVDLA_BC_pipe_io_vi),
    .io_ro(NV_NVDLA_BC_pipe_io_ro),
    .io_di(NV_NVDLA_BC_pipe_io_di),
    .io_vo(NV_NVDLA_BC_pipe_io_vo),
    .io_ri(NV_NVDLA_BC_pipe_io_ri),
    .io_dout(NV_NVDLA_BC_pipe_io_dout)
  );
  NV_NVDLA_BC_pipe NV_NVDLA_BC_pipe_1 ( // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1443.4]
    .reset(NV_NVDLA_BC_pipe_1_reset),
    .io_clk(NV_NVDLA_BC_pipe_1_io_clk),
    .io_vi(NV_NVDLA_BC_pipe_1_io_vi),
    .io_ro(NV_NVDLA_BC_pipe_1_io_ro),
    .io_di(NV_NVDLA_BC_pipe_1_io_di),
    .io_vo(NV_NVDLA_BC_pipe_1_io_vo),
    .io_ri(NV_NVDLA_BC_pipe_1_io_ri),
    .io_dout(NV_NVDLA_BC_pipe_1_io_dout)
  );
  NV_NVDLA_BC_pipe NV_NVDLA_BC_pipe_2 ( // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1446.4]
    .reset(NV_NVDLA_BC_pipe_2_reset),
    .io_clk(NV_NVDLA_BC_pipe_2_io_clk),
    .io_vi(NV_NVDLA_BC_pipe_2_io_vi),
    .io_ro(NV_NVDLA_BC_pipe_2_io_ro),
    .io_di(NV_NVDLA_BC_pipe_2_io_di),
    .io_vo(NV_NVDLA_BC_pipe_2_io_vo),
    .io_ri(NV_NVDLA_BC_pipe_2_io_ri),
    .io_dout(NV_NVDLA_BC_pipe_2_io_dout)
  );
  NV_NVDLA_BC_pipe NV_NVDLA_BC_pipe_3 ( // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1449.4]
    .reset(NV_NVDLA_BC_pipe_3_reset),
    .io_clk(NV_NVDLA_BC_pipe_3_io_clk),
    .io_vi(NV_NVDLA_BC_pipe_3_io_vi),
    .io_ro(NV_NVDLA_BC_pipe_3_io_ro),
    .io_di(NV_NVDLA_BC_pipe_3_io_di),
    .io_vo(NV_NVDLA_BC_pipe_3_io_vo),
    .io_ri(NV_NVDLA_BC_pipe_3_io_ri),
    .io_dout(NV_NVDLA_BC_pipe_3_io_dout)
  );
  NV_NVDLA_BC_pipe NV_NVDLA_BC_pipe_4 ( // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1452.4]
    .reset(NV_NVDLA_BC_pipe_4_reset),
    .io_clk(NV_NVDLA_BC_pipe_4_io_clk),
    .io_vi(NV_NVDLA_BC_pipe_4_io_vi),
    .io_ro(NV_NVDLA_BC_pipe_4_io_ro),
    .io_di(NV_NVDLA_BC_pipe_4_io_di),
    .io_vo(NV_NVDLA_BC_pipe_4_io_vo),
    .io_ri(NV_NVDLA_BC_pipe_4_io_ri),
    .io_dout(NV_NVDLA_BC_pipe_4_io_dout)
  );
  NV_NVDLA_BC_pipe NV_NVDLA_BC_pipe_5 ( // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1455.4]
    .reset(NV_NVDLA_BC_pipe_5_reset),
    .io_clk(NV_NVDLA_BC_pipe_5_io_clk),
    .io_vi(NV_NVDLA_BC_pipe_5_io_vi),
    .io_ro(NV_NVDLA_BC_pipe_5_io_ro),
    .io_di(NV_NVDLA_BC_pipe_5_io_di),
    .io_vo(NV_NVDLA_BC_pipe_5_io_vo),
    .io_ri(NV_NVDLA_BC_pipe_5_io_ri),
    .io_dout(NV_NVDLA_BC_pipe_5_io_dout)
  );
  NV_NVDLA_BC_pipe NV_NVDLA_BC_pipe_6 ( // @[NV_NVDLA_MCIF_READ_eg.scala 64:52:@1458.4]
    .reset(NV_NVDLA_BC_pipe_6_reset),
    .io_clk(NV_NVDLA_BC_pipe_6_io_clk),
    .io_vi(NV_NVDLA_BC_pipe_6_io_vi),
    .io_ro(NV_NVDLA_BC_pipe_6_io_ro),
    .io_di(NV_NVDLA_BC_pipe_6_io_di),
    .io_vo(NV_NVDLA_BC_pipe_6_io_vo),
    .io_ri(NV_NVDLA_BC_pipe_6_io_ri),
    .io_dout(NV_NVDLA_BC_pipe_6_io_dout)
  );
  assign _T_84 = io_noc2mcif_axi_r_bits_id[3:0]; // @[NV_NVDLA_MCIF_READ_eg.scala 29:51:@1315.4]
  assign rq_wr_prdy_0 = NV_NVDLA_fifo_new_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1387.4]
  assign ipipe_axi_axid = pipe_pr_io_dout[67:64]; // @[NV_NVDLA_MCIF_READ_eg.scala 34:41:@1321.4]
  assign _T_125 = ipipe_axi_axid == 4'h8; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1339.4]
  assign rq_wr_pvld_0 = pipe_pr_io_vo & _T_125; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1340.4]
  assign _T_111 = rq_wr_prdy_0 & rq_wr_pvld_0; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1325.4]
  assign rq_wr_prdy_1 = NV_NVDLA_fifo_new_1_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1395.4]
  assign _T_128 = ipipe_axi_axid == 4'h9; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1342.4]
  assign rq_wr_pvld_1 = pipe_pr_io_vo & _T_128; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1343.4]
  assign _T_112 = rq_wr_prdy_1 & rq_wr_pvld_1; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1326.4]
  assign _T_118 = _T_111 | _T_112; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1332.4]
  assign rq_wr_prdy_2 = NV_NVDLA_fifo_new_2_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1403.4]
  assign _T_131 = ipipe_axi_axid == 4'h1; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1345.4]
  assign rq_wr_pvld_2 = pipe_pr_io_vo & _T_131; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1346.4]
  assign _T_113 = rq_wr_prdy_2 & rq_wr_pvld_2; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1327.4]
  assign _T_119 = _T_118 | _T_113; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1333.4]
  assign rq_wr_prdy_3 = NV_NVDLA_fifo_new_3_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1411.4]
  assign _T_134 = ipipe_axi_axid == 4'h5; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1348.4]
  assign rq_wr_pvld_3 = pipe_pr_io_vo & _T_134; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1349.4]
  assign _T_114 = rq_wr_prdy_3 & rq_wr_pvld_3; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1328.4]
  assign _T_120 = _T_119 | _T_114; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1334.4]
  assign rq_wr_prdy_4 = NV_NVDLA_fifo_new_4_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1419.4]
  assign _T_137 = ipipe_axi_axid == 4'h6; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1351.4]
  assign rq_wr_pvld_4 = pipe_pr_io_vo & _T_137; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1352.4]
  assign _T_115 = rq_wr_prdy_4 & rq_wr_pvld_4; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1329.4]
  assign _T_121 = _T_120 | _T_115; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1335.4]
  assign rq_wr_prdy_5 = NV_NVDLA_fifo_new_5_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1427.4]
  assign _T_140 = ipipe_axi_axid == 4'h2; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1354.4]
  assign rq_wr_pvld_5 = pipe_pr_io_vo & _T_140; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1355.4]
  assign _T_116 = rq_wr_prdy_5 & rq_wr_pvld_5; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1330.4]
  assign _T_122 = _T_121 | _T_116; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1336.4]
  assign rq_wr_prdy_6 = NV_NVDLA_fifo_new_6_io_wr_prdy; // @[NV_NVDLA_MCIF_READ_eg.scala 37:26:@1323.4 NV_NVDLA_MCIF_READ_eg.scala 55:23:@1435.4]
  assign _T_143 = ipipe_axi_axid == 4'h3; // @[NV_NVDLA_MCIF_READ_eg.scala 42:58:@1357.4]
  assign rq_wr_pvld_6 = pipe_pr_io_vo & _T_143; // @[NV_NVDLA_MCIF_READ_eg.scala 42:40:@1358.4]
  assign _T_117 = rq_wr_prdy_6 & rq_wr_pvld_6; // @[NV_NVDLA_MCIF_READ_eg.scala 39:73:@1331.4]
  assign ipipe_axi_rdy = _T_122 | _T_117; // @[NV_NVDLA_MCIF_READ_eg.scala 39:88:@1337.4]
  assign rq_rd_pvld_0 = NV_NVDLA_fifo_new_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1389.4]
  assign rq_rd_pd_0 = NV_NVDLA_fifo_new_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1391.4]
  assign rq_rd_pvld_1 = NV_NVDLA_fifo_new_1_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1397.4]
  assign rq_rd_pd_1 = NV_NVDLA_fifo_new_1_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1399.4]
  assign rq_rd_pvld_2 = NV_NVDLA_fifo_new_2_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1405.4]
  assign rq_rd_pd_2 = NV_NVDLA_fifo_new_2_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1407.4]
  assign rq_rd_pvld_3 = NV_NVDLA_fifo_new_3_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1413.4]
  assign rq_rd_pd_3 = NV_NVDLA_fifo_new_3_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1415.4]
  assign rq_rd_pvld_4 = NV_NVDLA_fifo_new_4_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1421.4]
  assign rq_rd_pd_4 = NV_NVDLA_fifo_new_4_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1423.4]
  assign rq_rd_pvld_5 = NV_NVDLA_fifo_new_5_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1429.4]
  assign rq_rd_pd_5 = NV_NVDLA_fifo_new_5_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1431.4]
  assign rq_rd_pvld_6 = NV_NVDLA_fifo_new_6_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 48:26:@1382.4 NV_NVDLA_MCIF_READ_eg.scala 58:23:@1437.4]
  assign rq_rd_pd_6 = NV_NVDLA_fifo_new_6_io_rd_pd; // @[NV_NVDLA_MCIF_READ_eg.scala 49:26:@1383.4 NV_NVDLA_MCIF_READ_eg.scala 60:21:@1439.4]
  assign io_eg2ig_axi_vld = pipe_pr_io_vo & ipipe_axi_rdy; // @[NV_NVDLA_MCIF_READ_eg.scala 33:22:@1320.4]
  assign io_mcif2client_rd_rsp_pd_0_valid = NV_NVDLA_BC_pipe_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 72:43:@1466.4]
  assign io_mcif2client_rd_rsp_pd_0_bits = NV_NVDLA_BC_pipe_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 74:42:@1468.4]
  assign io_mcif2client_rd_rsp_pd_1_valid = NV_NVDLA_BC_pipe_1_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 72:43:@1474.4]
  assign io_mcif2client_rd_rsp_pd_1_bits = NV_NVDLA_BC_pipe_1_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 74:42:@1476.4]
  assign io_mcif2client_rd_rsp_pd_2_valid = NV_NVDLA_BC_pipe_2_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 72:43:@1482.4]
  assign io_mcif2client_rd_rsp_pd_2_bits = NV_NVDLA_BC_pipe_2_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 74:42:@1484.4]
  assign io_mcif2client_rd_rsp_pd_3_valid = NV_NVDLA_BC_pipe_3_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 72:43:@1490.4]
  assign io_mcif2client_rd_rsp_pd_3_bits = NV_NVDLA_BC_pipe_3_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 74:42:@1492.4]
  assign io_mcif2client_rd_rsp_pd_4_valid = NV_NVDLA_BC_pipe_4_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 72:43:@1498.4]
  assign io_mcif2client_rd_rsp_pd_4_bits = NV_NVDLA_BC_pipe_4_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 74:42:@1500.4]
  assign io_mcif2client_rd_rsp_pd_5_valid = NV_NVDLA_BC_pipe_5_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 72:43:@1506.4]
  assign io_mcif2client_rd_rsp_pd_5_bits = NV_NVDLA_BC_pipe_5_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 74:42:@1508.4]
  assign io_mcif2client_rd_rsp_pd_6_valid = NV_NVDLA_BC_pipe_6_io_vo; // @[NV_NVDLA_MCIF_READ_eg.scala 72:43:@1514.4]
  assign io_mcif2client_rd_rsp_pd_6_bits = NV_NVDLA_BC_pipe_6_io_dout; // @[NV_NVDLA_MCIF_READ_eg.scala 74:42:@1516.4]
  assign io_noc2mcif_axi_r_ready = pipe_pr_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 28:29:@1314.4]
  assign pipe_pr_reset = reset; // @[:@1311.4]
  assign pipe_pr_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 26:20:@1312.4]
  assign pipe_pr_io_ri = _T_122 | _T_117; // @[NV_NVDLA_MCIF_READ_eg.scala 31:19:@1318.4]
  assign pipe_pr_io_di = {_T_84,io_noc2mcif_axi_r_bits_data}; // @[NV_NVDLA_MCIF_READ_eg.scala 29:19:@1317.4]
  assign pipe_pr_io_vi = io_noc2mcif_axi_r_valid; // @[NV_NVDLA_MCIF_READ_eg.scala 27:19:@1313.4]
  assign NV_NVDLA_fifo_new_clock = io_nvdla_core_clk; // @[:@1361.4]
  assign NV_NVDLA_fifo_new_reset = reset; // @[:@1362.4]
  assign NV_NVDLA_fifo_new_io_wr_pvld = pipe_pr_io_vo & _T_125; // @[NV_NVDLA_MCIF_READ_eg.scala 54:32:@1386.4]
  assign NV_NVDLA_fifo_new_io_wr_pd = pipe_pr_io_dout[63:0]; // @[NV_NVDLA_MCIF_READ_eg.scala 56:30:@1388.4]
  assign NV_NVDLA_fifo_new_io_rd_prdy = NV_NVDLA_BC_pipe_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 59:32:@1390.4]
  assign NV_NVDLA_fifo_new_1_clock = io_nvdla_core_clk; // @[:@1364.4]
  assign NV_NVDLA_fifo_new_1_reset = reset; // @[:@1365.4]
  assign NV_NVDLA_fifo_new_1_io_wr_pvld = pipe_pr_io_vo & _T_128; // @[NV_NVDLA_MCIF_READ_eg.scala 54:32:@1394.4]
  assign NV_NVDLA_fifo_new_1_io_wr_pd = pipe_pr_io_dout[63:0]; // @[NV_NVDLA_MCIF_READ_eg.scala 56:30:@1396.4]
  assign NV_NVDLA_fifo_new_1_io_rd_prdy = NV_NVDLA_BC_pipe_1_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 59:32:@1398.4]
  assign NV_NVDLA_fifo_new_2_clock = io_nvdla_core_clk; // @[:@1367.4]
  assign NV_NVDLA_fifo_new_2_reset = reset; // @[:@1368.4]
  assign NV_NVDLA_fifo_new_2_io_wr_pvld = pipe_pr_io_vo & _T_131; // @[NV_NVDLA_MCIF_READ_eg.scala 54:32:@1402.4]
  assign NV_NVDLA_fifo_new_2_io_wr_pd = pipe_pr_io_dout[63:0]; // @[NV_NVDLA_MCIF_READ_eg.scala 56:30:@1404.4]
  assign NV_NVDLA_fifo_new_2_io_rd_prdy = NV_NVDLA_BC_pipe_2_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 59:32:@1406.4]
  assign NV_NVDLA_fifo_new_3_clock = io_nvdla_core_clk; // @[:@1370.4]
  assign NV_NVDLA_fifo_new_3_reset = reset; // @[:@1371.4]
  assign NV_NVDLA_fifo_new_3_io_wr_pvld = pipe_pr_io_vo & _T_134; // @[NV_NVDLA_MCIF_READ_eg.scala 54:32:@1410.4]
  assign NV_NVDLA_fifo_new_3_io_wr_pd = pipe_pr_io_dout[63:0]; // @[NV_NVDLA_MCIF_READ_eg.scala 56:30:@1412.4]
  assign NV_NVDLA_fifo_new_3_io_rd_prdy = NV_NVDLA_BC_pipe_3_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 59:32:@1414.4]
  assign NV_NVDLA_fifo_new_4_clock = io_nvdla_core_clk; // @[:@1373.4]
  assign NV_NVDLA_fifo_new_4_reset = reset; // @[:@1374.4]
  assign NV_NVDLA_fifo_new_4_io_wr_pvld = pipe_pr_io_vo & _T_137; // @[NV_NVDLA_MCIF_READ_eg.scala 54:32:@1418.4]
  assign NV_NVDLA_fifo_new_4_io_wr_pd = pipe_pr_io_dout[63:0]; // @[NV_NVDLA_MCIF_READ_eg.scala 56:30:@1420.4]
  assign NV_NVDLA_fifo_new_4_io_rd_prdy = NV_NVDLA_BC_pipe_4_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 59:32:@1422.4]
  assign NV_NVDLA_fifo_new_5_clock = io_nvdla_core_clk; // @[:@1376.4]
  assign NV_NVDLA_fifo_new_5_reset = reset; // @[:@1377.4]
  assign NV_NVDLA_fifo_new_5_io_wr_pvld = pipe_pr_io_vo & _T_140; // @[NV_NVDLA_MCIF_READ_eg.scala 54:32:@1426.4]
  assign NV_NVDLA_fifo_new_5_io_wr_pd = pipe_pr_io_dout[63:0]; // @[NV_NVDLA_MCIF_READ_eg.scala 56:30:@1428.4]
  assign NV_NVDLA_fifo_new_5_io_rd_prdy = NV_NVDLA_BC_pipe_5_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 59:32:@1430.4]
  assign NV_NVDLA_fifo_new_6_clock = io_nvdla_core_clk; // @[:@1379.4]
  assign NV_NVDLA_fifo_new_6_reset = reset; // @[:@1380.4]
  assign NV_NVDLA_fifo_new_6_io_wr_pvld = pipe_pr_io_vo & _T_143; // @[NV_NVDLA_MCIF_READ_eg.scala 54:32:@1434.4]
  assign NV_NVDLA_fifo_new_6_io_wr_pd = pipe_pr_io_dout[63:0]; // @[NV_NVDLA_MCIF_READ_eg.scala 56:30:@1436.4]
  assign NV_NVDLA_fifo_new_6_io_rd_prdy = NV_NVDLA_BC_pipe_6_io_ro; // @[NV_NVDLA_MCIF_READ_eg.scala 59:32:@1438.4]
  assign NV_NVDLA_BC_pipe_reset = reset; // @[:@1442.4]
  assign NV_NVDLA_BC_pipe_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 66:28:@1461.4]
  assign NV_NVDLA_BC_pipe_io_vi = NV_NVDLA_fifo_new_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 68:27:@1462.4]
  assign NV_NVDLA_BC_pipe_io_di = {rq_rd_pvld_0,rq_rd_pd_0}; // @[NV_NVDLA_MCIF_READ_eg.scala 70:27:@1465.4]
  assign NV_NVDLA_BC_pipe_io_ri = io_mcif2client_rd_rsp_pd_0_ready; // @[NV_NVDLA_MCIF_READ_eg.scala 73:27:@1467.4]
  assign NV_NVDLA_BC_pipe_1_reset = reset; // @[:@1445.4]
  assign NV_NVDLA_BC_pipe_1_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 66:28:@1469.4]
  assign NV_NVDLA_BC_pipe_1_io_vi = NV_NVDLA_fifo_new_1_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 68:27:@1470.4]
  assign NV_NVDLA_BC_pipe_1_io_di = {rq_rd_pvld_1,rq_rd_pd_1}; // @[NV_NVDLA_MCIF_READ_eg.scala 70:27:@1473.4]
  assign NV_NVDLA_BC_pipe_1_io_ri = io_mcif2client_rd_rsp_pd_1_ready; // @[NV_NVDLA_MCIF_READ_eg.scala 73:27:@1475.4]
  assign NV_NVDLA_BC_pipe_2_reset = reset; // @[:@1448.4]
  assign NV_NVDLA_BC_pipe_2_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 66:28:@1477.4]
  assign NV_NVDLA_BC_pipe_2_io_vi = NV_NVDLA_fifo_new_2_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 68:27:@1478.4]
  assign NV_NVDLA_BC_pipe_2_io_di = {rq_rd_pvld_2,rq_rd_pd_2}; // @[NV_NVDLA_MCIF_READ_eg.scala 70:27:@1481.4]
  assign NV_NVDLA_BC_pipe_2_io_ri = io_mcif2client_rd_rsp_pd_2_ready; // @[NV_NVDLA_MCIF_READ_eg.scala 73:27:@1483.4]
  assign NV_NVDLA_BC_pipe_3_reset = reset; // @[:@1451.4]
  assign NV_NVDLA_BC_pipe_3_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 66:28:@1485.4]
  assign NV_NVDLA_BC_pipe_3_io_vi = NV_NVDLA_fifo_new_3_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 68:27:@1486.4]
  assign NV_NVDLA_BC_pipe_3_io_di = {rq_rd_pvld_3,rq_rd_pd_3}; // @[NV_NVDLA_MCIF_READ_eg.scala 70:27:@1489.4]
  assign NV_NVDLA_BC_pipe_3_io_ri = io_mcif2client_rd_rsp_pd_3_ready; // @[NV_NVDLA_MCIF_READ_eg.scala 73:27:@1491.4]
  assign NV_NVDLA_BC_pipe_4_reset = reset; // @[:@1454.4]
  assign NV_NVDLA_BC_pipe_4_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 66:28:@1493.4]
  assign NV_NVDLA_BC_pipe_4_io_vi = NV_NVDLA_fifo_new_4_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 68:27:@1494.4]
  assign NV_NVDLA_BC_pipe_4_io_di = {rq_rd_pvld_4,rq_rd_pd_4}; // @[NV_NVDLA_MCIF_READ_eg.scala 70:27:@1497.4]
  assign NV_NVDLA_BC_pipe_4_io_ri = io_mcif2client_rd_rsp_pd_4_ready; // @[NV_NVDLA_MCIF_READ_eg.scala 73:27:@1499.4]
  assign NV_NVDLA_BC_pipe_5_reset = reset; // @[:@1457.4]
  assign NV_NVDLA_BC_pipe_5_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 66:28:@1501.4]
  assign NV_NVDLA_BC_pipe_5_io_vi = NV_NVDLA_fifo_new_5_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 68:27:@1502.4]
  assign NV_NVDLA_BC_pipe_5_io_di = {rq_rd_pvld_5,rq_rd_pd_5}; // @[NV_NVDLA_MCIF_READ_eg.scala 70:27:@1505.4]
  assign NV_NVDLA_BC_pipe_5_io_ri = io_mcif2client_rd_rsp_pd_5_ready; // @[NV_NVDLA_MCIF_READ_eg.scala 73:27:@1507.4]
  assign NV_NVDLA_BC_pipe_6_reset = reset; // @[:@1460.4]
  assign NV_NVDLA_BC_pipe_6_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_READ_eg.scala 66:28:@1509.4]
  assign NV_NVDLA_BC_pipe_6_io_vi = NV_NVDLA_fifo_new_6_io_rd_pvld; // @[NV_NVDLA_MCIF_READ_eg.scala 68:27:@1510.4]
  assign NV_NVDLA_BC_pipe_6_io_di = {rq_rd_pvld_6,rq_rd_pd_6}; // @[NV_NVDLA_MCIF_READ_eg.scala 70:27:@1513.4]
  assign NV_NVDLA_BC_pipe_6_io_ri = io_mcif2client_rd_rsp_pd_6_ready; // @[NV_NVDLA_MCIF_READ_eg.scala 73:27:@1515.4]
endmodule
