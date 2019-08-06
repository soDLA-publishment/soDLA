// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDMA_dual_reg extends Module{
//     val io = IO(new Bundle{
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         //Register control interface
//         val reg_rd_data = Output(UInt(32.W))
//         val reg_offset = Input(UInt(12.W))
//         val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)
//         val reg_wr_en = Input(Bool())

//         //Writable register flop/trigger outputs
//         val data_bank = Output(UInt(5.W))
//         val weight_bank = Output(UInt(5.W))
//         val batches = Output(UInt(5.W))
//         val batch_stride = Output(UInt(32.W))
//         val conv_x_stride = Output(UInt(3.W))
//         val conv_y_stride = Output(UInt(3.W))
//         val cvt_en = Output(Bool())
//         val cvt_truncate = Output(UInt(6.W))
//         val cvt_offset = Output(UInt(16.W))
//         val cvt_scale = Output(UInt(16.W))
//         val cya = Output(UInt(32.W))
//         val datain_addr_high_0 = Output(UInt(32.W))
//         val datain_addr_high_1 = Output(UInt(32.W))
//         val datain_addr_low_0 = Output(UInt(32.W))
//         val datain_addr_low_1 = Output(UInt(32.W))
//         val line_packed = Output(Bool())
//         val surf_packed = Output(Bool())
//         val datain_ram_type = Output(Bool())
//         val datain_format = Output(Bool())
//         val pixel_format = Output(UInt(6.W))
//         val pixel_mapping = Output(Bool())
//         val pixel_sign_override = Output(Bool())
//         val datain_height = Output(UInt(13.W))
//         val datain_width = Output(UInt(13.W))
//         val datain_channel = Output(UInt(13.W))
//         val datain_height_ext = Output(UInt(13.W))
//         val datain_width_ext = Output(UInt(13.W))
//         val entries = Output(UInt(14.W))
//         val grains = Output(UInt(12.W))
//         val line_stride = Output(UInt(32.W))
//         val uv_line_stride = Output(UInt(32.W))
//         val mean_format = Output(Bool())
//         val mean_gu = Output(UInt(16.W))
//         val mean_ry = Output(UInt(16.W))
//         val mean_ax = Output(UInt(16.W))
//         val mean_bv = Output(UInt(16.W))
//         val conv_mode = Output(Bool())
//         val data_reuse = Output(Bool())
//         val in_precision = Output(UInt(2.W))
//         val proc_precision = Output(UInt(2.W))
//         val skip_data_rls = Output(Bool())
//         val skip_weight_rls = Output(Bool())
//         val weight_reuse = Output(Bool())
//         val nan_to_zero = Output(Bool())
//         val op_en_trigger = Output(Bool())
//         val dma_en = Output(Bool())
//         val pixel_x_offset = Output(UInt(5.W))
//         val pixel_y_offset = Output(UInt(3.W))
//         val rsv_per_line = Output(UInt(10.W))
//         val rsv_per_uv_line = Output(UInt(10.W))
//         val rsv_height = Output(UInt(3.W))
//         val rsv_y_index = Output(UInt(5.W))
//         val surf_stride = Output(UInt(32.W))
//         val weight_addr_high = Output(UInt(32.W))
//         val weight_addr_low = Output(UInt(32.W))
//         val weight_bytes = Output(UInt(32.W))
//         val weight_format = Output(Bool())
//         val weight_ram_type = Output(Bool())
//         val byte_per_kernel = Output(UInt(18.W))
//         val weight_kernel = Output(UInt(13.W))
//         val wgs_addr_high = Output(UInt(32.W))
//         val wgs_addr_low = Output(UInt(32.W))
//         val wmb_addr_high = Output(UInt(32.W))
//         val wmb_addr_low = Output(UInt(32.W))
//         val wmb_bytes = Output(UInt(28.W))
//         val pad_bottom = Output(UInt(6.W))
//         val pad_left = Output(UInt(5.W))
//         val pad_right = Output(UInt(6.W))
//         val pad_top = Output(UInt(5.W))
//         val pad_value = Output(UInt(16.W))

//         //Read-only register inputs
//         val inf_data_num = Input(UInt(32.W))
//         val inf_weight_num = Input(UInt(32.W))
//         val nan_data_num = Input(UInt(32.W))
//         val nan_weight_num = Input(UInt(32.W))
//         val op_en = Input(Bool())
//         val dat_rd_latency = Input(UInt(32.W))
//         val dat_rd_stall = Input(UInt(32.W))
//         val wt_rd_latency = Input(UInt(32.W))
//         val wt_rd_stall = Input(UInt(32.W)) 
//       })
    
