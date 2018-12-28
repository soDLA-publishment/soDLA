package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CSC_WL_dec(implicit val conf: cscConfiguration) extends RawModule {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        val sg2wl_pvld = Input(Bool())  /* data valid */
        val sg2wl_pd = Input(UInt(18.W))

        val sc_state = Input(UInt(2.W))
        val sg2wl_reuse_rls = Input(Bool())
        val sc2cdma_wt_pending_req = Input(Bool())

        val cdma2sc_wt_updt = Input(Bool())  /* data valid */
        val cdma2sc_wt_kernels = Input(UInt(14.W))
        val cdma2sc_wt_entries = Input(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
        val cdma2sc_wmb_entries = Input(UInt(9.W))

        val sc2cdma_wt_updt = Output(Bool())     /* data valid */
        val sc2cdma_wt_kernels = Output(UInt(14.W))
        val sc2cdma_wt_entries = Output(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
        val sc2cdma_wmb_entries = Output(UInt(9.W))
        
        val sc2buf_wt_rd_en = Output(Bool()) 
        val sc2buf_wt_rd_addr = Output(UInt(conf.CBUF_ADDR_WIDTH.W))

        val sc2buf_wt_rd_valid = Input(Bool()) 
        val sc2buf_wt_rd_data = Input(UInt(conf.CBUF_ENTRY_BITS.W))

        if(conf.CBUF_WEIGHT_COMPRESSED){
        val sc2buf_wmb_rd_en = Output(Bool())      /* data valid */
        val sc2buf_wmb_rd_addr = Output(UInt(conf.CBUF_ADDR_WIDTH.W))
        val sc2buf_wmb_rd_valid = Input(Bool())   /* data valid */
        val sc2buf_wmb_rd_data = Input(UInt(conf.CBUF_ENTRY_BITS.W))     
        }
     
        val sc2mac_wt_a_pvld = Output(Bool())      /* data valid */
        val sc2mac_wt_b_pvld = Output(Bool())      /* data valid */
        val sc2mac_wt_a_mask = Output(UInt(conf.CSC_ATOMC.W))
        val sc2mac_wt_b_mask = Output(UInt(conf.CSC_ATOMC.W))
        val sc2mac_wt_a_sel = Output(UInt(conf.CSC_ATOMK_HF.W))
        val sc2mac_wt_b_sel = Output(UInt(conf.CSC_ATOMK_HF.W))
        val sc2mac_wt_a_data = Output(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))
        val sc2mac_wt_b_data = Output(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))     

        val nvdla_core_ng_clk = Input(Clock())  

        val reg2dp_op_en = Input(Bool())
        val reg2dp_in_precision = Input(UInt(2.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_y_extension = Input(UInt(2.W))
        val reg2dp_weight_reuse = Input(Bool())
        val reg2dp_skip_weight_rls = Input(Bool())
        val reg2dp_weight_format = Input(Bool())
        val reg2dp_weight_bytes = Input(UInt(32.W))
        val reg2dp_wmb_bytes = Input(UInt(28.W))
        val reg2dp_data_bank = Input(UInt(5.W))
        val reg2dp_weight_bank = Input(UInt(5.W)) 

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
withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Pipeline of Weight loader, for both compressed weight and uncompressed weight
    //
    //                      input_package--------------
    //                           |                    |
    //                      WMB_request               |
    //                           |                    |
    //                      conv_buffer               |
    //                           |                    |
    //                      WMB_data ---------> weight_request
    //                           |                    |
    //                           |              conv_buffer
    //                           |                    |
    //                           |              weight_data   
    //                           |                    |
    //                           |              weight_data   
    //                           |                    |
    //                           |------------> weight_decompressor
    //                                                |
    //                                          weight_to_MAC_cell
    //
    /////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////
    ///// status from sequence generator                     /////
    //////////////////////////////////////////////////////////////
    val is_sg_idle = (io.sc_state === 0.U)
    val is_sg_pending = (sc_state === 1.U)
    val is_sg_running = (io.sc_state === 2.U)
    val is_sg_done = (io.sc_state === 3.U)
    val addr_init = is_sg_running & ~is_sg_running_d1

    val is_sg_running_d1 = RegInit(false.B)
    is_sg_running_d1 := is_sg_running

    //////////////////////////////////////////////////////////////
    ///// input signals from registers                       /////
    //////////////////////////////////////////////////////////////
    val layer_st = io.reg2dp_op_en & is_sg_idle
    val data_bank_w = (io.reg2dp_data_bank + 1.U)(4, 0)
    val weight_bank_w = (io.reg2dp_weight_bank + 1.U)(4, 0)
    val is_int8 = (io.reg2dp_proc_precision === "b0".asUInt(2.W)) 
    val is_compressed = (io.reg2dp_weight_format === "b1".asUInt(1.W))
    val sub_h_total_w =  (9.U << io.reg2dp_y_extension)(5, 3)
    val last_wmb_entries_w = Mux(is_compressed_d1, io.reg2dp_wmb_bytes(8 + LOG2_ATOMC, LOG2_ATOMC), "b0".asUInt(9.W))

    val data_bank = RegInit(Fill(5, false.B))
    val weight_bank = RegInit(Fill(5, false.B))
    val last_weight_entries = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
    val last_wmb_entries = RegInit(Fill(9, false.B))
    val sub_h_total = RegInit("b1".asUInt(3.W))
    val is_compressed_d1 = RegInit(false.B)

    when(layer_st){
        data_bank := data_bank_w
        weight_bank := weight_bank_w
        sub_h_total := sub_h_total_w
        is_compressed_d1 := is_compressed   
    }
    when(is_sg_done & io.reg2dp_skip_weight_rls){
        last_weight_entries := io.reg2dp_weight_bytes(conf.CSC_ENTRIES_NUM_WIDTH-1+conf.LOG2_ATOMC, conf.LOG2_ATOMC)
        last_wmb_entries := last_wmb_entries_w
    }

    //////////////////////////////////////////////////////////////
    ///// cbuf status management                             /////
    //////////////////////////////////////////////////////////////
    val cbuf_reset = io.sc2cdma_wt_pending_req


    //////////////////////////////////// calculate avaliable weight entries ////////////////////////////////////
    //================  Non-SLCG clock domain ================//
withClock(io.nvdla_core_ng_clk){
    val wt_rls = Wire(Bool())
    val wt_rls_wt_entries = Wire(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val wt_entry_avl = = RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))

    val wt_entry_avl_add = Mux(io.cdma2sc_wt_updt, io.cdma2sc_wt_entries, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val wt_entry_avl_sub = Mux(wt_rls, wt_rls_wt_entries, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val wt_entry_avl_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), wt_entry_avl + wt_entry_avl_add - wt_entry_avl_sub)

    //////////////////////////////////// calculate avaliable wmb entries ////////////////////////////////////
    val wt_rls_wmb_entries = Wire(UInt(9.W))
    val wmb_entry_avl = RegInit("b0".asUInt(9.W))

    val wmb_entry_avl_add = Mux(io.cdma2sc_wt_updt, io.cdma2sc_wmb_entries, "b0".asUInt(9.W))
    val wmb_entry_avl_sub = Mux(wt_rls, wt_rls_wmb_entries, "b0".asUInt(9.W))
    val wmb_entry_avl_w = Mux(cbuf_reset, "b0".asUInt(9.W), wmb_entry_avl + wmb_entry_avl_add - wmb_entry_avl_sub)
    
    //////////////////////////////////// calculate weight entries start offset ////////////////////////////////////
    val wt_entry_st = RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W)) 
    val wt_rls_wt_entries = Wire(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))  

    val wt_entry_st_inc = wt_entry_st + wt_rls_wt_entries
    val wt_entry_st_inc_wrap = wt_entry_st_inc - Cat("b0".asUInt(1.W), weight_bank, "b0".asUInt(conf.LOG2_CBUF_BANK_DEPTH.W))
    val is_wt_entry_st_wrap = (wt_entry_st_inc >=  Cat("b0".asUInt(1.W), weight_bank, "b0".asUInt(conf.LOG2_CBUF_BANK_DEPTH.W)))
    val wt_entry_st_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(~wt_rls, wt_entry_st, Mux(is_wt_entry_st_wrap, wt_entry_st_inc_wrap, wt_entry_st_inc)))

    //////////////////////////////////// calculate weight entries end offset ////////////////////////////////////
    val wt_entry_end = Reg(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W)) 

    val wt_entry_end_inc = wt_entry_end + io.cdma2sc_wt_entries
    val wt_entry_end_inc_wrap = wt_entry_end_inc - Cat("b0".asUInt(1.W), weight_bank, "b0".asUInt(conf.LOG2_CBUF_BANK_DEPTH.W))
    val is_wt_entry_end_wrap = (wt_entry_end_inc >=  Cat("b0".asUInt(1.W), weight_bank, "b0".asUInt(conf.LOG2_CBUF_BANK_DEPTH.W)))
    val wt_entry_end_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(is_wt_entry_end_wrap, wt_entry_end_inc_wrap, wt_entry_end_inc)) 

    //////////////////////////////////// calculate wmb entries start offset ////////////////////////////////////
    val wmb_entry_st = RegInit("b0".asUInt(9.W)) 
    val wt_rls_wmb_entries = Wire(UInt(9.W))  

    val wmb_entry_st_inc = wmb_entry_st + wt_rls_wmb_entries
    val wmb_entry_st_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(~wt_rls, wmb_entry_st, wmb_entry_st_inc))

    //////////////////////////////////// calculate wmb entries end offset ////////////////////////////////////
    val wmb_entry_end = RegInit("b0".asUInt(9.W))

    val wmb_entry_end_inc = wmb_entry_end + io.cdma2sc_wmb_entries
    val wmb_entry_end_w = Mux(cbuf_reset, "b0".asUInt(9.W), wmb_entry_end_inc)

    //////////////////////////////////// registers and assertions ////////////////////////////////////
    when(io.cdma2sc_wt_updt | wt_rls | cbuf_reset){
        wt_entry_avl := wt_entry_avl_w 
        wmb_entry_avl := wmb_entry_avl_w
    }
    when(cbuf_reset | wt_rls){
        wt_entry_st := wt_entry_st_w
        wmb_entry_st := wmb_entry_st_w
    }
    when(cbuf_reset | io.cdma2sc_wt_updt){
        wt_entry_end := wt_entry_end_w
        wmb_entry_end := wmb_entry_end_w
    }
    }
    //================  Non-SLCG clock domain end ================//

    //////////////////////////////////////////////////////////////
    ///// cbuf status update                                 /////
    //////////////////////////////////////////////////////////////
    val wt_rsp_pipe_pvld = Wire(Bool())
    val wt_rsp_rls = Wire(Bool())
    val wt_rsp_wt_rls_entries = Wire(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val wt_rsp_wmb_rls_entries = Wire(UInt(9.W))

    val sc2cdma_wt_updt_out = RegInit(false.B)
    val sc2cdma_wt_entries_out = RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val sc2cdma_wmb_entries_out = RegInit("b0".asUInt(9.W))

    val sub_rls = wt_rsp_pipe_pvld & wt_rsp_rls
    val sub_rls_wt_entries = wt_rsp_wt_rls_entries
    val sub_rls_wmb_entries = wt_rsp_wmb_rls_entries
    val reuse_rls = io.sg2wl_reuse_rls
    val wt_rls = reuse_rls | sub_rls
    val wt_rls_wt_entries = Mux(reuse_rls, last_weight_entries, sub_rls_wt_entries)
    val wt_rls_wmb_entries = Mux(reuse_rls, last_wmb_entries, sub_rls_wmb_entries)
    val wt_rls_updt = wt_rls

    sc2cdma_wt_updt_out := wt_rls_updt
    when(wt_rls_updt){
        sc2cdma_wt_entries_out := wt_rls_wt_entries
        sc2cdma_wmb_entries_out := wt_rls_wmb_entries
    }

    io.sc2cdma_wt_updt := sc2cdma_wt_updt_out
    io.sc2cdma_wt_entries := sc2cdma_wt_entries_out
    io.sc2cdma_wmb_entries := sc2cdma_wmb_entries_out

    //sc2cmda_wt_kernels is useless
    io.sc2cdma_wt_kernels := "b0".asUInt(14.W)

    //////////////////////////////////////////////////////////////
    ///// input data package                                 /////
    //////////////////////////////////////////////////////////////
    if(conf.CSC_WL_PIPELINE_ADDITION == 0){
        val wl_in_pvld = io.sg2wl_pvld
        val wl_in_pd = io.sg2wl_pd
    }
    else{
        val wl_in_pvld = ShiftRegister(io.sg2wl_pvld, conf.CSC_WL_PIPELINE_ADDITION, false.B)
        val wl_in_pd = ShiftRegister(io.sg2wl_pd, conf.CSC_WL_PIPELINE_ADDITION, "b0".asUInt(18.W))        
    }

    val wl_pvld = wl_in_pvld
    val wl_pd = wl_in_pd

    // PKT_UNPACK_WIRE( csc_wt_pkg ,  wl_ ,  wl_pd )
    val wl_weight_size = wl_pd(6, 0)
    val wl_kernel_size = wl_pd(12, 7)
    val wl_cur_sub_h = wl_pd(14, 13)
    val wl_channel_end = wl_pd(15)
    val wl_group_end = wl_pd(16)
    val wl_wt_release = wl_pd(17)

    //////////////////////////////////////////////////////////////
    ///// generate wmb read request                          /////
    //////////////////////////////////////////////////////////////

    //////////////////////////////////// generate wmb_pipe_valid siganal ////////////////////////////////////
    val is_stripe_end = (stripe_cnt_inc === stripe_length)
    val stripe_cnt = RegInit("b0".asUInt(5.W))
    val wmb_pipe_valid_d1 = RegInit(false.B)

    val stripe_cnt_inc = stripe_cnt + 1.U
    val stripe_cnt_w = Mux(layer_st, "b0".asUInt(5.W), Mux(is_stripe_end, "b0".asUInt(5.W), stripe_cnt_inc))
    val stripe_length = wl_kernel_size(4, 0)
    val is_stripe_st = wl_pvld
    val wmb_pipe_valid = Mux(wl_pvld, true.B, Mux(~stripe_cnt.orR, false.B, wmb_pipe_valid_d1))
    val stripe_cnt_reg_en = layer_st | wmb_pipe_valid

    when(stripe_cnt_reg_en){
        stripe_cnt:= stripe_cnt_w
    }

    //////////////////////////////////// generate wmb_req_valid siganal ////////////////////////////////////
    val wmb_req_valid = Wire(Bool())
    val wmb_req_element = Wire(UInt(8.W))
    val wmb_element_avl = RegInit("b0".asUInt(11.W))
    val wmb_element_avl_last = RegInit("b0".asUInt(11.W))

    val wmb_element_avl_add = Mux(~wmb_req_valid, "b0".asUInt(11.W), conf.CSC_WMB_ELEMENTS.U)
    val wmb_element_avl_sub = Mux(wmb_pipe_valid, wmb_req_element, "h0".asUInt(8.W))
    val wmb_element_avl_inc = wmb_element_avl + wmb_element_avl_add - wmb_element_avl_sub
    val wmb_element_avl_w = Mux(layer_st, "b0".asUInt(11.W), Mux(is_stripe_end & ~wl_group_end & wl_channel_end, wmb_element_avl_last, wmb_element_avl_inc))
    val wmb_req_ori_element = wl_weight_size
    val wmb_req_cycle_element = Cat("b0".asUInt(1.W), wl_weight_size)

    val wmb_req_element = MuxLookup(wl_cur_sub_h,  Cat(wmb_req_cycle_element(5, 0), "b0".asUInt(2.W)),
                                Seq(
                                    "h0".asUInt(2.W) -> wmb_req_cycle_element,
                                    "h1".asUInt(2.W) -> Cat(wmb_req_cycle_element(6, 0), "b0".asUInt(1.W)),
                                    "h2".asUInt(2.W) -> Cat(wmb_req_cycle_element(6, 0), "b0".asUInt(1.W)) + wmb_req_cycle_element                                 
                                ))

    wmb_req_valid := wmb_pipe_valid & is_compressed_d1 & (wmb_element_avl < Cat("b0".asUInt(3.W), wmb_req_element))
    val wmb_element_avl_reg_en = layer_st | (wmb_pipe_valid & is_compressed_d1)
    val wmb_element_avl_last_reg_en = layer_st | (wmb_pipe_valid & is_compressed_d1 & is_stripe_end & wl_group_end)

    when(wmb_element_avl_reg_en){
        wmb_element_avl := wmb_element_avl_w
    }
    when(wmb_element_avl_last_reg_en){
        wmb_element_avl_last := wmb_element_avl_w
    }

    //////////////////////////////////// generate wmb read address ////////////////////////////////////
    val wmb_req_addr = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))
    val wmb_req_addr_last = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))

    val wmb_req_addr_inc = wmb_req_addr + 1.U
    val wmb_req_addr_w = Mux(addr_init, Cat("b0".asUInt((conf.CBUF_ADDR_WIDTH-9).W), wmb_entry_st_w), 
                         Mux(is_stripe_end & wl_channel_end & ~wl_group_end, wmb_req_addr_last, 
                         Mux(wmb_req_valid, wmb_req_addr_inc, wmb_req_addr)))
    val wmb_req_addr_reg_en = is_compressed_d1 & (addr_init | wmb_req_valid | (wmb_pipe_valid & is_stripe_end & wl_channel_end))
    val wmb_req_addr_last_reg_en = is_compressed_d1 & (addr_init | (wmb_pipe_valid & is_stripe_end & wl_group_end))

    when(wmb_req_addr_reg_en){
        wmb_req_addr := wmb_req_addr_w
    }
    when(wmb_req_addr_last_reg_en){
        wmb_req_addr_last := wmb_req_addr_w
    }

    //////////////////////////////////// wmb entries counter for release ////////////////////////////////////
    val wmb_rls_cnt_vld = RegInit(false.B)
    val wmb_rls_cnt = RegInit("b0".asUInt(9.W))

    val wmb_rls_cnt_vld_w = Mux(layer_st | (wl_group_end & is_stripe_end), false.B, 
                            Mux(wl_channel_end & is_stripe_end, true.B, wmb_rls_cnt_vld))
    val wmb_rls_cnt_inc = wmb_rls_cnt + 1.U
    val wmb_rls_cnt_w = Mux(layer_st, "b0".asUInt(9.W), 
                        Mux(is_stripe_end & wl_group_end, "b0".asUInt(9.W), wmb_rls_cnt_inc))   
    val wmb_rls_cnt_reg_en = layer_st |(is_compressed_d1 & wmb_pipe_valid & is_stripe_end & wl_group_end) |(is_compressed_d1 & wmb_req_valid & ~wmb_rls_cnt_vld)
    val wmb_rls_entries = Mux(wmb_rls_cnt_vld | ~wmb_req_valid), wmb_rls_cnt, wmb_rls_cnt_inc)

    wmb_rls_cnt_vld := wmb_rls_cnt_vld_w
    when(wmb_rls_cnt_reg_en){
        wmb_rls_cnt := wmb_rls_cnt_w
    }

    //////////////////////////////////// send wmb read request ////////////////////////////////////
    val sc2buf_wmb_rd_en_out = RegInit(false.B)
    val sc2buf_wmb_rd_addr_out = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))
    val wmb_req_ori_element_d1 = RegInit("b0".asUInt(7.W))
    val wmb_req_element_d1 = RegInit("b0".asUInt(8.W))
    val wmb_req_rls_entries_d1 = RegInit("b0".asUInt(9.W))
    val wmb_req_stripe_end_d1 = RegInit(false.B)
    val wmb_req_channel_end_d1 = RegInit(false.B) 
    val wmb_req_group_end_d1 = RegInit(false.B)
    val wmb_req_rls_d1 = RegInit(false.B)
    val wmb_req_cur_sub_h_d1 = RegInit("b0".asUInt(2.W))

    sc2buf_wmb_rd_en_out := wmb_req_valid
    when(wmb_req_valid){
        sc2buf_wmb_rd_addr_out := wmb_req_addr
    }
    wmb_pipe_valid_d1 := wmb_pipe_valid
    when(wmb_pipe_valid){
        wmb_req_ori_element_d1 := wmb_req_ori_element
        wmb_req_element_d1 := wmb_req_element
    }
    when(wmb_pipe_valid & wl_wt_release & is_stripe_end){
        wmb_req_rls_entries_d1 := wmb_rls_entries
    }
    when(wmb_pipe_valid){
        wmb_req_stripe_end_d1 := is_stripe_end
        wmb_req_channel_end_d1 := wl_channel_end & is_stripe_end
        wmb_req_group_end_d1 := wl_group_end & is_stripe_end
        wmb_req_rls_d1 := wl_wt_release & is_stripe_end
        wmb_req_cur_sub_h_d1 := wl_cur_sub_h
    }

    if(conf.CBUF_WEIGHT_COMPRESSED){
       io.sc2buf_wmb_rd_en := sc2buf_wmb_rd_en_out
       io.sc2buf_wmb_rd_addr := sc2buf_wmb_rd_addr_out
    }

    //////////////////////////////////////////////////////////////
    ///// sideband pipeline for wmb read                     /////
    //////////////////////////////////////////////////////////////
    val wmb_req_pipe_pvld = wmb_pipe_valid_d1
    val wmb_req_d1_stripe_end = wmb_req_stripe_end_d1
    val wmb_req_d1_channel_end = wmb_req_channel_end_d1
    val wmb_req_d1_group_end =  wmb_req_group_end_d1
    val wmb_req_d1_rls = wmb_req_rls_d1
    val wmb_req_d1_cur_sub_h = wmb_req_cur_sub_h_d1
    val wmb_req_d1_element = wmb_req_element_d1
    val wmb_req_d1_ori_element = wmb_req_ori_element_d1
    val wmb_req_d1_rls_entries = wmb_req_rls_entries_d1

    // PKT_PACK_WIRE( csc_wmb_req_pkg ,  wmb_req_d1_ ,  wmb_req_pipe_pd )
    val wmb_req_pipe_pd = Cat(wmb_req_d1_cur_sub_h, "b0".asUInt(1.W), wmb_req_d1_rls, wmb_req_d1_group_end, wmb_req_d1_channel_end
                            , wmb_req_d1_stripe_end, wmb_req_d1_rls_entries, wmb_req_d1_element, wmb_req_d1_ori_element)

    if(conf.NVDLA_CBUF_READ_LATENCY == 0){
        val wl_in_pvld = io.sg2wl_pvld
        val wl_in_pd = io.sg2wl_pd
    }
    else{
        val wl_in_pvld = ShiftRegister(io.sg2wl_pvld, conf.CSC_WL_PIPELINE_ADDITION, false.B)
        val wl_in_pd = ShiftRegister(io.sg2wl_pd, conf.CSC_WL_PIPELINE_ADDITION, "b0".asUInt(18.W))        
    }
    
    











    


































    
























    







}}

