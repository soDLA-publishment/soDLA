package nvdla

import chisel3._
import chisel3.experimental._


class NV_NVDLA_HLS_shiftrightss extends Module {
    
    val IN_WIDTH  = 49
    val OUT_WIDTH = 32
    val SHIFT_WIDTH = 6
    val SHIFT_MAX = Math.pow(2, SHIFT_WIDTH- 1).toInt 
    val HIGH_WIDTH = SHIFT_MAX + IN_WIDTH - OUT_WIDTH    
    
    val io = IO(new Bundle {      
        val data_in = Input(UInt(IN_WIDTH.W))
        val shift_num = Input(UInt(SHIFT_WIDTH.W))
        val data_out = Output(UInt(OUT_WIDTH.W))
    })
        
    val data_shift_l = Wire(UInt(OUT_WIDTH.W))
    val data_high = Wire(UInt(HIGH_WIDTH.W))
    val data_highr = Wire(UInt(IN_WIDTH.W))
    val data_shift_rt = Wire(UInt(IN_WIDTH.W))
    val data_shift_r = Wire(UInt(IN_WIDTH.W))
    val stick = Wire(UInt((IN_WIDTH-1).W))
    val data_max = Wire(UInt(OUT_WIDTH.W))
    val data_round = Wire(UInt(OUT_WIDTH.W))
    val shift_sign = Wire(Bool())
    val data_sign = Wire(Bool())
    val guide = Wire(Bool())
    val point5 = Wire(Bool())
    val left_shift_sat = Wire(Bool())
    val right_shift_sat = Wire(Bool())

    val shift_num_abs = Wire(UInt(SHIFT_WIDTH.W))

    
    data_sign := io.data_in(IN_WIDTH-1)
    shift_sign := io.shift_num(SHIFT_WIDTH-1)

    //shift left
    shift_num_abs := ~io.shift_num + 1.U

    data_high := (Cat(Fill(SHIFT_MAX, data_sign), io.data_in) << shift_num_abs)(IN_WIDTH + OUT_WIDTH - 1, OUT_WIDTH)
    
    data_shift_l := (Cat(Fill(SHIFT_MAX, data_sign), io.data_in) << shift_num_abs)(OUT_WIDTH - 1, 0)

    left_shift_sat := shift_sign & Cat(data_high, data_shift_l) != Fill(HIGH_WIDTH+1, data_sign)

    //shift right

    data_highr := (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(3*IN_WIDTH - 1, 2*IN_WIDTH)
    
    data_shift_rt := (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(2*IN_WIDTH - 1, IN_WIDTH)

    guide := (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(IN_WIDTH - 1)
    
    stick := (Cat(Fill(IN_WIDTH, data_sign), io.data_in, Fill(IN_WIDTH, false.B)) >> io.shift_num)(IN_WIDTH - 2, 0)

    data_shift_r := Mux(io.shift_num >= IN_WIDTH.U, Fill(IN_WIDTH, false.B), data_shift_rt)

    point5 := Mux(io.shift_num >= IN_WIDTH.U, false.B, guide & (~data_sign | stick.orR))

    data_round := (data_shift_r(OUT_WIDTH-1, 0) + point5)(OUT_WIDTH-1, 0)

    right_shift_sat := !shift_sign & 
                      ( data_sign & ~(data_shift_r(IN_WIDTH-2, OUT_WIDTH-1).andR)) |
                      (~data_sign &  (data_shift_r(IN_WIDTH-2, OUT_WIDTH-1).orR)) |
                      (~data_sign & (Cat(data_shift_r(((OUT_WIDTH-1) - 1), 0), point5).andR))
    
    data_max := Mux(data_sign, Cat(true.B, Fill(OUT_WIDTH-1, false.B)), ~Cat(true.B, Fill(OUT_WIDTH-1, false.B))) 
    
    io.data_out := Mux(left_shift_sat | right_shift_sat, data_max, Mux(shift_sign, data_shift_l, data_round))

  
}

