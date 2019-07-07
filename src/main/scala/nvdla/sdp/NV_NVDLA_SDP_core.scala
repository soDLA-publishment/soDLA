// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_core(implicit val conf: sdpConfiguration) extends Module {
//    val io = IO(new Bundle {

//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

// // #ifdef NVDLA_SDP_BS_ENABLE
// // #ifdef NVDLA_SDP_BN_ENABLE
// // #ifdef NVDLA_SDP_EW_ENABLE

//         val sdp_dp2wdma_valid = Output(Bool())
//         val sdp_dp2wdma_ready = Input(Bool())
//         val sdp_dp2wdma_pd = Output(UInt(conf.DP_DOUT_DW.W))

//         val sdp2pdp_valid = Output(Bool())
//         val sdp2pdp_ready = Input(Bool())
//         val sdp2pdp_pd = Output(UInt(conf.DP_DOUT_DW.W))

//         val cacc2sdp_valid = Input(Bool())
//         val cacc2sdp_ready = Output(Bool())
//         val cacc2sdp_pd = Input(UInt((conf.DP_IN_DW+2).W))

//         val sdp_mrdma2cmux_valid = Input(Bool())
//         val sdp_mrdma2cmux_ready = Output(Bool())
//         val sdp_mrdma2cmux_pd = Input(UInt((conf.DP_DIN_DW+2).W))

//         val reg2dp_bcore_slcg_op_en = Input(Bool())
//         val reg2dp_flying_mode = Input(Bool())

//  // #ifdef NVDLA_SDP_BS_ENABLE
// // #ifdef NVDLA_SDP_BN_ENABLE

//         val reg2dp_cvt_offset = Input(UInt(32.W))
//         val reg2dp_cvt_scale = Input(UInt(16.W))
//         val reg2dp_cvt_shift = Input(UInt(6.W))
//         val reg2dp_ecore_slcg_op_en = Input(Bool())

// // #ifdef NVDLA_SDP_EW_ENABLE
// // #ifdef NVDLA_SDP_LUT_ENABLE

//         val reg2dp_nan_to_zero = Input(Bool())
//         val reg2dp_ncore_slcg_op_en = Input(Bool())
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_out_precision = Input(UInt(2.W))
//         val reg2dp_output_dst = Input(Bool())
//         val reg2dp_perf_lut_en = Input(Bool())
//         val reg2dp_perf_sat_en = Input(Bool())
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val dp2reg_done = Input(Bool())
//         val dp2reg_out_saturation = Output(UInt(32.W))
//         val dla_clk_ovr_on_sync = Input(Clock())
//         val global_clk_ovr_on_sync = Input(Clock())
//         val tmc2slcg_disable_clock_gating = Input(Bool())

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

// //===========================================
// // CFG
// //===========================================

//     val cfg_bs_en = RegInit(false.B)
//     val cfg_bn_en = RegInit(false.B)
//     val cfg_ew_en = RegInit(false.B)
//     val cfg_mode_eql = RegInit(false.B)

// // #ifdef NVDLA_SDP_BS_ENABLE
// // #ifdef NVDLA_SDP_BN_ENABLE
// // #ifdef NVDLA_SDP_EW_ENABLE

//     val cfg_cvt_offset = RegInit(0.U(32.W))
//     val cfg_cvt_scale = RegInit(0.U(16.W))
//     val cfg_cvt_shift = RegInit(0.U(6.W))
//     val cfg_proc_precision = RegInit(0.U(2.W))
//     val cfg_out_precision = RegInit(0.W(2.W))
//     val cfg_nan_to_zero = RegInit(false.B)

//     val op_en_load = Wire(Bool())
//     when(op_en_load){
//         cfg_cvt_offset := io.reg2dp_cvt_offset
//         cfg_cvt_scale := io.reg2dp_cvt_scale
//         cfg_cvt_shift := io.reg2dp_cvt_shift
//         cfg_proc_precision := io.reg2dp_proc_precision
//         cfg_out_precision := io.reg2dp_out_precision
//         cfg_nan_to_zero := io.reg2dp_nan_to_zero        
//     }

// //===========================================
// // SLCG Gate
// //===========================================

//     val bcore_slcg_en = cfg_bs_en & io.reg2dp_bcore_slcg_op_en
//     val ncore_slcg_en = cfg_bn_en & io.reg2dp_ncore_slcg_op_en
//     val ecore_slcg_en = cfg_ew_en & io.reg2dp_ecore_slcg_op_en

//     val u_gate = Module(new NV_NVDLA_SDP_CORE_gate)
//     u_gate.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync
//     u_gate.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//     u_gate.io.nvdla_core_clk := io.nvdla_core_clk
//     u_gate.io.bcore_slcg_en := bcore_slcg_en
//     u_gate.io.ecore_slcg_en := ecore_slcg_en
//     u_gate.io.ncore_slcg_en := ncore_slcg_en
//     u_gate.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//     val nvdla_gated_bcore_clk = u_gate.io.nvdla_gated_bcore_clk
//     val nvdla_gated_ecore_clk = u_gate.io.nvdla_gated_ecore_clk
//     val nvdla_gated_ncore_clk = u_gate.io.nvdla_gated_ncore_clk

// //===========================================================================
// //  DATA PATH LOGIC 
// //  RDMA data 
// //===========================================================================
// //covert mrdma data from atomic_m to NVDLA_SDP_MAX_THROUGHPUT

//     // val u_dpin_pack = Module(new NV_NVDLA_SDP_RDMA_pack)

//     // ...

// // #ifdef NVDLA_SDP_BS_ENABLE
// // #ifdef NVDLA_SDP_BN_ENABLE
// // #ifdef NVDLA_SDP_EW_ENABLE

//     val wait_for_op_en = RegInit(true.B)
//     when(io.dp2reg_done){
//         wait_for_op_en := true.B
//     }.elsewhen(io.reg2dp_op_en){
//         wait_for_op_en := false.B
//     }

//     op_en_load := wait_for_op_en & io.reg2dp_op_en

// // #ifdef NVDLA_SDP_BS_ENABLE
// // #ifdef NVDLA_SDP_BN_ENABLE
// // #ifdef NVDLA_SDP_EW_ENABLE






// }}


// object NV_NVDLA_SDP_coreDriver extends App {
//   implicit val conf: sdpConfiguration = new sdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_core)
// }


