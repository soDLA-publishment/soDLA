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
//     val off_flying_en = (io.datin_src_cfg === 1.U)
//     val on_flying_en = (io.datin_src_cfg === 0.U)
//     val pdp_datin_pd_f_mux0 = Mux(off_flying_en, io.pdp_rdma2dp_pd.bits, io.sdp2pdp_pd.bits)
//     val pdp_datin_pvld_mux0 = Mux(off_flying_en, io.pdp_rdma2dp_pd.valid, io.sdp2pdp_pd.valid)
//     val pdp_rdma2dp_ready = pdp_datin_prdy_mux0 & off_flying_en
//     val sdp2pdp_ready = pdp_datin_prdy_mux0 & on_flying_en
//     val pdp_datin_prdy_mux0 = pdp_datin_prdy_f
//     //---------------------------------------------------------------
//     //---------------------------------------------------------------
//     //data select after switch
//     val pdp_datin_pd_f_0 = pdp_datin_pd_f_mux0
//     val pdp_datin_pvld_f = pdp_datin_pvld_mux0

//     //===============================================================
//     // 1 cycle pipeline for DW timing closure inside unit1d sub modudle
//     // DW has replaced by normal hls fp17 adder, this pipeline keep here
//     //---------------------------------------------------------------conf.NVDLA_MEMORY_ATOMIC_SIZE
//     val posc_last = pdp_datin_pd_f_0(conf.DBW+8, conf.DBW+4) === conf.ENUM.U
//     val pdp_din = Wire(Vec(conf.NVDLA_PDP_THROUGHPUT, UInt((conf.NVDLA_BPE+3).W)))
//     for(0 <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
//         pdp_din(i) := Cat(Fill(3, pdp_datin_pd_f_0(conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_BPE+conf.NVDLA_BPE-1)), 
//                      pdp_datin_pd_f_0(conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_BPE+conf.NVDLA_BPE-1, conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_BPE))
//     }

//     val datain_ext = pdp_din.asUInt

//     val pdp_datin_prdy = Wire(Bool())
//     val pdp_datin_pd_f0 = Cat(posc_last, pdp_datin_pd_f_0(conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_BPE+13, conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_BPE), datain_ext)
//     val pipe_0 = Module{new NV_NVDLA_IS_pipe(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+3) + 15)}
//     pipe_0.io.vi := pdp_datin_pvld_f
//     val pdp_datin_prdy_f0 := pipe_0.io.ro
//     pipe_0.io.di := pdp_datin_pd_f0
//     val pdp_datin_pvld0 = pipe_0.io.vo
//     pipe_0.io.ri := pdp_datin_prdy
//     val pdp_datin_pd0 = pipe_0.io.dout

//     val pdp_datin_prdy_f = pdp_datin_prdy_f0
//     val pdp_datin_pvld = pdp_datin_pvld0
//     val pdp_datin_pd = pdp_datin_pd0

//     val cur_datin_disable = RegInit(false.B)
//     val pdpw_active_en = RegInit(false.B)
//     pdp_datin_prdy_0 = ~ cur_datin_disable
//     val pdp_datin_prdy_1 = Wire(Bool())
//     pdp_datin_prdy := (pdp_datin_prdy_0 & pdp_datin_prdy_1) & pdpw_active_en; 

//     //==============================================================
//     //new splitw
//     //---------------------------------------------------------------
//     val load_din = Wire(Bool())
//     val splitw_end = Wire(Bool())
//     val bsync = pdp_datin_pd(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+3)+9)
//     val splitw_end_sync = Mux(load_din, pdp_datin_pd(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+3)+12), false.B)
//     val pdp_cube_sync = pdp_datin_pd(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+3)+13)
//     val pdp_cube_end = pdp_cube_sync & bsync & load_din
//     val splitw_cnt = RegInit("b0".asUInt(8.W))

//     when(splitw_end & bsync & splitw_end_sync & load_din){
//         splitw_cnt := 0.U
//     }
//     .elsewhen(splitw_end_sync & bsync & load_din){
//         splitw_cnt := splitw_cnt + 1.U
//     }

//     splitw_end := (splitw_cnt === io.pooling_splitw_num_cfg)
//     splitw_start := (splitw_cnt === 0.U)

//     //===============================================================
//     //config info
//     //
//     //---------------------------------------------------------------
//     val non_splitw = io.pooling_splitw_num_cfg === 0.U
//     val first_splitw_en = ~non_splitw & splitw_start
//     val last_splitw_en = ~non_splitw & splitw_end 

