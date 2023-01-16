package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class NV_NVDLA_CMAC_CORE_macTests(c: NV_NVDLA_CMAC_CORE_mac) extends PeekPokeTester(c) {
 
  implicit val conf: nvdlaConfig = new nvdlaConfig

  for (t <- 0 until 1000) {

    val wt = Array.fill(conf.CMAC_ATOMC){0}
    val wt_nz = Array.fill(conf.CMAC_ATOMC){false}
    val wt_pvld = Array.fill(conf.CMAC_ATOMC){false}

    val dat = Array.fill(conf.CMAC_ATOMC){0}
    val dat_nz = Array.fill(conf.CMAC_ATOMC){false}
    val dat_pvld = Array.fill(conf.CMAC_ATOMC){false}

    val mout = Array.fill(conf.CMAC_ATOMC){0}

    for (i <- 0 to conf.CMAC_ATOMC-1){

      wt(i) = rnd.nextInt(2*(1<<(conf.CMAC_BPE-1)-1)) - (1 << (conf.CMAC_BPE-1) - 1)
      wt_nz(i) = rnd.nextBoolean()
      wt_pvld(i) = rnd.nextBoolean()

      dat(i) = rnd.nextInt(2*(1<<(conf.CMAC_BPE-1)-1)) - (1 << (conf.CMAC_BPE-1) - 1)
      dat_nz(i) = rnd.nextBoolean()
      dat_pvld(i) = rnd.nextBoolean()

      poke(c.io.wt_actv(i).bits.data, wt(i))
      poke(c.io.wt_actv(i).bits.nz, wt_nz(i))
      poke(c.io.wt_actv(i).valid, wt_pvld(i))

      poke(c.io.dat_actv(i).bits.data, dat(i))
      poke(c.io.dat_actv(i).bits.nz, dat_nz(i))
      poke(c.io.dat_actv(i).valid, dat_pvld(i))

      if(wt_nz(i)&wt_pvld(i)&dat_nz(i)&dat_pvld(i)){
           mout(i) = wt(i)*dat(i)
      }
      else{
           mout(i) = 0
      }
    }
    
    val sum_out = mout.reduce(_+_)

    step(conf.CMAC_OUT_RETIMING)

    if(wt_pvld(0)&dat_pvld(0)){
      expect(c.io.mac_out.bits, sum_out)
      expect(c.io.mac_out.valid, wt_pvld(0)&dat_pvld(0))
    }
  }
}

class NV_NVDLA_CMAC_CORE_macTester extends ChiselFlatSpec {

  behavior of "NV_NVDLA_CMAC_CORE_mac"
  backends foreach {backend =>
    it should s"correctly perform mac logic $backend" in {
      implicit val nvconf: nvdlaConfig = new nvdlaConfig
      Driver(() => new NV_NVDLA_CMAC_CORE_mac())(c => new NV_NVDLA_CMAC_CORE_macTests(c)) should be (true)
    }
  }
}
