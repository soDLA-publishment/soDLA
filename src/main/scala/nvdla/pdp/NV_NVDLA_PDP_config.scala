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

    //redpanda3----------------
    val PDPBW = NVDLA_PDP_THROUGHPUT*NVDLA_PDP_BWPE
    val PDP_NUM = ATMM/PDPBW

    val NVDLA_HLS_ADD17_LATENCY = 4

}


class pdp_rdma_reg_flop_outputs extends Bundle{


        val cya = Output(UInt(32.W))
        val cube_in_channel = Output(UInt(13.W))
        val cube_in_height = Output(UInt(13.W))
        val cube_in_width = Output(UInt(13.W))
        val input_data = Output(UInt(2.W))
        val flying_mode = Output(Bool())
        val split_num = Output(UInt(8.W))
        val partial_width_in_first = Output(UInt(10.W))
        val partial_width_in_last = Output(UInt(10.W))
        val partial_width_in_mid = Output(UInt(10.W))
        val dma_en = Output(Bool())
        val kernel_stride_width = Output(UInt(4.W))
        val kernel_width = Output(UInt(4.W))
        val pad_width = Output(UInt(4.W))
        val src_base_addr_high = Output(UInt(32.W))
        val src_base_addr_low = Output(UInt(32.W))
        val src_line_stride = Output(UInt(32.W))
        val src_ram_type = Output(Bool())
        val src_surface_stride = Output(UInt(32.W))
}