// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_RDMA_EG_ro(implicit val conf: sdpConfiguration) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val pwrbus_ram_pd = Input(UInt(32.W))

//         val sdp_rdma2dp_valid = Output(Bool())
//         val sdp_rdma2dp_ready = Input(Bool())
//         val sdp_rdma2dp_pd = Output(UInt((conf.AM_DW2 + 1).W))

//         val rod0_wr_pd = Input(UInt(conf.AM_DW.W))
//         val rod1_wr_pd = Input(UInt(conf.AM_DW.W))
//         val rod2_wr_pd = Input(UInt(conf.AM_DW.W))
//         val rod3_wr_pd = Input(UInt(conf.AM_DW.W))

//         val rod_wr_mask = Input(UInt(4.W))
//         val rod_wr_vld = Input(Bool())
//         val rod_wr_rdy = Output(Bool())
//         val roc_wr_pd = Input(UInt(2.W))
//         val roc_wr_vld = Input(Bool())
//         val roc_wr_rdy = Output(Bool())
//         val cfg_dp_8 = Input(Bool())
//         val cfg_dp_size_1byte = Input(Bool())
//         val cfg_mode_per_element = Input(Bool())
// // #ifdef NVDLA_BATCH_ENABLE
// //...
//         // val cfg_mode_multi_batch = Input(Bool())
//         // val reg2dp_batch_number = Input(UInt(5.W))

//         val reg2dp_channel = Input(UInt(13.W))
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val layer_end = Output(Bool())
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


// //=======================================================
// // DATA FIFO: WRITE SIDE
// //=======================================================

//     val rod0_wr_prdy = Wire(Bool())
//     val rod1_wr_prdy = Wire(Bool())
//     val rod2_wr_prdy = Wire(Bool())
//     val rod3_wr_prdy = Wire(Bool())

//     io.rod_wr_rdy := !( io.rod_wr_mask(0) & !rod0_wr_prdy |
//                         io.rod_wr_mask(1) & !rod1_wr_prdy | 
//                         io.rod_wr_mask(2) & !rod2_wr_prdy | 
//                         io.rod_wr_mask(3) & !rod3_wr_prdy )
//     val rod0_wr_pvld = io.rod_wr_vld & io.rod_wr_mask(0) & 
//                         !(  io.rod_wr_mask(1) & !rod1_wr_prdy | 
//                             io.rod_wr_mask(2) & !rod2_wr_prdy | 
//                             io.rod_wr_mask(3) & !rod3_wr_prdy )
//     val rod1_wr_pvld = io.rod_wr_vld & io.rod_wr_mask(1) & 
//                         !(  io.rod_wr_mask(0) & !rod1_wr_prdy | 
//                             io.rod_wr_mask(2) & !rod2_wr_prdy | 
//                             io.rod_wr_mask(3) & !rod3_wr_prdy )
//     val rod2_wr_pvld = io.rod_wr_vld & io.rod_wr_mask(2) & 
//                         !(  io.rod_wr_mask(0) & !rod1_wr_prdy | 
//                             io.rod_wr_mask(1) & !rod2_wr_prdy | 
//                             io.rod_wr_mask(3) & !rod3_wr_prdy ) 
//     val rod3_wr_pvld = io.rod_wr_vld & io.rod_wr_mask(3) & 
//                         !(  io.rod_wr_mask(0) & !rod1_wr_prdy | 
//                             io.rod_wr_mask(1) & !rod2_wr_prdy | 
//                             io.rod_wr_mask(2) & !rod3_wr_prdy )  
    
//     val rod0_rd_prdy = Wire(Bool())
//     val rod1_rd_prdy = Wire(Bool())
//     val rod2_rd_prdy = Wire(Bool())
//     val rod3_rd_prdy = Wire(Bool())

