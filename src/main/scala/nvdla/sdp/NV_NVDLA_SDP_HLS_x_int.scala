package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

@chiselName
class NV_NVDLA_SDP_HLS_x_int(throughtput: Int)(implicit conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        //data_in
        val chn_data_in = Flipped(DecoupledIO(UInt((32*throughtput).W)))
        //alu_op
        val chn_alu_op = Flipped(DecoupledIO(UInt((16*throughtput).W)))     
        //mul_op
        val chn_mul_op = Flipped(DecoupledIO(UInt((16*throughtput).W)))
        //chn_data
        val chn_data_out = DecoupledIO(UInt((32*throughtput).W))
        //cfg
        val cfg_mul = Flipped(new sdp_x_int_mul_cfg_if)
        val cfg_alu = Flipped(new sdp_x_int_alu_cfg_if)
        val cfg_relu_bypass = Input(Bool())

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

    val chn_alu_op_prdy_wire = Wire(Vec(throughtput, Bool()))
    val chn_in_prdy_wire = Wire(Vec(throughtput, Bool()))
    val chn_mul_op_prdy_wire = Wire(Vec(throughtput, Bool()))
    val chn_out_pvld_wire = Wire(Vec(throughtput, Bool()))
    val chn_data_out_wire = Wire(Vec(throughtput, UInt(32.W)))

    val u_sdp_x_alu = Array.fill(throughtput){Module(new NV_NVDLA_SDP_HLS_X_int_alu)}
    val u_sdp_x_mul = Array.fill(throughtput){Module(new NV_NVDLA_SDP_HLS_X_int_mul)}
    val u_sdp_x_trt = Array.fill(throughtput){Module(new NV_NVDLA_SDP_HLS_X_int_trt)}
    val u_sdp_x_relu = Array.fill(throughtput){Module(new NV_NVDLA_SDP_HLS_X_int_relu)}

    for(i <- 0 to throughtput-1){
       
        //u_sdp_x_alu
        u_sdp_x_alu(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_sdp_x_alu(i).io.chn_alu_op.valid := io.chn_alu_op.valid
        chn_alu_op_prdy_wire(i) := u_sdp_x_alu(i).io.chn_alu_op.ready
        u_sdp_x_alu(i).io.chn_alu_op.bits := io.chn_alu_op.bits(16*i+15, 16*i)

        u_sdp_x_alu(i).io.alu_data_in.valid := io.chn_data_in.valid
        chn_in_prdy_wire(i) := u_sdp_x_alu(i).io.alu_data_in.ready
        u_sdp_x_alu(i).io.alu_data_in.bits := io.chn_data_in.bits(32*i+31, 32*i)

        u_sdp_x_alu(i).io.cfg_alu <> io.cfg_alu

        //u_sdp_x_mul
        u_sdp_x_mul(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_sdp_x_mul(i).io.alu_data_out <> u_sdp_x_alu(i).io.alu_data_out

        u_sdp_x_mul(i).io.chn_mul_op.valid := io.chn_mul_op.valid
        chn_mul_op_prdy_wire(i) := u_sdp_x_mul(i).io.chn_mul_op.ready
        u_sdp_x_mul(i).io.chn_mul_op.bits := io.chn_mul_op.bits(16*i+15, 16*i)

        u_sdp_x_mul(i).io.cfg_mul <> io.cfg_mul

        //u_sdp_x_trt
        u_sdp_x_trt(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_sdp_x_trt(i).io.mul_data_out <> u_sdp_x_mul(i).io.mul_data_out

        u_sdp_x_trt(i).io.cfg_mul_shift_value := io.cfg_mul.shift_value
        u_sdp_x_trt(i).io.bypass_trt_in := u_sdp_x_mul(i).io.bypass_trt_out

        //u_sdp_x_relu
        u_sdp_x_relu(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_sdp_x_relu(i).io.trt_data_out <> u_sdp_x_trt(i).io.trt_data_out

        chn_out_pvld_wire(i) := u_sdp_x_relu(i).io.relu_data_out.valid
        u_sdp_x_relu(i).io.relu_data_out.ready := io.chn_data_out.ready
        chn_data_out_wire(i) := u_sdp_x_relu(i).io.relu_data_out.bits

        u_sdp_x_relu(i).io.cfg_relu_bypass := io.cfg_relu_bypass

    }

    io.chn_data_in.ready := chn_in_prdy_wire(0)
    io.chn_alu_op.ready := chn_alu_op_prdy_wire(0)
    io.chn_mul_op.ready := chn_mul_op_prdy_wire(0)
    io.chn_data_out.valid := chn_out_pvld_wire(0)

    io.chn_data_out.bits := chn_data_out_wire.asUInt

}}

