module NV_NVDLA_CSB_LOGIC( // @[:@3.2]
  input         reset, // @[:@5.4]
  input         io_clk, // @[:@6.4]
  input         io_csb2dp_req_valid, // @[:@6.4]
  input  [62:0] io_csb2dp_req_bits, // @[:@6.4]
  output        io_csb2dp_resp_valid, // @[:@6.4]
  output [33:0] io_csb2dp_resp_bits, // @[:@6.4]
  input  [31:0] io_reg_rd_data, // @[:@6.4]
  output [11:0] io_reg_offset, // @[:@6.4]
  output [31:0] io_reg_wr_data, // @[:@6.4]
  output        io_reg_wr_en // @[:@6.4]
);
  reg  _T_43; // @[NV_NVDLA_CSB_LOGIC.scala 42:27:@8.4]
  reg [31:0] _RAND_0;
  reg [62:0] _T_46; // @[NV_NVDLA_CSB_LOGIC.scala 43:25:@9.4]
  reg [63:0] _RAND_1;
  wire [62:0] _GEN_0; // @[NV_NVDLA_CSB_LOGIC.scala 46:30:@11.4]
  wire [21:0] _T_47; // @[NV_NVDLA_CSB_LOGIC.scala 51:26:@14.4]
  wire  _T_49; // @[NV_NVDLA_CSB_LOGIC.scala 53:27:@16.4]
  wire  _T_50; // @[NV_NVDLA_CSB_LOGIC.scala 54:29:@17.4]
  wire  _T_55; // @[NV_NVDLA_CSB_LOGIC.scala 97:37:@22.4]
  wire  _T_56; // @[NV_NVDLA_CSB_LOGIC.scala 97:35:@23.4]
  wire [31:0] _T_60; // @[Bitwise.scala 72:12:@25.4]
  wire [31:0] _T_61; // @[NV_NVDLA_CSB_LOGIC.scala 98:48:@26.4]
  wire  _T_62; // @[NV_NVDLA_CSB_LOGIC.scala 99:35:@27.4]
  wire  _T_63; // @[NV_NVDLA_CSB_LOGIC.scala 99:47:@28.4]
  wire [32:0] _T_65; // @[Cat.scala 30:58:@29.4]
  wire  _T_69; // @[NV_NVDLA_CSB_LOGIC.scala 108:34:@31.4]
  wire [32:0] _T_73; // @[Bitwise.scala 72:12:@33.4]
  wire [32:0] _T_74; // @[NV_NVDLA_CSB_LOGIC.scala 109:59:@34.4]
  wire [33:0] _T_81; // @[Cat.scala 30:58:@39.4]
  reg [33:0] _T_84; // @[NV_NVDLA_CSB_LOGIC.scala 111:41:@40.4]
  reg [63:0] _RAND_2;
  reg  _T_87; // @[NV_NVDLA_CSB_LOGIC.scala 112:44:@41.4]
  reg [31:0] _RAND_3;
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_LOGIC.scala 115:22:@43.4]
  wire [23:0] _T_89; // @[Cat.scala 30:58:@46.4]
  assign _GEN_0 = io_csb2dp_req_valid ? io_csb2dp_req_bits : _T_46; // @[NV_NVDLA_CSB_LOGIC.scala 46:30:@11.4]
  assign _T_47 = _T_46[21:0]; // @[NV_NVDLA_CSB_LOGIC.scala 51:26:@14.4]
  assign _T_49 = _T_46[54]; // @[NV_NVDLA_CSB_LOGIC.scala 53:27:@16.4]
  assign _T_50 = _T_46[55]; // @[NV_NVDLA_CSB_LOGIC.scala 54:29:@17.4]
  assign _T_55 = ~ _T_49; // @[NV_NVDLA_CSB_LOGIC.scala 97:37:@22.4]
  assign _T_56 = _T_43 & _T_55; // @[NV_NVDLA_CSB_LOGIC.scala 97:35:@23.4]
  assign _T_60 = _T_56 ? 32'hffffffff : 32'h0; // @[Bitwise.scala 72:12:@25.4]
  assign _T_61 = _T_60 & io_reg_rd_data; // @[NV_NVDLA_CSB_LOGIC.scala 98:48:@26.4]
  assign _T_62 = _T_43 & _T_49; // @[NV_NVDLA_CSB_LOGIC.scala 99:35:@27.4]
  assign _T_63 = _T_62 & _T_50; // @[NV_NVDLA_CSB_LOGIC.scala 99:47:@28.4]
  assign _T_65 = {1'h0,_T_61}; // @[Cat.scala 30:58:@29.4]
  assign _T_69 = _T_63 | _T_56; // @[NV_NVDLA_CSB_LOGIC.scala 108:34:@31.4]
  assign _T_73 = _T_56 ? 33'h1ffffffff : 33'h0; // @[Bitwise.scala 72:12:@33.4]
  assign _T_74 = _T_73 & _T_65; // @[NV_NVDLA_CSB_LOGIC.scala 109:59:@34.4]
  assign _T_81 = {_T_63,_T_74}; // @[Cat.scala 30:58:@39.4]
  assign _GEN_1 = _T_69 ? _T_81 : _T_84; // @[NV_NVDLA_CSB_LOGIC.scala 115:22:@43.4]
  assign _T_89 = {_T_47,2'h0}; // @[Cat.scala 30:58:@46.4]
  assign io_csb2dp_resp_valid = _T_87; // @[NV_NVDLA_CSB_LOGIC.scala 127:30:@54.4]
  assign io_csb2dp_resp_bits = _T_84; // @[NV_NVDLA_CSB_LOGIC.scala 126:29:@53.4]
  assign io_reg_offset = _T_89[11:0]; // @[NV_NVDLA_CSB_LOGIC.scala 119:23:@47.4]
  assign io_reg_wr_data = _T_46[53:22]; // @[NV_NVDLA_CSB_LOGIC.scala 120:24:@48.4]
  assign io_reg_wr_en = _T_43 & _T_49; // @[NV_NVDLA_CSB_LOGIC.scala 121:22:@50.4]
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
  _T_43 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {2{`RANDOM}};
  _T_46 = _RAND_1[62:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_84 = _RAND_2[33:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_87 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      _T_43 <= 1'h0;
    end else begin
      _T_43 <= io_csb2dp_req_valid;
    end
    if (reset) begin
      _T_46 <= 63'h0;
    end else begin
      if (io_csb2dp_req_valid) begin
        _T_46 <= io_csb2dp_req_bits;
      end
    end
    if (reset) begin
      _T_84 <= 34'h0;
    end else begin
      if (_T_69) begin
        _T_84 <= _T_81;
      end
    end
    if (reset) begin
      _T_87 <= 1'h0;
    end else begin
      _T_87 <= _T_69;
    end
  end
endmodule
module NV_NVDLA_MCIF_CSB_reg( // @[:@56.2]
  input         reset, // @[:@58.4]
  input         io_nvdla_core_clk, // @[:@59.4]
  output [31:0] io_reg_rd_data, // @[:@59.4]
  input  [11:0] io_reg_offset, // @[:@59.4]
  input  [31:0] io_reg_wr_data, // @[:@59.4]
  input         io_reg_wr_en, // @[:@59.4]
  output [7:0]  io_field_rd_os_cnt, // @[:@59.4]
  output [7:0]  io_field_rd_weight_rsv_0, // @[:@59.4]
  output [7:0]  io_field_rd_weight_rsv_1, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_0, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_1, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_2, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_3, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_4, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_5, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_6, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_7, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_8, // @[:@59.4]
  output [7:0]  io_field_rd_weight_client_9, // @[:@59.4]
  output [7:0]  io_field_wr_os_cnt, // @[:@59.4]
  output [7:0]  io_field_wr_weight_rsv_0, // @[:@59.4]
  output [7:0]  io_field_wr_weight_rsv_1, // @[:@59.4]
  output [7:0]  io_field_wr_weight_rsv_2, // @[:@59.4]
  output [7:0]  io_field_wr_weight_client_0, // @[:@59.4]
  output [7:0]  io_field_wr_weight_client_1, // @[:@59.4]
  output [7:0]  io_field_wr_weight_client_2, // @[:@59.4]
  output [7:0]  io_field_wr_weight_client_3, // @[:@59.4]
  output [7:0]  io_field_wr_weight_client_4, // @[:@59.4]
  input         io_idle // @[:@59.4]
);
  wire [31:0] _GEN_22; // @[NV_NVDLA_MCIF_CSB_reg.scala 47:64:@61.4]
  wire  _T_82; // @[NV_NVDLA_MCIF_CSB_reg.scala 47:64:@61.4]
  wire  _T_83; // @[NV_NVDLA_MCIF_CSB_reg.scala 47:88:@62.4]
  wire  _T_85; // @[NV_NVDLA_MCIF_CSB_reg.scala 48:60:@63.4]
  wire  _T_86; // @[NV_NVDLA_MCIF_CSB_reg.scala 48:84:@64.4]
  wire  _T_88; // @[NV_NVDLA_MCIF_CSB_reg.scala 49:60:@65.4]
  wire  _T_89; // @[NV_NVDLA_MCIF_CSB_reg.scala 49:84:@66.4]
  wire  _T_91; // @[NV_NVDLA_MCIF_CSB_reg.scala 50:60:@67.4]
  wire  _T_92; // @[NV_NVDLA_MCIF_CSB_reg.scala 50:84:@68.4]
  wire  _T_94; // @[NV_NVDLA_MCIF_CSB_reg.scala 51:60:@69.4]
  wire  _T_95; // @[NV_NVDLA_MCIF_CSB_reg.scala 51:84:@70.4]
  wire  _T_97; // @[NV_NVDLA_MCIF_CSB_reg.scala 52:60:@71.4]
  wire  _T_98; // @[NV_NVDLA_MCIF_CSB_reg.scala 52:84:@72.4]
  wire [31:0] _T_106; // @[Cat.scala 30:58:@76.4]
  wire [31:0] _T_110; // @[Cat.scala 30:58:@79.4]
  wire [31:0] _T_114; // @[Cat.scala 30:58:@82.4]
  wire [31:0] _T_118; // @[Cat.scala 30:58:@85.4]
  wire [31:0] _T_122; // @[Cat.scala 30:58:@88.4]
  wire [31:0] _T_126; // @[Cat.scala 30:58:@91.4]
  wire [31:0] _T_131; // @[Cat.scala 30:58:@93.4]
  wire  _T_132; // @[Mux.scala 46:19:@94.4]
  wire [31:0] _T_133; // @[Mux.scala 46:16:@95.4]
  wire  _T_134; // @[Mux.scala 46:19:@96.4]
  wire [31:0] _T_135; // @[Mux.scala 46:16:@97.4]
  wire  _T_136; // @[Mux.scala 46:19:@98.4]
  wire [31:0] _T_137; // @[Mux.scala 46:16:@99.4]
  wire  _T_138; // @[Mux.scala 46:19:@100.4]
  wire [31:0] _T_139; // @[Mux.scala 46:16:@101.4]
  wire  _T_140; // @[Mux.scala 46:19:@102.4]
  wire [31:0] _T_141; // @[Mux.scala 46:16:@103.4]
  wire  _T_142; // @[Mux.scala 46:19:@104.4]
  wire [31:0] _T_143; // @[Mux.scala 46:16:@105.4]
  wire  _T_144; // @[Mux.scala 46:19:@106.4]
  wire [7:0] _T_146; // @[NV_NVDLA_MCIF_CSB_reg.scala 77:51:@109.4]
  reg [7:0] _T_149; // @[Reg.scala 19:20:@110.4]
  reg [31:0] _RAND_0;
  wire [7:0] _GEN_0; // @[Reg.scala 20:19:@111.4]
  wire [7:0] _T_150; // @[NV_NVDLA_MCIF_CSB_reg.scala 79:51:@115.4]
  reg [7:0] _T_153; // @[Reg.scala 19:20:@116.4]
  reg [31:0] _RAND_1;
  wire [7:0] _GEN_1; // @[Reg.scala 20:19:@117.4]
  reg [7:0] _T_157; // @[Reg.scala 19:20:@122.4]
  reg [31:0] _RAND_2;
  wire [7:0] _GEN_2; // @[Reg.scala 20:19:@123.4]
  wire [7:0] _T_158; // @[NV_NVDLA_MCIF_CSB_reg.scala 83:80:@127.4]
  reg [7:0] _T_161; // @[Reg.scala 19:20:@128.4]
  reg [31:0] _RAND_3;
  wire [7:0] _GEN_3; // @[Reg.scala 20:19:@129.4]
  wire [7:0] _T_162; // @[NV_NVDLA_MCIF_CSB_reg.scala 85:80:@133.4]
  reg [7:0] _T_165; // @[Reg.scala 19:20:@134.4]
  reg [31:0] _RAND_4;
  wire [7:0] _GEN_4; // @[Reg.scala 20:19:@135.4]
  reg [7:0] _T_169; // @[Reg.scala 19:20:@140.4]
  reg [31:0] _RAND_5;
  wire [7:0] _GEN_5; // @[Reg.scala 20:19:@141.4]
  reg [7:0] _T_173; // @[Reg.scala 19:20:@146.4]
  reg [31:0] _RAND_6;
  wire [7:0] _GEN_6; // @[Reg.scala 20:19:@147.4]
  reg [7:0] _T_177; // @[Reg.scala 19:20:@152.4]
  reg [31:0] _RAND_7;
  wire [7:0] _GEN_7; // @[Reg.scala 20:19:@153.4]
  reg [7:0] _T_181; // @[Reg.scala 19:20:@158.4]
  reg [31:0] _RAND_8;
  wire [7:0] _GEN_8; // @[Reg.scala 20:19:@159.4]
  reg [7:0] _T_185; // @[Reg.scala 19:20:@164.4]
  reg [31:0] _RAND_9;
  wire [7:0] _GEN_9; // @[Reg.scala 20:19:@165.4]
  reg [7:0] _T_189; // @[Reg.scala 19:20:@170.4]
  reg [31:0] _RAND_10;
  wire [7:0] _GEN_10; // @[Reg.scala 20:19:@171.4]
  reg [7:0] _T_193; // @[Reg.scala 19:20:@176.4]
  reg [31:0] _RAND_11;
  wire [7:0] _GEN_11; // @[Reg.scala 20:19:@177.4]
  reg [7:0] _T_197; // @[Reg.scala 19:20:@182.4]
  reg [31:0] _RAND_12;
  wire [7:0] _GEN_12; // @[Reg.scala 20:19:@183.4]
  reg [7:0] _T_201; // @[Reg.scala 19:20:@188.4]
  reg [31:0] _RAND_13;
  wire [7:0] _GEN_13; // @[Reg.scala 20:19:@189.4]
  reg [7:0] _T_205; // @[Reg.scala 19:20:@194.4]
  reg [31:0] _RAND_14;
  wire [7:0] _GEN_14; // @[Reg.scala 20:19:@195.4]
  reg [7:0] _T_209; // @[Reg.scala 19:20:@200.4]
  reg [31:0] _RAND_15;
  wire [7:0] _GEN_15; // @[Reg.scala 20:19:@201.4]
  reg [7:0] _T_213; // @[Reg.scala 19:20:@206.4]
  reg [31:0] _RAND_16;
  wire [7:0] _GEN_16; // @[Reg.scala 20:19:@207.4]
  reg [7:0] _T_217; // @[Reg.scala 19:20:@212.4]
  reg [31:0] _RAND_17;
  wire [7:0] _GEN_17; // @[Reg.scala 20:19:@213.4]
  reg [7:0] _T_221; // @[Reg.scala 19:20:@218.4]
  reg [31:0] _RAND_18;
  wire [7:0] _GEN_18; // @[Reg.scala 20:19:@219.4]
  reg [7:0] _T_225; // @[Reg.scala 19:20:@224.4]
  reg [31:0] _RAND_19;
  wire [7:0] _GEN_19; // @[Reg.scala 20:19:@225.4]
  reg [7:0] _T_229; // @[Reg.scala 19:20:@230.4]
  reg [31:0] _RAND_20;
  wire [7:0] _GEN_20; // @[Reg.scala 20:19:@231.4]
  reg [7:0] _T_233; // @[Reg.scala 19:20:@236.4]
  reg [31:0] _RAND_21;
  wire [7:0] _GEN_21; // @[Reg.scala 20:19:@237.4]
  assign _GEN_22 = {{20'd0}, io_reg_offset}; // @[NV_NVDLA_MCIF_CSB_reg.scala 47:64:@61.4]
  assign _T_82 = _GEN_22 == 32'h14; // @[NV_NVDLA_MCIF_CSB_reg.scala 47:64:@61.4]
  assign _T_83 = _T_82 & io_reg_wr_en; // @[NV_NVDLA_MCIF_CSB_reg.scala 47:88:@62.4]
  assign _T_85 = _GEN_22 == 32'h0; // @[NV_NVDLA_MCIF_CSB_reg.scala 48:60:@63.4]
  assign _T_86 = _T_85 & io_reg_wr_en; // @[NV_NVDLA_MCIF_CSB_reg.scala 48:84:@64.4]
  assign _T_88 = _GEN_22 == 32'h4; // @[NV_NVDLA_MCIF_CSB_reg.scala 49:60:@65.4]
  assign _T_89 = _T_88 & io_reg_wr_en; // @[NV_NVDLA_MCIF_CSB_reg.scala 49:84:@66.4]
  assign _T_91 = _GEN_22 == 32'h8; // @[NV_NVDLA_MCIF_CSB_reg.scala 50:60:@67.4]
  assign _T_92 = _T_91 & io_reg_wr_en; // @[NV_NVDLA_MCIF_CSB_reg.scala 50:84:@68.4]
  assign _T_94 = _GEN_22 == 32'hc; // @[NV_NVDLA_MCIF_CSB_reg.scala 51:60:@69.4]
  assign _T_95 = _T_94 & io_reg_wr_en; // @[NV_NVDLA_MCIF_CSB_reg.scala 51:84:@70.4]
  assign _T_97 = _GEN_22 == 32'h10; // @[NV_NVDLA_MCIF_CSB_reg.scala 52:60:@71.4]
  assign _T_98 = _T_97 & io_reg_wr_en; // @[NV_NVDLA_MCIF_CSB_reg.scala 52:84:@72.4]
  assign _T_106 = {16'h0,io_field_wr_os_cnt,io_field_rd_os_cnt}; // @[Cat.scala 30:58:@76.4]
  assign _T_110 = {io_field_rd_weight_client_3,io_field_rd_weight_client_2,io_field_rd_weight_client_1,io_field_rd_weight_client_0}; // @[Cat.scala 30:58:@79.4]
  assign _T_114 = {io_field_rd_weight_client_8,io_field_rd_weight_client_7,io_field_rd_weight_client_6,io_field_rd_weight_client_5}; // @[Cat.scala 30:58:@82.4]
  assign _T_118 = {io_field_rd_weight_rsv_0,io_field_rd_weight_rsv_1,io_field_rd_weight_client_4,io_field_rd_weight_client_9}; // @[Cat.scala 30:58:@85.4]
  assign _T_122 = {io_field_wr_weight_client_3,io_field_wr_weight_client_2,io_field_wr_weight_client_1,io_field_wr_weight_client_0}; // @[Cat.scala 30:58:@88.4]
  assign _T_126 = {io_field_wr_weight_rsv_0,io_field_wr_weight_rsv_1,io_field_wr_weight_rsv_2,io_field_wr_weight_client_4}; // @[Cat.scala 30:58:@91.4]
  assign _T_131 = {23'h0,io_idle,8'h0}; // @[Cat.scala 30:58:@93.4]
  assign _T_132 = 32'h18 == _GEN_22; // @[Mux.scala 46:19:@94.4]
  assign _T_133 = _T_132 ? _T_131 : 32'h0; // @[Mux.scala 46:16:@95.4]
  assign _T_134 = 32'h10 == _GEN_22; // @[Mux.scala 46:19:@96.4]
  assign _T_135 = _T_134 ? _T_126 : _T_133; // @[Mux.scala 46:16:@97.4]
  assign _T_136 = 32'hc == _GEN_22; // @[Mux.scala 46:19:@98.4]
  assign _T_137 = _T_136 ? _T_122 : _T_135; // @[Mux.scala 46:16:@99.4]
  assign _T_138 = 32'h8 == _GEN_22; // @[Mux.scala 46:19:@100.4]
  assign _T_139 = _T_138 ? _T_118 : _T_137; // @[Mux.scala 46:16:@101.4]
  assign _T_140 = 32'h4 == _GEN_22; // @[Mux.scala 46:19:@102.4]
  assign _T_141 = _T_140 ? _T_114 : _T_139; // @[Mux.scala 46:16:@103.4]
  assign _T_142 = 32'h0 == _GEN_22; // @[Mux.scala 46:19:@104.4]
  assign _T_143 = _T_142 ? _T_110 : _T_141; // @[Mux.scala 46:16:@105.4]
  assign _T_144 = 32'h14 == _GEN_22; // @[Mux.scala 46:19:@106.4]
  assign _T_146 = io_reg_wr_data[7:0]; // @[NV_NVDLA_MCIF_CSB_reg.scala 77:51:@109.4]
  assign _GEN_0 = _T_83 ? _T_146 : _T_149; // @[Reg.scala 20:19:@111.4]
  assign _T_150 = io_reg_wr_data[15:8]; // @[NV_NVDLA_MCIF_CSB_reg.scala 79:51:@115.4]
  assign _GEN_1 = _T_83 ? _T_150 : _T_153; // @[Reg.scala 20:19:@117.4]
  assign _GEN_2 = _T_86 ? _T_146 : _T_157; // @[Reg.scala 20:19:@123.4]
  assign _T_158 = io_reg_wr_data[31:24]; // @[NV_NVDLA_MCIF_CSB_reg.scala 83:80:@127.4]
  assign _GEN_3 = _T_86 ? _T_158 : _T_161; // @[Reg.scala 20:19:@129.4]
  assign _T_162 = io_reg_wr_data[23:16]; // @[NV_NVDLA_MCIF_CSB_reg.scala 85:80:@133.4]
  assign _GEN_4 = _T_86 ? _T_162 : _T_165; // @[Reg.scala 20:19:@135.4]
  assign _GEN_5 = _T_86 ? _T_150 : _T_169; // @[Reg.scala 20:19:@141.4]
  assign _GEN_6 = _T_89 ? _T_158 : _T_173; // @[Reg.scala 20:19:@147.4]
  assign _GEN_7 = _T_89 ? _T_146 : _T_177; // @[Reg.scala 20:19:@153.4]
  assign _GEN_8 = _T_89 ? _T_162 : _T_181; // @[Reg.scala 20:19:@159.4]
  assign _GEN_9 = _T_89 ? _T_150 : _T_185; // @[Reg.scala 20:19:@165.4]
  assign _GEN_10 = _T_92 ? _T_146 : _T_189; // @[Reg.scala 20:19:@171.4]
  assign _GEN_11 = _T_92 ? _T_150 : _T_193; // @[Reg.scala 20:19:@177.4]
  assign _GEN_12 = _T_92 ? _T_158 : _T_197; // @[Reg.scala 20:19:@183.4]
  assign _GEN_13 = _T_92 ? _T_162 : _T_201; // @[Reg.scala 20:19:@189.4]
  assign _GEN_14 = _T_95 ? _T_146 : _T_205; // @[Reg.scala 20:19:@195.4]
  assign _GEN_15 = _T_95 ? _T_158 : _T_209; // @[Reg.scala 20:19:@201.4]
  assign _GEN_16 = _T_95 ? _T_162 : _T_213; // @[Reg.scala 20:19:@207.4]
  assign _GEN_17 = _T_95 ? _T_150 : _T_217; // @[Reg.scala 20:19:@213.4]
  assign _GEN_18 = _T_98 ? _T_146 : _T_221; // @[Reg.scala 20:19:@219.4]
  assign _GEN_19 = _T_98 ? _T_158 : _T_225; // @[Reg.scala 20:19:@225.4]
  assign _GEN_20 = _T_98 ? _T_162 : _T_229; // @[Reg.scala 20:19:@231.4]
  assign _GEN_21 = _T_98 ? _T_150 : _T_233; // @[Reg.scala 20:19:@237.4]
  assign io_reg_rd_data = _T_144 ? _T_106 : _T_143; // @[NV_NVDLA_MCIF_CSB_reg.scala 56:20:@108.4]
  assign io_field_rd_os_cnt = _T_149; // @[NV_NVDLA_MCIF_CSB_reg.scala 77:24:@114.4]
  assign io_field_rd_weight_rsv_0 = _T_197; // @[NV_NVDLA_MCIF_CSB_reg.scala 101:30:@186.4]
  assign io_field_rd_weight_rsv_1 = _T_201; // @[NV_NVDLA_MCIF_CSB_reg.scala 103:30:@192.4]
  assign io_field_rd_weight_client_0 = _T_157; // @[NV_NVDLA_MCIF_CSB_reg.scala 81:54:@126.4]
  assign io_field_rd_weight_client_1 = _T_169; // @[NV_NVDLA_MCIF_CSB_reg.scala 87:53:@144.4]
  assign io_field_rd_weight_client_2 = _T_165; // @[NV_NVDLA_MCIF_CSB_reg.scala 85:53:@138.4]
  assign io_field_rd_weight_client_3 = _T_161; // @[NV_NVDLA_MCIF_CSB_reg.scala 83:53:@132.4]
  assign io_field_rd_weight_client_4 = _T_193; // @[NV_NVDLA_MCIF_CSB_reg.scala 99:53:@180.4]
  assign io_field_rd_weight_client_5 = _T_177; // @[NV_NVDLA_MCIF_CSB_reg.scala 91:55:@156.4]
  assign io_field_rd_weight_client_6 = _T_185; // @[NV_NVDLA_MCIF_CSB_reg.scala 95:55:@168.4]
  assign io_field_rd_weight_client_7 = _T_181; // @[NV_NVDLA_MCIF_CSB_reg.scala 93:55:@162.4]
  assign io_field_rd_weight_client_8 = _T_173; // @[NV_NVDLA_MCIF_CSB_reg.scala 89:58:@150.4]
  assign io_field_rd_weight_client_9 = _T_189; // @[NV_NVDLA_MCIF_CSB_reg.scala 97:57:@174.4]
  assign io_field_wr_os_cnt = _T_153; // @[NV_NVDLA_MCIF_CSB_reg.scala 79:24:@120.4]
  assign io_field_wr_weight_rsv_0 = _T_225; // @[NV_NVDLA_MCIF_CSB_reg.scala 115:30:@228.4]
  assign io_field_wr_weight_rsv_1 = _T_229; // @[NV_NVDLA_MCIF_CSB_reg.scala 117:30:@234.4]
  assign io_field_wr_weight_rsv_2 = _T_233; // @[NV_NVDLA_MCIF_CSB_reg.scala 119:30:@240.4]
  assign io_field_wr_weight_client_0 = _T_205; // @[NV_NVDLA_MCIF_CSB_reg.scala 105:54:@198.4]
  assign io_field_wr_weight_client_1 = _T_217; // @[NV_NVDLA_MCIF_CSB_reg.scala 111:53:@216.4]
  assign io_field_wr_weight_client_2 = _T_213; // @[NV_NVDLA_MCIF_CSB_reg.scala 109:53:@210.4]
  assign io_field_wr_weight_client_3 = _T_209; // @[NV_NVDLA_MCIF_CSB_reg.scala 107:53:@204.4]
  assign io_field_wr_weight_client_4 = _T_221; // @[NV_NVDLA_MCIF_CSB_reg.scala 113:53:@222.4]
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
  _T_149 = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_153 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_157 = _RAND_2[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_161 = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_165 = _RAND_4[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_169 = _RAND_5[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_173 = _RAND_6[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_177 = _RAND_7[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  _T_181 = _RAND_8[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  _T_185 = _RAND_9[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  _T_189 = _RAND_10[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  _T_193 = _RAND_11[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  _T_197 = _RAND_12[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  _T_201 = _RAND_13[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  _T_205 = _RAND_14[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_15 = {1{`RANDOM}};
  _T_209 = _RAND_15[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_16 = {1{`RANDOM}};
  _T_213 = _RAND_16[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_17 = {1{`RANDOM}};
  _T_217 = _RAND_17[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_18 = {1{`RANDOM}};
  _T_221 = _RAND_18[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_19 = {1{`RANDOM}};
  _T_225 = _RAND_19[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_20 = {1{`RANDOM}};
  _T_229 = _RAND_20[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_21 = {1{`RANDOM}};
  _T_233 = _RAND_21[7:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_149 <= 8'hff;
    end else begin
      if (_T_83) begin
        _T_149 <= _T_146;
      end
    end
    if (reset) begin
      _T_153 <= 8'hff;
    end else begin
      if (_T_83) begin
        _T_153 <= _T_150;
      end
    end
    if (reset) begin
      _T_157 <= 8'h1;
    end else begin
      if (_T_86) begin
        _T_157 <= _T_146;
      end
    end
    if (reset) begin
      _T_161 <= 8'h1;
    end else begin
      if (_T_86) begin
        _T_161 <= _T_158;
      end
    end
    if (reset) begin
      _T_165 <= 8'h1;
    end else begin
      if (_T_86) begin
        _T_165 <= _T_162;
      end
    end
    if (reset) begin
      _T_169 <= 8'h1;
    end else begin
      if (_T_86) begin
        _T_169 <= _T_150;
      end
    end
    if (reset) begin
      _T_173 <= 8'h1;
    end else begin
      if (_T_89) begin
        _T_173 <= _T_158;
      end
    end
    if (reset) begin
      _T_177 <= 8'h1;
    end else begin
      if (_T_89) begin
        _T_177 <= _T_146;
      end
    end
    if (reset) begin
      _T_181 <= 8'h1;
    end else begin
      if (_T_89) begin
        _T_181 <= _T_162;
      end
    end
    if (reset) begin
      _T_185 <= 8'h1;
    end else begin
      if (_T_89) begin
        _T_185 <= _T_150;
      end
    end
    if (reset) begin
      _T_189 <= 8'h1;
    end else begin
      if (_T_92) begin
        _T_189 <= _T_146;
      end
    end
    if (reset) begin
      _T_193 <= 8'h1;
    end else begin
      if (_T_92) begin
        _T_193 <= _T_150;
      end
    end
    if (reset) begin
      _T_197 <= 8'h1;
    end else begin
      if (_T_92) begin
        _T_197 <= _T_158;
      end
    end
    if (reset) begin
      _T_201 <= 8'h1;
    end else begin
      if (_T_92) begin
        _T_201 <= _T_162;
      end
    end
    if (reset) begin
      _T_205 <= 8'h1;
    end else begin
      if (_T_95) begin
        _T_205 <= _T_146;
      end
    end
    if (reset) begin
      _T_209 <= 8'h1;
    end else begin
      if (_T_95) begin
        _T_209 <= _T_158;
      end
    end
    if (reset) begin
      _T_213 <= 8'h1;
    end else begin
      if (_T_95) begin
        _T_213 <= _T_162;
      end
    end
    if (reset) begin
      _T_217 <= 8'h1;
    end else begin
      if (_T_95) begin
        _T_217 <= _T_150;
      end
    end
    if (reset) begin
      _T_221 <= 8'h1;
    end else begin
      if (_T_98) begin
        _T_221 <= _T_146;
      end
    end
    if (reset) begin
      _T_225 <= 8'h1;
    end else begin
      if (_T_98) begin
        _T_225 <= _T_158;
      end
    end
    if (reset) begin
      _T_229 <= 8'h1;
    end else begin
      if (_T_98) begin
        _T_229 <= _T_162;
      end
    end
    if (reset) begin
      _T_233 <= 8'h1;
    end else begin
      if (_T_98) begin
        _T_233 <= _T_150;
      end
    end
  end
endmodule
module NV_NVDLA_MCIF_csb( // @[:@242.2]
  input         clock, // @[:@243.4]
  input         reset, // @[:@244.4]
  input         io_nvdla_core_clk, // @[:@245.4]
  output        io_csb2mcif_req_ready, // @[:@245.4]
  input         io_csb2mcif_req_valid, // @[:@245.4]
  input  [62:0] io_csb2mcif_req_bits, // @[:@245.4]
  output        io_csb2mcif_resp_valid, // @[:@245.4]
  output [33:0] io_csb2mcif_resp_bits, // @[:@245.4]
  input         io_dp2reg_idle, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_os_cnt, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_rsv_0, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_rsv_1, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_0, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_1, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_2, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_3, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_4, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_5, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_6, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_7, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_8, // @[:@245.4]
  output [7:0]  io_reg2dp_field_rd_weight_client_9, // @[:@245.4]
  output [7:0]  io_reg2dp_field_wr_os_cnt, // @[:@245.4]
  output [7:0]  io_reg2dp_field_wr_weight_rsv_0, // @[:@245.4]
  output [7:0]  io_reg2dp_field_wr_weight_rsv_1, // @[:@245.4]
  output [7:0]  io_reg2dp_field_wr_weight_rsv_2, // @[:@245.4]
  output [7:0]  io_reg2dp_field_wr_weight_client_0, // @[:@245.4]
  output [7:0]  io_reg2dp_field_wr_weight_client_1, // @[:@245.4]
  output [7:0]  io_reg2dp_field_wr_weight_client_2, // @[:@245.4]
  output [7:0]  io_reg2dp_field_wr_weight_client_3, // @[:@245.4]
  output [7:0]  io_reg2dp_field_wr_weight_client_4 // @[:@245.4]
);
  wire  NV_NVDLA_CSB_LOGIC_reset; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire  NV_NVDLA_CSB_LOGIC_io_clk; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire  NV_NVDLA_CSB_LOGIC_io_csb2dp_req_valid; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire [62:0] NV_NVDLA_CSB_LOGIC_io_csb2dp_req_bits; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire  NV_NVDLA_CSB_LOGIC_io_csb2dp_resp_valid; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire [33:0] NV_NVDLA_CSB_LOGIC_io_csb2dp_resp_bits; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire [31:0] NV_NVDLA_CSB_LOGIC_io_reg_rd_data; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire [11:0] NV_NVDLA_CSB_LOGIC_io_reg_offset; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire [31:0] NV_NVDLA_CSB_LOGIC_io_reg_wr_data; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire  NV_NVDLA_CSB_LOGIC_io_reg_wr_en; // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
  wire  NV_NVDLA_MCIF_CSB_reg_reset; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire  NV_NVDLA_MCIF_CSB_reg_io_nvdla_core_clk; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [31:0] NV_NVDLA_MCIF_CSB_reg_io_reg_rd_data; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [11:0] NV_NVDLA_MCIF_CSB_reg_io_reg_offset; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [31:0] NV_NVDLA_MCIF_CSB_reg_io_reg_wr_data; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire  NV_NVDLA_MCIF_CSB_reg_io_reg_wr_en; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_os_cnt; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_rsv_0; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_rsv_1; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_0; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_1; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_2; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_3; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_4; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_5; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_6; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_7; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_8; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_9; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_wr_os_cnt; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_rsv_0; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_rsv_1; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_rsv_2; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_0; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_1; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_2; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_3; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire [7:0] NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_4; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  wire  NV_NVDLA_MCIF_CSB_reg_io_idle; // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
  NV_NVDLA_CSB_LOGIC NV_NVDLA_CSB_LOGIC ( // @[NV_NVDLA_MCIF_csb.scala 53:27:@247.4]
    .reset(NV_NVDLA_CSB_LOGIC_reset),
    .io_clk(NV_NVDLA_CSB_LOGIC_io_clk),
    .io_csb2dp_req_valid(NV_NVDLA_CSB_LOGIC_io_csb2dp_req_valid),
    .io_csb2dp_req_bits(NV_NVDLA_CSB_LOGIC_io_csb2dp_req_bits),
    .io_csb2dp_resp_valid(NV_NVDLA_CSB_LOGIC_io_csb2dp_resp_valid),
    .io_csb2dp_resp_bits(NV_NVDLA_CSB_LOGIC_io_csb2dp_resp_bits),
    .io_reg_rd_data(NV_NVDLA_CSB_LOGIC_io_reg_rd_data),
    .io_reg_offset(NV_NVDLA_CSB_LOGIC_io_reg_offset),
    .io_reg_wr_data(NV_NVDLA_CSB_LOGIC_io_reg_wr_data),
    .io_reg_wr_en(NV_NVDLA_CSB_LOGIC_io_reg_wr_en)
  );
  NV_NVDLA_MCIF_CSB_reg NV_NVDLA_MCIF_CSB_reg ( // @[NV_NVDLA_MCIF_csb.scala 54:25:@250.4]
    .reset(NV_NVDLA_MCIF_CSB_reg_reset),
    .io_nvdla_core_clk(NV_NVDLA_MCIF_CSB_reg_io_nvdla_core_clk),
    .io_reg_rd_data(NV_NVDLA_MCIF_CSB_reg_io_reg_rd_data),
    .io_reg_offset(NV_NVDLA_MCIF_CSB_reg_io_reg_offset),
    .io_reg_wr_data(NV_NVDLA_MCIF_CSB_reg_io_reg_wr_data),
    .io_reg_wr_en(NV_NVDLA_MCIF_CSB_reg_io_reg_wr_en),
    .io_field_rd_os_cnt(NV_NVDLA_MCIF_CSB_reg_io_field_rd_os_cnt),
    .io_field_rd_weight_rsv_0(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_rsv_0),
    .io_field_rd_weight_rsv_1(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_rsv_1),
    .io_field_rd_weight_client_0(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_0),
    .io_field_rd_weight_client_1(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_1),
    .io_field_rd_weight_client_2(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_2),
    .io_field_rd_weight_client_3(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_3),
    .io_field_rd_weight_client_4(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_4),
    .io_field_rd_weight_client_5(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_5),
    .io_field_rd_weight_client_6(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_6),
    .io_field_rd_weight_client_7(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_7),
    .io_field_rd_weight_client_8(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_8),
    .io_field_rd_weight_client_9(NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_9),
    .io_field_wr_os_cnt(NV_NVDLA_MCIF_CSB_reg_io_field_wr_os_cnt),
    .io_field_wr_weight_rsv_0(NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_rsv_0),
    .io_field_wr_weight_rsv_1(NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_rsv_1),
    .io_field_wr_weight_rsv_2(NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_rsv_2),
    .io_field_wr_weight_client_0(NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_0),
    .io_field_wr_weight_client_1(NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_1),
    .io_field_wr_weight_client_2(NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_2),
    .io_field_wr_weight_client_3(NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_3),
    .io_field_wr_weight_client_4(NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_4),
    .io_idle(NV_NVDLA_MCIF_CSB_reg_io_idle)
  );
  assign io_csb2mcif_req_ready = 1'h1; // @[NV_NVDLA_MCIF_csb.scala 57:25:@259.4]
  assign io_csb2mcif_resp_valid = NV_NVDLA_CSB_LOGIC_io_csb2dp_resp_valid; // @[NV_NVDLA_MCIF_csb.scala 57:25:@256.4]
  assign io_csb2mcif_resp_bits = NV_NVDLA_CSB_LOGIC_io_csb2dp_resp_bits; // @[NV_NVDLA_MCIF_csb.scala 57:25:@255.4]
  assign io_reg2dp_field_rd_os_cnt = NV_NVDLA_MCIF_CSB_reg_io_field_rd_os_cnt; // @[NV_NVDLA_MCIF_csb.scala 60:21:@286.4]
  assign io_reg2dp_field_rd_weight_rsv_0 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_rsv_0; // @[NV_NVDLA_MCIF_csb.scala 60:21:@285.4]
  assign io_reg2dp_field_rd_weight_rsv_1 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_rsv_1; // @[NV_NVDLA_MCIF_csb.scala 60:21:@284.4]
  assign io_reg2dp_field_rd_weight_client_0 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_0; // @[NV_NVDLA_MCIF_csb.scala 60:21:@274.4]
  assign io_reg2dp_field_rd_weight_client_1 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_1; // @[NV_NVDLA_MCIF_csb.scala 60:21:@275.4]
  assign io_reg2dp_field_rd_weight_client_2 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_2; // @[NV_NVDLA_MCIF_csb.scala 60:21:@276.4]
  assign io_reg2dp_field_rd_weight_client_3 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_3; // @[NV_NVDLA_MCIF_csb.scala 60:21:@277.4]
  assign io_reg2dp_field_rd_weight_client_4 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_4; // @[NV_NVDLA_MCIF_csb.scala 60:21:@278.4]
  assign io_reg2dp_field_rd_weight_client_5 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_5; // @[NV_NVDLA_MCIF_csb.scala 60:21:@279.4]
  assign io_reg2dp_field_rd_weight_client_6 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_6; // @[NV_NVDLA_MCIF_csb.scala 60:21:@280.4]
  assign io_reg2dp_field_rd_weight_client_7 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_7; // @[NV_NVDLA_MCIF_csb.scala 60:21:@281.4]
  assign io_reg2dp_field_rd_weight_client_8 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_8; // @[NV_NVDLA_MCIF_csb.scala 60:21:@282.4]
  assign io_reg2dp_field_rd_weight_client_9 = NV_NVDLA_MCIF_CSB_reg_io_field_rd_weight_client_9; // @[NV_NVDLA_MCIF_csb.scala 60:21:@283.4]
  assign io_reg2dp_field_wr_os_cnt = NV_NVDLA_MCIF_CSB_reg_io_field_wr_os_cnt; // @[NV_NVDLA_MCIF_csb.scala 60:21:@273.4]
  assign io_reg2dp_field_wr_weight_rsv_0 = NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_rsv_0; // @[NV_NVDLA_MCIF_csb.scala 60:21:@272.4]
  assign io_reg2dp_field_wr_weight_rsv_1 = NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_rsv_1; // @[NV_NVDLA_MCIF_csb.scala 60:21:@271.4]
  assign io_reg2dp_field_wr_weight_rsv_2 = NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_rsv_2; // @[NV_NVDLA_MCIF_csb.scala 60:21:@270.4]
  assign io_reg2dp_field_wr_weight_client_0 = NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_0; // @[NV_NVDLA_MCIF_csb.scala 60:21:@265.4]
  assign io_reg2dp_field_wr_weight_client_1 = NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_1; // @[NV_NVDLA_MCIF_csb.scala 60:21:@266.4]
  assign io_reg2dp_field_wr_weight_client_2 = NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_2; // @[NV_NVDLA_MCIF_csb.scala 60:21:@267.4]
  assign io_reg2dp_field_wr_weight_client_3 = NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_3; // @[NV_NVDLA_MCIF_csb.scala 60:21:@268.4]
  assign io_reg2dp_field_wr_weight_client_4 = NV_NVDLA_MCIF_CSB_reg_io_field_wr_weight_client_4; // @[NV_NVDLA_MCIF_csb.scala 60:21:@269.4]
  assign NV_NVDLA_CSB_LOGIC_reset = reset; // @[:@249.4]
  assign NV_NVDLA_CSB_LOGIC_io_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_csb.scala 55:22:@253.4]
  assign NV_NVDLA_CSB_LOGIC_io_csb2dp_req_valid = io_csb2mcif_req_valid; // @[NV_NVDLA_MCIF_csb.scala 57:25:@258.4]
  assign NV_NVDLA_CSB_LOGIC_io_csb2dp_req_bits = io_csb2mcif_req_bits; // @[NV_NVDLA_MCIF_csb.scala 57:25:@257.4]
  assign NV_NVDLA_CSB_LOGIC_io_reg_rd_data = NV_NVDLA_MCIF_CSB_reg_io_reg_rd_data; // @[NV_NVDLA_MCIF_csb.scala 58:22:@263.4]
  assign NV_NVDLA_MCIF_CSB_reg_reset = reset; // @[:@252.4]
  assign NV_NVDLA_MCIF_CSB_reg_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_MCIF_csb.scala 56:31:@254.4]
  assign NV_NVDLA_MCIF_CSB_reg_io_reg_offset = NV_NVDLA_CSB_LOGIC_io_reg_offset; // @[NV_NVDLA_MCIF_csb.scala 58:22:@262.4]
  assign NV_NVDLA_MCIF_CSB_reg_io_reg_wr_data = NV_NVDLA_CSB_LOGIC_io_reg_wr_data; // @[NV_NVDLA_MCIF_csb.scala 58:22:@261.4]
  assign NV_NVDLA_MCIF_CSB_reg_io_reg_wr_en = NV_NVDLA_CSB_LOGIC_io_reg_wr_en; // @[NV_NVDLA_MCIF_csb.scala 58:22:@260.4]
  assign NV_NVDLA_MCIF_CSB_reg_io_idle = io_dp2reg_idle; // @[NV_NVDLA_MCIF_csb.scala 59:21:@264.4]
endmodule
