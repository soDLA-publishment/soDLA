package nvdla

import chisel3._
import chisel3.util._

// this module is to pad value to the vector
class NV_NVDLA_VEC_padder(vector_len:Int = 8, data_width:Int = 8+6) extends Module {
   val io = IO(new Bundle {

        val vec_in =  Input(Vec(vector_len, UInt(data_width.W)))
        val pad_value = Input(UInt(data_width.W))
        val padding = Input(Bool())
        val vec_out = Output(Vec(vector_len, UInt(data_width.W)))
       
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

    val data = Wire(Vec(vector_len, UInt(data_width.W)))
    val data_ff = Wire(Vec(vector_len, UInt(data_width.W)))
    val data_with_mon = Wire(Vec(vector_len, UInt((data_width+2).W)))
    val data_ff_with_mon = Wire(Vec(vector_len, UInt((data_width+2).W)))
    for(i <- 0 to vector_len-1){
        //precalculate data_ff
        data_ff_with_mon(i) := 
        (Cat(io.vec_in(i)(data_width-1), io.vec_in(i)).asSInt +&
        Cat(io.pad_value(data_width-1), io.pad_value).asSInt).asUInt

        data_with_mon(i) := 
        Mux(io.padding, data_ff_with_mon(i), Cat(Fill(2, io.vec_in(i)(data_width-1)), io.vec_in(i)))

        data_ff(i) := data_ff_with_mon(i)(data_width-1, 0)
        data(i) := data_with_mon(i)(data_width-1, 0)

        io.vec_out(i) := data(i)
    }



}