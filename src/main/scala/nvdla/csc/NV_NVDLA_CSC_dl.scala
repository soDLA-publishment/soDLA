package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CSC_dlIO(implicit conf: nvdlaConfig) extends Bundle{
    val nvdla_core_clk = Input(Clock())
    val nvdla_core_ng_clk = Input(Clock())

    val sc_state = Input(UInt(2.W))
    val sg2dl = Flipped(new csc_sg2dl_if) /* data valid */
    
    val cdma2sc_dat_updt = Flipped(ValidIO(new updt_entries_slices_if))
    val sc2cdma_dat_pending_req = Input(Bool())
    val sc2cdma_dat_updt = ValidIO(new updt_entries_slices_if)

    val sc2buf_dat_rd = new sc2buf_data_rd_if

    val sc2mac_dat_a = ValidIO(new csc2cmac_data_if)              /* data valid */
    val sc2mac_dat_b = ValidIO(new csc2cmac_data_if)             /* data valid */

    val reg2dp_op_en = Input(Bool())
    val reg2dp_conv_mode = Input(Bool())
    val reg2dp_batches = Input(UInt(5.W))
    val reg2dp_proc_precision = Input(UInt(2.W))
    val reg2dp_datain_format = Input(Bool()) 
    val reg2dp_skip_data_rls = Input(Bool())
    val reg2dp_datain_channel_ext = Input(UInt(13.W))
    val reg2dp_datain_height_ext = Input(UInt(13.W))
    val reg2dp_datain_width_ext = Input(UInt(13.W))
    val reg2dp_y_extension = Input(UInt(2.W))
    val reg2dp_weight_channel_ext = Input(UInt(13.W))
    val reg2dp_entries = Input(UInt(14.W))
    val reg2dp_dataout_width = Input(UInt(13.W))
    val reg2dp_rls_slices = Input(UInt(12.W))
    val reg2dp_conv_x_stride_ext = Input(UInt(3.W))
    val reg2dp_conv_y_stride_ext = Input(UInt(3.W))
    val reg2dp_x_dilation_ext = Input(UInt(5.W))
    val reg2dp_y_dilation_ext = Input(UInt(5.W))
    val reg2dp_pad_left = Input(UInt(5.W))
    val reg2dp_pad_top = Input(UInt(5.W))
    val reg2dp_pad_value = Input(UInt(16.W))
    val reg2dp_data_bank = Input(UInt(5.W))
    val reg2dp_pra_truncate = Input(UInt(2.W))

    val slcg_wg_en = Output(Bool())

}

class NV_NVDLA_CSC_dl(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new NV_NVDLA_CSC_dlIO)
/////////////////////////////////////////////////////////////////////////////////////////////
// Pipeline of Weight loader, for both compressed weight and uncompressed weight
//
//                      input_package
//                           |                     
//                      data request               
//                           |                     
//                      conv_buffer                
//                           |                     
//                      feature data---> data relase
//                        |     |                  
//                      REG    PRA                 
//                        |     |                  
//                        REGISTER                 
//                           |                     
//                          MAC                    
//
/////////////////////////////////////////////////////////////////////////////////////////////
withClock(io.nvdla_core_clk){
//////////////////////////////////////////////////////////////
///// status from sequence generator                     /////
//////////////////////////////////////////////////////////////
val is_sg_idle = (io.sc_state === 0.U)
val is_sg_running = (io.sc_state === 2.U)
val is_sg_done = (io.sc_state === 3.U)

val is_sg_running_d1 = RegNext(is_sg_running, false.B)

//////////////////////////////////////////////////////////////
///// input signals from registers                       /////
//////////////////////////////////////////////////////////////
val layer_st = io.reg2dp_op_en & is_sg_idle
val is_pixel = io.reg2dp_datain_format === 1.U
val is_conv = io.reg2dp_conv_mode === 0.U
val is_img = is_conv & is_pixel
val is_winograd = false.B
val data_batch_w = 1.U(6.W)
val batch_cmp_w = 0.U(5.W) 

//y_ex=0,sub_h_total=1;y_ex=1,sub_h_total=2; y_ext=2,sub_h_total=4; non_image, sub_h_total=1;
//sub_h_total means how many h lines are used in post-extention
val sub_h_total_w = Mux(is_img, "b1001".asUInt(4.W) << io.reg2dp_y_extension, "b01000".asUInt(5.W))(5,3)  
val sub_h_cmp_w = Mux(is_img, sub_h_total_w, 1.U)
val dataout_w_init = sub_h_cmp_w -& 1.U
val conv_x_stride_w = io.reg2dp_conv_x_stride_ext +& 1.U
val pixel_x_stride_w = MuxLookup(io.reg2dp_datain_channel_ext(1,0), conv_x_stride_w, 
                    Array(3.U -> Cat(conv_x_stride_w, "b0".asUInt(2.W)), //*4, after pre_extension
                          2.U -> (Cat(conv_x_stride_w, "b0".asUInt(1.W)) +& conv_x_stride_w)))//*3

val pixel_x_init_w = MuxLookup(io.reg2dp_y_extension, Mux(io.reg2dp_weight_channel_ext >= conf.CSC_ATOMC_HEX.U, Fill(conf.LOG2_ATOMC, true.B), io.reg2dp_weight_channel_ext(conf.LOG2_ATOMC-1, 0)),
                          Array(2.U -> (Cat(pixel_x_stride_w, "b0".asUInt(1.W)) + pixel_x_stride_w + io.reg2dp_weight_channel_ext(5, 0)), 
                                1.U -> (pixel_x_stride_w + io.reg2dp_weight_channel_ext(5, 0))))
val pixel_x_init_offset_w = io.reg2dp_weight_channel_ext(conf.LOG2_ATOMC-1, 0) +& 1.U
val pixel_x_add_w = MuxLookup(io.reg2dp_y_extension, pixel_x_stride_w,
                               Array(2.U -> Cat(pixel_x_stride_w, "b0".asUInt(2.W)), //*4, after post_extension
                                     1.U -> Cat(pixel_x_stride_w, "b0".asUInt(1.W))))//*2

val pixel_ch_stride_w = if(conf.NVDLA_CC_ATOMC_DIV_ATOMK==1|conf.NVDLA_CC_ATOMC_DIV_ATOMK==2)
                        Cat(pixel_x_stride_w, "b0".asUInt((conf.LOG2_ATOMK+1).W)) //stick to 2*atomK  no matter which config.
                        else
                        Cat(pixel_x_stride_w, "b0".asUInt((conf.LOG2_ATOMK+2).W)) //stick to 4*atomK  no matter which config.

val conv_y_stride_w =  io.reg2dp_conv_y_stride_ext +& 1.U
val x_dilate_w = Mux(is_img, 1.U, io.reg2dp_x_dilation_ext +& 1.U) 
val y_dilate_w = Mux(is_img, 1.U, io.reg2dp_y_dilation_ext +& 1.U) 

val layer_st_d1 = RegInit(false.B)
val data_batch = RegInit(Fill(6, false.B))
val rls_slices = RegInit(Fill(14, false.B))
val h_offset_slice = RegInit(Fill(14, false.B))
val entries = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
val entries_batch = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
val dataout_width_cmp = RegInit(Fill(13, false.B))
val pra_truncate = RegInit(Fill(8, false.B))
val rls_entries = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
val h_bias_0_stride = RegInit(Fill(12, false.B))
val h_bias_1_stride = RegInit(Fill(12, false.B))
val slice_left = RegInit(Fill(14, false.B))

//reg2dp_entries means entry per slice
val entries_single_w = (io.reg2dp_entries +& 1.U)(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
val entries_batch_w = (entries_single_w * data_batch_w)(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
val entries_w = entries_single_w
val h_offset_slice_w = data_batch_w * y_dilate_w  
val h_bias_0_stride_w = (entries * data_batch)(11, 0)
val h_bias_1_stride_w = (entries * h_offset_slice)(11, 0)
val rls_slices_w = io.reg2dp_rls_slices + 1.U
val slice_left_w = Mux(io.reg2dp_skip_data_rls, io.reg2dp_datain_height_ext +& 1.U, io.reg2dp_datain_height_ext -& io.reg2dp_rls_slices)
val slices_oprand = Mux(layer_st_d1, rls_slices, slice_left)
val slice_entries_w = (entries_batch * slices_oprand)(conf.CSC_ENTRIES_NUM_WIDTH-1, 0)
val dataout_width_cmp_w = io.reg2dp_dataout_width
val pra_truncate_w = Mux(io.reg2dp_pra_truncate === 3.U, 2.U, io.reg2dp_pra_truncate)

val is_winograd_d1 = RegInit(Fill(22, false.B))
val is_img_d1 = RegInit(Fill(34, false.B))
val data_bank = RegInit(Fill(5, false.B))
val datain_width = RegInit(Fill(14, false.B))
val datain_width_cmp = RegInit(Fill(13, false.B))
val datain_height_cmp = RegInit(Fill(13, false.B)) 
val datain_channel_cmp = RegInit(Fill(11, false.B)) 
val sub_h_total_g0 = RegInit("h1".asUInt(3.W))
val sub_h_total_g1 = RegInit("h1".asUInt(3.W))  
val sub_h_total_g2 = RegInit("h1".asUInt(2.W))  
val sub_h_total_g3 = RegInit("h1".asUInt(3.W))  
val sub_h_total_g4 = RegInit("h1".asUInt(3.W))    
val sub_h_total_g5 = RegInit("h1".asUInt(3.W))  
val sub_h_total_g6 = RegInit("h1".asUInt(3.W))  
val sub_h_total_g7 = RegInit("h1".asUInt(3.W))  
val sub_h_total_g8 = RegInit("h1".asUInt(3.W))  
val sub_h_total_g9 = RegInit("h1".asUInt(3.W))  
val sub_h_total_g10 = RegInit("h1".asUInt(3.W))  
val sub_h_total_g11 = RegInit("h1".asUInt(3.W)) 
val sub_h_cmp_g0 = RegInit("h1".asUInt(3.W)) 
val sub_h_cmp_g1 = RegInit("h1".asUInt(3.W))
val conv_x_stride = RegInit(Fill(4, false.B)) 
val conv_y_stride = RegInit(Fill(4, false.B))
val pixel_x_stride_odd = RegInit(false.B) 
val batch_cmp = RegInit(Fill(5, false.B))
val pixel_x_init = RegInit(Fill(6, false.B))
val pixel_x_init_offset = RegInit(Fill(7, false.B))
val pixel_x_add = RegInit(Fill(7, false.B))
val pixel_x_byte_stride = RegInit(Fill(7, false.B))
val pixel_ch_stride = RegInit(Fill(12, false.B))
val x_dilate = RegInit(Fill(6, false.B))
val y_dilate = RegInit(Fill(6, false.B))
val pad_value = RegInit(Fill(16, false.B))
val entries_cmp = RegInit(Fill(conf.CSC_ENTRIES_NUM_WIDTH, false.B))
val h_bias_2_stride = RegInit(Fill(conf.CBUF_ADDR_WIDTH, false.B))
val h_bias_3_stride = RegInit(Fill(conf.CBUF_ADDR_WIDTH, false.B))

val last_slices = RegInit(Fill(14, false.B)) 
val last_entries = RegInit(Fill(conf.CBUF_ADDR_WIDTH, false.B))
val pra_precision = RegInit(Fill(8, false.B))

layer_st_d1 := layer_st
when(layer_st){
    is_winograd_d1 := Fill(22, is_winograd) 
    is_img_d1 := Fill(34, is_img) 
    data_bank := io.reg2dp_data_bank + 1.U
    datain_width := io.reg2dp_datain_width_ext +& 1.U
    datain_width_cmp := io.reg2dp_datain_width_ext
    datain_height_cmp := io.reg2dp_datain_height_ext
    datain_channel_cmp := Cat(Fill(conf.LOG2_ATOMC-2, false.B), io.reg2dp_weight_channel_ext(12, conf.LOG2_ATOMC))
    sub_h_total_g0 := sub_h_total_w
    sub_h_total_g1 := sub_h_total_w
    sub_h_total_g2 := sub_h_total_w(2,1)
    sub_h_total_g3 := sub_h_total_w
    sub_h_total_g4 := sub_h_total_w
    sub_h_total_g5 := sub_h_total_w
    sub_h_total_g6 := sub_h_total_w
    sub_h_total_g7 := sub_h_total_w
    sub_h_total_g8 := sub_h_total_w
    sub_h_total_g9 := sub_h_total_w
    sub_h_total_g10 := sub_h_total_w
    sub_h_total_g11 := sub_h_total_w
    sub_h_cmp_g0 := sub_h_cmp_w
    sub_h_cmp_g1 := sub_h_cmp_w
    conv_x_stride := conv_x_stride_w
    conv_y_stride := conv_y_stride_w
    pixel_x_stride_odd := pixel_x_stride_w(0)
    data_batch := data_batch_w
    batch_cmp := batch_cmp_w
    pixel_x_init := pixel_x_init_w
    pixel_x_init_offset := pixel_x_init_offset_w
    pixel_x_add := pixel_x_add_w
    pixel_x_byte_stride := pixel_x_stride_w
    pixel_ch_stride := pixel_ch_stride_w
    x_dilate := x_dilate_w
    y_dilate := y_dilate_w
    pad_value := io.reg2dp_pad_value
    entries := entries_w
    entries_batch := entries_batch_w 
    entries_cmp := Cat("h0".asUInt(1.W), io.reg2dp_entries)
    h_offset_slice := h_offset_slice_w  
    rls_slices := rls_slices_w
    slice_left := slice_left_w 
    dataout_width_cmp := dataout_width_cmp_w
    pra_truncate := Fill(4, pra_truncate_w)
    pra_precision := Fill(4, io.reg2dp_proc_precision)
}
when(layer_st_d1){
    h_bias_0_stride := h_bias_0_stride_w
    h_bias_1_stride := h_bias_1_stride_w
    h_bias_2_stride := entries
    h_bias_3_stride := entries
    rls_entries := slice_entries_w
}
when(is_sg_done){
    last_slices := slice_left
    last_entries := slice_entries_w 
}


////////////////////////////////////////////////////////////////////////
//  SLCG control signal                                               //
////////////////////////////////////////////////////////////////////////
io.slcg_wg_en := false.B

/////////////////////////////////////////////////////////////
///// cbuf status management                             /////
//////////////////////////////////////////////////////////////
val cbuf_reset = io.sc2cdma_dat_pending_req
val is_running_first = is_sg_running & ~is_sg_running_d1

//================  Non-SLCG clock domain ================//
val dat_rls = Wire(Bool())
val sc2cdma_dat_slices_w = Wire(UInt(14.W))
val sc2cdma_dat_entries_w =  Wire(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
val dat_slice_avl = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(14.W))}
val dat_entry_avl = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))}
val dat_entry_st = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))}
val dat_entry_end = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))}

