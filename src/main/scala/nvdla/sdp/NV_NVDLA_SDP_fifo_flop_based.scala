// package nvdla

// import chisel3._
// import chisel3.util._
// import chisel3.experimental._


// //NV_NVDLA_SDP_MRDMA_EG_CMD_sfifo

// class NV_NVDLA_SDP_fifo_flop_based(depth: Int, width: Int) extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val clk = Input(Clock())

//         //wr pipeline / ig2cq -- write(wr) 
//         val wr_rdy = Output(Bool())
//         val wr_vld = Input(Bool())  
//         val wr_data = Input(UInt(width.W))

//         //rd pipeline / cq2eg -- read(rd)
//         val rd_rdy = Input(Bool())
//         val rd_vld = Output(Bool()) 
//         val rd_data = Output(UInt(width.W)) 

//         //
//         val pwrbus_ram_pd = Input(UInt(32.W))

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
// withClock(io.clk){    
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

//     val clk_mgated_enable = Wire(Bool())  // assigned by code at end of this module
//     val clk_mgate = Module(new NV_CLK_gate_power)
//     clk_mgate.io.clk := io.clk
//     clk_mgate.io.clk_en := clk_mgated_enable 
//     val clk_mgated = clk_mgate.io.clk_gated

//     // 
//     // WRITE SIDE
//     //  
//     val wr_reserving = Wire(Bool()) 
//     val wr_busy_int = withClock(clk_mgated){RegInit(false.B)}    // copy for internal use
//     io.wr_rdy := !wr_busy_int
//     wr_reserving := io.wr_vld & !wr_busy_int   // reserving write space?
 
//     val wr_popping = withClock(clk_mgated){RegInit(false.B)}  // fwd: write side sees pop?
//     val wr_count = withClock(clk_mgated){RegInit(0.U)}   // write-side 
    
//     val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, (wr_count - 1.U))
//     val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count + 1.U, wr_count)
//     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

//     val wr_count_next_no_wr_popping_is_full = ( wr_count_next_no_wr_popping === depth.U)
//     val wr_count_next_is_full = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_full)

//     val wr_limit_muxed = Wire(UInt((log2Ceil(depth)+1).W))  // muxed with simulation/emulation overrides
//     val wr_limit_reg = wr_limit_muxed
//     val wr_busy_next = wr_count_next_is_full || (wr_limit_reg =/= 0.U &&  wr_count_next>= wr_limit_reg)
    
//     wr_busy_int := wr_busy_next
//     when (wr_reserving ^ wr_popping) {
//         wr_count := wr_count_next
//     } 

//     val wr_pushing = wr_reserving   // data pushed same cycle as wr_vld

//     //RAM

//     val wr_adr = withClock(clk_mgated){RegInit(0.U)} 			// current write address
//     val rd_adr_p = Wire(UInt(log2Ceil(depth).W))                // read address to use for ram
//     val rd_enable = Wire(Bool())
//     val ore = Wire(Bool())

//     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
//     // Fifogen handles this by ignoring the data on the ram data out for that cycle.

//     val ram = Module(new nv_ram_rwsp(depth, width))
//     ram.io.clk := io.clk
//     ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ram.io.wa := wr_adr
//     ram.io.we := wr_pushing 
//     ram.io.di := io.wr_data
//     ram.io.ra := rd_adr_p 
//     ram.io.re := rd_enable 
//     val rd_data_p = ram.io.dout
//     ram.io.ore := ore

//     // next lat_wr_adr if wr_pushing=1
//     val wr_adr_next = wr_adr + 1.U
//     when (wr_pushing) {
//         wr_adr := wr_adr_next
//     }   
//     val rd_popping = Wire(Bool())// read side doing pop this cycle?
//     val rd_adr = withClock(clk_mgated){RegInit(0.U)}  // current read address

//     // next    read address 
//     val rd_adr_next = rd_adr + 1.U
//     rd_adr_p := Mux(rd_popping, rd_adr_next, rd_adr) //for ram

//     when (rd_popping) {
//         rd_adr := rd_adr_next
//     }  

//     //
//     // SYNCHRONOUS BOUNDARY
//     //
//     wr_popping := rd_popping 

//     val rd_pushing = withClock(clk_mgated){RegInit(false.B)}
//     rd_pushing := wr_pushing// let data go into ram first 

//     //
//     // READ SIDE
//     //

