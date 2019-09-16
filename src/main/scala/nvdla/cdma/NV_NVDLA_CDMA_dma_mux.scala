package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_CDMA_dma_mux(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())

        //dc_dat2mcif
        val dc_dat2mcif_rd_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W)))
        val mcif2dc_dat_rd_rsp_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
        
        //img_dat2mcif
        val img_dat2mcif_rd_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W)))
        val mcif2img_dat_rd_rsp_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))    

        //cdma_dat2mcif
        val cdma_dat2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2cdma_dat_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) 

        //dc_dat2cvif
        val dc_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W)))) else None
        val cvif2dc_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None

        //img2cvif
        val img_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W)))) else None
        val cvif2img_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None

        //cdma_dat2cvif
        val cdma_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2cdma_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))) else None
   
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
    val mc_sel_dc_w = io.dc_dat2mcif_rd_req_pd.valid
    val mc_sel_img_w = io.img_dat2mcif_rd_req_pd.valid
    val req_mc_in_pvld = io.dc_dat2mcif_rd_req_pd.valid|io.img_dat2mcif_rd_req_pd.valid
    val req_mc_in_pd = (Fill(conf.NVDLA_CDMA_MEM_RD_REQ, mc_sel_dc_w)&io.dc_dat2mcif_rd_req_pd.bits)|
                       (Fill(conf.NVDLA_CDMA_MEM_RD_REQ, mc_sel_img_w)&io.img_dat2mcif_rd_req_pd.bits)
    
    val req_mc_out_prdy = Wire(Bool())
    val pipe_p1 = Module{new NV_NVDLA_IS_pipe(conf.NVDLA_CDMA_MEM_RD_REQ)}
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := req_mc_in_pvld
    val req_mc_in_prdy = pipe_p1.io.ro
    pipe_p1.io.di := req_mc_in_pd
    val req_mc_out_pvld = pipe_p1.io.vo
    pipe_p1.io.ri := req_mc_out_prdy
    val req_mc_out_pd = pipe_p1.io.dout

    io.dc_dat2mcif_rd_req_pd.ready := req_mc_in_prdy & io.dc_dat2mcif_rd_req_pd.valid
    io.img_dat2mcif_rd_req_pd.ready := req_mc_in_prdy & io.img_dat2mcif_rd_req_pd.valid
    io.cdma_dat2mcif_rd_req_pd.valid := req_mc_out_pvld
    io.cdma_dat2mcif_rd_req_pd.bits := req_mc_out_pd
    req_mc_out_prdy := io.cdma_dat2mcif_rd_req_pd.ready

    val mc_sel_dc = RegEnable(mc_sel_dc_w, false.B, req_mc_in_pvld & req_mc_in_prdy)
    val mc_sel_img = RegEnable(mc_sel_img_w, false.B, req_mc_in_pvld & req_mc_in_prdy)

////////////////////////////////////////////////////////////////////////
// Data response channel                                              //
////////////////////////////////////////////////////////////////////////
//////////////// MCIF interface ////////////////
    val rsp_mc_in_pvld = io.mcif2cdma_dat_rd_rsp_pd.valid
    val rsp_mc_in_pd = io.mcif2cdma_dat_rd_rsp_pd.bits

    val rsp_mc_out_prdy = Wire(Bool())
    val pipe_p2 = Module{new NV_NVDLA_IS_pipe(conf.NVDLA_CDMA_MEM_RD_REQ)}
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := rsp_mc_in_pvld
    val rsp_mc_in_prdy = pipe_p2.io.ro
    pipe_p2.io.di := rsp_mc_in_pd
    val rsp_mc_out_pvld = pipe_p2.io.vo
    pipe_p2.io.ri := rsp_mc_out_prdy
    val rsp_mc_out_pd = pipe_p2.io.dout

    io.mcif2cdma_dat_rd_rsp_pd.ready := rsp_mc_in_prdy
    io.mcif2dc_dat_rd_rsp_pd.valid  := rsp_mc_out_pvld & mc_sel_dc
    io.mcif2img_dat_rd_rsp_pd.valid := rsp_mc_out_pvld & mc_sel_img
    io.mcif2dc_dat_rd_rsp_pd.bits := Fill(conf.NVDLA_CDMA_MEM_RD_RSP, mc_sel_dc) & rsp_mc_out_pd
    io.mcif2img_dat_rd_rsp_pd.bits := Fill(conf.NVDLA_CDMA_MEM_RD_RSP, mc_sel_img) & rsp_mc_out_pd
    rsp_mc_out_prdy := (mc_sel_dc & io.mcif2dc_dat_rd_rsp_pd.ready)|(mc_sel_img & io.mcif2img_dat_rd_rsp_pd.ready)


