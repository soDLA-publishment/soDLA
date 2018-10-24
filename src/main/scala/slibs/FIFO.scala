package nvdla



class DataBundle extends Bundle {
  val a = UInt(32.W)
  val b = UInt(32.W)
}

class Fifo[T <: Data](gen: T, n: Int) extends Module {
  val io = IO(new Bundle {
    val enqVal = Input(Bool())
    val enqRdy = Output(Bool())
    val deqVal = Output(Bool())
    val deqRdy = Input(Bool())
    val enqDat = Input(gen)
    val deqDat = Output(gen)
  })
  val enqPtr     = Reg(init = 0.asUInt(sizeof(n).W))
  val deqPtr     = Reg(init = 0.asUInt(sizeof(n).W))
  val isFull     = Reg(init = false.B)
  val doEnq      = io.enqRdy && io.enqVal
  val doDeq      = io.deqRdy && io.deqVal
  val isEmpty    = !isFull && (enqPtr === deqPtr)
  val deqPtrInc  = deqPtr + 1.U
  val enqPtrInc  = enqPtr + 1.U
  val isFullNext = Mux(doEnq && ~doDeq && (enqPtrInc === deqPtr),
                         true.B, Mux(doDeq && isFull, false.B,
                         isFull))
  enqPtr := Mux(doEnq, enqPtrInc, enqPtr)
  deqPtr := Mux(doDeq, deqPtrInc, deqPtr)
  isFull := isFullNext
  val ram = Mem(n)
  when (doEnq) {
    ram(enqPtr) := io.enqDat
  }
  io.enqRdy := !isFull
  io.deqVal := !isEmpty
  ram(deqPtr) <> io.deqDat
}



//An Fifo with 8 elements of type DataBundle could then be instantiated as:

//val fifo = Module(new Fifo(new DataBundle, 8))
//It is also possible to define a generic decoupled (ready/valid) interface:

//class DecoupledIO[T <: Data](data: T) extends Bundle {
//  val ready = Input(Bool())
//  val valid = Output(Bool())
// val bits  = Output(data)
//}
//This template can then be used to add a handshaking protocol to any set of signals:

//class DecoupledDemo extends DecoupledIO(new DataBundle)
//The FIFO interface can be now be simplified as follows:

//class Fifo[T <: Data](data: T, n: Int) extends Module {
//  val io = IO(new Bundle {
//    val enq = Flipped(new DecoupledIO(data))
//    val deq = new DecoupledIO(data)
//  })
//  ...
//}
