// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import scala.math._
// import chisel3.iotesters.Driver

// class NV_NVDLA_CDMA_IMG_pack(implicit conf: cdmaConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         //img2sbuf
//         val img2sbuf_p_rd_data = Input(Vec(conf.ATMM_NUM, UInt(conf.ATMM.W)))
//         val img2sbuf_p_rd_addr = Output(Vec(conf.ATMM_NUM, UInt(8.W)))
//         val img2sbuf_p_rd_en = Output(Vec(conf.ATMM_NUM, Bool()))
        
//         val is_running = Input(Bool())
//         val layer_st = Input(Bool())

//         val pixel_bank = Input(UInt(6.W))
//         val pixel_data_expand = Input(Bool())
//         val pixel_data_shrink = Input(Bool())
//         val pixel_early_end = Input(Bool())
//         val pixel_packed_10b = Input(Bool())
//         val pixel_planar = Input(Bool())
//         val pixel_planar0_sft = Input(UInt(3.W))
//         val pixel_planar1_sft = Input(UInt(3.W))
//         val pixel_precision = Input(UInt(2.W))
//         val pixel_uint = Input(Bool())

//         val sg2pack_data_entries = Input(UInt(15.W))
//         val sg2pack_entry_end = Input(UInt(15.W))
//         val sg2pack_entry_mid = Input(UInt(15.W))
//         val sg2pack_entry_st = Input(UInt(15.W))
//         val sg2pack_height_total = Input(UInt(13.W))
//         val sg2pack_img_pd = Input(UInt(11.W))
//         val sg2pack_img_pvld = Input(Bool())
//         val sg2pack_mn_enable = Input(Bool())
//         val sg2pack_sub_h_end = Input(UInt(4.W))
//         val sg2pack_sub_h_mid = Input(UInt(4.W))
//         val sg2pack_sub_h_st = Input(UInt(4.W))
//         val status2dma_wr_idx = Input(UInt(15.W))

//         val img2cvt_dat_wr_addr =  Output(Vec(log2Ceil(conf.ATMM_NUM), UInt(17.W)))
//         val img2cvt_dat_wr_data = Output(Vec(log2Ceil(conf.ATMM_NUM), UInt(conf.DMAIF.W)))
//         val img2cvt_mn_wr_data = Output(Vec(log2Ceil(conf.ATMM_NUM), UInt((conf.BNUM*16).W)))
//         val img2cvt_dat_wr_pad_mask = Output(Vec(log2Ceil(conf.ATMM_NUM), UInt(conf.BNUM.W)))

//         val img2cvt_dat_wr_en = Output(Bool())
//         val img2cvt_dat_wr_info_pd = Output(UInt(12.W))
//         val img2status_dat_entries = Output(UInt(15.W))
//         val img2status_dat_slices = Output(UInt(14.W))
//         val img2status_dat_updt = Output(Bool())
//         val pack_is_done = Output(Bool())
//         val sg2pack_img_prdy = Output(Bool())

//         val reg2dp_datain_width = Input(UInt(13.W))
//         val reg2dp_datain_channel = Input(UInt(13.W))
//         val reg2dp_mean_ry = Input(UInt(16.W))
//         val reg2dp_mean_gu = Input(UInt(16.W))
//         val reg2dp_mean_bv = Input(UInt(16.W))
//         val reg2dp_mean_ax = Input(UInt(16.W))
//         val reg2dp_pad_left = Input(UInt(5.W))
//         val reg2dp_pad_right = Input(UInt(6.W))

//         val pwrbus_ram_pd = Input(UInt(32.W))
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
// ////////////////////////////////////////////////////////////////////////
// // signals from other modules                                         //
// ////////////////////////////////////////////////////////////////////////
// val is_running_d1 = RegInit(false.B)

// val img_pd = Mux(io.sg2pack_img_pvld,  io.sg2pack_img_pd, 0.U)

// val img_p0_burst = img_pd(3, 0)
// val img_p1_burst = img_pd(8, 4)
// val img_line_end = img_pd(9)
// val img_layer_end = img_pd(10)

// val is_first_running = ~is_running_d1 & io.is_running

// is_running_d1 := io.is_running
// ////////////////////////////////////////////////////////////////////////
// // general signals                                                    //
// ////////////////////////////////////////////////////////////////////////
// val data_width_mark_0 = RegInit("b0".asUInt(14.W))
// val data_width_mark_1 = RegInit("b0".asUInt(14.W))
// val data_width_mark_2 = RegInit("b0".asUInt(14.W))

