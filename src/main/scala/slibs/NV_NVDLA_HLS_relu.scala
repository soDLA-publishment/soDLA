package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_HLS_relu(DATA_WIDTH:Int = 32) extends Module {
   val io = IO(new Bundle {
        val data_in = Input(UInt(DATA_WIDTH.W))
        val data_out = Output(UInt(DATA_WIDTH.W))
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
        io.data_out := 0.asUInt
    }

}