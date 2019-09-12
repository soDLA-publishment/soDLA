package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_CDMA_IMG_ctrl(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_ng_clk = Input(Clock()) 
        //input state
        val pack_is_done = Input(Bool())
        val sc2cdma_dat_pending_req = Input(Bool())
        val sg_is_done = Input(Bool())
        val status2dma_fsm_switch = Input(Bool())
        //output state
        val img2status_state = Output(UInt(2.W))
        val is_running = Output(Bool())
        val layer_st = Output(Bool())
        //pixel
        val pixel_bank = Output(UInt(6.W))
        val pixel_data_expand = Output(Bool())
        val pixel_data_shrink = Output(Bool())
        val pixel_early_end = Output(Bool())
        val pixel_order = Output(UInt(11.W))
        val pixel_packed_10b = Output(Bool())
        val pixel_planar = Output(Bool())
        val pixel_precision = Output(UInt(2.W))
        val pixel_uint = Output(Bool())
        val slcg_img_gate_dc = Output(Bool())
        val slcg_img_gate_wg = Output(Bool())

        val pixel_planar0_bundle_limit = Output(UInt(4.W))
        val pixel_planar0_bundle_limit_1st = Output(UInt(4.W))
        val pixel_planar0_byte_sft = Output(UInt(conf.ATMMBW.W))
        val pixel_planar0_lp_burst = Output(UInt(4.W))
        val pixel_planar0_lp_vld = Output(Bool())
        val pixel_planar0_rp_burst = Output(UInt(4.W))
        val pixel_planar0_rp_vld = Output(Bool())
        val pixel_planar0_sft = Output(UInt(3.W))
        val pixel_planar0_width_burst = Output(UInt(14.W))

        val pixel_planar1_bundle_limit = Output(UInt(5.W))
        val pixel_planar1_bundle_limit_1st = Output(UInt(5.W))
        val pixel_planar1_byte_sft = Output(UInt(conf.ATMMBW.W)) 
        val pixel_planar1_lp_burst = Output(UInt(3.W))
        val pixel_planar1_lp_vld = Output(Bool())
        val pixel_planar1_rp_burst = Output(UInt(3.W))
        val pixel_planar1_rp_vld = Output(Bool())
        val pixel_planar1_sft = Output(UInt(3.W))
        val pixel_planar1_width_burst = Output(UInt(14.W))

        //reg2dp
        val reg2dp_op_en = Input(Bool())
        val reg2dp_conv_mode = Input(Bool())
        val reg2dp_in_precision = Input(UInt(2.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_datain_format = Input(Bool())
        val reg2dp_pixel_format = Input(UInt(6.W))
        val reg2dp_pixel_mapping = Input(Bool())
        val reg2dp_pixel_sign_override = Input(Bool())
        val reg2dp_datain_width = Input(UInt(13.W))
        val reg2dp_data_reuse = Input(Bool())
        val reg2dp_skip_data_rls = Input(Bool())
        val reg2dp_data_bank = Input(UInt(5.W))
        val reg2dp_pixel_x_offset = Input(UInt(5.W))
        val reg2dp_pad_left = Input(UInt(5.W))
        val reg2dp_pad_right = Input(UInt(6.W))

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
// CDMA image input data fetching logic FSM                           //
////////////////////////////////////////////////////////////////////////
    val img_en = Wire(Bool())
    val need_pending = Wire(Bool())
    val last_skip_data_rls = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}
    val mode_match = Wire(Bool())
    val img_done = Wire(Bool())
    val pending_req_end = Wire(Bool())

    val sIdle :: sPend :: sBusy :: sDone :: Nil = Enum(4)
    val cur_state = RegInit(sIdle)
    val nxt_state = WireInit(sIdle)

    switch (cur_state) {
        is (sIdle) {
        when (img_en & need_pending) { nxt_state := sPend }
        .elsewhen (img_en & io.reg2dp_data_reuse & last_skip_data_rls & mode_match) { nxt_state := sDone }
        .elsewhen (img_en) { nxt_state := sBusy }
        }
        is (sPend) {
        when (pending_req_end) { nxt_state := sBusy }
        }
        is (sBusy) {
        when (img_done) { nxt_state := sDone }
        }
        is (sDone) {
        when (io.status2dma_fsm_switch) { nxt_state := sIdle }
        }
    }
    cur_state := nxt_state
////////////////////////////////////////////////////////////////////////
//  FSM output signals                                                //
////////////////////////////////////////////////////////////////////////
    val is_running_d1 = RegInit(false.B)

    val is_idle = (cur_state === sIdle);
    val is_pending = (cur_state === sPend);
    io.is_running := (cur_state === sBusy);
    val is_done = (cur_state === sDone)

    io.layer_st := img_en & is_idle
    val is_first_running = io.is_running & !is_running_d1

    is_running_d1 := io.is_running
    io.img2status_state := RegNext(nxt_state, false.B)
////////////////////////////////////////////////////////////////////////
//  registers to keep last layer status                               //
////////////////////////////////////////////////////////////////////////
    val last_img = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}
    val last_data_bank = withClock(io.nvdla_core_ng_clk){RegInit(Fill(5, true.B))}
    val pending_req = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}
    val pending_req_d1 = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}
    pending_req_end := pending_req_d1 & ~pending_req

    when(io.reg2dp_op_en & is_idle){
        last_img := img_en
        last_data_bank := io.reg2dp_data_bank
        last_skip_data_rls := img_en & io.reg2dp_skip_data_rls
    }
    pending_req := io.sc2cdma_dat_pending_req
    pending_req_d1 := pending_req

