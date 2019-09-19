// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //Implementation overview of ping-pong register file.

// class NV_NVDLA_SDP_reg extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())      

//         //csb2sdp
//         val csb2cdma = new csb2dp_if

//         //reg2dp
//         val dp2reg_lut_hybrid = Input(UInt(32.W))
//         val dp2reg_lut_int_data = Input(UInt(16.W))
//         val dp2reg_lut_le_hit = Input(UInt(32.W))
//         val dp2reg_lut_lo_hit = Input(UInt(32.W))
//         val dp2reg_lut_oflow = Input(UInt(32.W))
//         val dp2reg_lut_uflow = Input(UInt(32.W))
//         val dp2reg_out_saturation = Input(UInt(32.W))
//         val dp2reg_status_inf_input_num = Input(UInt(32.W))
//         val dp2reg_status_nan_input_num = Input(UInt(32.W))
//         val dp2reg_status_nan_output_num = Input(UInt(32.W))
//         val dp2reg_status_unequal = Input(Bool())
//         val dp2reg_wdma_stall = Input(UInt(32.W))
//         val reg2dp_field = new sdp_reg_dual_flop_outputs


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
// //           │         │                  |     SDP     |
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
//     val s_reg.offset = Wire(UInt(24.W))
//     val s_reg.wr_data = Wire(UInt(32.W))
//     val s_reg.wr_en = Wire(Bool())
//     val dp2reg_lut_data = Wire(UInt(16.W))

//     val dp2reg_consumer = RegInit(false.B)  
//     val dp2reg_status_0 = Wire(UInt(2.W))
//     val dp2reg_status_1 = Wire(UInt(2.W))


//     val u_single_reg = Module(new NV_NVDLA_SDP_REG_single)

//     u_single_reg.io.nvdla_core_clk  := io.nvdla_core_clk
//     val s_reg.rd_data               = u_single_reg.io.reg.rd_data
//     u_single_reg.io.reg.offset      := s_reg.offset(11,0)
//     u_single_reg.io.reg.wr_data     := s_reg.wr_data
//     u_single_reg.io.reg.wr_en       := s_reg.wr_en
//     val reg2dp_lut_access_type      = u_single_reg.io.lut_access_type
//     val reg2dp_lut_addr             = u_single_reg.io.lut_addr
//     val reg2dp_lut_addr_trigger     = u_single_reg.io.lut_addr_trigger
//     val reg2dp_lut_table_id         = u_single_reg.io.lut_table_id
//     val reg2dp_lut_data_trigger     = u_single_reg.io.lut_data_trigger
//     io.reg2dp_lut_hybrid_priority   := u_single_reg.io.lut_hybrid_priority
//     io.reg2dp_lut_le_function       := u_single_reg.io.lut_le_function
//     io.reg2dp_lut_oflow_priority    := u_single_reg.io.lut_oflow_priority
//     io.reg2dp_lut_uflow_priority    := u_single_reg.io.lut_uflow_priority
//     io.reg2dp_lut_le_index_offset   := u_single_reg.io.lut_le_index_offset
//     io.reg2dp_lut_le_index_select   := u_single_reg.io.lut_le_index_select
//     io.reg2dp_lut_lo_index_select   := u_single_reg.io.lut_lo_index_select
//     io.reg2dp_lut_le_end            := u_single_reg.io.lut_le_end
//     io.reg2dp_lut_le_slope_oflow_scale  := u_single_reg.io.lut_le_slope_oflow_scale
//     io.reg2dp_lut_le_slope_uflow_scale  := u_single_reg.io.lut_le_slope_uflow_scale
//     io.reg2dp_lut_le_slope_oflow_shift  := u_single_reg.io.lut_le_slope_oflow_shift
//     io.reg2dp_lut_le_slope_uflow_shift  := u_single_reg.io.lut_le_slope_uflow_shift
//     io.reg2dp_lut_le_start          := u_single_reg.io.lut_le_start
//     io.reg2dp_lut_lo_end            := u_single_reg.io.lut_lo_end
//     io.reg2dp_lut_lo_slope_oflow_scale  := u_single_reg.io.lut_lo_slope_oflow_scale
//     io.reg2dp_lut_lo_slope_uflow_scale  := u_single_reg.io.lut_lo_slope_uflow_scale
//     io.reg2dp_lut_lo_slope_oflow_shift  := u_single_reg.io.lut_lo_slope_oflow_shift
//     io.reg2dp_lut_lo_slope_uflow_shift  := u_single_reg.io.lut_lo_slope_uflow_shift
//     io.reg2dp_lut_lo_start          := u_single_reg.io.lut_lo_start
//     val reg2dp_producer             = u_single_reg.io.producer
//     u_single_reg.io.lut_data        := dp2reg_lut_data
//     u_single_reg.io.consumer        := dp2reg_consumer
//     u_single_reg.io.status_0        := dp2reg_status_0
//     u_single_reg.io.status_1        := dp2reg_status_1

//     //Instance two duplicated register groups
//     //reg d0   
//     val d0_reg.offset = Wire(UInt(24.W))
//     val d0_reg.wr_data = Wire(UInt(32.W))
//     val d0_reg.wr_en = Wire(Bool())
//     val reg2dp_d0_op_en = RegInit(false.B)
//     val dp2reg_d0_lut_hybrid = RegInit(0.U(32.W))
//     val dp2reg_d0_lut_le_hit = RegInit(0.U(32.W))
//     val dp2reg_d0_lut_lo_hit = RegInit(0.U(32.W))
//     val dp2reg_d0_lut_oflow = RegInit(0.U(32.W))
//     val dp2reg_d0_lut_uflow = RegInit(0.U(32.W))
//     val dp2reg_d0_out_saturation = RegInit(0.U(32.W))
//     val dp2reg_d0_wdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d0_status_unequal = RegInit(false.B)
//     val dp2reg_d0_status_inf_input_num = RegInit(0.U(32.W))
//     val dp2reg_d0_status_nan_input_num = RegInit(0.U(32.W))
//     val dp2reg_d0_status_nan_output_num = RegInit(0.U(32.W))

