package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_SDP_HLS_c(implicit conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val cvt_pd_in = Flipped(DecoupledIO(UInt(conf.CV_IN_DW.W)))
        val cvt_pd_out = DecoupledIO(UInt((conf.CV_OUT_DW+conf.NVDLA_SDP_MAX_THROUGHPUT).W))

        val cfg_cvt = Flipped(new sdp_c_int_cfg_cvt_if)
        val cfg_mode_eql = Input(Bool())
        val cfg_out_precision = Input(UInt(2.W))
    })
    //     
    //          ┌─┐       ┌─┐
    //       ┌──┘ ┴───────┘ ┴──┐
    //       │                 │
    //       │       ───       │          
    //       │  ─┬┘       └┬─  │
    //       │                 │
    //       │       ─┴─       │
    //       │                 │
    //       └───┐         ┌───┘
    //           │         │
    //           │         │
    //           │         │
    //           │         └──────────────┐
    //           │                        │
    //           │                        ├─┐
    //           │                        ┌─┘    
    //           │                        │
    //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
    //             │ ─┤ ─┤       │ ─┤ ─┤         
    //             └──┴──┘       └──┴──┘ 
withClock(io.nvdla_core_clk){

    val cvt_data_in_wire = VecInit((0 to conf.NVDLA_SDP_MAX_THROUGHPUT-1) 
                            map { i => io.cvt_pd_in.bits(32*i+31, 32*i)})
    val cvt_data_out_wire = Wire(Vec(conf.NVDLA_SDP_MAX_THROUGHPUT, UInt(16.W)))
    val cvt_pd_out8 = VecInit((0 to conf.NVDLA_SDP_MAX_THROUGHPUT-1) 
                            map { i => cvt_data_out_wire(i)(7, 0)}).asUInt
    val cvt_pd_out16 = cvt_data_out_wire.asUInt
    val cvt_pd_out_0 = Wire(UInt(conf.CV_OUT_DW.W))
    cvt_pd_out_0 := Mux(io.cfg_out_precision === 0.U, cvt_pd_out8, cvt_pd_out16)
    val cvt_sat_out_wire = Wire(Vec(conf.NVDLA_SDP_MAX_THROUGHPUT, Bool()))
    val cvt_pd_out_1 = cvt_sat_out_wire.asUInt
    io.cvt_pd_out.bits := Cat(cvt_pd_out_1, cvt_pd_out_0) 

    val cvt_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_MAX_THROUGHPUT, Bool()))
    val cvt_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_MAX_THROUGHPUT, Bool()))
    val c_int = Array.fill(conf.NVDLA_SDP_MAX_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_C_int)}
    for(i <- 0 to conf.NVDLA_SDP_MAX_THROUGHPUT-1){

        c_int(i).io.nvdla_core_clk := io.nvdla_core_clk

        c_int(i).io.cvt_in.valid := io.cvt_pd_in.valid
        cvt_in_prdy_wire(i) := c_int(i).io.cvt_in.ready
        c_int(i).io.cvt_in.bits := cvt_data_in_wire(i)

        cvt_out_pvld_wire(i) := c_int(i).io.cvt_out.valid
        c_int(i).io.cvt_out.ready := io.cvt_pd_out.ready
        cvt_data_out_wire(i) := c_int(i).io.cvt_out.bits.data
        cvt_sat_out_wire(i) := c_int(i).io.cvt_out.bits.sat

        c_int(i).io.cfg_cvt <> io.cfg_cvt
        c_int(i).io.cfg_mode_eql := io.cfg_mode_eql
        c_int(i).io.cfg_out_precision := io.cfg_out_precision
   
    }

    io.cvt_pd_in.ready := cvt_in_prdy_wire(0)
    io.cvt_pd_out.valid := cvt_out_pvld_wire(0)
}}


