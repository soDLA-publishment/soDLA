module nv_flopram( // @[:@10.2]
  input         reset, // @[:@12.4]
  input         io_clk, // @[:@13.4]
  input  [63:0] io_di, // @[:@13.4]
  input         io_we, // @[:@13.4]
  input  [1:0]  io_wa, // @[:@13.4]
  input  [2:0]  io_ra, // @[:@13.4]
  output [63:0] io_dout // @[:@13.4]
);
  reg [63:0] _T_22; // @[nv_flopram.scala 68:61:@17.4]
  reg [63:0] _RAND_0;
  reg [63:0] _T_25; // @[nv_flopram.scala 68:61:@18.4]
  reg [63:0] _RAND_1;
  reg [63:0] _T_28; // @[nv_flopram.scala 68:61:@19.4]
  reg [63:0] _RAND_2;
  reg [63:0] _T_31; // @[nv_flopram.scala 68:61:@20.4]
  reg [63:0] _RAND_3;
  wire  _T_35; // @[nv_flopram.scala 73:32:@23.6]
  wire [63:0] _GEN_0; // @[nv_flopram.scala 73:40:@24.6]
  wire  _T_37; // @[nv_flopram.scala 73:32:@27.6]
  wire [63:0] _GEN_1; // @[nv_flopram.scala 73:40:@28.6]
  wire  _T_39; // @[nv_flopram.scala 73:32:@31.6]
  wire [63:0] _GEN_2; // @[nv_flopram.scala 73:40:@32.6]
  wire  _T_41; // @[nv_flopram.scala 73:32:@35.6]
  wire [63:0] _GEN_3; // @[nv_flopram.scala 73:40:@36.6]
  wire [63:0] _GEN_4; // @[nv_flopram.scala 70:16:@22.4]
  wire [63:0] _GEN_5; // @[nv_flopram.scala 70:16:@22.4]
  wire [63:0] _GEN_6; // @[nv_flopram.scala 70:16:@22.4]
  wire [63:0] _GEN_7; // @[nv_flopram.scala 70:16:@22.4]
  wire  _T_48; // @[Mux.scala 46:19:@41.4]
  wire [63:0] _T_49; // @[Mux.scala 46:16:@42.4]
  wire  _T_50; // @[Mux.scala 46:19:@43.4]
  wire [63:0] _T_51; // @[Mux.scala 46:16:@44.4]
  wire  _T_52; // @[Mux.scala 46:19:@45.4]
  wire [63:0] _T_53; // @[Mux.scala 46:16:@46.4]
  wire  _T_54; // @[Mux.scala 46:19:@47.4]
  wire [63:0] _T_55; // @[Mux.scala 46:16:@48.4]
  wire  _T_56; // @[Mux.scala 46:19:@49.4]
  assign _T_35 = io_wa == 2'h0; // @[nv_flopram.scala 73:32:@23.6]
  assign _GEN_0 = _T_35 ? io_di : _T_22; // @[nv_flopram.scala 73:40:@24.6]
  assign _T_37 = io_wa == 2'h1; // @[nv_flopram.scala 73:32:@27.6]
  assign _GEN_1 = _T_37 ? io_di : _T_25; // @[nv_flopram.scala 73:40:@28.6]
  assign _T_39 = io_wa == 2'h2; // @[nv_flopram.scala 73:32:@31.6]
  assign _GEN_2 = _T_39 ? io_di : _T_28; // @[nv_flopram.scala 73:40:@32.6]
  assign _T_41 = io_wa == 2'h3; // @[nv_flopram.scala 73:32:@35.6]
  assign _GEN_3 = _T_41 ? io_di : _T_31; // @[nv_flopram.scala 73:40:@36.6]
  assign _GEN_4 = io_we ? _GEN_0 : _T_22; // @[nv_flopram.scala 70:16:@22.4]
  assign _GEN_5 = io_we ? _GEN_1 : _T_25; // @[nv_flopram.scala 70:16:@22.4]
  assign _GEN_6 = io_we ? _GEN_2 : _T_28; // @[nv_flopram.scala 70:16:@22.4]
  assign _GEN_7 = io_we ? _GEN_3 : _T_31; // @[nv_flopram.scala 70:16:@22.4]
  assign _T_48 = 3'h4 == io_ra; // @[Mux.scala 46:19:@41.4]
  assign _T_49 = _T_48 ? io_di : 64'h0; // @[Mux.scala 46:16:@42.4]
  assign _T_50 = 3'h3 == io_ra; // @[Mux.scala 46:19:@43.4]
  assign _T_51 = _T_50 ? _T_31 : _T_49; // @[Mux.scala 46:16:@44.4]
  assign _T_52 = 3'h2 == io_ra; // @[Mux.scala 46:19:@45.4]
  assign _T_53 = _T_52 ? _T_28 : _T_51; // @[Mux.scala 46:16:@46.4]
  assign _T_54 = 3'h1 == io_ra; // @[Mux.scala 46:19:@47.4]
  assign _T_55 = _T_54 ? _T_25 : _T_53; // @[Mux.scala 46:16:@48.4]
  assign _T_56 = 3'h0 == io_ra; // @[Mux.scala 46:19:@49.4]
  assign io_dout = _T_56 ? _T_22 : _T_55; // @[nv_flopram.scala 83:13:@51.4]
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
module NV_NVDLA_MCIF_READ_eg_fifo( // @[:@53.2]
  input         clock, // @[:@54.4]
  input         reset, // @[:@55.4]
  input         io_clk, // @[:@56.4]
  input         io_wr_pvld, // @[:@56.4]
  output        io_wr_prdy, // @[:@56.4]
  input  [63:0] io_wr_pd, // @[:@56.4]
  output        io_rd_pvld, // @[:@56.4]
  input         io_rd_prdy, // @[:@56.4]
  output [63:0] io_rd_pd, // @[:@56.4]
  input  [31:0] io_pwrbus_ram_pd // @[:@56.4]
);
  wire  ram_reset; // @[FIFO_new.scala 254:29:@109.4]
  wire  ram_io_clk; // @[FIFO_new.scala 254:29:@109.4]
  wire [63:0] ram_io_di; // @[FIFO_new.scala 254:29:@109.4]
  wire  ram_io_we; // @[FIFO_new.scala 254:29:@109.4]
  wire [1:0] ram_io_wa; // @[FIFO_new.scala 254:29:@109.4]
  wire [2:0] ram_io_ra; // @[FIFO_new.scala 254:29:@109.4]
  wire [63:0] ram_io_dout; // @[FIFO_new.scala 254:29:@109.4]
  reg  wr_busy_int; // @[FIFO_new.scala 162:56:@65.4]
  reg [31:0] _RAND_0;
  wire  _T_26; // @[FIFO_new.scala 192:23:@67.4]
  wire  wr_reserving; // @[FIFO_new.scala 193:36:@70.4]
  reg [2:0] wr_count; // @[FIFO_new.scala 196:53:@73.4]
  reg [31:0] _RAND_1;
  wire [3:0] _T_33; // @[FIFO_new.scala 200:76:@74.4]
  wire [3:0] _T_34; // @[FIFO_new.scala 200:76:@75.4]
  wire [2:0] _T_35; // @[FIFO_new.scala 200:76:@76.4]
  wire [2:0] wr_count_next_wr_popping; // @[FIFO_new.scala 200:43:@77.4]
  wire [3:0] _T_37; // @[FIFO_new.scala 201:69:@78.4]
  wire [2:0] _T_38; // @[FIFO_new.scala 201:69:@79.4]
  wire [2:0] wr_count_next_no_wr_popping; // @[FIFO_new.scala 201:46:@80.4]
  reg  _T_63; // @[FIFO_new.scala 345:108:@121.4]
  reg [31:0] _RAND_2;
  wire  rd_popping; // @[FIFO_new.scala 374:47:@135.4]
  wire [2:0] wr_count_next; // @[FIFO_new.scala 202:32:@81.4]
  wire  wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 204:80:@82.4]
  wire  wr_count_next_is_full; // @[FIFO_new.scala 205:40:@83.4]
  wire  _T_47; // @[FIFO_new.scala 212:27:@91.4]
  wire [2:0] _GEN_0; // @[FIFO_new.scala 212:40:@92.4]
  reg [1:0] _T_50; // @[FIFO_new.scala 224:68:@95.4]
  reg [31:0] _RAND_3;
  wire [2:0] _T_52; // @[FIFO_new.scala 227:47:@96.4]
  wire [1:0] wr_adr_next; // @[FIFO_new.scala 227:47:@97.4]
  wire [1:0] _GEN_1; // @[FIFO_new.scala 228:29:@98.4]
  reg [1:0] rd_adr; // @[FIFO_new.scala 233:63:@102.4]
  reg [31:0] _RAND_4;
  wire [2:0] _T_57; // @[FIFO_new.scala 235:42:@103.4]
  wire [1:0] rd_adr_next_popping; // @[FIFO_new.scala 235:42:@104.4]
  wire [1:0] _GEN_2; // @[FIFO_new.scala 244:33:@105.4]
  reg  rd_pvld_p; // @[FIFO_new.scala 343:137:@120.4]
  reg [31:0] _RAND_5;
  reg [2:0] rd_count; // @[FIFO_new.scala 347:53:@122.4]
  reg [31:0] _RAND_6;
  wire [3:0] _T_67; // @[FIFO_new.scala 349:74:@123.4]
  wire [3:0] _T_68; // @[FIFO_new.scala 349:74:@124.4]
  wire [2:0] _T_69; // @[FIFO_new.scala 349:74:@125.4]
  wire [2:0] rd_count_next_rd_popping; // @[FIFO_new.scala 349:43:@126.4]
  wire [3:0] _T_71; // @[FIFO_new.scala 350:68:@127.4]
  wire [2:0] _T_72; // @[FIFO_new.scala 350:68:@128.4]
  wire [2:0] rd_count_next_no_rd_popping; // @[FIFO_new.scala 350:46:@129.4]
  wire [2:0] rd_count_next; // @[FIFO_new.scala 351:32:@130.4]
  wire  _T_73; // @[FIFO_new.scala 353:25:@131.4]
  wire [2:0] _GEN_3; // @[FIFO_new.scala 353:39:@132.4]
  wire  rd_count_p_next_rd_popping_not_0; // @[FIFO_new.scala 376:81:@137.4]
  wire  rd_count_p_next_no_rd_popping_not_0; // @[FIFO_new.scala 377:87:@138.4]
  wire  rd_count_p_next_not_0; // @[FIFO_new.scala 378:48:@139.4]
  wire  _GEN_4; // @[FIFO_new.scala 380:47:@141.4]
  wire  _GEN_5; // @[FIFO_new.scala 380:47:@141.4]
  nv_flopram ram ( // @[FIFO_new.scala 254:29:@109.4]
    .reset(ram_reset),
    .io_clk(ram_io_clk),
    .io_di(ram_io_di),
    .io_we(ram_io_we),
    .io_wa(ram_io_wa),
    .io_ra(ram_io_ra),
    .io_dout(ram_io_dout)
  );
  assign _T_26 = ~ wr_busy_int; // @[FIFO_new.scala 192:23:@67.4]
  assign wr_reserving = io_wr_pvld & _T_26; // @[FIFO_new.scala 193:36:@70.4]
  assign _T_33 = wr_count - 3'h1; // @[FIFO_new.scala 200:76:@74.4]
  assign _T_34 = $unsigned(_T_33); // @[FIFO_new.scala 200:76:@75.4]
  assign _T_35 = _T_34[2:0]; // @[FIFO_new.scala 200:76:@76.4]
  assign wr_count_next_wr_popping = wr_reserving ? wr_count : _T_35; // @[FIFO_new.scala 200:43:@77.4]
  assign _T_37 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@78.4]
  assign _T_38 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@79.4]
  assign wr_count_next_no_wr_popping = wr_reserving ? _T_38 : wr_count; // @[FIFO_new.scala 201:46:@80.4]
  assign rd_popping = _T_63 & io_rd_prdy; // @[FIFO_new.scala 374:47:@135.4]
  assign wr_count_next = rd_popping ? wr_count_next_wr_popping : wr_count_next_no_wr_popping; // @[FIFO_new.scala 202:32:@81.4]
  assign wr_count_next_no_wr_popping_is_full = wr_count_next_no_wr_popping == 3'h4; // @[FIFO_new.scala 204:80:@82.4]
  assign wr_count_next_is_full = rd_popping ? 1'h0 : wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 205:40:@83.4]
  assign _T_47 = wr_reserving ^ rd_popping; // @[FIFO_new.scala 212:27:@91.4]
  assign _GEN_0 = _T_47 ? wr_count_next : wr_count; // @[FIFO_new.scala 212:40:@92.4]
  assign _T_52 = _T_50 + 2'h1; // @[FIFO_new.scala 227:47:@96.4]
  assign wr_adr_next = _T_50 + 2'h1; // @[FIFO_new.scala 227:47:@97.4]
  assign _GEN_1 = wr_reserving ? wr_adr_next : _T_50; // @[FIFO_new.scala 228:29:@98.4]
  assign _T_57 = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@103.4]
  assign rd_adr_next_popping = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@104.4]
  assign _GEN_2 = rd_popping ? rd_adr_next_popping : rd_adr; // @[FIFO_new.scala 244:33:@105.4]
  assign _T_67 = rd_count - 3'h1; // @[FIFO_new.scala 349:74:@123.4]
  assign _T_68 = $unsigned(_T_67); // @[FIFO_new.scala 349:74:@124.4]
  assign _T_69 = _T_68[2:0]; // @[FIFO_new.scala 349:74:@125.4]
  assign rd_count_next_rd_popping = wr_reserving ? rd_count : _T_69; // @[FIFO_new.scala 349:43:@126.4]
  assign _T_71 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@127.4]
  assign _T_72 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@128.4]
  assign rd_count_next_no_rd_popping = wr_reserving ? _T_72 : rd_count; // @[FIFO_new.scala 350:46:@129.4]
  assign rd_count_next = rd_popping ? rd_count_next_rd_popping : rd_count_next_no_rd_popping; // @[FIFO_new.scala 351:32:@130.4]
  assign _T_73 = wr_reserving | rd_popping; // @[FIFO_new.scala 353:25:@131.4]
  assign _GEN_3 = _T_73 ? rd_count_next : rd_count; // @[FIFO_new.scala 353:39:@132.4]
  assign rd_count_p_next_rd_popping_not_0 = rd_count_next_rd_popping != 3'h0; // @[FIFO_new.scala 376:81:@137.4]
  assign rd_count_p_next_no_rd_popping_not_0 = rd_count_next_no_rd_popping != 3'h0; // @[FIFO_new.scala 377:87:@138.4]
  assign rd_count_p_next_not_0 = rd_popping ? rd_count_p_next_rd_popping_not_0 : rd_count_p_next_no_rd_popping_not_0; // @[FIFO_new.scala 378:48:@139.4]
  assign _GEN_4 = _T_73 ? rd_count_p_next_not_0 : rd_pvld_p; // @[FIFO_new.scala 380:47:@141.4]
  assign _GEN_5 = _T_73 ? rd_count_p_next_not_0 : _T_63; // @[FIFO_new.scala 380:47:@141.4]
  assign io_wr_prdy = ~ wr_busy_int; // @[FIFO_new.scala 192:20:@68.4]
  assign io_rd_pvld = rd_pvld_p; // @[FIFO_new.scala 386:28:@146.4]
  assign io_rd_pd = ram_io_dout; // @[FIFO_new.scala 385:26:@145.4]
  assign ram_reset = reset; // @[:@111.4]
  assign ram_io_clk = clock; // @[FIFO_new.scala 255:24:@112.4]
  assign ram_io_di = io_wr_pd; // @[FIFO_new.scala 261:23:@116.4]
  assign ram_io_we = io_wr_pvld & _T_26; // @[FIFO_new.scala 260:23:@115.4]
  assign ram_io_wa = _T_50; // @[FIFO_new.scala 258:31:@114.4]
  assign ram_io_ra = {{1'd0}, rd_adr}; // @[FIFO_new.scala 266:27:@117.4]
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