//     val overlap = Mux(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width, io.reg2dp_kernel_stride_width - io.reg2dp_kernel_width, 
//                   io.reg2dp_kernel_width - io.reg2dp_kernel_stride_width)

//     val pooling_pwidth = Wire(UInt(13.W))

//     when(non_splitw){
//         pooling_pwidth := io.reg2dp_cube_in_width
//     }
//     .elsewhen(splitw_end){
//         when(io.reg2dp_kernel_stride_width > io.reg2dp_kernel_width){
//             pooling_pwidth := io.pooling_lwidth_cfg -& overlap
//         }
//         .otherwise{
//             pooling_pwidth := io.pooling_lwidth_cfg +& overlap
//         }
//     }
//     .elsewhen(splitw_start){
//         pooling_pwidth := io.pooling_fwidth_cfg
//     }
//     .otherwise{
//         when(io.reg2dp_kernel_stride_width > io.reg2dp_kernel_width){
//             pooling_pwidth := io.pooling_mwidth_cfg -& overlap
//         }
//         .otherwise{
//             pooling_pwidth := io.pooling_mwidth_cfg +& overlap
//         }
//     }

//     //==============================================================
//     //enable pdp datapath
//     //--------------------------------------------------------------
//     val pdpw_active_en = RegInit(false.B)
//     when(pdp_op_start){
//         pdpw_active_en := true.B
//     }
//     .elsewhen(pdp_cube_end){
//         pdpw_active_en := false.B
//     }

//     //==============================================================
//     //stride count in padding bits
//     //
//     //--------------------------------------------------------------
//     val padding_left = Wire(UInt(3.W))

//     when(non_splitw){
//         padding_left := io.padding_h_cfg
//     }
//     .elsewhen(first_splitw_en & (~splitw_end_sync)){
//         padding_left := io.padding_h_cfg
//     }
//     .otherwise{
//         padding_left := 0.U
//     }

//     //stride ==1 
//     val padding_stride1_num = padding_left
//     //stride ==2
//     val padding_stride2_num = padding_left(2, 1)
//     //stride ==3
//     val padding_stride3_num = Mux(padding_left >= 6.U, 2.U, 
//                               Mux(padding_left >= 3.U, 1.U,
//                               0.U))
//     //stride==4 5 6 7
//     val pooling_stride_h = pooling_stride_h_cfg +& 1.U
//     val padding_stride4_num = Mux(padding_left > io.pooling_stride_h_cfg, 1.U, 0.U)
//     //number needed for padding in horizontal direction
//     val padding_stride_num = MuxLookup(io.pooling_stride_h_cfg, padding_stride4_num,
//                                  Array(0.U -> padding_stride1_num,
//                                        1.U -> padding_stride2_num,
//                                        2.U -> padding_stride3_num))
//     val strip_xcnt_offset = (padding_left - padding_stride_num*pooling_stride_h)(2, 0)

//     //////////////////////////////////////////////////
//     // line reg use num calculation, "+1"
//     //------------------------------------------------
//     val regs_num = RegInit(0.U(3.W))
//     //stride 1
//     val line_regs_1 = io.pooling_size_h_cfg
//     //stride 2
//     val line_regs_2 = io.pooling_size_h_cfg(2, 1)
//     //stride 3
//     val line_regs_3 = Mux(io.pooling_size_h_cfg>5.U, 2.U, 
//                       Mux(io.pooling_size_h_cfg>2.U, 1.U, 0.U))
//     //stride 4 5 6 7
//     val line_regs_4 = Mux(io.pooling_size_h_cfg>io.pooling_stride_h_cfg, 1.U, 0.U)

//     when(io.pdp_op_start){
//         regs_num := MuxLookup(io.pooling_stride_h_cfg, line_regs_4, 
//                         Array(0.U -> line_regs_1,
//                               1.U -> line_regs_2,
//                               2.U -> line_regs_3))
//     }

//     //////////////////////////////////////////////////
 
//     //==============================================================
//     //1D pooling stride/size counter
//     //
//     //-------------------------------------------------------------
//     //stride start
//     val strip_xcnt_stride = RegInit(0.U(4.U))
//     val strip_xcnt_stride_f = Wire(UInt(4.W))
//     val line_last_stripe_done = Wire(Bool())

//     load_din := pdp_datin_prdy & pdp_datin_pvld
//     val pooling_size_h = io.pooling_size_h_cfg +& 1.U
//     val pdp_din_lc = pdp_datin_pd(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+3)+14)
//     val strip_recieve_done = load_din & pdp_din_lc
//     val stride_end = strip_recieve_done & (strip_xcnt_stride === io.pooling_stride_h_cfg)
//     val init_cnt = line_last_stripe_done | io.pdp_op_start

