// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_RDMA_EG_ro(implicit val conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         val sdp_rdma2dp_valid = Output(Bool())
//         val sdp_rdma2dp_ready = Input(Bool())
//         val sdp_rdma2dp_pd = Output(UInt((conf.AM_DW2 + 1).W))

//         val rod_wr_vld = Input(Bool())
//         val rod_wr_rdy = Output(Bool())
//         val rod_wr_pd = Input(Vec(4,UInt(conf.AM_DW.W)))
//         val rod_wr_mask = Input(UInt(4.W))

//         val roc_wr_vld = Input(Bool())
//         val roc_wr_rdy = Output(Bool())
//         val roc_wr_pd = Input(UInt(2.W))

//         val cfg_dp_8 = Input(Bool())
//         val cfg_dp_size_1byte = Input(Bool())
//         val cfg_mode_per_element = Input(Bool())
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
//     //=======================================================
//     // DATA FIFO: WRITE SIDE
//     //=======================================================
//     val rod_wr_prdy = Wire(Vec(4, Bool()))
//     val rod_wr_mask_prdy = Wire(Vec(4, Bool()))
//     for(i <- 0 to 3){
//         rod_wr_mask_prdy(i) := io.rod_wr_mask(i) & !rod_wr_prdy(i)
//     }

//     io.rod_wr_rdy := !( rod_wr_mask_prdy.asUInt.orR )
                        
//     val rod_wr_pvld = Wire(Vec(4, Bool()))
//     rod_wr_pvld(0) := io.rod_wr_vld & io.rod_wr_mask(0) & 
//                       !(rod_wr_mask_prdy(1)|rod_wr_mask_prdy(2)|rod_wr_mask_prdy(3))
//     rod_wr_pvld(1) := io.rod_wr_vld & io.rod_wr_mask(1) & 
//                       !(rod_wr_mask_prdy(0)|rod_wr_mask_prdy(2)|rod_wr_mask_prdy(3))
//     rod_wr_pvld(2) := io.rod_wr_vld & io.rod_wr_mask(2) & 
//                       !(rod_wr_mask_prdy(0)|rod_wr_mask_prdy(1)|rod_wr_mask_prdy(3))
//     rod_wr_pvld(3) := io.rod_wr_vld & io.rod_wr_mask(3) & 
//                       !(rod_wr_mask_prdy(0)|rod_wr_mask_prdy(1)|rod_wr_mask_prdy(2))
    
//     val rod_rd_prdy = Wire(Vec(4,Bool()))
//     val rod_rd_pvld = Wire(Vec(4,Bool()))
//     val rod_rd_pd = Wire(Vec(4,UInt(conf.AM_DW.W)))
    
//     val u_rod = Array.fill(4){Module(new NV_NVDLA_IS_pipe(conf.AM_DW))}
//     for(i <- 0 to 3){
//         u_rod(i).io.clk    := io.nvdla_core_clk
//         u_rod(i).io.vi  := rod_wr_pvld(i)
//         rod_wr_prdy(i)  := u_rod(i).io.ro
//         u_rod(i).io.di  := io.rod_wr_pd(i)
//         rod_rd_pvld(i)  := u_rod(i).io.vo
//         u_rod(i).io.ri  := rod_rd_prdy(i)
//         rod_rd_pd(i)    := u_rod(i).io.dout
//     }

//     //=======================================================
//     // DATA FIFO: READ SIDE
//     //=======================================================
//     val is_last_h = Wire(Bool())
//     val is_last_w = Wire(Bool())
//     val is_last_c = Wire(Bool())
//     val rodx_rd_en = Wire(Bool())
//     when(io.cfg_mode_per_element){
//         rodx_rd_en := true.B
//     }.otherwise{
//         rodx_rd_en := is_last_h & is_last_w
//     }  
//     val out_rdy = Wire(Bool())

//     val rod_sel_vec = Wire(Vec(4,Bool()))
//     rod_rd_prdy(0) := out_rdy & rodx_rd_en & rod_sel_vec(0) & !(rod_sel_vec(1) & !rod_rd_pvld(1))
//     rod_rd_prdy(1) := out_rdy & rodx_rd_en & rod_sel_vec(1) & !(rod_sel_vec(0) & !rod_rd_pvld(0))
//     rod_rd_prdy(2) := out_rdy & rodx_rd_en & rod_sel_vec(2) & !(rod_sel_vec(3) & !rod_rd_pvld(3))
//     rod_rd_prdy(3) := out_rdy & rodx_rd_en & rod_sel_vec(3) & !(rod_sel_vec(2) & !rod_rd_pvld(2))

//     //==============
//     // CMD FIFO
//     //==============
//     val roc_rd_prdy = Wire(Bool())
//     val u_roc = Module(new NV_NVDLA_SDP_fifo_flop_based(4, 2))
//     u_roc.io.clk := io.nvdla_core_clk
//     u_roc.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_roc.io.wr_vld := io.roc_wr_vld
//     io.roc_wr_rdy := u_roc.io.wr_rdy
//     u_roc.io.wr_data := io.roc_wr_pd
//     val roc_rd_pvld = u_roc.io.rd_vld
//     u_roc.io.rd_rdy := roc_rd_prdy
//     val roc_rd_pd = u_roc.io.rd_data

