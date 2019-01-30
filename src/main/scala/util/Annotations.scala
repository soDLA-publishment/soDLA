// See LICENSE.SiFive for license details.

package nvdla

import chisel3._
import chisel3.internal.InstanceId
import chisel3.experimental.{annotate, ChiselAnnotation, RawModule}
import firrtl.annotations._

import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods.{pretty, render}

/** Record a sram. */
case class SRAMAnnotation(target: Named,
  address_width: Int,
  name: String,
  data_width: Int,
  depth: Int,
  description: String,
  write_mask_granularity: Int) extends SingleTargetAnnotation[Named] {
  def duplicate(n: Named) = this.copy(n)
}

case class RetimeModuleAnnotation(target: ModuleName) extends SingleTargetAnnotation[ModuleName] {
  def duplicate(n: ModuleName) = this.copy(n)
}

/** Mix this into a Module class or instance to mark it for register retiming */
trait ShouldBeRetimed { self: RawModule =>
  chisel3.experimental.annotate(new ChiselAnnotation { def toFirrtl: RetimeModuleAnnotation = RetimeModuleAnnotation(self.toNamed) })
}







