package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_CDP_RDMA_eg(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val mcif2cdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDP_MEM_RD_RSP.W)))
        val cdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

        val cvif2cdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDP_MEM_RD_RSP.W)))) else None
        val cdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

        val cdp_rdma2dp_pd = DecoupledIO(UInt((conf.CDPBW+25).W))

        val cq_rd_pd = Flipped(DecoupledIO(UInt(7.W)))

        val reg2dp_channel = Input(UInt(5.W))
        val reg2dp_input_data = Input(UInt(2.W))
        val reg2dp_src_ram_type = Input(Bool())
        val dp2reg_done = Output(Bool())
        val eg2ig_done = Output(Bool())

    })

withClock(io.nvdla_core_clk){

    val nv_NVDLA_CDP_RDMA_rdrsp = Module(new NV_NVDLA_DMAIF_rdrsp(conf.NVDLA_CDP_MEM_RD_RSP))
    nv_NVDLA_CDP_RDMA_rdrsp.io.nvdla_core_clk := io.nvdla_core_clk

    nv_NVDLA_CDP_RDMA_rdrsp.io.mcif_rd_rsp_pd <> io.mcif2cdp_rd_rsp_pd

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        nv_NVDLA_CDP_RDMA_rdrsp.io.cvif_rd_rsp_pd.get <> io.cvif2cdp_rd_rsp_pd.get
    }

    val dma_rd_cdt_lat_fifo_pop = Wire(Bool())

    io.cdp2mcif_rd_cdt_lat_fifo_pop := RegNext(dma_rd_cdt_lat_fifo_pop & (io.reg2dp_src_ram_type === true.B), false.B)
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdp2cvif_rd_cdt_lat_fifo_pop.get := RegNext(dma_rd_cdt_lat_fifo_pop & (io.reg2dp_src_ram_type === false.B), false.B)
    }

    //==============
    // Latency FIFO to buffer return DATA
    //==============
    val lat_rd_prdy = Wire(Bool())
    val u_lat_fifo = Module{new NV_NVDLA_fifo(
                        depth = conf.NVDLA_VMOD_CDP_RDMA_LATENCY_FIFO_DEPTH, 
                        width = conf.NVDLA_CDP_MEM_RD_RSP,
                        ram_type = 0, 
                        distant_wr_req = false)}
    u_lat_fifo.io.clk := io.nvdla_core_clk
    u_lat_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_lat_fifo.io.wr_pvld := nv_NVDLA_CDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.valid
    nv_NVDLA_CDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.ready := u_lat_fifo.io.wr_prdy
    u_lat_fifo.io.wr_pd := nv_NVDLA_CDP_RDMA_rdrsp.io.dmaif_rd_rsp_pd.bits

    val lat_rd_pvld = u_lat_fifo.io.rd_pvld 
    u_lat_fifo.io.rd_prdy := lat_rd_prdy
    val lat_rd_pd  = u_lat_fifo.io.rd_pd 

    val lat_rd_data = lat_rd_pd(conf.NVDLA_CDP_DMAIF_BW-1, 0)
    val lat_rd_mask = lat_rd_pd(conf.NVDLA_CDP_MEM_RD_RSP-1, conf.NVDLA_CDP_DMAIF_BW)

    dma_rd_cdt_lat_fifo_pop := lat_rd_pvld & lat_rd_prdy

    //==============
    // Re-Order FIFO to send data to CDP-core in DP order(read NVDLA PP uARCH for details)
    //==============
    val ro_wr_rdy = Wire(Vec(conf.ATMM_NUM, Bool()))

    val lat_rd_mask_func = VecInit((0 until conf.ATMM_NUM) map 
            {i => (~lat_rd_mask(i) | (lat_rd_mask(i) & ro_wr_rdy(i)))})

    lat_rd_prdy := lat_rd_pvld & lat_rd_mask_func.asUInt.andR

    val ro_wr_pvld = Wire(Vec(conf.ATMM_NUM, Bool()))
    for(i <- 0 until conf.ATMM_NUM){
        ro_wr_pvld(i) := lat_rd_pvld & (lat_rd_mask(i) & ro_wr_rdy(i)) & 
                         lat_rd_mask_func.drop(i).reduce(_ && _)
    }

    val ro_wr_rdys = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_CDP_NUM, Bool())))
    val ro_wr_pd = VecInit((0 to conf.TOTAL_CDP_NUM-1) 
                    map {i => lat_rd_data((conf.PDPBW*i + conf.PDPBW - 1), conf.PDPBW*i)})
    val ro_rd_pvld = Wire(Vec(conf.TOTAL_CDP_NUM, Bool()))
    val ro_rd_prdy = Wire(Vec(conf.TOTAL_CDP_NUM, Bool()))
    val ro_rd_pd = Wire(Vec(conf.TOTAL_CDP_NUM, UInt((conf.CDPBW).W)))
    val u_ro_fifo = Array.fill(conf.TOTAL_CDP_NUM){Module(new NV_NVDLA_fifo(
                    depth = conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE, 
                    width = 32,
                    ram_type = 0, 
                    distant_wr_req = false))}

    
    for(i <- 0 until conf.ATMM_NUM){
        ro_wr_rdy(i) := ro_wr_rdys(i).asUInt.andR
        for(j <- 0 until conf.BATCH_CDP_NUM){
                u_ro_fifo(i*conf.BATCH_CDP_NUM + j).io.clk := io.nvdla_core_clk
                u_ro_fifo(i*conf.BATCH_CDP_NUM + j).io.pwrbus_ram_pd := io.pwrbus_ram_pd

                u_ro_fifo(i*conf.BATCH_CDP_NUM + j).io.wr_pvld := ro_wr_pvld(i)
                ro_wr_rdys(i)(j) := u_ro_fifo(i*conf.BATCH_CDP_NUM + j).io.wr_prdy
                u_ro_fifo(i*conf.BATCH_CDP_NUM + j).io.wr_pd := ro_wr_pd(i*conf.BATCH_CDP_NUM + j)

                ro_rd_pvld(i*conf.BATCH_CDP_NUM + j) := u_ro_fifo(i*conf.BATCH_CDP_NUM + j).io.rd_pvld
                u_ro_fifo(i*conf.BATCH_CDP_NUM + j).io.rd_prdy := ro_rd_prdy(i*conf.BATCH_CDP_NUM + j)
                ro_rd_pd(i*conf.BATCH_CDP_NUM + j) := u_ro_fifo(i*conf.BATCH_CDP_NUM + j).io.rd_pd
        }
    }

    val tran_cnt_idle = Wire(Bool())
    val tran_cnt = RegInit(0.U(6.W))
    val beat_align = RegInit(0.U(conf.ATMM_NUM.W))
    val fifo_sel = Wire(UInt(6.W))
    if(conf.ATMM_NUM > 1){
        fifo_sel := Mux(tran_cnt_idle, 0.U, 
            conf.ATMM_NUM.U -& tran_cnt +& Cat(beat_align, Fill(log2Ceil(conf.ATMM_NUM), false.B)))
    } 
    else {
        fifo_sel := Mux(tran_cnt_idle, 0.U, conf.ATMM_NUM.U -& tran_cnt)
    }

    // DATA MUX out 
    val dp_rdy = Wire(Bool())
    val dp_vld = WireInit(false.B)
    val dp_data = WireInit(0.U(conf.CDPBW.W))
    for(i <- 0 until conf.TOTAL_CDP_NUM){
        when(fifo_sel === i.U){
            dp_vld := ro_rd_pvld(i) & (~tran_cnt_idle)
        }
        ro_rd_prdy(i) := dp_rdy & (fifo_sel === i.U) & (~tran_cnt_idle)
    }

    for(i <- 0 until conf.ATMM_NUM){
        when(fifo_sel === i.U){
            dp_data := ro_rd_pd(i)
        }
    }

    //////////////////////
    //replacd by 0 value in invalid position
    //////////////////////
    val ele_in_channel = Wire(UInt(5.W))
    ele_in_channel := io.reg2dp_channel(conf.ATMMBW-1, 0)

    val rest_channel = conf.BATCH_CDP_NUM.U -& 
