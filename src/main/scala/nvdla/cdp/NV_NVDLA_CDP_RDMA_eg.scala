// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_RDMA_eg(implicit val conf: cdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val reg2dp_channel = Input(UInt(5.W))
//         val reg2dp_input_data = Input(UInt(2.W))
//         val reg2dp_src_ram_type = Input(Bool())
//         val dp2reg_done = Output(Bool())
//         val eg2ig_done = Output(Bool())

//         val mcif2cdp_rd_rsp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_DMAIF_BW+(conf.NVDLA_CDP_DMAIF_BW/(conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE))).W)))

//         val cdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

//         val cvif2cdp_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//         val cvif2cdp_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None 
//         val cvif2cdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_DMAIF_BW+(conf.NVDLA_CDP_DMAIF_BW/(conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE))).W)))) else None
//         val cdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

//         val cdp_rdma2dp_valid = Output(Bool())
//         val cdp_rdma2dp_ready = Input(Bool())
//         val cdp_rdma2dp_pd = Output(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+25).W))

//         val cq_rd_pvld = Input(Bool())
//         val cq_rd_prdy = Output(Bool())
//         val cq_rd_pd = Input(UInt(7.W))

//         val pwrbus_ram_pd = Input(UInt(32.W))

//     })

// withClock(io.nvdla_core_clk){

//     val dma_rd_rsp_rdy = Wire(Bool())

//     val nv_NVDLA_CDP_RDMA_rdrsp = Module(new NV_NVDLA_DMAIF_rdrsp(conf.NVDLA_CDP_DMAIF_BW+conf.ATMM_NUM))
//     nv_NVDLA_CDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk

//     nv_NVDLA_CDP_RDMA_rdrsp.io.mcif_rd_rsp_pd <> io.mcif2cdp_rd_rsp_pd

//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         nv_NVDLA_CDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get <> io.cvif2cdp_rd_rsp_pd.get
//     }

//     val dma_rd_rsp_pd = nv_NVDLA_CDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.bits
//     val dma_rd_rsp_vld = nv_NVDLA_CDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.valid
//     nv_NVDLA_CDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.ready := dma_rd_rsp_rdy

//     val cdp2mcif_rd_cdt_lat_fifo_pop = RegInit(false.B)
//     val dma_rd_cdt_lat_fifo_pop = Wire(Bool())
//     val dma_rd_rsp_type = io.reg2dp_src_ram_type

//     cdp2mcif_rd_cdt_lat_fifo_pop := dma_rd_cdt_lat_fifo_pop & (dma_rd_rsp_type === true.B)

//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         val cdp2cvif_rd_cdt_lat_fifo_pop = RegInit(false.B)
//         cdp2cvif_rd_cdt_lat_fifo_pop := dma_rd_cdt_lat_fifo_pop & (dma_rd_rsp_type === false.B)
//     }

// //==============
// // Latency FIFO to buffer return DATA
// //==============

//     val lat_rd_prdy = Wire(Bool())
//     val u_lat_fifo = Module{new NV_NVDLA_fifo(
//                         depth = conf.NVDLA_VMOD_CDP_RDMA_LATENCY_FIFO_DEPTH, 
//                         width = conf.NVDLA_CDP_DMAIF_BW + conf.ATMM_NUM,
//                         ram_type = 0, 
//                         distant_wr_req = false)}
//     u_lat_fifo.io.clk := io.nvdla_core_clk
//     u_lat_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_lat_fifo.io.wr_pvld := dma_rd_rsp_vld
//     dma_rd_rsp_rdy := u_lat_fifo.io.wr_prdy
//     u_lat_fifo.io.wr_pd := dma_rd_rsp_pd
//     val lat_rd_pvld = u_lat_fifo.io.rd_pvld 
//     u_lat_fifo.io.rd_prdy := lat_rd_prdy
//     val lat_rd_pd  = u_lat_fifo.io.rd_pd 

//     val lat_rd_data = lat_rd_pd(conf.NVDLA_CDP_DMAIF_BW-1, 0)
//     val lat_rd_mask = lat_rd_pd(conf.NVDLA_DMA_RD_RSP-1, conf.NVDLA_CDP_DMAIF_BW)

//     dma_rd_cdt_lat_fifo_pop := lat_rd_pvld & lat_rd_prdy

