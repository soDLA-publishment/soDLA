package nvdla

import chisel3._
import chisel3.util._


class NV_NVDLA_NOCIF_DRAM_WRITE_IG_arb(implicit conf:nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        //bpt2arb
        val bpt2arb_cmd_pd = Flipped(Vec(conf.WDMA_NUM, DecoupledIO(UInt(conf.NVDLA_DMA_WR_IG_PW.W))))
        val bpt2arb_dat_pd = Flipped(Vec(conf.WDMA_NUM, DecoupledIO(UInt((conf.NVDLA_PRIMARY_MEMIF_WIDTH+2).W))))

        //arb2spt
        val arb2spt_cmd_pd = DecoupledIO(UInt(conf.NVDLA_DMA_WR_IG_PW.W))
        val arb2spt_dat_pd = DecoupledIO(UInt((conf.NVDLA_MEMIF_WIDTH+2).W))

        val clients2mcif_wr_wt = Input(Vec(conf.WDMA_NUM, UInt(8.W)))
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
    val src_cmd_inc = Wire(Vec(conf.WDMA_NUM, Bool()))
    val src_cmd_beats = Wire(Vec(conf.WDMA_NUM, UInt(3.W)))
    val src_cmd_camp_vld = Wire(Vec(conf.WDMA_NUM, Bool()))

    val is_last_beat = Wire(Bool())
    val src_dat_gnts = Wire(Vec(conf.WDMA_NUM, Bool()))
    val all_gnts = Wire(Vec(conf.WDMA_NUM, Bool()))

    val u_pipe = Array.fill(conf.WDMA_NUM){Module(new NV_NVDLA_BC_pipe(conf.NVDLA_DMA_WR_IG_PW))}
    val u_dfifo = Array.fill(conf.WDMA_NUM){Module(new NV_NVDLA_fifo(depth = 4, width = conf.NVDLA_PRIMARY_MEMIF_WIDTH+2, ram_type = 0, io_wr_count = true))}
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
        src_cmd_inc(i) := u_pipe(i).io.vo & u_pipe(i).io.dout(conf.NVDLA_MEM_ADDRESS_WIDTH+10)
        u_pipe(i).io.ri := is_last_beat & src_dat_gnts(i)
        u_dfifo(i).io.rd_prdy := all_gnts(i)
        src_cmd_beats(i) := src_cmd_size(i)(2,1) +& src_cmd_inc(i)
        src_cmd_camp_vld(i) := u_pipe(i).io.vo & (u_dfifo(i).io.wr_count.get > src_cmd_beats(i))   
    }


    val arb_gnts = Wire(Vec(conf.WDMA_NUM, Bool()))
    val gnt_busy = Wire(Bool())
    val u_write_ig_arb = Module(new NV_NVDLA_arb(n = conf.WDMA_NUM, wt_width = conf.WDMA_NUM, io_gnt_busy = true))
    u_write_ig_arb.io.clk := io.nvdla_core_clk
    u_write_ig_arb.io.gnt_busy.get := gnt_busy
    for(i<- 0 to conf.WDMA_NUM-1) {
        u_write_ig_arb.io.req(i) := src_cmd_camp_vld(i)
        u_write_ig_arb.io.wt(i) := io.clients2mcif_wr_wt(i)
        arb_gnts(i) := u_write_ig_arb.io.gnt(i)
    }

    val sticky = RegInit(false.B)
    val spt_is_busy = Wire(Bool())
    val stick_gnts = RegInit(VecInit(Seq.fill(5)(false.B)))
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
        when(src_dat_vld & is_last_beat){
            sticky := false.B
        }
        .otherwise{
            sticky := true.B
        }
    }
    .elsewhen(src_dat_vld & is_last_beat){
        sticky := false.B
    }

    for(i <- 0 to conf.WDMA_NUM-1){             
        src_dat_gnts(i) := all_gnts(i) & u_dfifo(i).io.rd_pvld
    }

    src_dat_vld := src_dat_gnts.asUInt.orR      

    val gnt_count = RegInit("b0".asUInt(2.W))
    when(src_dat_vld){
        when(is_last_beat){
            gnt_count := 0.U
        }
        .otherwise{
            gnt_count := gnt_count + 1.U
        }
    }                                           

    val arb_cmd_beats = Wire(UInt(2.W))
    val arb_cmd_size = Wire(UInt(3.W))
    val arb_cmd_inc = Wire(Bool())
    is_last_beat := (gnt_count === arb_cmd_beats)   
    arb_cmd_beats := arb_cmd_size(2,1) + arb_cmd_inc


    // ARB MUX
    val arb_cmd_pd = RegInit("b0".asUInt(conf.NVDLA_DMA_WR_IG_PW.W))
    val arb_dat_pd = RegInit("b0".asUInt((conf.NVDLA_MEMIF_WIDTH+2).W))
    for(i <- 0 to conf.WDMA_NUM-1){
        when(all_gnts(i)){
            arb_cmd_pd := u_pipe(i).io.dout
            arb_dat_pd := u_dfifo(i).io.rd_pd
        } 
    }

    arb_cmd_size := arb_cmd_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+7, conf.NVDLA_MEM_ADDRESS_WIDTH+5)
    arb_cmd_inc := arb_cmd_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+10)

    io.arb2spt_cmd_pd.bits := arb_cmd_pd
    io.arb2spt_dat_pd.bits := arb_dat_pd

    io.arb2spt_cmd_pd.valid := any_arb_gnt
    io.arb2spt_dat_pd.valid := src_dat_vld

    spt_is_busy := !(io.arb2spt_cmd_pd.ready & io.arb2spt_dat_pd.ready) //fixme
}}


