package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//bubble collapse
class NV_NVDLA_CSB_LOGIC extends Module {
    val io = IO(new Bundle {

        val clk = Input(Clock()) 
        val csb2dp = new csb2dp_if
        val reg = Flipped(new reg_control_if)

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
withClock(io.clk){   
    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    val req_pvld = RegInit(false.B)
    val req_pd = RegInit("b0".asUInt(63.W))

    req_pvld := io.csb2dp.req.valid
    when(io.csb2dp.req.valid){
        req_pd := io.csb2dp.req.bits
    }

    // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  req_pd ) 
    val req_addr = req_pd(21, 0)
    val req_wdat = req_pd(53, 22)
    val req_write = req_pd(54)
    val req_nposted = req_pd(55)
    val req_srcpriv = req_pd(56)
    val req_wrbe = req_pd(60, 57)
    val req_level = req_pd(62, 61)

    io.csb2dp.req.ready := true.B

    //Address in CSB master is word aligned while address in regfile is byte aligned.
    io.reg.offset := Cat(req_addr, "b0".asUInt(2.W))
    io.reg.wr_data := req_wdat
    io.reg.wr_en := req_pvld & req_write
    val reg_rd_en = req_pvld & ~req_write

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_rd_erpt ,  csb_rresp_ ,  csb_rresp_pd_w )
    val csb_rresp_rdat = io.reg.rd_data
    val csb_rresp_error = false.B
    val csb_rresp_pd_w = Cat(false.B, csb_rresp_error, csb_rresp_rdat)

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_wr_erpt ,  csb_wresp_ ,  csb_wresp_pd_w 
    val csb_wresp_rdat = "b0".asUInt(32.W)
    val csb_wresp_error = false.B
    val csb_wresp_pd_w = Cat(true.B, csb_wresp_error, csb_wresp_rdat)

    val csb2dp_resp_pd_out = RegInit("b0".asUInt(34.W))
    val csb2dp_resp_valid_out = RegInit(false.B)

    when(reg_rd_en){
        csb2dp_resp_pd_out := csb_rresp_pd_w
    }
    .elsewhen(io.reg.wr_en & req_nposted){
        csb2dp_resp_pd_out := csb_wresp_pd_w
    }
    csb2dp_resp_valid_out := (io.reg.wr_en & req_nposted) | reg_rd_en

    io.csb2dp.resp.bits := csb2dp_resp_pd_out
    io.csb2dp.resp.valid := csb2dp_resp_valid_out
}}