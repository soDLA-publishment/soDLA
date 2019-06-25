// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_RDMA_ig extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         val op_load = Input(Bool())
//         val dma_rd_req_rdy = Input(Bool())
//         val dma_rd_req_pd = Output(UInt(79.W))
//         val dma_rd_req_vld = Output(Bool())
//         val ig2cq_prdy = Input(Bool())
//         val ig2cq_pd = Output(UInt(16.W))
//         val ig2cq_pvld = Output(Bool())
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_winograd = Input(Bool())
//         val reg2dp_channel = Input(UInt(13.W))
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_rdma_data_mode = Input(Bool())
//         val reg2dp_rdma_data_size = Input(Bool())
//         val reg2dp_rdma_data_use = Input(UInt(2.W))
//         val reg2dp_base_addr_high = Input(UInt(32.W))
//         val reg2dp_base_addr_low = Input(UInt(32.W))
//         val reg2dp_line_stride = Input(UInt(32.W))
//         val reg2dp_surface_stride = Input(UInt(32.W))
//         val reg2dp_perf_dma_en = Input(Bool())
//         val dp2reg_rdma_stall = Output(UInt(32.W))
//     })
// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │          
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 
//     withClock(io.nvdla_core_clk){
//     // Address decode

//     val nvdla_sdp_rdma_s_pointer_0_wren = (io.reg_offset === "h4".asUInt(32.W))&io.reg_wr_en
//     val nvdla_sdp_rdma_s_status_0_wren = (io.reg_offset === "h0".asUInt(32.W))&io.reg_wr_en
    
//     val nvdla_sdp_rdma_s_pointer_0_out = Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)
//     val nvdla_sdp_rdma_s_status_0_out = Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

//     // Output mux
   
//     io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
//     Seq(      
//     "h4".asUInt(32.W)  -> nvdla_sdp_rdma_s_pointer_0_out,
//     "h0".asUInt(32.W)  -> nvdla_sdp_rdma_s_status_0_out 
//     ))

//     // Register flop declarations

//     val producer_out = RegInit(false.B)

//     when(nvdla_sdp_rdma_s_pointer_0_wren){
//         producer_out:= io.reg_wr_data(0)
//     }
        
//     io.producer := producer_out

// }}

