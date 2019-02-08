package nvdla

import chisel3._


class cscConfiguration(){
    
    val NVDLA_CBUF_BANK_NUMBER = 32
    val NVDLA_CBUF_BANK_DEPTH = 512
    val NVDLA_CBUF_ENTRY_WIDTH = 8
    val NVDLA_CBUF_BANK_DEPTH_LOG2 = 9
    val NVDLA_CBUF_WIDTH_MUL2_LOG2 = 4
    val NVDLA_CBUF_DEPTH_LOG2 = 14
    val NVDLA_CC_ATOMC_DIV_ATOMK = 1

    val CSC_ATOMC = NVDLA_MAC_ATOMIC_C_SIZE
    val CSC_ATOMK = NVDLA_MAC_ATOMIC_K_SIZE
    val CBUF_BANK_NUM = NVDLA_CBUF_BANK_NUMBER
    val CBUF_BANK_DEPTH = NVDLA_CBUF_BANK_DEPTH
    val CSC_BPE = NVDLA_BPE
    val CBUF_ENTRY_BITS = NVDLA_CBUF_ENTRY_WIDTH
    val CSC_ATOMK_HF = CSC_ATOMK/2
    val CSC_TWICE_ENTRY_BITS = CBUF_ENTRY_BITS*2         //entry*2
    val CSC_ENTRY_BITS = CBUF_ENTRY_BITS   //entry
    val CSC_HALF_ENTRY_BITS = CBUF_ENTRY_BITS/2          //entry/2
    val CSC_QUAT_ENTRY_BITS = CBUF_ENTRY_BITS/4          //entry/4
    val CSC_3QUAT_ENTRY_BITS = CBUF_ENTRY_BITS*3/4        //entry*3/4
    val CSC_ATOMC_HALF = CSC_ATOMC/2           //atomC/2
    val CSC_ATOMC_QUAT = CSC_ATOMC/4           //atomC/4
    val LOG2_ATOMC = NVDLA_MAC_ATOMIC_C_SIZE_LOG2           //log2(atomC)
    val LOG2_ATOMK = NVDLA_MAC_ATOMIC_K_SIZE_LOG2           //log2(atomK)
    val LOG2_CBUF_BANK_DEPTH = NVDLA_CBUF_BANK_DEPTH_LOG2              //log2(bank_depth)
    val CBUF_ADDR_WIDTH = NVDLA_CBUF_DEPTH_LOG2                   //log2(bank_num*bank_depth)
    val LOG2_BANK_NUM = NVDLA_CBUF_BANK_NUMBER_LOG2             //log2(bank_num)
    val NVDLA_VMOD_CBUF_WRITE_LATENCY = 3
    val NVDLA_VMOD_CBUF_READ_LATENCY = 6
    val NVDLA_HLS_CSC_PRA_LATENCY = 5
    val NVDLA_CBUF_READ_LATENCY = NVDLA_VMOD_CBUF_READ_LATENCY
    val NVDLA_MACCELL_NUMBER = CSC_ATOMK
    val CSC_DL_PRA_LATENCY = NVDLA_HLS_CSC_PRA_LATENCY
    val CSC_WL_LATENCY = 4
    val RT_CSC2CMAC_A_LATENCY = 2
    val RT_CSC2CMAC_B_LATENCY = 1
    val CSC_ENTRIES_NUM_WIDTH = 15

    var CSC_WL_PIPELINE_ADDITION = 0 
    var CSC_DL_PIPELINE_ADDITION = 0

    if(CSC_WL_LATENCY >= CSC_DL_PRA_LATENCY){
        CSC_DL_PIPELINE_ADDITION = CSC_WL_LATENCY-CSC_DL_PRA_LATENCY
        CSC_WL_PIPELINE_ADDITION = 0
    }
    else{
        CSC_DL_PIPELINE_ADDITION = 0
        CSC_WL_PIPELINE_ADDITION = CSC_DL_PRA_LATENCY-CSC_WL_LATENCY
    }
    val CSC_SG_DONE_FLUSH = "h30".asUInt(6.W)
    val CSC_SG_PEND_FLUSH = "h20".asUInt(6.W)

    //entry bits
    var CSC_WMB_ELEMENTS = "h200".asUInt(11.W)
    //atomC
    var CSC_WT_ELEMENTS = "h40"
    //in bytes, entry/8
    var CSC_ENTRY_HEX                                       8'h40  
    //CSC_ENTRY_HEX/2
    var CSC_HALF_ENTRY_HEX                                  8'h20
    //CSC_ENTRY_HEX/4
    var CSC_QUAT_ENTRY_HEX                                  8'h10
    //CSC_ENTRY_HEX-1
    var CSC_ENTRY_MINUS1_HEX                                8'h3f
    var CSC_ENTRY_HEX_MUL2                                  8'h80
    
    var CSC_ATOMC_HEX                                       7'h40
    var CSC_ATOMC_HEX_STR                                   "\"7'h40\""


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

