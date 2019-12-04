package nvdla

import chisel3._
import chisel3.experimental._


class NV_NVDLA_RT_cmac_a2cacc(delay: Int)(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //src
        val mac2accu_src = Flipped(ValidIO(new cmac2cacc_if))
        val mac2accu_dst = ValidIO(new cmac2cacc_if)
    })

withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

    val mac2accu_pvld_d = retiming(Bool(), delay)
    val mac2accu_data_d = retiming(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)), delay)
    val mac2accu_mask_d = retiming(Vec(conf.CMAC_ATOMK_HALF, Bool()), delay) 
    val mac2accu_pd_d = retiming(UInt(9.W), delay)
    
    //assign input port
    mac2accu_pvld_d(0) := io.mac2accu_src.valid
    mac2accu_mask_d(0) := io.mac2accu_src.bits.mask
    mac2accu_pd_d(0) := io.mac2accu_src.bits.pd
    mac2accu_data_d(0) := io.mac2accu_src.bits.data

    //data flight
    for(t <- 0 to delay-1){
        mac2accu_pvld_d(t+1) := mac2accu_pvld_d(t)  
        when(mac2accu_pvld_d(t)){
            mac2accu_pd_d(t+1) := mac2accu_pd_d(t)
        }        
        mac2accu_mask_d(t+1) := mac2accu_mask_d(t)
        for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
            when(mac2accu_mask_d(t)(i)){
                mac2accu_data_d(t+1)(i):= mac2accu_data_d(t)(i)
            }               
        }      
    }  

    //output assignment

    io.mac2accu_dst.valid := mac2accu_pvld_d(delay)
    io.mac2accu_dst.bits.mask := mac2accu_mask_d(delay) 
    io.mac2accu_dst.bits.pd := mac2accu_pd_d(delay)
    io.mac2accu_dst.bits.data := mac2accu_data_d(delay)
  }}