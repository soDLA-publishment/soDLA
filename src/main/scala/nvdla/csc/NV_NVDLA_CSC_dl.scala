// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CSC_dl(implicit val conf: cscConfiguration) extends Module {
//     val io = IO(new Bundle {

//         val nvdla_core_clk = Input(Clock())
//         val nvdla_core_ng_clk = Input(Clock())

//         val sg2dl_pvld = Input(Bool()) /* data valid */
//         val sg2dl_pd = Input(UInt(31.W))
//         val sc_state = Input(UInt(2.W))
//         val sg2dl_reuse_rls = Input(Bool())

//         val sc2cdma_dat_pending_req = Input(Bool())

//         val cdma2sc_dat_updt = Input(Bool())    /* data valid */
//         val cdma2sc_dat_entries = Input(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
//         val cdma2sc_dat_slices = Input(UInt(14.W))

//         val sc2cdma_dat_updt = Output(Bool())    /* data valid */
//         val sc2cdma_dat_entries = Output(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
//         val sc2cdma_dat_slices = Output(UInt(14.W))

//         val sc2buf_dat_rd_en = Output(Bool())    /* data valid */
//         val sc2buf_dat_rd_addr = Output(UInt(conf.CBUF_ADDR_WIDTH.W))

//         val sc2buf_dat_rd_valid = Input(Bool())      /* data valid */
//         val sc2buf_dat_rd_data = Input(UInt(conf.CBUF_ENTRY_BITS.W))
//         val sc2buf_dat_rd_shift = Output(UInt(conf.CBUF_RD_DATA_SHIFT_WIDTH.W))
//         val sc2buf_dat_rd_next1_en = Output(Bool())
//         val sc2buf_dat_rd_next1_addr = Output(UInt(conf.CBUF_ADDR_WIDTH.W))

//         val sc2mac_dat_a_pvld = Output(Bool())              /* data valid */
//         val sc2mac_dat_a_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
//         val sc2mac_dat_a_data = Output(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))
//         val sc2mac_dat_a_pd = Output(UInt(9.W))

//         val sc2mac_dat_b_pvld = Output(Bool())              /* data valid */
//         val sc2mac_dat_b_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
//         val sc2mac_dat_b_data = Output(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))
//         val sc2mac_dat_b_pd = Output(UInt(9.W))

//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_conv_mode = Input(Bool())
//         val reg2dp_batches = Input(UInt(5.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_datain_format = Input(Bool()) 
//         val reg2dp_skip_data_rls = Input(Bool())
//         val reg2dp_datain_channel_ext = Input(UInt(13.W))
//         val reg2dp_datain_height_ext = Input(UInt(13.W))
//         val reg2dp_datain_width_ext = Input(UInt(13.W))
//         val reg2dp_y_extension = Input(UInt(2.W))
//         val reg2dp_weight_channel_ext = Input(UInt(13.W))
//         val reg2dp_entries = Input(UInt(14.W))
//         val reg2dp_dataout_width = Input(UInt(13.W))
//         val reg2dp_rls_slices = Input(UInt(12.W))
//         val reg2dp_conv_x_stride_ext = Input(UInt(3.W))
//         val reg2dp_conv_y_stride_ext = Input(UInt(3.W))
//         val reg2dp_x_dilation_ext = Input(UInt(5.W))
//         val reg2dp_y_dilation_ext = Input(UInt(5.W))
//         val reg2dp_pad_left = Input(UInt(5.W))
//         val reg2dp_pad_top = Input(UInt(5.W))
//         val reg2dp_pad_value = Input(UInt(16.W))
//         val reg2dp_data_bank = Input(UInt(5.W))
//         val reg2dp_pra_truncate = Input(UInt(2.W))

//         val slcg_wg_en = Output(Bool())

