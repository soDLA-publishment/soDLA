package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class xxifConfiguration extends cdpConfiguration{

  val FV_RAND_WR_PAUSE = false

  val NVDLA_DMA_RD_IG_PW = NVDLA_MEM_ADDRESS_WIDTH+11
  val NVDLA_DMA_WR_IG_PW = NVDLA_MEM_ADDRESS_WIDTH+13

  val NVDLA_PRIMARY_MEMIF_STRB = NVDLA_MEMORY_ATOMIC_SIZE

  val tieoff_axid_bdma = 0
  val tieoff_axid_sdp = 1
  val tieoff_axid_pdp = 2
  val tieoff_axid_cdp = 3
  val tieoff_axid_rbk = 4
  val tieoff_axid_sdp_b = 5
  val tieoff_axid_sdp_n = 6
  val tieoff_axid_sdp_e = 7
  val tieoff_axid_cdma_dat = 8
  val tieoff_axid_cdma_wt = 9

  var arr_tieoff_axid = List(tieoff_axid_cdma_dat, tieoff_axid_cdma_wt, tieoff_axid_sdp)
  var awr_tieoff_axid = List(tieoff_axid_sdp)

  val RDMA_MAX_NUM = 10
  val WDMA_MAX_NUM = 5

  if(NVDLA_SDP_BS_ENABLE){
      arr_tieoff_axid = arr_tieoff_axid :+ tieoff_axid_sdp_b
  }
  if(NVDLA_SDP_BN_ENABLE){
      arr_tieoff_axid = arr_tieoff_axid :+ tieoff_axid_sdp_n
  }
  if(NVDLA_SDP_EW_ENABLE){
      arr_tieoff_axid = arr_tieoff_axid :+ tieoff_axid_sdp_e
  }
  if(NVDLA_PDP_ENABLE){
      arr_tieoff_axid = arr_tieoff_axid :+ tieoff_axid_pdp
      awr_tieoff_axid = awr_tieoff_axid :+ tieoff_axid_pdp
  }
  if(NVDLA_CDP_ENABLE){
      arr_tieoff_axid = arr_tieoff_axid :+ tieoff_axid_cdp
      awr_tieoff_axid = awr_tieoff_axid :+ tieoff_axid_cdp
  }
  if(NVDLA_RUBIK_ENABLE){
      arr_tieoff_axid = arr_tieoff_axid :+ tieoff_axid_rbk
      awr_tieoff_axid = awr_tieoff_axid :+ tieoff_axid_rbk
  }
  if(NVDLA_BDMA_ENABLE){
      arr_tieoff_axid = arr_tieoff_axid :+ tieoff_axid_bdma
      awr_tieoff_axid = awr_tieoff_axid :+ tieoff_axid_bdma
  }

  val RDMA_NUM = arr_tieoff_axid.length
  val WDMA_NUM = awr_tieoff_axid.length

  var arr_tieoff_lat_fifo_depth = List(0, 0, 256)
  if(NVDLA_SDP_BS_ENABLE) {arr_tieoff_lat_fifo_depth = arr_tieoff_lat_fifo_depth :+ 256}
  if(NVDLA_SDP_BN_ENABLE) {arr_tieoff_lat_fifo_depth = arr_tieoff_lat_fifo_depth :+ 256}
  if(NVDLA_SDP_EW_ENABLE) {arr_tieoff_lat_fifo_depth = arr_tieoff_lat_fifo_depth :+ 256}
  if(NVDLA_PDP_ENABLE)    {arr_tieoff_lat_fifo_depth = arr_tieoff_lat_fifo_depth :+ 256}
  if(NVDLA_CDP_ENABLE)    {arr_tieoff_lat_fifo_depth = arr_tieoff_lat_fifo_depth :+ 256}
  if(NVDLA_RUBIK_ENABLE)  {arr_tieoff_lat_fifo_depth = arr_tieoff_lat_fifo_depth :+ 256}
  if(NVDLA_BDMA_ENABLE)   {arr_tieoff_lat_fifo_depth = arr_tieoff_lat_fifo_depth :+ 256}


  //redpanda3
  val MCIF_WRITE_CQ_WIDTH = 3
  val MCIF_WRITE_CQ_VEC_NUM = 5
}




class nocif_axi_wr_data_if(implicit val conf: nvdlaConfig) extends Bundle{
  val data = Output(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))
  val strb = Output(UInt((conf.NVDLA_PRIMARY_MEMIF_WIDTH/8).W))
  val last = Output(Bool())
}

class nocif_axi_wr_address_if(implicit val conf: nvdlaConfig) extends Bundle{
  val id = Output(UInt(8.W))
  val len = Output(UInt(4.W))
  val addr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
}

class nocif_axi_wr_response_if extends Bundle{
  val id = Output(UInt(8.W))
}

class nocif_axi_rd_address_if(implicit val conf: nvdlaConfig) extends Bundle{
  val id = Output(UInt(8.W))
  val len = Output(UInt(4.W))
  val addr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
}

class nocif_axi_rd_data_if(implicit val conf: nvdlaConfig) extends Bundle{
  val id = Output(UInt(8.W))
  val last = Output(Bool())
  val data = Output(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))
}


class mcif_reg_flop_outputs(implicit val conf: nvdlaConfig) extends Bundle{

  val rd_os_cnt = Output(UInt(8.W))
  val rd_weight_rsv_0 = Output(UInt(8.W))
  val rd_weight_rsv_1 = Output(UInt(8.W))
  val rd_weight_client = Output(Vec(conf.RDMA_MAX_NUM, UInt(8.W)))

  val wr_os_cnt = Output(UInt(8.W))
  val wr_weight_rsv_0 = Output(UInt(8.W))
  val wr_weight_rsv_1 = Output(UInt(8.W))
  val wr_weight_rsv_2 = Output(UInt(8.W))
  val wr_weight_client = Output(Vec(conf.WDMA_MAX_NUM, UInt(8.W)))
}