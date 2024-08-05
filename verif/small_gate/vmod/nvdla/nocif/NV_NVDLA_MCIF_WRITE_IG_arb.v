module NV_NVDLA_BC_OS_pipe( // @[:@3.2]
  input         reset, // @[:@5.4]
  input         io_clk, // @[:@6.4]
  input         io_vi, // @[:@6.4]
  output        io_ro, // @[:@6.4]
  input  [44:0] io_di, // @[:@6.4]
  output        io_vo, // @[:@6.4]
  input         io_ri, // @[:@6.4]
  output [44:0] io_dout // @[:@6.4]
);
  reg  pipe_valid; // @[BC_OS_pipe.scala 57:29:@9.4]
  reg [31:0] _RAND_0;
  reg [44:0] pipe_data; // @[BC_OS_pipe.scala 58:50:@10.4]
  reg [63:0] _RAND_1;
  reg  pipe_ready; // @[BC_OS_pipe.scala 59:29:@11.4]
  reg [31:0] _RAND_2;
  wire  _T_26; // @[BC_OS_pipe.scala 61:36:@12.4]
  wire  pipe_ready_bc; // @[BC_OS_pipe.scala 61:33:@13.4]
  wire  _T_29; // @[BC_OS_pipe.scala 62:22:@15.4]
  wire  _T_30; // @[BC_OS_pipe.scala 63:35:@17.4]
  wire [44:0] _T_31; // @[BC_OS_pipe.scala 63:21:@18.4]
  reg  skid_valid; // @[BC_OS_pipe.scala 67:29:@21.4]
  reg [31:0] _RAND_3;
  reg  skid_ready_flop; // @[BC_OS_pipe.scala 68:34:@22.4]
  reg [31:0] _RAND_4;
  reg [44:0] skid_data; // @[BC_OS_pipe.scala 69:50:@23.4]
  reg [63:0] _RAND_5;
  wire  _T_43; // @[BC_OS_pipe.scala 79:30:@29.4]
  wire  pipe_skid_ready; // @[BC_OS_pipe.scala 73:31:@27.4 BC_OS_pipe.scala 92:21:@48.4]
  wire  _T_44; // @[BC_OS_pipe.scala 79:52:@30.4]
  wire  skid_catch; // @[BC_OS_pipe.scala 79:49:@31.4]
  wire  _T_46; // @[BC_OS_pipe.scala 80:52:@33.4]
  wire  skid_ready; // @[BC_OS_pipe.scala 80:22:@34.4]
  wire  _T_49; // @[BC_OS_pipe.scala 81:22:@37.4]
  wire [44:0] _T_50; // @[BC_OS_pipe.scala 85:21:@41.4]
  wire  _T_51; // @[BC_OS_pipe.scala 87:27:@43.4]
  wire [44:0] _T_52; // @[BC_OS_pipe.scala 88:26:@45.4]
  wire  pipe_skid_valid; // @[BC_OS_pipe.scala 72:31:@26.4 BC_OS_pipe.scala 87:21:@44.4]
  wire [44:0] pipe_skid_data; // @[BC_OS_pipe.scala 74:30:@28.4 BC_OS_pipe.scala 88:20:@46.4]
  assign _T_26 = ~ pipe_valid; // @[BC_OS_pipe.scala 61:36:@12.4]
  assign pipe_ready_bc = pipe_ready | _T_26; // @[BC_OS_pipe.scala 61:33:@13.4]
  assign _T_29 = pipe_ready_bc ? io_vi : 1'h1; // @[BC_OS_pipe.scala 62:22:@15.4]
  assign _T_30 = pipe_ready_bc & io_vi; // @[BC_OS_pipe.scala 63:35:@17.4]
  assign _T_31 = _T_30 ? io_di : pipe_data; // @[BC_OS_pipe.scala 63:21:@18.4]
  assign _T_43 = pipe_valid & skid_ready_flop; // @[BC_OS_pipe.scala 79:30:@29.4]
  assign pipe_skid_ready = io_ri; // @[BC_OS_pipe.scala 73:31:@27.4 BC_OS_pipe.scala 92:21:@48.4]
  assign _T_44 = ~ pipe_skid_ready; // @[BC_OS_pipe.scala 79:52:@30.4]
  assign skid_catch = _T_43 & _T_44; // @[BC_OS_pipe.scala 79:49:@31.4]
  assign _T_46 = ~ skid_catch; // @[BC_OS_pipe.scala 80:52:@33.4]
  assign skid_ready = skid_valid ? pipe_skid_ready : _T_46; // @[BC_OS_pipe.scala 80:22:@34.4]
  assign _T_49 = skid_valid ? _T_44 : skid_catch; // @[BC_OS_pipe.scala 81:22:@37.4]
  assign _T_50 = skid_catch ? pipe_data : skid_data; // @[BC_OS_pipe.scala 85:21:@41.4]
  assign _T_51 = skid_ready_flop ? pipe_valid : skid_valid; // @[BC_OS_pipe.scala 87:27:@43.4]
  assign _T_52 = skid_ready_flop ? pipe_data : skid_data; // @[BC_OS_pipe.scala 88:26:@45.4]
  assign pipe_skid_valid = _T_51; // @[BC_OS_pipe.scala 72:31:@26.4 BC_OS_pipe.scala 87:21:@44.4]
  assign pipe_skid_data = _T_52; // @[BC_OS_pipe.scala 74:30:@28.4 BC_OS_pipe.scala 88:20:@46.4]
  assign io_ro = pipe_ready | _T_26; // @[BC_OS_pipe.scala 64:11:@20.4]
  assign io_vo = pipe_skid_valid; // @[BC_OS_pipe.scala 91:11:@47.4]
  assign io_dout = pipe_skid_data; // @[BC_OS_pipe.scala 93:13:@49.4]
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
  skid_data = _RAND_5[44:0];
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
      pipe_data <= 45'h0;
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
      skid_data <= 45'h0;
    end else begin
      if (skid_catch) begin
        skid_data <= pipe_data;
      end
    end
  end
