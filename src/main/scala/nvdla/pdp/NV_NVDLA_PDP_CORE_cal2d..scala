// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_CORE_cal2dIO(implicit conf: nvdlaConfig) extends Bundle{
//     //clk
//     val nvdla_core_clk = Input(Clock())
//     val pwrbus_ram_pd = Input(UInt(32.W))

//     //pdp_dp2wdma
//     val pdp_dp2wdma_pd = DecoupledIO(UInt((conf.PDPBW+14).W))
//     //pooling
//     val pooling1d_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)).W)))

//     //config 
//     val padding_v_cfg = Input(UInt(3.W))
//     val pdp_op_start = Intput(Bool())
//     val pooling_channel_cfg = Input(UInt(13.W))
//     val pooling_out_fwidth_cfg = Input(UInt(10.W))
//     val pooling_out_lwidth_cfg = Input(UInt(10.W))
//     val pooling_out_mwidth_cfg = Input(UInt(10.W))
//     val pooling_size_v_cfg = Input(UInt(3.W))
//     val pooling_splitw_num_cfg = Input(UInt(8.W))
//     val pooling_stride_v_cfg = Input(UInt(4.W))
//     val pooling_type_cfg = Input(UInt(2.W))
//     val reg2dp_cube_in_height = Input(UInt(13.W))
//     val reg2dp_cube_out_width = Input(UInt(13.W))
//     val reg2dp_kernel_height = Input(UInt(3.W))
//     val reg2dp_kernel_width = Input(UInt(3.W))
//     val reg2dp_pad_bottom_cfg = Input(UInt(3.W))
//     val reg2dp_pad_top = Input(UInt(3.W))
//     val reg2dp_pad_value_1x_cfg = Input(UInt(19.W))
//     val reg2dp_pad_value_2x_cfg = Input(UInt(19.W))
//     val reg2dp_pad_value_3x_cfg = Input(UInt(19.W))
//     val reg2dp_pad_value_4x_cfg = Input(UInt(19.W))
//     val reg2dp_pad_value_5x_cfg = Input(UInt(19.W))
//     val reg2dp_pad_value_6x_cfg = Input(UInt(19.W))
//     val reg2dp_pad_value_7x_cfg = Input(UInt(19.W))
//     val reg2dp_partial_width_out_first = Input(UInt(10.W))
//     val reg2dp_partial_width_out_last = Input(UInt(10.W))
//     val reg2dp_partial_width_out_mid = Input(UInt(10.W))
//     val reg2dp_recip_height_cfg = Input(UInt(17.W))
//     val reg2dp_recip_width_cfg = Input(UInt(17.W))
// }

// class NV_NVDLA_PDP_CORE_cal2d(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new NV_NVDLA_PDP_CORE_cal2dIO)

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
//     //bank depth follows rule of 16 elements in width in worst case
//     //it's 64 in t194
//     //--------------------------------------------------------------
//     val bank_depth = (conf.BATCH_PDP_NUM*16-1).U

//     //==============================================================
//     // buffer the input data from pooling 1D unit
//     // calculate the data postion in input-data-cube
//     //
//     //--------------------------------------------------------------
//     val cur_datin_disable = RegInit(false.B)
//     val one_width_disable = RegInit(false.B)

//     val pooling1d_norm_rdy = Wire(Bool())
//     val one_width_norm_rdy := pooling1d_norm_rdy & (~one_width_disable)
//     io.pooling1d_pd.ready := one_width_norm_rdy & (~cur_datin_disable)

//     //////////////////////////////////////////////////////////////////////////////////////
//     val data_c_end = Wire(Bool())
//     val load_din = io.pooling1d_pd.valid & io.pooling1d_pd.ready
//     val stripe_receive_done = load_din & data_c_end
//     val average_pooling_en = io.pooling_type_cfg === 0.U

