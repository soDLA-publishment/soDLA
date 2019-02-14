// See LICENSE.txt for license details.
package nvdla

import chisel3._
import chisel3.iotesters.{Driver, TesterOptionsManager}
import utils.TutorialRunner


object ramLauncher {  
  val ram = Map(
      "nv_ram_rws" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new nv_ram_rws(64, 32), manager) {
          (c) => new nv_ram_rwsTests(c)
        }
      },
      "nv_ram_rwsp" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new nv_ram_rwsp(64, 32), manager) {
          (c) => new nv_ram_rwspTests(c)
        }
      }

   
  )
  def main(args: Array[String]): Unit = {
    TutorialRunner("ram", ram, args)
  }
}

