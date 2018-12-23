package nvdla

import chisel3._
import chisel3.experimental._


class NV_NVDLA_HLS_saturate extends Module {
    
    val IN_WIDTH = 49
    val OUT_WIDTH = 32
    
    val io = IO(new Bundle {      
        val data_in = Input(UInt(IN_WIDTH.W))
        val data_out = Output(UInt(OUT_WIDTH.W))
    })
        
    val data_max = Wire(UInt(OUT_WIDTH.W))
    val data_sign = Wire(Bool())
    val tru_need_sat = Wire(Bool())
    
    data_sign := io.data_in(IN_WIDTH-1)
    
    tru_need_sat := (data_sign & ~(io.data_in(IN_WIDTH-2, OUT_WIDTH-1).andR)) | (~data_sign & (io.data_in(IN_WIDTH-2, OUT_WIDTH-1).orR))
    
    data_max := Mux(data_sign, Cat(true.B, Fill(OUT_WIDTH-1, false.B)), ~Cat(true.B, Fill(OUT_WIDTH-1, false.B)))
    
    io.data_out := Mux(tru_need_sat, data_max, io.data_in)
    
       
}

