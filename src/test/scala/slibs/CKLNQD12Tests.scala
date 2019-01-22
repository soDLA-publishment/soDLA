package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class CKLNQD12Tests(c: CKLNQD12) extends PeekPokeTester(c) {
  for (t <- 0 until 10) {
//wait for multiclock support

  }
}

class CKLNQD12Tester extends ChiselFlatSpec {
  behavior of "CKLNQD12"
  backends foreach {backend =>
    it should s"... $backend" in {
      Driver(() => new CKLNQD12)(c => new CKLNQD12Tests(c)) should be (true)
    }
  }
}