//     val u_rod0 = Module(new NV_NVDLA_SDP_RDMA_fifo(conf.DEPTH, conf.AM_DW))   //need new fifo? depth unknown
//     u_rod0.io.nvdla_core_clk    := io.nvdla_core_clk
//     rod0_wr_prdy                := u_rod0.io.wr_prdy
//     u_rod0.io.wr_pvld           := rod0_wr_pvld
//     u_rod0.io.wr_pd             := io.rod0_wr_pd
//     u_rod0.io.rd_prdy           := rod0_rd_prdy
//     val rod0_rd_pvld            = u_rod0.io.rd_pvld
//     val rod0_rd_pd              = u_rod0.io.rd_pd
//     //No pwrbus_ram_pd

//     val u_rod1 = Module(new NV_NVDLA_SDP_RDMA_fifo(conf.DEPTH, conf.AM_DW))   //need new fifo? depth unknown
//     u_rod1.io.nvdla_core_clk    := io.nvdla_core_clk
//     rod1_wr_prdy                := u_rod1.io.wr_prdy
//     u_rod1.io.wr_pvld           := rod1_wr_pvld
//     u_rod1.io.wr_pd             := io.rod1_wr_pd
//     u_rod1.io.rd_prdy           := rod1_rd_prdy
//     val rod1_rd_pvld            = u_rod1.io.rd_pvld
//     val rod1_rd_pd              = u_rod1.io.rd_pd    

//     val u_rod2 = Module(new NV_NVDLA_SDP_RDMA_fifo(conf.DEPTH, conf.AM_DW))   //need new fifo? depth unknown
//     u_rod2.io.nvdla_core_clk    := io.nvdla_core_clk
//     rod2_wr_prdy                := u_rod2.io.wr_prdy
//     u_rod2.io.wr_pvld           := rod2_wr_pvld
//     u_rod2.io.wr_pd             := io.rod2_wr_pd
//     u_rod2.io.rd_prdy           := rod2_rd_prdy
//     val rod2_rd_pvld            = u_rod2.io.rd_pvld
//     val rod2_rd_pd              = u_rod2.io.rd_pd  

//     val u_rod3 = Module(new NV_NVDLA_SDP_RDMA_fifo(conf.DEPTH, conf.AM_DW))   //need new fifo? depth unknown
//     u_rod3.io.nvdla_core_clk    := io.nvdla_core_clk
//     rod3_wr_prdy                := u_rod3.io.wr_prdy
//     u_rod3.io.wr_pvld           := rod3_wr_pvld
//     u_rod3.io.wr_pd             := io.rod3_wr_pd
//     u_rod3.io.rd_prdy           := rod3_rd_prdy
//     val rod3_rd_pvld            = u_rod3.io.rd_pvld
//     val rod3_rd_pd              = u_rod3.io.rd_pd 


// //=======================================================
// // DATA FIFO: READ SIDE
// //=======================================================

//     val is_last_h = Wire(Bool())
//     val is_last_w = Wire(Bool())
//     val rodx_rd_en = Wire(Bool())
//     when(io.cfg_mode_per_element){
//         rodx_rd_en := true.B
//     }.otherwise{
//         rodx_rd_en := is_last_h & is_last_w
//     }  
//     val out_rdy = Wire(Bool())
//     val rod0_sel = Wire(Bool())
//     val rod1_sel = Wire(Bool())
//     val rod2_sel = Wire(Bool())
//     val rod3_sel = Wire(Bool())

//     rod0_rd_prdy := out_rdy & rodx_rd_en & rod0_sel & !(rod1_sel & !rod1_rd_pvld)
//     rod1_rd_prdy := out_rdy & rodx_rd_en & rod1_sel & !(rod0_sel & !rod0_rd_pvld)
//     rod2_rd_prdy := out_rdy & rodx_rd_en & rod2_sel & !(rod3_sel & !rod3_rd_pvld)
//     rod3_rd_prdy := out_rdy & rodx_rd_en & rod3_sel & !(rod2_sel & !rod2_rd_pvld)

