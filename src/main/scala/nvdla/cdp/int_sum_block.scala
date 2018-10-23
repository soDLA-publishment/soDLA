package nvdla

{

import chisel3._

import common._

//https://github.com/freechipsproject/chisel3/wiki/Multiple-Clock-Domains

// 9 digit


class int_sum_block(pINT8_BW: Int = 16) extends Module {
    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //control signal
        val int8_en = Input(Bool())
        val len5 = Input(Bool())
        val len7 = Input(Bool())
        val len9 = Input(Bool())
        val load_din_d = Input(Bool())
        val load_din_2d = Input(Bool())
        val reg2dp_normalz_len = Input(UInt(2))

        //sq_pd as a input

        val sq_pd = IO(new Bundle{
            val int16 = Input(Vec(input_num, SInt((pINT8_BW*2+1).W)))
            val sq_pd_int8 = IO(new Bundle{
                val lsb = Input(Vec(input_num, SInt((pINT8_BW+1).W)))
                val msb = Input(Vec(input_num, SInt((pINT8_BW+1).W)))
            })
        })

        //output signal
        val int8_sum = Output(SInt(pINT8_BW+1).W))
        val int16_sum = Output(SInt((pINT8_BW*2+1+input_num/2).W))
    })

        
    val io = if(int8_en)  else 

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when (load_din_d) {int8_msb_sum_3_5 := }
        
    }
  
}