////////////////////////////////////////////////////////////////////////
//  SLCG control signal                                               //
////////////////////////////////////////////////////////////////////////
    val slcg_img_en_w = img_en & (io.is_running | is_pending | is_done);
    val slcg_img_gate_w = Fill(2, !slcg_img_en_w)

    val slcg_img_gate_d3 = withClock(io.nvdla_core_ng_clk){ShiftRegister(slcg_img_gate_w, 2, Fill(2, true.B), true.B)}
    io.slcg_img_gate_dc := slcg_img_gate_d3(0)
    io.slcg_img_gate_wg := slcg_img_gate_d3(1)

//================  Non-SLCG clock domain end ================//

////////////////////////////////////////////////////////////////////////
//  FSM input signals                                                 //
////////////////////////////////////////////////////////////////////////
    val delay_cnt = RegInit("b0".asUInt(5.W))

    val img_end = io.is_running & ~is_first_running & io.sg_is_done & io.pack_is_done
    val delay_cnt_end = 9.U
    img_done := img_end & (delay_cnt === delay_cnt_end)
    val delay_cnt_w = Mux(~io.is_running, "b0".asUInt(6.W), 
                      Mux(img_end, delay_cnt+1.U, delay_cnt))
    need_pending := (last_data_bank =/= io.reg2dp_data_bank)
    mode_match := img_en & last_img
    val is_dc = (io.reg2dp_conv_mode === 0.U )
    val is_pixel = (io.reg2dp_datain_format === 1.U)
    img_en := io.reg2dp_op_en & is_dc & is_pixel

    when(img_end | is_done){
        delay_cnt := delay_cnt_w
    }

////////////////////////////////////////////////////////////////////////
//  pixel format parser                                               //
////////////////////////////////////////////////////////////////////////
    val pixel_planar_nxt = WireInit("h0".asUInt(1.W))
    val pixel_precision_nxt = WireInit("h0".asUInt(2.W))
    val pixel_order_nxt = WireInit("h1".asUInt(11.W))
    val pixel_packed_10b_nxt = WireInit("b0".asUInt(1.W))

