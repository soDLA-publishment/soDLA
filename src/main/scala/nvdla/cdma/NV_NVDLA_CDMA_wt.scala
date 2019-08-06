// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver


// class NV_NVDLA_CDMA_wtIO(implicit conf: cdmaConfiguration) extends Bundle {

//     //nvdla core clock
//     val nvdla_core_clk = Input(Clock())
//     val nvdla_core_ng_clk = Input(Clock())
//     val pwrbus_ram_pd = Input(UInt(32.W))

//     //mcif
//     val cdma_wt2mcif_rd_req_valid = Output(Bool())
//     val cdma_wt2mcif_rd_req_ready = Input(Bool())
//     val cdma_wt2mcif_rd_req_pd  = Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
//     val mcif2cdma_wt_rd_rsp_valid = Input(Bool())
//     val mcif2cdma_wt_rd_rsp_ready = Output(Bool())
//     val mcif2cdma_wt_rd_rsp_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))

//     //cvif
//     val cdma_wt2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//     val cdma_wt2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//     val cdma_wt2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
//     val cvif2cdma_wt_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//     val cvif2cdma_wt_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//     val cvif2cdma_wt_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None

//     val cdma2buf_wt_wr_en = Output(Bool())
//     val cdma2buf_wt_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Output(UInt((conf.ATMC/conf.DMAIF).W))) else None
//     val cdma2buf_wt_wr_addr = Output(UInt(17.W))
//     val cdma2buf_wt_wr_data = Output(UInt(conf.DMAIF.W))
//     val status2dma_fsm_switch = Input(Bool())

//     val wt2status_state = Output(UInt(2.W))

//     val cdma2sc_wt_updt = Output(Bool())
//     val cdma2sc_wt_kernels = Output(UInt(14.W))
//     val cdma2sc_wt_entries = Output(UInt(15.W))
//     val cdma2sc_wmb_entries = Output(UInt(12.W))
//     val sc2cdma_wt_updt = Input(Bool())
//     val sc2cdma_wt_kernels = Input(UInt(14.W))
//     val sc2cdma_wt_entries = Input(UInt(15.W))
//     val sc2cdma_wmb_entries = Input(UInt(9.W))

//     val sc2cdma_wt_pending_req = Input(Bool())
//     val cdma2sc_wt_pending_ack = Output(Bool())

//     val reg2dp_arb_weight = Input(UInt(4.W))
//     val reg2dp_arb_wmb = Input(UInt(4.W))
//     val reg2dp_op_en = Input(Bool())
//     val reg2dp_proc_precision = Input(UInt(2.W))
//     val reg2dp_weight_reuse = Input(Bool())
//     val reg2dp_skip_weight_rls = Input(Bool())
//     val reg2dp_weight_format = Input(Bool())
//     val reg2dp_byte_per_kernel = Input(UInt(18.W))
//     val reg2dp_weight_kernel = Input(UInt(13.W))
//     val reg2dp_weight_ram_type = Input(Bool())
//     val reg2dp_weight_addr_low = Input(UInt((32-conf.ATMMBW).W))
//     val reg2dp_wgs_addr_low = Input(UInt((32-conf.ATMMBW).W))
//     val reg2dp_wmb_addr_low = Input(UInt((32-conf.ATMMBW).W))
//     val reg2dp_weight_addr_high = Input(UInt(32.W))
//     val reg2dp_weight_bytes = Input(UInt(32.W))
//     val reg2dp_wgs_addr_high = Input(UInt(32.W))
//     val reg2dp_wmb_addr_high = Input(UInt(32.W))
//     val reg2dp_wmb_bytes = Input(UInt(28.W))
//     val reg2dp_data_bank = Input(UInt(5.W))
//     val reg2dp_weight_bank = Input(UInt(5.W))
//     val reg2dp_nan_to_zero = Input(Bool())
//     val reg2dp_dma_en = Input(Bool())

//     val dp2reg_nan_weight_num = Output(UInt(32.W))
//     val dp2reg_inf_weight_num = Output(UInt(32.W))
//     val dp2reg_wt_flush_done = Output(Bool())
//     val dp2reg_wt_rd_stall = Output(UInt(32.W))
//     val dp2reg_wt_rd_latency = Output(UInt(32.W))

// }



// class NV_NVDLA_CDMA_wt(implicit conf: cdmaConfiguration) extends Module {
//     val io = IO(new NV_NVDLA_CDMA_wtIO)
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
// // CDMA weight fetching logic FSM                                     //
// ////////////////////////////////////////////////////////////////////////
//     val need_pending = Wire(Bool())
//     val pending_req_end = Wire(Bool())
//     val fetch_done = Wire(Bool())
//     val last_skip_weight_rls = RegInit(false.B)
//     val sIdle :: sPend :: sBusy :: sDone :: Nil = Enum(4)
//     val cur_state = RegInit(sIdle)
//     val nxt_state = WireInit(sIdle)

//     switch (cur_state) {
//         is (sIdle) {
//         when (io.reg2dp_op_en & need_pending) { nxt_state := sPend }
//         .elsewhen (io.reg2dp_op_en & io.reg2dp_weight_reuse & last_skip_weight_rls) { nxt_state := sDone }
//         .elsewhen (io.reg2dp_op_en) { nxt_state := sBusy }
//         }
//         is (sPend) {
//         when (pending_req_end) { nxt_state := sBusy }
//         }
//         is (sBusy) {
//         when (fetch_done) { nxt_state := sDone }
//         }
//         is (sDone) {
//         when (io.status2dma_fsm_switch) { nxt_state := sIdle }
//         }
//     }
//     cur_state := nxt_state
// ////////////////////////////////////////////////////////////////////////
// //  FSM input signals                                                 //
// ////////////////////////////////////////////////////////////////////////
//     val last_data_bank = RegInit(Fill(5, true.B))
//     val last_weight_bank = RegInit(Fill(5, true.B))
//     val is_running = Wire(Bool())
//     val layer_st = Wire(Bool())
//     val status_done = RegInit(false.B)
//     val status_done_cnt = RegInit("b0".asUInt(4.W))

//     val status_done_cnt_w = Mux(layer_st, "b0".asUInt(4.W), 
//                             Mux(status_done & (status_done_cnt =/= "h8".asUInt(4.W)), status_done_cnt + 1.U,
//                             status_done_cnt))
//     fetch_done := status_done & (status_done_cnt === "h8".asUInt(4.W))
//     need_pending := ((last_data_bank =/= io.reg2dp_data_bank) | (last_weight_bank =/= io.reg2dp_weight_bank))

