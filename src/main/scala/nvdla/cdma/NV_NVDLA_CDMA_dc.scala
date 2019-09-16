package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver
import scala.math._

class NV_NVDLA_CDMA_dcIO(implicit conf: nvdlaConfig) extends Bundle{

    //clk & pwr_ram_bus
    val nvdla_core_clk = Input(Clock())
    val nvdla_core_ng_clk = Input(Clock())
    val pwrbus_ram_pd = Input(UInt(32.W))

    //mcif
    val dc_dat2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val mcif2dc_dat_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))

    //cvif
    val dc_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
    val cvif2dc_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))) else None

    //cvt
    val dc2cvt_dat_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Output(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W))) else None
    val dc2cvt_dat_wr = new nvdla_wr_if(17, conf.DMAIF)
    val dc2cvt_dat_wr_info_pd = Output(UInt(12.W))

    //reg2dp
    val reg2dp_op_en = Input(Bool())
    val reg2dp_conv_mode = Input(Bool())
    val reg2dp_data_reuse = Input(Bool())
    val reg2dp_skip_data_rls = Input(Bool())
    val reg2dp_datain_format = Input(Bool())
    val reg2dp_datain_width = Input(UInt(13.W))
    val reg2dp_datain_height = Input(UInt(13.W))
    val reg2dp_datain_channel = Input(UInt(13.W))
    val reg2dp_datain_ram_type = Input(Bool())
    val reg2dp_datain_addr_high_0 = Input(UInt(32.W))
    val reg2dp_datain_addr_low_0 = Input(UInt((32-conf.ATMMBW).W))
    val reg2dp_line_stride = Input(UInt((32-conf.ATMMBW).W))
    val reg2dp_surf_stride = Input(UInt((32-conf.ATMMBW).W))
    val reg2dp_batch_stride = Input(UInt((32-conf.ATMMBW).W))
    val reg2dp_line_packed = Input(Bool())
    val reg2dp_surf_packed = Input(Bool())
    val reg2dp_batches = Input(UInt(5.W))
    val reg2dp_entries = Input(UInt(17.W))  //entry number per slice
    val reg2dp_grains = Input(UInt(12.W))
    val reg2dp_data_bank = Input(UInt(5.W))
    val reg2dp_dma_en = Input(Bool())
    val dp2reg_dc_rd_stall = Output(UInt(32.W))
    val dp2reg_dc_rd_latency = Output(UInt(32.W))

    //slcg
    val slcg_dc_gate_wg = Output(Bool())
    val slcg_dc_gate_img = Output(Bool())

    val dc2status_state = Output(UInt(2.W))
    val dc2status_dat_updt = ValidIO(new updt_entries_slices_if)

    val status2dma_fsm_switch = Input(Bool())
    val status2dma_valid_slices = Input(UInt(14.W))
    val status2dma_free_entries = Input(UInt(15.W))
    val status2dma_wr_idx = Input(UInt(15.W))

    //conf.ATMM_NUM should be 1
    val dc2sbuf_p0_wr = new nvdla_wr_if(8, conf.ATMM)
    val dc2sbuf_p0_rd = new nvdla_rd_if(8, conf.ATMM)

    val sc2cdma_dat_pending_req = Input(Bool())

}

class NV_NVDLA_CDMA_dc(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new NV_NVDLA_CDMA_dcIO)

withClock(io.nvdla_core_clk){
////////////////////////////////////////////////////////////////////////
// CDMA direct convolution data fetching logic FSM                    //
////////////////////////////////////////////////////////////////////////
    val dc_en = Wire(Bool())
    val need_pending = Wire(Bool())
    val pending_req_end = Wire(Bool())
    val mode_match = Wire(Bool())
    val fetch_done = Wire(Bool())
    val last_skip_data_rls = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}

    val sIdle :: sPend :: sBusy :: sDone :: Nil = Enum(4)
    val cur_state = RegInit(sIdle)
    val nxt_state = WireInit(sIdle)

    switch (cur_state) {
        is (sIdle) {
        when (dc_en & need_pending) { nxt_state := sPend }
        .elsewhen (dc_en & io.reg2dp_data_reuse & last_skip_data_rls & mode_match) { nxt_state := sDone }
        .elsewhen (dc_en) { nxt_state := sBusy }
        }
        is (sPend) {
        when (pending_req_end) { nxt_state := sBusy }
        }
        is (sBusy) {
        when (fetch_done) { nxt_state := sDone }
        }
        is (sDone) {
        when (io.status2dma_fsm_switch) { nxt_state := sIdle }
        }
    }
    cur_state := nxt_state
////////////////////////////////////////////////////////////////////////
//  FSM input signals                                                 //
////////////////////////////////////////////////////////////////////////
    val is_running = Wire(Bool())
    val is_rsp_done = Wire(Bool())
    val delay_cnt = RegInit("b0".asUInt(5.W))
    val delay_cnt_end = conf.CDMA_STATUS_LATENCY.U
    val last_data_bank = withClock(io.nvdla_core_ng_clk){RegInit(Fill(5, true.B))}
    val last_dc = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}

    fetch_done := is_running & is_rsp_done & (delay_cnt === delay_cnt_end)
    need_pending := (last_data_bank =/= io.reg2dp_data_bank) 
    mode_match := dc_en & last_dc
    val is_feature = (io.reg2dp_datain_format === 0.U)
    val is_dc = (io.reg2dp_conv_mode === 0.U)
    dc_en := io.reg2dp_op_en & is_dc & is_feature

    when(~is_running){
        delay_cnt := "b0".asUInt(5.W)
    }
    .elsewhen(is_rsp_done){
        delay_cnt := delay_cnt + 1.U
    }
////////////////////////////////////////////////////////////////////////
//  FSM output signals                                                //
////////////////////////////////////////////////////////////////////////  
    val is_idle = Wire(Bool())
    val layer_st = dc_en & is_idle
    is_idle := (cur_state === sIdle)
    val is_pending = (cur_state === sPend)
    is_running := (cur_state === sBusy)
    val is_done = (cur_state === sDone)
    val is_nxt_running = (nxt_state === sBusy)
    val is_first_running = ~is_running & is_nxt_running
    val dc2status_state_w = nxt_state

    io.dc2status_state := RegNext(dc2status_state_w, false.B)
////////////////////////////////////////////////////////////////////////
//  registers to keep last layer status                               //
////////////////////////////////////////////////////////////////////////
    val pending_req = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}
    val pending_req_d1 = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}

    pending_req_end := pending_req_d1 & ~pending_req
    
    when(io.reg2dp_op_en & is_idle){
        last_dc := dc_en
        last_data_bank := io.reg2dp_data_bank
        last_skip_data_rls := dc_en & io.reg2dp_skip_data_rls
        pending_req := io.sc2cdma_dat_pending_req
        pending_req_d1 := pending_req
    }
