package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_RDMA_REG_dual extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        //Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)
        val reg_wr_en = Input(Bool())

        //Writable register flop/trigger outputs
        val bn_base_addr_high = Output(UInt(32.W))
        val bn_base_addr_low = Output(UInt(32.W))
        val bn_batch_stride = Output(UInt(32.W))
        val bn_line_stride = Output(UInt(32.W))
        val bn_surface_stride = Output(UInt(32.W))
        val brdma_data_mode = Output(Bool())
        val brdma_data_size = Output(Bool())
        val brdma_data_use = Output(UInt(2.W))
        val brdma_disable = Output(Bool())
        val brdma_ram_type = Output(Bool())
        val bs_base_addr_high = Output(UInt(32.W))
        val bs_base_addr_low = Output(UInt(32.W))
        val bs_batch_stride = Output(UInt(32.W))
        val bs_line_stride = Output(UInt(32.W))
        val bs_surface_stride = Output(UInt(32.W))
        val channel = Output(UInt(13.W))
        val height = Output(UInt(13.W))
        val width_a = Output(UInt(13.W))
        val erdma_data_mode = Output(Bool())
        val erdma_data_size = Output(Bool())
        val erdma_data_use = Output(UInt(2.W))
        val erdma_disable = Output(Bool())
        val erdma_ram_type = Output(Bool())
        val ew_base_addr_high = Output(UInt(32.W))
        val ew_base_addr_low = Output(UInt(32.W))
        val ew_batch_stride = Output(UInt(32.W))
        val ew_line_stride = Output(UInt(32.W))
        val ew_surface_stride = Output(UInt(32.W))
        val batch_number = Output(UInt(5.W))
        val flying_mode = Output(Bool())
        val in_precision = Output(UInt(2.W))
        val out_precision = Output(UInt(2.W))
        val proc_precision = Output(UInt(2.W))
        val winograd = Output(Bool())
        val nrdma_data_mode = Output(Bool())
        val nrdma_data_size = Output(Bool())
        val nrdma_data_use = Output(UInt(2.W))
        val nrdma_disable = Output(Bool())
        val nrdma_ram_type = Output(Bool())
        val op_en_trigger = Output(Bool())
        val perf_dma_en = Output(Bool())
        val perf_nan_inf_count_en = Output(Bool())
        val src_base_addr_high = Output(UInt(32.W))
        val src_base_addr_low = Output(UInt(32.W))
        val src_ram_type = Output(Bool())
        val src_line_stride = Output(UInt(32.W))
        val src_surface_stride = Output(UInt(32.W))

        //Read-only register inputs
        val op_en = Input(Bool())
        val brdma_stall = Input(UInt(32.W))
        val erdma_stall = Input(UInt(32.W))
        val mrdma_stall = Input(UInt(32.W))
        val nrdma_stall = Input(UInt(32.W))
        val status_inf_input_num = Input(UInt(32.W))
        val status_nan_input_num = Input(UInt(32.W)) 
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
    val nvdla_sdp_rdma_d_bn_base_addr_high_0_wren = (io.reg_offset === "h48".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_bn_base_addr_low_0_wren = (io.reg_offset ===  "h44".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_bn_batch_stride_0_wren = (io.reg_offset ===  "h54".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_bn_line_stride_0_wren = (io.reg_offset ===  "h4c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_bn_surface_stride_0_wren = (io.reg_offset === "h50".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_brdma_cfg_0_wren = (io.reg_offset === "h28".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_bs_base_addr_high_0_wren = (io.reg_offset === "h30".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_bs_base_addr_low_0_wren = (io.reg_offset === "h2c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_bs_batch_stride_0_wren = (io.reg_offset === "h3c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_bs_line_stride_0_wren = (io.reg_offset === "h34".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_bs_surface_stride_0_wren = (io.reg_offset === "h38".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_data_cube_channel_0_wren = (io.reg_offset === "h14".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_data_cube_height_0_wren = (io.reg_offset === "h10".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_data_cube_width_a_0_wren = (io.reg_offset === "h0c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_erdma_cfg_0_wren = (io.reg_offset === "h58".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_ew_base_addr_high_0_wren = (io.reg_offset === "h60".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_ew_base_addr_low_0_wren = (io.reg_offset === "h5c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_ew_batch_stride_0_wren = (io.reg_offset === "h6c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_ew_line_stride_0_wren = (io.reg_offset === "h64".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_ew_surface_stride_0_wren = (io.reg_offset === "h68".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_feature_mode_cfg_0_wren = (io.reg_offset === "h70".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_nrdma_cfg_0_wren = (io.reg_offset === "h40".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_op_enable_0_wren = (io.reg_offset === "h08".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_perf_brdma_read_stall_0_wren = (io.reg_offset === "h88".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_perf_enable_0_wren = (io.reg_offset === "h80".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_perf_erdma_read_stall_0_wren = (io.reg_offset === "h90".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_perf_mrdma_read_stall_0_wren = (io.reg_offset === "h84".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_perf_nrdma_read_stall_0_wren = (io.reg_offset === "h8c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_src_base_addr_high_0_wren = (io.reg_offset === "h1c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_src_base_addr_low_0_wren = (io.reg_offset === "h18".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_src_dma_cfg_0_wren = (io.reg_offset === "h74".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_src_line_stride_0_wren = (io.reg_offset === "h20".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_src_surface_stride_0_wren = (io.reg_offset === "h24".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_status_inf_input_num_0_wren = (io.reg_offset === "h7c".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)
    val nvdla_sdp_rdma_d_status_nan_input_num_0_wren = (io.reg_offset === "h78".asUInt(32.W)) & io.reg_wr_en ;  //spyglass disable UnloadedNet-ML //(W528)

    val nvdla_sdp_rdma_d_bn_base_addr_high_0_out = io.bn_base_addr_high
    val nvdla_sdp_rdma_d_bn_base_addr_low_0_out = io.bn_base_addr_low
    val nvdla_sdp_rdma_d_bn_batch_stride_0_out = io.bn_batch_stride
    val nvdla_sdp_rdma_d_bn_line_stride_0_out = io.bn_line_stride
    val nvdla_sdp_rdma_d_bn_surface_stride_0_out =  io.bn_surface_stride
    val nvdla_sdp_rdma_d_brdma_cfg_0_out = Cat("b0".asUInt(26.W), io.brdma_ram_type, io.brdma_data_mode, io.brdma_data_size, io.brdma_data_use, io.brdma_disable)
    val nvdla_sdp_rdma_d_bs_base_addr_high_0_out = io.bs_base_addr_high
    val nvdla_sdp_rdma_d_bs_base_addr_low_0_out = io.bs_base_addr_low
    val nvdla_sdp_rdma_d_bs_batch_stride_0_out = io.bs_batch_stride
    val nvdla_sdp_rdma_d_bs_line_stride_0_out = io.bs_line_stride
    val nvdla_sdp_rdma_d_bs_surface_stride_0_out = io.bs_surface_stride
    val nvdla_sdp_rdma_d_data_cube_channel_0_out = Cat("b0".asUInt(19.W), io.channel)
    val nvdla_sdp_rdma_d_data_cube_height_0_out = Cat("b0".asUInt(19.W), io.height)
    val nvdla_sdp_rdma_d_data_cube_width_a_0_out = Cat("b0".asUInt(19.W), io.width_a)
    val nvdla_sdp_rdma_d_erdma_cfg_0_out = Cat("b0".asUInt(26.W), io.erdma_ram_type, io.erdma_data_mode, io.erdma_data_size, io.erdma_data_use, io.erdma_disable)
    val nvdla_sdp_rdma_d_ew_base_addr_high_0_out = io.ew_base_addr_high
    val nvdla_sdp_rdma_d_ew_base_addr_low_0_out = io.ew_base_addr_low
    val nvdla_sdp_rdma_d_ew_batch_stride_0_out = io.ew_batch_stride
    val nvdla_sdp_rdma_d_ew_line_stride_0_out = io.ew_line_stride
    val nvdla_sdp_rdma_d_ew_surface_stride_0_out = io.ew_surface_stride
    val nvdla_sdp_rdma_d_feature_mode_cfg_0_out = Cat("b0".asUInt(19.W), io.batch_number, io.out_precision, io.proc_precision, io.in_precision, io.winograd, io.flying_mode)
    val nvdla_sdp_rdma_d_nrdma_cfg_0_out = Cat("b0".asUInt(26.W), io.nrdma_ram_type, io.nrdma_data_mode, io.nrdma_data_size, io.nrdma_data_use, io.nrdma_disable)
    val nvdla_sdp_rdma_d_op_enable_0_out = Cat("b0".asUInt(31.W), io.op_en)
    val nvdla_sdp_rdma_d_perf_brdma_read_stall_0_out = io.brdma_stall
    val nvdla_sdp_rdma_d_perf_enable_0_out = Cat("b0".asUInt(30.W), io.perf_nan_inf_count_en, io.perf_dma_en)
    val nvdla_sdp_rdma_d_perf_erdma_read_stall_0_out = io.erdma_stall
    val nvdla_sdp_rdma_d_perf_mrdma_read_stall_0_out = io.mrdma_stall
    val nvdla_sdp_rdma_d_perf_nrdma_read_stall_0_out = io.nrdma_stall
    val nvdla_sdp_rdma_d_src_base_addr_high_0_out = io.src_base_addr_high
    val nvdla_sdp_rdma_d_src_base_addr_low_0_out= io.src_base_addr_low
    val nvdla_sdp_rdma_d_src_dma_cfg_0_out = Cat("b0".asUInt(31.W), io.src_ram_type)
    val nvdla_sdp_rdma_d_src_line_stride_0_out = io.src_line_stride
    val nvdla_sdp_rdma_d_src_surface_stride_0_out = io.src_surface_stride
    val nvdla_sdp_rdma_d_status_inf_input_num_0_out = io.status_inf_input_num
    val nvdla_sdp_rdma_d_status_nan_input_num_0_out = io.status_nan_input_num

    io.op_en_trigger := nvdla_sdp_rdma_d_op_enable_0_wren

    //Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
    "h48".asUInt(32.W)  -> nvdla_sdp_rdma_d_bn_base_addr_high_0_out,
    "h44".asUInt(32.W)  -> nvdla_sdp_rdma_d_bn_base_addr_low_0_out,
    "h54".asUInt(32.W)  -> nvdla_sdp_rdma_d_bn_batch_stride_0_out,
    "h4c".asUInt(32.W)  -> nvdla_sdp_rdma_d_bn_line_stride_0_out,
    "h50".asUInt(32.W)  -> nvdla_sdp_rdma_d_bn_surface_stride_0_out,
    "h28".asUInt(32.W)  -> nvdla_sdp_rdma_d_brdma_cfg_0_out,
    "h30".asUInt(32.W)  -> nvdla_sdp_rdma_d_bs_base_addr_high_0_out,
    "h2c".asUInt(32.W)  -> nvdla_sdp_rdma_d_bs_base_addr_low_0_out,
    "h3c".asUInt(32.W)  -> nvdla_sdp_rdma_d_bs_batch_stride_0_out,
    "h34".asUInt(32.W)  -> nvdla_sdp_rdma_d_bs_line_stride_0_out,
    "h38".asUInt(32.W)  -> nvdla_sdp_rdma_d_bs_surface_stride_0_out,
    "h14".asUInt(32.W)  -> nvdla_sdp_rdma_d_data_cube_channel_0_out,
    "h10".asUInt(32.W)  -> nvdla_sdp_rdma_d_data_cube_height_0_out, 
    "h0c".asUInt(32.W)  -> nvdla_sdp_rdma_d_data_cube_width_a_0_out, 
    "h58".asUInt(32.W)  -> nvdla_sdp_rdma_d_erdma_cfg_0_out, 
    "h60".asUInt(32.W)  -> nvdla_sdp_rdma_d_ew_base_addr_high_0_out, 
    "h5c".asUInt(32.W)  -> nvdla_sdp_rdma_d_ew_base_addr_low_0_out, 
    "h6c".asUInt(32.W)  -> nvdla_sdp_rdma_d_ew_batch_stride_0_out, 
    "h64".asUInt(32.W)  -> nvdla_sdp_rdma_d_ew_line_stride_0_out, 
    "h68".asUInt(32.W)  -> nvdla_sdp_rdma_d_ew_surface_stride_0_out, 
    "h70".asUInt(32.W)  -> nvdla_sdp_rdma_d_feature_mode_cfg_0_out, 
    "h40".asUInt(32.W)  -> nvdla_sdp_rdma_d_nrdma_cfg_0_out, 
    "h08".asUInt(32.W)  -> nvdla_sdp_rdma_d_op_enable_0_out,
    "h88".asUInt(32.W)  -> nvdla_sdp_rdma_d_perf_brdma_read_stall_0_out,
    "h80".asUInt(32.W)  -> nvdla_sdp_rdma_d_perf_enable_0_out,
    "h90".asUInt(32.W)  -> nvdla_sdp_rdma_d_perf_erdma_read_stall_0_out,
    "h84".asUInt(32.W)  -> nvdla_sdp_rdma_d_perf_mrdma_read_stall_0_out,
    "h8c".asUInt(32.W)  -> nvdla_sdp_rdma_d_perf_nrdma_read_stall_0_out,
    "h1c".asUInt(32.W)  -> nvdla_sdp_rdma_d_src_base_addr_high_0_out,
    "h18".asUInt(32.W)  -> nvdla_sdp_rdma_d_src_base_addr_low_0_out,
    "h74".asUInt(32.W)  -> nvdla_sdp_rdma_d_src_dma_cfg_0_out,
    "h20".asUInt(32.W)  -> nvdla_sdp_rdma_d_src_line_stride_0_out,
    "h24".asUInt(32.W)  -> nvdla_sdp_rdma_d_src_surface_stride_0_out,
    "h7c".asUInt(32.W)  -> nvdla_sdp_rdma_d_status_inf_input_num_0_out,
    "h78".asUInt(32.W)  -> nvdla_sdp_rdma_d_status_nan_input_num_0_out                                                                              
    ))

    //Register flop declarations

    val bn_base_addr_high_out = RegInit("b0".asUInt(32.W))
    val bn_base_addr_low_out = RegInit("b0".asUInt(32.W))
    val bn_batch_stride_out = RegInit("b0".asUInt(32.W))
    val bn_line_stride_out = RegInit("b0".asUInt(32.W))
    val bn_surface_stride_out = RegInit("b0".asUInt(32.W))
    val brdma_data_mode_out = RegInit(false.B)
    val brdma_data_size_out = RegInit(false.B)
    val brdma_data_use_out = RegInit("b00".asUInt(2.W))
    val brdma_disable_out = RegInit(true.B)
    val brdma_ram_type_out = RegInit(false.B)
    val bs_base_addr_high_out = RegInit("b0".asUInt(32.W))
    val bs_base_addr_low_out = RegInit("b0".asUInt(32.W))
    val bs_batch_stride_out = RegInit("b0".asUInt(32.W))
    val bs_line_stride_out = RegInit("b0".asUInt(32.W))
    val bs_surface_stride_out = RegInit("b0".asUInt(32.W))
    val channel_out = RegInit("b0".asUInt(13.W))
    val height_out = RegInit("b0".asUInt(13.W))
    val width_a_out = RegInit("b0".asUInt(13.W))
    val erdma_data_mode_out = RegInit(false.B)
    val erdma_data_size_out = RegInit(false.B)
    val erdma_data_use_out = RegInit("b00".asUInt(2.W))
    val erdma_disable_out = RegInit(true.B)
    val erdma_ram_type_out = RegInit(false.B)
    val ew_base_addr_high_out = RegInit("b0".asUInt(32.W))
    val ew_base_addr_low_out = RegInit("b0".asUInt(32.W))
    val ew_batch_stride_out = RegInit("b0".asUInt(32.W))
    val ew_line_stride_out = RegInit("b0".asUInt(32.W))
    val ew_surface_stride_out = RegInit("b0".asUInt(32.W))
    val batch_number_out = RegInit("b0".asUInt(32.W))
    val flying_mode_out = RegInit(false.B)
    val in_precision_out = RegInit("b01".asUInt(2.W))
    val out_precision_out = RegInit("b00".asUInt(2.W))
    val proc_precision_out = RegInit("b01".asUInt(2.W))
    val winograd_out = RegInit(false.B)
    val nrdma_data_mode_out = RegInit(false.B)
    val nrdma_data_size_out = RegInit(false.B)
    val nrdma_data_use_out = RegInit("b00".asUInt(2.W))
    val nrdma_disable_out = RegInit(true.B)
    val nrdma_ram_type_out = RegInit(false.B)
    val perf_dma_en_out = RegInit(false.B)
    val perf_nan_inf_count_en_out = RegInit(false.B)
    val src_base_addr_high_out = RegInit("b0".asUInt(32.W))
    val src_base_addr_low_out = RegInit("b0".asUInt(32.W))
    val src_ram_type_out = RegInit(false.B)
    val src_line_stride_out = RegInit("b0".asUInt(32.W))
    val src_surface_stride_out = RegInit("b0".asUInt(32.W))

    // Register: NVDLA_SDP_RDMA_D_BN_BASE_ADDR_HIGH_0    Field: bn_base_addr_high
    when(nvdla_sdp_rdma_d_bn_base_addr_high_0_wren){
        bn_base_addr_high_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_BN_BASE_ADDR_LOW_0    Field: bn_base_addr_low
    when(nvdla_sdp_rdma_d_bn_base_addr_low_0_wren){
        bn_base_addr_low_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_BN_BATCH_STRIDE_0    Field: bn_batch_stride
    when(nvdla_sdp_rdma_d_bn_batch_stride_0_wren){
        bn_batch_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_BN_LINE_STRIDE_0    Field: bn_line_stride
    when(nvdla_sdp_rdma_d_bn_line_stride_0_wren){
        bn_line_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_BN_SURFACE_STRIDE_0    Field: bn_surface_stride
    when(nvdla_sdp_rdma_d_bn_surface_stride_0_wren){
        bn_surface_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_BRDMA_CFG_0    Field: brdma_data_mode
    when(nvdla_sdp_rdma_d_brdma_cfg_0_wren){
        brdma_data_mode_out := io.reg_wr_data(4)
    }

    // Register: NVDLA_SDP_RDMA_D_BRDMA_CFG_0    Field: brdma_data_size
    when(nvdla_sdp_rdma_d_brdma_cfg_0_wren){
        brdma_data_size_out := io.reg_wr_data(3)
    }

    // Register: NVDLA_SDP_RDMA_D_BRDMA_CFG_0    Field: brdma_data_use
    when(nvdla_sdp_rdma_d_brdma_cfg_0_wren){
        brdma_data_use_out := io.reg_wr_data(2, 1)
    }

    // Register: NVDLA_SDP_RDMA_D_BRDMA_CFG_0    Field: brdma_disable
    when(nvdla_sdp_rdma_d_brdma_cfg_0_wren){
        brdma_disable_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_SDP_RDMA_D_BRDMA_CFG_0    Field: brdma_ram_type
    when(nvdla_sdp_rdma_d_brdma_cfg_0_wren){
        brdma_ram_type_out := io.reg_wr_data(5)
    }

    // Register: NVDLA_SDP_RDMA_D_BS_BASE_ADDR_HIGH_0    Field: bs_base_addr_high
    when(nvdla_sdp_rdma_d_bs_base_addr_high_0_wren){
        bs_base_addr_high_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_BS_BASE_ADDR_LOW_0    Field: bs_base_addr_low
    when(nvdla_sdp_rdma_d_bs_base_addr_low_0_wren){
        bs_base_addr_low_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_BS_BATCH_STRIDE_0    Field: bs_batch_stride
    when(nvdla_sdp_rdma_d_bs_batch_stride_0_wren){
        bs_batch_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_BS_LINE_STRIDE_0    Field: bs_line_stride
    when(nvdla_sdp_rdma_d_bs_line_stride_0_wren){
        bs_line_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_BS_SURFACE_STRIDE_0    Field: bs_surface_stride
    when(nvdla_sdp_rdma_d_bs_surface_stride_0_wren){
        bs_surface_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_DATA_CUBE_CHANNEL_0    Field: channel
    when(nvdla_sdp_rdma_d_data_cube_channel_0_wren){
        channel_out := io.reg_wr_data(12, 0)
    }

    // Register: NVDLA_SDP_RDMA_D_DATA_CUBE_HEIGHT_0    Field: height
    when(nvdla_sdp_rdma_d_data_cube_height_0_wren){
        height_out := io.reg_wr_data(12, 0)
    }

    // Register: NVDLA_SDP_RDMA_D_DATA_CUBE_width_a_0    Field: width_a
    when(nvdla_sdp_rdma_d_data_cube_width_a_0_wren){
        width_a_out := io.reg_wr_data(12, 0)
    }

    // Register: NVDLA_SDP_RDMA_D_ERDMA_CFG_0    Field: erdma_data_mode
    when(nvdla_sdp_rdma_d_erdma_cfg_0_wren){
        erdma_data_mode_out := io.reg_wr_data(4)
    }

    // Register: NVDLA_SDP_RDMA_D_ERDMA_CFG_0    Field: erdma_data_size
    when(nvdla_sdp_rdma_d_erdma_cfg_0_wren){
        erdma_data_size_out := io.reg_wr_data(3)
    }

    // Register: NVDLA_SDP_RDMA_D_ERDMA_CFG_0    Field: erdma_data_use
    when(nvdla_sdp_rdma_d_erdma_cfg_0_wren){
        erdma_data_use_out := io.reg_wr_data(2, 1)
    }

    // Register: NVDLA_SDP_RDMA_D_ERDMA_CFG_0    Field: erdma_disable
    when(nvdla_sdp_rdma_d_erdma_cfg_0_wren){
        erdma_disable_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_SDP_RDMA_D_ERDMA_CFG_0    Field: erdma_ram_type
    when(nvdla_sdp_rdma_d_erdma_cfg_0_wren){
        erdma_ram_type_out := io.reg_wr_data(5)
    }

    // Register: NVDLA_SDP_RDMA_D_EW_BASE_ADDR_HIGH_0    Field: ew_base_addr_high
    when(nvdla_sdp_rdma_d_ew_base_addr_high_0_wren){
        ew_base_addr_high_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_EW_BASE_ADDR_LOW_0    Field: ew_base_addr_low
    when(nvdla_sdp_rdma_d_ew_base_addr_low_0_wren){
        ew_base_addr_low_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_EW_BATCH_STRIDE_0    Field: ew_batch_stride
    when(nvdla_sdp_rdma_d_ew_batch_stride_0_wren){
        ew_batch_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_EW_LINE_STRIDE_0    Field: ew_line_stride
    when(nvdla_sdp_rdma_d_ew_line_stride_0_wren){
        ew_line_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_EW_SURFACE_STRIDE_0    Field: ew_surface_stride
    when(nvdla_sdp_rdma_d_ew_surface_stride_0_wren){
        ew_surface_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_FEATURE_MODE_CFG_0    Field: batch_number
    when(nvdla_sdp_rdma_d_feature_mode_cfg_0_wren){
        batch_number_out := io.reg_wr_data(12, 8)
    }

    // Register: NVDLA_SDP_RDMA_D_FEATURE_MODE_CFG_0    Field: flying_mode
    when(nvdla_sdp_rdma_d_feature_mode_cfg_0_wren){
        flying_mode_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_SDP_RDMA_D_FEATURE_MODE_CFG_0    Field: in_precision
    when(nvdla_sdp_rdma_d_feature_mode_cfg_0_wren){
        in_precision_out := io.reg_wr_data(3, 2)
    }

    // Register: NVDLA_SDP_RDMA_D_FEATURE_MODE_CFG_0    Field: out_precision
    when(nvdla_sdp_rdma_d_feature_mode_cfg_0_wren){
        out_precision_out := io.reg_wr_data(7, 6)
    }

    // Register: NVDLA_SDP_RDMA_D_FEATURE_MODE_CFG_0    Field: proc_precision
    when(nvdla_sdp_rdma_d_feature_mode_cfg_0_wren){
        proc_precision_out := io.reg_wr_data(5, 4)
    }

    // Register: NVDLA_SDP_RDMA_D_FEATURE_MODE_CFG_0    Field: winograd
    when(nvdla_sdp_rdma_d_feature_mode_cfg_0_wren){
        winograd_out := io.reg_wr_data(1)
    }

    // Register: NVDLA_SDP_RDMA_D_NRDMA_CFG_0    Field: nrdma_data_mode
    when(nvdla_sdp_rdma_d_nrdma_cfg_0_wren){
        nrdma_data_mode_out := io.reg_wr_data(4)
    }

    // Register: NVDLA_SDP_RDMA_D_NRDMA_CFG_0    Field: nrdma_data_size
    when(nvdla_sdp_rdma_d_nrdma_cfg_0_wren){
        nrdma_data_size_out := io.reg_wr_data(3)
    }

    // Register: NVDLA_SDP_RDMA_D_NRDMA_CFG_0    Field: nrdma_data_use
    when(nvdla_sdp_rdma_d_nrdma_cfg_0_wren){
        nrdma_data_use_out := io.reg_wr_data(2, 1)
    }

    // Register: NVDLA_SDP_RDMA_D_NRDMA_CFG_0    Field: nrdma_disable
    when(nvdla_sdp_rdma_d_nrdma_cfg_0_wren){
        nrdma_disable_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_SDP_RDMA_D_NRDMA_CFG_0    Field: nrdma_ram_type
    when(nvdla_sdp_rdma_d_nrdma_cfg_0_wren){
        nrdma_ram_type_out := io.reg_wr_data(5)
    }

    // Not generating flops for field NVDLA_SDP_RDMA_D_OP_ENABLE_0::op_en (to be implemented outside)

    // Not generating flops for read-only field NVDLA_SDP_RDMA_D_PERF_BRDMA_READ_STALL_0::brdma_stall

    // Register: NVDLA_SDP_RDMA_D_PERF_ENABLE_0    Field: perf_dma_en
    when(nvdla_sdp_rdma_d_perf_enable_0_wren){
        perf_dma_en_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_SDP_RDMA_D_PERF_ENABLE_0    Field: perf_nan_inf_count_en
   when(nvdla_sdp_rdma_d_perf_enable_0_wren){
        perf_nan_inf_count_en_out := io.reg_wr_data(1)
    }

    // Not generating flops for read-only field NVDLA_SDP_RDMA_D_PERF_ERDMA_READ_STALL_0::erdma_stall

    // Not generating flops for read-only field NVDLA_SDP_RDMA_D_PERF_MRDMA_READ_STALL_0::mrdma_stall

    // Not generating flops for read-only field NVDLA_SDP_RDMA_D_PERF_NRDMA_READ_STALL_0::nrdma_stall

    // Register: NVDLA_SDP_RDMA_D_SRC_BASE_ADDR_HIGH_0    Field: src_base_addr_high
    when(nvdla_sdp_rdma_d_src_base_addr_high_0_wren){
        src_base_addr_high_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_SRC_BASE_ADDR_LOW_0    Field: src_base_addr_low
    when(nvdla_sdp_rdma_d_src_base_addr_low_0_wren){
        src_base_addr_low_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_SRC_DMA_CFG_0    Field: src_ram_type
    when(nvdla_sdp_rdma_d_src_dma_cfg_0_wren){
        src_ram_type_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_SDP_RDMA_D_SRC_LINE_STRIDE_0    Field: src_line_stride
    when(nvdla_sdp_rdma_d_src_line_stride_0_wren){
        src_line_stride_out := io.reg_wr_data
    }

    // Register: NVDLA_SDP_RDMA_D_SRC_SURFACE_STRIDE_0    Field: src_surface_stride
    when(nvdla_sdp_rdma_d_src_surface_stride_0_wren){
        src_surface_stride_out := io.reg_wr_data
    }

    // Not generating flops for read-only field NVDLA_SDP_RDMA_D_STATUS_INF_INPUT_NUM_0::status_inf_input_num

    // Not generating flops for read-only field NVDLA_SDP_RDMA_D_STATUS_NAN_INPUT_NUM_0::status_nan_input_num


    io.bn_base_addr_high := bn_base_addr_high_out
    io.bn_base_addr_low := bn_base_addr_low_out
    io.bn_batch_stride := bn_batch_stride_out
    io.bn_line_stride := bn_line_stride_out
    io.bn_surface_stride := bn_surface_stride_out
    io.brdma_data_mode := brdma_data_mode_out
    io.brdma_data_size := brdma_data_size_out
    io.brdma_data_use := brdma_data_use_out
    io.brdma_disable := brdma_disable_out
    io.brdma_ram_type := brdma_ram_type_out
    io.bs_base_addr_high := bs_base_addr_high_out
    io.bs_base_addr_low := bs_base_addr_low_out
    io.bs_batch_stride := bs_batch_stride_out
    io.bs_line_stride := bs_line_stride_out
    io.bs_surface_stride := bs_surface_stride_out
    io.channel := channel_out
    io.height := height_out
    io.width_a := width_a_out
    io.erdma_data_mode := erdma_data_mode_out
    io.erdma_data_size := erdma_data_size_out
    io.erdma_data_use := erdma_data_use_out
    io.erdma_disable := erdma_disable_out
    io.erdma_ram_type := erdma_ram_type_out
    io.ew_base_addr_high := ew_base_addr_high_out
    io.ew_base_addr_low := ew_base_addr_low_out
    io.ew_batch_stride := ew_batch_stride_out
    io.ew_line_stride := ew_line_stride_out
    io.ew_surface_stride := ew_surface_stride_out
    io.batch_number := batch_number_out
    io.flying_mode := flying_mode_out
    io.in_precision := in_precision_out
    io.out_precision := out_precision_out
    io.proc_precision := proc_precision_out
    io.winograd := winograd_out
    io.nrdma_data_mode := nrdma_data_mode_out
    io.nrdma_data_size := nrdma_data_size_out
    io.nrdma_data_use := nrdma_data_use_out
    io.nrdma_disable := nrdma_disable_out
    io.nrdma_ram_type := nrdma_ram_type_out
    io.perf_dma_en := perf_dma_en_out
    io.perf_nan_inf_count_en := perf_nan_inf_count_en_out
    io.src_base_addr_high := src_base_addr_high_out
    io.src_base_addr_low := src_base_addr_low_out
    io.src_ram_type := src_ram_type_out
    io.src_line_stride := src_line_stride_out
    io.src_surface_stride := src_surface_stride_out                                                               

}}

object NV_NVDLA_SDP_RDMA_REG_dualDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_REG_dual())
}