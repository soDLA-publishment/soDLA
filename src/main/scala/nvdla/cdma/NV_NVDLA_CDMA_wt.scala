package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_CDMA_wtIO(implicit conf: nvdlaConfig) extends Bundle {

    //nvdla core clock
    val nvdla_core_clk = Input(Clock())
    val nvdla_core_ng_clk = Input(Clock())
    val pwrbus_ram_pd = Input(UInt(32.W))

    //mcif
    val cdma_wt2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val mcif2cdma_wt_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))

    //cvif
    val cdma_wt2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
    val cvif2cdma_wt_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))) else None

    val cdma2buf_wt_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Output(UInt((conf.ATMC/conf.DMAIF).W))) else None
    val cdma2buf_wt_wr = new nvdla_wr_if(17, conf.DMAIF)

    val status2dma_fsm_switch = Input(Bool())
    val wt2status_state = Output(UInt(2.W))

    val cdma2sc_wt_updt = ValidIO(new updt_entries_kernels_if)
    val cdma2sc_wt_pending_ack = Output(Bool())
    val sc2cdma_wt_updt = Flipped(ValidIO(new updt_entries_kernels_if))
    val sc2cdma_wt_pending_req = Input(Bool())

    val reg2dp_arb_weight = Input(UInt(4.W))
    val reg2dp_arb_wmb = Input(UInt(4.W))
    val reg2dp_op_en = Input(Bool())
    val reg2dp_proc_precision = Input(UInt(2.W))
    val reg2dp_weight_reuse = Input(Bool())
    val reg2dp_skip_weight_rls = Input(Bool())
    val reg2dp_weight_format = Input(Bool())
    val reg2dp_byte_per_kernel = Input(UInt(18.W))
    val reg2dp_weight_kernel = Input(UInt(13.W))
    val reg2dp_weight_ram_type = Input(Bool())
    val reg2dp_weight_addr_low = Input(UInt((32-conf.ATMMBW).W))
    val reg2dp_wgs_addr_low = Input(UInt((32-conf.ATMMBW).W))
    val reg2dp_wmb_addr_low = Input(UInt((32-conf.ATMMBW).W))
    val reg2dp_weight_addr_high = Input(UInt(32.W))
    val reg2dp_weight_bytes = Input(UInt(32.W))
    val reg2dp_wgs_addr_high = Input(UInt(32.W))
    val reg2dp_wmb_addr_high = Input(UInt(32.W))
    val reg2dp_wmb_bytes = Input(UInt(28.W))
    val reg2dp_data_bank = Input(UInt(5.W))
    val reg2dp_weight_bank = Input(UInt(5.W))
    val reg2dp_nan_to_zero = Input(Bool())
    val reg2dp_dma_en = Input(Bool())

    val dp2reg_nan_weight_num = Output(UInt(32.W))
    val dp2reg_inf_weight_num = Output(UInt(32.W))
    val dp2reg_wt_flush_done = Output(Bool())
    val dp2reg_wt_rd_stall = Output(UInt(32.W))

}