//     val u_dual_reg_d0 = Module(new NV_NVDLA_SDP_REG_dual)
//     u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
//     val d0_reg.rd_data = u_dual_reg_d0.io.reg.rd_data
//     u_dual_reg_d0.io.reg.offset := d0_reg.offset(11,0)
//     u_dual_reg_d0.io.reg.wr_data := d0_reg.wr_data
//     u_dual_reg_d0.io.reg.wr_en := d0_reg.wr_en
//     val reg2dp_d0_cvt_offset = u_dual_reg_d0.io.cvt_offset
//     val reg2dp_d0_cvt_scale = u_dual_reg_d0.io.cvt_scale
//     val reg2dp_d0_cvt_shift = u_dual_reg_d0.io.cvt_shift
//     val reg2dp_d0_channel = u_dual_reg_d0.io.channel
//     val reg2dp_d0_height = u_dual_reg_d0.io.height
//     val reg2dp_d0_width = u_dual_reg_d0.io.width_a
//     val reg2dp_d0_out_precision = u_dual_reg_d0.io.out_precision
//     val reg2dp_d0_proc_precision = u_dual_reg_d0.io.proc_precision
//     val reg2dp_d0_bn_alu_shift_value = u_dual_reg_d0.io.bn_alu_shift_value
//     val reg2dp_d0_bn_alu_src = u_dual_reg_d0.io.bn_alu_src
//     val reg2dp_d0_bn_alu_operand = u_dual_reg_d0.io.bn_alu_operand
//     val reg2dp_d0_bn_alu_algo = u_dual_reg_d0.io.bn_alu_algo
//     val reg2dp_d0_bn_alu_bypass = u_dual_reg_d0.io.bn_alu_bypass
//     val reg2dp_d0_bn_bypass = u_dual_reg_d0.io.bn_bypass
//     val reg2dp_d0_bn_mul_bypass = u_dual_reg_d0.io.bn_mul_bypass
//     val reg2dp_d0_bn_mul_prelu = u_dual_reg_d0.io.bn_mul_prelu
//     val reg2dp_d0_bn_relu_bypass = u_dual_reg_d0.io.bn_relu_bypass
//     val reg2dp_d0_bn_mul_shift_value = u_dual_reg_d0.io.bn_mul_shift_value
//     val reg2dp_d0_bn_mul_src = u_dual_reg_d0.io.bn_mul_src
//     val reg2dp_d0_bn_mul_operand = u_dual_reg_d0.io.bn_mul_operand
//     val reg2dp_d0_bs_alu_shift_value = u_dual_reg_d0.io.bs_alu_shift_value
//     val reg2dp_d0_bs_alu_src = u_dual_reg_d0.io.bs_alu_src
//     val reg2dp_d0_bs_alu_operand = u_dual_reg_d0.io.bs_alu_operand
//     val reg2dp_d0_bs_alu_algo = u_dual_reg_d0.io.bs_alu_algo
//     val reg2dp_d0_bs_alu_bypass = u_dual_reg_d0.io.bs_alu_bypass
//     val reg2dp_d0_bs_bypass = u_dual_reg_d0.io.bs_bypass
//     val reg2dp_d0_bs_mul_bypass = u_dual_reg_d0.io.bs_mul_bypass
//     val reg2dp_d0_bs_mul_prelu = u_dual_reg_d0.io.bs_mul_prelu
//     val reg2dp_d0_bs_relu_bypass = u_dual_reg_d0.io.bs_relu_bypass
//     val reg2dp_d0_bs_mul_shift_value = u_dual_reg_d0.io.bs_mul_shift_value
//     val reg2dp_d0_bs_mul_src = u_dual_reg_d0.io.bs_mul_src
//     val reg2dp_d0_bs_mul_operand = u_dual_reg_d0.io.bs_mul_operand
//     val reg2dp_d0_ew_alu_cvt_bypass = u_dual_reg_d0.io.ew_alu_cvt_bypass
//     val reg2dp_d0_ew_alu_src = u_dual_reg_d0.io.ew_alu_src
//     val reg2dp_d0_ew_alu_cvt_offset = u_dual_reg_d0.io.ew_alu_cvt_offset
//     val reg2dp_d0_ew_alu_cvt_scale = u_dual_reg_d0.io.ew_alu_cvt_scale
//     val reg2dp_d0_ew_alu_cvt_truncate = u_dual_reg_d0.io.ew_alu_cvt_truncate
//     val reg2dp_d0_ew_alu_operand = u_dual_reg_d0.io.ew_alu_operand
//     val reg2dp_d0_ew_alu_algo = u_dual_reg_d0.io.ew_alu_algo
//     val reg2dp_d0_ew_alu_bypass = u_dual_reg_d0.io.ew_alu_bypass
//     val reg2dp_d0_ew_bypass = u_dual_reg_d0.io.ew_bypass
//     val reg2dp_d0_ew_lut_bypass = u_dual_reg_d0.io.ew_lut_bypass
//     val reg2dp_d0_ew_mul_bypass = u_dual_reg_d0.io.ew_mul_bypass
//     val reg2dp_d0_ew_mul_prelu = u_dual_reg_d0.io.ew_mul_prelu
//     val reg2dp_d0_ew_mul_cvt_bypass = u_dual_reg_d0.io.ew_mul_cvt_bypass
//     val reg2dp_d0_ew_mul_src = u_dual_reg_d0.io.ew_mul_src
//     val reg2dp_d0_ew_mul_cvt_offset = u_dual_reg_d0.io.ew_mul_cvt_offset
//     val reg2dp_d0_ew_mul_cvt_scale = u_dual_reg_d0.io.ew_mul_cvt_scale
//     val reg2dp_d0_ew_mul_cvt_truncate = u_dual_reg_d0.io.ew_mul_cvt_truncate
//     val reg2dp_d0_ew_mul_operand = u_dual_reg_d0.io.ew_mul_operand
//     val reg2dp_d0_ew_truncate = u_dual_reg_d0.io.ew_truncate
//     val reg2dp_d0_dst_base_addr_high = u_dual_reg_d0.io.dst_base_addr_high
//     val reg2dp_d0_dst_base_addr_low = u_dual_reg_d0.io.dst_base_addr_low
//     val reg2dp_d0_dst_batch_stride = u_dual_reg_d0.io.dst_batch_stride
//     val reg2dp_d0_dst_ram_type = u_dual_reg_d0.io.dst_ram_type
//     val reg2dp_d0_dst_line_stride = u_dual_reg_d0.io.dst_line_stride
//     val reg2dp_d0_dst_surface_stride = u_dual_reg_d0.io.dst_surface_stride
//     val reg2dp_d0_batch_number = u_dual_reg_d0.io.batch_number
//     val reg2dp_d0_flying_mode = u_dual_reg_d0.io.flying_mode
//     val reg2dp_d0_nan_to_zero = u_dual_reg_d0.io.nan_to_zero
//     val reg2dp_d0_output_dst = u_dual_reg_d0.io.output_dst
//     val reg2dp_d0_winograd = u_dual_reg_d0.io.winograd
//     val reg2dp_d0_op_en_trigger = u_dual_reg_d0.io.op_en_trigger
//     val reg2dp_d0_perf_dma_en = u_dual_reg_d0.io.perf_dma_en
//     val reg2dp_d0_perf_lut_en = u_dual_reg_d0.io.perf_lut_en
//     val reg2dp_d0_perf_nan_inf_count_en = u_dual_reg_d0.io.perf_nan_inf_count_en
//     val reg2dp_d0_perf_sat_en = u_dual_reg_d0.io.perf_sat_en
//     u_dual_reg_d0.io.op_en := reg2dp_d0_op_en
//     u_dual_reg_d0.io.lut_hybrid := dp2reg_d0_lut_hybrid
//     u_dual_reg_d0.io.lut_le_hit := dp2reg_d0_lut_le_hit
//     u_dual_reg_d0.io.lut_lo_hit := dp2reg_d0_lut_lo_hit
//     u_dual_reg_d0.io.lut_oflow := dp2reg_d0_lut_oflow
//     u_dual_reg_d0.io.lut_uflow := dp2reg_d0_lut_uflow
//     u_dual_reg_d0.io.out_saturation := dp2reg_d0_out_saturation
//     u_dual_reg_d0.io.wdma_stall := dp2reg_d0_wdma_stall
//     u_dual_reg_d0.io.status_unequal := dp2reg_d0_status_unequal
//     u_dual_reg_d0.io.status_inf_input_num := dp2reg_d0_status_inf_input_num
//     u_dual_reg_d0.io.status_nan_input_num := dp2reg_d0_status_nan_input_num
//     u_dual_reg_d0.io.status_nan_output_num := dp2reg_d0_status_nan_output_num


