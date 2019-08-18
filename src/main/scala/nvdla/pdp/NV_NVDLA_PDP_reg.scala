// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._



// //Implementation overview of ping-pong register file.

// class NV_NVDLA_PDP_reg extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())      

//         //csb2pdp
//         val csb2pdp_req_pd = Input(UInt(63.W))
//         val csb2pdp_req_pvld = Input(Bool())
//         val csb2pdp_req_prdy = Output(Bool())
        
//         val pdp2csb_resp_pd = Output(UInt(34.W))
//         val pdp2csb_resp_valid = Output(Bool())

//         //reg2dp
//         val dp2reg_d0_perf_write_stall = Input(UInt(32.W))
//         val dp2reg_d1_perf_write_stall = Input(UInt(32.W))
//         val dp2reg_done = Input(Bool())
//         val dp2reg_inf_input_num = Input(UInt(32.W))
//         val dp2reg_nan_input_num = Input(UInt(32.W))
//         val dp2reg_nan_output_num = Input(UInt(32.W))

//         val reg2dp_cube_in_channel = Output(UInt(13.W))
//         val reg2dp_cube_in_height = Output(UInt(13.W))
//         val reg2dp_cube_in_width = Output(UInt(13.W))
//         val reg2dp_cube_out_channel = Output(UInt(13.W))
//         val reg2dp_cube_out_height = Output(UInt(13.W))
//         val reg2dp_cube_out_width = Output(UInt(13.W))
//         val reg2dp_cya = Output(UInt(32.W))
//         val reg2dp_dma_en = Output(Bool())
//         val reg2dp_dst_base_addr_high = Output(UInt(32.W))
//         val reg2dp_dst_base_addr_low = Output(UInt(32.W))
//         val reg2dp_dst_line_stride = Output(UInt(32.W))
//         val reg2dp_dst_ram_type = Output(Bool())
//         val reg2dp_dst_surface_stride = Output(UInt(32.W))
//         val reg2dp_flying_mode = Output(Bool())
//         val reg2dp_input_data = Output(UInt(2.W))
//         val reg2dp_interrupt_ptr = Output(Bool())
//         val reg2dp_kernel_height = Output(UInt(4.W))
//         val reg2dp_kernel_stride_height = Output(UInt(4.W))
//         val reg2dp_kernel_stride_width = Output(UInt(4.W))
//         val reg2dp_kernel_width = Output(UInt(4.W))
//         val reg2dp_nan_to_zero = Output(Bool())
//         val reg2dp_op_en = Output(Bool())
//         val reg2dp_pad_bottom = Output(UInt(3.W))
//         val reg2dp_pad_left = Output(UInt(3.W))
//         val reg2dp_pad_right = Output(UInt(3.W))
//         val reg2dp_pad_top = Output(UInt(3.W))
//         val reg2dp_pad_value_1x = Output(UInt(19.W))
//         val reg2dp_pad_value_2x = Output(UInt(19.W))
//         val reg2dp_pad_value_3x = Output(UInt(19.W))
//         val reg2dp_pad_value_4x = Output(UInt(19.W))
//         val reg2dp_pad_value_5x = Output(UInt(19.W))
//         val reg2dp_pad_value_6x = Output(UInt(19.W))
//         val reg2dp_pad_value_7x = Output(UInt(19.W))
//         val reg2dp_partial_width_in_first = Output(UInt(10.W))
//         val reg2dp_partial_width_in_last = Output(UInt(10.W))
//         val reg2dp_partial_width_in_mid = Output(UInt(10.W))
//         val reg2dp_partial_width_out_first = Output(UInt(10.W))
//         val reg2dp_partial_width_out_last = Output(UInt(10.W))
//         val reg2dp_partial_width_out_mid = Output(UInt(10.W))
//         val reg2dp_pooling_method = Output(UInt(2.W))
//         val reg2dp_recip_kernel_height = Output(UInt(17.W))
//         val reg2dp_recip_kernel_width = Output(UInt(17.W))
//         val reg2dp_split_num = Output(UInt(8.W))
//         val reg2dp_src_base_addr_high = Output(UInt(32.W))
//         val reg2dp_src_base_addr_low = Output(UInt(32.W))
//         val reg2dp_src_line_stride = Output(UInt(32.W))
//         val reg2dp_src_surface_stride = Output(UInt(32.W))