ele_in_channel(conf.ATMMBW-1, conf.CDP_TPBW)
    
    val is_last_c = RegInit(false.B)
    val invalid_flag = WireInit(0.U(conf.NVDLA_CDP_THROUGHPUT.W))

    for(i <- 0 until conf.TOTAL_CDP_NUM){
        when(fifo_sel === i.U){
            when(is_last_c){
                when(tran_cnt < rest_channel){
                    invalid_flag := Fill(conf.NVDLA_CDP_THROUGHPUT, true.B)
                }.elsewhen(tran_cnt > rest_channel){
                    invalid_flag := 0.U
                }.otherwise{
                    if(conf.NVDLA_CDP_THROUGHPUT == 1){
                        invalid_flag := VecInit((0 to conf.NVDLA_CDP_THROUGHPUT-1) map 
                    { j => Fill(conf.NVDLA_CDP_THROUGHPUT, ele_in_channel(conf.ATMM_NUM-1, 0) === j.U) & 
                    ((Fill(conf.NVDLA_CDP_THROUGHPUT, true.B) << (j+1).U)(conf.NVDLA_CDP_THROUGHPUT-1, 0))}).reduce(_|_)
                    } else {
                        invalid_flag := 0.U 
                    }
                }
            }.otherwise{
                invalid_flag := 0.U
            }
        }
    }

    //==============
    // Return Data Counting
    //==============
    // unpack from rd_pd, which should be the same order as wr_pd
    val tran_rdy = Wire(Bool())
    io.cq_rd_pd.ready := tran_rdy
    val tran_vld = io.cq_rd_pd.valid

    val ig2eg_width = io.cq_rd_pd.bits(2,0)
    val ig2eg_align  = io.cq_rd_pd.bits(3)
    val ig2eg_last_w  =  io.cq_rd_pd.bits(4)
    val ig2eg_last_h  =  io.cq_rd_pd.bits(5)
    val ig2eg_last_c  =  io.cq_rd_pd.bits(6)

    val beat_cnt = RegInit(0.U(4.W))
    val tran_num = ig2eg_width +& 1.U
    tran_cnt_idle := (tran_cnt===0.U)
    val is_last_tran = (tran_cnt===1.U)
    val is_last_beat  = (beat_cnt===1.U)

    val fifo_rd_pvld = Wire(Vec(conf.TOTAL_CDP_NUM, Bool()))
    for(i <- 0 until conf.TOTAL_CDP_NUM){
        fifo_rd_pvld(i) := (fifo_sel === i.U) & ro_rd_pvld(i)
    }
    val fifo_rd_pvld_active = fifo_rd_pvld.asUInt.orR

    //the first cq_rd_prdy should start when fifo have data to be read

    tran_rdy := (tran_cnt_idle & fifo_rd_pvld_active) || (is_last_tran & is_last_beat & dp_rdy)
    val tran_accept = tran_vld & tran_rdy
    val is_cube_end = Wire(Bool())
    val width_cnt = RegInit(0.U(4.W))

    when(is_cube_end & tran_rdy){
        tran_cnt := 0.U
        beat_cnt := 0.U
    }.elsewhen(tran_rdy){
        when(tran_vld){
            tran_cnt := conf.BATCH_CDP_NUM.U
            beat_cnt := tran_num
        }.otherwise{
            tran_cnt := 0.U
            beat_cnt := 0.U
        }
    }.elsewhen(dp_rdy & fifo_rd_pvld_active){
        beat_cnt := Mux((beat_cnt===1.U), width_cnt, (beat_cnt - 1.U))
        when(is_last_beat){
            tran_cnt := tran_cnt - 1.U
        }
    }

    when(tran_rdy){
        beat_align := 0.U
    }.elsewhen(dp_rdy & fifo_rd_pvld_active){
        when(is_last_beat){
            beat_align := 0.U
        }.otherwise{
            beat_align := beat_align + 1.U
        }
    }

    when(tran_accept){
        width_cnt := tran_num
    }

    val is_last_w = RegInit(false.B)
    val is_last_h = RegInit(false.B)
    when(is_cube_end & tran_rdy){
        is_last_w := false.B
        is_last_h := false.B
        is_last_c := false.B
    }.elsewhen(tran_accept){
        is_last_w := ig2eg_last_w
        is_last_h := ig2eg_last_h
        is_last_c := ig2eg_last_c
    }

    val is_b_sync = is_last_beat

    val dp_pos_w = width_cnt -& beat_cnt //spyglass disable W484
    val dp_width = width_cnt //spyglass disable W484

    val dp_pos_c = conf.BATCH_CDP_NUM.U -& tran_cnt
    val dp_b_sync  = is_b_sync
    val dp_last_w = is_last_w
    val dp_last_h = is_last_h
    val dp_last_c = is_last_c

    is_cube_end := is_last_w & is_last_h & is_last_c
    val dp2reg_done_f = is_cube_end & tran_rdy
    val eg2ig_done_f  = is_cube_end & tran_rdy

    //==============
    // OUTPUT PACK and PIPE: To Data Processor
    //==============
    // PD Pack
    val dp_invalid = Wire(UInt(8.W))
    dp_invalid := invalid_flag

    // PKT_PACK_WIRE( cdp_rdma2dp , dp_ , dp_pd )
    val dp_pd = Cat(dp_invalid(7,0), dp_last_c, dp_last_h, dp_last_w, dp_b_sync, dp_pos_c(4,0) ,dp_width, dp_pos_w, dp_data)

    val cdp_rdma2dp_pd_i = Cat(dp_pd, dp2reg_done_f, eg2ig_done_f)

    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+27))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := dp_vld
    val dp_rdy_f = pipe_p1.io.ro
    pipe_p1.io.di := cdp_rdma2dp_pd_i
    val cdp_rdma2dp_valid_f = pipe_p1.io.vo
    pipe_p1.io.ri := io.cdp_rdma2dp_pd.ready
    val cdp_rdma2dp_pd_o = pipe_p1.io.dout

    dp_rdy := dp_rdy_f

    io.cdp_rdma2dp_pd.bits := cdp_rdma2dp_pd_o(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_BPE+26, 2)
    val dp2reg_done_flag = cdp_rdma2dp_pd_o(1)
    val eg2ig_done_flag = cdp_rdma2dp_pd_o(0)

    io.cdp_rdma2dp_pd.valid := cdp_rdma2dp_valid_f
    io.dp2reg_done := Mux((cdp_rdma2dp_valid_f & io.cdp_rdma2dp_pd.ready & dp2reg_done_flag), true.B, false.B)
    io.eg2ig_done  := Mux((cdp_rdma2dp_valid_f & io.cdp_rdma2dp_pd.ready & eg2ig_done_flag), true.B, false.B)


}}


object NV_NVDLA_CDP_RDMA_egDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_RDMA_eg())
}
