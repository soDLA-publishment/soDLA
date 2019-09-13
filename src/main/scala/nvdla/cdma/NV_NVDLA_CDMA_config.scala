package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import scala.math._


class cdmaConfiguration extends caccConfiguration{
    val CDMA_CBUF_WR_LATENCY = 3
    val NVDLA_HLS_CDMA_CVT_LATENCY = 3
    val CDMA_SBUF_SDATA_BITS = NVDLA_MEMORY_ATOMIC_SIZE*NVDLA_BPE
    val CDMA_SBUF_DEPTH = 256
    val CDMA_SBUF_NUMBER = 16
    val CDMA_SBUF_RD_LATENCY = 2
    val CDMA_SBUF_WR_LATENCY = 3

    val CDMA_CVT_CELL_LATENCY = NVDLA_HLS_CDMA_CVT_LATENCY
    val CDMA_CVT_LATENCY = CDMA_CVT_CELL_LATENCY + 3
    val CDMA_STATUS_LATENCY = (CDMA_CBUF_WR_LATENCY + CDMA_CVT_LATENCY)

    val SBUF_WINOGRAD = if(NVDLA_WINOGRAD_ENABLE) 1 else 0

    //DorisL-S----------------
    val NVDLA_CDMA_DMAIF_BW = NVDLA_MEMIF_WIDTH
    val NVDLA_CDMA_MEM_MASK_BIT = (NVDLA_MEMIF_WIDTH/NVDLA_BPE/NVDLA_MEMORY_ATOMIC_SIZE)

    val NVDLA_CDMA_MEM_RD_RSP  = ( NVDLA_MEMIF_WIDTH + NVDLA_CDMA_MEM_MASK_BIT )
    val NVDLA_CDMA_MEM_WR_REQ = ( NVDLA_MEMIF_WIDTH + NVDLA_CDMA_MEM_MASK_BIT + 1 )
    val NVDLA_CDMA_MEM_RD_REQ = ( NVDLA_MEM_ADDRESS_WIDTH + 15 )

    val CBUF_WR_BANK_ADDR_BITS  = 9 
    val CDMA_GRAIN_MAX_BIT = NVDLA_CDMA_GRAIN_MAX_BIT

    //redpanda3----------------
    val ATMM = NVDLA_MEMORY_ATOMIC_SIZE*NVDLA_BPE
    val ATMMBW = log2Ceil(NVDLA_MEMORY_ATOMIC_SIZE) 
    val ATMKBW = log2Ceil(NVDLA_MAC_ATOMIC_C_SIZE) 
    val DMAIF = NVDLA_MEMIF_WIDTH
    val ATMC = NVDLA_MAC_ATOMIC_C_SIZE*NVDLA_BPE
    val ATMM_NUM = DMAIF/ATMM
    val ATMC_NUM = ATMC/ATMM
    val BNUM = NVDLA_MEMIF_WIDTH/NVDLA_BPE
    val MN_BW = NVDLA_MEMIF_WIDTH / NVDLA_BPE * 16
    val SS = log2Ceil(ATMC/ATMM)
    var KK = log2Ceil(ATMC/DMAIF)
    if(DMAIF >= ATMC){
        KK = 0
    }
    val ATMM8 = ((8*NVDLA_MEMORY_ATOMIC_SIZE)/NVDLA_MAC_ATOMIC_C_SIZE)
    val CBUF_BANK_SIZE = NVDLA_CBUF_BANK_WIDTH * NVDLA_CBUF_BANK_DEPTH
    val CDMA_ADDR_ALIGN = NVDLA_MEMORY_ATOMIC_SIZE
    val CBUF_BANK_FETCH_BITS = log2Ceil(CBUF_BANK_SIZE/CDMA_ADDR_ALIGN)
    val BANK_DEPTH_BITS = log2Ceil(ATMC/DMAIF) + KK
  


}

class cdma_dual_reg_flop_outputs extends Bundle{
    val data_bank = Output(UInt(5.W))
    val weight_bank = Output(UInt(5.W))
    val batches = Output(UInt(5.W))
    val batch_stride = Output(UInt(32.W))
    val conv_x_stride = Output(UInt(3.W))
    val conv_y_stride = Output(UInt(3.W))
    val cvt_en = Output(Bool())
    val cvt_truncate = Output(UInt(6.W))
    val cvt_offset = Output(UInt(16.W))
    val cvt_scale = Output(UInt(16.W))
    val cya = Output(UInt(32.W))
    val datain_addr_high_0 = Output(UInt(32.W))
    val datain_addr_high_1 = Output(UInt(32.W))
    val datain_addr_low_0 = Output(UInt(32.W))
    val datain_addr_low_1 = Output(UInt(32.W))
    val line_packed = Output(Bool())
    val surf_packed = Output(Bool())
    val datain_ram_type = Output(Bool())
    val datain_format = Output(Bool())
    val pixel_format = Output(UInt(6.W))
    val pixel_mapping = Output(Bool())
    val pixel_sign_override = Output(Bool())
    val datain_height = Output(UInt(13.W))
    val datain_width = Output(UInt(13.W))
    val datain_channel = Output(UInt(13.W))
    val datain_height_ext = Output(UInt(13.W))
    val datain_width_ext = Output(UInt(13.W))
    val entries = Output(UInt(14.W))
    val grains = Output(UInt(12.W))
    val line_stride = Output(UInt(32.W))
    val uv_line_stride = Output(UInt(32.W))
    val mean_format = Output(Bool())
    val mean_gu = Output(UInt(16.W))
    val mean_ry = Output(UInt(16.W))
    val mean_ax = Output(UInt(16.W))
    val mean_bv = Output(UInt(16.W))
    val conv_mode = Output(Bool())
    val data_reuse = Output(Bool())
    val in_precision = Output(UInt(2.W))
    val proc_precision = Output(UInt(2.W))
    val skip_data_rls = Output(Bool())
    val skip_weight_rls = Output(Bool())
    val weight_reuse = Output(Bool())
    val nan_to_zero = Output(Bool())
    val dma_en = Output(Bool())
    val pixel_x_offset = Output(UInt(5.W))
    val pixel_y_offset = Output(UInt(3.W))
    val rsv_per_line = Output(UInt(10.W))
    val rsv_per_uv_line = Output(UInt(10.W))
    val rsv_height = Output(UInt(3.W))
    val rsv_y_index = Output(UInt(5.W))
    val surf_stride = Output(UInt(32.W))
    val weight_addr_high = Output(UInt(32.W))
    val weight_addr_low = Output(UInt(32.W))
    val weight_bytes = Output(UInt(32.W))
    val weight_format = Output(Bool())
    val weight_ram_type = Output(Bool())
    val byte_per_kernel = Output(UInt(18.W))
    val weight_kernel = Output(UInt(13.W))
    val wgs_addr_high = Output(UInt(32.W))
    val wgs_addr_low = Output(UInt(32.W))
    val wmb_addr_high = Output(UInt(32.W))
    val wmb_addr_low = Output(UInt(32.W))
    val wmb_bytes = Output(UInt(28.W))
    val pad_bottom = Output(UInt(6.W))
    val pad_left = Output(UInt(5.W))
    val pad_right = Output(UInt(6.W))
    val pad_top = Output(UInt(5.W))
    val pad_value = Output(UInt(16.W))
}


