module NV_CLK_gate_power( // @[:@32.2]
  input   io_clk, // @[:@35.4]
  output  io_clk_gated // @[:@35.4]
);
  assign io_clk_gated = io_clk; // @[NV_CLK_gate_power.scala 19:18:@37.4]
endmodule
module nv_flopram( // @[:@90.2]
  input         reset, // @[:@92.4]
  input         io_clk, // @[:@93.4]
  input         io_clk_mgated, // @[:@93.4]
  input  [49:0] io_di, // @[:@93.4]
  input         io_iwe, // @[:@93.4]
  input         io_we, // @[:@93.4]
  input  [1:0]  io_wa, // @[:@93.4]
  input  [2:0]  io_ra, // @[:@93.4]
  output [49:0] io_dout // @[:@93.4]
);
  reg [49:0] di_d; // @[Reg.scala 19:20:@95.4]
  reg [63:0] _RAND_0;
  wire [49:0] _GEN_0; // @[Reg.scala 20:19:@96.4]
  reg [49:0] _T_28; // @[nv_flopram.scala 68:61:@101.4]
  reg [63:0] _RAND_1;
  reg [49:0] _T_31; // @[nv_flopram.scala 68:61:@102.4]
  reg [63:0] _RAND_2;
  reg [49:0] _T_34; // @[nv_flopram.scala 68:61:@103.4]
  reg [63:0] _RAND_3;
  reg [49:0] _T_37; // @[nv_flopram.scala 68:61:@104.4]
  reg [63:0] _RAND_4;
  wire  _T_41; // @[nv_flopram.scala 73:32:@107.6]
  wire [49:0] _GEN_1; // @[nv_flopram.scala 73:40:@108.6]
  wire  _T_43; // @[nv_flopram.scala 73:32:@111.6]
  wire [49:0] _GEN_2; // @[nv_flopram.scala 73:40:@112.6]
  wire  _T_45; // @[nv_flopram.scala 73:32:@115.6]
  wire [49:0] _GEN_3; // @[nv_flopram.scala 73:40:@116.6]
  wire  _T_47; // @[nv_flopram.scala 73:32:@119.6]
  wire [49:0] _GEN_4; // @[nv_flopram.scala 73:40:@120.6]
  wire [49:0] _GEN_5; // @[nv_flopram.scala 70:16:@106.4]
  wire [49:0] _GEN_6; // @[nv_flopram.scala 70:16:@106.4]
  wire [49:0] _GEN_7; // @[nv_flopram.scala 70:16:@106.4]
  wire [49:0] _GEN_8; // @[nv_flopram.scala 70:16:@106.4]
  wire  _T_54; // @[Mux.scala 46:19:@125.4]
  wire [49:0] _T_55; // @[Mux.scala 46:16:@126.4]
  wire  _T_56; // @[Mux.scala 46:19:@127.4]
  wire [49:0] _T_57; // @[Mux.scala 46:16:@128.4]
  wire  _T_58; // @[Mux.scala 46:19:@129.4]
  wire [49:0] _T_59; // @[Mux.scala 46:16:@130.4]
  wire  _T_60; // @[Mux.scala 46:19:@131.4]
  wire [49:0] _T_61; // @[Mux.scala 46:16:@132.4]
  wire  _T_62; // @[Mux.scala 46:19:@133.4]
  assign _GEN_0 = io_iwe ? io_di : di_d; // @[Reg.scala 20:19:@96.4]
  assign _T_41 = io_wa == 2'h0; // @[nv_flopram.scala 73:32:@107.6]
  assign _GEN_1 = _T_41 ? di_d : _T_28; // @[nv_flopram.scala 73:40:@108.6]
  assign _T_43 = io_wa == 2'h1; // @[nv_flopram.scala 73:32:@111.6]
  assign _GEN_2 = _T_43 ? di_d : _T_31; // @[nv_flopram.scala 73:40:@112.6]
  assign _T_45 = io_wa == 2'h2; // @[nv_flopram.scala 73:32:@115.6]
  assign _GEN_3 = _T_45 ? di_d : _T_34; // @[nv_flopram.scala 73:40:@116.6]
  assign _T_47 = io_wa == 2'h3; // @[nv_flopram.scala 73:32:@119.6]
  assign _GEN_4 = _T_47 ? di_d : _T_37; // @[nv_flopram.scala 73:40:@120.6]
  assign _GEN_5 = io_we ? _GEN_1 : _T_28; // @[nv_flopram.scala 70:16:@106.4]
  assign _GEN_6 = io_we ? _GEN_2 : _T_31; // @[nv_flopram.scala 70:16:@106.4]
  assign _GEN_7 = io_we ? _GEN_3 : _T_34; // @[nv_flopram.scala 70:16:@106.4]
  assign _GEN_8 = io_we ? _GEN_4 : _T_37; // @[nv_flopram.scala 70:16:@106.4]
  assign _T_54 = 3'h4 == io_ra; // @[Mux.scala 46:19:@125.4]
  assign _T_55 = _T_54 ? io_di : 50'h0; // @[Mux.scala 46:16:@126.4]
  assign _T_56 = 3'h3 == io_ra; // @[Mux.scala 46:19:@127.4]
  assign _T_57 = _T_56 ? _T_37 : _T_55; // @[Mux.scala 46:16:@128.4]
  assign _T_58 = 3'h2 == io_ra; // @[Mux.scala 46:19:@129.4]
  assign _T_59 = _T_58 ? _T_34 : _T_57; // @[Mux.scala 46:16:@130.4]
  assign _T_60 = 3'h1 == io_ra; // @[Mux.scala 46:19:@131.4]
  assign _T_61 = _T_60 ? _T_31 : _T_59; // @[Mux.scala 46:16:@132.4]
  assign _T_62 = 3'h0 == io_ra; // @[Mux.scala 46:19:@133.4]
  assign io_dout = _T_62 ? _T_28 : _T_61; // @[nv_flopram.scala 83:13:@135.4]
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
  di_d = _RAND_0[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {2{`RANDOM}};
  _T_28 = _RAND_1[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_31 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {2{`RANDOM}};
  _T_34 = _RAND_3[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_37 = _RAND_4[49:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      di_d <= 50'h0;
    end else begin
      if (io_iwe) begin
        di_d <= io_di;
      end
    end
  end
  always @(posedge io_clk_mgated) begin
    if (reset) begin
      _T_28 <= 50'h0;
    end else begin
      if (io_we) begin
        if (_T_41) begin
          _T_28 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_31 <= 50'h0;
    end else begin
      if (io_we) begin
        if (_T_43) begin
          _T_31 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_34 <= 50'h0;
    end else begin
      if (io_we) begin
        if (_T_45) begin
          _T_34 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_37 <= 50'h0;
    end else begin
      if (io_we) begin
        if (_T_47) begin
          _T_37 <= di_d;
        end
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr_strict( // @[:@144.2]
  input        io_inc, // @[:@147.4]
  input  [2:0] io_gray, // @[:@147.4]
  output [2:0] io_gray_next // @[:@147.4]
);
  wire  _T_11; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:26:@149.4]
  wire  _T_12; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:39:@150.4]
  wire  _T_13; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:30:@151.4]
  wire  _T_14; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:53:@152.4]
  wire  polarity; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:44:@153.4]
  wire  _T_15; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:28:@154.4]
  wire  _T_18; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:76:@157.4]
  wire  _T_19; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:74:@158.4]
  wire  _T_20; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:64:@159.4]
  wire  _T_23; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:111:@162.4]
  wire  _T_24; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:101:@163.4]
  wire  _T_26; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:137:@165.4]
  wire  _T_27; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:135:@166.4]
  wire [2:0] _T_29; // @[Cat.scala 30:58:@168.4]
  assign _T_11 = io_gray[0]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:26:@149.4]
  assign _T_12 = io_gray[1]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:39:@150.4]
  assign _T_13 = _T_11 ^ _T_12; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:30:@151.4]
  assign _T_14 = io_gray[2]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:53:@152.4]
  assign polarity = _T_13 ^ _T_14; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 406:44:@153.4]
  assign _T_15 = ~ io_inc; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:28:@154.4]
  assign _T_18 = ~ _T_11; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:76:@157.4]
  assign _T_19 = polarity & _T_18; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:74:@158.4]
  assign _T_20 = _T_14 ^ _T_19; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:64:@159.4]
  assign _T_23 = polarity & _T_11; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:111:@162.4]
  assign _T_24 = _T_12 ^ _T_23; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:101:@163.4]
  assign _T_26 = ~ polarity; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:137:@165.4]
  assign _T_27 = _T_11 ^ _T_26; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:135:@166.4]
  assign _T_29 = {_T_20,_T_24,_T_27}; // @[Cat.scala 30:58:@168.4]
  assign io_gray_next = _T_15 ? io_gray : _T_29; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 409:21:@170.4]
endmodule
module p_SSYNC3DO_C_PPP( // @[:@172.2]
  input   io_clk, // @[:@175.4]
  input   io_d, // @[:@175.4]
  output  io_q, // @[:@175.4]
  input   io_clr_ // @[:@175.4]
);
  wire  _T_13; // @[p_SSYNC3DO_C_PPP.scala 15:27:@177.4]
  reg  _T_16; // @[p_SSYNC3DO_C_PPP.scala 17:24:@178.4]
  reg [31:0] _RAND_0;
  reg  _T_19; // @[p_SSYNC3DO_C_PPP.scala 18:25:@179.4]
  reg [31:0] _RAND_1;
  reg  _T_22; // @[p_SSYNC3DO_C_PPP.scala 19:25:@180.4]
  reg [31:0] _RAND_2;
  assign _T_13 = ~ io_clr_; // @[p_SSYNC3DO_C_PPP.scala 15:27:@177.4]
  assign io_q = _T_16; // @[p_SSYNC3DO_C_PPP.scala 25:10:@184.4]
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
  _T_16 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_19 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_22 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (_T_13) begin
      _T_16 <= 1'h0;
    end else begin
      _T_16 <= _T_19;
    end
    if (_T_13) begin
      _T_19 <= 1'h0;
    end else begin
      _T_19 <= _T_22;
    end
    if (_T_13) begin
      _T_22 <= 1'h0;
    end else begin
      _T_22 <= io_d;
    end
  end
