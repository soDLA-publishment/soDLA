// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver


// class NV_NVDLA_CDMA_CVT_cell(implicit conf: cdmaConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //nvdla core clock
//         val nvdla_core_clk = Input(Clock())
//         val nvdla_core_ng_clk = Input(Clock()) 
//         //input state
//         val pack_is_done = Input(Bool())
//         val sc2cdma_dat_pending_req = Input(Bool())
//         val sg_is_done = Input(Bool())
//         val status2dma_fsm_switch = Input(Bool())
//         //output state
//         val img2status_state = Output(UInt(2.W))
//         val is_running = Output(Bool())
//         val layer_st = Output(Bool())
//         //pixel
//         val pixel_bank = Output(UInt(6.W))
//         val pixel_data_expand = Output(Bool())
//         val pixel_data_shrink = Output(Bool())
//         val pixel_early_end = Output(Bool())
//         val pixel_order = Output(UInt(11.W))
//         val pixel_packed_10b = Output(Bool())
//         val pixel_planar = Output(Bool())
//         val pixel_precision = Output(UInt(2.W))
//         val pixel_uint = Output(Bool())
//         val slcg_img_gate_dc = Output(Bool())
//         val slcg_img_gate_w = Output(Bool())

//         val pixel_planar0_bundle_limit = Output(UInt(4.W))
//         val pixel_planar0_bundle_limit_1st = Output(UInt(4.W))
//         val pixel_planar0_byte_sft = Output(UInt(conf.ATMMBW.W))
//         val pixel_planar0_lp_burst = Output(UInt(4.W))
//         val pixel_planar0_lp_vld = Output(Bool())
//         val pixel_planar0_rp_burst = Output(UInt(4.W))
//         val pixel_planar0_rp_vld = Output(Bool())
//         val pixel_planar0_sft = Output(UInt(3.W))
//         val pixel_planar0_width_burst = Output(UInt(14.W))

//         val pixel_planar1_bundle_limit = Output(UInt(5.W))
//         val pixel_planar1_bundle_limit_1st = Output(UInt(5.W))
//         val pixel_planar1_byte_sft = Output(UInt(conf.ATMMBW.W)) 
//         val pixel_planar1_lp_burst = Output(UInt(3.W))
//         val pixel_planar1_lp_vld = Output(Bool())
//         val pixel_planar1_rp_burst = Output(UInt(3.W))
//         val pixel_planar1_rp_vld = Output(Bool())
//         val pixel_planar1_sft = Output(UInt(3.W))
//         val pixel_planar1_width_burst = Output(UInt(14.W))

//         //reg2dp
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_conv_mode = Input(Bool())
//         val reg2dp_in_precision = Input(UInt(2.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_datain_format = Input(Bool())
//         val reg2dp_pixel_format = Input(UInt(6.W))
//         val reg2dp_pixel_mapping = Input(Bool())
//         val reg2dp_pixel_sign_override = Input(Bool())
//         val reg2dp_datain_width = Input(UInt(13.W))
//         val reg2dp_data_reuse = Input(Bool())
//         val reg2dp_skip_data_rls = Input(Bool())
//         val reg2dp_data_bank = Input(UInt(5.W))
//         val reg2dp_pixel_x_offset = Input(UInt(5.W))
//         val reg2dp_pad_left = Input(UInt(5.W))
//         val reg2dp_pad_righ = Input(UInt(6.W))

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
// // CDMA image input data fetching logic FSM                           //
// ////////////////////////////////////////////////////////////////////////
//     val img_en = Wire(Bool())
//     val need_pending = Wire(Bool())
//     val last_skip_data_rls = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}
//     val mode_match = Wire(Bool())
//     val img_done = Wire(Bool())
//     val pending_req_end = Wire(Bool())

//     val sIdle :: sPend :: sBusy :: sDone :: Nil = Enum(4)
//     val cur_state = RegInit(sIdle)
//     val nxt_state = WireInit(sIdle)

//     switch (cur_state) {
//         is (sIdle) {
//         when (img_en & need_pending) { nxt_state := sPend }
//         .elsewhen (img_en & io.reg2dp_data_reuse & last_skip_data_rls & mode_match) { nxt_state := sDone }
//         .elsewhen (img_en) { nxt_state := sBusy }
//         }
//         is (sPend) {
//         when (pending_req_end) { nxt_state := sBusy }
//         }
//         is (sBusy) {
//         when (img_done) { nxt_state := sDone }
//         }
//         is (sDone) {
//         when (io.status2dma_fsm_switch) { nxt_state := sIdle }
//         }
//     }
//     cur_state := nxt_state
// ////////////////////////////////////////////////////////////////////////
// //  FSM output signals                                                //
// ////////////////////////////////////////////////////////////////////////
//     val img2status_state = RegInit(false.B)
//     val is_running_d1 = RegInit(false.B)

