// See LICENSE.txt for license details.
package nvdla

import chisel3._
import chisel3.iotesters.{Driver, TesterOptionsManager}
import utils.TutorialRunner


object cmacLauncher{  
  implicit val conf: nvdlaConfig = new nvdlaConfig
  val cmac = Map(
      "NV_NVDLA_CMAC_CORE_mac" -> { 
        (manager: TesterOptionsManager) =>
        Driver.execute(() => new NV_NVDLA_CMAC_CORE_mac(), manager) {
          (c) => new NV_NVDLA_CMAC_CORE_macTests(c)
        }
      },
      "NV_NVDLA_CMAC_CORE_active" -> { 
        (manager: TesterOptionsManager) =>
        Driver.execute(() => new NV_NVDLA_CMAC_CORE_active(), manager) {
          (c) => new NV_NVDLA_CMAC_CORE_activeTests(c)
        }
      },
      "NV_NVDLA_CMAC_CORE_rt_in" -> { 
        (manager: TesterOptionsManager) =>
        Driver.execute(() => new NV_NVDLA_CMAC_CORE_rt_in(), manager) {
          (c) => new NV_NVDLA_CMAC_CORE_rt_inTests(c)
        }
      },
      "NV_NVDLA_CMAC_CORE_rt_out" -> { 
        (manager: TesterOptionsManager) =>
        Driver.execute(() => new NV_NVDLA_CMAC_CORE_rt_out(), manager) {
          (c) => new NV_NVDLA_CMAC_CORE_rt_outTests(c)
        }
      }       
   

  )
  def main(args: Array[String]): Unit = {
    TutorialRunner("cmac", cmac, args)
  }
}

