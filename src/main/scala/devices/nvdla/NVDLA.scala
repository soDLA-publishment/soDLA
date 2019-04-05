// See LICENSE for license details.
package nvidia.blocks.dla

import Chisel._
import freechips.rocketchip.config._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.amba.axi4._
import freechips.rocketchip.amba.apb._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.interrupts._
import freechips.rocketchip.subsystem._

import nvidia.blocks.ip.dla._

case class NVDLAParams(
  config: String,
  raddress: BigInt
)

class NVDLA(params: NVDLAParams, val crossing: ClockCrossingType = AsynchronousCrossing(8, 3))(implicit p: Parameters) extends LazyModule with HasCrossing {

  val blackboxName = "nvdla_" + params.config
  val hasSecondAXI = params.config == "large"
  val dataWidthAXI = if (params.config == "large") 256 else 64

  // DTS
  val dtsdevice = new SimpleDevice("nvdla",Seq("nvidia,nvdla_2"))

  // dbb TL
  val dbb_tl_node = TLIdentityNode()

  // dbb AXI
  val dbb_axi_node = AXI4MasterNode(
    Seq(
      AXI4MasterPortParameters(
        masters = Seq(AXI4MasterParameters(
          name    = "NVDLA DBB",
          id      = IdRange(0, 256))))))

  // TL <-> AXI
  (dbb_tl_node
    := TLBuffer()
    := TLWidthWidget(dataWidthAXI/8)
    := AXI4ToTL()
    := AXI4UserYanker(capMaxFlight=Some(8))
    := AXI4Fragmenter()
    := AXI4IdIndexer(idBits=2)
    := AXI4Buffer()
    := dbb_axi_node)

  // cvsram AXI
  val cvsram_axi_node = if (hasSecondAXI) Some(AXI4MasterNode(
    Seq(
      AXI4MasterPortParameters(
        masters = Seq(AXI4MasterParameters(
          name    = "NVDLA CVSRAM",
          id      = IdRange(0, 256)))))))
  else None

  cvsram_axi_node.foreach {
    val sram = if (hasSecondAXI) Some(LazyModule(new AXI4RAM(
      address = AddressSet(0, 1*1024-1),
      beatBytes = dataWidthAXI/8)))
    else None
      sram.get.node := _
  }

  // cfg APB
  val cfg_apb_node = APBSlaveNode(
    Seq(
      APBSlavePortParameters(
        slaves = Seq(APBSlaveParameters(
          address       = Seq(AddressSet(params.raddress, 0x40000L-1L)), // 256KB
          resources     = dtsdevice.reg("control"),
          executable    = false,
          supportsWrite = true,
          supportsRead  = true)),
        beatBytes = 4)))

  val cfg_tl_node = cfg_apb_node := LazyModule(new TLToAPB).node

  val int_node = IntSourceNode(IntSourcePortSimple(num = 1, resources = dtsdevice.int))


