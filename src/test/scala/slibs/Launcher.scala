// See LICENSE.txt for license details.
package nvdla

import chisel3._
import chisel3.iotesters.{Driver, TesterOptionsManager}
import utils.TutorialRunner

object Launcher {
  val slibs = Map(
      "AN2D4PO4" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new AN2D4PO4(), manager) {
          (c) => new AN2D4PO4Tests(c)
        }
      },
      "OR2D1" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new OR2D1(), manager) {
          (c) => new OR2D1Tests(c)
        }
      },
      "CKLNQD12" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new CKLNQD12(), manager) {
          (c) => new CKLNQD12Tests(c)
        }
      },
      "MUX2HDD2" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new MUX2HDD2(), manager) {
          (c) => new MUX2HDD2Tests(c)
        }
      },
      "MUX2D4" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new MUX2D4(), manager) {
          (c) => new MUX2D4Tests(c)
        }
      },
      "NV_CLK_gate_power" -> { (manager: TesterOptionsManager) =>
        Driver.execute(() => new NV_CLK_gate_power(), manager) {
          (c) => new NV_CLK_gate_powerTests(c)
        }
      }         

  )
  def main(args: Array[String]): Unit = {
    TutorialRunner("slibs", slibs, args)
  }
}