//////////////////////////////////// calculate how many avaliable dat slices in cbuf////////////////////////////////////
val dat_slice_avl_add = Mux(io.cdma2sc_dat_updt.valid, io.cdma2sc_dat_updt.bits.slices, "b0".asUInt(14.W))
val dat_slice_avl_sub = Mux(dat_rls, sc2cdma_dat_slices_w, "b0".asUInt(14.W))
val dat_slice_avl_w = Mux(cbuf_reset, "b0".asUInt(14.W), dat_slice_avl + dat_slice_avl_add - dat_slice_avl_sub)

//////////////////////////////////// calculate how many avaliable dat entries in cbuf////////////////////////////////////
val dat_entry_avl_add = Mux(io.cdma2sc_dat_updt.valid, io.cdma2sc_dat_updt.bits.entries, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
val dat_entry_avl_sub = Mux(dat_rls, sc2cdma_dat_entries_w, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
val dat_entry_avl_w = Mux(cbuf_reset,"b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), dat_entry_avl + dat_entry_avl_add - dat_entry_avl_sub)

//////////////////////////////////// calculate avilable data entries start offset in cbuf banks ////////////////////////////////////
// data_bank is the highest bank for storing data
val dat_entry_st_inc = dat_entry_st + dat_entry_avl_sub
val dat_entry_st_inc_wrap = dat_entry_st_inc - Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B))
val is_dat_entry_st_wrap = dat_entry_st_inc >= Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B))
val dat_entry_st_w = Mux(cbuf_reset,"b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(is_dat_entry_st_wrap,  dat_entry_st_inc_wrap,  dat_entry_st_inc))

//////////////////////////////////// calculate avilable data entries end offset in cbuf banks////////////////////////////////////
val dat_entry_end_inc = dat_entry_end + dat_entry_avl_add
val dat_entry_end_inc_wrap = dat_entry_end_inc - Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B))
val is_dat_entry_end_wrap = dat_entry_end_inc >= Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B))
val dat_entry_end_w = Mux(cbuf_reset, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), Mux(is_dat_entry_end_wrap,  dat_entry_end_inc_wrap, dat_entry_end_inc))

//////////////////////////////////// registers and assertions ////////////////////////////////////
when(io.cdma2sc_dat_updt.valid|dat_rls|cbuf_reset){ dat_slice_avl := dat_slice_avl_w }
when(io.cdma2sc_dat_updt.valid|dat_rls|cbuf_reset){ dat_entry_avl := dat_entry_avl_w }
when(dat_rls|cbuf_reset){ dat_entry_st := dat_entry_st_w }
when(io.cdma2sc_dat_updt.valid|cbuf_reset){ dat_entry_end := dat_entry_end_w }
//================  Non-SLCG clock domain end ================//

//////////////////////////////////////////////////////////////
///// cbuf status update                                 /////
//////////////////////////////////////////////////////////////
val dat_rsp_pvld = Wire(Bool())
val dat_rsp_rls = Wire(Bool())
val sub_rls = (dat_rsp_pvld & dat_rsp_rls)
val reuse_rls = io.sg2dl.reuse_rls

dat_rls := (reuse_rls & last_slices.orR) | (sub_rls & rls_slices.orR)
sc2cdma_dat_slices_w := Mux(sub_rls, rls_slices, last_slices)
sc2cdma_dat_entries_w := Mux(sub_rls, rls_entries, last_entries)

io.sc2cdma_dat_updt.valid := RegNext(dat_rls, false.B)
io.sc2cdma_dat_updt.bits.slices := RegEnable(sc2cdma_dat_slices_w, "b0".asUInt(14.W), dat_rls)
io.sc2cdma_dat_updt.bits.entries := RegEnable(sc2cdma_dat_entries_w, "b0".asUInt(conf.CSC_ENTRIES_NUM_WIDTH.W), dat_rls)

//////////////////////////////////////////////////////////////
///// input sg2dl package                                 /////
//////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////
///// generate data read sequence                        /////
//////////////////////////////////////////////////////////////
val total_depth = conf.CSC_DL_PIPELINE_ADDITION + conf.CSC_DL_PRA_LATENCY
val dl_in_pvld_d =  Wire(Bool()) +: 
                    Seq.fill(total_depth)(RegInit(false.B))
val dl_in_pd_d = Wire(UInt(31.W)) +: 
                 Seq.fill(total_depth)(RegInit("b0".asUInt(31.W)))

dl_in_pvld_d(0) := io.sg2dl.pd.valid
dl_in_pd_d(0) := io.sg2dl.pd.bits

for(t <- 0 to total_depth-1){
    dl_in_pvld_d(t+1) := dl_in_pvld_d(t)
    when(dl_in_pvld_d(t)){
        dl_in_pd_d(t+1) := dl_in_pd_d(t)
    }
}

val dl_in_pvld = dl_in_pvld_d(total_depth)
val dl_in_pd = dl_in_pvld_d(total_depth)

//: my $pipe_depth = 4;
val dl_pvld_d =  Wire(Bool()) +: 
                Seq.fill(4)(RegInit(false.B))
val dl_pd_d = Wire(UInt(31.W)) +: 
              Seq.fill(4)(RegInit("b0".asUInt(31.W)))

dl_pvld_d(0) := dl_in_pvld
dl_pd_d(0) := dl_in_pd

for(t <- 0 to 4-1){
    dl_pvld_d(t+1) := dl_pvld_d(t)
    when(dl_pvld_d(t)){
        dl_pd_d(t+1) := dl_pd_d(t)
    }
}

val dl_pvld = (sub_h_total_g0(2) & dl_pvld_d(1)) |
              (sub_h_total_g0(1) & dl_pvld_d(3)) |
              (sub_h_total_g0(0) & dl_pvld_d(4));
 
val dl_pd = (Fill(31, sub_h_total_g1(2)) & dl_pd_d(1)) |
            (Fill(31, sub_h_total_g1(1)) & dl_pd_d(3)) |
            (Fill(31, sub_h_total_g1(0)) & dl_pd_d(4));

