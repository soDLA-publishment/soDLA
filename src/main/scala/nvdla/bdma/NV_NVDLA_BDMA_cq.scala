package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_BDMA_cq extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val ld2st_wr_prdy = Output(Bool())
        val ld2st_wr_idle = Output(Bool())
        val ld2st_wr_pvld = Input(Bool())
        val ld2st_wr_pd = Input(UInt(161.W))
        val ld2st_rd_prdy = Input(Bool())
        val ld2st_rd_pvld = Output(Bool())
        val ld2st_rd_pd = Output(UInt(161.W))

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
    withClock(io.nvdla_core_clk){
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
    val nvdla_core_clk_mgated_enable = Wire(Bool())
    val nvdla_core_clk_mgate = Module(new NV_CLK_gate_power)
    nvdla_core_clk_mgate.io.clk := io.nvdla_core_clk
    nvdla_core_clk_mgate.io.clk_en := nvdla_core_clk_mgated_enable
    val nvdla_core_clk_mgated = nvdla_core_clk_mgate.io.clk_gated

    ////////////////////////////////////////////////////////////////////////
    // WRITE SIDE                                                        //
    ////////////////////////////////////////////////////////////////////////
    val wr_reserving = Wire(Bool())
    val ld2st_wr_pvld_in = RegInit(false.B)    // registered wr_req
    val wr_busy_in = RegInit(false.B)   // inputs being held this cycle?
    io.ld2st_wr_prdy := !wr_busy_in
    val ld2st_wr_busy_next = Wire(Bool())     // fwd: fifo busy next?

    // factor for better timing with distant wr_req signal
    val wr_busy_in_next_wr_req_eq_1 = ld2st_wr_busy_next
    val wr_busy_in_next_wr_req_eq_0 = (ld2st_wr_pvld_in && ld2st_wr_busy_next) && !wr_reserving
    val wr_busy_in_next = Mux(io.ld2st_wr_pvld, wr_busy_in_next_wr_req_eq_1, wr_busy_in_next_wr_req_eq_0)
    val wr_busy_in_int = Wire(Bool())

    wr_busy_in := wr_busy_in_next
    when(!wr_busy_in_int){
        ld2st_wr_pvld_in := io.ld2st_wr_pvld && !wr_busy_in
    }

    val ld2st_wr_busy_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // copy for internal use
    wr_reserving := ld2st_wr_pvld_in && !ld2st_wr_busy_int   // reserving write space?

    val wr_popping = Wire(Bool())       // fwd: write side sees pop?
    val ld2st_wr_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(5.W))} // write-side count
    val wr_count_next_wr_popping = Mux(wr_reserving, ld2st_wr_count, ld2st_wr_count-1.U)
    val wr_count_next_no_wr_popping = Mux(wr_reserving, ld2st_wr_count+1.U, ld2st_wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_20 = (wr_count_next_no_wr_popping === 20.U)
    val wr_count_next_is_20 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_20)
    val wr_limit_muxed = Wire(UInt(5.W))    // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed
    ld2st_wr_busy_next := wr_count_next_is_20 ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))
    wr_busy_in_int := ld2st_wr_pvld_in && ld2st_wr_busy_int

    ld2st_wr_busy_int := ld2st_wr_busy_next
    when(wr_reserving ^ wr_popping){
        ld2st_wr_count := wr_count_next
    }


    val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

    //
    // RAM
    //  

    val ld2st_wr_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(5.W))}
    val wr_adr_next = Mux(ld2st_wr_adr === 19.U, "b0".asUInt(5.W), ld2st_wr_adr + 1.U)
    when(wr_pushing){
        ld2st_wr_adr := wr_adr_next
    }
    val rd_popping = Wire(Bool())

    val ld2st_rd_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(5.W))}   // read address this cycle
    val ram_we = wr_pushing && (ld2st_wr_count > 0.U || !rd_popping)      // note: write occurs next cycle
    val ram_iwe = !wr_busy_in && io.ld2st_wr_pvld

    val ld2st_rd_pd_p = Wire(UInt(161.W))   // read data out of ram
    

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.
    val ram = Module(new nv_flopram_internal_wr_reg(20, 161))
    ram.io.clk := io.nvdla_core_clk
    ram.io.clk_mgated := nvdla_core_clk_mgated
    ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    ram.io.di := io.ld2st_wr_pd 
    ram.io.iwe := ram_iwe
    ram.io.we := ram_we
    ram.io.wa := ld2st_wr_adr
    ram.io.ra := Mux(ld2st_wr_count === 0.U, 20.U, ld2st_rd_adr)
    ld2st_rd_pd_p := ram.io.dout
    

    val rd_adr_next_popping = Mux(ld2st_rd_adr === 19.U, "b0".asUInt(5.W), ld2st_rd_adr + 1.U)
    when(rd_popping){
        ld2st_rd_adr := rd_adr_next_popping
    }

    //
    // SYNCHRONOUS BOUNDARY
    //
    wr_popping := rd_popping    // let it be seen immediately
    val rd_pushing = wr_pushing // let it be seen immediately

    //
    // READ SIDE
    //
    val ld2st_rd_pvld_p = Wire(Bool())  // data out of fifo is valid
    val ld2st_rd_pvld_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}
    io.ld2st_rd_pvld := ld2st_rd_pvld_int 
    rd_popping := ld2st_rd_pvld_p && !(ld2st_rd_pvld_int && !io.ld2st_rd_prdy)
    val ld2st_rd_count_p = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(5.W))}  // read-side fifo count
    val rd_count_p_next_rd_popping = Mux(rd_pushing, ld2st_rd_count_p, ld2st_rd_count_p-1.U)
    val rd_count_p_next_no_rd_popping = Mux(rd_pushing, ld2st_rd_count_p + 1.U, ld2st_rd_count_p)
    val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)
    ld2st_rd_pvld_p := ld2st_rd_count_p =/= 0.U || rd_pushing

    when(rd_pushing || rd_popping){
        ld2st_rd_count_p := rd_count_p_next
    }

    val ld2st_rd_pd_out = withClock(nvdla_core_clk_mgated){Reg(UInt(161.W))}  // output data register
    val rd_req_next = (ld2st_rd_pvld_p || (ld2st_rd_pvld_int && !io.ld2st_rd_prdy))

    ld2st_rd_pvld_int := rd_req_next
    when(rd_popping){
        ld2st_rd_pd_out := ld2st_rd_pd_p
    }

    io.ld2st_rd_pd := ld2st_rd_pd_out
    //
    // Read-side Idle Calculation
    //

    val rd_idle = !ld2st_rd_pvld_int && !rd_pushing && ld2st_rd_count_p === 0.U

    //
    // Write-Side Idle Calculation
    //

    io.ld2st_wr_idle := !ld2st_wr_pvld_in && rd_idle && !wr_pushing && ld2st_wr_count === 0.U


    nvdla_core_clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
                         (ld2st_wr_pvld_in && !ld2st_wr_busy_int) || (ld2st_wr_busy_int =/= ld2st_wr_busy_next)) || 
                         (rd_pushing || rd_popping || (ld2st_rd_pvld_int && io.ld2st_rd_prdy)) || (wr_pushing))

    wr_limit_muxed := "d0".asUInt(5.W)

    
}}


    
object NV_NVDLA_BDMA_cqDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_BDMA_cq)
}