package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_COUNTER_STAGE_histogram(width: Int) extends Module{

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


class NV_COUNTER_STAGE_lut(width: Int) extends Module{

    val io = IO(new Bundle{
        val clk = Input(Clock())

        val lut_en = Input(Bool())
        val op_en_load = Input(Bool())

        val hit_add = Input(Bool())
        val hit_sub = Input(Bool())

        val hit_cnt = Output(UInt(width.W))

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
    val hit_adv = io.hit_add =/= io.hit_sub

    // cnt logic
    val cnt_cur_reg = RegInit("b0".asUInt(width.W))
    val cnt_ext = Wire(UInt((width+2).W))
    val cnt_mod = Wire(UInt((width+2).W))
    val cnt_new = Wire(UInt((width+2).W))
    val cnt_nxt = Wire(UInt((width+2).W))

    cnt_ext := cnt_cur_reg
    cnt_mod := cnt_cur_reg +& io.hit_add -& io.hit_sub
    cnt_new := Mux(hit_adv, cnt_mod, cnt_ext)
    cnt_nxt := Mux(io.op_en_load, "b0".asUInt((width+2).W), cnt_new)

    // stage flops
    when(io.lut_en){
        cnt_cur_reg := cnt_nxt
    }

    io.hit_cnt := cnt_cur_reg

}}