// //==============
// // CMD FIFO
// //==============
//     val roc_wr_pvld = Wire(Bool())
//     val roc_rd_prdy = Wire(Bool())
//     val u_roc = Module(new NV_NVDLA_SDP_RDMA_EG_RO_cfifo(4, 2))
//     u_roc.io.nvdla_core_clk     := io.nvdla_core_clk
//     u_roc.io.pwrbus_ram_pd      := io.pwrbus_ram_pd
//     val roc_wr_prdy             = u_roc.io.roc_wr_prdy
//     u_roc.io.roc_wr_pvld        := roc_wr_pvld
//     u_roc.io.roc_wr_pd          := io.roc_wr_pd
//     u_roc.io.roc_rd_prdy        := roc_rd_prdy
//     val roc_rd_pvld             = u_roc.io.roc_rd_pvld
//     val roc_rd_pd               = u_roc.io.roc_rd_pd

//     val is_last_beat = Wire(Bool())
//     val is_surf_end = Wire(Bool())
//     val roc_rd_en = is_last_beat & (is_surf_end | io.cfg_mode_per_element)
//     val out_accept = Wire(Bool())
//     roc_rd_prdy := roc_rd_en & out_accept
//     val size_of_beat = Mux(roc_rd_pvld, (roc_rd_pd + 1.U), 0.U(3.W))

// //==============
// // END
// //==============

//     val is_line_end = is_last_w
//     val is_surf_end = is_line_end & is_last_h
//     val is_last_c = Wire(Bool())
//     val is_cube_end = is_surf_end & is_last_c

// // #ifdef NVDLA_BATCH_ENABLE
// //==============
// //Batch Count
// //==============
// // ...

// //==============
// // Width Count
// //==============

//     val count_w = RegInit(0.U(13.W))
//     when(out_accept){
//         when(is_line_end){
//             count_w := 0.U
//         }.otherwise{
//             count_w := count_w + 1.U
//         }
//     }
//     is_last_w := (count_w === io.reg2dp_width)

// //==============
// // HEIGHT Count
// //==============

//     val count_h = RegInit(0.U(13.W))
//     when(out_accept){
//         when(is_surf_end){
//             count_h := 0.U
//         }.elsewhen(is_line_end){
//             count_h := count_h + 1.U
//         }
//     }
//     is_last_h := (count_h === io.reg2dp_height)

// //==============
// // SURF Count
// //==============

//     val size_of_surf = Mux(io.cfg_dp_8, 
//                             Cat(false.B, io.reg2dp_channel(12, conf.AM_AW)),
//                             io.reg2dp_channel(12, conf.AM_AW2))
//     val count_c = RegInit(0.U((14-conf.AM_AW).W))   
//     when(out_accept){
//         when(is_cube_end){
//             count_c := 0.U
//         }.elsewhen(is_surf_end){
//             count_c := count_c + 1.U
//         }
//     }
//     is_last_c := (count_c === size_of_surf)

// //==============
// // BEAT CNT: used to foreach 1~4 16E rod FIFOs
// //==============

//     val size_of_elem = Mux((io.cfg_dp_size_1byte | !io.cfg_dp_8), 
//                             1.U(2.W), 
//                             2.U(2.W))
//     val beat_cnt = RegInit(0.U(2.W))                      
//     val beat_cnt_nxt = beat_cnt + size_of_elem

//     when(out_accept){
//         when(io.cfg_mode_per_element){
//             when(is_last_beat){
//                 beat_cnt := 0.U
//             }.otherwise{
//                 beat_cnt := beat_cnt_nxt
//             }
//         }.elsewhen(is_surf_end){
//             when(is_last_beat){
//                 beat_cnt := 0.U
//             }.otherwise{
//                 beat_cnt := beat_cnt_nxt
//             }
//         }
//     }

//     is_last_beat := (beat_cnt_nxt === size_of_beat)
//     val rod_sel = beat_cnt
//     rod0_sel := (beat_cnt === 0.U(2.W))
//     rod1_sel := Mux((io.cfg_dp_size_1byte | !io.cfg_dp_8), 
//                     (beat_cnt === 1.U(2.W)), 
//                     (beat_cnt === 0.U(2.W))) 
//     rod2_sel := (beat_cnt === 2.U(2.W))
//     rod3_sel := Mux((io.cfg_dp_size_1byte | !io.cfg_dp_8), 
//                     (beat_cnt === 3.U(2.W)), 
//                     (beat_cnt === 2.U(2.W))) 


