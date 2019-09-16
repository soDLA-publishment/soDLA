package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_CDMA_IMG_sg(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())
    
        //pwr
        val pwrbus_ram_pd = Input(UInt(32.W))

        //mcif
        val img_dat2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2img_dat_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))

        val img_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2img_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))) else None

        //status
        val img2status_dat_entries = Input(UInt(15.W))
        val img2status_dat_updt = Input(Bool())

        //status2dma
        val status2dma_free_entries = Input(UInt(15.W))
        val status2dma_fsm_switch = Input(Bool())

        //state
        val is_running = Input(Bool())
        val layer_st = Input(Bool())

        //pixel
        val pixel_order = Input(UInt(11.W))
        val pixel_planar = Input(Bool())
        val pixel_planar0_bundle_limit = Input(UInt(4.W))
        val pixel_planar0_bundle_limit_1st = Input(UInt(4.W))
        val pixel_planar0_byte_sft = Input(UInt(conf.ATMMBW.W))
        val pixel_planar1_byte_sft = Input(UInt(conf.ATMMBW.W))
        val pixel_planar0_lp_burst = Input(UInt(4.W))
        val pixel_planar0_lp_vld = Input(Bool())
        val pixel_planar0_rp_burst = Input(UInt(4.W))
        val pixel_planar0_rp_vld = Input(Bool())
        val pixel_planar0_width_burst = Input(UInt(14.W)) 
        val pixel_planar1_bundle_limit = Input(UInt(5.W))
        val pixel_planar1_bundle_limit_1st = Input(UInt(5.W)) 
        val pixel_planar1_lp_burst = Input(UInt(3.W))
        val pixel_planar1_lp_vld = Input(Bool())
        val pixel_planar1_rp_burst = Input(UInt(3.W))
        val pixel_planar1_rp_vld = Input(Bool())
        val pixel_planar1_width_burst = Input(UInt(14.W))

        //sg2pack
        val sg2pack_img_pd = DecoupledIO(UInt(11.W))
        val sg2pack_data_entries = Output(UInt(15.W))
        val sg2pack_entry_end = Output(UInt(15.W))
        val sg2pack_entry_mid = Output(UInt(15.W))
        val sg2pack_entry_st = Output(UInt(15.W))
        val sg2pack_height_total = Output(UInt(13.W))
        val sg2pack_mn_enable = Output(Bool())
        val sg2pack_sub_h_end = Output(UInt(4.W))
        val sg2pack_sub_h_mid = Output(UInt(4.W))
        val sg2pack_sub_h_st = Output(UInt(4.W))
        val sg_is_done = Output(Bool())

        //img2sbuf
        val img2sbuf_p0_wr = new nvdla_wr_if(17, conf.DMAIF)

        //reg2dp
        val reg2dp_op_en = Input(Bool())
        val reg2dp_pixel_y_offset = Input(UInt(3.W))
        val reg2dp_datain_height = Input(UInt(13.W))
        val reg2dp_datain_ram_type = Input(Bool())
        val reg2dp_datain_addr_high_0 = Input(UInt(32.W))
        val reg2dp_datain_addr_low_0 = Input(UInt(32.W))
        val reg2dp_datain_addr_high_1 = Input(UInt(32.W))
        val reg2dp_datain_addr_low_1 = Input(UInt(32.W))
        val reg2dp_line_stride = Input(UInt(32.W))
        val reg2dp_uv_line_stride = Input(UInt(32.W))
        val reg2dp_mean_format = Input(Bool())
        val reg2dp_entries = Input(UInt(14.W))
        val reg2dp_dma_en = Input(Bool())

        val dp2reg_img_rd_stall = Output(UInt(32.W))
        val dp2reg_img_rd_latency = Output(UInt(32.W))

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
// general signal                                                     //
////////////////////////////////////////////////////////////////////////
    val is_running_d1 = RegInit(false.B)
    val mn_enable_d1 = RegInit(false.B)
    val data_height = RegInit("b0".asUInt(14.W))
    val height_cnt_total = RegInit("b0".asUInt(13.W))
    val data_entries = RegInit("b0".asUInt(15.W))

    val planar1_enable = io.pixel_planar
    val is_first_running = io.is_running & ~is_running_d1

    is_running_d1 := io.is_running
    when(io.layer_st){
        mn_enable_d1 := (io.reg2dp_mean_format === 1.U)
        data_height := io.reg2dp_datain_height +& 1.U
        height_cnt_total := io.reg2dp_datain_height
        data_entries := io.reg2dp_entries +& 1.U
    }
////////////////////////////////////////////////////////////////////////
//  generator preparing parameters                                    //
////////////////////////////////////////////////////////////////////////
///////////// sub_h for total control /////////////
    val pre_sub_h_st_d1 = RegInit("b0".asUInt(4.W))
    val pre_sub_h_mid_d1 = RegInit("b0".asUInt(4.W))
    val pre_sub_h_end_d1 = RegInit("b0".asUInt(4.W))
    val pre_entry_st_d1 = RegInit("b0".asUInt(15.W))
    val pre_entry_mid_d1 = RegInit("b0".asUInt(15.W))
    val pre_entry_end_d1 = RegInit("b0".asUInt(15.W))

    val sub_h_st_limit = "b1".asUInt(4.W)
    val sub_h_mid_w = "h1".asUInt(4.W)
    val sub_h_end_limit = "h1".asUInt(4.W)

    val sub_h_st_sel = (~(data_height(13, 4).orR)) && (data_height(3, 0) <= sub_h_st_limit)
    val sub_h_end_sel = (~(data_height(13, 4).orR)) && (data_height(3, 0) <= sub_h_end_limit)

    val sub_h_st_w = Mux(sub_h_st_sel, data_height(3, 0), sub_h_st_limit)
    val sub_h_end_w = Mux(sub_h_end_sel, data_height(3, 0), sub_h_end_limit)
    val pre_entry_st_w = (sub_h_st_w * data_entries)(14, 0)
    val pre_entry_end_w = (sub_h_end_w * data_entries)(14, 0)

    when(is_first_running){
        pre_sub_h_st_d1 := sub_h_st_w
        pre_sub_h_mid_d1 := sub_h_mid_w
        pre_sub_h_end_d1 := sub_h_end_w
        pre_entry_st_d1 := pre_entry_st_w
        pre_entry_mid_d1 := data_entries
        pre_entry_end_d1 := pre_entry_end_w
    }

