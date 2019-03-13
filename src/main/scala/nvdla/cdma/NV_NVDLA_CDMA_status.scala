// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDMA_status(implicit conf: cdmaConfiguration) extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         // dc2status
//         val dc2status_dat_updt = Input(Bool())
//         val dc2status_dat_entries = Input(UInt(15.W))
//         val dc2status_dat_slices = Input(UInt(14.W))

//         //img2status
//         val img2status_dat_updt = Input(Bool())
//         val img2status_dat_entries = Input(UInt(15.W))
//         val img2status_dat_slices = Input(UInt(14.W))
    
//         //sc2status
//         val sc2status_dat_updt = Input(Bool())
//         val sc2status_dat_entries = Input(UInt(15.W))
//         val sc2status_dat_slices = Input(UInt(14.W))

//         //cdma2cs
//         val cdma2sc_dat_updt = Output(Bool())
//         val cdma2sc_dat_entries = Output(UInt(15.W))
//         val cdma2sc_dat_slices = Output(UInt(14.W))

//         //status2dma
//         val cdma2sc_dat_slices = Output(UInt(14.W))
//         val status2dma_free_entries = Output(UInt(15.W))
//         val status2dma_wr_idx = Output(UInt(15.W))

//         // Writable register flop/trigger outputs
//         val producer = Output(Bool())
//         val arb_weight = Output(UInt(4.W))
//         val arb_wmb = Output(UInt(4.W))

//         // Read-only register inputs
//         val flush_done = Input(Bool())
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
//     val nvdla_cdma_s_arbiter_0_wren = (io.reg_offset === "h8".asUInt(32.W))&io.reg_wr_en
//     val nvdla_cdma_s_cbuf_flush_status_0_wren = (io.reg_offset === "hc".asUInt(32.W))&io.reg_wr_en
//     val nvdla_cdma_s_pointer_0_wren = (io.reg_offset === "h4".asUInt(32.W))&io.reg_wr_en
//     val nvdla_cdma_s_status_0_wren = (io.reg_offset === "h0".asUInt(32.W))&io.reg_wr_en

//     val nvdla_cdma_s_arbiter_0_out = Cat("b0".asUInt(12.W), io.arb_wmb, "b0".asUInt(15.W), io.producer)
//     val nvdla_cdma_s_cbuf_flush_status_0_out = Cat("b0".asUInt(31.W), io.flush_done)
//     val nvdla_cdma_s_pointer_0_out = Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)
//     val nvdla_cdma_s_status_0_out = Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

// // ///// Output mux  
//     io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
//     Seq(      
//     "h8".asUInt(32.W)  -> nvdla_cdma_s_arbiter_0_out,
//     "hc".asUInt(32.W)  -> nvdla_cdma_s_cbuf_flush_status_0_out,
//     "h4".asUInt(32.W)  -> nvdla_cdma_s_pointer_0_out,
//     "h0".asUInt(32.W)  -> nvdla_cdma_s_status_0_out
//     ))

// // ///// Register flop declarations
//     val arb_weight_out = RegInit("b1111".asUInt(4.W))
//     val arb_wmb_out = RegInit("b0011".asUInt(4.W))
//     val producer_out = RegInit(false.B)

//     when(nvdla_cdma_s_arbiter_0_wren){
//         arb_weight_out:= io.reg_wr_data(3, 0)
//     } 
//     when(nvdla_cdma_s_arbiter_0_wren){
//         arb_wmb_out:= io.reg_wr_data(19, 16)
//     }  
//     when(nvdla_cdma_s_pointer_0_wren){
//         producer_out:= io.reg_wr_data(0)
//     }
    
//     io.arb_weight := arb_weight_out
//     io.arb_wmb := arb_wmb_out
//     io.producer := producer_out
    
// }
