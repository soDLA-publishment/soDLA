package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class NV_NVDLA_CMAC_CORE_rt_outTests(c: NV_NVDLA_CMAC_CORE_rt_out) extends PeekPokeTester(c) {
 
  implicit val conf: cmacConfiguration = new cmacConfiguration

  for (t <- 0 until 100) {
    //load random inputs
    val out_data = Array.fill(conf.CMAC_ATOMK_HALF){0}
    val out_mask = Array.fill(conf.CMAC_ATOMK_HALF){false}
    val out_pd = rnd.nextInt(1<<9)
    val out_pvld = rnd.nextBoolean()

    poke(c.io.out_pd, out_pd)
    poke(c.io.out_pvld, out_pvld)

    for (i <- 0 until conf.CMAC_ATOMK_HALF-1){

      out_data(i) = rnd.nextInt(1<<conf.CMAC_BPE)
      out_mask(i) = rnd.nextBoolean()

      poke(c.io.out_data(i), out_data(i))
      poke(c.io.out_mask(i), out_mask(i))

    }

    //after rt_out
    step(conf.CMAC_OUT_RT_LATENCY)

    //check the result
    //dat valid
    expect(c.io.mac2accu_pvld, out_pvld) 
    if(out_pvld){
      for (i <- 0 until conf.CMAC_ATOMK_HALF-1){
        //dat mask
        expect(c.io.mac2accu_mask(i), out_mask(i))
        //dat data
        if(out_mask(i)){
          expect(c.io.mac2accu_data(i), out_data(i))
        }}
      //dat pd
      expect(c.io.mac2accu_pd, out_pd)    
    }    
}}

class NV_NVDLA_CMAC_CORE_rt_outTester extends ChiselFlatSpec {

  behavior of "NV_NVDLA_CMAC_CORE_rt_out"
  backends foreach {backend =>
    it should s"correctly retiming wt and dat $backend" in {
      implicit val conf: cmacConfiguration = new cmacConfiguration
      Driver(() => new NV_NVDLA_CMAC_CORE_rt_out())(c => new NV_NVDLA_CMAC_CORE_rt_outTests(c)) should be (true)
    }
  }
}