// See LICENSE for license details.
package np.blocks.dla

import chisel3._
import org.chipsalliance.cde.config._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.amba.axi4._
import freechips.rocketchip.amba.apb._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.interrupts._
import freechips.rocketchip.subsystem._

import np.blocks.ip.dla._

case class SODLAParams(
  config: String,
  raddress: BigInt,
  synthRAMs: Boolean = false
)

class SODLA(params: SODLAParams)(implicit p: Parameters) extends LazyModule {

  //val blackboxName = "nvdla_" + params.config
  val hasSecondAXI = params.config == "large"
  val dataWidthAXI = if (params.config == "large") 256 else 64

  // DTS
  val dtsdevice = new SimpleDevice("nvdla",Seq("nvidia,nv_" + params.config))

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
    := AXI4UserYanker(capMaxFlight=Some(16))
    := AXI4Fragmenter()
    := AXI4IdIndexer(idBits=3)
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

    val u_nvdla = Module(new sodla(params.config, hasSecondAXI, dataWidthAXI))

    u_nvdla.io.io_core_clk    := clock
    u_nvdla.io.io_rstn        := ~reset.asBool
    u_nvdla.io.io_csb_rstn    := ~reset.asBool

    val (dbb, _) = dbb_axi_node.out(0)

    dbb.aw.valid                            := u_nvdla.io.io_nvdla_core2dbb_aw_valid
    u_nvdla.io.io_nvdla_core2dbb_aw_ready    := dbb.aw.ready
    dbb.aw.bits.id                          := u_nvdla.io.io_nvdla_core2dbb_aw_bits_id
    dbb.aw.bits.len                         := u_nvdla.io.io_nvdla_core2dbb_aw_bits_len
    dbb.aw.bits.size                        := u_nvdla.io.io_nvdla_core2dbb_aw_size
    dbb.aw.bits.addr                        := u_nvdla.io.io_nvdla_core2dbb_aw_bits_addr

    dbb.w.valid                             := u_nvdla.io.io_nvdla_core2dbb_w_valid
    u_nvdla.io.io_nvdla_core2dbb_w_ready      := dbb.w.ready
    dbb.w.bits.data                         := u_nvdla.io.io_nvdla_core2dbb_w_bits_data
    dbb.w.bits.strb                         := u_nvdla.io.io_nvdla_core2dbb_w_bits_strb
    dbb.w.bits.last                         := u_nvdla.io.io_nvdla_core2dbb_w_bits_last

    dbb.ar.valid                            := u_nvdla.io.io_nvdla_core2dbb_ar_valid
    u_nvdla.io.io_nvdla_core2dbb_ar_ready    := dbb.ar.ready
    dbb.ar.bits.id                          := u_nvdla.io.io_nvdla_core2dbb_ar_bits_id
    dbb.ar.bits.len                         := u_nvdla.io.io_nvdla_core2dbb_ar_bits_len
    dbb.ar.bits.size                        := u_nvdla.io.io_nvdla_core2dbb_ar_size
    dbb.ar.bits.addr                        := u_nvdla.io.io_nvdla_core2dbb_ar_bits_addr

    u_nvdla.io.io_nvdla_core2dbb_b_valid      := dbb.b.valid
    dbb.b.ready                             := u_nvdla.io.io_nvdla_core2dbb_b_ready
    u_nvdla.io.io_nvdla_core2dbb_b_bits_id         := dbb.b.bits.id

    u_nvdla.io.io_nvdla_core2dbb_r_valid      := dbb.r.valid
    dbb.r.ready                             := u_nvdla.io.io_nvdla_core2dbb_r_ready
    u_nvdla.io.io_nvdla_core2dbb_r_bits_id         := dbb.r.bits.id
    u_nvdla.io.io_nvdla_core2dbb_r_bits_last       := dbb.r.bits.last
    u_nvdla.io.io_nvdla_core2dbb_r_bits_data       := dbb.r.bits.data

    val (cfg, _) = cfg_apb_node.in(0)

    u_nvdla.io.io_psel         := cfg.psel
    u_nvdla.io.io_penable      := cfg.penable
    u_nvdla.io.io_pwrite       := cfg.pwrite
    u_nvdla.io.io_paddr        := cfg.paddr
    u_nvdla.io.io_pwdata       := cfg.pwdata
    cfg.prdata              := u_nvdla.io.io_prdata
    cfg.pready              := u_nvdla.io.io_pready
    cfg.pslverr             := false.B

    val (io_int, _) = int_node.out(0)

    io_int(0)   := u_nvdla.io.io_dla_intr
  }
}


