#include "ape_get_ali.h"
#include "ape_small_single.h"
#include <stdio.h>

#define APE_REG_ADDR 0x10040000
// #define APE_REG_ADDR 0x3000000
#define APE_REG_SIZE 0x40000

#define APE_RAM_ADDR 0x80050000
#define APE_RAM_SIZE 0x30000
//addr alloc
#define WEIGHT_ADDR 0x80051000
#define BIAS_ADDR  0x80057200
#define INPUT_ADDR 0x80057300
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
    //输出对比
    for(int j = 0 ;j < out_num/4 ;j++) 
    {
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
    
    dram_init( 0x1000 , 2 );
    dram_copy( WEIGHT_SIZE ,weight_ptr, 1 ); 
    dram_copy( INPUT_SIZE ,input_ptr, 3 );
    

    //sdp4
    reg_write(SDP_S_POINTER_0,                   0x0);//0x9004
    reg_write(SDP_RDMA_S_POINTER_0,              0x0);//8004

    reg_write(SDP_RDMA_D_FEATURE_MODE_CFG_0,     0x0);//8070
    reg_write(SDP_RDMA_D_BRDMA_CFG_0,            0x0);//8028
    reg_write(SDP_RDMA_D_NRDMA_CFG_0,            0x0);//8040
    reg_write(SDP_RDMA_D_ERDMA_CFG_0,            0x0);//8058
    reg_write(SDP_RDMA_D_FEATURE_MODE_CFG_0,     0x1);//0x8070
    reg_write(SDP_RDMA_D_DATA_CUBE_WIDTH_0,      0x7);//800c
    reg_write(SDP_RDMA_D_DATA_CUBE_HEIGHT_0,     0x7);//8010
    reg_write(SDP_RDMA_D_DATA_CUBE_CHANNEL_0,    0x31);//8014
    reg_write(SDP_RDMA_D_BRDMA_CFG_0,            0x2a);//8028
    reg_write(SDP_RDMA_D_BS_BASE_ADDR_LOW_0,     BIAS_ADDR);//802c sdp_bias_size=0x64
    reg_write(SDP_RDMA_D_BS_BASE_ADDR_HIGH_0,    0x0);//8030
    reg_write(SDP_RDMA_D_BS_LINE_STRIDE_0,       0x10);//8034
    reg_write(SDP_RDMA_D_BS_SURFACE_STRIDE_0,    0x10);//8038
    reg_write(SDP_RDMA_D_NRDMA_CFG_0,            0x31);//8040
    reg_write(SDP_RDMA_D_ERDMA_CFG_0,            0x31);//8058
    reg_write(SDP_D_DATA_CUBE_WIDTH_0,           0x7);//903c
    reg_write(SDP_D_DATA_CUBE_HEIGHT_0,          0x7);//9040
    reg_write(SDP_D_DATA_CUBE_CHANNEL_0,         0x31);//9044
    reg_write(SDP_D_DST_BASE_ADDR_HIGH_0,        0x0);//904c
    reg_write(SDP_D_DST_BASE_ADDR_LOW_0,         OUTPUT_ADDR);//9048//sdp_output
    reg_write(SDP_D_DST_LINE_STRIDE_0,           0x40);//9050
    reg_write(SDP_D_DST_SURFACE_STRIDE_0,        0x200);//9054
    reg_write(SDP_D_DP_BS_CFG_0,                 0x58);//9058
    reg_write(SDP_D_DP_BS_ALU_CFG_0,             0x1);//905c
    reg_write(SDP_D_DP_BS_MUL_CFG_0,             0x1);//9064
    reg_write(SDP_D_DP_BN_CFG_0,                 0x53);//906c
    reg_write(SDP_D_DP_EW_CFG_0,                 0x53);//9080
    reg_write(SDP_D_FEATURE_MODE_CFG_0,          0x1);//90b0
    reg_write(SDP_D_DST_DMA_CFG_0,               0x1);//90b4
    reg_write(SDP_D_DATA_FORMAT_0,               0x0);//90bc
    reg_write(SDP_D_CVT_OFFSET_0,                0x0);//90c0
    reg_write(SDP_D_CVT_SCALE_0,                 0x7677);//90c4
    reg_write(SDP_D_CVT_SHIFT_0,                 0x19);//90c8
    
    reg_write(SDP_RDMA_D_OP_ENABLE_0,            0x1);//8008
    reg_write(SDP_D_OP_ENABLE_0,                 0x1);//9038

    //conv3
    reg_write(CACC_S_POINTER_0,                  0x0);//0x7004
    reg_write(CMAC_A_S_POINTER_0,                0x0);//5004
    reg_write(CMAC_B_S_POINTER_0,                0x0);//6004
    reg_write(CSC_S_POINTER_0,                   0x0);//4000
    reg_write(CDMA_S_POINTER_0,                  0x0);//3004

    reg_write(CACC_D_MISC_CFG_0,                 0x0);//0x700c
    reg_write(CACC_D_DATAOUT_SIZE_0_0,           0x70007);//7010
    reg_write(CACC_D_DATAOUT_SIZE_1_0,           0x31);//7014
    reg_write(CACC_D_DATAOUT_ADDR_0,             0x0);//7018
    reg_write(CACC_D_BATCH_NUMBER_0,             0x0);//701c
    reg_write(CACC_D_LINE_STRIDE_0,              0x40);//7020
    reg_write(CACC_D_SURF_STRIDE_0,              0x200);//7024
    reg_write(CACC_D_DATAOUT_MAP_0,              0x0);//7028
    reg_write(CACC_D_CLIP_CFG_0,                 0x0);//702c
    reg_write(CMAC_A_D_MISC_CFG_0,               0x0);//500c
    reg_write(CMAC_B_D_MISC_CFG_0,               0x0);//600c
    reg_write(CSC_D_MISC_CFG_0,                  0x0);//400c
    reg_write(CSC_D_DATAIN_FORMAT_0,             0x0);//4010
    reg_write(CSC_D_DATAIN_SIZE_EXT_0_0,         0xb000b);//4014
    reg_write(CSC_D_DATAIN_SIZE_EXT_1_0,         0x13);//4018
    reg_write(CSC_D_BATCH_NUMBER_0,              0x0);//401c
    reg_write(CSC_D_POST_Y_EXTENSION_0,          0x0);//4020
    reg_write(CSC_D_ENTRY_PER_SLICE_0,           0x23);//4024
    reg_write(CSC_D_WEIGHT_FORMAT_0,             0x0);//4028
    reg_write(CSC_D_WEIGHT_SIZE_EXT_0_0,         0x40004);//402c
    reg_write(CSC_D_WEIGHT_SIZE_EXT_1_0,         0x310013);//4030
    reg_write(CSC_D_WEIGHT_BYTES_0,              0x61a8);//4034
    reg_write(CSC_D_WMB_BYTES_0,                 0x0);//4038
    reg_write(CSC_D_DATAOUT_SIZE_0_0,            0x70007);//403c
    reg_write(CSC_D_DATAOUT_SIZE_1_0,            0x31);//4040
    reg_write(CSC_D_ATOMICS_0,                   0x3f);//4044
    reg_write(CSC_D_RELEASE_0,                   0xb);//4048
    reg_write(CSC_D_CONV_STRIDE_EXT_0,           0x0);//404c
    reg_write(CSC_D_DILATION_EXT_0,              0x0);//4050
    reg_write(CSC_D_ZERO_PADDING_0,              0x0);//4054
    reg_write(CSC_D_ZERO_PADDING_VALUE_0,        0x0);//4058
    reg_write(CSC_D_BANK_0,                      0x60000);//405c
    reg_write(CSC_D_PRA_CFG_0,                   0x0);//4060
    reg_write(CDMA_D_MISC_CFG_0,                 0x0);//3014
    reg_write(CDMA_D_DATAIN_FORMAT_0,            0x100000);//3018
    reg_write(CDMA_D_DATAIN_SIZE_0_0,            0xb000b);//301c
    reg_write(CDMA_D_DATAIN_SIZE_1_0,            0x13);//3020
    reg_write(CDMA_D_DATAIN_SIZE_EXT_0_0,        0xb000b);//3024
    reg_write(CDMA_D_DAIN_RAM_TYPE_0,            0x1);//302c
    reg_write(CDMA_D_DAIN_ADDR_HIGH_0_0,         0x0);//3030
    reg_write(CDMA_D_DAIN_ADDR_LOW_0_0,          INPUT_ADDR);//3034
    reg_write(CDMA_D_DAIN_ADDR_HIGH_1_0,         0x0);//3038
    reg_write(CDMA_D_DAIN_ADDR_LOW_1_0,          INPUT_ADDR);//303c //conv_input_size=0xd80
    reg_write(CDMA_D_LINE_STRIDE_0,              0x60);//3040
    reg_write(CDMA_D_SURF_STRIDE_0,              0x480);//3048
    reg_write(CDMA_D_LINE_UV_STRIDE_0,           0x0);//3044
    reg_write(CDMA_D_DAIN_MAP_0,                 0x10001);//304c
    reg_write(CDMA_D_BATCH_NUMBER_0,             0x0);//3058
    reg_write(CDMA_D_BATCH_STRIDE_0,             0x0);//305c
    reg_write(CDMA_D_ENTRY_PER_SLICE_0,          0x23);//3060
    reg_write(CDMA_D_FETCH_GRAIN_0,              0x0);//3064
    reg_write(CDMA_D_WEIGHT_FORMAT_0,            0x0);//3068
    reg_write(CDMA_D_WEIGHT_SIZE_0_0,            0x1f3);//306c
    reg_write(CDMA_D_WEIGHT_SIZE_1_0,            0x31);//3070
    reg_write(CDMA_D_WEIGHT_RAM_TYPE_0,          0x1);//3074
    reg_write(CDMA_D_WEIGHT_ADDR_HIGH_0,         0x0);//0x3078
    reg_write(CDMA_D_WEIGHT_ADDR_LOW_0,          WEIGHT_ADDR);//0x307c //conv_weight_size=0x61a8
    reg_write(CDMA_D_WEIGHT_BYTES_0,             0x61a8);//0x3080
    reg_write(CDMA_D_MEAN_FORMAT_0,              0x0);//0x3098
    reg_write(CDMA_D_CVT_CFG_0,                  0x1);//0x30a4
    reg_write(CDMA_D_CVT_OFFSET_0,               0x0);//0x30a8
    reg_write(CDMA_D_CVT_SCALE_0,                0x1);//0x30ac
    reg_write(CDMA_D_CONV_STRIDE_0,              0x0);//0x30b0
    reg_write(CDMA_D_ZERO_PADDING_0,             0x0);//0x30b4
    reg_write(CDMA_D_ZERO_PADDING_VALUE_0,       0x0);//0x30b8
    reg_write(CDMA_D_BANK_0,                     0x60000);//0x30bc

    reg_write(CACC_D_OP_ENABLE_0,                0x1);//7008
    reg_write(CMAC_A_D_OP_ENABLE_0,              0x1);//5008
    reg_write(CMAC_B_D_OP_ENABLE_0,              0x1);//6008
    reg_write(CSC_D_OP_ENABLE_0,                 0x1);//4008
    reg_write(CDMA_D_OP_ENABLE_0,                0x1);//3010
    reg_write(CDMA_D_OP_ENABLE_0,                0x1);//3010
    reg_write(CDMA_D_OP_ENABLE_0,                0x1);//3010

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
    
    
    get_context(2,0,3584);
    // get_context(2,0,1152);
}