//     when(init_cnt){
//         strip_xcnt_stride_f := strip_xcnt_offset
//     }
//     .elsewhen(stride_end){
//         strip_xcnt_stride_f := 0.U
//     }
//     .elsewhen(strip_recieve_done){
//         strip_xcnt_stride_f := strip_xcnt_stride + 1.U
//     }
//     .otherwise{
//         strip_xcnt_stride_f := strip_xcnt_stride
//     }

//     when(init_cnt | stride_end | strip_recieve_done === 1.U){
//         strip_xcnt_stride := strip_xcnt_stride_f
//     }

//     //pooling result ready
//     val strip_xcnt_psize = RegInit(0.U(3.W))

//     val overlap_ff = (io.pooling_size_h_cfg - io.pooling_stride_h_cfg)(2, 0)

//     when(init_cnt){
//         strip_xcnt_psize := padding_left
//     }
//     .elsewhen(io.pooling_size_h_cfg >= io.pooling_stride_h_cfg){
//         when(pooling_1d_rdy){
//             strip_xcnt_psize := overlap_ff
//         }
//         .elsewhen(strip_recieve_done){
//             strip_xcnt_psize := strip_xcnt_psize + 1.U
//         }
//     }
//     .otherwise{ // pooling_size < stride
//         when(strip_xcnt_stride_f <= io.pooling_size_h_cfg){
//             strip_xcnt_psize := strip_xcnt_stride_f
//         }
//         .otherwise{
//             strip_xcnt_psize := 0.U
//         }
//     }

//     /////////////////////////////////////////////////////////
//     //input data bubble control logic generation
//     //-------------------------------------------------------
//     val pooling_size = pooling_size_h
//     val stride = pooling_stride_h
//     val pad_l = padding_left

//     val pad_r = io.reg2dp_pad_right_cfg
 
//     val flush_num_cal = Wire(UInt(3.W))
//     val stride_1x = Wire(UInt(5.W))
//     val stride_2x = Wire(UInt(6.W))
//     val stride_3x = Wire(UInt(7.W))
//     val stride_4x = Wire(UInt(7.W))
//     val stride_5x = Wire(UInt(8.W))
//     val stride_6x = Wire(UInt(8.W))
//     val stride_7x = Wire(UInt(8.W))
//     //element/line num need flush at lint/surface end
//     when(pad_r < stride_1x){
//         flush_num_cal := 0.U
//     }
//     .elsewhen(pad_r < stride_2x){
//         flush_num_cal := 1.U
//     }
//     .elsewhen(pad_r < stride_3x){
//         flush_num_cal := 2.U
//     }
//     .elsewhen(pad_r < stride_4x){
//         flush_num_cal := 3.U
//     }
//     .elsewhen(pad_r < stride_5x){
//         flush_num_cal := 4.U
//     }
//     .elsewhen(pad_r < stride_6x){
//         flush_num_cal := 5.U
//     }
//     .elsewhen(pad_r < stride_7x){
//         flush_num_cal := 6.U
//     }
//     .otherwise{
//         flush_num_cal := 7.U
//     }
//     //small input detect
//     val non_split_small_active = (non_splitw & (!io.reg2dp_cube_in_width(12, 3).orR) & (io.reg2dp_cube_in_width +& io.reg2dp_pad_left(2, 0) < io.reg2dp_kernel_width))
//     val split_small_active = (~non_splitw) & ((big_stride & ((pooling_lwidth_cfg -& overlap) < io.reg2dp_kernel_width))
//                                          | ((~big_stride) & ((pooling_lwidth_cfg +& overlap) < io.reg2dp_kernel_width)))
//     val non_split_w_pl = io.reg2dp_cube_in_width +& io.reg2dp_pad_left
//     val non_split_w_pl_pr = non_split_w_pl +& io.reg2dp_pad_right

//     val big_stride = io.reg2dp_kernel_stride_width >= io.reg2dp_kernel_width
//     val split_w_olap = Mux(big_stride, pooling_lwidth_cfg -& overlap, pooling_lwidth_cfg +& overlap)
//     val split_w_olap_pr = split_w_olap +& io.reg2dp_pad_right

//     //pad_right remain afrer 1st kernel pooling
//     val pad_r_remain = Wire(UInt(8.W))
//     when(non_split_small_active){
//         pad_r_remain := non_split_w_pl_pr -& io.reg2dp_kernel_width
//     } 
//     .elsewhen(split_small_active){
//         pad_r_remain := pad_r_remain -& io.reg2dp_kernel_width
//     }
//     .otherwise{
//         pad_r_remain := 0.U
//     }

