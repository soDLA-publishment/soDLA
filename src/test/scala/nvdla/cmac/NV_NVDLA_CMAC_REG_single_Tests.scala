package nvdla

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class NV_NVDLA_CMAC_REG_single_Tests(c: NV_NVDLA_CMAC_REG_single) extends PeekPokeTester(c) {

  val R = scala.util.Random

  val pointer_wren_offset = 0x7004 & 0x00000FFF
  val status_wren_offset = 0x7000 & 0x00000FFF

  var last_producer = false

  for (i <- 0 until 100) {

    val offset = if (i % 3 == 0) {
      R.nextInt((math.pow(2, 11) - 1).toInt)
    } else if (i % 3 == 1) {
      pointer_wren_offset
    } else {
      status_wren_offset
    }

    //input
    val in_reg_offset = offset
    val in_reg_wr_data = R.nextInt((math.pow(2, 32) - 1).toInt)
    val in_reg_wr_en = R.nextBoolean()
    val in_consumer = R.nextBoolean()
    val in_status_0 = R.nextInt(4)
    val in_status_1 = R.nextInt(4)

    val out_producer_sw: Boolean = if (offset == pointer_wren_offset & in_reg_wr_en) {
      (in_reg_wr_data % 2) == 1
    } else {
      peek(c.io.producer) == 1
    }
    val out_data_out_sw: BigInt = if (offset == pointer_wren_offset) {
      (in_consumer.toInt << 16) + out_producer_sw.toInt
    } else if (offset == status_wren_offset) {
      (in_status_1 << 16) + in_status_0
    } else {
      0
    }
    last_producer = out_producer_sw

    poke(c.io.reg.offset, in_reg_offset)
    poke(c.io.reg.wr_data, in_reg_wr_data)
    poke(c.io.reg.wr_en, in_reg_wr_en)
    poke(c.io.consumer, in_consumer)
    poke(c.io.status_0, in_status_0)
    poke(c.io.status_1, in_status_1)

    step(1)

    expect(c.io.reg.rd_data, out_data_out_sw)
    if (peek(c.io.reg.rd_data) != out_data_out_sw) {
      println(peek(c.io.reg.rd_data).toString)
      println(out_data_out_sw.toString)
      println("rd_data cnt" + i.toString)
    }
    expect(c.io.producer, out_producer_sw)
    if (peek(c.io.producer) != out_producer_sw.toInt) {
      println(peek(c.io.producer).toString)
      println(out_producer_sw.toInt.toString)
      println("producer cnt" + i.toString)
    }
    println("=================")
  }

}

class NV_NVDLA_CMAC_REG_single_Tester extends ChiselFlatSpec {
  behavior of "NV_NVDLA_CMAC_REG_single"
  "running with --generate-vcd-output on" should "create a vcd file from your test" in {
    val args = Array("-tbn", "verilator", "-tgvo", "on", "--target-dir", "test_run_dir/NV_NVDLA_CMAC_REG_single_Tester",
      "--top-name", "NV_NVDLA_CMAC_REG_single_Tester")
    Driver.execute(args, () => new NV_NVDLA_CMAC_REG_single) {
      c => new NV_NVDLA_CMAC_REG_single_Tests(c)
    } should be(true)
  }
}
