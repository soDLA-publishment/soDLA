// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util.HasBlackBoxResource


// class HLS_fp17_add extends BlackBox with HasBlackBoxResource{
//   val io = IO(new Bundle{
//     val nvdla_core_clk = Input(Bool())

//     val chn_a_rsc_z = Input(UInt(17.W))
//     val chn_a_rsc_vz = Input(Bool())
//     val chn_a_rsc_lz = Output(Bool())

//     val chn_b_rsc_z = Input(UInt(17.W))
//     val chn_b_rsc_vz = Input(Bool())
//     val chn_b_rsc_lz = Output(Bool())

//     val chn_o_rsc_z = Output(UInt(17.W))
//     val chn_o_rsc_vz = Input(Bool())
//     val chn_o_rsc_lz = Output(Bool())
//   })
//   setResource("../vlibs/HLS_fp17_add.v")
// }


// object HLS_fp17_addDriver extends App {
//   chisel3.Driver.execute(args, () => new HLS_fp17_add())
// }


