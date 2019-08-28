// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_BDMA_reg extends Module{
//     val io = IO(new Bundle{}
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         //Register control interface
//         val reg = new reg_control_if

//         //Writable register flop/trigger outputs
//         val nvdla_bdma_cfg_cmd_0_dst_ram_type = Output(Bool())
//         val nvdla_bdma_cfg_cmd_0_src_ram_type = Output(Bool())
//         val nvdla_bdma_cfg_dst_addr_high_0_v8 = Output(UInt(32.W))
//         val nvdla_bdma_cfg_dst_addr_low_0_v32 = Output(UInt(27.W))
//         val nvdla_bdma_cfg_dst_line_0_stride = Output(UInt(27.W))
//         val nvdla_bdma_cfg_dst_surf_0_stride = Output(UInt(27.W))
//         val nvdla_bdma_cfg_launch0_0_grp0_launch = Output(Bool())
//         val nvdla_bdma_cfg_launch0_0_grp0_launch_trigger = Output(Bool())
//         val nvdla_bdma_cfg_launch1_0_grp1_launch = Output(Bool())
//         val nvdla_bdma_cfg_launch1_0_grp1_launch_trigger = Output(Bool())
//         val nvdla_bdma_cfg_line_0_size = Output(UInt(13.W))
//         val nvdla_bdma_cfg_line_repeat_0_number = Output(UInt(24.W))
//         val nvdla_bdma_cfg_op_0_en = Output(Bool())
//         val nvdla_bdma_cfg_op_0_en_trigger = Output(Bool())
//         val nvdla_bdma_cfg_src_addr_high_0_v8 = Output(UInt(32.W))
//         val nvdla_bdma_cfg_src_addr_low_0_v32 = Output(UInt(27.W))
//         val nvdla_bdma_cfg_src_line_0_stride = Output(UInt(27.W))
//         val nvdla_bdma_cfg_src_surf_0_stride = Output(UInt(27.W))
//         val nvdla_bdma_cfg_status_0_stall_count_en = Output(Bool())
//         val nvdla_bdma_cfg_surf_repeat_0_number = Output(UInt(24.W))

//         //Read-only register inputs
//         val nvdla_bdma_status_0_free_slot = Input(UInt(8.W))
//         val nvdla_bdma_status_0_grp0_busy = Input(Bool())
//         val nvdla_bdma_status_0_grp1_busy = Input(Bool())
//         val nvdla_bdma_status_0_idle = Input(Bool())
//         val nvdla_bdma_status_grp0_read_stall_0_count = Input(UInt(32.W))
//         val nvdla_bdma_status_grp0_write_stall_0_count = Input(UInt(32.W))
//         val nvdla_bdma_status_grp1_read_stall_0_count = Input(UInt(32.W))
//         val nvdla_bdma_status_grp1_write_stall_0_count = Input(UInt(32.W))
//     })
    
//     //      ┌─┐       ┌─┐
//     //   ┌──┘ ┴───────┘ ┴──┐
//     //   │                 │
//     //   │       ───       │
//     //   │  ─┬┘       └┬─  │
//     //   │                 │
//     //   │       ─┴─       │
//     //   │                 │
//     //   └───┐         ┌───┘
//     //       │         │
//     //       │         │
//     //       │         │
//     //       │         └──────────────┐
//     //       │                        │
//     //       │                        ├─┐
//     //       │                        ┌─┘    
//     //       │                        │
//     //       └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //         │ ─┤ ─┤       │ ─┤ ─┤         
//     //         └──┴──┘       └──┴──┘ 
//     withClock(io.nvdla_core_clk){

//     // Address decode
//     val  nvdla_bdma_cfg_cmd_0_wren = (io.reg.offset === "h14".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_dst_addr_high_0_wren = (io.reg.offset === "h0c".asUInt(32.W)) & io.reg.wr_en
//     val  nvdla_bdma_cfg_dst_addr_low_0_wren = (io.reg.offset === "h08".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_dst_line_0_wren = (io.reg.offset === "h20".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_dst_surf_0_wren = (io.reg.offset === "h2c".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_launch0_0_wren = (io.reg.offset === "h34".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_launch1_0_wren = (io.reg.offset === "h38".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_line_0_wren = (io.reg.offset === "h10".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_line_repeat_0_wren = (io.reg.offset === "h18".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_op_0_wren = (io.reg.offset === "h30".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_src_addr_high_0_wren = (io.reg.offset === "h04".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_src_addr_low_0_wren = (io.reg.offset === "h00".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_src_line_0_wren = (io.reg.offset === "h1c".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_src_surf_0_wren = (io.reg.offset === "h28".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_status_0_wren = (io.reg.offset === "h3c".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_cfg_surf_repeat_0_wren = (io.reg.offset === "h24".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_status_0_wren = (io.reg.offset === "h40".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_status_grp0_read_stall_0_wren = (io.reg.offset === "h44".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_status_grp0_write_stall_0_wren = (io.reg.offset === "h48".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_status_grp1_read_stall_0_wren = (io.reg.offset === "h4c".asUInt(32.W)) & io.reg.wr_en 
//     val  nvdla_bdma_status_grp1_write_stall_0_wren = (io.reg.offset === "h50".asUInt(32.W)) & io.reg.wr_en 

