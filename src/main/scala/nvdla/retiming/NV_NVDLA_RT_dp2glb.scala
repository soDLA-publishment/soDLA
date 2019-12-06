package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_RT_dp2glb(delay: Int)(implicit val conf: nvdlaConfig) extends RawModule {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //control signal
        val dp2glb_done_intr_src_pd = Input(UInt(2.W))
        val dp2glb_done_intr_dst_pd = Output(UInt(2.W))

    })

withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

    io.dp2glb_done_intr_dst_pd := ShiftRegister(io.dp2glb_done_intr_src_pd, delay, true.B)

}}