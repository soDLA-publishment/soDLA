package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_CSB_MASTER_falcon2csb_fifo(implicit val conf: csbMasterConfiguration)  extends Module {
    val io = IO(new Bundle {
        //general clock
        val wr_clk = Input(Clock())
        val rd_clk = Input(Clock())

        //control signal
        val wr_ready = Output(Bool())
        val wr_req = Input(Bool())   
        val rd_ready = Input(Bool())
        val rd_req = Output(Bool())   

        //data signal
        val wr_data = Input(UInt(50.W))
        val rd_data = Output(UInt(50.W))

        val pwrbus_ram_pd = Input(UInt(32.W))

    })
    
    //
    // DFT clock gate enable qualifier
    //

    // Write side
    val fifogenDFTWrQual = Module(new oneHotClk_async_write_clock)
    val dft_qualifier_wr_enable = fifogenDFTWrQual.io.enable_w

    val wr_clk_wr_dft_mgate = Module(new NV_CLK_gate_power)
    wr_clk_wr_dft_mgate.io.clk := io.wr_clk 
    wr_clk_wr_dft_mgate.io.clk_en := dft_qualifier_wr_enable
    val wr_clk_dft_mgated = wr_clk_wr_dft_mgate.io.clk_gated

    if(!conf.FPGA){
        // Add a dummy sink to prevent issue related to no fanout on this clock gate
        val UJ_BLKBOX_UNUSED_FIFOGEN_dft_wr_clkgate_sink = Module(new NV_BLKBOX_SINK)
        UJ_BLKBOX_UNUSED_FIFOGEN_dft_wr_clkgate_sink.io.A := wr_clk_dft_mgated.asUInt.toBool
    }

    // Read side
    val fifogenDFTRdQual = Module(new oneHotClk_async_read_clock)
    val dft_qualifier_rd_enable = fifogenDFTRdQual.io.enable_r

    val rd_clk_rd_dft_mgate = Module(new NV_CLK_gate_power)
    rd_clk_rd_dft_mgate.io.clk := io.rd_clk 
    rd_clk_rd_dft_mgate.io.clk_en := dft_qualifier_rd_enable
    val rd_clk_dft_mgated = rd_clk_rd_dft_mgate.io.clk_gated

    if(!conf.FPGA){
        // Add a dummy sink to prevent issue related to no fanout on this clock gate
        val UJ_BLKBOX_UNUSED_FIFOGEN_dft_rd_clkgate_sink = Module(new NV_BLKBOX_SINK)
        UJ_BLKBOX_UNUSED_FIFOGEN_dft_rd_clkgate_sink.io.A := rd_clk_dft_mgated.asUInt.toBool
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
    val wr_clk_wr_mgate = Module(new NV_CLK_gate_power)
    wr_clk_wr_mgate.io.clk := io.wr_clk
    wr_clk_wr_mgate.io.clk_en := wr_clk_wr_mgated_enable
    val wr_clk_wr_mgated = wr_clk_wr_mgate.io.clk_gated

    val rd_clk_rd_mgated_enable = Wire(Bool())// assigned by code at end of this module
    val rd_clk_rd_mgate = Module(new NV_CLK_gate_power)
    rd_clk_rd_mgate.io.clk := io.rd_clk
    rd_clk_rd_mgate.io.clk_en := rd_clk_rd_mgated_enable
    val rd_clk_rd_mgated = rd_clk_rd_mgate.io.clk_gated

    // 
    // WRITE SIDE
    //

    //dft gated
    val wr_reserving = Wire(Bool())
    val wr_req_in = withClock(wr_clk_dft_mgated){RegInit(false.B)}// registered wr_req
    val wr_busy_in = withClock(wr_clk_dft_mgated){RegInit(false.B)}// inputs being held this cycle?
    io.wr_ready := !wr_busy_in
    val wr_busy_next = Wire(Bool())

    // factor for better timing with distant wr_req signal 
    val wr_busy_in_next_wr_req_eq_1 = wr_busy_next
    val wr_busy_in_next_wr_req_eq_0 = (wr_req_in & wr_busy_next) & !wr_reserving
    val wr_busy_in_next = Mux(io.wr_req, wr_busy_in_next_wr_req_eq_1, wr_busy_in_next_wr_req_eq_0)

    val wr_busy_in_int = Wire(Bool())
    wr_busy_in := wr_busy_in_next
    when(!wr_busy_in_int){
        wr_req_in:= io.wr_req&& !wr_busy_in
    }

    //wr gated
    val wr_busy_int = withClock(wr_clk_wr_mgated){RegInit(false.B)}		        	// copy for internal use
    wr_reserving := wr_req_in && !wr_busy_int    // reserving write space?
    val wr_popping = Wire(Bool())	               // fwd: write side sees pop?
    val wr_count = withClock(wr_clk_wr_mgated){RegInit("d0".asUInt(3.W))}	// write-side 
    
    val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, (wr_count - "d1".U(1.W)))
    val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count + "d1".U(1.W), wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_4 = ( wr_count_next_no_wr_popping === "d4".U(3.W))
    val wr_count_next_is_4 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_4)

    val wr_limit_muxed = Wire(UInt(3.W))  // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed
    
    wr_busy_next := wr_count_next_is_4 | 
                    (wr_limit_reg =/= "d0".U(3.W) && 
                    wr_count_next >= wr_limit_reg) // busy next cycle? // check wr_limit if != 0  

    wr_busy_in_int := wr_req_in && wr_busy_int

    wr_busy_int := wr_busy_next
    when (wr_reserving ^ wr_popping) {
        wr_count := wr_count_next
    } 

    val  wr_pushing = wr_reserving  // data pushed same cycle as wr_req_in

    //RAM

    val wr_adr = withClock(wr_clk_wr_mgated){RegInit("d0".asUInt(2.W))}				// current write address


    // spyglass disable_block W484
    // next wr_adr if wr_pushing=1
    val wr_adr_next = wr_adr + 1.U
    when (wr_pushing) {
        wr_adr := wr_adr_next
    }

    val rd_adr = withClock(rd_clk_rd_mgated){RegInit("d0".asUInt(2.W))}	
    val ram_we = wr_pushing
    val ram_iwe = !wr_busy_in && io.wr_req

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.

    
    val ram = Module(new NV_NVDLA_CSB_MASTER_falcon2csb_fifo_flopram_rwa_4x50)
    ram.io.clk := wr_clk_dft_mgated
    ram.io.clk_mgated := wr_clk_wr_mgated
    ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    ram.io.di := io.wr_data
    ram.io.iwe := ram_iwe
    ram.io.we := ram_we
    ram.io.wa := wr_adr
    ram.io.ra := rd_adr
    val rd_data_p = ram.io.dout

    val rd_popping = Wire(Bool()) // read side doing pop this cycle?

    val rd_adr_next_popping = rd_adr + 1.U
    when(rd_popping){
        rd_adr := rd_adr_next_popping
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
    val wr_pushing_gray_cntr = Wire(UInt(3.W))

    // clk gating of strict synchronizers
    val wr_clk_wr_mgated_snd_gate = Module(new NV_CLK_gate_power)
    wr_clk_wr_mgated_snd_gate.io.clk := io.wr_clk
    wr_clk_wr_mgated_snd_gate.io.clk_en := dft_qualifier_wr_enable && (wr_pushing)
    val wr_clk_wr_mgated_strict_snd_gated = wr_clk_wr_mgated_snd_gate.io.clk_gated

    // wr_pushing -> rd_pushing translation
    val wr_pushing_gray = Module(new NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr_strict)
    if(conf.NV_FPGA_FIFOGEN){
        wr_pushing_gray.io.inc.get := wr_pushing
    }
    wr_pushing_gray.io.gray := wr_pushing_gray_cntr
    val wr_pushing_gray_cntr_next = wr_pushing_gray.io.gray_next

    val nv_AFIFO_wr_pushing_sync0 = Module(new p_STRICTSYNC3DOTM_C_PPP)
    nv_AFIFO_wr_pushing_sync0.io.SRC_CLK := wr_clk_wr_mgated_strict_snd_gated
    nv_AFIFO_wr_pushing_sync0.io.SRC_D_NEXT := wr_pushing_gray_cntr_next(0)
    nv_AFIFO_wr_pushing_sync0.io.DST_CLK := rd_clk_dft_mgated
    nv_AFIFO_wr_pushing_sync0.io.ATPG_CTL := false.B
    nv_AFIFO_wr_pushing_sync0.io.TEST_MODE := false.B
    val wr_pushing_gray_cntr_0 = nv_AFIFO_wr_pushing_sync0.io.SRC_D
    val wr_pushing_gray_cntr_sync_0 = nv_AFIFO_wr_pushing_sync0.io.DST_Q

    val nv_AFIFO_wr_pushing_sync1 = Module(new p_STRICTSYNC3DOTM_C_PPP)
    nv_AFIFO_wr_pushing_sync1.io.SRC_CLK := wr_clk_wr_mgated_strict_snd_gated
    nv_AFIFO_wr_pushing_sync1.io.SRC_D_NEXT := wr_pushing_gray_cntr_next(1)
    nv_AFIFO_wr_pushing_sync1.io.DST_CLK := rd_clk_dft_mgated
    nv_AFIFO_wr_pushing_sync1.io.ATPG_CTL := false.B
    nv_AFIFO_wr_pushing_sync1.io.TEST_MODE := false.B
    val wr_pushing_gray_cntr_1 = nv_AFIFO_wr_pushing_sync1.io.SRC_D
    val wr_pushing_gray_cntr_sync_1 = nv_AFIFO_wr_pushing_sync1.io.DST_Q

    val nv_AFIFO_wr_pushing_sync2 = Module(new p_STRICTSYNC3DOTM_C_PPP)
    nv_AFIFO_wr_pushing_sync2.io.SRC_CLK := wr_clk_wr_mgated_strict_snd_gated
    nv_AFIFO_wr_pushing_sync2.io.SRC_D_NEXT := wr_pushing_gray_cntr_next(2)
    nv_AFIFO_wr_pushing_sync2.io.DST_CLK := rd_clk_dft_mgated
    nv_AFIFO_wr_pushing_sync2.io.ATPG_CTL := false.B
    nv_AFIFO_wr_pushing_sync2.io.TEST_MODE := false.B
    val wr_pushing_gray_cntr_2 = nv_AFIFO_wr_pushing_sync2.io.SRC_D
    val wr_pushing_gray_cntr_sync_2 = nv_AFIFO_wr_pushing_sync2.io.DST_Q

    wr_pushing_gray_cntr := Cat(wr_pushing_gray_cntr_2, wr_pushing_gray_cntr_1, wr_pushing_gray_cntr_0)
    val wr_pushing_gray_cntr_sync = Cat(wr_pushing_gray_cntr_sync_2, wr_pushing_gray_cntr_sync_1, wr_pushing_gray_cntr_sync_0)

    val rd_pushing_gray_cntr = Wire(UInt(3.W))
    val rd_pushing = wr_pushing_gray_cntr_sync =/= rd_pushing_gray_cntr

    val rd_pushing_gray = Module(new NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr)
    rd_pushing_gray.io.clk := rd_clk_rd_mgated
    rd_pushing_gray.io.inc := rd_pushing
    rd_pushing_gray_cntr := rd_pushing_gray.io.gray

    // clk gating of strict synchronizers
    val rd_clk_rd_mgated_snd_gate = Module(new NV_CLK_gate_power)
    rd_clk_rd_mgated_snd_gate.io.clk := io.rd_clk
    rd_clk_rd_mgated_snd_gate.io.clk_en := dft_qualifier_rd_enable && (rd_popping)
    val rd_clk_rd_mgated_strict_snd_gated = rd_clk_rd_mgated_snd_gate.io.clk_gated

    val wr_clk_rcv_gate = Module(new NV_CLK_gate_power)
    wr_clk_rcv_gate.io.clk := io.wr_clk
    wr_clk_rcv_gate.io.clk_en := dft_qualifier_wr_enable && (wr_count_next_no_wr_popping =/= 0.U)
    val wr_clk_strict_rcv_gated = wr_clk_rcv_gate.io.clk_gated
    //
    // rd_popping -> wr_popping translation
    //
    val rd_popping_gray_cntr = Wire(UInt(3.W))
    val rd_popping_gray = Module(new NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr_strict)
    if(conf.NV_FPGA_FIFOGEN){
        rd_popping_gray.io.inc.get := rd_popping
    }
    rd_popping_gray.io.gray := rd_popping_gray_cntr
    val rd_popping_gray_cntr_next = rd_popping_gray.io.gray_next

    val nv_AFIFO_rd_popping_sync0 = Module(new p_STRICTSYNC3DOTM_C_PPP)
    nv_AFIFO_rd_popping_sync0.io.SRC_CLK := rd_clk_rd_mgated_strict_snd_gated
    nv_AFIFO_rd_popping_sync0.io.SRC_D_NEXT := rd_popping_gray_cntr_next(0)
    nv_AFIFO_rd_popping_sync0.io.DST_CLK := wr_clk_strict_rcv_gated
    nv_AFIFO_rd_popping_sync0.io.ATPG_CTL := false.B
    nv_AFIFO_rd_popping_sync0.io.TEST_MODE := false.B
    val rd_popping_gray_cntr_0 = nv_AFIFO_rd_popping_sync0.io.SRC_D
    val rd_popping_gray_cntr_sync_0 = nv_AFIFO_rd_popping_sync0.io.DST_Q

    val nv_AFIFO_rd_popping_sync1 = Module(new p_STRICTSYNC3DOTM_C_PPP)
    nv_AFIFO_rd_popping_sync1.io.SRC_CLK := rd_clk_rd_mgated_strict_snd_gated
    nv_AFIFO_rd_popping_sync1.io.SRC_D_NEXT := rd_popping_gray_cntr_next(1)
    nv_AFIFO_rd_popping_sync1.io.DST_CLK := wr_clk_strict_rcv_gated
    nv_AFIFO_rd_popping_sync1.io.ATPG_CTL := false.B
    nv_AFIFO_rd_popping_sync1.io.TEST_MODE := false.B
    val rd_popping_gray_cntr_1 = nv_AFIFO_rd_popping_sync1.io.SRC_D
    val rd_popping_gray_cntr_sync_1 = nv_AFIFO_rd_popping_sync1.io.DST_Q

    val nv_AFIFO_rd_popping_sync2 = Module(new p_STRICTSYNC3DOTM_C_PPP)
    nv_AFIFO_rd_popping_sync2.io.SRC_CLK := rd_clk_rd_mgated_strict_snd_gated
    nv_AFIFO_rd_popping_sync2.io.SRC_D_NEXT := rd_popping_gray_cntr_next(1)
    nv_AFIFO_rd_popping_sync2.io.DST_CLK := wr_clk_strict_rcv_gated
    nv_AFIFO_rd_popping_sync2.io.ATPG_CTL := false.B
    nv_AFIFO_rd_popping_sync2.io.TEST_MODE := false.B
    val rd_popping_gray_cntr_2 = nv_AFIFO_rd_popping_sync2.io.SRC_D
    val rd_popping_gray_cntr_sync_2 = nv_AFIFO_rd_popping_sync2.io.DST_Q

    rd_popping_gray_cntr := Cat(rd_popping_gray_cntr_2, rd_popping_gray_cntr_1, rd_popping_gray_cntr_0)
    val rd_popping_gray_cntr_sync = Cat(rd_popping_gray_cntr_sync_2, rd_popping_gray_cntr_sync_1, rd_popping_gray_cntr_sync_0)

    val wr_popping_gray_cntr = Wire(UInt(3.W))
    wr_popping := rd_popping_gray_cntr_sync =/= wr_popping_gray_cntr

    val wr_popping_gray = Module(new NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr)
    wr_popping_gray.io.clk := wr_clk_wr_mgated
    wr_popping_gray.io.inc := wr_popping
    wr_popping_gray_cntr := wr_popping_gray.io.gray

    //
    // READ SIDE
    //
    val rd_req_p = Wire(Bool()) // data out of fifo is valid
    val rd_req_int = withClock(rd_clk_rd_mgated){RegInit(false.B)}  // internal copy of rd_req
    io.rd_req := rd_req_int
    rd_popping := rd_req_p && !(rd_req_int && !io.rd_ready)
    val rd_count_p = withClock(rd_clk_rd_mgated){RegInit("b0".asUInt(3.W))}
    val rd_count_p_next_rd_popping = Mux(rd_pushing, rd_count_p, rd_count_p-1.U)
    val rd_count_p_next_no_rd_popping = Mux(rd_pushing, rd_count_p+1.U, rd_count_p)

    val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)
    rd_req_p := rd_count_p =/= 0.U || rd_pushing

    when( rd_pushing || rd_popping ){
        rd_count_p := rd_count_p_next
    }

    val nv_AFIFO_rd_data = withClock(rd_clk_rd_mgated){Reg(UInt(50.W))}
    val rd_req_next = (rd_req_p || (rd_req_int && !io.rd_ready))

    rd_req_int := rd_req_next
    when(rd_popping){
        nv_AFIFO_rd_data := rd_data_p
    }

    io.rd_data := nv_AFIFO_rd_data

    // Master Clock Gating (SLCG) Enables
    wr_clk_wr_mgated_enable := dft_qualifier_wr_enable && (wr_reserving || wr_pushing || wr_popping || wr_popping || (wr_req_in && !wr_busy_int) || (wr_busy_int != wr_busy_next))
    rd_clk_rd_mgated_enable := dft_qualifier_rd_enable && ((rd_pushing ||rd_popping || (rd_req_int && io.rd_ready)))
    
    wr_limit_muxed := "b0".asUInt(3.W)
}

