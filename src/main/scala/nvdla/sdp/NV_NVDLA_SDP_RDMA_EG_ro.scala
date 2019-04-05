// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_RDMA_EG_ro extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val pwrbus_ram_pd = Input(UInt(32.W))

//         val sdp_rdma2dp_valid = Output(Bool())
//         val sdp_rdma2dp_ready = Input(Bool())
// modify yourself

// modify yourself
// modify yourself
// modify yourself
// modify yourself
//         val rod_wr_mask = Input(UInt(4.W))
//         val rod_wr_vld = Input(Bool())
//         val rod_wr_rdy = Output(Bool())
//         val roc_wr_pd = Input(UInt(2.W))
//         val roc_wr_vld = Input(Bool())
//         val roc_wr_rdy = Output(Bool())
//         val cfg_dp_8 = Input(Bool())
//         val cfg_dp_size_1byte = Input(Bool())
//         val cfg_mode_per_element = Input(Bool())
// modify yourself
//         val cfg_mode_multi_batch = Input(Bool())
//         val reg2dp_batch_number = Input(UInt(5.W))

//         val reg2dp_channel = Input(UInt(13.W))
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val layer_end = Output(Bool())
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