// See LICENSE.txt for license details.
package cora

import chisel3._
import chisel3.iotesters.{Driver, TesterOptionsManager}
import utils.TutorialRunner

object matrixLauncher {
  implicit val conf: matrixConfiguration = new matrixConfiguration
  val matrix = Map(
    "RED_CORA_MATRIX_v2v_fp_noshareFMA" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new RED_CORA_MATRIX_v2v_fp_noshareFMA, manager) {
        (c) => new RED_CORA_MATRIX_v2v_fp_noshareFMATests(c)
      }
    },
    "RED_CORA_MATRIX_v2m" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new RED_CORA_MATRIX_v2m, manager) {
        (c) => new RED_CORA_MATRIX_v2mTests(c)
      }
    }        
  )

  def main(args: Array[String]): Unit = {
    TutorialRunner("matrix", matrix, args)
  }
}