class NV_NVDLA_CDMA_wt(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new NV_NVDLA_CDMA_wtIO)
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
// CDMA weight fetching logic FSM                                     //
////////////////////////////////////////////////////////////////////////
    val need_pending = Wire(Bool())
    val pending_req_end = Wire(Bool())
    val fetch_done = Wire(Bool())
    val last_skip_weight_rls = RegInit(false.B)
    val sIdle :: sPend :: sBusy :: sDone :: Nil = Enum(4)
    val cur_state = RegInit(sIdle)
    val nxt_state = WireInit(sIdle)

    switch (cur_state) {
        is (sIdle) {
        when (io.reg2dp_op_en & need_pending) { nxt_state := sPend }
        .elsewhen (io.reg2dp_op_en & io.reg2dp_weight_reuse & last_skip_weight_rls) { nxt_state := sDone }
        .elsewhen (io.reg2dp_op_en) { nxt_state := sBusy }
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
    val last_data_bank = RegInit(Fill(5, true.B))
    val last_weight_bank = RegInit(Fill(5, true.B))
    val is_running = Wire(Bool())
    val layer_st = Wire(Bool())
    val status_done = RegInit(false.B)
    val status_done_cnt = RegInit("b0".asUInt(4.W))

    val status_done_cnt_w = Mux(layer_st, "b0".asUInt(4.W), 
                            Mux(status_done & (status_done_cnt =/= "h8".asUInt(4.W)), status_done_cnt + 1.U,
                            status_done_cnt))
    fetch_done := status_done & (status_done_cnt === "h8".asUInt(4.W))
    need_pending := ((last_data_bank =/= io.reg2dp_data_bank) | (last_weight_bank =/= io.reg2dp_weight_bank))

    when(layer_st | is_running){
        status_done_cnt := status_done_cnt_w
    }
////////////////////////////////////////////////////////////////////////
//  FSM output signals                                                //
////////////////////////////////////////////////////////////////////////
    val wt2status_state_out = RegInit("b0".asUInt(2.W))
    val pending_req = RegInit(false.B)
    val pending_req_d1 = RegInit(false.B)
    val pending_ack = RegInit(false.B)

    layer_st := io.reg2dp_op_en && (cur_state === sIdle)
    val layer_end = io.status2dma_fsm_switch
    is_running := (cur_state === sBusy)
    val is_pending = (cur_state === sPend)
    val clear_all = pending_ack & pending_req
    val is_nxt_running = (nxt_state === sBusy)
    val wt2status_state_w = nxt_state
    pending_req_end := pending_req_d1 & ~pending_req

    wt2status_state_out := wt2status_state_w
    pending_req := io.sc2cdma_wt_pending_req
    pending_req_d1 := pending_req
    pending_ack := is_pending

    io.wt2status_state := wt2status_state_out
    io.cdma2sc_wt_pending_ack := pending_ack

////////////////////////////////////////////////////////////////////////
//  registers to keep last layer status                               //
////////////////////////////////////////////////////////////////////////
    when(layer_end){
        last_data_bank := io.reg2dp_data_bank
        last_weight_bank := io.reg2dp_weight_bank
        last_skip_weight_rls := io.reg2dp_skip_weight_rls
    }

    val layer_st_d1 = RegNext(layer_st, false.B)
////////////////////////////////////////////////////////////////////////
//  registers to calculate local values                               //
////////////////////////////////////////////////////////////////////////
    val group = RegInit(Fill(12, true.B))
    val weight_bank = RegInit(Fill(6, true.B))
    val weight_bank_end = RegInit(Fill(7, true.B))
    val nan_pass = RegInit(true.B)

    val byte_per_kernel = RegEnable(io.reg2dp_byte_per_kernel +& 1.U, layer_st)
    val is_int8 = true.B
    val is_fp16 = false.B
    val group_op = io.reg2dp_weight_kernel(12, conf.ATMKBW)
    val group_w = group_op +& 1.U
    val data_bank_w = io.reg2dp_data_bank +& 1.U
    val weight_bank_w = io.reg2dp_weight_bank +& 1.U
    val weight_bank_end_w = weight_bank_w +& data_bank_w
    val nan_pass_w = ~io.reg2dp_nan_to_zero | ~is_fp16
    val is_compressed = false.B

    when(layer_st){
        group := group_w
        weight_bank := weight_bank_w
        weight_bank_end := weight_bank_end_w
        nan_pass := nan_pass_w
    }
////////////////////////////////////////////////////////////////////////
//  generate address for weight data                                  //
////////////////////////////////////////////////////////////////////////
    val src_id_wt = "b00".asUInt(2.W)
    val src_id_wmb = "b01".asUInt(2.W)
    val src_id_wgs = "b10".asUInt(2.W)
    /////////////////// stage 1 ///////////////////
    val wt_req_size_d1 = RegInit("b0".asUInt(4.W))
    val wt_req_burst_cnt_d1 = RegInit("b0".asUInt(29.W))
    val wt_req_stage_vld_d1 = RegInit(false.B)
    val wt_req_reg_en = Wire(Bool())

    val wt_req_reg_en_d0 = wt_req_reg_en
    val wt_req_burst_cnt_dec = wt_req_burst_cnt_d1 - wt_req_size_d1
    val wt_req_burst_cnt_w = Mux(layer_st, io.reg2dp_weight_bytes(31, conf.ATMMBW), wt_req_burst_cnt_dec)
    val wt_req_size_addr_limit = Mux(layer_st, "h8".asUInt(4.W) - io.reg2dp_weight_addr_low(2, 0), "h8".asUInt(4.W))
    val wt_req_size_w = Mux(wt_req_size_addr_limit > wt_req_burst_cnt_w, wt_req_burst_cnt_w(3, 0), wt_req_size_addr_limit)

    when(wt_req_reg_en_d0){
        wt_req_size_d1 := wt_req_size_w
        wt_req_burst_cnt_d1 := wt_req_burst_cnt_w
    }

    wt_req_stage_vld_d1 := is_nxt_running
/////////////////// stage 2 ///////////////////
    val wt_req_addr_d2 = RegInit("b0".asUInt((64 - conf.ATMMBW).W))
    val wt_req_size_d2 = RegInit("b0".asUInt(4.W))
    val wt_req_size_out_d2 = RegInit("b0".asUInt(3.W))
    val wt_req_last_d2 = RegInit(false.B)
    val wt_req_done_d2 = RegInit(true.B)
    val wt_req_stage_vld_d2 = RegInit(false.B)

    val wt_req_reg_en_d1 = wt_req_reg_en
    val wt_req_last_w = wt_req_stage_vld_d1 && (wt_req_burst_cnt_d1 === wt_req_size_d1)
    val wt_req_addr_inc = wt_req_addr_d2(63-conf.ATMMBW, 3) +& 1.U
    val wt_req_addr_w = Mux(~wt_req_stage_vld_d2, Cat(io.reg2dp_weight_addr_high, io.reg2dp_weight_addr_low), Cat(wt_req_addr_inc, "b0".asUInt(3.W)))
    val wt_req_size_out_w = wt_req_size_d1(2, 0) -& 1.U
    val wt_req_done_w = Mux(layer_st, false.B, Mux(wt_req_last_d2, true.B, wt_req_done_d2))

    when(wt_req_reg_en_d1){
        wt_req_addr_d2 := wt_req_addr_w
        wt_req_size_d2 := wt_req_size_d1
        wt_req_size_out_d2 := wt_req_size_out_w
        wt_req_last_d2 := wt_req_last_w
        wt_req_done_d2 := wt_req_done_w
    }
    wt_req_stage_vld_d2 := wt_req_stage_vld_d1 & is_nxt_running
/////////////////// stage 3 ///////////////////
    val wt_req_vld_d3 = RegInit(false.B)
    val wt_req_addr_d3 = RegInit("b0".asUInt((64 - conf.ATMMBW).W))
    val wt_req_size_d3 = RegInit("b0".asUInt(4.W))
    val wt_req_size_out_d3 = RegInit("b0".asUInt(3.W))
    val wt_req_done_d3 = RegInit(true.B)

    val wt_req_reg_en_d2 = wt_req_reg_en
    val wt_req_vld_w = is_nxt_running & wt_req_stage_vld_d2

     wt_req_vld_d3 := wt_req_vld_w
     when(wt_req_reg_en_d2){
         wt_req_addr_d3 := wt_req_addr_d2
         wt_req_size_d3 := wt_req_size_d2
         wt_req_size_out_d3 := wt_req_size_out_d2
         wt_req_done_d3 := is_running & wt_req_done_d2
     }

    val wt_req_src_d3 = src_id_wt

/////////////////// overflow control logic ///////////////////
    val wt_data_onfly = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(14.W))}
    val wt_data_stored = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(17.W))}
    val wt_data_avl = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(17.W))}
    val wt_req_sum = wt_data_onfly + wt_data_stored + wt_data_avl
    val wt_req_overflow = is_running && (wt_req_sum > (Cat(weight_bank, "b0".asUInt(conf.CBUF_BANK_FETCH_BITS.W)) +& conf.ATMM8.U))
    val wt_req_overflow_d3 = wt_req_overflow

