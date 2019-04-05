package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_RDMA_eg extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        val pwrbus_ram_pd = Input(UInt(32.W))
        val op_load = Input(Bool())
        val eg_done = Output(Bool())
        val cq2eg_pd = Input(UInt(16.W))
        val cq2eg_pvld = Input(Bool())
        val cq2eg_prdy = Output(Bool())
modify yourself
        val lat_fifo_rd_pvld = Input(Bool())
        val lat_fifo_rd_prdy = Output(Bool())
        val dma_rd_cdt_lat_fifo_pop = Output(Bool())
modify yourself
        val sdp_rdma2dp_alu_valid = Output(Bool())
        val sdp_rdma2dp_alu_ready = Input(Bool())
modify yourself
        val sdp_rdma2dp_mul_valid = Output(Bool())
        val sdp_rdma2dp_mul_ready = Input(Bool())
        val reg2dp_batch_number = Input(UInt(5.W))
        val reg2dp_channel = Input(UInt(13.W))
        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_width = Input(UInt(13.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_out_precision = Input(UInt(2.W))
        val reg2dp_rdma_data_mode = Input(Bool())
        val reg2dp_rdma_data_size = Input(Bool())
        val reg2dp_rdma_data_use = Input(UInt(2.W))
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
    // Address decode

    val nvdla_sdp_rdma_s_pointer_0_wren = (io.reg_offset === "h4".asUInt(32.W))&io.reg_wr_en
    val nvdla_sdp_rdma_s_status_0_wren = (io.reg_offset === "h0".asUInt(32.W))&io.reg_wr_en
    
    val nvdla_sdp_rdma_s_pointer_0_out = Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)
    val nvdla_sdp_rdma_s_status_0_out = Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

    // Output mux
   
    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
    "h4".asUInt(32.W)  -> nvdla_sdp_rdma_s_pointer_0_out,
    "h0".asUInt(32.W)  -> nvdla_sdp_rdma_s_status_0_out 
    ))

    // Register flop declarations

    val producer_out = RegInit(false.B)

    when(nvdla_sdp_rdma_s_pointer_0_wren){
        producer_out:= io.reg_wr_data(0)
    }
        
    io.producer := producer_out

}}

