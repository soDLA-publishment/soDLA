package nvdla

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class NV_NVDLA_CMAC_CORE_rt_inTests(c: NV_NVDLA_CMAC_CORE_rt_in) extends PeekPokeTester(c) {
 
  implicit val conf: nvdlaConfig = new nvdlaConfig

  for (t <- 0 until 100) {
    //load random inputs

    val sc2mac_dat_data = Array.fill(conf.CMAC_ATOMC){0}
    val sc2mac_dat_mask = Array.fill(conf.CMAC_ATOMC){false}
    val sc2mac_dat_pd = rnd.nextInt(1<<9)
    val sc2mac_dat_pvld = rnd.nextBoolean()

    val sc2mac_wt_data = Array.fill(conf.CMAC_ATOMC){0}
    val sc2mac_wt_mask = Array.fill(conf.CMAC_ATOMC){false}
    val sc2mac_wt_sel = Array.fill(conf.CMAC_ATOMK_HALF){false}
    val sc2mac_wt_pvld = rnd.nextBoolean()

    poke(c.io.sc2mac_dat.bits.pd, sc2mac_dat_pd)
    poke(c.io.sc2mac_dat.valid, sc2mac_dat_pvld)
    poke(c.io.sc2mac_wt.valid, sc2mac_wt_pvld)

    for (i <- 0 to conf.CMAC_ATOMC-1){

      sc2mac_dat_data(i) = rnd.nextInt(1<<conf.CMAC_BPE)
      sc2mac_dat_mask(i) = rnd.nextBoolean()

      sc2mac_wt_data(i) = rnd.nextInt(1<<conf.CMAC_BPE)
      sc2mac_wt_mask(i) = rnd.nextBoolean()

      poke(c.io.sc2mac_dat.bits.data(i), sc2mac_dat_data(i))
      poke(c.io.sc2mac_dat.bits.mask(i), sc2mac_dat_mask(i))

      poke(c.io.sc2mac_wt.bits.data(i), sc2mac_wt_data(i))
      poke(c.io.sc2mac_wt.bits.mask(i), sc2mac_wt_mask(i))

    }

    for (i <- 0 to conf.CMAC_ATOMK_HALF-1){

      sc2mac_wt_sel(i) = rnd.nextBoolean()

      poke(c.io.sc2mac_wt.bits.sel(i), sc2mac_wt_sel(i))

    }

    //after rt_in
    step(conf.CMAC_IN_RT_LATENCY)

    //check the result

    //dat valid
    expect(c.io.in_dat.valid, sc2mac_dat_pvld)
    if(sc2mac_dat_pvld){
      for (i <- 0 to conf.CMAC_ATOMC-1){
        //dat mask
        expect(c.io.in_dat.bits.mask(i), sc2mac_dat_mask(i))
        //dat data
        if(sc2mac_dat_mask(i)){
          expect(c.io.in_dat.bits.data(i), sc2mac_dat_data(i))
        }}
      //dat pd
      expect(c.io.in_dat.bits.pd, sc2mac_dat_pd)
    }

    //wt valid
    expect(c.io.in_wt.valid, sc2mac_wt_pvld)
    if(sc2mac_wt_pvld){
      for (i <- 0 to conf.CMAC_ATOMC-1){
        //wt mask
        expect(c.io.in_wt.bits.mask(i), sc2mac_wt_mask(i))

        if(sc2mac_wt_mask(i)){
          //wt data
          expect(c.io.in_wt.bits.data(i), sc2mac_wt_data(i))
        }
      }     
      for (j <- 0 to conf.CMAC_ATOMK_HALF-1){
        //wt sel
        expect(c.io.in_wt.bits.sel(j), sc2mac_wt_sel(j))
      }  
    }


}}

class NV_NVDLA_CMAC_CORE_rt_inTester extends ChiselFlatSpec {

  behavior of "NV_NVDLA_CMAC_CORE_rt_in"
  backends foreach {backend =>
    it should s"correctly retiming wt and dat $backend" in {
      implicit val conf: nvdlaConfig = new nvdlaConfig
      Driver(() => new NV_NVDLA_CMAC_CORE_rt_in())(c => new NV_NVDLA_CMAC_CORE_rt_inTests(c)) should be (true)
    }
  }
}