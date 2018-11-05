package common



{

case class cbufConfiguration(){
    val CBUF_BANK_NUMBER = NVDLA_CBUF_BANK_NUMBER
    val CBUF_BANK_DEPTH = NVDLA_CBUF_BANK_DEPTH
    val CBUF_ENTRY_WIDTH = NVDLA_CBUF_ENTRY_WIDTH
    val CBUF_ENTRY_BYTE = CBUF_ENTRY_WIDTH/8
    val CBUF_RAM_DEPTH = NVDLA_CBUF_BANK_DEPTH 
    val CBUF_BANK_DEPTH_BIT = log2Ceil(CBUF_BANK_DEPTH)  //log2(bank_depth), how many bits need to give an address in BANK
    val CBUF_RD_DATA_SHIFT_WIDTH = log2Ceil(CBUF_BANK_DEPTH)  //log2(ram_width*2),width of data shift
    val CBUF_ADDR_WIDTH             NVDLA_CBUF_DEPTH_LOG2       //log2(bank_depth*bank_num)for both read and write
    val CBUF_RD_PORT_WIDTH = CBUF_ENTRY_WIDTH
    val CBUF_WR_PORT_NUMBER = 2   //how many write ports.
    val CSC_IMAGE_MAX_STRIDE_BYTE = 32  //=stride_max* 
}

}