endmodule
module nv_flopram( // @[:@154.2]
  input         reset, // @[:@156.4]
  input         io_clk, // @[:@157.4]
  input  [64:0] io_di, // @[:@157.4]
  input         io_we, // @[:@157.4]
  input  [1:0]  io_wa, // @[:@157.4]
  input  [2:0]  io_ra, // @[:@157.4]
  output [64:0] io_dout // @[:@157.4]
);
  reg [64:0] _T_22; // @[nv_flopram.scala 68:61:@161.4]
  reg [95:0] _RAND_0;
  reg [64:0] _T_25; // @[nv_flopram.scala 68:61:@162.4]
  reg [95:0] _RAND_1;
  reg [64:0] _T_28; // @[nv_flopram.scala 68:61:@163.4]
  reg [95:0] _RAND_2;
  reg [64:0] _T_31; // @[nv_flopram.scala 68:61:@164.4]
  reg [95:0] _RAND_3;
  wire  _T_35; // @[nv_flopram.scala 73:32:@167.6]
  wire [64:0] _GEN_0; // @[nv_flopram.scala 73:40:@168.6]
  wire  _T_37; // @[nv_flopram.scala 73:32:@171.6]
  wire [64:0] _GEN_1; // @[nv_flopram.scala 73:40:@172.6]
  wire  _T_39; // @[nv_flopram.scala 73:32:@175.6]
  wire [64:0] _GEN_2; // @[nv_flopram.scala 73:40:@176.6]
  wire  _T_41; // @[nv_flopram.scala 73:32:@179.6]
  wire [64:0] _GEN_3; // @[nv_flopram.scala 73:40:@180.6]
  wire [64:0] _GEN_4; // @[nv_flopram.scala 70:16:@166.4]
  wire [64:0] _GEN_5; // @[nv_flopram.scala 70:16:@166.4]
  wire [64:0] _GEN_6; // @[nv_flopram.scala 70:16:@166.4]
  wire [64:0] _GEN_7; // @[nv_flopram.scala 70:16:@166.4]
  wire  _T_48; // @[Mux.scala 46:19:@185.4]
  wire [64:0] _T_49; // @[Mux.scala 46:16:@186.4]
  wire  _T_50; // @[Mux.scala 46:19:@187.4]
  wire [64:0] _T_51; // @[Mux.scala 46:16:@188.4]
  wire  _T_52; // @[Mux.scala 46:19:@189.4]
  wire [64:0] _T_53; // @[Mux.scala 46:16:@190.4]
  wire  _T_54; // @[Mux.scala 46:19:@191.4]
  wire [64:0] _T_55; // @[Mux.scala 46:16:@192.4]
  wire  _T_56; // @[Mux.scala 46:19:@193.4]
  assign _T_35 = io_wa == 2'h0; // @[nv_flopram.scala 73:32:@167.6]
  assign _GEN_0 = _T_35 ? io_di : _T_22; // @[nv_flopram.scala 73:40:@168.6]
  assign _T_37 = io_wa == 2'h1; // @[nv_flopram.scala 73:32:@171.6]
  assign _GEN_1 = _T_37 ? io_di : _T_25; // @[nv_flopram.scala 73:40:@172.6]
  assign _T_39 = io_wa == 2'h2; // @[nv_flopram.scala 73:32:@175.6]
  assign _GEN_2 = _T_39 ? io_di : _T_28; // @[nv_flopram.scala 73:40:@176.6]
  assign _T_41 = io_wa == 2'h3; // @[nv_flopram.scala 73:32:@179.6]
  assign _GEN_3 = _T_41 ? io_di : _T_31; // @[nv_flopram.scala 73:40:@180.6]
  assign _GEN_4 = io_we ? _GEN_0 : _T_22; // @[nv_flopram.scala 70:16:@166.4]
  assign _GEN_5 = io_we ? _GEN_1 : _T_25; // @[nv_flopram.scala 70:16:@166.4]
  assign _GEN_6 = io_we ? _GEN_2 : _T_28; // @[nv_flopram.scala 70:16:@166.4]
  assign _GEN_7 = io_we ? _GEN_3 : _T_31; // @[nv_flopram.scala 70:16:@166.4]
  assign _T_48 = 3'h4 == io_ra; // @[Mux.scala 46:19:@185.4]
  assign _T_49 = _T_48 ? io_di : 65'h0; // @[Mux.scala 46:16:@186.4]
  assign _T_50 = 3'h3 == io_ra; // @[Mux.scala 46:19:@187.4]
  assign _T_51 = _T_50 ? _T_31 : _T_49; // @[Mux.scala 46:16:@188.4]
  assign _T_52 = 3'h2 == io_ra; // @[Mux.scala 46:19:@189.4]
  assign _T_53 = _T_52 ? _T_28 : _T_51; // @[Mux.scala 46:16:@190.4]
  assign _T_54 = 3'h1 == io_ra; // @[Mux.scala 46:19:@191.4]
  assign _T_55 = _T_54 ? _T_25 : _T_53; // @[Mux.scala 46:16:@192.4]
  assign _T_56 = 3'h0 == io_ra; // @[Mux.scala 46:19:@193.4]
  assign io_dout = _T_56 ? _T_22 : _T_55; // @[nv_flopram.scala 83:13:@195.4]
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
  _RAND_0 = {3{`RANDOM}};
  _T_22 = _RAND_0[64:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {3{`RANDOM}};
  _T_25 = _RAND_1[64:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {3{`RANDOM}};
  _T_28 = _RAND_2[64:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {3{`RANDOM}};
  _T_31 = _RAND_3[64:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      _T_22 <= 65'h0;
    end else begin
      if (io_we) begin
        if (_T_35) begin
          _T_22 <= io_di;
        end
      end
    end
    if (reset) begin
      _T_25 <= 65'h0;
    end else begin
      if (io_we) begin
        if (_T_37) begin
          _T_25 <= io_di;
        end
      end
    end
    if (reset) begin
      _T_28 <= 65'h0;
    end else begin
      if (io_we) begin
        if (_T_39) begin
          _T_28 <= io_di;
        end
      end
    end
    if (reset) begin
      _T_31 <= 65'h0;
    end else begin
      if (io_we) begin
        if (_T_41) begin
          _T_31 <= io_di;
        end
      end
    end
  end
endmodule
module NV_NVDLA_fifo_new( // @[:@197.2]
  input         clock, // @[:@198.4]
  input         reset, // @[:@199.4]
  input         io_wr_pvld, // @[:@200.4]
  output        io_wr_prdy, // @[:@200.4]
  input  [64:0] io_wr_pd, // @[:@200.4]
  output [2:0]  io_wr_count, // @[:@200.4]
  output        io_rd_pvld, // @[:@200.4]
  input         io_rd_prdy, // @[:@200.4]
  output [64:0] io_rd_pd // @[:@200.4]
);
  wire  ram_reset; // @[FIFO_new.scala 254:29:@254.4]
  wire  ram_io_clk; // @[FIFO_new.scala 254:29:@254.4]
  wire [64:0] ram_io_di; // @[FIFO_new.scala 254:29:@254.4]
  wire  ram_io_we; // @[FIFO_new.scala 254:29:@254.4]
  wire [1:0] ram_io_wa; // @[FIFO_new.scala 254:29:@254.4]
  wire [2:0] ram_io_ra; // @[FIFO_new.scala 254:29:@254.4]
  wire [64:0] ram_io_dout; // @[FIFO_new.scala 254:29:@254.4]
  reg  wr_busy_int; // @[FIFO_new.scala 162:56:@209.4]
  reg [31:0] _RAND_0;
  wire  _T_28; // @[FIFO_new.scala 192:23:@211.4]
  wire  wr_reserving; // @[FIFO_new.scala 193:36:@214.4]
  reg [2:0] wr_count; // @[FIFO_new.scala 196:53:@217.4]
  reg [31:0] _RAND_1;
  wire [3:0] _T_35; // @[FIFO_new.scala 200:76:@219.4]
  wire [3:0] _T_36; // @[FIFO_new.scala 200:76:@220.4]
  wire [2:0] _T_37; // @[FIFO_new.scala 200:76:@221.4]
  wire [2:0] wr_count_next_wr_popping; // @[FIFO_new.scala 200:43:@222.4]
  wire [3:0] _T_39; // @[FIFO_new.scala 201:69:@223.4]
  wire [2:0] _T_40; // @[FIFO_new.scala 201:69:@224.4]
  wire [2:0] wr_count_next_no_wr_popping; // @[FIFO_new.scala 201:46:@225.4]
  reg  _T_65; // @[FIFO_new.scala 345:108:@266.4]
  reg [31:0] _RAND_2;
  wire  rd_popping; // @[FIFO_new.scala 374:47:@280.4]
  wire [2:0] wr_count_next; // @[FIFO_new.scala 202:32:@226.4]
  wire  wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 204:80:@227.4]
  wire  wr_count_next_is_full; // @[FIFO_new.scala 205:40:@228.4]
  wire  _T_49; // @[FIFO_new.scala 212:27:@236.4]
  wire [2:0] _GEN_0; // @[FIFO_new.scala 212:40:@237.4]
  reg [1:0] _T_52; // @[FIFO_new.scala 224:68:@240.4]
  reg [31:0] _RAND_3;
  wire [2:0] _T_54; // @[FIFO_new.scala 227:47:@241.4]
  wire [1:0] wr_adr_next; // @[FIFO_new.scala 227:47:@242.4]
  wire [1:0] _GEN_1; // @[FIFO_new.scala 228:29:@243.4]
  reg [1:0] rd_adr; // @[FIFO_new.scala 233:63:@247.4]
  reg [31:0] _RAND_4;
  wire [2:0] _T_59; // @[FIFO_new.scala 235:42:@248.4]
  wire [1:0] rd_adr_next_popping; // @[FIFO_new.scala 235:42:@249.4]
  wire [1:0] _GEN_2; // @[FIFO_new.scala 244:33:@250.4]
  reg  rd_pvld_p; // @[FIFO_new.scala 343:137:@265.4]
  reg [31:0] _RAND_5;
  reg [2:0] rd_count; // @[FIFO_new.scala 347:53:@267.4]
  reg [31:0] _RAND_6;
  wire [3:0] _T_69; // @[FIFO_new.scala 349:74:@268.4]
  wire [3:0] _T_70; // @[FIFO_new.scala 349:74:@269.4]
  wire [2:0] _T_71; // @[FIFO_new.scala 349:74:@270.4]
  wire [2:0] rd_count_next_rd_popping; // @[FIFO_new.scala 349:43:@271.4]
  wire [3:0] _T_73; // @[FIFO_new.scala 350:68:@272.4]
  wire [2:0] _T_74; // @[FIFO_new.scala 350:68:@273.4]
  wire [2:0] rd_count_next_no_rd_popping; // @[FIFO_new.scala 350:46:@274.4]
  wire [2:0] rd_count_next; // @[FIFO_new.scala 351:32:@275.4]
  wire  _T_75; // @[FIFO_new.scala 353:25:@276.4]
  wire [2:0] _GEN_3; // @[FIFO_new.scala 353:39:@277.4]
  wire  rd_count_p_next_rd_popping_not_0; // @[FIFO_new.scala 376:81:@282.4]
  wire  rd_count_p_next_no_rd_popping_not_0; // @[FIFO_new.scala 377:87:@283.4]
  wire  rd_count_p_next_not_0; // @[FIFO_new.scala 378:48:@284.4]
  wire  _GEN_4; // @[FIFO_new.scala 380:47:@286.4]
  wire  _GEN_5; // @[FIFO_new.scala 380:47:@286.4]
  nv_flopram ram ( // @[FIFO_new.scala 254:29:@254.4]
    .reset(ram_reset),
    .io_clk(ram_io_clk),
    .io_di(ram_io_di),
    .io_we(ram_io_we),
    .io_wa(ram_io_wa),
    .io_ra(ram_io_ra),
    .io_dout(ram_io_dout)
  );
  assign _T_28 = ~ wr_busy_int; // @[FIFO_new.scala 192:23:@211.4]
  assign wr_reserving = io_wr_pvld & _T_28; // @[FIFO_new.scala 193:36:@214.4]
  assign _T_35 = wr_count - 3'h1; // @[FIFO_new.scala 200:76:@219.4]
  assign _T_36 = $unsigned(_T_35); // @[FIFO_new.scala 200:76:@220.4]
  assign _T_37 = _T_36[2:0]; // @[FIFO_new.scala 200:76:@221.4]
  assign wr_count_next_wr_popping = wr_reserving ? wr_count : _T_37; // @[FIFO_new.scala 200:43:@222.4]
  assign _T_39 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@223.4]
  assign _T_40 = wr_count + 3'h1; // @[FIFO_new.scala 201:69:@224.4]
  assign wr_count_next_no_wr_popping = wr_reserving ? _T_40 : wr_count; // @[FIFO_new.scala 201:46:@225.4]
  assign rd_popping = _T_65 & io_rd_prdy; // @[FIFO_new.scala 374:47:@280.4]
  assign wr_count_next = rd_popping ? wr_count_next_wr_popping : wr_count_next_no_wr_popping; // @[FIFO_new.scala 202:32:@226.4]
  assign wr_count_next_no_wr_popping_is_full = wr_count_next_no_wr_popping == 3'h4; // @[FIFO_new.scala 204:80:@227.4]
  assign wr_count_next_is_full = rd_popping ? 1'h0 : wr_count_next_no_wr_popping_is_full; // @[FIFO_new.scala 205:40:@228.4]
  assign _T_49 = wr_reserving ^ rd_popping; // @[FIFO_new.scala 212:27:@236.4]
  assign _GEN_0 = _T_49 ? wr_count_next : wr_count; // @[FIFO_new.scala 212:40:@237.4]
  assign _T_54 = _T_52 + 2'h1; // @[FIFO_new.scala 227:47:@241.4]
  assign wr_adr_next = _T_52 + 2'h1; // @[FIFO_new.scala 227:47:@242.4]
  assign _GEN_1 = wr_reserving ? wr_adr_next : _T_52; // @[FIFO_new.scala 228:29:@243.4]
  assign _T_59 = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@248.4]
  assign rd_adr_next_popping = rd_adr + 2'h1; // @[FIFO_new.scala 235:42:@249.4]
  assign _GEN_2 = rd_popping ? rd_adr_next_popping : rd_adr; // @[FIFO_new.scala 244:33:@250.4]
  assign _T_69 = rd_count - 3'h1; // @[FIFO_new.scala 349:74:@268.4]
  assign _T_70 = $unsigned(_T_69); // @[FIFO_new.scala 349:74:@269.4]
  assign _T_71 = _T_70[2:0]; // @[FIFO_new.scala 349:74:@270.4]
  assign rd_count_next_rd_popping = wr_reserving ? rd_count : _T_71; // @[FIFO_new.scala 349:43:@271.4]
  assign _T_73 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@272.4]
  assign _T_74 = rd_count + 3'h1; // @[FIFO_new.scala 350:68:@273.4]
  assign rd_count_next_no_rd_popping = wr_reserving ? _T_74 : rd_count; // @[FIFO_new.scala 350:46:@274.4]
  assign rd_count_next = rd_popping ? rd_count_next_rd_popping : rd_count_next_no_rd_popping; // @[FIFO_new.scala 351:32:@275.4]
  assign _T_75 = wr_reserving | rd_popping; // @[FIFO_new.scala 353:25:@276.4]
  assign _GEN_3 = _T_75 ? rd_count_next : rd_count; // @[FIFO_new.scala 353:39:@277.4]
  assign rd_count_p_next_rd_popping_not_0 = rd_count_next_rd_popping != 3'h0; // @[FIFO_new.scala 376:81:@282.4]
  assign rd_count_p_next_no_rd_popping_not_0 = rd_count_next_no_rd_popping != 3'h0; // @[FIFO_new.scala 377:87:@283.4]
  assign rd_count_p_next_not_0 = rd_popping ? rd_count_p_next_rd_popping_not_0 : rd_count_p_next_no_rd_popping_not_0; // @[FIFO_new.scala 378:48:@284.4]
  assign _GEN_4 = _T_75 ? rd_count_p_next_not_0 : rd_pvld_p; // @[FIFO_new.scala 380:47:@286.4]
  assign _GEN_5 = _T_75 ? rd_count_p_next_not_0 : _T_65; // @[FIFO_new.scala 380:47:@286.4]
  assign io_wr_prdy = ~ wr_busy_int; // @[FIFO_new.scala 192:20:@212.4]
  assign io_wr_count = wr_count; // @[FIFO_new.scala 198:29:@218.4]
  assign io_rd_pvld = rd_pvld_p; // @[FIFO_new.scala 386:28:@291.4]
  assign io_rd_pd = ram_io_dout; // @[FIFO_new.scala 385:26:@290.4]
  assign ram_reset = reset; // @[:@256.4]
  assign ram_io_clk = clock; // @[FIFO_new.scala 255:24:@257.4]
  assign ram_io_di = io_wr_pd; // @[FIFO_new.scala 261:23:@261.4]
  assign ram_io_we = io_wr_pvld & _T_28; // @[FIFO_new.scala 260:23:@260.4]
  assign ram_io_wa = _T_52; // @[FIFO_new.scala 258:31:@259.4]
  assign ram_io_ra = {{1'd0}, rd_adr}; // @[FIFO_new.scala 266:27:@262.4]
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
  _T_65 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_52 = _RAND_3[1:0];
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
      if (_T_49) begin
        if (rd_popping) begin
          if (!(wr_reserving)) begin
            wr_count <= _T_37;
          end
        end else begin
          if (wr_reserving) begin
            wr_count <= _T_40;
          end
        end
      end
    end
    if (reset) begin
      _T_65 <= 1'h0;
    end else begin
      if (_T_75) begin
        if (rd_popping) begin
          _T_65 <= rd_count_p_next_rd_popping_not_0;
        end else begin
          _T_65 <= rd_count_p_next_no_rd_popping_not_0;
        end
      end
    end
    if (reset) begin
      _T_52 <= 2'h0;
    end else begin
      if (wr_reserving) begin
        _T_52 <= wr_adr_next;
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
      if (_T_75) begin
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
      if (_T_75) begin
        if (rd_popping) begin
          if (!(wr_reserving)) begin
            rd_count <= _T_71;
          end
        end else begin
          if (wr_reserving) begin
            rd_count <= _T_74;
          end
        end
      end
    end
  end
endmodule
module NV_NVDLA_arb( // @[:@627.2]
  input        reset, // @[:@629.4]
  input        io_clk, // @[:@630.4]
  input        io_req_0, // @[:@630.4]
  input        io_req_1, // @[:@630.4]
  input        io_req_2, // @[:@630.4]
  input  [7:0] io_wt_0, // @[:@630.4]
  input  [7:0] io_wt_1, // @[:@630.4]
  input  [7:0] io_wt_2, // @[:@630.4]
  input        io_gnt_busy, // @[:@630.4]
  output       io_gnt_0, // @[:@630.4]
  output       io_gnt_1, // @[:@630.4]
  output       io_gnt_2, // @[:@630.4]
  output       io_gnt_3, // @[:@630.4]
  output       io_gnt_4 // @[:@630.4]
);
  wire  _T_70; // @[NV_NVDLA_arb.scala 41:68:@632.4]
  wire  _T_71; // @[NV_NVDLA_arb.scala 41:56:@633.4]
  wire  _T_73; // @[NV_NVDLA_arb.scala 41:68:@634.4]
  wire  _T_74; // @[NV_NVDLA_arb.scala 41:56:@635.4]
  wire  _T_76; // @[NV_NVDLA_arb.scala 41:68:@636.4]
  wire  _T_77; // @[NV_NVDLA_arb.scala 41:56:@637.4]
  wire [4:0] _T_98; // @[NV_NVDLA_arb.scala 41:75:@651.4]
  wire [8:0] _T_111; // @[NV_NVDLA_arb.scala 45:36:@653.4]
  wire [8:0] _T_112; // @[NV_NVDLA_arb.scala 45:36:@654.4]
  wire [7:0] _T_113; // @[NV_NVDLA_arb.scala 45:36:@655.4]
  wire [8:0] _T_115; // @[NV_NVDLA_arb.scala 45:36:@657.4]
  wire [8:0] _T_116; // @[NV_NVDLA_arb.scala 45:36:@658.4]
  wire [7:0] _T_117; // @[NV_NVDLA_arb.scala 45:36:@659.4]
  wire [8:0] _T_119; // @[NV_NVDLA_arb.scala 45:36:@661.4]
  wire [8:0] _T_120; // @[NV_NVDLA_arb.scala 45:36:@662.4]
  wire [7:0] _T_121; // @[NV_NVDLA_arb.scala 45:36:@663.4]
  wire [8:0] _T_123; // @[NV_NVDLA_arb.scala 45:36:@665.4]
  wire [8:0] _T_124; // @[NV_NVDLA_arb.scala 45:36:@666.4]
  wire [7:0] _T_125; // @[NV_NVDLA_arb.scala 45:36:@667.4]
  reg [4:0] _T_132; // @[NV_NVDLA_arb.scala 49:26:@673.4]
  reg [31:0] _RAND_0;
  reg [7:0] _T_135; // @[NV_NVDLA_arb.scala 50:26:@674.4]
  reg [31:0] _RAND_1;
  wire  _T_139; // @[NV_NVDLA_arb.scala 54:14:@677.4]
  wire  _T_141; // @[NV_NVDLA_arb.scala 54:37:@678.4]
  wire  _T_142; // @[NV_NVDLA_arb.scala 54:31:@679.4]
  wire [4:0] _T_146; // @[NV_NVDLA_arb.scala 55:31:@684.6]
  wire [4:0] _GEN_0; // @[NV_NVDLA_arb.scala 54:45:@680.4]
  wire  _T_151; // @[NV_NVDLA_arb.scala 68:18:@690.4]
  wire [4:0] _T_152; // @[NV_NVDLA_arb.scala 68:35:@691.4]
  wire  _T_154; // @[NV_NVDLA_arb.scala 68:46:@692.4]
  wire  _T_155; // @[NV_NVDLA_arb.scala 68:28:@693.4]
  wire  _T_156; // @[NV_NVDLA_arb.scala 68:26:@694.4]
  wire [12:0] _T_344; // @[NV_NVDLA_arb.scala 70:52:@856.6]
  wire [4:0] _T_345; // @[NV_NVDLA_arb.scala 70:59:@857.6]
  wire  _T_346; // @[NV_NVDLA_arb.scala 70:26:@858.6]
  wire  _T_369; // @[NV_NVDLA_arb.scala 72:84:@876.8]
  wire  _T_370; // @[NV_NVDLA_arb.scala 72:84:@877.8]
  wire  _T_371; // @[NV_NVDLA_arb.scala 72:84:@878.8]
  wire  _T_372; // @[NV_NVDLA_arb.scala 72:84:@879.8]
  wire  _T_373; // @[NV_NVDLA_arb.scala 72:84:@880.8]
  wire [7:0] _T_374; // @[Mux.scala 61:16:@881.8]
  wire [7:0] _T_375; // @[Mux.scala 61:16:@882.8]
  wire [7:0] _T_376; // @[Mux.scala 61:16:@883.8]
  wire [7:0] _T_377; // @[Mux.scala 61:16:@884.8]
  wire [7:0] _T_378; // @[Mux.scala 61:16:@885.8]
  wire [12:0] _T_307; // @[NV_NVDLA_arb.scala 70:52:@824.6]
  wire [4:0] _T_308; // @[NV_NVDLA_arb.scala 70:59:@825.6]
  wire  _T_309; // @[NV_NVDLA_arb.scala 70:26:@826.6]
  wire [7:0] _T_337; // @[Mux.scala 61:16:@849.8]
  wire [7:0] _T_338; // @[Mux.scala 61:16:@850.8]
  wire [7:0] _T_339; // @[Mux.scala 61:16:@851.8]
  wire [7:0] _T_340; // @[Mux.scala 61:16:@852.8]
  wire [7:0] _T_341; // @[Mux.scala 61:16:@853.8]
  wire [8:0] _T_270; // @[NV_NVDLA_arb.scala 70:52:@792.6]
  wire [4:0] _T_271; // @[NV_NVDLA_arb.scala 70:59:@793.6]
  wire  _T_272; // @[NV_NVDLA_arb.scala 70:26:@794.6]
  wire [7:0] _T_300; // @[Mux.scala 61:16:@817.8]
  wire [7:0] _T_301; // @[Mux.scala 61:16:@818.8]
  wire [7:0] _T_302; // @[Mux.scala 61:16:@819.8]
  wire [7:0] _T_303; // @[Mux.scala 61:16:@820.8]
  wire [7:0] _T_304; // @[Mux.scala 61:16:@821.8]
  wire [8:0] _T_233; // @[NV_NVDLA_arb.scala 70:52:@760.6]
  wire [4:0] _T_234; // @[NV_NVDLA_arb.scala 70:59:@761.6]
  wire  _T_235; // @[NV_NVDLA_arb.scala 70:26:@762.6]
  wire [7:0] _T_263; // @[Mux.scala 61:16:@785.8]
  wire [7:0] _T_264; // @[Mux.scala 61:16:@786.8]
  wire [7:0] _T_265; // @[Mux.scala 61:16:@787.8]
  wire [7:0] _T_266; // @[Mux.scala 61:16:@788.8]
  wire [7:0] _T_267; // @[Mux.scala 61:16:@789.8]
  wire [6:0] _T_196; // @[NV_NVDLA_arb.scala 70:52:@728.6]
  wire [4:0] _T_197; // @[NV_NVDLA_arb.scala 70:59:@729.6]
  wire  _T_198; // @[NV_NVDLA_arb.scala 70:26:@730.6]
  wire [7:0] _T_226; // @[Mux.scala 61:16:@753.8]
  wire [7:0] _T_227; // @[Mux.scala 61:16:@754.8]
  wire [7:0] _T_228; // @[Mux.scala 61:16:@755.8]
  wire [7:0] _T_229; // @[Mux.scala 61:16:@756.8]
  wire [7:0] _T_230; // @[Mux.scala 61:16:@757.8]
  wire [6:0] _T_159; // @[NV_NVDLA_arb.scala 70:52:@696.6]
  wire [4:0] _T_160; // @[NV_NVDLA_arb.scala 70:59:@697.6]
  wire  _T_161; // @[NV_NVDLA_arb.scala 70:26:@698.6]
  wire [7:0] _GEN_3; // @[NV_NVDLA_arb.scala 70:66:@699.6]
  wire [7:0] _GEN_5; // @[NV_NVDLA_arb.scala 70:66:@731.6]
  wire [7:0] _GEN_7; // @[NV_NVDLA_arb.scala 70:66:@763.6]
  wire [7:0] _GEN_9; // @[NV_NVDLA_arb.scala 70:66:@795.6]
  wire [7:0] _GEN_11; // @[NV_NVDLA_arb.scala 70:66:@827.6]
  wire [7:0] _GEN_13; // @[NV_NVDLA_arb.scala 70:66:@859.6]
  wire [8:0] _T_380; // @[NV_NVDLA_arb.scala 78:32:@891.6]
  wire [8:0] _T_381; // @[NV_NVDLA_arb.scala 78:32:@892.6]
  wire [7:0] _T_382; // @[NV_NVDLA_arb.scala 78:32:@893.6]
  wire [7:0] _GEN_15; // @[NV_NVDLA_arb.scala 68:52:@695.4]
  wire [7:0] _GEN_1; // @[NV_NVDLA_arb.scala 54:45:@680.4]
  wire [4:0] _T_178; // @[Mux.scala 61:16:@710.8]
  wire [4:0] _T_179; // @[Mux.scala 61:16:@711.8]
  wire [4:0] _T_180; // @[Mux.scala 61:16:@712.8]
  wire [4:0] _T_181; // @[Mux.scala 61:16:@713.8]
  wire [4:0] _T_182; // @[Mux.scala 61:16:@714.8]
  wire [4:0] _GEN_2; // @[NV_NVDLA_arb.scala 70:66:@699.6]
  wire [4:0] _T_215; // @[Mux.scala 61:16:@742.8]
  wire [4:0] _T_216; // @[Mux.scala 61:16:@743.8]
  wire [4:0] _T_217; // @[Mux.scala 61:16:@744.8]
  wire [4:0] _T_218; // @[Mux.scala 61:16:@745.8]
  wire [4:0] _T_219; // @[Mux.scala 61:16:@746.8]
  wire [4:0] _GEN_4; // @[NV_NVDLA_arb.scala 70:66:@731.6]
  wire [4:0] _T_252; // @[Mux.scala 61:16:@774.8]
  wire [4:0] _T_253; // @[Mux.scala 61:16:@775.8]
  wire [4:0] _T_254; // @[Mux.scala 61:16:@776.8]
  wire [4:0] _T_255; // @[Mux.scala 61:16:@777.8]
  wire [4:0] _T_256; // @[Mux.scala 61:16:@778.8]
  wire [4:0] _GEN_6; // @[NV_NVDLA_arb.scala 70:66:@763.6]
  wire [4:0] _T_289; // @[Mux.scala 61:16:@806.8]
  wire [4:0] _T_290; // @[Mux.scala 61:16:@807.8]
  wire [4:0] _T_291; // @[Mux.scala 61:16:@808.8]
  wire [4:0] _T_292; // @[Mux.scala 61:16:@809.8]
  wire [4:0] _T_293; // @[Mux.scala 61:16:@810.8]
  wire [4:0] _GEN_8; // @[NV_NVDLA_arb.scala 70:66:@795.6]
  wire [4:0] _T_326; // @[Mux.scala 61:16:@838.8]
  wire [4:0] _T_327; // @[Mux.scala 61:16:@839.8]
  wire [4:0] _T_328; // @[Mux.scala 61:16:@840.8]
  wire [4:0] _T_329; // @[Mux.scala 61:16:@841.8]
  wire [4:0] _T_330; // @[Mux.scala 61:16:@842.8]
  wire [4:0] _GEN_10; // @[NV_NVDLA_arb.scala 70:66:@827.6]
  wire [4:0] _GEN_12; // @[NV_NVDLA_arb.scala 70:66:@859.6]
  wire [4:0] _GEN_14; // @[NV_NVDLA_arb.scala 68:52:@695.4]
  wire  _T_384; // @[NV_NVDLA_arb.scala 84:52:@897.4]
  wire  _T_387; // @[NV_NVDLA_arb.scala 84:52:@901.4]
  wire  _T_390; // @[NV_NVDLA_arb.scala 84:52:@905.4]
  wire  _T_393; // @[NV_NVDLA_arb.scala 84:52:@909.4]
  wire  _T_396; // @[NV_NVDLA_arb.scala 84:52:@913.4]
  assign _T_70 = io_wt_0 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@632.4]
  assign _T_71 = io_req_0 & _T_70; // @[NV_NVDLA_arb.scala 41:56:@633.4]
  assign _T_73 = io_wt_1 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@634.4]
  assign _T_74 = io_req_1 & _T_73; // @[NV_NVDLA_arb.scala 41:56:@635.4]
  assign _T_76 = io_wt_2 != 8'h0; // @[NV_NVDLA_arb.scala 41:68:@636.4]
  assign _T_77 = io_req_2 & _T_76; // @[NV_NVDLA_arb.scala 41:56:@637.4]
  assign _T_98 = {2'h0,_T_77,_T_74,_T_71}; // @[NV_NVDLA_arb.scala 41:75:@651.4]
  assign _T_111 = io_wt_0 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@653.4]
  assign _T_112 = $unsigned(_T_111); // @[NV_NVDLA_arb.scala 45:36:@654.4]
  assign _T_113 = _T_112[7:0]; // @[NV_NVDLA_arb.scala 45:36:@655.4]
  assign _T_115 = io_wt_1 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@657.4]
  assign _T_116 = $unsigned(_T_115); // @[NV_NVDLA_arb.scala 45:36:@658.4]
  assign _T_117 = _T_116[7:0]; // @[NV_NVDLA_arb.scala 45:36:@659.4]
  assign _T_119 = io_wt_2 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@661.4]
  assign _T_120 = $unsigned(_T_119); // @[NV_NVDLA_arb.scala 45:36:@662.4]
  assign _T_121 = _T_120[7:0]; // @[NV_NVDLA_arb.scala 45:36:@663.4]
  assign _T_123 = 8'h0 - 8'h1; // @[NV_NVDLA_arb.scala 45:36:@665.4]
  assign _T_124 = $unsigned(_T_123); // @[NV_NVDLA_arb.scala 45:36:@666.4]
  assign _T_125 = _T_124[7:0]; // @[NV_NVDLA_arb.scala 45:36:@667.4]
  assign _T_139 = ~ io_gnt_busy; // @[NV_NVDLA_arb.scala 54:14:@677.4]
  assign _T_141 = _T_98 != 5'h0; // @[NV_NVDLA_arb.scala 54:37:@678.4]
  assign _T_142 = _T_139 & _T_141; // @[NV_NVDLA_arb.scala 54:31:@679.4]
  assign _T_146 = {io_gnt_4,io_gnt_3,io_gnt_2,io_gnt_1,io_gnt_0}; // @[NV_NVDLA_arb.scala 55:31:@684.6]
  assign _GEN_0 = _T_142 ? _T_146 : _T_132; // @[NV_NVDLA_arb.scala 54:45:@680.4]
  assign _T_151 = _T_135 == 8'h0; // @[NV_NVDLA_arb.scala 68:18:@690.4]
  assign _T_152 = _T_98 & _T_132; // @[NV_NVDLA_arb.scala 68:35:@691.4]
  assign _T_154 = _T_152 != 5'h0; // @[NV_NVDLA_arb.scala 68:46:@692.4]
  assign _T_155 = ~ _T_154; // @[NV_NVDLA_arb.scala 68:28:@693.4]
  assign _T_156 = _T_151 | _T_155; // @[NV_NVDLA_arb.scala 68:26:@694.4]
  assign _T_344 = 13'h1 << 3'h5; // @[NV_NVDLA_arb.scala 70:52:@856.6]
  assign _T_345 = _T_344[5:1]; // @[NV_NVDLA_arb.scala 70:59:@857.6]
  assign _T_346 = _T_132 == _T_345; // @[NV_NVDLA_arb.scala 70:26:@858.6]
  assign _T_369 = _T_98[0]; // @[NV_NVDLA_arb.scala 72:84:@876.8]
  assign _T_370 = _T_98[1]; // @[NV_NVDLA_arb.scala 72:84:@877.8]
  assign _T_371 = _T_98[2]; // @[NV_NVDLA_arb.scala 72:84:@878.8]
  assign _T_372 = _T_98[3]; // @[NV_NVDLA_arb.scala 72:84:@879.8]
  assign _T_373 = _T_98[4]; // @[NV_NVDLA_arb.scala 72:84:@880.8]
  assign _T_374 = _T_373 ? _T_125 : 8'h0; // @[Mux.scala 61:16:@881.8]
  assign _T_375 = _T_372 ? _T_125 : _T_374; // @[Mux.scala 61:16:@882.8]
  assign _T_376 = _T_371 ? _T_121 : _T_375; // @[Mux.scala 61:16:@883.8]
  assign _T_377 = _T_370 ? _T_117 : _T_376; // @[Mux.scala 61:16:@884.8]
  assign _T_378 = _T_369 ? _T_113 : _T_377; // @[Mux.scala 61:16:@885.8]
  assign _T_307 = 13'h1 << 3'h4; // @[NV_NVDLA_arb.scala 70:52:@824.6]
  assign _T_308 = _T_307[5:1]; // @[NV_NVDLA_arb.scala 70:59:@825.6]
  assign _T_309 = _T_132 == _T_308; // @[NV_NVDLA_arb.scala 70:26:@826.6]
  assign _T_337 = _T_372 ? _T_125 : 8'h0; // @[Mux.scala 61:16:@849.8]
  assign _T_338 = _T_371 ? _T_121 : _T_337; // @[Mux.scala 61:16:@850.8]
  assign _T_339 = _T_370 ? _T_117 : _T_338; // @[Mux.scala 61:16:@851.8]
  assign _T_340 = _T_369 ? _T_113 : _T_339; // @[Mux.scala 61:16:@852.8]
  assign _T_341 = _T_373 ? _T_125 : _T_340; // @[Mux.scala 61:16:@853.8]
  assign _T_270 = 9'h1 << 2'h3; // @[NV_NVDLA_arb.scala 70:52:@792.6]
  assign _T_271 = _T_270[5:1]; // @[NV_NVDLA_arb.scala 70:59:@793.6]
  assign _T_272 = _T_132 == _T_271; // @[NV_NVDLA_arb.scala 70:26:@794.6]
  assign _T_300 = _T_371 ? _T_121 : 8'h0; // @[Mux.scala 61:16:@817.8]
  assign _T_301 = _T_370 ? _T_117 : _T_300; // @[Mux.scala 61:16:@818.8]
  assign _T_302 = _T_369 ? _T_113 : _T_301; // @[Mux.scala 61:16:@819.8]
  assign _T_303 = _T_373 ? _T_125 : _T_302; // @[Mux.scala 61:16:@820.8]
  assign _T_304 = _T_372 ? _T_125 : _T_303; // @[Mux.scala 61:16:@821.8]
  assign _T_233 = 9'h1 << 2'h2; // @[NV_NVDLA_arb.scala 70:52:@760.6]
  assign _T_234 = _T_233[5:1]; // @[NV_NVDLA_arb.scala 70:59:@761.6]
  assign _T_235 = _T_132 == _T_234; // @[NV_NVDLA_arb.scala 70:26:@762.6]
  assign _T_263 = _T_370 ? _T_117 : 8'h0; // @[Mux.scala 61:16:@785.8]
  assign _T_264 = _T_369 ? _T_113 : _T_263; // @[Mux.scala 61:16:@786.8]
  assign _T_265 = _T_373 ? _T_125 : _T_264; // @[Mux.scala 61:16:@787.8]
  assign _T_266 = _T_372 ? _T_125 : _T_265; // @[Mux.scala 61:16:@788.8]
  assign _T_267 = _T_371 ? _T_121 : _T_266; // @[Mux.scala 61:16:@789.8]
  assign _T_196 = 7'h1 << 1'h1; // @[NV_NVDLA_arb.scala 70:52:@728.6]
  assign _T_197 = _T_196[5:1]; // @[NV_NVDLA_arb.scala 70:59:@729.6]
  assign _T_198 = _T_132 == _T_197; // @[NV_NVDLA_arb.scala 70:26:@730.6]
  assign _T_226 = _T_369 ? _T_113 : 8'h0; // @[Mux.scala 61:16:@753.8]
  assign _T_227 = _T_373 ? _T_125 : _T_226; // @[Mux.scala 61:16:@754.8]
  assign _T_228 = _T_372 ? _T_125 : _T_227; // @[Mux.scala 61:16:@755.8]
  assign _T_229 = _T_371 ? _T_121 : _T_228; // @[Mux.scala 61:16:@756.8]
  assign _T_230 = _T_370 ? _T_117 : _T_229; // @[Mux.scala 61:16:@757.8]
  assign _T_159 = 7'h1 << 1'h0; // @[NV_NVDLA_arb.scala 70:52:@696.6]
  assign _T_160 = _T_159[5:1]; // @[NV_NVDLA_arb.scala 70:59:@697.6]
  assign _T_161 = _T_132 == _T_160; // @[NV_NVDLA_arb.scala 70:26:@698.6]
  assign _GEN_3 = _T_161 ? _T_378 : 8'h0; // @[NV_NVDLA_arb.scala 70:66:@699.6]
  assign _GEN_5 = _T_198 ? _T_230 : _GEN_3; // @[NV_NVDLA_arb.scala 70:66:@731.6]
  assign _GEN_7 = _T_235 ? _T_267 : _GEN_5; // @[NV_NVDLA_arb.scala 70:66:@763.6]
  assign _GEN_9 = _T_272 ? _T_304 : _GEN_7; // @[NV_NVDLA_arb.scala 70:66:@795.6]
  assign _GEN_11 = _T_309 ? _T_341 : _GEN_9; // @[NV_NVDLA_arb.scala 70:66:@827.6]
  assign _GEN_13 = _T_346 ? _T_378 : _GEN_11; // @[NV_NVDLA_arb.scala 70:66:@859.6]
  assign _T_380 = _T_135 - 8'h1; // @[NV_NVDLA_arb.scala 78:32:@891.6]
  assign _T_381 = $unsigned(_T_380); // @[NV_NVDLA_arb.scala 78:32:@892.6]
  assign _T_382 = _T_381[7:0]; // @[NV_NVDLA_arb.scala 78:32:@893.6]
  assign _GEN_15 = _T_156 ? _GEN_13 : _T_382; // @[NV_NVDLA_arb.scala 68:52:@695.4]
  assign _GEN_1 = _T_142 ? _GEN_15 : _T_135; // @[NV_NVDLA_arb.scala 54:45:@680.4]
  assign _T_178 = _T_373 ? 5'h10 : 5'h0; // @[Mux.scala 61:16:@710.8]
  assign _T_179 = _T_372 ? 5'h8 : _T_178; // @[Mux.scala 61:16:@711.8]
  assign _T_180 = _T_371 ? 5'h4 : _T_179; // @[Mux.scala 61:16:@712.8]
  assign _T_181 = _T_370 ? 5'h2 : _T_180; // @[Mux.scala 61:16:@713.8]
  assign _T_182 = _T_369 ? 5'h1 : _T_181; // @[Mux.scala 61:16:@714.8]
  assign _GEN_2 = _T_161 ? _T_182 : 5'h0; // @[NV_NVDLA_arb.scala 70:66:@699.6]
  assign _T_215 = _T_369 ? 5'h1 : 5'h0; // @[Mux.scala 61:16:@742.8]
  assign _T_216 = _T_373 ? 5'h10 : _T_215; // @[Mux.scala 61:16:@743.8]
  assign _T_217 = _T_372 ? 5'h8 : _T_216; // @[Mux.scala 61:16:@744.8]
  assign _T_218 = _T_371 ? 5'h4 : _T_217; // @[Mux.scala 61:16:@745.8]
  assign _T_219 = _T_370 ? 5'h2 : _T_218; // @[Mux.scala 61:16:@746.8]
  assign _GEN_4 = _T_198 ? _T_219 : _GEN_2; // @[NV_NVDLA_arb.scala 70:66:@731.6]
  assign _T_252 = _T_370 ? 5'h2 : 5'h0; // @[Mux.scala 61:16:@774.8]
  assign _T_253 = _T_369 ? 5'h1 : _T_252; // @[Mux.scala 61:16:@775.8]
  assign _T_254 = _T_373 ? 5'h10 : _T_253; // @[Mux.scala 61:16:@776.8]
  assign _T_255 = _T_372 ? 5'h8 : _T_254; // @[Mux.scala 61:16:@777.8]
  assign _T_256 = _T_371 ? 5'h4 : _T_255; // @[Mux.scala 61:16:@778.8]
  assign _GEN_6 = _T_235 ? _T_256 : _GEN_4; // @[NV_NVDLA_arb.scala 70:66:@763.6]
  assign _T_289 = _T_371 ? 5'h4 : 5'h0; // @[Mux.scala 61:16:@806.8]
  assign _T_290 = _T_370 ? 5'h2 : _T_289; // @[Mux.scala 61:16:@807.8]
  assign _T_291 = _T_369 ? 5'h1 : _T_290; // @[Mux.scala 61:16:@808.8]
  assign _T_292 = _T_373 ? 5'h10 : _T_291; // @[Mux.scala 61:16:@809.8]
  assign _T_293 = _T_372 ? 5'h8 : _T_292; // @[Mux.scala 61:16:@810.8]
  assign _GEN_8 = _T_272 ? _T_293 : _GEN_6; // @[NV_NVDLA_arb.scala 70:66:@795.6]
  assign _T_326 = _T_372 ? 5'h8 : 5'h0; // @[Mux.scala 61:16:@838.8]
  assign _T_327 = _T_371 ? 5'h4 : _T_326; // @[Mux.scala 61:16:@839.8]
  assign _T_328 = _T_370 ? 5'h2 : _T_327; // @[Mux.scala 61:16:@840.8]
  assign _T_329 = _T_369 ? 5'h1 : _T_328; // @[Mux.scala 61:16:@841.8]
  assign _T_330 = _T_373 ? 5'h10 : _T_329; // @[Mux.scala 61:16:@842.8]
  assign _GEN_10 = _T_309 ? _T_330 : _GEN_8; // @[NV_NVDLA_arb.scala 70:66:@827.6]
  assign _GEN_12 = _T_346 ? _T_182 : _GEN_10; // @[NV_NVDLA_arb.scala 70:66:@859.6]
  assign _GEN_14 = _T_156 ? _GEN_12 : _T_132; // @[NV_NVDLA_arb.scala 68:52:@695.4]
  assign _T_384 = _GEN_14[0]; // @[NV_NVDLA_arb.scala 84:52:@897.4]
  assign _T_387 = _GEN_14[1]; // @[NV_NVDLA_arb.scala 84:52:@901.4]
  assign _T_390 = _GEN_14[2]; // @[NV_NVDLA_arb.scala 84:52:@905.4]
  assign _T_393 = _GEN_14[3]; // @[NV_NVDLA_arb.scala 84:52:@909.4]
  assign _T_396 = _GEN_14[4]; // @[NV_NVDLA_arb.scala 84:52:@913.4]
  assign io_gnt_0 = _T_139 & _T_384; // @[NV_NVDLA_arb.scala 84:23:@899.4]
  assign io_gnt_1 = _T_139 & _T_387; // @[NV_NVDLA_arb.scala 84:23:@903.4]
  assign io_gnt_2 = _T_139 & _T_390; // @[NV_NVDLA_arb.scala 84:23:@907.4]
  assign io_gnt_3 = _T_139 & _T_393; // @[NV_NVDLA_arb.scala 84:23:@911.4]
  assign io_gnt_4 = _T_139 & _T_396; // @[NV_NVDLA_arb.scala 84:23:@915.4]
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
  _T_132 = _RAND_0[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_135 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      _T_132 <= 5'h0;
    end else begin
      if (_T_142) begin
        _T_132 <= _T_146;
      end
    end
    if (reset) begin
      _T_135 <= 8'h0;
    end else begin
      if (_T_142) begin
        if (_T_156) begin
          if (_T_346) begin
            if (_T_369) begin
              _T_135 <= _T_113;
            end else begin
              if (_T_370) begin
                _T_135 <= _T_117;
              end else begin
                if (_T_371) begin
                  _T_135 <= _T_121;
                end else begin
                  if (_T_372) begin
                    _T_135 <= _T_125;
                  end else begin
                    if (_T_373) begin
                      _T_135 <= _T_125;
                    end else begin
                      _T_135 <= 8'h0;
                    end
                  end
                end
              end
            end
          end else begin
            if (_T_309) begin
              if (_T_373) begin
                _T_135 <= _T_125;
              end else begin
                if (_T_369) begin
                  _T_135 <= _T_113;
                end else begin
                  if (_T_370) begin
                    _T_135 <= _T_117;
                  end else begin
                    if (_T_371) begin
                      _T_135 <= _T_121;
                    end else begin
                      if (_T_372) begin
                        _T_135 <= _T_125;
                      end else begin
                        _T_135 <= 8'h0;
                      end
                    end
                  end
                end
              end
            end else begin
              if (_T_272) begin
                if (_T_372) begin
                  _T_135 <= _T_125;
                end else begin
                  if (_T_373) begin
                    _T_135 <= _T_125;
                  end else begin
                    if (_T_369) begin
                      _T_135 <= _T_113;
                    end else begin
                      if (_T_370) begin
                        _T_135 <= _T_117;
                      end else begin
                        if (_T_371) begin
                          _T_135 <= _T_121;
                        end else begin
                          _T_135 <= 8'h0;
                        end
                      end
                    end
                  end
                end
              end else begin
                if (_T_235) begin
                  if (_T_371) begin
                    _T_135 <= _T_121;
                  end else begin
                    if (_T_372) begin
                      _T_135 <= _T_125;
                    end else begin
                      if (_T_373) begin
                        _T_135 <= _T_125;
                      end else begin
                        if (_T_369) begin
                          _T_135 <= _T_113;
                        end else begin
                          if (_T_370) begin
                            _T_135 <= _T_117;
                          end else begin
                            _T_135 <= 8'h0;
                          end
                        end
                      end
                    end
                  end
                end else begin
                  if (_T_198) begin
                    if (_T_370) begin
                      _T_135 <= _T_117;
                    end else begin
                      if (_T_371) begin
                        _T_135 <= _T_121;
                      end else begin
                        if (_T_372) begin
                          _T_135 <= _T_125;
                        end else begin
                          if (_T_373) begin
                            _T_135 <= _T_125;
                          end else begin
                            if (_T_369) begin
                              _T_135 <= _T_113;
                            end else begin
                              _T_135 <= 8'h0;
                            end
                          end
                        end
                      end
                    end
                  end else begin
                    if (_T_161) begin
                      if (_T_369) begin
                        _T_135 <= _T_113;
                      end else begin
                        if (_T_370) begin
                          _T_135 <= _T_117;
                        end else begin
                          if (_T_371) begin
                            _T_135 <= _T_121;
                          end else begin
                            if (_T_372) begin
                              _T_135 <= _T_125;
                            end else begin
                              if (_T_373) begin
                                _T_135 <= _T_125;
                              end else begin
                                _T_135 <= 8'h0;
                              end
                            end
                          end
                        end
                      end
                    end else begin
                      _T_135 <= 8'h0;
                    end
                  end
                end
              end
            end
          end
        end else begin
          _T_135 <= _T_382;
        end
      end
    end
  end
endmodule
module NV_NVDLA_MCIF_WRITE_IG_arb( // @[:@917.2]
  input         clock, // @[:@918.4]
  input         reset, // @[:@919.4]
  input         io_nvdla_core_clk, // @[:@920.4]
  input  [31:0] io_pwrbus_ram_pd, // @[:@920.4]
  output        io_bpt2arb_cmd_pd_0_ready, // @[:@920.4]
  input         io_bpt2arb_cmd_pd_0_valid, // @[:@920.4]
  input  [44:0] io_bpt2arb_cmd_pd_0_bits, // @[:@920.4]
  output        io_bpt2arb_cmd_pd_1_ready, // @[:@920.4]
  input         io_bpt2arb_cmd_pd_1_valid, // @[:@920.4]
  input  [44:0] io_bpt2arb_cmd_pd_1_bits, // @[:@920.4]
  output        io_bpt2arb_cmd_pd_2_ready, // @[:@920.4]
  input         io_bpt2arb_cmd_pd_2_valid, // @[:@920.4]
  input  [44:0] io_bpt2arb_cmd_pd_2_bits, // @[:@920.4]
  output        io_bpt2arb_dat_pd_0_ready, // @[:@920.4]
  input         io_bpt2arb_dat_pd_0_valid, // @[:@920.4]
  input  [64:0] io_bpt2arb_dat_pd_0_bits, // @[:@920.4]
  output        io_bpt2arb_dat_pd_1_ready, // @[:@920.4]
  input         io_bpt2arb_dat_pd_1_valid, // @[:@920.4]
  input  [64:0] io_bpt2arb_dat_pd_1_bits, // @[:@920.4]
  output        io_bpt2arb_dat_pd_2_ready, // @[:@920.4]
  input         io_bpt2arb_dat_pd_2_valid, // @[:@920.4]
  input  [64:0] io_bpt2arb_dat_pd_2_bits, // @[:@920.4]
  input         io_arb2spt_cmd_pd_ready, // @[:@920.4]
  output        io_arb2spt_cmd_pd_valid, // @[:@920.4]
  output [44:0] io_arb2spt_cmd_pd_bits, // @[:@920.4]
  input         io_arb2spt_dat_pd_ready, // @[:@920.4]
  output        io_arb2spt_dat_pd_valid, // @[:@920.4]
  output [64:0] io_arb2spt_dat_pd_bits, // @[:@920.4]
  input  [7:0]  io_reg2dp_wr_weight_0, // @[:@920.4]
  input  [7:0]  io_reg2dp_wr_weight_1, // @[:@920.4]
  input  [7:0]  io_reg2dp_wr_weight_2 // @[:@920.4]
);
  wire  NV_NVDLA_BC_OS_pipe_reset; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@929.4]
  wire  NV_NVDLA_BC_OS_pipe_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@929.4]
  wire  NV_NVDLA_BC_OS_pipe_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@929.4]
  wire  NV_NVDLA_BC_OS_pipe_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@929.4]
  wire [44:0] NV_NVDLA_BC_OS_pipe_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@929.4]
  wire  NV_NVDLA_BC_OS_pipe_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@929.4]
  wire  NV_NVDLA_BC_OS_pipe_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@929.4]
  wire [44:0] NV_NVDLA_BC_OS_pipe_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@929.4]
  wire  NV_NVDLA_BC_OS_pipe_1_reset; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@932.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@932.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@932.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@932.4]
  wire [44:0] NV_NVDLA_BC_OS_pipe_1_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@932.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@932.4]
  wire  NV_NVDLA_BC_OS_pipe_1_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@932.4]
  wire [44:0] NV_NVDLA_BC_OS_pipe_1_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@932.4]
  wire  NV_NVDLA_BC_OS_pipe_2_reset; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@935.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@935.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_vi; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@935.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@935.4]
  wire [44:0] NV_NVDLA_BC_OS_pipe_2_io_di; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@935.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_vo; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@935.4]
  wire  NV_NVDLA_BC_OS_pipe_2_io_ri; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@935.4]
  wire [44:0] NV_NVDLA_BC_OS_pipe_2_io_dout; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@935.4]
  wire  NV_NVDLA_fifo_new_clock; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
  wire  NV_NVDLA_fifo_new_reset; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
  wire  NV_NVDLA_fifo_new_io_wr_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
  wire  NV_NVDLA_fifo_new_io_wr_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
  wire [64:0] NV_NVDLA_fifo_new_io_wr_pd; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
  wire [2:0] NV_NVDLA_fifo_new_io_wr_count; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
  wire  NV_NVDLA_fifo_new_io_rd_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
  wire  NV_NVDLA_fifo_new_io_rd_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
  wire [64:0] NV_NVDLA_fifo_new_io_rd_pd; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
  wire  NV_NVDLA_fifo_new_1_clock; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
  wire  NV_NVDLA_fifo_new_1_reset; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
  wire  NV_NVDLA_fifo_new_1_io_wr_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
  wire  NV_NVDLA_fifo_new_1_io_wr_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
  wire [64:0] NV_NVDLA_fifo_new_1_io_wr_pd; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
  wire [2:0] NV_NVDLA_fifo_new_1_io_wr_count; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
  wire  NV_NVDLA_fifo_new_1_io_rd_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
  wire  NV_NVDLA_fifo_new_1_io_rd_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
  wire [64:0] NV_NVDLA_fifo_new_1_io_rd_pd; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
  wire  NV_NVDLA_fifo_new_2_clock; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
  wire  NV_NVDLA_fifo_new_2_reset; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
  wire  NV_NVDLA_fifo_new_2_io_wr_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
  wire  NV_NVDLA_fifo_new_2_io_wr_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
  wire [64:0] NV_NVDLA_fifo_new_2_io_wr_pd; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
  wire [2:0] NV_NVDLA_fifo_new_2_io_wr_count; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
  wire  NV_NVDLA_fifo_new_2_io_rd_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
  wire  NV_NVDLA_fifo_new_2_io_rd_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
  wire [64:0] NV_NVDLA_fifo_new_2_io_rd_pd; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
  wire  u_write_ig_arb_reset; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_clk; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_req_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_req_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_req_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire [7:0] u_write_ig_arb_io_wt_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire [7:0] u_write_ig_arb_io_wt_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire [7:0] u_write_ig_arb_io_wt_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_gnt_busy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_gnt_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_gnt_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_gnt_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_gnt_3; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  u_write_ig_arb_io_gnt_4; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
  wire  _T_223; // @[Bitwise.scala 72:15:@956.4]
  wire [2:0] _T_226; // @[Bitwise.scala 72:12:@957.4]
  wire [2:0] _T_227; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:72:@958.4]
  wire [2:0] src_cmd_size_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:53:@959.4]
  reg [2:0] gnt_count; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 131:28:@1098.4]
  reg [31:0] _RAND_0;
  reg  sticky; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 96:25:@1038.4]
  reg [31:0] _RAND_1;
  reg  stick_gnts_4; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 98:29:@1046.4]
  reg [31:0] _RAND_2;
  wire  arb_gnts_4; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 93:21:@1037.4]
  wire  all_gnts_4; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  reg  stick_gnts_3; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 98:29:@1046.4]
  reg [31:0] _RAND_3;
  wire  arb_gnts_3; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 93:21:@1034.4]
  wire  all_gnts_3; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  reg  stick_gnts_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 98:29:@1046.4]
  reg [31:0] _RAND_4;
  wire  arb_gnts_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 88:21:@1031.4]
  wire  all_gnts_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  reg  stick_gnts_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 98:29:@1046.4]
  reg [31:0] _RAND_5;
  wire  arb_gnts_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 88:21:@1028.4]
  wire  all_gnts_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  reg  stick_gnts_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 98:29:@1046.4]
  reg [31:0] _RAND_6;
  wire  arb_gnts_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 88:21:@1025.4]
  wire  all_gnts_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  wire [44:0] _GEN_10; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1117.4]
  wire [44:0] _GEN_12; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1121.4]
  wire [44:0] _GEN_14; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1125.4]
  wire [44:0] _GEN_16; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 154:26:@1129.4]
  wire [44:0] arb_cmd_pd; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 154:26:@1133.4]
  wire [2:0] arb_cmd_size; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 160:31:@1137.4]
  wire  _T_382; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 142:32:@1111.4]
  wire  is_last_beat; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 51:28:@925.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 142:18:@1112.4]
  wire  src_dat_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 52:27:@926.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 166:17:@1143.4]
  wire  _T_229; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 74:41:@961.4]
  wire  src_dat_gnts_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 123:40:@1084.4]
  wire  _T_232; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 77:78:@967.4]
  wire  _T_234; // @[Bitwise.scala 72:15:@979.4]
  wire [2:0] _T_237; // @[Bitwise.scala 72:12:@980.4]
  wire [2:0] _T_238; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:72:@981.4]
  wire [2:0] src_cmd_size_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:53:@982.4]
  wire  src_dat_gnts_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 123:40:@1086.4]
  wire  _T_243; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 77:78:@990.4]
  wire  _T_245; // @[Bitwise.scala 72:15:@1002.4]
  wire [2:0] _T_248; // @[Bitwise.scala 72:12:@1003.4]
  wire [2:0] _T_249; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:72:@1004.4]
  wire [2:0] src_cmd_size_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:53:@1005.4]
  wire  src_dat_gnts_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 123:40:@1088.4]
  wire  _T_254; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 77:78:@1013.4]
  wire [4:0] _T_326; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 99:32:@1050.4]
  wire  any_arb_gnt; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 99:39:@1051.4]
  wire  _T_392; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 168:46:@1144.4]
  wire  spt_is_busy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 168:20:@1145.4]
  wire  _GEN_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  wire  _GEN_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  wire  _GEN_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  wire  _GEN_3; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  wire  _GEN_4; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  wire [4:0] _T_371; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 129:33:@1095.4]
  wire  src_dat_vld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 129:40:@1096.4]
  wire  _T_356; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 111:26:@1069.6]
  wire  _T_357; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 111:40:@1070.6]
  wire  _GEN_5; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 111:55:@1071.6]
  wire  _GEN_6; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 118:56:@1081.6]
  wire  _GEN_7; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 110:22:@1068.4]
  wire [3:0] _T_379; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 137:36:@1105.8]
  wire [2:0] _T_380; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 137:36:@1106.8]
  wire [2:0] _GEN_8; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 133:27:@1101.6]
  wire [2:0] _GEN_9; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 132:36:@1100.4]
  wire [64:0] _GEN_11; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1117.4]
  wire [64:0] _GEN_13; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1121.4]
  wire [64:0] _GEN_15; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1125.4]
  wire [64:0] _GEN_17; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 154:26:@1129.4]
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe ( // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@929.4]
    .reset(NV_NVDLA_BC_OS_pipe_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_io_dout)
  );
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe_1 ( // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@932.4]
    .reset(NV_NVDLA_BC_OS_pipe_1_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_1_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_1_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_1_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_1_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_1_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_1_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_1_io_dout)
  );
  NV_NVDLA_BC_OS_pipe NV_NVDLA_BC_OS_pipe_2 ( // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 58:50:@935.4]
    .reset(NV_NVDLA_BC_OS_pipe_2_reset),
    .io_clk(NV_NVDLA_BC_OS_pipe_2_io_clk),
    .io_vi(NV_NVDLA_BC_OS_pipe_2_io_vi),
    .io_ro(NV_NVDLA_BC_OS_pipe_2_io_ro),
    .io_di(NV_NVDLA_BC_OS_pipe_2_io_di),
    .io_vo(NV_NVDLA_BC_OS_pipe_2_io_vo),
    .io_ri(NV_NVDLA_BC_OS_pipe_2_io_ri),
    .io_dout(NV_NVDLA_BC_OS_pipe_2_io_dout)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new ( // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@938.4]
    .clock(NV_NVDLA_fifo_new_clock),
    .reset(NV_NVDLA_fifo_new_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_io_wr_pd),
    .io_wr_count(NV_NVDLA_fifo_new_io_wr_count),
    .io_rd_pvld(NV_NVDLA_fifo_new_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_io_rd_pd)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new_1 ( // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@941.4]
    .clock(NV_NVDLA_fifo_new_1_clock),
    .reset(NV_NVDLA_fifo_new_1_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_1_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_1_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_1_io_wr_pd),
    .io_wr_count(NV_NVDLA_fifo_new_1_io_wr_count),
    .io_rd_pvld(NV_NVDLA_fifo_new_1_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_1_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_1_io_rd_pd)
  );
  NV_NVDLA_fifo_new NV_NVDLA_fifo_new_2 ( // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 59:51:@944.4]
    .clock(NV_NVDLA_fifo_new_2_clock),
    .reset(NV_NVDLA_fifo_new_2_reset),
    .io_wr_pvld(NV_NVDLA_fifo_new_2_io_wr_pvld),
    .io_wr_prdy(NV_NVDLA_fifo_new_2_io_wr_prdy),
    .io_wr_pd(NV_NVDLA_fifo_new_2_io_wr_pd),
    .io_wr_count(NV_NVDLA_fifo_new_2_io_wr_count),
    .io_rd_pvld(NV_NVDLA_fifo_new_2_io_rd_pvld),
    .io_rd_prdy(NV_NVDLA_fifo_new_2_io_rd_prdy),
    .io_rd_pd(NV_NVDLA_fifo_new_2_io_rd_pd)
  );
  NV_NVDLA_arb u_write_ig_arb ( // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 82:32:@1018.4]
    .reset(u_write_ig_arb_reset),
    .io_clk(u_write_ig_arb_io_clk),
    .io_req_0(u_write_ig_arb_io_req_0),
    .io_req_1(u_write_ig_arb_io_req_1),
    .io_req_2(u_write_ig_arb_io_req_2),
    .io_wt_0(u_write_ig_arb_io_wt_0),
    .io_wt_1(u_write_ig_arb_io_wt_1),
    .io_wt_2(u_write_ig_arb_io_wt_2),
    .io_gnt_busy(u_write_ig_arb_io_gnt_busy),
    .io_gnt_0(u_write_ig_arb_io_gnt_0),
    .io_gnt_1(u_write_ig_arb_io_gnt_1),
    .io_gnt_2(u_write_ig_arb_io_gnt_2),
    .io_gnt_3(u_write_ig_arb_io_gnt_3),
    .io_gnt_4(u_write_ig_arb_io_gnt_4)
  );
  assign _T_223 = NV_NVDLA_BC_OS_pipe_io_vo; // @[Bitwise.scala 72:15:@956.4]
  assign _T_226 = _T_223 ? 3'h7 : 3'h0; // @[Bitwise.scala 72:12:@957.4]
  assign _T_227 = NV_NVDLA_BC_OS_pipe_io_dout[39:37]; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:72:@958.4]
  assign src_cmd_size_0 = _T_226 & _T_227; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:53:@959.4]
  assign arb_gnts_4 = u_write_ig_arb_io_gnt_4; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 93:21:@1037.4]
  assign all_gnts_4 = sticky ? stick_gnts_4 : arb_gnts_4; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  assign arb_gnts_3 = u_write_ig_arb_io_gnt_3; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 93:21:@1034.4]
  assign all_gnts_3 = sticky ? stick_gnts_3 : arb_gnts_3; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  assign arb_gnts_2 = u_write_ig_arb_io_gnt_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 88:21:@1031.4]
  assign all_gnts_2 = sticky ? stick_gnts_2 : arb_gnts_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  assign arb_gnts_1 = u_write_ig_arb_io_gnt_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 88:21:@1028.4]
  assign all_gnts_1 = sticky ? stick_gnts_1 : arb_gnts_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  assign arb_gnts_0 = u_write_ig_arb_io_gnt_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 80:24:@1016.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 88:21:@1025.4]
  assign all_gnts_0 = sticky ? stick_gnts_0 : arb_gnts_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 100:20:@1052.4]
  assign _GEN_10 = all_gnts_0 ? NV_NVDLA_BC_OS_pipe_io_dout : 45'h0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1117.4]
  assign _GEN_12 = all_gnts_1 ? NV_NVDLA_BC_OS_pipe_1_io_dout : _GEN_10; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1121.4]
  assign _GEN_14 = all_gnts_2 ? NV_NVDLA_BC_OS_pipe_2_io_dout : _GEN_12; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1125.4]
  assign _GEN_16 = all_gnts_3 ? 45'h0 : _GEN_14; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 154:26:@1129.4]
  assign arb_cmd_pd = all_gnts_4 ? 45'h0 : _GEN_16; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 154:26:@1133.4]
  assign arb_cmd_size = arb_cmd_pd[39:37]; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 160:31:@1137.4]
  assign _T_382 = gnt_count == arb_cmd_size; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 142:32:@1111.4]
  assign is_last_beat = _T_382; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 51:28:@925.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 142:18:@1112.4]
  assign src_dat_rdy = io_arb2spt_dat_pd_ready; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 52:27:@926.4 NV_NVDLA_MCIF_WRITE_IG_arb.scala 166:17:@1143.4]
  assign _T_229 = is_last_beat & src_dat_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 74:41:@961.4]
  assign src_dat_gnts_0 = all_gnts_0 & NV_NVDLA_fifo_new_io_rd_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 123:40:@1084.4]
  assign _T_232 = NV_NVDLA_fifo_new_io_wr_count > src_cmd_size_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 77:78:@967.4]
  assign _T_234 = NV_NVDLA_BC_OS_pipe_1_io_vo; // @[Bitwise.scala 72:15:@979.4]
  assign _T_237 = _T_234 ? 3'h7 : 3'h0; // @[Bitwise.scala 72:12:@980.4]
  assign _T_238 = NV_NVDLA_BC_OS_pipe_1_io_dout[39:37]; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:72:@981.4]
  assign src_cmd_size_1 = _T_237 & _T_238; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:53:@982.4]
  assign src_dat_gnts_1 = all_gnts_1 & NV_NVDLA_fifo_new_1_io_rd_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 123:40:@1086.4]
  assign _T_243 = NV_NVDLA_fifo_new_1_io_wr_count > src_cmd_size_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 77:78:@990.4]
  assign _T_245 = NV_NVDLA_BC_OS_pipe_2_io_vo; // @[Bitwise.scala 72:15:@1002.4]
  assign _T_248 = _T_245 ? 3'h7 : 3'h0; // @[Bitwise.scala 72:12:@1003.4]
  assign _T_249 = NV_NVDLA_BC_OS_pipe_2_io_dout[39:37]; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:72:@1004.4]
  assign src_cmd_size_2 = _T_248 & _T_249; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 73:53:@1005.4]
  assign src_dat_gnts_2 = all_gnts_2 & NV_NVDLA_fifo_new_2_io_rd_pvld; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 123:40:@1088.4]
  assign _T_254 = NV_NVDLA_fifo_new_2_io_wr_count > src_cmd_size_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 77:78:@1013.4]
  assign _T_326 = {arb_gnts_4,arb_gnts_3,arb_gnts_2,arb_gnts_1,arb_gnts_0}; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 99:32:@1050.4]
  assign any_arb_gnt = _T_326 != 5'h0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 99:39:@1051.4]
  assign _T_392 = io_arb2spt_cmd_pd_ready & io_arb2spt_dat_pd_ready; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 168:46:@1144.4]
  assign spt_is_busy = ~ _T_392; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 168:20:@1145.4]
  assign _GEN_0 = any_arb_gnt ? arb_gnts_0 : stick_gnts_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  assign _GEN_1 = any_arb_gnt ? arb_gnts_1 : stick_gnts_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  assign _GEN_2 = any_arb_gnt ? arb_gnts_2 : stick_gnts_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  assign _GEN_3 = any_arb_gnt ? arb_gnts_3 : stick_gnts_3; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  assign _GEN_4 = any_arb_gnt ? arb_gnts_4 : stick_gnts_4; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 104:22:@1060.4]
  assign _T_371 = {2'h0,src_dat_gnts_2,src_dat_gnts_1,src_dat_gnts_0}; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 129:33:@1095.4]
  assign src_dat_vld = _T_371 != 5'h0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 129:40:@1096.4]
  assign _T_356 = src_dat_vld & src_dat_rdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 111:26:@1069.6]
  assign _T_357 = _T_356 & is_last_beat; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 111:40:@1070.6]
  assign _GEN_5 = _T_357 ? 1'h0 : 1'h1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 111:55:@1071.6]
  assign _GEN_6 = _T_357 ? 1'h0 : sticky; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 118:56:@1081.6]
  assign _GEN_7 = any_arb_gnt ? _GEN_5 : _GEN_6; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 110:22:@1068.4]
  assign _T_379 = gnt_count + 3'h1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 137:36:@1105.8]
  assign _T_380 = gnt_count + 3'h1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 137:36:@1106.8]
  assign _GEN_8 = is_last_beat ? 3'h0 : _T_380; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 133:27:@1101.6]
  assign _GEN_9 = _T_356 ? _GEN_8 : gnt_count; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 132:36:@1100.4]
  assign _GEN_11 = all_gnts_0 ? NV_NVDLA_fifo_new_io_rd_pd : 65'h0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1117.4]
  assign _GEN_13 = all_gnts_1 ? NV_NVDLA_fifo_new_1_io_rd_pd : _GEN_11; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1121.4]
  assign _GEN_15 = all_gnts_2 ? NV_NVDLA_fifo_new_2_io_rd_pd : _GEN_13; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 148:26:@1125.4]
  assign _GEN_17 = all_gnts_3 ? 65'h0 : _GEN_15; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 154:26:@1129.4]
  assign io_bpt2arb_cmd_pd_0_ready = NV_NVDLA_BC_OS_pipe_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 64:36:@949.4]
  assign io_bpt2arb_cmd_pd_1_ready = NV_NVDLA_BC_OS_pipe_1_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 64:36:@972.4]
  assign io_bpt2arb_cmd_pd_2_ready = NV_NVDLA_BC_OS_pipe_2_io_ro; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 64:36:@995.4]
  assign io_bpt2arb_dat_pd_0_ready = NV_NVDLA_fifo_new_io_wr_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 70:36:@954.4]
  assign io_bpt2arb_dat_pd_1_ready = NV_NVDLA_fifo_new_1_io_wr_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 70:36:@977.4]
  assign io_bpt2arb_dat_pd_2_ready = NV_NVDLA_fifo_new_2_io_wr_prdy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 70:36:@1000.4]
  assign io_arb2spt_cmd_pd_valid = _T_326 != 5'h0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 164:29:@1141.4]
  assign io_arb2spt_cmd_pd_bits = all_gnts_4 ? 45'h0 : _GEN_16; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 161:28:@1139.4]
  assign io_arb2spt_dat_pd_valid = _T_371 != 5'h0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 165:29:@1142.4]
  assign io_arb2spt_dat_pd_bits = all_gnts_4 ? 65'h0 : _GEN_17; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 162:28:@1140.4]
  assign NV_NVDLA_BC_OS_pipe_reset = reset; // @[:@931.4]
  assign NV_NVDLA_BC_OS_pipe_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 61:26:@947.4]
  assign NV_NVDLA_BC_OS_pipe_io_vi = io_bpt2arb_cmd_pd_0_valid; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 63:25:@948.4]
  assign NV_NVDLA_BC_OS_pipe_io_di = io_bpt2arb_cmd_pd_0_bits; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 65:25:@950.4]
  assign NV_NVDLA_BC_OS_pipe_io_ri = _T_229 & src_dat_gnts_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 74:25:@963.4]
  assign NV_NVDLA_BC_OS_pipe_1_reset = reset; // @[:@934.4]
  assign NV_NVDLA_BC_OS_pipe_1_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 61:26:@970.4]
  assign NV_NVDLA_BC_OS_pipe_1_io_vi = io_bpt2arb_cmd_pd_1_valid; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 63:25:@971.4]
  assign NV_NVDLA_BC_OS_pipe_1_io_di = io_bpt2arb_cmd_pd_1_bits; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 65:25:@973.4]
  assign NV_NVDLA_BC_OS_pipe_1_io_ri = _T_229 & src_dat_gnts_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 74:25:@986.4]
  assign NV_NVDLA_BC_OS_pipe_2_reset = reset; // @[:@937.4]
  assign NV_NVDLA_BC_OS_pipe_2_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 61:26:@993.4]
  assign NV_NVDLA_BC_OS_pipe_2_io_vi = io_bpt2arb_cmd_pd_2_valid; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 63:25:@994.4]
  assign NV_NVDLA_BC_OS_pipe_2_io_di = io_bpt2arb_cmd_pd_2_bits; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 65:25:@996.4]
  assign NV_NVDLA_BC_OS_pipe_2_io_ri = _T_229 & src_dat_gnts_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 74:25:@1009.4]
  assign NV_NVDLA_fifo_new_clock = io_nvdla_core_clk; // @[:@939.4]
  assign NV_NVDLA_fifo_new_reset = reset; // @[:@940.4]
  assign NV_NVDLA_fifo_new_io_wr_pvld = io_bpt2arb_dat_pd_0_valid; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 69:31:@953.4]
  assign NV_NVDLA_fifo_new_io_wr_pd = io_bpt2arb_dat_pd_0_bits; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 71:29:@955.4]
  assign NV_NVDLA_fifo_new_io_rd_prdy = src_dat_rdy & all_gnts_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 75:31:@965.4]
  assign NV_NVDLA_fifo_new_1_clock = io_nvdla_core_clk; // @[:@942.4]
  assign NV_NVDLA_fifo_new_1_reset = reset; // @[:@943.4]
  assign NV_NVDLA_fifo_new_1_io_wr_pvld = io_bpt2arb_dat_pd_1_valid; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 69:31:@976.4]
  assign NV_NVDLA_fifo_new_1_io_wr_pd = io_bpt2arb_dat_pd_1_bits; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 71:29:@978.4]
  assign NV_NVDLA_fifo_new_1_io_rd_prdy = src_dat_rdy & all_gnts_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 75:31:@988.4]
  assign NV_NVDLA_fifo_new_2_clock = io_nvdla_core_clk; // @[:@945.4]
  assign NV_NVDLA_fifo_new_2_reset = reset; // @[:@946.4]
  assign NV_NVDLA_fifo_new_2_io_wr_pvld = io_bpt2arb_dat_pd_2_valid; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 69:31:@999.4]
  assign NV_NVDLA_fifo_new_2_io_wr_pd = io_bpt2arb_dat_pd_2_bits; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 71:29:@1001.4]
  assign NV_NVDLA_fifo_new_2_io_rd_prdy = src_dat_rdy & all_gnts_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 75:31:@1011.4]
  assign u_write_ig_arb_reset = reset; // @[:@1020.4]
  assign u_write_ig_arb_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 83:27:@1021.4]
  assign u_write_ig_arb_io_req_0 = NV_NVDLA_BC_OS_pipe_io_vo & _T_232; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 86:34:@1023.4]
  assign u_write_ig_arb_io_req_1 = NV_NVDLA_BC_OS_pipe_1_io_vo & _T_243; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 86:34:@1026.4]
  assign u_write_ig_arb_io_req_2 = NV_NVDLA_BC_OS_pipe_2_io_vo & _T_254; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 86:34:@1029.4]
  assign u_write_ig_arb_io_wt_0 = io_reg2dp_wr_weight_0; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 87:33:@1024.4]
  assign u_write_ig_arb_io_wt_1 = io_reg2dp_wr_weight_1; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 87:33:@1027.4]
  assign u_write_ig_arb_io_wt_2 = io_reg2dp_wr_weight_2; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 87:33:@1030.4]
  assign u_write_ig_arb_io_gnt_busy = sticky | spt_is_busy; // @[NV_NVDLA_MCIF_WRITE_IG_arb.scala 84:36:@1022.4]
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
  gnt_count = _RAND_0[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  sticky = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  stick_gnts_4 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  stick_gnts_3 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  stick_gnts_2 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  stick_gnts_1 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  stick_gnts_0 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      gnt_count <= 3'h0;
    end else begin
      if (_T_356) begin
        if (is_last_beat) begin
          gnt_count <= 3'h0;
        end else begin
          gnt_count <= _T_380;
        end
      end
    end
    if (reset) begin
      sticky <= 1'h0;
    end else begin
      if (any_arb_gnt) begin
        if (_T_357) begin
          sticky <= 1'h0;
        end else begin
          sticky <= 1'h1;
        end
      end else begin
        if (_T_357) begin
          sticky <= 1'h0;
        end
      end
    end
    if (reset) begin
      stick_gnts_4 <= 1'h0;
    end else begin
      if (any_arb_gnt) begin
        stick_gnts_4 <= arb_gnts_4;
      end
    end
    if (reset) begin
      stick_gnts_3 <= 1'h0;
    end else begin
      if (any_arb_gnt) begin
        stick_gnts_3 <= arb_gnts_3;
      end
    end
    if (reset) begin
      stick_gnts_2 <= 1'h0;
    end else begin
      if (any_arb_gnt) begin
        stick_gnts_2 <= arb_gnts_2;
      end
    end
    if (reset) begin
      stick_gnts_1 <= 1'h0;
    end else begin
      if (any_arb_gnt) begin
        stick_gnts_1 <= arb_gnts_1;
      end
    end
    if (reset) begin
      stick_gnts_0 <= 1'h0;
    end else begin
      if (any_arb_gnt) begin
        stick_gnts_0 <= arb_gnts_0;
      end
    end
  end
endmodule
