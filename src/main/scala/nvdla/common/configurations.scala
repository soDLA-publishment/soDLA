package nvdla

import chisel3._
import chisel3.experimental._

   
case class cdpConfiguration(){
    val pINT8_BW = 8
}

case class cdmaConfiguration()
{
    val rscid = 1
    val width = 8
}


case class ramSizeCongiguration()
{
    val phy_rows = 20
    val phy_cols = 288
}

case class cmacv1Configuration()
{
    //谜一样的变量
    val CMAC_ATOMC = 128
    val CMAC_ATOMK_HALF = 8
    val CMAC_RESULT_WIDTH = 176
    val RT_CMAC_A2CACC_LATENCY = 2
    val CMAC_INPUT_NUM = 128
    val CMAC_BPE = 8
    val EXP = 192
    val PVLD = 104
    val NAN = 64
}

case class cmacConfiguration()
{
    //谜一样的变量
    val CMAC_ATOMC = 128
    val CMAC_ATOMK_HALF = 8
    val CMAC_RESULT_WIDTH = 176
    val RT_CMAC_A2CACC_LATENCY = 2
    val CMAC_INPUT_NUM = 128
    val CMAC_BPE = 8
    val EXP = 192
    val PVLD = 104
    val NAN = 64
    val CMAC_RESULT_WIDTH = 16 + 7   //16b+log2(atomC)
    val CMAC_OUT_RETIMING = 3
    val CMAC_IN_RT_LATENCY = 2
    val CMAC_OUT_RT_LATENCY  = 2
    val CMAC_ACTV_LATENCY = 2
    val PKT_nvdla_stripe_info_stripe_st_FIELD = 5
    val PKT_nvdla_stripe_info_stripe_end_FIELD = 6
    val MAC_PD_LATENCY = (CMAC_OUT_RETIMING+CMAC_ACTV_LATENCY-3)     //pd must be 3T earlier than data
    
}


case class cacc2glbConfiguration()
{
    val RT_CMAC_CACC2GLB_LATENCY = 2
}

case class csb2caccConfiguration()
{
    val RT_CSB2CACC_LATENCY = 3   
}

case class csb2cmacConfiguration()
{
    val RT_CSB2CMAC_LATENCY = 3   
}


case class glbConfiguration()
{
    val NVDLA_BDMA_ENABLE = true
    val NVDLA_CDP_ENABLE = true
    val NVDLA_PDP_ENABLE = true
    val NVDLA_RUBIK_ENABLE = true
}

case class csbMasterConfiguration(){
    val FPGA = true
}



