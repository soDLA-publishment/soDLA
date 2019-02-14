// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CSC_SG_dat_fifo(implicit val conf: cscConfiguration) extends Module {
//     val io = IO(new Bundle {

//         // reg2dp
//         val reg2dp_op_en = Input(UInt(1.W))
//         val reg2dp_conv_mode = Input(UInt(1.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_data_reuse = Input(UInt(1.W))
//         val reg2dp_skip_data_rls = Input(UInt(1.W))
//         val reg2dp_weight_reuse = Input(UInt(1.W)) 
//         val reg2dp_skip_weight_rls = Input(UInt(1.W)) 
//         val reg2dp_batches = Input(UInt(5.W)) 
//         val reg2dp_datain_format = Input(UInt(1.W)) 
//         val reg2dp_datain_height_ext = Input(UInt(13.W)) 
//         val reg2dp_y_extension = Input(UInt(2.W)) 
//         val reg2dp_weight_width_ext = Input(UInt(5.W)) 
//         val reg2dp_weight_height_ext = Input(UInt(5.W)) 
//         val reg2dp_weight_channel_ext = Input(UInt(13.W)) 
//         val reg2dp_weight_kernel = Input(UInt(13.W)) 
//         val reg2dp_dataout_width = Input(UInt(13.W)) 
//         val reg2dp_dataout_height = Input(UInt(13.W)) 
//         val reg2dp_data_bank = Input(UInt(5.W)) 
//         val reg2dp_weight_bank = Input(UInt(5.W)) 
//         val reg2dp_atomics = Input(UInt(21.W)) 
//         val reg2dp_rls_slices = Input(UInt(12.W)) 
//         val dp2reg_done = Output(Bool())

//         //cdma dat
//         val cdma2sc_dat_updt = Input(Bool());     
//         val cdma2sc_dat_slices = Input(UInt(14.W))
//         val cdma2sc_dat_entries = Input(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
//         val sc2cdma_dat_pending_req = Output(Bool())   //send sg pending to cdma
//         val cdma2sc_dat_pending_ack = Input(Bool())   //cdma ask sg to clr pending

//         //cdma wt
//         val cdma2sc_wt_updt = Input(Bool())       
//         val cdma2sc_wt_kernels = Input(UInt(14.W))
//         val cdma2sc_wt_entries = Input(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
//         val sc2cdma_wt_pending_req = Output(Bool())   //send wt pending to cdma
//         val cdma2sc_wt_pending_ack = Input(Bool())  //cdma ask sg to clr pending

//         val sc_state = Output(UInt(2.W))

//         //to dl
//         val sg2dl_pvld = Output(Bool())
//         val sg2dl_pd = Output(UInt(31.W))
//         val sg2dl_reuse_rls = Output(Bool())
//         //to wl
//         val sg2wl_pvld = Output(Bool())
//         val sg2wl_pd = Output(UInt(18.W))
//         val sg2wl_reuse_rls = Output(Bool())

//         //cacc to sc
//         val accu2sc_credit_vld = Input(Bool())
//         val accu2sc_credit_size = Input(UInt(3.W))

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
//     ////////////////////////////////////////////////////////////////////////
//     // CSC control FSM                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     val need_pending = Wire(Bool())
//     val pending_done = Wire(Bool())
//     val layer_done = RegInit(false.B)
//     val fifo_is_clear = Wire(Bool())
//     val pkg_vld = RegInit(false.B)

//     val SG_STATE_IDLE :: SG_STATE_PEND :: SG_STATE_BUSY :: SG_STATE_DONE :: Nil = Enum(4)
//     val cur_state = RegInit(SG_STATE_IDLE)
//     val nxt_state = WireInit(SG_STATE_IDLE)

//     switch (cur_state) {
//         is (SG_STATE_IDLE) {
//         when (io.reg2dp_op_en & need_pending) { nxt_state := SG_STATE_PEND }
//         .elsewhen (io.reg2dp_op_en) { nxt_state := SG_STATE_BUSY }
//         }
//         is (SG_STATE_PEND) {
//         when (pending_done) { nxt_state := SG_STATE_BUSY }
//         }
//         is (SG_STATE_BUSY) {
//         when (layer_done & fifo_is_clear & ~pkg_vld) { nxt_state := SG_STATE_DONE }
//         }
//         is (SG_STATE_DONE) {
//         when (io.dp2reg_done) { nxt_state := SG_STATE_IDLE }
//         }
//     }
//     cur_state := nxt_state

