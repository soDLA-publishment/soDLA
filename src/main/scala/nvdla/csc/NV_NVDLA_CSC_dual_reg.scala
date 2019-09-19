package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CSC_dual_reg extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        //Register control interface
        val reg = new reg_control_if

        //Writable register flop/trigger outputs
        val field = new csc_dual_reg_flop_outputs
        val op_en_trigger = Output(Bool())

        //Read-only register inputs
        val op_en = Input(Bool())    
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
    val nvdla_csc_d_atomics_0_wren = (io.reg.offset === "h44".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_bank_0_wren = (io.reg.offset ===  "h5c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_batch_number_0_wren = (io.reg.offset ===  "h1c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_conv_stride_ext_0_wren = (io.reg.offset ===  "h4c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_cya_0_wren = (io.reg.offset === "h64".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_datain_format_0_wren = (io.reg.offset === "h10".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_datain_size_ext_0_0_wren = (io.reg.offset === "h14".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_datain_size_ext_1_0_wren = (io.reg.offset === "h18".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_dataout_size_0_0_wren = (io.reg.offset === "h3c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_dataout_size_1_0_wren = (io.reg.offset === "h40".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_dilation_ext_0_wren = (io.reg.offset === "h50".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_entry_per_slice_0_wren = (io.reg.offset === "h24".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_misc_cfg_0_wren = (io.reg.offset === "h0c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_op_enable_0_wren = (io.reg.offset === "h08".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_post_y_extension_0_wren = (io.reg.offset === "h20".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_pra_cfg_0_wren = (io.reg.offset === "h60".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_release_0_wren = (io.reg.offset === "h48".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_weight_bytes_0_wren = (io.reg.offset === "h34".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_weight_format_0_wren = (io.reg.offset === "h28".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_weight_size_ext_0_0_wren = (io.reg.offset === "h2c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_weight_size_ext_1_0_wren = (io.reg.offset === "h30".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_wmb_bytes_0_wren = (io.reg.offset === "h38".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_zero_padding_0_wren = (io.reg.offset === "h54".asUInt(32.W)) & io.reg.wr_en
    val nvdla_csc_d_zero_padding_value_0_wren = (io.reg.offset === "h58".asUInt(32.W)) & io.reg.wr_en

    io.op_en_trigger := nvdla_csc_d_op_enable_0_wren

    //Output mux

    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(  
    //nvdla_csc_d_atomics_0_out    
    "h44".asUInt(32.W)  -> Cat("b0".asUInt(11.W), io.field.atomics),
    //nvdla_csc_d_bank_0_out
    "h5c".asUInt(32.W)  -> Cat("b0".asUInt(11.W), io.field.weight_bank, "b0".asUInt(11.W), io.field.data_bank),
    //nvdla_csc_d_batch_number_0_out
    "h1c".asUInt(32.W)  -> Cat("b0".asUInt(27.W), io.field.batches),
    //nvdla_csc_d_conv_stride_ext_0_out
    "h4c".asUInt(32.W)  -> Cat("b0".asUInt(13.W), io.field.conv_y_stride_ext, "b0".asUInt(13.W), io.field.conv_x_stride_ext),
    //nvdla_csc_d_cya_0_out
    "h64".asUInt(32.W)  -> io.field.cya,
    //nvdla_csc_d_datain_format_0_out
    "h10".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.field.datain_format),
    //nvdla_csc_d_datain_size_ext_0_0_out
    "h14".asUInt(32.W)  -> Cat("b0".asUInt(3.W), io.field.datain_height_ext, "b0".asUInt(3.W), io.field.datain_width_ext),
    //nvdla_csc_d_datain_size_ext_1_0_out
    "h18".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.datain_channel_ext),
    //nvdla_csc_d_dataout_size_0_0_out
    "h3c".asUInt(32.W)  -> Cat("b0".asUInt(3.W), io.field.dataout_height, "b0".asUInt(3.W), io.field.dataout_width),
    //nvdla_csc_d_dataout_size_1_0_out 
    "h40".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.dataout_channel),
    //nvdla_csc_d_dilation_ext_0_out
    "h50".asUInt(32.W)  -> Cat("b0".asUInt(11.W), io.field.y_dilation_ext, "b0".asUInt(11.W), io.field.x_dilation_ext),
    //nvdla_csc_d_entry_per_slice_0_out
    "h24".asUInt(32.W)  -> Cat("b0".asUInt(18.W), io.field.entries),
    //nvdla_csc_d_misc_cfg_0_out
    "h0c".asUInt(32.W)  -> Cat("b0".asUInt(3.W), io.field.skip_weight_rls, "b0".asUInt(3.W), io.field.skip_data_rls, 
                                "b0".asUInt(3.W), io.field.weight_reuse, "b0".asUInt(3.W), io.field.data_reuse, "b0".asUInt(2.W), 
                                io.field.proc_precision, "b0".asUInt(2.W), io.field.in_precision, "b0".asUInt(7.W), io.field.conv_mode), 
    //nvdla_csc_d_op_enable_0_out
    "h08".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.op_en), 
    //nvdla_csc_d_post_y_extension_0_out
    "h20".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.y_extension), 
    //nvdla_csc_d_pra_cfg_0_out
    "h60".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.pra_truncate), 
    //nvdla_csc_d_release_0_out
    "h48".asUInt(32.W)  -> Cat("b0".asUInt(20.W), io.field.rls_slices), 
    //nvdla_csc_d_weight_bytes_0_out 
    "h34".asUInt(32.W)  -> io.field.weight_bytes, 
    //nvdla_csc_d_weight_format_0_out
    "h28".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.field.weight_format), 
    //nvdla_csc_d_weight_size_ext_0_0_out
    "h2c".asUInt(32.W)  -> Cat("b0".asUInt(11.W), io.field.weight_height_ext, "b0".asUInt(11.W), io.field.weight_width_ext), 
    //nvdla_csc_d_weight_size_ext_1_0_out
    "h30".asUInt(32.W)  -> Cat("b0".asUInt(3.W), io.field.weight_kernel, "b0".asUInt(3.W), io.field.weight_channel_ext), 
    //nvdla_csc_d_wmb_bytes_0_out
    "h38".asUInt(32.W)  -> Cat("b0".asUInt(4.W), io.field.wmb_bytes, "b0".asUInt(7.W)), 
    //nvdla_csc_d_zero_padding_0_out
    "h54".asUInt(32.W)  -> Cat("b0".asUInt(11.W), io.field.pad_top, "b0".asUInt(11.W), io.field.pad_left),
    //nvdla_csc_d_zero_padding_value_0_out
    "h58".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.pad_value)                                                                                    
    ))

    //Register flop declarations

    // Register: NVDLA_CSC_D_ATOMICS_0    Field: atomics
    io.field.atomics := RegEnable(io.reg.wr_data(20,0), "b1".asUInt(21.W), nvdla_csc_d_atomics_0_wren)
    //Register: NVDLA_CSC_D_BANK_0    Field  data_bank
    io.field.data_bank := RegEnable(io.reg.wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_bank_0_wren)
    //Register: NVDLA_CSC_D_BANK_0    Field: weight_bank    
    io.field.weight_bank := RegEnable(io.reg.wr_data(20,16), "b0".asUInt(5.W), nvdla_csc_d_bank_0_wren)
    //Register: NVDLA_CSC_D_BATCH_NUMBER_0    Field: batches
    io.field.batches := RegEnable(io.reg.wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_batch_number_0_wren)
    //Register: NVDLA_CSC_D_CONV_STRIDE_EXT_0    Field: conv_x_stride_ext
    io.field.conv_x_stride_ext := RegEnable(io.reg.wr_data(2,0), "b0".asUInt(3.W), nvdla_csc_d_conv_stride_ext_0_wren)
    //Register: NVDLA_CSC_D_CONV_STRIDE_EXT_0    Field: conv_y_stride_ext
    io.field.conv_y_stride_ext := RegEnable(io.reg.wr_data(18,16), "b0".asUInt(3.W), nvdla_csc_d_conv_stride_ext_0_wren)
    //Register: NVDLA_CSC_D_CYA_0    Field: cya
    io.field.cya := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_csc_d_cya_0_wren)
    //Register: NVDLA_CSC_D_DATAIN_FORMAT_0    Field: datain_format
    io.field.datain_format := RegEnable(io.reg.wr_data(0), false.B, nvdla_csc_d_datain_format_0_wren) 
    //Register: NVDLA_CSC_D_DATAIN_SIZE_EXT_0_0    Field: datain_height_ext
    io.field.datain_height_ext := RegEnable(io.reg.wr_data(28,16), "b0".asUInt(13.W), nvdla_csc_d_datain_size_ext_0_0_wren)
    //Register: NVDLA_CSC_D_DATAIN_SIZE_EXT_0_0    Field: datain_width_ext
    io.field.datain_width_ext := RegEnable(io.reg.wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_datain_size_ext_0_0_wren)
    //Register: NVDLA_CSC_D_DATAIN_SIZE_EXT_1_0    Field: datain_channel_ext
    io.field.datain_channel_ext := RegEnable(io.reg.wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_datain_size_ext_1_0_wren)
    //Register: NVDLA_CSC_D_DATAOUT_SIZE_0_0    Field: dataout_height
    io.field.dataout_height := RegEnable(io.reg.wr_data(28,16), "b0".asUInt(13.W), nvdla_csc_d_dataout_size_0_0_wren)
    //Register: NVDLA_CSC_D_DATAOUT_SIZE_0_0    Field: dataout_width
    io.field.dataout_width := RegEnable(io.reg.wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_dataout_size_0_0_wren)
    //Register: NVDLA_CSC_D_DATAOUT_SIZE_1_0    Field: dataout_channel
    io.field.dataout_channel := RegEnable(io.reg.wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_dataout_size_1_0_wren)
    //Register: NVDLA_CSC_D_DILATION_EXT_0    Field: x_dilation_ext
    io.field.x_dilation_ext := RegEnable(io.reg.wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_dilation_ext_0_wren)
    //Register: NVDLA_CSC_D_DILATION_EXT_0    Field: y_dilation_ext
    io.field.y_dilation_ext := RegEnable(io.reg.wr_data(20,16), "b0".asUInt(5.W), nvdla_csc_d_dilation_ext_0_wren)
    //Register: NVDLA_CSC_D_ENTRY_PER_SLICE_0    Field: entries
    io.field.entries := RegEnable(io.reg.wr_data(13,0), "b0".asUInt(14.W), nvdla_csc_d_entry_per_slice_0_wren)
    //Register: NVDLA_CSC_D_MISC_CFG_0    Field: conv_mode
    io.field.conv_mode := RegEnable(io.reg.wr_data(0), false.B, nvdla_csc_d_misc_cfg_0_wren)
    //Register: NVDLA_CSC_D_MISC_CFG_0    Field: data_reuse
    io.field.data_reuse := RegEnable(io.reg.wr_data(16), false.B, nvdla_csc_d_misc_cfg_0_wren)
    //Register: NVDLA_CSC_D_MISC_CFG_0    Field: in_precision
    io.field.in_precision := RegEnable(io.reg.wr_data(9,8), "b1".asUInt(2.W), nvdla_csc_d_misc_cfg_0_wren)
    //Register: NVDLA_CSC_D_MISC_CFG_0    Field: proc_precision
    io.field.proc_precision := RegEnable(io.reg.wr_data(13, 12), "b1".asUInt(2.W), nvdla_csc_d_misc_cfg_0_wren)
    //Register: NVDLA_CSC_D_MISC_CFG_0    Field: skip_data_rls
    io.field.skip_data_rls := RegEnable(io.reg.wr_data(24), false.B, nvdla_csc_d_misc_cfg_0_wren)
    //Register: NVDLA_CSC_D_MISC_CFG_0    Field: skip_weight_rls
    io.field.skip_weight_rls := RegEnable(io.reg.wr_data(28), false.B, nvdla_csc_d_misc_cfg_0_wren)
    //Register: NVDLA_CSC_D_MISC_CFG_0    Field: weight_reuse
    io.field.weight_reuse := RegEnable(io.reg.wr_data(20), false.B, nvdla_csc_d_misc_cfg_0_wren)
    //Register: NVDLA_CSC_D_POST_Y_EXTENSION_0    Field: y_extension
    io.field.y_extension := RegEnable(io.reg.wr_data(1,0), "b0".asUInt(2.W), nvdla_csc_d_post_y_extension_0_wren)
    //Register: NVDLA_CSC_D_PRA_CFG_0    Field: pra_truncate
    io.field.pra_truncate := RegEnable(io.reg.wr_data(1,0), "b0".asUInt(2.W), nvdla_csc_d_pra_cfg_0_wren)
    //Register: NVDLA_CSC_D_RELEASE_0    Field: rls_slices
    io.field.rls_slices := RegEnable(io.reg.wr_data(11,0), "b1".asUInt(12.W), nvdla_csc_d_release_0_wren)
    //Register: NVDLA_CSC_D_WEIGHT_BYTES_0    Field: weight_bytes
    io.field.weight_bytes := RegEnable(io.reg.wr_data(31,0), "b0".asUInt(32.W), nvdla_csc_d_weight_bytes_0_wren)
    //Register: NVDLA_CSC_D_WEIGHT_FORMAT_0    Field: weight_format
    io.field.weight_format := RegEnable(io.reg.wr_data(0), false.B, nvdla_csc_d_weight_format_0_wren)
    //Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_0_0    Field: weight_height_ext
    io.field.weight_height_ext := RegEnable(io.reg.wr_data(20,16), "b0".asUInt(5.W), nvdla_csc_d_weight_size_ext_0_0_wren)
    //Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_0_0    Field: weight_width_ext
    io.field.weight_width_ext := RegEnable(io.reg.wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_weight_size_ext_0_0_wren)
    //Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_1_0    Field: weight_channel_ext
    io.field.weight_channel_ext := RegEnable(io.reg.wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_weight_size_ext_1_0_wren)
    //Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_1_0    Field: weight_kernel
    io.field.weight_kernel := RegEnable(io.reg.wr_data(28,16), "b0".asUInt(13.W), nvdla_csc_d_weight_size_ext_1_0_wren)
    //Register: NVDLA_CSC_D_WMB_BYTES_0    Field: wmb_bytes
    io.field.wmb_bytes := RegEnable(io.reg.wr_data(27,0), "b0".asUInt(28.W), nvdla_csc_d_wmb_bytes_0_wren)
    //Register: NVDLA_CSC_D_ZERO_PADDING_0    Field: pad_left
    io.field.pad_left := RegEnable(io.reg.wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_zero_padding_0_wren)
    //Register: NVDLA_CSC_D_ZERO_PADDING_0    Field: pad_top
    io.field.pad_top := RegEnable(io.reg.wr_data(20,16), "b0".asUInt(5.W), nvdla_csc_d_zero_padding_0_wren)
    //Register: NVDLA_CSC_D_ZERO_PADDING_VALUE_0    Field: pad_value
    io.field.pad_value := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_csc_d_zero_padding_value_0_wren)                                                                   

}}