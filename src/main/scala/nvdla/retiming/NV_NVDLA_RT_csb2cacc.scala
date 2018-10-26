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


        val cacc2csb_resp_dst_valid = Input(Bool()) /* data valid */
        val cacc2csb_resp_dst_pd =  Input(UInt(34.W))  /* pkt_id_width=1 pkt_widths=33,33  */                                 /* pkt_id_width=1 pkt_widths=33,33  */


    })


    val cacc2csb_resp_pd_d  = Reg(Vec(conf.RT_CMAC_A2CACC_LATENCY, UInt(34.W)))
    val cacc2csb_resp_valid_d = Reg(Vec(conf.RT_CMAC_A2CACC_LATENCY, UInt((conf.CMAC_ATOMK_HALF).W)))
    val mac2accu_pd_d = Reg(Vec(conf.RT_CMAC_A2CACC_LATENCY, UInt(9.W))
    val mac2accu_pvld_d = Reg(Vec(conf.RT_CMAC_A2CACC_LATENCY, Bool()))


    //assign input port
    mac2accu_pvld_d(0) := io.mac2accu_src_pvld
    mac2accu_mask_d(0) := io.mac2accu_src_mask
    mac2accu_mode_d(0) := io.mac2accu_src_mode
    mac2accu_pd_d(0) := io.mac2accu_src_pd
    mac2accu_data_d(0) := io.mac2accu_src_data


    //initial condition
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {

        for(t <- 0 to (conf.RT_CMAC_A2CACC_LATENCY-1){

            mac2accu_pvld_d(t+1) := RegNext(mac2accu_pvld_d(t))
            mac2accu_mask_d(t+1) := RegNext(mac2accu_mask_d(t))
  
        }
    } 

    //data flight
    withClock(io.nvdla_core_clk) {
        for(t <- 0 to (conf.RT_CMAC_A2CACC_LATENCY-1){

            mac2accu_pd_d(t+1) := ShiftRegister(mac2accu_pd_d(t) , 1, mac2accu_pvld_d(t))
            mac2accu_mode_d(t+1) := ShiftRegister(mac2accu_mode_d(t) , 1, mac2accu_pvld_d(t))
            
            for(i <- 0 to (conf.CMAC_ATOMK_HALF-1){
            when (mac2accu_mask_d(t)(i)){

                    mac2accu_data_d(t+1)(i)(43,0):= RegNext(mac2accu_data_d(t)(i)(43,0))
                }
                when (mac2accu_mode_d(t)(i)){

                        mac2accu_data_d(t+1)(i)(conf.CMAC_RESULT_WIDTH,44):= RegNext(mac2accu_data_d(t)(i)(conf.CMAC_RESULT_WIDTH,44))

                }       
                
            }
        }
   
    }  

    //output assignment

    mac2accu_pvld_d(2) := io.mac2accu_dst_pvld
    mac2accu_mask_d(2) := io.mac2accu_dst_mask
    mac2accu_mode_d(2) := io.mac2accu_dst_mode
    mac2accu_pd_d(2) := io.mac2accu_dst_pd
    mac2accu_data_d(2) := io.mac2accu_dst_data



  }