    
package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

//this module is to mac dat and wt

class NV_NVDLA_CMAC_CORE_mac(useRealClock:Boolean = false)(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())

        //wt and dat
        val dat_actv = Vec(conf.CMAC_ATOMC, Flipped(ValidIO(new cmac_core_actv)))
        val wt_actv = Vec(conf.CMAC_ATOMC, Flipped(ValidIO(new cmac_core_actv)))

        //output
        val mac_out = ValidIO(UInt(conf.CMAC_RESULT_WIDTH.W))      
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

    val mout = VecInit(Seq.fill(conf.CMAC_ATOMC)(0.asSInt((2*conf.CMAC_BPE).W)))

    for(i <- 0 to conf.CMAC_ATOMC-1){
        when(io.wt_actv(i).valid&io.wt_actv(i).bits.nz&io.dat_actv(i).valid&io.dat_actv(i).bits.nz){                       
             mout(i) := io.wt_actv(i).bits.data.asSInt*io.dat_actv(i).bits.data.asSInt
        }
    }  

    val sum_out = mout.reduce(_+&_).asUInt
    
    //add retiming
    val pp_pvld_d0 = io.dat_actv(0).valid&io.wt_actv(0).valid

    io.mac_out.bits := ShiftRegister(sum_out, conf.CMAC_OUT_RETIMING, pp_pvld_d0)
    io.mac_out.valid := ShiftRegister(pp_pvld_d0, conf.CMAC_OUT_RETIMING, pp_pvld_d0)

    }

    val mac = withClock(internal_clock){new macImpl} 

}

object NV_NVDLA_CMAC_CORE_macDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CMAC_CORE_mac(useRealClock = true))
}