// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver


// class NV_NVDLA_CDMA_IMG_sg(implicit conf: cdmaConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //nvdla core clock
//         val nvdla_core_clk = Input(Clock())
//         //cvif
//         val img_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None 
//         val img_dat2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//         val img_dat2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//         val cvif2img_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None
//         val cvif2img_dat_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//         val cvif2img_dat_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//         //mcif
//         val img_dat2mcif_rd_req_pd = Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
//         val img_dat2mcif_rd_req_valid = Output(Bool())
//         val img_dat2mcif_rd_req_ready = Input(Bool())
//         val mcif2img_dat_rd_rsp_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
//         val mcif2img_dat_rd_rsp_valid = Input(Bool())
//         val mcif2img_dat_rd_rsp_ready = Output(Bool())
//         //status
//         val img2status_dat_entries = Input(UInt(15.W))
//         val img2status_dat_updt = Input(Bool())
//         //state
//         val is_running = Input(Bool())
//         val layer_st = Input(Bool())
//         //pixel
//         val pixel_order = Input(UInt(11.W))
//         val pixel_planar = Input(Bool())
//         val pixel_planar0_bundle_limit = Input(UInt(4.W))
//         val pixel_planar0_bundle_limit_1st = Input(UInt(4.W))
//         val pixel_planar0_byte_sft = Input(UInt(conf.ATMMBW.W))
//         val pixel_planar1_byte_sft = Input(UInt(conf.ATMMBW.W))
//         val pixel_planar0_lp_burst = Input(UInt(4.W))
//         val pixel_planar0_lp_vld = Input(Bool())
//         val pixel_planar0_rp_burst = Input(UInt(4.W))
//         val pixel_planar0_rp_vld = Input(Bool())
//         val pixel_planar0_width_burst = Input(UInt(14.W)) 
//         val pixel_planar1_bundle_limit = Input(UInt(5.W))
//         val pixel_planar1_bundle_limit_1st = Input(UInt(5.W)) 
//         val pixel_planar1_lp_burst = Input(UInt(3.W))
//         val pixel_planar1_lp_vld = Input(Bool())
//         val pixel_planar1_rp_burst = Input(UInt(3.W))
//         val pixel_planar1_rp_vld = Input(Bool())
//         val pixel_planar1_width_burst = Input(UInt(14.W))
//         //pwr
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         //sg2pack
//         val sg2pack_img_prdy = Input(Bool())
//         val sg2pack_data_entries = Output(UInt(15.W))
//         val sg2pack_entry_end = Output(UInt(15.W))
//         val sg2pack_entry_mid = Output(UInt(15.W))
//         val sg2pack_entry_st = Output(UInt(15.W))
//         val sg2pack_height_total = Output(UInt(13.W))
//         val sg2pack_img_pd = Output(UInt(11.W))
//         val sg2pack_img_pvld = Output(Bool())
//         val sg2pack_mn_enable = Output(Bool())
//         val sg2pack_sub_h_end = Output(UInt(4.W))
//         val sg2pack_sub_h_mid = Output(UInt(4.W))
//         val sg2pack_sub_h_st = Output(UInt(4.W))
//         val sg_is_done = Output(Bool())
//         //status2dma
//         val status2dma_free_entries = Input(UInt(15.W))
//         val status2dma_fsm_switch = Input(Bool())
//         //img2sbuf
//         val img2sbuf_p0_wr_data = Output(UInt(conf.ATMM.W))
//         val img2sbuf_p0_wr_addr = Output(UInt(8.W))
//         val img2sbuf_p0_wr_en = Output(Bool())

//         val img2sbuf_p1_wr_data = if(conf.ATMM_NUM > 1) Some(Output(UInt(conf.ATMM.W))) else None
//         val img2sbuf_p1_wr_addr = if(conf.ATMM_NUM > 1) Some(Output(UInt(8.W))) else None
//         val img2sbuf_p1_wr_en = if(conf.ATMM_NUM > 1) Some(Output(Bool())) else None
//         //reg2dp
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_pixel_y_offset = Input(UInt(3.W))
//         val reg2dp_datain_height = Input(UInt(13.W))
//         val reg2dp_datain_ram_type = Input(Bool())
//         val reg2dp_datain_addr_high_0 = Input(UInt(32.W))
//         val reg2dp_datain_addr_low_0 = Input(UInt(32.W))
//         val reg2dp_datain_addr_high_1 = Input(UInt(32.W))
//         val reg2dp_datain_addr_low_1 = Input(UInt(32.W))
//         val reg2dp_line_stride = Input(UInt(32.W))
//         val reg2dp_uv_line_stride = Input(UInt(32.W))
//         val reg2dp_mean_format = Input(Bool())
//         val reg2dp_entries = Input(UInt(14.W))
//         val reg2dp_dma_en = Input(Bool())

//         val dp2reg_img_rd_stall = Output(UInt(32.W))
//         val dp2reg_img_rd_latency = Output(UInt(32.W))

