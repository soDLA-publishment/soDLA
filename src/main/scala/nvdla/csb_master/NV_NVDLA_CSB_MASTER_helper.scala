package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver



class NV_NVDLA_CSB_MASTER_for_client(address_space: String)(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val core_req_pop_valid = Input(Bool())
        val core_byte_addr = Input(UInt(18.W))
        val addr_mask = Input(UInt(18.W))
        val core_req_pd_d1 = Input(UInt(50.W))

        val csb2client = Flipped(new csb2dp_if)
        val client_resp_pd = ValidIO(UInt(50.W))
        val select_client = Output(Bool())

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
withClock(io.nvdla_core_clk){

    val client_req_pvld = RegInit(false.B)
    val csb2client_req_pvld_out = RegInit(false.B)
    val csb2client_req_pd_tmp = if(!conf.REGINIT_DATA) Reg(UInt(50.W)) else RegInit("b0".asUInt(50.W))
    val client_resp_valid_out = RegInit(false.B)
    val client_resp_pd_out = if(!conf.REGINIT_DATA) Reg(UInt(34.W)) else RegInit("b0".asUInt(34.W))

    io.select_client := ((io.core_byte_addr & io.addr_mask) === address_space.asUInt(32.W))
    val client_req_pvld_w = Mux(io.core_req_pop_valid & io.select_client, true.B,
                            false.B)
    val csb2client_req_pvld_w = client_req_pvld
    val csb2client_req_en = client_req_pvld 

    client_req_pvld := client_req_pvld_w
    csb2client_req_pvld_out := csb2client_req_pvld_w
    when(csb2client_req_en){
        csb2client_req_pd_tmp := io.core_req_pd_d1
    }
    io.csb2client.req.bits := Cat("b0".asUInt(7.W), csb2client_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2client_req_pd_tmp(15, 0))
    client_resp_valid_out := io.csb2client.resp.valid
    when(io.csb2client.resp.valid){
        client_resp_pd_out := io.csb2client.resp.bits
    }

    io.csb2client.req.valid := csb2client_req_pvld_out
    io.client_resp_pd.valid := client_resp_valid_out
    io.client_resp_pd.bits := client_resp_pd_out

}}
