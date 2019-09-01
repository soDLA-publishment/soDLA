package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_eg(implicit conf: xxifConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))
        val eg2ig_axi_vld = Output(Bool())

        val mcif2rdma_rd_rsp_valid = Output(Vec(conf.RDMA_NUM, Bool()))
        val mcif2rdma_rd_rsp_ready = Input(Vec(conf.RDMA_NUM, Bool()))
        val mcif2rdma_rd_rsp_pd = Output(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_RSP.W)))

        //noc2mcif
        val noc2mcif_axi_r_rvalid = Input(Bool())
        val noc2mcif_axi_r_rready = Output(Bool())
        val noc2mcif_axi_r_rid = Input(UInt(8.W))
        val noc2mcif_axi_r_rlast = Input(Bool())
        val noc2mcif_axi_r_rdata = Input(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))
    })

    withClock(io.nvdla_core_clk) {

        val noc2mcif_axi_r_pd = Cat(io.noc2mcif_axi_r_rid(3, 0), io.noc2mcif_axi_r_rdata)

        val ipipe_axi_rdy = Wire(Bool())
        val ipipe_axi_vld = Wire(Bool())
        val ipipe_axi_axid = Wire(UInt(4.W))
        val ipipe_axi_data = Wire(UInt((conf.NVDLA_PRIMARY_MEMIF_WIDTH).W))
        val ipipe_axi_pd = Wire(UInt((conf.NVDLA_PRIMARY_MEMIF_WIDTH + 4).W))

        val pipe_pr = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_PRIMARY_MEMIF_WIDTH + 4))
        pipe_pr.io.clk := io.nvdla_core_clk
        pipe_pr.io.di := noc2mcif_axi_r_pd
        pipe_pr.io.vi := io.noc2mcif_axi_r_rvalid
        io.noc2mcif_axi_r_rready := pipe_pr.io.ro
        ipipe_axi_pd := pipe_pr.io.dout
        ipipe_axi_vld := pipe_pr.io.vo
        pipe_pr.io.ri := ipipe_axi_rdy

        io.eg2ig_axi_vld := ipipe_axi_vld & ipipe_axi_rdy
        ipipe_axi_axid := ipipe_axi_pd(conf.NVDLA_PRIMARY_MEMIF_WIDTH + 3, conf.NVDLA_PRIMARY_MEMIF_WIDTH)
        ipipe_axi_data := ipipe_axi_pd(conf.NVDLA_PRIMARY_MEMIF_WIDTH - 1, 0)

        val rq_wr_prdy = Wire(Vec(conf.RDMA_NUM, Bool()))
        val rq_wr_pvld = Wire(Vec(conf.RDMA_NUM, Bool()))
        ipipe_axi_rdy := (rq_wr_prdy zip rq_wr_pvld).map { case (a, b) => a & b }.reduce(_ | _)

        for (i <- 0 until conf.RDMA_NUM) {
            rq_wr_pvld(i) := ipipe_axi_vld & (ipipe_axi_axid === conf.tieoff_axid_read(i).U)
        }

        val lat_fifo = Array.fill(conf.RDMA_NUM) {
            Module(new NV_NVDLA_fifo(depth = 16,
                width = 256, wr_empty_port = false, wr_idle_port = false, rd_idle_port = false,
                ram_type = 1,
                distant_rd_data = false, distant_wr_data = false, distant_rd_req = false, distant_wr_req = false))
        }

        val rq_rd_prdy = Wire(Vec(conf.RDMA_NUM, Bool()))
        val rq_rd_pvld = Wire(Vec(conf.RDMA_NUM, Bool()))
        val rq_rd_pd   = Wire(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W)))
        for(i<-0 until conf.RDMA_NUM) {
            lat_fifo(i).io.clk := io.nvdla_core_clk
            lat_fifo(i).io.pwrbus_ram_pd := io.pwrbus_ram_pd

            rq_wr_prdy(i) := lat_fifo(i).io.wr_ready
            lat_fifo(i).io.wr_req := rq_wr_pvld(i)
            lat_fifo(i).io.wr_data:= ipipe_axi_data

            lat_fifo(i).io.rd_ready := rq_rd_prdy(i)
            rq_rd_pvld(i) := lat_fifo(i).io.rd_req
            rq_rd_pd(i) := lat_fifo(i).io.rd_data
        }


        val pipe_out = Array.fill(conf.RDMA_NUM) { Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_RSP)) }
        for(i<-0 until conf.RDMA_NUM) {
            pipe_out(i).io.clk := io.nvdla_core_clk
            pipe_out(i).io.di := Cat(rq_rd_pvld(i), rq_rd_pd(i))
            pipe_out(i).io.vi := rq_rd_pvld(i)
            rq_rd_prdy(i) := pipe_out(i).io.ro

            io.mcif2rdma_rd_rsp_pd(i) := pipe_out(i).io.dout
            io.mcif2rdma_rd_rsp_valid(i) := pipe_out(i).io.vo
            pipe_out(i).io.ri := io.mcif2rdma_rd_rsp_ready(i)
        }
    }
}

object NV_NVDLA_MCIF_READ_egDriver extends App {
    implicit val conf: xxifConfiguration = new xxifConfiguration
    chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_READ_eg())
}


