// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_nan(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())
//         //rdma2dp
//         val pdp_rdma2dp_pd = Flipped(DecoupledIO(UInt(((conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT)+14).W)))
//         //preproc
//         val nan_preproc_pd = DecoupledIO(UInt(((conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT)+14).W))
//         //config  
//         val dp2reg_done = Input(Bool())
//         val reg2dp_flying_mode = Input(Bool())
//         val reg2dp_nan_to_zero = Input(Bool())
//         val reg2dp_op_en = Input(Bool())
//         val dp2reg_inf_input_num = Output(UInt(32.W))
//         val dp2reg_nan_input_num = Output(UInt(32.W))

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
//     io.dp2reg_inf_input_num := 0.U
//     io.dp2reg_nan_input_num := 0.U
//     //==========================================
//     //DP input side
//     //==========================================
//     val din_pvld_d1 = RegInit(false.B)
//     val din_prdy_d1 = io.nan_preproc_pd.ready
//     val waiting_for_op_en = RegInit(true.B)

//     io.pdp_rdma2dp_pd.ready := (~din_pvld_d1 | din_prdy_d1) & (~waiting_for_op_en)
//     val load_din = io.pdp_rdma2dp_pd.valid & io.pdp_rdma2dp_pd.ready
//     val onfly_en = (io.reg2dp_flying_mode === false.B); 
//     //////////////////////////////////////////////////////////////////////
//     //waiting for op_en
//     //////////////////////////////////////////////////////////////////////
//     val op_en_d1 = RegInit(false.B)

//     val op_en_load = io.reg2dp_op_en & (~op_en_d1);
//     val layer_end = Cat(io.pdp_rdma2dp_pd.bits((conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT)+13), io.pdp_rdma2dp_pd.bits((conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT)+9)).andR & load_din;
//     when(layer_end & (~onfly_en)){
//         waiting_for_op_en := true.B
//     }
//     .elsewhen(op_en_load){
//         when(onfly_en){
//             waiting_for_op_en := true.B
//         }
//         .otherwise{
//             waiting_for_op_en := false.B
//         }
//     }

//     /////////////////////////////////////
//     //NaN process mode control
//     /////////////////////////////////////
//     val datin_d = RegInit("b0".asUInt((conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT+14).W))

//     when(load_din){
//         datin_d := io.pdp_rdma2dp_pd.bits
//     }

//     when(io.pdp_rdma2dp_pd.valid & (~waiting_for_op_en)){
//         din_pvld_d1 := true.B
//     }
//     .elsewhen(din_prdy_d1){
//         din_pvld_d1 := false.B
//     }

//     //-------------output data -----------------
//     io.nan_preproc_pd.bits := datin_d
//     io.nan_preproc_pd.valid := din_pvld_d1
// }}


// object NV_NVDLA_PDP_nanDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_nan())
// }