endmodule
module p_STRICTSYNC3DOTM_C_PPP( // @[:@186.2]
  input   io_SRC_D_NEXT, // @[:@189.4]
  input   io_SRC_CLK, // @[:@189.4]
  input   io_SRC_CLRN, // @[:@189.4]
  input   io_DST_CLK, // @[:@189.4]
  input   io_DST_CLRN, // @[:@189.4]
  output  io_SRC_D, // @[:@189.4]
  output  io_DST_Q // @[:@189.4]
);
  wire  sync3d_io_clk; // @[p_STRICTSYNC3DOTM_C_PPP.scala 27:22:@195.4]
  wire  sync3d_io_d; // @[p_STRICTSYNC3DOTM_C_PPP.scala 27:22:@195.4]
  wire  sync3d_io_q; // @[p_STRICTSYNC3DOTM_C_PPP.scala 27:22:@195.4]
  wire  sync3d_io_clr_; // @[p_STRICTSYNC3DOTM_C_PPP.scala 27:22:@195.4]
  wire  _T_23; // @[p_STRICTSYNC3DOTM_C_PPP.scala 25:45:@191.4]
  reg  _T_26; // @[p_STRICTSYNC3DOTM_C_PPP.scala 25:67:@192.4]
  reg [31:0] _RAND_0;
  p_SSYNC3DO_C_PPP sync3d ( // @[p_STRICTSYNC3DOTM_C_PPP.scala 27:22:@195.4]
    .io_clk(sync3d_io_clk),
    .io_d(sync3d_io_d),
    .io_q(sync3d_io_q),
    .io_clr_(sync3d_io_clr_)
  );
  assign _T_23 = ~ io_SRC_CLRN; // @[p_STRICTSYNC3DOTM_C_PPP.scala 25:45:@191.4]
  assign io_SRC_D = _T_26; // @[p_STRICTSYNC3DOTM_C_PPP.scala 25:12:@194.4]
  assign io_DST_Q = sync3d_io_q; // @[p_STRICTSYNC3DOTM_C_PPP.scala 31:12:@201.4]
  assign sync3d_io_clk = io_DST_CLK; // @[p_STRICTSYNC3DOTM_C_PPP.scala 28:17:@198.4]
  assign sync3d_io_d = io_SRC_D; // @[p_STRICTSYNC3DOTM_C_PPP.scala 30:15:@200.4]
  assign sync3d_io_clr_ = io_DST_CLRN; // @[p_STRICTSYNC3DOTM_C_PPP.scala 29:18:@199.4]
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
  _T_26 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_SRC_CLK) begin
    if (_T_23) begin
      _T_26 <= 1'h0;
    end else begin
      _T_26 <= io_SRC_D_NEXT;
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr( // @[:@265.2]
  input        io_clk, // @[:@268.4]
  input        io_reset_, // @[:@268.4]
  input        io_inc, // @[:@268.4]
  output [2:0] io_gray // @[:@268.4]
);
  wire  _T_13; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 428:29:@270.4]
  reg [2:0] _T_16; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 429:29:@271.4]
  reg [31:0] _RAND_0;
  wire  _T_17; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:30:@272.4]
  wire  _T_18; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:44:@273.4]
  wire  _T_19; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:34:@274.4]
  wire  _T_20; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:57:@275.4]
  wire  _T_21; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:47:@276.4]
  wire  _T_22; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:34:@278.6]
  wire  _T_23; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:57:@279.6]
  wire  _T_24; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:49:@280.6]
  wire  _T_25; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:47:@281.6]
  wire  _T_26; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:37:@282.6]
  wire  _T_27; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:71:@283.6]
  wire  _T_29; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:84:@285.6]
  wire  _T_30; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:74:@286.6]
  wire  _T_32; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:110:@288.6]
  wire  _T_33; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:108:@289.6]
  wire [2:0] _T_35; // @[Cat.scala 30:58:@291.6]
  wire [2:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 431:19:@277.4]
  assign _T_13 = ~ io_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 428:29:@270.4]
  assign _T_17 = _T_16[0]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:30:@272.4]
  assign _T_18 = _T_16[1]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:44:@273.4]
  assign _T_19 = _T_17 ^ _T_18; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:34:@274.4]
  assign _T_20 = _T_16[2]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:57:@275.4]
  assign _T_21 = _T_19 ^ _T_20; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 430:47:@276.4]
  assign _T_22 = io_gray[2]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:34:@278.6]
  assign _T_23 = io_gray[0]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:57:@279.6]
  assign _T_24 = ~ _T_23; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:49:@280.6]
  assign _T_25 = _T_21 & _T_24; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:47:@281.6]
  assign _T_26 = _T_22 ^ _T_25; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:37:@282.6]
  assign _T_27 = io_gray[1]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:71:@283.6]
  assign _T_29 = _T_21 & _T_23; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:84:@285.6]
  assign _T_30 = _T_27 ^ _T_29; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:74:@286.6]
  assign _T_32 = ~ _T_21; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:110:@288.6]
  assign _T_33 = _T_23 ^ _T_32; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 432:108:@289.6]
  assign _T_35 = {_T_26,_T_30,_T_33}; // @[Cat.scala 30:58:@291.6]
  assign _GEN_0 = io_inc ? _T_35 : _T_16; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 431:19:@277.4]
  assign io_gray = _T_16; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 434:15:@294.4]
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
  _T_16 = _RAND_0[2:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (_T_13) begin
      _T_16 <= 3'h0;
    end else begin
      if (io_inc) begin
        _T_16 <= _T_35;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_falcon2csb_fifo( // @[:@462.2]
  input         reset, // @[:@464.4]
  input         io_wr_clk, // @[:@465.4]
  input         io_rd_clk, // @[:@465.4]
  input         io_wr_reset_, // @[:@465.4]
  input         io_rd_reset_, // @[:@465.4]
  output        io_wr_ready, // @[:@465.4]
  input         io_wr_req, // @[:@465.4]
  output        io_rd_req, // @[:@465.4]
  input  [49:0] io_wr_data, // @[:@465.4]
  output [49:0] io_rd_data // @[:@465.4]
);
  wire  wr_clk_wr_dft_mgate_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 40:37:@470.4]
  wire  wr_clk_wr_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 40:37:@470.4]
  wire  rd_clk_rd_dft_mgate_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 55:37:@478.4]
  wire  rd_clk_rd_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 55:37:@478.4]
  wire  wr_clk_wr_mgate_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 78:33:@484.4]
  wire  wr_clk_wr_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 78:33:@484.4]
  wire  rd_clk_rd_mgate_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 84:33:@490.4]
  wire  rd_clk_rd_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 84:33:@490.4]
  wire  ram_reset; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
  wire  ram_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
  wire  ram_io_clk_mgated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
  wire [49:0] ram_io_di; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
  wire  ram_io_iwe; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
  wire  ram_io_we; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
  wire [1:0] ram_io_wa; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
  wire [2:0] ram_io_ra; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
  wire [49:0] ram_io_dout; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
  wire  wr_clk_wr_mgated_snd_gate_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 233:43:@575.4]
  wire  wr_clk_wr_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 233:43:@575.4]
  wire  wr_pushing_gray_io_inc; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 239:33:@581.4]
  wire [2:0] wr_pushing_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 239:33:@581.4]
  wire [2:0] wr_pushing_gray_io_gray_next; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 239:33:@581.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 246:43:@586.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 246:43:@586.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 246:43:@586.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 246:43:@586.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 246:43:@586.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 246:43:@586.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 246:43:@586.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 257:43:@597.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 257:43:@597.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 257:43:@597.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 257:43:@597.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 257:43:@597.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 257:43:@597.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 257:43:@597.4]
  wire  nv_AFIFO_wr_pushing_sync2_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 268:43:@608.4]
  wire  nv_AFIFO_wr_pushing_sync2_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 268:43:@608.4]
  wire  nv_AFIFO_wr_pushing_sync2_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 268:43:@608.4]
  wire  nv_AFIFO_wr_pushing_sync2_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 268:43:@608.4]
  wire  nv_AFIFO_wr_pushing_sync2_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 268:43:@608.4]
  wire  nv_AFIFO_wr_pushing_sync2_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 268:43:@608.4]
  wire  nv_AFIFO_wr_pushing_sync2_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 268:43:@608.4]
  wire  rd_pushing_gray_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 285:33:@626.4]
  wire  rd_pushing_gray_io_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 285:33:@626.4]
  wire  rd_pushing_gray_io_inc; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 285:33:@626.4]
  wire [2:0] rd_pushing_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 285:33:@626.4]
  wire  rd_clk_rd_mgated_snd_gate_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 292:43:@633.4]
  wire  rd_clk_rd_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 292:43:@633.4]
  wire  wr_clk_rcv_gate_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 297:33:@639.4]
  wire  wr_clk_rcv_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 297:33:@639.4]
  wire  rd_popping_gray_io_inc; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 305:33:@647.4]
  wire [2:0] rd_popping_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 305:33:@647.4]
  wire [2:0] rd_popping_gray_io_gray_next; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 305:33:@647.4]
  wire  nv_AFIFO_rd_popping_sync0_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 312:43:@652.4]
  wire  nv_AFIFO_rd_popping_sync0_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 312:43:@652.4]
  wire  nv_AFIFO_rd_popping_sync0_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 312:43:@652.4]
  wire  nv_AFIFO_rd_popping_sync0_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 312:43:@652.4]
  wire  nv_AFIFO_rd_popping_sync0_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 312:43:@652.4]
  wire  nv_AFIFO_rd_popping_sync0_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 312:43:@652.4]
  wire  nv_AFIFO_rd_popping_sync0_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 312:43:@652.4]
  wire  nv_AFIFO_rd_popping_sync1_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 323:43:@663.4]
  wire  nv_AFIFO_rd_popping_sync1_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 323:43:@663.4]
  wire  nv_AFIFO_rd_popping_sync1_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 323:43:@663.4]
  wire  nv_AFIFO_rd_popping_sync1_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 323:43:@663.4]
  wire  nv_AFIFO_rd_popping_sync1_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 323:43:@663.4]
  wire  nv_AFIFO_rd_popping_sync1_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 323:43:@663.4]
  wire  nv_AFIFO_rd_popping_sync1_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 323:43:@663.4]
  wire  nv_AFIFO_rd_popping_sync2_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 334:43:@674.4]
  wire  nv_AFIFO_rd_popping_sync2_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 334:43:@674.4]
  wire  nv_AFIFO_rd_popping_sync2_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 334:43:@674.4]
  wire  nv_AFIFO_rd_popping_sync2_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 334:43:@674.4]
  wire  nv_AFIFO_rd_popping_sync2_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 334:43:@674.4]
  wire  nv_AFIFO_rd_popping_sync2_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 334:43:@674.4]
  wire  nv_AFIFO_rd_popping_sync2_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 334:43:@674.4]
  wire  wr_popping_gray_io_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 351:33:@693.4]
  wire  wr_popping_gray_io_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 351:33:@693.4]
  wire  wr_popping_gray_io_inc; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 351:33:@693.4]
  wire [2:0] wr_popping_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 351:33:@693.4]
  wire  _T_30; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 95:58:@496.4]
  reg  wr_req_in; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 95:80:@497.4]
  reg [31:0] _RAND_0;
  reg  wr_busy_in; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 96:81:@499.4]
  reg [31:0] _RAND_1;
  wire  _T_36; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 97:20:@500.4]
  wire [2:0] rd_popping_gray_cntr_sync; // @[Cat.scala 30:58:@689.4]
  wire [2:0] wr_popping_gray_cntr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 348:36:@690.4 NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 355:26:@699.4]
  wire  wr_popping; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 349:45:@691.4]
  reg  wr_busy_int; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 112:81:@516.4]
  reg [31:0] _RAND_2;
  wire  _T_47; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 113:34:@517.4]
  wire  wr_reserving; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 113:31:@518.4]
  reg [2:0] wr_count; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 115:78:@522.4]
  reg [31:0] _RAND_3;
  wire [3:0] _T_58; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 118:66:@527.4]
  wire [2:0] _T_59; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 118:66:@528.4]
  wire [2:0] wr_count_next_no_wr_popping; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 118:42:@529.4]
  wire  wr_count_next_no_wr_popping_is_4; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 121:74:@531.4]
  wire  wr_count_next_is_4; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 122:33:@532.4]
  wire [3:0] _T_54; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 117:74:@523.4]
  wire [3:0] _T_55; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 117:74:@524.4]
  wire [2:0] _T_56; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 117:74:@525.4]
  wire [2:0] wr_count_next_wr_popping; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 117:39:@526.4]
  wire [2:0] wr_count_next; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 119:28:@530.4]
  wire  _T_38; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 102:50:@503.4]
  wire  _T_39; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 102:68:@504.4]
  wire  wr_busy_in_next_wr_req_eq_0; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 102:66:@505.4]
  wire  wr_busy_in_next; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 103:30:@506.4]
  wire  wr_busy_in_int; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 131:33:@539.4]
  wire  _T_41; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 107:10:@509.4]
  wire  _T_43; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 108:30:@512.6]
  wire  _GEN_0; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 107:26:@510.4]
  wire  _T_69; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 134:24:@542.4]
  wire [2:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 134:38:@543.4]
  reg [1:0] wr_adr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 142:76:@547.4]
  reg [31:0] _RAND_4;
  wire [2:0] _T_74; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 147:30:@548.4]
  wire [1:0] wr_adr_next; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 147:30:@549.4]
  wire [1:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 148:23:@550.4]
  wire  _T_75; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 152:54:@553.4]
  reg [1:0] rd_adr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 152:76:@554.4]
  reg [31:0] _RAND_5;
  wire [2:0] _T_81; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 173:38:@569.4]
  wire [1:0] rd_adr_next_popping; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 173:38:@570.4]
  reg [2:0] rd_count_p; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 364:80:@710.4]
  reg [31:0] _RAND_6;
  wire  _T_135; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 369:28:@719.4]
  wire [2:0] wr_pushing_gray_cntr_sync; // @[Cat.scala 30:58:@623.4]
  wire [2:0] rd_pushing_gray_cntr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 282:36:@624.4 NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 289:26:@632.4]
  wire  rd_pushing; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 283:48:@625.4]
  wire  rd_req_p; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 369:36:@720.4]
  reg  rd_req_int; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 361:80:@702.4]
  reg [31:0] _RAND_7;
  wire [1:0] _GEN_3; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 174:21:@571.4]
  wire [1:0] _T_93; // @[Cat.scala 30:58:@619.4]
  wire [1:0] _T_111; // @[Cat.scala 30:58:@685.4]
  wire [3:0] _T_128; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 365:76:@711.4]
  wire [3:0] _T_129; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 365:76:@712.4]
  wire [2:0] _T_130; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 365:76:@713.4]
  wire [2:0] rd_count_p_next_rd_popping; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 365:41:@714.4]
  wire [3:0] _T_132; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 366:67:@715.4]
  wire [2:0] _T_133; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 366:67:@716.4]
  wire [2:0] rd_count_p_next_no_rd_popping; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 366:44:@717.4]
  wire [2:0] rd_count_p_next; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 368:30:@718.4]
  wire  _T_137; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 371:22:@722.4]
  wire [2:0] _GEN_4; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 371:37:@723.4]
  reg [49:0] nv_AFIFO_rd_data; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 375:82:@727.4]
  reg [63:0] _RAND_8;
  NV_CLK_gate_power wr_clk_wr_dft_mgate ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 40:37:@470.4]
    .io_clk(wr_clk_wr_dft_mgate_io_clk),
    .io_clk_gated(wr_clk_wr_dft_mgate_io_clk_gated)
  );
  NV_CLK_gate_power rd_clk_rd_dft_mgate ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 55:37:@478.4]
    .io_clk(rd_clk_rd_dft_mgate_io_clk),
    .io_clk_gated(rd_clk_rd_dft_mgate_io_clk_gated)
  );
  NV_CLK_gate_power wr_clk_wr_mgate ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 78:33:@484.4]
    .io_clk(wr_clk_wr_mgate_io_clk),
    .io_clk_gated(wr_clk_wr_mgate_io_clk_gated)
  );
  NV_CLK_gate_power rd_clk_rd_mgate ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 84:33:@490.4]
    .io_clk(rd_clk_rd_mgate_io_clk),
    .io_clk_gated(rd_clk_rd_mgate_io_clk_gated)
  );
  nv_flopram ram ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 160:21:@557.4]
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
  NV_CLK_gate_power wr_clk_wr_mgated_snd_gate ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 233:43:@575.4]
    .io_clk(wr_clk_wr_mgated_snd_gate_io_clk),
    .io_clk_gated(wr_clk_wr_mgated_snd_gate_io_clk_gated)
  );
  NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr_strict wr_pushing_gray ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 239:33:@581.4]
    .io_inc(wr_pushing_gray_io_inc),
    .io_gray(wr_pushing_gray_io_gray),
    .io_gray_next(wr_pushing_gray_io_gray_next)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_wr_pushing_sync0 ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 246:43:@586.4]
    .io_SRC_D_NEXT(nv_AFIFO_wr_pushing_sync0_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_wr_pushing_sync0_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_wr_pushing_sync0_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_wr_pushing_sync0_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_wr_pushing_sync0_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_wr_pushing_sync0_io_SRC_D),
    .io_DST_Q(nv_AFIFO_wr_pushing_sync0_io_DST_Q)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_wr_pushing_sync1 ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 257:43:@597.4]
    .io_SRC_D_NEXT(nv_AFIFO_wr_pushing_sync1_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_wr_pushing_sync1_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_wr_pushing_sync1_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_wr_pushing_sync1_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_wr_pushing_sync1_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_wr_pushing_sync1_io_SRC_D),
    .io_DST_Q(nv_AFIFO_wr_pushing_sync1_io_DST_Q)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_wr_pushing_sync2 ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 268:43:@608.4]
    .io_SRC_D_NEXT(nv_AFIFO_wr_pushing_sync2_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_wr_pushing_sync2_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_wr_pushing_sync2_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_wr_pushing_sync2_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_wr_pushing_sync2_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_wr_pushing_sync2_io_SRC_D),
    .io_DST_Q(nv_AFIFO_wr_pushing_sync2_io_DST_Q)
  );
  NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr rd_pushing_gray ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 285:33:@626.4]
    .io_clk(rd_pushing_gray_io_clk),
    .io_reset_(rd_pushing_gray_io_reset_),
    .io_inc(rd_pushing_gray_io_inc),
    .io_gray(rd_pushing_gray_io_gray)
  );
  NV_CLK_gate_power rd_clk_rd_mgated_snd_gate ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 292:43:@633.4]
    .io_clk(rd_clk_rd_mgated_snd_gate_io_clk),
    .io_clk_gated(rd_clk_rd_mgated_snd_gate_io_clk_gated)
  );
  NV_CLK_gate_power wr_clk_rcv_gate ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 297:33:@639.4]
    .io_clk(wr_clk_rcv_gate_io_clk),
    .io_clk_gated(wr_clk_rcv_gate_io_clk_gated)
  );
  NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr_strict rd_popping_gray ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 305:33:@647.4]
    .io_inc(rd_popping_gray_io_inc),
    .io_gray(rd_popping_gray_io_gray),
    .io_gray_next(rd_popping_gray_io_gray_next)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_rd_popping_sync0 ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 312:43:@652.4]
    .io_SRC_D_NEXT(nv_AFIFO_rd_popping_sync0_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_rd_popping_sync0_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_rd_popping_sync0_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_rd_popping_sync0_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_rd_popping_sync0_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_rd_popping_sync0_io_SRC_D),
    .io_DST_Q(nv_AFIFO_rd_popping_sync0_io_DST_Q)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_rd_popping_sync1 ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 323:43:@663.4]
    .io_SRC_D_NEXT(nv_AFIFO_rd_popping_sync1_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_rd_popping_sync1_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_rd_popping_sync1_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_rd_popping_sync1_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_rd_popping_sync1_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_rd_popping_sync1_io_SRC_D),
    .io_DST_Q(nv_AFIFO_rd_popping_sync1_io_DST_Q)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_rd_popping_sync2 ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 334:43:@674.4]
    .io_SRC_D_NEXT(nv_AFIFO_rd_popping_sync2_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_rd_popping_sync2_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_rd_popping_sync2_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_rd_popping_sync2_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_rd_popping_sync2_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_rd_popping_sync2_io_SRC_D),
    .io_DST_Q(nv_AFIFO_rd_popping_sync2_io_DST_Q)
  );
  NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr wr_popping_gray ( // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 351:33:@693.4]
    .io_clk(wr_popping_gray_io_clk),
    .io_reset_(wr_popping_gray_io_reset_),
    .io_inc(wr_popping_gray_io_inc),
    .io_gray(wr_popping_gray_io_gray)
  );
  assign _T_30 = ~ io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 95:58:@496.4]
  assign _T_36 = ~ wr_busy_in; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 97:20:@500.4]
  assign rd_popping_gray_cntr_sync = {nv_AFIFO_rd_popping_sync2_io_DST_Q,nv_AFIFO_rd_popping_sync1_io_DST_Q,nv_AFIFO_rd_popping_sync0_io_DST_Q}; // @[Cat.scala 30:58:@689.4]
  assign wr_popping_gray_cntr = wr_popping_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 348:36:@690.4 NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 355:26:@699.4]
  assign wr_popping = rd_popping_gray_cntr_sync != wr_popping_gray_cntr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 349:45:@691.4]
  assign _T_47 = ~ wr_busy_int; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 113:34:@517.4]
  assign wr_reserving = wr_req_in & _T_47; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 113:31:@518.4]
  assign _T_58 = wr_count + 3'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 118:66:@527.4]
  assign _T_59 = wr_count + 3'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 118:66:@528.4]
  assign wr_count_next_no_wr_popping = wr_reserving ? _T_59 : wr_count; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 118:42:@529.4]
  assign wr_count_next_no_wr_popping_is_4 = wr_count_next_no_wr_popping == 3'h4; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 121:74:@531.4]
  assign wr_count_next_is_4 = wr_popping ? 1'h0 : wr_count_next_no_wr_popping_is_4; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 122:33:@532.4]
  assign _T_54 = wr_count - 3'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 117:74:@523.4]
  assign _T_55 = $unsigned(_T_54); // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 117:74:@524.4]
  assign _T_56 = _T_55[2:0]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 117:74:@525.4]
  assign wr_count_next_wr_popping = wr_reserving ? wr_count : _T_56; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 117:39:@526.4]
  assign wr_count_next = wr_popping ? wr_count_next_wr_popping : wr_count_next_no_wr_popping; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 119:28:@530.4]
  assign _T_38 = wr_req_in & wr_count_next_is_4; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 102:50:@503.4]
  assign _T_39 = ~ wr_reserving; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 102:68:@504.4]
  assign wr_busy_in_next_wr_req_eq_0 = _T_38 & _T_39; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 102:66:@505.4]
  assign wr_busy_in_next = io_wr_req ? wr_count_next_is_4 : wr_busy_in_next_wr_req_eq_0; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 103:30:@506.4]
  assign wr_busy_in_int = wr_req_in & wr_busy_int; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 131:33:@539.4]
  assign _T_41 = ~ wr_busy_in_int; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 107:10:@509.4]
  assign _T_43 = io_wr_req & _T_36; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 108:30:@512.6]
  assign _GEN_0 = _T_41 ? _T_43 : wr_req_in; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 107:26:@510.4]
  assign _T_69 = wr_reserving ^ wr_popping; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 134:24:@542.4]
  assign _GEN_1 = _T_69 ? wr_count_next : wr_count; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 134:38:@543.4]
  assign _T_74 = wr_adr + 2'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 147:30:@548.4]
  assign wr_adr_next = wr_adr + 2'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 147:30:@549.4]
  assign _GEN_2 = wr_reserving ? wr_adr_next : wr_adr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 148:23:@550.4]
  assign _T_75 = ~ io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 152:54:@553.4]
  assign _T_81 = rd_adr + 2'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 173:38:@569.4]
  assign rd_adr_next_popping = rd_adr + 2'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 173:38:@570.4]
  assign _T_135 = rd_count_p != 3'h0; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 369:28:@719.4]
  assign wr_pushing_gray_cntr_sync = {nv_AFIFO_wr_pushing_sync2_io_DST_Q,nv_AFIFO_wr_pushing_sync1_io_DST_Q,nv_AFIFO_wr_pushing_sync0_io_DST_Q}; // @[Cat.scala 30:58:@623.4]
  assign rd_pushing_gray_cntr = rd_pushing_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 282:36:@624.4 NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 289:26:@632.4]
  assign rd_pushing = wr_pushing_gray_cntr_sync != rd_pushing_gray_cntr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 283:48:@625.4]
  assign rd_req_p = _T_135 | rd_pushing; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 369:36:@720.4]
  assign _GEN_3 = rd_req_p ? rd_adr_next_popping : rd_adr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 174:21:@571.4]
  assign _T_93 = {nv_AFIFO_wr_pushing_sync2_io_SRC_D,nv_AFIFO_wr_pushing_sync1_io_SRC_D}; // @[Cat.scala 30:58:@619.4]
  assign _T_111 = {nv_AFIFO_rd_popping_sync2_io_SRC_D,nv_AFIFO_rd_popping_sync1_io_SRC_D}; // @[Cat.scala 30:58:@685.4]
  assign _T_128 = rd_count_p - 3'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 365:76:@711.4]
  assign _T_129 = $unsigned(_T_128); // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 365:76:@712.4]
  assign _T_130 = _T_129[2:0]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 365:76:@713.4]
  assign rd_count_p_next_rd_popping = rd_pushing ? rd_count_p : _T_130; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 365:41:@714.4]
  assign _T_132 = rd_count_p + 3'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 366:67:@715.4]
  assign _T_133 = rd_count_p + 3'h1; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 366:67:@716.4]
  assign rd_count_p_next_no_rd_popping = rd_pushing ? _T_133 : rd_count_p; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 366:44:@717.4]
  assign rd_count_p_next = rd_req_p ? rd_count_p_next_rd_popping : rd_count_p_next_no_rd_popping; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 368:30:@718.4]
  assign _T_137 = rd_pushing | rd_req_p; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 371:22:@722.4]
  assign _GEN_4 = _T_137 ? rd_count_p_next : rd_count_p; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 371:37:@723.4]
  assign io_wr_ready = ~ wr_busy_in; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 97:17:@501.4]
  assign io_rd_req = rd_req_int; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 362:15:@703.4]
  assign io_rd_data = nv_AFIFO_rd_data; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 383:16:@735.4]
  assign wr_clk_wr_dft_mgate_io_clk = io_wr_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 41:32:@473.4]
  assign rd_clk_rd_dft_mgate_io_clk = io_rd_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 56:32:@481.4]
  assign wr_clk_wr_mgate_io_clk = io_wr_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 79:28:@487.4]
  assign rd_clk_rd_mgate_io_clk = io_rd_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 85:28:@493.4]
  assign ram_reset = reset; // @[:@559.4]
  assign ram_io_clk = wr_clk_wr_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 161:16:@560.4]
  assign ram_io_clk_mgated = wr_clk_wr_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 162:27:@561.4]
  assign ram_io_di = io_wr_data; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 164:15:@563.4]
  assign ram_io_iwe = _T_36 & io_wr_req; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 165:20:@564.4]
  assign ram_io_we = wr_req_in & _T_47; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 166:15:@565.4]
  assign ram_io_wa = wr_adr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 167:19:@566.4]
  assign ram_io_ra = {{1'd0}, rd_adr}; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 168:15:@567.4]
  assign wr_clk_wr_mgated_snd_gate_io_clk = io_wr_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 234:38:@578.4]
  assign wr_pushing_gray_io_inc = wr_req_in & _T_47; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 241:36:@584.4]
  assign wr_pushing_gray_io_gray = {_T_93,nv_AFIFO_wr_pushing_sync0_io_SRC_D}; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 243:29:@585.4]
  assign nv_AFIFO_wr_pushing_sync0_io_SRC_D_NEXT = wr_pushing_gray_io_gray_next[0]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 248:45:@591.4]
  assign nv_AFIFO_wr_pushing_sync0_io_SRC_CLK = wr_clk_wr_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 247:42:@589.4]
  assign nv_AFIFO_wr_pushing_sync0_io_SRC_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 252:43:@595.4]
  assign nv_AFIFO_wr_pushing_sync0_io_DST_CLK = rd_clk_rd_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 249:42:@592.4]
  assign nv_AFIFO_wr_pushing_sync0_io_DST_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 253:43:@596.4]
  assign nv_AFIFO_wr_pushing_sync1_io_SRC_D_NEXT = wr_pushing_gray_io_gray_next[1]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 259:45:@602.4]
  assign nv_AFIFO_wr_pushing_sync1_io_SRC_CLK = wr_clk_wr_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 258:42:@600.4]
  assign nv_AFIFO_wr_pushing_sync1_io_SRC_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 263:43:@606.4]
  assign nv_AFIFO_wr_pushing_sync1_io_DST_CLK = rd_clk_rd_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 260:42:@603.4]
  assign nv_AFIFO_wr_pushing_sync1_io_DST_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 264:43:@607.4]
  assign nv_AFIFO_wr_pushing_sync2_io_SRC_D_NEXT = wr_pushing_gray_io_gray_next[2]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 270:45:@613.4]
  assign nv_AFIFO_wr_pushing_sync2_io_SRC_CLK = wr_clk_wr_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 269:42:@611.4]
  assign nv_AFIFO_wr_pushing_sync2_io_SRC_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 274:43:@617.4]
  assign nv_AFIFO_wr_pushing_sync2_io_DST_CLK = rd_clk_rd_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 271:42:@614.4]
  assign nv_AFIFO_wr_pushing_sync2_io_DST_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 275:43:@618.4]
  assign rd_pushing_gray_io_clk = rd_clk_rd_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 286:28:@629.4]
  assign rd_pushing_gray_io_reset_ = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 287:31:@630.4]
  assign rd_pushing_gray_io_inc = wr_pushing_gray_cntr_sync != rd_pushing_gray_cntr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 288:28:@631.4]
  assign rd_clk_rd_mgated_snd_gate_io_clk = io_rd_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 293:38:@636.4]
  assign wr_clk_rcv_gate_io_clk = io_wr_clk; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 298:28:@642.4]
  assign rd_popping_gray_io_inc = _T_135 | rd_pushing; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 307:36:@650.4]
  assign rd_popping_gray_io_gray = {_T_111,nv_AFIFO_rd_popping_sync0_io_SRC_D}; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 309:29:@651.4]
  assign nv_AFIFO_rd_popping_sync0_io_SRC_D_NEXT = rd_popping_gray_io_gray_next[0]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 314:45:@657.4]
  assign nv_AFIFO_rd_popping_sync0_io_SRC_CLK = rd_clk_rd_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 313:42:@655.4]
  assign nv_AFIFO_rd_popping_sync0_io_SRC_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 318:43:@661.4]
  assign nv_AFIFO_rd_popping_sync0_io_DST_CLK = wr_clk_rcv_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 315:42:@658.4]
  assign nv_AFIFO_rd_popping_sync0_io_DST_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 319:43:@662.4]
  assign nv_AFIFO_rd_popping_sync1_io_SRC_D_NEXT = rd_popping_gray_io_gray_next[1]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 325:45:@668.4]
  assign nv_AFIFO_rd_popping_sync1_io_SRC_CLK = rd_clk_rd_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 324:42:@666.4]
  assign nv_AFIFO_rd_popping_sync1_io_SRC_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 329:43:@672.4]
  assign nv_AFIFO_rd_popping_sync1_io_DST_CLK = wr_clk_rcv_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 326:42:@669.4]
  assign nv_AFIFO_rd_popping_sync1_io_DST_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 330:43:@673.4]
  assign nv_AFIFO_rd_popping_sync2_io_SRC_D_NEXT = rd_popping_gray_io_gray_next[2]; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 336:45:@679.4]
  assign nv_AFIFO_rd_popping_sync2_io_SRC_CLK = rd_clk_rd_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 335:42:@677.4]
  assign nv_AFIFO_rd_popping_sync2_io_SRC_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 340:43:@683.4]
  assign nv_AFIFO_rd_popping_sync2_io_DST_CLK = wr_clk_rcv_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 337:42:@680.4]
  assign nv_AFIFO_rd_popping_sync2_io_DST_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 341:43:@684.4]
  assign wr_popping_gray_io_clk = wr_clk_wr_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 352:28:@696.4]
  assign wr_popping_gray_io_reset_ = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 353:31:@697.4]
  assign wr_popping_gray_io_inc = rd_popping_gray_cntr_sync != wr_popping_gray_cntr; // @[NV_NVDLA_CSB_MASTER_falcon2csb_fifo.scala 354:28:@698.4]
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
  wr_req_in = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  wr_busy_in = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  wr_busy_int = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  wr_count = _RAND_3[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  wr_adr = _RAND_4[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  rd_adr = _RAND_5[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  rd_count_p = _RAND_6[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  rd_req_int = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {2{`RANDOM}};
  nv_AFIFO_rd_data = _RAND_8[49:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge wr_clk_wr_dft_mgate_io_clk_gated) begin
    if (_T_30) begin
      wr_req_in <= 1'h0;
    end else begin
      if (_T_41) begin
        wr_req_in <= _T_43;
      end
    end
    if (_T_30) begin
      wr_busy_in <= 1'h0;
    end else begin
      if (io_wr_req) begin
        if (wr_popping) begin
          wr_busy_in <= 1'h0;
        end else begin
          wr_busy_in <= wr_count_next_no_wr_popping_is_4;
        end
      end else begin
        wr_busy_in <= wr_busy_in_next_wr_req_eq_0;
      end
    end
  end
  always @(posedge wr_clk_wr_mgate_io_clk_gated) begin
    if (_T_30) begin
      wr_busy_int <= 1'h0;
    end else begin
      if (wr_popping) begin
        wr_busy_int <= 1'h0;
      end else begin
        wr_busy_int <= wr_count_next_no_wr_popping_is_4;
      end
    end
    if (_T_30) begin
      wr_count <= 3'h0;
    end else begin
      if (_T_69) begin
        if (wr_popping) begin
          if (!(wr_reserving)) begin
            wr_count <= _T_56;
          end
        end else begin
          if (wr_reserving) begin
            wr_count <= _T_59;
          end
        end
      end
    end
    if (_T_30) begin
      wr_adr <= 2'h0;
    end else begin
      if (wr_reserving) begin
        wr_adr <= wr_adr_next;
      end
    end
  end
  always @(posedge rd_clk_rd_mgate_io_clk_gated) begin
    if (_T_75) begin
      rd_adr <= 2'h0;
    end else begin
      if (rd_req_p) begin
        rd_adr <= rd_adr_next_popping;
      end
    end
    if (_T_75) begin
      rd_count_p <= 3'h0;
    end else begin
      if (_T_137) begin
        if (rd_req_p) begin
          if (!(rd_pushing)) begin
            rd_count_p <= _T_130;
          end
        end else begin
          if (rd_pushing) begin
            rd_count_p <= _T_133;
          end
        end
      end
    end
    if (_T_75) begin
      rd_req_int <= 1'h0;
    end else begin
      rd_req_int <= rd_req_p;
    end
    if (rd_req_p) begin
      nv_AFIFO_rd_data <= ram_io_dout;
    end
  end
endmodule
module nv_flopram_1( // @[:@840.2]
  input         reset, // @[:@842.4]
  input         io_clk, // @[:@843.4]
  input         io_clk_mgated, // @[:@843.4]
  input  [33:0] io_di, // @[:@843.4]
  input         io_iwe, // @[:@843.4]
  input         io_we, // @[:@843.4]
  input         io_wa, // @[:@843.4]
  input  [1:0]  io_ra, // @[:@843.4]
  output [33:0] io_dout // @[:@843.4]
);
  reg [33:0] di_d; // @[Reg.scala 19:20:@845.4]
  reg [63:0] _RAND_0;
  wire [33:0] _GEN_0; // @[Reg.scala 20:19:@846.4]
  reg [33:0] _T_28; // @[nv_flopram.scala 68:61:@851.4]
  reg [63:0] _RAND_1;
  reg [33:0] _T_31; // @[nv_flopram.scala 68:61:@852.4]
  reg [63:0] _RAND_2;
  wire  _T_35; // @[nv_flopram.scala 73:32:@855.6]
  wire [33:0] _GEN_1; // @[nv_flopram.scala 73:40:@856.6]
  wire [33:0] _GEN_2; // @[nv_flopram.scala 73:40:@860.6]
  wire [33:0] _GEN_3; // @[nv_flopram.scala 70:16:@854.4]
  wire [33:0] _GEN_4; // @[nv_flopram.scala 70:16:@854.4]
  wire  _T_42; // @[Mux.scala 46:19:@865.4]
  wire [33:0] _T_43; // @[Mux.scala 46:16:@866.4]
  wire  _T_44; // @[Mux.scala 46:19:@867.4]
  wire [33:0] _T_45; // @[Mux.scala 46:16:@868.4]
  wire  _T_46; // @[Mux.scala 46:19:@869.4]
  assign _GEN_0 = io_iwe ? io_di : di_d; // @[Reg.scala 20:19:@846.4]
  assign _T_35 = io_wa == 1'h0; // @[nv_flopram.scala 73:32:@855.6]
  assign _GEN_1 = _T_35 ? di_d : _T_28; // @[nv_flopram.scala 73:40:@856.6]
  assign _GEN_2 = io_wa ? di_d : _T_31; // @[nv_flopram.scala 73:40:@860.6]
  assign _GEN_3 = io_we ? _GEN_1 : _T_28; // @[nv_flopram.scala 70:16:@854.4]
  assign _GEN_4 = io_we ? _GEN_2 : _T_31; // @[nv_flopram.scala 70:16:@854.4]
  assign _T_42 = 2'h2 == io_ra; // @[Mux.scala 46:19:@865.4]
  assign _T_43 = _T_42 ? io_di : 34'h0; // @[Mux.scala 46:16:@866.4]
  assign _T_44 = 2'h1 == io_ra; // @[Mux.scala 46:19:@867.4]
  assign _T_45 = _T_44 ? _T_31 : _T_43; // @[Mux.scala 46:16:@868.4]
  assign _T_46 = 2'h0 == io_ra; // @[Mux.scala 46:19:@869.4]
  assign io_dout = _T_46 ? _T_28 : _T_45; // @[nv_flopram.scala 83:13:@871.4]
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
  di_d = _RAND_0[33:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {2{`RANDOM}};
  _T_28 = _RAND_1[33:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_31 = _RAND_2[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (reset) begin
      di_d <= 34'h0;
    end else begin
      if (io_iwe) begin
        di_d <= io_di;
      end
    end
  end
  always @(posedge io_clk_mgated) begin
    if (reset) begin
      _T_28 <= 34'h0;
    end else begin
      if (io_we) begin
        if (_T_35) begin
          _T_28 <= di_d;
        end
      end
    end
    if (reset) begin
      _T_31 <= 34'h0;
    end else begin
      if (io_we) begin
        if (io_wa) begin
          _T_31 <= di_d;
        end
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_csb2falcon_fifo_gray_cntr_strict( // @[:@880.2]
  input        io_inc, // @[:@883.4]
  input  [1:0] io_gray, // @[:@883.4]
  output [1:0] io_gray_next // @[:@883.4]
);
  wire  _T_11; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 381:26:@885.4]
  wire  _T_12; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 381:39:@886.4]
  wire  polarity; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 381:30:@887.4]
  wire  _T_13; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 384:28:@888.4]
  wire  _T_15; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 384:64:@890.4]
  wire  _T_17; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 384:87:@892.4]
  wire  _T_18; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 384:85:@893.4]
  wire [1:0] _T_19; // @[Cat.scala 30:58:@894.4]
  assign _T_11 = io_gray[0]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 381:26:@885.4]
  assign _T_12 = io_gray[1]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 381:39:@886.4]
  assign polarity = _T_11 ^ _T_12; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 381:30:@887.4]
  assign _T_13 = ~ io_inc; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 384:28:@888.4]
  assign _T_15 = _T_12 ^ polarity; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 384:64:@890.4]
  assign _T_17 = ~ polarity; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 384:87:@892.4]
  assign _T_18 = _T_11 ^ _T_17; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 384:85:@893.4]
  assign _T_19 = {_T_15,_T_18}; // @[Cat.scala 30:58:@894.4]
  assign io_gray_next = _T_13 ? io_gray : _T_19; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 384:21:@896.4]
