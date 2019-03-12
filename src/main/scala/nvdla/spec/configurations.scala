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


class csbMasterConfiguration(){
    val FPGA = true
}

class ppregCongiguration()
{
    val rbk_pointer_0 = "h004"
    val rbk_status_0 = "h10000"

}

// class caccConfiguration()
// {   

    
//     val CACC_IN_WIDTH = NVDLA_MAC_RESULT_WIDTH  //16+log2(atomC),sum result width for one atomic operation.
//     val SDP_MAX_THROUGHPUT = NVDLA_SDP_MAX_THROUGHPUT  //2^n, no bigger than atomM
//     val CACC_ATOMK = NVDLA_MAC_ATOMIC_K_SIZE
//     val CACC_ATOMK_LOG2 = NVDLA_MAC_ATOMIC_K_SIZE_LOG2
//     val CACC_ABUF_WIDTH = CACC_PARSUM_WIDTH*CACC_ATOMK
//     val CACC_DBUF_WIDTH = CACC_FINAL_WIDTH*CACC_ATOMK
//     val CACC_PARSUM_WIDTH = 34  //sum result width for one layer operation.
//     val CACC_FINAL_WIDTH = 32  //sum result width for one layer operation with saturaton.
//     val CACC_SDP_DATA_WIDTH = CACC_FINAL_WIDTH*SDP_MAX_THROUGHPUT
//     val CACC_SDP_WIDTH = CACC_SDP_DATA_WIDTH+2    //cacc to sdp pd width
//     val CACC_DWIDTH_DIV_SWIDTH = (CACC_DBUF_WIDTH)/(CACC_SDP_DATA_WIDTH)  //1,2,4...
//     val CACC_CELL_PARTIAL_LATENCY = 2 
//     val CACC_CELL_FINAL_LATENCY = 2
//     val CACC_D_RAM_WRITE_LATENCY = 1
//     val NVDLA_CACC_D_MISC_CFG_0_PROC_PRECISION_INT8 = "h0"
//     val CACC_CHANNEL_BITS = 12

// if(NVDLA_CC_ATOMC_DIV_ATOMK==1)
//     #define CACC_ABUF_DEPTH                                 NVDLA_MAC_ATOMIC_K_SIZE*2  //2*atomK
//     #define CACC_ABUF_AWIDTH                                NVDLA_MAC_ATOMIC_K_SIZE_LOG2+1   //log2(abuf_depth)
// #elif(NVDLA_CC_ATOMC_DIV_ATOMK==2)
//     #define CACC_ABUF_DEPTH                                 NVDLA_MAC_ATOMIC_K_SIZE*2  //2*atomK
//     #define CACC_ABUF_AWIDTH                                NVDLA_MAC_ATOMIC_K_SIZE_LOG2+1   //log2(abuf_depth)
// #elif(NVDLA_CC_ATOMC_DIV_ATOMK==4)
//     #define CACC_ABUF_DEPTH                                 NVDLA_MAC_ATOMIC_K_SIZE*4  //4*atomK
//     #define CACC_ABUF_AWIDTH                                NVDLA_MAC_ATOMIC_K_SIZE_LOG2+2   //log2(abuf_depth)
// #endif
    
//     val CACC_DBUF_DEPTH = CACC_ABUF_DEPTH
//     val CACC_DBUF_AWIDTH = CACC_ABUF_AWIDTH    //address width
// }


