package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_MRDMA_eg extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val pwrbus_ram_pd = Input(UInt(32.W))
        val op_load = Input(Bool())
        val eg_done = Output(Bool())
        val cq2eg_pd = Input(UInt(14.W))
        val cq2eg_pvld = Input(Bool())
        val cq2eg_prdy = Output(Bool())
        val dma_rd_rsp_ram_type = Output(Bool())
modify yourself
        val dma_rd_rsp_vld = Input(Bool())
        val dma_rd_rsp_rdy = Output(Bool())
        val dma_rd_cdt_lat_fifo_pop = Output(Bool())
modify yourself
        val sdp_mrdma2cmux_valid = Output(Bool())
        val sdp_mrdma2cmux_ready = Input(Bool())
        val reg2dp_src_ram_type = Input(Bool())
        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_width = Input(UInt(13.W))
        val reg2dp_in_precision = Input(UInt(2.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_perf_nan_inf_count_en = Input(Bool())
        val dp2reg_status_inf_input_num = Output(UInt(32.W))
        val dp2reg_status_nan_input_num = Output(UInt(32.W))
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
withClock(io.nvdla_core_clk)
}