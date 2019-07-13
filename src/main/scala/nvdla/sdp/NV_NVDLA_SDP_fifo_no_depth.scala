package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._


//NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo

class NV_NVDLA_SDP_fifo_no_depth(width: Int) extends Module {
    val io = IO(new Bundle {
        //general clock
        val clk = Input(Clock())

        val wr_pvld = Input(Bool())  
        val wr_pd = Input(UInt(width.W))

        val rd_pvld = Output(Bool()) 
        val rd_prdy = Input(Bool())  
        val rd_pd = Output(UInt(width.W)) 

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
    val rd_req_next_o = (rd_pvld_p || (rd_pvld_int_o && !rd_prdy_d_o))

    rd_pvld_int_o := rd_req_next_o
    rd_pd_o := Fill(width, false.B)

    //
    // FINAL OUTPUT
    //
    val rd_pd_out = Reg(UInt(width.W))  // output data register
    val rd_pvld_int_d = RegInit(false.B)    // so we can bubble-collapse rd_prdy_d
    rd_prdy_d_o := !((rd_pvld_o && rd_pvld_int_d && !rd_prdy_d))
    val rd_req_next = Mux(!rd_prdy_d_o,  rd_pvld_o, rd_pvld_p)  

    when(!rd_pvld_int || io.rd_prdy ){
        rd_pvld_int := rd_req_next
    }
    rd_pvld_int_d := rd_pvld_int

    when(rd_req_next && (!rd_pvld_int || io.rd_prdy)){
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
                     
  
}}


object NV_NVDLA_SDP_fifo_no_depthDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_fifo_no_depth(1))
}
