package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_cdp(implicit val conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {

        val nvdla_clock = Flipped(new nvdla_clock_if)
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val csb2cdp_rdma = new csb2dp_if
        val csb2cdp = new csb2dp_if

        val cdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt((conf.NVDLA_PDP_MEM_RD_REQ).W))) else None
        val cdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_WR_REQ.W))) else None
        val cvif2cdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_RSP.W)))) else None
        val cdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2cdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None

        val cdp2mcif_rd_req_pd = DecoupledIO(UInt((conf.NVDLA_PDP_MEM_RD_REQ).W))
        val cdp2mcif_wr_req_pd = DecoupledIO(UInt(conf.NVDLA_PDP_MEM_WR_REQ.W))
        val mcif2cdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_RSP.W)))
        val cdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())
        val mcif2cdp_wr_rsp_complete = Input(Bool())

        val cdp2glb_done_intr_pd = Output(UInt(2.W))        

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

    val u_slcg_core = Array.fill(2){Module(new NV_NVDLA_slcg(1, false))}
    val u_reg = Module(new NV_NVDLA_CDP_reg)
    val u_rdma = Module(new NV_NVDLA_CDP_rdma)
    val u_nan = Module(new NV_NVDLA_CDP_DP_nan)
    val u_wdma = Module(new NV_NVDLA_CDP_wdma)
    val u_dp = Module(new NV_NVDLA_CDP_dp)
    //=======================================
    //        SLCG gen unit
    //--------------------------------------
    //0 for dp, 1 for wdma
    for(i <- 0 to 1){
        u_slcg_core(i).io.nvdla_clock <> io.nvdla_clock
    }
    u_slcg_core(0).io.slcg_en(0) := u_reg.io.slcg_op_en(0)
    u_slcg_core(1).io.slcg_en(0) := u_reg.io.slcg_op_en(1)
    val nvdla_op_gated_clk_core = u_slcg_core(0).io.nvdla_core_gated_clk
    val nvdla_op_gated_clk_wdma = u_slcg_core(1).io.nvdla_core_gated_clk

    //=======================================
    //CONFIG instance
    //rdma has seperate config register, while wdma share with core
    //---------------------------------------
    //general clock
    u_reg.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk 
    //csb
    io.csb2cdp <> u_reg.io.csb2cdp
    //reg2dp
    u_reg.io.dp2reg_inf_input_num := 0.U
    u_reg.io.dp2reg_d0_out_saturation := 0.U
    u_reg.io.dp2reg_d1_out_saturation := 0.U
    u_reg.io.dp2reg_nan_input_num := 0.U
    u_reg.io.dp2reg_nan_output_num := 0.U
    u_reg.io.dp2reg_perf <> u_dp.io.dp2reg_perf
    u_reg.io.dp2reg_done := u_wdma.io.dp2reg_done
    u_reg.io.dp2reg_lut_data := u_dp.io.dp2reg_lut_data
    u_reg.io.dp2reg_d0_perf_write_stall := u_wdma.io.dp2reg_d0_perf_write_stall
    u_reg.io.dp2reg_d1_perf_write_stall := u_wdma.io.dp2reg_d1_perf_write_stall

    val field_single = u_reg.io.reg2dp_field_single
    val field_dual = u_reg.io.reg2dp_field_dual
    
    //=======================================
    //RDMA
    //--------------------------------------- 
    u_rdma.io.nvdla_clock <> io.nvdla_clock
    u_rdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    //csb2pdp_rdma
    io.csb2cdp_rdma <> u_rdma.io.csb2cdp_rdma
    //request_response
    io.cdp2mcif_rd_req_pd <> u_rdma.io.cdp2mcif_rd_req_pd
    u_rdma.io.mcif2cdp_rd_rsp_pd <> io.mcif2cdp_rd_rsp_pd
    io.cdp2mcif_rd_cdt_lat_fifo_pop := u_rdma.io.cdp2mcif_rd_cdt_lat_fifo_pop

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdp2cvif_rd_req_pd.get <> u_rdma.io.cdp2cvif_rd_req_pd.get
        u_rdma.io.cvif2cdp_rd_rsp_pd.get <> io.cvif2cdp_rd_rsp_pd.get
        io.cdp2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.cdp2cvif_rd_cdt_lat_fifo_pop.get
    }
    
    //=======================================
    //NaN control of RDMA output data
    //---------------------------------------
    u_nan.io.nvdla_core_clk := nvdla_op_gated_clk_core
    //rdma2dp
    u_nan.io.cdp_rdma2dp_pd <> u_rdma.io.cdp_rdma2dp_pd
    //config  
    u_nan.io.reg2dp_op_en := u_reg.io.reg2dp_op_en

    //=======================================
    //WDMA
    //---------------------------------------
    u_wdma.io.nvdla_core_clk_orig := io.nvdla_clock.nvdla_core_clk
    u_wdma.io.nvdla_core_clk := nvdla_op_gated_clk_wdma
    u_wdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_wdma.io.cdp_dp2wdma_pd <> u_dp.io.cdp_dp2wdma_pd
    // cdp2mcif_wr
    io.cdp2mcif_wr_req_pd <> u_wdma.io.cdp2mcif_wr_req_pd
    u_wdma.io.mcif2cdp_wr_rsp_complete := io.mcif2cdp_wr_rsp_complete
    // cdp2cvif_wr
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdp2cvif_wr_req_pd.get <> u_wdma.io.cdp2cvif_wr_req_pd.get
        u_wdma.io.cvif2cdp_wr_rsp_complete.get := io.cvif2cdp_wr_rsp_complete.get
    }
    io.cdp2glb_done_intr_pd := u_wdma.io.cdp2glb_done_intr_pd
    // config
    u_wdma.io.reg2dp_dma_en := field_dual.dma_en
    u_wdma.io.reg2dp_dst_base_addr_high := field_dual.dst_base_addr_high
    u_wdma.io.reg2dp_dst_base_addr_low := field_dual.dst_base_addr_low
    u_wdma.io.reg2dp_dst_line_stride := field_dual.dst_line_stride
    u_wdma.io.reg2dp_dst_ram_type := field_dual.dst_ram_type
    u_wdma.io.reg2dp_dst_surface_stride := field_dual.dst_surface_stride
    u_wdma.io.reg2dp_interrupt_ptr := u_reg.io.reg2dp_interrupt_ptr
    u_wdma.io.reg2dp_op_en := u_reg.io.reg2dp_op_en
    //========================================
    //PDP core instance
    //----------------------------------------
    //clk
    u_dp.io.nvdla_core_clk := nvdla_op_gated_clk_core
    u_dp.io.nvdla_core_clk_orig := io.nvdla_clock.nvdla_core_clk
    u_dp.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    //pdp_rdma2dp
    u_dp.io.cdp_rdma2dp_pd <> u_nan.io.nan_preproc_pd

    //config 
    u_dp.io.reg2dp_datin.offset := field_dual.datin_offset
    u_dp.io.reg2dp_datin.scale := field_dual.datin_scale
    u_dp.io.reg2dp_datin.shifter := field_dual.datin_shifter
    u_dp.io.reg2dp_datout.offset := field_dual.datin_offset
    u_dp.io.reg2dp_datout.scale := field_dual.datin_scale
    u_dp.io.reg2dp_datout.shifter := field_dual.datin_shifter
    u_dp.io.reg2dp_lut_access_type := field_single.lut_access_type
    u_dp.io.reg2dp_lut_addr := u_reg.io.reg2dp_lut_addr
    u_dp.io.reg2dp_lut_data := u_reg.io.reg2dp_lut_data
    u_dp.io.reg2dp_lut_data_trigger := u_reg.io.reg2dp_lut_data_trigger
    u_dp.io.reg2dp_lut_hybrid_priority := field_single.lut_hybrid_priority
    u_dp.io.reg2dp_lut_le_end_high := field_single.lut_le_end_high
    u_dp.io.reg2dp_lut_le_end_low := field_single.lut_le_end_low
    u_dp.io.reg2dp_lut_le_function := field_single.lut_le_function
    u_dp.io.reg2dp_lut_le_index_offset := field_single.lut_le_index_offset
    u_dp.io.reg2dp_lut_le_index_select := field_single.lut_le_index_select
    u_dp.io.reg2dp_lut_le_slope_oflow_scale := field_single.lut_le_slope_oflow_scale
    u_dp.io.reg2dp_lut_le_slope_oflow_shift := field_single.lut_le_slope_oflow_shift
    u_dp.io.reg2dp_lut_le_slope_uflow_scale := field_single.lut_le_slope_uflow_scale
    u_dp.io.reg2dp_lut_le_slope_uflow_shift := field_single.lut_le_slope_uflow_shift
    u_dp.io.reg2dp_lut_le_start_high := field_single.lut_le_start_high
    u_dp.io.reg2dp_lut_le_start_low := field_single.lut_le_start_low
    u_dp.io.reg2dp_lut_lo_end_high := field_single.lut_lo_end_high
    u_dp.io.reg2dp_lut_lo_end_low := field_single.lut_lo_end_low
    u_dp.io.reg2dp_lut_lo_index_select := field_single.lut_lo_index_select
    u_dp.io.reg2dp_lut_lo_slope_oflow_scale := field_single.lut_lo_slope_oflow_scale
    u_dp.io.reg2dp_lut_lo_slope_oflow_shift := field_single.lut_lo_slope_oflow_scale
    u_dp.io.reg2dp_lut_lo_slope_uflow_scale := field_single.lut_lo_slope_uflow_scale 
    u_dp.io.reg2dp_lut_lo_slope_uflow_shift := field_single.lut_lo_slope_uflow_shift
    u_dp.io.reg2dp_lut_lo_start_high := field_single.lut_lo_start_high
    u_dp.io.reg2dp_lut_lo_start_low := field_single.lut_lo_start_low
    u_dp.io.reg2dp_lut_oflow_priority := field_single.lut_oflow_priority
    u_dp.io.reg2dp_lut_table_id := field_single.lut_table_id
    u_dp.io.reg2dp_lut_uflow_priority := field_single.lut_uflow_priority
    u_dp.io.reg2dp_sqsum_bypass := field_dual.sqsum_bypass
    u_dp.io.reg2dp_mul_bypass := field_dual.mul_bypass
    u_dp.io.reg2dp_normalz_len := field_dual.normalz_len

    u_dp.io.dp2reg_done := u_wdma.io.dp2reg_done


 
}}


object NV_NVDLA_cdpDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_cdp)
}





