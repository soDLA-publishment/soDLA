package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._




class NV_NVDLA_CSC_dual_reg extends RawModule{
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        // Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)

        val reg_wr_en = Input(Bool())

        // Writable register flop/trigger outputs

        val atomics = Output(UInt(21.W))
        val data_bank = Output(UInt(5.W))
        val weight_bank = Output(UInt(5.W))
        val batches = Output(UInt(5.W))
        val conv_x_stride_ext = Output(UInt(3.W))
        val conv_y_stride_ext = Output(UInt(3.W))
        val cya = Output(UInt(32.W))
        val datain_format = Output(Bool())
        val datain_height_ext = Output(UInt(13.W))
        val datain_width_ext = Output(UInt(13.W))
        val datain_channel_ext = Output(UInt(13.W))
        val dataout_height = Output(UInt(13.W))
        val dataout_width = Output(UInt(13.W))
        val dataout_channel = Output(UInt(13.W))
        val x_dilation_ext = Output(UInt(5.W))
        val y_dilation_ext = Output(UInt(5.W))
        val entries = Output(UInt(14.W))
        val conv_mode = Output(Bool())
        val data_reuse = Output(Bool())
        val in_precision = Output(UInt(2.W))
        val proc_precision = Output(UInt(2.W))
        val skip_data_rls = Output(Bool())
        val skip_weight_rls = Output(Bool())
        val weight_reuse = Output(Bool())
        val op_en_trigger = Output(Bool())
        val y_extension = Output(UInt(2.W))
        val pra_truncate = Output(UInt(2.W))
        val rls_slices = Output(UInt(12.W))
        val weight_bytes = Output(UInt(32.W))
        val weight_format = Output(Bool())
        val weight_height_ext = Output(UInt(5.W))
        val weight_width_ext = Output(UInt(5.W))
        val weight_channel_ext = Output(UInt(13.W))
        val weight_kernel = Output(UInt(13.W))
        val wmb_bytes = Output(UInt(28.W))
        val pad_left = Output(UInt(5.W))
        val pad_top = Output(UInt(5.W))
        val pad_value = Output(UInt(16.W))

        // Read-only register inputs

        val op_en = Input(Bool())    
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
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

    // Address decode

