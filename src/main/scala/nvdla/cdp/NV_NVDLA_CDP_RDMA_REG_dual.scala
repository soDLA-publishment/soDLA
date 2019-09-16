package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_RDMA_REG_dual extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg = new reg_control_if

        // Writable register flop/trigger outputs

        val field = new cdp_rdma_reg_dual_flop_outputs
        val op_en_trigger = Output(Bool())

        // Read-only register input
        val op_en = Input(Bool())
        val perf_read_stall = Input(UInt(32.W))
    })
//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 
    withClock(io.nvdla_core_clk){

    // Address decode
    val nvdla_cdp_rdma_d_cya_0_wren = (io.reg.offset === "h40".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_data_cube_channel_0_wren = (io.reg.offset === "h14".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_data_cube_height_0_wren = (io.reg.offset === "h10".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_data_cube_width_0_wren = (io.reg.offset === "h0c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_data_format_0_wren = (io.reg.offset === "h34".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_operation_mode_0_wren = (io.reg.offset === "h30".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_op_enable_0_wren = (io.reg.offset === "h08".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_perf_enable_0_wren = (io.reg.offset === "h38".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_perf_read_stall_0_wren = (io.reg.offset === "h3c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_src_base_addr_high_0_wren = (io.reg.offset === "h1c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_src_base_addr_low_0_wren = (io.reg.offset === "h18".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_src_compression_en_0_wren = (io.reg.offset === "h2c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_src_dma_cfg_0_wren = (io.reg.offset === "h28".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_src_line_stride_0_wren = (io.reg.offset === "h20".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_cdp_rdma_d_src_surface_stride_0_wren = (io.reg.offset === "h24".asUInt(32.W)) & io.reg.wr_en 
    
    val operation_mode = "h0".asUInt(2.W)
    val src_compression_en = "h0".asUInt(1.W)

    io.op_en_trigger := nvdla_cdp_rdma_d_op_enable_0_wren

    // Output mux

    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq( 
    //nvdla_cdp_rdma_d_cya_0_out     
    "h40".asUInt(32.W)  -> io.field.cya,
    //nvdla_cdp_rdma_d_data_cube_channel_0_out
    "h14".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.channel),
    //nvdla_cdp_rdma_d_data_cube_height_0_out
    "h10".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.height),
    //nvdla_cdp_rdma_d_data_cube_width_0_out
    "h0c".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.cdp_width),
    //nvdla_cdp_rdma_d_data_format_0_out
    "h34".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.field.input_data),
    //nvdla_cdp_rdma_d_operation_mode_0_out
    "h30".asUInt(32.W)  -> Cat("b0".asUInt(30.W), operation_mode),
    //nvdla_cdp_rdma_d_op_enable_0_out
    "h08".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.op_en),
    //nvdla_cdp_rdma_d_perf_enable_0_out
    "h38".asUInt(32.W)  ->  Cat("b0".asUInt(31.W), io.field.dma_en),
    //nvdla_cdp_rdma_d_perf_read_stall_0_out
    "h3c".asUInt(32.W)  -> io.perf_read_stall,
    //nvdla_cdp_rdma_d_src_base_addr_high_0_out
    "h1c".asUInt(32.W)  -> io.field.src_base_addr_high,
    //nvdla_cdp_rdma_d_src_base_addr_low_0_out
    "h18".asUInt(32.W)  -> io.field.src_base_addr_low,
    //nvdla_cdp_rdma_d_src_compression_en_0_out
    "h2c".asUInt(32.W)  -> Cat("b0".asUInt(31.W), src_compression_en),
    //nvdla_cdp_rdma_d_src_dma_cfg_0_out
    "h28".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.field.src_ram_type),
    //nvdla_cdp_rdma_d_src_line_stride_0_out
    "h20".asUInt(32.W)  -> io.field.src_line_stride,
    //nvdla_cdp_rdma_d_src_surface_stride_0_out
    "h24".asUInt(32.W)  -> io.field.src_surface_stride
    ))

    // Register flop declarations

    // Register: NVDLA_CDP_RDMA_D_CYA_0    Field: cya
    io.field.cya := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_rdma_d_cya_0_wren)
    // Register: NVDLA_CDP_RDMA_D_DATA_CUBE_CHANNEL_0    Field: channel
    io.field.channel := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_cdp_rdma_d_data_cube_channel_0_wren)
    // Register: NVDLA_CDP_RDMA_D_DATA_CUBE_HEIGHT_0    Field: height
    io.field.height := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_cdp_rdma_d_data_cube_height_0_wren)
    // Register: NVDLA_CDP_RDMA_D_DATA_CUBE_WIDTH_0    Field: width
    io.field.cdp_width := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_cdp_rdma_d_data_cube_width_0_wren)
    // Register: NVDLA_CDP_RDMA_D_DATA_FORMAT_0    Field: input_data
    io.field.input_data := RegEnable(io.reg.wr_data(1, 0), "b0".asUInt(2.W), nvdla_cdp_rdma_d_data_format_0_wren)
    // Register: NVDLA_CDP_RDMA_D_PERF_ENABLE_0    Field: dma_en
    io.field.dma_en := RegEnable(io.reg.wr_data(0), false.B, nvdla_cdp_rdma_d_perf_enable_0_wren)
    // Register: NVDLA_CDP_RDMA_D_SRC_BASE_ADDR_HIGH_0    Field: src_base_addr_high
    io.field.src_base_addr_high := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_rdma_d_src_base_addr_high_0_wren)
    // Register: NVDLA_CDP_RDMA_D_SRC_BASE_ADDR_LOW_0    Field: src_base_addr_low
    io.field.src_base_addr_low := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_rdma_d_src_base_addr_low_0_wren)
    // Register: NVDLA_CDP_RDMA_D_SRC_DMA_CFG_0    Field: src_ram_type
    io.field.src_ram_type := RegEnable(io.reg.wr_data(0), false.B, nvdla_cdp_rdma_d_src_dma_cfg_0_wren)
    // Register: NVDLA_CDP_RDMA_D_SRC_LINE_STRIDE_0    Field: src_line_stride
    io.field.src_line_stride := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_rdma_d_src_line_stride_0_wren)
    // Register: NVDLA_CDP_RDMA_D_SRC_SURFACE_STRIDE_0    Field: src_surface_stride
    io.field.src_surface_stride := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cdp_rdma_d_src_surface_stride_0_wren)

}}
