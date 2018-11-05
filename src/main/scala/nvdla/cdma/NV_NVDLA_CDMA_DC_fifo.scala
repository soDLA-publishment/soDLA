package nvdla

import chisel3._

class NV_NVDLA_CDMA_DC_fifo extends Module {
    val io = IO(new Bundle {
        // spyglass disable_block W401 -- clock is not input to module
        //general clock
        val clk = Input(Clock())
        val reset_ = Input(Bool())

        //control signal
        val wr_ready = Output(Bool())
        val wr_req = Input(Bool())   
        val rd_ready = Input(Bool())
        val rd_req = Output(Bool())   

        //data signal

        val wr_data = Input(UInt(6.W))
        val rd_data = Output(UInt(6.W))
        val pwrbus_ram_pd = Input(UInt(32.W))

    })
    
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
    val clk_mgated = Wire(Clock())

    val clk_mgate = Module(new NV_CLK_gate_power)
    io.clk := clk_mgate.io.clk 
    io.reset_ := clk_mgate.io.reset_ 
    clk_mgated_enable := clk_mgate.io.clk_en 
    clk_mgated := clk_mgate.io.clk_gated

    // 
    // WRITE SIDE
    //  
    val wr_reserving = Wire(Bool())
    val wr_req_in = Reg(Bool())
    val wr_data_in = Reg(UInt(6.W))
    val wr_busy_in = Reg(Bool())
    io.wr_ready := !wr_busy_in
    val wr_busy_next = Wire(Bool())

    // factor for better timing with distant wr_req signal
    val wr_busy_in_next_wr_req_eq_1 = wr_busy_next
    val wr_busy_in_next_wr_req_eq_0 = (wr_req_in && wr_busy_next) && !wr_reserving
    val wr_busy_in_next = Mux(io.wr_req, wr_busy_in_next_wr_req_eq_1, wr_busy_in_next_wr_req_eq_0)

    val wr_busy_in_int = Wire(Bool())

    withClockAndReset(io.clk, !io.reset_) {
        wr_busy_in := wr_busy_in_next
        when (!wr_busy_in_int) {
            wr_req_in := wr_req && !wr_busy_in
        }

    } 

    withClock(io.clk){
         when (!wr_busy_in&&wr_req) {
            wr_data_in := wr_data
        }       
    }

    val wr_busy_int = Reg(Bool())		        	// copy for internal use
    wr_reserving := wr_req_in & !wr_busy_int    // reserving write space?


    val wr_popping = Reg(Bool())	               // fwd: write side sees pop?
    val wr_count = Reg(UInt(8.W))			// write-side 
    
    val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, (wr_count - "d1".U(1.W)))
    val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count + "d1".U(1.W), wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_128 = ( wr_count_next_no_wr_popping === "d128".U(8.W))
    val wr_count_next_is_128 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_128)

    val wr_limit_muxed = Wire(UInt(8.W))  // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed

    val wr_busy_next := wr_count_next_is_128|(wr_limit_reg != "d0".U(8.W) &&wr_count_next >= wr_limit_reg)// busy next cycle? // check wr_limit if != 0

    wr_busy_in_int := wr_req_in && wr_busy_int

    withClockAndReset(clk_mgated, !io.reset_) {
        wr_busy_in := wr_busy_in_next
        when (wr_reserving ^ wr_popping) {
            wr_count := wr_count_next
        }
    } 

    val  wr_pushing = wr_reserving

    //RAM

    val wr_adr = Reg(UInt(7.W)) 			// current write address
    val rd_adr_p = Wire(UInt(7.W))  // read address to use for ram
    val rd_data_p = Wire(UInt(6.W)) // read data directly out of ram

    val rd_enable = Wire(Bool())

    val ore = Wire(Bool())
    io.pwrbus_ram_pd := Wire(UInt(32.W))

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.

    val ram = Module(new nv_ram_rwsp(128, 6))
    io.clk:= ram.io.clk
    io.pwrbus_ram_pd := ram.io.pwrbus_ram_pd
    wr_adr := ram.io.wa
    wr_pushing := ram.io.we
    wr_data_in := ram.io.di
    rd_adr_p := ram.io.ra
    rd_enable := ram.io.re
    rd_data_p := ram.io.dout
    ore := ram.io.ore

    // next wr_adr if wr_pushing=1
    val wr_adr_next = wr_adr + "d1".UInt(1.W)

    // spyglass disable_block W484

    withClockAndReset(clk_mgated, !io.reset_) {
        when (wr_pushing) {
            wr_adr := wr_adr_next
        }
    }  

    // spyglass enable_block W484

    val rd_popping = Wire(Bool())// read side doing pop this cycle?
    val rd_adr = Reg(UInt(7.W))// current read address

    // next    read address 

    val rd_adr_next = rd_adr + "d1".UInt(1.W)
    rd_adr_p := Mux(rd_popping, rd_adr_next, rd_adr) //for ram

    // spyglass disable_block W484

    withClockAndReset(clk_mgated, !io.reset_) {
        when (rd_popping) {
            rd_adr := rd_adr_next
        }
    }  

    // spyglass enable_block W484

    //
    // SYNCHRONOUS BOUNDARY
    //

    withClockAndReset(clk_mgated, !io.reset_) {
        wr_popping := rd_popping
    } 

    val rd_pushing = Reg(Bool())

    withClockAndReset(clk_mgated, !io.reset_) {
        rd_pushing := wr_pushing// let data go into ram first
    } 

    //
    // READ SIDE
    //











    

    

  
}