//     when(layer_st | is_running){
//         status_done_cnt := status_done_cnt_w
//     }
// ////////////////////////////////////////////////////////////////////////
// //  FSM output signals                                                //
// ////////////////////////////////////////////////////////////////////////
//     val wt2status_state_out = RegInit("b0".asUInt(2.W))
//     val pending_req = RegInit(false.B)
//     val pending_req_d1 = RegInit(false.B)
//     val pending_ack = RegInit(false.B)

//     layer_st := io.reg2dp_op_en && (cur_state === sIdle)
//     val layer_end = io.status2dma_fsm_switch
//     is_running := (cur_state === sBusy)
//     val is_pending = (cur_state === sPend)
//     val clear_all = pending_ack & pending_req
//     val is_nxt_running = (nxt_state === sBusy)
//     val wt2status_state_w = nxt_state
//     pending_req_end := pending_req_d1 & ~pending_req

//     wt2status_state_out := wt2status_state_w
//     pending_req := io.sc2cdma_wt_pending_req
//     pending_req_d1 := pending_req
//     pending_ack := is_pending

//     io.wt2status_state := wt2status_state_out
//     io.cdma2sc_wt_pending_ack := pending_ack

// ////////////////////////////////////////////////////////////////////////
// //  registers to keep last layer status                               //
// ////////////////////////////////////////////////////////////////////////
//     when(layer_end){
//         last_data_bank := io.reg2dp_data_bank
//         last_weight_bank := io.reg2dp_weight_bank
//         last_skip_weight_rls := io.reg2dp_skip_weight_rls
//     }

//     val layer_st_d1 = RegNext(layer_st, false.B)
// ////////////////////////////////////////////////////////////////////////
// //  registers to calculate local values                               //
// ////////////////////////////////////////////////////////////////////////
//     val group = RegInit(Fill(12, true.B))
//     val weight_bank = RegInit(Fill(6, true.B))
//     val weight_bank_end = RegInit(Fill(7, true.B))
//     val nan_pass = RegInit(true.B)

//     val byte_per_kernel = RegEnable(io.reg2dp_byte_per_kernel +& 1.U, layer_st)
//     val is_int8 = true.B
//     val is_fp16 = false.B
//     val group_op = io.reg2dp_weight_kernel(12, conf.ATMKBW)
//     val group_w = group_op +& 1.U
//     val data_bank_w = io.reg2dp_data_bank +& 1.U
//     val weight_bank_w = io.reg2dp_weight_bank +& 1.U
//     val weight_bank_end_w = weight_bank_w +& data_bank_w
//     val nan_pass_w = ~io.reg2dp_nan_to_zero | ~is_fp16
//     val is_compressed = false.B

//     when(layer_st){
//         group := group_w
//         weight_bank := weight_bank_w
//         weight_bank_end := weight_bank_end_w
//         nan_pass := nan_pass_w
//     }
// ////////////////////////////////////////////////////////////////////////
// //  generate address for weight data                                  //
// ////////////////////////////////////////////////////////////////////////
//     val src_id_wt = "b00".asUInt(2.W)
//     val src_id_wmb = "b01".asUInt(2.W)
//     val src_id_wgs = "b10".asUInt(2.W)
// /////////////////// stage 1 ///////////////////
//     val wt_req_size_d1 = RegInit("b0".asUInt(4.W))
//     val wt_req_burst_cnt_d1 = RegInit("b0".asUInt(29.W))
//     val wt_req_stage_vld_d1 = RegInit(false.B)
//     val wt_req_reg_en = Wire(Bool())

//     val wt_req_reg_en_d0 = wt_req_reg_en
//     val wt_req_burst_cnt_dec = wt_req_burst_cnt_d1 - wt_req_size_d1
//     val wt_req_burst_cnt_w = Mux(layer_st, io.reg2dp_weight_bytes(31, conf.ATMMBW), wt_req_burst_cnt_dec)
//     val wt_req_size_addr_limit = Mux(layer_st, "h8".asUInt(4.W) - io.reg2dp_weight_addr_low(2, 0), "h8".asUInt(4.W))
//     val wt_req_size_w = Mux(wt_req_size_addr_limit > wt_req_burst_cnt_w, wt_req_burst_cnt_w(3, 0), wt_req_size_addr_limit)

//     when(wt_req_reg_en_d0){
//         wt_req_size_d1 := wt_req_size_w
//         wt_req_burst_cnt_d1 := wt_req_burst_cnt_w
//     }

//     wt_req_stage_vld_d1 := is_nxt_running
// /////////////////// stage 2 ///////////////////
//     val wt_req_addr_d2 = RegInit("b0".asUInt((64 - conf.ATMMBW).W))
//     val wt_req_size_d2 = RegInit("b0".asUInt(4.W))
//     val wt_req_size_out_d2 = RegInit("b0".asUInt(3.W))
//     val wt_req_last_d2 = RegInit(false.B)
//     val wt_req_done_d2 = RegInit(true.B)
//     val wt_req_stage_vld_d2 = RegInit(false.B)

//     val wt_req_reg_en_d1 = wt_req_reg_en
//     val wt_req_last_w = wt_req_stage_vld_d1 && (wt_req_burst_cnt_d1 === wt_req_size_d1)
//     val wt_req_addr_inc = wt_req_addr_d2(63-conf.ATMMBW, 3) +& 1.U
//     val wt_req_addr_w = Mux(~wt_req_stage_vld_d2, Cat(io.reg2dp_weight_addr_high, io.reg2dp_weight_addr_low), Cat(wt_req_addr_inc, "b0".asUInt(3.W)))
//     val wt_req_size_out_w = wt_req_size_d1(2, 0) -& 1.U
//     val wt_req_done_w = Mux(layer_st, false.B, Mux(wt_req_last_d2, true.B, wt_req_done_d2))

//     when(wt_req_reg_en_d1){
//         wt_req_addr_d2 := wt_req_addr_w
//         wt_req_size_d2 := wt_req_size_d1
//         wt_req_size_out_d2 := wt_req_size_out_w
//         wt_req_last_d2 := wt_req_last_w
//         wt_req_done_d2 := wt_req_done_w
//     }
//     wt_req_stage_vld_d2 := wt_req_stage_vld_d1 & is_nxt_running
// /////////////////// stage 3 ///////////////////
//     val wt_req_vld_d3 = RegInit(false.B)
//     val wt_req_addr_d3 = RegInit("b0".asUInt((64 - conf.ATMMBW).W))
//     val wt_req_size_d3 = RegInit("b0".asUInt(4.W))
//     val wt_req_size_out_d3 = RegInit("b0".asUInt(3.W))
//     val wt_req_done_d3 = RegInit(true.B)

//     val wt_req_reg_en_d2 = wt_req_reg_en
//     val wt_req_vld_w = is_nxt_running & wt_req_stage_vld_d2

