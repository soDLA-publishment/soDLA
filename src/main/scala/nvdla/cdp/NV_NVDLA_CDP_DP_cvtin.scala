package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_DP_cvtin(implicit val conf: cdpConfiguration) extends Module {

    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())

        //cdp_rdma2dp
        val cdp_rdma2dp_valid = Input(Bool())
        val cdp_rdma2dp_ready = Output(Bool())
        val cdp_rdma2dp_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE+25).W))

        val reg2dp_datin_offset = Input(UInt(16.W))
        val reg2dp_datin_scale = Input(UInt(16.W))
        val reg2dp_datin_shifter = Input(UInt(5.W))

        val cvt2buf_pd = Output(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W))
        val cvt2buf_pvld = Output(Bool())
        val cvt2buf_prdy = Input(Bool())

        val cvt2sync_pd = Output(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W))
        val cvt2sync_pvld = Output(Bool())
        val cvt2sync_prdy = Input(Bool())
    })

    /////////////////////////////////////////////////////////////
withClock(io.nvdla_core_clk){

    val cdp_cvtin_input_rdy_f = Wire(Bool())
    val data_info_in_rdy = Wire(Bool())
    io.cdp_rdma2dp_ready := cdp_cvtin_input_rdy_f & data_info_in_rdy

//===============================================
//pipeline delay for data info to sync with data path
//-----------------------------------------------
//data info valid in

    val data_info_in_vld = io.cdp_rdma2dp_valid & cdp_cvtin_input_rdy_f
    val data_info_in_pd = io.cdp_rdma2dp_pd(conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE+24, conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE)

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
    val cdp_cvtin_input_vld_f = io.cdp_rdma2dp_valid & data_info_in_rdy
//cvtin ready input
    val cdp_cvtin_input_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    cdp_cvtin_input_rdy_f := cdp_cvtin_input_rdy.asUInt.andR

//cvt sub-unit valid in
    val cdp_cvtin_input_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    //  NVDLA_CDP_THROUGHPUT = 8
    cdp_cvtin_input_vld(0) := cdp_cvtin_input_vld_f & cdp_cvtin_input_rdy(1) & cdp_cvtin_input_rdy(2) & cdp_cvtin_input_rdy(3) & cdp_cvtin_input_rdy(4) & cdp_cvtin_input_rdy(5) & cdp_cvtin_input_rdy(6) & cdp_cvtin_input_rdy(7)    
    cdp_cvtin_input_vld(1) := cdp_cvtin_input_vld_f & cdp_cvtin_input_rdy(0) & cdp_cvtin_input_rdy(2) & cdp_cvtin_input_rdy(3) & cdp_cvtin_input_rdy(4) & cdp_cvtin_input_rdy(5) & cdp_cvtin_input_rdy(6) & cdp_cvtin_input_rdy(7)    
    cdp_cvtin_input_vld(2) := cdp_cvtin_input_vld_f & cdp_cvtin_input_rdy(0) & cdp_cvtin_input_rdy(1) & cdp_cvtin_input_rdy(3) & cdp_cvtin_input_rdy(4) & cdp_cvtin_input_rdy(5) & cdp_cvtin_input_rdy(6) & cdp_cvtin_input_rdy(7)    
    cdp_cvtin_input_vld(3) := cdp_cvtin_input_vld_f & cdp_cvtin_input_rdy(0) & cdp_cvtin_input_rdy(1) & cdp_cvtin_input_rdy(2) & cdp_cvtin_input_rdy(4) & cdp_cvtin_input_rdy(5) & cdp_cvtin_input_rdy(6) & cdp_cvtin_input_rdy(7)    
    cdp_cvtin_input_vld(4) := cdp_cvtin_input_vld_f & cdp_cvtin_input_rdy(0) & cdp_cvtin_input_rdy(1) & cdp_cvtin_input_rdy(2) & cdp_cvtin_input_rdy(3) & cdp_cvtin_input_rdy(5) & cdp_cvtin_input_rdy(6) & cdp_cvtin_input_rdy(7)    
    cdp_cvtin_input_vld(5) := cdp_cvtin_input_vld_f & cdp_cvtin_input_rdy(0) & cdp_cvtin_input_rdy(1) & cdp_cvtin_input_rdy(2) & cdp_cvtin_input_rdy(3) & cdp_cvtin_input_rdy(4) & cdp_cvtin_input_rdy(6) & cdp_cvtin_input_rdy(7)    
    cdp_cvtin_input_vld(6) := cdp_cvtin_input_vld_f & cdp_cvtin_input_rdy(0) & cdp_cvtin_input_rdy(1) & cdp_cvtin_input_rdy(2) & cdp_cvtin_input_rdy(3) & cdp_cvtin_input_rdy(4) & cdp_cvtin_input_rdy(5) & cdp_cvtin_input_rdy(7)    
    cdp_cvtin_input_vld(7) := cdp_cvtin_input_vld_f & cdp_cvtin_input_rdy(0) & cdp_cvtin_input_rdy(1) & cdp_cvtin_input_rdy(2) & cdp_cvtin_input_rdy(3) & cdp_cvtin_input_rdy(4) & cdp_cvtin_input_rdy(5) & cdp_cvtin_input_rdy(6)    

//cvt sub-unit data in
    val cdp_cvtin_input_pd = VecInit(
        (0 until conf.NVDLA_CDP_THROUGHPUT) map {
            i => io.cdp_rdma2dp_pd(conf.NVDLA_CDP_BWPE*i+conf.NVDLA_CDP_BWPE-1, conf.NVDLA_CDP_BWPE*i)
        })

    val reg2dp_datin_offset_use = RegInit(0.U(16.W))
    reg2dp_datin_offset_use := io.reg2dp_datin_offset

    val reg2dp_datin_scale_use = RegInit(0.U(16.W))
    reg2dp_datin_scale_use := io.reg2dp_datin_scale

    val reg2dp_datin_shifter_use = RegInit(0.U(5.W))
    reg2dp_datin_shifter_use := io.reg2dp_datin_shifter

    val cdp_cvtin_output_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    val cdp_cvtin_output_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    val cdp_cvtin_output_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(conf.NVDLA_CDP_ICVTO_BWPE.W)))

    val u_HLS_cdp_icvt = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new HLS_cdp_icvt)}
    for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
        u_HLS_cdp_icvt(i).io.nvdla_core_clk := io.nvdla_core_clk
        u_HLS_cdp_icvt(i).io.cfg_alu_in_rsc_z := reg2dp_datin_offset_use
        u_HLS_cdp_icvt(i).io.cfg_mul_in_rsc_z := reg2dp_datin_scale_use
        u_HLS_cdp_icvt(i).io.cfg_truncate_rsc_z := reg2dp_datin_shifter_use
        u_HLS_cdp_icvt(i).io.chn_data_in_rsc_vz := cdp_cvtin_input_vld(i)
        u_HLS_cdp_icvt(i).io.chn_data_in_rsc_z := cdp_cvtin_input_pd(i)
        u_HLS_cdp_icvt(i).io.chn_data_out_rsc_vz := cdp_cvtin_output_rdy(i)
        cdp_cvtin_input_rdy(i) := u_HLS_cdp_icvt(i).io.chn_data_in_rsc_lz
        cdp_cvtin_output_vld(i) := u_HLS_cdp_icvt(i).io.chn_data_out_rsc_lz
        cdp_cvtin_output_pd(i) := u_HLS_cdp_icvt(i).io.chn_data_out_rsc_z
    }

