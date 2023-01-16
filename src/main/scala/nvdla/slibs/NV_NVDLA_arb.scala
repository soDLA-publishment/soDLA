package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_arb(n:Int, wt_width:Int, io_gnt_busy:Boolean) extends Module {
    val io = IO(new Bundle{
        val clk = Input(Clock())

        val req = Input(Vec(n, Bool()))
        val wt = Input(Vec(n, UInt(wt_width.W)))
        val gnt_busy = if(io_gnt_busy) Some(Input(Bool())) else None
        val gnt = Output(Vec(n, Bool()))

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
withClock(io.clk) {

    val req = VecInit((0 to n-1)  map { i => io.req(i) & (io.wt(i).orR)}).asUInt

    val new_wt_left = Wire(Vec(n, UInt(wt_width.W)))
    for(i <- 0 to n-1){
        new_wt_left(i) := io.wt(i) - 1.U
    }

    //wrr_gnt wt_left
    val wrr_gnt = RegInit("b0".asUInt(n.W)) 
    val wt_left = RegInit("b0".asUInt(wt_width.W))
    val wt_left_nxt = WireInit("b0".asUInt(wt_width.W))

    if(io_gnt_busy){
        when(~io.gnt_busy.get & req =/= 0.U){
            wrr_gnt := io.gnt.asUInt
            wt_left := wt_left_nxt
        }
    }
    else{
        when(req =/= 0.U){
            wrr_gnt := io.gnt.asUInt
            wt_left := wt_left_nxt
        }
    }

    //gnt_pre
    val gnt_pre = WireInit("b0".asUInt(n.W))
    when(wt_left === 0.U | ~((req & wrr_gnt).orR)) {
        for(i <- 0 to n){
            when(wrr_gnt === ("b1".asUInt((n+1).W) << i.U)(n, 1)){
                gnt_pre := MuxCase(0.U(n.W), (i until i+n) map{ j => req(j % n) -> (1.U << (j % n))})
                wt_left_nxt := MuxCase(0.U(wt_width.W), (i until i+n) map{ j => req(j % n) -> new_wt_left(j % n)} )
            }
        }
    }
    .otherwise {
        gnt_pre := wrr_gnt
        wt_left_nxt := wt_left - 1.U
    }

    //generate io.gnt
    if(io_gnt_busy){
        for(i <- 0 to n-1){
            io.gnt(i) := ~io.gnt_busy.get & gnt_pre(i)
        }  
    }
    else{
        for(i <- 0 to n-1){
            io.gnt(i) := gnt_pre(i)
        }  
    }
}}


object read_ig_arb_1Driver extends App {
    chisel3.Driver.execute(args, () => new NV_NVDLA_arb(10, 8, true))
}


object read_eg_arb_1Driver extends App {
    chisel3.Driver.execute(args, () => new NV_NVDLA_arb(10, 8, false))
}