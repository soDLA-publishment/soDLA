    
package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

//this module is to mac dat and wt

class NV_NVDLA_CMAC_CORE_mac(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

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
withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

    val mout = VecInit(Seq.fill(conf.CMAC_ATOMC)(0.asSInt((2*conf.CMAC_BPE).W)))

    for(i <- 0 to conf.CMAC_ATOMC-1){
        when(io.wt_actv(i).valid&io.wt_actv(i).bits.nz&io.dat_actv(i).valid&io.dat_actv(i).bits.nz){                       
             mout(i) := io.wt_actv(i).bits.data.asSInt*io.dat_actv(i).bits.data.asSInt
        }
    }  

    val sum_out = mout.reduce(_+&_).asUInt
    
    //add retiming
    val pp_pvld_d0 = io.dat_actv(0).valid&io.wt_actv(0).valid

    val pp_pvld_d = Wire(Bool()) +: 
                    Seq.fill(conf.CMAC_OUT_RETIMING)(RegInit(false.B))
    val sum_out_d = retiming(UInt(conf.CMAC_RESULT_WIDTH.W), conf.CMAC_OUT_RETIMING)

    for(t <- 0 to conf.CMAC_OUT_RETIMING-1){
        pp_pvld_d(t+1) := pp_pvld_d(t)
        when(pp_pvld_d(t)){
            sum_out_d(t+1) := sum_out_d(t)
        }
    }

    io.mac_out.bits := pp_pvld_d(conf.CMAC_OUT_RETIMING)
    io.mac_out.valid := sum_out_d(conf.CMAC_OUT_RETIMING)


}}

object NV_NVDLA_CMAC_CORE_macDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CMAC_CORE_mac)
}