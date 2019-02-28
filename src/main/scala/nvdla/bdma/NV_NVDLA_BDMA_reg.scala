// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_BDMA_reg extends Module{
//     val io = IO(new Bundle{
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         //Register control interface
//         val reg_rd_data = Output(UInt(32.W))
//         val reg_offset = Input(UInt(12.W))
//         val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)
//         val reg_wr_en = Input(Bool())

//         //Writable register flop/trigger outputs
//         val nvdla_bdma_cfg_cmd_0_dst_ram_type = Output(Bool())
//         val nvdla_bdma_cfg_cmd_0_src_ram_type = Output(Bool())
//         val nvdla_bdma_cfg_dst_addr_high_0_v8 = Output(UInt(32.W))
//         val nvdla_bdma_cfg_dst_addr_low_0_v32 = Output(UInt(27.W))
//         val nvdla_bdma_cfg_dst_line_0_stride = Output(UInt(27.W))
//         val nvdla_bdma_cfg_dst_surf_0_stride = Output(UInt(27.W))
//         val nvdla_bdma_cfg_launch0_0_grp0_launch = Output(Bool())
//         val nvdla_bdma_cfg_launch0_0_grp0_launch_trigger = Output(Bool())
//         val nvdla_bdma_cfg_launch1_0_grp1_launch = Output(Bool())
//         val nvdla_bdma_cfg_launch1_0_grp1_launch_trigger = Output(Bool())
//         val nvdla_bdma_cfg_line_0_size = Output(UInt(13.W))
//         val nvdla_bdma_cfg_line_repeat_0_number = Output(UInt(23.W))
//         val nvdla_bdma_cfg_op_0_en = Output(Bool())
//         val nvdla_bdma_cfg_op_0_en_trigger = Output(Bool())
//         val nvdla_bdma_cfg_src_addr_high_0_v8 = Output(UInt(32.W))
//         val nvdla_bdma_cfg_src_addr_low_0_v32 = Output(UInt(27.W))
//         val nvdla_bdma_cfg_src_line_0_stride = Output(UInt(27.W))
//         val nvdla_bdma_cfg_src_surf_0_stride = Output(UInt(27.W))
//         val nvdla_bdma_cfg_status_0_stall_count_en = Output(Bool())
//         val nvdla_bdma_cfg_surf_repeat_0_number = Output(UInt(24.W))

//         //Read-only register inputs
//         val nvdla_bdma_status_0_free_slot = Input(UInt(8.W))
//         val nvdla_bdma_status_0_grp0_busy = Input(Bool())
//         val nvdla_bdma_status_0_grp1_busy = Input(Bool())
//         val nvdla_bdma_status_0_idle = Input(Bool())
//         val nvdla_bdma_status_grp0_read_stall_0_count = Input(UInt(32.W))
//         val nvdla_bdma_status_grp0_write_stall_0_count = Input(UInt(32.W))
//         val nvdla_bdma_status_grp1_read_stall_0_count = Input(UInt(32.W))
//         val nvdla_bdma_status_grp1_write_stall_0_count = Input(UInt(32.W))
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
//     val  nvdla_bdma_cfg_cmd_0_wren = (io.reg_offset === "h14".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_dst_addr_high_0_wren = (io.reg_offset === "h0c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_dst_addr_low_0_wren = (io.reg_offset === "h08".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_dst_line_0_wren = (io.reg_offset === "h20".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_dst_surf_0_wren = (io.reg_offset === "h2c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_launch0_0_wren = (io.reg_offset === "h34".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_launch1_0_wren = (io.reg_offset === "h38".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_line_0_wren = (io.reg_offset === "h10".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_line_repeat_0_wren = (io.reg_offset === "h18".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_op_0_wren = (io.reg_offset === "h30".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_src_addr_high_0_wren = (io.reg_offset === "h04".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_src_addr_low_0_wren = (io.reg_offset === "h00".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_src_line_0_wren = (io.reg_offset === "h1c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_src_surf_0_wren = (io.reg_offset === "h28".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_status_0_wren = (io.reg_offset === "h3c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_cfg_surf_repeat_0_wren = (io.reg_offset === "h24".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_status_0_wren = (io.reg_offset === "h40".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_status_grp0_read_stall_0_wren = (io.reg_offset === "h44".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_status_grp0_write_stall_0_wren = (io.reg_offset === "h48".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_status_grp1_read_stall_0_wren = (io.reg_offset === "h4c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
//     val  nvdla_bdma_status_grp1_write_stall_0_wren = (io.reg_offset === "h50".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)

