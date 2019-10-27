package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class nvdlaConfig extends xxifConfiguration{

    val NVDLA_DMAIF_BW = NVDLA_MEMIF_WIDTH  

    val MULTI_MASK = (NVDLA_DMAIF_BW/NVDLA_BPE/NVDLA_MEMORY_ATOMIC_SIZE)

    val NVDLA_MEM_MASK_BIT = MULTI_MASK

    val NVDLA_MEM_RD_RSP  = ( NVDLA_DMAIF_BW + NVDLA_MEM_MASK_BIT )
    val NVDLA_MEM_WR_REQ  = ( NVDLA_DMAIF_BW + NVDLA_MEM_MASK_BIT + 1 )
    val NVDLA_MEM_RD_REQ  = ( NVDLA_MEM_ADDRESS_WIDTH + 15 )

}




