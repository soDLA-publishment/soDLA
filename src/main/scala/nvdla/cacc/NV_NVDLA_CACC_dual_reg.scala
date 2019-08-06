// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CACC_dual_reg extends Module{
//     val io = IO(new Bundle{
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         //Register control interface
//         val reg_rd_data = Output(UInt(32.W))
//         val reg_offset = Input(UInt(12.W))
//         val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)
//         val reg_wr_en = Input(Bool())

//         //Writable register flop/trigger outputs
//         val batches = Output(UInt(5.W))
//         val clip_truncate = Output(UInt(5.W))
//         val cya = Output(UInt(32.W))
//         val dataout_addr = Output(UInt(32.W))
//         val line_packed = Output(Bool())
//         val surf_packed = Output(Bool())
//         val dataout_height = Output(UInt(13.W))
//         val dataout_width = Output(UInt(13.W))
//         val dataout_channel = Output(UInt(13.W))
//         val line_stride = Output(UInt(24.W))
//         val conv_mode = Output(Bool())
//         val proc_precision = Output(UInt(2.W))
//         val op_en_trigger = Output(Bool())
//         val surf_stride = Output(UInt(24.W))

//         //Read-only register inputs
//         val op_en = Input(Bool())  
//         val sat_count = Input(UInt(32.W))  
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
//     val nvdla_cacc_d_batch_number_0_wren = (io.reg_offset === "h1c".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_clip_cfg_0_wren = (io.reg_offset === "h2c".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_cya_0_wren = (io.reg_offset === "h34".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_dataout_addr_0_wren = (io.reg_offset === "h18".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_dataout_map_0_wren = (io.reg_offset === "h28".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_dataout_size_0_0_wren = (io.reg_offset === "h10".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_dataout_size_1_0_wren = (io.reg_offset === "h14".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_line_stride_0_wren = (io.reg_offset === "h20".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_misc_cfg_0_wren = (io.reg_offset === "h0c".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_op_enable_0_wren = (io.reg_offset === "h08".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_out_saturation_0_wren = (io.reg_offset === "h30".asUInt(32.W)) & io.reg_wr_en ;    
//     val nvdla_cacc_d_surf_stride_0_wren = (io.reg_offset === "h24".asUInt(32.W)) & io.reg_wr_en ;    

//     val nvdla_cacc_d_batch_number_0_out = Cat("b0".asUInt(27.W), io.batches)
//     val nvdla_cacc_d_clip_cfg_0_out =  Cat("b0".asUInt(27.W), io.clip_truncate)
//     val nvdla_cacc_d_cya_0_out =  Cat(io.cya)
//     val nvdla_cacc_d_dataout_addr_0_out =  Cat(io.dataout_addr)
//     val nvdla_cacc_d_dataout_map_0_out =  Cat("b0".asUInt(15.W), io.surf_packed, "b0".asUInt(15.W), io.line_packed)
//     val nvdla_cacc_d_dataout_size_0_0_out =  Cat("b0".asUInt(3.W), io.dataout_height, "b0".asUInt(3.W), io.dataout_width)
//     val nvdla_cacc_d_dataout_size_1_0_out =  Cat("b0".asUInt(19.W), io.dataout_channel)
//     val nvdla_cacc_d_line_stride_0_out =  Cat("b0".asUInt(8.W), io.line_stride)
//     val nvdla_cacc_d_misc_cfg_0_out =  Cat("b0".asUInt(18.W), io.proc_precision, "b0".asUInt(11.W), io.conv_mode)
//     val nvdla_cacc_d_op_enable_0_out =  Cat("b0".asUInt(31.W), io.op_en)
//     val nvdla_cacc_d_out_saturation_0_out =  Cat( io.sat_count)
//     val nvdla_cacc_d_surf_stride_0_out =  Cat("b0".asUInt(8.W), io.surf_stride)

//     io.op_en_trigger := nvdla_cacc_d_op_enable_0_wren

//     //Output mux