//     })
// /////////////////////////////////////////////////////////////////////////////////////////////
// // Pipeline of Weight loader, for both compressed weight and uncompressed weight
// //
// //                      input_package
// //                           |                     
// //                      data request               
// //                           |                     
// //                      conv_buffer                
// //                           |                     
// //                      feature data---> data relase
// //                        |     |                  
// //                      REG    PRA                 
// //                        |     |                  
// //                        REGISTER                 
// //                           |                     
// //                          MAC                    
// //
// /////////////////////////////////////////////////////////////////////////////////////////////
// withClock(io.nvdla_core_clk){

// //////////////////////////////////////////////////////////////
// ///// status from sequence generator                     /////
// //////////////////////////////////////////////////////////////
// val is_sg_idle = (io.sc_state === 0.U)
// val is_sg_running = (io.sc_state === 2.U)
// val is_sg_done = (io.sc_state == 3.U)

// val is_sg_running_d1 = RegInit(false.B)
// is_sg_running_d1 := is_sg_running

// //////////////////////////////////////////////////////////////
// ///// input signals from registers                       /////
// //////////////////////////////////////////////////////////////
// val layer_st = io.reg2dp_op_en & is_sg_idle
// val is_pixel = io.reg2dp_datain_format 
// val is_winograd =  if(conf.NVDLA_WINOGRAD_ENABLE) io.reg2dp_conv_mode else false.B
// val is_conv = (io.reg2dp_conv_mode == 0.U)
// val is_img = is_conv & is_pixel
// val data_bank_w = io.reg2dp_data_bank + 1.U
// val data_batch_w = if(NVDLA_BATCH_ENABLE) Mux(is_winograd | is_img, 1.U, io.reg2dp_batches +& 1.U) else 1.U
// val batch_cmp_w = if(NVDLA_BATCH_ENABLE) Mux(is_winograd | is_img, 0.U, io.reg2dp_batches) else 0.U 
// val is_int8 = (io.reg2dp_proc_precision === 0.U)
// val is_fp16 = (io.reg2dp_proc_precision === 2.U)
// val datain_width_w = Mux(is_winograd, io.reg2dp_datain_width_ext(12, 2) +& 1.U, io.reg2dp_datain_width_ext +& 1.U)
// val datain_width_cmp_w = io.reg2dp_datain_width_ext
// val datain_height_cmp_w = io.reg2dp_datain_height_ext
// val datain_channel_cmp_w = Mux(is_winograd, io.reg2dp_datain_width_ext(12, 2) , Cat(Fill(conf.LOG2_ATOMC-2, false.B), io.reg2dp_weight_channel_ext(12, conf.LOG2_ATOMC)))

// //y_ex=0,sub_h_total=1;y_ex=1,sub_h_total=2; y_ext=2,sub_h_total=4; non_image, sub_h_total=1;
// //sub_h_total means how many h lines are used in post-extention
// val sub_h_total_w = Mux(is_img, "b1001".asUInt(4.W) << io.reg2dp_y_extension, "h01000".asUInt(5.W))(5,3)  
// val sub_h_cmp_w = Mux(is_img, sub_h_total_w, Mux(is_winograd, 2.U, 1.U))
// val dataout_w_init = sub_h_cmp_w -& 1.U
// val conv_x_stride_w = Mux(is_winograd, 1.U, io.reg2dp_conv_x_stride_ext +& 1.U)
// val pixel_x_stride_w = MuxLookUp(io.reg2dp_datain_channel_ext(1,0), conv_x_stride_w, 
//                                  Array(3.U -> Cat(conv_x_stride_w, "b0".asUInt(2.W)), //*4, after pre_extension
//                                        2.U -> Cat(conv_x_stride_w, "b0".asUInt(1.W)) +& conv_x_stride_w))//*3

