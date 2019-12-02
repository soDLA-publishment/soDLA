package nvdla

import chisel3._
import chisel3.util._


class sync_reset extends Module {
    val io = IO(new Bundle {
        val clk= Input(Clock())
        val inreset_ = Input(Bool())
        val direct_reset_ = Input(Bool())
        val test_mode = Input(Bool())
        val outreset_ = Output(Bool())
    })
    val reset_ = Wire(Bool())
    val inreset_tm_ = Wire(Bool())
    val inreset_xclamp_ = Wire(Bool())
    val dft_xclamp_ctrl_cdc_sync_reset = Wire(Bool())

    val UJ_dft_xclamp_ctrl_cdc_sync_reset = Module(new NV_BLKBOX_SRC0)
    dft_xclamp_ctrl_cdc_sync_reset := UJ_dft_xclamp_ctrl_cdc_sync_reset.io.Y


    val UJ_inreset_x_clamp = Module(new OR2D1)
    UJ_inreset_x_clamp.io.A1 := io.inreset_
    UJ_inreset_x_clamp.io.A2 := dft_xclamp_ctrl_cdc_sync_reset
    inreset_xclamp_ := UJ_inreset_x_clamp.io.Z

    val UI_test_mode_inmux = Module(new MUX2D4)
    UI_test_mode_inmux.io.S := io.test_mode
    UI_test_mode_inmux.io.I1 := io.direct_reset_
    UI_test_mode_inmux.io.I0 := io.inreset_
    inreset_tm_ := UI_test_mode_inmux.io.Z

    val NV_GENERIC_CELL = Module(new p_SSYNC2DO_C_PP)
    NV_GENERIC_CELL.io.clk := io.clk
    NV_GENERIC_CELL.io.clr_ := inreset_tm_
    NV_GENERIC_CELL.io.d := inreset_xclamp_
    reset_ := NV_GENERIC_CELL.io.q

    val UI_test_mode_outmux = Module(new MUX2D4)
    UI_test_mode_outmux.io.S := io.test_mode
    UI_test_mode_outmux.io.I1 := io.direct_reset_
    UI_test_mode_outmux.io.I0 := reset_
    io.outreset_ := UI_test_mode_outmux.io.Z
    
}
