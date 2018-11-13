package nvdla

import chisel3._

class NV_NVDLA_CSB_MASTER_csb2falcon_fifo(implicit val conf: csbMasterConfiguration)  extends Module {
    val io = IO(new Bundle {
        // spyglass disable_block W401 -- clock is not input to module
        //general clock
        val wr_clk = Input(Clock())
        val wr_reset_ = Input(Bool())

        val rd_clk = Input(Clock())
        val rd_reset_ = Input(Bool()) 

        //control signal
        val wr_ready = Output(Bool())
        val wr_req = Input(Bool())   
        val rd_ready = Input(Bool())
        val rd_req = Output(Bool())   

        //data signal

        val wr_data = Input(UInt(34.W))
        val rd_data = Output(UInt(34.W))
        val pwrbus_ram_pd = Input(UInt(32.W))

    })
    
    //
    // DFT clock gate enable qualifier
    //

    // Write side

    val dft_qualifier_wr_enable = Wire(Bool())

    val fifogenDFTWrQual = Module(new oneHotClk_async_write_clock)
    dft_qualifier_wr_enable := fifogenDFTWrQual.io.enable_w

    val wr_clk_dft_mgated = Wire(Bool())

    val wr_clk_wr_dft_mgate = Module(new NV_CLK_gate_power)

    io.wr_clk := wr_clk_wr_dft_mgate.io.clk
    io.wr_reset_ := wr_clk_wr_dft_mgate.io.reset_
    dft_qualifier_wr_enable := wr_clk_wr_dft_mgate.io.clk_en
    wr_clk_dft_mgated := wr_clk_wr_dft_mgate.io.clk_gated

    if(!conf.FPGA){
        // Add a dummy sink to prevent issue related to no fanout on this clock gate
        val UJ_BLKBOX_UNUSED_FIFOGEN_dft_wr_clkgate_sink = Module(new NV_BLKBOX_SINK)
        wr_clk_dft_mgated := UJ_BLKBOX_UNUSED_FIFOGEN_dft_wr_clkgate_sink.io.A
    }

    // Read side

    val dft_qualifier_rd_enable = Wire(Bool())

    val fifogenDFTRdQuall = Module(new oneHotClk_async_read_clock)
    dft_qualifier_rd_enable := fifogenDFTWrQual.io.enable_r

    val rd_clk_dft_mgated = Wire(Bool())

    val rd_clk_rd_dft_mgate = Module(new NV_CLK_gate_power)

    io.rd_clk := rd_clk_rd_dft_mgate.io.clk
    io.rd_reset_ := rd_clk_rd_dft_mgate.io.reset_
    dft_qualifier_rd_enable := rd_clk_rd_dft_mgate.io.clk_en
    rd_clk_dft_mgated := rd_clk_rd_dft_mgate.io.clk_gated

    if(!conf.FPGA){
        // Add a dummy sink to prevent issue related to no fanout on this clock gate
        val UJ_BLKBOX_UNUSED_FIFOGEN_dft_rd_clkgate_sink = Module(new NV_BLKBOX_SINK)
        rd_clk_dft_mgated := UJ_BLKBOX_UNUSED_FIFOGEN_dft_rd_clkgate_sink.io.A
    }

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
    val wr_clk_wr_mgated_enable = Wire(Bool())// assigned by code at end of this module
    val wr_clk_wr_mgated = Wire(Bool())

    val wr_clk_wr_mgate = Module(new NV_CLK_gate_power)

    io.wr_clk := wr_clk_wr_mgate.io.clk
    io.wr_reset_ := wr_clk_wr_mgate.io.reset_
    wr_clk_wr_mgated_enable := wr_clk_wr_mgate.io.clk_en
    wr_clk_wr_mgated := wr_clk_wr_mgate.io.clk_gated

    val rd_clk_rd_mgated_enable = Wire(Bool())// assigned by code at end of this module
    val rd_clk_rd_mgated = Wire(Bool())

    val rd_clk_rd_mgate = Module(new NV_CLK_gate_power)

    io.rd_clk := rd_clk_rd_mgate.io.clk
    io.rd_reset_ := rd_clk_rd_mgate.io.reset_
    rd_clk_rd_mgated_enable := rd_clk_rd_mgate.io.clk_en
    rd_clk_rd_mgated := rd_clk_rd_mgate.io.clk_gated

    // 
    // WRITE SIDE
    //

    val wr_reserving = Wire(Bool())
    val wr_req_in = Reg(Bool())// registered wr_req
    val wr_busy_in = Reg(Bool())// inputs being held this cycle?
    io.we_ready := !wr_busy_in
    val wr_busy_next = Wire(Bool())

    // factor for better timing with distant wr_req signal 
    val wr_busy_in_next_wr_req_eq_1 = wr_busy_next
    val wr_busy_in_next_wr_req_eq_0 = (wr_req_in & wr_busy_next) & !wr_reserving
    val wr_busy_in_next = Mux(io.wr_req,  wr_busy_in_next_wr_req_eq_1, wr_busy_in_next_wr_req_eq_0)

    val wr_busy_in_int = Wire(Bool())

    withClockAndReset(wr_clk_dft_mgate, wr_reset_){
        wr_busy_in := wr_busy_in_next
        when(!wr_busy_in_int){
            wr_req_in:= io.wr_req&& !wr_busy_in
        }
    }

    val wr_busy_int = Reg(Bool())		        	// copy for internal use
    wr_reserving := wr_req_in && !wr_busy_int    // reserving write space?