//pixel_planar0_sft_nxt  // log2(atmm/BytePerPixel(4 in RGB, 1 in YUV))
//pixel_planar1_sft_nxt  // log2(atmm/BytePerPixel(useless in RGB, 2 in YUV))
//pixel_planar0_mask_nxt // atmm/BytePerPixel -1
//pixel_planar1_mask_nxt // atmm/BytePerPixel -1
    val p0_sft_rgb = log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE/4)
    val p0_mask_rgb = conf.NVDLA_MEMORY_ATOMIC_SIZE/4 - 1
    val p0_sft_yuv = log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)
    val p0_mask_yuv = conf.NVDLA_MEMORY_ATOMIC_SIZE - 1


    val pixel_planar0_sft_nxt = WireInit(p0_sft_rgb.asUInt(3.W))
    val pixel_planar1_sft_nxt = WireInit(3.asUInt(3.W))
    val pixel_planar0_mask_nxt = WireInit(p0_mask_rgb.asUInt(5.W))
    val pixel_planar1_mask_nxt = WireInit(7.asUInt(5.W))
    //               0    T_R8, 
    when(io.reg2dp_pixel_format === "h0".asUInt(6.W)){
        pixel_planar0_sft_nxt := p0_sft_yuv.asUInt(3.W)
        pixel_planar0_mask_nxt := p0_mask_yuv.asUInt(5.W)
    }
    //               c    T_A8B8G8R8,
    .elsewhen(io.reg2dp_pixel_format === "hc".asUInt(6.W)){
        pixel_order_nxt := "h1".asUInt(11.W)
    }
    //               d    T_A8R8G8B8,
    .elsewhen(io.reg2dp_pixel_format === "hd".asUInt(6.W)){
        pixel_order_nxt := "h2".asUInt(11.W)
    }
    //               e    T_B8G8R8A8,
    .elsewhen(io.reg2dp_pixel_format === "he".asUInt(6.W)){
        pixel_order_nxt := "h8".asUInt(11.W)
    }
    //               f    T_R8G8B8A8,
    .elsewhen(io.reg2dp_pixel_format === "hf".asUInt(6.W)){
        pixel_order_nxt := "h20".asUInt(11.W)
    }
    //              10    T_X8B8G8R8,
    .elsewhen(io.reg2dp_pixel_format === "h10".asUInt(6.W)){
        pixel_order_nxt := "h1".asUInt(11.W)
    }
    //              11    T_X8R8G8B8,
    .elsewhen(io.reg2dp_pixel_format === "h11".asUInt(6.W)){
        pixel_order_nxt := "h2".asUInt(11.W)
    }
    //              12    T_B8G8R8X8,
    .elsewhen(io.reg2dp_pixel_format === "h12".asUInt(6.W)){
        pixel_order_nxt := "h8".asUInt(11.W)
    }
    //              13    T_R8G8B8X8,   
    .elsewhen(io.reg2dp_pixel_format === "h13".asUInt(6.W)){
        pixel_order_nxt := "h20".asUInt(11.W)
    }
    //              1a    T_A8Y8U8V8,
    .elsewhen(io.reg2dp_pixel_format === "h1a".asUInt(6.W)){
        pixel_order_nxt := "h2".asUInt(11.W)
    }
    //              1b    T_V8U8Y8A8,
    .elsewhen(io.reg2dp_pixel_format === "h1b".asUInt(6.W)){
        pixel_order_nxt := "h8".asUInt(11.W)
    }
    //              1c    T_Y8___U8V8_N444,    
    .elsewhen(io.reg2dp_pixel_format === "h1c".asUInt(6.W)){
        pixel_planar_nxt := "h1".asUInt(1.W)
        pixel_order_nxt := "h200".asUInt(11.W)
        pixel_planar0_sft_nxt := log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE).asUInt(3.W)
        pixel_planar1_sft_nxt := log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE/2).asUInt(3.W)
        pixel_planar0_mask_nxt := (conf.NVDLA_MEMORY_ATOMIC_SIZE-1).asUInt(5.W)
        pixel_planar1_mask_nxt := (conf.NVDLA_MEMORY_ATOMIC_SIZE/2-1).asUInt(5.W)
    }
    //              1d    T_Y8___V8U8_N444
    .elsewhen(io.reg2dp_pixel_format === "h1c".asUInt(6.W)){
        pixel_planar_nxt := "h1".asUInt(1.W)
        pixel_planar0_sft_nxt := log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE).asUInt(3.W)
        pixel_planar1_sft_nxt := log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE/2).asUInt(3.W)
        pixel_planar0_mask_nxt := (conf.NVDLA_MEMORY_ATOMIC_SIZE-1).asUInt(5.W)
        pixel_planar1_mask_nxt := (conf.NVDLA_MEMORY_ATOMIC_SIZE/2-1).asUInt(5.W)
    }
    val is_int8 = true.B
    val is_input_int8 = true.B
    val pixel_data_expand_nxt = false.B
    val pixel_data_shrink_nxt = false.B

    //////// pixel_lp_burst, pixel_width_burst, pixel_rp_burst ////////
    val pixel_uint_nxt = (io.reg2dp_pixel_sign_override === false.B)
    val pixel_planar1_x_offset = (io.reg2dp_pixel_x_offset & pixel_planar1_mask_nxt)
    val pixel_planar0_lp_filled = io.reg2dp_pad_left & pixel_planar0_mask_nxt
    val pixel_planar1_lp_filled = io.reg2dp_pad_left & pixel_planar1_mask_nxt
    val pixel_planar0_lp_burst_w = Mux(io.reg2dp_pixel_x_offset >= pixel_planar0_lp_filled, 
    io.reg2dp_pad_left >> pixel_planar0_sft_nxt, io.reg2dp_pad_left >> pixel_planar0_sft_nxt+&1.U)(3, 0)
    val pixel_planar1_lp_burst_w = Mux(io.reg2dp_pixel_x_offset >= pixel_planar1_lp_filled, 
    io.reg2dp_pad_left >> pixel_planar1_sft_nxt, io.reg2dp_pad_left >> pixel_planar1_sft_nxt+&1.U)(2, 0)
    val pixel_planar0_fetch_width = io.reg2dp_datain_width +& io.reg2dp_pixel_x_offset
    val pixel_planar1_fetch_width = io.reg2dp_datain_width +& pixel_planar1_x_offset 
    val pixel_planar0_width_burst_w = (pixel_planar0_fetch_width >> pixel_planar0_sft_nxt) +& 1.U
    val pixel_planar1_width_burst_w = (pixel_planar1_fetch_width >> pixel_planar1_sft_nxt) +& 1.U
    val pixel_store_width = io.reg2dp_pad_left +& io.reg2dp_datain_width +& io.reg2dp_pad_right
    val pixel_planar0_burst_need_w = (pixel_store_width >> pixel_planar0_sft_nxt) +& 2.U
    val pixel_planar1_burst_need_w = (pixel_store_width >> pixel_planar0_sft_nxt) +& 2.U
    val pixel_planar0_rp_burst_w = (pixel_planar0_burst_need_w -& pixel_planar0_lp_burst_w -& pixel_planar0_width_burst_w)(3, 0)
    val pixel_planar1_rp_burst_w = (pixel_planar0_burst_need_w -& pixel_planar0_lp_burst_w -& pixel_planar0_width_burst_w)(2, 0)
    val byte_per_pixel = Mux(~(pixel_precision_nxt.orR), "h3".asUInt(3.W), "h6".asUInt(3.W))

    ////////////////////////////////////////////////
    // early end control
    ////////////////////////////////////////////////
    val pixel_planar1_total_width_w 
    = (io.reg2dp_pad_left +& io.reg2dp_datain_width(log2Ceil(conf.DMAIF/8)-1, 0) +& io.reg2dp_pad_right +& 1.U)(log2Ceil(conf.DMAIF/8)-1, 0)

    val pixel_planar1_tail_width_w
    = (pixel_planar1_total_width_w * byte_per_pixel +& Fill(log2Ceil(conf.DMAIF/8), true.B))(log2Ceil(conf.DMAIF/8)+2, log2Ceil(conf.DMAIF/8))

    /////////////////////////////
    val pixel_early_end_w = pixel_planar_nxt & 
    ((pixel_planar1_tail_width_w === 1.U)| 
    (pixel_planar1_tail_width_w === 4.U)|
    ((pixel_planar1_tail_width_w === 2.U)&  
    (Cat(pixel_planar1_total_width_w, 0.U)> (conf.NVDLA_CDMA_DMAIF_BW/8).U)))
    
    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    val pixel_element_sft_w = (io.reg2dp_pixel_x_offset -& io.reg2dp_pad_left(conf.ATMMBW-1, 0))(conf.ATMMBW-1, 0)
    val pixel_planar0_byte_sft_w = (Cat(pixel_element_sft_w, Fill(conf.ATMMBW, false.B)) >> pixel_planar0_sft_nxt)(conf.ATMMBW-1, 0)
    val pixel_planar1_byte_sft_w = (Cat(pixel_element_sft_w, Fill(conf.ATMMBW, false.B)) >> pixel_planar1_sft_nxt)(conf.ATMMBW-1, 0)

    val pixel_planar0_bundle_limit_w = "h8".asUInt(4.W)
    val pixel_planar0_bundle_limit_1st_w = "h9".asUInt(4.W)
    val pixel_planar1_bundle_limit_w = "h10".asUInt(5.W)
    val pixel_planar1_bundle_limit_1st_w = "h11".asUInt(5.W)

    val planar1_vld_w = pixel_planar_nxt.toBool
    val pixel_planar0_lp_vld_w = pixel_planar0_lp_burst_w.orR
    val pixel_planar1_lp_vld_w = pixel_planar1_lp_burst_w.orR
    val pixel_planar0_rp_vld_w = pixel_planar0_rp_burst_w.orR
    val pixel_planar1_rp_vld_w = pixel_planar1_rp_burst_w.orR

    io.pixel_planar := RegEnable(pixel_planar_nxt, false.B, io.layer_st)
    io.pixel_precision := RegEnable(pixel_precision_nxt, "b0".asUInt(2.W), io.layer_st)
    io.pixel_order := RegEnable(pixel_order_nxt, "b0".asUInt(11.W), io.layer_st)
    io.pixel_packed_10b := RegEnable(pixel_packed_10b_nxt, false.B, io.layer_st)
    io.pixel_data_expand := RegEnable(pixel_data_expand_nxt, false.B, io.layer_st)
    io.pixel_data_shrink := RegEnable(pixel_data_shrink_nxt, false.B, io.layer_st)
    io.pixel_uint := RegEnable(pixel_uint_nxt, false.B, io.layer_st)
    io.pixel_planar0_sft := RegEnable(pixel_planar0_sft_nxt, "b0".asUInt(3.W), io.layer_st)
    io.pixel_planar1_sft := RegEnable(pixel_planar1_sft_nxt, "b0".asUInt(3.W), io.layer_st & planar1_vld_w)
    io.pixel_planar0_lp_burst := RegEnable(pixel_planar0_lp_burst_w, "b0".asUInt(4.W), io.layer_st)
    io.pixel_planar1_lp_burst := RegEnable(pixel_planar1_lp_burst_w, "b0".asUInt(3.W), io.layer_st & planar1_vld_w)
    io.pixel_planar0_lp_vld := RegEnable(pixel_planar0_lp_vld_w, false.B, io.layer_st)
    io.pixel_planar1_lp_vld := RegEnable(pixel_planar1_lp_vld_w, false.B, io.layer_st & planar1_vld_w)
    io.pixel_planar0_width_burst := RegEnable(pixel_planar0_width_burst_w, "b0".asUInt(14.W), io.layer_st)
    io.pixel_planar1_width_burst := RegEnable(pixel_planar1_width_burst_w, "b0".asUInt(14.W), io.layer_st & planar1_vld_w)
    io.pixel_planar0_rp_burst := RegEnable(pixel_planar0_rp_burst_w, "b0".asUInt(4.W), io.layer_st)
    io.pixel_planar1_rp_burst := RegEnable(pixel_planar1_rp_burst_w, "b0".asUInt(3.W), io.layer_st & planar1_vld_w)
    io.pixel_planar0_rp_vld := RegEnable(pixel_planar0_rp_vld_w, false.B, io.layer_st)
    io.pixel_planar1_rp_vld := RegEnable(pixel_planar1_rp_vld_w, false.B, io.layer_st & planar1_vld_w)
    io.pixel_early_end := RegEnable(pixel_early_end_w, false.B, io.layer_st & planar1_vld_w)
    io.pixel_planar0_byte_sft := RegEnable(pixel_planar0_byte_sft_w, "b0".asUInt(conf.ATMMBW.W), io.layer_st)
    io.pixel_planar1_byte_sft := RegEnable(pixel_planar1_byte_sft_w, "b0".asUInt(conf.ATMMBW.W), io.layer_st & planar1_vld_w)
    io.pixel_bank := RegEnable(io.reg2dp_data_bank +& 1.U, "b0".asUInt(6.W), io.layer_st)
    io.pixel_planar0_bundle_limit := RegEnable(pixel_planar0_bundle_limit_w, "b0".asUInt(4.W), io.layer_st)
    io.pixel_planar1_bundle_limit := RegEnable(pixel_planar1_bundle_limit_w, "b0".asUInt(5.W), io.layer_st & planar1_vld_w)
    io.pixel_planar0_bundle_limit_1st := RegEnable(pixel_planar0_bundle_limit_1st_w, "b0".asUInt(4.W), io.layer_st)
    io.pixel_planar1_bundle_limit_1st := RegEnable(pixel_planar1_bundle_limit_1st_w, "b0".asUInt(5.W), io.layer_st & planar1_vld_w)

}}

object NV_NVDLA_CDMA_IMG_ctrlDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_IMG_ctrl())
}

