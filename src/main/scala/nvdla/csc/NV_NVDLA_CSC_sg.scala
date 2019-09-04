package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CSC_sg(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_ng_clk = Input(Clock())

        //cdma dat
        val cdma2sc_dat_updt = Flipped(ValidIO(new updt_entries_slices_if))
        val sc2cdma_dat_pending_req = Output(Bool())   //send sg pending to cdma
        val cdma2sc_dat_pending_ack = Input(Bool())   //cdma ask sg to clr pending

        //cdma wt
        val cdma2sc_wt_updt = Flipped(ValidIO(new updt_entries_kernels_if))    
        val sc2cdma_wt_pending_req = Output(Bool())   //send wt pending to cdma
        val cdma2sc_wt_pending_ack = Input(Bool())  //cdma ask sg to clr pending

        val sc_state = Output(UInt(2.W))

        //to dl
        val sg2dl = new csc_sg2dl_if

        //to wl
        val sg2wl = new csc_sg2wl_if

        //cacc to sc
        val accu2sc_credit_size = Flipped(ValidIO(UInt(3.W)))

        // reg2dp
        val reg2dp_op_en = Input(Bool())
        val reg2dp_conv_mode = Input(Bool())
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_data_reuse = Input(Bool())
        val reg2dp_skip_data_rls = Input(Bool())
        val reg2dp_weight_reuse = Input(Bool()) 
        val reg2dp_skip_weight_rls = Input(Bool()) 
        val reg2dp_batches = Input(UInt(5.W))
        val reg2dp_datain_format = Input(Bool()) 
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

        val pwrbus_ram_pd = Input(UInt(32.W))

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
    ////////////////////////////////////////////////////////////////////////
    // CSC control FSM                                                    //
    ////////////////////////////////////////////////////////////////////////
    val need_pending = Wire(Bool())
    val pending_done = Wire(Bool())
    val layer_done = RegInit(false.B)
    val fifo_is_clear = Wire(Bool())
    val pkg_vld = RegInit(false.B)

    val sIdle :: sPend :: sBusy :: sDone :: Nil = Enum(4)
    val cur_state = RegInit(sIdle)
    val nxt_state = WireInit(sIdle)

    switch (cur_state) {
        is (sIdle) {
        when (io.reg2dp_op_en & need_pending) { nxt_state := sPend }
        .elsewhen (io.reg2dp_op_en) { nxt_state := sBusy }
        }
        is (sPend) {
        when (pending_done) { nxt_state := sBusy }
        }
        is (sBusy) {
        when (layer_done & fifo_is_clear & ~pkg_vld) { nxt_state := sDone }
        }
        is (sDone) {
        when (io.dp2reg_done) { nxt_state := sIdle }
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
    val last_data_bank = RegInit(31.U)
    val last_weight_bank = RegInit(31.U) 
    val dat_pending_clr = RegInit(false.B)
    val dat_pending_req = RegInit(false.B)
    val dat_pending_ack = RegInit(false.B)
    val wt_pending_clr = RegInit(false.B)
    val wt_pending_req = RegInit(false.B)
    val wt_pending_ack = RegInit(false.B)
    val is_pending = Wire(Bool())
    val dat_stripe_size = RegInit("b0".asUInt(7.W))
    val is_done = Wire(Bool())
    val is_nxt_done = Wire(Bool())
    val flush_cycles = RegInit("b0".asUInt(8.W))
    val sg_dn_cnt = RegInit("b0".asUInt(8.W))
    val sg2dat_layer_end = Wire(Bool())
 
    fifo_is_clear := ~dat_pop_req & ~wt_pop_req & dat_push_empty & wt_push_empty
    val dat_bank_change = (last_data_bank =/= io.reg2dp_data_bank)
    val wt_bank_change = (last_weight_bank =/= io.reg2dp_weight_bank)
    need_pending := (dat_bank_change | wt_bank_change)
    pending_done := is_pending & ~(dat_pending_clr^dat_pending_req) & ~(wt_pending_clr^wt_pending_req)

    when(is_nxt_done){
        sg_dn_cnt := Mux(~is_done & is_nxt_done, flush_cycles, sg_dn_cnt - 1.U)
    }
    dat_pending_ack := io.cdma2sc_dat_pending_ack
    wt_pending_ack := io.cdma2sc_wt_pending_ack
    when(dat_pop_req & dat_pop_ready & sg2dat_layer_end){
        flush_cycles := dat_stripe_size +& conf.CSC_SG_DONE_FLUSH.U
    }

    ////////////////////////////////////////////////////////////////////////
    //  FSM output signals                                                //
    ////////////////////////////////////////////////////////////////////////
    val cur_mode = Wire(UInt(3.W))
    val last_mode = RegInit(Fill(3, false.B))

    val layer_st = io.reg2dp_op_en & (cur_state === sIdle)
    val is_idle = (cur_state === sIdle)
    is_pending := (cur_state === sPend)
    val is_running = (cur_state === sBusy)
    is_done := (cur_state === sDone)
    is_nxt_done := (nxt_state === sDone)
    val is_nxt_pending = (nxt_state === sPend)
    io.sc_state := Mux(is_idle, "b00".asUInt(2.W), Mux(is_pending, "b01".asUInt(2.W), Mux(is_running, "b10".asUInt(2.W), "b11".asUInt(2.W))))
    val is_mode_change = (last_mode =/= cur_mode)

    io.dp2reg_done := RegNext(is_done && (sg_dn_cnt === 1.U), false.B)
    dat_pending_req := Mux(is_nxt_pending & dat_bank_change, "b1".asUInt(1.W), Mux( ~is_nxt_pending, "b0".asUInt(1.W), dat_pending_req))
    wt_pending_req := Mux(is_nxt_pending, "b1".asUInt(1.W), Mux(~is_nxt_pending, "b0".asUInt(1.W), wt_pending_req))
    dat_pending_clr := Mux(is_pending & dat_pending_ack, "b1".asUInt(1.W), Mux(~is_nxt_pending, "b0".asUInt(1.W), dat_pending_clr))
    wt_pending_clr := Mux(is_pending & wt_pending_ack, "b1".asUInt(1.W), Mux(~is_nxt_pending, "b0".asUInt(1.W), wt_pending_clr))

    // sg send pending status to cdma
    io.sc2cdma_dat_pending_req := dat_pending_req
    io.sc2cdma_wt_pending_req := wt_pending_req

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
        last_kernels := io.reg2dp_weight_kernel +& 1.U
        last_skip_weight_rls := io.reg2dp_skip_weight_rls
        last_mode := cur_mode
    }

    ////////////////////////////////////////////////////////////////////////
    //  registers to calculate local values                               //
    ////////////////////////////////////////////////////////////////////////
    val is_pixel = (io.reg2dp_datain_format === 1.U)
    val is_conv = (io.reg2dp_conv_mode === 0.U )
    val is_img = is_conv & is_pixel
    val is_dc = is_conv & ~is_pixel 

    cur_mode :=  Cat(is_img, false.B, is_dc)

    val c_fetch_size = conf.CSC_ENTRY_HEX.U 

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
    val lower_limit = RegInit(conf.CSC_ATOMK_HEX.U)
    val upper_limit = RegInit(conf.CSC_ATOMK_MUL2_HEX.U)

    val weight_r_add_w = ("h9".asUInt(6.W) << io.reg2dp_y_extension)(5, 3)

    when(layer_st){
        data_in_height := io.reg2dp_datain_height_ext +& 1.U
        data_out_atomic := Mux(is_img, io.reg2dp_dataout_width +& 1.U, io.reg2dp_atomics +& 1.U)
        data_batch := "b0".asUInt(6.W) // ndef NVDLA_BATCH_ENABLE
        weight_width_cmp := Mux(is_img, "b0".asUInt(5.W), io.reg2dp_weight_width_ext)
        weight_height_cmp := io.reg2dp_weight_height_ext
        weight_channel := io.reg2dp_weight_channel_ext +& 1.U
        weight_groups := io.reg2dp_weight_kernel(12, conf.LOG2_ATOMK) +& 1.U
        weight_r_add := weight_r_add_w
        weight_r_last := Mux(weight_r_add_w(0), "b0".asUInt(2.W), 
                         Mux(weight_r_add_w(1), Cat("b0".asUInt(1.W), io.reg2dp_weight_height_ext(0))
                         , io.reg2dp_weight_height_ext(1, 0)))
        rls_slices := io.reg2dp_rls_slices + 1.U
        slice_left := Mux(io.reg2dp_skip_data_rls, io.reg2dp_datain_height_ext +& 1.U, io.reg2dp_datain_height_ext -& io.reg2dp_rls_slices)
        is_img_d1 := is_img
        //In opensource, DC batching only support fully connected layer. In this case stripe operation length is always 1
        //upper_limit = 2*lower_limit or upper_limit = lower_limit
        lower_limit := Mux(is_img, conf.CSC_IMG_STRIPE.U, conf.CSC_ATOMK_HEX.U)
        upper_limit :=  Mux(is_img, conf.CSC_IMG_STRIPE.U, conf.CSC_ATOMK_MUL2_HEX.U)
    }

    ////////////////////////////////////////////////////////////////////////
    //  sequence generator for direct convolution                         //
    ////////////////////////////////////////////////////////////////////////
    //---------------------------layer count -----------------------------//
    val is_last_group = Wire(Bool())
    val op_layer_en = Wire(Bool())

    when(layer_st | op_layer_en){
        layer_done:= Mux(layer_st, false.B, Mux(is_last_group, true.B, layer_done))
    }

    //---------------------------kernel group count -----------------------------//
    val op_group_en = Wire(Bool())
    val group_up_cnt = RegInit(Fill(10, false.B))
    val group_up_cnt_inc = group_up_cnt + 1.U
    is_last_group := (group_up_cnt_inc === weight_groups)
    val cur_kernel = Mux(~is_last_group, conf.CSC_ATOMK_HEX.U, io.reg2dp_weight_kernel(conf.LOG2_ATOMK-1, 0) +& 1.U)
    
    when(layer_st | op_group_en){
        group_up_cnt:= Mux(layer_st, "b0".asUInt(10.W), group_up_cnt_inc)
    }

    //--------------------------- output height count, for image case only -----------------------------//
    val dataout_h_up_cnt = RegInit(Fill(13, false.B))
    val op_do_h_en = Wire(Bool())

    val is_last_do_h = ~is_img_d1 | (dataout_h_up_cnt === io.reg2dp_dataout_height)
    when(layer_st | op_do_h_en){
        dataout_h_up_cnt := Mux(layer_st, "h0".asUInt(13.W), 
                            Mux(is_last_do_h, "h0".asUInt(13.W), 
                            (dataout_h_up_cnt + 1.U)))
    }

    //--------------------------- output stripe count -----------------------------//
    val stripe_up_cnt = RegInit(Fill(22, false.B))
    val stripe_up_cnt_2x_inc = stripe_up_cnt + Cat(upper_limit, "b0".asUInt(1.W))
    val stripe_up_cnt_1x_inc =  stripe_up_cnt + upper_limit
    val is_stripe_be_2x = (stripe_up_cnt_2x_inc <= data_out_atomic)
    val is_stripe_le_1x = (stripe_up_cnt_1x_inc >= data_out_atomic)
    val is_last_stripe = is_stripe_le_1x
    val cur_stripe_inc = (data_out_atomic -  stripe_up_cnt)(6, 0)
    val cur_stripe = Mux(is_stripe_be_2x, upper_limit, Mux(is_stripe_le_1x, cur_stripe_inc, lower_limit))
    val op_stripe_en = Wire(Bool())

    when(layer_st | op_stripe_en){
        stripe_up_cnt := Mux(layer_st, "b0".asUInt(22.W), 
                         Mux(is_last_stripe, "b0".asUInt(22.W),
                         Mux(is_stripe_be_2x, (stripe_up_cnt + upper_limit),
                         (stripe_up_cnt + lower_limit))))
    }

    //--------------------------- channel count -----------------------------//
    val op_channel_en = Wire(Bool())
    val channel_up_cnt = RegInit(Fill(14, false.B))
    val channel_up_cnt_inc = channel_up_cnt + c_fetch_size(6, 0)
    val is_last_channel = (channel_up_cnt_inc >= weight_channel)
    
    val cur_channel = Mux(~is_last_channel, c_fetch_size(6, 0), io.reg2dp_weight_channel_ext(conf.LOG2_ATOMC - 1, 0)+&1.U)

    when(layer_st | op_channel_en){
        channel_up_cnt := Mux(layer_st, Fill(14, false.B), Mux(is_last_channel, Fill(14, false.B), channel_up_cnt_inc))
    }

    //--------------------------- weight block count -----------------------------//
    val weight_s_up_cnt = RegInit(Fill(5, false.B))
    val weight_r_up_cnt = RegInit(Fill(5, false.B))
    val op_s_en = Wire(Bool())
    val op_r_en = Wire(Bool())

    val weight_s_up_cnt_inc = weight_s_up_cnt + 1.U
    val weight_r_up_cnt_inc = weight_r_up_cnt +& weight_r_add
    val is_last_s = (weight_s_up_cnt === weight_width_cmp)
    val is_last_r = (weight_r_up_cnt_inc > weight_height_cmp)
    val cur_r = Mux(is_last_r, weight_r_last, 
                Mux(weight_r_add(2), "h3".asUInt(2.W), 
                Mux(weight_r_add(1), "h1".asUInt(2.W),
                "h0".asUInt(2.W))))

    val is_last_block = is_last_s & is_last_r
    
    when(layer_st | op_s_en){
        weight_s_up_cnt := Mux(layer_st, "b0".asUInt(5.W),
                            Mux(is_last_s, "b0".asUInt(5.W),
                            weight_s_up_cnt_inc))
        weight_r_up_cnt := Mux(layer_st, "b0".asUInt(5.W),
                            Mux(is_last_r, "b0".asUInt(5.W),
                            weight_r_up_cnt_inc(4, 0)))
    }
    //--------------------------- cbuf check logic -----------------------------//
    val slices_avl = RegInit("b0".asUInt(14.W))
    val required_kernels = RegInit("b0".asUInt(14.W))
    val kernels_avl = RegInit("b0".asUInt(15.W))

    val dat_cbuf_ready = (slices_avl >= data_in_height)
    val required_kernels_inc = required_kernels + cur_kernel
    val wt_cbuf_ready = required_kernels_inc <= kernels_avl

    when(layer_st | op_group_en){
        required_kernels := Mux((layer_st | is_last_group | ~io.reg2dp_skip_weight_rls), "b0".asUInt(14.W), required_kernels_inc)
    }

    //--------------------------- register enable signal -----------------------------//
    val dat_push_ready = Wire(Bool())
    val wt_push_ready = Wire(Bool())

    val fifo_push_ready = dat_push_ready & wt_push_ready
    val cbuf_ready = dat_cbuf_ready & wt_cbuf_ready
    val pkg_adv = is_running & cbuf_ready & ~layer_done & (~pkg_vld | fifo_push_ready)
    op_s_en := pkg_adv
    op_r_en := pkg_adv & is_last_s
    op_channel_en := pkg_adv & is_last_block
    op_stripe_en := pkg_adv & is_last_block & is_last_channel
    op_do_h_en := is_img_d1 & pkg_adv & is_last_block & is_last_channel & is_last_stripe
    op_group_en := pkg_adv & is_last_block & is_last_channel & is_last_stripe & is_last_do_h
    op_layer_en := pkg_adv & is_last_block & is_last_channel & is_last_stripe & is_last_do_h & is_last_group

    pkg_vld := Mux(~is_running, false.B, Mux(cbuf_ready & ~layer_done, true.B, Mux(fifo_push_ready, false.B, pkg_vld)))

    //--------------------------- package registers -----------------------------//
    val pkg_idx = RegInit(Fill(2, true.B))
    val dat_pkg_w_offset = RegInit("b0".asUInt(5.W))
    val dat_pkg_h_offset = RegInit("b0".asUInt(5.W)) 
    val dat_pkg_channel_size = RegInit("b0".asUInt(7.W))
    val dat_pkg_stripe_length = RegInit("b0".asUInt(7.W))
    val dat_pkg_cur_sub_h = RegInit("b0".asUInt(3.W))
    val dat_pkg_block_end = RegInit(false.B)
    val dat_pkg_channel_end = RegInit(false.B)
    val dat_pkg_group_end = RegInit(false.B)
    val dat_pkg_layer_end = RegInit(false.B)
    val dat_pkg_dat_release = RegInit(false.B) 

    val pkg_idx_w = Mux(layer_st, "h3".asUInt(2.W), pkg_idx + "b1".asUInt(2.W))
    val pkg_weight_size_w = cur_channel
    val stripe_length_w = cur_stripe
    val pkg_block_end_w = is_last_block
    val pkg_channel_end_w = is_last_block & is_last_channel
    val pkg_group_end_w = is_last_block & is_last_channel & is_last_stripe & is_last_do_h
    val pkg_layer_end_w = is_last_block & is_last_channel & is_last_stripe & is_last_do_h & is_last_group

    when(layer_st | pkg_adv){
        pkg_idx := Mux(layer_st, "h3".asUInt(2.W), pkg_idx + "b1".asUInt(2.W))
    }
    when(pkg_adv){
        dat_pkg_w_offset := weight_s_up_cnt
        dat_pkg_h_offset := weight_r_up_cnt
        dat_pkg_channel_size := cur_channel
        dat_pkg_stripe_length := stripe_length_w
        dat_pkg_cur_sub_h := cur_r
        dat_pkg_block_end := pkg_block_end_w
        dat_pkg_channel_end := pkg_channel_end_w
        dat_pkg_group_end := pkg_group_end_w
        dat_pkg_layer_end := pkg_layer_end_w
        dat_pkg_dat_release := ~io.reg2dp_skip_data_rls & pkg_layer_end_w
    }

    // PKT_PACK_WIRE( csc_dat_pkg ,  dat_pkg_ ,  dat_pkg_pd )
    val wt_pkg_kernel_size = RegInit("b0".asUInt(7.W))
    val wt_pkg_weight_size = RegInit("b0".asUInt(7.W))
    val wt_pkg_cur_sub_h = RegInit("b0".asUInt(3.W))
    val wt_pkg_wt_release = RegInit(false.B)

    val dat_pkg_pd = Cat(dat_pkg_dat_release, dat_pkg_layer_end, dat_pkg_group_end, dat_pkg_channel_end, dat_pkg_block_end,
                        dat_pkg_cur_sub_h(1, 0), dat_pkg_stripe_length(6, 0), dat_pkg_channel_size(6, 0), 
                        dat_pkg_h_offset(4, 0), dat_pkg_w_offset(4, 0))

    val dat_push_data = Cat(pkg_idx, dat_pkg_pd)

    when(pkg_adv){
        wt_pkg_kernel_size := cur_kernel
        wt_pkg_weight_size := cur_channel
        wt_pkg_cur_sub_h := cur_r
        wt_pkg_wt_release := ~io.reg2dp_skip_weight_rls & pkg_group_end_w
    }

    val wt_pkg_channel_end = dat_pkg_channel_end
    val wt_pkg_group_end = dat_pkg_group_end

    // PKT_PACK_WIRE( csc_wt_pkg ,  wt_pkg_ ,  wt_pkg_pd )
    val wt_pkg_pd = Cat(wt_pkg_wt_release, wt_pkg_group_end, wt_pkg_channel_end, 
                        wt_pkg_cur_sub_h(1, 0), wt_pkg_kernel_size(5, 0), wt_pkg_weight_size)

    val wt_push_data = Cat(pkg_idx, wt_pkg_pd)
    ////////////////////////////////////////////////////////////////////////
    //  package fifos                                                     //
    ////////////////////////////////////////////////////////////////////////
    val dat_push_req = pkg_vld & wt_push_ready
    val wt_push_req = pkg_vld & dat_push_ready
    val wt_pop_ready = Wire(Bool())

    val u_dat_fifo = Module(new NV_NVDLA_fifo(depth = 4, width = 33,
                        ram_type = 2, distant_wr_req = true,
                        io_wr_empty = true ))
    u_dat_fifo.io.clk := io.nvdla_core_clk
    u_dat_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_dat_fifo.io.wr_pvld := dat_push_req
    dat_push_ready := u_dat_fifo.io.wr_prdy
    u_dat_fifo.io.wr_pd := dat_push_data
    dat_pop_req := u_dat_fifo.io.rd_pvld
    u_dat_fifo.io.rd_prdy := dat_pop_ready
    val dat_pop_data = u_dat_fifo.io.rd_pd
    dat_push_empty := u_dat_fifo.io.wr_empty.get

    val u_wt_fifo = Module(new NV_NVDLA_fifo(depth = 4, width = 20,
                        ram_type = 2, distant_wr_req = true,
                        io_wr_empty = true ))
    u_wt_fifo.io.clk := io.nvdla_core_clk
    u_wt_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_wt_fifo.io.wr_pvld := wt_push_req
    wt_push_ready := u_wt_fifo.io.wr_prdy
    u_wt_fifo.io.wr_pd := wt_push_data
    wt_pop_req := u_wt_fifo.io.rd_pvld
    u_wt_fifo.io.rd_prdy := wt_pop_ready
    val wt_pop_data = u_wt_fifo.io.rd_pd
    wt_push_empty := u_wt_fifo.io.wr_empty.get
    
    ////////////////////////////////////////////////////////////////////////
    //  issue control logic                                               //
    ////////////////////////////////////////////////////////////////////////
    val dat_pop_idx = dat_pop_data(32, 31)
    val dat_pop_pd = dat_pop_data(30, 0)
    val wt_pop_idx = wt_pop_data(19, 18)
    val wt_pop_pd = wt_pop_data(17, 0)

    // PKT_UNPACK_WIRE( csc_dat_pkg ,  sg2dat_ ,  dat_pop_pd )
    val sg2dat_w_offset = dat_pop_pd(4, 0)
    val sg2dat_h_offset = dat_pop_pd(9, 5)
    val sg2dat_channel_size = dat_pop_pd(16, 10)
    val sg2dat_stripe_length = dat_pop_pd(23, 17)
    val sg2dat_cur_sub_h = dat_pop_pd(25, 24)
    val sg2dat_block_end = dat_pop_pd(26)
    val sg2dat_channel_end = dat_pop_pd(27)
    val sg2dat_group_end = dat_pop_pd(28)
    sg2dat_layer_end := dat_pop_pd(29)
    val sg2dat_dat_release = dat_pop_pd(30)

    // PKT_UNPACK_WIRE( csc_wt_pkg ,  sg2wt_ ,  wt_pop_pd )
    val sg2wt_weight_size = wt_pop_pd(6, 0)
    val sg2wt_kernel_size = wt_pop_pd(12, 7)
    val sg2wt_cur_sub_h = wt_pop_pd(14, 13)
    val sg2wt_channel_end = wt_pop_pd(15)
    val sg2wt_group_end = wt_pop_pd(16)
    val sg2wt_wt_release = wt_pop_pd(17)

    val sg2wt_kernel_size_inc = sg2wt_kernel_size + 1.U
    val dat_stripe_batch_size_w = sg2dat_stripe_length 
    val dat_stripe_img_size_w = sg2dat_stripe_length
    val dat_stripe_size_w = Mux(is_img_d1, dat_stripe_img_size_w, dat_stripe_batch_size_w)
    val dat_stripe_img_length_w = Mux(~is_img_d1, "b0".asUInt(7.W), 
                                  Mux(io.reg2dp_y_extension === "h2".asUInt(2.W), ((sg2dat_stripe_length +& "h3".asUInt(2.W)) & "hfc".asUInt(8.W))(6,0),
                                  Mux(io.reg2dp_y_extension === "h1".asUInt(2.W), ((sg2dat_stripe_length +& "h1".asUInt(2.W)) & "hfe".asUInt(8.W))(6,0),
                                  sg2dat_stripe_length)))
                                  
    val dat_stripe_length_w = Mux(is_img_d1, dat_stripe_img_length_w, dat_stripe_batch_size_w)

    //delay for one cycle
    val wt_pop_ready_d1 = RegInit(false.B)
    val dat_stripe_length = RegInit("b0".asUInt(7.W))
    val pop_cnt = RegInit("b0".asUInt(6.W))
    val sg2dl_pvld_out = RegInit(false.B)
    val sg2dl_pd_out = RegInit("b0".asUInt(31.W))
    val sg2wl_pvld_out = RegInit(false.B)
    val sg2wl_pd_out = RegInit("b0".asUInt(18.W))
    val credit_ready = Wire(Bool())

    val dat_max_cycles = Mux(~dat_pop_ready, "b0".asUInt(7.W), 
                         Mux((dat_stripe_length < conf.CSC_MIN_STRIPE.U), conf.CSC_MIN_STRIPE.U,
                         dat_stripe_length
                         ))
    val wt_cycles = sg2wt_kernel_size
    val wt_max_cycles = Mux(~wt_pop_ready, "b0".asUInt(6.W), 
                         Mux((wt_cycles <= "b1".asUInt(6.W)) & (pop_cnt <= "b1".asUInt(6.W)), "h2".asUInt(6.W),
                         Mux((wt_cycles > pop_cnt), wt_cycles, pop_cnt
                         )))
    val max_cycles = Mux(dat_max_cycles >= wt_max_cycles,  (dat_max_cycles - 1.U), (wt_max_cycles - 1.U))(5, 0)
    val pop_cnt_dec = pop_cnt - 1.U
    val pop_cnt_w = Mux((dat_pop_ready|wt_pop_ready), max_cycles, Mux(pop_cnt===0.U, 0.U, pop_cnt_dec))
    wt_pop_ready := wt_pop_req & (((pop_cnt === 0.U) & credit_ready) | (dat_pop_idx === wt_pop_idx))
    dat_pop_ready := dat_pop_req & (pop_cnt === 0.U) & credit_ready & ((dat_pop_idx =/= wt_pop_idx) | ~wt_pop_req)


    wt_pop_ready_d1 := wt_pop_ready 
    when(wt_pop_ready_d1){
        dat_stripe_size := dat_stripe_size_w
        dat_stripe_length := dat_stripe_length_w   
    }  
    pop_cnt := pop_cnt_w
    sg2dl_pvld_out := dat_pop_ready
    when(dat_pop_ready){
        sg2dl_pd_out := dat_pop_pd
    }
    sg2wl_pvld_out := wt_pop_ready
    when(wt_pop_ready){
        sg2wl_pd_out := wt_pop_pd
    }

    io.sg2dl.pd.valid := sg2dl_pvld_out
    io.sg2dl.pd.bits := sg2dl_pd_out
    io.sg2wl.pd.valid := sg2wl_pvld_out
    io.sg2wl.pd.bits := sg2wl_pd_out

    ////////////////////////////////////////////////////////////////////////
    //  credit controll logic                                             //
    ////////////////////////////////////////////////////////////////////////
    //================  Non-SLCG clock domain ================//
    val credit_cnt = withClock(io.nvdla_core_ng_clk){RegInit(conf.NVDLA_CC_CREDIT_SIZE.U)}
    val credit_vld = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}
    val credit_size = withClock(io.nvdla_core_ng_clk){Reg(UInt(3.W))}

    credit_vld := io.accu2sc_credit_size.valid
    when(io.accu2sc_credit_size.valid){
        credit_size := io.accu2sc_credit_size.bits
    }

    val dat_impact_cnt = Cat("b0".asUInt(2.W), dat_stripe_size)
    val credit_req_size = dat_impact_cnt
    val credit_cnt_add = Mux(credit_vld,  credit_size,  "b0".asUInt(4.W))
    val credit_cnt_dec = Mux(dat_pop_ready&sg2dat_channel_end, dat_impact_cnt, "b0".asUInt(9.W))
    credit_ready := ~sg2dat_channel_end | (credit_cnt >= credit_req_size)

    when(dat_pop_ready | credit_vld){
        credit_cnt := credit_cnt + credit_cnt_add - credit_cnt_dec
    }

    ////////////////////////////////////////////////////////////////////////
    //  convolution buffer local status                                   //
    ////////////////////////////////////////////////////////////////////////
    val dat_release = pkg_adv & pkg_layer_end_w & ~io.reg2dp_skip_data_rls
    val dat_reuse_release = is_idle & io.reg2dp_op_en & (~io.reg2dp_data_reuse | is_mode_change) & (last_slices.orR);
    val slices_avl_add = Mux(io.cdma2sc_dat_updt.valid, io.cdma2sc_dat_updt.bits.slices, "b0".asUInt(14.W));
    val slices_avl_sub = Mux(dat_release, rls_slices, Mux(dat_reuse_release, last_slices, "b0".asUInt(14.W)));
    val wt_release = pkg_adv & ~io.reg2dp_skip_weight_rls & pkg_group_end_w;
    val wt_reuse_release = is_idle & io.reg2dp_op_en & ~io.reg2dp_weight_reuse & last_skip_weight_rls;
    val kernels_avl_add = Mux(io.cdma2sc_wt_updt.valid, io.cdma2sc_wt_updt.bits.kernels, "b0".asUInt(14.W));
    val kernels_avl_sub = Mux(wt_release, Cat("b0".asUInt(7.W), cur_kernel), Mux(wt_reuse_release, last_kernels, "b0".asUInt(14.W)));

    when(dat_pending_req | dat_release | dat_reuse_release | io.cdma2sc_dat_updt.valid){
        slices_avl := Mux(dat_pending_req, "b0".asUInt(14.W), (slices_avl + slices_avl_add - slices_avl_sub))
    }
    when(wt_pending_req | wt_release | wt_reuse_release | io.cdma2sc_wt_updt.valid){
        kernels_avl := Mux(wt_pending_req, "b0".asUInt(14.W), kernels_avl + kernels_avl_add - kernels_avl_sub);
    }

    io.sg2dl.reuse_rls := withClock(io.nvdla_core_ng_clk){RegNext(dat_reuse_release, false.B)}
    io.sg2wl.reuse_rls := withClock(io.nvdla_core_ng_clk){RegNext(wt_reuse_release, false.B)}

    //================  Non-SLCG clock domain end ================//

    //////////////////////////////////////////////////////////////
    ///// functional point                                   /////
    //////////////////////////////////////////////////////////////

}}



object NV_NVDLA_CSC_sgDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CSC_sg())
}

    

    



