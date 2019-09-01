// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //Implementation overview of ping-pong register file.

// class NV_NVDLA_SDP_RDMA_reg extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())      

//         //csb2pdp
//         val csb2sdp_rdma_req_pd = Input(UInt(63.W))
//         val csb2sdp_rdma_req_pvld = Input(Bool())
//         val csb2sdp_rdma_req_prdy = Output(Bool())
        
//         val sdp_rdma2csb_resp_pd = Output(UInt(34.W))
//         val sdp_rdma2csb_resp_valid = Output(Bool())

//         //reg2dp
//         val dp2reg_brdma_stall = Input(UInt(32.W))
//         val dp2reg_erdma_stall = Input(UInt(32.W))
//         val dp2reg_mrdma_stall = Input(UInt(32.W))
//         val dp2reg_nrdma_stall = Input(UInt(32.W))
//         val dp2reg_done = Input(Bool())
//         val dp2reg_status_inf_input_num = Input(UInt(32.W))
//         val dp2reg_status_nan_input_num = Input(UInt(32.W))
//         val reg2dp_batch_number = Output(UInt(5.W))

//         val reg2dp_bn_base_addr_high = Output(UInt(32.W))
//         val reg2dp_bn_base_addr_low = Output(UInt(32.W))
//         val reg2dp_bn_batch_stride = Output(UInt(32.W))
//         val reg2dp_bn_line_stride = Output(UInt(32.W))
//         val reg2dp_bn_surface_stride = Output(UInt(32.W))

//         val reg2dp_brdma_data_mode = Output(Bool())
//         val reg2dp_brdma_data_size = Output(Bool())
//         val reg2dp_brdma_data_use = Output(UInt(2.W))
//         val reg2dp_brdma_disable = Output(Bool())
//         val reg2dp_brdma_ram_type = Output(Bool())
//         val reg2dp_bs_base_addr_high = Output(UInt(32.W))
//         val reg2dp_bs_base_addr_low = Output(UInt(32.W))
//         val reg2dp_bs_batch_stride = Output(UInt(32.W))
//         val reg2dp_bs_line_stride = Output(UInt(32.W))
//         val reg2dp_bs_surface_stride = Output(UInt(32.W))

//         val reg2dp_channel = Output(UInt(13.W))

//         val reg2dp_erdma_data_mode = Output(Bool())
//         val reg2dp_erdma_data_size = Output(Bool())
//         val reg2dp_erdma_data_use = Output(UInt(2.W))
//         val reg2dp_erdma_disable = Output(Bool())
//         val reg2dp_erdma_ram_type = Output(Bool())
//         val reg2dp_ew_base_addr_high = Output(UInt(32.W))
//         val reg2dp_ew_base_addr_low = Output(UInt(32.W))
//         val reg2dp_ew_batch_stride = Output(UInt(32.W))
//         val reg2dp_ew_line_stride = Output(UInt(32.W))
//         val reg2dp_ew_surface_stride = Output(UInt(32.W))

//         val reg2dp_flying_mode = Output(Bool())
//         val reg2dp_height = Output(UInt(13.W))
//         val reg2dp_in_precision = Output(UInt(2.W))

//         val reg2dp_nrdma_data_mode = Output(Bool())
//         val reg2dp_nrdma_data_size = Output(Bool())
//         val reg2dp_nrdma_data_use = Output(UInt(2.W))
//         val reg2dp_nrdma_disable = Output(Bool())
//         val reg2dp_nrdma_ram_type = Output(Bool())

//         val reg2dp_op_en = Output(Bool())
//         val reg2dp_out_precision = Output(UInt(2.W))
//         val reg2dp_perf_dma_en = Output(Bool())
//         val reg2dp_perf_nan_inf_count_en = Output(Bool())
//         val reg2dp_proc_precision = Output(UInt(2.W))
//         val reg2dp_src_base_addr_high = Output(UInt(32.W))
//         val reg2dp_src_base_addr_low = Output(UInt(32.W))
//         val reg2dp_src_line_stride = Output(UInt(32.W))
//         val reg2dp_src_ram_type = Output(Bool())
//         val reg2dp_src_surface_stride = Output(UInt(32.W))
//         val reg2dp_width = Output(UInt(13.W))
//         val reg2dp_winograd = Output(Bool())

//         //slave cg op
//         val slcg_op_en = Output(UInt(4.W))
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
// //           │         │                  |     SDP     |
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
//     val s_reg.wr_data = Wire(UInt(32.W))
//     val s_reg.wr_en = Wire(Bool())
//     val dp2reg_status_0 = Wire(UInt(2.W))
//     val dp2reg_status_1 = Wire(UInt(2.W))
//     val reg.offset = Wire(UInt(24.W))