//     //      ┌─┐       ┌─┐
//     //   ┌──┘ ┴───────┘ ┴──┐
//     //   │                 │
//     //   │       ───       │
//     //   │  ─┬┘       └┬─  │
//     //   │                 │
//     //   │       ─┴─       │
//     //   │                 │
//     //   └───┐         ┌───┘
//     //       │         │
//     //       │         │
//     //       │         │
//     //       │         └──────────────┐
//     //       │                        │
//     //       │                        ├─┐
//     //       │                        ┌─┘    
//     //       │                        │
//     //       └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //         │ ─┤ ─┤       │ ─┤ ─┤         
//     //         └──┴──┘       └──┴──┘ 
//     withClock(io.nvdla_core_clk){

//     // Address decode
//     val nvdla_cdma_d_bank_0_wren = (io.reg_offset === "hbc".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_batch_number_0_wren = (io.reg_offset === "h58".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_batch_stride_0_wren = (io.reg_offset === "h5c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_conv_stride_0_wren = (io.reg_offset === "hb0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_cvt_cfg_0_wren = (io.reg_offset === "ha4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_cvt_offset_0_wren = (io.reg_offset === "ha8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_cvt_scale_0_wren = (io.reg_offset === "hac".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_cya_0_wren = (io.reg_offset === "he8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_dain_addr_high_0_0_wren = (io.reg_offset === "h30".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_dain_addr_high_1_0_wren = (io.reg_offset === "h38".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_dain_addr_low_0_0_wren = (io.reg_offset === "h34".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_dain_addr_low_1_0_wren = (io.reg_offset === "h3c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_dain_map_0_wren = (io.reg_offset === "h4c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_dain_ram_type_0_wren = (io.reg_offset === "h2c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_datain_format_0_wren = (io.reg_offset === "h18".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_datain_size_0_0_wren = (io.reg_offset === "h1c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_datain_size_1_0_wren = (io.reg_offset === "h20".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_datain_size_ext_0_0_wren = (io.reg_offset === "h24".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_entry_per_slice_0_wren = (io.reg_offset === "h60".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_fetch_grain_0_wren = (io.reg_offset === "h64".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_inf_input_data_num_0_wren = (io.reg_offset === "hcc".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_inf_input_weight_num_0_wren = (io.reg_offset === "hd0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_line_stride_0_wren = (io.reg_offset === "h40".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_line_uv_stride_0_wren = (io.reg_offset === "h44".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_mean_format_0_wren = (io.reg_offset === "h98".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_mean_global_0_0_wren = (io.reg_offset === "h9c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_mean_global_1_0_wren = (io.reg_offset === "ha0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_misc_cfg_0_wren = (io.reg_offset === "h14".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_nan_flush_to_zero_0_wren = (io.reg_offset === "hc0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_nan_input_data_num_0_wren = (io.reg_offset === "hc4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_nan_input_weight_num_0_wren = (io.reg_offset === "hc8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_op_enable_0_wren = (io.reg_offset === "h10".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_perf_dat_read_latency_0_wren = (io.reg_offset === "he0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_perf_dat_read_stall_0_wren = (io.reg_offset === "hd8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_perf_enable_0_wren = (io.reg_offset === "hd4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_perf_wt_read_latency_0_wren = (io.reg_offset === "he4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_perf_wt_read_stall_0_wren = (io.reg_offset === "hdc".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_pixel_offset_0_wren = (io.reg_offset === "h28".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_reserved_x_cfg_0_wren = (io.reg_offset === "h50".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_reserved_y_cfg_0_wren = (io.reg_offset === "h54".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_surf_stride_0_wren = (io.reg_offset === "h48".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_weight_addr_high_0_wren = (io.reg_offset === "h78".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_weight_addr_low_0_wren = (io.reg_offset === "h7c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_weight_bytes_0_wren = (io.reg_offset === "h80".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_weight_format_0_wren = (io.reg_offset === "h68".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_weight_ram_type_0_wren = (io.reg_offset === "h74".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_weight_size_0_0_wren = (io.reg_offset === "h6c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_weight_size_1_0_wren = (io.reg_offset === "h70".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_wgs_addr_high_0_wren = (io.reg_offset === "h84".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_wgs_addr_low_0_wren = (io.reg_offset === "h88".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_wmb_addr_high_0_wren = (io.reg_offset === "h8c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_wmb_addr_low_0_wren = (io.reg_offset === "h90".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_wmb_bytes_0_wren = (io.reg_offset === "h94".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_zero_padding_0_wren = (io.reg_offset === "hb4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_cdma_d_zero_padding_value_0_wren = (io.reg_offset === "hb8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)


