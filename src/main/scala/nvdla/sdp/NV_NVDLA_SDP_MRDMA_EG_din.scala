package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_SDP_MRDMA_EG_din(implicit val conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        //dma_rd
        val dma_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))
        val dma_rd_cdt_lat_fifo_pop = Output(Bool())             
        val dma_rd_rsp_ram_type = Output(Bool())

        //cmd2dat
        val cmd2dat_spt_pd = Flipped(DecoupledIO(UInt(13.W)))

        //pfifo
        val pfifo_rd_pd = Vec(4, DecoupledIO(UInt(conf.AM_DW.W)))

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
    //==============
    // Latency FIFO to buffer return DATA
    //==============
    val lat_ecc_rd_pvld = Wire(Bool())
    val lat_ecc_rd_prdy = Wire(Bool())
    val lat_ecc_rd_pd = Wire(UInt(conf.NVDLA_DMA_RD_RSP.W))
    
    io.dma_rd_rsp_ram_type := io.reg2dp_src_ram_type
    io.dma_rd_cdt_lat_fifo_pop := lat_ecc_rd_pvld & lat_ecc_rd_prdy

    val u_lat_fifo = Module(new NV_NVDLA_fifo(depth = conf.NVDLA_VMOD_SDP_MRDMA_LATENCY_FIFO_DEPTH, width = conf.NVDLA_DMA_RD_RSP, ram_type = 2, distant_wr_req = false))
    u_lat_fifo.io.clk := io.nvdla_core_clk
    u_lat_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_lat_fifo.io.wr_pvld := io.dma_rd_rsp_pd.valid
    io.dma_rd_rsp_pd.ready := u_lat_fifo.io.wr_prdy
    u_lat_fifo.io.wr_pd := io.dma_rd_rsp_pd.bits
    lat_ecc_rd_pvld := u_lat_fifo.io.rd_pvld
    u_lat_fifo.io.rd_prdy := lat_ecc_rd_prdy
    lat_ecc_rd_pd := u_lat_fifo.io.rd_pd

    val lat_ecc_rd_accept = lat_ecc_rd_pvld & lat_ecc_rd_prdy  
    // val lat_ecc_rd_data(conf.NVDLA_MEMIF_WIDTH - 1, 0) = lat_ecc_rd_pd(conf.NVDLA_MEMIF_WIDTH - 1, 0)    
    val lat_ecc_rd_mask = Cat(0.U((4 - conf.NVDLA_DMA_MASK_BIT).W), lat_ecc_rd_pd(conf.NVDLA_DMA_RD_RSP-1, conf.NVDLA_MEMIF_WIDTH))
    val lat_ecc_rd_size = PopCount(lat_ecc_rd_mask)

    //========command for pfifo wr ====================
    val is_last_beat = Wire(Bool())
    io.cmd2dat_spt_pd.ready := lat_ecc_rd_accept & is_last_beat

    val cmd2dat_spt_size = io.cmd2dat_spt_pd.bits
    val cmd_size = Mux(io.cmd2dat_spt_pd.valid, (cmd2dat_spt_size + 1.U), 0.U)

    val beat_cnt = RegInit(0.U)
    val beat_cnt_nxt = beat_cnt + lat_ecc_rd_size

    when(lat_ecc_rd_accept){
        when(is_last_beat){
            beat_cnt := 0.U
        }.otherwise{
            beat_cnt := beat_cnt_nxt
        }
    }

    is_last_beat := beat_cnt_nxt === cmd_size

    /////////combine lat fifo pd to 4*atomic_m*bpe//////
    val lat_ecc_rd_beat_end = is_last_beat
    val unpack_out_prdy = Wire(Bool())
    
    val u_rdma_unpack = Module(new NV_NVDLA_SDP_RDMA_unpack)
    u_rdma_unpack.io.nvdla_core_clk := io.nvdla_core_clk

    u_rdma_unpack.io.inp_end := lat_ecc_rd_beat_end
    u_rdma_unpack.io.inp.valid := lat_ecc_rd_pvld
    lat_ecc_rd_prdy := u_rdma_unpack.io.inp.ready
    u_rdma_unpack.io.inp.bits := lat_ecc_rd_pd

    val unpack_out_pvld = u_rdma_unpack.io.out.valid
    u_rdma_unpack.io.out.ready := unpack_out_prdy
    val unpack_out_pd = u_rdma_unpack.io.out.bits

    val pfifo_wr_rdy = Wire(Bool())
    unpack_out_prdy := pfifo_wr_rdy
    val pfifo_wr_mask = unpack_out_pd(4*conf.AM_DW+3, 4*conf.AM_DW)
    val pfifo_wr_vld = unpack_out_pvld

    //==================================
    // FIFO WRITE
    val pfifo_wr_pd = VecInit((0 to 3) map {
                       i => unpack_out_pd(conf.AM_DW*i+conf.AM_DW-1, conf.AM_DW*i)})
    
    val pfifo_wr_prdy = Wire(Vec(4, Bool())) 
    val pfifo_wr_pvld = Wire(Vec(4, Bool())) 

    pfifo_wr_rdy := !(pfifo_wr_mask(0) & !pfifo_wr_prdy(0) | 
                    pfifo_wr_mask(1) & !pfifo_wr_prdy(1) | 
                    pfifo_wr_mask(2) & !pfifo_wr_prdy(2) | 
                    pfifo_wr_mask(3) & !pfifo_wr_prdy(3) )
    pfifo_wr_pvld(0) := pfifo_wr_vld & pfifo_wr_mask(0) & 
                        !(  pfifo_wr_mask(1) & !pfifo_wr_prdy(1) | 
                            pfifo_wr_mask(2) & !pfifo_wr_prdy(2) | 
                            pfifo_wr_mask(3) & !pfifo_wr_prdy(3) )
    pfifo_wr_pvld(1) := pfifo_wr_vld & pfifo_wr_mask(1) & 
                        !(  pfifo_wr_mask(0) & !pfifo_wr_prdy(0) | 
                            pfifo_wr_mask(2) & !pfifo_wr_prdy(2) | 
                            pfifo_wr_mask(3) & !pfifo_wr_prdy(3) )
    pfifo_wr_pvld(2) := pfifo_wr_vld & pfifo_wr_mask(2) & 
                        !(  pfifo_wr_mask(0) & !pfifo_wr_prdy(0) | 
                            pfifo_wr_mask(1) & !pfifo_wr_prdy(1) | 
                            pfifo_wr_mask(3) & !pfifo_wr_prdy(3) )
    pfifo_wr_pvld(3) := pfifo_wr_vld & pfifo_wr_mask(3) & 
                        !(  pfifo_wr_mask(0) & !pfifo_wr_prdy(0) | 
                            pfifo_wr_mask(1) & !pfifo_wr_prdy(1) | 
                            pfifo_wr_mask(2) & !pfifo_wr_prdy(3) )
    
    val u_pfifo =  Array.fill(4){Module(new NV_NVDLA_IS_pipe(conf.AM_DW))}
    for(i <- 0 to 3){
        u_pfifo(i).io.clk := io.nvdla_core_clk
        u_pfifo(i).io.vi := pfifo_wr_pvld(i)
        pfifo_wr_prdy(i) := u_pfifo(i).io.ro
        u_pfifo(i).io.di := pfifo_wr_pd(i)

        io.pfifo_rd_pd(i).valid := u_pfifo(i).io.vo
        u_pfifo(i).io.ri := io.pfifo_rd_pd(i).ready
        io.pfifo_rd_pd(i).bits := u_pfifo(i).io.dout
    }
}}


object NV_NVDLA_SDP_MRDMA_EG_dinDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_MRDMA_EG_din())
}