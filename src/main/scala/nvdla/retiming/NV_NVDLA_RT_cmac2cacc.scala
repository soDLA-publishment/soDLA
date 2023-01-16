package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_RT_cmac2cacc(latency: Int)(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //mac2accu
        val mac2accu_src = Flipped(ValidIO(new cmac2cacc_if)) /* data valid */
        val mac2accu_dst = ValidIO(new cmac2cacc_if)
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
    //==========================================================
    // Output retiming
    //==========================================================
    //initial value
    val out_rt_pvld_d = Wire(Bool()) +: 
                        Seq.fill(latency)(RegInit(false.B))
    val out_rt_mask_d = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool())) +: 
                        Seq.fill(latency)(RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(false.B)))) 
    val out_rt_pd_d = Wire(UInt(9.W)) +: 
                      Seq.fill(latency)(RegInit("b0".asUInt(9.W))) 
    val out_rt_data_d = retiming(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)), latency)

    //delay input
    out_rt_pvld_d(0) := io.mac2accu_src.valid
    out_rt_mask_d(0) := io.mac2accu_src.bits.mask
    out_rt_pd_d(0) := io.mac2accu_src.bits.pd
    out_rt_data_d(0) := io.mac2accu_src.bits.data

    //passing logic
    for(t <- 0 to latency-1){
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
    io.mac2accu_dst.valid := out_rt_pvld_d(latency)
    io.mac2accu_dst.bits.mask := out_rt_mask_d(latency)
    io.mac2accu_dst.bits.pd := out_rt_pd_d(latency)
    io.mac2accu_dst.bits.data := out_rt_data_d(latency)

}}

object NV_NVDLA_RT_cmac2caccDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_RT_cmac2cacc(latency = 2))
}

    
    

    



    















    



 

