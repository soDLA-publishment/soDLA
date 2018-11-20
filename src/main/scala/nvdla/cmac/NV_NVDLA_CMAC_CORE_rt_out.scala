package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._




class NV_NVDLA_CMAC_CORE_rt_out(implicit val conf: cmacConfiguration) extends RawModule {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock()) 
        val nvdla_wg_clk = Input(Clock())          
        val nvdla_core_rstn = Input(Bool())

        //config
        val cfg_is_wg = Input(Bool())
        val cfg_reg_en = Input(Bool())

        //input:(atomk_half, cmac_result)
        val out_data = Input(Vec(conf.CMAC_ATOMK_HALF, conf.CMAC_TYPE(conf.CMAC_RESULT_WIDTH.W)))
        val out_mask = Input(Vec(conf.CMAC_ATOMK_HALF, Bool()))
        val out_pd = Input(UInt(9.W))
        val out_pvld = Input(Bool())

        //output:(atomk_half, cmac_result)  
        val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF, conf.CMAC_TYPE(conf.CMAC_RESULT_WIDTH.W)))
        val mac2accu_mask = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))
        val mac2accu_pd = Output(UInt(9.W))
        val mac2accu_pvld = Output(Bool())
    })

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 
                
    //==========================================================
    // Config logic
    //==========================================================

    val cfg_reg_en_d1 = RegInit(false.B)
    val cfg_is_wg_d1 = RegInit(false.B)

    cfg_reg_en_d1 := io.cfg_reg_en
    when(io.cfg_reg_en){
        cfg_is_wg_d1:= io.cfg_is_wg
    }

    //==========================================================
    // Output retiming
    //==========================================================

    //initial value
    val out_rt_pvld_d = retimingInit(false.B, conf.CMAC_OUT_RT_LATENCY)
    val out_rt_mask_d = retimingInit(Vec(conf.CMAC_ATOMK_HALF, false.B), conf.CMAC_OUT_RT_LATENCY)  
    val out_rt_pd_d = retimingInit(0.asUInt(9.W), conf.CMAC_OUT_RT_LATENCY)  
    val out_rt_data_d = retiming(Vec(conf.CMAC_ATOMK_HALF, conf.CMAC_TYPE(conf.CMAC_RESULT_WIDTH.W)), conf.CMAC_OUT_RT_LATENCY)

    //delay input
    out_rt_pvld_d(0) := io.out_pvld
    out_rt_mask_d(0) := io.out_mask
    out_rt_pd_d(0) := io.out_pd
    out_rt_data_d(0) := io.out_data

    //passing logic

    for(t <- 0 to conf.CMAC_OUT_RT_LATENCY-1){
        out_rt_pvld_d(t+1) := out_rt_pvld_d(t)
        out_rt_mask_d(t+1) := out_rt_mask_d(t)
        when(out_rt_pvld_d(t)){
            out_rt_pd_d(t+1) := out_rt_pd_d(t)
        }
        for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
            when(out_rt_mask_d(t)(i)){  
                out_rt_data_d(t+1)(i) := out_rt_data_d(t)(i)
            } 
        }      
    } 
    //assign output
    io.mac2accu_pvld := out_rt_pvld_d(conf.CMAC_OUT_RT_LATENCY)
    io.mac2accu_mask := out_rt_mask_d(conf.CMAC_OUT_RT_LATENCY)
    io.mac2accu_pd := out_rt_pd_d(conf.CMAC_OUT_RT_LATENCY)
    io.mac2accu_data := out_rt_data_d(conf.CMAC_OUT_RT_LATENCY)
  
}}
    
    

    



    















    



 

