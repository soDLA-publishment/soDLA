package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_REG_single extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        //Register control interface
        val reg = new reg_control_if

// class reg_control_if extends Bundle{
//     val rd_data = Output(UInt(32.W))
//     val offset = Input(UInt(12.W))
//     val wr_data = Input(UInt(32.W))
//     val wr_en = Input(Bool())
// }

        //Writable register flop/trigger outputs
        val field = new cdp_reg_single_flop_outputs
        val lut_addr_trigger = Output(Bool())
        val lut_data_trigger = Output(Bool())

        //Read-only register inputs
        val lut_addr = Input(UInt(9.W))
        val lut_data = Input(UInt(16.W))
        val consumer = Input(Bool())
        val status_0 = Input(UInt(2.W))
        val status_1 = Input(UInt(2.W))
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
    val nvdla_cdp_s_lut_access_cfg_0_wren = (io.reg.offset === "h08".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_access_data_0_wren = (io.reg.offset === "h0c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_cfg_0_wren = (io.reg.offset === "h10".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_info_0_wren = (io.reg.offset === "h14".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_le_end_high_0_wren = (io.reg.offset === "h24".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_le_end_low_0_wren = (io.reg.offset === "h20".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_le_slope_scale_0_wren = (io.reg.offset === "h38".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_le_slope_shift_0_wren = (io.reg.offset === "h3c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_le_start_high_0_wren = (io.reg.offset === "h1c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_le_start_low_0_wren = (io.reg.offset === "h18".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_lo_end_high_0_wren = (io.reg.offset === "h34".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_lo_end_low_0_wren = (io.reg.offset === "h30".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_lo_slope_scale_0_wren = (io.reg.offset === "h40".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_lo_slope_shift_0_wren = (io.reg.offset === "h44".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_lo_start_high_0_wren = (io.reg.offset === "h2c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_lut_lo_start_low_0_wren = (io.reg.offset === "h28".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_pointer_0_wren = (io.reg.offset === "h04".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_s_status_0_wren = (io.reg.offset === "h00".asUInt(32.W)) & io.reg.wr_en 

    io.lut_addr_trigger := nvdla_cdp_s_lut_access_cfg_0_wren
    io.lut_data_trigger := nvdla_cdp_s_lut_access_data_0_wren

    //Output mux

    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(      
    //nvdla_cdp_s_lut_access_cfg_0_out
    "h08".asUInt(32.W)  -> Cat("b0".asUInt(14.W), io.field.lut_access_type, io.field.lut_table_id, "b0".asUInt(6.W), io.lut_addr),
    //nvdla_cdp_s_lut_access_data_0_out
    "h0c".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.lut_data),
    //nvdla_cdp_s_lut_cfg_0_out
    "h10".asUInt(32.W)  -> Cat("b0".asUInt(25.W), io.field.lut_hybrid_priority, io.field.lut_oflow_priority, io.field.lut_uflow_priority, "b0".asUInt(3.W), io.field.lut_le_function),
    //nvdla_cdp_s_lut_info_0_out
    "h14".asUInt(32.W)  -> Cat("b0".asUInt(8.W), io.field.lut_lo_index_select, io.field.lut_le_index_select, io.field.lut_le_index_offset),
    //nvdla_cdp_s_lut_le_end_high_0_out
    "h24".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.field.lut_le_end_high),
    //nvdla_cdp_s_lut_le_end_low_0_out
    "h20".asUInt(32.W)  -> io.field.lut_le_end_low,
    //nvdla_cdp_s_lut_le_slope_scale_0_out
    "h38".asUInt(32.W)  -> Cat(io.field.lut_le_slope_oflow_scale, io.field.lut_le_slope_uflow_scale),
    //nvdla_cdp_s_lut_le_slope_shift_0_out
    "h3c".asUInt(32.W)  -> Cat("b0".asUInt(22.W), io.field.lut_le_slope_oflow_shift, io.field.lut_le_slope_uflow_shift),
    //nvdla_cdp_s_lut_le_start_high_0_out
    "h1c".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.field.lut_le_start_high),
    //nvdla_cdp_s_lut_le_start_low_0_out
    "h18".asUInt(32.W)  -> io.field.lut_le_start_low,
    //nvdla_cdp_s_lut_lo_end_high_0_out
    "h34".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.field.lut_lo_end_high),
    //nvdla_cdp_s_lut_lo_end_low_0_out
    "h30".asUInt(32.W)  -> io.field.lut_lo_end_low,
    //nvdla_cdp_s_lut_lo_slope_scale_0_out
    "h40".asUInt(32.W)  -> Cat(io.field.lut_lo_slope_oflow_scale, io.field.lut_lo_slope_uflow_scale),
    //nvdla_cdp_s_lut_lo_slope_shift_0_out
    "h44".asUInt(32.W)  -> Cat("b0".asUInt(22.W), io.field.lut_lo_slope_oflow_shift, io.field.lut_lo_slope_uflow_shift),
    //nvdla_cdp_s_lut_lo_start_high_0_out
    "h2c".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.field.lut_lo_start_high),
    //nvdla_cdp_s_lut_lo_start_low_0_out
    "h28".asUInt(32.W)  -> io.field.lut_lo_start_low,
    //nvdla_cdp_s_pointer_0_out
    "h04".asUInt(32.W)  -> Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.field.producer),
    //nvdla_cdp_s_status_0_out
    "h00".asUInt(32.W)  -> Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0),
   ))

    //Register flop declarations       

  // Register: NVDLA_CDP_S_LUT_ACCESS_CFG_0    Field: lut_access_type
    io.field.lut_access_type := RegEnable(io.reg.wr_data(17), false.B, nvdla_cdp_s_lut_access_cfg_0_wren)
  // Not generating flops for field NVDLA_CDP_S_LUT_ACCESS_CFG_0::lut_addr (to be implemented outside)
  // Register: NVDLA_CDP_S_LUT_ACCESS_CFG_0    Field: lut_table_id
    io.field.lut_table_id := RegEnable(io.reg.wr_data(16), false.B, nvdla_cdp_s_lut_access_cfg_0_wren)
  // Not generating flops for field NVDLA_CDP_S_LUT_ACCESS_DATA_0::lut_data (to be implemented outside)
  // Register: NVDLA_CDP_S_LUT_CFG_0    Field: lut_hybrid_priority
    io.field.lut_hybrid_priority := RegEnable(io.reg.wr_data(6), false.B, nvdla_cdp_s_lut_cfg_0_wren)
  // Register: NVDLA_CDP_S_LUT_CFG_0    Field: lut_le_function
    io.field.lut_le_function := RegEnable(io.reg.wr_data(0), false.B, nvdla_cdp_s_lut_cfg_0_wren)
  // Register: NVDLA_CDP_S_LUT_CFG_0    Field: lut_oflow_priority
    io.field.lut_oflow_priority := RegEnable(io.reg.wr_data(5), false.B, nvdla_cdp_s_lut_cfg_0_wren)
  // Register: NVDLA_CDP_S_LUT_CFG_0    Field: lut_uflow_priority
    io.field.lut_uflow_priority := RegEnable(io.reg.wr_data(4), false.B, nvdla_cdp_s_lut_cfg_0_wren)
  // Register: NVDLA_CDP_S_LUT_INFO_0    Field: lut_le_index_offset
    io.field.lut_le_index_offset := RegEnable(io.reg.wr_data(7, 0), "b0".asUInt(8.W), nvdla_cdp_s_lut_info_0_wren)
  // Register: NVDLA_CDP_S_LUT_INFO_0    Field: lut_le_index_select
    io.field.lut_le_index_select := RegEnable(io.reg.wr_data(15, 8), "b0".asUInt(8.W), nvdla_cdp_s_lut_info_0_wren)
  // Register: NVDLA_CDP_S_LUT_INFO_0    Field: lut_lo_index_select
    io.field.lut_lo_index_select := RegEnable(io.reg.wr_data(23, 16), "b0".asUInt(8.W), nvdla_cdp_s_lut_info_0_wren)
  // Register: NVDLA_CDP_S_LUT_LE_END_HIGH_0    Field: lut_le_end_high
    io.field.lut_le_end_high := RegEnable(io.reg.wr_data(5, 0), "b0".asUInt(6.W), nvdla_cdp_s_lut_le_end_high_0_wren)
  // Register: NVDLA_CDP_S_LUT_LE_END_LOW_0    Field: lut_le_end_low
    io.field.lut_le_end_low := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_s_lut_le_end_low_0_wren)
  // Register: NVDLA_CDP_S_LUT_LE_SLOPE_SCALE_0    Field: lut_le_slope_oflow_scale
    io.field.lut_le_slope_oflow_scale := RegEnable(io.reg.wr_data(31, 16), "b0".asUInt(16.W), nvdla_cdp_s_lut_le_slope_scale_0_wren)
  // Register: NVDLA_CDP_S_LUT_LE_SLOPE_SCALE_0    Field: lut_le_slope_uflow_scale
    io.field.lut_le_slope_uflow_scale := RegEnable(io.reg.wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdp_s_lut_le_slope_scale_0_wren)
  // Register: NVDLA_CDP_S_LUT_LE_SLOPE_SHIFT_0    Field: lut_le_slope_oflow_shift
    io.field.lut_le_slope_oflow_shift := RegEnable(io.reg.wr_data(9, 5), "b0".asUInt(5.W), nvdla_cdp_s_lut_le_slope_shift_0_wren)
  // Register: NVDLA_CDP_S_LUT_LE_SLOPE_SHIFT_0    Field: lut_le_slope_uflow_shift
    io.field.lut_le_slope_uflow_shift := RegEnable(io.reg.wr_data(4, 0), "b0".asUInt(5.W), nvdla_cdp_s_lut_le_slope_shift_0_wren)
  // Register: NVDLA_CDP_S_LUT_LE_START_HIGH_0    Field: lut_le_start_high
    io.field.lut_le_start_high := RegEnable(io.reg.wr_data(5, 0), "b0".asUInt(6.W), nvdla_cdp_s_lut_le_start_high_0_wren)
  // Register: NVDLA_CDP_S_LUT_LE_START_LOW_0    Field: lut_le_start_low
    io.field.lut_le_start_low := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_s_lut_le_start_low_0_wren)
  // Register: NVDLA_CDP_S_LUT_LO_END_HIGH_0    Field: lut_lo_end_high
    io.field.lut_lo_end_high := RegEnable(io.reg.wr_data(5, 0), "b0".asUInt(6.W), nvdla_cdp_s_lut_lo_end_high_0_wren)
  // Register: NVDLA_CDP_S_LUT_LO_END_LOW_0    Field: lut_lo_end_low
    io.field.lut_lo_end_low := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_s_lut_lo_end_low_0_wren)
  // Register: NVDLA_CDP_S_LUT_LO_SLOPE_SCALE_0    Field: lut_lo_slope_oflow_scale
    io.field.lut_lo_slope_oflow_scale := RegEnable(io.reg.wr_data(31, 16), "b0".asUInt(16.W), nvdla_cdp_s_lut_lo_slope_scale_0_wren)
  // Register: NVDLA_CDP_S_LUT_LO_SLOPE_SCALE_0    Field: lut_lo_slope_uflow_scale
    io.field.lut_lo_slope_uflow_scale := RegEnable(io.reg.wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdp_s_lut_lo_slope_scale_0_wren)
  // Register: NVDLA_CDP_S_LUT_LO_SLOPE_SHIFT_0    Field: lut_lo_slope_oflow_shift
    io.field.lut_lo_slope_oflow_shift := RegEnable(io.reg.wr_data(9, 5), "b0".asUInt(5.W), nvdla_cdp_s_lut_lo_slope_shift_0_wren)
  // Register: NVDLA_CDP_S_LUT_LO_SLOPE_SHIFT_0    Field: lut_lo_slope_uflow_shift
    io.field.lut_lo_slope_uflow_shift := RegEnable(io.reg.wr_data(4, 0), "b0".asUInt(5.W), nvdla_cdp_s_lut_lo_slope_shift_0_wren)
  // Register: NVDLA_CDP_S_LUT_LO_START_HIGH_0    Field: lut_lo_start_high
    io.field.lut_lo_start_high := RegEnable(io.reg.wr_data(5, 0), "b0".asUInt(6.W), nvdla_cdp_s_lut_lo_start_high_0_wren)
  // Register: NVDLA_CDP_S_LUT_LO_START_LOW_0    Field: lut_lo_start_low
    io.field.lut_lo_start_low := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_s_lut_lo_start_low_0_wren)
  // Not generating flops for read-only field NVDLA_CDP_S_POINTER_0::consumer
  // Register: NVDLA_CDP_S_POINTER_0    Field: producer
    io.field.producer := RegEnable(io.reg.wr_data(0), false.B, nvdla_cdp_s_pointer_0_wren)
  // Not generating flops for read-only field NVDLA_CDP_S_STATUS_0::status_0
  // Not generating flops for read-only field NVDLA_CDP_S_STATUS_0::status_1
}}