/////////////////// pipeline control logic ///////////////////
    val wt_req_rdy = Wire(Bool())
    wt_req_reg_en := layer_st | (is_running & (~wt_req_vld_d3 | wt_req_rdy))
///////////////////////////// connect to dma ////////////////////////////////
    val dma_rd_req_rdy = Wire(Bool())
    val dma_req_fifo_ready = Wire(Bool())
    val arb_sp_out_rdy = dma_rd_req_rdy & dma_req_fifo_ready
    val arb_sp_out_vld = wt_req_vld_d3 & ~wt_req_overflow_d3 & ~wt_req_done_d3
    wt_req_rdy := arb_sp_out_rdy & arb_sp_out_vld
    val dma_req_src = wt_req_src_d3
    val dma_req_size = wt_req_size_d3
    val dma_req_size_out = wt_req_size_out_d3
    val dma_req_addr = wt_req_addr_d3

////////////////////////////////////////////////////////////////////////
//  CDMA WT read request interface                                    //
////////////////////////////////////////////////////////////////////////
//==============
// DMA Interface
//==============
// rd Channel: Request
    val dma_rd_req_pd = Wire(UInt((conf.NVDLA_CDMA_MEM_RD_REQ).W))
    val dma_rd_req_addr = Wire(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    val dma_rd_req_size = Wire(UInt(15.W))
    val dma_rd_req_vld = Wire(Bool())

    val nv_NVDLA_PDP_RDMA_rdreq = Module{new NV_NVDLA_DMAIF_rdreq(conf.NVDLA_CDMA_MEM_RD_REQ)}
    nv_NVDLA_PDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
    nv_NVDLA_PDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.reg2dp_weight_ram_type
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdma_wt2cvif_rd_req_pd.get <> nv_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_pd.get
    }
    io.cdma_wt2mcif_rd_req_pd <> nv_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_pd

    nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.bits := dma_rd_req_pd
    nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.valid := dma_rd_req_vld
    dma_rd_req_rdy := nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd.ready
    // rd Channel: Response
    val nv_NVDLA_PDP_RDMA_rdrsp = Module{new NV_NVDLA_DMAIF_rdrsp(conf.NVDLA_CDMA_MEM_RD_RSP)}
    nv_NVDLA_PDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get <> io.cvif2cdma_wt_rd_rsp_pd.get
    }
    nv_NVDLA_PDP_RDMA_rdrsp.io.mcif_rd_rsp_pd <> io.mcif2cdma_wt_rd_rsp_pd

///////////////////////////////////////////
//DorisLei: adding a 8*atmm fifo here for data buffering.
//use case: Cbuf has empty entries, but empty entry number < 8*atmm
//continue reading 8*atmm data from memory and then Cbuf can be fully written 
    val dma_rd_rsp_rdy = Wire(Bool())
    val u_8atmm_fifo = Module{new NV_NVDLA_fifo(depth = 8*conf.ATMM/conf.DMAIF, width = conf.NVDLA_CDMA_MEM_RD_RSP,
                        ram_type = 2, 
                        distant_wr_req = false)}
    u_8atmm_fifo.io.clk := io.nvdla_core_clk
    u_8atmm_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_8atmm_fifo.io.wr_pvld := nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.bits
    nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.ready := u_8atmm_fifo.io.wr_prdy
    u_8atmm_fifo.io.wr_pd := nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.bits
    val dma_rd_rsp_vld = u_8atmm_fifo.io.rd_pvld 
    u_8atmm_fifo.io.rd_prdy := dma_rd_rsp_rdy
    val dma_rd_rsp_pd = u_8atmm_fifo.io.rd_pd 
    
