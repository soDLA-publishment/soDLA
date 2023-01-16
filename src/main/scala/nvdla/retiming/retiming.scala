package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

object retiming {

def apply[T <: Data](delay_input: T, t: Int): Seq[T] = Wire(delay_input) +: Seq.fill(t)(Reg(delay_input))

}





