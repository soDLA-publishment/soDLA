package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_COUNTER_STAGE(width: Int) extends Module{

    val io = IO(new Bundle{
        val clk = Input(Clock())

        val rd_stall_inc = Input(Bool())
        val rd_stall_dec = Input(Bool())
        val rd_stall_clr = Input(Bool())
        val rd_stall_cen = Input(Bool())

        val cnt_cur = Output(UInt(width.W))

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

    // adv logic
    val adv = io.rd_stall_inc ^ io.rd_stall_dec

    // cnt logic
    val cnt_cur_reg = RegInit("b0".asUInt(width.W))
    val cnt_ext = Wire(UInt((width+2).W))
    val cnt_inc = Wire(UInt((width+2).W))
    val cnt_dec = Wire(UInt((width+2).W))
    val cnt_mod = Wire(UInt((width+2).W))
    val cnt_new = Wire(UInt((width+2).W))
    val cnt_nxt = Wire(UInt((width+2).W))

    cnt_ext := cnt_cur_reg
    cnt_inc := cnt_cur_reg +& 1.U
    cnt_dec := cnt_cur_reg -& 1.U
    cnt_mod := Mux(io.rd_stall_inc && !io.rd_stall_dec, cnt_inc, 
                  Mux(!io.rd_stall_inc && io.rd_stall_dec, cnt_dec,
                  cnt_ext))
    cnt_new := Mux(adv, cnt_mod, cnt_ext)
    cnt_nxt := Mux(io.rd_stall_clr, 0.U, cnt_new)

    // stage flops
    when(io.rd_stall_cen){
        cnt_cur_reg := cnt_nxt
    }

    io.cnt_cur := cnt_cur_reg

}}