//     io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
//     Seq(      
//     "h1c".asUInt(32.W)  -> nvdla_cacc_d_batch_number_0_out,
//     "h2c".asUInt(32.W)  -> nvdla_cacc_d_clip_cfg_0_out,
//     "h34".asUInt(32.W)  -> nvdla_cacc_d_cya_0_out,
//     "h18".asUInt(32.W)  -> nvdla_cacc_d_dataout_addr_0_out,
//     "h28".asUInt(32.W)  -> nvdla_cacc_d_dataout_map_0_out,
//     "h10".asUInt(32.W)  -> nvdla_cacc_d_dataout_size_0_0_out,
//     "h14".asUInt(32.W)  -> nvdla_cacc_d_dataout_size_1_0_out,
//     "h20".asUInt(32.W)  -> nvdla_cacc_d_line_stride_0_out,
//     "h0c".asUInt(32.W)  -> nvdla_cacc_d_misc_cfg_0_out,
//     "h08".asUInt(32.W)  -> nvdla_cacc_d_op_enable_0_out,
//     "h30".asUInt(32.W)  -> nvdla_cacc_d_out_saturation_0_out,
//     "h24".asUInt(32.W)  -> nvdla_cacc_d_surf_stride_0_out                                                                              
//     ))

//     //Register flop declarations
//     io.batches := RegEnable(io.reg_wr_data(4, 0), "b0".asUInt(5.W), nvdla_cacc_d_batch_number_0_wren)
//     // Register: NVDLA_CACC_D_CLIP_CFG_0    Field: clip_truncate
//     io.clip_truncate := RegEnable(io.reg_wr_data(4, 0), "b0".asUInt(5.W), nvdla_cacc_d_clip_cfg_0_wren)
//     // Register: NVDLA_CACC_D_CYA_0    Field: cya
//     io.cya := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cacc_d_cya_0_wren)
//     // Register: NVDLA_CACC_D_DATAOUT_ADDR_0    Field: dataout_addr
//     io.dataout_addr := RegEnable(io.reg_wr_data(31, 0), "b0".asUInt(32.W), nvdla_cacc_d_dataout_addr_0_wren)
//     // Register: NVDLA_CACC_D_DATAOUT_MAP_0    Field: line_packed
//     io.line_packed := RegEnable(io.reg_wr_data(0), false.B, nvdla_cacc_d_dataout_map_0_wren)
//     // Register: NVDLA_CACC_D_DATAOUT_MAP_0    Field: surf_packed
//     io.surf_packed := RegEnable(io.reg_wr_data(16), false.B, nvdla_cacc_d_dataout_map_0_wren)
//     // Register: NVDLA_CACC_D_DATAOUT_SIZE_0_0    Field: dataout_height
//     io.dataout_height := RegEnable(io.reg_wr_data(28, 16), "b0".asUInt(13.W), nvdla_cacc_d_dataout_size_0_0_wren)
//     // Register: NVDLA_CACC_D_DATAOUT_SIZE_0_0    Field: dataout_width
//     io.dataout_width := RegEnable(io.reg_wr_data(12, 0), "b0".asUInt(13.W), nvdla_cacc_d_dataout_size_0_0_wren)
//     // Register: NVDLA_CACC_D_DATAOUT_SIZE_1_0    Field: dataout_channel
//     io.dataout_channel := RegEnable(io.reg_wr_data(12, 0), "b0".asUInt(13.W), nvdla_cacc_d_dataout_size_1_0_wren)
//     // Register: NVDLA_CACC_D_LINE_STRIDE_0    Field: line_stride
//     io.line_stride := RegEnable(io.reg_wr_data(23, 0), "b0".asUInt(24.W), nvdla_cacc_d_line_stride_0_wren)
//     // Register: NVDLA_CACC_D_MISC_CFG_0    Field: conv_mode
//     io.conv_mode := RegEnable(io.reg_wr_data(0), false.B, nvdla_cacc_d_misc_cfg_0_wren)
//     // Register: NVDLA_CACC_D_MISC_CFG_0    Field: proc_precision
//     io.proc_precision := RegEnable(io.reg_wr_data(13, 12), "b01".asUInt(2.W), nvdla_cacc_d_misc_cfg_0_wren)
//     // Register: NVDLA_CACC_D_SURF_STRIDE_0    Field: surf_stride
//     io.surf_stride := RegEnable(io.reg_wr_data(23, 0), "b0".asUInt(24.W), nvdla_cacc_d_surf_stride_0_wren)                                                           

// }}