package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_RT_csb2cacc(implicit val conf: csb2caccConfiguration) extends RawModule {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        val csb2cacc_req_src_pvld = Input(Bool()) /* data valid */
        val csb2cacc_req_dst_pvld = Output(Bool()) 

        val csb2cacc_req_src_pd = Input(UInt(63.W))//63 is a magic number    
        val csb2cacc_req_dst_pd = Output(UInt(63.W))

        val cacc2csb_resp_src_valid = Input(Bool()) /* data valid */
        val cacc2csb_resp_dst_valid = Output(Bool())

        val cacc2csb_resp_src_pd =  Input(UInt(34.W))  /* pkt_id_width=1 pkt_widths=33,33  */                                 
        val cacc2csb_resp_dst_pd =  Output(UInt(34.W))    

        val csb2cacc_req_dst_prdy = Input(Bool()) /* data return handshake */
        val csb2cacc_req_src_prdy = Output(Bool()) 

    })

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

    io.csb2cacc_req_src_prdy := false.B
        
    //output assignment
    io.csb2cacc_req_dst_pvld:=ShiftRegister(io.csb2cacc_req_src_pvld, conf.RT_CSB2CACC_LATENCY, false.B, true.B)
    io.csb2cacc_req_dst_pd:=ShiftRegister(io.csb2cacc_req_src_pd, conf.RT_CSB2CACC_LATENCY)
    io.cacc2csb_resp_dst_valid:=ShiftRegister(io.cacc2csb_resp_src_valid, conf.RT_CSB2CACC_LATENCY, false.B, true.B)
    io.cacc2csb_resp_dst_pd:=ShiftRegister(io.cacc2csb_resp_src_pd, conf.RT_CSB2CACC_LATENCY)  


}}