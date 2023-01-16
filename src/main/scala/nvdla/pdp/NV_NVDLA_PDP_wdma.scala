package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_wdma(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk_orig = Input(Clock())
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        // dp2wdma
        val pdp_dp2wdma_pd = Flipped(DecoupledIO(UInt(conf.PDPBW.W)))

        // pdp2mcif_wr
        val pdp2mcif_wr_req_pd = DecoupledIO(UInt(conf.NVDLA_PDP_MEM_WR_REQ.W))
        val mcif2pdp_wr_rsp_complete = Input(Bool())

        // pdp2cvif_wr
        val pdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_WR_REQ.W))) else None
        val cvif2pdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None

        val pdp2glb_done_intr_pd = Output(UInt(2.W))
        val rdma2wdma_done = Input(Bool())

        // config
        val reg2dp_cube_out_channel = Input(UInt(13.W))
        val reg2dp_cube_out_height = Input(UInt(13.W))
        val reg2dp_cube_out_width = Input(UInt(13.W))
        val reg2dp_dma_en = Input(Bool())
        val reg2dp_dst_base_addr_high = Input(UInt(32.W))
        val reg2dp_dst_base_addr_low = Input(UInt(32.W))
        val reg2dp_dst_line_stride = Input(UInt(32.W))
        val reg2dp_dst_ram_type = Input(Bool())
        val reg2dp_dst_surface_stride = Input(UInt(32.W))
        val reg2dp_flying_mode = Input(Bool())
        val reg2dp_interrupt_ptr = Input(Bool())
        val reg2dp_op_en = Input(Bool())
        val reg2dp_partial_width_out_first = Input(UInt(10.W))
        val reg2dp_partial_width_out_last = Input(UInt(10.W))
        val reg2dp_partial_width_out_mid = Input(UInt(10.W))
        val reg2dp_split_num = Input(UInt(8.W))
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
    // tracing rdma reading done to avoid layer switched but RDMA still reading the last layer
    //==============
    val op_done = Wire(Bool())
    val op_load = Wire(Bool())
    val on_fly_en = io.reg2dp_flying_mode === 0.U
    val off_fly_en = io.reg2dp_flying_mode === 1.U

    val reading_done_flag = RegInit(false.B)
    when(op_done){
        reading_done_flag := false.B
    }
    .elsewhen(io.rdma2wdma_done & off_fly_en){
        reading_done_flag := true.B
    }
    .elsewhen(op_load & on_fly_en){
        reading_done_flag := true.B
    }
    .elsewhen(op_load & off_fly_en){
        reading_done_flag := false.B
    }

    val waiting_rdma = RegInit(false.B)
    when(op_done & (~reading_done_flag)){
        waiting_rdma := true.B
    }
    .elsewhen(reading_done_flag){
        waiting_rdma := false.B
    }

    val wdma_done = RegInit(false.B)
    when(op_done & reading_done_flag){
        wdma_done := true.B
    }
    .elsewhen(waiting_rdma & reading_done_flag){
        wdma_done := true.B
    }
    .otherwise{
        wdma_done := false.B
    }

    //==============
    // Work Processing
    //==============
    val op_prcess = RegInit(false.B)
    op_load := io.reg2dp_op_en & ~op_prcess;
    val is_last_beat = Wire(Bool())
    val reg_cube_last = RegInit(false.B)
    val dat_accept = Wire(Bool())
    op_done := reg_cube_last & is_last_beat & dat_accept
    io.dp2reg_done := wdma_done

    when(op_load){
        op_prcess := true.B
    }
    .elsewhen(wdma_done){
        op_prcess := false.B
    }

    //==============
    // Data INPUT pipe and Unpack
    //==============
    val pipe_0 = Module(new NV_NVDLA_IS_pipe(conf.PDPBW))
    pipe_0.io.clk := io.nvdla_core_clk
    pipe_0.io.vi := io.pdp_dp2wdma_pd.valid
    io.pdp_dp2wdma_pd.ready := pipe_0.io.ro
    pipe_0.io.di := io.pdp_dp2wdma_pd.bits

    //==============
    // Instance CMD
    //==============
    val u_dat = Module(new NV_NVDLA_PDP_WDMA_dat)
    u_dat.io.nvdla_core_clk := io.nvdla_core_clk
    u_dat.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_dat.io.dp2wdma_pd.valid := pipe_0.io.vo
    pipe_0.io.ri := u_dat.io.dp2wdma_pd.ready
    u_dat.io.dp2wdma_pd.bits := pipe_0.io.dout

    // config
    u_dat.io.reg2dp_cube_out_channel := io.reg2dp_cube_out_channel
    u_dat.io.reg2dp_cube_out_height := io.reg2dp_cube_out_height
    u_dat.io.reg2dp_cube_out_width := io.reg2dp_cube_out_width
    u_dat.io.reg2dp_partial_width_out_first := io.reg2dp_partial_width_out_first
    u_dat.io.reg2dp_partial_width_out_last := io.reg2dp_partial_width_out_last
    u_dat.io.reg2dp_partial_width_out_mid := io.reg2dp_partial_width_out_mid
    u_dat.io.reg2dp_split_num := io.reg2dp_split_num

    u_dat.io.op_load := op_load
    u_dat.io.wdma_done := wdma_done

    /////////////////////////////////////////
    // DATA FIFO: READ SIDE
    val reg_lenb = RegInit("b0".asUInt(5.W))
    val reg_size = RegInit("b0".asUInt(13.W))
    val atomm_invld = Wire(Vec(conf.ATMM_NUM, Bool()))
    val dat_fifo_rd_pvld = Wire(Vec(conf.ATMM_NUM, Bool()))
    val dat_fifo_rd_last_pvld = Wire(Bool())
    val dat_rdy = Wire(Bool())

    for(i <- 0 to conf.ATMM_NUM-1){
        dat_fifo_rd_pvld(i) := MuxLookup(reg_lenb, 0.U, 
                            (0 to conf.BATCH_PDP_NUM-1) map { j => j.U -> u_dat.io.dat_fifo_rd_pd(i)(j).valid })
    
        for(j <- 0 to conf.BATCH_PDP_NUM-1){
            u_dat.io.dat_fifo_rd_pd(i)(j).ready := Mux(atomm_invld(i)&is_last_beat, false.B, dat_rdy & dat_fifo_rd_last_pvld)
        }
    }

    if(conf.ATMM_NUM == 1){
        atomm_invld(0) := false.B
        dat_fifo_rd_last_pvld := dat_fifo_rd_pvld(0)
    }
    else if(conf.ATMM_NUM == 2){
        atomm_invld(0) := false.B
        atomm_invld(1) := reg_size(0) === 0.U
        dat_fifo_rd_last_pvld := Mux((reg_size(0) === 0.U)&is_last_beat, dat_fifo_rd_pvld(0), dat_fifo_rd_pvld(1))
    }
    else if(conf.ATMM_NUM == 4){
        atomm_invld(0) := false.B
        atomm_invld(1) := reg_size(1, 0) < 1.U
        atomm_invld(2) := reg_size(1, 0) < 2.U
        atomm_invld(3) := reg_size(1, 0) < 3.U
        dat_fifo_rd_last_pvld := Mux((reg_size(1, 0) === 0.U)& is_last_beat, dat_fifo_rd_pvld(0),
                                 Mux((reg_size(1, 0) === 1.U)& is_last_beat, dat_fifo_rd_pvld(1),
                                 Mux((reg_size(1, 0) === 2.U)& is_last_beat, dat_fifo_rd_pvld(2),
                                 Mux((reg_size(1, 0) === 3.U)& is_last_beat, dat_fifo_rd_pvld(3), 
                                 0.U))))
    }
    val dat_fifo_rd_pd = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_PDP_NUM, UInt(conf.PDPBW.W))))
    for(i <- 0 to conf.ATMM_NUM-1){
        for(j <- 0 to conf.BATCH_PDP_NUM-1){
            dat_fifo_rd_pd(i)(j) := u_dat.io.dat_fifo_rd_pd(i)(j).bits
        }
    }
    val dat_data = dat_fifo_rd_pd.asUInt

    //==============
    // Instance CMD
    //==============
    val u_cmd = Module(new NV_NVDLA_PDP_WDMA_cmd)
    u_cmd.io.nvdla_core_clk := io.nvdla_core_clk
    u_cmd.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    // config
    u_cmd.io.reg2dp_cube_out_channel := io.reg2dp_cube_out_channel
    u_cmd.io.reg2dp_cube_out_height := io.reg2dp_cube_out_height
    u_cmd.io.reg2dp_cube_out_width := io.reg2dp_cube_out_width
    u_cmd.io.reg2dp_dst_base_addr_high := io.reg2dp_dst_base_addr_high
    u_cmd.io.reg2dp_dst_base_addr_low := io.reg2dp_dst_base_addr_low
    u_cmd.io.reg2dp_dst_line_stride := io.reg2dp_dst_line_stride
    u_cmd.io.reg2dp_dst_surface_stride := io.reg2dp_dst_surface_stride
    u_cmd.io.reg2dp_partial_width_out_first := io.reg2dp_partial_width_out_first
    u_cmd.io.reg2dp_partial_width_out_last := io.reg2dp_partial_width_out_last
    u_cmd.io.reg2dp_partial_width_out_mid := io.reg2dp_partial_width_out_mid
    u_cmd.io.reg2dp_split_num := io.reg2dp_split_num

    u_cmd.io.op_load := op_load


    // CMD FIFO: Read side
    val cmd_en = RegInit(true.B)
    val dat_en = RegInit(false.B)
    val dma_wr_req_rdy = Wire(Bool())
    u_cmd.io.cmd_fifo_rd_pd.ready := cmd_en & dma_wr_req_rdy

    // Unpack cmd & data together
    // PKT_UNPACK_WIRE( pdp_wdma_cmd , cmd_fifo_rd_ , cmd_fifo_rd_pd )
    val cmd_fifo_rd_addr = u_cmd.io.cmd_fifo_rd_pd.bits(63, 0)
    val cmd_fifo_rd_size = u_cmd.io.cmd_fifo_rd_pd.bits(76, 74)
    //val cmd_fifo_rd_lenb = u_cmd.io.cmd_fifo_rd_pd.bits(78, 77)
    val cmd_fifo_rd_cube_end = u_cmd.io.cmd_fifo_rd_pd.bits(79)

    // addr/size/lenb/end
    val dma_wr_cmd_accept = Wire(Bool())
    when(dma_wr_cmd_accept){
        reg_lenb := (conf.ATMM_NUM - 1).U
    }

    when(dma_wr_cmd_accept){
        reg_size := cmd_fifo_rd_size
    }

    when(dma_wr_cmd_accept){
        reg_cube_last := cmd_fifo_rd_cube_end
    }

    //==============
    // BLOCK Operation
    //==============
    val dma_wr_cmd_vld = cmd_en & u_cmd.io.cmd_fifo_rd_pd.valid
    dma_wr_cmd_accept := dma_wr_cmd_vld & dma_wr_req_rdy

    val dma_wr_dat_vld = dat_en & dat_fifo_rd_last_pvld
    dat_rdy := dat_en & dma_wr_req_rdy
    dat_accept := dma_wr_dat_vld & dma_wr_req_rdy

    // count_w and tran_cnt is used to index 8B in each 8(B)x8(w)x4(c) block, (w may be < 8 if is_first_w or is_last_w)
    val count_w = RegInit("b0".asUInt(13.W))
    when(dma_wr_cmd_accept){
        count_w := 0.U
    }
    .elsewhen(dat_accept){
        count_w := count_w + (conf.ATMM_NUM).U
    }


    if(conf.ATMM_NUM == 1){
        is_last_beat := count_w === reg_size
    }
    else if(conf.ATMM_NUM == 2){
        is_last_beat := (count_w === reg_size) || (count_w === (reg_size -& 1.U))
    }
    else if(conf.ATMM_NUM == 4){
        is_last_beat := (count_w === reg_size) || (count_w === (reg_size -& 1.U)) || (count_w === (reg_size -& 2.U)) || (count_w === (reg_size -& 3.U)) 
    }

    when(is_last_beat & dat_accept){
        cmd_en := true.B
        dat_en := false.B
    }
    .elsewhen(dma_wr_cmd_accept){
        cmd_en := false.B
        dat_en := true.B
    }

    //==============
    // DMA REQ: Size
    //==============
    // packet: cmd
    val dma_wr_cmd_addr = cmd_fifo_rd_addr
    val dma_wr_cmd_size = Cat(false.B, cmd_fifo_rd_size)
    val dma_wr_cmd_require_ack = cmd_fifo_rd_cube_end;

    // PKT_PACK_WIRE( dma_write_cmd ,  dma_wr_cmd_ ,  dma_wr_cmd_pd )
    val dma_wr_cmd_pd = Cat(dma_wr_cmd_require_ack, dma_wr_cmd_size, dma_wr_cmd_addr)

    // packet: data
    val dma_wr_dat_data = dat_data
    val cmd_fifo_rd_size_use = RegInit("b0".asUInt(13.W))
    when(u_cmd.io.cmd_fifo_rd_pd.valid & u_cmd.io.cmd_fifo_rd_pd.ready){
        cmd_fifo_rd_size_use := cmd_fifo_rd_size
    }

    val dma_wr_dat_mask = Wire(UInt(2.W))
    if(conf.ATMM_NUM == 1){
        dma_wr_dat_mask := "b1".asUInt
    }
    else if(conf.ATMM_NUM == 2){
        dma_wr_dat_mask := Mux((cmd_fifo_rd_size_use(0) === 0.U) && is_last_beat, "b01".asUInt(2.W), "b11".asUInt(2.W))
    }
    else if(conf.ATMM_NUM == 4){
        dma_wr_dat_mask := Mux((cmd_fifo_rd_size_use(1, 0) === 0.U) && is_last_beat, "b0001".asUInt(4.W), 
                           Mux((cmd_fifo_rd_size_use(1, 0) === 1.U) && is_last_beat, "b0011".asUInt(4.W),
                           Mux((cmd_fifo_rd_size_use(1, 0) === 2.U) && is_last_beat, "b0111".asUInt(4.W),
                           "b1111".asUInt(4.W))))
    }

    // PKT_PACK_WIRE( dma_write_data ,  dma_wr_dat_ ,  dma_wr_dat_pd )
    val dma_wr_dat_pd = Cat(dma_wr_dat_mask, dma_wr_dat_data)

    // pack cmd & dat
    val dma_wr_req_vld = dma_wr_cmd_vld | dma_wr_dat_vld;

    //* PKT_nvdla_dma_wr_req_dma_write_cmd_ID or PKT_nvdla_dma_wr_req_dma_write_data_ID
    val dma_wr_req_pd_pkt = Mux(cmd_en, false.B, true.B)
    val dma_wr_req_pd_data = Wire(UInt((conf.NVDLA_PDP_DMAIF_BW + conf.NVDLA_PDP_MEM_MASK_BIT).W))
    dma_wr_req_pd_data := Mux(cmd_en, dma_wr_cmd_pd, dma_wr_dat_pd)

    val dma_wr_req_pd = Cat(dma_wr_req_pd_pkt, dma_wr_req_pd_data)

    //==============
    // reading stall counter before DMA_if
    //==============
    val cnt_inc = true.B
    val pdp_wr_stall_count_dec = false.B
    val cnt_clr = op_done
    val cnt_cen = (io.reg2dp_dma_en === true.B ) & (dma_wr_req_vld & (~dma_wr_req_rdy))

    val u_histo_rdma = Module{new NV_COUNTER_STAGE_histogram(32)}
    u_histo_rdma.io.clk := io.nvdla_core_clk
    u_histo_rdma.io.rd_stall_inc := cnt_inc
    u_histo_rdma.io.rd_stall_dec := pdp_wr_stall_count_dec
    u_histo_rdma.io.rd_stall_clr := cnt_clr
    u_histo_rdma.io.rd_stall_cen := cnt_cen
    val pdp_wr_stall_count = u_histo_rdma.io.cnt_cur

    val layer_flag = RegInit(false.B)
    when(cnt_clr){
        layer_flag := ~layer_flag
    }

    val dp2reg_d0_perf_write_stall_out = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_perf_write_stall_out = RegInit("b0".asUInt(32.W))

    when(cnt_clr & (~layer_flag)){
        dp2reg_d0_perf_write_stall_out := pdp_wr_stall_count
    }

    when(cnt_clr & layer_flag){
        dp2reg_d1_perf_write_stall_out := pdp_wr_stall_count
    }

    io.dp2reg_d0_perf_write_stall := dp2reg_d0_perf_write_stall_out
    io.dp2reg_d1_perf_write_stall := dp2reg_d1_perf_write_stall_out

    val u_NV_NVDLA_PDP_WDMA_wr = Module(new NV_NVDLA_DMAIF_wr(conf.NVDLA_PDP_MEM_WR_REQ))
    u_NV_NVDLA_PDP_WDMA_wr.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_PDP_WDMA_wr.io.reg2dp_dst_ram_type := io.reg2dp_dst_ram_type

    u_NV_NVDLA_PDP_WDMA_wr.io.dmaif_wr_req_pd.valid := dma_wr_req_vld
    dma_wr_req_rdy := u_NV_NVDLA_PDP_WDMA_wr.io.dmaif_wr_req_pd.ready
    u_NV_NVDLA_PDP_WDMA_wr.io.dmaif_wr_req_pd.bits := dma_wr_req_pd
    val dma_wr_rsp_complete = u_NV_NVDLA_PDP_WDMA_wr.io.dmaif_wr_rsp_complete

    io.pdp2mcif_wr_req_pd <> u_NV_NVDLA_PDP_WDMA_wr.io.mcif_wr_req_pd
    u_NV_NVDLA_PDP_WDMA_wr.io.mcif_wr_rsp_complete := io.mcif2pdp_wr_rsp_complete


    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.pdp2cvif_wr_req_pd.get <> u_NV_NVDLA_PDP_WDMA_wr.io.cvif_wr_req_pd.get
        u_NV_NVDLA_PDP_WDMA_wr.io.cvif_wr_rsp_complete.get := io.cvif2pdp_wr_rsp_complete.get
    }


    //logic for wdma writing done, and has accepted dma_wr_rsp_complete, but RDMA still not reading done
    val wdma_done_d1 = RegInit(false.B)
    wdma_done_d1 := wdma_done
    

    val intp_waiting_rdma = RegInit(false.B)
    when(dma_wr_rsp_complete & waiting_rdma){
        intp_waiting_rdma := true.B
    }
    .elsewhen(wdma_done_d1){
         intp_waiting_rdma := false.B
    }

    //
    val intr_fifo_wr_pvld = Wire(Bool())
    val intr_fifo_rd_prdy = Wire(Bool())
    val intr_fifo_wr_pd = Wire(Bool())
    val u_intr_fifo = Module{new NV_NVDLA_fifo_new(depth = 0, width = 1)}
    u_intr_fifo.io.clk := io.nvdla_core_clk
    u_intr_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_intr_fifo.io.wr_pvld := intr_fifo_wr_pvld
    u_intr_fifo.io.wr_pd := intr_fifo_wr_pd

    val intr_fifo_rd_pvld = u_intr_fifo.io.rd_pvld
    u_intr_fifo.io.rd_prdy := intr_fifo_rd_prdy
    val intr_fifo_rd_pd = u_intr_fifo.io.rd_pd

    intr_fifo_wr_pd := io.reg2dp_interrupt_ptr;
    intr_fifo_wr_pvld := wdma_done;
    intr_fifo_rd_prdy := dma_wr_rsp_complete & (~waiting_rdma) || (intp_waiting_rdma & wdma_done_d1);

    val pdp2glb_done_intr_pd_0 = RegInit(false.B)
    val pdp2glb_done_intr_pd_1 = RegInit(false.B)
    pdp2glb_done_intr_pd_0 := intr_fifo_rd_pvld & intr_fifo_rd_prdy & (intr_fifo_rd_pd === false.B)
    pdp2glb_done_intr_pd_1 := intr_fifo_rd_pvld & intr_fifo_rd_prdy & (intr_fifo_rd_pd === true.B)


    io.pdp2glb_done_intr_pd := Cat(pdp2glb_done_intr_pd_1, pdp2glb_done_intr_pd_0)
    

}}



object NV_NVDLA_PDP_wdmaDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_wdma())
}