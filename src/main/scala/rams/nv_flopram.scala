package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

// 
// Flop-Based RAM (with asynchronous wr_reg)
//

class nv_flopram(dep: Int, wid: Int, wr_reg: Boolean = false) extends Module{
  val io = IO(new Bundle{
        val clk = Input(Clock())  
        val clk_mgated = if(wr_reg) Some(Input(Clock())) else None

        val di = Input(UInt(wid.W))
        val iwe = if(wr_reg) Some(Input(Bool())) else None
        val we = Input(Bool())
        val wa = Input(UInt(log2Ceil(dep).W))
        val ra = Input(UInt((log2Ceil(dep+1)).W))
        val dout = Output(UInt(wid.W))

        val pwrbus_ram_pd = Input(UInt(32.W))

  }) 
//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │          
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 

val di_d = if(wr_reg) withClock(io.clk){RegEnable(io.di, io.iwe.get)} // -wr_reg
            else io.di

val internal_clk = Wire(Clock()) 
if(wr_reg){
    internal_clk := io.clk_mgated.get
}  
else{
    internal_clk := io.clk
} 

withClock(internal_clk){
    val ram_ff = Seq.fill(dep)(Reg(UInt(wid.W))) :+ Wire(UInt(wid.W))
    when(io.we){
        for(i <- 0 to dep-1){
            when(io.wa === i.U){
                ram_ff(i) := io.di
            }
        } 
    }   
    ram_ff(dep) := io.di
    io.dout := MuxLookup(io.ra, "b0".asUInt(wid.W), 
        (0 to dep) map { i => i.U -> ram_ff(i)} )
}}