//     //////////////////////////////////////////////////////////////////////////////////////
//     val c_cnt = RegInit(0.U(5.W))
//     when(load_din){
//         when(data_c_end){
//             c_cnt := 0.U
//         }
//         .otherwise{
//             c_cnt := c_cnt + 1.U
//         }
//     }
//     data_c_end := c_cnt === (conf.BATCH_PDP_NUM-1).U
//     //end of line
//     val wr_line_dat_done = Wire(Bool())
//     val pout_width_cur = Wire(UInt(13.W))
//     val wr_line_dat_cnt = RegInit(0.U(13.W))
//     when(wr_line_dat_done){
//         wr_line_dat_cnt := 0.U
//     }
//     .elsewhen(stripe_receive_done){
//         wr_line_dat_cnt := wr_line_dat_cnt + 1.U
//     }

//     wr_line_dat_done := (wr_line_dat_cnt === pout_width_cur) & stripe_receive_done;
//     //end of surface
//     val wr_surface_dat_done = Wire(Bool())
//     val wr_surface_dat_cnt = RegInit(0.U(13.W))
//     when(wr_surface_dat_done){
//         wr_surface_dat_cnt := 0.U
//     }
//     .elsewhen(wr_line_dat_done){
//         wr_surface_dat_cnt := wr_surface_dat_cnt + 1.U
//     }

//     val last_line_in = wr_surface_dat_cnt === io.reg2dp_cube_in_height
//     wr_surface_dat_done := wr_line_dat_done & last_line_in

//     val surface_num = io.pooling_channel_cfg(12, conf.ATMMBW)

//     val surface_cnt_rd = RegInit("b0".asUInt((13-conf.ATMMBW).W));
//     val wr_subcube_dat_done = Wire(Bool())
//     when(wr_subcube_dat_done){
//         surface_cnt_rd := 0.U
//     }
//     .elsewhen(wr_surface_dat_done){
//         surface_cnt_rd := surface_cnt_rd + 1.U
//     }

//     wr_subcube_dat_done := (surface_num === surface_cnt_rd) & wr_surface_dat_done

//     //total cube done
//     val wr_splitc_cnt = RegInit("b0".asUInt(8.W))
//     val wr_total_cube_done = Wire(Bool())
//     when(wr_total_cube_done){
//         wr_splitc_cnt := 0.U
//     }
//     .elsewhen(wr_subcube_dat_done){
//         wr_splitc_cnt := wr_splitc_cnt + 1.U
//     }

//     wr_total_cube_done := (wr_splitc_cnt === io.pooling_splitw_num_cfg) & wr_subcube_dat_done;

//     //////////////////////////////////////////////////////////////////////////////////////
//     //split width selection 
//     val splitw_enable = (io.pooling_splitw_num_cfg =/= 0.U) 
//     val last_splitw = (wr_splitc_cnt === io.pooling_splitw_num_cfg) & splitw_enable
//     val first_splitw = (wr_splitc_cnt === 0.U) & splitw_enable
    
//     pout_width_cur := Mux(~splitw_enable, io.reg2dp_cube_out_width, 
//                       Mux(last_splitw, io.pooling_out_lwidth_cfg,
//                       Mux(first_splitw, io.pooling_out_fwidth_cfg, 
//                       io.pooling_out_mwidth_cfg)))

//     //=============================================================
//     // physical memory bank 8
//     // 8 memory banks are used to load maximum 8 pooling output lines
//     //
//     //-------------------------------------------------------------
//     val pooling_size_v = Wire(UInt(4.W))
//     //maximum pooling output lines  need to be  buffer
//     //stride 1
//     val buffer_lines_0 = pooling_size_v  
//     //stride 2
//     val buffer_lines_1 = pooling_size_v(3, 1) +& pooling_size_v(0)
//     //stride 3
//     val buffer_lines_2 = Mux(5.U >= io.pooling_size_v_cfg, 2.U, 3.U)
//     //stride 4 5 6 7
//     val buffer_lines_3 = 2.U

//     val pooling_stride_big =  (io.pooling_stride_v_cfg >= io.pooling_size_v_cfg)

//     val buffer_lines_num = RegInit("b0".asUInt(4.W))

//     when(io.pdp_op_start){
//         when(pooling_stride_big){
//             buffer_lines_num := 1.U
//         }
//         .otherwise{
//             buffer_lines_num := MuxLookup(io.pooling_stride_v_cfg, buffer_lines_3,
//                                     Array(0.U -> buffer_lines_0,
//                                           1.U -> buffer_lines_1,
//                                           2.U -> buffer_lines_2))
//         }
//     }