// //==============
// // Re-Order FIFO to send data to CDP-core in DP order(read NVDLA PP uARCH for details)
// //==============
//     val ro_wr_rdy = Wire(Vec(conf.ATMM_NUM, Bool()))
//     val lat_rd_mask_func = VecInit(
//             (0 until conf.NVDLA_DMA_MASK_BIT) map 
//             {i => ~lat_rd_mask(i) | (lat_rd_mask(i) & ro_wr_rdy(i))}
//             )
//     lat_rd_prdy := lat_rd_pvld & lat_rd_mask_func.asUInt.andR

//     val ro_wr_pvld = Wire(Vec(conf.ATMM_NUM, Bool()))
//     for(i <- 0 until conf.ATMM_NUM){
//         ro_wr_pvld(i) := lat_rd_pvld & lat_rd_mask_func.asUInt.andR & lat_rd_mask(i)
//     }
// // (lat_rd_mask[${i}] & ro${i}_wr_rdy) = (~lat_rd_mask[${i}] | (lat_rd_mask[${i}] & ro${i}_wr_rdy)) & lat_rd_mask[${i}]

//     val ro_wr_rdys = Wire(Vec(conf.ATMM_NUM, UInt((conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT).W)))

// // (conf.NVDLA_CDP_DMAIF_BW/(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE)) / 
// // (conf.NVDLA_CDP_DMAIF_BW/(conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE))
//     val ro_wr_pd = Wire(Vec(conf.ATMM_NUM, UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE).W)))
//     val u_ro_fifo = Array.fill(
//         (conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT)*(conf.ATMM_NUM+1)
//         ){Module(new NV_NVDLA_fifo(
//                     depth = conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE, 
//                     width = 32,
//                     ram_type = 0, 
//                     distant_wr_req = false))}
//     val ro_rd_pvld = Wire(Vec(conf.ATMM_NUM, Bool()))
//     val ro_rd_prdy = Wire(Vec(conf.ATMM_NUM, Bool()))
//     val ro_rd_pd = Wire(Vec(conf.ATMM_NUM, UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE).W)))
    
//     for(i <- 0 until conf.ATMM_NUM){
//         ro_wr_rdy(i) := ro_wr_rdys(i).asUInt.andR
//         for(j <- 0 until (conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT)){
//             val k = (conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT) * i + j
//             ro_wr_pd(k) := lat_rd_data(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE*(k+1)-1, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE*k)
//             u_ro_fifo(k).io.clk := io.nvdla_core_clk
//             u_ro_fifo(k).io.pwrbus_ram_pd := io.pwrbus_ram_pd
//             u_ro_fifo(k).io.wr_pvld := ro_wr_pvld(i)
//             ro_wr_rdys(j)(i) := u_ro_fifo(k).io.wr_prdy
//             u_ro_fifo(k).io.wr_pd := ro_wr_pd(k)
//             ro_rd_pvld(k) := u_ro_fifo(k).io.rd_pvld
//             u_ro_fifo(k).io.rd_prdy := ro_rd_prdy(k)
//             ro_rd_pd(k) := u_ro_fifo(k).io.rd_pd
//         }
//     }

//     val fbit = log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT)
//     val tran_cnt_idle = Wire(Bool())
//     val tran_cnt = RegInit(0.U(6.W))
//     when(conf.ATMM_NUM > 1){
//         val beat_align = RegInit(0.U(log2Ceil(conf.ATMM_NUM).W))
//     }.elsewhen{
//         val beat_align = RegInit(false.B)
//     }

//     val fifo_sel = Wire(UInt(6.W))
//     if(conf.ATMM_NUM > 1){
//         fifo_sel := Mux(
//             tran_cnt_idle, 
//             0.U, 
//             (((conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT).asUInt(6.W)-tran_cnt) 
//             + Cat(beat_align, Fill(log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT), false.B)))
//             )
//     } else {
//         fifo_sel := Mux(
//             tran_cnt_idle,
//             0.U,
//             ((conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT).asUInt(6.W)-tran_cnt) 
//         )
//     }

//     // DATA MUX out : not sure
//     val dp_rdy = Wire(Bool())
//     val dp_vld = RegInit(false.B)
//     val dp_data = RegInit(0.U((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE).W))
//     for(i <- 0 until conf.ATMM_NUM){
//         for(j <- 0 until (conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT)){
//             when(fifo_sel === ((conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT)*i+j).asUInt(6.W)){
//                 dp_vld := ro_rd_pvld((conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT)*i+j) & (~tran_cnt_idle)
//             }
//             ro_rd_prdy((conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT)*i+j) := dp_rdy & (fifo_sel===((conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT)*i+j).asUInt) & (~tran_cnt_idle)
//         }
//     }

//     for(i <- 0 until conf.ATMM_NUM){
//         when(fifo_sel === i.asUInt(6.W)){
//             dp_data := ro_rd_pd(i)
//         }
//     }

