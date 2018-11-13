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

    val cmac2csb_resp_pd_d_wire = Wire(UInt(34.W))
    val cmac2csb_resp_pd_d_regs = Seq.fill(conf.RT_CSB2CACC_LATENCY)(UInt(34.W))
    val cmac2csb_resp_pd_d = VecInit(cmac2csb_resp_pd_d_wire +: cmac2csb_resp_pd_d_regs)

    val cmac2csb_resp_valid_d_wire = Wire(Bool())
    val cmac2csb_resp_valid_d_regs = Seq.fill(conf.RT_CSB2CACC_LATENCY)(Bool())
    val cmac2csb_resp_valid_d = VecInit(cmac2csb_resp_valid_d_wire +: cmac2csb_resp_valid_d_regs)

    val csb2cmac_req_pd_d_wire = Wire(UInt(63.W))
    val csb2cmac_req_pd_d_regs = Seq.fill(conf.RT_CSB2CACC_LATENCY)(UInt(63.W))
    val csb2cmac_req_pd_d = VecInit(csb2cmac_req_pd_d_wire +: csb2cmac_req_pd_d_regs)

    val csb2cmac_req_pvld_d_wire = Wire(Bool())
    val csb2cmac_req_pvld_d_regs = Seq.fill(conf.RT_CSB2CACC_LATENCY)(Bool())
    val csb2cmac_req_pvld_d = VecInit(csb2cmac_req_pvld_d_wire +: csb2cmac_req_pvld_d_regs)


    //assign port
    csb2cmac_req_pvld_d(0) := io.csb2cmac_req_src_pvld
    csb2cmac_req_pd_d(0) := io.csb2cmac_req_src_pd
    cmac2csb_resp_valid_d(0) := io.cmac2csb_resp_src_valid
    cmac2csb_resp_pd_d(0) := io.cmac2csb_resp_src_pd
    io.csb2cmac_req_src_prdy := true.B


    //initial condition
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

        for(t <- 0 to (conf.RT_CSB2CACC_LATENCY-1)){

            csb2cmac_req_pvld_d(t+1) := csb2cmac_req_pvld_d(t)
            cmac2csb_resp_valid_d(t+1):= cmac2csb_resp_valid_d(t)
  
        }
    } 

    //data flight
    withClock(io.nvdla_core_clk){

        for(t <- 0 to (conf.RT_CSB2CACC_LATENCY-1)){
            csb2cmac_req_pd_d(t+1) := csb2cmac_req_pd_d(t)
            cmac2csb_resp_pd_d(t+1) := cmac2csb_resp_pd_d(t)
        }
   
    }  

    //output assignment

    io.csb2cmac_req_dst_pvld:=csb2cmac_req_pvld_d(3)
    io.csb2cmac_req_dst_pd:=csb2cmac_req_pd_d(3)
    io.cmac2csb_resp_dst_valid:=cmac2csb_resp_valid_d(3)
    io.cmac2csb_resp_dst_pd:=cmac2csb_resp_pd_d(3)

}