package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

//Implementation overview of ping-pong register file.

class NV_NVDLA_CDMA_regfile extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        //csb2cdma
        val csb2cdma = new csb2dp_if

        //reg2dp
        val dp2reg_done = Input(Bool())
        val dp2reg_dc_rd_latency = Input(UInt(32.W))
        val dp2reg_dc_rd_stall = Input(UInt(32.W))
        val dp2reg_img_rd_latency = Input(UInt(32.W))
        val dp2reg_img_rd_stall = Input(UInt(32.W))
        val dp2reg_inf_data_num = Input(UInt(32.W))
        val dp2reg_inf_weight_num = Input(UInt(32.W))
        val dp2reg_nan_data_num = Input(UInt(32.W))
        val dp2reg_nan_weight_num = Input(UInt(32.W))
        val dp2reg_wg_rd_latency = Input(UInt(32.W))
        val dp2reg_wg_rd_stall = Input(UInt(32.W))
        val dp2reg_dat_flush_done = Input(Bool())
        val dp2reg_wt_flush_done = Input(Bool())
        val dp2reg_wt_rd_latency = Input(UInt(32.W))
        val dp2reg_wt_rd_stall = Input(UInt(32.W))
        val dp2reg_consumer = Output(Bool())
        val reg2dp_field = new cdma_dual_reg_flop_outputs
        val reg2dp_op_en = Output(Bool())
        val reg2dp_arb_weight = Output(UInt(4.W))
        val reg2dp_arb_wmb = Output(UInt(4.W))
        

        //slave cg op
        val slcg_op_en = Output(UInt(8.W))
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
//           │         │                  |     CDMA    |
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
    val dp2reg_consumer_out = RegInit(false.B)
    val dp2reg_flush_done = RegInit(false.B)
    val reg_offset = Wire(UInt(12.W))
    val reg_wr_data = Wire(UInt(32.W))
    val s_reg_wr_en = Wire(Bool())
    val dp2reg_status_0 = Wire(Bool())
    val dp2reg_status_1 = Wire(Bool())
    
    val u_single_reg = Module(new NV_NVDLA_CDMA_single_reg)

    u_single_reg.io.nvdla_core_clk := io.nvdla_core_clk 
    u_single_reg.io.reg.offset := reg_offset
    u_single_reg.io.reg.wr_data := reg_wr_data 
    u_single_reg.io.reg.wr_en := s_reg_wr_en
    u_single_reg.io.flush_done := dp2reg_flush_done
    u_single_reg.io.consumer := dp2reg_consumer_out
    u_single_reg.io.status_0 := dp2reg_status_0
    u_single_reg.io.status_1 := dp2reg_status_1 
    val s_reg_rd_data = u_single_reg.io.reg.rd_data
    val reg2dp_producer = u_single_reg.io.producer
    io.reg2dp_arb_weight := u_single_reg.io.arb_weight
    io.reg2dp_arb_wmb := u_single_reg.io.arb_wmb 

    //Instance two duplicated register groups
    val d0_reg_wr_en = Wire(Bool())
    val reg2dp_d0_op_en = RegInit(false.B)
    val dp2reg_d0_inf_data_num = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_inf_weight_num = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_nan_data_num = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_nan_weight_num = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_dat_rd_latency = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_dat_rd_stall = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_wt_rd_latency = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_wt_rd_stall = RegInit("b0".asUInt(32.W))

    val u_dual_reg_d0 = Module(new NV_NVDLA_CDMA_dual_reg)
    u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d0.io.reg.offset := reg_offset
    u_dual_reg_d0.io.reg.wr_data := reg_wr_data
    u_dual_reg_d0.io.reg.wr_en := d0_reg_wr_en
    val d0_reg_rd_data = u_dual_reg_d0.io.reg.rd_data
    u_dual_reg_d0.io.op_en := reg2dp_d0_op_en
    u_dual_reg_d0.io.inf_data_num := dp2reg_d0_inf_data_num 
    u_dual_reg_d0.io.inf_weight_num := dp2reg_d0_inf_weight_num
    u_dual_reg_d0.io.nan_data_num := dp2reg_d0_nan_data_num
    u_dual_reg_d0.io.nan_weight_num := dp2reg_d0_nan_weight_num
    u_dual_reg_d0.io.dat_rd_latency := dp2reg_d0_dat_rd_latency
    u_dual_reg_d0.io.dat_rd_stall := dp2reg_d0_dat_rd_stall
    u_dual_reg_d0.io.wt_rd_latency := dp2reg_d0_wt_rd_latency 
    u_dual_reg_d0.io.wt_rd_stall := dp2reg_d0_wt_rd_stall
    val reg2dp_d0_op_en_trigger = u_dual_reg_d0.io.op_en_trigger   
    val reg2dp_d0_field = u_dual_reg_d0.io.field
 
    val d1_reg_wr_en = Wire(Bool())
    val reg2dp_d1_op_en = RegInit(false.B)
    val dp2reg_d1_inf_data_num = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_inf_weight_num = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_nan_data_num = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_nan_weight_num = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_dat_rd_latency = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_dat_rd_stall = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_wt_rd_latency = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_wt_rd_stall = RegInit("b0".asUInt(32.W))

    val u_dual_reg_d1 = Module(new NV_NVDLA_CDMA_dual_reg)
    u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d1.io.reg.offset := reg_offset
    u_dual_reg_d1.io.reg.wr_data := reg_wr_data
    u_dual_reg_d1.io.reg.wr_en := d1_reg_wr_en
    val d1_reg_rd_data = u_dual_reg_d1.io.reg.rd_data
    u_dual_reg_d1.io.op_en := reg2dp_d1_op_en
    u_dual_reg_d1.io.inf_data_num := dp2reg_d1_inf_data_num 
    u_dual_reg_d1.io.inf_weight_num := dp2reg_d1_inf_weight_num
    u_dual_reg_d1.io.nan_data_num := dp2reg_d1_nan_data_num
    u_dual_reg_d1.io.nan_weight_num := dp2reg_d1_nan_weight_num
    u_dual_reg_d1.io.dat_rd_latency := dp2reg_d1_dat_rd_latency
    u_dual_reg_d1.io.dat_rd_stall := dp2reg_d1_dat_rd_stall
    u_dual_reg_d1.io.wt_rd_latency := dp2reg_d1_wt_rd_latency 
    u_dual_reg_d1.io.wt_rd_stall := dp2reg_d1_wt_rd_stall
    val reg2dp_d1_op_en_trigger = u_dual_reg_d1.io.op_en_trigger   
    val reg2dp_d1_field = u_dual_reg_d1.io.field

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CONSUMER PIONTER IN GENERAL SINGLE REGISTER GROUP         //
    //                                                                    //
    //////////////////////////////////////////////////////////////////////// 
    val dp2reg_consumer_out_w = ~dp2reg_consumer_out

    when(io.dp2reg_done){
        dp2reg_consumer_out := dp2reg_consumer_out_w
    }

    io.dp2reg_consumer := dp2reg_consumer_out

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE TWO STATUS FIELDS IN GENERAL SINGLE REGISTER GROUP        //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    dp2reg_status_0 := Mux(reg2dp_d0_op_en === false.B, "h0".asUInt(2.W), 
                       Mux(dp2reg_consumer_out === true.B, "h2".asUInt(2.W), 
                       "h1".asUInt(2.W)))

    dp2reg_status_1 := Mux(reg2dp_d1_op_en === false.B, "h0".asUInt(2.W), 
                       Mux(dp2reg_consumer_out === false.B, "h2".asUInt(2.W), 
                       "h1".asUInt(2.W)))

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OP_EN LOGIC                                               //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    val reg2dp_op_en_reg = RegInit("b0".asUInt(3.W))
    val reg2dp_d0_op_en_w = Mux(~reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg_wr_data(0), 
                            Mux(io.dp2reg_done && dp2reg_consumer_out === false.B, false.B, reg2dp_d0_op_en))

    reg2dp_d0_op_en := reg2dp_d0_op_en_w

    val reg2dp_d1_op_en_w =  Mux(~reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg_wr_data(0), 
                             Mux(io.dp2reg_done && dp2reg_consumer_out === true.B, false.B, reg2dp_d1_op_en))

    reg2dp_d1_op_en := reg2dp_d1_op_en_w

    val reg2dp_op_en_ori = Mux(dp2reg_consumer_out, reg2dp_d1_op_en, reg2dp_d0_op_en)
    val reg2dp_op_en_reg_w = Mux(io.dp2reg_done,  "b0".asUInt(3.W), Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori))

    reg2dp_op_en_reg := reg2dp_op_en_reg_w 
    io.reg2dp_op_en := reg2dp_op_en_reg(2)

    io.slcg_op_en := ShiftRegister(Fill(8, reg2dp_op_en_ori), 3)
    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE ACCESS LOGIC TO EACH REGISTER GROUP                       //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    //EACH subunit has 4KB address space 
    val reg_wr_en = Wire(Bool())
    val select_s = Mux(reg_offset(11,0) < "h0010".asUInt(32.W), true.B, false.B)
    val select_d0 = (reg_offset(11,0) >= "h0010".asUInt(32.W)) & (reg2dp_producer === false.B)
    val select_d1 = (reg_offset(11,0) >= "h0010".asUInt(32.W)) & (reg2dp_producer === true.B)

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
    val csb_logic = Module(new NV_NVDLA_CSB_LOGIC)
    csb_logic.io.clk := io.nvdla_core_clk
    csb_logic.io.csb2dp <> io.csb2cdma
    reg_offset := csb_logic.io.reg.offset
    reg_wr_en := csb_logic.io.reg.wr_en
    reg_wr_data := csb_logic.io.reg.wr_data
    csb_logic.io.reg.rd_data := reg_rd_data

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    io.reg2dp_field := Mux(dp2reg_consumer_out, reg2dp_d1_field, reg2dp_d0_field)

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // PASTE ADDIFITON LOGIC HERE FROM EXTRA FILE                         //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    //  for interrupt                                                     //
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    //  for cbuf flushing logic                                           //
    ////////////////////////////////////////////////////////////////////////
    dp2reg_flush_done := io.dp2reg_wt_flush_done & io.dp2reg_dat_flush_done

    ////////////////////////////////////////////////////////////////////////
    //  for general counting register                                     //
    ////////////////////////////////////////////////////////////////////////
    val dp2reg_d0_set = reg2dp_d0_op_en & ~reg2dp_d0_op_en_w
    val dp2reg_d0_clr = ~reg2dp_d0_op_en & reg2dp_d0_op_en_w
    val dp2reg_d0_reg = reg2dp_d0_op_en ^ reg2dp_d0_op_en_w

    val dp2reg_d1_set = reg2dp_d1_op_en & ~reg2dp_d1_op_en_w
    val dp2reg_d1_clr = ~reg2dp_d1_op_en & reg2dp_d1_op_en_w
    val dp2reg_d1_reg = reg2dp_d1_op_en ^ reg2dp_d1_op_en_w

    ////////////////////////////////////////////////////////////////////////
    //  for NaN and infinity counting registers                                   //
    ////////////////////////////////////////////////////////////////////////
    //////// group 0 ////////
    val dp2reg_d0_nan_weight_num_w = Mux(dp2reg_d0_set, io.dp2reg_nan_weight_num,
                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
                                     dp2reg_d0_nan_weight_num))
    val dp2reg_d0_inf_weight_num_w = Mux(dp2reg_d0_set, io.dp2reg_inf_weight_num,
                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
                                     dp2reg_d0_inf_weight_num))
    val dp2reg_d0_nan_data_num_w = Mux(dp2reg_d0_set, io.dp2reg_nan_data_num,
                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
                                     dp2reg_d0_nan_data_num))  
    val dp2reg_d0_inf_data_num_w = Mux(dp2reg_d0_set, io.dp2reg_inf_data_num,
                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
                                     dp2reg_d0_inf_data_num))

    when(dp2reg_d0_reg){
        dp2reg_d0_nan_weight_num := dp2reg_d0_nan_weight_num_w
        dp2reg_d0_inf_weight_num := dp2reg_d0_inf_weight_num_w
        dp2reg_d0_nan_data_num := dp2reg_d0_nan_data_num_w
        dp2reg_d0_inf_data_num := dp2reg_d0_inf_data_num_w
    }

    //////// group 1 ////////
    val dp2reg_d1_nan_weight_num_w = Mux(dp2reg_d1_set, io.dp2reg_nan_weight_num,
                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
                                     dp2reg_d1_nan_weight_num))
    val dp2reg_d1_inf_weight_num_w = Mux(dp2reg_d1_set, io.dp2reg_inf_weight_num,
                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
                                     dp2reg_d1_inf_weight_num))
    val dp2reg_d1_nan_data_num_w = Mux(dp2reg_d1_set, io.dp2reg_nan_data_num,
                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
                                     dp2reg_d1_nan_data_num))  
    val dp2reg_d1_inf_data_num_w = Mux(dp2reg_d1_set, io.dp2reg_inf_data_num,
                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
                                     dp2reg_d1_inf_data_num))

    when(dp2reg_d1_reg){
        dp2reg_d1_nan_weight_num := dp2reg_d1_nan_weight_num_w
        dp2reg_d1_inf_weight_num := dp2reg_d1_inf_weight_num_w
        dp2reg_d1_nan_data_num := dp2reg_d1_nan_data_num_w
        dp2reg_d1_inf_data_num := dp2reg_d1_inf_data_num_w
    }

    ////////////////////////////////////////////////////////////////////////
    //  for perf conting registers                                        //
    ////////////////////////////////////////////////////////////////////////
    //////// group 0 ////////
    val dp2reg_d0_wt_rd_stall_w = Mux(dp2reg_d0_set, io.dp2reg_wt_rd_stall,
                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
                                     dp2reg_d0_wt_rd_stall))
    val dp2reg_d0_wt_rd_latency_w = Mux(dp2reg_d0_set, io.dp2reg_wt_rd_latency,
                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
                                     dp2reg_d0_wt_rd_latency))
    val dp2reg_d0_dat_rd_stall_w = Mux(dp2reg_d0_set, (io.dp2reg_dc_rd_stall | io.dp2reg_wg_rd_stall | io.dp2reg_img_rd_stall),
                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
                                     dp2reg_d0_dat_rd_stall))  
    val dp2reg_d0_dat_rd_latency_w = Mux(dp2reg_d0_set, (io.dp2reg_dc_rd_latency | io.dp2reg_wg_rd_latency | io.dp2reg_img_rd_latency),
                                     Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
                                     dp2reg_d0_dat_rd_latency))

    when(dp2reg_d0_reg){                                 
        dp2reg_d0_wt_rd_stall := dp2reg_d0_wt_rd_stall_w 
        dp2reg_d0_wt_rd_latency := dp2reg_d0_wt_rd_latency_w
        dp2reg_d0_dat_rd_stall := dp2reg_d0_dat_rd_stall_w
        dp2reg_d0_dat_rd_latency := dp2reg_d0_dat_rd_latency_w
    }
    //////// group 1 ////////
    val dp2reg_d1_wt_rd_stall_w = Mux(dp2reg_d1_set, io.dp2reg_wt_rd_stall,
                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
                                     dp2reg_d1_wt_rd_stall))
    val dp2reg_d1_wt_rd_latency_w = Mux(dp2reg_d1_set, io.dp2reg_wt_rd_latency,
                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
                                     dp2reg_d1_wt_rd_latency))
    val dp2reg_d1_dat_rd_stall_w = Mux(dp2reg_d1_set, (io.dp2reg_dc_rd_stall | io.dp2reg_wg_rd_stall | io.dp2reg_img_rd_stall),
                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
                                     dp2reg_d1_dat_rd_stall))  
    val dp2reg_d1_dat_rd_latency_w = Mux(dp2reg_d1_set, (io.dp2reg_dc_rd_latency | io.dp2reg_wg_rd_latency | io.dp2reg_img_rd_latency),
                                     Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
                                     dp2reg_d1_dat_rd_latency))

    when(dp2reg_d1_reg){                                 
        dp2reg_d1_wt_rd_stall := dp2reg_d1_wt_rd_stall_w 
        dp2reg_d1_wt_rd_latency := dp2reg_d1_wt_rd_latency_w
        dp2reg_d1_dat_rd_stall := dp2reg_d1_dat_rd_stall_w
        dp2reg_d1_dat_rd_latency := dp2reg_d1_dat_rd_latency_w
    }

}}

object NV_NVDLA_CDMA_regfileDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_regfile())
}