//     val rd_vld_p = withClock(clk_mgated){RegInit(false.B)} // data out of fifo is valid
//     val rd_vld_int = withClock(clk_mgated){RegInit(false.B)} // internal copy of rd_vld
//     io.rd_vld := rd_vld_int
//     rd_popping := rd_vld_p && !(rd_vld_int && !io.rd_rdy)
//     val rd_count_p = withClock(clk_mgated){RegInit(0.U)} // read-side fifo count
//     // spyglass disable_block W164a W484

//     val rd_count_p_next_rd_popping = Mux(rd_pushing, rd_count_p, rd_count_p - 1.U)
//     val rd_count_p_next_no_rd_popping =  Mux(rd_pushing, rd_count_p + 1.U, rd_count_p)

//     // spyglass enable_block W164a W484
//     val rd_count_p_next = Mux(rd_popping,  rd_count_p_next_rd_popping,  rd_count_p_next_no_rd_popping)
//     val rd_count_p_next_rd_popping_not_0= (rd_count_p_next_rd_popping =/= 0.U)
//     val rd_count_p_next_no_rd_popping_not_0 = (rd_count_p_next_no_rd_popping =/= 0.U)
//     val rd_count_p_next_not_0 = Mux(rd_popping, rd_count_p_next_rd_popping_not_0, rd_count_p_next_no_rd_popping_not_0)

//     rd_enable := (rd_count_p_next_not_0) && (!rd_vld_p || rd_popping)    // anytime data's there and not stalled

//     when(rd_pushing || rd_popping){
//         rd_count_p := rd_count_p_next 
//         rd_vld_p := rd_count_p_next_not_0
//     } 

//     val rd_vld_next = (rd_vld_p || (rd_vld_int && !io.rd_rdy))
//     rd_vld_int := rd_vld_next

//     io.rd_data := rd_data_p
//     ore := rd_popping

//     // Master Clock Gating (SLCG) Enables
//     //

//     clk_mgated_enable := ((wr_reserving || wr_pushing || rd_popping || wr_popping || 
//                          (io.wr_vld && !wr_busy_int) || (wr_busy_int =/= wr_busy_next)) || 
//                          (rd_pushing || rd_popping || (rd_vld_int && io.rd_rdy) || wr_pushing))
 
//     wr_limit_muxed := 0.U
  
// }}

// class NV_NVDLA_SDP_MRDMA_EG_CMD_sfifo extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val spt_fifo_prdy = Output(Bool())
//         val spt_fifo_pvld = Input(Bool())
//         val spt_fifo_pd = Input(UInt(13.W))

//         val cmd2dat_spt_prdy = Input(Bool())
//         val cmd2dat_spt_pvld = Output(Bool())
//         val cmd2dat_spt_pd = Output(UInt(13.W))

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
// withClock(io.nvdla_core_clk){

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
//     val spt_fifo_busy_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // copy for internal use
//     io.spt_fifo_prdy := !spt_fifo_busy_int
//     wr_reserving := io.spt_fifo_pvld && !spt_fifo_busy_int   // reserving write space?

//     val wr_popping = Wire(Bool())// fwd: write side sees pop?
//     val spt_fifo_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(3.W))} // write-side count
//     val wr_count_next_wr_popping = Mux(wr_reserving, spt_fifo_count, spt_fifo_count-1.U)
//     val wr_count_next_no_wr_popping = Mux(wr_reserving, spt_fifo_count+1.U, spt_fifo_count)
//     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

//     val wr_count_next_no_wr_popping_is_4 = (wr_count_next_no_wr_popping === 4.U)
//     val wr_count_next_is_4 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_4)
//     val wr_limit_muxed = Wire(UInt(3.W))    // muxed with simulation/emulation overrides
//     val wr_limit_reg = wr_limit_muxed
//     val spt_fifo_busy_next = wr_count_next_is_4 ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

//     spt_fifo_busy_int := spt_fifo_busy_next
//     when(wr_reserving ^ wr_popping){
//         spt_fifo_count := wr_count_next
//     }

//     val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

//     //
//     // RAM
//     //  

//     val spt_fifo_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))}
//     val spt_fifo_adr_next = spt_fifo_adr + 1.U
//     when(wr_pushing){
//         spt_fifo_adr := spt_fifo_adr_next
//     }
//     val rd_popping = Wire(Bool())

//     val cmd2dat_spt_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))}   // read address this cycle
//     val ram_we = wr_pushing && (spt_fifo_count > 0.U || !rd_popping)      // note: write occurs next cycle
    

