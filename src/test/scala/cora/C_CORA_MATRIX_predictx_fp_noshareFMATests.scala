package cora

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class C_CORA_MATRIX_v2v_fp_noshareFMATests(c: C_CORA_MATRIX_v2v_fp_noshareFMA) extends PeekPokeTester(c) {
implicit val conf: matrixConfiguration = new matrixConfiguration
}

class C_CORA_MATRIX_predictx_fp_noshareFMATester extends ChiselFlatSpec {
  behavior of "C_CORA_MATRIX_v2v_fp_noshareFMA"
  backends foreach {backend =>
    it should s"correctly randomly and generated logic $backend" in {
      implicit val conf: matrixConfiguration = new matrixConfiguration
      Driver(() => new C_CORA_MATRIX_v2v_fp_noshareFMA)(c => new C_CORA_MATRIX_v2v_fp_noshareFMATests(c)) should be (true)
    }
  }
}