//     //reg d1   
//     val d1_reg.offset = Wire(UInt(24.W))
//     val d1_reg.wr_data = Wire(UInt(32.W))
//     val d1_reg.wr_en = Wire(Bool())
//     val reg2dp_d1_op_en = RegInit(false.B)
//     val dp2reg_d1_lut_hybrid = RegInit(0.U(32.W))
//     val dp2reg_d1_lut_le_hit = RegInit(0.U(32.W))
//     val dp2reg_d1_lut_lo_hit = RegInit(0.U(32.W))
//     val dp2reg_d1_lut_oflow = RegInit(0.U(32.W))
//     val dp2reg_d1_lut_uflow = RegInit(0.U(32.W))
//     val dp2reg_d1_out_saturation = RegInit(0.U(32.W))
//     val dp2reg_d1_wdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d1_status_unequal = RegInit(false.B)
//     val dp2reg_d1_status_inf_input_num = RegInit(0.U(32.W))
//     val dp2reg_d1_status_nan_input_num = RegInit(0.U(32.W))
//     val dp2reg_d1_status_nan_output_num = RegInit(0.U(32.W))

//     val u_dual_reg_d1 = Module(new NV_NVDLA_SDP_REG_dual)
//     u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk
//     val d1_reg.rd_data = u_dual_reg_d1.io.reg.rd_data
//     u_dual_reg_d1.io.reg.offset := d1_reg.offset(11,0)
//     u_dual_reg_d1.io.reg.wr_data := d1_reg.wr_data
//     u_dual_reg_d1.io.reg.wr_en := d1_reg.wr_en
//     val reg2dp_d1_cvt_offset = u_dual_reg_d1.io.cvt_offset
//     val reg2dp_d1_cvt_scale = u_dual_reg_d1.io.cvt_scale
//     val reg2dp_d1_cvt_shift = u_dual_reg_d1.io.cvt_shift
//     val reg2dp_d1_channel = u_dual_reg_d1.io.channel
//     val reg2dp_d1_height = u_dual_reg_d1.io.height
//     val reg2dp_d1_width = u_dual_reg_d1.io.width_a
//     val reg2dp_d1_out_precision = u_dual_reg_d1.io.out_precision
//     val reg2dp_d1_proc_precision = u_dual_reg_d1.io.proc_precision
//     val reg2dp_d1_bn_alu_shift_value = u_dual_reg_d1.io.bn_alu_shift_value
//     val reg2dp_d1_bn_alu_src = u_dual_reg_d1.io.bn_alu_src
//     val reg2dp_d1_bn_alu_operand = u_dual_reg_d1.io.bn_alu_operand
//     val reg2dp_d1_bn_alu_algo = u_dual_reg_d1.io.bn_alu_algo
//     val reg2dp_d1_bn_alu_bypass = u_dual_reg_d1.io.bn_alu_bypass
//     val reg2dp_d1_bn_bypass = u_dual_reg_d1.io.bn_bypass
//     val reg2dp_d1_bn_mul_bypass = u_dual_reg_d1.io.bn_mul_bypass
//     val reg2dp_d1_bn_mul_prelu = u_dual_reg_d1.io.bn_mul_prelu
//     val reg2dp_d1_bn_relu_bypass = u_dual_reg_d1.io.bn_relu_bypass
//     val reg2dp_d1_bn_mul_shift_value = u_dual_reg_d1.io.bn_mul_shift_value
//     val reg2dp_d1_bn_mul_src = u_dual_reg_d1.io.bn_mul_src
//     val reg2dp_d1_bn_mul_operand = u_dual_reg_d1.io.bn_mul_operand
//     val reg2dp_d1_bs_alu_shift_value = u_dual_reg_d1.io.bs_alu_shift_value
//     val reg2dp_d1_bs_alu_src = u_dual_reg_d1.io.bs_alu_src
//     val reg2dp_d1_bs_alu_operand = u_dual_reg_d1.io.bs_alu_operand
//     val reg2dp_d1_bs_alu_algo = u_dual_reg_d1.io.bs_alu_algo
//     val reg2dp_d1_bs_alu_bypass = u_dual_reg_d1.io.bs_alu_bypass
//     val reg2dp_d1_bs_bypass = u_dual_reg_d1.io.bs_bypass
//     val reg2dp_d1_bs_mul_bypass = u_dual_reg_d1.io.bs_mul_bypass
//     val reg2dp_d1_bs_mul_prelu = u_dual_reg_d1.io.bs_mul_prelu
//     val reg2dp_d1_bs_relu_bypass = u_dual_reg_d1.io.bs_relu_bypass
//     val reg2dp_d1_bs_mul_shift_value = u_dual_reg_d1.io.bs_mul_shift_value
//     val reg2dp_d1_bs_mul_src = u_dual_reg_d1.io.bs_mul_src
//     val reg2dp_d1_bs_mul_operand = u_dual_reg_d1.io.bs_mul_operand
//     val reg2dp_d1_ew_alu_cvt_bypass = u_dual_reg_d1.io.ew_alu_cvt_bypass
//     val reg2dp_d1_ew_alu_src = u_dual_reg_d1.io.ew_alu_src
//     val reg2dp_d1_ew_alu_cvt_offset = u_dual_reg_d1.io.ew_alu_cvt_offset
//     val reg2dp_d1_ew_alu_cvt_scale = u_dual_reg_d1.io.ew_alu_cvt_scale
//     val reg2dp_d1_ew_alu_cvt_truncate = u_dual_reg_d1.io.ew_alu_cvt_truncate
//     val reg2dp_d1_ew_alu_operand = u_dual_reg_d1.io.ew_alu_operand
//     val reg2dp_d1_ew_alu_algo = u_dual_reg_d1.io.ew_alu_algo
//     val reg2dp_d1_ew_alu_bypass = u_dual_reg_d1.io.ew_alu_bypass
//     val reg2dp_d1_ew_bypass = u_dual_reg_d1.io.ew_bypass
//     val reg2dp_d1_ew_lut_bypass = u_dual_reg_d1.io.ew_lut_bypass
//     val reg2dp_d1_ew_mul_bypass = u_dual_reg_d1.io.ew_mul_bypass
//     val reg2dp_d1_ew_mul_prelu = u_dual_reg_d1.io.ew_mul_prelu
//     val reg2dp_d1_ew_mul_cvt_bypass = u_dual_reg_d1.io.ew_mul_cvt_bypass
//     val reg2dp_d1_ew_mul_src = u_dual_reg_d1.io.ew_mul_src
//     val reg2dp_d1_ew_mul_cvt_offset = u_dual_reg_d1.io.ew_mul_cvt_offset
//     val reg2dp_d1_ew_mul_cvt_scale = u_dual_reg_d1.io.ew_mul_cvt_scale
//     val reg2dp_d1_ew_mul_cvt_truncate = u_dual_reg_d1.io.ew_mul_cvt_truncate
//     val reg2dp_d1_ew_mul_operand = u_dual_reg_d1.io.ew_mul_operand
//     val reg2dp_d1_ew_truncate = u_dual_reg_d1.io.ew_truncate
//     val reg2dp_d1_dst_base_addr_high = u_dual_reg_d1.io.dst_base_addr_high
//     val reg2dp_d1_dst_base_addr_low = u_dual_reg_d1.io.dst_base_addr_low
//     val reg2dp_d1_dst_batch_stride = u_dual_reg_d1.io.dst_batch_stride
//     val reg2dp_d1_dst_ram_type = u_dual_reg_d1.io.dst_ram_type
//     val reg2dp_d1_dst_line_stride = u_dual_reg_d1.io.dst_line_stride
//     val reg2dp_d1_dst_surface_stride = u_dual_reg_d1.io.dst_surface_stride
//     val reg2dp_d1_batch_number = u_dual_reg_d1.io.batch_number
//     val reg2dp_d1_flying_mode = u_dual_reg_d1.io.flying_mode
//     val reg2dp_d1_nan_to_zero = u_dual_reg_d1.io.nan_to_zero
//     val reg2dp_d1_output_dst = u_dual_reg_d1.io.output_dst
//     val reg2dp_d1_winograd = u_dual_reg_d1.io.winograd
//     val reg2dp_d1_op_en_trigger = u_dual_reg_d1.io.op_en_trigger
//     val reg2dp_d1_perf_dma_en = u_dual_reg_d1.io.perf_dma_en
//     val reg2dp_d1_perf_lut_en = u_dual_reg_d1.io.perf_lut_en
//     val reg2dp_d1_perf_nan_inf_count_en = u_dual_reg_d1.io.perf_nan_inf_count_en
//     val reg2dp_d1_perf_sat_en = u_dual_reg_d1.io.perf_sat_en
//     u_dual_reg_d1.io.op_en := reg2dp_d1_op_en
//     u_dual_reg_d1.io.lut_hybrid := dp2reg_d1_lut_hybrid
//     u_dual_reg_d1.io.lut_le_hit := dp2reg_d1_lut_le_hit
//     u_dual_reg_d1.io.lut_lo_hit := dp2reg_d1_lut_lo_hit
//     u_dual_reg_d1.io.lut_oflow := dp2reg_d1_lut_oflow
//     u_dual_reg_d1.io.lut_uflow := dp2reg_d1_lut_uflow
//     u_dual_reg_d1.io.out_saturation := dp2reg_d1_out_saturation
//     u_dual_reg_d1.io.wdma_stall := dp2reg_d1_wdma_stall
//     u_dual_reg_d1.io.status_unequal := dp2reg_d1_status_unequal
//     u_dual_reg_d1.io.status_inf_input_num := dp2reg_d1_status_inf_input_num
//     u_dual_reg_d1.io.status_nan_input_num := dp2reg_d1_status_nan_input_num
//     u_dual_reg_d1.io.status_nan_output_num := dp2reg_d1_status_nan_output_num


