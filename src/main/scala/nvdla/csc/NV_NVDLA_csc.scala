package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.Driver


class NV_NVDLA_csc(implicit val conf: cscConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())   
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val tmc2slcg_disable_clock_gating = Input(Bool())
        val nvdla_core_rstn = Input(Bool())

        //cdma
        val sc2cdma_dat_pending_req = Output(Bool())  
        val sc2cdma_wt_pending_req = Output(Bool())
        val cdma2sc_dat_pending_ack = Input(Bool())
        val cdma2sc_wt_pending_ack = Input(Bool())

        val cdma2sc_dat_updt = Input(Bool())    /* data valid */
        val cdma2sc_dat_entries = Input(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
        val cdma2sc_dat_slices = Input(UInt(14.W))

        val sc2cdma_dat_updt = Output(Bool())
        val sc2cdma_dat_entries = Output(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
        val sc2cdma_dat_slices = Output(UInt(14.W))

        val cdma2sc_wt_updt = Input(Bool())    /* data valid */
        val cdma2sc_wt_entries = Input(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
        val cdma2sc_wmb_entries = Input(UInt(9.W))
        val cdma2sc_wt_kernels = Input(UInt(14.W))

        val sc2cdma_wt_updt = Output(Bool())
        val sc2cdma_wt_entries = Output(UInt(conf.CSC_ENTRIES_NUM_WIDTH.W))
        val sc2cdma_wmb_entries = Output(UInt(9.W))
        val sc2cdma_wt_kernels = Output(UInt(14.W))       

        //accu
        val accu2sc_credit_vld = Input(Bool())
        val accu2sc_credit_size = Input(UInt(3.W))

        //csb
        val csb2csc_req_pvld = Input(Bool())    /* data valid */
        val csb2csc_req_prdy = Output(Bool())   /* data return handshake */
        val csb2csc_req_pd = Input(UInt(63.W))
        val csc2csb_resp_valid = Output(Bool())
        val csc2csb_resp_pd = Output(UInt(34.W))    /* pkt_id_width=1 pkt_widths=33,33  */

        //cbuf_dat
        val sc2buf_dat_rd_en = Output(Bool())
        val sc2buf_dat_rd_addr = Output(UInt(conf.CBUF_ADDR_WIDTH.W))
        val sc2buf_dat_rd_valid = Input(Bool())
        val sc2buf_dat_rd_data = Input(UInt(conf.CBUF_ENTRY_BITS.W))
        val sc2buf_dat_rd_shift = Output(UInt(conf.CBUF_RD_DATA_SHIFT_WIDTH.W))
        val sc2buf_dat_rd_next1_en = Output(Bool())
        val sc2buf_dat_rd_next1_addr = Output(UInt(conf.CBUF_ADDR_WIDTH.W))

        //cbuf_wt
        val sc2buf_wt_rd_en = Output(Bool())
        val sc2buf_wt_rd_addr = Output(UInt(conf.CBUF_ADDR_WIDTH.W))
        val sc2buf_wt_rd_valid = Input(Bool())
        val sc2buf_wt_rd_data = Input(UInt(conf.CBUF_ENTRY_BITS.W))

        //mac_dat
        val sc2mac_dat_a_pvld = Output(Bool())
        val sc2mac_dat_a_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
        val sc2mac_dat_a_data = Output(Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W)))
        val sc2mac_dat_a_pd = Output(UInt(9.W))

        val sc2mac_dat_b_pvld = Output(Bool())
        val sc2mac_dat_b_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
        val sc2mac_dat_b_data = Output(Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W)))
        val sc2mac_dat_b_pd = Output(UInt(9.W))   

        //mac_wt
        val sc2mac_wt_a_pvld = Output(Bool())
        val sc2mac_wt_a_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
        val sc2mac_wt_a_data = Output(Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W)))
        val sc2mac_wt_a_sel = Output(Vec(conf.CSC_ATOMK_HF, Bool()))

        val sc2mac_wt_b_pvld = Output(Bool())
        val sc2mac_wt_b_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
        val sc2mac_wt_b_data = Output(Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W)))
        val sc2mac_wt_b_sel = Output(Vec(conf.CSC_ATOMK_HF, Bool()))

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
withReset(!io.nvdla_core_rstn){
    val dp2reg_done = Wire(Bool())
    val nvdla_op_gated_clk = Wire(Vec(3, Clock()))
    val reg2dp_atomics = Wire(UInt(21.W))
    val reg2dp_batches = Wire(UInt(5.W))
    val reg2dp_conv_mode = Wire(Bool())
    val reg2dp_conv_x_stride_ext = Wire(UInt(3.W))
    val reg2dp_conv_y_stride_ext = Wire(UInt(3.W))
    val reg2dp_cya = Wire(UInt(32.W))
    val reg2dp_data_bank = Wire(UInt(5.W))
    val reg2dp_data_reuse = Wire(Bool())
    val reg2dp_datain_channel_ext = Wire(UInt(13.W))
    val reg2dp_datain_format = Wire(Bool())
    val reg2dp_datain_height_ext = Wire(UInt(13.W))
    val reg2dp_datain_width_ext = Wire(UInt(13.W))
    val reg2dp_dataout_channel = Wire(UInt(13.W))
    val reg2dp_dataout_height = Wire(UInt(13.W))
    val reg2dp_dataout_width = Wire(UInt(13.W))
    val reg2dp_entries = Wire(UInt(14.W))
    val reg2dp_in_precision = Wire(UInt(2.W))
    val reg2dp_op_en = Wire(Bool())
    val reg2dp_pad_left = Wire(UInt(5.W))
    val reg2dp_pad_top = Wire(UInt(5.W))
    val reg2dp_pad_value = Wire(UInt(16.W))
    val reg2dp_pra_truncate = Wire(UInt(2.W))
    val reg2dp_proc_precision = Wire(UInt(2.W))
    val reg2dp_rls_slices = Wire(UInt(12.W))
    val reg2dp_skip_data_rls = Wire(Bool())
    val reg2dp_skip_weight_rls = Wire(Bool())
    val reg2dp_weight_bank = Wire(UInt(5.W))
    val reg2dp_weight_bytes = Wire(UInt(32.W))
    val reg2dp_weight_channel_ext = Wire(UInt(13.W))
    val reg2dp_weight_format = Wire(Bool())
    val reg2dp_weight_height_ext = Wire(UInt(5.W))
    val reg2dp_weight_kernel = Wire(UInt(13.W))
    val reg2dp_weight_reuse = Wire(Bool())
    val reg2dp_weight_width_ext = Wire(UInt(5.W))
    val reg2dp_wmb_bytes = Wire(UInt(28.W))
    val reg2dp_x_dilation_ext = Wire(UInt(5.W))
    val reg2dp_y_dilation_ext = Wire(UInt(5.W))
    val reg2dp_y_extension = Wire(UInt(2.W))
    val sc_state = Wire(UInt(2.W))
    val sg2dl_pd = Wire(UInt(31.W))
    val sg2dl_pvld = Wire(Bool())
    val sg2dl_reuse_rls = Wire(Bool())
    val sg2wl_pd = Wire(UInt(18.W))
    val sg2wl_pvld = Wire(Bool())
    val sg2wl_reuse_rls = Wire(Bool())
    val slcg_op_en = Wire(UInt(4.W))
    val slcg_wg_en = Wire(Bool())

    //==========================================================
    // Regfile
    //==========================================================

    val u_regfile = Module(new NV_NVDLA_CSC_regfile)

    u_regfile.io.nvdla_core_clk := io.nvdla_core_clk                //|< i

    u_regfile.io.csb2csc_req_pd := io.csb2csc_req_pd       //|< i
    u_regfile.io.csb2csc_req_pvld := io.csb2csc_req_pvld           //|< i
    u_regfile.io.dp2reg_done := dp2reg_done                   //|< w

    io.csc2csb_resp_pd := u_regfile.io.csc2csb_resp_pd      //|> o
    io.csc2csb_resp_valid := u_regfile.io.csc2csb_resp_valid         //|> o
    io.csb2csc_req_prdy := u_regfile.io.csb2csc_req_prdy           //|> o

    reg2dp_atomics := u_regfile.io.reg2dp_atomics
    reg2dp_batches := u_regfile.io.reg2dp_batches
    reg2dp_conv_mode := u_regfile.io.reg2dp_conv_mode
    reg2dp_conv_x_stride_ext := u_regfile.io.reg2dp_conv_x_stride_ext
    reg2dp_conv_y_stride_ext := u_regfile.io.reg2dp_conv_y_stride_ext
    reg2dp_cya := u_regfile.io.reg2dp_cya
    reg2dp_data_bank := u_regfile.io.reg2dp_data_bank
    reg2dp_data_reuse := u_regfile.io.reg2dp_data_reuse
    reg2dp_datain_channel_ext := u_regfile.io.reg2dp_datain_channel_ext
    reg2dp_datain_format := u_regfile.io.reg2dp_datain_format
    reg2dp_datain_height_ext := u_regfile.io.reg2dp_datain_height_ext
    reg2dp_datain_width_ext := u_regfile.io.reg2dp_datain_width_ext
    reg2dp_dataout_channel := u_regfile.io.reg2dp_dataout_channel
    reg2dp_dataout_height := u_regfile.io.reg2dp_dataout_height
    reg2dp_dataout_width := u_regfile.io.reg2dp_dataout_width
    reg2dp_entries := u_regfile.io.reg2dp_entries
    reg2dp_in_precision := u_regfile.io.reg2dp_in_precision
    reg2dp_op_en := u_regfile.io.reg2dp_op_en
    reg2dp_pad_left := u_regfile.io.reg2dp_pad_left
    reg2dp_pad_top := u_regfile.io.reg2dp_pad_top
    reg2dp_pad_value := u_regfile.io.reg2dp_pad_value
    reg2dp_pra_truncate := u_regfile.io.reg2dp_pra_truncate
    reg2dp_proc_precision := u_regfile.io.reg2dp_proc_precision
    reg2dp_rls_slices := u_regfile.io.reg2dp_rls_slices
    reg2dp_skip_data_rls := u_regfile.io.reg2dp_skip_data_rls
    reg2dp_skip_weight_rls := u_regfile.io.reg2dp_skip_weight_rls
    reg2dp_weight_bank := u_regfile.io.reg2dp_weight_bank
    reg2dp_weight_bytes := u_regfile.io.reg2dp_weight_bytes
    reg2dp_weight_channel_ext := u_regfile.io.reg2dp_weight_channel_ext
    reg2dp_weight_format := u_regfile.io.reg2dp_weight_format
    reg2dp_weight_height_ext := u_regfile.io.reg2dp_weight_height_ext
    reg2dp_weight_kernel := u_regfile.io.reg2dp_weight_kernel
    reg2dp_weight_reuse := u_regfile.io.reg2dp_weight_reuse
    reg2dp_weight_width_ext := u_regfile.io.reg2dp_weight_width_ext
    reg2dp_wmb_bytes := u_regfile.io.reg2dp_wmb_bytes
    reg2dp_x_dilation_ext := u_regfile.io.reg2dp_x_dilation_ext
    reg2dp_y_dilation_ext := u_regfile.io.reg2dp_y_dilation_ext
    reg2dp_y_extension := u_regfile.io.reg2dp_y_extension

    slcg_op_en := u_regfile.io.slcg_op_en              


    //==========================================================
    // Sequence generator
    //==========================================================

    val u_sg = Module(new NV_NVDLA_CSC_sg)

    u_sg.io.nvdla_core_clk := nvdla_op_gated_clk(0)
    u_sg.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_sg.io.cdma2sc_dat_updt := io.cdma2sc_dat_updt
    u_sg.io.cdma2sc_dat_updt := io.cdma2sc_dat_updt
    u_sg.io.cdma2sc_dat_entries := io.cdma2sc_dat_entries
    u_sg.io.cdma2sc_dat_slices := io.cdma2sc_dat_slices
    u_sg.io.cdma2sc_wt_updt := io.cdma2sc_wt_updt
    u_sg.io.cdma2sc_wt_kernels := io.cdma2sc_wt_kernels
    u_sg.io.cdma2sc_wt_entries := io.cdma2sc_wt_entries
    u_sg.io.cdma2sc_wmb_entries := io.cdma2sc_wmb_entries
    u_sg.io.accu2sc_credit_vld := io.accu2sc_credit_vld
    u_sg.io.accu2sc_credit_size := io.accu2sc_credit_size
    u_sg.io.cdma2sc_dat_pending_ack := io.cdma2sc_dat_pending_ack
    u_sg.io.cdma2sc_wt_pending_ack := io.cdma2sc_wt_pending_ack
    u_sg.io.nvdla_core_ng_clk := io.nvdla_core_clk
    u_sg.io.reg2dp_op_en := reg2dp_op_en
    u_sg.io.reg2dp_conv_mode := reg2dp_conv_mode
    u_sg.io.reg2dp_proc_precision := reg2dp_proc_precision
    u_sg.io.reg2dp_data_reuse := reg2dp_data_reuse
    u_sg.io.reg2dp_skip_data_rls := reg2dp_skip_data_rls
    u_sg.io.reg2dp_weight_reuse := reg2dp_weight_reuse
    u_sg.io.reg2dp_skip_weight_rls := reg2dp_skip_weight_rls
    u_sg.io.reg2dp_weight_reuse := reg2dp_weight_reuse
    u_sg.io.reg2dp_skip_weight_rls := reg2dp_skip_weight_rls
    u_sg.io.reg2dp_batches := reg2dp_batches
    u_sg.io.reg2dp_datain_format := reg2dp_datain_format
    u_sg.io.reg2dp_datain_height_ext := reg2dp_datain_height_ext
    u_sg.io.reg2dp_y_extension := reg2dp_y_extension
    u_sg.io.reg2dp_weight_width_ext := reg2dp_weight_width_ext
    u_sg.io.reg2dp_weight_height_ext := reg2dp_weight_height_ext
    u_sg.io.reg2dp_weight_channel_ext := reg2dp_weight_channel_ext
    u_sg.io.reg2dp_weight_kernel := reg2dp_weight_kernel
    u_sg.io.reg2dp_dataout_width := reg2dp_dataout_width
    u_sg.io.reg2dp_dataout_height := reg2dp_dataout_height
    u_sg.io.reg2dp_data_bank := reg2dp_data_bank
    u_sg.io.reg2dp_weight_bank := reg2dp_weight_bank 
    u_sg.io.reg2dp_atomics := reg2dp_atomics
    u_sg.io.reg2dp_rls_slices := reg2dp_rls_slices 
    dp2reg_done := u_sg.io.dp2reg_done
    sg2dl_pvld := u_sg.io.sg2dl_pvld
    sg2dl_pd := u_sg.io.sg2dl_pd
    sg2wl_pvld := u_sg.io.sg2wl_pvld 
    sg2wl_pd := u_sg.io.sg2wl_pd
    sc_state := u_sg.io.sc_state
    io.sc2cdma_dat_pending_req := u_sg.io.sc2cdma_dat_pending_req
    io.sc2cdma_wt_pending_req := u_sg.io.sc2cdma_wt_pending_req
    sg2dl_reuse_rls := u_sg.io.sg2dl_reuse_rls
    sg2wl_reuse_rls := u_sg.io.sg2wl_reuse_rls

    //==========================================================
    // Weight loader
    //==========================================================

    val u_wl = Module(new NV_NVDLA_CSC_wl)

    u_wl.io.nvdla_core_clk := nvdla_op_gated_clk(1)
    u_wl.io.sg2wl_pvld := sg2wl_pvld
    u_wl.io.sg2wl_pd := sg2wl_pd
    u_wl.io.sc_state := sc_state 
    u_wl.io.sg2wl_reuse_rls := sg2wl_reuse_rls
    u_wl.io.sc2cdma_wt_pending_req := io.sc2cdma_wt_pending_req
    u_wl.io.cdma2sc_wt_updt := io.cdma2sc_wt_updt
    u_wl.io.cdma2sc_wt_kernels := io.cdma2sc_wt_kernels 
    u_wl.io.cdma2sc_wt_entries := io.cdma2sc_wt_entries
    u_wl.io.cdma2sc_wmb_entries := io.cdma2sc_wmb_entries
    u_wl.io.sc2buf_wt_rd_valid := io.sc2buf_wt_rd_valid
    u_wl.io.sc2buf_wt_rd_data := io.sc2buf_wt_rd_data
    u_wl.io.nvdla_core_ng_clk := io.nvdla_core_clk 
    u_wl.io.reg2dp_op_en := reg2dp_op_en
    u_wl.io.reg2dp_in_precision := reg2dp_in_precision
    u_wl.io.reg2dp_proc_precision := reg2dp_proc_precision
    u_wl.io.reg2dp_y_extension := reg2dp_y_extension
    u_wl.io.reg2dp_weight_reuse := reg2dp_weight_reuse
    u_wl.io.reg2dp_skip_weight_rls := reg2dp_skip_weight_rls
    u_wl.io.reg2dp_weight_format := reg2dp_weight_format
    u_wl.io.reg2dp_weight_bytes := reg2dp_weight_bytes
    u_wl.io.reg2dp_wmb_bytes := reg2dp_wmb_bytes
    u_wl.io.reg2dp_data_bank := reg2dp_data_bank
    u_wl.io.reg2dp_weight_bank := reg2dp_weight_bank
    io.sc2cdma_wt_updt := u_wl.io.sc2cdma_wt_updt
    io.sc2cdma_wt_kernels := u_wl.io.sc2cdma_wt_kernels
    io.sc2cdma_wt_entries := u_wl.io.sc2cdma_wt_entries
    io.sc2cdma_wmb_entries := u_wl.io.sc2cdma_wmb_entries
    io.sc2buf_wt_rd_en := u_wl.io.sc2buf_wt_rd_en 
    io.sc2buf_wt_rd_addr := u_wl.io.sc2buf_wt_rd_addr
    io.sc2mac_wt_a_pvld := u_wl.io.sc2mac_wt_a_pvld
    io.sc2mac_wt_a_mask := u_wl.io.sc2mac_wt_a_mask
    io.sc2mac_wt_a_data := u_wl.io.sc2mac_wt_a_data
    io.sc2mac_wt_a_sel := u_wl.io.sc2mac_wt_a_sel
    io.sc2mac_wt_b_pvld := u_wl.io.sc2mac_wt_b_pvld
    io.sc2mac_wt_b_mask := u_wl.io.sc2mac_wt_b_mask
    io.sc2mac_wt_b_data := u_wl.io.sc2mac_wt_b_data
    io.sc2mac_wt_b_sel := u_wl.io.sc2mac_wt_b_sel

    //==========================================================
    // Data loader
    //==========================================================

    val u_dl = Module(new NV_NVDLA_CSC_dl)
    
    u_dl.io.nvdla_core_clk := nvdla_op_gated_clk(2)
    u_dl.io.sg2dl_pvld := sg2dl_pvld
    u_dl.io.sg2dl_pd := sg2dl_pd
    u_dl.io.sc_state := sc_state
    u_dl.io.sg2dl_reuse_rls := sg2dl_reuse_rls
    u_dl.io.sc2cdma_dat_pending_req := io.sc2cdma_dat_pending_req
    u_dl.io.cdma2sc_dat_updt := io.cdma2sc_dat_updt
    u_dl.io.cdma2sc_dat_entries := io.cdma2sc_dat_entries
    u_dl.io.cdma2sc_dat_slices := io.cdma2sc_dat_slices
    u_dl.io.sc2buf_dat_rd_valid := io.sc2buf_dat_rd_valid
    u_dl.io.sc2buf_dat_rd_data := io.sc2buf_dat_rd_data
    u_dl.io.nvdla_core_ng_clk := io.nvdla_core_clk
    u_dl.io.reg2dp_op_en := reg2dp_op_en
    u_dl.io.reg2dp_conv_mode := reg2dp_conv_mode
    u_dl.io.reg2dp_batches := reg2dp_batches
    u_dl.io.reg2dp_proc_precision := reg2dp_proc_precision
    u_dl.io.reg2dp_datain_format := reg2dp_datain_format
    u_dl.io.reg2dp_skip_data_rls := reg2dp_skip_data_rls
    u_dl.io.reg2dp_datain_channel_ext := reg2dp_datain_channel_ext
    u_dl.io.reg2dp_datain_height_ext := reg2dp_datain_height_ext
    u_dl.io.reg2dp_datain_width_ext := reg2dp_datain_width_ext
    u_dl.io.reg2dp_y_extension := reg2dp_y_extension
    u_dl.io.reg2dp_weight_channel_ext := reg2dp_weight_channel_ext
    u_dl.io.reg2dp_entries := reg2dp_entries
    u_dl.io.reg2dp_dataout_width := reg2dp_dataout_width
    u_dl.io.reg2dp_rls_slices := reg2dp_rls_slices
    u_dl.io.reg2dp_conv_x_stride_ext := reg2dp_conv_x_stride_ext
    u_dl.io.reg2dp_conv_y_stride_ext := reg2dp_conv_y_stride_ext
    u_dl.io.reg2dp_x_dilation_ext := reg2dp_x_dilation_ext
    u_dl.io.reg2dp_y_dilation_ext := reg2dp_y_dilation_ext
    u_dl.io.reg2dp_pad_left := reg2dp_pad_left
    u_dl.io.reg2dp_pad_top := reg2dp_pad_top
    u_dl.io.reg2dp_pad_value := reg2dp_pad_value
    u_dl.io.reg2dp_data_bank := reg2dp_data_bank
    u_dl.io.reg2dp_pra_truncate := reg2dp_pra_truncate
    io.sc2cdma_dat_updt := u_dl.io.sc2cdma_dat_updt
    io.sc2cdma_dat_entries := u_dl.io.sc2cdma_dat_entries
    io.sc2cdma_dat_slices := u_dl.io.sc2cdma_dat_slices
    io.sc2buf_dat_rd_en := u_dl.io.sc2buf_dat_rd_en
    io.sc2buf_dat_rd_addr := u_dl.io.sc2buf_dat_rd_addr
    io.sc2buf_dat_rd_shift := u_dl.io.sc2buf_dat_rd_shift
    io.sc2buf_dat_rd_next1_en := u_dl.io.sc2buf_dat_rd_next1_en
    io.sc2buf_dat_rd_next1_addr := u_dl.io.sc2buf_dat_rd_next1_addr
    io.sc2mac_dat_a_pvld := u_dl.io.sc2mac_dat_a_pvld
    io.sc2mac_dat_a_mask := u_dl.io.sc2mac_dat_a_mask
    io.sc2mac_dat_a_data := u_dl.io.sc2mac_dat_a_data
    io.sc2mac_dat_a_pd := u_dl.io.sc2mac_dat_a_pd
    io.sc2mac_dat_b_pvld := u_dl.io.sc2mac_dat_b_pvld
    io.sc2mac_dat_b_mask := u_dl.io.sc2mac_dat_b_mask
    io.sc2mac_dat_b_data := u_dl.io.sc2mac_dat_b_data
    io.sc2mac_dat_b_pd := u_dl.io.sc2mac_dat_b_pd
    slcg_wg_en := u_dl.io.slcg_wg_en
    

    //==========================================================
    // SLCG groups
    //==========================================================

    val u_slcg_op = Array.fill(3){Module(new NV_NVDLA_slcg)}

    for(i<- 0 to 2){

        u_slcg_op(i).io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
        u_slcg_op(i).io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
        u_slcg_op(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_slcg_op(i).io.slcg_en_src_0 := slcg_op_en(i)
        u_slcg_op(i).io.slcg_en_src_1 := true.B
        u_slcg_op(i).io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

        nvdla_op_gated_clk(i) := u_slcg_op(i).io.nvdla_core_gated_clk                                                                                               
    }






 

}}


object NV_NVDLA_cscDriver extends App {
  implicit val conf: cscConfiguration = new cscConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_csc())
}