//     //how many need bubble after 1st kernel pooling
//     val samllW_flush_num = Wire(UInt(3.W))
//     val flush_num = Wire(UInt(3.W))

//     when(flush_num_cal === 0.U){
//         flush_num := 0.U
//     }
//     .elsewhen(non_split_small_active | split_small_active){
//         flush_num := samllW_flush_num
//     }
//     .otherwise{
//         flush_num := flush_num_cal
//     }

//     stride_1x := stride
//     stride_2x := Cat(stride, false.B)
//     stride_3x := stride_2x +& stride
//     stride_4x := Cat(stride, false.B, false.B)
//     stride_5x := stride_4x +& stride 
//     stride_6x := stride_3x +& stride_3x
//     stride_7x := stride_4x +& stride_3x

//     //the 1st element/line num need output data
//     val kernel_padl = pooling_size -& pad_l
//     val partial_w_last = io.pooling_lwidth_cfg +& 1.U
//     val cube_width_in = io.reg2dp_cube_in_width +& 1.U
//     val ks_width = io.reg2dp_kernel_stride_width +& 1.U
//     val k_add_ks = (pooling_size +& ks_width).asUInt(11.W)
 
//     val first_out_num = Wire(UInt(4.W))
//     when(non_splitw){
//         when(non_split_small_active){
//             first_out_num := cube_width_in
//         }
//         .otherwise{
//             first_out_num := kernel_padl
//         }
//     }
//     .otherwise{
//         when(first_splitw_en){
//             first_out_num := kernel_padl
//         }
//         .elsewhen(last_splitw_en & split_small_active){
//             when(big_stride){
//                 first_out_num := partial_w_last
//             }
//             .otherwise{
//                 first_out_num := partial_w_last +& overlap
//             }
//         }
//         .otherwise{
//             when(big_stride){
//                 first_out_num := k_add_ks
//             }
//             .otherwise{
//                 first_out_num := pooling_size
//             }
//         }
//     }

//     val need_bubble = RegInit(false.B)
//     val bubble_num = RegInit("b0".asUInt(3.W))
//     when(pdp_cube_end){
//         when(flush_num.orR){
//             need_bubble := true.B
//             bubble_num := flush_num
//         }
//         .otherwise{
//             need_bubble := false.B
//             bubble_num := 0.U
//         }
//     }
//     .elsewhen(non_splitw){
//         when(pdp_op_start){
//             when(flush_num >= first_out_num){
//                 need_bubble := true.B
//                 bubble_num := flush_num -& first_out_num +& 1.U
//             }
//             .otherwise{
//                 need_bubble := false.B
//                 bubble_num := 0.U
//             }
//         }
//     }
//     .otherwise{//split mode
//         when(splitw_end){
//             when(flush_num >= first_out_num){
//                 need_bubble := true.B
//                 bubble_num := flush_num -& first_out_num +& 1.U
//             }
//             .otherwise{
//                 need_bubble := false.B
//                 bubble_num := 0.U
//             }
//         }
//     }

//     val cur_datin_disable = RegInit(false.B)
//     when(pdp_cube_end & (flush_num.orR)){
//         cur_datin_disable := true.B
//     }
//     .elsewhen(non_splitw){
//         when(line_last_stripe_done & need_bubble){
//             cur_datin_disable := true.B
//         }
//         .elsewhen(bubble_en_end){
//             cur_datin_disable := false.B
//         }
//     }
//     .otherwise{
//         when(last_splitw_en & line_last_stripe_done & need_bubble){
//             cur_datin_disable := true.B
//         }
//         .otherwise{
//             cur_datin_disable := false.B
//         }
//     }

//     val last_c = Wire(Bool())
//     val channel_cnt = RegInit("b0".asUInt(5.W))
//     when(cur_datin_disable){
//         when(last_c){
//             channel_cnt := 0.U
//         }
//         .elsewhen(pdp_datin_prdy_1){
//             channel_cnt := channel_cnt +& 1.U
//         }
//     }
//     .otherwise{
//         channel_cnt := 0.U
//     }
//     last_c := (channel_cnt === ENUM.U) & pdp_datin_prdy_1

//     val bubble_cnt = RegInit("b0".asUInt(3.W)) 
//     when(cur_datin_disable){
//         when(bubble_en_end){
//             bubble_cnt := 0.U
//         }
//         .elsewhen(last_c){
//             bubble_cnt := bubble_cnt +& 1.U
//         }
//     }
//     .otherwise{
//         bubble_cnt := 0.U
//     }

