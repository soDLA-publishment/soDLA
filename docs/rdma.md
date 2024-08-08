## Read DMA for Weight

### Weight Read DMA (WT_RDMA)

A Read DMA is to send a request for read to AXI bus, and provide the corresponding value from AXI bus directly. A Read DMA(RDMA) for reading weight doesn't require data formats such as image or dc use. So I introduce wt_rdma first. Weights in Neural networks oftenly don't require to read from AXI bus multiple times, so mostly, weights is cached locally. Weight can be cached in cbuf, csc_wt, cmac activate stage. After weight is read from AXI bus, it would be stored in cbuf stage directly. 
```
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
        .otherwise{ nxt_state := sIdle }
        }
        is (sPend) {
        when (pending_req_end) { nxt_state := sBusy }
        .otherwise{ nxt_state := sPend }
        }
        is (sBusy) {
        when (fetch_done) { nxt_state := sDone }
        .otherwise{ nxt_state := sBusy }
        }
        is (sDone) {
        when (io.status2dma_fsm_switch) { nxt_state := sIdle }
        .otherwise{ nxt_state := sDone }
        }
    }
    
    cur_state := nxt_state
```
line 90-123: This is a common 4-state FSM setup within all nvdla design, the remove pending request is generated from csc module. After wdma is pending state, it will tell csc module that wdma is in the pending state. 

```
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
```
line 124-142: After the definition of FSM, is the input signal and output signal definitions. In FSM input signals, status_done is a up counter for 8 ticks. 
As for the pending judgement, if the register value has changed, then it is pending. 
In the layer start and running state, the status keep counting.

```
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
```

line 143-167: This is the code blocks for FSM out signals. There are some indicators for the next states, which are to make the timing logic more compact. The clear_all is to show that register is configuring the weight, so to clear all.



