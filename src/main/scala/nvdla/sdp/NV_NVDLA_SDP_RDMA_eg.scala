// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_RDMA_eg(implicit val conf: sdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         val pwrbus_ram_pd = Input(UInt(32.W))
//         val op_load = Input(Bool())
//         val eg_done = Output(Bool())

//         val cq2eg_pd = Input(UInt(16.W))
//         val cq2eg_pvld = Input(Bool())
//         val cq2eg_prdy = Output(Bool())     // read

//         val lat_fifo_rd_pd = Input(UInt(conf.NVDLA_DMA_RD_RSP.W))
//         val lat_fifo_rd_pvld = Input(Bool())
//         val lat_fifo_rd_prdy = Output(Bool())

//         val dma_rd_cdt_lat_fifo_pop = Output(Bool())

//         val sdp_rdma2dp_alu_pd = Output((conf.AM_DW2 + 1).W)
//         val sdp_rdma2dp_alu_valid = Output(Bool())
//         val sdp_rdma2dp_alu_ready = Input(Bool())

//         val sdp_rdma2dp_mul_pd = Output((conf.AM_DW2 + 1).W)
//         val sdp_rdma2dp_mul_valid = Output(Bool())
//         val sdp_rdma2dp_mul_ready = Input(Bool())

//         val reg2dp_batch_number = Input(UInt(5.W))
//         val reg2dp_channel = Input(UInt(13.W))
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_out_precision = Input(UInt(2.W))
//         val reg2dp_rdma_data_mode = Input(Bool())
//         val reg2dp_rdma_data_size = Input(Bool())
//         val reg2dp_rdma_data_use = Input(UInt(2.W))
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
//     withClock(io.nvdla_core_clk){

// //==============
// // CFG REG
// //==============

//         val cfg_data_size_1byte = (io.reg2dp_rdma_data_size === false.B)
//         val cfg_data_size_2byte = (io.reg2dp_rdma_data_size === true.B)

//         val cfg_mode_mul_only = (io.reg2dp_rdma_data_use === 0.U)
//         val cfg_mode_alu_only = (io.reg2dp_rdma_data_use === 1.U)
//         val cfg_mode_both = (io.reg2dp_rdma_data_use === 2.U)
//         val cfg_mode_per_element = (io.reg2dp_rdma_data_mode === true.B)

//         val cfg_mode_single = cfg_mode_mul_only || cfg_mode_alu_only   

//         val cfg_mode_1bytex1 = cfg_data_size_1byte & cfg_mode_single
//         val cfg_mode_2bytex1 = cfg_data_size_2byte & cfg_mode_single
//         val cfg_mode_1bytex2 = cfg_data_size_1byte & cfg_mode_both
//         val cfg_mode_2bytex2 = cfg_data_size_2byte & cfg_mode_both

//         // #ifdef NVDLA_BATCH_ENABLE
//         // #endif

//         val cfg_dp_8 = (io.reg2dp_proc_precision === 0.U)
//         val cfg_do_8 = (io.reg2dp_out_precision === 0.U)

//         val cfg_alu_en = cfg_mode_alu_only || cfg_mode_both
//         val cfg_mul_en = cfg_mode_mul_only || cfg_mode_both

// //==============
// // DMA Interface
// //==============

//         io.dma_rd_cdt_lat_fifo_pop := io.lat_fifo_rd_pvld & io.lat_fifo_rd_prdy

// //==============
// // Latency FIFO to buffer return DATA
// //==============

//         val lat_fifo_rd_mask = Cat(
//                     Fill((4-conf.NVDLA_DMA_MASK_BIT), false.B), 
//                     io.lat_fifo_rd_pd((conf.NVDLA_DMA_RD_RSP-1), NVDLA_MEMIF_WIDTH)
//                     )
//         val lat_fifo_rd_size = lat_fifo_rd_mask(3) + lat_fifo_rd_mask(2) + lat_fifo_rd_mask(1) + lat_fifo_rd_mask(0)

// //==================================================================
// // Context Queue: read
// //==================================================================

//         val ig2eg_size = io.cq2eg_pd(14,0)
//         val ig2eg_cube_end = io.cq2eg_pd(15)

//         val beat_size = ig2eg_size + 1.U
//         val beat_count = RegInit(0.U(15.W))
//         val beat_count_nxt = beat_count + lat_fifo_rd_size
//         val is_last_beat = (beat_count_nxt === beat_size)
//         val is_beat_end := is_last_beat & io.lat_fifo_rd_pvld & io.lat_fifo_rd_prdy
//         io.cq2eg_prdy := is_beat_end

//         when(io.lat_fifo_rd_pvld & io.lat_fifo_rd_prdy){
//             when(is_last_beat){
//                 beat_count := 0.U
//             }.otherwise{
//                 beat_count := beat_count_nxt
//             }
//         }

// // `ifdef SPYGLASS_ASSERT_ON
// //...

//         val lat_fifo_rd_beat_end = is_last_beat
//         val unpack_out_prdy = Wire(Bool())

//         val u_rdma_unpack = Module(new NV_NVDLA_SDP_RDMA_unpack)
//         u_rdma_unpack.io.nvdla_core_clk := io.nvdla_core_clk
//         u_rdma_unpack.io.inp_end := lat_fifo_rd_beat_end
//         u_rdma_unpack.io.inp_pvld := io.lat_fifo_rd_pvld
//         io.lat_fifo_rd_prdy := u_rdma_unpack.io.inp_prdy
//         u_rdma_unpack.io.inp_data := io.lat_fifo_rd_pd

//         val unpack_out_pvld = u_rdma_unpack.io.out_pvld
//         u_rdma_unpack.io.out_prdy := unpack_out_prdy
//         val unpack_out_pd = u_rdma_unpack.io.out_data


//         val unpack_out_mask = unpack_out_pd 
//         val alu_rod_rdy = Wire(Bool())
//         val mul_rod_rdy = Wire(Bool())

//         unpack_out_prdy := Mux(
//                             cfg_mode_both,
//                             (alu_rod_rdy & mul_rod_rdy),
//                             Mux(cfg_mode_alu_only, alu_rod_rdy, mul_rod_rdy)
//                             ) 
        




//     // // Address decode

//     // val nvdla_sdp_rdma_s_pointer_0_wren = (io.reg_offset === "h4".asUInt(32.W))&io.reg_wr_en
//     // val nvdla_sdp_rdma_s_status_0_wren = (io.reg_offset === "h0".asUInt(32.W))&io.reg_wr_en
    
//     // val nvdla_sdp_rdma_s_pointer_0_out = Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)
//     // val nvdla_sdp_rdma_s_status_0_out = Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

//     // // Output mux
   
//     // io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
//     // Seq(      
//     // "h4".asUInt(32.W)  -> nvdla_sdp_rdma_s_pointer_0_out,
//     // "h0".asUInt(32.W)  -> nvdla_sdp_rdma_s_status_0_out 
//     // ))

//     // // Register flop declarations

//     // val producer_out = RegInit(false.B)

//     // when(nvdla_sdp_rdma_s_pointer_0_wren){
//     //     producer_out:= io.reg_wr_data(0)
//     // }
        
//     // io.producer := producer_out

// }}