//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE CONSUMER PIONTER IN GENERAL SINGLE REGISTER GROUP         //
//     //                                                                    //
//     //////////////////////////////////////////////////////////////////////// 
//     val dp2reg_consumer_w = dp2reg_consumer

//     when(io.dp2reg_done){
//         dp2reg_consumer := dp2reg_consumer_w
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE TWO STATUS FIELDS IN GENERAL SINGLE REGISTER GROUP        //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     dp2reg_status_0 := Mux(reg2dp_d0_op_en === false.B,  0.U, 
//                        Mux(dp2reg_consumer === true.B, 2.U, 1.U))

//     dp2reg_status_1 := Mux(reg2dp_d1_op_en === false.B, 0.U, 
//                        Mux(dp2reg_consumer === false.B, 2.U, 1.U))

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE OP_EN LOGIC                                               //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     val reg2dp_op_en_reg = RegInit(0.U(3.W))
//     val reg_wr_data = Wire(UInt(32.W))

//     val reg2dp_d0_op_en_w = Mux(!reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg.wr_data(0), 
//                             Mux(io.dp2reg_done && dp2reg_consumer === false.B, false.B, reg2dp_d0_op_en))
//     reg2dp_d0_op_en := reg2dp_d0_op_en_w

//     val reg2dp_d1_op_en_w = Mux(!reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg.wr_data(0), 
//                             Mux(io.dp2reg_done && dp2reg_consumer === true.B, false.B, reg2dp_d1_op_en))
//     reg2dp_d1_op_en := reg2dp_d1_op_en_w