//      wt_req_vld_d3 := wt_req_vld_w
//      when(wt_req_reg_en_d2){
//          wt_req_addr_d3 := wt_req_addr_d2
//          wt_req_size_d3 := wt_req_size_d2
//          wt_req_size_out_d3 := wt_req_size_out_d2
//          wt_req_done_d3 := is_running & wt_req_done_d2
//      }

//     val wt_req_src_d3 = src_id_wt
// /////////////////// overflow control logic ///////////////////
//     val wt_data_onfly = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(14.W))}
//     val wt_data_stored = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(17.W))}
//     val wt_data_avl = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(17.W))}
//     val wt_req_sum = wt_data_onfly + wt_data_stored + wt_data_avl
//     val wt_req_overflow = is_running && (wt_req_sum > (Cat(weight_bank, "b0".asUInt(conf.CBUF_BANK_FETCH_BITS.W)) +& conf.ATMM8.U))
//     val wt_req_overflow_d3 = wt_req_overflow
// /////////////////// pipeline control logic ///////////////////
//     val wt_req_rdy = Wire(Bool())
//     wt_req_reg_en := layer_st | (is_running & (~wt_req_vld_d3 | wt_req_rdy))
// ///////////////////////////// connect to dma ////////////////////////////////
//     val dma_rd_req_rdy = Wire(Bool())
//     val dma_req_fifo_ready = Wire(Bool())
//     val arb_sp_out_rdy = dma_rd_req_rdy & dma_req_fifo_ready
//     val arb_sp_out_vld = wt_req_vld_d3 & ~wt_req_overflow_d3 & ~wt_req_done_d3
//     wt_req_rdy := arb_sp_out_rdy & arb_sp_out_vld
//     val dma_req_src = wt_req_src_d3
//     val dma_req_size = wt_req_size_d3
//     val dma_req_size_out = wt_req_size_out_d3
//     val dma_req_addr = wt_req_addr_d3

// ////////////////////////////////////////////////////////////////////////
// //  CDMA WT read request interface                                    //
// ////////////////////////////////////////////////////////////////////////
// //==============
// // DMA Interface
// //==============
// // rd Channel: Request
//     val dma_rd_req_pd = Wire(UInt((conf.NVDLA_CDMA_MEM_RD_REQ).W))
//     val dma_rd_req_addr = Wire(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
//     val dma_rd_req_size = Wire(UInt(15.W))
//     val dma_rd_req_vld = Wire(Bool())
//     val dmaif_rd_rsp_prdy = Wire(Bool())

//     val nv_NVDLA_PDP_RDMA_rdreq = Module{new NV_NVDLA_DMAIF_rdreq}
//     nv_NVDLA_PDP_RDMA_rdreq.io.nvdla_core_clk := io.nvdla_core_clk
//     nv_NVDLA_PDP_RDMA_rdreq.io.reg2dp_src_ram_type := io.reg2dp_weight_ram_type
//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         nv_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_ready.get := io.cdma_wt2cvif_rd_req_ready.get
//         io.cdma_wt2cvif_rd_req_pd.get := nv_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_pd.get
//         io.cdma_wt2cvif_rd_req_valid.get := nv_NVDLA_PDP_RDMA_rdreq.io.cvif_rd_req_valid.get
//     }
//     io.cdma_wt2mcif_rd_req_pd:= nv_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_pd
//     io.cdma_wt2mcif_rd_req_valid := nv_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_valid
//     nv_NVDLA_PDP_RDMA_rdreq.io.mcif_rd_req_ready := io.cdma_wt2mcif_rd_req_ready

//     nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_pd := dma_rd_req_pd
//     nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_vld := dma_rd_req_vld
//     dma_rd_req_rdy := nv_NVDLA_PDP_RDMA_rdreq.io.dmaif_rd_req_rdy
//     // rd Channel: Response
//     val nv_NVDLA_PDP_RDMA_rdrsp = Module{new NV_NVDLA_DMAIF_rdrsp}
//     nv_NVDLA_PDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk
//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get := io.cvif2cdma_wt_rd_rsp_pd.get
//         nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_valid.get := io.cvif2cdma_wt_rd_rsp_valid.get
//         io.cvif2cdma_wt_rd_rsp_ready.get := nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_ready.get
//     }
//     nv_NVDLA_PDP_RDMA_rdrsp.io.mcif_rd_rsp_pd := io.mcif2cdma_wt_rd_rsp_pd
//     nv_NVDLA_PDP_RDMA_rdrsp.io.mcif_rd_rsp_valid := io.mcif2cdma_wt_rd_rsp_valid
//     io.mcif2cdma_wt_rd_rsp_ready := nv_NVDLA_PDP_RDMA_rdrsp.io.mcif_rd_rsp_ready

//     val dmaif_rd_rsp_pd= nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd
//     val dmaif_rd_rsp_pvld = nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pvld
//     nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_prdy := dmaif_rd_rsp_prdy

// ///////////////////////////////////////////
// //DorisLei: adding a 8*atmm fifo here for data buffering.
// //use case: Cbuf has empty entries, but empty entry number < 8*atmm
// //continue reading 8*atmm data from memory and then Cbuf can be fully written 
//     val dma_rd_rsp_rdy = Wire(Bool())
//     val u_8atmm_fifo = Module{new NV_NVDLA_CDMA_WT_8ATMM_fifo(conf.NVDLA_CDMA_MEM_RD_RSP, 8*(conf.NVDLA_MEMORY_ATOMIC_SIZE * conf.NVDLA_CDMA_BPE)/(conf.NVDLA_CDMA_DMAIF_BW))}
//     u_8atmm_fifo.io.nvdla_core_clk := io.nvdla_core_clk
//     dmaif_rd_rsp_prdy := u_8atmm_fifo.io.lat_wr_prdy
//     u_8atmm_fifo.io.lat_wr_pvld := dmaif_rd_rsp_pvld
//     u_8atmm_fifo.io.lat_wr_pd := dmaif_rd_rsp_pd
//     u_8atmm_fifo.io.lat_rd_prdy := dma_rd_rsp_rdy
//     val dma_rd_rsp_vld = u_8atmm_fifo.io.lat_rd_pvld 
//     val dma_rd_rsp_pd = u_8atmm_fifo.io.lat_rd_pd 
//     u_8atmm_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
// ///////////////////////////////////////////
//     dma_rd_req_pd := Cat(dma_rd_req_size, dma_rd_req_addr)
//     dma_rd_req_vld := arb_sp_out_vld & dma_req_fifo_ready
//     dma_rd_req_addr := Cat(dma_req_addr, "b0".asUInt(conf.ATMMBW.W))
//     dma_rd_req_size := Cat("b0".asUInt(12.W), dma_req_size_out)
//     val dma_rd_req_type = io.reg2dp_weight_ram_type
// ///////////////////////////////////
// //DorisLei redefine dma_rd_rsp_rdy to block reading process when cbuf is full
// ///////////////////////////////////
//     val wt_cbuf_wr_vld_w = Wire(Bool())
//     val dmaif_within_atmc_cnt = if(conf.DMAIF < conf.ATMC) Some(RegInit("b0".asUInt(4.W))) else None
//     if(conf.DMAIF < conf.ATMC){
//         when(wt_cbuf_wr_vld_w){
//             when(dmaif_within_atmc_cnt.get === (conf.ATMC/conf.DMAIF -1).U){
//                 dmaif_within_atmc_cnt.get := "d0".asUInt(4.W)
//             }
//             .otherwise{
//                 dmaif_within_atmc_cnt.get := dmaif_within_atmc_cnt.get + 1.U
//             }
//         }
//     }

