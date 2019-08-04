package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_MCIF_WRITE_cq extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        
        //cq_wr
        val cq_wr_prdy = Output(Bool())
        val cq_wr_pvld = Input(Bool())
        val cq_wr_thread_id = Input(UInt(3.W))

        //ifdef FV_RAND_WR_PAUSE
        val cq_wr_pause = Input(Bool())

        val cq_wr_pd = Input(UInt(3.W))
        
        //cq_rd0
        val cq_rd0_prdy = Input(Bool())
        val cq_rd0_pvld = Output(Bool())
        val cq_rd0_pd = Output(UInt(3.W))

        //cq_rd1
        val cq_rd1_prdy = Input(Bool())
        val cq_rd1_pvld = Output(Bool())
        val cq_rd1_pd = Output(UInt(3.W))

        //cq_rd2
        val cq_rd2_prdy = Input(Bool())
        val cq_rd2_pvld = Output(Bool())
        val cq_rd2_pd = Output(UInt(3.W))

        //cq_rd3
        val cq_rd3_prdy = Input(Bool())
        val cq_rd3_pvld = Output(Bool())
        val cq_rd3_pd = Output(UInt(3.W))

        //cq_rd4
        val cq_rd4_prdy = Input(Bool())
        val cq_rd4_pvld = Output(Bool())
        val cq_rd4_pd = Output(UInt(3.W))

        val pwrbus_ram_pd = Input(UInt(32.W))

        })
    withClock(io.nvdla_core_clk){
}}