//     val u_single_reg = Module(new NV_NVDLA_BASIC_REG_single)

//     u_single_reg.io.nvdla_core_clk  := io.nvdla_core_clk
//     val s_reg.rd_data               = u_single_reg.io.reg.rd_data
//     u_single_reg.io.reg.offset      := reg.offset(11,0)
//     u_single_reg.io.reg.wr_data     := s_reg.wr_data
//     u_single_reg.io.reg.wr_en       := s_reg.wr_en
//     val reg2dp_producer             = u_single_reg.io.producer
//     u_single_reg.io.consumer        := dp2reg_consumer
//     u_single_reg.io.status_0        := dp2reg_status_0
//     u_single_reg.io.status_1        := dp2reg_status_1


//     //Instance two duplicated register groups

//     val d0_reg.wr_data = Wire(UInt(32.W))
//     val d0_reg.wr_en = Wire(Bool())
//     val reg2dp_d0_op_en = RegInit(false.B)
//     val dp2reg_d0_brdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d0_erdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d0_mrdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d0_nrdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d0_status_inf_input_num = RegInit(0.U(32.W))
//     val dp2reg_d0_status_nan_input_num = RegInit(0.U(32.W))
    

//     val u_dual_reg_d0 = Module(new NV_NVDLA_SDP_RDMA_REG_dual)

