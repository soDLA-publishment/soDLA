package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class MUX2D4Tests(c: MUX2D4) extends PeekPokeTester(c) {
  for (t <- 0 until 10) {
    val i0 = rnd.nextBoolean()
    val i1 = rnd.nextBoolean()
    val s = rnd.nextBoolean()

    poke(c.io.I0, i0)
    poke(c.io.I1, i1)
    poke(c.io.S, s)

    expect(c.io.Z, if(s) i1 else i0)
  }
}

class MUX2D2Tester extends ChiselFlatSpec {
  behavior of "MUX2D4"
  backends foreach {backend =>
    it should s"correctly randomly perform selecct logic $backend" in {
      Driver(() => new MUX2D4)(c => new MUX2D4Tests(c)) should be (true)
    }
  }
}