endmodule
module NV_NVDLA_CSB_MASTER_csb2falcon_fifo_gray_cntr( // @[:@960.2]
  input        io_clk, // @[:@963.4]
  input        io_reset_, // @[:@963.4]
  input        io_inc, // @[:@963.4]
  output [1:0] io_gray // @[:@963.4]
);
  wire  _T_13; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 402:29:@965.4]
  reg [1:0] _T_16; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 403:29:@966.4]
  reg [31:0] _RAND_0;
  wire  _T_17; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 404:30:@967.4]
  wire  _T_18; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 404:44:@968.4]
  wire  _T_19; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 404:34:@969.4]
  wire  _T_21; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 406:38:@972.6]
  wire  _T_23; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 406:62:@974.6]
  wire  _T_24; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 406:60:@975.6]
  wire [1:0] _T_25; // @[Cat.scala 30:58:@976.6]
  wire [1:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 405:19:@970.4]
  assign _T_13 = ~ io_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 402:29:@965.4]
  assign _T_17 = _T_16[0]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 404:30:@967.4]
  assign _T_18 = _T_16[1]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 404:44:@968.4]
  assign _T_19 = _T_17 ^ _T_18; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 404:34:@969.4]
  assign _T_21 = _T_18 ^ _T_19; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 406:38:@972.6]
  assign _T_23 = ~ _T_19; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 406:62:@974.6]
  assign _T_24 = _T_17 ^ _T_23; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 406:60:@975.6]
  assign _T_25 = {_T_21,_T_24}; // @[Cat.scala 30:58:@976.6]
  assign _GEN_0 = io_inc ? _T_25 : _T_16; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 405:19:@970.4]
  assign io_gray = _T_16; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 408:15:@979.4]
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
  _T_16 = _RAND_0[1:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_clk) begin
    if (_T_13) begin
      _T_16 <= 2'h0;
    end else begin
      if (io_inc) begin
        _T_16 <= _T_25;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_csb2falcon_fifo( // @[:@1096.2]
  input         reset, // @[:@1098.4]
  input         io_wr_clk, // @[:@1099.4]
  input         io_wr_reset_, // @[:@1099.4]
  input         io_rd_clk, // @[:@1099.4]
  input         io_rd_reset_, // @[:@1099.4]
  input         io_wr_req, // @[:@1099.4]
  output        io_rd_req, // @[:@1099.4]
  input  [33:0] io_wr_data, // @[:@1099.4]
  output [33:0] io_rd_data // @[:@1099.4]
);
  wire  wr_clk_wr_dft_mgate_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 38:37:@1104.4]
  wire  wr_clk_wr_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 38:37:@1104.4]
  wire  rd_clk_rd_dft_mgate_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 53:37:@1112.4]
  wire  rd_clk_rd_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 53:37:@1112.4]
  wire  wr_clk_wr_mgate_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 76:33:@1118.4]
  wire  wr_clk_wr_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 76:33:@1118.4]
  wire  rd_clk_rd_mgate_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 82:33:@1124.4]
  wire  rd_clk_rd_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 82:33:@1124.4]
  wire  ram_reset; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
  wire  ram_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
  wire  ram_io_clk_mgated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
  wire [33:0] ram_io_di; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
  wire  ram_io_iwe; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
  wire  ram_io_we; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
  wire  ram_io_wa; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
  wire [1:0] ram_io_ra; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
  wire [33:0] ram_io_dout; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
  wire  wr_clk_wr_mgated_snd_gate_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 231:43:@1209.4]
  wire  wr_clk_wr_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 231:43:@1209.4]
  wire  wr_pushing_gray_io_inc; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 237:33:@1215.4]
  wire [1:0] wr_pushing_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 237:33:@1215.4]
  wire [1:0] wr_pushing_gray_io_gray_next; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 237:33:@1215.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 244:43:@1220.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 244:43:@1220.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 244:43:@1220.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 244:43:@1220.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 244:43:@1220.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 244:43:@1220.4]
  wire  nv_AFIFO_wr_pushing_sync0_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 244:43:@1220.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 255:43:@1231.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 255:43:@1231.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 255:43:@1231.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 255:43:@1231.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 255:43:@1231.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 255:43:@1231.4]
  wire  nv_AFIFO_wr_pushing_sync1_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 255:43:@1231.4]
  wire  rd_pushing_gray_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 272:33:@1247.4]
  wire  rd_pushing_gray_io_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 272:33:@1247.4]
  wire  rd_pushing_gray_io_inc; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 272:33:@1247.4]
  wire [1:0] rd_pushing_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 272:33:@1247.4]
  wire  rd_clk_rd_mgated_snd_gate_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 279:43:@1254.4]
  wire  rd_clk_rd_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 279:43:@1254.4]
  wire  wr_clk_rcv_gate_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 284:33:@1260.4]
  wire  wr_clk_rcv_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 284:33:@1260.4]
  wire  rd_popping_gray_io_inc; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 292:33:@1268.4]
  wire [1:0] rd_popping_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 292:33:@1268.4]
  wire [1:0] rd_popping_gray_io_gray_next; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 292:33:@1268.4]
  wire  nv_AFIFO_rd_popping_sync0_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 299:43:@1273.4]
  wire  nv_AFIFO_rd_popping_sync0_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 299:43:@1273.4]
  wire  nv_AFIFO_rd_popping_sync0_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 299:43:@1273.4]
  wire  nv_AFIFO_rd_popping_sync0_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 299:43:@1273.4]
  wire  nv_AFIFO_rd_popping_sync0_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 299:43:@1273.4]
  wire  nv_AFIFO_rd_popping_sync0_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 299:43:@1273.4]
  wire  nv_AFIFO_rd_popping_sync0_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 299:43:@1273.4]
  wire  nv_AFIFO_rd_popping_sync1_io_SRC_D_NEXT; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 310:43:@1284.4]
  wire  nv_AFIFO_rd_popping_sync1_io_SRC_CLK; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 310:43:@1284.4]
  wire  nv_AFIFO_rd_popping_sync1_io_SRC_CLRN; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 310:43:@1284.4]
  wire  nv_AFIFO_rd_popping_sync1_io_DST_CLK; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 310:43:@1284.4]
  wire  nv_AFIFO_rd_popping_sync1_io_DST_CLRN; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 310:43:@1284.4]
  wire  nv_AFIFO_rd_popping_sync1_io_SRC_D; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 310:43:@1284.4]
  wire  nv_AFIFO_rd_popping_sync1_io_DST_Q; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 310:43:@1284.4]
  wire  wr_popping_gray_io_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 327:33:@1301.4]
  wire  wr_popping_gray_io_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 327:33:@1301.4]
  wire  wr_popping_gray_io_inc; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 327:33:@1301.4]
  wire [1:0] wr_popping_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 327:33:@1301.4]
  wire  _T_30; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 93:58:@1130.4]
  reg  wr_req_in; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 93:80:@1131.4]
  reg [31:0] _RAND_0;
  reg  wr_busy_in; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 94:81:@1133.4]
  reg [31:0] _RAND_1;
  wire  _T_36; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 95:20:@1134.4]
  wire [1:0] rd_popping_gray_cntr_sync; // @[Cat.scala 30:58:@1297.4]
  wire [1:0] wr_popping_gray_cntr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 324:36:@1298.4 NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 331:26:@1307.4]
  wire  wr_popping; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 325:45:@1299.4]
  reg  wr_busy_int; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 110:81:@1150.4]
  reg [31:0] _RAND_2;
  wire  _T_47; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 111:34:@1151.4]
  wire  ram_we; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 111:31:@1152.4]
  reg [1:0] wr_count; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 113:78:@1156.4]
  reg [31:0] _RAND_3;
  wire [2:0] _T_58; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 116:66:@1161.4]
  wire [1:0] _T_59; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 116:66:@1162.4]
  wire [1:0] wr_count_next_no_wr_popping; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 116:42:@1163.4]
  wire  wr_count_next_no_wr_popping_is_2; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 119:74:@1165.4]
  wire  wr_count_next_is_2; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 120:33:@1166.4]
  wire [2:0] _T_54; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 115:74:@1157.4]
  wire [2:0] _T_55; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 115:74:@1158.4]
  wire [1:0] _T_56; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 115:74:@1159.4]
  wire [1:0] wr_count_next_wr_popping; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 115:39:@1160.4]
  wire [1:0] wr_count_next; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 117:28:@1164.4]
  wire  _T_38; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 100:50:@1137.4]
  wire  _T_39; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 100:68:@1138.4]
  wire  wr_busy_in_next_wr_req_eq_0; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 100:66:@1139.4]
  wire  wr_busy_in_next; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 101:30:@1140.4]
  wire  wr_busy_in_int; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 129:33:@1173.4]
  wire  _T_41; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 105:10:@1143.4]
  wire  _T_43; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 106:31:@1146.6]
  wire  _GEN_0; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 105:26:@1144.4]
  wire  _T_69; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 132:24:@1176.4]
  wire [1:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 132:38:@1177.4]
  reg  wr_adr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 140:76:@1181.4]
  reg [31:0] _RAND_4;
  wire [1:0] _T_74; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 145:30:@1182.4]
  wire  wr_adr_next; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 145:30:@1183.4]
  wire  _GEN_2; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 146:23:@1184.4]
  wire  _T_75; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 150:54:@1187.4]
  reg  rd_adr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 150:76:@1188.4]
  reg [31:0] _RAND_5;
  wire [1:0] _T_81; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 171:38:@1203.4]
  wire  rd_adr_next_popping; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 171:38:@1204.4]
  reg [1:0] rd_count_p; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 340:81:@1318.4]
  reg [31:0] _RAND_6;
  wire  _T_125; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 345:28:@1327.4]
  wire [1:0] wr_pushing_gray_cntr_sync; // @[Cat.scala 30:58:@1244.4]
  wire [1:0] rd_pushing_gray_cntr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 269:36:@1245.4 NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 276:26:@1253.4]
  wire  rd_pushing; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 270:48:@1246.4]
  wire  rd_req_p; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 345:36:@1328.4]
  reg  rd_req_int; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 337:80:@1310.4]
  reg [31:0] _RAND_7;
  wire  _GEN_3; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 172:21:@1205.4]
  wire [2:0] _T_118; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 341:76:@1319.4]
  wire [2:0] _T_119; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 341:76:@1320.4]
  wire [1:0] _T_120; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 341:76:@1321.4]
  wire [1:0] rd_count_p_next_rd_popping; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 341:41:@1322.4]
  wire [2:0] _T_122; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 342:67:@1323.4]
  wire [1:0] _T_123; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 342:67:@1324.4]
  wire [1:0] rd_count_p_next_no_rd_popping; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 342:44:@1325.4]
  wire [1:0] rd_count_p_next; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 344:30:@1326.4]
  wire  _T_127; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 347:22:@1330.4]
  wire [1:0] _GEN_4; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 347:37:@1331.4]
  reg [33:0] nv_AFIFO_rd_data; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 351:83:@1335.4]
  reg [63:0] _RAND_8;
  NV_CLK_gate_power wr_clk_wr_dft_mgate ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 38:37:@1104.4]
    .io_clk(wr_clk_wr_dft_mgate_io_clk),
    .io_clk_gated(wr_clk_wr_dft_mgate_io_clk_gated)
  );
  NV_CLK_gate_power rd_clk_rd_dft_mgate ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 53:37:@1112.4]
    .io_clk(rd_clk_rd_dft_mgate_io_clk),
    .io_clk_gated(rd_clk_rd_dft_mgate_io_clk_gated)
  );
  NV_CLK_gate_power wr_clk_wr_mgate ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 76:33:@1118.4]
    .io_clk(wr_clk_wr_mgate_io_clk),
    .io_clk_gated(wr_clk_wr_mgate_io_clk_gated)
  );
  NV_CLK_gate_power rd_clk_rd_mgate ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 82:33:@1124.4]
    .io_clk(rd_clk_rd_mgate_io_clk),
    .io_clk_gated(rd_clk_rd_mgate_io_clk_gated)
  );
  nv_flopram_1 ram ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 158:21:@1191.4]
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
  NV_CLK_gate_power wr_clk_wr_mgated_snd_gate ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 231:43:@1209.4]
    .io_clk(wr_clk_wr_mgated_snd_gate_io_clk),
    .io_clk_gated(wr_clk_wr_mgated_snd_gate_io_clk_gated)
  );
  NV_NVDLA_CSB_MASTER_csb2falcon_fifo_gray_cntr_strict wr_pushing_gray ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 237:33:@1215.4]
    .io_inc(wr_pushing_gray_io_inc),
    .io_gray(wr_pushing_gray_io_gray),
    .io_gray_next(wr_pushing_gray_io_gray_next)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_wr_pushing_sync0 ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 244:43:@1220.4]
    .io_SRC_D_NEXT(nv_AFIFO_wr_pushing_sync0_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_wr_pushing_sync0_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_wr_pushing_sync0_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_wr_pushing_sync0_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_wr_pushing_sync0_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_wr_pushing_sync0_io_SRC_D),
    .io_DST_Q(nv_AFIFO_wr_pushing_sync0_io_DST_Q)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_wr_pushing_sync1 ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 255:43:@1231.4]
    .io_SRC_D_NEXT(nv_AFIFO_wr_pushing_sync1_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_wr_pushing_sync1_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_wr_pushing_sync1_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_wr_pushing_sync1_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_wr_pushing_sync1_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_wr_pushing_sync1_io_SRC_D),
    .io_DST_Q(nv_AFIFO_wr_pushing_sync1_io_DST_Q)
  );
  NV_NVDLA_CSB_MASTER_csb2falcon_fifo_gray_cntr rd_pushing_gray ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 272:33:@1247.4]
    .io_clk(rd_pushing_gray_io_clk),
    .io_reset_(rd_pushing_gray_io_reset_),
    .io_inc(rd_pushing_gray_io_inc),
    .io_gray(rd_pushing_gray_io_gray)
  );
  NV_CLK_gate_power rd_clk_rd_mgated_snd_gate ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 279:43:@1254.4]
    .io_clk(rd_clk_rd_mgated_snd_gate_io_clk),
    .io_clk_gated(rd_clk_rd_mgated_snd_gate_io_clk_gated)
  );
  NV_CLK_gate_power wr_clk_rcv_gate ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 284:33:@1260.4]
    .io_clk(wr_clk_rcv_gate_io_clk),
    .io_clk_gated(wr_clk_rcv_gate_io_clk_gated)
  );
  NV_NVDLA_CSB_MASTER_csb2falcon_fifo_gray_cntr_strict rd_popping_gray ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 292:33:@1268.4]
    .io_inc(rd_popping_gray_io_inc),
    .io_gray(rd_popping_gray_io_gray),
    .io_gray_next(rd_popping_gray_io_gray_next)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_rd_popping_sync0 ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 299:43:@1273.4]
    .io_SRC_D_NEXT(nv_AFIFO_rd_popping_sync0_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_rd_popping_sync0_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_rd_popping_sync0_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_rd_popping_sync0_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_rd_popping_sync0_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_rd_popping_sync0_io_SRC_D),
    .io_DST_Q(nv_AFIFO_rd_popping_sync0_io_DST_Q)
  );
  p_STRICTSYNC3DOTM_C_PPP nv_AFIFO_rd_popping_sync1 ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 310:43:@1284.4]
    .io_SRC_D_NEXT(nv_AFIFO_rd_popping_sync1_io_SRC_D_NEXT),
    .io_SRC_CLK(nv_AFIFO_rd_popping_sync1_io_SRC_CLK),
    .io_SRC_CLRN(nv_AFIFO_rd_popping_sync1_io_SRC_CLRN),
    .io_DST_CLK(nv_AFIFO_rd_popping_sync1_io_DST_CLK),
    .io_DST_CLRN(nv_AFIFO_rd_popping_sync1_io_DST_CLRN),
    .io_SRC_D(nv_AFIFO_rd_popping_sync1_io_SRC_D),
    .io_DST_Q(nv_AFIFO_rd_popping_sync1_io_DST_Q)
  );
  NV_NVDLA_CSB_MASTER_csb2falcon_fifo_gray_cntr wr_popping_gray ( // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 327:33:@1301.4]
    .io_clk(wr_popping_gray_io_clk),
    .io_reset_(wr_popping_gray_io_reset_),
    .io_inc(wr_popping_gray_io_inc),
    .io_gray(wr_popping_gray_io_gray)
  );
  assign _T_30 = ~ io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 93:58:@1130.4]
  assign _T_36 = ~ wr_busy_in; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 95:20:@1134.4]
  assign rd_popping_gray_cntr_sync = {nv_AFIFO_rd_popping_sync1_io_DST_Q,nv_AFIFO_rd_popping_sync0_io_DST_Q}; // @[Cat.scala 30:58:@1297.4]
  assign wr_popping_gray_cntr = wr_popping_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 324:36:@1298.4 NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 331:26:@1307.4]
  assign wr_popping = rd_popping_gray_cntr_sync != wr_popping_gray_cntr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 325:45:@1299.4]
  assign _T_47 = ~ wr_busy_int; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 111:34:@1151.4]
  assign ram_we = wr_req_in & _T_47; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 111:31:@1152.4]
  assign _T_58 = wr_count + 2'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 116:66:@1161.4]
  assign _T_59 = wr_count + 2'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 116:66:@1162.4]
  assign wr_count_next_no_wr_popping = ram_we ? _T_59 : wr_count; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 116:42:@1163.4]
  assign wr_count_next_no_wr_popping_is_2 = wr_count_next_no_wr_popping == 2'h2; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 119:74:@1165.4]
  assign wr_count_next_is_2 = wr_popping ? 1'h0 : wr_count_next_no_wr_popping_is_2; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 120:33:@1166.4]
  assign _T_54 = wr_count - 2'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 115:74:@1157.4]
  assign _T_55 = $unsigned(_T_54); // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 115:74:@1158.4]
  assign _T_56 = _T_55[1:0]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 115:74:@1159.4]
  assign wr_count_next_wr_popping = ram_we ? wr_count : _T_56; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 115:39:@1160.4]
  assign wr_count_next = wr_popping ? wr_count_next_wr_popping : wr_count_next_no_wr_popping; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 117:28:@1164.4]
  assign _T_38 = wr_req_in & wr_count_next_is_2; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 100:50:@1137.4]
  assign _T_39 = ~ ram_we; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 100:68:@1138.4]
  assign wr_busy_in_next_wr_req_eq_0 = _T_38 & _T_39; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 100:66:@1139.4]
  assign wr_busy_in_next = io_wr_req ? wr_count_next_is_2 : wr_busy_in_next_wr_req_eq_0; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 101:30:@1140.4]
  assign wr_busy_in_int = wr_req_in & wr_busy_int; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 129:33:@1173.4]
  assign _T_41 = ~ wr_busy_in_int; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 105:10:@1143.4]
  assign _T_43 = io_wr_req & _T_36; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 106:31:@1146.6]
  assign _GEN_0 = _T_41 ? _T_43 : wr_req_in; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 105:26:@1144.4]
  assign _T_69 = ram_we ^ wr_popping; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 132:24:@1176.4]
  assign _GEN_1 = _T_69 ? wr_count_next : wr_count; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 132:38:@1177.4]
  assign _T_74 = wr_adr + 1'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 145:30:@1182.4]
  assign wr_adr_next = wr_adr + 1'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 145:30:@1183.4]
  assign _GEN_2 = ram_we ? wr_adr_next : wr_adr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 146:23:@1184.4]
  assign _T_75 = ~ io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 150:54:@1187.4]
  assign _T_81 = rd_adr + 1'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 171:38:@1203.4]
  assign rd_adr_next_popping = rd_adr + 1'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 171:38:@1204.4]
  assign _T_125 = rd_count_p != 2'h0; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 345:28:@1327.4]
  assign wr_pushing_gray_cntr_sync = {nv_AFIFO_wr_pushing_sync1_io_DST_Q,nv_AFIFO_wr_pushing_sync0_io_DST_Q}; // @[Cat.scala 30:58:@1244.4]
  assign rd_pushing_gray_cntr = rd_pushing_gray_io_gray; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 269:36:@1245.4 NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 276:26:@1253.4]
  assign rd_pushing = wr_pushing_gray_cntr_sync != rd_pushing_gray_cntr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 270:48:@1246.4]
  assign rd_req_p = _T_125 | rd_pushing; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 345:36:@1328.4]
  assign _GEN_3 = rd_req_p ? rd_adr_next_popping : rd_adr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 172:21:@1205.4]
  assign _T_118 = rd_count_p - 2'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 341:76:@1319.4]
  assign _T_119 = $unsigned(_T_118); // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 341:76:@1320.4]
  assign _T_120 = _T_119[1:0]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 341:76:@1321.4]
  assign rd_count_p_next_rd_popping = rd_pushing ? rd_count_p : _T_120; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 341:41:@1322.4]
  assign _T_122 = rd_count_p + 2'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 342:67:@1323.4]
  assign _T_123 = rd_count_p + 2'h1; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 342:67:@1324.4]
  assign rd_count_p_next_no_rd_popping = rd_pushing ? _T_123 : rd_count_p; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 342:44:@1325.4]
  assign rd_count_p_next = rd_req_p ? rd_count_p_next_rd_popping : rd_count_p_next_no_rd_popping; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 344:30:@1326.4]
  assign _T_127 = rd_pushing | rd_req_p; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 347:22:@1330.4]
  assign _GEN_4 = _T_127 ? rd_count_p_next : rd_count_p; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 347:37:@1331.4]
  assign io_rd_req = rd_req_int; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 338:15:@1311.4]
  assign io_rd_data = nv_AFIFO_rd_data; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 359:16:@1343.4]
  assign wr_clk_wr_dft_mgate_io_clk = io_wr_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 39:32:@1107.4]
  assign rd_clk_rd_dft_mgate_io_clk = io_rd_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 54:32:@1115.4]
  assign wr_clk_wr_mgate_io_clk = io_wr_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 77:28:@1121.4]
  assign rd_clk_rd_mgate_io_clk = io_rd_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 83:28:@1127.4]
  assign ram_reset = reset; // @[:@1193.4]
  assign ram_io_clk = wr_clk_wr_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 159:16:@1194.4]
  assign ram_io_clk_mgated = wr_clk_wr_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 160:27:@1195.4]
  assign ram_io_di = io_wr_data; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 162:15:@1197.4]
  assign ram_io_iwe = _T_36 & io_wr_req; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 163:20:@1198.4]
  assign ram_io_we = wr_req_in & _T_47; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 164:15:@1199.4]
  assign ram_io_wa = wr_adr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 165:19:@1200.4]
  assign ram_io_ra = {{1'd0}, rd_adr}; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 166:15:@1201.4]
  assign wr_clk_wr_mgated_snd_gate_io_clk = io_wr_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 232:38:@1212.4]
  assign wr_pushing_gray_io_inc = wr_req_in & _T_47; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 239:36:@1218.4]
  assign wr_pushing_gray_io_gray = {nv_AFIFO_wr_pushing_sync1_io_SRC_D,nv_AFIFO_wr_pushing_sync0_io_SRC_D}; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 241:29:@1219.4]
  assign nv_AFIFO_wr_pushing_sync0_io_SRC_D_NEXT = wr_pushing_gray_io_gray_next[0]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 246:45:@1225.4]
  assign nv_AFIFO_wr_pushing_sync0_io_SRC_CLK = wr_clk_wr_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 245:42:@1223.4]
  assign nv_AFIFO_wr_pushing_sync0_io_SRC_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 250:43:@1229.4]
  assign nv_AFIFO_wr_pushing_sync0_io_DST_CLK = rd_clk_rd_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 247:42:@1226.4]
  assign nv_AFIFO_wr_pushing_sync0_io_DST_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 251:43:@1230.4]
  assign nv_AFIFO_wr_pushing_sync1_io_SRC_D_NEXT = wr_pushing_gray_io_gray_next[1]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 257:45:@1236.4]
  assign nv_AFIFO_wr_pushing_sync1_io_SRC_CLK = wr_clk_wr_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 256:42:@1234.4]
  assign nv_AFIFO_wr_pushing_sync1_io_SRC_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 261:43:@1240.4]
  assign nv_AFIFO_wr_pushing_sync1_io_DST_CLK = rd_clk_rd_dft_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 258:42:@1237.4]
  assign nv_AFIFO_wr_pushing_sync1_io_DST_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 262:43:@1241.4]
  assign rd_pushing_gray_io_clk = rd_clk_rd_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 273:28:@1250.4]
  assign rd_pushing_gray_io_reset_ = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 274:31:@1251.4]
  assign rd_pushing_gray_io_inc = wr_pushing_gray_cntr_sync != rd_pushing_gray_cntr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 275:28:@1252.4]
  assign rd_clk_rd_mgated_snd_gate_io_clk = io_rd_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 280:38:@1257.4]
  assign wr_clk_rcv_gate_io_clk = io_wr_clk; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 285:28:@1263.4]
  assign rd_popping_gray_io_inc = _T_125 | rd_pushing; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 294:36:@1271.4]
  assign rd_popping_gray_io_gray = {nv_AFIFO_rd_popping_sync1_io_SRC_D,nv_AFIFO_rd_popping_sync0_io_SRC_D}; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 296:29:@1272.4]
  assign nv_AFIFO_rd_popping_sync0_io_SRC_D_NEXT = rd_popping_gray_io_gray_next[0]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 301:45:@1278.4]
  assign nv_AFIFO_rd_popping_sync0_io_SRC_CLK = rd_clk_rd_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 300:42:@1276.4]
  assign nv_AFIFO_rd_popping_sync0_io_SRC_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 305:43:@1282.4]
  assign nv_AFIFO_rd_popping_sync0_io_DST_CLK = wr_clk_rcv_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 302:42:@1279.4]
  assign nv_AFIFO_rd_popping_sync0_io_DST_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 306:43:@1283.4]
  assign nv_AFIFO_rd_popping_sync1_io_SRC_D_NEXT = rd_popping_gray_io_gray_next[1]; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 312:45:@1289.4]
  assign nv_AFIFO_rd_popping_sync1_io_SRC_CLK = rd_clk_rd_mgated_snd_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 311:42:@1287.4]
  assign nv_AFIFO_rd_popping_sync1_io_SRC_CLRN = io_rd_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 316:43:@1293.4]
  assign nv_AFIFO_rd_popping_sync1_io_DST_CLK = wr_clk_rcv_gate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 313:42:@1290.4]
  assign nv_AFIFO_rd_popping_sync1_io_DST_CLRN = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 317:43:@1294.4]
  assign wr_popping_gray_io_clk = wr_clk_wr_mgate_io_clk_gated; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 328:28:@1304.4]
  assign wr_popping_gray_io_reset_ = io_wr_reset_; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 329:31:@1305.4]
  assign wr_popping_gray_io_inc = rd_popping_gray_cntr_sync != wr_popping_gray_cntr; // @[NV_NVDLA_CSB_MASTER_csb2falcon_fifo.scala 330:28:@1306.4]
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
  wr_req_in = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  wr_busy_in = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  wr_busy_int = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  wr_count = _RAND_3[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  wr_adr = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  rd_adr = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  rd_count_p = _RAND_6[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  rd_req_int = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {2{`RANDOM}};
  nv_AFIFO_rd_data = _RAND_8[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge wr_clk_wr_dft_mgate_io_clk_gated) begin
    if (_T_30) begin
      wr_req_in <= 1'h0;
    end else begin
      if (_T_41) begin
        wr_req_in <= _T_43;
      end
    end
    if (_T_30) begin
      wr_busy_in <= 1'h0;
    end else begin
      if (io_wr_req) begin
        if (wr_popping) begin
          wr_busy_in <= 1'h0;
        end else begin
          wr_busy_in <= wr_count_next_no_wr_popping_is_2;
        end
      end else begin
        wr_busy_in <= wr_busy_in_next_wr_req_eq_0;
      end
    end
  end
  always @(posedge wr_clk_wr_mgate_io_clk_gated) begin
    if (_T_30) begin
      wr_busy_int <= 1'h0;
    end else begin
      if (wr_popping) begin
        wr_busy_int <= 1'h0;
      end else begin
        wr_busy_int <= wr_count_next_no_wr_popping_is_2;
      end
    end
    if (_T_30) begin
      wr_count <= 2'h0;
    end else begin
      if (_T_69) begin
        if (wr_popping) begin
          if (!(ram_we)) begin
            wr_count <= _T_56;
          end
        end else begin
          if (ram_we) begin
            wr_count <= _T_59;
          end
        end
      end
    end
    if (_T_30) begin
      wr_adr <= 1'h0;
    end else begin
      if (ram_we) begin
        wr_adr <= wr_adr_next;
      end
    end
  end
  always @(posedge rd_clk_rd_mgate_io_clk_gated) begin
    if (_T_75) begin
      rd_adr <= 1'h0;
    end else begin
      if (rd_req_p) begin
        rd_adr <= rd_adr_next_popping;
      end
    end
    if (_T_75) begin
      rd_count_p <= 2'h0;
    end else begin
      if (_T_127) begin
        if (rd_req_p) begin
          if (!(rd_pushing)) begin
            rd_count_p <= _T_120;
          end
        end else begin
          if (rd_pushing) begin
            rd_count_p <= _T_123;
          end
        end
      end
    end
    if (_T_75) begin
      rd_req_int <= 1'h0;
    end else begin
      rd_req_int <= rd_req_p;
    end
    if (rd_req_p) begin
      nv_AFIFO_rd_data <= ram_io_dout;
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client( // @[:@1361.2]
  input         reset, // @[:@1363.4]
  input         io_nvdla_core_clk, // @[:@1364.4]
  input         io_core_req_pop_valid, // @[:@1364.4]
  input  [17:0] io_core_byte_addr, // @[:@1364.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1364.4]
  output        io_csb2client_req_valid, // @[:@1364.4]
  output [62:0] io_csb2client_req_bits, // @[:@1364.4]
  input         io_csb2client_resp_valid, // @[:@1364.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1364.4]
  output        io_client_resp_pd_valid, // @[:@1364.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1364.4]
  output        io_select_client // @[:@1364.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1366.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1367.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1368.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1369.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1370.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1371.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1372.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1374.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1378.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1381.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1382.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1383.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1384.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1388.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1371.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1372.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1374.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1378.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1381.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1382.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1383.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1384.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1388.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1391.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1386.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1392.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1393.4]
  assign io_select_client = _GEN_2 == 32'h0; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1373.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_1( // @[:@1395.2]
  input         reset, // @[:@1397.4]
  input         io_nvdla_core_clk, // @[:@1398.4]
  input         io_core_req_pop_valid, // @[:@1398.4]
  input  [17:0] io_core_byte_addr, // @[:@1398.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1398.4]
  output        io_csb2client_req_valid, // @[:@1398.4]
  output [62:0] io_csb2client_req_bits, // @[:@1398.4]
  input         io_csb2client_resp_valid, // @[:@1398.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1398.4]
  output        io_client_resp_pd_valid, // @[:@1398.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1398.4]
  output        io_select_client // @[:@1398.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1400.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1401.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1402.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1403.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1404.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1405.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1406.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1408.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1412.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1415.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1416.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1417.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1418.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1422.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1405.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1406.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1408.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1412.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1415.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1416.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1417.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1418.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1422.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1425.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1420.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1426.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1427.4]
  assign io_select_client = _GEN_2 == 32'h1000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1407.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_2( // @[:@1429.2]
  input         reset, // @[:@1431.4]
  input         io_nvdla_core_clk, // @[:@1432.4]
  input         io_core_req_pop_valid, // @[:@1432.4]
  input  [17:0] io_core_byte_addr, // @[:@1432.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1432.4]
  output        io_csb2client_req_valid, // @[:@1432.4]
  output [62:0] io_csb2client_req_bits, // @[:@1432.4]
  input         io_csb2client_resp_valid, // @[:@1432.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1432.4]
  output        io_client_resp_pd_valid, // @[:@1432.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1432.4]
  output        io_select_client // @[:@1432.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1434.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1435.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1436.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1437.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1438.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1439.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1440.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1442.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1446.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1449.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1450.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1451.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1452.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1456.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1439.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1440.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1442.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1446.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1449.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1450.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1451.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1452.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1456.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1459.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1454.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1460.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1461.4]
  assign io_select_client = _GEN_2 == 32'h2000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1441.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_3( // @[:@1463.2]
  input         reset, // @[:@1465.4]
  input         io_nvdla_core_clk, // @[:@1466.4]
  input         io_core_req_pop_valid, // @[:@1466.4]
  input  [17:0] io_core_byte_addr, // @[:@1466.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1466.4]
  output        io_csb2client_req_valid, // @[:@1466.4]
  output [62:0] io_csb2client_req_bits, // @[:@1466.4]
  input         io_csb2client_resp_valid, // @[:@1466.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1466.4]
  output        io_client_resp_pd_valid, // @[:@1466.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1466.4]
  output        io_select_client // @[:@1466.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1468.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1469.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1470.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1471.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1472.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1473.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1474.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1476.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1480.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1483.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1484.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1485.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1486.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1490.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1473.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1474.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1476.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1480.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1483.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1484.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1485.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1486.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1490.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1493.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1488.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1494.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1495.4]
  assign io_select_client = _GEN_2 == 32'h3000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1475.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_4( // @[:@1497.2]
  input         reset, // @[:@1499.4]
  input         io_nvdla_core_clk, // @[:@1500.4]
  input         io_core_req_pop_valid, // @[:@1500.4]
  input  [17:0] io_core_byte_addr, // @[:@1500.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1500.4]
  output        io_csb2client_req_valid, // @[:@1500.4]
  output [62:0] io_csb2client_req_bits, // @[:@1500.4]
  input         io_csb2client_resp_valid, // @[:@1500.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1500.4]
  output        io_client_resp_pd_valid, // @[:@1500.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1500.4]
  output        io_select_client // @[:@1500.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1502.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1503.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1504.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1505.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1506.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1507.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1508.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1510.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1514.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1517.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1518.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1519.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1520.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1524.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1507.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1508.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1510.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1514.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1517.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1518.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1519.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1520.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1524.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1527.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1522.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1528.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1529.4]
  assign io_select_client = _GEN_2 == 32'h4000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1509.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_5( // @[:@1531.2]
  input         reset, // @[:@1533.4]
  input         io_nvdla_core_clk, // @[:@1534.4]
  input         io_core_req_pop_valid, // @[:@1534.4]
  input  [17:0] io_core_byte_addr, // @[:@1534.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1534.4]
  output        io_csb2client_req_valid, // @[:@1534.4]
  output [62:0] io_csb2client_req_bits, // @[:@1534.4]
  input         io_csb2client_resp_valid, // @[:@1534.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1534.4]
  output        io_client_resp_pd_valid, // @[:@1534.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1534.4]
  output        io_select_client // @[:@1534.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1536.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1537.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1538.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1539.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1540.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1541.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1542.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1544.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1548.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1551.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1552.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1553.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1554.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1558.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1541.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1542.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1544.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1548.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1551.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1552.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1553.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1554.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1558.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1561.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1556.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1562.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1563.4]
  assign io_select_client = _GEN_2 == 32'h5000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1543.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_6( // @[:@1565.2]
  input         reset, // @[:@1567.4]
  input         io_nvdla_core_clk, // @[:@1568.4]
  input         io_core_req_pop_valid, // @[:@1568.4]
  input  [17:0] io_core_byte_addr, // @[:@1568.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1568.4]
  output        io_csb2client_req_valid, // @[:@1568.4]
  output [62:0] io_csb2client_req_bits, // @[:@1568.4]
  input         io_csb2client_resp_valid, // @[:@1568.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1568.4]
  output        io_client_resp_pd_valid, // @[:@1568.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1568.4]
  output        io_select_client // @[:@1568.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1570.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1571.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1572.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1573.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1574.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1575.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1576.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1578.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1582.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1585.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1586.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1587.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1588.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1592.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1575.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1576.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1578.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1582.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1585.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1586.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1587.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1588.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1592.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1595.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1590.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1596.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1597.4]
  assign io_select_client = _GEN_2 == 32'h6000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1577.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_7( // @[:@1599.2]
  input         reset, // @[:@1601.4]
  input         io_nvdla_core_clk, // @[:@1602.4]
  input         io_core_req_pop_valid, // @[:@1602.4]
  input  [17:0] io_core_byte_addr, // @[:@1602.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1602.4]
  output        io_csb2client_req_valid, // @[:@1602.4]
  output [62:0] io_csb2client_req_bits, // @[:@1602.4]
  input         io_csb2client_resp_valid, // @[:@1602.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1602.4]
  output        io_client_resp_pd_valid, // @[:@1602.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1602.4]
  output        io_select_client // @[:@1602.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1604.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1605.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1606.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1607.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1608.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1609.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1610.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1612.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1616.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1619.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1620.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1621.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1622.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1626.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1609.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1610.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1612.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1616.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1619.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1620.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1621.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1622.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1626.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1629.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1624.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1630.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1631.4]
  assign io_select_client = _GEN_2 == 32'h7000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1611.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_8( // @[:@1633.2]
  input         reset, // @[:@1635.4]
  input         io_nvdla_core_clk, // @[:@1636.4]
  input         io_core_req_pop_valid, // @[:@1636.4]
  input  [17:0] io_core_byte_addr, // @[:@1636.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1636.4]
  output        io_csb2client_req_valid, // @[:@1636.4]
  output [62:0] io_csb2client_req_bits, // @[:@1636.4]
  input         io_csb2client_resp_valid, // @[:@1636.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1636.4]
  output        io_client_resp_pd_valid, // @[:@1636.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1636.4]
  output        io_select_client // @[:@1636.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1638.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1639.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1640.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1641.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1642.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1643.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1644.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1646.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1650.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1653.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1654.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1655.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1656.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1660.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1643.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1644.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1646.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1650.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1653.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1654.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1655.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1656.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1660.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1663.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1658.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1664.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1665.4]
  assign io_select_client = _GEN_2 == 32'h8000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1645.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_9( // @[:@1667.2]
  input         reset, // @[:@1669.4]
  input         io_nvdla_core_clk, // @[:@1670.4]
  input         io_core_req_pop_valid, // @[:@1670.4]
  input  [17:0] io_core_byte_addr, // @[:@1670.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1670.4]
  output        io_csb2client_req_valid, // @[:@1670.4]
  output [62:0] io_csb2client_req_bits, // @[:@1670.4]
  input         io_csb2client_resp_valid, // @[:@1670.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1670.4]
  output        io_client_resp_pd_valid, // @[:@1670.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1670.4]
  output        io_select_client // @[:@1670.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1672.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1673.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1674.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1675.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1676.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1677.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1678.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1680.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1684.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1687.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1688.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1689.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1690.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1694.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1677.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1678.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1680.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1684.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1687.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1688.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1689.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1690.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1694.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1697.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1692.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1698.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1699.4]
  assign io_select_client = _GEN_2 == 32'h9000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1679.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_10( // @[:@1701.2]
  input         reset, // @[:@1703.4]
  input         io_nvdla_core_clk, // @[:@1704.4]
  input         io_core_req_pop_valid, // @[:@1704.4]
  input  [17:0] io_core_byte_addr, // @[:@1704.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1704.4]
  output        io_csb2client_req_valid, // @[:@1704.4]
  output [62:0] io_csb2client_req_bits, // @[:@1704.4]
  input         io_csb2client_resp_valid, // @[:@1704.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1704.4]
  output        io_client_resp_pd_valid, // @[:@1704.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1704.4]
  output        io_select_client // @[:@1704.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1706.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1707.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1708.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1709.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1710.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1711.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1712.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1714.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1718.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1721.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1722.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1723.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1724.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1728.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1711.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1712.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1714.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1718.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1721.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1722.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1723.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1724.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1728.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1731.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1726.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1732.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1733.4]
  assign io_select_client = _GEN_2 == 32'ha000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1713.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_11( // @[:@1735.2]
  input         reset, // @[:@1737.4]
  input         io_nvdla_core_clk, // @[:@1738.4]
  input         io_core_req_pop_valid, // @[:@1738.4]
  input  [17:0] io_core_byte_addr, // @[:@1738.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1738.4]
  output        io_csb2client_req_valid, // @[:@1738.4]
  output [62:0] io_csb2client_req_bits, // @[:@1738.4]
  input         io_csb2client_resp_valid, // @[:@1738.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1738.4]
  output        io_client_resp_pd_valid, // @[:@1738.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1738.4]
  output        io_select_client // @[:@1738.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1740.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1741.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1742.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1743.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1744.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1745.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1746.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1748.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1752.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1755.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1756.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1757.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1758.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1762.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1745.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1746.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1748.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1752.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1755.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1756.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1757.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1758.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1762.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1765.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1760.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1766.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1767.4]
  assign io_select_client = _GEN_2 == 32'hb000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1747.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_12( // @[:@1769.2]
  input         reset, // @[:@1771.4]
  input         io_nvdla_core_clk, // @[:@1772.4]
  input         io_core_req_pop_valid, // @[:@1772.4]
  input  [17:0] io_core_byte_addr, // @[:@1772.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1772.4]
  output        io_csb2client_req_valid, // @[:@1772.4]
  output [62:0] io_csb2client_req_bits, // @[:@1772.4]
  input         io_csb2client_resp_valid, // @[:@1772.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1772.4]
  output        io_client_resp_pd_valid, // @[:@1772.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1772.4]
  output        io_select_client // @[:@1772.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1774.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1775.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1776.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1777.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1778.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1779.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1780.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1782.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1786.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1789.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1790.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1791.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1792.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1796.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1779.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1780.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1782.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1786.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1789.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1790.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1791.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1792.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1796.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1799.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1794.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1800.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1801.4]
  assign io_select_client = _GEN_2 == 32'hc000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1781.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_CSB_MASTER_for_client_13( // @[:@1803.2]
  input         reset, // @[:@1805.4]
  input         io_nvdla_core_clk, // @[:@1806.4]
  input         io_core_req_pop_valid, // @[:@1806.4]
  input  [17:0] io_core_byte_addr, // @[:@1806.4]
  input  [49:0] io_core_req_pd_d1, // @[:@1806.4]
  output        io_csb2client_req_valid, // @[:@1806.4]
  output [62:0] io_csb2client_req_bits, // @[:@1806.4]
  input         io_csb2client_resp_valid, // @[:@1806.4]
  input  [33:0] io_csb2client_resp_bits, // @[:@1806.4]
  output        io_client_resp_pd_valid, // @[:@1806.4]
  output [49:0] io_client_resp_pd_bits, // @[:@1806.4]
  output        io_select_client // @[:@1806.4]
);
  reg  _T_57; // @[NV_NVDLA_CSB_MASTER_helper.scala 48:34:@1808.4]
  reg [31:0] _RAND_0;
  reg  _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 49:42:@1809.4]
  reg [31:0] _RAND_1;
  reg [49:0] _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 50:84:@1810.4]
  reg [63:0] _RAND_2;
  reg  _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 51:40:@1811.4]
  reg [31:0] _RAND_3;
  reg [33:0] _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 52:81:@1812.4]
  reg [63:0] _RAND_4;
  wire [17:0] _T_70; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1813.4]
  wire [31:0] _GEN_2; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1814.4]
  wire  _T_73; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1816.4]
  wire [49:0] _GEN_0; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1820.4]
  wire [33:0] _T_78; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1823.4]
  wire [15:0] _T_80; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1824.4]
  wire [21:0] _T_81; // @[Cat.scala 30:58:@1825.4]
  wire [40:0] _T_82; // @[Cat.scala 30:58:@1826.4]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1830.4]
  assign _T_70 = io_core_byte_addr & 18'h3f000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:45:@1813.4]
  assign _GEN_2 = {{14'd0}, _T_70}; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:61:@1814.4]
  assign _T_73 = io_core_req_pop_valid & io_select_client; // @[NV_NVDLA_CSB_MASTER_helper.scala 55:55:@1816.4]
  assign _GEN_0 = _T_57 ? io_core_req_pd_d1 : _T_63; // @[NV_NVDLA_CSB_MASTER_helper.scala 62:28:@1820.4]
  assign _T_78 = _T_63[49:16]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:74:@1823.4]
  assign _T_80 = _T_63[15:0]; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:123:@1824.4]
  assign _T_81 = {6'h0,_T_80}; // @[Cat.scala 30:58:@1825.4]
  assign _T_82 = {7'h0,_T_78}; // @[Cat.scala 30:58:@1826.4]
  assign _GEN_1 = io_csb2client_resp_valid ? io_csb2client_resp_bits : _T_69; // @[NV_NVDLA_CSB_MASTER_helper.scala 67:35:@1830.4]
  assign io_csb2client_req_valid = _T_60; // @[NV_NVDLA_CSB_MASTER_helper.scala 71:29:@1833.4]
  assign io_csb2client_req_bits = {_T_82,_T_81}; // @[NV_NVDLA_CSB_MASTER_helper.scala 65:28:@1828.4]
  assign io_client_resp_pd_valid = _T_66; // @[NV_NVDLA_CSB_MASTER_helper.scala 72:29:@1834.4]
  assign io_client_resp_pd_bits = {{16'd0}, _T_69}; // @[NV_NVDLA_CSB_MASTER_helper.scala 73:28:@1835.4]
  assign io_select_client = _GEN_2 == 32'hd000; // @[NV_NVDLA_CSB_MASTER_helper.scala 54:22:@1815.4]
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
  _T_57 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_60 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  _T_63 = _RAND_2[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_66 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {2{`RANDOM}};
  _T_69 = _RAND_4[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      _T_57 <= 1'h0;
    end else begin
      _T_57 <= _T_73;
    end
    if (reset) begin
      _T_60 <= 1'h0;
    end else begin
      _T_60 <= _T_57;
    end
    if (reset) begin
      _T_63 <= 50'h0;
    end else begin
      if (_T_57) begin
        _T_63 <= io_core_req_pd_d1;
      end
    end
    if (reset) begin
      _T_66 <= 1'h0;
    end else begin
      _T_66 <= io_csb2client_resp_valid;
    end
    if (reset) begin
      _T_69 <= 34'h0;
    end else begin
      if (io_csb2client_resp_valid) begin
        _T_69 <= io_csb2client_resp_bits;
      end
    end
  end
endmodule
module NV_NVDLA_csb_master( // @[:@1837.2]
  input         clock, // @[:@1838.4]
  input         reset, // @[:@1839.4]
  input         io_nvdla_core_clk, // @[:@1840.4]
  input         io_nvdla_falcon_clk, // @[:@1840.4]
  input         io_nvdla_core_rstn, // @[:@1840.4]
  input         io_nvdla_falcon_rstn, // @[:@1840.4]
  input  [31:0] io_pwrbus_ram_pd, // @[:@1840.4]
  output        io_csb2nvdla_ready, // @[:@1840.4]
  input         io_csb2nvdla_valid, // @[:@1840.4]
  input  [15:0] io_csb2nvdla_bits_addr, // @[:@1840.4]
  input  [31:0] io_csb2nvdla_bits_wdat, // @[:@1840.4]
  input         io_csb2nvdla_bits_write, // @[:@1840.4]
  input         io_csb2nvdla_bits_nposted, // @[:@1840.4]
  output        io_nvdla2csb_valid, // @[:@1840.4]
  output [31:0] io_nvdla2csb_bits_data, // @[:@1840.4]
  output        io_nvdla2csb_wr_complete, // @[:@1840.4]
  input         io_csb2cfgrom_req_ready, // @[:@1840.4]
  output        io_csb2cfgrom_req_valid, // @[:@1840.4]
  output [62:0] io_csb2cfgrom_req_bits, // @[:@1840.4]
  input         io_csb2cfgrom_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2cfgrom_resp_bits, // @[:@1840.4]
  input         io_csb2glb_req_ready, // @[:@1840.4]
  output        io_csb2glb_req_valid, // @[:@1840.4]
  output [62:0] io_csb2glb_req_bits, // @[:@1840.4]
  input         io_csb2glb_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2glb_resp_bits, // @[:@1840.4]
  input         io_csb2mcif_req_ready, // @[:@1840.4]
  output        io_csb2mcif_req_valid, // @[:@1840.4]
  output [62:0] io_csb2mcif_req_bits, // @[:@1840.4]
  input         io_csb2mcif_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2mcif_resp_bits, // @[:@1840.4]
  input         io_csb2cdma_req_ready, // @[:@1840.4]
  output        io_csb2cdma_req_valid, // @[:@1840.4]
  output [62:0] io_csb2cdma_req_bits, // @[:@1840.4]
  input         io_csb2cdma_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2cdma_resp_bits, // @[:@1840.4]
  input         io_csb2csc_req_ready, // @[:@1840.4]
  output        io_csb2csc_req_valid, // @[:@1840.4]
  output [62:0] io_csb2csc_req_bits, // @[:@1840.4]
  input         io_csb2csc_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2csc_resp_bits, // @[:@1840.4]
  input         io_csb2cmac_a_req_ready, // @[:@1840.4]
  output        io_csb2cmac_a_req_valid, // @[:@1840.4]
  output [62:0] io_csb2cmac_a_req_bits, // @[:@1840.4]
  input         io_csb2cmac_a_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2cmac_a_resp_bits, // @[:@1840.4]
  input         io_csb2cmac_b_req_ready, // @[:@1840.4]
  output        io_csb2cmac_b_req_valid, // @[:@1840.4]
  output [62:0] io_csb2cmac_b_req_bits, // @[:@1840.4]
  input         io_csb2cmac_b_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2cmac_b_resp_bits, // @[:@1840.4]
  input         io_csb2cacc_req_ready, // @[:@1840.4]
  output        io_csb2cacc_req_valid, // @[:@1840.4]
  output [62:0] io_csb2cacc_req_bits, // @[:@1840.4]
  input         io_csb2cacc_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2cacc_resp_bits, // @[:@1840.4]
  input         io_csb2sdp_rdma_req_ready, // @[:@1840.4]
  output        io_csb2sdp_rdma_req_valid, // @[:@1840.4]
  output [62:0] io_csb2sdp_rdma_req_bits, // @[:@1840.4]
  input         io_csb2sdp_rdma_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2sdp_rdma_resp_bits, // @[:@1840.4]
  input         io_csb2sdp_req_ready, // @[:@1840.4]
  output        io_csb2sdp_req_valid, // @[:@1840.4]
  output [62:0] io_csb2sdp_req_bits, // @[:@1840.4]
  input         io_csb2sdp_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2sdp_resp_bits, // @[:@1840.4]
  input         io_csb2pdp_rdma_req_ready, // @[:@1840.4]
  output        io_csb2pdp_rdma_req_valid, // @[:@1840.4]
  output [62:0] io_csb2pdp_rdma_req_bits, // @[:@1840.4]
  input         io_csb2pdp_rdma_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2pdp_rdma_resp_bits, // @[:@1840.4]
  input         io_csb2pdp_req_ready, // @[:@1840.4]
  output        io_csb2pdp_req_valid, // @[:@1840.4]
  output [62:0] io_csb2pdp_req_bits, // @[:@1840.4]
  input         io_csb2pdp_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2pdp_resp_bits, // @[:@1840.4]
  input         io_csb2cdp_rdma_req_ready, // @[:@1840.4]
  output        io_csb2cdp_rdma_req_valid, // @[:@1840.4]
  output [62:0] io_csb2cdp_rdma_req_bits, // @[:@1840.4]
  input         io_csb2cdp_rdma_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2cdp_rdma_resp_bits, // @[:@1840.4]
  input         io_csb2cdp_req_ready, // @[:@1840.4]
  output        io_csb2cdp_req_valid, // @[:@1840.4]
  output [62:0] io_csb2cdp_req_bits, // @[:@1840.4]
  input         io_csb2cdp_resp_valid, // @[:@1840.4]
  input  [33:0] io_csb2cdp_resp_bits // @[:@1840.4]
);
  wire  u_fifo_csb2nvdla_reset; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire  u_fifo_csb2nvdla_io_wr_clk; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire  u_fifo_csb2nvdla_io_rd_clk; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire  u_fifo_csb2nvdla_io_wr_reset_; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire  u_fifo_csb2nvdla_io_rd_reset_; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire  u_fifo_csb2nvdla_io_wr_ready; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire  u_fifo_csb2nvdla_io_wr_req; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire  u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire [49:0] u_fifo_csb2nvdla_io_wr_data; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire [49:0] u_fifo_csb2nvdla_io_rd_data; // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
  wire  u_fifo_nvdla2csb_reset; // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
  wire  u_fifo_nvdla2csb_io_wr_clk; // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
  wire  u_fifo_nvdla2csb_io_wr_reset_; // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
  wire  u_fifo_nvdla2csb_io_rd_clk; // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
  wire  u_fifo_nvdla2csb_io_rd_reset_; // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
  wire  u_fifo_nvdla2csb_io_wr_req; // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
  wire  u_fifo_nvdla2csb_io_rd_req; // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
  wire [33:0] u_fifo_nvdla2csb_io_wr_data; // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
  wire [33:0] u_fifo_nvdla2csb_io_rd_data; // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
  wire  u_client_cfgrom_reset; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire  u_client_cfgrom_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire  u_client_cfgrom_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire [17:0] u_client_cfgrom_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire [49:0] u_client_cfgrom_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire  u_client_cfgrom_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire [62:0] u_client_cfgrom_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire  u_client_cfgrom_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire [33:0] u_client_cfgrom_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire  u_client_cfgrom_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire [49:0] u_client_cfgrom_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire  u_client_cfgrom_io_select_client; // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
  wire  u_client_glb_reset; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire  u_client_glb_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire  u_client_glb_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire [17:0] u_client_glb_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire [49:0] u_client_glb_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire  u_client_glb_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire [62:0] u_client_glb_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire  u_client_glb_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire [33:0] u_client_glb_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire  u_client_glb_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire [49:0] u_client_glb_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire  u_client_glb_io_select_client; // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
  wire  u_client_mcif_reset; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire  u_client_mcif_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire  u_client_mcif_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire [17:0] u_client_mcif_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire [49:0] u_client_mcif_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire  u_client_mcif_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire [62:0] u_client_mcif_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire  u_client_mcif_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire [33:0] u_client_mcif_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire  u_client_mcif_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire [49:0] u_client_mcif_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire  u_client_mcif_io_select_client; // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
  wire  u_client_cdma_reset; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire  u_client_cdma_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire  u_client_cdma_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire [17:0] u_client_cdma_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire [49:0] u_client_cdma_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire  u_client_cdma_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire [62:0] u_client_cdma_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire  u_client_cdma_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire [33:0] u_client_cdma_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire  u_client_cdma_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire [49:0] u_client_cdma_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire  u_client_cdma_io_select_client; // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
  wire  u_client_csc_reset; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire  u_client_csc_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire  u_client_csc_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire [17:0] u_client_csc_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire [49:0] u_client_csc_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire  u_client_csc_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire [62:0] u_client_csc_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire  u_client_csc_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire [33:0] u_client_csc_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire  u_client_csc_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire [49:0] u_client_csc_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire  u_client_csc_io_select_client; // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
  wire  u_client_cmac_a_reset; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire  u_client_cmac_a_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire  u_client_cmac_a_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire [17:0] u_client_cmac_a_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire [49:0] u_client_cmac_a_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire  u_client_cmac_a_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire [62:0] u_client_cmac_a_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire  u_client_cmac_a_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire [33:0] u_client_cmac_a_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire  u_client_cmac_a_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire [49:0] u_client_cmac_a_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire  u_client_cmac_a_io_select_client; // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
  wire  u_client_cmac_b_reset; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire  u_client_cmac_b_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire  u_client_cmac_b_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire [17:0] u_client_cmac_b_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire [49:0] u_client_cmac_b_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire  u_client_cmac_b_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire [62:0] u_client_cmac_b_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire  u_client_cmac_b_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire [33:0] u_client_cmac_b_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire  u_client_cmac_b_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire [49:0] u_client_cmac_b_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire  u_client_cmac_b_io_select_client; // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
  wire  u_client_cacc_reset; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire  u_client_cacc_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire  u_client_cacc_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire [17:0] u_client_cacc_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire [49:0] u_client_cacc_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire  u_client_cacc_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire [62:0] u_client_cacc_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire  u_client_cacc_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire [33:0] u_client_cacc_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire  u_client_cacc_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire [49:0] u_client_cacc_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire  u_client_cacc_io_select_client; // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
  wire  u_client_sdp_rdma_reset; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire  u_client_sdp_rdma_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire  u_client_sdp_rdma_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire [17:0] u_client_sdp_rdma_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire [49:0] u_client_sdp_rdma_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire  u_client_sdp_rdma_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire [62:0] u_client_sdp_rdma_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire  u_client_sdp_rdma_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire [33:0] u_client_sdp_rdma_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire  u_client_sdp_rdma_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire [49:0] u_client_sdp_rdma_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire  u_client_sdp_rdma_io_select_client; // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
  wire  u_client_sdp_reset; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire  u_client_sdp_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire  u_client_sdp_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire [17:0] u_client_sdp_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire [49:0] u_client_sdp_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire  u_client_sdp_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire [62:0] u_client_sdp_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire  u_client_sdp_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire [33:0] u_client_sdp_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire  u_client_sdp_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire [49:0] u_client_sdp_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire  u_client_sdp_io_select_client; // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_reset; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire [17:0] NV_NVDLA_CSB_MASTER_for_client_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire [49:0] NV_NVDLA_CSB_MASTER_for_client_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire [62:0] NV_NVDLA_CSB_MASTER_for_client_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire [33:0] NV_NVDLA_CSB_MASTER_for_client_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire [49:0] NV_NVDLA_CSB_MASTER_for_client_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_io_select_client; // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_1_reset; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_1_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_1_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire [17:0] NV_NVDLA_CSB_MASTER_for_client_1_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire [49:0] NV_NVDLA_CSB_MASTER_for_client_1_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire [62:0] NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire [33:0] NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_1_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire [49:0] NV_NVDLA_CSB_MASTER_for_client_1_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_1_io_select_client; // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_2_reset; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_2_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_2_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire [17:0] NV_NVDLA_CSB_MASTER_for_client_2_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire [49:0] NV_NVDLA_CSB_MASTER_for_client_2_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire [62:0] NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire [33:0] NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_2_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire [49:0] NV_NVDLA_CSB_MASTER_for_client_2_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_2_io_select_client; // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_3_reset; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_3_io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_3_io_core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire [17:0] NV_NVDLA_CSB_MASTER_for_client_3_io_core_byte_addr; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire [49:0] NV_NVDLA_CSB_MASTER_for_client_3_io_core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire [62:0] NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_resp_valid; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire [33:0] NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_resp_bits; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_3_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire [49:0] NV_NVDLA_CSB_MASTER_for_client_3_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire  NV_NVDLA_CSB_MASTER_for_client_3_io_select_client; // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
  wire  _T_537; // @[NV_NVDLA_csb_master.scala 92:38:@1842.4]
  wire [47:0] _T_538; // @[Cat.scala 30:58:@1843.4]
  wire [1:0] _T_539; // @[Cat.scala 30:58:@1844.4]
  wire  _T_543; // @[NV_NVDLA_csb_master.scala 135:74:@1871.4]
  wire  _T_545; // @[NV_NVDLA_csb_master.scala 135:79:@1872.4]
  wire  nvdla2csb_rresp_is_valid; // @[NV_NVDLA_csb_master.scala 135:53:@1873.4]
  wire  nvdla2csb_wresp_is_valid; // @[NV_NVDLA_csb_master.scala 136:52:@1876.4]
  wire  _T_550; // @[NV_NVDLA_csb_master.scala 138:62:@1877.4]
  reg  _T_553; // @[NV_NVDLA_csb_master.scala 138:92:@1878.4]
  reg [31:0] _RAND_0;
  reg [31:0] _T_558; // @[Reg.scala 19:20:@1882.4]
  reg [31:0] _RAND_1;
  wire [33:0] _GEN_0; // @[Reg.scala 20:19:@1883.4]
  reg  _T_563; // @[NV_NVDLA_csb_master.scala 140:98:@1888.4]
  reg [31:0] _RAND_2;
  wire [15:0] core_req_addr; // @[NV_NVDLA_csb_master.scala 145:32:@1891.4]
  wire  core_req_write; // @[NV_NVDLA_csb_master.scala 146:33:@1892.4]
  wire  core_req_nposted; // @[NV_NVDLA_csb_master.scala 147:37:@1893.4]
  wire  core_req_pop_valid; // @[NV_NVDLA_csb_master.scala 148:40:@1894.4]
  reg [49:0] core_req_pd_d1; // @[Reg.scala 11:16:@1897.4]
  reg [63:0] _RAND_3;
  reg  csb2dummy_req_pvld; // @[NV_NVDLA_csb_master.scala 367:33:@2086.4]
  reg [31:0] _RAND_4;
  reg  csb2dummy_req_nposted; // @[NV_NVDLA_csb_master.scala 368:32:@2087.4]
  reg [31:0] _RAND_5;
  reg  csb2dummy_req_read; // @[NV_NVDLA_csb_master.scala 369:29:@2088.4]
  reg [31:0] _RAND_6;
  wire  _T_581; // @[NV_NVDLA_csb_master.scala 372:23:@2089.4]
  wire  _T_582; // @[NV_NVDLA_csb_master.scala 373:23:@2090.4]
  wire  _T_585; // @[NV_NVDLA_csb_master.scala 376:23:@2093.4]
  wire  _T_586; // @[NV_NVDLA_csb_master.scala 377:23:@2094.4]
  wire  _T_587; // @[NV_NVDLA_csb_master.scala 378:23:@2095.4]
  wire  _T_588; // @[NV_NVDLA_csb_master.scala 379:23:@2096.4]
  wire  _T_589; // @[NV_NVDLA_csb_master.scala 380:23:@2097.4]
  wire  _T_590; // @[NV_NVDLA_csb_master.scala 381:23:@2098.4]
  wire  _T_591; // @[NV_NVDLA_csb_master.scala 382:23:@2099.4]
  wire  _T_592; // @[NV_NVDLA_csb_master.scala 383:23:@2100.4]
  wire  _T_593; // @[NV_NVDLA_csb_master.scala 384:23:@2101.4]
  wire  _T_594; // @[NV_NVDLA_csb_master.scala 385:23:@2102.4]
  wire  _T_595; // @[NV_NVDLA_csb_master.scala 386:23:@2103.4]
  wire  select_dummy; // @[NV_NVDLA_csb_master.scala 371:20:@2105.4]
  wire  dummy_req_pvld_w; // @[NV_NVDLA_csb_master.scala 389:44:@2106.4]
  wire  _T_597; // @[NV_NVDLA_csb_master.scala 395:27:@2112.6]
  wire  _T_608; // @[NV_NVDLA_csb_master.scala 403:70:@2122.4]
  wire  dummy_resp_valid_w; // @[NV_NVDLA_csb_master.scala 403:45:@2123.4]
  wire  _T_609; // @[NV_NVDLA_csb_master.scala 404:25:@2124.4]
  wire  dummy_resp_type_w; // @[NV_NVDLA_csb_master.scala 404:45:@2125.4]
  reg  dummy_resp_type; // @[Reg.scala 19:20:@2126.4]
  reg [31:0] _RAND_7;
  wire  _GEN_4; // @[Reg.scala 20:19:@2127.4]
  wire [33:0] dummy_resp_pd; // @[NV_NVDLA_csb_master.scala 406:24:@2130.4]
  reg  dummy_resp_valid; // @[NV_NVDLA_csb_master.scala 408:31:@2131.4]
  reg [31:0] _RAND_8;
  wire  _T_626; // @[Bitwise.scala 72:15:@2136.4]
  wire [33:0] _T_629; // @[Bitwise.scala 72:12:@2137.4]
  wire [49:0] _GEN_5; // @[NV_NVDLA_csb_master.scala 435:49:@2138.4]
  wire [49:0] _T_630; // @[NV_NVDLA_csb_master.scala 435:49:@2138.4]
  wire  _T_631; // @[Bitwise.scala 72:15:@2139.4]
  wire [33:0] _T_634; // @[Bitwise.scala 72:12:@2140.4]
  wire [49:0] _GEN_6; // @[NV_NVDLA_csb_master.scala 436:52:@2141.4]
  wire [49:0] _T_635; // @[NV_NVDLA_csb_master.scala 436:52:@2141.4]
  wire [49:0] _T_636; // @[NV_NVDLA_csb_master.scala 435:71:@2142.4]
  wire  _T_637; // @[Bitwise.scala 72:15:@2143.4]
  wire [33:0] _T_640; // @[Bitwise.scala 72:12:@2144.4]
  wire [49:0] _GEN_7; // @[NV_NVDLA_csb_master.scala 437:53:@2145.4]
  wire [49:0] _T_641; // @[NV_NVDLA_csb_master.scala 437:53:@2145.4]
  wire [49:0] _T_642; // @[NV_NVDLA_csb_master.scala 436:71:@2146.4]
  wire  _T_655; // @[Bitwise.scala 72:15:@2153.4]
  wire [33:0] _T_658; // @[Bitwise.scala 72:12:@2154.4]
  wire [49:0] _GEN_8; // @[NV_NVDLA_csb_master.scala 440:53:@2155.4]
  wire [49:0] _T_659; // @[NV_NVDLA_csb_master.scala 440:53:@2155.4]
  wire [49:0] _T_660; // @[NV_NVDLA_csb_master.scala 439:75:@2156.4]
  wire  _T_661; // @[Bitwise.scala 72:15:@2157.4]
  wire [33:0] _T_664; // @[Bitwise.scala 72:12:@2158.4]
  wire [49:0] _GEN_9; // @[NV_NVDLA_csb_master.scala 441:52:@2159.4]
  wire [49:0] _T_665; // @[NV_NVDLA_csb_master.scala 441:52:@2159.4]
  wire [49:0] _T_666; // @[NV_NVDLA_csb_master.scala 440:73:@2160.4]
  wire  _T_667; // @[Bitwise.scala 72:15:@2161.4]
  wire [33:0] _T_670; // @[Bitwise.scala 72:12:@2162.4]
  wire [49:0] _GEN_10; // @[NV_NVDLA_csb_master.scala 442:55:@2163.4]
  wire [49:0] _T_671; // @[NV_NVDLA_csb_master.scala 442:55:@2163.4]
  wire [49:0] _T_672; // @[NV_NVDLA_csb_master.scala 441:71:@2164.4]
  wire  _T_673; // @[Bitwise.scala 72:15:@2165.4]
  wire [33:0] _T_676; // @[Bitwise.scala 72:12:@2166.4]
  wire [49:0] _GEN_11; // @[NV_NVDLA_csb_master.scala 443:55:@2167.4]
  wire [49:0] _T_677; // @[NV_NVDLA_csb_master.scala 443:55:@2167.4]
  wire [49:0] _T_678; // @[NV_NVDLA_csb_master.scala 442:77:@2168.4]
  wire  _T_679; // @[Bitwise.scala 72:15:@2169.4]
  wire [33:0] _T_682; // @[Bitwise.scala 72:12:@2170.4]
  wire [49:0] _GEN_12; // @[NV_NVDLA_csb_master.scala 444:53:@2171.4]
  wire [49:0] _T_683; // @[NV_NVDLA_csb_master.scala 444:53:@2171.4]
  wire [49:0] _T_684; // @[NV_NVDLA_csb_master.scala 443:77:@2172.4]
  wire  _T_685; // @[Bitwise.scala 72:15:@2173.4]
  wire [33:0] _T_688; // @[Bitwise.scala 72:12:@2174.4]
  wire [49:0] _GEN_13; // @[NV_NVDLA_csb_master.scala 445:57:@2175.4]
  wire [49:0] _T_689; // @[NV_NVDLA_csb_master.scala 445:57:@2175.4]
  wire [49:0] _T_690; // @[NV_NVDLA_csb_master.scala 444:73:@2176.4]
  wire  _T_691; // @[Bitwise.scala 72:15:@2177.4]
  wire [33:0] _T_694; // @[Bitwise.scala 72:12:@2178.4]
  wire [49:0] _GEN_14; // @[NV_NVDLA_csb_master.scala 446:52:@2179.4]
  wire [49:0] _T_695; // @[NV_NVDLA_csb_master.scala 446:52:@2179.4]
  wire [49:0] _T_696; // @[NV_NVDLA_csb_master.scala 445:81:@2180.4]
  wire  _T_697; // @[Bitwise.scala 72:15:@2181.4]
  wire [33:0] _T_700; // @[Bitwise.scala 72:12:@2182.4]
  wire [49:0] _GEN_15; // @[NV_NVDLA_csb_master.scala 447:58:@2183.4]
  wire [49:0] _T_701; // @[NV_NVDLA_csb_master.scala 447:58:@2183.4]
  wire [49:0] _T_702; // @[NV_NVDLA_csb_master.scala 446:71:@2184.4]
  wire  _T_703; // @[Bitwise.scala 72:15:@2185.4]
  wire [33:0] _T_706; // @[Bitwise.scala 72:12:@2186.4]
  wire [49:0] _GEN_16; // @[NV_NVDLA_csb_master.scala 448:53:@2187.4]
  wire [49:0] _T_707; // @[NV_NVDLA_csb_master.scala 448:53:@2187.4]
  wire [49:0] _T_708; // @[NV_NVDLA_csb_master.scala 447:83:@2188.4]
  wire  _T_709; // @[Bitwise.scala 72:15:@2189.4]
  wire [33:0] _T_712; // @[Bitwise.scala 72:12:@2190.4]
  wire [49:0] _GEN_17; // @[NV_NVDLA_csb_master.scala 449:58:@2191.4]
  wire [49:0] _T_713; // @[NV_NVDLA_csb_master.scala 449:58:@2191.4]
  wire [49:0] _T_714; // @[NV_NVDLA_csb_master.scala 448:73:@2192.4]
  wire  _T_715; // @[Bitwise.scala 72:15:@2193.4]
  wire [33:0] _T_718; // @[Bitwise.scala 72:12:@2194.4]
  wire [49:0] _GEN_18; // @[NV_NVDLA_csb_master.scala 450:53:@2195.4]
  wire [49:0] _T_719; // @[NV_NVDLA_csb_master.scala 450:53:@2195.4]
  wire [49:0] _T_720; // @[NV_NVDLA_csb_master.scala 449:83:@2196.4]
  wire [33:0] _T_730; // @[Bitwise.scala 72:12:@2201.4]
  wire [33:0] _T_731; // @[NV_NVDLA_csb_master.scala 452:51:@2202.4]
  wire [49:0] _GEN_19; // @[NV_NVDLA_csb_master.scala 451:73:@2203.4]
  wire [49:0] _T_732; // @[NV_NVDLA_csb_master.scala 451:73:@2203.4]
  wire  _T_733; // @[NV_NVDLA_csb_master.scala 454:40:@2205.4]
  wire  _T_734; // @[NV_NVDLA_csb_master.scala 455:43:@2206.4]
  wire  _T_737; // @[NV_NVDLA_csb_master.scala 458:45:@2209.4]
  wire  _T_738; // @[NV_NVDLA_csb_master.scala 459:44:@2210.4]
  wire  _T_739; // @[NV_NVDLA_csb_master.scala 460:43:@2211.4]
  wire  _T_740; // @[NV_NVDLA_csb_master.scala 461:46:@2212.4]
  wire  _T_741; // @[NV_NVDLA_csb_master.scala 462:46:@2213.4]
  wire  _T_742; // @[NV_NVDLA_csb_master.scala 463:44:@2214.4]
  wire  _T_743; // @[NV_NVDLA_csb_master.scala 464:48:@2215.4]
  wire  _T_744; // @[NV_NVDLA_csb_master.scala 465:43:@2216.4]
  wire  _T_745; // @[NV_NVDLA_csb_master.scala 466:49:@2217.4]
  wire  _T_746; // @[NV_NVDLA_csb_master.scala 467:44:@2218.4]
  wire  _T_747; // @[NV_NVDLA_csb_master.scala 468:49:@2219.4]
  NV_NVDLA_CSB_MASTER_falcon2csb_fifo u_fifo_csb2nvdla ( // @[NV_NVDLA_csb_master.scala 99:30:@1846.4]
    .reset(u_fifo_csb2nvdla_reset),
    .io_wr_clk(u_fifo_csb2nvdla_io_wr_clk),
    .io_rd_clk(u_fifo_csb2nvdla_io_rd_clk),
    .io_wr_reset_(u_fifo_csb2nvdla_io_wr_reset_),
    .io_rd_reset_(u_fifo_csb2nvdla_io_rd_reset_),
    .io_wr_ready(u_fifo_csb2nvdla_io_wr_ready),
    .io_wr_req(u_fifo_csb2nvdla_io_wr_req),
    .io_rd_req(u_fifo_csb2nvdla_io_rd_req),
    .io_wr_data(u_fifo_csb2nvdla_io_wr_data),
    .io_rd_data(u_fifo_csb2nvdla_io_rd_data)
  );
  NV_NVDLA_CSB_MASTER_csb2falcon_fifo u_fifo_nvdla2csb ( // @[NV_NVDLA_csb_master.scala 118:30:@1860.4]
    .reset(u_fifo_nvdla2csb_reset),
    .io_wr_clk(u_fifo_nvdla2csb_io_wr_clk),
    .io_wr_reset_(u_fifo_nvdla2csb_io_wr_reset_),
    .io_rd_clk(u_fifo_nvdla2csb_io_rd_clk),
    .io_rd_reset_(u_fifo_nvdla2csb_io_rd_reset_),
    .io_wr_req(u_fifo_nvdla2csb_io_wr_req),
    .io_rd_req(u_fifo_nvdla2csb_io_rd_req),
    .io_wr_data(u_fifo_nvdla2csb_io_wr_data),
    .io_rd_data(u_fifo_nvdla2csb_io_rd_data)
  );
  NV_NVDLA_CSB_MASTER_for_client u_client_cfgrom ( // @[NV_NVDLA_csb_master.scala 156:29:@1904.4]
    .reset(u_client_cfgrom_reset),
    .io_nvdla_core_clk(u_client_cfgrom_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_cfgrom_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_cfgrom_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_cfgrom_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_cfgrom_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_cfgrom_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_cfgrom_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_cfgrom_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_cfgrom_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_cfgrom_io_client_resp_pd_bits),
    .io_select_client(u_client_cfgrom_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_1 u_client_glb ( // @[NV_NVDLA_csb_master.scala 167:26:@1917.4]
    .reset(u_client_glb_reset),
    .io_nvdla_core_clk(u_client_glb_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_glb_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_glb_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_glb_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_glb_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_glb_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_glb_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_glb_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_glb_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_glb_io_client_resp_pd_bits),
    .io_select_client(u_client_glb_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_2 u_client_mcif ( // @[NV_NVDLA_csb_master.scala 178:27:@1930.4]
    .reset(u_client_mcif_reset),
    .io_nvdla_core_clk(u_client_mcif_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_mcif_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_mcif_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_mcif_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_mcif_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_mcif_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_mcif_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_mcif_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_mcif_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_mcif_io_client_resp_pd_bits),
    .io_select_client(u_client_mcif_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_3 u_client_cdma ( // @[NV_NVDLA_csb_master.scala 217:27:@1943.4]
    .reset(u_client_cdma_reset),
    .io_nvdla_core_clk(u_client_cdma_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_cdma_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_cdma_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_cdma_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_cdma_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_cdma_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_cdma_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_cdma_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_cdma_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_cdma_io_client_resp_pd_bits),
    .io_select_client(u_client_cdma_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_4 u_client_csc ( // @[NV_NVDLA_csb_master.scala 228:26:@1956.4]
    .reset(u_client_csc_reset),
    .io_nvdla_core_clk(u_client_csc_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_csc_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_csc_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_csc_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_csc_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_csc_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_csc_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_csc_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_csc_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_csc_io_client_resp_pd_bits),
    .io_select_client(u_client_csc_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_5 u_client_cmac_a ( // @[NV_NVDLA_csb_master.scala 239:29:@1969.4]
    .reset(u_client_cmac_a_reset),
    .io_nvdla_core_clk(u_client_cmac_a_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_cmac_a_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_cmac_a_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_cmac_a_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_cmac_a_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_cmac_a_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_cmac_a_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_cmac_a_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_cmac_a_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_cmac_a_io_client_resp_pd_bits),
    .io_select_client(u_client_cmac_a_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_6 u_client_cmac_b ( // @[NV_NVDLA_csb_master.scala 250:29:@1982.4]
    .reset(u_client_cmac_b_reset),
    .io_nvdla_core_clk(u_client_cmac_b_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_cmac_b_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_cmac_b_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_cmac_b_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_cmac_b_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_cmac_b_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_cmac_b_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_cmac_b_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_cmac_b_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_cmac_b_io_client_resp_pd_bits),
    .io_select_client(u_client_cmac_b_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_7 u_client_cacc ( // @[NV_NVDLA_csb_master.scala 261:27:@1995.4]
    .reset(u_client_cacc_reset),
    .io_nvdla_core_clk(u_client_cacc_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_cacc_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_cacc_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_cacc_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_cacc_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_cacc_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_cacc_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_cacc_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_cacc_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_cacc_io_client_resp_pd_bits),
    .io_select_client(u_client_cacc_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_8 u_client_sdp_rdma ( // @[NV_NVDLA_csb_master.scala 272:31:@2008.4]
    .reset(u_client_sdp_rdma_reset),
    .io_nvdla_core_clk(u_client_sdp_rdma_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_sdp_rdma_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_sdp_rdma_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_sdp_rdma_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_sdp_rdma_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_sdp_rdma_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_sdp_rdma_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_sdp_rdma_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_sdp_rdma_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_sdp_rdma_io_client_resp_pd_bits),
    .io_select_client(u_client_sdp_rdma_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_9 u_client_sdp ( // @[NV_NVDLA_csb_master.scala 283:26:@2021.4]
    .reset(u_client_sdp_reset),
    .io_nvdla_core_clk(u_client_sdp_io_nvdla_core_clk),
    .io_core_req_pop_valid(u_client_sdp_io_core_req_pop_valid),
    .io_core_byte_addr(u_client_sdp_io_core_byte_addr),
    .io_core_req_pd_d1(u_client_sdp_io_core_req_pd_d1),
    .io_csb2client_req_valid(u_client_sdp_io_csb2client_req_valid),
    .io_csb2client_req_bits(u_client_sdp_io_csb2client_req_bits),
    .io_csb2client_resp_valid(u_client_sdp_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(u_client_sdp_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(u_client_sdp_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(u_client_sdp_io_client_resp_pd_bits),
    .io_select_client(u_client_sdp_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_10 NV_NVDLA_CSB_MASTER_for_client ( // @[NV_NVDLA_csb_master.scala 294:62:@2034.4]
    .reset(NV_NVDLA_CSB_MASTER_for_client_reset),
    .io_nvdla_core_clk(NV_NVDLA_CSB_MASTER_for_client_io_nvdla_core_clk),
    .io_core_req_pop_valid(NV_NVDLA_CSB_MASTER_for_client_io_core_req_pop_valid),
    .io_core_byte_addr(NV_NVDLA_CSB_MASTER_for_client_io_core_byte_addr),
    .io_core_req_pd_d1(NV_NVDLA_CSB_MASTER_for_client_io_core_req_pd_d1),
    .io_csb2client_req_valid(NV_NVDLA_CSB_MASTER_for_client_io_csb2client_req_valid),
    .io_csb2client_req_bits(NV_NVDLA_CSB_MASTER_for_client_io_csb2client_req_bits),
    .io_csb2client_resp_valid(NV_NVDLA_CSB_MASTER_for_client_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(NV_NVDLA_CSB_MASTER_for_client_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(NV_NVDLA_CSB_MASTER_for_client_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(NV_NVDLA_CSB_MASTER_for_client_io_client_resp_pd_bits),
    .io_select_client(NV_NVDLA_CSB_MASTER_for_client_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_11 NV_NVDLA_CSB_MASTER_for_client_1 ( // @[NV_NVDLA_csb_master.scala 309:57:@2047.4]
    .reset(NV_NVDLA_CSB_MASTER_for_client_1_reset),
    .io_nvdla_core_clk(NV_NVDLA_CSB_MASTER_for_client_1_io_nvdla_core_clk),
    .io_core_req_pop_valid(NV_NVDLA_CSB_MASTER_for_client_1_io_core_req_pop_valid),
    .io_core_byte_addr(NV_NVDLA_CSB_MASTER_for_client_1_io_core_byte_addr),
    .io_core_req_pd_d1(NV_NVDLA_CSB_MASTER_for_client_1_io_core_req_pd_d1),
    .io_csb2client_req_valid(NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_req_valid),
    .io_csb2client_req_bits(NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_req_bits),
    .io_csb2client_resp_valid(NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(NV_NVDLA_CSB_MASTER_for_client_1_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(NV_NVDLA_CSB_MASTER_for_client_1_io_client_resp_pd_bits),
    .io_select_client(NV_NVDLA_CSB_MASTER_for_client_1_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_12 NV_NVDLA_CSB_MASTER_for_client_2 ( // @[NV_NVDLA_csb_master.scala 324:62:@2060.4]
    .reset(NV_NVDLA_CSB_MASTER_for_client_2_reset),
    .io_nvdla_core_clk(NV_NVDLA_CSB_MASTER_for_client_2_io_nvdla_core_clk),
    .io_core_req_pop_valid(NV_NVDLA_CSB_MASTER_for_client_2_io_core_req_pop_valid),
    .io_core_byte_addr(NV_NVDLA_CSB_MASTER_for_client_2_io_core_byte_addr),
    .io_core_req_pd_d1(NV_NVDLA_CSB_MASTER_for_client_2_io_core_req_pd_d1),
    .io_csb2client_req_valid(NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_req_valid),
    .io_csb2client_req_bits(NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_req_bits),
    .io_csb2client_resp_valid(NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(NV_NVDLA_CSB_MASTER_for_client_2_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(NV_NVDLA_CSB_MASTER_for_client_2_io_client_resp_pd_bits),
    .io_select_client(NV_NVDLA_CSB_MASTER_for_client_2_io_select_client)
  );
  NV_NVDLA_CSB_MASTER_for_client_13 NV_NVDLA_CSB_MASTER_for_client_3 ( // @[NV_NVDLA_csb_master.scala 338:57:@2073.4]
    .reset(NV_NVDLA_CSB_MASTER_for_client_3_reset),
    .io_nvdla_core_clk(NV_NVDLA_CSB_MASTER_for_client_3_io_nvdla_core_clk),
    .io_core_req_pop_valid(NV_NVDLA_CSB_MASTER_for_client_3_io_core_req_pop_valid),
    .io_core_byte_addr(NV_NVDLA_CSB_MASTER_for_client_3_io_core_byte_addr),
    .io_core_req_pd_d1(NV_NVDLA_CSB_MASTER_for_client_3_io_core_req_pd_d1),
    .io_csb2client_req_valid(NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_req_valid),
    .io_csb2client_req_bits(NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_req_bits),
    .io_csb2client_resp_valid(NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_resp_valid),
    .io_csb2client_resp_bits(NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_resp_bits),
    .io_client_resp_pd_valid(NV_NVDLA_CSB_MASTER_for_client_3_io_client_resp_pd_valid),
    .io_client_resp_pd_bits(NV_NVDLA_CSB_MASTER_for_client_3_io_client_resp_pd_bits),
    .io_select_client(NV_NVDLA_CSB_MASTER_for_client_3_io_select_client)
  );
  assign _T_537 = ~ io_nvdla_core_rstn; // @[NV_NVDLA_csb_master.scala 92:38:@1842.4]
  assign _T_538 = {io_csb2nvdla_bits_wdat,io_csb2nvdla_bits_addr}; // @[Cat.scala 30:58:@1843.4]
  assign _T_539 = {io_csb2nvdla_bits_nposted,io_csb2nvdla_bits_write}; // @[Cat.scala 30:58:@1844.4]
  assign _T_543 = u_fifo_nvdla2csb_io_rd_data[33]; // @[NV_NVDLA_csb_master.scala 135:74:@1871.4]
  assign _T_545 = _T_543 == 1'h0; // @[NV_NVDLA_csb_master.scala 135:79:@1872.4]
  assign nvdla2csb_rresp_is_valid = u_fifo_nvdla2csb_io_rd_req & _T_545; // @[NV_NVDLA_csb_master.scala 135:53:@1873.4]
  assign nvdla2csb_wresp_is_valid = u_fifo_nvdla2csb_io_rd_req & _T_543; // @[NV_NVDLA_csb_master.scala 136:52:@1876.4]
  assign _T_550 = io_nvdla_falcon_rstn == 1'h0; // @[NV_NVDLA_csb_master.scala 138:62:@1877.4]
  assign _GEN_0 = nvdla2csb_rresp_is_valid ? u_fifo_nvdla2csb_io_rd_data : {{2'd0}, _T_558}; // @[Reg.scala 20:19:@1883.4]
  assign core_req_addr = u_fifo_csb2nvdla_io_rd_data[15:0]; // @[NV_NVDLA_csb_master.scala 145:32:@1891.4]
  assign core_req_write = u_fifo_csb2nvdla_io_rd_data[48]; // @[NV_NVDLA_csb_master.scala 146:33:@1892.4]
  assign core_req_nposted = u_fifo_csb2nvdla_io_rd_data[49]; // @[NV_NVDLA_csb_master.scala 147:37:@1893.4]
  assign core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 148:40:@1894.4]
  assign _T_581 = u_client_cfgrom_io_select_client | u_client_glb_io_select_client; // @[NV_NVDLA_csb_master.scala 372:23:@2089.4]
  assign _T_582 = _T_581 | u_client_mcif_io_select_client; // @[NV_NVDLA_csb_master.scala 373:23:@2090.4]
  assign _T_585 = _T_582 | u_client_cdma_io_select_client; // @[NV_NVDLA_csb_master.scala 376:23:@2093.4]
  assign _T_586 = _T_585 | u_client_csc_io_select_client; // @[NV_NVDLA_csb_master.scala 377:23:@2094.4]
  assign _T_587 = _T_586 | u_client_cmac_a_io_select_client; // @[NV_NVDLA_csb_master.scala 378:23:@2095.4]
  assign _T_588 = _T_587 | u_client_cmac_b_io_select_client; // @[NV_NVDLA_csb_master.scala 379:23:@2096.4]
  assign _T_589 = _T_588 | u_client_cacc_io_select_client; // @[NV_NVDLA_csb_master.scala 380:23:@2097.4]
  assign _T_590 = _T_589 | u_client_sdp_rdma_io_select_client; // @[NV_NVDLA_csb_master.scala 381:23:@2098.4]
  assign _T_591 = _T_590 | u_client_sdp_io_select_client; // @[NV_NVDLA_csb_master.scala 382:23:@2099.4]
  assign _T_592 = _T_591 | NV_NVDLA_CSB_MASTER_for_client_io_select_client; // @[NV_NVDLA_csb_master.scala 383:23:@2100.4]
  assign _T_593 = _T_592 | NV_NVDLA_CSB_MASTER_for_client_1_io_select_client; // @[NV_NVDLA_csb_master.scala 384:23:@2101.4]
  assign _T_594 = _T_593 | NV_NVDLA_CSB_MASTER_for_client_3_io_select_client; // @[NV_NVDLA_csb_master.scala 385:23:@2102.4]
  assign _T_595 = _T_594 | NV_NVDLA_CSB_MASTER_for_client_2_io_select_client; // @[NV_NVDLA_csb_master.scala 386:23:@2103.4]
  assign select_dummy = ~ _T_595; // @[NV_NVDLA_csb_master.scala 371:20:@2105.4]
  assign dummy_req_pvld_w = core_req_pop_valid & select_dummy; // @[NV_NVDLA_csb_master.scala 389:44:@2106.4]
  assign _T_597 = ~ core_req_write; // @[NV_NVDLA_csb_master.scala 395:27:@2112.6]
  assign _T_608 = csb2dummy_req_nposted | csb2dummy_req_read; // @[NV_NVDLA_csb_master.scala 403:70:@2122.4]
  assign dummy_resp_valid_w = csb2dummy_req_pvld & _T_608; // @[NV_NVDLA_csb_master.scala 403:45:@2123.4]
  assign _T_609 = ~ csb2dummy_req_read; // @[NV_NVDLA_csb_master.scala 404:25:@2124.4]
  assign dummy_resp_type_w = _T_609 & csb2dummy_req_nposted; // @[NV_NVDLA_csb_master.scala 404:45:@2125.4]
  assign _GEN_4 = dummy_resp_valid_w ? dummy_resp_type_w : dummy_resp_type; // @[Reg.scala 20:19:@2127.4]
  assign dummy_resp_pd = dummy_resp_type ? 34'h200000000 : 34'h0; // @[NV_NVDLA_csb_master.scala 406:24:@2130.4]
  assign _T_626 = u_client_cfgrom_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2136.4]
  assign _T_629 = _T_626 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2137.4]
  assign _GEN_5 = {{16'd0}, _T_629}; // @[NV_NVDLA_csb_master.scala 435:49:@2138.4]
  assign _T_630 = _GEN_5 & u_client_cfgrom_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 435:49:@2138.4]
  assign _T_631 = u_client_glb_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2139.4]
  assign _T_634 = _T_631 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2140.4]
  assign _GEN_6 = {{16'd0}, _T_634}; // @[NV_NVDLA_csb_master.scala 436:52:@2141.4]
  assign _T_635 = _GEN_6 & u_client_glb_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 436:52:@2141.4]
  assign _T_636 = _T_630 | _T_635; // @[NV_NVDLA_csb_master.scala 435:71:@2142.4]
  assign _T_637 = u_client_mcif_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2143.4]
  assign _T_640 = _T_637 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2144.4]
  assign _GEN_7 = {{16'd0}, _T_640}; // @[NV_NVDLA_csb_master.scala 437:53:@2145.4]
  assign _T_641 = _GEN_7 & u_client_mcif_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 437:53:@2145.4]
  assign _T_642 = _T_636 | _T_641; // @[NV_NVDLA_csb_master.scala 436:71:@2146.4]
  assign _T_655 = u_client_cdma_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2153.4]
  assign _T_658 = _T_655 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2154.4]
  assign _GEN_8 = {{16'd0}, _T_658}; // @[NV_NVDLA_csb_master.scala 440:53:@2155.4]
  assign _T_659 = _GEN_8 & u_client_cdma_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 440:53:@2155.4]
  assign _T_660 = _T_642 | _T_659; // @[NV_NVDLA_csb_master.scala 439:75:@2156.4]
  assign _T_661 = u_client_csc_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2157.4]
  assign _T_664 = _T_661 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2158.4]
  assign _GEN_9 = {{16'd0}, _T_664}; // @[NV_NVDLA_csb_master.scala 441:52:@2159.4]
  assign _T_665 = _GEN_9 & u_client_csc_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 441:52:@2159.4]
  assign _T_666 = _T_660 | _T_665; // @[NV_NVDLA_csb_master.scala 440:73:@2160.4]
  assign _T_667 = u_client_cmac_a_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2161.4]
  assign _T_670 = _T_667 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2162.4]
  assign _GEN_10 = {{16'd0}, _T_670}; // @[NV_NVDLA_csb_master.scala 442:55:@2163.4]
  assign _T_671 = _GEN_10 & u_client_cmac_a_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 442:55:@2163.4]
  assign _T_672 = _T_666 | _T_671; // @[NV_NVDLA_csb_master.scala 441:71:@2164.4]
  assign _T_673 = u_client_cmac_b_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2165.4]
  assign _T_676 = _T_673 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2166.4]
  assign _GEN_11 = {{16'd0}, _T_676}; // @[NV_NVDLA_csb_master.scala 443:55:@2167.4]
  assign _T_677 = _GEN_11 & u_client_cmac_b_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 443:55:@2167.4]
  assign _T_678 = _T_672 | _T_677; // @[NV_NVDLA_csb_master.scala 442:77:@2168.4]
  assign _T_679 = u_client_cacc_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2169.4]
  assign _T_682 = _T_679 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2170.4]
  assign _GEN_12 = {{16'd0}, _T_682}; // @[NV_NVDLA_csb_master.scala 444:53:@2171.4]
  assign _T_683 = _GEN_12 & u_client_cacc_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 444:53:@2171.4]
  assign _T_684 = _T_678 | _T_683; // @[NV_NVDLA_csb_master.scala 443:77:@2172.4]
  assign _T_685 = u_client_sdp_rdma_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2173.4]
  assign _T_688 = _T_685 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2174.4]
  assign _GEN_13 = {{16'd0}, _T_688}; // @[NV_NVDLA_csb_master.scala 445:57:@2175.4]
  assign _T_689 = _GEN_13 & u_client_sdp_rdma_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 445:57:@2175.4]
  assign _T_690 = _T_684 | _T_689; // @[NV_NVDLA_csb_master.scala 444:73:@2176.4]
  assign _T_691 = u_client_sdp_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2177.4]
  assign _T_694 = _T_691 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2178.4]
  assign _GEN_14 = {{16'd0}, _T_694}; // @[NV_NVDLA_csb_master.scala 446:52:@2179.4]
  assign _T_695 = _GEN_14 & u_client_sdp_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 446:52:@2179.4]
  assign _T_696 = _T_690 | _T_695; // @[NV_NVDLA_csb_master.scala 445:81:@2180.4]
  assign _T_697 = NV_NVDLA_CSB_MASTER_for_client_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2181.4]
  assign _T_700 = _T_697 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2182.4]
  assign _GEN_15 = {{16'd0}, _T_700}; // @[NV_NVDLA_csb_master.scala 447:58:@2183.4]
  assign _T_701 = _GEN_15 & NV_NVDLA_CSB_MASTER_for_client_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 447:58:@2183.4]
  assign _T_702 = _T_696 | _T_701; // @[NV_NVDLA_csb_master.scala 446:71:@2184.4]
  assign _T_703 = NV_NVDLA_CSB_MASTER_for_client_1_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2185.4]
  assign _T_706 = _T_703 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2186.4]
  assign _GEN_16 = {{16'd0}, _T_706}; // @[NV_NVDLA_csb_master.scala 448:53:@2187.4]
  assign _T_707 = _GEN_16 & NV_NVDLA_CSB_MASTER_for_client_1_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 448:53:@2187.4]
  assign _T_708 = _T_702 | _T_707; // @[NV_NVDLA_csb_master.scala 447:83:@2188.4]
  assign _T_709 = NV_NVDLA_CSB_MASTER_for_client_2_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2189.4]
  assign _T_712 = _T_709 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2190.4]
  assign _GEN_17 = {{16'd0}, _T_712}; // @[NV_NVDLA_csb_master.scala 449:58:@2191.4]
  assign _T_713 = _GEN_17 & NV_NVDLA_CSB_MASTER_for_client_2_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 449:58:@2191.4]
  assign _T_714 = _T_708 | _T_713; // @[NV_NVDLA_csb_master.scala 448:73:@2192.4]
  assign _T_715 = NV_NVDLA_CSB_MASTER_for_client_3_io_client_resp_pd_valid; // @[Bitwise.scala 72:15:@2193.4]
  assign _T_718 = _T_715 ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2194.4]
  assign _GEN_18 = {{16'd0}, _T_718}; // @[NV_NVDLA_csb_master.scala 450:53:@2195.4]
  assign _T_719 = _GEN_18 & NV_NVDLA_CSB_MASTER_for_client_3_io_client_resp_pd_bits; // @[NV_NVDLA_csb_master.scala 450:53:@2195.4]
  assign _T_720 = _T_714 | _T_719; // @[NV_NVDLA_csb_master.scala 449:83:@2196.4]
  assign _T_730 = dummy_resp_valid ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@2201.4]
  assign _T_731 = _T_730 & dummy_resp_pd; // @[NV_NVDLA_csb_master.scala 452:51:@2202.4]
  assign _GEN_19 = {{16'd0}, _T_731}; // @[NV_NVDLA_csb_master.scala 451:73:@2203.4]
  assign _T_732 = _T_720 | _GEN_19; // @[NV_NVDLA_csb_master.scala 451:73:@2203.4]
  assign _T_733 = u_client_cfgrom_io_client_resp_pd_valid | u_client_glb_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 454:40:@2205.4]
  assign _T_734 = _T_733 | u_client_mcif_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 455:43:@2206.4]
  assign _T_737 = _T_734 | u_client_cdma_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 458:45:@2209.4]
  assign _T_738 = _T_737 | u_client_csc_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 459:44:@2210.4]
  assign _T_739 = _T_738 | u_client_cmac_a_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 460:43:@2211.4]
  assign _T_740 = _T_739 | u_client_cmac_b_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 461:46:@2212.4]
  assign _T_741 = _T_740 | u_client_cacc_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 462:46:@2213.4]
  assign _T_742 = _T_741 | u_client_sdp_rdma_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 463:44:@2214.4]
  assign _T_743 = _T_742 | u_client_sdp_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 464:48:@2215.4]
  assign _T_744 = _T_743 | NV_NVDLA_CSB_MASTER_for_client_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 465:43:@2216.4]
  assign _T_745 = _T_744 | NV_NVDLA_CSB_MASTER_for_client_1_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 466:49:@2217.4]
  assign _T_746 = _T_745 | NV_NVDLA_CSB_MASTER_for_client_2_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 467:44:@2218.4]
  assign _T_747 = _T_746 | NV_NVDLA_CSB_MASTER_for_client_3_io_client_resp_pd_valid; // @[NV_NVDLA_csb_master.scala 468:49:@2219.4]
  assign io_csb2nvdla_ready = u_fifo_csb2nvdla_io_wr_ready; // @[NV_NVDLA_csb_master.scala 110:20:@1856.4]
  assign io_nvdla2csb_valid = _T_553; // @[NV_NVDLA_csb_master.scala 138:20:@1880.4]
  assign io_nvdla2csb_bits_data = _T_558; // @[NV_NVDLA_csb_master.scala 139:24:@1886.4]
  assign io_nvdla2csb_wr_complete = _T_563; // @[NV_NVDLA_csb_master.scala 140:26:@1890.4]
  assign io_csb2cfgrom_req_valid = u_client_cfgrom_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 162:31:@1915.4]
  assign io_csb2cfgrom_req_bits = u_client_cfgrom_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 162:31:@1914.4]
  assign io_csb2glb_req_valid = u_client_glb_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 173:28:@1928.4]
  assign io_csb2glb_req_bits = u_client_glb_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 173:28:@1927.4]
  assign io_csb2mcif_req_valid = u_client_mcif_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 184:29:@1941.4]
  assign io_csb2mcif_req_bits = u_client_mcif_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 184:29:@1940.4]
  assign io_csb2cdma_req_valid = u_client_cdma_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 223:29:@1954.4]
  assign io_csb2cdma_req_bits = u_client_cdma_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 223:29:@1953.4]
  assign io_csb2csc_req_valid = u_client_csc_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 234:28:@1967.4]
  assign io_csb2csc_req_bits = u_client_csc_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 234:28:@1966.4]
  assign io_csb2cmac_a_req_valid = u_client_cmac_a_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 245:31:@1980.4]
  assign io_csb2cmac_a_req_bits = u_client_cmac_a_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 245:31:@1979.4]
  assign io_csb2cmac_b_req_valid = u_client_cmac_b_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 256:31:@1993.4]
  assign io_csb2cmac_b_req_bits = u_client_cmac_b_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 256:31:@1992.4]
  assign io_csb2cacc_req_valid = u_client_cacc_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 267:29:@2006.4]
  assign io_csb2cacc_req_bits = u_client_cacc_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 267:29:@2005.4]
  assign io_csb2sdp_rdma_req_valid = u_client_sdp_rdma_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 278:33:@2019.4]
  assign io_csb2sdp_rdma_req_bits = u_client_sdp_rdma_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 278:33:@2018.4]
  assign io_csb2sdp_req_valid = u_client_sdp_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 289:28:@2032.4]
  assign io_csb2sdp_req_bits = u_client_sdp_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 289:28:@2031.4]
  assign io_csb2pdp_rdma_req_valid = NV_NVDLA_CSB_MASTER_for_client_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 301:41:@2045.4]
  assign io_csb2pdp_rdma_req_bits = NV_NVDLA_CSB_MASTER_for_client_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 301:41:@2044.4]
  assign io_csb2pdp_req_valid = NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 316:36:@2058.4]
  assign io_csb2pdp_req_bits = NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 316:36:@2057.4]
  assign io_csb2cdp_rdma_req_valid = NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 331:41:@2071.4]
  assign io_csb2cdp_rdma_req_bits = NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 331:41:@2070.4]
  assign io_csb2cdp_req_valid = NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_req_valid; // @[NV_NVDLA_csb_master.scala 345:36:@2084.4]
  assign io_csb2cdp_req_bits = NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_req_bits; // @[NV_NVDLA_csb_master.scala 345:36:@2083.4]
  assign u_fifo_csb2nvdla_reset = ~ io_nvdla_core_rstn; // @[:@1848.4]
  assign u_fifo_csb2nvdla_io_wr_clk = io_nvdla_falcon_clk; // @[NV_NVDLA_csb_master.scala 101:28:@1849.4]
  assign u_fifo_csb2nvdla_io_rd_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 102:28:@1850.4]
  assign u_fifo_csb2nvdla_io_wr_reset_ = io_nvdla_falcon_rstn; // @[NV_NVDLA_csb_master.scala 103:31:@1851.4]
  assign u_fifo_csb2nvdla_io_rd_reset_ = io_nvdla_core_rstn; // @[NV_NVDLA_csb_master.scala 104:31:@1852.4]
  assign u_fifo_csb2nvdla_io_wr_req = io_csb2nvdla_valid; // @[NV_NVDLA_csb_master.scala 108:28:@1854.4]
  assign u_fifo_csb2nvdla_io_wr_data = {_T_539,_T_538}; // @[NV_NVDLA_csb_master.scala 109:29:@1855.4]
  assign u_fifo_nvdla2csb_reset = ~ io_nvdla_core_rstn; // @[:@1862.4]
  assign u_fifo_nvdla2csb_io_wr_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 120:28:@1863.4]
  assign u_fifo_nvdla2csb_io_wr_reset_ = io_nvdla_core_rstn; // @[NV_NVDLA_csb_master.scala 122:31:@1865.4]
  assign u_fifo_nvdla2csb_io_rd_clk = io_nvdla_falcon_clk; // @[NV_NVDLA_csb_master.scala 121:28:@1864.4]
  assign u_fifo_nvdla2csb_io_rd_reset_ = io_nvdla_falcon_rstn; // @[NV_NVDLA_csb_master.scala 123:31:@1866.4]
  assign u_fifo_nvdla2csb_io_wr_req = _T_747 | dummy_resp_valid; // @[NV_NVDLA_csb_master.scala 126:28:@1868.4]
  assign u_fifo_nvdla2csb_io_wr_data = _T_732[33:0]; // @[NV_NVDLA_csb_master.scala 127:29:@1869.4]
  assign u_client_cfgrom_reset = ~ io_nvdla_core_rstn; // @[:@1906.4]
  assign u_client_cfgrom_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 157:35:@1907.4]
  assign u_client_cfgrom_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 158:39:@1908.4]
  assign u_client_cfgrom_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 159:35:@1909.4]
  assign u_client_cfgrom_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 161:35:@1911.4]
  assign u_client_cfgrom_io_csb2client_resp_valid = io_csb2cfgrom_resp_valid; // @[NV_NVDLA_csb_master.scala 162:31:@1913.4]
  assign u_client_cfgrom_io_csb2client_resp_bits = io_csb2cfgrom_resp_bits; // @[NV_NVDLA_csb_master.scala 162:31:@1912.4]
  assign u_client_glb_reset = ~ io_nvdla_core_rstn; // @[:@1919.4]
  assign u_client_glb_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 168:32:@1920.4]
  assign u_client_glb_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 169:36:@1921.4]
  assign u_client_glb_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 170:32:@1922.4]
  assign u_client_glb_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 172:32:@1924.4]
  assign u_client_glb_io_csb2client_resp_valid = io_csb2glb_resp_valid; // @[NV_NVDLA_csb_master.scala 173:28:@1926.4]
  assign u_client_glb_io_csb2client_resp_bits = io_csb2glb_resp_bits; // @[NV_NVDLA_csb_master.scala 173:28:@1925.4]
  assign u_client_mcif_reset = ~ io_nvdla_core_rstn; // @[:@1932.4]
  assign u_client_mcif_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 179:33:@1933.4]
  assign u_client_mcif_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 180:37:@1934.4]
  assign u_client_mcif_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 181:33:@1935.4]
  assign u_client_mcif_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 183:33:@1937.4]
  assign u_client_mcif_io_csb2client_resp_valid = io_csb2mcif_resp_valid; // @[NV_NVDLA_csb_master.scala 184:29:@1939.4]
  assign u_client_mcif_io_csb2client_resp_bits = io_csb2mcif_resp_bits; // @[NV_NVDLA_csb_master.scala 184:29:@1938.4]
  assign u_client_cdma_reset = ~ io_nvdla_core_rstn; // @[:@1945.4]
  assign u_client_cdma_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 218:33:@1946.4]
  assign u_client_cdma_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 219:37:@1947.4]
  assign u_client_cdma_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 220:33:@1948.4]
  assign u_client_cdma_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 222:33:@1950.4]
  assign u_client_cdma_io_csb2client_resp_valid = io_csb2cdma_resp_valid; // @[NV_NVDLA_csb_master.scala 223:29:@1952.4]
  assign u_client_cdma_io_csb2client_resp_bits = io_csb2cdma_resp_bits; // @[NV_NVDLA_csb_master.scala 223:29:@1951.4]
  assign u_client_csc_reset = ~ io_nvdla_core_rstn; // @[:@1958.4]
  assign u_client_csc_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 229:32:@1959.4]
  assign u_client_csc_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 230:36:@1960.4]
  assign u_client_csc_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 231:32:@1961.4]
  assign u_client_csc_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 233:32:@1963.4]
  assign u_client_csc_io_csb2client_resp_valid = io_csb2csc_resp_valid; // @[NV_NVDLA_csb_master.scala 234:28:@1965.4]
  assign u_client_csc_io_csb2client_resp_bits = io_csb2csc_resp_bits; // @[NV_NVDLA_csb_master.scala 234:28:@1964.4]
  assign u_client_cmac_a_reset = ~ io_nvdla_core_rstn; // @[:@1971.4]
  assign u_client_cmac_a_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 240:35:@1972.4]
  assign u_client_cmac_a_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 241:39:@1973.4]
  assign u_client_cmac_a_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 242:35:@1974.4]
  assign u_client_cmac_a_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 244:35:@1976.4]
  assign u_client_cmac_a_io_csb2client_resp_valid = io_csb2cmac_a_resp_valid; // @[NV_NVDLA_csb_master.scala 245:31:@1978.4]
  assign u_client_cmac_a_io_csb2client_resp_bits = io_csb2cmac_a_resp_bits; // @[NV_NVDLA_csb_master.scala 245:31:@1977.4]
  assign u_client_cmac_b_reset = ~ io_nvdla_core_rstn; // @[:@1984.4]
  assign u_client_cmac_b_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 251:35:@1985.4]
  assign u_client_cmac_b_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 252:39:@1986.4]
  assign u_client_cmac_b_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 253:35:@1987.4]
  assign u_client_cmac_b_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 255:35:@1989.4]
  assign u_client_cmac_b_io_csb2client_resp_valid = io_csb2cmac_b_resp_valid; // @[NV_NVDLA_csb_master.scala 256:31:@1991.4]
  assign u_client_cmac_b_io_csb2client_resp_bits = io_csb2cmac_b_resp_bits; // @[NV_NVDLA_csb_master.scala 256:31:@1990.4]
  assign u_client_cacc_reset = ~ io_nvdla_core_rstn; // @[:@1997.4]
  assign u_client_cacc_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 262:33:@1998.4]
  assign u_client_cacc_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 263:37:@1999.4]
  assign u_client_cacc_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 264:33:@2000.4]
  assign u_client_cacc_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 266:33:@2002.4]
  assign u_client_cacc_io_csb2client_resp_valid = io_csb2cacc_resp_valid; // @[NV_NVDLA_csb_master.scala 267:29:@2004.4]
  assign u_client_cacc_io_csb2client_resp_bits = io_csb2cacc_resp_bits; // @[NV_NVDLA_csb_master.scala 267:29:@2003.4]
  assign u_client_sdp_rdma_reset = ~ io_nvdla_core_rstn; // @[:@2010.4]
  assign u_client_sdp_rdma_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 273:37:@2011.4]
  assign u_client_sdp_rdma_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 274:41:@2012.4]
  assign u_client_sdp_rdma_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 275:37:@2013.4]
  assign u_client_sdp_rdma_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 277:37:@2015.4]
  assign u_client_sdp_rdma_io_csb2client_resp_valid = io_csb2sdp_rdma_resp_valid; // @[NV_NVDLA_csb_master.scala 278:33:@2017.4]
  assign u_client_sdp_rdma_io_csb2client_resp_bits = io_csb2sdp_rdma_resp_bits; // @[NV_NVDLA_csb_master.scala 278:33:@2016.4]
  assign u_client_sdp_reset = ~ io_nvdla_core_rstn; // @[:@2023.4]
  assign u_client_sdp_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 284:32:@2024.4]
  assign u_client_sdp_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 285:36:@2025.4]
  assign u_client_sdp_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 286:32:@2026.4]
  assign u_client_sdp_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 288:32:@2028.4]
  assign u_client_sdp_io_csb2client_resp_valid = io_csb2sdp_resp_valid; // @[NV_NVDLA_csb_master.scala 289:28:@2030.4]
  assign u_client_sdp_io_csb2client_resp_bits = io_csb2sdp_resp_bits; // @[NV_NVDLA_csb_master.scala 289:28:@2029.4]
  assign NV_NVDLA_CSB_MASTER_for_client_reset = ~ io_nvdla_core_rstn; // @[:@2036.4]
  assign NV_NVDLA_CSB_MASTER_for_client_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 296:45:@2037.4]
  assign NV_NVDLA_CSB_MASTER_for_client_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 297:49:@2038.4]
  assign NV_NVDLA_CSB_MASTER_for_client_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 298:45:@2039.4]
  assign NV_NVDLA_CSB_MASTER_for_client_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 300:45:@2041.4]
  assign NV_NVDLA_CSB_MASTER_for_client_io_csb2client_resp_valid = io_csb2pdp_rdma_resp_valid; // @[NV_NVDLA_csb_master.scala 301:41:@2043.4]
  assign NV_NVDLA_CSB_MASTER_for_client_io_csb2client_resp_bits = io_csb2pdp_rdma_resp_bits; // @[NV_NVDLA_csb_master.scala 301:41:@2042.4]
  assign NV_NVDLA_CSB_MASTER_for_client_1_reset = ~ io_nvdla_core_rstn; // @[:@2049.4]
  assign NV_NVDLA_CSB_MASTER_for_client_1_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 311:40:@2050.4]
  assign NV_NVDLA_CSB_MASTER_for_client_1_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 312:44:@2051.4]
  assign NV_NVDLA_CSB_MASTER_for_client_1_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 313:40:@2052.4]
  assign NV_NVDLA_CSB_MASTER_for_client_1_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 315:40:@2054.4]
  assign NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_resp_valid = io_csb2pdp_resp_valid; // @[NV_NVDLA_csb_master.scala 316:36:@2056.4]
  assign NV_NVDLA_CSB_MASTER_for_client_1_io_csb2client_resp_bits = io_csb2pdp_resp_bits; // @[NV_NVDLA_csb_master.scala 316:36:@2055.4]
  assign NV_NVDLA_CSB_MASTER_for_client_2_reset = ~ io_nvdla_core_rstn; // @[:@2062.4]
  assign NV_NVDLA_CSB_MASTER_for_client_2_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 326:45:@2063.4]
  assign NV_NVDLA_CSB_MASTER_for_client_2_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 327:49:@2064.4]
  assign NV_NVDLA_CSB_MASTER_for_client_2_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 328:45:@2065.4]
  assign NV_NVDLA_CSB_MASTER_for_client_2_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 330:45:@2067.4]
  assign NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_resp_valid = io_csb2cdp_rdma_resp_valid; // @[NV_NVDLA_csb_master.scala 331:41:@2069.4]
  assign NV_NVDLA_CSB_MASTER_for_client_2_io_csb2client_resp_bits = io_csb2cdp_rdma_resp_bits; // @[NV_NVDLA_csb_master.scala 331:41:@2068.4]
  assign NV_NVDLA_CSB_MASTER_for_client_3_reset = ~ io_nvdla_core_rstn; // @[:@2075.4]
  assign NV_NVDLA_CSB_MASTER_for_client_3_io_nvdla_core_clk = io_nvdla_core_clk; // @[NV_NVDLA_csb_master.scala 340:40:@2076.4]
  assign NV_NVDLA_CSB_MASTER_for_client_3_io_core_req_pop_valid = u_fifo_csb2nvdla_io_rd_req; // @[NV_NVDLA_csb_master.scala 341:44:@2077.4]
  assign NV_NVDLA_CSB_MASTER_for_client_3_io_core_byte_addr = {core_req_addr,2'h0}; // @[NV_NVDLA_csb_master.scala 342:40:@2078.4]
  assign NV_NVDLA_CSB_MASTER_for_client_3_io_core_req_pd_d1 = core_req_pd_d1; // @[NV_NVDLA_csb_master.scala 344:40:@2080.4]
  assign NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_resp_valid = io_csb2cdp_resp_valid; // @[NV_NVDLA_csb_master.scala 345:36:@2082.4]
  assign NV_NVDLA_CSB_MASTER_for_client_3_io_csb2client_resp_bits = io_csb2cdp_resp_bits; // @[NV_NVDLA_csb_master.scala 345:36:@2081.4]
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
  _T_553 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_558 = _RAND_1[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_563 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {2{`RANDOM}};
  core_req_pd_d1 = _RAND_3[49:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  csb2dummy_req_pvld = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  csb2dummy_req_nposted = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  csb2dummy_req_read = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  dummy_resp_type = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  dummy_resp_valid = _RAND_8[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_falcon_clk) begin
    if (_T_550) begin
      _T_553 <= 1'h0;
    end else begin
      _T_553 <= nvdla2csb_rresp_is_valid;
    end
    if (_T_550) begin
      _T_558 <= 32'h0;
    end else begin
      _T_558 <= _GEN_0[31:0];
    end
    if (_T_550) begin
      _T_563 <= 1'h0;
    end else begin
      _T_563 <= nvdla2csb_wresp_is_valid;
    end
  end
  always @(posedge io_nvdla_core_clk) begin
    if (core_req_pop_valid) begin
      core_req_pd_d1 <= u_fifo_csb2nvdla_io_rd_data;
    end
    if (_T_537) begin
      csb2dummy_req_pvld <= 1'h0;
    end else begin
      csb2dummy_req_pvld <= dummy_req_pvld_w;
    end
    if (dummy_req_pvld_w) begin
      csb2dummy_req_nposted <= core_req_nposted;
    end
    if (dummy_req_pvld_w) begin
      csb2dummy_req_read <= _T_597;
    end
    if (_T_537) begin
      dummy_resp_type <= 1'h0;
    end else begin
      if (dummy_resp_valid_w) begin
        dummy_resp_type <= dummy_resp_type_w;
      end
    end
    if (_T_537) begin
      dummy_resp_valid <= 1'h0;
    end else begin
      dummy_resp_valid <= dummy_resp_valid_w;
    end
  end
endmodule
