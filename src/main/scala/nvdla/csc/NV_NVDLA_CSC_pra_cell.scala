// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._


// //for winograd

// class NV_NVDLA_CSC_pra_cell(implicit val conf: cscConfiguration) extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         // cfg
//         val cfg_precision = Input(UInt(2.W))
//         val cfg_truncate_rsc_z = Input(UInt(2.W))

//         //chn data_in
//         val chn_data_in_rsc_vz = Input(Bool())
//         val chn_data_in_rsc_lz = Output(Bool())
//         val chn_data_in_rsc_z = Input(UInt(16*16.W))

//         //chn data_out  
//         val chn_data_out_rsc_lz = Output(Bool())
//         val chn_data_out_rsc_vz = Input(Bool())
//         val chn_data_out_rsc_z = Output(UInt(16*16.W))

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

//     val chn_data_out  = Wire(UInt(16*16.W))
//     val chn_in_prdy = Wire(Bool())
//     val chn_out_pvld = Wire(Bool())

//     val chn_in_pvld = io.chn_data_in_rsc_vz
//     val chn_out_prdy = io.chn_data_out_rsc_vz
//     val cfg_truncate = io.cfg_truncate_rsc_z
//     val chn_data_in = io.chn_data_in_rsc_z

//     io.chn_data_out_rsc_z := chn_data_out
//     io.chn_data_in_rsc_lz := chn_in_prdy
//     io.chn_data_out_rsc_lz := chn_out_pvld

//     val din_prdy = Wire(Bool())
//     val pipe_p1 = Module(new NV_NVDLA_BC_pipe(256))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := chn_in_pvld
//     chn_in_prdy := pipe_p1.io.ro
//     pipe_p1.io.chn_data_in := chn_data_in
//     val din_pvld = pipe_p1.io.din_pvld
//     pipe_p1.io.din_prdy := din_prdy
//     val chn_data_reg = pipe_p1.io.chn_data_reg 
    
//     val mdout = Wire(Vec(16, SInt(17.W)))
//     val mdata_out = Wire(Vec(16, SInt(17.W))) 
//     val tdout = Wire(Vec(16, SInt(18.W)))

//     mdout(0) := chn_data_reg(0) -& chn_data_reg(8)
//     mdout(1) := chn_data_reg(1) -& chn_data_reg(9)
//     mdout(2) := chn_data_reg(2) -& chn_data_reg(10)
//     mdout(3) := chn_data_reg(3) -& chn_data_reg(11)

//     mdout(4) := chn_data_reg(4) +& chn_data_reg(8)
//     mdout(5) := chn_data_reg(5) +& chn_data_reg(9)
//     mdout(6) := chn_data_reg(6) +& chn_data_reg(10)
//     mdout(7) := chn_data_reg(7) +& chn_data_reg(11)

//     mdout(8) := chn_data_reg(8) -& chn_data_reg(4)
//     mdout(9) := chn_data_reg(9) -& chn_data_reg(5)
//     mdout(10) := chn_data_reg(10) -& chn_data_reg(6)
//     mdout(11) := chn_data_reg(11) -& chn_data_reg(7)

//     mdout(12) := chn_data_reg(4) -& chn_data_reg(12)
//     mdout(13) := chn_data_reg(5) -& chn_data_reg(13)
//     mdout(14) := chn_data_reg(6) -& chn_data_reg(14)
//     mdout(15) := chn_data_reg(7) -& chn_data_reg(15)

//     tdout(0) := mdata_out(0) -& mdata_out(8)
//     tdout(1) := mdata_out(1) -& mdata_out(9)
//     tdout(2) := mdata_out(2) -& mdata_out(10)
//     tdout(3) := mdata_out(3) -& mdata_out(11)

//     tdout(4) := mdata_out(4) +& mdata_out(8)
//     tdout(5) := mdata_out(5) +& mdata_out(9)
//     tdout(6) := mdata_out(6) +& mdata_out(10)
//     tdout(7) := mdata_out(7) +& mdata_out(11)

//     tdout(8) := mdata_out(8) -& mdata_out(4)
//     tdout(9) := mdata_out(9) -& mdata_out(5)
//     tdout(10) := mdata_out(10) -& mdata_out(6)
//     tdout(11) := mdata_out(11) -& mdata_out(7)

