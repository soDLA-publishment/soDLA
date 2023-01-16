package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class NV_NVDLA_BC_pipeTests(c: NV_NVDLA_BC_pipe) extends PeekPokeTester(c) {
  for(t <- 0 to 9){
    
    val wdata = rnd.nextInt(1<<32)
    poke(c.io.vi, 1)
    poke(c.io.di, wdata)
    poke(c.io.ri, 1)

    step(1)

    val wdata2 = rnd.nextInt(1<<32)
    poke(c.io.vi, 1)
    poke(c.io.di, wdata)
    poke(c.io.ri, 1)

    expect(c.io.vo, 1)
    expect(c.io.ro, 1)
    expect(c.io.dout, wdata)

    step(1)

    expect(c.io.vo, 1)
    expect(c.io.ro, 1)
    expect(c.io.dout, wdata2)

  }
}


class NV_NVDLA_BC_pipeTester extends ChiselFlatSpec {
  behavior of "NV_NVDLA_BC_pipe"
  backends foreach {backend =>
    it should s"... $backend" in {
      Driver(() => new NV_NVDLA_BC_pipe(32))(c => new NV_NVDLA_BC_pipeTests(c)) should be (true)
    }
  }
}