package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_CDMA_IMG_sg2pack_fifo(depth:Int = 128, width:Int = 11) extends Module {
    val io = IO(new Bundle {
        //clk
        val clk = Input(Clock())

        val wr_ready = Output(Bool())
        val wr_req = Input(Bool())
        val wr_data = Input(UInt(width.W))
        val rd_ready = Input(Bool())
        val rd_req = Output(Bool())
        val rd_data = Output(UInt(width.W))

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
    val wr_req_in = RegInit(false.B)    // registered wr_req
    val wr_busy_in = RegInit(false.B)   // inputs being held this cycle?
    io.wr_ready := !wr_busy_in
    val wr_busy_next = Wire(Bool())     // fwd: fifo busy next?

    // factor for better timing with distant wr_req signal
    val wr_busy_in_next_wr_req_eq_1 = wr_busy_next
    val wr_busy_in_next_wr_req_eq_0 = (wr_req_in && wr_busy_next) && !wr_reserving
    val wr_busy_in_next = Mux(io.wr_req, wr_busy_in_next_wr_req_eq_1, wr_busy_in_next_wr_req_eq_0)
    val wr_busy_in_int = Wire(Bool())

    wr_busy_in := wr_busy_in_next
    when(!wr_busy_in_int){
        wr_req_in := io.wr_req && !wr_busy_in
    }

    val wr_busy_int = withClock(clk_mgated){RegInit(false.B)}  // copy for internal use
    wr_reserving := wr_req_in && !wr_busy_int   // reserving write space?

    val wr_popping = Wire(Bool())// fwd: write side sees pop?
    val wr_count = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))} // write-side count
    val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, wr_count-1.U)
    val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count+1.U, wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_full = (wr_count_next_no_wr_popping === depth.U)
    val wr_count_next_is_full = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_full)
    val wr_limit_muxed = Wire(UInt(log2Ceil(depth+1).W))    // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed
    wr_busy_next := wr_count_next_is_full ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))
    wr_busy_in_int := wr_req_in && wr_busy_int

    wr_busy_int := wr_busy_next
    when(wr_reserving ^ wr_popping){
        wr_count := wr_count_next
    }

    val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

    //
    // RAM
    //  

    val wr_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))}
    val wr_adr_next = wr_adr + 1.U
    when(wr_pushing){
        wr_adr := wr_adr_next
    }
    val rd_popping = Wire(Bool())

    val rd_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))}   // read address this cycle
    val ram_we = wr_pushing && (wr_count > 0.U || !rd_popping)      // note: write occurs next cycle
    val ram_iwe = !wr_busy_in && io.wr_req
    val rd_data_p = Wire(UInt(width.W))// read data out of ram
    

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.
    val ram = Module(new nv_flopram_internal_wr_reg(depth, width))
    ram.io.clk := io.clk
    ram.io.clk_mgated := clk_mgated
    ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    ram.io.di := io.wr_data
    ram.io.iwe := ram_iwe
    ram.io.we := ram_we
    ram.io.wa := wr_adr
    ram.io.ra := Mux(wr_count === 0.U, depth.U, rd_adr)
    rd_data_p := ram.io.dout
    

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
    val rd_req_p = Wire(Bool())  // data out of fifo is valid
    val rd_req_int = withClock(clk_mgated){RegInit(false.B)} // internal copy of rd_req
    io.rd_req := rd_req_int
    rd_popping := rd_req_p && !(rd_req_int && !io.rd_ready)

    val rd_count_p = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))} //read-side fifo count
    val rd_count_p_next_rd_popping = Mux(rd_pushing, rd_count_p, rd_count_p - 1.U)
    val rd_count_p_next_no_rd_popping = Mux(rd_pushing, rd_count_p + 1.U, rd_count_p)
    val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)
    rd_req_p := rd_count_p =/= 0.U || rd_pushing;
    when(rd_pushing || rd_popping){
        rd_count_p := rd_count_p_next
    }

    val rd_data_out = withClock(clk_mgated){RegInit("b0".asUInt(width.W))} // output data register
    val rd_req_next = (rd_req_p || (rd_req_int && !io.rd_ready))

    rd_req_int := rd_req_next
    when(rd_popping){
        rd_data_out := rd_data_p
    }

    io.rd_data := rd_data_out

    clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
                         (wr_req_in && !wr_busy_int) || (wr_busy_int =/= wr_busy_next)) || 
                         (rd_pushing || rd_popping || (rd_req_int && io.rd_ready)) || (wr_pushing))

    wr_limit_muxed := "d0".asUInt(log2Ceil(depth+1).W)

    
}}


    
object NV_NVDLA_CDMA_IMG_sg2pack_fifoDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_IMG_sg2pack_fifo())
}