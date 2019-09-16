package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_cdmaIO(implicit val conf: nvdlaConfig) extends Bundle{

    //general clock
    val nvdla_clock = Flipped(new nvdla_clock_if)
    val nvdla_core_rstn = Input(Bool())

    //csb
    val csb2cdma = new csb2dp_if 

    //buf dat
    val cdma2buf_dat_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Output(UInt((conf.ATMC/conf.DMAIF).W))) else None
    val cdma2buf_dat_wr = new nvdla_wr_if(17, conf.DMAIF)

    //buf wt
    val cdma2buf_wt_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Output(UInt((conf.ATMC/conf.DMAIF).W))) else None 
    val cdma2buf_wt_wr = new nvdla_wr_if(17, conf.DMAIF)

    //glb
    val cdma_dat2glb_done_intr_pd = Output(UInt(2.W))
    val cdma_wt2glb_done_intr_pd = Output(UInt(2.W))

    //cvif
    val cdma_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
    val cvif2cdma_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))) else None

    val cdma_wt2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
    val cvif2cdma_wt_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))) else None

    //mcif
    val cdma_dat2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val mcif2cdma_dat_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))

    val cdma_wt2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val mcif2cdma_wt_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W)))

    //sc
    val sc2cdma_dat_pending_req = Input(Bool())
    val sc2cdma_wt_pending_req = Input(Bool())
    val cdma2sc_dat_pending_ack = Output(Bool())
    val cdma2sc_wt_pending_ack = Output(Bool())

    val cdma2sc_dat_updt = ValidIO(new updt_entries_slices_if)
    val sc2cdma_dat_updt = Flipped(ValidIO(new updt_entries_slices_if))

    val cdma2sc_wt_updt = ValidIO(new updt_entries_kernels_if)
    val sc2cdma_wt_updt = Flipped(ValidIO(new updt_entries_kernels_if))

    //pwrbus
    val pwrbus_ram_pd = Input(UInt(32.W))

}

class NV_NVDLA_cdma(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new NV_NVDLA_cdmaIO)
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

