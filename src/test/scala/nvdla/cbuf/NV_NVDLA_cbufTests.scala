package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class NV_NVDLA_cbufTests(c: NV_NVDLA_cbuf) extends PeekPokeTester(c) {
    implicit val conf: cbufConfiguration = new cbufConfiguration
}


class NV_NVDLA_cbufTester extends ChiselFlatSpec {
  behavior of "NV_NVDLA_cbuf"
  backends foreach {backend =>
    it should s"correctly randomly and generated logic $backend" in {
      implicit val conf: cbufConfiguration = new cbufConfiguration
      Driver(() => new NV_NVDLA_cbuf)(c => new NV_NVDLA_cbufTests(c)) should be (true)
    }
  }
}