///////////////////////////////////////////
    dma_rd_req_pd := Cat(dma_rd_req_size, dma_rd_req_addr)
    dma_rd_req_vld := arb_sp_out_vld & dma_req_fifo_ready
    dma_rd_req_addr := Cat(dma_req_addr, "b0".asUInt(conf.ATMMBW.W))
    dma_rd_req_size := Cat("b0".asUInt(12.W), dma_req_size_out)
    val dma_rd_req_type = io.reg2dp_weight_ram_type
///////////////////////////////////
//DorisLei redefine dma_rd_rsp_rdy to block reading process when cbuf is full
///////////////////////////////////
    val wt_cbuf_wr_vld_w = Wire(Bool())
    val dmaif_within_atmc_cnt = if(conf.DMAIF < conf.ATMC) Some(RegInit("b0".asUInt(4.W))) else None
    if(conf.DMAIF < conf.ATMC){
        when(wt_cbuf_wr_vld_w){
            when(dmaif_within_atmc_cnt.get === (conf.ATMC/conf.DMAIF -1).U){
                dmaif_within_atmc_cnt.get := "d0".asUInt(4.W)
            }
            .otherwise{
                dmaif_within_atmc_cnt.get := dmaif_within_atmc_cnt.get + 1.U
            }
        }
    }

    val sc_wt_updt = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}
    val sc_wt_entries = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(15.W))}
    val wt_wr_dmatx_cnt = RegInit("b0".asUInt(17.W))
    when(wt_cbuf_wr_vld_w & (!sc_wt_updt)){
        if(conf.DMAIF == conf.ATMC){
            wt_wr_dmatx_cnt := wt_wr_dmatx_cnt + 1.U
        }
        else if(conf.DMAIF < conf.ATMC){
            when(dmaif_within_atmc_cnt.get === (conf.ATMC/conf.DMAIF -1).U){
                wt_wr_dmatx_cnt := wt_wr_dmatx_cnt + 1.U
            }
        }
    }
    .elsewhen(wt_cbuf_wr_vld_w & sc_wt_updt){
        if(conf.DMAIF == conf.ATMC){
            wt_wr_dmatx_cnt := wt_wr_dmatx_cnt + 1.U - sc_wt_entries
        }
        else if(conf.DMAIF < conf.ATMC){
            when(dmaif_within_atmc_cnt.get === (conf.ATMC/conf.DMAIF -1).U){
                wt_wr_dmatx_cnt := wt_wr_dmatx_cnt + 1.U - sc_wt_entries
            }
            .otherwise{
                wt_wr_dmatx_cnt := wt_wr_dmatx_cnt - sc_wt_entries
            }
        }       
    }
    .elsewhen(!wt_cbuf_wr_vld_w & sc_wt_updt){
        wt_wr_dmatx_cnt := wt_wr_dmatx_cnt - sc_wt_entries
    }

    dma_rd_rsp_rdy := (wt_wr_dmatx_cnt < Cat(weight_bank, "b0".asUInt(log2Ceil(conf.NVDLA_CBUF_BANK_DEPTH).W)))
    val dma_req_fifo_req = Wire(Bool())
    val dma_req_fifo_data = Wire(UInt(6.W))
    val dma_rsp_fifo_ready = Wire(Bool())

    val u_fifo = Module{new  NV_NVDLA_fifo(depth = 128, width = 6,
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
    
    dma_req_fifo_req := arb_sp_out_vld & dma_rd_req_rdy
    dma_req_fifo_data := Cat(dma_req_src, dma_req_size)

    ////////////////////////////////////////////////////////////////////////
    //  CDMA read response connection                                     //
    ////////////////////////////////////////////////////////////////////////
    val dma_rsp_size_cnt = RegInit("b0".asUInt(4.W))

    val dma_rd_rsp_data = dma_rd_rsp_pd(conf.NVDLA_MEMIF_WIDTH-1, 0)
    val dma_rd_rsp_mask = dma_rd_rsp_pd(conf.NVDLA_CDMA_MEM_RD_RSP - 1, conf.NVDLA_MEMIF_WIDTH)
    val dma_rsp_size = dma_rsp_fifo_data(3, 0)
    val dma_rsp_src = dma_rsp_fifo_data(5, 4)
    val dma_rsp_size_cnt_inc = dma_rsp_size_cnt + PopCount(dma_rd_rsp_mask)

    val dma_rsp_size_cnt_w = Mux(dma_rsp_size_cnt_inc === dma_rsp_size, "b0".asUInt(4.W), dma_rsp_size_cnt_inc)
    dma_rsp_fifo_ready := (dma_rd_rsp_vld & dma_rd_rsp_rdy & (dma_rsp_size_cnt_inc === dma_rsp_size))
    val wt_rsp_valid = (dma_rd_rsp_vld & dma_rd_rsp_rdy & (dma_rsp_src === src_id_wt))

    val dma_rsp_data_p = dma_rd_rsp_data

    when(dma_rd_rsp_vld & dma_rd_rsp_rdy){
        dma_rsp_size_cnt := dma_rsp_size_cnt_w
    }

    ////////////////////////////////////////////////////////////////////////
    //  WT read data                                                      //
    ////////////////////////////////////////////////////////////////////////
    val wt_local_data = Reg(UInt(conf.ATMM.W))
    val wt_local_data_vld = RegInit(false.B)
    val wt_cbuf_wr_idx = RegInit("b0".asUInt(17.W))

    val wt_local_data_vld_w = Wire(Bool())
    val wt_local_data_reg_en = Wire(Bool())
    val wt_local_data_w = Wire(UInt(conf.ATMMBW.W))
    val wt_cbuf_wr_data_ori_w = Wire(UInt(conf.NVDLA_CDMA_DMAIF_BW.W))

    if(conf.NVDLA_CDMA_MEM_MASK_BIT == 1){
        val wt_local_data_cnt =  wt_local_data_vld +& dma_rd_rsp_mask(0)
        val dma_rsp_data_p0 = dma_rd_rsp_data
        wt_local_data_vld_w := false.B
        wt_local_data_reg_en := false.B
        wt_cbuf_wr_vld_w := wt_rsp_valid
        wt_local_data_w := 0.U
        wt_cbuf_wr_data_ori_w := dma_rsp_data_p0

    }
    else if(conf.NVDLA_CDMA_MEM_MASK_BIT == 2){
        val wt_local_data_cnt =  wt_local_data_vld +& dma_rd_rsp_mask(0) +& dma_rd_rsp_mask(1)
        val dma_rsp_data_p0 = dma_rd_rsp_data(conf.ATMM-1, 0)
        val dma_rsp_data_p1 = dma_rd_rsp_data(2*conf.ATMM-1, conf.ATMM)
        wt_local_data_vld_w := wt_local_data_cnt(0)
        wt_local_data_reg_en := wt_rsp_valid & wt_local_data_cnt(0)
        wt_cbuf_wr_vld_w := wt_rsp_valid & wt_local_data_cnt(1)
        wt_local_data_w := Mux(dma_rd_rsp_mask(1), dma_rsp_data_p1, dma_rsp_data_p0) 
        wt_cbuf_wr_data_ori_w := Mux(wt_local_data_vld,  Cat(dma_rsp_data_p0, wt_local_data), dma_rd_rsp_data)
    }
    else if(conf.NVDLA_CDMA_MEM_MASK_BIT == 4){
        val wt_local_data_cnt =  wt_local_data_vld +& dma_rd_rsp_mask(0) +& dma_rd_rsp_mask(1) +& dma_rd_rsp_mask(2) +& dma_rd_rsp_mask(3) 
        val dma_rsp_data_p0 = dma_rd_rsp_data(conf.ATMM-1, 0)
        val dma_rsp_data_p1 = dma_rd_rsp_data(2*conf.ATMM-1, conf.ATMM)
        val dma_rsp_data_p2 = dma_rd_rsp_data(3*conf.ATMM-1, 2*conf.ATMM)
        wt_local_data_vld_w := wt_local_data_cnt(1, 0).orR
        wt_local_data_reg_en := wt_rsp_valid & wt_local_data_vld_w
        wt_cbuf_wr_vld_w := wt_rsp_valid & wt_local_data_cnt(2)
        wt_local_data_w := Mux(dma_rd_rsp_mask(3), dma_rd_rsp_data,
                           Mux(dma_rd_rsp_mask(2), Cat(dma_rsp_data_p2, dma_rsp_data_p1, dma_rsp_data_p0),
                           Mux(dma_rd_rsp_mask(1), Cat(dma_rsp_data_p1, dma_rsp_data_p0),
                           dma_rsp_data_p0)))
        wt_cbuf_wr_data_ori_w := Mux(wt_local_data_vld,  Cat(dma_rsp_data_p0, wt_local_data), dma_rd_rsp_data)       
    }


    val wt_cbuf_wr_idx_inc = wt_cbuf_wr_idx + 1.U
    val wt_cbuf_wr_idx_set = (layer_st & ~(wt_cbuf_wr_idx.orR))
    
    val wt_cbuf_wr_idx_wrap = (wt_cbuf_wr_idx_inc === Cat(weight_bank_end, "b0".asUInt(conf.BANK_DEPTH_BITS.W)))
    val wt_cbuf_wr_idx_w = Mux(clear_all | wt_cbuf_wr_idx_set | wt_cbuf_wr_idx_wrap, 
                           Cat(data_bank_w, "b0".asUInt(conf.BANK_DEPTH_BITS.W)), wt_cbuf_wr_idx_inc(16, 0))
    val wt_cbuf_wr_data_w = wt_cbuf_wr_data_ori_w
    when(wt_local_data_reg_en){
        wt_local_data := wt_local_data_w
    }
    when(wt_rsp_valid){
        wt_local_data_vld := wt_local_data_vld_w
    }
    when(wt_cbuf_wr_idx_set | clear_all | wt_cbuf_wr_vld_w){
        wt_cbuf_wr_idx := wt_cbuf_wr_idx_w
    }

////////////////////////////////////////////////////////////////////////
//  weight buffer flushing logic                                      //
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
//  Non-SLCG clock domain                                             //
////////////////////////////////////////////////////////////////////////
    val wt_cbuf_flush_idx = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(18.W))}
    val wt_cbuf_flush_idx_w = wt_cbuf_flush_idx + 1.U
    val wt_cbuf_flush_vld_w = ~wt_cbuf_flush_idx(log2Ceil(conf.ATMC/conf.DMAIF) + conf.KK - 1)

    io.dp2reg_wt_flush_done := wt_cbuf_flush_idx(log2Ceil(conf.ATMC/conf.DMAIF) + conf.KK - 1)

    when(wt_cbuf_flush_vld_w){
        wt_cbuf_flush_idx := wt_cbuf_flush_idx_w
    }

    val cdma2buf_wt_wr_en_w = wt_cbuf_wr_vld_w | wt_cbuf_flush_vld_w
    val cdma2buf_wt_wr_addr_w = Wire(UInt(17.W))
    val cdma2buf_wt_wr_sel_out = if(conf.DMAIF < conf.ATMC) 
                                Some(withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt((conf.ATMC/conf.DMAIF).W))})
                                else None
    val cdma2buf_wt_wr_sel_w = if(conf.DMAIF < conf.ATMC) Some(Wire(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W)))
                               else None 
    if(conf.DMAIF < conf.ATMC){
        cdma2buf_wt_wr_addr_w := Mux(wt_cbuf_wr_vld_w, wt_cbuf_wr_idx(16, log2Ceil(conf.ATMC/conf.DMAIF)),
                                 (conf.NVDLA_CBUF_BANK_NUMBER * conf.NVDLA_CBUF_BANK_DEPTH / 2).U + wt_cbuf_flush_idx(16, log2Ceil(conf.ATMC/conf.DMAIF)))
        cdma2buf_wt_wr_sel_w.get := Mux(wt_cbuf_wr_vld_w, wt_cbuf_wr_idx(log2Ceil(conf.ATMC/conf.DMAIF)-1, 0), wt_cbuf_flush_idx(log2Ceil(conf.ATMC/conf.DMAIF)-1, 0))
        when(cdma2buf_wt_wr_en_w){
            cdma2buf_wt_wr_sel_out.get := VecInit((0 to conf.ATMC/conf.DMAIF-1) map { i => (cdma2buf_wt_wr_sel_w.get === (conf.ATMC/conf.DMAIF).U)}).asUInt
        }
        io.cdma2buf_wt_wr_sel.get := cdma2buf_wt_wr_sel_out.get
    }
    else if(conf.DMAIF == conf.ATMC){
        cdma2buf_wt_wr_addr_w := Mux(wt_cbuf_wr_vld_w, wt_cbuf_wr_idx(16, 0), 
                                 (conf.NVDLA_CBUF_BANK_NUMBER * conf.NVDLA_CBUF_BANK_DEPTH / 2).U + wt_cbuf_flush_idx(16, 0))      
    }

    val cdma2buf_wt_wr_data_w = Mux(wt_cbuf_wr_vld_w, wt_cbuf_wr_data_w, 0.U)
    io.cdma2buf_wt_wr.addr.valid := withClock(io.nvdla_core_ng_clk){RegNext(cdma2buf_wt_wr_en_w, false.B)}
    io.cdma2buf_wt_wr.addr.bits := withClock(io.nvdla_core_ng_clk){RegEnable(cdma2buf_wt_wr_addr_w, "b0".asUInt(17.W), cdma2buf_wt_wr_en_w)}
