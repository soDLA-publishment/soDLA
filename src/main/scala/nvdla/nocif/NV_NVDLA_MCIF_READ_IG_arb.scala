 package nvdla

 import chisel3._
 import chisel3.experimental._
 import chisel3.util._


 class NV_NVDLA_MCIF_READ_IG_arb(implicit conf:nvdlaConfig) extends Module {
     val io = IO(new Bundle {
         //general clock
         val nvdla_core_clk = Input(Clock())
         val nvdla_core_rstn = Input(Bool())

         val reg2dp_rw_weight = Input(Vec(conf.RDMA_NUM, UInt(8.W)))

        // bpt2arb
        val bpt2arb_req_valid   = Input(Vec(conf.RDMA_NUM, Bool()))
        val bpt2arb_req_ready  = Output(Vec(conf.RDMA_NUM, Bool()))
        val bpt2arb_req_pd     = Input(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_IG_PW.W)))


        // arb2spt
        val arb2spt_req_valid = Output(Bool())
        val arb2spt_req_ready = Input(Bool())
        val arb2spt_req_pd = Output(UInt(conf.NVDLA_DMA_RD_IG_PW.W))
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

         val pipe_p = Array.fill(conf.RDMA_NUM) { Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_IG_PW))}
         for(i <- 0 until conf.RDMA_NUM){
             pipe_p(i).io.clk := io.nvdla_core_clk
             pipe_p(i).io.di := io.bpt2arb_req_pd(i)
             pipe_p(i).io.vi := io.bpt2arb_req_valid(i)
             io.bpt2arb_req_ready(i) := pipe_p(i).io.ro

             arb_src_pd(i) := pipe_p(i).io.dout
             arb_src_vld(i):= pipe_p(i).io.vo
             pipe_p(i).io.ri := arb_src_rdy(i)
         }


         val gnt_busy = Wire(Bool())

         val u_read_ig_arb = Module(new read_ig_arb)
         u_read_ig_arb.io.clk := io.nvdla_core_clk
         for(i<-0 until conf.RDMA_NUM) {
             u_read_ig_arb.io.req(i) := arb_src_vld(i)
             u_read_ig_arb.io.wt(i) := io.reg2dp_rw_weight(i)
             arb_src_rdy(i) := u_read_ig_arb.io.gnt(i)
         }
         u_read_ig_arb.io.gnt_busy := gnt_busy
         u_read_ig_arb.io.wt(8) := 0.U
         u_read_ig_arb.io.wt(9) := 0.U
         u_read_ig_arb.io.req(8) := false.B
         u_read_ig_arb.io.req(9) := false.B


         val arb_pd = MuxLookup(u_read_ig_arb.io.gnt(conf.RDMA_NUM-1, 0), "b0".asUInt(conf.RDMA_NUM.W),
                       Array(
                           "b00000001".asUInt(conf.RDMA_NUM.W) -> io.bpt2arb_req_pd(0),
                           "b00000010".asUInt(conf.RDMA_NUM.W) -> io.bpt2arb_req_pd(1),
                           "b00000100".asUInt(conf.RDMA_NUM.W) -> io.bpt2arb_req_pd(2),
                           "b00001000".asUInt(conf.RDMA_NUM.W) -> io.bpt2arb_req_pd(3),
                           "b00010000".asUInt(conf.RDMA_NUM.W) -> io.bpt2arb_req_pd(4),
                           "b00100000".asUInt(conf.RDMA_NUM.W) -> io.bpt2arb_req_pd(5),
                           "b01000000".asUInt(conf.RDMA_NUM.W) -> io.bpt2arb_req_pd(6),
                           "b10000000".asUInt(conf.RDMA_NUM.W) -> io.bpt2arb_req_pd(7),
                       ))

         val pipe_out = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_IG_PW))
         pipe_out.io.clk := io.nvdla_core_clk
         pipe_out.io.di := arb_pd
         pipe_out.io.vi := (u_read_ig_arb.io.gnt).asUInt().orR
         gnt_busy := !pipe_out.io.ro

         io.arb2spt_req_pd := pipe_out.io.dout
         io.arb2spt_req_valid := pipe_out.io.vo
         pipe_out.io.ri := io.arb2spt_req_ready
     }
 }

 object NV_NVDLA_MCIF_READ_IG_arbDriver extends App {
     implicit val conf: nvdlaConfig = new nvdlaConfig
     chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_READ_IG_arb())
 }