//     val reg2dp_op_en_ori = Mux(dp2reg_consumer, reg2dp_d1_op_en, reg2dp_d0_op_en)

//     val reg2dp_op_en_reg_w = Mux(io.dp2reg_done,  0.U, Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori))

//     reg2dp_op_en_reg := reg2dp_op_en_reg_w 

//     io.reg2dp_op_en := reg2dp_op_en_reg(2)

//     val slcg_op_en = ShiftRegister(Fill(4, reg2dp_op_en_ori), 3)

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE ACCESS LOGIC TO EACH REGISTER GROUP                       //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     //EACH subunit has 4KB address space 
//     val reg_offset = Wire(UInt(24.W))
//     val select_s = Mux(reg_offset(11,0) < "h038".asUInt(32.W), true.B, false.B)
//     val select_d0 = (reg_offset(11,0) >= "h038".asUInt(32.W)) & (reg2dp_producer === false.B)
//     val select_d1 = (reg_offset(11,0) >= "h038".asUInt(32.W)) & (reg2dp_producer === true.B)

//     val reg_wr_en = Wire(Bool())
//     s_reg_wr_en := reg_wr_en & select_s
//     d0_reg_wr_en := reg_wr_en & select_d0 & !reg2dp_d0_op_en
//     d1_reg_wr_en := reg_wr_en & select_d1 & !reg2dp_d1_op_en

//     s_reg_offset := reg_offset
//     d0_reg_offset := reg_offset
//     d1_reg_offset := reg_offset

//     s_reg_wr_data  := reg_wr_data
//     d0_reg_wr_data := reg_wr_data
//     d1_reg_wr_data := reg_wr_data

//     val reg_rd_data =  (Fill(32, select_s) & s_reg_rd_data) |
//                         (Fill(32, select_d0) & d0_reg_rd_data) |
//                         (Fill(32, select_d1) & d1_reg_rd_data)

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//      val csb_logic = Module(new NV_NVDLA_CSB_LOGIC)
//     csb_logic.io.clk := io.nvdla_core_clk
//     csb_logic.io.csb2dp <> io.csb2cdma
//     reg_offset := csb_logic.io.reg.offset
//     reg_wr_en := csb_logic.io.reg.wr_en
//     reg_wr_data := csb_logic.io.reg.wr_data
//     csb_logic.io.reg.rd_data := reg_rd_data   

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////