//     io.nvdla_bdma_cfg_launch0_0_grp0_launch_trigger := nvdla_bdma_cfg_launch0_0_wren
//     io.nvdla_bdma_cfg_launch1_0_grp1_launch_trigger := nvdla_bdma_cfg_launch1_0_wren
//     io.nvdla_bdma_cfg_op_0_en_trigger := nvdla_bdma_cfg_op_0_wren

//     //Output mux
//     io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
//     Seq(  
//     //nvdla_bdma_cfg_cmd_0_out    
//     "h14".asUInt(32.W)  -> Cat("b0".asUInt(30.W), io.nvdla_bdma_cfg_cmd_0_dst_ram_type, io.nvdla_bdma_cfg_cmd_0_src_ram_type),
//     //nvdla_bdma_cfg_dst_addr_high_0_out
//     "h0c".asUInt(32.W)  -> io.nvdla_bdma_cfg_dst_addr_high_0_v8,
//     //nvdla_bdma_cfg_dst_addr_low_0_out
//     "h08".asUInt(32.W)  -> Cat(io.nvdla_bdma_cfg_dst_addr_low_0_v32, "b0".asUInt(5.W)),
//     //nvdla_bdma_cfg_dst_line_0_out
//     "h20".asUInt(32.W)  -> Cat(io.nvdla_bdma_cfg_dst_line_0_stride, "b0".asUInt(5.W)),
//     //nvdla_bdma_cfg_dst_surf_0_out
//     "h2c".asUInt(32.W)  -> Cat(io.nvdla_bdma_cfg_dst_surf_0_stride, "b0".asUInt(5.W)),
//     //nvdla_bdma_cfg_launch0_0_out
//     "h34".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.nvdla_bdma_cfg_launch0_0_grp0_launch),
//     //nvdla_bdma_cfg_launch1_0_out
//     "h38".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.nvdla_bdma_cfg_launch1_0_grp1_launch),
//     //nvdla_bdma_cfg_line_0_out
//     "h10".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.nvdla_bdma_cfg_line_0_size),
//     //nvdla_bdma_cfg_line_repeat_0_out
//     "h18".asUInt(32.W)  -> Cat("b0".asUInt(8.W), io.nvdla_bdma_cfg_line_repeat_0_number),
//     //nvdla_bdma_cfg_op_0_out
//     "h30".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.nvdla_bdma_cfg_op_0_en),
//     //nvdla_bdma_cfg_src_addr_high_0_out
//     "h04".asUInt(32.W)  -> io.nvdla_bdma_cfg_src_addr_high_0_v8,
//     //nvdla_bdma_cfg_src_addr_low_0_out
//     "h00".asUInt(32.W)  -> Cat(io.nvdla_bdma_cfg_src_addr_low_0_v32, "b0".asUInt(5.W)),   
//     //nvdla_bdma_cfg_src_line_0_out 
//     "h1c".asUInt(32.W)  -> Cat(io.nvdla_bdma_cfg_src_line_0_stride, "b0".asUInt(5.W)),
//     //nvdla_bdma_cfg_src_surf_0_out
//     "h28".asUInt(32.W)  -> Cat(io.nvdla_bdma_cfg_src_surf_0_stride, "b0".asUInt(5.W)),
//     //nvdla_bdma_cfg_status_0_out
//     "h3c".asUInt(32.W)  ->  Cat("b0".asUInt(31.W), io.nvdla_bdma_cfg_status_0_stall_count_en),
//     //nvdla_bdma_cfg_surf_repeat_0_out
//     "h24".asUInt(32.W)  -> Cat("b0".asUInt(8.W), io.nvdla_bdma_cfg_surf_repeat_0_number),
//     //nvdla_bdma_status_0_out
//     "h40".asUInt(32.W)  -> Cat("b0".asUInt(21.W), io.nvdla_bdma_status_0_grp1_busy, io.nvdla_bdma_status_0_grp0_busy, io.nvdla_bdma_status_0_idle, io.nvdla_bdma_status_0_free_slot ),
//     //nvdla_bdma_status_grp0_read_stall_0_out
//     "h44".asUInt(32.W)  -> io.nvdla_bdma_status_grp0_read_stall_0_count,
//     //nvdla_bdma_status_grp0_write_stall_0_out
//     "h48".asUInt(32.W)  -> io.nvdla_bdma_status_grp0_write_stall_0_count,
//     //nvdla_bdma_status_grp1_read_stall_0_out
//     "h4c".asUInt(32.W)  -> io.nvdla_bdma_status_grp1_read_stall_0_count,
//     //nvdla_bdma_status_grp1_write_stall_0_out
//     "h50".asUInt(32.W)  -> io.nvdla_bdma_status_grp1_write_stall_0_count                                                                        
//     ))