//////////////// CVIF interface ////////////////

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
////////////////////////////////////////////////////////////////////////
// Data request channel                                               //
////////////////////////////////////////////////////////////////////////
    val cv_sel_dc_w = io.dc_dat2cvif_rd_req_pd.get.valid
    val cv_sel_img_w = io.img_dat2cvif_rd_req_pd.get.valid
    val req_cv_in_pvld = io.dc_dat2cvif_rd_req_pd.get.valid|io.img_dat2cvif_rd_req_pd.get.valid
    val req_cv_in_pd = (Fill(conf.NVDLA_CDMA_MEM_RD_REQ, cv_sel_dc_w)&io.dc_dat2cvif_rd_req_pd.get.bits)|
                       (Fill(conf.NVDLA_CDMA_MEM_RD_REQ, cv_sel_img_w)&io.img_dat2cvif_rd_req_pd.get.bits)
    
    val req_cv_out_prdy = Wire(Bool())
    val pipe_p3 = Module{new NV_NVDLA_IS_pipe(conf.NVDLA_CDMA_MEM_RD_REQ)}
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := req_cv_in_pvld
    val req_cv_in_prdy = pipe_p3.io.ro
    pipe_p3.io.di := req_cv_in_pd
    val req_cv_out_pvld = pipe_p3.io.vo
    pipe_p3.io.ri := req_cv_out_prdy
    val req_cv_out_pd = pipe_p3.io.dout

    io.dc_dat2cvif_rd_req_pd.get.ready := req_cv_in_prdy & io.dc_dat2cvif_rd_req_pd.get.valid
    io.img_dat2cvif_rd_req_pd.get.ready := req_cv_in_prdy & io.img_dat2cvif_rd_req_pd.get.valid
    io.cdma_dat2cvif_rd_req_pd.get.valid := req_cv_out_pvld
    io.cdma_dat2cvif_rd_req_pd.get.bits := req_cv_out_pd
    req_cv_out_prdy := io.cdma_dat2cvif_rd_req_pd.get.ready

    val cv_sel_dc = RegEnable(cv_sel_dc_w, false.B, req_cv_in_pvld & req_cv_in_prdy)
    val cv_sel_img = RegEnable(cv_sel_img_w, false.B, req_cv_in_pvld & req_cv_in_prdy)

////////////////////////////////////////////////////////////////////////
// Data response channel                                              //
////////////////////////////////////////////////////////////////////////
    val rsp_cv_in_pvld = io.cvif2cdma_dat_rd_rsp_pd.get.valid
    val rsp_cv_in_pd = io.cvif2cdma_dat_rd_rsp_pd.get.bits

    val rsp_cv_out_prdy = Wire(Bool())
    val pipe_p4 = Module{new NV_NVDLA_IS_pipe(conf.NVDLA_CDMA_MEM_RD_REQ)}
    pipe_p4.io.clk := io.nvdla_core_clk
    pipe_p4.io.vi := rsp_cv_in_pvld
    val rsp_cv_in_prdy = pipe_p4.io.ro
    pipe_p4.io.di := rsp_cv_in_pd
    val rsp_cv_out_pvld = pipe_p4.io.vo
    pipe_p4.io.ri := rsp_cv_out_prdy
    val rsp_cv_out_pd = pipe_p4.io.dout

    io.cvif2cdma_dat_rd_rsp_pd.get.ready := rsp_cv_in_prdy
    io.cvif2dc_dat_rd_rsp_pd.get.valid := rsp_cv_out_pvld & cv_sel_dc
    io.cvif2img_dat_rd_rsp_pd.get.valid := rsp_cv_out_pvld & cv_sel_img
    io.cvif2dc_dat_rd_rsp_pd.get.bits := Fill(conf.NVDLA_CDMA_MEM_RD_RSP, cv_sel_dc) & rsp_cv_out_pd
    io.cvif2img_dat_rd_rsp_pd.get.bits := Fill(conf.NVDLA_CDMA_MEM_RD_RSP, cv_sel_img) & rsp_cv_out_pd
    rsp_cv_out_prdy := (cv_sel_dc & io.cvif2dc_dat_rd_rsp_pd.get.ready)|(cv_sel_img & io.cvif2img_dat_rd_rsp_pd.get.ready)

    }

}}

object NV_NVDLA_CDMA_dma_muxDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_dma_mux())
}

