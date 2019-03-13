package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class cdpConfiguration(){
    val pINT8_BW = 8
}


class ramSizeConfiguration()
{
    val phy_rows = 20
    val phy_cols = 288
}



class cacc2glbConfiguration()
{
    val RT_CMAC_CACC2GLB_LATENCY = 2
}

class csb2caccConfiguration()
{
    val RT_CSB2CACC_LATENCY = 3   
}

class csb2cmacConfiguration()
{
    val RT_CSB2CMAC_LATENCY = 3   
}

class ppregCongiguration()
{
    val rbk_pointer_0 = "h004"
    val rbk_status_0 = "h10000"

}



