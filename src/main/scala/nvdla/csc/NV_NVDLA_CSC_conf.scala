package nvdla

import chisel3._
import chisel3.util._


class cscConfiguration(){


    val NVDLA_CBUF_BANK_NUMBER = 32
    val NVDLA_CBUF_BANK_DEPTH = 512
    val NVDLA_CBUF_ENTRY_WIDTH = 8*8
    val NVDLA_CBUF_BANK_DEPTH_LOG2 = 9
    val NVDLA_CBUF_WIDTH_MUL2_LOG2 = 4
    val NVDLA_CBUF_DEPTH_LOG2 = 14
    val NVDLA_CC_ATOMC_DIV_ATOMK = 1
    val NVDLA_MAC_ATOMIC_C_SIZE = 8
    val NVDLA_MAC_ATOMIC_K_SIZE = 8
    val CSC_TYPE = SInt
    val NVDLA_BPE = 8
    val CBUF_RD_DATA_SHIFT_WIDTH = 4
    val NVDLA_BATCH_ENABLE = false
    val NVDLA_WINOGRAD_ENABLE = false


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
    val LOG2_ATOMC = log2Ceil(NVDLA_MAC_ATOMIC_C_SIZE)           //log2(atomC)
    val LOG2_ATOMK = log2Ceil(NVDLA_MAC_ATOMIC_C_SIZE)            //log2(atomK)
    val LOG2_CBUF_BANK_DEPTH = NVDLA_CBUF_BANK_DEPTH_LOG2              //log2(bank_depth)
    val CBUF_ADDR_WIDTH = NVDLA_CBUF_DEPTH_LOG2                   //log2(bank_num*bank_depth)
    val LOG2_BANK_NUM = 5            //log2(bank_num)
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
    val CSC_SG_DONE_FLUSH = "h30"
    val CSC_SG_PEND_FLUSH = "h20"

    //entry bits
    var CSC_WMB_ELEMENTS = "h200"
    //atomC
    var CSC_WT_ELEMENTS = "h40"
    //in bytes, entry/8
    var CSC_ENTRY_HEX = "h40"  
    //CSC_ENTRY_HEX/2
    var CSC_HALF_ENTRY_HEX = "h20"
    //CSC_ENTRY_HEX/4
    var CSC_QUAT_ENTRY_HEX = "h10"
    //CSC_ENTRY_HEX-1
    var CSC_ENTRY_MINUS1_HEX = "h3f"
    var CSC_ENTRY_HEX_MUL2 = "h80"
    
    var CSC_ATOMC_HEX  = "h40"


    if(NVDLA_MAC_ATOMIC_C_SIZE==64){
        //entry bits
        CSC_WMB_ELEMENTS = "h200"
        //atomC
        CSC_WT_ELEMENTS = "h40"
        //in bytes, entry/8
        CSC_ENTRY_HEX = "h40"
        //CSC_ENTRY_HEX/2
        CSC_HALF_ENTRY_HEX  = "h20"
        //CSC_ENTRY_HEX/4  
        CSC_QUAT_ENTRY_HEX = "h10" 
        //CSC_ENTRY_HEX-1   
        CSC_ENTRY_MINUS1_HEX = "h3f"
        CSC_ENTRY_HEX_MUL2 = "h80"

        CSC_ATOMC_HEX = "h40"
    }
    else if(NVDLA_MAC_ATOMIC_C_SIZE==32){
        //entry bits
        CSC_WMB_ELEMENTS = "h100"
        //atomC
        CSC_WT_ELEMENTS = "h20"
        //in bytes, entry/8
        CSC_ENTRY_HEX = "h20"
        //CSC_ENTRY_HEX/2
        CSC_HALF_ENTRY_HEX  = "h10"
        //CSC_ENTRY_HEX/4  
        CSC_QUAT_ENTRY_HEX = "h8" 
        //CSC_ENTRY_HEX-1   
        CSC_ENTRY_MINUS1_HEX = "h1f"
        CSC_ENTRY_HEX_MUL2 = "h40"
        
        CSC_ATOMC_HEX = "h20"

    }
    else if(NVDLA_MAC_ATOMIC_C_SIZE==8){
        //entry bits
        CSC_WMB_ELEMENTS = "h40"
        //atomC
        CSC_WT_ELEMENTS = "h8"
        //in bytes, entry/8
        CSC_ENTRY_HEX = "h08"
        //CSC_ENTRY_HEX/2
        CSC_HALF_ENTRY_HEX  = "h04"
        //CSC_ENTRY_HEX/4  
        CSC_QUAT_ENTRY_HEX = "h2" 
        //CSC_ENTRY_HEX-1   
        CSC_ENTRY_MINUS1_HEX = "h07"
        CSC_ENTRY_HEX_MUL2 = "h10"
        
        CSC_ATOMC_HEX = "h08"

    }

    //atomK
    var CSC_MIN_STRIPE = "d32"
    //atomK
    var CSC_ATOMK_HEX = "h20"
    //atomK*2
    var CSC_ATOMK_MUL2_HEX  = "h40"
    //atomK*4
    var CSC_ATOMK_MUL4_HEX = "h40"

    if(NVDLA_MAC_ATOMIC_K_SIZE==32){
        //atomK
        CSC_MIN_STRIPE = "d32"
        //atomK
        CSC_ATOMK_HEX = "h20"
        //atomK*2
        CSC_ATOMK_MUL2_HEX  = "h40"
    }
    else if(NVDLA_MAC_ATOMIC_K_SIZE==16){
        //atomK
        CSC_MIN_STRIPE = "d16"
        //atomK
        CSC_ATOMK_HEX = "h10"
        //atomK*2
        CSC_ATOMK_MUL2_HEX  = "h20"
        //atomK*4
        CSC_ATOMK_MUL4_HEX = "h40"
    }
    else if(NVDLA_MAC_ATOMIC_K_SIZE==8){
        //atomK
        CSC_MIN_STRIPE = "d16"
        //atomK
        CSC_ATOMK_HEX = "h10"
        //atomK*2
        CSC_ATOMK_MUL2_HEX  = "h20"
        //atomK*4
        CSC_ATOMK_MUL4_HEX = "h40"
    }

    //notice, for image case, first atom OP within one strip OP must fetch from entry align place, in the middle of an entry is not supported.
    //thus, when atomC/atomK=4, stripe=4*atomK, feature data still keeps atomK*2

    var CSC_IMG_STRIPE = CSC_ATOMK_MUL2_HEX
    var NVDLA_CC_CREDIT_SIZE = CSC_ATOMK*2

    if(NVDLA_CC_ATOMC_DIV_ATOMK==1){
        CSC_IMG_STRIPE = CSC_ATOMK_MUL2_HEX
        NVDLA_CC_CREDIT_SIZE = CSC_ATOMK*2  
    }
    else if(NVDLA_CC_ATOMC_DIV_ATOMK==2){
        CSC_IMG_STRIPE = CSC_ATOMK_MUL2_HEX
        NVDLA_CC_CREDIT_SIZE = CSC_ATOMK*2
    } 
    else if(NVDLA_CC_ATOMC_DIV_ATOMK==4){
        CSC_IMG_STRIPE = CSC_ATOMK_MUL4_HEX
        NVDLA_CC_CREDIT_SIZE = CSC_ATOMK*4
    } 

    //batch keep 1
    val CSC_BATCH_STRIPE = "h1"

}