//     val  nvdla_bdma_cfg_cmd_0_out :=  Mux( 30'b0, nvdla_bdma_cfg_cmd_0_dst_ram_type, nvdla_bdma_cfg_cmd_0_src_ram_type)
//     val  nvdla_bdma_cfg_dst_addr_high_0_out :=  Mux(nvdla_bdma_cfg_dst_addr_high_0_v8 )
//     val  nvdla_bdma_cfg_dst_addr_low_0_out :=  Mux(nvdla_bdma_cfg_dst_addr_low_0_v32, )
//     val  nvdla_bdma_cfg_dst_line_0_out :=  Mux(nvdla_bdma_cfg_dst_line_0_stride, 5'b0 )
//     val  nvdla_bdma_cfg_dst_surf_0_out :=  Mux(nvdla_bdma_cfg_dst_surf_0_stride, 5'b0 )
//     val  nvdla_bdma_cfg_launch0_0_out :=  Mux(31'b0, nvdla_bdma_cfg_launch0_0_grp0_launch )
//     val  nvdla_bdma_cfg_launch1_0_out :=  Mux(31'b0, nvdla_bdma_cfg_launch1_0_grp1_launch )
//     val  nvdla_bdma_cfg_line_0_out :=  Mux(19'b0, nvdla_bdma_cfg_line_0_size )
//     val  nvdla_bdma_cfg_line_repeat_0_out :=  Mux(8'b0, nvdla_bdma_cfg_line_repeat_0_number )
//     val  nvdla_bdma_cfg_op_0_out :=  Mux(31'b0, nvdla_bdma_cfg_op_0_en )
//     val  nvdla_bdma_cfg_src_addr_high_0_out :=  Mux(nvdla_bdma_cfg_src_addr_high_0_v8 )
//     val  nvdla_bdma_cfg_src_addr_low_0_out :=  Mux( nvdla_bdma_cfg_src_addr_low_0_v32, 5'b0 )
//     val  nvdla_bdma_cfg_src_line_0_out :=  Mux(nvdla_bdma_cfg_src_line_0_stride, 5'b0 )
//     val  nvdla_bdma_cfg_src_surf_0_out :=  Mux(nvdla_bdma_cfg_src_surf_0_stride, 5'b0 )
//     val  nvdla_bdma_cfg_status_0_out :=  Mux(31'b0, nvdla_bdma_cfg_status_0_stall_count_en )
//     val  nvdla_bdma_cfg_surf_repeat_0_out :=  Mux(8'b0, nvdla_bdma_cfg_surf_repeat_0_number )
//     val  nvdla_bdma_status_0_out :=  Mux(21'b0, nvdla_bdma_status_0_grp1_busy, nvdla_bdma_status_0_grp0_busy, nvdla_bdma_status_0_idle, nvdla_bdma_status_0_free_slot )
//     val  nvdla_bdma_status_grp0_read_stall_0_out :=  Mux(nvdla_bdma_status_grp0_read_stall_0_count )
//     val  nvdla_bdma_status_grp0_write_stall_0_out :=  Mux(nvdla_bdma_status_grp0_write_stall_0_count )
//     val  nvdla_bdma_status_grp1_read_stall_0_out :=  Mux(nvdla_bdma_status_grp1_read_stall_0_count )
//     val  nvdla_bdma_status_grp1_write_stall_0_out :=  Mux(nvdla_bdma_status_grp1_write_stall_0_count )

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

//     val batches_out = RegInit("b0".asUInt(5.W))
//     val clip_truncate_out = RegInit("b0".asUInt(5.W))
//     val cya_out = RegInit("b0".asUInt(32.W))
//     val dataout_addr_out = RegInit("b0".asUInt(32.W))
//     val line_packed_out = RegInit(false.B)
//     val surf_packed_out = RegInit(false.B)
//     val dataout_height_out = RegInit("b0".asUInt(13.W))
//     val dataout_width_out = RegInit("b0".asUInt(13.W))
//     val dataout_channel_out = RegInit("b0".asUInt(13.W))
//     val line_stride_out = RegInit("b0".asUInt(24.W))
//     val conv_mode_out = RegInit(false.B)
//     val proc_precision_out = RegInit("b01".asUInt(2.W))
//     val surf_stride_out = RegInit("b0".asUInt(24.W))

