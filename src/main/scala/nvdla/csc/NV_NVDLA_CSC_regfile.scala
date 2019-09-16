package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_CSC_regfile(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        //csb2csc
        val csb2csc = new csb2dp_if

        //reg2dp
        val reg2dp_op_en = Output(Bool())
        val reg2dp_field = new csc_dual_reg_flop_outputs
        val dp2reg_done = Input(Bool()) 
        
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
//       │       ─┴─       │                    reg   
//       │                 │                    ||
//       └───┐         ┌───┘              |-------------|
//           │         │                  |     CSC     |
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
    val dp2reg_status_0 = Wire(UInt(2.W))
    val dp2reg_status_1 = Wire(UInt(2.W))

    val reg_offset = Wire(UInt(12.W))
    val reg_wr_data = Wire(UInt(32.W))
    val s_reg_wr_en = Wire(Bool())

    val u_single_reg = Module(new NV_NVDLA_BASIC_REG_single)

    u_single_reg.io.nvdla_core_clk := io.nvdla_core_clk
    u_single_reg.io.reg.offset := reg_offset
    u_single_reg.io.reg.wr_data := reg_wr_data 
    u_single_reg.io.reg.wr_en := s_reg_wr_en
    val s_reg_rd_data = u_single_reg.io.reg.rd_data
    u_single_reg.io.consumer := dp2reg_consumer
    u_single_reg.io.status_0 := dp2reg_status_0
    u_single_reg.io.status_1 := dp2reg_status_1 
    val reg2dp_producer = u_single_reg.io.producer

    //Instance two duplicated register groups
    val d0_reg_wr_en = Wire(Bool())
    val reg2dp_d0_op_en = RegInit(false.B)

    val u_dual_reg_d0 = Module(new NV_NVDLA_CSC_dual_reg)
    u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d0.io.reg.offset := reg_offset
    u_dual_reg_d0.io.reg.wr_data := reg_wr_data
    u_dual_reg_d0.io.reg.wr_en := d0_reg_wr_en
    val d0_reg_rd_data = u_dual_reg_d0.io.reg.rd_data
    u_dual_reg_d0.io.op_en := reg2dp_d0_op_en
    val reg2dp_d0_field = u_dual_reg_d0.io.field
    val reg2dp_d0_op_en_trigger = u_dual_reg_d0.io.op_en_trigger

    val d1_reg_wr_en = Wire(Bool())
    val reg2dp_d1_op_en = RegInit(false.B)

    val u_dual_reg_d1 = Module(new NV_NVDLA_CSC_dual_reg)
    u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d1.io.reg.offset := reg_offset
    u_dual_reg_d1.io.reg.wr_data := reg_wr_data
    u_dual_reg_d1.io.reg.wr_en := d1_reg_wr_en
    val d1_reg_rd_data = u_dual_reg_d1.io.reg.rd_data
    u_dual_reg_d1.io.op_en := reg2dp_d1_op_en 
    val reg2dp_d1_field = u_dual_reg_d1.io.field
    val reg2dp_d1_op_en_trigger = u_dual_reg_d1.io.op_en_trigger

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CONSUMER PIONTER IN GENERAL SINGLE REGISTER GROUP         //
    //                                                                    //
    //////////////////////////////////////////////////////////////////////// 
    when(io.dp2reg_done){
        dp2reg_consumer := ~dp2reg_consumer
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

    reg2dp_d0_op_en := Mux(~reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg_wr_data(0), 
                       Mux(io.dp2reg_done && dp2reg_consumer === false.B, false.B, reg2dp_d0_op_en))

    reg2dp_d1_op_en := Mux(~reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg_wr_data(0), 
                       Mux(io.dp2reg_done && dp2reg_consumer === true.B, false.B, reg2dp_d1_op_en))

    val reg2dp_op_en_ori = Mux(dp2reg_consumer, reg2dp_d1_op_en, reg2dp_d0_op_en)

    reg2dp_op_en_reg := Mux(io.dp2reg_done, "b0".asUInt(3.W), Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori))
    io.reg2dp_op_en := reg2dp_op_en_reg(2)

    io.slcg_op_en := ShiftRegister(Fill(11, reg2dp_op_en_ori), 3)
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
    val csb_logic = Module(new NV_NVDLA_CSB_LOGIC)
    csb_logic.io.clk := io.nvdla_core_clk
    csb_logic.io.csb2dp <> io.csb2csc
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
}}

object NV_NVDLA_CSC_regfileDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CSC_regfile())
}
