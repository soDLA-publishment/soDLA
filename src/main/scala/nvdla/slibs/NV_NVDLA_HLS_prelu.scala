package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_HLS_prelu(IN_WIDTH:Int=32, OUT_WIDTH:Int=64, OP_WIDTH:Int=32) extends Module {
   val io = IO(new Bundle {
        val cfg_prelu_en = Input(Bool())
        val data_in = Input(UInt(IN_WIDTH.W))
        val op_in = Input(UInt(OP_WIDTH.W))
        val data_out =  Output(UInt(OUT_WIDTH.W))
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

    val data_in_sign = io.data_in

    when(io.cfg_prelu_en & !data_in_sign){
        io.data_out := io.data_in
    }
    .otherwise{
        io.data_out := (io.data_in.asSInt * io.op_in.asSInt).asUInt
    }

}