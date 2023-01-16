package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//calcate pooling data based on real pooling size --- (* 1/kernel_width or 1/kernel_height)
class NV_NVDLA_VEC_DIV_kernel(vector_len:Int = 8, data_width:Int = 8+6) extends Module {
   val round_width = data_width-3

   val io = IO(new Bundle {
        val vec_in = Input(Vec(vector_len, UInt(data_width.W)))  
        val reg2dp_recip_width_or_height_use = Input(UInt(17.W))
        val average_pooling_en = Input(Bool())
        val vec_out = Output(Vec(vector_len, UInt(round_width.W)))      
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
    
    val data_ext_ff = VecInit((0 to vector_len-1) 
        map { i => (io.vec_in(i).asSInt * Cat(false.B, io.reg2dp_recip_width_or_height_use).asSInt).asUInt})
    val data_ext = VecInit((0 to vector_len-1) 
        map { i => Mux(io.average_pooling_en, data_ext_ff(i), Cat(io.vec_in(i)(data_width-1), io.vec_in(i), "b0".asUInt(16.W)))})
    val data_ext_less_neg_0_5 = VecInit((0 to vector_len-1) 
        map { i => data_ext(i)(data_width + 16)&(data_ext(i)(15)&(~(data_ext(i)(14, 0).orR)|(~data_ext(i)(15)))) })
    val data_ext_more_neg_0_5 = VecInit((0 to vector_len-1) 
        map { i => data_ext(i)(data_width + 16)&(data_ext(i)(15)&(data_ext(i)(14, 0).orR)) })
    val data_ext_add1 = VecInit((0 to vector_len-1) 
        map { i => data_ext(i)(round_width+16-1, 16) + 1.U})
    val mult = VecInit((0 to vector_len-1) 
        map { i => Mux(data_ext_less_neg_0_5(i), data_ext(i)(round_width+16-1, 16),
                   Mux(data_ext_more_neg_0_5(i), data_ext_add1(i),
                   data_ext(i)(round_width+16-2, 16) +& data_ext(i)(15)))})  //rounding 0.5=1, -0.5=-1

    for(i<- 0 to vector_len-1){
        io.vec_out(i) := mult(i)
    }

}