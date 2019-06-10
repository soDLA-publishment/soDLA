package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_CDMA_dma_mux(implicit conf: cdmaConfiguration) extends Module {

    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())
        //dc_dat2cvif
        val dc_dat2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val dc_dat2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val dc_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2dc_dat_rd_rsp_valid  = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2dc_dat_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cvif2dc_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None
            
        //img2cvif
        val img_dat2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val img_dat2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val img_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2img_dat_rd_rsp_valid  = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2img_dat_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cvif2img_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None

        //cdma_dat2cvif
        val cdma_dat2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cdma_dat2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cdma_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2cdma_dat_rd_rsp_valid  = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cvif2cdma_dat_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2cdma_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None

        //dc_dat2mcif
        val dc_dat2mcif_rd_req_valid = Input(Bool())
        val dc_dat2mcif_rd_req_ready = Output(Bool())
        val dc_dat2mcif_rd_req_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2dc_dat_rd_rsp_valid = Output(Bool())
        val mcif2dc_dat_rd_rsp_ready = Input(Bool())
        val mcif2dc_dat_rd_rsp_pd = Output(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))

        //img_dat2mcif
        val img_dat2mcif_rd_req_valid = Input(Bool())
        val img_dat2mcif_rd_req_ready = Output(Bool())
        val img_dat2mcif_rd_req_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2img_dat_rd_rsp_valid = Output(Bool())
        val mcif2img_dat_rd_rsp_ready = Input(Bool())
        val mcif2img_dat_rd_rsp_pd = Output(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))        

        //cdma_dat2mcif
        val cdma_dat2mcif_rd_req_valid = Output(Bool())
        val cdma_dat2mcif_rd_req_ready = Input(Bool())
        val cdma_dat2mcif_rd_req_pd = Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2cdma_dat_rd_rsp_valid = Input(Bool())
        val mcif2cdma_dat_rd_rsp_ready = Output(Bool())
        val mcif2cdma_dat_rd_rsp_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))    
    })
    
    //      ┌─┐       ┌─┐
    //   ┌──┘ ┴───────┘ ┴──┐
    //   │                 │
    //   │       ───       │
    //   │  ─┬┘       └┬─  │
    //   │                 │
    //   │       ─┴─       │
    //   │                 │
    //   └───┐         ┌───┘
    //       │         │    オランダ
    //       │         │
    //       │         │
    //       │         └──────────────┐
    //       │                        │
    //       │                        ├─┐
    //       │                        ┌─┘    
    //       │                        │
    //       └─┐  ┐  ┌───────┬──┐  ┌──┘         
    //         │ ─┤ ─┤       │ ─┤ ─┤         
    //         └──┴──┘       └──┴──┘ 
