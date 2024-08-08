# MCIF Design


MCIF is a arbitration mechanism to arbit the axi4 request from data processors. The data processors include convolution processors, single data point processors, planar data point processors. Each of the data processors within nvdla are axi masters, and the slaves are other processors from NOC spaces. 

The following is a classic axi4 structure. About axi4 explanation, if you want to explore more, there is a more detailed video from Mr Dillon Huff(https://www.youtube.com/watch?v=1zw1HBsjDH8&t=10s, non-youtube-link https://www.bilibili.com/video/BV1Kh41127yN/?spm_id_from=333.337.search-card.all.click). Mr Dillon Huff is also an activate open-source maintainer. 

![image info](./imgs/axi4.png)


Each axi4 request has 5 channels, in the read category, there are two channels, 

master needs to provide the address(1st channel), 

master get the corresponding data(2nd channel). 

In the write category, there are 3 channels,

master will write the address(3rd channel),

master will get the corresponding data(4th channel),

then get back the responce from the axi slave(5th channel). 

So totally 5 channels. 

MCIF needs to separate those channels into different catagories in read channels and write channels, and then arbitrate them. Data processors, like cdma_dat(data channel from CDMA) and cdma_wt(weight channel from CDMA) only has read function. Other data processors, like SDP, has not only read, but also write. So separate and arbitrate those channels are MCIF's work. As for splitting the data and command, those are the works for WDMAs. 

Another terminology is ingress and exgress. Those are the words to describe the directions from CPU or memory side. 1st, 3rd, 4th channel are ingress. 2nd and 5th are exgress.



In the NV_NVDLA_XXIF_config.scala:

```
  val NVDLA_DMA_RD_IG_PW = NVDLA_MEM_ADDRESS_WIDTH+11
  val NVDLA_DMA_WR_IG_PW = NVDLA_MEM_ADDRESS_WIDTH+13
```

IG means ingress, in the read ingress, additional info are needed to provided. Below is the NVDLA_DMA_RD_IG_PW corresponding data. 

//ftran(1 bit), ltran(1 bit), out_odd(1 bit), out_swizzle(1 bit), out_size(3 bit) bpt2arb(address_width), axid(4 bit)

In the write ingress, additional info are needed to provided. Below is the NVDLA_DMA_WR_IG_PW corresponding data. 

// ftran(1 bit), ltran(1 bit), inc(1 bit), odd(1 bit), swizzle(1 bit), size(3 bit), addr(address_width), require_ack(1 bit), axid(4 bit)

ftran, ltran are first transitions and last transitions, they are functional. inc means incremental, has the same definition as axi4, is default to be 1 and cannot be modified. 
odd is the parity check info. Swizzle is always disabled. Size is the size of data to transfer. Require ack means want an acknowlegdge siganl from CPU after the last transfer. axi-id is the same as defined in axi4, it is a special number to recognize. Data processors, each of them, has an axi-id to connect to MCIF. 

If you want to customize your own data processor, one way is to add a small one, give it an axi-id, and connected to the MCIF. A benefit of it, is that you don't have to add another NOC endpoint for your own data processor. The disadvantage of might be occupying the bandwidth of the original nvdla. 

## Block Hierarchical Review

Within MCIF, there are three modules, u_csb, u_mcif_read, u_mcif_write. 

u_csb is a type of configuration register, different from the commonly used ping-pong register in NVDLA, u_csb has only one set of register, while ping-pong register requires two registers(in ping-pong, there are two groups, one is active, another is shadow, it means you can configure one register during the runtime, another group is still working). The reason of this might be MCIF is not a real data processor, if you go over the MCIF configurations 

```
NVDLA_MCIF_CFG_RD_WEIGHT_0_0

NVDLA_MCIF_CFG_RD_WEIGHT_1_0

NVDLA_MCIF_CFG_RD_WEIGHT_2_0
```

Those are the priorities for each of the data processors, and there is no op_en(operation enable) signal. Means the MCIF could be configured at the very begin stage. Those configurations will not be partipated in the real-time programming stages.


u_mcif_read includes read_ig and read_eg











