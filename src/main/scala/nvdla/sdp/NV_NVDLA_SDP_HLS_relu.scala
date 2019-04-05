package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_HLS_relu extends Module {
   val DATA_WIDTH = 32
   val io = IO(new Bundle {
        val data_in = Input(SInt(DATA_WIDTH.W))
        val data_out = Output(SInt(DATA_WIDTH.W))
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

    val data_in_sign = io.data_in(DATA_WIDTH-1)

    when(!data_in_sign){
        io.data_out := io.data_in
    }
    .otherwise{
        io.data_out := 0.asSInt
    }

}