package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

@chiselName
class NV_NVDLA_SDP_HLS_Y_int_core(implicit conf: nvdlaConfig) extends Module{
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        //chn_alu_op
        val chn_alu_op = Flipped(DecoupledIO(UInt(conf.EW_OC_DW.W)))
        //chn_data_in
        val chn_data_in = Flipped(DecoupledIO(UInt(conf.EW_IN_DW.W)))
        //chn_mul_op
        val chn_mul_op = Flipped(DecoupledIO(UInt(conf.EW_OC_DW.W)))
        //chn_data_out
        val chn_data_out = DecoupledIO(UInt(conf.EW_CORE_OUT_DW.W))

        val cfg_alu = Flipped(new sdp_y_int_alu_cfg_if) 
        val cfg_mul = Flipped(new sdp_y_int_mul_cfg_if)
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

    val chn_alu_op_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
    val chn_alu_op_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val chn_data_in_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
    val chn_data_out_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
    val chn_in_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val chn_mul_op_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
    val chn_mul_op_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val chn_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val mul_data_out_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, UInt(32.W)))
    val mul_out_prdy_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))
    val mul_out_pvld_wire = Wire(Vec(conf.NVDLA_SDP_EW_THROUGHPUT, Bool()))

    io.chn_data_in.ready := chn_in_prdy_wire(0)
    io.chn_alu_op.ready := chn_alu_op_prdy_wire(0)
    io.chn_mul_op.ready := chn_mul_op_prdy_wire(0)
    io.chn_data_out.valid := chn_out_pvld_wire(0)
    
    for(i <- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){
        chn_data_in_wire(i) := io.chn_data_in.bits(32*i+31, 32*i)
        chn_alu_op_wire(i) := io.chn_alu_op.bits(32*i+31, 32*i)
        chn_mul_op_wire(i) := io.chn_mul_op.bits(32*i+31, 32*i)
    }
    io.chn_data_out.bits := VecInit((0 to conf.NVDLA_SDP_EW_THROUGHPUT-1) map {
                        i => chn_data_out_wire(i)}).asUInt
    
    val u_sdp_y_core_mul = Array.fill(conf.NVDLA_SDP_EW_THROUGHPUT){Module{new NV_NVDLA_SDP_HLS_Y_int_mul}}
    val u_sdp_y_core_alu = Array.fill(conf.NVDLA_SDP_EW_THROUGHPUT){Module{new NV_NVDLA_SDP_HLS_Y_int_alu}}

    for(i<- 0 to conf.NVDLA_SDP_EW_THROUGHPUT-1){

    //: NV_NVDLA_SDP_HLS_Y_int_mul
        u_sdp_y_core_mul(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_sdp_y_core_mul(i).io.chn_mul_in.valid := io.chn_data_in.valid
        chn_in_prdy_wire(i) := u_sdp_y_core_mul(i).io.chn_mul_in.ready
        u_sdp_y_core_mul(i).io.chn_mul_in.bits := chn_data_in_wire(i)

        u_sdp_y_core_mul(i).io.chn_mul_op.valid := io.chn_mul_op.valid
        chn_mul_op_prdy_wire(i) := u_sdp_y_core_mul(i).io.chn_mul_op.ready
        u_sdp_y_core_mul(i).io.chn_mul_op.bits := chn_mul_op_wire(i)

        mul_out_pvld_wire(i) := u_sdp_y_core_mul(i).io.mul_data_out.valid
        u_sdp_y_core_mul(i).io.mul_data_out.ready := mul_out_prdy_wire(i)
        mul_data_out_wire(i) := u_sdp_y_core_mul(i).io.mul_data_out.bits

        u_sdp_y_core_mul(i).io.cfg_mul <> io.cfg_mul

    //: NV_NVDLA_SDP_HLS_Y_int_alu
        u_sdp_y_core_alu(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_sdp_y_core_alu(i).io.alu_data_in.valid := mul_out_pvld_wire(i)
        mul_out_prdy_wire(i) := u_sdp_y_core_alu(i).io.alu_data_in.ready
        u_sdp_y_core_alu(i).io.alu_data_in.bits := mul_data_out_wire(i)

        u_sdp_y_core_alu(i).io.chn_alu_op.valid := io.chn_alu_op.valid
        chn_alu_op_prdy_wire(i) := u_sdp_y_core_alu(i).io.chn_alu_op.ready
        u_sdp_y_core_alu(i).io.chn_alu_op.bits := chn_alu_op_wire(i)

        chn_out_pvld_wire(i) := u_sdp_y_core_alu(i).io.alu_data_out.valid
        u_sdp_y_core_alu(i).io.alu_data_out.ready := io.chn_data_out.ready
        chn_data_out_wire(i) := u_sdp_y_core_alu(i).io.alu_data_out.bits

        u_sdp_y_core_alu(i).io.cfg_alu <> io.cfg_alu

    }



}}


object NV_NVDLA_SDP_HLS_Y_int_coreDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_HLS_Y_int_core)
}