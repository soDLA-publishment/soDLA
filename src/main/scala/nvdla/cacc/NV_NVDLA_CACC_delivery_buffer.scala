// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //this module is to process dat

// class NV_NVDLA_CACC_delivery_buffer(implicit conf: caccConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         //cacc2sdp
//         val cacc2sdp_ready = Input(Bool())
//         val cacc2sdp_pd = Output(UInt(conf.CACC_SDP_WIDTH.W))
//         val cacc2sdp_valid = Output(Bool())

//         //cacc2glb
//         val cacc2glb_done_intr_pd = Output(UInt(2.W))

//         //dbud
//         val dbuf_rd_addr = Input(UInt(conf.CACC_ABUF_AWIDTH.W))
//         val dbuf_rd_en = Input(Bool())
//         val dbuf_rd_layer_end = Input(Bool())
//         val dbuf_wr_addr = Input(UInt(conf.CACC_DBUF_AWIDTH.W))
// input   [CACC_DBUF_WIDTH-1:0]   dbuf_wr_data;
// input                           dbuf_wr_en;
//         val reg2dp_dataout_width = Input(UInt(13.W))
//         val reg2dp_dataout_height = Input(UInt(13.W))
//         val reg2dp_dataout_channel = Input(UInt(13.W))
//         val reg2dp_dataout_addr = Input(UInt((31-conf.NVDLA_MEMORY_ATOMIC_LOG2).W))
//         val reg2dp_line_packed = Input(Bool())
//         val reg2dp_surf_packed = Input(Bool())
//         val reg2dp_batches = Input(UInt(5.W))
//         val reg2dp_line_stride = Input(UInt(24.W))
//         val reg2dp_surf_stride = Input(UInt(24.W)) 

//         //accu2sc
//         val accu2sc_credit_size = Output(UInt(3.W))
//         val accu2sc_credit_vld = Output(Bool())

//         //pwrbus
//         val pwrbus_ram_pd = Input(UInt(32.W))


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
// //           │                        ├─┐         addition --> narrow down to 34 bit-->narrow down to 32 bit
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘

//     //====================
//     // Addition
//     //====================
//     val i_sat_vld = RegInit(false.B)
//     val i_sat_sel  = RegInit(false.B)
//     val i_sum_pd = Reg(UInt(35.W))

//     i_sat_vld := i_vld
//     when(io.in_valid){
//         i_sat_sel := io.in_sel
//         i_sum_pd := io.in_data +& Mux(io.in_op_valid, io.in_op, "b0".asUInt(34.W))
//     } 

//     //====================
//     // narrow down to 34bit, and need satuation only
//     //====================
//     val i_sat_bits = WireInit("b0".asUInt(33.W))
//     val i_partial_result = WireInit("b0".asUInt(34.W))

//     when(i_sum_pd(34)^i_sum_pd(33)){//saturation condition, sign and msb
//         i_sat_bits := Fill(33, ~i_sum_pd(34))
//     }
//     .otherwise{
//         i_sat_bits := i_sum_pd(32, 0)
//     }

//     i_partial_result := Cat(i_sum_pd(34), i_sat_bits)

//     //====================
//     // narrow down to 32bit, and need rounding and saturation 
//     //====================  
//     val i_pre_sft_pd = Mux(i_sat_sel, i_sat_pd(33, 0), Fill(34, false.B))
//     val i_sft_pd = (Cat(i_pre_sft_pd, "b0".asUInt(16.W)) >> cfg_truncate)(49, 16)
//     val i_guide = (Cat(i_pre_sft_pd, "b0".asUInt(16.W)) >> cfg_truncate)(15)
//     val i_stick = (Cat(i_pre_sft_pd, "b0".asUInt(16.W)) >> cfg_truncate)(14, 0)
//     val i_point5 = i_sat_sel & i_guide & (~i_sat_sign|(i_stick.orR))
//     val i_sft_need_sat = (i_sat_sign & (~(i_sft_pd(32, 31).andR)))|
//                          (~i_sat_sign & (i_sft_pd(32, 31).orR)) |
//                          (~i_sat_sign & Cat(i_sft_pd(30,0), i_point5).andR) 
//     val i_sft_max = Mux(i_sat_sign, Cat(true.B, "b0".asUInt(31.W)), ~Cat(true.B, "b0".asUInt(31.W)))
//     val i_tru_pd = i_pos_pd
//     val i_final_result = Mux(i_sft_need_sat, i_sft_max, i_tru_pd)
//     val i_partial_vld = i_sat_vld & ~i_sat_sel
//     val i_final_vld = i_sat_vld&i_sat_sel

//     io.out_final_valid := RegNext(i_partial_vld, false.B)
//     io.out_final_sat := RegNext(i_final_vld & i_sft_need_sat, false.B)
//     io.out_partial_data := RegNext(i_partial_result)
//     io.out_final_data := RegNext(i_final_result)

// }

