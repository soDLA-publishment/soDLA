package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_wdma(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk_orig = Input(Clock())
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        // dp2wdma
        val cdp_dp2wdma_pd = Flipped(DecoupledIO(UInt((conf.CDPBW+17).W)))

        // cdp2mcif_wr
        val cdp2mcif_wr_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))
        val mcif2cdp_wr_rsp_complete = Input(Bool())

        // cdp2cvif_wr
        val cdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))) else None
        val cvif2cdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cdp2glb_done_intr_pd = Output(UInt(2.W))

        // config
        val reg2dp_dma_en = Input(Bool())
        val reg2dp_dst_base_addr_high = Input(UInt(32.W))
        val reg2dp_dst_base_addr_low = Input(UInt(32.W))
        val reg2dp_dst_line_stride = Input(UInt(32.W))
        val reg2dp_dst_ram_type = Input(Bool())
        val reg2dp_dst_surface_stride = Input(UInt(32.W))
        val reg2dp_interrupt_ptr = Input(Bool())
        val reg2dp_op_en = Input(Bool())
        val dp2reg_d0_perf_write_stall = Output(UInt(32.W))
        val dp2reg_d1_perf_write_stall = Output(UInt(32.W))
        val dp2reg_done = Output(Bool())

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
    ///////////////////////////////////////////////////////////////////////////////////////
    //==============
    // Work Processing
    //==============
    val op_prcess = RegInit(false.B)
    val op_load = io.reg2dp_op_en & !op_prcess;
    val is_last_beat = Wire(Bool())
    val reg_cube_last = RegInit(false.B)
    val dat_accept = Wire(Bool())
    val op_done = reg_cube_last & is_last_beat & dat_accept
    io.dp2reg_done := op_done

    when(op_load){
        op_prcess := true.B
    }
    .elsewhen(op_done){
        op_prcess := false.B
    }

    //==============
    // Data INPUT pipe and Unpack
    //==============
    val dp2wdma_rdy = Wire(Bool())
    val pipe_0 = Module(new NV_NVDLA_IS_pipe(conf.CDPBW+17))
    pipe_0.io.clk := io.nvdla_core_clk
    pipe_0.io.vi := io.cdp_dp2wdma_pd.valid
    io.cdp_dp2wdma_pd.ready := pipe_0.io.ro
    pipe_0.io.di := io.cdp_dp2wdma_pd.bits

    val dp2wdma_vld = pipe_0.io.vo
    pipe_0.io.ri := dp2wdma_rdy
    val dp2wdma_pd = pipe_0.io.dout

    val dp2wdma_data = dp2wdma_pd(conf.CDPBW-1, 0)
    val dp2wdma_pos_w = dp2wdma_pd(conf.CDPBW+3, conf.CDPBW)
    val dp2wdma_width = dp2wdma_pd(conf.CDPBW+7, conf.CDPBW+4)
    val dp2wdma_pos_c = dp2wdma_pd(conf.CDPBW+12, conf.CDPBW+8)
    val dp2wdma_b_sync = dp2wdma_pd(conf.CDPBW+13)
    val dp2wdma_last_w = dp2wdma_pd(conf.CDPBW+14)
    val dp2wdma_last_h = dp2wdma_pd(conf.CDPBW+15)
    val dp2wdma_last_c = dp2wdma_pd(conf.CDPBW+16)
    
    val dp2wdma_cmd_pd = Cat(dp2wdma_last_c, dp2wdma_last_h, dp2wdma_last_w, dp2wdma_b_sync, 
                             dp2wdma_pos_c, dp2wdma_width, dp2wdma_pos_w, dp2wdma_data)
    
    ///////////////////////////////////////////////////////
    // when b_sync, both cmd.fifo and dat.fifo need be ready, when !b_sync, only dat.fifo need be ready
    val dat_fifo_wr_rdy = Wire(Bool())
    val cmd_fifo_wr_prdy = Wire(Bool())
    dp2wdma_rdy := dp2wdma_vld & Mux(dp2wdma_b_sync, (dat_fifo_wr_rdy & cmd_fifo_wr_prdy), dat_fifo_wr_rdy);

    //==============
    // Input FIFO : DATA and its swizzle
    //==============
    val dp2wdma_pos_w_bit0 = Wire(UInt(4.W))

    if(conf.ATMM_NUM == 1){
        dp2wdma_pos_w_bit0 := false.B
    }
    else if(conf.ATMM_NUM == 2){
        dp2wdma_pos_w_bit0 := dp2wdma_pos_w(0)
    }
    else if(conf.ATMM_NUM == 4){
        dp2wdma_pos_w_bit0 := dp2wdma_pos_w(1, 0)
    }
    else if(conf.ATMM_NUM == 8){
        dp2wdma_pos_w_bit0 := dp2wdma_pos_w(2, 0)
    }
    else if(conf.ATMM_NUM == 16){
        dp2wdma_pos_w_bit0 := dp2wdma_pos_w(3, 0)
    }

    val req_chn_size = RegInit("b0".asUInt(5.W))
    val u_dat_fifo = Array.fill(conf.ATMM_NUM){Array.fill(conf.BATCH_CDP_NUM){Module(new NV_NVDLA_fifo(depth = 32, width = conf.CDPBW, ram_type = 0, distant_wr_req = false))}}
    val dat_fifo_wr_pvld = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_CDP_NUM, Bool())))
    val dat_fifo_wr_prdy = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_CDP_NUM, Bool())))
    val dat_wr_rdys = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_CDP_NUM, Bool())))
    val dat_fifo_rd_pvld = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_CDP_NUM, Bool())))
    val dat_fifo_rd_prdy = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_CDP_NUM, Bool())))
    val dat_fifo_rd_pd = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_CDP_NUM, UInt(conf.CDPBW.W))))
    val dat_rdy_per_batch = Wire(Vec(conf.ATMM_NUM, Bool()))
    for(i <- 0 to conf.ATMM_NUM-1){
        for(j <- 0 to conf.BATCH_CDP_NUM-1){
            u_dat_fifo(i)(j).io.clk := io.nvdla_core_clk
            u_dat_fifo(i)(j).io.pwrbus_ram_pd := io.pwrbus_ram_pd

            u_dat_fifo(i)(j).io.wr_pvld := dat_fifo_wr_pvld(i)(j)
            dat_fifo_wr_prdy(i)(j) := u_dat_fifo(i)(j).io.wr_prdy 
            u_dat_fifo(i)(j).io.wr_pd := dp2wdma_pd  

            dat_fifo_rd_pvld(i)(j) := u_dat_fifo(i)(j).io.rd_pvld
            u_dat_fifo(i)(j).io.rd_prdy := dat_fifo_rd_prdy(i)(j)
            dat_fifo_rd_pd(i)(j) := u_dat_fifo(i)(j).io.rd_pd

            dat_fifo_rd_prdy(i)(j) := dat_rdy_per_batch(i) & (j.U <= req_chn_size)

            dat_fifo_wr_pvld(i)(j) := ((!dp2wdma_b_sync) || (dp2wdma_b_sync & cmd_fifo_wr_prdy)) & dp2wdma_vld & (dp2wdma_pos_c === j.U) & (dp2wdma_pos_w_bit0 === i.U)
            dat_wr_rdys(i)(j) := dat_fifo_rd_prdy(i)(j) & (dp2wdma_pos_c === j.U) & (dp2wdma_pos_w_bit0 === i.U)
        }
    }

    dat_fifo_wr_rdy := dat_wr_rdys.asUInt.orR
    val dat_fifo_rd_pvld_per_batch = Wire(Vec(conf.ATMM_NUM, Bool()))
    for(i <- 0 to conf.ATMM_NUM-1){
        dat_fifo_rd_pvld_per_batch(i) := dat_fifo_rd_pvld(i).asUInt.andR
    }

    val dat_data = dat_fifo_rd_pd.asUInt

    //==============
    // Input FIFO: CMD
    //==============
    // cmd-fifo control
    // if b_sync, need push into both dat_fifo and cmd_fifo
    val cmd_fifo_wr_pvld = dp2wdma_vld & (dp2wdma_b_sync & (dp2wdma_pos_c === (conf.BATCH_CDP_NUM-1).U)) & dat_fifo_wr_rdy;
    val cmd_fifo_wr_pd   = dp2wdma_cmd_pd

    // FIFO:Write side
    val cmd_fifo_rd_prdy = Wire(Bool())
    val u_cmd_fifo = Module(new NV_NVDLA_fifo(depth = 4, width = 17, ram_type = 0, distant_wr_req = false))
    u_cmd_fifo.io.clk := io.nvdla_core_clk
    u_cmd_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_cmd_fifo.io.wr_pvld := cmd_fifo_wr_pvld
    cmd_fifo_wr_prdy := u_cmd_fifo.io.wr_prdy
    u_cmd_fifo.io.wr_pd := cmd_fifo_wr_pd

    val cmd_fifo_rd_pvld = u_cmd_fifo.io.rd_pvld
    u_cmd_fifo.io.rd_prdy := cmd_fifo_rd_prdy
    val cmd_fifo_rd_pd = u_cmd_fifo.io.rd_pd

    val cmd_en = RegInit(true.B)
    val cmd_rdy = Wire(Bool())
    cmd_fifo_rd_prdy := cmd_en & cmd_rdy

    // Unpack cmd & data together
    val cmd_fifo_rd_pos_w = cmd_fifo_rd_pd(3, 0)
    val cmd_fifo_rd_width = cmd_fifo_rd_pd(7, 4)
    val cmd_fifo_rd_pos_c = cmd_fifo_rd_pd(12, 8)
    val cmd_fifo_rd_b_sync = cmd_fifo_rd_pd(13)
    val cmd_fifo_rd_last_w = cmd_fifo_rd_pd(14)
    val cmd_fifo_rd_last_h = cmd_fifo_rd_pd(15)
    val cmd_fifo_rd_last_c = cmd_fifo_rd_pd(16)

    val cmd_fifo_rd_b_sync_NC = cmd_fifo_rd_b_sync;
    val is_last_w = cmd_fifo_rd_last_w
    val is_last_h = cmd_fifo_rd_last_h
    val is_last_c = cmd_fifo_rd_last_c
    val is_cube_last = is_last_w & is_last_h & is_last_c

    //==============
    // BLOCK Operation
    //==============
    val cmd_vld = cmd_en & cmd_fifo_rd_pvld
    val dma_wr_req_rdy = Wire(Bool())
    cmd_rdy := dma_wr_req_rdy
    val cmd_accept = cmd_vld & cmd_rdy

    val is_beat_num_odd = RegInit("b0".asUInt(4.W))
    when(cmd_accept){
        if(conf.BATCH_CDP_NUM == 1){
            is_beat_num_odd := 0.U
        }
        else if(conf.BATCH_CDP_NUM == 2){
            is_beat_num_odd := cmd_fifo_rd_pos_w(0)
        }
        else if(conf.BATCH_CDP_NUM == 4){
            is_beat_num_odd := cmd_fifo_rd_pos_w(1, 0)
        }
        else if(conf.BATCH_CDP_NUM == 8){
            is_beat_num_odd := cmd_fifo_rd_pos_w(2, 0)
        }
        else if(conf.BATCH_CDP_NUM == 16){
            is_beat_num_odd := cmd_fifo_rd_pos_w(3, 0)
        }
    }

    val dat_en = RegInit(false.B)
    val dat_rdy = Wire(Bool())
    val dat_vld_per_batch = Wire(Vec(conf.ATMM_NUM, Bool()))
    for(i <- 0 to conf.ATMM_NUM-1){
        dat_vld_per_batch(i) := dat_en & dat_fifo_rd_pvld_per_batch(i) & !(is_last_beat & (is_beat_num_odd < i.U))
        dat_rdy_per_batch(i) :=  dat_en & dat_rdy & !(is_last_beat & (is_beat_num_odd < i.U))
    }

    val dat_vld = dat_en & (dat_vld_per_batch.asUInt.orR)
    dat_rdy := dat_en & dma_wr_req_rdy
    dat_accept := dat_vld & dat_rdy

    // Req.cmd
    when(is_last_beat & dat_accept){
        cmd_en := true.B
        dat_en := false.B   
    }
    .elsewhen(cmd_accept){
        cmd_en := false.B
        dat_en := true.B
    }

    when(cmd_accept){
        reg_cube_last := is_cube_last
        req_chn_size := cmd_fifo_rd_pos_c
    }

    val width_size = cmd_fifo_rd_pos_w

    // Beat CNT
    val beat_cnt = RegInit("b0".asUInt(3.W))
    when(cmd_accept){
        beat_cnt := 0.U
    }
    .elsewhen(dat_accept){
        beat_cnt := beat_cnt + 1.U
    }
    
    val cmd_fifo_rd_pos_w_reg = RegInit("b0".asUInt(3.W))
    when(cmd_fifo_rd_pvld & cmd_fifo_rd_prdy){
        cmd_fifo_rd_pos_w_reg := cmd_fifo_rd_pos_w(3, log2Ceil(conf.ATMM_NUM))
    }

    is_last_beat := (beat_cnt === cmd_fifo_rd_pos_w_reg)

    //==============
    // DMA REQ: DATA
    //==============
    //------------------------------------
    // mode:  64      ||  mode: 32
    // clk : 0 0 1 1  ||  clk : 0 0 1 1
    // - - - - - - - -||  - - - - - - - 
    // fifo: 0 4 0 4  ||  fifo: 0 4 0 4
    // fifo: 1 5 1 5  ||  fifo: 1 5 1 5
    // fifo: 2 6 2 6  ||  fifo: 2 6 2 6
    // fifo: 3 7 3 7  ||  fifo: 3 7 3 7
    // - - - - - - - -||  - - - - - - - 
    // bus : L-H L-H  ||  bus : L H-L H
    //------------------------------------

    //==============
    // DMA REQ: ADDR
    //==============
    // rename for reuse between rdma and wdma
    val reg2dp_base_addr = Cat(io.reg2dp_dst_base_addr_high,io.reg2dp_dst_base_addr_low)
    val reg2dp_line_stride = io.reg2dp_dst_line_stride
    val reg2dp_surf_stride = io.reg2dp_dst_surface_stride
    //==============
    // DMA Req : ADDR : Prepration
    // DMA Req: go through the CUBE: W8->C->H
    //==============
    // Width: need be updated when move to next line 
    // Trigger Condition: (is_last_c & is_last_w)
    val base_addr_w = RegInit("b0".asUInt(64.W))
    when(op_load){
        base_addr_w := reg2dp_base_addr
    }
    .elsewhen(cmd_accept){
        when(is_last_c && is_last_w){
            base_addr_w := base_addr_w + reg2dp_line_stride
        }
    }

    // base_Chn: need be updated when move to next w.group 
    // Trigger Condition: (is_last_c)
    //  1, jump to next line when is_last_w
    //  2, jump to next w.group when !is_last_w
    val width_size_use = width_size +& 1.U
    val base_addr_c = RegInit("b0".asUInt(64.W))
    when(op_load){
        base_addr_c := reg2dp_base_addr
    }
    .elsewhen(cmd_accept){
        when(is_last_c && is_last_w){
            base_addr_c := base_addr_w + reg2dp_line_stride
        }
        .otherwise{
            base_addr_c := base_addr_c + Cat(width_size_use, "b0".asUInt(conf.ATMMBW.W))
        }
    }

    //==============
    // DMA Req : ADDR : Generation
    //==============
    val dma_req_addr = RegInit("b0".asUInt(64.W))
    when(op_load){
        dma_req_addr := reg2dp_base_addr
    }
    .elsewhen(cmd_accept){
        when(is_last_c){
            when(is_last_w){
                dma_req_addr := dma_req_addr + reg2dp_line_stride
            }
            .otherwise{
                dma_req_addr := base_addr_c + Cat(width_size_use, "b0".asUInt(conf.ATMMBW.W))
            }
        }
        .otherwise{
            dma_req_addr := dma_req_addr + reg2dp_surf_stride
        }
    }

    ////==============
    //==============
    // DMA REQ: Size
    //==============
    // packet: cmd
    val dma_wr_cmd_vld  = cmd_vld
    val dma_wr_cmd_addr = dma_req_addr
    val dma_wr_cmd_size = cmd_fifo_rd_pos_w
    val dma_wr_cmd_require_ack = is_cube_last

    // PKT_PACK_WIRE( dma_write_cmd ,  dma_wr_cmd_ ,  dma_wr_cmd_pd )
    val dma_wr_cmd_pd = Cat(dma_wr_cmd_require_ack, dma_wr_cmd_size, dma_wr_cmd_addr)
    // packet: data
    val dma_wr_dat_vld = dat_vld
    val dma_wr_dat_data = dat_data

    val dma_wr_dat_mask = WireInit(Fill(conf.ATMM_NUM, true.B))
    when(is_last_beat){
        dma_wr_dat_mask := MuxLookup(is_beat_num_odd, Fill(conf.ATMM_NUM, true.B),
                           (0 to (conf.ATMM_NUM-1)) map { i => i.U -> Fill(i+1, true.B)})
    }

    val dma_wr_dat_pd = Cat(dma_wr_dat_mask, dma_wr_dat_data)

    //============================
    // pack cmd & dat
    val dma_wr_req_vld = dma_wr_cmd_vld | dma_wr_dat_vld
    val dma_wr_req_pd_0 = WireInit("b0".asUInt((conf.NVDLA_DMA_WR_REQ-1).W))
    when(cmd_en){
        dma_wr_req_pd_0 := dma_wr_cmd_pd
    }
    .otherwise{
        dma_wr_req_pd_0 := dma_wr_dat_pd
    }

    val dma_wr_req_pd_1 = Mux(cmd_en, false.B, true.B)

    val dma_wr_req_pd = Cat(dma_wr_req_pd_1, dma_wr_req_pd_0)

    //==============
    // writting stall counter before DMA_if
    //==============
    val cnt_inc = true.B
    val cdp_wr_stall_count_dec = false.B
    val cnt_clr = op_done
    val cnt_cen = (io.reg2dp_dma_en === true.B ) & (dma_wr_req_vld & (~dma_wr_req_rdy))

    val u_histo_wdma = Module{new NV_COUNTER_STAGE_histogram(32)}
    u_histo_wdma.io.clk := io.nvdla_core_clk
    u_histo_wdma.io.rd_stall_inc := cnt_inc
    u_histo_wdma.io.rd_stall_dec := cdp_wr_stall_count_dec
    u_histo_wdma.io.rd_stall_clr := cnt_clr
    u_histo_wdma.io.rd_stall_cen := cnt_cen
    val cdp_wr_stall_count = u_histo_wdma.io.cnt_cur

    val layer_flag = RegInit(false.B)
    when(cnt_clr){
        layer_flag := ~layer_flag
    }

    val dp2reg_d0_perf_write_stall_out = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_perf_write_stall_out = RegInit("b0".asUInt(32.W))

    when(cnt_clr & (~layer_flag)){
        dp2reg_d0_perf_write_stall_out := cdp_wr_stall_count
    }

    when(cnt_clr & layer_flag){
        dp2reg_d1_perf_write_stall_out := cdp_wr_stall_count
    }

    io.dp2reg_d0_perf_write_stall := dp2reg_d0_perf_write_stall_out
    io.dp2reg_d1_perf_write_stall := dp2reg_d1_perf_write_stall_out

    //==============
    // DMA Interface
    //==============

    val u_NV_NVDLA_CDP_WDMA_wr = Module(new NV_NVDLA_DMAIF_wr(conf.NVDLA_CDP_MEM_WR_REQ))
    u_NV_NVDLA_CDP_WDMA_wr.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CDP_WDMA_wr.io.reg2dp_dst_ram_type := io.reg2dp_dst_ram_type

    u_NV_NVDLA_CDP_WDMA_wr.io.dmaif_wr_req_pd.valid := dma_wr_req_vld
    dma_wr_req_rdy := u_NV_NVDLA_CDP_WDMA_wr.io.dmaif_wr_req_pd.ready
    u_NV_NVDLA_CDP_WDMA_wr.io.dmaif_wr_req_pd.bits := dma_wr_req_pd
    val dma_wr_rsp_complete = u_NV_NVDLA_CDP_WDMA_wr.io.dmaif_wr_rsp_complete

    io.cdp2mcif_wr_req_pd <> u_NV_NVDLA_CDP_WDMA_wr.io.mcif_wr_req_pd
    u_NV_NVDLA_CDP_WDMA_wr.io.mcif_wr_rsp_complete := io.mcif2cdp_wr_rsp_complete


    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdp2cvif_wr_req_pd.get <> u_NV_NVDLA_CDP_WDMA_wr.io.cvif_wr_req_pd.get
        u_NV_NVDLA_CDP_WDMA_wr.io.cvif_wr_rsp_complete.get := io.cvif2cdp_wr_rsp_complete.get
    }

    ////////////////////////////////////////////////////////
    val intr_fifo_wr_pd = io.reg2dp_interrupt_ptr
    val intr_fifo_wr_pvld = op_done
    val intr_fifo_rd_prdy = dma_wr_rsp_complete

    //interrupt fifo
    val u_intr_fifo = Module{new NV_NVDLA_fifo(depth = 0, width = 1)}
    u_intr_fifo.io.clk := io.nvdla_core_clk_orig
    u_intr_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_intr_fifo.io.wr_pvld := intr_fifo_wr_pvld
    u_intr_fifo.io.wr_pd := intr_fifo_wr_pd

    val intr_fifo_rd_pvld = u_intr_fifo.io.rd_pvld
    u_intr_fifo.io.rd_prdy := intr_fifo_rd_prdy
    val intr_fifo_rd_pd = u_intr_fifo.io.rd_pd

    val cdp2glb_done_intr_pd_0 = withClock(io.nvdla_core_clk_orig){RegNext(intr_fifo_rd_pvld & intr_fifo_rd_prdy & (intr_fifo_rd_pd === 0.U), false.B)}
    val cdp2glb_done_intr_pd_1 = withClock(io.nvdla_core_clk_orig){RegNext(intr_fifo_rd_pvld & intr_fifo_rd_prdy & (intr_fifo_rd_pd === 1.U), false.B)}

        
    io.cdp2glb_done_intr_pd := Cat(cdp2glb_done_intr_pd_1, cdp2glb_done_intr_pd_0)

}}



object NV_NVDLA_CDP_wdmaDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_wdma())
}