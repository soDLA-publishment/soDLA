package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_MCIF_WRITE_IG_cvt(implicit conf:nvdlaConfig)  extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())

        //spt2cvt
        val spt2cvt_cmd_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_WR_IG_PW.W)))
        val spt2cvt_dat_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_PRIMARY_MEMIF_WIDTH+conf.NVDLA_DMA_MASK_BIT).W)))

        //cq_wr
        val cq_wr_pd = DecoupledIO(UInt(conf.MCIF_WRITE_CQ_WIDTH.W))
        val cq_wr_thread_id = Output(UInt(conf.MCIF_WRITE_CQ_VEC_NUM.W))

        //mcif2noc
        val mcif2noc_axi_aw = DecoupledIO(new nocif_axi_wr_address_if)
        val mcif2noc_axi_w = DecoupledIO(new nocif_axi_wr_data_if)

        //eg2ig
        val eg2ig_axi_len = Flipped(ValidIO(UInt(2.W)))
        val reg2dp_wr_os_cnt = Input(UInt(8.W))
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

    //IG_cvt===upack : none-flop-in
    val cmd_rdy = Wire(Bool())
    val pipe_p1 = Module(new NV_NVDLA_BC_pipe(conf.NVDLA_DMA_WR_IG_PW))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.spt2cvt_cmd_pd.valid
    io.spt2cvt_cmd_pd.ready := pipe_p1.io.ro
    pipe_p1.io.di := io.spt2cvt_cmd_pd.bits
    val cmd_vld = pipe_p1.io.vo
    pipe_p1.io.ri := cmd_rdy
    val cmd_pd = pipe_p1.io.dout

    val dat_rdy = Wire(Bool())
    val pipe_p2 = Module(new NV_NVDLA_BC_pipe(conf.NVDLA_PRIMARY_MEMIF_WIDTH + conf.NVDLA_DMA_MASK_BIT))
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := io.spt2cvt_dat_pd.valid
    io.spt2cvt_dat_pd.ready := pipe_p2.io.ro
    pipe_p2.io.di := io.spt2cvt_dat_pd.bits
    val dat_vld = pipe_p2.io.vo
    pipe_p2.io.ri := dat_rdy
    val dat_pd = pipe_p2.io.dout

    val os_cnt_full = Wire(Bool())
    val os_cmd_vld = cmd_vld & !os_cnt_full

    val all_downs_rdy = Wire(Bool())
    val axi_dat_rdy = Wire(Bool())
    val is_first_beat = Wire(Bool())
    //IG_cvt=== push into the cq on first beat of data
    dat_rdy := Mux(is_first_beat, os_cmd_vld & all_downs_rdy, axi_dat_rdy)
    //IG_cvt=== will release cmd on the acception of last beat of data
    cmd_rdy := is_first_beat & dat_vld & all_downs_rdy & !os_cnt_full

    //IG_cvt===UNPACK after ipipe
    val cmd_vld_pd = Fill(conf.NVDLA_DMA_WR_IG_PW, cmd_vld) & cmd_pd

    val cmd_axid = cmd_vld_pd(3,0)
    val cmd_require_ack = cmd_vld_pd(4)
    val cmd_addr = cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+4,5)
    val cmd_size = cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+7, conf.NVDLA_MEM_ADDRESS_WIDTH+5);
    val cmd_swizzle = cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+8)
    val cmd_odd  =  cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+9)
    val cmd_inc = cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+10)
    val cmd_ltran = cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+11)
    val cmd_ftran = cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+12)

    // PKT_UNPACK_WIRE( cvt_write_data , dat_ , dat_pd )
    val dat_data = dat_pd(conf.NVDLA_PRIMARY_MEMIF_WIDTH-1, 0)
    val dat_mask = dat_pd(conf.NVDLA_PRIMARY_MEMIF_WIDTH+conf.NVDLA_DMA_MASK_BIT-1, conf.NVDLA_PRIMARY_MEMIF_WIDTH)

    val axi_len = cmd_size(1, 0)

    val is_first_cmd_dat_vld = os_cmd_vld & dat_vld && is_first_beat

    val beat_count = RegInit("b0".asUInt(2.W))
    when(is_first_cmd_dat_vld & all_downs_rdy){
        beat_count := axi_len
    }
    .elsewhen((beat_count =/= 0.U) & dat_vld & axi_dat_rdy){
        beat_count := beat_count - 1.U
    }

    is_first_beat := (beat_count === 0.U)
    val is_single_beat = (axi_len === 0.U)
    val is_last_beat = (beat_count === 1.U || (beat_count === 0.U && is_single_beat))

    val axi_axid = cmd_axid(3, 0)
    val axi_addr = cmd_addr
    val axi_data = dat_data
    val axi_last = is_last_beat
    val axi_strb = Fill(conf.NVDLA_MEMORY_ATOMIC_SIZE, dat_mask) //{{32{dat_mask[1]}},{32{dat_mask[0]}}};

    //=====================================
    val os_cnt = Wire(UInt(9.W))
    val os_inp_add_nxt = Mux(cmd_vld, axi_len +& 1.U, 0.U)
    val eg2ig_axi_vld_d = RegInit(false.B)
    val eg2ig_axi_len_d = RegInit("b0".asUInt(2.W))

    eg2ig_axi_vld_d := io.eg2ig_axi_len.valid
    when(io.eg2ig_axi_len.valid){
       eg2ig_axi_len_d := io.eg2ig_axi_len.bits
    }

    val os_inp_sub_nxt = Mux(eg2ig_axi_vld_d, eg2ig_axi_len_d +& 1.U, 0.U)
    val os_inp_nxt = os_cnt +& os_inp_add_nxt -& os_inp_sub_nxt

    // IG_cvt=== 256 outstanding trans
    val axi_cmd_vld = Wire(Bool())
    val axi_cmd_rdy = Wire(Bool())
    val os_cnt_add_en = axi_cmd_vld & axi_cmd_rdy
    val os_cnt_sub_en = eg2ig_axi_vld_d
    val os_cnt_cen = os_cnt_add_en | os_cnt_sub_en
    val os_cnt_add = Mux(os_cnt_add_en, axi_len +& 1.U, 0.U)
    val os_cnt_sub = Mux(os_cnt_sub_en, eg2ig_axi_len_d +& 1.U, 0.U)
    val cfg_wr_os_cnt = io.reg2dp_wr_os_cnt
    val wr_os_cnt_ext = Cat(false.B, cfg_wr_os_cnt)
    os_cnt_full := os_inp_nxt > (wr_os_cnt_ext +& 1.U)

    val perf_os = Module(new NV_COUNTER_STAGE_os)
    perf_os.io.clk := io.nvdla_core_clk
    perf_os.io.os_cnt_add := os_cnt_add
    perf_os.io.os_cnt_sub := os_cnt_sub
    perf_os.io.os_cnt_cen := os_cnt_cen
    os_cnt := perf_os.io.os_cnt_cur

    //IG_cvt=== PIPE for $NOC ADDR Channel
    // cmd will be pushed into pipe with the 1st beat of data in that cmd, 
    // and when *_beat_vld is high, *_cmd_vld should always be there.
    axi_cmd_vld := is_first_cmd_dat_vld & io.cq_wr_pd.ready & axi_dat_rdy
    val axi_cmd_pd = Wire(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH + 6).W))
    val pipe_p3 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_MEM_ADDRESS_WIDTH+6))
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := axi_cmd_vld
    axi_cmd_rdy := pipe_p3.io.ro
    pipe_p3.io.di := axi_cmd_pd
    io.mcif2noc_axi_aw.valid := pipe_p3.io.vo
    pipe_p3.io.ri := io.mcif2noc_axi_aw.ready
    val axi_aw_pd = pipe_p3.io.dout

    //IG_cvt=== PIPE for $NOC DATA Channel
    // first beat of data also need cq and cmd rdy, this is because we also need push ack/cmd into cq fifo and cmd pipe on first beat of data
    val axi_dat_vld = dat_vld & (!is_first_beat || (os_cmd_vld & io.cq_wr_pd.ready & axi_cmd_rdy))
    val axi_dat_pd = Wire(UInt((conf.NVDLA_PRIMARY_MEMIF_WIDTH + conf.NVDLA_PRIMARY_MEMIF_STRB+1).W))
    val pipe_p4 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_PRIMARY_MEMIF_WIDTH+conf.NVDLA_PRIMARY_MEMIF_STRB+1))
    pipe_p4.io.clk := io.nvdla_core_clk
    pipe_p4.io.vi := axi_dat_vld
    axi_dat_rdy := pipe_p4.io.ro
    pipe_p4.io.di := axi_dat_pd
    io.mcif2noc_axi_w.valid := pipe_p4.io.vo
    pipe_p4.io.ri := io.mcif2noc_axi_w.ready
    val axi_w_pd = pipe_p4.io.dout

    axi_cmd_pd := Cat(axi_axid,axi_addr,axi_len)
    val opipe_axi_len = axi_aw_pd(1, 0)
    val opipe_axi_addr = axi_aw_pd(conf.NVDLA_MEM_ADDRESS_WIDTH + 1, 2)
    val opipe_axi_axid = axi_aw_pd(conf.NVDLA_MEM_ADDRESS_WIDTH + 5, conf.NVDLA_MEM_ADDRESS_WIDTH + 2)
    axi_dat_pd := Cat(axi_data,axi_strb,axi_last)
    val opipe_axi_last = axi_w_pd(0)
    val opipe_axi_strb = axi_w_pd(conf.NVDLA_PRIMARY_MEMIF_STRB, 1)
    val opipe_axi_data = axi_w_pd(conf.NVDLA_PRIMARY_MEMIF_WIDTH + conf.NVDLA_PRIMARY_MEMIF_STRB, conf.NVDLA_PRIMARY_MEMIF_STRB + 1)

    // IG_cvt===AXI OUT ZERO EXT
    io.mcif2noc_axi_aw.bits.id := opipe_axi_axid
    io.mcif2noc_axi_aw.bits.addr := opipe_axi_addr
    io.mcif2noc_axi_aw.bits.len := opipe_axi_len
    io.mcif2noc_axi_w.bits.last := opipe_axi_last
    io.mcif2noc_axi_w.bits.data := opipe_axi_data
    io.mcif2noc_axi_w.bits.strb := opipe_axi_strb

    //=====================================
    // DownStream readiness
    //=====================================
    val axi_both_rdy  = axi_cmd_rdy & axi_dat_rdy
    all_downs_rdy := io.cq_wr_pd.ready & axi_both_rdy

    //=====================================
    // Outstanding Queue
    //=====================================
    // IG_cvt===valid for axi_cmd and oq, inter-lock

    io.cq_wr_pd.valid := is_first_cmd_dat_vld & axi_both_rdy & !os_cnt_full;
    val cq_wr_require_ack = cmd_ltran & cmd_require_ack;
    val cq_wr_len = axi_len

    // PKT_PACK_WIRE( mcif_write_ig2eg ,  cq_wr_ , cq_wr_pd )
    io.cq_wr_pd.bits := Cat(cq_wr_len, cq_wr_require_ack)
    io.cq_wr_thread_id := cmd_axid

}}

