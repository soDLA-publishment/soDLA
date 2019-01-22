package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class OR2D1Tests(c: OR2D1) extends PeekPokeTester(c) {
  for (t <- 0 until 10) {
    val a1 = rnd.nextBoolean()
    val a2 = rnd.nextBoolean()

    poke(c.io.A1, a1)
    poke(c.io.A2, a2)

    val z = a1 | a2

    expect(c.io.Z, z)
  }
}

class OR2D1Tester extends ChiselFlatSpec {
  behavior of "OR2D1"
  backends foreach {backend =>
    it should s"correctly randomly perform or logic $backend" in {
      Driver(() => new OR2D1)(c => new OR2D1Tests(c)) should be (true)
    }
  }
}