//     io.reg2dp_cvt_offset := Mux(dp2reg_consumer, reg2dp_d1_cvt_offset, reg2dp_d0_cvt_offset)
//     io.reg2dp_cvt_scale := Mux(dp2reg_consumer, reg2dp_d1_cvt_scale, reg2dp_d0_cvt_scale)
//     io.reg2dp_cvt_shift := Mux(dp2reg_consumer, reg2dp_d1_cvt_shift, reg2dp_d0_cvt_shift)
//     io.reg2dp_channel := Mux(dp2reg_consumer, reg2dp_d1_channel, reg2dp_d0_channel)
//     io.reg2dp_height := Mux(dp2reg_consumer, reg2dp_d1_height, reg2dp_d0_height)
//     io.reg2dp_width := Mux(dp2reg_consumer, reg2dp_d1_width, reg2dp_d0_width)
//     io.reg2dp_out_precision := Mux(dp2reg_consumer, reg2dp_d1_out_precision, reg2dp_d0_out_precision)
//     io.reg2dp_proc_precision := Mux(dp2reg_consumer, reg2dp_d1_proc_precision, reg2dp_d0_proc_precision)
//     io.reg2dp_bn_alu_shift_value := Mux(dp2reg_consumer, reg2dp_d1_bn_alu_shift_value, reg2dp_d0_bn_alu_shift_value)
//     io.reg2dp_bn_alu_src := Mux(dp2reg_consumer, reg2dp_d1_bn_alu_src, reg2dp_d0_bn_alu_src)
//     io.reg2dp_bn_alu_operand := Mux(dp2reg_consumer, reg2dp_d1_bn_alu_operand, reg2dp_d0_bn_alu_operand)
//     io.reg2dp_bn_alu_algo := Mux(dp2reg_consumer, reg2dp_d1_bn_alu_algo, reg2dp_d0_bn_alu_algo)
//     io.reg2dp_bn_alu_bypass := Mux(dp2reg_consumer, reg2dp_d1_bn_alu_bypass, reg2dp_d0_bn_alu_bypass)
//     io.reg2dp_bn_bypass := Mux(dp2reg_consumer, reg2dp_d1_bn_bypass, reg2dp_d0_bn_bypass)
//     io.reg2dp_bn_mul_bypass := Mux(dp2reg_consumer, reg2dp_d1_bn_mul_bypass, reg2dp_d0_bn_mul_bypass)
//     io.reg2dp_bn_mul_prelu := Mux(dp2reg_consumer, reg2dp_d1_bn_mul_prelu, reg2dp_d0_bn_mul_prelu)
//     io.reg2dp_bn_relu_bypass := Mux(dp2reg_consumer, reg2dp_d1_bn_relu_bypass, reg2dp_d0_bn_relu_bypass)
//     io.reg2dp_bn_mul_shift_value := Mux(dp2reg_consumer, reg2dp_d1_bn_mul_shift_value, reg2dp_d0_bn_mul_shift_value)
//     io.reg2dp_bn_mul_src := Mux(dp2reg_consumer, reg2dp_d1_bn_mul_src, reg2dp_d0_bn_mul_src)
//     io.reg2dp_bn_mul_operand := Mux(dp2reg_consumer, reg2dp_d1_bn_mul_operand, reg2dp_d0_bn_mul_operand)
//     io.reg2dp_bs_alu_shift_value := Mux(dp2reg_consumer, reg2dp_d1_bs_alu_shift_value, reg2dp_d0_bs_alu_shift_value)
//     io.reg2dp_bs_alu_src := Mux(dp2reg_consumer, reg2dp_d1_bs_alu_src, reg2dp_d0_bs_alu_src)
//     io.reg2dp_bs_alu_operand := Mux(dp2reg_consumer, reg2dp_d1_bs_alu_operand, reg2dp_d0_bs_alu_operand)
//     io.reg2dp_bs_alu_algo := Mux(dp2reg_consumer, reg2dp_d1_bs_alu_algo, reg2dp_d0_bs_alu_algo)
//     io.reg2dp_bs_alu_bypass := Mux(dp2reg_consumer, reg2dp_d1_bs_alu_bypass, reg2dp_d0_bs_alu_bypass)
//     io.reg2dp_bs_bypass := Mux(dp2reg_consumer, reg2dp_d1_bs_bypass, reg2dp_d0_bs_bypass)
//     io.reg2dp_bs_mul_bypass := Mux(dp2reg_consumer, reg2dp_d1_bs_mul_bypass, reg2dp_d0_bs_mul_bypass)
//     io.reg2dp_bs_mul_prelu := Mux(dp2reg_consumer, reg2dp_d1_bs_mul_prelu, reg2dp_d0_bs_mul_prelu)
//     io.reg2dp_bs_relu_bypass := Mux(dp2reg_consumer, reg2dp_d1_bs_relu_bypass, reg2dp_d0_bs_relu_bypass)
//     io.reg2dp_bs_mul_shift_value := Mux(dp2reg_consumer, reg2dp_d1_bs_mul_shift_value, reg2dp_d0_bs_mul_shift_value)
//     io.reg2dp_bs_mul_src := Mux(dp2reg_consumer, reg2dp_d1_bs_mul_src, reg2dp_d0_bs_mul_src)
//     io.reg2dp_bs_mul_operand := Mux(dp2reg_consumer, reg2dp_d1_bs_mul_operand, reg2dp_d0_bs_mul_operand)
//     io.reg2dp_ew_alu_cvt_bypass := Mux(dp2reg_consumer, reg2dp_d1_ew_alu_cvt_bypass, reg2dp_d0_ew_alu_cvt_bypass)
//     io.reg2dp_ew_alu_src := Mux(dp2reg_consumer, reg2dp_d1_ew_alu_src, reg2dp_d0_ew_alu_src)
//     io.reg2dp_ew_alu_cvt_offset := Mux(dp2reg_consumer, reg2dp_d1_ew_alu_cvt_offset, reg2dp_d0_ew_alu_cvt_offset)
//     io.reg2dp_ew_alu_cvt_scale := Mux(dp2reg_consumer, reg2dp_d1_ew_alu_cvt_scale, reg2dp_d0_ew_alu_cvt_scale)
//     io.reg2dp_ew_alu_cvt_truncate := Mux(dp2reg_consumer, reg2dp_d1_ew_alu_cvt_truncate, reg2dp_d0_ew_alu_cvt_truncate)
//     io.reg2dp_ew_alu_operand := Mux(dp2reg_consumer, reg2dp_d1_ew_alu_operand, reg2dp_d0_ew_alu_operand)
//     io.reg2dp_ew_alu_algo := Mux(dp2reg_consumer, reg2dp_d1_ew_alu_algo, reg2dp_d0_ew_alu_algo)
//     io.reg2dp_ew_alu_bypass := Mux(dp2reg_consumer, reg2dp_d1_ew_alu_bypass, reg2dp_d0_ew_alu_bypass)
//     io.reg2dp_ew_bypass := Mux(dp2reg_consumer, reg2dp_d1_ew_bypass, reg2dp_d0_ew_bypass)
//     io.reg2dp_ew_lut_bypass := Mux(dp2reg_consumer, reg2dp_d1_ew_lut_bypass, reg2dp_d0_ew_lut_bypass)
//     io.reg2dp_ew_mul_bypass := Mux(dp2reg_consumer, reg2dp_d1_ew_mul_bypass, reg2dp_d0_ew_mul_bypass)
//     io.reg2dp_ew_mul_prelu := Mux(dp2reg_consumer, reg2dp_d1_ew_mul_prelu, reg2dp_d0_ew_mul_prelu)
//     io.reg2dp_ew_mul_cvt_bypass := Mux(dp2reg_consumer, reg2dp_d1_ew_mul_cvt_bypass, reg2dp_d0_ew_mul_cvt_bypass)
//     io.reg2dp_ew_mul_src := Mux(dp2reg_consumer, reg2dp_d1_ew_mul_src, reg2dp_d0_ew_mul_src)
//     io.reg2dp_ew_mul_cvt_offset := Mux(dp2reg_consumer, reg2dp_d1_ew_mul_cvt_offset, reg2dp_d0_ew_mul_cvt_offset)
//     io.reg2dp_ew_mul_cvt_scale := Mux(dp2reg_consumer, reg2dp_d1_ew_mul_cvt_scale, reg2dp_d0_ew_mul_cvt_scale)
//     io.reg2dp_ew_mul_cvt_truncate := Mux(dp2reg_consumer, reg2dp_d1_ew_mul_cvt_truncate, reg2dp_d0_ew_mul_cvt_truncate)
//     io.reg2dp_ew_mul_operand := Mux(dp2reg_consumer, reg2dp_d1_ew_mul_operand, reg2dp_d0_ew_mul_operand)
//     io.reg2dp_ew_truncate := Mux(dp2reg_consumer, reg2dp_d1_ew_truncate, reg2dp_d0_ew_truncate)
//     io.reg2dp_dst_base_addr_high := Mux(dp2reg_consumer, reg2dp_d1_dst_base_addr_high, reg2dp_d0_dst_base_addr_high)
//     io.reg2dp_dst_base_addr_low := Mux(dp2reg_consumer, reg2dp_d1_dst_base_addr_low, reg2dp_d0_dst_base_addr_low)
//     io.reg2dp_dst_batch_stride := Mux(dp2reg_consumer, reg2dp_d1_dst_batch_stride, reg2dp_d0_dst_batch_stride)
//     io.reg2dp_dst_ram_type := Mux(dp2reg_consumer, reg2dp_d1_dst_ram_type, reg2dp_d0_dst_ram_type)
//     io.reg2dp_dst_line_stride := Mux(dp2reg_consumer, reg2dp_d1_dst_line_stride, reg2dp_d0_dst_line_stride)
//     io.reg2dp_dst_surface_stride := Mux(dp2reg_consumer, reg2dp_d1_dst_surface_stride, reg2dp_d0_dst_surface_stride)
//     io.reg2dp_batch_number := Mux(dp2reg_consumer, reg2dp_d1_batch_number, reg2dp_d0_batch_number)
//     io.reg2dp_flying_mode := Mux(dp2reg_consumer, reg2dp_d1_flying_mode, reg2dp_d0_flying_mode)
//     io.reg2dp_nan_to_zero := Mux(dp2reg_consumer, reg2dp_d1_nan_to_zero, reg2dp_d0_nan_to_zero)
//     io.reg2dp_output_dst := Mux(dp2reg_consumer, reg2dp_d1_output_dst, reg2dp_d0_output_dst)
//     io.reg2dp_winograd := Mux(dp2reg_consumer, reg2dp_d1_winograd, reg2dp_d0_winograd)
//     io.reg2dp_perf_dma_en := Mux(dp2reg_consumer, reg2dp_d1_perf_dma_en, reg2dp_d0_perf_dma_en)
//     io.reg2dp_perf_lut_en := Mux(dp2reg_consumer, reg2dp_d1_perf_lut_en, reg2dp_d0_perf_lut_en)
//     io.reg2dp_perf_nan_inf_count_en := Mux(dp2reg_consumer, reg2dp_d1_perf_nan_inf_count_en, reg2dp_d0_perf_nan_inf_count_en)
//     io.reg2dp_perf_sat_en := Mux(dp2reg_consumer, reg2dp_d1_perf_sat_en, reg2dp_d0_perf_sat_en)


