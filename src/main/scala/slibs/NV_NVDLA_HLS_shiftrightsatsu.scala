package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_HLS_shiftrightsatsu(IN_WIDTH:Int = 49, OUT_WIDTH:Int = 32, SHIFT_WIDTH:Int = 6) extends Module {
    
    val io = IO(new Bundle {      
        val data_in = Input(UInt(IN_WIDTH.W))
        val shift_num = Input(UInt(SHIFT_WIDTH.W))
        val data_out = Output(UInt(OUT_WIDTH.W))
        val sat_out = Output(Bool())
    })
        
    
    val data_sign = io.data_in(IN_WIDTH-1)
    
    val data_high = (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(3*IN_WIDTH - 1, 2*IN_WIDTH)
    
    val data_shift = (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(2*IN_WIDTH - 1, IN_WIDTH)

    val guide = (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(IN_WIDTH - 1)
    
    val stick = (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(IN_WIDTH - 2, 0)

    val point5 = guide & (~data_sign | stick.orR)

    val data_round = (data_shift(OUT_WIDTH-1, 0) +& point5)(OUT_WIDTH-1, 0)

    val tru_need_sat =  ( data_sign & ~(data_shift(IN_WIDTH-2, OUT_WIDTH-1).andR)) |
                      (~data_sign &  (data_shift(IN_WIDTH-2, OUT_WIDTH-1).orR)) |
                      (~data_sign & (Cat(data_shift(((OUT_WIDTH-1) - 1), 0), point5).andR))
    
    val data_max = Mux(data_sign, Cat(true.B, Fill(OUT_WIDTH-1, false.B)), ~Cat(true.B, Fill(OUT_WIDTH-1, false.B))) 
    
    io.data_out := Mux(io.shift_num >= IN_WIDTH.U, Fill(OUT_WIDTH, false.B), Mux(tru_need_sat, data_max, data_round))

    io.sat_out :=  Mux(io.shift_num >= IN_WIDTH.U, false.B, tru_need_sat) 
  
}