//     val nvdla_cdma_d_bank_0_out = Cat("b0".asUInt(11.W), io.weight_bank, "b0".asUInt(11.W), io.data_bank)
//     val nvdla_cdma_d_batch_number_0_out = Cat("b0".asUInt(27.W), io.batches)
//     val nvdla_cdma_d_batch_stride_0_out = Cat( io.batch_stride)
//     val nvdla_cdma_d_conv_stride_0_out = Cat("b0".asUInt(13.W), io.conv_y_stride, "b0".asUInt(13.W), io.conv_x_stride)
//     val nvdla_cdma_d_cvt_cfg_0_out = Cat("b0".asUInt(22.W), io.cvt_truncate,"b0".asUInt(3.W), io.cvt_en)
//     val nvdla_cdma_d_cvt_offset_0_out = Cat("b0".asUInt(16.W), io.cvt_offset)
//     val nvdla_cdma_d_cvt_scale_0_out = Cat("b0".asUInt(16.W), io.cvt_scale)
//     val nvdla_cdma_d_cya_0_out = Cat( io.cya)
//     val nvdla_cdma_d_dain_addr_high_0_0_out = Cat( io.datain_addr_high_0)
//     val nvdla_cdma_d_dain_addr_high_1_0_out = Cat( io.datain_addr_high_1)
//     val nvdla_cdma_d_dain_addr_low_0_0_out = Cat( io.datain_addr_low_0)
//     val nvdla_cdma_d_dain_addr_low_1_0_out = Cat( io.datain_addr_low_1)
//     val nvdla_cdma_d_dain_map_0_out = Cat("b0".asUInt(15.W), io.surf_packed, "b0".asUInt(15.W), io.line_packed)
//     val nvdla_cdma_d_dain_ram_type_0_out = Cat("b0".asUInt(31.W), io.datain_ram_type)
//     val nvdla_cdma_d_datain_format_0_out = Cat( "b0".asUInt(11.W), io.pixel_sign_override, "b0".asUInt(3.W), io.pixel_mapping, "b0".asUInt(2.W), io.pixel_format, "b0".asUInt(7.W), io.datain_format)
//     val nvdla_cdma_d_datain_size_0_0_out = Cat( "b0".asUInt(3.W), io.datain_height, "b0".asUInt(3.W), io.datain_width)
//     val nvdla_cdma_d_datain_size_1_0_out = Cat( "b0".asUInt(19.W), io.datain_channel)
//     val nvdla_cdma_d_datain_size_ext_0_0_out = Cat( "b0".asUInt(3.W), io.datain_height_ext, "b0".asUInt(3.W), io.datain_width_ext)
//     val nvdla_cdma_d_entry_per_slice_0_out = Cat( "b0".asUInt(18.W), io.entries)
//     val nvdla_cdma_d_fetch_grain_0_out = Cat( "b0".asUInt(20.W), io.grains)
//     val nvdla_cdma_d_inf_input_data_num_0_out = Cat( io.inf_data_num)
//     val nvdla_cdma_d_inf_input_weight_num_0_out = Cat( io.inf_weight_num)
//     val nvdla_cdma_d_line_stride_0_out = Cat( io.line_stride)
//     val nvdla_cdma_d_line_uv_stride_0_out = Cat( io.uv_line_stride)
//     val nvdla_cdma_d_mean_format_0_out = Cat( "b0".asUInt(31.W), io.mean_format)
//     val nvdla_cdma_d_mean_global_0_0_out = Cat( io.mean_gu, io.mean_ry)
//     val nvdla_cdma_d_mean_global_1_0_out = Cat( io.mean_ax, io.mean_bv)
//     val nvdla_cdma_d_misc_cfg_0_out = Cat( "b0".asUInt(3.W), io.skip_weight_rls, "b0".asUInt(3.W), io.skip_data_rls, "b0".asUInt(3.W), io.weight_reuse, "b0".asUInt(3.W), io.data_reuse, "b0".asUInt(2.W), io.proc_precision, "b0".asUInt(2.W), io.in_precision, "b0".asUInt(7.W), io.conv_mode)
//     val nvdla_cdma_d_nan_flush_to_zero_0_out = Cat( "b0".asUInt(31.W), io.nan_to_zero)
//     val nvdla_cdma_d_nan_input_data_num_0_out = Cat( io.nan_data_num)
//     val nvdla_cdma_d_nan_input_weight_num_0_out = Cat( io.nan_weight_num)
//     val nvdla_cdma_d_op_enable_0_out = Cat( "b0".asUInt(31.W), io.op_en)
//     val nvdla_cdma_d_perf_dat_read_latency_0_out = Cat( io.dat_rd_latency)
//     val nvdla_cdma_d_perf_dat_read_stall_0_out = Cat( io.dat_rd_stall)
//     val nvdla_cdma_d_perf_enable_0_out = Cat( "b0".asUInt(31.W), io.dma_en)
//     val nvdla_cdma_d_perf_wt_read_latency_0_out = Cat( io.wt_rd_latency)
//     val nvdla_cdma_d_perf_wt_read_stall_0_out = Cat( io.wt_rd_stall)
//     val nvdla_cdma_d_pixel_offset_0_out = Cat( "b0".asUInt(13.W), io.pixel_y_offset, "b0".asUInt(11.W), io.pixel_x_offset)
//     val nvdla_cdma_d_reserved_x_cfg_0_out = Cat( "b0".asUInt(6.W), io.rsv_per_uv_line, "b0".asUInt(6.W), io.rsv_per_line)
//     val nvdla_cdma_d_reserved_y_cfg_0_out = Cat( "b0".asUInt(11.W), io.rsv_y_index, "b0".asUInt(13.W), io.rsv_height)
//     val nvdla_cdma_d_surf_stride_0_out = Cat( io.surf_stride)
//     val nvdla_cdma_d_weight_addr_high_0_out = Cat( io.weight_addr_high)
//     val nvdla_cdma_d_weight_addr_low_0_out = Cat( io.weight_addr_low)
//     val nvdla_cdma_d_weight_bytes_0_out = Cat( io.weight_bytes)
//     val nvdla_cdma_d_weight_format_0_out = Cat( "b0".asUInt(31.W), io.weight_format)
//     val nvdla_cdma_d_weight_ram_type_0_out = Cat( "b0".asUInt(31.W), io.weight_ram_type)
//     val nvdla_cdma_d_weight_size_0_0_out = Cat( "b0".asUInt(14.W), io.byte_per_kernel)
//     val nvdla_cdma_d_weight_size_1_0_out = Cat( "b0".asUInt(19.W), io.weight_kernel)
//     val nvdla_cdma_d_wgs_addr_high_0_out = Cat( io.wgs_addr_high)
//     val nvdla_cdma_d_wgs_addr_low_0_out = Cat( io.wgs_addr_low)
//     val nvdla_cdma_d_wmb_addr_high_0_out = Cat( io.wmb_addr_high)
//     val nvdla_cdma_d_wmb_addr_low_0_out = Cat( io.wmb_addr_low)
//     val nvdla_cdma_d_wmb_bytes_0_out = Cat( "b0".asUInt(4.W), io.wmb_bytes)
//     val nvdla_cdma_d_zero_padding_0_out = Cat( "b0".asUInt(2.W), io.pad_bottom, "b0".asUInt(3.W), io.pad_top, "b0".asUInt(2.W), io.pad_right, "b0".asUInt(3.W), io.pad_left)
//     val nvdla_cdma_d_zero_padding_value_0_out = Cat( "b0".asUInt(16.W), io.pad_value)

