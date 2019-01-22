package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class NV_NVDLA_CMAC_CORE_macTests(c: NV_NVDLA_CMAC_CORE_mac) extends PeekPokeTester(c) {


}

class NV_NVDLA_CMAC_CORE_macTester extends ChiselFlatSpec {

  behavior of "NV_NVDLA_CMAC_CORE_mac"
  backends foreach {backend =>
    it should s"correctly perform mac logic $backend" in {
      implicit val conf: cmacConfiguration = new cmacConfiguration
      Driver(() => new NV_NVDLA_CMAC_CORE_mac())(c => new NV_NVDLA_CMAC_CORE_macTests(c)) should be (true)
    }
  }
}
