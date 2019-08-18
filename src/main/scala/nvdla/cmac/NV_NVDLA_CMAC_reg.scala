package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_CMAC_reg(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        //csb2cmac
        val csb2cmac_a = new csb2dp_if

        //reg2dp
        val reg2dp_op_en = Output(Bool())
        val reg2dp_field = new cmac_reg_flop_outputs
        val dp2reg_done = Input(Bool())

        //slave cg op
        val slcg_op_en = Output(UInt(conf.CMAC_SLCG_NUM.W))
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
    val dp2reg_status_0 = Wire(Bool())
    val dp2reg_status_1 = Wire(Bool())

    val reg.offset = Wire(UInt(12.W))
    val reg_wr_data = Wire(UInt(32.W))
    val s_reg_wr_en = Wire(Bool())

    val u_single_reg = Module(new NV_NVDLA_CMAC_REG_single)

    u_single_reg.io.nvdla_core_clk := io.nvdla_core_clk
    u_single_reg.io.reg.offset := reg.offset
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

    val u_dual_reg_d0 = Module(new NV_NVDLA_CMAC_REG_dual)
    u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d0.io.reg.offset := reg.offset
    u_dual_reg_d0.io.reg.wr_data := reg_wr_data
    u_dual_reg_d0.io.reg.wr_en := d0_reg_wr_en
    val d0_reg_rd_data = u_dual_reg_d0.io.reg.rd_data
    u_dual_reg_d0.io.op_en := reg2dp_d0_op_en
    val reg2dp_d0_field = u_dual_reg_d0.io.field
    val reg2dp_d0_op_en_trigger = u_dual_reg_d0.io.op_en_trigger

    val d1_reg_wr_en = Wire(Bool())
    val reg2dp_d1_op_en = RegInit(false.B)

    val u_dual_reg_d1 = Module(new NV_NVDLA_CMAC_REG_dual)
    u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d1.io.reg.offset := reg.offset
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
    val select_s = Mux(reg.offset(11,0) < "h0008".asUInt(32.W), true.B, false.B)
    val select_d0 = (reg.offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === false.B)
    val select_d1 = (reg.offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === true.B)

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

    req_pvld := io.csb2cmac_a.req.valid
    when(io.csb2cmac_a.req.valid){
        req_pd := io.csb2cmac_a.req.bits
    }

    // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  req_pd ) 
    val req_addr = req_pd(21, 0)
    val req_wdat = req_pd(53, 22)
    val req_write = req_pd(54)
    val req_nposted = req_pd(55)
    val req_srcpriv = req_pd(56)
    val req_wrbe = req_pd(60, 57)
    val req_level = req_pd(62, 61)

    io.csb2cmac_a.req.ready := true.B

    //Address in CSB master is word aligned while address in regfile is byte aligned.
    reg.offset := Cat(req_addr, "b0".asUInt(2.W))
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

    val csb2cmac_a_resp_pd_out = RegInit("b0".asUInt(34.W))
    val csb2cmac_a_resp_valid_out = RegInit(false.B)

    when(reg_rd_en){
        csb2cmac_a_resp_pd_out := csb_rresp_pd_w
    }
    .elsewhen(reg_wr_en & req_nposted){
        csb2cmac_a_resp_pd_out := csb_wresp_pd_w
    }
    csb2cmac_a_resp_valid_out := (reg_wr_en & req_nposted) | reg_rd_en

    io.csb2cmac_a.resp.bits := csb2cmac_a_resp_pd_out
    io.csb2cmac_a.resp.valid := csb2cmac_a_resp_valid_out

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////

    io.reg2dp_field := Mux(dp2reg_consumer, reg2dp_d1_field, reg2dp_d0_field)

}}