// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CSC_single_reg extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         // Register control interface
//         val reg_control = new reg_control_if

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
//     val nvdla_csc_s_pointer_0_wren = (io.reg_control.offset === "h4".asUInt(32.W))&io.reg_control.wr_en
//     val nvdla_csc_s_status_0_wren = (io.reg_control.offset === "h0".asUInt(32.W))&io.reg_control.wr

// // ///// Output mux  
//     io.reg_rd_data := MuxLookup(io.reg_control.offset, "b0".asUInt(32.W), 
//     Seq(  
//     //nvdla_csc_s_pointer_0_out    
//     "h4".asUInt(32.W)  -> Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer),
//     //nvdla_csc_s_status_0_out
//     "h0".asUInt(32.W)  -> Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)
//     ))

// // ///// Register flop declarations
//     io.producer := RegEnable(io.reg_control.wr_data(0), false.B, nvdla_csc_s_pointer_0_wren)
    
// }