////////////////////////////////////////////////////////////////////////
//  request generator for input image                                 //
////////////////////////////////////////////////////////////////////////
    val src_dummy = "h0".asUInt(2.W)
    val src_p0 = "h1".asUInt(2.W)
    val src_p1 = "h2".asUInt(2.W)

    ///////////// height counter /////////////
    val req_height_cnt = RegInit("b0".asUInt(13.W))
    val req_height_en = Wire(Bool())
    val is_1st_height = ~(req_height_cnt.orR)
    val is_last_height = (req_height_cnt === height_cnt_total)
    
    when(req_height_en){
        req_height_cnt := Mux(is_first_running, "b0".asUInt(13.W), req_height_cnt + 1.U)
    }
    ///////////// image planar count /////////////
    val req_img_planar_cnt = RegInit(false.B)
    val req_img_planar_en = Wire(Bool())
    val is_img_last_planar = (req_img_planar_cnt === io.pixel_planar)
    val req_img_planar_cnt_w = Mux((is_first_running | is_img_last_planar), false.B,  ~req_img_planar_cnt)
    
    when(req_img_planar_en){
        req_img_planar_cnt := req_img_planar_cnt_w
    }
    ///////////// image planar 0 bundle and burst count /////////////
    val req_img_p0_bundle_cnt = RegInit("b0".asUInt(4.W))
    val req_img_p0_burst_en = Wire(Bool())
    val is_p0_bundle_end = Wire(Bool())
    val is_p0_last_burst = Wire(Bool())
    val req_img_p0_cur_burst = Wire(UInt(4.W))
    
    when(req_img_p0_burst_en){
        req_img_p0_bundle_cnt := Mux(is_first_running | is_p0_last_burst, io.pixel_planar0_bundle_limit_1st,
                                 Mux(is_p0_bundle_end, io.pixel_planar0_bundle_limit,
                                 req_img_p0_bundle_cnt - req_img_p0_cur_burst))
    }


    val req_img_p0_burst_cnt = RegInit("b0".asUInt(14.W))
    val req_img_p0_sec_cnt = RegInit("b0".asUInt(2.W))
    val is_p0_cur_sec_end = Wire(Bool())
    val req_img_p0_sec_en = Wire(Bool())

    val req_img_p0_burst_cnt_dec = req_img_p0_burst_cnt -& req_img_p0_bundle_cnt
    req_img_p0_cur_burst := Mux(req_img_p0_burst_cnt_dec(14), req_img_p0_burst_cnt(3, 0), req_img_p0_bundle_cnt)
    is_p0_cur_sec_end := (req_img_p0_burst_cnt <= req_img_p0_bundle_cnt)
    val is_p0_1st_burst = ((req_img_p0_burst_cnt === io.pixel_planar0_lp_burst)&(req_img_p0_sec_cnt === 0.U))|
                          ((req_img_p0_burst_cnt === io.pixel_planar0_width_burst)&(req_img_p0_sec_cnt === 1.U)& ~io.pixel_planar0_lp_vld)
    is_p0_last_burst := (is_p0_cur_sec_end & (req_img_p0_sec_cnt === 1.U) & ~io.pixel_planar0_rp_vld)|
                           (is_p0_cur_sec_end & (req_img_p0_sec_cnt === 2.U))
    is_p0_bundle_end := (req_img_p0_cur_burst === req_img_p0_bundle_cnt) | is_p0_last_burst
    val req_img_p0_burst_size = req_img_p0_cur_burst
    val is_p0_req_real = (req_img_p0_sec_cnt === 1.U)

    when(req_img_p0_burst_en){
        req_img_p0_burst_cnt := Mux((is_first_running | is_p0_last_burst) & io.pixel_planar0_lp_vld, io.pixel_planar0_lp_burst, 
                                Mux((is_first_running | is_p0_last_burst) & ~io.pixel_planar0_lp_vld, io.pixel_planar0_width_burst,
                                Mux((req_img_p0_sec_cnt === 0.U)&is_p0_cur_sec_end, io.pixel_planar0_width_burst,
                                Mux((req_img_p0_sec_cnt === 1.U)&is_p0_cur_sec_end, io.pixel_planar0_rp_burst,
                                req_img_p0_burst_cnt_dec(13, 0)))))
    }
    when(req_img_p0_sec_en){
        req_img_p0_sec_cnt := Mux((is_first_running | is_p0_last_burst) & io.pixel_planar0_lp_vld, 0.U, 
                              Mux((is_first_running | is_p0_last_burst) & io.pixel_planar0_lp_vld, 1.U, 
                              req_img_p0_sec_cnt + 1.U))
    }

    ///////////// image planar 1 bundle and burst count /////////////
    val req_img_p1_bundle_cnt = RegInit("b0".asUInt(5.W))
    val req_img_p1_burst_cnt = RegInit("b0".asUInt(14.W))
    val req_img_p1_sec_cnt = RegInit("b0".asUInt(2.W))
    val req_img_p1_cur_burst = Wire(UInt(5.W))
    val is_p1_cur_sec_end = Wire(Bool())
    val req_img_p1_burst_en = Wire(Bool())
    val req_img_p1_sec_en = Wire(Bool())

    val req_img_p1_burst_cnt_dec = req_img_p1_burst_cnt -& req_img_p1_bundle_cnt
    req_img_p1_cur_burst := Mux(req_img_p1_burst_cnt_dec(14), req_img_p1_burst_cnt(4, 0), req_img_p1_bundle_cnt)
    val req_img_p1_burst_size = req_img_p1_cur_burst
    is_p1_cur_sec_end := req_img_p1_burst_cnt_dec(14) | (req_img_p1_burst_cnt === req_img_p1_bundle_cnt)
    val is_p1_1st_burst = ((req_img_p1_burst_cnt === io.pixel_planar1_lp_burst) & (req_img_p1_sec_cnt === 0.U)) |
                          ((req_img_p1_burst_cnt === io.pixel_planar1_width_burst) & (req_img_p1_sec_cnt === 1.U) & ~io.pixel_planar1_lp_vld);
    val is_p1_last_burst = (is_p1_cur_sec_end & (req_img_p1_sec_cnt === 1.U) & ~io.pixel_planar1_rp_vld) |
                           (is_p1_cur_sec_end & (req_img_p1_sec_cnt === 2.U))
    val is_p1_bundle_end = (req_img_p1_cur_burst === req_img_p1_bundle_cnt) | is_p1_last_burst
    val is_p1_req_real = (req_img_p1_sec_cnt === 1.U)

    when(req_img_p1_burst_en){
        req_img_p1_bundle_cnt := Mux((is_first_running | is_p1_last_burst), io.pixel_planar1_bundle_limit_1st,
                                 Mux(is_p1_bundle_end, io.pixel_planar1_bundle_limit,
                                req_img_p1_bundle_cnt - req_img_p1_cur_burst))
        req_img_p1_burst_cnt := Mux((is_first_running | is_p1_last_burst) & io.pixel_planar1_lp_vld, io.pixel_planar1_lp_burst,
                                Mux((is_first_running | is_p1_last_burst) & ~io.pixel_planar1_lp_vld, io.pixel_planar1_width_burst,
                                Mux((req_img_p1_sec_cnt === 0.U) & is_p1_cur_sec_end, io.pixel_planar1_width_burst,
                                Mux((req_img_p1_sec_cnt === 1.U) & is_p1_cur_sec_end, io.pixel_planar1_rp_burst,
                                req_img_p1_burst_cnt_dec(13, 0)))))
    }
    when(req_img_p1_sec_en){
        req_img_p1_sec_cnt := Mux((is_first_running | is_p1_last_burst)& io.pixel_planar1_lp_vld, 0.U,
                              Mux((is_first_running | is_p1_last_burst)& ~io.pixel_planar1_lp_vld, 1.U,
                              req_img_p1_sec_cnt + 1.U))
    }

    ///////////// image burst signal /////////////
    val is_img_1st_burst = Mux(~req_img_planar_cnt, is_p0_1st_burst, is_p1_1st_burst)
    val is_img_last_burst = Mux(~req_img_planar_cnt, is_p0_last_burst, is_p1_last_burst)
    val is_img_bundle_end = Mux(~req_img_planar_cnt, is_p0_bundle_end, is_p1_bundle_end)
    val req_img_burst_size = Mux(~req_img_planar_cnt, req_img_p0_burst_size, req_img_p1_burst_size)
    val is_img_dummy = Mux(~req_img_planar_cnt, ~is_p0_req_real, ~is_p1_req_real)

    ///////////// control signal /////////////
    val req_reg_en =  Wire(Bool())
    val is_last_req = Wire(Bool())
    val req_valid = RegInit(false.B)
    val req_valid_d1 = RegInit(false.B)
    val req_ready_d1 = Wire(Bool())

    val req_valid_w = Mux(~io.is_running, false.B,
                      Mux(is_first_running, true.B,
                      Mux(req_reg_en & is_last_req, false.B, 
                      req_valid)))

    req_valid := req_valid_w

    val req_adv = req_valid & (~req_valid_d1 | req_ready_d1);
    is_last_req := (is_img_last_burst & is_img_last_planar & is_last_height);
    val req_img_reg_en = req_adv;
    req_reg_en := req_adv;
    req_img_p0_burst_en := is_first_running | (req_img_reg_en & ~req_img_planar_cnt);
    req_img_p0_sec_en := is_first_running | (req_img_reg_en & ~req_img_planar_cnt & is_p0_cur_sec_end);
    req_img_p1_burst_en := is_first_running | (req_img_reg_en & req_img_planar_cnt);
    req_img_p1_sec_en := is_first_running | (req_img_reg_en & req_img_planar_cnt & is_p1_cur_sec_end);
    val req_img_p0_burst_offset_en = is_first_running | (req_img_reg_en & ~req_img_planar_cnt & (is_p0_req_real | is_p0_last_burst));
    val req_img_p1_burst_offset_en = is_first_running | (req_img_reg_en & req_img_planar_cnt & (is_p1_req_real | is_p1_last_burst));
    req_img_planar_en := is_first_running | (req_img_reg_en & is_img_bundle_end);
    req_height_en := is_first_running | (req_img_reg_en & is_img_last_burst & is_img_last_planar);

    ///////////// address line offset for image /////////////
    val req_img_p0_line_offset = RegInit("b0".asUInt(32.W))
    val req_img_p1_line_offset = RegInit("b0".asUInt(32.W))

    when(req_height_en){
        req_img_p0_line_offset := Mux(is_first_running, "b0".asUInt(32.W), req_img_p0_line_offset + io.reg2dp_line_stride) 
    }
    when(req_height_en & planar1_enable){
        req_img_p1_line_offset := Mux(is_first_running, "b0".asUInt(32.W), req_img_p0_line_offset + io.reg2dp_uv_line_stride)
    }
    ///////////// address burst offset for image /////////////
    val req_img_p0_burst_offset = RegInit("b0".asUInt((32-conf.ATMMBW).W))
    val req_img_p1_burst_offset = RegInit("b0".asUInt((32-conf.ATMMBW).W))

    when(req_img_p0_burst_offset_en){
        req_img_p0_burst_offset := Mux(is_first_running | is_p0_last_burst, "b0".asUInt((32-conf.ATMMBW).W), req_img_p0_burst_offset + req_img_p0_cur_burst)
    }
    when(req_img_p1_burst_offset_en){
        req_img_p1_burst_offset := Mux(is_first_running | is_p1_last_burst, "b0".asUInt((32-conf.ATMMBW).W), req_img_p1_burst_offset + req_img_p1_cur_burst)
    }

    ///////////// address base for image /////////////
    val req_img_p0_addr_base = Reg(UInt(64.W))
    val req_img_p1_addr_base = Reg(UInt(64.W))

    val req_img_p0_addr = req_img_p0_addr_base + req_img_p0_line_offset + Cat(req_img_p0_burst_offset, Fill(conf.ATMM_NUM, false.B))
    val req_img_p1_addr = req_img_p1_addr_base + req_img_p1_line_offset + Cat(req_img_p1_burst_offset, Fill(conf.ATMM_NUM, false.B))

    when(io.layer_st){
        req_img_p0_addr_base := Cat(io.reg2dp_datain_addr_high_0, io.reg2dp_datain_addr_low_0)
        req_img_p1_addr_base := Cat(io.reg2dp_datain_addr_high_1, io.reg2dp_datain_addr_low_1)
    }

    ///////////// request package /////////////
    val req_addr_d1 = RegInit("b0".asUInt(64.W))
    val req_size_d1 = RegInit("b0".asUInt(5.W))
    val req_size_out_d1 = RegInit("b0".asUInt(5.W))
    val req_line_st_d1 = RegInit(false.B)
    val req_bundle_end_d1 = RegInit(false.B)
    val req_line_end_d1 = RegInit(false.B)
    val req_grant_end_d1 = RegInit(false.B)
    val req_end_d1 = RegInit(false.B)
    val req_planar_d1 = RegInit(false.B)
    val req_is_dummy_d1 = RegInit(false.B)

    val req_valid_d1_w = Mux(~io.is_running, false.B,
                         Mux(req_valid, true.B,
                         Mux(req_ready_d1, false.B, req_valid_d1)))
    val req_addr = Mux(req_img_planar_cnt, req_img_p1_addr,  req_img_p0_addr)
    val req_size = req_img_burst_size
    val req_size_out = req_size - 1.U
    val req_line_st = is_img_1st_burst
    val req_bundle_end = (is_img_bundle_end & is_img_last_planar)
    val req_line_end = (is_img_last_burst & is_img_last_planar)
    val req_grant_end = (is_img_last_burst & is_img_last_planar)
    val req_is_dummy = is_img_dummy

    req_valid_d1 := req_valid_d1_w
    when(req_reg_en){
        req_addr_d1 := req_addr
        req_size_d1 := req_size
        req_size_out_d1 := req_size_out
        req_line_st_d1 := req_line_st
        req_bundle_end_d1 := req_bundle_end
        req_line_end_d1 := req_line_end
        req_grant_end_d1 := req_grant_end
        req_end_d1 := is_last_req
        req_planar_d1 := req_img_planar_cnt
        req_is_dummy_d1 := req_is_dummy
    }

    ////////////////////////////////////////////////////////////////////////
    //  request arbiter and cbuf entry monitor                            //
    ////////////////////////////////////////////////////////////////////////
    val dma_rd_req_rdy = Wire(Bool())
    val dma_req_fifo_ready = Wire(Bool())
    val is_cbuf_ready = RegInit(true.B)
    val req_is_done = RegInit(true.B)

    req_ready_d1 := ((dma_rd_req_rdy | req_is_dummy_d1) & dma_req_fifo_ready & is_cbuf_ready)
    val req_is_done_w = Mux(is_first_running, false.B, 
                        Mux((req_valid_d1 & req_ready_d1 & req_end_d1), true.B,
                        req_is_done))
    
    req_is_done := req_is_done_w

    ///////////// cbuf monitor /////////////
    val img_entry_onfly = RegInit(false.B)

    val cur_required_entry = Mux((is_cbuf_ready | req_is_done), "b0".asUInt(15.W),
                             Mux(is_last_height, pre_entry_end_d1, 
                             Mux(is_1st_height, pre_entry_st_d1,
                             pre_entry_mid_d1)))
    val total_required_entry = cur_required_entry + img_entry_onfly
    val is_cbuf_enough = io.status2dma_free_entries >= total_required_entry
    val is_cbuf_ready_w = Mux(~io.is_running | is_first_running, false.B,
                          Mux(req_valid_d1 & req_ready_d1 & req_grant_end_d1, false.B,
                          Mux(~is_cbuf_ready, is_cbuf_enough,
                          is_cbuf_ready)))
    val img_entry_onfly_sub = Mux(io.img2status_dat_updt, io.img2status_dat_entries, "b0".asUInt(15.W))
    val img_entry_onfly_add = Mux(~req_is_done & is_cbuf_enough & ~is_cbuf_ready, cur_required_entry, "b0".asUInt(15.W))
    val img_entry_onfly_w = img_entry_onfly + img_entry_onfly_add - img_entry_onfly_sub
    val img_entry_onfly_en = (~req_is_done & is_cbuf_enough & ~is_cbuf_ready) | io.img2status_dat_updt

    is_cbuf_ready := is_cbuf_ready_w
    when(img_entry_onfly_en){
        img_entry_onfly := img_entry_onfly_w
    }
    ////////////////////////////////////////////////////////////////////////
    //  CDMA IMG read request interface                                   //
    ////////////////////////////////////////////////////////////////////////
    //==============
    // DMA Interface
    //==============
    // rd Channel: Request
    val dma_rd_req_pd = Wire(UInt((conf.NVDLA_CDMA_MEM_RD_REQ).W))
    val dma_rd_req_vld = Wire(Bool())
    val dma_rd_rsp_pd = Wire(UInt((conf.NVDLA_CDMA_MEM_RD_RSP).W))
    val dma_rd_rsp_vld = Wire(Bool())
    val dma_rd_rsp_rdy = Wire(Bool())

    val nv_NVDLA_PDP_RDMA_rdreq = Module{new NV_NVDLA_DMAIF_rdreq(conf.NVDLA_CDMA_MEM_RD_REQ)}
    nv_NVDLA_PDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
    nv_NVDLA_PDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.reg2dp_datain_ram_type
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.img_dat2cvif_rd_req_pd.get <> nv_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_pd.get
    }
    io.img_dat2mcif_rd_req_pd <> nv_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_pd

    nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.bits := dma_rd_req_pd
    nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.valid := dma_rd_req_vld
    dma_rd_req_rdy := nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.ready
    // rd Channel: Response
    val nv_NVDLA_PDP_RDMA_rdrsp = Module{new NV_NVDLA_DMAIF_rdrsp(conf.NVDLA_CDMA_MEM_RD_RSP)}
    nv_NVDLA_PDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get <> io.cvif2img_dat_rd_rsp_pd.get
    }
    nv_NVDLA_PDP_RDMA_rdrsp.io.mcif_rd_rsp_pd <> io.mcif2img_dat_rd_rsp_pd

    dma_rd_rsp_pd := nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.bits
    dma_rd_rsp_vld := nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.valid
    nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.ready := dma_rd_rsp_rdy

    ///////////////////////////////////////////

    // PKT_PACK_WIRE( dma_read_cmd ,  dma_rd_req_ ,  dma_rd_req_pd )
    val dma_rd_req_size = Wire(UInt(15.W))
    val dma_rd_req_addr = Wire(UInt(64.W))
    val dma_blocking = Wire(Bool())
    val dma_rsp_blocking = Wire(Bool())
    dma_rd_req_pd := Cat(dma_rd_req_size, dma_rd_req_addr(conf.NVDLA_MEM_ADDRESS_WIDTH-1,0))
    dma_rd_req_vld := req_valid_d1 & dma_req_fifo_ready & is_cbuf_ready & ~req_is_dummy_d1
    dma_rd_req_addr := req_addr_d1
    dma_rd_req_size := req_size_out_d1
    val dma_rd_req_type = io.reg2dp_datain_ram_type
    dma_rd_rsp_rdy := ~dma_blocking
    dma_blocking := dma_rsp_blocking

    val dma_req_fifo_req = Wire(Bool())
    val dma_req_fifo_data = Wire(UInt(11.W))
    val dma_rsp_fifo_ready = Wire(Bool())
    val u_NV_NVDLA_CDMA_IMG_fifo = Module{new NV_NVDLA_fifo(depth = 128, width = 11,
                                    ram_type = 2, 
                                    distant_wr_req = true)}
    u_NV_NVDLA_CDMA_IMG_fifo.io.clk := io.nvdla_core_clk
    u_NV_NVDLA_CDMA_IMG_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_NV_NVDLA_CDMA_IMG_fifo.io.wr_pvld := dma_req_fifo_req
    dma_req_fifo_ready := u_NV_NVDLA_CDMA_IMG_fifo.io.wr_prdy
    u_NV_NVDLA_CDMA_IMG_fifo.io.wr_pd := dma_req_fifo_data
    val dma_rsp_fifo_req = u_NV_NVDLA_CDMA_IMG_fifo.io.rd_pvld
    u_NV_NVDLA_CDMA_IMG_fifo.io.rd_prdy := dma_rsp_fifo_ready
    val dma_rsp_fifo_data = u_NV_NVDLA_CDMA_IMG_fifo.io.rd_pd
    

    dma_req_fifo_req := req_valid_d1 & is_cbuf_ready & (dma_rd_req_rdy | req_is_dummy_d1) 
    dma_req_fifo_data := Cat(req_planar_d1, req_end_d1, req_line_end_d1,
                            req_bundle_end_d1, req_line_st_d1, req_is_dummy_d1,
                            req_size_d1)

    ////////////////////////////////////////////////////////////////////////
    //  CDMA IMG read response logic                                      //
    ////////////////////////////////////////////////////////////////////////
    val dma_rd_rsp_data = dma_rd_rsp_pd(conf.NVDLA_MEMIF_WIDTH-1, 0)
    val dma_rd_rsp_mask = dma_rd_rsp_pd(conf.NVDLA_CDMA_MEM_RD_RSP-1, conf.NVDLA_MEMIF_WIDTH)
    val dma_rsp_size_cnt = RegInit("b0".asUInt(5.W))
    val dma_rsp_vld = Wire(Bool())
    
    val dma_rsp_planar = dma_rsp_fifo_data(10)
    val dma_rsp_end = dma_rsp_fifo_data(9)
    val dma_rsp_line_end = dma_rsp_fifo_data(8)
    val dma_rsp_bundle_end = dma_rsp_fifo_data(7)
    val dma_rsp_line_st = dma_rsp_fifo_data(6)
    val dma_rsp_dummy = dma_rsp_fifo_data(5)
    val dma_rsp_size = dma_rsp_fifo_data(4, 0)

    dma_rsp_blocking := (dma_rsp_fifo_req & dma_rsp_dummy)

    val dma_rsp_mask = Mux(~dma_rsp_fifo_req, false.B, 
                      Mux(~dma_rsp_dummy, dma_rd_rsp_vld & dma_rd_rsp_mask(0), 
                     true.B))

    val dma_rsp_size_cnt_inc = dma_rsp_size_cnt + dma_rsp_mask
    val dma_rsp_size_cnt_w = Mux(dma_rsp_size_cnt_inc === dma_rsp_size, "b0".asUInt(5.W), dma_rsp_size_cnt_inc)

    when(dma_rsp_vld){
        dma_rsp_size_cnt := dma_rsp_size_cnt_w
    }
    dma_rsp_vld := dma_rsp_fifo_req & (dma_rsp_dummy | dma_rd_rsp_vld)
    dma_rsp_fifo_ready := (dma_rsp_vld & (dma_rsp_size_cnt_inc === dma_rsp_size))

    ////////////////////////////////////////////////////////////////////////
    //  CDMA pixel data response logic stage 1                            //
    ////////////////////////////////////////////////////////////////////////
    val rsp_img_vld = RegInit(false.B)
    val rsp_img_vld_w = dma_rsp_vld
    val rsp_img_p0_data_w = Wire(UInt((conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE).W))
    val rsp_img_p0_vld_w = dma_rsp_mask(0)
    val rsp_img_p0_vld = RegInit(false.B)
    val rsp_img_p0_data = Reg(UInt((conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE).W))

    rsp_img_p0_vld := rsp_img_p0_vld_w
    when(rsp_img_p0_vld_w){
        rsp_img_p0_data := rsp_img_p0_data_w
    }

    val rsp_dat = dma_rd_rsp_data

    //10'h1     ABGR, VU, single, unchange
    //10'h2     ARGB, AYUV, 8bpp
    //10'h4     ARGB, AYUV, 16bpp
    //10'h8     BGRA, VUYA, 8bpp
    //10'h10    BGRA, VUYA, 16bpp
    //10'h20    RGBA, YUVA, 8bpp
    //10'h40    ARGB, AYUV, packed_10b
    //10'h80    BGRA, YUVA, packed_10b
    //10'h100   RGBA, packed_10b
    //10'h200   UV, 8bpp
    //10'h400   UV, 16bpp

    val rsp_img_sel = Cat(io.pixel_order(10)&dma_rsp_planar, io.pixel_order(9)&dma_rsp_planar, io.pixel_order(8, 1), 
                        io.pixel_order(0)|(~dma_rsp_planar & io.pixel_planar))

    //////// reordering ////////
    val rsp_img_data_sw_o0 = rsp_dat
    val rsp_img_data_sw_o1 = VecInit((0 to conf.NVDLA_CDMA_DMAIF_BW/32-1) 
    map {i => Cat(rsp_dat(i*32+31, i*32+24), rsp_dat(i*32+7, i*32), rsp_dat(i*32+15, i*32+8), rsp_dat(i*32+23, i*32+16))}).asUInt
    val rsp_img_data_sw_o3 = VecInit((0 to conf.NVDLA_CDMA_DMAIF_BW/32-1) 
    map {i => Cat(rsp_dat(i*32+7, i*32), rsp_dat(i*32+31, i*32+24), rsp_dat(i*32+23, i*32+16), rsp_dat(i*32+15, i*32+8))}).asUInt
    val rsp_img_data_sw_o5 = VecInit((0 to conf.NVDLA_CDMA_DMAIF_BW/32-1) 
    map {i => Cat(rsp_dat(i*32+7, i*32), rsp_dat(i*32+15, i*32+8), rsp_dat(i*32+23, i*32+16), rsp_dat(i*32+31, i*32+24))}).asUInt
    val rsp_img_data_sw_o9 = VecInit((0 to conf.NVDLA_CDMA_DMAIF_BW/16-1) 
    map {i => Cat(rsp_dat(i*16+7, i*16), rsp_dat(i*16+15, i*16+8))}).asUInt

    rsp_img_p0_data_w := (Fill(conf.NVDLA_MEMIF_WIDTH, rsp_img_sel(0)) & rsp_img_data_sw_o0) |
                        (Fill(conf.NVDLA_MEMIF_WIDTH, rsp_img_sel(1)) & rsp_img_data_sw_o1) |
                        (Fill(conf.NVDLA_MEMIF_WIDTH, rsp_img_sel(3)) & rsp_img_data_sw_o3) |
                        (Fill(conf.NVDLA_MEMIF_WIDTH, rsp_img_sel(5)) & rsp_img_data_sw_o5) |
                        (Fill(conf.NVDLA_MEMIF_WIDTH, rsp_img_sel(9)) & rsp_img_data_sw_o9) 

    val dma_rsp_w_burst_size = dma_rsp_size
    val rsp_img_1st_burst_w = dma_rsp_line_st & (dma_rsp_size_cnt === 0.U)

    val rsp_img_planar = RegInit(false.B)
    val rsp_img_1st_burst = RegInit(false.B)
    val rsp_img_line_st = RegInit(false.B)
    val rsp_img_req_end = RegInit(false.B)
    when(rsp_img_vld_w){
        rsp_img_planar := dma_rsp_planar
        rsp_img_1st_burst := rsp_img_1st_burst_w
        rsp_img_line_st := dma_rsp_line_st
        rsp_img_line_st := dma_rsp_fifo_ready
    }

    val rsp_img_w_burst_size = RegInit("b0".asUInt(5.W))
    val rsp_img_line_end = RegInit(false.B)
    val rsp_img_bundle_end = RegInit(false.B)
    val rsp_img_end = RegInit(false.B)
    when(rsp_img_vld_w & dma_rsp_fifo_ready){
        rsp_img_w_burst_size := dma_rsp_w_burst_size
    }
    when(rsp_img_vld_w){
        rsp_img_line_end := dma_rsp_line_end & dma_rsp_fifo_ready
        rsp_img_bundle_end := dma_rsp_bundle_end & dma_rsp_fifo_ready
        rsp_img_end := dma_rsp_end & dma_rsp_fifo_ready
    }

    ////////////////////////////////////////////////////////////////////////
    //  CDMA pixel data response logic stage 2: cache and sbuf write      //
    ////////////////////////////////////////////////////////////////////////
    //////// cache line control ////////
    val rsp_img_c0l0_wr_en = (rsp_img_p0_vld & (~rsp_img_planar))
    val rsp_img_c1l0_wr_en = (rsp_img_p0_vld &   rsp_img_planar)

    // need cache more when more dmaif/atmm
    val rsp_img_p0_cache_data = RegInit("b0".asUInt(conf.ATMM.W))
    val rsp_img_p1_cache_data = RegInit("b0".asUInt(conf.ATMM.W))
    
    when(rsp_img_c0l0_wr_en){
        rsp_img_p0_cache_data := rsp_img_p0_data
    }
    when(rsp_img_c1l0_wr_en){
        rsp_img_p1_cache_data := rsp_img_p0_data
    }

    //per atmm
    val rsp_img_p0_vld_d1 = RegInit(false.B)
    val rsp_img_p0_data_d1 = Reg(UInt((conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE).W))

    val rsp_img_sft = Wire(UInt(conf.ATMMBW.W))
    val rsp_img_p0_vld_d1_w = rsp_img_p0_vld & (~rsp_img_1st_burst)
    val rsp_img_p0_data_atmm0 = rsp_img_p0_data
    val rsp_img_p0_data_d1_w = Cat(rsp_img_p0_data_atmm0, Mux(rsp_img_c0l0_wr_en, rsp_img_p0_cache_data, rsp_img_p1_cache_data)>>Cat(rsp_img_sft, "b0".asUInt(3.W)))(conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE-1, 0)
    val rsp_img_planar_idx_add = "h1".asUInt(3.W)

    rsp_img_p0_vld_d1 := rsp_img_p0_vld_d1_w 
    when(rsp_img_p0_vld_d1_w){
        rsp_img_p0_data_d1 := rsp_img_p0_data_d1_w 
    } 
    rsp_img_sft := Mux(rsp_img_planar,  io.pixel_planar1_byte_sft,  io.pixel_planar0_byte_sft)

    //////// data write control logic: normal write back ////////
    val rsp_img_p0_planar0_idx = RegInit("b0".asUInt(7.W))
    val rsp_img_p0_planar1_idx = RegInit("b0".asUInt(7.W))
    val rsp_img_p0_addr_d1 = RegInit("b0".asUInt(8.W))

    val rsp_img_p0_planar0_idx_inc = rsp_img_p0_planar0_idx + rsp_img_planar_idx_add
    val rsp_img_p0_planar1_idx_inc = rsp_img_p0_planar1_idx + rsp_img_planar_idx_add
    val rsp_img_p0_planar0_idx_w = Mux(is_first_running, "b0".asUInt(7.W), rsp_img_p0_planar0_idx_inc)
    val rsp_img_p0_planar1_idx_w = Mux(is_first_running, "b0".asUInt(7.W), rsp_img_p0_planar1_idx_inc)
    val rsp_img_p0_planar0_en = is_first_running | (rsp_img_p0_vld_d1_w & ~rsp_img_planar)
    val rsp_img_p0_planar1_en = is_first_running | (rsp_img_p0_vld_d1_w & rsp_img_planar)
    val rsp_img_p0_addr = Mux(~rsp_img_planar, Cat(false.B, rsp_img_p0_planar0_idx(0), rsp_img_p0_planar0_idx(6, 1)), 
                         Cat(true.B, rsp_img_p0_planar1_idx(0), rsp_img_p0_planar1_idx(6, 1)))

    when(rsp_img_p0_planar0_en){
        rsp_img_p0_planar0_idx := rsp_img_p0_planar0_idx_w
    }
    when(rsp_img_p0_planar1_en){
        rsp_img_p0_planar1_idx := rsp_img_p0_planar1_idx_w
    }
    when(rsp_img_p0_vld_d1_w){
        rsp_img_p0_addr_d1 := rsp_img_p0_addr
    }

    //////// data write control logic: MISC output ////////
    val rsp_img_p0_burst_cnt = RegInit("b0".asUInt(4.W))
    val rsp_img_p1_burst_cnt = RegInit("b0".asUInt(5.W))
    val rsp_img_p0_burst_size_d1 = RegInit("b0".asUInt(4.W))
    val rsp_img_p1_burst_size_d1 = RegInit("b0".asUInt(5.W))
    val rsp_img_bundle_done_d1 = RegInit(false.B)
    val rsp_img_line_end_d1 = RegInit(false.B)
    val rsp_img_layer_end_d1 = RegInit(false.B)

    val rsp_img_p0_burst_cnt_inc = rsp_img_p0_burst_cnt + rsp_img_w_burst_size(3, 0) - rsp_img_line_st
    val rsp_img_p1_burst_cnt_inc = rsp_img_p1_burst_cnt + rsp_img_w_burst_size - rsp_img_line_st
    val rsp_img_p0_burst_cnt_w = Mux(is_first_running, "b0".asUInt(4.W), 
                                 Mux(rsp_img_vld & rsp_img_bundle_end, "b0".asUInt(4.W),
                                 Mux(~rsp_img_planar, rsp_img_p0_burst_cnt_inc, 
                                 rsp_img_p0_burst_cnt)))
    val rsp_img_p1_burst_cnt_w = Mux(is_first_running, "b0".asUInt(5.W),
                                 Mux(rsp_img_vld & rsp_img_bundle_end, "b0".asUInt(5.W),
                                 Mux(rsp_img_planar, rsp_img_p1_burst_cnt_inc, 
                                 rsp_img_p1_burst_cnt)))
    val rsp_img_bundle_done = (rsp_img_vld & rsp_img_bundle_end)
    val rsp_img_p0_burst_size_w = Mux(~rsp_img_planar, rsp_img_p0_burst_cnt_inc, rsp_img_p0_burst_cnt)
    val rsp_img_p1_burst_size_w = rsp_img_p1_burst_cnt_inc
    val rsp_img_p0_burst_cnt_en = is_first_running | (rsp_img_vld & rsp_img_req_end)
    val rsp_img_p1_burst_cnt_en = is_first_running | (rsp_img_vld & rsp_img_req_end & io.pixel_planar)
    val rsp_img_p0_burst_size_en = is_first_running | (rsp_img_vld & rsp_img_bundle_end)
    val rsp_img_p1_burst_size_en = is_first_running | (rsp_img_vld & rsp_img_bundle_end & io.pixel_planar)

    when(rsp_img_p0_burst_cnt_en){
        rsp_img_p0_burst_cnt := rsp_img_p0_burst_cnt_w
    }
    when(rsp_img_p1_burst_cnt_en){
        rsp_img_p1_burst_cnt := rsp_img_p1_burst_cnt_w
    }
    when(rsp_img_p0_burst_size_en){
        rsp_img_p0_burst_size_d1 := rsp_img_p0_burst_size_w
    }   
    when(rsp_img_p1_burst_size_en){
        rsp_img_p1_burst_size_d1 := rsp_img_p1_burst_size_w
    }
    rsp_img_bundle_done_d1 := rsp_img_bundle_done
    when(rsp_img_bundle_done){
        rsp_img_line_end_d1 := rsp_img_line_end
        rsp_img_layer_end_d1 := rsp_img_end
    }
    //////// data write control logic: status ////////
    val rsp_img_is_done = RegInit(true.B)

    val rsp_img_is_done_w = Mux(is_first_running, false.B, 
                            Mux(rsp_img_end & rsp_img_line_end, true.B,
                            rsp_img_is_done))
    when(io.is_running){
        rsp_img_is_done := rsp_img_is_done_w
    }
    ////////////////////////////////////////////////////////////////////////
    // Shared buffer write signals //
    ////////////////////////////////////////////////////////////////////////
    io.img2sbuf_p0_wr.addr.valid := rsp_img_p0_vld_d1;
    io.img2sbuf_p0_wr.addr.bits := rsp_img_p0_addr_d1;
    io.img2sbuf_p0_wr.data := rsp_img_p0_data_d1;
    
    ////////////////////////////////////////////////////////////////////////
    // Signal from SG to PACK //
    ////////////////////////////////////////////////////////////////////////
    val sg2pack_img_line_end = rsp_img_line_end_d1
    val sg2pack_img_layer_end = rsp_img_layer_end_d1
    val sg2pack_img_p0_burst = rsp_img_p0_burst_size_d1
    val sg2pack_img_p1_burst = rsp_img_p1_burst_size_d1

    // PKT_PACK_WIRE( sg2pack_info ,  sg2pack_img_  ,  sg2pack_push_data )
    val sg2pack_push_data = Cat(sg2pack_img_layer_end, sg2pack_img_line_end, sg2pack_img_p1_burst(4, 0), sg2pack_img_p0_burst(3, 0))

    val sg2pack_pop_req = Wire(Bool())
    val sg2pack_pop_data = Wire(UInt(11.W))

    val sg2pack_push_req = rsp_img_bundle_done_d1
    io.sg2pack_img_pd.valid := sg2pack_pop_req
    io.sg2pack_img_pd.bits := sg2pack_pop_data
    val sg2pack_pop_ready = io.sg2pack_img_pd.ready

    val u_NV_NVDLA_CDMA_IMG_sg2pack_fifo = Module{new NV_NVDLA_fifo(depth = 128, width = 11,
                                            ram_type = 1, 
                                            distant_wr_req = false)}
    u_NV_NVDLA_CDMA_IMG_sg2pack_fifo.io.clk := io.nvdla_core_clk
    u_NV_NVDLA_CDMA_IMG_sg2pack_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_NV_NVDLA_CDMA_IMG_sg2pack_fifo.io.wr_pvld := sg2pack_push_req
    val sg2pack_push_ready = u_NV_NVDLA_CDMA_IMG_sg2pack_fifo.io.wr_prdy
    u_NV_NVDLA_CDMA_IMG_sg2pack_fifo.io.wr_pd := sg2pack_push_data
    sg2pack_pop_req := u_NV_NVDLA_CDMA_IMG_sg2pack_fifo.io.rd_pvld
    u_NV_NVDLA_CDMA_IMG_sg2pack_fifo.io.rd_prdy := sg2pack_pop_ready
    sg2pack_pop_data := u_NV_NVDLA_CDMA_IMG_sg2pack_fifo.io.rd_pd
    

    io.sg2pack_height_total := height_cnt_total
    io.sg2pack_mn_enable := mn_enable_d1
    io.sg2pack_data_entries := data_entries
    io.sg2pack_entry_st := pre_entry_st_d1
    io.sg2pack_entry_mid := pre_entry_mid_d1
    io.sg2pack_entry_end := pre_entry_end_d1
    io.sg2pack_sub_h_st := pre_sub_h_st_d1
    io.sg2pack_sub_h_mid := pre_sub_h_mid_d1
    io.sg2pack_sub_h_end := pre_sub_h_end_d1

    ////////////////////////////////////////////////////////////////////////
    //  Global status                                                     //
    ////////////////////////////////////////////////////////////////////////
    val sg_is_done_out = RegInit(true.B)

    val sg_is_done_w = ~is_first_running & req_is_done & rsp_img_is_done

    when(io.is_running){
        sg_is_done_out := sg_is_done_w
    }
    io.sg_is_done := sg_is_done_out

    ////////////////////////////////////////////////////////////////////////
    // performance counting register //
    ////////////////////////////////////////////////////////////////////////
    //stall
    val img_rd_stall_inc = RegNext(dma_rd_req_vld & ~dma_rd_req_rdy & io.reg2dp_dma_en, false.B)
    val img_rd_stall_clr = RegNext(io.status2dma_fsm_switch & io.reg2dp_dma_en, false.B)
    val img_rd_stall_cen = RegNext(io.reg2dp_op_en & io.reg2dp_dma_en, false.B)

    val dp2reg_img_rd_stall_dec = false.B

    val stl = Module(new NV_COUNTER_STAGE(32))
    stl.io.clk := io.nvdla_core_clk
    stl.io.rd_stall_inc := img_rd_stall_inc
    stl.io.rd_stall_dec := dp2reg_img_rd_stall_dec
    stl.io.rd_stall_clr := img_rd_stall_clr
    stl.io.rd_stall_cen := img_rd_stall_cen
    io.dp2reg_img_rd_stall := stl.io.cnt_cur

    //latency_1
    val img_rd_latency_inc = RegNext(dma_rd_req_vld & ~dma_rd_req_rdy & io.reg2dp_dma_en, false.B)
    val img_rd_latency_dec = RegNext(dma_rsp_fifo_ready & ~dma_rsp_dummy & io.reg2dp_dma_en, false.B)
    val img_rd_latency_clr = RegNext(io.status2dma_fsm_switch, false.B)
    val img_rd_latency_cen = RegNext(io.reg2dp_op_en & io.reg2dp_dma_en, false.B)

    val outs_dp2reg_img_rd_latency = Wire(UInt(9.W))
    val ltc_1_inc = (outs_dp2reg_img_rd_latency =/= 511.U) & img_rd_latency_inc
    val ltc_1_dec = (outs_dp2reg_img_rd_latency =/= 511.U) & img_rd_latency_dec

    val ltc_1 = Module(new NV_COUNTER_STAGE(9))
    ltc_1.io.clk := io.nvdla_core_clk
    ltc_1.io.rd_stall_inc := ltc_1_inc
    ltc_1.io.rd_stall_dec := ltc_1_dec
    ltc_1.io.rd_stall_clr := img_rd_latency_clr
    ltc_1.io.rd_stall_cen := img_rd_latency_cen
    outs_dp2reg_img_rd_latency := ltc_1.io.cnt_cur

    //latency_2
    val ltc_2_inc = (~io.dp2reg_img_rd_latency.andR) & (outs_dp2reg_img_rd_latency.orR)
    val ltc_2_dec = false.B

    val ltc_2 = Module(new NV_COUNTER_STAGE(32))
    ltc_2.io.clk := io.nvdla_core_clk
    ltc_2.io.rd_stall_inc := ltc_2_inc
    ltc_2.io.rd_stall_dec := ltc_2_dec
    ltc_2.io.rd_stall_clr := img_rd_latency_clr
    ltc_2.io.rd_stall_cen := img_rd_latency_cen
    io.dp2reg_img_rd_latency := ltc_2.io.cnt_cur



}}

object NV_NVDLA_CDMA_IMG_sgDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_IMG_sg())
}