// PKT_UNPACK_WIRE( csc_dat_pkg ,  dl_ ,  dl_pd )
val dl_w_offset = dl_pd(4, 0)   //this is weight offset
val dl_h_offset = dl_pd(9, 5)   //weight offset
val dl_channel_size = dl_pd(16, 10)
val dl_stripe_length = dl_pd(23, 17)
val dl_cur_sub_h = dl_pd(25, 24)
val dl_block_end = dl_pd(26)
val dl_channel_end = dl_pd(27)
val dl_group_end = dl_pd(28)
val dl_layer_end = dl_pd(29)
val dl_dat_release = dl_pd(30)

////////////////////////// batch up counter //////////////////////////
val is_batch_end = Wire(Bool())
val dat_exec_valid = Wire(Bool())
val batch_cnt = RegInit("b0".asUInt(5.W))

batch_cnt := Mux(layer_st, "b0".asUInt(5.W), 
             Mux(is_batch_end, "b0".asUInt(5.W),
             batch_cnt + 1.U))

is_batch_end := batch_cnt === batch_cmp

////////////////////////// sub height up counter //////////////////////////
val sub_h_cnt = RegInit("b0".asUInt(2.W))
val is_sub_h_end = Wire(Bool())

val sub_h_cnt_inc = sub_h_cnt + 1.U
is_sub_h_end := (sub_h_cnt_inc === sub_h_cmp_g0)
val sub_h_cnt_reg_en = layer_st | (((io.reg2dp_y_extension).orR) & dat_exec_valid)
when(sub_h_cnt_reg_en){
    sub_h_cnt := Mux(layer_st | is_sub_h_end, "b0".asUInt(2.W), sub_h_cnt_inc)
}

////////////////////////// stripe up counter //////////////////////////
val stripe_cnt = RegInit("b0".asUInt(7.W))
val is_stripe_equal = Wire(Bool())
val is_stripe_end = Wire(Bool())

val stripe_cnt_inc = stripe_cnt + 1.U
is_stripe_equal := is_batch_end & (stripe_cnt_inc === dl_stripe_length)
is_stripe_end := is_stripe_equal & is_sub_h_end
val stripe_cnt_reg_en = layer_st | (dat_exec_valid & is_batch_end)

when(stripe_cnt_reg_en){
    stripe_cnt := Mux(layer_st, "b0".asUInt(7.W),
                  Mux(is_stripe_equal & ~is_sub_h_end, "b0".asUInt(2.W),
                  Mux(is_stripe_end, "b0".asUInt(7.W),
                  stripe_cnt_inc)))
}

////////////////////////// pipe valid generator //////////////////////////
val dat_pipe_local_valid = RegInit(false.B)
val dat_pipe_valid_d1 = RegInit(false.B)
val dat_exec_valid_d1 = RegInit(false.B)

val dat_pipe_valid = Wire(Bool())
val dat_pipe_local_valid_w = Mux(dat_pipe_valid & is_stripe_equal, false.B,
                            Mux(dl_pvld, true.B,
                            dat_pipe_local_valid))
dat_pipe_valid := dl_pvld | dat_pipe_local_valid
dat_exec_valid := Mux(dl_pvld, true.B, 
                  Mux((~(stripe_cnt.orR) & ~(sub_h_cnt.orR) & ~(batch_cnt.orR)),
                  false.B, dat_exec_valid_d1))

dat_pipe_local_valid := dat_pipe_local_valid_w
dat_pipe_valid_d1 := dat_pipe_valid
dat_exec_valid_d1 := dat_exec_valid

////////////////////////// request bytes //////////////////////////
val dat_req_bytes_d1 = RegInit("b0".asUInt(8.W))
val dat_req_bytes = Cat("b0".asUInt(1.W), dl_channel_size)
when(dat_exec_valid){
    dat_req_bytes_d1 := dat_req_bytes
}

////////////////////////// output width coordinate counter //////////////////////////
// sub_h T, output will compute sub_h point in w direction
val dataout_w_cnt = RegInit("b0".asUInt(13.W))
val dataout_w_ori = RegInit("b0".asUInt(13.W))

val dataout_w_add = sub_h_cmp_g1
val dataout_w_cnt_inc = dataout_w_cnt + dataout_w_add 
val is_w_end = is_batch_end & is_sub_h_end & (dataout_w_cnt >= dataout_width_cmp)
val is_w_end_ahead = is_batch_end & (dataout_w_cnt >= dataout_width_cmp)
val dataout_w_cnt_w = Mux(layer_st, dataout_w_init,
                      Mux(is_stripe_end & ~dl_channel_end, dataout_w_ori,
                      Mux(is_w_end, dataout_w_init, dataout_w_cnt_inc)))
val dataout_w_cnt_reg_en = layer_st | (dat_exec_valid & is_batch_end & is_sub_h_end)
val dataout_w_ori_reg_en = layer_st | (dat_exec_valid & is_stripe_end & dl_channel_end)

when(dataout_w_cnt_reg_en){
    dataout_w_cnt := dataout_w_cnt_w
}
when(dataout_w_ori_reg_en){
    dataout_w_ori := dataout_w_cnt_w
}

////////////////////////// input channel coordinate counter, only feature  //////////////////////////
val datain_c_cnt = RegInit("b0".asUInt(11.W))

val is_last_channel = (datain_c_cnt === datain_channel_cmp)
val datain_c_cnt_reg_en = layer_st | (dat_exec_valid & is_stripe_end & dl_block_end)

when(datain_c_cnt_reg_en){
    datain_c_cnt := Mux(layer_st, "b0".asUInt(11.W), 
                    Mux(dl_channel_end, "b0".asUInt(11.W),
                    datain_c_cnt + 1.U))
}

////////////////////////// input width coordinate counter, feature/image dedicated counter //////////////////////////
val datain_w_cnt = RegInit("b0".asUInt(14.W))
val datain_w_ori = RegInit("b0".asUInt(14.W))
val pixel_w_cnt = RegInit("b0".asUInt(16.W))
val pixel_w_ori = RegInit("b0".asUInt(16.W)) 
val pixel_w_ch_ori = RegInit("b0".asUInt(16.W))
val channel_op_cnt = RegInit("h2".asUInt(13.W))
val dat_req_stripe_st = Wire(Bool())
val pixel_force_clr_d1 = RegInit(false.B)
val pixel_force_fetch_d1 = RegInit(false.B)

val datain_w_cnt_st = Mux(is_img, "b0".asUInt(14.W),
                      "b0".asUInt(13.W) -& io.reg2dp_pad_left)
val datain_w_cnt_inc = datain_w_cnt + conv_x_stride

//full data cube w counter,start form negtive, only for feature data. non-image, by element
val datain_w_cnt_w = Mux(layer_st, datain_w_cnt_st, 
                     Mux(is_stripe_end & ~dl_channel_end, datain_w_ori,
                     Mux(is_w_end, datain_w_cnt_st, datain_w_cnt_inc)))

val dl_w_offset_ext = dl_w_offset * x_dilate
val datain_w_cur = datain_w_cnt + dl_w_offset_ext
val datain_w_cnt_reg_en = layer_st | (dat_exec_valid & is_batch_end & is_sub_h_end & ~is_img_d1(0))
val datain_w_ori_reg_en = layer_st | (dat_exec_valid & is_stripe_end & dl_channel_end & ~is_img_d1(1))

//notice:after sub_h T, pixel_x_add elements in W direction is used by CMAC
val pixel_x_cnt_add = Mux(is_sub_h_end, pixel_x_add, "b0".asUInt(6.W))
//channel count.
val total_channel_op = Mux(io.reg2dp_weight_channel_ext(conf.LOG2_ATOMC-1, 0) === 0.U, io.reg2dp_weight_channel_ext(12, conf.LOG2_ATOMC),
                        io.reg2dp_weight_channel_ext(12, conf.LOG2_ATOMC)+1.U)
channel_op_cnt := Mux(dl_channel_end&is_stripe_end, 2.U,
                  Mux(dl_block_end&is_stripe_end, channel_op_cnt + 1.U,
                  channel_op_cnt))
val next_is_last_channel = (channel_op_cnt >= total_channel_op)

//notice, after pre-extention, image weight w_total <=128
val pixel_w_cnt_w = Mux(layer_st_d1, pixel_x_init,
                    Mux(is_stripe_end & dl_block_end & dl_channel_end & is_w_end, pixel_x_init,
                    Mux(is_stripe_end & dl_block_end & dl_channel_end & ~is_w_end, pixel_w_ch_ori + pixel_ch_stride,
                    Mux(is_stripe_end & dl_block_end & next_is_last_channel, pixel_w_ori + pixel_x_init_offset,
                    Mux(is_stripe_end & dl_block_end & ~next_is_last_channel, pixel_w_ori + conf.CSC_ENTRY_HEX.U,
                    Mux(is_stripe_end & ~dl_block_end, pixel_w_ori, pixel_w_cnt + pixel_x_cnt_add))))))

val pixel_w_cur = Cat(Fill(conf.LOG2_ATOMC-1, false.B), pixel_w_cnt(15, conf.LOG2_ATOMC)) //by entry
val pixel_w_cnt_reg_en = layer_st_d1 | (dat_exec_valid & is_img_d1(2) & (is_sub_h_end | is_w_end))
val pixel_w_ori_reg_en = layer_st_d1 | (dat_exec_valid & is_img_d1(3) & is_stripe_end & dl_block_end)
val pixel_ch_ori_reg_en = layer_st_d1 | (dat_exec_valid & is_img_d1(4) & is_stripe_end & dl_block_end & dl_channel_end)

val pixel_force_fetch = Mux(is_img_d1(0) & dat_req_stripe_st, true.B, Mux(pixel_force_clr_d1, false.B, pixel_force_fetch_d1))
val pixel_force_clr = is_img_d1(0) & is_sub_h_end & (pixel_force_fetch | pixel_force_fetch_d1)

when(datain_w_cnt_reg_en){
    datain_w_cnt := datain_w_cnt_w
    pixel_w_cnt := pixel_w_cnt_w
}
when(datain_w_ori_reg_en){
    datain_w_ori := datain_w_cnt_w
    pixel_w_ori := pixel_w_cnt_w
}
when(pixel_ch_ori_reg_en){
    pixel_w_ch_ori := pixel_w_cnt_w
}

////////////////////////// input height coordinate counter, feature/image both  //////////////////////////
// full data cube h counter, start form negative
val datain_h_cnt = RegInit("b0".asUInt(14.W))
val datain_h_ori = RegInit("b0".asUInt(14.W))

val datain_h_cnt_st = "b0".asUInt(14.W) - io.reg2dp_pad_top
val datain_h_cnt_inc = datain_h_cnt + conv_y_stride
val datain_h_cnt_w = Mux(layer_st | (is_stripe_end & dl_group_end), datain_h_cnt_st,
                     Mux(is_stripe_end & ~dl_channel_end, datain_h_ori,
                     Mux(is_w_end, datain_h_cnt_inc, datain_h_cnt)))
