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

    

    //redpanda3

    val CDPBW = NVDLA_CDP_THROUGHPUT*NVDLA_CDP_BWPE
    val CDP_ICVTO_BW = NVDLA_CDP_THROUGHPUT*NVDLA_CDP_ICVTO_BWPE
    val BATCH_CDP_NUM = ATMM/CDPBW
    val TOTAL_CDP_NUM = NVDLA_MEMIF_WIDTH/PDPBW 

    val NVDLA_CDP_MEM_MASK_BIT = ATMM_NUM

    val CDP_TPBW = log2Ceil(NVDLA_CDP_THROUGHPUT)

    val NVDLA_CDP_MEM_RD_RSP = ( NVDLA_CDP_DMAIF_BW + NVDLA_CDP_MEM_MASK_BIT )
    val NVDLA_CDP_MEM_WR_REQ = ( NVDLA_CDP_DMAIF_BW + NVDLA_CDP_MEM_MASK_BIT + 1 )
    val NVDLA_CDP_MEM_RD_REQ = ( NVDLA_MEM_ADDRESS_WIDTH + 15 )

    val pINT8_BW = NVDLA_BPE + 1   //int8 bitwidth after icvt
    val pPP_BW = (pINT8_BW + pINT8_BW) - 1 + 4  //(pINT8_BW * pINT8_BW) -1 is for int8 mode x^2, +4 is after 9 lrn




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


class cdp_reg_dual_flop_outputs extends Bundle{
    val cya = Output(UInt(32.W))
    val input_data_type = Output(UInt(2.W))
    val datin_offset = Output(UInt(16.W))
    val datin_scale = Output(UInt(16.W))
    val datin_shifter = Output(UInt(5.W))
    val datout_offset = Output(UInt(32.W))
    val datout_scale = Output(UInt(16.W))
    val datout_shifter = Output(UInt(6.W))
    val dst_base_addr_high = Output(UInt(32.W))
    val dst_base_addr_low = Output(UInt(32.W))
    val dst_ram_type = Output(Bool())
    val dst_line_stride = Output(UInt(32.W))
    val dst_surface_stride = Output(UInt(32.W))
    val mul_bypass = Output(Bool())
    val sqsum_bypass = Output(Bool())
    val normalz_len = Output(UInt(2.W))
    val nan_to_zero = Output(Bool())
    val dma_en = Output(Bool())
    val lut_en = Output(Bool())
}


class cdp_reg_single_flop_outputs extends Bundle{
    val lut_access_type = Output(Bool())
    val lut_table_id = Output(Bool())
    val lut_hybrid_priority = Output(Bool())
    val lut_le_function = Output(Bool())
    val lut_oflow_priority = Output(Bool())
    val lut_uflow_priority = Output(Bool())
    val lut_le_index_offset = Output(UInt(8.W))
    val lut_le_index_select = Output(UInt(8.W))
    val lut_lo_index_select = Output(UInt(8.W))
    val lut_le_end_high = Output(UInt(6.W))
    val lut_le_end_low = Output(UInt(32.W))
    val lut_le_slope_oflow_scale = Output(UInt(16.W))
    val lut_le_slope_uflow_scale = Output(UInt(16.W))
    val lut_le_slope_oflow_shift = Output(UInt(5.W))
    val lut_le_slope_uflow_shift = Output(UInt(5.W))
    val lut_le_start_high = Output(UInt(6.W))
    val lut_le_start_low = Output(UInt(32.W))
    val lut_lo_end_high = Output(UInt(6.W))
    val lut_lo_end_low = Output(UInt(32.W))
    val lut_lo_slope_oflow_scale = Output(UInt(16.W))
    val lut_lo_slope_uflow_scale = Output(UInt(16.W))
    val lut_lo_slope_oflow_shift = Output(UInt(5.W))
    val lut_lo_slope_uflow_shift = Output(UInt(5.W))
    val lut_lo_start_high = Output(UInt(6.W))
    val lut_lo_start_low = Output(UInt(32.W))
}