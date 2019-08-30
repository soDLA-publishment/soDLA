package nvdla


class xxifConfiguration extends project_spec{

  val RDMA_MAX_NUM = 10
  val WDMA_MAX_NUM = 5
  val WDMA_NUM = 8

  val FV_RAND_WR_PAUSE = false

  var RDMA_NUM = 0
  if(NVDLA_SDP_BS_ENABLE) {RDMA_NUM += 1}
  if(NVDLA_SDP_BN_ENABLE) {RDMA_NUM += 1}
  if(NVDLA_SDP_EW_ENABLE) {RDMA_NUM += 1}
  if(NVDLA_PDP_ENABLE)    {RDMA_NUM += 1}
  if(NVDLA_CDP_ENABLE)    {RDMA_NUM += 1}
  if(NVDLA_RUBIK_ENABLE)  {RDMA_NUM += 1}
  if(NVDLA_BDMA_ENABLE)   {RDMA_NUM += 1}

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

  var tieoff_axid_read = List(tieoff_axid_cdma_dat, tieoff_axid_cdma_wt, tieoff_axid_sdp)
  if(NVDLA_SDP_BS_ENABLE) {tieoff_axid_read :+ tieoff_axid_sdp_b}
  if(NVDLA_SDP_BN_ENABLE) {tieoff_axid_read :+ tieoff_axid_sdp_n}
  if(NVDLA_SDP_EW_ENABLE) {tieoff_axid_read :+ tieoff_axid_sdp_e}
  if(NVDLA_PDP_ENABLE)    {tieoff_axid_read :+ tieoff_axid_pdp}
  if(NVDLA_CDP_ENABLE)    {tieoff_axid_read :+ tieoff_axid_cdp}
  if(NVDLA_RUBIK_ENABLE)  {tieoff_axid_read :+ tieoff_axid_rbk}
  if(NVDLA_BDMA_ENABLE)   {tieoff_axid_read :+ tieoff_axid_sdp_b}

}

