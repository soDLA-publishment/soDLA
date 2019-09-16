package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_MCIF_WRITE_IG_bpt (conf:nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val axid = Input(UInt(4.W))
        
        //dma2bpt
        val dma2bpt_req_valid = Input(Bool())
        val dma2bpt_req_ready = Output(Bool())
        val dma2bpt_req_pd = Input(UInt(515.W))

        //bpt2arb
        val bpt2arb_cmd_valid = Output(Bool())
        val bpt2arb_cmd_ready = Input(Bool())
        val bpt2arb_cmd_pd = Output(UInt(77.W))

        val bpt2arb_dat_valid = Output(Bool())
        val bpt2arb_dat_ready = Input(Bool())
        val bpt2arb_dat_pd = Output(UInt(514.W))
    })
    
    withClock(io.nvdla_core_clk){
    }
}   