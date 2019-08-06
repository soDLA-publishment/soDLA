// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_int_alu extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val alu_in_pvld = Input(Bool())
//         val alu_in_prdy = Output(Bool())
//         val alu_data_in = Input(UInt(32.W))

//         val chn_alu_op_pvld = Input(Bool())
//         val chn_alu_op_prdy = Output(Bool())
//         val chn_alu_op = Input(UInt(32.W))

//         val alu_out_pvld = Output(Bool())
//         val alu_out_prdy = Input(Bool())
//         val alu_data_out = Output(UInt(32.W))

//         val cfg_alu_algo = Input(UInt(2.W))
//         val cfg_alu_bypass = Input(Bool())
//         val cfg_alu_op = Input(UInt(32.W))
//         val cfg_alu_src = Input(Bool())
        
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

//     val alu_sync_prdy = Wire(Bool())
//     val y_alu_sync2data = Module{new NV_NVDLA_SDP_HLS_sync2data(32, 32)}
//     y_alu_sync2data.io.chn1_en := io.cfg_alu_src & !io.cfg_alu_bypass
//     y_alu_sync2data.io.chn2_en := !io.cfg_alu_bypass
//     y_alu_sync2data.io.chn1_in_pvld := io.chn_alu_op_pvld
//     io.chn_alu_op_prdy := y_alu_sync2data.io.chn1_in_prdy
//     y_alu_sync2data.io.chn2_in_pvld := io.alu_in_pvld
//     val alu_in_srdy = y_alu_sync2data.io.chn2_in_prdy
//     val alu_sync_pvld = y_alu_sync2data.io.chn_out_pvld
//     y_alu_sync2data.io.chn_out_prdy := alu_sync_prdy
//     y_alu_sync2data.io.data1_in := io.chn_alu_op
//     y_alu_sync2data.io.data2_in := io.alu_data_in
//     val alu_op_sync = y_alu_sync2data.io.data1_out 
//     val alu_data_sync = y_alu_sync2data.io.data2_out

//     val alu_op_mux = Mux(io.cfg_alu_src, alu_op_sync, io.cfg_alu_op)

//     //pack {alu_data_sync[31:0],alu_op_mux[31:0]}
//     val alu_data_in_p1 = Cat(alu_data_sync, alu_op_mux)
//     val alu_mux_prdy = Wire(Bool())
//     val pipe_p1 = Module{new NV_NVDLA_BC_pipe(64)}
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := alu_sync_pvld
//     alu_sync_prdy := pipe_p1.io.ro
//     pipe_p1.io.di := alu_data_in_p1
//     val alu_mux_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := alu_mux_prdy
//     val alu_data_out_p1 = pipe_p1.io.dout

//     val alu_data_reg = alu_data_out_p1(63, 32)
//     val alu_op_reg = alu_data_out_p1(31, 0)

//     val alu_op_ext = Cat(alu_op_reg(31), alu_op_reg)
//     val alu_data_ext = Cat(alu_data_reg(31), alu_data_reg)
//     val alu_sum = (alu_op_ext.asSInt + alu_op_ext.asSInt).asUInt

//     val alu_dout = Wire(UInt(33.W))
//     when(io.cfg_alu_algo === 0.U){
//         alu_dout := Mux(alu_data_ext.asSInt > alu_op_ext.asSInt, alu_data_ext, alu_op_ext)
//     }
//     .elsewhen(io.cfg_alu_algo === 1.U){
//         alu_dout := Mux(alu_data_ext.asSInt < alu_op_ext.asSInt, alu_data_ext, alu_op_ext)
//     }
//     .elsewhen(io.cfg_alu_algo === 3.U){
//         alu_dout := Mux(alu_data_ext === alu_op_ext, 0.U, 1.U)
//     }
//     .otherwise{
//         alu_dout := alu_sum
//     }

//     val y_alu_saturate = Module{new NV_NVDLA_HLS_saturate(32+1, 32)}
//     y_alu_saturate.io.data_in := alu_dout
//     val alu_sat = y_alu_saturate.io.data_out

//     val alu_final_prdy = Wire(Bool())
//     val pipe_p2 = Module{new NV_NVDLA_BC_pipe(32)}
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := alu_mux_pvld
//     alu_mux_prdy := pipe_p2.io.ro
//     pipe_p2.io.di := alu_sat
//     val alu_final_pvld = pipe_p2.io.vo
//     pipe_p2.io.ri := alu_final_prdy
//     val alu_data_final = pipe_p2.io.dout

//     io.alu_in_prdy := Mux(io.cfg_alu_bypass, io.alu_out_prdy, alu_in_srdy)
//     alu_final_prdy := Mux(io.cfg_alu_bypass, true.B, io.alu_out_prdy)
//     io.alu_out_pvld := Mux(io.cfg_alu_bypass, io.alu_in_pvld, alu_final_pvld)
//     io.alu_data_out := Mux(io.cfg_alu_bypass, io.alu_data_in, alu_data_final)


// }}


// object NV_NVDLA_SDP_HLS_Y_int_aluDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_alu)
// }