package np.devices.sodla

import chisel3._
import freechips.rocketchip.config._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.amba.axi4._
import freechips.rocketchip.amba.apb._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.interrupts._
import freechips.rocketchip.subsystem._

import np.devices.ip.sodla._

case class SODLAParams(
  config: String,
  raddress: BigInt,
  synthRAMs: Boolean = false
)

class SODLA(params: SODLAParams)(implicit p: Parameters) extends LazyModule {
  // val blackboxName = "nvdla_" + params.config
  val hasSecondAXI = params.config == "small"
  val dataWidthAXI = if (params.config == "large") 256 else 64

  // DTS
  val dtsdevice = new SimpleDevice("sodla",Seq("nvidia,nv_" + params.config))

  // dbb TL
  val dbb_tl_node = TLIdentityNode()

  // dbb AXI
  val dbb_axi_node = AXI4MasterNode(
    Seq(
      AXI4MasterPortParameters(
        masters = Seq(AXI4MasterParameters(
          name    = "SODLA DBB",
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
          name    = "SODLA CVSRAM",
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

    val u_sodla = Module(new sodla(params.config, hasSecondAXI, dataWidthAXI))
    u_sodla.io.io_core_clk    := clock
    u_sodla.io.io_rstn        := ~reset.asBool
    u_sodla.io.io_csb_rstn    := ~reset.asBool

    val (dbb, _) = dbb_axi_node.out(0)

    dbb.aw.valid                            := u_sodla.io.io_nvdla_core2dbb_aw_valid
    u_sodla.io.io_nvdla_core2dbb_aw_ready   := dbb.aw.ready
    dbb.aw.bits.id                          := u_sodla.io.io_nvdla_core2dbb_aw_bits_id
    dbb.aw.bits.len                         := u_sodla.io.io_nvdla_core2dbb_aw_bits_len
    dbb.aw.bits.size                        := u_sodla.io.io_nvdla_core2dbb_aw_size
    dbb.aw.bits.addr                        := u_sodla.io.io_nvdla_core2dbb_aw_bits_addr

    dbb.w.valid                             := u_sodla.io.io_nvdla_core2dbb_w_valid
    u_sodla.io.io_nvdla_core2dbb_w_ready    := dbb.w.ready
    dbb.w.bits.data                         := u_sodla.io.io_nvdla_core2dbb_w_bits_data
    dbb.w.bits.strb                         := u_sodla.io.io_nvdla_core2dbb_w_bits_strb
    dbb.w.bits.last                         := u_sodla.io.io_nvdla_core2dbb_w_bits_last

    dbb.ar.valid                            := u_sodla.io.io_nvdla_core2dbb_ar_valid
    u_sodla.io.io_nvdla_core2dbb_ar_ready   := dbb.ar.ready
    dbb.ar.bits.id                          := u_sodla.io.io_nvdla_core2dbb_ar_bits_id
    dbb.ar.bits.len                         := u_sodla.io.io_nvdla_core2dbb_ar_bits_len
    dbb.ar.bits.size                        := u_sodla.io.io_nvdla_core2dbb_ar_size
    dbb.ar.bits.addr                        := u_sodla.io.io_nvdla_core2dbb_ar_bits_addr

    u_sodla.io.io_nvdla_core2dbb_b_valid    := dbb.b.valid
    dbb.b.ready                             := u_sodla.io.io_nvdla_core2dbb_b_ready
    u_sodla.io.io_nvdla_core2dbb_b_bits_id  := dbb.b.bits.id

    u_sodla.io.io_nvdla_core2dbb_r_valid    := dbb.r.valid
    dbb.r.ready                             := u_sodla.io.io_nvdla_core2dbb_r_ready
    u_sodla.io.io_nvdla_core2dbb_r_bits_id  := dbb.r.bits.id
    u_sodla.io.io_nvdla_core2dbb_r_bits_last:= dbb.r.bits.last
    u_sodla.io.io_nvdla_core2dbb_r_bits_data:= dbb.r.bits.data

    // u_sodla.io.nvdla_core2cvsram.foreach { u_sodla_cvsram =>
    //   val (cvsram, _) = cvsram_axi_node.get.out(0)

    //   cvsram.aw.valid                       := u_sodla_cvsram.aw_awvalid
    //   u_sodla_cvsram.aw_awready             := cvsram.aw.ready
    //   cvsram.aw.bits.id                     := u_sodla_cvsram.aw_awid
    //   cvsram.aw.bits.len                    := u_sodla_cvsram.aw_awlen
    //   cvsram.aw.bits.size                   := u_sodla_cvsram.aw_awsize
    //   cvsram.aw.bits.addr                   := u_sodla_cvsram.aw_awaddr

    //   cvsram.w.valid                        := u_sodla_cvsram.w_wvalid
    //   u_sodla_cvsram.w_wready               := cvsram.w.ready
    //   cvsram.w.bits.data                    := u_sodla_cvsram.w_wdata
    //   cvsram.w.bits.strb                    := u_sodla_cvsram.w_wstrb
    //   cvsram.w.bits.last                    := u_sodla_cvsram.w_wlast

    //   cvsram.ar.valid                       := u_sodla_cvsram.ar_arvalid
    //   u_sodla_cvsram.ar_arready             := cvsram.ar.ready
    //   cvsram.ar.bits.id                     := u_sodla_cvsram.ar_arid
    //   cvsram.ar.bits.len                    := u_sodla_cvsram.ar_arlen
    //   cvsram.ar.bits.size                   := u_sodla_cvsram.ar_arsize
    //   cvsram.ar.bits.addr                   := u_sodla_cvsram.ar_araddr

    //   u_sodla_cvsram.b_bvalid               := cvsram.b.valid
    //   cvsram.b.ready                        := u_sodla_cvsram.b_bready
    //   u_sodla_cvsram.b_bid                  := cvsram.b.bits.id

    //   u_sodla_cvsram.r_rvalid               := cvsram.r.valid
    //   cvsram.r.ready                        := u_sodla_cvsram.r_rready
    //   u_sodla_cvsram.r_rid                  := cvsram.r.bits.id
    //   u_sodla_cvsram.r_rlast                := cvsram.r.bits.last
    //   u_sodla_cvsram.r_rdata                := cvsram.r.bits.data
    // }

    val (cfg, _) = cfg_apb_node.in(0)

    u_sodla.io.io_psel      := cfg.psel
    u_sodla.io.io_penable   := cfg.penable
    u_sodla.io.io_pwrite    := cfg.pwrite
    u_sodla.io.io_paddr     := cfg.paddr
    u_sodla.io.io_pwdata    := cfg.pwdata
    cfg.prdata              := u_sodla.io.io_prdata
    cfg.pready              := u_sodla.io.io_pready
    cfg.pslverr             := false.B

    val (io_int, _) = int_node.out(0)

    io_int(0)   := u_sodla.io.io_dla_intr
  }
}