// //////////////////////
// //replacd by 0 value in invalid position
// //////////////////////
//     val ele_in_channel = Wire(UInt(5.W))
//     if(log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)==5){
//         ele_in_channel := io.reg2dp_channel(log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)-1, 0)
//     } else {
//         ele_in_channel := Cat(
//             Fill(5-log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE), false.B),
//             io.reg2dp_channel(log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)-1, 0)
//         )
//     }

//     val rest_channel = 
//         (conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT).asUInt(6.W) - 
//         ele_in_channel(log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)-1, log2Ceil(conf.NVDLA_CDP_THROUGHPUT))
    
//     val is_last_c = RegInit(false.B)
//     val invalid_flag = RegInit(0.U(conf.NVDLA_CDP_THROUGHPUT.W))

//     for(i <- 0 until conf.ATMM_NUM){
//         when(fifo_sel === i.asUInt(6.W)){
//             when(is_last_c){
//                 when(tran_cnt < rest_channel){
//                     invalid_flag := Fill(conf.NVDLA_CDP_THROUGHPUT, true.B)
//                 }.elsewhen(tran_cnt > rest_channel){
//                     invalid_flag := 0.U
//                 }.otherwise{
//                     if(conf.NVDLA_CDP_THROUGHPUT==1){
//                         invalid_flag := 0.U
//                     } else {
//                         invalid_flag := 0.U 
//                             // Fill(conf.NVDLA_CDP_THROUGHPUT, (ele_in_channel===0.U)) & Cat(Fill(conf.NVDLA_CDP_THROUGHPUT-1, true.B), false.B)
// // print invalid_flag = {${tp}{ele_in_channel[${tpbw}-1:0]==${tpbw}'d0}} & {{(${tp}-1){1'b1}},1'b0}
// // foreach my $i (0..$tp-2) {
// // my $j = $i + 1;
// // my $s = $j + 1;
// // if($s < $tp){
// // print  | {${tp}{ele_in_channel[${tpbw}-1:0]==${tpbw}'d${j}}} & {{(${tp}-${s}){1'b1}},${s}'b0}
// // } else {
// // print  | {${tp}{ele_in_channel[${tpbw}-1:0]==${tpbw}'d${j}}} & {${s}'b0} 
// // }                
//                     }
//                 }
//             }.otherwise{
//                 invalid_flag := 0.U
//             }
//         }
//     }

// //==============
// // Return Data Counting
// //==============
// // unpack from rd_pd, which should be the same order as wr_pd
//     val tran_rdy = Wire(Bool())
//     io.cq_rd_prdy := tran_rdy
//     val tran_vld = io.cq_rd_pvld

//     val ig2eg_width = io.cq_rd_pd(2,0)
//     val ig2eg_align   =     io.cq_rd_pd(3)
//     val ig2eg_last_w  =     io.cq_rd_pd(4)
//     val ig2eg_last_h  =     io.cq_rd_pd(5)
//     val ig2eg_last_c  =     io.cq_rd_pd(6)

//     val tran_num = ig2eg_width +& 1.U
//     tran_cnt_idle := (tran_cnt===0.U)
//     val is_last_tran  = (tran_cnt===1.U)
//     val beat_cnt = RegInit(0.U(4.W))
//     val is_last_beat  = (beat_cnt===1.U)

//     val fifo_rd_pvld = Wire(Vec(conf.ATMM_NUM, Bool()))
//     for(i <- 0 until conf.ATMM_NUM){
//         fifo_rd_pvld(i) := (fifo_sel===i.asUInt(6.W)) & ro_rd_pvld(i)
//     }
//     val fifo_rd_pvld_active = fifo_rd_pvld.asUInt.andR

// //the first cq_rd_prdy should start when fifo have data to be read

//     tran_rdy := (tran_cnt_idle & fifo_rd_pvld_active/*(|fifo_rd_pvld)*/) || (is_last_tran & is_last_beat & dp_rdy)
//     val tran_accept = tran_vld & tran_rdy
//     val is_cube_end = Wire(Bool())
//     val width_cnt = RegInit(0.U(4.W))

//     when(is_cube_end & tran_rdy){
//         tran_cnt := 0.U
//         beat_cnt := 0.U
//     }.elsewhen(tran_rdy){
//         when(tran_vld){
//             tran_cnt := (conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT).asUInt(6.W)
//             beat_cnt := tran_num
//         }.otherwise{
//             tran_cnt := 0.U
//             beat_cnt := 0.U
//         }
//     }.elsewhen(dp_rdy & fifo_rd_pvld_active){
//         beat_cnt := Mux((beat_cnt===1.U), width_cnt, (beat_cnt - 1.U))
//         when(is_last_beat){
//             tran_cnt := tran_cnt - 1.U
//         }
//     }