//     val bubble_num_dec = bubble_num - 1.U
//     val bubble_en_end = (bubble_cnt == bubble_num_dec) & last_c

//     //////////////////////////////////////////////////////
//     //last line element output en during cur line element comming
//     //----------------------------------------------------
//     //subcube end flag for last_out_en control in the sub-cube end
//     val subcube_end_flag = RegInit(false.B)
//     when(splitw_end_sync & bsync){
//         subcube_end_flag := true.B
//     }
//     .elsewhen(load_din){
//         subcube_end_flag := false.B
//     }

//     val last_out_en = RegInit(false.B)
//     val last_out_done = Wire(Bool())
//     when(last_out_done){
//         last_out_en := false.B
//     }
//     .elsewhen(pdp_cube_end){
//         last_out_en := false.B
//     }
//     .elsewhen((first_out_num =/= 1.U) & (~subcube_end_flag)){
//         when(need_bubble){
//             when(bubble_en_end){
//                 last_out_en := true.B
//             }   
//         }
//         .elsewhen(flush_num.orR){
//             when(non_splitw & line_last_stripe_done){
//                 last_out_en := true.B
//             }
//             .elsewhen(~non_splitw & last_splitw_en & line_last_stripe_done){
//                 last_out_en := true.B
//             }
//         }
//     }
//     .otherwise{
//         last_out_en := false.B
//     }

//     val first_out_num_dec2 = Wire(UInt(3.W))
//     first_out_num_dec2 := Mux(need_bubble, first_out_num -& 2.U, flush_num -& 1.U)

//     val last_out_cnt = RegInit("b0".asUInt(3.W))
//     when(last_out_en){
//         when(strip_recieve_done){
//             when(last_out_done){
//                 last_out_cnt := 0.U
//             }
//             .otherwise{
//                 last_out_cnt := last_out_cnt + 1.U
//             }
//         }
//     }
//     .otherwise{
//         last_out_cnt := 0.U
//     }

//     last_out_done := (last_out_cnt === first_out_num_dec2) & strip_recieve_done & last_out_en
//     pooling_1d_rdy := (strip_xcnt_psize === io.pooling_size_h_cfg) &  strip_recieve_done

//     /////////////////////////////////////////////////////////

//     //strip count in total width
//     val strip_cnt_total = RegInit("b0".asUInt(13.W))
//     when(init_cnt){
//         strip_cnt_total := 0.U
//     }
//     .elsewhen(strip_recieve_done){
//         strip_cnt_total := strip_cnt_total +& 1.U
//     }

//     val strip_width_end  = (strip_cnt_total === pooling_pwidth)
//     line_last_stripe_done := (strip_width_end & strip_recieve_done)

//     //-----------------------
//     //flag the last one pooling in width direction
//     //-----------------------
//     val rest_width = pooling_pwidth - strip_cnt_total
//     val rest_width_use = Mux(non_splitw | splitw_end, rest_width +& io.reg2dp_pad_right_cfg, rest_width)
//     val last_pooling_flag = rest_width_use <= io.pooling_size_h_cfg

//     //======================================================================
//     //pooling 1D unit counter
//     //
//     //----------------------------------------------------------------------
//     val unit1d_cnt_stride = RegInit(0.U(3.W))
//     when(init_cnt){
//         unit1d_cnt_stride := padding_stride_num
//     }
//     .elsewhen(stride_end){
//         when(unit1d_cnt_stride === regs_num){
//             unit1d_cnt_stride := 0.U
//         }
//         .otherwise{
//             unit1d_cnt_stride := unit1d_cnt_stride + 1.U
//         }
//     }

//     val line_ldata_valid = Wire(Bool())
//     val unit1d_cnt_pooling = RegInit(0.U(3.W))
//     when(init_cnt){
//         unit1d_cnt_pooling := 0.U
//     }
//     .elsewhen(pooling_1d_rdy | line_ldata_valid){
//         when(unit1d_cnt_pooling === regs_num){
//             unit1d_cnt_pooling := 0.U
//         }
//         .otherwise{
//             unit1d_cnt_pooling := unit1d_cnt_pooling + 1.U
//         }
//     }
//     line_ldata_valid := line_last_stripe_done

//     val init_unit1d_set = Wire(Vec(8, Bool()))
//     val unit1d_set_trig = Wire(Vec(8, Bool()))
//     val unit1d_en = RegInit(VecInit(Seq.fill(8)(false.B)))




    


// }}


// object NV_NVDLA_PDP_CORE_cal1dDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_cal1d())
// }