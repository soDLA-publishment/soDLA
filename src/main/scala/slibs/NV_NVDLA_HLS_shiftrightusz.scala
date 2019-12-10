package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_HLS_shiftrightusz(IN_WIDTH:Int, OUT_WIDTH:Int, FRAC_WIDTH:Int, SHIFT_WIDTH:Int) extends Module {

    //suppose FRAC_WIDTH > IN_WIDTH > OUT_WIDTH
    val SHIFT_MAX = Math.pow(2, SHIFT_WIDTH- 1).toInt 
    val HIGH_WIDTH = SHIFT_MAX + IN_WIDTH - OUT_WIDTH    
    
    val io = IO(new Bundle {      
        val data_in = Input(UInt(IN_WIDTH.W))
        val shift_num = Input(UInt(SHIFT_WIDTH.W))
        val data_out = Output(UInt(OUT_WIDTH.W))
        val frac_out = Output(UInt(FRAC_WIDTH.W))
    })
        
    //shift left
    val shift_sign = io.shift_num(SHIFT_WIDTH-1)

    val shift_num_abs = ~io.shift_num +& 1.U

    val data_high = (Cat(Fill(SHIFT_MAX, false.B), io.data_in) << shift_num_abs)(HIGH_WIDTH + OUT_WIDTH - 1, OUT_WIDTH)
    
    val data_shift_l = (Cat(Fill(SHIFT_MAX, false.B), io.data_in) << shift_num_abs)(OUT_WIDTH - 1, 0)

    val left_shift_sat = shift_sign & (Cat(data_high, data_shift_l) =/= Fill(HIGH_WIDTH+1, false.B))

    //shift right
    val data_shift_r = (Cat(io.data_in, Fill(FRAC_WIDTH, false.B)) >> io.shift_num)(IN_WIDTH + FRAC_WIDTH -1, FRAC_WIDTH)

    val frac_shift = (Cat(io.data_in, Fill(FRAC_WIDTH, false.B)) >> io.shift_num)(FRAC_WIDTH-1, 0)

    val right_shift_sat = !shift_sign & data_shift_r(IN_WIDTH-1, OUT_WIDTH).orR

    val data_max = Fill(OUT_WIDTH, true.B)

    //final out
    io.data_out := Mux(left_shift_sat | right_shift_sat, data_max, Mux(shift_sign, data_shift_l, data_shift_r(OUT_WIDTH-1, 0)))
    io.frac_out := Mux(shift_sign, Fill(FRAC_WIDTH, false.B), frac_shift)
  
}

