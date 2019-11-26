package nvdla

import chisel3._
import chisel3.util._



class NV_NVDLA_SDP_HLS_Y_idx_top(implicit conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val chn_lut_in_pd = Flipped(DecoupledIO(UInt(conf.EW_CORE_OUT_DW.W)))
        val chn_lut_out_pd = DecoupledIO(UInt(conf.EW_IDX_OUT_DW.W))

        val cfg_lut = Flipped(new sdp_y_int_idx_cfg_if)

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
    val chn_lut_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val chn_lut_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val lut_data_in_wire = VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) 
                               map {i => io.chn_lut_in_pd.bits(32*i+31, 32*i)})
    val lut_out_addr_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(9.W)))
    val lut_out_fraction_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(35.W)))
    val lut_out_le_hit_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val lut_out_lo_hit_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val lut_out_oflow_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val lut_out_sel_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val lut_out_uflow_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val lut_out_x_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))

    val y_int_idx = Array.fill(conf.NVDLA_SDP_EW_THROUGHPUT){Module(new NV_NVDLA_SDP_HLS_Y_int_idx)}

    for(i <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){

        y_int_idx(i).io.nvdla_core_clk := io.nvdla_core_clk

        y_int_idx(i).io.lut_data_in.valid := io.chn_lut_in_pd.valid
        chn_lut_in_prdy_wire(i) := y_int_idx(i).io.lut_data_in.ready
        y_int_idx(i).io.lut_data_in.bits := lut_data_in_wire(i)
        
        chn_lut_out_pvld_wire(i) := y_int_idx(i).io.lut_out.valid
        y_int_idx(i).io.lut_out.ready := io.chn_lut_out_pd.ready
        lut_out_fraction_wire(i) := y_int_idx(i).io.lut_out.bits.frac 
        lut_out_le_hit_wire(i) := y_int_idx(i).io.lut_out.bits.le_hit
        lut_out_lo_hit_wire(i) := y_int_idx(i).io.lut_out.bits.lo_hit
        lut_out_oflow_wire(i) := y_int_idx(i).io.lut_out.bits.oflow
        lut_out_addr_wire(i) := y_int_idx(i).io.lut_out.bits.ram_addr 
        lut_out_sel_wire(i) := y_int_idx(i).io.lut_out.bits.ram_sel 
        lut_out_uflow_wire(i) := y_int_idx(i).io.lut_out.bits.uflow 
        lut_out_x_wire(i) := y_int_idx(i).io.lut_out.bits.x

        y_int_idx(i).io.cfg_lut <> io.cfg_lut

    }


    io.chn_lut_in_pd.ready := chn_lut_in_prdy_wire(0)
    io.chn_lut_out_pd.valid := chn_lut_out_pvld_wire(0)
    io.chn_lut_out_pd.bits := Cat(lut_out_lo_hit_wire.asUInt, lut_out_le_hit_wire.asUInt, lut_out_addr_wire.asUInt,
                                lut_out_sel_wire.asUInt, lut_out_uflow_wire.asUInt, lut_out_oflow_wire.asUInt,
                                lut_out_x_wire.asUInt, lut_out_fraction_wire.asUInt)

}}


