// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_X_int_alu extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val chn_alu_op = Flipped(DecoupledIO(UInt(16.W)))
//         val alu_data_in = Flipped(DecoupledIO(UInt(32.W)))
//         val alu_data_out = DecoupledIO(UInt(33.W))

//         val cfg_alu_algo = Input(UInt(2.W))
//         val cfg_alu_bypass = Input(Bool())
//         val cfg_alu_op = Input(UInt(16.W))
//         val cfg_alu_shift_value = Input(UInt(6.W))
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
//     val x_alu_sync2data = Module{new NV_NVDLA_SDP_HLS_sync2data(16, 32)}
//     x_alu_sync2data.io.chn1_en := io.cfg_alu_src & !io.cfg_alu_bypass
//     x_alu_sync2data.io.chn2_en := !io.cfg_alu_bypass
//     x_alu_sync2data.io.chn1_in_pvld := io.alu_op_pvld
//     io.alu_op_prdy := x_alu_sync2data.io.chn1_in_prdy
//     x_alu_sync2data.io.chn2_in_pvld := io.alu_in_pvld
//     val alu_in_srdy = x_alu_sync2data.io.chn2_in_prdy
//     val alu_sync_pvld = x_alu_sync2data.io.chn_out_pvld    
//     x_alu_sync2data.io.chn_out_prdy := alu_sync_prdy        
//     x_alu_sync2data.io.data1_in := io.chn_alu_op
//     x_alu_sync2data.io.data2_in := io.alu_data_in
//     val alu_op_sync = x_alu_sync2data.io.data1_out
//     val alu_data_sync = x_alu_sync2data.io.data2_out

//     val alu_op_in = Mux(io.cfg_alu_src, alu_op_sync, io.cfg_alu_op)

//     val x_alu_shiftleft_su = Module{new NV_NVDLA_HLS_shiftleftsu(16, 32, 6)}
//     x_alu_shiftleft_su.io.data_in := alu_op_in
//     x_alu_shiftleft_su.io.shift_num := io.cfg_alu_shift_value
//     val alu_op_shift = x_alu_shiftleft_su.io.data_out

//     val alu_shift_prdy = Wire(Bool())
//     val pipe_p1_data_in = Cat(alu_op_shift, alu_data_sync)
//     val pipe_p1 = Module{new NV_NVDLA_BC_pipe(64)}
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := alu_sync_pvld
//     alu_sync_prdy := pipe_p1.io.ro
//     pipe_p1.io.di := pipe_p1_data_in
//     val alu_shift_pvld = pipe_p1.io.vo
//     pipe_p1.io.ri := alu_shift_prdy
//     val pipe_p1_data_out = pipe_p1.io.dout

//     val alu_data_reg = pipe_p1_data_out(31, 0)
//     val operand_shift = pipe_p1_data_out(63, 32)

//     val operand_ext = Cat(operand_shift(31), operand_shift)
//     val alu_data_ext = Cat(alu_data_reg(31), alu_data_reg)
//     val alu_sum = (alu_data_ext.asSInt + operand_ext.asSInt).asUInt

//     val alu_dout = Wire(UInt(33.W))
//     when(io.cfg_alu_algo === 0.U){
//         alu_dout := Mux(alu_data_ext.asSInt > operand_ext.asSInt, alu_data_ext, operand_ext)
//     }
//     .elsewhen(io.cfg_alu_algo === 1.U){
//         alu_dout := Mux(alu_data_ext.asSInt < operand_ext.asSInt, alu_data_ext, operand_ext)
//     }
//     .otherwise{
//         alu_dout := alu_sum
//     }

//     val alu_final_prdy = Wire(Bool())
//     val pipe_p2 = Module{new NV_NVDLA_BC_pipe(32)}
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := alu_shift_pvld
//     alu_shift_prdy := pipe_p2.io.ro
//     pipe_p2.io.di := alu_dout
//     val alu_final_pvld = pipe_p2.io.vo
//     pipe_p2.io.ri := alu_final_prdy
//     val alu_data_final = pipe_p2.io.dout

//     io.alu_in_prdy := Mux(io.cfg_alu_bypass, io.alu_out_prdy, alu_in_srdy)
//     alu_final_prdy := Mux(io.cfg_alu_bypass, true.B, io.alu_out_prdy)
//     io.alu_out_pvld := Mux(io.cfg_alu_bypass, io.alu_in_pvld, alu_final_pvld)
//     io.alu_data_out := Mux(io.cfg_alu_bypass, Cat(io.alu_data_in(31), io.alu_data_in), alu_data_final)


// }} 

// object NV_NVDLA_SDP_HLS_X_int_aluDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_X_int_alu)
// }