//         //slave cg op
//         val slcg_op_en = Output(UInt(3.W))
//     })
// //                             
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │              |-------------|
// //       │       ───       │              |     CSB     |
// //       │  ─┬┘       └┬─  │              |-------------|
// //       │                 │                    ||
// //       │       ─┴─       │                    reg   <= DP(data processor)
// //       │                 │                    ||
// //       └───┐         ┌───┘              |-------------|
// //           │         │                  |     PDP     |
// //           │         │                  |-------------|
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 

// withClock(io.nvdla_core_clk){

//     //Instance single register group
//     val dp2reg_consumer = RegInit(false.B)
//     val reg.offset = Wire(UInt(12.W))
//     val reg_wr_data = Wire(UInt(32.W))
//     val s_reg_wr_en = Wire(Bool())
//     val dp2reg_status_0 = Wire(UInt(2.W))
//     val dp2reg_status_1 = Wire(UInt(2.W))

//     val u_single_reg = Module(new NV_NVDLA_PDP_REG_single)

//     u_single_reg.io.reg.offset := reg.offset
//     u_single_reg.io.reg_wr_data := reg_wr_data 
//     u_single_reg.io.reg_wr_en := s_reg_wr_en
//     u_single_reg.io.nvdla_core_clk := io.nvdla_core_clk
//     u_single_reg.io.consumer := dp2reg_consumer
//     u_single_reg.io.status_0 := dp2reg_status_0
//     u_single_reg.io.status_1 := dp2reg_status_1 
//     val s_reg_rd_data = u_single_reg.io.reg_rd_data
//     val reg2dp_producer = u_single_reg.io.producer

//     //Instance two duplicated register groups
//     val d0_reg_wr_en = Wire(Bool())
//     val dp2reg_d0_inf_input_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d0_nan_input_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d0_nan_output_num = RegInit("b0".asUInt(32.W))
//     val reg2dp_d0_op_en = RegInit(false.B)