//     io.op_en_trigger := nvdla_cdma_d_op_enable_0_wren

//     //Output mux

//     io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
//     Seq(      
//         "hbc".asUInt(32.W)  -> nvdla_cdma_d_bank_0_out,
//         "h58".asUInt(32.W)  -> nvdla_cdma_d_batch_number_0_out,
//         "h5c".asUInt(32.W)  -> nvdla_cdma_d_batch_stride_0_out,
//         "hb0".asUInt(32.W)  -> nvdla_cdma_d_conv_stride_0_out,
//         "ha4".asUInt(32.W)  -> nvdla_cdma_d_cvt_cfg_0_out,
//         "ha8".asUInt(32.W)  -> nvdla_cdma_d_cvt_offset_0_out,
//         "hac".asUInt(32.W)  -> nvdla_cdma_d_cvt_scale_0_out,
//         "he8".asUInt(32.W)  -> nvdla_cdma_d_cya_0_out,
//         "h30".asUInt(32.W)  -> nvdla_cdma_d_dain_addr_high_0_0_out,
//         "h38".asUInt(32.W)  -> nvdla_cdma_d_dain_addr_high_1_0_out,
//         "h34".asUInt(32.W)  -> nvdla_cdma_d_dain_addr_low_0_0_out,
//         "h3c".asUInt(32.W)  -> nvdla_cdma_d_dain_addr_low_1_0_out,
//         "h4c".asUInt(32.W)  -> nvdla_cdma_d_dain_map_0_out,
//         "h2c".asUInt(32.W)  -> nvdla_cdma_d_dain_ram_type_0_out,
//         "h18".asUInt(32.W)  -> nvdla_cdma_d_datain_format_0_out,
//         "h1c".asUInt(32.W)  -> nvdla_cdma_d_datain_size_0_0_out,
//         "h20".asUInt(32.W)  -> nvdla_cdma_d_datain_size_1_0_out,
//         "h24".asUInt(32.W)  -> nvdla_cdma_d_datain_size_ext_0_0_out,
//         "h60".asUInt(32.W)  -> nvdla_cdma_d_entry_per_slice_0_out,
//         "h64".asUInt(32.W)  -> nvdla_cdma_d_fetch_grain_0_out,
//         "hcc".asUInt(32.W)  -> nvdla_cdma_d_inf_input_data_num_0_out,
//         "hd0".asUInt(32.W)  -> nvdla_cdma_d_inf_input_weight_num_0_out,
//         "h40".asUInt(32.W)  -> nvdla_cdma_d_line_stride_0_out,
//         "h44".asUInt(32.W)  -> nvdla_cdma_d_line_uv_stride_0_out,
//         "h98".asUInt(32.W)  -> nvdla_cdma_d_mean_format_0_out,
//         "h9c".asUInt(32.W)  -> nvdla_cdma_d_mean_global_0_0_out,
//         "ha0".asUInt(32.W)  -> nvdla_cdma_d_mean_global_1_0_out,
//         "h14".asUInt(32.W)  -> nvdla_cdma_d_misc_cfg_0_out,
//         "hc0".asUInt(32.W)  -> nvdla_cdma_d_nan_flush_to_zero_0_out,
//         "hc4".asUInt(32.W)  -> nvdla_cdma_d_nan_input_data_num_0_out,
//         "hc8".asUInt(32.W)  -> nvdla_cdma_d_nan_input_weight_num_0_out,
//         "h10".asUInt(32.W)  -> nvdla_cdma_d_op_enable_0_out,
//         "he0".asUInt(32.W)  -> nvdla_cdma_d_perf_dat_read_latency_0_out,
//         "hd8".asUInt(32.W)  -> nvdla_cdma_d_perf_dat_read_stall_0_out,
//         "hd4".asUInt(32.W)  -> nvdla_cdma_d_perf_enable_0_out,
//         "he4".asUInt(32.W)  -> nvdla_cdma_d_perf_wt_read_latency_0_out,
//         "hdc".asUInt(32.W)  -> nvdla_cdma_d_perf_wt_read_stall_0_out,
//         "h28".asUInt(32.W)  -> nvdla_cdma_d_pixel_offset_0_out,
//         "h50".asUInt(32.W)  -> nvdla_cdma_d_reserved_x_cfg_0_out,
//         "h54".asUInt(32.W)  -> nvdla_cdma_d_reserved_y_cfg_0_out,
//         "h48".asUInt(32.W)  -> nvdla_cdma_d_surf_stride_0_out,
//         "h78".asUInt(32.W)  -> nvdla_cdma_d_weight_addr_high_0_out,
//         "h7c".asUInt(32.W)  -> nvdla_cdma_d_weight_addr_low_0_out,
//         "h80".asUInt(32.W)  -> nvdla_cdma_d_weight_bytes_0_out,
//         "h68".asUInt(32.W)  -> nvdla_cdma_d_weight_format_0_out,
//         "h74".asUInt(32.W)  -> nvdla_cdma_d_weight_ram_type_0_out,
//         "h6c".asUInt(32.W)  -> nvdla_cdma_d_weight_size_0_0_out,
//         "h70".asUInt(32.W)  -> nvdla_cdma_d_weight_size_1_0_out,
//         "h84".asUInt(32.W)  -> nvdla_cdma_d_wgs_addr_high_0_out,
//         "h88".asUInt(32.W)  -> nvdla_cdma_d_wgs_addr_low_0_out,
//         "h8c".asUInt(32.W)  -> nvdla_cdma_d_wmb_addr_high_0_out,
//         "h90".asUInt(32.W)  -> nvdla_cdma_d_wmb_addr_low_0_out,
//         "h94".asUInt(32.W)  -> nvdla_cdma_d_wmb_bytes_0_out,
//         "hb4".asUInt(32.W)  -> nvdla_cdma_d_zero_padding_0_out,
//         "hb8".asUInt(32.W)  -> nvdla_cdma_d_zero_padding_value_0_out
                                                    
