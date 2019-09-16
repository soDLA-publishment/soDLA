package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_CDMA_WT_sp_arb extends Module {
    val io = IO(new Bundle {
        val req0 = Input(Bool())
        val req1 = Input(Bool())
        val gnt_busy = Input(Bool())
        val gnt0 = Output(Bool())
        val gnt1 = Output(Bool())
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
    val req = Cat(io.req1, io.req0)
    val gnt_pre = Wire(Vec(2, Bool()))
    when(req(0)){
        gnt_pre(0) := true.B
        gnt_pre(1) := false.B
    }
    .elsewhen(req(1)){
        gnt_pre(0) := false.B
        gnt_pre(1) := true.B
    }
    .otherwise{
        gnt_pre(0) := false.B
        gnt_pre(1) := false.B
    }


    io.gnt0 := gnt_pre(0)&(!io.gnt_busy)
    io.gnt1 := gnt_pre(1)&(!io.gnt_busy)
}





    
object NV_NVDLA_CDMA_WT_sp_arbDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_WT_sp_arb())
}