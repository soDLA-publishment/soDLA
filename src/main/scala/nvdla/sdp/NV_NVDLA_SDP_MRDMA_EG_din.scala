// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_MRDMA_EG_din extends Module {
//    val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         //dma_rd
//         val dma_rd_rsp_vld = Input(Bool())
//         val dma_rd_rsp_rdy = Output(Bool())
//         val dma_rd_rsp_pd = Input(UInt(conf.NVDLA_DMA_RD_RSP.W))
//         val dma_rd_cdt_lat_fifo_pop = Output(Bool())             
//         val dma_rd_rsp_ram_type = Output(Bool())

//         //cmd2dat
//         val cmd2dat_spt_pvld = Input(Bool())
//         val cmd2dat_spt_prdy = Output(Bool())
//         val cmd2dat_spt_pd = Input(UInt(13.W))

//         //pfifo
//         val pfifo0_rd_pvld = Output(Bool())
//         val pfifo0_rd_prdy = Input(Bool())
//         val pfifo0_rd_pd = Output(UInt(conf.AM_DW.W))

//         val pfifo1_rd_pvld = Output(Bool())
//         val pfifo1_rd_prdy = Input(Bool())
//         val pfifo1_rd_pd = Output(UInt(conf.AM_DW.W))

//         val pfifo2_rd_pvld = Output(Bool())
//         val pfifo2_rd_prdy = Input(Bool())
//         val pfifo2_rd_pd = Output(UInt(conf.AM_DW.W))

//         val pfifo3_rd_pvld = Output(Bool())
//         val pfifo3_rd_prdy = Input(Bool())
//         val pfifo3_rd_pd = Output(UInt(conf.AM_DW.W))

//         val reg2dp_src_ram_type = Input(Bool())

//     })
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
//     //==============
//     // Latency FIFO to buffer return DATA
//     //==============
//     val lat_ecc_rd_pvld = Wire(Bool())
//     val lat_ecc_rd_prdy = Wire(Bool())
    
//     val dma_rd_rsp_ram_type = io.reg2dp_src_ram_type
//     val dma_rd_cdt_lat_fifo_pop = lat_ecc_rd_pvld & lat_ecc_rd_prdy

//     val u_lat_fifo = Module(new NV_NVDLA_SDP_MRDMA_EG_lat_fifo(conf.NVDLA_VMOD_SDP_MRDMA_LATENCY_FIFO_DEPTH, conf.NVDLA_DMA_RD_RSP))
//     u_lat_fifo.io.clk := 
// }}