withClock(io.nvdla_core_clk){

////////////////////////////////////////////////////////////////////////
// Data request channel                                               //
////////////////////////////////////////////////////////////////////////
//////////////// MCIF interface ////////////////
    val mc_sel_dc_w = io.dc_dat2mcif_rd_req_valid
    val mc_sel_img_w = io.img_dat2mcif_rd_req_valid
    val req_mc_in_pvld = io.dc_dat2mcif_rd_req_valid|io.img_dat2mcif_rd_req_valid
    val req_mc_in_pd = (Fill(conf.NVDLA_CDMA_MEM_RD_REQ, mc_sel_dc_w)&io.dc_dat2mcif_rd_req_pd)|
                       (Fill(conf.NVDLA_CDMA_MEM_RD_REQ, mc_sel_img_w)&io.img_dat2mcif_rd_req_pd)
    
    
    //pipe skid buffer
    //reg
    val req_mc_in_prdy = RegInit(true.B)
    val req_mc_out_prdy = Wire(Bool())
    val skid_flop_req_mc_in_prdy = RegInit(true.B)
    val skid_flop_req_mc_in_pvld = RegInit(false.B)
    val skid_flop_req_mc_in_pd = Reg(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val pipe_skid_req_mc_in_pvld = RegInit(false.B)
    val pipe_skid_req_mc_in_pd = Reg(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    //Wire
    val skid_req_mc_in_pvld = Wire(Bool())
    val skid_req_mc_in_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val skid_req_mc_in_prdy = Wire(Bool())
    val pipe_skid_req_mc_in_prdy = Wire(Bool())
    val req_mc_out_pvld = Wire(Bool())
    val req_mc_out_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    //skid ready
    req_mc_in_prdy := skid_req_mc_in_prdy
    skid_flop_req_mc_in_prdy := skid_req_mc_in_prdy
    //skid valid
    when(skid_flop_req_mc_in_prdy){
        skid_flop_req_mc_in_pvld := req_mc_in_pvld
    }
    skid_req_mc_in_pvld := Mux(skid_flop_req_mc_in_prdy, req_mc_in_pvld, skid_flop_req_mc_in_pvld)
    //skid data
    when(skid_flop_req_mc_in_prdy & req_mc_in_pvld){
        skid_flop_req_mc_in_pd := req_mc_in_pd
    }
    skid_req_mc_in_pd := Mux(skid_flop_req_mc_in_prdy, req_mc_in_pd, skid_flop_req_mc_in_pd)
    //pipe ready
    skid_req_mc_in_prdy := pipe_skid_req_mc_in_prdy || !pipe_skid_req_mc_in_pvld
    //pipe valid
    when(skid_req_mc_in_prdy){
        pipe_skid_req_mc_in_pvld := skid_req_mc_in_pvld
    }
    //pipe data
    when(skid_req_mc_in_prdy && skid_req_mc_in_pvld){
        pipe_skid_req_mc_in_pd := skid_req_mc_in_pd
    }
    //pipe output
    pipe_skid_req_mc_in_prdy := req_mc_out_prdy
    req_mc_out_pvld := pipe_skid_req_mc_in_pvld
    req_mc_out_pd := pipe_skid_req_mc_in_pd

    io.dc_dat2mcif_rd_req_ready := req_mc_in_prdy & io.dc_dat2mcif_rd_req_valid
    io.img_dat2mcif_rd_req_ready := req_mc_in_prdy & io.img_dat2mcif_rd_req_valid
    io.cdma_dat2mcif_rd_req_valid := req_mc_out_pvld
    io.cdma_dat2mcif_rd_req_pd := req_mc_out_pd
    req_mc_out_prdy := io.cdma_dat2mcif_rd_req_ready

    val mc_sel_dc = RegEnable(mc_sel_dc_w, false.B, req_mc_in_pvld & req_mc_in_prdy)
    val mc_sel_img = RegEnable(mc_sel_img_w, false.B, req_mc_in_pvld & req_mc_in_prdy)

////////////////////////////////////////////////////////////////////////
// Data response channel                                              //
////////////////////////////////////////////////////////////////////////
//////////////// MCIF interface ////////////////
    val rsp_mc_in_pvld = io.mcif2cdma_dat_rd_rsp_valid
    val rsp_mc_in_pd = io.mcif2cdma_dat_rd_rsp_pd
    //pipe  skid buffer
    //reg
    val rsp_mc_in_prdy = RegInit(true.B)
    val rsp_mc_out_prdy = Wire(Bool())
    val skid_flop_rsp_mc_in_prdy = RegInit(true.B)
    val skid_flop_rsp_mc_in_pvld = RegInit(false.B)
    val skid_flop_rsp_mc_in_pd = Reg(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    val pipe_skid_rsp_mc_in_pvld = RegInit(false.B)
    val pipe_skid_rsp_mc_in_pd = Reg(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    //Wire
    val skid_rsp_mc_in_pvld = Wire(Bool())
    val skid_rsp_mc_in_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    val skid_rsp_mc_in_prdy = WireBool())
    val pipe_skid_rsp_mc_in_prdy = Wire(Bool())
    val rsp_mc_out_pvld = Wire(Bool())
    val rsp_mc_out_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    //skid ready
    rsp_mc_in_prdy := skid_rsp_mc_in_prdy
    skid_flop_rsp_mc_in_prdy := skid_rsp_mc_in_prdy
    //skid valid
    when(skid_flop_rsp_mc_in_prdy){
        skid_flop_rsp_mc_in_pvld := rsp_mc_in_pvld
    }
    skid_rsp_mc_in_pvld := Mux(skid_flop_rsp_mc_in_prdy, rsp_mc_in_pvld, skid_flop_rsp_mc_in_pvld)
    //skid data
    when(skid_flop_rsp_mc_in_prdy & rsp_mc_in_pvld){
        skid_flop_rsp_mc_in_pd := rsp_mc_in_pd
    }
    skid_rsp_mc_in_pd := Mux(skid_flop_rsp_mc_in_prdy, rsp_mc_in_pd, skid_flop_rsp_mc_in_pd)
    //pipe ready
    skid_rsp_mc_in_prdy := pipe_skid_rsp_mc_in_prdy || !pipe_skid_rsp_mc_in_pvld
    //pipe valid
    when(skid_rsp_mc_in_prdy){
        pipe_skid_rsp_mc_in_pvld := skid_rsp_mc_in_pvld
    }
    //pipe data
    when(skid_rsp_mc_in_prdy && skid_rsp_mc_in_pvld){
        pipe_skid_rsp_mc_in_pd := skid_rsp_mc_in_pd
    }
    //pipe output
    pipe_skid_rsp_mc_in_prdy := rsp_mc_out_prdy
    rsp_mc_out_pvld := pipe_skid_rsp_mc_in_pvld
    rsp_mc_out_pd := pipe_skid_rsp_mc_in_pd

    io.mcif2cdma_dat_rd_rsp_ready := rsp_mc_in_prdy
    io.mcif2dc_dat_rd_rsp_valid  := rsp_mc_out_pvld & mc_sel_dc
    io.mcif2img_dat_rd_rsp_valid := rsp_mc_out_pvld & mc_sel_img
    io.mcif2dc_dat_rd_rsp_pd := Fill(conf.NVDLA_CDMA_MEM_RD_RSP, mc_sel_dc) & rsp_mc_out_pd
    io.mcif2img_dat_rd_rsp_pd := Fill(conf.NVDLA_CDMA_MEM_RD_RSP, mc_sel_img) & rsp_mc_out_pd
    rsp_mc_out_prdy := (mc_sel_dc & io.mcif2dc_dat_rd_rsp_ready)|(mc_sel_img & io.mcif2img_dat_rd_rsp_ready)


//////////////// CVIF interface ////////////////

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
////////////////////////////////////////////////////////////////////////
// Data request channel                                               //
////////////////////////////////////////////////////////////////////////
    val cv_sel_dc_w = io.dc_dat2cvif_rd_req_valid.get
    val cv_sel_img_w = io.img_dat2cvif_rd_req_valid.get
    val req_cv_in_pvld = io.dc_dat2cvif_rd_req_valid.get|io.img_dat2cvif_rd_req_valid.get
    val req_cv_in_pd = (Fill(conf.NVDLA_CDMA_MEM_RD_REQ, cv_sel_dc_w)&io.dc_dat2cvif_rd_req_pd.get)|
                       (Fill(conf.NVDLA_CDMA_MEM_RD_REQ, cv_sel_img_w)&io.img_dat2cvif_rd_req_pd.get)
    
    
    //pipe skid buffer
    //reg
    val req_cv_in_prdy = RegInit(true.B)
    val req_cv_out_prdy = Wire(Bool())
    val skid_flop_req_cv_in_prdy = RegInit(true.B)
    val skid_flop_req_cv_in_pvld = RegInit(false.B)
    val skid_flop_req_cv_in_pd = Reg(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val pipe_skid_req_cv_in_pvld = RegInit(false.B)
    val pipe_skid_req_cv_in_pd = Reg(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    //Wire
    val skid_req_cv_in_pvld = Wire(Bool())
    val skid_req_cv_in_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val skid_req_cv_in_prdy = Wire(Bool())
    val pipe_skid_req_cv_in_prdy = Wire(Bool())
    val req_cv_out_pvld = Wire(Bool())
    val req_cv_out_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    //skid ready
    req_cv_in_prdy := skid_req_cv_in_prdy
    skid_flop_req_cv_in_prdy := skid_req_cv_in_prdy
    //skid valid
    when(skid_flop_req_cv_in_prdy){
        skid_flop_req_cv_in_pvld := req_cv_in_pvld
    }
    skid_req_cv_in_pvld := Mux(skid_flop_req_cv_in_prdy, req_cv_in_pvld, skid_flop_req_cv_in_pvld)
    //skid data
    when(skid_flop_req_cv_in_prdy & req_cv_in_pvld){
        skid_flop_req_cv_in_pd := req_cv_in_pd
    }
    skid_req_cv_in_pd := Mux(skid_flop_req_cv_in_prdy, req_cv_in_pd, skid_flop_req_cv_in_pd)
    //pipe ready
    skid_req_cv_in_prdy := pipe_skid_req_cv_in_prdy || !pipe_skid_req_cv_in_pvld
    //pipe valid
    when(skid_req_cv_in_prdy){
        pipe_skid_req_cv_in_pvld := skid_req_cv_in_pvld
    }
    //pipe data
    when(skid_req_cv_in_prdy && skid_req_cv_in_pvld){
        pipe_skid_req_cv_in_pd := skid_req_cv_in_pd
    }
    //pipe output
    pipe_skid_req_cv_in_prdy := req_cv_out_prdy
    req_cv_out_pvld := pipe_skid_req_cv_in_pvld
    req_cv_out_pd := pipe_skid_req_cv_in_pd

    io.dc_dat2cvif_rd_req_ready.get := req_cv_in_prdy & io.dc_dat2cvif_rd_req_valid.get
    io.img_dat2cvif_rd_req_ready.get := req_cv_in_prdy & io.img_dat2cvif_rd_req_valid.get
    io.cdma_dat2cvif_rd_req_valid.get := req_cv_out_pvld
    io.cdma_dat2cvif_rd_req_pd.get := req_cv_out_pd
    req_cv_out_prdy := io.cdma_dat2cvif_rd_req_ready.get

    val cv_sel_dc = RegEnable(cv_sel_dc_w, false.B, req_cv_in_pvld & req_cv_in_prdy)
    val cv_sel_img = RegEnable(cv_sel_img_w, false.B, req_cv_in_pvld & req_cv_in_prdy)

////////////////////////////////////////////////////////////////////////
// Data response channel                                              //
////////////////////////////////////////////////////////////////////////
    val rsp_cv_in_pvld = io.cvif2cdma_dat_rd_rsp_valid.get
    val rsp_cv_in_pd = io.cvif2cdma_dat_rd_rsp_pd.get
    //pipe  skid buffer
    //reg
    val rsp_cv_in_prdy = RegInit(true.B)
    val rsp_cv_out_prdy = Wire(Bool())
    val skid_flop_rsp_cv_in_prdy = RegInit(true.B)
    val skid_flop_rsp_cv_in_pvld = RegInit(false.B)
    val skid_flop_rsp_cv_in_pd = Reg(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    val pipe_skid_rsp_cv_in_pvld = RegInit(false.B)
    val pipe_skid_rsp_cv_in_pd = Reg(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    //Wire
    val skid_rsp_cv_in_pvld = Wire(Bool())
    val skid_rsp_cv_in_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    val skid_rsp_cv_in_prdy = Wire(Bool())
    val pipe_skid_rsp_cv_in_prdy = Wire(Bool())
    val rsp_cv_out_pvld = Wire(Bool())
    val rsp_cv_out_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    //skid ready
    rsp_cv_in_prdy := skid_rsp_cv_in_prdy
    skid_flop_rsp_cv_in_prdy := skid_rsp_cv_in_prdy
    //skid valid
    when(skid_flop_rsp_cv_in_prdy){
        skid_flop_rsp_cv_in_pvld := rsp_cv_in_pvld
    }
    skid_rsp_cv_in_pvld := Mux(skid_flop_rsp_cv_in_prdy, rsp_cv_in_pvld, skid_flop_rsp_cv_in_pvld)
    //skid data
    when(skid_flop_rsp_cv_in_prdy & rsp_cv_in_pvld){
        skid_flop_rsp_cv_in_pd := rsp_cv_in_pd
    }
    skid_rsp_cv_in_pd := Mux(skid_flop_rsp_cv_in_prdy, rsp_cv_in_pd, skid_flop_rsp_cv_in_pd)
    //pipe ready
    skid_rsp_cv_in_prdy := pipe_skid_rsp_cv_in_prdy || !pipe_skid_rsp_cv_in_pvld
    //pipe valid
    when(skid_rsp_cv_in_prdy){
        pipe_skid_rsp_cv_in_pvld := skid_rsp_cv_in_pvld
    }
    //pipe data
    when(skid_rsp_cv_in_prdy && skid_rsp_cv_in_pvld){
        pipe_skid_rsp_cv_in_pd := skid_rsp_cv_in_pd
    }
    //pipe output
    pipe_skid_rsp_cv_in_prdy := rsp_cv_out_prdy
    rsp_cv_out_pvld := pipe_skid_rsp_cv_in_pvld
    rsp_cv_out_pd := pipe_skid_rsp_cv_in_pd

    io.cvif2cdma_dat_rd_rsp_ready.get := rsp_cv_in_prdy
    io.cvif2dc_dat_rd_rsp_valid.get := rsp_cv_out_pvld & cv_sel_dc
    io.cvif2img_dat_rd_rsp_valid.get := rsp_cv_out_pvld & cv_sel_img
    io.cvif2dc_dat_rd_rsp_pd.get := Fill(conf.NVDLA_CDMA_MEM_RD_RSP, cv_sel_dc) & rsp_cv_out_pd
    io.cvif2img_dat_rd_rsp_pd.get := Fill(conf.NVDLA_CDMA_MEM_RD_RSP, cv_sel_img) & rsp_cv_out_pd
    rsp_cv_out_prdy := (cv_sel_dc & io.cvif2dc_dat_rd_rsp_ready.get)|(cv_sel_img & io.cvif2img_dat_rd_rsp_ready.get)

    }


}}

object NV_NVDLA_CDMA_dma_muxDriver extends App {
  implicit val conf: cdmaConfiguration = new cdmaConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_dma_mux())
}

