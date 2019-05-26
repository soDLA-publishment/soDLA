package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class cdpConfiguration extends project_spec
{
    val NVDLA_CDP_BWPE = NVDLA_BPE
    val NVDLA_CDP_ICVTO_BWPE = NVDLA_CDP_BWPE+1
    val NVDLA_CDP_DMAIF_BW = NVDLA_MEMIF_WIDTH
    val NVDLA_CDP_MEM_ADDR_BW = NVDLA_MEM_ADDRESS_WIDTH

    val LARGE_FIFO_RAM = true
    val SMALL_FIFO_RAM = false

}