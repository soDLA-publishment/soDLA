// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._


// class NV_NVDLA_ssync3d_c extends Module {
//     val io = IO(new Bundle {
//         val i_clk = Input(Clock())
//         val i_rstn = Input(Bool())
//         val sync_i = Input(Bool())
//         val o_clk = Input(Clock())
//         val o_rstn = Input(Bool())
//         val sync_o = Output(Bool())
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
// val sync_i_o_clk_sync_0 = Module(new p_STRICTSYNC3DOTM_C_PPP)
// sync_i_o_clk_sync_0.io.SRC_CLK := io.i_clk
// sync_i_o_clk_sync_0.io.SRC_CLRN := io.i_rstn
// sync_i_o_clk_sync_0.io.SRC_D_NEXT := io.sync_i
// sync_i_o_clk_sync_0.io.SRC_D := 




// }}