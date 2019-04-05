// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_MRDMA_EG_cmd extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val pwrbus_ram_pd = Input(Bool())
//         val eg_done = Input(Bool())
//         val cq2eg_pvld = Input(Bool())
//         val cq2eg_prdy = Output(Bool())
//         val cq2eg_pd = Input(UInt(14.W))
//         val cmd2dat_spt_pvld = Output(Bool())
//         val cmd2dat_spt_prdy = Input(Bool())
//         val cmd2dat_spt_pd = Output(UInt(13.W))
//         val cmd2dat_dma_pvld = Output(Bool())
//         val cmd2dat_dma_prdy = Input(Bool())
//         val cmd2dat_dma_pd = Output(UInt(15.W))
//         val reg2dp_height = Input(Bool())
//         val reg2dp_in_precision = Input(Bool())
//         val reg2dp_proc_precision = Input(Bool())
//         val reg2dp_width = Input(Bool())
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