//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // PASTE ADDIFITON LOGIC HERE FROM EXTRA FILE                         //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     // USER logic can be put here:

// // ifndef ___ARNVDLA_VH_INC_
// //assign reg2dp_lut_data = reg.wr_data[::range(15)]
//     io.reg2dp_interrupt_ptr := dp2reg_consumer

// //===================================================
// //////// Single Flop Write Control////////
// //===================================================

//     val lut_int_addr_trigger = RegInit(false.B) 
//     lut_int_addr_trigger := reg2dp_lut_addr_trigger

//     val lut_int_data_rd_trigger = reg_rd_en & (Cat(0.U(20.W), reg.offset(11,0)) === "h00c".asUInt(32.W))
//     val lut_int_data_wr_trigger = RegInit(false.B)
//     lut_int_data_wr_trigger := reg2dp_lut_data_trigger

//     io.reg2dp_lut_int_data_wr := lut_int_data_wr_trigger

//     val lut_int_addr = RegInit(0.U(10.W))
//     io.reg2dp_lut_int_addr := lut_int_addr
//     when(lut_int_addr_trigger){
//         lut_int_addr := reg2dp_lut_addr
//     }.elsewhen(lut_int_data_wr_trigger || lut_int_data_rd_trigger){
//         lut_int_addr := lut_int_addr + 1.U
//     }

//     val lut_int_access_type = RegInit(false.B)
//     when(lut_int_addr_trigger){
//         lut_int_access_type := reg2dp_lut_access_type
//     }
//     io.reg2dp_lut_int_access_type := lut_int_access_type

//     val lut_int_table_id = RegInit(false.B)
//     when(lut_int_addr_trigger){
//         lut_int_table_id := reg2dp_lut_table_id
//     }
//     io.reg2dp_lut_int_table_id := lut_int_table_id

//     val reg2dp_lut_int_data_out = Reg(UInt(16.W))
//     when(reg2dp_lut_data_trigger){
//         reg2dp_lut_int_data_out := s_reg.wr_data
//     }
//     io.reg2dp_lut_int_data := reg2dp_lut_int_data_out
//     dp2reg_lut_data := io.dp2reg_lut_int_data

//     val wdma_slcg_op_en = slcg_op_en(0)
//     val bcore_slcg_op_en = slcg_op_en(1)
//     val ncore_slcg_op_en = slcg_op_en(2)
//     val ecore_slcg_op_en = slcg_op_en(3)

//     val reg2dp_lut_slcg_en_out = RegInit(false.B)
//     when(reg2dp_lut_addr_trigger){
//         reg2dp_lut_slcg_en_out := true.B
//     }.elsewhen(ecore_slcg_op_en){
//         reg2dp_lut_slcg_en_out := false.B
//     }
//     io.reg2dp_lut_slcg_en := reg2dp_lut_slcg_en_out

//     io.reg2dp_wdma_slcg_op_en := wdma_slcg_op_en
//     io.reg2dp_bcore_slcg_op_en := bcore_slcg_op_en
//     io.reg2dp_ncore_slcg_op_en := ncore_slcg_op_en
//     io.reg2dp_ecore_slcg_op_en := ecore_slcg_op_en

// //===================================================
// //////// Dual Flop Write Control////////
// //===================================================
    
//     val dp2reg_d0_set = reg2dp_d0_op_en & !reg2dp_d0_op_en_w
//     val dp2reg_d0_clr = !reg2dp_d0_op_en & reg2dp_d0_op_en_w
//     val dp2reg_d0_reg = reg2dp_d0_op_en ^ reg2dp_d0_op_en_w