////////////////////////////////////////////////////////////////////////
//  SLCG control signal                                               //
////////////////////////////////////////////////////////////////////////
    val slcg_dc_en_w = dc_en & (is_running | is_pending | is_done)
    val slcg_dc_gate_w = Fill(2, ~slcg_dc_en_w)

    io.slcg_dc_gate_wg := withClock(io.nvdla_core_ng_clk){ShiftRegister(slcg_dc_gate_w(0), 3, Fill(2, true.B), true.B)}
    io.slcg_dc_gate_img := withClock(io.nvdla_core_ng_clk){ShiftRegister(slcg_dc_gate_w(1), 3, Fill(2, true.B), true.B)}

//================  Non-SLCG clock domain end ================//

////////////////////////////////////////////////////////////////////////
//  registers to calculate local values                               //
////////////////////////////////////////////////////////////////////////
    val data_width = RegInit("b0".asUInt(16.W))
    val data_width_sub_one = RegInit("b0".asUInt(15.W))
    val data_height = RegInit("b0".asUInt(14.W))
    val data_batch = RegInit("b0".asUInt(6.W))
    val data_entries = RegInit("b0".asUInt(18.W))
    val fetch_grain = RegInit("b0".asUInt(13.W))
    val data_surface = RegInit("b0".asUInt(11.W))
    val grain_addr = RegInit("b0".asUInt((12+32-conf.ATMMBW).W))
    val data_bank = RegInit("b0".asUInt(6.W))

    val is_packed_1x1 = (io.reg2dp_datain_width === 0.U) & (io.reg2dp_datain_height === 0.U) & io.reg2dp_surf_packed 
    val data_surface_inc = io.reg2dp_datain_channel(12, conf.ATMMBW) +& 1.U
    val is_data_normal = true.B
    val data_height_w = io.reg2dp_datain_height +& 1.U
    val data_surface_w = Mux(is_packed_1x1, 1.U, data_surface_inc)
    val fetch_grain_w = Mux(~io.reg2dp_line_packed, 1.U, io.reg2dp_grains+1.U)
    val grain_addr_w = fetch_grain_w * io.reg2dp_line_stride

    when(layer_st){
        when(is_packed_1x1){
            data_width := data_surface_inc
        }
        .otherwise{
            data_width := io.reg2dp_datain_width +& 1.U
        }
        data_width_sub_one := Mux(is_packed_1x1, io.reg2dp_datain_channel(12, conf.ATMMBW), io.reg2dp_datain_width)
        data_height := data_height_w
        data_batch := io.reg2dp_batches +& 1.U
        data_entries := io.reg2dp_entries + 1.U
        fetch_grain := fetch_grain_w
        data_surface := data_surface_w
        grain_addr := grain_addr_w
        data_bank := io.reg2dp_data_bank +& 1.U
    }

