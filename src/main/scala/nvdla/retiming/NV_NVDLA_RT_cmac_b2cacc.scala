package nvdla

import chisel3._
import chisel3.experimental._

//switch to nvdlav2 branch


class NV_NVDLA_RT_cmac_b2cacc(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //src
        val mac2accu_src_pvld = Input(Bool())
        val mac2accu_src_mask = Input(Vec(conf.CMAC_ATOMK_HALF, Bool()))
        val mac2accu_src_mode = Input(Bool())
        val mac2accu_src_pd = Input(UInt(9.W))//magic number
        val mac2accu_src_data = Input(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_RESULT_WIDTH).W)))

        val mac2accu_dst_pvld = Output(Bool())
        val mac2accu_dst_mask = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))
        val mac2accu_dst_mode = Output(Bool())
        val mac2accu_dst_pd = Output(UInt(9.W))//magic number
        val mac2accu_dst_data = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_RESULT_WIDTH).W)))


    })
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

    val mac2accu_data_d = retiming(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)), conf.RT_CMAC_B2CACC_LATENCY)
    val mac2accu_pvld_d = retiming(Bool(), conf.RT_CMAC_B2CACC_LATENCY)
    val mac2accu_mask_d = retiming(Vec(conf.CMAC_ATOMK_HALF, Bool()), conf.RT_CMAC_B2CACC_LATENCY) 
    val mac2accu_mode_d = retiming(Bool(), conf.RT_CMAC_B2CACC_LATENCY)
    val mac2accu_pd_d = retiming(UInt(9.W), conf.RT_CMAC_B2CACC_LATENCY)
    
    //assign input port
    mac2accu_pvld_d(0) := io.mac2accu_src_pvld
    mac2accu_mask_d(0) := io.mac2accu_src_mask
    mac2accu_mode_d(0) := io.mac2accu_src_mode
    mac2accu_pd_d(0) := io.mac2accu_src_pd
    mac2accu_data_d(0) := io.mac2accu_src_data


    //data flight
    for(t <- 0 to conf.RT_CMAC_B2CACC_LATENCY-1){
        mac2accu_pvld_d(t+1) := mac2accu_pvld_d(t)
        mac2accu_mask_d(t+1) := mac2accu_mask_d(t)
        when(mac2accu_pvld_d(t)){
            mac2accu_pd_d(t+1) := mac2accu_pd_d(t)
            mac2accu_mode_d(t+1) := mac2accu_mode_d(t) 
        }        
        for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
            when (mac2accu_mask_d(t)(i)){mac2accu_data_d(t+1)(i):= mac2accu_data_d(t)(i)}               
        }      
    }  

    //output assignment

    io.mac2accu_dst_pvld := mac2accu_pvld_d(conf.RT_CMAC_B2CACC_LATENCY)
    io.mac2accu_dst_mask := mac2accu_mask_d(conf.RT_CMAC_B2CACC_LATENCY) 
    io.mac2accu_dst_mode := mac2accu_mode_d(conf.RT_CMAC_B2CACC_LATENCY)
    io.mac2accu_dst_pd := mac2accu_pd_d(conf.RT_CMAC_B2CACC_LATENCY)
    io.mac2accu_dst_data := mac2accu_data_d(conf.RT_CMAC_B2CACC_LATENCY)
  }}