    val wr_popping = Wire(Bool())	               // fwd: write side sees pop?
    val wr_count = Reg(UInt(2.W))			// write-side 
    
    val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, (wr_count - "d1".U(1.W)))
    val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count + "d1".U(1.W), wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_2 = ( wr_count_next_no_wr_popping === "d2".U(2.W))
    val wr_count_next_is_2 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_2)

    val wr_limit_muxed = Wire(UInt(2.W))  // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed
    
    wr_busy_next := wr_count_next_is_2 | (wr_limit_reg != "d0".U(2.W) && wr_count_next >= wr_limit_reg) // busy next cycle? // check wr_limit if != 0  

    val wr_busy_in_int = wr_req_in && wr_busy_int

    withClockAndReset(wr_clk_wr_mgated, io.wr_reset_) {
        wr_busy_int := wr_busy_next
        when (wr_reserving ^ wr_popping) {
            wr_count := wr_count_next
        }
    } 

    val  wr_pushing = wr_reserving

    //RAM

    val wr_adr = Reg(UInt(1.W)) 			// current write address


    // spyglass disable_block W484
    // next wr_adr if wr_pushing=1

    val wr_adr_next = wr_adr + "d1".U(1.W)

    withClockAndReset(rd_clk_rd_mgated, !io.rd_reset_ ){
        when (wr_pushing) {
            wr_adr := wr_adr_next
        }
    } 

    val rd_adr = Reg(Bool())
    val ram_we = wr_pushing
    val ram_iwe = !wr_busy_in && io.wr_req
    val rd_data_p = Wire(UInt(34.W))

    val io.pwrbus_ram_pd = Wire(UInt(32.W))

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.

    
    val ram = Module(new NV_NVDLA_CSB_MASTER_csb2falcon_fifo_flopram_rwa_2x34)
    wr_clk_dft_mgated:= ram.io.clk
    wr_clk_wr_mgated := ram.io.clk_mgated
    io.pwrbus_ram_pd  := ram.io.pwrbus_ram_pd
    io.wr_data := ram.io.di
    ram_iwe:= ram.io.iwe
    ram_we:= ram.io.we
    wr_adr := ram.io.wa
    rd_adr:= ram.io.ra
    rd_data_p := ram.io.dout 

    val rd_popping = Wire(Bool()) // read side doing pop this cycle?

    val rd_adr_next_popping = rd_adr + "d1".UInt(1.W)// spyglass disable W484
    // spyglass disable_block W484


    withClockAndReset(rd_clk_rd_mgated, !io.rd_reset_) {
        when (rd_popping) {
            rd_adr := rd_adr_next_popping
        }
    } 

    //
// ASYNCHRONOUS BOUNDARY USING TRADITIONAL SYNCHRONIZERS
//
// Our goal here is to translate wr_pushing pulses into rd_pushing
// pulses on the read side and, conversely, to translate rd_popping
// pulses to wr_popping pulses on the write side.
//
// We don't try to optimize the case where the async fifo depth is
// a power of two.  We handle the general case using one scheme to
// avoid maintaining different implementations.  We may use a couple
// more counters, but they are quite cheap in the grand scheme of things.
// This wr_pushing/rd_pushing/rd_popping/wr_popping centric scheme also
// fits in well with the case where there is no asynchronous boundary.
//
// The scheme works as follows.  For the wr_pushing -> rd_pushing translation,
// we keep an 2-bit gray counter on the write and read sides.
// This counter is initialized to 0 on both sides.   When wr_pushing
// is pulsed, the write side gray-increments its counter, registers it,
// then sends it through an 2-bit synchronizer to the other side.
// Whenever the read side sees the new gray counter not equal to its
// copy of the gray counter, it gray-increments its counter and pulses
// rd_pushing=1.  The actual value of the gray counter is irrelevant.
// It must be a power-of-2 to make the gray code work.  Otherwise,
// we're just looking for changes in the gray value.
//
// The same technique is used for the rd_popping -> wr_popping translation.
//
// The gray counter algorithm uses a 1-bit polarity register that starts
// off as 0 and is inverted whenever the gray counter is incremented.
//
// In plain English, the next gray counter is determined as follows:
// if the current polarity register is 0, invert bit 0 (the lsb); otherwise,
// find the rightmost one bit and invert the bit to the left of the one bit
// if the one bit is not the msb else invert the msb one bit.  The
// general expression is thus:
//
// { gray[n-1] ^ (polarity             & ~gray[n-3] & ~gray[n-4] & ... ),
//   gray[n-2] ^ (polarity & gray[n-3] & ~gray[n-4] & ~gray[n-5] & ... ),
//   gray[n-3] ^ (polarity & gray[n-4] & ~gray[n-5] & ~gray[n-6] & ... ),
//   ...
//   gray[0]   ^ (~polarity) }
//
// For n == 1, the next gray value is obviously just ~gray.
//
// The wr_pushing/rd_popping signal does not affect the registered
// gray counter until the next cycle.  However, for non-FF-type rams,
// the write will not complete until the end of the next cycle, so
// we must delay wr_pushing yet another more cycle,
// unless the -rd_clk_le_2x_wr_clk option was given
// (or the -rd_clk_le_2x_wr_clk_dynamic option was given
// and the rd_clk_le_2x_wr_clk signal is 1).
//


// clk gating of strict synchronizers
//
    



    

    

  
}