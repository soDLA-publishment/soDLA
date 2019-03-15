// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver


// class NV_NVDLA_CDMA_dc(implicit conf: cdmaConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //clock
//         val nvdla_core_clk = Input(Clock())
//         val nvdla_core_ng_clk = Input(Clock())


//         //reg2dp
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_conv_mode = Input(Bool())
//         val reg2dp_data_reuse = Input(Bool())
//         val reg2dp_skip_data_rls = Input(Bool())
//         val reg2dp_datain_format = Input(Bool())
//         val reg2dp_datain_width = Input(UInt(13.W))
//         val reg2dp_datain_height = Input(UInt(13.W))
//         val reg2dp_datain_channel = Input(UInt(13.W))
//         val reg2dp_datain_ram_type = Input(Bool())
//         val reg2dp_datain_addr_high_0 = Input(UInt(32.W))
//         val reg2dp_datain_addr_low_0 = Input(UInt((31-conf.ATMBW).W))
//         val reg2dp_line_stride
//         val reg2dp_surf_stride
//         val reg2dp_batch_stride

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

//     val chn_in_prdy = Wire(Bool())
//     val chn_alu_prdy = Wire(Bool())
//     val chn_out_pvld = Wire(Bool())
//     val chn_sync_pvld = Wire(Bool())
//     val chn_sync_prdy = Wire(Bool())
//     val chn_data_out = Wire(SInt(16.W))

//     val chn_in_pvld  = io.chn_data_in_rsc_vz
//     val chn_alu_pvld = io.chn_alu_in_rsc_vz
//     val chn_data_in = io.chn_data_in_rsc_z
//     val chn_alu_in = io.chn_alu_in_rsc_z
//     val cfg_mul_in = io.cfg_mul_in_rsc_z
//     val chn_out_prdy = io.chn_data_out_rsc_vz

//     io.chn_data_in_rsc_lz := chn_in_prdy
//     io.chn_alu_in_rsc_lz := chn_alu_prdy
//     io.chn_data_out_rsc_lz := chn_out_pvld
//     io.chn_data_out_rsc_z := chn_data_out

//     chn_sync_pvld := chn_alu_pvld  & chn_in_pvld
//     chn_alu_prdy := chn_sync_prdy & chn_in_pvld
//     chn_in_prdy := chn_sync_prdy & chn_alu_pvld

//     val chn_data_ext = Wire(SInt(18.W))
//     chn_data_ext := chn_data_in
//     val chn_alu_ext = Wire(SInt(18.W))
//     chn_alu_ext := chn_alu_in

//     //sub
//     val sub_out_prdy = Wire(Bool())
//     val sub_dout = chn_data_ext - chn_alu_ext

//     val pipe_p1 = Module(new NV_NVDLA_CDMA_CVT_CELL_pipe_p1)
//     pipe_p1.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p1.io.chn_sync_pvld := chn_sync_pvld
//     pipe_p1.io.sub_dout := sub_dout
//     pipe_p1.io.sub_out_prdy := sub_out_prdy
//     chn_sync_prdy := pipe_p1.io.chn_sync_prdy
//     val sub_data_out = pipe_p1.io.sub_data_out
//     val sub_out_pvld = pipe_p1.io.sub_out_pvld

//     //mul 
//     val mul_out_prdy = Wire(Bool())
//     val mul_dout = sub_data_out * cfg_mul_in

//     val pipe_p2 = Module(new NV_NVDLA_CDMA_CVT_CELL_pipe_p2)
//     pipe_p2.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p2.io.mul_dout := mul_dout
//     pipe_p2.io.mul_out_prdy := mul_out_prdy
//     pipe_p2.io.sub_out_pvld := sub_out_pvld
//     val mul_data_out = pipe_p2.io.mul_data_out
//     val mul_out_pvld = pipe_p2.io.mul_out_pvld
//     sub_out_prdy := pipe_p2.io.sub_out_prdy

//     //truncate
//     val u_shiftright_su = Module(new NV_NVDLA_HLS_shiftrightsu(34, 17, 6))
//     u_shiftright_su.io.data_in := mul_data_out.asUInt
//     u_shiftright_su.io.shift_num := io.cfg_truncate
//     val tru_dout = u_shiftright_su.io.data_out
    
//     //unsigned
//     val tru_out_prdy = Wire(Bool())
//     val tru_data_out = tru_dout
//     val tru_out_pvld = mul_out_pvld
//     mul_out_prdy := tru_out_prdy