val datain_h_cnt_reg_en = layer_st | (dat_exec_valid & ((is_stripe_end & ~dl_channel_end) | is_w_end))
val datain_h_ori_reg_en = layer_st | (dat_exec_valid & is_stripe_end & dl_channel_end)
val dl_h_offset_ext = dl_h_offset * y_dilate
val datain_h_cur = datain_h_cnt + dl_h_offset_ext + sub_h_cnt

when(datain_h_cnt_reg_en){ datain_h_cnt := datain_h_cnt_w }
when(datain_h_ori_reg_en){ datain_h_ori := datain_h_cnt_w }

////////////////////////// fetch valid generate //////////////////////////
val dat_conv_req_dummy = (datain_w_cur(13))|(datain_w_cur > datain_width_cmp)|(datain_h_cur(13))|(datain_h_cur > datain_height_cmp)
val dat_wg_req_dummy = false.B
val dat_wg_req_skip = datain_w_cur(13, 2).orR & datain_w_cur(1) & stripe_cnt(6, 1).orR
val dat_img_req_dummy = datain_h_cur(13) | (datain_h_cur > datain_height_cmp)

//w address(in entry) is bigger than avilable entrys
val w_bias_w = Wire(UInt(14.W))
val dat_img_req_skip = w_bias_w(13, 2) > entries_cmp
val dat_req_dummy = Mux(is_img_d1(5), dat_img_req_dummy, dat_conv_req_dummy)
val dat_req_skip = is_img_d1(6) & dat_img_req_skip
val dat_req_valid = (dat_exec_valid & ~dat_req_dummy & ~dat_req_skip)

//Add corner case
val dat_req_sub_c_w = Mux(~is_img_d1(7), datain_c_cnt(0), dl_block_end)
val dat_req_sub_w_w = datain_w_cur(1, 0)
val dat_req_sub_w_st_en = dat_exec_valid & (sub_h_cnt === 0.U)
val dat_req_batch_index = batch_cnt
dat_req_stripe_st := dl_pvld
val dat_req_stripe_end = is_stripe_equal & dat_pipe_valid
val dat_req_channel_end = dl_channel_end
val dat_req_layer_end = dl_layer_end

// PKT_PACK_WIRE( nvdla_stripe_info ,  dat_req_ ,  dat_req_flag_w )
val dat_req_flag_w = Cat(dat_req_layer_end, dat_req_channel_end, dat_req_stripe_end, dat_req_stripe_st, dat_req_batch_index)

val dat_req_valid_d1 = RegInit(false.B)
val dat_req_sub_w_d1 = RegInit("b0".asUInt(2.W))
val dat_req_sub_h_d1 = RegInit("b0".asUInt(2.W))
val dat_req_sub_c_d1 = RegInit(false.B)
val dat_req_ch_end_d1 = RegInit(false.B)
val dat_req_dummy_d1 = RegInit(false.B)
val dat_req_cur_sub_h_d1 = RegInit("b0".asUInt(2.W))
val dat_req_sub_w_st_d1 = RegInit(false.B)
val dat_req_flag_d1 = RegInit("b0".asUInt(9.W))
val dat_req_rls_d1 = RegInit(false.B)

dat_req_valid_d1 := dat_req_valid
when(dat_exec_valid){
    dat_req_sub_w_d1 := dat_req_sub_w_w
    dat_req_sub_h_d1 := sub_h_cnt
    dat_req_sub_c_d1 := dat_req_sub_c_w
    dat_req_ch_end_d1 := is_last_channel
    dat_req_dummy_d1 := dat_exec_valid
    dat_req_cur_sub_h_d1 := dl_cur_sub_h
    dat_req_flag_d1 := dat_req_flag_w
    dat_req_rls_d1 := dl_dat_release & is_stripe_equal & dat_pipe_valid
    pixel_force_fetch_d1 := pixel_force_fetch
    pixel_force_clr_d1 := pixel_force_clr
}
when(dat_req_sub_w_st_en){
    dat_req_sub_w_st_d1 := dat_req_stripe_st
}

//////////////////////////////////////////////////////////////
///// generate data read address                         /////
//////////////////////////////////////////////////////////////
////////////////////////// data read index generator: 1st stage //////////////////////////
val c_bias = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))
val c_bias_d1 = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))
val h_bias_0_d1 = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))
val h_bias_1_d1 = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))
val h_bias_2_d1 = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))
val h_bias_3_d1 = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))
val w_bias_d1 = RegInit("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))

//channel bias, by w_in element
val c_bias_add = Mux(~is_img_d1(8), datain_width(11, 0), "b0".asUInt(12.W))
val c_bias_w = Mux(layer_st, 0.U, 
               Mux(is_stripe_end & dl_channel_end, 0.U, c_bias + c_bias_add))
val c_bias_reg_en = layer_st | (dat_exec_valid & is_stripe_end & dl_block_end)
val c_bias_d1_reg_en = c_bias =/= c_bias_d1

//height bias, by element
val h_bias_0_w = (datain_h_cnt * h_bias_0_stride)(conf.CBUF_ADDR_WIDTH-1, 0)
val h_bias_1_w = (dl_h_offset * h_bias_1_stride)(conf.CBUF_ADDR_WIDTH-1, 0)
val h_bias_2_w = (batch_cnt * h_bias_2_stride)(conf.CBUF_ADDR_WIDTH-1, 0)
val h_bias_3_w = Mux(layer_st, 0.U, sub_h_cnt * h_bias_3_stride)(conf.CBUF_ADDR_WIDTH-1, 0)
val h_bias_reg_en = Cat(layer_st | is_img_d1(9), dat_exec_valid)

//width bias, by entry in image, by element in feature data
val w_bias_int8 = Wire(UInt(15.W))

if(conf.NVDLA_CC_ATOMC_DIV_ATOMK==1){
    w_bias_int8 := Mux(is_img_d1(10), pixel_w_cur,   //by entry in image 
                   Mux(~is_last_channel | datain_c_cnt(0) , Cat("b0".asUInt(2.W), datain_w_cur(12, 0)),  //by element
                   Cat("b0".asUInt(2.W), datain_w_cur(12, 0))))    //by element, last channel and current c is even, atomC=atomM
}
else if(conf.NVDLA_CC_ATOMC_DIV_ATOMK==2){
    w_bias_int8 := Mux(is_img_d1(10), pixel_w_cur,   //by entry in image 
                   Mux(~is_last_channel , Cat("b0".asUInt(2.W), datain_w_cur(12, 0)),  //not last channel, by element
                   Mux(dat_req_bytes > conf.CSC_HALF_ENTRY_HEX.U, Cat("b0".asUInt(2.W), datain_w_cur(12, 0)),  //last channel & request >1/2*entry
                   Cat("b0".asUInt(3.W), datain_w_cur(12, 1)))))   //by element, last channel and current c is even, atomC=atomM
}
else if(conf.NVDLA_CC_ATOMC_DIV_ATOMK==4){
    w_bias_int8 := Mux(is_img_d1(10), pixel_w_cur,   //by entry in image 
                   Mux(dat_req_bytes > conf.CSC_HALF_ENTRY_HEX.U, Cat("b0".asUInt(2.W), datain_w_cur(12, 0)),  //last channel & request >1/2*entry
                   Mux(dat_req_bytes <= conf.CSC_HALF_ENTRY_HEX.U, Cat("b0".asUInt(4.W), datain_w_cur(12, 2)),  //last channel & request <=1/4*entry
                   Cat("b0".asUInt(3.W), datain_w_cur(12, 1)))))  //last channel & (1/4*entry<request<=1/2*entry
}

w_bias_w := w_bias_int8(13, 0)
val w_bias_reg_en = dat_exec_valid
val dat_req_base_d1 = dat_entry_st

when(c_bias_reg_en){
    c_bias := c_bias_w
}
when(c_bias_d1_reg_en){
    c_bias_d1 := c_bias
}
when(h_bias_reg_en(0)){
    h_bias_0_d1 := h_bias_0_w
    h_bias_1_d1 := h_bias_1_w
    h_bias_2_d1 := h_bias_2_w
}
when(h_bias_reg_en(1)){
    h_bias_3_d1 := h_bias_3_w
}
when(w_bias_reg_en){
    w_bias_d1 := w_bias_w
}

////////////////////////// data read index generator: 2st stage //////////////////////////
//////////////////////////////////////////////////////////////
///// sideband pipeline                                  /////
//////////////////////////////////////////////////////////////
val dat_req_sub_h_addr = RegInit(VecInit(Seq.fill(4)(Fill(conf.CBUF_ADDR_WIDTH, true.B))))
val sc2buf_dat_rd_en_out = RegInit(false.B)
val sc2buf_dat_rd_addr_out = RegInit(Fill(conf.CBUF_ADDR_WIDTH, true.B))
val dat_req_pipe_pvld = RegInit(false.B)
val dat_req_exec_pvld = RegInit(false.B)
val dat_req_pipe_sub_w = RegInit("b0".asUInt(2.W))
val dat_req_pipe_sub_h = RegInit("b0".asUInt(2.W)) 
val dat_req_pipe_sub_c = RegInit(false.B)
val dat_req_pipe_ch_end = RegInit(false.B)
val dat_req_pipe_bytes = RegInit("b0".asUInt(8.W))
val dat_req_pipe_dummy = RegInit(false.B)
val dat_req_pipe_cur_sub_h = RegInit("b0".asUInt(2.W))
val dat_req_pipe_sub_w_st = RegInit(false.B)
val dat_req_pipe_rls = RegInit(false.B)
val dat_req_pipe_flag = RegInit("b0".asUInt(9.W))

val h_bias_d1 = h_bias_0_d1 + h_bias_1_d1 + h_bias_2_d1 + h_bias_3_d1
val dat_req_addr_sum = dat_req_base_d1 + c_bias_d1 + h_bias_d1 + w_bias_d1
val is_dat_req_addr_wrap = dat_req_addr_sum >= Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B))
val dat_req_addr_wrap = dat_req_addr_sum - Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B))
val dat_req_addr_w = Mux(layer_st | dat_req_dummy_d1, Fill(conf.CBUF_ADDR_WIDTH, true.B), 
                     Mux(is_dat_req_addr_wrap, dat_req_addr_wrap, dat_req_addr_sum))    //get the adress sends to cbuf