// val pixel_x_init_w = if(conf.LOG2_ATOMC == 6)
//                     MuxLookUp(io.reg2dp_y_extension, Mux(io.reg2dp_weight_channel_ext >= conf.CSC_ATOMC_HEX.U, Fill(conf.LOG2_ATOMC, true.B), io.reg2dp_weight_channel_ext(conf.LOG2_ATOMC-1, 0)),
//                                Array(2.U -> Cat(pixel_x_stride_w, "b0".asUInt(1.W)) + pixel_x_stride_w + io.reg2dp_weight_channel_ext(5, 0), 
//                                      1.U -> pixel_x_stride_w + io.reg2dp_weight_channel_ext(5, 0)))
//                     else
//                     MuxLookUp(io.reg2dp_y_extension, Mux(io.reg2dp_weight_channel_ext >= conf.CSC_ATOMC_HEX.U, Fill(conf.LOG2_ATOMC, true.B), io.reg2dp_weight_channel_ext(conf.LOG2_ATOMC-1, 0)),
//                                  Array(2.U -> Cat(pixel_x_stride_w, "b0".asUInt(1.W)) + pixel_x_stride_w + io.reg2dp_weight_channel_ext(5, 0), 
//                                        1.U -> pixel_x_stride_w + io.reg2dp_weight_channel_ext(5, 0)))


// val pixel_x_init_offset_w = io.reg2dp_weight_channel_ext(conf.LOG2_ATOMC-1, 0) +& 1.U
// val pixel_x_add_w = MuxLookUp(io.reg2dp_y_extension, pixel_x_stride_w,
//                                Array(2.U -> Cat(pixel_x_stride_w, "b0".asUInt(2.W)), //*4, after post_extension
//                                      1.U -> Cat(pixel_x_stride_w, "b0".asUInt(1.W))))//*2

// val pixel_ch_stride_w = if(conf.CC_ATOMC_DIV_ATOMK==1|conf.CC_ATOMC_DIV_ATOMK==2)
//                         Cat(pixel_x_stride_w, "b0".asUInt((conf.LOG2_ATOMK+1).W)) //stick to 2*atomK  no matter which config.
//                         else
//                         Cat(pixel_x_stride_w, "b0".asUInt((conf.LOG2_ATOMK+2).W)) //stick to 4*atomK  no matter which config.





// val conv_y_stride_w = Mux(is_winograd, 1.U, io.reg2dp_conv_y_stride_ext +& 1.U)
// val x_dilate_w = Mux(is_winograd | is_img, 1.U, io.reg2dp_x_dilation_ext +& 1.U) 
// val y_dilate_w = Mux(is_winograd | is_img, 1.U, io.reg2dp_y_dilation_ext +& 1.U) 

// //reg2dp_entries means entry per slice
// val layer_st_d1 = RegInit(false.B)
// val data_batch = RegInit(Fill(6, false.B))
// val rls_slices = RegInit(Fill(14, false.B))
// val h_offset_slice = RegInit(Fill(14, false.B))
// val entries = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
// val entries_batch = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
// val rls_slices = RegInit(Fill(14, false.B))
// val dataout_width_cmp = RegInit(Fill(13, false.B))
// val pra_truncate = RegInit(Fill(8, false.B))
// val rls_entries = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
// val h_bias_0_stride = RegInit(Fill(12, false.B))
// val h_bias_1_stride = RegInit(Fill(12, false.B))

// val entries_single_w = (io.reg2dp_entries +& 1.U)(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
// val entries_batch_w = (entries_single_w * data_batch_w)(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
// val entries_w = Mux(is_winograd, Cat(io.reg2dp_entries(12,0), "b0".asUInt(2.W)) +& 4.U, entries_single_w)(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
// val h_offset_slice_w = data_batch_w * y_dilate_w
// val h_bias_0_stride_w = (entries * data_batch)(11, 0)
// val h_bias_1_stride_w = (entries * h_offset_slice)(11, 0)
// val rls_slices_w = io.reg2dp_rls_slices + 1.U
// val slice_left_w = Mux(io.reg2dp_skip_data_rls, io.reg2dp_datain_height_ext + 1.U, io.reg2dp_datain_height_ext - io.reg2dp_rls_slices)(13, 0)
// val slices_oprand = Mux(layer_st_d1, rls_slices, slice_left)
// val slice_entries_w = (entries_batch * slices_oprand)(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
// val dataout_width_cmp_w = io.reg2dp_dataout_width
// val pra_truncate_w = Mux(io.reg2dp_pra_truncate === 3.U, 2.U, io.reg2dp_pra_truncate)