//     val u_saturate_int16 = Module(new NV_NVDLA_HLS_saturate(17, 16)) 
//     u_saturate_int16.io.data_in := tru_data_out
//     val dout_int16_sat = u_saturate_int16.io.data_out.asSInt 

//     val u_saturate_int8 = Module(new NV_NVDLA_HLS_saturate(17, 8)) 
//     u_saturate_int8.io.data_in := tru_data_out
//     val dout_int8_sat = u_saturate_int8.io.data_out.asSInt 

//     val chn_dout = Mux(io.cfg_out_precision === 1.U, dout_int16_sat, dout_int8_sat)

//     val pipe_p3 = Module(new NV_NVDLA_CDMA_CVT_CELL_pipe_p3)
//     pipe_p3.io.nvdla_core_clk := io.nvdla_core_clk
//     pipe_p3.io.chn_dout := chn_dout
//     pipe_p3.io.chn_out_prdy := chn_out_prdy
//     pipe_p3.io.tru_out_pvld := tru_out_pvld
//     chn_data_out  := pipe_p3.io.chn_data_out
//     chn_out_pvld := pipe_p3.io.chn_out_pvld
//     tru_out_prdy := pipe_p3.io.tru_out_prdy  

// }}


// class NV_NVDLA_CDMA_CVT_CELL_pipe_p1 extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         //input 
//         val sub_dout = Input(SInt(18.W))
//         val chn_sync_pvld = Input(Bool())
//         val sub_out_prdy = Input(Bool())

//         //output
//         val sub_data_out = Output(SInt(18.W))
//         val chn_sync_prdy = Output(Bool())
//         val sub_out_pvld = Output(Bool())

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
//     withClock(io.nvdla_core_clk){

//     val p1_skid_valid = RegInit(false.B)
//     val p1_skid_ready_flop = RegInit(true.B)
//     val chn_sync_prdy_out = RegInit(true.B)
//     val p1_skid_pipe_ready = Wire(Bool())
//     val p1_skid_data = RegInit(0.asSInt(16.W))

//     val p1_skid_catch = io.chn_sync_pvld && p1_skid_ready_flop && !p1_skid_pipe_ready
//     val p1_skid_ready = Mux(p1_skid_valid, p1_skid_pipe_ready, !p1_skid_catch)

//     p1_skid_valid := Mux(p1_skid_valid, !p1_skid_pipe_ready, p1_skid_catch)
//     p1_skid_ready_flop := p1_skid_ready
//     chn_sync_prdy_out  := p1_skid_ready
//     p1_skid_data := Mux(p1_skid_catch, io.sub_dout, p1_skid_data)

//     io.chn_sync_prdy := chn_sync_prdy_out

//     val p1_skid_pipe_valid = Mux(p1_skid_ready_flop, io.chn_sync_pvld, p1_skid_valid)
//     val p1_skid_pipe_data = Mux(p1_skid_ready_flop, io.sub_dout, p1_skid_data)
//     //## pipe (1) valid-ready-bubble-collapse
//     val p1_pipe_ready = Wire(Bool())
//     val p1_pipe_valid = RegInit(false.B)
//     val p1_pipe_ready_bc = p1_pipe_ready || !p1_pipe_valid
//     val p1_pipe_data = RegInit(0.asSInt(16.W))

//     p1_pipe_valid := Mux(p1_pipe_ready_bc,  p1_skid_pipe_valid, true.B)
//     p1_pipe_data := Mux(p1_pipe_ready_bc && p1_skid_pipe_valid,  p1_skid_pipe_data, p1_pipe_data)

//     p1_skid_pipe_ready := p1_pipe_ready_bc
//     //## pipe (1) output
//     io.sub_out_pvld := p1_pipe_valid
//     p1_pipe_ready := io.sub_out_prdy
//     io.sub_data_out := p1_pipe_data
    
// }}


// class NV_NVDLA_CDMA_CVT_CELL_pipe_p2 extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         //input 
//         val mul_dout = Input(SInt(34.W))
//         val sub_out_pvld = Input(Bool())
//         val mul_out_prdy = Input(Bool())

//         //output
//         val mul_data_out= Output(SInt(34.W))
//         val sub_out_prdy = Output(Bool())
//         val mul_out_pvld = Output(Bool())

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
//     withClock(io.nvdla_core_clk){

//     val p2_skid_valid = RegInit(false.B)
//     val p2_skid_ready_flop = RegInit(true.B)
//     val sub_out_prdy_out = RegInit(true.B)
//     val p2_skid_pipe_ready = Wire(Bool())
//     val p2_skid_data = RegInit(0.asSInt(16.W))

