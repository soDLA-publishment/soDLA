// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver

// //Implementation overview of ping-pong register file.

// class NV_NVDLA_CDMA_regfile extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())      

//         //csb2cdma
//         val csb2cdma_req_pd = Input(UInt(63.W))
//         val csb2cdma_req_pvld = Input(Bool())
//         val csb2cdma_req_prdy = Output(Bool())
        
//         val cdma2csb_resp_pd = Output(UInt(34.W))
//         val cdma2csb_resp_valid = Output(Bool())

//         //reg2dp
//         val dp2reg_dat_flush_done = Input(Bool())
//         val dp2reg_dc_rd_latency = Input(UInt(32.W))
//         val dp2reg_dc_rd_stall = Input(UInt(32.W))
//         val dp2reg_done = Input(Bool())
//         val dp2reg_img_rd_latency = Input(UInt(32.W))
//         val dp2reg_img_rd_stall = Input(UInt(32.W))
//         val dp2reg_inf_data_num = Input(UInt(32.W))
//         val dp2reg_inf_weight_num = Input(UInt(32.W))
//         val dp2reg_nan_data_num = Input(UInt(32.W))
//         val dp2reg_nan_weight_num = Input(UInt(32.W))
//         val dp2reg_wg_rd_latency = Input(UInt(32.W))
//         val dp2reg_wg_rd_stall = Input(UInt(32.W))
//         val dp2reg_wt_flush_done = Input(Bool())
//         val dp2reg_wt_rd_latency = Input(UInt(32.W))
//         val dp2reg_wt_rd_stall = Input(UInt(32.W))
//         val dp2reg_consumer = Output(Bool())

//         val reg2dp_arb_weight = Output(UInt(4.W))
//         val reg2dp_arb_wmb = Output(UInt(4.W))
//         val reg2dp_batch_stride = Output(UInt(32.W))
//         val reg2dp_batches = Output(UInt(5.W))
//         val reg2dp_byte_per_kernel = Output(UInt(18.W))
//         val reg2dp_conv_mode = Output(Bool())
//         val reg2dp_conv_x_stride = Output(UInt(3.W))
//         val reg2dp_conv_y_stride = Output(UInt(3.W))
//         val reg2dp_cvt_en = Output(Bool())
//         val reg2dp_cvt_offset = Output(UInt(16.W))
//         val reg2dp_cvt_scale = Output(UInt(16.W))
//         val reg2dp_cvt_truncate = Output(UInt(6.W))
//         val reg2dp_cya = Output(UInt(32.W))
//         val reg2dp_data_bank = Output(UInt(5.W))
//         val reg2dp_data_reuse = Output(Bool())
//         val reg2dp_datain_addr_high_0 = Output(UInt(32.W))
//         val reg2dp_datain_addr_high_1 = Output(UInt(32.W))
//         val reg2dp_datain_addr_low_0 = Output(UInt(32.W))
//         val reg2dp_datain_addr_low_1 = Output(UInt(32.W))
//         val reg2dp_datain_channel = Output(UInt(13.W))
//         val reg2dp_datain_format = Output(Bool())
//         val reg2dp_datain_height = Output(UInt(13.W))
//         val reg2dp_datain_height_ext = Output(UInt(13.W))
//         val reg2dp_datain_ram_type = Output(Bool())
//         val reg2dp_datain_width = Output(UInt(13.W))
//         val reg2dp_datain_width_ext = Output(UInt(13.W))
//         val reg2dp_dma_en = Output(Bool())
//         val reg2dp_entries = Output(UInt(14.W))
//         val reg2dp_grains = Output(UInt(12.W))
//         val reg2dp_in_precision = Output(UInt(2.W))
//         val reg2dp_line_packed = Output(Bool())
//         val reg2dp_line_stride = Output(UInt(32.W))
//         val reg2dp_mean_ax = Output(UInt(16.W))
//         val reg2dp_mean_bv = Output(UInt(16.W))
//         val reg2dp_mean_format = Output(Bool())
//         val reg2dp_mean_gu = Output(UInt(16.W))
//         val reg2dp_mean_ry = Output(UInt(16.W))
//         val reg2dp_nan_to_zero = Output(Bool())
//         val reg2dp_op_en = Output(Bool())
//         val reg2dp_pad_bottom = Output(UInt(6.W))
//         val reg2dp_pad_left = Output(UInt(5.W))
//         val reg2dp_pad_right = Output(UInt(6.W))
//         val reg2dp_pad_top = Output(UInt(5.W))
//         val reg2dp_pad_value = Output(UInt(16.W))
//         val reg2dp_pixel_format = Output(UInt(6.W))
//         val reg2dp_pixel_mapping = Output(Bool())
//         val reg2dp_pixel_sign_override = Output(Bool())
//         val reg2dp_pixel_x_offset = Output(UInt(5.W))
//         val reg2dp_pixel_y_offset = Output(UInt(3.W))
//         val reg2dp_proc_precision = Output(UInt(2.W))
//         val reg2dp_rsv_height = Output(UInt(3.W))
//         val reg2dp_rsv_per_line = Output(UInt(10.W))
//         val reg2dp_rsv_per_uv_line = Output(UInt(10.W))
//         val reg2dp_rsv_y_index = Output(UInt(5.W))
//         val reg2dp_skip_data_rls = Output(Bool())
//         val reg2dp_skip_weight_rls = Output(Bool())
//         val reg2dp_surf_packed = Output(Bool())
//         val reg2dp_surf_stride = Output(UInt(32.W))
//         val reg2dp_uv_line_stride = Output(UInt(32.W))
//         val reg2dp_weight_addr_high = Output(UInt(32.W))
//         val reg2dp_weight_addr_low = Output(UInt(32.W))
//         val reg2dp_weight_bank = Output(UInt(5.W))
//         val reg2dp_weight_bytes = Output(UInt(32.W))
//         val reg2dp_weight_format = Output(Bool())
//         val reg2dp_weight_kernel = Output(UInt(13.W))
//         val reg2dp_weight_ram_type = Output(Bool())
//         val reg2dp_weight_reuse = Output(Bool())
//         val reg2dp_wgs_addr_high = Output(UInt(32.W))
//         val reg2dp_wgs_addr_low = Output(UInt(32.W))
//         val reg2dp_wmb_addr_high = Output(UInt(32.W))
//         val reg2dp_wmb_addr_low = Output(UInt(32.W))
//         val reg2dp_wmb_bytes = Output(UInt(28.W))
//         //slave cg op
//         val slcg_op_en = Output(UInt(8.W))
//     })
// //                             
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │              |-------------|
// //       │       ───       │              |     CSB     |
// //       │  ─┬┘       └┬─  │              |-------------|
// //       │                 │                    ||
// //       │       ─┴─       │                    reg   <= DP(data processor)
// //       │                 │                    ||
// //       └───┐         ┌───┘              |-------------|
// //           │         │                  |     CDMA    |
// //           │         │                  |-------------|
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 

