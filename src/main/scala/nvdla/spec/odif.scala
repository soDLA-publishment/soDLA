package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

// flow valid
class csc2cmac_data_if(implicit val conf: nvdlaConfig) extends Bundle{
    val mask = Output(Vec(conf.NVDLA_MAC_ATOMIC_C_SIZE, Bool()))
    val data = Output(Vec(conf.NVDLA_MAC_ATOMIC_C_SIZE, UInt(conf.NVDLA_BPE.W)))
}

//  flow valid
class csc2cmac_wt_if(implicit val conf: nvdlaConfig) extends Bundle{
    val sel = Vec(conf.NVDLA_MAC_ATOMIC_K_SIZE_DIV2, Bool())
    val mask = Vec(conf.NVDLA_MAC_ATOMIC_C_SIZE, Bool())
    val data = Vec(conf.NVDLA_MAC_ATOMIC_C_SIZE, UInt(conf.NVDLA_BPE.W))
}

//  flow valid
class cmac2cacc_if(implicit val conf: nvdlaConfig) extends Bundle{
    val mask = Vec(conf.NVDLA_MAC_ATOMIC_K_SIZE_DIV2, Bool())
    val data = Vec(conf.NVDLA_MAC_ATOMIC_K_SIZE_DIV2, UInt(conf.NVDLA_MAC_RESULT_WIDTH.W))
    val mode = Bool()
    val batch_index = UInt(5.W)
    val stripe_st = Bool()
    val stripe_end = Bool()
    val channel_end = Bool()
    val layer_end = Bool()
}

// flow valid_ready
class cacc2sdp_if(implicit val conf: nvdlaConfig) extends Bundle{
    val pd = Vec(NVDLA_SDP_MAX_THROUGHPUT, UInt(conf.NVDLA_CACC_SDP_SINGLE_THROUGHPUT.W))
    val pd_batch_end = Bool()
    val pd_layer_end = Bool()
}

//  flow valid_ready
class sdp2pdp_if(implicit val conf: nvdlaConfig) extends Bundle{
    val pd = UInt((conf.NVDLA_SDP_MAX_THROUGHPUT * conf.NVDLA_BPE).W)
}

//  flow valid_ready
class nvdla_dma_rd_req(implicit val conf: nvdlaConfig) extends Bundle{
    val addr = UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W)
    val size = UInt(conf.NVDLA_DMA_RD_SIZE.W)
}

//  flow valid_ready
class nvdla_dma_rd_rsp(implicit val conf: nvdlaConfig) extends Bundle{
    val data = UInt(conf.NVDLA_MEMIF_WIDTH.W)
    val mask = UInt(conf.NVDLA_DMA_MASK_BIT.W)
}

//  flow valid_ready
class nvdla_dma_wr_req(implicit val conf: nvdlaConfig) extends Bundle{
    val addr = UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W)
    val size = UInt(conf.NVDLA_DMA_WR_SIZE.W)
    val data = UInt(conf.NVDLA_MEMIF_WIDTH.W)
    val mask = UInt(conf.NVDLA_DMA_MASK_BIT.W)
}

class nvdla_dma_wr_rsp(implicit val conf: nvdlaConfig) extends Bundle{
    val complete = Bool()
}











