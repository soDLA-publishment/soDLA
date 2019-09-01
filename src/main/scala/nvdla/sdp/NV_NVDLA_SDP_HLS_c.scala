// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_c(implicit conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val cvt_in_pvld = Input(Bool())
//         val cvt_in_prdy = Output(Bool())
//         val cvt_pd_in = Input(UInt(conf.CV_IN_DW.W))

//         val cvt_out_pvld = Output(Bool())
//         val cvt_out_prdy = Input(Bool())
//         val cvt_pd_out = Output(UInt((conf.CV_OUT_DW+conf.NVDLA_SDP_MAX_THROUGHPUT).W))

//         val cfg_mode_eql = Input(Bool())
//         val cfg_offset = Input(UInt(32.W))
//         val cfg_out_precision = Input(UInt(2.W))
//         val cfg_scale = Input(UInt(16.W))
//         val cfg_truncate = Input(UInt(6.W))

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

//     val cvt_data_in_wire = VecInit((0 to conf.NVDLA_SDP_MAX_THROUGHPUT-1) 
//                             map { i => io.cvt_pd_in(32*i+31, 32*i)})
//     val cvt_data_out_wire = Wire(Vec(conf.NVDLA_SDP_MAX_THROUGHPUT, UInt(16.W)))
//     val cvt_pd_out8 = VecInit((0 to conf.NVDLA_SDP_MAX_THROUGHPUT-1) 
//                             map { i => cvt_data_out_wire(i)(7, 0)}).asUInt
//     val cvt_pd_out16 = cvt_data_out_wire.asUInt
//     val cvt_pd_out_0 = Wire(UInt(conf.CV_OUT_DW.W))
//     cvt_pd_out_0 := Mux(io.cfg_out_precision === 0.U, cvt_pd_out8, cvt_pd_out16)
//     val cvt_sat_out_wire = Wire(Vec(conf.NVDLA_SDP_MAX_THROUGHPUT, Bool()))
//     val cvt_pd_out_1 = cvt_sat_out_wire.asUInt
//     io.cvt_pd_out := Cat(cvt_pd_out_1, cvt_pd_out_0) 

//     val cvt_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_MAX_THROUGHPUT, Bool()))
//     val cvt_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_MAX_THROUGHPUT, Bool()))
//     val c_int = Array.fill(conf.NVDLA_SDP_MAX_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_C_int)}
//     for(i <- 0 to conf.NVDLA_SDP_MAX_THROUGHPUT-1){

//         c_int(i).io.nvdla_core_clk := io.nvdla_core_clk

//         c_int(i).io.cvt_in_pvld := io.cvt_in_pvld
//         cvt_in_prdy_wire(i) := c_int(i).io.cvt_in_prdy
//         c_int(i).io.cvt_data_in := cvt_data_in_wire(i)

//         cvt_out_pvld_wire(i) := c_int(i).io.cvt_out_pvld
//         c_int(i).io.cvt_out_prdy := io.cvt_out_prdy
//         cvt_data_out_wire(i) := c_int(i).io.cvt_data_out
//         cvt_sat_out_wire(i) := c_int(i).io.cvt_sat_out

//         c_int(i).io.cfg_mode_eql := io.cfg_mode_eql
//         c_int(i).io.cfg_offset := io.cfg_offset
//         c_int(i).io.cfg_out_precision := io.cfg_out_precision
//         c_int(i).io.cfg_scale := io.cfg_scale
//         c_int(i).io.cfg_truncate := io.cfg_truncate
   
//     }

//     io.cvt_in_prdy := cvt_in_prdy_wire(0)
//     io.cvt_out_pvld := cvt_out_pvld_wire(0)
// }}





// object NV_NVDLA_SDP_HLS_cDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_c)
// }