//     ////////////////////////////////////////////////////////////////////////
//     //  FSM input signals                                                 //
//     ////////////////////////////////////////////////////////////////////////
//     val dat_pop_req = Wire(Bool())
//     val dat_push_empty = Wire(Bool())
//     val dat_pop_ready = Wire(Bool())
//     val wt_pop_req = Wire(Bool())
//     val wt_push_empty = Wire(Bool())
//     val last_data_bank = RegInit(Fill(5, true.B))
//     val last_weight_bank = RegInit(Fill(5, true.B)) 
//     val dat_pending_clr = RegInit(false.B)
//     val dat_pending_req = RegInit(false.B)
//     val dat_pending_ack = RegInit(false.B)
//     val wt_pending_clr = RegInit(false.B)
//     val wt_pending_req = RegInit(false.B)
//     val wt_pending_ack = RegInit(false.B)
//     val is_pending = Wire(Bool())
//     val dat_stripe_size = RegInit(Fill(7, false.B))
//     val is_done = Wire(Bool())
//     val is_nxt_done = Wire(Bool())
//     val flush_cycles = RegInit(Fill(7, false.B))
//     val sg_dn_cnt = RegInit(Fill(8, false.B))
//     val sg2dat_layer_end = Wire(Bool())
 
//     val fifo_is_clear = ~dat_pop_req & ~wt_pop_req & dat_push_empty & wt_push_empty
//     val dat_bank_change = (last_data_bank != io.reg2dp_data_bank)
//     val wt_bank_change = (last_weight_bank != io.reg2dp_weight_bank)
//     val need_pending = (dat_bank_change | wt_bank_change)
//     val pending_done = is_pending & ~(dat_pending_clr^dat_pending_req) & ~(wt_pending_clr^wt_pending_req)
//     val flush_cycles_w = dat_stripe_size +& conf.CSC_SG_DONE_FLUSH.U
//     val sg_dn_cnt_w = Mux(~is_done & is_nxt_done, flush_cycles, (sg_dn_cnt - 1.U))

//     when(is_nxt_done){
//         sg_dn_cnt := sg_dn_cnt_w
//     }
//     dat_pending_ack := io.cdma2sc_dat_pending_ack
//     wt_pending_ack := io.cdma2sc_wt_pending_ack
//     when(dat_pop_req & dat_pop_ready & sg2dat_layer_end){
//         flush_cycles := flush_cycles_w 
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //  FSM output signals                                                //
//     ////////////////////////////////////////////////////////////////////////
//     val cur_mode = Wire(UInt(3.W))
//     val last_mode = RegInit(Fill(3, false.B))

//     val layer_st = io.reg2dp_op_en && (cur_state === SG_STATE_IDLE)
//     val is_idle = (cur_state === SG_STATE_IDLE)
//     is_pending := (cur_state === SG_STATE_PEND)
//     val is_running = (cur_state === SG_STATE_BUSY)
//     is_done := (cur_state === SG_STATE_DONE)
//     is_nxt_done := (nxt_state === SG_STATE_DONE)
//     val is_nxt_pending = (nxt_state === SG_STATE_PEND)
//     io.sc_state := Mux(is_idle, "b00".asUInt(2.W), Mux(is_pending, "b01".asUInt(2.W), Mux(is_running, "b10".asUInt(2.W), "b11".asUInt(2.W))))
//     val dat_pending_req_w = Mux(is_nxt_pending & dat_bank_change, "b1".asUInt(1.W), Mux( ~is_nxt_pending, "b0".asUInt(1.W), dat_pending_req))
//     val wt_pending_req_w = Mux(is_nxt_pending, "b1".asUInt(1.W), Mux(~is_nxt_pending, "b0".asUInt(1.W), wt_pending_req))
//     val is_mode_change = (last_mode != cur_mode)
//     val dat_pending_clr_w = Mux(is_pending & dat_pending_ack, "b1".asUInt(1.W), Mux(~is_nxt_pending, "b0".asUInt(1.W), dat_pending_clr))
//     val wt_pending_clr_w =  Mux(is_pending & dat_pending_ack, "b1".asUInt(1.W), Mux(~is_nxt_pending, "b0".asUInt(1.W), wt_pending_clr))