//     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
//     // Fifogen handles this by ignoring the data on the ram data out for that cycle.
//     val ram = Module(new NV_NVDLA_SDP_MRDMA_EG_CMD_sfifo_flopram_rwsa_4x13())
//     ram.io.clk := nvdla_core_clk_mgated
//     ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ram.io.di := io.spt_fifo_pd
//     ram.io.we := ram_we
//     ram.io.wa := spt_fifo_adr
//     ram.io.ra := Mux(spt_fifo_count === 0.U, 4.U, Cat(false.B, cmd2dat_spt_adr))
//     io.cmd2dat_spt_pd := ram.io.dout
    

//     val rd_adr_next_popping = cmd2dat_spt_adr + 1.U
//     when(rd_popping){
//         cmd2dat_spt_adr := rd_adr_next_popping
//     }

//     //
//     // SYNCHRONOUS BOUNDARY
//     //
//     wr_popping := rd_popping    // let it be seen immediately
//     val rd_pushing = wr_pushing // let it be seen immediately

//     //
//     // READ SIDE
//     //
//     rd_popping := io.cmd2dat_spt_pvld && io.cmd2dat_spt_prdy

//     val cmd2dat_spt_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(3.W))} //read-side fifo count
//     val rd_count_next_rd_popping = Mux(rd_pushing, cmd2dat_spt_count, cmd2dat_spt_count - 1.U)
//     val rd_count_next_no_rd_popping = Mux(rd_pushing, cmd2dat_spt_count + 1.U, cmd2dat_spt_count)
//     val rd_count_next = Mux(rd_popping, rd_count_next_rd_popping, rd_count_next_no_rd_popping)
//     io.cmd2dat_spt_pvld := cmd2dat_spt_count =/= 0.U || rd_pushing;
//     when(rd_pushing || rd_popping){
//         cmd2dat_spt_count := rd_count_next
//     }

//     nvdla_core_clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
//                                     (io.spt_fifo_pvld && !spt_fifo_busy_int) || (spt_fifo_busy_int =/= spt_fifo_busy_next)) || 
//                                     (rd_pushing || rd_popping || (io.cmd2dat_spt_pvld && io.cmd2dat_spt_prdy)) || (wr_pushing))

//     wr_limit_muxed := "d0".asUInt(3.W)

// }}

// // 
// // Flop-Based RAM 
// //

// class NV_NVDLA_SDP_MRDMA_EG_CMD_sfifo_flopram_rwsa_4x13 extends Module{
//   val io = IO(new Bundle{
//         val clk = Input(Clock())    // write clock

//         val di = Input(UInt(13.W))
//         val we = Input(Bool())
//         val wa = Input(UInt(2.W))
//         val ra = Input(UInt(3.W))
//         val dout = Output(UInt(13.W))

//         val pwrbus_ram_pd = Input(UInt(32.W))

//   })  
// withClock(io.clk){
//     val ram_ff = Seq.fill(4)(Reg(UInt(13.W))) :+ Wire(UInt(13.W))
//     when(io.we){
//         for(i <- 0 to 3){
//             when(io.wa === i.U){
//                 ram_ff(i) := io.di
//             }
//         } 
//     }   
//     ram_ff(4) := io.di
//     io.dout := MuxLookup(io.ra, "b0".asUInt(13.W), 
//         (0 to 4) map { i => i.U -> ram_ff(i)} )
// }}


// class NV_NVDLA_SDP_MRDMA_EG_CMD_dfifo extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val dma_fifo_prdy = Output(Bool())
//         val dma_fifo_pvld = Input(Bool())
//         val dma_fifo_pd = Input(UInt(15.W))
//         val cmd2dat_dma_prdy = Input(Bool())
//         val cmd2dat_dma_pvld = Output(Bool())
//         val cmd2dat_dma_pd = Output(UInt(15.W))

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
// withClock(io.nvdla_core_clk){

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
//     val dma_fifo_busy_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // copy for internal use
//     io.dma_fifo_prdy := !dma_fifo_busy_int
//     wr_reserving := io.dma_fifo_pvld && !dma_fifo_busy_int   // reserving write space?

//     val wr_popping = Wire(Bool())// fwd: write side sees pop?
//     val dma_fifo_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(3.W))} // write-side count
//     val wr_count_next_wr_popping = Mux(wr_reserving, dma_fifo_count, dma_fifo_count-1.U)
//     val wr_count_next_no_wr_popping = Mux(wr_reserving, dma_fifo_count+1.U, dma_fifo_count)
//     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

