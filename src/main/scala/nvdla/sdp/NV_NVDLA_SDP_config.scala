package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class sdpConfiguration extends cdmaConfiguration
{
    val AM_AW = NVDLA_MEMORY_ATOMIC_LOG2       //atomic m address width
    val AM_AW2 = NVDLA_MEMORY_ATOMIC_LOG2-1
    val AM_DW = NVDLA_MEMORY_ATOMIC_SIZE*8      //atomic m bus width: atomic_m * 1byte
    val AM_DW2 = NVDLA_MEMORY_ATOMIC_SIZE*16    //atomic m bus width: atomic_m * 2byte

    val TW = NVDLA_SDP_EW_THROUGHPUT_LOG2  

    val SDP_WR_CMD_DW = NVDLA_MEM_ADDRESS_WIDTH-AM_AW+13 

    val BS_OP_DW = 16*NVDLA_SDP_BS_THROUGHPUT    
    val BN_OP_DW = 16*NVDLA_SDP_BN_THROUGHPUT
    val EW_OP_DW = 16*NVDLA_SDP_EW_THROUGHPUT  
    val EW_OC_DW = 32*NVDLA_SDP_EW_THROUGHPUT

    val BS_IN_DW = 32*NVDLA_SDP_BS_THROUGHPUT
    val BS_OUT_DW = 32*NVDLA_SDP_BS_THROUGHPUT
    val BN_IN_DW = 32*NVDLA_SDP_BN_THROUGHPUT
    val BN_OUT_DW = 32*NVDLA_SDP_BN_THROUGHPUT
    val EW_IN_DW = 32*NVDLA_SDP_EW_THROUGHPUT
    val EW_OUT_DW = 32*NVDLA_SDP_EW_THROUGHPUT

    val EW_CORE_OUT_DW = 32*NVDLA_SDP_EW_THROUGHPUT
    val EW_IDX_OUT_DW = 81*NVDLA_SDP_EW_THROUGHPUT
    val EW_LUT_OUT_DW = 185*NVDLA_SDP_EW_THROUGHPUT
    val EW_INP_OUT_DW = 32*NVDLA_SDP_EW_THROUGHPUT

    val DP_DIN_DW = 32*NVDLA_MEMORY_ATOMIC_SIZE 
    val DP_IN_DW = 32*NVDLA_SDP_MAX_THROUGHPUT
    val BS_DOUT_DW = 32*NVDLA_SDP_MAX_THROUGHPUT
    val BN_DIN_DW  = 32*NVDLA_SDP_MAX_THROUGHPUT
    val BN_DOUT_DW  = 32*NVDLA_SDP_MAX_THROUGHPUT
    val EW_DIN_DW  = 32*NVDLA_SDP_MAX_THROUGHPUT
    val EW_DOUT_DW = 32*NVDLA_SDP_MAX_THROUGHPUT
    val CV_IN_DW = 32*NVDLA_SDP_MAX_THROUGHPUT
    val CV_OUT_DW = 16*NVDLA_SDP_MAX_THROUGHPUT
    val DP_OUT_DW = NVDLA_BPE*NVDLA_SDP_MAX_THROUGHPUT 
    val DP_DOUT_DW = AM_DW                         //int8: 32 * 1B ; int16: 16 * 2B

    val LUT_TABLE_LE_DEPTH = 65
    val LUT_TABLE_LO_DEPTH = 257
    val LUT_TABLE_MAX_DEPTH = LUT_TABLE_LO_DEPTH

}

class lut_out_if extends Bundle{
    val frac = Output(UInt(35.W))
    val index = Output(UInt(9.W))
    val oflow = Output(Bool())
    val uflow = Output(Bool())
}

class sdp_rdma_reg_dual_flop_outputs extends Bundle{
    val bn_base_addr_high = Output(UInt(32.W))
    val bn_base_addr_low = Output(UInt(32.W))
    val bn_batch_stride = Output(UInt(32.W))
    val bn_line_stride = Output(UInt(32.W))
    val bn_surface_stride = Output(UInt(32.W))
    val brdma_data_mode = Output(Bool())
    val brdma_data_size = Output(Bool())
    val brdma_data_use = Output(UInt(2.W))
    val brdma_disable = Output(Bool())
    val brdma_ram_type = Output(Bool())
    val bs_base_addr_high = Output(UInt(32.W))
    val bs_base_addr_low = Output(UInt(32.W))
    val bs_batch_stride = Output(UInt(32.W))
    val bs_line_stride = Output(UInt(32.W))
    val bs_surface_stride = Output(UInt(32.W))
    val channel = Output(UInt(13.W))
    val height = Output(UInt(13.W))
    val width_a = Output(UInt(13.W))
    val erdma_data_mode = Output(Bool())
    val erdma_data_size = Output(Bool())
    val erdma_data_use = Output(UInt(2.W))
    val erdma_disable = Output(Bool())
    val erdma_ram_type = Output(Bool())
    val ew_base_addr_high = Output(UInt(32.W))
    val ew_base_addr_low = Output(UInt(32.W))
    val ew_batch_stride = Output(UInt(32.W))
    val ew_line_stride = Output(UInt(32.W))
    val ew_surface_stride = Output(UInt(32.W))
    val batch_number = Output(UInt(5.W))
    val flying_mode = Output(Bool())
    val in_precision = Output(UInt(2.W))
    val out_precision = Output(UInt(2.W))
    val proc_precision = Output(UInt(2.W))
    val winograd = Output(Bool())
    val nrdma_data_mode = Output(Bool())
    val nrdma_data_size = Output(Bool())
    val nrdma_data_use = Output(UInt(2.W))
    val nrdma_disable = Output(Bool())
    val nrdma_ram_type = Output(Bool())
    val perf_dma_en = Output(Bool())
    val perf_nan_inf_count_en = Output(Bool())
    val src_base_addr_high = Output(UInt(32.W))
    val src_base_addr_low = Output(UInt(32.W))
    val src_ram_type = Output(Bool())
    val src_line_stride = Output(UInt(32.W))
    val src_surface_stride = Output(UInt(32.W))
}

