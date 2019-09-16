package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_DMAIF_rdreq(DMABW: Int)(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
    
        val dmaif_rd_req_pd = Flipped(DecoupledIO(UInt(DMABW.W)))

        val mcif_rd_req_pd = DecoupledIO(UInt(DMABW.W))
        val cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(DMABW.W)))
                             else None

        val reg2dp_src_ram_type = Input(Bool())

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

    val is_pipe0 = Module{new NV_NVDLA_IS_pipe(DMABW)}
    is_pipe0.io.clk := io.nvdla_core_clk
    is_pipe0.io.vi := io.dmaif_rd_req_pd.valid & (io.reg2dp_src_ram_type === 1.U)
    val mc_dma_rd_req_rdy = is_pipe0.io.ro
    is_pipe0.io.di := io.dmaif_rd_req_pd.bits
    io.mcif_rd_req_pd.valid := is_pipe0.io.vo
    is_pipe0.io.ri := io.mcif_rd_req_pd.ready
    io.mcif_rd_req_pd.bits := is_pipe0.io.dout

    val mc_rd_req_rdyi = mc_dma_rd_req_rdy & (io.reg2dp_src_ram_type === 1.U)


    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){

        val is_pipe1 = Module{new NV_NVDLA_IS_pipe(DMABW)}
        is_pipe1.io.clk := io.nvdla_core_clk
        is_pipe1.io.vi := io.dmaif_rd_req_pd.valid & (io.reg2dp_src_ram_type === 0.U)
        val cv_dma_rd_req_rdy = is_pipe1.io.ro
        is_pipe1.io.di := io.dmaif_rd_req_pd.bits
        io.cvif_rd_req_pd.get.valid := is_pipe1.io.vo
        is_pipe1.io.ri := io.cvif_rd_req_pd.get.ready
        io.cvif_rd_req_pd.get.bits := is_pipe1.io.dout

        val cv_rd_req_rdyi = cv_dma_rd_req_rdy & (io.reg2dp_src_ram_type === 0.U)
        io.dmaif_rd_req_pd.ready := mc_rd_req_rdyi | cv_rd_req_rdyi

    }
    else{
        io.dmaif_rd_req_pd.ready := mc_rd_req_rdyi
    }
    
}}
