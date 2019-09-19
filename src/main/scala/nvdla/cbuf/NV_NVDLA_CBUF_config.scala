 package nvdla

 import chisel3._


 class cbufConfiguration extends cmacConfiguration{

     val CBUF_ENTRY_WIDTH = NVDLA_CBUF_ENTRY_WIDTH
     val CBUF_ENTRY_BYTE = CBUF_ENTRY_WIDTH/8
     var CBUF_RAM_DEPTH = NVDLA_CBUF_BANK_DEPTH
     val CBUF_BANK_DEPTH_BITS = NVDLA_CBUF_BANK_DEPTH_LOG2  //log2(bank_depth), how many bits need to give an address in BANK
     val CBUF_RD_DATA_SHIFT_WIDTH =  NVDLA_CBUF_WIDTH_MUL2_LOG2  //log2(ram_width*2),width of data shift
     val CBUF_ADDR_WIDTH  =  NVDLA_CBUF_DEPTH_LOG2       //log2(bank_depth*bank_num)for both read and write
     val CBUF_RD_PORT_WIDTH = NVDLA_CBUF_ENTRY_WIDTH
     val CBUF_WR_PORT_NUMBER = 2   //how many write ports.
     val CSC_IMAGE_MAX_STRIDE_BYTE = 32  //=stride_max*

     var CBUF_BANK_RAM_CASE = 1

     if((NVDLA_CC_ATOMC_DIV_ATOMK==1)& (CBUF_ENTRY_BYTE >= CSC_IMAGE_MAX_STRIDE_BYTE)){
         CBUF_BANK_RAM_CASE = 0
     }
     else if((NVDLA_CC_ATOMC_DIV_ATOMK==1)& (CBUF_ENTRY_BYTE < CSC_IMAGE_MAX_STRIDE_BYTE)){
         CBUF_BANK_RAM_CASE = 1
     }
     else if((NVDLA_CC_ATOMC_DIV_ATOMK==2)& (CBUF_ENTRY_BYTE >= CSC_IMAGE_MAX_STRIDE_BYTE)){
         CBUF_BANK_RAM_CASE = 2
     }
     else if((NVDLA_CC_ATOMC_DIV_ATOMK==2)& (CBUF_ENTRY_BYTE < CSC_IMAGE_MAX_STRIDE_BYTE)){
         CBUF_BANK_RAM_CASE = 3
     }
     else if((NVDLA_CC_ATOMC_DIV_ATOMK==4)& (CBUF_ENTRY_BYTE >= CSC_IMAGE_MAX_STRIDE_BYTE)){
         CBUF_BANK_RAM_CASE = 4
     }
     else if((NVDLA_CC_ATOMC_DIV_ATOMK==4)& (CBUF_ENTRY_BYTE < CSC_IMAGE_MAX_STRIDE_BYTE)){
         CBUF_BANK_RAM_CASE = 5
     }
   

     //ram case could be 0/1/2/3/4/5  0:1ram/bank; 1:1*2ram/bank; 2:2*1ram/bank; 3:2*2ram/bank  4:4*1ram/bank  5:4*2ram/bank

     var CBUF_RAM_PER_BANK = 1
     var CBUF_WR_BANK_SEL_WIDTH = 1
     var CBUF_RAM_WIDTH = NVDLA_CBUF_ENTRY_WIDTH
     var CBUF_RAM_DEPTH_BITS = 1

     if(CBUF_BANK_RAM_CASE==0){
         CBUF_RAM_PER_BANK = 1
         CBUF_WR_BANK_SEL_WIDTH = 1
         CBUF_RAM_WIDTH = NVDLA_CBUF_ENTRY_WIDTH
         CBUF_RAM_DEPTH = NVDLA_CBUF_BANK_DEPTH
         CBUF_RAM_DEPTH_BITS = CBUF_BANK_DEPTH_BITS
     }
     else if(CBUF_BANK_RAM_CASE==1){
         CBUF_RAM_PER_BANK = 2
         CBUF_WR_BANK_SEL_WIDTH = 1
         CBUF_RAM_WIDTH = NVDLA_CBUF_ENTRY_WIDTH
         CBUF_RAM_DEPTH = NVDLA_CBUF_BANK_DEPTH/2
         CBUF_RAM_DEPTH_BITS = CBUF_BANK_DEPTH_BITS-1
     }
     else if(CBUF_BANK_RAM_CASE==2){
         CBUF_RAM_PER_BANK = 2
         CBUF_WR_BANK_SEL_WIDTH = 2
         CBUF_RAM_WIDTH = NVDLA_CBUF_ENTRY_WIDTH/2
         CBUF_RAM_DEPTH = NVDLA_CBUF_BANK_DEPTH
         CBUF_RAM_DEPTH_BITS = CBUF_BANK_DEPTH_BITS
     }
     else if(CBUF_BANK_RAM_CASE==3){
         CBUF_RAM_PER_BANK = 4
         CBUF_WR_BANK_SEL_WIDTH = 2
         CBUF_RAM_WIDTH = NVDLA_CBUF_ENTRY_WIDTH/2
         CBUF_RAM_DEPTH = NVDLA_CBUF_BANK_DEPTH/2
         CBUF_RAM_DEPTH_BITS = CBUF_BANK_DEPTH_BITS-1
     }
     else if(CBUF_BANK_RAM_CASE==4){
         CBUF_RAM_PER_BANK = 4
         CBUF_WR_BANK_SEL_WIDTH = 4
         CBUF_RAM_WIDTH = NVDLA_CBUF_ENTRY_WIDTH/4
         CBUF_RAM_DEPTH = NVDLA_CBUF_BANK_DEPTH
         CBUF_RAM_DEPTH_BITS = CBUF_BANK_DEPTH_BITS
     }
     else if(CBUF_BANK_RAM_CASE==5){
         CBUF_RAM_PER_BANK = 8
         CBUF_WR_BANK_SEL_WIDTH = 4
         CBUF_RAM_WIDTH = NVDLA_CBUF_ENTRY_WIDTH/4
         CBUF_RAM_DEPTH = NVDLA_CBUF_BANK_DEPTH/2
         CBUF_RAM_DEPTH_BITS = CBUF_BANK_DEPTH_BITS-1
     }
     val CBUF_WR_PORT_WIDTH = CBUF_RAM_WIDTH

     var CBUF_BANK_SLICE_max = 9
     var CBUF_BANK_SLICE_min = 9

     if(CBUF_BANK_NUMBER == 2){
         if(CBUF_BANK_DEPTH == 512){
             CBUF_BANK_SLICE_max = 9
             CBUF_BANK_SLICE_min = 9
         }
         else if(CBUF_BANK_DEPTH == 256){
             CBUF_BANK_SLICE_max = 8
             CBUF_BANK_SLICE_min = 8
         }
         else if(CBUF_BANK_DEPTH == 128){
             CBUF_BANK_SLICE_max = 7
             CBUF_BANK_SLICE_min = 7
         }
         else if(CBUF_BANK_DEPTH == 64){
             CBUF_BANK_SLICE_max = 6
             CBUF_BANK_SLICE_min = 6
         }
     }
     else if(CBUF_BANK_NUMBER==4){
         if(CBUF_BANK_DEPTH == 512){
             CBUF_BANK_SLICE_max = 10
             CBUF_BANK_SLICE_min = 9
         }
         else if(CBUF_BANK_DEPTH == 256){
             CBUF_BANK_SLICE_max = 9
             CBUF_BANK_SLICE_min = 8
         }
         else if(CBUF_BANK_DEPTH == 128){
             CBUF_BANK_SLICE_max = 8
             CBUF_BANK_SLICE_min = 7
         }
         else if(CBUF_BANK_DEPTH == 64){
             CBUF_BANK_SLICE_max = 7
             CBUF_BANK_SLICE_min = 6
         }
     }
     else if(CBUF_BANK_NUMBER==8){
         if(CBUF_BANK_DEPTH == 512){
             CBUF_BANK_SLICE_max = 11
             CBUF_BANK_SLICE_min = 9
         }
         else if(CBUF_BANK_DEPTH == 256){
             CBUF_BANK_SLICE_max = 10
             CBUF_BANK_SLICE_min = 8
         }
         else if(CBUF_BANK_DEPTH == 128){
             CBUF_BANK_SLICE_max = 9
             CBUF_BANK_SLICE_min = 7
         }
         else if(CBUF_BANK_DEPTH == 64){
             CBUF_BANK_SLICE_max = 8
             CBUF_BANK_SLICE_min = 6
         }
     }
     else if(CBUF_BANK_NUMBER==16){
         if(CBUF_BANK_DEPTH == 512){
             CBUF_BANK_SLICE_max = 12
             CBUF_BANK_SLICE_min = 9
         }
         else if(CBUF_BANK_DEPTH == 256){
             CBUF_BANK_SLICE_max = 11
             CBUF_BANK_SLICE_min = 8
         }
         else if(CBUF_BANK_DEPTH == 128){
             CBUF_BANK_SLICE_max = 10
             CBUF_BANK_SLICE_min = 7
         }
         else if(CBUF_BANK_DEPTH == 64){
             CBUF_BANK_SLICE_max = 9
             CBUF_BANK_SLICE_min = 6
         }
     }
     else if(CBUF_BANK_NUMBER==32){
         if(CBUF_BANK_DEPTH == 512){
             CBUF_BANK_SLICE_max = 13
             CBUF_BANK_SLICE_min = 9
         }
         else if(CBUF_BANK_DEPTH == 256){
             CBUF_BANK_SLICE_max = 12
             CBUF_BANK_SLICE_min = 8
         }
         else if(CBUF_BANK_DEPTH == 128){
             CBUF_BANK_SLICE_max = 11
             CBUF_BANK_SLICE_min = 7
         }
         else if(CBUF_BANK_DEPTH == 64){
             CBUF_BANK_SLICE_max = 10
             CBUF_BANK_SLICE_min = 6
         }
     }


 }
