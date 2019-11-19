package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_eg(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val eg2ig_axi_vld = Output(Bool())

        val mcif2rdma_rd_rsp_pd = Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))

        //noc2mcif
        val noc2mcif_axi_r = Flipped(Decoupled(new nocif_axi_rd_data_if))
    })

withClock(io.nvdla_core_clk) {
    
    val ipipe_axi_rdy = Wire(Bool())
    val pipe_pr = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_PRIMARY_MEMIF_WIDTH + 4))
    pipe_pr.io.clk := io.nvdla_core_clk
    pipe_pr.io.vi := io.noc2mcif_axi_r.valid
    io.noc2mcif_axi_r.ready := pipe_pr.io.ro
    pipe_pr.io.di := Cat(io.noc2mcif_axi_r.bits.id(3, 0), io.noc2mcif_axi_r.bits.data)
    val ipipe_axi_vld = pipe_pr.io.vo
    pipe_pr.io.ri := ipipe_axi_rdy

    io.eg2ig_axi_vld := ipipe_axi_vld & ipipe_axi_rdy
    val ipipe_axi_axid = pipe_pr.io.dout(conf.NVDLA_PRIMARY_MEMIF_WIDTH + 3, conf.NVDLA_PRIMARY_MEMIF_WIDTH)
    val ipipe_axi_data = pipe_pr.io.dout(conf.NVDLA_PRIMARY_MEMIF_WIDTH - 1, 0)

    val rq_wr_prdy = Wire(Vec(conf.RDMA_NUM, Bool()))
    val rq_wr_pvld = Wire(Vec(conf.RDMA_NUM, Bool()))
    ipipe_axi_rdy := (rq_wr_prdy zip rq_wr_pvld).map { case (a, b) => a & b }.reduce(_ | _)

    for (i <- 0 until conf.RDMA_NUM) {
        rq_wr_pvld(i) := ipipe_axi_vld & (ipipe_axi_axid === conf.arr_tieoff_axid(i).asUInt(4.W))
    }

    val lat_fifo = Array.fill(conf.RDMA_NUM){Module(new NV_NVDLA_fifo(depth = 4,width = conf.NVDLA_PRIMARY_MEMIF_WIDTH, ram_type = 0))}
    val rq_rd_prdy = Wire(Vec(conf.RDMA_NUM, Bool()))
    val rq_rd_pvld = Wire(Vec(conf.RDMA_NUM, Bool()))
    val rq_rd_pd   = Wire(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W)))
    for(i<-0 until conf.RDMA_NUM) {
        lat_fifo(i).io.clk := io.nvdla_core_clk
        lat_fifo(i).io.pwrbus_ram_pd := io.pwrbus_ram_pd

        lat_fifo(i).io.wr_pvld := rq_wr_pvld(i)
        rq_wr_prdy(i) := lat_fifo(i).io.wr_prdy
        lat_fifo(i).io.wr_pd := ipipe_axi_data

        rq_rd_pvld(i) := lat_fifo(i).io.rd_pvld
        lat_fifo(i).io.rd_prdy := rq_rd_prdy(i)
        rq_rd_pd(i) := lat_fifo(i).io.rd_pd
    }


    val pipe_out = Array.fill(conf.RDMA_NUM){Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_RSP)) }
    for(i<-0 until conf.RDMA_NUM) {
        pipe_out(i).io.clk := io.nvdla_core_clk

        pipe_out(i).io.vi := rq_rd_pvld(i)
        rq_rd_prdy(i) := pipe_out(i).io.ro
        pipe_out(i).io.di := Cat(rq_rd_pvld(i), rq_rd_pd(i))

        io.mcif2rdma_rd_rsp_pd(i).valid := pipe_out(i).io.vo
        pipe_out(i).io.ri := io.mcif2rdma_rd_rsp_pd(i).ready 
        io.mcif2rdma_rd_rsp_pd(i).bits := pipe_out(i).io.dout
        
    }
}}

object NV_NVDLA_MCIF_READ_egDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_READ_eg())
}