val dat_req_addr_minus1 = dat_req_addr_w - 1.U
val is_dat_req_addr_minus1_wrap = (dat_req_addr_minus1 >= Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, false.B)))
val dat_req_addr_minus1_wrap = Cat(data_bank, Fill(conf.LOG2_CBUF_BANK_DEPTH, true.B))
val dat_req_addr_minus1_real = Mux(is_dat_req_addr_minus1_wrap, dat_req_addr_minus1_wrap, dat_req_addr_minus1)
val dat_req_addr_last = MuxLookup(dat_req_sub_h_d1, 0.U,
                        (0 to 3) map { i => i.U -> dat_req_sub_h_addr(i) })
val sc2buf_dat_rd_en_w = dat_req_valid_d1 & ((dat_req_addr_last =/= dat_req_addr_w) | pixel_force_fetch_d1)
val dat_req_sub_h_addr_en = VecInit((0 to 3) map 
{ i => layer_st | ((dat_req_valid_d1 | dat_req_dummy_d1) & (dat_req_sub_h_d1 === i.U))})


//CBUF_NO_SUPPORT_READ_JUMPING
val sc2buf_dat_rd_addr_w = dat_req_addr_w

for(i <- 0 to 3){
    when(dat_req_sub_h_addr_en(i)){
        dat_req_sub_h_addr(i) := dat_req_addr_w
    }
}

sc2buf_dat_rd_en_out := sc2buf_dat_rd_en_w
when(layer_st|sc2buf_dat_rd_en_w){sc2buf_dat_rd_addr_out := sc2buf_dat_rd_addr_w}

io.sc2buf_dat_rd.addr.valid := sc2buf_dat_rd_en_out
io.sc2buf_dat_rd.addr.bits := sc2buf_dat_rd_addr_out

dat_req_pipe_pvld := dat_pipe_valid_d1
dat_req_pipe_flag := dat_exec_valid_d1
when(dat_exec_valid_d1){ 
    dat_req_pipe_sub_w := dat_req_sub_w_d1
    dat_req_pipe_sub_h := dat_req_sub_h_d1
    dat_req_pipe_sub_c := dat_req_sub_c_d1
    dat_req_pipe_ch_end := dat_req_ch_end_d1
    dat_req_pipe_bytes := dat_req_bytes_d1
    dat_req_pipe_dummy := dat_req_dummy_d1
    dat_req_pipe_cur_sub_h := dat_req_cur_sub_h_d1
    dat_req_pipe_sub_w_st := dat_req_sub_w_st_d1
    dat_req_pipe_rls := dat_req_rls_d1
    dat_req_pipe_flag := dat_req_flag_d1
}

//////////////////////////////////////////////////////////////
///// sideband pipeline                                  /////
//////////////////////////////////////////////////////////////
val dat_req_exec_dummy = dat_req_pipe_dummy
val dat_req_exec_sub_h = dat_req_pipe_sub_h

// PKT_PACK_WIRE( csc_dat_req_pkg ,  dat_req_pipe_ ,  dat_req_pipe_pd )
val dat_req_pipe_pd = Cat(dat_req_pipe_flag(8, 0), dat_req_pipe_rls, dat_req_pipe_sub_w_st,
                        dat_req_pipe_dummy, dat_req_pipe_cur_sub_h(1, 0), dat_req_pipe_bytes(7, 0),
                        false.B, dat_req_pipe_ch_end, dat_req_pipe_sub_c, dat_req_pipe_sub_h(1, 0),
                        dat_req_pipe_sub_w(1, 0))

//add latency for data request contorl signal
val dat_rsp_pipe_pvld_d = Wire(Bool()) +: 
                          Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit(false.B))
val dat_rsp_pipe_pd_d = Wire(UInt(29.W)) +: 
                        Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit("b0".asUInt(29.W)))
val dat_rsp_exec_pvld_d = Wire(Bool()) +: 
                          Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit(false.B))
val dat_rsp_exec_dummy_d = Wire(Bool()) +: 
                          Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit(false.B))
val dat_rsp_exec_sub_h_d = Wire(UInt(2.W)) +: 
                           Seq.fill(conf.NVDLA_CBUF_READ_LATENCY)(RegInit("b0".asUInt(2.W)))

dat_rsp_pipe_pvld_d(0) := dat_req_pipe_pvld
dat_rsp_pipe_pd_d(0) := dat_req_pipe_pd
dat_rsp_exec_pvld_d(0) := dat_req_exec_pvld
dat_rsp_exec_dummy_d(0) := dat_req_exec_dummy
dat_rsp_exec_sub_h_d(0) := dat_req_exec_sub_h

for(t <- 0 to conf.NVDLA_CBUF_READ_LATENCY-1){
    dat_rsp_pipe_pvld_d(t+1) := dat_rsp_pipe_pvld_d(t)
    when(dat_rsp_pipe_pvld_d(t)){
        dat_rsp_pipe_pd_d(t+1) := dat_rsp_pipe_pd_d(t)
    }
    dat_rsp_exec_pvld_d(t+1) := dat_rsp_exec_pvld_d(t)
    when(dat_rsp_exec_pvld_d(t)){
        dat_rsp_exec_dummy_d(t+1) := dat_rsp_exec_dummy_d(t)
        dat_rsp_exec_sub_h_d(t+1) := dat_rsp_exec_sub_h_d(t)
    }
}  

val dat_rsp_pipe_pvld = dat_rsp_pipe_pvld_d(conf.NVDLA_CBUF_READ_LATENCY)
val dat_rsp_pipe_pd = dat_rsp_pipe_pd_d(conf.NVDLA_CBUF_READ_LATENCY)
val dat_rsp_exec_pvld = dat_rsp_exec_pvld_d(conf.NVDLA_CBUF_READ_LATENCY)
val dat_rsp_exec_dummy = dat_rsp_exec_dummy_d(conf.NVDLA_CBUF_READ_LATENCY)
val dat_rsp_exec_sub_h = dat_rsp_exec_sub_h_d(conf.NVDLA_CBUF_READ_LATENCY)

// PKT_UNPACK_WIRE( csc_dat_req_pkg ,  dat_rsp_pipe_ ,  dat_rsp_pipe_pd )
val dat_rsp_pipe_sub_w = dat_rsp_pipe_pd(1, 0)
val dat_rsp_pipe_sub_h = dat_rsp_pipe_pd(3, 2)
val dat_rsp_pipe_sub_c = dat_rsp_pipe_pd(4)
val dat_rsp_pipe_ch_end = dat_rsp_pipe_pd(5)
val dat_rsp_pipe_bytes = dat_rsp_pipe_pd(14, 7)
val dat_rsp_pipe_cur_sub_h = dat_rsp_pipe_pd(16, 15)
val dat_rsp_pipe_dummy = dat_rsp_pipe_pd(17)
val dat_rsp_pipe_sub_w_st = dat_rsp_pipe_pd(18)
val dat_rsp_pipe_rls = dat_rsp_pipe_pd(19)
val dat_rsp_pipe_flag = dat_rsp_pipe_pd(28, 20)

//////////////////////////////////////////////////////////////
///// dl data cache                                      /////
//////////////////////////////////////////////////////////////
val dat_l0c0_dummy = RegInit(true.B)
val dat_l1c0_dummy = RegInit(true.B)
val dat_l2c0_dummy = RegInit(true.B)
val dat_l3c0_dummy = RegInit(true.B)
val dat_l0c1_dummy = RegInit(true.B)
val dat_l1c1_dummy = RegInit(true.B)
val dat_l2c1_dummy = RegInit(true.B)
val dat_l3c1_dummy = RegInit(true.B)

val dat_l0c0 = Reg(UInt(conf.CBUF_ENTRY_BITS.W))
val dat_l1c0 = Reg(UInt(conf.CBUF_ENTRY_BITS.W)) 
val dat_l2c0 = Reg(UInt(conf.CBUF_ENTRY_BITS.W)) 
val dat_l3c0 = Reg(UInt(conf.CBUF_ENTRY_BITS.W)) 
val dat_l0c1 = Reg(UInt(conf.CBUF_ENTRY_BITS.W)) 
val dat_l1c1 = Reg(UInt(conf.CBUF_ENTRY_BITS.W)) 
val dat_l2c1 = Reg(UInt(conf.CBUF_ENTRY_BITS.W)) 
val dat_l3c1 = Reg(UInt(conf.CBUF_ENTRY_BITS.W)) 

val dat_l0c0_en = (io.sc2buf_dat_rd.data.valid & (dat_rsp_exec_sub_h === "h0".asUInt(2.W)))
val dat_l1c0_en = (io.sc2buf_dat_rd.data.valid & (dat_rsp_exec_sub_h === "h1".asUInt(2.W)))
val dat_l2c0_en = (io.sc2buf_dat_rd.data.valid & (dat_rsp_exec_sub_h === "h2".asUInt(2.W)))
val dat_l3c0_en = (io.sc2buf_dat_rd.data.valid & (dat_rsp_exec_sub_h === "h3".asUInt(2.W)))

//only winograd/image
val dat_wg_adv = false.B
val dat_l0c1_en = (dat_wg_adv & ~dat_rsp_exec_sub_h(0)) | (is_img_d1(12) & dat_l0c0_en & ~dat_l0c0_dummy)
val dat_l1c1_en = (dat_wg_adv & dat_rsp_exec_sub_h(0)) | (is_img_d1(13) & dat_l1c0_en & ~dat_l1c0_dummy)
val dat_l2c1_en = (is_img_d1(15) & dat_l2c0_en & ~dat_l2c0_dummy)
val dat_l3c1_en = (is_img_d1(16) & dat_l3c0_en & ~dat_l3c0_dummy)

val dat_dummy_l0_en = dat_rsp_exec_pvld & dat_rsp_exec_dummy & (dat_rsp_exec_sub_h === "h0".asUInt(2.W))
val dat_dummy_l1_en = dat_rsp_exec_pvld & dat_rsp_exec_dummy & (dat_rsp_exec_sub_h === "h1".asUInt(2.W))
val dat_dummy_l2_en = dat_rsp_exec_pvld & dat_rsp_exec_dummy & (dat_rsp_exec_sub_h === "h2".asUInt(2.W))
val dat_dummy_l3_en = dat_rsp_exec_pvld & dat_rsp_exec_dummy & (dat_rsp_exec_sub_h === "h3".asUInt(2.W))

val dat_l0_set = dat_l0c0_en | dat_dummy_l0_en
val dat_l1_set = dat_l1c0_en | dat_dummy_l1_en
val dat_l2_set = dat_l2c0_en | dat_dummy_l2_en
val dat_l3_set = dat_l3c0_en | dat_dummy_l3_en