//     val sc_wt_updt = withClock(io.nvdla_core_ng_clk){RegInit(false.B)}
//     val sc_wt_entries = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(15.W))}
//     val wt_wr_dmatx_cnt = RegInit("b0".asUInt(17.W))
//     when(wt_cbuf_wr_vld_w & (!sc_wt_updt)){
//         if(conf.DMAIF == conf.ATMC){
//             wt_wr_dmatx_cnt := wt_wr_dmatx_cnt + 1.U
//         }
//         else if(conf.DMAIF < conf.ATMC){
//             when(dmaif_within_atmc_cnt.get === (conf.ATMC/conf.DMAIF -1).U){
//                 wt_wr_dmatx_cnt := wt_wr_dmatx_cnt + 1.U
//             }
//         }
//     }
//     .elsewhen(wt_cbuf_wr_vld_w & sc_wt_updt){
//         if(conf.DMAIF == conf.ATMC){
//             wt_wr_dmatx_cnt := wt_wr_dmatx_cnt + 1.U - sc_wt_entries
//         }
//         else if(conf.DMAIF < conf.ATMC){
//             when(dmaif_within_atmc_cnt.get === (conf.ATMC/conf.DMAIF -1).U){
//                 wt_wr_dmatx_cnt := wt_wr_dmatx_cnt + 1.U - sc_wt_entries
//             }
//             .otherwise{
//                 wt_wr_dmatx_cnt := wt_wr_dmatx_cnt - sc_wt_entries
//             }
//         }       
//     }
//     .elsewhen(!wt_cbuf_wr_vld_w & sc_wt_updt){
//         wt_wr_dmatx_cnt := wt_wr_dmatx_cnt - sc_wt_entries
//     }

//     dma_rd_rsp_rdy := (wt_wr_dmatx_cnt < Cat(weight_bank, "b0".asUInt(log2Ceil(conf.NVDLA_CBUF_BANK_DEPTH).W)))
//     val dma_req_fifo_req = Wire(Bool())
//     val dma_req_fifo_data = Wire(UInt(6.W))
//     val dma_rsp_fifo_ready = Wire(Bool())

//     val u_fifo = Module{new NV_NVDLA_CDMA_fifo(128, 6)}
//     u_fifo.io.clk := io.nvdla_core_clk
//     dma_req_fifo_ready := u_fifo.io.wr_ready
//     u_fifo.io.wr_req := dma_req_fifo_req
//     u_fifo.io.wr_data := dma_req_fifo_data
//     u_fifo.io.rd_ready := dma_rsp_fifo_ready
//     val dma_rsp_fifo_req = u_fifo.io.rd_req
//     val dma_rsp_fifo_data = u_fifo.io.rd_data
//     u_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     dma_req_fifo_req := arb_sp_out_vld & dma_rd_req_rdy
//     dma_req_fifo_data := Cat(dma_req_src, dma_req_size)

//     ////////////////////////////////////////////////////////////////////////
//     //  CDMA read response connection                                     //
//     ////////////////////////////////////////////////////////////////////////
//     val dma_rsp_size_cnt = RegInit("b0".asUInt(4.W))

//     val dma_rd_rsp_data = dma_rd_rsp_pd(conf.NVDLA_MEMIF_WIDTH-1, 0)
//     val dma_rd_rsp_mask = dma_rd_rsp_pd(conf.NVDLA_CDMA_MEM_RD_RSP - 1, conf.NVDLA_MEMIF_WIDTH)
//     val dma_rsp_size = dma_rsp_fifo_data(3, 0)
//     val dma_rsp_src = dma_rsp_fifo_data(5, 4)
//     val dma_rsp_size_cnt_inc = dma_rsp_size_cnt + dma_rd_rsp_mask(0)

//     val dma_rsp_size_cnt_w = Mux(dma_rsp_size_cnt_inc === dma_rsp_size, "b0".asUInt(4.W), dma_rsp_size_cnt_inc)
//     dma_rsp_fifo_ready := (dma_rd_rsp_vld & dma_rd_rsp_rdy & (dma_rsp_size_cnt_inc === dma_rsp_size))
//     val wt_rsp_valid = (dma_rd_rsp_vld & dma_rd_rsp_rdy & (dma_rsp_src === src_id_wt))

//     val dma_rsp_data_p0 = dma_rd_rsp_data

//     when(dma_rd_rsp_vld & dma_rd_rsp_rdy){
//         dma_rsp_size_cnt := dma_rsp_size_cnt_w
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //  WT read data                                                      //
//     ////////////////////////////////////////////////////////////////////////
//     val wt_local_data = Reg(UInt(conf.ATMM.W))
//     val wt_local_data_vld = RegInit(false.B)
//     val wt_cbuf_wr_idx = RegInit("b0".asUInt(17.W))
//     val wt_local_data_cnt = wt_local_data_vld + dma_rd_rsp_mask(0)
//     //$mask == 1
//     val wt_local_data_vld_w = false.B
//     val wt_local_data_reg_en = false.B
//     wt_cbuf_wr_vld_w := wt_rsp_valid
//     val wt_local_data_w = "b0".asUInt(conf.ATMM.W)
//     val wt_cbuf_wr_data_ori_w = dma_rsp_data_p0

//     val wt_cbuf_wr_idx_inc = wt_cbuf_wr_idx + 1.U
//     val wt_cbuf_wr_idx_set = (layer_st & ~(wt_cbuf_wr_idx.orR))
    
//     val wt_cbuf_wr_idx_wrap = (wt_cbuf_wr_idx_inc === Cat(weight_bank_end, "b0".asUInt(conf.BANK_DEPTH_BITS.W)))
//     val wt_cbuf_wr_idx_w = Mux(clear_all | wt_cbuf_wr_idx_set | wt_cbuf_wr_idx_wrap, 
//                            Cat(data_bank_w, "b0".asUInt(conf.BANK_DEPTH_BITS.W)), wt_cbuf_wr_idx_inc)
//     val wt_cbuf_wr_data_w = wt_cbuf_wr_data_ori_w
//     when(wt_local_data_reg_en){
//         wt_local_data := wt_local_data_w
//     }
//     when(wt_rsp_valid){
//         wt_local_data_vld := wt_local_data_vld_w
//     }
//     when(wt_cbuf_wr_idx_set | clear_all | wt_cbuf_wr_vld_w){
//         wt_cbuf_wr_idx := wt_cbuf_wr_idx_w
//     }

