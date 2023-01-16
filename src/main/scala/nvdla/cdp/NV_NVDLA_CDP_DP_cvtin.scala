package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class cdp_dp_cvtin_reg2dp_if extends Bundle{
    val offset = Output(UInt(16.W))
    val scale = Output(UInt(16.W))
    val shifter = Output(UInt(16.W))
}

class NV_NVDLA_CDP_DP_cvtin(implicit val conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())

        //cdp_rdma2dp
        val cdp_rdma2dp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE+25).W)))
        //cdp2buf
        val cvt2buf_pd = DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W))
        //cvt2sync
        val cvt2sync_pd = DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W))
        //config
        val reg2dp_datin = Flipped(new cdp_dp_cvtin_reg2dp_if)
    })

withClock(io.nvdla_core_clk){

    val cdp_cvtin_input_rdy_f = Wire(Bool())
    val data_info_in_rdy = Wire(Bool())
    io.cdp_rdma2dp_pd.ready := cdp_cvtin_input_rdy_f & data_info_in_rdy

    //===============================================
    //pipeline delay for data info to sync with data path
    //-----------------------------------------------
    //data info valid in
    val data_info_in_vld = io.cdp_rdma2dp_pd.valid & cdp_cvtin_input_rdy_f
    val data_info_in_pd = io.cdp_rdma2dp_pd.bits(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE+24, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE)

    val data_info_in_vld_d0 = data_info_in_vld
    val data_info_in_pd_d0 = data_info_in_pd(24, 0)

    val data_info_in_rdy_d1_f = Wire(Bool())
    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(25))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := data_info_in_vld_d0
    val data_info_in_rdy_d0 = pipe_p1.io.ro
    pipe_p1.io.di := data_info_in_pd_d0
    val data_info_in_vld_d1 = pipe_p1.io.vo
    pipe_p1.io.ri := data_info_in_rdy_d1_f
    val data_info_in_pd_d1 = pipe_p1.io.dout

    val data_info_in_rdy_d2_f = Wire(Bool())
    val pipe_p2 = Module(new NV_NVDLA_IS_pipe(25))
    pipe_p2.io.clk := io.nvdla_core_clk
    pipe_p2.io.vi := data_info_in_vld_d1
    val data_info_in_rdy_d1 = pipe_p2.io.ro
    pipe_p2.io.di := data_info_in_pd_d1
    val data_info_in_vld_d2 = pipe_p2.io.vo
    pipe_p2.io.ri := data_info_in_rdy_d2_f
    val data_info_in_pd_d2 = pipe_p2.io.dout

    val data_info_in_rdy_d3 = Wire(Bool())
    val pipe_p3 = Module(new NV_NVDLA_IS_pipe(25))
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := data_info_in_vld_d2
    val data_info_in_rdy_d2 = pipe_p3.io.ro
    pipe_p3.io.di := data_info_in_pd_d2
    val data_info_in_vld_d3 = pipe_p3.io.vo
    pipe_p3.io.ri := data_info_in_rdy_d3
    val data_info_in_pd_d3 = pipe_p3.io.dout

    data_info_in_rdy := data_info_in_rdy_d0
    data_info_in_rdy_d1_f := data_info_in_rdy_d1
    data_info_in_rdy_d2_f := data_info_in_rdy_d2

    val data_info_out_vld = data_info_in_vld_d3
    val data_info_out_rdy = Wire(Bool())
    data_info_in_rdy_d3 := data_info_out_rdy
    val data_info_out_pd = data_info_in_pd_d3

    //===============================================
    //convertor process
    //-----------------------------------------------
    //cvtin valid input
    val cdp_cvtin_input_vld_f = io.cdp_rdma2dp_pd.valid & data_info_in_rdy
    //cvtin ready input
    val cdp_cvtin_input_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    cdp_cvtin_input_rdy_f := cdp_cvtin_input_rdy.asUInt.andR

    //cvt sub-unit valid in
    val cdp_cvtin_input_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    for(i <- 0 to conf.NVDLA_CDP_THROUGHPUT-1){
        cdp_cvtin_input_vld(i) := cdp_cvtin_input_vld_f & cdp_cvtin_input_rdy.drop(i).reduce(_ && _)
    }

    //cvt sub-unit data in
    val cdp_cvtin_input_pd = VecInit(
        (0 until conf.NVDLA_CDP_THROUGHPUT) map {
            i => io.cdp_rdma2dp_pd.bits(conf.NVDLA_CDP_BWPE*i+conf.NVDLA_CDP_BWPE-1, conf.NVDLA_CDP_BWPE*i)
        })

    val reg2dp_datin_offset_use = RegInit(0.U(16.W))
    val reg2dp_datin_scale_use = RegInit(0.U(16.W))
    val reg2dp_datin_shifter_use = RegInit(0.U(5.W))

    reg2dp_datin_offset_use := io.reg2dp_datin.offset
    reg2dp_datin_scale_use := io.reg2dp_datin.scale
    reg2dp_datin_shifter_use := io.reg2dp_datin.shifter

    val cdp_cvtin_output_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    val cdp_cvtin_output_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    val cdp_cvtin_output_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(conf.NVDLA_CDP_ICVTO_BWPE.W)))

    val u_HLS_cdp_icvt = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new HLS_cdp_icvt)}
    for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
        u_HLS_cdp_icvt(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_HLS_cdp_icvt(i).io.chn_data_in_rsc_z.valid := cdp_cvtin_input_vld(i)
        cdp_cvtin_input_rdy(i) := u_HLS_cdp_icvt(i).io.chn_data_in_rsc_z.ready
        u_HLS_cdp_icvt(i).io.chn_data_in_rsc_z.bits := cdp_cvtin_input_pd(i)

        cdp_cvtin_output_vld(i) := u_HLS_cdp_icvt(i).io.chn_data_out_rsc_z.valid
        u_HLS_cdp_icvt(i).io.chn_data_out_rsc_z.ready := cdp_cvtin_output_rdy(i)
        cdp_cvtin_output_pd(i) := u_HLS_cdp_icvt(i).io.chn_data_out_rsc_z.bits

        u_HLS_cdp_icvt(i).io.cfg_alu_in_rsc_z := reg2dp_datin_offset_use
        u_HLS_cdp_icvt(i).io.cfg_mul_in_rsc_z := reg2dp_datin_scale_use
        u_HLS_cdp_icvt(i).io.cfg_truncate_rsc_z := reg2dp_datin_shifter_use
    }

    //sub-unit output ready
    val cdp_cvtin_output_rdy_f = Wire(Bool())
    
    for(i <- 0 to conf.NVDLA_CDP_THROUGHPUT-1){
        cdp_cvtin_output_rdy(i) := cdp_cvtin_output_rdy_f & cdp_cvtin_output_vld.drop(i).reduce(_ && _)
    }       

    //output valid
    val cdp_cvtin_output_vld_f = cdp_cvtin_output_vld.asUInt.andR
    //output ready
    val cvtin_o_prdy = Wire(Bool())
    cdp_cvtin_output_rdy_f := cvtin_o_prdy & data_info_out_vld

    //===============================================
    //data info output
    //-----------------------------------------------
    //data info output ready
    data_info_out_rdy := cvtin_o_prdy & cdp_cvtin_output_vld_f

    //===============================================
    //convertor output
    //-----------------------------------------------
    cvtin_o_prdy := io.cvt2buf_pd.ready & io.cvt2sync_pd.ready
    val cvtin_o_pvld = cdp_cvtin_output_vld_f & data_info_out_vld

    val invalid_flag = data_info_out_pd(17+conf.NVDLA_CDP_THROUGHPUT-1, 17)

    val icvt_out_pd_vec = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(conf.NVDLA_CDP_ICVTO_BWPE.W)))

    icvt_out_pd_vec(0) := Fill(conf.NVDLA_CDP_ICVTO_BWPE, ~invalid_flag(0)) & cdp_cvtin_output_pd(0)
    for(i <- 1 to conf.NVDLA_CDP_THROUGHPUT-1){
        icvt_out_pd_vec(i) := Mux(invalid_flag(i), Fill(conf.NVDLA_CDP_ICVTO_BWPE, false.B), cdp_cvtin_output_pd(i))
    }

    val icvt_out_pd = icvt_out_pd_vec.asUInt

    io.cvt2buf_pd.bits   := Cat(data_info_out_pd(16, 0), icvt_out_pd)
    io.cvt2buf_pd.valid := cvtin_o_pvld & io.cvt2sync_pd.ready
    io.cvt2sync_pd.valid := cvtin_o_pvld & io.cvt2buf_pd.ready
    io.cvt2sync_pd.bits   := Cat(data_info_out_pd(16, 0), icvt_out_pd)

}
}
      
object NV_NVDLA_CDP_DP_cvtinDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_cvtin())
}
