package nvdla

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}


class NV_NVDLA_MCIF_READ_IG_arbTests(c: NV_NVDLA_MCIF_READ_IG_arb) extends PeekPokeTester(c) {
 
  implicit val conf: xxifConfiguration = new xxifConfiguration

//==========================================================
//test dat data forwading 
//==========================================================  

  for(i<-0 until conf.RDMA_NUM) {
    poke(c.io.bpt2arb_req_valid(i), false)
    poke(c.io.bpt2arb_req_pd(i), 0)
  }

  poke(c.io.bpt2arb_req_valid(0), true)
  poke(c.io.bpt2arb_req_pd(0), 1111)
  poke(c.io.bpt2arb_req_valid(6), true)
  poke(c.io.bpt2arb_req_pd(6), 6666)
  poke(c.io.arb2spt_req_ready, true)


  step(4)
  expect(c.io.arb2spt_req_pd, 1111)

  poke(c.io.bpt2arb_req_valid(0), false)
  step(4)
  expect(c.io.arb2spt_req_pd, 6666)
}

class NV_NVDLA_MCIF_READ_IG_arbTester extends ChiselFlatSpec {

  behavior of "NV_NVDLA_MCIF_READ_IG_arb"
  backends foreach {backend =>
    it should s"correctly activate wt and dat $backend" in {
      implicit val conf: xxifConfiguration = new xxifConfiguration
      Driver(() => new NV_NVDLA_MCIF_READ_IG_arb())(c => new NV_NVDLA_MCIF_READ_IG_arbTests(c)) should be (true)
    }
  }
}
