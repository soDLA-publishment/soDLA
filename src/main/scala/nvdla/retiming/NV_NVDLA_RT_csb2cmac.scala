package nvdla

import chisel3._


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


    val cmac2csb_resp_pd_d  = Reg(Vec(conf.RT_CSB2cmac_LATENCY, UInt(34.W)))
    val cmac2csb_resp_valid_d = Reg(Vec(conf.RT_CSB2cmac_LATENCY, Bool()))
    val csb2cmac_req_pd_d= Reg(Vec(conf.RT_CSB2cmac_LATENCY, UInt(63.W)))
    val csb2cmac_req_pvld_d = Reg(Vec(conf.RT_CSB2cmac_LATENCY, Bool()))


    //assign port
    cmac2csb_resp_pd_d(0) := io.csb2cmac_req_src_pvld
    cmac2csb_resp_valid_d(0) := io.cmac2csb_resp_src_valid
    csb2cmac_req_pd_d(0) := csb2cmac_req_src_pd
    csb2cmac_req_pvld_d(0) := io.csb2cmac_req_src_pvld
    csb2cmac_req_src_prdy := true.B


    //initial condition
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {

        for(t <- 0 to (conf.RT_CSB2cmac_LATENCY-1){

            csb2cmac_req_pvld_d(t+1) := RegNext(csb2cmac_req_pvld_d(t))
            cmac2csb_resp_valid_d(t+1):= RegNext(cmac2csb_resp_valid_d(t))
  
        }
    } 

    //data flight
    withClock(io.nvdla_core_clk) {

        for(t <- 0 to (conf.RT_CSB2cmac_LATENCY-1){
            csb2cmac_req_pd_d(t+1) := RegNext(csb2cmac_req_pd_d(t))
            cmac2csb_resp_pd_d(t+1) := RegNext(cmac2csb_resp_pd_d(t))
        }
   
    }  

    //output assignment

    csb2cmac_req_dst_pvld:=csb2cmac_req_pvld_d(3)
    csb2cmac_req_dst_pd:=csb2cmac_req_pd_d(3)
    cmac2csb_resp_dst_valid:=cmac2csb_resp_valid_d(3)
    cmac2csb_resp_dst_pd:=cmac2csb_resp_pd_d(3)





  }