//     //memory bank merge num

//     val bank_merge_num = MuxLookup(buffer_lines_num, 1.U,
//                              Array(1.U -> 8.U,
//                                    2.U -> 4.U,
//                                    3.U -> 2.U,
//                                    4.U -> 3.U))
//     //==========================================================
//     //bank active enable signal 
//     //
//     //----------------------------------------------------------
//     //stride intial data

//     //stride ==1
//     val padding_stride1_num = io.padding_v_cfg
//     //stride ==2
//     val padding_stride2_num = io.padding_v_cfg(2, 1)
//     //stride ==3
//     val padding_stride3_num = Mux(io.padding_v_cfg >= 6.U, 2.U,
//                               Mux(io.padding_v_cfg >= 3.U, 1.U,
//                               0.U))
//     //stride==4 5 6 7
//     val padding_stride4_num = Mux(io.padding_v_cfg > io.pooling_stride_v_cfg, 1.U, 0.U)
//     val pooling_stride_v = io.pooling_stride_v_cfg +& 1.U
//     //real num-1
//     val padding_stride_num = MuxLookup(io.pooling_stride_v_cfg, padding_stride4_num,
//                              Array(0.U -> padding_stride1_num,
//                                    1.U -> padding_stride2_num,
//                                    2.U -> padding_stride3_num))

//     val strip_ycnt_offset = (io.padding_v_cfg -& padding_stride_num * pooling_stride_v)(2, 0)

//     /////////////////////////////////////////////////////////////////////////////////
//     val strip_ycnt_stride = RegInit("b0".asUInt(4.W))
//     val strip_ycnt_stride_f = Wire(UInt(4.W))

//     val middle_surface_trig = wr_surface_dat_done & (~wr_total_cube_done);
//     val stride_end = wr_line_dat_done & (strip_ycnt_stride === io.pooling_stride_v_cfg);
//     val init_cnt = middle_surface_trig | io.pdp_op_start;  

//     //pooling stride in vertical direction
//     when(init_cnt){
//         strip_ycnt_stride_f := strip_ycnt_offset
//     }
//     .elsewhen(stride_end){
//         strip_ycnt_stride_f := 0.U
//     }
//     .elsewhen(wr_line_dat_done){
//         strip_ycnt_stride_f := strip_ycnt_stride + 1.U
//     }
//     .otherwise{
//         strip_ycnt_stride_f := strip_ycnt_stride
//     }

//     when(init_cnt | stride_end | wr_line_dat_done){
//         strip_ycnt_stride := strip_ycnt_stride_f
//     }

//     //2D pooling result ready
//     val strip_ycnt_psize = RegInit("b0".asUInt(3.W))
//     val pooling_2d_rdy = Wire(Bool())

//     io.pooling_size_minus_sride := io.pooling_size_v_cfg - io.pooling_stride_v_cfg

//     when(init_cnt){
//         strip_ycnt_psize := io.padding_v_cfg
//     }
//     .elsewhen(io.pooling_size_v_cfg >= io.pooling_stride_v_cfg){
//         when(pooling_2d_rdy){
//             strip_ycnt_psize := io.pooling_size_minus_sride
//         }
//         .elsewhen(wr_line_dat_done){
//             strip_ycnt_psize := strip_ycnt_psize + 1.U
//         }
//     }
//     .otherwise{
//         when(strip_ycnt_stride_f <= io.pooling_size_v_cfg){
//             strip_ycnt_psize := strip_ycnt_stride_f
//         }
//         .otherwise{
//             strip_ycnt_psize := 0.U
//         }
//     }

//     //=====================================================================
//     pooling_size_v := io.pooling_size_v_cfg +& 1.U

//     val pooling_size = pooling_size_v
//     val stride = pooling_stride_v
//     val pad_l = io.padding_v_cfg
//     val pad_r = io.reg2dp_pad_bottom_cfg

//     //line num need flush at surface end
//     val stride_1x = stride
//     val stride_2x = Cat(stride, 0.U)
//     val stride_3x = stride_2x +& stride
//     val stride_4x = Cat(stride, "b0".asUInt(2.W))
//     val stride_5x = stride_4x +& stride
//     val stride_6x = stride_3x +& stride_3x
//     val stride_7x = stride_4x +& stride_3x
//     val flush_num_cal = Wire(UInt(3.W))