//     u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
//     val d0_reg.rd_data              = u_dual_reg_d0.io.reg.rd_data
//     u_dual_reg_d0.io.reg.offset     := reg.offset(11,0)
//     u_dual_reg_d0.io.reg.wr_data    := d0_reg.wr_data
//     u_dual_reg_d0.io.reg.wr_en      := d0_reg.wr_en
//     val reg2dp_d0_bn_base_addr_high = u_dual_reg_d0.io.bn_base_addr_high
//     val reg2dp_d0_bn_base_addr_low  = u_dual_reg_d0.io.bn_base_addr_low
//     val reg2dp_d0_bn_batch_stride   = u_dual_reg_d0.io.bn_batch_stride
//     val reg2dp_d0_bn_line_stride    = u_dual_reg_d0.io.bn_line_stride
//     val reg2dp_d0_bn_surface_stride = u_dual_reg_d0.io.bn_surface_stride
//     val reg2dp_d0_brdma_data_mode   = u_dual_reg_d0.io.brdma_data_mode
//     val reg2dp_d0_brdma_data_size   = u_dual_reg_d0.io.brdma_data_size
//     val reg2dp_d0_brdma_data_use    = u_dual_reg_d0.io.brdma_data_use
//     val reg2dp_d0_brdma_disable     = u_dual_reg_d0.io.brdma_disable
//     val reg2dp_d0_brdma_ram_type    = u_dual_reg_d0.io.brdma_ram_type
//     val reg2dp_d0_bs_base_addr_high = u_dual_reg_d0.io.bs_base_addr_high
//     val reg2dp_d0_bs_base_addr_low  = u_dual_reg_d0.io.bs_base_addr_low
//     val reg2dp_d0_bs_batch_stride   = u_dual_reg_d0.io.bs_batch_stride
//     val reg2dp_d0_bs_line_stride    = u_dual_reg_d0.io.bs_line_stride
//     val reg2dp_d0_bs_surface_stride = u_dual_reg_d0.io.bs_surface_stride
//     val reg2dp_d0_channel           = u_dual_reg_d0.io.channel
//     val reg2dp_d0_height            = u_dual_reg_d0.io.height
//     val reg2dp_d0_width             = u_dual_reg_d0.io.width_a
//     val reg2dp_d0_erdma_data_mode   = u_dual_reg_d0.io.erdma_data_mode
//     val reg2dp_d0_erdma_data_size   = u_dual_reg_d0.io.erdma_data_size
//     val reg2dp_d0_erdma_data_use    = u_dual_reg_d0.io.erdma_data_use
//     val reg2dp_d0_erdma_disable     = u_dual_reg_d0.io.erdma_disable
//     val reg2dp_d0_erdma_ram_type    = u_dual_reg_d0.io.erdma_ram_type
//     val reg2dp_d0_ew_base_addr_high = u_dual_reg_d0.io.ew_base_addr_high
//     val reg2dp_d0_ew_base_addr_low  = u_dual_reg_d0.io.ew_base_addr_low
//     val reg2dp_d0_ew_batch_stride   = u_dual_reg_d0.io.ew_batch_stride
//     val reg2dp_d0_ew_line_stride    = u_dual_reg_d0.io.ew_line_stride
//     val reg2dp_d0_ew_surface_stride = u_dual_reg_d0.io.ew_surface_stride
//     val reg2dp_d0_batch_number      = u_dual_reg_d0.io.batch_number
//     val reg2dp_d0_flying_mode       = u_dual_reg_d0.io.flying_mode
//     val reg2dp_d0_in_precision      = u_dual_reg_d0.io.in_precision
//     val reg2dp_d0_out_precision     = u_dual_reg_d0.io.out_precision
//     val reg2dp_d0_proc_precision    = u_dual_reg_d0.io.proc_precision
//     val reg2dp_d0_winograd          = u_dual_reg_d0.io.winograd
//     val reg2dp_d0_nrdma_data_mode   = u_dual_reg_d0.io.nrdma_data_mode
//     val reg2dp_d0_nrdma_data_size   = u_dual_reg_d0.io.nrdma_data_size
//     val reg2dp_d0_nrdma_data_use    = u_dual_reg_d0.io.nrdma_data_use
//     val reg2dp_d0_nrdma_disable     = u_dual_reg_d0.io.nrdma_disable
//     val reg2dp_d0_nrdma_ram_type    = u_dual_reg_d0.io.nrdma_ram_type
//     val reg2dp_d0_op_en_trigger     = u_dual_reg_d0.io.op_en_trigger
//     val reg2dp_d0_perf_dma_en       = u_dual_reg_d0.io.perf_dma_en
//     val reg2dp_d0_perf_nan_inf_count_en = u_dual_reg_d0.io.perf_nan_inf_count_en
//     val reg2dp_d0_src_base_addr_high    = u_dual_reg_d0.io.src_base_addr_high
//     val reg2dp_d0_src_base_addr_low = u_dual_reg_d0.io.src_base_addr_low
//     val reg2dp_d0_src_ram_type      = u_dual_reg_d0.io.src_ram_type
//     val reg2dp_d0_src_line_stride   = u_dual_reg_d0.io.src_line_stride
//     val reg2dp_d0_src_surface_stride    = u_dual_reg_d0.io.src_surface_stride
//     u_dual_reg_d0.io.op_en          := reg2dp_d0_op_en
//     u_dual_reg_d0.io.brdma_stall    := dp2reg_d0_brdma_stall
//     u_dual_reg_d0.io.erdma_stall    := dp2reg_d0_erdma_stall
//     u_dual_reg_d0.io.mrdma_stall    := dp2reg_d0_mrdma_stall
//     u_dual_reg_d0.io.nrdma_stall    := dp2reg_d0_nrdma_stall
//     u_dual_reg_d0.io.status_inf_input_num := dp2reg_d0_status_inf_input_num
//     u_dual_reg_d0.io.status_nan_input_num := dp2reg_d0_status_nan_input_num


//     val d1_reg.wr_data = Wire(UInt(32.W))
//     val d1_reg.wr_en = Wire(Bool())
//     val reg2dp_d1_op_en = RegInit(false.B)
//     val dp2reg_d1_brdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d1_erdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d1_mrdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d1_nrdma_stall = RegInit(0.U(32.W))
//     val dp2reg_d1_status_inf_input_num = RegInit(0.U(32.W))
//     val dp2reg_d1_status_nan_input_num = RegInit(0.U(32.W))
    

//     val u_dual_reg_d1 = Module(new NV_NVDLA_SDP_RDMA_REG_dual)

