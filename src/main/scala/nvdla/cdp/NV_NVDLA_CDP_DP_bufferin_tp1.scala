// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_bufferin_tp1(implicit val conf: nvdlaConfig) extends Module {
//     val buf2sq_data_bw = conf.NVDLA_CDP_ICVTO_BWPE*(conf.NVDLA_CDP_THROUGHPUT+8)
//     val buf2sq_dp_bw = buf2sq_data_bw + 17
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val cdp_rdma2dp_valid = Input(Bool())
//         val cdp_rdma2dp_ready = Output(Bool())
//         val cdp_rdma2dp_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W))
//         val normalz_buf_data_pvld = Output(Bool())
//         val normalz_buf_data_prdy = Input(Bool())
//         val normalz_buf_data = Output(UInt(buf2sq_dp_bw.W))
//     })

// withClock(io.nvdla_core_clk){
// /////////////////////////////////////////////////////////////
// //
// /////////////////////////////////////////////////////////////
// // pipe
//     val nvdla_cdp_rdma2dp_ready = Wire(Bool())
//     val nvdla_cdp_rdma2dp_valid = RegInit(false.B)
//     io.cdp_rdma2dp_ready := nvdla_cdp_rdma2dp_ready || (~nvdla_cdp_rdma2dp_valid)
    
//     when(io.cdp_rdma2dp_valid){
//         nvdla_cdp_rdma2dp_valid := true.B
//     }.elsewhen(nvdla_cdp_rdma2dp_ready){
//         nvdla_cdp_rdma2dp_valid := false.B
//     }

//     val nvdla_cdp_rdma2dp_pd = RegInit(0.U((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W))
//     when(io.cdp_rdma2dp_valid & io.cdp_rdma2dp_ready){
//         nvdla_cdp_rdma2dp_pd := io.cdp_rdma2dp_pd
//     }

// /////////////////////////////////////////////////////////////
// //==============
// // INPUT UNPACK: from RDMA
// //==============

//     val dp_data = nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE-1,0)
//     val dp_pos_w = nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+3, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE)
//     val dp_width = nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+7, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+4)
//     val dp_pos_c = nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+12, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+8)
//     val dp_b_sync = nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+13)
//     val dp_last_w = nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+14)
//     val dp_last_h = nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+15)
//     val dp_last_c = nvdla_cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+16)

//     val is_pos_w       = dp_pos_w
//     val is_width_f     = dp_width
//     val is_width  = is_width_f - true.B
//     val is_pos_c  = dp_pos_c
//     val is_b_sync = dp_b_sync
//     val is_last_w = dp_last_w
//     val is_last_h = dp_last_h
//     val is_last_c = dp_last_c
// ///////////////////////////////////////////////////
//     val rdma2dp_ready_normal = Wire(Bool())
//     val hold_here = RegInit(false.B)
//     nvdla_cdp_rdma2dp_ready := rdma2dp_ready_normal & (~hold_here)
//     val rdma2dp_valid_rebuild = nvdla_cdp_rdma2dp_valid | hold_here

//     val vld = rdma2dp_valid_rebuild
//     val load_din = vld & nvdla_cdp_rdma2dp_ready
//     val load_din_full = rdma2dp_valid_rebuild & rdma2dp_ready_normal
// ///////////////////////////////////////////////////
// //: my $tp = NVDLA_CDP_THROUGHPUT;
// //: my $m  = int(4/$tp+0.99) -1;
// //: if($tp < 4){
// //:     print " val is_4ele_here = (is_pos_c == ${m});  \n";
// //: } else {
// //:     print " val is_4ele_here = (is_pos_c == 0);  \n";
// //: }
//     val is_4ele_here = (is_pos_c === 0.U) // NVDLA_CDP_THROUGHPUT = 8

//     val is_posc_end = (is_pos_c === ((conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT) - 1).U)

//     // val wAIT = "b000".asUInt(3.W)
//     // val nORMAL_C = "b001".asUInt(3.W)
//     // val fIRST_C = "b010".asUInt(3.W)
//     // val sECOND_C = "b011".asUInt(3.W)
//     // val cUBE_END = "b100".asUInt(3.W)