//     })
// //     .
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
// // general signal                                                     //
// ////////////////////////////////////////////////////////////////////////
//     val is_running_d1 = RegInit(false.B)
//     val mn_enable_d1 = RegInit(false.B)
//     val data_height = RegInit("b0".asUInt(14.W))
//     val height_cnt_total = RegInit("b0".asUInt(13.W))
//     val data_entries = RegInit("b0".asUInt(15.W))

//     val planar1_enable = io.pixel_planar
//     val data_height_w = io.reg2dp_datain_height +& 1.U
//     val mn_enable = (io.reg2dp_mean_format === 1.U)
//     val data_entries_w = io.reg2dp_entries + 1.U
//     val is_first_running = io.is_running & ~is_running_d1

//     is_running_d1 := is_running
//     when(layer_st){
//         mn_enable_d1 := mn_enable
//         data_height := data_height_w
//         height_cnt_total := reg2dp_datain_height
//         data_entries := data_entries_w

//     }
// ////////////////////////////////////////////////////////////////////////
// //  generator preparing parameters                                    //
// ////////////////////////////////////////////////////////////////////////
// ///////////// sub_h for total control /////////////
//     val pre_sub_h_st_d1 = RegInit("b0".asUInt(4.W))
//     val pre_sub_h_mid_d1 = RegInit("b0".asUInt(4.W))
//     val pre_sub_h_end_d1 = RegInit("b0".asUInt(4.W))
//     val pre_entry_st_d1 = RegInit("b0".asUInt(15.W))
//     val pre_entry_mid_d1 = RegInit("b0".asUInt(15.W))
//     val pre_entry_end_d1 = RegInit("b0".asUInt(15.W))

//     val sub_h_st_limit = "b1".asUInt(4.W)
//     val sub_h_mid_w = "h1".asUInt(4.W)
//     val sub_h_end_limit = "h1".asUInt(4.W)

//     val sub_h_st_sel = (~data_height(13, 4)).orR && (data_height(3, 0) <= sub_h_st_limit)
//     val sub_h_end_sel = (~data_height(13, 4)).orR && (data_height(3, 0) <= sub_h_end_limit)

//     val sub_h_st_w = Mux(sub_h_st_sel, data_height(3, 0), sub_h_st_limit)
//     val sub_h_end_w = Mux(sub_h_end_sel, data_height(3, 0), sub_h_end_limit)
//     val pre_entry_st_w = (sub_h_st_w * data_entries)(14, 0)
//     val pre_entry_end_w = (sub_h_end_w * data_entries)(14, 0)

//     when(is_first_running){
//         pre_sub_h_st_d1 := sub_h_st_w
//         pre_sub_h_mid_d1 := sub_h_mid_w
//         pre_sub_h_end_d1 := sub_h_end_w
//         pre_entry_st_d1 := pre_entry_st_w
//         pre_entry_mid_d1 := data_entries
//         pre_entry_end_d1 := pre_entry_end_w
//     }

// ////////////////////////////////////////////////////////////////////////
// //  request generator for input image                                 //
// ////////////////////////////////////////////////////////////////////////
//     val src_dummy = "h0".asUInt(2.W)
//     val src_p0 = "h1".asUInt(2.W)
//     val src_p1 = "h2".asUInt(2.W)

//     ///////////// height counter /////////////
//     val req_height_cnt = RegInit("b0".asUInt(13.W))

//     val is_1st_height = ~(req_height_cnt.orR)
//     val is_last_height = (req_height_cnt === height_cnt_total)
//     val req_height_cnt_inc = req_height_cnt + 1.U
//     val req_height_cnt_w = Mux(is_first_running, "b0".asUInt(13.W), req_height_cnt_inc)

//     when(req_height_en){
//         req_height_cnt := req_height_cnt_w
//     }

//     ///////////// image planar count /////////////
//     val req_img_planar_cnt = RegInit(false.B)

//     val is_img_last_planar = (req_img_planar_cnt === io.pixel_planar)
//     val req_img_planar_cnt_w = Mux((is_first_running | is_img_last_planar), false.B,  ~req_img_planar_cnt)

//     when(req_img_planar_en){
//         req_img_planar_cnt := req_img_planar_cnt_w
//     }

//     ///////////// image planar 0 bundle and burst count /////////////
//     val req_img_p0_bundle_cnt = RegInit("b0".asUInt(4.W))
//     val is_p0_bundle_end = Wire(Bool())
//     val is_p0_last_burst = Wire(Bool())
//     val req_img_p0_cur_burst = Wire(UInt(4.W))

//     val req_img_p0_bundle_cnt_w = Mux(is_first_running | is_p0_last_burst, io.pixel_planar0_bundle_limit_1st,
//                                   Mux(is_p0_bundle_end, io.pixel_planar0_bundle_limit,
//                                   req_img_p0_bundle_cnt - req_img_p0_cur_burst))
    
//     when(req_img_p0_burst_en){
//         req_img_p0_bundle_cnt := req_img_p0_bundle_cnt_w
//     }


//     val req_img_p0_burst_cnt = RegInit("b0".asUInt(14.W))
//     val req_img_p0_sec_cnt = RegInit("b0".asUInt(2.W))
//     val is_p0_cur_sec_end = Wire(Bool())

