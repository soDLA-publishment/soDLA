// See LICENSE.txt for license details.
package nvdla

import chisel3._
import chisel3.iotesters.{Driver, TesterOptionsManager}
import utils.TutorialRunner

object FPULauncher {
  val fpu = Map(
    "MulAddRecFNPipe" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new MulAddRecFNPipe(2, 18, 18), manager) {
        (c) => new MulAddRecFNPipeTests(c)
      }
    }

  )

  def main(args: Array[String]): Unit = {
    TutorialRunner("FPU", fpu, args)
  }
}
