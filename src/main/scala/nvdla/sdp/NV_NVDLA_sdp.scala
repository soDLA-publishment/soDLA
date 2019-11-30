package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_sdp(implicit val conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {

        val nvdla_clock = Flipped(new nvdla_clock_if)
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val cacc2sdp_pd = Flipped(DecoupledIO(UInt((conf.DP_IN_DW+2).W)))
        val sdp2pdp_pd = DecoupledIO(UInt(conf.DP_OUT_DW.W))
        val sdp2glb_done_intr_pd = Output(UInt(2.W))

        val csb2sdp_rdma = new csb2dp_if
        val csb2sdp = new csb2dp_if

        val sdp_b2mcif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val sdp_b2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
        val mcif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None 

        val sdp_b2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val sdp_b2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
        val cvif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None 

        val sdp_n2mcif_rd_req_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val sdp_n2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
        val mcif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None 

        val sdp_n2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val sdp_n2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
        val cvif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None 

        val sdp_e2mcif_rd_req_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val sdp_e2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
        val mcif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None 

        val sdp_e2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val sdp_e2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
        val cvif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None 

        val sdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
        val sdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())
        val mcif2sdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))

        val sdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
        val sdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2sdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None 

        val sdp2mcif_wr_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))
        val mcif2sdp_wr_rsp_complete = Input(Bool())

        val sdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))) else None
        val cvif2sdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None

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
withReset(!io.nvdla_core_rstn){  
    //=======================================
    //DMA
    //--------------------------------------- 
    val u_rdma = Module(new NV_NVDLA_SDP_rdma)
    val u_wdma = Module(new NV_NVDLA_SDP_wdma)
    //========================================
    //SDP core instance
    //----------------------------------------
    val u_core = Module(new NV_NVDLA_SDP_core)
    //=======================================
    //CONFIG instance
    //rdma has seperate config register, while wdma share with core
    //---------------------------------------
    val u_reg = Module(new NV_NVDLA_SDP_reg)
    //glb
    u_rdma.io.nvdla_clock <> io.nvdla_clock
    u_rdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_wdma.io.nvdla_clock <> io.nvdla_clock
    u_wdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_core.io.nvdla_clock <> io.nvdla_clock
    u_core.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_reg.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk
    io.sdp2glb_done_intr_pd := u_wdma.io.sdp2glb_done_intr_pd

    //mcif
    io.sdp2mcif_rd_req_pd <> u_rdma.io.sdp2mcif_rd_req_pd
    io.sdp2mcif_rd_cdt_lat_fifo_pop := u_rdma.io.sdp2mcif_rd_cdt_lat_fifo_pop
    u_rdma.io.mcif2sdp_rd_rsp_pd <> io.mcif2sdp_rd_rsp_pd

    io.sdp2mcif_wr_req_pd <> u_wdma.io.sdp2mcif_wr_req_pd
    u_wdma.io.mcif2sdp_wr_rsp_complete := io.mcif2sdp_wr_rsp_complete
    u_core.io.sdp_mrdma2cmux_pd <> u_rdma.io.sdp_mrdma2cmux_pd

    //csb2sdp
    u_rdma.io.csb2sdp_rdma <> io.csb2sdp_rdma
    u_reg.io.csb2sdp <> io.csb2sdp
    val s_field = u_reg.io.reg2dp_single_field
    val d_field = u_reg.io.reg2dp_dual_field

    //sdp2cacc
    u_core.io.cacc2sdp_pd <> io.cacc2sdp_pd

    //sdp_dp2wdma
    u_wdma.io.sdp_dp2wdma_pd <> u_core.io.sdp_dp2wdma_pd

    //sdp2pdp
    io.sdp2pdp_pd <> u_core.io.sdp2pdp_pd
    u_core.io.reg2dp_ncore_slcg_op_en := u_reg.io.reg2dp_ncore_slcg_op_en
    u_core.io.reg2dp_bcore_slcg_op_en := u_reg.io.reg2dp_bcore_slcg_op_en
    u_core.io.reg2dp_ecore_slcg_op_en := u_reg.io.reg2dp_ecore_slcg_op_en


    if(conf.NVDLA_SDP_BS_ENABLE){
        //sdp_b2mcif
        io.sdp_b2mcif_rd_req_pd.get <> u_rdma.io.sdp_b2mcif_rd_req_pd.get
        io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get
        u_rdma.io.mcif2sdp_b_rd_rsp_pd.get <> io.mcif2sdp_b_rd_rsp_pd.get
        //sdp_brdma2dp_alu
        u_core.io.sdp_brdma2dp_alu_pd.get <> u_rdma.io.sdp_brdma2dp_alu_pd.get
        //sdp_brdma2dp_mul
        u_core.io.sdp_brdma2dp_mul_pd.get <> u_rdma.io.sdp_brdma2dp_mul_pd.get
        //reg2dp_bs
        u_core.io.reg2dp_bs_bypass.get := d_field.bs_bypass
        u_core.io.reg2dp_bs_relu_bypass.get := d_field.bs_relu_bypass

        u_core.io.reg2dp_bs_alu.get.bypass := d_field.bs_alu_bypass
        u_core.io.reg2dp_bs_alu.get.algo := d_field.bs_alu_algo
        u_core.io.reg2dp_bs_alu.get.op := d_field.bs_alu_operand
        u_core.io.reg2dp_bs_alu.get.shift_value := d_field.bs_alu_shift_value
        u_core.io.reg2dp_bs_alu.get.src := d_field.bs_alu_src
        
        u_core.io.reg2dp_bs_mul.get.bypass := d_field.bs_mul_bypass
        u_core.io.reg2dp_bs_mul.get.op := d_field.bs_mul_operand
        u_core.io.reg2dp_bs_mul.get.prelu := d_field.bs_mul_prelu
        u_core.io.reg2dp_bs_mul.get.shift_value := d_field.bs_mul_shift_value
        u_core.io.reg2dp_bs_mul.get.src := d_field.bs_mul_src

        

    }
    if(conf.NVDLA_SDP_BN_ENABLE){
        //sdp_n2mcif
        io.sdp_n2mcif_rd_req_pd.get <> u_rdma.io.sdp_n2mcif_rd_req_pd.get
        io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get
        u_rdma.io.mcif2sdp_n_rd_rsp_pd.get <> io.mcif2sdp_n_rd_rsp_pd.get
        //sdp_nrdma2dp_alu
        u_core.io.sdp_nrdma2dp_alu_pd.get <> u_rdma.io.sdp_nrdma2dp_alu_pd.get
        //sdp_nrdma2dp_mul
        u_core.io.sdp_nrdma2dp_mul_pd.get <> u_rdma.io.sdp_nrdma2dp_mul_pd.get
        //reg2dp_bn
        u_core.io.reg2dp_bn_bypass.get := d_field.bs_bypass
        u_core.io.reg2dp_bn_relu_bypass.get := d_field.bs_relu_bypass

        u_core.io.reg2dp_bn_alu.get.bypass := d_field.bs_alu_bypass
        u_core.io.reg2dp_bn_alu.get.algo := d_field.bs_alu_algo
        u_core.io.reg2dp_bn_alu.get.op := d_field.bs_alu_operand
        u_core.io.reg2dp_bn_alu.get.shift_value := d_field.bs_alu_shift_value
        u_core.io.reg2dp_bn_alu.get.src := d_field.bs_alu_src
        
        u_core.io.reg2dp_bn_mul.get.bypass := d_field.bs_mul_bypass
        u_core.io.reg2dp_bn_mul.get.op := d_field.bs_mul_operand
        u_core.io.reg2dp_bn_mul.get.prelu := d_field.bs_mul_prelu
        u_core.io.reg2dp_bn_mul.get.shift_value := d_field.bs_mul_shift_value
        u_core.io.reg2dp_bn_mul.get.src := d_field.bs_mul_src

    }
    if(conf.NVDLA_SDP_EW_ENABLE){
        //sdp_e2mcif
        io.sdp_e2mcif_rd_req_pd.get <> u_rdma.io.sdp_e2mcif_rd_req_pd.get
        io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get
        u_rdma.io.mcif2sdp_e_rd_rsp_pd.get <> io.mcif2sdp_e_rd_rsp_pd.get
        //sdp_erdma2dp_alu
        u_core.io.sdp_erdma2dp_alu_pd.get <> u_rdma.io.sdp_erdma2dp_alu_pd.get
        //sdp_erdma2dp_mul
        u_core.io.sdp_erdma2dp_mul_pd.get <> u_rdma.io.sdp_erdma2dp_mul_pd.get

        u_wdma.io.reg2dp_ew_bypass := d_field.ew_bypass
        u_wdma.io.reg2dp_ew_alu_algo := d_field.ew_alu_algo
        u_wdma.io.reg2dp_ew_alu_bypass := d_field.ew_alu_bypass
        
        u_core.io.reg2dp_ew_bypass.get := d_field.ew_bypass
        u_core.io.reg2dp_ew.get.alu.algo := d_field.ew_alu_algo
        u_core.io.reg2dp_ew.get.alu.bypass := d_field.ew_alu_bypass
        u_core.io.reg2dp_ew.get.alu.src := d_field.ew_alu_src
        u_core.io.reg2dp_ew.get.alu.op := d_field.ew_alu_operand
        u_core.io.reg2dp_ew.get.alu_cvt.bypass := d_field.ew_alu_cvt_bypass
        u_core.io.reg2dp_ew.get.alu_cvt.offset := d_field.ew_alu_cvt_offset
        u_core.io.reg2dp_ew.get.alu_cvt.scale := d_field.ew_alu_cvt_scale
        u_core.io.reg2dp_ew.get.alu_cvt.truncate := d_field.ew_alu_cvt_truncate    
        u_core.io.reg2dp_ew.get.lut_bypass := d_field.ew_lut_bypass
        u_core.io.reg2dp_ew.get.mul.bypass := d_field.ew_mul_bypass
        u_core.io.reg2dp_ew.get.mul_cvt.bypass := d_field.ew_mul_cvt_bypass
        u_core.io.reg2dp_ew.get.mul_cvt.offset := d_field.ew_mul_cvt_offset
        u_core.io.reg2dp_ew.get.mul_cvt.scale := d_field.ew_mul_cvt_scale
        u_core.io.reg2dp_ew.get.mul_cvt.truncate := d_field.ew_mul_cvt_truncate
        u_core.io.reg2dp_ew.get.mul.op := d_field.ew_mul_operand
        u_core.io.reg2dp_ew.get.mul.prelu := d_field.ew_mul_prelu
        u_core.io.reg2dp_ew.get.mul.src := d_field.ew_mul_src
        u_core.io.reg2dp_ew.get.mul.truncate := d_field.ew_truncate

        if(conf.NVDLA_SDP_LUT_ENABLE){
            
            u_core.io.reg2dp_lut.get.int_access_type := u_reg.io.reg2dp_lut_int_access_type
            u_core.io.reg2dp_lut.get.int_addr := u_reg.io.reg2dp_lut_int_addr
            u_core.io.reg2dp_lut.get.int_data := u_reg.io.reg2dp_lut_int_data
            u_core.io.reg2dp_lut.get.int_data_wr := u_reg.io.reg2dp_lut_int_data_wr
            u_core.io.reg2dp_lut.get.int_table_id := u_reg.io.reg2dp_lut_int_table_id
            u_core.io.reg2dp_lut.get.le_end := s_field.lut_le_end
            u_core.io.reg2dp_lut.get.le_slope_oflow_scale := s_field.lut_le_slope_oflow_scale
            u_core.io.reg2dp_lut.get.le_slope_oflow_shift := s_field.lut_le_slope_oflow_shift
            u_core.io.reg2dp_lut.get.le_slope_uflow_scale := s_field.lut_le_slope_uflow_scale
            u_core.io.reg2dp_lut.get.le_slope_uflow_shift := s_field.lut_le_slope_uflow_shift
            u_core.io.reg2dp_lut.get.lo_end := s_field.lut_lo_end
            u_core.io.reg2dp_lut.get.lo_slope_oflow_scale := s_field.lut_lo_slope_oflow_scale
            u_core.io.reg2dp_lut.get.lo_slope_oflow_shift := s_field.lut_lo_slope_oflow_shift
            u_core.io.reg2dp_lut.get.lo_slope_uflow_scale := s_field.lut_lo_slope_uflow_scale
            u_core.io.reg2dp_lut.get.lo_slope_uflow_shift := s_field.lut_lo_slope_uflow_shift
            u_core.io.reg2dp_lut.get.le_start := s_field.lut_le_start
            u_core.io.reg2dp_lut.get.lo_start := s_field.lut_lo_start
            u_core.io.reg2dp_lut.get.le_function := s_field.lut_le_function
            u_core.io.reg2dp_lut.get.le_index_offset := s_field.lut_le_index_offset
            u_core.io.reg2dp_lut_slcg_en.get := u_reg.io.reg2dp_lut_slcg_en
            
            u_core.io.reg2dp_idx.get.hybrid_priority := s_field.lut_hybrid_priority
            u_core.io.reg2dp_idx.get.le_index_offset := s_field.lut_le_index_offset
            u_core.io.reg2dp_idx.get.le_function := s_field.lut_le_function
            u_core.io.reg2dp_idx.get.le_index_select := s_field.lut_le_index_select
            u_core.io.reg2dp_idx.get.le_start := s_field.lut_le_start
            u_core.io.reg2dp_idx.get.lo_start := s_field.lut_lo_start
            u_core.io.reg2dp_idx.get.lo_index_select := s_field.lut_lo_index_select
            u_core.io.reg2dp_idx.get.oflow_priority := s_field.lut_oflow_priority
            u_core.io.reg2dp_idx.get.uflow_priority := s_field.lut_uflow_priority

            u_reg.io.dp2reg_lut_hybrid := u_core.io.dp2reg_lut.get.hybrid
            u_reg.io.dp2reg_lut_int_data := u_core.io.dp2reg_lut.get.int_data 
            u_reg.io.dp2reg_lut_le_hit := u_core.io.dp2reg_lut.get.le_hit 
            u_reg.io.dp2reg_lut_lo_hit := u_core.io.dp2reg_lut.get.lo_hit 
            u_reg.io.dp2reg_lut_oflow := u_core.io.dp2reg_lut.get.oflow 
            u_reg.io.dp2reg_lut_uflow := u_core.io.dp2reg_lut.get.uflow 
        }
        else{
            u_reg.io.dp2reg_lut_hybrid := 0.U
            u_reg.io.dp2reg_lut_int_data := 0.U
            u_reg.io.dp2reg_lut_le_hit := 0.U
            u_reg.io.dp2reg_lut_lo_hit := 0.U
            u_reg.io.dp2reg_lut_oflow := 0.U
            u_reg.io.dp2reg_lut_uflow := 0.U
        }
    }
    else{
        u_wdma.io.reg2dp_ew_alu_algo := "b0".asUInt(2.W)
        u_wdma.io.reg2dp_ew_alu_bypass := "b1".asUInt(1.W)
        u_wdma.io.reg2dp_ew_bypass := "b1".asUInt(1.W)

    }
    u_wdma.io.reg2dp_op_en := u_reg.io.reg2dp_op_en
    u_wdma.io.reg2dp_wdma_slcg_op_en := u_reg.io.reg2dp_wdma_slcg_op_en
    u_wdma.io.reg2dp_output_dst := d_field.output_dst
    u_wdma.io.reg2dp_batch_number := d_field.batch_number
    u_wdma.io.reg2dp_winograd := d_field.winograd
    u_wdma.io.reg2dp_channel := d_field.channel
    u_wdma.io.reg2dp_height := d_field.height
    u_wdma.io.reg2dp_width := d_field.width_a
    u_wdma.io.reg2dp_proc_precision := d_field.proc_precision
    u_wdma.io.reg2dp_out_precision := d_field.out_precision
    u_wdma.io.reg2dp_dst_ram_type := d_field.dst_ram_type
    u_wdma.io.reg2dp_dst_base_addr_high := d_field.dst_base_addr_high 
    u_wdma.io.reg2dp_dst_base_addr_low := d_field.dst_base_addr_low
    u_wdma.io.reg2dp_dst_batch_stride  := d_field.dst_batch_stride 
    u_wdma.io.reg2dp_dst_line_stride := d_field.dst_line_stride
    u_wdma.io.reg2dp_dst_surface_stride := d_field.dst_surface_stride
    u_wdma.io.reg2dp_interrupt_ptr := u_reg.io.reg2dp_interrupt_ptr 
    u_wdma.io.reg2dp_perf_dma_en := d_field.perf_dma_en

    u_core.io.reg2dp_cvt.offset := d_field.cvt_offset
    u_core.io.reg2dp_cvt.scale := d_field.cvt_scale
    u_core.io.reg2dp_cvt.truncate := d_field.cvt_shift
    u_core.io.reg2dp_op_en := u_reg.io.reg2dp_op_en
    u_core.io.reg2dp_flying_mode := d_field.flying_mode
    u_core.io.reg2dp_output_dst := d_field.output_dst
    u_core.io.reg2dp_nan_to_zero := d_field.nan_to_zero
    u_core.io.reg2dp_proc_precision := d_field.proc_precision
    u_core.io.reg2dp_out_precision := d_field.out_precision
    u_core.io.reg2dp_perf_lut_en := d_field.perf_lut_en
    u_core.io.reg2dp_perf_sat_en := d_field.perf_sat_en
    u_core.io.dp2reg_done := u_wdma.io.dp2reg_done
    u_reg.io.dp2reg_out_saturation := u_core.io.dp2reg_out_saturation

    u_reg.io.dp2reg_done := u_wdma.io.dp2reg_done     
    u_reg.io.dp2reg_status_inf_input_num := 0.U
    u_reg.io.dp2reg_status_nan_input_num := 0.U
    u_reg.io.dp2reg_status_nan_output_num := u_wdma.io.dp2reg_status_nan_output_num
    u_reg.io.dp2reg_status_unequal := u_wdma.io.dp2reg_status_unequal
    u_reg.io.dp2reg_wdma_stall := u_wdma.io.dp2reg_wdma_stall


    //cvif
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        //sdp2cvif
        io.sdp2cvif_rd_req_pd.get <> u_rdma.io.sdp2cvif_rd_req_pd.get
        io.sdp2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
        u_rdma.io.cvif2sdp_rd_rsp_pd.get <> io.cvif2sdp_rd_rsp_pd.get

        io.sdp2cvif_wr_req_pd.get <> u_wdma.io.sdp2cvif_wr_req_pd.get
        u_wdma.io.cvif2sdp_wr_rsp_complete.get := io.cvif2sdp_wr_rsp_complete.get
        
        if(conf.NVDLA_SDP_BS_ENABLE){
            //sdp_bcvif
            io.sdp_b2cvif_rd_req_pd.get <> u_rdma.io.sdp_b2cvif_rd_req_pd.get
            io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get
            u_rdma.io.cvif2sdp_b_rd_rsp_pd.get <> io.cvif2sdp_b_rd_rsp_pd.get
        }
        if(conf.NVDLA_SDP_BN_ENABLE){
            //sdp_n2cvif
            io.sdp_n2cvif_rd_req_pd.get <> u_rdma.io.sdp_n2cvif_rd_req_pd.get
            io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get
            u_rdma.io.cvif2sdp_n_rd_rsp_pd.get <> io.cvif2sdp_n_rd_rsp_pd.get
        }
        if(conf.NVDLA_SDP_EW_ENABLE){
            //sdp_e2cvif
            io.sdp_e2cvif_rd_req_pd.get <> u_rdma.io.sdp_e2cvif_rd_req_pd.get
            io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get
            u_rdma.io.cvif2sdp_e_rd_rsp_pd.get <> io.cvif2sdp_e_rd_rsp_pd.get
        }
    }
    
}}


object NV_NVDLA_sdpDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_sdp)
}