//     u_dual_reg_d1.io.nvdla_core_clk     := io.nvdla_core_clk
//     val d1_reg.rd_data                  = u_dual_reg_d1.io.reg.rd_data
//     u_dual_reg_d1.io.reg.offset         := reg.offset(11,0)
//     u_dual_reg_d1.io.reg.wr_data        := d1_reg.wr_data
//     u_dual_reg_d1.io.reg.wr_en          := d1_reg.wr_en
//     val reg2dp_d1_bn_base_addr_high     = u_dual_reg_d1.io.bn_base_addr_high
//     val reg2dp_d1_bn_base_addr_low      = u_dual_reg_d1.io.bn_base_addr_low
//     val reg2dp_d1_bn_batch_stride       = u_dual_reg_d1.io.bn_batch_stride
//     val reg2dp_d1_bn_line_stride        = u_dual_reg_d1.io.bn_line_stride
//     val reg2dp_d1_bn_surface_stride     = u_dual_reg_d1.io.bn_surface_stride
//     val reg2dp_d1_brdma_data_mode       = u_dual_reg_d1.io.brdma_data_mode
//     val reg2dp_d1_brdma_data_size       = u_dual_reg_d1.io.brdma_data_size
//     val reg2dp_d1_brdma_data_use        = u_dual_reg_d1.io.brdma_data_use
//     val reg2dp_d1_brdma_disable         = u_dual_reg_d1.io.brdma_disable
//     val reg2dp_d1_brdma_ram_type        = u_dual_reg_d1.io.brdma_ram_type
//     val reg2dp_d1_bs_base_addr_high     = u_dual_reg_d1.io.bs_base_addr_high
//     val reg2dp_d1_bs_base_addr_low      = u_dual_reg_d1.io.bs_base_addr_low
//     val reg2dp_d1_bs_batch_stride       = u_dual_reg_d1.io.bs_batch_stride
//     val reg2dp_d1_bs_line_stride        = u_dual_reg_d1.io.bs_line_stride
//     val reg2dp_d1_bs_surface_stride     = u_dual_reg_d1.io.bs_surface_stride
//     val reg2dp_d1_channel               = u_dual_reg_d1.io.channel
//     val reg2dp_d1_height                = u_dual_reg_d1.io.height
//     val reg2dp_d1_width                 = u_dual_reg_d1.io.width_a
//     val reg2dp_d1_erdma_data_mode       = u_dual_reg_d1.io.erdma_data_mode
//     val reg2dp_d1_erdma_data_size       = u_dual_reg_d1.io.erdma_data_size
//     val reg2dp_d1_erdma_data_use        = u_dual_reg_d1.io.erdma_data_use
//     val reg2dp_d1_erdma_disable         = u_dual_reg_d1.io.erdma_disable
//     val reg2dp_d1_erdma_ram_type        = u_dual_reg_d1.io.erdma_ram_type
//     val reg2dp_d1_ew_base_addr_high     = u_dual_reg_d1.io.ew_base_addr_high
//     val reg2dp_d1_ew_base_addr_low      = u_dual_reg_d1.io.ew_base_addr_low
//     val reg2dp_d1_ew_batch_stride       = u_dual_reg_d1.io.ew_batch_stride
//     val reg2dp_d1_ew_line_stride        = u_dual_reg_d1.io.ew_line_stride
//     val reg2dp_d1_ew_surface_stride     = u_dual_reg_d1.io.ew_surface_stride
//     val reg2dp_d1_batch_number          = u_dual_reg_d1.io.batch_number
//     val reg2dp_d1_flying_mode           = u_dual_reg_d1.io.flying_mode
//     val reg2dp_d1_in_precision          = u_dual_reg_d1.io.in_precision
//     val reg2dp_d1_out_precision         = u_dual_reg_d1.io.out_precision
//     val reg2dp_d1_proc_precision        = u_dual_reg_d1.io.proc_precision
//     val reg2dp_d1_winograd              = u_dual_reg_d1.io.winograd
//     val reg2dp_d1_nrdma_data_mode       = u_dual_reg_d1.io.nrdma_data_mode
//     val reg2dp_d1_nrdma_data_size       = u_dual_reg_d1.io.nrdma_data_size
//     val reg2dp_d1_nrdma_data_use        = u_dual_reg_d1.io.nrdma_data_use
//     val reg2dp_d1_nrdma_disable         = u_dual_reg_d1.io.nrdma_disable
//     val reg2dp_d1_nrdma_ram_type        = u_dual_reg_d1.io.nrdma_ram_type
//     val reg2dp_d1_op_en_trigger         = u_dual_reg_d1.io.op_en_trigger
//     val reg2dp_d1_perf_dma_en           = u_dual_reg_d1.io.perf_dma_en
//     val reg2dp_d1_perf_nan_inf_count_en = u_dual_reg_d1.io.perf_nan_inf_count_en
//     val reg2dp_d1_src_base_addr_high    = u_dual_reg_d1.io.src_base_addr_high
//     val reg2dp_d1_src_base_addr_low     = u_dual_reg_d1.io.src_base_addr_low
//     val reg2dp_d1_src_ram_type          = u_dual_reg_d1.io.src_ram_type
//     val reg2dp_d1_src_line_stride       = u_dual_reg_d1.io.src_line_stride
//     val reg2dp_d1_src_surface_stride    = u_dual_reg_d1.io.src_surface_stride
//     u_dual_reg_d1.io.op_en              := reg2dp_d1_op_en
//     u_dual_reg_d1.io.brdma_stall        := dp2reg_d1_brdma_stall
//     u_dual_reg_d1.io.erdma_stall        := dp2reg_d1_erdma_stall
//     u_dual_reg_d1.io.mrdma_stall        := dp2reg_d1_mrdma_stall
//     u_dual_reg_d1.io.nrdma_stall        := dp2reg_d1_nrdma_stall
//     u_dual_reg_d1.io.status_inf_input_num := dp2reg_d1_status_inf_input_num
//     u_dual_reg_d1.io.status_nan_input_num := dp2reg_d1_status_nan_input_num


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
//     dp2reg_status_0 := Mux(reg2dp_d0_op_en === false.B,  0.U, 
//                        Mux(dp2reg_consumer === true.B, 2.U, 1.U))

