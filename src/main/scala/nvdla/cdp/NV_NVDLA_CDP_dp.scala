package nvdla

import chisel3._
import chisel3.util._


class NV_NVDLA_CDP_dp(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {

        val nvdla_core_clk = Input(Clock())
        val nvdla_core_clk_orig = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val cdp_rdma2dp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE+25).W)))
        val cdp_dp2wdma_pd = DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_BWPE+17).W))

        //config  
        val reg2dp_datin = Flipped(new cdp_dp_cvtin_reg2dp_if)
        val reg2dp_datout = Flipped(new cdp_dp_cvtout_reg2dp_if)

        val reg2dp_lut_access_type = Input(Bool())
        val reg2dp_lut_addr = Input(UInt(10.W))
        val reg2dp_lut_data = Input(UInt(16.W))
        val reg2dp_lut_data_trigger = Input(Bool())
        val reg2dp_lut_hybrid_priority = Input(Bool())
        val reg2dp_lut_le_end_high = Input(UInt(6.W))
        val reg2dp_lut_le_end_low = Input(UInt(32.W))
        val reg2dp_lut_le_function = Input(Bool())
        val reg2dp_lut_le_index_offset = Input(UInt(8.W))
        val reg2dp_lut_le_index_select = Input(UInt(8.W))
        val reg2dp_lut_le_slope_oflow_scale = Input(UInt(16.W))
        val reg2dp_lut_le_slope_oflow_shift = Input(UInt(5.W))
        val reg2dp_lut_le_slope_uflow_scale = Input(UInt(16.W))
        val reg2dp_lut_le_slope_uflow_shift = Input(UInt(5.W))
        val reg2dp_lut_le_start_high = Input(UInt(6.W))
        val reg2dp_lut_le_start_low = Input(UInt(32.W))
        val reg2dp_lut_lo_end_high = Input(UInt(6.W))
        val reg2dp_lut_lo_end_low = Input(UInt(32.W))
        val reg2dp_lut_lo_index_select = Input(UInt(8.W))
        val reg2dp_lut_lo_slope_oflow_scale = Input(UInt(16.W))
        val reg2dp_lut_lo_slope_oflow_shift = Input(UInt(5.W))
        val reg2dp_lut_lo_slope_uflow_scale = Input(UInt(16.W))
        val reg2dp_lut_lo_slope_uflow_shift = Input(UInt(5.W))
        val reg2dp_lut_lo_start_high = Input(UInt(6.W))
        val reg2dp_lut_lo_start_low = Input(UInt(32.W))
        val reg2dp_lut_oflow_priority = Input(Bool())
        val reg2dp_lut_table_id = Input(Bool())
        val reg2dp_lut_uflow_priority = Input(Bool())

        val reg2dp_sqsum_bypass = Input(Bool())
        val reg2dp_mul_bypass = Input(Bool())
        val reg2dp_normalz_len = Input(UInt(2.W))
        val dp2reg_done = Input(Bool())
        val dp2reg_perf = new cdp_dp_intp_dp2reg_perf_lut_if
        val dp2reg_lut_data = Output(UInt(16.W))
    

    })

