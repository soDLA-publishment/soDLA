package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._


//NV_NVDLA_PDP_cal1d_info_fifo

class NV_NVDLA_PDP_fifo_flop_based(depth: Int, width: Int) extends Module {
    val io = IO(new Bundle {
        //general clock
        val clk = Input(Clock())

        //wr pipeline / ig2cq -- write(wr) 
        val wr_vld = Input(Bool())  
        val wr_rdy = Output(Bool())
        val wr_data = Input(UInt(width.W))

        //rd pipeline / cq2eg -- read(rd)
        val rd_vld = Output(Bool()) 
        val rd_rdy = Input(Bool())
        val rd_data = Output(UInt(width.W)) 

        //
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

    val clk_mgated_enable = Wire(Bool())  // assigned by code at end of this module
    val clk_mgate = Module(new NV_CLK_gate_power)
    clk_mgate.io.clk := io.clk
    clk_mgate.io.clk_en := clk_mgated_enable 
    val clk_mgated = clk_mgate.io.clk_gated

    // 
    // WRITE SIDE
    //  
    val wr_reserving = Wire(Bool()) 
    val wr_busy_int = withClock(clk_mgated){RegInit(false.B)}    // copy for internal use
    io.wr_rdy := !wr_busy_int
    wr_reserving := io.wr_vld & !wr_busy_int   // reserving write space?
 
    val wr_popping = Wire(Bool())  // fwd: write side sees pop?
    val wr_count = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))}   // write-side 
    
    val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, (wr_count - 1.U))
    val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count + 1.U, wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_full = ( wr_count_next_no_wr_popping === depth.U)
    val wr_count_next_is_full = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_full)

    val wr_limit_muxed = Wire(UInt(log2Ceil(depth+1).W))  // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed
    val wr_busy_next = wr_count_next_is_full || (wr_limit_reg =/= 0.U &&  wr_count_next>= wr_limit_reg)
    
    wr_busy_int := wr_busy_next
    when (wr_reserving ^ wr_popping) {
        wr_count := wr_count_next
    } 

    val wr_pushing = wr_reserving   // data pushed same cycle as wr_vld

    //RAM

    val wr_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))} 			// current write address
    when(wr_pushing){
        wr_adr := wr_adr + 1.U
    }

    val rd_popping = Wire(Bool())

    val rd_adr = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))} 	     // read address to use for ram
    val ram_we = wr_pushing && (wr_count > 0.U || !rd_popping);   // note: write occurs next cycle

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.

    val ram = Module(new nv_flopram(depth, width))
    ram.io.clk := clk_mgated
    ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    ram.io.di := io.wr_data
    ram.io.we := ram_we
    ram.io.wa := wr_adr
    ram.io.ra := Mux(wr_count === 0.U, depth.U, Cat(false.B, rd_adr))
    io.rd_data := ram.io.dout

    // next    read address 
    val rd_adr_next_popping = rd_adr + 1.U
    when (rd_popping) {
        rd_adr := rd_adr_next_popping
    }  

    //
    // SYNCHRONOUS BOUNDARY
    //
    wr_popping := rd_popping    // let it be seen immediately

    val rd_pushing = wr_pushing    // let it be seen immediately

    //
    // READ SIDE
    //

    rd_popping := io.rd_vld && io.rd_rdy
    val rd_count = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))} // read-side fifo count
    // spyglass disable_block W164a W484

    val rd_count_next_rd_popping = Mux(rd_pushing, rd_count, rd_count - 1.U)
    val rd_count_next_no_rd_popping =  Mux(rd_pushing, rd_count + 1.U, rd_count)

    // spyglass enable_block W164a W484
    val rd_count_next = Mux(rd_popping,  rd_count_next_rd_popping,  rd_count_next_no_rd_popping)
    io.rd_vld := (rd_count =/= 0.U) || rd_pushing

    when(rd_pushing || rd_popping){
        rd_count := rd_count_next
    } 

    // Master Clock Gating (SLCG) Enables
    //

    clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
                        (io.wr_vld && !wr_busy_int) || (wr_busy_int =/= wr_busy_next)) || 
                        (rd_pushing || rd_popping || (io.rd_vld && io.rd_rdy)) || 
                        (wr_pushing))
 
    wr_limit_muxed := "b0".asUInt(log2Ceil(depth+1).W)
}}

object NV_NVDLA_SDP_fifo_flop_basedDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_fifo_flop_based(4, 15))
}
