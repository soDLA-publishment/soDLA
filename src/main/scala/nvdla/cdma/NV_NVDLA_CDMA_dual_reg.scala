package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDMA_dual_reg extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        //Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)
        val reg_wr_en = Input(Bool())

        //Writable register flop/trigger outputs
        val data_bank = Output(UInt(5.W))
        val weight_bank = Output(UInt(5.W))
        val batches = Output(UInt(5.W))
        val batch_stride = Output(UInt(32.W))
        val conv_x_stride = Output(UInt(3.W))
        val conv_y_stride = Output(UInt(3.W))
        val cvt_en = Output(Bool())
        val cvt_truncate = Output(UInt(6.W))
        val cvt_offset = Output(UInt(16.W))
        val cvt_scale = Output(UInt(16.W))
        val cya = Output(UInt(32.W))
        val datain_addr_high_0 = Output(UInt(32.W))
        val datain_addr_high_1 = Output(UInt(32.W))
        val datain_addr_low_0 = Output(UInt(32.W))
        val datain_addr_low_1 = Output(UInt(32.W))
        val line_packed = Output(Bool())
        val surf_packed = Output(Bool())
        val datain_ram_type = Output(Bool())
        val datain_format = Output(Bool())
        val pixel_format = Output(UInt(6.W))
        val pixel_mapping = Output(Bool())
        val pixel_sign_override = Output(Bool())
        val datain_height = Output(UInt(13.W))
        val datain_width = Output(UInt(13.W))
        val datain_channel = Output(UInt(13.W))
        val datain_height_ext = Output(UInt(13.W))
        val datain_width_ext = Output(UInt(13.W))
        val entries = Output(UInt(14.W))
        val grains = Output(UInt(12.W))
        val line_stride = Output(UInt(32.W))
        val uv_line_stride = Output(UInt(32.W))
        val mean_format = Output(Bool())
        val mean_gu = Output(UInt(16.W))
        val mean_ry = Output(UInt(16.W))
        val mean_ax = Output(UInt(16.W))
        val mean_bv = Output(UInt(16.W))
        val conv_mode = Output(Bool())
        val data_reuse = Output(Bool())
        val in_precision = Output(UInt(2.W))
        val proc_precision = Output(UInt(2.W))
        val skip_data_rls = Output(Bool())
        val skip_weight_rls = Output(Bool())
        val weight_reuse = Output(Bool())
        val nan_to_zero = Output(Bool())
        val op_en_trigger = Output(Bool())
        val dma_en = Output(Bool())
        val pixel_x_offset = Output(UInt(5.W))
        val pixel_y_offset = Output(UInt(3.W))
        val rsv_per_line = Output(UInt(10.W))
        val rsv_per_uv_line = Output(UInt(10.W))
        val rsv_height = Output(UInt(3.W))
        val rsv_y_index = Output(UInt(5.W))
        val surf_stride = Output(UInt(32.W))
        val weight_addr_high = Output(UInt(32.W))
        val weight_addr_low = Output(UInt(32.W))
        val weight_bytes = Output(UInt(32.W))
        val weight_format = Output(Bool())
        val weight_ram_type = Output(Bool())
        val byte_per_kernel = Output(UInt(18.W))
        val weight_kernel = Output(UInt(13.W))
        val wgs_addr_high = Output(UInt(32.W))
        val wgs_addr_low = Output(UInt(32.W))
        val wmb_addr_high = Output(UInt(32.W))
        val wmb_addr_low = Output(UInt(32.W))
        val wmb_bytes = Output(UInt(28.W))
        val pad_bottom = Output(UInt(6.W))
        val pad_left = Output(UInt(5.W))
        val pad_right = Output(UInt(6.W))
        val pad_top = Output(UInt(5.W))
        val pad_value = Output(UInt(16.W))

        //Read-only register inputs
        val inf_data_num = Input(UInt(32.W))
        val inf_weight_num = Input(UInt(32.W))
        val nan_data_num = Input(UInt(32.W))
        val nan_weight_num = Input(UInt(32.W))
        val op_en = Input(Bool())
        val dat_rd_latency = Input(UInt(32.W))
        val dat_rd_stall = Input(UInt(32.W))
        val wt_rd_latency = Input(UInt(32.W))
        val wt_rd_stall = Input(UInt(32.W)) 
      })
    
    //      ┌─┐       ┌─┐
    //   ┌──┘ ┴───────┘ ┴──┐
    //   │                 │
    //   │       ───       │
    //   │  ─┬┘       └┬─  │
    //   │                 │
    //   │       ─┴─       │
    //   │                 │
    //   └───┐         ┌───┘
    //       │         │
    //       │         │
    //       │         │
    //       │         └──────────────┐
    //       │                        │
    //       │                        ├─┐
    //       │                        ┌─┘    
    //       │                        │
    //       └─┐  ┐  ┌───────┬──┐  ┌──┘         
    //         │ ─┤ ─┤       │ ─┤ ─┤         
    //         └──┴──┘       └──┴──┘ 
    withClock(io.nvdla_core_clk){

    // Address decode
    val nvdla_cdma_d_bank_0_wren = (io.reg_offset === "hbc".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_batch_number_0_wren = (io.reg_offset === "h58".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_batch_stride_0_wren = (io.reg_offset === "h5c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_conv_stride_0_wren = (io.reg_offset === "hb0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_cvt_cfg_0_wren = (io.reg_offset === "ha4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_cvt_offset_0_wren = (io.reg_offset === "ha8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_cvt_scale_0_wren = (io.reg_offset === "hac".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_cya_0_wren = (io.reg_offset === "he8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_dain_addr_high_0_0_wren = (io.reg_offset === "h30".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_dain_addr_high_1_0_wren = (io.reg_offset === "h38".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_dain_addr_low_0_0_wren = (io.reg_offset === "h34".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_dain_addr_low_1_0_wren = (io.reg_offset === "h3c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_dain_map_0_wren = (io.reg_offset === "h4c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_dain_ram_type_0_wren = (io.reg_offset === "h2c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_datain_format_0_wren = (io.reg_offset === "h18".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_datain_size_0_0_wren = (io.reg_offset === "h1c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_datain_size_1_0_wren = (io.reg_offset === "h20".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_datain_size_ext_0_0_wren = (io.reg_offset === "h24".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_entry_per_slice_0_wren = (io.reg_offset === "h60".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_fetch_grain_0_wren = (io.reg_offset === "h64".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_inf_input_data_num_0_wren = (io.reg_offset === "hcc".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_inf_input_weight_num_0_wren = (io.reg_offset === "hd0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_line_stride_0_wren = (io.reg_offset === "h40".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_line_uv_stride_0_wren = (io.reg_offset === "h44".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_mean_format_0_wren = (io.reg_offset === "h98".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_mean_global_0_0_wren = (io.reg_offset === "h9c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_mean_global_1_0_wren = (io.reg_offset === "ha0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_misc_cfg_0_wren = (io.reg_offset === "h14".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_nan_flush_to_zero_0_wren = (io.reg_offset === "hc0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_nan_input_data_num_0_wren = (io.reg_offset === "hc4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_nan_input_weight_num_0_wren = (io.reg_offset === "hc8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_op_enable_0_wren = (io.reg_offset === "h10".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_perf_dat_read_latency_0_wren = (io.reg_offset === "he0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_perf_dat_read_stall_0_wren = (io.reg_offset === "hd8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_perf_enable_0_wren = (io.reg_offset === "hd4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_perf_wt_read_latency_0_wren = (io.reg_offset === "he4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_perf_wt_read_stall_0_wren = (io.reg_offset === "hdc".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_pixel_offset_0_wren = (io.reg_offset === "h28".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_reserved_x_cfg_0_wren = (io.reg_offset === "h50".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_reserved_y_cfg_0_wren = (io.reg_offset === "h54".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_surf_stride_0_wren = (io.reg_offset === "h48".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_weight_addr_high_0_wren = (io.reg_offset === "h78".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_weight_addr_low_0_wren = (io.reg_offset === "h7c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_weight_bytes_0_wren = (io.reg_offset === "h80".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_weight_format_0_wren = (io.reg_offset === "h68".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_weight_ram_type_0_wren = (io.reg_offset === "h74".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_weight_size_0_0_wren = (io.reg_offset === "h6c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_weight_size_1_0_wren = (io.reg_offset === "h70".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_wgs_addr_high_0_wren = (io.reg_offset === "h84".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_wgs_addr_low_0_wren = (io.reg_offset === "h88".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_wmb_addr_high_0_wren = (io.reg_offset === "h8c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_wmb_addr_low_0_wren = (io.reg_offset === "h90".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_wmb_bytes_0_wren = (io.reg_offset === "h94".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_zero_padding_0_wren = (io.reg_offset === "hb4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_cdma_d_zero_padding_value_0_wren = (io.reg_offset === "hb8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)


    val nvdla_cdma_d_bank_0_out = Cat("b0".asUInt(11.W), io.weight_bank, "b0".asUInt(11.W), io.data_bank)
    val nvdla_cdma_d_batch_number_0_out = Cat("b0".asUInt(27.W), io.batches)
    val nvdla_cdma_d_batch_stride_0_out = Cat( io.batch_stride)
    val nvdla_cdma_d_conv_stride_0_out = Cat("b0".asUInt(13.W), io.conv_y_stride, "b0".asUInt(13.W), io.conv_x_stride)
    val nvdla_cdma_d_cvt_cfg_0_out = Cat("b0".asUInt(22.W), io.cvt_truncate,"b0".asUInt(3.W), io.cvt_en)
    val nvdla_cdma_d_cvt_offset_0_out = Cat("b0".asUInt(16.W), io.cvt_offset)
    val nvdla_cdma_d_cvt_scale_0_out = Cat("b0".asUInt(16.W), io.cvt_scale)
    val nvdla_cdma_d_cya_0_out = Cat( io.cya)
    val nvdla_cdma_d_dain_addr_high_0_0_out = Cat( io.datain_addr_high_0)
    val nvdla_cdma_d_dain_addr_high_1_0_out = Cat( io.datain_addr_high_1)
    val nvdla_cdma_d_dain_addr_low_0_0_out = Cat( io.datain_addr_low_0)
    val nvdla_cdma_d_dain_addr_low_1_0_out = Cat( io.datain_addr_low_1)
    val nvdla_cdma_d_dain_map_0_out = Cat("b0".asUInt(15.W), io.surf_packed, "b0".asUInt(15.W), io.line_packed)
    val nvdla_cdma_d_dain_ram_type_0_out = Cat("b0".asUInt(31.W), io.datain_ram_type)
    val nvdla_cdma_d_datain_format_0_out = Cat( "b0".asUInt(11.W), io.pixel_sign_override, "b0".asUInt(3.W), io.pixel_mapping, "b0".asUInt(2.W), io.pixel_format, "b0".asUInt(7.W), io.datain_format)
    val nvdla_cdma_d_datain_size_0_0_out = Cat( "b0".asUInt(3.W), io.datain_height, "b0".asUInt(3.W), io.datain_width)
    val nvdla_cdma_d_datain_size_1_0_out = Cat( "b0".asUInt(19.W), io.datain_channel)
    val nvdla_cdma_d_datain_size_ext_0_0_out = Cat( "b0".asUInt(3.W), io.datain_height_ext, "b0".asUInt(3.W), io.datain_width_ext)
    val nvdla_cdma_d_entry_per_slice_0_out = Cat( "b0".asUInt(18.W), io.entries)
    val nvdla_cdma_d_fetch_grain_0_out = Cat( "b0".asUInt(20.W), io.grains)
    val nvdla_cdma_d_inf_input_data_num_0_out = Cat( io.inf_data_num)
    val nvdla_cdma_d_inf_input_weight_num_0_out = Cat( io.inf_weight_num)
    val nvdla_cdma_d_line_stride_0_out = Cat( io.line_stride)
    val nvdla_cdma_d_line_uv_stride_0_out = Cat( io.uv_line_stride)
    val nvdla_cdma_d_mean_format_0_out = Cat( "b0".asUInt(31.W), io.mean_format)
    val nvdla_cdma_d_mean_global_0_0_out = Cat( io.mean_gu, io.mean_ry)
    val nvdla_cdma_d_mean_global_1_0_out = Cat( io.mean_ax, io.mean_bv)
    val nvdla_cdma_d_misc_cfg_0_out = Cat( "b0".asUInt(3.W), io.skip_weight_rls, "b0".asUInt(3.W), io.skip_data_rls, "b0".asUInt(3.W), io.weight_reuse, "b0".asUInt(3.W), io.data_reuse, "b0".asUInt(2.W), io.proc_precision, "b0".asUInt(2.W), io.in_precision, "b0".asUInt(7.W), io.conv_mode)
    val nvdla_cdma_d_nan_flush_to_zero_0_out = Cat( "b0".asUInt(31.W), io.nan_to_zero)
    val nvdla_cdma_d_nan_input_data_num_0_out = Cat( io.nan_data_num)
    val nvdla_cdma_d_nan_input_weight_num_0_out = Cat( io.nan_weight_num)
    val nvdla_cdma_d_op_enable_0_out = Cat( "b0".asUInt(31.W), io.op_en)
    val nvdla_cdma_d_perf_dat_read_latency_0_out = Cat( io.dat_rd_latency)
    val nvdla_cdma_d_perf_dat_read_stall_0_out = Cat( io.dat_rd_stall)
    val nvdla_cdma_d_perf_enable_0_out = Cat( "b0".asUInt(31.W), io.dma_en)
    val nvdla_cdma_d_perf_wt_read_latency_0_out = Cat( io.wt_rd_latency)
    val nvdla_cdma_d_perf_wt_read_stall_0_out = Cat( io.wt_rd_stall)
    val nvdla_cdma_d_pixel_offset_0_out = Cat( "b0".asUInt(13.W), io.pixel_y_offset, "b0".asUInt(11.W), io.pixel_x_offset)
    val nvdla_cdma_d_reserved_x_cfg_0_out = Cat( "b0".asUInt(6.W), io.rsv_per_uv_line, "b0".asUInt(6.W), io.rsv_per_line)
    val nvdla_cdma_d_reserved_y_cfg_0_out = Cat( "b0".asUInt(11.W), io.rsv_y_index, "b0".asUInt(13.W), io.rsv_height)
    val nvdla_cdma_d_surf_stride_0_out = Cat( io.surf_stride)
    val nvdla_cdma_d_weight_addr_high_0_out = Cat( io.weight_addr_high)
    val nvdla_cdma_d_weight_addr_low_0_out = Cat( io.weight_addr_low)
    val nvdla_cdma_d_weight_bytes_0_out = Cat( io.weight_bytes)
    val nvdla_cdma_d_weight_format_0_out = Cat( "b0".asUInt(31.W), io.weight_format)
    val nvdla_cdma_d_weight_ram_type_0_out = Cat( "b0".asUInt(31.W), io.weight_ram_type)
    val nvdla_cdma_d_weight_size_0_0_out = Cat( "b0".asUInt(14.W), io.byte_per_kernel)
    val nvdla_cdma_d_weight_size_1_0_out = Cat( "b0".asUInt(19.W), io.weight_kernel)
    val nvdla_cdma_d_wgs_addr_high_0_out = Cat( io.wgs_addr_high)
    val nvdla_cdma_d_wgs_addr_low_0_out = Cat( io.wgs_addr_low)
    val nvdla_cdma_d_wmb_addr_high_0_out = Cat( io.wmb_addr_high)
    val nvdla_cdma_d_wmb_addr_low_0_out = Cat( io.wmb_addr_low)
    val nvdla_cdma_d_wmb_bytes_0_out = Cat( "b0".asUInt(4.W), io.wmb_bytes)
    val nvdla_cdma_d_zero_padding_0_out = Cat( "b0".asUInt(2.W), io.pad_bottom, "b0".asUInt(3.W), io.pad_top, "b0".asUInt(2.W), io.pad_right, "b0".asUInt(3.W), io.pad_left)
    val nvdla_cdma_d_zero_padding_value_0_out = Cat( "b0".asUInt(16.W), io.pad_value)

    io.op_en_trigger := nvdla_cdma_d_op_enable_0_wren

    //Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
        "hbc".asUInt(32.W)  -> nvdla_cdma_d_bank_0_out,
        "h58".asUInt(32.W)  -> nvdla_cdma_d_batch_number_0_out,
        "h5c".asUInt(32.W)  -> nvdla_cdma_d_batch_stride_0_out,
        "hb0".asUInt(32.W)  -> nvdla_cdma_d_conv_stride_0_out,
        "ha4".asUInt(32.W)  -> nvdla_cdma_d_cvt_cfg_0_out,
        "ha8".asUInt(32.W)  -> nvdla_cdma_d_cvt_offset_0_out,
        "hac".asUInt(32.W)  -> nvdla_cdma_d_cvt_scale_0_out,
        "he8".asUInt(32.W)  -> nvdla_cdma_d_cya_0_out,
        "h30".asUInt(32.W)  -> nvdla_cdma_d_dain_addr_high_0_0_out,
        "h38".asUInt(32.W)  -> nvdla_cdma_d_dain_addr_high_1_0_out,
        "h34".asUInt(32.W)  -> nvdla_cdma_d_dain_addr_low_0_0_out,
        "h3c".asUInt(32.W)  -> nvdla_cdma_d_dain_addr_low_1_0_out,
        "h4c".asUInt(32.W)  -> nvdla_cdma_d_dain_map_0_out,
        "h2c".asUInt(32.W)  -> nvdla_cdma_d_dain_ram_type_0_out,
        "h18".asUInt(32.W)  -> nvdla_cdma_d_datain_format_0_out,
        "h1c".asUInt(32.W)  -> nvdla_cdma_d_datain_size_0_0_out,
        "h20".asUInt(32.W)  -> nvdla_cdma_d_datain_size_1_0_out,
        "h24".asUInt(32.W)  -> nvdla_cdma_d_datain_size_ext_0_0_out,
        "h60".asUInt(32.W)  -> nvdla_cdma_d_entry_per_slice_0_out,
        "h64".asUInt(32.W)  -> nvdla_cdma_d_fetch_grain_0_out,
        "hcc".asUInt(32.W)  -> nvdla_cdma_d_inf_input_data_num_0_out,
        "hd0".asUInt(32.W)  -> nvdla_cdma_d_inf_input_weight_num_0_out,
        "h40".asUInt(32.W)  -> nvdla_cdma_d_line_stride_0_out,
        "h44".asUInt(32.W)  -> nvdla_cdma_d_line_uv_stride_0_out,
        "h98".asUInt(32.W)  -> nvdla_cdma_d_mean_format_0_out,
        "h9c".asUInt(32.W)  -> nvdla_cdma_d_mean_global_0_0_out,
        "ha0".asUInt(32.W)  -> nvdla_cdma_d_mean_global_1_0_out,
        "h14".asUInt(32.W)  -> nvdla_cdma_d_misc_cfg_0_out,
        "hc0".asUInt(32.W)  -> nvdla_cdma_d_nan_flush_to_zero_0_out,
        "hc4".asUInt(32.W)  -> nvdla_cdma_d_nan_input_data_num_0_out,
        "hc8".asUInt(32.W)  -> nvdla_cdma_d_nan_input_weight_num_0_out,
        "h10".asUInt(32.W)  -> nvdla_cdma_d_op_enable_0_out,
        "he0".asUInt(32.W)  -> nvdla_cdma_d_perf_dat_read_latency_0_out,
        "hd8".asUInt(32.W)  -> nvdla_cdma_d_perf_dat_read_stall_0_out,
        "hd4".asUInt(32.W)  -> nvdla_cdma_d_perf_enable_0_out,
        "he4".asUInt(32.W)  -> nvdla_cdma_d_perf_wt_read_latency_0_out,
        "hdc".asUInt(32.W)  -> nvdla_cdma_d_perf_wt_read_stall_0_out,
        "h28".asUInt(32.W)  -> nvdla_cdma_d_pixel_offset_0_out,
        "h50".asUInt(32.W)  -> nvdla_cdma_d_reserved_x_cfg_0_out,
        "h54".asUInt(32.W)  -> nvdla_cdma_d_reserved_y_cfg_0_out,
        "h48".asUInt(32.W)  -> nvdla_cdma_d_surf_stride_0_out,
        "h78".asUInt(32.W)  -> nvdla_cdma_d_weight_addr_high_0_out,
        "h7c".asUInt(32.W)  -> nvdla_cdma_d_weight_addr_low_0_out,
        "h80".asUInt(32.W)  -> nvdla_cdma_d_weight_bytes_0_out,
        "h68".asUInt(32.W)  -> nvdla_cdma_d_weight_format_0_out,
        "h74".asUInt(32.W)  -> nvdla_cdma_d_weight_ram_type_0_out,
        "h6c".asUInt(32.W)  -> nvdla_cdma_d_weight_size_0_0_out,
        "h70".asUInt(32.W)  -> nvdla_cdma_d_weight_size_1_0_out,
        "h84".asUInt(32.W)  -> nvdla_cdma_d_wgs_addr_high_0_out,
        "h88".asUInt(32.W)  -> nvdla_cdma_d_wgs_addr_low_0_out,
        "h8c".asUInt(32.W)  -> nvdla_cdma_d_wmb_addr_high_0_out,
        "h90".asUInt(32.W)  -> nvdla_cdma_d_wmb_addr_low_0_out,
        "h94".asUInt(32.W)  -> nvdla_cdma_d_wmb_bytes_0_out,
        "hb4".asUInt(32.W)  -> nvdla_cdma_d_zero_padding_0_out,
        "hb8".asUInt(32.W)  -> nvdla_cdma_d_zero_padding_value_0_out
                                                    
   ))

    //Register flop declarations

    val data_bank_out = RegInit("b0".asUInt(5.W))
    val weight_bank_out = RegInit("b0".asUInt(5.W))
    val batches_out = RegInit("b0".asUInt(5.W))
    val batch_stride_out = RegInit("b0".asUInt(32.W))
    val conv_x_stride_out = RegInit("b0".asUInt(3.W))
    val conv_y_stride_out = RegInit("b0".asUInt(3.W))
    val cvt_en_out = RegInit(false.B)
    val cvt_truncate_out = RegInit("b0".asUInt(6.W))
    val cvt_offset_out = RegInit("b0".asUInt(16.W))
    val cvt_scale_out = RegInit("b0".asUInt(16.W))
    val cya_out = RegInit("b0".asUInt(32.W))   
    val datain_addr_high_0_out = RegInit("b0".asUInt(32.W))
    val datain_addr_high_1_out = RegInit("b0".asUInt(32.W))
    val datain_addr_low_0_out = RegInit("b0".asUInt(32.W))   
    val datain_addr_low_1_out = RegInit("b0".asUInt(32.W))
    val line_packed_out = RegInit(false.B)
    val surf_packed_out = RegInit(false.B)    
    val datain_ram_type_out = RegInit(false.B)   
    val datain_format_out = RegInit(false.B)
    val pixel_format_out = RegInit("b001100".asUInt(6.W))  
    val pixel_mapping_out = RegInit(false.B) 
    val pixel_sign_override_out = RegInit(false.B)
    val datain_height_out = RegInit("b0".asUInt(13.W))
    val datain_width_out = RegInit("b0".asUInt(13.W))   
    val datain_channel_out = RegInit("b0".asUInt(13.W))
    val datain_height_ext_out = RegInit("b0".asUInt(13.W))
    val datain_width_ext_out = RegInit("b0".asUInt(13.W)) 
    val entries_out = RegInit("b0".asUInt(14.W))
    val grains_out = RegInit("b0".asUInt(12.W))   
    val line_stride_out = RegInit("b0".asUInt(32.W))
    val uv_line_stride_out = RegInit("b0".asUInt(32.W))
    val mean_format_out = RegInit(false.B)  
    val mean_gu_out = RegInit("b0".asUInt(16.W))
    val mean_ry_out = RegInit("b0".asUInt(16.W))
    val mean_ax_out = RegInit("b0".asUInt(16.W))   
    val mean_bv_out = RegInit("b0".asUInt(16.W))
    val conv_mode_out = RegInit(false.B)
    val data_reuse_out = RegInit(false.B)  
    val in_precision_out = RegInit("b01".asUInt(2.W))
    val proc_precision_out = RegInit("b01".asUInt(2.W))   
    val skip_data_rls_out = RegInit(false.B)
    val skip_weight_rls_out = RegInit(false.B)
    val weight_reuse_out = RegInit(false.B)  
    val nan_to_zero_out = RegInit(false.B)
    val dma_en_out = RegInit(false.B)
    val pixel_x_offset_out = RegInit("b0".asUInt(5.W))   
    val pixel_y_offset_out = RegInit("b0".asUInt(3.W))
    val rsv_per_line_out = RegInit("b0".asUInt(10.W))
    val rsv_per_uv_line_out = RegInit("b0".asUInt(10.W))      
    val rsv_height_out = RegInit("b0".asUInt(3.W))
    val rsv_y_index_out = RegInit("b0".asUInt(5.W))  
    val surf_stride_out = RegInit("b0".asUInt(32.W))
    val weight_addr_high_out = RegInit("b0".asUInt(32.W))
    val weight_addr_low_out = RegInit("b0".asUInt(32.W))   
    val weight_bytes_out = RegInit("b0".asUInt(32.W))
    val weight_format_out = RegInit(false.B)
    val weight_ram_type_out = RegInit(false.B)      
    val byte_per_kernel_out = RegInit("b0".asUInt(18.W))
    val weight_kernel_out = RegInit("b0".asUInt(13.W))  
    val wgs_addr_high_out = RegInit("b0".asUInt(32.W))
    val wgs_addr_low_out = RegInit("b0".asUInt(32.W))
    val wmb_addr_high_out = RegInit("b0".asUInt(32.W))   
    val wmb_addr_low_out = RegInit("b0".asUInt(32.W))
    val wmb_bytes_out = RegInit("b0".asUInt(28.W))
    val pad_bottom_out = RegInit("b0".asUInt(6.W)) 
    val pad_left_out = RegInit("b0".asUInt(5.W))
    val pad_right_out = RegInit("b0".asUInt(6.W))   
    val pad_top_out = RegInit("b0".asUInt(5.W))
    val pad_value_out = RegInit("b0".asUInt(16.W))      
   
  // Register: NVDLA_CDMA_D_BANK_0    Field: data_bank
    when(nvdla_cdma_d_bank_0_wren){
        data_bank_out := io.reg_wr_data(4, 0)
    } 

  // Register: NVDLA_CDMA_D_BANK_0    Field: weight_bank
    when(nvdla_cdma_d_bank_0_wren){
        weight_bank_out := io.reg_wr_data(20, 16)
    }

  // Register: NVDLA_CDMA_D_BATCH_NUMBER_0    Field: batches
    when(nvdla_cdma_d_batch_number_0_wren){
        batches_out := io.reg_wr_data(4, 0)
    }

  // Register: NVDLA_CDMA_D_BATCH_STRIDE_0    Field: batch_stride
    when(nvdla_cdma_d_batch_stride_0_wren){
        batch_stride_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_CONV_STRIDE_0    Field: conv_x_stride
    when(nvdla_cdma_d_conv_stride_0_wren){
        conv_x_stride_out := io.reg_wr_data(2, 0)
    }

  // Register: NVDLA_CDMA_D_CONV_STRIDE_0    Field: conv_y_stride
    when(nvdla_cdma_d_conv_stride_0_wren){
        conv_y_stride_out := io.reg_wr_data(18, 16)
    }

  // Register: NVDLA_CDMA_D_CVT_CFG_0    Field: cvt_en
    when(nvdla_cdma_d_cvt_cfg_0_wren){
        cvt_en_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_CDMA_D_CVT_CFG_0    Field: cvt_truncate
    when(nvdla_cdma_d_cvt_cfg_0_wren){
        cvt_truncate_out := io.reg_wr_data(9, 4)
    }

  // Register: NVDLA_CDMA_D_CVT_OFFSET_0    Field: cvt_offset
    when(nvdla_cdma_d_cvt_offset_0_wren){
        cvt_offset_out := io.reg_wr_data(15, 0)
    }

  // Register: NVDLA_CDMA_D_CVT_SCALE_0    Field: cvt_scale
    when(nvdla_cdma_d_cvt_scale_0_wren){
        cvt_scale_out := io.reg_wr_data(15, 0)
    }

  // Register: NVDLA_CDMA_D_CYA_0    Field: cya
    when(nvdla_cdma_d_cya_0_wren){
        cya_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_DAIN_ADDR_HIGH_0_0    Field: datain_addr_high_0
    when(nvdla_cdma_d_dain_addr_high_0_0_wren){
        datain_addr_high_0_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_DAIN_ADDR_HIGH_1_0    Field: datain_addr_high_1
    when(nvdla_cdma_d_dain_addr_high_1_0_wren){
        datain_addr_high_1_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_DAIN_ADDR_LOW_0_0    Field: datain_addr_low_0
    when(nvdla_cdma_d_dain_addr_low_0_0_wren){
        datain_addr_low_0_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_DAIN_ADDR_LOW_1_0    Field: datain_addr_low_1
    when(nvdla_cdma_d_dain_addr_low_1_0_wren){
        datain_addr_low_1_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_DAIN_MAP_0    Field: line_packed
    when(nvdla_cdma_d_dain_map_0_wren){
        line_packed_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_CDMA_D_DAIN_MAP_0    Field: surf_packed
    when(nvdla_cdma_d_dain_map_0_wren){
        surf_packed_out := io.reg_wr_data(16)
    }

  // Register: NVDLA_CDMA_D_DAIN_RAM_TYPE_0    Field: datain_ram_type
    when(nvdla_cdma_d_dain_ram_type_0_wren){
        datain_ram_type_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_CDMA_D_DATAIN_FORMAT_0    Field: datain_format
    when(nvdla_cdma_d_datain_format_0_wren){
        datain_format_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_CDMA_D_DATAIN_FORMAT_0    Field: pixel_format
    when(nvdla_cdma_d_datain_format_0_wren){
        pixel_format_out := io.reg_wr_data(13, 8)
    }

  // Register: NVDLA_CDMA_D_DATAIN_FORMAT_0    Field: pixel_mapping
    when(nvdla_cdma_d_datain_format_0_wren){
        pixel_mapping_out := io.reg_wr_data(16)
    }

  // Register: NVDLA_CDMA_D_DATAIN_FORMAT_0    Field: pixel_sign_override
    when(nvdla_cdma_d_datain_format_0_wren){
        pixel_sign_override_out := io.reg_wr_data(20)
    }

  // Register: NVDLA_CDMA_D_DATAIN_SIZE_0_0    Field: datain_height
    when(nvdla_cdma_d_datain_size_0_0_wren){
        datain_height_out := io.reg_wr_data(28, 16)
    }

  // Register: NVDLA_CDMA_D_DATAIN_SIZE_0_0    Field: datain_width
    when(nvdla_cdma_d_datain_size_0_0_wren){
        datain_width_out := io.reg_wr_data(12, 0)
    }

  // Register: NVDLA_CDMA_D_DATAIN_SIZE_1_0    Field: datain_channel
    when(nvdla_cdma_d_datain_size_1_0_wren){
        datain_channel_out := io.reg_wr_data(12, 0)
    }

  // Register: NVDLA_CDMA_D_DATAIN_SIZE_EXT_0_0    Field: datain_height_ext
    when(nvdla_cdma_d_datain_size_ext_0_0_wren){
        datain_height_ext_out := io.reg_wr_data(28, 16)
    }

  // Register: NVDLA_CDMA_D_DATAIN_SIZE_EXT_0_0    Field: datain_width_ext
    when(nvdla_cdma_d_datain_size_ext_0_0_wren){
        datain_width_ext_out := io.reg_wr_data(12, 0)
    }

  // Register: NVDLA_CDMA_D_ENTRY_PER_SLICE_0    Field: entries
    when(nvdla_cdma_d_entry_per_slice_0_wren){
        entries_out := io.reg_wr_data(13, 0)
    }

  // Register: NVDLA_CDMA_D_FETCH_GRAIN_0    Field: grains
    when(nvdla_cdma_d_fetch_grain_0_wren){
        grains_out := io.reg_wr_data(11, 0)
    }

  // Not generating flops for read-only field NVDLA_CDMA_D_INF_INPUT_DATA_NUM_0::inf_data_num

  // Not generating flops for read-only field NVDLA_CDMA_D_INF_INPUT_WEIGHT_NUM_0::inf_weight_num

  // Register: NVDLA_CDMA_D_LINE_STRIDE_0    Field: line_stride
    when(nvdla_cdma_d_perf_enable_0_wren){
        line_stride_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_LINE_UV_STRIDE_0    Field: uv_line_stride
    when(nvdla_cdma_d_line_uv_stride_0_wren){
        uv_line_stride_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_MEAN_FORMAT_0    Field: mean_format
    when(nvdla_cdma_d_mean_format_0_wren){
        mean_format_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_CDMA_D_MEAN_GLOBAL_0_0    Field: mean_gu
    when(nvdla_cdma_d_mean_global_0_0_wren){
        mean_gu_out := io.reg_wr_data(31, 16)
    }

  // Register: NVDLA_CDMA_D_MEAN_GLOBAL_0_0    Field: mean_ry
    when(nvdla_cdma_d_mean_global_0_0_wren){
        mean_ry_out := io.reg_wr_data(15, 0)
    }

  // Register: NVDLA_CDMA_D_MEAN_GLOBAL_1_0    Field: mean_ax
    when(nvdla_cdma_d_mean_global_1_0_wren){
        mean_ax_out := io.reg_wr_data(31, 16)
    }

  // Register: NVDLA_CDMA_D_MEAN_GLOBAL_1_0    Field: mean_bv
    when(nvdla_cdma_d_mean_global_1_0_wren){
        mean_bv_out := io.reg_wr_data(15, 0)
    }

  // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: conv_mode
    when(nvdla_cdma_d_misc_cfg_0_wren){
        conv_mode_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: data_reuse
    when(nvdla_cdma_d_misc_cfg_0_wren){
        data_reuse_out := io.reg_wr_data(16)
    }

  // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: in_precision
    when(nvdla_cdma_d_misc_cfg_0_wren){
        in_precision_out := io.reg_wr_data(9, 8)
    }

  // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: proc_precision
    when(nvdla_cdma_d_misc_cfg_0_wren){
        proc_precision_out := io.reg_wr_data(13, 12)
    }

  // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: skip_data_rls
    when(nvdla_cdma_d_misc_cfg_0_wren){
        skip_data_rls_out := io.reg_wr_data(24)
    }

  // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: skip_weight_rls
    when(nvdla_cdma_d_misc_cfg_0_wren){
        skip_weight_rls_out := io.reg_wr_data(28)
    }

  // Register: NVDLA_CDMA_D_MISC_CFG_0    Field: weight_reuse
    when(nvdla_cdma_d_misc_cfg_0_wren){
        weight_reuse_out := io.reg_wr_data(20)
    }

  // Register: NVDLA_CDMA_D_NAN_FLUSH_TO_ZERO_0    Field: nan_to_zero
    when(nvdla_cdma_d_nan_flush_to_zero_0_wren){
        nan_to_zero_out := io.reg_wr_data(0)
    }
  // Not generating flops for read-only field NVDLA_CDMA_D_NAN_INPUT_DATA_NUM_0::nan_data_num

  // Not generating flops for read-only field NVDLA_CDMA_D_NAN_INPUT_WEIGHT_NUM_0::nan_weight_num

  // Not generating flops for field NVDLA_CDMA_D_OP_ENABLE_0::op_en (to be implemented outside)

  // Not generating flops for read-only field NVDLA_CDMA_D_PERF_DAT_READ_LATENCY_0::dat_rd_latency

  // Not generating flops for read-only field NVDLA_CDMA_D_PERF_DAT_READ_STALL_0::dat_rd_stall

  // Register: NVDLA_CDMA_D_PERF_ENABLE_0    Field: dma_en
    when(nvdla_cdma_d_perf_enable_0_wren){
        dma_en_out := io.reg_wr_data(0)
    }

  // Not generating flops for read-only field NVDLA_CDMA_D_PERF_WT_READ_LATENCY_0::wt_rd_latency

  // Not generating flops for read-only field NVDLA_CDMA_D_PERF_WT_READ_STALL_0::wt_rd_stall

  // Register: NVDLA_CDMA_D_PIXEL_OFFSET_0    Field: pixel_x_offset
    when(nvdla_cdma_d_pixel_offset_0_wren){
        pixel_x_offset_out := io.reg_wr_data(4, 0)
    }

  // Register: NVDLA_CDMA_D_PIXEL_OFFSET_0    Field: pixel_y_offset
    when(nvdla_cdma_d_pixel_offset_0_wren){
        pixel_y_offset_out := io.reg_wr_data(18, 16)
    }

  // Register: NVDLA_CDMA_D_RESERVED_X_CFG_0    Field: rsv_per_line
    when(nvdla_cdma_d_reserved_x_cfg_0_wren){
        rsv_per_line_out := io.reg_wr_data(9, 0)
    }

  // Register: NVDLA_CDMA_D_RESERVED_X_CFG_0    Field: rsv_per_uv_line
    when(nvdla_cdma_d_reserved_x_cfg_0_wren){
        rsv_per_uv_line_out := io.reg_wr_data(25, 16)
    }

  // Register: NVDLA_CDMA_D_RESERVED_Y_CFG_0    Field: rsv_height
    when(nvdla_cdma_d_reserved_y_cfg_0_wren){
        rsv_height_out := io.reg_wr_data(2, 0)
    }

  // Register: NVDLA_CDMA_D_RESERVED_Y_CFG_0    Field: rsv_y_index
    when(nvdla_cdma_d_reserved_y_cfg_0_wren){
        rsv_y_index_out := io.reg_wr_data(20, 16)
    }

  // Register: NVDLA_CDMA_D_SURF_STRIDE_0    Field: surf_stride
    when(nvdla_cdma_d_surf_stride_0_wren){
        surf_stride_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_WEIGHT_ADDR_HIGH_0    Field: weight_addr_high
    when(nvdla_cdma_d_weight_addr_high_0_wren){
        weight_addr_high_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_WEIGHT_ADDR_LOW_0    Field: weight_addr_low
    when(nvdla_cdma_d_weight_addr_low_0_wren){
        weight_addr_low_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_WEIGHT_BYTES_0    Field: weight_bytes
    when(nvdla_cdma_d_weight_bytes_0_wren){
        weight_bytes_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_WEIGHT_FORMAT_0    Field: weight_format
    when(nvdla_cdma_d_weight_format_0_wren){
        weight_format_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_CDMA_D_WEIGHT_RAM_TYPE_0    Field: weight_ram_type
    when(nvdla_cdma_d_weight_ram_type_0_wren){
        weight_ram_type_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_CDMA_D_WEIGHT_SIZE_0_0    Field: byte_per_kernel
    when(nvdla_cdma_d_weight_size_0_0_wren){
        byte_per_kernel_out := io.reg_wr_data(17, 0)
    }

  // Register: NVDLA_CDMA_D_WEIGHT_SIZE_1_0    Field: weight_kernel
    when(nvdla_cdma_d_weight_size_1_0_wren){
        weight_kernel_out := io.reg_wr_data(12, 0)
    }

  // Register: NVDLA_CDMA_D_WGS_ADDR_HIGH_0    Field: wgs_addr_high
    when(nvdla_cdma_d_wgs_addr_high_0_wren){
        wgs_addr_high_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_WGS_ADDR_LOW_0    Field: wgs_addr_low
    when(nvdla_cdma_d_wgs_addr_low_0_wren){
        wgs_addr_low_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_WMB_ADDR_HIGH_0    Field: wmb_addr_high
    when(nvdla_cdma_d_wmb_addr_high_0_wren){
        wmb_addr_high_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_WMB_ADDR_LOW_0    Field: wmb_addr_low
    when(nvdla_cdma_d_wmb_addr_low_0_wren){
        wmb_addr_low_out := io.reg_wr_data(31, 0)
    }

  // Register: NVDLA_CDMA_D_WMB_BYTES_0    Field: wmb_bytes
    when(nvdla_cdma_d_wmb_bytes_0_wren){
        wmb_bytes_out := io.reg_wr_data(27, 0)
    }

  // Register: NVDLA_CDMA_D_ZERO_PADDING_0    Field: pad_bottom
    when(nvdla_cdma_d_zero_padding_0_wren){
        pad_bottom_out := io.reg_wr_data(29, 24)
    }

  // Register: NVDLA_CDMA_D_ZERO_PADDING_0    Field: pad_left
    when(nvdla_cdma_d_zero_padding_0_wren){
        pad_left_out := io.reg_wr_data(4, 0)
    }

  // Register: NVDLA_CDMA_D_ZERO_PADDING_0    Field: pad_right
    when(nvdla_cdma_d_zero_padding_0_wren){
        pad_right_out := io.reg_wr_data(13, 8)
    }

  // Register: NVDLA_CDMA_D_ZERO_PADDING_0    Field: pad_top
    when(nvdla_cdma_d_zero_padding_0_wren){
        pad_top_out := io.reg_wr_data(20, 16)
    }

  // Register: NVDLA_CDMA_D_ZERO_PADDING_VALUE_0    Field: pad_value
    when(nvdla_cdma_d_zero_padding_value_0_wren){
        pad_value_out := io.reg_wr_data(15, 0)
    }

    io.data_bank := data_bank_out
    io.weight_bank := weight_bank_out
    io.batches := batches_out
    io.batch_stride := batch_stride_out
    io.conv_x_stride := conv_x_stride_out
    io.conv_y_stride := conv_y_stride_out
    io.cvt_en := cvt_en_out
    io.cvt_truncate := cvt_truncate_out
    io.cvt_offset := cvt_offset_out
    io.cvt_scale := cvt_scale_out
    io.cya := cya_out
    io.datain_addr_high_0 := datain_addr_high_0_out
    io.datain_addr_high_1 := datain_addr_high_1_out
    io.datain_addr_low_0 := datain_addr_low_0_out
    io.datain_addr_low_1 := datain_addr_low_1_out
    io.line_packed := line_packed_out
    io.surf_packed := surf_packed_out
    io.datain_ram_type := datain_ram_type_out
    io.datain_format := datain_format_out
    io.pixel_format := pixel_format_out
    io.pixel_mapping := pixel_mapping_out
    io.pixel_sign_override := pixel_sign_override_out
    io.datain_height := datain_height_out
    io.datain_width := datain_width_out
    io.datain_channel := datain_channel_out
    io.datain_height_ext := datain_height_ext_out
    io.datain_width_ext := datain_width_ext_out
    io.entries := entries_out
    io.grains := grains_out
    io.line_stride := line_stride_out
    io.uv_line_stride := uv_line_stride_out
    io.mean_format := mean_format_out
    io.mean_gu := mean_gu_out
    io.mean_ry := mean_ry_out
    io.mean_ax := mean_ax_out
    io.mean_bv := mean_bv_out
    io.conv_mode := conv_mode_out
    io.data_reuse := data_reuse_out
    io.in_precision := in_precision_out
    io.proc_precision := proc_precision_out
    io.skip_data_rls := skip_data_rls_out
    io.skip_weight_rls := skip_weight_rls_out
    io.weight_reuse := weight_reuse_out
    io.nan_to_zero := nan_to_zero_out
    io.dma_en := dma_en_out
    io.pixel_x_offset := pixel_x_offset_out
    io.pixel_y_offset := pixel_x_offset_out
    io.rsv_per_line := rsv_per_line_out
    io.rsv_per_uv_line := rsv_per_uv_line_out
    io.rsv_height := rsv_height_out
    io.rsv_y_index := rsv_y_index_out
    io.surf_stride := surf_stride_out
    io.weight_addr_high := weight_addr_high_out
    io.weight_addr_low := weight_addr_low_out
    io.weight_bytes := weight_bytes_out
    io.weight_format := weight_format_out
    io.weight_ram_type := weight_ram_type_out
    io.byte_per_kernel := byte_per_kernel_out
    io.weight_kernel := weight_kernel_out
    io.wgs_addr_high := wgs_addr_high_out
    io.wgs_addr_low := wgs_addr_low_out
    io.wmb_addr_high := wmb_addr_high_out
    io.wmb_addr_low := wmb_addr_low_out
    io.wmb_bytes := wmb_bytes_out
    io.pad_bottom := pad_bottom_out
    io.pad_left := pad_left_out
    io.pad_right := pad_right_out
    io.pad_top := pad_top_out
    io.pad_value := pad_value_out


}}