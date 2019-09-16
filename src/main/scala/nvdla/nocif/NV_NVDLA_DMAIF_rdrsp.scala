package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_DMAIF_rdrsp(DMABW: Int)(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val mcif_rd_rsp_pd = Flipped(DecoupledIO(UInt(DMABW.W)))
        val cvif_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(DMABW.W)))) else None

        val dmaif_rd_rsp_pd = DecoupledIO(UInt(DMABW.W))

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

    val dma_rd_rsp_vld = Wire(Bool())
    val dma_rd_rsp_rdy = Wire(Bool())
    val dma_rd_rsp_pd = Wire(UInt(DMABW.W))

    ///////////////////////////////////////
    // pipe before mux
    ///////////////////////////////////////
    val is_pipe0 = Module{new NV_NVDLA_IS_pipe(DMABW)}
    is_pipe0.io.clk := io.nvdla_core_clk
    is_pipe0.io.vi := io.mcif_rd_rsp_pd.valid
    io.mcif_rd_rsp_pd.ready := is_pipe0.io.ro
    is_pipe0.io.di := io.mcif_rd_rsp_pd.bits
    val mcif_rd_rsp_valid_d0 = is_pipe0.io.vo
    is_pipe0.io.ri := dma_rd_rsp_rdy
    val mcif_rd_rsp_pd_d0 = is_pipe0.io.dout

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        val is_pipe1 = Module{new NV_NVDLA_IS_pipe(DMABW)}
        is_pipe1.io.clk := io.nvdla_core_clk
        is_pipe1.io.vi := io.cvif_rd_rsp_pd.get.valid
        io.cvif_rd_rsp_pd.get.ready := is_pipe1.io.ro
        is_pipe1.io.di := io.cvif_rd_rsp_pd.get.bits
        val cvif_rd_rsp_valid_d0 = is_pipe1.io.vo
        is_pipe1.io.ri := dma_rd_rsp_rdy
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
    is_pipe2.io.vi := dma_rd_rsp_vld
    dma_rd_rsp_rdy := is_pipe2.io.ro
    is_pipe2.io.di := dma_rd_rsp_pd
    io.dmaif_rd_rsp_pd.valid := is_pipe2.io.vo
    is_pipe2.io.ri := io.dmaif_rd_rsp_pd.ready
    io.dmaif_rd_rsp_pd.bits:= is_pipe2.io.dout

    
}}

