// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_HLS_Y_inp_top(implicit conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val chn_inp_in_pvld = Input(Bool())
//         val chn_inp_in_prdy = Output(Bool())
//         val chn_inp_in_pd = Input(UInt(conf.EW_LUT_OUT_DW.W))

//         val chn_inp_out_pvld = Output(Bool())
//         val chn_inp_out_prdy = Input(Bool())
//         val chn_inp_out_pd = Output(UInt(conf.EW_INP_OUT_DW.W))
        
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
//     val k = conf.NVDLA_SDP_EW_THROUGHPUT
//     val bf = conf.NVDLA_SDP_EW_THROUGHPUT*32
//     val by0 = conf.NVDLA_SDP_EW_THROUGHPUT*(32+35)
//     val by1 = conf.NVDLA_SDP_EW_THROUGHPUT*(32+35+16)
//     val bsc = conf.NVDLA_SDP_EW_THROUGHPUT*(32+35+32)
//     val bsf = conf.NVDLA_SDP_EW_THROUGHPUT*(32+35+48)
//     val bof = conf.NVDLA_SDP_EW_THROUGHPUT*(32+35+48+5)
//     val bbs = conf.NVDLA_SDP_EW_THROUGHPUT*(32+35+85)
//     val bfw = conf.NVDLA_SDP_EW_THROUGHPUT*(32+35+85+32)

//     val chn_inp_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val chn_inp_out_data_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
//     val chn_inp_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
//     val inp_in_bias_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                             map { i => io.chn_inp_in_pd(32*i+31+bbs, 32*i+bbs)})
//     val inp_in_flow_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                             map { i => io.chn_inp_in_pd(i+bfw)})
//     val inp_in_fraction_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                                map { i => io.chn_inp_in_pd(35*i+34+bf, 35*i+bf)})
//     val inp_in_offset_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                             map { i => io.chn_inp_in_pd(32*i+31+bof, 32*i+bof)})
//     val inp_in_scale_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                             map { i => io.chn_inp_in_pd(16*i+15+bsc, 16*i+bsc)})
//     val inp_in_shift_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                             map { i => io.chn_inp_in_pd(5*i+4+bsf, 5*i+bsf)})
//     val inp_in_x_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                          map { i => io.chn_inp_in_pd(32*i+31, 32*i)})
//     val inp_in_y0_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                          map { i => io.chn_inp_in_pd(16*i+15+by0, 16*i+by0)})
//     val inp_in_y1_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
//                          map { i => io.chn_inp_in_pd(16*i+15+by1, 16*i+by1)})

//     io.chn_inp_out_pd := chn_inp_out_data_wire.asUInt

//     val y_int_inp = Array.fill(conf.NVDLA_SDP_EW_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_Y_int_inp)}

//     for(i <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){

//         y_int_inp(i).io.nvdla_core_clk := io.nvdla_core_clk
//         y_int_inp(i).io.inp_bias_in := inp_in_bias_wire(i)
//         y_int_inp(i).io.inp_flow_in := inp_in_flow_wire(i)
//         y_int_inp(i).io.inp_frac_in := inp_in_fraction_wire(i)
//         y_int_inp(i).io.inp_in_pvld := io.chn_inp_in_pvld
//         y_int_inp(i).io.inp_offset_in := inp_in_offset_wire(i)
//         y_int_inp(i).io.inp_out_prdy := io.chn_inp_out_prdy
//         y_int_inp(i).io.inp_scale_in := inp_in_scale_wire(i)
//         y_int_inp(i).io.inp_shift_in := inp_in_shift_wire(i)
//         y_int_inp(i).io.inp_x_in := inp_in_x_wire(i)
//         y_int_inp(i).io.inp_y0_in := inp_in_y0_wire(i)
//         y_int_inp(i).io.inp_y1_in := inp_in_y1_wire(i)
//         chn_inp_out_data_wire(i) := y_int_inp(i).io.inp_data_out
//         chn_inp_in_prdy_wire(i) := y_int_inp(i).io.inp_in_prdy
//         chn_inp_out_pvld_wire(i) := y_int_inp(i).io.inp_out_pvld

//     }
    
//     io.chn_inp_in_prdy := chn_inp_in_prdy_wire(0)
//     io.chn_inp_out_pvld := chn_inp_out_pvld_wire(0)


// }}


// object NV_NVDLA_SDP_HLS_Y_inp_topDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_inp_top)
// }