package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

//Implementation overview of ping-pong register file.

class NV_NVDLA_CSC_regfile(implicit val conf: cscConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        //csb2csc
        val csb2csc_req_pd = Input(UInt(63.W))
        val csb2csc_req_pvld = Input(Bool())
        val csb2csc_req_prdy = Output(Bool())
        
        val csc2csb_resp_pd = Output(UInt(34.W))
        val csc2csb_resp_valid = Output(Bool())

        //reg2dp
        val dp2reg_done = Input(Bool())
        val reg2dp_atomics = Output(UInt(21.W))
        val reg2dp_batches = Output(UInt(5.W))
        val reg2dp_conv_mode = Output(Bool())
        val reg2dp_conv_x_stride_ext = Output(UInt(3.W))
        val reg2dp_conv_y_stride_ext = Output(UInt(3.W))
        val reg2dp_cya = Output(UInt(32.W))
        val reg2dp_data_bank = Output(UInt(5.W))
        val reg2dp_data_reuse = Output(Bool())
        val reg2dp_datain_channel_ext = Output(UInt(13.W))
        val reg2dp_datain_format = Output(Bool())
        val reg2dp_datain_height_ext = Output(UInt(13.W))
        val reg2dp_datain_width_ext = Output(UInt(13.W))
        val reg2dp_dataout_channel = Output(UInt(13.W))
        val reg2dp_dataout_height = Output(UInt(13.W))
        val reg2dp_dataout_width = Output(UInt(13.W))
        val reg2dp_entries = Output(UInt(14.W))
        val reg2dp_in_precision = Output(UInt(2.W))
        val reg2dp_op_en = Output(Bool())
        val reg2dp_pad_left = Output(UInt(5.W))
        val reg2dp_pad_top = Output(UInt(5.W))
        val reg2dp_pad_value = Output(UInt(16.W))
        val reg2dp_pra_truncate = Output(UInt(2.W))
        val reg2dp_proc_precision = Output(UInt(2.W))
        val reg2dp_rls_slices = Output(UInt(12.W))
        val reg2dp_skip_data_rls = Output(Bool())
        val reg2dp_skip_weight_rls = Output(Bool())
        val reg2dp_weight_bank = Output(UInt(5.W))
        val reg2dp_weight_bytes = Output(UInt(32.W))
        val reg2dp_weight_channel_ext = Output(UInt(13.W))
        val reg2dp_weight_format = Output(Bool())
        val reg2dp_weight_height_ext = Output(UInt(5.W))
        val reg2dp_weight_kernel = Output(UInt(13.W))
        val reg2dp_weight_reuse = Output(Bool())
        val reg2dp_weight_width_ext = Output(UInt(5.W))
        val reg2dp_wmb_bytes = Output(UInt(28.W))
        val reg2dp_x_dilation_ext = Output(UInt(5.W))
        val reg2dp_y_dilation_ext = Output(UInt(5.W))
        val reg2dp_y_extension = Output(UInt(2.W))

        //slave cg op
        val slcg_op_en = Output(UInt(4.W))
    })
