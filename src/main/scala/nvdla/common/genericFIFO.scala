// package nvdla

// import chisel3._


// class Fifo[T <: Data](gen: T, n: Int) extends Module {
//   val io = IO(new Bundle {
//     val enqVal = Input(Bool())
//     val enqRdy = Output(Bool())
//     val deqVal = Output(Bool())
//     val deqRdy = Input(Bool())
//     val enqDat = Input(gen)
//     val deqDat = Output(gen)
//   })
//   val enqPtr     = Reg(init = 0.asUInt(sizeof(n).W))
//   val deqPtr     = Reg(init = 0.asUInt(sizeof(n).W))
//   val isFull     = Reg(init = false.B)
//   val doEnq      = io.enqRdy && io.enqVal
//   val doDeq      = io.deqRdy && io.deqVal
//   val isEmpty    = !isFull && (enqPtr === deqPtr)
//   val deqPtrInc  = deqPtr + 1.U
//   val enqPtrInc  = enqPtr + 1.U
//   val isFullNext = Mux(doEnq && ~doDeq && (enqPtrInc === deqPtr),
//                          true.B, Mux(doDeq && isFull, false.B,
//                          isFull))
//   enqPtr := Mux(doEnq, enqPtrInc, enqPtr)
//   deqPtr := Mux(doDeq, deqPtrInc, deqPtr)
//   isFull := isFullNext
//   val ram = Mem(n)
//   when (doEnq) {
//     ram(enqPtr) := io.enqDat
//   }
//   io.enqRdy := !isFull
//   io.deqVal := !isEmpty
//   ram(deqPtr) <> io.deqDat
// }