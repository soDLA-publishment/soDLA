package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_RDMA_REG_single extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg.offset = Input(UInt(12.W))

        val reg_wr_en = Input(Bool())
        val reg_wr_data = Input(UInt(32.W))

        // Writable register flop/trigger outputs
        val producer = Output(Bool())

        // Read-only register inputs
        val consumer = Input(Bool())
        val status_0 = Input(UInt(2.W))
        val status_1 = Input(UInt(2.W))       
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
// ///// Address decode
    val nvdla_pdp_rdma_s_pointer_0_wren = (io.reg.offset === "h4".asUInt(32.W))&io.reg_wr_en
    val nvdla_pdp_rdma_s_status_0_wren = (io.reg.offset === "h0".asUInt(32.W))&io.reg_wr_en

// ///// Output mux  
    io.reg_rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(  
    //nvdla_pdp_rdma_s_pointer_0_out    
    "h4".asUInt(32.W)  -> Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer),
    //nvdla_pdp_rdma_s_status_0_out
    "h0".asUInt(32.W)  -> Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)
    ))

// ///// Register flop declarations
    val producer_out = RegInit(false.B)

    when(nvdla_pdp_rdma_s_pointer_0_wren){
        producer_out:= io.reg_wr_data(0)
    }
        
    io.producer := producer_out
    
}

