// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_CORE_unit1d(implicit val conf: pdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         //pdma2pdp
//         val pdma2pdp_pvld = Input(Bool())
//         val pdma2pdp_prdy = Output(Bool())
//         val pdma2pdp_pd = Input(UInt((((conf.NVDLA_PDP_BWPE + 3)*conf.NVDLA_PDP_THROUGHPUT)+9).W))

//         //pooling
//         val pooling_out_pvld = Output(Bool())
//         val pooling_out_prdy = Input(Bool())
//         val pooling_out = Output(UInt((((conf.NVDLA_PDP_BWPE + 3)*conf.NVDLA_PDP_THROUGHPUT)+4).W))

//         //config  
//         val average_pooling_en = Input(Bool())
//         val cur_datin_disable = Input(Bool())
//         val last_out_en = Input(Bool())
//         val pdp_din_lc_f = Input(Bool())
//         val pooling_din_1st = Input(Bool())
//         val pooling_din_last = Input(Bool())
//         val pooling_type_cfg = Input(UInt(2.W))
//         val pooling_unit_en = Input(Bool())

//     })
// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 
// withClock(io.nvdla_core_clk){
// //=======================================================
// //1D pooling unit
// //-------------------------------------------------------

// // interface
//     val pdp_din_wpos = io.pdma2pdp_pd(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_PDP_BWPE+3)+3, conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_PDP_BWPE+3))
//     val pdp_din_cpos = io.pdma2pdp_pd(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_PDP_BWPE+3)+8, conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_PDP_BWPE+3)+4)
//     val buf_sel       = pdp_din_cpos
  
//     val pipe_in_rdy = Wire(Bool())
//     val pdma2pdp_prdy_f = Wire(Bool())
//     val load_din      = io.pdma2pdp_pvld & pdma2pdp_prdy_f & (~io.cur_datin_disable) & pooling_unit_en;
//     pdma2pdp_prdy_f := pipe_in_rdy
//     pdma2pdp_prdy := pdma2pdp_prdy_f

// //=========================================================
// //POOLING FUNCTION DEFINITION
// //
// def pooling_MIN(data0: UInt, data1: UInt) = 
//     Mux(data1.asSInt>data0.asSInt, data0, data1)

// def pooling_MAX(data0: UInt, data1: UInt) = 
//     Mux(data0.asSInt>data1.asSInt, data0, data1)
    
// def pooling_SUM(data0: UInt, data1: UInt) = 
//     (data0.asSInt + data1.asSInt).asUInt

// def pooling_fun(data0: UInt, data1: UInt, pooling_type: UInt) = 
//     VecInit((0 to conf.NVDLA_PDP_THROUGHPUT - 1) map 
//     { i => 
//     Mux(pooling_type===2.U, pooling_SUM(data0((conf.NVDLA_PDP_BWPE+3)*i+(conf.NVDLA_PDP_BWPE+3)-1, (conf.NVDLA_PDP_BWPE+3)*i), data1((conf.NVDLA_PDP_BWPE+3)*i+(conf.NVDLA_PDP_BWPE+3)-1, (conf.NVDLA_PDP_BWPE+3)*i)),
//     Mux(pooling_type===1.U, pooling_MIN(data0((conf.NVDLA_PDP_BWPE+3)*i+(conf.NVDLA_PDP_BWPE+3)-1, (conf.NVDLA_PDP_BWPE+3)*i), data1((conf.NVDLA_PDP_BWPE+3)*i+(conf.NVDLA_PDP_BWPE+3)-1, (conf.NVDLA_PDP_BWPE+3)*i)),
//     Mux(pooling_type===0.U, pooling_MAX(data0((conf.NVDLA_PDP_BWPE+3)*i+(conf.NVDLA_PDP_BWPE+3)-1, (conf.NVDLA_PDP_BWPE+3)*i), data1((conf.NVDLA_PDP_BWPE+3)*i+(conf.NVDLA_PDP_BWPE+3)-1, (conf.NVDLA_PDP_BWPE+3)*i)), 0.U)))}).asUInt

// //=========================================================
// // pooling real size
// //
// val pooling_size = RegInit("b0".asUInt(3.W))
// when(load_din & io.pdp_din_lc_f){
//     when(io.pooling_din_last){
//         pooling_size := 0.U
//     }
//     .otherwise{
//         pooling_size := pooling_size + 1.U
//     }
// }

// val pooling_out_size = pooling_size

// ////====================================================================
// //// pooling data 
// ////
// val datain_ext = io.pdma2pdp_pd(conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_PDP_BWPE+3)-1, 0)
// val cur_pooling_dat = MuxLookup(buf_sel, 0.U,
//                       )



 
// }}


// object NV_NVDLA_PDP_CORE_unit1dDriver extends App {
//   implicit val conf: pdpConfiguration = new pdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_unit1d())
// }