//     dp2reg_status_1 := Mux(reg2dp_d1_op_en === false.B, 0.U, 
//                        Mux(dp2reg_consumer === false.B, 2.U, 1.U))

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE OP_EN LOGIC                                               //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     val reg2dp_op_en_reg = RegInit(0.U)
//     val reg.wr_data = Wire(UInt(32.W))
//     val reg2dp_d0_op_en_w = Mux(~reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg.wr_data(0), 
//                             Mux(io.dp2reg_done && dp2reg_consumer === false.B, false.B, reg2dp_d0_op_en))
//     reg2dp_d0_op_en := reg2dp_d0_op_en_w
//     val reg2dp_d1_op_en_w = Mux(~reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg.wr_data(0), 
//                             Mux(io.dp2reg_done && dp2reg_consumer === true.B, false.B, reg2dp_d1_op_en))
//     reg2dp_d1_op_en := reg2dp_d1_op_en_w
//     val reg2dp_op_en_ori = Mux(dp2reg_consumer, reg2dp_d1_op_en, reg2dp_d0_op_en)
//     val reg2dp_op_en_reg_w = Mux(io.dp2reg_done,  0.U, Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori))
//     reg2dp_op_en_reg := reg2dp_op_en_reg_w 
//     io.reg2dp_op_en := reg2dp_op_en_reg(2)
//     io.slcg_op_en := ShiftRegister(Fill(4, reg2dp_op_en_ori), 3)
//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE ACCESS LOGIC TO EACH REGISTER GROUP                       //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     //EACH subunit has 4KB address space 
//     val reg.wr_en = Wire(Bool())
//     val select_s = Mux(reg.offset(11,0) < "h0008".asUInt(32.W), true.B, false.B)
//     val select_d0 = (reg.offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === false.B)
//     val select_d1 = (reg.offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === true.B)

//     s_reg.wr_en := reg.wr_en & select_s
//     d0_reg.wr_en := reg.wr_en & select_d0 & !reg2dp_d0_op_en
//     d1_reg.wr_en := reg.wr_en & select_d1 & !reg2dp_d1_op_en

//     s_reg.wr_data  := reg.wr_data;
//     d0_reg.wr_data := reg.wr_data;
//     d1_reg.wr_data := reg.wr_data;

//     val reg.rd_data =  (Fill(32, select_s) & s_reg.rd_data) |
//                         (Fill(32, select_d0) & d0_reg.rd_data) |
//                         (Fill(32, select_d1) & d1_reg.rd_data)

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     val req_pvld = RegInit(false.B)
//     val req_pd = RegInit(0.U(63.W))

//     req_pvld := io.csb2sdp_rdma_req_pvld
//     when(io.csb2sdp_rdma_req_pvld){
//         req_pd := io.csb2sdp_rdma_req_pd
//     }

//     // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  req_pd ) 
//     val req_addr    = req_pd(21, 0)
//     val req_wdat    = req_pd(53, 22)
//     val req_write   = req_pd(54)
//     val req_nposted = req_pd(55)
//     val req_srcpriv = req_pd(56)
//     val req_wrbe    = req_pd(60, 57)
//     val req_level   = req_pd(62, 61)

//     io.csb2sdp_rdma_req_prdy := true.B

//     //Address in CSB master is word aligned while address in regfile is byte aligned.
//     reg.offset  := Cat(req_addr, 0.U(2.W))
//     reg.wr_data := req_wdat
//     reg.wr_en   := req_pvld & req_write
//     val reg_rd_en = req_pvld & ~req_write