////////////////////////////////////////////////////////////////////////
//  Non-SLCG clock domain end                                         //
////////////////////////////////////////////////////////////////////////
    io.cdma2buf_wt_wr.data := RegEnable(cdma2buf_wt_wr_data_w, "b0".asUInt(conf.NVDLA_CDMA_DMAIF_BW.W), cdma2buf_wt_wr_en_w)
    io.dp2reg_nan_weight_num := "b0".asUInt(32.W)
    io.dp2reg_inf_weight_num := "b0".asUInt(32.W)
////////////////////////////////////////////////////////////////////////
//  WT data status monitor                                            //
////////////////////////////////////////////////////////////////////////
//================  Non-SLCG clock domain ================//
//sc2cdma_wt_kernels are useless

//retiming
    sc_wt_updt := io.sc2cdma_wt_updt.valid
    when(io.sc2cdma_wt_updt.valid){
        sc_wt_entries := io.sc2cdma_wt_updt.bits.entries
    }
//cation: the basic unit of data_stored, data_onfly and data_avl is atomic_m bytes, 32 bytes in Xavier
    val status_update = Wire(Bool())
    val wt_data_stored_sub = Wire(UInt(17.W))
    val wt_data_avl_sub = Wire(UInt(17.W))
    val incr_wt_entries_w = Wire(UInt(15.W))
    val wt_data_onfly_add = Mux(wt_req_reg_en_d2 & wt_req_stage_vld_d2 & ~wt_req_done_d2, wt_req_size_d2, "b0".asUInt(4.W))
    val wt_data_onfly_sub = Mux(wt_cbuf_wr_vld_w, (conf.DMAIF/conf.ATMM).U, "b0".asUInt(3.W))
    if(conf.ATMC/conf.ATMM == 1){
        wt_data_stored_sub := Mux(status_update, incr_wt_entries_w, "b0".asUInt(17.W))
        wt_data_avl_sub := Mux(sc_wt_updt, sc_wt_entries, "b0".asUInt(17.W))
    }
    else if(conf.ATMC/conf.ATMM == 2){
        wt_data_stored_sub := Mux(status_update, Cat(incr_wt_entries_w, "b0".asUInt(1.W)), "b0".asUInt(17.W))
        wt_data_avl_sub := Mux(sc_wt_updt,  Cat(sc_wt_entries, "b0".asUInt(1.W)), "b0".asUInt(17.W))       
    }
    else if(conf.ATMC/conf.ATMM == 4){
        wt_data_stored_sub := Mux(status_update, Cat(incr_wt_entries_w, "b0".asUInt(2.W)), "b0".asUInt(17.W))
        wt_data_avl_sub := Mux(sc_wt_updt,  Cat(sc_wt_entries, "b0".asUInt(2.W)), "b0".asUInt(17.W))   
    }

    val wt_data_onfly_w = wt_data_onfly + wt_data_onfly_add - wt_data_onfly_sub
    val wt_data_stored_w = wt_data_stored + wt_data_onfly_sub - wt_data_stored_sub
    val wt_data_avl_w = Mux(clear_all, 0.U, wt_data_avl + wt_data_stored_sub - wt_data_avl_sub)
    val wt_data_onfly_reg_en = ((wt_req_reg_en_d2 & wt_req_stage_vld_d2) | wt_cbuf_wr_vld_w)

    when(wt_data_onfly_reg_en){
        wt_data_onfly := wt_data_onfly_w
    }
    when(wt_cbuf_wr_vld_w | status_update){
        wt_data_stored := wt_data_stored_w
    }
    when(status_update | sc_wt_updt | clear_all){
        wt_data_avl := wt_data_avl_w
    }

