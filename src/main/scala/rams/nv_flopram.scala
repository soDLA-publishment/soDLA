package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

// 
// Flop-Based RAM 
//

class nv_flopram(dep: Int, wid: Int) extends Module{
  val io = IO(new Bundle{
        val clk = Input(Clock())    // write clock

        val di = Input(UInt(wid.W))
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
withClock(io.clk){
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

// 
// Flop-Based RAM (with internal wr_reg)
//

class nv_flopram_internal_wr_reg(dep: Int, wid: Int) extends Module {
    val io = IO(new Bundle {
        //clk
        val clk = Input(Clock())
        val clk_mgated = Input(Clock())
        
        val di = Input(UInt(wid.W))
        val iwe = Input(Bool())
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
withClock(io.clk){
    val di_d = Reg(UInt(wid.W))
    when(io.iwe){
        di_d := io.di // -wr_reg
    }
    withClock(io.clk_mgated){
        val ram_ff = Seq.fill(dep)(Reg(UInt(wid.W))) :+ Wire(UInt(wid.W))
        when(io.we){
            for(i <- 0 to dep-1){
                when(io.wa === i.U){
                    ram_ff(i) := di_d
                }
            } 
        }   
        ram_ff(dep) := di_d
        io.dout := MuxLookup(io.ra, "b0".asUInt(wid.W), 
            (0 to dep) map { i => i.U -> ram_ff(i)} )
    }

}}