//     val u_dual_reg_d0 = Module(new NV_NVDLA_PDP_REG_dual)
//     u_dual_reg_d0.io.reg.offset := reg.offset
//     u_dual_reg_d0.io.reg_wr_data := reg_wr_data
//     u_dual_reg_d0.io.reg_wr_en := d0_reg_wr_en
//     u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
//     u_dual_reg_d0.io.op_en := reg2dp_d0_op_en
//     u_dual_reg_d0.io.perf_write_stall := io.dp2reg_d0_perf_write_stall
//     u_dual_reg_d0.io.inf_input_num := dp2reg_d0_inf_input_num
//     u_dual_reg_d0.io.nan_input_num := dp2reg_d0_nan_input_num
//     u_dual_reg_d0.io.nan_output_num := dp2reg_d0_nan_output_num
//     val reg2dp_d0_cya = u_dual_reg_d0.io.cya
//     val reg2dp_d0_cube_in_channel = u_dual_reg_d0.io.cube_in_channel
//     val reg2dp_d0_cube_in_height = u_dual_reg_d0.io.cube_in_height
//     val reg2dp_d0_cube_in_width = u_dual_reg_d0.io.cube_in_width
//     val reg2dp_d0_cube_out_channel = u_dual_reg_d0.io.cube_out_channel
//     val reg2dp_d0_cube_out_height = u_dual_reg_d0.io.cube_out_height
//     val reg2dp_d0_cube_out_width = u_dual_reg_d0.io.cube_out_width
//     val reg2dp_d0_input_data = u_dual_reg_d0.io.input_data
//     val reg2dp_d0_dst_base_addr_high = u_dual_reg_d0.io.dst_base_addr_high
//     val reg2dp_d0_dst_base_addr_low = u_dual_reg_d0.io.dst_base_addr_low
//     val reg2dp_d0_dst_line_stride = u_dual_reg_d0.io.dst_line_stride
//     val reg2dp_d0_dst_ram_type = u_dual_reg_d0.io.dst_ram_type
//     val reg2dp_d0_dst_surface_stride = u_dual_reg_d0.io.dst_surface_stride
//     val reg2dp_d0_nan_to_zero = u_dual_reg_d0.io.nan_to_zero
//     val reg2dp_d0_flying_mode = u_dual_reg_d0.io.flying_mode
//     val reg2dp_d0_pooling_method = u_dual_reg_d0.io.pooling_method
//     val reg2dp_d0_split_num = u_dual_reg_d0.io.split_num
//     val reg2dp_d0_op_en_trigger = u_dual_reg_d0.io.op_en_trigger
//     val reg2dp_d0_partial_width_in_first = u_dual_reg_d0.io.partial_width_in_first
//     val reg2dp_d0_partial_width_in_last = u_dual_reg_d0.io.partial_width_in_last
//     val reg2dp_d0_partial_width_in_mid = u_dual_reg_d0.io.partial_width_in_mid
//     val reg2dp_d0_partial_width_out_first = u_dual_reg_d0.io.partial_width_out_first
//     val reg2dp_d0_partial_width_out_last = u_dual_reg_d0.io.partial_width_out_last
//     val reg2dp_d0_partial_width_out_mid = u_dual_reg_d0.io.partial_width_out_mid
//     val reg2dp_d0_dma_en = u_dual_reg_d0.io.dma_en
//     val reg2dp_d0_kernel_height = u_dual_reg_d0.io.kernel_height
//     val reg2dp_d0_kernel_stride_height = u_dual_reg_d0.io.kernel_stride_height
//     val reg2dp_d0_kernel_stride_width = u_dual_reg_d0.io.kernel_stride_width
//     val reg2dp_d0_kernel_width = u_dual_reg_d0.io.kernel_width
//     val reg2dp_d0_pad_bottom = u_dual_reg_d0.io.pad_bottom
//     val reg2dp_d0_pad_left = u_dual_reg_d0.io.pad_left
//     val reg2dp_d0_pad_right = u_dual_reg_d0.io.pad_right
//     val reg2dp_d0_pad_top = u_dual_reg_d0.io.pad_top
//     val reg2dp_d0_pad_value_1x = u_dual_reg_d0.io.pad_value_1x
//     val reg2dp_d0_pad_value_2x = u_dual_reg_d0.io.pad_value_2x
//     val reg2dp_d0_pad_value_3x = u_dual_reg_d0.io.pad_value_3x
//     val reg2dp_d0_pad_value_4x = u_dual_reg_d0.io.pad_value_4x
//     val reg2dp_d0_pad_value_5x = u_dual_reg_d0.io.pad_value_5x
//     val reg2dp_d0_pad_value_6x  = u_dual_reg_d0.io.pad_value_6x
//     val reg2dp_d0_pad_value_7x = u_dual_reg_d0.io.pad_value_7x
//     val reg2dp_d0_recip_kernel_height = u_dual_reg_d0.io.recip_kernel_height
//     val reg2dp_d0_recip_kernel_width = u_dual_reg_d0.io.recip_kernel_width
//     val reg2dp_d0_src_base_addr_high = u_dual_reg_d0.io.src_base_addr_high
//     val reg2dp_d0_src_base_addr_low = u_dual_reg_d0.io.src_base_addr_low
//     val reg2dp_d0_src_line_stride = u_dual_reg_d0.io.src_line_stride 
//     val reg2dp_d0_src_surface_stride = u_dual_reg_d0.io.src_surface_stride 
//     val d0_reg_rd_data = u_dual_reg_d0.io.reg_rd_data
                        
//     val d1_reg_wr_en = Wire(Bool())
//     val dp2reg_d1_inf_input_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d1_nan_input_num = RegInit("b0".asUInt(32.W))
//     val dp2reg_d1_nan_output_num = RegInit("b0".asUInt(32.W))
//     val reg2dp_d1_op_en = RegInit(false.B)

