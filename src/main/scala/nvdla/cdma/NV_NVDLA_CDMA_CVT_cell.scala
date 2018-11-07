package nvdla

import chisel3._

import common._

class NV_NVDLA_CDMA_CVT_cell(implicit conf: cdmaConfiguration) extends BlackBox {

    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        
        val cfg_mul_in_rsc_z = Input(UInt(16.W))
        val cfg_in_precision = Input(UInt(2.W))

        //control signal
        val int8_en = Input(Bool())
        val len5 = Input(Bool())
        val len7 = Input(Bool())
        val len9 = Input(Bool())
        val load_din_d = Input(Bool())
        val load_din_2d = Input(Bool())
        val reg2dp_normalz_len = Input(UInt(2.W))

        //sq_pd as a input
        val sq_pd_int8 = Input(Vec(9, SInt((2*pINT8_BW-1).W)))

        //output signal
        val int8_sum = Output(SInt((2*pINT8_BW+3).W))
    })





}