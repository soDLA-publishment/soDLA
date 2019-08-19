// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_REG_single extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         // Register control interface
//         val reg.rd_data = Output(UInt(32.W))
//         val reg.offset = Input(UInt(12.W))

//         val reg.wr_en = Input(Bool())
//         val reg.wr_data = Input(UInt(32.W))

//         // Writable register flop/trigger outputs
//         val producer = Output(Bool())

//         // Read-only register inputs
//         val consumer = Input(Bool())
//         val status_0 = Input(UInt(2.W))
//         val status_1 = Input(UInt(2.W))       
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
// // ///// Address decode
//     val nvdla_pdp_s_pointer_0_wren = (io.reg.offset === "h4".asUInt(32.W))&io.reg.wr_en
//     val nvdla_pdp_s_status_0_wren = (io.reg.offset === "h0".asUInt(32.W))&io.reg.wr_en
//     val nvdla_pdp_s_pointer_0_out = Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)
//     val nvdla_pdp_s_status_0_out = Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

// // ///// Output mux  
//     io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
//     Seq(      
//     "h4".asUInt(32.W)  -> nvdla_pdp_s_pointer_0_out,
//     "h0".asUInt(32.W)  -> nvdla_pdp_s_status_0_out
//     ))

// // ///// Register flop declarations

//     io.producer := RegEnable(io.reg.wr_data(0), false.B, nvdla_pdp_s_pointer_0_wren)
    
// }