// ////dp int8 one byte per element or int16 two bytes per elment/////////// 

//     val out_data_1bpe = Reg(UInt(conf.AM_DW.W))
//     out_data_1bpe := MuxCase(
//         Fill(conf.AM_DW, false.B),
//         Array(
//             (rod_sel === 0.U) -> rod0_rd_pd,
//             (rod_sel === 1.U) -> rod1_rd_pd,
//             (rod_sel === 2.U) -> rod2_rd_pd,
//             (rod_sel === 3.U) -> rod3_rd_pd
//         ))

//     val out_vld_1bpe = Reg(Bool())
//     out_vld_1bpe := MuxCase(
//         false.B,
//         Array(
//             (rod_sel === 0.U) -> rod0_rd_pvld,
//             (rod_sel === 1.U) -> rod1_rd_pvld,
//             (rod_sel === 2.U) -> rod2_rd_pvld,
//             (rod_sel === 3.U) -> rod3_rd_pvld
//         ))


// ////dp int8 two byte per element/////////// 

//     val out_data_2bpe = Reg(UInt(conf.AM_DW2.W))
//     out_data_2bpe := MuxCase(
//         Fill(conf.AM_DW2, false.B),
//         Array(
//             (rod_sel === 0.U) -> Cat(rod1_rd_pd, rod0_rd_pd),
//             (rod_sel === 2.U) -> Cat(rod3_rd_pd, rod2_rd_pd)
//         ))

//     val out_vld_2bpe = Reg(Bool())
//     out_vld_2bpe := MuxCase(
//         false.B,
//         Array(
//             (rod_sel === 0.U) -> rod0_rd_pvld & rod1_rd_pvld,
//             (rod_sel === 2.U) -> rod2_rd_pvld & rod3_rd_pvld
//         ))

// ////mux out data ////

//     val out_vld = Mux((io.cfg_dp_size_1byte | !io.cfg_dp_8), 
//                         out_vld_1bpe, 
//                         out_vld_2bpe)
//     val out_pd := Cat(is_cube_end, 
//                         Mux(!io.cfg_dp_8,
//                             Cat(0.U(conf.AM_DW.W), out_data_1bpe),
//                             Mux(io.cfg_dp_size_1byte, 
//                                 out_data_1bpe, //source: out_data_1bpe_ext (ut_data_1bpe_ext[16*${i}+15:16*${i}] = {{8{out_data_1bpe[8*${i}+7]}}, out_data_1bpe[8*${i}+7:8*${i}]})
//                                 out_data_2bpe)
//                             )
//                         )
    
//     out_accept := out_vld & out_rdy

//     val pipe_p1 = Module{new NV_NVDLA_BC_pipe(conf.AM_DW2+1)}
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := out_vld
//     out_rdy := pipe_p1.io.ro
//     pipe_p1.io.di := out_pd
//     io.sdp_rdma2dp_valid := pipe_p1.io.vo
//     pipe_p1.io.ri := io.sdp_rdma2dp_ready
//     io.sdp_rdma2dp_pd := pipe_p1.io.dout

//     io.layer_end := io.sdp_rdma2dp_valid & io.sdp_rdma2dp_ready & io.sdp_rdma2dp_pd(AM_DW2)
// }
// }

// class NV_NVDLA_SDP_RDMA_EG_RO_cfifo extends Module {
//     val io = IO(new Bundle{


//         input         nvdla_core_clk;
// input         nvdla_core_rstn;
// output        roc_wr_prdy;
// input         roc_wr_pvld;
// input  [1:0] roc_wr_pd;
// input         roc_rd_prdy;
// output        roc_rd_pvld;
// output [1:0] roc_rd_pd;
// input  [31:0] pwrbus_ram_pd;
//     })
// }