//     // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_rd_erpt ,  csb_rresp_ ,  csb_rresp_pd_w )
//     val csb_rresp_rdat = reg.rd_data
//     val csb_rresp_error = false.B
//     val csb_rresp_pd_w = Cat(false.B, csb_rresp_error, csb_rresp_rdat)

//     // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_wr_erpt ,  csb_wresp_ ,  csb_wresp_pd_w 
//     val csb_wresp_rdat = 0.U(32.W)
//     val csb_wresp_error = false.B
//     val csb_wresp_pd_w = Cat(true.B, csb_wresp_error, csb_wresp_rdat)

//     val sdp_rdma2csb_resp_pd_out = RegInit(0.U)
//     val sdp_rdma2csb_resp_valid_out = RegInit(false.B)

//     when(reg_rd_en){
//         sdp_rdma2csb_resp_pd_out := csb_rresp_pd_w
//     }
//     .elsewhen(reg.wr_en & req_nposted){
//         sdp_rdma2csb_resp_pd_out := csb_wresp_pd_w
//     }
//     sdp_rdma2csb_resp_valid_out := (reg.wr_en & req_nposted) | reg_rd_en

//     io.sdp_rdma2csb_resp_pd := sdp_rdma2csb_resp_pd_out
//     io.sdp_rdma2csb_resp_valid := sdp_rdma2csb_resp_valid_out

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////

