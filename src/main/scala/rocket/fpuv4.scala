// // See LICENSE.Berkeley for license details.
// // See LICENSE.SiFive for license details.

// package cora

// import chisel3._
// import Chisel.ImplicitConversions._
// import chisel3.internal.sourceinfo.SourceInfo
// import chisel3.experimental._
// import chisel3.util._
// import hardfloat._

// class MulAddRecFNPipeV4(latency: Int = 2, expWidth: Int = 9, sigWidth: Int = 23) extends Module
// {
//     require(latency<=2) 

//     val io = new Bundle {
//         val validin = Input(Bool())
//         val op = Input(UInt(2.W))
//         val a = Input(UInt((expWidth + sigWidth + 1).W))
//         val b = Input(UInt((expWidth + sigWidth + 1).W))
//         val c = Input(UInt((expWidth + sigWidth + 1).W))
//         val roundingMode   = Input(UInt(3.W))
//         val detectTininess = Input(UInt(1.W))
//         val out = Output(UInt((expWidth + sigWidth + 1).W))
//         val exceptionFlags = Output(UInt(5.W))
//         val validout = Output(Bool())
//     }

//     //------------------------------------------------------------------------
//     //------------------------------------------------------------------------
//     val mulAddRecFNToRaw_preMul =
//         Array.fill(4){Module(new MulAddRecFNToRaw_preMul(expWidth, sigWidth))}
//     val mulAddRecFNToRaw_postMul =
//         Module(new MulAddRecFNToRaw_postMul(expWidth, sigWidth))

//     for (i <- 0 to 3){
//         mulAddRecFNToRaw_preMul(i).io.op := io.op
//         mulAddRecFNToRaw_preMul(i).io.a  := io.a
//         mulAddRecFNToRaw_preMul(i).io.b  := io.b
//         mulAddRecFNToRaw_preMul(i).io.c  := 0.U
//     }

//     val mulAddResult =
//         (mulAddRecFNToRaw_preMul(0).io.mulAddA * mulAddRecFNToRaw_preMul(0).io.mulAddB) +&
//         (mulAddRecFNToRaw_preMul(1).io.mulAddA * mulAddRecFNToRaw_preMul(1).io.mulAddB) +&
//         (mulAddRecFNToRaw_preMul(2).io.mulAddA * mulAddRecFNToRaw_preMul(2).io.mulAddB) +&
//         (mulAddRecFNToRaw_preMul(3).io.mulAddA * mulAddRecFNToRaw_preMul(3).io.mulAddB)

//     val valid_stage0 = Wire(Bool())
//     val roundingMode_stage0 = Wire(UInt(3.W))
//     val detectTininess_stage0 = Wire(UInt(1.W))

//     val toPostMul = mulAddRecFNToRaw_preMul(0).io.toPostMul
  
//     val postmul_regs = if(latency>0) 1 else 0
//     mulAddRecFNToRaw_postMul.io.fromPreMul   := Pipe(io.validin, toPostMul, postmul_regs).bits
//     mulAddRecFNToRaw_postMul.io.mulAddResult := Pipe(io.validin, mulAddResult, postmul_regs).bits
//     mulAddRecFNToRaw_postMul.io.roundingMode := Pipe(io.validin, io.roundingMode, postmul_regs).bits
//     roundingMode_stage0                      := Pipe(io.validin, io.roundingMode, postmul_regs).bits
//     detectTininess_stage0                    := Pipe(io.validin, io.detectTininess, postmul_regs).bits
//     valid_stage0                             := Pipe(io.validin, false.B, postmul_regs).valid
    
//     //------------------------------------------------------------------------
//     //------------------------------------------------------------------------
//     val roundRawFNToRecFN = Module(new RoundRawFNToRecFN(expWidth, sigWidth, 0))

//     val round_regs = if(latency==2) 1 else 0
//     roundRawFNToRecFN.io.invalidExc         := Pipe(valid_stage0, mulAddRecFNToRaw_postMul.io.invalidExc, round_regs).bits
//     roundRawFNToRecFN.io.in                 := Pipe(valid_stage0, mulAddRecFNToRaw_postMul.io.rawOut, round_regs).bits
//     roundRawFNToRecFN.io.roundingMode       := Pipe(valid_stage0, roundingMode_stage0, round_regs).bits
//     roundRawFNToRecFN.io.detectTininess     := Pipe(valid_stage0, detectTininess_stage0, round_regs).bits
//     io.validout                             := Pipe(valid_stage0, false.B, round_regs).valid

//     roundRawFNToRecFN.io.infiniteExc := false.B

//     io.out            := roundRawFNToRecFN.io.out
//     io.exceptionFlags := roundRawFNToRecFN.io.exceptionFlags
// }