// val is_winograd_d1 = RegInit(Fill(22, false.B))
// val is_img_d1 = RegInit(Fill(34, false.B))
// val data_bank = RegInit(Fill(5, false.B))
// val datain_width = RegInit(Fill(14, false.B))
// val datain_width_cmp = RegInit(Fill(13, false.B))
// val datain_height_cmp = RegInit(Fill(13, false.B)) 
// val datain_channel_cmp = RegInit(Fill(11, false.B)) 
// val sub_h_total_g0 = RegInit("h1".asUInt(3.W))
// val sub_h_total_g1 = RegInit("h1".asUInt(3.W))  
// val sub_h_total_g2 = RegInit("h1".asUInt(2.W))  
// val sub_h_total_g3 = RegInit("h1".asUInt(3.W))  
// val sub_h_total_g4 = RegInit("h1".asUInt(3.W))    
// val sub_h_total_g5 = RegInit("h1".asUInt(3.W))  
// val sub_h_total_g6 = RegInit("h1".asUInt(3.W))  
// val sub_h_total_g7 = RegInit("h1".asUInt(3.W))  
// val sub_h_total_g8 = RegInit("h1".asUInt(3.W))  
// val sub_h_total_g8 = RegInit("h1".asUInt(3.W))  
// val sub_h_total_g10 = RegInit("h1".asUInt(3.W))  
// val sub_h_total_g11 = RegInit("h1".asUInt(3.W)) 
// val sub_h_cmp_g0 = RegInit("h1".asUInt(3.W)) 
// val sub_h_cmp_g1 = RegInit("h1".asUInt(3.W))
// val conv_x_stride = RegInit(Fill(4, false.B)) 
// val conv_y_stride = RegInit(Fill(4, false.B))
// val pixel_x_stride_odd = RegInit(false.B) 
// val batch_cmp = RegInit(Fill(5, false.B))
// val pixel_x_init = RegInit(Fill(6, false.B))
// val pixel_x_init_offset = RegInit(Fill(7, false.B))
// val pixel_x_add = RegInit(Fill(7, false.B))
// val pixel_x_byte_stride = RegInit(Fill(7, false.B))
// val pixel_ch_stride = RegInit(Fill(12, false.B))
// val x_dilate = RegInit(Fill(6, false.B))
// val y_dilate = RegInit(Fill(6, false.B))
// val pad_value = RegInit(Fill(16, false.B))
// val entries_cmp = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
// val h_bias_2_stride = RegInit(Fill(conf.CBUF_ADDR_WIDTH, false.B))
// val h_bias_3_stride = RegInit(Fill(conf.CBUF_ADDR_WIDTH, false.B))

// val last_slices = RegInit(Fill(14, false.B)) 
// val last_entries = RegInit(Fill(conf.CBUF_ADDR_WIDTH, false.B))

// val pra_precision = RegInit(Fill(8, false.B))
// layer_st_d1 := layer_st
// when(layer_st){
//     entries_batch := entries_batch_w
//     entries := entries_w
//     h_offset_slice := h_offset_slice_w
//     data_batch := data_batch_w
//     rls_slices := rls_slices_w
//     slice_left := slice_left_w         
//     dataout_width_cmp := dataout_width_cmp_w
//     pra_truncate := Fill(4, pra_truncate_w)
// }
// when(layer_st_d1){
//     h_bias_0_stride := h_bias_0_stride_w
//     h_bias_1_stride := h_bias_1_stride_w
//     rls_entries := slice_entries_w
// }
// when(is_sg_done){
//     rls_entries := slice_entries_w
// }






