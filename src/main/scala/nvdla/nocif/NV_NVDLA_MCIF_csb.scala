package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_MCIF_WRITE_iq extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())

        //csb2mcif
        val csb2mcif_req_pvld = Input(Bool())
        val csb2mcif_req_prdy = Output(Bool())
        val csb2mcif_req_pd = Input(UInt(63.W))

        //mcif2csb
        val mcif2csb_resp_valid = Output(Bool())
        val mcif2csb_resp_pd = Output(UInt(34.W))

        val dp2reg_idle = Input(Bool())

        //reg2dp
        val reg2dp_rd_os_cnt = Output(UInt(8.W))
        val reg2dp_rd_weight_bdma = Output(UInt(8.W))
        val reg2dp_rd_weight_cdma_dat = Output(UInt(8.W))
        val reg2dp_rd_weight_cdma_wt = Output(UInt(8.W))
        val reg2dp_rd_weight_cdp = Output(UInt(8.W))
        val reg2dp_rd_weight_pdp = Output(UInt(8.W))
        val reg2dp_rd_weight_rbk = Output(UInt(8.W))
        val reg2dp_rd_weight_rsv_0 = Output(UInt(8.W))
        val reg2dp_rd_weight_rsv_1 = Output(UInt(8.W))
        val reg2dp_rd_weight_sdp = Output(UInt(8.W))
        val reg2dp_rd_weight_sdp_b = Output(UInt(8.W))
        val reg2dp_rd_weight_sdp_e = Output(UInt(8.W))
        val reg2dp_rd_weight_sdp_n = Output(UInt(8.W))
        val reg2dp_wr_os_cnt = Output(UInt(8.W))
        val reg2dp_wr_weight_bdma = Output(UInt(8.W))
        val reg2dp_wr_weight_cdp = Output(UInt(8.W))
        val reg2dp_wr_weight_pdp = Output(UInt(8.W))
        val reg2dp_wr_weight_rbk = Output(UInt(8.W))
        val reg2dp_wr_weight_rsv_0 = Output(UInt(8.W))
        val reg2dp_wr_weight_rsv_1 = Output(UInt(8.W))
        val reg2dp_wr_weight_rsv_2 = Output(UInt(8.W))
        val reg2dp_wr_weight_sdp = Output(UInt(8.W))

        })
    withClock(io.nvdla_core_clk){
}}