//     val u_dual_reg_d1 = Module(new NV_NVDLA_PDP_REG_dual)
//     u_dual_reg_d1.io.reg.offset := reg.offset
//     u_dual_reg_d1.io.reg_wr_data := reg_wr_data
//     u_dual_reg_d1.io.reg_wr_en := d1_reg_wr_en
//     u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk
//     u_dual_reg_d1.io.op_en := reg2dp_d1_op_en
//     u_dual_reg_d1.io.perf_write_stall := io.dp2reg_d1_perf_write_stall
//     u_dual_reg_d1.io.inf_input_num := dp2reg_d1_inf_input_num
//     u_dual_reg_d1.io.nan_input_num := dp2reg_d1_nan_input_num
//     u_dual_reg_d1.io.nan_output_num := dp2reg_d1_nan_output_num
//     val reg2dp_d1_cya = u_dual_reg_d1.io.cya
//     val reg2dp_d1_cube_in_channel = u_dual_reg_d1.io.cube_in_channel
//     val reg2dp_d1_cube_in_height = u_dual_reg_d1.io.cube_in_height
//     val reg2dp_d1_cube_in_width = u_dual_reg_d1.io.cube_in_width
//     val reg2dp_d1_cube_out_channel = u_dual_reg_d1.io.cube_out_channel
//     val reg2dp_d1_cube_out_height = u_dual_reg_d1.io.cube_out_height
//     val reg2dp_d1_cube_out_width = u_dual_reg_d1.io.cube_out_width
//     val reg2dp_d1_input_data = u_dual_reg_d1.io.input_data
//     val reg2dp_d1_dst_base_addr_high = u_dual_reg_d1.io.dst_base_addr_high
//     val reg2dp_d1_dst_base_addr_low = u_dual_reg_d1.io.dst_base_addr_low
//     val reg2dp_d1_dst_line_stride = u_dual_reg_d1.io.dst_line_stride
//     val reg2dp_d1_dst_ram_type = u_dual_reg_d1.io.dst_ram_type
//     val reg2dp_d1_dst_surface_stride = u_dual_reg_d1.io.dst_surface_stride
//     val reg2dp_d1_nan_to_zero = u_dual_reg_d1.io.nan_to_zero
//     val reg2dp_d1_flying_mode = u_dual_reg_d1.io.flying_mode
//     val reg2dp_d1_pooling_method = u_dual_reg_d1.io.pooling_method
//     val reg2dp_d1_split_num = u_dual_reg_d1.io.split_num
//     val reg2dp_d1_op_en_trigger = u_dual_reg_d1.io.op_en_trigger
//     val reg2dp_d1_partial_width_in_first = u_dual_reg_d1.io.partial_width_in_first
//     val reg2dp_d1_partial_width_in_last = u_dual_reg_d1.io.partial_width_in_last
//     val reg2dp_d1_partial_width_in_mid = u_dual_reg_d1.io.partial_width_in_mid
//     val reg2dp_d1_partial_width_out_first = u_dual_reg_d1.io.partial_width_out_first
//     val reg2dp_d1_partial_width_out_last = u_dual_reg_d1.io.partial_width_out_last
//     val reg2dp_d1_partial_width_out_mid = u_dual_reg_d1.io.partial_width_out_mid
//     val reg2dp_d1_dma_en = u_dual_reg_d1.io.dma_en
//     val reg2dp_d1_kernel_height = u_dual_reg_d1.io.kernel_height
//     val reg2dp_d1_kernel_stride_height = u_dual_reg_d1.io.kernel_stride_height
//     val reg2dp_d1_kernel_stride_width = u_dual_reg_d1.io.kernel_stride_width
//     val reg2dp_d1_kernel_width = u_dual_reg_d1.io.kernel_width
//     val reg2dp_d1_pad_bottom = u_dual_reg_d1.io.pad_bottom
//     val reg2dp_d1_pad_left = u_dual_reg_d1.io.pad_left
//     val reg2dp_d1_pad_right = u_dual_reg_d1.io.pad_right
//     val reg2dp_d1_pad_top = u_dual_reg_d1.io.pad_top
//     val reg2dp_d1_pad_value_1x = u_dual_reg_d1.io.pad_value_1x
//     val reg2dp_d1_pad_value_2x = u_dual_reg_d1.io.pad_value_2x
//     val reg2dp_d1_pad_value_3x = u_dual_reg_d1.io.pad_value_3x
//     val reg2dp_d1_pad_value_4x = u_dual_reg_d1.io.pad_value_4x
//     val reg2dp_d1_pad_value_5x = u_dual_reg_d1.io.pad_value_5x
//     val reg2dp_d1_pad_value_6x  = u_dual_reg_d1.io.pad_value_6x
//     val reg2dp_d1_pad_value_7x = u_dual_reg_d1.io.pad_value_7x
//     val reg2dp_d1_recip_kernel_height = u_dual_reg_d1.io.recip_kernel_height
//     val reg2dp_d1_recip_kernel_width = u_dual_reg_d1.io.recip_kernel_width
//     val reg2dp_d1_src_base_addr_high = u_dual_reg_d1.io.src_base_addr_high
//     val reg2dp_d1_src_base_addr_low = u_dual_reg_d1.io.src_base_addr_low
//     val reg2dp_d1_src_line_stride = u_dual_reg_d1.io.src_line_stride 
//     val reg2dp_d1_src_surface_stride = u_dual_reg_d1.io.src_surface_stride 
//     val d1_reg_rd_data = u_dual_reg_d1.io.reg_rd_data

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE CONSUMER PIONTER IN GENERAL SINGLE REGISTER GROUP         //
//     //                                                                    //
//     //////////////////////////////////////////////////////////////////////// 
//     val dp2reg_consumer_w = ~dp2reg_consumer

