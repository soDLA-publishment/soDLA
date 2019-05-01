package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._

class matrixConfiguration()
{
    //retiming
    val HARDFLOAT_MAC_LATENCY = 2  
    val MAC_RETIMING = 0
    val V2V_MAC_LATENCY = 3 + HARDFLOAT_MAC_LATENCY * 3 

    val REC_DT_RETIMING = 3
    val MATRIX_V2V_RETIMING = 6 

    val VERIFY = false
    val KF_STAT_FOR_VERIFY = 4
    val KF_TYPE_FOR_VERIFY = SInt

    val KF_STAT = if(VERIFY) KF_STAT_FOR_VERIFY else 4
    var KF_TYPE = UInt
    val KF_BPE = if(VERIFY) 32 else 33

}


