package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.iotesters.Driver

class NV_DW_lsd(a_width:Int = 8) extends Module {
    val b_width = a_width-1
    val enc_width = log2Ceil(a_width)
    val io = IO(new Bundle {
        val a = Input(UInt(a_width.W))
        val dec = Output(UInt(a_width.W))
        val enc = Output(UInt(enc_width.W))
    })
    //get the encoded output: the number of sign bits.
    def DWF_lsd_enc(A: UInt):Int = {
        var done = 0;
        var temp_enc = a_width-1;
        var i = a_width - 2
        do{
            if( A(i+1) != A(i) ){
                temp_enc = a_width - i -2
                done = 1
            }
            else if( i == 0 ){
                temp_enc = a_width - 1
                done = 1
            }
            i = i - 1
        }while(done == 0);
        temp_enc
    }

    //get the sign bit position of input.
    def DWF_lsd(A: UInt):UInt = {
        val temp_enc = DWF_lsd_enc(A);
        val temp = b_width - temp_enc;
        val temp_dec = 1.U << (temp-1).U;
        temp_dec
    }

    io.enc := DWF_lsd_enc(io.a).asUInt(a_width.W)
    io.dec := DWF_lsd(io.a)
    
}


object NV_DW_lsdDriver extends App {
  chisel3.Driver.execute(args, () => new NV_DW_lsd)
}


