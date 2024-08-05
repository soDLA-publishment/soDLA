module nv_flopram( // @[:@10.2]
  input         reset, // @[:@12.4]
  input         io_clk, // @[:@13.4]
  input         io_clk_mgated, // @[:@13.4]
  input  [32:0] io_di, // @[:@13.4]
  input         io_iwe, // @[:@13.4]
  input         io_we, // @[:@13.4]
  input  [1:0]  io_wa, // @[:@13.4]
  input  [2:0]  io_ra, // @[:@13.4]
  output [32:0] io_dout // @[:@13.4]
);
  reg [32:0] di_d; // @[Reg.scala 19:20:@15.4]
  reg [63:0] _RAND_0;
  wire [32:0] _GEN_0; // @[Reg.scala 20:19:@16.4]
  reg [32:0] _T_28; // @[nv_flopram.scala 68:61:@21.4]
  reg [63:0] _RAND_1;
  reg [32:0] _T_31; // @[nv_flopram.scala 68:61:@22.4]
  reg [63:0] _RAND_2;
  reg [32:0] _T_34; // @[nv_flopram.scala 68:61:@23.4]
  reg [63:0] _RAND_3;
  reg [32:0] _T_37; // @[nv_flopram.scala 68:61:@24.4]
  reg [63:0] _RAND_4;
  wire  _T_41; // @[nv_flopram.scala 73:32:@27.6]
  wire [32:0] _GEN_1; // @[nv_flopram.scala 73:40:@28.6]
  wire  _T_43; // @[nv_flopram.scala 73:32:@31.6]
  wire [32:0] _GEN_2; // @[nv_flopram.scala 73:40:@32.6]
  wire  _T_45; // @[nv_flopram.scala 73:32:@35.6]
  wire [32:0] _GEN_3; // @[nv_flopram.scala 73:40:@36.6]
  wire  _T_47; // @[nv_flopram.scala 73:32:@39.6]
  wire [32:0] _GEN_4; // @[nv_flopram.scala 73:40:@40.6]
  wire [32:0] _GEN_5; // @[nv_flopram.scala 70:16:@26.4]
  wire [32:0] _GEN_6; // @[nv_flopram.scala 70:16:@26.4]
  wire [32:0] _GEN_7; // @[nv_flopram.scala 70:16:@26.4]
  wire [32:0] _GEN_8; // @[nv_flopram.scala 70:16:@26.4]
  wire  _T_54; // @[Mux.scala 46:19:@45.4]
  wire [32:0] _T_55; // @[Mux.scala 46:16:@46.4]
  wire  _T_56; // @[Mux.scala 46:19:@47.4]
  wire [32:0] _T_57; // @[Mux.scala 46:16:@48.4]
  wire  _T_58; // @[Mux.scala 46:19:@49.4]
  wire [32:0] _T_59; // @[Mux.scala 46:16:@50.4]
  wire  _T_60; // @[Mux.scala 46:19:@51.4]
  wire [32:0] _T_61; // @[Mux.scala 46:16:@52.4]
  wire  _T_62; // @[Mux.scala 46:19:@53.4]
  assign _GEN_0 = io_iwe ? io_di : di_d; // @[Reg.scala 20:19:@16.4]
  assign _T_41 = io_wa == 2'h0; // @[nv_flopram.scala 73:32:@27.6]
  assign _GEN_1 = _T_41 ? di_d : _T_28; // @[nv_flopram.scala 73:40:@28.6]
  assign _T_43 = io_wa == 2'h1; // @[nv_flopram.scala 73:32:@31.6]
  assign _GEN_2 = _T_43 ? di_d : _T_31; // @[nv_flopram.scala 73:40:@32.6]
  assign _T_45 = io_wa == 2'h2; // @[nv_flopram.scala 73:32:@35.6]
  assign _GEN_3 = _T_45 ? di_d : _T_34; // @[nv_flopram.scala 73:40:@36.6]
  assign _T_47 = io_wa == 2'h3; // @[nv_flopram.scala 73:32:@39.6]
  assign _GEN_4 = _T_47 ? di_d : _T_37; // @[nv_flopram.scala 73:40:@40.6]
  assign _GEN_5 = io_we ? _GEN_1 : _T_28; // @[nv_flopram.scala 70:16:@26.4]
  assign _GEN_6 = io_we ? _GEN_2 : _T_31; // @[nv_flopram.scala 70:16:@26.4]
  assign _GEN_7 = io_we ? _GEN_3 : _T_34; // @[nv_flopram.scala 70:16:@26.4]
  assign _GEN_8 = io_we ? _GEN_4 : _T_37; // @[nv_flopram.scala 70:16:@26.4]
  assign _T_54 = 3'h4 == io_ra; // @[Mux.scala 46:19:@45.4]
  assign _T_55 = _T_54 ? io_di : 33'h0; // @[Mux.scala 46:16:@46.4]
  assign _T_56 = 3'h3 == io_ra; // @[Mux.scala 46:19:@47.4]
  assign _T_57 = _T_56 ? _T_37 : _T_55; // @[Mux.scala 46:16:@48.4]
  assign _T_58 = 3'h2 == io_ra; // @[Mux.scala 46:19:@49.4]
  assign _T_59 = _T_58 ? _T_34 : _T_57; // @[Mux.scala 46:16:@50.4]
  assign _T_60 = 3'h1 == io_ra; // @[Mux.scala 46:19:@51.4]
  assign _T_61 = _T_60 ? _T_31 : _T_59; // @[Mux.scala 46:16:@52.4]
  assign _T_62 = 3'h0 == io_ra; // @[Mux.scala 46:19:@53.4]
  assign io_dout = _T_62 ? _T_28 : _T_61; // @[nv_flopram.scala 83:13:@55.4]
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
  di_d = _RAND_0[32:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {2{`RANDOM}};
  _T_28 = _RAND_1[32:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_31 = _RAND_2[32:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {2{`RANDOM}};
  _T_34 = _RAND_3[32:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_37 = _RAND_4[32:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      di_d <= 33'h0;
    end else begin
      if (io_iwe) begin
        di_d <= io_di;
      end
    end
  end
  always @(posedge io_clk_mgated) begin
    if (reset) begin
      _T_28 <= 33'h0;
    end else begin
      if (io_we) begin
        if (_T_41) begin
          _T_28 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_31 <= 33'h0;
    end else begin
      if (io_we) begin
        if (_T_43) begin
          _T_31 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_34 <= 33'h0;
    end else begin
      if (io_we) begin
        if (_T_45) begin
          _T_34 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_37 <= 33'h0;
    end else begin
      if (io_we) begin
        if (_T_47) begin
          _T_37 <= di_d;
        end
      end
    end
  end
endmodule
module NV_NVDLA_fifo_new( // @[:@57.2]
  input         clock, // @[:@58.4]
  input         reset, // @[:@59.4]
  input         io_clk, // @[:@60.4]
  input         io_wr_pvld, // @[:@60.4]
  output        io_wr_prdy, // @[:@60.4]
  input  [32:0] io_wr_pd, // @[:@60.4]
  output        io_wr_empty, // @[:@60.4]
  output        io_rd_pvld, // @[:@60.4]
  input         io_rd_prdy, // @[:@60.4]
  output [32:0] io_rd_pd // @[:@60.4]
);
  wire  ram_reset; // @[FIFO_new.scala 273:29:@139.4]
  wire  ram_io_clk; // @[FIFO_new.scala 273:29:@139.4]
  wire  ram_io_clk_mgated; // @[FIFO_new.scala 273:29:@139.4]
  wire [32:0] ram_io_di; // @[FIFO_new.scala 273:29:@139.4]
  wire  ram_io_iwe; // @[FIFO_new.scala 273:29:@139.4]
  wire  ram_io_we; // @[FIFO_new.scala 273:29:@139.4]
  wire [1:0] ram_io_wa; // @[FIFO_new.scala 273:29:@139.4]
  wire [2:0] ram_io_ra; // @[FIFO_new.scala 273:29:@139.4]
  wire [32:0] ram_io_dout; // @[FIFO_new.scala 273:29:@139.4]
  reg  wr_busy_int; // @[FIFO_new.scala 162:56:@69.4]
  reg [31:0] _RAND_0;
  reg  wr_pvld_in; // @[FIFO_new.scala 164:44:@70.4]
  reg [31:0] _RAND_1;
  reg  wr_busy_in; // @[FIFO_new.scala 170:44:@71.4]
  reg [31:0] _RAND_2;
  reg [2:0] rd_count; // @[FIFO_new.scala 347:53:@155.4]
  reg [31:0] _RAND_3;
  wire  _T_98; // @[FIFO_new.scala 391:35:@168.4]
  wire  _T_38; // @[FIFO_new.scala 193:39:@87.4]
  wire  wr_reserving; // @[FIFO_new.scala 193:36:@88.4]
  wire  rd_pvld_p; // @[FIFO_new.scala 391:42:@169.4]
  wire  rd_popping; // @[FIFO_new.scala 406:41:@171.4]
  reg [2:0] wr_count; // @[FIFO_new.scala 196:53:@91.4]
  reg [31:0] _RAND_4;
  wire [3:0] _T_48; // @[FIFO_new.scala 201:69:@96.4]
  wire [2:0] _T_49; // @[FIFO_new.scala 201:69:@97.4]
  wire [2:0] wr_count_next_no_wr_popping; // @[FIFO_new.scala 201:46:@98.4]
  wire  wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 204:80:@100.4]
  wire  wr_count_next_is_full; // @[FIFO_new.scala 205:40:@101.4]
  wire [3:0] _T_44; // @[FIFO_new.scala 200:76:@92.4]
  wire [3:0] _T_45; // @[FIFO_new.scala 200:76:@93.4]
  wire [2:0] _T_46; // @[FIFO_new.scala 200:76:@94.4]
  wire [2:0] wr_count_next_wr_popping; // @[FIFO_new.scala 200:43:@95.4]
  wire [2:0] wr_count_next; // @[FIFO_new.scala 202:32:@99.4]
  wire  _T_32; // @[FIFO_new.scala 176:60:@73.4]
  wire  _T_33; // @[FIFO_new.scala 176:80:@74.4]
  wire  wr_busy_in_next_wr_pvld_eq_0; // @[FIFO_new.scala 176:77:@75.4]
  wire  wr_busy_in_next; // @[FIFO_new.scala 177:38:@76.4]
  wire  wr_busy_in_int; // @[FIFO_new.scala 178:45:@77.4]
  wire  _T_34; // @[FIFO_new.scala 181:18:@79.4]
  wire  _T_35; // @[FIFO_new.scala 182:45:@81.6]
  wire  _T_36; // @[FIFO_new.scala 182:42:@82.6]
  wire  _GEN_0; // @[FIFO_new.scala 181:34:@80.4]
  wire  _T_58; // @[FIFO_new.scala 212:27:@109.4]
  wire [2:0] _GEN_1; // @[FIFO_new.scala 212:40:@110.4]
  wire  _T_60; // @[FIFO_new.scala 217:54:@113.4]
  wire  _T_61; // @[FIFO_new.scala 217:65:@114.4]
  wire  _T_62; // @[FIFO_new.scala 217:62:@115.4]
  reg  _T_65; // @[FIFO_new.scala 217:39:@116.4]
  reg [31:0] _RAND_5;
  reg [1:0] _T_68; // @[FIFO_new.scala 224:68:@119.4]
  reg [31:0] _RAND_6;
  wire [2:0] _T_70; // @[FIFO_new.scala 227:47:@120.4]
  wire [1:0] wr_adr_next; // @[FIFO_new.scala 227:47:@121.4]
  wire [1:0] _GEN_2; // @[FIFO_new.scala 228:29:@122.4]
  reg [1:0] rd_adr; // @[FIFO_new.scala 233:63:@126.4]
  reg [31:0] _RAND_7;
  wire [2:0] _T_75; // @[FIFO_new.scala 235:42:@127.4]
  wire [1:0] rd_adr_next_popping; // @[FIFO_new.scala 235:42:@128.4]
  wire [1:0] _GEN_3; // @[FIFO_new.scala 244:33:@129.4]
  wire  _T_78; // @[FIFO_new.scala 250:96:@133.4]
  wire  _T_79; // @[FIFO_new.scala 250:105:@134.4]
  wire  _T_80; // @[FIFO_new.scala 250:102:@135.4]
  wire  _T_83; // @[FIFO_new.scala 284:43:@149.4]
  wire [3:0] _T_90; // @[FIFO_new.scala 349:74:@156.4]
  wire [3:0] _T_91; // @[FIFO_new.scala 349:74:@157.4]
  wire [2:0] _T_92; // @[FIFO_new.scala 349:74:@158.4]
  wire [2:0] rd_count_next_rd_popping; // @[FIFO_new.scala 349:43:@159.4]
  wire [3:0] _T_94; // @[FIFO_new.scala 350:68:@160.4]
  wire [2:0] _T_95; // @[FIFO_new.scala 350:68:@161.4]
  wire [2:0] rd_count_next_no_rd_popping; // @[FIFO_new.scala 350:46:@162.4]
  wire [2:0] rd_count_next; // @[FIFO_new.scala 351:32:@163.4]
  wire  _T_96; // @[FIFO_new.scala 353:25:@164.4]
  wire [2:0] _GEN_4; // @[FIFO_new.scala 353:39:@165.4]
  nv_flopram ram ( // @[FIFO_new.scala 273:29:@139.4]
    .reset(ram_reset),
    .io_clk(ram_io_clk),
    .io_clk_mgated(ram_io_clk_mgated),
    .io_di(ram_io_di),
    .io_iwe(ram_io_iwe),
    .io_we(ram_io_we),
    .io_wa(ram_io_wa),
    .io_ra(ram_io_ra),
    .io_dout(ram_io_dout)
  );
  assign _T_98 = rd_count != 3'h0; // @[FIFO_new.scala 391:35:@168.4]
  assign _T_38 = ~ wr_busy_int; // @[FIFO_new.scala 193:39:@87.4]
  assign wr_reserving = wr_pvld_in & _T_38; // @[FIFO_new.scala 193:36:@88.4]
  assign rd_pvld_p = _T_98 | wr_reserving; // @[FIFO_new.scala 391:42:@169.4]
  assign rd_popping = rd_pvld_p & io_rd_prdy; // @[FIFO_new.scala 406:41:@171.4]
  assign _T_48 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@96.4]
  assign _T_49 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@97.4]
  assign wr_count_next_no_wr_popping = wr_reserving ? _T_49 : wr_count; // @[FIFO_new.scala 201:46:@98.4]
  assign wr_count_next_no_wr_popping_is_full = wr_count_next_no_wr_popping == 3'h4; // @[FIFO_new.scala 204:80:@100.4]
  assign wr_count_next_is_full = rd_popping ? 1'h0 : wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 205:40:@101.4]
  assign _T_44 = wr_count - 3'h1; // @[FIFO_new.scala 200:76:@92.4]
  assign _T_45 = $unsigned(_T_44); // @[FIFO_new.scala 200:76:@93.4]
  assign _T_46 = _T_45[2:0]; // @[FIFO_new.scala 200:76:@94.4]
  assign wr_count_next_wr_popping = wr_reserving ? wr_count : _T_46; // @[FIFO_new.scala 200:43:@95.4]
  assign wr_count_next = rd_popping ? wr_count_next_wr_popping : wr_count_next_no_wr_popping; // @[FIFO_new.scala 202:32:@99.4]
  assign _T_32 = wr_pvld_in & wr_count_next_is_full; // @[FIFO_new.scala 176:60:@73.4]
  assign _T_33 = ~ wr_reserving; // @[FIFO_new.scala 176:80:@74.4]
  assign wr_busy_in_next_wr_pvld_eq_0 = _T_32 & _T_33; // @[FIFO_new.scala 176:77:@75.4]
  assign wr_busy_in_next = io_wr_pvld ? wr_count_next_is_full : wr_busy_in_next_wr_pvld_eq_0; // @[FIFO_new.scala 177:38:@76.4]
  assign wr_busy_in_int = wr_pvld_in & wr_busy_int; // @[FIFO_new.scala 178:45:@77.4]
  assign _T_34 = ~ wr_busy_in_int; // @[FIFO_new.scala 181:18:@79.4]
  assign _T_35 = ~ wr_busy_in; // @[FIFO_new.scala 182:45:@81.6]
  assign _T_36 = io_wr_pvld & _T_35; // @[FIFO_new.scala 182:42:@82.6]
  assign _GEN_0 = _T_34 ? _T_36 : wr_pvld_in; // @[FIFO_new.scala 181:34:@80.4]
  assign _T_58 = wr_reserving ^ rd_popping; // @[FIFO_new.scala 212:27:@109.4]
  assign _GEN_1 = _T_58 ? wr_count_next : wr_count; // @[FIFO_new.scala 212:40:@110.4]
  assign _T_60 = wr_count_next == 3'h0; // @[FIFO_new.scala 217:54:@113.4]
  assign _T_61 = ~ io_wr_pvld; // @[FIFO_new.scala 217:65:@114.4]
  assign _T_62 = _T_60 & _T_61; // @[FIFO_new.scala 217:62:@115.4]
  assign _T_70 = _T_68 + 2'h1; // @[FIFO_new.scala 227:47:@120.4]
  assign wr_adr_next = _T_68 + 2'h1; // @[FIFO_new.scala 227:47:@121.4]
  assign _GEN_2 = wr_reserving ? wr_adr_next : _T_68; // @[FIFO_new.scala 228:29:@122.4]
  assign _T_75 = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@127.4]
  assign rd_adr_next_popping = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@128.4]
  assign _GEN_3 = rd_popping ? rd_adr_next_popping : rd_adr; // @[FIFO_new.scala 244:33:@129.4]
  assign _T_78 = wr_count > 3'h0; // @[FIFO_new.scala 250:96:@133.4]
  assign _T_79 = ~ rd_popping; // @[FIFO_new.scala 250:105:@134.4]
  assign _T_80 = _T_78 | _T_79; // @[FIFO_new.scala 250:102:@135.4]
  assign _T_83 = wr_count == 3'h0; // @[FIFO_new.scala 284:43:@149.4]
  assign _T_90 = rd_count - 3'h1; // @[FIFO_new.scala 349:74:@156.4]
  assign _T_91 = $unsigned(_T_90); // @[FIFO_new.scala 349:74:@157.4]
  assign _T_92 = _T_91[2:0]; // @[FIFO_new.scala 349:74:@158.4]
  assign rd_count_next_rd_popping = wr_reserving ? rd_count : _T_92; // @[FIFO_new.scala 349:43:@159.4]
  assign _T_94 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@160.4]
  assign _T_95 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@161.4]
  assign rd_count_next_no_rd_popping = wr_reserving ? _T_95 : rd_count; // @[FIFO_new.scala 350:46:@162.4]
  assign rd_count_next = rd_popping ? rd_count_next_rd_popping : rd_count_next_no_rd_popping; // @[FIFO_new.scala 351:32:@163.4]
  assign _T_96 = wr_reserving | rd_popping; // @[FIFO_new.scala 353:25:@164.4]
  assign _GEN_4 = _T_96 ? rd_count_next : rd_count; // @[FIFO_new.scala 353:39:@165.4]
  assign io_wr_prdy = ~ wr_busy_in; // @[FIFO_new.scala 192:20:@86.4]
  assign io_wr_empty = _T_65; // @[FIFO_new.scala 217:29:@118.4]
  assign io_rd_pvld = _T_98 | wr_reserving; // @[FIFO_new.scala 409:28:@173.4]
  assign io_rd_pd = ram_io_dout; // @[FIFO_new.scala 410:26:@174.4]
  assign ram_reset = reset; // @[:@141.4]
  assign ram_io_clk = io_clk; // @[FIFO_new.scala 274:24:@142.4]
  assign ram_io_clk_mgated = clock; // @[FIFO_new.scala 275:35:@143.4]
  assign ram_io_di = io_wr_pd; // @[FIFO_new.scala 277:23:@145.4]
  assign ram_io_iwe = _T_35 & io_wr_pvld; // @[FIFO_new.scala 278:28:@146.4]
  assign ram_io_we = wr_reserving & _T_80; // @[FIFO_new.scala 279:23:@147.4]
  assign ram_io_wa = _T_68; // @[FIFO_new.scala 281:31:@148.4]
  assign ram_io_ra = _T_83 ? 3'h4 : {{1'd0}, rd_adr}; // @[FIFO_new.scala 284:27:@151.4]
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
  wr_pvld_in = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  wr_busy_in = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  rd_count = _RAND_3[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  wr_count = _RAND_4[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_65 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_68 = _RAND_6[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  rd_adr = _RAND_7[1:0];
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
      wr_pvld_in <= 1'h0;
    end else begin
      if (_T_34) begin
        wr_pvld_in <= _T_36;
      end
    end
    if (reset) begin
      wr_busy_in <= 1'h0;
    end else begin
      if (io_wr_pvld) begin
        if (rd_popping) begin
          wr_busy_in <= 1'h0;
        end else begin
          wr_busy_in <= wr_count_next_no_wr_popping_is_full;
        end
      end else begin
        wr_busy_in <= wr_busy_in_next_wr_pvld_eq_0;
      end
    end
    if (reset) begin
      rd_count <= 3'h0;
    end else begin
      if (_T_96) begin
        if (rd_popping) begin
          if (!(wr_reserving)) begin
            rd_count <= _T_92;
          end
        end else begin
          if (wr_reserving) begin
            rd_count <= _T_95;
          end
        end
      end
    end
    if (reset) begin
      wr_count <= 3'h0;
    end else begin
      if (_T_58) begin
        if (rd_popping) begin
          if (!(wr_reserving)) begin
            wr_count <= _T_46;
          end
        end else begin
          if (wr_reserving) begin
            wr_count <= _T_49;
          end
        end
      end
    end
    if (reset) begin
      _T_65 <= 1'h1;
    end else begin
      _T_65 <= _T_62;
    end
    if (reset) begin
      _T_68 <= 2'h0;
    end else begin
      if (wr_reserving) begin
        _T_68 <= wr_adr_next;
      end
    end
    if (reset) begin
      rd_adr <= 2'h0;
    end else begin
      if (rd_popping) begin
        rd_adr <= rd_adr_next_popping;
      end
    end
  end
endmodule
module nv_flopram_1( // @[:@197.2]
  input         reset, // @[:@199.4]
  input         io_clk, // @[:@200.4]
  input         io_clk_mgated, // @[:@200.4]
  input  [19:0] io_di, // @[:@200.4]
  input         io_iwe, // @[:@200.4]
  input         io_we, // @[:@200.4]
  input  [1:0]  io_wa, // @[:@200.4]
  input  [2:0]  io_ra, // @[:@200.4]
  output [19:0] io_dout // @[:@200.4]
);
  reg [19:0] di_d; // @[Reg.scala 19:20:@202.4]
  reg [31:0] _RAND_0;
  wire [19:0] _GEN_0; // @[Reg.scala 20:19:@203.4]
  reg [19:0] _T_28; // @[nv_flopram.scala 68:61:@208.4]
  reg [31:0] _RAND_1;
  reg [19:0] _T_31; // @[nv_flopram.scala 68:61:@209.4]
  reg [31:0] _RAND_2;
  reg [19:0] _T_34; // @[nv_flopram.scala 68:61:@210.4]
  reg [31:0] _RAND_3;
  reg [19:0] _T_37; // @[nv_flopram.scala 68:61:@211.4]
  reg [31:0] _RAND_4;
  wire  _T_41; // @[nv_flopram.scala 73:32:@214.6]
  wire [19:0] _GEN_1; // @[nv_flopram.scala 73:40:@215.6]
  wire  _T_43; // @[nv_flopram.scala 73:32:@218.6]
  wire [19:0] _GEN_2; // @[nv_flopram.scala 73:40:@219.6]
  wire  _T_45; // @[nv_flopram.scala 73:32:@222.6]
  wire [19:0] _GEN_3; // @[nv_flopram.scala 73:40:@223.6]
  wire  _T_47; // @[nv_flopram.scala 73:32:@226.6]
  wire [19:0] _GEN_4; // @[nv_flopram.scala 73:40:@227.6]
  wire [19:0] _GEN_5; // @[nv_flopram.scala 70:16:@213.4]
  wire [19:0] _GEN_6; // @[nv_flopram.scala 70:16:@213.4]
  wire [19:0] _GEN_7; // @[nv_flopram.scala 70:16:@213.4]
  wire [19:0] _GEN_8; // @[nv_flopram.scala 70:16:@213.4]
  wire  _T_54; // @[Mux.scala 46:19:@232.4]
  wire [19:0] _T_55; // @[Mux.scala 46:16:@233.4]
  wire  _T_56; // @[Mux.scala 46:19:@234.4]
  wire [19:0] _T_57; // @[Mux.scala 46:16:@235.4]
  wire  _T_58; // @[Mux.scala 46:19:@236.4]
  wire [19:0] _T_59; // @[Mux.scala 46:16:@237.4]
  wire  _T_60; // @[Mux.scala 46:19:@238.4]
  wire [19:0] _T_61; // @[Mux.scala 46:16:@239.4]
  wire  _T_62; // @[Mux.scala 46:19:@240.4]
  assign _GEN_0 = io_iwe ? io_di : di_d; // @[Reg.scala 20:19:@203.4]
  assign _T_41 = io_wa == 2'h0; // @[nv_flopram.scala 73:32:@214.6]
  assign _GEN_1 = _T_41 ? di_d : _T_28; // @[nv_flopram.scala 73:40:@215.6]
  assign _T_43 = io_wa == 2'h1; // @[nv_flopram.scala 73:32:@218.6]
  assign _GEN_2 = _T_43 ? di_d : _T_31; // @[nv_flopram.scala 73:40:@219.6]
  assign _T_45 = io_wa == 2'h2; // @[nv_flopram.scala 73:32:@222.6]
  assign _GEN_3 = _T_45 ? di_d : _T_34; // @[nv_flopram.scala 73:40:@223.6]
  assign _T_47 = io_wa == 2'h3; // @[nv_flopram.scala 73:32:@226.6]
  assign _GEN_4 = _T_47 ? di_d : _T_37; // @[nv_flopram.scala 73:40:@227.6]
  assign _GEN_5 = io_we ? _GEN_1 : _T_28; // @[nv_flopram.scala 70:16:@213.4]
  assign _GEN_6 = io_we ? _GEN_2 : _T_31; // @[nv_flopram.scala 70:16:@213.4]
  assign _GEN_7 = io_we ? _GEN_3 : _T_34; // @[nv_flopram.scala 70:16:@213.4]
  assign _GEN_8 = io_we ? _GEN_4 : _T_37; // @[nv_flopram.scala 70:16:@213.4]
  assign _T_54 = 3'h4 == io_ra; // @[Mux.scala 46:19:@232.4]
  assign _T_55 = _T_54 ? io_di : 20'h0; // @[Mux.scala 46:16:@233.4]
  assign _T_56 = 3'h3 == io_ra; // @[Mux.scala 46:19:@234.4]
  assign _T_57 = _T_56 ? _T_37 : _T_55; // @[Mux.scala 46:16:@235.4]
  assign _T_58 = 3'h2 == io_ra; // @[Mux.scala 46:19:@236.4]
  assign _T_59 = _T_58 ? _T_34 : _T_57; // @[Mux.scala 46:16:@237.4]
  assign _T_60 = 3'h1 == io_ra; // @[Mux.scala 46:19:@238.4]
  assign _T_61 = _T_60 ? _T_31 : _T_59; // @[Mux.scala 46:16:@239.4]
  assign _T_62 = 3'h0 == io_ra; // @[Mux.scala 46:19:@240.4]
  assign io_dout = _T_62 ? _T_28 : _T_61; // @[nv_flopram.scala 83:13:@242.4]
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
  di_d = _RAND_0[19:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_28 = _RAND_1[19:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_31 = _RAND_2[19:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_34 = _RAND_3[19:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_37 = _RAND_4[19:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      di_d <= 20'h0;
    end else begin
      if (io_iwe) begin
        di_d <= io_di;
      end
    end
  end
  always @(posedge io_clk_mgated) begin
    if (reset) begin
      _T_28 <= 20'h0;
    end else begin
      if (io_we) begin
        if (_T_41) begin
          _T_28 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_31 <= 20'h0;
    end else begin
      if (io_we) begin
        if (_T_43) begin
          _T_31 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_34 <= 20'h0;
    end else begin
      if (io_we) begin
        if (_T_45) begin
          _T_34 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_37 <= 20'h0;
    end else begin
      if (io_we) begin
        if (_T_47) begin
          _T_37 <= di_d;
        end
      end
    end
  end
endmodule
module NV_NVDLA_fifo_new_1( // @[:@244.2]
  input         clock, // @[:@245.4]
  input         reset, // @[:@246.4]
  input         io_clk, // @[:@247.4]
  input         io_wr_pvld, // @[:@247.4]
  output        io_wr_prdy, // @[:@247.4]
  input  [19:0] io_wr_pd, // @[:@247.4]
  output        io_wr_empty, // @[:@247.4]
  output        io_rd_pvld, // @[:@247.4]
  input         io_rd_prdy, // @[:@247.4]
  output [19:0] io_rd_pd // @[:@247.4]
);
  wire  ram_reset; // @[FIFO_new.scala 273:29:@326.4]
  wire  ram_io_clk; // @[FIFO_new.scala 273:29:@326.4]
  wire  ram_io_clk_mgated; // @[FIFO_new.scala 273:29:@326.4]
  wire [19:0] ram_io_di; // @[FIFO_new.scala 273:29:@326.4]
  wire  ram_io_iwe; // @[FIFO_new.scala 273:29:@326.4]
  wire  ram_io_we; // @[FIFO_new.scala 273:29:@326.4]
  wire [1:0] ram_io_wa; // @[FIFO_new.scala 273:29:@326.4]
  wire [2:0] ram_io_ra; // @[FIFO_new.scala 273:29:@326.4]
  wire [19:0] ram_io_dout; // @[FIFO_new.scala 273:29:@326.4]
  reg  wr_busy_int; // @[FIFO_new.scala 162:56:@256.4]
  reg [31:0] _RAND_0;
  reg  wr_pvld_in; // @[FIFO_new.scala 164:44:@257.4]
  reg [31:0] _RAND_1;
  reg  wr_busy_in; // @[FIFO_new.scala 170:44:@258.4]
  reg [31:0] _RAND_2;
  reg [2:0] rd_count; // @[FIFO_new.scala 347:53:@342.4]
  reg [31:0] _RAND_3;
  wire  _T_98; // @[FIFO_new.scala 391:35:@355.4]
  wire  _T_38; // @[FIFO_new.scala 193:39:@274.4]
  wire  wr_reserving; // @[FIFO_new.scala 193:36:@275.4]
  wire  rd_pvld_p; // @[FIFO_new.scala 391:42:@356.4]
  wire  rd_popping; // @[FIFO_new.scala 406:41:@358.4]
  reg [2:0] wr_count; // @[FIFO_new.scala 196:53:@278.4]
  reg [31:0] _RAND_4;
  wire [3:0] _T_48; // @[FIFO_new.scala 201:69:@283.4]
  wire [2:0] _T_49; // @[FIFO_new.scala 201:69:@284.4]
  wire [2:0] wr_count_next_no_wr_popping; // @[FIFO_new.scala 201:46:@285.4]
  wire  wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 204:80:@287.4]
  wire  wr_count_next_is_full; // @[FIFO_new.scala 205:40:@288.4]
  wire [3:0] _T_44; // @[FIFO_new.scala 200:76:@279.4]
  wire [3:0] _T_45; // @[FIFO_new.scala 200:76:@280.4]
  wire [2:0] _T_46; // @[FIFO_new.scala 200:76:@281.4]
  wire [2:0] wr_count_next_wr_popping; // @[FIFO_new.scala 200:43:@282.4]
  wire [2:0] wr_count_next; // @[FIFO_new.scala 202:32:@286.4]
  wire  _T_32; // @[FIFO_new.scala 176:60:@260.4]
  wire  _T_33; // @[FIFO_new.scala 176:80:@261.4]
  wire  wr_busy_in_next_wr_pvld_eq_0; // @[FIFO_new.scala 176:77:@262.4]
  wire  wr_busy_in_next; // @[FIFO_new.scala 177:38:@263.4]
  wire  wr_busy_in_int; // @[FIFO_new.scala 178:45:@264.4]
  wire  _T_34; // @[FIFO_new.scala 181:18:@266.4]
  wire  _T_35; // @[FIFO_new.scala 182:45:@268.6]
  wire  _T_36; // @[FIFO_new.scala 182:42:@269.6]
  wire  _GEN_0; // @[FIFO_new.scala 181:34:@267.4]
  wire  _T_58; // @[FIFO_new.scala 212:27:@296.4]
  wire [2:0] _GEN_1; // @[FIFO_new.scala 212:40:@297.4]
  wire  _T_60; // @[FIFO_new.scala 217:54:@300.4]
  wire  _T_61; // @[FIFO_new.scala 217:65:@301.4]
  wire  _T_62; // @[FIFO_new.scala 217:62:@302.4]
  reg  _T_65; // @[FIFO_new.scala 217:39:@303.4]
  reg [31:0] _RAND_5;
  reg [1:0] _T_68; // @[FIFO_new.scala 224:68:@306.4]
  reg [31:0] _RAND_6;
  wire [2:0] _T_70; // @[FIFO_new.scala 227:47:@307.4]
  wire [1:0] wr_adr_next; // @[FIFO_new.scala 227:47:@308.4]
  wire [1:0] _GEN_2; // @[FIFO_new.scala 228:29:@309.4]
  reg [1:0] rd_adr; // @[FIFO_new.scala 233:63:@313.4]
  reg [31:0] _RAND_7;
  wire [2:0] _T_75; // @[FIFO_new.scala 235:42:@314.4]
  wire [1:0] rd_adr_next_popping; // @[FIFO_new.scala 235:42:@315.4]
  wire [1:0] _GEN_3; // @[FIFO_new.scala 244:33:@316.4]
  wire  _T_78; // @[FIFO_new.scala 250:96:@320.4]
  wire  _T_79; // @[FIFO_new.scala 250:105:@321.4]
  wire  _T_80; // @[FIFO_new.scala 250:102:@322.4]
  wire  _T_83; // @[FIFO_new.scala 284:43:@336.4]
  wire [3:0] _T_90; // @[FIFO_new.scala 349:74:@343.4]
  wire [3:0] _T_91; // @[FIFO_new.scala 349:74:@344.4]
  wire [2:0] _T_92; // @[FIFO_new.scala 349:74:@345.4]
  wire [2:0] rd_count_next_rd_popping; // @[FIFO_new.scala 349:43:@346.4]
  wire [3:0] _T_94; // @[FIFO_new.scala 350:68:@347.4]
  wire [2:0] _T_95; // @[FIFO_new.scala 350:68:@348.4]
  wire [2:0] rd_count_next_no_rd_popping; // @[FIFO_new.scala 350:46:@349.4]
  wire [2:0] rd_count_next; // @[FIFO_new.scala 351:32:@350.4]
  wire  _T_96; // @[FIFO_new.scala 353:25:@351.4]
  wire [2:0] _GEN_4; // @[FIFO_new.scala 353:39:@352.4]
  nv_flopram_1 ram ( // @[FIFO_new.scala 273:29:@326.4]
    .reset(ram_reset),
    .io_clk(ram_io_clk),
    .io_clk_mgated(ram_io_clk_mgated),
    .io_di(ram_io_di),
    .io_iwe(ram_io_iwe),
    .io_we(ram_io_we),
    .io_wa(ram_io_wa),
    .io_ra(ram_io_ra),
    .io_dout(ram_io_dout)
  );
  assign _T_98 = rd_count != 3'h0; // @[FIFO_new.scala 391:35:@355.4]
  assign _T_38 = ~ wr_busy_int; // @[FIFO_new.scala 193:39:@274.4]
  assign wr_reserving = wr_pvld_in & _T_38; // @[FIFO_new.scala 193:36:@275.4]
  assign rd_pvld_p = _T_98 | wr_reserving; // @[FIFO_new.scala 391:42:@356.4]
  assign rd_popping = rd_pvld_p & io_rd_prdy; // @[FIFO_new.scala 406:41:@358.4]
  assign _T_48 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@283.4]
  assign _T_49 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@284.4]
  assign wr_count_next_no_wr_popping = wr_reserving ? _T_49 : wr_count; // @[FIFO_new.scala 201:46:@285.4]
  assign wr_count_next_no_wr_popping_is_full = wr_count_next_no_wr_popping == 3'h4; // @[FIFO_new.scala 204:80:@287.4]
  assign wr_count_next_is_full = rd_popping ? 1'h0 : wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 205:40:@288.4]
  assign _T_44 = wr_count - 3'h1; // @[FIFO_new.scala 200:76:@279.4]
  assign _T_45 = $unsigned(_T_44); // @[FIFO_new.scala 200:76:@280.4]
  assign _T_46 = _T_45[2:0]; // @[FIFO_new.scala 200:76:@281.4]
  assign wr_count_next_wr_popping = wr_reserving ? wr_count : _T_46; // @[FIFO_new.scala 200:43:@282.4]
  assign wr_count_next = rd_popping ? wr_count_next_wr_popping : wr_count_next_no_wr_popping; // @[FIFO_new.scala 202:32:@286.4]
  assign _T_32 = wr_pvld_in & wr_count_next_is_full; // @[FIFO_new.scala 176:60:@260.4]
  assign _T_33 = ~ wr_reserving; // @[FIFO_new.scala 176:80:@261.4]
  assign wr_busy_in_next_wr_pvld_eq_0 = _T_32 & _T_33; // @[FIFO_new.scala 176:77:@262.4]
  assign wr_busy_in_next = io_wr_pvld ? wr_count_next_is_full : wr_busy_in_next_wr_pvld_eq_0; // @[FIFO_new.scala 177:38:@263.4]
  assign wr_busy_in_int = wr_pvld_in & wr_busy_int; // @[FIFO_new.scala 178:45:@264.4]
  assign _T_34 = ~ wr_busy_in_int; // @[FIFO_new.scala 181:18:@266.4]
  assign _T_35 = ~ wr_busy_in; // @[FIFO_new.scala 182:45:@268.6]
  assign _T_36 = io_wr_pvld & _T_35; // @[FIFO_new.scala 182:42:@269.6]
  assign _GEN_0 = _T_34 ? _T_36 : wr_pvld_in; // @[FIFO_new.scala 181:34:@267.4]
  assign _T_58 = wr_reserving ^ rd_popping; // @[FIFO_new.scala 212:27:@296.4]
  assign _GEN_1 = _T_58 ? wr_count_next : wr_count; // @[FIFO_new.scala 212:40:@297.4]
  assign _T_60 = wr_count_next == 3'h0; // @[FIFO_new.scala 217:54:@300.4]
  assign _T_61 = ~ io_wr_pvld; // @[FIFO_new.scala 217:65:@301.4]
  assign _T_62 = _T_60 & _T_61; // @[FIFO_new.scala 217:62:@302.4]
  assign _T_70 = _T_68 + 2'h1; // @[FIFO_new.scala 227:47:@307.4]
  assign wr_adr_next = _T_68 + 2'h1; // @[FIFO_new.scala 227:47:@308.4]
  assign _GEN_2 = wr_reserving ? wr_adr_next : _T_68; // @[FIFO_new.scala 228:29:@309.4]
  assign _T_75 = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@314.4]
  assign rd_adr_next_popping = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@315.4]
  assign _GEN_3 = rd_popping ? rd_adr_next_popping : rd_adr; // @[FIFO_new.scala 244:33:@316.4]
  assign _T_78 = wr_count > 3'h0; // @[FIFO_new.scala 250:96:@320.4]
  assign _T_79 = ~ rd_popping; // @[FIFO_new.scala 250:105:@321.4]
  assign _T_80 = _T_78 | _T_79; // @[FIFO_new.scala 250:102:@322.4]
  assign _T_83 = wr_count == 3'h0; // @[FIFO_new.scala 284:43:@336.4]
  assign _T_90 = rd_count - 3'h1; // @[FIFO_new.scala 349:74:@343.4]
  assign _T_91 = $unsigned(_T_90); // @[FIFO_new.scala 349:74:@344.4]
  assign _T_92 = _T_91[2:0]; // @[FIFO_new.scala 349:74:@345.4]
  assign rd_count_next_rd_popping = wr_reserving ? rd_count : _T_92; // @[FIFO_new.scala 349:43:@346.4]
  assign _T_94 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@347.4]
  assign _T_95 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@348.4]
  assign rd_count_next_no_rd_popping = wr_reserving ? _T_95 : rd_count; // @[FIFO_new.scala 350:46:@349.4]
  assign rd_count_next = rd_popping ? rd_count_next_rd_popping : rd_count_next_no_rd_popping; // @[FIFO_new.scala 351:32:@350.4]
  assign _T_96 = wr_reserving | rd_popping; // @[FIFO_new.scala 353:25:@351.4]
  assign _GEN_4 = _T_96 ? rd_count_next : rd_count; // @[FIFO_new.scala 353:39:@352.4]
  assign io_wr_prdy = ~ wr_busy_in; // @[FIFO_new.scala 192:20:@273.4]
  assign io_wr_empty = _T_65; // @[FIFO_new.scala 217:29:@305.4]
  assign io_rd_pvld = _T_98 | wr_reserving; // @[FIFO_new.scala 409:28:@360.4]
  assign io_rd_pd = ram_io_dout; // @[FIFO_new.scala 410:26:@361.4]
  assign ram_reset = reset; // @[:@328.4]
  assign ram_io_clk = io_clk; // @[FIFO_new.scala 274:24:@329.4]
  assign ram_io_clk_mgated = clock; // @[FIFO_new.scala 275:35:@330.4]
  assign ram_io_di = io_wr_pd; // @[FIFO_new.scala 277:23:@332.4]
  assign ram_io_iwe = _T_35 & io_wr_pvld; // @[FIFO_new.scala 278:28:@333.4]
  assign ram_io_we = wr_reserving & _T_80; // @[FIFO_new.scala 279:23:@334.4]
  assign ram_io_wa = _T_68; // @[FIFO_new.scala 281:31:@335.4]
  assign ram_io_ra = _T_83 ? 3'h4 : {{1'd0}, rd_adr}; // @[FIFO_new.scala 284:27:@338.4]
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
  wr_pvld_in = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  wr_busy_in = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  rd_count = _RAND_3[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  wr_count = _RAND_4[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_65 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_68 = _RAND_6[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  rd_adr = _RAND_7[1:0];
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
      wr_pvld_in <= 1'h0;
    end else begin
      if (_T_34) begin
        wr_pvld_in <= _T_36;
      end
    end
    if (reset) begin
      wr_busy_in <= 1'h0;
    end else begin
      if (io_wr_pvld) begin
        if (rd_popping) begin
          wr_busy_in <= 1'h0;
        end else begin
          wr_busy_in <= wr_count_next_no_wr_popping_is_full;
        end
      end else begin
        wr_busy_in <= wr_busy_in_next_wr_pvld_eq_0;
      end
    end
    if (reset) begin
      rd_count <= 3'h0;
    end else begin
      if (_T_96) begin
        if (rd_popping) begin
          if (!(wr_reserving)) begin
            rd_count <= _T_92;
          end
        end else begin
          if (wr_reserving) begin
            rd_count <= _T_95;
          end
        end
      end
    end
    if (reset) begin
      wr_count <= 3'h0;
    end else begin
      if (_T_58) begin
        if (rd_popping) begin
          if (!(wr_reserving)) begin
            wr_count <= _T_46;
          end
        end else begin
          if (wr_reserving) begin
            wr_count <= _T_49;
          end
        end
      end
    end
    if (reset) begin
      _T_65 <= 1'h1;
    end else begin
      _T_65 <= _T_62;
    end
    if (reset) begin
      _T_68 <= 2'h0;
    end else begin
      if (wr_reserving) begin
        _T_68 <= wr_adr_next;
      end
    end
    if (reset) begin
      rd_adr <= 2'h0;
    end else begin
      if (rd_popping) begin
        rd_adr <= rd_adr_next_popping;
      end
    end
  end
endmodule
module NV_NVDLA_CSC_sg( // @[:@377.2]
  input         clock, // @[:@378.4]
  input         reset, // @[:@379.4]
  input         io_nvdla_core_clk, // @[:@380.4]
  input         io_nvdla_core_ng_clk, // @[:@380.4]
  input         io_cdma2sc_dat_updt_valid, // @[:@380.4]
  input  [14:0] io_cdma2sc_dat_updt_bits_entries, // @[:@380.4]
  input  [13:0] io_cdma2sc_dat_updt_bits_slices, // @[:@380.4]
  output        io_sc2cdma_dat_pending_req, // @[:@380.4]
  input         io_cdma2sc_dat_pending_ack, // @[:@380.4]
  input         io_cdma2sc_wt_updt_valid, // @[:@380.4]
  input  [14:0] io_cdma2sc_wt_updt_bits_entries, // @[:@380.4]
  input  [13:0] io_cdma2sc_wt_updt_bits_kernels, // @[:@380.4]
  output        io_sc2cdma_wt_pending_req, // @[:@380.4]
  input         io_cdma2sc_wt_pending_ack, // @[:@380.4]
  output [1:0]  io_sc_state, // @[:@380.4]
  output        io_sg2dl_pd_valid, // @[:@380.4]
  output [30:0] io_sg2dl_pd_bits, // @[:@380.4]
  output        io_sg2dl_reuse_rls, // @[:@380.4]
  output        io_sg2wl_pd_valid, // @[:@380.4]
  output [17:0] io_sg2wl_pd_bits, // @[:@380.4]
  output        io_sg2wl_reuse_rls, // @[:@380.4]
  input         io_accu2sc_credit_size_valid, // @[:@380.4]
  input  [2:0]  io_accu2sc_credit_size_bits, // @[:@380.4]
  input         io_reg2dp_op_en, // @[:@380.4]
  input         io_reg2dp_conv_mode, // @[:@380.4]
  input  [1:0]  io_reg2dp_proc_precision, // @[:@380.4]
  input         io_reg2dp_data_reuse, // @[:@380.4]
  input         io_reg2dp_skip_data_rls, // @[:@380.4]
  input         io_reg2dp_weight_reuse, // @[:@380.4]
  input         io_reg2dp_skip_weight_rls, // @[:@380.4]
  input  [4:0]  io_reg2dp_batches, // @[:@380.4]
  input         io_reg2dp_datain_format, // @[:@380.4]
  input  [12:0] io_reg2dp_datain_height_ext, // @[:@380.4]
  input  [1:0]  io_reg2dp_y_extension, // @[:@380.4]
  input  [4:0]  io_reg2dp_weight_width_ext, // @[:@380.4]
  input  [4:0]  io_reg2dp_weight_height_ext, // @[:@380.4]
  input  [12:0] io_reg2dp_weight_channel_ext, // @[:@380.4]
  input  [12:0] io_reg2dp_weight_kernel, // @[:@380.4]
  input  [12:0] io_reg2dp_dataout_width, // @[:@380.4]
  input  [12:0] io_reg2dp_dataout_height, // @[:@380.4]
  input  [4:0]  io_reg2dp_data_bank, // @[:@380.4]
  input  [4:0]  io_reg2dp_weight_bank, // @[:@380.4]
  input  [20:0] io_reg2dp_atomics, // @[:@380.4]
  input  [11:0] io_reg2dp_rls_slices, // @[:@380.4]
  output        io_dp2reg_done, // @[:@380.4]
  input  [31:0] io_pwrbus_ram_pd // @[:@380.4]
);
  wire  u_dat_fifo_clock; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire  u_dat_fifo_reset; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire  u_dat_fifo_io_clk; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire  u_dat_fifo_io_wr_pvld; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire  u_dat_fifo_io_wr_prdy; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire [32:0] u_dat_fifo_io_wr_pd; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire  u_dat_fifo_io_wr_empty; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire  u_dat_fifo_io_rd_pvld; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire  u_dat_fifo_io_rd_prdy; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire [32:0] u_dat_fifo_io_rd_pd; // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
  wire  u_wt_fifo_clock; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  wire  u_wt_fifo_reset; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  wire  u_wt_fifo_io_clk; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  wire  u_wt_fifo_io_wr_pvld; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  wire  u_wt_fifo_io_wr_prdy; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  wire [19:0] u_wt_fifo_io_wr_pd; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  wire  u_wt_fifo_io_wr_empty; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  wire  u_wt_fifo_io_rd_pvld; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  wire  u_wt_fifo_io_rd_prdy; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  wire [19:0] u_wt_fifo_io_rd_pd; // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
  reg  layer_done; // @[NV_NVDLA_CSC_sg.scala 89:29:@384.4]
  reg [31:0] _RAND_0;
  reg  pkg_vld; // @[NV_NVDLA_CSC_sg.scala 91:26:@386.4]
  reg [31:0] _RAND_1;
  reg [1:0] cur_state; // @[NV_NVDLA_CSC_sg.scala 94:28:@387.4]
  reg [31:0] _RAND_2;
  wire  _T_134; // @[Conditional.scala 37:30:@390.4]
  reg [4:0] last_data_bank; // @[NV_NVDLA_CSC_sg.scala 126:33:@443.4]
  reg [31:0] _RAND_3;
  wire  dat_bank_change; // @[NV_NVDLA_CSC_sg.scala 143:43:@464.4]
  reg [4:0] last_weight_bank; // @[NV_NVDLA_CSC_sg.scala 127:35:@444.4]
  reg [31:0] _RAND_4;
  wire  wt_bank_change; // @[NV_NVDLA_CSC_sg.scala 144:44:@465.4]
  wire  need_pending; // @[NV_NVDLA_CSC_sg.scala 145:38:@466.4]
  wire  _T_135; // @[NV_NVDLA_CSC_sg.scala 99:31:@392.6]
  wire [1:0] _GEN_0; // @[NV_NVDLA_CSC_sg.scala 100:37:@397.8]
  wire [1:0] _GEN_1; // @[NV_NVDLA_CSC_sg.scala 99:47:@393.6]
  wire  _T_136; // @[Conditional.scala 37:30:@405.6]
  wire  is_pending; // @[NV_NVDLA_CSC_sg.scala 165:30:@498.4]
  reg  dat_pending_clr; // @[NV_NVDLA_CSC_sg.scala 128:34:@445.4]
  reg [31:0] _RAND_5;
  reg  dat_pending_req; // @[NV_NVDLA_CSC_sg.scala 129:34:@446.4]
  reg [31:0] _RAND_6;
  wire  _T_179; // @[NV_NVDLA_CSC_sg.scala 146:51:@468.4]
  wire  _T_180; // @[NV_NVDLA_CSC_sg.scala 146:34:@469.4]
  wire  _T_181; // @[NV_NVDLA_CSC_sg.scala 146:32:@470.4]
  reg  wt_pending_clr; // @[NV_NVDLA_CSC_sg.scala 131:33:@448.4]
  reg [31:0] _RAND_7;
  reg  wt_pending_req; // @[NV_NVDLA_CSC_sg.scala 132:33:@449.4]
  reg [31:0] _RAND_8;
  wire  _T_182; // @[NV_NVDLA_CSC_sg.scala 146:87:@471.4]
  wire  _T_183; // @[NV_NVDLA_CSC_sg.scala 146:71:@472.4]
  wire  pending_done; // @[NV_NVDLA_CSC_sg.scala 146:69:@473.4]
  wire [1:0] _GEN_2; // @[NV_NVDLA_CSC_sg.scala 104:29:@407.8]
  wire  _T_137; // @[Conditional.scala 37:30:@415.8]
  wire  dat_pop_req; // @[NV_NVDLA_CSC_sg.scala 121:27:@438.4 NV_NVDLA_CSC_sg.scala 450:17:@883.4]
  wire  _T_173; // @[NV_NVDLA_CSC_sg.scala 142:22:@458.4]
  wire  wt_pop_req; // @[NV_NVDLA_CSC_sg.scala 124:26:@441.4 NV_NVDLA_CSC_sg.scala 464:16:@894.4]
  wire  _T_174; // @[NV_NVDLA_CSC_sg.scala 142:37:@459.4]
  wire  _T_175; // @[NV_NVDLA_CSC_sg.scala 142:35:@460.4]
  wire  dat_push_empty; // @[NV_NVDLA_CSC_sg.scala 122:30:@439.4 NV_NVDLA_CSC_sg.scala 454:20:@885.4]
  wire  _T_176; // @[NV_NVDLA_CSC_sg.scala 142:49:@461.4]
  wire  wt_push_empty; // @[NV_NVDLA_CSC_sg.scala 125:29:@442.4 NV_NVDLA_CSC_sg.scala 467:19:@896.4]
  wire  fifo_is_clear; // @[NV_NVDLA_CSC_sg.scala 142:66:@462.4]
  wire  _T_138; // @[NV_NVDLA_CSC_sg.scala 108:26:@417.10]
  wire  _T_139; // @[NV_NVDLA_CSC_sg.scala 108:44:@418.10]
  wire  _T_140; // @[NV_NVDLA_CSC_sg.scala 108:42:@419.10]
  wire [1:0] _GEN_3; // @[NV_NVDLA_CSC_sg.scala 108:54:@420.10]
  wire  _T_141; // @[Conditional.scala 37:30:@428.10]
  wire [1:0] _GEN_4; // @[NV_NVDLA_CSC_sg.scala 112:31:@430.12]
  wire [1:0] _GEN_5; // @[Conditional.scala 39:67:@429.10]
  wire [1:0] _GEN_6; // @[Conditional.scala 39:67:@416.8]
  wire [1:0] _GEN_7; // @[Conditional.scala 39:67:@406.6]
  wire [1:0] nxt_state; // @[Conditional.scala 40:58:@391.4]
  reg  dat_pending_ack; // @[NV_NVDLA_CSC_sg.scala 130:34:@447.4]
  reg [31:0] _RAND_9;
  reg  wt_pending_ack; // @[NV_NVDLA_CSC_sg.scala 133:33:@450.4]
  reg [31:0] _RAND_10;
  reg [6:0] dat_stripe_size; // @[NV_NVDLA_CSC_sg.scala 135:34:@452.4]
  reg [31:0] _RAND_11;
  reg [7:0] flush_cycles; // @[NV_NVDLA_CSC_sg.scala 138:31:@455.4]
  reg [31:0] _RAND_12;
  reg [7:0] sg_dn_cnt; // @[NV_NVDLA_CSC_sg.scala 139:28:@456.4]
  reg [31:0] _RAND_13;
  wire  is_done; // @[NV_NVDLA_CSC_sg.scala 167:27:@501.4]
  wire  _T_185; // @[NV_NVDLA_CSC_sg.scala 149:26:@476.6]
  wire  is_nxt_done; // @[NV_NVDLA_CSC_sg.scala 168:31:@503.4]
  wire  _T_186; // @[NV_NVDLA_CSC_sg.scala 149:35:@477.6]
  wire [8:0] _T_188; // @[NV_NVDLA_CSC_sg.scala 149:74:@478.6]
  wire [8:0] _T_189; // @[NV_NVDLA_CSC_sg.scala 149:74:@479.6]
  wire [7:0] _T_190; // @[NV_NVDLA_CSC_sg.scala 149:74:@480.6]
  wire [7:0] _T_191; // @[NV_NVDLA_CSC_sg.scala 149:25:@481.6]
  wire [7:0] _GEN_9; // @[NV_NVDLA_CSC_sg.scala 148:22:@475.4]
  reg [5:0] pop_cnt; // @[NV_NVDLA_CSC_sg.scala 515:26:@936.4]
  reg [31:0] _RAND_14;
  wire  _T_697; // @[NV_NVDLA_CSC_sg.scala 535:45:@977.4]
  wire  _T_698; // @[NV_NVDLA_CSC_sg.scala 535:34:@978.4]
  wire [30:0] dat_pop_pd; // @[NV_NVDLA_CSC_sg.scala 474:34:@898.4]
  wire  sg2dat_channel_end; // @[NV_NVDLA_CSC_sg.scala 488:40:@907.4]
  wire  _T_713; // @[NV_NVDLA_CSC_sg.scala 575:21:@1014.4]
  reg [8:0] credit_cnt; // @[NV_NVDLA_CSC_sg.scala 562:61:@1003.4]
  reg [31:0] _RAND_15;
  wire [8:0] dat_impact_cnt; // @[Cat.scala 30:58:@1010.4]
  wire  _T_714; // @[NV_NVDLA_CSC_sg.scala 575:55:@1015.4]
  wire  credit_ready; // @[NV_NVDLA_CSC_sg.scala 575:41:@1016.4]
  wire  _T_699; // @[NV_NVDLA_CSC_sg.scala 535:54:@979.4]
  wire [1:0] dat_pop_idx; // @[NV_NVDLA_CSC_sg.scala 472:35:@897.4]
  wire [1:0] wt_pop_idx; // @[NV_NVDLA_CSC_sg.scala 476:33:@899.4]
  wire  _T_700; // @[NV_NVDLA_CSC_sg.scala 535:85:@980.4]
  wire  _T_702; // @[NV_NVDLA_CSC_sg.scala 535:101:@982.4]
  wire  dat_pop_ready; // @[NV_NVDLA_CSC_sg.scala 535:69:@983.4]
  wire  _T_192; // @[NV_NVDLA_CSC_sg.scala 153:22:@486.4]
  wire  sg2dat_layer_end; // @[NV_NVDLA_CSC_sg.scala 490:35:@909.4]
  wire  _T_193; // @[NV_NVDLA_CSC_sg.scala 153:38:@487.4]
  wire [7:0] _T_195; // @[NV_NVDLA_CSC_sg.scala 154:41:@489.6]
  wire [7:0] _GEN_10; // @[NV_NVDLA_CSC_sg.scala 153:57:@488.4]
  reg [2:0] last_mode; // @[NV_NVDLA_CSC_sg.scala 161:28:@494.4]
  reg [31:0] _RAND_16;
  wire  _T_203; // @[NV_NVDLA_CSC_sg.scala 163:49:@495.4]
  wire  layer_st; // @[NV_NVDLA_CSC_sg.scala 163:36:@496.4]
  wire  is_running; // @[NV_NVDLA_CSC_sg.scala 166:33:@500.4]
  wire  is_nxt_pending; // @[NV_NVDLA_CSC_sg.scala 169:37:@505.4]
  wire [1:0] _T_211; // @[NV_NVDLA_CSC_sg.scala 170:90:@506.4]
  wire [1:0] _T_212; // @[NV_NVDLA_CSC_sg.scala 170:55:@507.4]
  wire  is_conv; // @[NV_NVDLA_CSC_sg.scala 204:40:@554.4]
  wire  is_img; // @[NV_NVDLA_CSC_sg.scala 205:26:@555.4]
  wire  _T_267; // @[NV_NVDLA_CSC_sg.scala 206:27:@556.4]
  wire  is_dc; // @[NV_NVDLA_CSC_sg.scala 206:25:@557.4]
  wire [2:0] cur_mode; // @[Cat.scala 30:58:@559.4]
  wire  is_mode_change; // @[NV_NVDLA_CSC_sg.scala 171:37:@510.4]
  wire  _T_215; // @[NV_NVDLA_CSC_sg.scala 173:53:@511.4]
  wire  _T_216; // @[NV_NVDLA_CSC_sg.scala 173:39:@512.4]
  reg  _T_219; // @[NV_NVDLA_CSC_sg.scala 173:30:@513.4]
  reg [31:0] _RAND_17;
  wire  _T_220; // @[NV_NVDLA_CSC_sg.scala 174:43:@516.4]
  wire  _T_222; // @[NV_NVDLA_CSC_sg.scala 174:85:@517.4]
  wire  _T_224; // @[NV_NVDLA_CSC_sg.scala 174:83:@518.4]
  wire  _T_225; // @[NV_NVDLA_CSC_sg.scala 174:27:@519.4]
  wire  _T_229; // @[NV_NVDLA_CSC_sg.scala 175:64:@522.4]
  wire  _T_230; // @[NV_NVDLA_CSC_sg.scala 175:26:@523.4]
  wire  _T_231; // @[NV_NVDLA_CSC_sg.scala 176:39:@525.4]
  wire  _T_235; // @[NV_NVDLA_CSC_sg.scala 176:79:@527.4]
  wire  _T_236; // @[NV_NVDLA_CSC_sg.scala 176:27:@528.4]
  wire  _T_237; // @[NV_NVDLA_CSC_sg.scala 177:38:@530.4]
  wire  _T_241; // @[NV_NVDLA_CSC_sg.scala 177:77:@532.4]
  wire  _T_242; // @[NV_NVDLA_CSC_sg.scala 177:26:@533.4]
  reg [13:0] last_slices; // @[NV_NVDLA_CSC_sg.scala 186:30:@538.4]
  reg [31:0] _RAND_18;
  reg [13:0] slice_left; // @[NV_NVDLA_CSC_sg.scala 187:29:@540.4]
  reg [31:0] _RAND_19;
  reg [13:0] last_kernels; // @[NV_NVDLA_CSC_sg.scala 188:31:@542.4]
  reg [31:0] _RAND_20;
  reg  last_skip_weight_rls; // @[NV_NVDLA_CSC_sg.scala 189:39:@543.4]
  reg [31:0] _RAND_21;
  wire [13:0] _T_264; // @[NV_NVDLA_CSC_sg.scala 195:49:@548.6]
  wire [4:0] _GEN_11; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  wire [4:0] _GEN_12; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  wire [13:0] _GEN_13; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  wire [13:0] _GEN_14; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  wire  _GEN_15; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  wire [2:0] _GEN_16; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  reg [13:0] data_in_height; // @[NV_NVDLA_CSC_sg.scala 212:33:@562.4]
  reg [31:0] _RAND_22;
  reg [21:0] data_out_atomic; // @[NV_NVDLA_CSC_sg.scala 213:34:@564.4]
  reg [31:0] _RAND_23;
  reg [4:0] weight_width_cmp; // @[NV_NVDLA_CSC_sg.scala 215:35:@568.4]
  reg [31:0] _RAND_24;
  reg [4:0] weight_height_cmp; // @[NV_NVDLA_CSC_sg.scala 216:36:@570.4]
  reg [31:0] _RAND_25;
  reg [13:0] weight_channel; // @[NV_NVDLA_CSC_sg.scala 217:33:@572.4]
  reg [31:0] _RAND_26;
  reg [9:0] weight_groups; // @[NV_NVDLA_CSC_sg.scala 218:32:@574.4]
  reg [31:0] _RAND_27;
  reg [2:0] weight_r_add; // @[NV_NVDLA_CSC_sg.scala 219:31:@575.4]
  reg [31:0] _RAND_28;
  reg [2:0] weight_r_last; // @[NV_NVDLA_CSC_sg.scala 220:32:@576.4]
  reg [31:0] _RAND_29;
  reg [13:0] rls_slices; // @[NV_NVDLA_CSC_sg.scala 221:29:@578.4]
  reg [31:0] _RAND_30;
  reg  is_img_d1; // @[NV_NVDLA_CSC_sg.scala 222:28:@579.4]
  reg [31:0] _RAND_31;
  reg [3:0] lower_limit; // @[NV_NVDLA_CSC_sg.scala 223:30:@580.4]
  reg [31:0] _RAND_32;
  reg [4:0] upper_limit; // @[NV_NVDLA_CSC_sg.scala 224:30:@581.4]
  reg [31:0] _RAND_33;
  wire [8:0] _T_330; // @[NV_NVDLA_CSC_sg.scala 226:44:@582.4]
  wire [2:0] weight_r_add_w; // @[NV_NVDLA_CSC_sg.scala 226:69:@583.4]
  wire [13:0] _T_332; // @[NV_NVDLA_CSC_sg.scala 229:55:@585.6]
  wire [13:0] _T_334; // @[NV_NVDLA_CSC_sg.scala 230:64:@587.6]
  wire [21:0] _T_336; // @[NV_NVDLA_CSC_sg.scala 230:90:@588.6]
  wire [21:0] _T_337; // @[NV_NVDLA_CSC_sg.scala 230:31:@589.6]
  wire [4:0] _T_340; // @[NV_NVDLA_CSC_sg.scala 232:32:@592.6]
  wire [13:0] _T_342; // @[NV_NVDLA_CSC_sg.scala 234:56:@595.6]
  wire [9:0] _T_343; // @[NV_NVDLA_CSC_sg.scala 235:50:@597.6]
  wire [10:0] _T_345; // @[NV_NVDLA_CSC_sg.scala 235:72:@598.6]
  wire [9:0] _T_346; // @[NV_NVDLA_CSC_sg.scala 235:79:@599.6]
  wire  _T_347; // @[NV_NVDLA_CSC_sg.scala 237:44:@602.6]
  wire  _T_349; // @[NV_NVDLA_CSC_sg.scala 238:44:@603.6]
  wire  _T_351; // @[NV_NVDLA_CSC_sg.scala 238:98:@604.6]
  wire [1:0] _T_352; // @[Cat.scala 30:58:@605.6]
  wire [1:0] _T_353; // @[NV_NVDLA_CSC_sg.scala 239:55:@606.6]
  wire [1:0] _T_354; // @[NV_NVDLA_CSC_sg.scala 238:29:@607.6]
  wire [1:0] _T_355; // @[NV_NVDLA_CSC_sg.scala 237:29:@608.6]
  wire [12:0] _T_357; // @[NV_NVDLA_CSC_sg.scala 240:44:@610.6]
  wire [11:0] _T_358; // @[NV_NVDLA_CSC_sg.scala 240:44:@611.6]
  wire [12:0] _GEN_62; // @[NV_NVDLA_CSC_sg.scala 241:116:@614.6]
  wire [13:0] _T_361; // @[NV_NVDLA_CSC_sg.scala 241:116:@614.6]
  wire [13:0] _T_362; // @[NV_NVDLA_CSC_sg.scala 241:116:@615.6]
  wire [13:0] _T_363; // @[NV_NVDLA_CSC_sg.scala 241:26:@616.6]
  wire [4:0] _T_366; // @[NV_NVDLA_CSC_sg.scala 245:27:@619.6]
  wire [13:0] _GEN_17; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [21:0] _GEN_18; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [4:0] _GEN_20; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [4:0] _GEN_21; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [13:0] _GEN_22; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [9:0] _GEN_23; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [2:0] _GEN_24; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [2:0] _GEN_25; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [13:0] _GEN_26; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [13:0] _GEN_27; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire  _GEN_28; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [4:0] _GEN_29; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  wire [4:0] _GEN_30; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  reg [13:0] slices_avl; // @[NV_NVDLA_CSC_sg.scala 340:61:@737.4]
  reg [31:0] _RAND_34;
  wire  dat_cbuf_ready; // @[NV_NVDLA_CSC_sg.scala 344:38:@740.4]
  reg [13:0] required_kernels; // @[NV_NVDLA_CSC_sg.scala 341:35:@738.4]
  reg [31:0] _RAND_35;
  reg [9:0] group_up_cnt; // @[NV_NVDLA_CSC_sg.scala 262:31:@634.4]
  reg [31:0] _RAND_36;
  wire [10:0] _T_385; // @[NV_NVDLA_CSC_sg.scala 263:41:@635.4]
  wire [9:0] group_up_cnt_inc; // @[NV_NVDLA_CSC_sg.scala 263:41:@636.4]
  wire  is_last_group; // @[NV_NVDLA_CSC_sg.scala 264:40:@637.4]
  wire  _T_387; // @[NV_NVDLA_CSC_sg.scala 265:26:@639.4]
  wire [2:0] _T_389; // @[NV_NVDLA_CSC_sg.scala 265:87:@640.4]
  wire [3:0] _T_391; // @[NV_NVDLA_CSC_sg.scala 265:110:@641.4]
  wire [3:0] cur_kernel; // @[NV_NVDLA_CSC_sg.scala 265:25:@642.4]
  wire [13:0] _GEN_63; // @[NV_NVDLA_CSC_sg.scala 345:49:@741.4]
  wire [14:0] _T_504; // @[NV_NVDLA_CSC_sg.scala 345:49:@741.4]
  wire [13:0] required_kernels_inc; // @[NV_NVDLA_CSC_sg.scala 345:49:@742.4]
  reg [14:0] kernels_avl; // @[NV_NVDLA_CSC_sg.scala 342:62:@739.4]
  reg [31:0] _RAND_37;
  wire [14:0] _GEN_64; // @[NV_NVDLA_CSC_sg.scala 346:46:@743.4]
  wire  wt_cbuf_ready; // @[NV_NVDLA_CSC_sg.scala 346:46:@743.4]
  wire  cbuf_ready; // @[NV_NVDLA_CSC_sg.scala 357:37:@755.4]
  wire  _T_513; // @[NV_NVDLA_CSC_sg.scala 358:30:@756.4]
  wire  _T_514; // @[NV_NVDLA_CSC_sg.scala 358:45:@757.4]
  wire  _T_515; // @[NV_NVDLA_CSC_sg.scala 358:43:@758.4]
  wire  dat_push_ready; // @[NV_NVDLA_CSC_sg.scala 353:30:@752.4 NV_NVDLA_CSC_sg.scala 448:20:@881.4]
  wire  wt_push_ready; // @[NV_NVDLA_CSC_sg.scala 354:29:@753.4 NV_NVDLA_CSC_sg.scala 462:19:@892.4]
  wire  fifo_push_ready; // @[NV_NVDLA_CSC_sg.scala 356:42:@754.4]
  wire  _T_517; // @[NV_NVDLA_CSC_sg.scala 358:69:@760.4]
  wire  pkg_adv; // @[NV_NVDLA_CSC_sg.scala 358:57:@761.4]
  reg [4:0] weight_s_up_cnt; // @[NV_NVDLA_CSC_sg.scala 313:34:@708.4]
  reg [31:0] _RAND_38;
  wire  is_last_s; // @[NV_NVDLA_CSC_sg.scala 320:38:@716.4]
  reg [4:0] weight_r_up_cnt; // @[NV_NVDLA_CSC_sg.scala 314:34:@710.4]
  reg [31:0] _RAND_39;
  wire [4:0] _GEN_65; // @[NV_NVDLA_CSC_sg.scala 319:47:@715.4]
  wire [5:0] weight_r_up_cnt_inc; // @[NV_NVDLA_CSC_sg.scala 319:47:@715.4]
  wire [5:0] _GEN_66; // @[NV_NVDLA_CSC_sg.scala 321:42:@717.4]
  wire  is_last_r; // @[NV_NVDLA_CSC_sg.scala 321:42:@717.4]
  wire  is_last_block; // @[NV_NVDLA_CSC_sg.scala 327:35:@723.4]
  wire  _T_530; // @[NV_NVDLA_CSC_sg.scala 365:28:@780.4]
  reg [13:0] channel_up_cnt; // @[NV_NVDLA_CSC_sg.scala 302:33:@691.4]
  reg [31:0] _RAND_40;
  wire [14:0] _T_445; // @[NV_NVDLA_CSC_sg.scala 303:45:@692.4]
  wire [13:0] channel_up_cnt_inc; // @[NV_NVDLA_CSC_sg.scala 303:45:@693.4]
  wire  is_last_channel; // @[NV_NVDLA_CSC_sg.scala 304:47:@694.4]
  wire  _T_531; // @[NV_NVDLA_CSC_sg.scala 365:44:@781.4]
  reg [21:0] stripe_up_cnt; // @[NV_NVDLA_CSC_sg.scala 283:32:@663.4]
  reg [31:0] _RAND_41;
  wire [21:0] _GEN_67; // @[NV_NVDLA_CSC_sg.scala 285:47:@667.4]
  wire [22:0] _T_421; // @[NV_NVDLA_CSC_sg.scala 285:47:@667.4]
  wire [21:0] stripe_up_cnt_1x_inc; // @[NV_NVDLA_CSC_sg.scala 285:47:@668.4]
  wire  is_stripe_le_1x; // @[NV_NVDLA_CSC_sg.scala 287:49:@670.4]
  wire  _T_532; // @[NV_NVDLA_CSC_sg.scala 365:62:@782.4]
  wire  _T_402; // @[NV_NVDLA_CSC_sg.scala 275:24:@651.4]
  reg [12:0] dataout_h_up_cnt; // @[NV_NVDLA_CSC_sg.scala 272:35:@649.4]
  reg [31:0] _RAND_42;
  wire  _T_403; // @[NV_NVDLA_CSC_sg.scala 275:55:@652.4]
  wire  is_last_do_h; // @[NV_NVDLA_CSC_sg.scala 275:35:@653.4]
  wire  _T_533; // @[NV_NVDLA_CSC_sg.scala 365:79:@783.4]
  wire  op_layer_en; // @[NV_NVDLA_CSC_sg.scala 365:94:@784.4]
  wire  _T_372; // @[NV_NVDLA_CSC_sg.scala 256:19:@626.4]
  wire  _T_375; // @[NV_NVDLA_CSC_sg.scala 257:48:@628.6]
  wire  _T_376; // @[NV_NVDLA_CSC_sg.scala 257:25:@629.6]
  wire  _GEN_31; // @[NV_NVDLA_CSC_sg.scala 256:33:@627.4]
  wire  _T_392; // @[NV_NVDLA_CSC_sg.scala 267:19:@643.4]
  wire [9:0] _T_394; // @[NV_NVDLA_CSC_sg.scala 268:27:@645.6]
  wire [9:0] _GEN_32; // @[NV_NVDLA_CSC_sg.scala 267:33:@644.4]
  wire  _T_522; // @[NV_NVDLA_CSC_sg.scala 363:29:@770.4]
  wire  _T_523; // @[NV_NVDLA_CSC_sg.scala 363:39:@771.4]
  wire  _T_524; // @[NV_NVDLA_CSC_sg.scala 363:55:@772.4]
  wire  op_do_h_en; // @[NV_NVDLA_CSC_sg.scala 363:73:@773.4]
  wire  _T_404; // @[NV_NVDLA_CSC_sg.scala 276:19:@654.4]
  wire [13:0] _T_408; // @[NV_NVDLA_CSC_sg.scala 279:47:@656.6]
  wire [12:0] _T_409; // @[NV_NVDLA_CSC_sg.scala 279:47:@657.6]
  wire [12:0] _T_410; // @[NV_NVDLA_CSC_sg.scala 278:32:@658.6]
  wire [12:0] _T_411; // @[NV_NVDLA_CSC_sg.scala 277:32:@659.6]
  wire [12:0] _GEN_33; // @[NV_NVDLA_CSC_sg.scala 276:32:@655.4]
  wire [5:0] _T_419; // @[Cat.scala 30:58:@664.4]
  wire [21:0] _GEN_68; // @[NV_NVDLA_CSC_sg.scala 284:46:@665.4]
  wire [22:0] _T_420; // @[NV_NVDLA_CSC_sg.scala 284:46:@665.4]
  wire [21:0] stripe_up_cnt_2x_inc; // @[NV_NVDLA_CSC_sg.scala 284:46:@666.4]
  wire  is_stripe_be_2x; // @[NV_NVDLA_CSC_sg.scala 286:49:@669.4]
  wire [22:0] _T_422; // @[NV_NVDLA_CSC_sg.scala 289:43:@671.4]
  wire [22:0] _T_423; // @[NV_NVDLA_CSC_sg.scala 289:43:@672.4]
  wire [21:0] _T_424; // @[NV_NVDLA_CSC_sg.scala 289:43:@673.4]
  wire [6:0] cur_stripe_inc; // @[NV_NVDLA_CSC_sg.scala 289:60:@674.4]
  wire [6:0] _T_425; // @[NV_NVDLA_CSC_sg.scala 290:59:@675.4]
  wire [6:0] cur_stripe; // @[NV_NVDLA_CSC_sg.scala 290:25:@676.4]
  wire  _T_427; // @[NV_NVDLA_CSC_sg.scala 293:19:@678.4]
  wire [21:0] _GEN_70; // @[NV_NVDLA_CSC_sg.scala 297:41:@682.6]
  wire [22:0] _T_432; // @[NV_NVDLA_CSC_sg.scala 297:41:@682.6]
  wire [21:0] _T_433; // @[NV_NVDLA_CSC_sg.scala 297:41:@683.6]
  wire [21:0] _T_434; // @[NV_NVDLA_CSC_sg.scala 296:29:@684.6]
  wire [21:0] _T_435; // @[NV_NVDLA_CSC_sg.scala 295:29:@685.6]
  wire [21:0] _T_436; // @[NV_NVDLA_CSC_sg.scala 294:29:@686.6]
  wire [21:0] _GEN_34; // @[NV_NVDLA_CSC_sg.scala 293:34:@679.4]
  wire  _T_446; // @[NV_NVDLA_CSC_sg.scala 306:27:@695.4]
  wire [2:0] _T_448; // @[NV_NVDLA_CSC_sg.scala 306:93:@696.4]
  wire [3:0] _T_450; // @[NV_NVDLA_CSC_sg.scala 306:117:@697.4]
  wire [6:0] cur_channel; // @[NV_NVDLA_CSC_sg.scala 306:26:@698.4]
  wire  _T_451; // @[NV_NVDLA_CSC_sg.scala 308:19:@699.4]
  wire [13:0] _T_462; // @[NV_NVDLA_CSC_sg.scala 309:63:@703.6]
  wire [13:0] _T_463; // @[NV_NVDLA_CSC_sg.scala 309:30:@704.6]
  wire [13:0] _GEN_35; // @[NV_NVDLA_CSC_sg.scala 308:35:@700.4]
  wire [5:0] _T_479; // @[NV_NVDLA_CSC_sg.scala 318:47:@713.4]
  wire [4:0] weight_s_up_cnt_inc; // @[NV_NVDLA_CSC_sg.scala 318:47:@714.4]
  wire  _T_480; // @[NV_NVDLA_CSC_sg.scala 323:33:@718.4]
  wire  _T_482; // @[NV_NVDLA_CSC_sg.scala 324:33:@719.4]
  wire [1:0] _T_485; // @[NV_NVDLA_CSC_sg.scala 324:20:@720.4]
  wire [1:0] _T_486; // @[NV_NVDLA_CSC_sg.scala 323:20:@721.4]
  wire [2:0] cur_r; // @[NV_NVDLA_CSC_sg.scala 322:20:@722.4]
  wire  _T_487; // @[NV_NVDLA_CSC_sg.scala 329:19:@724.4]
  wire [4:0] _T_490; // @[NV_NVDLA_CSC_sg.scala 331:32:@726.6]
  wire [4:0] _T_491; // @[NV_NVDLA_CSC_sg.scala 330:31:@727.6]
  wire [4:0] _GEN_36; // @[NV_NVDLA_CSC_sg.scala 329:29:@725.4]
  wire  op_r_en; // @[NV_NVDLA_CSC_sg.scala 360:24:@763.4]
  wire  _T_492; // @[NV_NVDLA_CSC_sg.scala 334:19:@730.4]
  wire [4:0] _T_495; // @[NV_NVDLA_CSC_sg.scala 337:48:@732.6]
  wire [4:0] _T_496; // @[NV_NVDLA_CSC_sg.scala 336:32:@733.6]
  wire [4:0] _T_497; // @[NV_NVDLA_CSC_sg.scala 335:31:@734.6]
  wire [4:0] _GEN_37; // @[NV_NVDLA_CSC_sg.scala 334:29:@731.4]
  wire  _T_506; // @[NV_NVDLA_CSC_sg.scala 349:43:@746.6]
  wire  _T_507; // @[NV_NVDLA_CSC_sg.scala 349:61:@747.6]
  wire  _T_508; // @[NV_NVDLA_CSC_sg.scala 349:59:@748.6]
  wire [13:0] _T_510; // @[NV_NVDLA_CSC_sg.scala 349:32:@749.6]
  wire [13:0] _GEN_38; // @[NV_NVDLA_CSC_sg.scala 348:33:@745.4]
  wire  _T_535; // @[NV_NVDLA_CSC_sg.scala 367:20:@786.4]
  wire  _T_538; // @[NV_NVDLA_CSC_sg.scala 367:57:@788.4]
  wire  _T_541; // @[NV_NVDLA_CSC_sg.scala 367:83:@789.4]
  wire  _T_542; // @[NV_NVDLA_CSC_sg.scala 367:45:@790.4]
  wire  _T_543; // @[NV_NVDLA_CSC_sg.scala 367:19:@791.4]
  reg [1:0] pkg_idx; // @[NV_NVDLA_CSC_sg.scala 370:26:@794.4]
  reg [31:0] _RAND_43;
  reg [4:0] dat_pkg_w_offset; // @[NV_NVDLA_CSC_sg.scala 371:35:@795.4]
  reg [31:0] _RAND_44;
  reg [4:0] dat_pkg_h_offset; // @[NV_NVDLA_CSC_sg.scala 372:35:@796.4]
  reg [31:0] _RAND_45;
  reg [6:0] dat_pkg_channel_size; // @[NV_NVDLA_CSC_sg.scala 373:39:@797.4]
  reg [31:0] _RAND_46;
  reg [6:0] dat_pkg_stripe_length; // @[NV_NVDLA_CSC_sg.scala 374:40:@798.4]
  reg [31:0] _RAND_47;
  reg [2:0] dat_pkg_cur_sub_h; // @[NV_NVDLA_CSC_sg.scala 375:36:@799.4]
  reg [31:0] _RAND_48;
  reg  dat_pkg_block_end; // @[NV_NVDLA_CSC_sg.scala 376:36:@800.4]
  reg [31:0] _RAND_49;
  reg  dat_pkg_channel_end; // @[NV_NVDLA_CSC_sg.scala 377:38:@801.4]
  reg [31:0] _RAND_50;
  reg  dat_pkg_group_end; // @[NV_NVDLA_CSC_sg.scala 378:36:@802.4]
  reg [31:0] _RAND_51;
  reg  dat_pkg_layer_end; // @[NV_NVDLA_CSC_sg.scala 379:36:@803.4]
  reg [31:0] _RAND_52;
  reg  dat_pkg_dat_release; // @[NV_NVDLA_CSC_sg.scala 380:38:@804.4]
  reg [31:0] _RAND_53;
  wire [2:0] _T_572; // @[NV_NVDLA_CSC_sg.scala 382:61:@805.4]
  wire [1:0] _T_573; // @[NV_NVDLA_CSC_sg.scala 382:61:@806.4]
  wire [1:0] pkg_idx_w; // @[NV_NVDLA_CSC_sg.scala 382:24:@807.4]
  wire  pkg_channel_end_w; // @[NV_NVDLA_CSC_sg.scala 386:43:@808.4]
  wire  _T_575; // @[NV_NVDLA_CSC_sg.scala 387:59:@810.4]
  wire  pkg_group_end_w; // @[NV_NVDLA_CSC_sg.scala 387:76:@811.4]
  wire  pkg_layer_end_w; // @[NV_NVDLA_CSC_sg.scala 388:91:@815.4]
  wire [1:0] _GEN_39; // @[NV_NVDLA_CSC_sg.scala 390:29:@817.4]
  wire  _T_585; // @[NV_NVDLA_CSC_sg.scala 403:32:@833.6]
  wire  _T_586; // @[NV_NVDLA_CSC_sg.scala 403:57:@834.6]
  wire [4:0] _GEN_40; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  wire [4:0] _GEN_41; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  wire [6:0] _GEN_42; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  wire [6:0] _GEN_43; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  wire [2:0] _GEN_44; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  wire  _GEN_45; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  wire  _GEN_46; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  wire  _GEN_47; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  wire  _GEN_48; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  wire  _GEN_49; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  reg [6:0] wt_pkg_kernel_size; // @[NV_NVDLA_CSC_sg.scala 407:37:@837.4]
  reg [31:0] _RAND_54;
  reg [6:0] wt_pkg_weight_size; // @[NV_NVDLA_CSC_sg.scala 408:37:@838.4]
  reg [31:0] _RAND_55;
  reg [2:0] wt_pkg_cur_sub_h; // @[NV_NVDLA_CSC_sg.scala 409:35:@839.4]
  reg [31:0] _RAND_56;
  reg  wt_pkg_wt_release; // @[NV_NVDLA_CSC_sg.scala 410:36:@840.4]
  reg [31:0] _RAND_57;
  wire [1:0] _T_595; // @[NV_NVDLA_CSC_sg.scala 413:42:@841.4]
  wire [30:0] dat_pkg_pd; // @[Cat.scala 30:58:@854.4]
  wire [32:0] dat_push_data; // @[Cat.scala 30:58:@855.4]
  wire  _T_609; // @[NV_NVDLA_CSC_sg.scala 423:57:@861.6]
  wire [6:0] _GEN_50; // @[NV_NVDLA_CSC_sg.scala 419:18:@856.4]
  wire [6:0] _GEN_51; // @[NV_NVDLA_CSC_sg.scala 419:18:@856.4]
  wire [2:0] _GEN_52; // @[NV_NVDLA_CSC_sg.scala 419:18:@856.4]
  wire  _GEN_53; // @[NV_NVDLA_CSC_sg.scala 419:18:@856.4]
  wire [1:0] _T_610; // @[NV_NVDLA_CSC_sg.scala 431:41:@864.4]
  wire [5:0] _T_611; // @[NV_NVDLA_CSC_sg.scala 431:67:@865.4]
  wire [17:0] wt_pkg_pd; // @[Cat.scala 30:58:@870.4]
  wire [19:0] wt_push_data; // @[Cat.scala 30:58:@871.4]
  wire [17:0] wt_pop_pd; // @[NV_NVDLA_CSC_sg.scala 478:32:@900.4]
  wire [6:0] sg2dat_stripe_length; // @[NV_NVDLA_CSC_sg.scala 485:42:@904.4]
  wire [5:0] sg2wt_kernel_size; // @[NV_NVDLA_CSC_sg.scala 495:38:@913.4]
  wire  _T_623; // @[NV_NVDLA_CSC_sg.scala 506:61:@922.4]
  wire [7:0] _T_625; // @[NV_NVDLA_CSC_sg.scala 506:106:@923.4]
  wire [7:0] _T_627; // @[NV_NVDLA_CSC_sg.scala 506:127:@924.4]
  wire [6:0] _T_628; // @[NV_NVDLA_CSC_sg.scala 506:147:@925.4]
  wire  _T_630; // @[NV_NVDLA_CSC_sg.scala 507:61:@926.4]
  wire [7:0] _T_632; // @[NV_NVDLA_CSC_sg.scala 507:106:@927.4]
  wire [7:0] _T_634; // @[NV_NVDLA_CSC_sg.scala 507:127:@928.4]
  wire [6:0] _T_635; // @[NV_NVDLA_CSC_sg.scala 507:147:@929.4]
  wire [6:0] _T_636; // @[NV_NVDLA_CSC_sg.scala 507:38:@930.4]
  wire [6:0] _T_637; // @[NV_NVDLA_CSC_sg.scala 506:38:@931.4]
  wire [6:0] dat_stripe_img_length_w; // @[NV_NVDLA_CSC_sg.scala 505:38:@932.4]
  wire [6:0] dat_stripe_length_w; // @[NV_NVDLA_CSC_sg.scala 510:34:@933.4]
  reg  wt_pop_ready_d1; // @[NV_NVDLA_CSC_sg.scala 513:34:@934.4]
  reg [31:0] _RAND_58;
  reg [6:0] dat_stripe_length; // @[NV_NVDLA_CSC_sg.scala 514:36:@935.4]
  reg [31:0] _RAND_59;
  reg  sg2dl_pvld_out; // @[NV_NVDLA_CSC_sg.scala 516:33:@937.4]
  reg [31:0] _RAND_60;
  reg [30:0] sg2dl_pd_out; // @[NV_NVDLA_CSC_sg.scala 517:31:@938.4]
  reg [31:0] _RAND_61;
  reg  sg2wl_pvld_out; // @[NV_NVDLA_CSC_sg.scala 518:33:@939.4]
  reg [31:0] _RAND_62;
  reg [17:0] sg2wl_pd_out; // @[NV_NVDLA_CSC_sg.scala 519:31:@940.4]
  reg [31:0] _RAND_63;
  wire  _T_653; // @[NV_NVDLA_CSC_sg.scala 522:30:@942.4]
  wire  _T_656; // @[NV_NVDLA_CSC_sg.scala 523:49:@943.4]
  wire [6:0] _T_658; // @[NV_NVDLA_CSC_sg.scala 523:29:@944.4]
  wire [6:0] dat_max_cycles; // @[NV_NVDLA_CSC_sg.scala 522:29:@945.4]
  wire  _T_692; // @[NV_NVDLA_CSC_sg.scala 534:54:@972.4]
  wire  _T_693; // @[NV_NVDLA_CSC_sg.scala 534:85:@973.4]
  wire  _T_694; // @[NV_NVDLA_CSC_sg.scala 534:70:@974.4]
  wire  wt_pop_ready; // @[NV_NVDLA_CSC_sg.scala 534:32:@975.4]
  wire  _T_659; // @[NV_NVDLA_CSC_sg.scala 527:29:@946.4]
  wire  _T_662; // @[NV_NVDLA_CSC_sg.scala 528:41:@947.4]
  wire  _T_664; // @[NV_NVDLA_CSC_sg.scala 528:73:@948.4]
  wire  _T_665; // @[NV_NVDLA_CSC_sg.scala 528:62:@949.4]
  wire  _T_667; // @[NV_NVDLA_CSC_sg.scala 529:41:@950.4]
  wire [5:0] _T_668; // @[NV_NVDLA_CSC_sg.scala 529:29:@951.4]
  wire [5:0] _T_669; // @[NV_NVDLA_CSC_sg.scala 528:29:@952.4]
  wire [5:0] wt_max_cycles; // @[NV_NVDLA_CSC_sg.scala 527:28:@953.4]
  wire [6:0] _GEN_71; // @[NV_NVDLA_CSC_sg.scala 531:41:@954.4]
  wire  _T_670; // @[NV_NVDLA_CSC_sg.scala 531:41:@954.4]
  wire [7:0] _T_672; // @[NV_NVDLA_CSC_sg.scala 531:76:@955.4]
  wire [7:0] _T_673; // @[NV_NVDLA_CSC_sg.scala 531:76:@956.4]
  wire [6:0] _T_674; // @[NV_NVDLA_CSC_sg.scala 531:76:@957.4]
  wire [6:0] _T_676; // @[Cat.scala 30:58:@958.4]
  wire [7:0] _T_678; // @[NV_NVDLA_CSC_sg.scala 531:109:@959.4]
  wire [7:0] _T_679; // @[NV_NVDLA_CSC_sg.scala 531:109:@960.4]
  wire [6:0] _T_680; // @[NV_NVDLA_CSC_sg.scala 531:109:@961.4]
  wire [6:0] _T_681; // @[NV_NVDLA_CSC_sg.scala 531:25:@962.4]
  wire [5:0] max_cycles; // @[NV_NVDLA_CSC_sg.scala 531:116:@963.4]
  wire [6:0] _T_683; // @[NV_NVDLA_CSC_sg.scala 532:31:@964.4]
  wire [6:0] _T_684; // @[NV_NVDLA_CSC_sg.scala 532:31:@965.4]
  wire [5:0] pop_cnt_dec; // @[NV_NVDLA_CSC_sg.scala 532:31:@966.4]
  wire  _T_685; // @[NV_NVDLA_CSC_sg.scala 533:39:@967.4]
  wire [5:0] _T_689; // @[NV_NVDLA_CSC_sg.scala 533:70:@969.4]
  wire [5:0] pop_cnt_w; // @[NV_NVDLA_CSC_sg.scala 533:24:@970.4]
  wire [6:0] _GEN_54; // @[NV_NVDLA_CSC_sg.scala 539:26:@986.4]
  wire [6:0] _GEN_55; // @[NV_NVDLA_CSC_sg.scala 539:26:@986.4]
  wire [30:0] _GEN_56; // @[NV_NVDLA_CSC_sg.scala 545:24:@992.4]
  wire [17:0] _GEN_57; // @[NV_NVDLA_CSC_sg.scala 549:23:@996.4]
  reg  credit_vld; // @[NV_NVDLA_CSC_sg.scala 563:61:@1004.4]
  reg [31:0] _RAND_64;
  reg [2:0] credit_size; // @[NV_NVDLA_CSC_sg.scala 564:58:@1005.4]
  reg [31:0] _RAND_65;
  wire [3:0] credit_cnt_add; // @[NV_NVDLA_CSC_sg.scala 573:29:@1011.4]
  wire  _T_711; // @[NV_NVDLA_CSC_sg.scala 574:43:@1012.4]
  wire [8:0] credit_cnt_dec; // @[NV_NVDLA_CSC_sg.scala 574:29:@1013.4]
  wire  _T_716; // @[NV_NVDLA_CSC_sg.scala 577:24:@1018.4]
  wire [8:0] _GEN_72; // @[NV_NVDLA_CSC_sg.scala 578:34:@1020.6]
  wire [9:0] _T_717; // @[NV_NVDLA_CSC_sg.scala 578:34:@1020.6]
  wire [8:0] _T_718; // @[NV_NVDLA_CSC_sg.scala 578:34:@1021.6]
  wire [9:0] _T_719; // @[NV_NVDLA_CSC_sg.scala 578:51:@1022.6]
  wire [9:0] _T_720; // @[NV_NVDLA_CSC_sg.scala 578:51:@1023.6]
  wire [8:0] _T_721; // @[NV_NVDLA_CSC_sg.scala 578:51:@1024.6]
  wire [8:0] _GEN_59; // @[NV_NVDLA_CSC_sg.scala 577:37:@1019.4]
  wire  _T_722; // @[NV_NVDLA_CSC_sg.scala 584:31:@1027.4]
  wire  dat_release; // @[NV_NVDLA_CSC_sg.scala 584:49:@1029.4]
  wire  _T_724; // @[NV_NVDLA_CSC_sg.scala 585:37:@1030.4]
  wire  _T_725; // @[NV_NVDLA_CSC_sg.scala 585:58:@1031.4]
  wire  _T_726; // @[NV_NVDLA_CSC_sg.scala 585:80:@1032.4]
  wire  _T_727; // @[NV_NVDLA_CSC_sg.scala 585:55:@1033.4]
  wire  _T_729; // @[NV_NVDLA_CSC_sg.scala 585:113:@1034.4]
  wire  dat_reuse_release; // @[NV_NVDLA_CSC_sg.scala 585:98:@1035.4]
  wire [13:0] slices_avl_add; // @[NV_NVDLA_CSC_sg.scala 586:29:@1036.4]
  wire [13:0] _T_732; // @[NV_NVDLA_CSC_sg.scala 587:58:@1037.4]
  wire [13:0] slices_avl_sub; // @[NV_NVDLA_CSC_sg.scala 587:29:@1038.4]
  wire  _T_734; // @[NV_NVDLA_CSC_sg.scala 588:30:@1040.4]
  wire  wt_release; // @[NV_NVDLA_CSC_sg.scala 588:59:@1041.4]
  wire  _T_736; // @[NV_NVDLA_CSC_sg.scala 589:56:@1043.4]
  wire  _T_737; // @[NV_NVDLA_CSC_sg.scala 589:54:@1044.4]
  wire  wt_reuse_release; // @[NV_NVDLA_CSC_sg.scala 589:80:@1045.4]
  wire [13:0] kernels_avl_add; // @[NV_NVDLA_CSC_sg.scala 590:30:@1046.4]
  wire [10:0] _T_740; // @[Cat.scala 30:58:@1047.4]
  wire [13:0] _T_742; // @[NV_NVDLA_CSC_sg.scala 591:81:@1048.4]
  wire [13:0] kernels_avl_sub; // @[NV_NVDLA_CSC_sg.scala 591:30:@1049.4]
  wire  _T_743; // @[NV_NVDLA_CSC_sg.scala 593:26:@1050.4]
  wire  _T_744; // @[NV_NVDLA_CSC_sg.scala 593:40:@1051.4]
  wire  _T_745; // @[NV_NVDLA_CSC_sg.scala 593:60:@1052.4]
  wire [14:0] _T_747; // @[NV_NVDLA_CSC_sg.scala 594:75:@1054.6]
  wire [13:0] _T_748; // @[NV_NVDLA_CSC_sg.scala 594:75:@1055.6]
  wire [14:0] _T_749; // @[NV_NVDLA_CSC_sg.scala 594:92:@1056.6]
  wire [14:0] _T_750; // @[NV_NVDLA_CSC_sg.scala 594:92:@1057.6]
  wire [13:0] _T_751; // @[NV_NVDLA_CSC_sg.scala 594:92:@1058.6]
  wire [13:0] _T_752; // @[NV_NVDLA_CSC_sg.scala 594:26:@1059.6]
  wire [13:0] _GEN_60; // @[NV_NVDLA_CSC_sg.scala 593:88:@1053.4]
  wire  _T_753; // @[NV_NVDLA_CSC_sg.scala 596:25:@1062.4]
  wire  _T_754; // @[NV_NVDLA_CSC_sg.scala 596:38:@1063.4]
  wire  _T_755; // @[NV_NVDLA_CSC_sg.scala 596:57:@1064.4]
  wire [14:0] _GEN_73; // @[NV_NVDLA_CSC_sg.scala 597:75:@1066.6]
  wire [15:0] _T_757; // @[NV_NVDLA_CSC_sg.scala 597:75:@1066.6]
  wire [14:0] _T_758; // @[NV_NVDLA_CSC_sg.scala 597:75:@1067.6]
  wire [14:0] _GEN_74; // @[NV_NVDLA_CSC_sg.scala 597:93:@1068.6]
  wire [15:0] _T_759; // @[NV_NVDLA_CSC_sg.scala 597:93:@1068.6]
  wire [15:0] _T_760; // @[NV_NVDLA_CSC_sg.scala 597:93:@1069.6]
  wire [14:0] _T_761; // @[NV_NVDLA_CSC_sg.scala 597:93:@1070.6]
  wire [14:0] _T_762; // @[NV_NVDLA_CSC_sg.scala 597:27:@1071.6]
  wire [14:0] _GEN_61; // @[NV_NVDLA_CSC_sg.scala 596:84:@1065.4]
  reg  _T_765; // @[NV_NVDLA_CSC_sg.scala 600:66:@1074.4]
  reg [31:0] _RAND_66;
  reg  _T_768; // @[NV_NVDLA_CSC_sg.scala 601:66:@1077.4]
  reg [31:0] _RAND_67;
  NV_NVDLA_fifo_new u_dat_fifo ( // @[NV_NVDLA_CSC_sg.scala 442:28:@875.4]
    .clock(u_dat_fifo_clock),
    .reset(u_dat_fifo_reset),
    .io_clk(u_dat_fifo_io_clk),
    .io_wr_pvld(u_dat_fifo_io_wr_pvld),
    .io_wr_prdy(u_dat_fifo_io_wr_prdy),
    .io_wr_pd(u_dat_fifo_io_wr_pd),
    .io_wr_empty(u_dat_fifo_io_wr_empty),
    .io_rd_pvld(u_dat_fifo_io_rd_pvld),
    .io_rd_prdy(u_dat_fifo_io_rd_prdy),
    .io_rd_pd(u_dat_fifo_io_rd_pd)
  );
  NV_NVDLA_fifo_new_1 u_wt_fifo ( // @[NV_NVDLA_CSC_sg.scala 456:27:@886.4]
    .clock(u_wt_fifo_clock),
    .reset(u_wt_fifo_reset),
    .io_clk(u_wt_fifo_io_clk),
    .io_wr_pvld(u_wt_fifo_io_wr_pvld),
    .io_wr_prdy(u_wt_fifo_io_wr_prdy),
    .io_wr_pd(u_wt_fifo_io_wr_pd),
    .io_wr_empty(u_wt_fifo_io_wr_empty),
    .io_rd_pvld(u_wt_fifo_io_rd_pvld),
    .io_rd_prdy(u_wt_fifo_io_rd_prdy),
    .io_rd_pd(u_wt_fifo_io_rd_pd)
  );
  assign _T_134 = 2'h0 == cur_state; // @[Conditional.scala 37:30:@390.4]
  assign dat_bank_change = last_data_bank != io_reg2dp_data_bank; // @[NV_NVDLA_CSC_sg.scala 143:43:@464.4]
  assign wt_bank_change = last_weight_bank != io_reg2dp_weight_bank; // @[NV_NVDLA_CSC_sg.scala 144:44:@465.4]
  assign need_pending = dat_bank_change | wt_bank_change; // @[NV_NVDLA_CSC_sg.scala 145:38:@466.4]
  assign _T_135 = io_reg2dp_op_en & need_pending; // @[NV_NVDLA_CSC_sg.scala 99:31:@392.6]
  assign _GEN_0 = io_reg2dp_op_en ? 2'h2 : 2'h0; // @[NV_NVDLA_CSC_sg.scala 100:37:@397.8]
  assign _GEN_1 = _T_135 ? 2'h1 : _GEN_0; // @[NV_NVDLA_CSC_sg.scala 99:47:@393.6]
  assign _T_136 = 2'h1 == cur_state; // @[Conditional.scala 37:30:@405.6]
  assign is_pending = cur_state == 2'h1; // @[NV_NVDLA_CSC_sg.scala 165:30:@498.4]
  assign _T_179 = dat_pending_clr ^ dat_pending_req; // @[NV_NVDLA_CSC_sg.scala 146:51:@468.4]
  assign _T_180 = ~ _T_179; // @[NV_NVDLA_CSC_sg.scala 146:34:@469.4]
  assign _T_181 = is_pending & _T_180; // @[NV_NVDLA_CSC_sg.scala 146:32:@470.4]
  assign _T_182 = wt_pending_clr ^ wt_pending_req; // @[NV_NVDLA_CSC_sg.scala 146:87:@471.4]
  assign _T_183 = ~ _T_182; // @[NV_NVDLA_CSC_sg.scala 146:71:@472.4]
  assign pending_done = _T_181 & _T_183; // @[NV_NVDLA_CSC_sg.scala 146:69:@473.4]
  assign _GEN_2 = pending_done ? 2'h2 : 2'h1; // @[NV_NVDLA_CSC_sg.scala 104:29:@407.8]
  assign _T_137 = 2'h2 == cur_state; // @[Conditional.scala 37:30:@415.8]
  assign dat_pop_req = u_dat_fifo_io_rd_pvld; // @[NV_NVDLA_CSC_sg.scala 121:27:@438.4 NV_NVDLA_CSC_sg.scala 450:17:@883.4]
  assign _T_173 = ~ dat_pop_req; // @[NV_NVDLA_CSC_sg.scala 142:22:@458.4]
  assign wt_pop_req = u_wt_fifo_io_rd_pvld; // @[NV_NVDLA_CSC_sg.scala 124:26:@441.4 NV_NVDLA_CSC_sg.scala 464:16:@894.4]
  assign _T_174 = ~ wt_pop_req; // @[NV_NVDLA_CSC_sg.scala 142:37:@459.4]
  assign _T_175 = _T_173 & _T_174; // @[NV_NVDLA_CSC_sg.scala 142:35:@460.4]
  assign dat_push_empty = u_dat_fifo_io_wr_empty; // @[NV_NVDLA_CSC_sg.scala 122:30:@439.4 NV_NVDLA_CSC_sg.scala 454:20:@885.4]
  assign _T_176 = _T_175 & dat_push_empty; // @[NV_NVDLA_CSC_sg.scala 142:49:@461.4]
  assign wt_push_empty = u_wt_fifo_io_wr_empty; // @[NV_NVDLA_CSC_sg.scala 125:29:@442.4 NV_NVDLA_CSC_sg.scala 467:19:@896.4]
  assign fifo_is_clear = _T_176 & wt_push_empty; // @[NV_NVDLA_CSC_sg.scala 142:66:@462.4]
  assign _T_138 = layer_done & fifo_is_clear; // @[NV_NVDLA_CSC_sg.scala 108:26:@417.10]
  assign _T_139 = ~ pkg_vld; // @[NV_NVDLA_CSC_sg.scala 108:44:@418.10]
  assign _T_140 = _T_138 & _T_139; // @[NV_NVDLA_CSC_sg.scala 108:42:@419.10]
  assign _GEN_3 = _T_140 ? 2'h3 : 2'h2; // @[NV_NVDLA_CSC_sg.scala 108:54:@420.10]
  assign _T_141 = 2'h3 == cur_state; // @[Conditional.scala 37:30:@428.10]
  assign _GEN_4 = io_dp2reg_done ? 2'h0 : 2'h3; // @[NV_NVDLA_CSC_sg.scala 112:31:@430.12]
  assign _GEN_5 = _T_141 ? _GEN_4 : 2'h0; // @[Conditional.scala 39:67:@429.10]
  assign _GEN_6 = _T_137 ? _GEN_3 : _GEN_5; // @[Conditional.scala 39:67:@416.8]
  assign _GEN_7 = _T_136 ? _GEN_2 : _GEN_6; // @[Conditional.scala 39:67:@406.6]
  assign nxt_state = _T_134 ? _GEN_1 : _GEN_7; // @[Conditional.scala 40:58:@391.4]
  assign is_done = cur_state == 2'h3; // @[NV_NVDLA_CSC_sg.scala 167:27:@501.4]
  assign _T_185 = ~ is_done; // @[NV_NVDLA_CSC_sg.scala 149:26:@476.6]
  assign is_nxt_done = nxt_state == 2'h3; // @[NV_NVDLA_CSC_sg.scala 168:31:@503.4]
  assign _T_186 = _T_185 & is_nxt_done; // @[NV_NVDLA_CSC_sg.scala 149:35:@477.6]
  assign _T_188 = sg_dn_cnt - 8'h1; // @[NV_NVDLA_CSC_sg.scala 149:74:@478.6]
  assign _T_189 = $unsigned(_T_188); // @[NV_NVDLA_CSC_sg.scala 149:74:@479.6]
  assign _T_190 = _T_189[7:0]; // @[NV_NVDLA_CSC_sg.scala 149:74:@480.6]
  assign _T_191 = _T_186 ? flush_cycles : _T_190; // @[NV_NVDLA_CSC_sg.scala 149:25:@481.6]
  assign _GEN_9 = is_nxt_done ? _T_191 : sg_dn_cnt; // @[NV_NVDLA_CSC_sg.scala 148:22:@475.4]
  assign _T_697 = pop_cnt == 6'h0; // @[NV_NVDLA_CSC_sg.scala 535:45:@977.4]
  assign _T_698 = dat_pop_req & _T_697; // @[NV_NVDLA_CSC_sg.scala 535:34:@978.4]
  assign dat_pop_pd = u_dat_fifo_io_rd_pd[30:0]; // @[NV_NVDLA_CSC_sg.scala 474:34:@898.4]
  assign sg2dat_channel_end = dat_pop_pd[27]; // @[NV_NVDLA_CSC_sg.scala 488:40:@907.4]
  assign _T_713 = ~ sg2dat_channel_end; // @[NV_NVDLA_CSC_sg.scala 575:21:@1014.4]
  assign dat_impact_cnt = {2'h0,dat_stripe_size}; // @[Cat.scala 30:58:@1010.4]
  assign _T_714 = credit_cnt >= dat_impact_cnt; // @[NV_NVDLA_CSC_sg.scala 575:55:@1015.4]
  assign credit_ready = _T_713 | _T_714; // @[NV_NVDLA_CSC_sg.scala 575:41:@1016.4]
  assign _T_699 = _T_698 & credit_ready; // @[NV_NVDLA_CSC_sg.scala 535:54:@979.4]
  assign dat_pop_idx = u_dat_fifo_io_rd_pd[32:31]; // @[NV_NVDLA_CSC_sg.scala 472:35:@897.4]
  assign wt_pop_idx = u_wt_fifo_io_rd_pd[19:18]; // @[NV_NVDLA_CSC_sg.scala 476:33:@899.4]
  assign _T_700 = dat_pop_idx != wt_pop_idx; // @[NV_NVDLA_CSC_sg.scala 535:85:@980.4]
  assign _T_702 = _T_700 | _T_174; // @[NV_NVDLA_CSC_sg.scala 535:101:@982.4]
  assign dat_pop_ready = _T_699 & _T_702; // @[NV_NVDLA_CSC_sg.scala 535:69:@983.4]
  assign _T_192 = dat_pop_req & dat_pop_ready; // @[NV_NVDLA_CSC_sg.scala 153:22:@486.4]
  assign sg2dat_layer_end = dat_pop_pd[29]; // @[NV_NVDLA_CSC_sg.scala 490:35:@909.4]
  assign _T_193 = _T_192 & sg2dat_layer_end; // @[NV_NVDLA_CSC_sg.scala 153:38:@487.4]
  assign _T_195 = dat_stripe_size + 7'h30; // @[NV_NVDLA_CSC_sg.scala 154:41:@489.6]
  assign _GEN_10 = _T_193 ? _T_195 : flush_cycles; // @[NV_NVDLA_CSC_sg.scala 153:57:@488.4]
  assign _T_203 = cur_state == 2'h0; // @[NV_NVDLA_CSC_sg.scala 163:49:@495.4]
  assign layer_st = io_reg2dp_op_en & _T_203; // @[NV_NVDLA_CSC_sg.scala 163:36:@496.4]
  assign is_running = cur_state == 2'h2; // @[NV_NVDLA_CSC_sg.scala 166:33:@500.4]
  assign is_nxt_pending = nxt_state == 2'h1; // @[NV_NVDLA_CSC_sg.scala 169:37:@505.4]
  assign _T_211 = is_running ? 2'h2 : 2'h3; // @[NV_NVDLA_CSC_sg.scala 170:90:@506.4]
  assign _T_212 = is_pending ? 2'h1 : _T_211; // @[NV_NVDLA_CSC_sg.scala 170:55:@507.4]
  assign is_conv = io_reg2dp_conv_mode == 1'h0; // @[NV_NVDLA_CSC_sg.scala 204:40:@554.4]
  assign is_img = is_conv & io_reg2dp_datain_format; // @[NV_NVDLA_CSC_sg.scala 205:26:@555.4]
  assign _T_267 = ~ io_reg2dp_datain_format; // @[NV_NVDLA_CSC_sg.scala 206:27:@556.4]
  assign is_dc = is_conv & _T_267; // @[NV_NVDLA_CSC_sg.scala 206:25:@557.4]
  assign cur_mode = {is_img,1'h0,is_dc}; // @[Cat.scala 30:58:@559.4]
  assign is_mode_change = last_mode != cur_mode; // @[NV_NVDLA_CSC_sg.scala 171:37:@510.4]
  assign _T_215 = sg_dn_cnt == 8'h1; // @[NV_NVDLA_CSC_sg.scala 173:53:@511.4]
  assign _T_216 = is_done & _T_215; // @[NV_NVDLA_CSC_sg.scala 173:39:@512.4]
  assign _T_220 = is_nxt_pending & dat_bank_change; // @[NV_NVDLA_CSC_sg.scala 174:43:@516.4]
  assign _T_222 = ~ is_nxt_pending; // @[NV_NVDLA_CSC_sg.scala 174:85:@517.4]
  assign _T_224 = _T_222 ? 1'h0 : dat_pending_req; // @[NV_NVDLA_CSC_sg.scala 174:83:@518.4]
  assign _T_225 = _T_220 ? 1'h1 : _T_224; // @[NV_NVDLA_CSC_sg.scala 174:27:@519.4]
  assign _T_229 = _T_222 ? 1'h0 : wt_pending_req; // @[NV_NVDLA_CSC_sg.scala 175:64:@522.4]
  assign _T_230 = is_nxt_pending ? 1'h1 : _T_229; // @[NV_NVDLA_CSC_sg.scala 175:26:@523.4]
  assign _T_231 = is_pending & dat_pending_ack; // @[NV_NVDLA_CSC_sg.scala 176:39:@525.4]
  assign _T_235 = _T_222 ? 1'h0 : dat_pending_clr; // @[NV_NVDLA_CSC_sg.scala 176:79:@527.4]
  assign _T_236 = _T_231 ? 1'h1 : _T_235; // @[NV_NVDLA_CSC_sg.scala 176:27:@528.4]
  assign _T_237 = is_pending & wt_pending_ack; // @[NV_NVDLA_CSC_sg.scala 177:38:@530.4]
  assign _T_241 = _T_222 ? 1'h0 : wt_pending_clr; // @[NV_NVDLA_CSC_sg.scala 177:77:@532.4]
  assign _T_242 = _T_237 ? 1'h1 : _T_241; // @[NV_NVDLA_CSC_sg.scala 177:26:@533.4]
  assign _T_264 = io_reg2dp_weight_kernel + 13'h1; // @[NV_NVDLA_CSC_sg.scala 195:49:@548.6]
  assign _GEN_11 = io_dp2reg_done ? io_reg2dp_data_bank : last_data_bank; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  assign _GEN_12 = io_dp2reg_done ? io_reg2dp_weight_bank : last_weight_bank; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  assign _GEN_13 = io_dp2reg_done ? slice_left : last_slices; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  assign _GEN_14 = io_dp2reg_done ? _T_264 : last_kernels; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  assign _GEN_15 = io_dp2reg_done ? io_reg2dp_skip_weight_rls : last_skip_weight_rls; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  assign _GEN_16 = io_dp2reg_done ? cur_mode : last_mode; // @[NV_NVDLA_CSC_sg.scala 191:25:@544.4]
  assign _T_330 = 9'h9 << io_reg2dp_y_extension; // @[NV_NVDLA_CSC_sg.scala 226:44:@582.4]
  assign weight_r_add_w = _T_330[5:3]; // @[NV_NVDLA_CSC_sg.scala 226:69:@583.4]
  assign _T_332 = io_reg2dp_datain_height_ext + 13'h1; // @[NV_NVDLA_CSC_sg.scala 229:55:@585.6]
  assign _T_334 = io_reg2dp_dataout_width + 13'h1; // @[NV_NVDLA_CSC_sg.scala 230:64:@587.6]
  assign _T_336 = io_reg2dp_atomics + 21'h1; // @[NV_NVDLA_CSC_sg.scala 230:90:@588.6]
  assign _T_337 = is_img ? {{8'd0}, _T_334} : _T_336; // @[NV_NVDLA_CSC_sg.scala 230:31:@589.6]
  assign _T_340 = is_img ? 5'h0 : io_reg2dp_weight_width_ext; // @[NV_NVDLA_CSC_sg.scala 232:32:@592.6]
  assign _T_342 = io_reg2dp_weight_channel_ext + 13'h1; // @[NV_NVDLA_CSC_sg.scala 234:56:@595.6]
  assign _T_343 = io_reg2dp_weight_kernel[12:3]; // @[NV_NVDLA_CSC_sg.scala 235:50:@597.6]
  assign _T_345 = _T_343 + 10'h1; // @[NV_NVDLA_CSC_sg.scala 235:72:@598.6]
  assign _T_346 = _T_345[9:0]; // @[NV_NVDLA_CSC_sg.scala 235:79:@599.6]
  assign _T_347 = weight_r_add_w[0]; // @[NV_NVDLA_CSC_sg.scala 237:44:@602.6]
  assign _T_349 = weight_r_add_w[1]; // @[NV_NVDLA_CSC_sg.scala 238:44:@603.6]
  assign _T_351 = io_reg2dp_weight_height_ext[0]; // @[NV_NVDLA_CSC_sg.scala 238:98:@604.6]
  assign _T_352 = {1'h0,_T_351}; // @[Cat.scala 30:58:@605.6]
  assign _T_353 = io_reg2dp_weight_height_ext[1:0]; // @[NV_NVDLA_CSC_sg.scala 239:55:@606.6]
  assign _T_354 = _T_349 ? _T_352 : _T_353; // @[NV_NVDLA_CSC_sg.scala 238:29:@607.6]
  assign _T_355 = _T_347 ? 2'h0 : _T_354; // @[NV_NVDLA_CSC_sg.scala 237:29:@608.6]
  assign _T_357 = io_reg2dp_rls_slices + 12'h1; // @[NV_NVDLA_CSC_sg.scala 240:44:@610.6]
  assign _T_358 = io_reg2dp_rls_slices + 12'h1; // @[NV_NVDLA_CSC_sg.scala 240:44:@611.6]
  assign _GEN_62 = {{1'd0}, io_reg2dp_rls_slices}; // @[NV_NVDLA_CSC_sg.scala 241:116:@614.6]
  assign _T_361 = io_reg2dp_datain_height_ext - _GEN_62; // @[NV_NVDLA_CSC_sg.scala 241:116:@614.6]
  assign _T_362 = $unsigned(_T_361); // @[NV_NVDLA_CSC_sg.scala 241:116:@615.6]
  assign _T_363 = io_reg2dp_skip_data_rls ? _T_332 : _T_362; // @[NV_NVDLA_CSC_sg.scala 241:26:@616.6]
  assign _T_366 = is_img ? 5'h10 : 5'h8; // @[NV_NVDLA_CSC_sg.scala 245:27:@619.6]
  assign _GEN_17 = layer_st ? _T_332 : data_in_height; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_18 = layer_st ? _T_337 : data_out_atomic; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_20 = layer_st ? _T_340 : weight_width_cmp; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_21 = layer_st ? io_reg2dp_weight_height_ext : weight_height_cmp; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_22 = layer_st ? _T_342 : weight_channel; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_23 = layer_st ? _T_346 : weight_groups; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_24 = layer_st ? weight_r_add_w : weight_r_add; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_25 = layer_st ? {{1'd0}, _T_355} : weight_r_last; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_26 = layer_st ? {{2'd0}, _T_358} : rls_slices; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_27 = layer_st ? _T_363 : slice_left; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_28 = layer_st ? is_img : is_img_d1; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_29 = layer_st ? _T_366 : {{1'd0}, lower_limit}; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign _GEN_30 = layer_st ? 5'h10 : upper_limit; // @[NV_NVDLA_CSC_sg.scala 228:19:@584.4]
  assign dat_cbuf_ready = slices_avl >= data_in_height; // @[NV_NVDLA_CSC_sg.scala 344:38:@740.4]
  assign _T_385 = group_up_cnt + 10'h1; // @[NV_NVDLA_CSC_sg.scala 263:41:@635.4]
  assign group_up_cnt_inc = group_up_cnt + 10'h1; // @[NV_NVDLA_CSC_sg.scala 263:41:@636.4]
  assign is_last_group = group_up_cnt_inc == weight_groups; // @[NV_NVDLA_CSC_sg.scala 264:40:@637.4]
  assign _T_387 = ~ is_last_group; // @[NV_NVDLA_CSC_sg.scala 265:26:@639.4]
  assign _T_389 = io_reg2dp_weight_kernel[2:0]; // @[NV_NVDLA_CSC_sg.scala 265:87:@640.4]
  assign _T_391 = _T_389 + 3'h1; // @[NV_NVDLA_CSC_sg.scala 265:110:@641.4]
  assign cur_kernel = _T_387 ? 4'h8 : _T_391; // @[NV_NVDLA_CSC_sg.scala 265:25:@642.4]
  assign _GEN_63 = {{10'd0}, cur_kernel}; // @[NV_NVDLA_CSC_sg.scala 345:49:@741.4]
  assign _T_504 = required_kernels + _GEN_63; // @[NV_NVDLA_CSC_sg.scala 345:49:@741.4]
  assign required_kernels_inc = required_kernels + _GEN_63; // @[NV_NVDLA_CSC_sg.scala 345:49:@742.4]
  assign _GEN_64 = {{1'd0}, required_kernels_inc}; // @[NV_NVDLA_CSC_sg.scala 346:46:@743.4]
  assign wt_cbuf_ready = _GEN_64 <= kernels_avl; // @[NV_NVDLA_CSC_sg.scala 346:46:@743.4]
  assign cbuf_ready = dat_cbuf_ready & wt_cbuf_ready; // @[NV_NVDLA_CSC_sg.scala 357:37:@755.4]
  assign _T_513 = is_running & cbuf_ready; // @[NV_NVDLA_CSC_sg.scala 358:30:@756.4]
  assign _T_514 = ~ layer_done; // @[NV_NVDLA_CSC_sg.scala 358:45:@757.4]
  assign _T_515 = _T_513 & _T_514; // @[NV_NVDLA_CSC_sg.scala 358:43:@758.4]
  assign dat_push_ready = u_dat_fifo_io_wr_prdy; // @[NV_NVDLA_CSC_sg.scala 353:30:@752.4 NV_NVDLA_CSC_sg.scala 448:20:@881.4]
  assign wt_push_ready = u_wt_fifo_io_wr_prdy; // @[NV_NVDLA_CSC_sg.scala 354:29:@753.4 NV_NVDLA_CSC_sg.scala 462:19:@892.4]
  assign fifo_push_ready = dat_push_ready & wt_push_ready; // @[NV_NVDLA_CSC_sg.scala 356:42:@754.4]
  assign _T_517 = _T_139 | fifo_push_ready; // @[NV_NVDLA_CSC_sg.scala 358:69:@760.4]
  assign pkg_adv = _T_515 & _T_517; // @[NV_NVDLA_CSC_sg.scala 358:57:@761.4]
  assign is_last_s = weight_s_up_cnt == weight_width_cmp; // @[NV_NVDLA_CSC_sg.scala 320:38:@716.4]
  assign _GEN_65 = {{2'd0}, weight_r_add}; // @[NV_NVDLA_CSC_sg.scala 319:47:@715.4]
  assign weight_r_up_cnt_inc = weight_r_up_cnt + _GEN_65; // @[NV_NVDLA_CSC_sg.scala 319:47:@715.4]
  assign _GEN_66 = {{1'd0}, weight_height_cmp}; // @[NV_NVDLA_CSC_sg.scala 321:42:@717.4]
  assign is_last_r = weight_r_up_cnt_inc > _GEN_66; // @[NV_NVDLA_CSC_sg.scala 321:42:@717.4]
  assign is_last_block = is_last_s & is_last_r; // @[NV_NVDLA_CSC_sg.scala 327:35:@723.4]
  assign _T_530 = pkg_adv & is_last_block; // @[NV_NVDLA_CSC_sg.scala 365:28:@780.4]
  assign _T_445 = channel_up_cnt + 14'h8; // @[NV_NVDLA_CSC_sg.scala 303:45:@692.4]
  assign channel_up_cnt_inc = channel_up_cnt + 14'h8; // @[NV_NVDLA_CSC_sg.scala 303:45:@693.4]
  assign is_last_channel = channel_up_cnt_inc >= weight_channel; // @[NV_NVDLA_CSC_sg.scala 304:47:@694.4]
  assign _T_531 = _T_530 & is_last_channel; // @[NV_NVDLA_CSC_sg.scala 365:44:@781.4]
  assign _GEN_67 = {{17'd0}, upper_limit}; // @[NV_NVDLA_CSC_sg.scala 285:47:@667.4]
  assign _T_421 = stripe_up_cnt + _GEN_67; // @[NV_NVDLA_CSC_sg.scala 285:47:@667.4]
  assign stripe_up_cnt_1x_inc = stripe_up_cnt + _GEN_67; // @[NV_NVDLA_CSC_sg.scala 285:47:@668.4]
  assign is_stripe_le_1x = stripe_up_cnt_1x_inc >= data_out_atomic; // @[NV_NVDLA_CSC_sg.scala 287:49:@670.4]
  assign _T_532 = _T_531 & is_stripe_le_1x; // @[NV_NVDLA_CSC_sg.scala 365:62:@782.4]
  assign _T_402 = ~ is_img_d1; // @[NV_NVDLA_CSC_sg.scala 275:24:@651.4]
  assign _T_403 = dataout_h_up_cnt == io_reg2dp_dataout_height; // @[NV_NVDLA_CSC_sg.scala 275:55:@652.4]
  assign is_last_do_h = _T_402 | _T_403; // @[NV_NVDLA_CSC_sg.scala 275:35:@653.4]
  assign _T_533 = _T_532 & is_last_do_h; // @[NV_NVDLA_CSC_sg.scala 365:79:@783.4]
  assign op_layer_en = _T_533 & is_last_group; // @[NV_NVDLA_CSC_sg.scala 365:94:@784.4]
  assign _T_372 = layer_st | op_layer_en; // @[NV_NVDLA_CSC_sg.scala 256:19:@626.4]
  assign _T_375 = is_last_group ? 1'h1 : layer_done; // @[NV_NVDLA_CSC_sg.scala 257:48:@628.6]
  assign _T_376 = layer_st ? 1'h0 : _T_375; // @[NV_NVDLA_CSC_sg.scala 257:25:@629.6]
  assign _GEN_31 = _T_372 ? _T_376 : layer_done; // @[NV_NVDLA_CSC_sg.scala 256:33:@627.4]
  assign _T_392 = layer_st | _T_533; // @[NV_NVDLA_CSC_sg.scala 267:19:@643.4]
  assign _T_394 = layer_st ? 10'h0 : group_up_cnt_inc; // @[NV_NVDLA_CSC_sg.scala 268:27:@645.6]
  assign _GEN_32 = _T_392 ? _T_394 : group_up_cnt; // @[NV_NVDLA_CSC_sg.scala 267:33:@644.4]
  assign _T_522 = is_img_d1 & pkg_adv; // @[NV_NVDLA_CSC_sg.scala 363:29:@770.4]
  assign _T_523 = _T_522 & is_last_block; // @[NV_NVDLA_CSC_sg.scala 363:39:@771.4]
  assign _T_524 = _T_523 & is_last_channel; // @[NV_NVDLA_CSC_sg.scala 363:55:@772.4]
  assign op_do_h_en = _T_524 & is_stripe_le_1x; // @[NV_NVDLA_CSC_sg.scala 363:73:@773.4]
  assign _T_404 = layer_st | op_do_h_en; // @[NV_NVDLA_CSC_sg.scala 276:19:@654.4]
  assign _T_408 = dataout_h_up_cnt + 13'h1; // @[NV_NVDLA_CSC_sg.scala 279:47:@656.6]
  assign _T_409 = dataout_h_up_cnt + 13'h1; // @[NV_NVDLA_CSC_sg.scala 279:47:@657.6]
  assign _T_410 = is_last_do_h ? 13'h0 : _T_409; // @[NV_NVDLA_CSC_sg.scala 278:32:@658.6]
  assign _T_411 = layer_st ? 13'h0 : _T_410; // @[NV_NVDLA_CSC_sg.scala 277:32:@659.6]
  assign _GEN_33 = _T_404 ? _T_411 : dataout_h_up_cnt; // @[NV_NVDLA_CSC_sg.scala 276:32:@655.4]
  assign _T_419 = {upper_limit,1'h0}; // @[Cat.scala 30:58:@664.4]
  assign _GEN_68 = {{16'd0}, _T_419}; // @[NV_NVDLA_CSC_sg.scala 284:46:@665.4]
  assign _T_420 = stripe_up_cnt + _GEN_68; // @[NV_NVDLA_CSC_sg.scala 284:46:@665.4]
  assign stripe_up_cnt_2x_inc = stripe_up_cnt + _GEN_68; // @[NV_NVDLA_CSC_sg.scala 284:46:@666.4]
  assign is_stripe_be_2x = stripe_up_cnt_2x_inc <= data_out_atomic; // @[NV_NVDLA_CSC_sg.scala 286:49:@669.4]
  assign _T_422 = data_out_atomic - stripe_up_cnt; // @[NV_NVDLA_CSC_sg.scala 289:43:@671.4]
  assign _T_423 = $unsigned(_T_422); // @[NV_NVDLA_CSC_sg.scala 289:43:@672.4]
  assign _T_424 = _T_423[21:0]; // @[NV_NVDLA_CSC_sg.scala 289:43:@673.4]
  assign cur_stripe_inc = _T_424[6:0]; // @[NV_NVDLA_CSC_sg.scala 289:60:@674.4]
  assign _T_425 = is_stripe_le_1x ? cur_stripe_inc : {{3'd0}, lower_limit}; // @[NV_NVDLA_CSC_sg.scala 290:59:@675.4]
  assign cur_stripe = is_stripe_be_2x ? {{2'd0}, upper_limit} : _T_425; // @[NV_NVDLA_CSC_sg.scala 290:25:@676.4]
  assign _T_427 = layer_st | _T_531; // @[NV_NVDLA_CSC_sg.scala 293:19:@678.4]
  assign _GEN_70 = {{18'd0}, lower_limit}; // @[NV_NVDLA_CSC_sg.scala 297:41:@682.6]
  assign _T_432 = stripe_up_cnt + _GEN_70; // @[NV_NVDLA_CSC_sg.scala 297:41:@682.6]
  assign _T_433 = stripe_up_cnt + _GEN_70; // @[NV_NVDLA_CSC_sg.scala 297:41:@683.6]
  assign _T_434 = is_stripe_be_2x ? stripe_up_cnt_1x_inc : _T_433; // @[NV_NVDLA_CSC_sg.scala 296:29:@684.6]
  assign _T_435 = is_stripe_le_1x ? 22'h0 : _T_434; // @[NV_NVDLA_CSC_sg.scala 295:29:@685.6]
  assign _T_436 = layer_st ? 22'h0 : _T_435; // @[NV_NVDLA_CSC_sg.scala 294:29:@686.6]
  assign _GEN_34 = _T_427 ? _T_436 : stripe_up_cnt; // @[NV_NVDLA_CSC_sg.scala 293:34:@679.4]
  assign _T_446 = ~ is_last_channel; // @[NV_NVDLA_CSC_sg.scala 306:27:@695.4]
  assign _T_448 = io_reg2dp_weight_channel_ext[2:0]; // @[NV_NVDLA_CSC_sg.scala 306:93:@696.4]
  assign _T_450 = _T_448 + 3'h1; // @[NV_NVDLA_CSC_sg.scala 306:117:@697.4]
  assign cur_channel = _T_446 ? 7'h8 : {{3'd0}, _T_450}; // @[NV_NVDLA_CSC_sg.scala 306:26:@698.4]
  assign _T_451 = layer_st | _T_530; // @[NV_NVDLA_CSC_sg.scala 308:19:@699.4]
  assign _T_462 = is_last_channel ? 14'h0 : channel_up_cnt_inc; // @[NV_NVDLA_CSC_sg.scala 309:63:@703.6]
  assign _T_463 = layer_st ? 14'h0 : _T_462; // @[NV_NVDLA_CSC_sg.scala 309:30:@704.6]
  assign _GEN_35 = _T_451 ? _T_463 : channel_up_cnt; // @[NV_NVDLA_CSC_sg.scala 308:35:@700.4]
  assign _T_479 = weight_s_up_cnt + 5'h1; // @[NV_NVDLA_CSC_sg.scala 318:47:@713.4]
  assign weight_s_up_cnt_inc = weight_s_up_cnt + 5'h1; // @[NV_NVDLA_CSC_sg.scala 318:47:@714.4]
  assign _T_480 = weight_r_add[2]; // @[NV_NVDLA_CSC_sg.scala 323:33:@718.4]
  assign _T_482 = weight_r_add[1]; // @[NV_NVDLA_CSC_sg.scala 324:33:@719.4]
  assign _T_485 = _T_482 ? 2'h1 : 2'h0; // @[NV_NVDLA_CSC_sg.scala 324:20:@720.4]
  assign _T_486 = _T_480 ? 2'h3 : _T_485; // @[NV_NVDLA_CSC_sg.scala 323:20:@721.4]
  assign cur_r = is_last_r ? weight_r_last : {{1'd0}, _T_486}; // @[NV_NVDLA_CSC_sg.scala 322:20:@722.4]
  assign _T_487 = layer_st | pkg_adv; // @[NV_NVDLA_CSC_sg.scala 329:19:@724.4]
  assign _T_490 = is_last_s ? 5'h0 : weight_s_up_cnt_inc; // @[NV_NVDLA_CSC_sg.scala 331:32:@726.6]
  assign _T_491 = layer_st ? 5'h0 : _T_490; // @[NV_NVDLA_CSC_sg.scala 330:31:@727.6]
  assign _GEN_36 = _T_487 ? _T_491 : weight_s_up_cnt; // @[NV_NVDLA_CSC_sg.scala 329:29:@725.4]
  assign op_r_en = pkg_adv & is_last_s; // @[NV_NVDLA_CSC_sg.scala 360:24:@763.4]
  assign _T_492 = layer_st | op_r_en; // @[NV_NVDLA_CSC_sg.scala 334:19:@730.4]
  assign _T_495 = weight_r_up_cnt_inc[4:0]; // @[NV_NVDLA_CSC_sg.scala 337:48:@732.6]
  assign _T_496 = is_last_r ? 5'h0 : _T_495; // @[NV_NVDLA_CSC_sg.scala 336:32:@733.6]
  assign _T_497 = layer_st ? 5'h0 : _T_496; // @[NV_NVDLA_CSC_sg.scala 335:31:@734.6]
  assign _GEN_37 = _T_492 ? _T_497 : weight_r_up_cnt; // @[NV_NVDLA_CSC_sg.scala 334:29:@731.4]
  assign _T_506 = layer_st | is_last_group; // @[NV_NVDLA_CSC_sg.scala 349:43:@746.6]
  assign _T_507 = ~ io_reg2dp_skip_weight_rls; // @[NV_NVDLA_CSC_sg.scala 349:61:@747.6]
  assign _T_508 = _T_506 | _T_507; // @[NV_NVDLA_CSC_sg.scala 349:59:@748.6]
  assign _T_510 = _T_508 ? 14'h0 : required_kernels_inc; // @[NV_NVDLA_CSC_sg.scala 349:32:@749.6]
  assign _GEN_38 = _T_392 ? _T_510 : required_kernels; // @[NV_NVDLA_CSC_sg.scala 348:33:@745.4]
  assign _T_535 = ~ is_running; // @[NV_NVDLA_CSC_sg.scala 367:20:@786.4]
  assign _T_538 = cbuf_ready & _T_514; // @[NV_NVDLA_CSC_sg.scala 367:57:@788.4]
  assign _T_541 = fifo_push_ready ? 1'h0 : pkg_vld; // @[NV_NVDLA_CSC_sg.scala 367:83:@789.4]
  assign _T_542 = _T_538 ? 1'h1 : _T_541; // @[NV_NVDLA_CSC_sg.scala 367:45:@790.4]
  assign _T_543 = _T_535 ? 1'h0 : _T_542; // @[NV_NVDLA_CSC_sg.scala 367:19:@791.4]
  assign _T_572 = pkg_idx + 2'h1; // @[NV_NVDLA_CSC_sg.scala 382:61:@805.4]
  assign _T_573 = pkg_idx + 2'h1; // @[NV_NVDLA_CSC_sg.scala 382:61:@806.4]
  assign pkg_idx_w = layer_st ? 2'h3 : _T_573; // @[NV_NVDLA_CSC_sg.scala 382:24:@807.4]
  assign pkg_channel_end_w = is_last_block & is_last_channel; // @[NV_NVDLA_CSC_sg.scala 386:43:@808.4]
  assign _T_575 = pkg_channel_end_w & is_stripe_le_1x; // @[NV_NVDLA_CSC_sg.scala 387:59:@810.4]
  assign pkg_group_end_w = _T_575 & is_last_do_h; // @[NV_NVDLA_CSC_sg.scala 387:76:@811.4]
  assign pkg_layer_end_w = pkg_group_end_w & is_last_group; // @[NV_NVDLA_CSC_sg.scala 388:91:@815.4]
  assign _GEN_39 = _T_487 ? pkg_idx_w : pkg_idx; // @[NV_NVDLA_CSC_sg.scala 390:29:@817.4]
  assign _T_585 = ~ io_reg2dp_skip_data_rls; // @[NV_NVDLA_CSC_sg.scala 403:32:@833.6]
  assign _T_586 = _T_585 & pkg_layer_end_w; // @[NV_NVDLA_CSC_sg.scala 403:57:@834.6]
  assign _GEN_40 = pkg_adv ? weight_s_up_cnt : dat_pkg_w_offset; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _GEN_41 = pkg_adv ? weight_r_up_cnt : dat_pkg_h_offset; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _GEN_42 = pkg_adv ? cur_channel : dat_pkg_channel_size; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _GEN_43 = pkg_adv ? cur_stripe : dat_pkg_stripe_length; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _GEN_44 = pkg_adv ? cur_r : dat_pkg_cur_sub_h; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _GEN_45 = pkg_adv ? is_last_block : dat_pkg_block_end; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _GEN_46 = pkg_adv ? pkg_channel_end_w : dat_pkg_channel_end; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _GEN_47 = pkg_adv ? pkg_group_end_w : dat_pkg_group_end; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _GEN_48 = pkg_adv ? pkg_layer_end_w : dat_pkg_layer_end; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _GEN_49 = pkg_adv ? _T_586 : dat_pkg_dat_release; // @[NV_NVDLA_CSC_sg.scala 393:18:@823.4]
  assign _T_595 = dat_pkg_cur_sub_h[1:0]; // @[NV_NVDLA_CSC_sg.scala 413:42:@841.4]
  assign dat_pkg_pd = {dat_pkg_dat_release,dat_pkg_layer_end,dat_pkg_group_end,dat_pkg_channel_end,dat_pkg_block_end,_T_595,dat_pkg_stripe_length,dat_pkg_channel_size,dat_pkg_h_offset,dat_pkg_w_offset}; // @[Cat.scala 30:58:@854.4]
  assign dat_push_data = {pkg_idx,dat_pkg_pd}; // @[Cat.scala 30:58:@855.4]
  assign _T_609 = _T_507 & pkg_group_end_w; // @[NV_NVDLA_CSC_sg.scala 423:57:@861.6]
  assign _GEN_50 = pkg_adv ? {{3'd0}, cur_kernel} : wt_pkg_kernel_size; // @[NV_NVDLA_CSC_sg.scala 419:18:@856.4]
  assign _GEN_51 = pkg_adv ? cur_channel : wt_pkg_weight_size; // @[NV_NVDLA_CSC_sg.scala 419:18:@856.4]
  assign _GEN_52 = pkg_adv ? cur_r : wt_pkg_cur_sub_h; // @[NV_NVDLA_CSC_sg.scala 419:18:@856.4]
  assign _GEN_53 = pkg_adv ? _T_609 : wt_pkg_wt_release; // @[NV_NVDLA_CSC_sg.scala 419:18:@856.4]
  assign _T_610 = wt_pkg_cur_sub_h[1:0]; // @[NV_NVDLA_CSC_sg.scala 431:41:@864.4]
  assign _T_611 = wt_pkg_kernel_size[5:0]; // @[NV_NVDLA_CSC_sg.scala 431:67:@865.4]
  assign wt_pkg_pd = {wt_pkg_wt_release,dat_pkg_group_end,dat_pkg_channel_end,_T_610,_T_611,wt_pkg_weight_size}; // @[Cat.scala 30:58:@870.4]
  assign wt_push_data = {pkg_idx,wt_pkg_wt_release,dat_pkg_group_end,dat_pkg_channel_end,_T_610,_T_611,wt_pkg_weight_size}; // @[Cat.scala 30:58:@871.4]
  assign wt_pop_pd = u_wt_fifo_io_rd_pd[17:0]; // @[NV_NVDLA_CSC_sg.scala 478:32:@900.4]
  assign sg2dat_stripe_length = dat_pop_pd[23:17]; // @[NV_NVDLA_CSC_sg.scala 485:42:@904.4]
  assign sg2wt_kernel_size = wt_pop_pd[12:7]; // @[NV_NVDLA_CSC_sg.scala 495:38:@913.4]
  assign _T_623 = io_reg2dp_y_extension == 2'h2; // @[NV_NVDLA_CSC_sg.scala 506:61:@922.4]
  assign _T_625 = sg2dat_stripe_length + 7'h3; // @[NV_NVDLA_CSC_sg.scala 506:106:@923.4]
  assign _T_627 = _T_625 & 8'hfc; // @[NV_NVDLA_CSC_sg.scala 506:127:@924.4]
  assign _T_628 = _T_627[6:0]; // @[NV_NVDLA_CSC_sg.scala 506:147:@925.4]
  assign _T_630 = io_reg2dp_y_extension == 2'h1; // @[NV_NVDLA_CSC_sg.scala 507:61:@926.4]
  assign _T_632 = sg2dat_stripe_length + 7'h1; // @[NV_NVDLA_CSC_sg.scala 507:106:@927.4]
  assign _T_634 = _T_632 & 8'hfe; // @[NV_NVDLA_CSC_sg.scala 507:127:@928.4]
  assign _T_635 = _T_634[6:0]; // @[NV_NVDLA_CSC_sg.scala 507:147:@929.4]
  assign _T_636 = _T_630 ? _T_635 : sg2dat_stripe_length; // @[NV_NVDLA_CSC_sg.scala 507:38:@930.4]
  assign _T_637 = _T_623 ? _T_628 : _T_636; // @[NV_NVDLA_CSC_sg.scala 506:38:@931.4]
  assign dat_stripe_img_length_w = _T_402 ? 7'h0 : _T_637; // @[NV_NVDLA_CSC_sg.scala 505:38:@932.4]
  assign dat_stripe_length_w = is_img_d1 ? dat_stripe_img_length_w : sg2dat_stripe_length; // @[NV_NVDLA_CSC_sg.scala 510:34:@933.4]
  assign _T_653 = ~ dat_pop_ready; // @[NV_NVDLA_CSC_sg.scala 522:30:@942.4]
  assign _T_656 = dat_stripe_length < 7'h8; // @[NV_NVDLA_CSC_sg.scala 523:49:@943.4]
  assign _T_658 = _T_656 ? 7'h8 : dat_stripe_length; // @[NV_NVDLA_CSC_sg.scala 523:29:@944.4]
  assign dat_max_cycles = _T_653 ? 7'h0 : _T_658; // @[NV_NVDLA_CSC_sg.scala 522:29:@945.4]
  assign _T_692 = _T_697 & credit_ready; // @[NV_NVDLA_CSC_sg.scala 534:54:@972.4]
  assign _T_693 = dat_pop_idx == wt_pop_idx; // @[NV_NVDLA_CSC_sg.scala 534:85:@973.4]
  assign _T_694 = _T_692 | _T_693; // @[NV_NVDLA_CSC_sg.scala 534:70:@974.4]
  assign wt_pop_ready = wt_pop_req & _T_694; // @[NV_NVDLA_CSC_sg.scala 534:32:@975.4]
  assign _T_659 = ~ wt_pop_ready; // @[NV_NVDLA_CSC_sg.scala 527:29:@946.4]
  assign _T_662 = sg2wt_kernel_size <= 6'h1; // @[NV_NVDLA_CSC_sg.scala 528:41:@947.4]
  assign _T_664 = pop_cnt <= 6'h1; // @[NV_NVDLA_CSC_sg.scala 528:73:@948.4]
  assign _T_665 = _T_662 & _T_664; // @[NV_NVDLA_CSC_sg.scala 528:62:@949.4]
  assign _T_667 = sg2wt_kernel_size > pop_cnt; // @[NV_NVDLA_CSC_sg.scala 529:41:@950.4]
  assign _T_668 = _T_667 ? sg2wt_kernel_size : pop_cnt; // @[NV_NVDLA_CSC_sg.scala 529:29:@951.4]
  assign _T_669 = _T_665 ? 6'h2 : _T_668; // @[NV_NVDLA_CSC_sg.scala 528:29:@952.4]
  assign wt_max_cycles = _T_659 ? 6'h0 : _T_669; // @[NV_NVDLA_CSC_sg.scala 527:28:@953.4]
  assign _GEN_71 = {{1'd0}, wt_max_cycles}; // @[NV_NVDLA_CSC_sg.scala 531:41:@954.4]
  assign _T_670 = dat_max_cycles >= _GEN_71; // @[NV_NVDLA_CSC_sg.scala 531:41:@954.4]
  assign _T_672 = dat_max_cycles - 7'h1; // @[NV_NVDLA_CSC_sg.scala 531:76:@955.4]
  assign _T_673 = $unsigned(_T_672); // @[NV_NVDLA_CSC_sg.scala 531:76:@956.4]
  assign _T_674 = _T_673[6:0]; // @[NV_NVDLA_CSC_sg.scala 531:76:@957.4]
  assign _T_676 = {1'h0,wt_max_cycles}; // @[Cat.scala 30:58:@958.4]
  assign _T_678 = _T_676 - 7'h1; // @[NV_NVDLA_CSC_sg.scala 531:109:@959.4]
  assign _T_679 = $unsigned(_T_678); // @[NV_NVDLA_CSC_sg.scala 531:109:@960.4]
  assign _T_680 = _T_679[6:0]; // @[NV_NVDLA_CSC_sg.scala 531:109:@961.4]
  assign _T_681 = _T_670 ? _T_674 : _T_680; // @[NV_NVDLA_CSC_sg.scala 531:25:@962.4]
  assign max_cycles = _T_681[5:0]; // @[NV_NVDLA_CSC_sg.scala 531:116:@963.4]
  assign _T_683 = pop_cnt - 6'h1; // @[NV_NVDLA_CSC_sg.scala 532:31:@964.4]
  assign _T_684 = $unsigned(_T_683); // @[NV_NVDLA_CSC_sg.scala 532:31:@965.4]
  assign pop_cnt_dec = _T_684[5:0]; // @[NV_NVDLA_CSC_sg.scala 532:31:@966.4]
  assign _T_685 = dat_pop_ready | wt_pop_ready; // @[NV_NVDLA_CSC_sg.scala 533:39:@967.4]
  assign _T_689 = _T_697 ? 6'h0 : pop_cnt_dec; // @[NV_NVDLA_CSC_sg.scala 533:70:@969.4]
  assign pop_cnt_w = _T_685 ? max_cycles : _T_689; // @[NV_NVDLA_CSC_sg.scala 533:24:@970.4]
  assign _GEN_54 = wt_pop_ready_d1 ? sg2dat_stripe_length : dat_stripe_size; // @[NV_NVDLA_CSC_sg.scala 539:26:@986.4]
  assign _GEN_55 = wt_pop_ready_d1 ? dat_stripe_length_w : dat_stripe_length; // @[NV_NVDLA_CSC_sg.scala 539:26:@986.4]
  assign _GEN_56 = dat_pop_ready ? dat_pop_pd : sg2dl_pd_out; // @[NV_NVDLA_CSC_sg.scala 545:24:@992.4]
  assign _GEN_57 = wt_pop_ready ? wt_pop_pd : sg2wl_pd_out; // @[NV_NVDLA_CSC_sg.scala 549:23:@996.4]
  assign credit_cnt_add = credit_vld ? {{1'd0}, credit_size} : 4'h0; // @[NV_NVDLA_CSC_sg.scala 573:29:@1011.4]
  assign _T_711 = dat_pop_ready & sg2dat_channel_end; // @[NV_NVDLA_CSC_sg.scala 574:43:@1012.4]
  assign credit_cnt_dec = _T_711 ? dat_impact_cnt : 9'h0; // @[NV_NVDLA_CSC_sg.scala 574:29:@1013.4]
  assign _T_716 = dat_pop_ready | credit_vld; // @[NV_NVDLA_CSC_sg.scala 577:24:@1018.4]
  assign _GEN_72 = {{5'd0}, credit_cnt_add}; // @[NV_NVDLA_CSC_sg.scala 578:34:@1020.6]
  assign _T_717 = credit_cnt + _GEN_72; // @[NV_NVDLA_CSC_sg.scala 578:34:@1020.6]
  assign _T_718 = credit_cnt + _GEN_72; // @[NV_NVDLA_CSC_sg.scala 578:34:@1021.6]
  assign _T_719 = _T_718 - credit_cnt_dec; // @[NV_NVDLA_CSC_sg.scala 578:51:@1022.6]
  assign _T_720 = $unsigned(_T_719); // @[NV_NVDLA_CSC_sg.scala 578:51:@1023.6]
  assign _T_721 = _T_720[8:0]; // @[NV_NVDLA_CSC_sg.scala 578:51:@1024.6]
  assign _GEN_59 = _T_716 ? _T_721 : credit_cnt; // @[NV_NVDLA_CSC_sg.scala 577:37:@1019.4]
  assign _T_722 = pkg_adv & pkg_layer_end_w; // @[NV_NVDLA_CSC_sg.scala 584:31:@1027.4]
  assign dat_release = _T_722 & _T_585; // @[NV_NVDLA_CSC_sg.scala 584:49:@1029.4]
  assign _T_724 = _T_203 & io_reg2dp_op_en; // @[NV_NVDLA_CSC_sg.scala 585:37:@1030.4]
  assign _T_725 = ~ io_reg2dp_data_reuse; // @[NV_NVDLA_CSC_sg.scala 585:58:@1031.4]
  assign _T_726 = _T_725 | is_mode_change; // @[NV_NVDLA_CSC_sg.scala 585:80:@1032.4]
  assign _T_727 = _T_724 & _T_726; // @[NV_NVDLA_CSC_sg.scala 585:55:@1033.4]
  assign _T_729 = last_slices != 14'h0; // @[NV_NVDLA_CSC_sg.scala 585:113:@1034.4]
  assign dat_reuse_release = _T_727 & _T_729; // @[NV_NVDLA_CSC_sg.scala 585:98:@1035.4]
  assign slices_avl_add = io_cdma2sc_dat_updt_valid ? io_cdma2sc_dat_updt_bits_slices : 14'h0; // @[NV_NVDLA_CSC_sg.scala 586:29:@1036.4]
  assign _T_732 = dat_reuse_release ? last_slices : 14'h0; // @[NV_NVDLA_CSC_sg.scala 587:58:@1037.4]
  assign slices_avl_sub = dat_release ? rls_slices : _T_732; // @[NV_NVDLA_CSC_sg.scala 587:29:@1038.4]
  assign _T_734 = pkg_adv & _T_507; // @[NV_NVDLA_CSC_sg.scala 588:30:@1040.4]
  assign wt_release = _T_734 & pkg_group_end_w; // @[NV_NVDLA_CSC_sg.scala 588:59:@1041.4]
  assign _T_736 = ~ io_reg2dp_weight_reuse; // @[NV_NVDLA_CSC_sg.scala 589:56:@1043.4]
  assign _T_737 = _T_724 & _T_736; // @[NV_NVDLA_CSC_sg.scala 589:54:@1044.4]
  assign wt_reuse_release = _T_737 & last_skip_weight_rls; // @[NV_NVDLA_CSC_sg.scala 589:80:@1045.4]
  assign kernels_avl_add = io_cdma2sc_wt_updt_valid ? io_cdma2sc_wt_updt_bits_kernels : 14'h0; // @[NV_NVDLA_CSC_sg.scala 590:30:@1046.4]
  assign _T_740 = {7'h0,cur_kernel}; // @[Cat.scala 30:58:@1047.4]
  assign _T_742 = wt_reuse_release ? last_kernels : 14'h0; // @[NV_NVDLA_CSC_sg.scala 591:81:@1048.4]
  assign kernels_avl_sub = wt_release ? {{3'd0}, _T_740} : _T_742; // @[NV_NVDLA_CSC_sg.scala 591:30:@1049.4]
  assign _T_743 = dat_pending_req | dat_release; // @[NV_NVDLA_CSC_sg.scala 593:26:@1050.4]
  assign _T_744 = _T_743 | dat_reuse_release; // @[NV_NVDLA_CSC_sg.scala 593:40:@1051.4]
  assign _T_745 = _T_744 | io_cdma2sc_dat_updt_valid; // @[NV_NVDLA_CSC_sg.scala 593:60:@1052.4]
  assign _T_747 = slices_avl + slices_avl_add; // @[NV_NVDLA_CSC_sg.scala 594:75:@1054.6]
  assign _T_748 = slices_avl + slices_avl_add; // @[NV_NVDLA_CSC_sg.scala 594:75:@1055.6]
  assign _T_749 = _T_748 - slices_avl_sub; // @[NV_NVDLA_CSC_sg.scala 594:92:@1056.6]
  assign _T_750 = $unsigned(_T_749); // @[NV_NVDLA_CSC_sg.scala 594:92:@1057.6]
  assign _T_751 = _T_750[13:0]; // @[NV_NVDLA_CSC_sg.scala 594:92:@1058.6]
  assign _T_752 = dat_pending_req ? 14'h0 : _T_751; // @[NV_NVDLA_CSC_sg.scala 594:26:@1059.6]
  assign _GEN_60 = _T_745 ? _T_752 : slices_avl; // @[NV_NVDLA_CSC_sg.scala 593:88:@1053.4]
  assign _T_753 = wt_pending_req | wt_release; // @[NV_NVDLA_CSC_sg.scala 596:25:@1062.4]
  assign _T_754 = _T_753 | wt_reuse_release; // @[NV_NVDLA_CSC_sg.scala 596:38:@1063.4]
  assign _T_755 = _T_754 | io_cdma2sc_wt_updt_valid; // @[NV_NVDLA_CSC_sg.scala 596:57:@1064.4]
  assign _GEN_73 = {{1'd0}, kernels_avl_add}; // @[NV_NVDLA_CSC_sg.scala 597:75:@1066.6]
  assign _T_757 = kernels_avl + _GEN_73; // @[NV_NVDLA_CSC_sg.scala 597:75:@1066.6]
  assign _T_758 = kernels_avl + _GEN_73; // @[NV_NVDLA_CSC_sg.scala 597:75:@1067.6]
  assign _GEN_74 = {{1'd0}, kernels_avl_sub}; // @[NV_NVDLA_CSC_sg.scala 597:93:@1068.6]
  assign _T_759 = _T_758 - _GEN_74; // @[NV_NVDLA_CSC_sg.scala 597:93:@1068.6]
  assign _T_760 = $unsigned(_T_759); // @[NV_NVDLA_CSC_sg.scala 597:93:@1069.6]
  assign _T_761 = _T_760[14:0]; // @[NV_NVDLA_CSC_sg.scala 597:93:@1070.6]
  assign _T_762 = wt_pending_req ? 15'h0 : _T_761; // @[NV_NVDLA_CSC_sg.scala 597:27:@1071.6]
  assign _GEN_61 = _T_755 ? _T_762 : kernels_avl; // @[NV_NVDLA_CSC_sg.scala 596:84:@1065.4]
  assign io_sc2cdma_dat_pending_req = dat_pending_req; // @[NV_NVDLA_CSC_sg.scala 180:32:@535.4]
  assign io_sc2cdma_wt_pending_req = wt_pending_req; // @[NV_NVDLA_CSC_sg.scala 181:31:@536.4]
  assign io_sc_state = _T_203 ? 2'h0 : _T_212; // @[NV_NVDLA_CSC_sg.scala 170:17:@509.4]
  assign io_sg2dl_pd_valid = sg2dl_pvld_out; // @[NV_NVDLA_CSC_sg.scala 553:23:@999.4]
  assign io_sg2dl_pd_bits = sg2dl_pd_out; // @[NV_NVDLA_CSC_sg.scala 554:22:@1000.4]
  assign io_sg2dl_reuse_rls = _T_765; // @[NV_NVDLA_CSC_sg.scala 600:24:@1076.4]
  assign io_sg2wl_pd_valid = sg2wl_pvld_out; // @[NV_NVDLA_CSC_sg.scala 555:23:@1001.4]
  assign io_sg2wl_pd_bits = sg2wl_pd_out; // @[NV_NVDLA_CSC_sg.scala 556:22:@1002.4]
  assign io_sg2wl_reuse_rls = _T_768; // @[NV_NVDLA_CSC_sg.scala 601:24:@1079.4]
  assign io_dp2reg_done = _T_219; // @[NV_NVDLA_CSC_sg.scala 173:20:@515.4]
  assign u_dat_fifo_clock = io_nvdla_core_clk; // @[:@876.4]
  assign u_dat_fifo_reset = reset; // @[:@877.4]
  assign u_dat_fifo_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_CSC_sg.scala 445:23:@878.4]
  assign u_dat_fifo_io_wr_pvld = pkg_vld & wt_push_ready; // @[NV_NVDLA_CSC_sg.scala 447:27:@880.4]
  assign u_dat_fifo_io_wr_pd = dat_push_data; // @[NV_NVDLA_CSC_sg.scala 449:25:@882.4]
  assign u_dat_fifo_io_rd_prdy = _T_699 & _T_702; // @[NV_NVDLA_CSC_sg.scala 451:27:@884.4]
  assign u_wt_fifo_clock = io_nvdla_core_clk; // @[:@887.4]
  assign u_wt_fifo_reset = reset; // @[:@888.4]
  assign u_wt_fifo_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_CSC_sg.scala 459:22:@889.4]
  assign u_wt_fifo_io_wr_pvld = pkg_vld & dat_push_ready; // @[NV_NVDLA_CSC_sg.scala 461:26:@891.4]
  assign u_wt_fifo_io_wr_pd = wt_push_data; // @[NV_NVDLA_CSC_sg.scala 463:24:@893.4]
  assign u_wt_fifo_io_rd_prdy = wt_pop_req & _T_694; // @[NV_NVDLA_CSC_sg.scala 465:26:@895.4]
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
  layer_done = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  pkg_vld = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  cur_state = _RAND_2[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  last_data_bank = _RAND_3[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  last_weight_bank = _RAND_4[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  dat_pending_clr = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  dat_pending_req = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  wt_pending_clr = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  wt_pending_req = _RAND_8[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  dat_pending_ack = _RAND_9[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  wt_pending_ack = _RAND_10[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  dat_stripe_size = _RAND_11[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  flush_cycles = _RAND_12[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  sg_dn_cnt = _RAND_13[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  pop_cnt = _RAND_14[5:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_15 = {1{`RANDOM}};
  credit_cnt = _RAND_15[8:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_16 = {1{`RANDOM}};
  last_mode = _RAND_16[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_17 = {1{`RANDOM}};
  _T_219 = _RAND_17[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_18 = {1{`RANDOM}};
  last_slices = _RAND_18[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_19 = {1{`RANDOM}};
  slice_left = _RAND_19[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_20 = {1{`RANDOM}};
  last_kernels = _RAND_20[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_21 = {1{`RANDOM}};
  last_skip_weight_rls = _RAND_21[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_22 = {1{`RANDOM}};
  data_in_height = _RAND_22[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_23 = {1{`RANDOM}};
  data_out_atomic = _RAND_23[21:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_24 = {1{`RANDOM}};
  weight_width_cmp = _RAND_24[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_25 = {1{`RANDOM}};
  weight_height_cmp = _RAND_25[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_26 = {1{`RANDOM}};
  weight_channel = _RAND_26[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_27 = {1{`RANDOM}};
  weight_groups = _RAND_27[9:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_28 = {1{`RANDOM}};
  weight_r_add = _RAND_28[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_29 = {1{`RANDOM}};
  weight_r_last = _RAND_29[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_30 = {1{`RANDOM}};
  rls_slices = _RAND_30[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_31 = {1{`RANDOM}};
  is_img_d1 = _RAND_31[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_32 = {1{`RANDOM}};
  lower_limit = _RAND_32[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_33 = {1{`RANDOM}};
  upper_limit = _RAND_33[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_34 = {1{`RANDOM}};
  slices_avl = _RAND_34[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_35 = {1{`RANDOM}};
  required_kernels = _RAND_35[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_36 = {1{`RANDOM}};
  group_up_cnt = _RAND_36[9:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_37 = {1{`RANDOM}};
  kernels_avl = _RAND_37[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_38 = {1{`RANDOM}};
  weight_s_up_cnt = _RAND_38[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_39 = {1{`RANDOM}};
  weight_r_up_cnt = _RAND_39[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_40 = {1{`RANDOM}};
  channel_up_cnt = _RAND_40[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_41 = {1{`RANDOM}};
  stripe_up_cnt = _RAND_41[21:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_42 = {1{`RANDOM}};
  dataout_h_up_cnt = _RAND_42[12:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_43 = {1{`RANDOM}};
  pkg_idx = _RAND_43[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_44 = {1{`RANDOM}};
  dat_pkg_w_offset = _RAND_44[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_45 = {1{`RANDOM}};
  dat_pkg_h_offset = _RAND_45[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_46 = {1{`RANDOM}};
  dat_pkg_channel_size = _RAND_46[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_47 = {1{`RANDOM}};
  dat_pkg_stripe_length = _RAND_47[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_48 = {1{`RANDOM}};
  dat_pkg_cur_sub_h = _RAND_48[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_49 = {1{`RANDOM}};
  dat_pkg_block_end = _RAND_49[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_50 = {1{`RANDOM}};
  dat_pkg_channel_end = _RAND_50[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_51 = {1{`RANDOM}};
  dat_pkg_group_end = _RAND_51[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_52 = {1{`RANDOM}};
  dat_pkg_layer_end = _RAND_52[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_53 = {1{`RANDOM}};
  dat_pkg_dat_release = _RAND_53[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_54 = {1{`RANDOM}};
  wt_pkg_kernel_size = _RAND_54[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_55 = {1{`RANDOM}};
  wt_pkg_weight_size = _RAND_55[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_56 = {1{`RANDOM}};
  wt_pkg_cur_sub_h = _RAND_56[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_57 = {1{`RANDOM}};
  wt_pkg_wt_release = _RAND_57[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_58 = {1{`RANDOM}};
  wt_pop_ready_d1 = _RAND_58[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_59 = {1{`RANDOM}};
  dat_stripe_length = _RAND_59[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_60 = {1{`RANDOM}};
  sg2dl_pvld_out = _RAND_60[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_61 = {1{`RANDOM}};
  sg2dl_pd_out = _RAND_61[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_62 = {1{`RANDOM}};
  sg2wl_pvld_out = _RAND_62[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_63 = {1{`RANDOM}};
  sg2wl_pd_out = _RAND_63[17:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_64 = {1{`RANDOM}};
  credit_vld = _RAND_64[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_65 = {1{`RANDOM}};
  credit_size = _RAND_65[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_66 = {1{`RANDOM}};
  _T_765 = _RAND_66[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_67 = {1{`RANDOM}};
  _T_768 = _RAND_67[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      layer_done <= 1'h0;
    end else begin
      if (_T_372) begin
        if (layer_st) begin
          layer_done <= 1'h0;
        end else begin
          if (is_last_group) begin
            layer_done <= 1'h1;
          end
        end
      end
    end
    if (reset) begin
      pkg_vld <= 1'h0;
    end else begin
      if (_T_535) begin
        pkg_vld <= 1'h0;
      end else begin
        if (_T_538) begin
          pkg_vld <= 1'h1;
        end else begin
          if (fifo_push_ready) begin
            pkg_vld <= 1'h0;
          end
        end
      end
    end
    if (reset) begin
      cur_state <= 2'h0;
    end else begin
      if (_T_134) begin
        if (_T_135) begin
          cur_state <= 2'h1;
        end else begin
          if (io_reg2dp_op_en) begin
            cur_state <= 2'h2;
          end else begin
            cur_state <= 2'h0;
          end
        end
      end else begin
        if (_T_136) begin
          if (pending_done) begin
            cur_state <= 2'h2;
          end else begin
            cur_state <= 2'h1;
          end
        end else begin
          if (_T_137) begin
            if (_T_140) begin
              cur_state <= 2'h3;
            end else begin
              cur_state <= 2'h2;
            end
          end else begin
            if (_T_141) begin
              if (io_dp2reg_done) begin
                cur_state <= 2'h0;
              end else begin
                cur_state <= 2'h3;
              end
            end else begin
              cur_state <= 2'h0;
            end
          end
        end
      end
    end
    if (reset) begin
      last_data_bank <= 5'h1f;
    end else begin
      if (io_dp2reg_done) begin
        last_data_bank <= io_reg2dp_data_bank;
      end
    end
    if (reset) begin
      last_weight_bank <= 5'h1f;
    end else begin
      if (io_dp2reg_done) begin
        last_weight_bank <= io_reg2dp_weight_bank;
      end
    end
    if (reset) begin
      dat_pending_clr <= 1'h0;
    end else begin
      if (_T_231) begin
        dat_pending_clr <= 1'h1;
      end else begin
        if (_T_222) begin
          dat_pending_clr <= 1'h0;
        end
      end
    end
    if (reset) begin
      dat_pending_req <= 1'h0;
    end else begin
      if (_T_220) begin
        dat_pending_req <= 1'h1;
      end else begin
        if (_T_222) begin
          dat_pending_req <= 1'h0;
        end
      end
    end
    if (reset) begin
      wt_pending_clr <= 1'h0;
    end else begin
      if (_T_237) begin
        wt_pending_clr <= 1'h1;
      end else begin
        if (_T_222) begin
          wt_pending_clr <= 1'h0;
        end
      end
    end
    if (reset) begin
      wt_pending_req <= 1'h0;
    end else begin
      if (is_nxt_pending) begin
        wt_pending_req <= 1'h1;
      end else begin
        if (_T_222) begin
          wt_pending_req <= 1'h0;
        end
      end
    end
    if (reset) begin
      dat_pending_ack <= 1'h0;
    end else begin
      dat_pending_ack <= io_cdma2sc_dat_pending_ack;
    end
    if (reset) begin
      wt_pending_ack <= 1'h0;
    end else begin
      wt_pending_ack <= io_cdma2sc_wt_pending_ack;
    end
    if (reset) begin
      dat_stripe_size <= 7'h0;
    end else begin
      if (wt_pop_ready_d1) begin
        dat_stripe_size <= sg2dat_stripe_length;
      end
    end
    if (reset) begin
      flush_cycles <= 8'h0;
    end else begin
      if (_T_193) begin
        flush_cycles <= _T_195;
      end
    end
    if (reset) begin
      sg_dn_cnt <= 8'h0;
    end else begin
      if (is_nxt_done) begin
        if (_T_186) begin
          sg_dn_cnt <= flush_cycles;
        end else begin
          sg_dn_cnt <= _T_190;
        end
      end
    end
    if (reset) begin
      pop_cnt <= 6'h0;
    end else begin
      if (_T_685) begin
        pop_cnt <= max_cycles;
      end else begin
        if (_T_697) begin
          pop_cnt <= 6'h0;
        end else begin
          pop_cnt <= pop_cnt_dec;
        end
      end
    end
    if (reset) begin
      last_mode <= 3'h0;
    end else begin
      if (io_dp2reg_done) begin
        last_mode <= cur_mode;
      end
    end
    if (reset) begin
      _T_219 <= 1'h0;
    end else begin
      _T_219 <= _T_216;
    end
    if (reset) begin
      last_slices <= 14'h0;
    end else begin
      if (io_dp2reg_done) begin
        last_slices <= slice_left;
      end
    end
    if (reset) begin
      slice_left <= 14'h0;
    end else begin
      if (layer_st) begin
        if (io_reg2dp_skip_data_rls) begin
          slice_left <= _T_332;
        end else begin
          slice_left <= _T_362;
        end
      end
    end
    if (reset) begin
      last_kernels <= 14'h0;
    end else begin
      if (io_dp2reg_done) begin
        last_kernels <= _T_264;
      end
    end
    if (reset) begin
      last_skip_weight_rls <= 1'h0;
    end else begin
      if (io_dp2reg_done) begin
        last_skip_weight_rls <= io_reg2dp_skip_weight_rls;
      end
    end
    if (reset) begin
      data_in_height <= 14'h0;
    end else begin
      if (layer_st) begin
        data_in_height <= _T_332;
      end
    end
    if (reset) begin
      data_out_atomic <= 22'h0;
    end else begin
      if (layer_st) begin
        if (is_img) begin
          data_out_atomic <= {{8'd0}, _T_334};
        end else begin
          data_out_atomic <= _T_336;
        end
      end
    end
    if (reset) begin
      weight_width_cmp <= 5'h0;
    end else begin
      if (layer_st) begin
        if (is_img) begin
          weight_width_cmp <= 5'h0;
        end else begin
          weight_width_cmp <= io_reg2dp_weight_width_ext;
        end
      end
    end
    if (reset) begin
      weight_height_cmp <= 5'h0;
    end else begin
      if (layer_st) begin
        weight_height_cmp <= io_reg2dp_weight_height_ext;
      end
    end
    if (reset) begin
      weight_channel <= 14'h0;
    end else begin
      if (layer_st) begin
        weight_channel <= _T_342;
      end
    end
    if (reset) begin
      weight_groups <= 10'h0;
    end else begin
      if (layer_st) begin
        weight_groups <= _T_346;
      end
    end
    if (reset) begin
      weight_r_add <= 3'h1;
    end else begin
      if (layer_st) begin
        weight_r_add <= weight_r_add_w;
      end
    end
    if (reset) begin
      weight_r_last <= 3'h1;
    end else begin
      if (layer_st) begin
        weight_r_last <= {{1'd0}, _T_355};
      end
    end
    if (reset) begin
      rls_slices <= 14'h0;
    end else begin
      if (layer_st) begin
        rls_slices <= {{2'd0}, _T_358};
      end
    end
    if (reset) begin
      is_img_d1 <= 1'h0;
    end else begin
      if (layer_st) begin
        is_img_d1 <= is_img;
      end
    end
    if (reset) begin
      lower_limit <= 4'h8;
    end else begin
      lower_limit <= _GEN_29[3:0];
    end
    if (reset) begin
      upper_limit <= 5'h10;
    end else begin
      if (layer_st) begin
        upper_limit <= 5'h10;
      end
    end
    if (reset) begin
      required_kernels <= 14'h0;
    end else begin
      if (_T_392) begin
        if (_T_508) begin
          required_kernels <= 14'h0;
        end else begin
          required_kernels <= required_kernels_inc;
        end
      end
    end
    if (reset) begin
      group_up_cnt <= 10'h0;
    end else begin
      if (_T_392) begin
        if (layer_st) begin
          group_up_cnt <= 10'h0;
        end else begin
          group_up_cnt <= group_up_cnt_inc;
        end
      end
    end
    if (reset) begin
      weight_s_up_cnt <= 5'h0;
    end else begin
      if (_T_487) begin
        if (layer_st) begin
          weight_s_up_cnt <= 5'h0;
        end else begin
          if (is_last_s) begin
            weight_s_up_cnt <= 5'h0;
          end else begin
            weight_s_up_cnt <= weight_s_up_cnt_inc;
          end
        end
      end
    end
    if (reset) begin
      weight_r_up_cnt <= 5'h0;
    end else begin
      if (_T_492) begin
        if (layer_st) begin
          weight_r_up_cnt <= 5'h0;
        end else begin
          if (is_last_r) begin
            weight_r_up_cnt <= 5'h0;
          end else begin
            weight_r_up_cnt <= _T_495;
          end
        end
      end
    end
    if (reset) begin
      channel_up_cnt <= 14'h0;
    end else begin
      if (_T_451) begin
        if (layer_st) begin
          channel_up_cnt <= 14'h0;
        end else begin
          if (is_last_channel) begin
            channel_up_cnt <= 14'h0;
          end else begin
            channel_up_cnt <= channel_up_cnt_inc;
          end
        end
      end
    end
    if (reset) begin
      stripe_up_cnt <= 22'h0;
    end else begin
      if (_T_427) begin
        if (layer_st) begin
          stripe_up_cnt <= 22'h0;
        end else begin
          if (is_stripe_le_1x) begin
            stripe_up_cnt <= 22'h0;
          end else begin
            if (is_stripe_be_2x) begin
              stripe_up_cnt <= stripe_up_cnt_1x_inc;
            end else begin
              stripe_up_cnt <= _T_433;
            end
          end
        end
      end
    end
    if (reset) begin
      dataout_h_up_cnt <= 13'h0;
    end else begin
      if (_T_404) begin
        if (layer_st) begin
          dataout_h_up_cnt <= 13'h0;
        end else begin
          if (is_last_do_h) begin
            dataout_h_up_cnt <= 13'h0;
          end else begin
            dataout_h_up_cnt <= _T_409;
          end
        end
      end
    end
    if (reset) begin
      pkg_idx <= 2'h3;
    end else begin
      if (_T_487) begin
        if (layer_st) begin
          pkg_idx <= 2'h3;
        end else begin
          pkg_idx <= _T_573;
        end
      end
    end
    if (reset) begin
      dat_pkg_w_offset <= 5'h0;
    end else begin
      if (pkg_adv) begin
        dat_pkg_w_offset <= weight_s_up_cnt;
      end
    end
    if (reset) begin
      dat_pkg_h_offset <= 5'h0;
    end else begin
      if (pkg_adv) begin
        dat_pkg_h_offset <= weight_r_up_cnt;
      end
    end
    if (reset) begin
      dat_pkg_channel_size <= 7'h0;
    end else begin
      if (pkg_adv) begin
        if (_T_446) begin
          dat_pkg_channel_size <= 7'h8;
        end else begin
          dat_pkg_channel_size <= {{3'd0}, _T_450};
        end
      end
    end
    if (reset) begin
      dat_pkg_stripe_length <= 7'h0;
    end else begin
      if (pkg_adv) begin
        if (is_stripe_be_2x) begin
          dat_pkg_stripe_length <= {{2'd0}, upper_limit};
        end else begin
          if (is_stripe_le_1x) begin
            dat_pkg_stripe_length <= cur_stripe_inc;
          end else begin
            dat_pkg_stripe_length <= {{3'd0}, lower_limit};
          end
        end
      end
    end
    if (reset) begin
      dat_pkg_cur_sub_h <= 3'h0;
    end else begin
      if (pkg_adv) begin
        if (is_last_r) begin
          dat_pkg_cur_sub_h <= weight_r_last;
        end else begin
          dat_pkg_cur_sub_h <= {{1'd0}, _T_486};
        end
      end
    end
    if (reset) begin
      dat_pkg_block_end <= 1'h0;
    end else begin
      if (pkg_adv) begin
        dat_pkg_block_end <= is_last_block;
      end
    end
    if (reset) begin
      dat_pkg_channel_end <= 1'h0;
    end else begin
      if (pkg_adv) begin
        dat_pkg_channel_end <= pkg_channel_end_w;
      end
    end
    if (reset) begin
      dat_pkg_group_end <= 1'h0;
    end else begin
      if (pkg_adv) begin
        dat_pkg_group_end <= pkg_group_end_w;
      end
    end
    if (reset) begin
      dat_pkg_layer_end <= 1'h0;
    end else begin
      if (pkg_adv) begin
        dat_pkg_layer_end <= pkg_layer_end_w;
      end
    end
    if (reset) begin
      dat_pkg_dat_release <= 1'h0;
    end else begin
      if (pkg_adv) begin
        dat_pkg_dat_release <= _T_586;
      end
    end
    if (reset) begin
      wt_pkg_kernel_size <= 7'h0;
    end else begin
      if (pkg_adv) begin
        wt_pkg_kernel_size <= {{3'd0}, cur_kernel};
      end
    end
    if (reset) begin
      wt_pkg_weight_size <= 7'h0;
    end else begin
      if (pkg_adv) begin
        if (_T_446) begin
          wt_pkg_weight_size <= 7'h8;
        end else begin
          wt_pkg_weight_size <= {{3'd0}, _T_450};
        end
      end
    end
    if (reset) begin
      wt_pkg_cur_sub_h <= 3'h0;
    end else begin
      if (pkg_adv) begin
        if (is_last_r) begin
          wt_pkg_cur_sub_h <= weight_r_last;
        end else begin
          wt_pkg_cur_sub_h <= {{1'd0}, _T_486};
        end
      end
    end
    if (reset) begin
      wt_pkg_wt_release <= 1'h0;
    end else begin
      if (pkg_adv) begin
        wt_pkg_wt_release <= _T_609;
      end
    end
    if (reset) begin
      wt_pop_ready_d1 <= 1'h0;
    end else begin
      wt_pop_ready_d1 <= wt_pop_ready;
    end
    if (reset) begin
      dat_stripe_length <= 7'h0;
    end else begin
      if (wt_pop_ready_d1) begin
        if (is_img_d1) begin
          if (_T_402) begin
            dat_stripe_length <= 7'h0;
          end else begin
            if (_T_623) begin
              dat_stripe_length <= _T_628;
            end else begin
              if (_T_630) begin
                dat_stripe_length <= _T_635;
              end else begin
                dat_stripe_length <= sg2dat_stripe_length;
              end
            end
          end
        end else begin
          dat_stripe_length <= sg2dat_stripe_length;
        end
      end
    end
    if (reset) begin
      sg2dl_pvld_out <= 1'h0;
    end else begin
      sg2dl_pvld_out <= dat_pop_ready;
    end
    if (reset) begin
      sg2dl_pd_out <= 31'h0;
    end else begin
      if (dat_pop_ready) begin
        sg2dl_pd_out <= dat_pop_pd;
      end
    end
    if (reset) begin
      sg2wl_pvld_out <= 1'h0;
    end else begin
      sg2wl_pvld_out <= wt_pop_ready;
    end
    if (reset) begin
      sg2wl_pd_out <= 18'h0;
    end else begin
      if (wt_pop_ready) begin
        sg2wl_pd_out <= wt_pop_pd;
      end
    end
  end
  always @(posedge io_nvdla_core_ng_clk) begin
    if (reset) begin
      credit_cnt <= 9'h10;
    end else begin
      if (_T_716) begin
        credit_cnt <= _T_721;
      end
    end
    if (reset) begin
      slices_avl <= 14'h0;
    end else begin
      if (_T_745) begin
        if (dat_pending_req) begin
          slices_avl <= 14'h0;
        end else begin
          slices_avl <= _T_751;
        end
      end
    end
    if (reset) begin
      kernels_avl <= 15'h0;
    end else begin
      if (_T_755) begin
        if (wt_pending_req) begin
          kernels_avl <= 15'h0;
        end else begin
          kernels_avl <= _T_761;
        end
      end
    end
    if (reset) begin
      credit_vld <= 1'h0;
    end else begin
      credit_vld <= io_accu2sc_credit_size_valid;
    end
    if (io_accu2sc_credit_size_valid) begin
      credit_size <= io_accu2sc_credit_size_bits;
    end
    if (reset) begin
      _T_765 <= 1'h0;
    end else begin
      _T_765 <= dat_reuse_release;
    end
    if (reset) begin
      _T_768 <= 1'h0;
    end else begin
      _T_768 <= wt_reuse_release;
    end
  end
endmodule
