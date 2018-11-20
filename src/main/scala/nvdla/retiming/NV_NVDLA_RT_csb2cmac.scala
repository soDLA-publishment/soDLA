package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_RT_csb2cmac(implicit val conf: csb2cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //control signal
        val csb2cmac_req_src_pvld = Input(Bool()) /* data valid */
        val csb2cmac_req_src_prdy = Output(Bool()) /* data return handshake */
        val csb2cmac_req_src_pd = Input(UInt(63.W))//63 is a magic number

        val csb2cmac_req_dst_pvld = Output(Bool()) /* data valid */
        val csb2cmac_req_dst_prdy = Input(Bool()) /* data return handshake */
        val csb2cmac_req_dst_pd = Output(UInt(63.W))//63 is a magic number


        //data signal
        val cmac2csb_resp_src_valid = Input(Bool()) /* data valid */
        val cmac2csb_resp_src_pd =  Input(UInt(34.W))  /* pkt_id_width=1 pkt_widths=33,33  */                                 /* pkt_id_width=1 pkt_widths=33,33  */


        val cmac2csb_resp_dst_valid = Output(Bool()) /* data valid */
        val cmac2csb_resp_dst_pd =  Output(UInt(34.W))  /* pkt_id_width=1 pkt_widths=33,33  */                                 /* pkt_id_width=1 pkt_widths=33,33  */


    })

    //output assignment

    io.csb2cmac_req_src_prdy := true.B

    io.csb2cmac_req_dst_pvld := ShiftRegister(io.csb2cmac_req_src_pvld, 3, false.B, true.B)
    io.csb2cmac_req_dst_pd := ShiftRegister(io.csb2cmac_req_src_pd, 3)
    io.cmac2csb_resp_dst_valid := ShiftRegister(io.cmac2csb_resp_src_valid, 3, false.B, true.B)
    io.cmac2csb_resp_dst_pd := ShiftRegister(io.cmac2csb_resp_src_pd, 3)

}