//     val p2_skid_catch = io.sub_out_pvld && p2_skid_ready_flop && !p2_skid_pipe_ready
//     val p2_skid_ready = Mux(p2_skid_valid, p2_skid_pipe_ready, !p2_skid_catch)

//     p2_skid_valid := Mux(p2_skid_valid, !p2_skid_pipe_ready, p2_skid_catch)
//     p2_skid_ready_flop := p2_skid_ready
//     sub_out_prdy_out  := p2_skid_ready
//     p2_skid_data := Mux(p2_skid_catch, io.mul_dout, p2_skid_data)

//     io.sub_out_prdy := sub_out_prdy_out

//     val p2_skid_pipe_valid = Mux(p2_skid_ready_flop, io.sub_out_pvld, p2_skid_valid)
//     val p2_skid_pipe_data = Mux(p2_skid_ready_flop, io.mul_dout, p2_skid_data)
//     //## pipe (2) valid-ready-bubble-collapse
//     val p2_pipe_ready = Wire(Bool())
//     val p2_pipe_valid = RegInit(false.B)
//     val p2_pipe_ready_bc = p2_pipe_ready || !p2_pipe_valid
//     val p2_pipe_data = RegInit(0.asSInt(16.W))

//     p2_pipe_valid := Mux(p2_pipe_ready_bc,  p2_skid_pipe_valid, true.B)
//     p2_pipe_data := Mux(p2_pipe_ready_bc && p2_skid_pipe_valid,  p2_skid_pipe_data, p2_pipe_data)

//     p2_skid_pipe_ready := p2_pipe_ready_bc
//     //## pipe (2) output
//     io.mul_out_pvld := p2_pipe_valid
//     p2_pipe_ready := io.mul_out_prdy
//     io.mul_data_out := p2_pipe_data
    
// }}


// class NV_NVDLA_CDMA_CVT_CELL_pipe_p3 extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         //input 
//         val chn_dout = Input(SInt(16.W))
//         val tru_out_pvld = Input(Bool())
//         val chn_out_prdy = Input(Bool())

//         //output
//         val chn_data_out= Output(SInt(16.W))
//         val tru_out_prdy = Output(Bool())
//         val chn_out_pvld = Output(Bool())

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
//     withClock(io.nvdla_core_clk){

//     val p3_skid_valid = RegInit(false.B)
//     val p3_skid_ready_flop = RegInit(true.B)
//     val tru_out_prdy_out = RegInit(true.B)
//     val p3_skid_pipe_ready = Wire(Bool())
//     val p3_skid_data = RegInit(0.asSInt(16.W))

//     val p3_skid_catch = io.tru_out_pvld && p3_skid_ready_flop && !p3_skid_pipe_ready
//     val p3_skid_ready = Mux(p3_skid_valid, p3_skid_pipe_ready, !p3_skid_catch)

//     p3_skid_valid := Mux(p3_skid_valid, !p3_skid_pipe_ready, p3_skid_catch)
//     p3_skid_ready_flop := p3_skid_ready
//     tru_out_prdy_out  := p3_skid_ready
//     p3_skid_data := Mux(p3_skid_catch, io.chn_dout, p3_skid_data)

//     io.tru_out_prdy := tru_out_prdy_out

//     val p3_skid_pipe_valid = Mux(p3_skid_ready_flop, io.tru_out_pvld, p3_skid_valid)
//     val p3_skid_pipe_data = Mux(p3_skid_ready_flop, io.chn_dout, p3_skid_data)
//     //## pipe (3) valid-ready-bubble-collapse
//     val p3_pipe_ready = Wire(Bool())
//     val p3_pipe_valid = RegInit(false.B)
//     val p3_pipe_ready_bc = p3_pipe_ready || !p3_pipe_valid
//     val p3_pipe_data = RegInit(0.asSInt(16.W))

//     p3_pipe_valid := Mux(p3_pipe_ready_bc,  p3_skid_pipe_valid, true.B)
//     p3_pipe_data := Mux(p3_pipe_ready_bc && p3_skid_pipe_valid,  p3_skid_pipe_data, p3_pipe_data)

//     p3_skid_pipe_ready := p3_pipe_ready_bc
//     //## pipe (2) output
//     io.chn_out_pvld:= p3_pipe_valid
//     p3_pipe_ready := io.chn_out_prdy
//     io.chn_data_out := p3_pipe_data
    
// }}

// object NV_NVDLA_CDMA_CVT_cellDriver extends App {
//   implicit val conf: cdmaConfiguration = new cdmaConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_CVT_cell())
// }
