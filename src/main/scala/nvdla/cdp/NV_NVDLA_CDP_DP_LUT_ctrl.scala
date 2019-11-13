package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_DP_LUT_ctrl(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val sum2itp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT * (conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)).W)))
        val sum2sync_pd = DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT * (conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)).W))
        val dp2lut = DecoupledIO(Vec(conf.NVDLA_CDP_THROUGHPUT, new cdp_dp_lut_ctrl_dp2lut_if))

        val reg2dp_lut = Flipped(new cdp_dp_lut_ctrl_reg2dp_lut_if)
        val reg2dp_sqsum_bypass = Input(Bool())
    })

withClock(io.nvdla_core_clk){

    ////////////////////////////////////////////////////////////////////////////////////////
    val sum2itp_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    io.sum2itp_pd.ready := (sum2itp_rdy.asUInt.andR) & io.sum2sync_pd.ready

    //////////////////////////////////////////////////////////////////////
    //from intp_ctrl input port to sync fifo for interpolation
    io.sum2sync_pd.valid := io.sum2itp_pd.valid & (sum2itp_rdy.asUInt.andR)
    io.sum2sync_pd.bits := io.sum2itp_pd.bits

    ///////////////////////////////////////////
    val sum2itp_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT - 1)){
        for(j <- 0 to (conf.NVDLA_CDP_THROUGHPUT - 1)){
            sum2itp_vld(i) := io.sum2itp_pd.valid & io.sum2sync_pd.ready & 
                        (if(j != i) sum2itp_rdy(j) else true.B) 
        }
    }

    val sum2itp_pd = VecInit(
        (0 to conf.NVDLA_CDP_THROUGHPUT-1) map {
            i => io.sum2itp_pd.bits((conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)*i+(conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)-1, (conf.NVDLA_CDP_ICVTO_BWPE * 2 - 1 + 4)*i)
            })

    val dp2lut_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    val dp2lut_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))

    val u_LUT_CTRL_unit = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new NV_NVDLA_CDP_DP_LUT_CTRL_unit)}
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        u_LUT_CTRL_unit(i).io.nvdla_core_clk := io.nvdla_core_clk
        u_LUT_CTRL_unit(i).io.sum2itp_pd.bits := sum2itp_pd(i)
        u_LUT_CTRL_unit(i).io.sum2itp_pd.valid := sum2itp_vld(i)
        sum2itp_rdy(i) := u_LUT_CTRL_unit(i).io.sum2itp_pd.ready

        u_LUT_CTRL_unit(i).io.reg2dp_lut <> io.reg2dp_lut
        u_LUT_CTRL_unit(i).io.reg2dp_sqsum_bypass := io.reg2dp_sqsum_bypass

        dp2lut_vld(i) := u_LUT_CTRL_unit(i).io.dp2lut.valid
        u_LUT_CTRL_unit(i).io.dp2lut.ready := dp2lut_rdy(i)

        io.dp2lut.bits(i).x_info := u_LUT_CTRL_unit(i).io.dp2lut.bits.x_info
        io.dp2lut.bits(i).x_entry := u_LUT_CTRL_unit(i).io.dp2lut.bits.x_entry
        io.dp2lut.bits(i).y_info := u_LUT_CTRL_unit(i).io.dp2lut.bits.y_info
        io.dp2lut.bits(i).y_entry := u_LUT_CTRL_unit(i).io.dp2lut.bits.y_entry
    }

    io.dp2lut.valid := dp2lut_vld.asUInt.andR

// NVDLA_CDP_THROUGHPUT = 8
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT - 1)){
        for(j <- 0 to (conf.NVDLA_CDP_THROUGHPUT - 1)){
            dp2lut_rdy(i) := io.dp2lut.ready & 
                        (if(j != i) dp2lut_vld(j) else true.B) 
        }
    }
}}


object NV_NVDLA_CDP_DP_LUT_ctrlDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_LUT_ctrl())
}
