// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CSC_dual_reg extends Module{
//     val io = IO(new Bundle{
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         //Register control interface
//         val reg_control = new reg_control_if

//         //Writable register flop/trigger outputs
//         val atomics = Output(UInt(21.W))
//         val data_bank = Output(UInt(5.W))
//         val weight_bank = Output(UInt(5.W))
//         val batches = Output(UInt(5.W))
//         val conv_x_stride_ext = Output(UInt(3.W))
//         val conv_y_stride_ext = Output(UInt(3.W))
//         val cya = Output(UInt(32.W))
//         val datain_format = Output(Bool())
//         val datain_height_ext = Output(UInt(13.W))
//         val datain_width_ext = Output(UInt(13.W))
//         val datain_channel_ext = Output(UInt(13.W))
//         val dataout_height = Output(UInt(13.W))
//         val dataout_width = Output(UInt(13.W))
//         val dataout_channel = Output(UInt(13.W))
//         val x_dilation_ext = Output(UInt(5.W))
//         val y_dilation_ext = Output(UInt(5.W))
//         val entries = Output(UInt(14.W))
//         val conv_mode = Output(Bool())
//         val data_reuse = Output(Bool())
//         val in_precision = Output(UInt(2.W))
//         val proc_precision = Output(UInt(2.W))
//         val skip_data_rls = Output(Bool())
//         val skip_weight_rls = Output(Bool())
//         val weight_reuse = Output(Bool())
//         val op_en_trigger = Output(Bool())
//         val y_extension = Output(UInt(2.W))
//         val pra_truncate = Output(UInt(2.W))
//         val rls_slices = Output(UInt(12.W))
//         val weight_bytes = Output(UInt(32.W))
//         val weight_format = Output(Bool())
//         val weight_height_ext = Output(UInt(5.W))
//         val weight_width_ext = Output(UInt(5.W))
//         val weight_channel_ext = Output(UInt(13.W))
//         val weight_kernel = Output(UInt(13.W))
//         val wmb_bytes = Output(UInt(28.W))
//         val pad_left = Output(UInt(5.W))
//         val pad_top = Output(UInt(5.W))
//         val pad_value = Output(UInt(16.W))

//         //Read-only register inputs
//         val op_en = Input(Bool())    
//     })
    
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
//     val nvdla_csc_d_atomics_0_wren = (io.reg_offset === "h44".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_bank_0_wren = (io.reg_offset ===  "h5c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_batch_number_0_wren = (io.reg_offset ===  "h1c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_conv_stride_ext_0_wren = (io.reg_offset ===  "h4c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_cya_0_wren = (io.reg_offset === "h64".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_datain_format_0_wren = (io.reg_offset === "h10".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_datain_size_ext_0_0_wren = (io.reg_offset === "h14".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_datain_size_ext_1_0_wren = (io.reg_offset === "h18".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_dataout_size_0_0_wren = (io.reg_offset === "h3c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_dataout_size_1_0_wren = (io.reg_offset === "h40".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_dilation_ext_0_wren = (io.reg_offset === "h50".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_entry_per_slice_0_wren = (io.reg_offset === "h24".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_misc_cfg_0_wren = (io.reg_offset === "h0c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_op_enable_0_wren = (io.reg_offset === "h08".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_post_y_extension_0_wren = (io.reg_offset === "h20".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_pra_cfg_0_wren = (io.reg_offset === "h60".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_release_0_wren = (io.reg_offset === "h48".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_weight_bytes_0_wren = (io.reg_offset === "h34".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_weight_format_0_wren = (io.reg_offset === "h28".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_weight_size_ext_0_0_wren = (io.reg_offset === "h2c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_weight_size_ext_1_0_wren = (io.reg_offset === "h30".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_wmb_bytes_0_wren = (io.reg_offset === "h38".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_zero_padding_0_wren = (io.reg_offset === "h54".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
//     val nvdla_csc_d_zero_padding_value_0_wren = (io.reg_offset === "h58".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)

