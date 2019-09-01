// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_WDMA_cmd(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         // cmd rd
//         val cmd_fifo_rd_pd = DecoupledIO(UInt(80.W))

//         // config
//         val reg2dp_cube_out_channel = Input(UInt(13.W))
//         val reg2dp_cube_out_height = Input(UInt(13.W))
//         val reg2dp_cube_out_width = Input(UInt(13.W))
//         val reg2dp_dst_base_addr_high = Input(UInt(32.W))
//         val reg2dp_dst_base_addr_low = Input(UInt(32.W))
//         val reg2dp_dst_line_stride = Input(UInt(32.W))
//         val reg2dp_dst_surface_stride = Input(UInt(32.W))
//         val reg2dp_partial_width_out_first = Input(UInt(10.W))
//         val reg2dp_partial_width_out_last = Input(UInt(10.W))
//         val reg2dp_partial_width_out_mid = Input(UInt(10.W))
//         val reg2dp_split_num = Input(UInt(8.W))

//         // Read-only register input
//         val op_load = Input(Bool())
//         val perf_read_stall = Input(UInt(32.W))
//     })
// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 
// withClock(io.nvdla_core_clk){
//     val cmd_fifo_wr_prdy = Wire(Bool())
//     val is_cube_end = Wire(Bool())
//     val op_prcess = RegInit(false.B)
//     val op_done = op_prcess & cmd_fifo_wr_prdy & is_cube_end;

//     when(io.op_load){
//         op_prcess := true.B
//     }
//     .elsewhen(op_done){
//         op_prcess := false.B
//     }

//     val cmd_fifo_wr_pvld = op_prcess
//     val cmd_fifo_wr_accpet = cmd_fifo_wr_pvld & cmd_fifo_wr_prdy

//     // SPLIT MODE
//     val cfg_mode_split = (io.reg2dp_split_num =/= 0.U)
//     val cfg_base_addr = Cat(io.reg2dp_dst_base_addr_high, io.reg2dp_dst_base_addr_low)

//     //==============
//     // CUBE DRAW
//     //==============
//     val is_last_wg = Wire(Bool())
//     val is_last_h = Wire(Bool())
//     val is_last_surf = Wire(Bool())

//     val is_line_end  = true.B
//     val is_surf_end  = is_line_end & is_last_h;
//     val is_split_end = is_surf_end & is_last_surf;
//     is_cube_end  := is_split_end & is_last_wg;

//     // WIDTH COUNT: in width direction, indidate one block 
//     val is_fspt = Wire(Bool())
//     val is_lspt = Wire(Bool())
//     val is_mspt = Wire(Bool())
//     val split_size_of_width = Mux(is_fspt, io.reg2dp_partial_width_out_first,
//                               Mux(is_lspt, io.reg2dp_partial_width_out_last,
//                               Mux(is_mspt, io.reg2dp_partial_width_out_mid,
//                               "b0".asUInt(10.W))))
//     val size_of_width = Mux(cfg_mode_split, split_size_of_width, io.reg2dp_cube_out_width)
//     val splitw_stride = (size_of_width +& 1.U) << conf.ATMMBW.U

//     val count_wg = RegInit("b0".asUInt(8.W))
//     when(io.op_load){
//         count_wg := 0.U
//     }
//     .elsewhen(cmd_fifo_wr_accpet){
//         when(is_split_end){
//             count_wg := count_wg + 1.U
//         }
//     }

//     is_last_wg := (count_wg === io.reg2dp_split_num)
//     val is_first_wg = (count_wg === 0.U)

//     is_fspt := cfg_mode_split & is_first_wg
//     is_lspt := cfg_mode_split & is_last_wg
//     is_mspt := cfg_mode_split & !is_fspt & !is_lspt

//     //==============
//     // COUNT SURF
//     //==============
//     val count_surf = RegInit("b0".asUInt(conf.ATMMBW.W))
//     when(cmd_fifo_wr_accpet){
//         when(is_split_end){
//             count_surf := 0.U
//         }
//         .elsewhen(is_surf_end){
//             count_surf := count_surf + 1.U
//         }
//     }

