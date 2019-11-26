package nvdla

import chisel3._
import chisel3.util._

// this is a two clock read, synchronous-write memory

class nv_ram_rwsp(dep: Int, wid: Int) extends Module{

    val io = IO(new Bundle {
        //clock
        val clk = Input(Clock())

        //control signal
        val re = Input(Bool())
        val we = Input(Bool())
        val ore = Input(Bool())

        //data signal
        val ra = Input(UInt(log2Ceil(dep).W))
        val wa = Input(UInt(log2Ceil(dep).W))
        val pwrbus_ram_pd = Input(UInt(32.W))
        val di = Input(UInt(wid.W))
        val dout = Output(UInt(wid.W))
    })
 withClock(io.clk){

    // assign data...
    // Create a synchronous-read, synchronous-write memory (like in FPGAs).
    val mem = Reg(Vec(dep, UInt(wid.W)))
    val ra_d = Reg(UInt(log2Ceil(dep).W))
    val dout_r = Reg(UInt(wid.W))
    // Create one write port and one read port.
    when (io.we) { 
        mem(io.wa) := io.di
    }
    when (io.re) {
        ra_d := io.ra
    }
    val dout_ram = mem(ra_d)
    when (io.ore){
        dout_r := dout_ram
    }
    io.dout := dout_r

}}