//     io.dp2reg_done := RegNext(is_done && (sg_dn_cnt == 1.U), false.B)
//     dat_pending_req := dat_pending_req_w
//     wt_pending_req := wt_pending_req_w
//     dat_pending_clr := dat_pending_clr_w
//     wt_pending_clr := wt_pending_clr_w

//     // sg send pending status to cdma
//     io.sc2cdma_dat_pending_req := dat_pending_req
//     io.sc2cdma_wt_pending_req := = wt_pending_req

//     ////////////////////////////////////////////////////////////////////////
//     //  registers to keep last layer status                               //
//     ////////////////////////////////////////////////////////////////////////
//     val last_slices = RegInit(Fill(14, false.B))
//     val slice_left = RegInit(Fill(14, false.B))
//     val last_kernels = RegInit(Fill(14, false.B))
//     val last_skip_weight_rls = RegInit(false.B)

//     when(io.dp2reg_done){
//         last_data_bank := io.reg2dp_data_bank
//         last_weight_bank := io.reg2dp_weight_bank
//         last_slices := slice_left
//         last_kernels := io.reg2dp_weight_kernel + 1.U
//         last_skip_weight_rls := io.reg2dp_skip_weight_rls
//         last_mode := cur_mode
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //  registers to calculate local values                               //
//     ////////////////////////////////////////////////////////////////////////
//     val is_img = Wire(Bool())
//     val is_int8 = (io.reg2dp_proc_precision === 0.U)
//     val is_pixel = (io.reg2dp_datain_format === 1.U)
//     val is_conv = (io.reg2dp_conv_mode === 0.U )
//     val is_dc = is_conv & ~is_pixel 
//     cur_mode :=  Cat(is_img, 0.U, is_dc)
//     val data_out_atomic_w = Mux(is_img, io.reg2dp_dataout_width +& 1.U, io.reg2dp_atomics +& 1.U)
//     val weight_width_cmp_w = Mux(is_img, "b0".asUInt(5.W), io.reg2dp_weight_width_ext)
//     val weight_height_cmp_w = io.reg2dp_weight_height_ext
//     is_img := is_conv & is_pixel
//     val data_in_height_w = io.reg2dp_datain_height_ext +& 1.U
//     val weight_channel_w = io.reg2dp_weight_channel_ext +& 1.U
//     val weight_groups_w = io.reg2dp_weight_kernel(12, conf.LOG2_ATOMK) +& 1.U
//     val weight_r_add_w = ("h9".asUInt(6.W) << io.reg2dp_y_extension)(5, 3)
//     val weight_r_last_w = Mux(weight_r_add_w(0), "b0".asUInt(2.W), 
//                           Mux(weight_r_add_w(1), Cat("b0".asUInt(1.W), io.reg2dp_weight_height_ext(0))
//                           , io.reg2dp_weight_height_ext(1, 0)))
//     val rls_slices_w = io.reg2dp_rls_slices + 1.U
//     val slice_left_w = Mux(io.reg2dp_skip_data_rls, io.reg2dp_datain_height_ext + 1.U, io.reg2dp_datain_height_ext - reg2dp_rls_slices)

//     //In opensource, DC batching only support fully connected layer. In this case stripe operation length is always 1
//     //upper_limit = 2*lower_limit or upper_limit = lower_limit
//     val lower_limit_w = Mux(is_img, conf.CSC_IMG_STRIPE.U, conf.CSC_ATOMK_HEX.U)
//     val upper_limit_w = Mux(is_img, conf.CSC_IMG_STRIPE.U, conf.CSC_ATOMK_MUL2_HEX.U)

//     val c_fetch_size = conf.CSC_ENTRY_HEX.U //ndef NVDLA_WINOGRAD_ENABLE
//     val data_batch_w = "b0".asUInt(6.W) // ndef NVDLA_BATCH_ENABLE

