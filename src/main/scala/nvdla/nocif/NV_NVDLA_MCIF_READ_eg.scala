package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_eg(implicit conf: nocifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //
        val mcif2sdp_rd_rsp_valid = Output(Bool());  /* data valid */
        val mcif2sdp_rd_rsp_ready = Input(Bool())  /* data return handshake */
        val mcif2sdp_rd_rsp_pd = Output(UInt(514.W));

        val mcif2sdp_b_rd_rsp_valid = Output(Bool())  /* data valid */
        val mcif2sdp_b_rd_rsp_ready = Input(Bool())  /* data return handshake */
        val mcif2sdp_b_rd_rsp_pd = Output(UInt(514.W))

        val mcif2sdp_n_rd_rsp_valid = Output(Bool())  /* data valid */
        val mcif2sdp_n_rd_rsp_ready = Input(Bool())  /* data return handshake */
        val mcif2sdp_n_rd_rsp_pd = Output(UInt(514.W))

        val mcif2sdp_e_rd_rsp_valid = Output(Bool())  /* data valid */
        val mcif2sdp_e_rd_rsp_ready = Input(Bool())  /* data return handshake */
        val mcif2sdp_e_rd_rsp_pd = Output(UInt(514.W))

        val mcif2cdp_rd_rsp_valid = Output(Bool())  /* data valid */
        val mcif2cdp_rd_rsp_ready = Input(Bool())  /* data return handshake */
        val mcif2cdp_rd_rsp_pd = Output(UInt(514.W))

        val mcif2pdp_rd_rsp_valid = Output(Bool())  /* data valid */
        val mcif2pdp_rd_rsp_ready = Input(Bool())   /* data return handshake */
        val mcif2pdp_rd_rsp_pd = Output(UInt(514.W))

        val mcif2bdma_rd_rsp_valid = Output(Bool())  /* data valid */
        val mcif2bdma_rd_rsp_ready = Input(Bool())   /* data return handshake */
        val mcif2bdma_rd_rsp_pd = Output(UInt(514.W))

        val mcif2rbk_rd_rsp_valid = Output(Bool())  /* data valid */
        val mcif2rbk_rd_rsp_ready = Input(Bool())   /* data return handshake */
        val mcif2rbk_rd_rsp_pd = Output(UInt(514.W))

        val mcif2cdma_wt_rd_rsp_valid = Output(Bool())  /* data valid */
        val mcif2cdma_wt_rd_rsp_ready = Input(Bool())   /* data return handshake */
        val mcif2cdma_wt_rd_rsp_pd = Output(UInt(514.W))

        val mcif2cdma_dat_rd_rsp_valid = Output(Bool())  /* data valid */
        val mcif2cdma_dat_rd_rsp_ready = Input(Bool())  /* data return handshake */
        val mcif2cdma_dat_rd_rsp_pd = Output(UInt(514.W))


        //cq_rd
        val cq_rd_pvld = Input(Vec(conf.RDMA_NUM, Bool()))
        val cq_rd_prdy = Output(Vec(conf.RDMA_NUM, Bool()))
        val cq_rd_pd = Input(Vec(conf.RDMA_NUM, UInt(7.W)))

        //noc2mcif
        val noc2mcif_axi_r_rvalid = Input(Bool())
        val noc2mcif_axi_r_rready = Output(Bool())
        val noc2mcif_axi_r_rid = Input(UInt(8.W))
        val noc2mcif_axi_r_rlast = Input(Bool())
        val noc2mcif_axi_r_rdata = Input(UInt(512.W))


        //pwrbus
        val pwrbus_ram_pd = Input(UInt(32.W))

        val eg2ig_axi_vld = Output(Bool())
    })

    withClock(io.nvdla_core_clk){
    }
}



