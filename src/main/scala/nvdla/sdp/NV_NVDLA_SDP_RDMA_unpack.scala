// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver

// class NV_NVDLA_SDP_RDMA_unpack(implicit val conf: nvdlaConfig) extends Module {
//    val RATIO = 4*conf.AM_DW/conf.NVDLA_MEMIF_WIDTH
//    val io = IO(new Bundle {
//         //in clock
//         val nvdla_core_clk = Input(Clock())

//         // val cfg_dp_8 = Input(Bool())

//         val inp_end = Input(Bool())
//         val inp = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))

//         val out = DecoupledIO(UInt((4*conf.AM_DW+4).W))

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

//     val pack_pvld = RegInit(false.B)

//     val pack_prdy = io.out.ready
//     io.out.valid := pack_pvld
//     io.inp.ready := (!pack_pvld) | pack_prdy

//     val is_pack_last = Wire(Bool())
//     when(io.inp.ready){
//         pack_pvld := io.inp.valid & is_pack_last
//     }
//     val inp_acc = io.inp.valid & io.inp.ready

//     val data_mask = Cat(Fill(4-conf.NVDLA_DMA_MASK_BIT, false.B), io.inp.bits)
//     val data_size = PopCount(data_mask)
//     val pack_cnt = RegInit(0.U)
//     val pack_cnt_nxt = pack_cnt + data_size
//     when(inp_acc){
//         when(is_pack_last){
//             pack_cnt := 0.U
//         }
//         .otherwise{
//             pack_cnt := pack_cnt_nxt
//         }
//     }
//     is_pack_last := (pack_cnt_nxt === 4.U) | io.inp_end;

//     val pack_mask = RegInit(0.U)
//     when(inp_acc & is_pack_last){
//         pack_mask := 1.U << pack_cnt_nxt - 1.U
//     }

//     val pack_seq = Reg(Vec(RATIO, UInt(conf.AM_DW.W)))
        
//     when(inp_acc){
//         for(i <- 0 to RATIO-1){
//             pack_seq(i) := io.inp.bits(4/RATIO*conf.AM_DW-1, 0)
//         }
//     }

//     val pack_total = pack_seq.asUInt
//     io.out.bits := Cat(pack_mask, pack_total)

// }}


// object NV_NVDLA_SDP_RDMA_unpackDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_unpack())
// }