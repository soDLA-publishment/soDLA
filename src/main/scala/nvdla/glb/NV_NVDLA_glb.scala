package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_glb(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_falcon_clk = Input(Clock())

        val csb2glb_req_pvld = Input(Bool())  /* data valid */
        val csb2glb_req_prdy = Output(Bool())  /* data return handshake */
        val csb2glb_req_pd = Input(UInt(63.W))

        val glb2csb_resp_valid = Output(Bool())  /* data valid */
        val glb2csb_resp_pd = Output(UInt(34.W))     /* pkt_id_width=1 pkt_widths=33,33  */

        val core_intr = Output(Bool())

        //bdma
        val bdma2glb_done_intr_pd = if(conf.NVDLA_BDMA_ENABLE) Some(Input(UInt(2.W))) else None
        //cdp
        val cdp2glb_done_intr_pd = if(conf.NVDLA_CDP_ENABLE) Some(Input(UInt(2.W))) else None   
        //pdp
        val pdp2glb_done_intr_pd = if(conf.NVDLA_PDP_ENABLE) Some(Input(UInt(2.W))) else None   
        //rubik
        val rubik2glb_done_intr_pd = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(UInt(2.W))) else None            
        //cacc
        val cacc2glb_done_intr_pd = Input(UInt(2.W))
        //cdma
        val cdma_dat2glb_done_intr_pd = Input(UInt(2.W))
        val cdma_wt2glb_done_intr_pd = Input(UInt(2.W))
        //sdp
        val sdp2glb_done_intr_pd = Input(UInt(2.W))
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

    val u_csb = Module(new NV_NVDLA_GLB_csb)
    val u_ic = Module(new NV_NVDLA_GLB_ic)

    u_csb.io.nvdla_core_clk := io.nvdla_core_clk
    u_ic.io.nvdla_core_clk := io.nvdla_core_clk
    u_ic.io.nvdla_falcon_clk := io.nvdla_falcon_clk

    if(conf.NVDLA_BDMA_ENABLE){
        u_csb.io.bdma_done_status0.get := u_ic.io.bdma_done_status0.get
        u_csb.io.bdma_done_status1.get := u_ic.io.bdma_done_status1.get
        u_ic.io.bdma_done_mask0.get := u_csb.io.bdma_done_mask0.get
        u_ic.io.bdma_done_mask1.get := u_csb.io.bdma_done_mask1.get
        u_ic.io.bdma2glb_done_intr_pd.get := io.bdma2glb_done_intr_pd.get
    }
    if(conf.NVDLA_CDP_ENABLE){
        u_csb.io.cdp_done_status0.get := u_ic.io.cdp_done_status0.get
        u_csb.io.cdp_done_status1.get := u_ic.io.cdp_done_status1.get
        u_ic.io.cdp_done_mask0.get := u_csb.io.cdp_done_mask0.get
        u_ic.io.cdp_done_mask1.get := u_csb.io.cdp_done_mask1.get
        u_ic.io.cdp2glb_done_intr_pd.get := io.cdp2glb_done_intr_pd.get
    }    
    if(conf.NVDLA_PDP_ENABLE){
        u_csb.io.pdp_done_status0.get := u_ic.io.pdp_done_status0.get
        u_csb.io.pdp_done_status1.get := u_ic.io.pdp_done_status1.get
        u_ic.io.pdp_done_mask0.get := u_csb.io.pdp_done_mask0.get
        u_ic.io.pdp_done_mask1.get := u_csb.io.pdp_done_mask1.get
        u_ic.io.pdp2glb_done_intr_pd.get := io.pdp2glb_done_intr_pd.get
    }
    if(conf.NVDLA_RUBIK_ENABLE){
        u_csb.io.rubik_done_status0.get := u_ic.io.rubik_done_status0.get
        u_csb.io.rubik_done_status1.get := u_ic.io.rubik_done_status1.get
        u_ic.io.rubik_done_mask0.get := u_csb.io.rubik_done_mask0.get
        u_ic.io.rubik_done_mask1.get := u_csb.io.rubik_done_mask1.get
        u_ic.io.rubik2glb_done_intr_pd.get := io.rubik2glb_done_intr_pd.get
    }   
    u_csb.io.cacc_done_status0 := u_ic.io.cacc_done_status0
    u_csb.io.cacc_done_status1 := u_ic.io.cacc_done_status1
    u_csb.io.cdma_dat_done_status0 := u_ic.io.cdma_dat_done_status0
    u_csb.io.cdma_dat_done_status1 := u_ic.io.cdma_dat_done_status1
    u_csb.io.cdma_wt_done_status0 := u_ic.io.cdma_wt_done_status0
    u_csb.io.cdma_wt_done_status1 := u_ic.io.cdma_wt_done_status1
    u_csb.io.sdp_done_status0 := u_ic.io.sdp_done_status0
    u_csb.io.sdp_done_status1 := u_ic.io.sdp_done_status1

    u_ic.io.cacc_done_mask0 := u_csb.io.cacc_done_mask0
    u_ic.io.cacc_done_mask1 := u_csb.io.cacc_done_mask1
    u_ic.io.cdma_dat_done_mask0 := u_csb.io.cdma_dat_done_mask0   
    u_ic.io.cdma_dat_done_mask1 := u_csb.io.cdma_dat_done_mask1 
    u_ic.io.cdma_wt_done_mask0 := u_csb.io.cdma_wt_done_mask0 
    u_ic.io.cdma_wt_done_mask1 := u_csb.io.cdma_wt_done_mask1

    u_ic.io.sdp_done_mask0 := u_csb.io.sdp_done_mask0
    u_ic.io.sdp_done_mask1 := u_csb.io.sdp_done_mask1
    u_ic.io.sdp_done_set0_trigger := u_csb.io.sdp_done_set0_trigger  
    u_ic.io.sdp_done_status0_trigger := u_csb.io.sdp_done_status0_trigger 
    u_ic.io.req_wdat := u_csb.io.req_wdat


    io.csb2glb_req_prdy := u_csb.io.csb2glb_req_prdy
    io.glb2csb_resp_pd := u_csb.io.glb2csb_resp_pd
    io.glb2csb_resp_valid := u_csb.io.glb2csb_resp_valid

    u_csb.io.csb2glb_req_pd := io.csb2glb_req_pd
    u_csb.io.csb2glb_req_pvld := io.csb2glb_req_pvld

    u_ic.io.cacc2glb_done_intr_pd := io.cacc2glb_done_intr_pd
    u_ic.io.cdma_dat2glb_done_intr_pd := io.cdma_dat2glb_done_intr_pd
    u_ic.io.cdma_wt2glb_done_intr_pd := io.cdma_wt2glb_done_intr_pd
    u_ic.io.sdp2glb_done_intr_pd := io.sdp2glb_done_intr_pd

    io.core_intr := u_ic.io.core_intr


}

object NV_NVDLA_glbDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_glb())
}
    
    

    



    















    



 

