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
//     val lut_uflow_in = idx_data_in.asSInt <= io.cfg_lut_start.asSInt
//     val lut_index_sub_tmp = (idx_data_in.asSInt - cfg_lut_start.asSInt).asUInt

//     val lut_index_sub = Mux(lut_uflow_in, 0.U, lut_index_sub_tmp)

//     val pipe_p1 = Module(new new NV_NVDLA_BC_pipe(33))

 
    

// }}