//     val data_in_height = RegInit(Fill(14, false.B))
//     val data_out_atomic = RegInit(Fill(22, false.B))
//     val data_batch = RegInit(Fill(6, false.B)) 
//     val weight_width_cmp = RegInit(Fill(5, false.B)) 
//     val weight_height_cmp = RegInit(Fill(5, false.B))
//     val weight_channel = RegInit(Fill(14, false.B)) 
//     val weight_groups = RegInit(Fill(10, false.B)) 
//     val weight_r_add = RegInit("h1".asUInt(3.W))
//     val weight_r_last = RegInit("h1".asUInt(3.W))
//     val rls_slices = RegInit(Fill(14, false.B))
//     val is_img_d1 = RegInit(false.B)
//     val lower_limit = RegInit(conf.CSC_ATOMK_HEX_STR.U)
//     val upper_limit = RegInit(conf.CSC_ATOMK_MUL2_HEX_STR.U)

//     when(layer_st){
//         data_in_height := data_in_height_w
//         data_out_atomic := data_out_atomic_w
//         data_batch := data_batch_w
//         weight_width_cmp := weight_width_cmp_w
//         weight_height_cmp := weight_height_cmp_w
//         weight_channel := weight_channel_w
//         weight_groups := weight_groups_w
//         weight_r_add := weight_r_add_w
//         weight_r_last := weight_r_last_w
//         rls_slices := rls_slices_w
//         slice_left := slice_left_w
//         is_img_d1 := is_img
//         lower_limit := lower_limit_w
//         upper_limit := upper_limit_w
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //  sequence generator for direct convolution                         //
//     ////////////////////////////////////////////////////////////////////////
//     //---------------------------layer count -----------------------------//
//     val is_last_group = Wire(Bool())
//     val layer_done = RegInit(false.B)
//     val op_layer_en = Wire(Bool())

//     val layer_done_w = Mux(layer_st, false.B, Mux(is_last_group, 1.U, layer_done))
//     when(layer_st | op_layer_en){
//         layer_done:= layer_done_w
//     }
// 1
//     //---------------------------kernel group count -----------------------------//
//     val op_group_en = Wire(Bool())
//     val group_up_cnt = RegInit(Fill(10, false.B))
//     val group_up_cnt_inc = group_up_cnt + 1.U
//     is_last_group := (group_up_cnt_inc === weight_groups)
//     val group_up_cnt_w = Mux(layer_st, "b0".asUInt(10.W), group_up_cnt_inc)
//     val cur_kernel = Mux(~is_last_group, conf.CSC_ATOMK_HEX.U, io.reg2dp_weight_kernel(conf.LOG2_ATOMK-1, 0)+&1.U)
    
//     when(layer_st | op_group_en){
//         group_up_cnt:= group_up_cnt_w
//     }

//     //--------------------------- output height count, for image case only -----------------------------//
//     val dataout_h_up_cnt = RegInit(Fill(13, false.B))

//     val is_last_do_h = ~is_img_d1 | (dataout_h_up_cnt == reg2dp_dataout_height)
//     val dataout_h_up_cnt_w = Mux(layer_st, "h0".asUInt(14.W), 
//                              Mux(is_last_do_h, "h0".asUInt(14.W), 
//                              (dataout_h_up_cnt + 1.U)))
//     when(layer_st | op_do_h_en){
//         dataout_h_up_cnt := dataout_h_up_cnt_w
//     }

//     //--------------------------- output stripe count -----------------------------//
//     val stripe_up_cnt = RegInit(Fill(22, false.B))
//     val stripe_up_cnt_2x_inc = stripe_up_cnt + Cat(upper_limit, "b0".asUInt(1.W))
//     val stripe_up_cnt_1x_inc =  stripe_up_cnt + upper_limit
//     val is_stripe_be_2x = (stripe_up_cnt_2x_inc <= data_out_atomic)
//     val is_stripe_le_1x = (stripe_up_cnt_1x_inc >= data_out_atomic)
//     val is_last_stripe = is_stripe_le_1x
//     val stripe_up_cnt_w = Mux(layer_st, "b0".asUInt(22.W)
//                              is_last_stripe, "b0".asUInt(23.W)
//                              is_stripe_be_2x, (stripe_up_cnt + upper_limit),
//                              (stripe_up_cnt + lower_limit))
//     val cur_stripe_inc = (data_out_atomic -  stripe_up_cnt)(6, 0)
//     val cur_stripe = Mux(is_stripe_be_2x, upper_limit, Mux(is_stripe_le_1x, cur_stripe_inc, lower_limit))

//     when(layer_st | op_stripe_en){
//         stripe_up_cnt := stripe_up_cnt_w
//     }