//     val nvdla_csc_d_datain_format_0_out = Cat("b0".asUInt(31.W), io.datain_format)
//     val nvdla_csc_d_datain_size_ext_0_0_out = Cat("b0".asUInt(3.W), io.datain_height_ext, "b0".asUInt(3.W), io.datain_width_ext )
//     val nvdla_csc_d_datain_size_ext_1_0_out = Cat("b0".asUInt(19.W), io.datain_channel_ext)
//     val nvdla_csc_d_dataout_size_0_0_out = Cat("b0".asUInt(3.W), io.dataout_height, "b0".asUInt(3.W), io.dataout_width )
//     val nvdla_csc_d_dataout_size_1_0_out = Cat("b0".asUInt(19.W), io.dataout_channel)
//     val nvdla_csc_d_dilation_ext_0_out = Cat("b0".asUInt(11.W), io.y_dilation_ext, "b0".asUInt(11.W), io.x_dilation_ext )
//     val nvdla_csc_d_entry_per_slice_0_out = Cat("b0".asUInt(18.W), io.entries)
//     val nvdla_csc_d_misc_cfg_0_out = Cat("b0".asUInt(3.W), io.skip_weight_rls, "b0".asUInt(3.W), io.skip_data_rls, 
//                                     "b0".asUInt(3.W), io.weight_reuse, "b0".asUInt(3.W), io.data_reuse, "b0".asUInt(2.W), 
//                                     io.proc_precision, "b0".asUInt(2.W), io.in_precision, "b0".asUInt(7.W), io.conv_mode)
//     val nvdla_csc_d_op_enable_0_out = Cat("b0".asUInt(31.W), io.op_en)
//     val nvdla_csc_d_post_y_extension_0_out = Cat( "b0".asUInt(30.W), io.y_extension)
//     val nvdla_csc_d_pra_cfg_0_out = Cat("b0".asUInt(30.W), io.pra_truncate)
//     val nvdla_csc_d_release_0_out = Cat("b0".asUInt(20.W), io.rls_slices)
//     val nvdla_csc_d_weight_bytes_0_out = io.weight_bytes
//     val nvdla_csc_d_weight_format_0_out = Cat("b0".asUInt(31.W), io.weight_format)
//     val nvdla_csc_d_weight_size_ext_0_0_out = Cat("b0".asUInt(11.W), io.weight_height_ext, "b0".asUInt(11.W), io.weight_width_ext )
//     val nvdla_csc_d_weight_size_ext_1_0_out = Cat("b0".asUInt(3.W), io.weight_kernel, "b0".asUInt(3.W), io.weight_channel_ext)
//     val nvdla_csc_d_wmb_bytes_0_out = Cat("b0".asUInt(4.W), io.wmb_bytes, "b0".asUInt(7.W))
//     val nvdla_csc_d_zero_padding_0_out = Cat("b0".asUInt(11.W), io.pad_top, "b0".asUInt(11.W), io.pad_left)
//     val nvdla_csc_d_zero_padding_value_0_out = Cat("b0".asUInt(16.W), io.pad_value)

//     io.op_en_trigger := nvdla_csc_d_op_enable_0_wren

//     //Output mux

//     io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
//     Seq(  
//     //nvdla_csc_d_atomics_0_out    
//     "h44".asUInt(32.W)  -> Cat("b0".asUInt(11.W), io.atomics),
//     //nvdla_csc_d_bank_0_out
//     "h5c".asUInt(32.W)  -> Cat("b0".asUInt(11.W), io.weight_bank, "b0".asUInt(11.W), io.data_bank),
//     //nvdla_csc_d_batch_number_0_out
//     "h1c".asUInt(32.W)  -> Cat("b0".asUInt(27.W), io.batches),
//     //nvdla_csc_d_conv_stride_ext_0_out
//     "h4c".asUInt(32.W)  -> Cat("b0".asUInt(13.W), io.conv_y_stride_ext, "b0".asUInt(13.W), io.conv_x_stride_ext),
//     //nvdla_csc_d_cya_0_out
//     "h64".asUInt(32.W)  -> io.cya,
//     "h10".asUInt(32.W)  -> nvdla_csc_d_datain_format_0_out,
//     "h14".asUInt(32.W)  -> nvdla_csc_d_datain_size_ext_0_0_out,
//     "h18".asUInt(32.W)  -> nvdla_csc_d_datain_size_ext_1_0_out,
//     "h3c".asUInt(32.W)  -> nvdla_csc_d_dataout_size_0_0_out,
//     "h40".asUInt(32.W)  -> nvdla_csc_d_dataout_size_1_0_out,
//     "h50".asUInt(32.W)  -> nvdla_csc_d_dilation_ext_0_out,
//     "h24".asUInt(32.W)  -> nvdla_csc_d_entry_per_slice_0_out,
//     "h0c".asUInt(32.W)  -> nvdla_csc_d_misc_cfg_0_out, 
//     "h08".asUInt(32.W)  -> nvdla_csc_d_op_enable_0_out, 
//     "h20".asUInt(32.W)  -> nvdla_csc_d_post_y_extension_0_out, 
//     "h60".asUInt(32.W)  -> nvdla_csc_d_pra_cfg_0_out, 
//     "h48".asUInt(32.W)  -> nvdla_csc_d_release_0_out, 
//     "h34".asUInt(32.W)  -> nvdla_csc_d_weight_bytes_0_out, 
//     "h28".asUInt(32.W)  -> nvdla_csc_d_weight_format_0_out, 
//     "h2c".asUInt(32.W)  -> nvdla_csc_d_weight_size_ext_0_0_out, 
//     "h30".asUInt(32.W)  -> nvdla_csc_d_weight_size_ext_1_0_out, 
//     "h38".asUInt(32.W)  -> nvdla_csc_d_wmb_bytes_0_out, 
//     "h54".asUInt(32.W)  -> nvdla_csc_d_zero_padding_0_out,
//     "h58".asUInt(32.W)  -> nvdla_csc_d_zero_padding_value_0_out                                                                                    
//     ))