//     val wAIT :: nORMAL_C :: fIRST_C :: sECOND_C :: cUBE_END :: Nil = Enum(5)

//     val stat_cur = RegInit(wAIT)
//     val normalC2CubeEnd = RegInit(false.B)
//     val more2less = Wire(Bool())
//     val width_pre_cnt = RegInit(0.U(4.W))
//     val width_pre = RegInit(0.U(4.W))
//     val is_hold_4ele_done = Wire(Bool())
//     val cube_done = Wire(Bool())

//     switch (stat_cur) {
//         is (wAIT) {
//             when(is_b_sync & is_4ele_here & load_din){
//                 stat_cur := nORMAL_C // NVDLA_MEMORY_ATOMIC_SIZE = 32
//             }
//         }
//         is (nORMAL_C) {
//             when(is_b_sync & is_posc_end & is_last_c & is_last_h & is_last_w & load_din){
//                 normalC2CubeEnd := true.B
//                 stat_cur := cUBE_END
//             }.elsewhen(is_b_sync & is_posc_end & is_last_c & load_din){
//                 stat_cur := fIRST_C
//             }
//         }
//         is (fIRST_C) {
//             when((is_4ele_here & (is_pos_w === is_width) & (~more2less) & load_din)
//                 || (more2less & (width_pre_cnt === width_pre) & is_hold_4ele_done & hold_here & rdma2dp_ready_normal)){
//                     stat_cur := sECOND_C // NVDLA_MEMORY_ATOMIC_SIZE = 32
//                 }
//         }
//         is (sECOND_C) {
//             when(is_b_sync & load_din){
//                 stat_cur := nORMAL_C
//             }
//         }
//         is (cUBE_END) {
//             when(cube_done){
//                 stat_cur := wAIT
//             }
//         }
//     } 

//     val data_shift_valid = RegInit(false.B)
//     val data_shift_ready = Wire(Bool())
//     rdma2dp_ready_normal := (~data_shift_valid) | data_shift_ready
//     when(vld){
//         data_shift_valid := true.B
//     }.elsewhen(data_shift_ready){
//         data_shift_valid := false.B
//     }

//     val buf_dat_vld = RegInit(false.B)
//     val buf_dat_rdy = Wire(Bool())
//     data_shift_ready := (~buf_dat_vld | buf_dat_rdy)

//     val hold_here_dly = RegInit(false.B)
//     val stat_cur_dly = RegInit(0.U(3.W))
//     val data_shift_load_all = data_shift_ready & data_shift_valid
//     val data_shift_load = data_shift_load_all & ((~hold_here_dly)  | (stat_cur_dly === cUBE_END))

//     val data_shift = RegInit(
//         VecInit(
//             Seq.fill(2)
//             (VecInit(
//                 Seq.fill(8)(0.U((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE).W)))
//                 )
//                 )
//             )   //8/conf.NVDLA_CDP_THROUGHPUT+1 = 2, 2 columns
//     val data_1stC = RegInit(
//         VecInit(
//             Seq.fill(8)(0.U((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE).W))
//         )
//     )   // $tp = NVDLA_CDP_THROUGHPUT = 8,  $reg_1stc_num = int(4/$tp) = 1, 1 column

//     val cube_end_width_cnt = RegInit(0.U(4.W))