//     when(pad_r < stride_1x ){
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
//     val small_active = (~(io.reg2dp_cube_in_height(12, 3).orR)) & ((io.reg2dp_cube_in_height(2, 0) +& io.reg2dp_pad_top(2, 0)) < io.reg2dp_kernel_height(2, 0))
//     //non-split mode cube_width + pad_left + pad_right
//     val h_pt = io.reg2dp_cube_in_height(2, 0) +& io.reg2dp_pad_top
//     val h_pt_pb = h_pt +& pad_r

//     //pad_right remain afrer 1st kernel pooling
//     val pad_r_remain = Wire(UInt(6.W))
//     when(small_active){
//         pad_r_remain := h_pt_pb -& io.reg2dp_kernel_height(2, 0)
//     }
//     .otherwise{
//         pad_r_remain := 0.U
//     }

//     //how many need bubble after 1st kernel pooling
//     val samllH_flush_num = Wire(UInt(3.W))
//     when(pad_r_remain === stride_6x){
//         samllH_flush_num := 6.U
//     }
//     .elsewhen(pad_r_remain === stride_5x){
//         samllH_flush_num := 5.U
//     }
//     .elsewhen(pad_r_remain === stride_4x){
//         samllH_flush_num := 4.U
//     }
//     .elsewhen(pad_r_remain === stride_3x){
//         samllH_flush_num := 3.U
//     }
//     .elsewhen(pad_r_remain === stride_2x){
//         samllH_flush_num := 2.U
//     }
//     .elsewhen(pad_r_remain === stride_1x){
//         samllH_flush_num := 1.U
//     }
//     .otherwise{
//         samllH_flush_num := 0.U
//     }

//     //flush num calc
//     val flush_num = Wire(UInt(3.W))
//     when(flush_num_cal === 0.U){
//         flush_num := 0.U
//     }
//     .elsewhen(small_active){
//         flush_num := samllH_flush_num
//     }
//     .otherwise{
//         flush_num := flush_num_cal
//     }

//     val need_flush = (flush_num =/= 0.U)

//     //the 1st element/line num need output data
//     val cube_in_height_cfg = io.reg2dp_cube_in_height +& 1.U
//     val first_out_num = Mux(small_active, cube_in_height_cfg, pooling_size - pad_l)

//     val need_bubble = RegInit(false.B)
//     val bubble_num_use = RegInit("b0".asUInt(3.W))
//     val bubble_add = Wire(UInt(3.W))
//     when(wr_subcube_dat_done){
//        when(need_flush){
//            need_bubble := true.B
//            bubble_num_use := flush_num
//        }
//        .otherwise{
//            need_bubble := false.B
//            bubble_num_use := 0.U
//        }
//     }
//     .elsewhen(last_line_in){
//         when(flush_num >= first_out_num){
//             need_bubble := true.B
//             bubble_num_use := flush_num -& first_out_num +& 1.U +& bubble_add
//         }
//         .elsewhen(bubble_add.orR){
//             need_bubble := true.B
//             bubble_num_use := bubble_add
//         }
//         .otherwise{
//             need_bubble := false.B
//             bubble_num_use := 0.U
//         }
//     }

//     ///////////////////////////////////////////////////////////////////////
//     //bubble control when next surface comming .  Beginning
//     ///////////////////////////////////////////////////////////////////////
//     val up_pnum = Wire(Vec(6, Bool()))
//     val u_bcontrol_begin = Module(new NV_NVDLA_PDP_CORE_CAL2D_bubble_control_begin)
//     u_bcontrol_begin.io.nvdla_core_clk := io.nvdla_core_clk
//     u_bcontrol_begin.io.pdp_op_start := io.pdp_op_start
//     u_bcontrol_begin.io.flush_num := flush_num
//     u_bcontrol_begin.io.first_out_num := first_out_num 
//     u_bcontrol_begin.io.up_pnum := up_pnum
//     bubble_add := u_bcontrol_begin.io.bubble_add
//     val flush_in_next_surf = u_bcontrol_begin.io.flush_in_next_surf

