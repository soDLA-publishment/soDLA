// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_wdma(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk_orig = Input(Clock())
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         // dp2wdma
//         val pdp_dp2wdma_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_PDP_BWPE).W)))

//         // pdp2mcif_wr
//         val pdp2mcif_wr_req_pd = DecoupledIO(UInt(conf.NVDLA_PDP_MEM_WR_REQ.W))
//         val mcif2pdp_wr_rsp_complete = Input(Bool())

//         // pdp2cvif_wr
//         val pdp2cvif_wr_req_pd = DecoupledIO(UInt(conf.NVDLA_PDP_MEM_WR_REQ.W))
//         val cvif2pdp_wr_rsp_complete = Input(Bool())

//         val pdp2glb_done_intr_pd = Output(UInt(2.W))
//         val rdma2wdma_done = Input(Bool())

//         // config
//         val reg2dp_cube_out_channel = Input(UInt(13.W))
//         val reg2dp_cube_out_height = Input(UInt(13.W))
//         val reg2dp_cube_out_width = Input(UInt(13.W))
//         val reg2dp_dma_en = Input(Bool())
//         val reg2dp_dst_base_addr_high = Input(UInt(32.W))
//         val reg2dp_dst_base_addr_low = Input(UInt(32.W))
//         val reg2dp_dst_line_stride = Input(UInt(32.W))
//         val reg2dp_dst_ram_type = Input(Bool())
//         val reg2dp_dst_surface_stride = Input(UInt(32.W))
//         val reg2dp_flying_mode = Input(Bool())
//         val reg2dp_interrupt_ptr = Input(Bool())
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_partial_width_out_first = Input(UInt(10.W))
//         val reg2dp_partial_width_out_last = Input(UInt(10.W))
//         val reg2dp_partial_width_out_mid = Input(UInt(10.W))
//         val reg2dp_split_num = Input(UInt(8.W))
//         val dp2reg_d0_perf_write_stall = Output(UInt(32.W))
//         val dp2reg_d1_perf_write_stall = Output(UInt(32.W))
//         val dp2reg_done = Output(Bool())

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
//     ///////////////////////////////////////////////////////////////////////////////////////
//     //==============
//     // tracing rdma reading done to avoid layer switched but RDMA still reading the last layer
//     //==============
//     val op_done = Wire(Bool())
//     val on_fly_en = io.reg2dp_flying_mode === 0.U
//     val off_fly_en = io.reg2dp_flying_mode === 1.U

//     val reading_done_flag = RegInit(false.B)
//     when(op_done){
//         reading_done_flag := false.B
//     }
//     .elsewhen(io.rdma2wdma_done & off_fly_en){
//         reading_done_flag := true.B
//     }
//     .elsewhen(io.op_load & on_fly_en){
//         reading_done_flag := true.B
//     }
//     .elsewhen(io.op_load & off_fly_en){
//         reading_done_flag := false.B
//     }

//     val waiting_rdma = RegInit(false.B)
//     when(op_done & (~reading_done_flag)){
//         waiting_rdma := true.B
//     }
//     .elsewhen(reading_done_flag){
//         waiting_rdma := false.B
//     }

//     val wdma_done = RegInit(false.B)
//     when(op_done & reading_done_flag){
//         wdma_done := true.B
//     }
//     .elsewhen(waiting_rdma & reading_done_flag){
//         wdma_done := true.B
//     }
//     .otherwise{
//         wdma_done := false.B
//     }

//     //==============
//     // Work Processing
//     //==============
//     val op_prcess = RegInit(false.B)
//     op_load := io.reg2dp_op_en & !op_prcess;
//     val is_last_beat = Wire(Bool())
//     op_done := io.reg_cube_last & is_last_beat & dat_accept
//     io.dp2reg_done := wdma_done

//     when(op_load){
//         op_prcess := true.B
//     }
//     .elsewhen(wdma_done){
//         op_prcess := false.B
//     }

//     //==============
//     // Data INPUT pipe and Unpack
//     //==============
//     val pipe_0 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_PDP_BWPE))
//     pipe_0.io.clk := io.nvdla_core_clk
//     pipe_0.io.vi := io.pdp_dp2wdma_pd.valid
//     io.pdp_dp2wdma_pd.ready = pipe_0.io.ro
//     pipe_0.io.di := io.pdp_dp2wdma_pd.bits

// }}



// object NV_NVDLA_PDP_wdmaDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_wdma())
// }