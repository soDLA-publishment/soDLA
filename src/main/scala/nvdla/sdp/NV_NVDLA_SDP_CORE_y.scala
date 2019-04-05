// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_CORE_y extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Bool())
//         val nvdla_core_rstn = Input(Bool())
// modify yourself
//         val ew_alu_in_vld = Input(Bool())
//         val ew_alu_in_rdy = Output(Bool())
// modify yourself
//         val ew_data_in_pvld = Input(Bool())
//         val ew_data_in_prdy = Output(Bool())
// modify yourself
//         val ew_mul_in_vld = Input(Bool())
//         val ew_mul_in_rdy = Output(Bool())
// modify yourself
//         val ew_data_out_pvld = Output(Bool())
//         val ew_data_out_prdy = Input(Bool())
//         val reg2dp_ew_alu_algo = Input(UInt(2.W))
//         val reg2dp_ew_alu_bypass = Input(Bool())
//         val reg2dp_ew_alu_cvt_bypass = Input(Bool())
//         val reg2dp_ew_alu_cvt_offset = Input(UInt(32.W))
//         val reg2dp_ew_alu_cvt_scale = Input(UInt(16.W))
//         val reg2dp_ew_alu_cvt_truncate = Input(UInt(6.W))
//         val reg2dp_ew_alu_operand = Input(UInt(32.W))
//         val reg2dp_ew_alu_src = Input(Bool())
//         val reg2dp_ew_lut_bypass = Input(Bool())
//         val reg2dp_ew_mul_bypass = Input(Bool())
//         val reg2dp_ew_mul_cvt_bypass = Input(Bool())
//         val reg2dp_ew_mul_cvt_offset = Input(UInt(32.W))
//         val reg2dp_ew_mul_cvt_scale = Input(UInt(16.W))
//         val reg2dp_ew_mul_cvt_truncate = Input(UInt(6.W))
//         val reg2dp_ew_mul_operand = Input(UInt(32.W))
//         val reg2dp_ew_mul_prelu = Input(Bool())
//         val reg2dp_ew_mul_src = Input(Bool())
//         val reg2dp_ew_truncate = Input(UInt(10.W))
// modify yourself
//         val reg2dp_lut_hybrid_priority = Input(Bool())
//         val reg2dp_lut_int_access_type = Input(Bool())
//         val reg2dp_lut_int_addr = Input(UInt(10.W))
//         val reg2dp_lut_int_data = Input(UInt(16.W))
//         val reg2dp_lut_int_data_wr = Input(Bool())
//         val reg2dp_lut_int_table_id = Input(Bool())
//         val reg2dp_lut_le_end = Input(UInt(32.W))
//         val reg2dp_lut_le_function = Input(Bool())
//         val reg2dp_lut_le_index_offset = Input(UInt(8.W))
//         val reg2dp_lut_le_index_select = Input(UInt(8.W))
//         val reg2dp_lut_le_slope_oflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_le_slope_oflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_le_slope_uflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_le_slope_uflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_le_start = Input(UInt(32.W))
//         val reg2dp_lut_lo_end = Input(UInt(32.W))
//         val reg2dp_lut_lo_index_select = Input(UInt(8.W))
//         val reg2dp_lut_lo_slope_oflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_lo_slope_oflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_lo_slope_uflow_scale = Input(UInt(16.W))
//         val reg2dp_lut_lo_slope_uflow_shift = Input(UInt(5.W))
//         val reg2dp_lut_lo_start = Input(UInt(32.W))
//         val reg2dp_lut_oflow_priority = Input(Bool())
//         val reg2dp_lut_uflow_priority = Input(Bool())
//         val dp2reg_lut_hybrid = Output(UInt(32.W))
//         val dp2reg_lut_int_data = Output(UInt(16.W))
//         val dp2reg_lut_le_hit = Output(UInt(32.W))
//         val dp2reg_lut_lo_hit = Output(UInt(32.W))
//         val dp2reg_lut_oflow = Output(UInt(32.W))
//         val dp2reg_lut_uflow = Output(UInt(32.W))
//     })
//     val nvdla_core_clk_slcg_0_en = io.bcore_slcg_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
//                                    (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)
//     val nvdla_core_clk_slcg_1_en = io.ecore_slcg_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
//                                    (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)
//     val nvdla_core_clk_slcg_2_en = io.ncore_slcg_en | io.dla_clk_ovr_on_sync.asUInt.toBool |
//                                    (io.tmc2slcg_disable_clock_gating|io.global_clk_ovr_on_sync.asUInt.toBool)                   

//     val nvdla_core_clk_slcg_0 = Module(new NV_CLK_gate_power)
//     nvdla_core_clk_slcg_0.io.clk := io.nvdla_core_clk
//     nvdla_core_clk_slcg_0.io.clk_en := nvdla_core_clk_slcg_0_en
//     io.nvdla_gated_bcore_clk := nvdla_core_clk_slcg_0.io.clk_gated 

//     val nvdla_core_clk_slcg_1 = Module(new NV_CLK_gate_power)
//     nvdla_core_clk_slcg_1.io.clk := io.nvdla_core_clk
//     nvdla_core_clk_slcg_1.io.clk_en := nvdla_core_clk_slcg_1_en
//     io.nvdla_gated_ecore_clk := nvdla_core_clk_slcg_1.io.clk_gated 

//     val nvdla_core_clk_slcg_2 = Module(new NV_CLK_gate_power)
//     nvdla_core_clk_slcg_2.io.clk := io.nvdla_core_clk
//     nvdla_core_clk_slcg_2.io.clk_en := nvdla_core_clk_slcg_2_en
//     io.nvdla_gated_ncore_clk := nvdla_core_clk_slcg_2.io.clk_gated                   

// }