// ////////////////////////////////////////////////////////////////////////
// //  weight buffer flushing logic                                      //
// ////////////////////////////////////////////////////////////////////////
// ////////////////////////////////////////////////////////////////////////
// //  Non-SLCG clock domain                                             //
// ////////////////////////////////////////////////////////////////////////
//     val wt_cbuf_flush_idx = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(18.W))}
//     val wt_cbuf_flush_idx_w = wt_cbuf_flush_idx + 1.U
//     val wt_cbuf_flush_vld_w = ~wt_cbuf_flush_idx(log2Ceil(conf.ATMC/conf.DMAIF) + log2Ceil(conf.NVDLA_CBUF_BANK_NUMBER*conf.NVDLA_CBUF_BANK_DEPTH)-1)

//     io.dp2reg_wt_flush_done := wt_cbuf_flush_idx(log2Ceil(conf.ATMC/conf.DMAIF) + log2Ceil(conf.NVDLA_CBUF_BANK_NUMBER*conf.NVDLA_CBUF_BANK_DEPTH)-1)

//     when(wt_cbuf_flush_vld_w){
//         wt_cbuf_flush_idx := wt_cbuf_flush_idx_w
//     }

//     val cdma2buf_wt_wr_en_w = wt_cbuf_wr_vld_w | wt_cbuf_flush_vld_w
//     val cdma2buf_wt_wr_addr_w = Wire(UInt(17.W))
//     val cdma2buf_wt_wr_sel_out = if(conf.DMAIF < conf.ATMC) 
//                                 Some(withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt((conf.ATMC/conf.DMAIF).W))})
//                                 else None
//     val cdma2buf_wt_wr_sel_w = if(conf.DMAIF < conf.ATMC) Some(Wire(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W)))
//                                else None 
//     if(conf.DMAIF < conf.ATMC){
//         cdma2buf_wt_wr_addr_w := Mux(wt_cbuf_wr_vld_w, wt_cbuf_wr_idx(16, log2Ceil(conf.ATMC/conf.DMAIF)),
//                                  (conf.NVDLA_CBUF_BANK_NUMBER * conf.NVDLA_CBUF_BANK_DEPTH / 2).U + wt_cbuf_flush_idx(16, log2Ceil(conf.ATMC/conf.DMAIF)))
//         cdma2buf_wt_wr_sel_w.get := Mux(wt_cbuf_wr_vld_w, wt_cbuf_wr_idx(log2Ceil(conf.ATMC/conf.DMAIF)-1, 0), wt_cbuf_flush_idx(log2Ceil(conf.ATMC/conf.DMAIF)-1, 0))
//         when(cdma2buf_wt_wr_en_w){
//             cdma2buf_wt_wr_sel_out.get := VecInit((0 to conf.ATMC/conf.DMAIF-1) map { i => (cdma2buf_wt_wr_sel_w.get === (conf.ATMC/conf.DMAIF).U)}).asUInt
//         }
//         io.cdma2buf_wt_wr_sel.get := cdma2buf_wt_wr_sel_out.get
//     }
//     else if(conf.DMAIF == conf.ATMC){
//         cdma2buf_wt_wr_addr_w := Mux(wt_cbuf_wr_vld_w, wt_cbuf_wr_idx(16, 0), 
//                                  (conf.NVDLA_CBUF_BANK_NUMBER * conf.NVDLA_CBUF_BANK_DEPTH / 2).U + wt_cbuf_flush_idx(16, 0))      
//     }

//     val cdma2buf_wt_wr_data_w = Mux(wt_cbuf_wr_vld_w, wt_cbuf_wr_data_w, 0.U)
//     io.cdma2buf_wt_wr_en := withClock(io.nvdla_core_ng_clk){RegNext(cdma2buf_wt_wr_en_w, false.B)}
//     io.cdma2buf_wt_wr_addr := withClock(io.nvdla_core_ng_clk){RegEnable(cdma2buf_wt_wr_addr_w, "b0".asUInt(17.W), cdma2buf_wt_wr_en_w)}
// ////////////////////////////////////////////////////////////////////////
// //  Non-SLCG clock domain end                                         //
// ////////////////////////////////////////////////////////////////////////
//     io.cdma2buf_wt_wr_data := RegEnable(cdma2buf_wt_wr_data_w, "b0".asUInt(conf.NVDLA_CDMA_DMAIF_BW.W), cdma2buf_wt_wr_en_w)
//     io.dp2reg_nan_weight_num := "b0".asUInt(32.W)
//     io.dp2reg_inf_weight_num := "b0".asUInt(32.W)
// ////////////////////////////////////////////////////////////////////////
// //  WT data status monitor                                            //
// ////////////////////////////////////////////////////////////////////////
// //================  Non-SLCG clock domain ================//
// //sc2cdma_wt_kernels are useless

// //retiming
//     sc_wt_updt := io.sc2cdma_wt_updt
//     when(io.sc2cdma_wt_updt){
//         sc_wt_entries := io.sc2cdma_wt_entries
//     }
// //cation: the basic unit of data_stored, data_onfly and data_avl is atomic_m bytes, 32 bytes in Xavier
//     val status_update = Wire(Bool())
//     val wt_data_stored_sub = Wire(UInt(17.W))
//     val wt_data_avl_sub = Wire(UInt(17.W))
//     val incr_wt_entries_w = Wire(UInt(15.W))
//     val wt_data_onfly_add = Mux(wt_req_reg_en_d2 & wt_req_stage_vld_d2 & ~wt_req_done_d2, wt_req_size_d2, "b0".asUInt(4.W))
//     val wt_data_onfly_sub = Mux(wt_cbuf_wr_vld_w, (conf.DMAIF/conf.ATMM).U, "b0".asUInt(3.W))
//     if(conf.ATMC/conf.ATMM == 1){
//         wt_data_stored_sub := Mux(status_update, incr_wt_entries_w, "b0".asUInt(17.W))
//         wt_data_avl_sub := Mux(sc_wt_updt, sc_wt_entries, "b0".asUInt(17.W))
//     }
//     else if(conf.ATMC/conf.ATMM == 2){
//         wt_data_stored_sub := Mux(status_update, Cat(incr_wt_entries_w, "b0".asUInt(1.W)), "b0".asUInt(17.W))
//         wt_data_avl_sub := Mux(sc_wt_updt,  Cat(sc_wt_entries, "b0".asUInt(1.W)), "b0".asUInt(17.W))       
//     }

