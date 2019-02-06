// See LICENSE.txt for license details.
package nvdla

import chisel3._
import chisel3.iotesters.{Driver, TesterOptionsManager}
import utils.TutorialRunner


object cbufLauncher {  
  implicit val conf: cbufConfiguration = new cbufConfiguration
  val cbuf = Map(
      "NV_NVDLA_cbuf" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new NV_NVDLA_cbuf, manager) {
          (c) => new NV_NVDLA_cbufTests(c)
        }
      }             
   
  )
  def main(args: Array[String]): Unit = {
    TutorialRunner("cbuf", cbuf, args)
  }
}

