package cora

import ChiselFloat._
import chisel3._
import chisel3.util._
import chisel3.experimental._

//modified from MulAddRecFN redpanda

class Vector4_FP extends Module{

    val io = IO(new Bundle {
        //input
        val a = Input(UInt(32.W))
        val b = Input(UInt(32.W))

        //output
        val res = Output(UInt(32.W))
       
    })   

    val adder = Module(new FPAdd32())
    adder.io.a := io.a
    adder.io.b := io.b
    io.res := adder.io.res

}