//     val wt_data_onfly_w = wt_data_onfly + wt_data_onfly_add - wt_data_onfly_sub
//     val wt_data_stored_w = wt_data_stored + wt_data_onfly_sub - wt_data_stored_sub
//     val wt_data_avl_w = Mux(clear_all, 0.U, wt_data_avl + wt_data_stored_sub - wt_data_avl_sub)
//     val wt_data_onfly_reg_en = ((wt_req_reg_en_d2 & wt_req_stage_vld_d2) | wt_cbuf_wr_vld_w)

//     when(wt_data_onfly_reg_en){
//         wt_data_onfly := wt_data_onfly_w
//     }
//     when(wt_cbuf_wr_vld_w | status_update){
//         wt_data_stored := wt_data_stored_w
//     }
//     when(status_update | sc_wt_updt | clear_all){
//         wt_data_avl := wt_data_avl_w
//     }

// ////////////////////////////////////////////////////////////////////////
// //  status update logic                                               //
// ////////////////////////////////////////////////////////////////////////
//     val status_group_cnt = RegInit("b0".asUInt(12.W))
//     val pre_wt_required_bytes = RegInit("b0".asUInt(32.W))
//     val wt_fetched_cnt = RegInit("b0".asUInt(26.W))
//     val required_valid = RegInit(false.B)
//     val wt_required_bytes = RegInit("b0".asUInt(32.W))

//     val status_group_cnt_inc = status_group_cnt + 1.U
//     val status_last_group = (status_group_cnt_inc === group)
//     val status_group_cnt_w = Mux(layer_st, "b0".asUInt(12.W), status_group_cnt_inc)
//     val status_done_w = Mux(layer_st, false.B,
//                         Mux(status_last_group, true.B,
//                         status_done))
//     val normal_bpg = Cat(byte_per_kernel, "b0".asUInt(conf.ATMKBW.W))
//     val wt_required_bytes_w = Mux(layer_st, "b0".asUInt(32.W),
//                               Mux(status_last_group, io.reg2dp_weight_bytes,
//                               pre_wt_required_bytes + normal_bpg))
//     val required_valid_w = is_running & ~status_update
//     val wt_required_en = ~required_valid & required_valid_w
//     val pre_wt_required_bytes_w = Mux(layer_st, "b0".asUInt(32.W), wt_required_bytes)
    

//     required_valid := required_valid_w
//     when(layer_st | wt_required_en){
//         wt_required_bytes := wt_required_bytes_w
//     }
//     //////// caution: one in fetched_cnt refers to 64 bytes ////////
//     val wt_fetched_cnt_inc = wt_fetched_cnt + 1.U
//     val wt_fetched_cnt_w = Mux(layer_st, "b0".asUInt(26.W), wt_fetched_cnt_inc) 
//     val wt_satisfied = if(conf.ATMC > conf.DMAIF)
//                        is_running & (Cat(wt_fetched_cnt, "b0".asUInt(log2Ceil(conf.DMAIF).W)) >= wt_required_bytes) & 
//                        ~(wt_fetched_cnt(conf.ATMC/conf.DMAIF - 1, 0).orR)
//                        else
//                        is_running & (Cat(wt_fetched_cnt, "b0".asUInt(log2Ceil(conf.DMAIF).W)) >= wt_required_bytes)
//     status_update := Mux(~required_valid, false.B, wt_satisfied)

//     when(layer_st | status_update){
//         status_group_cnt := status_group_cnt_w
//         status_done := status_done_w
//         pre_wt_required_bytes := pre_wt_required_bytes_w
//     }
//     when(layer_st | wt_cbuf_wr_vld_w){
//         wt_fetched_cnt := wt_fetched_cnt_w
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //  avaliable kernels monitor                                         //
//     ////////////////////////////////////////////////////////////////////////
//     // Avaliable kernel size is useless here. Discard the code;

//     ////////////////////////////////////////////////////////////////////////
//     //  CDMA WT communicate to CSC                                        //
//     ////////////////////////////////////////////////////////////////////////
//     val incr_wt_updt = RegInit(false.B)
//     val pre_wt_fetched_cnt = RegInit("b0".asUInt(26.W))
//     val incr_wt_entries = RegInit("b0".asUInt(15.W))
//     val incr_wt_kernels = RegInit("b0".asUInt(6.W))

//     val pre_wt_fetched_cnt_w = Mux(status_last_group, "b0".asUInt(26.W), wt_fetched_cnt)
//     val incr_wt_cnt =  wt_fetched_cnt - pre_wt_fetched_cnt
//     if(conf.DMAIF == conf.ATMC){
//         incr_wt_entries_w := incr_wt_cnt(14, 0)
//     }
//     else if(conf.DMAIF < conf.ATMC){
//         incr_wt_entries_w := incr_wt_cnt(14+log2Ceil(conf.ATMC/conf.DMAIF), log2Ceil(conf.ATMC/conf.DMAIF))
//     }

//     val incr_wt_kernels_w = Mux(~status_last_group, conf.NVDLA_MAC_ATOMIC_K_SIZE.U, 
//                             io.reg2dp_weight_kernel(conf.ATMKBW-1, 0) +& 1.U)

//     incr_wt_updt := status_update
//     when(status_update){
//         pre_wt_fetched_cnt := pre_wt_fetched_cnt_w
//         incr_wt_entries := incr_wt_entries_w
//         incr_wt_kernels := incr_wt_kernels_w
//     }

//     val incr_wt_updt_d = Wire(Bool()) +: 
//                          Seq.fill(conf.CDMA_CBUF_WR_LATENCY)(RegInit(false.B))
//     val incr_wt_kernels_d = Wire(UInt(6.W)) +: 
//                             Seq.fill(conf.CDMA_CBUF_WR_LATENCY)(RegInit("b0".asUInt(6.W)))
//     val incr_wt_entries_d = Wire(UInt(15.W)) +: 
//                             Seq.fill(conf.CDMA_CBUF_WR_LATENCY)(RegInit("b0".asUInt(15.W)))
//     incr_wt_updt_d(0) := incr_wt_updt
//     incr_wt_kernels_d(0) := incr_wt_kernels
//     incr_wt_entries_d(0) := incr_wt_entries

//     for(t <- 0 to conf.CDMA_CBUF_WR_LATENCY-1){
//         incr_wt_updt_d(t+1) := incr_wt_updt_d(t)
//         when(incr_wt_updt_d(t)){
//             incr_wt_entries_d(t+1) := incr_wt_entries_d(t)
//             incr_wt_kernels_d(t+1) := incr_wt_kernels_d(t)
//         }
//     }

