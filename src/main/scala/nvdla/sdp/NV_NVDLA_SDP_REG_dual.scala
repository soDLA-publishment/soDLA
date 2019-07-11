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
    val nvdla_sdp_d_cvt_offset_0_wren = (io.reg_offset === "hc0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_cvt_scale_0_wren = (io.reg_offset ===  "hc4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_cvt_shift_0_wren = (io.reg_offset ===  "hc8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_data_cube_channel_0_wren = (io.reg_offset ===  "h44".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_data_cube_height_0_wren = (io.reg_offset === "h40".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_data_cube_width_0_wren = (io.reg_offset === "h3c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_data_format_0_wren = (io.reg_offset === "hbc".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bn_alu_cfg_0_wren = (io.reg_offset === "h70".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bn_alu_src_value_0_wren = (io.reg_offset === "h74".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bn_cfg_0_wren = (io.reg_offset === "h6c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bn_mul_cfg_0_wren = (io.reg_offset === "h78".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bn_mul_src_value_0_wren = (io.reg_offset === "h7c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bs_alu_cfg_0_wren = (io.reg_offset === "h5c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bs_alu_src_value_0_wren = (io.reg_offset === "h60".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bs_cfg_0_wren = (io.reg_offset === "h58".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bs_mul_cfg_0_wren = (io.reg_offset === "h64".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_bs_mul_src_value_0_wren = (io.reg_offset === "h68".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_alu_cfg_0_wren = (io.reg_offset === "h84".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_wren = (io.reg_offset === "h8c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_wren = (io.reg_offset === "h90".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_wren = (io.reg_offset === "h94".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_alu_src_value_0_wren = (io.reg_offset === "h88".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_cfg_0_wren = (io.reg_offset === "h80".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_mul_cfg_0_wren = (io.reg_offset === "h98".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_wren = (io.reg_offset === "ha0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_wren = (io.reg_offset === "ha4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_wren = (io.reg_offset === "ha8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_mul_src_value_0_wren = (io.reg_offset === "h9c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dp_ew_truncate_value_0_wren = (io.reg_offset === "hac".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dst_base_addr_high_0_wren = (io.reg_offset === "h4c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dst_base_addr_low_0_wren = (io.reg_offset === "h48".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dst_batch_stride_0_wren = (io.reg_offset === "hb8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dst_dma_cfg_0_wren = (io.reg_offset === "hb4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dst_line_stride_0_wren = (io.reg_offset === "h50".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_dst_surface_stride_0_wren = (io.reg_offset === "h54".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_feature_mode_cfg_0_wren = (io.reg_offset === "hb0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_op_enable_0_wren = (io.reg_offset === "h38".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_perf_enable_0_wren = (io.reg_offset === "hdc".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_perf_lut_hybrid_0_wren = (io.reg_offset === "hf0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_perf_lut_le_hit_0_wren = (io.reg_offset === "hf4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_perf_lut_lo_hit_0_wren = (io.reg_offset === "hf8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_perf_lut_oflow_0_wren = (io.reg_offset === "he8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_perf_lut_uflow_0_wren = (io.reg_offset === "he4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_perf_out_saturation_0_wren = (io.reg_offset === "hec".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_perf_wdma_write_stall_0_wren = (io.reg_offset === "he0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_status_0_wren = (io.reg_offset === "hcc".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_status_inf_input_num_0_wren = (io.reg_offset === "hd4".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_status_nan_input_num_0_wren = (io.reg_offset === "hd0".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_d_status_nan_output_num_0_wren = (io.reg_offset === "hd8".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)


    val nvdla_sdp_d_cvt_offset_0_out = io.cvt_offset
    val nvdla_sdp_d_cvt_scale_0_out = Cat("b0".asUInt(16.W), io.cvt_scale)
    val nvdla_sdp_d_cvt_shift_0_out = Cat("b0".asUInt(26.W), io.cvt_shift)
    val nvdla_sdp_d_data_cube_channel_0_out = Cat("b0".asUInt(19.W), io.channel)
    val nvdla_sdp_d_data_cube_height_0_out = Cat("b0".asUInt(19.W), io.height)
    val nvdla_sdp_d_data_cube_width_0_out = Cat("b0".asUInt(19.W), io.width_a)
    val nvdla_sdp_d_data_format_0_out = Cat("b0".asUInt(28.W), io.out_precision, io.proc_precision)
    val nvdla_sdp_d_dp_bn_alu_cfg_0_out = Cat("b0".asUInt(18.W), io.bn_alu_shift_value, "b0".asUInt(7.W), io.bn_alu_src)
    val nvdla_sdp_d_dp_bn_alu_src_value_0_out = Cat("b0".asUInt(16.W), io.bn_alu_operand)
    val nvdla_sdp_d_dp_bn_cfg_0_out = Cat("b0".asUInt(25.W), io.bn_relu_bypass, io.bn_mul_prelu, io.bn_mul_bypass, io.bn_alu_algo, io.bn_alu_bypass, io.bn_bypass)
    val nvdla_sdp_d_dp_bn_mul_cfg_0_out = Cat("b0".asUInt(16.W), io.bn_mul_shift_value, "b0".asUInt(7.W), io.bn_mul_src)
    val nvdla_sdp_d_dp_bn_mul_src_value_0_out = Cat("b0".asUInt(16.W), io.bn_mul_operand)
    val nvdla_sdp_d_dp_bs_alu_cfg_0_out = Cat("b0".asUInt(18.W), io.bs_alu_shift_value, "b0".asUInt(7.W), io.bs_alu_src)
    val nvdla_sdp_d_dp_bs_alu_src_value_0_out = Cat("b0".asUInt(16.W), io.bs_alu_operand)
    val nvdla_sdp_d_dp_bs_cfg_0_out = Cat("b0".asUInt(25.W), io.bs_relu_bypass, io.bs_mul_prelu, io.bs_mul_bypass, io.bs_alu_algo, io.bs_alu_bypass, io.bs_bypass)
    val nvdla_sdp_d_dp_bs_mul_cfg_0_out = Cat("b0".asUInt(16.W), io.bs_mul_shift_value, "b0".asUInt(7.W), io.bs_mul_src)
    val nvdla_sdp_d_dp_bs_mul_src_value_0_out = Cat("b0".asUInt(16.W), io.bs_mul_operand)
    val nvdla_sdp_d_dp_ew_alu_cfg_0_out = Cat("b0".asUInt(30.W), io.ew_alu_cvt_bypass, io.ew_alu_src)
    val nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_out = io.ew_alu_cvt_offset
    val nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_out = Cat("b0".asUInt(16.W), io.ew_alu_cvt_scale)
    val nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_out = Cat("b0".asUInt(26.W), io.ew_alu_cvt_truncate)
    val nvdla_sdp_d_dp_ew_alu_src_value_0_out = io.ew_alu_operand
    val nvdla_sdp_d_dp_ew_cfg_0_out = Cat("b0".asUInt(25.W), io.ew_lut_bypass, io.ew_mul_prelu, io.ew_mul_bypass, io.ew_alu_algo, io.ew_alu_bypass, io.ew_bypass)
    val nvdla_sdp_d_dp_ew_mul_cfg_0_out = Cat("b0".asUInt(30.W), io.ew_mul_cvt_bypass, io.ew_mul_src)
    val nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_out = io.ew_mul_cvt_offset
    val nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_out = Cat("b0".asUInt(16.W), io.ew_mul_cvt_scale)
    val nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_out = Cat("b0".asUInt(26.W), io.ew_mul_cvt_truncate)
    val nvdla_sdp_d_dp_ew_mul_src_value_0_out = io.ew_mul_operand
    val nvdla_sdp_d_dp_ew_truncate_value_0_out = Cat("b0".asUInt(22.W), io.ew_truncate)
    val nvdla_sdp_d_dst_base_addr_high_0_out = io.dst_base_addr_high
    val nvdla_sdp_d_dst_base_addr_low_0_out = io.dst_base_addr_low
    val nvdla_sdp_d_dst_batch_stride_0_out = io.dst_batch_stride
    val nvdla_sdp_d_dst_dma_cfg_0_out = Cat("b0".asUInt(31.W), io.dst_ram_type)
    val nvdla_sdp_d_dst_line_stride_0_out = io.dst_line_stride
    val nvdla_sdp_d_dst_surface_stride_0_out = io.dst_surface_stride
    val nvdla_sdp_d_feature_mode_cfg_0_out = Cat("b0".asUInt(19.W), io.batch_number, "b0".asUInt(4.W), io.nan_to_zero, io.winograd, io.output_dst, io.flying_mode)
    val nvdla_sdp_d_op_enable_0_out = Cat("b0".asUInt(31.W), io.op_en)
    val nvdla_sdp_d_perf_enable_0_out = Cat("b0".asUInt(28.W), io.perf_nan_inf_count_en, io.perf_sat_en, io.perf_lut_en, io.perf_dma_en)
    val nvdla_sdp_d_perf_lut_hybrid_0_out = io.lut_hybrid
    val nvdla_sdp_d_perf_lut_le_hit_0_out = io.lut_le_hit
    val nvdla_sdp_d_perf_lut_lo_hit_0_out= io.lut_lo_hit
    val nvdla_sdp_d_perf_lut_oflow_0_out= io.lut_oflow
    val nvdla_sdp_d_perf_lut_uflow_0_out= io.lut_uflow
    val nvdla_sdp_d_perf_out_saturation_0_out= io.out_saturation
    val nvdla_sdp_d_perf_wdma_write_stall_0_out= io.wdma_stall
    val nvdla_sdp_d_status_0_out = Cat("b0".asUInt(31.W), io.status_unequal)
    val nvdla_sdp_d_status_inf_input_num_0_out = io.status_inf_input_num
    val nvdla_sdp_d_status_nan_input_num_0_out = io.status_nan_input_num
    val nvdla_sdp_d_status_nan_output_num_0_out = io.status_nan_output_num

    io.op_en_trigger := nvdla_sdp_d_op_enable_0_wren

    //Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
    "hc0".asUInt(32.W)  -> nvdla_sdp_d_cvt_offset_0_out,
    "hc4".asUInt(32.W)  -> nvdla_sdp_d_cvt_scale_0_out,
    "hc8".asUInt(32.W)  -> nvdla_sdp_d_cvt_shift_0_out,
    "h44".asUInt(32.W)  -> nvdla_sdp_d_data_cube_channel_0_out,
    "h40".asUInt(32.W)  -> nvdla_sdp_d_data_cube_height_0_out,
    "h3c".asUInt(32.W)  -> nvdla_sdp_d_data_cube_width_0_out,
    "hbc".asUInt(32.W)  -> nvdla_sdp_d_data_format_0_out,
    "h70".asUInt(32.W)  -> nvdla_sdp_d_dp_bn_alu_cfg_0_out,
    "h74".asUInt(32.W)  -> nvdla_sdp_d_dp_bn_alu_src_value_0_out,
    "h6c".asUInt(32.W)  -> nvdla_sdp_d_dp_bn_cfg_0_out,
    "h78".asUInt(32.W)  -> nvdla_sdp_d_dp_bn_mul_cfg_0_out,
    "h7c".asUInt(32.W)  -> nvdla_sdp_d_dp_bn_mul_src_value_0_out,
    "h5c".asUInt(32.W)  -> nvdla_sdp_d_dp_bs_alu_cfg_0_out, 
    "h60".asUInt(32.W)  -> nvdla_sdp_d_dp_bs_alu_src_value_0_out, 
    "h58".asUInt(32.W)  -> nvdla_sdp_d_dp_bs_cfg_0_out, 
    "h64".asUInt(32.W)  -> nvdla_sdp_d_dp_bs_mul_cfg_0_out, 
    "h68".asUInt(32.W)  -> nvdla_sdp_d_dp_bs_mul_src_value_0_out, 
    "h84".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_alu_cfg_0_out, 
    "h8c".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_out, 
    "h90".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_out, 
    "h94".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_out, 
    "h88".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_alu_src_value_0_out, 
    "h80".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_cfg_0_out,
    "h98".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_mul_cfg_0_out,
    "ha0".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_out,
    "ha4".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_out,
    "ha8".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_out,
    "h9c".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_mul_src_value_0_out,
    "hac".asUInt(32.W)  -> nvdla_sdp_d_dp_ew_truncate_value_0_out,
    "h4c".asUInt(32.W)  -> nvdla_sdp_d_dst_base_addr_high_0_out,
    "h48".asUInt(32.W)  -> nvdla_sdp_d_dst_base_addr_low_0_out,
    "hb8".asUInt(32.W)  -> nvdla_sdp_d_dst_batch_stride_0_out,
    "hb4".asUInt(32.W)  -> nvdla_sdp_d_dst_dma_cfg_0_out,
    "h50".asUInt(32.W)  -> nvdla_sdp_d_dst_line_stride_0_out,
    "h54".asUInt(32.W)  -> nvdla_sdp_d_dst_surface_stride_0_out,                                                                              
    "hb0".asUInt(32.W)  -> nvdla_sdp_d_feature_mode_cfg_0_out,                                                                              
    "h38".asUInt(32.W)  -> nvdla_sdp_d_op_enable_0_out,                                                                              
    "hdc".asUInt(32.W)  -> nvdla_sdp_d_perf_enable_0_out,                                                                              
    "hf0".asUInt(32.W)  -> nvdla_sdp_d_perf_lut_hybrid_0_out,                                                                              
    "hf4".asUInt(32.W)  -> nvdla_sdp_d_perf_lut_le_hit_0_out,                                                                              
    "hf8".asUInt(32.W)  -> nvdla_sdp_d_perf_lut_lo_hit_0_out,                                                                              
    "he8".asUInt(32.W)  -> nvdla_sdp_d_perf_lut_oflow_0_out,                                                                              
    "he4".asUInt(32.W)  -> nvdla_sdp_d_perf_lut_uflow_0_out,                                                                              
    "hec".asUInt(32.W)  -> nvdla_sdp_d_perf_out_saturation_0_out,                                                                              
    "he0".asUInt(32.W)  -> nvdla_sdp_d_perf_wdma_write_stall_0_out,                                                                              
    "hcc".asUInt(32.W)  -> nvdla_sdp_d_status_0_out,                                                                              
    "hd4".asUInt(32.W)  -> nvdla_sdp_d_status_inf_input_num_0_out,                                                                              
    "hd0".asUInt(32.W)  -> nvdla_sdp_d_status_nan_input_num_0_out,                                                                              
    "hd8".asUInt(32.W)  -> nvdla_sdp_d_status_nan_output_num_0_out                                                                              

    ))

    //Register flop declarations

    val cvt_offset_out = RegInit("b0".asUInt(32.W))
    val cvt_scale_out = RegInit("b0".asUInt(16.W))
    val cvt_shift_out = RegInit("b0".asUInt(6.W))
    val channel_out = RegInit("b0".asUInt(13.W))
    val height_out = RegInit("b0".asUInt(13.W))
    val width_a_out = RegInit("b0".asUInt(13.W))
    val out_precision_out = RegInit("b00".asUInt(2.W))
    val proc_precision_out = RegInit("b00".asUInt(2.W))
    val bn_alu_shift_value_out = RegInit("b0".asUInt(6.W))
    val bn_alu_src_out = RegInit(false.B)
    val bn_alu_operand_out = RegInit("b0".asUInt(16.W))
    val bn_alu_algo_out = RegInit("b00".asUInt(2.W))
    val bn_alu_bypass_out = RegInit(true.B)
    val bn_bypass_out = RegInit(true.B)
    val bn_mul_bypass_out = RegInit(true.B)
    val bn_mul_prelu_out = RegInit(false.B)
    val bn_relu_bypass_out = RegInit(true.B)
    val bn_mul_shift_value_out = RegInit("b0".asUInt(8.W))
    val bn_mul_src_out = RegInit(false.B)
    val bn_mul_operand_out = RegInit("b0".asUInt(16.W))
    val bs_alu_shift_value_out = RegInit("b0".asUInt(6.W))
    val bs_alu_src_out = RegInit(false.B)
    val bs_alu_operand_out = RegInit("b0".asUInt(16.W))
    val bs_alu_algo_out = RegInit("b00".asUInt(2.W))
    val bs_alu_bypass_out = RegInit(true.B)
    val bs_bypass_out = RegInit(true.B)
    val bs_mul_bypass_out = RegInit(true.B)
    val bs_mul_prelu_out = RegInit(true.B)
    val bs_relu_bypass_out = RegInit(true.B)
    val bs_mul_shift_value_out = RegInit("b0".asUInt(8.W))
    val bs_mul_src_out = RegInit(false.B)
    val bs_mul_operand_out = RegInit("b0".asUInt(16.W))
    val ew_alu_cvt_bypass_out = RegInit(true.B)
    val ew_alu_src_out = RegInit(false.B)
    val ew_alu_cvt_offset_out = RegInit("b0".asUInt(32.W))
    val ew_alu_cvt_scale_out = RegInit("b0".asUInt(16.W))
    val ew_alu_cvt_truncate_out = RegInit("b0".asUInt(6.W))
    val ew_alu_operand_out = RegInit("b0".asUInt(32.W))
    val ew_alu_algo_out = RegInit("b00".asUInt(2.W))
    val ew_alu_bypass_out = RegInit(true.B)
    val ew_bypass_out = RegInit(true.B)
    val ew_lut_bypass_out = RegInit(true.B)
    val ew_mul_bypass_out = RegInit(true.B)
    val ew_mul_prelu_out = RegInit(false.B)
    val ew_mul_cvt_bypass_out = RegInit(true.B)
    val ew_mul_src_out = RegInit(false.B)
    val ew_mul_cvt_offset_out = RegInit("b0".asUInt(32.W))
    val ew_mul_cvt_scale_out = RegInit("b0".asUInt(16.W))
    val ew_mul_cvt_truncate_out = RegInit("b0".asUInt(6.W))
    val ew_mul_operand_out = RegInit("b0".asUInt(32.W))
    val ew_truncate_out = RegInit("b0".asUInt(10.W))    
    val dst_base_addr_high_out = RegInit("b0".asUInt(32.W))
    val dst_base_addr_low_out = RegInit("b0".asUInt(32.W))
    val dst_batch_stride_out = RegInit("b0".asUInt(32.W))
    val dst_ram_type_out = RegInit(false.B)
    val dst_line_stride_out = RegInit("b0".asUInt(32.W))
    val dst_surface_stride_out = RegInit("b0".asUInt(32.W))
    val batch_number_out = RegInit("b0".asUInt(5.W))
    val flying_mode_out = RegInit(false.B)
    val nan_to_zero_out = RegInit(false.B)
    val output_dst_out = RegInit(false.B)
    val winograd_out = RegInit(false.B)
    val perf_dma_en_out = RegInit(false.B)
    val perf_lut_en_out = RegInit(false.B)
    val perf_nan_inf_count_en_out = RegInit(false.B)
    val perf_sat_en_out = RegInit(false.B)

  // Register: NVDLA_SDP_D_CVT_OFFSET_0    Field: cvt_offset
    when(nvdla_sdp_d_cvt_offset_0_wren){
        cvt_offset_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_D_CVT_SCALE_0    Field: cvt_scale
    when(nvdla_sdp_d_cvt_scale_0_wren){
        cvt_scale_out := io.reg_wr_data(15,0)
    }

  // Register: NVDLA_SDP_D_CVT_SHIFT_0    Field: cvt_shift
    when(nvdla_sdp_d_cvt_shift_0_wren){
        cvt_shift_out := io.reg_wr_data(5,0)
    }

  // Register: NVDLA_SDP_D_DATA_CUBE_CHANNEL_0    Field: channel
    when(nvdla_sdp_d_data_cube_channel_0_wren){
        channel_out := io.reg_wr_data(12,0)
    }

  // Register: NVDLA_SDP_D_DATA_CUBE_HEIGHT_0    Field: height
    when(nvdla_sdp_d_data_cube_height_0_wren){
        height_out := io.reg_wr_data(12,0)
    }

  // Register: NVDLA_SDP_D_DATA_CUBE_WIDTH_0    Field: width
    when(nvdla_sdp_d_data_cube_width_0_wren){
        width_a_out := io.reg_wr_data(12,0)
    }

  // Register: NVDLA_SDP_D_DATA_FORMAT_0    Field: out_precision
    when(nvdla_sdp_d_data_format_0_wren){
        out_precision_out := io.reg_wr_data(3,2)
    }

  // Register: NVDLA_SDP_D_DATA_FORMAT_0    Field: proc_precision
    when(nvdla_sdp_d_data_format_0_wren){
        proc_precision_out := io.reg_wr_data(1,0)
    }

  // Register: NVDLA_SDP_D_DP_BN_ALU_CFG_0    Field: bn_alu_shift_value
    when(nvdla_sdp_d_dp_bn_alu_cfg_0_wren){
        bn_alu_shift_value_out := io.reg_wr_data(13,8)
    }

  // Register: NVDLA_SDP_D_DP_BN_ALU_CFG_0    Field: bn_alu_src
    when(nvdla_sdp_d_dp_bn_alu_cfg_0_wren){
        bn_alu_src_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DP_BN_ALU_SRC_VALUE_0    Field: bn_alu_operand
    when(nvdla_sdp_d_dp_bn_alu_src_value_0_wren){
        bn_alu_operand_out := io.reg_wr_data(15,0)
    }

  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_alu_algo
    when(nvdla_sdp_d_dp_bn_cfg_0_wren){
        bn_alu_algo_out := io.reg_wr_data(3,2)
    }

  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_alu_bypass
    when(nvdla_sdp_d_dp_bn_cfg_0_wren){
        bn_alu_bypass_out := io.reg_wr_data(1)
    }

  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_bypass
    when(nvdla_sdp_d_dp_bn_cfg_0_wren){
        bn_bypass_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_mul_bypass
    when(nvdla_sdp_d_dp_bn_cfg_0_wren){
        bn_mul_bypass_out := io.reg_wr_data(4)
    }

  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_mul_prelu
    when(nvdla_sdp_d_dp_bn_cfg_0_wren){
        bn_mul_prelu_out := io.reg_wr_data(5)
    }

  // Register: NVDLA_SDP_D_DP_BN_CFG_0    Field: bn_relu_bypass
    when(nvdla_sdp_d_dp_bn_cfg_0_wren){
        bn_relu_bypass_out := io.reg_wr_data(6)
    }

  // Register: NVDLA_SDP_D_DP_BN_MUL_CFG_0    Field: bn_mul_shift_value
    when(nvdla_sdp_d_dp_bn_mul_cfg_0_wren){
        bn_mul_shift_value_out := io.reg_wr_data(15,8)
    }

  // Register: NVDLA_SDP_D_DP_BN_MUL_CFG_0    Field: bn_mul_src
    when(nvdla_sdp_d_dp_bn_mul_cfg_0_wren){
        bn_mul_src_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DP_BN_MUL_SRC_VALUE_0    Field: bn_mul_operand
    when(nvdla_sdp_d_dp_bn_mul_src_value_0_wren){
        bn_mul_operand_out := io.reg_wr_data(15,0)
    }

  // Register: NVDLA_SDP_D_DP_BS_ALU_CFG_0    Field: bs_alu_shift_value
    when(nvdla_sdp_d_dp_bs_alu_cfg_0_wren){
        bs_alu_shift_value_out := io.reg_wr_data(13,8)
    }

  // Register: NVDLA_SDP_D_DP_BS_ALU_CFG_0    Field: bs_alu_src
    when(nvdla_sdp_d_dp_bs_alu_cfg_0_wren){
        bs_alu_src_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DP_BS_ALU_SRC_VALUE_0    Field: bs_alu_operand
    when(nvdla_sdp_d_dp_bs_alu_src_value_0_wren){
        bs_alu_operand_out := io.reg_wr_data(15,0)
    }

  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_alu_algo
    when(nvdla_sdp_d_dp_bs_cfg_0_wren){
        bs_alu_algo_out := io.reg_wr_data(3,2)
    }

  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_alu_bypass
    when(nvdla_sdp_d_dp_bs_cfg_0_wren){
        bs_alu_bypass_out := io.reg_wr_data(1)
    }

  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_bypass
    when(nvdla_sdp_d_dp_bs_cfg_0_wren){
        bs_bypass_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_mul_bypass
    when(nvdla_sdp_d_dp_bs_cfg_0_wren){
        bs_mul_bypass_out := io.reg_wr_data(4)
    }

  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_mul_prelu
    when(nvdla_sdp_d_dp_bs_cfg_0_wren){
        bs_mul_prelu_out := io.reg_wr_data(5)
    }

  // Register: NVDLA_SDP_D_DP_BS_CFG_0    Field: bs_relu_bypass
    when(nvdla_sdp_d_dp_bs_cfg_0_wren){
        bs_relu_bypass_out := io.reg_wr_data(6)
    }

  // Register: NVDLA_SDP_D_DP_BS_MUL_CFG_0    Field: bs_mul_shift_value
    when(nvdla_sdp_d_dp_bs_mul_cfg_0_wren){
        bs_mul_shift_value_out := io.reg_wr_data(15,8)
    }

  // Register: NVDLA_SDP_D_DP_BS_MUL_CFG_0    Field: bs_mul_src
    when(nvdla_sdp_d_dp_bs_mul_cfg_0_wren){
        bs_mul_src_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DP_BS_MUL_SRC_VALUE_0    Field: bs_mul_operand
    when(nvdla_sdp_d_dp_bs_mul_src_value_0_wren){
        bs_mul_operand_out := io.reg_wr_data(15,0)
    }

  // Register: NVDLA_SDP_D_DP_EW_ALU_CFG_0    Field: ew_alu_cvt_bypass
    when(nvdla_sdp_d_dp_ew_alu_cfg_0_wren){
        ew_alu_cvt_bypass_out := io.reg_wr_data(1)
    }

  // Register: NVDLA_SDP_D_DP_EW_ALU_CFG_0    Field: ew_alu_src
    when(nvdla_sdp_d_dp_ew_alu_cfg_0_wren){
        ew_alu_src_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DP_EW_ALU_CVT_OFFSET_VALUE_0    Field: ew_alu_cvt_offset
    when(nvdla_sdp_d_dp_ew_alu_cvt_offset_value_0_wren){
        ew_alu_cvt_offset_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_D_DP_EW_ALU_CVT_SCALE_VALUE_0    Field: ew_alu_cvt_scale
    when(nvdla_sdp_d_dp_ew_alu_cvt_scale_value_0_wren){
        ew_alu_cvt_scale_out := io.reg_wr_data(15,0)
    }

  // Register: NVDLA_SDP_D_DP_EW_ALU_CVT_TRUNCATE_VALUE_0    Field: ew_alu_cvt_truncate
    when(nvdla_sdp_d_dp_ew_alu_cvt_truncate_value_0_wren){
        ew_alu_cvt_truncate_out := io.reg_wr_data(5, 0)
    }

  // Register: NVDLA_SDP_D_DP_EW_ALU_SRC_VALUE_0    Field: ew_alu_operand
    when(nvdla_sdp_d_dp_ew_alu_src_value_0_wren){
        ew_alu_operand_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_alu_algo
    when(nvdla_sdp_d_dp_ew_cfg_0_wren){
        ew_alu_algo_out := io.reg_wr_data(3,2)
    }

  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_alu_bypass
    when(nvdla_sdp_d_dp_ew_cfg_0_wren){
        ew_alu_bypass_out := io.reg_wr_data(1)
    }

  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_bypass
   when(nvdla_sdp_d_dp_ew_cfg_0_wren){
        ew_bypass_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_lut_bypass
    when(nvdla_sdp_d_dp_ew_cfg_0_wren){
        ew_lut_bypass_out := io.reg_wr_data(6)
    }

  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_mul_bypass
    when(nvdla_sdp_d_dp_ew_cfg_0_wren){
        ew_mul_bypass_out := io.reg_wr_data(4)
    }

  // Register: NVDLA_SDP_D_DP_EW_CFG_0    Field: ew_mul_prelu
    when(nvdla_sdp_d_dp_ew_cfg_0_wren){
        ew_mul_prelu_out := io.reg_wr_data(5)
    }

  // Register: NVDLA_SDP_D_DP_EW_MUL_CFG_0    Field: ew_mul_cvt_bypass
    when(nvdla_sdp_d_dp_ew_mul_cfg_0_wren){
        ew_mul_cvt_bypass_out := io.reg_wr_data(1)
    }

  // Register: NVDLA_SDP_D_DP_EW_MUL_CFG_0    Field: ew_mul_src
    when(nvdla_sdp_d_dp_ew_mul_cfg_0_wren){
        ew_mul_src_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DP_EW_MUL_CVT_OFFSET_VALUE_0    Field: ew_mul_cvt_offset
    when(nvdla_sdp_d_dp_ew_mul_cvt_offset_value_0_wren){
        ew_mul_cvt_offset_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_D_DP_EW_MUL_CVT_SCALE_VALUE_0    Field: ew_mul_cvt_scale
    when(nvdla_sdp_d_dp_ew_mul_cvt_scale_value_0_wren){
        ew_mul_cvt_scale_out := io.reg_wr_data(15,0)
    }

  // Register: NVDLA_SDP_D_DP_EW_MUL_CVT_TRUNCATE_VALUE_0    Field: ew_mul_cvt_truncate
    when(nvdla_sdp_d_dp_ew_mul_cvt_truncate_value_0_wren){
        ew_mul_cvt_truncate_out := io.reg_wr_data(5,0)
    }

  // Register: NVDLA_SDP_D_DP_EW_MUL_SRC_VALUE_0    Field: ew_mul_operand
    when(nvdla_sdp_d_dp_ew_mul_src_value_0_wren){
        ew_mul_operand_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_D_DP_EW_TRUNCATE_VALUE_0    Field: ew_truncate
    when(nvdla_sdp_d_dp_ew_truncate_value_0_wren){
        ew_truncate_out := io.reg_wr_data(9,0)
    }

  // Register: NVDLA_SDP_D_DST_BASE_ADDR_HIGH_0    Field: dst_base_addr_high
    when(nvdla_sdp_d_dst_base_addr_high_0_wren){
        dst_base_addr_high_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_D_DST_BASE_ADDR_LOW_0    Field: dst_base_addr_low
    when(nvdla_sdp_d_dst_base_addr_low_0_wren){
        dst_base_addr_low_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_D_DST_BATCH_STRIDE_0    Field: dst_batch_stride
    when(nvdla_sdp_d_dst_batch_stride_0_wren){
        dst_batch_stride_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_D_DST_DMA_CFG_0    Field: dst_ram_type
    when(nvdla_sdp_d_dst_dma_cfg_0_wren){
        dst_ram_type_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_DST_LINE_STRIDE_0    Field: dst_line_stride
    when(nvdla_sdp_d_dst_line_stride_0_wren){
        dst_line_stride_out := io.reg_wr_data
    }

  // Register: NVDLA_SDP_D_DST_SURFACE_STRIDE_0    Field: dst_surface_stride
    when(nvdla_sdp_d_dst_surface_stride_0_wren){
        dst_surface_stride_out := io.reg_wr_data
    }    

  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: batch_number
    when(nvdla_sdp_d_feature_mode_cfg_0_wren){
        batch_number_out := io.reg_wr_data(12,8)
    }

  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: flying_mode
    when(nvdla_sdp_d_feature_mode_cfg_0_wren){
        flying_mode_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: nan_to_zero
    when(nvdla_sdp_d_feature_mode_cfg_0_wren){
        nan_to_zero_out := io.reg_wr_data(3)
    }

  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: output_dst
    when(nvdla_sdp_d_feature_mode_cfg_0_wren){
        output_dst_out := io.reg_wr_data(1)
    }  

  // Register: NVDLA_SDP_D_FEATURE_MODE_CFG_0    Field: winograd
    when(nvdla_sdp_d_feature_mode_cfg_0_wren){
        winograd_out := io.reg_wr_data(2)
    }

  // Not generating flops for field NVDLA_SDP_D_OP_ENABLE_0::op_en (to be implemented outside)

  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_dma_en
    when(nvdla_sdp_d_perf_enable_0_wren){
        perf_dma_en_out := io.reg_wr_data(0)
    }

  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_lut_en
    when(nvdla_sdp_d_perf_enable_0_wren){
        perf_lut_en_out := io.reg_wr_data(1)
    }    

  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_nan_inf_count_en
    when(nvdla_sdp_d_perf_enable_0_wren){
        perf_nan_inf_count_en_out := io.reg_wr_data(3)
    }

  // Register: NVDLA_SDP_D_PERF_ENABLE_0    Field: perf_sat_en
    when(nvdla_sdp_d_perf_enable_0_wren){
        perf_sat_en_out := io.reg_wr_data(2)
    }   

  // Not generating flops for read-only field NVDLA_SDP_D_PERF_LUT_HYBRID_0::lut_hybrid

  // Not generating flops for read-only field NVDLA_SDP_D_PERF_LUT_LE_HIT_0::lut_le_hit

  // Not generating flops for read-only field NVDLA_SDP_D_PERF_LUT_LO_HIT_0::lut_lo_hit

  // Not generating flops for read-only field NVDLA_SDP_D_PERF_LUT_OFLOW_0::lut_oflow

  // Not generating flops for read-only field NVDLA_SDP_D_PERF_LUT_UFLOW_0::lut_uflow

  // Not generating flops for read-only field NVDLA_SDP_D_PERF_OUT_SATURATION_0::out_saturation

  // Not generating flops for read-only field NVDLA_SDP_D_PERF_WDMA_WRITE_STALL_0::wdma_stall

  // Not generating flops for read-only field NVDLA_SDP_D_STATUS_0::status_unequal

  // Not generating flops for read-only field NVDLA_SDP_D_STATUS_INF_INPUT_NUM_0::status_inf_input_num

  // Not generating flops for read-only field NVDLA_SDP_D_STATUS_NAN_INPUT_NUM_0::status_nan_input_num

  // Not generating flops for read-only field NVDLA_SDP_D_STATUS_NAN_OUTPUT_NUM_0::status_nan_output_num


    io.cvt_offset := cvt_offset_out
    io.cvt_scale := cvt_scale_out
    io.cvt_shift := cvt_shift_out
    io.channel := channel_out
    io.height := height_out
    io.width_a := width_a_out
    io.out_precision := out_precision_out
    io.proc_precision := proc_precision_out
    io.bn_alu_shift_value := bn_alu_shift_value_out
    io.bn_alu_src := bn_alu_src_out
    io.bn_alu_operand := bn_alu_operand_out
    io.bn_alu_algo := bn_alu_algo_out
    io.bn_alu_bypass := bn_alu_bypass_out
    io.bn_bypass := bn_bypass_out
    io.bn_mul_bypass := bn_mul_bypass_out
    io.bn_mul_prelu := bn_mul_prelu_out
    io.bn_relu_bypass := bn_relu_bypass_out
    io.bn_mul_shift_value := bn_mul_shift_value_out
    io.bn_mul_src := bn_mul_src_out
    io.bn_mul_operand := bn_mul_operand_out
    io.bs_alu_shift_value := bs_alu_shift_value_out
    io.bs_alu_src := bs_alu_src_out
    io.bs_alu_operand := bs_alu_operand_out
    io.bs_alu_algo := bs_alu_algo_out
    io.bs_alu_bypass := bs_alu_bypass_out
    io.bs_bypass := bs_bypass_out
    io.bs_mul_bypass := bs_mul_bypass_out
    io.bs_mul_prelu := bs_mul_prelu_out
    io.bs_relu_bypass := bs_relu_bypass_out
    io.bs_mul_shift_value := bs_mul_shift_value_out
    io.bs_mul_src := bs_mul_src_out
    io.bs_mul_operand := bs_mul_operand_out

    io.ew_alu_cvt_bypass := ew_alu_cvt_bypass_out
    io.ew_alu_src := ew_alu_src_out
    io.ew_alu_cvt_offset := ew_alu_cvt_offset_out
    io.ew_alu_cvt_scale := ew_alu_cvt_scale_out
    io.ew_alu_cvt_truncate := ew_alu_cvt_truncate_out
    io.ew_alu_operand := ew_alu_operand_out
    io.ew_alu_algo := ew_alu_algo_out
    io.ew_alu_bypass := ew_alu_bypass_out
    io.ew_bypass := ew_bypass_out
    io.ew_lut_bypass := ew_lut_bypass_out
    io.ew_mul_bypass := ew_mul_bypass_out
    io.ew_mul_prelu := ew_mul_prelu_out
    io.ew_mul_cvt_bypass := ew_mul_cvt_bypass_out
    io.ew_mul_src := ew_mul_src_out
    io.ew_mul_cvt_offset := ew_mul_cvt_offset_out
    io.ew_mul_cvt_scale := ew_mul_cvt_scale_out
    io.ew_mul_cvt_truncate := ew_mul_cvt_truncate_out
    io.ew_mul_operand := ew_mul_operand_out
    io.ew_truncate := ew_truncate_out
    io.dst_base_addr_high := dst_base_addr_high_out
    io.dst_base_addr_low := dst_base_addr_low_out
    io.dst_batch_stride := dst_batch_stride_out   
    io.dst_ram_type := dst_ram_type_out
    io.dst_line_stride := dst_line_stride_out
    io.dst_surface_stride := dst_surface_stride_out
    io.batch_number := batch_number_out
    io.flying_mode := flying_mode_out
    io.nan_to_zero := nan_to_zero_out
    io.output_dst := output_dst_out
    io.winograd := winograd_out 
    io.perf_dma_en := perf_dma_en_out
    io.perf_lut_en := perf_lut_en_out
    io.perf_nan_inf_count_en := perf_nan_inf_count_en_out
    io.perf_sat_en := perf_sat_en_out
                                                                   

}}

object NV_NVDLA_SDP_REG_dualDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_REG_dual())
}

