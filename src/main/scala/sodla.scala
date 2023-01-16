package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class sodla(implicit val conf: nvdlaConfig) extends RawModule {
    val io = IO(new Bundle {
        val core_clk = Input(Clock())
        val rstn = Input(Bool())
        val csb_rstn = Input(Bool())

        val dla_intr = Output(Bool())

        ///////////////
        //axi
        //2dbb
        val nvdla_core2dbb_aw = DecoupledIO(new nocif_axi_wr_address_if)
        val nvdla_core2dbb_aw_size = Output(UInt(3.W))
        val nvdla_core2dbb_w = DecoupledIO(new nocif_axi_wr_data_if)
        val nvdla_core2dbb_b = Flipped(DecoupledIO(new nocif_axi_wr_response_if))
        val nvdla_core2dbb_ar = DecoupledIO(new nocif_axi_rd_address_if)
        val nvdla_core2dbb_ar_size = Output(UInt(3.W))
        val nvdla_core2dbb_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if))
        //2cvsram
        val nvdla_core2cvsram_aw = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_address_if)) else None
        val nvdla_core2cvsram_w = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_data_if)) else None
        val nvdla_core2cvsram_b = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_wr_response_if))) else None
        val nvdla_core2cvsram_ar = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_rd_address_if)) else None
        val nvdla_core2cvsram_r = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_data_if))) else None
        // cfg APB
        val psel = Input(Bool())
        val penable = Input(Bool())
        val pwrite = Input(Bool())
        val paddr = Input(UInt((32).W))
        val pwdata = Input(UInt((32).W))
        val prdata = Output(UInt((32).W))
        val pready = Output(Bool())
    })

    val u_top = withClockAndReset(io.core_clk, ~io.rstn) {
      Module(new NV_nvdla)
    }

    val u_apb2csb = withClockAndReset(io.core_clk, ~io.csb_rstn) {
      Module(new NV_NVDLA_apb2csb)
    }

    u_apb2csb.io.pclk   := io.core_clk
    u_apb2csb.io.prstn  := io.csb_rstn
    u_apb2csb.io.psel   := io.psel   
    u_apb2csb.io.penable:= io.penable
    u_apb2csb.io.pwrite := io.pwrite 
    u_apb2csb.io.paddr  := io.paddr  
    u_apb2csb.io.pwdata := io.pwdata 
    io.prdata := u_apb2csb.io.prdata
    io.pready := u_apb2csb.io.pready


    io.dla_intr := u_top.io.dla_intr
    u_top.io.dla_core_clk   := io.core_clk
    u_top.io.dla_csb_clk    := io.core_clk    
    u_top.io.global_clk_ovr_on  := false.B.asClock
    u_top.io.tmc2slcg_disable_clock_gating := 0.U
    u_top.io.direct_reset_  := 1.U
    u_top.io.test_mode      := 0.U
    u_top.io.dla_reset_rstn := io.rstn
    u_top.io.csb2nvdla  <> u_apb2csb.io.csb2nvdla
    u_top.io.nvdla2csb  <> u_apb2csb.io.nvdla2csb
    u_top.io.nvdla_core2dbb_aw <> io.nvdla_core2dbb_aw
    u_top.io.nvdla_core2dbb_w  <> io.nvdla_core2dbb_w 
    u_top.io.nvdla_core2dbb_b  <> io.nvdla_core2dbb_b 
    u_top.io.nvdla_core2dbb_ar <> io.nvdla_core2dbb_ar
    u_top.io.nvdla_core2dbb_r  <> io.nvdla_core2dbb_r 

    u_top.io.nvdla_pwrbus_ram_c_pd  := 0.U(32.W)
    u_top.io.nvdla_pwrbus_ram_ma_pd := 0.U(32.W)
    u_top.io.nvdla_pwrbus_ram_mb_pd := 0.U(32.W)
    u_top.io.nvdla_pwrbus_ram_p_pd  := 0.U(32.W)
    u_top.io.nvdla_pwrbus_ram_o_pd  := 0.U(32.W)
    u_top.io.nvdla_pwrbus_ram_a_pd  := 0.U(32.W)

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.nvdla_core2cvsram_ar.get <> u_top.io.nvdla_core2cvsram_ar.get
        io.nvdla_core2cvsram_aw.get <> u_top.io.nvdla_core2cvsram_aw.get 
        io.nvdla_core2cvsram_w.get <> u_top.io.nvdla_core2cvsram_w.get 
        u_top.io.nvdla_core2cvsram_b.get <> io.nvdla_core2cvsram_b.get
        u_top.io.nvdla_core2cvsram_r.get <> io.nvdla_core2cvsram_r.get
    }

  io.nvdla_core2dbb_aw_size := 3.U(3.W)
  io.nvdla_core2dbb_ar_size := 3.U(3.W)
}

object SO_smallDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new sodla())
}

object SO_largeDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new sodla())
}