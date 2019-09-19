// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_x1_int(implicit conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         //data_in
//         val chn_data_in = Flipped(DecoupledIO(UInt(conf.BS_IN_DW.W)))
//         //alu_op
//         val chn_alu_op = Flipped(DecoupledIO(UInt(conf.BS_OP_DW.W)))     
//         //mul_op
//         val chn_mul_op = Flipped(DecoupledIO(UInt(conf.BS_OP_DW.W)))
//         //chn_data
//         val chn_data_out = DecoupledIO(UInt(conf.BS_OUT_DW.W))
//         //cfg
//         val cfg_alu_algo = Input(UInt(2.W))
//         val cfg_alu_bypass = Input(Bool())
//         val cfg_alu_op = Input(UInt(16.W))
//         val cfg_alu_shift_value = Input(UInt(6.W))
//         val cfg_alu_src = Input(Bool())
//         val cfg_mul_bypass = Input(Bool())
//         val cfg_mul_op = Input(UInt(16.W))
//         val cfg_mul_prelu = Input(Bool())
//         val cfg_mul_shift_value = Input(UInt(6.W))
//         val cfg_mul_src = Input(Bool())
//         val cfg_relu_bypass = Input(Bool())
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

//     val alu_data_out_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, UInt(33.W)))
//     val alu_out_prdy_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val alu_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val bypass_trt_out_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val chn_alu_op_wire = VecInit((0 to conf.NVDLA_SDP_BS_THROUGHPUT-1) 
//                                map { i => io.chn_alu_op(16*i+15, 16*i)})
//     val chn_alu_op_prdy_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val chn_data_in_wire = VecInit((0 to conf.NVDLA_SDP_BS_THROUGHPUT-1) 
//                                map { i => io.chn_data_in(32*i+31, 32*i)})
//     val chn_data_out_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, UInt(32.W)))
//     val chn_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val chn_mul_op_wire = VecInit((0 to conf.NVDLA_SDP_BS_THROUGHPUT-1) 
//                                map { i => io.chn_mul_op(16*i+15, 16*i)})
//     val chn_mul_op_prdy_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val chn_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val mul_data_out_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, UInt(49.W)))
//     val mul_out_prdy_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val mul_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val trt_data_out_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, UInt(32.W)))
//     val trt_out_prdy_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     val trt_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_BS_THROUGHPUT, Bool()))
//     io.chn_data_out := chn_data_out_wire.asUInt

//     io.chn_in_prdy := chn_in_prdy_wire(0)
//     io.chn_alu_op_prdy := chn_alu_op_prdy_wire(0)
//     io.chn_mul_op_prdy := chn_mul_op_prdy_wire(0)
//     io.chn_out_pvld := chn_out_pvld_wire(0)

//     val u_sdp_x_alu = Array.fill(conf.NVDLA_SDP_BS_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_X_int_alu)}
//     val u_sdp_x_mul = Array.fill(conf.NVDLA_SDP_BS_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_X_int_mul)}
//     val u_sdp_x_trt = Array.fill(conf.NVDLA_SDP_BS_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_X_int_trt)}
//     val u_sdp_x_relu = Array.fill(conf.NVDLA_SDP_BS_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_X_int_relu)}

//     for(i <- 0 to conf.NVDLA_SDP_BS_THROUGHPUT-1){

//         //u_sdp_x_alu
//         u_sdp_x_alu(i).io.nvdla_core_clk := io.nvdla_core_clk

//         u_sdp_x_alu(i).io.alu_op_pvld := io.chn_alu_op_pvld
//         chn_alu_op_prdy_wire(i) := u_sdp_x_alu(i).io.alu_op_prdy
//         u_sdp_x_alu(i).io.chn_alu_op := chn_alu_op_wire(i)

//         u_sdp_x_alu(i).io.alu_in_pvld := io.chn_in_pvld
//         chn_in_prdy_wire(i) := u_sdp_x_alu(i).io.alu_in_prdy
//         u_sdp_x_alu(i).io.alu_data_in := chn_data_in_wire(i)

