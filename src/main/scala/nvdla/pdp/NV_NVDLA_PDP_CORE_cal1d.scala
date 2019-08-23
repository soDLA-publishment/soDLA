// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_CORE_cal1d(implicit val conf: pdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         //pdp_rdma2dp
//         val pdp_rdma2dp_valid = Input(Bool())
//         val pdp_rdma2dp_pd = Input(UInt((conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_BPE+13).W))

//         //sdp2pdp
//         val sdp2pdp_valid = Input(Bool())

//         //pooling
//         val pooling_out_pvld = Output(Bool())
//         val pooling_out_prdy = Input(Bool())
//         val pooling_out = Output(UInt((((conf.NVDLA_PDP_BWPE + 3)*conf.NVDLA_PDP_THROUGHPUT)+4).W))

//         //config 
//         val pooling1d_prdy = Input(Bool())
//         val pooling_channel_cfg = Input(UInt(13.W))
//         val pooling_fwidth_cfg = Input(UInt(10.W))
//         val pooling_lwidth_cfg = Input(UInt(10.W))
//         val pooling_mwidth_cfg = Input(UInt(10.W))
//         val pooling_out_fwidth_cfg = Input(UInt(10.W))
//         val pooling_out_lwidth_cfg = Input(UInt(10.W))
//         val pooling_out_mwidth_cfg = Input(UInt(10.W))
//         val pooling_size_h_cfg = Input(UInt(3.W))
//         val pooling_splitw_num_cfg = Input(UInt(8.W))
//         val pooling_stride_h_cfg;
//         val pooling_type_cfg;
//         val reg2dp_cube_in_height;
//         val reg2dp_cube_in_width;
//         val reg2dp_cube_out_width;
//         val reg2dp_kernel_stride_width;
//         val reg2dp_kernel_width;
//         val reg2dp_op_en;
//         val reg2dp_pad_left;
//         val reg2dp_pad_right;
//         val reg2dp_pad_right_cfg;
//         val reg2dp_pad_value_1x_cfg;
//         val reg2dp_pad_value_2x_cfg;
//         val reg2dp_pad_value_3x_cfg;
//         val reg2dp_pad_value_4x_cfg;
//         val reg2dp_pad_value_5x_cfg;
//         val reg2dp_pad_value_6x_cfg;
//         val reg2dp_pad_value_7x_cfg;
//         val dp2reg_done = Input(Bool())
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
//     val pdp_op_start = ~pdp_op_pending & io.reg2dp_op_en;
//     when(pdp_op_start){
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
//   implicit val conf: pdpConfiguration = new pdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_cal1d())
// }