// withClock(io.nvdla_core_clk){

//     //Instance single register group
//     val dp2reg_consumer_out = RegInit(false.B)
//     val reg.offset = Wire(UInt(12.W))
//     val reg.wr_data = Wire(UInt(32.W))
//     val s_reg.wr_en = Wire(Bool())
//     val dp2reg_status_0 = Wire(Bool())
//     val dp2reg_status_1 = Wire(Bool())
//     val dp2reg_flush_done = RegInit(false.B)

//     val u_single_reg = Module(new NV_NVDLA_CDMA_single_reg)

//     u_single_reg.io.reg.offset := reg.offset
//     u_single_reg.io.reg.wr_data := reg.wr_data 
//     u_single_reg.io.reg.wr_en := s_reg.wr_en
//     u_single_reg.io.nvdla_core_clk := io.nvdla_core_clk
//     u_single_reg.io.flush_done := dp2reg_flush_done
//     u_single_reg.io.consumer := dp2reg_consumer_out
//     u_single_reg.io.status_0 := dp2reg_status_0
//     u_single_reg.io.status_1 := dp2reg_status_1 
//     val s_reg.rd_data = u_single_reg.io.reg.rd_data
//     io.reg2dp_arb_weight := u_single_reg.io.arb_weight
//     io.reg2dp_arb_wmb := u_single_reg.io.arb_wmb 
//     val reg2dp_producer = u_single_reg.io.producer

//     //Instance two duplicated register groups
//     val d0_reg.wr_en = Wire(Bool())
//     val reg2dp_d0_op_en = RegInit(false.B)
//     val dp2reg_d0_inf_data_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d0_inf_weight_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d0_nan_data_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d0_nan_weight_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d0_dat_rd_latency = RegInit("b0".asUInt(32.W))
//     val dp2reg_d0_dat_rd_stall = RegInit("b0".asUInt(32.W))
//     val dp2reg_d0_wt_rd_latency = RegInit("b0".asUInt(32.W))
//     val dp2reg_d0_wt_rd_stall = RegInit("b0".asUInt(32.W))


//     val u_dual_reg_d0 = Module(new NV_NVDLA_CDMA_dual_reg)
//     u_dual_reg_d0.io.reg.offset := reg.offset
//     u_dual_reg_d0.io.reg.wr_data := reg.wr_data
//     u_dual_reg_d0.io.reg.wr_en := d0_reg.wr_en
//     u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk

//     u_dual_reg_d0.io.inf_data_num := dp2reg_d0_inf_data_num 
//     u_dual_reg_d0.io.inf_weight_num := dp2reg_d0_inf_weight_num
//     u_dual_reg_d0.io.nan_data_num := dp2reg_d0_nan_data_num
//     u_dual_reg_d0.io.nan_weight_num := dp2reg_d0_nan_weight_num
//     u_dual_reg_d0.io.op_en := reg2dp_d0_op_en
//     u_dual_reg_d0.io.dat_rd_latency := dp2reg_d0_dat_rd_latency
//     u_dual_reg_d0.io.dat_rd_stall := dp2reg_d0_dat_rd_stall
//     u_dual_reg_d0.io.wt_rd_latency := dp2reg_d0_wt_rd_latency 
//     u_dual_reg_d0.io.wt_rd_stall := dp2reg_d0_wt_rd_stall