//     val req_img_p0_burst_cnt_dec = req_img_p0_burst_cnt -& req_img_p0_bundle_cnt
//     req_img_p0_cur_burst := Mux(req_img_p0_burst_cnt_dec(14), req_img_p0_burst_cnt(3, 0), req_img_p0_bundle_cnt)
//     val req_img_p0_burst_cnt_w = Mux((is_first_running | is_p0_last_burst) & io.pixel_planar0_lp_vld, io.pixel_planar0_lp_burst, 
//                                  Mux((is_first_running | is_p0_last_burst) & ~io.pixel_planar0_lp_vld, io.pixel_planar0_width_burst,
//                                  Mux((req_img_p0_sec_cnt === 0.U)&is_p0_cur_sec_end, io.pixel_planar0_width_burst,
//                                  Mux((req_img_p0_sec_cnt === 1.U)&is_p0_cur_sec_end, io.pixel_planar0_rp_burst,
//                                  req_img_p0_burst_cnt_dec(13, 0)))))
//     val req_img_p0_sec_cnt_w = Mux((is_first_running | is_p0_last_burst) & io.pixel_planar0_lp_vld, 0.U, 
//                                Mux((is_first_running | is_p0_last_burst) & io.pixel_planar0_lp_vld, 1.U, 
//                                req_img_p0_sec_cnt + 1.U))
//     is_p0_cur_sec_end := (req_img_p0_burst_cnt <= req_img_p0_bundle_cnt)
//     val is_p0_1st_burst = ((req_img_p0_burst_cnt === io.req_img_p0_burst_cnt)&(req_img_p0_sec_cnt === 0.U))|
//                           ((req_img_p0_burst_cnt === io.pixel_planar0_width_burst)&(req_img_p0_sec_cnt === 1.U)&~pixel_planar0_lp_vld)
//     is_p0_last_burst := (is_p0_cur_sec_end & (req_img_p0_sec_cnt === 1.U) & ~io.pixel_planar0_rp_vld)|
//                            (is_p0_cur_sec_end & (req_img_p0_sec_cnt === 2.U))
//     val is_p0_bundle_end = (req_img_p0_cur_burst == req_img_p0_bundle_cnt) | is_p0_last_burst
//     val req_img_p0_burst_size = req_img_p0_cur_burst
//     val is_p0_req_real = (req_img_p0_sec_cnt === 1.U)

//     when(req_img_p0_burst_en){
//         req_img_p0_burst_cnt := req_img_p0_burst_cnt_w
//     }
//     when(req_img_p0_sec_en){
//         req_img_p0_sec_cnt := req_img_p0_sec_cnt_w
//     }

//     ///////////// image planar 1 bundle and burst count /////////////
//     val req_img_p1_bundle_cnt = RegInit("b0".asUInt(5.W))
//     val req_img_p1_burst_cnt = RegInit("b0".asUInt(14.W))
//     val req_img_p1_sec_cnt = RegInit("b0".asUInt(2.W))
//     val req_img_p1_cur_burst = Wire(UInt(5.W))
//     val is_p1_cur_sec_end = Wire(Bool())

//     val req_img_p1_bundle_cnt_w = Mux((is_first_running | is_p1_last_burst), io.pixel_planar1_bundle_limit_1st,
//                                   Mux(is_p1_bundle_end, io.pixel_planar1_bundle_limit,
//                                   req_img_p1_bundle_cnt - req_img_p1_cur_burst))
//     val req_img_p1_burst_cnt_dec = req_img_p1_burst_cnt -& req_img_p1_bundle_cnt
//     req_img_p1_cur_burst := Mux(req_img_p1_burst_cnt_dec(14), req_img_p1_burst_cnt(4, 0), req_img_p1_bundle_cnt)
//     val req_img_p1_burst_cnt_w = Mux((is_first_running | is_p1_last_burst) & io.pixel_planar1_lp_vld, io.pixel_planar1_lp_burst,
//                                  Mux((is_first_running | is_p1_last_burst) & ~io.pixel_planar1_lp_vld, io.pixel_planar1_width_burst,
//                                  Mux((req_img_p1_sec_cnt === 0.U) & is_p1_cur_sec_end, io.pixel_planar1_width_burst,
//                                  Mux((req_img_p1_sec_cnt === 1.U) & is_p1_cur_sec_end, io.pixel_planar1_rp_burst,
//                                  req_img_p1_burst_cnt_dec(13, 0)))))
//     val req_img_p1_sec_cnt_w = Mux((is_first_running | is_p1_last_burst)& io.pixel_planar1_lp_vld, 0.U,
//                                Mux((is_first_running | is_p1_last_burst)& ~io.pixel_planar1_lp_vld, 1.U,
//                                req_img_p1_sec_cnt + 1.U))
//     val req_img_p1_burst_size = req_img_p1_cur_burst
//     is_p1_cur_sec_end := req_img_p1_burst_cnt_dec(14) | (req_img_p1_burst_cnt === req_img_p1_bundle_cnt)
//     val is_p1_1st_burst







// }}

// object NV_NVDLA_CDMA_IMG_sgDriver extends App {
//   implicit val conf: cdmaConfiguration = new cdmaConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_IMG_sg())
// }

