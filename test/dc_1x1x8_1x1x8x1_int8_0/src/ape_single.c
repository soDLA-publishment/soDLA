#include "ape_get_ali.h"
#include "ape_small_single.h"
#include <stdio.h>

#define APE_REG_ADDR 0x10040000
// #define APE_REG_ADDR 0x3000000
#define APE_REG_SIZE 0x40000

#define APE_RAM_ADDR 0x80050000
#define APE_RAM_SIZE 0x30000
//addr alloc
#define ALLOC_SIZE 0x100
#define WEIGHT_ADDR 0x80050100
// #define BIAS_ADDR  0x80057200
#define INPUT_ADDR  0x80050200
#define OUTPUT_ADDR 0x80050000

#define reg_write(addr,val) reg_write32(APE_REG_ADDR+addr,val)
#define reg_read(addr) reg_read32(APE_REG_ADDR+addr)

ScU32 mem_ptr[10]={};

ScU32 *ape_ptr;
ScU8 *ram_ptr;
void *ram_addr[24]={}; 

void reg_write32(ScU32 addr, ScU32 data)
{
	volatile ScU32 *ptr = (volatile ScU32 *) addr;
	*ptr = data;
	// printf("addr:0x%x data:0x%x\n",addr,data);
}
ScU32 reg_read32(ScU32 addr)
{
	volatile ScU32 *ptr = (volatile ScU32 *) addr;
	// printf("addr:0x%x data:0x%x\n",addr, *ptr);
	return *ptr;
	
}


void dram_init(ScSU64 size,ScS32 index) {

    ScU64 start_dram  = 0;
    ScU8 *start_ptr = NULL;
    static ScSU64 size_start = 0;
    
    
    ScU64 addr = NULL;
    if(size %4 ) {
        printf("dram_malloc size wrong %d\n",size);
        return NULL;
    }
    
    // start_dram = APE_RAM_ADDR + size_start;
    
    start_ptr = ram_ptr +size_start;
    // for(int i=0;i<0x10;i++)
    // {
    //     start_ptr[i] =i;
    //     printf("result1=0x%x\n",start_ptr[i]);
    // }
    ram_addr[index] = start_ptr;					
    //addr = start_dram;
    // ScU8 *tmp = (ScU8 *)ram_addr[index];
    // for(int j=0;j<0x10;j++)
    // {
    //     printf("result2 =0x%x\n",tmp[j]);
    // }

    size_start = size_start +size;
    printf("-----ram_addr[%d]:0x%x \n",index,ram_addr[index]);

    return ;    

}

void dram_copy(ScSU64 size,void * data_ptr,ScS32 index) {
   

    if(data_ptr != NULL) {

        printf("data_ptr: %p\n",data_ptr);
        // memcpy((ScU8 *)ram_addr[index],(ScU8 *)data_ptr,size);
        ram_addr[index] = data_ptr;
        // if(index==1)
        // {
        //    get_context(1,0,100);
        // }
        printf("-----ram_addr[%d]: %p\n",index,ram_addr[index]);
    }
   return; 

}

void poll_field_not_equal(volatile ScU32 reg_num, volatile ScU32 field, volatile ScU32 expect_value)
{

    volatile ScU32 data;
    ScU32 reg;
	printf("poll_reg_num=0x%x, field = 0x%x, expect_value=0x%x\n", reg_num, field, expect_value);
    
    while (1){   
        
        data = reg_read(reg_num);
		if ((data & field) != expect_value){
			break;
		}  
    }

}