//     switch (stat_cur) {
//         is (wAIT) {
//             when(load_din){
//                 for(i <- 0 to 7){
//                     when(is_pos_w === i.U){
//                         data_shift(0)(i) := dp_data
//                     }
//                     data_shift(0)(i) := 0.U
//                     data_shift(1)(i) := 0.U
//                 }
//             }
//         }
//         is (nORMAL_C) {
//             when(load_din){
//                 for(i <- 0 to 7){
//                     when(is_pos_w === i.U){
//                         data_shift(0)(i) := dp_data
//                         data_shift(1)(i) := data_shift(0)(i)
//                     }
//                 }
//             }
//         }
//         is (fIRST_C) {
//             for(i <- 0 to 7){
//                 when(hold_here & rdma2dp_ready_normal){
//                     when(width_pre_cnt === i.U){
//                         data_shift(0)(i) := 0.U
//                         data_shift(1)(i) := data_shift(0)(i)
//                     }
//                 }.elsewhen((is_pos_w === i.U) & load_din){
//                     data_1stC(i) := dp_data
//                     data_shift(0)(i) := 0.U
//                     data_shift(1)(i) := data_shift(0)(i)
//                 }
//             }
//         }
//         is (sECOND_C) {
//             when(load_din){
//                 for(i <- 0 to 7){
//                     when(is_pos_w === i.U){
//                         data_shift(0)(i) := dp_data
//                     }
//                 }
//             }
//         }
//         is (cUBE_END) {
//             when(rdma2dp_ready_normal){
//                 for(i <- 0 to 7){
//                     when(cube_end_width_cnt === i.U){
//                         data_shift(0)(i) := 0.U
//                         data_shift(1)(i) := data_shift(0)(i)
//                     }
//                 }
//             }
//         }
//     }

//     when((stat_cur===nORMAL_C) & is_last_c & is_b_sync & is_posc_end & load_din){
//         width_pre := is_width
//     }

//     val width_cur_1 = Reg(UInt(4.W))
//     when((stat_cur===fIRST_C) & (is_pos_w === 0.U) & (is_pos_c === 0.U)){
//         width_cur_1 := is_width 
//     }.otherwise{
//         width_cur_1 := 0.U
//     }

//     val width_cur_2 = RegInit(0.U(4.W))
//     when((stat_cur===fIRST_C) & (is_pos_w === 0.U) & load_din){
//         width_cur_2 := is_width
//     }

//     val width_cur = Mux(((stat_cur===fIRST_C) & (is_pos_w === 0.U)), width_cur_1, width_cur_2)

//     more2less := (stat_cur===fIRST_C) & (width_cur < width_pre)
//     val less2more = (stat_cur===fIRST_C) & (width_cur > width_pre)
//     val l2m_1stC_vld = (stat_cur===fIRST_C) & less2more & (is_pos_w <= width_pre)

//     when((stat_cur===fIRST_C) & more2less){
//         when((is_pos_w===is_width) & load_din){
//             hold_here := true.B
//         }.elsewhen((width_pre_cnt === width_pre) & rdma2dp_ready_normal){
//             hold_here := false.B
//         }
//     }.elsewhen(normalC2CubeEnd){
//         hold_here := true.B
//     }.elsewhen(cube_done){
//         hold_here := false.B
//     }

//     when((stat_cur===fIRST_C) & more2less){
//         when((is_pos_w===is_width) & load_din){
//             width_pre_cnt := is_width + 1.U
//         }.elsewhen(hold_here & rdma2dp_ready_normal){
//             width_pre_cnt := width_pre_cnt + 1.U
//         }
//     }.otherwise{
//         width_pre_cnt := 0.U
//     }

//     val hold_4ele_cnt = RegInit(0.U(2.W))
//     when((stat_cur===fIRST_C) & more2less & hold_here & (width_pre_cnt === width_pre) & rdma2dp_ready_normal){
//         when(is_hold_4ele_done){
//             hold_4ele_cnt := 0.U
//         }.otherwise{
//             hold_4ele_cnt := hold_4ele_cnt + true.B
//         }
//     }

// //: my $atomm = NVDLA_MEMORY_ATOMIC_SIZE;   = 32
// //: my $tp = NVDLA_CDP_THROUGHPUT;  = 8
// //: my $m  = int(4/$tp+0.99) -1;    = 0
// //:     print "assign is_hold_4ele_done = (hold_4ele_cnt == ${m});  ";

//     is_hold_4ele_done := (hold_4ele_cnt === 0.U)

// //the last block data need to be output in cube end

//     val last_width = RegInit(0.U(4.W))
//     when(normalC2CubeEnd & load_din){
//         last_width := is_width
//     }

