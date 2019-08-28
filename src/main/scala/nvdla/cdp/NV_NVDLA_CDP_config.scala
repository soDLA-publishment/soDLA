package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class cdpConfiguration extends pdpConfiguration
{
    val NVDLA_CDP_BWPE = NVDLA_BPE
    val NVDLA_CDP_ICVTO_BWPE = NVDLA_CDP_BWPE+1
    val NVDLA_CDP_DMAIF_BW = NVDLA_MEMIF_WIDTH
    val NVDLA_CDP_MEM_ADDR_BW = NVDLA_MEM_ADDRESS_WIDTH

    val LARGE_FIFO_RAM = true
    val SMALL_FIFO_RAM = false
    

}

class cdp_rdma_reg_dual_flop_outputs extends Bundle{
    val cya = Output(UInt(32.W))
    val channel = Output(UInt(13.W))
    val height = Output(UInt(13.W))
    val cdp_width = Output(UInt(13.W))
    val input_data = Output(UInt(2.W))
    val dma_en = Output(Bool())
    val src_base_addr_high = Output(UInt(32.W))
    val src_base_addr_low = Output(UInt(32.W))
    val src_ram_type = Output(Bool())
    val src_line_stride = Output(UInt(32.W))
    val src_surface_stride = Output(UInt(32.W))
}