class sdp_reg_single_flop_outputs extends Bundle{

    val lut_hybrid_priority = Output(Bool())
    val lut_le_function = Output(Bool())
    val lut_oflow_priority = Output(Bool())
    val lut_uflow_priority = Output(Bool())
    val lut_le_index_offset = Output(UInt(8.W))
    val lut_le_index_select = Output(UInt(8.W))
    val lut_lo_index_select = Output(UInt(8.W))

    val lut_le_end = Output(UInt(32.W))
    val lut_le_slope_oflow_scale = Output(UInt(16.W))
    val lut_le_slope_uflow_scale = Output(UInt(16.W))
    val lut_le_slope_oflow_shift = Output(UInt(5.W))
    val lut_le_slope_uflow_shift = Output(UInt(5.W))
    val lut_le_start = Output(UInt(32.W))
    val lut_lo_end = Output(UInt(32.W))
    val lut_lo_slope_oflow_scale = Output(UInt(16.W))
    val lut_lo_slope_uflow_scale = Output(UInt(16.W))
    val lut_lo_slope_oflow_shift = Output(UInt(5.W))
    val lut_lo_slope_uflow_shift = Output(UInt(5.W))
    val lut_lo_start = Output(UInt(32.W))
}

class sdp_reg_dual_flop_outputs extends Bundle{

    val cvt_offset = Output(UInt(32.W))
    val cvt_scale = Output(UInt(16.W))
    val cvt_shift = Output(UInt(6.W))
    val channel = Output(UInt(13.W))
    val height = Output(UInt(13.W))
    val width_a = Output(UInt(13.W))
    val out_precision = Output(UInt(2.W))
    val proc_precision = Output(UInt(2.W))

    val bn_alu_shift_value = Output(UInt(6.W))
    val bn_alu_src = Output(Bool())      
    val bn_alu_operand = Output(UInt(16.W))
    val bn_alu_algo = Output(UInt(2.W))
    val bn_alu_bypass = Output(Bool())
    val bn_bypass = Output(Bool())
    val bn_mul_bypass = Output(Bool())
    val bn_mul_prelu = Output(Bool())
    val bn_relu_bypass = Output(Bool())
    val bn_mul_shift_value = Output(UInt(8.W))
    val bn_mul_src = Output(Bool())
    val bn_mul_operand = Output(UInt(16.W))

    val bs_alu_shift_value = Output(UInt(6.W))
    val bs_alu_src = Output(Bool())      
    val bs_alu_operand = Output(UInt(16.W))
    val bs_alu_algo = Output(UInt(2.W))
    val bs_alu_bypass = Output(Bool())
    val bs_bypass = Output(Bool())
    val bs_mul_bypass = Output(Bool())
    val bs_mul_prelu = Output(Bool())
    val bs_relu_bypass = Output(Bool())
    val bs_mul_shift_value = Output(UInt(8.W))
    val bs_mul_src = Output(Bool())
    val bs_mul_operand = Output(UInt(16.W))

    val ew_alu_cvt_bypass = Output(Bool())
    val ew_alu_src = Output(Bool())
    val ew_alu_cvt_offset = Output(UInt(32.W))
    val ew_alu_cvt_scale = Output(UInt(16.W))
    val ew_alu_cvt_truncate = Output(UInt(6.W))
    val ew_alu_operand = Output(UInt(32.W))
    val ew_alu_algo = Output(UInt(2.W))
    val ew_alu_bypass = Output(Bool())
    val ew_bypass = Output(Bool())
    val ew_lut_bypass = Output(Bool())
    val ew_mul_bypass = Output(Bool())
    val ew_mul_prelu = Output(Bool())
    val ew_mul_cvt_bypass = Output(Bool())
    val ew_mul_src = Output(Bool())
    val ew_mul_cvt_offset = Output(UInt(32.W))
    val ew_mul_cvt_scale = Output(UInt(16.W))
    val ew_mul_cvt_truncate = Output(UInt(6.W))
    val ew_mul_operand = Output(UInt(32.W))
    val ew_truncate = Output(UInt(10.W))

    val dst_base_addr_high = Output(UInt(32.W))
    val dst_base_addr_low = Output(UInt(32.W))
    val dst_batch_stride = Output(UInt(32.W))
    val dst_ram_type = Output(Bool())
    val dst_line_stride = Output(UInt(32.W))
    val dst_surface_stride = Output(UInt(32.W))

    val batch_number = Output(UInt(5.W))
    val flying_mode = Output(Bool())
    val nan_to_zero = Output(Bool())
    val output_dst = Output(Bool())
    val winograd = Output(Bool())
    
    val perf_dma_en = Output(Bool())
    val perf_lut_en = Output(Bool())
    val perf_nan_inf_count_en = Output(Bool())
    val perf_sat_en = Output(Bool())
}