//     //pooling No. in flush time
//     val u_pnum_flush = Module(new NV_NVDLA_PDP_CORE_CAL2D_pnum_flush)
//     val unit2d_cnt_pooling = RegInit("b0".asUInt(3.W))
//     val unit2d_cnt_pooling_max = Wire(UInt(3.W))
//     u_pnum_flush.io.nvdla_core_clk := io.nvdla_core_clk
//     u_pnum_flush.io.unit2d_cnt_pooling := unit2d_cnt_pooling
//     u_pnum_flush.io.unit2d_cnt_pooling_max := unit2d_cnt_pooling_max
//     u_pnum_flush.io.last_line_in = last_line_in
//     val pnum_flush = u_pnum_flush.io.pnum_flush

//     //-------------------------
//     //update pooling No. in line2 of next surface
//     //-------------------------
//     val u_pnum_updt = Module(new NV_NVDLA_PDP_CORE_CAL2D_pnum_updt)
//     u_pnum_updt.io.nvdla_core_clk := io.nvdla_core_clk
//     u_pnum_updt.io.padding_v_cfg = io.padding_v_cfg
//     u_pnum_updt.io.stride = stride
//     up_pnum := u_pnum_updt.io.up_pnum

//     ///////////////////////////////////////////////////////////////////////
//     //bubble control when next surface comming .  Ending
//     ///////////////////////////////////////////////////////////////////////
//     val is_one_width_in = Wire(Bool())
//     val one_width_bubble_end = Wire(Bool())
//     val subend_need_flush_flg = RegInit(false.B)
//     when(wr_subcube_dat_done & need_flush & is_one_width_in){
//         subend_need_flush_flg := true.B
//     }
//     .elsewhen(one_width_bubble_end){
//         subend_need_flush_flg := false.B
//     }

//     val surfend_need_bubble_flg = RegInit(false.B)
//     when(wr_surface_dat_done & need_bubble & is_one_width_in){
//         surfend_need_bubble_flg := true.B
//     }
//     .elsewhen(one_width_bubble_end){
//         surfend_need_bubble_flg := false.B
//     }

//     /////////////////////////////////////////
//     val cur_datin_disable = RegInit(false.B)
//     val one_width_bubble_end = Wire(Bool())
//     val bubble_en_end = Wire(Bool())
//     when((wr_subcube_dat_done & need_flush & (~is_one_width_in)) | (subend_need_flush_flg & one_width_bubble_end)){
//         cur_datin_disable := true.B
//     }
//     .elsewhen((wr_subcube_dat_done & need_bubble & (~is_one_width_in)) | (subend_need_flush_flg & one_width_bubble_end)){
//         cur_datin_disable := true.B
//     }
//     .elsewhen(bubble_en_end){
//         cur_datin_disable := false.B
//     }

//     ///////////////////////////////////////////
//     val pout_width_cur_latch = RegInit("b0".asUInt(13.W))
//     when((wr_subcube_dat_done & need_flush) || (wr_surface_dat_done & need_bubble)){
//         pout_width_cur_latch := pout_width_cur
//     }

//     val channel_cnt = RegInit("b0".asUInt(5.W))
//     val last_c = Wire(Bool())
//     when(cur_datin_disable){
//         when(last_c){
//             channel_cnt := 0.U
//         }
//         .elsewhen(one_width_norm_rdy){
//             channel_cnt := channel_cnt + 1.U
//         }
//     }
//     .otherwise{
//         channel_cnt := 0.U
//     }

//     last_c := (channel_cnt === (conf.BATCH_PDP_NUM - 1).U) & one_width_norm_rdy

//     val line_cnt = RegInit("b0".asUInt(13.W))
//     val line_end = Wire(Bool())
//     when(cur_datin_disable){
//         when(line_end){
//             line_cnt := 0.U
//         }
//         .elsewhen(last_c){
//             line_cnt := line_cnt + 1.U
//         }
//     }
//     .otherwise{
//         line_cnt := 0.U
//     }

//     line_end := (line_cnt === pout_width_cur_latch) & last_c

