package ChiselFloat

import chisel3._
import chisel3.experimental._
import chisel3.util._
import FloatUtils.{floatToBigInt, doubleToBigInt}

class MantissaRounder(val n: Int) extends Module {
    val io = new Bundle {
        val in = Input(UInt(n.W))
        val out = Output(UInt((n-1).W))
    }

    io.out := io.in(n - 1, 1) + io.in(0)
}

class FPMult(val n: Int) extends Module {
    val io = new Bundle {
        val a = Input(UInt(n.W))
        val b = Input(UInt(n.W))
        val res = Output(UInt(n.W))
    }

    val a_wrap = new FloatWrapper(io.a)
    val b_wrap = new FloatWrapper(io.b)

    val stage1_sign = a_wrap.sign ^ b_wrap.sign
    val stage1_exponent = a_wrap.exponent + b_wrap.exponent
    val stage1_mantissa = a_wrap.mantissa * b_wrap.mantissa
    val stage1_zero = a_wrap.zero || b_wrap.zero

    val sign_reg = RegNext(stage1_sign)
    val exponent_reg = RegNext(stage1_exponent)
    val mantissa_reg = RegNext(stage1_mantissa)
    val zero_reg = RegNext(stage1_zero)

    val stage2_sign = sign_reg
    val stage2_exponent = UInt(a_wrap.exponent.getWidth.W)
    val stage2_mantissa = UInt((a_wrap.mantissa.getWidth - 1).W)

    val (mantissaLead, mantissaSize, exponentSize, exponentSub) = n match {
        case 32 => (47, 23, 8, 127)
        case 64 => (105, 52, 11, 1023)
    }

    val rounder = Module(new MantissaRounder(mantissaSize + 1))

    when (zero_reg) {
        stage2_exponent := UInt(0, exponentSize)
        rounder.io.in := UInt(0, mantissaSize + 1)
    } .elsewhen (mantissa_reg(mantissaLead) === Bits(1)) {
        stage2_exponent := exponent_reg - UInt(exponentSub - 1)
        rounder.io.in := mantissa_reg(mantissaLead - 1,
                                      mantissaLead - mantissaSize - 1)
    } .otherwise {
        stage2_exponent := exponent_reg - UInt(exponentSub)
        rounder.io.in := mantissa_reg(mantissaLead - 2,
                                      mantissaLead - mantissaSize - 2)
    }

    stage2_mantissa := rounder.io.out

    io.res := Cat(stage2_sign,
                  stage2_exponent,
                  stage2_mantissa)
}

class FPMult32 extends FPMult(32) {}
class FPMult64 extends FPMult(64) {}

