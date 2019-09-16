// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_WDMA_dat(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         // dp2wdma
//         val dp2wdma_vld = Input(Bool())
//         val dp2wdma_rdy = Output(Bool())
//         val dp2wdma_pd = Input(UInt((conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_PDP_BWPE).W))

//         // dat_fifo
//         val dat_fifo_rd_pvld = Output(Vec(conf.ATMM_NUM, Vec(conf.PDP_NUM, Bool())))
//         val dat_fifo_rd_prdy = Input(Vec(conf.ATMM_NUM, Vec(conf.PDP_NUM, Bool())))
//         val dat_fifo_rd_pd = Output(Vec(conf.ATMM_NUM, Vec(conf.PDP_NUM, UInt(conf.PDPBW.W))))

//         // config
//         val reg2dp_cube_out_channel = Input(UInt(13.W))
//         val reg2dp_cube_out_height = Input(UInt(13.W))
//         val reg2dp_cube_out_width = Input(UInt(13.W))
//         val reg2dp_partial_width_out_first = Input(UInt(10.W))
//         val reg2dp_partial_width_out_last = Input(UInt(10.W))
//         val reg2dp_partial_width_out_mid = Input(UInt(10.W))
//         val reg2dp_split_num = Input(UInt(8.W))

//        // Read-only register input
//         val op_load = Input(Bool())
//         val wdma_done = Input(Bool())
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
//     val cfg_mode_split = (io.reg2dp_split_num =/= 0.U)
// //==============
// // CUBE DRAW
// //==============
//     val is_last_b = Wire(Bool())
//     val is_last_w = Wire(Bool())
//     val is_last_h = Wire(Bool())
//     val is_last_surf = Wire(Bool())
//     val is_last_wg = Wire(Bool())
//     val is_blk_end = is_last_b
//     val is_line_end  = is_blk_end & is_last_w
//     val is_surf_end = is_line_end & is_last_h
//     val is_split_end = is_surf_end & is_last_surf
//     val is_cube_end  = is_split_end & is_last_wg

//     // WIDTH COUNT: in width direction, indidate one block 
//     val is_fspt = Wire(Bool())
//     val is_lspt = Wire(Bool())
//     val is_mspt = Wire(Bool())
//     val split_size_of_width = Mux(is_fspt, io.reg2dp_partial_width_out_first,
//                               Mux(is_lspt, io.reg2dp_partial_width_out_last,
//                               Mux(is_mspt, io.reg2dp_partial_width_out_mid, 
//                               "b0".asUInt(10.W))))
//     val size_of_width = Mux(cfg_mode_split, split_size_of_width, io.reg2dp_cube_out_width)

//     // WG: WidthGroup, including one FSPT, one LSPT, and many MSPT
//     val spt_dat_accept = Wire(Bool())
//     val count_wg = RegInit("b0".asUInt(8.W))
//     when(op_load){
//         when(spt_dat_accept){
//             when(is_cube_end){
//                 count_wg := 0.U
//             }
//             .elsewhen(is_split_end){
//                 count_wg := count_wg + 1.U
//             }
//         }
//     }

//     is_last_wg := count_wg === io.reg2dp_split_num
//     is_first_wg := count_wg === 0.U

//     is_fspt := cfg_mode_split & is_first_wg
//     is_lspt := cfg_mode_split & is_last_wg
//     is_mspt := cfg_mode_split & !is_fspt & !is_lspt

//     //================================================================
//     // C direction: count_b + count_surf
//     // count_b: in each W in line, will go 4 step in c first
//     // count_surf: when one surf with 4c is done, will go to next surf
//     //================================================================

//     //==============
//     // COUNT B
//     //==============
//     val spt_dat_accept = Wire(Bool())
//     val is_blk_end = Wire(Bool())
//     val count_b = RegInit(0.U)
//     when(spt_dat_accept){
//         when(is_blk_end){
//             count_b := 0.U
//         }
//         .otherwise{
//             count_b := count_b + 1.U
//         }
//     }

//     is_last_b := count_b === (conf.PDP_NUM - 1).U