//     val d0_reg.rd_data = u_dual_reg_d0.io.reg.rd_data
//     val reg2dp_d0_data_bank = u_dual_reg_d0.io.data_bank
//     val reg2dp_d0_weight_bank = u_dual_reg_d0.io.weight_bank
//     val reg2dp_d0_batches = u_dual_reg_d0.io.batches
//     val reg2dp_d0_batch_stride = u_dual_reg_d0.io.batch_stride
//     val reg2dp_d0_conv_x_stride = u_dual_reg_d0.io.conv_x_stride
//     val reg2dp_d0_conv_y_stride = u_dual_reg_d0.io.conv_y_stride
//     val reg2dp_d0_cvt_en = u_dual_reg_d0.io.cvt_en
//     val reg2dp_d0_cvt_truncate = u_dual_reg_d0.io.cvt_truncate
//     val reg2dp_d0_cvt_offset = u_dual_reg_d0.io.cvt_offset
//     val reg2dp_d0_cvt_scale = u_dual_reg_d0.io.cvt_scale
//     val reg2dp_d0_cya = u_dual_reg_d0.io.cya
//     val reg2dp_d0_datain_addr_high_0 = u_dual_reg_d0.io.datain_addr_high_0
//     val reg2dp_d0_datain_addr_high_1 = u_dual_reg_d0.io.datain_addr_high_1
//     val reg2dp_d0_datain_addr_low_0 = u_dual_reg_d0.io.datain_addr_low_0
//     val reg2dp_d0_datain_addr_low_1 = u_dual_reg_d0.io.datain_addr_low_1
//     val reg2dp_d0_line_packed = u_dual_reg_d0.io.line_packed
//     val reg2dp_d0_surf_packed = u_dual_reg_d0.io.surf_packed
//     val reg2dp_d0_datain_ram_type = u_dual_reg_d0.io.datain_ram_type
//     val reg2dp_d0_datain_format = u_dual_reg_d0.io.datain_format
//     val reg2dp_d0_pixel_format = u_dual_reg_d0.io.pixel_format
//     val reg2dp_d0_pixel_mapping = u_dual_reg_d0.io.pixel_mapping
//     val reg2dp_d0_pixel_sign_override = u_dual_reg_d0.io.pixel_sign_override
//     val reg2dp_d0_datain_height = u_dual_reg_d0.io.datain_height
//     val reg2dp_d0_datain_width = u_dual_reg_d0.io.datain_width
//     val reg2dp_d0_datain_channel = u_dual_reg_d0.io.datain_channel
//     val reg2dp_d0_datain_height_ext = u_dual_reg_d0.io.datain_height_ext
//     val reg2dp_d0_datain_width_ext = u_dual_reg_d0.io.datain_width_ext
//     val reg2dp_d0_entries = u_dual_reg_d0.io.entries
//     val reg2dp_d0_grains = u_dual_reg_d0.io.grains
//     val reg2dp_d0_line_stride = u_dual_reg_d0.io.line_stride
//     val reg2dp_d0_uv_line_stride = u_dual_reg_d0.io.uv_line_stride
//     val reg2dp_d0_mean_format = u_dual_reg_d0.io.mean_format
//     val reg2dp_d0_mean_gu = u_dual_reg_d0.io.mean_gu
//     val reg2dp_d0_mean_ry = u_dual_reg_d0.io.mean_ry
//     val reg2dp_d0_mean_ax = u_dual_reg_d0.io.mean_ax
//     val reg2dp_d0_mean_bv = u_dual_reg_d0.io.mean_bv
//     val reg2dp_d0_conv_mode = u_dual_reg_d0.io.conv_mode
//     val reg2dp_d0_data_reuse = u_dual_reg_d0.io.data_reuse
//     val reg2dp_d0_in_precision = u_dual_reg_d0.io.in_precision
//     val reg2dp_d0_proc_precision = u_dual_reg_d0.io.proc_precision
//     val reg2dp_d0_skip_data_rls = u_dual_reg_d0.io.skip_data_rls
//     val reg2dp_d0_skip_weight_rls = u_dual_reg_d0.io.skip_weight_rls
//     val reg2dp_d0_weight_reuse = u_dual_reg_d0.io.weight_reuse
//     val reg2dp_d0_nan_to_zero = u_dual_reg_d0.io.nan_to_zero
//     val reg2dp_d0_op_en_trigger = u_dual_reg_d0.io.op_en_trigger
//     val reg2dp_d0_dma_en = u_dual_reg_d0.io.dma_en
//     val reg2dp_d0_pixel_x_offset = u_dual_reg_d0.io.pixel_x_offset
//     val reg2dp_d0_pixel_y_offset = u_dual_reg_d0.io.pixel_y_offset
//     val reg2dp_d0_rsv_per_line = u_dual_reg_d0.io.rsv_per_line
//     val reg2dp_d0_rsv_per_uv_line = u_dual_reg_d0.io.rsv_per_uv_line
//     val reg2dp_d0_rsv_height = u_dual_reg_d0.io.rsv_height
//     val reg2dp_d0_rsv_y_index = u_dual_reg_d0.io.rsv_y_index
//     val reg2dp_d0_surf_stride = u_dual_reg_d0.io.surf_stride
//     val reg2dp_d0_weight_addr_high = u_dual_reg_d0.io.weight_addr_high
//     val reg2dp_d0_weight_addr_low = u_dual_reg_d0.io.weight_addr_low
//     val reg2dp_d0_weight_bytes = u_dual_reg_d0.io.weight_bytes
//     val reg2dp_d0_weight_format = u_dual_reg_d0.io.weight_format
//     val reg2dp_d0_weight_ram_type = u_dual_reg_d0.io.weight_ram_type
//     val reg2dp_d0_byte_per_kernel = u_dual_reg_d0.io.byte_per_kernel
//     val reg2dp_d0_weight_kernel = u_dual_reg_d0.io.weight_kernel
//     val reg2dp_d0_wgs_addr_high = u_dual_reg_d0.io.wgs_addr_high
//     val reg2dp_d0_wgs_addr_low = u_dual_reg_d0.io.wgs_addr_low
//     val reg2dp_d0_wmb_addr_high = u_dual_reg_d0.io.wmb_addr_high
//     val reg2dp_d0_wmb_addr_low = u_dual_reg_d0.io.wmb_addr_low
//     val reg2dp_d0_wmb_bytes = u_dual_reg_d0.io.wmb_bytes
//     val reg2dp_d0_pad_bottom = u_dual_reg_d0.io.pad_bottom
//     val reg2dp_d0_pad_left = u_dual_reg_d0.io.pad_left
//     val reg2dp_d0_pad_right = u_dual_reg_d0.io.pad_right
//     val reg2dp_d0_pad_top = u_dual_reg_d0.io.pad_top
//     val reg2dp_d0_pad_value = u_dual_reg_d0.io.pad_value
 

//     val d1_reg.wr_en = Wire(Bool())
//     val reg2dp_d1_op_en = RegInit(false.B)
//     val dp2reg_d1_inf_data_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d1_inf_weight_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d1_nan_data_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d1_nan_weight_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d1_dat_rd_latency = RegInit("b0".asUInt(32.W))
//     val dp2reg_d1_dat_rd_stall = RegInit("b0".asUInt(32.W))
//     val dp2reg_d1_wt_rd_latency = RegInit("b0".asUInt(32.W))
//     val dp2reg_d1_wt_rd_stall = RegInit("b0".asUInt(32.W))


//     val u_dual_reg_d1 = Module(new NV_NVDLA_CDMA_dual_reg)
//     u_dual_reg_d1.io.reg.offset := reg.offset
//     u_dual_reg_d1.io.reg.wr_data := reg.wr_data
//     u_dual_reg_d1.io.reg.wr_en := d1_reg.wr_en
//     u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk

