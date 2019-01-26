package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class NV_NVDLA_CMAC_coreTests(c: NV_NVDLA_CMAC_core) extends PeekPokeTester(c) {
 
  implicit val conf: cmacConfiguration = new cmacConfiguration

}

class NV_NVDLA_CMAC_coreTester extends ChiselFlatSpec {

  behavior of "NV_NVDLA_CMAC_core"
  backends foreach {backend =>
    it should s"correctly retiming wt and dat $backend" in {
      implicit val conf: cmacConfiguration = new cmacConfiguration
      Driver(() => new NV_NVDLA_CMAC_core())(c => new NV_NVDLA_CMAC_coreTests(c)) should be (true)
    }
  }
}