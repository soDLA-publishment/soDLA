;buildInfoPackage: chisel3, version: 3.1.6, scalaVersion: 2.12.4, sbtVersion: 1.1.1, builtAtString: 2018-12-21 23:41:18.566, builtAtMillis: 1545435678566
circuit nv_ram_rws : 
  module nv_ram_rws : 
    input clock : Clock
    input reset : UInt<1>
    output io : {flip re : UInt<1>, flip we : UInt<1>, flip ra : UInt<6>, flip wa : UInt<6>, flip di : UInt<32>, dout : UInt<32>}
    
    smem mem : UInt<32>[64] @[nv_ram_rws.scala 24:26]
    when io.we : @[nv_ram_rws.scala 26:18]
      write mport _T_19 = mem[io.wa], clock
      _T_19 <= io.di
      io.dout is invalid @[nv_ram_rws.scala 28:17]
      skip @[nv_ram_rws.scala 26:18]
    else : @[nv_ram_rws.scala 30:15]
      wire _T_21 : UInt @[nv_ram_rws.scala 31:28]
      _T_21 is invalid @[nv_ram_rws.scala 31:28]
      when io.re : @[nv_ram_rws.scala 31:28]
        _T_21 <= io.ra @[nv_ram_rws.scala 31:28]
        node _T_23 = or(_T_21, UInt<6>("h00")) @[nv_ram_rws.scala 31:28]
        node _T_24 = bits(_T_23, 5, 0) @[nv_ram_rws.scala 31:28]
        read mport _T_25 = mem[_T_24], clock @[nv_ram_rws.scala 31:28]
        skip @[nv_ram_rws.scala 31:28]
      io.dout <= _T_25 @[nv_ram_rws.scala 31:17]
      skip @[nv_ram_rws.scala 30:15]
    