//     io.cdma2sc_wt_kernels := incr_wt_kernels_d(conf.CDMA_CBUF_WR_LATENCY)
//     io.cdma2sc_wt_entries := incr_wt_entries_d(conf.CDMA_CBUF_WR_LATENCY) 
//     io.cdma2sc_wmb_entries := "b0".asUInt(12.W)
//     io.cdma2sc_wt_updt := incr_wt_updt_d(conf.CDMA_CBUF_WR_LATENCY) 

// ////////////////////////////////////////////////////////////////////////
// // performance counting register //
// ////////////////////////////////////////////////////////////////////////  
//     val wt_rd_stall_inc = RegInit(false.B)
//     val wt_rd_stall_clr = RegInit(false.B)
//     val wt_rd_stall_cen = RegInit(false.B)
//     val dp2reg_wt_rd_stall_dec = false.B

//     wt_rd_stall_inc := dma_rd_req_vld & ~dma_rd_req_rdy & io.reg2dp_dma_en
//     wt_rd_stall_clr := io.status2dma_fsm_switch & io.reg2dp_dma_en
//     wt_rd_stall_cen := io.reg2dp_op_en & io.reg2dp_dma_en
//     // stl adv logic
//     val stl_adv = wt_rd_stall_inc ^ dp2reg_wt_rd_stall_dec

//     // stl cnt logic
//     val stl_cnt_cur = RegInit("b0".asUInt(32.W))
//     val stl_cnt_ext = Wire(UInt(34.W))
//     val stl_cnt_inc = Wire(UInt(34.W))
//     val stl_cnt_dec = Wire(UInt(34.W))
//     val stl_cnt_mod = Wire(UInt(34.W))
//     val stl_cnt_new = Wire(UInt(34.W))
//     val stl_cnt_nxt = Wire(UInt(34.W))

//     stl_cnt_ext := stl_cnt_cur
//     stl_cnt_inc := stl_cnt_cur +& 1.U
//     stl_cnt_dec := stl_cnt_cur -& 1.U
//     stl_cnt_mod := Mux(wt_rd_stall_inc && !dp2reg_wt_rd_stall_dec, stl_cnt_inc, 
//                    Mux(!wt_rd_stall_inc && dp2reg_wt_rd_stall_dec, stl_cnt_dec,
//                    stl_cnt_ext))
//     stl_cnt_new := Mux(stl_adv, stl_cnt_mod, stl_cnt_ext)
//     stl_cnt_nxt := Mux(wt_rd_stall_clr, 0.U, stl_cnt_new)

//     // stl flops
//     when(wt_rd_stall_cen){
//         stl_cnt_cur := stl_cnt_nxt
//     }
//    // stl output logic
//     val wt_rd_latency_inc = RegInit(false.B)
//     val wt_rd_latency_dec = RegInit(false.B)
//     val wt_rd_latency_clr = RegInit(false.B)
//     val wt_rd_latency_cen = RegInit(false.B)

//     io.dp2reg_wt_rd_stall := stl_cnt_cur

//     wt_rd_latency_inc := dma_rd_req_vld & dma_rd_req_rdy & io.reg2dp_dma_en
//     wt_rd_latency_dec := dma_rsp_fifo_ready & io.reg2dp_dma_en
//     wt_rd_latency_clr := io.status2dma_fsm_switch
//     wt_rd_latency_cen := io.reg2dp_op_en & io.reg2dp_dma_en

//     val outs_dp2reg_wt_rd_latency = Wire(UInt(9.W))

//     val ltc_1_inc = (outs_dp2reg_wt_rd_latency =/=511.U) & wt_rd_latency_inc
//     val ltc_1_dec = (outs_dp2reg_wt_rd_latency =/=511.U) & wt_rd_latency_dec

//     // ltc_1 adv logic
//     val ltc_1_adv = ltc_1_inc ^ ltc_1_dec

//     // ltc_1 cnt logic
//     val ltc_1_cnt_cur = RegInit("b0".asUInt(9.W))
//     val ltc_1_cnt_ext = Wire(UInt(11.W))
//     val ltc_1_cnt_inc = Wire(UInt(11.W))
//     val ltc_1_cnt_dec = Wire(UInt(11.W))
//     val ltc_1_cnt_mod = Wire(UInt(11.W))
//     val ltc_1_cnt_new = Wire(UInt(11.W))
//     val ltc_1_cnt_nxt = Wire(UInt(11.W))

//     ltc_1_cnt_ext := ltc_1_cnt_cur
//     ltc_1_cnt_inc := ltc_1_cnt_cur +& 1.U
//     ltc_1_cnt_dec := ltc_1_cnt_cur -& 1.U
//     ltc_1_cnt_mod := Mux(ltc_1_inc && !ltc_1_dec, ltc_1_cnt_inc, 
//                      Mux((!ltc_1_inc && ltc_1_dec), ltc_1_cnt_dec,
//                      ltc_1_cnt_ext))
//     ltc_1_cnt_new := Mux(ltc_1_adv, ltc_1_cnt_mod, ltc_1_cnt_ext)
//     ltc_1_cnt_nxt := Mux(wt_rd_latency_clr, 0.U, ltc_1_cnt_new)

//     // ltc_1 flops
//     when(wt_rd_latency_cen){
//         ltc_1_cnt_cur := ltc_1_cnt_nxt
//     }

//     // ltc_1 output logic
//     outs_dp2reg_wt_rd_latency := ltc_1_cnt_cur

//     io.dp2reg_wt_rd_latency := 0.U

// }}




// class NV_NVDLA_CDMA_WT_8ATMM_fifo(fifo_width:Int, fifo_depth:Int) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         val lat_wr_prdy = Output(Bool())
//         val lat_wr_pvld = Input(Bool())
//         val lat_wr_pd = Input(UInt(fifo_width.W))
//         val lat_rd_prdy = Input(Bool())
//         val lat_rd_pvld = Output(Bool())
//         val lat_rd_pd = Output(UInt(fifo_width.W))

//         val pwrbus_ram_pd = Input(UInt(32.W))
//     })
//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │
//     //       │                 │
//     //       └───┐         ┌───┘
//     //           │         │
//     //           │         │
//     //           │         │
//     //           │         └──────────────┐
//     //           │                        │
//     //           │                        ├─┐
//     //           │                        ┌─┘    
//     //           │                        │
//     //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //             │ ─┤ ─┤       │ ─┤ ─┤         
//     //             └──┴──┘       └──┴──┘
//     withClock(io.nvdla_core_clk){
//     // Master Clock Gating (SLCG)
//     //
//     // We gate the clock(s) when idle or stalled.
//     // This allows us to turn off numerous miscellaneous flops
//     // that don't get gated during synthesis for one reason or another.
//     //
//     // We gate write side and read side separately. 
//     // If the fifo is synchronous, we also gate the ram separately, but if
//     // -master_clk_gated_unified or -status_reg/-status_logic_reg is specified, 
//     // then we use one clk gate for write, ram, and read.
//     //
//     val nvdla_core_clk_mgated_enable = Wire(Bool())
//     val nvdla_core_clk_mgate = Module(new NV_CLK_gate_power)
//     nvdla_core_clk_mgate.io.clk := io.nvdla_core_clk
//     nvdla_core_clk_mgate.io.clk_en := nvdla_core_clk_mgated_enable
//     val nvdla_core_clk_mgated = nvdla_core_clk_mgate.io.clk_gated