//     io.reg2dp_bn_base_addr_high := Mux(dp2reg_consumer, reg2dp_d1_bn_base_addr_high, reg2dp_d0_bn_base_addr_high)
//     io.reg2dp_bn_base_addr_low := Mux(dp2reg_consumer, reg2dp_d1_bn_base_addr_low, reg2dp_d0_bn_base_addr_low)
//     io.reg2dp_bn_batch_stride := Mux(dp2reg_consumer, reg2dp_d1_bn_batch_stride, reg2dp_d0_bn_batch_stride)
//     io.reg2dp_bn_line_stride := Mux(dp2reg_consumer, reg2dp_d1_bn_line_stride, reg2dp_d0_bn_line_stride)
//     io.reg2dp_bn_surface_stride := Mux(dp2reg_consumer, reg2dp_d1_bn_surface_stride, reg2dp_d0_bn_surface_stride)
//     io.reg2dp_brdma_data_mode := Mux(dp2reg_consumer, reg2dp_d1_brdma_data_mode, reg2dp_d0_brdma_data_mode)
//     io.reg2dp_brdma_data_size := Mux(dp2reg_consumer, reg2dp_d1_brdma_data_size, reg2dp_d0_brdma_data_size)
//     io.reg2dp_brdma_data_use := Mux(dp2reg_consumer, reg2dp_d1_brdma_data_use, reg2dp_d0_brdma_data_use)
//     io.reg2dp_brdma_disable := Mux(dp2reg_consumer, reg2dp_d1_brdma_disable, reg2dp_d0_brdma_disable)
//     io.reg2dp_brdma_ram_type := Mux(dp2reg_consumer, reg2dp_d1_brdma_ram_type, reg2dp_d0_brdma_ram_type)
//     io.reg2dp_bs_base_addr_high := Mux(dp2reg_consumer, reg2dp_d1_bs_base_addr_high, reg2dp_d0_bs_base_addr_high)
//     io.reg2dp_bs_base_addr_low := Mux(dp2reg_consumer, reg2dp_d1_bs_base_addr_low, reg2dp_d0_bs_base_addr_low)
//     io.reg2dp_bs_batch_stride := Mux(dp2reg_consumer, reg2dp_d1_bs_batch_stride, reg2dp_d0_bs_batch_stride)
//     io.reg2dp_bs_line_stride := Mux(dp2reg_consumer, reg2dp_d1_bs_line_stride, reg2dp_d0_bs_line_stride)
//     io.reg2dp_bs_surface_stride := Mux(dp2reg_consumer, reg2dp_d1_bs_surface_stride, reg2dp_d0_bs_surface_stride)
//     io.reg2dp_channel := Mux(dp2reg_consumer, reg2dp_d1_channel, reg2dp_d0_channel)
//     io.reg2dp_height := Mux(dp2reg_consumer, reg2dp_d1_height, reg2dp_d0_height)
//     io.reg2dp_width := Mux(dp2reg_consumer, reg2dp_d1_width, reg2dp_d0_width)
//     io.reg2dp_erdma_data_mode := Mux(dp2reg_consumer, reg2dp_d1_erdma_data_mode, reg2dp_d0_erdma_data_mode)
//     io.reg2dp_erdma_data_size := Mux(dp2reg_consumer, reg2dp_d1_erdma_data_size, reg2dp_d0_erdma_data_size)
//     io.reg2dp_erdma_data_use := Mux(dp2reg_consumer, reg2dp_d1_erdma_data_use, reg2dp_d0_erdma_data_use)
//     io.reg2dp_erdma_disable := Mux(dp2reg_consumer, reg2dp_d1_erdma_disable, reg2dp_d0_erdma_disable)
//     io.reg2dp_erdma_ram_type := Mux(dp2reg_consumer, reg2dp_d1_erdma_ram_type, reg2dp_d0_erdma_ram_type)
//     io.reg2dp_ew_base_addr_high := Mux(dp2reg_consumer, reg2dp_d1_ew_base_addr_high, reg2dp_d0_ew_base_addr_high)
//     io.reg2dp_ew_base_addr_low := Mux(dp2reg_consumer, reg2dp_d1_ew_base_addr_low, reg2dp_d0_ew_base_addr_low)
//     io.reg2dp_ew_batch_stride := Mux(dp2reg_consumer, reg2dp_d1_ew_batch_stride, reg2dp_d0_ew_batch_stride)
//     io.reg2dp_ew_line_stride := Mux(dp2reg_consumer, reg2dp_d1_ew_line_stride, reg2dp_d0_ew_line_stride)
//     io.reg2dp_ew_surface_stride := Mux(dp2reg_consumer, reg2dp_d1_ew_surface_stride, reg2dp_d0_ew_surface_stride)
//     io.reg2dp_batch_number := Mux(dp2reg_consumer, reg2dp_d1_batch_number, reg2dp_d0_batch_number)
//     io.reg2dp_flying_mode := Mux(dp2reg_consumer, reg2dp_d1_flying_mode, reg2dp_d0_flying_mode)
//     io.reg2dp_in_precision := Mux(dp2reg_consumer, reg2dp_d1_in_precision, reg2dp_d0_in_precision)
//     io.reg2dp_out_precision := Mux(dp2reg_consumer, reg2dp_d1_out_precision, reg2dp_d0_out_precision)
//     io.reg2dp_proc_precision := Mux(dp2reg_consumer, reg2dp_d1_proc_precision, reg2dp_d0_proc_precision)
//     io.reg2dp_winograd := Mux(dp2reg_consumer, reg2dp_d1_winograd, reg2dp_d0_winograd)
//     io.reg2dp_nrdma_data_mode := Mux(dp2reg_consumer, reg2dp_d1_nrdma_data_mode, reg2dp_d0_nrdma_data_mode)
//     io.reg2dp_nrdma_data_size := Mux(dp2reg_consumer, reg2dp_d1_nrdma_data_size, reg2dp_d0_nrdma_data_size)
//     io.reg2dp_nrdma_data_use := Mux(dp2reg_consumer, reg2dp_d1_nrdma_data_use, reg2dp_d0_nrdma_data_use)
//     io.reg2dp_nrdma_disable := Mux(dp2reg_consumer, reg2dp_d1_nrdma_disable, reg2dp_d0_nrdma_disable)
//     io.reg2dp_nrdma_ram_type := Mux(dp2reg_consumer, reg2dp_d1_nrdma_ram_type, reg2dp_d0_nrdma_ram_type)
//     io.reg2dp_perf_dma_en := Mux(dp2reg_consumer, reg2dp_d1_perf_dma_en, reg2dp_d0_perf_dma_en)
//     io.reg2dp_perf_nan_inf_count_en := Mux(dp2reg_consumer, reg2dp_d1_perf_nan_inf_count_en, reg2dp_d0_perf_nan_inf_count_en)
//     io.reg2dp_src_base_addr_high := Mux(dp2reg_consumer, reg2dp_d1_src_base_addr_high, reg2dp_d0_src_base_addr_high)
//     io.reg2dp_src_base_addr_low := Mux(dp2reg_consumer, reg2dp_d1_src_base_addr_low, reg2dp_d0_src_base_addr_low)
//     io.reg2dp_src_ram_type := Mux(dp2reg_consumer, reg2dp_d1_src_ram_type, reg2dp_d1_src_ram_type)
//     io.reg2dp_src_line_stride := Mux(dp2reg_consumer, reg2dp_d1_src_line_stride, reg2dp_d0_src_line_stride)
//     io.reg2dp_src_surface_stride := Mux(dp2reg_consumer, reg2dp_d1_src_surface_stride, reg2dp_d0_src_surface_stride)