//     u_dual_reg_d1.io.inf_data_num := dp2reg_d1_inf_data_num 
//     u_dual_reg_d1.io.inf_weight_num := dp2reg_d1_inf_weight_num
//     u_dual_reg_d1.io.nan_data_num := dp2reg_d1_nan_data_num
//     u_dual_reg_d1.io.nan_weight_num := dp2reg_d1_nan_weight_num
//     u_dual_reg_d1.io.op_en := reg2dp_d1_op_en
//     u_dual_reg_d1.io.dat_rd_latency := dp2reg_d1_dat_rd_latency
//     u_dual_reg_d1.io.dat_rd_stall := dp2reg_d1_dat_rd_stall
//     u_dual_reg_d1.io.wt_rd_latency := dp2reg_d1_wt_rd_latency 
//     u_dual_reg_d1.io.wt_rd_stall := dp2reg_d1_wt_rd_stall

//     val d1_reg.rd_data = u_dual_reg_d1.io.reg.rd_data
//     val reg2dp_d1_data_bank = u_dual_reg_d1.io.data_bank
//     val reg2dp_d1_weight_bank = u_dual_reg_d1.io.weight_bank
//     val reg2dp_d1_batches = u_dual_reg_d1.io.batches
//     val reg2dp_d1_batch_stride = u_dual_reg_d1.io.batch_stride
//     val reg2dp_d1_conv_x_stride = u_dual_reg_d1.io.conv_x_stride
//     val reg2dp_d1_conv_y_stride = u_dual_reg_d1.io.conv_y_stride
//     val reg2dp_d1_cvt_en = u_dual_reg_d1.io.cvt_en
//     val reg2dp_d1_cvt_truncate = u_dual_reg_d1.io.cvt_truncate
//     val reg2dp_d1_cvt_offset = u_dual_reg_d1.io.cvt_offset
//     val reg2dp_d1_cvt_scale = u_dual_reg_d1.io.cvt_scale
//     val reg2dp_d1_cya = u_dual_reg_d1.io.cya
//     val reg2dp_d1_datain_addr_high_0 = u_dual_reg_d1.io.datain_addr_high_0
//     val reg2dp_d1_datain_addr_high_1 = u_dual_reg_d1.io.datain_addr_high_1
//     val reg2dp_d1_datain_addr_low_0 = u_dual_reg_d1.io.datain_addr_low_0
//     val reg2dp_d1_datain_addr_low_1 = u_dual_reg_d1.io.datain_addr_low_1
//     val reg2dp_d1_line_packed = u_dual_reg_d1.io.line_packed
//     val reg2dp_d1_surf_packed = u_dual_reg_d1.io.surf_packed
//     val reg2dp_d1_datain_ram_type = u_dual_reg_d1.io.datain_ram_type
//     val reg2dp_d1_datain_format = u_dual_reg_d1.io.datain_format
//     val reg2dp_d1_pixel_format = u_dual_reg_d1.io.pixel_format
//     val reg2dp_d1_pixel_mapping = u_dual_reg_d1.io.pixel_mapping
//     val reg2dp_d1_pixel_sign_override = u_dual_reg_d1.io.pixel_sign_override
//     val reg2dp_d1_datain_height = u_dual_reg_d1.io.datain_height
//     val reg2dp_d1_datain_width = u_dual_reg_d1.io.datain_width
//     val reg2dp_d1_datain_channel = u_dual_reg_d1.io.datain_channel
//     val reg2dp_d1_datain_height_ext = u_dual_reg_d1.io.datain_height_ext
//     val reg2dp_d1_datain_width_ext = u_dual_reg_d1.io.datain_width_ext
//     val reg2dp_d1_entries = u_dual_reg_d1.io.entries
//     val reg2dp_d1_grains = u_dual_reg_d1.io.grains
//     val reg2dp_d1_line_stride = u_dual_reg_d1.io.line_stride
//     val reg2dp_d1_uv_line_stride = u_dual_reg_d1.io.uv_line_stride
//     val reg2dp_d1_mean_format = u_dual_reg_d1.io.mean_format
//     val reg2dp_d1_mean_gu = u_dual_reg_d1.io.mean_gu
//     val reg2dp_d1_mean_ry = u_dual_reg_d1.io.mean_ry
//     val reg2dp_d1_mean_ax = u_dual_reg_d1.io.mean_ax
//     val reg2dp_d1_mean_bv = u_dual_reg_d1.io.mean_bv
//     val reg2dp_d1_conv_mode = u_dual_reg_d1.io.conv_mode
//     val reg2dp_d1_data_reuse = u_dual_reg_d1.io.data_reuse
//     val reg2dp_d1_in_precision = u_dual_reg_d1.io.in_precision
//     val reg2dp_d1_proc_precision = u_dual_reg_d1.io.proc_precision
//     val reg2dp_d1_skip_data_rls = u_dual_reg_d1.io.skip_data_rls
//     val reg2dp_d1_skip_weight_rls = u_dual_reg_d1.io.skip_weight_rls
//     val reg2dp_d1_weight_reuse = u_dual_reg_d1.io.weight_reuse
//     val reg2dp_d1_nan_to_zero = u_dual_reg_d1.io.nan_to_zero
//     val reg2dp_d1_op_en_trigger = u_dual_reg_d1.io.op_en_trigger
//     val reg2dp_d1_dma_en = u_dual_reg_d1.io.dma_en
//     val reg2dp_d1_pixel_x_offset = u_dual_reg_d1.io.pixel_x_offset
//     val reg2dp_d1_pixel_y_offset = u_dual_reg_d1.io.pixel_y_offset
//     val reg2dp_d1_rsv_per_line = u_dual_reg_d1.io.rsv_per_line
//     val reg2dp_d1_rsv_per_uv_line = u_dual_reg_d1.io.rsv_per_uv_line
//     val reg2dp_d1_rsv_height = u_dual_reg_d1.io.rsv_height
//     val reg2dp_d1_rsv_y_index = u_dual_reg_d1.io.rsv_y_index
//     val reg2dp_d1_surf_stride = u_dual_reg_d1.io.surf_stride
//     val reg2dp_d1_weight_addr_high = u_dual_reg_d1.io.weight_addr_high
//     val reg2dp_d1_weight_addr_low = u_dual_reg_d1.io.weight_addr_low
//     val reg2dp_d1_weight_bytes = u_dual_reg_d1.io.weight_bytes
//     val reg2dp_d1_weight_format = u_dual_reg_d1.io.weight_format
//     val reg2dp_d1_weight_ram_type = u_dual_reg_d1.io.weight_ram_type
//     val reg2dp_d1_byte_per_kernel = u_dual_reg_d1.io.byte_per_kernel
//     val reg2dp_d1_weight_kernel = u_dual_reg_d1.io.weight_kernel
//     val reg2dp_d1_wgs_addr_high = u_dual_reg_d1.io.wgs_addr_high
//     val reg2dp_d1_wgs_addr_low = u_dual_reg_d1.io.wgs_addr_low
//     val reg2dp_d1_wmb_addr_high = u_dual_reg_d1.io.wmb_addr_high
//     val reg2dp_d1_wmb_addr_low = u_dual_reg_d1.io.wmb_addr_low
//     val reg2dp_d1_wmb_bytes = u_dual_reg_d1.io.wmb_bytes
//     val reg2dp_d1_pad_bottom = u_dual_reg_d1.io.pad_bottom
//     val reg2dp_d1_pad_left = u_dual_reg_d1.io.pad_left
//     val reg2dp_d1_pad_right = u_dual_reg_d1.io.pad_right
//     val reg2dp_d1_pad_top = u_dual_reg_d1.io.pad_top
//     val reg2dp_d1_pad_value = u_dual_reg_d1.io.pad_value

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE CONSUMER PIONTER IN GENERAL SINGLE REGISTER GROUP         //
//     //                                                                    //
//     //////////////////////////////////////////////////////////////////////// 
//     val dp2reg_consumer_out_w = ~dp2reg_consumer_out

