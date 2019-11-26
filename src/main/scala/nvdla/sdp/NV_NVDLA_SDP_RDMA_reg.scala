package nvdla

import chisel3._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_SDP_RDMA_reg extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        //csb2pdp
        val csb2sdp_rdma = new csb2dp_if

        //reg2dp
        val reg2dp_op_en = Output(Bool())
        val reg2dp_field = new sdp_rdma_reg_dual_flop_outputs

        //dp2reg
        val dp2reg_brdma_stall = Input(UInt(32.W))
        val dp2reg_erdma_stall = Input(UInt(32.W))
        val dp2reg_mrdma_stall = Input(UInt(32.W))
        val dp2reg_nrdma_stall = Input(UInt(32.W))
        val dp2reg_done = Input(Bool())
        val dp2reg_status_inf_input_num = Input(UInt(32.W))
        val dp2reg_status_nan_input_num = Input(UInt(32.W))

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
//           │         │                  |     SDP     |
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
    val s_reg_wr_data = Wire(UInt(32.W))
    val s_reg_wr_en = Wire(Bool())
    val dp2reg_status_0 = Wire(UInt(2.W))
    val dp2reg_status_1 = Wire(UInt(2.W))
    val reg_offset = Wire(UInt(24.W))

    val u_single_reg = Module(new NV_NVDLA_BASIC_REG_single)

    u_single_reg.io.nvdla_core_clk  := io.nvdla_core_clk
    val s_reg_rd_data = u_single_reg.io.reg.rd_data
    u_single_reg.io.reg.offset := reg_offset(11,0)
    u_single_reg.io.reg.wr_data := s_reg_wr_data
    u_single_reg.io.reg.wr_en := s_reg_wr_en
    val reg2dp_producer = u_single_reg.io.producer
    u_single_reg.io.consumer := dp2reg_consumer
    u_single_reg.io.status_0 := dp2reg_status_0
    u_single_reg.io.status_1 := dp2reg_status_1

    //Instance two duplicated register groups

    val d0_reg_wr_data = Wire(UInt(32.W))
    val d0_reg_wr_en = Wire(Bool())
    val reg2dp_d0_op_en = RegInit(false.B)
    val dp2reg_d0_brdma_stall = RegInit(0.U(32.W))
    val dp2reg_d0_erdma_stall = RegInit(0.U(32.W))
    val dp2reg_d0_mrdma_stall = RegInit(0.U(32.W))
    val dp2reg_d0_nrdma_stall = RegInit(0.U(32.W))
    val dp2reg_d0_status_inf_input_num = RegInit(0.U(32.W))
    val dp2reg_d0_status_nan_input_num = RegInit(0.U(32.W))
    
    val u_dual_reg_d0 = Module(new NV_NVDLA_SDP_RDMA_REG_dual)
    u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
    val d0_reg_rd_data = u_dual_reg_d0.io.reg.rd_data
    u_dual_reg_d0.io.reg.offset := reg_offset(11,0)
    u_dual_reg_d0.io.reg.wr_data := d0_reg_wr_data
    u_dual_reg_d0.io.reg.wr_en := d0_reg_wr_en
    val reg2dp_d0_field = u_dual_reg_d0.io.field
    val reg2dp_d0_op_en_trigger = u_dual_reg_d0.io.op_en_trigger
    u_dual_reg_d0.io.op_en := reg2dp_d0_op_en
    u_dual_reg_d0.io.brdma_stall := dp2reg_d0_brdma_stall
    u_dual_reg_d0.io.erdma_stall := dp2reg_d0_erdma_stall
    u_dual_reg_d0.io.mrdma_stall := dp2reg_d0_mrdma_stall
    u_dual_reg_d0.io.nrdma_stall := dp2reg_d0_nrdma_stall
    u_dual_reg_d0.io.status_inf_input_num := dp2reg_d0_status_inf_input_num
    u_dual_reg_d0.io.status_nan_input_num := dp2reg_d0_status_nan_input_num

    val d1_reg_wr_data = Wire(UInt(32.W))
    val d1_reg_wr_en = Wire(Bool())
    val reg2dp_d1_op_en = RegInit(false.B)
    val dp2reg_d1_brdma_stall = RegInit(0.U(32.W))
    val dp2reg_d1_erdma_stall = RegInit(0.U(32.W))
    val dp2reg_d1_mrdma_stall = RegInit(0.U(32.W))
    val dp2reg_d1_nrdma_stall = RegInit(0.U(32.W))
    val dp2reg_d1_status_inf_input_num = RegInit(0.U(32.W))
    val dp2reg_d1_status_nan_input_num = RegInit(0.U(32.W))
    
    val u_dual_reg_d1 = Module(new NV_NVDLA_SDP_RDMA_REG_dual)
    u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk
    val d1_reg_rd_data = u_dual_reg_d1.io.reg.rd_data
    u_dual_reg_d1.io.reg.offset := reg_offset(11,0)
    u_dual_reg_d1.io.reg.wr_data := d1_reg_wr_data
    u_dual_reg_d1.io.reg.wr_en := d1_reg_wr_en
    val reg2dp_d1_field = u_dual_reg_d1.io.field
    val reg2dp_d1_op_en_trigger = u_dual_reg_d1.io.op_en_trigger
    u_dual_reg_d1.io.op_en := reg2dp_d1_op_en
    u_dual_reg_d1.io.brdma_stall := dp2reg_d1_brdma_stall
    u_dual_reg_d1.io.erdma_stall := dp2reg_d1_erdma_stall
    u_dual_reg_d1.io.mrdma_stall := dp2reg_d1_mrdma_stall
    u_dual_reg_d1.io.nrdma_stall := dp2reg_d1_nrdma_stall
    u_dual_reg_d1.io.status_inf_input_num := dp2reg_d1_status_inf_input_num
    u_dual_reg_d1.io.status_nan_input_num := dp2reg_d1_status_nan_input_num

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
    dp2reg_status_0 := Mux(reg2dp_d0_op_en === false.B,  0.U, 
                       Mux(dp2reg_consumer === true.B, 2.U, 1.U))

    dp2reg_status_1 := Mux(reg2dp_d1_op_en === false.B, 0.U, 
                       Mux(dp2reg_consumer === false.B, 2.U, 1.U))

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OP_EN LOGIC                                               //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    val reg2dp_op_en_reg = RegInit(0.U)
    val reg_wr_data = Wire(UInt(32.W))
    val reg2dp_d0_op_en_w = Mux(~reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg_wr_data(0), 
                            Mux(io.dp2reg_done && (dp2reg_consumer === false.B), false.B, reg2dp_d0_op_en))
    reg2dp_d0_op_en := reg2dp_d0_op_en_w
    val reg2dp_d1_op_en_w = Mux(~reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg_wr_data(0), 
                            Mux(io.dp2reg_done && (dp2reg_consumer === true.B), false.B, reg2dp_d1_op_en))
    reg2dp_d1_op_en := reg2dp_d1_op_en_w
    val reg2dp_op_en_ori = Mux(dp2reg_consumer, reg2dp_d1_op_en, reg2dp_d0_op_en)
    val reg2dp_op_en_reg_w = Mux(io.dp2reg_done,  0.U, Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori))
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

    s_reg_wr_data  := reg_wr_data;
    d0_reg_wr_data := reg_wr_data;
    d1_reg_wr_data := reg_wr_data;

    val reg_rd_data =  (Fill(32, select_s) & s_reg_rd_data) |
                        (Fill(32, select_d0) & d0_reg_rd_data) |
                        (Fill(32, select_d1) & d1_reg_rd_data)

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    val csb_logic = Module(new NV_NVDLA_CSB_LOGIC)
    csb_logic.io.clk := io.nvdla_core_clk
    csb_logic.io.csb2dp <> io.csb2sdp_rdma
    reg_offset := csb_logic.io.reg.offset
    reg_wr_en := csb_logic.io.reg.wr_en
    reg_wr_data := csb_logic.io.reg.wr_data
    csb_logic.io.reg.rd_data := reg_rd_data

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    io.reg2dp_field := Mux(dp2reg_consumer, reg2dp_d1_field, reg2dp_d0_field)

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // PASTE ADDIFITON LOGIC HERE FROM EXTRA FILE                         //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    // USER logic can be put here:
    //////// Dual Flop Write Control////////

    val dp2reg_d0_set = reg2dp_d0_op_en & ~reg2dp_d0_op_en_w
    val dp2reg_d0_clr = ~reg2dp_d0_op_en & reg2dp_d0_op_en_w
    val dp2reg_d0_reg = reg2dp_d0_op_en ^ reg2dp_d0_op_en_w

    val dp2reg_d1_set = reg2dp_d1_op_en & ~reg2dp_d1_op_en_w;
    val dp2reg_d1_clr = ~reg2dp_d1_op_en & reg2dp_d1_op_en_w;
    val dp2reg_d1_reg = reg2dp_d1_op_en ^ reg2dp_d1_op_en_w;

    //////// for overflow counting register ////////
    val dp2reg_d0_status_nan_input_num_w = Mux(dp2reg_d0_set, io.dp2reg_status_nan_input_num, 
                                           Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_status_nan_input_num))
    val dp2reg_d0_brdma_stall_w = Mux(dp2reg_d0_set, io.dp2reg_brdma_stall, 
                                  Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_brdma_stall))
    val dp2reg_d0_status_inf_input_num_w = Mux(dp2reg_d0_set, io.dp2reg_status_inf_input_num, 
                                           Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_status_inf_input_num))
    val dp2reg_d0_erdma_stall_w = Mux(dp2reg_d0_set, io.dp2reg_erdma_stall, 
                                  Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_erdma_stall))
    val dp2reg_d0_nrdma_stall_w = Mux(dp2reg_d0_set, io.dp2reg_nrdma_stall, 
                                  Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_nrdma_stall))
    val dp2reg_d0_mrdma_stall_w = Mux(dp2reg_d0_set, io.dp2reg_mrdma_stall, 
                                  Mux(dp2reg_d0_clr, 0.U, dp2reg_d0_mrdma_stall))

    when(dp2reg_d0_reg){
        dp2reg_d0_status_nan_input_num := dp2reg_d0_status_nan_input_num_w
        dp2reg_d0_brdma_stall := dp2reg_d0_brdma_stall_w
        dp2reg_d0_status_inf_input_num := dp2reg_d0_status_inf_input_num_w
        dp2reg_d0_erdma_stall := dp2reg_d0_erdma_stall_w
        dp2reg_d0_nrdma_stall := dp2reg_d0_nrdma_stall_w
        dp2reg_d0_mrdma_stall := dp2reg_d0_mrdma_stall_w
    }

    val dp2reg_d1_status_nan_input_num_w = Mux(dp2reg_d1_set, io.dp2reg_status_nan_input_num, 
                                           Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_status_nan_input_num))
    val dp2reg_d1_brdma_stall_w = Mux(dp2reg_d1_set, io.dp2reg_brdma_stall, 
                                  Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_brdma_stall))
    val dp2reg_d1_status_inf_input_num_w = Mux(dp2reg_d1_set, io.dp2reg_status_inf_input_num, 
                                           Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_status_inf_input_num))
    val dp2reg_d1_erdma_stall_w = Mux(dp2reg_d1_set, io.dp2reg_erdma_stall, 
                                  Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_erdma_stall))
    val dp2reg_d1_nrdma_stall_w = Mux(dp2reg_d1_set, io.dp2reg_nrdma_stall, 
                                 Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_nrdma_stall))
    val dp2reg_d1_mrdma_stall_w = Mux(dp2reg_d1_set, io.dp2reg_mrdma_stall, 
                                  Mux(dp2reg_d1_clr, 0.U, dp2reg_d1_mrdma_stall))

    when(dp2reg_d1_reg){
        dp2reg_d1_status_nan_input_num := dp2reg_d1_status_nan_input_num_w
        dp2reg_d1_brdma_stall := dp2reg_d1_brdma_stall_w
        dp2reg_d1_status_inf_input_num := dp2reg_d1_status_inf_input_num_w
        dp2reg_d1_erdma_stall := dp2reg_d1_erdma_stall_w
        dp2reg_d1_nrdma_stall := dp2reg_d1_nrdma_stall_w
        dp2reg_d1_mrdma_stall := dp2reg_d1_mrdma_stall_w
    }

}}


object NV_NVDLA_SDP_RDMA_regDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_reg())
}