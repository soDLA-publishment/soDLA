package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class read_ig_arbTests(c: read_ig_arb) extends PeekPokeTester(c) {
    implicit val conf: xxifConfiguration = new xxifConfiguration

  //==========================================================
  //test dat data forwading
  //==========================================================
    val req = Array.fill(10) {false}
    val wt =  Array( 0, 1, 2, 4, 6, 8, 16, 32, 64, 128).reverse

    poke(c.io.gnt_busy, false)

    for(i<-0 until 10) {
        poke(c.io.req(i), req(i))
        poke(c.io.wt(i), wt(i))
    }

    poke(c.io.gnt_busy, true)
    step(2)

    poke(c.io.gnt_busy, false)
    poke(c.io.req(0), true)
    poke(c.io.req(2), true)

    step(2)
    expect(c.io.gnt, 1)

    poke(c.io.req(0), false)
    poke(c.io.req(3), true)
    step(2)
    expect(c.io.gnt, 4)


    poke(c.io.req(2), false)
    step(2)
    expect(c.io.gnt, 8)


    poke(c.io.req(3), false)
    step(5)
    expect(c.io.gnt, 0)
}

class read_ig_arbTester extends ChiselFlatSpec {
  behavior of "read_ig_arb"
  backends foreach {backend =>
    it should s"correctly activate wt and dat $backend" in {
      implicit val conf: xxifConfiguration = new xxifConfiguration
      Driver(() => new read_ig_arb())(c => new read_ig_arbTests(c)) should be (true)
    }
  }
}
