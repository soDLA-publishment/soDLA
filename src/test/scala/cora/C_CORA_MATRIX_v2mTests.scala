package cora

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class C_CORA_MATRIX_v2mTests(c: C_CORA_MATRIX_v2m) extends PeekPokeTester(c) {
implicit val conf: matrixConfiguration = new matrixConfiguration
}

class C_CORA_MATRIX_v2mTester extends ChiselFlatSpec {
  behavior of "C_CORA_MATRIX_v2m"
  backends foreach {backend =>
    it should s"correctly randomly and generated logic $backend" in {
      implicit val conf: matrixConfiguration = new matrixConfiguration
      Driver(() => new C_CORA_MATRIX_v2m)(c => new C_CORA_MATRIX_v2mTests(c)) should be (true)
    }
  }
}
