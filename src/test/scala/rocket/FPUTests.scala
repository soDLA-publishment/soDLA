package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class MulAddRecFNPipeTests(c: MulAddRecFNPipe) extends PeekPokeTester(c) {

}

class MulAddRecFNPipeTester extends ChiselFlatSpec {
  behavior of "MulAddRecFNPipe"
  backends foreach {backend =>
    it should s"correctly randomly and generated logic $backend" in {
      Driver(() => new MulAddRecFNPipe(2, 18, 18))(c => new MulAddRecFNPipeTests(c)) should be (true)
    }
  }
}
