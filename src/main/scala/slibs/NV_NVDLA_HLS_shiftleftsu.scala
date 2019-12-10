package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_HLS_shiftleftsu(IN_WIDTH:Int, OUT_WIDTH:Int, SHIFT_WIDTH:Int) extends Module {
    
    val SHIFT_MAX = (1<<SHIFT_WIDTH)-1
    val HIGH_WIDTH = SHIFT_MAX + IN_WIDTH - OUT_WIDTH
    
    val io = IO(new Bundle {      
        val data_in = Input(UInt(IN_WIDTH.W))
        val shift_num = Input(UInt(SHIFT_WIDTH.W))
        val data_out = Output(UInt(OUT_WIDTH.W))
    })
        
    val data_sign = io.data_in(IN_WIDTH-1)    

    val data_high = (Cat(Fill(SHIFT_MAX, data_sign), io.data_in) << io.shift_num)(HIGH_WIDTH + OUT_WIDTH - 1, OUT_WIDTH)

    val data_shift = (Cat(Fill(SHIFT_MAX, data_sign), io.data_in) << io.shift_num)(OUT_WIDTH - 1, 0)

    val left_shift_sat = Cat(data_high, data_shift) =/= Fill(HIGH_WIDTH+1, data_sign) 

    val data_max = Mux(data_sign, Cat(true.B, Fill(OUT_WIDTH-1, false.B)), ~Cat(true.B, Fill(OUT_WIDTH-1, false.B)))

    io.data_out := Mux(left_shift_sat, data_max, data_shift)
          
}