//     //--------------------------- channel count -----------------------------//
//     val channel_up_cnt_inc = channel_up_cnt + c_fetch_size(6, 0)
//     val is_last_channel = (channel_up_cnt_inc >= weight_channel)
//     val channel_up_cnt_w = Mux(layer_st, Fill(14, false.B), Mux(is_last_channel, Fill(14, false.B), channel_up_cnt_inc))

//     val cur_channel = Mux(~is_last_channel, c_fetch_size(6, 0), reg2dp_weight_channel_ext(LOG2_ATOMC - 1, 0)+1.U)
//     when(layer_st | op_channel_en){
//         channel_up_cnt := channel_up_cnt_w
//     }

//     //--------------------------- weight block count -----------------------------//
//     val weight_s_up_cnt = RegInit(Fill(5, false.B))
//     val weight_r_up_cnt = RegInit(Fill(5, false.B))
//     val op_s_en = Wire(Bool())
//     val op_r_en = Wire(Bool())

//     val weight_s_up_cnt_inc = weight_s_up_cnt + 1.U
//     val weight_r_up_cnt_inc = weight_r_up_cnt +& weight_r_add
//     val is_last_s = (weight_s_up_cnt === weight_width_cmp)
//     val is_last_r = (weight_r_up_cnt_inc > weight_height_cmp)
//     val cur_r = Mux(is_last_r, weight_r_last, 
//                 Mux(weight_r_add(2), "h3".asUInt(2.W), 
//                 Mux(weight_r_add(1), "h1".asUInt(2.W),
//                 "h0".asUInt(2.W))))

//     val is_last_block = is_last_s & is_last_r
//     val weight_s_up_cnt_w = Mux(layer_st, "b0".asUInt(5.W),
//                             Mux(is_last_s, "b0".asUInt(5.W),
//                             weight_s_up_cnt_inc))
//     val weight_r_up_cnt_w = Mux(layer_st, "b0".asUInt(5.W),
//                             Mux(is_last_r, "b0".asUInt(5.W),
//                             weight_r_up_cnt_inc(4, 0)))
    
//     when(layer_st | op_s_en){
//         weight_s_up_cnt := weight_s_up_cnt_w
//     }
//     when(layer_st | op_r_en){
//         weight_r_up_cnt := weight_r_up_cnt_w
//     }

//     //--------------------------- cbuf check logic -----------------------------//
//     val slices_avl = RegInit("b0".asUInt(14.W))
//     val required_kernels = RegInit("b0".asUInt(14.W))
//     val kernels_avl = RegInit("b0".asUInt(15.W))

//     val dat_cbuf_ready = (slices_avl >= data_in_height)
//     val required_kernels_inc = required_kernels + cur_kernel
//     val required_kernels_w = Mux((layer_st | is_last_group | ~io.reg2dp_skip_weight_rls), "b0".asUInt(14.W), required_kernels_inc)
//     val wt_cbuf_ready = required_kernels_inc <= kernels_avl

//     when(layer_st | op_group_en){
//         required_kernels := required_kernels_w
//     }

//     //--------------------------- register enable signal -----------------------------//
//     val dat_push_ready = Wire(Bool())
//     val wt_push_ready = Wire(Bool())

//     val fifo_push_ready = dat_push_ready & wt_push_ready
//     val cbuf_ready = dat_cbuf_ready & wt_cbuf_ready
//     val pkg_adv = is_running & cbuf_ready & ~layer_done & (~pkg_vld | fifo_push_ready)
//     op_s_en := pkg_adv
//     op_r_en := pkg_adv & is_last_s
//     op_do_h_en := pkg_vld & is_last_s
//     op_channel_en := pkg_adv & is_last_block
//     op_stripe_en := pkg_adv & is_last_block & is_last_channel
//     op_do_h_en := is_img_d1 & pkg_adv & is_last_block & is_last_channel & is_last_stripe
//     op_group_en := pkg_adv & is_last_block & is_last_channel & is_last_stripe & is_last_do_h
//     op_layer_en := pkg_adv & is_last_block & is_last_channel & is_last_stripe & is_last_do_h & is_last_group
//     val pkg_adv_w = Mux(~is_running, false.B, Mux(cbuf_ready&~layer_done, true.B, Mux(fifo_push_ready, false.B, pkg_vld)))

//     pkg_vld := pkg_adv_w

