// See LICENSE for license details.
package np.blocks.dla

import chisel3._
import org.chipsalliance.cde.config.Field
import freechips.rocketchip.subsystem.BaseSubsystem
import freechips.rocketchip.diplomacy.{LazyModule,BufferParams}
import freechips.rocketchip.tilelink.{TLBuffer, TLIdentityNode, TLWidthWidget, TLFragmenter}

case object SODLAKey extends Field[Option[SODLAParams]](None)
case object SODLAFrontBusExtraBuffers extends Field[Int](0)

trait CanHavePeripherySODLA { this: BaseSubsystem =>
  p(SODLAKey).map { params =>
    val nvdla = LazyModule(new SODLA(params))

    fbus.coupleFrom("nvdla_dbb") { _ := TLBuffer.chainNode(p(SODLAFrontBusExtraBuffers)) := nvdla.dbb_tl_node }
    pbus.coupleTo("nvdla_cfg") { nvdla.cfg_tl_node := TLFragmenter(4, pbus.blockBytes) := TLWidthWidget(pbus.beatBytes) := _ }

    ibus.fromSync := nvdla.int_node
  }
}