//     val is_last_beat = Wire(Bool())
//     val is_surf_end = Wire(Bool())
//     val roc_rd_en = is_last_beat & (is_surf_end | io.cfg_mode_per_element)
//     val out_accept = Wire(Bool())
//     roc_rd_prdy := roc_rd_en & out_accept
//     val size_of_beat = Mux(roc_rd_pvld, (roc_rd_pd +& 1.U), 0.U(3.W))

//     //==============
//     // END
//     //==============
//     val is_line_end = is_last_w
//     is_surf_end := is_line_end & is_last_h
//     val is_cube_end = is_surf_end & is_last_c

//     //==============
//     // Width Count
//     //==============
//     val count_w = RegInit(0.U(13.W))
//     when(out_accept){
//         when(is_line_end){
//             count_w := 0.U
//         }.otherwise{
//             count_w := count_w + 1.U
//         }
//     }
//     is_last_w := (count_w === io.reg2dp_width)

//     //==============
//     // HEIGHT Count
//     //==============
//     val count_h = RegInit(0.U(13.W))
//     when(out_accept){
//         when(is_surf_end){
//             count_h := 0.U
//         }.elsewhen(is_line_end){
//             count_h := count_h + 1.U
//         }
//     }
//     is_last_h := (count_h === io.reg2dp_height)

//     //==============
//     // SURF Count
//     //==============
//     val size_of_surf = Mux(io.cfg_dp_8, Cat(false.B, io.reg2dp_channel(12, conf.AM_AW)), io.reg2dp_channel(12, conf.AM_AW2))
//     val count_c = RegInit(0.U((14-conf.AM_AW).W))   
//     when(out_accept){
//         when(is_cube_end){
//             count_c := 0.U
//         }.elsewhen(is_surf_end){
//             count_c := count_c + 1.U
//         }
//     }
//     is_last_c := (count_c === size_of_surf)

//     //==============
//     // BEAT CNT: used to foreach 1~4 16E rod FIFOs
//     //==============
//     val size_of_elem = Mux(io.cfg_dp_size_1byte | !io.cfg_dp_8, 1.U, 2.U)
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
//     rod_sel_vec(0) := beat_cnt === 0.U
//     rod_sel_vec(1) := Mux(io.cfg_dp_size_1byte | !io.cfg_dp_8, beat_cnt === 1.U, beat_cnt === 0.U) 
//     rod_sel_vec(2) := beat_cnt === 2.U
//     rod_sel_vec(3) := Mux(io.cfg_dp_size_1byte | !io.cfg_dp_8, beat_cnt === 3.U, beat_cnt === 2.U)

//     ////dp int8 one byte per element or int16 two bytes per elment/////////// 
//     val out_data_1bpe = MuxLookup(rod_sel, 0.U,
//         (0 to 3) map {i => i.U -> rod_rd_pd(i)}
//         )

//     val out_vld_1bpe = MuxLookup(rod_sel, 0.U,
//         (0 to 3) map {i => i.U -> rod_rd_pvld(i)}
//         )

//     val out_data_1bpe_ext = VecInit((0 to conf.NVDLA_MEMORY_ATOMIC_SIZE-1) 
//                             map { i => Cat(Fill(8, out_data_1bpe(8*i+7)), out_data_1bpe(8*i+7, 8*i))}).asUInt

//     ////dp int8 two byte per element/////////// 
//     val out_data_2bpe = MuxLookup(rod_sel, 0.U,
//         Array(
//         0.U -> Cat(rod_rd_pd(1), rod_rd_pd(0)),
//         2.U -> Cat(rod_rd_pd(3), rod_rd_pd(2))
//         ))

//     val out_vld_2bpe = MuxLookup(rod_sel, false.B,
//         Array(
//         0.U -> (rod_rd_pvld(1) & rod_rd_pvld(0)),
//         2.U -> (rod_rd_pvld(3) & rod_rd_pvld(2))
//         ))

//     ////mux out data ////
//     val out_vld = Mux(io.cfg_dp_size_1byte | !io.cfg_dp_8, out_vld_1bpe, out_vld_2bpe)
//     val out_pd = Cat(is_cube_end, 
//                  Mux(!io.cfg_dp_8, Cat(0.U(conf.AM_DW.W), out_data_1bpe(conf.AM_DW-1, 0)),
//                  Mux(io.cfg_dp_size_1byte, out_data_1bpe_ext(conf.AM_DW2-1, 0), out_data_2bpe(conf.AM_DW2-1, 0))))
//     out_accept := out_vld & out_rdy

//     val pipe_p1 = Module{new NV_NVDLA_IS_pipe(conf.AM_DW2+1)}
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := out_vld
//     out_rdy := pipe_p1.io.ro
//     pipe_p1.io.di := out_pd
//     io.sdp_rdma2dp_valid := pipe_p1.io.vo
//     pipe_p1.io.ri := io.sdp_rdma2dp_ready
//     io.sdp_rdma2dp_pd := pipe_p1.io.dout

//     io.layer_end := io.sdp_rdma2dp_valid & io.sdp_rdma2dp_ready & io.sdp_rdma2dp_pd(conf.AM_DW2)
// }}


// object NV_NVDLA_SDP_RDMA_EG_roDriver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_EG_ro())
// }

// /////////////////////