//     is_last_surf := (count_surf === io.reg2dp_cube_out_channel(12, conf.ATMMBW))

//     // per Surf
//     val count_h = RegInit("b0".asUInt(13.W))
//     when(cmd_fifo_wr_accpet){
//         when(is_surf_end){
//             count_h := 0.U
//         }
//         .elsewhen(is_line_end){
//             count_h := count_h + 1.U
//         }
//     }

//     is_last_h := (count_h === io.reg2dp_cube_out_height)

//     //==============
//     // ADDR
//     //==============
//     // LINE
//     val base_addr_line = RegInit("b0".asUInt(64.W))
//     val base_addr_surf = RegInit("b0".asUInt(64.W))
//     val base_addr_split = RegInit("b0".asUInt(64.W))

//     when(io.op_load){
//         base_addr_line := cfg_base_addr
//     }
//     .elsewhen(cmd_fifo_wr_accpet){
//         when(is_split_end){
//             base_addr_line := base_addr_split + splitw_stride
//         }
//         .elsewhen(is_surf_end){
//             base_addr_line := base_addr_surf + io.reg2dp_dst_surface_stride
//         }
//         .elsewhen(is_line_end){
//             base_addr_line := base_addr_line + io.reg2dp_dst_line_stride
//         }

//     }

//     // SURF
//     when(io.op_load){
//         base_addr_surf := cfg_base_addr
//     }
//     .elsewhen(cmd_fifo_wr_accpet){
//         when(is_split_end){
//             base_addr_surf := base_addr_split + splitw_stride
//         }
//         .elsewhen(is_surf_end){
//             base_addr_surf := base_addr_surf + io.reg2dp_dst_surface_stride
//         }
//     }

//     // SPLIT
//     when(io.op_load){
//         base_addr_split := base_addr_split
//     }
//     .elsewhen(cmd_fifo_wr_accpet){
//         when(is_split_end){
//             base_addr_split := base_addr_split + splitw_stride
//         }
//     }

//     //==============
//     // CMD FIFO WRITE 
//     //==============
//     val cmd_fifo_wr_pd = Wire(UInt(80.W))
//     val u_fifo = Module{new NV_NVDLA_PDP_WDMA_CMD_fifo}
//     u_fifo.io.nvdla_core_clk := io.nvdla_core_clk
//     u_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_fifo.io.cmd_fifo_wr_pvld := cmd_fifo_wr_pvld
//     cmd_fifo_wr_prdy := u_fifo.io.cmd_fifo_wr_prdy
//     u_fifo.io.cmd_fifo_wr_pd := cmd_fifo_wr_pd
//     io.cmd_fifo_rd_pvld := u_fifo.io.cmd_fifo_rd_pvld
//     u_fifo.io.cmd_fifo_rd_prdy := io.cmd_fifo_rd_prdy
//     io.cmd_fifo_rd_pd := u_fifo.io.cmd_fifo_rd_pd

//     //==============
//     // DMA Req : ADDR : Generation
//     //==============
//     val spt_cmd_addr = base_addr_line
//     val spt_cmd_size = size_of_width
//     val spt_cmd_lenb = "b0".asUInt(2.W)
//     val spt_cmd_cube_end = is_cube_end

//     cmd_fifo_wr_pd := Cat(spt_cmd_cube_end, spt_cmd_lenb, spt_cmd_size, spt_cmd_addr)

// }}



// class NV_NVDLA_PDP_WDMA_CMD_fifo extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         val cmd_fifo_wr_prdy = Output(Bool())
//         val cmd_fifo_wr_pvld = Input(Bool())
//         val cmd_fifo_wr_pd = Input(UInt(80.W))
//         val cmd_fifo_rd_prdy = Input(Bool())
//         val cmd_fifo_rd_pvld = Output(Bool())
//         val cmd_fifo_rd_pd = Output(UInt(80.W))

