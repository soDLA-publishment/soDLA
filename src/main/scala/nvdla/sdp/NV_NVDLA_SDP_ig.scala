// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //NV_NVDLA_SDP_RDMA_ig.v

// class NV_NVDLA_SDP_ig(implicit conf: sdpConfiguration) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val dma_rd_req_vld = Output(Bool())
//         val dma_rd_req_rdy = Input(Bool())
//         val dma_rd_req_pd = Output(UInt(conf.NVDLA_DMA_RD_REQ.W))
        
//         val ig2cq_pvld = Output(Bool())
//         val ig2cq_prdy = Input(Bool())
//         val ig2cq_pd = Output(UInt(16.W))

//         val op_load = Input(Bool())

//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_winograd = Input(Bool())
//         val reg2dp_channel = Input(UInt(13.W))
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_rdma_data_mode = Input(Bool())
//         val reg2dp_rdma_data_size = Input(Bool())
//         val reg2dp_rdma_data_use = Input(UInt(2.W))
//         val reg2dp_base_addr_high = Input(UInt(32.W))
//         val reg2dp_base_addr_low = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_line_stride = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_surface_stride = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_perf_dma_en = = Input(Bool())
//         val dp2reg_rdma_stall = Output(UInt(32.W))

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

//     val cmd_accept = Wire(Bool())
//     val is_cube_end = Wire(Bool())
//     val op_done = cmd_accept & is_cube_end
//     val cmd_process = RegInit(0.U)

//     when(op_load){
//         cmd_process := 1.U
//     }
//     .elsewhen(op_done){
//         cmd_process := 0.U
//     }

//     //==============
//     // Address catenate and offset calc
//     //==============
//     val cfg_base_addr = if(conf.NVDLA_MEM_ADDRESS_WIDTH > 32) 
//                         Cat(io.reg2dp_base_addr_high, io.reg2dp_base_addr_low)
//                         else
//                         io.reg2dp_base_addr_low
//     val cfg_surf_stride = io.reg2dp_surface_stride
//     val cfg_line_stride = io.reg2dp_line_stride

//     val cfg_data_size_1byte = io.reg2dp_rdma_data_size === 0.U
//     val cfg_data_use_both = io.reg2dp_rdma_data_use === 2.U
//     val cfg_data_mode_per_kernel = io.reg2dp_rdma_data_mode === 0.U

//     val cfg_proc_int8 = io.reg2dp_proc_precision === 0.U
//     val cfg_proc_int16 = io.reg2dp_proc_precision === 1.U

//     //winongrad unavailable
//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 0.U)

//     //=================================================
//     // Cube Shape
//     //=================================================
//     val is_last_c = Wire(Bool())
//     val is_last_h = Wire(Bool())
//     val is_last_w = Wire(Bool())
//     val is_line_end = true.B

//     val is_surf_end = cfg_mode_1x1_pack | cfg_data_mode_per_kernel | (is_line_end & is_last_h)




// }}




