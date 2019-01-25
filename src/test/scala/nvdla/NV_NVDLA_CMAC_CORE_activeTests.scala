// package nvdla

// import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


// class NV_NVDLA_CMAC_CORE_activeTests(c: NV_NVDLA_CMAC_CORE_active) extends PeekPokeTester(c) {
 
//   implicit val conf: cmacConfiguration = new cmacConfiguration

//   for (t <- 0 until 100) {

//     //input_dat 
//     val in_dat_data = Array.fill(conf.CMAC_ATOMC){0}
//     val in_dat_mask = Array.fill(conf.CMAC_ATOMC){false}
//     val in_dat_pvld = false
//     val in_dat_stripe_st = false
//     val in_dat_stripe_end = false

//     //input_wt
//     val in_wt_data = Array.fill(conf.CMAC_ATOMC){0}
//     val in_wt_mask = Array.fill(conf.CMAC_ATOMC){false}
//     val in_wt_pvld = false
//     val in_wt_sel = Array.fill(conf.CMAC_ATOMK_HALF){false}

//     for (i <- 0 until conf.CMAC_ATOMC-1){

//       in_dat_data(i) = rnd.nextInt(1<<conf.CMAC_BPE)
//       in_dat_mask(i) = rnd.nextBoolean()
//       wt_pvld(i) = rnd.nextBoolean()

//       in_wt_data(i) = rnd.nextInt(1<<conf.CMAC_BPE)
//       in_wt_mask(i) = rnd.nextBoolean()
//       dat_pvld(i) = rnd.nextBoolean()

//       poke(c.io.wt_actv_data(i), wt(i))
//       poke(c.io.wt_actv_nz(i), wt_nz(i))
//       poke(c.io.wt_actv_pvld(i), wt_pvld(i))

//       poke(c.io.dat_actv_data(i), dat(i))
//       poke(c.io.dat_actv_nz(i), dat_nz(i))
//       poke(c.io.dat_actv_pvld(i), dat_pvld(i))

//     }
    
//     val sum_out = mout.reduce(_+_)

//     step(conf.CMAC_OUT_RETIMING)

//     if(wt_pvld(0)&dat_pvld(0)){
//       expect(c.io.mac_out_data, sum_out)
//       expect(c.io.mac_out_pvld, wt_pvld(0)&dat_pvld(0))
//     }
//   }
// }

// class NV_NVDLA_CMAC_CORE_activeTester extends ChiselFlatSpec {

//   behavior of "NV_NVDLA_CMAC_CORE_active"
//   backends foreach {backend =>
//     it should s"correctly activate wt and dat $backend" in {
//       implicit val conf: cmacConfiguration = new cmacConfiguration
//       Driver(() => new NV_NVDLA_CMAC_CORE_active())(c => new NV_NVDLA_CMAC_CORE_activeTests(c)) should be (true)
//     }
//   }
// }
