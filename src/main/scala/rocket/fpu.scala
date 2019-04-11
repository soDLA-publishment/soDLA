// See LICENSE.Berkeley for license details.
// See LICENSE.SiFive for license details.

package cora


import Chisel._
import Chisel.ImplicitConversions._
import chisel3.internal.sourceinfo.SourceInfo
import chisel3.experimental._

object FPConstants
{
  val RM_SZ = 3
  val FLAGS_SZ = 5
}
import FPConstants._

class MulAddRecFNPipe(expWidth: Int = 8, sigWidth: Int = 24) extends Module
{
    val latency = 2
    require(latency<=2) 

    val io = new Bundle {
        val validin = Bool(INPUT)
        val op = Bits(INPUT, 2)
        val a = Bits(INPUT, expWidth + sigWidth + 1)
        val b = Bits(INPUT, expWidth + sigWidth + 1)
        val c = Bits(INPUT, expWidth + sigWidth + 1)
        val roundingMode   = UInt(INPUT, 3)
        val detectTininess = UInt(INPUT, 1)
        val out = Bits(OUTPUT, expWidth + sigWidth + 1)
        val exceptionFlags = Bits(OUTPUT, 5)
        val validout = Bool(OUTPUT)
    }

    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    val mulAddRecFNToRaw_preMul =
        Module(new hardfloat.MulAddRecFNToRaw_preMul(expWidth, sigWidth))
    val mulAddRecFNToRaw_postMul =
        Module(new hardfloat.MulAddRecFNToRaw_postMul(expWidth, sigWidth))

    mulAddRecFNToRaw_preMul.io.op := io.op
    mulAddRecFNToRaw_preMul.io.a  := io.a
    mulAddRecFNToRaw_preMul.io.b  := io.b
    mulAddRecFNToRaw_preMul.io.c  := io.c

    val mulAddResult =
        (mulAddRecFNToRaw_preMul.io.mulAddA *
             mulAddRecFNToRaw_preMul.io.mulAddB) +&
            mulAddRecFNToRaw_preMul.io.mulAddC

    val valid_stage0 = Wire(Bool())
    val roundingMode_stage0 = Wire(UInt(width=3))
    val detectTininess_stage0 = Wire(UInt(width=1))
  
    val postmul_regs = if(latency>0) 1 else 0
    mulAddRecFNToRaw_postMul.io.fromPreMul   := Pipe(io.validin, mulAddRecFNToRaw_preMul.io.toPostMul, postmul_regs).bits
    mulAddRecFNToRaw_postMul.io.mulAddResult := Pipe(io.validin, mulAddResult, postmul_regs).bits
    mulAddRecFNToRaw_postMul.io.roundingMode := Pipe(io.validin, io.roundingMode, postmul_regs).bits
    roundingMode_stage0                      := Pipe(io.validin, io.roundingMode, postmul_regs).bits
    detectTininess_stage0                    := Pipe(io.validin, io.detectTininess, postmul_regs).bits
    valid_stage0                             := Pipe(io.validin, false.B, postmul_regs).valid
    
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    val roundRawFNToRecFN = Module(new hardfloat.RoundRawFNToRecFN(expWidth, sigWidth, 0))

    val round_regs = if(latency==2) 1 else 0
    roundRawFNToRecFN.io.invalidExc         := Pipe(valid_stage0, mulAddRecFNToRaw_postMul.io.invalidExc, round_regs).bits
    roundRawFNToRecFN.io.in                 := Pipe(valid_stage0, mulAddRecFNToRaw_postMul.io.rawOut, round_regs).bits
    roundRawFNToRecFN.io.roundingMode       := Pipe(valid_stage0, roundingMode_stage0, round_regs).bits
    roundRawFNToRecFN.io.detectTininess     := Pipe(valid_stage0, detectTininess_stage0, round_regs).bits
    io.validout                             := Pipe(valid_stage0, false.B, round_regs).valid

    roundRawFNToRecFN.io.infiniteExc := Bool(false)

    io.out            := roundRawFNToRecFN.io.out
    io.exceptionFlags := roundRawFNToRecFN.io.exceptionFlags
}


// class IntToFP(val latency: Int)(implicit p: Parameters) extends FPUModule()(p) with ShouldBeRetimed {
//   val io = new Bundle {
//     val in = Valid(new IntToFPInput).flip
//     val out = Valid(new FPResult)
//   }

//   val in = Pipe(io.in)
//   val tag = !in.bits.singleIn // TODO typeTag

//   val mux = Wire(new FPResult)
//   mux.exc := Bits(0)
//   mux.data := recode(in.bits.in1, !in.bits.singleIn)

//   val intValue = {
//     val res = Wire(init = in.bits.in1.asSInt)
//     for (i <- 0 until nIntTypes-1) {
//       val smallInt = in.bits.in1((minXLen << i) - 1, 0)
//       when (in.bits.typ.extract(log2Ceil(nIntTypes), 1) === i) {
//         res := Mux(in.bits.typ(0), smallInt.zext, smallInt.asSInt)
//       }
//     }
//     res.asUInt
//   }

//   when (in.bits.wflags) { // fcvt
//     // could be improved for RVD/RVQ with a single variable-position rounding
//     // unit, rather than N fixed-position ones
//     val i2fResults = for (t <- floatTypes) yield {
//       val i2f = Module(new hardfloat.INToRecFN(xLen, t.exp, t.sig))
//       i2f.io.signedIn := ~in.bits.typ(0)
//       i2f.io.in := intValue
//       i2f.io.roundingMode := in.bits.rm
//       i2f.io.detectTininess := hardfloat.consts.tininess_afterRounding
//       (sanitizeNaN(i2f.io.out, t), i2f.io.exceptionFlags)
//     }

//     val (data, exc) = i2fResults.unzip
//     val dataPadded = data.init.map(d => Cat(data.last >> d.getWidth, d)) :+ data.last
//     mux.data := dataPadded(tag)
//     mux.exc := exc(tag)
//   }

//     io.out <> Pipe(in.valid, mux, latency-1)
// }




