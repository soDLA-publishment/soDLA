package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_REG_single extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        //Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)
        val reg_wr_en = Input(Bool())

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
    val nvdla_sdp_s_lut_access_cfg_0_wren = (io.reg_offset === "h08".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_access_data_0_wren = (io.reg_offset ===  "h0c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_cfg_0_wren = (io.reg_offset ===  "h10".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_info_0_wren = (io.reg_offset ===  "h14".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_le_end_0_wren = (io.reg_offset === "h1c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_le_slope_scale_0_wren = (io.reg_offset === "h28".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_le_slope_shift_0_wren = (io.reg_offset === "h2c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_le_start_0_wren = (io.reg_offset === "h18".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_lo_end_0_wren = (io.reg_offset === "h24".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_lo_slope_scale_0_wren = (io.reg_offset === "h30".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_lo_slope_shift_0_wren = (io.reg_offset === "h34".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_lut_lo_start_0_wren = (io.reg_offset === "h20".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_pointer_0_wren = (io.reg_offset === "h04".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_s_status_0_wren = (io.reg_offset === "h00".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)

    val nvdla_sdp_s_lut_access_cfg_0_out = Cat("b0".asUInt(14.W), io.lut_access_type, io.lut_table_id, "b0".asUInt(6.W), io.lut_addr)
    val nvdla_sdp_s_lut_access_data_0_out = Cat("b0".asUInt(16.W), io.lut_data)
    val nvdla_sdp_s_lut_cfg_0_out = Cat("b0".asUInt(25.W), io.lut_hybrid_priority, io.lut_oflow_priority, io.lut_uflow_priority, "b0".asUInt(3.W), io.lut_le_function)
    val nvdla_sdp_s_lut_info_0_out = Cat("b0".asUInt(8.W), io.lut_lo_index_select, io.lut_le_index_select, io.lut_le_index_offset)
    val nvdla_sdp_s_lut_le_end_0_out = io.lut_le_end
    val nvdla_sdp_s_lut_le_slope_scale_0_out = Cat(io.lut_le_slope_oflow_scale, io.lut_le_slope_uflow_scale)
    val nvdla_sdp_s_lut_le_slope_shift_0_out = Cat("b0".asUInt(22.W), io.lut_le_slope_oflow_shift, io.lut_le_slope_uflow_shift)
    val nvdla_sdp_s_lut_le_start_0_out = io.lut_le_start
    val nvdla_sdp_s_lut_lo_end_0_out = io.lut_lo_end
    val nvdla_sdp_s_lut_lo_slope_scale_0_out = Cat(io.lut_lo_slope_oflow_scale, io.lut_lo_slope_uflow_scale)
    val nvdla_sdp_s_lut_lo_slope_shift_0_out = Cat("b0".asUInt(22.W), io.lut_lo_slope_oflow_shift, io.lut_lo_slope_uflow_shift)
    val nvdla_sdp_s_lut_lo_start_0_out = io.lut_lo_start
    val nvdla_sdp_s_pointer_0_out = Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)
    val nvdla_sdp_s_status_0_out = Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

    io.lut_addr_trigger := nvdla_sdp_s_lut_access_cfg_0_wren
    io.lut_data_trigger := nvdla_sdp_s_lut_access_data_0_wren

    //Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
    "h08".asUInt(32.W)  -> nvdla_sdp_s_lut_access_cfg_0_out,
    "h0c".asUInt(32.W)  -> nvdla_sdp_s_lut_access_data_0_out,
    "h10".asUInt(32.W)  -> nvdla_sdp_s_lut_cfg_0_out,
    "h14".asUInt(32.W)  -> nvdla_sdp_s_lut_info_0_out,
    "h1c".asUInt(32.W)  -> nvdla_sdp_s_lut_le_end_0_out,
    "h28".asUInt(32.W)  -> nvdla_sdp_s_lut_le_slope_scale_0_out,
    "h2c".asUInt(32.W)  -> nvdla_sdp_s_lut_le_slope_shift_0_out,
    "h18".asUInt(32.W)  -> nvdla_sdp_s_lut_le_start_0_out,
    "h24".asUInt(32.W)  -> nvdla_sdp_s_lut_lo_end_0_out,
    "h30".asUInt(32.W)  -> nvdla_sdp_s_lut_lo_slope_scale_0_out,
    "h34".asUInt(32.W)  -> nvdla_sdp_s_lut_lo_slope_shift_0_out,
    "h20".asUInt(32.W)  -> nvdla_sdp_s_lut_lo_start_0_out,
    "h04".asUInt(32.W)  -> nvdla_sdp_s_pointer_0_out, 
    "h00".asUInt(32.W)  -> nvdla_sdp_s_status_0_out
    ))

    //Register flop declarations

    val lut_access_type_out = RegInit(false.B)
    val lut_addr_out = RegInit("b0".asUInt(10.W))
    val lut_table_id_out = RegInit(false.B)
    val lut_hybrid_priority_out = RegInit(false.B)
    val lut_le_function_out = RegInit(false.B)
    val lut_oflow_priority_out = RegInit(false.B)
    val lut_uflow_priority_out = RegInit(false.B)
    val lut_le_index_offset_out = RegInit("b0".asUInt(8.W))
    val lut_le_index_select_out = RegInit("b0".asUInt(8.W))
    val lut_lo_index_select_out = RegInit("b0".asUInt(8.W))
    val lut_le_end_out = RegInit("b0".asUInt(32.W))
    val lut_le_slope_oflow_scale_out = RegInit("b0".asUInt(16.W))
    val lut_le_slope_uflow_scale_out = RegInit("b0".asUInt(16.W))
    val lut_le_slope_oflow_shift_out = RegInit("b0".asUInt(5.W))
    val lut_le_slope_uflow_shift_out = RegInit("b0".asUInt(5.W))
    val lut_le_start_out = RegInit("b0".asUInt(32.W))
    val lut_lo_end_out = RegInit("b0".asUInt(32.W))
    val lut_lo_slope_oflow_scale_out = RegInit("b0".asUInt(16.W))
    val lut_lo_slope_uflow_scale_out = RegInit("b0".asUInt(16.W))
    val lut_lo_slope_oflow_shift_out = RegInit("b0".asUInt(5.W))
    val lut_lo_slope_uflow_shift_out = RegInit("b0".asUInt(5.W))
    val lut_lo_start_out = RegInit("b0".asUInt(32.W))    
    val producer_out = RegInit(false.B)

  // Register: NVDLA_SDP_S_LUT_ACCESS_CFG_0    Field: lut_access_type
    when(nvdla_sdp_s_lut_access_cfg_0_wren){
        lut_access_type_out := io.reg_wr_data(17)
    }

  // Register: NVDLA_SDP_S_LUT_ACCESS_CFG_0    Field: lut_addr
    when(nvdla_sdp_s_lut_access_cfg_0_wren){
        lut_addr_out := io.reg_wr_data(9,0)
    }

  // Register: NVDLA_SDP_S_LUT_ACCESS_CFG_0    Field: lut_table_id
    when(nvdla_sdp_s_lut_access_cfg_0_wren){
        lut_table_id_out := io.reg_wr_data(16)
    }

  // Not generating flops for field NVDLA_SDP_S_LUT_ACCESS_DATA_0::lut_data (to be implemented outside)

  // Register: NVDLA_SDP_S_LUT_CFG_0    Field: lut_hybrid_priority
    when(nvdla_sdp_s_lut_cfg_0_wren){
        lut_hybrid_priority_out := io.reg_wr_data(6)
    }

  // Register: NVDLA_SDP_S_LUT_CFG_0    Field: lut_le_function
    when(nvdla_sdp_s_lut_cfg_0_wren){
        lut_le_function_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_S_LUT_CFG_0    Field: lut_oflow_priority
    when(nvdla_sdp_s_lut_cfg_0_wren){
        lut_oflow_priority_out := io.reg_wr_data(5)
    }

  // Register: NVDLA_SDP_S_LUT_CFG_0    Field: lut_uflow_priority
    when(nvdla_sdp_s_lut_cfg_0_wren){
        lut_uflow_priority_out := io.reg_wr_data(4)
    }

  // Register: NVDLA_SDP_S_LUT_INFO_0    Field: lut_le_index_offset
    when(nvdla_sdp_s_lut_info_0_wren){
        lut_le_index_offset_out := io.reg_wr_data(7,0)
    }

  // Register: NVDLA_SDP_S_LUT_INFO_0    Field: lut_le_index_select
    when(nvdla_sdp_s_lut_info_0_wren){
        lut_le_index_select_out := io.reg_wr_data(15,8)
    }

  // Register: NVDLA_SDP_S_LUT_INFO_0    Field: lut_lo_index_select
    when(nvdla_sdp_s_lut_info_0_wren){
        lut_lo_index_select_out := io.reg_wr_data(23,16)
    }

  // Register: NVDLA_SDP_S_LUT_LE_END_0    Field: lut_le_end
    when(nvdla_sdp_s_lut_le_end_0_wren){
        lut_le_end_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_S_LUT_LE_SLOPE_SCALE_0    Field: lut_le_slope_oflow_scale
    when(nvdla_sdp_s_lut_le_slope_scale_0_wren){
        lut_le_slope_oflow_scale_out := io.reg_wr_data(31,16)
    }

  // Register: NVDLA_SDP_S_LUT_LE_SLOPE_SCALE_0    Field: lut_le_slope_uflow_scale
    when(nvdla_sdp_s_lut_le_slope_scale_0_wren){
        lut_le_slope_uflow_scale_out := io.reg_wr_data(15,0)
    }

  // Register: NVDLA_SDP_S_LUT_LE_SLOPE_SHIFT_0    Field: lut_le_slope_oflow_shift
    when(nvdla_sdp_s_lut_le_slope_shift_0_wren){
        lut_le_slope_oflow_shift_out := io.reg_wr_data(9,5)
    }

  // Register: NVDLA_SDP_S_LUT_LE_SLOPE_SHIFT_0    Field: lut_le_slope_uflow_shift
    when(nvdla_sdp_s_lut_le_slope_shift_0_wren){
        lut_le_slope_uflow_shift_out := io.reg_wr_data(4,0)
    }

  // Register: NVDLA_SDP_S_LUT_LE_START_0    Field: lut_le_start
    when(nvdla_sdp_s_lut_le_start_0_wren){
        lut_le_start_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_S_LUT_LO_END_0    Field: lut_lo_end
    when(nvdla_sdp_s_lut_lo_end_0_wren){
        lut_lo_end_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_S_LUT_LO_SLOPE_SCALE_0    Field: lut_lo_slope_oflow_scale
    when(nvdla_sdp_s_lut_lo_slope_scale_0_wren){
        lut_lo_slope_oflow_scale_out := io.reg_wr_data(31,16)
    }

  // Register: NVDLA_SDP_S_LUT_LO_SLOPE_SCALE_0    Field: lut_lo_slope_uflow_scale
    when(nvdla_sdp_s_lut_lo_slope_scale_0_wren){
        lut_lo_slope_uflow_scale_out := io.reg_wr_data(15,0)
    }

  // Register: NVDLA_SDP_S_LUT_LO_SLOPE_SHIFT_0    Field: lut_lo_slope_oflow_shift
    when(nvdla_sdp_s_lut_lo_slope_shift_0_wren){
        lut_lo_slope_oflow_shift_out := io.reg_wr_data(9,5)
    }

  // Register: NVDLA_SDP_S_LUT_LO_SLOPE_SHIFT_0    Field: lut_lo_slope_uflow_shift
    when(nvdla_sdp_s_lut_lo_slope_shift_0_wren){
        lut_lo_slope_uflow_shift_out := io.reg_wr_data(4,0)
    }

  // Register: NVDLA_SDP_S_LUT_LO_START_0    Field: lut_lo_start
    when(nvdla_sdp_s_lut_lo_start_0_wren){
        lut_lo_start_out := io.reg_wr_data
    }

  // Not generating flops for read-only field NVDLA_SDP_S_POINTER_0::consumer

  // Register: NVDLA_SDP_S_POINTER_0    Field: producer
    when(nvdla_sdp_s_pointer_0_wren){
        producer_out := io.reg_wr_data(0)
    }

  // Not generating flops for read-only field NVDLA_SDP_S_STATUS_0::status_0

  // Not generating flops for read-only field NVDLA_SDP_S_STATUS_0::status_1


    io.lut_access_type := lut_access_type_out
    io.lut_addr := lut_addr_out
    io.lut_table_id := lut_table_id_out
    io.lut_hybrid_priority := lut_hybrid_priority_out
    io.lut_le_function := lut_le_function_out
    io.lut_oflow_priority := lut_oflow_priority_out
    io.lut_uflow_priority := lut_uflow_priority_out
    io.lut_le_index_offset := lut_le_index_offset_out
    io.lut_le_index_select := lut_le_index_select_out
    io.lut_lo_index_select := lut_lo_index_select_out
    io.lut_le_end := lut_le_end_out
    io.lut_le_slope_oflow_scale := lut_le_slope_oflow_scale_out
    io.lut_le_slope_uflow_scale := lut_le_slope_uflow_scale_out
    io.lut_le_slope_oflow_shift := lut_le_slope_oflow_shift_out
    io.lut_le_slope_uflow_shift := lut_le_slope_uflow_shift_out
    io.lut_le_start := lut_le_start_out
    io.lut_lo_end := lut_lo_end_out
    io.lut_lo_slope_oflow_scale := lut_lo_slope_oflow_scale_out
    io.lut_lo_slope_uflow_scale := lut_lo_slope_uflow_scale_out
    io.lut_lo_slope_oflow_shift := lut_lo_slope_oflow_shift_out
    io.lut_lo_slope_uflow_shift := lut_lo_slope_uflow_shift_out
    io.lut_lo_start := lut_lo_start_out
    io.producer := producer_out

}}

object NV_NVDLA_SDP_REG_singleDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_REG_single())
}

