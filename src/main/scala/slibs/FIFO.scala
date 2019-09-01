 package nvdla

 import chisel3._
 import chisel3.experimental._
 import chisel3.util._


 class NV_NVDLA_fifo(depth: Int, width: Int, wr_empty_port: Boolean,
                     wr_idle_port: Boolean, rd_idle_port: Boolean,
                     ram_type: Int,
                     distant_wr_req: Boolean, distant_wr_data: Boolean,
                     distant_rd_req: Boolean, distant_rd_data: Boolean) extends Module {
     val io = IO(new Bundle {
         //clk
         val clk = Input(Clock())

         val wr_ready = Output(Bool())
         val wr_empty = if(wr_empty_port) Some(Output(Bool())) else None
         val wr_idle = if(wr_idle_port) Some(Output(Bool())) else None
         val wr_req = Input(Bool())
         val wr_data = Input(UInt(width.W))
        
         val rd_ready = Input(Bool())
         val rd_req = Output(Bool())
         val rd_data = Output(UInt(width.W))
         val rd_idle = if(rd_idle_port) Some(Output(Bool())) else None

         val pwrbus_ram_pd = Input(UInt(32.W))
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
     withClock(io.clk){
     // Master Clock Gating (SLCG)
     //
     // We gate the clock(s) when idle or stalled.
     // This allows us to turn off numerous miscellaneous flops
     // that don't get gated during synthesis for one reason or another.
     //
     // We gate write side and read side separately.
     // If the fifo is synchronous, we also gate the ram separately, but if
     // -master_clk_gated_unified or -status_reg/-status_logic_reg is specified,
     // then we use one clk gate for write, ram, and read.
     //
     val clk_mgated_enable = Wire(Bool())
     val clk_mgate = Module(new NV_CLK_gate_power)
     clk_mgate.io.clk := io.clk
     clk_mgate.io.clk_en := clk_mgated_enable
     val clk_mgated = clk_mgate.io.clk_gated

     ////////////////////////////////////////////////////////////////////////
     // WRITE SIDE                                                        //
     ////////////////////////////////////////////////////////////////////////
     val wr_reserving = Wire(Bool())
     val wr_busy_int = withClock(clk_mgated){RegInit(false.B)}  // copy for internal use

     val wr_req_in = if(distant_wr_req) RegInit(false.B) else io.wr_req    // registered wr_req
     val wr_data_in = if(distant_wr_req & distant_wr_data) Reg(UInt(width.W)) else io.wr_data   // registered wr_data
     val wr_busy_in = if(distant_wr_req) RegInit(false.B) else wr_busy_int    // inputs being held this cycle?
     val wr_busy_next = Wire(Bool())     // fwd: fifo busy next?

     // factor for better timing with distant wr_req signal
     if(distant_wr_req){
         val wr_busy_in_next_wr_req_eq_1 = wr_busy_next
         val wr_busy_in_next_wr_req_eq_0 = (wr_req_in && wr_busy_next) && !wr_reserving
         val wr_busy_in_next = Mux(io.wr_req, wr_busy_in_next_wr_req_eq_1, wr_busy_in_next_wr_req_eq_0)
         val wr_busy_in_int = wr_req_in && wr_busy_int

         wr_busy_in := wr_busy_in_next
         when(!wr_busy_in_int){
             wr_req_in := io.wr_req && !wr_busy_in
         }

         if(distant_wr_data){
             when(!wr_busy_in && io.wr_req){
                 wr_data_in := io.wr_data
             }
         }
     }

     io.wr_ready := !wr_busy_in
     wr_reserving := wr_req_in && !wr_busy_int   // reserving write space?

     val wr_popping = Wire(Bool())       // fwd: write side sees pop?
     val wr_count = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))} // write-side count
     val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, wr_count-1.U)
     val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count+1.U, wr_count)
     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

     val wr_count_next_no_wr_popping_is_full = (wr_count_next_no_wr_popping === depth.U)
     val wr_count_next_is_full = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_full)

     val wr_limit_muxed = Wire(UInt(log2Ceil(depth+1).W))    // muxed with simulation/emulation overrides
     val wr_limit_reg = wr_limit_muxed
     wr_busy_next := wr_count_next_is_full ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

     wr_busy_int := wr_busy_next
     when(wr_reserving ^ wr_popping){
         wr_count := wr_count_next
     }

