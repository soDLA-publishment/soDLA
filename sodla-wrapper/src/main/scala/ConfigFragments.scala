// See LICENSE for license details.
package np.blocks.dla

import chisel3._

import org.chipsalliance.cde.config.{Field, Parameters, Config}

/**
 * Config fragment to add a NVDLA to the SoC.
 * Supports "small" and "large" configs only.
 * Can enable synth. RAMs instead of default FPGA RAMs.
 */
class WithSODLA(config: String, synthRAMs: Boolean = false) extends Config((site, here, up) => {
  case SODLAKey => Some(SODLAParams(config = config, raddress = 0x10040000L, synthRAMs = synthRAMs))
  case SODLAFrontBusExtraBuffers => 0
})
