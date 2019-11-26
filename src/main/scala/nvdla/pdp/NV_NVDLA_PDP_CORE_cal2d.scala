package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_PDP_CORE_cal2dIO(implicit conf: nvdlaConfig) extends Bundle{
    //clk
    val nvdla_core_clk = Input(Clock())
    val pwrbus_ram_pd = Input(UInt(32.W))

    //pdp_dp2wdma
    val pdp_dp2wdma_pd = DecoupledIO(UInt((conf.PDPBW+14).W))
    //pooling
    val pooling1d_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)).W)))

    //config 
    val padding_v_cfg = Input(UInt(3.W))
    val pdp_op_start = Input(Bool())
    val pooling_channel_cfg = Input(UInt(13.W))
    val pooling_out_fwidth_cfg = Input(UInt(10.W))
    val pooling_out_lwidth_cfg = Input(UInt(10.W))
    val pooling_out_mwidth_cfg = Input(UInt(10.W))
    val pooling_size_v_cfg = Input(UInt(3.W))
    val pooling_splitw_num_cfg = Input(UInt(8.W))
    val pooling_stride_v_cfg = Input(UInt(4.W))
    val pooling_type_cfg = Input(UInt(2.W))
    val reg2dp_cube_in_height = Input(UInt(13.W))
    val reg2dp_cube_out_width = Input(UInt(13.W))
    val reg2dp_kernel_height = Input(UInt(3.W))
    val reg2dp_kernel_width = Input(UInt(3.W))
    val reg2dp_pad_bottom_cfg = Input(UInt(3.W))
    val reg2dp_pad_top = Input(UInt(3.W))
    val reg2dp_pad_value_1x_cfg = Input(UInt(19.W))
    val reg2dp_pad_value_2x_cfg = Input(UInt(19.W))
    val reg2dp_pad_value_3x_cfg = Input(UInt(19.W))
    val reg2dp_pad_value_4x_cfg = Input(UInt(19.W))
    val reg2dp_pad_value_5x_cfg = Input(UInt(19.W))
    val reg2dp_pad_value_6x_cfg = Input(UInt(19.W))
    val reg2dp_pad_value_7x_cfg = Input(UInt(19.W))
    val reg2dp_partial_width_out_first = Input(UInt(10.W))
    val reg2dp_partial_width_out_last = Input(UInt(10.W))
    val reg2dp_partial_width_out_mid = Input(UInt(10.W))
    val reg2dp_recip_height_cfg = Input(UInt(17.W))
    val reg2dp_recip_width_cfg = Input(UInt(17.W))
}

class NV_NVDLA_PDP_CORE_cal2d(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new NV_NVDLA_PDP_CORE_cal2dIO)