//     val bubble_cnt = RegInit("b0".asUInt(3.W))
//     when(cur_datin_disable){
//         when(bubble_en_end){
//             bubble_cnt := 0.U
//         }
//         .elsewhen(line_end){
//             bubble_cnt := bubble_cnt + 1.U
//         }
//     }
//     .otherwise{
//         bubble_cnt := 0.U
//     }

//     bubble_en_end := (bubble_cnt === (bubble_num_use -& 1.U)) & line_end;

//     //////////////////////////////////////////////////////
//     //last lines output en during new lines comming
//     //----------------------------------------------------
//     //cube end flag for last_out_en control in the cube end
//     val cube_end_flag = RegInit(false.B)
//     when(wr_subcube_dat_done){
//         cube_end_flag := true.B
//     }
//     .elsewhen(load_din){
//         cube_end_flag := false.B
//     }

//     val last_out_en = RegInit(false.B)
//     val last_out_done = Wire(Bool())
//     when(first_out_num =/= 1.U){
//         when((need_bubble & bubble_en_end & (~cube_end_flag) & (bubble_add < flush_in_next_surf)) | (~need_bubble & need_flush & wr_surface_dat_done & (~wr_subcube_dat_done))){
//             last_out_en := true.B
//         }
//         .elsewhen(last_out_done){
//             last_out_en := false.B
//         }
//     }
//     .otherwise{
//         last_out_en := false.B
//     }

//     val first_out_num_dec2 = flush_num - bubble_num_use - 1.U   //first_out_num - 2'd2;
//     val last_out_cnt = RegInit("b0".asUInt(3.W))
//     val flush_num_dec1 = Wire(UInt(3.W))
//     when(last_out_en){
//         when(wr_line_dat_done){
//             when(((last_out_cnt == first_out_num_dec2) & need_bubble) | (~need_bubble & (last_out_cnt === flush_num_dec1))){
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
//     flush_num_dec1 := flush_num - 1.U

//     last_out_done := (((last_out_cnt === first_out_num_dec2) & need_bubble) | (~need_bubble & (last_out_cnt === flush_num_dec1))) & wr_line_dat_done & last_out_en;

//     ///////////////////////////////////////////////////////////////////////
//     //bubble control when input width is only 1 element in width
//     ///////////////////////////////////////////////////////////////////////
//     val is_one_width_in = Wire(Bool())

//     when(~splitw_enable){
//         is_one_width_in := io.reg2dp_cube_out_width === 0.U
//     }
//     .elsewhen(first_splitw){
//         is_one_width_in := io.reg2dp_partial_width_out_first === 0.U
//     }
//     .elsewhen(last_splitw){
//         is_one_width_in := io.reg2dp_partial_width_out_first === 0.U
//     }
//     .otherwise{
//         is_one_width_in := Mux(io.pooling_splitw_num_cfg > 1.U, io.reg2dp_partial_width_out_mid, false.B)
//     }

//     /////////////
//     val one_width_disable = RegInit(false.B)
//     when(wr_line_dat_done & is_one_width_in){
//         one_width_disable := true.B
//     }
//     .elsewhen(one_width_bubble_end){
//         one_width_disable := false.B
//     }

//     /////////////
//     val one_width_bubble_cnt = RegInit("b0".asUInt(3.W))
//     when(one_width_disable){
//        when(one_width_bubble_end){
//            one_width_bubble_cnt := 0.U
//        }
//        .elsewhen(pooling1d_norm_rdy){
//            one_width_bubble_cnt := one_width_bubble_cnt + 1.U
//        }
//     }
//     .otherwise{
//         one_width_bubble_cnt := 0.U
//     }

//     one_width_bubble_end := (one_width_bubble_cnt === (4-2).U) & pooling1d_norm_rdy

//     //////////////////////////////////////////////////////

//     pooling_2d_rdy = wr_line_dat_done & (strip_ycnt_psize === io.pooling_size_v_cfg)