  lazy val module = new LazyModuleImp(this) {

    val u_nvdla = Module(new nvdla(blackboxName, hasSecondAXI, dataWidthAXI))

    u_nvdla.io.core_clk    := clock
    u_nvdla.io.csb_clk     := clock
    u_nvdla.io.rstn        := ~reset
    u_nvdla.io.csb_rstn    := ~reset

    val (dbb, _) = dbb_axi_node.out(0)

    dbb.aw.valid                            := u_nvdla.io.nvdla_core2dbb_aw_awvalid
    u_nvdla.io.nvdla_core2dbb_aw_awready    := dbb.aw.ready
    dbb.aw.bits.id                          := u_nvdla.io.nvdla_core2dbb_aw_awid
    dbb.aw.bits.len                         := u_nvdla.io.nvdla_core2dbb_aw_awlen
    dbb.aw.bits.size                        := u_nvdla.io.nvdla_core2dbb_aw_awsize
    dbb.aw.bits.addr                        := u_nvdla.io.nvdla_core2dbb_aw_awaddr

    dbb.w.valid                             := u_nvdla.io.nvdla_core2dbb_w_wvalid
    u_nvdla.io.nvdla_core2dbb_w_wready      := dbb.w.ready
    dbb.w.bits.data                         := u_nvdla.io.nvdla_core2dbb_w_wdata
    dbb.w.bits.strb                         := u_nvdla.io.nvdla_core2dbb_w_wstrb
    dbb.w.bits.last                         := u_nvdla.io.nvdla_core2dbb_w_wlast

    dbb.ar.valid                            := u_nvdla.io.nvdla_core2dbb_ar_arvalid
    u_nvdla.io.nvdla_core2dbb_ar_arready    := dbb.ar.ready
    dbb.ar.bits.id                          := u_nvdla.io.nvdla_core2dbb_ar_arid
    dbb.ar.bits.len                         := u_nvdla.io.nvdla_core2dbb_ar_arlen
    dbb.ar.bits.size                        := u_nvdla.io.nvdla_core2dbb_ar_arsize
    dbb.ar.bits.addr                        := u_nvdla.io.nvdla_core2dbb_ar_araddr

    u_nvdla.io.nvdla_core2dbb_b_bvalid      := dbb.b.valid
    dbb.b.ready                             := u_nvdla.io.nvdla_core2dbb_b_bready
    u_nvdla.io.nvdla_core2dbb_b_bid         := dbb.b.bits.id

    u_nvdla.io.nvdla_core2dbb_r_rvalid      := dbb.r.valid
    dbb.r.ready                             := u_nvdla.io.nvdla_core2dbb_r_rready
    u_nvdla.io.nvdla_core2dbb_r_rid         := dbb.r.bits.id
    u_nvdla.io.nvdla_core2dbb_r_rlast       := dbb.r.bits.last
    u_nvdla.io.nvdla_core2dbb_r_rdata       := dbb.r.bits.data

    u_nvdla.io.nvdla_core2cvsram.foreach { u_nvdla_cvsram =>
      val (cvsram, _) = cvsram_axi_node.get.out(0)

      cvsram.aw.valid                       := u_nvdla_cvsram.aw_awvalid
      u_nvdla_cvsram.aw_awready             := cvsram.aw.ready
      cvsram.aw.bits.id                     := u_nvdla_cvsram.aw_awid
      cvsram.aw.bits.len                    := u_nvdla_cvsram.aw_awlen
      cvsram.aw.bits.size                   := u_nvdla_cvsram.aw_awsize
      cvsram.aw.bits.addr                   := u_nvdla_cvsram.aw_awaddr

      cvsram.w.valid                        := u_nvdla_cvsram.w_wvalid
      u_nvdla_cvsram.w_wready               := cvsram.w.ready
      cvsram.w.bits.data                    := u_nvdla_cvsram.w_wdata
      cvsram.w.bits.strb                    := u_nvdla_cvsram.w_wstrb
      cvsram.w.bits.last                    := u_nvdla_cvsram.w_wlast

      cvsram.ar.valid                       := u_nvdla_cvsram.ar_arvalid
      u_nvdla_cvsram.ar_arready             := cvsram.ar.ready
      cvsram.ar.bits.id                     := u_nvdla_cvsram.ar_arid
      cvsram.ar.bits.len                    := u_nvdla_cvsram.ar_arlen
      cvsram.ar.bits.size                   := u_nvdla_cvsram.ar_arsize
      cvsram.ar.bits.addr                   := u_nvdla_cvsram.ar_araddr

      u_nvdla_cvsram.b_bvalid               := cvsram.b.valid
      cvsram.b.ready                        := u_nvdla_cvsram.b_bready
      u_nvdla_cvsram.b_bid                  := cvsram.b.bits.id

      u_nvdla_cvsram.r_rvalid               := cvsram.r.valid
      cvsram.r.ready                        := u_nvdla_cvsram.r_rready
      u_nvdla_cvsram.r_rid                  := cvsram.r.bits.id
      u_nvdla_cvsram.r_rlast                := cvsram.r.bits.last
      u_nvdla_cvsram.r_rdata                := cvsram.r.bits.data
    }

    val (cfg, _) = cfg_apb_node.in(0)

    u_nvdla.io.psel         := cfg.psel
    u_nvdla.io.penable      := cfg.penable
    u_nvdla.io.pwrite       := cfg.pwrite
    u_nvdla.io.paddr        := cfg.paddr
    u_nvdla.io.pwdata       := cfg.pwdata
    cfg.prdata              := u_nvdla.io.prdata
    cfg.pready              := u_nvdla.io.pready
    cfg.pslverr             := Bool(false)

    val (io_int, _) = int_node.out(0)

    io_int(0)   := u_nvdla.io.dla_intr
  }
}


