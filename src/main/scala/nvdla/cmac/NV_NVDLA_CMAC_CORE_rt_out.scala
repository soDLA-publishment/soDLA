package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CMAC_CORE_rt_out(useRealClock:Boolean = false)(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())

        //input:(atomk_half, cmac_result)
        val out = Flipped(ValidIO(new cmac2cacc_if))  /* data valid */

        //output:(atomk_half, cmac_result)  
        val mac2accu = ValidIO(new cmac2cacc_if)    /* data valid */
        val dp2reg_done = Output(Bool())
    })
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
    val internal_clock = if(useRealClock) io.nvdla_core_clk else clock

    class rt_outImpl{
    //==========================================================
    // Output retiming
    //==========================================================

    //initial value
    val out_rt_pvld_d = Wire(Bool()) +: 
                        Seq.fill(conf.CMAC_OUT_RT_LATENCY)(RegInit(false.B))

    val out_rt_mask_d = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool())) +: 
                        Seq.fill(conf.CMAC_OUT_RT_LATENCY)(RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(false.B)))) 

    val out_rt_pd_d = Wire(UInt(9.W)) +: 
                      Seq.fill(conf.CMAC_OUT_RT_LATENCY)(RegInit("b0".asUInt(9.W))) 
                       
    val out_rt_data_d = retiming(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)), conf.CMAC_OUT_RT_LATENCY)

    val dp2reg_done_d = Wire(Bool()) +: 
                        Seq.fill(conf.CMAC_OUT_RT_LATENCY)(RegInit(false.B))
    //delay input
    out_rt_pvld_d(0) := io.out.valid
    out_rt_mask_d(0) := io.out.bits.mask
    out_rt_pd_d(0) := io.out.bits.pd
    out_rt_data_d(0) := io.out.bits.data

    dp2reg_done_d(0) := io.out.bits.pd(conf.PKT_nvdla_stripe_info_layer_end_FIELD)&
                        io.out.bits.pd(conf.PKT_nvdla_stripe_info_stripe_end_FIELD)&
                        io.out.valid

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
        dp2reg_done_d(t+1) := dp2reg_done_d(t)      
    } 
    //assign output
    io.mac2accu.valid := out_rt_pvld_d(conf.CMAC_OUT_RT_LATENCY)
    io.mac2accu.bits.mask := out_rt_mask_d(conf.CMAC_OUT_RT_LATENCY)
    io.mac2accu.bits.pd := out_rt_pd_d(conf.CMAC_OUT_RT_LATENCY)
    io.mac2accu.bits.data := out_rt_data_d(conf.CMAC_OUT_RT_LATENCY)
    io.dp2reg_done := dp2reg_done_d(conf.CMAC_OUT_RT_LATENCY)
  
}
    val rt_out = withClock(internal_clock){new rt_outImpl}
}

object NV_NVDLA_CMAC_CORE_rt_outDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CMAC_CORE_rt_out(useRealClock = true))
}

    
    

    



    















    



 