//     ////////////////////////////////////////////////////////////////////////
//     // WRITE SIDE                                                        //
//     ////////////////////////////////////////////////////////////////////////
//     val wr_reserving = Wire(Bool())
//     val lat_wr_busy_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // copy for internal use
//     io.lat_wr_prdy := !lat_wr_busy_int
//     wr_reserving := io.lat_wr_pvld && !lat_wr_busy_int   // reserving write space?

//     val wr_popping = withClock(nvdla_core_clk_mgated){RegInit(false.B)}       // fwd: write side sees pop?
//     val lat_wr_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt((log2Ceil(fifo_depth)+1).W))} // write-side count
//     val wr_count_next_wr_popping = Mux(wr_reserving, lat_wr_count, lat_wr_count-1.U)
//     val wr_count_next_no_wr_popping = Mux(wr_reserving, lat_wr_count+1.U, lat_wr_count)
//     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

//     val wr_count_next_no_wr_popping_is_fifo_depth = (wr_count_next_no_wr_popping === fifo_depth.U)
//     val wr_count_next_is_fifo_depth = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_fifo_depth)
//     val wr_limit_muxed = Wire(UInt((log2Ceil(fifo_depth)+1).W))    // muxed with simulation/emulation overrides
//     val wr_limit_reg = wr_limit_muxed
//     val lat_wr_busy_next = wr_count_next_is_fifo_depth ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

//     lat_wr_busy_int := lat_wr_busy_next
//     when(wr_reserving ^ wr_popping){
//         lat_wr_count := wr_count_next
//     }

//     val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

//     //
//     // RAM
//     //  

//     val lat_wr_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(log2Ceil(fifo_depth).W))}   // current write address
//     val lat_rd_adr_p = Wire(UInt(log2Ceil(fifo_depth).W));		// read address to use for ram
//     val lat_rd_pd_p = Wire(UInt(fifo_width.W));		// read data directly out of ram
//     val rd_enable = Wire(Bool())
//     val ore = Wire(Bool())

//     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
//     // Fifogen handles this by ignoring the data on the ram data out for that cycle.

//     val ram = Module(new nv_ram_rwsp(fifo_depth, fifo_width))
//     ram.io.clk := io.nvdla_core_clk
//     ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ram.io.wa := lat_wr_adr
//     ram.io.we := wr_pushing
//     ram.io.di := io.lat_wr_pd 
//     ram.io.ra := lat_rd_adr_p
//     ram.io.re := rd_enable
//     ram.io.ore := ore
//     lat_rd_pd_p := ram.io.dout
    
//     // next wr_adr if wr_pushing=1
//     val wr_adr_next = lat_wr_adr + 1.U
//     when(wr_pushing){
//         lat_wr_adr  := wr_adr_next
//     }

//     val rd_popping = Wire(Bool())   // read side doing pop this cycle?
//     val lat_rd_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(log2Ceil(fifo_depth).W))}  // current read address
//     val rd_adr_next = lat_rd_adr + 1.U
//     lat_rd_adr_p := Mux(rd_popping, rd_adr_next, lat_rd_adr) // for ram
//     when(rd_popping){
//         lat_rd_adr := rd_adr_next
//     }


//     //
//     // SYNCHRONOUS BOUNDARY
//     //
//     wr_popping := rd_popping    
//     val rd_pushing = withClock(nvdla_core_clk_mgated){RegNext(wr_pushing, false.B)} 

//     //
//     // READ SIDE
//     //
//     val lat_rd_pvld_p = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // data out of fifo is valid
//     val lat_rd_pvld_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)} // internal copy of rd_req
//     io.lat_rd_pvld := lat_rd_pvld_int
//     rd_popping := lat_rd_pvld_p && !(lat_rd_pvld_int && !io.lat_rd_prdy)

//     val lat_rd_count_p = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt((log2Ceil(fifo_depth)+1).W))} // read-side fifo count
//     val rd_count_p_next_rd_popping = Mux(rd_pushing, lat_rd_count_p, lat_rd_count_p-1.U)
//     val rd_count_p_next_no_rd_popping = Mux(rd_pushing, lat_rd_count_p + 1.U, lat_rd_count_p)
//     val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)

//     val rd_count_p_next_rd_popping_not_0 = rd_count_p_next_rd_popping =/= 0.U
//     val rd_count_p_next_no_rd_popping_not_0 = rd_count_p_next_no_rd_popping =/= 0.U
//     val rd_count_p_next_not_0 = Mux(rd_popping, rd_count_p_next_rd_popping_not_0, rd_count_p_next_no_rd_popping_not_0)

//     rd_enable := ((rd_count_p_next_not_0) && ((~lat_rd_pvld_p) || rd_popping));  // anytime data's there and not stalled

    
//     when(rd_pushing || rd_popping){
//         lat_rd_count_p := rd_count_p_next
//         lat_rd_pvld_p := rd_count_p_next_not_0
//     }
    
//     val rd_req_next = (lat_rd_pvld_p || (lat_rd_pvld_int && !io.lat_rd_prdy))

//     lat_rd_pvld_int := rd_req_next

//     io.lat_rd_pd := lat_rd_pd_p
//     ore := rd_popping

//     nvdla_core_clk_mgated_enable := ((wr_reserving || wr_pushing || rd_popping || 
//                         wr_popping || (io.lat_wr_pvld && !lat_wr_busy_int) || 
//                         (lat_wr_busy_int =/= lat_wr_busy_next)) || (rd_pushing || rd_popping || 
//                         (lat_rd_pvld_int && io.lat_rd_prdy) || wr_pushing))

//     wr_limit_muxed := "d0".asUInt((log2Ceil(fifo_depth)+1).W)

    
// }}


// object NV_NVDLA_CDMA_wtDriver extends App {
//   implicit val conf: cdmaConfiguration = new cdmaConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_wt())
// }


// object NV_NVDLA_CDMA_WT_8ATMM_fifoDriver extends App {
//   implicit val conf: cdmaConfiguration = new cdmaConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_WT_8ATMM_fifo(257, 8))
// }
