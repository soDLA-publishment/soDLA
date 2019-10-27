// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_CORE_cal2d(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         //pdp_dp2wdma
//         val pdp_dp2wdma_pd = DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_BPE+14).W))
//         //pooling
//         val pooling1d_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE+6)).W)))

//         //config 
//         val padding_v_cfg = Input(UInt(3.W))
//         val pdp_op_start = Intput(Bool())
//         val pooling_channel_cfg = Input(UInt(13.W))
//         val pooling_out_fwidth_cfg = Input(UInt(10.W))
//         val pooling_out_lwidth_cfg = Input(UInt(10.W))
//         val pooling_out_mwidth_cfg = Input(UInt(10.W))
//         val pooling_size_v_cfg = Input(UInt(3.W))
//         val pooling_splitw_num_cfg = Input(UInt(8.W))
//         val pooling_stride_v_cfg = Input(UInt(4.W))
//         val pooling_type_cfg = Input(UInt(2.W))
//         val reg2dp_cube_in_height = Input(UInt(13.W))
//         val reg2dp_cube_out_width = Input(UInt(13.W))
//         val reg2dp_kernel_height = Input(UInt(3.W))
//         val reg2dp_kernel_width = Input(UInt(3.W))
//         val reg2dp_pad_bottom_cfg = Input(UInt(3.W))
//         val reg2dp_pad_top = Input(UInt(3.W))
//         val reg2dp_pad_value_1x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_2x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_3x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_4x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_5x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_6x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_7x_cfg = Input(UInt(19.W))
//         val reg2dp_partial_width_out_first = Input(UInt(10.W))
//         val reg2dp_partial_width_out_last = Input(UInt(10.W))
//         val reg2dp_partial_width_out_mid = Input(UInt(10.W))
//         val reg2dp_recip_height_cfg = Input(UInt(17.W))
//         val reg2dp_recip_width_cfg = Input(UInt(17.W))
    
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
//     //bank depth follows rule of 16 elements in width in worst case
//     //it's 64 in t194
//     //--------------------------------------------------------------
//     val bank_depth = (NVDLA_MEMORY_ATOMIC_SIZE/NVDLA_PDP_THROUGHPUT*16-1).U

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
//     data_c_end := c_cnt === conf.ENUM.U
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

//     val surface_cnt_rd = RegInit("b0".asUInt((13-conf.ATMMBW).W))
//     when(wr_subcube_dat_done){
//         surface_cnt_rd := 0.U
//     }
//     .elsewhen(wr_surface_dat_done){
//         surface_cnt_rd := surface_cnt_rd + 1.U
//     }

//     wr_subcube_dat_done := (surface_num === surface_cnt_rd) & wr_surface_dat_done

//     //total cube done
//     val wr_total_cube_done = Wire(Bool())
//     val wr_splitc_cnt = RegInit("b0".asUInt(8.W))
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
//     val pad_l = padding_v_cfg
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
//     val bubble_add = RegInit(0.U)
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
//     when(io.pdp_op_start){
//         when(flush_num >= first_out_num){
//             bubble_num := flush_num -& first_out_num +& 1.U
//         }
//         .otherwise{
//             bubble_num := 0.U
//         }
//     }

//     val flush_in_next_surf = flush_num - bubble_num
//     ///////////////
//     val pnum_flush = Reg(Vec(7, UInt(3.W)))
//     val next_pnum = Wire(Vec((0 to 7) map { i => Vec(i, UInt(3.W))}))
//     //set next_pnum(0)(x) and  next_pnum(1)(x) to zero
//     for(i <- 0 to 1){
//         for(j <- 0 to i){
//             next_pnum(i)(j) := 0.U
//         }  
//     }
//     //begin
//     for(i <- 2 to 7){
//         when(flush_in_next_surf === i.U){
//             for(j <- 0 to i-1){
//                 next_pnum(i)(j) := pnum_flush(7-i+j)
//             }
//             for(k <- 0 to (6-i)){
//                 when(bubble_num === k.U){
//                     for(j <- 0 to i-1){
//                         next_pnum(i)(j) := pnum_flush(k+j)
//                     }      
//                 }        
//             }

//         }
//         .otherwise{
//             for(j <- 0 to i+2-1){
//                 next_pnum(i)(j) := 0.U
//             }
//         }
//     }

//     val bubble_add = RegInit("b0".asUInt(3.W))
//     val up_pnum = Wire(UInt(3.W)) +: Reg(Bool()) +: Reg(UInt(2.W)) +: Reg(UInt(2.W)) +: Reg(UInt(3.W)) +: Reg(UInt(3.W))

//     for(i <- 2 to 7){
//         when(flush_in_next_surf === i.U){

//         }

//     }

    

// }}


// object NV_NVDLA_PDP_CORE_cal2dDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_cal2d())
// }