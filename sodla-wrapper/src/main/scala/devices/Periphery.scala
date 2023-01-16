package np.devices.sodla

import chisel3._
import freechips.rocketchip.config.Field
import freechips.rocketchip.subsystem.BaseSubsystem
import freechips.rocketchip.diplomacy.{LazyModule,BufferParams}
import freechips.rocketchip.tilelink.{TLBuffer, TLIdentityNode}

case object SODLAKey extends Field[Option[SODLAParams]](None)
case object SODLAFrontBusExtraBuffers extends Field[Int](0)

trait CanHavePeripherySODLA { this: BaseSubsystem =>
  p(SODLAKey).map { params =>
    val sodla = LazyModule(new SODLA(params))

    fbus.fromMaster(name = Some("sodla_dbb"), buffer = BufferParams.default) {
      TLBuffer.chainNode(p(SODLAFrontBusExtraBuffers))
    } := sodla.dbb_tl_node

    pbus.toFixedWidthSingleBeatSlave(4, Some("sodla_cfg")) { sodla.cfg_tl_node }

    ibus.fromSync := sodla.int_node
  }
}