//     when(stat_cur === cUBE_END){
//         when(rdma2dp_ready_normal){
//             when(cube_end_width_cnt === last_width){
//                 cube_end_width_cnt := 0.U
//             }.otherwise{
//                 cube_end_width_cnt := cube_end_width_cnt + 1.U
//             }
//         }
//     }.otherwise{
//         cube_end_width_cnt := 0.U
//     } 

//     val cube_end_c_cnt = RegInit(0.U(3.W))
//     when(stat_cur === cUBE_END){
//         when(rdma2dp_ready_normal){
//             when(cube_end_width_cnt === last_width){
//                 cube_end_c_cnt := cube_end_c_cnt + 1.U
//             }
//         }
//     }.otherwise{
//         cube_end_c_cnt := 0.U
//     }

// //: my $tp = NVDLA_CDP_THROUGHPUT;      = 8
// //: if( $tp >= 4 ) {
// //:     print " assign cube_done = (stat_cur==CUBE_END) & (cube_end_width_cnt == last_width) & rdma2dp_ready_normal;    \n";
// //: } elsif( $tp == 2 ) {
// //:     print " assign cube_done = (stat_cur==CUBE_END) & (cube_end_width_cnt == last_width) & (cube_end_c_cnt == 3'd1) & rdma2dp_ready_normal; \n";
// //: } elsif( $tp == 1 ) {
// //:     print " assign cube_done = (stat_cur==CUBE_END) & (cube_end_width_cnt == last_width) & (cube_end_c_cnt == 3'd3) & rdma2dp_ready_normal; \n";
// //: }

//     cube_done := (stat_cur===cUBE_END) & (cube_end_width_cnt === last_width) & rdma2dp_ready_normal

// //1pipe delay for buffer data generation

//     when(load_din_full === true.B){
//         stat_cur_dly := stat_cur
//     }

//     val more2less_dly = RegInit(false.B)
//     when(load_din_full === true.B){
//         more2less_dly := more2less
//     }

//     val less2more_dly = RegInit(false.B)
//     when(load_din_full === true.B){
//         less2more_dly := less2more
//     }

//     when(load_din_full === true.B){
//         hold_here_dly := hold_here
//     }

//     val is_pos_w_dly = RegInit(0.U(4.W))
//     when((stat_cur === cUBE_END) & rdma2dp_ready_normal){
//         is_pos_w_dly := cube_end_width_cnt
//     }.elsewhen(load_din){
//         is_pos_w_dly := is_pos_w
//     }

//     val width_pre_cnt_dly = RegInit(0.U(4.W))
//     when(load_din_full){
//         width_pre_cnt_dly := width_pre_cnt
//     }

//     val width_pre_dly = RegInit(0.U(4.W))
//     when(load_din_full){
//         width_pre_dly := width_pre
//     }

// /////////////////////////////
// //buffer data generation for output data

//     val buffer_data = RegInit(0.U(buf2sq_data_bw.W))
// //: if(NVDLA_CDP_THROUGHPUT == 1) {
// // ...
// //: if(NVDLA_CDP_THROUGHPUT == 2) {
// // ...
// // NVDLA_CDP_THROUGHPUT = 8 , no need for scripting this part currently

//     when(data_shift_valid){
//         buf_dat_vld := true.B
//     }.elsewhen(buf_dat_rdy){
//         buf_dat_vld := false.B
//     }

// //assign buf_dat_rdy = buffer_ready;

//     val stat_cur_dly2 = RegInit(0.U(3.W))
//     when(data_shift_load_all){
//         stat_cur_dly2 := stat_cur_dly
//     }

//     val less2more_dly2 = RegInit(false.B)
//     when(data_shift_load_all){
//         less2more_dly2 := less2more_dly
//     }

//     val is_pos_w_dly2 = RegInit(0.U(4.W))
//     when(data_shift_load_all){
//         is_pos_w_dly2 := is_pos_w_dly
//     }

//     val width_pre_dly2 = RegInit(0.U(4.W))
//     when(data_shift_load_all){
//         width_pre_dly2 := width_pre_dly
//     }