class NV_NVDLA_CSB_MASTER_falcon2csb_fifo_flopram_rwa_4x50 extends Module{
  val io = IO(new Bundle{
        val clk = Input(Clock())
        val clk_mgated = Input(Clock())

        val di = Input(UInt(50.W))
        val iwe = Input(Bool())
        val we = Input(Bool())
        val wa = Input(UInt(2.W))
        val ra = Input(UInt(2.W))
        val dout = Output(UInt(50.W))

        val pwrbus_ram_pd = Input(UInt(32.W))

  })  
withClock(io.clk){
    val di_d = Reg(UInt(50.W))
    when(io.iwe){
        di_d := io.di
    }
    val ram_ff = withClock(io.clk_mgated){Reg(Vec(4, UInt(50.W)))} 
    when(io.we){
        for(i <- 0 to 3){
            when(io.wa === i.U){
                ram_ff(i) := di_d
            }
        }
    }    
    io.dout := MuxLookup(io.ra, "b0".asUInt(50.W), 
        (0 to 3) map { i => i.U -> ram_ff(i)})
}}



//
// See the ASYNCHONROUS BOUNDARY section above for details on the
// gray counter implementation.
//

class NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr_strict(implicit val conf: csbMasterConfiguration) extends Module {
  val io = IO(new Bundle{
      val inc = if(conf.NV_FPGA_FIFOGEN) Some(Input(Bool())) else None
      val gray = Input(UInt(3.W))
      val gray_next = Output(UInt(3.W))
  })
   val polarity = io.gray(0) ^ io.gray(1)  ^ io.gray(2)   // polarity of gray counter bits
      
