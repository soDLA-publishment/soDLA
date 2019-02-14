package nvdla

import chisel3.util._
import chisel3.testers.BasicTester
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class nv_ram_rwspTests(c: nv_ram_rwsp) extends PeekPokeTester(c) {

for(t <- 0 until 100){ 
    //write a data
    val in_data = rnd.nextInt(1<<32)
    val in_address = rnd.nextInt(1<<6)

    poke(c.io.we, true)
    poke(c.io.wa, in_address)
    poke(c.io.di, in_data)

    step(1)

    //read the data
    poke(c.io.re, true)
    poke(c.io.ore, true)
    poke(c.io.ra, in_address)

    step(1)
    expect(c.io.dout, in_data)
  
}}

class nv_ram_rwspTester extends ChiselFlatSpec {

  behavior of "nv_ram_rwsp"
  backends foreach {backend =>
    it should s"read and write$backend" in {
      Driver(() => new nv_ram_rwsp(64, 32))(c => new nv_ram_rwspTests(c)) should be (true)
    }
  }
}
