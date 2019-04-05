// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_MRDMA_EG_din extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val pwrbus_ram_pd = Input(UInt(32.W))
//         val reg2dp_src_ram_type = Input(Bool())
//         val dma_rd_rsp_ram_type = Output(Bool())
// modify yourself
//         val dma_rd_rsp_vld = Input(Bool())
//         val dma_rd_rsp_rdy = Output(Bool())
//         val dma_rd_cdt_lat_fifo_pop = Output(Bool())
//         val cmd2dat_spt_pd = Input(UInt(13.W))
//         val cmd2dat_spt_pvld = Input(Bool())
//         val cmd2dat_spt_prdy = Output(Bool())
//         val pfifo0_rd_prdy = Input(Bool())
//         val pfifo1_rd_prdy = Input(Bool())
//         val pfifo2_rd_prdy = Input(Bool())
//         val pfifo3_rd_prdy = Input(Bool())
// modify yourself
//         val pfifo0_rd_pvld = Output(Bool())
// modify yourself
//         val pfifo1_rd_pvld = Output(Bool())
// modify yourself
//         val pfifo2_rd_pvld = Output(Bool())
// modify yourself
//         val pfifo3_rd_pvld = Output(Bool())
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
// withClock(io.nvdla_core_clk)
// }