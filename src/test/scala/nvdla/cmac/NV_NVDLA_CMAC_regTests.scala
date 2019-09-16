package nvdla

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class regTestbench(implicit val conf: nvdlaConfig) extends Module {
  val io = IO(new Bundle {
    //general clock
    val nvdla_core_clk = Input(Clock())

    //csb2cmac
    val csb2cmac_a = new csb2dp_if

    //reg2dp
//    val reg2dp_op_en = Output(Bool())
//    val reg2dp_field = new cmac_reg_dual_flop_outputs
    val dp2reg_done = Input(Bool())

    //slave cg op
//    val slcg_op_en = Output(UInt(conf.CMAC_SLCG_NUM.W))

    val out = Output(Bool())
  })

  val golden = Module(new nvdla.cmac.NV_NVDLA_CMAC_reg_golden())
  val dut = Module(new NV_NVDLA_CMAC_reg())

  dut.io.nvdla_core_clk := io.nvdla_core_clk
  dut.io.csb2cmac_a.req.valid := io.csb2cmac_a.req.valid
  dut.io.csb2cmac_a.req.bits := io.csb2cmac_a.req.bits
  dut.io.dp2reg_done := io.dp2reg_done

  golden.io.nvdla_core_clk := dut.clock.asUInt.asBool()
  golden.io.nvdla_core_rstn := dut.reset
  golden.io.csb2cmac_a_req_pvld := io.csb2cmac_a.req.valid
  golden.io.csb2cmac_a_req_pd := io.csb2cmac_a.req.bits
  golden.io.dp2reg_done := io.dp2reg_done

  io.out := (dut.io.csb2cmac_a.resp.bits === golden.io.cmac_a2csb_resp_pd) &
    (dut.io.csb2cmac_a.resp.valid === golden.io.cmac_a2csb_resp_valid) &
    (dut.io.dp2reg_done === golden.io.dp2reg_done) &
    (dut.io.reg2dp_field.conv_mode === golden.io.reg2dp_conv_mode) &
    (dut.io.reg2dp_field.proc_precision === golden.io.reg2dp_proc_precision) &
    (dut.io.slcg_op_en === golden.io.slcg_op_en)

  io.csb2cmac_a.req.ready := 0.U
  io.csb2cmac_a.resp.valid := 0.U
  io.csb2cmac_a.resp.bits := 0.U

}

object getVerilog extends App{
  implicit val config = new nvdlaConfig()
  chisel3.Driver.execute(Array("--target-dir", "test_run_dir"), () => new regTestbench())
}

class NV_NVDLA_CMAC_reg_Tests(c: regTestbench) extends PeekPokeTester(c) {

  val R = scala.util.Random

  val in_csb2cmac_a_req_pd = (R.nextInt(4) << 61) + (R.nextInt(16) << 57) + (R.nextInt(0x8) << 54) +
    (R.nextInt(65536) << 22) + 0x8
  val in_csb2cmac_a_req_pvld = R.nextBoolean()
  val in_dp2reg_done = R.nextBoolean()

  poke(c.io.csb2cmac_a.req.valid, in_csb2cmac_a_req_pvld)
  poke(c.io.csb2cmac_a.req.bits, in_csb2cmac_a_req_pd)
  poke(c.io.dp2reg_done, in_dp2reg_done)

  step(5)

  expect(c.io.out, 1)

//  val out_cmac_a2csb_resp_pd = peek(c.io.csb2cmac_a.resp.bits)
//  val out_cmac_a2csb_resp_valid = peek(c.io.csb2cmac_a.resp.valid)
//  val out_csb2cmac_a_req_prdy = peek(c.io.csb2cmac_a.req.valid)
//  val out_reg2dp_conv_mode = peek(c.io.reg2dp_field.conv_mode)
//  val out_reg2dp_op_en = peek(c.io.reg2dp_op_en)
//  val out_reg2dp_proc_precision = peek(c.io.reg2dp_field.proc_precision)
//  val out_slcg_op_en = peek(c.io.slcg_op_en)

//  val out_list = Array(
//    out_cmac_a2csb_resp_pd,
//    out_cmac_a2csb_resp_valid,
//    out_csb2cmac_a_req_prdy,
//    out_reg2dp_conv_mode,
//    out_reg2dp_op_en,
//    out_reg2dp_proc_precision,
//    out_slcg_op_en
//  )

//  println("output result")
//  out_list.foreach((x) => {
//    println(x.toString(16))
//  })

}

class NV_NVDLA_CMAC_reg_Tester extends ChiselFlatSpec {
  implicit val config = new nvdlaConfig()
  behavior of "NV_NVDLA_CMAC_reg"
  "running with --generate-vcd-output on" should "create a vcd file from your test" in {
    val args = Array("-tbn", "verilator", "-tgvo", "on", "--target-dir", "test_run_dir/NV_NVDLA_CMAC_reg_Tester",
      "--top-name", "NV_NVDLA_CMAC_reg_Tester")
    Driver.execute(args, () => new regTestbench) {
      c => new NV_NVDLA_CMAC_reg_Tests(c)
    } should be(true)
  }
}
