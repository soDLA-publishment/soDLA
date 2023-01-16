    
package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

//this module is to mac dat and wt
@chiselName
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
withClockAndReset(io.nvdla_core_clk, ~io.nvdla_core_rstn){

    val mout = VecInit(Seq.fill(conf.CMAC_ATOMC)(0.asSInt((conf.CMAC_RESULT_WIDTH-1).W)))
    val op_out_pvld = Wire(Vec(conf.CMAC_ATOMC, Bool()))
     
    for(i <- 0 to conf.CMAC_ATOMC-1){
        // when(io.wt_actv(i).valid&io.wt_actv(i).bits.nz&io.dat_actv(i).valid&io.dat_actv(i).bits.nz){                       
        //      mout(i) := io.wt_actv(i).bits.data.asSInt*io.dat_actv(i).bits.data.asSInt
        // }
        op_out_pvld(i) := io.wt_actv(i).valid & io.wt_actv(i).bits.nz & io.dat_actv(i).valid & io.dat_actv(i).bits.nz
        mout(i) := (io.wt_actv(i).bits.data.asSInt*io.dat_actv(i).bits.data.asSInt) &  Fill(conf.CMAC_RESULT_WIDTH-1, op_out_pvld(i)).asSInt
    }  

    val sum_out = Wire(UInt(conf.CMAC_RESULT_WIDTH.W))
    sum_out := mout.reduce(_+&_).asUInt
    
    //add retiming
    val pp_pvld_d0 = io.dat_actv(0).valid&io.wt_actv(0).valid

    val pp_pvld_d = Seq.fill(conf.CMAC_OUT_RETIMING)(RegInit(false.B))
    val sum_out_d = Seq.fill(conf.CMAC_OUT_RETIMING)(RegInit("b0".asUInt(conf.CMAC_RESULT_WIDTH.W)))

    pp_pvld_d(0) := pp_pvld_d0
    sum_out_d(0) := sum_out
    for(t <- 0 to conf.CMAC_OUT_RETIMING-2){
        pp_pvld_d(t+1) := pp_pvld_d(t)
        when(pp_pvld_d(t)){
            sum_out_d(t+1) := sum_out_d(t)
        }
    }

    io.mac_out.bits := sum_out_d(conf.CMAC_OUT_RETIMING-1)
    io.mac_out.valid := pp_pvld_d(conf.CMAC_OUT_RETIMING-1)
}}

object NV_NVDLA_CMAC_CORE_macDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CMAC_CORE_mac)
}