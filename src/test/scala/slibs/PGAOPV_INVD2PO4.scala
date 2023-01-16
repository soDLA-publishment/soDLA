package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class PGAOPV_INVD2PO4Tests(c: PGAOPV_INVD2PO4) extends PeekPokeTester(c) {
  for (t <- 0 until 10) {
    val I = rnd.nextBoolean()
    poke(c.io.I, I)
    expect(c.io.ZN, !I)
  }
}

class PGAOPV_INVD2PO4Tester extends ChiselFlatSpec {
  behavior of "PGAOPV_INVD2PO4"
  backends foreach {backend =>
    it should s"correctly randomly perform selecct logic $backend" in {
      Driver(() => new PGAOPV_INVD2PO4)(c => new PGAOPV_INVD2PO4Tests(c)) should be (true)
    }
  }
}
