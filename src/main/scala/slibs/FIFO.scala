package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_fifo(depth: Int, width: Int,
                    ram_type: Int, 
                    distant_wr_req: Boolean, 
                    io_wr_empty: Boolean = false, 
                    io_wr_idle: Boolean = false,
                    io_wr_count: Boolean = false,
                    io_rd_idle: Boolean = false) extends Module {
    val io = IO(new Bundle {
        //clk
        val clk = Input(Clock())

        val wr_pvld = Input(Bool())
        val wr_prdy = Output(Bool())
        val wr_pd = Input(UInt(width.W))

        val wr_count =  if(io_wr_count) Some(Output(UInt(log2Ceil(depth+1).W))) else None
        val wr_empty = if(io_wr_empty) Some(Output(Bool())) else None
        val wr_idle = if(io_wr_idle) Some(Output(Bool())) else None
        
        val rd_pvld = Output(Bool())
        val rd_prdy = Input(Bool())  
        val rd_pd = Output(UInt(width.W))

        val rd_idle = if(io_rd_idle) Some(Output(Bool())) else None

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

    if(depth == 0){
        // 
        // WRITE SIDE
        //  
        //          
        // NOTE: 0-depth fifo has no write side
        //          

        //
        // RAM
        //
        //
        // NOTE: 0-depth fifo has no ram.
        //

        val rd_pd_p = io.wr_pd

        //
        // SYNCHRONOUS BOUNDARY
        //

        //
        // NOTE: 0-depth fifo has no real boundary between write and read sides
        //
        val rd_prdy_d = RegInit(true.B)  // rd_prdy registered in cleanly

        rd_prdy_d := io.rd_prdy

        val rd_prdy_d_o = Wire(Bool())            // combinatorial rd_busy

        val rd_pvld_int = RegInit(false.B)        // internal copy of rd_pvld
        io.rd_pvld := rd_pvld_int
        val rd_pvld_p = io.wr_pvld      // no real fifo, take from write-side input
        val rd_pvld_int_o = withClock(clk_mgated){RegInit(false.B)}    // internal copy of rd_pvld_o
        val rd_pvld_o = rd_pvld_int_o
        val rd_popping = rd_pvld_p && !(rd_pvld_int_o && !rd_prdy_d_o);

        // 
        // SKID for -rd_busy_reg
        //
        val rd_pd_o = withClock(clk_mgated){Reg(UInt(width.W))} // output data register
        val rd_pvld_next_o = (rd_pvld_p || (rd_pvld_int_o && !rd_prdy_d_o))

        rd_pvld_int_o := rd_pvld_next_o
        rd_pd_o := Fill(width, false.B)

        //
        // FINAL OUTPUT
        //
        val rd_pd_out = Reg(UInt(width.W))  // output data register
        val rd_pvld_int_d = RegInit(false.B)    // so we can bubble-collapse rd_prdy_d
        rd_prdy_d_o := !((rd_pvld_o && rd_pvld_int_d && !rd_prdy_d))
        val rd_pvld_next = Mux(!rd_prdy_d_o,  rd_pvld_o, rd_pvld_p)  

        when(!rd_pvld_int || io.rd_prdy ){
            rd_pvld_int := rd_pvld_next
        }
        rd_pvld_int_d := rd_pvld_int

        when(rd_pvld_next && (!rd_pvld_int || io.rd_prdy)){
            rd_pd_out := MuxLookup(!rd_prdy_d_o, Fill(width, false.B),
                    Array(
                    0.U -> rd_pd_p,
                    1.U -> rd_pd_o
                    ))
        }
        io.rd_pd := rd_pd_out
        clk_mgated_enable := (false.B || (io.wr_pvld || 
                            (rd_pvld_int && rd_prdy_d) || 
                            (rd_pvld_int_o && rd_prdy_d_o)))       
    }

    else{

        ////////////////////////////////////////////////////////////////////////
        // WRITE SIDE                                                        //
        ////////////////////////////////////////////////////////////////////////
        val wr_reserving = Wire(Bool())
        val wr_busy_int = withClock(clk_mgated){RegInit(false.B)}  // copy for internal use

        val wr_pvld_in = if(distant_wr_req) RegInit(false.B) else io.wr_pvld    // registered wr_pvld
        val wr_pd_in = if(distant_wr_req & (ram_type == 2)) Reg(UInt(width.W)) else io.wr_pd   // registered wr_pd
        val wr_busy_in = if(distant_wr_req) RegInit(false.B) else wr_busy_int    // inputs being held this cycle?
        val wr_busy_next = Wire(Bool())     // fwd: fifo busy next?

        // factor for better timing with distant wr_pvld signal
        if(distant_wr_req){
            val wr_busy_in_next_wr_pvld_eq_1 = wr_busy_next
            val wr_busy_in_next_wr_pvld_eq_0 = (wr_pvld_in && wr_busy_next) && !wr_reserving
            val wr_busy_in_next = Mux(io.wr_pvld, wr_busy_in_next_wr_pvld_eq_1, wr_busy_in_next_wr_pvld_eq_0)
            val wr_busy_in_int = wr_pvld_in && wr_busy_int

            wr_busy_in := wr_busy_in_next
            when(!wr_busy_in_int){
                wr_pvld_in := io.wr_pvld && !wr_busy_in
            }

            if(ram_type == 2){
                when(!wr_busy_in && io.wr_pvld){
                    wr_pd_in := io.wr_pd
                }
            }
        }

        io.wr_prdy := !wr_busy_in
        wr_reserving := wr_pvld_in && !wr_busy_int   // reserving write space?

        val wr_popping = Wire(Bool())       // fwd: write side sees pop?
        val wr_count = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))} // write-side count
        if(io_wr_count){
            io.wr_count.get := wr_count
        }
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

        if(io_wr_empty){
            io.wr_empty.get := RegNext(wr_count_next === 0.U && !io.wr_pvld, true.B)
        }
        val wr_pushing = wr_reserving // data pushed same cycle as wr_pvld_in
        
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
        val rd_adr_next_popping = rd_adr + 1.U
        when(rd_popping){
            rd_adr := rd_adr_next_popping
        }
         
        val rd_pd_p = Wire(UInt(width.W))
        // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
        // Fifogen handles this by ignoring the data on the ram data out for that cycle.
        if(ram_type == 0){
            val ram_we = wr_pushing && (wr_count > 0.U || !rd_popping)      // note: write occurs next cycle

            val ram = Module(new nv_flopram(depth, width, false))
            ram.io.clk := clk_mgated
            ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
            ram.io.wa := wr_adr
            ram.io.we := ram_we
            ram.io.di := wr_pd_in
            ram.io.ra := Mux(wr_count === 0.U, depth.U, rd_adr)
            rd_pd_p := ram.io.dout
        }

        if(ram_type == 1){
            val ram_iwe = !wr_busy_in && io.wr_pvld
            val ram_we = wr_pushing && (wr_count > 0.U || !rd_popping)      // note: write occurs next cycle

            val ram = Module(new nv_flopram(depth, width, true))
            ram.io.clk := io.clk
            ram.io.clk_mgated.get := clk_mgated
            ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
            ram.io.di := wr_pd_in
            ram.io.iwe.get := ram_iwe
            ram.io.we := ram_we
            ram.io.wa := wr_adr
            ram.io.ra := Mux(wr_count === 0.U, depth.U, rd_adr)
            rd_pd_p := ram.io.dout
        }
        val rd_enable = if(ram_type == 2) Some(Wire(Bool())) else None
        if(ram_type == 2){
            //need two cycles
            val ram = Module(new nv_ram_rwsp(depth, width))
            ram.io.clk := io.clk
            ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
            ram.io.wa := wr_adr
            ram.io.we := wr_pushing
            ram.io.di := wr_pd_in
            ram.io.ra := Mux(rd_popping, rd_adr_next_popping, rd_adr)   // for ram
            ram.io.re := rd_enable.get
            ram.io.ore := rd_popping
            rd_pd_p := ram.io.dout
        }


        //
        // SYNCHRONOUS BOUNDARY
        //
        wr_popping := rd_popping    // let it be seen immediately
        val rd_pushing = if(ram_type == 2) withClock(clk_mgated){RegNext(wr_pushing, false.B)} else wr_pushing // let data go into ram first or let it be seen immediately

        //
        // READ SIDE
        //

        val rd_pvld_p = withClock(clk_mgated){RegInit(false.B)} // data out of fifo is valid
        val rd_pvld_int = if(ram_type == 2) Some(withClock(clk_mgated){RegInit(false.B)}) else None // internal copy of rd_req
        
        rd_popping := io.rd_pvld && io.rd_prdy
        val rd_count = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))}
        val rd_count_next_rd_popping = Mux(rd_pushing, rd_count, rd_count-1.U)
        val rd_count_next_no_rd_popping = Mux(rd_pushing, rd_count + 1.U, rd_count)
        val rd_count_next = Mux(rd_popping, rd_count_next_rd_popping, rd_count_next_no_rd_popping)

        when(rd_pushing || rd_popping){
            rd_count := rd_count_next
        }

        if(ram_type == 2){
            val rd_count_p_next_rd_popping_not_0 = rd_count_next_rd_popping =/= 0.U
            val rd_count_p_next_no_rd_popping_not_0 = rd_count_next_no_rd_popping =/= 0.U
            val rd_count_p_next_not_0 = Mux(rd_popping, rd_count_p_next_rd_popping_not_0, rd_count_p_next_no_rd_popping_not_0)
            rd_enable.get := ((rd_count_p_next_not_0) && ((~rd_pvld_p) || rd_popping)); // anytime data's there and not stalled
            when(rd_pushing || rd_popping){
                rd_pvld_p := rd_count_p_next_not_0
            }
            val rd_pvld_next = (rd_pvld_p || (rd_pvld_int.get && !io.rd_prdy))
            rd_pvld_int.get := rd_pvld_next

            io.rd_pvld := rd_pvld_int.get
        }
        else{
            io.rd_pvld := rd_count=/= 0.U|rd_pushing
        }


        io.rd_pd := rd_pd_p

        //
        // Read-side Idle Calculation
        //

        if(io_rd_idle){
            io.rd_idle.get := !io.rd_pvld && !rd_pushing && rd_count === 0.U
        }
        
        //
        // Write-Side Idle Calculation
        //

        if(io_wr_idle){
            val rd_idle = !io.rd_pvld && !rd_pushing && rd_count === 0.U
            io.wr_idle.get := !wr_pvld_in && rd_idle && !wr_pushing && wr_count === 0.U
        }

        
        // Master Clock Gating (SLCG) Enables
        //

        clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping ||
                            (wr_pvld_in && !wr_busy_int) || (wr_busy_int =/= wr_busy_next)) || 
                            (rd_pushing || rd_popping || (io.rd_pvld && io.rd_prdy)) || 
                            (wr_pushing))

        wr_limit_muxed := "d0".asUInt(log2Ceil(depth+1).W)

    }
}}