//sub-unit output ready
    val cdp_cvtin_output_rdy_f = Wire(Bool())
    //  NVDLA_CDP_THROUGHPUT = 8
    cdp_cvtin_output_rdy(0) := cdp_cvtin_output_rdy_f & cdp_cvtin_output_vld(1) & cdp_cvtin_output_vld(2) & cdp_cvtin_output_vld(3) & cdp_cvtin_output_vld(4) & cdp_cvtin_output_vld(5) & cdp_cvtin_output_vld(6) & cdp_cvtin_output_vld(7)    
    cdp_cvtin_output_rdy(1) := cdp_cvtin_output_rdy_f & cdp_cvtin_output_vld(0) & cdp_cvtin_output_vld(2) & cdp_cvtin_output_vld(3) & cdp_cvtin_output_vld(4) & cdp_cvtin_output_vld(5) & cdp_cvtin_output_vld(6) & cdp_cvtin_output_vld(7)    
    cdp_cvtin_output_rdy(2) := cdp_cvtin_output_rdy_f & cdp_cvtin_output_vld(0) & cdp_cvtin_output_vld(1) & cdp_cvtin_output_vld(3) & cdp_cvtin_output_vld(4) & cdp_cvtin_output_vld(5) & cdp_cvtin_output_vld(6) & cdp_cvtin_output_vld(7)    
    cdp_cvtin_output_rdy(3) := cdp_cvtin_output_rdy_f & cdp_cvtin_output_vld(0) & cdp_cvtin_output_vld(1) & cdp_cvtin_output_vld(2) & cdp_cvtin_output_vld(4) & cdp_cvtin_output_vld(5) & cdp_cvtin_output_vld(6) & cdp_cvtin_output_vld(7)    
    cdp_cvtin_output_rdy(4) := cdp_cvtin_output_rdy_f & cdp_cvtin_output_vld(0) & cdp_cvtin_output_vld(1) & cdp_cvtin_output_vld(2) & cdp_cvtin_output_vld(3) & cdp_cvtin_output_vld(5) & cdp_cvtin_output_vld(6) & cdp_cvtin_output_vld(7)    
    cdp_cvtin_output_rdy(5) := cdp_cvtin_output_rdy_f & cdp_cvtin_output_vld(0) & cdp_cvtin_output_vld(1) & cdp_cvtin_output_vld(2) & cdp_cvtin_output_vld(3) & cdp_cvtin_output_vld(4) & cdp_cvtin_output_vld(6) & cdp_cvtin_output_vld(7)    
    cdp_cvtin_output_rdy(6) := cdp_cvtin_output_rdy_f & cdp_cvtin_output_vld(0) & cdp_cvtin_output_vld(1) & cdp_cvtin_output_vld(2) & cdp_cvtin_output_vld(3) & cdp_cvtin_output_vld(4) & cdp_cvtin_output_vld(5) & cdp_cvtin_output_vld(7)    
    cdp_cvtin_output_rdy(7) := cdp_cvtin_output_rdy_f & cdp_cvtin_output_vld(0) & cdp_cvtin_output_vld(1) & cdp_cvtin_output_vld(2) & cdp_cvtin_output_vld(3) & cdp_cvtin_output_vld(4) & cdp_cvtin_output_vld(5) & cdp_cvtin_output_vld(6)    

