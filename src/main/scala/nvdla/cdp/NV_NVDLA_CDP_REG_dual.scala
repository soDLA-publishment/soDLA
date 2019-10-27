package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_REG_dual extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        //Register control interface
        val reg = new reg_control_if

        //Writable register flop/trigger outputs
        val field = new cdp_reg_dual_flop_outputs
        val op_en_trigger = Output(Bool())

        //Read-only register inputs
        val inf_input_num = Input(UInt(32.W))
        val nan_input_num = Input(UInt(32.W))
        val nan_output_num = Input(UInt(32.W))
        val op_en = Input(Bool())
        val out_saturation = Input(UInt(32.W))
        val perf_lut_hybrid = Input(UInt(32.W))
        val perf_lut_le_hit = Input(UInt(32.W))
        val perf_lut_lo_hit = Input(UInt(32.W)) 
        val perf_lut_oflow = Input(UInt(32.W)) 
        val perf_lut_uflow = Input(UInt(32.W)) 
        val perf_write_stall = Input(UInt(32.W)) 

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
    val nvdla_cdp_d_cya_0_wren = (io.reg.offset === "hb8".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_data_format_0_wren = (io.reg.offset === "h68".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_datin_offset_0_wren = (io.reg.offset === "h74".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_datin_scale_0_wren = (io.reg.offset === "h78".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_datin_shifter_0_wren = (io.reg.offset === "h7c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_datout_offset_0_wren = (io.reg.offset === "h80".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_datout_scale_0_wren = (io.reg.offset === "h84".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_datout_shifter_0_wren = (io.reg.offset === "h88".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_dst_base_addr_high_0_wren = (io.reg.offset === "h54".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_dst_base_addr_low_0_wren = (io.reg.offset === "h54".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_dst_compression_en_0_wren = (io.reg.offset === "h64".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_dst_dma_cfg_0_wren = (io.reg.offset === "h60".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_dst_line_stride_0_wren = (io.reg.offset === "h58".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_dst_surface_stride_0_wren = (io.reg.offset === "h5c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_func_bypass_0_wren = (io.reg.offset === "h4c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_inf_input_num_0_wren = (io.reg.offset === "h90".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_lrn_cfg_0_wren = (io.reg.offset === "h70".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_nan_flush_to_zero_0_wren = (io.reg.offset === "h6c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_nan_input_num_0_wren = (io.reg.offset === "h8c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_nan_output_num_0_wren = (io.reg.offset === "h94".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_op_enable_0_wren = (io.reg.offset === "h48".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_out_saturation_0_wren = (io.reg.offset === "h98".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_perf_enable_0_wren = (io.reg.offset === "h9c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_perf_lut_hybrid_0_wren = (io.reg.offset === "hac".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_perf_lut_le_hit_0_wren = (io.reg.offset === "hb0".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_perf_lut_lo_hit_0_wren = (io.reg.offset === "hb4".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_perf_lut_oflow_0_wren = (io.reg.offset === "ha8".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_perf_lut_uflow_0_wren = (io.reg.offset === "ha4".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_d_perf_write_stall_0_wren = (io.reg.offset === "ha0".asUInt(32.W)) & io.reg.wr_en 

    io.op_en_trigger := nvdla_cdp_d_op_enable_0_wren

    //Output mux
    val dst_compression_en = Wire(false.B)

    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(      
    //nvdla_cdp_d_cya_0_out
    "hb8".asUInt(32.W)  -> io.field.cya,
    //nvdla_cdp_d_data_format_0_out
    "h68".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.input_data_type),
    //nvdla_cdp_d_datin_offset_0_out
    "h74".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.datin_offset),
    //nvdla_cdp_d_datin_scale_0_out
    "h78".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.datin_scale),
    //nvdla_cdp_d_datin_shifter_0_out
    "h7c".asUInt(32.W)  -> Cat("b0".asUInt(27.W), io.field.datin_shifter),
    //nvdla_cdp_d_datout_offset_0_out
    "h80".asUInt(32.W)  -> io.field.datout_offset,
    //nvdla_cdp_d_datout_scale_0_out
    "h84".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.datout_scale),
    //nvdla_cdp_d_datout_shifter_0_out
    "h88".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.field.datout_shifter),
    //nvdla_cdp_d_dst_base_addr_high_0_out
    "h54".asUInt(32.W)  -> io.field.dst_base_addr_high,
    //nvdla_cdp_d_dst_base_addr_low_0_out
    "h50".asUInt(32.W)  -> io.field.dst_base_addr_low,
    //nvdla_cdp_d_dst_compression_en_0_out
    "h64".asUInt(32.W)  -> Cat("b0".asUInt(31.W), dst_compression_en),
    //nvdla_cdp_d_dst_dma_cfg_0_out
    "h60".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.field.dst_ram_type),
    //nvdla_cdp_d_dst_line_stride_0_out
    "h58".asUInt(32.W)  -> io.field.dst_line_stride,
    //nvdla_cdp_d_dst_surface_stride_0_out
    "h5c".asUInt(32.W)  -> io.field.dst_surface_stride,
    //nvdla_cdp_d_func_bypass_0_out
    "h4c".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.mul_bypass, io.field.sqsum_bypass),
    //nvdla_cdp_d_inf_input_num_0_out
    "h90".asUInt(32.W)  -> io.inf_input_num,
    //nvdla_cdp_d_lrn_cfg_0_out
    "h70".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.normalz_len),
    //nvdla_cdp_d_nan_flush_to_zero_0_out
    "h6c".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.field.nan_to_zero),
    //nvdla_cdp_d_nan_input_num_0_out
    "h8c".asUInt(32.W)  -> io.nan_input_num,
    //nvdla_cdp_d_nan_output_num_0_out
    "h94".asUInt(32.W)  -> io.nan_output_num,
    //nvdla_cdp_d_op_enable_0_out
    "h48".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.op_en),
    //nvdla_cdp_d_out_saturation_0_out
    "h98".asUInt(32.W)  -> io.out_saturation,
    //nvdla_cdp_d_perf_enable_0_out
    "h9c".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.lut_en, io.field.dma_en),
    //nvdla_cdp_d_perf_lut_hybrid_0_out
    "hac".asUInt(32.W)  -> io.perf_lut_hybrid,
    //nvdla_cdp_d_perf_lut_le_hit_0_out
    "hb0".asUInt(32.W)  -> io.perf_lut_le_hit,
    //nvdla_cdp_d_perf_lut_lo_hit_0_out
    "hb4".asUInt(32.W)  -> io.perf_lut_lo_hit,
    //nvdla_cdp_d_perf_lut_oflow_0_out
    "ha8".asUInt(32.W)  -> io.perf_lut_oflow,
    //nvdla_cdp_d_perf_lut_uflow_0_out
    "ha4".asUInt(32.W)  -> io.perf_lut_uflow,
    //nvdla_cdp_d_perf_write_stall_0_out
    "ha0".asUInt(32.W)  -> io.perf_write_stall,                                                 
   ))

    //Register flop declarations       

    // Register: NVDLA_CDP_D_CYA_0    Field: cya
    io.field.cya := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_d_cya_0_wren)
    // Register: NVDLA_CDP_D_DATA_FORMAT_0    Field: input_data_type
    io.field.input_data_type := RegEnable(io.reg.wr_data(1, 0), "b0".asUInt(2.W), nvdla_cdp_d_data_format_0_wren)
    // Register: NVDLA_CDP_D_DATIN_OFFSET_0    Field: datin_offset
    io.field.datin_offset := RegEnable(io.reg.wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdp_d_datin_offset_0_wren)
    // Register: NVDLA_CDP_D_DATIN_SCALE_0    Field: datin_scale
    io.field.datin_scale := RegEnable(io.reg.wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdp_d_datin_scale_0_wren)
    // Register: NVDLA_CDP_D_DATIN_SHIFTER_0    Field: datin_shifter
    io.field.datin_shifter := RegEnable(io.reg.wr_data(4, 0), "b0".asUInt(5.W), nvdla_cdp_d_datin_shifter_0_wren)
    // Register: NVDLA_CDP_D_DATOUT_OFFSET_0    Field: datout_offset
    io.field.datout_offset := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_d_datout_offset_0_wren)
    // Register: NVDLA_CDP_D_DATOUT_SCALE_0    Field: datout_scale
    io.field.datout_scale := RegEnable(io.reg.wr_data(15, 0), "b0".asUInt(16.W), nvdla_cdp_d_datout_scale_0_wren)
    // Register: NVDLA_CDP_D_DATOUT_SHIFTER_0    Field: datout_shifter
    io.field.datout_shifter := RegEnable(io.reg.wr_data(5, 0), "b0".asUInt(6.W), nvdla_cdp_d_datout_shifter_0_wren)
    // Register: NVDLA_CDP_D_DST_BASE_ADDR_HIGH_0    Field: dst_base_addr_high
    io.field.dst_base_addr_high := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_d_dst_base_addr_high_0_wren)
    // Register: NVDLA_CDP_D_DST_BASE_ADDR_LOW_0    Field: dst_base_addr_low
    io.field.dst_base_addr_low := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_d_dst_base_addr_low_0_wren)
    // Not generating flops for constant field NVDLA_CDP_D_DST_COMPRESSION_EN_0::dst_compression_en
    // Register: NVDLA_CDP_D_DST_DMA_CFG_0    Field: dst_ram_type
    io.field.dst_ram_type := RegEnable(io.reg.wr_data(0), false.B, nvdla_cdp_d_dst_dma_cfg_0_wren)
    // Register: NVDLA_CDP_D_DST_LINE_STRIDE_0    Field: dst_line_stride
    io.field.dst_line_stride := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_d_dst_line_stride_0_wren)
    // Register: NVDLA_CDP_D_DST_SURFACE_STRIDE_0    Field: dst_surface_stride
    io.field.dst_surface_stride := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_d_dst_surface_stride_0_wren)
    // Register: NVDLA_CDP_D_FUNC_BYPASS_0    Field: mul_bypass
    io.field.mul_bypass := RegEnable(io.reg.wr_data(1), false.B, nvdla_cdp_d_func_bypass_0_wren)
    // Register: NVDLA_CDP_D_FUNC_BYPASS_0    Field: sqsum_bypass
    io.field.sqsum_bypass := RegEnable(io.reg.wr_data(0), false.B, nvdla_cdp_d_func_bypass_0_wren)
    // Not generating flops for read-only field NVDLA_CDP_D_INF_INPUT_NUM_0::inf_input_num
    // Register: NVDLA_CDP_D_LRN_CFG_0    Field: normalz_len
    io.field.normalz_len := RegEnable(io.reg.wr_data(1, 0), "b0".asUInt(2.W), nvdla_cdp_d_lrn_cfg_0_wren)
    // Register: NVDLA_CDP_D_NAN_FLUSH_TO_ZERO_0    Field: nan_to_zero
    io.field.nan_to_zero := RegEnable(io.reg.wr_data(0), false.B, nvdla_cdp_d_nan_flush_to_zero_0_wren)
  // Not generating flops for read-only field NVDLA_CDP_D_NAN_INPUT_NUM_0::nan_input_num

  // Not generating flops for read-only field NVDLA_CDP_D_NAN_OUTPUT_NUM_0::nan_output_num

  // Not generating flops for field NVDLA_CDP_D_OP_ENABLE_0::op_en (to be implemented outside)

  // Not generating flops for read-only field NVDLA_CDP_D_OUT_SATURATION_0::out_saturation
    // Register: NVDLA_CDP_D_PERF_ENABLE_0    Field: dma_en
    io.field.dma_en := RegEnable(io.reg.wr_data(0), false.B, nvdla_cdp_d_perf_enable_0_wren)
    // Register: NVDLA_CDP_D_PERF_ENABLE_0    Field: lut_en
    io.field.lut_en := RegEnable(io.reg.wr_data(1), false.B, nvdla_cdp_d_perf_enable_0_wren)
  // Not generating flops for read-only field NVDLA_CDP_D_PERF_LUT_HYBRID_0::perf_lut_hybrid

  // Not generating flops for read-only field NVDLA_CDP_D_PERF_LUT_LE_HIT_0::perf_lut_le_hit

  // Not generating flops for read-only field NVDLA_CDP_D_PERF_LUT_LO_HIT_0::perf_lut_lo_hit

  // Not generating flops for read-only field NVDLA_CDP_D_PERF_LUT_OFLOW_0::perf_lut_oflow

  // Not generating flops for read-only field NVDLA_CDP_D_PERF_LUT_UFLOW_0::perf_lut_uflow

  // Not generating flops for read-only field NVDLA_CDP_D_PERF_WRITE_STALL_0::perf_write_stall



}}

