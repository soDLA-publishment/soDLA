package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class AN2D4PO4Tests(c: AN2D4PO4) extends PeekPokeTester(c) {
  for (t <- 0 until 1) {
    val a1 = rnd.nextBoolean()
    val a2 = rnd.nextBoolean()

    poke(c.io.A1, a1)
    poke(c.io.A2, a2)

    val z = a1 & a2

    expect(c.io.Z, z)
  }
}

class AN2D4PO4Tester extends ChiselFlatSpec {
  behavior of "AN2D4PO4"
  backends foreach {backend =>
    it should s"correctly randomly and generated logic $backend" in {
      Driver(() => new AN2D4PO4)(c => new AN2D4PO4Tests(c)) should be (true)
    }
  }
}
