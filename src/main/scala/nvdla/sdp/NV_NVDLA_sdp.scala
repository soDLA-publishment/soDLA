package nvdla

import chisel3._

class nvdla(addressWidth: Int) extends BlackBox {

    val nvdla_core_io = IO(new Bundle {
        val clockB = Input(Clock())
        val resetB = Input(Bool())
        val stuff = Input(Bool())
    })