//     val is_idle = (cur_state === sIdle);
//     val is_pending = (cur_state === sPend);
//     io.is_running = (cur_state === sBusy);
//     val is_done = (cur_state === sDone)

//     io.layer_st = img_en & is_idle
//     val img2status_state_w = nxt_state
//     val is_first_running = io.is_running & !is_running_d1

//     img2status_state := img2status_state_w
//     is_running_d1 := io.is_running

// ////////////////////////////////////////////////////////////////////////
// //  registers to keep last layer status                               //
// ////////////////////////////////////////////////////////////////////////
//     val pending_req_end = pending_req_d1 & ~pending_req

//     val last_img = withClock(nvdla_core_ng_clk){RegInit(false.B)}
//     val last_data_bank = withClock(nvdla_core_ng_clk){RegInit(Fill(5, true.B))}
//     val last_skip_data_rls = withClock(nvdla_core_ng_clk){RegInit(false.B)}
//     val pending_req = withClock(nvdla_core_ng_clk){RegInit(false.B)}
//     val pending_req_d1 = withClock(nvdla_core_ng_clk){RegInit(false.B)}

//     when(io.reg2dp_op_en & is_idle){
//         last_img := img_en
//         last_data_bank := io.reg2dp_data_bank
//         last_skip_data_rls := img_en & io.reg2dp_skip_data_rls
//     }
//     pending_req := io.sc2cdma_dat_pending_req
//     pending_req_d1 := pending_req

// ////////////////////////////////////////////////////////////////////////
// //  SLCG control signal                                               //
// ////////////////////////////////////////////////////////////////////////
//     val slcg_img_en_w = img_en & (io.is_running | is_pending | is_done);
//     val slcg_img_gate_w = Fill(2, !slcg_img_en_w)

//     val slcg_img_gate_d3 = withClock(io.nvdla_core_ng_clk){ShiftRegister(slcg_img_gate_w, 2, Fill(2, true.B))}
//     val slcg_img_gate_dc = slcg_img_gate_d3(0)
//     val slcg_img_gate_wg = slcg_img_gate_d3(1)

// //================  Non-SLCG clock domain end ================//

// ////////////////////////////////////////////////////////////////////////
// //  FSM input signals                                                 //
// ////////////////////////////////////////////////////////////////////////
//     val delay_cnt = RegInit("b0".asUInt(5.W))

//     img_end := io.is_running & ~is_first_running & io.sg_is_done & io.pack_is_done
//     val delay_cnt_end = 9.U
//     img_done := img_end & (delay_cnt === delay_cnt_end)
//     val delay_cnt_w = Mux(~io.is_running, "b0".asUInt(6.W), 
//                       Mux(img_end, delay_cnt+1.U, delay_cnt))
//     need_pending := (last_data_bank =/= io.reg2dp_data_bank)
//     mode_match := img_en & last_img
//     val is_dc = (io.reg2dp_conv_mode === 0.U )
//     val is_pixel = (io.reg2dp_datain_format === 1.U)
//     img_en := io.reg2dp_op_en & is_dc & is_pixel

//     when(img_end | is_done){
//         delay_cnt := delay_cnt_w
//     }

// ////////////////////////////////////////////////////////////////////////
// //  pixel format parser                                               //
// ////////////////////////////////////////////////////////////////////////
//     val pixel_planar_nxt = WireInit("h0".asUInt(1.W))
//     val pixel_precision_nxt = WireInit("h0".asUInt(2.W))
//     val pixel_order_nxt = WireInit("h1".asUInt(11.W))
//     val pixel_packed_10b_nxt = WireInit("b0".asUInt(1.W))

//     when(io.reg2dp_pixel_format === "h0".asUInt(6.W)){

//     }









//     // val chn_in_prdy = Wire(Bool())
//     // val chn_alu_prdy = Wire(Bool())
//     // val chn_out_pvld = Wire(Bool())
//     // val chn_sync_pvld = Wire(Bool())
//     // val chn_sync_prdy = Wire(Bool())
//     // val chn_data_out = Wire(SInt(16.W))