   if(conf.NV_FPGA_FIFOGEN){
       io.gray_next := Mux(~io.inc.get, io.gray, Cat(io.gray(2)^(polarity&(!io.gray(0))), io.gray(1)^(polarity&io.gray(0)), io.gray(0)^(!polarity)))
   }
   else {
       io.gray_next := Cat(io.gray(2)^(polarity&(!io.gray(0))), io.gray(1)^(polarity&io.gray(0)), io.gray(0)^(!polarity))
   }



}



class NV_NVDLA_CSB_MASTER_falcon2csb_fifo_gray_cntr extends Module {
  val io = IO(new Bundle{
      val clk = Input(Clock())
      val inc = Input(Bool())
      val gray = Output(UInt(3.W))
  })
  withClock(io.clk){
      val gray_out = RegInit("b0".asUInt(3.W))  // gray counter
      val polarity = gray_out(0) ^ gray_out(1)^ gray_out(2)   // polarity of gray counter bits
      when(io.inc){
          gray_out := Cat(io.gray(2)^(polarity&(!io.gray(0))), io.gray(1)^(polarity&io.gray(0)), io.gray(0)^(!polarity))
      }
      io.gray := gray_out
  }
}


object NV_NVDLA_CSB_MASTER_falcon2csb_fifoDriver extends App {
  implicit val conf: csbMasterConfiguration = new csbMasterConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_CSB_MASTER_falcon2csb_fifo())
}