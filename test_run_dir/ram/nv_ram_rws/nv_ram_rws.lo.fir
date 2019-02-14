circuit nv_ram_rws : @[:@2.0]
  module nv_ram_rws : @[:@3.2]
    input clock : Clock @[:@4.4]
    input reset : UInt<1> @[:@5.4]
    input io_re : UInt<1> @[:@6.4]
    input io_we : UInt<1> @[:@6.4]
    input io_ra : UInt<6> @[:@6.4]
    input io_wa : UInt<6> @[:@6.4]
    input io_di : UInt<32> @[:@6.4]
    output io_dout : UInt<32> @[:@6.4]
  
    mem mem : @[nv_ram_rws.scala 24:26:@8.4]
      data-type => UInt<32>
      depth => 64
      read-latency => 1
      write-latency => 1
      reader => _T_25
      writer => _T_19
      read-under-write => undefined
    node _GEN_0 = validif(io_re, io_ra) @[nv_ram_rws.scala 31:28:@17.6]
    node _T_21 = _GEN_0 @[nv_ram_rws.scala 31:28:@15.6 nv_ram_rws.scala 31:28:@18.8]
    node _T_23 = or(_T_21, UInt<6>("h0")) @[nv_ram_rws.scala 31:28:@19.8]
    node _T_24 = bits(_T_23, 5, 0) @[nv_ram_rws.scala 31:28:@20.8]
    node _GEN_1 = mux(io_re, UInt<1>("h1"), UInt<1>("h0")) @[nv_ram_rws.scala 31:28:@17.6]
    node _GEN_2 = validif(io_re, _T_24) @[nv_ram_rws.scala 31:28:@17.6]
    node _GEN_3 = validif(io_re, clock) @[nv_ram_rws.scala 31:28:@17.6]
    node _GEN_4 = validif(io_we, io_wa) @[nv_ram_rws.scala 26:18:@9.4]
    node _GEN_5 = validif(io_we, clock) @[nv_ram_rws.scala 26:18:@9.4]
    node _GEN_6 = mux(io_we, UInt<1>("h1"), UInt<1>("h0")) @[nv_ram_rws.scala 26:18:@9.4]
    node _GEN_7 = validif(io_we, UInt<1>("h1")) @[nv_ram_rws.scala 26:18:@9.4]
    node _GEN_8 = validif(io_we, io_di) @[nv_ram_rws.scala 26:18:@9.4]
    node _GEN_9 = validif(eq(io_we, UInt<1>("h0")), mem._T_25.data) @[nv_ram_rws.scala 26:18:@9.4]
    node _GEN_10 = mux(io_we, UInt<1>("h0"), _GEN_1) @[nv_ram_rws.scala 26:18:@9.4]
    node _GEN_11 = validif(eq(io_we, UInt<1>("h0")), _GEN_2) @[nv_ram_rws.scala 26:18:@9.4]
    node _GEN_12 = validif(eq(io_we, UInt<1>("h0")), _GEN_3) @[nv_ram_rws.scala 26:18:@9.4]
    io_dout <= _GEN_9 @[nv_ram_rws.scala 31:17:@23.6]
    mem._T_25.addr <= _GEN_11 @[nv_ram_rws.scala 31:28:@21.8]
    mem._T_25.en <= _GEN_10 @[nv_ram_rws.scala 24:26:@8.4 nv_ram_rws.scala 31:28:@20.8]
    mem._T_25.clk <= _GEN_12 @[nv_ram_rws.scala 31:28:@21.8]
    mem._T_19.addr <= _GEN_4 @[:@10.6]
    mem._T_19.en <= _GEN_6 @[nv_ram_rws.scala 24:26:@8.4 :@10.6]
    mem._T_19.clk <= _GEN_5 @[:@10.6]
    mem._T_19.data <= _GEN_8 @[:@11.6]
    mem._T_19.mask <= _GEN_7 @[:@10.6 :@11.6]
