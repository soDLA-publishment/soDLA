// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_int_core(implicit conf: nvdlaConfig) extends Module{
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val chn_alu_op_pvld = Input(Bool())
//         val chn_alu_op_prdy = Output(Bool())
//         val chn_alu_op = Input(UInt(conf.EW_OC_DW.W))

//         val chn_in_pvld = Input(Bool())
//         val chn_in_prdy = Output(Bool())
//         val chn_data_in = Input(UInt(conf.EW_IN_DW.W))

//         val chn_mul_op_pvld = Input(Bool())
//         val chn_mul_op_prdy = Output(Bool())
//         val chn_mul_op = Input(UInt(conf.EW_OC_DW.W))

//         val chn_out_pvld = Output(Bool())
//         val chn_out_prdy = Input(Bool())
//         val chn_data_out = Output(UInt(conf.EW_CORE_OUT_DW.W))

//         val cfg_alu_algo = Input(UInt(2.W))
//         val cfg_alu_bypass = Input(Bool())
//         val cfg_alu_op = Input(UInt(32.W))
//         val cfg_alu_src = Input(Bool())
//         val cfg_mul_bypass = Input(Bool())
//         val cfg_mul_op = Input(UInt(32.W))
//         val cfg_mul_prelu = Input(Bool())
//         val cfg_mul_src = Input(Bool())
//         val cfg_mul_truncate = Input(UInt(10.W))   
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

//     val chn_alu_op_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
//     val chn_alu_op_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val chn_data_in_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
//     val chn_data_out_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
//     val chn_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val chn_mul_op_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
//     val chn_mul_op_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val chn_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val mul_data_out_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
//     val mul_out_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val mul_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))

//     io.chn_in_prdy := chn_in_prdy_wire(0)
//     io.chn_alu_op_prdy := chn_alu_op_prdy_wire(0)
//     io.chn_mul_op_prdy := chn_mul_op_prdy_wire(0)
//     io.chn_out_pvld := chn_out_pvld_wire(0)
    
//     for(i <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){
//         chn_data_in_wire(i) := io.chn_data_in(32*i+31, 32*i)
//         chn_alu_op_wire(i) := io.chn_alu_op(32*i+31, 32*i)
//         chn_mul_op_wire(i) := io.chn_mul_op(32*i+31, 32*i)
//     }
//     io.chn_data_out := VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map {
//                         i => chn_data_out_wire(i)}).asUInt
    
//     val u_sdp_y_core_mul = Array.fill(conf.NVDLA_SDP_EW_THROUGHPUT){Module{new NV_NVDLA_SDP_HLS_Y_int_mul}}
//     val u_sdp_y_core_alu = Array.fill(conf.NVDLA_SDP_EW_THROUGHPUT){Module{new NV_NVDLA_SDP_HLS_Y_int_alu}}

//     for(i<- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){

//     //: NV_NVDLA_SDP_HLS_Y_int_mul
//         u_sdp_y_core_mul(i).io.nvdla_core_clk := io.nvdla_core_clk

//         u_sdp_y_core_mul(i).io.chn_in_pvld := io.chn_in_pvld
//         chn_in_prdy_wire(i) := u_sdp_y_core_mul(i).io.chn_in_prdy
//         u_sdp_y_core_mul(i).io.chn_mul_in := chn_data_in_wire(i)

//         u_sdp_y_core_mul(i).io.chn_mul_op_pvld := io.chn_mul_op_pvld
//         chn_mul_op_prdy_wire(i) := u_sdp_y_core_mul(i).io.chn_mul_op_prdy
//         u_sdp_y_core_mul(i).io.chn_mul_op := chn_mul_op_wire(i)

//         mul_out_pvld_wire(i) := u_sdp_y_core_mul(i).io.mul_out_pvld
//         u_sdp_y_core_mul(i).io.mul_out_prdy := mul_out_prdy_wire(i)
//         mul_data_out_wire(i) := u_sdp_y_core_mul(i).io.mul_data_out

//         u_sdp_y_core_mul(i).io.cfg_mul_bypass := io.cfg_mul_bypass
//         u_sdp_y_core_mul(i).io.cfg_mul_op := io.cfg_mul_op
//         u_sdp_y_core_mul(i).io.cfg_mul_prelu := io.cfg_mul_prelu
//         u_sdp_y_core_mul(i).io.cfg_mul_src := io.cfg_mul_src
//         u_sdp_y_core_mul(i).io.cfg_mul_truncate := io.cfg_mul_truncate

//     //: NV_NVDLA_SDP_HLS_Y_int_alu
//         u_sdp_y_core_alu(i).io.nvdla_core_clk := io.nvdla_core_clk

//         u_sdp_y_core_alu(i).io.alu_in_pvld := mul_out_pvld_wire(i)
//         mul_out_prdy_wire(i) := u_sdp_y_core_alu(i).io.alu_in_prdy
//         u_sdp_y_core_alu(i).io.alu_data_in := mul_data_out_wire(i)

//         u_sdp_y_core_alu(i).io.chn_alu_op_pvld := io.chn_alu_op_pvld
//         chn_alu_op_prdy_wire(i) := u_sdp_y_core_alu(i).io.chn_alu_op_prdy
//         u_sdp_y_core_alu(i).io.chn_alu_op := chn_alu_op_wire(i)

//         chn_out_pvld_wire(i) := u_sdp_y_core_alu(i).io.alu_out_pvld
//         u_sdp_y_core_alu(i).io.alu_out_prdy := io.chn_out_prdy
//         chn_data_out_wire(i) := u_sdp_y_core_alu(i).io.alu_data_out

//         u_sdp_y_core_alu(i).io.cfg_alu_algo := io.cfg_alu_algo
//         u_sdp_y_core_alu(i).io.cfg_alu_bypass := io.cfg_alu_bypass
//         u_sdp_y_core_alu(i).io.cfg_alu_op := io.cfg_alu_op
//         u_sdp_y_core_alu(i).io.cfg_alu_src := io.cfg_alu_src

        

//     }



// }}


// object NV_NVDLA_SDP_HLS_Y_int_coreDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_core)
// }