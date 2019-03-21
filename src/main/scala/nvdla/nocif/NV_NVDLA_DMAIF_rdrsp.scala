package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_DMAIF_rdrsp(implicit conf: cdmaConfiguration) extends Module {
    val DMABW = conf.DMAIF + conf.ATMM_NUM
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
        
        val cvif_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(DMABW.W))) else None
        val cvif_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cvif_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

        val mcif_rd_rsp_pd = Input(UInt(DMABW.W))
        val mcif_rd_rsp_valid = Input(Bool())
        val mcif_rd_rsp_ready = Output(Bool())

        val dmaif_rd_rsp_pd = Output(UInt(DMABW.W))
        val dmaif_rd_rsp_pvld = Output(Bool())
        val dmaif_rd_rsp_prdy = Input(Bool())

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
    val dma_rd_rsp_rdy = Wire(Bool())
    val dma_rd_rsp_vld = Wire(Bool())
    val dma_rd_rsp_pd = Wire(UInt(DMABW.W))
///////////////////////////////////////
// pipe before mux
///////////////////////////////////////
//: &eperl::pipe(" -wid $dmabw -is -do mcif_rd_rsp_pd_d0 -vo mcif_rd_rsp_valid_d0 -ri dma_rd_rsp_rdy -di mcif_rd_rsp_pd -vi mcif_rd_rsp_valid -ro mcif_rd_rsp_ready  ");
    val is_pipe0 = Module{new NV_NVDLA_IS_pipe(DMABW)}
    is_pipe0.io.clk := io.nvdla_core_clk
    is_pipe0.io.ri := dma_rd_rsp_rdy
    is_pipe0.io.vi := dma_rd_rsp_vld
    is_pipe0.io.di := dma_rd_rsp_pd
    io.mcif_rd_rsp_ready := is_pipe0.io.ro
    val mcif_rd_rsp_valid_d0 = is_pipe0.io.vo
    val mcif_rd_rsp_pd_d0 = is_pipe0.io.dout

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        val cv_dma_rd_rsp_rdy = dma_rd_rsp_rdy
        val is_pipe1 = Module{new NV_NVDLA_IS_pipe(DMABW)}
        is_pipe1.io.clk := io.nvdla_core_clk
        is_pipe1.io.ri := cv_dma_rd_rsp_rdy
        is_pipe1.io.vi := io.cvif_rd_rsp_valid.get
        is_pipe1.io.di := io.cvif_rd_rsp_pd.get
        io.cvif_rd_rsp_ready.get := is_pipe1.io.ro
        val cvif_rd_rsp_valid_d0 = is_pipe1.io.vo
        val cvif_rd_rsp_pd_d0 = is_pipe1.io.dout
    ///////////////////////////////////////
    //mux
    ///////////////////////////////////////
        dma_rd_rsp_vld := mcif_rd_rsp_valid_d0 | cvif_rd_rsp_valid_d0
        dma_rd_rsp_pd := (Fill(DMABW, mcif_rd_rsp_valid_d0) & mcif_rd_rsp_pd_d0)|
                            (Fill(DMABW, cvif_rd_rsp_valid_d0) & cvif_rd_rsp_pd_d0)
    }
    else{
    ///////////////////////////////////////
    //mux
    ///////////////////////////////////////
        dma_rd_rsp_vld := mcif_rd_rsp_valid_d0
        dma_rd_rsp_pd := (Fill(DMABW, mcif_rd_rsp_valid_d0) & mcif_rd_rsp_pd_d0)
    }

///////////////////////////////////////
// pipe after mux
///////////////////////////////////////
    val is_pipe2 = Module{new NV_NVDLA_IS_pipe(DMABW)}
    is_pipe2.io.clk := io.nvdla_core_clk
    is_pipe2.io.ri := io.dmaif_rd_rsp_prdy
    is_pipe2.io.vi := dma_rd_rsp_vld
    is_pipe2.io.di := dma_rd_rsp_pd
    val dma_rd_rsp_rdy_f = is_pipe2.io.ro
    io.dmaif_rd_rsp_pvld := is_pipe2.io.vo
    io.dmaif_rd_rsp_pd := is_pipe2.io.dout

    dma_rd_rsp_rdy := dma_rd_rsp_rdy_f
    
}}

object NV_NVDLA_DMAIF_rdrspDriver extends App {
  implicit val conf: cdmaConfiguration = new cdmaConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_DMAIF_rdrsp())
}