//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 
withClock(io.nvdla_core_clk){
    ////////////////////////////////////////////////////////////////
    //==============================================================
    //bank depth follows rule of 16 elements in width in worst case
    //it's 64 in t194
    //--------------------------------------------------------------
    val bank_depth = (conf.BATCH_PDP_NUM*16-1).U

    //==============================================================
    // buffer the input data from pooling 1D unit
    // calculate the data postion in input-data-cube
    //
    //--------------------------------------------------------------
    val cur_datin_disable = RegInit(false.B)
    val one_width_disable = RegInit(false.B)

    val pooling1d_norm_rdy = Wire(Bool())
    val one_width_norm_rdy = pooling1d_norm_rdy & (~one_width_disable)
    io.pooling1d_pd.ready := one_width_norm_rdy & (~cur_datin_disable)

    //////////////////////////////////////////////////////////////////////////////////////
    val data_c_end = Wire(Bool())
    val load_din = io.pooling1d_pd.valid & io.pooling1d_pd.ready
    val stripe_receive_done = load_din & data_c_end
    val average_pooling_en = io.pooling_type_cfg === 0.U

    //////////////////////////////////////////////////////////////////////////////////////
    val c_cnt = RegInit(0.U(5.W))
    when(load_din){
        when(data_c_end){
            c_cnt := 0.U
        }
        .otherwise{
            c_cnt := c_cnt + 1.U
        }
    }
    data_c_end := c_cnt === (conf.BATCH_PDP_NUM-1).U
    //end of line
    val wr_line_dat_done = Wire(Bool())
    val pout_width_cur = Wire(UInt(13.W))
    val wr_line_dat_cnt = RegInit(0.U(13.W))
    when(wr_line_dat_done){
        wr_line_dat_cnt := 0.U
    }
    .elsewhen(stripe_receive_done){
        wr_line_dat_cnt := wr_line_dat_cnt + 1.U
    }

    wr_line_dat_done := (wr_line_dat_cnt === pout_width_cur) & stripe_receive_done;
    //end of surface
    val wr_surface_dat_done = Wire(Bool())
    val wr_surface_dat_cnt = RegInit(0.U(13.W))
    when(wr_surface_dat_done){
        wr_surface_dat_cnt := 0.U
    }
    .elsewhen(wr_line_dat_done){
        wr_surface_dat_cnt := wr_surface_dat_cnt + 1.U
    }

    val last_line_in = wr_surface_dat_cnt === io.reg2dp_cube_in_height
    wr_surface_dat_done := wr_line_dat_done & last_line_in

    val surface_num = io.pooling_channel_cfg(12, conf.ATMMBW)

    val surface_cnt_rd = RegInit("b0".asUInt((13-conf.ATMMBW).W));
    val wr_subcube_dat_done = Wire(Bool())
    when(wr_subcube_dat_done){
        surface_cnt_rd := 0.U
    }
    .elsewhen(wr_surface_dat_done){
        surface_cnt_rd := surface_cnt_rd + 1.U
    }

    wr_subcube_dat_done := (surface_num === surface_cnt_rd) & wr_surface_dat_done

    //total cube done
    val wr_splitc_cnt = RegInit("b0".asUInt(8.W))
    val wr_total_cube_done = Wire(Bool())
    when(wr_total_cube_done){
        wr_splitc_cnt := 0.U
    }
    .elsewhen(wr_subcube_dat_done){
        wr_splitc_cnt := wr_splitc_cnt + 1.U
    }

    wr_total_cube_done := (wr_splitc_cnt === io.pooling_splitw_num_cfg) & wr_subcube_dat_done;

    //////////////////////////////////////////////////////////////////////////////////////
    //split width selection 
    val splitw_enable = (io.pooling_splitw_num_cfg =/= 0.U) 
    val last_splitw = (wr_splitc_cnt === io.pooling_splitw_num_cfg) & splitw_enable
    val first_splitw = (wr_splitc_cnt === 0.U) & splitw_enable
    
    pout_width_cur := Mux(~splitw_enable, io.reg2dp_cube_out_width, 
                      Mux(last_splitw, io.pooling_out_lwidth_cfg,
                      Mux(first_splitw, io.pooling_out_fwidth_cfg, 
                      io.pooling_out_mwidth_cfg)))

    //=============================================================
    // physical memory bank 8
    // 8 memory banks are used to load maximum 8 pooling output lines
    //
    //-------------------------------------------------------------
    val pooling_size_v = Wire(UInt(4.W))
    val u_bank_merge_num = Module(new NV_NVDLA_PDP_CORE_CAL2D_bank_merge_num)
    u_bank_merge_num.io.nvdla_core_clk := io.nvdla_core_clk
    u_bank_merge_num.io.pooling_size_v := pooling_size_v
    u_bank_merge_num.io.pooling_stride_v_cfg := io.pooling_stride_v_cfg
    u_bank_merge_num.io.pooling_size_v_cfg := io.pooling_size_v_cfg
    u_bank_merge_num.io.pdp_op_start := io.pdp_op_start
    val bank_merge_num = u_bank_merge_num.io.bank_merge_num
    val buffer_lines_num = u_bank_merge_num.io.buffer_lines_num

    //==========================================================
    //bank active enable signal 
    //
    //----------------------------------------------------------
    //stride intial data

    //stride ==1
    val padding_stride1_num = io.padding_v_cfg
    //stride ==2
    val padding_stride2_num = io.padding_v_cfg(2, 1)
    //stride ==3
    val padding_stride3_num = Mux(io.padding_v_cfg >= 6.U, 2.U,
                              Mux(io.padding_v_cfg >= 3.U, 1.U,
                              0.U))
    //stride==4 5 6 7
    val padding_stride4_num = Mux(io.padding_v_cfg > io.pooling_stride_v_cfg, 1.U, 0.U)
    val pooling_stride_v = io.pooling_stride_v_cfg +& 1.U
    //real num-1
    val padding_stride_num = MuxLookup(io.pooling_stride_v_cfg, padding_stride4_num,
                             Array(0.U -> padding_stride1_num,
                                   1.U -> padding_stride2_num,
                                   2.U -> padding_stride3_num))

    val strip_ycnt_offset = (io.padding_v_cfg -& padding_stride_num * pooling_stride_v)(2, 0)

    /////////////////////////////////////////////////////////////////////////////////
    val strip_ycnt_stride = RegInit("b0".asUInt(4.W))
    val strip_ycnt_stride_f = Wire(UInt(4.W))

    val middle_surface_trig = wr_surface_dat_done & (~wr_total_cube_done);
    val stride_end = wr_line_dat_done & (strip_ycnt_stride === io.pooling_stride_v_cfg);
    val init_cnt = middle_surface_trig | io.pdp_op_start;  

    //pooling stride in vertical direction
    when(init_cnt){
        strip_ycnt_stride_f := strip_ycnt_offset
    }
    .elsewhen(stride_end){
        strip_ycnt_stride_f := 0.U
    }
    .elsewhen(wr_line_dat_done){
        strip_ycnt_stride_f := strip_ycnt_stride + 1.U
    }
    .otherwise{
        strip_ycnt_stride_f := strip_ycnt_stride
    }

    when(init_cnt | stride_end | wr_line_dat_done){
        strip_ycnt_stride := strip_ycnt_stride_f
    }

    //2D pooling result ready
    val strip_ycnt_psize = RegInit("b0".asUInt(3.W))
    val pooling_2d_rdy = Wire(Bool())

    val pooling_size_minus_stride = io.pooling_size_v_cfg - io.pooling_stride_v_cfg

    when(init_cnt){
        strip_ycnt_psize := io.padding_v_cfg
    }
    .elsewhen(io.pooling_size_v_cfg >= io.pooling_stride_v_cfg){
        when(pooling_2d_rdy){
            strip_ycnt_psize := pooling_size_minus_stride
        }
        .elsewhen(wr_line_dat_done){
            strip_ycnt_psize := strip_ycnt_psize + 1.U
        }
    }
    .otherwise{
        when(strip_ycnt_stride_f <= io.pooling_size_v_cfg){
            strip_ycnt_psize := strip_ycnt_stride_f
        }
        .otherwise{
            strip_ycnt_psize := 0.U
        }
    }

    //=====================================================================
    pooling_size_v := io.pooling_size_v_cfg +& 1.U

    val pooling_size = pooling_size_v
    val stride = pooling_stride_v
    val pad_l = io.padding_v_cfg
    val pad_r = io.reg2dp_pad_bottom_cfg

    //line num need flush at surface end
    val stride_1x = stride
    val stride_2x = Cat(stride, 0.U)
    val stride_3x = stride_2x +& stride
    val stride_4x = Cat(stride, "b0".asUInt(2.W))
    val stride_5x = stride_4x +& stride
    val stride_6x = stride_3x +& stride_3x
    val stride_7x = stride_4x +& stride_3x
    val flush_num_cal = Wire(UInt(3.W))

    when(pad_r < stride_1x ){
        flush_num_cal := 0.U
    }
    .elsewhen(pad_r < stride_2x){
        flush_num_cal := 1.U
    }
    .elsewhen(pad_r < stride_3x){
        flush_num_cal := 2.U
    }
    .elsewhen(pad_r < stride_4x){
        flush_num_cal := 3.U
    }
    .elsewhen(pad_r < stride_5x){
        flush_num_cal := 4.U
    }
    .elsewhen(pad_r < stride_6x){
        flush_num_cal := 5.U
    }
    .elsewhen(pad_r < stride_7x){
        flush_num_cal := 6.U
    }
    .otherwise{
        flush_num_cal := 7.U
    }
    
    //small input detect
    val small_active = (~(io.reg2dp_cube_in_height(12, 3).orR)) & ((io.reg2dp_cube_in_height(2, 0) +& io.reg2dp_pad_top(2, 0)) < io.reg2dp_kernel_height(2, 0))
    //non-split mode cube_width + pad_left + pad_right
    val h_pt = io.reg2dp_cube_in_height(2, 0) +& io.reg2dp_pad_top
    val h_pt_pb = h_pt +& pad_r

    //pad_right remain afrer 1st kernel pooling
    val pad_r_remain = Wire(UInt(6.W))
    when(small_active){
        pad_r_remain := h_pt_pb -& io.reg2dp_kernel_height(2, 0)
    }
    .otherwise{
        pad_r_remain := 0.U
    }

    //how many need bubble after 1st kernel pooling
    val samllH_flush_num = Wire(UInt(3.W))
    when(pad_r_remain === stride_6x){
        samllH_flush_num := 6.U
    }
    .elsewhen(pad_r_remain === stride_5x){
        samllH_flush_num := 5.U
    }
    .elsewhen(pad_r_remain === stride_4x){
        samllH_flush_num := 4.U
    }
    .elsewhen(pad_r_remain === stride_3x){
        samllH_flush_num := 3.U
    }
    .elsewhen(pad_r_remain === stride_2x){
        samllH_flush_num := 2.U
    }
    .elsewhen(pad_r_remain === stride_1x){
        samllH_flush_num := 1.U
    }
    .otherwise{
        samllH_flush_num := 0.U
    }

    //flush num calc
    val flush_num = Wire(UInt(3.W))
    when(flush_num_cal === 0.U){
        flush_num := 0.U
    }
    .elsewhen(small_active){
        flush_num := samllH_flush_num
    }
    .otherwise{
        flush_num := flush_num_cal
    }

    val need_flush = (flush_num =/= 0.U)

    //the 1st element/line num need output data
    val cube_in_height_cfg = io.reg2dp_cube_in_height +& 1.U
    val first_out_num = Mux(small_active, cube_in_height_cfg, pooling_size - pad_l)

    val need_bubble = RegInit(false.B)
    val bubble_num_use = RegInit("b0".asUInt(3.W))
    val bubble_add = Wire(UInt(3.W))
    when(wr_subcube_dat_done){
       when(need_flush){
           need_bubble := true.B
           bubble_num_use := flush_num
       }
       .otherwise{
           need_bubble := false.B
           bubble_num_use := 0.U
       }
    }
    .elsewhen(last_line_in){
        when(flush_num >= first_out_num){
            need_bubble := true.B
            bubble_num_use := flush_num -& first_out_num +& 1.U +& bubble_add
        }
        .elsewhen(bubble_add.orR){
            need_bubble := true.B
            bubble_num_use := bubble_add
        }
        .otherwise{
            need_bubble := false.B
            bubble_num_use := 0.U
        }
    }

    ///////////////////////////////////////////////////////////////////////
    //bubble control when next surface comming .  Beginning
    ///////////////////////////////////////////////////////////////////////
    val up_pnum = Wire(Vec(6, Bool()))
    val pnum_flush = Wire(Vec(7, Bool()))
    val u_bcontrol_begin = Module(new NV_NVDLA_PDP_CORE_CAL2D_bubble_control_begin)
    u_bcontrol_begin.io.nvdla_core_clk := io.nvdla_core_clk
    u_bcontrol_begin.io.pdp_op_start := io.pdp_op_start
    u_bcontrol_begin.io.flush_num := flush_num
    u_bcontrol_begin.io.first_out_num := first_out_num 
    u_bcontrol_begin.io.up_pnum := up_pnum
    u_bcontrol_begin.io.pnum_flush := pnum_flush
    bubble_add := u_bcontrol_begin.io.bubble_add
    val flush_in_next_surf = u_bcontrol_begin.io.flush_in_next_surf

    //pooling No. in flush time
    val u_pnum_flush = Module(new NV_NVDLA_PDP_CORE_CAL2D_pnum_flush)
    val unit2d_cnt_pooling = RegInit("b0".asUInt(3.W))
    val unit2d_cnt_pooling_max = Wire(UInt(3.W))
    u_pnum_flush.io.nvdla_core_clk := io.nvdla_core_clk
    u_pnum_flush.io.unit2d_cnt_pooling := unit2d_cnt_pooling
    u_pnum_flush.io.unit2d_cnt_pooling_max := unit2d_cnt_pooling_max
    u_pnum_flush.io.last_line_in := last_line_in
    pnum_flush := u_pnum_flush.io.pnum_flush

    //-------------------------
    //update pooling No. in line2 of next surface
    //-------------------------
    val u_pnum_updt = Module(new NV_NVDLA_PDP_CORE_CAL2D_pnum_updt)
    u_pnum_updt.io.nvdla_core_clk := io.nvdla_core_clk
    u_pnum_updt.io.padding_v_cfg := io.padding_v_cfg
    u_pnum_updt.io.stride := stride
    up_pnum := u_pnum_updt.io.up_pnum

    ///////////////////////////////////////////////////////////////////////
    //bubble control when next surface comming .  Ending
    ///////////////////////////////////////////////////////////////////////
    val is_one_width_in = Wire(Bool())
    val one_width_bubble_end = Wire(Bool())
    val subend_need_flush_flg = RegInit(false.B)
    when(wr_subcube_dat_done & need_flush & is_one_width_in){
        subend_need_flush_flg := true.B
    }
    .elsewhen(one_width_bubble_end){
        subend_need_flush_flg := false.B
    }

    val surfend_need_bubble_flg = RegInit(false.B)
    when(wr_surface_dat_done & need_bubble & is_one_width_in){
        surfend_need_bubble_flg := true.B
    }
    .elsewhen(one_width_bubble_end){
        surfend_need_bubble_flg := false.B
    }

    /////////////////////////////////////////
    val bubble_en_end = Wire(Bool())
    when((wr_subcube_dat_done & need_flush & (~is_one_width_in)) | (subend_need_flush_flg & one_width_bubble_end)){
        cur_datin_disable := true.B
    }
    .elsewhen((wr_subcube_dat_done & need_bubble & (~is_one_width_in)) | (subend_need_flush_flg & one_width_bubble_end)){
        cur_datin_disable := true.B
    }
    .elsewhen(bubble_en_end){
        cur_datin_disable := false.B
    }

    ///////////////////////////////////////////
    val pout_width_cur_latch = RegInit("b0".asUInt(13.W))
    when((wr_subcube_dat_done & need_flush) || (wr_surface_dat_done & need_bubble)){
        pout_width_cur_latch := pout_width_cur
    }

    val channel_cnt = RegInit("b0".asUInt(5.W))
    val last_c = Wire(Bool())
    when(cur_datin_disable){
        when(last_c){
            channel_cnt := 0.U
        }
        .elsewhen(one_width_norm_rdy){
            channel_cnt := channel_cnt + 1.U
        }
    }
    .otherwise{
        channel_cnt := 0.U
    }

    last_c := (channel_cnt === (conf.BATCH_PDP_NUM - 1).U) & one_width_norm_rdy

    val line_cnt = RegInit("b0".asUInt(13.W))
    val line_end = Wire(Bool())
    when(cur_datin_disable){
        when(line_end){
            line_cnt := 0.U
        }
        .elsewhen(last_c){
            line_cnt := line_cnt + 1.U
        }
    }
    .otherwise{
        line_cnt := 0.U
    }

    line_end := (line_cnt === pout_width_cur_latch) & last_c

    val bubble_cnt = RegInit("b0".asUInt(3.W))
    when(cur_datin_disable){
        when(bubble_en_end){
            bubble_cnt := 0.U
        }
        .elsewhen(line_end){
            bubble_cnt := bubble_cnt + 1.U
        }
    }
    .otherwise{
        bubble_cnt := 0.U
    }

    bubble_en_end := (bubble_cnt === (bubble_num_use -& 1.U)) & line_end;

    //////////////////////////////////////////////////////
    //last lines output en during new lines comming
    //----------------------------------------------------
    //cube end flag for last_out_en control in the cube end
    val cube_end_flag = RegInit(false.B)
    when(wr_subcube_dat_done){
        cube_end_flag := true.B
    }
    .elsewhen(load_din){
        cube_end_flag := false.B
    }

    val last_out_en = RegInit(false.B)
    val last_out_done = Wire(Bool())
    when(first_out_num =/= 1.U){
        when((need_bubble & bubble_en_end & (~cube_end_flag) & (bubble_add < flush_in_next_surf)) | (~need_bubble & need_flush & wr_surface_dat_done & (~wr_subcube_dat_done))){
            last_out_en := true.B
        }
        .elsewhen(last_out_done){
            last_out_en := false.B
        }
    }
    .otherwise{
        last_out_en := false.B
    }

    val first_out_num_dec2 = flush_num - bubble_num_use - 1.U   //first_out_num - 2'd2;
    val last_out_cnt = RegInit("b0".asUInt(3.W))
    val flush_num_dec1 = Wire(UInt(3.W))
    when(last_out_en){
        when(wr_line_dat_done){
            when(((last_out_cnt === first_out_num_dec2) & need_bubble) | (~need_bubble & (last_out_cnt === flush_num_dec1))){
                last_out_cnt := 0.U
            }
            .otherwise{
                last_out_cnt := last_out_cnt + 1.U
            }
        }
    }
    .otherwise{
        last_out_cnt := 0.U
    }
    flush_num_dec1 := flush_num - 1.U

    last_out_done := (((last_out_cnt === first_out_num_dec2) & need_bubble) | (~need_bubble & (last_out_cnt === flush_num_dec1))) & wr_line_dat_done & last_out_en;

    ///////////////////////////////////////////////////////////////////////
    //bubble control when input width is only 1 element in width
    ///////////////////////////////////////////////////////////////////////
    when(~splitw_enable){
        is_one_width_in := io.reg2dp_cube_out_width === 0.U
    }
    .elsewhen(first_splitw){
        is_one_width_in := io.reg2dp_partial_width_out_first === 0.U
    }
    .elsewhen(last_splitw){
        is_one_width_in := io.reg2dp_partial_width_out_first === 0.U
    }
    .otherwise{
        is_one_width_in := Mux(io.pooling_splitw_num_cfg > 1.U, io.reg2dp_partial_width_out_mid, false.B)
    }

    /////////////
    when(wr_line_dat_done & is_one_width_in){
        one_width_disable := true.B
    }
    .elsewhen(one_width_bubble_end){
        one_width_disable := false.B
    }

    /////////////
    val one_width_bubble_cnt = RegInit("b0".asUInt(3.W))
    when(one_width_disable){
       when(one_width_bubble_end){
           one_width_bubble_cnt := 0.U
       }
       .elsewhen(pooling1d_norm_rdy){
           one_width_bubble_cnt := one_width_bubble_cnt + 1.U
       }
    }
    .otherwise{
        one_width_bubble_cnt := 0.U
    }

    one_width_bubble_end := (one_width_bubble_cnt === (4-2).U) & pooling1d_norm_rdy

    //////////////////////////////////////////////////////

    pooling_2d_rdy := wr_line_dat_done & (strip_ycnt_psize === io.pooling_size_v_cfg)

    //=====================================================================
    //pooling 2D unit counter
    //
    //---------------------------------------------------------------------
    val unit2d_cnt_stride = RegInit("b0".asUInt(3.W))
    val stride_trig_end = Wire(Bool())
    when(init_cnt){
        unit2d_cnt_stride := padding_stride_num
    }
    .elsewhen(stride_end){
        when(stride_trig_end){
            unit2d_cnt_stride := 0.U
        }
        .otherwise{
            unit2d_cnt_stride := unit2d_cnt_stride + 1.U
        }
    }

    stride_trig_end := (unit2d_cnt_pooling_max === unit2d_cnt_stride)

    val unit2d_cnt_pooling_end = Wire(Bool())
    when(init_cnt){
        unit2d_cnt_pooling := 0.U
    }
    .elsewhen(pooling_2d_rdy | wr_surface_dat_done){
        when(unit2d_cnt_pooling_end){
            unit2d_cnt_pooling := 0.U
        }
        .otherwise{
            unit2d_cnt_pooling := unit2d_cnt_pooling + 1.U
        }
    }

    unit2d_cnt_pooling_end := (unit2d_cnt_pooling === unit2d_cnt_pooling_max)

    unit2d_cnt_pooling_max := (buffer_lines_num - 1.U)(2, 0)

    //-------------------------
    //flag the last one pooling in height direction
    //-------------------------
    val rest_height = io.reg2dp_cube_in_height - wr_surface_dat_cnt
    val rest_height_use = rest_height +& io.reg2dp_pad_bottom_cfg
    val last_pooling_flag = rest_height_use <= io.pooling_size_v_cfg
    //unit2d pooling enable  
    val init_unit2d_set = VecInit((0 to 7) 
                    map {i => init_cnt & (padding_stride_num >= i.U)}) 
    val unit2d_set_trig = Wire(Vec(8, Bool()))
    unit2d_set_trig(0) := stride_end & stride_trig_end & (~last_pooling_flag)
    for(i <- 1 to 7){
        unit2d_set_trig(i) := stride_end & (unit2d_cnt_stride === (i-1).U) & (~stride_trig_end) & (~last_pooling_flag)
    }
    val unit2d_set = VecInit((0 to 7) 
                    map {i => unit2d_set_trig(i)|init_unit2d_set(i)}) 
    val unit2d_clr = VecInit((0 to 7) 
                    map {i => (pooling_2d_rdy & (unit2d_cnt_pooling === i.U)) | wr_surface_dat_done}) 
    val unit2d_en = RegInit(VecInit(Seq.fill(8)(false.B)))
    for(i <- 0 to 7){
        when(wr_total_cube_done){
            unit2d_en(i) := false.B
        }
        .elsewhen(unit2d_set(i)){
            unit2d_en(i) := true.B
        }
        .elsewhen(unit2d_clr(i)){
            unit2d_en(i) := false.B
        }
    }
    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    val datin_buf = RegInit("b0".asUInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)).W))
    val wr_line_end_buf = RegInit(false.B)
    val wr_surface_dat_done_buf = RegInit(false.B)

    when(load_din){
        datin_buf := io.pooling1d_pd.bits
        wr_line_end_buf := wr_line_dat_done
        wr_surface_dat_done_buf := wr_surface_dat_done
    }

    //////////////////////////////////////////////////////////////////////
    //calculate the real pooling size within one pooling 
    //PerBeg
    val mem_re_sel = Wire(Vec(4, Bool()))
    val u_calculate_real_pooling_size = Module(new NV_NVDLA_PDP_CORE_CAL2D_calculate_real_pooling_size)
    u_calculate_real_pooling_size.io.nvdla_core_clk := io.nvdla_core_clk
    u_calculate_real_pooling_size.io.wr_line_dat_done := wr_line_dat_done
    u_calculate_real_pooling_size.io.mem_re_sel := mem_re_sel
    u_calculate_real_pooling_size.io.unit2d_set := unit2d_set
    u_calculate_real_pooling_size.io.unit2d_en := unit2d_en
    val unit2d_vsize_cnt_d = u_calculate_real_pooling_size.io.unit2d_vsize_cnt_d

    //============================================================
    val active_last_line = (strip_ycnt_psize === io.pooling_size_v_cfg) | last_line_in
    //============================================================
    //memory bank read/write controller
    //
    //------------------------------------------------------------
    //memory read
    //mem bank0 enable
    //
    val wr_sub_lbuf_cnt = RegInit("b0".asUInt(3.W))
    val u_mem_rd = Module(new NV_NVDLA_PDP_CORE_CAL2D_mem_rd)
    u_mem_rd.io.nvdla_core_clk := io.nvdla_core_clk
    u_mem_rd.io.load_din := load_din
    u_mem_rd.io.wr_line_dat_done := wr_line_dat_done
    u_mem_rd.io.unit2d_en := unit2d_en
    u_mem_rd.io.unit2d_set := unit2d_set
    u_mem_rd.io.buffer_lines_num := buffer_lines_num
    u_mem_rd.io.wr_sub_lbuf_cnt := wr_sub_lbuf_cnt
    val mem_re_vec = u_mem_rd.io.mem_re
    val mem_re_1st_vec = u_mem_rd.io.mem_re_1st
    mem_re_sel := u_mem_rd.io.mem_re_sel

    ///////////////////////////
    //shouldn't read data from mem for the first pooling line
    ///////////////////////////
    val sub_lbuf_dout_cnt = RegInit("b0".asUInt(9.W))
    val mem_re = mem_re_vec(0).asUInt | mem_re_vec(1).asUInt | mem_re_vec(2).asUInt | mem_re_vec(3).asUInt
    val mem_re_1st = mem_re_1st_vec(0).asUInt | mem_re_1st_vec(1).asUInt | mem_re_1st_vec(2).asUInt | mem_re_1st_vec(3).asUInt
    val mem_raddr = sub_lbuf_dout_cnt

    //line buffer counter
    val sub_lbuf_dout_done = Wire(Bool())
    val last_sub_lbuf_done = Wire(Bool())
    when(wr_line_dat_done | last_sub_lbuf_done | line_end){
        wr_sub_lbuf_cnt := 0.U
    }
    .elsewhen(sub_lbuf_dout_done){
        wr_sub_lbuf_cnt := wr_sub_lbuf_cnt + 1.U
    }
    last_sub_lbuf_done := ((bank_merge_num -& 1.U) === wr_sub_lbuf_cnt) & sub_lbuf_dout_done
    //--------------------------------------------------------------------
    when(sub_lbuf_dout_done | wr_line_dat_done | line_end){
        sub_lbuf_dout_cnt := 0.U
    }
    .elsewhen(load_din | (cur_datin_disable & one_width_norm_rdy)){
        sub_lbuf_dout_cnt := sub_lbuf_dout_cnt + 1.U
    }

    sub_lbuf_dout_done := (sub_lbuf_dout_cnt === bank_depth) & (load_din | (cur_datin_disable & one_width_norm_rdy))
    //==============================================================================================
    //buffer the data from memory  and from UNIT1D
    //
    //----------------------------------------------------------------------------------------------
    //write memory
    val flush_read_en_d = RegInit(false.B)
    val wr_data_stage0_prdy = Wire(Bool())
    val load_wr_stage1 = Wire(Bool())
    val mem_data_lst = RegInit(VecInit(Seq.fill(8)("b0".asUInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)+3).W))))
    val mem_data = RegInit(VecInit(Seq.fill(8)("b0".asUInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)+3).W))))
    val mem_rdata = Wire(Vec(8, UInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)+4).W)))
    when(flush_read_en_d & wr_data_stage0_prdy){
        for(i <- 0 to 7){
            mem_data_lst(i) := mem_rdata(i)(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)+2, 0)
        }
    }
    val datin_buf_2d = RegInit("b0".asUInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)).W))
    val wr_line_end_2d = RegInit(false.B)
    val wr_surface_dat_done_2d = RegInit(false.B)
    val last_active_line_d = RegInit(false.B)
    val last_active_line_2d = RegInit(false.B)
    val mem_re_1st_d = RegInit("b0".asUInt(8.W))
    val mem_re_1st_2d = RegInit("b0".asUInt(8.W))
    val mem_raddr_d = RegInit("b0".asUInt(9.W))
    val mem_raddr_2d = RegInit("b0".asUInt(9.W))
    when(load_wr_stage1){
        datin_buf_2d := datin_buf
        wr_line_end_2d := wr_line_end_buf
        wr_surface_dat_done_2d := wr_surface_dat_done_buf
        last_active_line_d := active_last_line
        last_active_line_2d := last_active_line_d
        mem_re_1st_d := mem_re_1st
        mem_re_1st_2d := mem_re_1st_d
        mem_raddr_2d := mem_raddr_d  
        for(i <- 0 to 7){
            mem_data(i) := Mux(mem_re_1st_d(i), Cat(unit2d_vsize_cnt_d(i), datin_buf),
                           Cat(unit2d_vsize_cnt_d(i), mem_rdata(i)))
        }  
    }
    val flush_read_en = Wire(Bool())
    when((mem_re.asUInt.orR) | (flush_read_en & one_width_norm_rdy)){
        mem_raddr_d := mem_raddr
    }

    //=========================== 
    //8bits mem_re two cycle delay
    val mem_re_d = RegInit("b0".asUInt(8.W))
    when(load_din){
        mem_re_d := mem_re
    }

    val mem_re_2d = RegInit("b0".asUInt(8.W))
    when(load_wr_stage1){
        mem_re_2d := mem_re_d
    }

    //write stage0
    val wr_data_stage0_vld = RegInit(false.B)
    pooling1d_norm_rdy := ~wr_data_stage0_vld | wr_data_stage0_prdy
    //rebuild valid signal with cur_datin_disable control
    val pooling1d_vld_rebuild = Mux(one_width_disable | cur_datin_disable, true.B, io.pooling1d_pd.valid)
    val load_din_all = pooling1d_norm_rdy & pooling1d_vld_rebuild
    //pipe delay
    when(pooling1d_vld_rebuild){
        wr_data_stage0_vld := true.B
    }
    .elsewhen(wr_data_stage0_prdy){
        wr_data_stage0_vld := false.B
    }

    val wr_data_stage1_vld = RegInit(false.B)
    val wr_data_stage1_prdy = Wire(Bool())
    wr_data_stage0_prdy := ~wr_data_stage1_vld | wr_data_stage1_prdy

    //write  stage1
    val cur_datin_disable_d = RegInit(false.B)
    val one_width_disable_d = RegInit(false.B)
    val load_wr_stage1_all = wr_data_stage0_vld & wr_data_stage0_prdy
    load_wr_stage1 := wr_data_stage0_vld & wr_data_stage0_prdy & (~cur_datin_disable_d) & (~one_width_disable_d);
    when(wr_data_stage0_vld){
        wr_data_stage1_vld := true.B
    }
    .elsewhen(wr_data_stage1_prdy){
        wr_data_stage1_vld := false.B
    }

    //write stage2
    val wr_data_stage2_vld = RegInit(false.B)
    val pout_data_stage0_prdy = Wire(Bool())
    val cur_datin_disable_2d = RegInit(false.B)
    val one_width_disable_2d = RegInit(false.B)
    val load_wr_stage2_all = wr_data_stage1_vld & wr_data_stage1_prdy
    val load_wr_stage2 = wr_data_stage1_vld & wr_data_stage1_prdy & (~cur_datin_disable_2d) & (~one_width_disable_2d);
    when(wr_data_stage1_vld){
        wr_data_stage2_vld := true.B
    }
    .elsewhen(pout_data_stage0_prdy){
        wr_data_stage2_vld := false.B
    }

    val cur_datin_disable_3d = RegInit(false.B)
    val one_width_disable_3d = RegInit(false.B)
    val load_wr_stage3_all = wr_data_stage2_vld & pout_data_stage0_prdy
    val load_wr_stage3 = wr_data_stage2_vld & pout_data_stage0_prdy & (~cur_datin_disable_3d) & (~one_width_disable_3d)

    //====================================================================
    // pooling data calculation and write back
    //
    //--------------------------------------------------------------------
    val pooling_datin = datin_buf_2d
    //read from memory
    val mem_data_valid = Mux(load_wr_stage2, mem_re_2d, "b0".asUInt(8.W))
    val pooling_2d_result = VecInit((0 to 7) 
    map{ i => 
        Mux(io.pooling_type_cfg===2.U, pooling_SUM(pooling_datin, mem_data(i)(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)-1, 0)),
        Mux(io.pooling_type_cfg===1.U, pooling_MIN(pooling_datin, mem_data(i)(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)-1, 0), mem_data_valid(i)),
        Mux(io.pooling_type_cfg===0.U, pooling_MAX(pooling_datin, mem_data(i)(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)-1, 0), mem_data_valid(i)), 
    0.U)))})

    val pooling_2d_info = VecInit((0 to 7) map { i => 
    Cat(wr_line_end_2d, mem_data(i)(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)+2, conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)))
     })

    //memory write data
    val int_mem_wdata = RegInit(VecInit(Seq.fill(8)("b0".asUInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)+4).W))))
    when(load_wr_stage2){
        for(i <- 0 to 7){
            int_mem_wdata(i) := Cat(pooling_2d_info(i), pooling_2d_result(i))
        }
    }

    //write enabel signal
    val int_mem_we = RegInit("b0".asUInt(8.W))
    when(load_wr_stage2){
        int_mem_we := mem_re_2d
    }

    val int_mem_waddr = RegInit("b0".asUInt(9.W))
    when(load_wr_stage2){
        int_mem_waddr := mem_raddr_2d
    }

    //memory write select 
    val mem_wdata = int_mem_wdata
    val mem_we = int_mem_we & Fill(8, load_wr_stage3)
    val mem_waddr = VecInit((0 to 7) map { i => int_mem_waddr})

    //=============================================================================
    //memory line buffer instance
    // 
    //-----------------------------------------------------------------------------
    val bank_uram_0 = Array.fill(8){Module(new nv_ram_rws(dep = 16*conf.BATCH_PDP_NUM, wid = (conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)+4)))}
    val mem_re_last = Wire(UInt(8.W))
    for(i<- 0 to 7){
        bank_uram_0(i).io.clk := io.nvdla_core_clk
        bank_uram_0(i).io.re := mem_re(i)|mem_re_last(i)
        bank_uram_0(i).io.ra := mem_raddr(log2Ceil(16*conf.BATCH_PDP_NUM)-1, 0)
        bank_uram_0(i).io.we := mem_we
        bank_uram_0(i).io.wa := mem_waddr(i)(log2Ceil(16*conf.BATCH_PDP_NUM)-1, 0)
        bank_uram_0(i).io.di := mem_wdata(i)
        mem_rdata(i) := bank_uram_0(i).io.dout
    }

    //==============================================================================
    //data reading control during datin_disable time
    //
    val u_rd_control_in_disable_time = Module(new NV_NVDLA_PDP_CORE_CAL2D_rd_control_in_disable_time)
    u_rd_control_in_disable_time.io.nvdla_core_clk := io.nvdla_core_clk
    u_rd_control_in_disable_time.io.line_end := line_end
    u_rd_control_in_disable_time.io.cur_datin_disable := cur_datin_disable
    u_rd_control_in_disable_time.io.wr_surface_dat_done := wr_surface_dat_done
    u_rd_control_in_disable_time.io.wr_line_dat_done := wr_line_dat_done
    u_rd_control_in_disable_time.io.wr_sub_lbuf_cnt := wr_sub_lbuf_cnt
    u_rd_control_in_disable_time.io.last_out_en := last_out_en
    u_rd_control_in_disable_time.io.one_width_norm_rdy := one_width_norm_rdy
    u_rd_control_in_disable_time.io.load_din := load_din
    u_rd_control_in_disable_time.io.load_din_all := load_din_all
    u_rd_control_in_disable_time.io.wr_data_stage0_prdy := wr_data_stage0_prdy
    u_rd_control_in_disable_time.io.wr_data_stage1_prdy := wr_data_stage1_prdy

    u_rd_control_in_disable_time.io.load_wr_stage1 := load_wr_stage1
    u_rd_control_in_disable_time.io.load_wr_stage1_all := load_wr_stage1_all
    u_rd_control_in_disable_time.io.load_wr_stage2 := load_wr_stage2
    u_rd_control_in_disable_time.io.load_wr_stage2_all := load_wr_stage2_all

    u_rd_control_in_disable_time.io.unit2d_cnt_pooling := unit2d_cnt_pooling
    u_rd_control_in_disable_time.io.unit2d_cnt_pooling_max := unit2d_cnt_pooling_max

    u_rd_control_in_disable_time.io.mem_re_sel := mem_re_sel
    u_rd_control_in_disable_time.io.mem_data_lst := mem_data_lst

    val mem_re_last_2d = u_rd_control_in_disable_time.io.mem_re_last_2d
    flush_read_en := u_rd_control_in_disable_time.io.flush_read_en
    mem_re_last := u_rd_control_in_disable_time.io.mem_re_last
    val pout_mem_data_last = u_rd_control_in_disable_time.io.pout_mem_data_last

    //==============================================================================
    //unit2d pooling data read out
    //
    //
    //------------------------------------------------------------------------------
    //data count in sub line 
    val rd_line_out = Wire(Bool())
    val rd_sub_lbuf_end = Wire(Bool())
    val rd_line_out_done = wr_line_end_2d & rd_line_out

    val rd_line_out_cnt = RegInit("b0".asUInt(9.W))
    when(rd_line_out_done | rd_sub_lbuf_end){
        rd_line_out_cnt := 0.U
    }
    .elsewhen(rd_line_out){
        rd_line_out_cnt := rd_line_out_cnt + 1.U
    }

    rd_sub_lbuf_end := ((rd_line_out & (rd_line_out_cnt === bank_depth)) | rd_line_out_done)

    //sub line buffer counter
    val rd_sub_lbuf_cnt = RegInit("b0".asUInt(3.W))
    val rd_comb_lbuf_end = Wire(Bool())
    when(rd_comb_lbuf_end){
        rd_sub_lbuf_cnt := 0.U
    }
    .elsewhen(rd_sub_lbuf_end){
        rd_sub_lbuf_cnt := rd_sub_lbuf_cnt + 1.U
    }

    rd_comb_lbuf_end := (rd_sub_lbuf_end & (rd_sub_lbuf_cnt ===(bank_merge_num -& 1.U))) | rd_line_out_done;

    //combine line buffer counter
    val rd_comb_lbuf_cnt = RegInit("b0".asUInt(3.W))
    val rd_lbuf_end = Wire(Bool())
    when(rd_lbuf_end | (wr_surface_dat_done_2d & load_wr_stage2)){
        rd_comb_lbuf_cnt := 0.U
    }
    .elsewhen(rd_comb_lbuf_end & last_active_line_2d){
        rd_comb_lbuf_cnt := rd_comb_lbuf_cnt + 1.U
    }

    rd_lbuf_end := (rd_comb_lbuf_cnt === (buffer_lines_num -& 1.U)) & rd_comb_lbuf_end & last_active_line_2d

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //unit2d_data_rdy need two active delays as load_wr_stage2
    val pout_mem_data_sel = Wire(UInt(8.W))
    rd_line_out := pout_mem_data_sel.orR
    val rd_pout_data_en = (rd_line_out | ((load_wr_stage2 & (mem_re_last_2d.orR))| (cur_datin_disable_2d & wr_data_stage1_prdy)));

    //read output stage
    wr_data_stage1_prdy := (~wr_data_stage2_vld | pout_data_stage0_prdy)

    val rd_pout_data_en_d = RegInit(false.B)
    when(load_wr_stage2_all){
        rd_pout_data_en_d := rd_pout_data_en
    }

    val pout_data_stage1_vld = RegInit(false.B)
    val pout_data_stage1_prdy = Wire(Bool())
    val rd_pout_data_stage0 = load_wr_stage3_all & rd_pout_data_en_d
    pout_data_stage0_prdy := ~pout_data_stage1_vld | pout_data_stage1_prdy

    when(wr_data_stage2_vld){
        pout_data_stage1_vld := true.B
    }
    .elsewhen(pout_data_stage1_prdy){
        pout_data_stage1_vld := false.B
    }

    val pout_data_stage2_vld = RegInit(false.B)
    val pout_data_stage2_prdy = Wire(Bool())
    pout_data_stage1_prdy := ~pout_data_stage2_vld | pout_data_stage2_prdy

    val rd_pout_data_en_2d = RegInit(false.B)
    when(load_wr_stage3_all){
        rd_pout_data_en_2d := rd_pout_data_en_d
    }
    val rd_pout_data_stage1_all = pout_data_stage1_vld & pout_data_stage1_prdy
    val rd_pout_data_stage1 = pout_data_stage1_vld & pout_data_stage1_prdy & rd_pout_data_en_2d

    when(pout_data_stage1_vld){
        pout_data_stage2_vld := true.B
    }
    .elsewhen(pout_data_stage2_prdy){
        pout_data_stage2_vld := false.B
    }

    val pout_data_stage3_vld = RegInit(false.B)
    val pout_data_stage3_prdy = Wire(Bool())
    pout_data_stage2_prdy := ~pout_data_stage3_vld | pout_data_stage3_prdy

    val rd_pout_data_en_3d = RegInit(false.B)
    when(rd_pout_data_stage1_all){
        rd_pout_data_en_3d := rd_pout_data_en_2d
    }
    val rd_pout_data_stage2_all = pout_data_stage2_vld & pout_data_stage2_prdy
    val rd_pout_data_stage2 = pout_data_stage2_vld & pout_data_stage2_prdy & rd_pout_data_en_3d

    val rd_pout_data_en_4d = RegInit(false.B)
    when(rd_pout_data_stage2_all){
        rd_pout_data_en_4d := rd_pout_data_en_3d
    }

    when(pout_data_stage2_vld){
        pout_data_stage3_vld := true.B
    }
    .elsewhen(pout_data_stage3_prdy){
        pout_data_stage3_vld := false.B
    }

    /////////////////////////////////////////////////////////
    //line buffer1 
    val pout_mem_data_sel_0 = mem_re_2d & Fill(8, load_wr_stage2) & Fill(8, mem_re_sel(0)) & Fill(8, last_active_line_2d)
    //line buffer2
    val pout_mem_data_sel_1 = Wire(Vec(8, Bool()))
    for(i <- 0 to 3){
        pout_mem_data_sel_1(i) := mem_re_2d(i) & load_wr_stage2 & (rd_comb_lbuf_cnt === 0.U) & last_active_line_2d & mem_re_sel(1)
    }
    for(i <- 4 to 7){
        pout_mem_data_sel_1(i) := mem_re_2d(i) & load_wr_stage2 & (rd_comb_lbuf_cnt === 1.U) & last_active_line_2d & mem_re_sel(1)
    }
    //line buffer3,4
    val pout_mem_data_sel_2 = Wire(Vec(8, Bool()))
    for(i <- 0 to 1){
        pout_mem_data_sel_2(i) := mem_re_2d(i) & load_wr_stage2 & (rd_comb_lbuf_cnt === 0.U) & last_active_line_2d & mem_re_sel(2)
    }
    for(i <- 2 to 3){
        pout_mem_data_sel_2(i) := mem_re_2d(i) & load_wr_stage2 & (rd_comb_lbuf_cnt === 1.U) & last_active_line_2d & mem_re_sel(2)
    }
    for(i <- 4 to 5){
        pout_mem_data_sel_2(i) := mem_re_2d(i) & load_wr_stage2 & (rd_comb_lbuf_cnt === 2.U) & last_active_line_2d & mem_re_sel(2)
    }
    for(i <- 6 to 7){
        pout_mem_data_sel_2(i) := mem_re_2d(i) & load_wr_stage2 & (rd_comb_lbuf_cnt === 3.U) & last_active_line_2d & mem_re_sel(2)
    }
    //line buffer 5 6 7 8
    val pout_mem_data_sel_3 = Wire(Vec(8, Bool()))
    for(i <- 0 to 7){
        pout_mem_data_sel_3(i) := mem_re_2d(i) & load_wr_stage2 & (rd_comb_lbuf_cnt === i.U) & last_active_line_2d & mem_re_sel(3)
    }

    pout_mem_data_sel := (pout_mem_data_sel_3.asUInt | pout_mem_data_sel_2.asUInt | pout_mem_data_sel_1.asUInt | pout_mem_data_sel_0.asUInt)
    val pout_mem_data_act = MuxLookup(pout_mem_data_sel, "b0".asUInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)+3).W),
                            Array(
                                "h01".U -> Cat(pooling_2d_info(0), pooling_2d_result(0)),
                                "h02".U -> Cat(pooling_2d_info(1), pooling_2d_result(1)),
                                "h04".U -> Cat(pooling_2d_info(2), pooling_2d_result(2)),
                                "h08".U -> Cat(pooling_2d_info(3), pooling_2d_result(3)),
                                "h10".U -> Cat(pooling_2d_info(4), pooling_2d_result(4)),
                                "h20".U -> Cat(pooling_2d_info(5), pooling_2d_result(5)),
                                "h40".U -> Cat(pooling_2d_info(6), pooling_2d_result(6)),
                                "h80".U -> Cat(pooling_2d_info(7), pooling_2d_result(7)),

                            ))
    val int_pout_mem_data = pout_mem_data_act | pout_mem_data_last
    //=============================================================
    //pooling output data to DMA
    //
    //-------------------------------------------------------------
    val pout_mem_data = RegInit(VecInit(Seq.fill(conf.NVDLA_PDP_THROUGHPUT)("b0".asUInt((conf.NVDLA_BPE+6).W))))
    val pout_mem_size_v = RegInit("b0".asUInt(3.W))

    when(rd_pout_data_en){
        for(i <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
            pout_mem_data(i) := int_pout_mem_data(i*(conf.NVDLA_PDP_BWPE+6)+conf.NVDLA_PDP_BWPE+5, i*(conf.NVDLA_PDP_BWPE+6))
        }
        pout_mem_size_v := int_pout_mem_data(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_PDP_BWPE+6)+2, conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_PDP_BWPE+6))
    }

    //===========================================================
    //adding pad value in v direction
    //-----------------------------------------------------------
    val u_pad_v_and_div_kernel = Module(new NV_NVDLA_PDP_CORE_CAL2D_pad_v_and_div_kernel)
    u_pad_v_and_div_kernel.io.nvdla_core_clk := io.nvdla_core_clk
    u_pad_v_and_div_kernel.io.pout_mem_data := pout_mem_data
    u_pad_v_and_div_kernel.io.pout_mem_size_v := pout_mem_size_v
    u_pad_v_and_div_kernel.io.pooling_size_v_cfg := io.pooling_size_v_cfg
    u_pad_v_and_div_kernel.io.average_pooling_en := average_pooling_en
    u_pad_v_and_div_kernel.io.reg2dp_kernel_width := io.reg2dp_kernel_width
    u_pad_v_and_div_kernel.io.rd_pout_data_stage0 := rd_pout_data_stage0
    u_pad_v_and_div_kernel.io.rd_pout_data_stage1 := rd_pout_data_stage1
    u_pad_v_and_div_kernel.io.reg2dp_recip_width_cfg := io.reg2dp_recip_width_cfg
    u_pad_v_and_div_kernel.io.reg2dp_recip_height_cfg := io.reg2dp_recip_height_cfg
    u_pad_v_and_div_kernel.io.reg2dp_pad_value_1x_cfg := io.reg2dp_pad_value_1x_cfg
    u_pad_v_and_div_kernel.io.reg2dp_pad_value_2x_cfg := io.reg2dp_pad_value_2x_cfg
    u_pad_v_and_div_kernel.io.reg2dp_pad_value_3x_cfg := io.reg2dp_pad_value_3x_cfg
    u_pad_v_and_div_kernel.io.reg2dp_pad_value_4x_cfg := io.reg2dp_pad_value_4x_cfg
    u_pad_v_and_div_kernel.io.reg2dp_pad_value_5x_cfg := io.reg2dp_pad_value_5x_cfg
    u_pad_v_and_div_kernel.io.reg2dp_pad_value_6x_cfg := io.reg2dp_pad_value_6x_cfg
    u_pad_v_and_div_kernel.io.reg2dp_pad_value_7x_cfg := io.reg2dp_pad_value_7x_cfg
    val pout_data = u_pad_v_and_div_kernel.io.pout_data

    //=============================

    //======================================
    //interface between POOLING data and DMA
    io.pdp_dp2wdma_pd.valid := pout_data_stage3_vld & rd_pout_data_en_4d
    pout_data_stage3_prdy := io.pdp_dp2wdma_pd.ready
    io.pdp_dp2wdma_pd.bits := pout_data.asUInt

}}


object NV_NVDLA_PDP_CORE_cal2dDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_cal2d())
}