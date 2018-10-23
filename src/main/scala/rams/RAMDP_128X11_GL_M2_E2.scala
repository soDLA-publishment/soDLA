package nvdla

import chisel3._


class RAMDP_128X11_GL_M2_E2 extends Module {
  val io = IO(new Bundle {
    val CLK_R = Input(Clock())
    val RE = Input(Bool())
    val CLK_W = Input(Clock())
    val WE = Input(Bool())
    val WD = Vec(288, Bool())
    val WADR = Vec()
  })


  withClock (!io.CP) {
    // In this withClock scope, all synchronous elements are clocked against io.clockB.

    // This register is clocked against io.clockB, but uses implict reset from the parent context.
    val qd = RegNext(io.TE || io.E)
  }

  io.Q := qd & io.CP
}


val width:Int = 32
val addr = Wire(UInt(width.W))
val dataIn = Wire(UInt(width.W))
val dataOut = Wire(UInt(width.W))
val enable = Wire(Bool())

// assign data...

// Create a synchronous-read, synchronous-write memory (like in FPGAs).
val mem = SyncReadMem(1024, UInt(width.W))
// Create one write port and one read port.
mem.write(addr, dataIn)
dataOut := mem.read(addr, enable)