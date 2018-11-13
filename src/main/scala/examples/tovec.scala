
// class ReshapeVec extends Module {
//   val io = IO (new Bundle {
//     val in = Input(Vec(12, UInt(8.W)))
//     val out = Output(Vec(3, Vec(4, UInt(8.W))))
//   })
//   // Note I don't wrap outer Seq in VecInit because Chisel allows you to connect Scala Seqs to Chisel Vecs
//   io.out := io.in.grouped(4).map(VecInit(_)).toSeq
// }
