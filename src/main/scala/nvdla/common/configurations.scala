package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class cdpConfiguration(){
    val pINT8_BW = 8
}

class cdmaConfiguration()
{
    val rscid = 1
    val width = 8
}

class ramSizeCongiguration()
{
    val phy_rows = 20
    val phy_cols = 288
}

class cmacConfiguration()
{
    val CMAC_ATOMC = 128
    val CMAC_ATOMK_HALF = 1
    val RT_CMAC_A2CACC_LATENCY = 2
    val RT_CMAC_B2CACC_LATENCY = 3   
    val CMAC_INPUT_NUM = CMAC_ATOMC
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
    val PKT_nvdla_stripe_info_layer_end_FIELD = 8
    val MAC_PD_LATENCY = (CMAC_OUT_RETIMING + CMAC_ACTV_LATENCY - 3)     //pd must be 3T earlier than data
    val CMAC_SLCG_NUM = CMAC_ATOMK_HALF + 3
    val CMAC_TYPE = UInt  
}

class cmacSINTConfiguration()
{
    val CMAC_ATOMC = 128
    val CMAC_ATOMK_HALF = 8
    val RT_CMAC_A2CACC_LATENCY = 2
    val RT_CMAC_B2CACC_LATENCY = 3   
    val CMAC_INPUT_NUM = CMAC_ATOMC 
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
    val PKT_nvdla_stripe_info_layer_end_FIELD = 8
    val MAC_PD_LATENCY = (CMAC_OUT_RETIMING + CMAC_ACTV_LATENCY - 3)     //pd must be 3T earlier than data
    val CMAC_SLCG_NUM = CMAC_ATOMK_HALF + 3
    val CMAC_TYPE = SInt  
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

class glbConfiguration()
{
    val NVDLA_BDMA_ENABLE = true
    val NVDLA_CDP_ENABLE = true
    val NVDLA_PDP_ENABLE = true
    val NVDLA_RUBIK_ENABLE = true
}

class csbMasterConfiguration(){
    val FPGA = true
}

class ppregCongiguration()
{
    val rbk_pointer_0 = "h004"
    val rbk_status_0 = "h10000"

}