withReset(io.nvdla_core_rstn){

    val u_regfile = Module(new NV_NVDLA_CDMA_regfile)
    val u_wt = Module(new NV_NVDLA_CDMA_wt)   
    val u_slcg_wt = Module(new NV_NVDLA_slcg(1, false))
    val u_dc = Module(new NV_NVDLA_CDMA_dc)
    val u_slcg_dc = Module(new NV_NVDLA_slcg(3, false))
    val u_img = Module(new NV_NVDLA_CDMA_img)
    val u_slcg_img = Module(new NV_NVDLA_slcg(3, false))
    val u_dma_mux = Module(new NV_NVDLA_CDMA_dma_mux)
    val u_slcg_mux = Module(new NV_NVDLA_slcg(1, false))
    val u_cvt = Module(new NV_NVDLA_CDMA_cvt)
    val u_slcg_cvt = Module(new NV_NVDLA_slcg(1, false))
    val u_slcg_hls = Module(new NV_NVDLA_slcg(2, false))
    val u_shared_buffer = Module(new NV_NVDLA_CDMA_shared_buffer)
    val u_slcg_buffer = Module(new NV_NVDLA_slcg(1, false))
    val u_status = Module(new NV_NVDLA_CDMA_status)
    //==========================================================
    // Regfile
    //==========================================================
    u_regfile.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk                //|< i

    u_regfile.io.csb2cdma <> io.csb2cdma       //|< b
    //dc
    u_regfile.io.dp2reg_dc_rd_stall := u_dc.io.dp2reg_dc_rd_stall
    u_regfile.io.dp2reg_dc_rd_latency := u_dc.io.dp2reg_dc_rd_latency
    //img
    u_regfile.io.dp2reg_img_rd_stall := u_img.io.dp2reg_img_rd_stall
    u_regfile.io.dp2reg_img_rd_latency := u_img.io.dp2reg_img_rd_latency
    //wgs
    u_regfile.io.dp2reg_wg_rd_stall := "b0".asUInt(32.W)
    u_regfile.io.dp2reg_wg_rd_latency :=  "b0".asUInt(32.W)
    //wt
    u_regfile.io.dp2reg_nan_weight_num := u_wt.io.dp2reg_nan_weight_num
    u_regfile.io.dp2reg_inf_weight_num := u_wt.io.dp2reg_inf_weight_num
    u_regfile.io.dp2reg_wt_flush_done := u_wt.io.dp2reg_wt_flush_done
    u_regfile.io.dp2reg_wt_rd_stall := u_wt.io.dp2reg_wt_rd_stall
    u_regfile.io.dp2reg_wt_rd_latency := 0.U
    //status
    u_regfile.io.dp2reg_done := u_status.io.dp2reg_done
    //cvt
    u_regfile.io.dp2reg_nan_data_num := u_cvt.io.dp2reg_nan_data_num
    u_regfile.io.dp2reg_inf_data_num := u_cvt.io.dp2reg_inf_data_num
    u_regfile.io.dp2reg_dat_flush_done := u_cvt.io.dp2reg_dat_flush_done

    val field = u_regfile.io.reg2dp_field  
    val reg2dp_op_en = u_regfile.io.reg2dp_op_en  
    val reg2dp_arb_weight = u_regfile.io.reg2dp_arb_weight 
    val reg2dp_arb_wmb = u_regfile.io.reg2dp_arb_wmb   
                 
    //==========================================================
    // Weight DMA
    //==========================================================
    u_wt.io.nvdla_core_clk := u_slcg_wt.io.nvdla_core_gated_clk  
    u_wt.io.nvdla_core_ng_clk := io.nvdla_clock.nvdla_core_clk
    u_wt.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    // to I/O
    io.cdma_wt2mcif_rd_req_pd <> u_wt.io.cdma_wt2mcif_rd_req_pd
    u_wt.io.mcif2cdma_wt_rd_rsp_pd <> io.mcif2cdma_wt_rd_rsp_pd

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdma_wt2cvif_rd_req_pd.get  <> u_wt.io.cdma_wt2cvif_rd_req_pd.get
        u_wt.io.cvif2cdma_wt_rd_rsp_pd.get <> io.cvif2cdma_wt_rd_rsp_pd.get
    }

    if(conf.DMAIF < conf.ATMC){
        io.cdma2buf_wt_wr_sel.get := u_wt.io.cdma2buf_wt_wr_sel.get
    }
    io.cdma2buf_wt_wr <> u_wt.io.cdma2buf_wt_wr

    io.cdma2sc_wt_updt <> u_wt.io.cdma2sc_wt_updt
    u_wt.io.sc2cdma_wt_updt <> io.sc2cdma_wt_updt

    u_wt.io.sc2cdma_wt_pending_req := io.sc2cdma_wt_pending_req
    io.cdma2sc_wt_pending_ack := u_wt.io.cdma2sc_wt_pending_ack

    //regfile
    u_wt.io.reg2dp_arb_weight := u_regfile.io.reg2dp_arb_weight
    u_wt.io.reg2dp_arb_wmb := u_regfile.io.reg2dp_arb_wmb
    u_wt.io.reg2dp_op_en := u_regfile.io.reg2dp_op_en
    u_wt.io.reg2dp_proc_precision := field.proc_precision
    u_wt.io.reg2dp_weight_reuse := field.weight_reuse
    u_wt.io.reg2dp_skip_weight_rls := field.skip_weight_rls
    u_wt.io.reg2dp_weight_format := field.weight_format
    u_wt.io.reg2dp_byte_per_kernel := field.byte_per_kernel
    u_wt.io.reg2dp_weight_kernel := field.weight_kernel
    u_wt.io.reg2dp_weight_ram_type := field.weight_ram_type
    u_wt.io.reg2dp_weight_addr_low := field.weight_addr_low(31, conf.ATMMBW)
    u_wt.io.reg2dp_wgs_addr_low := field.wgs_addr_low(31, conf.ATMMBW)
    u_wt.io.reg2dp_wmb_addr_low := field.wmb_addr_low(31, conf.ATMMBW)
    u_wt.io.reg2dp_weight_addr_high := field.weight_addr_high
    u_wt.io.reg2dp_weight_bytes := field.weight_bytes
    u_wt.io.reg2dp_wgs_addr_high := field.wgs_addr_high
    u_wt.io.reg2dp_wmb_addr_high := field.wmb_addr_high
    u_wt.io.reg2dp_wmb_bytes := field.wmb_bytes
    u_wt.io.reg2dp_data_bank := field.data_bank
    u_wt.io.reg2dp_weight_bank := field.weight_bank
    u_wt.io.reg2dp_nan_to_zero := field.nan_to_zero
    u_wt.io.reg2dp_dma_en := field.dma_en

    //==========================================================
    //-------------- SLCG for weight DMA --------------//
    //==========================================================  
    u_slcg_wt.io.nvdla_clock <> io.nvdla_clock
    u_slcg_wt.io.slcg_en(0) := u_regfile.io.slcg_op_en(0)

    //==========================================================
    // Direct convolution DMA
    //==========================================================
    u_dc.io.nvdla_core_clk := u_slcg_dc.io.nvdla_core_gated_clk 
    u_dc.io.nvdla_core_ng_clk := io.nvdla_clock.nvdla_core_clk
    u_dc.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    //data address channel
    u_dma_mux.io.dc_dat2mcif_rd_req_pd <> u_dc.io.dc_dat2mcif_rd_req_pd
    u_dc.io.mcif2dc_dat_rd_rsp_pd <> u_dma_mux.io.mcif2dc_dat_rd_rsp_pd
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_dma_mux.io.dc_dat2cvif_rd_req_pd.get <> u_dc.io.dc_dat2cvif_rd_req_pd.get
        u_dc.io.cvif2dc_dat_rd_rsp_pd.get <> u_dma_mux.io.cvif2dc_dat_rd_rsp_pd.get       
    }
    //data response
    u_dc.io.sc2cdma_dat_pending_req := io.sc2cdma_dat_pending_req
    //to converter>>pixel/feature data
    if(conf.DMAIF < conf.ATMC){
        u_cvt.io.dc2cvt_dat_wr_sel.get := u_dc.io.dc2cvt_dat_wr_sel.get
    }
    u_cvt.io.dc2cvt_dat_wr <> u_dc.io.dc2cvt_dat_wr
    u_cvt.io.dc2cvt_dat_wr_info_pd := u_dc.io.dc2cvt_dat_wr_info_pd
    //cdma status
    u_status.io.dc2status_state := u_dc.io.dc2status_state
    u_status.io.dc2status_dat_updt <> u_dc.io.dc2status_dat_updt
    u_dc.io.status2dma_fsm_switch := u_status.io.status2dma_fsm_switch
    u_dc.io.status2dma_valid_slices := u_status.io.status2dma_valid_slices
    u_dc.io.status2dma_free_entries := u_status.io.status2dma_free_entries
    u_dc.io.status2dma_wr_idx := u_status.io.status2dma_wr_idx
    //to shared buffer
    u_shared_buffer.io.dc2sbuf_p_wr(0) <> u_dc.io.dc2sbuf_p0_wr
    u_shared_buffer.io.dc2sbuf_p_rd(0) <> u_dc.io.dc2sbuf_p0_rd
    //config
    u_dc.io.reg2dp_op_en := reg2dp_op_en
    u_dc.io.reg2dp_conv_mode := field.conv_mode
    u_dc.io.reg2dp_data_reuse := field.data_reuse
    u_dc.io.reg2dp_skip_data_rls := field.skip_data_rls
    u_dc.io.reg2dp_datain_format := field.datain_format
    u_dc.io.reg2dp_datain_width := field.datain_width
    u_dc.io.reg2dp_datain_height := field.datain_height
    u_dc.io.reg2dp_datain_channel := field.datain_channel
    u_dc.io.reg2dp_datain_ram_type := field.datain_ram_type
    u_dc.io.reg2dp_datain_addr_high_0 := field.datain_addr_high_0
    u_dc.io.reg2dp_datain_addr_low_0 := field.datain_addr_low_0(31-conf.ATMMBW, 0)
    u_dc.io.reg2dp_line_stride := field.line_stride(31-conf.ATMMBW, 0)
    u_dc.io.reg2dp_surf_stride := field.surf_stride(31-conf.ATMMBW, 0)
    u_dc.io.reg2dp_batch_stride := field.batch_stride(31-conf.ATMMBW, 0)
    u_dc.io.reg2dp_line_packed := field.line_packed
    u_dc.io.reg2dp_surf_packed := field.surf_packed
    u_dc.io.reg2dp_batches := field.batches
    u_dc.io.reg2dp_entries := field.entries
    u_dc.io.reg2dp_grains := field.grains
    u_dc.io.reg2dp_data_bank := field.data_bank
    u_dc.io.reg2dp_dma_en := field.dma_en

    //==========================================================
    //-------------- SLCG for DC DMA --------------//
    //==========================================================
    u_slcg_dc.io.nvdla_clock <> io.nvdla_clock

    u_slcg_dc.io.slcg_en(0) := u_regfile.io.slcg_op_en(1)
    val slcg_wg_gate_dc = true.B
    u_slcg_dc.io.slcg_en(1) := slcg_wg_gate_dc
    u_slcg_dc.io.slcg_en(2) := u_img.io.slcg_img_gate_dc

    //==========================================================
    //-------------- SLCG for WG DMA --------------//
    //==========================================================


    //==========================================================
    // Image convolution DMA
    //==========================================================
    u_img.io.nvdla_core_clk := u_slcg_img.io.nvdla_core_gated_clk
    u_img.io.nvdla_core_ng_clk := io.nvdla_clock.nvdla_core_clk
    u_img.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    //data address channel
    u_dma_mux.io.img_dat2mcif_rd_req_pd <> u_img.io.img_dat2mcif_rd_req_pd
    u_img.io.mcif2img_dat_rd_rsp_pd <> u_dma_mux.io.mcif2img_dat_rd_rsp_pd
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_dma_mux.io.img_dat2cvif_rd_req_pd.get <> u_img.io.img_dat2cvif_rd_req_pd.get
        u_img.io.cvif2img_dat_rd_rsp_pd.get <> u_dma_mux.io.cvif2img_dat_rd_rsp_pd.get
    }
    //data response
    u_img.io.sc2cdma_dat_pending_req := io.sc2cdma_dat_pending_req
    //to converter>>pixel/feature data
    if(conf.DMAIF < conf.ATMC){
        u_cvt.io.img2cvt_dat_wr_sel.get := u_img.io.img2cvt_dat_wr_sel.get
    }
    u_cvt.io.img2cvt_dat_wr <> u_img.io.img2cvt_dat_wr
    u_cvt.io.img2cvt_mn_wr_data := u_img.io.img2cvt_mn_wr_data
    u_cvt.io.img2cvt_dat_wr_pad_mask := u_img.io.img2cvt_dat_wr_pad_mask
    u_cvt.io.img2cvt_dat_wr_info_pd := u_img.io.img2cvt_dat_wr_info_pd
    //cdma status
    u_status.io.img2status_state := u_img.io.img2status_state
    u_status.io.img2status_dat_updt <> u_img.io.img2status_dat_updt
    u_img.io.status2dma_valid_slices := u_status.io.status2dma_valid_slices
    u_img.io.status2dma_free_entries := u_status.io.status2dma_free_entries
    u_img.io.status2dma_wr_idx := u_status.io.status2dma_wr_idx
    u_img.io.status2dma_fsm_switch := u_status.io.status2dma_fsm_switch
    //to shared buffer
    u_shared_buffer.io.img2sbuf_p_wr(0) <> u_img.io.img2sbuf_p0_wr
    u_shared_buffer.io.img2sbuf_p_rd(0) <> u_img.io.img2sbuf_p0_rd
    //config
    u_img.io.reg2dp_op_en := reg2dp_op_en
    u_img.io.reg2dp_conv_mode := field.conv_mode
    u_img.io.reg2dp_in_precision := field.in_precision
    u_img.io.reg2dp_proc_precision := field.proc_precision
    u_img.io.reg2dp_data_reuse := field.data_reuse
    u_img.io.reg2dp_skip_data_rls := field.skip_data_rls
    u_img.io.reg2dp_datain_format := field.datain_format
    u_img.io.reg2dp_pixel_format := field.pixel_format
    u_img.io.reg2dp_pixel_mapping := field.pixel_mapping
    u_img.io.reg2dp_pixel_sign_override := field.pixel_sign_override
    u_img.io.reg2dp_datain_width := field.datain_width
    u_img.io.reg2dp_datain_height := field.datain_height
    u_img.io.reg2dp_datain_channel := field.datain_channel
    u_img.io.reg2dp_pixel_x_offset := field.pixel_x_offset
    u_img.io.reg2dp_pixel_y_offset := field.pixel_y_offset
    u_img.io.reg2dp_datain_ram_type := field.datain_ram_type
    u_img.io.reg2dp_datain_addr_high_0 := field.datain_addr_high_0
    u_img.io.reg2dp_datain_addr_low_0 := field.datain_addr_low_0
    u_img.io.reg2dp_datain_addr_low_1 := field.datain_addr_low_1
    u_img.io.reg2dp_line_stride := field.line_stride
    u_img.io.reg2dp_uv_line_stride := field.uv_line_stride
    u_img.io.reg2dp_datain_addr_high_1 := field.datain_addr_high_1
    u_img.io.reg2dp_mean_format := field.mean_format
    u_img.io.reg2dp_mean_ry := field.mean_ry
    u_img.io.reg2dp_mean_gu := field.mean_gu
    u_img.io.reg2dp_mean_bv := field.mean_bv
    u_img.io.reg2dp_mean_ax := field.mean_ax
    u_img.io.reg2dp_entries := field.entries
    u_img.io.reg2dp_pad_left := field.pad_left
    u_img.io.reg2dp_pad_right := field.pad_right
    u_img.io.reg2dp_data_bank := field.data_bank
    u_img.io.reg2dp_dma_en := field.dma_en
    u_img.io.reg2dp_rsv_per_line := field.rsv_per_line
    u_img.io.reg2dp_rsv_per_uv_line := field.rsv_per_uv_line
    u_img.io.reg2dp_rsv_height := field.rsv_height
    u_img.io.reg2dp_rsv_y_index := field.rsv_y_index

    //dangle
    val slcg_img_gate_wg = u_img.io.slcg_img_gate_wg

    //==========================================================
    //-------------- SLCG for IMG DMA --------------//
    //==========================================================
    u_slcg_img.io.nvdla_clock <> io.nvdla_clock

    u_slcg_img.io.slcg_en(0) := u_regfile.io.slcg_op_en(3)
    u_slcg_img.io.slcg_en(1) := u_dc.io.slcg_dc_gate_img
    val slcg_wg_gate_img = true.B
    u_slcg_img.io.slcg_en(2) := slcg_wg_gate_img

    //==========================================================
    //-------------- DMA mux--------------//
    //==========================================================
    u_dma_mux.io.nvdla_core_clk := u_slcg_mux.io.nvdla_core_gated_clk

    io.cdma_dat2mcif_rd_req_pd <> u_dma_mux.io.cdma_dat2mcif_rd_req_pd
    u_dma_mux.io.mcif2cdma_dat_rd_rsp_pd <> io.mcif2cdma_dat_rd_rsp_pd

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdma_dat2cvif_rd_req_pd.get <> u_dma_mux.io.cdma_dat2cvif_rd_req_pd.get
        u_dma_mux.io.cvif2cdma_dat_rd_rsp_pd.get <> io.cvif2cdma_dat_rd_rsp_pd.get
    }

    //==========================================================
    //-------------- SLCG for MUX  --------------//
    //==========================================================
    u_slcg_mux.io.nvdla_clock <> io.nvdla_clock

    u_slcg_mux.io.slcg_en(0) := u_regfile.io.slcg_op_en(4)
    //==========================================================
    //-------------- DMA data convertor--------------//
    //==========================================================
    u_cvt.io.nvdla_core_clk := u_slcg_cvt.io.nvdla_core_gated_clk
    u_cvt.io.nvdla_hls_clk := u_slcg_hls.io.nvdla_core_gated_clk
    u_cvt.io.nvdla_core_ng_clk := io.nvdla_clock.nvdla_core_clk

    //to IO 
    if(conf.DMAIF < conf.ATMC){
        io.cdma2buf_dat_wr_sel.get := u_cvt.io.cdma2buf_dat_wr_sel.get
    }
    io.cdma2buf_dat_wr <> u_cvt.io.cdma2buf_dat_wr

    //config
    u_cvt.io.reg2dp_op_en := u_regfile.io.reg2dp_op_en
    u_cvt.io.reg2dp_in_precision := field.in_precision
    u_cvt.io.reg2dp_proc_precision := field.proc_precision
    u_cvt.io.reg2dp_cvt_en := field.cvt_en
    u_cvt.io.reg2dp_cvt_truncate := field.cvt_truncate
    u_cvt.io.reg2dp_cvt_offset := field.cvt_offset
    u_cvt.io.reg2dp_cvt_scale := field.cvt_scale
    u_cvt.io.reg2dp_nan_to_zero := field.nan_to_zero
    u_cvt.io.reg2dp_pad_value := field.pad_value
    u_cvt.io.dp2reg_done := u_status.io.dp2reg_done

    //==========================================================
    //-------------- SLCG for CVT  --------------//
    //==========================================================
    u_slcg_cvt.io.nvdla_clock <> io.nvdla_clock

    u_slcg_cvt.io.slcg_en(0) := u_regfile.io.slcg_op_en(5)

    //==========================================================
    //-------------- SLCG for CVT HLS CELL  --------------//
    //==========================================================
    u_slcg_hls.io.nvdla_clock <> io.nvdla_clock

    u_slcg_hls.io.slcg_en(0) := u_regfile.io.slcg_op_en(6)
    u_slcg_hls.io.slcg_en(1) := u_cvt.io.slcg_hls_en 

    //==========================================================
    //-------------- Shared buffer  --------------//
    //==========================================================
    u_shared_buffer.io.nvdla_core_clk := u_slcg_buffer.io.nvdla_core_gated_clk
    u_shared_buffer.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_shared_buffer.io.dc2sbuf_p_wr(1).addr.valid := false.B
    u_shared_buffer.io.dc2sbuf_p_wr(1).addr.bits := "b0".asUInt(8.W)
    u_shared_buffer.io.dc2sbuf_p_wr(1).data := "b0".asUInt((conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE).W)
    u_shared_buffer.io.dc2sbuf_p_rd(1).addr.valid := false.B
    u_shared_buffer.io.dc2sbuf_p_rd(1).addr.bits := "b0".asUInt(8.W)

    u_shared_buffer.io.img2sbuf_p_wr(1).addr.valid := false.B
    u_shared_buffer.io.img2sbuf_p_wr(1).addr.bits := "b0".asUInt(8.W)
    u_shared_buffer.io.img2sbuf_p_wr(1).data := "b0".asUInt((conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE).W)
    u_shared_buffer.io.img2sbuf_p_rd(1).addr.valid := false.B
    u_shared_buffer.io.img2sbuf_p_rd(1).addr.bits := "b0".asUInt(8.W)

    //==========================================================
    //-------------- SLCG for shared buffer  --------------//
    //==========================================================
    u_slcg_buffer.io.nvdla_clock <> io.nvdla_clock
    u_slcg_buffer.io.slcg_en(0) := u_regfile.io.slcg_op_en(7)

    //==========================================================
    //-------------- CDMA status controller --------------//
    //==========================================================
    u_status.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk

    u_status.io.dc2status_dat_updt <> u_dc.io.dc2status_dat_updt

    u_status.io.sc2cdma_dat_updt <> io.sc2cdma_dat_updt
    io.cdma2sc_dat_updt <> u_status.io.cdma2sc_dat_updt

    io.cdma_wt2glb_done_intr_pd := u_status.io.cdma_wt2glb_done_intr_pd
    io.cdma_dat2glb_done_intr_pd := u_status.io.cdma_dat2glb_done_intr_pd

    u_status.io.sc2cdma_dat_pending_req := io.sc2cdma_dat_pending_req
    io.cdma2sc_dat_pending_ack := u_status.io.cdma2sc_dat_pending_ack

    u_status.io.reg2dp_op_en := u_regfile.io.reg2dp_op_en
    u_status.io.reg2dp_data_bank := field.data_bank
    u_status.io.dp2reg_consumer := u_regfile.io.dp2reg_consumer

    u_wt.io.status2dma_fsm_switch := u_status.io.status2dma_fsm_switch
    u_status.io.wt2status_state := u_wt.io.wt2status_state

}}


object NV_NVDLA_cdmaDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_cdma())
}
