// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_CORE_cal1d(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         //pdp_rdma2dp
//         val pdp_rdma2dp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_BPE+14).W)))
//         //sdp2pdp
//         val sdp2pdp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_BPE+14).W)))

//         //pooling
//         val pooling1d_pd = DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)).W))

//         //config 
//         val pooling_channel_cfg = Input(UInt(13.W))
//         val pooling_fwidth_cfg = Input(UInt(10.W))
//         val pooling_lwidth_cfg = Input(UInt(10.W))
//         val pooling_mwidth_cfg = Input(UInt(10.W))
//         val pooling_out_fwidth_cfg = Input(UInt(10.W))
//         val pooling_out_lwidth_cfg = Input(UInt(10.W))
//         val pooling_out_mwidth_cfg = Input(UInt(10.W))
//         val pooling_size_h_cfg = Input(UInt(3.W))
//         val pooling_splitw_num_cfg = Input(UInt(8.W))
//         val pooling_stride_h_cfg = Input(UInt(4.W))
//         val pooling_type_cfg = Input(UInt(2.W))
//         val reg2dp_cube_in_height = Input(UInt(13.W))
//         val reg2dp_cube_in_width = Input(UInt(13.W))
//         val reg2dp_cube_out_width = Input(UInt(13.W))
//         val reg2dp_kernel_stride_width = Input(UInt(4.W))
//         val reg2dp_kernel_width = Input(UInt(2.W))
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_pad_left = Input(UInt(3.W))
//         val reg2dp_pad_right = Input(UInt(3.W))
//         val reg2dp_pad_right_cfg = Input(UInt(3.W))
//         val reg2dp_pad_value_1x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_2x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_3x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_4x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_5x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_6x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_7x_cfg = Input(UInt(19.W))
//         val dp2reg_done = Input(Bool())

//         val pdp_op_start = Output(Bool())
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
//     ////////////////////////////////////////////////////////////////
//     //==============================================================
//     //PDP start
//     //
//     //--------------------------------------------------------------
//     val pdp_op_pending = RegInit(false.B)
//     io.pdp_op_start := ~pdp_op_pending & io.reg2dp_op_en;
//     when(io.pdp_op_start){
//         pdp_op_pending := true.B
//     }
//     .elsewhen(io.dp2reg_done){
//         pdp_op_pending := false.B
//     }

//     //==============================================================
//     //input data source select
//     //--------------------------------------------------------------
//     val off_flying_en = (io.datin_src_cfg === 0.U)
    



// }}


// object NV_NVDLA_PDP_CORE_cal1dDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_cal1d())
// }