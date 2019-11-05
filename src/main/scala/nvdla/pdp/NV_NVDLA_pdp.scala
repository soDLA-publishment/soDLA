package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_pdp(implicit val conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {

        val nvdla_clock = Flipped(new nvdla_clock_if)
        val nvdla_core_rstn = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val csb2pdp_rdma = new csb2dp_if
        val csb2pdp = new csb2dp_if

        val pdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt((conf.NVDLA_PDP_MEM_RD_REQ).W))) else None
        val pdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_WR_REQ.W))) else None
        val cvif2pdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_RSP.W)))) else None
        val pdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2pdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None

        val pdp2mcif_rd_req_pd = DecoupledIO(UInt((conf.NVDLA_PDP_MEM_RD_REQ).W))
        val pdp2mcif_wr_req_pd = DecoupledIO(UInt(conf.NVDLA_PDP_MEM_WR_REQ.W))
        val mcif2pdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_RSP.W)))
        val pdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())
        val mcif2pdp_wr_rsp_complete = Input(Bool())

        val pdp2glb_done_intr_pd = Output(UInt(2.W))

        val sdp2pdp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_ONFLY_INPUT_BW.W)))
        

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

    val u_reg = Module(new NV_NVDLA_PDP_reg)
    val u_slcg_core = Array.fill(2){Module(new NV_NVDLA_slcg(1, false))}
    val u_rdma = Module(new NV_NVDLA_PDP_rdma)
    val u_nan = Module(new NV_NVDLA_PDP_nan)
    val u_wdma = Module(new NV_NVDLA_PDP_wdma)
    val u_core = Module(new NV_NVDLA_PDP_core)

    //=======================================
    //CONFIG instance
    //rdma has seperate config register, while wdma share with core
    //---------------------------------------
    //general clock
    u_reg.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk 
    //csb
    io.csb2pdp <> u_reg.io.csb2pdp
    //reg2dp
    val field = new pdp_reg_dual_flop_outputs

    u_reg.io.dp2reg_d0_perf_write_stall := u_wdma.io.dp2reg_d0_perf_write_stall
    u_reg.io.dp2reg_d1_perf_write_stall := u_wdma.io.dp2reg_d1_perf_write_stall
    u_reg.io.dp2reg_done := u_wdma.io.dp2reg_done

    u_reg.io.dp2reg_inf_input_num := 0.U
    u_reg.io.dp2reg_nan_input_num := 0.U
    u_reg.io.dp2reg_nan_output_num := 0.U
    //=======================================
    //        SLCG gen unit
    //--------------------------------------
    //0 for core, 1 for wdma, 2 for fp
    for(i <- 0 to 1){
        u_slcg_core(i).io.nvdla_clock <> io.nvdla_clock
    }
    u_slcg_core(0).io.slcg_en(0) := u_reg.io.slcg_op_en(0)
    u_slcg_core(1).io.slcg_en(0) := u_reg.io.slcg_op_en(1)
    val nvdla_op_gated_clk_core = u_slcg_core(0).io.nvdla_core_gated_clk
    val nvdla_op_gated_clk_wdma = u_slcg_core(1).io.nvdla_core_gated_clk

    //=======================================
    //RDMA
    //--------------------------------------- 
    u_rdma.io.nvdla_clock <> io.nvdla_clock
    u_rdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    //csb2pdp_rdma
    io.csb2pdp_rdma <> u_rdma.io.csb2pdp_rdma
    //request_response
    io.pdp2mcif_rd_req_pd <> u_rdma.io.pdp2mcif_rd_req_pd
    u_rdma.io.mcif2pdp_rd_rsp_pd <> io.mcif2pdp_rd_rsp_pd
    io.pdp2mcif_rd_cdt_lat_fifo_pop := u_rdma.io.pdp2mcif_rd_cdt_lat_fifo_pop

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.pdp2cvif_rd_req_pd.get <> u_rdma.io.pdp2cvif_rd_req_pd.get
        u_rdma.io.cvif2pdp_rd_rsp_pd.get <> io.cvif2pdp_rd_rsp_pd.get
        io.pdp2cvif_rd_cdt_lat_fifo_pop.get := u_rdma.io.pdp2cvif_rd_cdt_lat_fifo_pop.get
    }
    
    //=======================================
    //NaN control of RDMA output data
    //---------------------------------------
    u_nan.io.nvdla_core_clk := nvdla_op_gated_clk_core
    //rdma2dp
    u_nan.io.pdp_rdma2dp_pd <> u_rdma.io.pdp_rdma2dp_pd
    //config  
    u_nan.io.reg2dp_flying_mode := field.flying_mode
    u_nan.io.reg2dp_nan_to_zero :=  field.nan_to_zero
    u_nan.io.reg2dp_op_en :=  u_reg.io.reg2dp_op_en
    u_nan.io.dp2reg_done := u_wdma.io.dp2reg_done

    //=======================================
    //WDMA
    //---------------------------------------
    u_wdma.io.nvdla_core_clk_orig := io.nvdla_clock.nvdla_core_clk
    u_wdma.io.nvdla_core_clk := nvdla_op_gated_clk_wdma
    u_wdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    // pdp2mcif_wr
    io.pdp2mcif_wr_req_pd <> u_wdma.io.pdp2mcif_wr_req_pd
    u_wdma.io.mcif2pdp_wr_rsp_complete := io.mcif2pdp_wr_rsp_complete
    // pdp2cvif_wr
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.pdp2cvif_wr_req_pd.get <> u_wdma.io.pdp2cvif_wr_req_pd.get
        u_wdma.io.cvif2pdp_wr_rsp_complete.get := io.cvif2pdp_wr_rsp_complete.get
    }
    
    io.pdp2glb_done_intr_pd := u_wdma.io.pdp2glb_done_intr_pd
    u_wdma.io.rdma2wdma_done := u_rdma.io.rdma2wdma_done
    // dp2wdma
    u_wdma.io.pdp_dp2wdma_pd <> u_core.io.pdp_dp2wdma_pd
    // config
    u_wdma.io.reg2dp_cube_out_channel := field.cube_out_channel
    u_wdma.io.reg2dp_cube_out_height := field.cube_out_height
    u_wdma.io.reg2dp_cube_out_width := field.cube_out_width
    u_wdma.io.reg2dp_dma_en := field.dma_en
    u_wdma.io.reg2dp_dst_base_addr_high := field.dst_base_addr_high
    u_wdma.io.reg2dp_dst_base_addr_low := field.dst_base_addr_low
    u_wdma.io.reg2dp_dst_line_stride := field.dst_line_stride
    u_wdma.io.reg2dp_dst_ram_type := field.dst_ram_type
    u_wdma.io.reg2dp_dst_surface_stride := field.dst_surface_stride
    u_wdma.io.reg2dp_flying_mode := field.flying_mode
    u_wdma.io.reg2dp_partial_width_out_first := field.partial_width_out_first
    u_wdma.io.reg2dp_partial_width_out_last := field.partial_width_out_last
    u_wdma.io.reg2dp_partial_width_out_mid := field.partial_width_out_mid
    u_wdma.io.reg2dp_split_num := field.split_num

    u_wdma.io.reg2dp_interrupt_ptr := u_reg.io.reg2dp_interrupt_ptr
    u_wdma.io.reg2dp_op_en := u_reg.io.reg2dp_op_en
    //========================================
    //PDP core instance
    //----------------------------------------
    //clk
    u_core.io.nvdla_core_clk := nvdla_op_gated_clk_core
    u_core.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    //pdp_rdma2dp
    u_core.io.pdp_rdma2dp_pd <> u_nan.io.nan_preproc_pd
    //sdp2pdp
    u_core.io.sdp2pdp_pd <> io.sdp2pdp_pd
    //config 
    u_core.io.datin_src_cfg := field.flying_mode
    u_core.io.dp2reg_done := u_wdma.io.dp2reg_done
    
    u_core.io.padding_h_cfg := field.pad_left
    u_core.io.padding_v_cfg := field.pad_top
    u_core.io.pooling_channel_cfg := field.cube_out_channel
    u_core.io.pooling_fwidth_cfg := field.partial_width_in_first
    u_core.io.pooling_lwidth_cfg := field.partial_width_in_last
    u_core.io.pooling_mwidth_cfg := field.partial_width_in_mid
    u_core.io.pooling_out_fwidth_cfg := field.partial_width_out_first
    u_core.io.pooling_out_lwidth_cfg := field.partial_width_out_last
    u_core.io.pooling_out_mwidth_cfg := field.partial_width_out_mid
    u_core.io.pooling_size_h_cfg := field.kernel_width
    u_core.io.pooling_size_v_cfg := field.kernel_height
    u_core.io.pooling_splitw_num_cfg := field.split_num
    u_core.io.pooling_stride_h_cfg := field.kernel_stride_width
    u_core.io.pooling_stride_v_cfg := field.kernel_stride_height
    u_core.io.pooling_type_cfg := field.pooling_method
    u_core.io.reg2dp_cube_in_channel := field.cube_in_channel
    u_core.io.reg2dp_cube_in_height := field.cube_in_height
    u_core.io.reg2dp_cube_in_width := field.cube_in_width
    u_core.io.reg2dp_cube_out_width := field.cube_out_width
    u_core.io.reg2dp_flying_mode := field.flying_mode
    u_core.io.reg2dp_kernel_height := field.kernel_height
    u_core.io.reg2dp_kernel_stride_width := field.kernel_stride_width
    u_core.io.reg2dp_kernel_width := field.kernel_width
    u_core.io.reg2dp_pad_bottom_cfg := field.pad_bottom
    u_core.io.reg2dp_pad_left := field.pad_left
    u_core.io.reg2dp_pad_right := field.pad_right
    u_core.io.reg2dp_pad_right_cfg := field.pad_right
    u_core.io.reg2dp_pad_top := field.pad_top
    u_core.io.reg2dp_pad_value_1x_cfg := field.pad_value_1x
    u_core.io.reg2dp_pad_value_2x_cfg := field.pad_value_2x
    u_core.io.reg2dp_pad_value_3x_cfg := field.pad_value_3x
    u_core.io.reg2dp_pad_value_4x_cfg := field.pad_value_4x
    u_core.io.reg2dp_pad_value_5x_cfg := field.pad_value_5x
    u_core.io.reg2dp_pad_value_6x_cfg := field.pad_value_6x
    u_core.io.reg2dp_pad_value_7x_cfg := field.pad_value_7x
    u_core.io.reg2dp_partial_width_out_first := field.partial_width_out_first
    u_core.io.reg2dp_partial_width_out_last := field.partial_width_out_last
    u_core.io.reg2dp_partial_width_out_mid := field.partial_width_out_mid
    u_core.io.reg2dp_recip_height_cfg := field.recip_kernel_height
    u_core.io.reg2dp_recip_width_cfg := field.recip_kernel_width

    u_core.io.reg2dp_op_en := u_reg.io.reg2dp_op_en

 
}}


object NV_NVDLA_pdpDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_sdp)
}





