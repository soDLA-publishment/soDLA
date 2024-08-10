## Configuration Calculations


### Overall Specifications
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

NVDLA_MEMIF_WIDTH is the data width of axi bus, which is 64 or 256(large). NVDLA_MEMORY_ATOMIC_SIZE is the size of one memory atomic operation. NVDLA_BPE is bandwidth-per-element, which is 8 for int8. So each of a transfer, you can send 8 elements or 32 elements(large)

MULTI_MASK is to consider the case when the memory data width is larger than the standard axi4 specifications. 
However, MULTI_MASK is defaulted to be 1

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

MEMORY_ATOMIC_SIZE is 8 in small configuration, this was derived from 64 bits data-width AXI4 bus, and you can only get 8 data in a row within each burst. 

PRIMARY_MEMIF_LATENCY is commonly get from the NOC team according to a consulting talk to Yuanzhi Hua. 


```
class project_spec extends nv_small_config
{
```


is the latency and throughput calculations:


CBUF_BANK* are the parameters for the sram banks on chip. 

NVDLA_MEM_ADDRESS_WIDTH is 32, this is the memory address width between nvdla and memory. 

NVDLA_PRIMARY_MEMIF_WIDTH is the data width of the main memory interface(called MCIF). NVDLA_SECONDARY_MEMIF_WIDTH is the data width of the second main memory interface(called CVIF). 

NVDLA_MEMIF_WIDTH is the maximum data width of mcif, cvif, and the maximum nvdla data processor bandwidths.

NVDLA_DMA_MASK_BIT is to consider how much nvdla memory operations needed to consume the data from cvif or mcif. So it is defined as NVDLA_MEMIF_WIDTH/ (NVDLA_BPE * NVDLA_MEMORY_ATOMIC_SIZE)

NVDLA_DMA_RD_REQ, as a read type, equals to the NVDLA_MEM_ADDRESS_WIDTH plus other infos like ltran, ftran, swizzle defined as axi4.

NVDLA_DMA_RD_RSP, as a read type, equals to the NVDLA_MEMIF_WIDTH plus the NVDLA_DMA_MASK_BIT, since nvdla needs to know which parts of the data, upper, or lower, is currently writing to the rdma.

NVDLA_DMA_WR_REQ, as a write type, equals to the NVDLA_MEMIF_WIDTH plus the NVDLA_DMA_MASK_BIT plus another one, since nvdla not only needs to know which parts of the data is writing, but also needs to know whether it is a dat or cmd type. If the first bit is one, write data, elif the first bit is zero, write cmd.

The same token, NVDLA_DMA_WR_CMD is the memory address plus the size plus one, one is to distinguish whether it is a dat or cmd type. 

*LOG2 parameters are to express the width of some parameters, for example, NVDLA_MEMORY_ATOMIC_LOG2, NVDLA_PRIMARY_MEMIF_WIDTH_LOG2.

*FIFO_DEPTH is the maximum fifo depth to carry all the data within one latency.

Down to the bottoms are FPGA related parameters, for example, SYNTHESIS in FPGA case will use the d-register rather than the given ram models, VLIB_BYPASS_POWER_CG(power gating option in the clock configurations) will be disabled. 

### MCIF Parameters

In nocif/NV_NVDLA_XXIF_config.scala(soDLA location) or vmod/include/NV_NVDLA_MCIF_define.vh

NVDLA_DMA_RD_IG_PW and NVDLA_DMA_WR_IG_PW are related to the internal format, they are all ingress request, needs to provide the memory adress and other infos

In the read ingress, corresponds to

//ftran(1 bit), ltran(1 bit), out_odd(1 bit), out_swizzle(1 bit), out_size(3 bit) bpt2arb(address_width), axid(4 bit)

In the write ingress, corresponds to 

// ftran(1 bit), ltran(1 bit), inc(1 bit), odd(1 bit), swizzle(1 bit), size(3 bit), addr(address_width), require_ack(1 bit), axid(4 bit)

In the tieoff_*,

each of the processor is given a special number, or axi-id, since cpu/memory needs to know the sender, and axi-id is used during the comparison in the exgress stage(a detailed behavior will mentioned later).

###































