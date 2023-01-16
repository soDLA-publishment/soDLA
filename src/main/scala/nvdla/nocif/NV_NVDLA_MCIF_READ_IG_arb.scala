 package nvdla

 import chisel3._
 import chisel3.experimental._
 import chisel3.util._

@chiselName
 class NV_NVDLA_MCIF_READ_IG_arb(implicit conf:nvdlaConfig) extends Module {
     val io = IO(new Bundle {
         //general clock
         val nvdla_core_clk = Input(Clock())
       
        // bpt2arb
        val bpt2arb_req_pd = Flipped(Vec(conf.RDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_RD_IG_PW.W))))

        // arb2spt
        val arb2spt_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_IG_PW.W))

        val reg2dp_rd_weight = Input(Vec(conf.RDMA_NUM, UInt(8.W)))
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

    val arb_src_pd = Wire(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_IG_PW.W)))
    val arb_src_rdy = Wire(Vec(conf.RDMA_NUM, Bool()))
    val arb_src_vld = Wire(Vec(conf.RDMA_NUM, Bool()))

    val pipe_p = Array.fill(conf.RDMA_NUM){Module(new NV_NVDLA_BC_OS_pipe(conf.NVDLA_DMA_RD_IG_PW))}
    for(i <- 0 to conf.RDMA_NUM-1){
        pipe_p(i).io.clk := io.nvdla_core_clk

        pipe_p(i).io.vi := io.bpt2arb_req_pd(i).valid
        io.bpt2arb_req_pd(i).ready := pipe_p(i).io.ro
        pipe_p(i).io.di := io.bpt2arb_req_pd(i).bits

        arb_src_vld(i):= pipe_p(i).io.vo
        pipe_p(i).io.ri := arb_src_rdy(i)
        arb_src_pd(i) := pipe_p(i).io.dout
    }


    val gnt_busy = Wire(Bool())
    val src_gnt = Wire(Vec(conf.RDMA_NUM, Bool()))
    val u_read_ig_arb = Module(new NV_NVDLA_arb(n = conf.RDMA_MAX_NUM, wt_width = 8, io_gnt_busy = true))
    u_read_ig_arb.io.clk := io.nvdla_core_clk
    u_read_ig_arb.io.gnt_busy.get := gnt_busy
    for(i<- 0 to conf.RDMA_NUM-1) {
        u_read_ig_arb.io.req(i) := arb_src_vld(i)
        u_read_ig_arb.io.wt(i) := io.reg2dp_rd_weight(i)
        src_gnt(i) := u_read_ig_arb.io.gnt(i)
    }
    for(i<- conf.RDMA_NUM to conf.RDMA_MAX_NUM-1){
        u_read_ig_arb.io.req(i) := false.B
        u_read_ig_arb.io.wt(i) := 0.U(8.W)
    }

    arb_src_rdy := src_gnt

    // MUX OUT
    val arb_pd = WireInit("b0".asUInt(conf.NVDLA_DMA_RD_IG_PW.W))
    for(i <- 0 to conf.RDMA_NUM-1){
        when(src_gnt(i)){
            arb_pd := arb_src_pd(i)
        }  
    }

    val arb_out_vld = src_gnt.asUInt.orR
    val arb_out_rdy = Wire(Bool())
    gnt_busy := ~arb_out_rdy
    val arb_out_pd = arb_pd

    val pipe_out = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_IG_PW))
    pipe_out.io.clk := io.nvdla_core_clk
    pipe_out.io.vi := arb_out_vld
    arb_out_rdy := pipe_out.io.ro
    pipe_out.io.di := arb_pd
    io.arb2spt_req_pd.valid := pipe_out.io.vo
    pipe_out.io.ri := io.arb2spt_req_pd.ready
    io.arb2spt_req_pd.bits := pipe_out.io.dout
}
}

 object NV_NVDLA_MCIF_READ_IG_arbDriver extends App {
     implicit val conf: nvdlaConfig = new nvdlaConfig
     chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_READ_IG_arb())
 }