//     //=====================================================================
//     //pooling 2D unit counter
//     //
//     //---------------------------------------------------------------------
//     val unit2d_cnt_stride = RegInit("b0".asUInt(3.W))
//     val stride_trig_end = Wire(Bool())
//     when(init_cnt){
//         unit2d_cnt_stride := padding_stride_num
//     }
//     .elsewhen(stride_end){
//         when(stride_trig_end){
//             unit2d_cnt_stride := 0.U
//         }
//         .otherwise{
//             unit2d_cnt_stride := unit2d_cnt_stride + 1.U
//         }
//     }

//     stride_trig_end := (unit2d_cnt_pooling_max === unit2d_cnt_stride)

//     val unit2d_cnt_pooling = RegInit("b0".asUInt(3.W))
//     val unit2d_cnt_pooling_end = Wire(Bool())
//     when(init_cnt){
//         unit2d_cnt_pooling := 0.U
//     }
//     .elsewhen(pooling_2d_rdy | wr_surface_dat_done){
//         when(unit2d_cnt_pooling_end){
//             unit2d_cnt_pooling := 0.U
//         }
//         .otherwise{
//             unit2d_cnt_pooling := unit2d_cnt_pooling + 1.U
//         }
//     }

//     unit2d_cnt_pooling_end := (unit2d_cnt_pooling === unit2d_cnt_pooling_max)

//     unit2d_cnt_pooling_max := (buffer_lines_num - 1.U)(2, 0)

//     //-------------------------
//     //flag the last one pooling in height direction
//     //-------------------------
//     val rest_height = reg2dp_cube_in_height - wr_surface_dat_cnt
//     val rest_height_use = rest_height +& reg2dp_pad_bottom_cfg
//     val last_pooling_flag = rest_height_use <= io.pooling_size_v_cfg
//     //unit2d pooling enable  
//     val init_unit2d_set = VecInit((0 to 7) 
//                     map {i => init_cnt & (padding_stride_num >= i.U)}) 
//     val unit2d_set_trig = Wire(Vec(8, Bool()))
//     unit2d_set_trig(0) := stride_end & stride_trig_end & (~last_pooling_flag)
//     for(i <- 1 to 7){
//         unit2d_set_trig(i) := stride_end & (unit2d_cnt_stride === (i-1).U) & (~stride_trig_end) & (~last_pooling_flag)
//     }
//     val unit2d_set = VecInit((0 to 7) 
//                     map {i => unit2d_set_trig(i)|init_unit2d_set(i)}) 
//     val unit2d_clr = VecInit((0 to 7) 
//                     map {i => (pooling_2d_rdy & (unit2d_cnt_pooling === i.U)) | wr_surface_dat_done}) 
//     val unit2d_en = RegInit(VecInit(Seq.fill(8)(false.B)))
//     for(i <- 0 to 7){
//         when(wr_total_cube_done){
//             unit2d_en(i) := false.B
//         }
//         .elsewhen(unit2d_set(i)){
//             unit2d_en(i) := true.B
//         }
//         .elsewhen(unit2d_clr(i)){
//             unit2d_en(i) := false.B
//         }
//     }
//     ///////////////////////////////////////////////////////////////////////
//     ///////////////////////////////////////////////////////////////////////
//     ///////////////////////////////////////////////////////////////////////
//     val datin_buf = RegInit("b0".asUInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)).W))
//     val wr_line_end_buf = RegInit(false.B)
//     val wr_surface_dat_done_buf = RegInit(false.B)

//     when(load_din){
//         datin_buf := io.pooling1d_pd.bits
//         wr_line_end_buf := wr_line_dat_done
//         wr_surface_dat_done_buf := wr_surface_dat_done
//     }

//     //////////////////////////////////////////////////////////////////////
//     //calculate the real pooling size within one pooling 
//     //PerBeg
//     val mem_re_sel = Wire(Vec(4, Bool()))
//     val u_calculate_real_pooling_size = Module(new NV_NVDLA_PDP_CORE_CAL2D_calculate_real_pooling_size))
//     u_calculate_real_pooling_size.io.nvdla_core_clk := io.nvdla_core_clk
//     u_calculate_real_pooling_size.io.wr_line_dat_done := wr_line_dat_done
//     u_calculate_real_pooling_size.io.mem_re_sel := mem_re_sel
//     u_calculate_real_pooling_size.io.unit2d_set := unit2d_set
//     u_calculate_real_pooling_size.io.unit2d_en := unit2d_en
//     val unit2d_vsize_cnt_d = u_calculate_real_pooling_size.io.unit2d_vsize_cnt_d

