package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

@chiselName
class NV_NVDLA_MCIF_WRITE_IG_arb(implicit conf:nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        //bpt2arb
        val bpt2arb_cmd_pd = Flipped(Vec(conf.WDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_WR_IG_PW.W))))
        val bpt2arb_dat_pd = Flipped(Vec(conf.WDMA_NUM, DecoupledIO(UInt((conf.NVDLA_DMA_WR_REQ-1).W))))

        //arb2spt
        val arb2spt_cmd_pd = DecoupledIO(UInt(conf.NVDLA_DMA_WR_IG_PW.W))
        val arb2spt_dat_pd = DecoupledIO(UInt((conf.NVDLA_DMA_WR_REQ-1).W))

        val reg2dp_wr_weight = Input(Vec(conf.WDMA_NUM, UInt(8.W)))
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

    val src_cmd_size = Wire(Vec(conf.WDMA_NUM, UInt(3.W)))
    val src_cmd_beats = Wire(Vec(conf.WDMA_NUM, UInt(3.W)))
    val src_cmd_camp_vld = Wire(Vec(conf.WDMA_NUM, Bool()))

    val is_last_beat = Wire(Bool())
    val src_dat_rdy = Wire(Bool())
    dontTouch(is_last_beat)
    dontTouch(src_dat_rdy)
    val src_dat_gnts = Wire(Vec(conf.WDMA_MAX_NUM, Bool()))
    val all_gnts = Wire(Vec(conf.WDMA_MAX_NUM, Bool()))

    val u_pipe = Array.fill(conf.WDMA_NUM){Module(new NV_NVDLA_BC_OS_pipe(conf.NVDLA_DMA_WR_IG_PW))}
    val u_dfifo = Array.fill(conf.WDMA_NUM){Module(new NV_NVDLA_fifo_new(depth = 4, width = conf.NVDLA_DMA_WR_REQ-1, ram_type = 0, io_wr_count = true))}
    for(i<- 0 to conf.WDMA_NUM-1){
        u_pipe(i).io.clk := io.nvdla_core_clk

        u_pipe(i).io.vi := io.bpt2arb_cmd_pd(i).valid
        io.bpt2arb_cmd_pd(i).ready := u_pipe(i).io.ro
        u_pipe(i).io.di := io.bpt2arb_cmd_pd(i).bits

        u_dfifo(i).io.clk := io.nvdla_core_clk
        u_dfifo(i).io.pwrbus_ram_pd := io.pwrbus_ram_pd
        u_dfifo(i).io.wr_pvld := io.bpt2arb_dat_pd(i).valid
        io.bpt2arb_dat_pd(i).ready := u_dfifo(i).io.wr_prdy
        u_dfifo(i).io.wr_pd := io.bpt2arb_dat_pd(i).bits
        
        src_cmd_size(i) := Fill(3, u_pipe(i).io.vo) & u_pipe(i).io.dout(conf.NVDLA_MEM_ADDRESS_WIDTH+7, conf.NVDLA_MEM_ADDRESS_WIDTH+5)
        u_pipe(i).io.ri := is_last_beat & src_dat_rdy & src_dat_gnts(i)
        u_dfifo(i).io.rd_prdy := src_dat_rdy & all_gnts(i)
        src_cmd_beats(i) := src_cmd_size(i)
        src_cmd_camp_vld(i) := u_pipe(i).io.vo & (u_dfifo(i).io.wr_count.get > src_cmd_beats(i))   
    }

    val arb_gnts = Wire(Vec(conf.WDMA_MAX_NUM, Bool()))
    val gnt_busy = Wire(Bool())
    val u_write_ig_arb = Module(new NV_NVDLA_arb(n = conf.WDMA_MAX_NUM, wt_width = 8, io_gnt_busy = true))
    u_write_ig_arb.io.clk := io.nvdla_core_clk
    u_write_ig_arb.io.gnt_busy.get := gnt_busy
    for(i<- 0 to conf.WDMA_NUM-1) {
        u_write_ig_arb.io.req(i) := src_cmd_camp_vld(i)
        u_write_ig_arb.io.wt(i) := io.reg2dp_wr_weight(i)
        arb_gnts(i) := u_write_ig_arb.io.gnt(i)
    }
    for(i<- conf.WDMA_NUM to conf.WDMA_MAX_NUM-1){
        u_write_ig_arb.io.req(i) := false.B
        u_write_ig_arb.io.wt(i) := 0.U(8.W)
        arb_gnts(i) := u_write_ig_arb.io.gnt(i)
    }

    val sticky = RegInit(false.B)
    val spt_is_busy = Wire(Bool())
    val stick_gnts = RegInit(VecInit(Seq.fill(conf.WDMA_MAX_NUM)(false.B)))
    val any_arb_gnt = arb_gnts.asUInt.orR
    all_gnts := Mux(sticky, stick_gnts, arb_gnts)
    gnt_busy := sticky || spt_is_busy

    // MUX out based on GNT
    when(any_arb_gnt){
        stick_gnts := arb_gnts
    }

    //keep grant not change until all data accept
    val src_dat_vld = Wire(Bool())
    when(any_arb_gnt){
        when(src_dat_vld & src_dat_rdy & is_last_beat){
            sticky := false.B
        }
        .otherwise{
            sticky := true.B
        }
    }
    .elsewhen(src_dat_vld & src_dat_rdy & is_last_beat){
        sticky := false.B
    }

    for(i <- 0 to conf.WDMA_NUM-1){
        src_dat_gnts(i) := all_gnts(i) & u_dfifo(i).io.rd_pvld
    }
    for(i <- conf.WDMA_NUM to conf.WDMA_MAX_NUM-1){
        src_dat_gnts(i) := false.B
    }

    src_dat_vld := src_dat_gnts.asUInt.orR

    val gnt_count = RegInit("b0".asUInt(3.W))
    when(src_dat_vld & src_dat_rdy){
        when(is_last_beat){
            gnt_count := 0.U
        }
        .otherwise{
            gnt_count := gnt_count + 1.U
        }
    }

    val arb_cmd_size = Wire(UInt(3.W))
    is_last_beat := (gnt_count === arb_cmd_size)

    // ARB MUX
    val arb_cmd_pd = WireInit("b0".asUInt(conf.NVDLA_DMA_WR_IG_PW.W))
    val arb_dat_pd = WireInit("b0".asUInt((conf.NVDLA_DMA_WR_REQ-1).W))
    for(i <- 0 to conf.WDMA_NUM-1){
        when(all_gnts(i)){
            arb_cmd_pd := u_pipe(i).io.dout
            arb_dat_pd := u_dfifo(i).io.rd_pd
        } 
    }
    for(i <- conf.WDMA_NUM to conf.WDMA_MAX_NUM-1){
        when(all_gnts(i)){
            arb_cmd_pd := 0.U
            arb_dat_pd := 0.U
        }
    } 

    arb_cmd_size := arb_cmd_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+7, conf.NVDLA_MEM_ADDRESS_WIDTH+5)
    io.arb2spt_cmd_pd.bits := arb_cmd_pd
    io.arb2spt_dat_pd.bits := arb_dat_pd

    io.arb2spt_cmd_pd.valid := any_arb_gnt
    io.arb2spt_dat_pd.valid := src_dat_vld
    src_dat_rdy := io.arb2spt_dat_pd.ready

    spt_is_busy := ~(io.arb2spt_cmd_pd.ready & io.arb2spt_dat_pd.ready) //fixme
}}


 object NV_NVDLA_MCIF_WRITE_IG_arbDriver extends App {
     implicit val conf: nvdlaConfig = new nvdlaConfig
     chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_WRITE_IG_arb())
 }

