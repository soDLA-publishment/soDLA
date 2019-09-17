package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_HLS_sync2data(DATA1_WIDTH:Int, DATA2_WIDTH:Int) extends Module {
   val io = IO(new Bundle {
        val chn1_en = Input(Bool())
        val chn1_in_pvld = Input(Bool())
        val chn1_in_prdy = Output(Bool())
        val data1_in = Input(UInt(DATA1_WIDTH.W))
        
        val chn2_en = Input(Bool())
        val chn2_in_pvld = Input(Bool())
        val chn2_in_prdy = Output(Bool())
        val data2_in = Input(UInt(DATA2_WIDTH.W))
        
        val chn_out_prdy = Input(Bool())
        val chn_out_pvld = Output(Bool())
        val data1_out = Output(UInt(DATA1_WIDTH.W))
        val data2_out = Output(UInt(DATA2_WIDTH.W))
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

    io.chn_out_pvld := Mux(io.chn1_en & io.chn2_en, io.chn1_in_pvld & io.chn2_in_pvld, 
                       Mux(io.chn2_en, io.chn2_in_pvld,
                       Mux(io.chn1_en, io.chn1_in_pvld, 
                       false.B)))
    io.chn1_in_prdy := Mux(io.chn1_en & io.chn2_en, io.chn_out_prdy & io.chn2_in_pvld,
                       Mux(io.chn2_en, true.B, 
                       io.chn_out_prdy))
    io.chn2_in_prdy := Mux(io.chn1_en & io.chn2_en, io.chn_out_prdy & io.chn1_in_pvld,
                       Mux(io.chn2_en, io.chn_out_prdy, 
                       true.B))

    io.data1_out := Mux(io.chn1_en, io.data1_in, 0.asUInt)
    io.data2_out := Mux(io.chn2_en, io.data2_in, 0.asUInt)

}