// when(layer_st){
//     is_winograd_d1 := Fill(22, is_winograd)
//     is_img_d1 := Fill(34, is_img)
//     data_bank := data_bank_w
//     datain_width := datain_width_w
//     datain_width_cmp := datain_width_cmp_w
//     datain_height_cmp := datain_height_cmp_w
//     datain_channel_cmp := datain_channel_cmp_w
//     sub_h_total_g0 := sub_h_total_w
//     sub_h_total_g1 := sub_h_total_w
//     sub_h_total_g2 := sub_h_total_w(2,1)
//     sub_h_total_g3 := sub_h_total_w
//     sub_h_total_g4 := sub_h_total_w
//     sub_h_total_g5 := sub_h_total_w
//     sub_h_total_g6 := sub_h_total_w
//     sub_h_total_g7 := sub_h_total_w
//     sub_h_total_g8 := sub_h_total_w
//     sub_h_total_g9 := sub_h_total_w
//     sub_h_total_g10 := sub_h_total_w
//     sub_h_total_g11 := sub_h_total_w
//     sub_h_cmp_g0 := sub_h_cmp_w
//     sub_h_cmp_g1 := sub_h_cmp_w
//     pixel_x_stride_odd := pixel_x_stride_w(0)
    
//     batch_cmp := batch_cmp_w
//     pixel_x_init := pixel_x_init_w
//     pixel_x_init_offset := pixel_x_init_offset_w
//     pixel_x_add := pixel_x_add_w(6,0)
//     pixel_x_byte_stride := pixel_x_byte_stride_w
//     pixel_ch_stride := pixel_ch_stride_w
//     x_dilate := x_dilate_w
//     y_dilate := y_dilate_w
//     pad_value := io.reg2dp_pad_value
    
    
//     entries_cmp := Cat("h0".asUInt(1.W), io.reg2dp_entries)
    
    
    
    
    
//     pra_precision := Fill(4, io.reg2dp_proc_precision)
// }
// when(layer_st_d1){

//     h_bias_2_stride := entries
//     h_bias_3_stride := entries
    
// }

//     last_slices := slice_left
//     last_entries := slice_entries_w 
// }
// }}


// ////////////////////////////////////////////////////////////////////////
// //  SLCG control signal                                               //
// ////////////////////////////////////////////////////////////////////////
// val slcg_wg_en = ShiftRegister(io.reg2dp_op_en & is_winograd, 3, false.B)

// /////////////////////////////////////////////////////////////
// ///// cbuf status management                             /////
// //////////////////////////////////////////////////////////////
// val cbuf_reset = io.sc2cdma_dat_pending_req
// val is_running_first = is_sg_running & ~is_sg_running_d1

// //================  Non-SLCG clock domain ================//
// val dat_rls = Wire(Bool())
// val sc2cdma_dat_slices_w = Wire(UInt(14.W))
// val sc2cdma_dat_entries_w =  Wire(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
// val dat_slice_avl = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(14.W))}
// val dat_entry_avl = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))}
// val dat_entry_st = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))}
// val dat_entry_end = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))}

// //////////////////////////////////// calculate how many avaliable dat slices in cbuf////////////////////////////////////
// val dat_slice_avl_add = Mux(io.cdma2sc_dat_updt, io.cdma2sc_dat_slices, "b0".asUInt(14.W))
// val dat_slice_avl_sub = Mux(dat_rls, sc2cdma_dat_slices_w, "b0".asUInt(14.W))
// val dat_slice_avl_w = Mux(cbuf_reset, "b0".asUInt(14.W), dat_slice_avl + dat_slice_avl_add - dat_slice_avl_sub)

