package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CSC_wlIO(implicit conf: nvdlaConfig) extends Bundle{

    //clock
    val nvdla_core_clk = Input(Clock())    
    val nvdla_core_ng_clk = Input(Clock())    

    val sg2wl = Flipped(new csc_sg2wl_if)   /* data valid */

    val sc_state = Input(UInt(2.W))

    val sc2cdma_wt_pending_req = Input(Bool())

    val cdma2sc_wt_updt = Flipped(ValidIO(new updt_entries_kernels_if))  /* data valid */
    val cdma2sc_wmb_entries = Input(UInt(9.W))
    val sc2cdma_wt_updt =  ValidIO(new updt_entries_kernels_if)    /* data valid */
    val sc2cdma_wmb_entries = Output(UInt(9.W))

    val sc2buf_wt_rd = new sc2buf_wt_rd_if
    val sc2mac_wt_a = ValidIO(new csc2cmac_wt_if)     /* data valid */
    val sc2mac_wt_b = ValidIO(new csc2cmac_wt_if)     /* data valid */
    
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

}

class NV_NVDLA_CSC_wl(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new NV_NVDLA_CSC_wlIO)
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
    val sc2buf_wmb_rd_valid = false.B
    val sc2buf_wmb_rd_data = Fill(conf.CBUF_ENTRY_BITS, false.B)

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
    val is_sg_running_d1 = RegInit(false.B)

    val is_sg_idle = (io.sc_state === 0.U)
    val is_sg_pending = (io.sc_state === 1.U)
    val is_sg_running = (io.sc_state === 2.U)
    val is_sg_done = (io.sc_state === 3.U)
    val addr_init = is_sg_running & ~is_sg_running_d1
    
    is_sg_running_d1 := is_sg_running

    //////////////////////////////////////////////////////////////
    ///// input signals from registers                       /////
    //////////////////////////////////////////////////////////////
    val data_bank = RegInit(Fill(5, false.B))
    val weight_bank = RegInit(Fill(5, false.B))
    val last_weight_entries = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
    val last_wmb_entries = RegInit(Fill(9, false.B))
    val sub_h_total = RegInit("b1".asUInt(3.W))
    val is_compressed_d1 = RegInit(false.B)

    val layer_st = io.reg2dp_op_en & is_sg_idle
    val is_int8 = (io.reg2dp_proc_precision === "b0".asUInt(2.W)) 
    val is_compressed = (io.reg2dp_weight_format === "b1".asUInt(1.W))

    when(layer_st){
        data_bank := io.reg2dp_data_bank + 1.U
        weight_bank := io.reg2dp_weight_bank + 1.U
        sub_h_total := ("h9".asUInt(6.W) << io.reg2dp_y_extension)(5, 3)
        is_compressed_d1 := is_compressed   
    }
    when(is_sg_done & io.reg2dp_skip_weight_rls){
        last_weight_entries := io.reg2dp_weight_bytes(conf.CSC_ENTRIES_NUM_WIDTH-1+conf.LOG2_ATOMC, conf.LOG2_ATOMC)
        last_wmb_entries := Mux(is_compressed_d1, io.reg2dp_wmb_bytes(8 + conf.LOG2_ATOMC, conf.LOG2_ATOMC), "b0".asUInt(9.W))
    }

    //////////////////////////////////////////////////////////////
    ///// cbuf status management                             /////
    //////////////////////////////////////////////////////////////
    val cbuf_reset = io.sc2cdma_wt_pending_req
    //////////////////////////////////// calculate avaliable weight entries ////////////////////////////////////
    //================  Non-SLCG clock domain ================//
    val wt_rls = Wire(Bool())
    val wt_rls_wt_entries = Wire(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val wt_entry_avl = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))}

    val wt_entry_avl_add = Mux(io.cdma2sc_wt_updt.valid, io.cdma2sc_wt_updt.bits.entries, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val wt_entry_avl_sub = Mux(wt_rls, wt_rls_wt_entries, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val wt_entry_avl_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), wt_entry_avl + wt_entry_avl_add - wt_entry_avl_sub)

    //////////////////////////////////// calculate avaliable wmb entries ////////////////////////////////////
    val wt_rls_wmb_entries = Wire(UInt(9.W))
    val wmb_entry_avl = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(9.W))}

    val wmb_entry_avl_add = Mux(io.cdma2sc_wt_updt.valid, io.cdma2sc_wmb_entries, "b0".asUInt(9.W))
    val wmb_entry_avl_sub = Mux(wt_rls, wt_rls_wmb_entries, "b0".asUInt(9.W))
    val wmb_entry_avl_w = Mux(cbuf_reset, "b0".asUInt(9.W), wmb_entry_avl + wmb_entry_avl_add - wmb_entry_avl_sub)
    
    //////////////////////////////////// calculate weight entries start offset ////////////////////////////////////
    val wt_entry_st = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))} 

    val wt_entry_st_inc = wt_entry_st + wt_rls_wt_entries
    val wt_entry_st_inc_wrap = wt_entry_st_inc - Cat(weight_bank, "b0".asUInt(conf.LOG2_CBUF_BANK_DEPTH.W))
    val is_wt_entry_st_wrap = (wt_entry_st_inc >=  Cat(weight_bank, "b0".asUInt(conf.LOG2_CBUF_BANK_DEPTH.W)))
    val wt_entry_st_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(~wt_rls, wt_entry_st, Mux(is_wt_entry_st_wrap, wt_entry_st_inc_wrap, wt_entry_st_inc)))


    //////////////////////////////////// calculate weight entries end offset ////////////////////////////////////
    val wt_entry_end = withClock(io.nvdla_core_ng_clk){Reg(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))}

    val wt_entry_end_inc = wt_entry_end + io.cdma2sc_wt_updt.bits.entries
    val wt_entry_end_inc_wrap = wt_entry_end_inc - Cat(weight_bank, "b0".asUInt(conf.LOG2_CBUF_BANK_DEPTH.W))
    val is_wt_entry_end_wrap = (wt_entry_end_inc >=  Cat(weight_bank, "b0".asUInt(conf.LOG2_CBUF_BANK_DEPTH.W)))
    val wt_entry_end_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(is_wt_entry_end_wrap, wt_entry_end_inc_wrap, wt_entry_end_inc)) 

    //////////////////////////////////// calculate wmb entries start offset ////////////////////////////////////
    val wmb_entry_st = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(9.W))}

    val wmb_entry_st_inc = wmb_entry_st + wt_rls_wmb_entries
    val wmb_entry_st_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(~wt_rls, wmb_entry_st, wmb_entry_st_inc))
    //////////////////////////////////// calculate wmb entries end offset ////////////////////////////////////
    val wmb_entry_end = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(9.W))}

    val wmb_entry_end_inc = wmb_entry_end + io.cdma2sc_wmb_entries
    val wmb_entry_end_w = Mux(cbuf_reset, "b0".asUInt(9.W), wmb_entry_end_inc)
    //////////////////////////////////// registers and assertions ////////////////////////////////////
    when(io.cdma2sc_wt_updt.valid | wt_rls | cbuf_reset){
        wt_entry_avl := wt_entry_avl_w 
        wmb_entry_avl := wmb_entry_avl_w
    }
    when(cbuf_reset | wt_rls){
        wt_entry_st := wt_entry_st_w
        wmb_entry_st := wmb_entry_st_w
    }
    when(cbuf_reset | io.cdma2sc_wt_updt.valid){
        wt_entry_end := wt_entry_end_w
        wmb_entry_end := wmb_entry_end_w
    }
    
    //================  Non-SLCG clock domain end ================//

    //////////////////////////////////////////////////////////////
    ///// cbuf status update                                 /////
    //////////////////////////////////////////////////////////////
    val wt_rsp_pipe_pvld = Wire(Bool())
    val wt_rsp_rls = Wire(Bool())
    val wt_rsp_wt_rls_entries = Wire(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
    val wt_rsp_wmb_rls_entries = Wire(UInt(9.W))

    val sub_rls = wt_rsp_pipe_pvld & wt_rsp_rls
    val sub_rls_wt_entries = wt_rsp_wt_rls_entries
    val sub_rls_wmb_entries = wt_rsp_wmb_rls_entries
    val reuse_rls = io.sg2wl.reuse_rls
    wt_rls := reuse_rls | sub_rls
    wt_rls_wt_entries := Mux(reuse_rls, last_weight_entries, sub_rls_wt_entries)
    wt_rls_wmb_entries := Mux(reuse_rls, last_wmb_entries, sub_rls_wmb_entries)
    val wt_rls_updt = wt_rls

    io.sc2cdma_wt_updt.valid := RegNext(wt_rls_updt, false.B)
    io.sc2cdma_wt_updt.bits.entries := RegEnable(Mux(reuse_rls, last_weight_entries, sub_rls_wt_entries), "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), wt_rls_updt)
    io.sc2cdma_wmb_entries := RegEnable(Mux(reuse_rls, last_wmb_entries, sub_rls_wmb_entries), "b0".asUInt(9.W), wt_rls_updt)

    //sc2cmda_wt_kernels is useless
    io.sc2cdma_wt_updt.bits.kernels := "b0".asUInt(14.W)

    //////////////////////////////////////////////////////////////
    ///// input data package                                 /////
    //////////////////////////////////////////////////////////////
    val wl_in_pvld_d = Wire(Bool()) +: 
                       Seq.fill(conf.CSC_WL_PIPELINE_ADDITION)(RegInit(false.B))
    val wl_in_pd_d = Wire(Bool()) +: 
                   Seq.fill(conf.CSC_WL_PIPELINE_ADDITION)(RegInit("b0".asUInt(18.W)))

    wl_in_pvld_d(0) := io.sg2wl.pd.valid
    wl_in_pd_d(0) := io.sg2wl.pd.bits

    for(t <- 0 to conf.CSC_WL_PIPELINE_ADDITION-1){
        wl_in_pvld_d(t+1) := wl_in_pvld_d(t)
        when(wl_in_pvld_d(t)){
            wl_in_pd_d(t+1) := wl_in_pd_d(t)
        }
    }

    val wl_pvld = wl_in_pvld_d(conf.CSC_WL_PIPELINE_ADDITION)
    val wl_pd = wl_in_pd_d(conf.CSC_WL_PIPELINE_ADDITION)

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
    val is_stripe_end = Wire(Bool())
    val stripe_cnt = RegInit("b0".asUInt(5.W))
    val wmb_pipe_valid_d1 = RegInit(false.B)

    val stripe_cnt_inc = stripe_cnt + 1.U
    val stripe_cnt_w = Mux(layer_st, "b0".asUInt(5.W), Mux(is_stripe_end, "b0".asUInt(5.W), stripe_cnt_inc))
    val stripe_length = wl_kernel_size(4, 0)
    is_stripe_end := (stripe_cnt_inc === stripe_length)
    val is_stripe_st = wl_pvld
    val wmb_pipe_valid = Mux(wl_pvld, true.B, Mux(~(stripe_cnt.orR), false.B, wmb_pipe_valid_d1))
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

    wmb_req_element := MuxLookup(wl_cur_sub_h,  Cat("b0".asUInt(1.W), wmb_req_cycle_element(5, 0), "b0".asUInt(2.W)),
                                Seq(
                                    "h0".asUInt(2.W) -> wmb_req_cycle_element,
                                    "h1".asUInt(2.W) -> Cat( "b0".asUInt(1.W), wmb_req_cycle_element(6, 0), "b0".asUInt(1.W)),
                                    "h2".asUInt(2.W) -> (Cat(wmb_req_cycle_element(6, 0), "b0".asUInt(1.W)) +& wmb_req_cycle_element)                                 
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
    val wmb_req_addr_w = Mux(addr_init, wmb_entry_st_w, 
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
    val wmb_rls_entries = Mux(wmb_rls_cnt_vld | ~wmb_req_valid, wmb_rls_cnt, wmb_rls_cnt_inc)

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

    val wmb_rsp_pipe_pvld_d = Wire(Bool()) +: 
                              Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit(false.B))
    val wmb_rsp_pipe_pd_d = Wire(UInt(31.W)) +: 
                            Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit("b0".asUInt(31.W)))

    wmb_rsp_pipe_pvld_d(0) := wmb_req_pipe_pvld
    wmb_rsp_pipe_pd_d(0) := wmb_req_pipe_pd

    for(t<- 0 to conf.NVDLA_CBUF_READ_LATENCY -1){
        wmb_rsp_pipe_pvld_d(t+1) := wmb_rsp_pipe_pvld_d(t)
        when(wmb_rsp_pipe_pvld_d(t)){
            wmb_rsp_pipe_pd_d(t+1) := wmb_rsp_pipe_pd_d(t)
        }
    }

    val wmb_rsp_pipe_pvld = wmb_rsp_pipe_pvld_d(conf.NVDLA_CBUF_READ_LATENCY)
    val wmb_rsp_pipe_pd = wmb_rsp_pipe_pd_d(conf.NVDLA_CBUF_READ_LATENCY)
    //////////////////////////////////////////////////////////////
    ///// wmb data process                                   /////
    //////////////////////////////////////////////////////////////
    val wmb_rsp_ori_element = wmb_rsp_pipe_pd(6, 0)
    val wmb_rsp_element = wmb_rsp_pipe_pd(14, 7)
    val wmb_rsp_rls_entries = wmb_rsp_pipe_pd(23, 15)
    val wmb_rsp_stripe_end = wmb_rsp_pipe_pd(24)
    val wmb_rsp_channel_end = wmb_rsp_pipe_pd(25)
    val wmb_rsp_group_end = wmb_rsp_pipe_pd(26)
    val wmb_rsp_rls = wmb_rsp_pipe_pd(27)
    val wmb_rsp_cur_sub_h = wmb_rsp_pipe_pd(30, 29)

    //////////////////////////////////// wmb remain counter ////////////////////////////////////
    val wmb_rsp_bit_remain_add = Mux(sc2buf_wmb_rd_valid, conf.CSC_WMB_ELEMENTS.U, "b0".asUInt(11.W))
    val wmb_rsp_bit_remain_sub = Mux(wmb_rsp_pipe_pvld, wmb_rsp_element, "b0".asUInt(8.W))

    //how many mask bits is stored 
    val wmb_rsp_bit_remain_last = RegInit("b0".asUInt(10.W))
    val wmb_rsp_bit_remain = RegInit("b0".asUInt(10.W))

    val wmb_rsp_bit_remain_w = Mux(layer_st, "b0".asUInt(10.W), Mux(wmb_rsp_channel_end & ~wmb_rsp_group_end, wmb_rsp_bit_remain_last, wmb_rsp_bit_remain +& wmb_rsp_bit_remain_add -& wmb_rsp_bit_remain_sub))(9, 0)
    val wmb_rsp_bit_remain_last_reg_en = layer_st | (wmb_rsp_pipe_pvld & wmb_rsp_group_end & is_compressed_d1)

    when(layer_st | (wmb_rsp_pipe_pvld & is_compressed_d1)){
        wmb_rsp_bit_remain := wmb_rsp_bit_remain_w
    }
    when(wmb_rsp_bit_remain_last_reg_en){
        wmb_rsp_bit_remain_last := wmb_rsp_bit_remain_w
    }

    //////////////////////////////////// generate element mask for both compressed and compressed case ////////////////////////////////////
    //emask for element mask, NOT byte mask
    val wt_req_emask = RegInit(Fill(conf.CSC_ATOMC, false.B))
    val wmb_emask_remain = RegInit(Fill(conf.CBUF_ENTRY_BITS, false.B))

    val wmb_emask_rd_ls = Mux(~sc2buf_wmb_rd_valid, "b0".asUInt(conf.CSC_ATOMC.W), sc2buf_wmb_rd_data(conf.CSC_ATOMC-1, 0) << wmb_rsp_bit_remain(6, 0))
    val wmb_rsp_emask_in = (wmb_emask_rd_ls | wmb_emask_remain(conf.CSC_ATOMC-1, 0) | Fill(conf.CSC_ATOMC, ~is_compressed_d1)) //wmb for current atomic op
    val wmb_rsp_vld_s = ~(Fill(conf.CSC_ATOMC, true.B) << wmb_rsp_element)
    val wmb_rsp_emask = wmb_rsp_emask_in(conf.CSC_ATOMC-1, 0) & wmb_rsp_vld_s //the mask needed

    when(wmb_rsp_pipe_pvld){
        wt_req_emask := wmb_rsp_emask
    }

    //////////////////////////////////// generate local remain masks ////////////////////////////////////
    
    val wmb_emask_remain_last = RegInit(Fill(conf.CBUF_ENTRY_BITS, false.B))
    val wmb_shift_remain = wmb_rsp_element - wmb_rsp_bit_remain(6, 0)
    val wmb_emask_rd_rs = (sc2buf_wmb_rd_data >> wmb_shift_remain)
    val wmb_emask_remain_rs = (wmb_emask_remain >> wmb_rsp_element)

    //all wmb remain, no more than 1 entry
    val wmb_emask_remain_w = Mux(layer_st, Fill(conf.CBUF_ENTRY_BITS, false.B), Mux(wmb_rsp_channel_end & ~wmb_rsp_group_end, wmb_emask_remain_last, Mux(sc2buf_wmb_rd_valid, wmb_emask_rd_rs, wmb_emask_remain_rs)))
    val wmb_emask_remain_reg_en = layer_st | (wmb_rsp_pipe_pvld & is_compressed_d1)
    val wmb_emask_remain_last_reg_en = layer_st | (wmb_rsp_pipe_pvld & wmb_rsp_group_end & is_compressed_d1)
    val wmb_rsp_ori_sft_3 = Cat(wmb_rsp_ori_element(4, 0), false.B) + wmb_rsp_ori_element(4, 0)

    when(wmb_emask_remain_reg_en){
        wmb_emask_remain := wmb_emask_remain_w
    }
    when(wmb_emask_remain_last_reg_en){
        wmb_emask_remain_last := wmb_emask_remain_w
    }

    //////////////////////////////////// registers for pipeline ////////////////////////////////////
    val wt_req_pipe_valid = RegInit(false.B)
    val wt_req_ori_element = RegInit("b0".asUInt(7.W))
    val wt_req_stripe_end = RegInit(false.B)
    val wt_req_channel_end = RegInit(false.B)
    val wt_req_group_end = RegInit(false.B)
    val wt_req_rls = RegInit(false.B)
    val wt_req_wmb_rls_entries = RegInit("b0".asUInt(9.W))
    val wt_req_cur_sub_h = RegInit("b0".asUInt(2.W)) 
    val wt_req_ori_sft_3 = RegInit("b0".asUInt(7.W)) 

    wt_req_pipe_valid := wmb_rsp_pipe_pvld
    when(wmb_rsp_pipe_pvld){
        wt_req_ori_element := wmb_rsp_ori_element
        wt_req_stripe_end := wmb_rsp_stripe_end
        wt_req_channel_end := wmb_rsp_channel_end
        wt_req_group_end := wmb_rsp_group_end
        wt_req_rls := wmb_rsp_rls
        wt_req_wmb_rls_entries := wmb_rsp_rls_entries
        wt_req_cur_sub_h := wmb_rsp_cur_sub_h
        wt_req_ori_sft_3 := wmb_rsp_ori_sft_3
    }

    //////////////////////////////////////////////////////////////
    ///// weight data request generate                       /////
    //////////////////////////////////////////////////////////////

    //////////////////////////////////// generate mask sum ////////////////////////////////////

    ////CAUSION! wt_req_bmask is byte mask, not elemnet mask!////
    val wt_req_bmask = Wire(Vec(conf.CSC_ATOMC, UInt(1.W)))
    for(i <- 0 to conf.CSC_ATOMC-1){
        wt_req_bmask(i) := wt_req_emask(i).asUInt
    }
    val wt_req_bytes =  wt_req_bmask.reduce(_+&_)

    //////////////////////////////////// generate element mask for decoding////////////////////////////////////
    val wt_req_mask_d1 = RegInit("b0".asUInt(conf.CSC_ATOMC.W))

    //valid bit for each sub h line 
    val wt_req_vld_bit = ~(Fill(conf.CSC_ATOMC, true.B) << wt_req_ori_element)

    //valid bit to select sub h line
    val sub_h_mask_1 = Mux(wt_req_cur_sub_h >= 1.U, Fill(conf.CSC_ATOMC, true.B), Fill(conf.CSC_ATOMC, false.B))
    val sub_h_mask_2 = Mux(wt_req_cur_sub_h >= 2.U, Fill(conf.CSC_ATOMC, true.B), Fill(conf.CSC_ATOMC, false.B))
    val sub_h_mask_3 = Mux(wt_req_cur_sub_h >= 3.U, Fill(conf.CSC_ATOMC, true.B), Fill(conf.CSC_ATOMC, false.B))

    //element number to be shifted
    val wt_req_ori_sft_1 = wt_req_ori_element
    val wt_req_ori_sft_2 = Cat(wt_req_ori_element(5, 0), false.B)
    val wt_req_emask_p0 = wt_req_emask(conf.CSC_ATOMC-1, 0) & wt_req_vld_bit
    val wt_req_emask_p1 = (wt_req_emask(conf.CSC_ATOMC-1, 0) >> wt_req_ori_sft_1) & wt_req_vld_bit & sub_h_mask_1
    val wt_req_emask_p2 = (wt_req_emask(conf.CSC_ATOMC-1, 0) >> wt_req_ori_sft_2) & wt_req_vld_bit & sub_h_mask_2    
    val wt_req_emask_p3 = (wt_req_emask(conf.CSC_ATOMC-1, 0) >> wt_req_ori_sft_3) & wt_req_vld_bit & sub_h_mask_3

    //Caution! Must reset wt_req_mask to all zero when layer started
    //other width wt_req_mask_en may gate wt_rsp_mask_d1_w improperly!
    val wt_req_mask_w = Mux(layer_st, Fill(conf.CSC_ATOMC, false.B),
                        Mux(sub_h_total === "h1".asUInt(3.W), wt_req_emask_p0,
                        Mux(sub_h_total === "h2".asUInt(3.W), Cat(wt_req_emask_p1(conf.CSC_ATOMC/2-1, 0), wt_req_emask_p0(conf.CSC_ATOMC/2-1, 0)),
                        Cat(wt_req_emask_p3(conf.CSC_ATOMC/4-1, 0), wt_req_emask_p2(conf.CSC_ATOMC/4-1, 0), wt_req_emask_p1(conf.CSC_ATOMC/4-1, 0), wt_req_emask_p0(conf.CSC_ATOMC/4-1, 0)) 
                        )))

    val wt_req_mask_en = wt_req_pipe_valid & (wt_req_mask_w =/= wt_req_mask_d1)

    //////////////////////////////////// generate weight read request ////////////////////////////////////
    val wt_byte_avl = RegInit("b0".asUInt(8.W))
    val wt_byte_avl_last = RegInit("b0".asUInt(8.W))

    val wt_req_valid = wt_req_pipe_valid & (wt_byte_avl < wt_req_bytes)
    //////////////////////////////////// generate weight avaliable bytes ////////////////////////////////////
    val wt_byte_avl_add = Mux(~wt_req_valid, "b0".asUInt(8.W), conf.CSC_WT_ELEMENTS.U)
    val wt_byte_avl_sub = wt_req_bytes
    val wt_byte_avl_inc = wt_byte_avl + wt_byte_avl_add - wt_byte_avl_sub
    val wt_byte_avl_w = Mux(layer_st, "b0".asUInt(8.W), 
                        Mux(~wt_req_group_end & wt_req_channel_end, wt_byte_avl_last,
                        wt_byte_avl_inc
                        ))
    val wt_byte_last_reg_en = layer_st | (wt_req_pipe_valid & wt_req_stripe_end & wt_req_group_end)

    when(layer_st | wt_req_pipe_valid){
        wt_byte_avl := wt_byte_avl_w
    }
    when(wt_byte_last_reg_en){
        wt_byte_avl_last := wt_byte_avl_w
    }

    //////////////////////////////////// generate weight read address ////////////////////////////////////
    val wt_req_addr = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))
    val wt_req_addr_last = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))

    val wt_req_addr_inc = wt_req_addr + 1.U
    val is_wr_req_addr_wrap = (wt_req_addr_inc === Cat(weight_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B)))
    val wt_req_addr_inc_wrap = Mux(is_wr_req_addr_wrap, Fill(conf.CBUF_ADDR_WIDTH, false.B), wt_req_addr_inc)

    val wt_req_addr_w = Mux(addr_init, wt_entry_st_w(conf.CBUF_ADDR_WIDTH-1, 0), 
                        Mux(~wt_req_group_end & wt_req_channel_end, wt_req_addr_last,
                        Mux(wt_req_valid, wt_req_addr_inc_wrap,
                        wt_req_addr
                        )))

    val wt_req_addr_reg_en = addr_init | wt_req_valid | (wt_req_pipe_valid & wt_req_channel_end)
    val wt_req_addr_last_reg_en = addr_init | (wt_req_pipe_valid & wt_req_pipe_valid & wt_req_group_end)
    val wt_req_addr_out = wt_req_addr + Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B))

    when(wt_req_addr_reg_en){
        wt_req_addr := wt_req_addr_w
    }
    when(wt_req_addr_last_reg_en){
        wt_req_addr_last := wt_req_addr_w
    }

    //////////////////////////////////// weight entries counter for release ////////////////////////////////////
    val wt_rls_cnt_vld = RegInit(false.B)
    val wt_rls_cnt = RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))

    val wt_rls_cnt_vld_w = Mux((layer_st | wt_req_group_end), false.B,  Mux(wt_req_channel_end,  true.B, wt_rls_cnt_vld))
    val wt_rls_cnt_inc = wt_rls_cnt + 1.U
    val wt_rls_cnt_w = Mux(layer_st, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(wt_req_group_end, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), wt_rls_cnt_inc))
    val wt_rls_cnt_reg_en = layer_st | (wt_req_pipe_valid & wt_req_group_end) | (~wt_rls_cnt_vld & wt_req_valid)
    val wt_rls_entries = Mux(wt_rls_cnt_vld | ~wt_req_valid, wt_rls_cnt, wt_rls_cnt_inc)

    wt_rls_cnt_vld := wt_rls_cnt_vld_w
    when(wt_rls_cnt_reg_en){
        wt_rls_cnt := wt_rls_cnt_w
    }

    //////////////////////////////////// send weight read request ////////////////////////////////////
    val sc2buf_wt_rd_en_out = RegInit(false.B)
    val sc2buf_wt_rd_addr_out = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))

    val wt_req_pipe_valid_d1 = RegInit(false.B)
    val wt_req_stripe_end_d1 = RegInit(false.B)
    val wt_req_channel_end_d1 = RegInit(false.B)
    val wt_req_group_end_d1 = RegInit(false.B)
    val wt_req_rls_d1 = RegInit(false.B) 
    val wt_req_bytes_d1 = RegInit("b0".asUInt(8.W))

    val wt_req_mask_en_d1 = RegInit(false.B)
    val wt_req_wmb_rls_entries_d1 = RegInit("b0".asUInt(9.W))
    val wt_req_wt_rls_entries_d1 = RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))


    sc2buf_wt_rd_en_out := wt_req_valid
    when(wt_req_valid){
        sc2buf_wt_rd_addr_out := wt_req_addr_out
    }
    wt_req_pipe_valid_d1 := wt_req_pipe_valid
    when(wt_req_pipe_valid){
        wt_req_stripe_end_d1 := wt_req_stripe_end
        wt_req_channel_end_d1 := wt_req_channel_end
        wt_req_group_end_d1 := wt_req_group_end
        wt_req_rls_d1 := wt_req_rls
        wt_req_bytes_d1 := wt_req_bytes
    }
    //Caution! Here wt_req_mask is still element mask
    when(layer_st | wt_req_pipe_valid){
        wt_req_mask_d1 := wt_req_mask_w
    }
    wt_req_mask_en_d1 := wt_req_mask_en
    when(wt_req_pipe_valid){
        wt_req_wmb_rls_entries_d1 := wt_req_wmb_rls_entries
    }
    when(wt_req_pipe_valid & wt_req_rls){
        wt_req_wt_rls_entries_d1 := wt_rls_entries
    }
    io.sc2buf_wt_rd.addr.valid := sc2buf_wt_rd_en_out 
    io.sc2buf_wt_rd.addr.bits := sc2buf_wt_rd_addr_out 

    //////////////////////////////////////////////////////////////
    ///// sideband pipeline for wmb read                     /////
    //////////////////////////////////////////////////////////////
    val wt_req_pipe_pvld = wt_req_pipe_valid_d1

    val wt_req_d1_stripe_end = wt_req_stripe_end_d1
    val wt_req_d1_channel_end = wt_req_channel_end_d1
    val wt_req_d1_group_end = wt_req_group_end_d1
    val wt_req_d1_rls = wt_req_rls_d1
    val wt_req_d1_bytes = wt_req_bytes_d1
    val wt_req_d1_wmb_rls_entries = wt_req_wmb_rls_entries_d1
    val wt_req_d1_wt_rls_entries = wt_req_wt_rls_entries_d1

    // PKT_PACK_WIRE( csc_wt_req_pkg ,  wt_req_d1_ ,  wt_req_pipe_pd )
    val wt_req_pipe_pd = Cat(wt_req_d1_rls, wt_req_d1_group_end, wt_req_d1_channel_end
                            , wt_req_d1_stripe_end, wt_req_d1_wt_rls_entries(14, 0), wt_req_d1_wmb_rls_entries(8, 0)
                            , wt_req_d1_bytes(7, 0))

    //delay chain
    val wt_rsp_pipe_pvld_d = Wire(Bool()) +: 
                             Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit(false.B))
    val wt_rsp_pipe_pd_d = Wire(UInt(36.W)) +: 
                            Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit("b0".asUInt(36.W)))
    val wt_rsp_mask_en_d = Wire(Bool()) +: 
                           Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit(false.B))
    val wt_rsp_mask_d = Wire(UInt(conf.CSC_ATOMC.W)) +: 
                            Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit("b0".asUInt(conf.CSC_ATOMC.W)))
    
    wt_rsp_pipe_pvld_d(0) := wt_req_pipe_pvld
    wt_rsp_pipe_pd_d(0) := wt_req_pipe_pd
    wt_rsp_mask_en_d(0) := wt_req_mask_en_d1
    wt_rsp_mask_d(0) := wt_req_mask_d1
    
    for(t <- 0 to conf.NVDLA_CBUF_READ_LATENCY-1){
        wt_rsp_pipe_pvld_d(t+1) := wt_rsp_pipe_pvld_d(t)
        when(wt_rsp_pipe_pvld_d(t)){
            wt_rsp_pipe_pd_d(t+1) := wt_rsp_pipe_pd_d(t)
        }
        wt_rsp_mask_en_d(t+1) := wt_rsp_mask_en_d(t)
        when(wt_rsp_mask_en_d(t)){
            wt_rsp_mask_d(t+1) := wt_rsp_mask_d(t)
        }
    }
    wt_rsp_pipe_pvld := wt_rsp_pipe_pvld_d(conf.NVDLA_CBUF_READ_LATENCY)
    val wt_rsp_pipe_pd = wt_rsp_pipe_pd_d(conf.NVDLA_CBUF_READ_LATENCY)
    val wt_rsp_mask_en = wt_rsp_mask_en_d(conf.NVDLA_CBUF_READ_LATENCY)
    val wt_rsp_mask = wt_rsp_mask_d(conf.NVDLA_CBUF_READ_LATENCY)



    //////////////////////////////////////////////////////////////
    ///// weight data process                                /////
    //////////////////////////////////////////////////////////////
    val wt_rsp_bytes = wt_rsp_pipe_pd(7, 0)
    wt_rsp_wmb_rls_entries := wt_rsp_pipe_pd(16, 8)
    wt_rsp_wt_rls_entries := wt_rsp_pipe_pd(31, 17)
    val wt_rsp_stripe_end  = wt_rsp_pipe_pd(32)
    val wt_rsp_channel_end  = wt_rsp_pipe_pd(33)
    val wt_rsp_group_end  = wt_rsp_pipe_pd(34)
    wt_rsp_rls  := wt_rsp_pipe_pd(35)

    //////////////////////////////////// generate byte mask for decoding ////////////////////////////////////
    val wt_rsp_mask_d1_w = wt_rsp_mask

    //////////////////////////////////// weight remain counter ////////////////////////////////////
    val wt_rsp_byte_remain = RegInit("b0".asUInt(7.W))
    val wt_rsp_byte_remain_last = RegInit("b0".asUInt(7.W))

    val wt_rsp_byte_remain_add = Mux(io.sc2buf_wt_rd.data.valid, conf.CSC_WT_ELEMENTS.U, "h0".asUInt(8.W))
    val wt_rsp_byte_remain_w = Mux(layer_st, "b0".asUInt(8.W), 
                             Mux(wt_rsp_channel_end & ~wt_rsp_group_end, Cat("b0".asUInt(2.W), wt_rsp_byte_remain_last), wt_rsp_byte_remain + wt_rsp_byte_remain_add - wt_rsp_bytes))(6, 0)
    val wt_rsp_byte_remain_en = layer_st | wt_rsp_pipe_pvld
    val wt_rsp_byte_remain_last_en = layer_st | (wt_rsp_pipe_pvld & wt_rsp_group_end)

    when(wt_rsp_byte_remain_en){
        wt_rsp_byte_remain := wt_rsp_byte_remain_w
    }
    when(wt_rsp_byte_remain_last_en){
        wt_rsp_byte_remain_last := wt_rsp_byte_remain_w
    }

    //////////////////////////////////// generate local remain bytes ////////////////////////////////////
    val wt_data_remain = Reg(UInt(conf.CBUF_ENTRY_BITS.W))
    val wt_data_remain_last = Reg(UInt(conf.CBUF_ENTRY_BITS.W))

    val wt_shift_remain = wt_rsp_bytes - wt_rsp_byte_remain(6, 0)
    val wt_data_input_rs = (io.sc2buf_wt_rd.data.bits(conf.CBUF_ENTRY_BITS-1, 0) >> Cat(wt_shift_remain, "b0".asUInt(3.W)))
    val wt_data_remain_masked = Mux( ~wt_rsp_byte_remain.orR, "b0".asUInt(conf.CBUF_ENTRY_BITS.W),  wt_data_remain)
    val wt_data_remain_rs = (wt_data_remain >> Cat(wt_rsp_bytes, "b0".asUInt(3.W)))
    //weight data local remain, 1 entry at most
    val wt_data_remain_w = Mux(layer_st, "b0".asUInt(conf.CBUF_ENTRY_BITS.W), 
                           Mux(wt_rsp_channel_end & ~wt_rsp_group_end & (wt_rsp_byte_remain_last.orR), wt_data_remain_last,
                           Mux(io.sc2buf_wt_rd.data.valid, wt_data_input_rs,
                           wt_data_remain_rs
                           )))
    val wt_data_remain_reg_en = layer_st | (wt_rsp_pipe_pvld & (wt_rsp_byte_remain_w.orR))
    val wt_data_remain_last_reg_en = layer_st | (wt_rsp_pipe_pvld & wt_rsp_group_end & (wt_rsp_byte_remain_w.orR))
    val wt_data_input_ls = (io.sc2buf_wt_rd.data.bits << Cat(wt_rsp_byte_remain(6, 0), "b0".asUInt(3.W)))
    val wt_data_input_sft = Mux(io.sc2buf_wt_rd.data.valid, wt_data_input_ls, "b0".asUInt(conf.CBUF_ENTRY_BITS.W))
    
    when(wt_data_remain_reg_en){
        wt_data_remain := wt_data_remain_w
    }
    when(wt_data_remain_last_reg_en){
        wt_data_remain_last := wt_data_remain_w
    }

    //////////////////////////////////// generate bytes for decoding ////////////////////////////////////
    val dec_input_data = RegInit(VecInit(Seq.fill(conf.CSC_ATOMC)(0.asUInt(conf.CSC_BPE.W))))

    val wt_rsp_data = (wt_data_input_sft | wt_data_remain_masked)
    when(wt_rsp_pipe_pvld){
        for(i <- 0 to conf.CSC_ATOMC-1){
            dec_input_data(i) := wt_rsp_data(i*conf.CSC_BPE+conf.CSC_BPE-1, i*conf.CSC_BPE).asUInt
        }
    }


    //////////////////////////////////// generate select signal ////////////////////////////////////
    val wt_rsp_last_stripe_end = RegInit(false.B)
    val wt_rsp_sel_d1 = RegInit("b1".asUInt(conf.CSC_ATOMK.W))

    val wt_rsp_sel_w = Mux(wt_rsp_last_stripe_end, "b1".asUInt(conf.CSC_ATOMK.W), 
                       Cat(wt_rsp_sel_d1(conf.CSC_ATOMK-2, 0), wt_rsp_sel_d1(conf.CSC_ATOMK-1))
                       )
    when(wt_rsp_pipe_pvld){
        wt_rsp_last_stripe_end := wt_rsp_stripe_end
        wt_rsp_sel_d1 := wt_rsp_sel_w
    }
    
    val dec_input_sel = VecInit((0 to conf.CSC_ATOMK-1) map { i => wt_rsp_sel_d1(i).toBool})

    //////////////////////////////////// prepare other signals ////////////////////////////////////
    val dec_input_pipe_valid = RegInit(false.B)
    val dec_input_mask = RegInit(VecInit(Seq.fill(conf.CSC_ATOMC)(false.B)))
    val dec_input_mask_en = RegInit("b0".asUInt(10.W))

    dec_input_pipe_valid := wt_rsp_pipe_pvld
    when(wt_rsp_mask_en){        
        dec_input_mask := VecInit((0 to conf.CSC_ATOMC-1) map { i => wt_rsp_mask_d1_w(i)})
    } 
    dec_input_mask_en := Fill(10, wt_rsp_mask_en)


    val u_dec = Module(new NV_NVDLA_CSC_WL_dec)
    u_dec.io.nvdla_core_clk := io.nvdla_core_clk          //|< i
    u_dec.io.input.bits.data := dec_input_data  //|< r
    u_dec.io.input.bits.mask := dec_input_mask  //|< r
    u_dec.io.input_mask_en := dec_input_mask_en  //|< r
    u_dec.io.input.valid := dec_input_pipe_valid    //|< r
    u_dec.io.input.bits.sel := dec_input_sel     //|< w
    val sc2mac_out_data = u_dec.io.output.bits.data
    val sc2mac_out_mask = u_dec.io.output.bits.mask
    val sc2mac_out_pvld = u_dec.io.output.valid
    val sc2mac_out_sel = u_dec.io.output.bits.sel

    //////////////////////////////////////////////////////////////
    ///// registers for retiming                             /////
    //////////////////////////////////////////////////////////////

    val sc2mac_out_a_sel_w = Fill(conf.CSC_ATOMK_HF, sc2mac_out_pvld) & Cat(sc2mac_out_sel.asUInt)(conf.CSC_ATOMK_HF-1, 0)
    val sc2mac_out_b_sel_w = Fill(conf.CSC_ATOMK_HF, sc2mac_out_pvld) & Cat(sc2mac_out_sel.asUInt)(conf.CSC_ATOMK-1, conf.CSC_ATOMK_HF)
    val sc2mac_wt_a_pvld_w = sc2mac_out_a_sel_w.orR
    val sc2mac_wt_b_pvld_w = sc2mac_out_b_sel_w.orR

    val sc2mac_wt_a_pvld_out = RegInit(false.B)
    val sc2mac_wt_b_pvld_out = RegInit(false.B)
    val sc2mac_wt_a_mask_out = RegInit(VecInit(Seq.fill(conf.CSC_ATOMC)(false.B)))
    val sc2mac_wt_b_mask_out = RegInit(VecInit(Seq.fill(conf.CSC_ATOMC)(false.B)))
    val sc2mac_wt_a_sel_out = RegInit(VecInit(Seq.fill(conf.CSC_ATOMK_HF)(false.B)))
    val sc2mac_wt_b_sel_out = RegInit(VecInit(Seq.fill(conf.CSC_ATOMK_HF)(false.B)))
    val sc2mac_wt_a_data_out = Reg(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))
    val sc2mac_wt_b_data_out = Reg(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))   
    val sc2mac_out_a_mask = VecInit((0 to conf.CSC_ATOMC-1) map { i => sc2mac_out_mask(i).toBool&sc2mac_wt_a_pvld_w})
    val sc2mac_out_b_mask = VecInit((0 to conf.CSC_ATOMC-1) map { i => sc2mac_out_mask(i).toBool&sc2mac_wt_b_pvld_w})

    sc2mac_wt_a_pvld_out := sc2mac_wt_a_pvld_w
    sc2mac_wt_b_pvld_out := sc2mac_wt_b_pvld_w
    when(sc2mac_wt_a_pvld_w | sc2mac_wt_a_pvld_out){
        sc2mac_wt_a_mask_out := sc2mac_out_a_mask
        sc2mac_wt_a_sel_out := VecInit((0 to conf.CSC_ATOMK_HF-1) map { i => sc2mac_out_a_sel_w(i)})
    }
    when(sc2mac_wt_b_pvld_w | sc2mac_wt_b_pvld_out){
        sc2mac_wt_b_mask_out := sc2mac_out_b_mask
        sc2mac_wt_b_sel_out := VecInit((0 to conf.CSC_ATOMK_HF-1) map { i => sc2mac_out_b_sel_w(i)})
    }

    for (i <- 0 to conf.CSC_ATOMC-1){
        when(sc2mac_out_a_mask(i)){
            sc2mac_wt_a_data_out(i) := sc2mac_out_data(i)
        }
        when(sc2mac_out_b_mask(i)){
            sc2mac_wt_b_data_out(i) := sc2mac_out_data(i)
        }        
    }
    io.sc2mac_wt_a.valid := sc2mac_wt_a_pvld_out
    io.sc2mac_wt_b.valid := sc2mac_wt_b_pvld_out
    io.sc2mac_wt_a.bits.mask := sc2mac_wt_a_mask_out
    io.sc2mac_wt_b.bits.mask := sc2mac_wt_b_mask_out 
    io.sc2mac_wt_a.bits.sel := sc2mac_wt_a_sel_out
    io.sc2mac_wt_b.bits.sel := sc2mac_wt_b_sel_out 
    io.sc2mac_wt_a.bits.data := sc2mac_wt_a_data_out
    io.sc2mac_wt_b.bits.data := sc2mac_wt_b_data_out

}}


object NV_NVDLA_CSC_wlDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CSC_wl)
}