//         alu_out_pvld_wire(i) := u_sdp_x_alu(i).io.alu_out_pvld
//         u_sdp_x_alu(i).io.alu_out_prdy := alu_out_prdy_wire(i)
//         alu_data_out_wire(i) := u_sdp_x_alu(i).io.alu_data_out

//         u_sdp_x_alu(i).io.cfg_alu_algo := io.cfg_alu_algo
//         u_sdp_x_alu(i).io.cfg_alu_bypass := io.cfg_alu_bypass
//         u_sdp_x_alu(i).io.cfg_alu_op := io.cfg_alu_op
//         u_sdp_x_alu(i).io.cfg_alu_shift_value := io.cfg_alu_shift_value
//         u_sdp_x_alu(i).io.cfg_alu_src := io.cfg_alu_src

//         //u_sdp_x_mul
//         u_sdp_x_mul(i).io.nvdla_core_clk := io.nvdla_core_clk

//         u_sdp_x_mul(i).io.alu_out_pvld := alu_out_pvld_wire(i)
//         alu_out_prdy_wire(i) := u_sdp_x_mul(i).io.alu_out_prdy
//         u_sdp_x_mul(i).io.alu_data_out := alu_data_out_wire(i)

//         u_sdp_x_mul(i).io.mul_op_pvld := io.chn_mul_op_pvld
//         chn_mul_op_prdy_wire(i) := u_sdp_x_mul(i).io.mul_op_prdy
//         u_sdp_x_mul(i).io.chn_mul_op := chn_mul_op_wire(i)

//         mul_out_pvld_wire(i) := u_sdp_x_mul(i).io.mul_out_pvld
//         u_sdp_x_mul(i).io.mul_out_prdy := mul_out_prdy_wire(i)
//         mul_data_out_wire(i) := u_sdp_x_mul(i).io.mul_data_out

//         u_sdp_x_mul(i).io.cfg_mul_bypass := io.cfg_mul_bypass
//         u_sdp_x_mul(i).io.cfg_mul_op := io.cfg_mul_op
//         u_sdp_x_mul(i).io.cfg_mul_prelu := io.cfg_mul_prelu
//         u_sdp_x_mul(i).io.cfg_mul_src := io.cfg_mul_src

//         bypass_trt_out_wire(i) := u_sdp_x_mul(i).io.bypass_trt_out

//         //u_sdp_x_trt
//         u_sdp_x_trt(i).io.nvdla_core_clk := io.nvdla_core_clk

//         u_sdp_x_trt(i).io.mul_out_pvld := mul_out_pvld_wire(i)
//         mul_out_prdy_wire(i) := u_sdp_x_trt(i).io.mul_out_prdy
//         u_sdp_x_trt(i).io.mul_data_out := mul_data_out_wire(i)

//         trt_out_pvld_wire(i) := u_sdp_x_trt(i).io.trt_out_pvld
//         u_sdp_x_trt(i).io.trt_out_prdy := trt_out_prdy_wire(i)
//         trt_data_out_wire(i) := u_sdp_x_trt(i).io.trt_data_out

//         u_sdp_x_trt(i).io.cfg_mul_shift_value := io.cfg_mul_shift_value
//         u_sdp_x_trt(i).io.bypass_trt_in := bypass_trt_out_wire(i)

//         //u_sdp_x_relu
//         u_sdp_x_relu(i).io.nvdla_core_clk := io.nvdla_core_clk

//         u_sdp_x_relu(i).io.trt_out_pvld := trt_out_pvld_wire(i)
//         trt_out_prdy_wire(i) := u_sdp_x_relu(i).io.trt_out_prdy
//         u_sdp_x_relu(i).io.trt_data_out := trt_data_out_wire(i) 

//         chn_out_pvld_wire(i) := u_sdp_x_relu(i).io.relu_out_pvld
//         u_sdp_x_relu(i).io.relu_out_prdy := io.chn_out_prdy
//         chn_data_out_wire(i) := u_sdp_x_relu(i).io.relu_data_out

//         u_sdp_x_relu(i).io.cfg_relu_bypass := io.cfg_relu_bypass

//     }
// }}

// object NV_NVDLA_SDP_HLS_x1_intDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_x1_int)
// }