////////////////////////////////////////////////////////////////////////
//  status update logic                                               //
////////////////////////////////////////////////////////////////////////
    val status_group_cnt = RegInit("b0".asUInt(12.W))
    val pre_wt_required_bytes = RegInit("b0".asUInt(32.W))
    val wt_fetched_cnt = RegInit("b0".asUInt(26.W))
    val required_valid = RegInit(false.B)
    val wt_required_bytes = RegInit("b0".asUInt(32.W))

    val status_group_cnt_inc = status_group_cnt + 1.U
    val status_last_group = (status_group_cnt_inc === group)
    val status_group_cnt_w = Mux(layer_st, "b0".asUInt(12.W), status_group_cnt_inc)
    val status_done_w = Mux(layer_st, false.B,
                        Mux(status_last_group, true.B,
                        status_done))
    val normal_bpg = Cat(byte_per_kernel, "b0".asUInt(conf.ATMKBW.W))
    val wt_required_bytes_w = Mux(layer_st, "b0".asUInt(32.W),
                              Mux(status_last_group, io.reg2dp_weight_bytes,
                              pre_wt_required_bytes + normal_bpg))
    val required_valid_w = is_running & ~status_update
    val wt_required_en = ~required_valid & required_valid_w
    val pre_wt_required_bytes_w = Mux(layer_st, "b0".asUInt(32.W), wt_required_bytes)
    

    required_valid := required_valid_w
    when(layer_st | wt_required_en){
        wt_required_bytes := wt_required_bytes_w
    }
    //////// caution: one in fetched_cnt refers to 64 bytes ////////
    val wt_fetched_cnt_inc = wt_fetched_cnt + 1.U
    val wt_fetched_cnt_w = Mux(layer_st, "b0".asUInt(26.W), wt_fetched_cnt_inc) 
    val wt_satisfied = if(conf.ATMC > conf.DMAIF)
                       is_running & (Cat(wt_fetched_cnt, "b0".asUInt(log2Ceil(conf.DMAIF).W)) >= wt_required_bytes) & 
                       ~(wt_fetched_cnt(conf.ATMC/conf.DMAIF - 1, 0).orR)
                       else
                       is_running & (Cat(wt_fetched_cnt, "b0".asUInt(log2Ceil(conf.DMAIF).W)) >= wt_required_bytes)
    status_update := Mux(~required_valid, false.B, wt_satisfied)

    when(layer_st | status_update){
        status_group_cnt := status_group_cnt_w
        status_done := status_done_w
        pre_wt_required_bytes := pre_wt_required_bytes_w
    }
    when(layer_st | wt_cbuf_wr_vld_w){
        wt_fetched_cnt := wt_fetched_cnt_w
    }

    ////////////////////////////////////////////////////////////////////////
    //  avaliable kernels monitor                                         //
    ////////////////////////////////////////////////////////////////////////
    // Avaliable kernel size is useless here. Discard the code;

    ////////////////////////////////////////////////////////////////////////
    //  CDMA WT communicate to CSC                                        //
    ////////////////////////////////////////////////////////////////////////
    val incr_wt_updt = RegInit(false.B)
    val pre_wt_fetched_cnt = RegInit("b0".asUInt(26.W))
    val incr_wt_entries = RegInit("b0".asUInt(15.W))
    val incr_wt_kernels = RegInit("b0".asUInt(6.W))

    val pre_wt_fetched_cnt_w = Mux(status_last_group, "b0".asUInt(26.W), wt_fetched_cnt)
    val incr_wt_cnt =  wt_fetched_cnt - pre_wt_fetched_cnt
    if(conf.DMAIF == conf.ATMC){
        incr_wt_entries_w := incr_wt_cnt(14, 0)
    }
    else if(conf.DMAIF < conf.ATMC){
        incr_wt_entries_w := incr_wt_cnt(14+log2Ceil(conf.ATMC/conf.DMAIF), log2Ceil(conf.ATMC/conf.DMAIF))
    }

    val incr_wt_kernels_w = Mux(~status_last_group, conf.NVDLA_MAC_ATOMIC_K_SIZE.U, 
                            io.reg2dp_weight_kernel(conf.ATMKBW-1, 0) +& 1.U)

    incr_wt_updt := status_update
    when(status_update){
        pre_wt_fetched_cnt := pre_wt_fetched_cnt_w
        incr_wt_entries := incr_wt_entries_w
        incr_wt_kernels := incr_wt_kernels_w
    }

    val incr_wt_updt_d = Wire(Bool()) +: 
                         Seq.fill(conf.CDMA_CBUF_WR_LATENCY)(RegInit(false.B))
    val incr_wt_kernels_d = Wire(UInt(6.W)) +: 
                            Seq.fill(conf.CDMA_CBUF_WR_LATENCY)(RegInit("b0".asUInt(6.W)))
    val incr_wt_entries_d = Wire(UInt(15.W)) +: 
                            Seq.fill(conf.CDMA_CBUF_WR_LATENCY)(RegInit("b0".asUInt(15.W)))
    incr_wt_updt_d(0) := incr_wt_updt
    incr_wt_kernels_d(0) := incr_wt_kernels
    incr_wt_entries_d(0) := incr_wt_entries

    for(t <- 0 to conf.CDMA_CBUF_WR_LATENCY-1){
        incr_wt_updt_d(t+1) := incr_wt_updt_d(t)
        when(incr_wt_updt_d(t)){
            incr_wt_entries_d(t+1) := incr_wt_entries_d(t)
            incr_wt_kernels_d(t+1) := incr_wt_kernels_d(t)
        }
    }


    io.cdma2sc_wt_updt.valid := incr_wt_updt_d(conf.CDMA_CBUF_WR_LATENCY) 
    io.cdma2sc_wt_updt.bits.kernels := incr_wt_kernels_d(conf.CDMA_CBUF_WR_LATENCY)
    io.cdma2sc_wt_updt.bits.entries := incr_wt_entries_d(conf.CDMA_CBUF_WR_LATENCY) 