//     val buffer_data_vld = Reg(Bool())
//     when(
//         ((stat_cur_dly2===fIRST_C) & less2more_dly2 & 
//         (is_pos_w_dly2 > width_pre_dly2)) || (stat_cur_dly2===wAIT)
//         ){
//         buffer_data_vld := false.B
//     }.otherwise{
//         buffer_data_vld := buf_dat_vld
//     }

// ///////////////////////////////////////////////////////////////////////////////////////////
// //output data_info generation
// ///////////////////////////////////////////////////////////////////////////////////////////

//     val fIRST_C_end = ((stat_cur===fIRST_C) & (width_pre_cnt === width_pre) & more2less & rdma2dp_ready_normal)
//     val fIRST_C_bf_end = ((stat_cur===fIRST_C) & (width_pre_cnt < width_pre) & more2less)

//     val width_align = RegInit(0.U(4.W))
//     when((is_b_sync & load_din & (~fIRST_C_bf_end)) | fIRST_C_end){
//         width_align := is_width
//     }

//     val last_w_align = RegInit(false.B)
//     when((is_b_sync & load_din & (~fIRST_C_bf_end)) | fIRST_C_end){
//         last_w_align := is_last_w
//     }

//     val last_h_align = RegInit(false.B)
//     when((is_b_sync & load_din & (~fIRST_C_bf_end)) | fIRST_C_end){
//         last_h_align := is_last_h
//     }

//     val last_c_align = RegInit(false.B)
//     when((is_b_sync & load_din & (~fIRST_C_bf_end)) | fIRST_C_end){
//         last_c_align := is_last_c
//     }

//     val pos_c_align = RegInit(0.U(5.W))
//     when(fIRST_C_end){
//         pos_c_align := 0.U
//     }.elsewhen(is_b_sync & load_din & (~fIRST_C_bf_end)){
//         pos_c_align := is_pos_c
//     }

//     val pos_w_align = Reg(UInt(4.W))

//     when(stat_cur === cUBE_END){
//         pos_w_align := cube_end_width_cnt
//     }.elsewhen(stat_cur === wAIT){
//         pos_w_align := 0.U
//     }.elsewhen(stat_cur === fIRST_C){
//         when(more2less){
//             when(hold_here){
//                 pos_w_align := width_pre_cnt
//             }.otherwise{
//                 pos_w_align := is_pos_w
//             }
//         }.elsewhen(less2more){
//             when(is_pos_w <= width_pre){
//                 pos_w_align := is_pos_w
//             }.otherwise{
//                 pos_w_align := 0.U
//             }
//         }.otherwise{
//             pos_w_align := is_pos_w
//         }
//     }.otherwise{
//         pos_w_align := is_pos_w
//     }

//     val b_sync_align = Reg(Bool())
//     when(stat_cur === cUBE_END){
//         b_sync_align := cube_done
//     }.elsewhen(stat_cur === wAIT){
//         b_sync_align := false.B
//     }.elsewhen(stat_cur === fIRST_C){
//         when(more2less){
//             b_sync_align := (width_pre_cnt === width_pre)
//         }.elsewhen(less2more){
//             b_sync_align := (is_pos_w === width_pre) & load_din
//         }.otherwise{
//             b_sync_align := (is_b_sync & load_din)
//         }
//     }.otherwise{
//         b_sync_align := (is_b_sync & load_din)
//     }

// ///////////////////
// //Two cycle delay
// ///////////////////