//     when(io.dp2reg_done){
//         dp2reg_consumer := dp2reg_consumer_w
//     }

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE TWO STATUS FIELDS IN GENERAL SINGLE REGISTER GROUP        //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     dp2reg_status_0 := Mux(reg2dp_d0_op_en === false.B, "h0".asUInt(2.W), 
//                        Mux(dp2reg_consumer === true.B, "h2".asUInt(2.W), 
//                        "h1".asUInt(2.W)))

//     dp2reg_status_1 := Mux(reg2dp_d1_op_en === false.B, "h0".asUInt(2.W), 
//                        Mux(dp2reg_consumer === false.B, "h2".asUInt(2.W), 
//                        "h1".asUInt(2.W)))

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE OP_EN LOGIC                                               //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     val reg2dp_op_en_reg = RegInit("b0".asUInt(3.W))
//     val reg2dp_d0_op_en_w = Mux(~reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg_wr_data(0), 
//                             Mux(io.dp2reg_done && dp2reg_consumer === false.B, false.B, reg2dp_d0_op_en))

//     reg2dp_d0_op_en := reg2dp_d0_op_en_w

//     val reg2dp_d1_op_en_w =  Mux(~reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg_wr_data(0), 
//                              Mux(io.dp2reg_done && dp2reg_consumer === true.B, false.B, reg2dp_d1_op_en))

//     reg2dp_d1_op_en := reg2dp_d1_op_en_w

//     val reg2dp_op_en_ori = Mux(dp2reg_consumer, reg2dp_d1_op_en, reg2dp_d0_op_en)
//     val reg2dp_op_en_reg_w = Mux(io.dp2reg_done,  "b0".asUInt(3.W), Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori))

//     reg2dp_op_en_reg := reg2dp_op_en_reg_w 
//     io.reg2dp_op_en := reg2dp_op_en_reg(2)

//     io.slcg_op_en := ShiftRegister(Fill(11, reg2dp_op_en_ori), 3)
//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE ACCESS LOGIC TO EACH REGISTER GROUP                       //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     //EACH subunit has 4KB address space 
//     val reg_wr_en = Wire(Bool())
//     val select_s = Mux(reg.offset(11,0) < "h0008".asUInt(32.W), true.B, false.B)
//     val select_d0 = (reg.offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === false.B)
//     val select_d1 = (reg.offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === true.B)

//     s_reg_wr_en := reg_wr_en & select_s
//     d0_reg_wr_en := reg_wr_en & select_d0 & !reg2dp_d0_op_en
//     d1_reg_wr_en := reg_wr_en & select_d1 & !reg2dp_d1_op_en

//     val reg_rd_data = (Fill(32, select_s) & s_reg_rd_data)|
//                         (Fill(32, select_d0) & d0_reg_rd_data)|
//                         (Fill(32, select_d1)& d1_reg_rd_data)

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     val req_pvld = RegInit(false.B)
//     val req_pd = RegInit("b0".asUInt(63.W))

