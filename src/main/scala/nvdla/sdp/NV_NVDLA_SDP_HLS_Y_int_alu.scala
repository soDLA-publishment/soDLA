// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_int_alu extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val alu_in_pvld = Input(Bool())
//         val alu_in_prdy = Output(Bool())
//         val alu_data_in = Input(UInt(32.W))

//         val chn_alu_op_pvld = Input(Bool())
//         val chn_alu_op_prdy = Output(Bool())
//         val chn_alu_op = Input(UInt(32.W))

//         val alu_out_pvld = Output(Bool())
//         val alu_out_prdy = Input(Bool())
//         val alu_data_out = Output(UInt(32.W))

//         val cfg_alu_algo = Input(UInt(2.W))
//         val cfg_alu_bypass = Input(Bool())
//         val cfg_alu_op = Input(UInt(32.W))
//         val cfg_alu_src = Input(Bool())
        
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
// withClock(io.nvdla_core_clk){

//     val alu_sync_prdy = Wire(Bool())
//     val y_alu_sync2data = Module{new NV_NVDLA_SDP_HLS_sync2data(32, 32)}
//     y_alu_sync2data.io.chn1_en := io.cfg_alu_src & !io.cfg_alu_bypass
//     y_alu_sync2data.io.chn2_en := !io.cfg_alu_bypass
//     y_alu_sync2data.io.chn1_in_pvld := io.chn_alu_op_pvld
//     val chn_alu_op_prdy = y_alu_sync2data.io.chn1_in_prdy
//     y_alu_sync2data.io.chn2_in_pvld := io.alu_in_pvld
//     val alu_in_srdy = y_alu_sync2data.io.chn2_in_prdy
//     y_alu_sync2data.io.data2_in := io.alu_data_in
//     val alu_sync_pvld = y_alu_sync2data.io.chn_out_pvld
//     y_alu_sync2data.io.chn_out_prdy := alu_sync_prdy
//     y_alu_sync2data.io.data1_in := io.chn_alu_op


// }}


// object NV_NVDLA_SDP_HLS_Y_int_cvtDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_cvt)
// }