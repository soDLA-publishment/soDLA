package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_SDP_RDMA_fifo(depth: Int, width: Int) extends Module {
    val io = IO(new Bundle {
        //clk
        val clk = Input(Clock())

        val wr_prdy = Output(Bool())
        val wr_pvld = Input(Bool())
        val wr_pd = Input(UInt(width.W))
        val rd_prdy = Input(Bool())
        val rd_pvld = Output(Bool())
        val rd_pd = Output(UInt(width.W))

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
    io.wr_prdy := !wr_busy_int
    wr_reserving := io.wr_pvld && !wr_busy_int   // reserving write space?

    val wr_popping = withClock(clk_mgated){RegInit(false.B)}       // fwd: write side sees pop?
    val wr_count = withClock(clk_mgated){RegInit("b0".asUInt((log2Ceil(depth)+1).W))} // write-side count

    val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, wr_count-1.U)
    val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count+1.U, wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_full = (wr_count_next_no_wr_popping === depth.U)
    val wr_count_next_is_full = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_full)

    val wr_limit_muxed = Wire(UInt((log2Ceil(depth)+1).W))    // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed
    val wr_busy_next = wr_count_next_is_full ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

    wr_busy_int := wr_busy_next
    when(wr_reserving ^ wr_popping){
        wr_count := wr_count_next
    }

    val wr_pushing = wr_reserving  // data pushed same cycle as wr_pvld

    //
    // RAM
    //  

    val wr_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))}   // current write address
    val rd_adr_p = Wire(UInt(log2Ceil(depth).W))       // read address to use for ram
    val rd_pd_p = Wire(UInt(width.W))       // read data directly out of ram

    val rd_enable = Wire(Bool())
    val ore = Wire(Bool())

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.

    val ram = Module(new nv_ram_rwsp(depth, width))
    ram.io.clk := io.clk
    ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    ram.io.wa := wr_adr
    ram.io.we := wr_pushing
    ram.io.di := io.wr_pd
    ram.io.ra := rd_adr_p   // for ram
    ram.io.re := rd_enable
    ram.io.ore := ore
    rd_pd_p := ram.io.dout
    
    // next wr_adr if wr_pushing=1
    val wr_adr_next = wr_adr + 1.U
    when(wr_pushing){
        wr_adr := wr_adr_next
    }

    val rd_popping = Wire(Bool())
    val rd_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))} 
    // next    read address
    val rd_adr_next = rd_adr + 1.U
    rd_adr_p := Mux(rd_popping, rd_adr_next, rd_adr)
    when(rd_popping){
        rd_adr := rd_adr_next
    }

    //
    // SYNCHRONOUS BOUNDARY
    //
    wr_popping := rd_popping    
    val rd_pushing = withClock(clk_mgated){RegNext(wr_pushing, false.B)} 

    //
    // READ SIDE
    //
    val rd_pvld_p = withClock(clk_mgated){RegInit(false.B)}  // data out of fifo is valid
    val rd_pvld_int = withClock(clk_mgated){RegInit(false.B)} // internal copy of rd_req
    io.rd_pvld := rd_pvld_int
    rd_popping := rd_pvld_p && !(rd_pvld_int && !io.rd_prdy)

    val rd_count_p = withClock(clk_mgated){RegInit("b0".asUInt((log2Ceil(depth)+1).W))} //read-side fifo count
    val rd_count_p_next_rd_popping = Mux(rd_pushing, rd_count_p, rd_count_p-1.U)
    val rd_count_p_next_no_rd_popping = Mux(rd_pushing, rd_count_p + 1.U, rd_count_p)
    val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)

    val rd_count_p_next_rd_popping_not_0 = rd_count_p_next_rd_popping =/= 0.U
    val rd_count_p_next_no_rd_popping_not_0 = rd_count_p_next_no_rd_popping =/= 0.U
    val rd_count_p_next_not_0 = Mux(rd_popping, rd_count_p_next_rd_popping_not_0, rd_count_p_next_no_rd_popping_not_0)

    rd_enable := ((rd_count_p_next_not_0) && ((~rd_pvld_p) || rd_popping));  // anytime data's there and not stalled

    
    when(rd_pushing || rd_popping){
        rd_count_p := rd_count_p_next
        rd_pvld_p := rd_count_p_next_not_0
    }
    
    val rd_req_next = (rd_pvld_p || (rd_pvld_int && !io.rd_prdy))

    rd_pvld_int := rd_req_next

    io.rd_pd := rd_pd_p
    ore := rd_popping

    clk_mgated_enable := ((wr_reserving || wr_pushing || rd_popping ||
                         wr_popping || (io.wr_pvld && !wr_busy_int) ||
                          (wr_busy_int =/= wr_busy_next)) || (rd_pushing ||
                           rd_popping || (rd_pvld_int && io.rd_prdy) || wr_pushing))

    wr_limit_muxed := "d0".asUInt((log2Ceil(depth)+1).W)

    
}}
