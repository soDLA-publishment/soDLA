package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_BDMA_reg extends Module{
    val io = IO(new Bundle{
        // clk
        val nvdla_core_clk = Input(Clock())

        //Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)
        val reg_wr_en = Input(Bool())

        //Writable register flop/trigger outputs
        val nvdla_bdma_cfg_cmd_0_dst_ram_type = Output(Bool())
        val nvdla_bdma_cfg_cmd_0_src_ram_type = Output(Bool())
        val nvdla_bdma_cfg_dst_addr_high_0_v8 = Output(UInt(32.W))
        val nvdla_bdma_cfg_dst_addr_low_0_v32 = Output(UInt(27.W))
        val nvdla_bdma_cfg_dst_line_0_stride = Output(UInt(27.W))
        val nvdla_bdma_cfg_dst_surf_0_stride = Output(UInt(27.W))
        val nvdla_bdma_cfg_launch0_0_grp0_launch = Output(Bool())
        val nvdla_bdma_cfg_launch0_0_grp0_launch_trigger = Output(Bool())
        val nvdla_bdma_cfg_launch1_0_grp1_launch = Output(Bool())
        val nvdla_bdma_cfg_launch1_0_grp1_launch_trigger = Output(Bool())
        val nvdla_bdma_cfg_line_0_size = Output(UInt(13.W))
        val nvdla_bdma_cfg_line_repeat_0_number = Output(UInt(24.W))
        val nvdla_bdma_cfg_op_0_en = Output(Bool())
        val nvdla_bdma_cfg_op_0_en_trigger = Output(Bool())
        val nvdla_bdma_cfg_src_addr_high_0_v8 = Output(UInt(32.W))
        val nvdla_bdma_cfg_src_addr_low_0_v32 = Output(UInt(27.W))
        val nvdla_bdma_cfg_src_line_0_stride = Output(UInt(27.W))
        val nvdla_bdma_cfg_src_surf_0_stride = Output(UInt(27.W))
        val nvdla_bdma_cfg_status_0_stall_count_en = Output(Bool())
        val nvdla_bdma_cfg_surf_repeat_0_number = Output(UInt(24.W))

        //Read-only register inputs
        val nvdla_bdma_status_0_free_slot = Input(UInt(8.W))
        val nvdla_bdma_status_0_grp0_busy = Input(Bool())
        val nvdla_bdma_status_0_grp1_busy = Input(Bool())
        val nvdla_bdma_status_0_idle = Input(Bool())
        val nvdla_bdma_status_grp0_read_stall_0_count = Input(UInt(32.W))
        val nvdla_bdma_status_grp0_write_stall_0_count = Input(UInt(32.W))
        val nvdla_bdma_status_grp1_read_stall_0_count = Input(UInt(32.W))
        val nvdla_bdma_status_grp1_write_stall_0_count = Input(UInt(32.W))
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
    val  nvdla_bdma_cfg_cmd_0_wren = (io.reg_offset === "h14".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_dst_addr_high_0_wren = (io.reg_offset === "h0c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_dst_addr_low_0_wren = (io.reg_offset === "h08".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_dst_line_0_wren = (io.reg_offset === "h20".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_dst_surf_0_wren = (io.reg_offset === "h2c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_launch0_0_wren = (io.reg_offset === "h34".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_launch1_0_wren = (io.reg_offset === "h38".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_line_0_wren = (io.reg_offset === "h10".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_line_repeat_0_wren = (io.reg_offset === "h18".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_op_0_wren = (io.reg_offset === "h30".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_src_addr_high_0_wren = (io.reg_offset === "h04".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_src_addr_low_0_wren = (io.reg_offset === "h00".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_src_line_0_wren = (io.reg_offset === "h1c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_src_surf_0_wren = (io.reg_offset === "h28".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_status_0_wren = (io.reg_offset === "h3c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_cfg_surf_repeat_0_wren = (io.reg_offset === "h24".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_status_0_wren = (io.reg_offset === "h40".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_status_grp0_read_stall_0_wren = (io.reg_offset === "h44".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_status_grp0_write_stall_0_wren = (io.reg_offset === "h48".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_status_grp1_read_stall_0_wren = (io.reg_offset === "h4c".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)
    val  nvdla_bdma_status_grp1_write_stall_0_wren = (io.reg_offset === "h50".asUInt(32.W)) & io.reg_wr_en ; //spyglass disable UnloadedNet-ML //(W528)

    val  nvdla_bdma_cfg_cmd_0_out = Cat("b0".asUInt(30.W), io.nvdla_bdma_cfg_cmd_0_dst_ram_type, io.nvdla_bdma_cfg_cmd_0_src_ram_type)
    val  nvdla_bdma_cfg_dst_addr_high_0_out = io.nvdla_bdma_cfg_dst_addr_high_0_v8 
    val  nvdla_bdma_cfg_dst_addr_low_0_out = Cat(io.nvdla_bdma_cfg_dst_addr_low_0_v32, "b0".asUInt(5.W))
    val  nvdla_bdma_cfg_dst_line_0_out = Cat(io.nvdla_bdma_cfg_dst_line_0_stride, "b0".asUInt(5.W))
    val  nvdla_bdma_cfg_dst_surf_0_out = Cat(io.nvdla_bdma_cfg_dst_surf_0_stride, "b0".asUInt(5.W))
    val  nvdla_bdma_cfg_launch0_0_out = Cat("b0".asUInt(31.W), io.nvdla_bdma_cfg_launch0_0_grp0_launch)
    val  nvdla_bdma_cfg_launch1_0_out = Cat("b0".asUInt(31.W), io.nvdla_bdma_cfg_launch1_0_grp1_launch)
    val  nvdla_bdma_cfg_line_0_out = Cat("b0".asUInt(19.W), io.nvdla_bdma_cfg_line_0_size)
    val  nvdla_bdma_cfg_line_repeat_0_out = Cat("b0".asUInt(8.W), io.nvdla_bdma_cfg_line_repeat_0_number)
    val  nvdla_bdma_cfg_op_0_out = Cat("b0".asUInt(31.W), io.nvdla_bdma_cfg_op_0_en)
    val  nvdla_bdma_cfg_src_addr_high_0_out = io.nvdla_bdma_cfg_src_addr_high_0_v8
    val  nvdla_bdma_cfg_src_addr_low_0_out = Cat(io.nvdla_bdma_cfg_src_addr_low_0_v32, "b0".asUInt(5.W))
    val  nvdla_bdma_cfg_src_line_0_out = Cat(io.nvdla_bdma_cfg_src_line_0_stride, "b0".asUInt(5.W))
    val  nvdla_bdma_cfg_src_surf_0_out = Cat(io.nvdla_bdma_cfg_src_surf_0_stride, "b0".asUInt(5.W))
    val  nvdla_bdma_cfg_status_0_out = Cat("b0".asUInt(31.W), io.nvdla_bdma_cfg_status_0_stall_count_en)
    val  nvdla_bdma_cfg_surf_repeat_0_out =  Cat("b0".asUInt(8.W), io.nvdla_bdma_cfg_surf_repeat_0_number)
    val  nvdla_bdma_status_0_out =  Cat("b0".asUInt(21.W), io.nvdla_bdma_status_0_grp1_busy, io.nvdla_bdma_status_0_grp0_busy, io.nvdla_bdma_status_0_idle, io.nvdla_bdma_status_0_free_slot )
    val  nvdla_bdma_status_grp0_read_stall_0_out = io.nvdla_bdma_status_grp0_read_stall_0_count
    val  nvdla_bdma_status_grp0_write_stall_0_out = io.nvdla_bdma_status_grp0_write_stall_0_count 
    val  nvdla_bdma_status_grp1_read_stall_0_out = io.nvdla_bdma_status_grp1_read_stall_0_count 
    val  nvdla_bdma_status_grp1_write_stall_0_out = io.nvdla_bdma_status_grp1_write_stall_0_count 

    io.nvdla_bdma_cfg_launch0_0_grp0_launch_trigger := nvdla_bdma_cfg_launch0_0_wren
    io.nvdla_bdma_cfg_launch1_0_grp1_launch_trigger := nvdla_bdma_cfg_launch1_0_wren
    io.nvdla_bdma_cfg_op_0_en_trigger := nvdla_bdma_cfg_op_0_wren

    //Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
    "h14".asUInt(32.W)  -> nvdla_bdma_cfg_cmd_0_out,
    "h0c".asUInt(32.W)  -> nvdla_bdma_cfg_dst_addr_high_0_out,
    "h08".asUInt(32.W)  -> nvdla_bdma_cfg_dst_addr_low_0_out,
    "h20".asUInt(32.W)  -> nvdla_bdma_cfg_dst_line_0_out,
    "h2c".asUInt(32.W)  -> nvdla_bdma_cfg_dst_surf_0_out,
    "h34".asUInt(32.W)  -> nvdla_bdma_cfg_launch0_0_out,
    "h38".asUInt(32.W)  -> nvdla_bdma_cfg_launch1_0_out,
    "h10".asUInt(32.W)  -> nvdla_bdma_cfg_line_0_out,
    "h18".asUInt(32.W)  -> nvdla_bdma_cfg_line_repeat_0_out,
    "h30".asUInt(32.W)  -> nvdla_bdma_cfg_op_0_out,
    "h04".asUInt(32.W)  -> nvdla_bdma_cfg_src_addr_high_0_out,
    "h00".asUInt(32.W)  -> nvdla_bdma_cfg_src_addr_low_0_out,    
    "h1c".asUInt(32.W)  -> nvdla_bdma_cfg_src_line_0_out,
    "h28".asUInt(32.W)  -> nvdla_bdma_cfg_src_surf_0_out,
    "h3c".asUInt(32.W)  -> nvdla_bdma_cfg_status_0_out,
    "h24".asUInt(32.W)  -> nvdla_bdma_cfg_surf_repeat_0_out,
    "h40".asUInt(32.W)  -> nvdla_bdma_status_0_out,
    "h44".asUInt(32.W)  -> nvdla_bdma_status_grp0_read_stall_0_out,
    "h48".asUInt(32.W)  -> nvdla_bdma_status_grp0_write_stall_0_out,
    "h4c".asUInt(32.W)  -> nvdla_bdma_status_grp1_read_stall_0_out,
    "h50".asUInt(32.W)  -> nvdla_bdma_status_grp1_write_stall_0_out                                                                        
    ))

    //Register flop declarations

    val nvdla_bdma_cfg_cmd_0_dst_ram_type_out = RegInit(false.B)
    val nvdla_bdma_cfg_cmd_0_src_ram_type_out = RegInit(false.B)
    val nvdla_bdma_cfg_dst_addr_high_0_v8_out = RegInit("b0".asUInt(32.W))
    val nvdla_bdma_cfg_dst_addr_low_0_v32_out = RegInit("b0".asUInt(27.W))
    val nvdla_bdma_cfg_dst_line_0_stride_out = RegInit("b0".asUInt(27.W))
    val nvdla_bdma_cfg_dst_surf_0_stride_out = RegInit("b0".asUInt(27.W))
    val nvdla_bdma_cfg_launch0_0_grp0_launch_out = RegInit(false.B)
    val nvdla_bdma_cfg_launch1_0_grp1_launch_out = RegInit(false.B)
    val nvdla_bdma_cfg_line_0_size_out = RegInit("b0".asUInt(13.W))
    val nvdla_bdma_cfg_line_repeat_0_number_out = RegInit("b0".asUInt(24.W))
    val nvdla_bdma_cfg_op_0_en_out = RegInit(false.B)
    val nvdla_bdma_cfg_src_addr_high_0_v8_out = RegInit("b0".asUInt(32.W))
    val nvdla_bdma_cfg_src_addr_low_0_v32_out = RegInit("b0".asUInt(27.W))
    val nvdla_bdma_cfg_src_line_0_stride_out = RegInit("b0".asUInt(27.W))
    val nvdla_bdma_cfg_src_surf_0_stride_out = RegInit("b0".asUInt(27.W))
    val nvdla_bdma_cfg_status_0_stall_count_en_out = RegInit(false.B)
    val nvdla_bdma_cfg_surf_repeat_0_number_out = RegInit("b0".asUInt(24.W))

    // Register: NVDLA_BDMA_CFG_CMD_0    Field: dst_ram_type
    when(nvdla_bdma_cfg_cmd_0_wren){
        nvdla_bdma_cfg_cmd_0_dst_ram_type_out := io.reg_wr_data(1)
    }

    // Register: NVDLA_BDMA_CFG_CMD_0    Field: src_ram_type
    when(nvdla_bdma_cfg_cmd_0_wren){
        nvdla_bdma_cfg_cmd_0_src_ram_type_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_BDMA_CFG_DST_ADDR_HIGH_0    Field: v8
    when(nvdla_bdma_cfg_dst_addr_high_0_wren){
        nvdla_bdma_cfg_dst_addr_high_0_v8_out := io.reg_wr_data(31, 0)
    }

    // Register: NVDLA_BDMA_CFG_DST_ADDR_LOW_0    Field: v32
    when(nvdla_bdma_cfg_dst_addr_low_0_wren){
        nvdla_bdma_cfg_dst_addr_low_0_v32_out := io.reg_wr_data(31, 5)
    }

   // Register: NVDLA_BDMA_CFG_DST_LINE_0    Field: stride
    when(nvdla_bdma_cfg_dst_line_0_wren){
        nvdla_bdma_cfg_dst_line_0_stride_out := io.reg_wr_data(31, 5)
    }

    // Register: NVDLA_BDMA_CFG_DST_SURF_0    Field: stride
    when(nvdla_bdma_cfg_dst_surf_0_wren){
        nvdla_bdma_cfg_dst_surf_0_stride_out := io.reg_wr_data(31, 5)
    }

    // Register: NVDLA_BDMA_CFG_LAUNCH0_0    Field: grp0_launch
    when(nvdla_bdma_cfg_launch0_0_wren){
        nvdla_bdma_cfg_launch0_0_grp0_launch_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_BDMA_CFG_LAUNCH1_0    Field: grp1_launch
    when(nvdla_bdma_cfg_launch1_0_wren){
        nvdla_bdma_cfg_launch1_0_grp1_launch_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_BDMA_CFG_LINE_0    Field: size
    when(nvdla_bdma_cfg_line_0_wren){
        nvdla_bdma_cfg_line_0_size_out := io.reg_wr_data(12, 0)
    }

    // Register: NVDLA_BDMA_CFG_LINE_REPEAT_0    Field: number
    when(nvdla_bdma_cfg_line_repeat_0_wren){
        nvdla_bdma_cfg_line_repeat_0_number_out := io.reg_wr_data(23, 0)
    }

    // Register: NVDLA_BDMA_CFG_OP_0    Field: en
    when(nvdla_bdma_cfg_op_0_wren){
        nvdla_bdma_cfg_op_0_en_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_BDMA_CFG_SRC_ADDR_HIGH_0    Field: v8
    when(nvdla_bdma_cfg_src_addr_high_0_wren){
        nvdla_bdma_cfg_src_addr_high_0_v8_out := io.reg_wr_data(31, 0)
    }

    // Register: NVDLA_BDMA_CFG_SRC_ADDR_LOW_0    Field: v32
    when(nvdla_bdma_cfg_src_addr_low_0_wren){
        nvdla_bdma_cfg_src_addr_low_0_v32_out := io.reg_wr_data(31, 5)
    }

    // Register: NVDLA_BDMA_CFG_SRC_LINE_0    Field: stride
    when(nvdla_bdma_cfg_src_line_0_wren){
        nvdla_bdma_cfg_src_line_0_stride_out := io.reg_wr_data(31, 5)
    }

    // Register: NVDLA_BDMA_CFG_SRC_SURF_0    Field: stride
    when(nvdla_bdma_cfg_src_surf_0_wren){
        nvdla_bdma_cfg_src_surf_0_stride_out := io.reg_wr_data(31, 5)
    }

    // Register: NVDLA_BDMA_CFG_STATUS_0    Field: stall_count_en
    when(nvdla_bdma_cfg_status_0_wren){
        nvdla_bdma_cfg_status_0_stall_count_en_out := io.reg_wr_data(0)
    }

    // Register: NVDLA_BDMA_CFG_SURF_REPEAT_0    Field: number
    when(nvdla_bdma_cfg_surf_repeat_0_wren){
        nvdla_bdma_cfg_surf_repeat_0_number_out := io.reg_wr_data(23, 0)
    }

  // Not generating flops for read-only field NVDLA_BDMA_STATUS_0::free_slot

  // Not generating flops for read-only field NVDLA_BDMA_STATUS_0::grp0_busy

  // Not generating flops for read-only field NVDLA_BDMA_STATUS_0::grp1_busy

  // Not generating flops for read-only field NVDLA_BDMA_STATUS_0::idle

  // Not generating flops for read-only field NVDLA_BDMA_STATUS_GRP0_READ_STALL_0::count

  // Not generating flops for read-only field NVDLA_BDMA_STATUS_GRP0_WRITE_STALL_0::count

  // Not generating flops for read-only field NVDLA_BDMA_STATUS_GRP1_READ_STALL_0::count

  // Not generating flops for read-only field NVDLA_BDMA_STATUS_GRP1_WRITE_STALL_0::count

    io.nvdla_bdma_cfg_cmd_0_dst_ram_type := nvdla_bdma_cfg_cmd_0_dst_ram_type_out
    io.nvdla_bdma_cfg_cmd_0_src_ram_type := nvdla_bdma_cfg_cmd_0_src_ram_type_out
    io.nvdla_bdma_cfg_dst_addr_high_0_v8 := nvdla_bdma_cfg_dst_addr_high_0_v8_out
    io.nvdla_bdma_cfg_dst_addr_low_0_v32 := nvdla_bdma_cfg_dst_addr_low_0_v32_out
    io.nvdla_bdma_cfg_dst_line_0_stride := nvdla_bdma_cfg_dst_line_0_stride_out
    io.nvdla_bdma_cfg_dst_line_0_stride := nvdla_bdma_cfg_dst_line_0_stride_out
    io.nvdla_bdma_cfg_launch0_0_grp0_launch := nvdla_bdma_cfg_launch0_0_grp0_launch_out
    io.nvdla_bdma_cfg_launch1_0_grp1_launch := nvdla_bdma_cfg_launch1_0_grp1_launch_out
    io.nvdla_bdma_cfg_line_0_size := nvdla_bdma_cfg_line_0_size_out
    io.nvdla_bdma_cfg_line_repeat_0_number := nvdla_bdma_cfg_line_repeat_0_number_out 
    io.nvdla_bdma_cfg_op_0_en := nvdla_bdma_cfg_op_0_en_out 
    io.nvdla_bdma_cfg_src_addr_high_0_v8 := nvdla_bdma_cfg_src_addr_high_0_v8_out
    io.nvdla_bdma_cfg_src_addr_low_0_v32 := nvdla_bdma_cfg_src_addr_low_0_v32_out                                                             
    io.nvdla_bdma_cfg_src_line_0_stride := nvdla_bdma_cfg_src_line_0_stride_out
    io.nvdla_bdma_cfg_src_surf_0_stride := nvdla_bdma_cfg_src_surf_0_stride_out 
    io.nvdla_bdma_cfg_status_0_stall_count_en  := nvdla_bdma_cfg_status_0_stall_count_en_out 
    io.nvdla_bdma_cfg_surf_repeat_0_number := nvdla_bdma_cfg_surf_repeat_0_number_out
}}