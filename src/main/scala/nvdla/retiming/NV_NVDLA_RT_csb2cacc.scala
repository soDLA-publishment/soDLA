package nvdla

import chisel3._


class NV_NVDLA_RT_csb2cacc(implicit val conf: csb2caccConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //control signal
        val csb2cacc_req_src_pvld = Input(Bool()) /* data valid */
        val csb2cacc_req_src_prdy = Output(Bool()) /* data return handshake */
        val csb2cacc_req_src_pd = Input(UInt(63.W))//63 is a magic number

        val csb2cacc_req_dst_pvld = Output(Bool()) /* data valid */
        val csb2cacc_req_dst_prdy = Input(Bool()) /* data return handshake */
        val csb2cacc_req_dst_pd = Output(UInt(63.W))//63 is a magic number


        //data signal
        val cacc2csb_resp_src_valid = Input(Bool()) /* data valid */
        val cacc2csb_resp_src_pd =  Input(UInt(34.W))  /* pkt_id_width=1 pkt_widths=33,33  */                                 /* pkt_id_width=1 pkt_widths=33,33  */


        val cacc2csb_resp_dst_valid = Output(Bool()) /* data valid */
        val cacc2csb_resp_dst_pd =  Output(UInt(34.W))  /* pkt_id_width=1 pkt_widths=33,33  */                                 /* pkt_id_width=1 pkt_widths=33,33  */


    })

    val cacc2csb_resp_pd_d_wire = Wire(UInt(34.W))
    val cacc2csb_resp_pd_d_regs = Seq.fill(conf.RT_CSB2CACC_LATENCY)(UInt(34.W))
    val cacc2csb_resp_pd_d = VecInit(cacc2csb_resp_pd_d_wire +: cacc2csb_resp_pd_d_regs)

    val cacc2csb_resp_valid_d_wire = Wire(Bool())
    val cacc2csb_resp_valid_d_regs = Seq.fill(conf.RT_CSB2CACC_LATENCY)(Bool())
    val cacc2csb_resp_valid_d = VecInit(cacc2csb_resp_valid_d_wire +: cacc2csb_resp_valid_d_regs)

    val csb2cacc_req_pd_d_wire = Wire(UInt(63.W))
    val csb2cacc_req_pd_d_regs = Seq.fill(conf.RT_CSB2CACC_LATENCY)(UInt(63.W))
    val csb2cacc_req_pd_d = VecInit(csb2cacc_req_pd_d_wire +: csb2cacc_req_pd_d_regs)

    val csb2cacc_req_pvld_d_wire = Wire(Bool())
    val csb2cacc_req_pvld_d_regs = Seq.fill(conf.RT_CSB2CACC_LATENCY)(Bool())
    val csb2cacc_req_pvld_d = VecInit(csb2cacc_req_pvld_d_wire +: csb2cacc_req_pvld_d_regs)


    //assign port
    csb2cacc_req_pvld_d(0) := io.csb2cacc_req_src_pvld
    csb2cacc_req_pd_d(0) := io.csb2cacc_req_src_pd
    cacc2csb_resp_valid_d(0) := io.cacc2csb_resp_src_valid
    cacc2csb_resp_pd_d(0) := io.cacc2csb_resp_src_pd
    csb2cacc_req_src_prdy := true.B


    //initial condition
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

        for(t <- 0 to (conf.RT_CSB2CACC_LATENCY-1){

            csb2cacc_req_pvld_d(t+1) := csb2cacc_req_pvld_d(t)
            cacc2csb_resp_valid_d(t+1):= cacc2csb_resp_valid_d(t)
  
        }
    } 

    //data flight
    withClock(io.nvdla_core_clk){

        for(t <- 0 to (conf.RT_CSB2CACC_LATENCY-1){
            csb2cacc_req_pd_d(t+1) := csb2cacc_req_pd_d(t)
            cacc2csb_resp_pd_d(t+1) := cacc2csb_resp_pd_d(t)
        }
   
    }  

    //output assignment

    csb2cacc_req_dst_pvld:=csb2cacc_req_pvld_d(3)
    csb2cacc_req_dst_pd:=csb2cacc_req_pd_d(3)
    cacc2csb_resp_dst_valid:=cacc2csb_resp_valid_d(3)
    cacc2csb_resp_dst_pd:=cacc2csb_resp_pd_d(3)

}