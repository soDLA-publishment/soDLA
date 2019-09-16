package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_REG_dual extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg = new reg_control_if

        // Writable register flop/trigger outputs
        val field = new pdp_reg_dual_flop_outputs
        val op_en_trigger = Output(Bool())

        // Read-only register input
        val inf_input_num = Input(UInt(32.W))
        val nan_input_num = Input(UInt(32.W))
        val nan_output_num = Input(UInt(32.W))
        val op_en = Input(Bool())
        val perf_write_stall = Input(UInt(32.W))
    })
//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 
    withClock(io.nvdla_core_clk){

    // Address decode
    val nvdla_pdp_d_cya_0_wren = (io.reg.offset === "h9c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_data_cube_in_channel_0_wren = (io.reg.offset === "h14".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_data_cube_in_height_0_wren = (io.reg.offset === "h10".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_data_cube_in_width_0_wren = (io.reg.offset === "h0c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_data_cube_out_channel_0_wren = (io.reg.offset === "h20".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_data_cube_out_height_0_wren = (io.reg.offset === "h1c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_data_cube_out_width_0_wren = (io.reg.offset === "h18".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_data_format_0_wren = (io.reg.offset === "h84".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_dst_base_addr_high_0_wren = (io.reg.offset === "h74".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_dst_base_addr_low_0_wren = (io.reg.offset === "h70".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_dst_line_stride_0_wren = (io.reg.offset === "h78".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_dst_ram_cfg_0_wren = (io.reg.offset === "h80".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_dst_surface_stride_0_wren = (io.reg.offset === "h7c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_inf_input_num_0_wren = (io.reg.offset === "h88".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_nan_flush_to_zero_0_wren = (io.reg.offset === "h28".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_nan_input_num_0_wren = (io.reg.offset === "h8c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_nan_output_num_0_wren = (io.reg.offset === "h90".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_operation_mode_cfg_0_wren = (io.reg.offset === "h24".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_op_enable_0_wren = (io.reg.offset === "h08".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_partial_width_in_0_wren = (io.reg.offset === "h2c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_partial_width_out_0_wren = (io.reg.offset === "h30".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_perf_enable_0_wren = (io.reg.offset === "h94".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_perf_write_stall_0_wren = (io.reg.offset === "h98".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_pooling_kernel_cfg_0_wren = (io.reg.offset === "h34".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_pooling_padding_cfg_0_wren = (io.reg.offset === "h40".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_pooling_padding_value_1_cfg_0_wren = (io.reg.offset === "h44".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_pooling_padding_value_2_cfg_0_wren = (io.reg.offset === "h48".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_pooling_padding_value_3_cfg_0_wren = (io.reg.offset === "h4c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_pooling_padding_value_4_cfg_0_wren = (io.reg.offset === "h50".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_pooling_padding_value_5_cfg_0_wren = (io.reg.offset === "h54".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_pooling_padding_value_6_cfg_0_wren = (io.reg.offset === "h58".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_pooling_padding_value_7_cfg_0_wren = (io.reg.offset === "h5c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_recip_kernel_height_0_wren = (io.reg.offset === "h3c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_recip_kernel_width_0_wren = (io.reg.offset === "h38".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_src_base_addr_high_0_wren = (io.reg.offset === "h64".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_src_base_addr_low_0_wren = (io.reg.offset === "h60".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_src_line_stride_0_wren = (io.reg.offset === "h68".asUInt(32.W)) & io.reg.wr_en
    val nvdla_pdp_d_src_surface_stride_0_wren = (io.reg.offset === "h6c".asUInt(32.W)) & io.reg.wr_en

    io.op_en_trigger := nvdla_pdp_d_op_enable_0_wren

    // Output mux

    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(      
    //nvdla_pdp_d_cya_0_out
    "h9c".asUInt(32.W)  -> io.field.cya,
    //nvdla_pdp_d_data_cube_in_channel_0_out
    "h14".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.cube_in_channel),
    //nvdla_pdp_d_data_cube_in_height_0_out
    "h10".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.cube_in_height),
    //nvdla_pdp_d_data_cube_in_width_0_out
    "h0c".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.cube_in_width),
    //nvdla_pdp_d_data_cube_out_channel_0_out
    "h20".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.cube_out_channel),
    //nvdla_pdp_d_data_cube_out_height_0_out 
    "h1c".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.cube_out_height),
    //nvdla_pdp_d_data_cube_out_width_0_out
    "h18".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.cube_out_width),
    //nvdla_pdp_d_data_format_0_out
    "h84".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.input_data),
    //nvdla_pdp_d_dst_base_addr_high_0_out
    "h74".asUInt(32.W)  -> io.field.dst_base_addr_high,
    //nvdla_pdp_d_dst_base_addr_low_0_out
    "h70".asUInt(32.W)  -> io.field.dst_base_addr_low,
    //nvdla_pdp_d_dst_line_stride_0_out
    "h78".asUInt(32.W)  -> io.field.dst_line_stride,
    //nvdla_pdp_d_dst_ram_cfg_0_out 
    "h80".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.field.dst_ram_type),
    //nvdla_pdp_d_dst_surface_stride_0_out
    "h7c".asUInt(32.W)  -> io.field.dst_surface_stride,
    //nvdla_pdp_d_inf_input_num_0_out
    "h88".asUInt(32.W)  -> io.inf_input_num,
    //nvdla_pdp_d_nan_flush_to_zero_0_out
    "h28".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.field.nan_to_zero),
    //nvdla_pdp_d_nan_input_num_0_out
    "h8c".asUInt(32.W)  -> io.nan_input_num,
    //nvdla_pdp_d_nan_output_num_0_out
    "h90".asUInt(32.W)  -> io.nan_output_num,
    //nvdla_pdp_d_operation_mode_cfg_0_out
    "h24".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.split_num, "b0".asUInt(3.W), io.field.flying_mode, "b0".asUInt(2.W), io.field.pooling_method),
    //nvdla_pdp_d_op_enable_0_out
    "h08".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.op_en),
    //nvdla_pdp_d_partial_width_in_0_out
    "h2c".asUInt(32.W)  -> Cat("b0".asUInt(2.W), io.field.partial_width_in_mid, io.field.partial_width_in_last, io.field.partial_width_in_first),
    //nvdla_pdp_d_partial_width_out_0_out
    "h30".asUInt(32.W)  -> Cat("b0".asUInt(2.W), io.field.partial_width_out_mid, io.field.partial_width_out_last, io.field.partial_width_out_first),
    //nvdla_pdp_d_perf_enable_0_out
    "h94".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.field.dma_en),
    //nvdla_pdp_d_perf_write_stall_0_out
    "h98".asUInt(32.W)  -> io.perf_write_stall,
    //nvdla_pdp_d_pooling_kernel_cfg_0_out
    "h34".asUInt(32.W)  -> Cat("b0".asUInt(8.W), io.field.kernel_stride_height, io.field.kernel_stride_width, "b0".asUInt(4.W), io.field.kernel_height, "b0".asUInt(4.W), io.field.kernel_width),
    //nvdla_pdp_d_pooling_padding_cfg_0_out
    "h40".asUInt(32.W)  -> Cat("b0".asUInt(17.W), io.field.pad_bottom, false.B, io.field.pad_right, false.B, io.field.pad_top, false.B, io.field.pad_left),
    //nvdla_pdp_d_pooling_padding_value_1_cfg_0_out
    "h44".asUInt(32.W)  -> Cat("b0".asUInt(13.W), io.field.pad_value_1x),
    //nvdla_pdp_d_pooling_padding_value_2_cfg_0_out
    "h48".asUInt(32.W)  -> Cat("b0".asUInt(13.W), io.field.pad_value_2x),
    //nvdla_pdp_d_pooling_padding_value_3_cfg_0_out
    "h4c".asUInt(32.W)  -> Cat("b0".asUInt(13.W), io.field.pad_value_3x),
    //nvdla_pdp_d_pooling_padding_value_4_cfg_0_out 
    "h50".asUInt(32.W)  -> Cat("b0".asUInt(13.W), io.field.pad_value_4x),
    //nvdla_pdp_d_pooling_padding_value_5_cfg_0_out
    "h54".asUInt(32.W)  -> Cat("b0".asUInt(13.W), io.field.pad_value_5x),
    //nvdla_pdp_d_pooling_padding_value_6_cfg_0_out
    "h58".asUInt(32.W)  -> Cat("b0".asUInt(13.W), io.field.pad_value_6x),
    //nvdla_pdp_d_pooling_padding_value_7_cfg_0_out 
    "h5c".asUInt(32.W)  -> Cat("b0".asUInt(13.W), io.field.pad_value_7x),
    //nvdla_pdp_d_recip_kernel_height_0_out
    "h3c".asUInt(32.W)  -> Cat("b0".asUInt(15.W), io.field.recip_kernel_height),
    //nvdla_pdp_d_recip_kernel_width_0_out
    "h38".asUInt(32.W)  -> Cat("b0".asUInt(15.W), io.field.recip_kernel_width),
    //nvdla_pdp_d_src_base_addr_high_0_out
    "h64".asUInt(32.W)  -> io.field.src_base_addr_high,
    //nvdla_pdp_d_src_base_addr_low_0_out
    "h60".asUInt(32.W)  -> io.field.src_base_addr_low ,
    //nvdla_pdp_d_src_line_stride_0_out
    "h68".asUInt(32.W)  -> io.field.src_line_stride,
    //nvdla_pdp_d_src_surface_stride_0_out
    "h6c".asUInt(32.W)  -> io.field.src_surface_stride
    ))

    // Register flop declaration

    // Register: NVDLA_PDP_D_CYA_0    Field: cya
    io.field.cya := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_pdp_d_cya_0_wren)   
    // Register: NVDLA_PDP_D_DATA_CUBE_IN_CHANNEL_0    Field: cube_in_channel
    io.field.cube_in_channel := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_pdp_d_data_cube_in_channel_0_wren)   
    // Register: NVDLA_PDP_D_DATA_CUBE_IN_HEIGHT_0    Field: cube_in_height
    io.field.cube_in_height := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_pdp_d_data_cube_in_height_0_wren)  
    // Register: NVDLA_PDP_D_DATA_CUBE_IN_WIDTH_0    Field: cube_in_width 
    io.field.cube_in_width := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_pdp_d_data_cube_in_width_0_wren)   
    // Register: NVDLA_PDP_D_DATA_CUBE_OUT_CHANNEL_0    Field: cube_out_channel
    io.field.cube_out_channel := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_pdp_d_data_cube_out_channel_0_wren)  
    // Register: NVDLA_PDP_D_DATA_CUBE_OUT_HEIGHT_0    Field: cube_out_height 
    io.field.cube_out_height := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_pdp_d_data_cube_out_height_0_wren)  
    // Register: NVDLA_PDP_D_DATA_CUBE_OUT_WIDTH_0    Field: cube_out_width 
    io.field.cube_out_width := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_pdp_d_data_cube_out_width_0_wren)   
    // Register: NVDLA_PDP_D_DATA_FORMAT_0    Field: input_data
    io.field.input_data := RegEnable(io.reg.wr_data(1, 0), "b0".asUInt(2.W), nvdla_pdp_d_data_format_0_wren)   
    // Register: NVDLA_PDP_D_DST_BASE_ADDR_HIGH_0    Field: dst_base_addr_high
    io.field.dst_base_addr_high := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_pdp_d_dst_base_addr_high_0_wren)  
    // Register: NVDLA_PDP_D_DST_BASE_ADDR_LOW_0    Field: dst_base_addr_low 
    io.field.dst_base_addr_low := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_pdp_d_dst_base_addr_low_0_wren)   
    // Register: NVDLA_PDP_D_DST_LINE_STRIDE_0    Field: dst_line_stride
    io.field.dst_line_stride := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_pdp_d_dst_line_stride_0_wren)   
    // Register: NVDLA_PDP_D_DST_RAM_CFG_0    Field: dst_ram_type
    io.field.dst_ram_type := RegEnable(io.reg.wr_data(0), false.B, nvdla_pdp_d_dst_ram_cfg_0_wren)   
    // Register: NVDLA_PDP_D_DST_SURFACE_STRIDE_0    Field: dst_surface_stride
    io.field.dst_surface_stride := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_pdp_d_dst_surface_stride_0_wren) 
    // Register: NVDLA_PDP_D_NAN_FLUSH_TO_ZERO_0    Field: nan_to_zero  
    io.field.nan_to_zero := RegEnable(io.reg.wr_data(0), false.B, nvdla_pdp_d_nan_flush_to_zero_0_wren)   
    // Register: NVDLA_PDP_D_OPERATION_MODE_CFG_0    Field: flying_mode
    io.field.flying_mode := RegEnable(io.reg.wr_data(4), false.B, nvdla_pdp_d_operation_mode_cfg_0_wren)   
    // Register: NVDLA_PDP_D_OPERATION_MODE_CFG_0    Field: pooling_method
    io.field.pooling_method := RegEnable(io.reg.wr_data(1, 0), "b0".asUInt(2.W), nvdla_pdp_d_operation_mode_cfg_0_wren)   
    // Register: NVDLA_PDP_D_OPERATION_MODE_CFG_0    Field: split_num
    io.field.split_num := RegEnable(io.reg.wr_data(15, 8), "b0".asUInt(8.W), nvdla_pdp_d_operation_mode_cfg_0_wren)  
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_IN_0    Field: partial_width_in_first 
    io.field.partial_width_in_first := RegEnable(io.reg.wr_data(9, 0), "b0".asUInt(10.W), nvdla_pdp_d_partial_width_in_0_wren) 
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_IN_0    Field: partial_width_in_last  
    io.field.partial_width_in_last := RegEnable(io.reg.wr_data(19, 10), "b0".asUInt(10.W), nvdla_pdp_d_partial_width_in_0_wren)   
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_IN_0    Field: partial_width_in_mid
    io.field.partial_width_in_mid := RegEnable(io.reg.wr_data(29, 20), "b0".asUInt(10.W), nvdla_pdp_d_partial_width_in_0_wren)  
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_OUT_0    Field: partial_width_out_first 
    io.field.partial_width_out_first := RegEnable(io.reg.wr_data(9, 0), "b0".asUInt(10.W), nvdla_pdp_d_partial_width_out_0_wren)  
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_OUT_0    Field: partial_width_out_last 
    io.field.partial_width_out_last := RegEnable(io.reg.wr_data(19, 10), "b0".asUInt(10.W), nvdla_pdp_d_partial_width_out_0_wren)   
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_OUT_0    Field: partial_width_out_mid
    io.field.partial_width_out_mid := RegEnable(io.reg.wr_data(29, 20), "b0".asUInt(10.W), nvdla_pdp_d_partial_width_out_0_wren)  
    // Register: NVDLA_PDP_D_PERF_ENABLE_0    Field: dma_en 
    io.field.dma_en := RegEnable(io.reg.wr_data(0), false.B, nvdla_pdp_d_perf_enable_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_KERNEL_CFG_0    Field: kernel_heigh
    io.field.kernel_height := RegEnable(io.reg.wr_data(11, 8), "b0".asUInt(4.W), nvdla_pdp_d_pooling_kernel_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_KERNEL_CFG_0    Field: kernel_stride_height
    io.field.kernel_stride_height := RegEnable(io.reg.wr_data(23, 20), "b0".asUInt(4.W), nvdla_pdp_d_pooling_kernel_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_KERNEL_CFG_0    Field: kernel_stride_width
    io.field.kernel_stride_width := RegEnable(io.reg.wr_data(19, 16), "b0".asUInt(4.W), nvdla_pdp_d_pooling_kernel_cfg_0_wren)  
    // Register: NVDLA_PDP_D_POOLING_KERNEL_CFG_0    Field: kernel_width 
    io.field.kernel_width := RegEnable(io.reg.wr_data(3, 0), "b0".asUInt(4.W), nvdla_pdp_d_pooling_kernel_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_PADDING_CFG_0    Field: pad_bottom
    io.field.pad_bottom := RegEnable(io.reg.wr_data(14, 12), "b0".asUInt(3.W), nvdla_pdp_d_pooling_padding_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_PADDING_CFG_0    Field: pad_left
    io.field.pad_left := RegEnable(io.reg.wr_data(2, 0), "b0".asUInt(3.W), nvdla_pdp_d_pooling_padding_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_PADDING_CFG_0    Field: pad_right
    io.field.pad_right := RegEnable(io.reg.wr_data(10, 8), "b0".asUInt(3.W), nvdla_pdp_d_pooling_padding_cfg_0_wren)  
    // Register: NVDLA_PDP_D_POOLING_PADDING_CFG_0    Field: pad_top 
    io.field.pad_top := RegEnable(io.reg.wr_data(6, 4), "b0".asUInt(3.W), nvdla_pdp_d_pooling_padding_cfg_0_wren)  
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_1_CFG_0    Field: pad_value_1x 
    io.field.pad_value_1x := RegEnable(io.reg.wr_data(18, 0), "b0".asUInt(19.W), nvdla_pdp_d_pooling_padding_value_1_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_2_CFG_0    Field: pad_value_2x
    io.field.pad_value_2x := RegEnable(io.reg.wr_data(18, 0), "b0".asUInt(19.W), nvdla_pdp_d_pooling_padding_value_2_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_3_CFG_0    Field: pad_value_3x
    io.field.pad_value_3x := RegEnable(io.reg.wr_data(18, 0), "b0".asUInt(19.W), nvdla_pdp_d_pooling_padding_value_3_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_4_CFG_0    Field: pad_value_4x
    io.field.pad_value_4x := RegEnable(io.reg.wr_data(18, 0), "b0".asUInt(19.W), nvdla_pdp_d_pooling_padding_value_4_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_5_CFG_0    Field: pad_value_5x
    io.field.pad_value_5x := RegEnable(io.reg.wr_data(18, 0), "b0".asUInt(19.W), nvdla_pdp_d_pooling_padding_value_5_cfg_0_wren)   
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_6_CFG_0    Field: pad_value_6x
    io.field.pad_value_6x := RegEnable(io.reg.wr_data(18, 0), "b0".asUInt(19.W), nvdla_pdp_d_pooling_padding_value_6_cfg_0_wren) 
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_7_CFG_0    Field: pad_value_7x  
    io.field.pad_value_7x := RegEnable(io.reg.wr_data(18, 0), "b0".asUInt(19.W), nvdla_pdp_d_pooling_padding_value_7_cfg_0_wren)   
    // Register: NVDLA_PDP_D_RECIP_KERNEL_HEIGHT_0    Field: recip_kernel_height
    io.field.recip_kernel_height := RegEnable(io.reg.wr_data(16, 0), "b0".asUInt(17.W), nvdla_pdp_d_recip_kernel_height_0_wren)   
    // Register: NVDLA_PDP_D_RECIP_KERNEL_WIDTH_0    Field: recip_kernel_width
    io.field.recip_kernel_width := RegEnable(io.reg.wr_data(16, 0), "b0".asUInt(17.W), nvdla_pdp_d_recip_kernel_width_0_wren)   
    // Register: NVDLA_PDP_D_SRC_BASE_ADDR_HIGH_0    Field: src_base_addr_high
    io.field.src_base_addr_high := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_pdp_d_src_base_addr_high_0_wren)  
    // Register: NVDLA_PDP_D_SRC_BASE_ADDR_LOW_0    Field: src_base_addr_low
    io.field.src_base_addr_low := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_pdp_d_src_base_addr_low_0_wren)   
    // Register: NVDLA_PDP_D_SRC_LINE_ST,RIDE_0    Field: src_line_stride
    io.field.src_line_stride := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_pdp_d_src_line_stride_0_wren)   
    // Register: NVDLA_PDP_D_SRC_SURFACE_STRIDE_0    Field: src_surface_stride
    io.field.src_surface_stride := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_pdp_d_src_surface_stride_0_wren)   

}}