//     when(io.dp2reg_done){
//         dp2reg_consumer_out := dp2reg_consumer_out_w
//     }

//     io.dp2reg_consumer := dp2reg_consumer_out

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE TWO STATUS FIELDS IN GENERAL SINGLE REGISTER GROUP        //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     dp2reg_status_0 := Mux(reg2dp_d0_op_en === false.B, "h0".asUInt(2.W), 
//                        Mux(dp2reg_consumer_out === true.B, "h2".asUInt(2.W), 
//                        "h1".asUInt(2.W)))

//     dp2reg_status_1 := Mux(reg2dp_d1_op_en === false.B, "h0".asUInt(2.W), 
//                        Mux(dp2reg_consumer_out === false.B, "h2".asUInt(2.W), 
//                        "h1".asUInt(2.W)))

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE OP_EN LOGIC                                               //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     val reg2dp_op_en_reg = RegInit("b0".asUInt(3.W))
//     val reg2dp_d0_op_en_w = Mux(~reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg.wr_data(0), 
//                             Mux(io.dp2reg_done && dp2reg_consumer_out === false.B, false.B, reg2dp_d0_op_en))

//     reg2dp_d0_op_en := reg2dp_d0_op_en_w

//     val reg2dp_d1_op_en_w =  Mux(~reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg.wr_data(0), 
//                              Mux(io.dp2reg_done && dp2reg_consumer_out === true.B, false.B, reg2dp_d1_op_en))

//     reg2dp_d1_op_en := reg2dp_d1_op_en_w

//     val reg2dp_op_en_ori = Mux(dp2reg_consumer_out, reg2dp_d1_op_en, reg2dp_d0_op_en)
//     val reg2dp_op_en_reg_w = Mux(io.dp2reg_done,  "b0".asUInt(3.W), Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori))

//     reg2dp_op_en_reg := reg2dp_op_en_reg_w 
//     io.reg2dp_op_en := reg2dp_op_en_reg(2)

//     io.slcg_op_en := ShiftRegister(Fill(8, reg2dp_op_en_ori), 3)
//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE ACCESS LOGIC TO EACH REGISTER GROUP                       //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     //EACH subunit has 4KB address space 
//     val reg.wr_en = Wire(Bool())
//     val select_s = Mux(reg.offset(11,0) < "h0010".asUInt(32.W), true.B, false.B)
//     val select_d0 = (reg.offset(11,0) >= "h0010".asUInt(32.W)) & (reg2dp_producer === false.B)
//     val select_d1 = (reg.offset(11,0) >= "h0010".asUInt(32.W)) & (reg2dp_producer === true.B)

//     s_reg.wr_en := reg.wr_en & select_s
//     d0_reg.wr_en := reg.wr_en & select_d0 & !reg2dp_d0_op_en
//     d1_reg.wr_en := reg.wr_en & select_d1 & !reg2dp_d1_op_en

//     val reg.rd_data = (Fill(32, select_s) & s_reg.rd_data)|
//                         (Fill(32, select_d0) & d0_reg.rd_data)|
//                         (Fill(32, select_d1)& d1_reg.rd_data)

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     val req_pvld = RegInit(false.B)
//     val req_pd = RegInit("b0".asUInt(63.W))

//     req_pvld := io.csb2cdma_req_pvld
//     when(io.csb2cdma_req_pvld){
//         req_pd := io.csb2cdma_req_pd
//     }

//     // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  req_pd ) 
//     val req_addr = req_pd(21, 0)
//     val req_wdat = req_pd(53, 22)
//     val req_write = req_pd(54)
//     val req_nposted = req_pd(55)
//     val req_srcpriv = req_pd(56)
//     val req_wrbe = req_pd(60, 57)
//     val req_level = req_pd(62, 61)

//     io.csb2cdma_req_prdy := true.B

//     //Address in CSB master is word aligned while address in regfile is byte aligned.
//     reg.offset := Cat(req_addr, "b0".asUInt(2.W))
//     reg.wr_data := req_wdat
//     reg.wr_en := req_pvld & req_write
//     val reg_rd_en = req_pvld & ~req_write

//     // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_rd_erpt ,  csb_rresp_ ,  csb_rresp_pd_w )
//     val csb_rresp_rdat = reg.rd_data
//     val csb_rresp_error = false.B
//     val csb_rresp_pd_w = Cat(false.B, csb_rresp_error, csb_rresp_rdat)

//     // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_wr_erpt ,  csb_wresp_ ,  csb_wresp_pd_w 
//     val csb_wresp_rdat = "b0".asUInt(32.W)
//     val csb_wresp_error = false.B
//     val csb_wresp_pd_w = Cat(true.B, csb_wresp_error, csb_wresp_rdat)

//     val cdma2csb_resp_pd_out = RegInit("b0".asUInt(34.W))
//     val cdma2csb_resp_valid_out = RegInit(false.B)