//     if(empty){
//         io.wr_empty.get := RegNext(wr_count_next === 0.U && !io.wr_req, true.B)
//     }
    
     val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

     //
     // RAM
     //

     val wr_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))} // current write address
     val wr_adr_next = wr_adr + 1.U
     when(wr_pushing){
         wr_adr := wr_adr_next
     }
     val rd_popping = Wire(Bool())

     val rd_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))}   // read address this cycle
     val ram_we = wr_pushing && (wr_count > 0.U || !rd_popping)      // note: write occurs next cycle
    
     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
     // Fifogen handles this by ignoring the data on the ram data out for that cycle.
     if(ram_type == 1){
//         val ram_iwe = !wr_busy_in && io.wr_req
         val ram = Module(new nv_flopram(depth, width))
         ram.io.clk := clk_mgated
         ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
         ram.io.wa := wr_adr
         ram.io.we := ram_we
         ram.io.di := wr_data_in
         ram.io.ra := Mux(wr_count === 0.U, depth.U, rd_adr)
         io.rd_data := ram.io.dout
     }

     if(ram_type == 2){
         val ram_iwe = !wr_busy_in && io.wr_req
         val ram = Module(new nv_flopram_internal_wr_reg(depth, width))
         ram.io.clk := io.clk
         ram.io.clk_mgated := clk_mgated
         ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
         ram.io.wa := wr_adr
         ram.io.we := ram_we
         ram.io.di := wr_data_in
         ram.io.ra := Mux(wr_count === 0.U, depth.U, rd_adr)
         ram.io.iwe := ram_iwe
         io.rd_data := ram.io.dout
     }

/*
     if(ram_type == 4){
         val ram = Module(new nv_ram_rwsp(depth, width))
         ram.io.clk := io.clk
         ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
         ram.io.wa := wr_adr
         ram.io.we := wr_pushing
         ram.io.di := wr_data_in
         ram.io.ra := Mux(rd_popping, rd_adr + 1.U, rd_adr)   // for ram
         ram.io.re := rd_enable
         ram.io.ore := ore
         io.rd_data := ram.io.dout
     }
*/

     val rd_adr_next_popping = rd_adr + 1.U
     when(rd_popping){
         rd_adr := rd_adr_next_popping
     }

     //
     // SYNCHRONOUS BOUNDARY
     //
     wr_popping := rd_popping    // let it be seen immediately
     val rd_pushing = wr_pushing // let it be seen immediately

     //
     // READ SIDE
     //

     val rd_req_p = withClock(clk_mgated){RegInit(false.B)} // data out of fifo is valid

     rd_popping := io.rd_req && io.rd_ready
     val rd_count = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))}
     val rd_count_next_rd_popping = Mux(rd_pushing, rd_count, rd_count-1.U)
     val rd_count_next_no_rd_popping = Mux(rd_pushing, rd_count + 1.U, rd_count)
     val rd_count_next = Mux(rd_popping, rd_count_next_rd_popping, rd_count_next_no_rd_popping)

     io.rd_req := rd_count=/= 0.U|rd_pushing
     when(rd_pushing || rd_popping){
         rd_count := rd_count_next
     }

     //
     // Read-side Idle Calculation
     //

//     val rd_idle = !rd_pvld_int && !rd_pushing && rd_count_p === 0.U

     //
     // Write-Side Idle Calculation
     //

//     if(wr_idle_port){
//         io.wr_idle := !wr_pvld_in && rd_idle && !wr_pushing && wr_count === 0.U
//     }

    
     // Master Clock Gating (SLCG) Enables
     //

     clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping ||
                          (wr_req_in && !wr_busy_int) || (wr_busy_int =/= wr_busy_next)) ||
                          (rd_pushing || rd_popping || (io.rd_req && io.rd_ready)) ||
                          (wr_pushing))

     wr_limit_muxed := "d0".asUInt(log2Ceil(depth+1).W)

    }
}

 object NV_NVDLA_fifoDriver extends App {
     chisel3.Driver.execute(args, () => new NV_NVDLA_fifo(depth=16,
         width = 256, wr_empty_port = false, wr_idle_port = false, rd_idle_port = false,
         ram_type = 1,
         distant_rd_data = false, distant_wr_data = false, distant_rd_req = false, distant_wr_req = false))
 }