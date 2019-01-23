package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class NV_NVDLA_CMAC_CORE_macSINTTests(c: NV_NVDLA_CMAC_CORE_macSINT) extends PeekPokeTester(c) {
 
  implicit val conf: cmacSINTConfiguration = new cmacSINTConfiguration

  for (t <- 0 until 100) {

    val wt = Array.fill(conf.CMAC_ATOMC){0}
    val wt_nz = Array.fill(conf.CMAC_ATOMC){false}
    val wt_pvld = Array.fill(conf.CMAC_ATOMC){false}

    val dat = Array.fill(conf.CMAC_ATOMC){0}
    val dat_nz = Array.fill(conf.CMAC_ATOMC){false}
    val dat_pvld = Array.fill(conf.CMAC_ATOMC){false}

    val mout = Array.fill(conf.CMAC_ATOMC){0}

    for (i <- 0 until conf.CMAC_ATOMC-1){

      wt(i) = rnd.nextInt(2*(1<<(conf.CMAC_BPE-1)-1)) - (1 << (conf.CMAC_BPE-1) - 1)
      wt_nz(i) = true
      wt_pvld(i) = true

      dat(i) = rnd.nextInt(2*(1<<(conf.CMAC_BPE-1)-1)) - (1 << (conf.CMAC_BPE-1) - 1) 
      dat_nz(i) = true
      dat_pvld(i) = true

      poke(c.io.wt_actv_data(i), wt(i))
      poke(c.io.wt_actv_nz(i), wt_nz(i))
      poke(c.io.wt_actv_pvld(i), wt_pvld(i))

      poke(c.io.dat_actv_data(i), dat(i))
      poke(c.io.dat_actv_nz(i), dat_nz(i))
      poke(c.io.dat_actv_pvld(i), dat_pvld(i))

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
      expect(c.io.mac_out_data, sum_out)
      expect(c.io.mac_out_pvld, wt_pvld(0)&dat_pvld(0))
    }
  }
}

class NV_NVDLA_CMAC_CORE_macSINTTester extends ChiselFlatSpec {

  behavior of "NV_NVDLA_CMAC_CORE_macSINT"
  backends foreach {backend =>
    it should s"correctly perform mac logic $backend" in {
      implicit val conf: cmacSINTConfiguration = new cmacSINTConfiguration
      Driver(() => new NV_NVDLA_CMAC_CORE_macSINT())(c => new NV_NVDLA_CMAC_CORE_macSINTTests(c)) should be (true)
    }
  }
}
