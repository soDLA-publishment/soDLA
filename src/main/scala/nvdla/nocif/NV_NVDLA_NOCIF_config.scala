package nvdla


class nocifConfiguration extends project_spec{

  val RDMA_MAX_NUM = 10
  val WDMA_MAX_NUM = 5
  val WDMA_NUM = 8
  val RDMA_NUM = 5
  val FV_RAND_WR_PAUSE = false

  val NVDLA_DMA_RD_IG_PW = NVDLA_MEM_ADDRESS_WIDTH+11
  val NVDLA_DMA_WR_IG_PW = NVDLA_MEM_ADDRESS_WIDTH+13

  val NVDLA_PRIMARY_MEMIF_STRB = NVDLA_MEMORY_ATOMIC_SIZE
}

