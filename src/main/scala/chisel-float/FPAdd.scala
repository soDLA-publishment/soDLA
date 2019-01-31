package ChiselFloat

import chisel3._
import chisel3.experimental._
import chisel3.util._
import FloatUtils.{floatToBigInt, doubleToBigInt, getExpMantWidths,
                   floatAdd, doubleAdd}
import scala.collection.mutable.Queue

class SatLeftShift(val m: Int, val n: Int) extends Module {
    val io = new Bundle {
        val shiftin = Input(UInt(m.W))
        val shiftby = Input(UInt(n.W))
        val shiftout = Output(UInt(m.W))
    }

    io.shiftout := Mux(io.shiftby > m.U, 0.U, io.shiftin >> io.shiftby)
}

class FPAddStage1(val n: Int) extends Module {
    val (expWidth, mantWidth) = getExpMantWidths(n)

    val io = new Bundle {
        val a = Input(UInt(n.W))
        val b = Input(UInt(n.W))

        val b_larger = Output(Bool())
        val mant_shift = Output(UInt((expWidth).W))
        val exp = Output(UInt((expWidth).W))
        val manta = Output(UInt((mantWidth + 1).W))
        val mantb = Output(UInt((mantWidth + 1).W))
        val sign = Output(Bool())
        val sub = Output(Bool())
    }

    val a_wrap = new FloatWrapper(io.a)
    val b_wrap = new FloatWrapper(io.b)

    // we need to add a bit to the beginning before subtracting
    // so that we can catch if it becomes negative
    val ext_exp_a = Cat("b01".asUInt(2.W), a_wrap.exponent)
    val ext_exp_b = Cat("b01".asUInt(2.W), b_wrap.exponent)
    val exp_diff = ext_exp_a - ext_exp_b

    val reg_b_larger = Reg(Bool())
    val reg_mant_shift = Reg(UInt(expWidth.W))
    val reg_exp = Reg(UInt(expWidth.W))
    val reg_manta = RegNext(a_wrap.mantissa)
    val reg_mantb = RegNext(b_wrap.mantissa)
    val reg_sign = Reg(Bool())
    val reg_sub = RegNext(a_wrap.sign ^ b_wrap.sign)

    // In stage 1, we subtract the exponents
    // This will tell us which number is larger
    // as well as what we need to shift the smaller mantissa by

    // b is larger
    when (exp_diff(expWidth) === true.B) {
        // absolute value
        reg_mant_shift := -exp_diff(expWidth - 1, 0)
        //mant_shift := (~exp_diff) + UInt(1)
        reg_b_larger := true.B
        reg_exp := b_wrap.exponent
        reg_sign := b_wrap.sign
    } .otherwise {
        reg_mant_shift := exp_diff(expWidth - 1, 0)
        reg_b_larger := false.B
        reg_exp := a_wrap.exponent
        reg_sign := a_wrap.sign
    }

    io.mant_shift := reg_mant_shift
    io.b_larger := reg_b_larger
    io.exp := reg_exp
    io.manta := reg_manta
    io.mantb := reg_mantb
    io.sign := reg_sign
    io.sub := reg_sub
}

class FPAddStage2(val n: Int) extends Module {
    val (expWidth, mantWidth) = getExpMantWidths(n)

    val io = new Bundle {
        val manta_in = Input(UInt((mantWidth + 1).W))
        val mantb_in = Input(UInt((mantWidth + 1).W))
        val exp_in = Input(UInt((expWidth).W))
        val mant_shift = Input(UInt((expWidth).W))
        val b_larger = Input(Bool())
        val sign_in = Input(Bool())
        val sub_in = Input(Bool())

        val manta_out = Output(UInt((mantWidth + 1).W))
        val mantb_out = Output(UInt((mantWidth + 1).W))
        val exp_out = Output(UInt((expWidth).W))
        val sign_out = Output(Bool())
        val sub_out = Output(Bool())
    }

    // in stage 2 we shift the appropriate mantissa by the amount
    // detected in stage 1

    val larger_mant = UInt((mantWidth + 1).W)
    val smaller_mant = UInt((mantWidth + 1).W)

    when (io.b_larger) {
        larger_mant := io.mantb_in
        smaller_mant := io.manta_in
    } .otherwise {
        larger_mant := io.manta_in
        smaller_mant := io.mantb_in
    }

    val shifted_mant = Mux(io.mant_shift > (mantWidth + 1).U,
                           UInt(0), smaller_mant >> io.mant_shift)
    val reg_manta = Reg(next = larger_mant)
    val reg_mantb = Reg(next = shifted_mant)
    val reg_sign = Reg(next = io.sign_in)
    val reg_sub = Reg(next = io.sub_in)
    val reg_exp = Reg(next = io.exp_in)