//                             
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │              |-------------|
//       │       ───       │              |     CSB     |
//       │  ─┬┘       └┬─  │              |-------------|
//       │                 │                    ||
//       │       ─┴─       │                    reg   <= DP(data processor)
//       │                 │                    ||
//       └───┐         ┌───┘              |-------------|
//           │         │                  |     CMAC    |
//           │         │                  |-------------|
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

    //Instance single register group
    val dp2reg_consumer = RegInit(false.B)
    val reg_offset = Wire(UInt(12.W))
    val reg_wr_data = Wire(UInt(32.W))
    val s_reg_wr_en = Wire(Bool())
    val dp2reg_status_0 = Wire(Bool())
    val dp2reg_status_1 = Wire(Bool())

    val u_single_reg = Module(new NV_NVDLA_CMAC_REG_single)

    u_single_reg.io.reg_offset := reg_offset
    u_single_reg.io.reg_wr_data := reg_wr_data 
    u_single_reg.io.reg_wr_en := s_reg_wr_en
    u_single_reg.io.nvdla_core_clk := io.nvdla_core_clk
    u_single_reg.io.consumer := dp2reg_consumer
    u_single_reg.io.status_0 := dp2reg_status_0
    u_single_reg.io.status_1 := dp2reg_status_1 
    val s_reg_rd_data = u_single_reg.io.reg_rd_data
    val reg2dp_producer = u_single_reg.io.producer

    //Instance two duplicated register groups
    val d0_reg_wr_en = Wire(Bool())
    val reg2dp_d0_op_en = RegInit(false.B)

    val u_dual_reg_d0 = Module(new NV_NVDLA_CSC_dual_reg)
    u_dual_reg_d0.io.reg_offset := reg_offset
    u_dual_reg_d0.io.reg_wr_data := reg_wr_data
    u_dual_reg_d0.io.reg_wr_en := d0_reg_wr_en
    u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d0.io.op_en := reg2dp_d0_op_en
    val d0_reg_rd_data = u_dual_reg_d0.io.reg_rd_data
    val reg2dp_d0_atomics = u_dual_reg_d0.io.atomics
    val reg2dp_d0_data_bank = u_dual_reg_d0.io.data_bank
    val reg2dp_d0_weight_bank = u_dual_reg_d0.io.weight_bank
    val reg2dp_d0_batches = u_dual_reg_d0.io.batches
    val reg2dp_d0_conv_x_stride_ext = u_dual_reg_d0.io.conv_x_stride_ext
    val reg2dp_d0_conv_y_stride_ext = u_dual_reg_d0.io.conv_y_stride_ext
    val reg2dp_d0_cya = u_dual_reg_d0.io.cya
    val reg2dp_d0_datain_format = u_dual_reg_d0.io.datain_format
    val reg2dp_d0_datain_height_ext = u_dual_reg_d0.io.datain_height_ext
    val reg2dp_d0_datain_width_ext = u_dual_reg_d0.io.datain_width_ext
    val reg2dp_d0_datain_channel_ext = u_dual_reg_d0.io.datain_channel_ext
    val reg2dp_d0_dataout_height = u_dual_reg_d0.io.dataout_height
    val reg2dp_d0_dataout_width = u_dual_reg_d0.io.dataout_width
    val reg2dp_d0_dataout_channel = u_dual_reg_d0.io.dataout_channel
    val reg2dp_d0_x_dilation_ext = u_dual_reg_d0.io.x_dilation_ext
    val reg2dp_d0_y_dilation_ext = u_dual_reg_d0.io.y_dilation_ext
    val reg2dp_d0_entries = u_dual_reg_d0.io.entries
    val reg2dp_d0_conv_mode = u_dual_reg_d0.io.conv_mode
    val reg2dp_d0_data_reuse = u_dual_reg_d0.io.data_reuse
    val reg2dp_d0_in_precision = u_dual_reg_d0.io.in_precision
    val reg2dp_d0_proc_precision = u_dual_reg_d0.io.proc_precision
    val reg2dp_d0_skip_data_rls = u_dual_reg_d0.io.skip_data_rls
    val reg2dp_d0_skip_weight_rls = u_dual_reg_d0.io.skip_weight_rls
    val reg2dp_d0_weight_reuse = u_dual_reg_d0.io.weight_reuse
    val reg2dp_d0_op_en_trigger = u_dual_reg_d0.io.op_en_trigger
    val reg2dp_d0_y_extension = u_dual_reg_d0.io.y_extension
    val reg2dp_d0_pra_truncate = u_dual_reg_d0.io.pra_truncate
    val reg2dp_d0_rls_slices = u_dual_reg_d0.io.rls_slices
    val reg2dp_d0_weight_bytes = u_dual_reg_d0.io.weight_bytes
    val reg2dp_d0_weight_format = u_dual_reg_d0.io.weight_format
    val reg2dp_d0_weight_height_ext = u_dual_reg_d0.io.weight_height_ext
    val reg2dp_d0_weight_width_ext = u_dual_reg_d0.io.weight_width_ext
    val reg2dp_d0_weight_channel_ext = u_dual_reg_d0.io.weight_channel_ext
    val reg2dp_d0_weight_kernel = u_dual_reg_d0.io.weight_kernel
    val reg2dp_d0_wmb_bytes = u_dual_reg_d0.io.wmb_bytes
    val reg2dp_d0_pad_left = u_dual_reg_d0.io.pad_left
    val reg2dp_d0_pad_top = u_dual_reg_d0.io.pad_top
    val reg2dp_d0_pad_value = u_dual_reg_d0.io.pad_value            


    val d1_reg_wr_en = Wire(Bool())
    val reg2dp_d1_op_en = RegInit(false.B)

    val u_dual_reg_d1 = Module(new NV_NVDLA_CSC_dual_reg)
    u_dual_reg_d1.io.reg_offset := reg_offset
    u_dual_reg_d1.io.reg_wr_data := reg_wr_data
    u_dual_reg_d1.io.reg_wr_en := d0_reg_wr_en
    u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d1.io.op_en := reg2dp_d1_op_en
    val d1_reg_rd_data = u_dual_reg_d1.io.reg_rd_data
    val reg2dp_d1_atomics = u_dual_reg_d1.io.atomics
    val reg2dp_d1_data_bank = u_dual_reg_d1.io.data_bank
    val reg2dp_d1_weight_bank = u_dual_reg_d1.io.weight_bank
    val reg2dp_d1_batches = u_dual_reg_d1.io.batches
    val reg2dp_d1_conv_x_stride_ext = u_dual_reg_d1.io.conv_x_stride_ext
    val reg2dp_d1_conv_y_stride_ext = u_dual_reg_d1.io.conv_y_stride_ext
    val reg2dp_d1_cya = u_dual_reg_d1.io.cya
    val reg2dp_d1_datain_format = u_dual_reg_d1.io.datain_format
    val reg2dp_d1_datain_height_ext = u_dual_reg_d1.io.datain_height_ext
    val reg2dp_d1_datain_width_ext = u_dual_reg_d1.io.datain_width_ext
    val reg2dp_d1_datain_channel_ext = u_dual_reg_d1.io.datain_channel_ext
    val reg2dp_d1_dataout_height = u_dual_reg_d1.io.dataout_height
    val reg2dp_d1_dataout_width = u_dual_reg_d1.io.dataout_width
    val reg2dp_d1_dataout_channel = u_dual_reg_d1.io.dataout_channel
    val reg2dp_d1_x_dilation_ext = u_dual_reg_d1.io.x_dilation_ext
    val reg2dp_d1_y_dilation_ext = u_dual_reg_d1.io.y_dilation_ext
    val reg2dp_d1_entries = u_dual_reg_d1.io.entries
    val reg2dp_d1_conv_mode = u_dual_reg_d1.io.conv_mode
    val reg2dp_d1_data_reuse = u_dual_reg_d1.io.data_reuse
    val reg2dp_d1_in_precision = u_dual_reg_d1.io.in_precision
    val reg2dp_d1_proc_precision = u_dual_reg_d1.io.proc_precision
    val reg2dp_d1_skip_data_rls = u_dual_reg_d1.io.skip_data_rls
    val reg2dp_d1_skip_weight_rls = u_dual_reg_d1.io.skip_weight_rls
    val reg2dp_d1_weight_reuse = u_dual_reg_d1.io.weight_reuse
    val reg2dp_d1_op_en_trigger = u_dual_reg_d1.io.op_en_trigger
    val reg2dp_d1_y_extension = u_dual_reg_d1.io.y_extension
    val reg2dp_d1_pra_truncate = u_dual_reg_d1.io.pra_truncate
    val reg2dp_d1_rls_slices = u_dual_reg_d1.io.rls_slices
    val reg2dp_d1_weight_bytes = u_dual_reg_d1.io.weight_bytes
    val reg2dp_d1_weight_format = u_dual_reg_d1.io.weight_format
    val reg2dp_d1_weight_height_ext = u_dual_reg_d1.io.weight_height_ext
    val reg2dp_d1_weight_width_ext = u_dual_reg_d1.io.weight_width_ext
    val reg2dp_d1_weight_channel_ext = u_dual_reg_d1.io.weight_channel_ext
    val reg2dp_d1_weight_kernel = u_dual_reg_d1.io.weight_kernel
    val reg2dp_d1_wmb_bytes = u_dual_reg_d1.io.wmb_bytes
    val reg2dp_d1_pad_left = u_dual_reg_d1.io.pad_left
    val reg2dp_d1_pad_top = u_dual_reg_d1.io.pad_top
    val reg2dp_d1_pad_value = u_dual_reg_d1.io.pad_value         

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CONSUMER PIONTER IN GENERAL SINGLE REGISTER GROUP         //
    //                                                                    //
    //////////////////////////////////////////////////////////////////////// 
    val dp2reg_consumer_w = ~dp2reg_consumer

    when(io.dp2reg_done){
        dp2reg_consumer := dp2reg_consumer_w
    }

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE TWO STATUS FIELDS IN GENERAL SINGLE REGISTER GROUP        //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    dp2reg_status_0 := Mux(reg2dp_d0_op_en === false.B, "h0".asUInt(2.W), 
                       Mux(dp2reg_consumer === true.B, "h2".asUInt(2.W), 
                       "h1".asUInt(2.W)))

    dp2reg_status_1 := Mux(reg2dp_d1_op_en === false.B, "h0".asUInt(2.W), 
                       Mux(dp2reg_consumer === false.B, "h2".asUInt(2.W), 
                       "h1".asUInt(2.W)))

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OP_EN LOGIC                                               //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    val reg2dp_op_en_reg = RegInit("b0".asUInt(3.W))
    val reg2dp_d0_op_en_w = Mux(~reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg_wr_data(0), 
                            Mux(io.dp2reg_done && dp2reg_consumer === false.B, false.B, reg2dp_d0_op_en))

    reg2dp_d0_op_en := reg2dp_d0_op_en_w

    val reg2dp_d1_op_en_w =  Mux(~reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg_wr_data(0), 
                             Mux(io.dp2reg_done && dp2reg_consumer === true.B, false.B, reg2dp_d1_op_en))

    reg2dp_d1_op_en := reg2dp_d1_op_en_w

    val reg2dp_op_en_ori = Mux(dp2reg_consumer, reg2dp_d1_op_en, reg2dp_d0_op_en)
    val reg2dp_op_en_reg_w = Mux(io.dp2reg_done,  "b0".asUInt(3.W), Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori))

    reg2dp_op_en_reg := reg2dp_op_en_reg_w 
    io.reg2dp_op_en := reg2dp_op_en_reg(2)

    io.slcg_op_en := ShiftRegister(Fill(4, reg2dp_op_en_ori), 3)
    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE ACCESS LOGIC TO EACH REGISTER GROUP                       //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    //EACH subunit has 4KB address space 
    val reg_wr_en = Wire(Bool())
    val select_s = Mux(reg_offset(11,0) < "h0008".asUInt(32.W), true.B, false.B)
    val select_d0 = (reg_offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === false.B)
    val select_d1 = (reg_offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === true.B)

    s_reg_wr_en := reg_wr_en & select_s
    d0_reg_wr_en := reg_wr_en & select_d0 & !reg2dp_d0_op_en
    d1_reg_wr_en := reg_wr_en & select_d1 & !reg2dp_d1_op_en

    val reg_rd_data = (Fill(32, select_s) & s_reg_rd_data)|
                        (Fill(32, select_d0) & d0_reg_rd_data)|
                        (Fill(32, select_d1)& d1_reg_rd_data)

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    val req_pvld = RegInit(false.B)
    val req_pd = RegInit("b0".asUInt(63.W))

    req_pvld := io.csb2csc_req_pvld
    when(io.csb2csc_req_pvld){
        req_pd := io.csb2csc_req_pd
    }

    // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  req_pd ) 
    val req_addr = req_pd(21, 0)
    val req_wdat = req_pd(53, 22)
    val req_write = req_pd(54)
    val req_nposted = req_pd(55)
    val req_srcpriv = req_pd(56)
    val req_wrbe = req_pd(60, 57)
    val req_level = req_pd(62, 61)

    io.csb2csc_req_prdy := true.B

    //Address in CSB master is word aligned while address in regfile is byte aligned.
    reg_offset := Cat(req_addr, "b0".asUInt(2.W))
    reg_wr_data := req_wdat
    reg_wr_en := req_pvld & req_write
    val reg_rd_en = req_pvld & ~req_write

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_rd_erpt ,  csb_rresp_ ,  csb_rresp_pd_w )
    val csb_rresp_rdat = reg_rd_data
    val csb_rresp_error = false.B
    val csb_rresp_pd_w = Cat(false.B, csb_rresp_error, csb_rresp_rdat)

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_wr_erpt ,  csb_wresp_ ,  csb_wresp_pd_w 
    val csb_wresp_rdat = "b0".asUInt(32.W)
    val csb_wresp_error = false.B
    val csb_wresp_pd_w = Cat(true.B, csb_wresp_error, csb_wresp_rdat)

    val csc2csb_resp_pd_out = RegInit("b0".asUInt(34.W))
    val csc2csb_resp_valid_out = RegInit(false.B)

    when(reg_rd_en){
        csc2csb_resp_pd_out := csb_rresp_pd_w
    }
    .elsewhen(reg_wr_en & req_nposted){
        csc2csb_resp_valid_out := csb_wresp_pd_w
    }
    csc2csb_resp_valid_out := (reg_wr_en & req_nposted) | reg_rd_en

    io.csc2csb_resp_pd := csc2csb_resp_pd_out
    io.csc2csb_resp_valid := csc2csb_resp_valid_out

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////

    io.reg2dp_atomics := Mux(dp2reg_consumer, reg2dp_d1_atomics, reg2dp_d0_atomics)
    io.reg2dp_data_bank := Mux(dp2reg_consumer, reg2dp_d1_data_bank, reg2dp_d0_data_bank)
    io.reg2dp_weight_bank := Mux(dp2reg_consumer, reg2dp_d1_weight_bank, reg2dp_d0_weight_bank)
    io.reg2dp_batches := Mux(dp2reg_consumer, reg2dp_d1_batches, reg2dp_d0_batches)
    io.reg2dp_conv_x_stride_ext := Mux(dp2reg_consumer, reg2dp_d1_conv_x_stride_ext, reg2dp_d0_conv_x_stride_ext)
    io.reg2dp_conv_y_stride_ext := Mux(dp2reg_consumer, reg2dp_d1_conv_y_stride_ext, reg2dp_d0_conv_y_stride_ext)
    io.reg2dp_cya := Mux(dp2reg_consumer, reg2dp_d1_cya, reg2dp_d0_cya)
    io.reg2dp_datain_format := Mux(dp2reg_consumer, reg2dp_d1_datain_format, reg2dp_d0_datain_format)
    io.reg2dp_datain_height_ext := Mux(dp2reg_consumer, reg2dp_d1_datain_height_ext, reg2dp_d0_datain_height_ext)
    io.reg2dp_datain_width_ext := Mux(dp2reg_consumer, reg2dp_d1_datain_width_ext, reg2dp_d0_datain_width_ext)
    io.reg2dp_datain_channel_ext := Mux(dp2reg_consumer, reg2dp_d1_datain_channel_ext, reg2dp_d0_datain_channel_ext)
    io.reg2dp_dataout_height := Mux(dp2reg_consumer, reg2dp_d1_dataout_height, reg2dp_d0_dataout_height)
    io.reg2dp_dataout_width := Mux(dp2reg_consumer, reg2dp_d1_dataout_width, reg2dp_d0_dataout_width)
    io.reg2dp_dataout_channel := Mux(dp2reg_consumer, reg2dp_d1_dataout_channel, reg2dp_d0_dataout_channel)
    io.reg2dp_x_dilation_ext := Mux(dp2reg_consumer, reg2dp_d1_x_dilation_ext, reg2dp_d0_x_dilation_ext)
    io.reg2dp_y_dilation_ext := Mux(dp2reg_consumer, reg2dp_d1_y_dilation_ext, reg2dp_d0_y_dilation_ext)
    io.reg2dp_entries := Mux(dp2reg_consumer, reg2dp_d1_entries, reg2dp_d0_entries)
    io.reg2dp_conv_mode := Mux(dp2reg_consumer, reg2dp_d1_conv_mode, reg2dp_d0_conv_mode)
    io.reg2dp_data_reuse := Mux(dp2reg_consumer, reg2dp_d1_data_reuse, reg2dp_d0_data_reuse)
    io.reg2dp_in_precision := Mux(dp2reg_consumer, reg2dp_d1_in_precision, reg2dp_d0_in_precision)
    io.reg2dp_proc_precision := Mux(dp2reg_consumer, reg2dp_d1_proc_precision, reg2dp_d0_proc_precision)
    io.reg2dp_skip_data_rls := Mux(dp2reg_consumer, reg2dp_d1_skip_data_rls, reg2dp_d0_skip_data_rls)
    io.reg2dp_skip_weight_rls := Mux(dp2reg_consumer, reg2dp_d1_skip_weight_rls, reg2dp_d0_skip_weight_rls)
    io.reg2dp_weight_reuse := Mux(dp2reg_consumer, reg2dp_d1_weight_reuse, reg2dp_d0_weight_reuse)
    io.reg2dp_y_extension := Mux(dp2reg_consumer, reg2dp_d1_y_extension, reg2dp_d0_y_extension)
    io.reg2dp_pra_truncate := Mux(dp2reg_consumer, reg2dp_d1_pra_truncate, reg2dp_d0_pra_truncate)
    io.reg2dp_rls_slices := Mux(dp2reg_consumer, reg2dp_d1_rls_slices, reg2dp_d0_rls_slices)
    io.reg2dp_weight_bytes := Mux(dp2reg_consumer, reg2dp_d1_weight_bytes, reg2dp_d0_weight_bytes)
    io.reg2dp_weight_format := Mux(dp2reg_consumer, reg2dp_d1_weight_format, reg2dp_d0_weight_format)
    io.reg2dp_weight_height_ext := Mux(dp2reg_consumer, reg2dp_d1_weight_height_ext, reg2dp_d0_weight_height_ext)
    io.reg2dp_weight_width_ext := Mux(dp2reg_consumer, reg2dp_d1_weight_width_ext, reg2dp_d0_weight_width_ext)
    io.reg2dp_weight_channel_ext := Mux(dp2reg_consumer, reg2dp_d1_weight_channel_ext, reg2dp_d0_weight_channel_ext)
    io.reg2dp_weight_kernel := Mux(dp2reg_consumer, reg2dp_d1_weight_kernel, reg2dp_d0_weight_kernel)
    io.reg2dp_wmb_bytes := Mux(dp2reg_consumer, reg2dp_d1_wmb_bytes, reg2dp_d0_wmb_bytes)
    io.reg2dp_pad_left := Mux(dp2reg_consumer, reg2dp_d1_pad_left, reg2dp_d0_pad_left)
    io.reg2dp_pad_top := Mux(dp2reg_consumer, reg2dp_d1_pad_top, reg2dp_d0_pad_top)
    io.reg2dp_pad_value := Mux(dp2reg_consumer, reg2dp_d1_pad_value, reg2dp_d0_pad_value)


}}

object NV_NVDLA_CSC_regfileDriver extends App {
  implicit val conf: cscConfiguration = new cscConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_CSC_regfile())
}