//     req_pvld := io.csb2pdp_req_pvld
//     when(io.csb2pdp_req_pvld){
//         req_pd := io.csb2pdp_req_pd
//     }

//     // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  req_pd ) 
//     val req_addr = req_pd(21, 0)
//     val req_wdat = req_pd(53, 22)
//     val req_write = req_pd(54)
//     val req_nposted = req_pd(55)
//     val req_srcpriv = req_pd(56)
//     val req_wrbe = req_pd(60, 57)
//     val req_level = req_pd(62, 61)

//     io.csb2pdp_req_prdy := true.B

//     //Address in CSB master is word aligned while address in regfile is byte aligned.
//     reg.offset := Cat(req_addr, "b0".asUInt(2.W))
//     reg_wr_data := req_wdat
//     reg_wr_en := req_pvld & req_write
//     val reg_rd_en = req_pvld & ~req_write

//     // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_rd_erpt ,  csb_rresp_ ,  csb_rresp_pd_w )
//     val csb_rresp_rdat = reg_rd_data
//     val csb_rresp_error = false.B
//     val csb_rresp_pd_w = Cat(false.B, csb_rresp_error, csb_rresp_rdat)

//     // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_wr_erpt ,  csb_wresp_ ,  csb_wresp_pd_w 
//     val csb_wresp_rdat = "b0".asUInt(32.W)
//     val csb_wresp_error = false.B
//     val csb_wresp_pd_w = Cat(true.B, csb_wresp_error, csb_wresp_rdat)

//     val pdp2csb_resp_pd_out = RegInit("b0".asUInt(34.W))
//     val pdp2csb_resp_valid_out = RegInit(false.B)

//     when(reg_rd_en){
//         pdp2csb_resp_pd_out := csb_rresp_pd_w
//     }
//     .elsewhen(reg_wr_en & req_nposted){
//         pdp2csb_resp_pd_out := csb_wresp_pd_w
//     }
//     pdp2csb_resp_valid_out := (reg_wr_en & req_nposted) | reg_rd_en

//     io.pdp2csb_resp_pd := pdp2csb_resp_pd_out
//     io.pdp2csb_resp_valid := pdp2csb_resp_valid_out

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////

