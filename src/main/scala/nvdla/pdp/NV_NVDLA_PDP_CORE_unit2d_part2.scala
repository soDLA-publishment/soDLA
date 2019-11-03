// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_CORE_CAL2D_add_pad_v(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         val pout_mem_data = Input(Vec(conf.NVDLA_PDP_THROUGHPUT, UInt((conf.NVDLA_PDP_BWPE+6).W)))
//         val pout_mem_size_v = Input(UInt(3.W))
//         val pooling_size_v_cfg = Input(UInt(3.W))
//         val average_pooling_en = Input(Bool())
//         val reg2dp_kernel_width = Input(UInt(3.W))
//         val rd_pout_data_stage0 = Input(Bool())
//         val reg2dp_recip_width_cfg = Input(UInt(17.W))
//         val reg2dp_recip_height_cfg = Input(UInt(17.W))
//         val reg2dp_pad_value_1x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_2x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_3x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_4x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_5x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_6x_cfg = Input(UInt(19.W))
//         val reg2dp_pad_value_7x_cfg = Input(UInt(19.W))

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
//     //===========================================================
//     //adding pad value in v direction
//     //-----------------------------------------------------------
//     //padding value 1x,2x,3x,4x,5x,6x,7x table
//     val pout_mem_size_v_use =  io.pout_mem_size_v
//     val padding_here = io.average_pooling_en & (pout_mem_size_v_use =/= io.pooling_size_v_cfg)
//     val pad_table_index = io.pooling_size_v_cfg - pout_mem_size_v_use

//     val pad_table_out = MuxLookup(pad_table_index, "b0".asUInt(19.W),
//                         Array(
//                             1.U -> io.reg2dp_pad_value_1x_cfg,
//                             2.U -> io.reg2dp_pad_value_2x_cfg,
//                             3.U -> io.reg2dp_pad_value_3x_cfg,
//                             4.U -> io.reg2dp_pad_value_4x_cfg,
//                             5.U -> io.reg2dp_pad_value_5x_cfg,
//                             6.U -> io.reg2dp_pad_value_6x_cfg,
//                             7.U -> io.reg2dp_pad_value_7x_cfg,
//                         ))
    
//     val kernel_width_cfg = io.reg2dp_kernel_width +& 1.U

//     val pad_value = (pad_table_out.asSInt * Cat(false.B, kernel_width_cfg).asSInt).asUInt

//     val u_pad = Module(new NV_NVDLA_VEC_padder(vector_len = conf.NVDLA_PDP_THROUGHPUT, data_width = conf.NVDLA_BPE+7))
//     u_pad.io.vec_in(i) := io.pout_mem_data(i)  
//     u_pad.io.pad_value := pad_value
//     u_pad.io.padding := padding_here
//     val data_8bit := u_pad.io.vec_out(i)

//     val pout_data_0 = RegInit(VecInit(Seq.fill(conf.NVDLA_PDP_THROUGHPUT)("b0".asUInt((conf.NVDLA_PDP_BWPE+6).W)))
//     when(io.average_pooling_en){
//         when(io.rd_pout_data_stage0){
//             for(i <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
//                 pout_data_0(i) := data_8bit(i)
//             }
//         }
//     }
//     .elsewhen(io.rd_pout_data_stage0){
//         pout_data_0(i) := Cat(io.pout_mem_data(i)(conf.NVDLA_PDP_BWPE+5), io.pout_mem_data(i))
//     }

//     //===========================================================
//     //stage1: (* /kernel_width)
//     //stage1 : calcate pooling data based on real pooling size --- (* 1/kernel_width)
//     //-----------------------------------------------------------
//     val reg2dp_recip_width_use = RegInit("b0".asUInt(17.W))
//     reg2dp_recip_width_use := io.reg2dp_recip_width_cfg

//     val reg2dp_recip_height_use = RegInit("b0".asUInt(17.W))
//     reg2dp_recip_height_use := io.reg2dp_recip_height_cfg

//     val data_hmult_8bit_ext = 



 
// }}