//     when(nvdla_cacc_d_batch_number_0_wren){
//         batches_out := io.reg_wr_data(4, 0)
//     }

//   // Register: NVDLA_CACC_D_CLIP_CFG_0    Field: clip_truncate
//     when(nvdla_cacc_d_clip_cfg_0_wren){
//         clip_truncate_out := io.reg_wr_data(4, 0)
//     }

//   // Register: NVDLA_CACC_D_CYA_0    Field: cya
//     when(nvdla_cacc_d_cya_0_wren){
//         cya_out := io.reg_wr_data(31, 0)
//     }

//   // Register: NVDLA_CACC_D_DATAOUT_ADDR_0    Field: dataout_addr
//     when(nvdla_cacc_d_dataout_addr_0_wren){
//         dataout_addr_out := io.reg_wr_data(31, 0)
//     }

//   // Register: NVDLA_CACC_D_DATAOUT_MAP_0    Field: line_packed
//     when(nvdla_cacc_d_dataout_map_0_wren){
//         line_packed_out := io.reg_wr_data(0)
//     }

//   // Register: NVDLA_CACC_D_DATAOUT_MAP_0    Field: surf_packed
//     when(nvdla_cacc_d_dataout_map_0_wren){
//         surf_packed_out := io.reg_wr_data(16)
//     }

//   // Register: NVDLA_CACC_D_DATAOUT_SIZE_0_0    Field: dataout_height
//     when(nvdla_cacc_d_dataout_size_0_0_wren){
//         dataout_height_out := io.reg_wr_data(28, 16)
//     }

//   // Register: NVDLA_CACC_D_DATAOUT_SIZE_0_0    Field: dataout_width
//     when(nvdla_cacc_d_dataout_size_0_0_wren){
//         dataout_width_out := io.reg_wr_data(12, 0)
//     }

//   // Register: NVDLA_CACC_D_DATAOUT_SIZE_1_0    Field: dataout_channel
//     when(nvdla_cacc_d_dataout_size_1_0_wren){
//         dataout_channel_out := io.reg_wr_data(12, 0)
//     }

//   // Register: NVDLA_CACC_D_LINE_STRIDE_0    Field: line_stride
//     when(nvdla_cacc_d_line_stride_0_wren){
//         line_stride_out := io.reg_wr_data(23, 0)
//     }

//   // Register: NVDLA_CACC_D_MISC_CFG_0    Field: conv_mode
//     when(nvdla_cacc_d_misc_cfg_0_wren){
//         conv_mode_out := io.reg_wr_data(0)
//     }

//   // Register: NVDLA_CACC_D_MISC_CFG_0    Field: proc_precision
//     when(nvdla_cacc_d_misc_cfg_0_wren){
//         proc_precision_out := io.reg_wr_data(13, 12)
//     }

//   // Not generating flops for field NVDLA_CACC_D_OP_ENABLE_0::op_en (to be implemented outside)

//   // Not generating flops for read-only field NVDLA_CACC_D_OUT_SATURATION_0::sat_count

//   // Register: NVDLA_CACC_D_SURF_STRIDE_0    Field: surf_stride
//     when(nvdla_cacc_d_surf_stride_0_wren){
//         surf_stride_out:= io.reg_wr_data(23, 0)
//     }

//     io.batches := batches_out
//     io.clip_truncate := clip_truncate_out
//     io.cya := cya_out
//     io.dataout_addr := dataout_addr_out
//     io.line_packed := line_packed_out
//     io.surf_packed := surf_packed_out
//     io.dataout_height := dataout_height_out
//     io.dataout_width := dataout_width_out
//     io.dataout_channel := dataout_channel_out
//     io.line_stride := line_stride_out 
//     io.conv_mode := conv_mode_out 
//     io.proc_precision := proc_precision_out
//     io.surf_stride := surf_stride_out                                                             

// }}