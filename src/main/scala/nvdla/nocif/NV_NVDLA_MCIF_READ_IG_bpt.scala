// package nvdla
// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_MCIF_READ_IG_bpt(implicit conf: cscConfiguration) extends Module {
//     val io = IO(new Bundle{
//         //general clock
//         val nvdla_core_clk = Input(Clock())

//         //dma2bpt
//         val dma2bpt_req_valid = Input(Bool())
//         val dma2bpt_req_ready = Output(Bool())
//         val dma2bpt_req_pd = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))
//         val dma2bpt_cdt_lat_fifo_pop = Input(Bool())

//         //bpt2arb
//         val bpt2arb_req_valid = Output(Bool())
//         val bpt2arb_req_ready = Input(Bool())
//         val bpt2arb_req_pd = Output(UInt(conf.NVDLA_DMA_RD_IG_PW.W))
//         val tieoff_axid = Input(UInt(3.W))
//         val tieoff_lat_fifo_depth = Input(UInt(8.W))

//     })
//     withClock(io.nvdla_core_clk){
// }
// }

// class NV_NVDLA_MCIF_READ_IG_BPT_pipe_p1(implicit conf: cscConfiguration) extends Module {
//     val io = IO(new Bundle{
//         //general clocl
//         val nvdla_core_clk = Input(Clock())
        
//         //dma2bpt_
//         val dma2bpt_req_pd = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))
//         val dma2bpt_req_valid = Input(Bool())
//         val dma2bpt_req_ready = Output(Bool())
        
//         //in
//         val in_pd_p = Output(UInt(conf.NVDLA_DMA_RD_REQ.W))
//         val in_vld_p = Output(Bool())
//         val in_rdy_p = Input(Bool())
//     })
//     withClock(io.nvdla_core_clk){
// }
// }

// class NV_NVDLA_MCIF_READ_IG_BPT_pipe_p2(implicit conf: cscConfiguration) extends Module {
//     val io = IO(new Bundle{
//         //general clocl
//         val nvdla_core_clk = Input(Clock())

//         //in
//         val in_pd_p = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))
//         val in_vld_p = Input(Bool())
//         val in_rdy_p = Output(Bool())
//         val in_pd = Output(UInt(conf.NVDLA_DMA_RD_REQ.W))
//         val in_vld = Output(Bool())
//         val in_rdy = Input(Bool())

//     })

// withClock(io.nvdla_core_clk){
// }}