// when(layer_st){
//     data_width_mark_0 := io.reg2dp_pad_left
//     data_width_mark_1 := io.reg2dp_pad_left +& io.reg2dp_datain_width +& 1.U
//     data_width_mark_2 := io.reg2dp_pad_left +& io.reg2dp_datain_width +& 1.U +& io.reg2dp_pad_right
// }
// //sft
// val lp_planar0_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
// val lp_planar1_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
// val rp_planar0_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
// val rp_planar1_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
// val zero_planar0_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
// val zero_planar1_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
// val data_planar0_add = RegInit("b0".asUInt(6.W))
// val data_planar1_add = RegInit("b0".asUInt(6.W))

// val lp_planar0_mask_sft_w = Cat(data_width_mark_0(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar0_sft
// val lp_planar1_mask_sft_w = Cat(data_width_mark_0(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar1_sft

// val rp_planar0_mask_sft_w = Cat(data_width_mark_1(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar0_sft
// val rp_planar1_mask_sft_w = Cat(data_width_mark_1(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar1_sft

// val zero_planar0_mask_sft_w = Cat(data_width_mark_2(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar0_sft
// val zero_planar1_mask_sft_w = Cat(data_width_mark_2(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar1_sft

// val data_planar0_add_w = 1.U << io.pixel_planar0_sft
// val data_planar1_add_w = 1.U << io.pixel_planar1_sft

// when(is_first_running){
//     lp_planar0_mask_sft := lp_planar0_mask_sft_w
//     lp_planar1_mask_sft := lp_planar1_mask_sft_w
//     rp_planar0_mask_sft := rp_planar0_mask_sft_w
//     rp_planar1_mask_sft := rp_planar1_mask_sft_w
//     zero_planar0_mask_sft := zero_planar0_mask_sft_w
//     zero_planar1_mask_sft := zero_planar1_mask_sft_w
//     data_planar0_add := data_planar0_add_w
//     data_planar1_add := data_planar1_add_w
// }

// ////////////////////////////////////////////////////////////////////////
// // Shared buffer read sequnce generator                               //
// ////////////////////////////////////////////////////////////////////////
// val rd_height_cnt = RegInit("b0".asUInt(13.W))
// val rd_height_en = Wire(Bool())

// val is_1st_height = ~(rd_height_cnt.orR)
// val is_last_height = (rd_height_cnt === io.sg2pack_height_total)
// val rd_height_cnt_inc = rd_height_cnt + 1.U
// val rd_height_cnt_w = Mux(is_first_running, 0.U, rd_height_cnt_inc)

// when(rd_height_en){
//     rd_height_cnt := rd_height_cnt_w
// }

// //////// sub height counter ////////
// val is_last_sub_h = true.B
// val rd_sub_h_cnt = "b0".asUInt(3.W)

// //////// loop cnt ////////
// // img_p0_burst[3:1],means img_p0_burst/2, 2 means atmm_num/per_dmaif
// val rd_loop_cnt = RegInit("b0".asUInt(4.W))
// val rd_loop_en = Wire(Bool())
// val rd_loop_cnt_limit = Wire(UInt(4.W))
// if(conf.ATMM_NUM == 1){
//     rd_loop_cnt_limit := img_p0_burst
// }
// else if(conf.ATMM_NUM == 2){
//     rd_loop_cnt_limit := img_p0_burst(3, 1) +& img_p0_burst(0)
// }
// val rd_loop_cnt_inc = rd_loop_cnt + 1.U
// val is_last_loop = (rd_loop_cnt_inc >= rd_loop_cnt_limit)
// val rd_loop_cnt_w = Mux(is_first_running | is_last_loop, 0.U, rd_loop_cnt_inc)
// when(rd_loop_en){   
//     rd_loop_cnt := rd_loop_cnt_w
// }

// //////// planar cnt ////////
// val rd_planar_cnt = RegInit(false.B)
// val rd_planar_en = Wire(Bool())

// val rd_planar_cnt_w = Mux(is_first_running | is_last_planar, false.B, ~rd_planar_cnt)
// val is_last_planar = ~io.pixel_planar | rd_planar_cnt

// when(rd_planar_en){
//     rd_planar_cnt := rd_planar_cnt_w
// }

// //////// partial burst cnt ////////
// val rd_pburst_cnt = RegInit("b0".asUInt(2.W))
// val rd_pburst_en = Wire(Bool())
// val rd_pburst_limit = Wire(UInt(2.W))
// if(conf.ATMM_NUM == 1){
//     rd_pburst_limit := Mux((rd_planar_cnt & (~is_last_loop | ~img_p1_burst(0))), "b1".asUInt(1.W), "b0".asUInt(2.W))
// }
// else if(conf.ATMM_NUM == 2){
//     rd_pburst_limit := Mux((rd_planar_cnt & (~is_last_loop | ~img_p0_burst(0))), "b1".asUInt(1.W), "b0".asUInt(2.W))
// }