////////////////////////////////////////////////////////////////////////
// performance counting register //
////////////////////////////////////////////////////////////////////////  
    //stall
    val wt_rd_stall_inc = RegNext(dma_rd_req_vld & ~dma_rd_req_rdy & io.reg2dp_dma_en, false.B)
    val wt_rd_stall_clr = RegNext(io.status2dma_fsm_switch & io.reg2dp_dma_en, false.B)
    val wt_rd_stall_cen = RegNext(io.reg2dp_op_en & io.reg2dp_dma_en, false.B)

    val dp2reg_wt_rd_stall_dec = false.B

    val stl = Module(new NV_COUNTER_STAGE(32))
    stl.io.clk := io.nvdla_core_clk
    stl.io.rd_stall_inc := wt_rd_stall_inc
    stl.io.rd_stall_dec := dp2reg_wt_rd_stall_dec
    stl.io.rd_stall_clr := wt_rd_stall_clr
    stl.io.rd_stall_cen := wt_rd_stall_cen
    io.dp2reg_wt_rd_stall := stl.io.cnt_cur

    //latency_1
    val wt_rd_latency_inc = RegNext(dma_rd_req_vld & ~dma_rd_req_rdy & io.reg2dp_dma_en, false.B)
    val wt_rd_latency_dec = RegNext(dma_rsp_fifo_ready & io.reg2dp_dma_en, false.B)
    val wt_rd_latency_clr = RegNext(io.status2dma_fsm_switch, false.B)
    val wt_rd_latency_cen = RegNext(io.reg2dp_op_en & io.reg2dp_dma_en, false.B)

    val outs_dp2reg_wt_rd_latency = Wire(UInt(9.W))
    val ltc_1_inc = (outs_dp2reg_wt_rd_latency =/= 511.U) & wt_rd_latency_inc
    val ltc_1_dec = (outs_dp2reg_wt_rd_latency =/= 511.U) & wt_rd_latency_dec

    val ltc_1 = Module(new NV_COUNTER_STAGE(9))
    ltc_1.io.clk := io.nvdla_core_clk
    ltc_1.io.rd_stall_inc := ltc_1_inc
    ltc_1.io.rd_stall_dec := ltc_1_dec
    ltc_1.io.rd_stall_clr := wt_rd_latency_clr
    ltc_1.io.rd_stall_cen := wt_rd_latency_cen
    outs_dp2reg_wt_rd_latency := ltc_1.io.cnt_cur

}}





object NV_NVDLA_CDMA_wtDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_wt())
}