//     //==============
//     // COUNT W
//     //==============
//     val count_w = RegInit(0.U)
//     when(spt_dat_accept){
//         when(is_line_end){
//             count_w := 0.U
//         }
//         .elsewhen(is_blk_end){
//             count_w := count_w + 1.U
//         }
//     }

//     is_last_w := (count_w === size_of_width)

//     //==============
//     // COUNT SURF
//     //==============
//     val count_surf = RegInit(0.U)
//     when(spt_dat_accept){
//         when(is_split_end){
//             count_surf := 0.U
//         }
//         .elsewhen(is_surf_end){
//             count_surf := count_surf + 1.U
//         }
//     }

//     is_last_surf := count_surf === io.reg2dp_cube_out_channel(12, conf.ATMMBW)

//     //==============
//     // COUNT HEIGHT 
//     //==============
//     val count_h = RegInit(0.U)
//     when(spt_dat_accept){
//         when(is_surf_end){
//             count_h := 0.U
//         }
//         .elsewhen(is_line_end){
//             count_h := count_h + 1.U
//         }
//     }

//     is_last_h := count_h === io.reg2dp_cube_out_height

//     //==============
//     // spt information gen
//     //==============
//     val spt_posb = count_b
//     val spt_posw = if(conf.ATMM_NUM == 1) "b0".asUInt(2.W) else Cat((2 - log2Ceil(conf.ATMM_NUM)), count_w(log2Ceil(conf.ATMM_NUM)-1, 0))

//     //==============
//     // Data FIFO WRITE contrl
//     //==============
//     val dp2wdma_dat_pd = io.dp2wdma_pd
//     val dat_fifo_wr_pvld = io.dp2wdma_vld
//     io.dp2wdma_rdy := dat_fifo_wr_prdy
//     val u_dat_fifo = Array.fill(conf.ATMM_NUM){Array.fill(conf.PDP_NUM){Module(new NV_NVDLA_PDP_WDMA_DAT_fifo(32, conf.PDPBW))}}

//     for(i <- 0 to conf.ATMM_NUM-1){
//         for(j <- 0 to conf.ATMM_NUM-1){
//             // DATA FIFO WRITE SIDE
//             // is last_b, then fifo idx large than count_b will need a push to fill in fake data to make up a full atomic_m
//             u_dat_fifo(i)(j).io.nvdla_core_clk

//     }

       
     











// }}

// class NV_NVDLA_PDP_WDMA_DAT_fifo(depth: Int, width: Int) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val clk = Input(Clock())

//         val wr_prdy = Output(Bool())
//         val wr_pvld = Input(Bool())
//         val wr_pd = Input(UInt(width.W))
//         val rd_prdy = Input(Bool())
//         val rd_pvld = Output(Bool())
//         val rd_pd = Output(UInt(width.W))

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
//     withClock(io.clk){
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
//     val clk_mgated_enable = Wire(Bool())
//     val clk_mgate = Module(new NV_CLK_gate_power)
//     clk_mgate.io.clk := io.clk
//     clk_mgate.io.clk_en := clk_mgated_enable
//     val clk_mgated = clk_mgate.io.clk_gated

//     ////////////////////////////////////////////////////////////////////////
//     // WRITE SIDE                                                        //
//     ////////////////////////////////////////////////////////////////////////
//     val wr_reserving = Wire(Bool())
//     val wr_busy_int = withClock(clk_mgated){RegInit(false.B)}  // copy for internal use
//     io.wr_prdy := !wr_busy_int
//     wr_reserving := io.wr_pvld && !wr_busy_int   // reserving write space?

//     val wr_popping = withClock(clk_mgated){RegInit(false.B)}       // fwd: write side sees pop?
//     val wr_count = withClock(clk_mgated){RegInit("b0".asUInt((log2Ceil(depth)+1).W))} // write-side count

//     val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, wr_count-1.U)
//     val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count+1.U, wr_count)
//     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

//     val wr_count_next_no_wr_popping_is_full = (wr_count_next_no_wr_popping === depth.U)
//     val wr_count_next_is_full = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_full)

//     val wr_limit_muxed = Wire(UInt((log2Ceil(depth)+1).W))    // muxed with simulation/emulation overrides
//     val wr_limit_reg = wr_limit_muxed
//     val wr_busy_next = wr_count_next_is_full ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

