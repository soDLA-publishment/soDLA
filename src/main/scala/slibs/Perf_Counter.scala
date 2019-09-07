// package nvdla

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

        val st_cnt_cur = Output(UInt(width.W))

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

    // stage adv logic
    val st_adv = io.rd_stall_inc ^ io.rd_stall_dec

    // stage cnt logic
    val st_cnt_cur_reg = RegInit("b0".asUInt(width.W))
    val st_cnt_ext = Wire(UInt((width+2).W))
    val st_cnt_inc = Wire(UInt((width+2).W))
    val st_cnt_dec = Wire(UInt((width+2).W))
    val st_cnt_mod = Wire(UInt((width+2).W))
    val st_cnt_new = Wire(UInt((width+2).W))
    val st_cnt_nxt = Wire(UInt((width+2).W))

    st_cnt_ext := st_cnt_cur_reg
    st_cnt_inc := st_cnt_cur_reg +& 1.U
    st_cnt_dec := st_cnt_cur_reg -& 1.U
    st_cnt_mod := Mux(io.rd_stall_inc && !io.rd_stall_dec, st_cnt_inc, 
                  Mux(!io.rd_stall_inc && io.rd_stall_dec, st_cnt_dec,
                  st_cnt_ext))
    st_cnt_new := Mux(st_adv, st_cnt_mod, st_cnt_ext)
    st_cnt_nxt := Mux(io.rd_stall_clr, 0.U, st_cnt_new)

    // stage flops
    when(io.rd_stall_cen){
        st_cnt_cur_reg := st_cnt_nxt
    }

    io.st_cnt_cur := st_cnt_cur_reg

}}