//     val wr_count_next_no_wr_popping_is_4 = (wr_count_next_no_wr_popping === 4.U)
//     val wr_count_next_is_4 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_4)
//     val wr_limit_muxed = Wire(UInt(3.W))    // muxed with simulation/emulation overrides
//     val wr_limit_reg = wr_limit_muxed
//     val dma_fifo_busy_next = wr_count_next_is_4 ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

//     dma_fifo_busy_int := dma_fifo_busy_next
//     when(wr_reserving ^ wr_popping){
//         dma_fifo_count := wr_count_next
//     }

//     val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

//     //
//     // RAM
//     //  

//     val dma_fifo_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))}
//     val dma_fifo_adr_next = dma_fifo_adr + 1.U
//     when(wr_pushing){
//         dma_fifo_adr := dma_fifo_adr_next
//     }
//     val rd_popping = Wire(Bool())

//     val cmd2dat_dma_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))}   // read address this cycle
//     val ram_we = wr_pushing && (dma_fifo_count > 0.U || !rd_popping)      // note: write occurs next cycle
    

//     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
//     // Fifogen handles this by ignoring the data on the ram data out for that cycle.
//     val ram = Module(new NV_NVDLA_SDP_MRDMA_EG_CMD_dfifo_flopram_rwsa_4x15())
//     ram.io.clk := nvdla_core_clk_mgated
//     ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ram.io.di := io.dma_fifo_pd
//     ram.io.we := ram_we
//     ram.io.wa := dma_fifo_adr
//     ram.io.ra := Mux(dma_fifo_count === 0.U, 4.U, Cat(false.B, cmd2dat_dma_adr))
//     io.cmd2dat_dma_pd := ram.io.dout
    

//     val rd_adr_next_popping = cmd2dat_dma_adr + 1.U
//     when(rd_popping){
//         cmd2dat_dma_adr := rd_adr_next_popping
//     }

//     //
//     // SYNCHRONOUS BOUNDARY
//     //
//     wr_popping := rd_popping    // let it be seen immediately
//     val rd_pushing = wr_pushing // let it be seen immediately

//     //
//     // READ SIDE
//     //
//     rd_popping := io.cmd2dat_dma_pvld && io.cmd2dat_dma_prdy

//     val cmd2dat_dma_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(3.W))} //read-side fifo count
//     val rd_count_next_rd_popping = Mux(rd_pushing, cmd2dat_dma_count, cmd2dat_dma_count - 1.U)
//     val rd_count_next_no_rd_popping = Mux(rd_pushing, cmd2dat_dma_count + 1.U, cmd2dat_dma_count)
//     val rd_count_next = Mux(rd_popping, rd_count_next_rd_popping, rd_count_next_no_rd_popping)
//     io.cmd2dat_dma_pvld := cmd2dat_dma_count =/= 0.U || rd_pushing;
//     when(rd_pushing || rd_popping){
//         cmd2dat_dma_count := rd_count_next
//     }

//     nvdla_core_clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
//                                     (io.dma_fifo_pvld && !dma_fifo_busy_int) || (dma_fifo_busy_int =/= dma_fifo_busy_next)) || 
//                                     (rd_pushing || rd_popping || (io.cmd2dat_dma_pvld && io.cmd2dat_dma_prdy)) || (wr_pushing))

//     wr_limit_muxed := "d0".asUInt(3.W)

// }}

// // 
// // Flop-Based RAM 
// //

// class NV_NVDLA_SDP_MRDMA_EG_CMD_dfifo_flopram_rwsa_4x15 extends Module{
//   val io = IO(new Bundle{
//         val clk = Input(Clock())    // write clock

//         val di = Input(UInt(15.W))
//         val we = Input(Bool())
//         val wa = Input(UInt(2.W))
//         val ra = Input(UInt(3.W))
//         val dout = Output(UInt(15.W))

//         val pwrbus_ram_pd = Input(UInt(32.W))

//   })  
// withClock(io.clk){
//     val ram_ff = Seq.fill(4)(Reg(UInt(15.W))) :+ Wire(UInt(15.W))
//     when(io.we){
//         for(i <- 0 to 3){
//             when(io.wa === i.U){
//                 ram_ff(i) := io.di
//             }
//         } 
//     }   
//     ram_ff(4) := io.di
//     io.dout := MuxLookup(io.ra, "b0".asUInt(15.W), 
//         (0 to 4) map { i => i.U -> ram_ff(i)} )
// }}