//         val pwrbus_ram_pd = Input(UInt(32.W))
//     })
//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │
//     //       │                 │
//     //       └───┐         ┌───┘
//     //           │         │
//     //           │         │
//     //           │         │
//     //           │         └──────────────┐
//     //           │                        │
//     //           │                        ├─┐
//     //           │                        ┌─┘    
//     //           │                        │
//     //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //             │ ─┤ ─┤       │ ─┤ ─┤         
//     //             └──┴──┘       └──┴──┘
//     withClock(io.nvdla_core_clk){
//     // Master Clock Gating (SLCG)
//     //
//     // We gate the clock(s) when idle or stalled.
//     // This allows us to turn off numerous miscellaneous flops
//     // that don't get gated during synthesis for one reason or another.
//     //
//     // We gate write side and read side separately. 
//     // If the fifo is synchronous, we also gate the ram separately, but if
//     // -master_clk_gated_unified or -status_reg/-status_logic_reg is specified, 
//     // then we use one clk gate for write, ram, and read.
//     //
//     val nvdla_core_clk_mgated_enable = Wire(Bool())
//     val nvdla_core_clk_mgate = Module(new NV_CLK_gate_power)
//     nvdla_core_clk_mgate.io.clk := io.nvdla_core_clk
//     nvdla_core_clk_mgate.io.clk_en := nvdla_core_clk_mgated_enable
//     val nvdla_core_clk_mgated = nvdla_core_clk_mgate.io.clk_gated

//     ////////////////////////////////////////////////////////////////////////
//     // WRITE SIDE                                                        //
//     ////////////////////////////////////////////////////////////////////////
//     val wr_reserving = Wire(Bool())
//     val cmd_fifo_wr_pvld_in = RegInit(false.B)    // registered cmd_fifo_wr_pvld
//     val wr_busy_in = RegInit(false.B)   // inputs being held this cycle?
//     io.cmd_fifo_wr_prdy := !wr_busy_in
//     val cmd_fifo_wr_busy_next = Wire(Bool())      // fwd: fifo busy next?

//     // factor for better timing with distant wr_req signal
//     val wr_busy_in_next_wr_req_eq_1 = cmd_fifo_wr_busy_next
//     val wr_busy_in_next_wr_req_eq_0 = (cmd_fifo_wr_pvld_in && cmd_fifo_wr_busy_next) && !wr_reserving
//     val wr_busy_in_next = Mux(io.cmd_fifo_wr_pvld, wr_busy_in_next_wr_req_eq_1, wr_busy_in_next_wr_req_eq_0)
//     val wr_busy_in_int = Wire(Bool())

//     wr_busy_in := wr_busy_in_next
//     when(!wr_busy_in_int){
//         cmd_fifo_wr_pvld_in := io.cmd_fifo_wr_pvld && !wr_busy_in
//     }

//     val cmd_fifo_wr_busy_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // copy for internal use
//     wr_reserving := cmd_fifo_wr_pvld_in && !cmd_fifo_wr_busy_int   // reserving write space?

//     val wr_popping = Wire(Bool())// fwd: write side sees pop?
//     val cmd_fifo_wr_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(1.W))} // write-side count
//     val wr_count_next_wr_popping = Mux(wr_reserving, cmd_fifo_wr_count, cmd_fifo_wr_count-1.U)
//     val wr_count_next_no_wr_popping = Mux(wr_reserving, cmd_fifo_wr_count+1.U, cmd_fifo_wr_count)
//     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

//     val wr_count_next_no_wr_popping_is_1 = (wr_count_next_no_wr_popping === 1.U)
//     val wr_count_next_is_1 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_1)
//     val wr_limit_muxed = Wire(UInt(1.W))    // muxed with simulation/emulation overrides
//     val wr_limit_reg = wr_limit_muxed
//     cmd_fifo_wr_busy_next := wr_count_next_is_1 ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))
//     wr_busy_in_int := cmd_fifo_wr_pvld_in && cmd_fifo_wr_busy_int

