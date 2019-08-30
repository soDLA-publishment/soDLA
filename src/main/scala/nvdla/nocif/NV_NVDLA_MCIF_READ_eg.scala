package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_eg(implicit conf: xxifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))
        val eg2ig_axi_vld = Output(Bool())

        val mcif2rdma_rd_rsp_valid = Output(Vec(conf.RDMA_NUM, Bool()))
        val mcif2rdma_rd_rsp_ready = Input(Vec(conf.RDMA_NUM, Bool()))
        val mcif2rdma_rd_rsp_pd    = Output(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_RSP.W)))

        //noc2mcif
        val noc2mcif_axi_r_rvalid = Input(Bool())
        val noc2mcif_axi_r_rready = Output(Bool())
        val noc2mcif_axi_r_rid = Input(UInt(8.W))
        val noc2mcif_axi_r_rlast = Input(Bool())
        val noc2mcif_axi_r_rdata = Input(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))
    })

    withClock(io.nvdla_core_clk){

        val noc2mcif_axi_r_pd = Cat(io.noc2mcif_axi_r_rid(3, 0), io.noc2mcif_axi_r_rdata)

        val ipipe_axi_rdy = Wire(Bool())
        val ipipe_axi_vld = Wire(Bool())
        val ipipe_axi_axid= Wire(UInt(4.W))
        val ipipe_axi_data= Wire(UInt((conf.NVDLA_PRIMARY_MEMIF_WIDTH).W))
        val ipipe_axi_pd  = Wire(UInt((conf.NVDLA_PRIMARY_MEMIF_WIDTH+4).W))

        val pipe_pr = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_PRIMARY_MEMIF_WIDTH+4))
        pipe_pr.io.clk := io.nvdla_core_clk
        pipe_pr.io.di  := noc2mcif_axi_r_pd
        pipe_pr.io.vi  := io.noc2mcif_axi_r_rvalid
        io.noc2mcif_axi_r_rready := pipe_pr.io.ro
        ipipe_axi_pd := pipe_pr.io.dout
        ipipe_axi_vld:= pipe_pr.io.vo
        pipe_pr.io.ri:= ipipe_axi_rdy

        val eg2ig_axi_vld = ipipe_axi_vld & ipipe_axi_rdy
        ipipe_axi_axid := ipipe_axi_pd(conf.NVDLA_PRIMARY_MEMIF_WIDTH+3, conf.NVDLA_PRIMARY_MEMIF_WIDTH)
        ipipe_axi_data := ipipe_axi_pd(conf.NVDLA_PRIMARY_MEMIF_WIDTH-1, 0)

//        val rq0_wr_pd = ipipe_axi_data

        val rq_wr_prdy = Wire(Vec(conf.RDMA_NUM, Bool()))
        val rq_wr_pvld = Wire(Vec(conf.RDMA_NUM, Bool()))
        ipipe_axi_rdy := (rq_wr_prdy zip rq_wr_pvld).map {case (a, b) => a & b}.reduce(_ | _)

        for(i<-0 until conf.RDMA_NUM-1) {
            rq_wr_prdy(i) := ipipe_axi_vld & (ipipe_axi_axid === conf.tieoff_axid_read(i).U)
        }

    }
}



