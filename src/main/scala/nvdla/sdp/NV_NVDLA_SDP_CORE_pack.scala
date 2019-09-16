// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver

// class NV_NVDLA_SDP_CORE_pack(IW: Int = 512, OW: Int = 128, CW: Int = 1, io_cfg_dp_8: Boolean = false) extends Module {
//    val RATIO = IW/OW
//    val io = IO(new Bundle {
//         //in clock
//         val nvdla_core_clk = Input(Clock())

//         val inp = Flipped(DecoupledIO(UInt((IW+CW).W)))
//         val out = DecoupledIO(UInt((OW+CW).W))

//         val cfg_dp_8 = if(io_cfg_dp_8) Some(Input(Bool())) else None

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

//     val is_pack_last = Wire(Bool())
//     val mux_data = Wire(UInt(OW.W))
//     io.out_data := mux_data

//     val pack_pvld = RegInit(false.B)
//     val pack_prdy = io.out_prdy 
//     io.out_pvld := pack_pvld
//     io.inp_prdy := (!pack_pvld) | (pack_prdy & is_pack_last)

//     when(io.inp_prdy){
//         pack_pvld := io.inp_pvld
//     }

//     val inp_acc = io.inp_pvld & io.inp_prdy
//     val out_acc = io.out_pvld & io.out_prdy

//     //push data 
//     val pack_data = Reg(UInt(IW.W))
//     when(inp_acc){
//         pack_data := io.inp_data
//     }

//     val pack_data_ext = Wire(UInt((OW*16).W))
//     pack_data_ext := pack_data

//     val pack_cnt = RegInit("h0".asUInt(4.W))
//     when(out_acc){
//         when(is_pack_last){
//             pack_cnt := "h0".asUInt(4.W)
//         }
//         .otherwise{
//             pack_cnt := pack_cnt + 1.U
//         }
//     }

//     is_pack_last := (pack_cnt === (RATIO-1).U)

//     val pack_seg = VecInit((0 to 15) map { i => pack_data_ext(i*OW + OW -1, i*OW)})

//     mux_data := MuxLookup(pack_cnt, "b0".asUInt(OW.W),
//                 (0 to RATIO-1) map { i => i.U -> pack_seg(i)})



// }}


// object NV_NVDLA_SDP_CORE_packDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_CORE_pack())
// }