dat_l0c0_dummy := Mux(dat_l0c0_en, false.B, Mux(dat_dummy_l0_en, true.B, dat_l0c0_dummy))
dat_l1c0_dummy := Mux(dat_l1c0_en, false.B, Mux(dat_dummy_l1_en, true.B, dat_l1c0_dummy))
dat_l2c0_dummy := Mux(dat_l2c0_en, false.B, Mux(dat_dummy_l2_en, true.B, dat_l2c0_dummy))
dat_l3c0_dummy := Mux(dat_l3c0_en, false.B, Mux(dat_dummy_l3_en, true.B, dat_l3c0_dummy))
dat_l0c1_dummy := Mux(dat_l0c1_en, false.B, Mux(dat_l0_set, dat_l0c0_dummy, dat_l0c1_dummy))
dat_l1c1_dummy := Mux(dat_l1c1_en, false.B, Mux(dat_l1_set & sub_h_total_g2.orR, dat_l1c0_dummy, dat_l1c1_dummy))
dat_l2c1_dummy := Mux(dat_l2c1_en, false.B, Mux(dat_l2_set & sub_h_total_g2(1), dat_l2c0_dummy, dat_l2c1_dummy))
dat_l3c1_dummy := Mux(dat_l3c1_en, false.B, Mux(dat_l3_set & sub_h_total_g2(1), dat_l3c0_dummy, dat_l3c1_dummy))

when(dat_l0c0_en){dat_l0c0 := io.sc2buf_dat_rd.data.bits}
when(dat_l1c0_en){dat_l1c0 := io.sc2buf_dat_rd.data.bits}
when(dat_l2c0_en){dat_l2c0 := io.sc2buf_dat_rd.data.bits}
when(dat_l3c0_en){dat_l3c0 := io.sc2buf_dat_rd.data.bits}
when(dat_l0c1_en){dat_l0c1 := dat_l0c0}
when(dat_l1c1_en){dat_l1c1 := dat_l1c0}
when(dat_l2c1_en){dat_l2c1 := dat_l2c0}
when(dat_l3c1_en){dat_l3c1 := dat_l3c0}

//////////////////////////////////////////////////////////////
///// response contorl                                   /////
//////////////////////////////////////////////////////////////
val rsp_sft_cnt_l0 = RegInit("b0".asUInt(8.W))
val rsp_sft_cnt_l1 = RegInit("b0".asUInt(8.W))
val rsp_sft_cnt_l2 = RegInit("b0".asUInt(8.W))
val rsp_sft_cnt_l3 = RegInit("b0".asUInt(8.W))
val rsp_sft_cnt_l0_ori = RegInit("b0".asUInt(8.W))
val rsp_sft_cnt_l1_ori = RegInit("b0".asUInt(8.W))
val rsp_sft_cnt_l2_ori = RegInit("b0".asUInt(8.W))
val rsp_sft_cnt_l3_ori = RegInit("b0".asUInt(8.W))

// PKT_PACK_WIRE( csc_dat_rsp_pkg ,  dat_rsp_pipe_ ,  dat_rsp_pd_d0 )
//: my $delay_depth = 4;
val dat_rsp_pvld_d = Wire(Bool()) +: 
                     Seq.fill(4)(RegInit(false.B))
val dat_rsp_pd_d = Wire(UInt(27.W)) +: 
                     Seq.fill(4)(RegInit("b0".asUInt(27.W))) 

dat_rsp_pvld_d(0) := dat_rsp_pipe_pvld
dat_rsp_pd_d(0) :=  Cat(dat_rsp_pipe_flag, dat_rsp_pipe_rls, dat_rsp_pipe_cur_sub_h, dat_rsp_pipe_bytes,
                        false.B, dat_rsp_pipe_ch_end, dat_rsp_pipe_sub_c, dat_rsp_pipe_sub_h, dat_rsp_pipe_sub_w) 

for(t <- 0 to 3){
    dat_rsp_pvld_d(t+1) := dat_rsp_pvld_d(t)
    when(dat_rsp_pvld_d(t)){
        dat_rsp_pd_d(t+1) := dat_rsp_pd_d(t)
    }
}   

dat_rsp_pvld := (sub_h_total_g3(2) & dat_rsp_pvld_d(4)) |
                   (sub_h_total_g3(1) & dat_rsp_pvld_d(2)) |
                   (sub_h_total_g3(0) & dat_rsp_pvld_d(1))

val dat_rsp_l0_pvld = dat_rsp_pvld_d(1)
val dat_rsp_l1_pvld = dat_rsp_pvld_d(2)
val dat_rsp_l2_pvld = dat_rsp_pvld_d(3)
val dat_rsp_l3_pvld = dat_rsp_pvld_d(4)

val dat_rsp_pd = (Fill(27, sub_h_total_g4(2)) & dat_rsp_pd_d(4)) |
                 (Fill(27, sub_h_total_g4(1)) & dat_rsp_pd_d(2)) |
                 (Fill(27, sub_h_total_g4(0)) & dat_rsp_pd_d(1))

val dat_rsp_l0_sub_c = dat_rsp_pd_d(1)(4);
val dat_rsp_l1_sub_c = dat_rsp_pd_d(2)(4);
val dat_rsp_l2_sub_c = dat_rsp_pd_d(3)(4);
val dat_rsp_l3_sub_c = dat_rsp_pd_d(4)(4);

val dat_rsp_l0_flag = dat_rsp_pd_d(1)(26, 18)
val dat_rsp_l1_flag = dat_rsp_pd_d(2)(26, 18)
val dat_rsp_l2_flag = dat_rsp_pd_d(3)(26, 18)
val dat_rsp_l3_flag = dat_rsp_pd_d(4)(26, 18)

val dat_rsp_l0_stripe_end = dat_rsp_l0_flag(6);
val dat_rsp_l1_stripe_end = dat_rsp_l1_flag(6);
val dat_rsp_l2_stripe_end = dat_rsp_l2_flag(6);
val dat_rsp_l3_stripe_end = dat_rsp_l3_flag(6);

// PKT_UNPACK_WIRE( csc_dat_rsp_pkg ,  dat_rsp_ ,  dat_rsp_pd )
val dat_rsp_sub_w = dat_rsp_pd(1, 0)
val dat_rsp_sub_h = dat_rsp_pd(3, 2)
val dat_rsp_sub_c = dat_rsp_pd(4)
val dat_rsp_ch_end = dat_rsp_pd(5)
val dat_rsp_bytes = dat_rsp_pd(14, 7)
val dat_rsp_cur_sub_h = dat_rsp_pd(16, 15)
dat_rsp_rls := dat_rsp_pd(17)
val dat_rsp_flag = dat_rsp_pd(26, 18)

// PKT_UNPACK_WIRE( nvdla_stripe_info ,  dat_rsp_ ,  dat_rsp_flag )
val dat_rsp_batch_index = dat_rsp_flag(4, 0)
val dat_rsp_stripe_st = dat_rsp_flag(5)
val dat_rsp_stripe_end = dat_rsp_flag(6)
val dat_rsp_channel_end = dat_rsp_flag(7)
val dat_rsp_layer_end = dat_rsp_flag(8)

val rsp_sft_cnt_l0_sub = Mux(dat_l0c0_en, conf.CSC_ENTRY_HEX.U, "b0".asUInt(8.W))
val rsp_sft_cnt_l1_sub = Mux(dat_l1c0_en, conf.CSC_ENTRY_HEX.U, "b0".asUInt(8.W))
val rsp_sft_cnt_l2_sub = Mux(dat_l2c0_en, conf.CSC_ENTRY_HEX.U, "b0".asUInt(8.W))
val rsp_sft_cnt_l3_sub = Mux(dat_l3c0_en, conf.CSC_ENTRY_HEX.U, "b0".asUInt(8.W))

val rsp_sft_cnt_l0_inc = Mux(pixel_x_byte_stride > conf.CSC_ENTRY_HEX.U, conf.CSC_ENTRY_HEX.U, rsp_sft_cnt_l0 + pixel_x_byte_stride - rsp_sft_cnt_l0_sub)
val rsp_sft_cnt_l1_inc = Mux(pixel_x_byte_stride > conf.CSC_ENTRY_HEX.U, conf.CSC_ENTRY_HEX.U, rsp_sft_cnt_l1 + pixel_x_byte_stride - rsp_sft_cnt_l1_sub)
val rsp_sft_cnt_l2_inc = Mux(pixel_x_byte_stride > conf.CSC_ENTRY_HEX.U, conf.CSC_ENTRY_HEX.U, rsp_sft_cnt_l2 + pixel_x_byte_stride - rsp_sft_cnt_l2_sub)
val rsp_sft_cnt_l3_inc = Mux(pixel_x_byte_stride > conf.CSC_ENTRY_HEX.U, conf.CSC_ENTRY_HEX.U, rsp_sft_cnt_l3 + pixel_x_byte_stride - rsp_sft_cnt_l3_sub)

//the data frm cbuf's low Bytes is always needed. High Bytes maybe unneeded.
val dat_rsp_l0_block_end = dat_rsp_l0_sub_c
val dat_rsp_l1_block_end = dat_rsp_l1_sub_c
val dat_rsp_l2_block_end = dat_rsp_l2_sub_c
val dat_rsp_l3_block_end = dat_rsp_l3_sub_c

val rsp_sft_cnt_l0_w = Mux(layer_st, conf.CSC_ENTRY_HEX.U,  //begin from C0
                       Mux(dat_rsp_l0_stripe_end & ~dat_rsp_l0_block_end, rsp_sft_cnt_l0_ori,
                       Mux(dat_rsp_l0_stripe_end & dat_rsp_l0_block_end, conf.CSC_ENTRY_HEX.U,
                       Mux(dat_dummy_l0_en, rsp_sft_cnt_l0_inc & conf.CSC_ENTRY_MINUS1_HEX.U,
                       rsp_sft_cnt_l0_inc))))
val rsp_sft_cnt_l1_w = Mux(layer_st, conf.CSC_ENTRY_HEX.U,  
                       Mux(dat_rsp_l1_stripe_end & ~dat_rsp_l1_block_end, rsp_sft_cnt_l1_ori,
                       Mux(dat_rsp_l1_stripe_end & dat_rsp_l1_block_end, conf.CSC_ENTRY_HEX.U,
                       Mux(dat_dummy_l1_en, rsp_sft_cnt_l1_inc & conf.CSC_ENTRY_MINUS1_HEX.U,
                       rsp_sft_cnt_l1_inc))))
