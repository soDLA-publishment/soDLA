// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_WDMA_DAT_out(implicit val conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         //in clock
//         val nvdla_core_clk = Input(Clock())
//         val op_load = Input(Bool())

//         //dma_wr
//         val dma_wr_req_vld = Output(Bool())
//         val dma_wr_req_rdy = Input(Bool())
//         val dma_wr_req_pd = Output(UInt(conf.NVDLA_DMA_WR_REQ.W))

//         //cmd2dat_dma
//         val cmd2dat_dma_pvld = Input(Bool())
//         val cmd2dat_dma_prdy = Output(Bool())
//         val cmd2dat_dma_pd = Input(UInt((conf.SDP_WR_CMD_DW+2).W))

//         //out dfifo
//         val dfifo_rd_pvld = Input(Vec(4, Bool()))
//         val dfifo_rd_prdy = Output(Vec(4, Bool()))
//         val dfifo_rd_pd = Input(Vec(4, UInt(conf.AM_DW.W)))

//         val reg2dp_batch_number = Input(UInt(5.W))
//         val reg2dp_ew_alu_algo = Input(UInt(2.W))
//         val reg2dp_ew_alu_bypass = Input(Bool())
//         val reg2dp_ew_bypass = Input(Bool())
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_interrupt_ptr = Input(Bool())
//         val reg2dp_out_precision = Input(UInt(2.W))
//         val reg2dp_output_dst = Input(Bool())
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_winograd = Input(Bool())

//         val dp2reg_done = Output(Bool())
//         val dp2reg_status_unequal = Output(Bool())

//         val intr_req_ptr = Output(Bool())
//         val intr_req_pvld = Output(Bool())
//     })

// withClock(io.nvdla_core_clk){

//     val cfg_mode_batch = io.reg2dp_batch_number =/= 0.U
//     val cfg_mode_winog = io.reg2dp_winograd === 1.U
//     val cfg_mode_eql = (io.reg2dp_ew_bypass === 0.U) & (io.reg2dp_ew_alu_bypass =/= 0.U) & (io.reg2dp_ew_alu_algo =/= 3.U)

//     val cfg_mode_pdp = io.reg2dp_output_dst === 1.U
//     val cfg_mode_quite = cfg_mode_eql | cfg_mode_pdp

//     val cfg_di_int8  = io.reg2dp_proc_precision  === 0.U 
//     val cfg_do_int16 = io.reg2dp_out_precision === 1.U 

//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 0.U)

//     //pop comand data
//     val cmd2dat_dma_addr = io.cmd2dat_dma_pd(conf.NVDLA_MEM_ADDRESS_WIDTH-conf.AM_AW-1, 0)
//     val cmd2dat_dma_size = io.cmd2dat_dma_pd(conf.SDP_WR_CMD_DW-1, conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW)
//     val cmd2dat_dma_odd = io.cmd2dat_dma_pd(conf.SDP_WR_CMD_DW)
//     val cmd2dat_dma_cube_end = io.cmd2dat_dma_pd(conf.SDP_WR_CMD_DW+1)

//     val cmd_rdy = Wire(Bool())
//     val cmd_vld = RegInit(false.B)
//     val dat_accept = Wire(Bool())
//     val is_last_beat = Wire(Bool())
//     io.cmd2dat_dma_prdy := cmd_rdy || !cmd_vld
//     cmd_rdy := dat_accept & is_last_beat

//     when(io.cmd2dat_dma_prdy){
//         cmd_vld := io.cmd2dat_dma_pvld
//     }


//     val cmd_size = RegInit(0.U(14.W))
//     val cmd_addr = RegInit(0.U((conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW).W))
//     val cmd_odd = RegInit(false.B)
//     val cmd_cube_end = RegInit(false.B)

//     when(io.cmd2dat_dma_pvld & io.cmd2dat_dma_prdy){
//         cmd_size := cmd2dat_dma_size
//         cmd_addr := cmd2dat_dma_addr
//         cmd_odd := cmd2dat_dma_odd
//         cmd_cube_end := cmd2dat_dma_cube_end
//     }

// // Switch between CMD/DAT pkt
//     val cmd_en = RegInit(true.B)
//     val dat_en = RegInit(false.B)
//     val cmd_accept = Wire(Bool())
//     when(cmd_accept){
//         cmd_en := false.B
//         dat_en := true.B
//     }.elsewhen(dat_accept){
//         cmd_en := true.B
//         dat_en := false.B
//     }