void get_context(ScS16 ram_idx,ScU32 offset,ScS32 out_num) {

    int i;
    int max=0;
    int index;
    ScU32 tmp ;
    
    ScU32 *value = (ScU32 *)(ram_addr[ram_idx]);
    // for(int j=0;j<0x10;j++)
    // {
    //     printf("result3 =0x%x\n",value[j]);
    // }
    ScU32 *right_value = (ScU32 *)(ape_get_right_value());
    printf("init_value:%p  out_value:%p\n",(ScU32 *)ram_addr[ram_idx],value);

    //打印输出
    for(i = 0 ;i < out_num/4 ;i++) 
    {   
        tmp = value[i];
        // printf("%x \n",i,tmp);
        if((i+1)%4==0)
        {
            printf("0x%08x,\n",tmp);
        }
        else
        {
            printf("0x%08x,",tmp);
        }
    }
    printf("\n");
    //输出对比
    for(int j = 0 ;j < out_num/4 ;j++) 
    {
        printf("value[%d]=0x%x\n",j,value[j]);
        if(value[j] != right_value[j])
        {
            printf("==== data compare vp failed!==== \n");
            break ;
        }   
        else if((value[j] == right_value[j]) && (j == out_num/4-1))
        {
            printf("==== data compare vp right!==== \n");
            return ;
        }  
    }
    return ;
}

