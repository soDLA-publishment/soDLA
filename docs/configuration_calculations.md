## Configuration Calculations

```
class nvdlaConfig extends xxifConfiguration{

    val NVDLA_DMAIF_BW = NVDLA_MEMIF_WIDTH  

    val MULTI_MASK = (NVDLA_DMAIF_BW/NVDLA_BPE/NVDLA_MEMORY_ATOMIC_SIZE)

    val NVDLA_MEM_MASK_BIT = MULTI_MASK

    val NVDLA_MEM_RD_RSP  = ( NVDLA_DMAIF_BW + NVDLA_MEM_MASK_BIT )
    val NVDLA_MEM_WR_REQ  = ( NVDLA_DMAIF_BW + NVDLA_MEM_MASK_BIT + 1 )
    val NVDLA_MEM_RD_REQ  = ( NVDLA_MEM_ADDRESS_WIDTH + 15 )

}
```

NVDLA_MEMIF_WIDTH is the data width of axi bus, which is 64. NVDLA_MEMORY_ATOMIC_SIZE is the size of one memory atomic operation. NVDLA_BPE is bandwidth-per-element, which is 8 for int8. 

NVDLA_MEM_RD_REQ basicly need an address some additional infos, here is the data formats in microarchitectures:

ftran(1 bit), ltran(1 bit), out_odd(1 bit), out_swizzle(1 bit), out_size(3 bit), bpt2arb(address_width), axid(4 bit)

other bits include the dirty, mask. 


```
class nv_small_config
{
  val FEATURE_DATA_TYPE_INT8 = true
  val WEIGHT_DATA_TYPE_INT8 = true
  val WEIGHT_COMPRESSION_ENABLE = false
  val WINOGRAD_ENABLE = false
  val BATCH_ENABLE = false
  val SECONDARY_MEMIF_ENABLE = false
  val SDP_LUT_ENABLE = false
  val SDP_BS_ENABLE = true
  val SDP_BN_ENABLE = true
  val SDP_EW_ENABLE = false
  val BDMA_ENABLE = false
  val RUBIK_ENABLE = false
  val RUBIK_CONTRACT_ENABLE = false
  val RUBIK_RESHAPE_ENABLE = false
  val PDP_ENABLE = true
  val CDP_ENABLE = true
  val RETIMING_ENABLE = false
  val MAC_ATOMIC_C_SIZE = 8 
  val MAC_ATOMIC_K_SIZE = 8
  val MEMORY_ATOMIC_SIZE = 8
  val MAX_BATCH_SIZE = 0
  val CBUF_BANK_NUMBER = 32
  val CBUF_BANK_WIDTH = 8
  val CBUF_BANK_DEPTH = 512
  val SDP_BS_THROUGHPUT = 1
  val SDP_BN_THROUGHPUT = 1
  val SDP_EW_THROUGHPUT = 0
  val PDP_THROUGHPUT = 1
  val CDP_THROUGHPUT = 1
  val PRIMARY_MEMIF_LATENCY = 64
  val SECONDARY_MEMIF_LATENCY = 0
  val PRIMARY_MEMIF_MAX_BURST_LENGTH = 1
  val PRIMARY_MEMIF_WIDTH = 64
  val SECONDARY_MEMIF_MAX_BURST_LENGTH = 0
  val SECONDARY_MEMIF_WIDTH = 0
  val MEM_ADDRESS_WIDTH = 32
}
```

A Convolution is 3-layer of mac operations. The traditional format is C'WHC, C' mean kernel size of weight, WHC is a 3-dimentional data cube, after a convolution, the output dimention is WHC'. The original C, or channel c, is reduced in the channel operation which will mentioned later. 

Each data from WHC cube is consists of sum of Channel[Sum of Stripe[multiply and accumulate]]

MAC_ATOMIC_C_SIZE is the amount of data participated in the atomic operations within a stripe in the input data cube. The ATOMIC_C has nothing related to the channel c in the input data cube(If ATOMIC_C is related to the channel c, then in the CACC stage, there shouldn't be an indicator signal channel_end). CMAC stage from microarchitecture perspective is K lanes of mac(or fma, multiply-and-accumlate), with a function of caching weight from last stages to save energy. MAC size of each lane is the MAC_ATOMIC_C_SIZE, so we can say that the C is reduced in the operation. 

MAC_ATOMIC_K_SIZE is the amount of weight kernels or kernel group size, each of the kernel is individual. In the data format of C'WHC, C' means the kernel size. 

MEMORY_ATOMIC_SIZE is 8 in small configuration, I can only guess this was derived from 64 bits data-width AXI4 bus, and you can only get 8 data in a row within each burst. However, you can only get maximum of 1 burst in the sodla small setting. 

PRIMARY_MEMIF_LATENCY is commonly get from the NOC team according to a consulting talk to Yuanzhi Hua. 

CBUF_BANK* are the parameters for the sram banks on chip. 


```
class project_spec extends nv_small_config
{
```


is the latency and throughput calculations, *FIFO_DEPTH is the maximum fifo depth to carry all the data within one latency. 