val rsp_sft_cnt_l2_w = Mux(layer_st, conf.CSC_ENTRY_HEX.U,  
                       Mux(dat_rsp_l2_stripe_end & ~dat_rsp_l2_block_end, rsp_sft_cnt_l2_ori,
                       Mux(dat_rsp_l2_stripe_end & dat_rsp_l2_block_end, conf.CSC_ENTRY_HEX.U,
                       Mux(dat_dummy_l2_en, rsp_sft_cnt_l2_inc & conf.CSC_ENTRY_MINUS1_HEX.U,
                       rsp_sft_cnt_l2_inc))))
val rsp_sft_cnt_l3_w = Mux(layer_st, conf.CSC_ENTRY_HEX.U,  
                       Mux(dat_rsp_l3_stripe_end & ~dat_rsp_l3_block_end, rsp_sft_cnt_l3_ori,
                       Mux(dat_rsp_l3_stripe_end & dat_rsp_l3_block_end, conf.CSC_ENTRY_HEX.U,
                       Mux(dat_dummy_l3_en, rsp_sft_cnt_l3_inc & conf.CSC_ENTRY_MINUS1_HEX.U,
                       rsp_sft_cnt_l3_inc)))) 

val rsp_sft_cnt_l0_en = layer_st | (is_img_d1(17) & dat_rsp_l0_pvld)     
val rsp_sft_cnt_l1_en = layer_st | (is_img_d1(18) & dat_rsp_l1_pvld & (sub_h_total_g5 =/= "h1".asUInt(3.W)))    
val rsp_sft_cnt_l2_en = layer_st | (is_img_d1(19) & dat_rsp_l2_pvld & (sub_h_total_g5 === "h4".asUInt(3.W)))    
val rsp_sft_cnt_l3_en = layer_st | (is_img_d1(20) & dat_rsp_l3_pvld & (sub_h_total_g5 === "h4".asUInt(3.W)))                                           

val rsp_sft_cnt_l0_ori_en = layer_st | (is_img_d1(21) & dat_rsp_l0_pvld & dat_rsp_l0_stripe_end & dat_rsp_l0_block_end)
val rsp_sft_cnt_l1_ori_en = layer_st | (is_img_d1(22) & dat_rsp_l1_pvld & dat_rsp_l1_stripe_end & dat_rsp_l1_block_end & (sub_h_total_g6 =/= "h1".asUInt(3.W)))
val rsp_sft_cnt_l2_ori_en = layer_st | (is_img_d1(23) & dat_rsp_l2_pvld & dat_rsp_l2_stripe_end & dat_rsp_l2_block_end & (sub_h_total_g6 === "h4".asUInt(3.W)))
val rsp_sft_cnt_l3_ori_en = layer_st | (is_img_d1(24) & dat_rsp_l3_pvld & dat_rsp_l3_stripe_end & dat_rsp_l3_block_end & (sub_h_total_g6 === "h4".asUInt(3.W)))

when(rsp_sft_cnt_l0_en){ rsp_sft_cnt_l0 := rsp_sft_cnt_l0_w }
when(rsp_sft_cnt_l1_en){ rsp_sft_cnt_l1 := rsp_sft_cnt_l1_w }
when(rsp_sft_cnt_l2_en){ rsp_sft_cnt_l2 := rsp_sft_cnt_l2_w }
when(rsp_sft_cnt_l3_en){ rsp_sft_cnt_l3 := rsp_sft_cnt_l3_w }
when(rsp_sft_cnt_l0_ori_en){ rsp_sft_cnt_l0_ori := rsp_sft_cnt_l0_w }
when(rsp_sft_cnt_l1_ori_en){ rsp_sft_cnt_l1_ori := rsp_sft_cnt_l1_w }
when(rsp_sft_cnt_l2_ori_en){ rsp_sft_cnt_l2_ori := rsp_sft_cnt_l2_w }
when(rsp_sft_cnt_l3_ori_en){ rsp_sft_cnt_l3_ori := rsp_sft_cnt_l3_w }

//////////////////////////////////////////////////////////////
///// response data                                      /////
//////////////////////////////////////////////////////////////
//////////////// data for winograd ////////////////
//TODO：winograd need future update
//
//////////////// data for convlution ////////////////
val dat_rsp_pad_value = Fill(conf.CSC_ATOMC, pad_value(7,0))

val dat_rsp_l0c0 = Mux(dat_l0c0_dummy, dat_rsp_pad_value, dat_l0c0)
val dat_rsp_l1c0 = Mux(dat_l1c0_dummy, dat_rsp_pad_value, dat_l1c0)
val dat_rsp_l2c0 = Mux(dat_l2c0_dummy, dat_rsp_pad_value, dat_l2c0)
val dat_rsp_l3c0 = Mux(dat_l3c0_dummy, dat_rsp_pad_value, dat_l3c0)

val dat_rsp_l0c1 = Mux(dat_l0c1_dummy, dat_rsp_pad_value, dat_l0c1)
val dat_rsp_l1c1 = Mux(dat_l1c1_dummy, dat_rsp_pad_value, dat_l1c1)
val dat_rsp_l2c1 = Mux(dat_l2c1_dummy, dat_rsp_pad_value, dat_l2c1)
val dat_rsp_l3c1 = Mux(dat_l3c1_dummy, dat_rsp_pad_value, dat_l3c1)

//several atomM may combine together as an entry
val dat_rsp_conv_8b = Wire(UInt(conf.CBUF_ENTRY_BITS.W))

if(conf.NVDLA_CC_ATOMC_DIV_ATOMK==1){
    dat_rsp_conv_8b := Mux(is_img_d1(26), "b0".asUInt(conf.CBUF_ENTRY_BITS.W), 
                       dat_rsp_l0c0)
}
if(conf.NVDLA_CC_ATOMC_DIV_ATOMK==2){
    dat_rsp_conv_8b := Mux(is_img_d1(26), "b0".asUInt(conf.CBUF_ENTRY_BITS.W), 
                       Mux((dat_rsp_bytes <= conf.CSC_HALF_ENTRY_HEX.U)&(dat_rsp_sub_w(0) === "h0".asUInt(1.W)), Cat("b0".asUInt(conf.CSC_HALF_ENTRY_BITS.W), dat_rsp_l0c0(conf.CSC_HALF_ENTRY_BITS-1, 0)),
                       Mux((dat_rsp_bytes <= conf.CSC_HALF_ENTRY_HEX.U)&(dat_rsp_sub_w(0) === "h1".asUInt(1.W)), Cat("b0".asUInt(conf.CSC_HALF_ENTRY_BITS.W), dat_rsp_l0c0(conf.CSC_ENTRY_BITS-1, conf.CSC_HALF_ENTRY_BITS)),
                       dat_rsp_l0c0)))
}
if(conf.NVDLA_CC_ATOMC_DIV_ATOMK==4){
    dat_rsp_conv_8b := Mux(is_img_d1(26), "b0".asUInt(conf.CBUF_ENTRY_BITS.W), 
                       Mux((dat_rsp_bytes <= conf.CSC_HALF_ENTRY_HEX.U)&(dat_rsp_bytes > conf.CSC_QUAT_ENTRY_HEX.U)&(dat_rsp_sub_w(0) === "h0".asUInt(1.W)), Cat("b0".asUInt(conf.CSC_HALF_ENTRY_BITS.W), dat_rsp_l0c0(conf.CSC_HALF_ENTRY_BITS-1, 0)),
                       Mux((dat_rsp_bytes <= conf.CSC_HALF_ENTRY_HEX.U)&(dat_rsp_bytes > conf.CSC_QUAT_ENTRY_HEX.U)&(dat_rsp_sub_w(0) === "h1".asUInt(1.W)), Cat("b0".asUInt(conf.CSC_HALF_ENTRY_BITS.W), dat_rsp_l0c0(conf.CSC_ENTRY_BITS-1, conf.CSC_HALF_ENTRY_BITS)),
                       Mux((dat_rsp_bytes <= conf.CSC_QUAT_ENTRY_HEX.U)&(dat_rsp_sub_w === "h0".asUInt(2.W)), Cat("b0".asUInt(conf.CSC_3QUAT_ENTRY_BITS.W), dat_rsp_l0c0(conf.CSC_QUAT_ENTRY_BITS-1, 0)),
                       Mux((dat_rsp_bytes <= conf.CSC_QUAT_ENTRY_HEX.U)&(dat_rsp_sub_w === "h1".asUInt(2.W)), Cat("b0".asUInt(conf.CSC_3QUAT_ENTRY_BITS.W), dat_rsp_l0c0(conf.CSC_HALF_ENTRY_BITS-1, conf.CSC_QUAT_ENTRY_BITS)),
                       Mux((dat_rsp_bytes <= conf.CSC_QUAT_ENTRY_HEX.U)&(dat_rsp_sub_w === "h2".asUInt(2.W)), Cat("b0".asUInt(conf.CSC_3QUAT_ENTRY_BITS.W), dat_rsp_l0c0(conf.CSC_3QUAT_ENTRY_BITS-1, conf.CSC_HALF_ENTRY_BITS)),
                       Mux((dat_rsp_bytes <= conf.CSC_QUAT_ENTRY_HEX.U)&(dat_rsp_sub_w === "h3".asUInt(2.W)), Cat("b0".asUInt(conf.CSC_3QUAT_ENTRY_BITS.W), dat_rsp_l0c0(conf.CSC_ENTRY_BITS-1, conf.CSC_3QUAT_ENTRY_BITS)),
                       dat_rsp_l0c0)))))))
}
//transform from uint to vec of uint
val dat_rsp_conv = Wire(Vec(conf.CBUF_ENTRY_BITS/conf.CSC_BPE, UInt(conf.CSC_BPE.W)))
for(i <- 0 to conf.CBUF_ENTRY_BITS/conf.CSC_BPE - 1){
    dat_rsp_conv(i) := dat_rsp_conv_8b(i*conf.CSC_BPE + conf.CSC_BPE - 1, i*conf.CSC_BPE)
}

//////////////// data for image ////////////////
val dat_rsp_l0_sft_d1 = Reg(UInt(conf.CSC_HALF_ENTRY_BITS.W)) 
val dat_rsp_l0_sft_d2 = Reg(UInt(conf.CSC_QUAT_ENTRY_BITS.W)) 
val dat_rsp_l0_sft_d3 = Reg(UInt(conf.CSC_QUAT_ENTRY_BITS.W))

val dat_rsp_l1_sft_d2 = Reg(UInt(conf.CSC_QUAT_ENTRY_BITS.W))
val dat_rsp_l1_sft_d3 = Reg(UInt(conf.CSC_QUAT_ENTRY_BITS.W))

val dat_rsp_l2_sft_d3 = Reg(UInt(conf.CSC_QUAT_ENTRY_BITS.W))

