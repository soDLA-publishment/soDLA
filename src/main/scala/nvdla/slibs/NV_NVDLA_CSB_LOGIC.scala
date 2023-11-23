package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CSB_LOGIC(io_reg_rd_en:Boolean = false, nposted:Boolean = false) extends Module {
    val io = IO(new Bundle {
        val clk = Input(Clock()) 
        val csb2dp = new csb2dp_if
        val reg = Flipped(new reg_control_if)
        val reg_rd_en = if(io_reg_rd_en) Some(Output(Bool())) else None
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
    

    if(!nposted){
        //Address in CSB master is word aligned while address in regfile is byte aligned.
        io.reg.offset := Cat(req_addr, "b0".asUInt(2.W))
        io.reg.wr_data := req_wdat
        io.reg.wr_en := req_pvld & req_write
        val reg_rd_en = req_pvld & ~req_write
        if(io_reg_rd_en){
            io.reg_rd_en.get := reg_rd_en
        }

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
    }
    else{
        val rsp_rd_vld = req_pvld & ~req_write
        val rsp_wr_vld = req_pvld & req_write & req_nposted

        val rsp_rd_rdat = Fill(32, rsp_rd_vld) & io.reg.rd_data
        val rsp_wr_rdat = "b0".asUInt(32.W)
        
        //response
        // packet=dla_xx2csb_rd_erpt
        val rsp_rd = Cat(false.B, rsp_rd_rdat)
        // packet=dla_xx2csb_wr_erpt
        val rsp_wr = Cat(false.B, "b0".asUInt(32.W))

        //request
        val rsp_vld = rsp_wr_vld | rsp_rd_vld
        val rsp_pd = Cat(rsp_wr_vld, (Fill(33, rsp_rd_vld)&rsp_rd)|(Fill(33, rsp_wr_vld)&rsp_wr))

        val csb2dp_resp_pd_out = RegInit("b0".asUInt(34.W))
        val csb2dp_resp_valid_out = RegInit(false.B)

        csb2dp_resp_valid_out := rsp_vld
        when(rsp_vld){
            csb2dp_resp_pd_out := rsp_pd
        }

        io.reg.offset := Cat(req_addr, "b0".asUInt(2.W))
        io.reg.wr_data := req_wdat
        io.reg.wr_en := req_pvld & req_write
        val reg_rd_en = req_pvld & ~req_write
        if(io_reg_rd_en){
            io.reg_rd_en.get := reg_rd_en
        }
        io.csb2dp.resp.bits := csb2dp_resp_pd_out
        io.csb2dp.resp.valid := csb2dp_resp_valid_out
    }
}}



object NV_NVDLA_CSB_LOGICDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CSB_LOGIC(nposted = true))
}