//     when(reg_rd_en){
//         cdma2csb_resp_pd_out := csb_rresp_pd_w
//     }
//     .elsewhen(reg.wr_en & req_nposted){
//         cdma2csb_resp_pd_out := csb_wresp_pd_w
//     }
//     cdma2csb_resp_valid_out := (reg.wr_en & req_nposted) | reg_rd_en

//     io.cdma2csb_resp_pd := cdma2csb_resp_pd_out
//     io.cdma2csb_resp_valid := cdma2csb_resp_valid_out 

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////


//     io.reg2dp_data_bank := Mux(dp2reg_consumer_out , reg2dp_d1_data_bank , reg2dp_d0_data_bank)

//     io.reg2dp_weight_bank := Mux(dp2reg_consumer_out , reg2dp_d1_weight_bank , reg2dp_d0_weight_bank)

//     io.reg2dp_batches := Mux(dp2reg_consumer_out , reg2dp_d1_batches , reg2dp_d0_batches)

//     io.reg2dp_batch_stride := Mux(dp2reg_consumer_out,  reg2dp_d1_batch_stride , reg2dp_d0_batch_stride)

//     io.reg2dp_conv_x_stride := Mux(dp2reg_consumer_out,  reg2dp_d1_conv_x_stride , reg2dp_d0_conv_x_stride)

//     io.reg2dp_conv_y_stride := Mux(dp2reg_consumer_out,  reg2dp_d1_conv_y_stride , reg2dp_d0_conv_y_stride)

//     io.reg2dp_cvt_en := Mux(dp2reg_consumer_out,  reg2dp_d1_cvt_en , reg2dp_d0_cvt_en)

//     io.reg2dp_cvt_truncate := Mux(dp2reg_consumer_out,  reg2dp_d1_cvt_truncate , reg2dp_d0_cvt_truncate)

//     io.reg2dp_cvt_offset := Mux(dp2reg_consumer_out,  reg2dp_d1_cvt_offset , reg2dp_d0_cvt_offset)

//     io.reg2dp_cvt_scale := Mux(dp2reg_consumer_out,  reg2dp_d1_cvt_scale , reg2dp_d0_cvt_scale)

//     io.reg2dp_cya := Mux(dp2reg_consumer_out,  reg2dp_d1_cya , reg2dp_d0_cya)

//     io.reg2dp_datain_addr_high_0 := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_addr_high_0 , reg2dp_d0_datain_addr_high_0)

//     io.reg2dp_datain_addr_high_1 := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_addr_high_1 , reg2dp_d0_datain_addr_high_1)

//     io.reg2dp_datain_addr_low_0 := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_addr_low_0 , reg2dp_d0_datain_addr_low_0)

//     io.reg2dp_datain_addr_low_1 := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_addr_low_1 , reg2dp_d0_datain_addr_low_1)

//     io.reg2dp_line_packed := Mux(dp2reg_consumer_out,  reg2dp_d1_line_packed , reg2dp_d0_line_packed)

//     io.reg2dp_surf_packed := Mux(dp2reg_consumer_out,  reg2dp_d1_surf_packed , reg2dp_d0_surf_packed)

//     io.reg2dp_datain_ram_type := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_ram_type , reg2dp_d0_datain_ram_type)

//     io.reg2dp_datain_format := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_format , reg2dp_d0_datain_format)

//     io.reg2dp_pixel_format := Mux(dp2reg_consumer_out,  reg2dp_d1_pixel_format , reg2dp_d0_pixel_format)

//     io.reg2dp_pixel_mapping := Mux(dp2reg_consumer_out,  reg2dp_d1_pixel_mapping , reg2dp_d0_pixel_mapping)

//     io.reg2dp_pixel_sign_override := Mux(dp2reg_consumer_out,  reg2dp_d1_pixel_sign_override , reg2dp_d0_pixel_sign_override)

//     io.reg2dp_datain_height := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_height , reg2dp_d0_datain_height)

//     io.reg2dp_datain_width := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_width , reg2dp_d0_datain_width)

//     io.reg2dp_datain_channel := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_channel , reg2dp_d0_datain_channel)

//     io.reg2dp_datain_height_ext := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_height_ext , reg2dp_d0_datain_height_ext)

//     io.reg2dp_datain_width_ext := Mux(dp2reg_consumer_out,  reg2dp_d1_datain_width_ext , reg2dp_d0_datain_width_ext)

//     io.reg2dp_entries := Mux(dp2reg_consumer_out,  reg2dp_d1_entries , reg2dp_d0_entries)

//     io.reg2dp_grains := Mux(dp2reg_consumer_out,  reg2dp_d1_grains , reg2dp_d0_grains)

//     io.reg2dp_line_stride := Mux(dp2reg_consumer_out,  reg2dp_d1_line_stride , reg2dp_d0_line_stride)

//     io.reg2dp_uv_line_stride := Mux(dp2reg_consumer_out,  reg2dp_d1_uv_line_stride , reg2dp_d0_uv_line_stride)

//     io.reg2dp_mean_format := Mux(dp2reg_consumer_out,  reg2dp_d1_mean_format , reg2dp_d0_mean_format)

//     io.reg2dp_mean_gu := Mux(dp2reg_consumer_out,  reg2dp_d1_mean_gu , reg2dp_d0_mean_gu)

//     io.reg2dp_mean_ry := Mux(dp2reg_consumer_out,  reg2dp_d1_mean_ry , reg2dp_d0_mean_ry)

//     io.reg2dp_mean_ax := Mux(dp2reg_consumer_out,  reg2dp_d1_mean_ax , reg2dp_d0_mean_ax)

//     io.reg2dp_mean_bv := Mux(dp2reg_consumer_out,  reg2dp_d1_mean_bv , reg2dp_d0_mean_bv)

//     io.reg2dp_conv_mode := Mux(dp2reg_consumer_out,  reg2dp_d1_conv_mode , reg2dp_d0_conv_mode)

//     io.reg2dp_data_reuse := Mux(dp2reg_consumer_out,  reg2dp_d1_data_reuse , reg2dp_d0_data_reuse)