//     tdout(12) := mdata_out(4) -& mdata_out(12)
//     tdout(13) := mdata_out(5) -& mdata_out(13)
//     tdout(14) := mdata_out(6) -& mdata_out(14)
//     tdout(15) := mdata_out(7) -& mdata_out(15)

//     //row
    
//     val mout_prdy = Wire(Bool())

//     val pipe_p2 = Module(new NV_NVDLA_CSC_PRA_CELL_pipe_p2)
//     pipe_p2.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p2.io.mdout := mdout
//     pipe_p2.io.din_pvld := din_pvld
//     pipe_p2.io.mout_prdy := mout_prdy
//     din_prdy := pipe_p2.io.din_prdy 
//     mdata_out := pipe_p2.io.mdata_out
//     val mout_pvld = pipe_p2.io.mout_pvld

//     //col
//     val tout_prdy = Wire(Bool())

//     val pipe_p3 = Module(new NV_NVDLA_CSC_PRA_CELL_pipe_p3)
//     pipe_p3.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p3.io.tdout := tdout
//     pipe_p3.io.mout_pvld := mout_pvld
//     pipe_p3.io.tout_prdy := tout_prdy
//     mout_prdy := pipe_p3.io.mout_prdy 
//     val tdata_out = pipe_p3.io.tdata_out
//     val tout_pvld = pipe_p3.io.tout_pvld

//     val int16_shiftright_su = Array.fill(16){Module(new NV_NVDLA_HLS_shiftrightsu(18, 16, 2))}
//     val int8_shiftright_su = Array.fill(16){Module(new NV_NVDLA_HLS_shiftrightsu(18, 8, 2))}

//     val tru_dout_int16 = Wire(Vec(16, SInt(16.W)))
//     val tru_dout_int8 = Wire(Vec(16, SInt(8.W)))

//     for(i <- 0 to 15){
//         int16_shiftright_su(i).io.data_in := tdata_out(i).asUInt
//         int16_shiftright_su(i).io.shift_num := cfg_truncate
//         tru_dout_int16(i) := int16_shiftright_su(i).io.data_out.asSInt
//     }
//     for(i <- 0 to 15){
//         int8_shiftright_su(i).io.data_in := tdata_out(i).asUInt
//         int8_shiftright_su(i).io.shift_num := cfg_truncate
//         tru_dout_int8(i) := int8_shiftright_su(i).io.data_out.asSInt
//     }
  
//     val final_out_prdy = Wire(Bool())
//     val final_out_pvld = Wire(Bool())
//     val pipe_p4 = Module(new NV_NVDLA_CSC_PRA_CELL_pipe_p4)
//     pipe_p4.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p4.io.tru_dout_int16 := tru_dout_int16
//     pipe_p4.io.tru_dout_int8_ext := tru_dout_int8
//     pipe_p4.io.tout_pvld := tout_pvld
//     pipe_p4.io.final_out_prdy := final_out_prdy
//     tout_prdy := pipe_p4.io.tout_prdy 
//     final_out_pvld := pipe_p4.io.final_out_pvld
//     val tru_data_out_int16 = pipe_p4.io.tru_data_out_int16
//     val tru_data_out_int8 = pipe_p4.io.tru_data_out_int8

//     val chn_dout = Mux(io.cfg_precision===1.U, tru_data_out_int16, tru_data_out_int8)

//     val pipe_p5 = Module(new NV_NVDLA_CSC_PRA_CELL_pipe_p5)
//     pipe_p5.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p5.io.chn_dout := chn_dout
//     pipe_p5.io.final_out_pvld := final_out_pvld
//     pipe_p5.io.chn_out_prdy := chn_out_prdy
//     final_out_prdy := pipe_p5.io.final_out_prdy 
//     chn_data_out := pipe_p5.io.chn_data_out
//     chn_out_pvld := pipe_p5.io.chn_out_pvld
 
// }}




// object NV_NVDLA_CSC_pra_cellDriver extends App {
//   implicit val conf: cscConfiguration = new cscConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_CSC_pra_cell())
// }