//     val pos_w_dly1 = RegInit(0.U(4.W))
//     val width_dly1 = RegInit(0.U(4.W))
//     val pos_c_dly1 = RegInit(0.U(5.W))
//     val b_sync_dly1 = RegInit(false.B)
//     val last_w_dly1 = RegInit(false.B)
//     val last_h_dly1 = RegInit(false.B)
//     val last_c_dly1 = RegInit(false.B)
//     when((((stat_cur===nORMAL_C)||(stat_cur===sECOND_C)) & load_din)
//       || ((stat_cur===cUBE_END) & rdma2dp_ready_normal)){
//         pos_w_dly1  :=  pos_w_align
//         width_dly1  :=  width_align
//         pos_c_dly1  :=  pos_c_align
//         b_sync_dly1 :=  b_sync_align
//         last_w_dly1 :=  last_w_align
//         last_h_dly1 :=  last_h_align
//         last_c_dly1 :=  last_c_align
//       }.elsewhen(stat_cur===fIRST_C){
//           when(more2less & rdma2dp_ready_normal){
//               when(hold_here){
//                 pos_w_dly1  :=  pos_w_align
//                 width_dly1  :=  width_align
//                 pos_c_dly1  :=  pos_c_align
//                 b_sync_dly1 :=  b_sync_align
//                 last_w_dly1 :=  last_w_align
//                 last_h_dly1 :=  last_h_align
//                 last_c_dly1 :=  last_c_align
//               }.elsewhen(load_din){
//                 pos_w_dly1  :=  pos_w_align
//                 width_dly1  :=  width_align
//                 pos_c_dly1  :=  pos_c_align
//                 b_sync_dly1 :=  b_sync_align
//                 last_w_dly1 :=  last_w_align
//                 last_h_dly1 :=  last_h_align
//                 last_c_dly1 :=  last_c_align
//               }
//           }.elsewhen(less2more){
//               when(l2m_1stC_vld & load_din){
//                 pos_w_dly1  :=  pos_w_align
//                 width_dly1  :=  width_align
//                 pos_c_dly1  :=  pos_c_align
//                 b_sync_dly1 :=  b_sync_align
//                 last_w_dly1 :=  last_w_align
//                 last_h_dly1 :=  last_h_align
//                 last_c_dly1 :=  last_c_align
//               }
//           }.elsewhen(load_din){
//                 pos_w_dly1  :=  pos_w_align
//                 width_dly1  :=  width_align
//                 pos_c_dly1  :=  pos_c_align
//                 b_sync_dly1 :=  b_sync_align
//                 last_w_dly1 :=  last_w_align
//                 last_h_dly1 :=  last_h_align
//                 last_c_dly1 :=  last_c_align
//           }
//       }

//     val buffer_pos_w = RegInit(0.U(4.W))
//     when(data_shift_load_all){
//         buffer_pos_w := pos_w_dly1
//     }

//     val buffer_width = RegInit(0.U(4.W))
//     when(data_shift_load_all){
//         buffer_width := width_dly1
//     }

//     val buffer_pos_c = RegInit(0.U(5.W))
//     when(data_shift_load_all){
//         buffer_pos_c := pos_c_dly1
//     }

//     val buffer_b_sync = RegInit(false.B)
//     when(data_shift_load_all){
//         buffer_b_sync := b_sync_dly1
//     }

//     val buffer_last_w = RegInit(false.B)
//     when(data_shift_load_all){
//         buffer_last_w := last_w_dly1
//     }

//     val buffer_last_h = RegInit(false.B)
//     when(data_shift_load_all){
//         buffer_last_h := last_h_dly1
//     }

//     val buffer_last_c = RegInit(false.B)
//     when(data_shift_load_all){
//         buffer_last_c := last_c_dly1
//     }

// /////////////////////////////////////////

//     val buffer_pd = Cat(
//         buffer_last_c, buffer_last_h, buffer_last_w, buffer_b_sync, 
//         buffer_pos_c, buffer_width, buffer_pos_w, buffer_data
//         )

//     val buffer_valid = buffer_data_vld

// /////////////////////////////////////////


//     val pipe_p1 = Module(new NV_NVDLA_IS_pipe((conf.NVDLA_CDP_THROUGHPUT+8)*conf.NVDLA_CDP_ICVTO_BWPE+17))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := buffer_valid
//     val buffer_ready = pipe_p1.io.ro
//     pipe_p1.io.di := buffer_pd
//     io.normalz_buf_data_prdy := pipe_p1.io.vo
//     pipe_p1.io.ri := io.normalz_buf_data_pvld
//     io.normalz_buf_data := pipe_p1.io.dout

//     buf_dat_rdy := buffer_ready

// /////////////////////////////////////////

// //==============
// //function points
// //==============
// }}

// object NV_NVDLA_CDP_DP_bufferin_tp1Driver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_bufferin_tp1())
// }

