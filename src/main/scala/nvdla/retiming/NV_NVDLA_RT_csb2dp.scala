package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_RT_dp2csb(latency: Int) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //mac2accu
        val dp2csb_src = Flipped(new csb2dp_if)
        val dp2csb_dst = new csb2dp_if
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
    val csb2dp_req_valid_rt_d = Wire(Bool()) +: 
            Seq.fill(latency)(RegInit(false.B))
    val csb2dp_req_bits_rt_d = Wire(UInt(63.W)) +: 
            Seq.fill(latency)(Reg(UInt(63.W)))
    val csb2dp_resp_valid_rt_d = Wire(Bool()) +: 
            Seq.fill(latency)(RegInit(false.B))
    val csb2dp_resp_bits_rt_d = Wire(UInt(34.W)) +: 
            Seq.fill(latency)(Reg(UInt(34.W)))

    //delay inpute
    csb2dp_req_valid_rt_d(0) := io.dp2csb_dst.req.valid
    csb2dp_req_bits_rt_d(0) := io.dp2csb_dst.req.bits
    csb2dp_resp_valid_rt_d(0) := io.dp2csb_src.resp.valid
    csb2dp_resp_bits_rt_d(0) := io.dp2csb_src.resp.bits

    //passing logic
    for(t <- 0 to latency-1){
        csb2dp_req_valid_rt_d(t+1) := csb2dp_req_valid_rt_d(t)
        csb2dp_resp_valid_rt_d(t+1) := csb2dp_resp_valid_rt_d(t)
        when(csb2dp_req_valid_rt_d(t)){
            csb2dp_req_bits_rt_d(t+1) := csb2dp_req_bits_rt_d(t)
        }
        when(csb2dp_resp_valid_rt_d(t)){
            csb2dp_resp_bits_rt_d(t+1) := csb2dp_resp_bits_rt_d(t)
        }
    } 

    //output assignment
    csb2dp_req_bits_rt_d(0) := io.dp2csb_dst.req.bits
    csb2dp_resp_valid_rt_d(0) := io.dp2csb_src.resp.valid
    csb2dp_resp_bits_rt_d(0) := io.dp2csb_src.resp.bits


    io.dp2csb_src.req.valid := csb2dp_req_valid_rt_d(latency) 
    io.dp2csb_src.req.bits := csb2dp_req_bits_rt_d(latency)
    io.dp2csb_dst.resp.valid := csb2dp_resp_valid_rt_d(latency)
    io.dp2csb_dst.resp.bits := csb2dp_resp_bits_rt_d(latency)

}}

object NV_NVDLA_RT_dp2csbDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_RT_dp2csb(latency = 2))
}

    
    

    



    















    



 

