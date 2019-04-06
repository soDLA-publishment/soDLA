package cora

import chisel3._
import chisel3.experimental._
import chisel3.util._

class matrixConfiguration()
{
    //retiming
    val MATRIX_V2V_RETIMING = 6 

    val VERIFY_MODE = true
    val KF_STAT_FOR_VERIFY = 4
    val KF_TYPE_FOR_VERIFY = SInt

    val KF_STAT = if(VERIFY_MODE) KF_STAT_FOR_VERIFY else 4
    var KF_TYPE = SInt
    val KF_BPE = if(VERIFY_MODE) 32 else 33

}