//     io.reg2dp_in_precision := Mux(dp2reg_consumer_out,  reg2dp_d1_in_precision , reg2dp_d0_in_precision)

//     io.reg2dp_proc_precision := Mux(dp2reg_consumer_out,  reg2dp_d1_proc_precision , reg2dp_d0_proc_precision)

//     io.reg2dp_skip_data_rls := Mux(dp2reg_consumer_out,  reg2dp_d1_skip_data_rls , reg2dp_d0_skip_data_rls)

//     io.reg2dp_skip_weight_rls := Mux(dp2reg_consumer_out,  reg2dp_d1_skip_weight_rls , reg2dp_d0_skip_weight_rls)

//     io.reg2dp_weight_reuse := Mux(dp2reg_consumer_out,  reg2dp_d1_weight_reuse , reg2dp_d0_weight_reuse)

//     io.reg2dp_nan_to_zero := Mux(dp2reg_consumer_out,  reg2dp_d1_nan_to_zero , reg2dp_d0_nan_to_zero)

//     io.reg2dp_dma_en := Mux(dp2reg_consumer_out,  reg2dp_d1_dma_en , reg2dp_d0_dma_en)

//     io.reg2dp_pixel_x_offset := Mux(dp2reg_consumer_out,  reg2dp_d1_pixel_x_offset , reg2dp_d0_pixel_x_offset)

//     io.reg2dp_pixel_y_offset := Mux(dp2reg_consumer_out,  reg2dp_d1_pixel_y_offset , reg2dp_d0_pixel_y_offset)

//     io.reg2dp_rsv_per_line := Mux(dp2reg_consumer_out,  reg2dp_d1_rsv_per_line , reg2dp_d0_rsv_per_line)

//     io.reg2dp_rsv_per_uv_line := Mux(dp2reg_consumer_out,  reg2dp_d1_rsv_per_uv_line , reg2dp_d0_rsv_per_uv_line)

//     io.reg2dp_rsv_height := Mux(dp2reg_consumer_out,  reg2dp_d1_rsv_height , reg2dp_d0_rsv_height)

//     io.reg2dp_rsv_y_index := Mux(dp2reg_consumer_out,  reg2dp_d1_rsv_y_index , reg2dp_d0_rsv_y_index)

//     io.reg2dp_surf_stride := Mux(dp2reg_consumer_out,  reg2dp_d1_surf_stride , reg2dp_d0_surf_stride)

//     io.reg2dp_weight_addr_high := Mux(dp2reg_consumer_out,  reg2dp_d1_weight_addr_high , reg2dp_d0_weight_addr_high)

//     io.reg2dp_weight_addr_low := Mux(dp2reg_consumer_out,  reg2dp_d1_weight_addr_low , reg2dp_d0_weight_addr_low)

//     io.reg2dp_weight_bytes := Mux(dp2reg_consumer_out,  reg2dp_d1_weight_bytes , reg2dp_d0_weight_bytes)

//     io.reg2dp_weight_format := Mux(dp2reg_consumer_out,  reg2dp_d1_weight_format , reg2dp_d0_weight_format)

//     io.reg2dp_weight_ram_type := Mux(dp2reg_consumer_out,  reg2dp_d1_weight_ram_type , reg2dp_d0_weight_ram_type)

//     io.reg2dp_byte_per_kernel := Mux(dp2reg_consumer_out,  reg2dp_d1_byte_per_kernel , reg2dp_d0_byte_per_kernel)

//     io.reg2dp_weight_kernel := Mux(dp2reg_consumer_out,  reg2dp_d1_weight_kernel , reg2dp_d0_weight_kernel)

//     io.reg2dp_wgs_addr_high := Mux(dp2reg_consumer_out,  reg2dp_d1_wgs_addr_high , reg2dp_d0_wgs_addr_high)

//     io.reg2dp_wgs_addr_low := Mux(dp2reg_consumer_out,  reg2dp_d1_wgs_addr_low , reg2dp_d0_wgs_addr_low)

//     io.reg2dp_wmb_addr_high := Mux(dp2reg_consumer_out,  reg2dp_d1_wmb_addr_high , reg2dp_d0_wmb_addr_high)

//     io.reg2dp_wmb_addr_low := Mux(dp2reg_consumer_out,  reg2dp_d1_wmb_addr_low , reg2dp_d0_wmb_addr_low)

//     io.reg2dp_wmb_bytes := Mux(dp2reg_consumer_out,  reg2dp_d1_wmb_bytes , reg2dp_d0_wmb_bytes)

//     io.reg2dp_pad_bottom := Mux(dp2reg_consumer_out,  reg2dp_d1_pad_bottom , reg2dp_d0_pad_bottom)

//     io.reg2dp_pad_left := Mux(dp2reg_consumer_out,  reg2dp_d1_pad_left , reg2dp_d0_pad_left)

//     io.reg2dp_pad_right := Mux(dp2reg_consumer_out,  reg2dp_d1_pad_right , reg2dp_d0_pad_right)

//     io.reg2dp_pad_top := Mux(dp2reg_consumer_out,  reg2dp_d1_pad_top , reg2dp_d0_pad_top)

//     io.reg2dp_pad_value := Mux(dp2reg_consumer_out,  reg2dp_d1_pad_value , reg2dp_d0_pad_value)

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // PASTE ADDIFITON LOGIC HERE FROM EXTRA FILE                         //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     ////////////////////////////////////////////////////////////////////////
//     //  for interrupt                                                     //
//     ////////////////////////////////////////////////////////////////////////

//     ////////////////////////////////////////////////////////////////////////
//     //  for cbuf flushing logic                                           //
//     ////////////////////////////////////////////////////////////////////////
//     dp2reg_flush_done := io.dp2reg_wt_flush_done & io.dp2reg_dat_flush_done

//     ////////////////////////////////////////////////////////////////////////
//     //  for general counting register                                     //
//     ////////////////////////////////////////////////////////////////////////
//     val dp2reg_d0_set = reg2dp_d0_op_en & ~reg2dp_d0_op_en_w
//     val dp2reg_d0_clr = ~reg2dp_d0_op_en & reg2dp_d0_op_en_w
//     val dp2reg_d0_reg = reg2dp_d0_op_en ^ reg2dp_d0_op_en_w

