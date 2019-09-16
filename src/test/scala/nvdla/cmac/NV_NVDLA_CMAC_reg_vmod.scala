package nvdla.cmac

import chisel3._
import chisel3.experimental._
import chisel3.util._
import org.graalvm.compiler.lir.BailoutAndRestartBackendException_OptionDescriptors

class NV_NVDLA_CMAC_reg_G extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle{
    val nvdla_core_clk = Input(Bool())
    val nvdla_core_rstn = Input(Bool())
    val csb2cmac_a_req_pd = Input(UInt(63.W))
    val csb2cmac_a_req_pvld = Input(Bool())
    val dp2reg_done = Input(Bool())
    val cmac_a2csb_resp_pd = Output(UInt(34.W))
    val cmac_a2csb_resp_valid = Output(Bool())
    val csb2cmac_a_req_prdy = Output(Bool())
    val reg2dp_conv_mode = Output(Bool())
    val reg2dp_op_en = Output(Bool())
    val reg2dp_proc_precision = Output(UInt(2.W))
    val slcg_op_en = Output(UInt(7.W))
  })

  setResource("/cmac/NV_NVDLA_CMAC_reg_G.v")
  setResource("/cmac/NV_NVDLA_CMAC_REG_dual_G.v")
  setResource("/cmac/NV_NVDLA_CMAC_REG_single_G.v")
}

class NV_NVDLA_CMAC_reg_golden extends Module{
  val io = IO{new Bundle{
    val nvdla_core_clk = Input(Bool())
    val nvdla_core_rstn = Input(Bool())
    val csb2cmac_a_req_pd = Input(UInt(63.W))
    val csb2cmac_a_req_pvld = Input(Bool())
    val dp2reg_done = Input(Bool())
    val cmac_a2csb_resp_pd = Output(UInt(34.W))
    val cmac_a2csb_resp_valid = Output(Bool())
    val csb2cmac_a_req_prdy = Output(Bool())
    val reg2dp_conv_mode = Output(Bool())
    val reg2dp_op_en = Output(Bool())
    val reg2dp_proc_precision = Output(UInt(2.W))
    val slcg_op_en = Output(UInt(7.W))
  }}
  val box = Module(new NV_NVDLA_CMAC_reg_G)
  box.io.nvdla_core_clk := io.nvdla_core_clk
  box.io.nvdla_core_rstn := io.nvdla_core_rstn
  box.io.csb2cmac_a_req_pd := io.csb2cmac_a_req_pd
  box.io.csb2cmac_a_req_pvld := io.csb2cmac_a_req_pvld
  box.io.dp2reg_done := io.dp2reg_done
  io.cmac_a2csb_resp_pd := box.io.cmac_a2csb_resp_pd
  io.cmac_a2csb_resp_valid := box.io.cmac_a2csb_resp_valid
  io.csb2cmac_a_req_prdy := box.io.csb2cmac_a_req_prdy
  io.reg2dp_conv_mode := box.io.reg2dp_conv_mode
  io.reg2dp_op_en := box.io.reg2dp_op_en
  io.reg2dp_proc_precision := box.io.reg2dp_proc_precision
  io.slcg_op_en := box.io.slcg_op_en

}

class testio extends Bundle{
  val i1 = Input(UInt(3.W))
  val o1 = Output(UInt(3.W))
}

class test1 extends Module{
  val io = IO(new Bundle{
    val x = Decoupled(UInt(3.W))
  })
  io.x.bits := 0.U
  io.x.valid := 0.U
}

class test extends Module{
  val io = IO(new Bundle{
    val x = Decoupled(UInt(3.W))
  })
  val t = Module(new test1)
  val tmp = WireInit(io.x)
  io.x.valid := 0.U
  io.x.bits := 0.U
  t.io.x <> tmp

}

object getVerilog extends App{
  chisel3.Driver.execute(Array("--target-dir", "test_run_dir"), () => new test)
}