//     //--------------------------- package registers -----------------------------//
//     val pkg_idx = RegInit(Fill(2, true.B))
//     val dat_pkg_w_offset = RegInit("b0".asUInt(5.W))
//     val dat_pkg_h_offset = RegInit("b0".asUInt(5.W)) 
//     val dat_pkg_channel_size = RegInit("b0".asUInt(7.W))
//     val dat_pkg_stripe_length = RegInit("b0".asUInt(7.W))
//     val dat_pkg_cur_sub_h = RegInit("b0".asUInt(3.W))
//     val dat_pkg_block_end = RegInit(false.B)
//     val dat_pkg_channel_end = RegInit(false.B)
//     val dat_pkg_group_end = RegInit(false.B)
//     val dat_pkg_layer_end = RegInit(false.B)
//     val dat_pkg_dat_release = RegInit(false.B) 

//     val pkg_idx_w = Mux(layer_st, "h3".asUInt(2.W), pkg_idx + "b1".asUInt(2.W))
//     val pkg_weight_size_w = cur_channel
//     val stripe_length_w = cur_stripe
//     val pkg_block_end_w = is_last_block
//     val pkg_channel_end_w = is_last_block & is_last_channel
//     val pkg_group_end_w = is_last_block & is_last_channel & is_last_stripe & is_last_do_h
//     val pkg_layer_end_w = is_last_block & is_last_channel & is_last_stripe & is_last_do_h & is_last_group

//     when(layer_st | pkg_adv){
//         pkg_idx := pkg_idx_w
//     }
//     when(pkg_adv){
//         dat_pkg_w_offset := weight_s_up_cnt
//         dat_pkg_h_offset := weight_r_up_cnt
//         dat_pkg_channel_size := cur_channel
//         dat_pkg_stripe_length := stripe_length_w
//         dat_pkg_cur_sub_h := cur_r
//         dat_pkg_block_end := pkg_block_end_w
//         dat_pkg_channel_end := pkg_channel_end_w
//         dat_pkg_group_end := pkg_group_end_w
//         dat_pkg_layer_end := pkg_layer_end_w
//         dat_pkg_dat_release := ~reg2dp_skip_data_rls & pkg_layer_end_w
//     }

//     // PKT_PACK_WIRE( csc_dat_pkg ,  dat_pkg_ ,  dat_pkg_pd )
//     val wt_pkg_kernel_size = RegInit("b0".asUInt(7.W))
//     val wt_pkg_weight_size = RegInit("b0".asUInt(7.W))
//     val wt_pkg_cur_sub_h = RegInit("b0".asUInt(3.W))
//     val wt_pkg_wt_release = RegInit(false.B)

//     val dat_pkg_pd = Cat(dat_pkg_dat_release, dat_pkg_layer_end, dat_pkg_group_end, dat_pkg_channel_end, dat_pkg_block_end
//                         dat_pkg_cur_sub_h(1, 0), dat_pkg_stripe_length(6, 0), dat_pkg_channel_size(6, 0), 
//                         dat_pkg_h_offset(4, 0), dat_pkg_w_offset(4, 0))

//     val dat_push_data = Cat(pkg_idx, dat_pkg_pd)

//     when(pkg_adv){
//         wt_pkg_kernel_size := cur_kernel
//         wt_pkg_weight_size := pkg_weight_size_w
//         wt_pkg_cur_sub_h := cur_r
//         wt_pkg_wt_release := ~reg2dp_skip_weight_rls & pkg_group_end_w
//     }

//     val wt_pkg_channel_end = dat_pkg_channel_end
//     val wt_pkg_group_end = dat_pkg_group_end

//     // PKT_PACK_WIRE( csc_wt_pkg ,  wt_pkg_ ,  wt_pkg_pd )
//     val wt_pkg_pd = Cat(wt_pkg_wt_release, wt_pkg_group_end, wt_pkg_channel_end, 
//                         wt_pkg_cur_sub_h(1, 0), wt_pkg_kernel_size(5, 0), wt_pkg_weight_size)

//     val wt_push_data = Cat(pkg_idx, wt_pkg_pd)

//     ////////////////////////////////////////////////////////////////////////
//     //  issue control logic                                               //
//     ////////////////////////////////////////////////////////////////////////
//     val dat_pop_idx = dat_pop_data(32, 31)
//     val dat_pop_pd = dat_pop_data(30, 0)
//     val wt_pop_idx = wt_pop_data(19, 18)
//     val wt_pop_pd = wt_pop_data(17, 0)