//    ))

//     //Register flop declarations       

//     // Register: NVDLA_CDMA_D_BANK_0    Field: data_bank
//     io.data_bank := RegEnable(io.reg_wr_data(4, 0), "b0".asUInt(5.W), nvdla_cdma_d_bank_0_wren)
//     // Register: NVDLA_CDMA_D_BANK_0    Field: weight_bank
//     io.weight_bank := RegEnable(io.reg_wr_data(20, 16), "b0".asUInt(5.W), nvdla_cdma_d_bank_0_wren)
//     // Register: NVDLA_CDMA_D_BATCH_NUMBER_0    Field: batches
//     io.batches := RegEnable(io.reg_wr_data(4, 0), "b0".asUInt(5.W), nvdla_cdma_d_batch_number_0_wren)
//     // Register: NVDLA_CDMA_D_BATCH_STRIDE_0    Field: batch_stride
//     io.batch_stride := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_batch_stride_0_wren)
//     // Register: NVDLA_CDMA_D_CONV_STRIDE_0    Field: conv_x_stride
//     io.conv_x_stride := RegEnable(io.reg_wr_data(2, 0), "b0".asUInt(3.W), nvdla_cdma_d_conv_stride_0_wren)
//     // Register: NVDLA_CDMA_D_CONV_STRIDE_0    Field: conv_y_stride
//     io.conv_y_stride := RegEnable(io.reg_wr_data(18, 16), "b0".asUInt(3.W), nvdla_cdma_d_conv_stride_0_wren)
//     // Register: NVDLA_CDMA_D_CVT_CFG_0    Field: cvt_en
//     io.cvt_en := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_cvt_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_CVT_CFG_0    Field: cvt_truncate
//     io.cvt_truncate := RegEnable(io.reg_wr_data(9, 4), "b0".asUInt(6.W), nvdla_cdma_d_cvt_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_CVT_OFFSET_0    Field: cvt_offset
//     io.cvt_offset := RegEnable(io.reg_wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdma_d_cvt_offset_0_wren)
//     // Register: NVDLA_CDMA_D_CVT_SCALE_0    Field: cvt_scale
//     io.cvt_scale := RegEnable(io.reg_wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdma_d_cvt_scale_0_wren)
//     // Register: NVDLA_CDMA_D_CYA_0    Field: cya
//     io.cya := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_cya_0_wren)
//     // Register: NVDLA_CDMA_D_DAIN_ADDR_HIGH_0_0    Field: datain_addr_high_0
//     io.datain_addr_high_0 := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_dain_addr_high_0_0_wren)
//     // Register: NVDLA_CDMA_D_DAIN_ADDR_HIGH_1_0    Field: datain_addr_high_1
//     io.datain_addr_high_1 := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_dain_addr_high_1_0_wren)
//     // Register: NVDLA_CDMA_D_DAIN_ADDR_LOW_0_0    Field: datain_addr_low_0
//     io.datain_addr_low_0 := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_dain_addr_low_0_0_wren)
//     // Register: NVDLA_CDMA_D_DAIN_ADDR_LOW_1_0    Field: datain_addr_low_1
//     io.datain_addr_low_1 := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_dain_addr_low_1_0_wren)
//     // Register: NVDLA_CDMA_D_DAIN_MAP_0    Field: line_packed
//     io.line_packed := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_dain_map_0_wren)
//     // Register: NVDLA_CDMA_D_DAIN_MAP_0    Field: surf_packed
//     io.surf_packed := RegEnable(io.reg_wr_data(16), false.B, nvdla_cdma_d_dain_map_0_wren)
//     // Register: NVDLA_CDMA_D_DAIN_RAM_TYPE_0    Field: datain_ram_type
//     io.datain_ram_type := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_dain_ram_type_0_wren)
//     // Register: NVDLA_CDMA_D_DATAIN_FORMAT_0    Field: datain_format
//     io.datain_format := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_datain_format_0_wren)
//     // Register: NVDLA_CDMA_D_DATAIN_FORMAT_0    Field: pixel_format
//     io.pixel_format := RegEnable(io.reg_wr_data(13, 8), "b001100".asUInt(6.W), nvdla_cdma_d_datain_format_0_wren)
//     // Register: NVDLA_CDMA_D_DATAIN_FORMAT_0    Field: pixel_mapping
//     io.pixel_mapping := RegEnable(io.reg_wr_data(16), false.B, nvdla_cdma_d_datain_format_0_wren)
//     // Register: NVDLA_CDMA_D_DATAIN_FORMAT_0    Field: pixel_sign_override
//     io.pixel_sign_override := RegEnable(io.reg_wr_data(20), false.B, nvdla_cdma_d_datain_format_0_wren)
//     // Register: NVDLA_CDMA_D_DATAIN_SIZE_0_0    Field: datain_height
//     io.datain_height := RegEnable(io.reg_wr_data(28, 16), "b0".asUInt(13.W), nvdla_cdma_d_datain_size_0_0_wren)
//     // Register: NVDLA_CDMA_D_DATAIN_SIZE_0_0    Field: datain_width
//     io.datain_width := RegEnable(io.reg_wr_data(12, 0), "b0".asUInt(13.W), nvdla_cdma_d_datain_size_0_0_wren)
//     // Register: NVDLA_CDMA_D_DATAIN_SIZE_1_0    Field: datain_channel
//     io.datain_channel := RegEnable(io.reg_wr_data(12, 0), "b0".asUInt(13.W), nvdla_cdma_d_datain_size_1_0_wren)
//     // Register: NVDLA_CDMA_D_DATAIN_SIZE_EXT_0_0    Field: datain_height_ext
//     io.datain_height_ext := RegEnable(io.reg_wr_data(28, 16), "b0".asUInt(13.W), nvdla_cdma_d_datain_size_ext_0_0_wren)
//     // Register: NVDLA_CDMA_D_DATAIN_SIZE_EXT_0_0    Field: datain_width_ext
//     io.datain_width_ext := RegEnable(io.reg_wr_data(12, 0), "b0".asUInt(13.W), nvdla_cdma_d_datain_size_ext_0_0_wren)
//     // Register: NVDLA_CDMA_D_ENTRY_PER_SLICE_0    Field: entries
//     io.entries := RegEnable(io.reg_wr_data(13, 0), "b0".asUInt(14.W), nvdla_cdma_d_entry_per_slice_0_wren)
//     // Register: NVDLA_CDMA_D_FETCH_GRAIN_0    Field: grains
//     io.grains := RegEnable(io.reg_wr_data(11, 0), "b0".asUInt(12.W), nvdla_cdma_d_fetch_grain_0_wren)
//     // Register: NVDLA_CDMA_D_LINE_STRIDE_0    Field: line_stride
//     io.line_stride := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_perf_enable_0_wren)
//     // Register: NVDLA_CDMA_D_LINE_UV_STRIDE_0    Field: uv_line_stride
//     io.uv_line_stride := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_line_uv_stride_0_wren)
//     // Register: NVDLA_CDMA_D_MEAN_FORMAT_0    Field: mean_format
//     io.mean_format := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_mean_format_0_wren)
//     // Register: NVDLA_CDMA_D_MEAN_GLOBAL_0_0    Field: mean_gu
//     io.mean_gu := RegEnable(io.reg_wr_data(31, 16), "b0".asUInt(16.W), nvdla_cdma_d_mean_global_0_0_wren)
//     // Register: NVDLA_CDMA_D_MEAN_GLOBAL_0_0    Field: mean_ry
//     io.mean_ry := RegEnable(io.reg_wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdma_d_mean_global_0_0_wren)
//     // Register: NVDLA_CDMA_D_MEAN_GLOBAL_1_0    Field: mean_ax
//     io.mean_ax := RegEnable(io.reg_wr_data(31, 16), "b0".asUInt(16.W), nvdla_cdma_d_mean_global_1_0_wren)
//     // Register: NVDLA_CDMA_D_MEAN_GLOBAL_1_0    Field: mean_bv
//     io.mean_bv := RegEnable(io.reg_wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdma_d_mean_global_1_0_wren)
//     // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: conv_mode
//     io.conv_mode := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_misc_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: data_reuse
//     io.data_reuse := RegEnable(io.reg_wr_data(16), false.B, nvdla_cdma_d_misc_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: in_precision
//     io.in_precision := RegEnable(io.reg_wr_data(9, 8), "b01".asUInt(2.W), nvdla_cdma_d_misc_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: proc_precision
//     io.proc_precision := RegEnable(io.reg_wr_data(13, 12), "b01".asUInt(2.W), nvdla_cdma_d_misc_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: skip_data_rls
//     io.skip_data_rls := RegEnable(io.reg_wr_data(24), false.B, nvdla_cdma_d_misc_cfg_0_wren)
//      // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: skip_weight_rls
//     io.skip_weight_rls := RegEnable(io.reg_wr_data(28), false.B, nvdla_cdma_d_misc_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: weight_reuse
//     io.weight_reuse := RegEnable(io.reg_wr_data(20), false.B, nvdla_cdma_d_misc_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_NAN_FLUSH_TO_ZERO_0    Field: nan_to_zero
//     io.nan_to_zero := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_nan_flush_to_zero_0_wren)
//     // Register: NVDLA_CDMA_D_PERF_ENABLE_0    Field: dma_en
//     io.dma_en := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_perf_enable_0_wren)
//     // Register: NVDLA_CDMA_D_PIXEL_OFFSET_0    Field: pixel_x_offset
//     io.pixel_x_offset := RegEnable(io.reg_wr_data(4, 0), "b0".asUInt(5.W), nvdla_cdma_d_pixel_offset_0_wren)
//     // Register: NVDLA_CDMA_D_PIXEL_OFFSET_0    Field: pixel_y_offset
//     io.pixel_y_offset := RegEnable(io.reg_wr_data(18, 16), "b0".asUInt(3.W), nvdla_cdma_d_pixel_offset_0_wren)
//     // Register: NVDLA_CDMA_D_RESERVED_X_CFG_0    Field: rsv_per_line
//     io.rsv_per_line := RegEnable(io.reg_wr_data(9, 0), "b0".asUInt(10.W), nvdla_cdma_d_reserved_x_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_RESERVED_X_CFG_0    Field: rsv_per_uv_line
//     io.rsv_per_uv_line := RegEnable(io.reg_wr_data(25, 16), "b0".asUInt(10.W), nvdla_cdma_d_reserved_x_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_RESERVED_Y_CFG_0    Field: rsv_height
//     io.rsv_height := RegEnable(io.reg_wr_data(2, 0), "b0".asUInt(3.W), nvdla_cdma_d_reserved_y_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_RESERVED_Y_CFG_0    Field: rsv_y_index
//     io.rsv_y_index := RegEnable(io.reg_wr_data(20, 16), "b0".asUInt(5.W), nvdla_cdma_d_reserved_y_cfg_0_wren)
//     // Register: NVDLA_CDMA_D_SURF_STRIDE_0    Field: surf_stride
//     io.surf_stride := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_surf_stride_0_wren)
//     // Register: NVDLA_CDMA_D_WEIGHT_ADDR_HIGH_0    Field: weight_addr_high
//     io.weight_addr_high := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_weight_addr_high_0_wren)
//     // Register: NVDLA_CDMA_D_WEIGHT_ADDR_LOW_0    Field: weight_addr_low
//     io.weight_addr_low := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_weight_addr_low_0_wren)
//     // Register: NVDLA_CDMA_D_WEIGHT_BYTES_0    Field: weight_bytes
//     io.weight_bytes := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_weight_bytes_0_wren)
//     // Register: NVDLA_CDMA_D_WEIGHT_FORMAT_0    Field: weight_format
//     io.weight_format := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_weight_format_0_wren)
//     // Register: NVDLA_CDMA_D_WEIGHT_RAM_TYPE_0    Field: weight_ram_type
//     io.weight_ram_type := RegEnable(io.reg_wr_data(0), false.B, nvdla_cdma_d_weight_ram_type_0_wren)
//     // Register: NVDLA_CDMA_D_WEIGHT_SIZE_0_0    Field: byte_per_kernel
//     io.byte_per_kernel := RegEnable(io.reg_wr_data(17, 0), "b0".asUInt(18.W), nvdla_cdma_d_weight_size_0_0_wren)
//     // Register: NVDLA_CDMA_D_WEIGHT_SIZE_1_0    Field: weight_kernel
//     io.weight_kernel := RegEnable(io.reg_wr_data(12, 0), "b0".asUInt(13.W), nvdla_cdma_d_weight_size_1_0_wren)
//     // Register: NVDLA_CDMA_D_WGS_ADDR_HIGH_0    Field: wgs_addr_high
//     io.wgs_addr_high := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_wgs_addr_high_0_wren)
//     // Register: NVDLA_CDMA_D_WGS_ADDR_LOW_0    Field: wgs_addr_low
//     io.wgs_addr_low := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_wgs_addr_low_0_wren)
//     // Register: NVDLA_CDMA_D_WMB_ADDR_HIGH_0    Field: wmb_addr_high
//     io.wmb_addr_high := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_wmb_addr_high_0_wren)
//     // Register: NVDLA_CDMA_D_WMB_ADDR_LOW_0    Field: wmb_addr_low
//     io.wmb_addr_low := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdma_d_wmb_addr_low_0_wren)
//     // Register: NVDLA_CDMA_D_WMB_BYTES_0    Field: wmb_bytes
//     io.wmb_bytes := RegEnable(io.reg_wr_data(27, 0), "b0".asUInt(28.W), nvdla_cdma_d_wmb_bytes_0_wren)
//     // Register: NVDLA_CDMA_D_ZERO_PADDING_0    Field: pad_bottom
//     io.pad_bottom := RegEnable(io.reg_wr_data(29, 24), "b0".asUInt(6.W), nvdla_cdma_d_zero_padding_0_wren)
//     // Register: NVDLA_CDMA_D_ZERO_PADDING_0    Field: pad_left
//     io.pad_left := RegEnable(io.reg_wr_data(4, 0), "b0".asUInt(5.W), nvdla_cdma_d_zero_padding_0_wren)
//     // Register: NVDLA_CDMA_D_ZERO_PADDING_0    Field: pad_right
//     io.pad_right := RegEnable(io.reg_wr_data(13, 8), "b0".asUInt(6.W), nvdla_cdma_d_zero_padding_0_wren)
//     // Register: NVDLA_CDMA_D_ZERO_PADDING_0    Field: pad_top
//     io.pad_top := RegEnable(io.reg_wr_data(20, 16), "b0".asUInt(5.W), nvdla_cdma_d_zero_padding_0_wren)
//     // Register: NVDLA_CDMA_D_ZERO_PADDING_VALUE_0    Field: pad_value
//     io.pad_value := RegEnable(io.reg_wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdma_d_zero_padding_value_0_wren)


// }}