//     //Register flop declarations


//     //yifengdu y.f.du1994@gmail.com update on Jul 31, 2019 
//     //Solve Java heap space problem
//     io.atomics := RegEnable(io.reg_wr_data(20,0), "b1".asUInt(21.W), nvdla_csc_d_atomics_0_wren)
//     //Register: NVDLA_CSC_D_BANK_0    Field  data_bank
//     io.data_bank := RegEnable(io.reg_wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_bank_0_wren)
//     //Register: NVDLA_CSC_D_BANK_0    Field: weight_bank    
//     io.weight_bank := RegEnable(io.reg_wr_data(20,16), "b0".asUInt(5.W), nvdla_csc_d_bank_0_wren)
//     //Register: NVDLA_CSC_D_BATCH_NUMBER_0    Field: batches
//     io.batches := RegEnable(io.reg_wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_batch_number_0_wren)
//     //Register: NVDLA_CSC_D_CONV_STRIDE_EXT_0    Field: conv_x_stride_ext
//     io.conv_x_stride_ext := RegEnable(io.reg_wr_data(2,0), "b0".asUInt(3.W), nvdla_csc_d_conv_stride_ext_0_wren)
//     //Register: NVDLA_CSC_D_CONV_STRIDE_EXT_0    Field: conv_y_stride_ext
//     io.conv_y_stride_ext := RegEnable(io.reg_wr_data(18,16), "b0".asUInt(3.W), nvdla_csc_d_conv_stride_ext_0_wren)
//     //Register: NVDLA_CSC_D_CYA_0    Field: cya
//     io.cya := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_csc_d_cya_0_wren)
//     //Register: NVDLA_CSC_D_DATAIN_FORMAT_0    Field: datain_format
//     io.datain_format := RegEnable(io.reg_wr_data(0), false.B, nvdla_csc_d_datain_format_0_wren) 
//     //Register: NVDLA_CSC_D_DATAIN_SIZE_EXT_0_0    Field: datain_height_ext
//     io.datain_height_ext := RegEnable(io.reg_wr_data(28,16), "b0".asUInt(13.W), nvdla_csc_d_datain_size_ext_0_0_wren)
//     //Register: NVDLA_CSC_D_DATAIN_SIZE_EXT_0_0    Field: datain_width_ext
//     io.datain_width_ext := RegEnable(io.reg_wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_datain_size_ext_0_0_wren)
//     //Register: NVDLA_CSC_D_DATAIN_SIZE_EXT_1_0    Field: datain_channel_ext
//     io.datain_channel_ext := RegEnable(io.reg_wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_datain_size_ext_1_0_wren)
//     //Register: NVDLA_CSC_D_DATAOUT_SIZE_0_0    Field: dataout_height
//     io.dataout_height := RegEnable(io.reg_wr_data(28,16), "b0".asUInt(13.W), nvdla_csc_d_dataout_size_0_0_wren)
//     //Register: NVDLA_CSC_D_DATAOUT_SIZE_0_0    Field: dataout_width
//     io.dataout_width := RegEnable(io.reg_wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_dataout_size_0_0_wren)
//     //Register: NVDLA_CSC_D_DATAOUT_SIZE_1_0    Field: dataout_channel
//     io.dataout_channel := RegEnable(io.reg_wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_dataout_size_1_0_wren)
//     //Register: NVDLA_CSC_D_DILATION_EXT_0    Field: x_dilation_ext
//     io.x_dilation_ext := RegEnable(io.reg_wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_dilation_ext_0_wren)
//     //Register: NVDLA_CSC_D_DILATION_EXT_0    Field: y_dilation_ext
//     io.y_dilation_ext := RegEnable(io.reg_wr_data(20,16), "b0".asUInt(5.W), nvdla_csc_d_dilation_ext_0_wren)
//     //Register: NVDLA_CSC_D_ENTRY_PER_SLICE_0    Field: entries
//     io.entries := RegEnable(io.reg_wr_data(13,0), "b0".asUInt(14.W), nvdla_csc_d_entry_per_slice_0_wren)
//     //Register: NVDLA_CSC_D_MISC_CFG_0    Field: conv_mode
//     io.conv_mode := RegEnable(io.reg_wr_data(0), false.B, nvdla_csc_d_misc_cfg_0_wren)
//     //Register: NVDLA_CSC_D_MISC_CFG_0    Field: data_reuse
//     io.data_reuse := RegEnable(io.reg_wr_data(16), false.B, nvdla_csc_d_misc_cfg_0_wren)
//     //Register: NVDLA_CSC_D_MISC_CFG_0    Field: in_precision
//     io.in_precision := RegEnable(io.reg_wr_data(9,8), "b1".asUInt(2.W), nvdla_csc_d_misc_cfg_0_wren)
//     //Register: NVDLA_CSC_D_MISC_CFG_0    Field: proc_precision
//     io.proc_precision := RegEnable(io.reg_wr_data(13, 12), "b1".asUInt(2.W), nvdla_csc_d_misc_cfg_0_wren)
//     //Register: NVDLA_CSC_D_MISC_CFG_0    Field: skip_data_rls
//     io.skip_data_rls := RegEnable(io.reg_wr_data(24), false.B, nvdla_csc_d_misc_cfg_0_wren)
//     //Register: NVDLA_CSC_D_MISC_CFG_0    Field: skip_weight_rls
//     io.skip_weight_rls := RegEnable(io.reg_wr_data(28), false.B, nvdla_csc_d_misc_cfg_0_wren)
//     //Register: NVDLA_CSC_D_MISC_CFG_0    Field: weight_reuse
//     io.weight_reuse := RegEnable(io.reg_wr_data(20), false.B, nvdla_csc_d_misc_cfg_0_wren)

