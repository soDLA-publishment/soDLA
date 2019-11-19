package nvdla

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import chisel3.util.ValidIO

import scala.util.Random

class NV_NVDLA_CMAC_CORE_activateTest(c: NV_NVDLA_CMAC_CORE_active) extends PeekPokeTester(c){
  def encourage_data(size:Int = 0): Map[String, List[Int]]={
    val sel = List.fill[Int](c.conf.NVDLA_MAC_ATOMIC_K_SIZE_DIV2)(1)
    val mask = List.fill[Int](c.conf.NVDLA_MAC_ATOMIC_C_SIZE)(Random.nextInt(2))
    val data = List.fill[Int](c.conf.NVDLA_MAC_ATOMIC_C_SIZE)(Random.nextInt(256))
    val valid = List.fill[Int](1)(1)
    (c.io.in_dat.bits.sel, sel).zipped.foreach(poke(_, _))
    (c.io.in_dat.bits.mask, mask).zipped.foreach(poke(_, _))
    (c.io.in_dat.bits.data, data).zipped.foreach(poke(_, _))
    poke(c.io.in_dat.valid, valid(0))
    Map("sel"->sel, "mask"->mask, "data"->data, "valid"->valid)
  }

  def encourage_wt(size:Int = 0): Map[String, List[Int]] ={
    val sel = List.fill[Int](c.conf.NVDLA_MAC_ATOMIC_K_SIZE_DIV2)(1)
    val mask = List.fill[Int](c.conf.NVDLA_MAC_ATOMIC_C_SIZE)(1)
    val wt = List.fill[Int](c.conf.NVDLA_MAC_ATOMIC_C_SIZE)(Random.nextInt(256))
    val valid = List.fill[Int](1)(1)
    (c.io.in_wt.bits.sel, sel).zipped.foreach(poke(_, _))
    (c.io.in_wt.bits.mask, mask).zipped.foreach(poke(_, _))
    (c.io.in_wt.bits.data, wt).zipped.foreach(poke(_, _))
    poke(c.io.in_wt.valid, valid(0))
    Map("sel"->sel, "mask"->mask, "wt"->wt, "valid"->valid)
  }
  // --- test data channel
  // step_1: push data
  val data_info = encourage_data()
  step(2)
  // step_2: check data out
  for (i <- 0 until c.io.dat_actv.length){
    for(j <-0 until c.io.dat_actv(i).length){
      expect(c.io.dat_actv(i)(j).valid, data_info("valid")(0))
      expect(c.io.dat_actv(i)(j).bits.nz, data_info("mask")(j))
      if (peek(c.io.dat_actv(i)(j).bits.nz) == 1){
        expect(c.io.dat_actv(i)(j).bits.data, data_info("data")(j))
      }
    }
  }
  step(1)

  // --- test wt channel
  // step_1: push wt
  val wt_info = encourage_wt()
  poke(c.io.in_dat_stripe_st, 0)
  poke(c.io.in_dat_stripe_end, 1)
  step(3)
  // step_2: let module pop wt
  poke(c.io.in_dat_stripe_st, 1)
  poke(c.io.in_dat_stripe_end, 0)
  step(3)
  // step_3: check wt out
  for (i <- 0 until c.io.wt_actv.length){
    for(j <-0 until c.io.wt_actv(i).length){
      expect(c.io.wt_actv(i)(j).valid, wt_info("valid")(0))
      expect(c.io.wt_actv(i)(j).bits.nz, wt_info("mask")(j))
      if (peek(c.io.wt_actv(i)(j).bits.nz) == 1){
        expect(c.io.wt_actv(i)(j).bits.data, wt_info("wt")(j))
      }
    }
  }
}

class NV_NVDLA_CMAC_CORE_activateTester extends ChiselFlatSpec{
  "running with --generate-vcd-output on" should "create a vcd file from your test" in {
    implicit val cmacconf: cmacConfiguration = new cmacConfiguration
    implicit val nvconf: nvdlaConfig = new nvdlaConfig
    iotesters.Driver.execute(
      Array(
        "--generate-vcd-output", "on",
        "--target-dir", "test_run_dir/make_core_activate_vcd",
        "--top-name", "make_core_activate_vcd",
        "--backend-name", "verilator",
        // "-tmvf", "-full64 -cpp g++-4.8 -cc gcc-4.8 -LDFLAGS -Wl,-no-as-needed +memcbk  +vcs+dumparrays -debug_all"
      ),
      () => new NV_NVDLA_CMAC_CORE_active()
    ) {
      c => new NV_NVDLA_CMAC_CORE_activateTest(c)
    } should be(true)
  }
}
