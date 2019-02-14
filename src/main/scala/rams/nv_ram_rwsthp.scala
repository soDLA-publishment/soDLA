package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

// this is a two clock read, synchronous-write memory, with bypass

class nv_ram_rwsthp(dep: Int, wid: Int) extends Module{

    val io = IO(new Bundle {
        //control signal
        val re = Input(Bool())
        val we = Input(Bool())
        val byp_sel = Input(Bool())
        val ore = Input(Bool())

        //data signal
        val dbyp = Input(UInt(wid.W))
        val ra = Input(UInt(log2Ceil(dep).W))
        val wa = Input(UInt(log2Ceil(dep).W))
        val pwrbus_ram_pd = Input(UInt(32.W))
        val di = Input(UInt(wid.W))
        val dout = Output(UInt(wid.W))
    })

// assign data...

// Create a synchronous-read, synchronous-write memory (like in FPGAs).
val mem = SyncReadMem(dep, UInt(wid.W))
// Create one write port and one read port.
when (io.we) { 
    mem.write(io.wa, io.di) 
    io.dout := DontCare
}
.otherwise{ 
    val dout_ram = mem.read(io.ra, io.re)
    val fbypass_dout_ram = Mux(io.byp_sel, io.dbyp, dout_ram)
    when (io.ore){
        io.dout := RegNext(fbypass_dout_ram)
    }
    .otherwise{
        io.dout := DontCare       
    }

}
}
