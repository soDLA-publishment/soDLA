 package nvdla

 import chisel3._
 import chisel3.experimental._
 import chisel3.util._

 class caccConfiguration extends cscConfiguration
 {
     val CACC_PARSUM_WIDTH = 34  //sum result width for one layer operation.
     val CACC_FINAL_WIDTH = 32  //sum result width for one layer operation with saturaton.
     val CACC_IN_WIDTH = NVDLA_MAC_RESULT_WIDTH  //16+log2(atomC),sum result width for one atomic operation.
     val SDP_MAX_THROUGHPUT = NVDLA_SDP_MAX_THROUGHPUT  //2^n, no bigger than atomM
     val CACC_ATOMK = NVDLA_MAC_ATOMIC_K_SIZE
     val CACC_ATOMK_LOG2 = NVDLA_MAC_ATOMIC_K_SIZE_LOG2
     val CACC_ABUF_WIDTH = CACC_PARSUM_WIDTH*CACC_ATOMK
     val CACC_DBUF_WIDTH = CACC_FINAL_WIDTH*CACC_ATOMK
     val CACC_SDP_DATA_WIDTH = CACC_FINAL_WIDTH*SDP_MAX_THROUGHPUT
     val CACC_SDP_WIDTH = CACC_SDP_DATA_WIDTH+2    //cacc to sdp pd width
     val CACC_DWIDTH_DIV_SWIDTH = (CACC_DBUF_WIDTH)/(CACC_SDP_DATA_WIDTH)  //1,2,4...
     val CACC_CELL_PARTIAL_LATENCY = 2
     val CACC_CELL_FINAL_LATENCY = 2
     val CACC_D_RAM_WRITE_LATENCY = 1

     var CACC_ABUF_DEPTH = NVDLA_MAC_ATOMIC_K_SIZE*2  //2*atomK
     var CACC_ABUF_AWIDTH  = NVDLA_MAC_ATOMIC_K_SIZE_LOG2+1   //log2(abuf_depth)

     if(NVDLA_CC_ATOMC_DIV_ATOMK==1){
         CACC_ABUF_DEPTH =  NVDLA_MAC_ATOMIC_K_SIZE*2  //2*atomK
         CACC_ABUF_AWIDTH = NVDLA_MAC_ATOMIC_K_SIZE_LOG2+1   //log2(abuf_depth)
     }
     else if(NVDLA_CC_ATOMC_DIV_ATOMK==2){
         CACC_ABUF_DEPTH =  NVDLA_MAC_ATOMIC_K_SIZE*2  //2*atomK
         CACC_ABUF_AWIDTH = NVDLA_MAC_ATOMIC_K_SIZE_LOG2+1   //log2(abuf_depth)
     }
     else if(NVDLA_CC_ATOMC_DIV_ATOMK==4){
         CACC_ABUF_DEPTH =  NVDLA_MAC_ATOMIC_K_SIZE*4  //4*atomK,under image, stripe OP must begin from entry align place
         CACC_ABUF_AWIDTH = NVDLA_MAC_ATOMIC_K_SIZE_LOG2+2   //log2(abuf_depth)
     }

     val CACC_DBUF_DEPTH = CACC_ABUF_DEPTH
     val CACC_DBUF_AWIDTH = CACC_ABUF_AWIDTH    //address width
 }

 class cacc_dual_reg_flop_outputs extends Bundle{
     val batches = Output(UInt(5.W))
     val clip_truncate = Output(UInt(5.W))
     val cya = Output(UInt(32.W))
     val dataout_addr = Output(UInt(32.W))
     val line_packed = Output(Bool())
     val surf_packed = Output(Bool())
     val dataout_height = Output(UInt(13.W))
     val dataout_width = Output(UInt(13.W))
     val dataout_channel = Output(UInt(13.W))
     val line_stride = Output(UInt(24.W))
     val conv_mode = Output(Bool())
     val proc_precision = Output(UInt(2.W))
     val surf_stride = Output(UInt(24.W))
 }
