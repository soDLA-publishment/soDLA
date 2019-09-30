package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_CORE_y(implicit val conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {

        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(Bool())
        //alu_in
        val ew_alu_in_data = Flipped(DecoupledIO(UInt(conf.EW_OP_DW.W)))
        // data_in
        val ew_data_in_pd = Flipped(DecoupledIO(UInt(conf.EW_IN_DW.W)))
        // mul_in
        val ew_mul_in_data = Flipped(DecoupledIO(UInt(conf.EW_OP_DW.W)))
        // data_out
        val ew_data_out_pd = DecoupledIO(UInt(conf.EW_OUT_DW.W))
        // reg2dp
        val reg2dp_ew_alu_algo = Input(UInt(2.W))
        val reg2dp_ew_alu_bypass = Input(Bool())
        val reg2dp_ew_alu_cvt_bypass = Input(Bool())
        val reg2dp_ew_alu_cvt_offset = Input(UInt(32.W))
        val reg2dp_ew_alu_cvt_scale = Input(UInt(16.W))
        val reg2dp_ew_alu_cvt_truncate = Input(UInt(6.W))
        val reg2dp_ew_alu_operand = Input(UInt(32.W))
        val reg2dp_ew_alu_src = Input(Bool())
        val reg2dp_ew_lut_bypass = Input(Bool())
        val reg2dp_ew_mul_bypass = Input(Bool())
        val reg2dp_ew_mul_cvt_bypass = Input(Bool())
        val reg2dp_ew_mul_cvt_offset = Input(UInt(32.W))
        val reg2dp_ew_mul_cvt_scale = Input(UInt(16.W))
        val reg2dp_ew_mul_cvt_truncate = Input(UInt(6.W))
        val reg2dp_ew_mul_operand = Input(UInt(32.W))
        val reg2dp_ew_mul_prelu = Input(Bool())
        val reg2dp_ew_mul_src = Input(Bool())
        val reg2dp_ew_truncate = Input(UInt(10.W))

        val reg2dp_nan_to_zero = Input(Bool())
        val reg2dp_perf_lut_en = Input(Bool())
        val reg2dp_proc_precision = Input(UInt(2.W))

        val op_en_load = Input(Bool())

        val sdp_lut = if(conf.NVDLA_SDP_LUT_ENABLE) Some(new Bundle{

            val reg2dp_lut_hybrid_priority = Input(Bool())
            val reg2dp_lut_int_access_type = Input(Bool())
            val reg2dp_lut_int_addr = Input(UInt(10.W))
            val reg2dp_lut_int_data = Input(UInt(16.W))
            val reg2dp_lut_int_data_wr = Input(Bool())
            val reg2dp_lut_int_table_id = Input(Bool())
            val reg2dp_lut_le_end = Input(UInt(32.W))
            val reg2dp_lut_le_function = Input(Bool())
            val reg2dp_lut_le_index_offset = Input(UInt(8.W))
            val reg2dp_lut_le_index_select = Input(UInt(8.W))
            val reg2dp_lut_le_slope_oflow_scale = Input(UInt(16.W))
            val reg2dp_lut_le_slope_oflow_shift = Input(UInt(5.W))
            val reg2dp_lut_le_slope_uflow_scale = Input(UInt(16.W))
            val reg2dp_lut_le_slope_uflow_shift = Input(UInt(5.W))
            val reg2dp_lut_le_start = Input(UInt(32.W))
            val reg2dp_lut_lo_end = Input(UInt(32.W))
            val reg2dp_lut_lo_index_select = Input(UInt(8.W))
            val reg2dp_lut_lo_slope_oflow_scale = Input(UInt(16.W))
            val reg2dp_lut_lo_slope_oflow_shift = Input(UInt(5.W))
            val reg2dp_lut_lo_slope_uflow_scale = Input(UInt(16.W))
            val reg2dp_lut_lo_slope_uflow_shift = Input(UInt(5.W))
            val reg2dp_lut_lo_start = Input(UInt(32.W))
            val reg2dp_lut_oflow_priority = Input(Bool())
            val reg2dp_lut_uflow_priority = Input(Bool())
            val dp2reg_lut_hybrid = Output(UInt(32.W))
            val dp2reg_lut_int_data = Output(UInt(16.W))
            val dp2reg_lut_le_hit = Output(UInt(32.W))
            val dp2reg_lut_lo_hit = Output(UInt(32.W))
            val dp2reg_lut_oflow = Output(UInt(32.W))
            val dp2reg_lut_uflow = Output(UInt(32.W))

        }) else None  
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

    val cfg_ew_lut_bypass = if(conf.NVDLA_SDP_LUT_ENABLE) RegInit(false.B) else RegInit(true.B)
    val cfg_lut_le_start = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(32.W))) else None
    val cfg_lut_le_end = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(32.W))) else None
    val cfg_lut_lo_start = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(32.W))) else None
    val cfg_lut_lo_end = if(conf.NVDLA_SDP_LUT_ENABLE) Some(RegInit("b0".asUInt(32.W))) else None

    when(io.op_en_load){

        if(conf.NVDLA_SDP_LUT_ENABLE){   
            cfg_ew_lut_bypass := io.reg2dp_ew_lut_bypass           
            cfg_lut_le_start.get := io.sdp_lut.get.reg2dp_lut_le_start         
            cfg_lut_le_end.get := io.sdp_lut.get.reg2dp_lut_le_end
            cfg_lut_lo_start.get := io.sdp_lut.get.reg2dp_lut_lo_start
            cfg_lut_lo_end.get := io.sdp_lut.get.reg2dp_lut_lo_end  
        }  
        else{
            cfg_ew_lut_bypass := true.B
        }  
    }
    //===========================================
    // y input pipe
    //===========================================

    //=================================================
    val u_alu_cvt = Module(new NV_NVDLA_SDP_HLS_Y_cvt_top)
    u_alu_cvt.io.nvdla_core_clk := io.nvdla_core_clk
    u_alu_cvt.io.cvt_data_in <> io.ew_alu_in_data
    u_alu_cvt.io.cfg_cvt_bypass := RegEnable(io.reg2dp_ew_alu_cvt_bypass, false.B, io.op_en_load)
    u_alu_cvt.io.cfg_cvt_offset := RegEnable(io.reg2dp_ew_alu_cvt_offset, "b0".asUInt(32.W), io.op_en_load)
    u_alu_cvt.io.cfg_cvt_scale := RegEnable(io.reg2dp_ew_alu_cvt_scale, "b0".asUInt(16.W), io.op_en_load)
    u_alu_cvt.io.cfg_cvt_truncate := RegEnable(io.reg2dp_ew_alu_cvt_bypass, "b0".asUInt(6.W), io.op_en_load)

    val u_mul_cvt = Module(new NV_NVDLA_SDP_HLS_Y_cvt_top)
    u_mul_cvt.io.nvdla_core_clk := io.nvdla_core_clk
    u_mul_cvt.io.cvt_data_in <> io.ew_mul_in_data
    u_mul_cvt.io.cfg_cvt_bypass := RegEnable(io.reg2dp_ew_mul_cvt_bypass, false.B, io.op_en_load)
    u_mul_cvt.io.cfg_cvt_offset := RegEnable(io.reg2dp_ew_mul_cvt_offset, "b0".asUInt(32.W), io.op_en_load)
    u_mul_cvt.io.cfg_cvt_scale := RegEnable(io.reg2dp_ew_mul_cvt_scale, "b0".asUInt(16.W), io.op_en_load)
    u_mul_cvt.io.cfg_cvt_truncate := RegEnable(io.reg2dp_ew_mul_cvt_bypass, "b0".asUInt(6.W), io.op_en_load)

    val u_core = Module(new NV_NVDLA_SDP_HLS_Y_int_core)
    u_core.io.nvdla_core_clk := io.nvdla_core_clk
    u_core.io.chn_alu_op <> u_alu_cvt.io.cvt_data_out
    u_core.io.chn_data_in <> io.ew_data_in_pd
    u_core.io.chn_mul_op <> u_mul_cvt.io.cvt_data_out
    u_core.io.cfg_alu_algo := RegEnable(io.reg2dp_ew_alu_algo, "b0".asUInt(2.W), io.op_en_load)
    u_core.io.cfg_alu_bypass := RegEnable(io.reg2dp_ew_alu_bypass, false.B, io.op_en_load)
    u_core.io.cfg_alu_op := RegEnable(io.reg2dp_ew_alu_operand, "b0".asUInt(32.W), io.op_en_load)
    u_core.io.cfg_alu_src := RegEnable(io.reg2dp_ew_alu_src, false.B, io.op_en_load)
    u_core.io.cfg_mul_bypass := RegEnable(io.reg2dp_ew_mul_bypass, false.B, io.op_en_load)
    u_core.io.cfg_mul_op := RegEnable(io.reg2dp_ew_mul_operand, "b0".asUInt(32.W), io.op_en_load)
    u_core.io.cfg_mul_prelu := RegEnable(io.reg2dp_ew_mul_prelu, false.B, io.op_en_load)
    u_core.io.cfg_mul_src := RegEnable(io.reg2dp_ew_mul_src, false.B, io.op_en_load)
    u_core.io.cfg_mul_truncate := RegEnable(io.reg2dp_ew_truncate, "b0".asUInt(10.W), io.op_en_load)

    val idx_in_pvld = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Mux(cfg_ew_lut_bypass, false.B, u_core.io.chn_data_out.valid)) else None
    val idx_in_prdy = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Wire(Bool())) else None
    val idx_in_pd = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Fill(conf.EW_CORE_OUT_DW, idx_in_pvld.get)& u_core.io.chn_data_out.bits) else None
    

    if(conf.NVDLA_SDP_LUT_ENABLE){
        u_core.io.chn_data_out.ready := Mux(cfg_ew_lut_bypass, io.ew_data_out_pd.ready, idx_in_prdy.get) 
    }
    else{
        u_core.io.chn_data_out.ready := io.ew_data_out_pd.ready
    }

    val u_idx = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Module(new NV_NVDLA_SDP_HLS_Y_idx_top)) else None
    val u_lut = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Module(new NV_NVDLA_SDP_CORE_Y_lut)) else None
    val u_inp = if(conf.NVDLA_SDP_LUT_ENABLE) Some(Module(new NV_NVDLA_SDP_HLS_Y_inp_top)) else None

    if(conf.NVDLA_SDP_LUT_ENABLE){
        //u_idx
        u_idx.get.io.nvdla_core_clk := io.nvdla_core_clk

        u_idx.get.io.cfg_lut_hybrid_priority := RegEnable(io.sdp_lut.get.reg2dp_lut_hybrid_priority, false.B, io.op_en_load)
        u_idx.get.io.cfg_lut_le_function := RegEnable(io.sdp_lut.get.reg2dp_lut_le_function, false.B, io.op_en_load)
        u_idx.get.io.cfg_lut_le_index_offset := RegEnable(io.sdp_lut.get.reg2dp_lut_le_index_offset, "b0".asUInt(8.W), io.op_en_load)
        u_idx.get.io.cfg_lut_le_index_select := RegEnable(io.sdp_lut.get.reg2dp_lut_le_index_select, "b0".asUInt(8.W), io.op_en_load)
        u_idx.get.io.cfg_lut_lo_index_select := RegEnable(io.sdp_lut.get.reg2dp_lut_lo_index_select, "b0".asUInt(8.W), io.op_en_load)
        u_idx.get.io.cfg_lut_oflow_priority := RegEnable(io.sdp_lut.get.reg2dp_lut_oflow_priority, false.B, io.op_en_load)
        u_idx.get.io.cfg_lut_uflow_priority := RegEnable(io.sdp_lut.get.reg2dp_lut_uflow_priority, false.B, io.op_en_load)
        u_idx.get.io.cfg_lut_le_start := cfg_lut_le_start.get
        u_idx.get.io.cfg_lut_lo_start := cfg_lut_lo_start.get

        u_idx.get.io.chn_lut_in_pd.valid := idx_in_pvld.get
        idx_in_prdy.get := u_idx.get.io.chn_lut_in_pd.ready
        u_idx.get.io.chn_lut_in_pd.bits := idx_in_pd.get

        //u_lut
        u_lut.get.io.nvdla_core_clk := io.nvdla_core_clk
        u_lut.get.io.idx2lut_pd <> u_idx.get.io.chn_lut_out_pd

        u_lut.get.io.reg2dp_lut_int_access_type := io.sdp_lut.get.reg2dp_lut_int_access_type
        u_lut.get.io.reg2dp_lut_int_addr := io.sdp_lut.get.reg2dp_lut_int_addr
        u_lut.get.io.reg2dp_lut_int_data := io.sdp_lut.get.reg2dp_lut_int_data
        u_lut.get.io.reg2dp_lut_int_data_wr := io.sdp_lut.get.reg2dp_lut_int_data_wr
        u_lut.get.io.reg2dp_lut_int_table_id := io.sdp_lut.get.reg2dp_lut_int_table_id
        u_lut.get.io.reg2dp_lut_le_function := io.sdp_lut.get.reg2dp_lut_le_function
        u_lut.get.io.reg2dp_lut_le_index_offset := io.sdp_lut.get.reg2dp_lut_le_index_offset
        u_lut.get.io.reg2dp_lut_le_slope_oflow_scale := RegEnable(io.sdp_lut.get.reg2dp_lut_le_slope_oflow_scale, "b0".asUInt(16.W), io.op_en_load)
        u_lut.get.io.reg2dp_lut_le_slope_oflow_shift := RegEnable(io.sdp_lut.get.reg2dp_lut_le_slope_oflow_shift, "b0".asUInt(5.W), io.op_en_load)
        u_lut.get.io.reg2dp_lut_le_slope_uflow_scale := RegEnable(io.sdp_lut.get.reg2dp_lut_le_slope_uflow_scale, "b0".asUInt(16.W), io.op_en_load)
        u_lut.get.io.reg2dp_lut_le_slope_uflow_shift := RegEnable(io.sdp_lut.get.reg2dp_lut_le_slope_uflow_shift, "b0".asUInt(5.W), io.op_en_load)
        u_lut.get.io.reg2dp_lut_lo_slope_oflow_scale := RegEnable(io.sdp_lut.get.reg2dp_lut_lo_slope_oflow_scale, "b0".asUInt(16.W), io.op_en_load)
        u_lut.get.io.reg2dp_lut_lo_slope_oflow_shift := RegEnable(io.sdp_lut.get.reg2dp_lut_lo_slope_oflow_shift, "b0".asUInt(5.W), io.op_en_load)
        u_lut.get.io.reg2dp_lut_lo_slope_uflow_scale := RegEnable(io.sdp_lut.get.reg2dp_lut_lo_slope_uflow_scale, "b0".asUInt(16.W), io.op_en_load)
        u_lut.get.io.reg2dp_lut_lo_slope_uflow_shift := RegEnable(io.sdp_lut.get.reg2dp_lut_lo_slope_uflow_shift, "b0".asUInt(5.W), io.op_en_load)
        
        u_lut.get.io.reg2dp_perf_lut_en := io.reg2dp_perf_lut_en
        u_lut.get.io.reg2dp_proc_precision := io.reg2dp_proc_precision

        u_lut.get.io.reg2dp_lut_le_start := cfg_lut_le_start.get
        u_lut.get.io.reg2dp_lut_le_end := cfg_lut_le_end.get
        u_lut.get.io.reg2dp_lut_lo_start := cfg_lut_lo_start.get
        u_lut.get.io.reg2dp_lut_lo_end := cfg_lut_lo_end.get

        io.sdp_lut.get.dp2reg_lut_hybrid := u_lut.get.io.dp2reg_lut_hybrid
        io.sdp_lut.get.dp2reg_lut_int_data := u_lut.get.io.dp2reg_lut_int_data
        io.sdp_lut.get.dp2reg_lut_le_hit := u_lut.get.io.dp2reg_lut_le_hit
        io.sdp_lut.get.dp2reg_lut_lo_hit := u_lut.get.io.dp2reg_lut_lo_hit
        io.sdp_lut.get.dp2reg_lut_oflow := u_lut.get.io.dp2reg_lut_oflow
        io.sdp_lut.get.dp2reg_lut_uflow := u_lut.get.io.dp2reg_lut_uflow
        u_lut.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
        u_lut.get.io.op_en_load := io.op_en_load

        //u_inp
        u_inp.get.io.nvdla_core_clk := io.nvdla_core_clk
        u_inp.get.io.chn_inp_in_pd <> u_lut.get.io.lut2inp_pd 

        io.ew_data_out_pd.valid := Mux(cfg_ew_lut_bypass, u_core.io.chn_data_out.valid, u_inp.get.io.chn_inp_out_pd.valid)
        io.ew_data_out_pd.bits := Mux(cfg_ew_lut_bypass, u_core.io.chn_data_out.bits, u_inp.get.io.chn_inp_out_pd.bits)
        u_inp.get.io.chn_inp_out_pd.ready := Mux(cfg_ew_lut_bypass, false.B, io.ew_data_out_pd.ready)
        
    }
    else{
        io.ew_data_out_pd.valid := u_core.io.chn_data_out.valid
        io.ew_data_out_pd.bits := u_core.io.chn_data_out.bits
    }


}}


object NV_NVDLA_SDP_CORE_yDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_CORE_y)
}