// // #ifdef  NVDLA_SDP_DATA_TYPE_INT8TO16
// // #else
//     val size_of_atom = conf.NVDLA_DMA_MASK_BIT.asUInt

//     val size_of_beat = cmd_size + 1.U
//     val beat_count = RegInit(0.U(14.W))
//     val beat_count_nxt = beat_count + size_of_atom 

//     when(dat_accept){
//         when(is_last_beat){
//             beat_count := 0.U
//         }.otherwise{
//             beat_count := beat_count_nxt
//         }
//     }

//     is_last_beat := (beat_count_nxt >= size_of_beat)
//     val remain_beat = size_of_beat - beat_count
//     val dfifo_rd_size = Mux(is_last_beat, remain_beat, size_of_atom)

//     val dfifo_rd_en = Wire(Vec(4, Bool()))
//     dfifo_rd_en(0) := beat_count(1,0) === 0.U
//     dfifo_rd_en(1) := Mux((dfifo_rd_size === 4.U || dfifo_rd_size === 2.U), 
//                             beat_count(1,0) === 0.U,  
//                             beat_count(1,0) === 1.U
//                                 )
//     dfifo_rd_en(2) := Mux(dfifo_rd_size === 4.U, 
//                             beat_count(1,0) === 0.U, 
//                             beat_count(1,0) === 2.U)
//     dfifo_rd_en(3) := Mux(dfifo_rd_size === 4.U,
//                             beat_count(1,0) === 0.U,
//                             Mux(dfifo_rd_size === 2.U,
//                                 beat_count(1,0) === 2.U,
//                                 beat_count(1,0) === 3.U)
//                                 )
//     val dat_rdy = Wire(Bool())
    
//     val dfifo_rd_en_npvld = VecInit((0 to 3) map {i => dfifo_rd_en(i) & ! io.dfifo_rd_pvld(i)})

//     io.dfifo_rd_prdy(0) := dat_rdy & dfifo_rd_en(0) &
//                         !(dfifo_rd_en_npvld(1) | dfifo_rd_en_npvld(2) | dfifo_rd_en_npvld(3))
//     io.dfifo_rd_prdy(1) := dat_rdy & dfifo_rd_en(1) &
//                         !(dfifo_rd_en_npvld(0) | dfifo_rd_en_npvld(2) | dfifo_rd_en_npvld(3))
//     io.dfifo_rd_prdy(2) := dat_rdy & dfifo_rd_en(2) &
//                         !(dfifo_rd_en_npvld(0) | dfifo_rd_en_npvld(1) | dfifo_rd_en_npvld(3))
//     io.dfifo_rd_prdy(3) := dat_rdy & dfifo_rd_en(3) &
//                         !(dfifo_rd_en_npvld(0) | dfifo_rd_en_npvld(1) | dfifo_rd_en_npvld(2))
    
//     val dat_vld = !(dfifo_rd_en_npvld.asUInt.orR)

//     val dat_pd_atom4 = Cat(io.dfifo_rd_pd(3), io.dfifo_rd_pd(2), io.dfifo_rd_pd(1), io.dfifo_rd_pd(0))
//     val dat_pd_atom2 = Mux(beat_count(1,0) === 2.U, 
//                         Cat(Fill(2*conf.AM_DW, false.B), io.dfifo_rd_pd(3), io.dfifo_rd_pd(2)),
//                         Cat(Fill(2*conf.AM_DW, false.B), io.dfifo_rd_pd(1), io.dfifo_rd_pd(0))
//                         )
//     val dat_pd_atom1 = Mux(beat_count(1,0) === 3.U,
//                         Cat(Fill(3*conf.AM_DW, false.B), io.dfifo_rd_pd(3)),
//                         Mux(beat_count(1,0) === 2.U,
//                             Cat(Fill(3*conf.AM_DW, false.B), io.dfifo_rd_pd(2)),
//                             Mux(beat_count(1,0) === 2.U,
//                                 Cat(Fill(3*conf.AM_DW, false.B), io.dfifo_rd_pd(1)),
//                                 Cat(Fill(3*conf.AM_DW, false.B), io.dfifo_rd_pd(0))
//                                 )
//                             )
//                         )
//     val dat_pd_mux = Mux(size_of_atom === 4.U, 
//                             dat_pd_atom4, 
//                             Mux(size_of_atom === 2.U, dat_pd_atom2, dat_pd_atom1)
//                         )
    
