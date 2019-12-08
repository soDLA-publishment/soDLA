// package nvdla

// import chisel3._
// import chisel3.util._


// class NV_NVDLA_NOCIF_DRAM_WRITE_eg(implicit conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())      

//         val mcif2client_wr_rsp_complete = Output(Vec(conf.NVDLA_NUM_DMA_WRITE_CLIENTS, Bool()))
//         //cq_rd
//         val cq_rd_pd = Flipped(Vec(conf.NVDLA_NUM_DMA_WRITE_CLIENTS, DecoupledIO(UInt(conf.MCIF_WRITE_CQ_WIDTH.W))))
//         //noc2mcif
//         val noc2mcif_axi_b = Flipped(Decoupled(new nocif_axi_wr_response_if))
//         //eg2ig
//         val eg2ig_axi_len = ValidIO(UInt(2.W))
//     })
//  //
//  //          ┌─┐       ┌─┐
//  //       ┌──┘ ┴───────┘ ┴──┐
//  //       │                 │
//  //       │       ───       │
//  //       │  ─┬┘       └┬─  │
//  //       │                 │
//  //       │       ─┴─       │
//  //       │                 │
//  //       └───┐         ┌───┘
//  //           │         │
//  //           │         │
//  //           │         │
//  //           │         └──────────────┐
//  //           │                        │
//  //           │                        ├─┐
//  //           │                        ┌─┘
//  //           │                        │
//  //           └─┐  ┐  ┌───────┬──┐  ┌──┘
//  //             │ ─┤ ─┤       │ ─┤ ─┤
//  //             └──┴──┘       └──┴──┘
// withClock(io.nvdla_core_clk){

//     val cq_vld_rdy = VecInit((0 to conf.NVDLA_NUM_DMA_WRITE_CLIENTS-1) map{i => io.cq_rd_pd(i).valid & io.cq_rd_pd(i).ready})

//     val cq_vld = cq_vld_rdy.asUInt.orR | false.B

//     io.noc2mcif_axi_b.ready := !cq_vld

//     val iflop_axi_vld = RegInit(false.B)
//     when(io.noc2mcif_axi_b.ready){
//         iflop_axi_vld := io.noc2mcif_axi_b.valid
//     }

//     val iflop_axi_axid = RegInit(0.U(3.W))
//     when(io.noc2mcif_axi_b.valid & io.noc2mcif_axi_b.ready){
//         iflop_axi_axid := io.noc2mcif_axi_b.id(2,0)
//     }

// // EG===Contect Qeueu    
//     val dma_vld = Vec(conf.NVDLA_NUM_DMA_WRITE_CLIENTS, Wire(Bool()))
//     val cq_rd_require_ack = Vec(conf.NVDLA_NUM_DMA_WRITE_CLIENTS, Wire(Bool()))
//     val cq_rd_len = Vec(conf.NVDLA_NUM_DMA_WRITE_CLIENTS, Wire(UInt(2.W)))
//     val mcif2client_wr_rsp_complete_out = Vec(conf.NVDLA_NUM_DMA_WRITE_CLIENTS, RegInit(false.B))

//     for(i <- 0 to 4){
//         dma_vld(i) := iflop_axi_vld & (iflop_axi_axid === i.U)
//     }
//     for(i <- 5 to (conf.NVDLA_NUM_DMA_WRITE_CLIENTS - 1)){
//         dma_vld(i) := iflop_axi_vld & (iflop_axi_axid === 0.U)
//     }
//     for(i <- 0 to (conf.NVDLA_NUM_DMA_WRITE_CLIENTS - 1)){
//         io.cq_rd_pd(i).ready := dma_vld(i)
//         cq_rd_require_ack(i) := io.cq_rd_pd(i)(0)
//         cq_rd_len(i) := io.cq_rd_pd(i)(2,1)
//         mcif2client_wr_rsp_complete_out(i) := dma_vld(i) & io.cq_rd_pd(i).valid & cq_rd_require_ack(i)
//         io.mcif2client_wr_rsp_complete(i) := mcif2client_wr_rsp_complete_out(i)
//     }

// // EG2IG outstanding Counting
//     io.eg2ig_axi_len.valid := iflop_axi_vld & io.noc2mcif_axi_b.ready

//     for(i <- 0 to (conf.NVDLA_NUM_DMA_WRITE_CLIENTS - 1)){
//         when(dma_vld(i)){
//             io.eg2ig_axi_len.bits := cq_rd_len(i)
//         }
//     }

// }}