//     //Register: NVDLA_CSC_D_POST_Y_EXTENSION_0    Field: y_extension
//     io.y_extension := RegEnable(io.reg_wr_data(1,0), "b0".asUInt(2.W), nvdla_csc_d_post_y_extension_0_wren)
//     //Register: NVDLA_CSC_D_PRA_CFG_0    Field: pra_truncate
//     io.pra_truncate := RegEnable(io.reg_wr_data(1,0), "b0".asUInt(2.W), nvdla_csc_d_pra_cfg_0_wren)
//     //Register: NVDLA_CSC_D_RELEASE_0    Field: rls_slices
//     io.rls_slices := RegEnable(io.reg_wr_data(11,0), "b1".asUInt(12.W), nvdla_csc_d_release_0_wren)
//     //Register: NVDLA_CSC_D_WEIGHT_BYTES_0    Field: weight_bytes
//     io.weight_bytes := RegEnable(io.reg_wr_data(31,0), "b0".asUInt(32.W), nvdla_csc_d_weight_bytes_0_wren)
//     //Register: NVDLA_CSC_D_WEIGHT_FORMAT_0    Field: weight_format
//     io.weight_format := RegEnable(io.reg_wr_data(0), false.B, nvdla_csc_d_weight_format_0_wren)
//     //Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_0_0    Field: weight_height_ext
//     io.weight_height_ext := RegEnable(io.reg_wr_data(20,16), "b0".asUInt(5.W), nvdla_csc_d_weight_size_ext_0_0_wren)
//     //Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_0_0    Field: weight_width_ext
//     io.weight_width_ext := RegEnable(io.reg_wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_weight_size_ext_0_0_wren)
//     //Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_1_0    Field: weight_channel_ext
//     io.weight_channel_ext := RegEnable(io.reg_wr_data(12,0), "b0".asUInt(13.W), nvdla_csc_d_weight_size_ext_1_0_wren)
//     //Register: NVDLA_CSC_D_WEIGHT_SIZE_EXT_1_0    Field: weight_kernel
//     io.weight_kernel := RegEnable(io.reg_wr_data(28,16), "b0".asUInt(13.W), nvdla_csc_d_weight_size_ext_1_0_wren)
//     //Register: NVDLA_CSC_D_WMB_BYTES_0    Field: wmb_bytes
//     io.wmb_bytes := RegEnable(io.reg_wr_data(27,0), "b0".asUInt(28.W), nvdla_csc_d_wmb_bytes_0_wren)
//     //Register: NVDLA_CSC_D_ZERO_PADDING_0    Field: pad_left
//     io.pad_left := RegEnable(io.reg_wr_data(4,0), "b0".asUInt(5.W), nvdla_csc_d_zero_padding_0_wren)
//     //Register: NVDLA_CSC_D_ZERO_PADDING_0    Field: pad_top
//     io.pad_top := RegEnable(io.reg_wr_data(20,16), "b0".asUInt(5.W), nvdla_csc_d_zero_padding_0_wren)
//     //Register: NVDLA_CSC_D_ZERO_PADDING_VALUE_0    Field: pad_value
//     io.pad_value := RegEnable(io.reg_wr_data(15,0), "b0".asUInt(16.W), nvdla_csc_d_zero_padding_value_0_wren)                                                                   

// }}