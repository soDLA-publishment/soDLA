package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_RT_csb2dp(delay: Int)(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        val csb2dp_src = new csb2dp_if
        val csb2dp_dst = Flipped(new csb2dp_if)
    })
withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){

    val csb2dp_req_pvld_d = Wire(Bool()) +: Seq.fill(delay)(RegInit(false.B))
    val csb2dp_req_pd_d = Wire(UInt(63.W)) +: Seq.fill(delay)(Reg(UInt(63.W)))
    val dp2csb_resp_valid_d = Wire(Bool()) +: Seq.fill(delay)(RegInit(false.B))
    val dp2csb_resp_pd_d  = Wire(UInt(34.W)) +: Seq.fill(delay)(Reg(UInt(34.W))) 

    //initial condition  
    csb2dp_req_pvld_d(0) := io.csb2dp_src.req.valid
    csb2dp_req_pd_d(0) := io.csb2dp_src.req.bits

    dp2csb_resp_valid_d(0) := io.csb2dp_src.resp.valid
    dp2csb_resp_pd_d(0) := io.csb2dp_src.resp.bits


    for(t <- 0 to (delay-1)){
        csb2dp_req_pvld_d(t+1) := csb2dp_req_pvld_d(t)
        dp2csb_resp_valid_d(t+1):= dp2csb_resp_valid_d(t)
    } 

    for(t <- 0 to (delay-1)){
        csb2dp_req_pd_d(t+1) := csb2dp_req_pd_d(t)
        dp2csb_resp_pd_d(t+1) := dp2csb_resp_pd_d(t)
    }  
        
    //output assignment
    io.csb2dp_dst.req.valid := csb2dp_req_pvld_d(delay)
    io.csb2dp_dst.req.bits := csb2dp_req_pd_d(delay)
    io.csb2dp_dst.resp.valid := dp2csb_resp_valid_d(delay)
    io.csb2dp_dst.resp.bits := dp2csb_resp_pd_d(delay)


}}