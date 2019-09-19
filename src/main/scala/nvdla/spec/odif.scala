package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._


class cdma2buf_wr_if(implicit val conf: nvdlaConfig) extends Bundle{
    val en = Output(Vec(conf.CBUF_WR_PORT_NUMBER, Bool()))
    val sel = Output(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_BANK_SEL_WIDTH.W)))
    val addr = Output(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_ADDR_WIDTH.W)))
    val data = Output(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH.W)))
}

class sc2buf_data_rd_if(implicit val conf: nvdlaConfig)  extends Bundle{
    val addr = ValidIO(UInt(conf.CBUF_ADDR_WIDTH.W))
    val data = Flipped(ValidIO(UInt(conf.CBUF_RD_PORT_WIDTH.W)))
}

class sc2buf_wt_rd_if(implicit val conf: nvdlaConfig)  extends Bundle{
    val addr = ValidIO(UInt(conf.CBUF_ADDR_WIDTH.W))
    val data = Flipped(ValidIO(UInt(conf.CBUF_RD_PORT_WIDTH.W)))
}

// flow valid
class csc2cmac_data_if(implicit val conf: nvdlaConfig)  extends Bundle{
    val mask = Output(Vec(conf.NVDLA_MAC_ATOMIC_C_SIZE, Bool()))
    val data = Output(Vec(conf.NVDLA_MAC_ATOMIC_C_SIZE, UInt(conf.NVDLA_BPE.W)))
//pd
//   field batch_index 5
//   field stripe_st 1
//   field stripe_end 1
//   field channel_end 1
//   field layer_end 1
    val pd = Output(UInt(9.W))
}

//  flow valid
class csc2cmac_wt_if(implicit val conf: nvdlaConfig) extends Bundle{
    val sel = Output(Vec(conf.NVDLA_MAC_ATOMIC_K_SIZE_DIV2, Bool()))
    val mask = Output(Vec(conf.NVDLA_MAC_ATOMIC_C_SIZE, Bool()))
    val data = Output(Vec(conf.NVDLA_MAC_ATOMIC_C_SIZE, UInt(conf.NVDLA_BPE.W)))
}

//  flow valid
class cmac2cacc_if(implicit val conf: nvdlaConfig) extends Bundle{
    val mask = Output(Vec(conf.NVDLA_MAC_ATOMIC_K_SIZE_DIV2, Bool()))
    val data = Output(Vec(conf.NVDLA_MAC_ATOMIC_K_SIZE_DIV2, UInt(conf.NVDLA_MAC_RESULT_WIDTH.W)))
    //val mode = Output(Bool())
//pd
//   field batch_index 5
//   field stripe_st 1
//   field stripe_end 1
//   field channel_end 1
//   field layer_end 1
    val pd = Output(UInt(9.W))
}

// flow valid_ready
class cacc2sdp_if(implicit val conf: nvdlaConfig) extends Bundle{
//   field pd[NVDLA_SDP_MAX_THROUGHPUT]  NVDLA_CACC_SDP_SINGLE_THROUGHPUT
//   field pd_batch_end  1
//   field pd_layer_end  1
    val pd = Output(UInt((conf.NVDLA_SDP_MAX_THROUGHPUT*conf.NVDLA_CACC_SDP_SINGLE_THROUGHPUT+2).W))
}

//  flow valid_ready
class sdp2pdp_if(implicit val conf: nvdlaConfig) extends Bundle{
    val pd = Output(UInt((conf.NVDLA_SDP_MAX_THROUGHPUT * conf.NVDLA_BPE).W))
}

//  flow valid_ready
class nvdla_dma_rd_req_if(implicit val conf: nvdlaConfig) extends Bundle{
    val addr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    val size = Output(UInt(conf.NVDLA_DMA_RD_SIZE.W))
}

//  flow valid_ready
class nvdla_dma_rd_rsp_if(implicit val conf: nvdlaConfig) extends Bundle{
    val data = Output(UInt(conf.NVDLA_MEMIF_WIDTH.W))
    val mask = Output(UInt(conf.NVDLA_DMA_MASK_BIT.W))
}

//  flow valid_ready
class nvdla_dma_wr_req_if(implicit val conf: nvdlaConfig) extends Bundle{
    val addr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    val size = Output(UInt(conf.NVDLA_DMA_WR_SIZE.W))
    val data = Output(UInt(conf.NVDLA_MEMIF_WIDTH.W))
    val mask = Output(UInt(conf.NVDLA_DMA_MASK_BIT.W))
}

class nvdla_dma_wr_rsp_if(implicit val conf: nvdlaConfig) extends Bundle{
    val complete = Output(Bool())
}


class csb2dp_if extends Bundle{
    val req = Flipped(DecoupledIO(UInt(63.W)))
    val resp = ValidIO(UInt(34.W))
}

class nvdla_clock_if extends Bundle{
    val nvdla_core_clk = Output(Clock())
    val dla_clk_ovr_on_sync = Output(Clock())
    val global_clk_ovr_on_sync = Output(Clock())
    val tmc2slcg_disable_clock_gating = Output(Bool())
}

// Register control interface
class reg_control_if extends Bundle{
    val rd_data = Output(UInt(32.W))
    val offset = Input(UInt(12.W))
    val wr_data = Input(UInt(32.W))
    val wr_en = Input(Bool())
}

//

class cdma2sc_if(implicit val conf: nvdlaConfig) extends Bundle{
    val addr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    val size = Output(UInt(conf.NVDLA_DMA_WR_SIZE.W))
    val data = Output(UInt(conf.NVDLA_MEMIF_WIDTH.W))
    val mask = Output(UInt(conf.NVDLA_DMA_MASK_BIT.W))
}


class updt_entries_slices_if(implicit val conf: nvdlaConfig) extends Bundle{
    val entries = Output(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val slices = Output(UInt(14.W))
}

class updt_entries_kernels_if(implicit val conf: nvdlaConfig) extends Bundle{
    val entries = Output(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val kernels = Output(UInt(14.W))
}

class nvdla_wr_if(addr_width:Int, width:Int) extends Bundle{
    val addr = ValidIO(UInt(addr_width.W))
    val data = Output(UInt(width.W))

    override def cloneType: this.type =
    new nvdla_wr_if(addr_width:Int, width:Int).asInstanceOf[this.type]
}

class nvdla_rd_if(addr_width:Int, width:Int) extends Bundle{
    val addr = ValidIO(UInt(addr_width.W))
    val data = Input(UInt(width.W))

    override def cloneType: this.type =
    new nvdla_rd_if(addr_width:Int, width:Int).asInstanceOf[this.type]
}



















