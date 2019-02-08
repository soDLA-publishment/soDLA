package cora

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class RED_CORA_MATRIX_v2mTests(c: RED_CORA_MATRIX_v2m) extends PeekPokeTester(c) {
implicit val conf: matrixConfiguration = new matrixConfiguration
}

class RED_CORA_MATRIX_v2mTester extends ChiselFlatSpec {
  behavior of "RED_CORA_MATRIX_v2m"
  backends foreach {backend =>
    it should s"correctly randomly and generated logic $backend" in {
      implicit val conf: matrixConfiguration = new matrixConfiguration
      Driver(() => new RED_CORA_MATRIX_v2m)(c => new RED_CORA_MATRIX_v2mTests(c)) should be (true)
    }
  }
}