val dat_rsp_l0_sft_in = Mux(~is_img_d1(27), 0.U, Cat(dat_rsp_l0c0, dat_rsp_l0c1))
val dat_rsp_l1_sft_in = Mux(~is_img_d1(28), 0.U, Cat(dat_rsp_l1c0, dat_rsp_l1c1))
val dat_rsp_l2_sft_in = Mux(~is_img_d1(29), 0.U, Cat(dat_rsp_l2c0, dat_rsp_l2c1))
val dat_rsp_l3_sft_in = Mux(~is_img_d1(30), 0.U, Cat(dat_rsp_l3c0, dat_rsp_l3c1))

val dat_rsp_l0_sft = (dat_rsp_l0_sft_in >> Cat(rsp_sft_cnt_l0, "b0".asUInt(3.W)))(conf.CBUF_ENTRY_BITS-1, 0)
val dat_rsp_l1_sft = (dat_rsp_l1_sft_in >> Cat(rsp_sft_cnt_l1, "b0".asUInt(3.W)))(conf.CBUF_ENTRY_BITS-1, 0)
val dat_rsp_l2_sft = (dat_rsp_l2_sft_in >> Cat(rsp_sft_cnt_l2, "b0".asUInt(3.W)))(conf.CBUF_ENTRY_BITS-1, 0)
val dat_rsp_l3_sft = (dat_rsp_l3_sft_in >> Cat(rsp_sft_cnt_l3, "b0".asUInt(3.W)))(conf.CBUF_ENTRY_BITS-1, 0)

val dat_rsp_img_8b = Mux(~is_img_d1(32), 0.U,
                     Mux(sub_h_total_g8 === "h4".asUInt(3.W), Cat(dat_rsp_l3_sft(conf.CSC_QUAT_ENTRY_BITS - 1, 0), dat_rsp_l2_sft_d3(conf.CSC_QUAT_ENTRY_BITS - 1, 0), dat_rsp_l1_sft_d3(conf.CSC_QUAT_ENTRY_BITS - 1, 0), dat_rsp_l0_sft_d3(conf.CSC_QUAT_ENTRY_BITS - 1, 0)),
                     Mux(sub_h_total_g8 === "h2".asUInt(3.W), Cat(dat_rsp_l1_sft(conf.CSC_HALF_ENTRY_BITS - 1, 0), dat_rsp_l0_sft_d1(conf.CSC_HALF_ENTRY_BITS - 1, 0)),
                     dat_rsp_l0_sft(conf.CBUF_ENTRY_BITS-1, 0))))
                     
//transform from uint to vec of uint
val dat_rsp_img = Wire(Vec(conf.CBUF_ENTRY_BITS/conf.CSC_BPE, UInt(conf.CSC_BPE.W)))
for(i <- 0 to conf.CBUF_ENTRY_BITS/conf.CSC_BPE - 1){
    dat_rsp_img(i) := dat_rsp_img_8b(i*conf.CSC_BPE + conf.CSC_BPE - 1, i*conf.CSC_BPE)
}

val dat_rsp_sft_d1_en = dat_rsp_l0_pvld & (sub_h_total_g9 =/= "h1".asUInt(3.W));
val dat_rsp_sft_d2_en = dat_rsp_l1_pvld & (sub_h_total_g9 === "h4".asUInt(3.W));
val dat_rsp_sft_d3_en = dat_rsp_l2_pvld & (sub_h_total_g9 === "h4".asUInt(3.W));

when(dat_rsp_sft_d1_en){
    dat_rsp_l0_sft_d1 := dat_rsp_l0_sft
}
when(dat_rsp_sft_d2_en){
    dat_rsp_l0_sft_d2 := dat_rsp_l0_sft_d1
    dat_rsp_l1_sft_d2 := dat_rsp_l1_sft
}
when(dat_rsp_sft_d3_en){
    dat_rsp_l0_sft_d3 := dat_rsp_l0_sft_d2
    dat_rsp_l1_sft_d3 := dat_rsp_l1_sft_d2
    dat_rsp_l2_sft_d3 := dat_rsp_l2_sft
}
//////////////// byte mask ////////////////
//sub_h_total=2, each sub_h align to 1/2 entry;
//sub_h_total=4, each sub_h align to 1/4 entry;

val dat_rsp_ori_mask = ~((Fill(conf.CSC_ATOMC, true.B) << dat_rsp_bytes)(conf.CSC_ATOMC-1, 0))

val dat_rsp_cur_h_mask_p1 = Mux(dat_rsp_cur_sub_h >= "h1".asUInt(2.W), Fill(conf.CSC_ATOMC, true.B), 0.U)
val dat_rsp_cur_h_mask_p2 = Mux(dat_rsp_cur_sub_h >= "h2".asUInt(2.W), Fill(conf.CSC_ATOMC_HALF, true.B), 0.U)
val dat_rsp_cur_h_mask_p3 = Mux(dat_rsp_cur_sub_h === "h3".asUInt(2.W), Fill(conf.CSC_ATOMC_HALF, true.B), 0.U)

val dat_rsp_cur_h_e2_mask_8b = Cat(dat_rsp_cur_h_mask_p1(conf.CSC_ATOMC_HALF-1, 0), Fill(conf.CSC_ATOMC_HALF, true.B))
val dat_rsp_cur_h_e4_mask_8b = Cat(dat_rsp_cur_h_mask_p3(conf.CSC_ATOMC_QUAT-1, 0), dat_rsp_cur_h_mask_p2(conf.CSC_ATOMC_QUAT-1, 0), dat_rsp_cur_h_mask_p1(conf.CSC_ATOMC_QUAT-1, 0), Fill(conf.CSC_ATOMC_QUAT, true.B))

val dat_rsp_mask_8b = Mux(sub_h_total_g11 === "h4".asUInt(3.W), Fill(4, dat_rsp_ori_mask(conf.CSC_ATOMC_QUAT-1, 0))&dat_rsp_cur_h_e4_mask_8b,
                      Mux(sub_h_total_g11 === "h2".asUInt(3.W), Fill(2, dat_rsp_ori_mask(conf.CSC_ATOMC_HALF-1, 0))&dat_rsp_cur_h_e2_mask_8b,
                      dat_rsp_ori_mask))


val dat_rsp_data_w = Mux(is_img_d1(33), dat_rsp_img, dat_rsp_conv)
val dat_rsp_mask_val_int8 = VecInit((0 to conf.CSC_ATOMC-1) map { i => dat_rsp_data_w(i).asUInt.orR})
val dat_rsp_mask_w = VecInit((0 to conf.CSC_ATOMC-1) map { i => dat_rsp_mask_8b(i)&dat_rsp_mask_val_int8(i)})
val dat_rsp_p0_vld_w = dat_rsp_pvld 

//////////////////////////////////////////////////////////////
///// latency register to balance with PRA cell          /////

//////////////////////////////////////////////////////////////
val dat_out_pvld = RegInit(false.B)
val dat_out_flag = RegInit("b0".asUInt(9.W))
val dat_out_bypass_mask = RegInit(VecInit(Seq.fill(conf.CSC_ATOMC)(false.B)))
val dat_out_bypass_data = Reg(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))

val dat_out_pvld_w = dat_rsp_pvld
val dat_out_flag_w = dat_rsp_flag

val dat_out_bypass_p0_vld_w = dat_rsp_p0_vld_w
val dat_out_bypass_mask_w = dat_rsp_mask_w
val dat_out_bypass_data_w = dat_rsp_data_w

dat_out_pvld := dat_out_pvld_w
when(dat_out_pvld_w){
    dat_out_flag := dat_out_flag_w
}
when(dat_out_bypass_p0_vld_w){
    dat_out_bypass_mask := dat_out_bypass_mask_w
}
for(i <- 0 to conf.CSC_ATOMC-1){
    when(dat_out_bypass_p0_vld_w & dat_out_bypass_mask_w(i)){
        dat_out_bypass_data(i) := dat_out_bypass_data_w(i)
    }
}

//////////////////////////////////////////////////////////////
///// finial registers                                   /////
//////////////////////////////////////////////////////////////
val dl_out_pvld = RegInit(false.B)
val dl_out_mask = RegInit(VecInit(Seq.fill(conf.CSC_ATOMC)(false.B)))
val dl_out_flag = RegInit("b0".asUInt(9.W))
val dl_out_data = Reg(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))

val dat_out_data = dat_out_bypass_data
val dat_out_mask = Mux(~dat_out_pvld, VecInit(Seq.fill(conf.CSC_ATOMC)(false.B)), 
                   dat_out_bypass_mask)

dl_out_pvld := dat_out_pvld
when(dat_out_pvld | dl_out_pvld){
    dl_out_mask := dat_out_mask
}
when(dat_out_pvld){
    dl_out_flag := dat_out_pvld
}

for(i <- 0 to conf.CSC_ATOMC-1){
    when(dat_out_mask(i)){
        dl_out_data(i) := dat_out_data(i)
    }
}

//////////////////////////////////////////////////////////////
///// registers for retiming                             /////
//////////////////////////////////////////////////////////////
val dl_out_pvld_d1 = RegNext(dl_out_pvld, false.B)
val sc2mac_dat_pd_w = Mux(~dl_out_pvld, "b0".asUInt(9.W), dl_out_flag)

io.sc2mac_dat_a.valid := RegNext(dl_out_pvld, false.B)
io.sc2mac_dat_b.valid := RegNext(dl_out_pvld, false.B)
io.sc2mac_dat_a.bits.pd := RegEnable(sc2mac_dat_pd_w, "b0".asUInt(9.W), dl_out_pvld | dl_out_pvld_d1)
io.sc2mac_dat_b.bits.pd := RegEnable(sc2mac_dat_pd_w, "b0".asUInt(9.W), dl_out_pvld | dl_out_pvld_d1)
io.sc2mac_dat_a.bits.mask := RegEnable(dl_out_mask, VecInit(Seq.fill(conf.CSC_ATOMC)(false.B)), dl_out_pvld | dl_out_pvld_d1)
io.sc2mac_dat_b.bits.mask := RegEnable(dl_out_mask, VecInit(Seq.fill(conf.CSC_ATOMC)(false.B)), dl_out_pvld | dl_out_pvld_d1)
for(i <- 0 to conf.CSC_ATOMC-1){
    io.sc2mac_dat_a.bits.data(i) := RegEnable(dl_out_data(i), dl_out_mask(i))
    io.sc2mac_dat_b.bits.data(i) := RegEnable(dl_out_data(i), dl_out_mask(i))
}



}}


object NV_NVDLA_CSC_dlDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CSC_dl())
}


    



