package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_CDMA_img(implicit conf: cdmaConfiguration) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_ng_clk = Input(Clock())

        val img_dat2mcif_rd_req_valid = Output(Bool())
        val img_dat2mcif_rd_req_ready = Input(Bool())
        val img_dat2mcif_rd_req_pd = Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
        val mcif2img_dat_rd_rsp_valid = Input(Bool())
        val mcif2img_dat_rd_rsp_ready = Output(Bool())
        val mcif2img_dat_rd_rsp_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))

        val img_dat2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val img_dat2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val img_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2img_dat_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cvif2img_dat_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2img_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None

        val img2cvt_dat_wr_en = Output(Bool())
        val img2cvt_dat_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Output(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W))) else None
        val img2cvt_dat_wr_addr = Output(UInt(17.W))
        val img2cvt_dat_wr_data = Output(UInt(conf.DMAIF.W))
        val img2cvt_mn_wr_data = Output(UInt((conf.BNUM*16).W))
        val img2cvt_dat_wr_pad_mask = Output(UInt(conf.BNUM.W))
        val img2cvt_dat_wr_info_pd = Output(UInt(12.W))

        val img2status_state = Output(UInt(2.W))
        val img2status_dat_updt = Output(Bool())
        val img2status_dat_entries = Output(UInt(15.W))
        val img2status_dat_slices = Output(UInt(14.W))
        val status2dma_valid_slices = Input(UInt(14.W))
        val status2dma_free_entries = Input(UInt(15.W))
        val status2dma_wr_idx = Input(UInt(15.W))
        val status2dma_fsm_switch = Input(Bool())

        val img2sbuf_p0_wr_en = Output(Bool())
        val img2sbuf_p0_wr_addr = Output(UInt(8.W))
        val img2sbuf_p0_wr_data = Output(UInt(conf.ATMM.W))
        val img2sbuf_p0_rd_en = Output(Bool())
        val img2sbuf_p0_rd_addr = Output(UInt(8.W))
        val img2sbuf_p0_rd_data = Input(UInt(conf.ATMM.W))   

        val sc2cdma_dat_pending_req = Input(Bool())

        val reg2dp_op_en = Input(Bool())
        val reg2dp_conv_mode = Input(Bool())
        val reg2dp_in_precision = Input(UInt(2.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_data_reuse = Input(Bool())
        val reg2dp_skip_data_rls = Input(Bool())
        val reg2dp_datain_format = Input(Bool())
        val reg2dp_pixel_format = Input(UInt(6.W))
        val reg2dp_pixel_mapping = Input(Bool())
        val reg2dp_pixel_sign_override = Input(Bool())
        val reg2dp_datain_width = Input(UInt(13.W))
        val reg2dp_datain_height = Input(UInt(13.W))
        val reg2dp_datain_channel = Input(UInt(13.W))
        val reg2dp_pixel_x_offset = Input(UInt(5.W))
        val reg2dp_pixel_y_offset = Input(UInt(3.W))
        val reg2dp_datain_ram_type = Input(Bool())
        val reg2dp_datain_addr_high_0 = Input(UInt(32.W))
        val reg2dp_datain_addr_low_0 = Input(UInt(32.W))
        val reg2dp_datain_addr_low_1 = Input(UInt(32.W))
        val reg2dp_line_stride = Input(UInt(32.W))
        val reg2dp_uv_line_stride = Input(UInt(32.W))
        val reg2dp_datain_addr_high_1 = Input(UInt(32.W))
        val reg2dp_mean_format = Input(Bool())
        val reg2dp_mean_ry = Input(UInt(16.W))
        val reg2dp_mean_gu = Input(UInt(16.W))
        val reg2dp_mean_bv = Input(UInt(16.W))
        val reg2dp_mean_ax = Input(UInt(16.W))
        val reg2dp_entries = Input(UInt(14.W))
        val reg2dp_pad_left = Input(UInt(5.W))
        val reg2dp_pad_right = Input(UInt(6.W))
        val reg2dp_data_bank = Input(UInt(5.W))
        val reg2dp_dma_en = Input(Bool())
        val reg2dp_rsv_per_line = Input(UInt(10.W))
        val reg2dp_rsv_per_uv_line = Input(UInt(10.W))
        val reg2dp_rsv_height = Input(UInt(3.W))
        val reg2dp_rsv_y_index = Input(UInt(5.W))

        val slcg_img_gate_dc = Output(Bool())
        val slcg_img_gate_wg = Output(Bool())

        val dp2reg_img_rd_stall = Output(UInt(32.W))
        val dp2reg_img_rd_latency = Output(UInt(32.W))

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
    val is_running = Wire(Bool())
    val layer_st = Wire(Bool())
    val pack_is_done = Wire(Bool())
    val pixel_bank = Wire(UInt(6.W))
    val pixel_data_expand = Wire(Bool())
    val pixel_data_shrink = Wire(Bool())
    val pixel_early_end = Wire(Bool())
    val pixel_order = Wire(UInt(11.W))
    val pixel_packed_10b = Wire(Bool())
    val pixel_planar = Wire(Bool())
    val pixel_planar0_bundle_limit = Wire(UInt(4.W))
    val pixel_planar0_bundle_limit_1st = Wire(UInt(4.W))
    val pixel_planar0_byte_sft = Wire(UInt(conf.ATMMBW.W))
    val pixel_planar1_byte_sft = Wire(UInt(conf.ATMMBW.W))
    val pixel_planar0_lp_burst = Wire(UInt(4.W))
    val pixel_planar0_lp_vld = Wire(Bool())
    val pixel_planar0_rp_burst = Wire(UInt(4.W))
    val pixel_planar0_rp_vld = Wire(Bool())
    val pixel_planar0_sft = Wire(UInt(3.W))
    val pixel_planar0_width_burst = Wire(UInt(14.W))
    val pixel_planar1_bundle_limit = Wire(UInt(5.W))
    val pixel_planar1_bundle_limit_1st = Wire(UInt(5.W))
    val pixel_planar1_lp_burst = Wire(UInt(3.W))
    val pixel_planar1_lp_vld = Wire(Bool())
    val pixel_planar1_rp_burst = Wire(UInt(3.W))
    val pixel_planar1_rp_vld = Wire(Bool())
    val pixel_planar1_sft = Wire(UInt(3.W))
    val pixel_planar1_width_burst = Wire(UInt(14.W))
    val pixel_precision = Wire(UInt(2.W))
    val pixel_uint = Wire(Bool())
    val sg2pack_data_entries = Wire(UInt(15.W))
    val sg2pack_entry_end = Wire(UInt(15.W))
    val sg2pack_entry_mid = Wire(UInt(15.W))
    val sg2pack_entry_st = Wire(UInt(15.W))
    val sg2pack_height_total = Wire(UInt(13.W))
    val sg2pack_img_pd = Wire(UInt(11.W))
    val sg2pack_img_prdy = Wire(Bool())
    val sg2pack_img_pvld = Wire(Bool())
    val sg2pack_mn_enable = Wire(Bool())
    val sg2pack_sub_h_end = Wire(UInt(4.W))
    val sg2pack_sub_h_mid = Wire(UInt(4.W))
    val sg2pack_sub_h_st = Wire(UInt(4.W))
    val sg_is_done = Wire(Bool())

    /////////////////////////////////////////////////////////////////////////////////////////
    val u_ctrl = Module(new NV_NVDLA_CDMA_IMG_ctrl)
    u_ctrl.io.nvdla_core_clk := io.nvdla_core_clk
    u_ctrl.io.nvdla_core_ng_clk := io.nvdla_core_ng_clk

    u_ctrl.io.pack_is_done := pack_is_done
    u_ctrl.io.sc2cdma_dat_pending_req := io.sc2cdma_dat_pending_req
    u_ctrl.io.sg_is_done := sg_is_done
    u_ctrl.io.status2dma_fsm_switch := io.status2dma_fsm_switch 

    io.img2status_state := u_ctrl.io.img2status_state          
    is_running := u_ctrl.io.is_running                        
    layer_st := u_ctrl.io.layer_st                           
    pixel_bank := u_ctrl.io.pixel_bank                    
    pixel_data_expand := u_ctrl.io.pixel_data_expand                 
    pixel_data_shrink := u_ctrl.io.pixel_data_shrink                  
    pixel_early_end := u_ctrl.io.pixel_early_end                    
    pixel_order := u_ctrl.io.pixel_order                  
    pixel_packed_10b := u_ctrl.io.pixel_packed_10b                  
    pixel_planar := u_ctrl.io.pixel_planar                      
    pixel_planar0_bundle_limit := u_ctrl.io.pixel_planar0_bundle_limit    
    pixel_planar0_bundle_limit_1st := u_ctrl.io.pixel_planar0_bundle_limit_1st
    pixel_planar0_byte_sft := u_ctrl.io.pixel_planar0_byte_sft        
    pixel_planar0_lp_burst := u_ctrl.io.pixel_planar0_lp_burst        
    pixel_planar0_lp_vld := u_ctrl.io.pixel_planar0_lp_vld               
    pixel_planar0_rp_burst := u_ctrl.io.pixel_planar0_rp_burst        
    pixel_planar0_rp_vld := u_ctrl.io.pixel_planar0_rp_vld               
    pixel_planar0_sft := u_ctrl.io.pixel_planar0_sft             
    pixel_planar0_width_burst := u_ctrl.io.pixel_planar0_width_burst    
    pixel_planar1_bundle_limit := u_ctrl.io.pixel_planar1_bundle_limit   
    pixel_planar1_bundle_limit_1st := u_ctrl.io.pixel_planar1_bundle_limit_1st
    pixel_planar1_byte_sft := u_ctrl.io.pixel_planar1_byte_sft        
    pixel_planar1_lp_burst := u_ctrl.io.pixel_planar1_lp_burst        
    pixel_planar1_lp_vld := u_ctrl.io.pixel_planar1_lp_vld               
    pixel_planar1_rp_burst := u_ctrl.io.pixel_planar1_rp_burst        
    pixel_planar1_rp_vld := u_ctrl.io.pixel_planar1_rp_vld               
    pixel_planar1_sft := u_ctrl.io.pixel_planar1_sft            
    pixel_planar1_width_burst := u_ctrl.io.pixel_planar1_width_burst   
    pixel_precision := u_ctrl.io.pixel_precision               
    pixel_uint := u_ctrl.io.pixel_uint 
                           
    io.slcg_img_gate_dc := u_ctrl.io.slcg_img_gate_dc                   
    io.slcg_img_gate_wg := u_ctrl.io.slcg_img_gate_wg                  
    u_ctrl.io.reg2dp_op_en := io.reg2dp_op_en                    
    u_ctrl.io.reg2dp_conv_mode := io.reg2dp_conv_mode              
    u_ctrl.io.reg2dp_in_precision := io.reg2dp_in_precision         
    u_ctrl.io.reg2dp_proc_precision := io.reg2dp_proc_precision       
    u_ctrl.io.reg2dp_datain_format := io.reg2dp_datain_format          
    u_ctrl.io.reg2dp_pixel_format := io.reg2dp_pixel_format          
    u_ctrl.io.reg2dp_pixel_mapping := io.reg2dp_pixel_mapping          
    u_ctrl.io.reg2dp_pixel_sign_override := io.reg2dp_pixel_sign_override     
    u_ctrl.io.reg2dp_datain_width := io.reg2dp_datain_width        
    u_ctrl.io.reg2dp_data_reuse := io.reg2dp_data_reuse              
    u_ctrl.io.reg2dp_skip_data_rls := io.reg2dp_skip_data_rls         
    u_ctrl.io.reg2dp_data_bank := io.reg2dp_data_bank
    u_ctrl.io.reg2dp_pixel_x_offset := io.reg2dp_pixel_x_offset        
    u_ctrl.io.reg2dp_pad_left := io.reg2dp_pad_left            
    u_ctrl.io.reg2dp_pad_right := io.reg2dp_pad_right

    val u_sg = Module(new NV_NVDLA_CDMA_IMG_sg)
    u_sg.io.nvdla_core_clk := io.nvdla_core_clk
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        io.img_dat2cvif_rd_req_pd.get := u_sg.io.img_dat2cvif_rd_req_pd.get
        io.img_dat2cvif_rd_req_valid.get := u_sg.io.img_dat2cvif_rd_req_valid.get
        u_sg.io.img_dat2cvif_rd_req_ready.get := io.img_dat2cvif_rd_req_ready.get
        u_sg.io.cvif2img_dat_rd_rsp_pd.get := io.cvif2img_dat_rd_rsp_pd.get
        u_sg.io.cvif2img_dat_rd_rsp_valid.get := io.cvif2img_dat_rd_rsp_valid.get
        io.cvif2img_dat_rd_rsp_ready.get := u_sg.io.cvif2img_dat_rd_rsp_ready.get
    }
    io.img_dat2mcif_rd_req_pd := u_sg.io.img_dat2mcif_rd_req_pd
    io.img_dat2mcif_rd_req_valid := u_sg.io.img_dat2mcif_rd_req_valid
    u_sg.io.img_dat2mcif_rd_req_ready := io.img_dat2mcif_rd_req_ready
    u_sg.io.mcif2img_dat_rd_rsp_pd := io.mcif2img_dat_rd_rsp_pd
    u_sg.io.mcif2img_dat_rd_rsp_valid := io.mcif2img_dat_rd_rsp_valid
    io.mcif2img_dat_rd_rsp_ready := u_sg.io.mcif2img_dat_rd_rsp_ready

    u_sg.io.img2status_dat_entries := io.img2status_dat_entries
    u_sg.io.img2status_dat_updt := io.img2status_dat_updt
    u_sg.io.is_running := is_running
    u_sg.io.layer_st := layer_st
    u_sg.io.pixel_order := pixel_order
    u_sg.io.pixel_planar := pixel_planar
    u_sg.io.pixel_planar0_bundle_limit := pixel_planar0_bundle_limit
    u_sg.io.pixel_planar0_bundle_limit_1st := pixel_planar0_bundle_limit_1st
    u_sg.io.pixel_planar0_byte_sft := pixel_planar0_byte_sft
    u_sg.io.pixel_planar1_byte_sft := pixel_planar1_byte_sft
    u_sg.io.pixel_planar0_lp_burst := pixel_planar0_lp_burst
    u_sg.io.pixel_planar0_lp_vld := pixel_planar0_lp_vld
    u_sg.io.pixel_planar0_rp_burst := pixel_planar0_rp_burst
    u_sg.io.pixel_planar0_rp_vld := pixel_planar0_rp_vld
    u_sg.io.pixel_planar0_width_burst := pixel_planar0_width_burst
    u_sg.io.pixel_planar1_bundle_limit := pixel_planar1_bundle_limit
    u_sg.io.pixel_planar1_bundle_limit_1st := pixel_planar1_bundle_limit_1st
    u_sg.io.pixel_planar1_lp_burst := pixel_planar1_lp_burst
    u_sg.io.pixel_planar1_lp_vld := pixel_planar1_lp_vld
    u_sg.io.pixel_planar1_rp_burst := pixel_planar1_rp_burst
    u_sg.io.pixel_planar1_rp_vld := pixel_planar1_rp_vld
    u_sg.io.pixel_planar1_width_burst := pixel_planar1_width_burst
    u_sg.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_sg.io.reg2dp_op_en := io.reg2dp_op_en
    u_sg.io.sg2pack_img_prdy := sg2pack_img_prdy
    u_sg.io.status2dma_free_entries := io.status2dma_free_entries
    u_sg.io.status2dma_fsm_switch := io.status2dma_fsm_switch

    io.img2sbuf_p0_wr_addr := u_sg.io.img2sbuf_p0_wr_addr
    io.img2sbuf_p0_wr_data := u_sg.io.img2sbuf_p0_wr_data
    io.img2sbuf_p0_wr_en := u_sg.io.img2sbuf_p0_wr_en

    sg2pack_data_entries := u_sg.io.sg2pack_data_entries
    sg2pack_entry_end := u_sg.io.sg2pack_entry_end
    sg2pack_entry_mid := u_sg.io.sg2pack_entry_mid
    sg2pack_entry_st := u_sg.io.sg2pack_entry_st
    sg2pack_height_total := u_sg.io.sg2pack_height_total
    sg2pack_img_pd := u_sg.io.sg2pack_img_pd
    sg2pack_img_pvld := u_sg.io.sg2pack_img_pvld
    sg2pack_mn_enable := u_sg.io.sg2pack_mn_enable
    sg2pack_sub_h_end := u_sg.io.sg2pack_sub_h_end
    sg2pack_sub_h_mid := u_sg.io.sg2pack_sub_h_mid
    sg2pack_sub_h_st := u_sg.io.sg2pack_sub_h_st
    sg_is_done := u_sg.io.sg_is_done

    u_sg.io.reg2dp_pixel_y_offset := io.reg2dp_pixel_y_offset
    u_sg.io.reg2dp_datain_height := io.reg2dp_datain_height
    u_sg.io.reg2dp_datain_ram_type := io.reg2dp_datain_ram_type
    u_sg.io.reg2dp_datain_addr_high_0 := io.reg2dp_datain_addr_high_0
    u_sg.io.reg2dp_datain_addr_low_0 := io.reg2dp_datain_addr_low_0
    u_sg.io.reg2dp_datain_addr_high_1 := io.reg2dp_datain_addr_high_1
    u_sg.io.reg2dp_datain_addr_low_1 := io.reg2dp_datain_addr_low_1
    u_sg.io.reg2dp_line_stride := io.reg2dp_line_stride
    u_sg.io.reg2dp_uv_line_stride := io.reg2dp_uv_line_stride
    u_sg.io.reg2dp_mean_format := io.reg2dp_mean_format
    u_sg.io.reg2dp_entries := io.reg2dp_entries
    u_sg.io.reg2dp_dma_en := io.reg2dp_dma_en

    io.dp2reg_img_rd_stall := u_sg.io.dp2reg_img_rd_stall
    io.dp2reg_img_rd_latency := u_sg.io.dp2reg_img_rd_latency

    val u_pack = Module(new NV_NVDLA_CDMA_IMG_pack)
    u_pack.io.nvdla_core_clk := io.nvdla_core_clk
    u_pack.io.img2sbuf_p0_rd_data := io.img2sbuf_p0_rd_data
    io.img2sbuf_p0_rd_addr := u_pack.io.img2sbuf_p0_rd_addr
    io.img2sbuf_p0_rd_en := u_pack.io.img2sbuf_p0_rd_en
    u_pack.io.is_running := is_running
    u_pack.io.layer_st := layer_st
    u_pack.io.pixel_bank := pixel_bank
    u_pack.io.pixel_data_expand := pixel_data_expand
    u_pack.io.pixel_data_shrink := pixel_data_shrink
    u_pack.io.pixel_early_end := pixel_early_end
    u_pack.io.pixel_packed_10b := pixel_packed_10b
    u_pack.io.pixel_planar := pixel_planar
    u_pack.io.pixel_planar0_sft := pixel_planar0_sft
    u_pack.io.pixel_planar1_sft := pixel_planar1_sft
    u_pack.io.pixel_precision := pixel_precision
    u_pack.io.pixel_uint := pixel_uint 
    u_pack.io.sg2pack_data_entries := sg2pack_data_entries
    u_pack.io.sg2pack_entry_end := sg2pack_entry_end
    u_pack.io.sg2pack_entry_mid := sg2pack_entry_mid
    u_pack.io.sg2pack_entry_st := sg2pack_entry_st
    u_pack.io.sg2pack_height_total := sg2pack_height_total
    u_pack.io.sg2pack_img_pd := sg2pack_img_pd
    u_pack.io.sg2pack_img_pvld := sg2pack_img_pvld 
    u_pack.io.sg2pack_mn_enable := sg2pack_mn_enable
    u_pack.io.sg2pack_sub_h_end := sg2pack_sub_h_end 
    u_pack.io.sg2pack_sub_h_mid := sg2pack_sub_h_mid
    u_pack.io.sg2pack_sub_h_st := sg2pack_sub_h_st
    u_pack.io.status2dma_wr_idx := io.status2dma_wr_idx
    if(conf.DMAIF < conf.ATMC){
        io.img2cvt_dat_wr_sel.get := u_pack.io.img2cvt_dat_wr_sel.get  
    }
    io.img2cvt_dat_wr_addr := u_pack.io.img2cvt_dat_wr_addr
    io.img2cvt_dat_wr_data := u_pack.io.img2cvt_dat_wr_data
    io.img2cvt_mn_wr_data := u_pack.io.img2cvt_mn_wr_data
    io.img2cvt_dat_wr_pad_mask := u_pack.io.img2cvt_dat_wr_pad_mask
    io.img2cvt_dat_wr_en := u_pack.io.img2cvt_dat_wr_en
    io.img2cvt_dat_wr_info_pd := u_pack.io.img2cvt_dat_wr_info_pd

    io.img2status_dat_entries := u_pack.io.img2status_dat_entries
    io.img2status_dat_slices := u_pack.io.img2status_dat_slices
    io.img2status_dat_updt := u_pack.io.img2status_dat_updt
    pack_is_done := u_pack.io.pack_is_done
    sg2pack_img_prdy := u_pack.io.sg2pack_img_prdy

    u_pack.io.reg2dp_datain_width := io.reg2dp_datain_width
    u_pack.io.reg2dp_datain_channel := io.reg2dp_datain_channel
    u_pack.io.reg2dp_mean_ry := io.reg2dp_mean_ry
    u_pack.io.reg2dp_mean_gu := io.reg2dp_mean_gu
    u_pack.io.reg2dp_mean_bv := io.reg2dp_mean_bv
    u_pack.io.reg2dp_mean_ax := io.reg2dp_mean_ax
    u_pack.io.reg2dp_pad_left := io.reg2dp_pad_left
    u_pack.io.reg2dp_pad_right := io.reg2dp_pad_right

    
}


object NV_NVDLA_CDMA_imgDriver extends App {
  implicit val conf: cdmaConfiguration = new cdmaConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_img())
}

