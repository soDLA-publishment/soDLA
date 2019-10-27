package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_RDMA_eg(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val mcif2pdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_RSP.W)))
        val pdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

        val cvif2pdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_RSP.W)))) else None
        val pdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

        val pdp_rdma2dp_pd = DecoupledIO(UInt((conf.PDPBW+14).W))
        val cq2eg_pd = Flipped(DecoupledIO(UInt(18.W)))

        val reg2dp_src_ram_type = Input(Bool())
        val dp2reg_done = Output(Bool())
        val eg2ig_done = Output(Bool())
        val rdma2wdma_done = Output(Bool())

        val pwrbus_ram_pd = Input(UInt(32.W))

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

    ///////////////////////////////////////////////////////////////////////////////////
    val nv_NVDLA_PDP_RDMA_rdrsp = Module(new NV_NVDLA_DMAIF_rdrsp(conf.NVDLA_PDP_MEM_RD_RSP))
    nv_NVDLA_PDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk

    nv_NVDLA_PDP_RDMA_rdrsp.io.mcif_rd_rsp_pd <> io.mcif2pdp_rd_rsp_pd

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        nv_NVDLA_PDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get <> io.cvif2pdp_rd_rsp_pd.get
    }

    ////////////////////////////////////
    val dma_rd_cdt_lat_fifo_pop = Wire(Bool())
    val dma_rd_rsp_ram_type = io.reg2dp_src_ram_type
    io.pdp2mcif_rd_cdt_lat_fifo_pop := RegNext(dma_rd_cdt_lat_fifo_pop & (dma_rd_rsp_ram_type === true.B), false.B)

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.pdp2cvif_rd_cdt_lat_fifo_pop.get := RegNext(dma_rd_cdt_lat_fifo_pop & (dma_rd_rsp_ram_type === false.B), false.B)
    }

    //pipe for timing closure
    val is_pipe_egg = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_PDP_MEM_RD_RSP))
    is_pipe_egg.io.clk := io.nvdla_core_clk

    is_pipe_egg.io.vi := nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.valid
    nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.ready := is_pipe_egg.io.ro
    is_pipe_egg.io.di := nv_NVDLA_PDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.bits

    //==============
    // Latency FIFO to buffer return DATA
    //==============

    val lat_rd_prdy = Wire(Bool())
    val u_lat_fifo = Module{new NV_NVDLA_fifo(
                        depth = conf.NVDLA_VMOD_PDP_RDMA_LATENCY_FIFO_DEPTH, 
                        width = conf.NVDLA_PDP_MEM_RD_RSP,
                        ram_type = 2, 
                        distant_wr_req = false)}
    u_lat_fifo.io.clk := io.nvdla_core_clk
    u_lat_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_lat_fifo.io.wr_pvld := is_pipe_egg.io.vo
    is_pipe_egg.io.ri := u_lat_fifo.io.wr_prdy
    u_lat_fifo.io.wr_pd := is_pipe_egg.io.dout

    val lat_rd_pvld = u_lat_fifo.io.rd_pvld 
    u_lat_fifo.io.rd_prdy := lat_rd_prdy
    val lat_rd_pd  = u_lat_fifo.io.rd_pd 

    val lat_rd_data = lat_rd_pd(conf.NVDLA_PDP_DMAIF_BW-1, 0)
    val lat_rd_mask = lat_rd_pd(conf.NVDLA_DMA_RD_RSP-1, conf.NVDLA_PDP_DMAIF_BW)

    dma_rd_cdt_lat_fifo_pop := lat_rd_pvld & lat_rd_prdy

    // only care the rdy of ro-fifo which mask bit indidates
    val ro_wr_rdy = Wire(Vec(conf.ATMM_NUM, Bool()))
    val lat_rd_mask_func = VecInit((0 to conf.ATMM_NUM-1) map 
            {i => ~lat_rd_mask(i) | (lat_rd_mask(i) & ro_wr_rdy(i))})
    lat_rd_prdy := lat_rd_pvld & lat_rd_mask_func.asUInt.andR

    // when also need send to other group of ro-fifo, need clamp the vld if others are not ready
    val ro_wr_pvld = Wire(Vec(conf.ATMM_NUM, Bool()))
    for(i <- 0 until conf.ATMM_NUM){
        ro_wr_pvld(i) := lat_rd_pvld & (lat_rd_mask(i) & ro_wr_rdy(i)) & lat_rd_mask_func.drop(i).reduce(_ && _)
    }

    val ro_wr_rdys = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_PDP_NUM, Bool())))
    for(i <- 0 to conf.ATMM_NUM-1){
        ro_wr_rdy(i) := ro_wr_rdys(i).asUInt.andR
    }

    val u_ro_fifo = Array.fill(conf.TOTAL_PDP_NUM){
                    Module(new NV_NVDLA_fifo(
                    depth = 32, 
                    width = conf.PDPBW, 
                    ram_type = 2, 
                    distant_wr_req = false))}

    val ro_wr_pd = VecInit((0 to conf.TOTAL_PDP_NUM-1) 
                    map {i => lat_rd_data((conf.PDPBW*i + conf.PDPBW - 1), conf.PDPBW*i)})
    val ro_rd_pvld = Wire(Vec(conf.TOTAL_PDP_NUM, Bool()))
    val ro_rd_prdy = Wire(Vec(conf.TOTAL_PDP_NUM, Bool()))
    val ro_rd_pd = Wire(Vec(conf.TOTAL_PDP_NUM, UInt(conf.PDPBW.W)))
    for(i <- 0 to conf.ATMM_NUM-1){
        for(j <- 0 to conf.BATCH_PDP_NUM-1){

                u_ro_fifo(i*conf.BATCH_PDP_NUM + j).io.clk := io.nvdla_core_clk
                u_ro_fifo(i*conf.BATCH_PDP_NUM + j).io.pwrbus_ram_pd := io.pwrbus_ram_pd

                u_ro_fifo(i*conf.BATCH_PDP_NUM + j).io.wr_pvld := ro_wr_pvld(i)
                ro_wr_rdys(i)(j) := u_ro_fifo(i*conf.BATCH_PDP_NUM + j).io.wr_prdy
                u_ro_fifo(i*conf.BATCH_PDP_NUM + j).io.wr_pd := ro_wr_pd(i*conf.BATCH_PDP_NUM + j)

                ro_rd_pvld(i*conf.BATCH_PDP_NUM + j) := u_ro_fifo(i*conf.BATCH_PDP_NUM + j).io.rd_pvld
                u_ro_fifo(i*conf.BATCH_PDP_NUM + j).io.rd_prdy := ro_rd_prdy(i*conf.BATCH_PDP_NUM + j)
                ro_rd_pd(i*conf.BATCH_PDP_NUM + j) := u_ro_fifo(i*conf.BATCH_PDP_NUM + j).io.rd_pd
        }
    }

    // DATA MUX out
    val fifo_sel_cnt = RegInit("b0".asUInt(6.W))
    val fifo_sel = fifo_sel_cnt

    val dp_vld = WireInit(false.B)
    val dp_rdy = Wire(Bool())
    val dp_data = WireInit("b0".asUInt(conf.PDPBW.W))
    val tran_cnt_idle = Wire(Bool())

    for(i <- 0 to conf.TOTAL_PDP_NUM-1){
        when(fifo_sel === i.U){
            dp_vld := ro_rd_pvld(i) & (~tran_cnt_idle)
            dp_data := ro_rd_pd(i)
        }

        ro_rd_prdy(i) := dp_rdy & (fifo_sel === i.U) & (~tran_cnt_idle)
    }

    //==============
    // Context Queue: read
    //==============

    //==============
    // Return Data Counting
    //==============
    // unpack from rd_pd, which should be the same order as wr_pd
    val tran_rdy = Wire(Bool())
    io.cq2eg_pd.ready := tran_rdy
    val tran_vld = io.cq2eg_pd.valid

    // PKT_UNPACK_WIRE( pdp_rdma_ig2eg ,  ig2eg_ ,  cq2eg_pd )
    val ig2eg_size = io.cq2eg_pd.bits(12, 0)
    val ig2eg_align = io.cq2eg_pd.bits(13)
    val ig2eg_line_end = io.cq2eg_pd.bits(14)
    val ig2eg_surf_end = io.cq2eg_pd.bits(15)
    val ig2eg_split_end = io.cq2eg_pd.bits(16)
    val ig2eg_cube_end = io.cq2eg_pd.bits(17)

    val tran_num = Mux(io.cq2eg_pd.valid, ig2eg_size +& 1.U, "b0".asUInt(14.W))

    val tran_cnt = RegInit("b0".asUInt(6.W))
    val beat_cnt = RegInit("b0".asUInt(14.W))
    val width_cnt = RegInit("b0".asUInt(14.W))
    tran_cnt_idle := (tran_cnt === 0.U);
    val is_last_tran = (tran_cnt === 1.U);
    val is_last_beat = (beat_cnt === 1.U);
    
    val fifo_rd_pvld = Wire(Vec(conf.TOTAL_PDP_NUM, Bool()))
    for(i <- 0 to conf.TOTAL_PDP_NUM-1){
        fifo_rd_pvld(i) := (fifo_sel === i.U) & ro_rd_pvld(i)
    }

    val fifo_rd_pvld_active = fifo_rd_pvld.asUInt.orR

    tran_rdy := (tran_cnt_idle & fifo_rd_pvld_active) || (is_last_tran & is_last_beat & dp_rdy);
    val tran_accept = tran_vld & tran_rdy; 

    val is_cube_end = RegInit(false.B)
    val is_b_sync = Wire(Bool())
    when(is_cube_end & is_b_sync){
        fifo_sel_cnt := 0.U
    }
    .elsewhen(tran_rdy){
        fifo_sel_cnt := 0.U
    }
    .elsewhen(dp_rdy & fifo_rd_pvld_active){
        fifo_sel_cnt := Mux(fifo_sel_cnt === (conf.TOTAL_PDP_NUM-1).U, 0.U, fifo_sel_cnt+1.U)
    }

    when(is_cube_end & is_b_sync){
        tran_cnt := 0.U
        beat_cnt := 0.U
    }
    .elsewhen(tran_rdy){
        when(tran_vld){
            tran_cnt := conf.BATCH_PDP_NUM.U
            beat_cnt := tran_num
        }
        .otherwise{
            tran_cnt := 0.U
            beat_cnt := 0.U
        }
    }
    .elsewhen(dp_rdy & fifo_rd_pvld_active){
        beat_cnt := Mux(beat_cnt === 1.U, width_cnt, beat_cnt - 1.U)
        when(is_last_beat){
            tran_cnt := tran_cnt - 1.U
        }
    }

    val is_line_end = RegInit(false.B)
    val is_surf_end = RegInit(false.B)
    val is_split_end = RegInit(false.B)
    when(tran_accept){
        width_cnt := tran_num
        is_line_end := ig2eg_line_end
        is_surf_end := ig2eg_surf_end
        is_split_end := ig2eg_split_end
        is_cube_end := ig2eg_cube_end
    }

    is_b_sync := is_last_beat & is_last_tran & dp_rdy;
    val dp_pos_w = (width_cnt -& beat_cnt)(3, 0)
    val dp_pos_c = Wire(UInt(5.W))
    dp_pos_c := fifo_sel(log2Ceil(conf.BATCH_PDP_NUM)-1, 0)

    val dp_b_sync  = is_b_sync;
    val dp_line_end = is_line_end;
    val dp_surf_end = is_surf_end;
    val dp_split_end = is_split_end;
    val dp_cube_end = is_cube_end;

    val dp2reg_done_f = is_cube_end & is_b_sync
    val eg2ig_done_f = is_cube_end & is_b_sync
    val rdma2wdma_done_f = is_cube_end & is_b_sync

    //==============
    // OUTPUT PACK and PIPE: To Data Processor
    //==============
    // PD Pack

    // PKT_PACK_WIRE( pdp_rdma2dp , dp_ , dp_pd )
    val dp_pd = Cat(dp_cube_end, dp_split_end, dp_surf_end, dp_line_end, dp_b_sync, 
                    dp_pos_c, dp_pos_w, dp_data)
    val eg_out_pipe0_di = Cat(dp_pd, rdma2wdma_done_f, eg2ig_done_f, dp2reg_done_f)

    val is_pipe0 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT+17))
    is_pipe0.io.clk := io.nvdla_core_clk

    is_pipe0.io.vi := dp_vld
    dp_rdy := is_pipe0.io.ro
    is_pipe0.io.di := eg_out_pipe0_di

    io.pdp_rdma2dp_pd.valid := is_pipe0.io.vo
    is_pipe0.io.ri := io.pdp_rdma2dp_pd.ready
    val eg_out_pipe0_do = is_pipe0.io.dout

    io.pdp_rdma2dp_pd.bits := eg_out_pipe0_do(conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT+16, 3)
    val rdma2wdma_done_flag = eg_out_pipe0_do(2)
    val eg2ig_done_flag = eg_out_pipe0_do(1)
    val dp2reg_done_flag = eg_out_pipe0_do(0)

    io.rdma2wdma_done := Mux(io.pdp_rdma2dp_pd.valid & io.pdp_rdma2dp_pd.ready & rdma2wdma_done_flag, true.B, false.B)
    io.eg2ig_done := Mux(io.pdp_rdma2dp_pd.valid & io.pdp_rdma2dp_pd.ready & eg2ig_done_flag, true.B, false.B)
    io.dp2reg_done := Mux(io.pdp_rdma2dp_pd.valid & io.pdp_rdma2dp_pd.ready & dp2reg_done_flag, true.B, false.B)

}}


object NV_NVDLA_PDP_RDMA_egDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_RDMA_eg())
}
