package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class cmacConfiguration extends project_spec
{
    val CMAC_BPE = NVDLA_BPE //bits per element
    val CMAC_ATOMC = NVDLA_MAC_ATOMIC_C_SIZE 
    val CMAC_ATOMK = NVDLA_MAC_ATOMIC_K_SIZE
    val CMAC_ATOMK_HALF  = CMAC_ATOMK/2
    val CMAC_INPUT_NUM = CMAC_ATOMC  //for one MAC_CELL
    val CMAC_SLCG_NUM = 3+CMAC_ATOMK_HALF
    val CMAC_RESULT_WIDTH = NVDLA_MAC_RESULT_WIDTH    //16b+log2(atomC)
    val CMAC_IN_RT_LATENCY = 2   //both for data&pd
    val CMAC_OUT_RT_LATENCY = 2   //both for data&pd
    val CMAC_OUT_RETIMING = 3   //only data
    val CMAC_ACTV_LATENCY = 2   //only data
    val CMAC_DATA_LATENCY = (CMAC_IN_RT_LATENCY+CMAC_OUT_RT_LATENCY+CMAC_OUT_RETIMING+CMAC_ACTV_LATENCY)
    val MAC_PD_LATENCY = (CMAC_OUT_RETIMING+CMAC_ACTV_LATENCY-3)     //pd must be 3T earlier than data
    val RT_CMAC_A2CACC_LATENCY = 2
    val RT_CMAC_B2CACC_LATENCY = 3

    val PKT_nvdla_stripe_info_stripe_st_FIELD = 5
    val PKT_nvdla_stripe_info_stripe_end_FIELD = 6
    val PKT_nvdla_stripe_info_layer_end_FIELD = 8

}


class cmac_core_actv(implicit val conf: nvdlaConfig) extends Bundle{
    val nz = Output(Bool())
    val data = Output(UInt(conf.CMAC_BPE.W))
}

class cmac_reg_dual_flop_outputs extends Bundle{
    val conv_mode = Output(Bool())
    val proc_precision = Output(UInt(2.W))
}
