    io.manta_out := reg_manta
    io.mantb_out := reg_mantb
    io.sign_out := reg_sign
    io.sub_out := reg_sub
    io.exp_out := reg_exp
}

class FPAddStage3(val n: Int) extends Module {
    val (expWidth, mantWidth) = getExpMantWidths(n)

    val io = new Bundle {
        val manta = Input(UInt((mantWidth + 1).W))
        val mantb = Input(UInt((mantWidth + 1).W))
        val exp_in = Input(UInt((expWidth).W))
        val sign_in = Input(Bool())
        val sub = Input(Bool())

        val mant_out = Output(UInt((mantWidth + 1).W))
        val sign_out = Output(Bool())
        val exp_out = Output(UInt((expWidth).W))
    }

    // in stage 3 we subtract or add the mantissas
    // we must also detect overflows and adjust sign/exponent appropriately

    val manta_ext = Cat(UInt(0, 1), io.manta)
    val mantb_ext = Cat(UInt(0, 1), io.mantb)
    val mant_sum = Mux(io.sub, manta_ext - mantb_ext, manta_ext + mantb_ext)

    // here we drop the overflow bit
    val reg_mant = Reg(UInt((mantWidth + 1).W))
    val reg_sign = Reg(Bool())
    val reg_exp = Reg(UInt(expWidth.W))

    // this may happen if the operands were of opposite sign
    // but had the same exponent
    when (mant_sum(mantWidth + 1) === UInt(1)) {
        when (io.sub) {
            reg_mant := -mant_sum(mantWidth, 0)
            reg_sign := !io.sign_in
            reg_exp := io.exp_in
        } .otherwise {
            // if the sum overflowed, we need to shift back by one
            // and increment the exponent
            reg_mant := mant_sum(mantWidth + 1, 1)
            reg_exp := io.exp_in + UInt(1)
            reg_sign := io.sign_in
        }
    } .otherwise {
        reg_mant := mant_sum(mantWidth, 0)
        reg_sign := io.sign_in
        reg_exp := io.exp_in
    }

    io.sign_out := reg_sign
    io.exp_out := reg_exp
    io.mant_out := reg_mant
}

class FPAddStage4(val n: Int) extends Module {
    val (expWidth, mantWidth) = getExpMantWidths(n)

    val io = new Bundle {
        val exp_in = Input(UInt(expWidth.W))
        val mant_in = Input(UInt((mantWidth + 1).W))

        val exp_out = Output(UInt(expWidth.W))
        val mant_out = Output(UInt(mantWidth.W))
    }

    // finally in stage 4 we normalize mantissa and exponent
    // we need to reverse the sum, since we want the find the most
    // significant 1 instead of the least significant 1
    val norm_shift = PriorityEncoder(Reverse(io.mant_in))

    // if the mantissa sum is zero, result mantissa and exponent should be zero
    when (io.mant_in === UInt(0)) {
        io.mant_out := UInt(0)
        io.exp_out := UInt(0)
    } .otherwise {
        io.mant_out := (io.mant_in << norm_shift)(mantWidth - 1, 0)
        io.exp_out := io.exp_in - norm_shift
    }
}

class FPAdd(val n: Int) extends Module {
    val io = new Bundle {
        val a = Input(UInt(n.W))
        val b = Input(UInt(n.W))
        val res = Output(UInt(n.W))
    }

    val (expWidth, mantWidth) = getExpMantWidths(n)

    val stage1 = Module(new FPAddStage1(n))

    stage1.io.a := io.a
    stage1.io.b := io.b

    val stage2 = Module(new FPAddStage2(n))

    stage2.io.manta_in := stage1.io.manta
    stage2.io.mantb_in := stage1.io.mantb
    stage2.io.exp_in := stage1.io.exp
    stage2.io.sign_in := stage1.io.sign
    stage2.io.sub_in := stage1.io.sub
    stage2.io.b_larger := stage1.io.b_larger
    stage2.io.mant_shift := stage1.io.mant_shift

    val stage3 = Module(new FPAddStage3(n))

    stage3.io.manta := stage2.io.manta_out
    stage3.io.mantb := stage2.io.mantb_out
    stage3.io.exp_in := stage2.io.exp_out
    stage3.io.sign_in := stage2.io.sign_out
    stage3.io.sub := stage2.io.sub_out

    val stage4 = Module(new FPAddStage4(n))

    stage4.io.exp_in := stage3.io.exp_out
    stage4.io.mant_in := stage3.io.mant_out

    io.res := Cat(stage3.io.sign_out, stage4.io.exp_out, stage4.io.mant_out)
}

class FPAdd32 extends FPAdd(32) {}
class FPAdd64 extends FPAdd(64) {}

