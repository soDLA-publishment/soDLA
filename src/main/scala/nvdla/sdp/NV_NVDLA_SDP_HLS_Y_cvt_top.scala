package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_HLS_Y_cvt_top(implicit conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val cvt_data_in = Flipped(DecoupledIO(UInt(conf.EW_OP_DW.W)))
        val cvt_data_out = DecoupledIO(UInt(conf.EW_OC_DW.W))
    
        val cfg_cvt_bypass = Input(Bool())
        val cfg_cvt_offset = Input(UInt(32.W))
        val cfg_cvt_scale = Input(UInt(16.W))
        val cfg_cvt_truncate = Input(UInt(6.W)) 
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

    val cvt_data_in_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
                            map { i => io.cvt_data_in.bits(16*i+15, 16*i)})
    val cvt_data_out_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
    val cvt_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val cvt_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))

    io.cvt_data_out.bits := cvt_data_out_wire.asUInt

    val y_int_cvt = Array.fill(conf.NVDLA_SDP_EW_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_Y_int_cvt)}

    for(i <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){

        y_int_cvt(i).io.nvdla_core_clk := io.nvdla_core_clk

        y_int_cvt(i).io.cvt_data_in.valid := io.cvt_data_in.valid
        cvt_in_prdy_wire(i) := y_int_cvt(i).io.cvt_data_in.ready
        y_int_cvt(i).io.cvt_data_in.bits := cvt_data_in_wire(i)
        
        cvt_out_pvld_wire(i) := y_int_cvt(i).io.cvt_data_out.valid
        y_int_cvt(i).io.cvt_data_out.ready := io.cvt_data_out.ready
        cvt_data_out_wire(i) := y_int_cvt(i).io.cvt_data_out.bits

        y_int_cvt(i).io.cfg_cvt_bypass := io.cfg_cvt_bypass
        y_int_cvt(i).io.cfg_cvt_offset := io.cfg_cvt_offset
        y_int_cvt(i).io.cfg_cvt_scale := io.cfg_cvt_scale
        y_int_cvt(i).io.cfg_cvt_truncate := io.cfg_cvt_truncate
    }

    io.cvt_data_in.ready := cvt_in_prdy_wire(0)
    io.cvt_data_out.valid := cvt_out_pvld_wire(0)


}}


object NV_NVDLA_SDP_HLS_Y_cvt_topDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_cvt_top)
}