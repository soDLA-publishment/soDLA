module NV_NVDLA_GLB_CSB_reg( // @[:@3.2]
  input         reset, // @[:@5.4]
  input         io_nvdla_core_clk, // @[:@6.4]
  output [31:0] io_reg_rd_data, // @[:@6.4]
  input  [11:0] io_reg_offset, // @[:@6.4]
  input  [31:0] io_reg_wr_data, // @[:@6.4]
  input         io_reg_wr_en, // @[:@6.4]
  output        io_bdma_done_mask0, // @[:@6.4]
  output        io_bdma_done_mask1, // @[:@6.4]
  output        io_cacc_done_mask0, // @[:@6.4]
  output        io_cacc_done_mask1, // @[:@6.4]
  output        io_cdma_dat_done_mask0, // @[:@6.4]
  output        io_cdma_dat_done_mask1, // @[:@6.4]
  output        io_cdma_wt_done_mask0, // @[:@6.4]
  output        io_cdma_wt_done_mask1, // @[:@6.4]
  output        io_cdp_done_mask0, // @[:@6.4]
  output        io_cdp_done_mask1, // @[:@6.4]
  output        io_pdp_done_mask0, // @[:@6.4]
  output        io_pdp_done_mask1, // @[:@6.4]
  output        io_rubik_done_mask0, // @[:@6.4]
  output        io_rubik_done_mask1, // @[:@6.4]
  output        io_sdp_done_mask0, // @[:@6.4]
  output        io_sdp_done_mask1, // @[:@6.4]
  output        io_sdp_done_set0_trigger, // @[:@6.4]
  output        io_sdp_done_status0_trigger, // @[:@6.4]
  input         io_cacc_done_status0, // @[:@6.4]
  input         io_cacc_done_status1, // @[:@6.4]
  input         io_cdma_dat_done_status0, // @[:@6.4]
  input         io_cdma_dat_done_status1, // @[:@6.4]
  input         io_cdma_wt_done_status0, // @[:@6.4]
  input         io_cdma_wt_done_status1, // @[:@6.4]
  input         io_cdp_done_status0, // @[:@6.4]
  input         io_cdp_done_status1, // @[:@6.4]
  input         io_pdp_done_status0, // @[:@6.4]
  input         io_pdp_done_status1, // @[:@6.4]
  input         io_sdp_done_status0, // @[:@6.4]
  input         io_sdp_done_status1 // @[:@6.4]
);
  wire [31:0] _GEN_16; // @[NV_NVDLA_GLB_CSB_reg.scala 95:55:@8.4]
  wire  _T_116; // @[NV_NVDLA_GLB_CSB_reg.scala 95:55:@8.4]
  wire  _T_117; // @[NV_NVDLA_GLB_CSB_reg.scala 95:77:@9.4]
  wire  _T_119; // @[NV_NVDLA_GLB_CSB_reg.scala 96:54:@10.4]
  wire  _T_122; // @[NV_NVDLA_GLB_CSB_reg.scala 97:57:@12.4]
  wire [8:0] _T_140; // @[Cat.scala 30:58:@25.4]
  wire [31:0] _T_149; // @[Cat.scala 30:58:@34.4]
  wire [8:0] _T_180; // @[Cat.scala 30:58:@59.4]
  wire [31:0] _T_189; // @[Cat.scala 30:58:@68.4]
  wire  _T_194; // @[Mux.scala 46:19:@71.4]
  wire [31:0] _T_195; // @[Mux.scala 46:16:@72.4]
  wire  _T_196; // @[Mux.scala 46:19:@73.4]
  wire [31:0] _T_197; // @[Mux.scala 46:16:@74.4]
  wire  _T_198; // @[Mux.scala 46:19:@75.4]
  wire [31:0] _T_199; // @[Mux.scala 46:16:@76.4]
  wire  _T_200; // @[Mux.scala 46:19:@77.4]
  wire  _T_202; // @[NV_NVDLA_GLB_CSB_reg.scala 137:51:@80.4]
  reg  _T_205; // @[Reg.scala 19:20:@81.4]
  reg [31:0] _RAND_0;
  wire  _GEN_0; // @[Reg.scala 20:19:@82.4]
  wire  _T_206; // @[NV_NVDLA_GLB_CSB_reg.scala 138:51:@86.4]
  reg  _T_209; // @[Reg.scala 19:20:@87.4]
  reg [31:0] _RAND_1;
  wire  _GEN_1; // @[Reg.scala 20:19:@88.4]
  wire  _T_210; // @[NV_NVDLA_GLB_CSB_reg.scala 139:51:@92.4]
  reg  _T_213; // @[Reg.scala 19:20:@93.4]
  reg [31:0] _RAND_2;
  wire  _GEN_2; // @[Reg.scala 20:19:@94.4]
  wire  _T_214; // @[NV_NVDLA_GLB_CSB_reg.scala 140:51:@98.4]
  reg  _T_217; // @[Reg.scala 19:20:@99.4]
  reg [31:0] _RAND_3;
  wire  _GEN_3; // @[Reg.scala 20:19:@100.4]
  wire  _T_218; // @[NV_NVDLA_GLB_CSB_reg.scala 141:55:@104.4]
  reg  _T_221; // @[Reg.scala 19:20:@105.4]
  reg [31:0] _RAND_4;
  wire  _GEN_4; // @[Reg.scala 20:19:@106.4]
  wire  _T_222; // @[NV_NVDLA_GLB_CSB_reg.scala 142:55:@110.4]
  reg  _T_225; // @[Reg.scala 19:20:@111.4]
  reg [31:0] _RAND_5;
  wire  _GEN_5; // @[Reg.scala 20:19:@112.4]
  wire  _T_226; // @[NV_NVDLA_GLB_CSB_reg.scala 143:54:@116.4]
  reg  _T_229; // @[Reg.scala 19:20:@117.4]
  reg [31:0] _RAND_6;
  wire  _GEN_6; // @[Reg.scala 20:19:@118.4]
  wire  _T_230; // @[NV_NVDLA_GLB_CSB_reg.scala 144:54:@122.4]
  reg  _T_233; // @[Reg.scala 19:20:@123.4]
  reg [31:0] _RAND_7;
  wire  _GEN_7; // @[Reg.scala 20:19:@124.4]
  wire  _T_234; // @[NV_NVDLA_GLB_CSB_reg.scala 145:50:@128.4]
  reg  _T_237; // @[Reg.scala 19:20:@129.4]
  reg [31:0] _RAND_8;
  wire  _GEN_8; // @[Reg.scala 20:19:@130.4]
  wire  _T_238; // @[NV_NVDLA_GLB_CSB_reg.scala 146:50:@134.4]
  reg  _T_241; // @[Reg.scala 19:20:@135.4]
  reg [31:0] _RAND_9;
  wire  _GEN_9; // @[Reg.scala 20:19:@136.4]
  wire  _T_242; // @[NV_NVDLA_GLB_CSB_reg.scala 147:50:@140.4]
  reg  _T_245; // @[Reg.scala 19:20:@141.4]
  reg [31:0] _RAND_10;
  wire  _GEN_10; // @[Reg.scala 20:19:@142.4]
  wire  _T_246; // @[NV_NVDLA_GLB_CSB_reg.scala 148:50:@146.4]
  reg  _T_249; // @[Reg.scala 19:20:@147.4]
  reg [31:0] _RAND_11;
  wire  _GEN_11; // @[Reg.scala 20:19:@148.4]
  wire  _T_250; // @[NV_NVDLA_GLB_CSB_reg.scala 149:52:@152.4]
  reg  _T_253; // @[Reg.scala 19:20:@153.4]
  reg [31:0] _RAND_12;
  wire  _GEN_12; // @[Reg.scala 20:19:@154.4]
  wire  _T_254; // @[NV_NVDLA_GLB_CSB_reg.scala 150:52:@158.4]
  reg  _T_257; // @[Reg.scala 19:20:@159.4]
  reg [31:0] _RAND_13;
  wire  _GEN_13; // @[Reg.scala 20:19:@160.4]
  wire  _T_258; // @[NV_NVDLA_GLB_CSB_reg.scala 151:50:@164.4]
  reg  _T_261; // @[Reg.scala 19:20:@165.4]
  reg [31:0] _RAND_14;
  wire  _GEN_14; // @[Reg.scala 20:19:@166.4]
  wire  _T_262; // @[NV_NVDLA_GLB_CSB_reg.scala 152:50:@170.4]
  reg  _T_265; // @[Reg.scala 19:20:@171.4]
  reg [31:0] _RAND_15;
  wire  _GEN_15; // @[Reg.scala 20:19:@172.4]
  assign _GEN_16 = {{20'd0}, io_reg_offset}; // @[NV_NVDLA_GLB_CSB_reg.scala 95:55:@8.4]
  assign _T_116 = _GEN_16 == 32'h4; // @[NV_NVDLA_GLB_CSB_reg.scala 95:55:@8.4]
  assign _T_117 = _T_116 & io_reg_wr_en; // @[NV_NVDLA_GLB_CSB_reg.scala 95:77:@9.4]
  assign _T_119 = _GEN_16 == 32'h8; // @[NV_NVDLA_GLB_CSB_reg.scala 96:54:@10.4]
  assign _T_122 = _GEN_16 == 32'hc; // @[NV_NVDLA_GLB_CSB_reg.scala 97:57:@12.4]
  assign _T_140 = {io_rubik_done_mask0,io_bdma_done_mask1,io_bdma_done_mask0,io_pdp_done_mask1,io_pdp_done_mask0,io_cdp_done_mask1,io_cdp_done_mask0,io_sdp_done_mask1,io_sdp_done_mask0}; // @[Cat.scala 30:58:@25.4]
  assign _T_149 = {10'h0,io_cacc_done_mask1,io_cacc_done_mask0,io_cdma_wt_done_mask1,io_cdma_wt_done_mask0,io_cdma_dat_done_mask1,io_cdma_dat_done_mask0,6'h0,io_rubik_done_mask1,_T_140}; // @[Cat.scala 30:58:@34.4]
  assign _T_180 = {3'h0,io_pdp_done_status1,io_pdp_done_status0,io_cdp_done_status1,io_cdp_done_status0,io_sdp_done_status1,io_sdp_done_status0}; // @[Cat.scala 30:58:@59.4]
  assign _T_189 = {10'h0,io_cacc_done_status1,io_cacc_done_status0,io_cdma_wt_done_status1,io_cdma_wt_done_status0,io_cdma_dat_done_status1,io_cdma_dat_done_status0,7'h0,_T_180}; // @[Cat.scala 30:58:@68.4]
  assign _T_194 = 32'h0 == _GEN_16; // @[Mux.scala 46:19:@71.4]
  assign _T_195 = _T_194 ? 32'h303031 : 32'h0; // @[Mux.scala 46:16:@72.4]
  assign _T_196 = 32'hc == _GEN_16; // @[Mux.scala 46:19:@73.4]
  assign _T_197 = _T_196 ? _T_189 : _T_195; // @[Mux.scala 46:16:@74.4]
  assign _T_198 = 32'h8 == _GEN_16; // @[Mux.scala 46:19:@75.4]
  assign _T_199 = _T_198 ? 32'h0 : _T_197; // @[Mux.scala 46:16:@76.4]
  assign _T_200 = 32'h4 == _GEN_16; // @[Mux.scala 46:19:@77.4]
  assign _T_202 = io_reg_wr_data[6]; // @[NV_NVDLA_GLB_CSB_reg.scala 137:51:@80.4]
  assign _GEN_0 = _T_117 ? _T_202 : _T_205; // @[Reg.scala 20:19:@82.4]
  assign _T_206 = io_reg_wr_data[7]; // @[NV_NVDLA_GLB_CSB_reg.scala 138:51:@86.4]
  assign _GEN_1 = _T_117 ? _T_206 : _T_209; // @[Reg.scala 20:19:@88.4]
  assign _T_210 = io_reg_wr_data[20]; // @[NV_NVDLA_GLB_CSB_reg.scala 139:51:@92.4]
  assign _GEN_2 = _T_117 ? _T_210 : _T_213; // @[Reg.scala 20:19:@94.4]
  assign _T_214 = io_reg_wr_data[21]; // @[NV_NVDLA_GLB_CSB_reg.scala 140:51:@98.4]
  assign _GEN_3 = _T_117 ? _T_214 : _T_217; // @[Reg.scala 20:19:@100.4]
  assign _T_218 = io_reg_wr_data[16]; // @[NV_NVDLA_GLB_CSB_reg.scala 141:55:@104.4]
  assign _GEN_4 = _T_117 ? _T_218 : _T_221; // @[Reg.scala 20:19:@106.4]
  assign _T_222 = io_reg_wr_data[17]; // @[NV_NVDLA_GLB_CSB_reg.scala 142:55:@110.4]
  assign _GEN_5 = _T_117 ? _T_222 : _T_225; // @[Reg.scala 20:19:@112.4]
  assign _T_226 = io_reg_wr_data[18]; // @[NV_NVDLA_GLB_CSB_reg.scala 143:54:@116.4]
  assign _GEN_6 = _T_117 ? _T_226 : _T_229; // @[Reg.scala 20:19:@118.4]
  assign _T_230 = io_reg_wr_data[19]; // @[NV_NVDLA_GLB_CSB_reg.scala 144:54:@122.4]
  assign _GEN_7 = _T_117 ? _T_230 : _T_233; // @[Reg.scala 20:19:@124.4]
  assign _T_234 = io_reg_wr_data[2]; // @[NV_NVDLA_GLB_CSB_reg.scala 145:50:@128.4]
  assign _GEN_8 = _T_117 ? _T_234 : _T_237; // @[Reg.scala 20:19:@130.4]
  assign _T_238 = io_reg_wr_data[3]; // @[NV_NVDLA_GLB_CSB_reg.scala 146:50:@134.4]
  assign _GEN_9 = _T_117 ? _T_238 : _T_241; // @[Reg.scala 20:19:@136.4]
  assign _T_242 = io_reg_wr_data[4]; // @[NV_NVDLA_GLB_CSB_reg.scala 147:50:@140.4]
  assign _GEN_10 = _T_117 ? _T_242 : _T_245; // @[Reg.scala 20:19:@142.4]
  assign _T_246 = io_reg_wr_data[5]; // @[NV_NVDLA_GLB_CSB_reg.scala 148:50:@146.4]
  assign _GEN_11 = _T_117 ? _T_246 : _T_249; // @[Reg.scala 20:19:@148.4]
  assign _T_250 = io_reg_wr_data[8]; // @[NV_NVDLA_GLB_CSB_reg.scala 149:52:@152.4]
  assign _GEN_12 = _T_117 ? _T_250 : _T_253; // @[Reg.scala 20:19:@154.4]
  assign _T_254 = io_reg_wr_data[9]; // @[NV_NVDLA_GLB_CSB_reg.scala 150:52:@158.4]
  assign _GEN_13 = _T_117 ? _T_254 : _T_257; // @[Reg.scala 20:19:@160.4]
  assign _T_258 = io_reg_wr_data[0]; // @[NV_NVDLA_GLB_CSB_reg.scala 151:50:@164.4]
  assign _GEN_14 = _T_117 ? _T_258 : _T_261; // @[Reg.scala 20:19:@166.4]
  assign _T_262 = io_reg_wr_data[1]; // @[NV_NVDLA_GLB_CSB_reg.scala 152:50:@170.4]
  assign _GEN_15 = _T_117 ? _T_262 : _T_265; // @[Reg.scala 20:19:@172.4]
  assign io_reg_rd_data = _T_200 ? _T_149 : _T_199; // @[NV_NVDLA_GLB_CSB_reg.scala 108:20:@79.4]
  assign io_bdma_done_mask0 = _T_205; // @[NV_NVDLA_GLB_CSB_reg.scala 137:24:@85.4]
  assign io_bdma_done_mask1 = _T_209; // @[NV_NVDLA_GLB_CSB_reg.scala 138:24:@91.4]
  assign io_cacc_done_mask0 = _T_213; // @[NV_NVDLA_GLB_CSB_reg.scala 139:24:@97.4]
  assign io_cacc_done_mask1 = _T_217; // @[NV_NVDLA_GLB_CSB_reg.scala 140:24:@103.4]
  assign io_cdma_dat_done_mask0 = _T_221; // @[NV_NVDLA_GLB_CSB_reg.scala 141:28:@109.4]
  assign io_cdma_dat_done_mask1 = _T_225; // @[NV_NVDLA_GLB_CSB_reg.scala 142:28:@115.4]
  assign io_cdma_wt_done_mask0 = _T_229; // @[NV_NVDLA_GLB_CSB_reg.scala 143:27:@121.4]
  assign io_cdma_wt_done_mask1 = _T_233; // @[NV_NVDLA_GLB_CSB_reg.scala 144:27:@127.4]
  assign io_cdp_done_mask0 = _T_237; // @[NV_NVDLA_GLB_CSB_reg.scala 145:23:@133.4]
  assign io_cdp_done_mask1 = _T_241; // @[NV_NVDLA_GLB_CSB_reg.scala 146:23:@139.4]
  assign io_pdp_done_mask0 = _T_245; // @[NV_NVDLA_GLB_CSB_reg.scala 147:23:@145.4]
  assign io_pdp_done_mask1 = _T_249; // @[NV_NVDLA_GLB_CSB_reg.scala 148:23:@151.4]
  assign io_rubik_done_mask0 = _T_253; // @[NV_NVDLA_GLB_CSB_reg.scala 149:25:@157.4]
  assign io_rubik_done_mask1 = _T_257; // @[NV_NVDLA_GLB_CSB_reg.scala 150:25:@163.4]
  assign io_sdp_done_mask0 = _T_261; // @[NV_NVDLA_GLB_CSB_reg.scala 151:23:@169.4]
  assign io_sdp_done_mask1 = _T_265; // @[NV_NVDLA_GLB_CSB_reg.scala 152:23:@175.4]
  assign io_sdp_done_set0_trigger = _T_119 & io_reg_wr_en; // @[NV_NVDLA_GLB_CSB_reg.scala 103:30:@16.4]
  assign io_sdp_done_status0_trigger = _T_122 & io_reg_wr_en; // @[NV_NVDLA_GLB_CSB_reg.scala 104:33:@17.4]
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
  _T_205 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_209 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_213 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_217 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_221 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_225 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_229 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_233 = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  _T_237 = _RAND_8[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  _T_241 = _RAND_9[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  _T_245 = _RAND_10[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  _T_249 = _RAND_11[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  _T_253 = _RAND_12[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  _T_257 = _RAND_13[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  _T_261 = _RAND_14[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_15 = {1{`RANDOM}};
  _T_265 = _RAND_15[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_205 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_205 <= _T_202;
      end
    end
    if (reset) begin
      _T_209 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_209 <= _T_206;
      end
    end
    if (reset) begin
      _T_213 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_213 <= _T_210;
      end
    end
    if (reset) begin
      _T_217 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_217 <= _T_214;
      end
    end
    if (reset) begin
      _T_221 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_221 <= _T_218;
      end
    end
    if (reset) begin
      _T_225 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_225 <= _T_222;
      end
    end
    if (reset) begin
      _T_229 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_229 <= _T_226;
      end
    end
    if (reset) begin
      _T_233 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_233 <= _T_230;
      end
    end
    if (reset) begin
      _T_237 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_237 <= _T_234;
      end
    end
    if (reset) begin
      _T_241 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_241 <= _T_238;
      end
    end
    if (reset) begin
      _T_245 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_245 <= _T_242;
      end
    end
    if (reset) begin
      _T_249 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_249 <= _T_246;
      end
    end
    if (reset) begin
      _T_253 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_253 <= _T_250;
      end
    end
    if (reset) begin
      _T_257 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_257 <= _T_254;
      end
    end
    if (reset) begin
      _T_261 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_261 <= _T_258;
      end
    end
    if (reset) begin
      _T_265 <= 1'h0;
    end else begin
      if (_T_117) begin
        _T_265 <= _T_262;
      end
    end
  end
endmodule
module NV_NVDLA_GLB_csb( // @[:@177.2]
  input         io_nvdla_core_clk, // @[:@180.4]
  input         io_nvdla_core_rstn, // @[:@180.4]
  input         io_cdp_done_status0, // @[:@180.4]
  input         io_cdp_done_status1, // @[:@180.4]
  output        io_cdp_done_mask0, // @[:@180.4]
  output        io_cdp_done_mask1, // @[:@180.4]
  input         io_pdp_done_status0, // @[:@180.4]
  input         io_pdp_done_status1, // @[:@180.4]
  output        io_pdp_done_mask0, // @[:@180.4]
  output        io_pdp_done_mask1, // @[:@180.4]
  input         io_cacc_done_status0, // @[:@180.4]
  input         io_cacc_done_status1, // @[:@180.4]
  output        io_cacc_done_mask0, // @[:@180.4]
  output        io_cacc_done_mask1, // @[:@180.4]
  input         io_cdma_dat_done_status0, // @[:@180.4]
  input         io_cdma_dat_done_status1, // @[:@180.4]
  output        io_cdma_dat_done_mask0, // @[:@180.4]
  output        io_cdma_dat_done_mask1, // @[:@180.4]
  input         io_cdma_wt_done_status0, // @[:@180.4]
  input         io_cdma_wt_done_status1, // @[:@180.4]
  output        io_cdma_wt_done_mask0, // @[:@180.4]
  output        io_cdma_wt_done_mask1, // @[:@180.4]
  input         io_csb2glb_req_valid, // @[:@180.4]
  input  [62:0] io_csb2glb_req_bits, // @[:@180.4]
  output        io_csb2glb_resp_valid, // @[:@180.4]
  output [33:0] io_csb2glb_resp_bits, // @[:@180.4]
  input         io_sdp_done_status0, // @[:@180.4]
  input         io_sdp_done_status1, // @[:@180.4]
  output        io_sdp_done_mask0, // @[:@180.4]
  output        io_sdp_done_mask1, // @[:@180.4]
  output        io_sdp_done_set0_trigger, // @[:@180.4]
  output        io_sdp_done_status0_trigger, // @[:@180.4]
  output [31:0] io_req_wdat // @[:@180.4]
);
  wire  NV_NVDLA_GLB_CSB_reg_reset; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_nvdla_core_clk; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire [31:0] NV_NVDLA_GLB_CSB_reg_io_reg_rd_data; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire [11:0] NV_NVDLA_GLB_CSB_reg_io_reg_offset; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire [31:0] NV_NVDLA_GLB_CSB_reg_io_reg_wr_data; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_reg_wr_en; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_bdma_done_mask0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_bdma_done_mask1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cacc_done_mask0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cacc_done_mask1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_mask0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_mask1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_mask0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_mask1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdp_done_mask0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdp_done_mask1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_pdp_done_mask0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_pdp_done_mask1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_rubik_done_mask0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_rubik_done_mask1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_sdp_done_mask0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_sdp_done_mask1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_sdp_done_set0_trigger; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_sdp_done_status0_trigger; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cacc_done_status0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cacc_done_status1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_status0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_status1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_status0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_status1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdp_done_status0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_cdp_done_status1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_pdp_done_status0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_pdp_done_status1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_sdp_done_status0; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  NV_NVDLA_GLB_CSB_reg_io_sdp_done_status1; // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
  wire  _T_80; // @[NV_NVDLA_GLB_csb.scala 92:38:@182.4]
  reg  _T_99; // @[NV_NVDLA_GLB_csb.scala 116:26:@183.4]
  reg [31:0] _RAND_0;
  reg [62:0] _T_102; // @[Reg.scala 19:20:@185.4]
  reg [63:0] _RAND_1;
  wire [62:0] _GEN_0; // @[Reg.scala 20:19:@186.4]
  wire  _T_105; // @[NV_NVDLA_GLB_csb.scala 125:29:@191.4]
  wire [21:0] _T_106; // @[NV_NVDLA_GLB_csb.scala 126:26:@192.4]
  wire  _T_109; // @[NV_NVDLA_GLB_csb.scala 129:27:@195.4]
  wire  _T_113; // @[NV_NVDLA_GLB_csb.scala 138:33:@199.4]
  wire  _T_114; // @[NV_NVDLA_GLB_csb.scala 138:31:@200.4]
  wire [31:0] _T_118; // @[Bitwise.scala 72:12:@202.4]
  wire [31:0] _T_112; // @[NV_NVDLA_GLB_csb.scala 136:27:@198.4 NV_NVDLA_GLB_csb.scala 174:17:@238.4]
  wire [31:0] _T_119; // @[NV_NVDLA_GLB_csb.scala 139:44:@203.4]
  wire  _T_121; // @[NV_NVDLA_GLB_csb.scala 142:31:@204.4]
  wire  _T_122; // @[NV_NVDLA_GLB_csb.scala 142:43:@205.4]
  wire [32:0] _T_125; // @[Cat.scala 30:58:@206.4]
  wire  _T_127; // @[NV_NVDLA_GLB_csb.scala 153:30:@208.4]
  wire [32:0] _T_136; // @[Bitwise.scala 72:12:@213.4]
  wire [32:0] _T_137; // @[NV_NVDLA_GLB_csb.scala 155:39:@214.4]
  wire [33:0] _T_144; // @[Cat.scala 30:58:@219.4]
  reg  _T_147; // @[NV_NVDLA_GLB_csb.scala 156:37:@220.4]
  reg [31:0] _RAND_2;
  reg [33:0] _T_150; // @[Reg.scala 19:20:@223.4]
  reg [63:0] _RAND_3;
  wire [33:0] _GEN_1; // @[Reg.scala 20:19:@224.4]
  wire [9:0] _T_151; // @[NV_NVDLA_GLB_csb.scala 163:34:@228.4]
  NV_NVDLA_GLB_CSB_reg NV_NVDLA_GLB_CSB_reg ( // @[NV_NVDLA_GLB_csb.scala 167:23:@231.4]
    .reset(NV_NVDLA_GLB_CSB_reg_reset),
    .io_nvdla_core_clk(NV_NVDLA_GLB_CSB_reg_io_nvdla_core_clk),
    .io_reg_rd_data(NV_NVDLA_GLB_CSB_reg_io_reg_rd_data),
    .io_reg_offset(NV_NVDLA_GLB_CSB_reg_io_reg_offset),
    .io_reg_wr_data(NV_NVDLA_GLB_CSB_reg_io_reg_wr_data),
    .io_reg_wr_en(NV_NVDLA_GLB_CSB_reg_io_reg_wr_en),
    .io_bdma_done_mask0(NV_NVDLA_GLB_CSB_reg_io_bdma_done_mask0),
    .io_bdma_done_mask1(NV_NVDLA_GLB_CSB_reg_io_bdma_done_mask1),
    .io_cacc_done_mask0(NV_NVDLA_GLB_CSB_reg_io_cacc_done_mask0),
    .io_cacc_done_mask1(NV_NVDLA_GLB_CSB_reg_io_cacc_done_mask1),
    .io_cdma_dat_done_mask0(NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_mask0),
    .io_cdma_dat_done_mask1(NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_mask1),
    .io_cdma_wt_done_mask0(NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_mask0),
    .io_cdma_wt_done_mask1(NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_mask1),
    .io_cdp_done_mask0(NV_NVDLA_GLB_CSB_reg_io_cdp_done_mask0),
    .io_cdp_done_mask1(NV_NVDLA_GLB_CSB_reg_io_cdp_done_mask1),
    .io_pdp_done_mask0(NV_NVDLA_GLB_CSB_reg_io_pdp_done_mask0),
    .io_pdp_done_mask1(NV_NVDLA_GLB_CSB_reg_io_pdp_done_mask1),
    .io_rubik_done_mask0(NV_NVDLA_GLB_CSB_reg_io_rubik_done_mask0),
    .io_rubik_done_mask1(NV_NVDLA_GLB_CSB_reg_io_rubik_done_mask1),
    .io_sdp_done_mask0(NV_NVDLA_GLB_CSB_reg_io_sdp_done_mask0),
    .io_sdp_done_mask1(NV_NVDLA_GLB_CSB_reg_io_sdp_done_mask1),
    .io_sdp_done_set0_trigger(NV_NVDLA_GLB_CSB_reg_io_sdp_done_set0_trigger),
    .io_sdp_done_status0_trigger(NV_NVDLA_GLB_CSB_reg_io_sdp_done_status0_trigger),
    .io_cacc_done_status0(NV_NVDLA_GLB_CSB_reg_io_cacc_done_status0),
    .io_cacc_done_status1(NV_NVDLA_GLB_CSB_reg_io_cacc_done_status1),
    .io_cdma_dat_done_status0(NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_status0),
    .io_cdma_dat_done_status1(NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_status1),
    .io_cdma_wt_done_status0(NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_status0),
    .io_cdma_wt_done_status1(NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_status1),
    .io_cdp_done_status0(NV_NVDLA_GLB_CSB_reg_io_cdp_done_status0),
    .io_cdp_done_status1(NV_NVDLA_GLB_CSB_reg_io_cdp_done_status1),
    .io_pdp_done_status0(NV_NVDLA_GLB_CSB_reg_io_pdp_done_status0),
    .io_pdp_done_status1(NV_NVDLA_GLB_CSB_reg_io_pdp_done_status1),
    .io_sdp_done_status0(NV_NVDLA_GLB_CSB_reg_io_sdp_done_status0),
    .io_sdp_done_status1(NV_NVDLA_GLB_CSB_reg_io_sdp_done_status1)
  );
  assign _T_80 = io_nvdla_core_rstn == 1'h0; // @[NV_NVDLA_GLB_csb.scala 92:38:@182.4]
  assign _GEN_0 = io_csb2glb_req_valid ? io_csb2glb_req_bits : _T_102; // @[Reg.scala 20:19:@186.4]
  assign _T_105 = _T_102[55]; // @[NV_NVDLA_GLB_csb.scala 125:29:@191.4]
  assign _T_106 = _T_102[21:0]; // @[NV_NVDLA_GLB_csb.scala 126:26:@192.4]
  assign _T_109 = _T_102[54]; // @[NV_NVDLA_GLB_csb.scala 129:27:@195.4]
  assign _T_113 = ~ _T_109; // @[NV_NVDLA_GLB_csb.scala 138:33:@199.4]
  assign _T_114 = _T_99 & _T_113; // @[NV_NVDLA_GLB_csb.scala 138:31:@200.4]
  assign _T_118 = _T_114 ? 32'hffffffff : 32'h0; // @[Bitwise.scala 72:12:@202.4]
  assign _T_112 = NV_NVDLA_GLB_CSB_reg_io_reg_rd_data; // @[NV_NVDLA_GLB_csb.scala 136:27:@198.4 NV_NVDLA_GLB_csb.scala 174:17:@238.4]
  assign _T_119 = _T_118 & _T_112; // @[NV_NVDLA_GLB_csb.scala 139:44:@203.4]
  assign _T_121 = _T_99 & _T_109; // @[NV_NVDLA_GLB_csb.scala 142:31:@204.4]
  assign _T_122 = _T_121 & _T_105; // @[NV_NVDLA_GLB_csb.scala 142:43:@205.4]
  assign _T_125 = {1'h0,_T_119}; // @[Cat.scala 30:58:@206.4]
  assign _T_127 = _T_114 | _T_122; // @[NV_NVDLA_GLB_csb.scala 153:30:@208.4]
  assign _T_136 = _T_114 ? 33'h1ffffffff : 33'h0; // @[Bitwise.scala 72:12:@213.4]
  assign _T_137 = _T_136 & _T_125; // @[NV_NVDLA_GLB_csb.scala 155:39:@214.4]
  assign _T_144 = {_T_122,_T_137}; // @[Cat.scala 30:58:@219.4]
  assign _GEN_1 = _T_127 ? _T_144 : _T_150; // @[Reg.scala 20:19:@224.4]
  assign _T_151 = _T_106[9:0]; // @[NV_NVDLA_GLB_csb.scala 163:34:@228.4]
  assign io_cdp_done_mask0 = NV_NVDLA_GLB_CSB_reg_io_cdp_done_mask0; // @[NV_NVDLA_GLB_csb.scala 195:31:@245.4]
  assign io_cdp_done_mask1 = NV_NVDLA_GLB_CSB_reg_io_cdp_done_mask1; // @[NV_NVDLA_GLB_csb.scala 196:31:@246.4]
  assign io_pdp_done_mask0 = NV_NVDLA_GLB_CSB_reg_io_pdp_done_mask0; // @[NV_NVDLA_GLB_csb.scala 209:31:@251.4]
  assign io_pdp_done_mask1 = NV_NVDLA_GLB_CSB_reg_io_pdp_done_mask1; // @[NV_NVDLA_GLB_csb.scala 210:31:@252.4]
  assign io_cacc_done_mask0 = NV_NVDLA_GLB_CSB_reg_io_cacc_done_mask0; // @[NV_NVDLA_GLB_csb.scala 238:24:@263.4]
  assign io_cacc_done_mask1 = NV_NVDLA_GLB_CSB_reg_io_cacc_done_mask1; // @[NV_NVDLA_GLB_csb.scala 239:24:@264.4]
  assign io_cdma_dat_done_mask0 = NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_mask0; // @[NV_NVDLA_GLB_csb.scala 246:28:@269.4]
  assign io_cdma_dat_done_mask1 = NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_mask1; // @[NV_NVDLA_GLB_csb.scala 247:28:@270.4]
  assign io_cdma_wt_done_mask0 = NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_mask0; // @[NV_NVDLA_GLB_csb.scala 254:27:@275.4]
  assign io_cdma_wt_done_mask1 = NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_mask1; // @[NV_NVDLA_GLB_csb.scala 255:27:@276.4]
  assign io_csb2glb_resp_valid = _T_147; // @[NV_NVDLA_GLB_csb.scala 156:27:@222.4]
  assign io_csb2glb_resp_bits = _T_150; // @[NV_NVDLA_GLB_csb.scala 161:30:@227.4]
  assign io_sdp_done_mask0 = NV_NVDLA_GLB_CSB_reg_io_sdp_done_mask0; // @[NV_NVDLA_GLB_csb.scala 262:23:@281.4]
  assign io_sdp_done_mask1 = NV_NVDLA_GLB_CSB_reg_io_sdp_done_mask1; // @[NV_NVDLA_GLB_csb.scala 263:23:@282.4]
  assign io_sdp_done_set0_trigger = NV_NVDLA_GLB_CSB_reg_io_sdp_done_set0_trigger; // @[NV_NVDLA_GLB_csb.scala 264:30:@283.4]
  assign io_sdp_done_status0_trigger = NV_NVDLA_GLB_CSB_reg_io_sdp_done_status0_trigger; // @[NV_NVDLA_GLB_csb.scala 265:33:@284.4]
  assign io_req_wdat = _T_102[53:22]; // @[NV_NVDLA_GLB_csb.scala 130:17:@197.4]
  assign NV_NVDLA_GLB_CSB_reg_reset = io_nvdla_core_rstn == 1'h0; // @[:@233.4]
  assign NV_NVDLA_GLB_CSB_reg_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_GLB_csb.scala 169:29:@234.4]
  assign NV_NVDLA_GLB_CSB_reg_io_reg_offset = {_T_151,2'h0}; // @[NV_NVDLA_GLB_csb.scala 171:25:@235.4]
  assign NV_NVDLA_GLB_CSB_reg_io_reg_wr_data = io_req_wdat; // @[NV_NVDLA_GLB_csb.scala 172:26:@236.4]
  assign NV_NVDLA_GLB_CSB_reg_io_reg_wr_en = _T_99 & _T_109; // @[NV_NVDLA_GLB_csb.scala 173:24:@237.4]
  assign NV_NVDLA_GLB_CSB_reg_io_cacc_done_status0 = io_cacc_done_status0; // @[NV_NVDLA_GLB_csb.scala 236:32:@261.4]
  assign NV_NVDLA_GLB_CSB_reg_io_cacc_done_status1 = io_cacc_done_status1; // @[NV_NVDLA_GLB_csb.scala 237:32:@262.4]
  assign NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_status0 = io_cdma_dat_done_status0; // @[NV_NVDLA_GLB_csb.scala 244:36:@267.4]
  assign NV_NVDLA_GLB_CSB_reg_io_cdma_dat_done_status1 = io_cdma_dat_done_status1; // @[NV_NVDLA_GLB_csb.scala 245:36:@268.4]
  assign NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_status0 = io_cdma_wt_done_status0; // @[NV_NVDLA_GLB_csb.scala 252:35:@273.4]
  assign NV_NVDLA_GLB_CSB_reg_io_cdma_wt_done_status1 = io_cdma_wt_done_status1; // @[NV_NVDLA_GLB_csb.scala 253:35:@274.4]
  assign NV_NVDLA_GLB_CSB_reg_io_cdp_done_status0 = io_cdp_done_status0; // @[NV_NVDLA_GLB_csb.scala 193:35:@243.4]
  assign NV_NVDLA_GLB_CSB_reg_io_cdp_done_status1 = io_cdp_done_status1; // @[NV_NVDLA_GLB_csb.scala 194:35:@244.4]
  assign NV_NVDLA_GLB_CSB_reg_io_pdp_done_status0 = io_pdp_done_status0; // @[NV_NVDLA_GLB_csb.scala 207:35:@249.4]
  assign NV_NVDLA_GLB_CSB_reg_io_pdp_done_status1 = io_pdp_done_status1; // @[NV_NVDLA_GLB_csb.scala 208:35:@250.4]
  assign NV_NVDLA_GLB_CSB_reg_io_sdp_done_status0 = io_sdp_done_status0; // @[NV_NVDLA_GLB_csb.scala 258:30:@277.4]
  assign NV_NVDLA_GLB_CSB_reg_io_sdp_done_status1 = io_sdp_done_status1; // @[NV_NVDLA_GLB_csb.scala 259:31:@278.4]
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
  _T_99 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {2{`RANDOM}};
  _T_102 = _RAND_1[62:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_147 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {2{`RANDOM}};
  _T_150 = _RAND_3[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (_T_80) begin
      _T_99 <= 1'h0;
    end else begin
      _T_99 <= io_csb2glb_req_valid;
    end
    if (_T_80) begin
      _T_102 <= 63'h0;
    end else begin
      if (io_csb2glb_req_valid) begin
        _T_102 <= io_csb2glb_req_bits;
      end
    end
    if (_T_80) begin
      _T_147 <= 1'h0;
    end else begin
      _T_147 <= _T_127;
    end
    if (_T_80) begin
      _T_150 <= 34'h0;
    end else begin
      if (_T_127) begin
        _T_150 <= _T_144;
      end
    end
  end
endmodule
module NV_NVDLA_GLB_ic( // @[:@286.2]
  input         io_nvdla_core_clk, // @[:@289.4]
  input         io_nvdla_falcon_clk, // @[:@289.4]
  input         io_nvdla_core_rstn, // @[:@289.4]
  input         io_nvdla_falcon_rstn, // @[:@289.4]
  input  [1:0]  io_cdp2glb_done_intr_pd, // @[:@289.4]
  input         io_cdp_done_mask0, // @[:@289.4]
  input         io_cdp_done_mask1, // @[:@289.4]
  output        io_cdp_done_status0, // @[:@289.4]
  output        io_cdp_done_status1, // @[:@289.4]
  input  [1:0]  io_pdp2glb_done_intr_pd, // @[:@289.4]
  input         io_pdp_done_mask0, // @[:@289.4]
  input         io_pdp_done_mask1, // @[:@289.4]
  output        io_pdp_done_status0, // @[:@289.4]
  output        io_pdp_done_status1, // @[:@289.4]
  input  [1:0]  io_cacc2glb_done_intr_pd, // @[:@289.4]
  input         io_cacc_done_mask0, // @[:@289.4]
  input         io_cacc_done_mask1, // @[:@289.4]
  output        io_cacc_done_status0, // @[:@289.4]
  output        io_cacc_done_status1, // @[:@289.4]
  input  [1:0]  io_cdma_dat2glb_done_intr_pd, // @[:@289.4]
  input         io_cdma_dat_done_mask0, // @[:@289.4]
  input         io_cdma_dat_done_mask1, // @[:@289.4]
  output        io_cdma_dat_done_status0, // @[:@289.4]
  output        io_cdma_dat_done_status1, // @[:@289.4]
  input  [1:0]  io_cdma_wt2glb_done_intr_pd, // @[:@289.4]
  input         io_cdma_wt_done_mask0, // @[:@289.4]
  input         io_cdma_wt_done_mask1, // @[:@289.4]
  output        io_cdma_wt_done_status0, // @[:@289.4]
  output        io_cdma_wt_done_status1, // @[:@289.4]
  input  [1:0]  io_sdp2glb_done_intr_pd, // @[:@289.4]
  input         io_sdp_done_mask0, // @[:@289.4]
  input         io_sdp_done_mask1, // @[:@289.4]
  output        io_sdp_done_status0, // @[:@289.4]
  output        io_sdp_done_status1, // @[:@289.4]
  input         io_sdp_done_set0_trigger, // @[:@289.4]
  input         io_sdp_done_status0_trigger, // @[:@289.4]
  input  [21:0] io_req_wdat, // @[:@289.4]
  output        io_core_intr // @[:@289.4]
);
  wire  _T_81; // @[NV_NVDLA_GLB_ic.scala 99:38:@291.4]
  wire [5:0] _T_82; // @[NV_NVDLA_GLB_ic.scala 101:71:@292.4]
  wire [9:0] _T_83; // @[NV_NVDLA_GLB_ic.scala 101:92:@293.4]
  wire [15:0] _T_84; // @[Cat.scala 30:58:@294.4]
  wire [15:0] _T_86; // @[NV_NVDLA_GLB_ic.scala 101:26:@295.4]
  wire [15:0] _T_91; // @[NV_NVDLA_GLB_ic.scala 102:23:@299.4]
  wire [15:0] _T_100; // @[Cat.scala 30:58:@306.4]
  reg [15:0] _T_103; // @[NV_NVDLA_GLB_ic.scala 109:30:@307.4]
  reg [31:0] _RAND_0;
  reg  _T_106; // @[NV_NVDLA_GLB_ic.scala 114:39:@309.4]
  reg [31:0] _RAND_1;
  wire  _T_107; // @[NV_NVDLA_GLB_ic.scala 116:42:@310.4]
  wire  _T_108; // @[NV_NVDLA_GLB_ic.scala 116:59:@311.4]
  wire  _T_109; // @[NV_NVDLA_GLB_ic.scala 116:46:@312.4]
  wire  _T_111; // @[NV_NVDLA_GLB_ic.scala 117:45:@313.4]
  wire  _T_113; // @[NV_NVDLA_GLB_ic.scala 117:33:@314.4]
  wire  _T_114; // @[NV_NVDLA_GLB_ic.scala 116:33:@315.4]
  reg  _T_117; // @[NV_NVDLA_GLB_ic.scala 124:39:@318.4]
  reg [31:0] _RAND_2;
  wire  _T_118; // @[NV_NVDLA_GLB_ic.scala 126:42:@319.4]
  wire  _T_119; // @[NV_NVDLA_GLB_ic.scala 126:59:@320.4]
  wire  _T_120; // @[NV_NVDLA_GLB_ic.scala 126:46:@321.4]
  wire  _T_122; // @[NV_NVDLA_GLB_ic.scala 127:45:@322.4]
  wire  _T_124; // @[NV_NVDLA_GLB_ic.scala 127:33:@323.4]
  wire  _T_125; // @[NV_NVDLA_GLB_ic.scala 126:33:@324.4]
  reg  _T_128; // @[NV_NVDLA_GLB_ic.scala 135:39:@327.4]
  reg [31:0] _RAND_3;
  wire  _T_129; // @[NV_NVDLA_GLB_ic.scala 137:42:@328.4]
  wire  _T_130; // @[NV_NVDLA_GLB_ic.scala 137:59:@329.4]
  wire  _T_131; // @[NV_NVDLA_GLB_ic.scala 137:46:@330.4]
  wire  _T_133; // @[NV_NVDLA_GLB_ic.scala 138:45:@331.4]
  wire  _T_135; // @[NV_NVDLA_GLB_ic.scala 138:33:@332.4]
  wire  _T_136; // @[NV_NVDLA_GLB_ic.scala 137:33:@333.4]
  reg  _T_139; // @[NV_NVDLA_GLB_ic.scala 145:39:@336.4]
  reg [31:0] _RAND_4;
  wire  _T_140; // @[NV_NVDLA_GLB_ic.scala 147:42:@337.4]
  wire  _T_141; // @[NV_NVDLA_GLB_ic.scala 147:59:@338.4]
  wire  _T_142; // @[NV_NVDLA_GLB_ic.scala 147:46:@339.4]
  wire  _T_144; // @[NV_NVDLA_GLB_ic.scala 148:45:@340.4]
  wire  _T_146; // @[NV_NVDLA_GLB_ic.scala 148:33:@341.4]
  wire  _T_147; // @[NV_NVDLA_GLB_ic.scala 147:33:@342.4]
  reg  _T_150; // @[NV_NVDLA_GLB_ic.scala 158:39:@345.4]
  reg [31:0] _RAND_5;
  wire  _T_151; // @[NV_NVDLA_GLB_ic.scala 160:42:@346.4]
  wire  _T_152; // @[NV_NVDLA_GLB_ic.scala 160:59:@347.4]
  wire  _T_153; // @[NV_NVDLA_GLB_ic.scala 160:46:@348.4]
  wire  _T_155; // @[NV_NVDLA_GLB_ic.scala 161:45:@349.4]
  wire  _T_157; // @[NV_NVDLA_GLB_ic.scala 161:33:@350.4]
  wire  _T_158; // @[NV_NVDLA_GLB_ic.scala 160:33:@351.4]
  reg  _T_161; // @[NV_NVDLA_GLB_ic.scala 168:39:@354.4]
  reg [31:0] _RAND_6;
  wire  _T_162; // @[NV_NVDLA_GLB_ic.scala 170:42:@355.4]
  wire  _T_163; // @[NV_NVDLA_GLB_ic.scala 170:59:@356.4]
  wire  _T_164; // @[NV_NVDLA_GLB_ic.scala 170:46:@357.4]
  wire  _T_166; // @[NV_NVDLA_GLB_ic.scala 171:45:@358.4]
  wire  _T_168; // @[NV_NVDLA_GLB_ic.scala 171:33:@359.4]
  wire  _T_169; // @[NV_NVDLA_GLB_ic.scala 170:33:@360.4]
  reg  _T_172; // @[NV_NVDLA_GLB_ic.scala 226:44:@363.4]
  reg [31:0] _RAND_7;
  wire  _T_173; // @[NV_NVDLA_GLB_ic.scala 228:47:@364.4]
  wire  _T_174; // @[NV_NVDLA_GLB_ic.scala 228:65:@365.4]
  wire  _T_175; // @[NV_NVDLA_GLB_ic.scala 228:52:@366.4]
  wire  _T_177; // @[NV_NVDLA_GLB_ic.scala 229:45:@367.4]
  wire  _T_179; // @[NV_NVDLA_GLB_ic.scala 229:33:@368.4]
  wire  _T_180; // @[NV_NVDLA_GLB_ic.scala 228:38:@369.4]
  reg  _T_183; // @[NV_NVDLA_GLB_ic.scala 236:44:@372.4]
  reg [31:0] _RAND_8;
  wire  _T_184; // @[NV_NVDLA_GLB_ic.scala 238:47:@373.4]
  wire  _T_185; // @[NV_NVDLA_GLB_ic.scala 238:65:@374.4]
  wire  _T_186; // @[NV_NVDLA_GLB_ic.scala 238:52:@375.4]
  wire  _T_188; // @[NV_NVDLA_GLB_ic.scala 239:45:@376.4]
  wire  _T_190; // @[NV_NVDLA_GLB_ic.scala 239:33:@377.4]
  wire  _T_191; // @[NV_NVDLA_GLB_ic.scala 238:38:@378.4]
  reg  _T_194; // @[NV_NVDLA_GLB_ic.scala 246:43:@381.4]
  reg [31:0] _RAND_9;
  wire  _T_195; // @[NV_NVDLA_GLB_ic.scala 248:46:@382.4]
  wire  _T_196; // @[NV_NVDLA_GLB_ic.scala 248:64:@383.4]
  wire  _T_197; // @[NV_NVDLA_GLB_ic.scala 248:51:@384.4]
  wire  _T_199; // @[NV_NVDLA_GLB_ic.scala 249:45:@385.4]
  wire  _T_201; // @[NV_NVDLA_GLB_ic.scala 249:33:@386.4]
  wire  _T_202; // @[NV_NVDLA_GLB_ic.scala 248:37:@387.4]
  reg  _T_205; // @[NV_NVDLA_GLB_ic.scala 256:43:@390.4]
  reg [31:0] _RAND_10;
  wire  _T_206; // @[NV_NVDLA_GLB_ic.scala 258:46:@391.4]
  wire  _T_207; // @[NV_NVDLA_GLB_ic.scala 258:64:@392.4]
  wire  _T_208; // @[NV_NVDLA_GLB_ic.scala 258:51:@393.4]
  wire  _T_210; // @[NV_NVDLA_GLB_ic.scala 259:45:@394.4]
  wire  _T_212; // @[NV_NVDLA_GLB_ic.scala 259:33:@395.4]
  wire  _T_213; // @[NV_NVDLA_GLB_ic.scala 258:37:@396.4]
  reg  _T_216; // @[NV_NVDLA_GLB_ic.scala 266:40:@399.4]
  reg [31:0] _RAND_11;
  wire  _T_217; // @[NV_NVDLA_GLB_ic.scala 268:43:@400.4]
  wire  _T_218; // @[NV_NVDLA_GLB_ic.scala 268:61:@401.4]
  wire  _T_219; // @[NV_NVDLA_GLB_ic.scala 268:48:@402.4]
  wire  _T_221; // @[NV_NVDLA_GLB_ic.scala 269:45:@403.4]
  wire  _T_223; // @[NV_NVDLA_GLB_ic.scala 269:33:@404.4]
  wire  _T_224; // @[NV_NVDLA_GLB_ic.scala 268:34:@405.4]
  reg  _T_227; // @[NV_NVDLA_GLB_ic.scala 276:40:@408.4]
  reg [31:0] _RAND_12;
  wire  _T_228; // @[NV_NVDLA_GLB_ic.scala 278:43:@409.4]
  wire  _T_229; // @[NV_NVDLA_GLB_ic.scala 278:61:@410.4]
  wire  _T_230; // @[NV_NVDLA_GLB_ic.scala 278:48:@411.4]
  wire  _T_232; // @[NV_NVDLA_GLB_ic.scala 279:45:@412.4]
  wire  _T_234; // @[NV_NVDLA_GLB_ic.scala 279:33:@413.4]
  wire  _T_235; // @[NV_NVDLA_GLB_ic.scala 278:34:@414.4]
  wire  _T_236; // @[NV_NVDLA_GLB_ic.scala 286:21:@417.4]
  wire  _T_237; // @[NV_NVDLA_GLB_ic.scala 286:44:@418.4]
  wire  _T_238; // @[NV_NVDLA_GLB_ic.scala 286:72:@419.4]
  wire  _T_239; // @[NV_NVDLA_GLB_ic.scala 286:95:@420.4]
  wire  _T_240; // @[NV_NVDLA_GLB_ic.scala 286:70:@421.4]
  wire  _T_241; // @[NV_NVDLA_GLB_ic.scala 290:21:@422.4]
  wire  _T_242; // @[NV_NVDLA_GLB_ic.scala 290:44:@423.4]
  wire  _T_243; // @[NV_NVDLA_GLB_ic.scala 290:72:@424.4]
  wire  _T_244; // @[NV_NVDLA_GLB_ic.scala 290:95:@425.4]
  wire  _T_245; // @[NV_NVDLA_GLB_ic.scala 290:70:@426.4]
  wire  _T_248; // @[NV_NVDLA_GLB_ic.scala 302:24:@427.4]
  wire  _T_249; // @[NV_NVDLA_GLB_ic.scala 302:43:@428.4]
  wire  _T_250; // @[NV_NVDLA_GLB_ic.scala 303:24:@429.4]
  wire  _T_251; // @[NV_NVDLA_GLB_ic.scala 303:43:@430.4]
  wire  _T_252; // @[NV_NVDLA_GLB_ic.scala 302:66:@431.4]
  wire  _T_253; // @[NV_NVDLA_GLB_ic.scala 303:66:@432.4]
  wire  _T_254; // @[NV_NVDLA_GLB_ic.scala 304:32:@433.4]
  wire  _T_257; // @[NV_NVDLA_GLB_ic.scala 308:23:@436.4]
  wire  _T_258; // @[NV_NVDLA_GLB_ic.scala 308:47:@437.4]
  wire  _T_259; // @[NV_NVDLA_GLB_ic.scala 307:34:@438.4]
  wire  _T_260; // @[NV_NVDLA_GLB_ic.scala 309:23:@439.4]
  wire  _T_261; // @[NV_NVDLA_GLB_ic.scala 309:47:@440.4]
  wire  _T_262; // @[NV_NVDLA_GLB_ic.scala 308:75:@441.4]
  wire  _T_263; // @[NV_NVDLA_GLB_ic.scala 310:23:@442.4]
  wire  _T_264; // @[NV_NVDLA_GLB_ic.scala 310:46:@443.4]
  wire  _T_265; // @[NV_NVDLA_GLB_ic.scala 309:75:@444.4]
  wire  _T_266; // @[NV_NVDLA_GLB_ic.scala 311:23:@445.4]
  wire  _T_267; // @[NV_NVDLA_GLB_ic.scala 311:46:@446.4]
  wire  _T_268; // @[NV_NVDLA_GLB_ic.scala 310:73:@447.4]
  wire  _T_269; // @[NV_NVDLA_GLB_ic.scala 312:23:@448.4]
  wire  _T_270; // @[NV_NVDLA_GLB_ic.scala 312:43:@449.4]
  wire  _T_271; // @[NV_NVDLA_GLB_ic.scala 311:73:@450.4]
  wire  _T_272; // @[NV_NVDLA_GLB_ic.scala 313:23:@451.4]
  wire  _T_273; // @[NV_NVDLA_GLB_ic.scala 313:43:@452.4]
  wire  _T_274; // @[NV_NVDLA_GLB_ic.scala 312:67:@453.4]
  reg  _T_277; // @[NV_NVDLA_GLB_ic.scala 315:30:@454.4]
  reg [31:0] _RAND_13;
  wire  _T_278; // @[NV_NVDLA_GLB_ic.scala 317:60:@456.4]
  reg  _T_282; // @[Reg.scala 19:20:@457.4]
  reg [31:0] _RAND_14;
  reg  _T_284; // @[Reg.scala 19:20:@461.4]
  reg [31:0] _RAND_15;
  reg  _T_286; // @[Reg.scala 19:20:@465.4]
  reg [31:0] _RAND_16;
  assign _T_81 = ~ io_nvdla_core_rstn; // @[NV_NVDLA_GLB_ic.scala 99:38:@291.4]
  assign _T_82 = io_req_wdat[21:16]; // @[NV_NVDLA_GLB_ic.scala 101:71:@292.4]
  assign _T_83 = io_req_wdat[9:0]; // @[NV_NVDLA_GLB_ic.scala 101:92:@293.4]
  assign _T_84 = {_T_82,_T_83}; // @[Cat.scala 30:58:@294.4]
  assign _T_86 = io_sdp_done_status0_trigger ? _T_84 : 16'h0; // @[NV_NVDLA_GLB_ic.scala 101:26:@295.4]
  assign _T_91 = io_sdp_done_set0_trigger ? _T_84 : 16'h0; // @[NV_NVDLA_GLB_ic.scala 102:23:@299.4]
  assign _T_100 = {io_cacc2glb_done_intr_pd,io_cdma_wt2glb_done_intr_pd,io_cdma_dat2glb_done_intr_pd,2'h0,2'h0,io_pdp2glb_done_intr_pd,io_cdp2glb_done_intr_pd,io_sdp2glb_done_intr_pd}; // @[Cat.scala 30:58:@306.4]
  assign _T_107 = _T_91[0]; // @[NV_NVDLA_GLB_ic.scala 116:42:@310.4]
  assign _T_108 = _T_103[0]; // @[NV_NVDLA_GLB_ic.scala 116:59:@311.4]
  assign _T_109 = _T_107 | _T_108; // @[NV_NVDLA_GLB_ic.scala 116:46:@312.4]
  assign _T_111 = _T_86[0]; // @[NV_NVDLA_GLB_ic.scala 117:45:@313.4]
  assign _T_113 = _T_111 ? 1'h0 : _T_106; // @[NV_NVDLA_GLB_ic.scala 117:33:@314.4]
  assign _T_114 = _T_109 ? 1'h1 : _T_113; // @[NV_NVDLA_GLB_ic.scala 116:33:@315.4]
  assign _T_118 = _T_91[1]; // @[NV_NVDLA_GLB_ic.scala 126:42:@319.4]
  assign _T_119 = _T_103[1]; // @[NV_NVDLA_GLB_ic.scala 126:59:@320.4]
  assign _T_120 = _T_118 | _T_119; // @[NV_NVDLA_GLB_ic.scala 126:46:@321.4]
  assign _T_122 = _T_86[1]; // @[NV_NVDLA_GLB_ic.scala 127:45:@322.4]
  assign _T_124 = _T_122 ? 1'h0 : _T_117; // @[NV_NVDLA_GLB_ic.scala 127:33:@323.4]
  assign _T_125 = _T_120 ? 1'h1 : _T_124; // @[NV_NVDLA_GLB_ic.scala 126:33:@324.4]
  assign _T_129 = _T_91[2]; // @[NV_NVDLA_GLB_ic.scala 137:42:@328.4]
  assign _T_130 = _T_103[2]; // @[NV_NVDLA_GLB_ic.scala 137:59:@329.4]
  assign _T_131 = _T_129 | _T_130; // @[NV_NVDLA_GLB_ic.scala 137:46:@330.4]
  assign _T_133 = _T_86[2]; // @[NV_NVDLA_GLB_ic.scala 138:45:@331.4]
  assign _T_135 = _T_133 ? 1'h0 : _T_128; // @[NV_NVDLA_GLB_ic.scala 138:33:@332.4]
  assign _T_136 = _T_131 ? 1'h1 : _T_135; // @[NV_NVDLA_GLB_ic.scala 137:33:@333.4]
  assign _T_140 = _T_91[3]; // @[NV_NVDLA_GLB_ic.scala 147:42:@337.4]
  assign _T_141 = _T_103[3]; // @[NV_NVDLA_GLB_ic.scala 147:59:@338.4]
  assign _T_142 = _T_140 | _T_141; // @[NV_NVDLA_GLB_ic.scala 147:46:@339.4]
  assign _T_144 = _T_86[3]; // @[NV_NVDLA_GLB_ic.scala 148:45:@340.4]
  assign _T_146 = _T_144 ? 1'h0 : _T_139; // @[NV_NVDLA_GLB_ic.scala 148:33:@341.4]
  assign _T_147 = _T_142 ? 1'h1 : _T_146; // @[NV_NVDLA_GLB_ic.scala 147:33:@342.4]
  assign _T_151 = _T_91[4]; // @[NV_NVDLA_GLB_ic.scala 160:42:@346.4]
  assign _T_152 = _T_103[4]; // @[NV_NVDLA_GLB_ic.scala 160:59:@347.4]
  assign _T_153 = _T_151 | _T_152; // @[NV_NVDLA_GLB_ic.scala 160:46:@348.4]
  assign _T_155 = _T_86[4]; // @[NV_NVDLA_GLB_ic.scala 161:45:@349.4]
  assign _T_157 = _T_155 ? 1'h0 : _T_150; // @[NV_NVDLA_GLB_ic.scala 161:33:@350.4]
  assign _T_158 = _T_153 ? 1'h1 : _T_157; // @[NV_NVDLA_GLB_ic.scala 160:33:@351.4]
  assign _T_162 = _T_91[5]; // @[NV_NVDLA_GLB_ic.scala 170:42:@355.4]
  assign _T_163 = _T_103[5]; // @[NV_NVDLA_GLB_ic.scala 170:59:@356.4]
  assign _T_164 = _T_162 | _T_163; // @[NV_NVDLA_GLB_ic.scala 170:46:@357.4]
  assign _T_166 = _T_86[5]; // @[NV_NVDLA_GLB_ic.scala 171:45:@358.4]
  assign _T_168 = _T_166 ? 1'h0 : _T_161; // @[NV_NVDLA_GLB_ic.scala 171:33:@359.4]
  assign _T_169 = _T_164 ? 1'h1 : _T_168; // @[NV_NVDLA_GLB_ic.scala 170:33:@360.4]
  assign _T_173 = _T_91[10]; // @[NV_NVDLA_GLB_ic.scala 228:47:@364.4]
  assign _T_174 = _T_103[10]; // @[NV_NVDLA_GLB_ic.scala 228:65:@365.4]
  assign _T_175 = _T_173 | _T_174; // @[NV_NVDLA_GLB_ic.scala 228:52:@366.4]
  assign _T_177 = _T_86[10]; // @[NV_NVDLA_GLB_ic.scala 229:45:@367.4]
  assign _T_179 = _T_177 ? 1'h0 : _T_172; // @[NV_NVDLA_GLB_ic.scala 229:33:@368.4]
  assign _T_180 = _T_175 ? 1'h1 : _T_179; // @[NV_NVDLA_GLB_ic.scala 228:38:@369.4]
  assign _T_184 = _T_91[11]; // @[NV_NVDLA_GLB_ic.scala 238:47:@373.4]
  assign _T_185 = _T_103[11]; // @[NV_NVDLA_GLB_ic.scala 238:65:@374.4]
  assign _T_186 = _T_184 | _T_185; // @[NV_NVDLA_GLB_ic.scala 238:52:@375.4]
  assign _T_188 = _T_86[11]; // @[NV_NVDLA_GLB_ic.scala 239:45:@376.4]
  assign _T_190 = _T_188 ? 1'h0 : _T_183; // @[NV_NVDLA_GLB_ic.scala 239:33:@377.4]
  assign _T_191 = _T_186 ? 1'h1 : _T_190; // @[NV_NVDLA_GLB_ic.scala 238:38:@378.4]
  assign _T_195 = _T_91[12]; // @[NV_NVDLA_GLB_ic.scala 248:46:@382.4]
  assign _T_196 = _T_103[12]; // @[NV_NVDLA_GLB_ic.scala 248:64:@383.4]
  assign _T_197 = _T_195 | _T_196; // @[NV_NVDLA_GLB_ic.scala 248:51:@384.4]
  assign _T_199 = _T_86[12]; // @[NV_NVDLA_GLB_ic.scala 249:45:@385.4]
  assign _T_201 = _T_199 ? 1'h0 : _T_194; // @[NV_NVDLA_GLB_ic.scala 249:33:@386.4]
  assign _T_202 = _T_197 ? 1'h1 : _T_201; // @[NV_NVDLA_GLB_ic.scala 248:37:@387.4]
  assign _T_206 = _T_91[13]; // @[NV_NVDLA_GLB_ic.scala 258:46:@391.4]
  assign _T_207 = _T_103[13]; // @[NV_NVDLA_GLB_ic.scala 258:64:@392.4]
  assign _T_208 = _T_206 | _T_207; // @[NV_NVDLA_GLB_ic.scala 258:51:@393.4]
  assign _T_210 = _T_86[13]; // @[NV_NVDLA_GLB_ic.scala 259:45:@394.4]
  assign _T_212 = _T_210 ? 1'h0 : _T_205; // @[NV_NVDLA_GLB_ic.scala 259:33:@395.4]
  assign _T_213 = _T_208 ? 1'h1 : _T_212; // @[NV_NVDLA_GLB_ic.scala 258:37:@396.4]
  assign _T_217 = _T_91[14]; // @[NV_NVDLA_GLB_ic.scala 268:43:@400.4]
  assign _T_218 = _T_103[14]; // @[NV_NVDLA_GLB_ic.scala 268:61:@401.4]
  assign _T_219 = _T_217 | _T_218; // @[NV_NVDLA_GLB_ic.scala 268:48:@402.4]
  assign _T_221 = _T_86[14]; // @[NV_NVDLA_GLB_ic.scala 269:45:@403.4]
  assign _T_223 = _T_221 ? 1'h0 : _T_216; // @[NV_NVDLA_GLB_ic.scala 269:33:@404.4]
  assign _T_224 = _T_219 ? 1'h1 : _T_223; // @[NV_NVDLA_GLB_ic.scala 268:34:@405.4]
  assign _T_228 = _T_91[15]; // @[NV_NVDLA_GLB_ic.scala 278:43:@409.4]
  assign _T_229 = _T_103[15]; // @[NV_NVDLA_GLB_ic.scala 278:61:@410.4]
  assign _T_230 = _T_228 | _T_229; // @[NV_NVDLA_GLB_ic.scala 278:48:@411.4]
  assign _T_232 = _T_86[15]; // @[NV_NVDLA_GLB_ic.scala 279:45:@412.4]
  assign _T_234 = _T_232 ? 1'h0 : _T_227; // @[NV_NVDLA_GLB_ic.scala 279:33:@413.4]
  assign _T_235 = _T_230 ? 1'h1 : _T_234; // @[NV_NVDLA_GLB_ic.scala 278:34:@414.4]
  assign _T_236 = ~ io_cdp_done_mask0; // @[NV_NVDLA_GLB_ic.scala 286:21:@417.4]
  assign _T_237 = _T_236 & io_cdp_done_status0; // @[NV_NVDLA_GLB_ic.scala 286:44:@418.4]
  assign _T_238 = ~ io_cdp_done_mask1; // @[NV_NVDLA_GLB_ic.scala 286:72:@419.4]
  assign _T_239 = _T_238 & io_cdp_done_status1; // @[NV_NVDLA_GLB_ic.scala 286:95:@420.4]
  assign _T_240 = _T_237 | _T_239; // @[NV_NVDLA_GLB_ic.scala 286:70:@421.4]
  assign _T_241 = ~ io_pdp_done_mask0; // @[NV_NVDLA_GLB_ic.scala 290:21:@422.4]
  assign _T_242 = _T_241 & io_pdp_done_status0; // @[NV_NVDLA_GLB_ic.scala 290:44:@423.4]
  assign _T_243 = ~ io_pdp_done_mask1; // @[NV_NVDLA_GLB_ic.scala 290:72:@424.4]
  assign _T_244 = _T_243 & io_pdp_done_status1; // @[NV_NVDLA_GLB_ic.scala 290:95:@425.4]
  assign _T_245 = _T_242 | _T_244; // @[NV_NVDLA_GLB_ic.scala 290:70:@426.4]
  assign _T_248 = ~ io_sdp_done_mask0; // @[NV_NVDLA_GLB_ic.scala 302:24:@427.4]
  assign _T_249 = _T_248 & io_sdp_done_status0; // @[NV_NVDLA_GLB_ic.scala 302:43:@428.4]
  assign _T_250 = ~ io_sdp_done_mask1; // @[NV_NVDLA_GLB_ic.scala 303:24:@429.4]
  assign _T_251 = _T_250 & io_sdp_done_status1; // @[NV_NVDLA_GLB_ic.scala 303:43:@430.4]
  assign _T_252 = _T_249 | _T_251; // @[NV_NVDLA_GLB_ic.scala 302:66:@431.4]
  assign _T_253 = _T_252 | _T_240; // @[NV_NVDLA_GLB_ic.scala 303:66:@432.4]
  assign _T_254 = _T_253 | _T_245; // @[NV_NVDLA_GLB_ic.scala 304:32:@433.4]
  assign _T_257 = ~ io_cdma_dat_done_mask0; // @[NV_NVDLA_GLB_ic.scala 308:23:@436.4]
  assign _T_258 = _T_257 & io_cdma_dat_done_status0; // @[NV_NVDLA_GLB_ic.scala 308:47:@437.4]
  assign _T_259 = _T_254 | _T_258; // @[NV_NVDLA_GLB_ic.scala 307:34:@438.4]
  assign _T_260 = ~ io_cdma_dat_done_mask1; // @[NV_NVDLA_GLB_ic.scala 309:23:@439.4]
  assign _T_261 = _T_260 & io_cdma_dat_done_status1; // @[NV_NVDLA_GLB_ic.scala 309:47:@440.4]
  assign _T_262 = _T_259 | _T_261; // @[NV_NVDLA_GLB_ic.scala 308:75:@441.4]
  assign _T_263 = ~ io_cdma_wt_done_mask0; // @[NV_NVDLA_GLB_ic.scala 310:23:@442.4]
  assign _T_264 = _T_263 & io_cdma_wt_done_status0; // @[NV_NVDLA_GLB_ic.scala 310:46:@443.4]
  assign _T_265 = _T_262 | _T_264; // @[NV_NVDLA_GLB_ic.scala 309:75:@444.4]
  assign _T_266 = ~ io_cdma_wt_done_mask1; // @[NV_NVDLA_GLB_ic.scala 311:23:@445.4]
  assign _T_267 = _T_266 & io_cdma_wt_done_status1; // @[NV_NVDLA_GLB_ic.scala 311:46:@446.4]
  assign _T_268 = _T_265 | _T_267; // @[NV_NVDLA_GLB_ic.scala 310:73:@447.4]
  assign _T_269 = ~ io_cacc_done_mask0; // @[NV_NVDLA_GLB_ic.scala 312:23:@448.4]
  assign _T_270 = _T_269 & io_cacc_done_status0; // @[NV_NVDLA_GLB_ic.scala 312:43:@449.4]
  assign _T_271 = _T_268 | _T_270; // @[NV_NVDLA_GLB_ic.scala 311:73:@450.4]
  assign _T_272 = ~ io_cacc_done_mask1; // @[NV_NVDLA_GLB_ic.scala 313:23:@451.4]
  assign _T_273 = _T_272 & io_cacc_done_status1; // @[NV_NVDLA_GLB_ic.scala 313:43:@452.4]
  assign _T_274 = _T_271 | _T_273; // @[NV_NVDLA_GLB_ic.scala 312:67:@453.4]
  assign _T_278 = ~ io_nvdla_falcon_rstn; // @[NV_NVDLA_GLB_ic.scala 317:60:@456.4]
  assign io_cdp_done_status0 = _T_128; // @[NV_NVDLA_GLB_ic.scala 142:29:@335.4]
  assign io_cdp_done_status1 = _T_139; // @[NV_NVDLA_GLB_ic.scala 152:29:@344.4]
  assign io_pdp_done_status0 = _T_150; // @[NV_NVDLA_GLB_ic.scala 165:29:@353.4]
  assign io_pdp_done_status1 = _T_161; // @[NV_NVDLA_GLB_ic.scala 175:29:@362.4]
  assign io_cacc_done_status0 = _T_216; // @[NV_NVDLA_GLB_ic.scala 273:26:@407.4]
  assign io_cacc_done_status1 = _T_227; // @[NV_NVDLA_GLB_ic.scala 283:26:@416.4]
  assign io_cdma_dat_done_status0 = _T_172; // @[NV_NVDLA_GLB_ic.scala 233:30:@371.4]
  assign io_cdma_dat_done_status1 = _T_183; // @[NV_NVDLA_GLB_ic.scala 243:30:@380.4]
  assign io_cdma_wt_done_status0 = _T_194; // @[NV_NVDLA_GLB_ic.scala 253:29:@389.4]
  assign io_cdma_wt_done_status1 = _T_205; // @[NV_NVDLA_GLB_ic.scala 263:29:@398.4]
  assign io_sdp_done_status0 = _T_106; // @[NV_NVDLA_GLB_ic.scala 121:25:@317.4]
  assign io_sdp_done_status1 = _T_117; // @[NV_NVDLA_GLB_ic.scala 131:25:@326.4]
  assign io_core_intr = _T_286; // @[NV_NVDLA_GLB_ic.scala 317:18:@469.4]
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
  _T_103 = _RAND_0[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_106 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_117 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_128 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_139 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_150 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_161 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_172 = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  _T_183 = _RAND_8[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  _T_194 = _RAND_9[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  _T_205 = _RAND_10[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  _T_216 = _RAND_11[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  _T_227 = _RAND_12[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  _T_277 = _RAND_13[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  _T_282 = _RAND_14[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_15 = {1{`RANDOM}};
  _T_284 = _RAND_15[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_16 = {1{`RANDOM}};
  _T_286 = _RAND_16[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (_T_81) begin
      _T_103 <= 16'h0;
    end else begin
      _T_103 <= _T_100;
    end
    if (_T_81) begin
      _T_106 <= 1'h0;
    end else begin
      if (_T_109) begin
        _T_106 <= 1'h1;
      end else begin
        if (_T_111) begin
          _T_106 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_117 <= 1'h0;
    end else begin
      if (_T_120) begin
        _T_117 <= 1'h1;
      end else begin
        if (_T_122) begin
          _T_117 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_128 <= 1'h0;
    end else begin
      if (_T_131) begin
        _T_128 <= 1'h1;
      end else begin
        if (_T_133) begin
          _T_128 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_139 <= 1'h0;
    end else begin
      if (_T_142) begin
        _T_139 <= 1'h1;
      end else begin
        if (_T_144) begin
          _T_139 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_150 <= 1'h0;
    end else begin
      if (_T_153) begin
        _T_150 <= 1'h1;
      end else begin
        if (_T_155) begin
          _T_150 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_161 <= 1'h0;
    end else begin
      if (_T_164) begin
        _T_161 <= 1'h1;
      end else begin
        if (_T_166) begin
          _T_161 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_172 <= 1'h0;
    end else begin
      if (_T_175) begin
        _T_172 <= 1'h1;
      end else begin
        if (_T_177) begin
          _T_172 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_183 <= 1'h0;
    end else begin
      if (_T_186) begin
        _T_183 <= 1'h1;
      end else begin
        if (_T_188) begin
          _T_183 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_194 <= 1'h0;
    end else begin
      if (_T_197) begin
        _T_194 <= 1'h1;
      end else begin
        if (_T_199) begin
          _T_194 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_205 <= 1'h0;
    end else begin
      if (_T_208) begin
        _T_205 <= 1'h1;
      end else begin
        if (_T_210) begin
          _T_205 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_216 <= 1'h0;
    end else begin
      if (_T_219) begin
        _T_216 <= 1'h1;
      end else begin
        if (_T_221) begin
          _T_216 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_227 <= 1'h0;
    end else begin
      if (_T_230) begin
        _T_227 <= 1'h1;
      end else begin
        if (_T_232) begin
          _T_227 <= 1'h0;
        end
      end
    end
    if (_T_81) begin
      _T_277 <= 1'h0;
    end else begin
      _T_277 <= _T_274;
    end
  end
  always @(posedge io_nvdla_falcon_clk) begin
    if (_T_278) begin
      _T_282 <= 1'h0;
    end else begin
      _T_282 <= _T_277;
    end
    if (_T_278) begin
      _T_284 <= 1'h0;
    end else begin
      _T_284 <= _T_282;
    end
    if (_T_278) begin
      _T_286 <= 1'h0;
    end else begin
      _T_286 <= _T_284;
    end
  end
endmodule
module NV_NVDLA_glb( // @[:@471.2]
  input         clock, // @[:@472.4]
  input         reset, // @[:@473.4]
  input         io_nvdla_core_clk, // @[:@474.4]
  input         io_nvdla_falcon_clk, // @[:@474.4]
  input         io_nvdla_core_rstn, // @[:@474.4]
  input         io_nvdla_falcon_rstn, // @[:@474.4]
  output        io_csb2glb_req_ready, // @[:@474.4]
  input         io_csb2glb_req_valid, // @[:@474.4]
  input  [62:0] io_csb2glb_req_bits, // @[:@474.4]
  output        io_csb2glb_resp_valid, // @[:@474.4]
  output [33:0] io_csb2glb_resp_bits, // @[:@474.4]
  output        io_core_intr, // @[:@474.4]
  input  [1:0]  io_cdp2glb_done_intr_pd, // @[:@474.4]
  input  [1:0]  io_pdp2glb_done_intr_pd, // @[:@474.4]
  input  [1:0]  io_cacc2glb_done_intr_pd, // @[:@474.4]
  input  [1:0]  io_cdma_dat2glb_done_intr_pd, // @[:@474.4]
  input  [1:0]  io_cdma_wt2glb_done_intr_pd, // @[:@474.4]
  input  [1:0]  io_sdp2glb_done_intr_pd // @[:@474.4]
);
  wire  u_csb_io_nvdla_core_clk; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_nvdla_core_rstn; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdp_done_status0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdp_done_status1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdp_done_mask0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdp_done_mask1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_pdp_done_status0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_pdp_done_status1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_pdp_done_mask0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_pdp_done_mask1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cacc_done_status0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cacc_done_status1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cacc_done_mask0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cacc_done_mask1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdma_dat_done_status0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdma_dat_done_status1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdma_dat_done_mask0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdma_dat_done_mask1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdma_wt_done_status0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdma_wt_done_status1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdma_wt_done_mask0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_cdma_wt_done_mask1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_csb2glb_req_valid; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire [62:0] u_csb_io_csb2glb_req_bits; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_csb2glb_resp_valid; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire [33:0] u_csb_io_csb2glb_resp_bits; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_sdp_done_status0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_sdp_done_status1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_sdp_done_mask0; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_sdp_done_mask1; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_sdp_done_set0_trigger; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_csb_io_sdp_done_status0_trigger; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire [31:0] u_csb_io_req_wdat; // @[NV_NVDLA_glb.scala 59:23:@476.4]
  wire  u_ic_io_nvdla_core_clk; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_nvdla_falcon_clk; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_nvdla_core_rstn; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_nvdla_falcon_rstn; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire [1:0] u_ic_io_cdp2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdp_done_mask0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdp_done_mask1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdp_done_status0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdp_done_status1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire [1:0] u_ic_io_pdp2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_pdp_done_mask0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_pdp_done_mask1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_pdp_done_status0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_pdp_done_status1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire [1:0] u_ic_io_cacc2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cacc_done_mask0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cacc_done_mask1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cacc_done_status0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cacc_done_status1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire [1:0] u_ic_io_cdma_dat2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdma_dat_done_mask0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdma_dat_done_mask1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdma_dat_done_status0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdma_dat_done_status1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire [1:0] u_ic_io_cdma_wt2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdma_wt_done_mask0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdma_wt_done_mask1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdma_wt_done_status0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_cdma_wt_done_status1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire [1:0] u_ic_io_sdp2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_sdp_done_mask0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_sdp_done_mask1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_sdp_done_status0; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_sdp_done_status1; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_sdp_done_set0_trigger; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_sdp_done_status0_trigger; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire [21:0] u_ic_io_req_wdat; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  wire  u_ic_io_core_intr; // @[NV_NVDLA_glb.scala 60:22:@479.4]
  NV_NVDLA_GLB_csb u_csb ( // @[NV_NVDLA_glb.scala 59:23:@476.4]
    .io_nvdla_core_clk(u_csb_io_nvdla_core_clk),
    .io_nvdla_core_rstn(u_csb_io_nvdla_core_rstn),
    .io_cdp_done_status0(u_csb_io_cdp_done_status0),
    .io_cdp_done_status1(u_csb_io_cdp_done_status1),
    .io_cdp_done_mask0(u_csb_io_cdp_done_mask0),
    .io_cdp_done_mask1(u_csb_io_cdp_done_mask1),
    .io_pdp_done_status0(u_csb_io_pdp_done_status0),
    .io_pdp_done_status1(u_csb_io_pdp_done_status1),
    .io_pdp_done_mask0(u_csb_io_pdp_done_mask0),
    .io_pdp_done_mask1(u_csb_io_pdp_done_mask1),
    .io_cacc_done_status0(u_csb_io_cacc_done_status0),
    .io_cacc_done_status1(u_csb_io_cacc_done_status1),
    .io_cacc_done_mask0(u_csb_io_cacc_done_mask0),
    .io_cacc_done_mask1(u_csb_io_cacc_done_mask1),
    .io_cdma_dat_done_status0(u_csb_io_cdma_dat_done_status0),
    .io_cdma_dat_done_status1(u_csb_io_cdma_dat_done_status1),
    .io_cdma_dat_done_mask0(u_csb_io_cdma_dat_done_mask0),
    .io_cdma_dat_done_mask1(u_csb_io_cdma_dat_done_mask1),
    .io_cdma_wt_done_status0(u_csb_io_cdma_wt_done_status0),
    .io_cdma_wt_done_status1(u_csb_io_cdma_wt_done_status1),
    .io_cdma_wt_done_mask0(u_csb_io_cdma_wt_done_mask0),
    .io_cdma_wt_done_mask1(u_csb_io_cdma_wt_done_mask1),
    .io_csb2glb_req_valid(u_csb_io_csb2glb_req_valid),
    .io_csb2glb_req_bits(u_csb_io_csb2glb_req_bits),
    .io_csb2glb_resp_valid(u_csb_io_csb2glb_resp_valid),
    .io_csb2glb_resp_bits(u_csb_io_csb2glb_resp_bits),
    .io_sdp_done_status0(u_csb_io_sdp_done_status0),
    .io_sdp_done_status1(u_csb_io_sdp_done_status1),
    .io_sdp_done_mask0(u_csb_io_sdp_done_mask0),
    .io_sdp_done_mask1(u_csb_io_sdp_done_mask1),
    .io_sdp_done_set0_trigger(u_csb_io_sdp_done_set0_trigger),
    .io_sdp_done_status0_trigger(u_csb_io_sdp_done_status0_trigger),
    .io_req_wdat(u_csb_io_req_wdat)
  );
  NV_NVDLA_GLB_ic u_ic ( // @[NV_NVDLA_glb.scala 60:22:@479.4]
    .io_nvdla_core_clk(u_ic_io_nvdla_core_clk),
    .io_nvdla_falcon_clk(u_ic_io_nvdla_falcon_clk),
    .io_nvdla_core_rstn(u_ic_io_nvdla_core_rstn),
    .io_nvdla_falcon_rstn(u_ic_io_nvdla_falcon_rstn),
    .io_cdp2glb_done_intr_pd(u_ic_io_cdp2glb_done_intr_pd),
    .io_cdp_done_mask0(u_ic_io_cdp_done_mask0),
    .io_cdp_done_mask1(u_ic_io_cdp_done_mask1),
    .io_cdp_done_status0(u_ic_io_cdp_done_status0),
    .io_cdp_done_status1(u_ic_io_cdp_done_status1),
    .io_pdp2glb_done_intr_pd(u_ic_io_pdp2glb_done_intr_pd),
    .io_pdp_done_mask0(u_ic_io_pdp_done_mask0),
    .io_pdp_done_mask1(u_ic_io_pdp_done_mask1),
    .io_pdp_done_status0(u_ic_io_pdp_done_status0),
    .io_pdp_done_status1(u_ic_io_pdp_done_status1),
    .io_cacc2glb_done_intr_pd(u_ic_io_cacc2glb_done_intr_pd),
    .io_cacc_done_mask0(u_ic_io_cacc_done_mask0),
    .io_cacc_done_mask1(u_ic_io_cacc_done_mask1),
    .io_cacc_done_status0(u_ic_io_cacc_done_status0),
    .io_cacc_done_status1(u_ic_io_cacc_done_status1),
    .io_cdma_dat2glb_done_intr_pd(u_ic_io_cdma_dat2glb_done_intr_pd),
    .io_cdma_dat_done_mask0(u_ic_io_cdma_dat_done_mask0),
    .io_cdma_dat_done_mask1(u_ic_io_cdma_dat_done_mask1),
    .io_cdma_dat_done_status0(u_ic_io_cdma_dat_done_status0),
    .io_cdma_dat_done_status1(u_ic_io_cdma_dat_done_status1),
    .io_cdma_wt2glb_done_intr_pd(u_ic_io_cdma_wt2glb_done_intr_pd),
    .io_cdma_wt_done_mask0(u_ic_io_cdma_wt_done_mask0),
    .io_cdma_wt_done_mask1(u_ic_io_cdma_wt_done_mask1),
    .io_cdma_wt_done_status0(u_ic_io_cdma_wt_done_status0),
    .io_cdma_wt_done_status1(u_ic_io_cdma_wt_done_status1),
    .io_sdp2glb_done_intr_pd(u_ic_io_sdp2glb_done_intr_pd),
    .io_sdp_done_mask0(u_ic_io_sdp_done_mask0),
    .io_sdp_done_mask1(u_ic_io_sdp_done_mask1),
    .io_sdp_done_status0(u_ic_io_sdp_done_status0),
    .io_sdp_done_status1(u_ic_io_sdp_done_status1),
    .io_sdp_done_set0_trigger(u_ic_io_sdp_done_set0_trigger),
    .io_sdp_done_status0_trigger(u_ic_io_sdp_done_status0_trigger),
    .io_req_wdat(u_ic_io_req_wdat),
    .io_core_intr(u_ic_io_core_intr)
  );
  assign io_csb2glb_req_ready = 1'h1; // @[NV_NVDLA_glb.scala 130:16:@521.4]
  assign io_csb2glb_resp_valid = u_csb_io_csb2glb_resp_valid; // @[NV_NVDLA_glb.scala 130:16:@518.4]
  assign io_csb2glb_resp_bits = u_csb_io_csb2glb_resp_bits; // @[NV_NVDLA_glb.scala 130:16:@517.4]
  assign io_core_intr = u_ic_io_core_intr; // @[NV_NVDLA_glb.scala 137:18:@526.4]
  assign u_csb_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_glb.scala 62:29:@482.4]
  assign u_csb_io_nvdla_core_rstn = io_nvdla_core_rstn; // @[NV_NVDLA_glb.scala 63:30:@483.4]
  assign u_csb_io_cdp_done_status0 = u_ic_io_cdp_done_status0; // @[NV_NVDLA_glb.scala 87:39:@488.4]
  assign u_csb_io_cdp_done_status1 = u_ic_io_cdp_done_status1; // @[NV_NVDLA_glb.scala 88:39:@489.4]
  assign u_csb_io_pdp_done_status0 = u_ic_io_pdp_done_status0; // @[NV_NVDLA_glb.scala 94:39:@493.4]
  assign u_csb_io_pdp_done_status1 = u_ic_io_pdp_done_status1; // @[NV_NVDLA_glb.scala 95:39:@494.4]
  assign u_csb_io_cacc_done_status0 = u_ic_io_cacc_done_status0; // @[NV_NVDLA_glb.scala 107:32:@498.4]
  assign u_csb_io_cacc_done_status1 = u_ic_io_cacc_done_status1; // @[NV_NVDLA_glb.scala 108:32:@499.4]
  assign u_csb_io_cdma_dat_done_status0 = u_ic_io_cdma_dat_done_status0; // @[NV_NVDLA_glb.scala 109:36:@500.4]
  assign u_csb_io_cdma_dat_done_status1 = u_ic_io_cdma_dat_done_status1; // @[NV_NVDLA_glb.scala 110:36:@501.4]
  assign u_csb_io_cdma_wt_done_status0 = u_ic_io_cdma_wt_done_status0; // @[NV_NVDLA_glb.scala 111:35:@502.4]
  assign u_csb_io_cdma_wt_done_status1 = u_ic_io_cdma_wt_done_status1; // @[NV_NVDLA_glb.scala 112:35:@503.4]
  assign u_csb_io_csb2glb_req_valid = io_csb2glb_req_valid; // @[NV_NVDLA_glb.scala 130:16:@520.4]
  assign u_csb_io_csb2glb_req_bits = io_csb2glb_req_bits; // @[NV_NVDLA_glb.scala 130:16:@519.4]
  assign u_csb_io_sdp_done_status0 = u_ic_io_sdp_done_status0; // @[NV_NVDLA_glb.scala 113:31:@504.4]
  assign u_csb_io_sdp_done_status1 = u_ic_io_sdp_done_status1; // @[NV_NVDLA_glb.scala 114:31:@505.4]
  assign u_ic_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_glb.scala 64:28:@484.4]
  assign u_ic_io_nvdla_falcon_clk = io_nvdla_core_clk; // @[NV_NVDLA_glb.scala 69:34:@485.4]
  assign u_ic_io_nvdla_core_rstn = io_nvdla_core_rstn; // @[NV_NVDLA_glb.scala 71:29:@486.4]
  assign u_ic_io_nvdla_falcon_rstn = io_nvdla_core_rstn; // @[NV_NVDLA_glb.scala 76:35:@487.4]
  assign u_ic_io_cdp2glb_done_intr_pd = io_cdp2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 91:42:@492.4]
  assign u_ic_io_cdp_done_mask0 = u_csb_io_cdp_done_mask0; // @[NV_NVDLA_glb.scala 89:36:@490.4]
  assign u_ic_io_cdp_done_mask1 = u_csb_io_cdp_done_mask1; // @[NV_NVDLA_glb.scala 90:36:@491.4]
  assign u_ic_io_pdp2glb_done_intr_pd = io_pdp2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 98:42:@497.4]
  assign u_ic_io_pdp_done_mask0 = u_csb_io_pdp_done_mask0; // @[NV_NVDLA_glb.scala 96:36:@495.4]
  assign u_ic_io_pdp_done_mask1 = u_csb_io_pdp_done_mask1; // @[NV_NVDLA_glb.scala 97:36:@496.4]
  assign u_ic_io_cacc2glb_done_intr_pd = io_cacc2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 132:35:@522.4]
  assign u_ic_io_cacc_done_mask0 = u_csb_io_cacc_done_mask0; // @[NV_NVDLA_glb.scala 116:29:@506.4]
  assign u_ic_io_cacc_done_mask1 = u_csb_io_cacc_done_mask1; // @[NV_NVDLA_glb.scala 117:29:@507.4]
  assign u_ic_io_cdma_dat2glb_done_intr_pd = io_cdma_dat2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 133:39:@523.4]
  assign u_ic_io_cdma_dat_done_mask0 = u_csb_io_cdma_dat_done_mask0; // @[NV_NVDLA_glb.scala 118:33:@508.4]
  assign u_ic_io_cdma_dat_done_mask1 = u_csb_io_cdma_dat_done_mask1; // @[NV_NVDLA_glb.scala 119:33:@509.4]
  assign u_ic_io_cdma_wt2glb_done_intr_pd = io_cdma_wt2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 134:38:@524.4]
  assign u_ic_io_cdma_wt_done_mask0 = u_csb_io_cdma_wt_done_mask0; // @[NV_NVDLA_glb.scala 120:32:@510.4]
  assign u_ic_io_cdma_wt_done_mask1 = u_csb_io_cdma_wt_done_mask1; // @[NV_NVDLA_glb.scala 121:32:@511.4]
  assign u_ic_io_sdp2glb_done_intr_pd = io_sdp2glb_done_intr_pd; // @[NV_NVDLA_glb.scala 135:34:@525.4]
  assign u_ic_io_sdp_done_mask0 = u_csb_io_sdp_done_mask0; // @[NV_NVDLA_glb.scala 123:28:@512.4]
  assign u_ic_io_sdp_done_mask1 = u_csb_io_sdp_done_mask1; // @[NV_NVDLA_glb.scala 124:28:@513.4]
  assign u_ic_io_sdp_done_set0_trigger = u_csb_io_sdp_done_set0_trigger; // @[NV_NVDLA_glb.scala 125:35:@514.4]
  assign u_ic_io_sdp_done_status0_trigger = u_csb_io_sdp_done_status0_trigger; // @[NV_NVDLA_glb.scala 126:38:@515.4]
  assign u_ic_io_req_wdat = u_csb_io_req_wdat[21:0]; // @[NV_NVDLA_glb.scala 127:22:@516.4]
endmodule
