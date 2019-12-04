//  package nvdla

//  import chisel3._
//  import chisel3.util._


//  class NV_NVDLA_NOCIF_DRAM_READ_IG_spt(implicit conf:nvdlaConfig) extends Module {
//      val io = IO(new Bundle {
//          //general clock
//          val nvdla_core_clk = Input(Clock())

//         // arb2spt
//         val arb2spt_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_IG_PW.W)))

//         //spt2cvt
//         val spt2cvt_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_IG_PW.W))

//      })
//  //
//  //          ┌─┐       ┌─┐
//  //       ┌──┘ ┴───────┘ ┴──┐
//  //       │                 │
//  //       │       ───       │
//  //       │  ─┬┘       └┬─  │
//  //       │                 │
//  //       │       ─┴─       │
//  //       │                 │
//  //       └───┐         ┌───┘
//  //           │         │
//  //           │         │
//  //           │         │
//  //           │         └──────────────┐
//  //           │                        │
//  //           │                        ├─┐
//  //           │                        ┌─┘
//  //           │                        │
//  //           └─┐  ┐  ┌───────┬──┐  ┌──┘
//  //             │ ─┤ ─┤       │ ─┤ ─┤
//  //             └──┴──┘       └──┴──┘
// withClock(io.nvdla_core_clk){

//     val spt_req_rdy = Wire(Bool())

//     val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_IG_PW))
//         pipe_p1.io.clk := io.nvdla_core_clk

//         pipe_p1.io.vi := io.arb2spt_req_pd.valid
//         io.arb2spt_req_pd.ready := pipe_p1.io.ro
//         pipe_p1.io.di := io.arb2spt_req_pd.bits

//         val spt_req_vld = pipe_p1.io.vo
//         pipe_p1.io.ri := spt_req_rdy
//         val spt_req_pd = pipe_p1.io.dout

//     val spt_out_rdy = Reg(Bool())
//     val is_cross_256byte_boundary = Wire(Bool())
//     val is_2nd_req = RegInit(false.B)
//     spt_req_rdy := spt_out_rdy & (!is_cross_256byte_boundary || (is_cross_256byte_boundary & is_2nd_req))

// // PKT_UNPACK_WIRE( cvt_read_cmd , spt_req_ , spt_req_pd )
//     val spt_req_axid = spt_req_pd(3, 0)
//     val spt_req_addr = spt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH-1+4, 4)
//     val spt_req_size = spt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+6, conf.NVDLA_MEM_ADDRESS_WIDTH+4)
//     val spt_req_swizzle = spt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+7)
//     val spt_req_odd = spt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+8)
//     val spt_req_ltran = spt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+9)
//     val spt_req_ftran = spt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+10)

//     val spt_req_offset = spt_req_addr(conf.NVDLA_MEMORY_ATOMIC_LOG2+2, conf.NVDLA_MEMORY_ATOMIC_LOG2)
//     val end_offset = spt_req_offset +& spt_req_size

//     if (log2Ceil(conf.NVDLA_PRIMARY_MEMIF_WIDTH/8) == conf.NVDLA_MEMORY_ATOMIC_LOG2) {
//         is_cross_256byte_boundary := false.B
//     }
//     else {
//         is_cross_256byte_boundary := spt_req_vld & end_offset(3)
//     }

//     val first_req_size = Mux(is_cross_256byte_boundary, (7.U - spt_req_offset), spt_req_size)
//     val first_req_addr = spt_req_addr
   
// // second_* is useful only when is_2nd_req needed

//     val second_req_addr_i = Reg(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
//     second_req_addr_i := spt_req_addr
//     second_req_addr_i((log2Ceil(conf.NVDLA_PRIMARY_MEMIF_WIDTH/8)+1), 0) := 0.U

//     val second_req_addr = second_req_addr_i
//     val second_req_size = end_offset // only usefull when 2nd req is needed

//     val req_accept = Wire(Bool())
//     when(req_accept){
//         when(is_2nd_req){
//             is_2nd_req := false.B
//         }.elsewhen(is_cross_256byte_boundary){
//             is_2nd_req := true.B
//         }
//     }

//     val spt_out_vld = Wire(Bool())

//     val spt2cvt_addr = Mux(is_2nd_req, second_req_addr, first_req_addr)
//     val spt2cvt_size = Mux(is_2nd_req, second_req_size, first_req_size)

//     req_accept := spt_out_vld & spt_out_rdy

//     spt_out_vld := spt_req_vld

// // PKT_PACK_WIRE( cvt_read_cmd , spt2cvt_ , spt_out_pd )
//     val spt_out_pd = Cat(spt_req_ftran, spt_req_ltran, spt_req_odd, spt_req_swizzle, 
//     spt2cvt_size, spt2cvt_addr, spt_req_axid)


// // A skid pipe here?
// }
// }

