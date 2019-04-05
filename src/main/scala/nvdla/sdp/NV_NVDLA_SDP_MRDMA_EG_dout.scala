package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_MRDMA_EG_dout extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val op_load = Input(Bool())
modify yourself
        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_width = Input(UInt(13.W))
        val reg2dp_in_precision = Input(UInt(2.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_perf_nan_inf_count_en = Input(Bool())
        val dp2reg_status_inf_input_num = Output(UInt(32.W))
        val dp2reg_status_nan_input_num = Output(UInt(32.W))

        val sdp_mrdma2cmux_valid = Output(Bool())
        val sdp_mrdma2cmux_ready = Input(Bool())
modify yourself

        val cmd2dat_dma_pvld = Input(Bool())
        val cmd2dat_dma_prdy = Output(Bool())
        val cmd2dat_dma_pd = Input(UInt(15.W))

        val pfifo0_rd_pvld = Input(Bool())
        val pfifo0_rd_prdy = Output(Bool())
modify yourself

        val pfifo1_rd_pvld = Input(Bool())
        val pfifo1_rd_prdy = Output(Bool())
modify yourself

        val pfifo2_rd_pvld = Input(Bool())
        val pfifo2_rd_prdy = Output(Bool())
modify yourself

        val pfifo3_rd_pvld = Input(Bool())
        val pfifo3_rd_prdy = Output(Bool())
modify yourself
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