// //////////////////////////////////// calculate how many avaliable dat entries in cbuf////////////////////////////////////
// val dat_entry_avl_add = Mux(io.cdma2sc_dat_updt, io.cdma2sc_dat_entries, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
// val dat_entry_avl_sub = Mux(dat_rls, c2cdma_dat_entries_w, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
// val dat_entry_avl_w = Mux(cbuf_reset,"b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), dat_entry_avl + dat_entry_avl_add - dat_entry_avl_sub)

// //////////////////////////////////// calculate avilable data entries start offset in cbuf banks ////////////////////////////////////
// // data_bank is the highest bank for storing data
// val dat_entry_st_inc = (dat_entry_st + dat_entry_avl_sub)(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
// val dat_entry_st_inc_wrap = (dat_entry_st_inc - Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B)))(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
// val is_dat_entry_st_wrap = (dat_entry_st_inc >= Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B)))
// val dat_entry_st_w = Mux(cbuf_reset,"b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(is_dat_entry_st_wrap,  dat_entry_st_inc_wrap,  dat_entry_st_inc))

// //////////////////////////////////// calculate avilable data entries end offset in cbuf banks////////////////////////////////////
// val dat_entry_end_inc = (dat_entry_end + dat_entry_avl_add)(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
// val dat_entry_end_inc_wrap = (dat_entry_end_inc - Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B)))(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
// val is_dat_entry_end_wrap = (dat_entry_end_inc >= Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B)))
// val dat_entry_end_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(is_dat_entry_end_wrap,  dat_entry_end_inc_wrap, dat_entry_end_inc))

// //////////////////////////////////// registers and assertions ////////////////////////////////////
// when(io.cdma2sc_dat_updt|dat_rls|cbuf_reset){
//     dat_slice_avl := dat_slice_avl_w
// }
// when(io.cdma2sc_dat_updt|dat_rls|cbuf_reset){
//     dat_entry_avl := dat_entry_avl_w
// }
// when(dat_rls|cbuf_reset){
//     dat_entry_st := dat_entry_st_w
// }
// when(io.cdma2sc_dat_updt|cbuf_reset){
//     dat_entry_end := dat_entry_end_w
// }
// //================  Non-SLCG clock domain end ================//


// withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {

// //////////////////////////////////////////////////////////////
// ///// cbuf status update                                 /////
// //////////////////////////////////////////////////////////////
// val dat_rsp_pvld = Wire(Bool())
// val dat_rsp_rls = Wire(Bool())
// val sub_rls = (dat_rsp_pvld & dat_rsp_rls)
// val reuse_rls = io.sg2dl_reuse_rls
// val sc2cdma_dat_updt_out = RegInit(false.B)
// val sc2cdma_dat_slices_out = RegInit("b0".asUInt(14.W))
// val sc2cdma_dat_entries_out = RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))

// dat_rls := (reuse_rls & orR(last_slices)) | (sub_rls & orR(rls_slices))
// sc2cdma_dat_slices_w = Mux(sub_rls, rls_slices, last_slices)
// sc2cdma_dat_entries_w = Mux(sub_rls, rls_entries, last_entries)
// sc2cdma_dat_updt_out := dat_rls
// when(dat_rls){
//     sc2cdma_dat_slices_out := sc2cdma_dat_slices_w
//     sc2cdma_dat_entries_out := sc2cdma_dat_entries_w
// }

// io.sc2cdma_dat_updt := sc2cdma_dat_updt_out
// io.sc2cdma_dat_slices := sc2cdma_dat_slices_out
// io.sc2cdma_dat_entries := sc2cdma_dat_entries_out

// //////////////////////////////////////////////////////////////
// ///// input sg2dl package                                 /////
// //////////////////////////////////////////////////////////////

// //////////////////////////////////////////////////////////////
// ///// generate data read sequence                        /////
// //////////////////////////////////////////////////////////////

// dl_in_pvld_d0 := 










// }}




    






     
//   }