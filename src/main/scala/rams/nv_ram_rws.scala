package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

// this is a synchronous-read, synchronous-write memory

class nv_ram_rws(dep: Int, wid: Int) extends Module{

    val io = IO(new Bundle {
        //control signal
        val re = Input(Bool())
        val we = Input(Bool())

        //data signal
        val ra = Input(UInt(log2Ceil(dep).W))
        val wa = Input(UInt(log2Ceil(dep).W))
        val di = Input(UInt(wid.W))
        val dout = Output(UInt(wid.W))
    })
    
    // Create a synchronous-read, synchronous-write memory (like in FPGAs).
    val mem = SyncReadMem(dep, UInt(wid.W))
    // Create one write port and one read port.
    when (io.we) { 
        mem.write(io.wa, io.di) 
        io.dout := DontCare
    }
    .otherwise{ 
        io.dout := mem.read(io.ra, io.re)
    }

}
