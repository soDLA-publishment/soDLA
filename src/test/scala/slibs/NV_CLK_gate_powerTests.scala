package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class NV_CLK_gate_powerTests(c: NV_CLK_gate_power) extends PeekPokeTester(c) {

}

class NV_CLK_gate_powerTester extends ChiselFlatSpec {
  behavior of "NV_CLK_gate_power"
  backends foreach {backend =>
    it should s"...$backend" in {
      Driver(() => new NV_CLK_gate_power)(c => new NV_CLK_gate_powerTests(c)) should be (true)
    }
  }
}
