package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CSC_sg(implicit val conf: cscConfiguration) extends Module {
    val io = IO(new Bundle {

        // reg2dp
        val reg2dp_op_en = Input(UInt(1.W))
        val reg2dp_conv_mode = Input(UInt(1.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_data_reuse = Input(UInt(1.W))
        val reg2dp_skip_data_rls = Input(UInt(1.W))
        val reg2dp_weight_reuse = Input(UInt(1.W)) 
        val reg2dp_skip_weight_rls = Input(UInt(1.W)) 
        val reg2dp_batches = Input(UInt(5.W)) 
        val reg2dp_datain_format = Input(UInt(1.W)) 
        val reg2dp_datain_height_ext = Input(UInt(13.W)) 
        val reg2dp_y_extension = Input(UInt(2.W)) 
        val reg2dp_weight_width_ext = Input(UInt(5.W)) 
        val reg2dp_weight_height_ext = Input(UInt(5.W)) 
        val reg2dp_weight_channel_ext = Input(UInt(13.W)) 
        val reg2dp_weight_kernel = Input(UInt(13.W)) 
        val reg2dp_dataout_width = Input(UInt(13.W)) 
        val reg2dp_dataout_height = Input(UInt(13.W)) 
        val reg2dp_data_bank = Input(UInt(5.W)) 
        val reg2dp_weight_bank = Input(UInt(5.W)) 
        val reg2dp_atomics = Input(UInt(21.W)) 
        val reg2dp_rls_slices = Input(UInt(12.W)) 
        val dp2reg_done = Output(Bool())

        //cdma dat
        val cdma2sc_dat_updt = Input(Bool());     
        val cdma2sc_dat_slices = Input(UInt(14.W))
        val cdma2sc_dat_entries = Input(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
        val sc2cdma_dat_pending_req = Output(Bool())   //send sg pending to cdma
        val cdma2sc_dat_pending_ack = Input(Bool())   //cdma ask sg to clr pending

        //cdma wt
        val cdma2sc_wt_updt = Input(Bool())       
        val cdma2sc_wt_kernels = Input(UInt(14.W))
        val cdma2sc_wt_entries = Input(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
        val sc2cdma_wt_pending_req = Output(Bool())   //send wt pending to cdma
        val cdma2sc_wt_pending_ack = Input(Bool())  //cdma ask sg to clr pending

        val sc_state = Output(UInt(2.W))

        //to dl
        val sg2dl_pvld = Output(Bool())
        val sg2dl_pd = Output(UInt(31.W))
        val sg2dl_reuse_rls = Output(Bool())
        //to wl
        val sg2wl_pvld = Output(Bool())
        val sg2wl_pd = Output(UInt(18.W))
        val sg2wl_reuse_rls = Output(Bool())

        //cacc to sc
        val accu2sc_credit_vld = Input(Bool())
        val accu2sc_credit_size = Input(UInt(3.W))

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
    ////////////////////////////////////////////////////////////////////////
    // CSC control FSM                                                    //
    ////////////////////////////////////////////////////////////////////////
    val need_pending = Wire(Bool())
    val pending_done = Wire(Bool())
    val layer_done = RegInit(false.B)
    val fifo_is_clear = Wire(Bool())
    val pkg_vld = RegInit(false.B)

    val SG_STATE_IDLE :: SG_STATE_PEND :: SG_STATE_BUSY :: SG_STATE_DONE :: Nil = Enum(4)
    val cur_state = RegInit(SG_STATE_IDLE)
    val nxt_state = WireInit(SG_STATE_IDLE)

    switch (cur_state) {
        is (SG_STATE_IDLE) {
        when (io.reg2dp_op_en & need_pending) { nxt_state := SG_STATE_PEND }
        .elsewhen (io.reg2dp_op_en) { nxt_state := SG_STATE_BUSY }
        }
        is (SG_STATE_PEND) {
        when (pending_done) { nxt_state := SG_STATE_BUSY }
        }
        is (SG_STATE_BUSY) {
        when (layer_done & fifo_is_clear & ~pkg_vld) { nxt_state := SG_STATE_DONE }
        }
        is (SG_STATE_DONE) {
        when (io.dp2reg_done) { nxt_state := SG_STATE_IDLE }
        }
    }
    cur_state := nxt_state

    ////////////////////////////////////////////////////////////////////////
    //  FSM input signals                                                 //
    ////////////////////////////////////////////////////////////////////////
    val dat_pop_req = Wire(Bool())
    val dat_push_empty = Wire(Bool())
    val dat_pop_ready = Wire(Bool())
    val wt_pop_req = Wire(Bool())
    val wt_push_empty = Wire(Bool())
    val last_data_bank = RegInit(Fill(5, true.B))
    val last_weight_bank = RegInit(Fill(5, true.B)) 
    val dat_pending_clr = RegInit(false.B)
    val dat_pending_req = RegInit(false.B)
    val dat_pending_ack = RegInit(false.B)
    val wt_pending_clr = RegInit(false.B)
    val wt_pending_req = RegInit(false.B)
    val wt_pending_ack = RegInit(false.B)
    val is_pending = Wire(Bool())
    val dat_stripe_size = RegInit(Fill(7, false.B))
    val is_done = Wire(Bool())
    val is_nxt_done = Wire(Bool())
    val flush_cycles = RegInit(Fill(7, false.B))
    val sg_dn_cnt = RegInit(Fill(8, false.B))
    val sg2dat_layer_end = Wire(Bool())
 
    val fifo_is_clear = ~dat_pop_req & ~wt_pop_req & dat_push_empty & wt_push_empty
    val dat_bank_change = (last_data_bank != reg2dp_data_bank)
    val wt_bank_change = (last_weight_bank != reg2dp_weight_bank)
    val need_pending = (dat_bank_change | wt_bank_change)
    val pending_done = is_pending & ~(dat_pending_clr^dat_pending_req) & ~(wt_pending_clr^wt_pending_req)
    val flush_cycles_w = dat_stripe_size +& conf.CSC_SG_DONE_FLUSH.U
    val sg_dn_cnt_w = Mux(~is_done & is_nxt_done, flush_cycles, (sg_dn_cnt - 1.U))

    when(is_nxt_done){
        sg_dn_cnt := is_nxt_done
    }
    dat_pending_ack := io.cdma2sc_dat_pending_ack
    wt_pending_ack := io.cdma2sc_wt_pending_ack
    when(dat_pop_req & dat_pop_ready & sg2dat_layer_end)
    flush_cycles := flush_cycles_w 

    ////////////////////////////////////////////////////////////////////////
    //  FSM output signals                                                //
    ////////////////////////////////////////////////////////////////////////
    val cur_mode = Wire(UInt(3.W))
    val last_mode = RegInit(Fill(3, false.B))

    val layer_st = io.reg2dp_op_en && (cur_state === SG_STATE_IDLE)
    val is_idle = (cur_state === SG_STATE_IDLE)
    is_pending := (cur_state === SG_STATE_PEND)
    val is_running = (cur_state === SG_STATE_BUSY)
    is_done := (cur_state === SG_STATE_DONE)
    is_nxt_done := (nxt_state === SG_STATE_DONE)
    val is_nxt_pending = (nxt_state === SG_STATE_PEND)
    io.sc_state := Mux(is_idle, "b00".asUInt(2.W), Mux(is_pending, "b01".asUInt(2.W), Mux(is_running, "b10".asUInt(2.W), "b11".asUInt(2.W))))
    val dat_pending_req_w = Mux(is_nxt_pending & dat_bank_change, "b1".asUInt(1.W), Mux( ~is_nxt_pending, "b0".asUInt(1.W), dat_pending_req))
    val wt_pending_req_w = Mux(is_nxt_pending, "b1".asUInt(1.W), Mux(~is_nxt_pending, "b0".asUInt(1.W), wt_pending_req))
    val is_mode_change = (last_mode != cur_mode)
    val dat_pending_clr_w = Mux(is_pending & dat_pending_ack, "b1".asUInt(1.W), Mux(~is_nxt_pending, "b0".asUInt(1.W), dat_pending_clr))
    val wt_pending_clr_w =  Mux(is_pending & dat_pending_ack, "b1".asUInt(1.W), Mux(~is_nxt_pending, "b0".asUInt(1.W), wt_pending_clr))

    io.dp2reg_done = RegNext(is_done && (sg_dn_cnt == 1.U), false.B)
    dat_pending_req := dat_pending_req_w
    wt_pending_req := wt_pending_req_w
    dat_pending_clr := dat_pending_clr_w
    wt_pending_clr := wt_pending_clr_w

    // sg send pending status to cdma
    io.sc2cdma_dat_pending_req := dat_pending_req
    io.sc2cdma_wt_pending_req := = wt_pending_req

    ////////////////////////////////////////////////////////////////////////
    //  registers to keep last layer status                               //
    ////////////////////////////////////////////////////////////////////////
    val last_slices = RegInit(Fill(14, false.B))
    val slice_left = RegInit(Fill(14, false.B))
    val last_kernels = RegInit(Fill(14, false.B))
    val last_skip_weight_rls = RegInit(false.B)

    when(io.dp2reg_done){
        last_data_bank := io.reg2dp_data_bank
        last_weight_bank := io.reg2dp_weight_bank
        last_slices := slice_left
        last_kernels := io.reg2dp_weight_kernel + 1.U
        last_skip_weight_rls := io.reg2dp_skip_weight_rls
        last_mode := cur_mode
    }

    ////////////////////////////////////////////////////////////////////////
    //  registers to calculate local values                               //
    ////////////////////////////////////////////////////////////////////////
    val is_int8 = (io.reg2dp_proc_precision === 0.U)
    val is_pixel = (io.reg2dp_datain_format === 1.U)
    val is_conv = (io.reg2dp_conv_mode === 0.U )
    val is_dc = is_conv & ~is_pixel  
    val is_img = is_conv & is_pixel
    val data_in_height_w = io.reg2dp_datain_height_ext + 1.U
    val weight_channel_w = io.reg2dp_weight_channel_ext + 1.U
    val weight_groups_w = io.reg2dp_weight_kernel(12, conf.LOG2_ATOMK) + 1.U
    val weight_r_add_w = ("h9".asUInt(6.W) << io.reg2dp_y_extension)(5, 3)
    val weight_r_last_w = Mux(weight_r_add_w(0), "b0".asUInt(2.W), 
                          Mux(weight_r_add_w(1), Cat("b0".asUInt(1.W), io.reg2dp_weight_height_ext(0))
                          , io.reg2dp_weight_height_ext(1, 0)))
    val rls_slices_w = io.reg2dp_rls_slices + 1.U
    val slice_left_w = Mux(io.reg2dp_skip_data_rls, io.reg2dp_datain_height_ext + 1.U, io.reg2dp_datain_height_ext - reg2dp_rls_slices)

    //In opensource, DC batching only support fully connected layer. In this case stripe operation length is always 1
    //upper_limit = 2*lower_limit or upper_limit = lower_limit
    val lower_limit_w = Mux(is_img, conf.CSC_IMG_STRIPE.U, conf.CSC_ATOMK_HEX.U)
    val upper_limit_w = Mux(is_img, conf.CSC_IMG_STRIPE.U, conf.CSC_ATOMK_MUL2_HEX.U)

    val c_fetch_size = conf.CSC_ENTRY_HEX.U //ndef NVDLA_WINOGRAD_ENABLE
    val data_batch_w = "b0".asUInt(6.W) // ndef NVDLA_BATCH_ENABLE

    val data_in_height = RegInit(Fill(14, false.B))
    val data_out_atomic = RegInit(Fill(22, false.B))
    val data_batch = RegInit(Fill(6, false.B)) 
    val weight_width_cmp = RegInit(Fill(5, false.B)) 
    val weight_height_cmp = RegInit(Fill(5, false.B))
    val weight_channel = RegInit(Fill(14, false.B)) 
    val weight_groups = RegInit(Fill(10, false.B)) 
    val weight_r_add = RegInit("h1".asUInt(3.W))
    val weight_r_last = RegInit("h1".asUInt(3.W))
    val rls_slices = RegInit(Fill(14, false.B))
    val is_img_d1 = RegInit(false.B)
    val lower_limit = RegInit(conf.CSC_ATOMK_HEX_STR.U)
    val upper_limit = RegInit(conf.CSC_ATOMK_MUL2_HEX_STR.U)

    when(layer_st){
        data_in_height := data_in_height_w
        data_out_atomic := data_out_atomic_w
        data_batch := data_batch_w
        weight_width_cmp := weight_width_cmp_w
        weight_height_cmp := weight_height_cmp_w
        weight_channel := weight_channel_w
        weight_groups := weight_groups_w
        weight_r_add := weight_r_add_w
        weight_r_last := weight_r_last_w
        rls_slices := rls_slices_w
        slice_left := slice_left_w
        is_img_d1 := is_img
        lower_limit := lower_limit_w
        upper_limit := upper_limit_w
    }

    ////////////////////////////////////////////////////////////////////////
    //  sequence generator for direct convolution                         //
    ////////////////////////////////////////////////////////////////////////
    //---------------------------layer count -----------------------------//
    val is_last_group = Wire(Bool())
    val layer_done = RegInit(false.B)
    val op_layer_en = Wire(Bool())

    val layer_done_w = Mux(layer_st, false.B, Mux(is_last_group, 1.U, layer_done))
    when(layer_st | op_layer_en){
        layer_done:= layer_done_w
    }
1
    //---------------------------kernel group count -----------------------------//
    val group_up_cnt = RegInit(Fill(10, false.B))
    val group_up_cnt_inc = group_up_cnt + 1.U
    is_last_group := (group_up_cnt_inc === weight_groups)
    val group_up_cnt_w = Mux(layer_st, "b0".asUInt(10.W), group_up_cnt_inc)
    val cur_kernel = Mux(~is_last_group, conf.CSC_ATOMK_HEX.U, io.reg2dp_weight_kernel(conf.LOG2_ATOMK-1, 0)+1.U)
    
    when(layer_st | op_layer_en){
        group_up_cnt:= group_up_cnt_w
    }

    //--------------------------- output height count, for image case only -----------------------------//
    val dataout_h_up_cnt = RegInit(Fill(13, false.B))

    val is_last_do_h = ~is_img_d1 | (dataout_h_up_cnt == reg2dp_dataout_height)
    val dataout_h_up_cnt_w = Mux(layer_st, "h0".asUInt(14.W), 
                             Mux(is_last_do_h, "h0".asUInt(14.W), 
                             (dataout_h_up_cnt + 1.U)))
    when(layer_st | op_do_h_en){
        dataout_h_up_cnt := dataout_h_up_cnt_w
    }

    //--------------------------- output stripe count -----------------------------//
    val stripe_up_cnt = RegInit(Fill(22, false.B))
    val stripe_up_cnt_2x_inc = stripe_up_cnt + Cat(upper_limit, "b0".asUInt(1.W))
    val stripe_up_cnt_1x_inc =  stripe_up_cnt + upper_limit
    val is_stripe_be_2x = (stripe_up_cnt_2x_inc <= data_out_atomic)
    val is_stripe_le_1x = (stripe_up_cnt_1x_inc >= data_out_atomic)
    val is_last_stripe = is_stripe_le_1x
    val stripe_up_cnt_w = Mux(layer_st, "b0".asUInt(22.W)
                             is_last_stripe, "b0".asUInt(23.W)
                             is_stripe_be_2x, (stripe_up_cnt + upper_limit),
                             (stripe_up_cnt + lower_limit))
    val cur_stripe_inc = (data_out_atomic -  stripe_up_cnt)(6, 0)
    val cur_stripe = Mux(is_stripe_be_2x, upper_limit, Mux(is_stripe_le_1x, cur_stripe_inc, lower_limit))

    when(layer_st | op_stripe_en){
        stripe_up_cnt := stripe_up_cnt_w
    }

    //--------------------------- channel count -----------------------------//
    val channel_up_cnt_inc = channel_up_cnt + c_fetch_size(6, 0)
    val is_last_channel = (channel_up_cnt_inc >= weight_channel)
    val channel_up_cnt_w = Mux(layer_st, Fill(14, false.B), Mux(is_last_channel, Fill(14, false.B), channel_up_cnt_inc))

    val cur_channel = Mux(~is_last_channel, c_fetch_size(6, 0), reg2dp_weight_channel_ext(LOG2_ATOMC - 1, 0)+1.U)
    when(layer_st | op_channel_en){
        channel_up_cnt := channel_up_cnt_w
    }

    //--------------------------- weight block count -----------------------------//
    val weight_s_up_cnt = RegInit(Fill(5, false.B))
    val weight_r_up_cnt = RegInit(Fill(5, false.B))

    val weight_s_up_cnt_inc = weight_s_up_cnt + 1.U
    val weight_r_up_cnt_inc = weight_r_up_cnt +& weight_r_add
    val is_last_s = (weight_s_up_cnt === weight_width_cmp)
    val is_last_r = (weight_r_up_cnt_inc > weight_height_cmp)
    val cur_r = Mux(is_last_r, weight_r_last, 
                Mux(weight_r_add(2), "h3".asUInt(2.W), 
                Mux(weight_r_add(1), "h1".asUInt(2.W),
                "h0".asUInt(2.W))))

    val is_last_block = is_last_s & is_last_r
    val weight_s_up_cnt_w = Mux(layer_st, "b0".asUInt(5.W),
                            Mux(is_last_s, "b0".asUInt(5.W),
                            weight_s_up_cnt_inc))
    val weight_r_up_cnt_w = 




































    











    
}

