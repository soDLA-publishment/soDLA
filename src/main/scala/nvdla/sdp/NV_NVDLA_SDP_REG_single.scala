package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_REG_single extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg = new reg_control_if

        //Writable register flop/trigger outputs
        val lut_access_type = Output(Bool())      
        val lut_addr = Output(UInt(10.W))
        val lut_addr_trigger = Output(Bool())
        val lut_table_id = Output(Bool())
        val lut_data_trigger = Output(Bool())
        val lut_hybrid_priority = Output(Bool())
        val lut_le_function = Output(Bool())
        val lut_oflow_priority = Output(Bool())
        val lut_uflow_priority = Output(Bool())
        val lut_le_index_offset = Output(UInt(8.W))
        val lut_le_index_select = Output(UInt(8.W))
        val lut_lo_index_select = Output(UInt(8.W))
        val lut_le_end = Output(UInt(32.W))
        val lut_le_slope_oflow_scale = Output(UInt(16.W))
        val lut_le_slope_uflow_scale = Output(UInt(16.W))
        val lut_le_slope_oflow_shift = Output(UInt(5.W))
        val lut_le_slope_uflow_shift = Output(UInt(5.W))
        val lut_le_start = Output(UInt(32.W))
        val lut_lo_end = Output(UInt(32.W))
        val lut_lo_slope_oflow_scale = Output(UInt(16.W))
        val lut_lo_slope_uflow_scale = Output(UInt(16.W))
        val lut_lo_slope_oflow_shift = Output(UInt(5.W))
        val lut_lo_slope_uflow_shift = Output(UInt(5.W))
        val lut_lo_start = Output(UInt(32.W))
        val producer = Output(Bool())

        //Read-only register inputs
        val consumer = Input(Bool())
        val lut_data = Input(UInt(16.W))
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
    val nvdla_sdp_s_lut_access_cfg_0_wren = (io.reg.offset === "h08".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_access_data_0_wren = (io.reg.offset ===  "h0c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_cfg_0_wren = (io.reg.offset ===  "h10".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_info_0_wren = (io.reg.offset ===  "h14".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_le_end_0_wren = (io.reg.offset === "h1c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_le_slope_scale_0_wren = (io.reg.offset === "h28".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_le_slope_shift_0_wren = (io.reg.offset === "h2c".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_le_start_0_wren = (io.reg.offset === "h18".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_lo_end_0_wren = (io.reg.offset === "h24".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_lo_slope_scale_0_wren = (io.reg.offset === "h30".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_lo_slope_shift_0_wren = (io.reg.offset === "h34".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_lut_lo_start_0_wren = (io.reg.offset === "h20".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_pointer_0_wren = (io.reg.offset === "h04".asUInt(32.W)) & io.reg.wr_en
    val nvdla_sdp_s_status_0_wren = (io.reg.offset === "h00".asUInt(32.W)) & io.reg.wr_en

    io.lut_addr_trigger := nvdla_sdp_s_lut_access_cfg_0_wren
    io.lut_data_trigger := nvdla_sdp_s_lut_access_data_0_wren

    //Output mux

    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(      
    //nvdla_sdp_s_lut_access_cfg_0_out
    "h08".asUInt(32.W)  -> Cat("b0".asUInt(14.W), io.lut_access_type, io.lut_table_id, "b0".asUInt(6.W), io.lut_addr),
    //nvdla_sdp_s_lut_access_data_0_out
    "h0c".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.lut_data),
    //nvdla_sdp_s_lut_cfg_0_out
    "h10".asUInt(32.W)  -> Cat("b0".asUInt(25.W), io.lut_hybrid_priority, io.lut_oflow_priority, io.lut_uflow_priority, "b0".asUInt(3.W), io.lut_le_function),
    //nvdla_sdp_s_lut_info_0_out
    "h14".asUInt(32.W)  -> Cat("b0".asUInt(8.W), io.lut_lo_index_select, io.lut_le_index_select, io.lut_le_index_offset),
    //nvdla_sdp_s_lut_le_end_0_out
    "h1c".asUInt(32.W)  -> io.lut_le_end,
    //nvdla_sdp_s_lut_le_slope_scale_0_out
    "h28".asUInt(32.W)  -> Cat(io.lut_le_slope_oflow_scale, io.lut_le_slope_uflow_scale),
    //nvdla_sdp_s_lut_le_slope_shift_0_out
    "h2c".asUInt(32.W)  -> Cat("b0".asUInt(22.W), io.lut_le_slope_oflow_shift, io.lut_le_slope_uflow_shift),
    //nvdla_sdp_s_lut_le_start_0_out
    "h18".asUInt(32.W)  -> io.lut_le_start,
    //nvdla_sdp_s_lut_lo_end_0_out
    "h24".asUInt(32.W)  -> io.lut_lo_end,
    //nvdla_sdp_s_lut_lo_slope_scale_0_out
    "h30".asUInt(32.W)  -> Cat(io.lut_lo_slope_oflow_scale, io.lut_lo_slope_uflow_scale),
    //nvdla_sdp_s_lut_lo_slope_shift_0_out
    "h34".asUInt(32.W)  -> Cat("b0".asUInt(22.W), io.lut_lo_slope_oflow_shift, io.lut_lo_slope_uflow_shift),
    //nvdla_sdp_s_lut_lo_start_0_out
    "h20".asUInt(32.W)  -> io.lut_lo_start,
    //nvdla_sdp_s_pointer_0_out
    "h04".asUInt(32.W)  -> Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer), 
    //nvdla_sdp_s_status_0_out
    "h00".asUInt(32.W)  -> Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)
    ))

  //yifengdu y.f.du1994@gmail.com update on Aug 1, 2019 
  //Solve Java heap space problem
  
  // Register: NVDLA_SDP_S_LUT_ACCESS_CFG_0    Field: lut_access_type
    io.lut_access_type := RegEnable(io.reg.wr_data(17), false.B, nvdla_sdp_s_lut_access_cfg_0_wren)
  // Register: NVDLA_SDP_S_LUT_ACCESS_CFG_0    Field: lut_addr
    io.lut_addr := RegEnable(io.reg.wr_data(9,0), "b0".asUInt(10.W), nvdla_sdp_s_lut_access_cfg_0_wren)
  // Register: NVDLA_SDP_S_LUT_ACCESS_CFG_0    Field: lut_table_id
    io.lut_table_id := RegEnable(io.reg.wr_data(16), false.B, nvdla_sdp_s_lut_access_cfg_0_wren)
  // Register: NVDLA_SDP_S_LUT_CFG_0    Field: lut_hybrid_priority
    io.lut_hybrid_priority := RegEnable(io.reg.wr_data(6), false.B, nvdla_sdp_s_lut_cfg_0_wren)
  // Register: NVDLA_SDP_S_LUT_CFG_0    Field: lut_le_function
    io.lut_le_function := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_s_lut_cfg_0_wren)
  // Register: NVDLA_SDP_S_LUT_CFG_0    Field: lut_oflow_priority
    io.lut_oflow_priority := RegEnable(io.reg.wr_data(5), false.B, nvdla_sdp_s_lut_cfg_0_wren)
  // Register: NVDLA_SDP_S_LUT_CFG_0    Field: lut_uflow_priority
    io.lut_uflow_priority := RegEnable(io.reg.wr_data(4), false.B, nvdla_sdp_s_lut_cfg_0_wren)
  // Register: NVDLA_SDP_S_LUT_INFO_0    Field: lut_le_index_offset
    io.lut_le_index_offset := RegEnable(io.reg.wr_data(7,0), "b0".asUInt(8.W), nvdla_sdp_s_lut_info_0_wren)
  // Register: NVDLA_SDP_S_LUT_INFO_0    Field: lut_le_index_select
    io.lut_le_index_select := RegEnable(io.reg.wr_data(15,8), "b0".asUInt(8.W), nvdla_sdp_s_lut_info_0_wren)
  // Register: NVDLA_SDP_S_LUT_INFO_0    Field: lut_lo_index_select
    io.lut_lo_index_select := RegEnable(io.reg.wr_data(23,16), "b0".asUInt(8.W), nvdla_sdp_s_lut_info_0_wren)
  // Register: NVDLA_SDP_S_LUT_LE_END_0    Field: lut_le_end
    io.lut_le_end := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_s_lut_le_end_0_wren)
  // Register: NVDLA_SDP_S_LUT_LE_SLOPE_SCALE_0    Field: lut_le_slope_oflow_scale
    io.lut_le_slope_oflow_scale := RegEnable(io.reg.wr_data(31,16), "b0".asUInt(16.W), nvdla_sdp_s_lut_le_slope_scale_0_wren)
  // Register: NVDLA_SDP_S_LUT_LE_SLOPE_SCALE_0    Field: lut_le_slope_uflow_scale
    io.lut_le_slope_uflow_scale := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_s_lut_le_slope_scale_0_wren)
  // Register: NVDLA_SDP_S_LUT_LE_SLOPE_SHIFT_0    Field: lut_le_slope_oflow_shift
    io.lut_le_slope_oflow_shift := RegEnable(io.reg.wr_data(9,5), "b0".asUInt(5.W), nvdla_sdp_s_lut_le_slope_shift_0_wren)
  // Register: NVDLA_SDP_S_LUT_LE_SLOPE_SHIFT_0    Field: lut_le_slope_uflow_shift
    io.lut_le_slope_uflow_shift := RegEnable(io.reg.wr_data(4,0), "b0".asUInt(5.W), nvdla_sdp_s_lut_le_slope_shift_0_wren)
  // Register: NVDLA_SDP_S_LUT_LE_START_0    Field: lut_le_start
    io.lut_le_start := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_s_lut_le_start_0_wren)
  // Register: NVDLA_SDP_S_LUT_LO_END_0    Field: lut_lo_end
    io.lut_lo_end := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_s_lut_lo_end_0_wren)
  // Register: NVDLA_SDP_S_LUT_LO_SLOPE_SCALE_0    Field: lut_lo_slope_oflow_scale
    io.lut_lo_slope_oflow_scale := RegEnable(io.reg.wr_data(31,16), "b0".asUInt(16.W), nvdla_sdp_s_lut_lo_slope_scale_0_wren)
  // Register: NVDLA_SDP_S_LUT_LO_SLOPE_SCALE_0    Field: lut_lo_slope_uflow_scale
    io.lut_lo_slope_uflow_scale := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_s_lut_lo_slope_scale_0_wren)
  // Register: NVDLA_SDP_S_LUT_LO_SLOPE_SHIFT_0    Field: lut_lo_slope_oflow_shift
    io.lut_lo_slope_oflow_shift := RegEnable(io.reg.wr_data(9,5), "b0".asUInt(5.W), nvdla_sdp_s_lut_lo_slope_shift_0_wren)
  // Register: NVDLA_SDP_S_LUT_LO_SLOPE_SHIFT_0    Field: lut_lo_slope_uflow_shift
    io.lut_lo_slope_uflow_shift := RegEnable(io.reg.wr_data(4,0), "b0".asUInt(5.W), nvdla_sdp_s_lut_lo_slope_shift_0_wren)
  // Register: NVDLA_SDP_S_LUT_LO_START_0    Field: lut_lo_start
    io.lut_lo_start := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_s_lut_lo_start_0_wren)
  // Register: NVDLA_SDP_S_POINTER_0    Field: producer
    io.producer := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_s_pointer_0_wren)

}}

object NV_NVDLA_SDP_REG_singleDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_REG_single())
}

