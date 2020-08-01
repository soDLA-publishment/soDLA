package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

// this is a two clock read, synchronous-write memory, with bypass

@chiselName
class nv_ram_rwsthp(dep: Int, wid: Int, asic: Boolean = false) extends Module{

    val io = IO(new Bundle {
        //clock
        val clk = Input(Clock())

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

 withClock(io.clk){
     if(!asic){
        // Create a synchronous-read, synchronous-write memory (like in FPGAs).
        val mem = Reg(Vec(dep, UInt(wid.W)))
        val ra_d = Reg(UInt(log2Ceil(dep).W))
        // Create one write port and one read port.
        when (io.we) { 
            mem(io.wa) := io.di
        }
        when(io.re){
            ra_d := io.ra
        }

        val dout_ram = mem(ra_d)
        val fbypass_dout_ram = Mux(io.byp_sel, io.dbyp, dout_ram)
        val dout_r = Reg(UInt(wid.W))
        when (io.ore){
            dout_r := fbypass_dout_ram
        }

        io.dout := dout_r
    }
    else{
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
}}


object nv_ram_rwsthpDriver extends App {
  chisel3.Driver.execute(args, () => new nv_ram_rwsthp(19, 32))
}