//     //============================================================
//     val active_last_line = (strip_ycnt_psize === io.pooling_size_v_cfg) | last_line_in
//     //============================================================
//     //memory bank read/write controller
//     //
//     //------------------------------------------------------------
//     //memory read
//     //mem bank0 enable
//     //
//     val wr_sub_lbuf_cnt = RegInit("b0".asUInt(3.W))
//     val u_mem_rd = Module(new NV_NVDLA_PDP_CORE_CAL2D_mem_rd)
//     u_mem_rd.io.nvdla_core_clk = Input(Clock())
//     u_mem_rd.io.load_din := load_din
//     u_mem_rd.io.wr_line_dat_done := wr_line_dat_done
//     u_mem_rd.io.unit2d_en := unit2d_en
//     u_mem_rd.io.unit2d_set := unit2d_set
//     u_mem_rd.io.buffer_lines_num := buffer_lines_num
//     u_mem_rd.io.wr_sub_lbuf_cnt := wr_sub_lbuf_cnt
//     val mem_re_vec = u_mem_rd.io.mem_re
//     val mem_re_1st_vec = u_mem_rd.io.mem_re_1st
//     mem_re_sel := u_mem_rd.io.mem_re_sel

//     ///////////////////////////
//     //shouldn't read data from mem for the first pooling line
//     ///////////////////////////
//     val sub_lbuf_dout_cnt = RegInit("b0".asUInt(9.W))
//     val mem_re = mem_re_vec(0) | mem_re_vec(1) | mem_re_vec(2) | mem_re_vec(3)
//     val mem_re_1st = mem_re_1st_vec(0) | mem_re_1st_vec(1) | mem_re_1st_vec(2) | mem_re_1st_vec(3)
//     val mem_raddr = sub_lbuf_dout_cnt

//     //line buffer counter
//     val sub_lbuf_dout_done = Wire(Bool())
//     val last_sub_lbuf_done = Wire(Bool())
//     when(wr_line_dat_done | last_sub_lbuf_done | line_end){
//         wr_sub_lbuf_cnt := 0.U
//     }
//     .elsewhen(sub_lbuf_dout_done){
//         wr_sub_lbuf_cnt := wr_sub_lbuf_cnt + 1.U
//     }
//     last_sub_lbuf_done := ((bank_merge_num -& 1.U) === wr_sub_lbuf_cnt) & sub_lbuf_dout_done
//     //--------------------------------------------------------------------
//     when(sub_lbuf_dout_done | wr_line_dat_done | line_end){
//         sub_lbuf_dout_cnt := 0.U
//     }
//     .elsewhen(load_din | (cur_datin_disable & one_width_norm_rdy)){
//         sub_lbuf_dout_cnt := sub_lbuf_dout_cnt + 1.U
//     }

//     sub_lbuf_dout_done := (sub_lbuf_dout_cnt === bank_depth) & (load_din | (cur_datin_disable & one_width_norm_rdy))
//     //==============================================================================================
//     //buffer the data from memory  and from UNIT1D
//     //
//     //----------------------------------------------------------------------------------------------
// val pool_fun_vld = load_din;
// val int_pool_datin_ext = Mux(pool_fun_vld, datain_ext, 0.U)
// val int_pool_cur_dat = Mux(pool_fun_vld, cur_pooling_dat, 0.U)
// int_pooling := VecInit((0 to conf.NVDLA_PDP_THROUGHPUT - 1) map 
// { i => 
// Mux(io.pooling_type_cfg===2.U, pooling_SUM(int_pool_cur_dat(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i), int_pool_datin_ext(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i)),
// Mux(io.pooling_type_cfg===1.U, pooling_MIN(int_pool_cur_dat(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i), int_pool_datin_ext(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i)),
// Mux(io.pooling_type_cfg===0.U, pooling_MAX(int_pool_cur_dat(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i), int_pool_datin_ext(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i)), 
// 0.U)))}).asUInt






// }}


// object NV_NVDLA_PDP_CORE_cal2dDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_cal2d())
// }