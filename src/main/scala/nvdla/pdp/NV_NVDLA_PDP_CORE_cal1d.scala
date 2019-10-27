package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_CORE_cal1d(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        //pdp_rdma2dp
        val pdp_rdma2dp_pd = Flipped(DecoupledIO(UInt((conf.PDPBW+14).W)))
        //sdp2pdp
        val sdp2pdp_pd = Flipped(DecoupledIO(UInt((conf.PDPBW+14).W)))
        //pooling
        val pooling1d_pd = DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)).W))

        //config 
        val pooling_channel_cfg = Input(UInt(13.W))
        val pooling_fwidth_cfg = Input(UInt(10.W))
        val pooling_lwidth_cfg = Input(UInt(10.W))
        val pooling_mwidth_cfg = Input(UInt(10.W))
        val pooling_out_fwidth_cfg = Input(UInt(10.W))
        val pooling_out_lwidth_cfg = Input(UInt(10.W))
        val pooling_out_mwidth_cfg = Input(UInt(10.W))
        val pooling_size_h_cfg = Input(UInt(3.W))
        val pooling_splitw_num_cfg = Input(UInt(8.W))
        val pooling_stride_h_cfg = Input(UInt(4.W))
        val pooling_type_cfg = Input(UInt(2.W))
        val reg2dp_cube_in_height = Input(UInt(13.W))
        val reg2dp_cube_in_width = Input(UInt(13.W))
        val reg2dp_cube_out_width = Input(UInt(13.W))
        val reg2dp_kernel_stride_width = Input(UInt(4.W))
        val reg2dp_kernel_width = Input(UInt(2.W))
        val reg2dp_op_en = Input(Bool())
        val reg2dp_pad_left = Input(UInt(3.W))
        val reg2dp_pad_right = Input(UInt(3.W))
        val reg2dp_pad_right_cfg = Input(UInt(3.W))
        val reg2dp_pad_value_1x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_2x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_3x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_4x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_5x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_6x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_7x_cfg = Input(UInt(19.W))
        val dp2reg_done = Input(Bool())
        val datin_src_cfg = Input(Bool())
        val padding_h_cfg = Input(UInt(3.W))
        val pdp_op_start = Output(Bool())
    })
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
    //PDP start
    //
    //--------------------------------------------------------------
    val pdp_op_pending = RegInit(false.B)
    io.pdp_op_start := ~pdp_op_pending & io.reg2dp_op_en;
    when(io.pdp_op_start){
        pdp_op_pending := true.B
    }
    .elsewhen(io.dp2reg_done){
        pdp_op_pending := false.B
    }

    //==============================================================
    //input data source select
    //--------------------------------------------------------------
    val pdp_datin_prdy_f = Wire(Bool())
    val off_flying_en = (io.datin_src_cfg === 1.U)
    val on_flying_en = (io.datin_src_cfg === 0.U)
    val pdp_datin_pd_f_mux0 = Mux(off_flying_en, io.pdp_rdma2dp_pd.bits, io.sdp2pdp_pd.bits)
    val pdp_datin_pvld_mux0 = Mux(off_flying_en, io.pdp_rdma2dp_pd.valid, io.sdp2pdp_pd.valid)
    val pdp_datin_prdy_mux0 = pdp_datin_prdy_f
    io.pdp_rdma2dp_pd.ready := pdp_datin_prdy_mux0 & off_flying_en
    io.sdp2pdp_pd.ready := pdp_datin_prdy_mux0 & on_flying_en
    
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    //data select after switch
    val pdp_datin_pd_f_0 = pdp_datin_pd_f_mux0
    val pdp_datin_pvld_f = pdp_datin_pvld_mux0

    //===============================================================
    // 1 cycle pipeline for DW timing closure inside unit1d sub modudle
    // DW has replaced by normal hls fp17 adder, this pipeline keep here
    //---------------------------------------------------------------conf.NVDLA_MEMORY_ATOMIC_SIZE
    val posc_last = pdp_datin_pd_f_0(conf.PDPBW+8, conf.PDPBW+4) === conf.ENUM.U
    val pdp_din = Wire(Vec(conf.NVDLA_PDP_THROUGHPUT, UInt((conf.NVDLA_BPE+3).W)))
    for(i <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
        pdp_din(i) := Cat(Fill(3, pdp_datin_pd_f_0(conf.PDPBW+conf.NVDLA_BPE-1)), 
                     pdp_datin_pd_f_0(conf.PDPBW+conf.NVDLA_BPE-1, conf.PDPBW))
    }

    val datain_ext = pdp_din.asUInt

    val pdp_datin_prdy = Wire(Bool())
    val pdp_datin_pd_f0 = Cat(posc_last, pdp_datin_pd_f_0(conf.PDPBW+13, conf.PDPBW), datain_ext)
    val pipe_0 = Module{new NV_NVDLA_IS_pipe(conf.PDP_UNIT1D_BW + 15)}
    pipe_0.io.clk := io.nvdla_core_clk
    pipe_0.io.vi := pdp_datin_pvld_f
    val pdp_datin_prdy_f0 = pipe_0.io.ro
    pipe_0.io.di := pdp_datin_pd_f0
    val pdp_datin_pvld0 = pipe_0.io.vo
    pipe_0.io.ri := pdp_datin_prdy
    val pdp_datin_pd0 = pipe_0.io.dout

    pdp_datin_prdy_f := pdp_datin_prdy_f0
    val pdp_datin_pvld = pdp_datin_pvld0
    val pdp_datin_pd = pdp_datin_pd0

    val cur_datin_disable = RegInit(false.B)
    val pdpw_active_en = RegInit(false.B)
    val pdp_datin_prdy_0 = ~ cur_datin_disable
    val pdp_datin_prdy_1 = Wire(Bool())
    pdp_datin_prdy := (pdp_datin_prdy_0 & pdp_datin_prdy_1) & pdpw_active_en; 

    //==============================================================
    //new splitw
    //---------------------------------------------------------------
    val load_din = Wire(Bool())
    val splitw_end = Wire(Bool())
    val bsync = pdp_datin_pd(conf.PDP_UNIT1D_BW+9)
    val splitw_end_sync = Mux(load_din, pdp_datin_pd(conf.PDP_UNIT1D_BW+12), false.B)
    val pdp_cube_sync = pdp_datin_pd(conf.PDP_UNIT1D_BW+13)
    val pdp_cube_end = pdp_cube_sync & bsync & load_din
    val splitw_cnt = RegInit("b0".asUInt(8.W))

    when(splitw_end & bsync & splitw_end_sync & load_din){
        splitw_cnt := 0.U
    }
    .elsewhen(splitw_end_sync & bsync & load_din){
        splitw_cnt := splitw_cnt + 1.U
    }

    splitw_end := (splitw_cnt === io.pooling_splitw_num_cfg)
    val splitw_start = (splitw_cnt === 0.U)

    //===============================================================
    //config info
    //
    //---------------------------------------------------------------
    val non_splitw = io.pooling_splitw_num_cfg === 0.U
    val first_splitw_en = ~non_splitw & splitw_start
    val last_splitw_en = ~non_splitw & splitw_end 

    val overlap = Mux(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width, io.reg2dp_kernel_stride_width - io.reg2dp_kernel_width, 
                  io.reg2dp_kernel_width - io.reg2dp_kernel_stride_width)

    val pooling_pwidth = Wire(UInt(13.W))

    when(non_splitw){
        pooling_pwidth := io.reg2dp_cube_in_width
    }
    .elsewhen(splitw_end){
        when(io.reg2dp_kernel_stride_width > io.reg2dp_kernel_width){
            pooling_pwidth := io.pooling_lwidth_cfg -& overlap
        }
        .otherwise{
            pooling_pwidth := io.pooling_lwidth_cfg +& overlap
        }
    }
    .elsewhen(splitw_start){
        pooling_pwidth := io.pooling_fwidth_cfg
    }
    .otherwise{
        when(io.reg2dp_kernel_stride_width > io.reg2dp_kernel_width){
            pooling_pwidth := io.pooling_mwidth_cfg -& overlap
        }
        .otherwise{
            pooling_pwidth := io.pooling_mwidth_cfg +& overlap
        }
    }

    //==============================================================
    //enable pdp datapath
    //--------------------------------------------------------------
    when(io.pdp_op_start){
        pdpw_active_en := true.B
    }
    .elsewhen(pdp_cube_end){
        pdpw_active_en := false.B
    }

    //==============================================================
    //stride count in padding bits
    //
    //--------------------------------------------------------------
    val padding_left = Wire(UInt(3.W))

    when(non_splitw){
        padding_left := io.padding_h_cfg
    }
    .elsewhen(first_splitw_en & (~splitw_end_sync)){
        padding_left := io.padding_h_cfg
    }
    .otherwise{
        padding_left := 0.U
    }

    //stride ==1 
    val padding_stride1_num = padding_left
    //stride ==2
    val padding_stride2_num = padding_left(2, 1)
    //stride ==3
    val padding_stride3_num = Mux(padding_left >= 6.U, 2.U, 
                              Mux(padding_left >= 3.U, 1.U,
                              0.U))
    //stride==4 5 6 7
    val pooling_stride_h = io.pooling_stride_h_cfg +& 1.U
    val padding_stride4_num = Mux(padding_left > io.pooling_stride_h_cfg, 1.U, 0.U)
    //number needed for padding in horizontal direction
    val padding_stride_num = MuxLookup(io.pooling_stride_h_cfg, padding_stride4_num,
                                 Array(0.U -> padding_stride1_num,
                                       1.U -> padding_stride2_num,
                                       2.U -> padding_stride3_num))
    val strip_xcnt_offset = (padding_left - padding_stride_num*pooling_stride_h)(2, 0)

    //////////////////////////////////////////////////
    // line reg use num calculation, "+1"
    //------------------------------------------------
    val regs_num = RegInit(0.U(3.W))
    //stride 1
    val line_regs_1 = io.pooling_size_h_cfg
    //stride 2
    val line_regs_2 = io.pooling_size_h_cfg(2, 1)
    //stride 3
    val line_regs_3 = Mux(io.pooling_size_h_cfg>5.U, 2.U, 
                      Mux(io.pooling_size_h_cfg>2.U, 1.U, 0.U))
    //stride 4 5 6 7
    val line_regs_4 = Mux(io.pooling_size_h_cfg>io.pooling_stride_h_cfg, 1.U, 0.U)

    when(io.pdp_op_start){
        regs_num := MuxLookup(io.pooling_stride_h_cfg, line_regs_4, 
                        Array(0.U -> line_regs_1,
                              1.U -> line_regs_2,
                              2.U -> line_regs_3))
    }

    //////////////////////////////////////////////////
 
    //==============================================================
    //1D pooling stride/size counter
    //
    //-------------------------------------------------------------
    //stride start
    val strip_xcnt_stride = RegInit(0.U(4.U))
    val strip_xcnt_stride_f = Wire(UInt(4.W))
    val line_last_stripe_done = Wire(Bool())

    load_din := pdp_datin_prdy & pdp_datin_pvld
    val pooling_size_h = io.pooling_size_h_cfg +& 1.U
    val pdp_din_lc = pdp_datin_pd(conf.PDP_UNIT1D_BW+14)
    val strip_recieve_done = load_din & pdp_din_lc
    val stride_end = strip_recieve_done & (strip_xcnt_stride === io.pooling_stride_h_cfg)
    val init_cnt = line_last_stripe_done | io.pdp_op_start

    when(init_cnt){
        strip_xcnt_stride_f := strip_xcnt_offset
    }
    .elsewhen(stride_end){
        strip_xcnt_stride_f := 0.U
    }
    .elsewhen(strip_recieve_done){
        strip_xcnt_stride_f := strip_xcnt_stride + 1.U
    }
    .otherwise{
        strip_xcnt_stride_f := strip_xcnt_stride
    }

    when(init_cnt | stride_end | strip_recieve_done === 1.U){
        strip_xcnt_stride := strip_xcnt_stride_f
    }

    //pooling result ready
    val strip_xcnt_psize = RegInit(0.U(3.W))
    val pooling_1d_rdy = Wire(Bool())

    val overlap_ff = (io.pooling_size_h_cfg - io.pooling_stride_h_cfg)(2, 0)

    when(init_cnt){
        strip_xcnt_psize := padding_left
    }
    .elsewhen(io.pooling_size_h_cfg >= io.pooling_stride_h_cfg){
        when(pooling_1d_rdy){
            strip_xcnt_psize := overlap_ff
        }
        .elsewhen(strip_recieve_done){
            strip_xcnt_psize := strip_xcnt_psize + 1.U
        }
    }
    .otherwise{ // pooling_size < stride
        when(strip_xcnt_stride_f <= io.pooling_size_h_cfg){
            strip_xcnt_psize := strip_xcnt_stride_f
        }
        .otherwise{
            strip_xcnt_psize := 0.U
        }
    }

    /////////////////////////////////////////////////////////
    //input data bubble control logic generation
    //-------------------------------------------------------
    val pooling_size = pooling_size_h
    val stride = pooling_stride_h
    val pad_l = padding_left

    val pad_r = io.reg2dp_pad_right_cfg
 
    val flush_num_cal = Wire(UInt(3.W))
    val stride_1x = Wire(UInt(5.W))
    val stride_2x = Wire(UInt(6.W))
    val stride_3x = Wire(UInt(7.W))
    val stride_4x = Wire(UInt(7.W))
    val stride_5x = Wire(UInt(8.W))
    val stride_6x = Wire(UInt(8.W))
    val stride_7x = Wire(UInt(8.W))
    //element/line num need flush at lint/surface end
    when(pad_r < stride_1x){
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
    val non_split_small_active = (non_splitw & (!io.reg2dp_cube_in_width(12, 3).orR) & (io.reg2dp_cube_in_width +& io.reg2dp_pad_left(2, 0) < io.reg2dp_kernel_width))
    val big_stride = io.reg2dp_kernel_stride_width >= io.reg2dp_kernel_width
    val split_small_active = (~non_splitw) & ((big_stride & ((io.pooling_lwidth_cfg -& overlap) < io.reg2dp_kernel_width))
                                         | ((~big_stride) & ((io.pooling_lwidth_cfg +& overlap) < io.reg2dp_kernel_width)))
    val non_split_w_pl = io.reg2dp_cube_in_width +& io.reg2dp_pad_left
    val non_split_w_pl_pr = non_split_w_pl +& io.reg2dp_pad_right
    val split_w_olap = Mux(big_stride, io.pooling_lwidth_cfg -& overlap, io.pooling_lwidth_cfg +& overlap)
    val split_w_olap_pr = split_w_olap +& io.reg2dp_pad_right

    //pad_right remain afrer 1st kernel pooling
    val pad_r_remain = Wire(UInt(8.W))
    when(non_split_small_active){
        pad_r_remain := non_split_w_pl_pr -& io.reg2dp_kernel_width
    } 
    .elsewhen(split_small_active){
        pad_r_remain := split_w_olap_pr -& io.reg2dp_kernel_width
    }
    .otherwise{
        pad_r_remain := 0.U
    }

    //how many need bubble after 1st kernel pooling
    val samllW_flush_num = Wire(UInt(3.W))
    samllW_flush_num := MuxLookup(pad_r_remain, 0.U, 
                        Array(stride_6x -> 6.U,
                              stride_5x -> 5.U,
                              stride_4x -> 4.U,
                              stride_3x -> 3.U,
                              stride_2x -> 2.U,
                              stride_1x -> 1.U))

    val flush_num = Wire(UInt(3.W))

    when(flush_num_cal === 0.U){
        flush_num := 0.U
    }
    .elsewhen(non_split_small_active | split_small_active){
        flush_num := samllW_flush_num
    }
    .otherwise{
        flush_num := flush_num_cal
    }

    stride_1x := stride
    stride_2x := Cat(stride, false.B)
    stride_3x := stride_2x +& stride
    stride_4x := Cat(stride, false.B, false.B)
    stride_5x := stride_4x +& stride 
    stride_6x := stride_3x +& stride_3x
    stride_7x := stride_4x +& stride_3x

    //the 1st element/line num need output data
    val kernel_padl = pooling_size -& pad_l
    val partial_w_last = io.pooling_lwidth_cfg +& 1.U
    val cube_width_in = io.reg2dp_cube_in_width +& 1.U
    val ks_width = io.reg2dp_kernel_stride_width +& 1.U
    val k_add_ks = Wire(UInt(11.W))
    k_add_ks := pooling_size +& ks_width
 
    val first_out_num = Wire(UInt(4.W))
    when(non_splitw){
        when(non_split_small_active){
            first_out_num := cube_width_in
        }
        .otherwise{
            first_out_num := kernel_padl
        }
    }
    .otherwise{
        when(first_splitw_en){
            first_out_num := kernel_padl
        }
        .elsewhen(last_splitw_en & split_small_active){
            when(big_stride){
                first_out_num := partial_w_last
            }
            .otherwise{
                first_out_num := partial_w_last +& overlap
            }
        }
        .otherwise{
            when(big_stride){
                first_out_num := k_add_ks
            }
            .otherwise{
                first_out_num := pooling_size
            }
        }
    }

    val need_bubble = RegInit(false.B)
    val bubble_num = RegInit("b0".asUInt(3.W))
    when(pdp_cube_end){
        when(flush_num.orR){
            need_bubble := true.B
            bubble_num := flush_num
        }
        .otherwise{
            need_bubble := false.B
            bubble_num := 0.U
        }
    }
    .elsewhen(non_splitw){
        when(io.pdp_op_start){
            when(flush_num >= first_out_num){
                need_bubble := true.B
                bubble_num := flush_num -& first_out_num +& 1.U
            }
            .otherwise{
                need_bubble := false.B
                bubble_num := 0.U
            }
        }
    }
    .otherwise{//split mode
        when(splitw_end){
            when(flush_num >= first_out_num){
                need_bubble := true.B
                bubble_num := flush_num -& first_out_num +& 1.U
            }
            .otherwise{
                need_bubble := false.B
                bubble_num := 0.U
            }
        }
    }

    val bubble_en_end = Wire(Bool())

    when(pdp_cube_end & (flush_num.orR)){
        cur_datin_disable := true.B
    }
    .elsewhen(non_splitw){
        when(line_last_stripe_done & need_bubble){
            cur_datin_disable := true.B
        }
        .elsewhen(bubble_en_end){
            cur_datin_disable := false.B
        }
    }
    .otherwise{
        when(last_splitw_en & line_last_stripe_done & need_bubble){
            cur_datin_disable := true.B
        }
        .otherwise{
            cur_datin_disable := false.B
        }
    }

    val last_c = Wire(Bool())
    val channel_cnt = RegInit("b0".asUInt(5.W))
    when(cur_datin_disable){
        when(last_c){
            channel_cnt := 0.U
        }
        .elsewhen(pdp_datin_prdy_1){
            channel_cnt := channel_cnt +& 1.U
        }
    }
    .otherwise{
        channel_cnt := 0.U
    }
    last_c := (channel_cnt === conf.ENUM.U) & pdp_datin_prdy_1

    val bubble_cnt = RegInit("b0".asUInt(3.W)) 
    when(cur_datin_disable){
        when(bubble_en_end){
            bubble_cnt := 0.U
        }
        .elsewhen(last_c){
            bubble_cnt := bubble_cnt +& 1.U
        }
    }
    .otherwise{
        bubble_cnt := 0.U
    }

    val bubble_num_dec = bubble_num - 1.U
    bubble_en_end := (bubble_cnt === bubble_num_dec) & last_c

    //////////////////////////////////////////////////////
    //last line element output en during cur line element comming
    //----------------------------------------------------
    //subcube end flag for last_out_en control in the sub-cube end
    val subcube_end_flag = RegInit(false.B)
    when(splitw_end_sync & bsync){
        subcube_end_flag := true.B
    }
    .elsewhen(load_din){
        subcube_end_flag := false.B
    }

    val last_out_en = RegInit(false.B)
    val last_out_done = Wire(Bool())
    when(last_out_done){
        last_out_en := false.B
    }
    .elsewhen(pdp_cube_end){
        last_out_en := false.B
    }
    .elsewhen((first_out_num =/= 1.U) & (~subcube_end_flag)){
        when(need_bubble){
            when(bubble_en_end){
                last_out_en := true.B
            }   
        }
        .elsewhen(flush_num.orR){
            when(non_splitw & line_last_stripe_done){
                last_out_en := true.B
            }
            .elsewhen(~non_splitw & last_splitw_en & line_last_stripe_done){
                last_out_en := true.B
            }
        }
    }
    .otherwise{
        last_out_en := false.B
    }

    val first_out_num_dec2 = Wire(UInt(3.W))
    first_out_num_dec2 := Mux(need_bubble, first_out_num -& 2.U, flush_num -& 1.U)

    val last_out_cnt = RegInit("b0".asUInt(3.W))
    when(last_out_en){
        when(strip_recieve_done){
            when(last_out_done){
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

    last_out_done := (last_out_cnt === first_out_num_dec2) & strip_recieve_done & last_out_en
    pooling_1d_rdy := (strip_xcnt_psize === io.pooling_size_h_cfg) &  strip_recieve_done

    /////////////////////////////////////////////////////////

    //strip count in total width
    val strip_cnt_total = RegInit("b0".asUInt(13.W))
    when(init_cnt){
        strip_cnt_total := 0.U
    }
    .elsewhen(strip_recieve_done){
        strip_cnt_total := strip_cnt_total +& 1.U
    }

    val strip_width_end  = (strip_cnt_total === pooling_pwidth)
    line_last_stripe_done := (strip_width_end & strip_recieve_done)

    //-----------------------
    //flag the last one pooling in width direction
    //-----------------------
    val rest_width = pooling_pwidth - strip_cnt_total
    val rest_width_use = Mux(non_splitw | splitw_end, rest_width +& io.reg2dp_pad_right_cfg, rest_width)
    val last_pooling_flag = rest_width_use <= io.pooling_size_h_cfg

    //======================================================================
    //pooling 1D unit counter
    //
    //----------------------------------------------------------------------
    val unit1d_cnt_stride = RegInit(0.U(3.W))
    when(init_cnt){
        unit1d_cnt_stride := padding_stride_num
    }
    .elsewhen(stride_end){
        when(unit1d_cnt_stride === regs_num){
            unit1d_cnt_stride := 0.U
        }
        .otherwise{
            unit1d_cnt_stride := unit1d_cnt_stride + 1.U
        }
    }

    val line_ldata_valid = Wire(Bool())
    val unit1d_cnt_pooling = RegInit(0.U(3.W))
    when(init_cnt){
        unit1d_cnt_pooling := 0.U
    }
    .elsewhen(pooling_1d_rdy | line_ldata_valid){
        when(unit1d_cnt_pooling === regs_num){
            unit1d_cnt_pooling := 0.U
        }
        .otherwise{
            unit1d_cnt_pooling := unit1d_cnt_pooling + 1.U
        }
    }
    line_ldata_valid := line_last_stripe_done

    val init_unit1d_set = Wire(Vec(8, Bool()))
    val unit1d_set_trig = Wire(Vec(8, Bool()))
    val unit1d_set = Wire(Vec(8, Bool()))
    val unit1d_clr = Wire(Vec(8, Bool()))
    val pout_width_cur = Wire(UInt(13.W))
    val unit1d_en = RegInit(VecInit(Seq.fill(8)(false.B)))

    for(i <- 0 to 7){
        init_unit1d_set(i) := init_cnt & (padding_stride_num >= i.U) & (pout_width_cur >= i.U)
        if(i == 0){
            unit1d_set_trig(0) := stride_end & (unit1d_cnt_stride === regs_num) & (~last_pooling_flag)
        }
        else{
            unit1d_set_trig(i) := stride_end & (unit1d_cnt_stride === (i-1).U) & (unit1d_cnt_stride =/= regs_num) & (~last_pooling_flag)
        }
        unit1d_set(i) := unit1d_set_trig(i) | init_unit1d_set(i)
        unit1d_clr(i) := (pooling_1d_rdy & (unit1d_cnt_pooling === i.U)) | line_ldata_valid

        when(pdp_cube_end){
            unit1d_en(i) := false.B
        }
        .elsewhen(unit1d_set(i)){
            unit1d_en(i) := true.B
        }
        .elsewhen(unit1d_clr(i)){
            unit1d_en(i) := false.B
        }
    }

    val pooling_din_1st = RegInit(VecInit(Seq.fill(8)(false.B)))
    for(i <- 0 to 7){
        when(unit1d_set(i)){
            pooling_din_1st(i) := true.B
        }
        .elsewhen(strip_recieve_done){
            pooling_din_1st(i) := false.B
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    val unit1d_prdy = Wire(Vec(8, Bool()))
    val unit1d_prdy_uint = unit1d_prdy.asUInt
    val unit1d_pvld = Wire(Vec(8, Bool()))
    val pdp_info_in_prdy = Wire(Bool())
    val datin_buf = pdp_datin_pd(conf.PDP_UNIT1D_BW+10, 0)
    pdp_datin_prdy_1 := unit1d_prdy_uint.andR & pdp_info_in_prdy
    val pdp_full_pvld = pdp_datin_pvld | cur_datin_disable 

    unit1d_pvld(0) := pdp_full_pvld & pdp_info_in_prdy & (unit1d_prdy_uint(7,1).andR)
    unit1d_pvld(1) := pdp_full_pvld & pdp_info_in_prdy & (Cat(unit1d_prdy_uint(7,2), unit1d_prdy_uint(0)).andR)
    unit1d_pvld(2) := pdp_full_pvld & pdp_info_in_prdy & (Cat(unit1d_prdy_uint(7,3), unit1d_prdy_uint(1, 0)).andR)
    unit1d_pvld(3) := pdp_full_pvld & pdp_info_in_prdy & (Cat(unit1d_prdy_uint(7,4), unit1d_prdy_uint(2, 0)).andR)
    unit1d_pvld(4) := pdp_full_pvld & pdp_info_in_prdy & (Cat(unit1d_prdy_uint(7,5), unit1d_prdy_uint(3, 0)).andR)
    unit1d_pvld(5) := pdp_full_pvld & pdp_info_in_prdy & (Cat(unit1d_prdy_uint(7,6), unit1d_prdy_uint(4, 0)).andR)
    unit1d_pvld(6) := pdp_full_pvld & pdp_info_in_prdy & (Cat(unit1d_prdy_uint(7), unit1d_prdy_uint(5,0)).andR)
    unit1d_pvld(7) := pdp_full_pvld & pdp_info_in_prdy & (unit1d_prdy_uint(6,0).andR)

    //============================================================
    //pdp info pipe
    val pooling_din_last = Wire(Vec(8, Bool()))
    val pdp_info_in_pvld = pdp_full_pvld & unit1d_prdy_uint.andR
    val pdp_info_out_prdy = Wire(Bool())

    val pdp_info_in_pd = Cat(pdp_din_lc,last_c,last_out_en,cur_datin_disable,pooling_din_last.asUInt)
    val u_NV_NVDLA_PDP_cal1d_info_fifo = Module(new NV_NVDLA_fifo(depth=8, width=12, ram_type=0))
    u_NV_NVDLA_PDP_cal1d_info_fifo.io.clk := io.nvdla_core_clk
    u_NV_NVDLA_PDP_cal1d_info_fifo.io.wr_pvld := pdp_info_in_pvld
    pdp_info_in_prdy := u_NV_NVDLA_PDP_cal1d_info_fifo.io.wr_prdy
    u_NV_NVDLA_PDP_cal1d_info_fifo.io.wr_pd := pdp_info_in_pd
    val pdp_info_out_pvld = u_NV_NVDLA_PDP_cal1d_info_fifo.io.rd_pvld 
    u_NV_NVDLA_PDP_cal1d_info_fifo.io.rd_prdy := pdp_info_out_prdy
    val pdp_info_out_pd = u_NV_NVDLA_PDP_cal1d_info_fifo.io.rd_pd
    u_NV_NVDLA_PDP_cal1d_info_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    val unit1d_out_prdy_use = Wire(Bool())
    val unit1d_out_pvld = Wire(Vec(8, Bool()))
    pdp_info_out_prdy := unit1d_out_prdy_use & (unit1d_out_pvld.asUInt.andR)
    val pooling_din_last_sync = pdp_info_out_pd(7, 0)
    val cur_datin_disable_sync = pdp_info_out_pd(8)
    val last_out_en_sync = pdp_info_out_pd(9)
    val last_c_sync = pdp_info_out_pd(10)
    val pdp_din_lc_sync = pdp_info_out_pd(11)

    //============================================================
    // &Instance
    //
    //------------------------------------------------------------
    //assertion trace NVDLA_HLS_ADD17_LATENCY latency change from 4
    val unit1d = Array.fill(8){Module(new NV_NVDLA_PDP_CORE_unit1d)}
    val average_pooling_en = Wire(Bool())
    val unit1d_out = Wire(Vec(8, UInt((conf.PDP_UNIT1D_BW+4).W)))
    
    val unit1d_out_prdy = Wire(Vec(8, Bool()))
    for(i <- 0 to 7){
        pooling_din_last(i) := unit1d_en(i) & (((strip_xcnt_psize === io.pooling_size_h_cfg) & (unit1d_cnt_pooling === i.U)) | strip_width_end) 

        unit1d(i).io.nvdla_core_clk := io.nvdla_core_clk

        unit1d(i).io.pdma2pdp_pd.valid := unit1d_pvld(i)
        unit1d_prdy(i) := unit1d(i).io.pdma2pdp_pd.ready
        unit1d(i).io.pdma2pdp_pd.bits := datin_buf(conf.PDP_UNIT1D_BW+8, 0)

        unit1d_out_pvld(i) := unit1d(i).io.pooling_out.valid
        unit1d(i).io.pooling_out.ready := unit1d_out_prdy(i)
        unit1d_out(i) := unit1d(i).io.pooling_out.bits

        unit1d(i).io.average_pooling_en := average_pooling_en
        unit1d(i).io.cur_datin_disable := cur_datin_disable
        unit1d(i).io.last_out_en := (last_out_en_sync | cur_datin_disable_sync)
        unit1d(i).io.pdp_din_lc_f := pdp_din_lc
        unit1d(i).io.pooling_din_1st := pooling_din_1st(i)
        unit1d(i).io.pooling_din_last := pooling_din_last(i)
        unit1d(i).io.pooling_type_cfg := io.pooling_type_cfg
        unit1d(i).io.pooling_unit_en := unit1d_en(i)
    }

    val unit1d_out_pvld_uint = unit1d_out_pvld.asUInt

    //////////////////////////////////////////////////////////////////////////////////////
    unit1d_out_prdy(0) := unit1d_out_prdy_use & pdp_info_out_pvld & (unit1d_out_pvld_uint(7,1).andR)
    unit1d_out_prdy(1) := unit1d_out_prdy_use & pdp_info_out_pvld & (Cat(unit1d_out_pvld_uint(7,2), unit1d_out_pvld_uint(0)).andR)
    unit1d_out_prdy(2) := unit1d_out_prdy_use & pdp_info_out_pvld & (Cat(unit1d_out_pvld_uint(7,3), unit1d_out_pvld_uint(1, 0)).andR)
    unit1d_out_prdy(3) := unit1d_out_prdy_use & pdp_info_out_pvld & (Cat(unit1d_out_pvld_uint(7,4), unit1d_out_pvld_uint(2, 0)).andR)
    unit1d_out_prdy(4) := unit1d_out_prdy_use & pdp_info_out_pvld & (Cat(unit1d_out_pvld_uint(7,5), unit1d_out_pvld_uint(3, 0)).andR)
    unit1d_out_prdy(5) := unit1d_out_prdy_use & pdp_info_out_pvld & (Cat(unit1d_out_pvld_uint(7,6), unit1d_out_pvld_uint(4, 0)).andR)
    unit1d_out_prdy(6) := unit1d_out_prdy_use & pdp_info_out_pvld & (Cat(unit1d_out_pvld_uint(7), unit1d_out_pvld_uint(5,0)).andR)
    unit1d_out_prdy(7) := unit1d_out_prdy_use & pdp_info_out_pvld & (unit1d_out_pvld_uint(6,0).andR)

    val unit1d_out_pvld_use = unit1d_out_pvld_uint.andR & pdp_info_out_pvld

    //=========================================================
    //1d pooling output
    //
    //---------------------------------------------------------
    //unit1d count
    val pooling1d_out_v_norm = ((pooling_din_last_sync.orR) & pdp_din_lc_sync & (~cur_datin_disable_sync) & unit1d_out_pvld_use & unit1d_out_prdy_use)
    val pooling1d_out_v_disable = (cur_datin_disable_sync & last_c_sync) & unit1d_out_pvld_use & unit1d_out_prdy_use;
    val pooling1d_out_v_lastout = (last_out_en_sync & pdp_din_lc_sync & unit1d_out_pvld_use & unit1d_out_prdy_use);
    val pooling1d_out_v = pooling1d_out_v_norm | pooling1d_out_v_disable | pooling1d_out_v_lastout;

    //////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    //end of line
    val wr_line_dat_cnt = RegInit("b0".asUInt(13.W))
    val wr_line_dat_done = Wire(Bool())
    
    when(wr_line_dat_done){
        wr_line_dat_cnt := 0.U
    }
    .elsewhen(pooling1d_out_v){
        wr_line_dat_cnt := wr_line_dat_cnt + 1.U
    }

    wr_line_dat_done := (wr_line_dat_cnt===pout_width_cur) & pooling1d_out_v;

    //end of surface
    val wr_surface_dat_cnt = RegInit("b0".asUInt(13.W))
    val wr_surface_dat_done = Wire(Bool())
    when(wr_surface_dat_done){
        wr_surface_dat_cnt := 0.U
    }
    .elsewhen(wr_line_dat_done){
        wr_surface_dat_cnt := wr_surface_dat_cnt + 1.U
    }

    val last_line_in = wr_surface_dat_cnt === io.reg2dp_cube_in_height
    wr_surface_dat_done := wr_line_dat_done & last_line_in

    //end of splitw
    val cube_out_channel = io.pooling_channel_cfg
    val surface_num = cube_out_channel(12, log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE))

    val wr_subcube_dat_done = Wire(Bool())
    val surface_cnt_rd = RegInit(0.U((13-log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)).W))
    when(wr_subcube_dat_done){
        surface_cnt_rd := 0.U
    }
    .elsewhen(wr_surface_dat_done){
        surface_cnt_rd := surface_cnt_rd + 1.U
    }
    wr_subcube_dat_done := (surface_num === surface_cnt_rd) & wr_surface_dat_done

    //total cube done
    val wr_total_cube_done = Wire(Bool())
    val wr_splitc_cnt = RegInit("b0".asUInt(8.W))
    when(wr_total_cube_done){
        wr_splitc_cnt := 0.U
    }
    .elsewhen(wr_subcube_dat_done){
        wr_splitc_cnt := wr_splitc_cnt + 1.U
    }

    wr_total_cube_done := (wr_splitc_cnt === io.pooling_splitw_num_cfg) & wr_subcube_dat_done;

    //-------------------------------------------------
    //split width selection
    val splitw_enable = io.pooling_splitw_num_cfg =/= 0.U
    val last_splitw = (wr_splitc_cnt === io.pooling_splitw_num_cfg) & splitw_enable
    val first_splitw = (wr_splitc_cnt === 0.U) & splitw_enable

    pout_width_cur := Mux(~splitw_enable, io.reg2dp_cube_out_width, 
                         Mux(last_splitw, io.pooling_out_lwidth_cfg, 
                         Mux(first_splitw, io.pooling_out_fwidth_cfg, 
                         io.pooling_out_mwidth_cfg)))

    //////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    val pooling_out_cnt = RegInit("b0".asUInt(3.W))

    when(io.pdp_op_start){
        pooling_out_cnt := 0.U
    }
    .elsewhen(pooling1d_out_v){
        when((pooling_out_cnt === regs_num) |wr_line_dat_done){
            pooling_out_cnt := 0.U
        }
        .otherwise{
            pooling_out_cnt := pooling_out_cnt + 1.U
        }
    }

    val unit1d_actv_out_f = MuxLookup(pooling_out_cnt, 0.U, 
                              Array(0.U -> unit1d_out(0),
                                    1.U -> unit1d_out(1),
                                    2.U -> unit1d_out(2),
                                    3.U -> unit1d_out(3),
                                    4.U -> unit1d_out(4),
                                    5.U -> unit1d_out(5),
                                    6.U -> unit1d_out(6),
                                    7.U -> unit1d_out(7)))

    val unit1d_actv_out_prdy = Wire(Bool())
    unit1d_out_prdy_use := unit1d_actv_out_prdy;
    val unit1d_actv_out_pvld = unit1d_out_pvld_use & ((pooling_din_last_sync.orR) | cur_datin_disable_sync | last_out_en_sync);
    val unit1d_actv_out = unit1d_actv_out_f

    //=================================
    //padding value in h direction under average mode
    //
    //----------------------------------
    val pad_table_index = Wire(Bool())
    val pad_table_out = MuxLookup(pad_table_index, 0.U,
                            Array(1.U -> io.reg2dp_pad_value_1x_cfg,
                                  2.U -> io.reg2dp_pad_value_2x_cfg,
                                  3.U -> io.reg2dp_pad_value_3x_cfg,
                                  4.U -> io.reg2dp_pad_value_4x_cfg,
                                  5.U -> io.reg2dp_pad_value_5x_cfg,
                                  6.U -> io.reg2dp_pad_value_6x_cfg,
                                  7.U -> io.reg2dp_pad_value_7x_cfg))

    val loading_en = unit1d_actv_out_pvld & unit1d_actv_out_prdy

    val unit1d_actv_data_8bit = Wire(Vec(conf.NVDLA_PDP_THROUGHPUT, UInt((conf.NVDLA_BPE+3).W)))
    val unit1d_actv_data_8bit_ff = Wire(Vec(conf.NVDLA_PDP_THROUGHPUT, UInt((conf.NVDLA_BPE+3).W)))
    val unit1d_actv_data_8bit_with_mon = Wire(Vec(conf.NVDLA_PDP_THROUGHPUT, UInt((conf.NVDLA_BPE+5).W)))
    val unit1d_actv_data_8bit_ff_with_mon = Wire(Vec(conf.NVDLA_PDP_THROUGHPUT, UInt((conf.NVDLA_BPE+5).W)))

    val padding_here_int8 = Wire(Bool())
    for(i <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
        unit1d_actv_data_8bit_ff_with_mon(i) := 
        (Cat(unit1d_actv_out((conf.NVDLA_BPE+3)*i+(conf.NVDLA_BPE+3)-1), unit1d_actv_out((conf.NVDLA_BPE+3)*i+(conf.NVDLA_BPE+3)-1, (conf.NVDLA_BPE+3)*i)).asSInt 
        +& Cat(pad_table_out(10), pad_table_out(10, 0)).asSInt).asUInt

        unit1d_actv_data_8bit_with_mon(i) := 
        Mux(padding_here_int8, unit1d_actv_data_8bit_ff_with_mon(i), unit1d_actv_out((conf.NVDLA_BPE+3)*i+(conf.NVDLA_BPE+3)-1, (conf.NVDLA_BPE+3)*i))

        unit1d_actv_data_8bit_ff(i) := unit1d_actv_data_8bit_ff_with_mon(i)(conf.NVDLA_BPE+2, 0)
        unit1d_actv_data_8bit(i) := unit1d_actv_data_8bit_with_mon(i)(conf.NVDLA_BPE+2, 0)
    }

    val padding_here = (io.pooling_type_cfg === 0.U) & (unit1d_actv_out((conf.NVDLA_PDP_THROUGHPUT)*(conf.NVDLA_BPE+3)+2, (conf.NVDLA_PDP_THROUGHPUT)*(conf.NVDLA_BPE+3)) =/= io.pooling_size_h_cfg)
    padding_here_int8 := padding_here
    pad_table_index := io.pooling_size_h_cfg - unit1d_actv_out((conf.NVDLA_PDP_THROUGHPUT)*(conf.NVDLA_BPE+3)+2, (conf.NVDLA_PDP_THROUGHPUT)*(conf.NVDLA_BPE+3))

    val pooling1d_data_pad = RegInit("b0".asUInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)).W))
    when(loading_en){
        pooling1d_data_pad := VecInit((0 to conf.NVDLA_PDP_THROUGHPUT-1) 
                               map {i => Cat(Fill(3, unit1d_actv_data_8bit(i)(conf.NVDLA_BPE+2)), unit1d_actv_data_8bit(i)(conf.NVDLA_BPE+2, 0))}).asUInt
    
    }
    val pooling1d_data_pad_vld = RegInit(false.B)
    val pooling1d_data_pad_rdy = Wire(Bool())
    when(unit1d_actv_out_pvld){
        pooling1d_data_pad_vld := true.B 
    }   
    .elsewhen(pooling1d_data_pad_rdy){
        pooling1d_data_pad_vld := false.B
    }

    unit1d_actv_out_prdy := (~pooling1d_data_pad_vld | pooling1d_data_pad_rdy)

    //=================================
    //pad_value logic for fp16 average pooling
    //----------------------------------
    average_pooling_en := (io.pooling_type_cfg === 0.U)

    /////////////////////////////////////////////////////////////////////////////////////
    //=================================
    //pooling output 
    //
    //----------------------------------

    io.pooling1d_pd.bits := pooling1d_data_pad
    io.pooling1d_pd.valid := pooling1d_data_pad_vld
    pooling1d_data_pad_rdy := io.pooling1d_pd.ready

}}


object NV_NVDLA_PDP_CORE_cal1dDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_cal1d())
}