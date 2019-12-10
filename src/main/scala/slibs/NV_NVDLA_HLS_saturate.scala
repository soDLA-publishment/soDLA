package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_HLS_saturate(IN_WIDTH:Int, OUT_WIDTH:Int) extends Module {
    
    val io = IO(new Bundle {      
        val data_in = Input(UInt(IN_WIDTH.W))
        val data_out = Output(UInt(OUT_WIDTH.W))
    })
            
    val data_sign = io.data_in(IN_WIDTH-1)
    
    val tru_need_sat = (data_sign & ~(io.data_in(IN_WIDTH-2, OUT_WIDTH-1).andR)) | (~data_sign & (io.data_in(IN_WIDTH-2, OUT_WIDTH-1).orR))
    
    val data_max = Mux(data_sign, Cat(true.B, Fill(OUT_WIDTH-1, false.B)), ~Cat(true.B, Fill(OUT_WIDTH-1, false.B)))
    
    io.data_out := Mux(tru_need_sat, data_max, io.data_in)
         
}