//     when(tran_rdy){
//         beat_align := 0.U
//     }.elsewhen(dp_rdy & fifo_rd_pvld_active){
//         when(is_last_beat){
//             beat_align := 0.U
//         }.otherwise{
//             beat_align := beat_align + true.B
//         }
//     }

//     when(tran_accept === true.B){
//         width_cnt := tran_num
//     }

//     val is_last_w = RegInit(false.B)
//     val is_last_h = RegInit(false.B)
//     when(is_cube_end & tran_rdy){
//         is_last_w := false.B
//         is_last_h := false.B
//         is_last_c := false.B
//     }.elsewhen(tran_accept){
//         is_last_w := ig2eg_last_w
//         is_last_h := ig2eg_last_h
//         is_last_c := ig2eg_last_c
//     }

//     val is_b_sync = is_last_beat

//     val dp_pos_w = width_cnt - beat_cnt //spyglass disable W484
//     val dp_width = width_cnt //spyglass disable W484

//     val dp_pos_c = (conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT).asUInt(6.W) - tran_cnt
//     val dp_b_sync  = is_b_sync
//     val dp_last_w = is_last_w
//     val dp_last_h = is_last_h
//     val dp_last_c = is_last_c

//     is_cube_end := is_last_w & is_last_h & is_last_c
//     val dp2reg_done_f = is_cube_end & tran_rdy
//     val eg2ig_done_f  = is_cube_end & tran_rdy

// //==============
// // OUTPUT PACK and PIPE: To Data Processor
// //==============
// // PD Pack
//     val dp_invalid = Wire(UInt(8.W))
//     if(conf.NVDLA_CDP_THROUGHPUT==8){
//         dp_invalid := invalid_flag
//     } else{
//         dp_invalid := Cat(Fill(8-conf.NVDLA_CDP_THROUGHPUT, false.B), invalid_flag)
//     }

// // PKT_PACK_WIRE( cdp_rdma2dp , dp_ , dp_pd )
//     val dp_pd = Wire(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+25).W))
//     dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE-1,0) := dp_data
//     dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+3,conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE) := dp_pos_w
//     dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+7,conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+4) := dp_width
//     dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+12,conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+8) := dp_pos_c(4,0)
//     dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+13) := dp_b_sync
//     dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+14) := dp_last_w
//     dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+15) := dp_last_h
//     dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+16) := dp_last_c
//     dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+24,conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+17) := dp_invalid(7,0)

//     val cdp_rdma2dp_pd_i = Cat(dp_pd, dp2reg_done_f, eg2ig_done_f)

// //: my $k=NVDLA_CDP_THROUGHPUT*NVDLA_BPE+27;
// //: &eperl::pipe(" -wid $k -is -do cdp_rdma2dp_pd_o -vo cdp_rdma2dp_valid_f -ri cdp_rdma2dp_ready -di cdp_rdma2dp_pd_i  -vi dp_vld -ro dp_rdy_f ");
//     val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+27))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := dp_vld
//     val dp_rdy_f = pipe_p1.io.ro
//     pipe_p1.io.di := cdp_rdma2dp_pd_i
//     val cdp_rdma2dp_valid_f = pipe_p1.io.vo
//     pipe_p1.io.ri := io.cdp_rdma2dp_ready
//     val cdp_rdma2dp_pd_o = pipe_p1.io.dout

//     dp_rdy := dp_rdy_f

//     io.cdp_rdma2dp_pd := cdp_rdma2dp_pd_o(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+26, 2)
//     val dp2reg_done_flag = cdp_rdma2dp_pd_o(1)
//     val eg2ig_done_flag = cdp_rdma2dp_pd_o(0)

//     io.cdp_rdma2dp_valid := cdp_rdma2dp_valid_f
//     io.dp2reg_done := Mux((cdp_rdma2dp_valid_f & io.cdp_rdma2dp_ready & dp2reg_done_flag), true.B, false.B)
//     io.eg2ig_done  := Mux((cdp_rdma2dp_valid_f & io.cdp_rdma2dp_ready & eg2ig_done_flag), true.B, false.B)

// //==============
// //function points
// //==============
// // ...

// }}


// object NV_NVDLA_CDP_RDMA_egDriver extends App {
//     implicit val conf: cdpConfiguration = new cdpConfiguration
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_RDMA_eg())
// }
