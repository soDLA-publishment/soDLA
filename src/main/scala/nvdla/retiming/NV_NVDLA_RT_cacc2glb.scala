package nvdla

import chisel3._
import chisel3.experimental._


class NV_NVDLA_RT_cacc2glb(implicit val conf: cacc2glbConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //control signal
        val cacc2glb_done_intr_src_pd = Input(UInt(2.W))
        val cacc2glb_done_intr_dst_pd = Output(UInt(2.W))

    })

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        io.cacc2glb_done_intr_dst_pd := ShiftRegister(io.cacc2glb_done_intr_src_pd, 2, true.B)
    } 
  }