package nvdla

import chisel3._
import chisel3.util._


class sync3d_s_ppp extends Module {
    val io = IO(new Bundle {
        val clk = Input(Clock())
        val d = Input(Bool())
        val set_ = Input(Bool())
        val q = Output(Bool())
    })

    val nv_GENERIC_CELL = Module(new p_SSYNC3DO_S_PPP())
    nv_GENERIC_CELL.io.d := io.d
    nv_GENERIC_CELL.io.clk := io.clk
    nv_GENERIC_CELL.io.set_ := io.set_
    io.q := nv_GENERIC_CELL.io.q
    
}