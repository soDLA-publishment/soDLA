package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_cdma(implicit val conf: cdmaConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())   
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val tmc2slcg_disable_clock_gating = Input(Bool())      

        //csb
        val cdma2csb_resp_valid = Output(Bool())  
        val cdma2csb_resp_pd = Output(UInt(34.W))
        val csb2cdma_req_pvld = Input(Bool())
        val csb2cdma_req_prdy = Output(Bool()) 
        val csb2cdma_req_pd = Input(UInt(63.W))

        //buf dat
        val cdma2buf_dat_wr_en = Output(Bool())
        val cdma2buf_dat_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Output(UInt((conf.ATMC/conf.DMAIF).W))) else None
        val cdma2buf_dat_wr_addr = Output(UInt(17.W))
        val cdma2buf_dat_wr_data = Output(UInt(conf.DMAIF.W))

        //buf wt
        val cdma2buf_wt_wr_en = Output(Bool())
        val cdma2buf_wt_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Output(UInt((conf.ATMC/conf.DMAIF).W))) else None 
        val cdma2buf_wt_wr_addr = Output(UInt(17.W))
        val cdma2buf_wt_wr_data = Output(UInt(conf.DMAIF.W))

        //glb
        val cdma_dat2glb_done_intr_pd = Output(UInt(2.W))
        val cdma_wt2glb_done_intr_pd = Output(UInt(2.W))

        //cvif
        val cdma_dat2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cdma_dat2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cdma_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2cdma_dat_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cvif2cdma_dat_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2cdma_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None

        val cdma_wt2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cdma_wt2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cdma_wt2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2cdma_wt_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cvif2cdma_wt_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2cdma_wt_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None

        //mcif
        val cdma_dat2mcif_rd_req_valid = Output(Bool())
        val cdma_dat2mcif_rd_req_ready = Input(Bool())
        val cdma_dat2mcif_rd_req_pd = Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2cdma_dat_rd_rsp_valid = Input(Bool())
        val mcif2cdma_dat_rd_rsp_ready = Output(Bool())
        val mcif2cdma_dat_rd_rsp_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))

        val cdma_wt2mcif_rd_req_valid = Output(Bool())
        val cdma_wt2mcif_rd_req_ready = Input(Bool())
        val cdma_wt2mcif_rd_req_pd = Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2cdma_wt_rd_rsp_valid = Input(Bool())
        val mcif2cdma_wt_rd_rsp_ready = Output(Bool())
        val mcif2cdma_wt_rd_rsp_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))

        //sc
        val sc2cdma_dat_pending_req = Input(Bool())
        val sc2cdma_wt_pending_req = Input(Bool())
        val cdma2sc_dat_pending_ack = Output(Bool())
        val cdma2sc_wt_pending_ack = Output(Bool())

        val cdma2sc_dat_updt = Output(Bool())
        val cdma2sc_dat_entries = Output(UInt(15.W))
        val cdma2sc_dat_slices = Output(UInt(14.W))
        val sc2cdma_dat_updt = Input(Bool())
        val sc2cdma_dat_entries = Input(UInt(15.W))
        val sc2cdma_dat_slices = Input(UInt(14.W))

        val cdma2sc_wt_updt = Output(Bool())
        val cdma2sc_wt_kernels = Output(UInt(14.W))
        val cdma2sc_wt_entries = Output(UInt(15.W))
        val cdma2sc_wmb_entries = Output(UInt(9.W))
        val sc2cdma_wt_updt = Input(Bool())
        val sc2cdma_wt_kernels = Input(UInt(14.W))
        val sc2cdma_wt_entries = Input(UInt(15.W))
        val sc2cdma_wmb_entries = Input(UInt(9.W))

        //pwrbus
        val pwrbus_ram_pd = Input(UInt(32.W))

           
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
    val cdma2sc_wmb_entries_f = Wire(UInt(12.W))
    io.cdma2sc_wmb_entries := cdma2sc_wmb_entries_f(8, 0)

    val cvif2dc_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None
    val cvif2dc_dat_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None 
    val cvif2dc_dat_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None
    val cvif2img_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None
    val cvif2img_dat_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None 
    val cvif2img_dat_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None

    val dc_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
    val dc_dat2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None 
    val dc_dat2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None 
    val img_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
    val img_dat2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None 
    val img_dat2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None

    val dc2cvt_dat_wr_en = Wire(Bool())
    val dc2cvt_dat_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Wire(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W))) else None
    val dc2cvt_dat_wr_addr = Wire(UInt(17.W))
    val dc2cvt_dat_wr_data = Wire(UInt(conf.DMAIF.W))
    val dc2cvt_dat_wr_info_pd = Wire(UInt(12.W))

    val dc2sbuf_p0_wr_en = Wire(Bool())
    val dc2sbuf_p0_wr_addr = Wire(UInt(8.W))
    val dc2sbuf_p0_wr_data = Wire(UInt(conf.ATMM.W))
    val dc2sbuf_p0_rd_en = Wire(Bool())
    val dc2sbuf_p0_rd_addr = Wire(UInt(8.W))
    val dc2sbuf_p0_rd_data = Wire(UInt(conf.ATMM.W))

    val dc2status_dat_entries = Wire(UInt(15.W))
    val dc2status_dat_slices = Wire(UInt(14.W))
    val dc2status_dat_updt = Wire(Bool())
    val dc2status_state = Wire(UInt(2.W))
    val dc_dat2mcif_rd_req_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val dc_dat2mcif_rd_req_ready = Wire(Bool())
    val dc_dat2mcif_rd_req_valid = Wire(Bool())
    val mcif2dc_dat_rd_rsp_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    val mcif2dc_dat_rd_rsp_ready = Wire(Bool())
    val mcif2dc_dat_rd_rsp_valid = Wire(Bool())

    val dp2reg_consumer = Wire(Bool())
    val dp2reg_dat_flush_done = Wire(Bool())
    val dp2reg_dc_rd_latency = Wire(UInt(32.W))
    val dp2reg_dc_rd_stall = Wire(UInt(32.W))
    val dp2reg_done = Wire(Bool())
    val dp2reg_img_rd_latency = Wire(UInt(32.W))
    val dp2reg_img_rd_stall = Wire(UInt(32.W))
    val dp2reg_inf_data_num = Wire(UInt(32.W))
    val dp2reg_inf_weight_num = Wire(UInt(32.W))
    val dp2reg_nan_data_num = Wire(UInt(32.W))
    val dp2reg_nan_weight_num = Wire(UInt(32.W))
    val dp2reg_wg_rd_latency = Wire(UInt(32.W))
    val dp2reg_wg_rd_stall = Wire(UInt(32.W))
    val dp2reg_wt_flush_done = Wire(Bool())
    val dp2reg_wt_rd_latency = Wire(UInt(32.W))
    val dp2reg_wt_rd_stall = Wire(UInt(32.W))

    val img2cvt_dat_wr_en = Wire(Bool())
    val img2cvt_dat_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Wire(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W))) else None
    val img2cvt_dat_wr_addr = Wire(UInt(17.W))
    val img2cvt_dat_wr_data = Wire(UInt(conf.DMAIF.W))
    val img2cvt_mn_wr_data = Wire(UInt((conf.BNUM*16).W))
    val img2cvt_dat_wr_pad_mask = Wire(UInt(conf.BNUM.W))
    val img2cvt_dat_wr_info_pd = Wire(UInt(12.W))

    val img2sbuf_p0_wr_en = Wire(Bool())
    val img2sbuf_p0_wr_addr = Wire(UInt(8.W))
    val img2sbuf_p0_wr_data = Wire(UInt(conf.ATMM.W))
    val img2sbuf_p0_rd_en = Wire(Bool())
    val img2sbuf_p0_rd_addr = Wire(UInt(8.W))
    val img2sbuf_p0_rd_data = Wire(UInt(conf.ATMM.W))   

    val img2status_dat_entries = Wire(UInt(15.W))
    val img2status_dat_slices = Wire(UInt(14.W))
    val img2status_dat_updt = Wire(Bool())
    val img2status_state = Wire(UInt(2.W))
    val img_dat2mcif_rd_req_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
    val img_dat2mcif_rd_req_ready = Wire(Bool())
    val img_dat2mcif_rd_req_valid = Wire(Bool())
    val mcif2img_dat_rd_rsp_pd = Wire(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
    val mcif2img_dat_rd_rsp_ready = Wire(Bool())
    val mcif2img_dat_rd_rsp_valid = Wire(Bool())

    val reg2dp_arb_weight = Wire(UInt(4.W))
    val reg2dp_arb_wmb = Wire(UInt(4.W))
    val reg2dp_batch_stride = Wire(UInt(32.W))
    val reg2dp_batches = Wire(UInt(5.W))
    val reg2dp_byte_per_kernel = Wire(UInt(18.W))
    val reg2dp_conv_mode = Wire(Bool())
    val reg2dp_conv_x_stride = Wire(UInt(3.W))
    val reg2dp_conv_y_stride = Wire(UInt(3.W))
    val reg2dp_cvt_en = Wire(Bool())
    val reg2dp_cvt_offset = Wire(UInt(16.W))
    val reg2dp_cvt_scale = Wire(UInt(16.W))
    val reg2dp_cvt_truncate = Wire(UInt(6.W))
    val reg2dp_cya = Wire(UInt(32.W))
    val reg2dp_data_bank = Wire(UInt(5.W))
    val reg2dp_data_reuse = Wire(Bool())
    val reg2dp_datain_addr_high_0 = Wire(UInt(32.W))
    val reg2dp_datain_addr_high_1 = Wire(UInt(32.W))
    val reg2dp_datain_addr_low_0 = Wire(UInt(32.W))
    val reg2dp_datain_addr_low_1 = Wire(UInt(32.W))
    val reg2dp_datain_channel = Wire(UInt(13.W))
    val reg2dp_datain_format = Wire(Bool())
    val reg2dp_datain_height = Wire(UInt(13.W))
    val reg2dp_datain_height_ext = Wire(UInt(13.W))
    val reg2dp_datain_ram_type = Wire(Bool())
    val reg2dp_datain_width = Wire(UInt(13.W))
    val reg2dp_datain_width_ext = Wire(UInt(13.W))
    val reg2dp_dma_en = Wire(Bool())
    val reg2dp_entries = Wire(UInt(14.W))
    val reg2dp_grains = Wire(UInt(12.W))
    val reg2dp_in_precision = Wire(UInt(2.W))
    val reg2dp_line_packed = Wire(Bool())
    val reg2dp_line_stride = Wire(UInt(32.W))
    val reg2dp_mean_ax = Wire(UInt(16.W))
    val reg2dp_mean_bv = Wire(UInt(16.W))
    val reg2dp_mean_format = Wire(Bool())
    val reg2dp_mean_gu = Wire(UInt(16.W))
    val reg2dp_mean_ry = Wire(UInt(16.W))
    val reg2dp_nan_to_zero = Wire(Bool())
    val reg2dp_op_en = Wire(Bool())
    val reg2dp_pad_bottom = Wire(UInt(6.W))
    val reg2dp_pad_left = Wire(UInt(5.W))
    val reg2dp_pad_right = Wire(UInt(6.W))
    val reg2dp_pad_top = Wire(UInt(5.W))
    val reg2dp_pad_value = Wire(UInt(16.W))
    val reg2dp_pixel_format = Wire(UInt(6.W))
    val reg2dp_pixel_mapping = Wire(Bool())
    val reg2dp_pixel_sign_override = Wire(Bool())
    val reg2dp_pixel_x_offset = Wire(UInt(5.W))
    val reg2dp_pixel_y_offset = Wire(UInt(3.W))
    val reg2dp_proc_precision = Wire(UInt(2.W))
    val reg2dp_rsv_height = Wire(UInt(3.W))
    val reg2dp_rsv_per_line = Wire(UInt(10.W))
    val reg2dp_rsv_per_uv_line = Wire(UInt(10.W))
    val reg2dp_rsv_y_index = Wire(UInt(5.W))
    val reg2dp_skip_data_rls = Wire(Bool())
    val reg2dp_skip_weight_rls = Wire(Bool())
    val reg2dp_surf_packed = Wire(Bool())
    val reg2dp_surf_stride = Wire(UInt(32.W))
    val reg2dp_uv_line_stride = Wire(UInt(32.W))
    val reg2dp_weight_addr_high = Wire(UInt(32.W))
    val reg2dp_weight_addr_low = Wire(UInt(32.W))
    val reg2dp_weight_bank = Wire(UInt(5.W))
    val reg2dp_weight_bytes = Wire(UInt(32.W))
    val reg2dp_weight_format = Wire(Bool())
    val reg2dp_weight_kernel = Wire(UInt(13.W))
    val reg2dp_weight_ram_type = Wire(Bool())
    val reg2dp_weight_reuse = Wire(Bool())
    val reg2dp_wgs_addr_high = Wire(UInt(32.W))
    val reg2dp_wgs_addr_low = Wire(UInt(32.W))
    val reg2dp_wmb_addr_high = Wire(UInt(32.W))
    val reg2dp_wmb_addr_low = Wire(UInt(32.W))
    val reg2dp_wmb_bytes = Wire(UInt(28.W))

    val slcg_dc_gate_img = Wire(Bool())
    val slcg_dc_gate_wg = Wire(Bool())
    val slcg_hls_en = Wire(Bool())
    val slcg_img_gate_dc = Wire(Bool())
    val slcg_img_gate_wg = Wire(Bool())
    val slcg_op_en = Wire(UInt(8.W))
    val slcg_wg_gate_dc = Wire(Bool())
    val slcg_wg_gate_img = Wire(Bool())
    val status2dma_free_entries = Wire(UInt(15.W))
    val status2dma_fsm_switch = Wire(Bool())
    val status2dma_valid_slices = Wire(UInt(14.W))
    val status2dma_wr_idx = Wire(UInt(15.W))

    val wt2status_state = Wire(UInt(2.W))

    val nvdla_hls_gated_clk_cvt = Wire(Clock())
    val nvdla_op_gated_clk_buffer = Wire(Clock())
    val nvdla_op_gated_clk_cvt = Wire(Clock())
    val nvdla_op_gated_clk_dc = Wire(Clock())
    val nvdla_op_gated_clk_img = Wire(Clock())
    val nvdla_op_gated_clk_mux = Wire(Clock())
    val nvdla_op_gated_clk_wt = Wire(Clock())


    //==========================================================
    // Regfile
    //==========================================================

    val u_regfile = Module(new NV_NVDLA_CDMA_regfile)

    u_regfile.io.nvdla_core_clk := io.nvdla_core_clk                //|< i

    u_regfile.io.csb2cdma_req_pd := io.csb2cdma_req_pd       //|< i
    u_regfile.io.csb2cdma_req_pvld := io.csb2cdma_req_pvld           //|< i

    u_regfile.io.dp2reg_dat_flush_done := dp2reg_dat_flush_done         
    u_regfile.io.dp2reg_dc_rd_latency := dp2reg_dc_rd_latency     
    u_regfile.io.dp2reg_dc_rd_stall := dp2reg_dc_rd_stall      
    u_regfile.io.dp2reg_done := dp2reg_done                
    u_regfile.io.dp2reg_img_rd_latency := dp2reg_img_rd_latency
    u_regfile.io.dp2reg_img_rd_stall := dp2reg_img_rd_stall
    u_regfile.io.dp2reg_inf_data_num := dp2reg_inf_data_num     
    u_regfile.io.dp2reg_inf_weight_num := dp2reg_inf_weight_num
    u_regfile.io.dp2reg_nan_data_num := dp2reg_nan_data_num     
    u_regfile.io.dp2reg_nan_weight_num := dp2reg_nan_weight_num   
    u_regfile.io.dp2reg_wg_rd_latency := dp2reg_wg_rd_latency     
    u_regfile.io.dp2reg_wg_rd_stall := dp2reg_wg_rd_stall     
    u_regfile.io.dp2reg_wt_flush_done := dp2reg_wt_flush_done          
    u_regfile.io.dp2reg_wt_rd_latency := dp2reg_wt_rd_latency    
    u_regfile.io.dp2reg_wt_rd_stall := dp2reg_wt_rd_stall

    io.cdma2csb_resp_pd := u_regfile.io.cdma2csb_resp_pd      //|> o
    io.cdma2csb_resp_valid := u_regfile.io.cdma2csb_resp_valid         //|> o
    io.csb2cdma_req_prdy := u_regfile.io.csb2cdma_req_prdy           //|> o

    dp2reg_consumer := u_regfile.io.dp2reg_consumer
    reg2dp_arb_weight := u_regfile.io.reg2dp_arb_weight   
    reg2dp_arb_wmb := u_regfile.io.reg2dp_arb_wmb         
    reg2dp_batch_stride := u_regfile.io.reg2dp_batch_stride      
    reg2dp_batches := u_regfile.io.reg2dp_batches       
    reg2dp_byte_per_kernel := u_regfile.io.reg2dp_byte_per_kernel    
    reg2dp_conv_mode := u_regfile.io.reg2dp_conv_mode               
    reg2dp_conv_x_stride := u_regfile.io.reg2dp_conv_x_stride       
    reg2dp_conv_y_stride := u_regfile.io.reg2dp_conv_y_stride       
    reg2dp_cvt_en := u_regfile.io.reg2dp_cvt_en                
    reg2dp_cvt_offset := u_regfile.io.reg2dp_cvt_offset         
    reg2dp_cvt_scale := u_regfile.io.reg2dp_cvt_scale          
    reg2dp_cvt_truncate := u_regfile.io.reg2dp_cvt_truncate        
    reg2dp_cya := u_regfile.io.reg2dp_cya               
    reg2dp_data_bank := u_regfile.io.reg2dp_data_bank           
    reg2dp_data_reuse := u_regfile.io.reg2dp_data_reuse         
    reg2dp_datain_addr_high_0 := u_regfile.io.reg2dp_datain_addr_high_0 
    reg2dp_datain_addr_high_1 := u_regfile.io.reg2dp_datain_addr_high_1 
    reg2dp_datain_addr_low_0 := u_regfile.io.reg2dp_datain_addr_low_0 
    reg2dp_datain_addr_low_1 := u_regfile.io.reg2dp_datain_addr_low_1  
    reg2dp_datain_channel := u_regfile.io.reg2dp_datain_channel     
    reg2dp_datain_format := u_regfile.io.reg2dp_datain_format            
    reg2dp_datain_height := u_regfile.io.reg2dp_datain_height     
    reg2dp_datain_height_ext := u_regfile.io.reg2dp_datain_height_ext 
    reg2dp_datain_ram_type := u_regfile.io.reg2dp_datain_ram_type          
    reg2dp_datain_width := u_regfile.io.reg2dp_datain_width       
    reg2dp_datain_width_ext := u_regfile.io.reg2dp_datain_width_ext   
    reg2dp_dma_en := u_regfile.io.reg2dp_dma_en                
    reg2dp_entries := u_regfile.io.reg2dp_entries         
    reg2dp_grains := u_regfile.io.reg2dp_grains          
    reg2dp_in_precision := u_regfile.io.reg2dp_in_precision   
    reg2dp_line_packed := u_regfile.io.reg2dp_line_packed           
    reg2dp_line_stride := u_regfile.io.reg2dp_line_stride   
    reg2dp_mean_ax := u_regfile.io.reg2dp_mean_ax        
    reg2dp_mean_bv := u_regfile.io.reg2dp_mean_bv       
    reg2dp_mean_format := u_regfile.io.reg2dp_mean_format           
    reg2dp_mean_gu := u_regfile.io.reg2dp_mean_gu        
    reg2dp_mean_ry := u_regfile.io.reg2dp_mean_ry        
    reg2dp_nan_to_zero := u_regfile.io.reg2dp_nan_to_zero           
    reg2dp_op_en := u_regfile.io.reg2dp_op_en              
    reg2dp_pad_bottom := u_regfile.io.reg2dp_pad_bottom      
    reg2dp_pad_left := u_regfile.io.reg2dp_pad_left       
    reg2dp_pad_right := u_regfile.io.reg2dp_pad_right       
    reg2dp_pad_top  := u_regfile.io.reg2dp_pad_top         
    reg2dp_pad_value := u_regfile.io.reg2dp_pad_value     
    reg2dp_pixel_format := u_regfile.io.reg2dp_pixel_format    
    reg2dp_pixel_mapping := u_regfile.io.reg2dp_pixel_mapping         
    reg2dp_pixel_sign_override := u_regfile.io.reg2dp_pixel_sign_override  
    reg2dp_pixel_x_offset := u_regfile.io.reg2dp_pixel_x_offset
    reg2dp_pixel_y_offset := u_regfile.io.reg2dp_pixel_y_offset   
    reg2dp_proc_precision := u_regfile.io.reg2dp_proc_precision 
    reg2dp_rsv_height := u_regfile.io.reg2dp_rsv_height       
    reg2dp_rsv_per_line := u_regfile.io.reg2dp_rsv_per_line    
    reg2dp_rsv_per_uv_line := u_regfile.io.reg2dp_rsv_per_uv_line 
    reg2dp_rsv_y_index := u_regfile.io.reg2dp_rsv_y_index     
    reg2dp_skip_data_rls := u_regfile.io.reg2dp_skip_data_rls         
    reg2dp_skip_weight_rls := u_regfile.io.reg2dp_skip_weight_rls       
    reg2dp_surf_packed := u_regfile.io.reg2dp_surf_packed           
    reg2dp_surf_stride := u_regfile.io.reg2dp_surf_stride     
    reg2dp_uv_line_stride := u_regfile.io.reg2dp_uv_line_stride 
    reg2dp_weight_addr_high := u_regfile.io.reg2dp_weight_addr_high
    reg2dp_weight_addr_low := u_regfile.io.reg2dp_weight_addr_low
    reg2dp_weight_bank := u_regfile.io.reg2dp_weight_bank      
    reg2dp_weight_bytes := u_regfile.io.reg2dp_weight_bytes
    reg2dp_weight_format := u_regfile.io.reg2dp_weight_format        
    reg2dp_weight_kernel := u_regfile.io.reg2dp_weight_kernel 
    reg2dp_weight_ram_type := u_regfile.io.reg2dp_weight_ram_type      
    reg2dp_weight_reuse := u_regfile.io.reg2dp_weight_reuse          
    reg2dp_wgs_addr_high := u_regfile.io.reg2dp_wgs_addr_high  
    reg2dp_wgs_addr_low := u_regfile.io.reg2dp_wgs_addr_low   
    reg2dp_wmb_addr_high := u_regfile.io.reg2dp_wmb_addr_high  
    reg2dp_wmb_addr_low := u_regfile.io.reg2dp_wmb_addr_low  
    reg2dp_wmb_bytes := u_regfile.io.reg2dp_wmb_bytes

    slcg_op_en := u_regfile.io.slcg_op_en              


    //==========================================================
    // Weight DMA
    //==========================================================

    val u_wt = Module(new NV_NVDLA_CDMA_wt)

    u_wt.io.nvdla_core_clk := nvdla_op_gated_clk_wt
    u_wt.io.nvdla_core_ng_clk := io.nvdla_core_clk
    u_wt.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    io.cdma_wt2mcif_rd_req_valid := u_wt.io.cdma_wt2mcif_rd_req_valid
    u_wt.io.cdma_wt2mcif_rd_req_ready := io.cdma_wt2mcif_rd_req_ready
    io.cdma_wt2mcif_rd_req_pd := u_wt.io.cdma_wt2mcif_rd_req_pd
    u_wt.io.mcif2cdma_wt_rd_rsp_valid := io.mcif2cdma_wt_rd_rsp_valid
    io.mcif2cdma_wt_rd_rsp_ready := u_wt.io.mcif2cdma_wt_rd_rsp_ready
    u_wt.io.mcif2cdma_wt_rd_rsp_pd := io.mcif2cdma_wt_rd_rsp_pd

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.cdma_wt2cvif_rd_req_valid.get := u_wt.io.cdma_wt2cvif_rd_req_valid.get
        u_wt.io.cdma_wt2cvif_rd_req_ready.get := io.cdma_wt2cvif_rd_req_ready.get
        io.cdma_wt2cvif_rd_req_pd.get := u_wt.io.cdma_wt2cvif_rd_req_pd.get
        u_wt.io.cvif2cdma_wt_rd_rsp_valid.get := io.cvif2cdma_wt_rd_rsp_valid.get
        io.cvif2cdma_wt_rd_rsp_ready.get := u_wt.io.cvif2cdma_wt_rd_rsp_ready.get
        u_wt.io.cvif2cdma_wt_rd_rsp_pd.get := io.cvif2cdma_wt_rd_rsp_pd.get
    }
    io.cdma2buf_wt_wr_en := u_wt.io.cdma2buf_wt_wr_en
    if(conf.DMAIF < conf.ATMC){
        io.cdma2buf_wt_wr_sel.get := u_wt.io.cdma2buf_wt_wr_sel.get
    }
    io.cdma2buf_wt_wr_addr := u_wt.io.cdma2buf_wt_wr_addr
    io.cdma2buf_wt_wr_data := u_wt.io.cdma2buf_wt_wr_data
    u_wt.io.status2dma_fsm_switch := status2dma_fsm_switch

    wt2status_state := u_wt.io.wt2status_state
    io.cdma2sc_wt_updt := u_wt.io.cdma2sc_wt_updt
    io.cdma2sc_wt_kernels := u_wt.io.cdma2sc_wt_kernels
    io.cdma2sc_wt_entries := u_wt.io.cdma2sc_wt_entries
    cdma2sc_wmb_entries_f := u_wt.io.cdma2sc_wmb_entries

    u_wt.io.sc2cdma_wt_updt := io.sc2cdma_wt_updt
    u_wt.io.sc2cdma_wt_kernels := io.sc2cdma_wt_kernels
    u_wt.io.sc2cdma_wt_entries := io.sc2cdma_wt_entries
    u_wt.io.sc2cdma_wmb_entries := io.sc2cdma_wmb_entries

    u_wt.io.sc2cdma_wt_pending_req := io.sc2cdma_wt_pending_req
    io.cdma2sc_wt_pending_ack := u_wt.io.cdma2sc_wt_pending_ack


    u_wt.io.reg2dp_arb_weight := reg2dp_arb_weight
    u_wt.io.reg2dp_arb_wmb := reg2dp_arb_wmb
    u_wt.io.reg2dp_op_en := reg2dp_op_en
    u_wt.io.reg2dp_proc_precision := reg2dp_proc_precision
    u_wt.io.reg2dp_weight_reuse := reg2dp_weight_reuse
    u_wt.io.reg2dp_skip_weight_rls := reg2dp_skip_weight_rls
    u_wt.io.reg2dp_weight_format := reg2dp_weight_format
    u_wt.io.reg2dp_byte_per_kernel := reg2dp_byte_per_kernel
    u_wt.io.reg2dp_weight_kernel := reg2dp_weight_kernel
    u_wt.io.reg2dp_weight_ram_type := reg2dp_weight_ram_type
    u_wt.io.reg2dp_weight_addr_low := reg2dp_weight_addr_low(31, conf.ATMMBW)
    u_wt.io.reg2dp_wgs_addr_low := reg2dp_wgs_addr_low(31, conf.ATMMBW)
    u_wt.io.reg2dp_wmb_addr_low := reg2dp_wmb_addr_low(31, conf.ATMMBW)
    u_wt.io.reg2dp_weight_addr_high := reg2dp_weight_addr_high
    u_wt.io.reg2dp_weight_bytes := reg2dp_weight_bytes
    u_wt.io.reg2dp_wgs_addr_high := reg2dp_wgs_addr_high
    u_wt.io.reg2dp_wmb_addr_high := reg2dp_wmb_addr_high
    u_wt.io.reg2dp_wmb_bytes := reg2dp_wmb_bytes
    u_wt.io.reg2dp_data_bank := reg2dp_data_bank
    u_wt.io.reg2dp_weight_bank := reg2dp_weight_bank
    u_wt.io.reg2dp_nan_to_zero := reg2dp_nan_to_zero
    u_wt.io.reg2dp_dma_en := reg2dp_dma_en

    dp2reg_nan_weight_num := u_wt.io.dp2reg_nan_weight_num
    dp2reg_inf_weight_num := u_wt.io.dp2reg_inf_weight_num
    dp2reg_wt_flush_done := u_wt.io.dp2reg_wt_flush_done
    dp2reg_wt_rd_stall := u_wt.io.dp2reg_wt_rd_stall
    dp2reg_wt_rd_latency := u_wt.io.dp2reg_wt_rd_latency

    //==========================================================
    //-------------- SLCG for weight DMA --------------//
    //==========================================================
    val u_slcg_wt = Module(new NV_NVDLA_CDMA_slcg)

    u_slcg_wt.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
    u_slcg_wt.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
    u_slcg_wt.io.nvdla_core_clk := io.nvdla_core_clk

    u_slcg_wt.io.slcg_en_src_0 := slcg_op_en(0)
    u_slcg_wt.io.slcg_en_src_1 := true.B
    u_slcg_wt.io.slcg_en_src_2 := true.B
    u_slcg_wt.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

    nvdla_op_gated_clk_wt := u_slcg_wt.io.nvdla_core_gated_clk  

    //==========================================================
    // Direct convolution DMA
    //==========================================================
     val u_dc = Module(new NV_NVDLA_CDMA_dc)

    u_dc.io.nvdla_core_clk := nvdla_op_gated_clk_dc
    u_dc.io.nvdla_core_ng_clk := io.nvdla_core_clk
    u_dc.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    dc_dat2mcif_rd_req_valid := u_dc.io.dc_dat2mcif_rd_req_valid
    u_dc.io.dc_dat2mcif_rd_req_ready := dc_dat2mcif_rd_req_ready
    dc_dat2mcif_rd_req_pd := u_dc.io.dc_dat2mcif_rd_req_pd
    u_dc.io.mcif2dc_dat_rd_rsp_valid := mcif2dc_dat_rd_rsp_valid
    mcif2dc_dat_rd_rsp_ready := u_dc.io.mcif2dc_dat_rd_rsp_ready
    u_dc.io.mcif2dc_dat_rd_rsp_pd := mcif2dc_dat_rd_rsp_pd

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        dc_dat2cvif_rd_req_valid.get := u_dc.io.dc_dat2cvif_rd_req_valid.get
        u_dc.io.dc_dat2cvif_rd_req_ready.get := dc_dat2cvif_rd_req_ready.get
        dc_dat2cvif_rd_req_pd.get := u_dc.io.dc_dat2cvif_rd_req_pd.get
        u_dc.io.cvif2dc_dat_rd_rsp_valid.get := cvif2dc_dat_rd_rsp_valid.get
        cvif2dc_dat_rd_rsp_ready.get := u_dc.io.cvif2dc_dat_rd_rsp_ready.get
        u_dc.io.cvif2dc_dat_rd_rsp_pd.get := cvif2dc_dat_rd_rsp_pd.get
    }
    dc2cvt_dat_wr_en := u_dc.io.dc2cvt_dat_wr_en
    if(conf.DMAIF < conf.ATMC){
        dc2cvt_dat_wr_sel.get := u_dc.io.dc2cvt_dat_wr_sel.get
    }
    dc2cvt_dat_wr_addr := u_dc.io.dc2cvt_dat_wr_addr
    dc2cvt_dat_wr_data := u_dc.io.dc2cvt_dat_wr_data
    dc2cvt_dat_wr_info_pd := u_dc.io.dc2cvt_dat_wr_info_pd

    dc2status_state := u_dc.io.dc2status_state
    dc2status_dat_updt := u_dc.io.dc2status_dat_updt
    dc2status_dat_entries := u_dc.io.dc2status_dat_entries
    dc2status_dat_slices := u_dc.io.dc2status_dat_slices

    u_dc.io.status2dma_fsm_switch := status2dma_fsm_switch
    u_dc.io.status2dma_valid_slices := status2dma_valid_slices
    u_dc.io.status2dma_free_entries := status2dma_free_entries
    u_dc.io.status2dma_wr_idx := status2dma_wr_idx

    dc2sbuf_p0_wr_en := u_dc.io.dc2sbuf_p0_wr_en
    dc2sbuf_p0_wr_addr := u_dc.io.dc2sbuf_p0_wr_addr
    dc2sbuf_p0_wr_data := u_dc.io.dc2sbuf_p0_wr_data
    dc2sbuf_p0_rd_en := u_dc.io.dc2sbuf_p0_rd_en
    dc2sbuf_p0_rd_addr := u_dc.io.dc2sbuf_p0_rd_addr
    u_dc.io.dc2sbuf_p0_rd_data := dc2sbuf_p0_rd_data

    u_dc.io.sc2cdma_dat_pending_req := io.sc2cdma_dat_pending_req

    u_dc.io.reg2dp_op_en := reg2dp_op_en
    u_dc.io.reg2dp_conv_mode := reg2dp_conv_mode
    u_dc.io.reg2dp_data_reuse := reg2dp_data_reuse
    u_dc.io.reg2dp_skip_data_rls := reg2dp_skip_data_rls
    u_dc.io.reg2dp_datain_format := reg2dp_datain_format
    u_dc.io.reg2dp_datain_width := reg2dp_datain_width
    u_dc.io.reg2dp_datain_height := reg2dp_datain_height
    u_dc.io.reg2dp_datain_channel := reg2dp_datain_channel
    u_dc.io.reg2dp_datain_ram_type := reg2dp_datain_ram_type
    u_dc.io.reg2dp_datain_addr_high_0 := reg2dp_datain_addr_high_0
    u_dc.io.reg2dp_datain_addr_low_0 := reg2dp_datain_addr_low_0(31-conf.ATMMBW, 0)
    u_dc.io.reg2dp_line_stride := reg2dp_line_stride(31-conf.ATMMBW, 0)
    u_dc.io.reg2dp_surf_stride := reg2dp_surf_stride(31-conf.ATMMBW, 0)
    u_dc.io.reg2dp_batch_stride := reg2dp_batch_stride(31-conf.ATMMBW, 0)
    u_dc.io.reg2dp_line_packed := reg2dp_line_packed
    u_dc.io.reg2dp_surf_packed := reg2dp_surf_packed
    u_dc.io.reg2dp_batches := reg2dp_batches
    u_dc.io.reg2dp_entries := reg2dp_entries
    u_dc.io.reg2dp_grains := reg2dp_grains
    u_dc.io.reg2dp_data_bank := reg2dp_data_bank
    u_dc.io.reg2dp_dma_en := reg2dp_dma_en

    slcg_dc_gate_wg := u_dc.io.slcg_dc_gate_wg
    slcg_dc_gate_img := u_dc.io.slcg_dc_gate_img
    dp2reg_dc_rd_stall := u_dc.io.dp2reg_dc_rd_stall
    dp2reg_dc_rd_latency := u_dc.io.dp2reg_dc_rd_latency

    //==========================================================
    //-------------- SLCG for DC DMA --------------//
    //==========================================================
    val u_slcg_dc = Module(new NV_NVDLA_CDMA_slcg)

    u_slcg_dc.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
    u_slcg_dc.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
    u_slcg_dc.io.nvdla_core_clk := io.nvdla_core_clk

    u_slcg_dc.io.slcg_en_src_0 := slcg_op_en(1)
    u_slcg_dc.io.slcg_en_src_1 := slcg_wg_gate_dc
    u_slcg_dc.io.slcg_en_src_2 := slcg_img_gate_dc

    u_slcg_dc.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

    nvdla_op_gated_clk_dc := u_slcg_dc.io.nvdla_core_gated_clk  

    //==========================================================
    //-------------- SLCG for WG DMA --------------//
    //==========================================================
    slcg_wg_gate_dc := true.B
    slcg_wg_gate_img := true.B
    dp2reg_wg_rd_latency := "b0".asUInt(32.W)
    dp2reg_wg_rd_stall := "b0".asUInt(32.W)

    //==========================================================
    // Image convolution DMA
    //==========================================================
    val u_img = Module(new NV_NVDLA_CDMA_img)

    u_img.io.nvdla_core_clk := nvdla_op_gated_clk_img
    u_img.io.nvdla_core_ng_clk := io.nvdla_core_clk
    u_img.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    img_dat2mcif_rd_req_valid := u_img.io.img_dat2mcif_rd_req_valid
    u_img.io.img_dat2mcif_rd_req_ready := img_dat2mcif_rd_req_ready
    img_dat2mcif_rd_req_pd := u_img.io.img_dat2mcif_rd_req_pd
    u_img.io.mcif2img_dat_rd_rsp_valid := mcif2img_dat_rd_rsp_valid
    mcif2img_dat_rd_rsp_ready := u_img.io.mcif2img_dat_rd_rsp_ready
    u_img.io.mcif2img_dat_rd_rsp_pd := mcif2img_dat_rd_rsp_pd

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        img_dat2cvif_rd_req_valid.get := u_img.io.img_dat2cvif_rd_req_valid.get
        u_img.io.img_dat2cvif_rd_req_ready.get := img_dat2cvif_rd_req_ready.get
        img_dat2cvif_rd_req_pd.get := u_img.io.img_dat2cvif_rd_req_pd.get
        u_img.io.cvif2img_dat_rd_rsp_valid.get := cvif2img_dat_rd_rsp_valid.get
        cvif2img_dat_rd_rsp_ready.get := u_img.io.cvif2img_dat_rd_rsp_ready.get
        u_img.io.cvif2img_dat_rd_rsp_pd.get := cvif2img_dat_rd_rsp_pd.get
    }
    img2cvt_dat_wr_en := u_img.io.img2cvt_dat_wr_en
    if(conf.DMAIF < conf.ATMC){
        img2cvt_dat_wr_sel.get := u_img.io.img2cvt_dat_wr_sel.get
    }
    img2cvt_dat_wr_addr := u_img.io.img2cvt_dat_wr_addr
    img2cvt_dat_wr_data := u_img.io.img2cvt_dat_wr_data
    img2cvt_mn_wr_data := u_img.io.img2cvt_mn_wr_data
    img2cvt_dat_wr_pad_mask := u_img.io.img2cvt_dat_wr_pad_mask
    img2cvt_dat_wr_info_pd := u_img.io.img2cvt_dat_wr_info_pd

    img2status_state := u_img.io.img2status_state
    img2status_dat_updt := u_img.io.img2status_dat_updt
    img2status_dat_entries := u_img.io.img2status_dat_entries
    img2status_dat_slices := u_img.io.img2status_dat_slices

    u_img.io.status2dma_valid_slices := status2dma_valid_slices
    u_img.io.status2dma_free_entries := status2dma_free_entries
    u_img.io.status2dma_wr_idx := status2dma_wr_idx
    u_img.io.status2dma_fsm_switch := status2dma_fsm_switch

    img2sbuf_p0_wr_en := u_img.io.img2sbuf_p0_wr_en
    img2sbuf_p0_wr_addr := u_img.io.img2sbuf_p0_wr_addr
    img2sbuf_p0_wr_data := u_img.io.img2sbuf_p0_wr_data
    img2sbuf_p0_rd_en := u_img.io.img2sbuf_p0_rd_en
    img2sbuf_p0_rd_addr := u_img.io.img2sbuf_p0_rd_addr
    u_img.io.img2sbuf_p0_rd_data := img2sbuf_p0_rd_data

    u_img.io.sc2cdma_dat_pending_req := io.sc2cdma_dat_pending_req

    u_img.io.reg2dp_op_en := reg2dp_op_en
    u_img.io.reg2dp_conv_mode := reg2dp_conv_mode
    u_img.io.reg2dp_in_precision := reg2dp_in_precision
    u_img.io.reg2dp_proc_precision := reg2dp_proc_precision
    u_img.io.reg2dp_data_reuse := reg2dp_data_reuse
    u_img.io.reg2dp_skip_data_rls := reg2dp_skip_data_rls
    u_img.io.reg2dp_datain_format := reg2dp_datain_format
    u_img.io.reg2dp_pixel_format := reg2dp_pixel_format
    u_img.io.reg2dp_pixel_mapping := reg2dp_pixel_mapping
    u_img.io.reg2dp_pixel_sign_override := reg2dp_pixel_sign_override
    u_img.io.reg2dp_datain_width := reg2dp_datain_width
    u_img.io.reg2dp_datain_height := reg2dp_datain_height
    u_img.io.reg2dp_datain_channel := reg2dp_datain_channel
    u_img.io.reg2dp_pixel_x_offset := reg2dp_pixel_x_offset
    u_img.io.reg2dp_pixel_y_offset := reg2dp_pixel_y_offset
    u_img.io.reg2dp_datain_ram_type := reg2dp_datain_ram_type
    u_img.io.reg2dp_datain_addr_high_0 := reg2dp_datain_addr_high_0
    u_img.io.reg2dp_datain_addr_low_0 := reg2dp_datain_addr_low_0
    u_img.io.reg2dp_datain_addr_low_1 := reg2dp_datain_addr_low_1
    u_img.io.reg2dp_line_stride := reg2dp_line_stride
    u_img.io.reg2dp_uv_line_stride := reg2dp_uv_line_stride
    u_img.io.reg2dp_datain_addr_high_1 := reg2dp_datain_addr_high_1
    u_img.io.reg2dp_mean_format := reg2dp_mean_format
    u_img.io.reg2dp_mean_ry := reg2dp_mean_ry
    u_img.io.reg2dp_mean_gu := reg2dp_mean_gu
    u_img.io.reg2dp_mean_bv := reg2dp_mean_bv
    u_img.io.reg2dp_mean_ax := reg2dp_mean_ax
    u_img.io.reg2dp_entries := reg2dp_entries
    u_img.io.reg2dp_pad_left := reg2dp_pad_left
    u_img.io.reg2dp_pad_right := reg2dp_pad_right
    u_img.io.reg2dp_data_bank := reg2dp_data_bank
    u_img.io.reg2dp_dma_en := reg2dp_dma_en
    u_img.io.reg2dp_rsv_per_line := reg2dp_rsv_per_line
    u_img.io.reg2dp_rsv_per_uv_line := reg2dp_rsv_per_uv_line
    u_img.io.reg2dp_rsv_height := reg2dp_rsv_height
    u_img.io.reg2dp_rsv_y_index := reg2dp_rsv_y_index

    slcg_img_gate_dc := u_img.io.slcg_img_gate_dc
    slcg_img_gate_wg := u_img.io.slcg_img_gate_wg

    dp2reg_img_rd_stall := u_img.io.dp2reg_img_rd_stall
    dp2reg_img_rd_latency := u_img.io.dp2reg_img_rd_latency;

    val img2sbuf_p1_wr_en = false.B
    val img2sbuf_p1_wr_addr = "b0".asUInt(8.W)
    val img2sbuf_p1_wr_data = "b0".asUInt(64.W)
    val img2sbuf_p1_rd_en = false.B
    val img2sbuf_p1_rd_addr = "b0".asUInt(8.W)

    //==========================================================
    //-------------- SLCG for IMG DMA --------------//
    //==========================================================
    val u_slcg_img = Module(new NV_NVDLA_CDMA_slcg)

    u_slcg_img.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
    u_slcg_img.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
    u_slcg_img.io.nvdla_core_clk := io.nvdla_core_clk

    u_slcg_img.io.slcg_en_src_0 := slcg_op_en(3)
    u_slcg_img.io.slcg_en_src_1 := slcg_dc_gate_img
    u_slcg_img.io.slcg_en_src_2 := slcg_wg_gate_img

    u_slcg_img.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

    nvdla_op_gated_clk_img := u_slcg_img.io.nvdla_core_gated_clk


    //==========================================================
    //-------------- DMA mux--------------//
    //==========================================================
    val u_dma_mux = Module(new NV_NVDLA_CDMA_dma_mux)

    u_dma_mux.io.nvdla_core_clk := nvdla_op_gated_clk_mux

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_dma_mux.io.dc_dat2cvif_rd_req_valid.get := dc_dat2cvif_rd_req_valid.get
        dc_dat2cvif_rd_req_ready.get := u_dma_mux.io.dc_dat2cvif_rd_req_ready.get
        u_dma_mux.io.dc_dat2cvif_rd_req_pd.get := dc_dat2cvif_rd_req_pd.get
        cvif2dc_dat_rd_rsp_valid.get := u_dma_mux.io.cvif2dc_dat_rd_rsp_valid.get
        u_dma_mux.io.cvif2dc_dat_rd_rsp_ready.get := cvif2dc_dat_rd_rsp_ready.get
        cvif2dc_dat_rd_rsp_pd.get := u_dma_mux.io.cvif2dc_dat_rd_rsp_pd.get

        u_dma_mux.io.img_dat2cvif_rd_req_valid.get := img_dat2cvif_rd_req_valid.get
        img_dat2cvif_rd_req_ready.get := u_dma_mux.io.img_dat2cvif_rd_req_ready.get
        u_dma_mux.io.img_dat2cvif_rd_req_pd.get := img_dat2cvif_rd_req_pd.get
        cvif2img_dat_rd_rsp_valid.get := u_dma_mux.io.cvif2img_dat_rd_rsp_valid.get
        u_dma_mux.io.cvif2img_dat_rd_rsp_ready.get := cvif2img_dat_rd_rsp_ready.get
        cvif2img_dat_rd_rsp_pd.get := u_dma_mux.io.cvif2img_dat_rd_rsp_pd.get

        io.cdma_dat2cvif_rd_req_valid.get := u_dma_mux.io.cdma_dat2cvif_rd_req_valid.get
        u_dma_mux.io.cdma_dat2cvif_rd_req_ready.get := io.cdma_dat2cvif_rd_req_ready.get
        io.cdma_dat2cvif_rd_req_pd.get := u_dma_mux.io.cdma_dat2cvif_rd_req_pd.get
        u_dma_mux.io.cvif2cdma_dat_rd_rsp_valid.get := io.cvif2cdma_dat_rd_rsp_valid.get
        io.cvif2cdma_dat_rd_rsp_ready.get := u_dma_mux.io.cvif2cdma_dat_rd_rsp_ready.get
        u_dma_mux.io.cvif2cdma_dat_rd_rsp_pd.get := io.cvif2cdma_dat_rd_rsp_pd.get

    }
    
    u_dma_mux.io.dc_dat2mcif_rd_req_valid := dc_dat2mcif_rd_req_valid
    dc_dat2mcif_rd_req_ready := u_dma_mux.io.dc_dat2mcif_rd_req_ready
    u_dma_mux.io.dc_dat2mcif_rd_req_pd := dc_dat2mcif_rd_req_pd
    mcif2dc_dat_rd_rsp_valid := u_dma_mux.io.mcif2dc_dat_rd_rsp_valid
    u_dma_mux.io.mcif2dc_dat_rd_rsp_ready := mcif2dc_dat_rd_rsp_ready
    mcif2dc_dat_rd_rsp_pd := u_dma_mux.io.mcif2dc_dat_rd_rsp_pd

    u_dma_mux.io.img_dat2mcif_rd_req_valid := img_dat2mcif_rd_req_valid
    img_dat2mcif_rd_req_ready := u_dma_mux.io.img_dat2mcif_rd_req_ready
    u_dma_mux.io.img_dat2mcif_rd_req_pd := img_dat2mcif_rd_req_pd
    mcif2img_dat_rd_rsp_valid := u_dma_mux.io.mcif2img_dat_rd_rsp_valid
    u_dma_mux.io.mcif2img_dat_rd_rsp_ready := mcif2img_dat_rd_rsp_ready
    mcif2img_dat_rd_rsp_pd := u_dma_mux.io.mcif2img_dat_rd_rsp_pd

    io.cdma_dat2mcif_rd_req_valid := u_dma_mux.io.cdma_dat2mcif_rd_req_valid
    u_dma_mux.io.cdma_dat2mcif_rd_req_ready := io.cdma_dat2mcif_rd_req_ready
    io.cdma_dat2mcif_rd_req_pd := u_dma_mux.io.cdma_dat2mcif_rd_req_pd
    u_dma_mux.io.mcif2cdma_dat_rd_rsp_valid := io.mcif2cdma_dat_rd_rsp_valid
    io.mcif2cdma_dat_rd_rsp_ready := u_dma_mux.io.mcif2cdma_dat_rd_rsp_ready
    u_dma_mux.io.mcif2cdma_dat_rd_rsp_pd := io.mcif2cdma_dat_rd_rsp_pd

    //==========================================================
    //-------------- SLCG for MUX  --------------//
    //==========================================================
    val u_slcg_mux = Module(new NV_NVDLA_CDMA_slcg)

    u_slcg_mux.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
    u_slcg_mux.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
    u_slcg_mux.io.nvdla_core_clk := io.nvdla_core_clk

    u_slcg_mux.io.slcg_en_src_0 := slcg_op_en(4)
    u_slcg_mux.io.slcg_en_src_1 := true.B
    u_slcg_mux.io.slcg_en_src_2 := true.B

    u_slcg_mux.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

    nvdla_op_gated_clk_mux := u_slcg_mux.io.nvdla_core_gated_clk

    //==========================================================
    //-------------- DMA data convertor--------------//
    //==========================================================
    val u_cvt = Module(new NV_NVDLA_CDMA_cvt)

    u_cvt.io.nvdla_core_clk := nvdla_op_gated_clk_cvt
    u_cvt.io.nvdla_hls_clk := nvdla_hls_gated_clk_cvt
    u_cvt.io.nvdla_core_ng_clk := io.nvdla_core_clk
    slcg_hls_en := u_cvt.io.slcg_hls_en 

    u_cvt.io.dc2cvt_dat_wr_en := dc2cvt_dat_wr_en
    if(conf.DMAIF < conf.ATMC){
        u_cvt.io.dc2cvt_dat_wr_sel.get := dc2cvt_dat_wr_sel.get
    }
    u_cvt.io.dc2cvt_dat_wr_addr := dc2cvt_dat_wr_addr
    u_cvt.io.dc2cvt_dat_wr_data := dc2cvt_dat_wr_data
    u_cvt.io.dc2cvt_dat_wr_info_pd := dc2cvt_dat_wr_info_pd
    //////////////// img
    u_cvt.io.img2cvt_dat_wr_en := img2cvt_dat_wr_en
    if(conf.DMAIF < conf.ATMC){
        u_cvt.io.img2cvt_dat_wr_sel.get := img2cvt_dat_wr_sel.get
    }
    u_cvt.io.img2cvt_dat_wr_addr := img2cvt_dat_wr_addr
    u_cvt.io.img2cvt_dat_wr_data := img2cvt_dat_wr_data
    u_cvt.io.img2cvt_mn_wr_data := img2cvt_mn_wr_data
    u_cvt.io.img2cvt_dat_wr_pad_mask := img2cvt_dat_wr_pad_mask
    u_cvt.io.img2cvt_dat_wr_info_pd := img2cvt_dat_wr_info_pd

    io.cdma2buf_dat_wr_en := u_cvt.io.cdma2buf_dat_wr_en
    if(conf.DMAIF < conf.ATMC){
        io.cdma2buf_dat_wr_sel.get := u_cvt.io.cdma2buf_dat_wr_sel.get
    }
    io.cdma2buf_dat_wr_addr := u_cvt.io.cdma2buf_dat_wr_addr
    io.cdma2buf_dat_wr_data := u_cvt.io.cdma2buf_dat_wr_data

    u_cvt.io.reg2dp_op_en := reg2dp_op_en
    u_cvt.io.reg2dp_in_precision := reg2dp_in_precision
    u_cvt.io.reg2dp_proc_precision := reg2dp_proc_precision
    u_cvt.io.reg2dp_cvt_en := reg2dp_cvt_en
    u_cvt.io.reg2dp_cvt_truncate := reg2dp_cvt_truncate
    u_cvt.io.reg2dp_cvt_offset := reg2dp_cvt_offset
    u_cvt.io.reg2dp_cvt_scale := reg2dp_cvt_scale
    u_cvt.io.reg2dp_nan_to_zero := reg2dp_nan_to_zero
    u_cvt.io.reg2dp_pad_value := reg2dp_pad_value
    u_cvt.io.dp2reg_done := dp2reg_done
    dp2reg_nan_data_num := u_cvt.io.dp2reg_nan_data_num
    dp2reg_inf_data_num := u_cvt.io.dp2reg_inf_data_num
    dp2reg_dat_flush_done := u_cvt.io.dp2reg_dat_flush_done

    //==========================================================
    //-------------- SLCG for CVT  --------------//
    //==========================================================

    val u_slcg_cvt = Module(new NV_NVDLA_CDMA_slcg)

    u_slcg_cvt.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
    u_slcg_cvt.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
    u_slcg_cvt.io.nvdla_core_clk := io.nvdla_core_clk

    u_slcg_cvt.io.slcg_en_src_0 := slcg_op_en(5)
    u_slcg_cvt.io.slcg_en_src_1 := true.B
    u_slcg_cvt.io.slcg_en_src_2 := true.B

    u_slcg_cvt.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

    nvdla_op_gated_clk_cvt := u_slcg_cvt.io.nvdla_core_gated_clk

    //==========================================================
    //-------------- SLCG for CVT HLS CELL  --------------//
    //==========================================================

    val u_slcg_hls = Module(new NV_NVDLA_CDMA_slcg)

    u_slcg_hls.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
    u_slcg_hls.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
    u_slcg_hls.io.nvdla_core_clk := io.nvdla_core_clk

    u_slcg_hls.io.slcg_en_src_0 := slcg_op_en(6)
    u_slcg_hls.io.slcg_en_src_1 := slcg_hls_en
    u_slcg_hls.io.slcg_en_src_2 := true.B

    u_slcg_hls.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

    nvdla_hls_gated_clk_cvt := u_slcg_hls.io.nvdla_core_gated_clk

    //==========================================================
    //-------------- Shared buffer  --------------//
    //==========================================================

    val u_shared_buffer = Module(new NV_NVDLA_CDMA_shared_buffer)
    u_shared_buffer.io.nvdla_core_clk := nvdla_op_gated_clk_buffer
    u_shared_buffer.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_shared_buffer.io.dc2sbuf_p0_wr_en := dc2sbuf_p0_wr_en
    u_shared_buffer.io.dc2sbuf_p0_wr_addr := dc2sbuf_p0_wr_addr
    u_shared_buffer.io.dc2sbuf_p0_wr_data := dc2sbuf_p0_wr_data
    u_shared_buffer.io.dc2sbuf_p0_rd_en := dc2sbuf_p0_rd_en
    u_shared_buffer.io.dc2sbuf_p0_rd_addr := dc2sbuf_p0_rd_addr
    dc2sbuf_p0_rd_data := u_shared_buffer.io.dc2sbuf_p0_rd_data

    u_shared_buffer.io.dc2sbuf_p1_wr_en := false.B
    u_shared_buffer.io.dc2sbuf_p1_wr_addr := "b0".asUInt(8.W)
    u_shared_buffer.io.dc2sbuf_p1_wr_data := "b0".asUInt((conf.NVDLA_MEMORY_ATOMIC_SIZE*conf.NVDLA_BPE).W)
    u_shared_buffer.io.dc2sbuf_p1_rd_en := false.B
    u_shared_buffer.io.dc2sbuf_p1_rd_addr := "b0".asUInt(8.W)

    u_shared_buffer.io.img2sbuf_p0_wr_en := img2sbuf_p0_wr_en
    u_shared_buffer.io.img2sbuf_p0_wr_addr := img2sbuf_p0_wr_addr
    u_shared_buffer.io.img2sbuf_p0_wr_data := img2sbuf_p0_wr_data
    u_shared_buffer.io.img2sbuf_p0_rd_en := img2sbuf_p0_rd_en
    u_shared_buffer.io.img2sbuf_p0_rd_addr := img2sbuf_p0_rd_addr
    img2sbuf_p0_rd_data := u_shared_buffer.io.img2sbuf_p0_rd_data

    u_shared_buffer.io.img2sbuf_p1_wr_en := img2sbuf_p1_wr_en
    u_shared_buffer.io.img2sbuf_p1_wr_addr := img2sbuf_p1_wr_addr
    u_shared_buffer.io.img2sbuf_p1_wr_data := img2sbuf_p1_wr_data
    u_shared_buffer.io.img2sbuf_p1_rd_en := img2sbuf_p1_rd_en
    u_shared_buffer.io.img2sbuf_p1_rd_addr := img2sbuf_p1_rd_addr

    //==========================================================
    //-------------- SLCG for shared buffer  --------------//
    //==========================================================

    val u_slcg_buffer = Module(new NV_NVDLA_CDMA_slcg)

    u_slcg_buffer.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
    u_slcg_buffer.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
    u_slcg_buffer.io.nvdla_core_clk := io.nvdla_core_clk

    u_slcg_buffer.io.slcg_en_src_0 := slcg_op_en(7)
    u_slcg_buffer.io.slcg_en_src_1 := true.B
    u_slcg_buffer.io.slcg_en_src_2 := true.B

    u_slcg_buffer.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

    nvdla_op_gated_clk_buffer := u_slcg_buffer.io.nvdla_core_gated_clk

    //==========================================================
    //-------------- CDMA status controller --------------//
    //==========================================================

    val u_status = Module(new NV_NVDLA_CDMA_status)
    u_status.io.nvdla_core_clk := io.nvdla_core_clk

    u_status.io.dc2status_dat_updt := dc2status_dat_updt
    u_status.io.dc2status_dat_entries := dc2status_dat_entries
    u_status.io.dc2status_dat_slices := dc2status_dat_slices

    u_status.io.img2status_dat_updt := img2status_dat_updt
    u_status.io.img2status_dat_entries := img2status_dat_entries
    u_status.io.img2status_dat_slices := img2status_dat_slices

    u_status.io.sc2cdma_dat_updt := io.sc2cdma_dat_updt
    u_status.io.sc2cdma_dat_entries := io.sc2cdma_dat_entries
    u_status.io.sc2cdma_dat_slices := io.sc2cdma_dat_slices

    io.cdma2sc_dat_updt := u_status.io.cdma2sc_dat_updt
    io.cdma2sc_dat_entries := u_status.io.cdma2sc_dat_entries
    io.cdma2sc_dat_slices := u_status.io.cdma2sc_dat_slices

    status2dma_valid_slices := u_status.io.status2dma_valid_slices
    status2dma_free_entries := u_status.io.status2dma_free_entries
    status2dma_wr_idx := u_status.io.status2dma_wr_idx

    u_status.io.dc2status_state := dc2status_state
    u_status.io.img2status_state := img2status_state
    u_status.io.wt2status_state := wt2status_state

    dp2reg_done := u_status.io.dp2reg_done
    status2dma_fsm_switch := u_status.io.status2dma_fsm_switch
    io.cdma_wt2glb_done_intr_pd := u_status.io.cdma_wt2glb_done_intr_pd
    io.cdma_dat2glb_done_intr_pd := u_status.io.cdma_dat2glb_done_intr_pd

    u_status.io.sc2cdma_dat_pending_req := io.sc2cdma_dat_pending_req
    io.cdma2sc_dat_pending_ack := u_status.io.cdma2sc_dat_pending_ack

    u_status.io.reg2dp_op_en := reg2dp_op_en
    u_status.io.reg2dp_data_bank := reg2dp_data_bank

    u_status.io.dp2reg_consumer := dp2reg_consumer


 

}


object NV_NVDLA_cdmaDriver extends App {
  implicit val conf: cdmaConfiguration = new cdmaConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_cdma())
}