    val nvdla_csc_d_atomics_0_wren = (reg_offset_wr === "h44".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_bank_0_wren = (reg_offset_wr ===  "h5c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_batch_number_0_wren = (reg_offset_wr ===  "h1c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_conv_stride_ext_0_wren = (reg_offset_wr ===  "h4c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_cya_0_wren = (reg_offset_wr == "h64".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_datain_format_0_wren = (reg_offset_wr === "h10".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_datain_size_ext_0_0_wren = (reg_offset_wr === "h14".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_datain_size_ext_1_0_wren = (reg_offset_wr === "h18".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_dataout_size_0_0_wren = (reg_offset_wr === "h3c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_dataout_size_1_0_wren = (reg_offset_wr === "h40".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_dilation_ext_0_wren = (reg_offset_wr === "h50".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_entry_per_slice_0_wren = (reg_offset_wr === "h24".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_misc_cfg_0_wren = (reg_offset_wr === "h0c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_op_enable_0_wren = (reg_offset_wr === "h08".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_post_y_extension_0_wren = (reg_offset_wr === "h20".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_pra_cfg_0_wren = (reg_offset_wr === "h60".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_release_0_wren = (reg_offset_wr === "h48".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_weight_bytes_0_wren = (reg_offset_wr === "h34".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_weight_format_0_wren = (reg_offset_wr === "h28".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_weight_size_ext_0_0_wren = (reg_offset_wr === "h2c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_weight_size_ext_1_0_wren = (reg_offset_wr === "h30".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_wmb_bytes_0_wren = (reg_offset_wr === "h38".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_zero_padding_0_wren = (reg_offset_wr === "h54".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_csc_d_zero_padding_value_0_wren = (reg_offset_wr === "h58".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)

    val nvdla_csc_d_atomics_0_out = Cat("b0".asUInt(11.W), io.atomics )
    val nvdla_csc_d_bank_0_out = Cat("b0".asUInt(11.W), io.weight_bank, "b0".asUInt(11.W), io.data_bank)
    val nvdla_csc_d_batch_number_0_out = Cat("b0".asUInt(27.W), io.batches )
    val nvdla_csc_d_conv_stride_ext_0_out = Cat("b0".asUInt(13.W), io.conv_y_stride_ext, "b0".asUInt(13.W), io.conv_x_stride_ext )
    val nvdla_csc_d_cya_0_out =  cya 
    val nvdla_csc_d_datain_format_0_out = Cat("b0".asUInt(31.W), io.datain_format)
    val nvdla_csc_d_datain_size_ext_0_0_out = Cat("b0".asUInt(3.W), io.datain_height_ext, "b0".asUInt(3.W), io.datain_width_ext )
    val nvdla_csc_d_datain_size_ext_1_0_out = Cat("b0".asUInt(19.W), io.datain_channel_ext)
    val nvdla_csc_d_dataout_size_0_0_out = Cat("b0".asUInt(3.W), io.dataout_height, "b0".asUInt(3.W), io.dataout_width )
    val nvdla_csc_d_dataout_size_1_0_out = Cat("b0".asUInt(19.W), io.dataout_channel)
    val nvdla_csc_d_dilation_ext_0_out = Cat("b0".asUInt(11.W), io.y_dilation_ext, "b0".asUInt(11.W), io.x_dilation_ext )
    val nvdla_csc_d_entry_per_slice_0_out = Cat("b0".asUInt(18.W), io.entries)
    val nvdla_csc_d_misc_cfg_0_out = Cat("b0".asUInt(3.W), io.skip_weight_rls, "b0".asUInt(3.W), io.skip_data_rls, 
                                    "b0".asUInt(3.W), io.weight_reuse, "b0".asUInt(3.W), io.data_reuse, "b0".asUInt(2.W), 
                                    io.proc_precision, "b0".asUInt(2.W), io.in_precision, "b0".asUInt(7.W), io.conv_mode)
    val nvdla_csc_d_op_enable_0_out = Cat("b0".asUInt(31.W), io.op_en)
    val nvdla_csc_d_post_y_extension_0_out = Cat( "b0".asUInt(30.W), io.y_extension)
    val nvdla_csc_d_pra_cfg_0_out = Cat("b0".asUInt(30.W), io.pra_truncate)
    val nvdla_csc_d_release_0_out = Cat("b0".asUInt(20.W), io.rls_slices)
    val nvdla_csc_d_weight_bytes_0_out = io.weight_bytes
    val nvdla_csc_d_weight_format_0_out = Cat("b0".asUInt(31.W), io.weight_format)
    val nvdla_csc_d_weight_size_ext_0_0_out = Cat("b0".asUInt(11.W), io.weight_height_ext, "b0".asUInt(11.W), io.weight_width_ext )
    val nvdla_csc_d_weight_size_ext_1_0_out = Cat("b0".asUInt(3.W), io.weight_kernel, "b0".asUInt(3.W), io.weight_channel_ext)
    val nvdla_csc_d_wmb_bytes_0_out = Cat("b0".asUInt(4.W), io.wmb_bytes, "b0".asUInt(7.W))
    val nvdla_csc_d_zero_padding_0_out = Cat("b0".asUInt(11.W), io.pad_top, "b0".asUInt(11.W), io.pad_left)
    val nvdla_csc_d_zero_padding_value_0_out = Cat("b0".asUInt(16.W), io.pad_value)

    io.op_en_trigger := nvdla_cmac_a_d_op_enable_0_wren

    // Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
    "h44".asUInt(32.W)  -> nvdla_csc_d_atomics_0_out,
    "h5c".asUInt(32.W)  -> nvdla_csc_d_bank_0_out,
    "h1c".asUInt(32.W)  -> nvdla_csc_d_batch_number_0_out,
    "h4c".asUInt(32.W)  -> nvdla_csc_d_conv_stride_ext_0_out,
    "h64".asUInt(32.W)  -> nvdla_csc_d_cya_0_out,
    "h10".asUInt(32.W)  -> nvdla_csc_d_datain_format_0_out,
    "h14".asUInt(32.W)  -> nvdla_csc_d_datain_size_ext_0_0_out,
    "h18".asUInt(32.W)  -> nvdla_csc_d_datain_size_ext_1_0_out,
    "h3c".asUInt(32.W)  -> nvdla_csc_d_dataout_size_0_0_out,
    "h40".asUInt(32.W)  -> nvdla_csc_d_dataout_size_1_0_out,
    "h50".asUInt(32.W)  -> nvdla_csc_d_dilation_ext_0_out,
    "h24".asUInt(32.W)  -> nvdla_csc_d_entry_per_slice_0_out,
    "h0c".asUInt(32.W)  -> nvdla_csc_d_misc_cfg_0_out, 
    "h08".asUInt(32.W)  -> nvdla_csc_d_op_enable_0_out, 
    "h20".asUInt(32.W)  -> nvdla_csc_d_post_y_extension_0_out, 
    "h60".asUInt(32.W)  -> nvdla_csc_d_pra_cfg_0_out, 
    "h48".asUInt(32.W)  -> nvdla_csc_d_release_0_out, 
    "h34".asUInt(32.W)  -> nvdla_csc_d_weight_bytes_0_out, 
    "h28".asUInt(32.W)  -> nvdla_csc_d_weight_format_0_out, 
    "h2c".asUInt(32.W)  -> nvdla_csc_d_weight_size_ext_0_0_out, 
    "h30".asUInt(32.W)  -> nvdla_csc_d_weight_size_ext_1_0_out, 
    "h38".asUInt(32.W)  -> nvdla_csc_d_wmb_bytes_0_out, 
    "h54".asUInt(32.W)  -> nvdla_csc_d_zero_padding_0_out,
    "h58".asUInt(32.W)  -> nvdla_csc_d_zero_padding_value_0_out                                                                                    
    ))

    // Register flop declarations

    val atomics_out = RegInit("b1".asUInt(21.W))
    val data_bank_out = RegInit("b0".asUInt(5.W))
    val weight_bank_out = RegInit("b0".asUInt(5.W))
    val batches_out = RegInit("b0".asUInt(5.W))
    val conv_x_stride_ext_out = RegInit("b0".asUInt(3.W))
    val conv_y_stride_ext_out = RegInit("b0".asUInt(3.W))
    val cya_out = RegInit("b0".asUInt(32.W))
    val datain_format_out = RegInit(false.B)
    val datain_height_ext_out = RegInit("b0".asUInt(13.W))
    val datain_width_ext_out = RegInit("b0".asUInt(13.W))
    val datain_channel_ext_out = RegInit("b0".asUInt(13.W))
    val dataout_height_out = RegInit("b0".asUInt(13.W))
    val dataout_width_out = RegInit("b0".asUInt(13.W))
    val dataout_channel_out = RegInit("b0".asUInt(13.W))
    val x_dilation_ext_out = RegInit("b0".asUInt(5.W))
    val y_dilation_ext_out = RegInit("b0".asUInt(5.W))
    val entries_out = RegInit("b0".asUInt(14.W))
    val conv_mode_out = RegInit(false.B)
    val data_reuse_out = RegInit(false.B)
    val in_precision_out = RegInit("b1".asUInt(2.W))
    val proc_precision_out = RegInit("b1".asUInt(2.W))
    val skip_data_rls_out = RegInit(false.B)
    val skip_weight_rls_out = RegInit(false.B)
    val weight_reuse_out = RegInit(false.B)
    val y_extension_out = RegInit("b0".asUInt(2.W))
    val pra_truncate_out = RegInit("b0".asUInt(2.W))
    val rls_slices_out = RegInit("b1".asUInt(12.W))
    val weight_bytes_out = RegInit("b0".asUInt(32.W))
    val weight_format_out = RegInit(false.B)
    val weight_height_ext_out = RegInit("b0".asUInt(5.W))
    val weight_width_ext_out = RegInit("b0".asUInt(5.W))
    val weight_channel_ext_out = RegInit("b0".asUInt(13.W))
    val weight_kernel_out = RegInit("b0".asUInt(13.W))
    val wmb_bytes_out = RegInit("b0".asUInt(28.W))
    val pad_left_out = RegInit("b0".asUInt(5.W))
    val pad_top_out = RegInit("b0".asUInt(5.W))
    val pad_value_out = RegInit("b0".asUInt(16.W))

    when(nvdla_csc_d_atomics_0_wren) {
        atomics_out  := reg_wr_data(20,0)
    }

    // Register: NVDLA_CSC_D_BANK_0    Field  data_bank
    when  (nvdla_csc_d_bank_0_wren) {
        data_bank_out  := reg_wr_data(4,0)
    }

    // Register: NVDLA_CSC_D_BANK_0    Field: weight_bank
    when (nvdla_csc_d_bank_0_wren) {
        weight_bank_out  := reg_wr_data(20,16)
    }

    // Register: NVDLA_CSC_D_BATCH_NUMBER_0    Field: batches
    when (nvdla_csc_d_batch_number_0_wren) {
        batches_out  := reg_wr_data(4,0)
    }

    // Register: NVDLA_CSC_D_CONV_STRIDE_EXT_0    Field: conv_x_stride_ext
    when (nvdla_csc_d_conv_stride_ext_0_wren) {
        conv_x_stride_ext_out  := reg_wr_data(2,0)
    }

    // Register: NVDLA_CSC_D_CONV_STRIDE_EXT_0    Field: conv_y_stride_ext
    when (nvdla_csc_d_conv_stride_ext_0_wren) {
        conv_y_stride_ext_out  := reg_wr_data(18,16)
    }

    // Register: NVDLA_CSC_D_CYA_0    Field: cya
    when (nvdla_csc_d_cya_0_wren) {
        cya_out  := reg_wr_data
    }

    // Register: NVDLA_CSC_D_DATAIN_FORMAT_0    Field: datain_format
    when (nvdla_csc_d_datain_format_0_wren) {
        datain_format_out  := reg_wr_data(0)
    }

    // Register: NVDLA_CSC_D_DATAIN_SIZE_EXT_0_0    Field: datain_height_ext
    when (nvdla_csc_d_datain_size_ext_0_0_wren) {
        datain_height_ext_out  := reg_wr_data(28,16)
    }

    // Register: NVDLA_CSC_D_DATAIN_SIZE_EXT_0_0    Field: datain_width_ext
    when (nvdla_csc_d_datain_size_ext_0_0_wren) {
        datain_width_ext_out  := reg_wr_data(12,0)
    }

    // Register: NVDLA_CSC_D_DATAIN_SIZE_EXT_1_0    Field: datain_channel_ext
    when (nvdla_csc_d_datain_size_ext_1_0_wren) {
        datain_channel_ext_out  := reg_wr_data(12,0)
    }

    // Register: NVDLA_CSC_D_DATAOUT_SIZE_0_0    Field: dataout_height
    when (nvdla_csc_d_dataout_size_0_0_wren) {
        dataout_height_out  := reg_wr_data(28,16)
    }

    // Register: NVDLA_CSC_D_DATAOUT_SIZE_0_0    Field: dataout_width
    when (nvdla_csc_d_dataout_size_0_0_wren) {
        dataout_width_out  := reg_wr_data(12,0)
    }

    // Register: NVDLA_CSC_D_DATAOUT_SIZE_1_0    Field: dataout_channel
    when (nvdla_csc_d_dataout_size_1_0_wren) {
        dataout_channel_out  := reg_wr_data(12,0)
    }

    // Register: NVDLA_CSC_D_DILATION_EXT_0    Field: x_dilation_ext
    when (nvdla_csc_d_dilation_ext_0_wren) {
        x_dilation_ext_out  := reg_wr_data(4,0)
    }

    // Register: NVDLA_CSC_D_DILATION_EXT_0    Field: y_dilation_ext
    when (nvdla_csc_d_dilation_ext_0_wren) {
        y_dilation_ext_out  := reg_wr_data(20,16)
    }

    // Register: NVDLA_CSC_D_ENTRY_PER_SLICE_0    Field: entries
    when (nvdla_csc_d_entry_per_slice_0_wren) {
        entries_out  := reg_wr_data(13,0)
    }

    // Register: NVDLA_CSC_D_MISC_CFG_0    Field: conv_mode
    when (nvdla_csc_d_misc_cfg_0_wren) {
        conv_mode_out  := reg_wr_data(0)
    }

    // Register: NVDLA_CSC_D_MISC_CFG_0    Field: data_reuse
    when (nvdla_csc_d_misc_cfg_0_wren) {
        data_reuse_out  := reg_wr_data(16)
    }

    // Register: NVDLA_CSC_D_MISC_CFG_0    Field: in_precision
    when (nvdla_csc_d_misc_cfg_0_wren) {
        in_precision_out  := reg_wr_data(9,8)
    }

    // Register: NVDLA_CSC_D_MISC_CFG_0    Field: proc_precision
    when (nvdla_csc_d_misc_cfg_0_wren) {
        proc_precision_out  := reg_wr_data(13, 12)
    }

    // Register: NVDLA_CSC_D_MISC_CFG_0    Field: skip_data_rls
    when (nvdla_csc_d_misc_cfg_0_wren) {
        skip_data_rls_out  := reg_wr_data(24)
    }

    // Register: NVDLA_CSC_D_MISC_CFG_0    Field: skip_weight_rls
    when (nvdla_csc_d_misc_cfg_0_wren) {
        skip_weight_rls_out  := reg_wr_data(28)
    }

    // Register: NVDLA_CSC_D_MISC_CFG_0    Field: weight_reuse
    when (nvdla_csc_d_misc_cfg_0_wren) {
        weight_reuse_out  := reg_wr_data(20)
    }

    // Not generating flops for field NVDLA_CSC_D_OP_ENABLE_0::op_en (to be implemented outside)

    // Register: NVDLA_CSC_D_POST_Y_EXTENSION_0    Field: y_extension
    when (nvdla_csc_d_post_y_extension_0_wren) {
        y_extension_out  := reg_wr_data(1,0)
    }

    // Register: NVDLA_CSC_D_PRA_CFG_0    Field: pra_truncate
    when (nvdla_csc_d_pra_cfg_0_wren) {
        pra_truncate_out  := reg_wr_data(1,0)
    }

    // Register: NVDLA_CSC_D_RELEASE_0    Field: rls_slices
    when (nvdla_csc_d_release_0_wren) {
        rls_slices_out  := reg_wr_data(11,0)
    }

    // Register: NVDLA_CSC_D_WEIGHT_BYTES_0    Field: weight_bytes
    when (nvdla_csc_d_weight_bytes_0_wren) {
        weight_bytes_out  := reg_wr_data(31,0)
    }

    // Register: NVDLA_CSC_D_WEIGHT_FORMAT_0    Field: weight_format
    when (nvdla_csc_d_weight_format_0_wren) {
        weight_format_out  := reg_wr_data(0)
    }

    // Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_0_0    Field: weight_height_ext
    when (nvdla_csc_d_weight_size_ext_0_0_wren) {
        weight_height_ext_out  := reg_wr_data(20,16)
    }

    // Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_0_0    Field: weight_width_ext
    when (nvdla_csc_d_weight_size_ext_0_0_wren) {
        weight_width_ext_out  := reg_wr_data(4,0)
    }

    // Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_1_0    Field: weight_channel_ext
    when (nvdla_csc_d_weight_size_ext_1_0_wren) {
        weight_channel_ext_out  := reg_wr_data(12,0)
    }

    // Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_1_0    Field: weight_kernel
    when (nvdla_csc_d_weight_size_ext_1_0_wren) {
        weight_kernel_out  := reg_wr_data(28,16)
    }

    // Register: NVDLA_CSC_D_WMB_BYTES_0    Field: wmb_bytes
    when (nvdla_csc_d_wmb_bytes_0_wren) {
        wmb_bytes_out  := reg_wr_data(27,0)
    }

    // Register: NVDLA_CSC_D_ZERO_PADDING_0    Field: pad_left
    when (nvdla_csc_d_zero_padding_0_wren) {
        pad_left_out  := reg_wr_data(4,0)
    }

    // Register: NVDLA_CSC_D_ZERO_PADDING_0    Field: pad_top
    when (nvdla_csc_d_zero_padding_0_wren) {
        pad_top_out  := reg_wr_data(20,16)
    }

    // Register: NVDLA_CSC_D_ZERO_PADDING_VALUE_0    Field: pad_value
    when (nvdla_csc_d_zero_padding_value_0_wren) {
        pad_value_out  := reg_wr_data(15,0)
    }

    io.atomics := atomics_out
    io.data_bank := data_bank_out
    io.weight_bank := weight_bank_out
    io.batches := batches_out
    io.conv_x_stride_ext = conv_x_stride_ext_out
    io.conv_y_stride_ext = conv_y_stride_ext_out
    io.cya = cya_out
    io.datain_format = datain_format_out
    io.datain_height_ext = datain_height_ext_out
    io.datain_width_ext = datain_width_ext_out
    io.datain_channel_ext = datain_channel_ext_out
    io.dataout_height = dataout_height_out
    io.dataout_width = dataout_width_out
    io.dataout_channel = dataout_channel_out
    io.x_dilation_ext = x_dilation_ext_out
    io.y_dilation_ext = y_dilation_ext_out
    io.entries = entries_out
    io.conv_mode = conv_mode_out
    io.data_reuse = data_reuse_out
    io.in_precision = in_precision_out
    io.proc_precision = proc_precision_out
    io.skip_data_rls = skip_data_rls_out
    io.skip_weight_rls = skip_weight_rls_out
    io.weight_reuse = weight_reuse_out
    io.y_extension = y_extension_out
    io.pra_truncate = pra_truncate_out
    io.rls_slices = rls_slices_out
    io.weight_bytes = weight_bytes_out
    io.weight_format = weight_format_out
    io.weight_height_ext = weight_height_ext_out
    io.weight_width_ext = weight_width_ext_out
    io.weight_channel_ext = weight_channel_ext_out
    io.weight_kernel = weight_kernel_out
    io.wmb_bytes = wmb_bytes_out
    io.pad_left = pad_left_out
    io.pad_top = pad_top_out
    io.pad_value = pad_value_out                                                                   


}}