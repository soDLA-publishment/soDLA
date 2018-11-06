package nvdla

import chisel3._

// this is a synchronous-read, synchronous-write memory

class nv_ram_rws(dep: Int, wid: Int) extends Module{

    val io = IO(new Bundle {
        //clock
        val clk = Input(Clock())


        //control signal
        val re = Input(Bool())
        val we = Input(Bool())

        //data signal
        val ra = Input(UInt(log2Ceil(dep).W))
        val wa = Input(UInt(log2Ceil(dep).W))
        val pwrbus_ram_pd = Input(UInt(32.W))
        val di = Input(UInt(wid.W))
        val dout = Output(UInt(wid.W))
    })

// assign data...

// Create a synchronous-read, synchronous-write memory (like in FPGAs).
val mem = SyncReadMem(dep, UInt(wid.W)))
// Create one write port and one read port.
when (io.we) { 
    mem.write(wa, di) ;
    io.dout := DontCare
}
.otherwise{ 
    io.dout := mem.read(ra, read)
}

}