//     //Register flop declarations
//     // Register: NVDLA_BDMA_CFG_CMD_0    Field: dst_ram_type
//     io.nvdla_bdma_cfg_cmd_0_dst_ram_type := RegEnable(io.reg.wr_data(1), false.B, nvdla_bdma_cfg_cmd_0_wren)
//     // Register: NVDLA_BDMA_CFG_CMD_0    Field: src_ram_type
//     io.nvdla_bdma_cfg_cmd_0_src_ram_type := RegEnable(io.reg.wr_data(0), false.B, nvdla_bdma_cfg_cmd_0_wren)
//     // Register: NVDLA_BDMA_CFG_DST_ADDR_HIGH_0    Field: v8
//     io.nvdla_bdma_cfg_dst_addr_high_0_v8 := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_bdma_cfg_dst_addr_high_0_wren)
//     // Register: NVDLA_BDMA_CFG_DST_ADDR_LOW_0    Field: v32
//     io.nvdla_bdma_cfg_dst_addr_low_0_v32 := RegEnable(io.reg.wr_data(31, 5), "b0".asUInt(27.W), nvdla_bdma_cfg_dst_addr_low_0_wren)
//     // Register: NVDLA_BDMA_CFG_DST_LINE_0    Field: stride
//     io.nvdla_bdma_cfg_dst_line_0_stride := RegEnable(io.reg.wr_data(31, 5), "b0".asUInt(27.W), nvdla_bdma_cfg_dst_line_0_wren)
//     // Register: NVDLA_BDMA_CFG_DST_SURF_0    Field: stride
//     io.nvdla_bdma_cfg_dst_line_0_stride := RegEnable(io.reg.wr_data(31, 5), "b0".asUInt(27.W), nvdla_bdma_cfg_dst_surf_0_wren)
//     // Register: NVDLA_BDMA_CFG_LAUNCH0_0    Field: grp0_launch
//     io.nvdla_bdma_cfg_launch0_0_grp0_launch := RegEnable(io.reg.wr_data(0), false.B, nvdla_bdma_cfg_launch0_0_wren)
//     // Register: NVDLA_BDMA_CFG_LAUNCH1_0    Field: grp1_launch
//     io.nvdla_bdma_cfg_launch1_0_grp1_launch := RegEnable(io.reg.wr_data(0), false.B, nvdla_bdma_cfg_launch1_0_wren)
//     // Register: NVDLA_BDMA_CFG_LINE_0    Field: size
//     io.nvdla_bdma_cfg_line_0_size := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_bdma_cfg_line_0_wren)
//     // Register: NVDLA_BDMA_CFG_LINE_REPEAT_0    Field: number
//     io.nvdla_bdma_cfg_line_repeat_0_number := RegEnable(io.reg.wr_data(23, 0), "b0".asUInt(24.W), nvdla_bdma_cfg_line_repeat_0_wren)
//     // Register: NVDLA_BDMA_CFG_OP_0    Field: en
//     io.nvdla_bdma_cfg_op_0_en := RegEnable(io.reg.wr_data(0), false.B, nvdla_bdma_cfg_op_0_wren)
//     // Register: NVDLA_BDMA_CFG_SRC_ADDR_HIGH_0    Field: v8
//     io.nvdla_bdma_cfg_src_addr_high_0_v8 := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_bdma_cfg_src_addr_high_0_wren)
//     // Register: NVDLA_BDMA_CFG_SRC_ADDR_LOW_0    Field: v32
//     io.nvdla_bdma_cfg_src_addr_low_0_v32 := RegEnable(io.reg.wr_data(31, 5), "b0".asUInt(27.W), nvdla_bdma_cfg_src_addr_low_0_wren)  
//     // Register: NVDLA_BDMA_CFG_SRC_LINE_0    Field: stride                                                         
//     io.nvdla_bdma_cfg_src_line_0_stride := RegEnable(io.reg.wr_data(31, 5), "b0".asUInt(27.W), nvdla_bdma_cfg_src_line_0_wren)
//     // Register: NVDLA_BDMA_CFG_SRC_SURF_0    Field: stride
//     io.nvdla_bdma_cfg_src_surf_0_stride := RegEnable(io.reg.wr_data(31, 5), "b0".asUInt(27.W), nvdla_bdma_cfg_src_surf_0_wren)
//     // Register: NVDLA_BDMA_CFG_STATUS_0    Field: stall_count_en
//     io.nvdla_bdma_cfg_status_0_stall_count_en := RegEnable(io.reg.wr_data(0), false.B, nvdla_bdma_cfg_status_0_wren)
//     // Register: NVDLA_BDMA_CFG_SURF_REPEAT_0    Field: number
//     io.nvdla_bdma_cfg_surf_repeat_0_number := RegEnable(io.reg.wr_data(23, 0), "b0".asUInt(24.W), nvdla_bdma_cfg_surf_repeat_0_wren)
// }}