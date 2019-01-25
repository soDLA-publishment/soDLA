package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class NV_NVDLA_CMAC_CORE_activeTests(c: NV_NVDLA_CMAC_CORE_active) extends PeekPokeTester(c) {
 
  implicit val conf: cmacConfiguration = new cmacConfiguration

//test dat data forwading

  for (t <- 0 until 100) {

    //input_dat 
    val in_dat_data = Array.fill(conf.CMAC_ATOMC){0}
    val in_dat_mask = Array.fill(conf.CMAC_ATOMC){false}
    val in_dat_pvld = rnd.nextBoolean()
    val in_dat_stripe_st = false
    val in_dat_stripe_end = false

    //input_wt
    val in_wt_data = Array.fill(conf.CMAC_ATOMC){0}
    val in_wt_mask = Array.fill(conf.CMAC_ATOMC){false}
    val in_wt_pvld = rnd.nextBoolean()
    val in_wt_sel = Array.fill(conf.CMAC_ATOMK_HALF){false}

    //assign st, end, pvld
    poke(c.io.in_dat_pvld, in_dat_pvld)
    poke(c.io.in_wt_pvld, in_wt_pvld)

    poke(c.io.in_dat_stripe_st, in_dat_stripe_st)
    poke(c.io.in_dat_stripe_end, in_dat_stripe_end)    

    //assign data, mask
    for (i <- 0 until conf.CMAC_ATOMC-1){

      in_dat_data(i) = rnd.nextInt(1<<conf.CMAC_BPE)
      in_dat_mask(i) = rnd.nextBoolean()

      in_wt_data(i) = rnd.nextInt(1<<conf.CMAC_BPE)
      in_wt_mask(i) = rnd.nextBoolean()

      poke(c.io.in_dat_data(i), in_dat_data(i))
      poke(c.io.in_dat_mask(i), in_dat_mask(i))

      poke(c.io.in_wt_data(i), in_wt_data(i))
      poke(c.io.in_wt_mask(i), in_wt_mask(i))

    }

    //assign wt sel
    for(i <- 0 until conf.CMAC_ATOMK_HALF-1){

      in_wt_sel(i) = rnd.nextBoolean()

    }

    step(2)

    //check dat valid
    for(i <- 0 until conf.CMAC_ATOMK_HALF-1){
        for(j <- 0 until conf.CMAC_ATOMC-1){
            expect(c.io.dat_actv_pvld(i)(j), in_dat_pvld)
      }
    }
    //check dat pack
    if(in_dat_pvld){
      for(i <- 0 until conf.CMAC_ATOMK_HALF-1){
        if(in_wt_sel(i)){
          for(j <- 0 until conf.CMAC_ATOMC-1){
            expect(c.io.dat_actv_nz(i)(j), in_dat_mask(j))
            if(in_dat_mask(j)){
              expect(c.io.dat_actv_data(i)(j), in_dat_data(j))
            }
          }
        }
      }
    }
  }
}

class NV_NVDLA_CMAC_CORE_activeTester extends ChiselFlatSpec {

  behavior of "NV_NVDLA_CMAC_CORE_active"
  backends foreach {backend =>
    it should s"correctly activate wt and dat $backend" in {
      implicit val conf: cmacConfiguration = new cmacConfiguration
      Driver(() => new NV_NVDLA_CMAC_CORE_active())(c => new NV_NVDLA_CMAC_CORE_activeTests(c)) should be (true)
    }
  }
}