//     io.reg2dp_cya := Mux(dp2reg_consumer, reg2dp_d1_cya, reg2dp_d0_cya);
//     io.reg2dp_cube_in_channel := Mux(dp2reg_consumer, reg2dp_d1_cube_in_channel, reg2dp_d0_cube_in_channel);
//     io.reg2dp_cube_in_height := Mux(dp2reg_consumer, reg2dp_d1_cube_in_height, reg2dp_d0_cube_in_height);
//     io.reg2dp_cube_in_width := Mux(dp2reg_consumer, reg2dp_d1_cube_in_width, reg2dp_d0_cube_in_width);
//     io.reg2dp_cube_out_channel := Mux(dp2reg_consumer, reg2dp_d1_cube_out_channel, reg2dp_d0_cube_out_channel);
//     io.reg2dp_cube_out_height := Mux(dp2reg_consumer, reg2dp_d1_cube_out_height, reg2dp_d0_cube_out_height);
//     io.reg2dp_cube_out_width := Mux(dp2reg_consumer, reg2dp_d1_cube_out_width, reg2dp_d0_cube_out_width);
//     io.reg2dp_input_data := Mux(dp2reg_consumer, reg2dp_d1_input_data, reg2dp_d0_input_data);
//     io.reg2dp_dst_base_addr_high := Mux(dp2reg_consumer, reg2dp_d1_dst_base_addr_high, reg2dp_d0_dst_base_addr_high);
//     io.reg2dp_dst_base_addr_low := Mux(dp2reg_consumer, reg2dp_d1_dst_base_addr_low, reg2dp_d0_dst_base_addr_low);
//     io.reg2dp_dst_line_stride := Mux(dp2reg_consumer, reg2dp_d1_dst_line_stride, reg2dp_d0_dst_line_stride);
//     io.reg2dp_dst_ram_type := Mux(dp2reg_consumer, reg2dp_d1_dst_ram_type, reg2dp_d0_dst_ram_type);
//     io.reg2dp_dst_surface_stride := Mux(dp2reg_consumer, reg2dp_d1_dst_surface_stride, reg2dp_d0_dst_surface_stride);
//     io.reg2dp_nan_to_zero := Mux(dp2reg_consumer, reg2dp_d1_nan_to_zero, reg2dp_d0_nan_to_zero);
//     io.reg2dp_flying_mode := Mux(dp2reg_consumer, reg2dp_d1_flying_mode, reg2dp_d0_flying_mode);
//     io.reg2dp_pooling_method := Mux(dp2reg_consumer, reg2dp_d1_pooling_method, reg2dp_d0_pooling_method);
//     io.reg2dp_split_num := Mux(dp2reg_consumer, reg2dp_d1_split_num, reg2dp_d0_split_num);
//     io.reg2dp_partial_width_in_first := Mux(dp2reg_consumer, reg2dp_d1_partial_width_in_first, reg2dp_d0_partial_width_in_first);
//     io.reg2dp_partial_width_in_last := Mux(dp2reg_consumer, reg2dp_d1_partial_width_in_last, reg2dp_d0_partial_width_in_last);
//     io.reg2dp_partial_width_in_mid := Mux(dp2reg_consumer, reg2dp_d1_partial_width_in_mid, reg2dp_d0_partial_width_in_mid);
//     io.reg2dp_partial_width_out_first := Mux(dp2reg_consumer, reg2dp_d1_partial_width_out_first, reg2dp_d0_partial_width_out_first);
//     io.reg2dp_partial_width_out_last := Mux(dp2reg_consumer, reg2dp_d1_partial_width_out_last, reg2dp_d0_partial_width_out_last);
//     io.reg2dp_partial_width_out_mid := Mux(dp2reg_consumer, reg2dp_d1_partial_width_out_mid, reg2dp_d0_partial_width_out_mid);
//     io.reg2dp_dma_en := Mux(dp2reg_consumer, reg2dp_d1_dma_en, reg2dp_d0_dma_en);
//     io.reg2dp_kernel_height := Mux(dp2reg_consumer, reg2dp_d1_kernel_height, reg2dp_d0_kernel_height);
//     io.reg2dp_kernel_stride_height := Mux(dp2reg_consumer, reg2dp_d1_kernel_stride_height, reg2dp_d0_kernel_stride_height);
//     io.reg2dp_kernel_stride_width := Mux(dp2reg_consumer, reg2dp_d1_kernel_stride_width, reg2dp_d0_kernel_stride_width);
//     io.reg2dp_kernel_width := Mux(dp2reg_consumer, reg2dp_d1_kernel_width, reg2dp_d0_kernel_width);
//     io.reg2dp_pad_bottom := Mux(dp2reg_consumer, reg2dp_d1_pad_bottom, reg2dp_d0_pad_bottom);
//     io.reg2dp_pad_left := Mux(dp2reg_consumer, reg2dp_d1_pad_left, reg2dp_d0_pad_left);
//     io.reg2dp_pad_right := Mux(dp2reg_consumer, reg2dp_d1_pad_right, reg2dp_d0_pad_right);
//     io.reg2dp_pad_top := Mux(dp2reg_consumer, reg2dp_d1_pad_top, reg2dp_d0_pad_top);
//     io.reg2dp_pad_value_1x := Mux(dp2reg_consumer, reg2dp_d1_pad_value_1x, reg2dp_d0_pad_value_1x);
//     io.reg2dp_pad_value_2x := Mux(dp2reg_consumer, reg2dp_d1_pad_value_2x, reg2dp_d0_pad_value_2x);
//     io.reg2dp_pad_value_3x := Mux(dp2reg_consumer, reg2dp_d1_pad_value_3x, reg2dp_d0_pad_value_3x);
//     io.reg2dp_pad_value_4x := Mux(dp2reg_consumer, reg2dp_d1_pad_value_4x, reg2dp_d0_pad_value_4x);
//     io.reg2dp_pad_value_5x := Mux(dp2reg_consumer, reg2dp_d1_pad_value_5x, reg2dp_d0_pad_value_5x);
//     io.reg2dp_pad_value_6x := Mux(dp2reg_consumer, reg2dp_d1_pad_value_6x, reg2dp_d0_pad_value_6x);
//     io.reg2dp_pad_value_7x := Mux(dp2reg_consumer, reg2dp_d1_pad_value_7x, reg2dp_d0_pad_value_7x);
//     io.reg2dp_recip_kernel_height := Mux(dp2reg_consumer, reg2dp_d1_recip_kernel_height, reg2dp_d0_recip_kernel_height);
//     io.reg2dp_recip_kernel_width := Mux(dp2reg_consumer, reg2dp_d1_recip_kernel_width, reg2dp_d0_recip_kernel_width);
//     io.reg2dp_src_base_addr_high := Mux(dp2reg_consumer, reg2dp_d1_src_base_addr_high, reg2dp_d0_src_base_addr_high);
//     io.reg2dp_src_base_addr_low := Mux(dp2reg_consumer, reg2dp_d1_src_base_addr_low, reg2dp_d0_src_base_addr_low);
//     io.reg2dp_src_line_stride := Mux(dp2reg_consumer, reg2dp_d1_src_line_stride, reg2dp_d0_src_line_stride);
//     io.reg2dp_src_surface_stride := Mux(dp2reg_consumer, reg2dp_d1_src_surface_stride, reg2dp_d0_src_surface_stride);

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // PASTE ADDIFITON LOGIC HERE FROM EXTRA FILE                         //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////

