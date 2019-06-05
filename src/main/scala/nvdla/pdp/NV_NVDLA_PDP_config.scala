package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class pdpConfiguration extends sdpConfiguration
{
    val NVDLA_PDP_BWPE = NVDLA_BPE
    val NVDLA_PDP_DMAIF_BW = NVDLA_MEMIF_WIDTH

    val SDP_THROUGHPUT = NVDLA_SDP_MAX_THROUGHPUT

    val NVDLA_PDP_ONFLY_INPUT_BW = NVDLA_PDP_BWPE*SDP_THROUGHPUT
    /////////////////////////////////////////////////////////////
    val NVDLA_PDP_MEM_MASK_NUM  = (NVDLA_PDP_DMAIF_BW/NVDLA_PDP_BWPE/NVDLA_MEMORY_ATOMIC_SIZE)
    val NVDLA_PDP_MEM_MASK_BIT = NVDLA_PDP_MEM_MASK_NUM

    val NVDLA_PDP_MEM_RD_RSP = ( NVDLA_PDP_DMAIF_BW + NVDLA_PDP_MEM_MASK_BIT )
    val NVDLA_PDP_MEM_WR_REQ = ( NVDLA_PDP_DMAIF_BW + NVDLA_PDP_MEM_MASK_BIT + 1 )
    val NVDLA_PDP_MEM_RD_REQ = ( NVDLA_MEM_ADDRESS_WIDTH + 15 )

}