int main()
{
    ScU32 reg;
    ape_ptr = (ScU32 *)APE_REG_ADDR;
    ram_ptr = (ScU8 *)APE_RAM_ADDR;
   
    printf("ape_ptr: %p,ram_ptr: %p\n",ape_ptr,ram_ptr);
    
    ScU8 *weight_ptr =(ScU8 *)(ape_get_ali1());
	ScU8 *input_ptr  =(ScU8 *)(ape_get_ali3());
    
    dram_init( ALLOC_SIZE , 2 );
    dram_copy( WEIGHT_SIZE ,weight_ptr, 1 ); 
    dram_copy( INPUT_SIZE ,input_ptr, 3 );
    

    //Disable CDMA DATA_DONE and WEIGHT_DONE interrupts
    // reg_write(GLB_S_INTR_MASK_0, 0x3f03fc);
    // reg_write(GLB_S_INTR_STATUS_0, 0x0);
    
    reg_write(SDP_S_POINTER_0, 0x0);
    reg_write(SDP_D_DST_BASE_ADDR_HIGH_0, 0x0);
    reg_write(SDP_D_PERF_WDMA_WRITE_STALL_0, 0x0);
    reg_write(SDP_D_PERF_LUT_UFLOW_0, 0x0);
    reg_write(SDP_D_DST_SURFACE_STRIDE_0, 0x8);
    reg_write(SDP_D_PERF_LUT_HYBRID_0, 0x0);
    reg_write(SDP_D_PERF_OUT_SATURATION_0, 0x0);
    reg_write(SDP_S_STATUS_0, 0x0);
    reg_write(SDP_D_PERF_LUT_LE_HIT_0, 0x0);
    reg_write(SDP_D_DP_EW_MUL_CVT_OFFSET_VALUE_0, 0x8eb59288);
    reg_write(SDP_D_DP_EW_ALU_CFG_0, 0x2);
    reg_write(SDP_D_DP_EW_ALU_CVT_SCALE_VALUE_0, 0xb016);
    reg_write(SDP_D_DP_EW_TRUNCATE_VALUE_0, 0x33);
    reg_write(SDP_D_DP_BS_ALU_SRC_VALUE_0, 0xab7d);
    reg_write(SDP_D_STATUS_NAN_INPUT_NUM_0, 0x0);
    reg_write(SDP_D_DP_BN_CFG_0, 0x6b);
    reg_write(SDP_D_DP_BS_MUL_CFG_0, 0x2101);
    reg_write(SDP_D_DP_EW_MUL_CVT_TRUNCATE_VALUE_0, 0x18);
    reg_write(SDP_D_STATUS_NAN_OUTPUT_NUM_0, 0x0);
    reg_write(SDP_D_DATA_FORMAT_0, 0x0);
    reg_write(SDP_D_DP_BS_CFG_0, 0xb);
    reg_write(SDP_D_DP_EW_ALU_CVT_OFFSET_VALUE_0, 0x42139b55);
    reg_write(SDP_D_DP_BN_MUL_SRC_VALUE_0, 0x7e67);
    reg_write(SDP_D_STATUS_INF_INPUT_NUM_0, 0x0);
    reg_write(SDP_D_DATA_CUBE_WIDTH_0, 0x0);
    reg_write(SDP_D_PERF_LUT_OFLOW_0, 0x0);
    reg_write(SDP_D_DST_BATCH_STRIDE_0, 0xdf4cdbe0);
    reg_write(SDP_D_CVT_SHIFT_0, 0x21);
    reg_write(SDP_D_DP_EW_ALU_SRC_VALUE_0, 0x97e0);
    reg_write(SDP_D_DP_EW_ALU_CVT_TRUNCATE_VALUE_0, 0x35);
    reg_write(SDP_D_DST_LINE_STRIDE_0, 0x8);
    reg_write(SDP_D_DP_EW_MUL_CFG_0, 0x1);
    reg_write(SDP_D_DP_EW_MUL_SRC_VALUE_0, 0xabc443a9);
    reg_write(SDP_D_DP_BS_ALU_CFG_0, 0xe01);
    reg_write(SDP_D_CVT_OFFSET_0, 0x3dc324eb);
    reg_write(SDP_D_DST_DMA_CFG_0, 0x1);
    reg_write(SDP_D_CVT_SCALE_0, 0xa433);
    reg_write(SDP_D_DP_BN_ALU_CFG_0, 0x1e00);
    reg_write(SDP_D_DST_BASE_ADDR_LOW_0, OUTPUT_ADDR); //sdp_out_addr
    reg_write(SDP_D_DP_EW_CFG_0, 0x1);
    reg_write(SDP_D_DP_BN_MUL_CFG_0, 0x401);
    reg_write(SDP_D_DATA_CUBE_CHANNEL_0, 0x0);
    reg_write(SDP_D_PERF_LUT_LO_HIT_0, 0x0);
    reg_write(SDP_D_STATUS_0, 0x0);
    reg_write(SDP_D_DP_BS_MUL_SRC_VALUE_0, 0x59c1);
    reg_write(SDP_D_PERF_ENABLE_0, 0x7);
    reg_write(SDP_D_DP_BN_ALU_SRC_VALUE_0, 0x9430);
    reg_write(SDP_D_DATA_CUBE_HEIGHT_0, 0x0);
    reg_write(SDP_D_DP_EW_MUL_CVT_SCALE_VALUE_0, 0x90b8);
    reg_write(SDP_D_FEATURE_MODE_CFG_0, 0x9);

    reg_write(CDMA_S_POINTER_0, 0x0);
    reg_write(CDMA_D_CVT_CFG_0, 0xb0);
    reg_write(CDMA_D_NAN_INPUT_DATA_NUM_0, 0x0);
    reg_write(CDMA_D_INF_INPUT_WEIGHT_NUM_0, 0x0);
    reg_write(CDMA_D_CVT_OFFSET_0, 0x36e4);
    reg_write(CDMA_D_BATCH_NUMBER_0, 0x0);
    reg_write(CDMA_D_NAN_INPUT_WEIGHT_NUM_0, 0x0);
    reg_write(CDMA_D_MEAN_GLOBAL_1_0, 0x97392cf4);
    reg_write(CDMA_D_DATAIN_SIZE_0_0, 0x0);
    reg_write(CDMA_D_DAIN_MAP_0, 0x0);
    reg_write(CDMA_D_PERF_WT_READ_STALL_0, 0x0);
    reg_write(CDMA_D_FETCH_GRAIN_0, 0x0);
    reg_write(CDMA_D_INF_INPUT_DATA_NUM_0, 0x0);
    reg_write(CDMA_S_STATUS_0, 0x0);
    reg_write(CDMA_D_MEAN_FORMAT_0, 0x0);
    reg_write(CDMA_D_MISC_CFG_0, 0x10000000);
    reg_write(CDMA_D_DAIN_ADDR_LOW_1_0, INPUT_ADDR);
    reg_write(CDMA_D_WGS_ADDR_HIGH_0, 0x79);
    reg_write(CDMA_D_CYA_0, 0xaaaadb06);
    reg_write(CDMA_D_PERF_WT_READ_LATENCY_0, 0x0);
    reg_write(CDMA_D_PERF_DAT_READ_LATENCY_0, 0x0);
    reg_write(CDMA_S_CBUF_FLUSH_STATUS_0, 0x0);
    reg_write(CDMA_D_PERF_ENABLE_0, 0x0);
    reg_write(CDMA_D_ENTRY_PER_SLICE_0, 0x0);
    reg_write(CDMA_D_MEAN_GLOBAL_0_0, 0xfec724c9);
    reg_write(CDMA_D_WEIGHT_BYTES_0, 0x80);
    reg_write(CDMA_D_DAIN_ADDR_LOW_0_0, INPUT_ADDR);
    reg_write(CDMA_D_ZERO_PADDING_VALUE_0, 0xd0c0);
    reg_write(CDMA_D_LINE_STRIDE_0, 0x1c0);
    reg_write(CDMA_D_PIXEL_OFFSET_0, 0x20003);
    reg_write(CDMA_D_WMB_BYTES_0, 0x2399f80);
    reg_write(CDMA_D_WEIGHT_RAM_TYPE_0, 0x1);
    reg_write(CDMA_D_CVT_SCALE_0, 0xf9fa);
    reg_write(CDMA_D_SURF_STRIDE_0, 0x2100);
    reg_write(CDMA_D_WMB_ADDR_HIGH_0, 0xcc);
    reg_write(CDMA_D_WEIGHT_ADDR_LOW_0, WEIGHT_ADDR);
    reg_write(CDMA_D_DATAIN_SIZE_1_0, 0x7);
    reg_write(CDMA_D_WMB_ADDR_LOW_0, 0x50b37f00);
    reg_write(CDMA_D_WEIGHT_ADDR_HIGH_0, 0x0);
    reg_write(CDMA_S_ARBITER_0, 0xb0007);
    reg_write(CDMA_D_NAN_FLUSH_TO_ZERO_0, 0x1);
    reg_write(CDMA_D_DAIN_ADDR_HIGH_1_0, 0x7c);
    reg_write(CDMA_D_WEIGHT_FORMAT_0, 0x0);
    reg_write(CDMA_D_DATAIN_SIZE_EXT_0_0, 0x0);
    reg_write(CDMA_D_DAIN_RAM_TYPE_0, 0x1);
    reg_write(CDMA_D_WEIGHT_SIZE_0_0, 0x7);
    reg_write(CDMA_D_DAIN_ADDR_HIGH_0_0, 0x0);
    reg_write(CDMA_D_PERF_DAT_READ_STALL_0, 0x0);
    reg_write(CDMA_D_DATAIN_FORMAT_0, 0x400);
    reg_write(CDMA_D_WGS_ADDR_LOW_0, 0x94654220);
    reg_write(CDMA_D_CONV_STRIDE_0, 0x10000);
    reg_write(CDMA_D_BANK_0, 0x70006);
    reg_write(CDMA_D_LINE_UV_STRIDE_0, 0xf0e1fb40);
    reg_write(CDMA_D_BATCH_STRIDE_0, 0x0);
    reg_write(CDMA_D_WEIGHT_SIZE_1_0, 0x0);
    reg_write(CDMA_D_ZERO_PADDING_0, 0x0);
    reg_write(CSC_S_POINTER_0, 0x0);
    reg_write(CSC_D_RELEASE_0, 0x0);
    reg_write(CSC_D_DATAOUT_SIZE_0_0, 0x0);
    reg_write(CSC_D_POST_Y_EXTENSION_0, 0x0);
    reg_write(CSC_D_BATCH_NUMBER_0, 0x0);
    reg_write(CSC_D_WEIGHT_BYTES_0, 0x80);
    reg_write(CSC_D_WEIGHT_SIZE_EXT_0_0, 0x0);
    reg_write(CSC_D_DATAOUT_SIZE_1_0, 0x0);
    reg_write(CSC_D_PRA_CFG_0, 0x1);
    reg_write(CSC_D_DATAIN_SIZE_EXT_0_0, 0x0);
    reg_write(CSC_S_STATUS_0, 0x0);
    reg_write(CSC_D_CYA_0, 0xaaaadb06);
    reg_write(CSC_D_ENTRY_PER_SLICE_0, 0x0);
    reg_write(CSC_D_WEIGHT_FORMAT_0, 0x0);
    reg_write(CSC_D_BANK_0, 0x70006);
    reg_write(CSC_D_ATOMICS_0, 0x0);
    reg_write(CSC_D_CONV_STRIDE_EXT_0, 0x10000);
    reg_write(CSC_D_DILATION_EXT_0, 0xd0003);
    reg_write(CSC_D_ZERO_PADDING_0, 0x0);
    reg_write(CSC_D_WMB_BYTES_0, 0x2399f80);
    reg_write(CSC_D_ZERO_PADDING_VALUE_0, 0xd0c0);
    reg_write(CSC_D_DATAIN_SIZE_EXT_1_0, 0x7);
    reg_write(CSC_D_WEIGHT_SIZE_EXT_1_0, 0x7);
    reg_write(CSC_D_DATAIN_FORMAT_0, 0x0);
    reg_write(CSC_D_MISC_CFG_0, 0x10000000);
    reg_write(CMAC_A_S_POINTER_0, 0x0);
    reg_write(CMAC_A_D_MISC_CFG_0, 0x0);
    reg_write(CMAC_A_S_STATUS_0, 0x0);
    reg_write(CMAC_B_S_POINTER_0, 0x0);
    reg_write(CMAC_B_S_STATUS_0, 0x0);
    reg_write(CMAC_B_D_MISC_CFG_0, 0x0);
    reg_write(CACC_S_POINTER_0, 0x0);
    reg_write(CACC_D_CLIP_CFG_0, 0x6);
    reg_write(CACC_D_DATAOUT_ADDR_0, 0xf5ac2e0);
    reg_write(CACC_D_MISC_CFG_0, 0x0);
    reg_write(CACC_S_STATUS_0, 0x0);
    reg_write(CACC_D_DATAOUT_SIZE_0_0, 0x0);
    reg_write(CACC_D_CYA_0, 0xaaaadb06);
    reg_write(CACC_D_LINE_STRIDE_0, 0x20);
    reg_write(CACC_D_DATAOUT_MAP_0, 0x10001);
    reg_write(CACC_D_SURF_STRIDE_0, 0x20);
    reg_write(CACC_D_OUT_SATURATION_0, 0x0);
    reg_write(CACC_D_BATCH_NUMBER_0, 0x0);
    reg_write(CACC_D_DATAOUT_SIZE_1_0, 0x0);

    // poll_reg_equal(CDMA_S_CBUF_FLUSH_STATUS_0,0x1);

    reg_write(SDP_D_OP_ENABLE_0, 0x1);
    reg_write(CACC_D_OP_ENABLE_0, 0x1);
    reg_write(CMAC_A_D_OP_ENABLE_0, 0x1);
    reg_write(CMAC_B_D_OP_ENABLE_0, 0x1);
    reg_write(CSC_D_OP_ENABLE_0, 0x1);
    reg_write(CDMA_D_OP_ENABLE_0, 0x1);


    
    poll_field_not_equal(CACC_S_STATUS_0,   0x3, 0x1);
    poll_field_not_equal(CDMA_S_STATUS_0,   0x3, 0x1);
    poll_field_not_equal(CMAC_A_S_STATUS_0, 0x3, 0x1);
    poll_field_not_equal(CMAC_B_S_STATUS_0, 0x3, 0x1);
    poll_field_not_equal(CSC_S_STATUS_0,    0x3, 0x1);

    // poll_field_not_equal(CACC_S_STATUS_0,   0x30000, 0x10000);
    // poll_field_not_equal(CDMA_S_STATUS_0,   0x30000, 0x10000);
    // poll_field_not_equal(CMAC_A_S_STATUS_0, 0x30000, 0x10000);
    // poll_field_not_equal(CMAC_B_S_STATUS_0, 0x30000, 0x10000);
    // poll_field_not_equal(CSC_S_STATUS_0,    0x30000, 0x10000);
    
    
    get_context(2,0,0x8);
    // get_context(2,0,1152);
}
