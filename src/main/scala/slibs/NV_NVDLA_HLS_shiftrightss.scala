package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_HLS_shiftrightss(IN_WIDTH:Int = 49, OUT_WIDTH:Int = 32, SHIFT_WIDTH:Int = 6) extends Module {
    
    val SHIFT_MAX = Math.pow(2, SHIFT_WIDTH- 1).toInt 
    val HIGH_WIDTH = SHIFT_MAX + IN_WIDTH - OUT_WIDTH    
    
    val io = IO(new Bundle {      
        val data_in = Input(UInt(IN_WIDTH.W))
        val shift_num = Input(UInt(SHIFT_WIDTH.W))
        val data_out = Output(UInt(OUT_WIDTH.W))
    })
        
    val data_sign = io.data_in(IN_WIDTH-1)
    val shift_sign = io.shift_num(SHIFT_WIDTH-1)

    //shift left
    val shift_num_abs = ~io.shift_num + 1.U

    val data_high = (Cat(Fill(SHIFT_MAX, data_sign), io.data_in) << shift_num_abs)(IN_WIDTH + OUT_WIDTH - 1, OUT_WIDTH)
    
    val data_shift_l = (Cat(Fill(SHIFT_MAX, data_sign), io.data_in) << shift_num_abs)(OUT_WIDTH - 1, 0)

    val left_shift_sat = shift_sign & Cat(data_high, data_shift_l) =/= Fill(HIGH_WIDTH+1, data_sign)

    //shift right

    val data_highr = (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(3*IN_WIDTH - 1, 2*IN_WIDTH)
    
    val data_shift_rt = (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(2*IN_WIDTH - 1, IN_WIDTH)

    val guide = (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(IN_WIDTH - 1)
    
    val stick = (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(IN_WIDTH - 2, 0)

    val data_shift_r = Mux(io.shift_num >= IN_WIDTH.U, Fill(IN_WIDTH, false.B), data_shift_rt)

    val point5 = Mux(io.shift_num >= IN_WIDTH.U, false.B, guide & (~data_sign | stick.orR))

    val data_round = (data_shift_r(OUT_WIDTH-1, 0) + point5)(OUT_WIDTH-1, 0)

    val right_shift_sat = !shift_sign & 
                        ( data_sign & ~(data_shift_r(IN_WIDTH-2, OUT_WIDTH-1).andR)) |
                        (~data_sign &  (data_shift_r(IN_WIDTH-2, OUT_WIDTH-1).orR)) |
                        (~data_sign & (Cat(data_shift_r(((OUT_WIDTH-1) - 1), 0), point5).andR))
    
    val data_max = Mux(data_sign, Cat(true.B, Fill(OUT_WIDTH-1, false.B)), ~Cat(true.B, Fill(OUT_WIDTH-1, false.B))) 
    
    io.data_out := Mux(left_shift_sat | right_shift_sat, data_max, Mux(shift_sign, data_shift_l, data_round))

  
}