////////////////////////////////////////////////////////////////////////
//  prepare for address generation                                    //
////////////////////////////////////////////////////////////////////////
///////////// stage 1 /////////////}
    val req_height_cnt_d1 = RegInit("b0".asUInt(14.W))
    val req_cur_grain_d1 = RegInit("b0".asUInt(14.W))
    val is_req_grain_last_d1 = RegInit(false.B)
    val pre_valid_d1 = RegInit(false.B)
    val pre_ready_d1 = Wire(Bool())

    val pre_ready = ~pre_valid_d1 | pre_ready_d1
    val pre_reg_en = is_running & (req_height_cnt_d1 =/= data_height) & pre_ready
    val req_slice_left = data_height - req_height_cnt_d1
    val is_req_grain_last = (req_slice_left <= fetch_grain)
    val req_cur_grain_w  = Mux(is_req_grain_last, req_slice_left, fetch_grain)

    when(layer_st){
        req_height_cnt_d1 := "b0".asUInt(14.W)
    }
    .elsewhen(pre_reg_en){
        req_height_cnt_d1 := req_height_cnt_d1 + req_cur_grain_w
    }

    when(pre_reg_en){
        req_cur_grain_d1 := req_cur_grain_w
        is_req_grain_last_d1 := is_req_grain_last
    }
    when(~is_running){
        pre_valid_d1 := false.B
    }
    .otherwise{
        when(req_height_cnt_d1 =/= data_height){
            pre_valid_d1 := true.B
        }
        .elsewhen(pre_ready_d1){
            pre_valid_d1 := false.B
        }
    }
    ///////////// stage 2 /////////////
    val req_atomic_d2 = RegInit(false.B)
    val entry_per_batch_d2 = RegInit("b0".asUInt(18.W))
    val req_cur_grain_d2 = RegInit("b0".asUInt(14.W))
    val is_req_grain_last_d2 = RegInit(false.B)
    val pre_valid_d2 = RegInit(false.B)
    val pre_ready_d2 = Wire(Bool())

    val req_cur_atomic = (req_cur_grain_d1 * data_width)(conf.CDMA_GRAIN_MAX_BIT, 0)
    val entry_per_batch = (data_entries * data_batch)(17, 0)
    pre_ready_d1 := ~pre_valid_d2 | pre_ready_d2
    val pre_reg_en_d1 = pre_valid_d1 & pre_ready_d1

    when(pre_reg_en_d1){
        req_atomic_d2 := req_cur_atomic
        entry_per_batch_d2 := entry_per_batch
        req_cur_grain_d2 := req_cur_grain_d1
        is_req_grain_last_d2 := is_req_grain_last_d1
    }

    when(~is_running){
        pre_valid_d2 := false.B
    }
    .otherwise{
        when(pre_valid_d1){
            pre_valid_d2 := true.B
        }
        .elsewhen(pre_ready_d2){
            pre_valid_d2 := false.B
        }
    }
    ///////////// stage 3 /////////////
    val req_atomic_0_d3 = RegInit("b0".asUInt(conf.CDMA_GRAIN_MAX_BIT.W))
    val req_atomic_1_d3 = RegInit("b0".asUInt(conf.CDMA_GRAIN_MAX_BIT.W))
    val req_pre_valid_0_d3 = RegInit(false.B)
    val req_pre_valid_1_d3 = RegInit(false.B)
    val req_entry_0_d3 = RegInit("b0".asUInt(18.W)) 
    val req_entry_1_d3 = RegInit("b0".asUInt(18.W)) 
    val rsp_entry_init = RegInit("b0".asUInt(18.W))
    val rsp_entry_last = RegInit("b0".asUInt(18.W))
    val rsp_batch_entry_init = RegInit("b0".asUInt(18.W))
    val rsp_batch_entry_last = RegInit("b0".asUInt(18.W))
    val rsp_slice_init = RegInit("b0".asUInt(14.W))
    val rsp_slice_last = RegInit("b0".asUInt(14.W))
    val pre_gen_sel = RegInit(false.B)

    val entry_required = (req_cur_grain_d2 * entry_per_batch_d2)(17, 0)
    val pre_reg_en_d2_g0 = pre_valid_d2 & ~pre_gen_sel & ~req_pre_valid_0_d3
    val pre_reg_en_d2_g1 = pre_valid_d2 & pre_gen_sel & ~req_pre_valid_1_d3
    pre_ready_d2 := ((~pre_gen_sel & ~req_pre_valid_0_d3) | (pre_gen_sel & ~req_pre_valid_1_d3))
    val pre_reg_en_d2 = pre_valid_d2 & pre_ready_d2
    val pre_reg_en_d2_init = pre_valid_d2 & pre_ready_d2 & ~is_req_grain_last_d2
    val pre_reg_en_d2_last = pre_valid_d2 & pre_ready_d2 & is_req_grain_last_d2

    when(pre_reg_en_d2_g0){
        req_atomic_0_d3 := req_atomic_d2
        req_entry_0_d3 := entry_required
    }
    when(pre_reg_en_d2_g1){
        req_atomic_1_d3 := req_atomic_d2
        req_entry_1_d3 := entry_required
    }
    when(pre_reg_en_d2_init){
        rsp_entry_init := entry_required
        rsp_batch_entry_init := entry_per_batch_d2
        rsp_slice_init := req_cur_grain_d2
    }
    when(pre_reg_en_d2_last){
        rsp_entry_last := entry_required
        rsp_batch_entry_last := entry_per_batch_d2
        req_cur_grain_d2 := req_cur_grain_d2
    }

    ///////////// prepare control logic /////////////
    val req_csm_sel = RegInit(false.B)

    val req_grain_reg_en = Wire(Bool())
    val csm_reg_en = req_grain_reg_en
    val req_pre_valid_0_w = Mux(~is_running, false.B,
                            Mux(pre_reg_en_d2_g0, true.B,
                            Mux(~req_csm_sel & csm_reg_en, false.B,
                            req_pre_valid_0_d3)))
    val req_pre_valid_1_w = Mux(~is_running, false.B,
                            Mux(pre_reg_en_d2_g1, true.B,
                            Mux(req_csm_sel & csm_reg_en, false.B,
                            req_pre_valid_1_d3)))
    val req_pre_valid = Mux(~req_csm_sel, req_pre_valid_0_d3, req_pre_valid_1_d3)

    when(~is_running){
        pre_gen_sel := false.B
        req_csm_sel := false.B
    }
    .otherwise{
        when(pre_reg_en_d2){
            pre_gen_sel := pre_gen_sel + 1.U
        }
        when(csm_reg_en){
            req_csm_sel := req_csm_sel + 1.U
        }
    }
    req_pre_valid_0_d3 := req_pre_valid_0_w
    req_pre_valid_1_d3 := req_pre_valid_1_w

    ////////////////////////////////////////////////////////////////////////
    //  generate address for input feature data                           //
    ////////////////////////////////////////////////////////////////////////
    val req_batch_reg_en = Wire(Bool())
    val is_req_batch_end = Wire(Bool())
    val req_batch_cnt = RegInit("b0".asUInt(5.W))

    when(layer_st){
        req_batch_cnt := "b0".asUInt(5.W)
    }
    .otherwise{
        when(req_batch_reg_en){
            when(is_req_batch_end){
                req_batch_cnt := "b0".asUInt(5.W)
            }
            .otherwise{
                req_batch_cnt := req_batch_cnt + 1.U
            }
        }
    }

    is_req_batch_end := (req_batch_cnt === io.reg2dp_batches)

    ///////////// channel counter /////////////
    val req_ch_cnt = RegInit("b0".asUInt(11.W))
    val req_cur_ch = RegInit("b0".asUInt(3.W))
    val req_ch_reg_en = Wire(Bool())
    val is_req_ch_end = Wire(Bool())

    val req_ch_mode = Mux(is_packed_1x1, "h1".asUInt(3.W), conf.ATMM_NUM.U)
    val req_ch_left_w = Mux(layer_st | is_req_ch_end, data_surface_w, data_surface - req_ch_cnt - req_cur_ch)

    when(layer_st){
        req_ch_cnt := "b0".asUInt(11.W)
    }
    .otherwise{
        when(req_ch_reg_en){
            when(is_req_ch_end){
                req_ch_cnt := "b0".asUInt(11.W)
            }
            .otherwise{
                req_ch_cnt := req_ch_cnt + req_cur_ch
            }
        }
    }
    val data_surface_dec = data_surface - req_cur_ch
    is_req_ch_end := (req_ch_cnt === data_surface_dec)
    when(layer_st | req_ch_reg_en){
        when(req_ch_left_w > req_ch_mode){
            req_cur_ch := req_ch_mode
        }
        .otherwise{
            req_cur_ch := req_ch_left_w(2, 0)
        }
    }

    ///////////// atomic counter /////////////
    val req_atm_sel = RegInit("b0".asUInt(2.W))
    val req_atm_cnt_0 = RegInit("b0".asUInt(14.W))
    val req_atm_cnt_1 = RegInit("b0".asUInt(14.W))
    val req_atm_cnt_2 = RegInit("b0".asUInt(14.W))
    val req_atm_cnt_3 = RegInit("b0".asUInt(14.W))
    val req_atm_size = Wire(UInt(4.W))
    val req_atm_reg_en = Wire(Bool())
    val req_atm_reg_en_0 = Wire(Bool())
    val req_atm_reg_en_1 = Wire(Bool())
    val req_atm_reg_en_2 = Wire(Bool())
    val req_atm_reg_en_3 = Wire(Bool())
    val req_addr = Wire(UInt((64 - conf.ATMMBW).W))

    val req_atm = Mux(req_csm_sel, req_atomic_1_d3, req_atomic_0_d3)
    val is_atm_done = Cat((req_atm_cnt_3 === req_atm), (req_atm_cnt_2 === req_atm), (req_atm_cnt_1 === req_atm), (req_atm_cnt_0 === req_atm))
    val is_req_atm_end = ((req_cur_ch === 1.U)&(is_atm_done(0).andR))|
                         ((req_cur_ch === 2.U)&(is_atm_done(1, 0).andR))|
                         ((req_cur_ch === 3.U)&(is_atm_done(2, 0).andR))|
                         ((req_cur_ch === 4.U)&(is_atm_done(3, 0).andR))
    val req_atm_cnt = MuxLookup(req_atm_sel, "b0".asUInt(2.W),
                      Seq(
                          0.U -> req_atm_cnt_0,
                          1.U -> req_atm_cnt_1,
                          2.U -> req_atm_cnt_2,
                          3.U -> req_atm_cnt_3
                      ))
    val req_atm_cnt_inc = req_atm_cnt + req_atm_size

    val cur_atm_done = MuxLookup(req_atm_sel, false.B,
                      Seq(
                          0.U -> is_atm_done(0),
                          1.U -> is_atm_done(1),
                          2.U -> is_atm_done(2),
                          3.U -> is_atm_done(3)
                      ))

    val req_atm_left = req_atm - req_atm_cnt
    val req_atm_size_addr_limit = Mux(req_atm_cnt === 0.U, "h8".asUInt(4.W)-req_addr(2, 0), "h8".asUInt(4.W))
    req_atm_size := Mux(req_atm_left < req_atm_size_addr_limit, req_atm_left(3, 0), req_atm_size_addr_limit)
    val req_atm_size_out = req_atm_size - 1.U
    val req_atm_cnt_0_w = Mux(~is_running | is_req_atm_end, 0.U, req_atm_cnt_inc)
    val req_atm_cnt_1_w = Mux(~is_running | is_req_atm_end, 0.U, req_atm_cnt_inc)
    val req_atm_cnt_2_w = Mux(~is_running | is_req_atm_end, 0.U, req_atm_cnt_inc)
    val req_atm_cnt_3_w = Mux(~is_running | is_req_atm_end, 0.U, req_atm_cnt_inc)
    
    val is_req_atm_sel_end = (req_atm_sel === req_cur_ch-&1.U)
    when(~is_running){
        req_atm_sel := "b0".asUInt(2.W)
    }
    .otherwise{
        when(req_atm_reg_en){
            when(is_req_atm_sel_end | is_req_atm_end){
                req_atm_sel := "b0".asUInt(2.W)
            }
            .otherwise{
                req_atm_sel := req_atm_sel + 1.U
            }
            
        }
    }

    when(layer_st | req_atm_reg_en_0){
        req_atm_cnt_0 := req_atm_cnt_0_w
    }
    when(layer_st | req_atm_reg_en_1){
        req_atm_cnt_1 := req_atm_cnt_1_w
    }    
    when(layer_st | req_atm_reg_en_2){
        req_atm_cnt_2 := req_atm_cnt_2_w
    }
    when(layer_st | req_atm_reg_en_3){
        req_atm_cnt_3 := req_atm_cnt_3_w
    }

    ///////////// address counter /////////////
    val req_addr_grain_base = RegInit("b0".asUInt((64 - conf.ATMMBW).W))
    val req_addr_batch_base = RegInit("b0".asUInt((64 - conf.ATMMBW).W))
    val req_addr_ch_base = RegInit("b0".asUInt((64 - conf.ATMMBW).W))
    val req_addr_base = RegInit("b0".asUInt((64 - conf.ATMMBW).W))

    val req_addr_ori = Cat(io.reg2dp_datain_addr_high_0, io.reg2dp_datain_addr_low_0)
    val req_addr_grain_base_inc = req_addr_grain_base + grain_addr
    val req_addr_batch_base_inc = req_addr_batch_base + io.reg2dp_batch_stride
    val req_addr_ch_base_add = io.reg2dp_surf_stride << conf.KK
    val req_addr_ch_base_inc = req_addr_ch_base + req_addr_ch_base_add
    val req_addr_base_inc = req_addr_base + io.reg2dp_surf_stride
    val req_addr_grain_base_w = Mux(is_first_running,  req_addr_ori,  req_addr_grain_base_inc)
    val req_addr_batch_base_w = Mux(is_first_running, req_addr_ori, 
                                Mux(is_req_batch_end, req_addr_grain_base_inc,
                                req_addr_batch_base_inc))
    val req_addr_ch_base_w = Mux(is_first_running, req_addr_ori, 
                             Mux(is_req_ch_end & is_req_batch_end, req_addr_grain_base_inc,
                             Mux(is_req_ch_end, req_addr_batch_base_inc, 
                             req_addr_ch_base_inc)))
    val req_addr_base_w = Mux(is_first_running, req_addr_ori,
                          Mux(is_req_atm_end & is_req_ch_end & is_req_batch_end, req_addr_grain_base_inc,
                          Mux(is_req_atm_end & is_req_ch_end, req_addr_batch_base_inc,
                          Mux(is_req_atm_end, req_addr_ch_base_inc,
                          Mux(is_req_atm_sel_end, req_addr_ch_base,
                          req_addr_base_inc)))))
    req_addr:= req_addr_base + req_atm_cnt

    when(is_first_running | req_grain_reg_en){
        req_addr_grain_base := req_addr_grain_base_w
        req_addr_batch_base := req_addr_batch_base_w
        req_addr_ch_base := req_addr_ch_base_w
        req_addr_base := req_addr_base_w
    }

    ///////////// request package /////////////
    val req_valid_d1 = RegInit(false.B)
    val req_addr_d1 = RegInit("b0".asUInt((64-conf.ATMMBW).W))
    val cbuf_is_ready = RegInit(false.B)
    val req_size_d1 = RegInit("b0".asUInt(4.W))
    val req_size_out_d1 = RegInit("b0".asUInt(3.W))
    val req_ch_idx_d1 = RegInit("b0".asUInt(2.W))
    val req_reg_en = Wire(Bool())
    val req_ready_d1 = Wire(Bool())

    val req_valid_d0 = is_running & req_pre_valid & cbuf_is_ready & ~cur_atm_done

    when(~is_running){
        req_valid_d1 := false.B
    }
    .elsewhen(req_valid_d0){
        req_valid_d1 := true.B
    }
    .elsewhen(req_ready_d1){
        req_valid_d1 := false.B
    }

    when(req_reg_en){
        req_addr_d1 := req_addr
        req_size_d1 := req_atm_size
        req_size_out_d1 := req_atm_size_out
        req_ch_idx_d1 := req_atm_sel
    }

    ///////////// control logic ///////////// 
    val dma_req_fifo_ready = Wire(Bool())
    val dma_rd_req_rdy = Wire(Bool())

    req_ready_d1 := dma_req_fifo_ready & dma_rd_req_rdy
    val req_ready_d0 = req_ready_d1 | ~req_valid_d1 
    req_reg_en := req_pre_valid & cbuf_is_ready & ~cur_atm_done & req_ready_d0
    req_atm_reg_en := req_pre_valid & cbuf_is_ready & (cur_atm_done | req_ready_d0)
    req_atm_reg_en_0 := req_pre_valid & cbuf_is_ready & (is_req_atm_end | ((req_atm_sel === 0.U) & ~is_atm_done(0) & req_ready_d0))
    req_atm_reg_en_1 := req_pre_valid & cbuf_is_ready & (is_req_atm_end | ((req_atm_sel === 1.U) & ~is_atm_done(1) & req_ready_d0))
    req_atm_reg_en_2 := req_pre_valid & cbuf_is_ready & (is_req_atm_end | ((req_atm_sel === 2.U) & ~is_atm_done(2) & req_ready_d0))
    req_atm_reg_en_3 := req_pre_valid & cbuf_is_ready & (is_req_atm_end | ((req_atm_sel === 3.U) & ~is_atm_done(3) & req_ready_d0))
    //When is_req_atm_end is set, we don't need to wait cbuf_is_ready;
    req_ch_reg_en := req_pre_valid & is_req_atm_end
    req_batch_reg_en := req_pre_valid & is_req_atm_end & is_req_ch_end
    req_grain_reg_en := req_pre_valid & is_req_atm_end & is_req_ch_end & is_req_batch_end

    ////////////////////////////////////////////////////////////////////////
    // CDMA DC read request interface //
    ////////////////////////////////////////////////////////////////////////
    //==============
    // DMA Interface
    //==============
    // rd Channel: Request
    val dma_rd_req_pd = Wire(UInt((conf.NVDLA_CDMA_MEM_RD_REQ).W))
    val dma_rd_req_addr = Wire(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    val dma_rd_req_size = Wire(UInt(15.W))
    val dma_rd_req_vld = Wire(Bool())
    val dma_rd_rsp_rdy = Wire(Bool())

    val nv_NVDLA_PDP_RDMA_rdreq = Module{new NV_NVDLA_DMAIF_rdreq(conf.NVDLA_CDMA_MEM_RD_REQ)}
    nv_NVDLA_PDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
    nv_NVDLA_PDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.reg2dp_datain_ram_type

    nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.valid := dma_rd_req_vld
    dma_rd_req_rdy := nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.ready
    nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.bits := dma_rd_req_pd

    io.dc_dat2mcif_rd_req_pd <> nv_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_pd
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.dc_dat2cvif_rd_req_pd.get <> nv_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_pd.get 
    }
    
    // rd Channel: Response
    val nv_NVDLA_PDP_RDMA_rdrsp = Module{new NV_NVDLA_DMAIF_rdrsp(conf.NVDLA_CDMA_MEM_RD_RSP)}
    nv_NVDLA_PDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk

    nv_NVDLA_PDP_RDMA_rdrsp.io.mcif_rd_rsp_pd <> io.mcif2dc_dat_rd_rsp_pd
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get <> io.cvif2dc_dat_rd_rsp_pd.get
    }
    
    val dma_rd_rsp_pd = nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.bits
    val dma_rd_rsp_vld = nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.valid
    nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.ready := dma_rd_rsp_rdy

    val is_blocking = RegInit(false.B)
    dma_rd_req_pd := Cat(dma_rd_req_size, dma_rd_req_addr)
    dma_rd_req_vld := dma_req_fifo_ready & req_valid_d1
    val dma_rd_req_addr_f = Cat(req_addr_d1, "b0".asUInt(conf.ATMMBW.W))
    dma_rd_req_addr := dma_rd_req_addr_f(conf.NVDLA_MEM_ADDRESS_WIDTH-1, 0)
    dma_rd_req_size := Cat("b0".asUInt(13.W), req_size_out_d1)
    val dma_rd_req_type = io.reg2dp_datain_ram_type
    dma_rd_rsp_rdy := ~is_blocking

    val dma_req_fifo_req = req_valid_d1 & dma_rd_req_rdy
    val dma_req_fifo_data = Cat(req_ch_idx_d1, req_size_d1)
    val dma_rsp_fifo_ready = Wire(Bool())

    val u_fifo = Module{new NV_NVDLA_fifo(depth = 128, width = 6,
                        ram_type = 2, 
                        distant_wr_req = true)}
    u_fifo.io.clk := io.nvdla_core_clk
    u_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_fifo.io.wr_pvld := dma_req_fifo_req
    dma_req_fifo_ready := u_fifo.io.wr_prdy
    u_fifo.io.wr_pd := dma_req_fifo_data
    val dma_rsp_fifo_req = u_fifo.io.rd_pvld
    u_fifo.io.rd_prdy := dma_rsp_fifo_ready
    val dma_rsp_fifo_data = u_fifo.io.rd_pd
    


    ////////////////////////////////////////////////////////////////////////
    //  CDMA DC read response connection                                  //
    ////////////////////////////////////////////////////////////////////////
    val dma_rsp_size_cnt = RegInit("b0".asUInt(4.W))
    
    val dma_rd_rsp_data = dma_rd_rsp_pd(conf.NVDLA_CDMA_DMAIF_BW-1, 0)
    val dma_rd_rsp_mask = dma_rd_rsp_pd(conf.NVDLA_CDMA_MEM_RD_RSP-1, conf.NVDLA_CDMA_DMAIF_BW)
    val dma_rsp_size = dma_rsp_fifo_data(3, 0)
    val dma_rsp_ch_idx = dma_rsp_fifo_data(5, 4)

    val active_atom_num = PopCount(dma_rd_rsp_mask) + 2.U

    val dma_rsp_size_cnt_inc = dma_rsp_size_cnt + active_atom_num

    val dma_rsp_size_cnt_w = Mux(dma_rsp_size_cnt_inc === dma_rsp_size, "b0".asUInt(4.W), dma_rsp_size_cnt_inc)
    dma_rsp_fifo_ready := (dma_rd_rsp_vld & ~is_blocking & (dma_rsp_size_cnt_inc === dma_rsp_size))

    when(dma_rd_rsp_vld & ~is_blocking){
        dma_rsp_size_cnt := dma_rsp_size_cnt_w
    }
    ////////////////////////////////////////////////////////////////////////
    //  DC read data to shared buffer                                     //
    ////////////////////////////////////////////////////////////////////////
    val ch0_p0_wr_addr_cnt = RegInit("b0".asUInt(6.W))
    val ch1_p0_wr_addr_cnt = RegInit("b0".asUInt(6.W))
    val ch2_p0_wr_addr_cnt = RegInit("b0".asUInt(6.W))
    val ch3_p0_wr_addr_cnt = RegInit("b0".asUInt(6.W))

    val is_rsp_ch0 = dma_rsp_fifo_req & (dma_rsp_ch_idx === 0.U)
    val is_rsp_ch1 = dma_rsp_fifo_req & (dma_rsp_ch_idx === 1.U)
    val is_rsp_ch2 = dma_rsp_fifo_req & (dma_rsp_ch_idx === 2.U)
    val is_rsp_ch3 = dma_rsp_fifo_req & (dma_rsp_ch_idx === 3.U)
    val ch0_wr_addr_cnt_reg_en = dma_rd_rsp_vld & ~is_blocking & is_running & is_rsp_ch0
    val ch1_wr_addr_cnt_reg_en = dma_rd_rsp_vld & ~is_blocking & is_running & is_rsp_ch1
    val ch2_wr_addr_cnt_reg_en = dma_rd_rsp_vld & ~is_blocking & is_running & is_rsp_ch2
    val ch3_wr_addr_cnt_reg_en = dma_rd_rsp_vld & ~is_blocking & is_running & is_rsp_ch3

    when(layer_st){
        ch0_p0_wr_addr_cnt := "b0".asUInt(6.W)
    }
    .otherwise{
        when(ch0_wr_addr_cnt_reg_en){
            ch0_p0_wr_addr_cnt := ch0_p0_wr_addr_cnt + active_atom_num
        }
        when(ch1_wr_addr_cnt_reg_en){
            ch1_p0_wr_addr_cnt := ch1_p0_wr_addr_cnt + active_atom_num
        }
        when(ch2_wr_addr_cnt_reg_en){
            ch2_p0_wr_addr_cnt := ch2_p0_wr_addr_cnt + active_atom_num
        }
        when(ch3_wr_addr_cnt_reg_en){
            ch3_p0_wr_addr_cnt := ch3_p0_wr_addr_cnt + active_atom_num 
        }
    }


    val ch0_p0_wr_addr = Cat("h0".asUInt(2.W), ch0_p0_wr_addr_cnt(0), ch0_p0_wr_addr_cnt(5, 1))
    val ch1_p0_wr_addr = Cat("h0".asUInt(2.W), ch1_p0_wr_addr_cnt(0), ch1_p0_wr_addr_cnt(5, 1))
    val ch2_p0_wr_addr = Cat("h0".asUInt(2.W), ch2_p0_wr_addr_cnt(0), ch2_p0_wr_addr_cnt(5, 1))
    val ch3_p0_wr_addr = Cat("h0".asUInt(2.W), ch3_p0_wr_addr_cnt(0), ch3_p0_wr_addr_cnt(5, 1))

    ////////////////////////////////////////////////////////////////////////
    //  Shared buffer write signals                                       //
    ////////////////////////////////////////////////////////////////////////

    val p0_wr_en = is_running & dma_rd_rsp_vld & ~is_blocking
    val p0_wr_addr = (Fill(8, p0_wr_en) & is_rsp_ch0) & ch0_p0_wr_addr|
                     (Fill(8, p0_wr_en) & is_rsp_ch1) & ch1_p0_wr_addr|
                     (Fill(8, p0_wr_en) & is_rsp_ch2) & ch2_p0_wr_addr|
                     (Fill(8, p0_wr_en) & is_rsp_ch3) & ch3_p0_wr_addr
    
    io.dc2sbuf_p0_wr.addr.valid := p0_wr_en
    io.dc2sbuf_p0_wr.addr.bits := p0_wr_addr
    io.dc2sbuf_p0_wr.data := dma_rd_rsp_data(conf.ATMM-1, 0)


    ////////////////////////////////////////////////////////////////////////
    //  DC local buffer count                                             //
    ////////////////////////////////////////////////////////////////////////
    val rsp_ch0_rd_size = Wire(UInt(3.W))
    val ch0_rd_addr_cnt_reg_en = Wire(Bool())
    val ch0_cnt = RegInit("b0".asUInt(5.W))

    val ch0_cnt_add = Mux(ch0_wr_addr_cnt_reg_en, active_atom_num, "h0".asUInt(2.W))
    val ch0_cnt_sub = Mux(ch0_wr_addr_cnt_reg_en, rsp_ch0_rd_size, "h0".asUInt(3.W))

    when(layer_st){
        ch0_cnt := "b0".asUInt(5.W)
    }
    .otherwise{
        when(ch0_wr_addr_cnt_reg_en | ch0_rd_addr_cnt_reg_en){
            ch0_cnt := ch0_cnt + ch0_cnt_add - ch0_cnt_sub
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // DC response data counter---DC reading from Sbuf //
    ////////////////////////////////////////////////////////////////////////
    ///////////// all height counter /////////////
    val rsp_all_h_cnt = RegInit("b0".asUInt(14.W))
    val rsp_cur_grain = RegInit("b0".asUInt(13.W))
    val rsp_all_h_reg_en = Wire(Bool())

    val rsp_all_h_cnt_inc =  rsp_all_h_cnt + rsp_cur_grain;
    val rsp_all_h_left_w = Mux(layer_st, data_height_w, data_height - rsp_all_h_cnt_inc)
    val rsp_cur_grain_w = Mux(rsp_all_h_left_w > fetch_grain_w, fetch_grain_w, rsp_all_h_left_w(12, 0))
    val is_rsp_all_h_end = (rsp_all_h_cnt_inc === data_height)
    is_rsp_done := ~io.reg2dp_op_en | (rsp_all_h_cnt === data_height)

    when(layer_st){
        rsp_all_h_cnt := "b0".asUInt(14.W)      
    }
    .otherwise{
        when(rsp_all_h_reg_en){
            rsp_all_h_cnt := rsp_all_h_cnt_inc
        }
        when(layer_st | rsp_all_h_reg_en){
            rsp_cur_grain := rsp_cur_grain_w
        }
    }

    ///////////// batch counter /////////////
    val rsp_batch_cnt = RegInit("b0".asUInt(5.W))
    val is_rsp_batch_end = (rsp_batch_cnt === io.reg2dp_batches)
    val rsp_batch_reg_en = Wire(Bool())

    when(layer_st){
        rsp_batch_cnt := "b0".asUInt(5.W)
    }
    .elsewhen(rsp_batch_reg_en){
        when(is_rsp_batch_end){
            rsp_batch_cnt := "b0".asUInt(5.W)
        }
        .otherwise{
            rsp_batch_cnt := rsp_batch_cnt + 1.U
        }
    }

    ///////////// channel counter /////////////
    val rsp_ch_cnt = RegInit("b0".asUInt(11.W)) 
    val rsp_cur_ch = RegInit("b0".asUInt(3.W)) 
    val rsp_ch_reg_en = Wire(Bool())
    val is_rsp_ch_end = Wire(Bool())
    
    val rsp_ch_mode = req_ch_mode
    val rsp_ch_cnt_inc = rsp_ch_cnt + rsp_cur_ch
    val rsp_ch_left_w = Mux(layer_st | is_rsp_ch_end, data_surface_w, data_surface - rsp_ch_cnt_inc)
    val rsp_cur_ch_w = Mux(rsp_ch_left_w > rsp_ch_mode, rsp_ch_mode, rsp_ch_left_w(2, 0))
    val data_surface_dec_1 = data_surface - rsp_cur_ch
    is_rsp_ch_end := rsp_ch_cnt === data_surface_dec_1

    when(layer_st){
        rsp_ch_cnt := "b0".asUInt(11.W)
    }
    .elsewhen(rsp_ch_reg_en){
        when(is_rsp_ch_end){
            rsp_ch_cnt := "b0".asUInt(11.W)
        }
        .otherwise{
            rsp_ch_cnt := rsp_ch_cnt + rsp_cur_ch
        }
    }

    when(layer_st | rsp_ch_reg_en){
        rsp_cur_ch := rsp_cur_ch_w
    }

    ///////////// height counter /////////////
    val rsp_h_cnt = RegInit("b0".asUInt(12.W))
    val rsp_h_reg_en = Wire(Bool())
    val is_rsp_h_end = Wire(Bool())

    when(layer_st){
        rsp_h_cnt := "b0".asUInt(12.W)
    }
    .elsewhen(rsp_h_reg_en){
        when(is_rsp_h_end){
            rsp_h_cnt := "b0".asUInt(12.W)
        }
        .otherwise{
            rsp_h_cnt := rsp_h_cnt + 1.U
        }
    }

    is_rsp_h_end := (rsp_h_cnt === rsp_cur_grain-1.U)

    ///////////// width counter /////////////
    val rsp_w_cnt = RegInit("b0".asUInt(16.W))
    val rsp_w_cnt_add = Wire(UInt(3.W))
    val rsp_w_reg_en = Wire(Bool())
    val is_rsp_w_end = Wire(Bool())
    val rsp_w_left1 = (rsp_w_cnt === data_width_sub_one)

    when(layer_st){
        rsp_w_cnt := "b0".asUInt(16.W)
    }
    .elsewhen(rsp_w_reg_en){
        when(is_rsp_w_end){
            rsp_w_cnt := "b0".asUInt(16.W)
        }
        .otherwise{
            rsp_w_cnt := rsp_w_cnt + rsp_w_cnt_add
        }
    }

    val width_dec = data_width - rsp_w_cnt_add
    is_rsp_w_end := (rsp_w_cnt === width_dec)

    ///////////// response control signal /////////////
    val rsp_rd_en = Wire(Bool())
    val rsp_ch0_rd_one = ~(rsp_cur_ch === 1.U) |
                           rsp_w_left1 |
                          (is_data_normal & rsp_ch_cnt(1)) 

    val rsp_rd_more_atmm = Wire(UInt(3.W))
    if(conf.ATMM_NUM == 1){
        rsp_rd_more_atmm := 0.U
    }

    rsp_ch0_rd_size := rsp_w_cnt_add
    rsp_w_reg_en := rsp_rd_en
    rsp_h_reg_en := rsp_w_reg_en & is_rsp_w_end
    rsp_ch_reg_en := rsp_h_reg_en & is_rsp_h_end
    rsp_batch_reg_en := rsp_ch_reg_en & is_rsp_ch_end
    rsp_all_h_reg_en := rsp_batch_reg_en & is_rsp_batch_end

    ////////////////////////////////////////////////////////////////////////
    //  generate shared buffer rd signals                                 //
    ////////////////////////////////////////////////////////////////////////
    ///////////// read enable signal /////////////
    val ch0_aval = ch0_cnt.orR

    when(~is_rsp_done & is_running){
        rsp_rd_en := ch0_aval
        rsp_w_cnt_add := "d1".asUInt(3.W)
    }
    .otherwise{
        rsp_rd_en := false.B
        rsp_w_cnt_add := "d0".asUInt(3.W)
    }

    val p0_rd_en_w = rsp_rd_en
    ///////////// channel address counter /////////////

    val ch0_p0_rd_addr_cnt = RegInit("b0".asUInt(6.W))
    val rsp_rd_ch2ch3 = RegInit(false.B)

    ch0_rd_addr_cnt_reg_en := rsp_rd_en & ~rsp_rd_ch2ch3

    when(layer_st){
        ch0_p0_rd_addr_cnt := "b0".asUInt(6.W)
        rsp_rd_ch2ch3 := false.B
        when(rsp_rd_en){
            when(rsp_cur_ch <= 2.U){
                rsp_rd_ch2ch3 := false.B
            }
            .otherwise{
                rsp_rd_ch2ch3 := ~rsp_rd_ch2ch3
            }
        }

    }
    .otherwise{
        when(ch0_rd_addr_cnt_reg_en){
            ch0_p0_rd_addr_cnt := ch0_p0_rd_addr_cnt + rsp_ch0_rd_size
        }
    }

    val ch0_p0_rd_addr = Cat("h0".asUInt(2.W), ch0_p0_rd_addr_cnt(0), ch0_p0_rd_addr_cnt(5, 1))

    val p0_rd_addr_w = ch0_p0_rd_addr
    ///////////// shared buffer read address /////////////
    when(~is_running | layer_st){
        is_blocking := false.B
    }
    .otherwise{
        is_blocking := ~is_blocking & rsp_rd_en & rsp_ch0_rd_one
    }

    ///////////// output to shared buffer /////////////
    io.dc2sbuf_p0_rd.addr.valid := RegNext(p0_rd_en_w, false.B)
    io.dc2sbuf_p0_rd.addr.bits := RegEnable(p0_rd_addr_w, "b0".asUInt(8.W), p0_rd_en_w)

    ////////////////////////////////////////////////////////////////////////
    //  generate write signal to convertor                                //
    //////////////////////////////////////////////////////////////////////// 
    val idx_ch_offset = RegInit("b0".asUInt(18.W))
    val idx_batch_offset = RegInit("b0".asUInt(18.W))
    val idx_h_offset = RegInit("b0".asUInt(18.W))
    val idx_ch_offset_w = Wire(UInt(18.W))
    val idx_batch_offset_w = Wire(UInt(18.W))
    val is_w_cnt_div4 = WireInit(false.B)
    val is_w_cnt_div2 = WireInit(false.B)
    val cbuf_wr_hsel_w = if(conf.ATMC/conf.ATMM == 2) Some((is_w_cnt_div2 & rsp_w_cnt(0)) | (is_data_normal & rsp_ch_cnt(0))) else None
    if(conf.ATMC/conf.ATMM == 1){
        idx_ch_offset_w := Mux(layer_st, "b0".asUInt(18.W), 
                             Mux(is_rsp_ch_end, idx_batch_offset_w,
                             idx_ch_offset + data_width))
    }
    else if(conf.ATMC/conf.ATMM == 2){
        idx_ch_offset_w := Mux(layer_st, "b0".asUInt(18.W), 
                              Mux(is_rsp_ch_end, idx_batch_offset_w,
                              Mux(rsp_ch_cnt(0), idx_ch_offset + data_width, 
                              idx_ch_offset)))     
        is_w_cnt_div2 := (is_data_normal & is_rsp_ch_end & ~rsp_ch_cnt(0))
    }

    idx_batch_offset_w := Mux(layer_st | is_rsp_batch_end, "b0".asUInt(18.W), idx_batch_offset + data_entries)
    val idx_h_offset_w = Mux(layer_st, 0.U,
                         Mux(is_rsp_h_end, idx_ch_offset_w,
                         Mux(is_rsp_all_h_end, idx_h_offset + rsp_batch_entry_last,
                         idx_h_offset + rsp_batch_entry_init)))
    val idx_base = RegInit("b0".asUInt(15.W))
    val idx_grain_offset = RegInit("b0".asUInt(18.W))
    val cbuf_wr_en = RegInit(false.B)
    
    val idx_w_offset_add = Mux(is_w_cnt_div4, rsp_w_cnt(15, 2), 
                           Mux(is_w_cnt_div2, rsp_w_cnt(15, 1),
                           rsp_w_cnt(14, 0)))
    val cbuf_idx_inc = idx_base + (idx_grain_offset + idx_h_offset) + idx_w_offset_add
    val is_cbuf_idx_wrap = cbuf_idx_inc >= Cat(data_bank, "b0".asUInt(log2Ceil(conf.NVDLA_CBUF_BANK_DEPTH).W))
    val cbuf_idx_w = Mux(~is_cbuf_idx_wrap, cbuf_idx_inc(14, 0),
                     cbuf_idx_inc - Cat(data_bank, "b0".asUInt(log2Ceil(conf.NVDLA_CBUF_BANK_DEPTH).W)))
    val rsp_entry = Wire(UInt(18.W))

    when(is_first_running){
        idx_base := io.status2dma_wr_idx
        idx_grain_offset := 0.U
    }
    when(layer_st | rsp_batch_reg_en){
        idx_batch_offset := idx_batch_offset_w
        idx_ch_offset := idx_ch_offset_w
        idx_h_offset := idx_h_offset_w
    }
    when(rsp_all_h_reg_en){
        idx_grain_offset := idx_grain_offset + rsp_entry
    }
    cbuf_wr_en := rsp_rd_en

    val cbuf_wr_addr_0 = RegInit("b0".asUInt(17.W))
    when(rsp_w_reg_en){
        cbuf_wr_addr_0 := cbuf_idx_w
    }
    val cbuf_wr_hsel = if(conf.DMAIF < conf.ATMC) Some(RegEnable(cbuf_wr_hsel_w.get, false.B, rsp_w_reg_en)) else None
    val cbuf_wr_info_mask = Cat("b0".asUInt(3.W), p0_rd_en_w)

    val cbuf_wr_info_pd = Cat("d0".asUInt(3.W), //cbuf_wr_info_sub_h[2:0];
                              false.B,  //cbuf_wr_info_uint ;
                              false.B,  //cbuf_wr_info_mean ;
                              false.B,  //cbuf_wr_info_ext128 ;
                              false.B,  //cbuf_wr_info_ext64 ;
                              false.B,  //cbuf_wr_info_interleave ;
                              cbuf_wr_info_mask(3, 0))

    ////////////////////////////////////////////////////////////////////////
    // pipeline to sync the sbuf read to output to convertor //
    ////////////////////////////////////////////////////////////////////////
    if(conf.DMAIF < conf.ATMC){
        io.dc2cvt_dat_wr_sel.get := ShiftRegister(cbuf_wr_hsel.get, conf.CDMA_SBUF_RD_LATENCY+1, false.B, cbuf_wr_en)
    }
    io.dc2cvt_dat_wr.addr.valid := ShiftRegister(cbuf_wr_en, conf.CDMA_SBUF_RD_LATENCY+1, false.B, true.B)
    io.dc2cvt_dat_wr.addr.bits := ShiftRegister(cbuf_wr_addr_0, conf.CDMA_SBUF_RD_LATENCY+1, "b0".asUInt(17.W), cbuf_wr_en)
    if(conf.ATMM_NUM == 1){
        io.dc2cvt_dat_wr.data := ShiftRegister(io.dc2sbuf_p0_rd.data, conf.CDMA_SBUF_RD_LATENCY+1, "b0".asUInt(conf.NVDLA_CDMA_DMAIF_BW.W), cbuf_wr_en)
    }
    io.dc2cvt_dat_wr_info_pd := ShiftRegister(cbuf_wr_info_pd, conf.CDMA_SBUF_RD_LATENCY+1, "b0".asUInt(12.W), cbuf_wr_en)
    ////////////////////////////////////////////////////////////////////////
    //  convolution buffer slices & entries management                    //
    ////////////////////////////////////////////////////////////////////////
    val dc_entry_onfly = RegInit("b0".asUInt(15.W))

    val req_entry = Mux(req_csm_sel, req_entry_1_d3(14, 0), req_entry_0_d3(14, 0))
    rsp_entry := Mux(is_rsp_all_h_end, rsp_entry_last, rsp_entry_init)
    val dc_entry_onfly_add = Mux( ~req_grain_reg_en, "b0".asUInt(15.W), req_entry)
    val dc_entry_onfly_sub = Mux( ~io.dc2status_dat_updt.valid, "b0".asUInt(15.W), io.dc2status_dat_updt.bits.entries) 

    when(req_grain_reg_en | io.dc2status_dat_updt.valid){
        dc_entry_onfly := dc_entry_onfly + dc_entry_onfly_add - dc_entry_onfly_sub
    }

    ///////////// calculate if free entries is enough /////////////  
    val required_entries = dc_entry_onfly +& req_entry
    val is_free_entries_enough = required_entries <= io.status2dma_free_entries
    val rsp_slice = Mux(is_rsp_all_h_end, rsp_slice_last, rsp_slice_init)

    cbuf_is_ready := Mux((~is_running | ~req_pre_valid | csm_reg_en) , false.B, is_free_entries_enough)

    ///////////// update CDMA data status /////////////
    io.dc2status_dat_updt.valid := ShiftRegister(rsp_all_h_reg_en, conf.CDMA_SBUF_RD_LATENCY + 1, false.B, true.B)
    io.dc2status_dat_updt.bits.entries := ShiftRegister(rsp_entry, conf.CDMA_SBUF_RD_LATENCY + 1, "b0".asUInt(15.W), rsp_all_h_reg_en)
    io.dc2status_dat_updt.bits.slices := ShiftRegister(rsp_slice, conf.CDMA_SBUF_RD_LATENCY + 1, "b0".asUInt(14.W), rsp_all_h_reg_en)

    ////////////////////////////////////////////////////////////////////////
    // performance counting register //
    ////////////////////////////////////////////////////////////////////////
    //stall
    val dc_rd_stall_inc = RegNext(dma_rd_req_vld & ~dma_rd_req_rdy & io.reg2dp_dma_en, false.B)
    val dc_rd_stall_clr = RegNext(io.status2dma_fsm_switch & io.reg2dp_dma_en, false.B)
    val dc_rd_stall_cen = RegNext(io.reg2dp_op_en & io.reg2dp_dma_en, false.B)

    val dp2reg_dc_rd_stall_dec = false.B

    val stl = Module(new NV_COUNTER_STAGE(32))
    stl.io.clk := io.nvdla_core_clk
    stl.io.rd_stall_inc := dc_rd_stall_inc
    stl.io.rd_stall_dec := dp2reg_dc_rd_stall_dec
    stl.io.rd_stall_clr := dc_rd_stall_clr
    stl.io.rd_stall_cen := dc_rd_stall_cen
    io.dp2reg_dc_rd_stall := stl.io.cnt_cur

    //latency_1
    val dc_rd_latency_inc = RegNext(dma_rd_req_vld & ~dma_rd_req_rdy & io.reg2dp_dma_en, false.B)
    val dc_rd_latency_dec = RegNext(dma_rsp_fifo_ready & io.reg2dp_dma_en, false.B)
    val dc_rd_latency_clr = RegNext(io.status2dma_fsm_switch, false.B)
    val dc_rd_latency_cen = RegNext(io.reg2dp_op_en & io.reg2dp_dma_en, false.B)

    val outs_dp2reg_dc_rd_latency = Wire(UInt(9.W))
    val ltc_1_inc = (outs_dp2reg_dc_rd_latency =/= 511.U) & dc_rd_latency_inc
    val ltc_1_dec = (outs_dp2reg_dc_rd_latency =/= 511.U) & dc_rd_latency_dec

    val ltc_1 = Module(new NV_COUNTER_STAGE(9))
    ltc_1.io.clk := io.nvdla_core_clk
    ltc_1.io.rd_stall_inc := ltc_1_inc
    ltc_1.io.rd_stall_dec := ltc_1_dec
    ltc_1.io.rd_stall_clr := dc_rd_latency_clr
    ltc_1.io.rd_stall_cen := dc_rd_latency_cen
    outs_dp2reg_dc_rd_latency := ltc_1.io.cnt_cur

    //latency_2
    val ltc_2_inc = (~io.dp2reg_dc_rd_latency.andR) & (outs_dp2reg_dc_rd_latency.orR)
    val ltc_2_dec = false.B

    val ltc_2 = Module(new NV_COUNTER_STAGE(32))
    ltc_2.io.clk := io.nvdla_core_clk
    ltc_2.io.rd_stall_inc := ltc_2_inc
    ltc_2.io.rd_stall_dec := ltc_2_dec
    ltc_2.io.rd_stall_clr := dc_rd_latency_clr
    ltc_2.io.rd_stall_cen := dc_rd_latency_cen
    io.dp2reg_dc_rd_latency := ltc_2.io.cnt_cur

}}



object NV_NVDLA_CDMA_dcDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_dc())
}
