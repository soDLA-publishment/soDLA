package ChiselFloat

import java.lang.Float.{floatToRawIntBits, intBitsToFloat}
import java.lang.Double.{doubleToRawLongBits, longBitsToDouble}
import java.math.BigInteger

object FloatUtils {
    def floatsToBigInt(floats: Seq[Float]): BigInt = {
        // extra '0' byte in front
        val float_array = floats.toArray
        var byte_array = new Array[Byte](1 + float_array.length * 4)

        byte_array(0) = 0

        for (i <- 0 until float_array.length) {
            val start_index = i * 4
            val rawint = floatToRawIntBits(float_array(i))

            for (j <- 1 to 4) {
                byte_array(start_index + j) =
                    ((rawint >> ((4 - j) * 8)) & 0xff).toByte
            }
        }

        BigInt(new BigInteger(byte_array))
    }

    def doublesToBigInt(doubles: Seq[Double]): BigInt = {
        val double_array = doubles.toArray
        val byte_array = new Array[Byte](1 + double_array.length * 8)

        byte_array(0) = 0

        for (i <- 0 until double_array.length) {
            val start_index = i * 8
            val rawlong = doubleToRawLongBits(double_array(i))

            for (j <- 1 to 8) {
                byte_array(start_index + j) =
                    ((rawlong >> ((8 - j) * 8)) & 0xff).toByte
            }
        }

        BigInt(new BigInteger(byte_array))
    }

    def floatToBigInt(x: Float): BigInt = {
        val integer = floatToRawIntBits(x)
        var byte_array = new Array[Byte](5)

        byte_array(0) = 0

        for (i <- 1 to 4) {
            byte_array(i) = ((integer >> ((4 - i) * 8)) & 0xff).toByte
        }

        BigInt(new BigInteger(byte_array))
    }

    def doubleToBigInt(x: Double): BigInt = {
        val integer = doubleToRawLongBits(x)
        var byte_array = new Array[Byte](9)

        byte_array(0) = 0

        for (i <- 1 to 8) {
            byte_array(i) = ((integer >> ((8 - i) * 8)) & 0xff).toByte
        }

        BigInt(new BigInteger(byte_array))
    }

    def getExpMantWidths(n: Int): (Int, Int) = {
        n match {
            case 32 => (8, 23)
            case 64 => (11, 52)
        }
    }

    def findFirstOne(num: Long, start: Int): Int = {
        for (i <- 0 to start) {
            val bit = ((num >> (start - i)) & 0x1)
            if (bit == 1) {
                return i
            }
        }
        return start
    }

    def doubleAdd(a: Double, b: Double): Double = {
        val abits = doubleToRawLongBits(a)
        val bbits = doubleToRawLongBits(b)

        val asign = (abits >> 63) & 0x1L
        val aexp  = (abits >> 52) & 0x7ffL
        val amant = (abits & ((1L << 52) - 1L)) | (if (aexp == 0) 0 else 1L << 52)

        val bsign = (bbits >> 63) & 0x1L
        val bexp  = (bbits >> 52) & 0x7ffL
        val bmant = (bbits & ((1L << 52) - 1L)) | (if (bexp == 0) 0 else 1L << 52)

        val (bigger_mant, smaller_mant, exp, sign) = if (aexp > bexp) {
            val shiftby = (aexp - bexp)
            (amant, bmant >> shiftby, aexp, asign)
        } else {
            val shiftby = (bexp - aexp)
            (bmant, amant >> shiftby, bexp, bsign)
        }

        val sub = (asign ^ bsign) == 1
        val sum = if (sub) {
            bigger_mant - smaller_mant
        } else {
            bigger_mant + smaller_mant
        }

        val (actualsign, abssum) = if (sum < 0) {
            (~sign & 1, -sum)
        } else {
            (sign, sum)
        }

        val (actualexp, actualsum) = if (abssum == 0) {
            (0L, 0L)
            // check bit 53
        } else if ((abssum & (1L << 53)) != 0) {
            (exp + 1, abssum >> 1)
        } else {
            val firstOne = findFirstOne(abssum, 52)
            (exp - firstOne, abssum << firstOne)
        }

        val rawres = (sign << 63) | ((actualexp & 0x7ffL) << 52) |
            (actualsum & ((1L << 52) - 1L))
        longBitsToDouble(rawres)
    }

    def floatAdd(a: Float, b: Float): Float = {
        val abits = floatToRawIntBits(a)
        val bbits = floatToRawIntBits(b)

        val asign = (abits >> 31) & 0x1
        val aexp  = (abits >> 23) & 0xff
        val amant = (abits & 0x7fffff) | (if (aexp == 0) 0 else 1 << 23)

        val bsign = (bbits >> 31) & 0x1
        val bexp  = (bbits >> 23) & 0xff
        val bmant = (bbits & 0x7fffff) | (if (bexp == 0) 0 else 1 << 23)

        val (bigger_mant, smaller_mant, exp, sign) = if (aexp > bexp) {
            val shiftby = (aexp - bexp)
            (amant, bmant >> shiftby, aexp, asign)
        } else {
            val shiftby = (bexp - aexp)
            (bmant, amant >> shiftby, bexp, bsign)
        }

        val sub = (asign ^ bsign) == 1
        val sum = if (sub) {
            bigger_mant - smaller_mant
        } else {
            bigger_mant + smaller_mant
        }

        val (actualsign, abssum) = if (sum < 0) {
            (~sign & 1, -sum)
        } else {
            (sign, sum)
        }

        val (actualexp, actualsum) = if (abssum == 0) {
            (0, 0)
            // check bit 24
        } else if ((abssum & (1 << 24)) != 0) {
            (exp + 1, abssum >> 1)
        } else {
            val firstOne = findFirstOne(abssum, 23)
            (exp - firstOne, abssum << firstOne)
        }

        val rawres = (actualsign << 31) | ((actualexp & 0xff) << 23) |
            (actualsum & 0x7fffff)
        intBitsToFloat(rawres)
    }
}
