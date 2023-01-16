package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_RT_cacc2glb(latency: Int) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //cacc2glb
        val cacc2glb_done_intr_src_pd = Input(UInt(2.W))
        val cacc2glb_done_intr_dst_pd = Output(UInt(2.W))
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
    val cacc2glb_done_intr_pd_d = Wire(UInt(2.W)) +: 
        Seq.fill(latency)(RegInit("b0".asUInt(2.W)))

    //delay input
    cacc2glb_done_intr_pd_d(0) := io.cacc2glb_done_intr_src_pd
    //passing logic
    for(t <- 0 to latency-1){
        cacc2glb_done_intr_pd_d(t+1) := cacc2glb_done_intr_pd_d(t)  
    } 

    //assign output
    io.cacc2glb_done_intr_dst_pd := cacc2glb_done_intr_pd_d(latency)
}}

object NV_NVDLA_RT_cacc2glbDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_RT_cacc2glb(latency = 2))
}

    
    

    



    















    



 

