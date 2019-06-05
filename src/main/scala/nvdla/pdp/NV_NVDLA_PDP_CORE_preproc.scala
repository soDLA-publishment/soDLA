// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_CORE_preproc(implicit val conf: pdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         //sdp2pdp
//         val sdp2pdp_valid = Input(Bool())
//         val sdp2pdp_ready = Output(Bool())
//         val sdp2pdp_pd = Input(UInt(((conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT)+14).W))
//         //pre2cal1d
//         val pre2cal1d_pvld = Output(Bool())
//         val pre2cal1d_prdy = Input(Bool())
//         val pre2cal1d_pd = Output(UInt(((conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT)+14).W))
//         //config  
//         val reg2dp_cube_in_channel = Input(UInt(13.W))
//         val reg2dp_cube_in_height = Input(UInt(13.W))
//         val reg2dp_cube_in_width = Input(UInt(13.W))
//         val reg2dp_flying_mode = Input(Bool())
//         val reg2dp_op_en = Output(Bool())

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
// withClock(io.nvdla_core_clk){
//     /////////////////////////////////////////////////////////////////
//     //Data path pre process
//     //--------------------------------------------------------------
//     val onfly_en = (io.reg2dp_flying_mode === false.B );
//     //////////////////////////////
//     //sdp to pdp layer end info
//     //////////////////////////////
//     val sdp2pdp_c_cnt = RegInit("b0".asUInt(5.W))
//     val sdp2pdp_c_end = 
//     when(load_din){
//        when(sdp2pdp_c_end) 
//     }

// }}


// object NV_NVDLA_PDP_nanDriver extends App {
//   implicit val conf: pdpConfiguration = new pdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_nan())
// }