//     cmd_fifo_wr_busy_int := cmd_fifo_wr_busy_next
//     when(wr_reserving ^ wr_popping){
//         cmd_fifo_wr_count := wr_count_next
//     }

//     val wr_pushing = wr_reserving // data pushed same cycle as cmd_fifo_wr_pvld_in

//     //
//     // RAM
//     //  
//     val rd_popping = Wire(Bool())

//     val ram_we = wr_pushing && (cmd_fifo_wr_count > 0.U || !rd_popping)      // note: write occurs next cycle
//     val ram_iwe = !wr_busy_in && io.cmd_fifo_wr_pvld
    

//     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
//     // Fifogen handles this by ignoring the data on the ram data out for that cycle.
//     val ram = Module(new NV_NVDLA_PDP_WDMA_CMD_fifo_flopram_rwsa_1x80)
//     ram.io.clk := io.nvdla_core_clk
//     ram.io.clk_mgated := nvdla_core_clk_mgated
//     ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ram.io.di := io.cmd_fifo_wr_pd
//     ram.io.iwe := ram_iwe
//     ram.io.we := ram_we
//     ram.io.ra := Mux(cmd_fifo_wr_count === 0.U, true.B, false.B)
//     io.cmd_fifo_rd_pd := ram.io.dout
    
//     //
//     // SYNCHRONOUS BOUNDARY
//     //
//     wr_popping := rd_popping    // let it be seen immediately
//     val rd_pushing = wr_pushing // let it be seen immediately

//     //
//     // READ SIDE
//     //
//     rd_popping := io.cmd_fifo_rd_pvld && io.cmd_fifo_rd_prdy    // read-side fifo count

//     val cmd_fifo_rd_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(1.W))} //read-side fifo count
//     val rd_count_next_rd_popping = Mux(rd_pushing, cmd_fifo_rd_count, cmd_fifo_rd_count - 1.U)
//     val rd_count_next_no_rd_popping = Mux(rd_pushing, cmd_fifo_rd_count + 1.U, cmd_fifo_rd_count)
//     val rd_count_next = Mux(rd_popping, rd_count_next_rd_popping, rd_count_next_no_rd_popping)
//     io.cmd_fifo_rd_pvld := cmd_fifo_rd_count =/= 0.U || rd_pushing;
//     when(rd_pushing || rd_popping){
//         cmd_fifo_rd_count := rd_count_next
//     }

//     nvdla_core_clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
//                                     (cmd_fifo_wr_pvld_in && !cmd_fifo_wr_busy_int) || (cmd_fifo_wr_busy_int =/= cmd_fifo_wr_busy_next)) || 
//                                     (rd_pushing || rd_popping || (io.cmd_fifo_rd_pvld && io.cmd_fifo_rd_prdy)) || (wr_pushing))

//     wr_limit_muxed := "d0".asUInt(1.W)

    
// }}

// class NV_NVDLA_PDP_WDMA_CMD_fifo_flopram_rwsa_1x80 extends Module{
//   val io = IO(new Bundle{
//         val clk = Input(Clock())
//         val clk_mgated = Input(Clock())

//         val di = Input(UInt(80.W))
//         val iwe = Input(Bool())
//         val we = Input(Bool())
//         val ra = Input(Bool())
//         val dout = Output(UInt(80.W))

//         val pwrbus_ram_pd = Input(UInt(32.W))

//   })  
// withClock(io.clk){
//     val di_d = Reg(UInt(80.W))
//     when(io.iwe){
//         di_d := io.di
//     }
//     val ram_ff = Seq.fill(1)(withClock(io.clk_mgated){Reg(UInt(80.W))}) :+ Wire(UInt(80.W))
//     when(io.we){
//         ram_ff(0) := di_d
//     }   
//     ram_ff(1) := di_d 
//     io.dout := MuxLookup(io.ra, "b0".asUInt(80.W), 
//         (0 to 1) map { i => i.U -> ram_ff(i)} )
// }}


// object NV_NVDLA_PDP_WDMA_cmdDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_WDMA_cmd())
// }