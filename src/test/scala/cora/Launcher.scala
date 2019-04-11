// // See LICENSE.txt for license details.
// package cora

// import chisel3._
// import chisel3.iotesters.{Driver, TesterOptionsManager}
// import utils.TutorialRunner

// object matrixLauncher {
//   implicit val conf: matrixConfiguration = new matrixConfiguration
//   val matrix = Map(
//     "CORA_MATRIX_MUL_v2v_for_verify" -> { (manager: TesterOptionsManager) =>
//       Driver.execute(() => new CORA_MATRIX_MUL_v2v_for_verify, manager) {
//         (c) => new CORA_MATRIX_MUL_v2v_for_verifyTests(c)
//       }
//     },

//   )

//   def main(args: Array[String]): Unit = {
//     TutorialRunner("matrix", matrix, args)
//   }
// }
