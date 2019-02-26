package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

//this module is to mac dat and wt

class NV_NVDLA_CMAC_CORE_mac(useRealClock:Boolean = false)(implicit conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())

        //wt and dat
        val dat_actv_data = Input(Vec(conf.CMAC_ATOMC, conf.CMAC_TYPE(conf.CMAC_BPE.W)))
        val dat_actv_nz = Input(Vec(conf.CMAC_ATOMC, Bool()))
        val dat_actv_pvld = Input(Vec(conf.CMAC_ATOMC, Bool()))

        val wt_actv_data = Input(Vec(conf.CMAC_ATOMC, conf.CMAC_TYPE(conf.CMAC_BPE.W)))
        val wt_actv_nz = Input(Vec(conf.CMAC_ATOMC, Bool()))
        val wt_actv_pvld = Input(Vec(conf.CMAC_ATOMC, Bool()))

        //output
        val mac_out_data = Output(conf.CMAC_TYPE(conf.CMAC_RESULT_WIDTH.W))
        val mac_out_pvld = Output(Bool())         
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

    val internal_clock = if(useRealClock) io.nvdla_core_clk 
                         else clock


    class macImpl{

    val mout = VecInit(Seq.fill(conf.CMAC_ATOMC)(conf.CMAC_TYPE(0, (2*conf.CMAC_BPE).W)))

    for(i <- 0 to conf.CMAC_ATOMC-1){
        when(io.wt_actv_pvld(i)&io.wt_actv_nz(i)&io.dat_actv_pvld(i)&io.dat_actv_nz(i)){                       
             mout(i) := io.wt_actv_data(i)*io.dat_actv_data(i)
        }
    }  

    val sum_out = mout.reduce(_+&_)
    
    //add retiming
    val pp_pvld_d0 = io.dat_actv_pvld(0)&io.wt_actv_pvld(0)

    io.mac_out_data := ShiftRegister(sum_out, conf.CMAC_OUT_RETIMING, pp_pvld_d0)
    io.mac_out_pvld := ShiftRegister(pp_pvld_d0, conf.CMAC_OUT_RETIMING, pp_pvld_d0)

    }

    val mac = withClock(internal_clock){new macImpl} 

}

object NV_NVDLA_CMAC_CORE_macDriver extends App {
  implicit val conf: cmacConfiguration = new cmacConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_CMAC_CORE_mac(useRealClock = true))
}
