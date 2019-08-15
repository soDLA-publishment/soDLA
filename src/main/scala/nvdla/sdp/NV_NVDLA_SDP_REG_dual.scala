package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_REG_dual extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        //Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)
        val reg_wr_en = Input(Bool())

        //Writable register flop/trigger outputs
        val cvt_offset = Output(UInt(32.W))
        val cvt_scale = Output(UInt(16.W))
        val cvt_shift = Output(UInt(6.W))
        val channel = Output(UInt(13.W))
        val height = Output(UInt(13.W))
        val width_a = Output(UInt(13.W))
        val out_precision = Output(UInt(2.W))
        val proc_precision = Output(UInt(2.W))

        val bn_alu_shift_value = Output(UInt(6.W))
        val bn_alu_src = Output(Bool())      
        val bn_alu_operand = Output(UInt(16.W))
        val bn_alu_algo = Output(UInt(2.W))
        val bn_alu_bypass = Output(Bool())
        val bn_bypass = Output(Bool())
        val bn_mul_bypass = Output(Bool())
        val bn_mul_prelu = Output(Bool())
        val bn_relu_bypass = Output(Bool())
        val bn_mul_shift_value = Output(UInt(8.W))
        val bn_mul_src = Output(Bool())
        val bn_mul_operand = Output(UInt(16.W))

        val bs_alu_shift_value = Output(UInt(6.W))
        val bs_alu_src = Output(Bool())      
        val bs_alu_operand = Output(UInt(16.W))
        val bs_alu_algo = Output(UInt(2.W))
        val bs_alu_bypass = Output(Bool())
        val bs_bypass = Output(Bool())
        val bs_mul_bypass = Output(Bool())
        val bs_mul_prelu = Output(Bool())
        val bs_relu_bypass = Output(Bool())
        val bs_mul_shift_value = Output(UInt(8.W))
        val bs_mul_src = Output(Bool())
        val bs_mul_operand = Output(UInt(16.W))

        val ew_alu_cvt_bypass = Output(Bool())
        val ew_alu_src = Output(Bool())
        val ew_alu_cvt_offset = Output(UInt(32.W))
        val ew_alu_cvt_scale = Output(UInt(16.W))
        val ew_alu_cvt_truncate = Output(UInt(6.W))
        val ew_alu_operand = Output(UInt(32.W))
        val ew_alu_algo = Output(UInt(2.W))
        val ew_alu_bypass = Output(Bool())
        val ew_bypass = Output(Bool())
        val ew_lut_bypass = Output(Bool())
        val ew_mul_bypass = Output(Bool())
        val ew_mul_prelu = Output(Bool())
        val ew_mul_cvt_bypass = Output(Bool())
        val ew_mul_src = Output(Bool())
        val ew_mul_cvt_offset = Output(UInt(32.W))
        val ew_mul_cvt_scale = Output(UInt(16.W))
        val ew_mul_cvt_truncate = Output(UInt(6.W))
        val ew_mul_operand = Output(UInt(32.W))
        val ew_truncate = Output(UInt(10.W))

        val dst_base_addr_high = Output(UInt(32.W))
        val dst_base_addr_low = Output(UInt(32.W))
        val dst_batch_stride = Output(UInt(32.W))
        val dst_ram_type = Output(Bool())

        val dst_line_stride = Output(UInt(32.W))
        val dst_surface_stride = Output(UInt(32.W))
        val batch_number = Output(UInt(5.W))
        val flying_mode = Output(Bool())
        val nan_to_zero = Output(Bool())
        val output_dst = Output(Bool())
        val winograd = Output(Bool())
        val op_en_trigger = Output(Bool())
        val perf_dma_en = Output(Bool())
        val perf_lut_en = Output(Bool())
        val perf_nan_inf_count_en = Output(Bool())
        val perf_sat_en = Output(Bool())

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
    val nvdla_sdp_d_cvt_offset_0_wren = (io.reg_offset === "hc0".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_cvt_scale_0_wren = (io.reg_offset ===  "hc4".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_cvt_shift_0_wren = (io.reg_offset ===  "hc8".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_data_cube_channel_0_wren = (io.reg_offset ===  "h44".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_data_cube_height_0_wren = (io.reg_offset === "h40".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_data_cube_width_0_wren = (io.reg_offset === "h3c".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_data_format_0_wren = (io.reg_offset === "hbc".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bn_alu_cfg_0_wren = (io.reg_offset === "h70".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bn_alu_src_value_0_wren = (io.reg_offset === "h74".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bn_cfg_0_wren = (io.reg_offset === "h6c".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bn_mul_cfg_0_wren = (io.reg_offset === "h78".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bn_mul_src_value_0_wren = (io.reg_offset === "h7c".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bs_alu_cfg_0_wren = (io.reg_offset === "h5c".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bs_alu_src_value_0_wren = (io.reg_offset === "h60".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bs_cfg_0_wren = (io.reg_offset === "h58".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bs_mul_cfg_0_wren = (io.reg_offset === "h64".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_bs_mul_src_value_0_wren = (io.reg_offset === "h68".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_cfg_0_wren = (io.reg_offset === "h84".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_wren = (io.reg_offset === "h8c".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_wren = (io.reg_offset === "h90".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_wren = (io.reg_offset === "h94".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_alu_src_value_0_wren = (io.reg_offset === "h88".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_cfg_0_wren = (io.reg_offset === "h80".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_cfg_0_wren = (io.reg_offset === "h98".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_wren = (io.reg_offset === "ha0".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_wren = (io.reg_offset === "ha4".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_wren = (io.reg_offset === "ha8".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_mul_src_value_0_wren = (io.reg_offset === "h9c".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dp_ew_truncate_value_0_wren = (io.reg_offset === "hac".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dst_base_addr_high_0_wren = (io.reg_offset === "h4c".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dst_base_addr_low_0_wren = (io.reg_offset === "h48".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dst_batch_stride_0_wren = (io.reg_offset === "hb8".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dst_dma_cfg_0_wren = (io.reg_offset === "hb4".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dst_line_stride_0_wren = (io.reg_offset === "h50".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_dst_surface_stride_0_wren = (io.reg_offset === "h54".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_feature_mode_cfg_0_wren = (io.reg_offset === "hb0".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_op_enable_0_wren = (io.reg_offset === "h38".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_perf_enable_0_wren = (io.reg_offset === "hdc".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_perf_lut_hybrid_0_wren = (io.reg_offset === "hf0".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_perf_lut_le_hit_0_wren = (io.reg_offset === "hf4".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_perf_lut_lo_hit_0_wren = (io.reg_offset === "hf8".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_perf_lut_oflow_0_wren = (io.reg_offset === "he8".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_perf_lut_uflow_0_wren = (io.reg_offset === "he4".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_perf_out_saturation_0_wren = (io.reg_offset === "hec".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_perf_wdma_write_stall_0_wren = (io.reg_offset === "he0".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_status_0_wren = (io.reg_offset === "hcc".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_status_inf_input_num_0_wren = (io.reg_offset === "hd4".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_status_nan_input_num_0_wren = (io.reg_offset === "hd0".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_sdp_d_status_nan_output_num_0_wren = (io.reg_offset === "hd8".asUInt(32.W)) & io.reg_wr_en ;   


 

    io.op_en_trigger := nvdla_sdp_d_op_enable_0_wren

    //Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(
    //nvdla_sdp_d_cvt_offset_0_out  
    "hc0".asUInt(32.W)  -> io.cvt_offset,
    //nvdla_sdp_d_cvt_scale_0_out
    "hc4".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.cvt_scale),
    //nvdla_sdp_d_cvt_shift_0_out
    "hc8".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.cvt_shift),
    //nvdla_sdp_d_data_cube_channel_0_out
    "h44".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.channel),
    //nvdla_sdp_d_data_cube_height_0_out
    "h40".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.height),
    //nvdla_sdp_d_data_cube_width_0_out
    "h3c".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.width_a),
    //nvdla_sdp_d_data_format_0_out
    "hbc".asUInt(32.W)  -> Cat("b0".asUInt(28.W), io.out_precision, io.proc_precision),
    //nvdla_sdp_d_dp_bn_alu_cfg_0_out
    "h70".asUInt(32.W)  -> Cat("b0".asUInt(18.W), io.bn_alu_shift_value, "b0".asUInt(7.W), io.bn_alu_src),
    //nvdla_sdp_d_dp_bn_alu_src_value_0_out
    "h74".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.bn_alu_operand),
    //nvdla_sdp_d_dp_bn_cfg_0_out
    "h6c".asUInt(32.W)  -> Cat("b0".asUInt(25.W), io.bn_relu_bypass, io.bn_mul_prelu, io.bn_mul_bypass, io.bn_alu_algo, io.bn_alu_bypass, io.bn_bypass),
    //nvdla_sdp_d_dp_bn_mul_cfg_0_out
    "h78".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.bn_mul_shift_value, "b0".asUInt(7.W), io.bn_mul_src),
    //nvdla_sdp_d_dp_bn_mul_src_value_0_out
    "h7c".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.bn_mul_operand),
    //nvdla_sdp_d_dp_bs_alu_cfg_0_out
    "h5c".asUInt(32.W)  -> Cat("b0".asUInt(18.W), io.bs_alu_shift_value, "b0".asUInt(7.W), io.bs_alu_src),
    //nvdla_sdp_d_dp_bs_alu_src_value_0_out 
    "h60".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.bs_alu_operand), 
    //nvdla_sdp_d_dp_bs_cfg_0_out
    "h58".asUInt(32.W)  -> Cat("b0".asUInt(25.W), io.bs_relu_bypass, io.bs_mul_prelu, io.bs_mul_bypass, io.bs_alu_algo, io.bs_alu_bypass, io.bs_bypass),
    //nvdla_sdp_d_dp_bs_mul_cfg_0_out 
    "h64".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.bs_mul_shift_value, "b0".asUInt(7.W), io.bs_mul_src), 
    //nvdla_sdp_d_dp_bs_mul_src_value_0_out
    "h68".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.bs_mul_operand),
    //nvdla_sdp_d_dp_ew_alu_cfg_0_out
    "h84".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.ew_alu_cvt_bypass, io.ew_alu_src), 
    //nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_out
    "h8c".asUInt(32.W)  -> io.ew_alu_cvt_offset, 
    //nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_out
    "h90".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.ew_alu_cvt_scale), 
    //nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_out
    "h94".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.ew_alu_cvt_truncate),
    //nvdla_sdp_d_dp_ew_alu_src_value_0_out 
    "h88".asUInt(32.W)  -> io.ew_alu_operand, 
    //nvdla_sdp_d_dp_ew_cfg_0_out
    "h80".asUInt(32.W)  -> Cat("b0".asUInt(25.W), io.ew_lut_bypass, io.ew_mul_prelu, io.ew_mul_bypass, io.ew_alu_algo, io.ew_alu_bypass, io.ew_bypass),
    //nvdla_sdp_d_dp_ew_mul_cfg_0_out
    "h98".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.ew_mul_cvt_bypass, io.ew_mul_src),
    //nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_out
    "ha0".asUInt(32.W)  -> io.ew_mul_cvt_offset,
    //nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_out
    "ha4".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.ew_mul_cvt_scale),
    //nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_out
    "ha8".asUInt(32.W)  -> Cat("b0".asUInt(26.W), io.ew_mul_cvt_truncate),
    //nvdla_sdp_d_dp_ew_mul_src_value_0_out
    "h9c".asUInt(32.W)  -> io.ew_mul_operand,
    //nvdla_sdp_d_dp_ew_truncate_value_0_out
    "hac".asUInt(32.W)  -> Cat("b0".asUInt(22.W), io.ew_truncate),
    //nvdla_sdp_d_dst_base_addr_high_0_out
    "h4c".asUInt(32.W)  -> io.dst_base_addr_high,
    //nvdla_sdp_d_dst_base_addr_low_0_out
    "h48".asUInt(32.W)  -> io.dst_base_addr_low,
    //nvdla_sdp_d_dst_batch_stride_0_out
    "hb8".asUInt(32.W)  -> io.dst_batch_stride,
    //nvdla_sdp_d_dst_dma_cfg_0_out
    "hb4".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.dst_ram_type),
    //nvdla_sdp_d_dst_line_stride_0_out
    "h50".asUInt(32.W)  -> io.dst_line_stride,
    //nvdla_sdp_d_dst_surface_stride_0_out
    "h54".asUInt(32.W)  -> io.dst_surface_stride, 
    //nvdla_sdp_d_feature_mode_cfg_0_out                                                                             
    "hb0".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.batch_number, "b0".asUInt(4.W), io.nan_to_zero, io.winograd, io.output_dst, io.flying_mode),
    //nvdla_sdp_d_op_enable_0_out                                                                              
    "h38".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.op_en), 
    //nvdla_sdp_d_perf_enable_0_out                                                                             
    "hdc".asUInt(32.W)  -> Cat("b0".asUInt(28.W), io.perf_nan_inf_count_en, io.perf_sat_en, io.perf_lut_en, io.perf_dma_en),                                                                           
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
    io.cvt_offset := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_cvt_offset_0_wren)
  // Register: NVDLA_SDP_D_CVT_SCALE_0    Field: cvt_scale
    io.cvt_scale := RegEnable(io.reg_wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_cvt_scale_0_wren)
  // Register: NVDLA_SDP_D_CVT_SHIFT_0    Field: cvt_shift
    io.cvt_shift := RegEnable(io.reg_wr_data(5,0), "b0".asUInt(6.W), nvdla_sdp_d_cvt_shift_0_wren)
  // Register: NVDLA_SDP_D_DATA_CUBE_CHANNEL_0    Field: channel
    io.channel := RegEnable(io.reg_wr_data(12,0), "b0".asUInt(13.W), nvdla_sdp_d_data_cube_channel_0_wren)
  // Register: NVDLA_SDP_D_DATA_CUBE_HEIGHT_0    Field: height
    io.height := RegEnable(io.reg_wr_data(12,0), "b0".asUInt(13.W), nvdla_sdp_d_data_cube_height_0_wren)
  // Register: NVDLA_SDP_D_DATA_CUBE_WIDTH_0    Field: width
    io.width_a := RegEnable(io.reg_wr_data(12,0), "b0".asUInt(13.W), nvdla_sdp_d_data_cube_width_0_wren)
  // Register: NVDLA_SDP_D_DATA_FORMAT_0    Field: out_precision
    io.out_precision := RegEnable(io.reg_wr_data(3,2), "b00".asUInt(2.W), nvdla_sdp_d_data_format_0_wren)
  // Register: NVDLA_SDP_D_DATA_FORMAT_0    Field: proc_precision
    io.proc_precision := RegEnable(io.reg_wr_data(1,0), "b00".asUInt(2.W), nvdla_sdp_d_data_format_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_ALU_CFG_0    Field: bn_alu_shift_value
    io.bn_alu_shift_value := RegEnable(io.reg_wr_data(13,8), "b0".asUInt(6.W), nvdla_sdp_d_dp_bn_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_ALU_CFG_0    Field: bn_alu_src
    io.bn_alu_src := RegEnable(io.reg_wr_data(0), false.B, nvdla_sdp_d_dp_bn_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_ALU_SRC_VALUE_0    Field: bn_alu_operand
    io.bn_alu_operand := RegEnable(io.reg_wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_bn_alu_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_alu_algo
    io.bn_alu_algo := RegEnable(io.reg_wr_data(3,2), "b00".asUInt(2.W), nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_alu_bypass
    io.bn_alu_bypass := RegEnable(io.reg_wr_data(1), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_bypass
    io.bn_bypass := RegEnable(io.reg_wr_data(0), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_mul_bypass
    io.bn_mul_bypass := RegEnable(io.reg_wr_data(4), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_mul_prelu
    io.bn_mul_prelu := RegEnable(io.reg_wr_data(5), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_relu_bypass
    io.bn_relu_bypass := RegEnable(io.reg_wr_data(6), true.B, nvdla_sdp_d_dp_bn_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_MUL_CFG_0    Field: bn_mul_shift_value
    io.bn_mul_shift_value := RegEnable(io.reg_wr_data(15,8), "b0".asUInt(8.W), nvdla_sdp_d_dp_bn_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_MUL_CFG_0    Field: bn_mul_src
    io.bn_mul_src := RegEnable(io.reg_wr_data(0), false.B, nvdla_sdp_d_dp_bn_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BN_MUL_SRC_VALUE_0    Field: bn_mul_operand
    io.bn_mul_operand := RegEnable(io.reg_wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_bn_mul_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_ALU_CFG_0    Field: bs_alu_shift_value
    io.bs_alu_shift_value := RegEnable(io.reg_wr_data(13,8), "b0".asUInt(6.W), nvdla_sdp_d_dp_bs_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_ALU_CFG_0    Field: bs_alu_src
    io.bs_alu_src := RegEnable(io.reg_wr_data(0), false.B, nvdla_sdp_d_dp_bs_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_ALU_SRC_VALUE_0    Field: bs_alu_operand
    io.bs_alu_operand := RegEnable(io.reg_wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_bs_alu_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_alu_algo
    io.bs_alu_algo := RegEnable(io.reg_wr_data(3,2), "b00".asUInt(2.W), nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_alu_bypass
    io.bs_alu_bypass := RegEnable(io.reg_wr_data(1), true.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_bypass
    io.bs_bypass := RegEnable(io.reg_wr_data(0), true.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_mul_bypass
    io.bs_mul_bypass := RegEnable(io.reg_wr_data(4), true.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_mul_prelu
    io.bs_mul_prelu := RegEnable(io.reg_wr_data(5), false.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_relu_bypass
    io.bs_relu_bypass := RegEnable(io.reg_wr_data(6), true.B, nvdla_sdp_d_dp_bs_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_MUL_CFG_0    Field: bs_mul_shift_value
    io.bs_mul_shift_value := RegEnable(io.reg_wr_data(15,8), "b0".asUInt(8.W), nvdla_sdp_d_dp_bs_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_MUL_CFG_0    Field: bs_mul_src
    io.bs_mul_src := RegEnable(io.reg_wr_data(0), false.B, nvdla_sdp_d_dp_bs_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_BS_MUL_SRC_VALUE_0    Field: bs_mul_operand
    io.bs_mul_operand := RegEnable(io.reg_wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_bs_mul_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CFG_0    Field: ew_alu_cvt_bypass
    io.ew_alu_cvt_bypass := RegEnable(io.reg_wr_data(1), true.B, nvdla_sdp_d_dp_ew_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CFG_0    Field: ew_alu_src
    io.ew_alu_src := RegEnable(io.reg_wr_data(0), false.B, nvdla_sdp_d_dp_ew_alu_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CVT_OFFSET_VALUE_0    Field: ew_alu_cvt_offset
    io.ew_alu_cvt_offset := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CVT_SCALE_VALUE_0    Field: ew_alu_cvt_scale
    io.ew_alu_cvt_scale := RegEnable(io.reg_wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_CVT_TRUNCATE_VALUE_0    Field: ew_alu_cvt_truncate
    io.ew_alu_cvt_truncate := RegEnable(io.reg_wr_data(5, 0), "b0".asUInt(6.W), nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_ALU_SRC_VALUE_0    Field: ew_alu_operand
    io.ew_alu_operand := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dp_ew_alu_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_alu_algo
    io.ew_alu_algo := RegEnable(io.reg_wr_data(3,2), "b00".asUInt(2.W), nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_alu_bypass
    io.ew_alu_bypass := RegEnable(io.reg_wr_data(1), true.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_bypass
    io.ew_bypass := RegEnable(io.reg_wr_data(0), true.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_lut_bypass
    io.ew_lut_bypass := RegEnable(io.reg_wr_data(6), true.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_mul_bypass
    io.ew_mul_bypass := RegEnable(io.reg_wr_data(4), true.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_mul_prelu
    io.ew_mul_prelu := RegEnable(io.reg_wr_data(5), false.B, nvdla_sdp_d_dp_ew_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CFG_0    Field: ew_mul_cvt_bypass
    io.ew_mul_cvt_bypass := RegEnable(io.reg_wr_data(1), true.B, nvdla_sdp_d_dp_ew_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CFG_0    Field: ew_mul_src
    io.ew_mul_src := RegEnable(io.reg_wr_data(0), false.B, nvdla_sdp_d_dp_ew_mul_cfg_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CVT_OFFSET_VALUE_0    Field: ew_mul_cvt_offset
    io.ew_mul_cvt_offset := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CVT_SCALE_VALUE_0    Field: ew_mul_cvt_scale
    io.ew_mul_cvt_scale := RegEnable(io.reg_wr_data(15,0), "b0".asUInt(16.W), nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_CVT_TRUNCATE_VALUE_0    Field: ew_mul_cvt_truncate
    io.ew_mul_cvt_truncate := RegEnable(io.reg_wr_data(5,0), "b0".asUInt(6.W), nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_MUL_SRC_VALUE_0    Field: ew_mul_operand
    io.ew_mul_operand := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dp_ew_mul_src_value_0_wren)
  // Register: NVDLA_SDP_D_DP_EW_TRUNCATE_VALUE_0    Field: ew_truncate
    io.ew_truncate := RegEnable(io.reg_wr_data(9,0), "b0".asUInt(10.W), nvdla_sdp_d_dp_ew_truncate_value_0_wren)
  // Register: NVDLA_SDP_D_DST_BASE_ADDR_HIGH_0    Field: dst_base_addr_high
    io.dst_base_addr_high := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_base_addr_high_0_wren)
  // Register: NVDLA_SDP_D_DST_BASE_ADDR_LOW_0    Field: dst_base_addr_low
    io.dst_base_addr_low := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_base_addr_low_0_wren)
  // Register: NVDLA_SDP_D_DST_BATCH_STRIDE_0    Field: dst_batch_stride
    io.dst_batch_stride := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_batch_stride_0_wren)
  // Register: NVDLA_SDP_D_DST_DMA_CFG_0    Field: dst_ram_type
    io.dst_ram_type := RegEnable(io.reg_wr_data(0), false.B, nvdla_sdp_d_dst_dma_cfg_0_wren)
  // Register: NVDLA_SDP_D_DST_LINE_STRIDE_0    Field: dst_line_stride
    io.dst_line_stride := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_line_stride_0_wren)
  // Register: NVDLA_SDP_D_DST_SURFACE_STRIDE_0    Field: dst_surface_stride
    io.dst_surface_stride := RegEnable(io.reg_wr_data, "b0".asUInt(32.W), nvdla_sdp_d_dst_surface_stride_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: batch_number
    io.batch_number := RegEnable(io.reg_wr_data(12,8), "b0".asUInt(5.W), nvdla_sdp_d_feature_mode_cfg_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: flying_mode
    io.flying_mode := RegEnable(io.reg_wr_data(0), false.B, nvdla_sdp_d_feature_mode_cfg_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: nan_to_zero
    io.nan_to_zero := RegEnable(io.reg_wr_data(3), false.B, nvdla_sdp_d_feature_mode_cfg_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: output_dst
    io.output_dst := RegEnable(io.reg_wr_data(1), false.B, nvdla_sdp_d_feature_mode_cfg_0_wren)
  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: winograd
    io.winograd := RegEnable(io.reg_wr_data(2), false.B, nvdla_sdp_d_feature_mode_cfg_0_wren) 
  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_dma_en
    io.perf_dma_en := RegEnable(io.reg_wr_data(0), false.B, nvdla_sdp_d_perf_enable_0_wren)
  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_lut_en
    io.perf_lut_en := RegEnable(io.reg_wr_data(1), false.B, nvdla_sdp_d_perf_enable_0_wren)
  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_nan_inf_count_en
    io.perf_nan_inf_count_en := RegEnable(io.reg_wr_data(3), false.B, nvdla_sdp_d_perf_enable_0_wren)
  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_sat_en
    io.perf_sat_en := RegEnable(io.reg_wr_data(2), false.B, nvdla_sdp_d_perf_enable_0_wren)
                                                                   

}}

object NV_NVDLA_SDP_REG_dualDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_REG_dual())
}