//     // PKT_UNPACK_WIRE( csc_dat_pkg ,  sg2dat_ ,  dat_pop_pd )
//     val sg2dat_w_offset = dat_pop_pd(4, 0)
//     val sg2dat_h_offset = dat_pop_pd(9, 5)
//     val sg2dat_channel_size = dat_pop_pd(16, 10)
//     val sg2dat_stripe_length = dat_pop_pd(23, 17)
//     val sg2dat_cur_sub_h = dat_pop_pd(25, 24)
//     val sg2dat_block_end = dat_pop_pd(26)
//     val sg2dat_channel_end = dat_pop_pd(27)
//     val sg2dat_group_end = dat_pop_pd(28)
//     val sg2dat_layer_end = dat_pop_pd(29)
//     val sg2dat_dat_release = dat_pop_pd(30)

//     // PKT_UNPACK_WIRE( csc_wt_pkg ,  sg2wt_ ,  wt_pop_pd )
//     val sg2wt_weight_size = wt_pop_pd(6, 0)
//     val sg2wt_kernel_size = wt_pop_pd(12, 7)
//     val sg2wt_cur_sub_h = wt_pop_pd(14, 13)
//     val sg2wt_channel_end = wt_pop_pd(15)
//     val sg2wt_group_end = wt_pop_pd(16)
//     val sg2wt_wt_release = wt_pop_pd(17)

//     val sg2wt_kernel_size_inc = sg2wt_kernel_size + 1.U
//     val dat_stripe_batch_size_w = if(NVDLA_BATCH_ENABLE) sg2dat_stripe_length * data_batch
//                                   else sg2dat_stripe_length
//     val dat_stripe_img_size_w = sg2dat_stripe_length1
//     val dat_stripe_size_w = Mux(is_img_d1, dat_stripe_img_size_w, dat_stripe_batch_size_w)
//     val dat_stripe_img_length_w = Mux(~is_img_d1, "b0".asUInt(7.W), 
//                                   Mux(io.reg2dp_y_extension === "h2".asUInt(2.W), (sg2dat_stripe_length + "h3".asUInt(2.W)) & "hfc".asUInt(8.W),
//                                   Mux()
//                                   ))

//     val dat_stripe_length_w = Mux(is_img_d1, dat_stripe_img_length_w, dat_stripe_batch_size_w)

//     //delay for one cycle
//     val wt_pop_ready_d1 = RegInit(false.B)
//     val dat_stripe_size = RegInit("b0".asUInt(7.W))
//     val dat_stripe_length = RegInit("b0".asUInt(7.W))
//     val pop_cnt = RegInit("b0".asUInt(6.W))
//     val sg2dl_pvld_out = RegInit(false.B)
//     val sg2dl_pd_out = RegInit("b0".asUInt(31.W))
//     val sg2wl_pvld_out = RegInit(false.B)
//     val sg2wl_pd_out = RegInit("b0".asUInt(18.W))

//     val dat_max_cycles = Mux(~dat_pop_ready, "b0".asUInt(7.W), 
//                          Mux((dat_stripe_length < conf.CSC_MIN_STRIPE.U), conf.CSC_MIN_STRIPE.U,
//                          dat_stripe_length
//                          ))
//     val wt_cycles = sg2wt_kernel_size
//     val wt_max_cycles = Mux(~wt_pop_ready, "b0".asUInt(6.W), 
//                          Mux((wt_cycles <= "b1".asUInt(6.W)) & (pop_cnt <= "b1".asUInt(6.W)), "h2".asUInt(6.W),
//                          Mux((wt_cycles > pop_cnt), wt_cycles, pop_cnt
//                          )))
    



//     wt_pop_ready_d1 := wt_pop_ready 
//     when(wt_pop_ready_d1){
//         dat_stripe_size := dat_stripe_size_w
//         dat_stripe_length := dat_stripe_length_w   
//     }  
//     pop_cnt := pop_cnt_w
//     sg2dl_pvld := dat_pop_ready
//     when(sg2dl_pvld){
//         sg2dl_pd := dat_pop_pd
//     }
//     sg2wl_pvld := wt_pop_ready
//     when(wt_pop_ready){
        
//     }




    














    
















 
    




































    











    
// }

