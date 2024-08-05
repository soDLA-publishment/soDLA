module NV_NVDLA_apb2csb( // @[:@3.2]
  input         io_pclk, // @[:@4.4]
  input         io_prstn, // @[:@4.4]
  input         io_psel, // @[:@4.4]
  input         io_penable, // @[:@4.4]
  input         io_pwrite, // @[:@4.4]
  input  [31:0] io_paddr, // @[:@4.4]
  input  [31:0] io_pwdata, // @[:@4.4]
  output [31:0] io_prdata, // @[:@4.4]
  output        io_pready, // @[:@4.4]
  input         io_csb2nvdla_ready, // @[:@4.4]
  output        io_csb2nvdla_valid, // @[:@4.4]
  output [15:0] io_csb2nvdla_bits_addr, // @[:@4.4]
  output [31:0] io_csb2nvdla_bits_wdat, // @[:@4.4]
  output        io_csb2nvdla_bits_write, // @[:@4.4]
  output        io_csb2nvdla_bits_nposted, // @[:@4.4]
  input         io_nvdla2csb_valid, // @[:@4.4]
  input  [31:0] io_nvdla2csb_bits_data // @[:@4.4]
);
  wire  _T_55; // @[NV_NVDLA_apb2csb.scala 44:30:@6.4]
  reg  _T_58; // @[NV_NVDLA_apb2csb.scala 46:31:@7.4]
  reg [31:0] _RAND_0;
  wire  _T_59; // @[NV_NVDLA_apb2csb.scala 48:32:@8.4]
  wire  _T_60; // @[NV_NVDLA_apb2csb.scala 48:45:@9.4]
  wire  _T_62; // @[NV_NVDLA_apb2csb.scala 49:47:@11.4]
  wire  _T_63; // @[NV_NVDLA_apb2csb.scala 49:45:@12.4]
  wire  _T_64; // @[NV_NVDLA_apb2csb.scala 51:29:@13.4]
  wire  _T_66; // @[NV_NVDLA_apb2csb.scala 54:34:@18.6]
  wire  _GEN_0; // @[NV_NVDLA_apb2csb.scala 54:49:@19.6]
  wire  _GEN_1; // @[NV_NVDLA_apb2csb.scala 51:44:@14.4]
  wire  _T_68; // @[NV_NVDLA_apb2csb.scala 58:57:@22.4]
  wire  _T_69; // @[NV_NVDLA_apb2csb.scala 58:55:@23.4]
  wire  _T_74; // @[NV_NVDLA_apb2csb.scala 65:34:@33.4]
  wire  _T_75; // @[NV_NVDLA_apb2csb.scala 65:32:@34.4]
  wire  _T_76; // @[NV_NVDLA_apb2csb.scala 65:69:@35.4]
  wire  _T_77; // @[NV_NVDLA_apb2csb.scala 65:67:@36.4]
  wire  _T_78; // @[NV_NVDLA_apb2csb.scala 65:54:@37.4]
  assign _T_55 = ~ io_prstn; // @[NV_NVDLA_apb2csb.scala 44:30:@6.4]
  assign _T_59 = io_psel & io_penable; // @[NV_NVDLA_apb2csb.scala 48:32:@8.4]
  assign _T_60 = _T_59 & io_pwrite; // @[NV_NVDLA_apb2csb.scala 48:45:@9.4]
  assign _T_62 = ~ io_pwrite; // @[NV_NVDLA_apb2csb.scala 49:47:@11.4]
  assign _T_63 = _T_59 & _T_62; // @[NV_NVDLA_apb2csb.scala 49:45:@12.4]
  assign _T_64 = io_nvdla2csb_valid & _T_58; // @[NV_NVDLA_apb2csb.scala 51:29:@13.4]
  assign _T_66 = io_csb2nvdla_ready & _T_63; // @[NV_NVDLA_apb2csb.scala 54:34:@18.6]
  assign _GEN_0 = _T_66 ? 1'h1 : _T_58; // @[NV_NVDLA_apb2csb.scala 54:49:@19.6]
  assign _GEN_1 = _T_64 ? 1'h0 : _GEN_0; // @[NV_NVDLA_apb2csb.scala 51:44:@14.4]
  assign _T_68 = ~ _T_58; // @[NV_NVDLA_apb2csb.scala 58:57:@22.4]
  assign _T_69 = _T_63 & _T_68; // @[NV_NVDLA_apb2csb.scala 58:55:@23.4]
  assign _T_74 = ~ io_csb2nvdla_ready; // @[NV_NVDLA_apb2csb.scala 65:34:@33.4]
  assign _T_75 = _T_60 & _T_74; // @[NV_NVDLA_apb2csb.scala 65:32:@34.4]
  assign _T_76 = ~ io_nvdla2csb_valid; // @[NV_NVDLA_apb2csb.scala 65:69:@35.4]
  assign _T_77 = _T_63 & _T_76; // @[NV_NVDLA_apb2csb.scala 65:67:@36.4]
  assign _T_78 = _T_75 | _T_77; // @[NV_NVDLA_apb2csb.scala 65:54:@37.4]
  assign io_prdata = io_nvdla2csb_bits_data; // @[NV_NVDLA_apb2csb.scala 64:15:@32.4]
  assign io_pready = ~ _T_78; // @[NV_NVDLA_apb2csb.scala 65:15:@39.4]
  assign io_csb2nvdla_valid = _T_60 | _T_69; // @[NV_NVDLA_apb2csb.scala 58:24:@25.4]
  assign io_csb2nvdla_bits_addr = io_paddr[17:2]; // @[NV_NVDLA_apb2csb.scala 59:28:@27.4]
  assign io_csb2nvdla_bits_wdat = io_pwdata; // @[NV_NVDLA_apb2csb.scala 60:28:@29.4]
  assign io_csb2nvdla_bits_write = io_pwrite; // @[NV_NVDLA_apb2csb.scala 61:29:@30.4]
  assign io_csb2nvdla_bits_nposted = 1'h0; // @[NV_NVDLA_apb2csb.scala 62:31:@31.4]
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
  _T_58 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_pclk) begin
    if (_T_55) begin
      _T_58 <= 1'h0;
    end else begin
      if (_T_64) begin
        _T_58 <= 1'h0;
      end else begin
        if (_T_66) begin
          _T_58 <= 1'h1;
        end
      end
    end
  end
endmodule
