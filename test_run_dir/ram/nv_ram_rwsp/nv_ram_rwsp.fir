;buildInfoPackage: chisel3, version: 3.1.6, scalaVersion: 2.12.4, sbtVersion: 1.1.1, builtAtString: 2018-12-21 23:41:18.566, builtAtMillis: 1545435678566
circuit nv_ram_rwsp : 
  module nv_ram_rwsp : 
    input clock : Clock
    input reset : UInt<1>
    output io : {flip re : UInt<1>, flip we : UInt<1>, flip ore : UInt<1>, flip ra : UInt<6>, flip wa : UInt<6>, flip pwrbus_ram_pd : UInt<32>, flip di : UInt<32>, dout : UInt<32>}
    
    smem mem : UInt<32>[64] @[nv_ram_rwsp.scala 27:22]
    when io.we : @[nv_ram_rwsp.scala 29:14]
      write mport _T_23 = mem[io.wa], clock
      _T_23 <= io.di
      io.dout is invalid @[nv_ram_rwsp.scala 31:13]
      skip @[nv_ram_rwsp.scala 29:14]
    else : @[nv_ram_rwsp.scala 33:11]
      wire _T_25 : UInt @[nv_ram_rwsp.scala 34:28]
      _T_25 is invalid @[nv_ram_rwsp.scala 34:28]
      when io.re : @[nv_ram_rwsp.scala 34:28]
        _T_25 <= io.ra @[nv_ram_rwsp.scala 34:28]
        node _T_27 = or(_T_25, UInt<6>("h00")) @[nv_ram_rwsp.scala 34:28]
        node _T_28 = bits(_T_27, 5, 0) @[nv_ram_rwsp.scala 34:28]
        read mport _T_29 = mem[_T_28], clock @[nv_ram_rwsp.scala 34:28]
        skip @[nv_ram_rwsp.scala 34:28]
      when io.ore : @[nv_ram_rwsp.scala 35:18]
        reg _T_31 : UInt, clock @[nv_ram_rwsp.scala 36:27]
        _T_31 <= _T_29 @[nv_ram_rwsp.scala 36:27]
        io.dout <= _T_31 @[nv_ram_rwsp.scala 36:17]
        skip @[nv_ram_rwsp.scala 35:18]
      else : @[nv_ram_rwsp.scala 38:15]
        io.dout is invalid @[nv_ram_rwsp.scala 39:17]
        skip @[nv_ram_rwsp.scala 38:15]
      skip @[nv_ram_rwsp.scala 33:11]
    