//     val dp2reg_d1_set = reg2dp_d1_op_en & !reg2dp_d1_op_en_w
//     val dp2reg_d1_clr = !reg2dp_d1_op_en & reg2dp_d1_op_en_w
//     val dp2reg_d1_reg = reg2dp_d1_op_en ^ reg2dp_d1_op_en_w

//     //////// for overflow counting register ////////
//     val dp2reg_d0_lut_oflow_w = Mux(dp2reg_d0_set, io.dp2reg_lut_oflow, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_lut_oflow))
//     val dp2reg_d0_lut_uflow_w = Mux(dp2reg_d0_set, io.dp2reg_lut_uflow, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_lut_uflow))
//     val dp2reg_d0_wdma_stall_w = Mux(dp2reg_d0_set, io.dp2reg_wdma_stall, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_wdma_stall))
//     val dp2reg_d0_status_inf_input_num_w = Mux(dp2reg_d0_set, io.dp2reg_status_inf_input_num, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_status_inf_input_num))
//     val dp2reg_d0_out_saturation_w = Mux(dp2reg_d0_set, io.dp2reg_out_saturation, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_out_saturation))
//     val dp2reg_d0_status_nan_output_num_w = Mux(dp2reg_d0_set, io.dp2reg_status_nan_output_num, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_status_nan_output_num))
//     val dp2reg_d0_lut_le_hit_w = Mux(dp2reg_d0_set, io.dp2reg_lut_le_hit, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_lut_le_hit))
//     val dp2reg_d0_status_nan_input_num_w = Mux(dp2reg_d0_set, io.dp2reg_status_nan_input_num, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_status_nan_input_num))
//     val dp2reg_d0_status_unequal_w = Mux(dp2reg_d0_set, io.dp2reg_status_unequal, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_status_unequal))
//     val dp2reg_d0_lut_hybrid_w = Mux(dp2reg_d0_set, io.dp2reg_lut_hybrid, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_lut_hybrid))
//     val dp2reg_d0_lut_lo_hit_w = Mux(dp2reg_d0_set, io.dp2reg_lut_lo_hit, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_lut_lo_hit))

//     when(dp2reg_d0_reg){
//         dp2reg_d0_lut_oflow := dp2reg_d0_lut_oflow_w
//         dp2reg_d0_lut_uflow := dp2reg_d0_lut_uflow_w
//         dp2reg_d0_wdma_stall := dp2reg_d0_wdma_stall_w
//         dp2reg_d0_status_inf_input_num := dp2reg_d0_status_inf_input_num_w
//         dp2reg_d0_out_saturation := dp2reg_d0_out_saturation_w
//         dp2reg_d0_status_nan_output_num := dp2reg_d0_status_nan_output_num_w
//         dp2reg_d0_lut_le_hit := dp2reg_d0_lut_le_hit_w
//         dp2reg_d0_status_nan_input_num := dp2reg_d0_status_nan_input_num_w
//         dp2reg_d0_status_unequal := dp2reg_d0_status_unequal_w
//         dp2reg_d0_lut_hybrid := dp2reg_d0_lut_hybrid_w
//         dp2reg_d0_lut_lo_hit := dp2reg_d0_lut_lo_hit_w
        
//     }

//     val dp2reg_d1_lut_oflow_w = Mux(dp2reg_d1_set, io.dp2reg_lut_oflow, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_lut_oflow))
//     val dp2reg_d1_lut_uflow_w = Mux(dp2reg_d1_set, io.dp2reg_lut_uflow, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_lut_uflow)) 
//     val dp2reg_d1_wdma_stall_w = Mux(dp2reg_d1_set, io.dp2reg_wdma_stall, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_wdma_stall))   
//     val dp2reg_d1_status_inf_input_num_w = Mux(dp2reg_d1_set, io.dp2reg_status_inf_input_num, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_status_inf_input_num))
//     val dp2reg_d1_out_saturation_w = Mux(dp2reg_d1_set, io.dp2reg_out_saturation, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_out_saturation))
//     val dp2reg_d1_status_nan_output_num_w = Mux(dp2reg_d1_set, io.dp2reg_status_nan_output_num, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_status_nan_output_num))
//     val dp2reg_d1_lut_le_hit_w = Mux(dp2reg_d1_set, io.dp2reg_lut_le_hit, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_lut_le_hit))
//     val dp2reg_d1_status_nan_input_num_w = Mux(dp2reg_d1_set, io.dp2reg_status_nan_input_num, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_status_nan_input_num))
//     val dp2reg_d1_status_unequal_w = Mux(dp2reg_d1_set, io.dp2reg_status_unequal, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_status_unequal))
//     val dp2reg_d1_lut_hybrid_w = Mux(dp2reg_d1_set, io.dp2reg_lut_hybrid, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_lut_hybrid))
//     val dp2reg_d1_lut_lo_hit_w = Mux(dp2reg_d1_set, io.dp2reg_lut_lo_hit, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_lut_lo_hit))
//     when(dp2reg_d1_reg){
//         dp2reg_d1_lut_oflow := dp2reg_d1_lut_oflow_w
//         dp2reg_d1_lut_uflow := dp2reg_d1_lut_uflow_w
//         dp2reg_d1_wdma_stall := dp2reg_d1_wdma_stall_w
//         dp2reg_d1_status_inf_input_num := dp2reg_d1_status_inf_input_num_w
//         dp2reg_d1_out_saturation := dp2reg_d1_out_saturation_w
//         dp2reg_d1_status_nan_output_num := dp2reg_d1_status_nan_output_num_w
//         dp2reg_d1_lut_le_hit := dp2reg_d1_lut_le_hit_w
//         dp2reg_d1_status_nan_input_num := dp2reg_d1_status_nan_input_num_w
//         dp2reg_d1_status_unequal := dp2reg_d1_status_unequal_w
//         dp2reg_d1_lut_hybrid := dp2reg_d1_lut_hybrid_w
//         dp2reg_d1_lut_lo_hit := dp2reg_d1_lut_lo_hit_w
//     }


// }}


// object NV_NVDLA_SDP_regDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_reg())
// }