// val is_last_pburst = (rd_pburst_cnt === rd_pburst_limit)
// val rd_pburst_cnt_w = Mux(is_first_running | is_last_pburst, "b0".asUInt(2.W), rd_pburst_cnt + 1.U)
// when(rd_pburst_en){
//     rd_pburst_cnt := rd_pburst_cnt_w
// }

// //////// control logic ////////
// val rd_vld = Wire(Bool())
// val rd_sub_h_end = Wire(Bool())
// val rd_local_vld = RegInit(false.B)

// io.sg2pack_img_prdy := rd_vld & rd_sub_h_end
// rd_vld := (io.sg2pack_img_pvld | rd_local_vld)
// val rd_local_vld_w = Mux(~io.is_running, false.B
//                      Mux(rd_sub_h_end, false.B,
//                      io.sg2pack_img_pvld, true.B, rd_local_vld))

// val rd_pburst_end = rd_vld & is_last_pburst
// val rd_planar_end = rd_vld & is_last_pburst & is_last_planar
// val rd_loop_end   = rd_vld & is_last_pburst & is_last_planar & is_last_loop
// rd_sub_h_end     := rd_vld & is_last_pburst & is_last_planar & is_last_loop & is_last_sub_h
// val rd_line_end   = rd_vld & is_last_pburst & is_last_planar & is_last_loop & is_last_sub_h & img_line_end
// rd_height_end    := rd_vld & is_last_pburst & is_last_planar & is_last_loop & is_last_sub_h & img_line_end & is_last_height
// rd_pburst_en     := is_first_running | rd_vld
// rd_planar_en     := is_first_running | (rd_pburst_end & pixel_planar)
// rd_loop_en       := is_first_running | rd_planar_end
// rd_height_en     := is_first_running | rd_line_end

// val rd_planar0_burst_end = rd_vld & is_last_pburst & ~rd_planar_cnt & is_last_loop
// val rd_planar1_burst_end = rd_vld & is_last_pburst & rd_planar_cnt & is_last_loop

// val rd_planar0_line_end = rd_vld & is_last_pburst & ~rd_planar_cnt & is_last_loop & is_last_sub_h & img_line_end
// val rd_planar1_line_end = rd_vld & is_last_pburst & rd_planar_cnt & is_last_loop & is_last_sub_h & img_line_end

// rd_local_vld := rd_local_vld_w
// val rd_vld_d1 = RegNext(rd_vld, false.B)

// ////////////////////////////////////////////////////////////////////////
// // read control logic generator                                       //
// ////////////////////////////////////////////////////////////////////////
// //////// read enalbe mask ////////
// val rd_planar0_rd_mask = Wire(UInt(conf.conf.ATMM_NUM.W))
// val rd_planar1_rd_mask = Wire(UInt(conf.conf.ATMM_NUM.W))
// val rd_rd_mask = Wire(UInt(conf.ATMM_NUM.W))
// val rd_p_vld = Wire(Vec(conf.ATMM_NUM, Bool()))
// val rd_idx_add = Wire(UInt(3.W))

// if(conf.ATMM_NUM == 1){
//     rd_planar0_rd_mask := 1.U
//     rd_planar1_rd_mask := 1.U
//     rd_p_vld(0) := rd_vld & rd_rd_mask
//     rd_idx_add := 1.U
// }
// if(conf.ATMM_NUM == 2){
//     rd_planar0_rd_mask := Mux(is_last_loop & is_last_pburst & img_p0_burst(0), 1.U, 3.U)
//     rd_planar1_rd_mask := Mux(is_last_loop & is_last_pburst & img_p1_burst(1), 1.U, 3.U)
//     rd_p_vld(0) := rd_vld & rd_rd_mask(0)
//     rd_p_vld(1) := rd_vld & rd_rd_mask(1)
//     rd_idx_add := Mux(rd_rd_mask(1), 2.U, 1.U)
// }

// rd_p_vld_d1 := RegNext(rd_p_vld.asUInt, 0.U)
// rd_rd_mask := Mux(rd_planar_cnt, rd_planar1_rd_mask, rd_planar0_rd_mask)

// //////// read address ////////
// val rd_p_planar0_idx_inc = Wire(Vec(conf.ATMM_NUM, UInt(8.W)))
// val rd_p_planar1_idx_inc = Wire(Vec(conf.ATMM_NUM, UInt(8.W)))
// val rd_p_planar0_idx_w = Wire(Vec(conf.ATMM_NUM, UInt(7.W)))
// val rd_p_planar1_idx_w = Wire(Vec(conf.ATMM_NUM, UInt(7.W)))














    
// }}





    
// object NV_NVDLA_CDMA_IMG_fifoDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_IMG_fifo())
// }