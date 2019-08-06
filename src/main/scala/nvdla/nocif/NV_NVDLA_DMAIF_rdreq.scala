// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver

// class NV_NVDLA_DMAIF_rdreq(implicit conf: cdmaConfiguration) extends Module {
//     val DMABW  = conf.NVDLA_MEM_ADDRESS_WIDTH + 15
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val reg2dp_src_ram_type = Input(Bool())

//         val mcif_rd_req_pd = Output(UInt(DMABW.W))
//         val mcif_rd_req_valid = Output(Bool())
//         val mcif_rd_req_ready = Input(Bool())

//         val dmaif_rd_req_pd = Input(UInt(DMABW.W))
//         val dmaif_rd_req_vld = Input(Bool())
//         val dmaif_rd_req_rdy = Output(Bool())

//     })
//     val cvio = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(IO(new Bundle{

//         val cvif_rd_req_pd = Output(UInt(DMABW.W))
//         val cvif_rd_req_valid = Output(Bool())
//         val cvif_rd_req_ready = Input(Bool())

//     })) else None
//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │
//     //       │                 │
//     //       └───┐         ┌───┘
//     //           │         │
//     //           │         │
//     //           │         │
//     //           │         └──────────────┐
//     //           │                        │
//     //           │                        ├─┐
//     //           │                        ┌─┘    
//     //           │                        │
//     //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //             │ ─┤ ─┤       │ ─┤ ─┤         
//     //             └──┴──┘       └──┴──┘
// withClock(io.nvdla_core_clk){
//     val mc_dma_rd_req_rdy = Wire(Bool())
//     val rd_req_rdyi = Wire(Bool())

//     val dma_rd_req_ram_type = io.reg2dp_src_ram_type
//     val mc_dma_rd_req_vld = io.dmaif_rd_req_vld & (dma_rd_req_ram_type === 1.U)
//     val mc_rd_req_rdyi = mc_dma_rd_req_rdy & (dma_rd_req_ram_type === 1.U)
//     io.dmaif_rd_req_rdy := rd_req_rdyi

//     val is_pipe0 = Module{new NV_NVDLA_IS_pipe(DMABW)}
//     is_pipe0.io.clk := io.nvdla_core_clk
//     is_pipe0.io.ri := io.mcif_rd_req_ready
//     is_pipe0.io.vi := mc_dma_rd_req_vld
//     is_pipe0.io.di := io.dmaif_rd_req_pd
//     val mc_dma_rd_req_rdy_f = is_pipe0.io.ro
//     io.mcif_rd_req_valid := is_pipe0.io.vo
//     io.mcif_rd_req_pd := is_pipe0.io.dout

//     mc_dma_rd_req_rdy := mc_dma_rd_req_rdy_f

//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         val cv_dma_rd_req_rdy = Wire(Bool())
//         val cv_dma_rd_req_vld = io.dmaif_rd_req_vld & (dma_rd_req_ram_type === 0.U)
//         val cv_rd_req_rdyi = cv_dma_rd_req_rdy & (dma_rd_req_ram_type === 0.U)
//         rd_req_rdyi := mc_rd_req_rdyi | cv_rd_req_rdyi

//         val cv_dma_rd_req_pd = io.dmaif_rd_req_pd
//         val is_pipe1 = Module{new NV_NVDLA_IS_pipe(DMABW)}
//         is_pipe1.io.clk := io.nvdla_core_clk
//         is_pipe1.io.ri := cvio.get.cvif_rd_req_ready
//         is_pipe1.io.vi := cv_dma_rd_req_vld
//         is_pipe1.io.di := cv_dma_rd_req_pd
//         val cv_dma_rd_req_rdy_f = is_pipe1.io.ro
//         cvio.get.cvif_rd_req_valid := is_pipe1.io.vo
//         cvio.get.cvif_rd_req_pd := is_pipe1.io.dout

//         cv_dma_rd_req_rdy := cv_dma_rd_req_rdy_f

//     }
//     else{
//         rd_req_rdyi := mc_rd_req_rdyi
//     }
    
// }}

// object NV_NVDLA_DMAIF_rdreqDriver extends App {
//   implicit val conf: cdmaConfiguration = new cdmaConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_DMAIF_rdreq())
// }