//output valid
    val cdp_cvtin_output_vld_f = cdp_cvtin_output_vld.asUInt.andR
//output ready
    val cvtin_o_prdy = Wire(Bool())
    cdp_cvtin_output_rdy_f := cvtin_o_prdy & data_info_out_vld
//output data
    val cdp_cvtin_output_pd_all = 
        Cat(cdp_cvtin_output_pd(7), cdp_cvtin_output_pd(6), 
            cdp_cvtin_output_pd(5), cdp_cvtin_output_pd(4), 
            cdp_cvtin_output_pd(3), cdp_cvtin_output_pd(2), 
            cdp_cvtin_output_pd(1), cdp_cvtin_output_pd(0))   

//===============================================
//data info output
//-----------------------------------------------
//data info output ready
    data_info_out_rdy := cvtin_o_prdy & cdp_cvtin_output_vld_f

//===============================================
//convertor output
//-----------------------------------------------
    cvtin_o_prdy := io.cvt2buf_prdy & io.cvt2sync_prdy
    val cvtin_o_pvld = cdp_cvtin_output_vld_f & data_info_out_vld

    val invalid_flag = data_info_out_pd(17+conf.NVDLA_CDP_THROUGHPUT-1, 17)

    val icvt_out_pd = Cat(
        Mux(invalid_flag(7), Fill(conf.NVDLA_CDP_ICVTO_BWPE, false.B), cdp_cvtin_output_pd_all(conf.NVDLA_CDP_ICVTO_BWPE*7+conf.NVDLA_CDP_ICVTO_BWPE-1, conf.NVDLA_CDP_ICVTO_BWPE*7)), 
        Mux(invalid_flag(6), Fill(conf.NVDLA_CDP_ICVTO_BWPE, false.B), cdp_cvtin_output_pd_all(conf.NVDLA_CDP_ICVTO_BWPE*6+conf.NVDLA_CDP_ICVTO_BWPE-1, conf.NVDLA_CDP_ICVTO_BWPE*6)), 
        Mux(invalid_flag(5), Fill(conf.NVDLA_CDP_ICVTO_BWPE, false.B), cdp_cvtin_output_pd_all(conf.NVDLA_CDP_ICVTO_BWPE*5+conf.NVDLA_CDP_ICVTO_BWPE-1, conf.NVDLA_CDP_ICVTO_BWPE*5)), 
        Mux(invalid_flag(4), Fill(conf.NVDLA_CDP_ICVTO_BWPE, false.B), cdp_cvtin_output_pd_all(conf.NVDLA_CDP_ICVTO_BWPE*4+conf.NVDLA_CDP_ICVTO_BWPE-1, conf.NVDLA_CDP_ICVTO_BWPE*4)), 
        Mux(invalid_flag(3), Fill(conf.NVDLA_CDP_ICVTO_BWPE, false.B), cdp_cvtin_output_pd_all(conf.NVDLA_CDP_ICVTO_BWPE*3+conf.NVDLA_CDP_ICVTO_BWPE-1, conf.NVDLA_CDP_ICVTO_BWPE*3)), 
        Mux(invalid_flag(2), Fill(conf.NVDLA_CDP_ICVTO_BWPE, false.B), cdp_cvtin_output_pd_all(conf.NVDLA_CDP_ICVTO_BWPE*2+conf.NVDLA_CDP_ICVTO_BWPE-1, conf.NVDLA_CDP_ICVTO_BWPE*2)), 
        Mux(invalid_flag(1), Fill(conf.NVDLA_CDP_ICVTO_BWPE, false.B), cdp_cvtin_output_pd_all(conf.NVDLA_CDP_ICVTO_BWPE*1+conf.NVDLA_CDP_ICVTO_BWPE-1, conf.NVDLA_CDP_ICVTO_BWPE*1)), 
        (Fill(4, (~invalid_flag(0))) & cdp_cvtin_output_pd_all(conf.NVDLA_CDP_ICVTO_BWPE-1, 0))
    )

    io.cvt2buf_pd   := Cat(data_info_out_pd(16, 0), icvt_out_pd)
    io.cvt2buf_pvld := cvtin_o_pvld & io.cvt2sync_prdy
    io.cvt2sync_pvld := cvtin_o_pvld & io.cvt2buf_prdy
    io.cvt2sync_pd   := Cat(data_info_out_pd(16, 0), icvt_out_pd)

}
}
      
object NV_NVDLA_CDP_DP_cvtinDriver extends App {
    implicit val conf: cdpConfiguration = new cdpConfiguration
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_cvtin())
}
