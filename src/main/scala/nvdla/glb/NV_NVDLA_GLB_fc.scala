package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_GLB_fc(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())
          
        //csb2gec
        val csb2gec = new csb2dp_if
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
    
    io.csb2gec.req.ready := true.B
    val req_pvld = io.csb2gec.req.valid
    val rresp_rdat = "b0".asUInt(32.W)
    val wresp_rdat = "b0".asUInt(32.W)
    val rresp_error = false.B
    val wresp_error = false.B

    // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  csb2gec_req_pd )
    val req_addr = io.csb2gec.req.bits(21, 0)
    val req_wdat = io.csb2gec.req.bits(53, 22)
    val req_write = io.csb2gec.req.bits(54)
    val req_nposted = io.csb2gec.req.bits(55)
    val req_srcpriv = io.csb2gec.req.bits(56)
    val req_wrbe = io.csb2gec.req.bits(60, 57)
    val req_level = io.csb2gec.req.bits(62, 61)

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_rd_erpt ,  rresp_ ,  rresp_pd_w )
    val rresp_pd_w = Cat(false.B, rresp_error, rresp_rdat)  /* PKT_nvdla_xx2csb_resp_dla_xx2csb_rd_erpt_ID  */ ;

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_wr_erpt ,  wresp_ ,  wresp_pd_w )
    val wresp_pd_w = Cat(true.B, wresp_error, wresp_rdat)

    val wresp_en = req_pvld & req_write & req_nposted;
    val rresp_en = req_pvld & ~req_write;
    val resp_pd_w = Mux(wresp_en,  wresp_pd_w,  rresp_pd_w)
    val resp_en = wresp_en | rresp_en;

    io.csb2gec.resp.valid := RegNext(resp_en)
    io.csb2gec.resp.bits := RegEnable(resp_pd_w, "b0".asUInt(34.W), resp_en)
}}

    
    

    



    















    



 

