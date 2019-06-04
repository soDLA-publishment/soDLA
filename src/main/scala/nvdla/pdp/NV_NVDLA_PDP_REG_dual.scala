package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_REG_dual extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))
        val reg_wr_en = Input(Bool())

        // Writable register flop/trigger outputs
        val cya = Output(UInt(32.W))
        val cube_in_channel = Output(UInt(13.W))
        val cube_in_height = Output(UInt(13.W))
        val cube_in_width = Output(UInt(13.W))
        val cube_out_channel = Output(UInt(13.W))
        val cube_out_height = Output(UInt(13.W))
        val cube_out_width = Output(UInt(13.W))
        val input_data = Output(UInt(2.W))
        val dst_base_addr_high = Output(UInt(32.W))
        val dst_base_addr_low = Output(UInt(32.W))
        val dst_line_stride = Output(UInt(32.W))
        val dst_ram_type = Output(Bool())
        val dst_surface_stride = Output(UInt(32.W))
        val nan_to_zero = Output(Bool())
        val flying_mode = Output(Bool())
        val pooling_method = Output(UInt(2.W))
        val split_num = Output(UInt(8.W))
        val op_en_trigger = Output(Bool())
        val partial_width_in_first = Output(UInt(10.W))
        val partial_width_in_last = Output(UInt(10.W))
        val partial_width_in_mid = Output(UInt(10.W))
        val partial_width_out_first = Output(UInt(10.W))
        val partial_width_out_last = Output(UInt(10.W))
        val partial_width_out_mid = Output(UInt(10.W))
        val dma_en = Output(Bool())
        val kernel_height = Output(UInt(4.W))
        val kernel_stride_height = Output(UInt(4.W))
        val kernel_stride_width = Output(UInt(4.W))
        val kernel_width = Output(UInt(4.W))
        val pad_bottom = Output(UInt(3.W))
        val pad_left = Output(UInt(3.W))
        val pad_right = Output(UInt(3.W))
        val pad_top = Output(UInt(3.W))
        val pad_value_1x = Output(UInt(19.W))
        val pad_value_2x = Output(UInt(19.W))
        val pad_value_3x = Output(UInt(19.W))
        val pad_value_4x = Output(UInt(19.W))
        val pad_value_5x = Output(UInt(19.W))
        val pad_value_6x = Output(UInt(19.W))
        val pad_value_7x = Output(UInt(19.W))
        val recip_kernel_height = Output(UInt(17.W))
        val recip_kernel_width = Output(UInt(17.W))
        val src_base_addr_high = Output(UInt(32.W))
        val src_base_addr_low = Output(UInt(32.W))
        val src_line_stride = Output(UInt(32.W))
        val src_surface_stride = Output(UInt(32.W))

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
    val nvdla_pdp_d_cya_0_wren = (io.reg_offset === "9c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_data_cube_in_channel_0_wren = (io.reg_offset === "14".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_data_cube_in_height_0_wren = (io.reg_offset === "10".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_data_cube_in_width_0_wren = (io.reg_offset === "0c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_data_cube_out_channel_0_wren = (io.reg_offset === "20".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_data_cube_out_height_0_wren = (io.reg_offset === "1c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_data_cube_out_width_0_wren = (io.reg_offset === "18".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_data_format_0_wren = (io.reg_offset === "84".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_dst_base_addr_high_0_wren = (io.reg_offset === "74".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_dst_base_addr_low_0_wren = (io.reg_offset === "70".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_dst_line_stride_0_wren = (io.reg_offset === "78".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_dst_ram_cfg_0_wren = (io.reg_offset === "80".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_dst_surface_stride_0_wren = (io.reg_offset === "7c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_inf_input_num_0_wren = (io.reg_offset === "88".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_nan_flush_to_zero_0_wren = (io.reg_offset === "28".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_nan_input_num_0_wren = (io.reg_offset === "8c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_nan_output_num_0_wren = (io.reg_offset === "90".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_operation_mode_cfg_0_wren = (io.reg_offset === "24".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_op_enable_0_wren = (io.reg_offset === "08".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_partial_width_in_0_wren = (io.reg_offset === "2c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_partial_width_out_0_wren = (io.reg_offset === "30".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_perf_enable_0_wren = (io.reg_offset === "94".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_perf_write_stall_0_wren = (io.reg_offset === "98".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_pooling_kernel_cfg_0_wren = (io.reg_offset === "34".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_pooling_padding_cfg_0_wren = (io.reg_offset === "40".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_pooling_padding_value_1_cfg_0_wren = (io.reg_offset === "44".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_pooling_padding_value_2_cfg_0_wren = (io.reg_offset === "48".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_pooling_padding_value_3_cfg_0_wren = (io.reg_offset === "4c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_pooling_padding_value_4_cfg_0_wren = (io.reg_offset === "50".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_pooling_padding_value_5_cfg_0_wren = (io.reg_offset === "54".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_pooling_padding_value_6_cfg_0_wren = (io.reg_offset === "58".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_pooling_padding_value_7_cfg_0_wren = (io.reg_offset === "5c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_recip_kernel_height_0_wren = (io.reg_offset === "3c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_recip_kernel_width_0_wren = (io.reg_offset === "38".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_src_base_addr_high_0_wren = (io.reg_offset === "64".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_src_base_addr_low_0_wren = (io.reg_offset === "60".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_src_line_stride_0_wren = (io.reg_offset === "68".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_pdp_d_src_surface_stride_0_wren = (io.reg_offset === "6c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    
    val nvdla_pdp_d_cya_0_out = io.cya
    val nvdla_pdp_d_data_cube_in_channel_0_out = Cat("b0".asUInt(19.W), io.cube_in_channel) 
    val nvdla_pdp_d_data_cube_in_height_0_out = Cat("b0".asUInt(19.W), io.cube_in_height)
    val nvdla_pdp_d_data_cube_in_width_0_out = Cat("b0".asUInt(19.W), io.cube_in_width)
    val nvdla_pdp_d_data_cube_out_channel_0_out = Cat("b0".asUInt(19.W), io.cube_out_channel)
    val nvdla_pdp_d_data_cube_out_height_0_out = Cat("b0".asUInt(19.W), io.cube_out_height)
    val nvdla_pdp_d_data_cube_out_width_0_out = Cat("b0".asUInt(19.W), io.cube_out_width)
    val nvdla_pdp_d_data_format_0_out = Cat("b0".asUInt(30.W), io.input_data)
    val nvdla_pdp_d_dst_base_addr_high_0_out = io.dst_base_addr_high
//    val nvdla_pdp_d_dst_base_addr_low_0_out =  dst_base_addr_low, 5'b0 };
    val nvdla_pdp_d_dst_base_addr_low_0_out =  io.dst_base_addr_low
    val nvdla_pdp_d_dst_line_stride_0_out =  io.dst_line_stride 
    val nvdla_pdp_d_dst_ram_cfg_0_out = Cat("b0".asUInt(31.W), io.dst_ram_type)
    val nvdla_pdp_d_dst_surface_stride_0_out = io.dst_surface_stride
    val nvdla_pdp_d_inf_input_num_0_out = io.inf_input_num
    val nvdla_pdp_d_nan_flush_to_zero_0_out = Cat("b0".asUInt(31.W), io.nan_to_zero)
    val nvdla_pdp_d_nan_input_num_0_out = io.nan_input_num
    val nvdla_pdp_d_nan_output_num_0_out = io.nan_output_num
    val nvdla_pdp_d_operation_mode_cfg_0_out = Cat("b0".asUInt(16.W), io.split_num, "b0".asUInt(3.W), io.flying_mode, "b0".asUInt(2.W), io.pooling_method)
    val nvdla_pdp_d_op_enable_0_out =  Cat("b0".asUInt(31.W), io.op_en)
    val nvdla_pdp_d_partial_width_in_0_out =  Cat("b0".asUInt(2.W), io.partial_width_in_mid, io.partial_width_in_last, io.partial_width_in_first)
    val nvdla_pdp_d_partial_width_out_0_out = Cat("b0".asUInt(2.W), io.partial_width_out_mid, io.partial_width_out_last, io.partial_width_out_first)
    val nvdla_pdp_d_perf_enable_0_out = Cat("b0".asUInt(31.W), io.dma_en)
    val nvdla_pdp_d_perf_write_stall_0_out = io.perf_write_stall
    val nvdla_pdp_d_pooling_kernel_cfg_0_out = Cat("b0".asUInt(8.W), io.kernel_stride_height, io.kernel_stride_width, "b0".asUInt(4.W), io.kernel_height, "b0".asUInt(4.W), io.kernel_width)
    val nvdla_pdp_d_pooling_padding_cfg_0_out = Cat("b0".asUInt(17.W), io.pad_bottom, false.B, io.pad_right, false.B, io.pad_top, false.B, io.pad_left)
    val nvdla_pdp_d_pooling_padding_value_1_cfg_0_out = Cat("b0".asUInt(13.W), io.pad_value_1x)
    val nvdla_pdp_d_pooling_padding_value_2_cfg_0_out = Cat("b0".asUInt(13.W), io.pad_value_2x)
    val nvdla_pdp_d_pooling_padding_value_3_cfg_0_out = Cat("b0".asUInt(13.W), io.pad_value_3x)
    val nvdla_pdp_d_pooling_padding_value_4_cfg_0_out = Cat("b0".asUInt(13.W), io.pad_value_4x)
    val nvdla_pdp_d_pooling_padding_value_5_cfg_0_out = Cat("b0".asUInt(13.W), io.pad_value_5x)
    val nvdla_pdp_d_pooling_padding_value_6_cfg_0_out = Cat("b0".asUInt(13.W), io.pad_value_6x)
    val nvdla_pdp_d_pooling_padding_value_7_cfg_0_out = Cat("b0".asUInt(13.W), io.pad_value_7x)
    val nvdla_pdp_d_recip_kernel_height_0_out = Cat("b0".asUInt(15.W), io.recip_kernel_height)
    val nvdla_pdp_d_recip_kernel_width_0_out = Cat("b0".asUInt(15.W), io.recip_kernel_width)
    val nvdla_pdp_d_src_base_addr_high_0_out = io.src_base_addr_high
    val nvdla_pdp_d_src_base_addr_low_0_out = io.src_base_addr_low 
    val nvdla_pdp_d_src_line_stride_0_out = io.src_line_stride
    val nvdla_pdp_d_src_surface_stride_0_out = io.src_surface_stride

    io.op_en_trigger := nvdla_pdp_d_op_enable_0_wren

    // Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
    "h9c".asUInt(32.W)  -> nvdla_pdp_d_cya_0_out,
    "h14".asUInt(32.W)  -> nvdla_pdp_d_data_cube_in_channel_0_out,
    "h10".asUInt(32.W)  -> nvdla_pdp_d_data_cube_in_height_0_out,
    "h0c".asUInt(32.W)  -> nvdla_pdp_d_data_cube_in_width_0_out,
    "h20".asUInt(32.W)  -> nvdla_pdp_d_data_cube_out_channel_0_out,
    "h1c".asUInt(32.W)  -> nvdla_pdp_d_data_cube_out_height_0_out,
    "h18".asUInt(32.W)  -> nvdla_pdp_d_data_cube_out_width_0_out,
    "h84".asUInt(32.W)  -> nvdla_pdp_d_data_format_0_out,
    "h74".asUInt(32.W)  -> nvdla_pdp_d_dst_base_addr_high_0_out,
    "h70".asUInt(32.W)  -> nvdla_pdp_d_dst_base_addr_low_0_out,
    "h78".asUInt(32.W)  -> nvdla_pdp_d_dst_line_stride_0_out,
    "h80".asUInt(32.W)  -> nvdla_pdp_d_dst_ram_cfg_0_out,
    "h7c".asUInt(32.W)  -> nvdla_pdp_d_dst_surface_stride_0_out,
    "h88".asUInt(32.W)  -> nvdla_pdp_d_inf_input_num_0_out,
    "h28".asUInt(32.W)  -> nvdla_pdp_d_nan_flush_to_zero_0_out,
    "h8c".asUInt(32.W)  -> nvdla_pdp_d_nan_input_num_0_out,
    "h90".asUInt(32.W)  -> nvdla_pdp_d_nan_output_num_0_out,
    "h24".asUInt(32.W)  -> nvdla_pdp_d_operation_mode_cfg_0_out,
    "h08".asUInt(32.W)  -> nvdla_pdp_d_op_enable_0_out,
    "h2c".asUInt(32.W)  -> nvdla_pdp_d_partial_width_in_0_out,
    "h30".asUInt(32.W)  -> nvdla_pdp_d_partial_width_out_0_out,
    "h94".asUInt(32.W)  -> nvdla_pdp_d_perf_enable_0_out,
    "h98".asUInt(32.W)  -> nvdla_pdp_d_perf_write_stall_0_out,
    "h34".asUInt(32.W)  -> nvdla_pdp_d_pooling_kernel_cfg_0_out,
    "h40".asUInt(32.W)  -> nvdla_pdp_d_pooling_padding_cfg_0_out,
    "h44".asUInt(32.W)  -> nvdla_pdp_d_pooling_padding_value_1_cfg_0_out,
    "h48".asUInt(32.W)  -> nvdla_pdp_d_pooling_padding_value_2_cfg_0_out,
    "h4c".asUInt(32.W)  -> nvdla_pdp_d_pooling_padding_value_3_cfg_0_out,
    "h50".asUInt(32.W)  -> nvdla_pdp_d_pooling_padding_value_4_cfg_0_out,
    "h54".asUInt(32.W)  -> nvdla_pdp_d_pooling_padding_value_5_cfg_0_out,
    "h58".asUInt(32.W)  -> nvdla_pdp_d_pooling_padding_value_6_cfg_0_out,
    "h5c".asUInt(32.W)  -> nvdla_pdp_d_pooling_padding_value_7_cfg_0_out,
    "h3c".asUInt(32.W)  -> nvdla_pdp_d_recip_kernel_height_0_out,
    "h38".asUInt(32.W)  -> nvdla_pdp_d_recip_kernel_width_0_out,
    "h64".asUInt(32.W)  -> nvdla_pdp_d_src_base_addr_high_0_out,
    "h60".asUInt(32.W)  -> nvdla_pdp_d_src_base_addr_low_0_out,
    "h68".asUInt(32.W)  -> nvdla_pdp_d_src_line_stride_0_out,
    "h6c".asUInt(32.W)  -> nvdla_pdp_d_src_surface_stride_0_out
    ))

    // Register flop declarations

    val cya_out = RegInit("b0".asUInt(32.W))
    val cube_in_channel_out = RegInit("b0".asUInt(13.W))
    val cube_in_height_out = RegInit("b0".asUInt(13.W))
    val cube_in_width_out = RegInit("b0".asUInt(13.W))
    val cube_out_channel_out = RegInit("b0".asUInt(13.W))
    val cube_out_height_out = RegInit("b0".asUInt(13.W))
    val cube_out_width_out = RegInit("b0".asUInt(13.W))
    val input_data_out = RegInit("b0".asUInt(2.W))
    val dst_base_addr_high_out = RegInit("b0".asUInt(32.W))
    val dst_base_addr_low_out = RegInit("b0".asUInt(32.W))
    val dst_line_stride_out = RegInit("b0".asUInt(32.W))
    val dst_ram_type_out = RegInit(false.B)
    val dst_surface_stride_out = RegInit("b0".asUInt(32.W))
    val nan_to_zero_out = RegInit(false.B)
    val flying_mode_out = RegInit(false.B)
    val pooling_method_out = RegInit("b0".asUInt(2.W))
    val split_num_out = RegInit("b0".asUInt(8.W))
    val partial_width_in_first_out = RegInit("b0".asUInt(10.W))
    val partial_width_in_last_out = RegInit("b0".asUInt(10.W))
    val partial_width_in_mid_out = RegInit("b0".asUInt(10.W))
    val partial_width_out_first_out = RegInit("b0".asUInt(10.W))
    val partial_width_out_last_out = RegInit("b0".asUInt(10.W))
    val partial_width_out_mid_out = RegInit("b0".asUInt(10.W))
    val dma_en_out = RegInit(false.B)
    val kernel_height_out = RegInit("b0".asUInt(4.W))
    val kernel_stride_height_out = RegInit("b0".asUInt(4.W))
    val kernel_stride_width_out = RegInit("b0".asUInt(4.W))
    val kernel_width_out = RegInit("b0".asUInt(4.W))
    val pad_bottom_out = RegInit("b0".asUInt(3.W))
    val pad_left_out = RegInit("b0".asUInt(3.W))
    val pad_right_out = RegInit("b0".asUInt(3.W))
    val pad_top_out = RegInit("b0".asUInt(3.W))
    val pad_value_1x_out = RegInit("b0".asUInt(19.W))
    val pad_value_2x_out = RegInit("b0".asUInt(19.W))
    val pad_value_3x_out = RegInit("b0".asUInt(19.W))
    val pad_value_4x_out = RegInit("b0".asUInt(19.W))
    val pad_value_5x_out = RegInit("b0".asUInt(19.W))
    val pad_value_6x_out = RegInit("b0".asUInt(19.W))
    val pad_value_7x_out = RegInit("b0".asUInt(19.W))
    val recip_kernel_height_out = RegInit("b0".asUInt(17.W))
    val recip_kernel_width_out = RegInit("b0".asUInt(17.W))
    val src_base_addr_high_out = RegInit("b0".asUInt(32.W))
    val src_base_addr_low_out = RegInit("b0".asUInt(32.W))
    val src_line_stride_out = RegInit("b0".asUInt(32.W))
    val src_surface_stride_out = RegInit("b0".asUInt(32.W))

    // Register: NVDLA_PDP_D_CYA_0    Field: cya
    when(nvdla_pdp_d_cya_0_wren){
        cya_out := io.reg_wr_data(31, 0)
    }
    // Register: NVDLA_PDP_D_DATA_CUBE_IN_CHANNEL_0    Field: cube_in_channel
    when(nvdla_pdp_d_data_cube_in_channel_0_wren){
        cube_in_channel_out:= io.reg_wr_data(12, 0)
    }
    // Register: NVDLA_PDP_D_DATA_CUBE_IN_HEIGHT_0    Field: cube_in_height
    when(nvdla_pdp_d_data_cube_in_height_0_wren){
        cube_in_height_out:= io.reg_wr_data(12, 0)
    }
    // Register: NVDLA_PDP_D_DATA_CUBE_IN_WIDTH_0    Field: cube_in_width
    when(nvdla_pdp_d_data_cube_in_width_0_wren){
        cube_in_width_out:= io.reg_wr_data(12, 0)
    }
    // Register: NVDLA_PDP_D_DATA_CUBE_OUT_CHANNEL_0    Field: cube_out_channel
    when(nvdla_pdp_d_data_cube_out_channel_0_wren){
        cube_out_channel_out:= io.reg_wr_data(12, 0)
    }
    // Register: NVDLA_PDP_D_DATA_CUBE_OUT_HEIGHT_0    Field: cube_out_height
    when(nvdla_pdp_d_data_cube_out_height_0_wren){
        cube_out_height_out:= io.reg_wr_data(12, 0)
    }
    // Register: NVDLA_PDP_D_DATA_CUBE_OUT_WIDTH_0    Field: cube_out_width
    when(nvdla_pdp_d_data_cube_out_width_0_wren){
        cube_out_width_out:= io.reg_wr_data(12, 0)
    }
    // Register: NVDLA_PDP_D_DATA_FORMAT_0    Field: input_data
    when(nvdla_pdp_d_data_format_0_wren){
        input_data_out:= io.reg_wr_data(1, 0)
    }
    // Register: NVDLA_PDP_D_DST_BASE_ADDR_HIGH_0    Field: dst_base_addr_high
    when(nvdla_pdp_d_dst_base_addr_high_0_wren){
        dst_base_addr_high_out:= io.reg_wr_data(31, 0)
    }
    // Register: NVDLA_PDP_D_DST_BASE_ADDR_LOW_0    Field: dst_base_addr_low
    when(nvdla_pdp_d_dst_base_addr_low_0_wren){
        dst_base_addr_low_out:= io.reg_wr_data(31, 0)
    }
    // Register: NVDLA_PDP_D_DST_LINE_STRIDE_0    Field: dst_line_stride
    when(nvdla_pdp_d_dst_line_stride_0_wren){
        dst_line_stride_out:= io.reg_wr_data(31, 0)
    }
    // Register: NVDLA_PDP_D_DST_RAM_CFG_0    Field: dst_ram_type
    when(nvdla_pdp_d_dst_ram_cfg_0_wren){
        dst_ram_type_out:= io.reg_wr_data(0)
    }
    // Register: NVDLA_PDP_D_DST_SURFACE_STRIDE_0    Field: dst_surface_stride
    when(nvdla_pdp_d_dst_surface_stride_0_wren){
        dst_surface_stride_out:= io.reg_wr_data(31, 0)
    }
    // Not generating flops for read-only field NVDLA_PDP_D_INF_INPUT_NUM_0::inf_input_num
    // Register: NVDLA_PDP_D_NAN_FLUSH_TO_ZERO_0    Field: nan_to_zero
    when(nvdla_pdp_d_nan_flush_to_zero_0_wren){
        nan_to_zero_out:= io.reg_wr_data(0)
    }
    // Not generating flops for read-only field NVDLA_PDP_D_NAN_INPUT_NUM_0::nan_input_num

    // Not generating flops for read-only field NVDLA_PDP_D_NAN_OUTPUT_NUM_0::nan_output_num

    // Register: NVDLA_PDP_D_OPERATION_MODE_CFG_0    Field: flying_mode
    when(nvdla_pdp_d_operation_mode_cfg_0_wren){
        flying_mode_out:= io.reg_wr_data(4)
    }
    // Register: NVDLA_PDP_D_OPERATION_MODE_CFG_0    Field: pooling_method
    when(nvdla_pdp_d_operation_mode_cfg_0_wren){
        pooling_method_out:= io.reg_wr_data(1, 0)
    }
    // Register: NVDLA_PDP_D_OPERATION_MODE_CFG_0    Field: split_num
    when(nvdla_pdp_d_operation_mode_cfg_0_wren){
        split_num_out:= io.reg_wr_data(15, 8)
    }

    // Not generating flops for field NVDLA_PDP_D_OP_ENABLE_0::op_en (to be implemented outside)

    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_IN_0    Field: partial_width_in_first
    when(nvdla_pdp_d_partial_width_in_0_wren){
        partial_width_in_first_out:= io.reg_wr_data(9, 0)
    }
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_IN_0    Field: partial_width_in_last
    when(nvdla_pdp_d_partial_width_in_0_wren){
        partial_width_in_last_out:= io.reg_wr_data(19, 10)
    }
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_IN_0    Field: partial_width_in_mid
    when(nvdla_pdp_d_partial_width_in_0_wren){
        partial_width_in_mid_out:= io.reg_wr_data(29, 20)
    }
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_OUT_0    Field: partial_width_out_first
    when(nvdla_pdp_d_partial_width_out_0_wren){
        partial_width_out_first_out:= io.reg_wr_data(9, 0)
    }
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_OUT_0    Field: partial_width_out_last
    when(nvdla_pdp_d_partial_width_out_0_wren){
        partial_width_out_last_out:= io.reg_wr_data(19, 10)
    }
    // Register: NVDLA_PDP_D_PARTIAL_WIDTH_OUT_0    Field: partial_width_out_mid
    when(nvdla_pdp_d_partial_width_out_0_wren){
        partial_width_out_mid_out:= io.reg_wr_data(29, 20)
    }
    // Register: NVDLA_PDP_D_PERF_ENABLE_0    Field: dma_en
    when(nvdla_pdp_d_perf_enable_0_wren){
        dma_en_out:= io.reg_wr_data(0)
    }
    // Not generating flops for read-only field NVDLA_PDP_D_PERF_WRITE_STALL_0::perf_write_stall
    // Register: NVDLA_PDP_D_POOLING_KERNEL_CFG_0    Field: kernel_heigh
    when(nvdla_pdp_d_pooling_kernel_cfg_0_wren){
        kernel_height_out:= io.reg_wr_data(11, 8)
    }
    // Register: NVDLA_PDP_D_POOLING_KERNEL_CFG_0    Field: kernel_stride_height
    when(nvdla_pdp_d_pooling_kernel_cfg_0_wren){
        kernel_stride_height_out:= io.reg_wr_data(23, 20)
    }
    // Register: NVDLA_PDP_D_POOLING_KERNEL_CFG_0    Field: kernel_stride_width
    when(nvdla_pdp_d_pooling_kernel_cfg_0_wren){
        kernel_stride_width_out:= io.reg_wr_data(19, 16)
    }
    // Register: NVDLA_PDP_D_POOLING_KERNEL_CFG_0    Field: kernel_width
    when(nvdla_pdp_d_pooling_kernel_cfg_0_wren){
        kernel_width_out:= io.reg_wr_data(3, 0)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_CFG_0    Field: pad_bottom
    when(nvdla_pdp_d_pooling_padding_cfg_0_wren){
        pad_bottom_out:= io.reg_wr_data(14, 12)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_CFG_0    Field: pad_left
    when(nvdla_pdp_d_pooling_padding_cfg_0_wren){
        pad_left_out:= io.reg_wr_data(2, 0)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_CFG_0    Field: pad_right
    when(nvdla_pdp_d_pooling_padding_cfg_0_wren){
        pad_right_out:= io.reg_wr_data(10, 8)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_CFG_0    Field: pad_top
    when(nvdla_pdp_d_pooling_padding_cfg_0_wren){
        pad_top_out:= io.reg_wr_data(6, 4)
    }

    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_1_CFG_0    Field: pad_value_1x
    when(nvdla_pdp_d_pooling_padding_value_1_cfg_0_wren){
        pad_value_1x_out:= io.reg_wr_data(18, 0)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_2_CFG_0    Field: pad_value_2x
    when(nvdla_pdp_d_pooling_padding_value_2_cfg_0_wren){
        pad_value_2x_out:= io.reg_wr_data(18, 0)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_3_CFG_0    Field: pad_value_3x
    when(nvdla_pdp_d_pooling_padding_value_3_cfg_0_wren){
        pad_value_3x_out:= io.reg_wr_data(18, 0)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_4_CFG_0    Field: pad_value_4x
    when(nvdla_pdp_d_pooling_padding_value_4_cfg_0_wren){
        pad_value_4x_out:= io.reg_wr_data(18, 0)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_5_CFG_0    Field: pad_value_5x
    when(nvdla_pdp_d_pooling_padding_value_5_cfg_0_wren){
        pad_value_5x_out:= io.reg_wr_data(18, 0)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_6_CFG_0    Field: pad_value_6x
    when(nvdla_pdp_d_pooling_padding_value_6_cfg_0_wren){
        pad_value_6x_out:= io.reg_wr_data(18, 0)
    }
    // Register: NVDLA_PDP_D_POOLING_PADDING_VALUE_7_CFG_0    Field: pad_value_7x
    when(nvdla_pdp_d_pooling_padding_value_7_cfg_0_wren){
        pad_value_7x_out:= io.reg_wr_data(18, 0)
    }
    // Register: NVDLA_PDP_D_RECIP_KERNEL_HEIGHT_0    Field: recip_kernel_height
    when(nvdla_pdp_d_recip_kernel_height_0_wren){
        recip_kernel_height_out:= io.reg_wr_data(16, 0)
    }
    // Register: NVDLA_PDP_D_RECIP_KERNEL_WIDTH_0    Field: recip_kernel_width
    when(nvdla_pdp_d_recip_kernel_width_0_wren){
        recip_kernel_width_out:= io.reg_wr_data(16, 0)
    }
    // Register: NVDLA_PDP_D_SRC_BASE_ADDR_HIGH_0    Field: src_base_addr_high
    when(nvdla_pdp_d_src_base_addr_high_0_wren){
        src_base_addr_high_out:= io.reg_wr_data(31, 0)
    }
    // Register: NVDLA_PDP_D_SRC_BASE_ADDR_LOW_0    Field: src_base_addr_low
    when(nvdla_pdp_d_src_base_addr_low_0_wren){
        src_base_addr_low_out:= io.reg_wr_data(31, 0)
    }
    // Register: NVDLA_PDP_D_SRC_LINE_STRIDE_0    Field: src_line_stride
    when(nvdla_pdp_d_src_line_stride_0_wren){
        src_line_stride_out:= io.reg_wr_data(31, 0)
    }
    // Register: NVDLA_PDP_D_SRC_SURFACE_STRIDE_0    Field: src_surface_stride
    when(nvdla_pdp_d_src_surface_stride_0_wren){
        src_surface_stride_out:= io.reg_wr_data(31, 0)
    }

    io.cya := cya_out
    io.cube_in_channel := cube_in_channel_out
    io.cube_in_height := cube_in_height_out
    io.cube_in_width := cube_in_width_out
    io.cube_out_channel := cube_out_channel_out
    io.cube_out_height := cube_out_height_out
    io.cube_out_width := cube_out_width_out
    io.input_data := input_data_out
    io.dst_base_addr_high := dst_base_addr_high_out
    io.dst_base_addr_low := dst_base_addr_low_out
    io.dst_line_stride := dst_line_stride_out
    io.dst_ram_type := dst_ram_type_out
    io.dst_surface_stride := dst_surface_stride_out
    io.nan_to_zero := nan_to_zero_out
    io.flying_mode := flying_mode_out
    io.pooling_method := pooling_method_out
    io.split_num := split_num_out
    io.partial_width_in_first := partial_width_in_first_out
    io.partial_width_in_last := partial_width_in_last_out
    io.partial_width_in_mid := partial_width_in_mid_out
    io.partial_width_out_first := partial_width_out_first_out
    io.partial_width_out_last := partial_width_out_last_out
    io.partial_width_out_mid := partial_width_out_mid_out
    io.dma_en := dma_en_out
    io.kernel_height := kernel_height_out
    io.kernel_stride_height := kernel_stride_height_out
    io.kernel_stride_width := kernel_stride_width_out
    io.kernel_width := kernel_width_out
    io.pad_bottom := pad_bottom_out
    io.pad_left := pad_left_out
    io.pad_right := pad_right_out
    io.pad_top := pad_top_out
    io.pad_value_1x := pad_value_1x_out
    io.pad_value_2x := pad_value_2x_out
    io.pad_value_3x := pad_value_3x_out
    io.pad_value_4x := pad_value_4x_out
    io.pad_value_5x := pad_value_5x_out
    io.pad_value_6x := pad_value_6x_out
    io.pad_value_7x := pad_value_7x_out
    io.recip_kernel_height := recip_kernel_height_out
    io.recip_kernel_width := recip_kernel_width_out
    io.src_base_addr_high := src_base_addr_high_out
    io.src_base_addr_low := src_base_addr_low_out
    io.src_line_stride := src_line_stride_out
    io.src_surface_stride := src_surface_stride_out    

}}