// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_lut_expn(LUT_DEPTH:Int = 256) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
        
//         val idx_in_pvld = Input(Bool())
//         val idx_in_prdy = Output(Bool())
//         val idx_data_in = Input(UInt(32.W))

//         val idx_out_pvld = Output(Bool())
//         val idx_out_prdy = Input(Bool())
//         val lut_frac_out = Output(UInt(35.W))
//         val lut_index_out = Output(UInt(9.W))
//         val lut_oflow_out = Output(Bool())
//         val lut_uflow_out = Output(Bool())

//         val cfg_lut_offset = Input(UInt(8.W))
//         val cfg_lut_start = Input(UInt(32.W))
//     })
//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │a
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
//     val lut_uflow_in = io.idx_data_in.asSInt <= io.cfg_lut_start.asSInt
//     val lut_index_sub_tmp = (io.idx_data_in.asSInt - io.cfg_lut_start.asSInt).asUInt

//     val lut_index_sub = Mux(lut_uflow_in, 0.U, lut_index_sub_tmp)

//     val pipe_p1_data_in = Cat(lut_uflow_in, lut_index_sub)
//     val sub_prdy = Wire(Bool())
//     val pipe_p1 = Module(new NV_NVDLA_BC_pipe(33))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := io.idx_in_pvld
//     io.idx_in_prdy := pipe_p1.io.ro
//     pipe_p1.io.di := pipe_p1_data_in
//     val sub_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := sub_prdy
//     val pipe_p1_data_out = pipe_p1.io.dout

//     val lut_uflow_reg = pipe_p1_data_out(32)
//     val lut_index_sub_reg = pipe_p1_data_out(31, 0)

//     //log2 function

//     val log2_dw_lsd = Module(new NV_DW_lsd(33))
//     log2_dw_lsd.io.a := Cat(false.B, lut_index_sub_reg)
//     val leadzero = log2_dw_lsd.io.enc

//     val log2_lut_index = Mux(lut_uflow_reg|(!(lut_index_sub_reg.orR)), 0.U, 31.U - leadzero(4, 0)) //morework
//     val filter_frac = (1.U << log2_lut_index) - 1.U
//     val log2_lut_frac = lut_index_sub_reg & filter_frac


//     //log2 end
    
//     val log2_lut_index_tru = Wire(UInt(9.W))
//     log2_lut_index_tru := log2_lut_index //always positive
//     val pipe_p2_data_in = Cat(lut_uflow_reg, log2_lut_index_tru, log2_lut_frac)
//     val log2_prdy = Wire(Bool())
//     val pipe_p2 = Module(new NV_NVDLA_BC_pipe(42))
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := sub_pvld
//     sub_prdy := pipe_p2.io.ro
//     pipe_p2.io.di := pipe_p2_data_in
//     val log2_pvld = pipe_p2.io.vo
//     pipe_p2.io.ri := log2_prdy
//     val pipe_p2_data_out = pipe_p2.io.dout

//     val log2_lut_frac_reg = pipe_p2_data_out(31, 0)
//     val log2_lut_index_reg = pipe_p2_data_out(40, 32)
//     val lut_uflow_reg2 = pipe_p2_data_out(41)

//     val cfg_lut_offset_ext = Cat(io.cfg_lut_offset(7), io.cfg_lut_offset)

//     val lut_uflow_mid = ((Cat(false.B, log2_lut_index_reg).asSInt) < cfg_lut_offset_ext.asSInt) ; //morework

//     //10bit signed to 9bit unsigned,need saturation
//     val lut_index_sub_mid_tmp = (Cat(false.B, log2_lut_index_reg).asSInt - cfg_lut_offset_ext.asSInt).asUInt

//     val lut_index_sub_mid = Mux(lut_uflow_reg2 | lut_uflow_mid, 0.U, 
//                             Mux(lut_index_sub_mid_tmp(9), "h1ff".asUInt(9.W), 
//                             lut_index_sub_mid_tmp(8, 0)))

//     val lut_oflow_final = lut_index_sub_mid >= (LUT_DEPTH -1).U
//     val lut_uflow_final =  lut_uflow_reg2 | lut_uflow_mid;

//     //index integar
//     val lut_index_final = Wire(UInt(9.W))
//     when(lut_oflow_final){
//         lut_index_final := (LUT_DEPTH - 1).U
//     } 
//     .otherwise{
//         lut_index_final := lut_index_sub_mid
//     }

//     //index fraction
//     val lut_frac_final = Cat("b0".asUInt(3.W), log2_lut_frac_reg<<(35.U- log2_lut_index_reg))
//     val pipe_p3_data_in = Cat(lut_uflow_final, lut_oflow_final, lut_index_final, lut_index_final)
//     val pipe_p3 = Module(new NV_NVDLA_BC_pipe(46))
//     pipe_p3.io.clk := io.nvdla_core_clk
//     pipe_p3.io.vi := log2_pvld
//     log2_prdy := pipe_p3.io.ro
//     pipe_p3.io.di := pipe_p3_data_in
//     io.idx_out_pvld := pipe_p3.io.vo
//     pipe_p3.io.ri := io.idx_out_prdy
//     val pipe_p3_data_out = pipe_p3.io.dout

//     io.lut_frac_out := pipe_p3_data_out(34, 0)
//     io.lut_index_out := pipe_p3_data_out(43, 35)
//     io.lut_oflow_out := pipe_p3_data_out(44)
//     io.lut_uflow_out := pipe_p3_data_out(45)

// }}



// object NV_NVDLA_SDP_HLS_lut_expnDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_lut_expn())
// }