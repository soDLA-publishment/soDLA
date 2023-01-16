package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class MUX2HDD2Tests(c: MUX2HDD2) extends PeekPokeTester(c) {
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

class MUX2HDD2Tester extends ChiselFlatSpec {
  behavior of "MUX2HDD2"
  backends foreach {backend =>
    it should s"correctly randomly perform select logic $backend" in {
      Driver(() => new MUX2HDD2)(c => new MUX2HDD2Tests(c)) should be (true)
    }
  }
}


