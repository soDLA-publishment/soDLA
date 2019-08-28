package nvdla

import util._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import utils.Tools

class NV_NVDLA_CMAC_REG_dual_Tests(c: NV_NVDLA_CMAC_REG_dual) extends PeekPokeTester(c) {

  val R = scala.util.Random

  val misc_cfg_wren_offset = 0x700C & 0x00000FFF
  val op_enable_wren_offset = 0x7008 & 0x00000FFF

  var last_proc_precison = 1

  for (i <- 0 until 100) {

    val offset = if (i % 3 == 0) {
      R.nextInt((math.pow(2, 11) - 1).toInt)
    } else if (i % 3 == 1) {
      misc_cfg_wren_offset
    } else {
      op_enable_wren_offset
    }

    //input
    val in_reg_offset = offset
    val in_reg_wr_data = R.nextInt((math.pow(2, 32) - 1).toInt)
    val in_reg_wr_en = R.nextBoolean()
    val in_op_en = R.nextBoolean()

    val out_conv_mode_sw = (if (offset == misc_cfg_wren_offset & in_reg_wr_en) {
      (in_reg_wr_data % 2) == 1
    } else {
      peek(c.io.field.conv_mode) == 1
    }).toInt
    val out_op_en_trigger_sw = (offset == op_enable_wren_offset & in_reg_wr_en).toInt
    val out_proc_precison_sw = if (offset == misc_cfg_wren_offset & in_reg_wr_en) {
      (in_reg_wr_data & 0x3000) >> 12
    } else {
      last_proc_precison
    }
    last_proc_precison = out_proc_precison_sw
    val out_data_out_sw: BigInt = if (offset == misc_cfg_wren_offset) {
      (out_proc_precison_sw << 12) + out_conv_mode_sw
    } else if (offset == op_enable_wren_offset) {
      in_op_en
    } else {
      0
    }

    poke(c.io.reg.offset, in_reg_offset)
    poke(c.io.reg.wr_data, in_reg_wr_data)
    poke(c.io.reg.wr_en, in_reg_wr_en)
    poke(c.io.op_en, in_op_en)

    step(1)

    expect(c.io.reg.rd_data, out_data_out_sw)
//    Tools.diffString(peek(c.io.reg.rd_data).toString, out_data_out_sw.toString, "rd_data")

    expect(c.io.field.conv_mode, out_conv_mode_sw)
//    Tools.diffString(peek(c.io.field.conv_mode).toString, out_conv_mode_sw.toString, "conv_mode")

    expect(c.io.op_en_trigger, out_op_en_trigger_sw)
//    Tools.diffString(peek(c.io.op_en_trigger).toString, out_op_en_trigger_sw.toString, "op_en_trigger")

    expect(c.io.field.proc_precision, out_proc_precison_sw)
//    Tools.diffString(peek(c.io.field.proc_precision).toString, out_proc_precison_sw.toString, "proc_precision")

    println("=======" + i.toString + "=========")
  }

}

class NV_NVDLA_CMAC_REG_dual_Tester extends ChiselFlatSpec {
  behavior of "NV_NVDLA_CMAC_REG_dual"

  "running with --generate-vcd-output on" should "create a vcd file from your test" in {
    val args = Array("-tbn", "verilator", "-tgvo", "on", "--target-dir", "test_run_dir/NV_NVDLA_CMAC_REG_dual_Tester",
      "--top-name", "NV_NVDLA_CMAC_REG_dual_Tester")
    Driver.execute(args, () => new NV_NVDLA_CMAC_REG_dual) {
      c => new NV_NVDLA_CMAC_REG_dual_Tests(c)
    } should be(true)
  }

}
