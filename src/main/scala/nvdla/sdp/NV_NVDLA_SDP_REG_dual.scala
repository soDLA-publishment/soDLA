package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_REG_dual extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        //Register control interface
        val reg = new reg_control_if

        //Writable register flop/trigger outputs
        val field = new sdp_reg_dual_flop_outputs
        val op_en_trigger = Output(Bool())

        //Read-only register inputs
        val op_en = Input(Bool())
        val lut_hybrid = Input(UInt(32.W))
        val lut_le_hit = Input(UInt(32.W))
        val lut_lo_hit = Input(UInt(32.W))
        val lut_oflow = Input(UInt(32.W))
        val lut_uflow = Input(UInt(32.W))
        val out_saturation = Input(UInt(32.W)) 
        val wdma_stall = Input(UInt(32.W)) 
        val status_unequal = Input(Bool()) 
        val status_inf_input_num = Input(UInt(32.W)) 
        val status_nan_input_num = Input(UInt(32.W)) 
        val status_nan_output_num = Input(UInt(32.W)) 

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
    val nvdla_sdp_d_cvt_offset_0_wren = (io.reg.offset === "hc0".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_cvt_scale_0_wren = (io.reg.offset ===  "hc4".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_cvt_shift_0_wren = (io.reg.offset ===  "hc8".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_data_cube_channel_0_wren = (io.reg.offset ===  "h44".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_data_cube_height_0_wren = (io.reg.offset === "h40".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_data_cube_width_0_wren = (io.reg.offset === "h3c".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_data_format_0_wren = (io.reg.offset === "hbc".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bn_alu_cfg_0_wren = (io.reg.offset === "h70".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bn_alu_src_value_0_wren = (io.reg.offset === "h74".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bn_cfg_0_wren = (io.reg.offset === "h6c".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bn_mul_cfg_0_wren = (io.reg.offset === "h78".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bn_mul_src_value_0_wren = (io.reg.offset === "h7c".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bs_alu_cfg_0_wren = (io.reg.offset === "h5c".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bs_alu_src_value_0_wren = (io.reg.offset === "h60".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bs_cfg_0_wren = (io.reg.offset === "h58".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bs_mul_cfg_0_wren = (io.reg.offset === "h64".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_bs_mul_src_value_0_wren = (io.reg.offset === "h68".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_cfg_0_wren = (io.reg.offset === "h84".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_wren = (io.reg.offset === "h8c".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_wren = (io.reg.offset === "h90".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_wren = (io.reg.offset === "h94".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_src_value_0_wren = (io.reg.offset === "h88".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_cfg_0_wren = (io.reg.offset === "h80".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_cfg_0_wren = (io.reg.offset === "h98".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_wren = (io.reg.offset === "ha0".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_wren = (io.reg.offset === "ha4".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_wren = (io.reg.offset === "ha8".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_src_value_0_wren = (io.reg.offset === "h9c".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dp_ew_truncate_value_0_wren = (io.reg.offset === "hac".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dst_base_addr_high_0_wren = (io.reg.offset === "h4c".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dst_base_addr_low_0_wren = (io.reg.offset === "h48".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dst_batch_stride_0_wren = (io.reg.offset === "hb8".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dst_dma_cfg_0_wren = (io.reg.offset === "hb4".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dst_line_stride_0_wren = (io.reg.offset === "h50".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_dst_surface_stride_0_wren = (io.reg.offset === "h54".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_feature_mode_cfg_0_wren = (io.reg.offset === "hb0".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_op_enable_0_wren = (io.reg.offset === "h38".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_perf_enable_0_wren = (io.reg.offset === "hdc".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_perf_lut_hybrid_0_wren = (io.reg.offset === "hf0".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_perf_lut_le_hit_0_wren = (io.reg.offset === "hf4".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_perf_lut_lo_hit_0_wren = (io.reg.offset === "hf8".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_perf_lut_oflow_0_wren = (io.reg.offset === "he8".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_perf_lut_uflow_0_wren = (io.reg.offset === "he4".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_perf_out_saturation_0_wren = (io.reg.offset === "hec".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_perf_wdma_write_stall_0_wren = (io.reg.offset === "he0".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_status_0_wren = (io.reg.offset === "hcc".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_status_inf_input_num_0_wren = (io.reg.offset === "hd4".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_status_nan_input_num_0_wren = (io.reg.offset === "hd0".asUInt(32.W)) & io.reg.wr_en ;   
    val nvdla_sdp_d_status_nan_output_num_0_wren = (io.reg.offset === "hd8".asUInt(32.W)) & io.reg.wr_en ;   


 

    io.op_en_trigger := nvdla_sdp_d_op_enable_0_wren

    //Output mux

    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(
    //nvdla_sdp_d_cvt_offset_0_out  
    "hc0".asUInt(32.W)  -> io.field.cvt_offset,
    //nvdla_sdp_d_cvt_scale_0_out
    "hc4".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.cvt_scale),
    //nvdla_sdp_d_cvt_shift_0_out
    "hc8".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.field.cvt_shift),
    //nvdla_sdp_d_data_cube_channel_0_out
    "h44".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.channel),
    //nvdla_sdp_d_data_cube_height_0_out
    "h40".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.height),
    //nvdla_sdp_d_data_cube_width_0_out
    "h3c".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.width_a),
    //nvdla_sdp_d_data_format_0_out
    "hbc".asUInt(32.W)  -> Cat("b0".asUInt(28.W), io.field.out_precision, io.field.proc_precision),
    //nvdla_sdp_d_dp_bn_alu_cfg_0_out
    "h70".asUInt(32.W)  -> Cat("b0".asUInt(18.W), io.field.bn_alu_shift_value, "b0".asUInt(7.W), io.field.bn_alu_src),
    //nvdla_sdp_d_dp_bn_alu_src_value_0_out
    "h74".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.bn_alu_operand),
    //nvdla_sdp_d_dp_bn_cfg_0_out
    "h6c".asUInt(32.W)  -> Cat("b0".asUInt(25.W), io.field.bn_relu_bypass, io.field.bn_mul_prelu, io.field.bn_mul_bypass, io.field.bn_alu_algo, io.field.bn_alu_bypass, io.field.bn_bypass),
    //nvdla_sdp_d_dp_bn_mul_cfg_0_out
    "h78".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.bn_mul_shift_value, "b0".asUInt(7.W), io.field.bn_mul_src),
    //nvdla_sdp_d_dp_bn_mul_src_value_0_out
    "h7c".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.bn_mul_operand),
    //nvdla_sdp_d_dp_bs_alu_cfg_0_out
    "h5c".asUInt(32.W)  -> Cat("b0".asUInt(18.W), io.field.bs_alu_shift_value, "b0".asUInt(7.W), io.field.bs_alu_src),
    //nvdla_sdp_d_dp_bs_alu_src_value_0_out 
    "h60".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.bs_alu_operand), 
    //nvdla_sdp_d_dp_bs_cfg_0_out
    "h58".asUInt(32.W)  -> Cat("b0".asUInt(25.W), io.field.bs_relu_bypass, io.field.bs_mul_prelu, io.field.bs_mul_bypass, io.field.bs_alu_algo, io.field.bs_alu_bypass, io.field.bs_bypass),
    //nvdla_sdp_d_dp_bs_mul_cfg_0_out 
    "h64".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.bs_mul_shift_value, "b0".asUInt(7.W), io.field.bs_mul_src), 
    //nvdla_sdp_d_dp_bs_mul_src_value_0_out
    "h68".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.bs_mul_operand),
    //nvdla_sdp_d_dp_ew_alu_cfg_0_out
    "h84".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.ew_alu_cvt_bypass, io.field.ew_alu_src), 
    //nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_out
    "h8c".asUInt(32.W)  -> io.field.ew_alu_cvt_offset, 
    //nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_out
    "h90".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.ew_alu_cvt_scale), 
    //nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_out
    "h94".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.field.ew_alu_cvt_truncate),
    //nvdla_sdp_d_dp_ew_alu_src_value_0_out 
    "h88".asUInt(32.W)  -> io.field.ew_alu_operand, 
    //nvdla_sdp_d_dp_ew_cfg_0_out
    "h80".asUInt(32.W)  -> Cat("b0".asUInt(25.W), io.field.ew_lut_bypass, io.field.ew_mul_prelu, io.field.ew_mul_bypass, io.field.ew_alu_algo, io.field.ew_alu_bypass, io.field.ew_bypass),
    //nvdla_sdp_d_dp_ew_mul_cfg_0_out
    "h98".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.ew_mul_cvt_bypass, io.field.ew_mul_src),
    //nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_out
    "ha0".asUInt(32.W)  -> io.field.ew_mul_cvt_offset,
    //nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_out
    "ha4".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.ew_mul_cvt_scale),
    //nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_out
    "ha8".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.field.ew_mul_cvt_truncate),
    //nvdla_sdp_d_dp_ew_mul_src_value_0_out
    "h9c".asUInt(32.W)  -> io.field.ew_mul_operand,
    //nvdla_sdp_d_dp_ew_truncate_value_0_out
    "hac".asUInt(32.W)  -> Cat("b0".asUInt(22.W), io.field.ew_truncate),
    //nvdla_sdp_d_dst_base_addr_high_0_out
    "h4c".asUInt(32.W)  -> io.field.dst_base_addr_high,
    //nvdla_sdp_d_dst_base_addr_low_0_out
    "h48".asUInt(32.W)  -> io.field.dst_base_addr_low,
    //nvdla_sdp_d_dst_batch_stride_0_out
    "hb8".asUInt(32.W)  -> io.field.dst_batch_stride,
    //nvdla_sdp_d_dst_dma_cfg_0_out
    "hb4".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.field.dst_ram_type),
    //nvdla_sdp_d_dst_line_stride_0_out
    "h50".asUInt(32.W)  -> io.field.dst_line_stride,
    //nvdla_sdp_d_dst_surface_stride_0_out
    "h54".asUInt(32.W)  -> io.field.dst_surface_stride, 
    //nvdla_sdp_d_feature_mode_cfg_0_out                                                                             
    "hb0".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.batch_number, "b0".asUInt(4.W), io.field.nan_to_zero, io.field.winograd, io.field.output_dst, io.field.flying_mode),
    //nvdla_sdp_d_op_enable_0_out                                                                              
    "h38".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.op_en), 
    //nvdla_sdp_d_perf_enable_0_out                                                                             
    "hdc".asUInt(32.W)  -> Cat("b0".asUInt(28.W), io.field.perf_nan_inf_count_en, io.field.perf_sat_en, io.field.perf_lut_en, io.field.perf_dma_en),                                                                           
    //nvdla_sdp_d_perf_lut_hybrid_0_out
    "hf0".asUInt(32.W)  -> io.lut_hybrid,
    //nvdla_sdp_d_perf_lut_le_hit_0_out                                                                              
    "hf4".asUInt(32.W)  -> io.lut_le_hit, 
    //nvdla_sdp_d_perf_lut_lo_hit_0_out                                                                             
    "hf8".asUInt(32.W)  -> io.lut_lo_hit,
    //nvdla_sdp_d_perf_lut_oflow_0_out                                                                              
    "he8".asUInt(32.W)  -> io.lut_oflow, 
    //nvdla_sdp_d_perf_lut_uflow_0_out                                                                             
    "he4".asUInt(32.W)  -> io.lut_uflow, 
    //nvdla_sdp_d_perf_out_saturation_0_out                                                                             
    "hec".asUInt(32.W)  -> io.out_saturation,
    //nvdla_sdp_d_perf_wdma_write_stall_0_out                                                                              
    "he0".asUInt(32.W)  -> io.wdma_stall,  
    //nvdla_sdp_d_status_0_out                                                                            
    "hcc".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.status_unequal), 
    //nvdla_sdp_d_status_inf_input_num_0_out                                                                             
    "hd4".asUInt(32.W)  -> io.status_inf_input_num, 
    //nvdla_sdp_d_status_nan_input_num_0_out                                                                             
    "hd0".asUInt(32.W)  -> io.status_nan_input_num, 
    //nvdla_sdp_d_status_nan_output_num_0_out                                                                             
    "hd8".asUInt(32.W)  -> io.status_nan_output_num                                                                              

    ))


  //yifengdu y.f.du1994@gmail.com update on Aug 1, 2019 
  //Solve Java heap space problem
  
  // Register: NVDLA_SDP_D_CVT_OFFSET_0    Field: cvt_offset
    io.field.cvt_offset := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_cvt_offset_0_wren)
  // Register: NVDLA_SDP_D_CVT_SCALE_0    Field: cvt_scale
    io.field.cvt_scale := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_cvt_scale_0_wren)
  // Register: NVDLA_SDP_D_CVT_SHIFT_0    Field: cvt_shift
    io.field.cvt_shift := RegEnable(io.reg.wr_data(5,0), "b0".asUInt(6.W), nvdla_sdp_d_cvt_shift_0_wren)
  // Register: NVDLA_SDP_D_DATA_CUBE_CHANNEL_0    Field: channel
    io.field.channel := RegEnable(io.reg.wr_data(12,0), "b0".asUInt(13.W), nvdla_sdp_d_data_cube_channel_0_wren)
  // Register: NVDLA_SDP_D_DATA_CUBE_HEIGHT_0    Field: height
    io.field.height := RegEnable(io.reg.wr_data(12,0), "b0".asUInt(13.W), nvdla_sdp_d_data_cube_height_0_wren)
  // Register: NVDLA_SDP_D_DATA_CUBE_WIDTH_0    Field: width
    io.field.width_a := RegEnable(io.reg.wr_data(12,0), "b0".asUInt(13.W), nvdla_sdp_d_data_cube_width_0_wren)
  // Register: NVDLA_SDP_D_DATA_FORMAT_0    Field: out_precision
    io.field.out_precision := RegEnable(io.reg.wr_data(3,2), "b00".asUInt(2.W), nvdla_sdp_d_data_format_0_wren)
  // Register: NVDLA_SDP_D_DATA_FORMAT_0    Field: proc_precision
    io.field.proc_precision := RegEnable(io.reg.wr_data(1,0), "b00".asUInt(2.W), nvdla_sdp_d_data_format_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_ALU_CFG_0    Field: bn_alu_shift_value
    io.field.bn_alu_shift_value := RegEnable(io.reg.wr_data(13,8), "b0".asUInt(6.W), nvdla_sdp_d_dp_bn_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_ALU_CFG_0    Field: bn_alu_src
    io.field.bn_alu_src := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_d_dp_bn_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_ALU_SRC_VALUE_0    Field: bn_alu_operand
    io.field.bn_alu_operand := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_bn_alu_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_alu_algo
    io.field.bn_alu_algo := RegEnable(io.reg.wr_data(3,2), "b00".asUInt(2.W), nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_alu_bypass
    io.field.bn_alu_bypass := RegEnable(io.reg.wr_data(1), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_bypass
    io.field.bn_bypass := RegEnable(io.reg.wr_data(0), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_mul_bypass
    io.field.bn_mul_bypass := RegEnable(io.reg.wr_data(4), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_mul_prelu
    io.field.bn_mul_prelu := RegEnable(io.reg.wr_data(5), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_relu_bypass
    io.field.bn_relu_bypass := RegEnable(io.reg.wr_data(6), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_MUL_CFG_0    Field: bn_mul_shift_value
    io.field.bn_mul_shift_value := RegEnable(io.reg.wr_data(15,8), "b0".asUInt(8.W), nvdla_sdp_d_dp_bn_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_MUL_CFG_0    Field: bn_mul_src
    io.field.bn_mul_src := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_d_dp_bn_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_MUL_SRC_VALUE_0    Field: bn_mul_operand
    io.field.bn_mul_operand := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_bn_mul_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_ALU_CFG_0    Field: bs_alu_shift_value
    io.field.bs_alu_shift_value := RegEnable(io.reg.wr_data(13,8), "b0".asUInt(6.W), nvdla_sdp_d_dp_bs_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_ALU_CFG_0    Field: bs_alu_src
    io.field.bs_alu_src := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_d_dp_bs_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_ALU_SRC_VALUE_0    Field: bs_alu_operand
    io.field.bs_alu_operand := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_bs_alu_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_alu_algo
    io.field.bs_alu_algo := RegEnable(io.reg.wr_data(3,2), "b00".asUInt(2.W), nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_alu_bypass
    io.field.bs_alu_bypass := RegEnable(io.reg.wr_data(1), true.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_bypass
    io.field.bs_bypass := RegEnable(io.reg.wr_data(0), true.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_mul_bypass
    io.field.bs_mul_bypass := RegEnable(io.reg.wr_data(4), true.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_mul_prelu
    io.field.bs_mul_prelu := RegEnable(io.reg.wr_data(5), false.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_relu_bypass
    io.field.bs_relu_bypass := RegEnable(io.reg.wr_data(6), true.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_MUL_CFG_0    Field: bs_mul_shift_value
    io.field.bs_mul_shift_value := RegEnable(io.reg.wr_data(15,8), "b0".asUInt(8.W), nvdla_sdp_d_dp_bs_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_MUL_CFG_0    Field: bs_mul_src
    io.field.bs_mul_src := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_d_dp_bs_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_MUL_SRC_VALUE_0    Field: bs_mul_operand
    io.field.bs_mul_operand := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_bs_mul_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CFG_0    Field: ew_alu_cvt_bypass
    io.field.ew_alu_cvt_bypass := RegEnable(io.reg.wr_data(1), true.B, nvdla_sdp_d_dp_ew_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CFG_0    Field: ew_alu_src
    io.field.ew_alu_src := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_d_dp_ew_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CVT_OFFSET_VALUE_0    Field: ew_alu_cvt_offset
    io.field.ew_alu_cvt_offset := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CVT_SCALE_VALUE_0    Field: ew_alu_cvt_scale
    io.field.ew_alu_cvt_scale := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CVT_TRUNCATE_VALUE_0    Field: ew_alu_cvt_truncate
    io.field.ew_alu_cvt_truncate := RegEnable(io.reg.wr_data(5, 0), "b0".asUInt(6.W), nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_SRC_VALUE_0    Field: ew_alu_operand
    io.field.ew_alu_operand := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dp_ew_alu_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_alu_algo
    io.field.ew_alu_algo := RegEnable(io.reg.wr_data(3,2), "b00".asUInt(2.W), nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_alu_bypass
    io.field.ew_alu_bypass := RegEnable(io.reg.wr_data(1), true.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_bypass
    io.field.ew_bypass := RegEnable(io.reg.wr_data(0), true.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_lut_bypass
    io.field.ew_lut_bypass := RegEnable(io.reg.wr_data(6), true.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_mul_bypass
    io.field.ew_mul_bypass := RegEnable(io.reg.wr_data(4), true.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_mul_prelu
    io.field.ew_mul_prelu := RegEnable(io.reg.wr_data(5), false.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CFG_0    Field: ew_mul_cvt_bypass
    io.field.ew_mul_cvt_bypass := RegEnable(io.reg.wr_data(1), true.B, nvdla_sdp_d_dp_ew_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CFG_0    Field: ew_mul_src
    io.field.ew_mul_src := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_d_dp_ew_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CVT_OFFSET_VALUE_0    Field: ew_mul_cvt_offset
    io.field.ew_mul_cvt_offset := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CVT_SCALE_VALUE_0    Field: ew_mul_cvt_scale
    io.field.ew_mul_cvt_scale := RegEnable(io.reg.wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CVT_TRUNCATE_VALUE_0    Field: ew_mul_cvt_truncate
    io.field.ew_mul_cvt_truncate := RegEnable(io.reg.wr_data(5,0), "b0".asUInt(6.W), nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_SRC_VALUE_0    Field: ew_mul_operand
    io.field.ew_mul_operand := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dp_ew_mul_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_TRUNCATE_VALUE_0    Field: ew_truncate
    io.field.ew_truncate := RegEnable(io.reg.wr_data(9,0), "b0".asUInt(10.W), nvdla_sdp_d_dp_ew_truncate_value_0_wren)
  // Register: NVDLA_SDP_D_DST_BASE_ADDR_HIGH_0    Field: dst_base_addr_high
    io.field.dst_base_addr_high := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_base_addr_high_0_wren)
  // Register: NVDLA_SDP_D_DST_BASE_ADDR_LOW_0    Field: dst_base_addr_low
    io.field.dst_base_addr_low := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_base_addr_low_0_wren)
  // Register: NVDLA_SDP_D_DST_BATCH_STRIDE_0    Field: dst_batch_stride
    io.field.dst_batch_stride := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_batch_stride_0_wren)
  // Register: NVDLA_SDP_D_DST_DMA_CFG_0    Field: dst_ram_type
    io.field.dst_ram_type := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_d_dst_dma_cfg_0_wren)
  // Register: NVDLA_SDP_D_DST_LINE_STRIDE_0    Field: dst_line_stride
    io.field.dst_line_stride := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_line_stride_0_wren)
  // Register: NVDLA_SDP_D_DST_SURFACE_STRIDE_0    Field: dst_surface_stride
    io.field.dst_surface_stride := RegEnable(io.reg.wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_surface_stride_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: batch_number
    io.field.batch_number := RegEnable(io.reg.wr_data(12,8), "b0".asUInt(5.W), nvdla_sdp_d_feature_mode_cfg_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: flying_mode
    io.field.flying_mode := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_d_feature_mode_cfg_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: nan_to_zero
    io.field.nan_to_zero := RegEnable(io.reg.wr_data(3), false.B, nvdla_sdp_d_feature_mode_cfg_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: output_dst
    io.field.output_dst := RegEnable(io.reg.wr_data(1), false.B, nvdla_sdp_d_feature_mode_cfg_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: winograd
    io.field.winograd := RegEnable(io.reg.wr_data(2), false.B, nvdla_sdp_d_feature_mode_cfg_0_wren) 
  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_dma_en
    io.field.perf_dma_en := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_d_perf_enable_0_wren)
  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_lut_en
    io.field.perf_lut_en := RegEnable(io.reg.wr_data(1), false.B, nvdla_sdp_d_perf_enable_0_wren)
  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_nan_inf_count_en
    io.field.perf_nan_inf_count_en := RegEnable(io.reg.wr_data(3), false.B, nvdla_sdp_d_perf_enable_0_wren)
  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_sat_en
    io.field.perf_sat_en := RegEnable(io.reg.wr_data(2), false.B, nvdla_sdp_d_perf_enable_0_wren)
                                                                   

}}

object NV_NVDLA_SDP_REG_dualDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_REG_dual())
}

