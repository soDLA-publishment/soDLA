package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_DP_syncfifo(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {

        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val cvt2sync_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE + 17).W)))
        val sum2sync_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE*2+3)).W)))

        val sync2itp_pd = DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE*2+3)).W))
        val sync2mul_pd = DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE).W))
        val sync2ocvt_pd = DecoupledIO(UInt(17.W))

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
    //## pipe (1) randomizer
    val data_vld = Wire(Bool())
    val data_rdy = Wire(Bool())
    val info_vld = Wire(Bool())
    val info_rdy = Wire(Bool())
    val data_pd = io.cvt2sync_pd.bits(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE-1, 0)
    val info_pd = io.cvt2sync_pd.bits(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+16, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE)

    val pipe_0 = Module(new NV_NVDLA_BC_pipe(conf.NVDLA_CDP_THROUGHPUT * conf.NVDLA_CDP_ICVTO_BWPE))
    pipe_0.io.clk := io.nvdla_core_clk
    pipe_0.io.vi := data_vld
    data_rdy := pipe_0.io.ro
    pipe_0.io.di := data_pd

    val pipe_1 = Module(new NV_NVDLA_BC_pipe(7))
    pipe_1.io.clk := io.nvdla_core_clk
    pipe_1.io.vi := info_vld
    info_rdy := pipe_1.io.ro
    pipe_1.io.di := info_pd

    //////////////////////////////////////////////
    //datin sync fifo
    io.cvt2sync_pd.ready := data_rdy & info_rdy
    data_vld := io.cvt2sync_pd.valid & info_rdy
    info_vld := io.cvt2sync_pd.valid & data_rdy

    //////////////////////////////////////////////
    //////////////////////////////////////////////
    val u_data_sync_fifo =  Module{new NV_NVDLA_fifo_new(
                            depth = 80, 
                            width = conf.NVDLA_CDP_THROUGHPUT * conf.NVDLA_CDP_ICVTO_BWPE,
                            ram_type = 2, 
                            ram_bypass = true)}

    u_data_sync_fifo.io.clk := io.nvdla_core_clk
    u_data_sync_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_data_sync_fifo.io.wr_pvld := pipe_0.io.vo
    pipe_0.io.ri := u_data_sync_fifo.io.wr_prdy
    u_data_sync_fifo.io.wr_pd := pipe_0.io.dout

    io.sync2mul_pd.valid := u_data_sync_fifo.io.rd_pvld 
    u_data_sync_fifo.io.rd_prdy := io.sync2mul_pd.ready
    io.sync2mul_pd.bits := u_data_sync_fifo.io.rd_pd 

    ///////////////////////////////////////////
    val u_info_sync_fifo = Module{new NV_NVDLA_fifo_new(
                            depth = 80, 
                            width = 17,
                            ram_type = 2, 
                            ram_bypass = true)}
    u_info_sync_fifo.io.clk := io.nvdla_core_clk
    u_info_sync_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_info_sync_fifo.io.wr_pvld := pipe_1.io.vo
    pipe_1.io.ri := u_info_sync_fifo.io.wr_prdy
    u_info_sync_fifo.io.wr_pd := pipe_1.io.dout

    io.sync2ocvt_pd.valid := u_info_sync_fifo.io.rd_pvld 
    u_info_sync_fifo.io.rd_prdy := io.sync2ocvt_pd.ready
    io.sync2ocvt_pd.bits := u_info_sync_fifo.io.rd_pd 

    ///////////////////////////////////////////
    val u_sumpd_sync_fifo = Module{new NV_NVDLA_fifo_new(
                            depth = 80, 
                            width = conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE*2+3),
                            ram_type = 2, 
                            ram_bypass = true)}
    u_sumpd_sync_fifo.io.clk := io.nvdla_core_clk
    u_sumpd_sync_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_sumpd_sync_fifo.io.wr_pvld := io.sum2sync_pd.valid
    io.sum2sync_pd.ready := u_sumpd_sync_fifo.io.wr_prdy
    u_sumpd_sync_fifo.io.wr_pd := io.sum2sync_pd.bits

    io.sync2itp_pd.valid := u_sumpd_sync_fifo.io.rd_pvld 
    u_sumpd_sync_fifo.io.rd_prdy := io.sync2itp_pd.ready
    io.sync2itp_pd.bits := u_sumpd_sync_fifo.io.rd_pd 

}}


object NV_NVDLA_CDP_DP_syncfifoDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_syncfifo())
}