//     val dma_wr_dat_mask = Wire(UInt(4.W))
//     val dat_pd = dat_pd_mux & Cat(
//                         Fill(conf.AM_DW, dma_wr_dat_mask(3)), 
//                         Fill(conf.AM_DW, dma_wr_dat_mask(2)), 
//                         Fill(conf.AM_DW, dma_wr_dat_mask(1)), 
//                         Fill(conf.AM_DW, dma_wr_dat_mask(0))
//                         )
    
//     val dma_wr_rdy = cfg_mode_quite || io.dma_wr_req_rdy
//     dat_rdy := dat_en & dma_wr_rdy
//     dat_accept := dat_vld & dat_rdy
//     cmd_accept := cmd_en & cmd_vld & dma_wr_rdy

// //===========================
// // DMA OUTPUT
// //===========================
// // packet: cmd

//     val dma_wr_cmd_vld  = cmd_en & cmd_vld
//     val dma_wr_cmd_addr = Cat(cmd_addr, Fill(conf.AM_AW, false.B))
//     val dma_wr_cmd_size = cmd_size
//     val dma_wr_cmd_require_ack = cmd_cube_end

//     val dma_wr_cmd_pd = Cat(dma_wr_cmd_require_ack, dma_wr_cmd_size, dma_wr_cmd_addr)
//     val dma_wr_dat_vld = dat_en & dat_vld

// // #ifdef  NVDLA_SDP_DATA_TYPE_INT8TO16
// // #else

//     dma_wr_dat_mask := Mux(dfifo_rd_size === "h4".asUInt(3.W), "hf".asUInt(4.W), 
//                             Mux(dfifo_rd_size === "h3".asUInt(3.W), "h7".asUInt(4.W), 
//                                 Mux(dfifo_rd_size === "h2".asUInt(3.W), "h3".asUInt(4.W), dfifo_rd_size))
//                                 )
//     val dma_wr_dat_data = dat_pd(conf.NVDLA_MEMIF_WIDTH-1,0)
//     val dma_wr_dat_pd = Cat(dma_wr_dat_mask(conf.NVDLA_DMA_MASK_BIT-1,0), dma_wr_dat_data)
    
//     io.dma_wr_req_vld := (dma_wr_cmd_vld | dma_wr_dat_vld) & !cfg_mode_quite

//     val dma_wr_req_pd_reg = RegInit(0.U((conf.NVDLA_DMA_WR_REQ-1).W))
//     when(cmd_en){
//         dma_wr_req_pd_reg := dma_wr_cmd_pd
//     }.otherwise{
//         dma_wr_req_pd_reg := dma_wr_dat_pd
//     }

//     io.dma_wr_req_pd := Cat(Mux(cmd_en, false.B, true.B), dma_wr_req_pd_reg)


// //=================================================
// // Count the Equal Bit in EQ Mode
// //=================================================

//     val dfifo_unequal = RegInit(VecInit(Seq.fill(4)(false.B)))

//     for(i <- 0 to 3){
//         when(io.op_load){
//             dfifo_unequal(i) := false.B
//         }.otherwise{
//             when(io.dfifo_rd_pvld(i) & io.dfifo_rd_prdy(i)){
//                 dfifo_unequal(i) := dfifo_unequal(i) | (io.dfifo_rd_pd(i).asUInt.orR)
//             }
//         }
//     }
 
//     io.dp2reg_status_unequal := dfifo_unequal.asUInt.orR 

// //===========================
// // op_done
// //===========================

//     val layer_done = dat_accept & cmd_cube_end & is_last_beat
//     val dp2reg_done_reg = RegInit(false.B)
//     dp2reg_done_reg := layer_done
//     io.dp2reg_done := dp2reg_done_reg


// //==============
// // INTR Interface
// //==============

//     io.intr_req_ptr := io.reg2dp_interrupt_ptr
//     io.intr_req_pvld := layer_done

// //==============
// // FUNCTION POINT
// //==============
// ///////



// }}


 
// object NV_NVDLA_SDP_WDMA_DAT_outDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_WDMA_DAT_out())
// }


