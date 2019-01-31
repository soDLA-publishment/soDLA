package ChiselFloat

import chisel3._
import chisel3.experimental._
import chisel3.util._

// Wraps a Chisel Flo or Dbl datatype to allow easy
// extraction of the different parts (sign, exponent, mantissa)

class FloatWrapper(val num: Bits) {
    val (sign, exponent, mantissa, zero) = num.getWidth match {
        case 32 => (num(31),
                    num(30, 23),
                    // if the exponent is 0
                    // this is a denormalized number
                    Cat(Mux(num(30, 23) === Bits(0),
                            Bits(0, 1), Bits(1, 1)),
                        num(22, 0)),
                    num(30, 0) === Bits(0))
        case 64 => (num(63),
                    num(62, 52),
                    Cat(Mux(num(62, 52) === Bits(0),
                            Bits(0, 1), Bits(1, 1)),
                        num(51, 0)),
                    num(62, 0) === Bits(0))
    }
}
