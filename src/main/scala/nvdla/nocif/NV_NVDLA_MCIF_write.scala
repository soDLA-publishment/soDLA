package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_MCIF_write(implicit conf: xxifConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        val bdma2mcif_wr_req_valid = Input(Bool())        /* data valid */
        val bdma2mcif_wr_req_ready = Output(Bool())        /* data return handshake */
        val bdma2mcif_wr_req_pd = Input(UInt(515.W)) /* pkt_id_width=1 pkt_widths=78,514  */

        val cdp2mcif_wr_req_valid = Input(Bool())        /* data valid */
        val cdp2mcif_wr_req_ready = Output(Bool())        /* data return handshake */
        val cdp2mcif_wr_req_pd = Input(UInt(515.W)) /* pkt_id_width=1 pkt_widths=78,514  */

        val pdp2mcif_wr_req_valid = Input(Bool())        /* data valid */
        val pdp2mcif_wr_req_ready = Output(Bool())        /* data return handshake */
        val pdp2mcif_wr_req_pd = Input(UInt(515.W)) /* pkt_id_width=1 pkt_widths=78,514  */

        val rbk2mcif_wr_req_valid = Input(Bool())        /* data valid */
        val rbk2mcif_wr_req_ready = Output(Bool())        /* data return handshake */
        val rbk2mcif_wr_req_pd = Input(UInt(515.W)) /* pkt_id_width=1 pkt_widths=78,514  */

        val sdp2mcif_wr_req_valid = Input(Bool())        /* data valid */
        val sdp2mcif_wr_req_ready = Output(Bool())        /* data return handshake */
        val sdp2mcif_wr_req_pd = Input(UInt(515.W)) /* pkt_id_width=1 pkt_widths=78,514  */

        val mcif2bdma_wr_rsp_complete = Output(Bool())
        val mcif2cdp_wr_rsp_complete = Output(Bool())
        val mcif2pdp_wr_rsp_complete = Output(Bool())
        val mcif2rbk_wr_rsp_complete = Output(Bool())
        val mcif2sdp_wr_rsp_complete = Output(Bool())

        //noc2mcif
        val noc2mcif_axi_b_bvalid = Input(Bool())
        val noc2mcif_axi_b_bready = Output(Bool())
        val noc2mcif_axi_b_bid = Input(UInt(8.W))

        val pwrbus_ram_pd = Input(UInt(32.W))

        val reg2dp_wr_os_cnt = Input(UInt(8.W))
        val reg2dp_wr_weight_bdma = Input(UInt(8.W))
        val reg2dp_wr_weight_cdp = Input(UInt(8.W))
        val reg2dp_wr_weight_pdp = Input(UInt(8.W))
        val reg2dp_wr_weight_rbk = Input(UInt(8.W))
        val reg2dp_wr_weight_sdp = Input(UInt(8.W))
    })
}