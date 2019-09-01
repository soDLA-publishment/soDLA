// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver

// class NV_NVDLA_SDP_RDMA_pack(IW: Int = 512, OW: Int = 256, CW: Int = 1)(implicit val conf: nvdlaConfig) extends Module {
//    val RATIO = IW/OW
//    val io = IO(new Bundle {
//         //in clock
//         val nvdla_core_clk = Input(Clock())

//         val cfg_dp_8 = Input(Bool())
        
//         val inp_pvld = Input(Bool())
//         val inp_prdy = Output(Bool())
//         val inp_data = Input(UInt((IW+CW).W))

//         val out_pvld = Output(Bool())
//         val out_prdy = Input(Bool())
//         val out_data = Output(UInt((OW+CW).W))

//     })
//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │
//     //       │                 │
//     //       └───┐         ┌───┘
//     //           │         │
//     //           │         │
//     //           │         │
//     //           │         └──────────────┐
//     //           │                        │
//     //           │                        ├─┐
//     //           │                        ┌─┘    
//     //           │                        │
//     //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //             │ ─┤ ─┤       │ ─┤ ─┤         
//     //             └──┴──┘       └──┴──┘ 
// withClock(io.nvdla_core_clk){

//     val ctrl_end = Wire(UInt(CW.W))
//     val mux_data = Wire(UInt(OW.W))
//     io.out_data := Cat(ctrl_end, mux_data)

//     val pack_prdy = io.out_prdy

//     val pack_pvld = RegInit(false.B)
//     pack_pvld := io.inp_pvld
//     io.out_pvld := pack_pvld

//     val is_pack_last = Wire(Bool())
//     io.inp_prdy := (!pack_pvld) | (pack_prdy & is_pack_last)

//     val inp_acc = io.inp_pvld & io.inp_prdy
//     val out_acc = io.out_pvld & io.out_prdy

//     val ctrl_done = Reg(UInt(CW.W))
//     when(inp_acc){
//         ctrl_done := io.inp_data(IW+CW-1,IW)
//     }.elsewhen(out_acc & is_pack_last){
//         ctrl_done := 0.U
//     }

//     ctrl_end := ctrl_done & Fill(CW, is_pack_last)

//     //push data 
//     val pack_data = Reg(UInt(IW.W))
//     when(inp_acc){
//         pack_data := io.inp_data(IW-1,0)
//     }
    
//     val pack_data_ext = Wire(UInt((OW*16).W))
//     pack_data_ext := pack_data
    
//     val pack_cnt = RegInit(0.U(4.W))
//     when(out_acc){
//         when(is_pack_last){
//             pack_cnt := 0.U
//         }.otherwise{
//             pack_cnt := pack_cnt + 1.U
//         }
//     }
    
//     is_pack_last := Mux(!io.cfg_dp_8, (pack_cnt===(RATIO/2-1).U), (pack_cnt===(RATIO-1).U))

//     val pack_seg = VecInit((0 to RATIO-1) 
//                     map {i => pack_data_ext((OW*i + OW - 1), OW*i)})

//     mux_data := MuxLookup(
//                         pack_cnt, 
//                         "b0".asUInt(OW.W),
//                         (0 to RATIO-1) map {i => i.U -> pack_seg(i)})

//     }
// }


// object NV_NVDLA_SDP_RDMA_packDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_pack())
// }