//     ////////////////////////////////////////////////////////////////////////
//     //                                                                    //
//     // PASTE ADDIFITON LOGIC HERE FROM EXTRA FILE                         //
//     //                                                                    //
//     ////////////////////////////////////////////////////////////////////////
//     // USER logic can be put here:
//     //////// Dual Flop Write Control////////

//     val dp2reg_d0_set = reg2dp_d0_op_en & ~reg2dp_d0_op_en_w
//     val dp2reg_d0_clr = ~reg2dp_d0_op_en & reg2dp_d0_op_en_w
//     val dp2reg_d0_reg = reg2dp_d0_op_en ^ reg2dp_d0_op_en_w

//     val dp2reg_d1_set = reg2dp_d1_op_en & ~reg2dp_d1_op_en_w;
//     val dp2reg_d1_clr = ~reg2dp_d1_op_en & reg2dp_d1_op_en_w;
//     val dp2reg_d1_reg = reg2dp_d1_op_en ^ reg2dp_d1_op_en_w;

//     //////// for overflow counting register ////////
//     val dp2reg_d0_status_nan_input_num_w = Mux(dp2reg_d0_set, io.dp2reg_status_nan_input_num, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_status_nan_input_num))
//     val dp2reg_d0_brdma_stall_w = Mux(dp2reg_d0_set, io.dp2reg_brdma_stall, 
//                                   Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_brdma_stall))
//     val dp2reg_d0_status_inf_input_num_w = Mux(dp2reg_d0_set, io.dp2reg_status_inf_input_num, 
//                                            Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_status_inf_input_num))
//     val dp2reg_d0_erdma_stall_w = Mux(dp2reg_d0_set, io.dp2reg_erdma_stall, 
//                                   Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_erdma_stall))
//     val dp2reg_d0_nrdma_stall_w = Mux(dp2reg_d0_set, io.dp2reg_nrdma_stall, 
//                                   Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_nrdma_stall))
//     val dp2reg_d0_mrdma_stall_w = Mux(dp2reg_d0_set, io.dp2reg_mrdma_stall, 
//                                   Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_mrdma_stall))

//     when(dp2reg_d0_reg){
//         dp2reg_d0_status_nan_input_num := dp2reg_d0_status_nan_input_num_w
//         dp2reg_d0_brdma_stall := dp2reg_d0_brdma_stall_w
//         dp2reg_d0_status_inf_input_num := dp2reg_d0_status_inf_input_num_w
//         dp2reg_d0_erdma_stall := dp2reg_d0_erdma_stall_w
//         dp2reg_d0_nrdma_stall := dp2reg_d0_nrdma_stall_w
//         dp2reg_d0_mrdma_stall := dp2reg_d0_mrdma_stall_w
//     }

//     val dp2reg_d1_status_nan_input_num_w = Mux(dp2reg_d1_set, io.dp2reg_status_nan_input_num, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_status_nan_input_num))
//     val dp2reg_d1_brdma_stall_w = Mux(dp2reg_d1_set, io.dp2reg_brdma_stall, 
//                                   Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_brdma_stall))
//     val dp2reg_d1_status_inf_input_num_w = Mux(dp2reg_d1_set, io.dp2reg_status_inf_input_num, 
//                                            Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_status_inf_input_num))
//     val dp2reg_d1_erdma_stall_w = Mux(dp2reg_d1_set, io.dp2reg_erdma_stall, 
//                                   Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_erdma_stall))
//     val dp2reg_d1_nrdma_stall_w = Mux(dp2reg_d1_set, io.dp2reg_nrdma_stall, 
//                                  Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_nrdma_stall))
//     val dp2reg_d1_mrdma_stall_w = Mux(dp2reg_d1_set, io.dp2reg_mrdma_stall, 
//                                   Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_mrdma_stall))

//     when(dp2reg_d1_reg){
//         dp2reg_d1_status_nan_input_num := dp2reg_d1_status_nan_input_num_w
//         dp2reg_d1_brdma_stall := dp2reg_d1_brdma_stall_w
//         dp2reg_d1_status_inf_input_num := dp2reg_d1_status_inf_input_num_w
//         dp2reg_d1_erdma_stall := dp2reg_d1_erdma_stall_w
//         dp2reg_d1_nrdma_stall := dp2reg_d1_nrdma_stall_w
//         dp2reg_d1_mrdma_stall := dp2reg_d1_mrdma_stall_w
//     }

// }}


// object NV_NVDLA_SDP_RDMA_regDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_reg())
// }