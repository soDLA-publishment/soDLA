package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

object retiming {

def apply[T <: Data](delay_input: T, t: Int): Vec[T] =  Reg(Vec(Seq.fill(t)(delay_input)))

}

object retimingInit {

def apply[T <: Data](delay_input: T, t: Int): Vec[T] =  RegInit(VecInit(Seq.fill(t)(delay_input)))

}



