package nvdla

import chisel3._
import chisel3.util._


class sync3d_c_ppp extends Module {
    val io = IO(new Bundle {
        val clk = Input(Clock())
        val d = Input(Bool())
        val clr_ = Input(Bool())
        val q = Output(Bool())
    })

    val nv_GENERIC_CELL = Module(new p_SSYNC3DO_C_PPP())
    nv_GENERIC_CELL.io.d := io.d
    nv_GENERIC_CELL.io.clk := io.clk
    nv_GENERIC_CELL.io.clr_ := io.clr_
    io.q := nv_GENERIC_CELL.io.q
    
}