//     io.reg2dp_interrupt_ptr := dp2reg_consumer
//     //////// for general counting register ////////
//     val dp2reg_d0_set = reg2dp_d0_op_en & ~reg2dp_d0_op_en_w
//     val dp2reg_d0_clr = ~reg2dp_d0_op_en & reg2dp_d0_op_en_w
//     val dp2reg_d0_reg = reg2dp_d0_op_en ^ reg2dp_d0_op_en_w

//     val dp2reg_d1_set = reg2dp_d1_op_en & ~reg2dp_d1_op_en_w
//     val dp2reg_d1_clr = ~reg2dp_d1_op_en & reg2dp_d1_op_en_w
//     val dp2reg_d1_reg = reg2dp_d1_op_en ^ reg2dp_d1_op_en_w

//     //////// for NaN and infinity counting registers ////////
//     //////// group 0 ////////
//     val dp2reg_d0_nan_input_num_w = Mux(dp2reg_d0_set, io.dp2reg_nan_input_num,
//                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                     dp2reg_d0_nan_input_num))
//     val dp2reg_d0_inf_input_num_w = Mux(dp2reg_d0_set, io.dp2reg_inf_input_num,
//                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                     dp2reg_d0_inf_input_num))
//     val dp2reg_d0_nan_output_num_w = Mux(dp2reg_d0_set, io.dp2reg_nan_output_num,
//                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
//                                     dp2reg_d0_nan_output_num))
//     when(dp2reg_d0_reg){
//         dp2reg_d0_nan_input_num := dp2reg_d0_nan_input_num_w
//         dp2reg_d0_inf_input_num := dp2reg_d0_inf_input_num_w
//         dp2reg_d0_nan_output_num := dp2reg_d0_nan_output_num_w
//     }

//     //////// group 1 ////////
//     val dp2reg_d1_nan_input_num_w = Mux(dp2reg_d1_set, io.dp2reg_nan_input_num,
//                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                     dp2reg_d1_nan_input_num))
//     val dp2reg_d1_inf_input_num_w = Mux(dp2reg_d1_set, io.dp2reg_inf_input_num,
//                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                     dp2reg_d1_inf_input_num))
//     val dp2reg_d1_nan_output_num_w = Mux(dp2reg_d1_set, io.dp2reg_nan_output_num,
//                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
//                                     dp2reg_d1_nan_output_num))
//     when(dp2reg_d1_reg){
//         dp2reg_d1_nan_input_num := dp2reg_d1_nan_input_num_w
//         dp2reg_d1_inf_input_num := dp2reg_d1_inf_input_num_w
//         dp2reg_d1_nan_output_num := dp2reg_d1_nan_output_num_w
//     }
    
// }}


// object NV_NVDLA_PDP_regDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_reg())
// }