withClock(io.nvdla_core_clk){

    val sqsum_bypass_en = RegInit(false.B)
    sqsum_bypass_en := (io.reg2dp_sqsum_bypass === true.B)

    //===== convertor_in Instance========
    val u_NV_NVDLA_CDP_DP_cvtin = Module(new NV_NVDLA_CDP_DP_cvtin)
    u_NV_NVDLA_CDP_DP_cvtin.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CDP_DP_cvtin.io.cdp_rdma2dp_pd <> io.cdp_rdma2dp_pd
    u_NV_NVDLA_CDP_DP_cvtin.io.reg2dp_datin <> io.reg2dp_datin
    

    //===== sync fifo Instance========
    val u_NV_NVDLA_CDP_DP_syncfifo = Module(new NV_NVDLA_CDP_DP_syncfifo)
    u_NV_NVDLA_CDP_DP_syncfifo.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CDP_DP_syncfifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_NV_NVDLA_CDP_DP_syncfifo.io.cvt2sync_pd <> u_NV_NVDLA_CDP_DP_cvtin.io.cvt2sync_pd

    //===== Buffer_in Instance========
    val u_NV_NVDLA_CDP_DP_bufferin = if(conf.NVDLA_CDP_THROUGHPUT >= 4) Module(new NV_NVDLA_CDP_DP_bufferin)
                                     else Module(new NV_NVDLA_CDP_DP_bufferin_tp1)
    u_NV_NVDLA_CDP_DP_bufferin.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CDP_DP_bufferin.io.cdp_rdma2dp_pd.valid := Mux(sqsum_bypass_en, false.B, u_NV_NVDLA_CDP_DP_cvtin.io.cvt2buf_pd.valid)
    u_NV_NVDLA_CDP_DP_bufferin.io.cdp_rdma2dp_pd.bits := Mux(sqsum_bypass_en, 0.U, u_NV_NVDLA_CDP_DP_cvtin.io.cvt2buf_pd.bits)

    //===== sigma squre Instance========
    val u_NV_NVDLA_CDP_DP_sum = Module(new NV_NVDLA_CDP_DP_sum)
    u_NV_NVDLA_CDP_DP_sum.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CDP_DP_sum.io.normalz_buf_data_pd <> u_NV_NVDLA_CDP_DP_bufferin.io.normalz_buf_data
    u_NV_NVDLA_CDP_DP_sum.io.reg2dp_normalz_len := io.reg2dp_normalz_len

    //===== LUT controller Instance========
    val cvtin_out_int8 = VecInit((0 until conf.NVDLA_CDP_THROUGHPUT) map
        {i => u_NV_NVDLA_CDP_DP_cvtin.io.cvt2buf_pd.bits(i*conf.NVDLA_CDP_ICVTO_BWPE+conf.NVDLA_CDP_ICVTO_BWPE-1, i*conf.NVDLA_CDP_ICVTO_BWPE)}
    )
    val cvtin_out_int8_ext_elem = VecInit((0 until conf.NVDLA_CDP_THROUGHPUT) map 
        {i => Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+3), cvtin_out_int8(i)(conf.NVDLA_CDP_ICVTO_BWPE-1)), cvtin_out_int8(i))}
    )

    val cvtin_out_int8_ext = cvtin_out_int8_ext_elem.asUInt

    val u_NV_NVDLA_CDP_DP_LUT_ctrl = Module(new NV_NVDLA_CDP_DP_LUT_ctrl)
    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.nvdla_core_clk := io.nvdla_core_clk

    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2itp_pd.valid := Mux(sqsum_bypass_en, u_NV_NVDLA_CDP_DP_cvtin.io.cvt2buf_pd.valid, u_NV_NVDLA_CDP_DP_sum.io.sum2itp_pd.valid)
    u_NV_NVDLA_CDP_DP_sum.io.sum2itp_pd.ready := u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2itp_pd.ready
    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2itp_pd.bits := Mux(sqsum_bypass_en, cvtin_out_int8_ext, u_NV_NVDLA_CDP_DP_sum.io.sum2itp_pd.bits)

    u_NV_NVDLA_CDP_DP_syncfifo.io.sum2sync_pd <> u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2sync_pd

    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut.le_function := io.reg2dp_lut_le_function
    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut.le_index_offset := io.reg2dp_lut_le_index_offset
    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut.le_index_select := io.reg2dp_lut_le_index_select
    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut.le_start_high := io.reg2dp_lut_le_start_high
    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut.le_start_low := io.reg2dp_lut_le_start_low
    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut.lo_index_select := io.reg2dp_lut_lo_index_select
    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut.lo_start_high := io.reg2dp_lut_lo_start_high
    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_lut.lo_start_low := io.reg2dp_lut_lo_start_low

    u_NV_NVDLA_CDP_DP_LUT_ctrl.io.reg2dp_sqsum_bypass := io.reg2dp_sqsum_bypass

    u_NV_NVDLA_CDP_DP_cvtin.io.cvt2buf_pd.ready := Mux(sqsum_bypass_en, u_NV_NVDLA_CDP_DP_LUT_ctrl.io.sum2itp_pd.ready, u_NV_NVDLA_CDP_DP_bufferin.io.cdp_rdma2dp_pd.ready)

    //===== LUT Instance========
    val u_NV_NVDLA_CDP_DP_lut = Module(new NV_NVDLA_CDP_DP_lut)
    u_NV_NVDLA_CDP_DP_lut.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CDP_DP_lut.io.nvdla_core_clk_orig := io.nvdla_core_clk_orig
    u_NV_NVDLA_CDP_DP_lut.io.dp2lut <> u_NV_NVDLA_CDP_DP_LUT_ctrl.io.dp2lut
    
    u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut.access_type := io.reg2dp_lut_access_type
    u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut.addr := io.reg2dp_lut_addr
    u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut.data := io.reg2dp_lut_data
    u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut.data_trigger := io.reg2dp_lut_data_trigger
    u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut.hybrid_priority := io.reg2dp_lut_hybrid_priority
    u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut.oflow_priority := io.reg2dp_lut_oflow_priority
    u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut.table_id := io.reg2dp_lut_table_id
    u_NV_NVDLA_CDP_DP_lut.io.reg2dp_lut.uflow_priority := io.reg2dp_lut_uflow_priority

    io.dp2reg_lut_data := u_NV_NVDLA_CDP_DP_lut.io.dp2reg_lut_data
    //===== interpolator Instance========
    val u_NV_NVDLA_CDP_DP_intp = Module(new NV_NVDLA_CDP_DP_intp)
    u_NV_NVDLA_CDP_DP_intp.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CDP_DP_intp.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_NV_NVDLA_CDP_DP_intp.io.sync2itp_pd <> u_NV_NVDLA_CDP_DP_syncfifo.io.sync2itp_pd
    u_NV_NVDLA_CDP_DP_intp.io.lut2intp <> u_NV_NVDLA_CDP_DP_lut.io.lut2intp

    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_end_high := io.reg2dp_lut_le_end_high
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_end_low := io.reg2dp_lut_le_end_low
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_function := io.reg2dp_lut_le_function
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_index_offset := io.reg2dp_lut_le_index_offset
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_slope_oflow_scale := io.reg2dp_lut_le_slope_oflow_scale
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_slope_oflow_shift := io.reg2dp_lut_le_slope_oflow_shift
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_slope_uflow_scale := io.reg2dp_lut_le_slope_uflow_scale
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_slope_uflow_shift := io.reg2dp_lut_le_slope_uflow_shift
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_start_high := io.reg2dp_lut_le_start_high
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.le_start_low := io.reg2dp_lut_le_start_low
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.lo_end_high := io.reg2dp_lut_lo_end_high
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.lo_end_low := io.reg2dp_lut_lo_end_low
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.lo_slope_oflow_scale := io.reg2dp_lut_lo_slope_oflow_scale
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.lo_slope_oflow_shift := io.reg2dp_lut_lo_slope_oflow_shift
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.lo_slope_uflow_scale := io.reg2dp_lut_lo_slope_uflow_scale
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.lo_slope_uflow_shift := io.reg2dp_lut_lo_slope_uflow_shift
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.lo_start_high := io.reg2dp_lut_lo_start_high
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_lut.lo_start_low := io.reg2dp_lut_lo_start_low
    u_NV_NVDLA_CDP_DP_intp.io.reg2dp_sqsum_bypass := io.reg2dp_sqsum_bypass
    io.dp2reg_perf <> u_NV_NVDLA_CDP_DP_intp.io.dp2reg_perf
    u_NV_NVDLA_CDP_DP_intp.io.dp2reg_done := io.dp2reg_done

    //===== DP multiple Instance========
    val u_NV_NVDLA_CDP_DP_mul = Module(new NV_NVDLA_CDP_DP_mul)
    u_NV_NVDLA_CDP_DP_mul.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CDP_DP_mul.io.intp2mul_pd <> u_NV_NVDLA_CDP_DP_intp.io.intp2mul_pd
    u_NV_NVDLA_CDP_DP_mul.io.sync2mul_pd <> u_NV_NVDLA_CDP_DP_syncfifo.io.sync2mul_pd
    
    u_NV_NVDLA_CDP_DP_mul.io.reg2dp_mul_bypass := io.reg2dp_mul_bypass

    //===== convertor_out Instance========
    val u_NV_NVDLA_CDP_DP_cvtout = Module(new NV_NVDLA_CDP_DP_cvtout)
    u_NV_NVDLA_CDP_DP_cvtout.io.nvdla_core_clk := io.nvdla_core_clk
    u_NV_NVDLA_CDP_DP_cvtout.io.mul2ocvt_pd <> u_NV_NVDLA_CDP_DP_mul.io.mul2ocvt_pd
    u_NV_NVDLA_CDP_DP_cvtout.io.sync2ocvt_pd <> u_NV_NVDLA_CDP_DP_syncfifo.io.sync2ocvt_pd
    io.cdp_dp2wdma_pd <> u_NV_NVDLA_CDP_DP_cvtout.io.cvtout_pd
    
    u_NV_NVDLA_CDP_DP_cvtout.io.reg2dp_datout <> io.reg2dp_datout

}}


object NV_NVDLA_CDP_dpDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_dp())
}