//     wr_busy_int := wr_busy_next
//     when(wr_reserving ^ wr_popping){
//         wr_count := wr_count_next
//     }

//     val wr_pushing = wr_reserving  // data pushed same cycle as wr_pvld

//     //
//     // RAM
//     //  

//     val wr_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))}   // current write address
//     val rd_adr_p = Wire(UInt(log2Ceil(depth).W))       // read address to use for ram
//     val rd_pd_p = Wire(UInt(width.W))       // read data directly out of ram

//     val rd_enable = Wire(Bool())
//     val ore = Wire(Bool())

//     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
//     // Fifogen handles this by ignoring the data on the ram data out for that cycle.

//     val ram = Module(new nv_ram_rwsp(depth, width))
//     ram.io.clk := io.clk
//     ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ram.io.wa := wr_adr
//     ram.io.we := wr_pushing
//     ram.io.di := io.wr_pd
//     ram.io.ra := rd_adr_p   // for ram
//     ram.io.re := rd_enable
//     ram.io.ore := ore
//     rd_pd_p := ram.io.dout
    
//     // next wr_adr if wr_pushing=1
//     val wr_adr_next = wr_adr + 1.U
//     when(wr_pushing){
//         wr_adr := wr_adr_next
//     }

//     val rd_popping = Wire(Bool())
//     val rd_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))} 
//     // next    read address
//     val rd_adr_next = rd_adr + 1.U
//     rd_adr_p := Mux(rd_popping, rd_adr_next, rd_adr)
//     when(rd_popping){
//         rd_adr := rd_adr_next
//     }

//     //
//     // SYNCHRONOUS BOUNDARY
//     //
//     wr_popping := rd_popping    
//     val rd_pushing = withClock(clk_mgated){RegNext(wr_pushing, false.B)} 

//     //
//     // READ SIDE
//     //
//     val rd_pvld_p = withClock(clk_mgated){RegInit(false.B)}  // data out of fifo is valid
//     val rd_pvld_int = withClock(clk_mgated){RegInit(false.B)} // internal copy of rd_req
//     io.rd_pvld := rd_pvld_int
//     rd_popping := rd_pvld_p && !(rd_pvld_int && !io.rd_prdy)

//     val rd_count_p = withClock(clk_mgated){RegInit("b0".asUInt((log2Ceil(depth)+1).W))} //read-side fifo count
//     val rd_count_p_next_rd_popping = Mux(rd_pushing, rd_count_p, rd_count_p-1.U)
//     val rd_count_p_next_no_rd_popping = Mux(rd_pushing, rd_count_p + 1.U, rd_count_p)
//     val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)

//     val rd_count_p_next_rd_popping_not_0 = rd_count_p_next_rd_popping =/= 0.U
//     val rd_count_p_next_no_rd_popping_not_0 = rd_count_p_next_no_rd_popping =/= 0.U
//     val rd_count_p_next_not_0 = Mux(rd_popping, rd_count_p_next_rd_popping_not_0, rd_count_p_next_no_rd_popping_not_0)

//     rd_enable := ((rd_count_p_next_not_0) && ((~rd_pvld_p) || rd_popping));  // anytime data's there and not stalled

    
//     when(rd_pushing || rd_popping){
//         rd_count_p := rd_count_p_next
//         rd_pvld_p := rd_count_p_next_not_0
//     }
    
//     val rd_req_next = (rd_pvld_p || (rd_pvld_int && !io.rd_prdy))

//     rd_pvld_int := rd_req_next

//     io.rd_pd := rd_pd_p
//     ore := rd_popping

//     clk_mgated_enable := ((wr_reserving || wr_pushing || rd_popping ||
//                          wr_popping || (io.wr_pvld && !wr_busy_int) ||
//                           (wr_busy_int =/= wr_busy_next)) || (rd_pushing ||
//                            rd_popping || (rd_pvld_int && io.rd_prdy) || wr_pushing))

//     wr_limit_muxed := "d0".asUInt((log2Ceil(depth)+1).W)

    
// }}





// object NV_NVDLA_PDP_WDMA_cmdDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_WDMA_cmd())
// }