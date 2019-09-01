// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_nan(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val cdp_rdma2dp_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+25).W))
//         val cdp_rdma2dp_valid = Input(Bool())
//         // val dp2reg_done = Input(Bool())
//         val nan_preproc_prdy = Input(Bool())
// //input   [1:0] reg2dp_input_data_type;
//         // val reg2dp_nan_to_zero = Input(Bool())
//         val reg2dp_op_en = Input(Bool())
//         val cdp_rdma2dp_ready = Output(Bool())
//         // val dp2reg_inf_input_num = Output(UInt(32.W))
//         // val dp2reg_nan_input_num = Output(UInt(32.W))
//         val nan_preproc_pd = Output(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+25).W))
//         val nan_preproc_pvld = Output(Bool())
//     })

// withClock(io.nvdla_core_clk){

//     val din_pvld_d1 = RegInit(false.B)
//     val din_prdy_d1 = Wire(Bool())
//     val waiting_for_op_en = RegInit(false.B)
//     val cdp_rdma2dp_ready_f = (~din_pvld_d1 | din_prdy_d1) & (~waiting_for_op_en)
//     io.cdp_rdma2dp_ready := cdp_rdma2dp_ready_f
//     val load_din = io.cdp_rdma2dp_valid & cdp_rdma2dp_ready_f
    
// //////////////////////////////////////////////////////////////////////
// //waiting for op_en
// //////////////////////////////////////////////////////////////////////

//     val op_en_d1 = RegInit(false.B)
//     op_en_d1 := io.reg2dp_op_en

//     val op_en_load = io.reg2dp_op_en & (~op_en_d1)

//     val layer_end = (
//         Cat(
//             io.cdp_rdma2dp_pd(
//                 conf.NVDLA_CDP_THROUGHPUT * conf.NVDLA_BPE + 16, 
//                 conf.NVDLA_CDP_THROUGHPUT * conf.NVDLA_BPE + 13), 
//             io.cdp_rdma2dp_pd(
//                 conf.NVDLA_CDP_THROUGHPUT * conf.NVDLA_BPE + 8 + (log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE / conf.NVDLA_CDP_THROUGHPUT)) - 1, 
//                 conf.NVDLA_CDP_THROUGHPUT * conf.NVDLA_BPE + 8)
//             )
//         ).asUInt.andR & load_din
    
//     when(layer_end){
//         waiting_for_op_en := true.B
//     }.elsewhen(op_en_load){
//         waiting_for_op_en := false.B
//     }

// // //////////////////////////////////////////////////////////////////////
// // //NaN process mode control
// // //////////////////////////////////////////////////////////////////////

//     val datin_d = RegInit(0.U((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+25).W))
//     when(load_din){
//         datin_d := io.cdp_rdma2dp_pd
//     }
    
//     when(io.cdp_rdma2dp_valid & (~waiting_for_op_en)){
//         din_pvld_d1 := true.B
//     }.elsewhen(din_prdy_d1){
//         din_pvld_d1 := false.B
//     }

//     din_prdy_d1 := io.nan_preproc_prdy

// //-------------------------------------------
//     io.nan_preproc_pd := datin_d
//     io.nan_preproc_pvld := din_pvld_d1
// // //////////////////////////////////////////////////////////////////////
// // //input NaN element count
// // //////////////////////////////////////////////////////////////////////
// // .
// // .
// // .
// // .
// // .
// // .
// }}


// object NV_NVDLA_CDP_DP_nanDriver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_nan())
// }
