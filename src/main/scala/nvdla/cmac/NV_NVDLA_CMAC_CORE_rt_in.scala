package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_CMAC_CORE_rt_in(useRealClock:Boolean = false)(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())

        // odif
        // sc2mac dat&wt
        val sc2mac_dat = Flipped(ValidIO(new csc2cmac_data_if))  /* data valid */
        val sc2mac_wt = Flipped(ValidIO(new csc2cmac_wt_if))  /* data valid */

        // in dat&wt 
        val in_dat = ValidIO(new csc2cmac_data_if)  /* data valid */
        val in_wt = ValidIO(new csc2cmac_wt_if)  /* data valid */
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

    class rt_inImpl{

    // retiming init

    val in_rt_dat_data_d = retiming(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)), conf.CMAC_IN_RT_LATENCY)
    val in_rt_dat_mask_d = Wire(Vec(conf.CMAC_ATOMC, Bool())) +: 
                            Seq.fill(conf.CMAC_IN_RT_LATENCY)(RegInit(VecInit(Seq.fill(conf.CMAC_ATOMC)(false.B)))) 
    val in_rt_dat_pvld_d = Wire(Bool()) +: 
                            Seq.fill(conf.CMAC_IN_RT_LATENCY)(RegInit(false.B))
    val in_rt_dat_pd_d = Wire(UInt(9.W)) +: 
                            Seq.fill(conf.CMAC_IN_RT_LATENCY)(RegInit("b0".asUInt(9.W)))


    val in_rt_wt_data_d = retiming(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)), conf.CMAC_IN_RT_LATENCY)
    val in_rt_wt_mask_d = Wire(Vec(conf.CMAC_ATOMC, Bool())) +: 
                            Seq.fill(conf.CMAC_IN_RT_LATENCY)(RegInit(VecInit(Seq.fill(conf.CMAC_ATOMC)(false.B)))) 
    val in_rt_wt_pvld_d = Wire(Bool()) +: 
                            Seq.fill(conf.CMAC_IN_RT_LATENCY)(RegInit(false.B))
    val in_rt_wt_sel_d =  Wire(Vec(conf.CMAC_ATOMK_HALF, Bool())) +: 
                            Seq.fill(conf.CMAC_IN_RT_LATENCY)(RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(false.B)))) 

    // assign input

    in_rt_dat_pvld_d(0) := io.sc2mac_dat.valid
    in_rt_dat_mask_d(0) := io.sc2mac_dat.bits.mask
    in_rt_dat_pd_d(0) := io.sc2mac_dat.bits.pd
    in_rt_wt_pvld_d(0) := io.sc2mac_wt.valid
    in_rt_wt_mask_d(0) := io.sc2mac_wt.bits.mask
    in_rt_wt_sel_d(0) := io.sc2mac_wt.bits.sel
    in_rt_dat_data_d(0) := io.sc2mac_dat.bits.data
    in_rt_wt_data_d(0) := io.sc2mac_wt.bits.data

    //==========================================================
    // Retiming flops,add latency.
    //==========================================================   
    for(t <- 0 to conf.CMAC_IN_RT_LATENCY-1){
        in_rt_dat_pvld_d(t+1) := in_rt_dat_pvld_d(t)
        when(in_rt_dat_pvld_d(t)|in_rt_dat_pvld_d(t+1)){
            in_rt_dat_mask_d(t+1) := in_rt_dat_mask_d(t)
            in_rt_dat_pd_d(t+1) := in_rt_dat_pd_d(t)
        }

        in_rt_wt_pvld_d(t+1) := in_rt_wt_pvld_d(t)
        when(in_rt_wt_pvld_d(t)|in_rt_wt_pvld_d(t+1)){
            in_rt_wt_mask_d(t+1) := in_rt_wt_mask_d(t)
            in_rt_wt_sel_d(t+1) := in_rt_wt_sel_d(t)           
        }
        for(i <- 0 to conf.CMAC_ATOMC-1){
            when(in_rt_dat_mask_d(t)(i)){
                in_rt_dat_data_d(t+1)(i) := in_rt_dat_data_d(t)(i)                   
            }
            when(in_rt_wt_mask_d(t)(i)){
                in_rt_wt_data_d(t+1)(i) := in_rt_wt_data_d(t)(i) 
            }  
        }
    }          

    //assign output

    io.in_dat.valid := in_rt_dat_pvld_d(conf.CMAC_IN_RT_LATENCY)
    io.in_dat.bits.mask := in_rt_dat_mask_d(conf.CMAC_IN_RT_LATENCY)
    io.in_dat.bits.pd := in_rt_dat_pd_d(conf.CMAC_IN_RT_LATENCY)
    io.in_dat.bits.data := in_rt_dat_data_d(conf.CMAC_IN_RT_LATENCY)
    io.in_wt.valid := in_rt_wt_pvld_d(conf.CMAC_IN_RT_LATENCY)
    io.in_wt.bits.mask := in_rt_wt_mask_d(conf.CMAC_IN_RT_LATENCY)
    io.in_wt.bits.sel := in_rt_wt_sel_d(conf.CMAC_IN_RT_LATENCY)
    io.in_wt.bits.data := in_rt_wt_data_d(conf.CMAC_IN_RT_LATENCY)


    }

    val rt_in = withClock(internal_clock){new rt_inImpl}

}

object NV_NVDLA_CMAC_CORE_rt_inDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CMAC_CORE_rt_in(useRealClock = true))
}