//     val dp2reg_d1_set = reg2dp_d1_op_en & ~reg2dp_d1_op_en_w
//     val dp2reg_d1_clr = ~reg2dp_d1_op_en & reg2dp_d1_op_en_w
//     val dp2reg_d1_reg = reg2dp_d1_op_en ^ reg2dp_d1_op_en_w

//     ////////////////////////////////////////////////////////////////////////
//     //  for NaN and infinity counting registers                                   //
//     ////////////////////////////////////////////////////////////////////////
//     //////// group 0 ////////
//     val dp2reg_d0_nan_weight_num_w = Mux(dp2reg_d0_set, io.dp2reg_nan_weight_num,
//                                      Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                      dp2reg_d0_nan_weight_num))
//     val dp2reg_d0_inf_weight_num_w = Mux(dp2reg_d0_set, io.dp2reg_inf_weight_num,
//                                      Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                      dp2reg_d0_inf_weight_num))
//     val dp2reg_d0_nan_data_num_w = Mux(dp2reg_d0_set, io.dp2reg_nan_data_num,
//                                      Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                      dp2reg_d0_nan_data_num))  
//     val dp2reg_d0_inf_data_num_w = Mux(dp2reg_d0_set, io.dp2reg_inf_data_num,
//                                      Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                      dp2reg_d0_inf_data_num))

//     when(dp2reg_d0_reg){
//         dp2reg_d0_nan_weight_num := dp2reg_d0_nan_weight_num_w
//         dp2reg_d0_inf_weight_num := dp2reg_d0_inf_weight_num_w
//         dp2reg_d0_nan_data_num := dp2reg_d0_nan_data_num_w
//         dp2reg_d0_inf_data_num := dp2reg_d0_inf_data_num_w
//     }

//     //////// group 1 ////////
//     val dp2reg_d1_nan_weight_num_w = Mux(dp2reg_d1_set, io.dp2reg_nan_weight_num,
//                                      Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                      dp2reg_d1_nan_weight_num))
//     val dp2reg_d1_inf_weight_num_w = Mux(dp2reg_d1_set, io.dp2reg_inf_weight_num,
//                                      Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                      dp2reg_d1_inf_weight_num))
//     val dp2reg_d1_nan_data_num_w = Mux(dp2reg_d1_set, io.dp2reg_nan_data_num,
//                                      Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                      dp2reg_d1_nan_data_num))  
//     val dp2reg_d1_inf_data_num_w = Mux(dp2reg_d1_set, io.dp2reg_inf_data_num,
//                                      Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                      dp2reg_d1_inf_data_num))

//     when(dp2reg_d1_reg){
//         dp2reg_d1_nan_weight_num := dp2reg_d1_nan_weight_num_w
//         dp2reg_d1_inf_weight_num := dp2reg_d1_inf_weight_num_w
//         dp2reg_d1_nan_data_num := dp2reg_d1_nan_data_num_w
//         dp2reg_d1_inf_data_num := dp2reg_d1_inf_data_num_w
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //  for perf conting registers                                        //
//     ////////////////////////////////////////////////////////////////////////
//     //////// group 0 ////////
//     val dp2reg_d0_wt_rd_stall_w = Mux(dp2reg_d0_set, io.dp2reg_wt_rd_stall,
//                                      Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                      dp2reg_d0_wt_rd_stall))
//     val dp2reg_d0_wt_rd_latency_w = Mux(dp2reg_d0_set, io.dp2reg_wt_rd_latency,
//                                      Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                      dp2reg_d0_wt_rd_latency))
//     val dp2reg_d0_dat_rd_stall_w = Mux(dp2reg_d0_set, (io.dp2reg_dc_rd_stall | io.dp2reg_wg_rd_stall | io.dp2reg_img_rd_stall),
//                                      Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                      dp2reg_d0_dat_rd_stall))  
//     val dp2reg_d0_dat_rd_latency_w = Mux(dp2reg_d0_set, (io.dp2reg_dc_rd_latency | io.dp2reg_wg_rd_latency | io.dp2reg_img_rd_latency),
//                                      Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                      dp2reg_d0_dat_rd_latency))

//     when(dp2reg_d0_reg){                                 
//         dp2reg_d0_wt_rd_stall := dp2reg_d0_wt_rd_stall_w 
//         dp2reg_d0_wt_rd_latency := dp2reg_d0_wt_rd_latency_w
//         dp2reg_d0_dat_rd_stall := dp2reg_d0_dat_rd_stall_w
//         dp2reg_d0_dat_rd_latency := dp2reg_d0_dat_rd_latency_w
//     }
//     //////// group 1 ////////
//     val dp2reg_d1_wt_rd_stall_w = Mux(dp2reg_d1_set, io.dp2reg_wt_rd_stall,
//                                      Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                      dp2reg_d1_wt_rd_stall))
//     val dp2reg_d1_wt_rd_latency_w = Mux(dp2reg_d1_set, io.dp2reg_wt_rd_latency,
//                                      Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                      dp2reg_d1_wt_rd_latency))
//     val dp2reg_d1_dat_rd_stall_w = Mux(dp2reg_d1_set, (io.dp2reg_dc_rd_stall | io.dp2reg_wg_rd_stall | io.dp2reg_img_rd_stall),
//                                      Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                      dp2reg_d1_dat_rd_stall))  
//     val dp2reg_d1_dat_rd_latency_w = Mux(dp2reg_d1_set, (io.dp2reg_dc_rd_latency | io.dp2reg_wg_rd_latency | io.dp2reg_img_rd_latency),
//                                      Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                      dp2reg_d1_dat_rd_latency))

//     when(dp2reg_d1_reg){                                 
//         dp2reg_d1_wt_rd_stall := dp2reg_d1_wt_rd_stall_w 
//         dp2reg_d1_wt_rd_latency := dp2reg_d1_wt_rd_latency_w
//         dp2reg_d1_dat_rd_stall := dp2reg_d1_dat_rd_stall_w
//         dp2reg_d1_dat_rd_latency := dp2reg_d1_dat_rd_latency_w
//     }

// }}

// object NV_NVDLA_CDMA_regfileDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_regfile())
// }