//     // val chn_in_pvld  = io.chn_data_in_rsc_vz
//     // val chn_alu_pvld = io.chn_alu_in_rsc_vz
//     // val chn_data_in = io.chn_data_in_rsc_z
//     // val chn_alu_in = io.chn_alu_in_rsc_z
//     // val cfg_mul_in = io.cfg_mul_in_rsc_z
//     // val chn_out_prdy = io.chn_data_out_rsc_vz

//     // io.chn_data_in_rsc_lz := chn_in_prdy
//     // io.chn_alu_in_rsc_lz := chn_alu_prdy
//     // io.chn_data_out_rsc_lz := chn_out_pvld
//     // io.chn_data_out_rsc_z := chn_data_out

//     // chn_sync_pvld := chn_alu_pvld  & chn_in_pvld
//     // chn_alu_prdy := chn_sync_prdy & chn_in_pvld
//     // chn_in_prdy := chn_sync_prdy & chn_alu_pvld

//     // val chn_data_ext = Wire(SInt(18.W))
//     // chn_data_ext := chn_data_in
//     // val chn_alu_ext = Wire(SInt(18.W))
//     // chn_alu_ext := chn_alu_in

//     // //sub
//     // val sub_out_prdy = Wire(Bool())
//     // val sub_dout = chn_data_ext - chn_alu_ext

//     // val pipe_p1 = Module(new NV_NVDLA_CDMA_CVT_CELL_pipe_p1)
//     // pipe_p1.io.nvdla_core_clk := io.nvdla_core_clk
//     // pipe_p1.io.chn_sync_pvld := chn_sync_pvld
//     // pipe_p1.io.sub_dout := sub_dout
//     // pipe_p1.io.sub_out_prdy := sub_out_prdy
//     // chn_sync_prdy := pipe_p1.io.chn_sync_prdy
//     // val sub_data_out = pipe_p1.io.sub_data_out
//     // val sub_out_pvld = pipe_p1.io.sub_out_pvld

//     // //mul 
//     // val mul_out_prdy = Wire(Bool())
//     // val mul_dout = sub_data_out * cfg_mul_in

//     // val pipe_p2 = Module(new NV_NVDLA_CDMA_CVT_CELL_pipe_p2)
//     // pipe_p2.io.nvdla_core_clk := io.nvdla_core_clk
//     // pipe_p2.io.mul_dout := mul_dout
//     // pipe_p2.io.mul_out_prdy := mul_out_prdy
//     // pipe_p2.io.sub_out_pvld := sub_out_pvld
//     // val mul_data_out = pipe_p2.io.mul_data_out
//     // val mul_out_pvld = pipe_p2.io.mul_out_pvld
//     // sub_out_prdy := pipe_p2.io.sub_out_prdy

//     // //truncate
//     // val u_shiftright_su = Module(new NV_NVDLA_HLS_shiftrightsu(34, 17, 6))
//     // u_shiftright_su.io.data_in := mul_data_out.asUInt
//     // u_shiftright_su.io.shift_num := io.cfg_truncate
//     // val tru_dout = u_shiftright_su.io.data_out
    
//     // //unsigned
//     // val tru_out_prdy = Wire(Bool())
//     // val tru_data_out = tru_dout
//     // val tru_out_pvld = mul_out_pvld
//     // mul_out_prdy := tru_out_prdy

//     // val u_saturate_int16 = Module(new NV_NVDLA_HLS_saturate(17, 16)) 
//     // u_saturate_int16.io.data_in := tru_data_out
//     // val dout_int16_sat = u_saturate_int16.io.data_out.asSInt 

//     // val u_saturate_int8 = Module(new NV_NVDLA_HLS_saturate(17, 8)) 
//     // u_saturate_int8.io.data_in := tru_data_out
//     // val dout_int8_sat = u_saturate_int8.io.data_out.asSInt 

//     // val chn_dout = Mux(io.cfg_out_precision === 1.U, dout_int16_sat, dout_int8_sat)

//     // val pipe_p3 = Module(new NV_NVDLA_CDMA_CVT_CELL_pipe_p3)
//     // pipe_p3.io.nvdla_core_clk := io.nvdla_core_clk
//     // pipe_p3.io.chn_dout := chn_dout
//     // pipe_p3.io.chn_out_prdy := chn_out_prdy
//     // pipe_p3.io.tru_out_pvld := tru_out_pvld
//     // chn_data_out  := pipe_p3.io.chn_data_out
//     // chn_out_pvld := pipe_p3.io.chn_out_pvld
//     // tru_out_prdy := pipe_p3.io.tru_out_prdy  

// }}
