package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

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
        val di = Input(UInt(wid.W))
        val dout = Output(UInt(wid.W))
    })
 withClock(io.clk){
     
    val mem = Reg(Vec(dep, UInt(wid.W)))
    val ra_d = Reg(UInt(log2Ceil(dep).W))

    when (io.we) { 
        mem(io.wa) := io.di
    }
    when (io.re) {
        ra_d := io.ra
    }
    io.dout := mem(ra_d)


}}


object nv_ram_rwsDriver extends App {
  chisel3.Driver.execute(args, () => new nv_ram_rws(128, 128))
}

